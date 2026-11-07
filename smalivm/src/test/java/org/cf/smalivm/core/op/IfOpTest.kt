package org.cf.smalivm.core.op

import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Limits
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Transition
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * The twelve `if-*` opcodes (`22t`) and their `*z` forms (`21t`). Migrated from
 * `org.cf.smalivm.opcode.IfOpTest` -- all **71** behavioral tests, keeping the `@Nested` structure --
 * plus the fork, liveness, effect and engine-choice rows the corpus never covered. Its 72nd test was a
 * mockk change-detector and is adjudicated below.
 *
 * **Every `if` fixture here has the same three addresses**: the branch at 0, a `nop` at 2 (the
 * fall-through), and `return-void` at 3 (the branch target). So `[0, 3]` is "the branch was taken",
 * `[0, 2, 3]` is "it was not" -- **and also** "both arms ran", which is the trap this file's new tests
 * exist for:
 *
 * **`IF_ALL_VISITATIONS` and `IF_FALSE_VISITATIONS` ARE THE SAME SET, in the corpus and here.** A
 * visitation assertion therefore *cannot* distinguish a fork from a fall-through-only run, so
 * re-expressing the corpus's one fork proof as a visitation assertion would have deleted the only fork
 * coverage in the corpus while looking like a strengthening. The three channels that CAN see fork arity
 * are `graph.nodePile(a).size`, `graph.nodePile(a).first().children.size` and `graph.outcomes.size`, and
 * `testIfUnknownIntegerTakesBothPaths` below keeps the first of them and adds the other two.
 *
 * The same blindness runs the other way for the twelve `IF_FALSE_VISITATIONS` tests: a **spurious**
 * fork adds nothing to `{0, 2, 3}`, so those twelve would pass an implementation that forked
 * everything. `aProvableFalsePredicateTakesExactlyOneTransitionWhichVisitationCannotSee` closes that.
 * (The `IF_TRUE_VISITATIONS` tests are *not* blind to it -- an extra fall-through would add address 2 to
 * an exact-set comparison -- which is why the six reference rows below are real evidence.)
 */
