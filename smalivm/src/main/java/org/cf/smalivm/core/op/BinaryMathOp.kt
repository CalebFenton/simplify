package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * The twelve binary arithmetic/logical operators across four operand domains.
 *
 * Both the operator and the domain come from the mnemonic, parsed by hyphen-segment rather than by
 * prefix: `rsub-int` must not be read as `sub` (the oracle gets away with `startsWith` only because
 * "rsub-int" happens not to start with "sub", which is luck rather than design).
 *
 * The right-hand operand is a [Operand.Register] or, for the `/lit8` and `/lit16` forms, an
 * [Operand.Literal] typed `I` -- Dalvik's literal forms are int-only.
 *
 * Divide-by-zero routing lives in [armsFor]; see its KDoc.
 */
internal class BinaryMathOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val lhsRegister: Int,
    val rhs: Operand,
    private val operator: Operator,
    private val domain: VirtualType,
) : Op(address) {

    /** The right-hand operand: another register, or a literal baked into the instruction. */
    sealed interface Operand {
        data class Register(val register: Int) : Operand
        data class Literal(val value: Int) : Operand
    }

    enum class Operator { ADD, SUB, MUL, DIV, REM, AND, OR, XOR, SHL, SHR, USHR, RSUB }

    override val registersRead: Set<Int> = when (rhs) {
        is Operand.Register -> setOf(lhsRegister, rhs.register)
        is Operand.Literal -> setOf(lhsRegister)
    }

    // Both halves for a wide (long) result: the high half is not free scratch (see
    // Op.destinationRegisters and ExecutionGraph.availableRegisters).
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, domain.isWide)

    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.read(lhsRegister)
        val right = when (rhs) {
            is Operand.Register -> frame.read(rhs.register)
            is Operand.Literal -> Value.Known(rhs.value, INT)
        }
        val arms = armsFor(right)
        // Dalvik does not write the destination register when the division throws, so a path that
        // DEFINITELY throws must not either: the throwing node is piled and counted as executed, so
        // `consensus(address, destRegister)` would otherwise report `Unknown(domain)` where the pre-op
        // value still stands -- and a handler successor copies this same post-op frame, so it would see
        // it too. Mostly that is a harmless move up the lattice, but not when the destination held a
        // reference: `Unknown("I")` over a `Ljava/lang/String;` is type-wrong, not merely imprecise.
        //
        // A `[Continue, Throw]` fork stays exact: only the continue arm carries the destination update,
        // while the throw arm retains the pre-op value.
        val result = computed(lhs, right) ?: Value.Unknown(domain)
        return OpExecution.of(arms.map { arm ->
            if (arm is OpArm.Continue) arm.copy(update = OpUpdate.assign(destRegister, result)) else arm
        })
    }

    /**
     * The computed result, or null when an operand is unknown or not readable at the width this op reads it
     * (see [Coerce]); the caller turns null into `Unknown(domain)` rather than a guess.
     *
     * A division by a known zero returns null too, because there is no result: the path throws
     * instead. [armsFor] is what distinguishes "no result because unknown" from "no result
     * because it throws", and both route the zero question through the ONE [knownDivisor] below so
     * they cannot disagree about it.
     */
    private fun computed(lhs: Value, rhs: Value): Value? {
        if (lhs !is Value.Known || rhs !is Value.Known) return null
        // The same zero question [armsFor] asks, through the same function.
        if (canDivideByZero() && knownDivisor(rhs).let { it == null || it == 0L }) return null
        val raw: Any = when (domain.descriptor) {
            "I" -> {
                val a = Coerce.toInt(lhs.raw) ?: return null
                val b = Coerce.toInt(rhs.raw) ?: return null
                intResult(a, b)
            }
            "J" -> {
                val a = Coerce.toLong(lhs.raw) ?: return null
                // A long shift's distance is one 32-bit register, not a pair.
                val b = when (operator) {
                    Operator.SHL, Operator.SHR, Operator.USHR -> Coerce.toInt(rhs.raw)?.toLong()
                    else -> Coerce.toLong(rhs.raw)
                } ?: return null
                longResult(a, b)
            }
            else -> return floatingResult(lhs, rhs)
        }
        return Value.Known(raw, domain)
    }

    private fun intResult(a: Int, b: Int): Any = when (operator) {
        Operator.ADD -> a + b
        Operator.SUB -> a - b
        Operator.MUL -> a * b
        Operator.DIV -> a / b
        Operator.REM -> a % b
        Operator.AND -> a and b
        Operator.OR -> a or b
        Operator.XOR -> a xor b
        // Dalvik uses the low 5 bits of the shift distance; Kotlin's Int shifts already mask to 5,
        // so this `and` is belt-and-braces rather than load-bearing.
        Operator.SHL -> a shl (b and 0x1f)
        Operator.SHR -> a shr (b and 0x1f)
        Operator.USHR -> a ushr (b and 0x1f)
        Operator.RSUB -> b - a
    }

    private fun longResult(a: Long, b: Long): Any = when (operator) {
        Operator.ADD -> a + b
        Operator.SUB -> a - b
        Operator.MUL -> a * b
        Operator.DIV -> a / b
        Operator.REM -> a % b
        Operator.AND -> a and b
        Operator.OR -> a or b
        Operator.XOR -> a xor b
        // Long shifts take an int distance and Kotlin masks it to 6 bits, matching Dalvik -- no
        // explicit mask needed (and none should be added: a 5-bit mask here would silently turn a
        // shift-by-33 into a shift-by-1).
        Operator.SHL -> a shl b.toInt()
        Operator.SHR -> a shr b.toInt()
        Operator.USHR -> a ushr b.toInt()
        // rsub has no long form in Dalvik, so OpBuilder can never build this pair.
        Operator.RSUB -> error("rsub-long does not exist; decode built $name")
    }

    /**
     * Float and double: five operators only. Dalvik has no `and`/`or`/`xor`/shift forms for them, so
     * the `else` branch is unreachable through [OpBuilder] and loud rather than silent.
     *
     * Division by zero does NOT throw here -- it yields +/-Infinity, and `0.0/0.0` yields NaN. That is
     * why [canDivideByZero] excludes these two domains, and therefore why [computed]'s zero guard never
     * fires on the way to this function.
     */
    private fun floatingResult(lhs: Value.Known, rhs: Value.Known): Value? = when (domain.descriptor) {
        "F" -> {
            val a = Coerce.toFloat(lhs.raw) ?: return null
            val b = Coerce.toFloat(rhs.raw) ?: return null
            Value.Known(
                when (operator) {
                    Operator.ADD -> a + b
                    Operator.SUB -> a - b
                    Operator.MUL -> a * b
                    Operator.DIV -> a / b
                    Operator.REM -> a % b
                    else -> error("$operator has no float form; decode built $name")
                },
                domain,
            )
        }
        "D" -> {
            val a = Coerce.toDouble(lhs.raw) ?: return null
            val b = Coerce.toDouble(rhs.raw) ?: return null
            Value.Known(
                when (operator) {
                    Operator.ADD -> a + b
                    Operator.SUB -> a - b
                    Operator.MUL -> a * b
                    Operator.DIV -> a / b
                    Operator.REM -> a % b
                    else -> error("$operator has no double form; decode built $name")
                },
                domain,
            )
        }
        else -> error("Unexpected binary math domain ${domain.descriptor} in $name")
    }

    /**
     * Divide-by-zero routing, and the engine's first fork across two transitions.
     *
     * Three cases, and the middle one is the reason this VM exists:
     *  - a **known** zero divisor: the path definitely throws, so `[Throw]` alone -- continuing would
     *    explore a path Dalvik never takes;
     *  - a divisor whose zero-ness is **not provable** with an integral `div`/`rem` -- unknown, or not
     *    readable at the op's own width (see [knownDivisor]): both outcomes are feasible, so
     *    `[Continue, Throw]`. Forking is not a precision
     *    nicety here: dropping the exceptional path would leave a handler unexplored on a run still
     *    reporting `Complete`, and dropping the normal path would invent a throw (spec §2.2, more forks
     *    never fewer);
     *  - anything else: `[Continue]`.
     *
     * Only the DIVISOR is consulted, never the result. A known non-zero divisor cannot throw whatever
     * the dividend is -- integer overflow does not throw in Dalvik, `Integer.MIN_VALUE / -1` simply
     * yields `Integer.MIN_VALUE` -- so an unknown dividend over a known non-zero divisor gets one
     * transition. The oracle instead forks on `result.isUnknown` (`opcode/BinaryMathOp.kt:64`), which
     * explores an infeasible throw for exactly that input; see `BinaryMathOpTest`'s
     * `anUnknownDividendOverAKnownNonZeroDivisorDoesNotFork`.
     *
     * `Continue` is listed first to keep arm construction deterministic and match the reading order of the
     * code. Public outcome order is deliberately not a semantic contract.
     *
     * INTENDED DIVERGENCE from the oracle, in both directions, because its condition
     * (`opcode/BinaryMathOp.kt:64`) asks about the wrong two things:
     *  - it forks only for `DIV`, never `REM`, although `rem-int`/`rem-long` by zero throws identically
     *    -- and its own `doIntegerOperation` catches `ArithmeticException` for both (`:185`). So on an
     *    unknown divisor it explores one fewer feasible path than exists while reporting the run
     *    complete, which is the soundness class this rewrite exists to remove;
     *  - it gates on `result.isUnknown`, true when EITHER operand is unknown, so it forks an infeasible
     *    throw for the unknown-dividend case this KDoc's third paragraph describes.
     *
     * Both are recorded in `Fixtures.intendedDivergences` as `ORACLE_MISJUDGES_FEASIBILITY`
     * (`binary-rem-int-unknown-divisor` and `binary-div-int-unknown-dividend`), which is what makes them
     * checkable claims rather than prose: each entry names the exact addresses the oracle misses or
     * invents, and the registry test fails if either stops being true.
     */
    private fun armsFor(rhs: Value): List<OpArm> {
        if (!canDivideByZero()) return listOf(OpArm.Continue())
        return when (knownDivisor(rhs)) {
            null -> listOf(OpArm.Continue(), divideByZeroArm())
            0L -> listOf(divideByZeroArm())
            else -> listOf(OpArm.Continue())
        }
    }

    /**
     * Whether a zero divisor could make this operation throw at all: only integral `div`/`rem`, since
     * `F`/`D` division by zero yields +/-Infinity or NaN. The domain half matters: `0f`'s bits are an
     * integral zero, so an integral zero check reached from the float domain would throw where float
     * division does not.
     */
    private fun canDivideByZero(): Boolean =
        (operator == Operator.DIV || operator == Operator.REM) &&
            (domain.descriptor == "I" || domain.descriptor == "J")

    /**
     * The divisor's value **at this op's own arithmetic width**, or null when it is not [Value.Known] or not
     * readable at that width (see [Coerce]), so both outcomes are feasible.
     *
     * The width matters because the two widths disagree about zero: `div-int` reads one 32-bit register, so a
     * `Long` raw 2^32 is a zero divisor there (Dalvik throws) although the pair's 64 bits are not zero.
     * Asking at 64 bits would continue normally and drop the throw on a run still reporting `Complete`.
     */
    private fun knownDivisor(rhs: Value): Long? {
        if (rhs !is Value.Known) return null
        return when (domain.descriptor) {
            "I" -> Coerce.toInt(rhs.raw)?.toLong()
            else -> Coerce.toLong(rhs.raw)
        }
    }

    /**
     * The thrown value, as a tracked heap object (spec §4.1: a VM-tracked object's raw is a [HeapRef],
     * never the live host object). The host `ArithmeticException` rides inside the object, matching the
     * oracle's shape (`opcode/BinaryMathOp.kt:59-61` wraps the caught host exception) so the corpus's
     * message assertions port with one indirection.
     *
     * Two synthesized exceptions get distinct ids and so do not `agreesWith` each other: two throwing
     * paths join to `Unknown(ArithmeticException)` rather than to one shared exception. That is CORRECT,
     * not a residual imprecision -- two paths that each throw a fresh exception do not throw the same
     * object in Dalvik. Pinned by `BinaryMathOpTest.twoSeparatelySynthesizedExceptionsStillDoNotAgree`.
     */
    private fun divideByZeroArm(): OpArm.Throw = OpArm.Throw(
        evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(ARITHMETIC_EXCEPTION, "/ by zero")),
    )

    override fun toString(): String {
        val right = when (rhs) {
            is Operand.Register -> "r${rhs.register}"
            is Operand.Literal -> "0x${Integer.toHexString(rhs.value)}"
        }
        return "$name r$destRegister, r$lhsRegister, $right"
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")

        /**
         * Named here rather than built per throw so the descriptor is written once: handler resolution
         * compares this type against every `catch` clause, so a typo would silently stop matching
         * `catch (ArithmeticException)` and route every division fork to the unhandled escape instead.
         */
        val ARITHMETIC_EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/ArithmeticException;")
    }
}
