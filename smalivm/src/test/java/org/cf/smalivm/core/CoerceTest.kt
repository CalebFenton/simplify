package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The coercion table. A pure helper with a genuinely tricky domain (spec §10 permits focused unit
 * tests exactly here): Dalvik reuses one opcode across several types, so a register's declared tag is
 * a hint and its raw may be any numeric host box. Expected values come from Dalvik's numeric
 * conversion rules and the oracle's `Utils.castToPrimitive` table (`org/cf/util/Utils.java:56-104`),
 * which the differential compares against.
 */
class CoerceTest {
    @Test
    fun everyNumberBoxCoercesThroughItsNumberConversion() {
        assertEquals(5, Coerce.toInt(5.toByte()))
        assertEquals(5, Coerce.toInt(5.toShort()))
        assertEquals(5, Coerce.toInt(5L))
        assertEquals(5L, Coerce.toLong(5))
        // A Long outside Int range must not round-trip through toInt on its way to toLong, which
        // would silently truncate it (e.g. to -1L) instead of returning it unchanged.
        assertEquals(0xFFFFFFFFL, Coerce.toLong(0xFFFFFFFFL))
        // `Coerce.toFloat(5)` and `Coerce.toDouble(5)` used to be asserted here as `5f` and `5.0`. They are
        // NOT that any more and the rows are not deleted, they MOVED: an `F`/`D`-width read answers the
        // register's BITS, so those two claims now live in
        // [aRegistersBitsAreWhatAFloatWidthReadAnswers] and [aRegisterPairsBitsAreWhatADoubleWidthReadAnswers]
        // with the answers a device gives. Keeping a pointer rather than a silent deletion, because "the
        // by-value table is the contract" is exactly the belief this fix removes.
    }

    /** A boolean in a register is Dalvik's 1/0, which arithmetic must see as a number. */
    @Test
    fun booleansCoerceToOneAndZero() {
        assertEquals(1, Coerce.toInt(true))
        assertEquals(0, Coerce.toInt(false))
        assertEquals(1L, Coerce.toLong(true))
        // `Coerce.toDouble(true) == 1.0` used to be the fourth row. It is `null` now, and deliberately: a
        // 64-bit read of a register holding a one-bit boolean has 32 bits it cannot see. See
        // [aRegisterPairsBitsAreWhatADoubleWidthReadAnswers].
        assertNull(Coerce.toDouble(true))
    }

    /** `add-int` on a char is what a real compiler emits; the char must read as its code point. */
    @Test
    fun charactersCoerceToTheirCodePoint() {
        assertEquals(0x62, Coerce.toInt('b'))
        assertEquals(0x62L, Coerce.toLong('b'))
        // `Coerce.toFloat('b') == 98.0f` used to be the third row -- an `F`-width read now answers the
        // register's 32 BITS reinterpreted, which for code point 0x62 is 1.37E-43. Moved, not deleted; see
        // [aRegistersBitsAreWhatAFloatWidthReadAnswers].
        assertEquals(java.lang.Float.intBitsToFloat(0x62), Coerce.toFloat('b'))
    }

    /**
     * Not coercible is null, NOT an exception and NOT a guessed zero. The caller turns it into
     * `Value.Unknown`, which keeps the path exploring instead of truncating it and never invents a
     * constant. The oracle instead returns the value unchanged and then throws ClassCastException at
     * the call site -- an intended divergence, registered in `Fixtures.intendedDivergences` as
     * `binary-add-int-uncoercible-operand` and `cmp-float-uncoercible-operand`, which pin the oracle's
     * exact failure class and frame so this unit test's claim about the two engines stays checkable.
     */
    @Test
    fun nonNumericRawsAreNotCoercible() {
        assertNull(Coerce.toInt(null))
        assertNull(Coerce.toInt("42"))
        assertNull(Coerce.toInt(Any()))
        assertNull(Coerce.toLong(null))
        assertNull(Coerce.toFloat("x"))
        assertNull(Coerce.toDouble(null))
    }

