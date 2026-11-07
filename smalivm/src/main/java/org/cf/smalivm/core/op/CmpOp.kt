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
 * `cmp-long`, `cmpl-float`, `cmpg-float`, `cmpl-double`, `cmpg-double`: three-way compare into an `I` result.
 *
 * The `l`/`g` suffix is only about NaN, which is unordered: `cmpg-*` answers 1 and `cmpl-*` answers
 * -1 when either operand is NaN, which is what lets a compiler build both `<` and `>` from one
 * compare-then-branch pair.
 */
internal class CmpOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val lhsRegister: Int,
    val rhsRegister: Int,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(lhsRegister, rhsRegister)
    override val registersAssigned: Set<Int> = setOf(destRegister)

    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.read(lhsRegister)
        val rhs = frame.read(rhsRegister)
        val result = compared(lhs, rhs)?.let { Value.Known(it, INT) } ?: Value.Unknown(INT)
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, result)))
    }

    /**
     * Null when either operand is not [Value.Known] or not readable at the op's width (see [Coerce]); the
     * `Unknown(I)` written instead makes the following `if-*` explore both arms.
     */
    private fun compared(lhs: Value, rhs: Value): Int? {
        if (lhs !is Value.Known || rhs !is Value.Known) return null
        return when {
            // Widening float to double is exact, so it preserves order, signed zero and NaN.
            name.endsWith("float") -> ieeeCompare(
                (Coerce.toFloat(lhs.raw) ?: return null).toDouble(),
                (Coerce.toFloat(rhs.raw) ?: return null).toDouble(),
            )
            name.endsWith("double") -> ieeeCompare(
                Coerce.toDouble(lhs.raw) ?: return null,
                Coerce.toDouble(rhs.raw) ?: return null,
            )
            else -> {
                val a = Coerce.toLong(lhs.raw) ?: return null
                val b = Coerce.toLong(rhs.raw) ?: return null
                java.lang.Long.compare(a, b)
            }
        }
    }

    /**
     * ART's `HandleCmpl`/`HandleCmpg`: IEEE-754 `>`, `==`, `<`, so `-0.0 == 0.0` answers 0. Only NaN fails all
     * three, and the mnemonic's bias answers it. `Double.compare` is not this: it orders `-0.0` below `0.0`.
     */
    private fun ieeeCompare(a: Double, b: Double): Int = when {
        a > b -> 1
        a == b -> 0
        a < b -> -1
        else -> nanResult()
    }

    /** NaN is unordered, so the mnemonic picks the answer: `cmpg` biases high, `cmpl` low. */
    private fun nanResult(): Int = if (name.startsWith("cmpg")) 1 else -1

    override fun toString(): String = "$name r$destRegister, r$lhsRegister, r$rhsRegister"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
