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
 * `cmp-long`, `cmpl-float`, `cmpg-float`, `cmpl-double`, `cmpg-double`: three-way compare into an
 * `I` result. The result is always narrow, so [registersAssigned] is a plain single-register set --
 * unlike a wide-RESULT op, this never needs [Op.destinationRegisters].
 *
 * The `l`/`g` suffix is only about NaN, which is unordered: `cmpg-*` answers 1 and `cmpl-*` answers
 * -1 when either operand is NaN, which is what lets a compiler build both `<` and `>` from one
 * compare-then-branch pair.
 *
 * KNOWN IMPRECISION, parity-preserving and deliberate: this uses `Float.compare`/`Double.compare`,
 * which order `-0.0 < 0.0`, where Dalvik's `cmp*` answers 0 for that pair. The design spec lists
 * negative-zero compare semantics as explicitly out of scope, and the oracle (`opcode/CmpOp.kt:56,62`,
 * via `java.lang.Float/Double.compare`) has the same behavior, so matching it keeps the differential
 * honest. Fix it only if a real sample needs it.
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
     * Null when either operand is not a provable number: the comparison is not decidable.
     *
     * **`cmp-long`'s two reads are guarded and its float/double reads deliberately are not.** Since Plan 8
     * Task 3, `Coerce.toLong` answers null for a `Float`/`Double` raw -- **not because the bits are
     * unavailable, which an earlier revision of this line claimed, but as a deliberate precision loss with a
     * named blocker** (see `Coerce.toInt`'s KDoc) -- rather than performing a [JLS] §5.1.3 conversion, so
     * `cmp-long` on a computed `Float` answers `Unknown(I)` instead of a confident comparison. This op was a
     * **fourth** family with that defect and nothing in the repository had named it: the carry-forward
     * register's site list covered `BinaryMathOp` and `UnaryMathOp` only, and only their `Coerce.toInt`
     * reads. Pinned by `core/op/CmpOpTest.aCmpLongReadingAComputedFloatRawIsNotProvable`.
     *
     * **The float and double reads above ARE bit-reinterpreting, as of Task 3's fix round 1, and an earlier
     * revision of this paragraph said the opposite in all three of its clauses.** It read: *"The
     * `toFloat`/`toDouble` reads above are same-domain for an IEEE raw and are unguarded on purpose; the
     * mirror case (an `F`/`D` read of an integral raw) is an open item ...; and refusing it would break
     * `const`-loaded float literals."* Corrected:
     *  - they are **not unguarded** -- `Coerce.toFloat` answers `intBitsToFloat(the register's 32 bits)` and
     *    `Coerce.toDouble` answers `longBitsToDouble(the pair's 64)`;
     *  - the mirror case is **not open** -- it is fixed, and it was the more severe half, because
     *    `const-wide v0, 0x4000000000000000` then `cmpg-double` is ordinary compiler output that used to
     *    fold a comparison of `4.611686018427388E18` against its neighbour instead of `2.0`;
     *  - *"refusing would break `const`-loaded literals"* was true and led to the wrong conclusion: the
     *    third option, reading the bits, breaks nothing and is what ships.
     *
     * `cmp-double` is the family where this is hardest to witness, and the pin says why:
     * `core/op/CmpOpTest.aCmpDoubleOnConstWideLiteralBitsOrdersTheDoublesNotTheirBitPatterns` seeds two
     * NEGATIVE doubles, because for positive ones the bit pattern read as a signed long is order-isomorphic
     * to the value and both read modes return the same comparison.
     *
     * A `cmp-*` has exactly one successor, so the refusal explores neither more nor fewer paths -- but the
     * `Unknown(I)` it writes is what the following `if-*` reads, and an unprovable predicate forks both
     * arms, which is spec §2.2's "explore more".
     */
    private fun compared(lhs: Value, rhs: Value): Int? {
        if (lhs !is Value.Known || rhs !is Value.Known) return null
        return when {
            name.endsWith("float") -> {
                val a = Coerce.toFloat(lhs.raw) ?: return null
                val b = Coerce.toFloat(rhs.raw) ?: return null
                if (a.isNaN() || b.isNaN()) nanResult() else java.lang.Float.compare(a, b)
            }
            name.endsWith("double") -> {
                val a = Coerce.toDouble(lhs.raw) ?: return null
                val b = Coerce.toDouble(rhs.raw) ?: return null
                if (a.isNaN() || b.isNaN()) nanResult() else java.lang.Double.compare(a, b)
            }
            else -> {
                val a = Coerce.toLong(lhs.raw) ?: return null
                val b = Coerce.toLong(rhs.raw) ?: return null
                java.lang.Long.compare(a, b)
            }
        }
    }

    /** NaN is unordered, so the mnemonic picks the answer: `cmpg` biases high, `cmpl` low. */
    private fun nanResult(): Int = if (name.startsWith("cmpg")) 1 else -1

    override fun toString(): String = "$name r$destRegister, r$lhsRegister, r$rhsRegister"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