    /**
     * The provability boundary, pinned in both directions on the function every caller now reaches.
     * [Coerce.toProvableInt] is a one-line delegation to [Coerce.toInt] (as [Coerce.toArrayIndex] is to it),
     * so all three names are ONE implementation of one question and no two callers can answer it
     * differently -- the Global Constraint this task closes.
     *
     * **Why a floating-point raw has no answer.** An integral read asks *"can these bits be read as an
     * int"*, and for an IEEE-754 value they cannot: a device reads the register's 32 bits, so `-0.5f` is
     * `0xBF000000`, i.e. -1090519040, where a by-value [JLS] §5.1.3 narrowing produces `0`. Two unrelated
     * numbers. **Round 1 correction: the engine CAN read the bits** -- `Float.floatToRawIntBits(-0.5f)` is
     * exactly the -1090519040 a device sees, and [Coerce.toFloat]/[Coerce.toDouble] now do read them -- so
     * refusing here is the SAFE answer rather than the only one, and is a priced precision loss. The claim
     * that "spec §3 declines to pay for" a bit view was a citation to a Non-goals section that contains no
     * such claim; it is deleted rather than hedged. `Coerce.toInt`'s KDoc carries the price and the blocker.
     *
     * **This test used to assert the opposite for [Coerce.toInt]** -- `assertEquals(5, Coerce.toInt(5.9f))`
     * -- because Plan 7 narrowed only the array half and left the arithmetic ops reading the narrowed
     * answer. Those two lines were the *scope boundary*, not a contract, and Plan 8 Task 3 removed the
     * boundary: there is no consumer of the narrowing answer for a floating-point raw anywhere in the
     * engine, so keeping a second function to supply it would be keeping scaffolding.
     */
    @Test
    fun aFloatingPointRawIsNotProvableAtAnyIntegralWidth() {
        assertNull(Coerce.toProvableInt(5.9f))
        assertNull(Coerce.toProvableInt(-0.5f))
        assertNull(Coerce.toProvableInt(5.9))
        assertNull(Coerce.toProvableInt(-0.5))
        // The same refusal through the name the arithmetic ops read, which is where the live defect was:
        // `int-to-float`, `add-int`, `neg-int` and `not-int` all reach `toInt` directly.
        assertNull(Coerce.toInt(5.9f))
        assertNull(Coerce.toInt(-0.5f))
        assertNull(Coerce.toInt(5.9))
        assertNull(Coerce.toInt(Float.NaN))
        // ...and `toArrayIndex`, the third name for the same question.
        assertNull(Coerce.toArrayIndex(2.9f))
        // Everything else is accepted, because for these the answer IS the register's low 32 bits and
        // nothing is reinterpreted. Dalvik's tags are ambiguous, so all of them really do arrive.
        assertEquals(5, Coerce.toProvableInt(5))
        assertEquals(5, Coerce.toProvableInt(5.toByte()))
        assertEquals(5, Coerce.toProvableInt(5.toShort()))
        assertEquals(-1, Coerce.toProvableInt(0xFFFFFFFFL))
        assertEquals(0x62, Coerce.toProvableInt('b'))
        assertEquals(1, Coerce.toProvableInt(true))
        assertEquals(0, Coerce.toProvableInt(false))
        // ...and a non-numeric raw is still not coercible, which is a different reason for the same null.
        assertNull(Coerce.toProvableInt("42"))
        assertNull(Coerce.toProvableInt(null))
    }

    /**
     * The 64-bit read inherits the refusal, and this is the test that makes that an assertion rather than
     * an implementation accident.
     *
     * [Coerce.toLong] gates on [Coerce.toInt] and then re-reads the raw, so its `Float`/`Double` refusal
     * comes from the gate. That gate's *value* is discarded for a `Number` raw, which makes replacing it
     * with a bare coercibility test (`raw is Number || raw is Boolean || raw is Char`) look like a
     * simplification -- and that edit would silently un-guard the entire `J` domain of `BinaryMathOp` and
     * `UnaryMathOp` plus `cmp-long`, with no other test noticing. Measured: those are eight of this task's
     * fourteen read sites and NONE of them had a test before it.
     *
     * The op-level witnesses are
     * `core/op/UnaryMathOpTest.aLongToIntReadingAComputedDoubleRawIsNotProvableRatherThanTruncatedByValue`,
     * `core/op/BinaryMathOpTest.aLongDomainOperatorReadingAComputedFloatRawIsNotProvable` and
     * `core/op/CmpOpTest.aCmpLongReadingAComputedFloatRawIsNotProvable`.
     */
    @Test
    fun aFloatingPointRawIsRefusedAtBothIntegralWidths() {
        assertNull(Coerce.toLong(5.9f))
        assertNull(Coerce.toLong(-0.5f))
        assertNull(Coerce.toLong(5.9))
        assertNull(Coerce.toLong(-0.5))
        // And the widths stay independent claims: a Long raw still widens exactly and a narrow raw still
        // sign-extends, so the refusal has not swallowed the gate's original job.
        assertEquals(0xFFFFFFFFL, Coerce.toLong(0xFFFFFFFFL))
        assertEquals(-3L, Coerce.toLong(-3))
    }

