package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Outcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * `array-length` (op 21, format 12x = ONE code unit). Migrated from
 * `org.cf.smalivm.opcode.ArrayLengthOpTest` -- all ten tests, keeping the `@Nested` structure and its
 * `@DisplayName`s -- plus the unprovable-array fork the corpus never covered.
 *
 * **SPEC** [BC]: *"Store in the given destination register the length of the indicated array, in
 * entries"* -- and note it reads the array OBJECT, not any static type, which is why the loosely-tagged
 * seeds below (Strings in a `[I`) are correct rather than sloppy. A null array throws
 * `NullPointerException`: **INHERITED**, [JVMS] §6.5 `arraylength`.
 */
class ArrayLengthOpTest {
    companion object {
        private const val CLASS_NAME = "Larray_length_test;"
    }

    @Nested
    @DisplayName("with object arrays")
    inner class ObjectArrays {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
            expected = CoreState()
        }

        @Test
        fun canGetLengthForEmptyIntegerArray() {
            initial.setRegisters(0, arrayOf<Int>(), "[I")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForIntegerArray() {
            initial.setRegisters(0, arrayOf(1, 2, 3), "[I")
            expected.setRegisters(0, 3, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        /**
         * Pins `buildArrayLength`'s OPERAND ORDER, which nothing else in the suite can see.
         *
         * Bug class: every `array-length` in the fixture tree is `v0, v0`, so `destRegister` and
         * `arrayRegister` collapse to one number and swapping `registers.registerA`/`registerB` in
         * `OpBuilder.buildArrayLength` leaves the entire suite green. `12x`'s operands are ordered
         * (`B|A|op`, A the destination) and real code emits `array-length v1, v2` constantly.
         *
         * Discriminating BECAUSE of [ArrayLengthOp]'s third case: swapped, the op reads v1 -- a
         * `Known` non-array, hence provably non-null -- and correctly answers `Unknown(I)` without
         * forking, writing it to v2. So an inverted decoder does not crash or throw; it silently
         * returns an unknown length and leaves the array register clobbered. Both expectations below
         * fail, and the one on v2 is why the array register is asserted at all.
         *
         * `intArrayOf`, not the `arrayOf(1, 2, 3)` its siblings use: this is the first test in the file
         * to assert the ARRAY register's surviving value, so it is the first to reach `CoreTester`'s
         * runtime-type-assignable-to-declared-type check. `arrayOf(1, 2, 3)` is a host `Integer[]`, which
         * is not assignable to `[I` and fails there -- correctly, since primitive arrays are invariant
         * ([JLS] §4.10.3). The siblings assert only the int destination, so their loose `[I` tag on an
         * `Integer[]` is never checked; that looseness is deliberate corpus parity and is not touched here.
         */
        @Test
        fun theDestinationAndArrayOperandsAreNotInterchangeable() {
            initial.setRegisters(0, 0x1177, "I", 1, 0x2288, "I", 2, intArrayOf(1, 2, 3), "[I")
            expected.setRegisters(0, 0x1177, "I", 1, 3, "I", 2, intArrayOf(1, 2, 3), "[I")
            CoreTester.test(CLASS_NAME, "getLengthIntoADifferentRegister()V", initial, expected)
        }

        @Test
        fun canGetLengthForStringArray() {
            // The register tag lies on purpose (a String[] tagged `[I`), and the op must not care: it
            // reads the OBJECT's length. Object over tag -- see `HeapObject.TrackedArray`'s KDoc.
            initial.setRegisters(0, arrayOf("isn't", "this", "where"), "[I")
            expected.setRegisters(0, 3, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForUnknownValueOfIntegerType() {
            initial.setRegisters(0, CoreState.UNKNOWN, "[Ljava/lang/Integer;")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForUnknownValueOfPrimitiveType() {
            // `getLength()V` has NO try block, so the NPE this unknown array forks ESCAPES and the run's
            // outcomes contain a `Threw`. The run is still `Complete` (a guest throw is not a truncation)
            // and `terminatingConsensus` reads RETURNING paths only, so the expectation below is exactly
            // the normal path's value. The fork is correct; do not "fix" it to make this test pass.
            initial.setRegisters(0, CoreState.UNKNOWN, "[I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        /**
         * Bug class: forking the NPE for an array the engine PROVED non-null -- the mirror of the row
         * below, and the half "always fork" would get wrong. A tracked array is `Known(HeapRef)`, so the
         * throw is INFEASIBLE, and exploring it would tell a deobfuscator a `catch (NullPointerException)`
         * is reachable when Dalvik can never enter it (the over-approximating wrong answer
         * `Fixtures`' binary-div-int-unknown-dividend entry exists to keep honest).
         *
         * Every other test in this class reads the RETURNING consensus, which an extra escaping throw does
         * not change at all -- so without this the no-fork half of the op's contract has nothing to kill a
         * mutation that added one.
         */
        @Test
        fun aTrackedArrayIsProvablyNonNullSoNoNullPointerExceptionIsForked() {
            val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
                setRegisters(0, arrayOf(1, 2, 3), "[I")
            })
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor")
            assertTrue(graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(), "nothing may throw")
            // The result's WIDTH, while a graph is here: `array-length`'s result is narrow `I` always, so a
            // decode-time `isWide = true` would claim the next register belongs to the pair and offer a live
            // register as scratch space (see Op.destinationRegisters' KDoc).
            assertEquals(setOf(0), graph.registersAssigned(0), "a narrow I result owns one register")
        }

        /**
         * Bug class: collapsing "not a tracked array" into "may be null" and forking an NPE for a value the
         * engine just proved non-null. `frame.arrayAt(ref)` also answers null for a `HeapRef` pointing at a
         * `GuestThrowable`, so a naive `tracked == null` test hits this same branch for a definitely-non-null
         * value -- which is why the two must not share a fork.
         *
         * **What makes it provably non-null is the TYPE, not the value** -- corrected in Plan 7 Task 12's fix
         * round, which found that a primitive-typed register reads as null on ART whatever its value. This
         * seed is a `Ljava/lang/String;`, i.e. a genuine reference with a non-null raw, so the claim holds;
         * "a Known non-zero raw is provably non-null" would not.
         *
         * **VERIFIER** -> engine's choice: the DEX verifier would have rejected a non-array here, so there
         * is no Dalvik fact to reproduce (past its null check, ART's `ARRAY_LENGTH` does `AsArray()`
         * unchecked and reads whatever word sits at the length offset -- undefined, not a behavior to
         * copy). `Unknown(I)` is the total
         * answer, justified by spec §2.2. **Diverges from the oracle**, which logs the case and keeps its
         * exception child, i.e. over-approximates; no fixture exercised it there, so this is noted rather
         * than registered as a divergence.
         */
        @Test
        fun aKnownNonArrayIsProvablyNonNullSoItsLengthIsUnknownAndNothingThrows() {
            initial.setRegisters(0, "not an array at all", "Ljava/lang/String;")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
            val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
                setRegisters(0, "not an array at all", "Ljava/lang/String;")
            })
            assertTrue(
                graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
                "a Known REFERENCE with a non-null raw is provably non-null, so the NPE is infeasible",
            )
        }

        /**
         * Bug class: NOT forking the NPE for an array the engine cannot prove non-null. `Unknown([I)` may
         * be null, so the throw is feasible, and dropping it would leave an unexplored feasible path on a
         * run reporting Complete -- the worst outcome this engine can have (spec §2.2). The oracle forks
         * here too, so this is parity as well as soundness.
         */
        @Test
        fun anUnprovableArrayForksTheNullPointerExceptionAsWellAsTheLengthRead() {
            val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
                setRegisters(0, CoreState.UNKNOWN, "[I")
            })
            assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is not a truncation")
            // Two INDEPENDENTLY falsifiable lines rather than one assertion on their sum. `children + threw
            // == 2` is satisfied by 2+0, 1+1 and 0+2 alike, so it asserts nothing the line below it does not
            // -- and if a later edit dropped the second line, the first would pass on a completely wrong
            // graph shape.
            assertEquals(1, graph.nodePile(0).first().children.size, "exactly one normal successor")
            assertEquals(1, graph.outcomes.filterIsInstance<Outcome.Threw>().size, "and exactly one escaping throw")
        }
    }

    @Nested
    @DisplayName("with primitive arrays")
    inner class PrimitiveArrays {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
            expected = CoreState()
        }

        @Test
        fun canGetLengthForEmptyIntArray() {
            initial.setRegisters(0, intArrayOf(), "[I")
            expected.setRegisters(0, 0, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForIntArray() {
            initial.setRegisters(0, intArrayOf(1, 2, 3), "[I")
            expected.setRegisters(0, 3, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForLongArray() {
            // `[J` is an ARRAY descriptor, so `VirtualType.isWide` is false and `Frame.assign` reserves
            // no high half -- unlike a bare `J` tag on an array register, which `CoreState.HostArray`'s
            // KDoc rejects as a category error.
            initial.setRegisters(0, longArrayOf(1, 2, 3, 4), "[J")
            expected.setRegisters(0, 4, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForShortArray() {
            initial.setRegisters(0, shortArrayOf(1, 2), "[S")
            expected.setRegisters(0, 2, "I")
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun nullArrayThrowsExpectedException() {
            // `testCaught`, NOT `testThrows`: `getLengthWithCatch()V` (array_length_test.smali:12-30)
            // declares a `Ljava/lang/Exception;` handler around the `array-length`, so the NPE is CAUGHT,
            // `move-exception v0` binds it, and the run returns -- which produces no `Outcome.Threw` at
            // all. This is the shape the corpus's `testSimpleException` asserted.
            //
            // `wasReached(1)` is the right address here: `array-length` is format 12x = ONE code unit, so
            // the `nop` really is at 1 (unlike `aget`/`aput`, which are 23x = 2).
            //
            // The MESSAGE is load-bearing and unusually well grounded for a message in this family: the
            // corpus asserts it, the oracle declares the same string (`opcode/ArrayLengthOp.kt:19`), and
            // [ART] emits exactly it (`runtime/common_throws.cc:639-667`). CORPUS + ART.
            initial.setRegisters(0, null, "[S")
            CoreTester.testCaught(
                CLASS_NAME, "getLengthWithCatch()V", initial,
                exceptionType = "Ljava/lang/NullPointerException;",
                message = "Attempt to get length of null array",
                caughtInRegister = 0,
                unreachedAddress = 1,
            )
        }

        /**
         * The other half of Dalvik's null, which `Value.isNull` cannot see. **SPEC** ([BC]: *"In terms of
         * bitwise representation, `(Object) null == (int) 0`"*), so `const/4 v0, 0x0` followed by
         * `array-length` is a DEFINITE NPE -- but the register holds `Known(0, "I")`, whose raw is not
         * `null`, so without `Value.isDalvikNull` it would fall through to the provably-non-null branch
         * and a definite throw would be silently dropped. Not in the corpus; added here because Task 9's
         * rule 1 and Task 12 consume the same predicate.
         */
        @Test
        fun aKnownIntegralZeroIsDalviksNullAndThrowsTheSameNullPointerException() {
            initial.setRegisters(0, 0, "I")
            CoreTester.testCaught(
                CLASS_NAME, "getLengthWithCatch()V", initial,
                exceptionType = "Ljava/lang/NullPointerException;",
                message = "Attempt to get length of null array",
                caughtInRegister = 0,
                unreachedAddress = 1,
            )
        }

        /**
         * The SAME definite NPE for a NON-zero primitive register, which is the half the zero-only predicate
         * dropped.
         *
         * **The reason a primitive-typed register is null has nothing to do with its VALUE**, which is what the
         * `isDalvikNull`-only predicate got wrong until Plan 7 Task 12's fix round. ART fetches an object operand
         * with `GetVRegReference`, `ShadowFrame` keeps a PARALLEL reference array, and every primitive write
         * clears the matching slot -- `SetVReg`/`SetVRegFloat`/`SetVRegLong`/`SetVRegDouble` all end in
         * `References()[i].Clear()` (`shadow_frame.h:190-228`), and ART states the invariant in its own prose at
         * `:401-408`: *"when a primitive is stored in vX, the second (reference) part of the array will be
         * null"*. **[ART]'s SWITCH interpreter**, and stable across releases (`android14-release` is
         * identical). So `const/4 v0, 0x5`
         * is exactly as null as `const/4 v0, 0x0`, and testing only for zero DROPPED a definite NPE -- an
         * unexplored feasible path on a `Complete` run, spec §2.2's worst outcome. See
         * [org.cf.smalivm.core.readsAsNullReference] for the full chain.
         *
         * **TIER, corrected 2026-08-04: this row is VERIFIER, not [ART].** ART's *default* interpreter is
         * `nterp`, which reads the same operand out of the RAW dex-register array
         * (`mterp/x86_64ng/array.S:111-123` for `array-length`) and dereferences a non-zero value as an
         * object pointer -- so the two tiers CONTRADICT each other on exactly the value this test seeds, and
         * `method_verifier.cc:3174-3177` refuses to link it besides. The **zero** spellings (the sibling
         * test) are corroborated by BOTH tiers, which is a strengthening. **This expectation does not
         * change**: a definite NPE is the sound direction and matches the switch tier exactly. Only the
         * citation changes. See `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*.
         */
        @Test
        fun aKnownNonZeroPrimitiveIsAlsoDalviksNullAndThrowsTheSameNullPointerException() {
            initial.setRegisters(0, 5, "I")
            CoreTester.testCaught(
                CLASS_NAME, "getLengthWithCatch()V", initial,
                exceptionType = "Ljava/lang/NullPointerException;",
                message = "Attempt to get length of null array",
                caughtInRegister = 0,
                unreachedAddress = 1,
            )
        }
    }
}