class IfOpTest {
    companion object {
        private const val CLASS_NAME = "Lif_test;"
        private const val ADDRESS_IF = 0
        private const val ADDRESS_NOP = 2
        private const val ADDRESS_RETURN = 3

        /** Both arms ran. Deliberately equal to [IF_FALSE_VISITATIONS] -- see the class KDoc. */
        private val IF_ALL_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN)
        private val IF_FALSE_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN)
        private val IF_TRUE_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_RETURN)

        private val INT = VirtualType.of("I")
    }

    /**
     * The 31 same-domain tests: every raw here is `Int`/`Byte` and every comparison is decided by
     * reading both registers as signed 32-bit integers, which is exactly what a device does. **No
     * expectation in this nest moves** -- Dalvik's 32-bit register holds
     * `boolean`/`byte`/`char`/`short`/`int` identically, so they are one domain.
     *
     * These are also what pins OPERAND ORDER behaviorally: `testIfLessThanWith0And1IsTrue` seeds
     * `v0 = 0, v1 = 1` and expects the branch taken, so decoding `registerB` as the left-hand side
     * would answer `1 < 0` and fail. The `if-eq` tests could not see that.
     */
    @Nested
    inner class CompareIdenticalPrimitiveValueTypes {
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        @Test
        fun testIfEqualWithOneAndZeroIsFalse() {
            initial.setRegisters(0, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithTwoEqualIntegersIsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithTwoUnequalIntegersIsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithZeroAndZeroIsTrue() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualZeroWith0ByteIsTrue() {
            initial.setRegisters(0, 0x0.toByte(), "B")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWithOneAndZeroIsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithNegativeOneIsFalse() {
            initial.setRegisters(0, -1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithZeroIsTrue() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith1And0IsFalse() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWith0IsTrue() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWithNegative1IsTrue() {
            initial.setRegisters(0, -1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWithOneIsFalse() {
            initial.setRegisters(0, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanZeroWithNegativeOneIsTrue() {
            initial.setRegisters(0, -1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWith0And0IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWith7ByteIsTrue() {
            initial.setRegisters(0, 0x7.toByte(), "B")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }

    /**
     * The ten reference tests. **Six of them are the whole evidence that a definitely-allocated
     * reference is not an ambiguous predicate**, and all six expect the definite `[0, 3]` -- so if the
     * reference rows of [IfOp]'s decision procedure were ordered wrongly, six exact-set comparisons
     * would fail loudly here rather than silently reporting a dead arm as live. That is not
     * hypothetical: an earlier revision of this plan put "not provably readable at 32 bits" first, which
     * made every reference row unreachable (`Coerce.toInt` refuses a reference raw) and forked all six.
     *
     * `testIfNullEqualZero` rests on the provably-zero row; `testIfObjectNotEqualZero` and
     * `testIfPrimitiveArrayNotEqualZero` on the zero-against-pointer row (note the second seeds
     * `byteArrayOf(0x1)`, which `CoreState.setRegister` allocates as a tracked array, so its raw is a
     * `HeapRef` and not a host array); and `testIdenticalObjectReferencesAreEqual`,
     * `testIfNotEqualWithStringAndArrayReferenceIsTrue`,
     * `testIfNotEqualWithTwoDifferentStringReferencesIsTrue` on the two pointer-identity rows.
     */
    @Nested
    inner class CompareObjectReferences {
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        @Test
        fun testIdenticalObjectReferencesAreEqual() {
            val obj = "object string"
            initial.setRegisters(0, obj, "Ljava/lang/String;", 1, obj, "Ljava/lang/String;")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfFalseBooleanEqualZero() {
            // The corpus wrote `java.lang.Boolean.FALSE`; Kotlin's `false` boxes to the same instance,
            // so the seeded raw is byte-identical and the import is not needed.
            initial.setRegisters(0, false, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWithStringAndArrayReferenceIsTrue() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;", 1, IntArray(0), "[I")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        /**
         * **The test that distinguishes `sameReference` from `Value.agreesWith`, which is the mutation
         * that matters most in this file.** It deliberately builds two equal-content, non-identical
         * `String`s -- the corpus's own comment says *"Need to get crafty or javac will be smart enough
         * to use same literal for both objects"* -- and expects `if-ne` to be **true**. `agreesWith`
         * falls through to host `==` (`core/Value.kt:169`), which answers `true` for these two, which
         * makes `if-ne` **false**, explores only the fall-through, and reaches address 3 without address
         * 2 on a run reporting `Complete`. That is an unexplored feasible path -- the worst outcome this
         * engine produces -- reachable by wiring the row to the plausible-looking existing helper.
         */
        @Test
        fun testIfNotEqualWithTwoDifferentStringReferencesIsTrue() {
            val obj1 = "object string"
            // Need to get crafty or javac will be smart enough to use same literal for both objects
            val obj2 = StringBuilder(obj1).toString()
            initial.setRegisters(0, obj1, "Ljava/lang/String;", 1, obj2, "Ljava/lang/String;")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNullEqualZero() {
            initial.setRegisters(0, null, "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfObjectNotEqualZero() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfPrimitiveArrayNotEqualZero() {
            initial.setRegisters(0, byteArrayOf(0x1), "[B")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfTrueBooleanNotEqualZero() {
            initial.setRegisters(0, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        /**
         * **THE CORPUS'S ONLY FORK PROOF, kept as a pile-count assertion on purpose.** It must NOT be
         * re-expressed as a visitation assertion over both addresses: `IF_ALL_VISITATIONS` and
         * `IF_FALSE_VISITATIONS` are the same set, so such an assertion cannot tell a fork from a
         * fall-through-only run and the instruction would have deleted the only fork coverage the
         * corpus has (see the class KDoc).
         *
         * Strengthened with the other two channels that can see arity -- `children.size` and
         * `outcomes.size` -- so that **dropping either transition** and **adding a spurious third** are
         * each lethal. The visitation assertion below is kept for byte-identical migration and is
         * deliberately not the evidence.
         */
        @Test
        fun testIfUnknownIntegerTakesBothPaths() {
            initial.setRegister(0, Value.Unknown(INT))
            val graph = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", initial)
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_ALL_VISITATIONS)
            assertEquals(1, graph.nodePile(ADDRESS_NOP).size)
            // Two separate execution paths should reach the return op
            assertEquals(2, graph.nodePile(ADDRESS_RETURN).size)
            assertEquals(
                2, graph.nodePile(ADDRESS_IF).first().children.size,
                "one node, two successors -- the assertion a dropped transition cannot survive",
            )
            assertEquals(
                2, graph.outcomes.size,
                "and two ended paths, which is what a spurious THIRD transition would break",
            )
        }

        @Test
        fun testIfZeroIntegerEqualZero() {
            initial.setRegisters(0, Integer.valueOf(0), "I")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }

    /**
     * The 30 cross-type tests, and **the one nest where expectations move**.
     *
     * **13 of the 30 become forks, and they are consequences of Plan 8 Task 3's bit-reinterpretation
     * fix rather than accommodations.** Every one of them carries a `Float` or `Double` **raw**, and a
     * device compares the register's *bits*: `1.0f` is `0x3F800000` = 1065353216, not `1`. The oracle
     * read the host box's numeric *value* (`Value.toInteger` -> `Utils.castToPrimitive`'s
     * `Number.intValue()`), so `1.0` became `1` and it took **one** successor where the truth is
     * unknown -- an unexplored feasible path on a `Complete` run. `Coerce.toProvableInt` refuses such a
     * raw, so the predicate is unprovable and both arms run: `IF_TRUE_VISITATIONS` -> `IF_ALL_VISITATIONS`.
     * Each of the 13 is marked `MOVED` below. **Do not "fix" them back.**
     *
     * **THIRTEEN of the 30 also move FIXTURE, and that is a harness relocation rather than a changed
     * value.** Any seed with a `J`/`D` **tag** needs a register PAIR: `Frame.assign` reserves
     * `register + 1` as the wide high half, and `ifEqual()V` has `.locals 2`, so a wide seed in v1 has
     * nowhere to put it. Measured by reverting the relocation: **8 threw `IllegalArgumentException`
     * before the op ever ran** (`Frame.assign`'s `require(high < registerCount)`), and for the other 5
     * `assign` **cleared the pair's low half** -- `clearWidePartner` fires on v1 because v1 was v0's
     * reserved `WideHigh` -- so v0 read `Top`, the predicate became unprovable, 2 of the 5 failed and
     * **3 passed for the wrong reason** (their expectation is a fork anyway).
     * `testIfEqualWithLongAndDouble` is the shape that shows why no register count fixes it in place: a
     * long in v0 and a double in v1 needs `(v0, v1)` **and** `(v1, v2)` at once, which no device has. The
     * oracle's flat string-keyed state has no high half at all, so the corpus could express a register
     * file that cannot exist -- the core refusing it is the core being right. They now run against
     * `ifEqualWithRoomForWidePairs()V` (`if-eq v0, v2`, `.locals 4`, **identical addresses**), with raws
     * and tags byte-identical and only the operand register renumbered. The 13 are the ones with a wide
     * TAG, which is NOT the same 13 as the ones with a float/double RAW -- five `F`-tagged rows keep
     * `ifEqual()V`, and **five** rows move fixture while keeping their answer (`ByteAndLong`,
     * `IntAndLong`, `LongAndBoolean`, `LongAndChar`, `ShortAndLong`) -- 13 - 8 = 5 on each side, so the
     * decomposition sums to 18 distinct tests rather than to 19.
     *
     * **The discriminator is the RAW's runtime type, not the declared tag.** `testIfEqualWithByteAndLong`
     * keeps its definite answer because a `Long` raw read at 32 bits is bit truncation, which is what a
     * device does; and a `D`-tagged register whose raw is a `Long` (the sanctioned `const-wide` quirk)
     * still coerces, which is why `Coerce` is keyed to the raw.
     *
     * **Which of the 30 rest on a verifier GUARANTEE and which are engine CHOICES: 12 and 18,
     * computed.** [ART]'s `if-eq`/`if-ne` accept *both refs or both integral*, and `IsIntegralTypes`
     * (`runtime/verifier/reg_type.h:211-217`) is a whitelist of `Z`/`B`/`S`/`C`/`I` plus `Zero` that
     * excludes `kLongLo`, `kFloat` **and** `kDoubleLo`. So a pair verifies here exactly when both tags
     * are integral:
     *  - **12 verifier-guaranteed** (`Z`/`B`/`S`/`C`/`I` on both sides): `BooleanAndChar`,
     *    `ByteAndBoolean`, `ByteAndChar`, `ByteAndShort`, `IntAndBoolean`, `IntAndByte`, `IntAndChar`,
     *    `IntAndShort`, `ShortAndBoolean`, `ShortAndChar`, plus the two `*z` rows
     *    `testIfEqualZeroWithFalseIsTrue` and `testIfNotEqualZeroWithTrueIsTrue` (`if-eqz`/`if-nez`
     *    take a ref **or** an integral, and `Z` is integral). For these the answer is a fidelity
     *    target, and [VER] sharpens it: *"The Dalvik verifier is more restrictive than other VMs in one
     *    area: type safety on sub-32-bit integer widths ... it should be impossible to, say, pass a
     *    value outside the range [-128, 127] to a function that takes a byte as an argument."* So for
     *    verified input a `B` tag really is [-128, 127] and a `Z` really is 0 or 1.
     *  - **18 engine choices** for input no verifier admits -- every row involving `J`, `F` or `D`:
     *    the **seven** `J`-tagged rows (`ByteAndLong`, `IntAndLong`, `LongAndBoolean`, `LongAndChar`,
     *    `ShortAndLong`, `LongAndDouble`, `LongAndFloat` -- the last two carry an `F`/`D` raw as well)
     *    and the **eleven** remaining float/double rows. 7 + 11 = 18. **These are NOT tagged INHERITED**: the same [VER] sentence that
     *    licenses the 12 also caps the inheritance bridge -- *"generally the same set of checks"* is not
     *    "the same set", and sub-32-bit width is the documented place Dalvik is **stricter** than the
     *    JVM -- so a `Z`/`B`/`S`/`C` width claim must be checked against [VER] rather than taken from
     *    [JVMS], and the engine's answer for unverifiable input is a soundness-direction choice.
     *
     * Keep the verifier-invalid fixtures and recompute their expectations rather than replacing the inputs:
     * these inputs still have to be handled totally, and the fork is the sound total answer.
     */
    @Nested
    inner class ValueTypeCombinations {
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        @Test
        fun testIfEqualWithBooleanAndChar() {
            initial.setRegisters(0, true, "Z", 1, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndBoolean() {
            initial.setRegisters(0, 1.toByte(), "B", 1, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndChar() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        /** MOVED: a `Double` raw's 32 bits are not the number the host box holds. */
        @Test
        fun testIfEqualWithByteAndDouble() {
            initial.setRegisters(0, 1.toByte(), "B", 2, 1.0, "D")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: a `Float` raw's 32 bits are `0x3F800000`, not `1`. */
        @Test
        fun testIfEqualWithByteAndFloat() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1f, "F")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_ALL_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndLong() {
            initial.setRegisters(0, 1.toByte(), "B", 2, 1L, "J")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndShort() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1.toShort(), "S")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        /** MOVED: `Double` raw. */
        @Test
        fun testIfEqualWithDoubleAndBoolean() {
            initial.setRegisters(0, 1.0, "D", 2, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Double` raw. */
        @Test
        fun testIfEqualWithDoubleAndChar() {
            initial.setRegisters(0, 1.0, "D", 2, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Float` raw. */
        @Test
        fun testIfEqualWithFloatAndBoolean() {
            initial.setRegisters(0, 1f, "F", 1, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Float` raw. */
        @Test
        fun testIfEqualWithFloatAndChar() {
            initial.setRegisters(0, 1f, "F", 1, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_ALL_VISITATIONS)
        }

        /**
         * MOVED, and the corpus's own comment shows the oracle knew this row was fragile: it read the
         * two raws as `Double`s and compared *numbers*. The bits differ in every way -- `3.2f` is
         * `0x404CCCCD` and `3.2`'s low 32 bits are `0x9999999A` -- so unprovable is the honest answer.
         */
        @Test
        fun testIfEqualWithFloatAndDouble() {
            // 3.2 is tricky, 3.2F != 3.2D && Double.compareTo(3.2F, 3.2D)
            initial.setRegisters(0, 3.2f, "F", 2, 3.2, "D")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndBoolean() {
            initial.setRegisters(0, 1, "I", 1, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndByte() {
            initial.setRegisters(0, 1, "I", 1, 1.toByte(), "B")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndChar() {
            initial.setRegisters(0, 1, "I", 1, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        /** MOVED: `Double` raw. */
        @Test
        fun testIfEqualWithIntAndDouble() {
            initial.setRegisters(0, 1, "I", 2, 1.0, "D")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Float` raw. */
        @Test
        fun testIfEqualWithIntAndFloat() {
            initial.setRegisters(0, 1, "I", 1, 1f, "F")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_ALL_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndLong() {
            initial.setRegisters(0, 1, "I", 2, 1L, "J")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndShort() {
            initial.setRegisters(0, 1, "I", 1, 1.toShort(), "S")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndBoolean() {
            initial.setRegisters(0, 1L, "J", 2, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndChar() {
            initial.setRegisters(0, 1L, "J", 2, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_TRUE_VISITATIONS)
        }

        /** MOVED: `Double` raw. */
        @Test
        fun testIfEqualWithLongAndDouble() {
            initial.setRegisters(0, 1L, "J", 2, 1.0, "D")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Float` raw. */
        @Test
        fun testIfEqualWithLongAndFloat() {
            initial.setRegisters(0, 1L, "J", 2, 1f, "F")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndBoolean() {
            initial.setRegisters(0, 1.toShort(), "S", 1, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndChar() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1.toChar(), "C")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        /** MOVED: `Double` raw. */
        @Test
        fun testIfEqualWithShortAndDouble() {
            initial.setRegisters(0, 1.toShort(), "S", 2, 1.0, "D")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_ALL_VISITATIONS)
        }

        /** MOVED: `Float` raw. */
        @Test
        fun testIfEqualWithShortAndFloat() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1f, "F")
            CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_ALL_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndLong() {
            initial.setRegisters(0, 1.toShort(), "S", 2, 1L, "J")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualWithRoomForWidePairs()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualZeroWithFalseIsTrue() {
            initial.setRegisters(0, false, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWithTrueIsTrue() {
            initial.setRegisters(0, true, "Z")
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }

    // ----------------------------------------------------------------------------------------------
    // Rows the corpus never covered. Everything above is migration; everything below is this task's.
    // ----------------------------------------------------------------------------------------------

    /**
     * **The fork's arity in the direction the twelve `IF_FALSE_VISITATIONS` tests are blind to.** A
     * spurious fork adds nothing to `{0, 2, 3}`, so an implementation that forked *every* predicate
     * would pass all twelve. Migrated corpus tests are evidence about values, not about how many paths
     * exist -- measured five consecutive times across Plans 6 and 7 -- so the arity has to be asserted
     * directly.
     *
     * Also pins the **transition ORDER for a real op**, which `CommitTest`'s generic test cannot: that
     * one injects a scripted op through the `opFactory` seam, so swapping [IfOp]'s own two transitions
     * leaves it green. Fall-through first, matching the oracle's `arrayOf(nextAddress, targetAddress)`
     * (`opcode/IfOp.kt:45`).
     */
    @Test
    fun aProvableFalsePredicateTakesExactlyOneTransitionWhichVisitationCannotSee() {
        val definite = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegisters(0, 1, "I")
        })
        assertEquals(CompletionStatus.Complete, definite.completion())
        assertEquals(
            listOf(ADDRESS_NOP), definite.nodePile(ADDRESS_IF).single().children.map { it.address },
            "1 == 0 is provably false, so exactly one successor -- and it is the fall-through",
        )
        assertEquals(1, definite.outcomes.size, "one path, one outcome")

        val forked = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegister(0, Value.Unknown(INT))
        })
        assertEquals(
            listOf(ADDRESS_NOP, ADDRESS_RETURN), forked.nodePile(ADDRESS_IF).single().children.map { it.address },
            "the FALL-THROUGH is the first transition, so it is the first child and the first path explored",
        )
    }

    /**
     * **`registersRead` is real here, not a capacity hint, and `registersAssigned` is empty for a
     * reason that is not `FilledNewArrayOp`'s.** `ExecutionGraph.availableRegisters` is purely static --
     * `(0 until registerCount).filterNot { it in usedRegisters }` over every op's declared read and
     * assigned sets -- so these two sets are the *sole* guarantors of a correct free-register answer for
     * `docs/architecture/graph-optimizer-contract.md:136`'s consumer -- **Dead removal**, the row that
     * *"reads read/assignment metadata"*; `:138` is **Unreflection**, which consumes register layout and was
     * cited here by mistake (corrected by Plan 8 Task 6's fix round 2, which found the same slip in its own
     * twin of this KDoc). An op that lied about its reads
     * would hand out a live register as scratch space. The oracle's own counts are wrong in both
     * directions (`opcode/IfOp.kt:36-37`: `1` for both forms because the initializer runs before the
     * secondary constructor's assignment, and `1` assigned for an op that assigns nothing); they are
     * harmless there and are not to be "restored".
     *
     * **Not vacuous, and the control is the non-empty read set -- NOT the completion gate.** A bare
     * `assertEquals(emptySet(), registersAssigned(0))` passes HARDEST when nothing works: the base `Op`
     * answers `emptySet()`, so an `UnsupportedOp` -- or no op at all -- satisfies it. What rules that
     * out is the **non-empty `registersRead` at the same address**, because an `UnsupportedOp` answers
     * empty for both queries. The `Complete` and `wasReached` assertions below are deliberately kept but
     * prove nothing about these two answers: both queries are **static** (`ExecutionGraph.kt:198-199`
     * reads the decoded op and no node), so they would answer identically on a run that explored
     * nothing. They are here so a fixture that stops decoding fails loudly rather than being read as a
     * liveness result. `docs/architecture/state-model.md:290` lists
     * *"Assertions that opcode read/assignment counts are capacity hints rather than effect contracts"*
     * among the audit's MISSING test cases, so this closes an audited gap.
     */
    @Test
    fun aBranchReadsItsOperandsAndAssignsNothing() {
        val twoRegister = CoreTester.execute(CLASS_NAME, "ifEqual()V", CoreState().apply {
            setRegisters(0, 1, "I", 1, 1, "I")
        })
        assertEquals(CompletionStatus.Complete, twoRegister.completion())
        assertTrue(
            twoRegister.wasReached(ADDRESS_IF),
            "the fixture still decodes and runs -- NOT the non-vacuity control, which is the non-empty " +
                "read set below, since registersRead/registersAssigned are both static queries",
        )
        assertEquals(setOf(0, 1), twoRegister.registersRead(ADDRESS_IF), "the 22t form reads BOTH operands")
        assertEquals(
            emptySet<Int>(), twoRegister.registersAssigned(ADDRESS_IF),
            "an `if` writes no register at all -- a different reason from filled-new-array's, whose " +
                "result goes to a Frame slot a Set<Int> structurally cannot name",
        )

        val compareToZero = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegisters(0, 1, "I")
        })
        assertEquals(
            setOf(0), compareToZero.registersRead(ADDRESS_IF),
            "the 21t form reads ONE: its right-hand side is the literal zero, not a register",
        )
        assertEquals(emptySet<Int>(), compareToZero.registersAssigned(ADDRESS_IF))
        assertTrue(
            twoRegister.registersRead(ADDRESS_IF).size > compareToZero.registersRead(ADDRESS_IF).size,
            "and the two forms differ, which a hardcoded set of either size would fail",
        )
    }

    /**
     * **Side effects are `NONE` on every row, declared and pinned.** A branch chooses a successor; it
     * writes no register and touches nothing outside its own frame. The mutation that proves this test
     * ADDS a declaration rather than removing one, because a spurious `STRONG` is the silent and
     * expensive direction: `methodSideEffectLevelOverExploredPaths` maxes over addresses, so one
     * `STRONG` branch would stop every constant fold in the method -- and most methods in a real APK
     * contain a branch.
     */
    @Test
    fun aBranchDeclaresNoSideEffectOnEveryRow() {
        val definite = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegisters(0, 0, "I")
        })
        assertEquals(SideEffectLevel.NONE, definite.sideEffectLevel(ADDRESS_IF), "a decided branch mutates nothing")
        val trusted = definite.trusted()
        assertNotNull(trusted, "nothing threw and nothing aborted")
        assertEquals(
            SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
            "and the whole-method query a strategy actually reads agrees",
        )

        val forked = CoreTester.execute(CLASS_NAME, "ifEqualZero()V", CoreState().apply {
            setRegister(0, Value.Unknown(INT))
        })
        assertEquals(
            SideEffectLevel.NONE, forked.sideEffectLevel(ADDRESS_IF),
            "and an AMBIGUOUS predicate is still not an unmodellable write -- it is two successors",
        )

        val reference = CoreTester.execute(CLASS_NAME, "ifNotEqualZero()V", CoreState().apply {
            setRegisters(0, byteArrayOf(0x1), "[B")
        })
        assertEquals(SideEffectLevel.NONE, reference.sideEffectLevel(ADDRESS_IF), "nor is a pointer comparison")
    }

    /**
     * **`(Object) null == (int) 0`, in BOTH operand orders** -- and the oracle gets this wrong on
     * **verified** input, which is registry disagreement D2. `null` is not a `Number`/`Boolean`/`Char`,
     * so the oracle's numeric branch misses and control reaches `lhs.raw === rhs.raw`
     * (`opcode/IfOp.kt:66`): `null === Integer(0)` is `false`, so its `if-eq` answers **false**. [BC]
     * says the bits are identical, so the right answer is **true**. `if-eq vNull, vInt` verifies (a
     * literal zero is `kZero`, which `IsBooleanTypes` makes integral AND `IsReferenceTypes` makes a
     * reference), so this is a fidelity requirement rather than a choice for junk input.
     *
     * Both orders, because the whole reason [ART]'s rule looks half-verified is that its first branch
     * tests only vA.
     */
    @Test
    fun aNullReferenceAndAnIntegralZeroAreEqualBecauseTheirBitsAre() {
        val nullFirst = CoreState().apply { setRegisters(0, null, "Ljava/lang/String;", 1, 0, "I") }
        CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", nullFirst, IF_TRUE_VISITATIONS)
        val zeroFirst = CoreState().apply { setRegisters(0, 0, "I", 1, null, "Ljava/lang/String;") }
        CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", zeroFirst, IF_TRUE_VISITATIONS)
        // ...and exactly one successor either way: this is a DECIDED predicate, not an ambiguous one,
        // which the IF_TRUE set above already implies and this states directly.
        val graph = CoreTester.execute(CLASS_NAME, "ifEqual()V", nullFirst)
        assertEquals(listOf(ADDRESS_RETURN), graph.nodePile(ADDRESS_IF).single().children.map { it.address })
    }

    /**
     * **A provable ZERO against a live pointer is decided, in BOTH operand orders** — the row that makes
     * `if-nez vRef` definite instead of a fork, asserted for the two-register form where the corpus only
     * covers the `*z` one.
     *
     * **This closes a gap the review measured: deleting the FIRST of row 4's two mirrored lines killed 0
     * of 921**, while deleting the second killed 2 (`testIfObjectNotEqualZero`,
     * `testIfPrimitiveArrayNotEqualZero` — both `*z`, so both exercise only the pointer-on-the-left
     * order). Asymmetric coverage is worse than none, because the covered half reads as proof of the
     * whole row. With the first line gone, `if-eq v0, v1` with a provable zero in v0 and a live pointer
     * in v1 falls through to the fork row and **invents an infeasible arm** — the mirror defect this plan
     * family has shipped three times, and the one this row exists to prevent.
     *
     * `if-ne` rather than `if-eq` on purpose: a decided `if-ne` here is TRUE, so the visitation set is
     * `[0, 3]`, which a spurious fork breaks loudly. A decided `if-eq` would be FALSE, whose `[0, 2, 3]`
     * is indistinguishable from a fork (see the class KDoc) and would need the arity assertion to carry
     * the whole claim on its own.
     *
     * Both spellings of the zero are covered, because [org.cf.smalivm.core.provableInt] deliberately
     * cannot tell them apart: an integral `Known(0, "I")` and a reference `Known(null, T)` are both
     * `Exactly(0)`, which is [BC]'s *"`(Object) null == (int) 0`"* expressed in the lattice.
     */
    @Test
    fun aProvableZeroAgainstALivePointerIsDecidedInBothOperandOrders() {
        val cases = mapOf(
            "integral zero, then pointer" to CoreState().apply {
                setRegisters(0, 0, "I", 1, "live", "Ljava/lang/String;")
            },
            "pointer, then integral zero" to CoreState().apply {
                setRegisters(0, "live", "Ljava/lang/String;", 1, 0, "I")
            },
            "null reference, then pointer" to CoreState().apply {
                setRegisters(0, null, "Ljava/lang/String;", 1, "live", "Ljava/lang/String;")
            },
            "pointer, then null reference" to CoreState().apply {
                setRegisters(0, "live", "Ljava/lang/String;", 1, null, "Ljava/lang/String;")
            },
        )
        for ((label, seed) in cases) {
            CoreTester.testVisitation(CLASS_NAME, "ifNotEqual()V", seed, IF_TRUE_VISITATIONS)
            assertEquals(
                1, CoreTester.execute(CLASS_NAME, "ifNotEqual()V", seed).nodePile(ADDRESS_IF).single().children.size,
                "$label: zero versus a live pointer is DECIDED unequal, so exactly one successor",
            )
        }
    }

    /**
     * **Identical references decide ALL SIX comparisons, including the ordering ones.** If two registers
     * hold the same pointer then `cmp == 0`, so `if-ge`/`if-le` are provably **true** and
     * `if-lt`/`if-gt` provably **false**. Forking those would invent a live branch -- the mirror defect
     * this plan family shipped three times -- and an earlier revision of this task's table did exactly
     * that by routing only `EQ`/`NE` through the identity row and leaving the other four to fall into
     * the fork.
     */
    @Test
    fun identicalReferencesDecideEveryComparisonAndNotOnlyEquality() {
        val obj = "one object, two registers"
        val same = { CoreState().apply { setRegisters(0, obj, "Ljava/lang/String;", 1, obj, "Ljava/lang/String;") } }
        CoreTester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", same(), IF_TRUE_VISITATIONS)
        CoreTester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", same(), IF_TRUE_VISITATIONS)
        CoreTester.testVisitation(CLASS_NAME, "ifLessThan()V", same(), IF_FALSE_VISITATIONS)
        CoreTester.testVisitation(CLASS_NAME, "ifGreaterThan()V", same(), IF_FALSE_VISITATIONS)
        // The two FALSE rows above are exactly the ones an exact-set comparison cannot tell from a fork
        // (see the class KDoc), so they get the arity assertion the set cannot make.
        for (method in listOf("ifLessThan()V", "ifGreaterThan()V")) {
            val graph = CoreTester.execute(CLASS_NAME, method, same())
            assertEquals(
                1, graph.nodePile(ADDRESS_IF).single().children.size,
                "$method over one pointer is DECIDED, so it must not fork",
            )
        }
    }

    /**
     * **The ENGINE CHOICES, made here rather than fallen into, and both fork.** Neither input is
     * verifier-valid (`if-lt` and friends are integral-only, `if-eq` wants both refs or both integral),
     * so there is no runtime behavior to match and spec §2.2 decides. The oracle answers **both**
     * definitely, by applying `lhs.raw === rhs.raw` for *every* comparison whenever either raw is
     * non-numeric (`opcode/IfOp.kt:56-58`, `:65-67`), which yields `cmp ∈ {0, 1}`:
     *
     *  - **ordering over two DISTINCT references**: the oracle says `if-lt` is definitely false. But
     *    two pointers' relative magnitudes are not something the lattice holds, so deciding it drops a
     *    feasible arm. Forks.
     *  - **a live pointer against a NON-ZERO integral**: the oracle says definitely unequal. A pointer
     *    is provably non-zero, which decides a comparison against `0` and nothing else -- it is not
     *    provably different from `5`. Forks. (Against zero it does NOT fork; that is
     *    `testIfObjectNotEqualZero`, a different row and a SPEC-backed one.)
     *
     * Recorded as VERIFIER-tier choices in `docs/DALVIK-SEMANTICS.md`'s *"Choices this plan makes"*
     * table, which names this test. **No migrated expectation and no differential fixture observes
     * either** -- measured: `CompareObjectReferences` uses only `if-eq`/`if-ne`/`if-eqz`/`if-nez`, and no
     * corpus seed pairs a reference with a non-zero primitive -- so there is nothing for an
     * `IntendedDivergence` entry to witness, and this test is the pin instead.
     */
    @Test
    fun anUnprovableComparisonOverAPointerForksInsteadOfBeingDecidedByIdentity() {
        val twoStrings = CoreState().apply {
            setRegisters(0, "a", "Ljava/lang/String;", 1, StringBuilder("b").toString(), "Ljava/lang/String;")
        }
        CoreTester.testVisitation(CLASS_NAME, "ifLessThan()V", twoStrings, IF_ALL_VISITATIONS)
        val ordering = CoreTester.execute(CLASS_NAME, "ifLessThan()V", twoStrings)
        assertEquals(
            2, ordering.nodePile(ADDRESS_IF).single().children.size,
            "an ordering question over two pointers is unprovable, and the visitation set cannot say so",
        )

        val pointerAgainstFive = CoreState().apply {
            setRegisters(0, "a", "Ljava/lang/String;", 1, 5, "I")
        }
        val mixed = CoreTester.execute(CLASS_NAME, "ifEqual()V", pointerAgainstFive)
        assertEquals(CompletionStatus.Complete, mixed.completion())
        assertEquals(
            2, mixed.nodePile(ADDRESS_IF).single().children.size,
            "a pointer is provably non-zero, not provably different from 5",
        )
    }

    /**
     * **The gate that keeps `sameReference` away from a boxed primitive.** `sameReference` is a pure
     * identity test and cannot tell a pointer from a `Float` box, so ungated it answers `false` for two
     * distinct boxes of `1.0f` -- and a row that read that as "definitely unequal" would fold a
     * confident wrong constant for exactly the input Task 3 proved unprovable, silently reversing the
     * thirteen moved expectations above. [IfOp] therefore consults it only when BOTH operands are
     * provably live pointers.
     *
     * The `1000` row is the same trap in the integral domain and is guarded by a different mechanism --
     * the exactly-known row coming first -- since `Integer.valueOf` caches only -128..127, so
     * `Known(1000) === Known(1000)` is `false` while `1000 == 1000` is plainly true.
     */
    @Test
    fun aFloatRawIsNotAPointerSoIdentityNeverDecidesIt() {
        val twoFloatBoxes = CoreState().apply { setRegisters(0, 1f, "F", 1, 1f, "F") }
        val floats = CoreTester.execute(CLASS_NAME, "ifEqual()V", twoFloatBoxes)
        assertEquals(
            2, floats.nodePile(ADDRESS_IF).single().children.size,
            "two Float boxes are not two pointers; their 32 bits are simply not provable",
        )

        val twoBigIntegers = CoreState().apply { setRegisters(0, 1000, "I", 1, 1000, "I") }
        CoreTester.testVisitation(CLASS_NAME, "ifEqual()V", twoBigIntegers, IF_TRUE_VISITATIONS)
    }

    /**
     * **ONE ADDRESS, TWO FRAMES** -- the value-ambiguity fork's own shape, produced by a real `if` whose
     * branch target IS its fall-through. `if-eqz v0, :next` where `:next` is the following instruction
     * is an ordinary opaque-predicate emission, and `switch` produces the same shape whenever a case
     * offset is `+3`.
     *
     * `CARRY-FORWARD.md`'s Plan 8 item 1 said a `route`-style single-address list could not express
     * *"one address with N frames"*. It can: `commit` builds one [org.cf.smalivm.core.ExecutionNode] per
     * successor spec with its own `frame.copy()`, `addToPile` appends to an identity-keyed list, and
     * nothing dedupes. Task 12 must correct that item to say so.
     *
     * This real-opcode witness owns the claim; the old scripted scheduler twin was removed during the
     * Task 51 corpus migration.
     */
    @Test
    fun aBranchWhoseTargetIsItsFallThroughBuildsTwoNodesAtThatOneAddress() {
        val graph = CoreTester.execute(CLASS_NAME, "ifEqualZeroToFallThrough(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion(), "both arms end in the same return-void")
        assertEquals(
            listOf(2, 2), graph.nodePile(0).single().children.map { it.address },
            "both transitions name address 2, and both produce a node -- nothing dedupes them",
        )
        assertEquals(2, graph.nodePile(2).size, "so the join address holds TWO nodes, not one")
        assertEquals(2, graph.outcomes.size, "two ended paths")
        assertTrue(
            graph.nodePile(2).let { it[0] !== it[1] && it[0].frame !== it[1].frame },
            "and two INDEPENDENT frames: a shared one is the audit's shared-mutated-state defect",
        )
    }

    /**
     * **THE POINT OF THE FORK: reconciliation, in both directions.** An ambiguous predicate explores
     * both arms and per-address consensus joins what they found -- so a consumer may fold a value only
     * when every explored path agrees **and** the exploration completed (spec §6.5, §9).
     *
     * The disagreeing half is the one the corpus cannot show at all: every `if` fixture in it has a
     * `nop` on the fall-through, and a `nop` assigns nothing on either arm, so both nodes at the join
     * hold identical frames and the join is trivially idempotent. `ifEqualZeroSkippingAnAssignment`
     * assigns on one arm only, so the two nodes at the join genuinely disagree.
     */
    @Test
    fun aForkIsReconciledByPerAddressConsensusAndNotByEitherArm() {
        val disagree = CoreTester.execute(CLASS_NAME, "ifEqualZeroSkippingAnAssignment(I)V")
        assertEquals(CompletionStatus.Complete, disagree.completion())
        assertEquals(2, disagree.nodePile(3).size, "both arms reach the join")
        assertEquals(
            setOf(Value.Known(1, INT), Value.Top), disagree.registerValuesOverExploredPaths(3, 0),
            "one arm ran the const, the other skipped it -- two DIFFERENT values at one address",
        )
        assertEquals(
            Value.Top, disagree.consensusOverExploredPaths(3, 0),
            "so consensus is their join, never whichever arm was explored first",
        )
        assertEquals(
            Value.Top, disagree.trusted()!!.terminatingConsensus(0),
            "and the trusted view a consumer reads refuses to name a value it cannot prove",
        )

        val agree = CoreTester.execute(CLASS_NAME, "ifEqualZeroAssigningOneOnBothArms(I)V")
        assertEquals(CompletionStatus.Complete, agree.completion())
        assertEquals(
            Value.Known(1, INT), agree.trusted()!!.terminatingConsensus(0),
            "when every explored path agrees, an UNDECIDED predicate still yields a foldable value -- " +
                "which is the entire deobfuscation payoff of exploring both arms",
        )
    }

    /** Nine loop-free unknown diamonds retain all 512 paths because visits belong to each path. */
    @Test
    fun nineUnknownDiamondsKeepAllLoopFreeSiblingPaths() {
        val bounded = CoreTester.execute(CLASS_NAME, "nineUnknownDiamonds(I)V")
        assertEquals(
            CompletionStatus.Complete, bounded.completion(),
            "each of 512 paths reaches address 27 once with a path-local budget",
        )
        assertNotNull(bounded.trusted())
        assertEquals(512, bounded.outcomes.size, "one Outcome per ended path, whichever way it ended")
        assertEquals(
            0, bounded.outcomes.count { it == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "sibling reconvergence is not a loop",
        )

        val unbounded = CoreTester.execute(
            CLASS_NAME, "nineUnknownDiamonds(I)V", CoreState(),
            limits = Limits(maxAddressVisits = 2_000),
        )
        assertEquals(
            CompletionStatus.Complete, unbounded.completion(),
            "nothing is wrong with the method -- 512 paths is simply more than the budget allows",
        )
        assertEquals(512, unbounded.outcomes.size, "2^9 paths, no loop, one op per diamond")
        assertEquals(512, unbounded.nodePile(27).size, "all of them at the one join address")
    }

    /**
     * **TWO `const-class` INSTRUCTIONS ON ONE CLASS YIELD THE SAME REFERENCE, so this `if-eq` has ONE
     * successor.** [VER] says so directly, as a parenthetical in its monitor-verification section:
     * *"the verifier does not attempt to identify previously-made copies, track loads and stores through
     * fields, or recognize identical constant values (for example, **the result values from two
     * `const-class` instructions on the same class will be the same reference**, but the verifier
     * doesn't recognize this)"*. The verifier declining to recognise it is what makes it a **runtime**
     * fact this engine may lean on rather than a VERIFIER-tier choice.
     *
     * **Landed in Plan 8 Task 10, when `const-class` arrived** -- this test flipped from its prior
     * `Partial(UNSUPPORTED_OP)` shape (the honest statement while the opcode was unsupported) to the arity
     * assertion below. Identity-stability came free rather than needing an interned `VirtualType` or a
     * per-session cache: `ClassIndex.classLoader` (a `SmaliClassLoader`) caches each defined class, so both
     * `const-class Lif_test;` resolve through `ClassIndex.loadClassConstant` to the SAME host `Class`
     * object, and `Provable.sameReference` sees them equal by host `===`.
     *
     * Written as a fixture rather than inferred from `===` happening to work, because whether two
     * `const-class` executions yield one host object is otherwise an untested detail of that cache, and
     * an untested assumption there silently converts a provable equality into a fork -- the
     * invents-a-live-branch defect, which this plan family has now shipped three times. The `nop` at 6 is
     * proved dead only because the `assertEquals(Complete, ...)` gate precedes the `wasReached` negative.
     */
    @Test
    fun twoConstClassResultsForOneClassMustBeOneReferenceAndNotAFork() {
        val graph = CoreTester.execute(CLASS_NAME, "ifEqualTwoConstClassesOfOneClass()V")
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "two const-class on one class are one reference, so the if-eq is provable and nothing truncates",
        )
        assertEquals(
            listOf(7), graph.nodePile(4).single().children.map { it.address },
            "the if-eq at 4 is provably taken -- ONE successor (the :same return at 7), not a fork",
        )
        assertFalse(
            graph.wasReached(6),
            "the fall-through nop at 6 is dead: gated on the Complete assertion above, so this negative " +
                "is a dead-code proof and not a truncation artifact",
        )
    }

    /**
     * **THE DECISION TABLE, EXHAUSTIVELY — the standing pin for the disjointness that replaced the
     * documented row order.**
     *
     * [IfOp]'s KDoc claims its four gates are pairwise disjoint, so no row can shadow another and the
     * order in the source is presentational. A one-off mutation measurement cannot enforce that: the
     * next gate someone adds (Task 6 is told to reuse `provableInt`; Tasks 8-9 add `castOutcome`
     * callers) could overlap an existing one, first-matching-row semantics would silently return, and
     * nothing would fail. **The review demonstrated the gap is real rather than theoretical: one of the
     * four gates could be deleted with a green suite.**
     *
     * So the invariant is not asserted, it is *pinned by the table it is a property of*. Every pair of
     * lattice representatives × all six comparisons, with the expected answer written as DATA:
     *
     *  - the per-pair verdict is a literal matrix below — that is the row-selection claim, i.e. the
     *    disjointness. Any new or widened gate that shadows another changes a cell;
     *  - the expansion from a verdict to the expected [Transition] list is written out here rather than
     *    computed through [IfComparison.holdsFor], so the test cannot agree with the implementation by
     *    construction.
     *
     * The representatives cover every [org.cf.smalivm.core.ProvableInt] case and every distinction the
     * pointer rows are allowed to make: a reference-shaped zero and an integral zero (indistinguishable
     * by design — [BC]'s *"`(Object) null == (int) 0`"*), a non-zero exact, the same host object under
     * two different tags (the tag is ignored), a distinct host object with EQUAL CONTENT (identity, not
     * `equals`), the same heap id under two tags, a different heap id, a `Float` raw, and an `Unknown`.
     */
    @Test
    fun everyPairOfLatticeRepresentativesDecidesTheSameWayForAllSixComparisons() {
        val sameHostObject = StringBuilder("content").toString()
        val equalContentButDistinct = StringBuilder("content").toString()
        // Sanity: the pair the identity rows turn on. If javac/Kotlin ever interned these into one
        // object the STR_A/STR_B cells below would be asserting the wrong thing, silently.
        assertTrue(sameHostObject == equalContentButDistinct, "equal content")
        assertTrue(sameHostObject !== equalContentButDistinct, "but NOT the same object")

        val string = VirtualType.of("Ljava/lang/String;")
        val obj = VirtualType.of("Ljava/lang/Object;")
        val representatives = listOf(
            "NULLREF" to Value.Known(null, string),
            "ZERO" to Value.Known(0, INT),
            "ONE" to Value.Known(1, INT),
            "STR_A" to Value.Known(sameHostObject, string),
            "STR_A_OTHER_TAG" to Value.Known(sameHostObject, obj),
            "STR_B_EQUAL_CONTENT" to Value.Known(equalContentButDistinct, string),
            "HEAP1" to Value.Known(HeapRef(1), VirtualType.of("[B")),
            "HEAP1_OTHER_TAG" to Value.Known(HeapRef(1), obj),
            "HEAP2" to Value.Known(HeapRef(2), VirtualType.of("[B")),
            "FLOAT" to Value.Known(1f, VirtualType.of("F")),
            "UNKNOWN" to Value.Unknown(INT),
        )
        // E = equal (cmp 0)   N = unequal but unordered   L = less   G = greater   F = unprovable, fork
        //           NULLREF ZERO ONE STR_A A_TAG STR_B HEAP1 H1_TAG HEAP2 FLOAT UNKNOWN
        val matrix = listOf(
            /* NULLREF         */ "EELNNNNNNFF",
            /* ZERO            */ "EELNNNNNNFF",
            /* ONE             */ "GGEFFFFFFFF",
            /* STR_A           */ "NNFEENNNNFF",
            /* STR_A_OTHER_TAG */ "NNFEENNNNFF",
            /* STR_B_EQUAL     */ "NNFNNENNNFF",
            /* HEAP1           */ "NNFNNNEENFF",
            /* HEAP1_OTHER_TAG */ "NNFNNNEENFF",
            /* HEAP2           */ "NNFNNNNNEFF",
            /* FLOAT           */ "FFFFFFFFFFF",
            /* UNKNOWN         */ "FFFFFFFFFFF",
        )
        assertEquals(representatives.size, matrix.size, "one matrix row per representative")
        assertTrue(matrix.all { it.length == representatives.size }, "and one cell per representative")

        val target = ADDRESS_RETURN
        val jump = listOf(OpArm.Jump(target))
        val fallThrough = listOf(OpArm.Continue())
        val fork = listOf(OpArm.Continue(), OpArm.Jump(target))
        // Written out, NOT derived from IfComparison.holdsFor, so this table is an independent statement
        // of what each verdict means for each of the twelve opcodes.
        val expansion = mapOf(
            'E' to mapOf(
                IfComparison.EQ to jump, IfComparison.NE to fallThrough,
                IfComparison.LT to fallThrough, IfComparison.GE to jump,
                IfComparison.GT to fallThrough, IfComparison.LE to jump,
            ),
            'N' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to fork, IfComparison.GE to fork,
                IfComparison.GT to fork, IfComparison.LE to fork,
            ),
            'L' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to jump, IfComparison.GE to fallThrough,
                IfComparison.GT to fallThrough, IfComparison.LE to jump,
            ),
            'G' to mapOf(
                IfComparison.EQ to fallThrough, IfComparison.NE to jump,
                IfComparison.LT to fallThrough, IfComparison.GE to jump,
                IfComparison.GT to jump, IfComparison.LE to fallThrough,
            ),
            'F' to IfComparison.entries.associateWith { fork },
        )

        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, "ifEqual()V")
        var cells = 0
        for ((lhsIndex, lhsEntry) in representatives.withIndex()) {
            for ((rhsIndex, rhsEntry) in representatives.withIndex()) {
                val verdict = matrix[lhsIndex][rhsIndex]
                val expectedPerComparison = expansion.getValue(verdict)
                for (comparison in IfComparison.entries) {
                    val frame = Frame.entryFrame(view)
                    frame.assign(0, lhsEntry.second)
                    frame.assign(1, rhsEntry.second)
                    val op = IfOp(
                        address = ADDRESS_IF, name = "if-${comparison.name.lowercase()}",
                        comparison = comparison, targetAddress = target, lhsRegister = 0, rhsRegister = 1,
                    )
                    assertEquals(
                        expectedPerComparison.getValue(comparison), op.execute(frame).arms,
                        "${lhsEntry.first} ${comparison.name} ${rhsEntry.first} (verdict '$verdict')",
                    )
                    cells++
                }
            }
        }
        assertEquals(representatives.size * representatives.size * 6, cells, "the whole cross product ran")
    }

    /**
     * The mockk change-detector's adjudication, recorded where the test used to be.
     *
     * `UnitTest.hasExpectedToStringValue` asserted exactly one thing -- `"if-ge r2, r4, :addr_0"` -- and
     * everything else in it was mock scaffolding. **Its `every { state.readRegister(...) }` stubs were
     * NOT dead**: the test called `op.execute(node)` before asserting and the oracle's `execute` reads
     * both registers, so the stubs fed a real execution (with `Value.wrap(0, "D")`, a `D`-tagged pair,
     * which makes it a Task 3 input class too). The real criticism is different and weaker: it
     * **asserted** only `toString` while **exercising** `execute`, so no wrong comparison could fail it.
     *
     * Adjudication, per the Plan 4 rule: mutate `IfOp.toString` and see whether a behavioral test dies.
     * **Measured: none does** -- nothing in the behavioral suite reads a rendered op -- so the rendering
     * is genuinely uncovered elsewhere and is kept, as a row in
     * `OpBuilderTest.opsRenderTheirOperandsInTheirDeclaredOrder` (one test per rendering shape across
     * all op families, which is where Plan 4's and Plan 7 Task 11's restorations live). Both `if`
     * shapes are pinned there, since the `*z` form renders one operand and the `22t` form two.
     *
     * This method is documentation, not a test: it exists so the adjudication is findable from the file
     * that inherited the test. It is not annotated `@Test`.
     */
    @Suppress("unused")
    private fun mockkAdjudication() = Unit
}
