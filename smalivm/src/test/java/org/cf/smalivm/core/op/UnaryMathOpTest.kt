package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.experimental.inv

/**
 * `neg-*`/`not-*`/the eighteen primitive conversions. Migrated from the oracle-driven original;
 * the fixture (`ops/unary_math_test.smali`) and every expected value are unchanged, so a
 * divergence here is the new core's, not a re-specification.
 */
class UnaryMathOpTest {
    companion object {
        private val CLASS_NAME = "Lunary_math_test;"
    }

    /**
     * Liveness pin, found by mutation rather than reasoning: emptying `UnaryMathOp.registersRead`
     * left the whole suite green, because `negInt()V`'s source and destination happen to be the
     * same register (v0), so `registersAssigned` alone keeps `availableRegisters` sound and no value
     * assertion above notices the read was never recorded. `registersRead` is a liveness query the
     * optimizer reads directly (spec §9), so it needs a witness of its own rather than riding along
     * on a value assertion that cannot distinguish "read" from "never read".
     */
    @Test
    fun negIntReportsItsSourceRegisterAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "negInt()V")
        assertEquals(setOf(0), graph.registersRead(0))
    }

    @Nested
    inner class DoubleValue {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun testDoubleToFloat() {
            initial.setRegisters(0, 220.0, "D")
            expected.setRegisters(0, 220f, "F")
            CoreTester.test(CLASS_NAME, "doubleToFloat()V", initial, expected)
        }

        /**
         * Bug class: narrowing double-to-float by a path that drops the fraction before narrowing
         * (e.g. truncating to `Long` first, then converting) -- indistinguishable from correct
         * behavior by [testDoubleToFloat], whose seed (`220.0`) is already integral and so has no
         * fraction for such a bug to touch. Restores the fraction-preserving half of the deleted mockk
         * test `canDoubleToFloat` (Task 9's original adjudication verdicted the whole test a duplicate
         * of `testDoubleToFloat`, missing that a fractional seed is a materially different claim from
         * an integral one for a narrowing conversion). Oracle: IEEE 754 double-to-float narrowing
         * (round-to-nearest), independently computed and cross-checked bit-for-bit against a live JVM
         * (`(float) 11204.0345612345` == `0x462f1023`, decimal `11204.0341796875f`) rather than read
         * off this op's own output.
         */
        @Test
        fun testDoubleToFloatPreservesTheFraction() {
            initial.setRegisters(0, 11204.0345612345, "D")
            expected.setRegisters(0, 11204.0341796875f, "F")
            CoreTester.test(CLASS_NAME, "doubleToFloat()V", initial, expected)
        }

        @Test
        fun testDoubleToInt() {
            initial.setRegisters(0, 200.0, "D")
            expected.setRegisters(0, 200, "I")
            CoreTester.test(CLASS_NAME, "doubleToInt()V", initial, expected)
        }

        /**
         * Bug class: converting via a ceiling (or any rounding) instead of truncation toward zero --
         * indistinguishable from correct behavior by [testDoubleToInt], whose seed (`200.0`) is already
         * integral. Restores the fraction-preserving half of the deleted mockk test `canDoubleToInt`.
         * Oracle: Dalvik's `double-to-int` truncates toward zero, so `11204.0345612345` truncates to
         * `11204`, never `ceil`'s `11205`.
         */
        @Test
        fun testDoubleToIntTruncatesTheFraction() {
            initial.setRegisters(0, 11204.0345612345, "D")
            expected.setRegisters(0, 11204, "I")
            CoreTester.test(CLASS_NAME, "doubleToInt()V", initial, expected)
        }

        /**
         * The NEGATIVE direction of [testDoubleToIntTruncatesTheFraction], and it was uncovered anywhere
         * in the suite until Plan 8 Task 3 needed it. Both fraction-preserving truncation tests in this
         * file seed a positive value, which distinguishes truncation from `ceil` but **not** from
         * `floor` -- `floor(11204.03)` is also `11204`. The only witness of the negative direction was
         * `CoerceTest`'s `Coerce.toInt(-1.9) == -1` row, which Task 3 removed when it narrowed
         * `Coerce.toInt` to refuse floating-point raws outright, so the claim is re-homed here rather
         * than lost. Verified by mutation: `convertFromDouble`'s `"I" -> value.toInt()` replaced with
         * `floor(value).toInt()` kills this test and nothing else.
         *
         * Oracle: Dalvik's `double-to-int` truncates toward zero, so `-11204.0345612345` becomes
         * `-11204`, never `floor`'s `-11205`.
         */
        @Test
        fun testDoubleToIntTruncatesTowardZeroRatherThanFlooring() {
            initial.setRegisters(0, -11204.0345612345, "D")
            expected.setRegisters(0, -11204, "I")
            CoreTester.test(CLASS_NAME, "doubleToInt()V", initial, expected)
        }

        @Test
        fun testDoubleToLong() {
            initial.setRegisters(0, 210.0, "D")
            expected.setRegisters(0, 210L, "J")
            CoreTester.test(CLASS_NAME, "doubleToLong()V", initial, expected)
        }

        /**
         * `neg-double` on a register pair loaded the way a compiler loads a double literal:
         * `const-wide v0, 0x4000000000000000` -> `Value.Known(4611686018427387904L, "D")`
         * (`core/Op.kt`'s `buildConst` -- `const-wide` is the one opcode that puts a `Long` raw under a `D`
         * tag). **Verifier-VALID input**, i.e. ordinary compiler output rather than the unverifiable input
         * `Coerce.toInt`'s boundary is about, which is what makes this the more severe of the two halves
         * Plan 8 Task 3 touched.
         *
         * Oracle: a device reads the pair's 64 bits as double bits, so `0x4000000000000000` is `2.0` and the
         * negation is `-2.0`. Read by value the register was `4.611686018427388E18` and this op folded
         * `-4.611686018427388E18` -- a definite `Known` eighteen orders of magnitude out. Numbers from
         * `Double.longBitsToDouble`, cross-checked on a live JVM.
         */
        @Test
        fun aNegDoubleOnConstWideLiteralBitsAnswersWhatADeviceAnswers() {
            initial.setRegisters(0, 4611686018427387904L, "D")
            expected.setRegisters(0, -2.0, "D")
            CoreTester.test(CLASS_NAME, "negDouble()V", initial, expected)
        }

        /**
         * The other direction, and it is the one place the bit-reading rule legitimately REFUSES: a 64-bit
         * read of a register holding a NARROW raw. A device reads v0 **and v1**; the engine's
         * one-`Value`-per-wide-pair representation puts nothing readable in v1 for a narrow value, and
         * `Coerce.toDouble` is handed only v0's raw -- so 32 of the 64 bits do not exist at that layer and
         * answering any number would invent them.
         *
         * Read by value this folded `-5.0`, which is a number no device produces for this input. `Unknown(D)`
         * is the honest answer (spec §2.2) and, unlike the bit-readable cases, it is NOT over-refusal: there
         * is genuinely no bit-exact answer available here, which is exactly the distinction the round-1
         * review found stated wrongly for the cases where one IS available.
         */
        @Test
        fun aNegDoubleReadingANarrowRawIsNotProvableBecauseTheHighHalfIsAnotherRegister() {
            initial.setRegisters(0, 5, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "D")
            CoreTester.test(CLASS_NAME, "negDouble()V", initial, expected)
        }

        /**
         * The `J` counterpart of [testDoubleToIntTruncatesTheFraction] -- a separate `when` branch in
         * `UnaryMathOp.convertFromDouble`, so it needs its own witness. Restores the fraction-preserving
         * half of the deleted mockk test `canDoubleToLong`.
         */
        @Test
        fun testDoubleToLongTruncatesTheFraction() {
            initial.setRegisters(0, 11204.0345612345, "D")
            expected.setRegisters(0, 11204L, "J")
            CoreTester.test(CLASS_NAME, "doubleToLong()V", initial, expected)
        }

        @Test
        fun testNegDouble() {
            initial.setRegisters(0, 15.1, "D")
            expected.setRegisters(0, -15.1, "D")
            CoreTester.test(CLASS_NAME, "negDouble()V", initial, expected)
        }
    }

    @Nested
    inner class FloatValue {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun testFloatToDouble() {
            initial.setRegisters(0, 120f, "F")
            expected.setRegisters(0, 120.0, "D")
            CoreTester.test(CLASS_NAME, "floatToDouble()V", initial, expected)
        }

        /**
         * Bug class: widening float-to-double by a path that loses precision instead of the required
         * exact, lossless widen -- indistinguishable from correct behavior by [testFloatToDouble], whose
         * seed (`120f`) is already integral and round-trips exactly regardless. Restores the
         * fraction-preserving half of the deleted mockk test `canFloatToDouble` (verdicted a plain
         * duplicate by this task's original adjudication). Oracle: IEEE 754 float-to-double widening is
         * exact (no rounding is possible going from fewer bits to more); the literal `11204.0345f` is
         * itself only representable as `11204.0341796875` in `float`, and `double` must reproduce that
         * exact value -- independently cross-checked bit-for-bit against a live JVM
         * (`(double)(float) 11204.0345f` == `0x40c5e20460000000`).
         */
        @Test
        fun testFloatToDoublePreservesTheFraction() {
            initial.setRegisters(0, 11204.0345f, "F")
            expected.setRegisters(0, 11204.0341796875, "D")
            CoreTester.test(CLASS_NAME, "floatToDouble()V", initial, expected)
        }

        @Test
        fun testFloatToInt() {
            initial.setRegisters(0, 100f, "F")
            expected.setRegisters(0, 100, "I")
            CoreTester.test(CLASS_NAME, "floatToInt()V", initial, expected)
        }

        /**
         * Bug class: converting via a ceiling (or any rounding) instead of truncation toward zero --
         * indistinguishable from correct behavior by [testFloatToInt], whose seed (`100f`) is already
         * integral. Restores the fraction-preserving half of the deleted mockk test `canFloatToInt`.
         * Oracle: Dalvik's `float-to-int` truncates toward zero; the stored value of the `11204.0345f`
         * literal (`11204.0341796875f`, per the derivation above) truncates to `11204`, never `ceil`'s
         * `11205`.
         */
        @Test
        fun testFloatToIntTruncatesTheFraction() {
            initial.setRegisters(0, 11204.0345f, "F")
            expected.setRegisters(0, 11204, "I")
            CoreTester.test(CLASS_NAME, "floatToInt()V", initial, expected)
        }

        @Test
        fun testFloatToLong() {
            initial.setRegisters(0, 110f, "F")
            expected.setRegisters(0, 110L, "J")
            CoreTester.test(CLASS_NAME, "floatToLong()V", initial, expected)
        }

        /**
         * `float-to-int` is **two steps and only the first one changed**, which is the whole shape of the
         * round-1 fix: the op READS v0 at float width -- the register's 32 bits reinterpreted, so
         * `const v0, 0x3f800000` reads as `1.0f` -- and then CONVERTS by value in
         * `UnaryMathOp.convertFromFloat`, where [JLS] §5.1.3 truncation toward zero genuinely is what
         * `float-to-int` does to the value it read. Answer: `1`.
         *
         * Read by value the register was `1.0653532E9` and this op folded `1065353216` -- the seed straight
         * back out, which is what makes the seed discriminating: an implementation that skipped the
         * reinterpretation returns its own input, and no arithmetic slip produces `1` by accident.
         */
        @Test
        fun aFloatToIntOnConstLoadedLiteralBitsReadsTheBitsThenConverts() {
            initial.setRegisters(0, 1065353216, "I")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(CLASS_NAME, "floatToInt()V", initial, expected)
        }

        /**
         * The `J` counterpart of [testFloatToIntTruncatesTheFraction] -- a separate `when` branch in
         * `UnaryMathOp.convertFromFloat`, so it needs its own witness. Restores the fraction-preserving
         * half of the deleted mockk test `canFloatToLong`.
         */
        @Test
        fun testFloatToLongTruncatesTheFraction() {
            initial.setRegisters(0, 11204.0345f, "F")
            expected.setRegisters(0, 11204L, "J")
            CoreTester.test(CLASS_NAME, "floatToLong()V", initial, expected)
        }

        @Test
        fun testNegFloat() {
            initial.setRegisters(0, 10.5f, "F")
            expected.setRegisters(0, -10.5f, "F")
            CoreTester.test(CLASS_NAME, "negFloat()V", initial, expected)
        }
    }

    @Nested
    inner class IntValue {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun testIntToByte() {
            val value = 128
            initial.setRegisters(0, value, "I")
            expected.setRegisters(0, value.toByte(), "B")
            CoreTester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testIntToByteWithShort() {
            val value: Short = 1000
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.toByte(), "B")
            CoreTester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testIntToChar() {
            initial.setRegisters(0, 0x62, "I")
            expected.setRegisters(0, 0x62.toChar(), "C")
            CoreTester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testIntToCharWithShort() {
            val value: Short = 0x62
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.toInt().toChar(), "C")
            CoreTester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testIntToDouble() {
            initial.setRegisters(0, 13, "I")
            expected.setRegisters(0, 13.0, "D")
            CoreTester.test(CLASS_NAME, "intToDouble()V", initial, expected)
        }

        @Test
        fun testIntToFloat() {
            initial.setRegisters(0, 12, "I")
            expected.setRegisters(0, 12f, "F")
            CoreTester.test(CLASS_NAME, "intToFloat()V", initial, expected)
        }

        @Test
        fun testIntToLong() {
            initial.setRegisters(0, 11, "I")
            expected.setRegisters(0, 11L, "J")
            CoreTester.test(CLASS_NAME, "intToLong()V", initial, expected)
        }

        /**
         * `int-to-float` reading a register whose raw is a genuinely-computed `Float`, i.e. a read at
         * the `I` domain's width of bits that hold an IEEE-754 value. The answer is `Unknown(F)`, not a
         * value.
         *
         * Bug class: a confidently wrong `Known`, which is what constant propagation folds into
         * rewritten bytecode. Oracle: Dalvik, not engine output. `int-to-float` reads v0's **32 bits**
         * as a signed int and converts *that*; `5.9f`'s bit pattern is `0x40BCCCCD`, i.e. 1086115021,
         * so a device answers **`1.0861151E9f`** (`(float) 1086115021`, whose exact value is
         * `1086115072.0f`). `Coerce.toInt(5.9f)` instead performed a [JLS] §5.1.3 narrowing conversion
         * -- round toward zero -- and this op answered a definite `Known(5.0f)`. The two numbers are
         * unrelated, and the engine's was not marked as a guess.
         *
         * **What this test pins is the SAFE answer, not the precise one, and round 1 of the review
         * corrected the reasoning here.** An earlier revision said the engine "has no bit view of a
         * register (spec §3 declines to pay for one)" and that reopening the migrated corpus made the
         * bit-exact read too expensive. Both are false: `Float.floatToRawIntBits(5.9f)` IS 1086115021,
         * and making this read bit-exact moves **zero** migrated expectations (measured). So `Unknown`
         * here is a deliberate precision loss with a named blocker in `Coerce.toInt`'s KDoc -- not the
         * only available answer. Which SIDE of the soundness line: a single-successor op, so the same
         * one path is explored either way; the trade is a wrong constant for no constant, where a right
         * constant is reachable.
         *
         * **No differential could have found this**: the oracle narrows identically, through
         * `Value.toInteger` -> `Utils.getIntegerValue` -> `castToPrimitive` -> `intValue()`
         * (`opcode/UnaryMathOp.kt`'s `perform`, the `opName.startsWith("int")` branch), so both engines
         * answer `Known(5.0f)` and agree while both are wrong. Registered as
         * `unary-int-to-float-computed-float-raw` in `Fixtures.intendedDivergences` now that the core's
         * answer differs.
         *
         * Reachable with this build's opcode set: `int-to-float v0, v1` or `div-float` leaves a computed
         * `Float` raw in a register, and any `int-*` op reading it arrives here. Seeded rather than
         * fixture-driven because the seed states the same register contents more directly, and because
         * v0's own tag is `F` while `int-to-float`'s source domain is `I` -- which is the cross-domain
         * read itself.
         */
        @Test
        fun anIntToFloatReadingAComputedFloatRawIsNotProvableRatherThanTruncatedByValue() {
            initial.setRegisters(0, 5.9f, "F")
            expected.setRegisters(0, CoreState.UNKNOWN, "F")
            CoreTester.test(CLASS_NAME, "intToFloat()V", initial, expected)
        }

        @Test
        fun testIntToShort() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 0x10.toShort(), "S")
            CoreTester.test(CLASS_NAME, "intToShort()V", initial, expected)
        }

        @Test
        fun testNegInt() {
            initial.setRegisters(0, 0x42, "I")
            expected.setRegisters(0, -0x42, "I")
            CoreTester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNegIntWithShort() {
            val value: Short = 0x42
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, -value, "I")
            CoreTester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNegUnknownInt() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNotInt() {
            initial.setRegisters(0, 0x42, "I")
            expected.setRegisters(0, 0x42.inv(), "I")
            CoreTester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testNotIntWithShort() {
            val value: Short = 0x42
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.inv().toInt(), "I")
            CoreTester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testNotUnknownInt() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testUnknownIntToByte() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "B")
            CoreTester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testUnknownIntToChar() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "C")
            CoreTester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testUnknownIntToDouble() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "D")
            CoreTester.test(CLASS_NAME, "intToDouble()V", initial, expected)
        }

        @Test
        fun testUnknownIntToFloat() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "F")
            CoreTester.test(CLASS_NAME, "intToFloat()V", initial, expected)
        }

        @Test
        fun testUnknownIntToLong() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "J")
            CoreTester.test(CLASS_NAME, "intToLong()V", initial, expected)
        }

        @Test
        fun testUnknownIntToShort() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "S")
            CoreTester.test(CLASS_NAME, "intToShort()V", initial, expected)
        }
    }

    @Nested
    inner class LongValue {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun testLongToDouble() {
            initial.setRegisters(0, 23L, "J")
            expected.setRegisters(0, 23.0, "D")
            CoreTester.test(CLASS_NAME, "longToDouble()V", initial, expected)
        }

        /**
         * Bug class: widening `long-to-double` implemented by accidentally routing through `Float`
         * first (e.g. copy-pasting the `"F"` branch immediately above `"D"` in
         * `UnaryMathOp.convertFromLong`) -- a realistic copy-paste defect, not a contrived one, since
         * the two branches sit adjacent in a `when`. `Float`'s mantissa is 24 bits, exact only for
         * integers up to `2^24` (16,777,216); `23L` in [testLongToDouble] needs only 5 bits and
         * round-trips through `Float` exactly, so it cannot witness this bug. `112040345L` needs 27
         * bits, so `(float) 112040345L` is `112040344.0f` -- independently confirmed on a live JVM,
         * not assumed. `longToDouble()V` is otherwise exercised by no other test in this file. Restores
         * the value claim of the deleted mockk test `canLongToDouble`, which this task's adjudication
         * wrongly verdicted a duplicate of `testLongToDouble`: the two seeds differ in exactly the
         * property (magnitude relative to `Float`'s mantissa) this bug class depends on. Oracle:
         * `Double`'s mantissa is 53 bits, exact for every integer up to `2^53`; `112040345` (27 bits)
         * is far below that threshold, so its `Long`-to-`Double` widening is exact at precisely
         * `112040345.0`, never `112040344.0` -- unlike the general case, not every `Long` widens to
         * `Double` exactly (`Long` has 64 bits), but this specific value does.
         */
        @Test
        fun testLongToDoublePreservesPrecisionBeyondFloatsMantissa() {
            initial.setRegisters(0, 112040345L, "J")
            expected.setRegisters(0, 112040345.0, "D")
            CoreTester.test(CLASS_NAME, "longToDouble()V", initial, expected)
        }

        @Test
        fun testLongToFloat() {
            initial.setRegisters(0, 22L, "J")
            expected.setRegisters(0, 22f, "F")
            CoreTester.test(CLASS_NAME, "longToFloat()V", initial, expected)
        }

        @Test
        fun testLongToInt() {
            initial.setRegisters(0, 21L, "J")
            expected.setRegisters(0, 21, "I")
            CoreTester.test(CLASS_NAME, "longToInt()V", initial, expected)
        }

        /**
         * The `J`-width half of
         * [IntValue.anIntToFloatReadingAComputedFloatRawIsNotProvableRatherThanTruncatedByValue], and it
         * is a **separate claim** rather than a second example: `long-to-int` reads through
         * `Coerce.toLong`, not `Coerce.toInt`, so a refusal that covers only the 32-bit read leaves the
         * whole `J` domain answering confidently. Nothing in the repository named the `toLong` sites
         * before this task -- neither the carry-forward register nor any review -- and the defect there
         * is identical.
         *
         * Oracle: Dalvik reads v0/v1 as 64 bits and truncates the low 32
         * (`opcode/UnaryMathOp.kt`'s `opName.startsWith("long")` branch does `value.toLong().toInt()`);
         * `5.9`'s bit pattern is **`0x401799999999999A`** (= 4618328827877759386), whose low half is
         * **`0x9999999A`, i.e. -1717986918** as a signed int. `Coerce.toLong(5.9)` was `5L` -- via its
         * `Coerce.toInt` gate, then `raw.toLong()` -- so this op answered a definite `Known(5)`.
         *
         * **Round 1 correction: the hex an earlier revision gave here, `0x4017AE147AE147AE`, is the bit
         * pattern of 5.92, not 5.9, and its low half 2061584302 was wrong with it.** Recomputed from
         * `Double.doubleToRawLongBits` on a live JVM. Same standing as its sibling: a single-successor
         * op, so this is a deliberate precision loss rather than the only sound answer -- see
         * `Coerce.toInt`'s KDoc for the price and the blocker.
         */
        @Test
        fun aLongToIntReadingAComputedDoubleRawIsNotProvableRatherThanTruncatedByValue() {
            initial.setRegisters(0, 5.9, "D")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "longToInt()V", initial, expected)
        }

        @Test
        fun testNegLong() {
            initial.setRegisters(0, 0x100L, "J")
            expected.setRegisters(0, -0x100L, "J")
            CoreTester.test(CLASS_NAME, "negLong()V", initial, expected)
        }

        @Test
        fun testNotLong() {
            initial.setRegisters(0, 100L, "J")
            expected.setRegisters(0, 100L.inv(), "J")
            CoreTester.test(CLASS_NAME, "notLong()V", initial, expected)
        }
    }
}