    /**
     * The half of the old `narrowingTruncatesRatherThanSaturating` that is still reachable, and it is the
     * load-bearing half: a `Long` raw read at 32 bits **truncates** to its low bits rather than saturating
     * at `Int.MAX_VALUE`. That is exactly what a device does, because narrowing `Long -> Int` on the JVM
     * *is* bit truncation -- which is why `Long` is on the accepted side of the boundary above while
     * `Float`/`Double` are not.
     *
     * Its other two rows (`Coerce.toInt(1.9) == 1`, `Coerce.toInt(-1.9) == -1`) asserted [JLS] §5.1.3
     * round-toward-zero for a floating-point raw, which no caller can now ask for. That behavior is not
     * lost, only relocated to where it is genuinely Dalvik's: `float-to-int`/`double-to-int` read through
     * `Coerce.toFloat`/`Coerce.toDouble` and truncate in `UnaryMathOp.convertFromFloat`/`convertFromDouble`,
     * pinned by `core/op/UnaryMathOpTest.testFloatToIntTruncatesTheFraction` and
     * `testDoubleToIntTruncatesTheFraction`.
     *
     * **One of the two rows was genuinely uncovered elsewhere and had to be re-homed rather than
     * dropped**, which is the trap Plan 4 fell into when it retired 27 change-detectors and 13 were the
     * only witness of something. Both of those op-level tests seed a POSITIVE fraction, which
     * distinguishes truncation from `ceil` but **not** from `floor`; the `-1.9` row was the whole suite's
     * only witness of the negative direction. It is now
     * `core/op/UnaryMathOpTest.testDoubleToIntTruncatesTowardZeroRatherThanFlooring`.
     */
    /**
     * An `F`-width read answers the register's 32 BITS reinterpreted, not a by-value conversion. Every
     * expectation here is derived from [java.lang.Float.intBitsToFloat] on the bits the raw supplies, and
     * cross-checked against a live JVM rather than read off this engine.
     *
     * **The row that makes this a soundness fix rather than a refinement is the first one.** `const v0,
     * 0x3f800000` is how a float literal reaches a register, and `core/Op.kt`'s `buildConst` stores it as an
     * `Int` raw under an `I` tag, so an `add-float` reading it is ORDINARY COMPILER OUTPUT -- not the
     * verifier-invalid input [Coerce.toInt]'s boundary is about. By value it answered `1.0653532E9` per
     * operand and `2.1307064E9` for the sum, where a device answers `1.0f` and `2.0f`.
     */
    @Test
    fun aRegistersBitsAreWhatAFloatWidthReadAnswers() {
        // A float literal as a compiler emits it: `const v0, 0x3f800000` -> Known(1065353216, "I").
        assertEquals(1.0f, Coerce.toFloat(1065353216))
        // The rows that used to read 5f / 98.0f / 1.0f by value.
        assertEquals(java.lang.Float.intBitsToFloat(5), Coerce.toFloat(5))
        assertEquals(java.lang.Float.intBitsToFloat(0x62), Coerce.toFloat('b'))
        assertEquals(java.lang.Float.intBitsToFloat(1), Coerce.toFloat(true))
        assertEquals(java.lang.Float.intBitsToFloat(0), Coerce.toFloat(false))
        // All-ones is NaN, which a by-value read could never produce and a device certainly can.
        assertTrue(Coerce.toFloat(-1)!!.isNaN())
        // Same-domain, and the one row that did not change: reinterpreting a Float's own bits is identity.
        assertEquals(5.9f, Coerce.toFloat(5.9f))
        assertEquals(-0.5f, Coerce.toFloat(-0.5f))
        // A wide raw read at NARROW width: the low half of the pair. bits(-2.0) low 32 are zero.
        assertEquals(0f, Coerce.toFloat(-2.0))
        // ...and non-numeric is still not a register this can read.
        assertNull(Coerce.toFloat("x"))
        assertNull(Coerce.toFloat(null))
    }

    /**
     * The 64-bit sibling. `const-wide v0, 0x4000000000000000` stores `Known(4611686018427387904L, "D")` --
     * `const-wide` is the one opcode that puts a `Long` raw under a `D` tag -- so an `add-double` reading it
     * is ordinary compiler output too. By value it answered `4.611686018427388E18`; a device answers `2.0`.
     *
     * **And a NARROW raw answers null**, which is a genuine "not provable": 32 of the 64 bits live in
     * `register + 1`, a register this call has never seen. That is the opposite failure from the one above --
     * refusing where the answer is unavailable, rather than inventing where it is.
     */
    @Test
    fun aRegisterPairsBitsAreWhatADoubleWidthReadAnswers() {
        assertEquals(2.0, Coerce.toDouble(4611686018427387904L))
        assertEquals(0.5, Coerce.toDouble(4602678819172646912L))
        assertEquals(-2.0, Coerce.toDouble(-4611686018427387904L))
        // Same-domain identity.
        assertEquals(5.9, Coerce.toDouble(5.9))
        // The four narrow raws, all refused: a 64-bit read cannot see `register + 1` from here. The middle
        // two are the rows that used to answer 5.0 and 1.0 by value.
        assertNull(Coerce.toDouble(5))
        assertNull(Coerce.toDouble(true))
        assertNull(Coerce.toDouble('b'))
        assertNull(Coerce.toDouble(5.9f))
        assertNull(Coerce.toDouble(null))
    }

    @Test
    fun aWideRawReadAtThirtyTwoBitsTruncatesRatherThanSaturating() {
        assertEquals(-1, Coerce.toInt(0xFFFFFFFFL))
        assertEquals(0, Coerce.toInt(0x100000000L))
        assertEquals(1, Coerce.toInt(0x100000001L))
    }
}
