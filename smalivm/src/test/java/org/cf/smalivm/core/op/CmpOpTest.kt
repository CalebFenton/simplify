package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * `cmp-long`, `cmpl/g-float`, `cmpl/g-double`. Migrated from the oracle-driven original; the
 * fixture (`ops/cmp_test.smali`) and every expected value are unchanged, so a divergence here is
 * the new core's, not a re-specification.
 */
class CmpOpTest {

    companion object {
        private const val CLASS_NAME = "Lcmp_test;"
    }

    /**
     * Liveness pin, found by mutation rather than reasoning: every `cmp_test.smali` fixture's
     * destination register IS its lhs operand (`cmpl-float v0, v0, v1`), so `registersAssigned`
     * alone already reports v0 as touched -- unlike `UnaryMathOp`'s equivalent gap, though, the rhs
     * operand (v1 here) is a DIFFERENT register that `registersAssigned` never names, so dropping it
     * from `registersRead` is a real liveness loss that only a query reading `registersRead`
     * directly, rather than any value assertion, can see.
     */
    @Test
    fun cmplFloatReportsBothOperandRegistersAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "cmplFloat()V")
        assertEquals(setOf(0, 1), graph.registersRead(0))
    }

    @Nested
    inner class CmpDouble {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @Test
        fun canCmpgDoubleWithEqual() {
            initial.setRegisters(0, 4.5, "D", 2, 4.5, "D")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5, "D", 2, 0.5, "D")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithLessThan() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        /**
         * `cmp-double` on two register pairs loaded as `const-wide` literals, and the seed is chosen for the
         * one property that separates a bit read from a by-value read for a COMPARISON: the two answers must
         * ORDER the operands differently, which for doubles happens only among negatives.
         *
         * `bits(-2.0)` is `-4611686018427387904L` and `bits(-4.0)` is `-4607182418800017408L`. Bit-correct:
         * `-2.0 > -4.0`, so `cmpg-double` answers **1**. By value: `-4.611686018427388E18 <
         * -4.6071824188000174E18`, so it answered **-1**. Two positive doubles could not witness this at all
         * -- their bit patterns are order-isomorphic to their values -- which is why an ordinary
         * `const-wide` pair would have passed either way.
         *
         * Bug class: a confident wrong comparison on **verifier-VALID** input, and a comparison is what the
         * following `if-*` branches on, so the wrong constant selects the wrong arm. Numbers from
         * `Double.doubleToRawLongBits`/`longBitsToDouble`, cross-checked on a live JVM.
         */
        @Test
        fun aCmpDoubleOnConstWideLiteralBitsOrdersTheDoublesNotTheirBitPatterns() {
            initial.setRegisters(0, -4611686018427387904L, "D", 2, -4607182418800017408L, "D")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithNaN() {
            initial.setRegisters(0, 0.5, "D", 2, Double.NaN, "D")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "D", 2, CoreState.UNKNOWN, "D")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        /**
         * The LHS counterpart of [canCmplDoubleWithNaN]: every double NaN case above puts NaN on the
         * RHS. `compared()`'s NaN check (`a.isNaN() || b.isNaN()`) reads as symmetric, but dropping the
         * `a.isNaN() ||` half (keeping only `b.isNaN()`) still compiles -- neither operand needs a
         * smart-cast the way the `is Value.Known` guard above it does -- and left the whole suite green
         * before this test existed, confirmed empirically rather than assumed. Restores the deleted
         * mockk test's claim (`canCompareDoubleLessWithArg1NaN`) from the behavioral side; that test
         * also asserted `op.toString()`, restored separately in `OpBuilderTest`.
         *
         * MUST be `cmpl`, not `cmpg`: `java.lang.Double.compare(NaN, 20.5)` -- the fallback a dropped
         * `a.isNaN()` check falls through to -- already returns a POSITIVE number on its own (NaN sorts
         * as greatest), which coincidentally agrees with `cmpg`'s `nanResult()` of 1 regardless of
         * whether the explicit check ever ran. Only `cmpl`'s `nanResult()` of -1 disagrees with that
         * fallback, so only `cmpl` can tell "checked explicitly" apart from "fell through and got lucky"
         * -- confirmed by first writing this test against `cmpg` and finding it passed under the
         * mutation too.
         */
        @Test
        fun canCmplDoubleWithLhsNaN() {
            initial.setRegisters(0, Double.NaN, "D", 2, 20.5, "D")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithEqual() {
            initial.setRegisters(0, 4.5, "D", 2, 4.5, "D")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5, "D", 2, 0.5, "D")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithLessThan() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithNaN() {
            initial.setRegisters(0, 0.5, "D", 2, Double.NaN, "D")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "D", 2, CoreState.UNKNOWN, "D")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }
    }

    @Nested
    inner class FloatCmp {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @Test
        fun canCmpgFloatWithEqual() {
            initial.setRegisters(0, 4.5f, "F", 1, 4.5f, "F")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithLessThan() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithNaN() {
            initial.setRegisters(0, 0.5f, "F", 1, Float.NaN, "F")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "F", 1, CoreState.UNKNOWN, "F")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        /**
         * The LHS counterpart of [canCmplFloatWithNaN] -- see
         * [CmpOpTest.CmpDouble.canCmplDoubleWithLhsNaN] for why this is a genuine gap and not a
         * redundant mirror (the float branch has its own independent `a.isNaN() || b.isNaN()` check, so
         * it needs its own witness) and why it must be `cmpl`, not `cmpg` (`java.lang.Float.compare`'s
         * own NaN-sorts-greatest fallback already agrees with `cmpg`'s `nanResult()` of 1, so only
         * `cmpl`'s -1 can tell "checked explicitly" apart from "fell through and got lucky"). Restores
         * the deleted mockk test's claim (`canCompareFloatLessWithArg1NaN`).
         */
        @Test
        fun canCmplFloatWithLhsNaN() {
            initial.setRegisters(0, Float.NaN, "F", 1, 20.5f, "F")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithEqual() {
            initial.setRegisters(0, 4.5f, "F", 1, 4.5f, "F")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithLessThan() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithNaN() {
            initial.setRegisters(0, 0.5f, "F", 1, Float.NaN, "F")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "F", 1, CoreState.UNKNOWN, "F")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }
    }

    @Nested
    inner class LongCmp {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @Test
        fun canCmplLongWithEqual() {
            initial.setRegisters(0, 0x100L, "J", 2, 0x100L, "J")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmplLongWithGreaterThan() {
            initial.setRegisters(0, 0x100000L, "J", 2, 0x10L, "J")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmplLongWithUnknownArgument() {
            initial.setRegisters(0, 0x100L, "J", 2, CoreState.UNKNOWN, "J")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        /**
         * The LHS counterpart of [canCmplLongWithUnknownArgument]: every surviving behavioral test
         * made the RHS unknown, never the destination-aliased LHS register (`v0`, which every
         * `cmp_test.smali` fixture also uses as its result register). `compared()` checks
         * `lhs`/`rhs` symmetrically, so this proves the deleted mockk test's scenario
         * (`canArg1IsUnknownReturnsUnknown`) from the behavioral side instead of a mocked one.
         */
        @Test
        fun canCmpLongWithUnknownLhsArgument() {
            initial.setRegisters(0, CoreState.UNKNOWN, "J", 2, 0x100L, "J")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmpLongWithLessThan() {
            initial.setRegisters(0, 0x10L, "J", 2, 0x100000L, "J")
            expected.setRegisters(0, -1, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        /**
         * `cmp-long` reading a register whose raw is a genuinely-computed `Float` -- a fourth op family
         * with the same cross-domain read, and one nothing in the repository had named: the carry-forward
         * register's site list covered `BinaryMathOp` and `UnaryMathOp` only, and only their
         * `Coerce.toInt` reads.
         *
         * Bug class: a confidently wrong `Known`, and this one is a *comparison*, so the wrong constant
         * feeds straight into the `if-*` that follows it -- the fold a deobfuscator acts on. Oracle:
         * Dalvik reads v0/v1 as 64 bits, which for a 32-bit `Float` raw depends on a register this op does
         * not read, so the honest answer is that it cannot be determined. `Coerce.toLong(5.9f)` was `5L`,
         * so this op answered a definite `Known(-1)` (5 < 7).
         *
         * Which SIDE of the soundness line: `cmp-long` has exactly one successor, so nothing is explored
         * more or less; the loss is a fold the engine was not entitled to make. The *downstream* `if-*`
         * that reads an `Unknown(I)` will fork both arms, which is spec §2.2's "explore more" and is the
         * correct direction for an unprovable predicate.
         */
        @Test
        fun aCmpLongReadingAComputedFloatRawIsNotProvable() {
            initial.setRegisters(0, 5.9f, "F", 2, 7L, "J")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }
    }
}
