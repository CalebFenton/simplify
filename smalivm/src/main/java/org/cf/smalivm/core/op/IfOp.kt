package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ProvableInt
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.provableInt
import org.cf.smalivm.core.sameReference

/**
 * Which way the two operands must compare for the branch to be taken. `EQ` serves `if-eq` and `if-eqz`, and
 * so on: the `*z` form differs only in where its right-hand operand comes from.
 */
internal enum class IfComparison {
    EQ, NE, LT, GE, GT, LE;

    /** Whether the branch is taken, for a comparison the engine decided (negative / zero / positive). */
    fun holdsFor(cmp: Int): Boolean = when (this) {
        EQ -> cmp == 0
        NE -> cmp != 0
        LT -> cmp < 0
        GE -> cmp >= 0
        GT -> cmp > 0
        LE -> cmp <= 0
    }

    /**
     * Whether the branch is taken when the operands are provably unequal but not by how much. Only the
     * equality pair can answer; null makes the branch fork rather than guess an order.
     */
    fun holdsForUnequal(): Boolean? = when (this) {
        EQ -> false
        NE -> true
        LT, GE, GT, LE -> null
    }
}

/**
 * The twelve conditional branches: `22t` compares two registers, `21t` compares one register with the integer
 * zero ([rhsRegister] `== null`). They compare raw 32-bit register contents, signed, and in Dalvik
 * `(Object) null == (int) 0` ([BC]). A provable predicate takes exactly one arm; an unprovable one explores
 * both, fall-through first.
 */
internal class IfOp(
    address: Int,
    override val name: String,
    private val comparison: IfComparison,
    private val targetAddress: Int,
    private val lhsRegister: Int,
    private val rhsRegister: Int?,
) : Op(address) {

    override val registersRead: Set<Int> =
        if (rhsRegister == null) setOf(lhsRegister) else setOf(lhsRegister, rhsRegister)

    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.readRegister(lhsRegister)
        val rhs = rhsRegister?.let { frame.readRegister(it) }
        val taken = decide(lhs, rhs) ?: return OpExecution.of(OpArm.Continue(), OpArm.Jump(targetAddress))
        return OpExecution.single(if (taken) OpArm.Jump(targetAddress) else OpArm.Continue())
    }

    /**
     * Whether the branch is taken, or null when the predicate is unprovable. The rows are pairwise disjoint
     * over [ProvableInt] cases, so their order does not matter:
     *  1. both operands' bits known: compare them as signed ints;
     *  2. both provably live pointers: identity decides equality, and identical pointers decide every
     *     comparison;
     *  3. one provably zero and the other a live pointer: provably unequal;
     *  4. anything else (an unknown, a float raw, a pointer against a non-zero int): unprovable. The verifier
     *     admits none of the pointer-ordering inputs, so forking rather than deciding them is the engine's
     *     choice.
     */
    private fun decide(lhs: Value, rhs: Value?): Boolean? {
        val lhsBits = provableInt(lhs)
        // The `*z` form's right-hand side is the literal zero, known without a register.
        val rhsBits = rhs?.let { provableInt(it) } ?: ProvableInt.Exactly(0)
        val zero = { bits: ProvableInt -> bits is ProvableInt.Exactly && bits.bits == 0 }
        return when {
            lhsBits is ProvableInt.Exactly && rhsBits is ProvableInt.Exactly ->
                comparison.holdsFor(lhsBits.bits.compareTo(rhsBits.bits))
            rhs != null && lhsBits is ProvableInt.NonZero && rhsBits is ProvableInt.NonZero ->
                sameReference(lhs, rhs)?.let { same -> if (same) comparison.holdsFor(0) else comparison.holdsForUnequal() }
            zero(lhsBits) && rhsBits is ProvableInt.NonZero -> comparison.holdsForUnequal()
            zero(rhsBits) && lhsBits is ProvableInt.NonZero -> comparison.holdsForUnequal()
            else -> null
        }
    }

    override fun toString(): String = buildString {
        append(name).append(" r").append(lhsRegister)
        if (rhsRegister != null) append(", r").append(rhsRegister)
        append(", :addr_").append(targetAddress)
    }
}
