package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * One three-way compare. The `l`/`g` forms differ only on NaN, which is unordered: `cmpg-*` answers 1 and
 * `cmpl-*` answers -1 when either operand is NaN. [compare] answers null when an operand has no reading at
 * the instruction's width.
 */
internal enum class CmpOperation(private val compare: (Any?, Any?) -> Int?) {
    CMP_LONG({ a, b -> Coerce.toLong(a)?.let { x -> Coerce.toLong(b)?.let { y -> x.compareTo(y) } } }),
    CMPL_FLOAT({ a, b -> floats(a, b, nanResult = -1) }),
    CMPG_FLOAT({ a, b -> floats(a, b, nanResult = 1) }),
    CMPL_DOUBLE({ a, b -> doubles(a, b, nanResult = -1) }),
    CMPG_DOUBLE({ a, b -> doubles(a, b, nanResult = 1) }),
    ;

    fun applyTo(lhs: Value, rhs: Value): Int? =
        if (lhs is Value.Known && rhs is Value.Known) compare(lhs.raw, rhs.raw) else null

    private companion object {
        // Widening float to double is exact, so it preserves order, signed zero and NaN.
        fun floats(a: Any?, b: Any?, nanResult: Int): Int? {
            val x = Coerce.toFloat(a) ?: return null
            val y = Coerce.toFloat(b) ?: return null
            return ieeeCompare(x.toDouble(), y.toDouble(), nanResult)
        }

        fun doubles(a: Any?, b: Any?, nanResult: Int): Int? {
            val x = Coerce.toDouble(a) ?: return null
            val y = Coerce.toDouble(b) ?: return null
            return ieeeCompare(x, y, nanResult)
        }

        /**
         * ART's `HandleCmpl`/`HandleCmpg`: IEEE-754 `>`, `==`, `<`, so `-0.0 == 0.0` answers 0. Only NaN fails
         * all three. `Double.compare` is not this: it orders `-0.0` below `0.0`.
         */
        fun ieeeCompare(a: Double, b: Double, nanResult: Int): Int = when {
            a > b -> 1
            a == b -> 0
            a < b -> -1
            else -> nanResult
        }
    }
}

/** `cmp-long`, `cmpl-float`, `cmpg-float`, `cmpl-double`, `cmpg-double`: three-way compare into an `I` result. */
internal class CmpOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val lhsRegister: Int,
    val rhsRegister: Int,
    private val operation: CmpOperation,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(lhsRegister, rhsRegister)
    override val registersAssigned: Set<Int> = setOf(destRegister)

    /** An unprovable comparison writes `Unknown(I)`, so a following `if-*` explores both arms. */
    override fun execute(frame: Frame): OpExecution {
        val result = operation.applyTo(frame.read(lhsRegister), frame.read(rhsRegister))
            ?.let { Value.Known(it, INT) } ?: Value.Unknown(INT)
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, result)))
    }

    override fun toString(): String = "$name r$destRegister, r$lhsRegister, r$rhsRegister"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
