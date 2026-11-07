package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/** The `int` operators, including the `rsub` that only the int domain has. */
internal enum class IntOperator(val divides: Boolean = false, val evaluate: (Int, Int) -> Int) {
    ADD(evaluate = Int::plus),
    SUB(evaluate = Int::minus),
    RSUB(evaluate = { a, b -> b - a }),
    MUL(evaluate = Int::times),
    DIV(divides = true, evaluate = Int::div),
    REM(divides = true, evaluate = Int::rem),
    AND(evaluate = Int::and),
    OR(evaluate = Int::or),
    XOR(evaluate = Int::xor),
    // Kotlin masks an Int shift distance to its low 5 bits, as Dalvik does.
    SHL(evaluate = Int::shl),
    SHR(evaluate = Int::shr),
    USHR(evaluate = Int::ushr),
}

/** The `long` operators. A shift's distance is one 32-bit register, not a pair. */
internal enum class LongOperator(
    val divides: Boolean = false,
    val shifts: Boolean = false,
    val evaluate: (Long, Long) -> Long,
) {
    ADD(evaluate = Long::plus),
    SUB(evaluate = Long::minus),
    MUL(evaluate = Long::times),
    DIV(divides = true, evaluate = Long::div),
    REM(divides = true, evaluate = Long::rem),
    AND(evaluate = Long::and),
    OR(evaluate = Long::or),
    XOR(evaluate = Long::xor),
    // Kotlin masks a Long shift distance to its low 6 bits, as Dalvik does.
    SHL(shifts = true, evaluate = { a, b -> a shl b.toInt() }),
    SHR(shifts = true, evaluate = { a, b -> a shr b.toInt() }),
    USHR(shifts = true, evaluate = { a, b -> a ushr b.toInt() }),
}

/** The `float` and `double` operators. Division by zero yields an infinity or NaN rather than throwing. */
internal enum class FloatingOperator {
    ADD, SUB, MUL, DIV, REM;

    fun evaluate(a: Float, b: Float): Float = when (this) {
        ADD -> a + b
        SUB -> a - b
        MUL -> a * b
        DIV -> a / b
        REM -> a % b
    }

    fun evaluate(a: Double, b: Double): Double = when (this) {
        ADD -> a + b
        SUB -> a - b
        MUL -> a * b
        DIV -> a / b
        REM -> a % b
    }
}

/** One operator in one operand domain; the pairs Dalvik lacks (`rsub-long`, `and-float`) cannot be built. */
internal sealed interface BinaryOperation {
    val resultType: VirtualType

    /** The result's raw, or null when an operand has no reading at this domain's width or the divisor is zero. */
    fun compute(lhs: Any?, rhs: Any?): Any?

    /** True when [rhs] is provably a zero divisor, false when no `ArithmeticException` is possible, null when unprovable. */
    fun divisorIsZero(rhs: Value): Boolean?

    data class IntDomain(val operator: IntOperator) : BinaryOperation {
        override val resultType: VirtualType get() = INT

        override fun compute(lhs: Any?, rhs: Any?): Any? {
            val a = Coerce.toInt(lhs) ?: return null
            val b = Coerce.toInt(rhs) ?: return null
            return if (operator.divides && b == 0) null else operator.evaluate(a, b)
        }

        // `div-int` reads one 32-bit register, so a `Long` raw 2^32 is a zero divisor here.
        override fun divisorIsZero(rhs: Value): Boolean? =
            if (!operator.divides) false else (rhs as? Value.Known)?.let { Coerce.toInt(it.raw) }?.let { it == 0 }
    }

    data class LongDomain(val operator: LongOperator) : BinaryOperation {
        override val resultType: VirtualType get() = LONG

        override fun compute(lhs: Any?, rhs: Any?): Any? {
            val a = Coerce.toLong(lhs) ?: return null
            val b = (if (operator.shifts) Coerce.toInt(rhs)?.toLong() else Coerce.toLong(rhs)) ?: return null
            return if (operator.divides && b == 0L) null else operator.evaluate(a, b)
        }

        override fun divisorIsZero(rhs: Value): Boolean? =
            if (!operator.divides) false else (rhs as? Value.Known)?.let { Coerce.toLong(it.raw) }?.let { it == 0L }
    }

    data class FloatDomain(val operator: FloatingOperator) : BinaryOperation {
        override val resultType: VirtualType get() = FLOAT

        override fun compute(lhs: Any?, rhs: Any?): Any? {
            val a = Coerce.toFloat(lhs) ?: return null
            val b = Coerce.toFloat(rhs) ?: return null
            return operator.evaluate(a, b)
        }

        override fun divisorIsZero(rhs: Value): Boolean = false
    }

    data class DoubleDomain(val operator: FloatingOperator) : BinaryOperation {
        override val resultType: VirtualType get() = DOUBLE

        override fun compute(lhs: Any?, rhs: Any?): Any? {
            val a = Coerce.toDouble(lhs) ?: return null
            val b = Coerce.toDouble(rhs) ?: return null
            return operator.evaluate(a, b)
        }

        override fun divisorIsZero(rhs: Value): Boolean = false
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
        val LONG: VirtualType = VirtualType.of("J")
        val FLOAT: VirtualType = VirtualType.of("F")
        val DOUBLE: VirtualType = VirtualType.of("D")
    }
}

/**
 * The binary arithmetic and logical instructions. The right-hand operand is a register or, for the `/lit8`
 * and `/lit16` forms, an `int` literal.
 */
internal class BinaryMathOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val lhsRegister: Int,
    val rhs: Operand,
    private val operation: BinaryOperation,
) : Op(address) {

    /** The right-hand operand: another register, or a literal baked into the instruction. */
    sealed interface Operand {
        data class Register(val register: Int) : Operand
        data class Literal(val value: Int) : Operand
    }

    override val registersRead: Set<Int> = when (rhs) {
        is Operand.Register -> setOf(lhsRegister, rhs.register)
        is Operand.Literal -> setOf(lhsRegister)
    }

    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, operation.resultType.isWide)

    /**
     * Only the divisor decides whether `div`/`rem` throws: integer overflow does not throw in Dalvik
     * (`Integer.MIN_VALUE / -1` is `Integer.MIN_VALUE`). A throwing path does not write the destination, so
     * only the `Continue` arm carries the assignment.
     */
    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.read(lhsRegister)
        val right = when (rhs) {
            is Operand.Register -> frame.read(rhs.register)
            is Operand.Literal -> Value.Known(rhs.value, INT)
        }
        val divideByZero = GuestThrowables.throwing(GuestThrowables.divideByZero())
        val proceed = { OpArm.Continue(OpUpdate.assign(destRegister, computed(lhs, right))) }
        return when (operation.divisorIsZero(right)) {
            false -> OpExecution.single(proceed())
            true -> OpExecution.single(divideByZero)
            null -> OpExecution.of(proceed(), divideByZero)
        }
    }

    private fun computed(lhs: Value, rhs: Value): Value {
        if (lhs !is Value.Known || rhs !is Value.Known) return Value.Unknown(operation.resultType)
        val raw = operation.compute(lhs.raw, rhs.raw) ?: return Value.Unknown(operation.resultType)
        return Value.Known(raw, operation.resultType)
    }

    override fun toString(): String {
        val right = when (rhs) {
            is Operand.Register -> "r${rhs.register}"
            is Operand.Literal -> "0x${Integer.toHexString(rhs.value)}"
        }
        return "$name r$destRegister, r$lhsRegister, $right"
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
