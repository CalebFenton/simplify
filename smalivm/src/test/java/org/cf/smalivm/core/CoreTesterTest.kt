package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The migration bridge's own guards. 156 tests in this plan and ~300 in later ones assert through
 * [CoreTester]'s facade, so a hole here is a *silent* hole in all of them at once -- which is why
 * these exist even though the facade is "just a test harness".
 *
 * Every test below pins a way the facade could pass while proving nothing. Oracle for each is spec §9
 * (consensus is trustworthy only when the relevant paths were explored) plus the fixture's own Dalvik
 * semantics; no expected value is read back from the engine.
 *
 * `spinForever()V` is the truncation fixture rather than the malformed-operand refusal method,
 * deliberately: an unbounded backward `goto` exhausts `Limits`' DEFAULT per-address budget, so these
 * tests pin the gate against truncation *in general* and do not have to be re-pointed every time the
 * capability ratchets are retired or changed.
 */
class CoreTesterTest {

    @Test
    fun testRefusesToReadAnyConsensusFromATruncatedRun() {
        // Bug class, and the worst one this harness can have: `terminatingConsensus` on a Partial run
        // still answers, so a migrated test whose fixture stops early would assert against whatever
        // the truncated walk happened to produce -- or, if the run returned on no path at all, against
        // Value.Top -- and PASS. Then 156 tests would be recording "the engine agrees with Dalvik" from
        // an exploration that never finished.
        val expected = CoreState().apply { setRegisters(0, 0, "I") }
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(SPINE, "spinForever()V", CoreState(), expected)
        }
        assertTrue(
            failure.message!!.contains("exploration must complete before any consensus may be trusted"),
            "must fail on the completion gate, not on a value comparison: ${failure.message}",
        )
    }

    @Test
    fun testRefusesAnExpectedSlotItCannotObserve() {
        // Bug class: no query on ExecutionGraph or TrustedGraphView exposes a Frame's result/caught
        // slot, so an `expected.setResult(...)` -- the reflex for anyone porting a test that used the
        // old engine's RESULT_REGISTER -- would assert NOTHING and pass. Refusing it outright is the
        // only way the omission is visible to whoever writes it.
        val expected = CoreState().apply { setResult(42, "I") }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
        }
        assertTrue(failure.message!!.contains("no graph query exposes them"), failure.message!!)
    }

    @Test
    fun testRefusesAnExpectationThatNamesNoRegister() {
        // The same vacuity class as the slot refusal above and the likelier accident of the two: with an
        // empty `expected`, `test` asserts only that the run completed. A migration that forgot to fill
        // `expected`, or filled a different CoreState instance than the one it passed, would then pass
        // while comparing no value at all -- and 156 tests in this plan alone flow through this function.
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), CoreState())
        }
        assertTrue(failure.message!!.contains("at least one expected register"), failure.message!!)
    }

    @Test
    fun testRefusesTheCorpusNegativeResultRegisterIdiom() {
        // Bug class: the SAME unobservable-slot accident as the test above, by the idiom the unmigrated
        // corpus actually uses -- `expected.setRegisters(ExecutionState.RESULT_REGISTER, ...)`, a
        // negative index into the old engine's collapsed register map (nine live call sites remain, all in
        // `opcode/InvokeOpTest`, heading into Plan 9; the other four were in `opcode/FilledNewArrayTest`,
        // which Plan 7 Task 11 migrated and deleted -- this guard is what made that migration assert the
        // `move-result-object` register instead). The
        // `setResult` guard cannot see it: it lands in `registers[-1]`, out of range, where `valueIn`
        // answers `Value.Top` -- so the migrator's failure was `expected Known(42, I) but was Top`,
        // which points at the engine rather than at the port.
        val expected = CoreState().apply { setRegisters(-1, 42, "I") }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
        }
        assertTrue(failure.message!!.contains("RESULT_REGISTER"), failure.message!!)
    }

    @Test
    fun testRefusesAnExpectationAliasedToItsOwnSeed() {
        // Bug class: the vacuity the non-empty guard cannot see, because the aliased state is full.
        // Every assertion degenerates to "the seed survived", which no engine defect can break for a
        // register the method never writes -- `throwCaught` writes only v0 (its `move-exception`), so
        // the seeded v1 below is echoed back unchanged and the comparison is a tautology. Without the
        // guard this call PASSES; with it, it names the mistake.
        val seed = CoreState().apply {
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;", seed, seed)
        }
        assertTrue(failure.message!!.contains("both seed and expectation"), failure.message!!)
    }

    @Test
    fun testRefusesAnExpectedRegisterThatDoesNotExist() {
        // Bug class: a mistyped register number passing vacuously. `ExecutionGraph.valueIn` answers
        // `Value.Top` for an out-of-range register (deliberately -- a query must never throw during
        // traversal), so an expectation OF `Top` -- "this register is unassigned", the shape a liveness
        // migration writes -- compares Top against Top and passes no matter which register it names.
        // `returnFive()I` is `.registers 1`, so 999 is the wrong number by any reading.
        val expected = CoreState().apply { setRegister(999, Value.Top) }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
        }
        assertTrue(failure.message!!.contains("outside"), failure.message!!)
    }

    @Test
    fun testThrowsRefusesToReadAThrowFromATruncatedRun() {
        // Bug class: the one facade entry point whose own claim ("some path threw X") survives
        // truncation, so it looks safe ungated -- but callers pair it with negative claims that do not
        // survive. A later fixture that throws on one path and hits a limit on the other would pass
        // while `completion()` was Partial. Gated for that reason; see `testThrows`'s KDoc.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testThrows(
                SPINE, "spinForever()V", CoreState(),
                exceptionType = "Ljava/lang/ArithmeticException;",
            )
        }
        assertTrue(
            failure.message!!.contains("the other half needs a finished run"),
            "must fail on the completion gate, not on the absent throw: ${failure.message}",
        )
    }

    @Test
    fun testVisitationReportsExactlyTheAddressesThatRan() {
        // `gotoSkip()I` is @0 const/4, @1 goto, @2 const/4, @3 return -- four one-unit instructions,
        // derived from the fixture. The jumped-over const at @2 is the discriminating address: a
        // facade that reported the STATIC address set instead of the executed one would include it.
        CoreTester.testVisitation(SPINE, "gotoSkip()I", CoreState(), intArrayOf(0, 1, 3))
    }

    // NOTE: `testVisitation`'s truncated-run guard has no test of its own here on purpose.
    // `assertVisited` is now a one-line alias for `testVisitation`, so
    // `SpineBehaviorTest.assertVisitedRefusesToReadCoverageFromATruncatedRun` exercises this exact
    // guard through the alias. A second witness for it was only justified while the two functions were
    // duplicated implementations.

    @Test
    fun testThrowsMatchesTheThrownTypeAndItsMessage() {
        val initial = CoreState().apply {
            // `.registers 2` and one reference parameter, so p0 is v1.
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        CoreTester.testThrows(
            SPINE, "throwUncaught(Ljava/lang/RuntimeException;)V", initial,
            exceptionType = "Ljava/lang/RuntimeException;", message = "boom",
        )
    }

    @Test
    fun testThrowsReadsTheExactMultiArmEndingFrame() {
        CoreTester.testThrows(
            "Ldifferential/exceptions;",
            "ambiguousLocalDivide(I)V",
            CoreState(),
            exceptionType = "Ljava/lang/ArithmeticException;",
            message = "/ by zero",
        )
    }

    @Test
    fun testThrowsRejectsAThrowOfADifferentType() {
        // Bug class: a facade that only checked "some path threw" would accept ANY exception, so every
        // divide-by-zero test in Task 8 would pass on an unrelated throw -- including one the engine
        // raised for the wrong reason.
        val initial = CoreState().apply {
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testThrows(
                SPINE, "throwUncaught(Ljava/lang/RuntimeException;)V", initial,
                exceptionType = "Ljava/io/IOException;",
            )
        }
        assertTrue(failure.message!!.contains("expected a Ljava/io/IOException;"), failure.message!!)
    }

    @Test
    fun aHostArraySeedBecomesATrackedArrayWithPerElementValues() {
        // The migration's central claim: `setRegisters(0, intArrayOf(0x42), "[I", ...)` -- copied verbatim
        // from the corpus -- must land in the heap as a tracked array, not as a live host array in a raw
        // (spec §4.1 forbids the latter for a mutable, and Frame.copy would share it between siblings).
        //
        // THE FIXTURE MUST NOT WRITE THE ARRAY REGISTER, and that is why this drives
        // `getWithThreeDistinctRegisters()V` (`aget v2, v0, v1`) rather than `get()V` (`aget v0, v0, v1`).
        // `frame.peek(0)` below is the POST-op frame -- `ExecutionSession.step` runs the op against the
        // node's own frame -- so on `get()V` this read answered the seeded array ref only while `aget` was
        // an UnsupportedOp that touched nothing. Plan 7 Task 7 landed the opcode and it began answering the
        // ELEMENT (`Known(66, "I")`), which made the test fail while the claim it makes was still true. Any
        // future fixture used here must leave its array operand alone for the same reason.
        val graph = CoreTester.execute("Laget_test;", "getWithThreeDistinctRegisters()V", CoreState().apply {
            setRegisters(0, intArrayOf(0x42), "[I", 1, 0, "I")
        })
        val seeded = graph.nodePile(0).first().frame.peek(0) as Value.Known
        assertTrue(seeded.raw is HeapRef, "a seeded array must be tracked, not a live host raw: ${seeded.raw}")
        val array = graph.nodePile(0).first().frame.arrayAt(seeded.raw as HeapRef)!!
        assertEquals(1, array.length)
        assertEquals(Value.Known(0x42, VirtualType.of("I")), array.element(0))
    }

    @Test
    fun setRegisterTwoArgOverloadClearsAnyPriorArraySeedForThatRegister() {
        // M7 (Plan 6 whole-branch review): CoreState.setRegister(register, value: Value) did not clear
        // registerToArray, while both branches of the 3-arg setRegister already clear each other. A
        // migrator writing setRegisters(0, IntArray(1), "[I") and then hand-writing
        // setRegister(0, Value.Unknown(...)) -- the shape an unknown-element array migration reaches for,
        // e.g. core/op/APutOpTest.anUnknownValueMakesOnlyItsOwnElementUnknown -- used to leave register 0 in
        // BOTH maps at once, and CoreTester.test would assert two mutually exclusive things about it.
        // (The name here used to be the oracle's `APutOpTest.canPutUnknownValue`; that file was deleted in
        // Plan 7 Task 9, and a bare `APutOpTest.` would now resolve against the core's file instead.)
        val state = CoreState().apply {
            setRegisters(0, IntArray(1), "[I")
            setRegister(0, Value.Unknown(VirtualType.of("I")))
        }
        assertTrue(state.arrays.isEmpty(), "the 2-arg setRegister must clear a prior array seed: ${state.arrays}")
        assertEquals(Value.Unknown(VirtualType.of("I")), state.getRegister(0))
    }

    @Test
    fun theUnknownSentinelIsHonoredPerElementInsideAHostArray() {
        // TRAP 1 (CARRY-FORWARD Plans 6–7 item 3 used to flag this; now closed, see STATUS.md's Plan 6
        // entry): the top-level sentinel check cannot see a sentinel nested in a composite raw, and
        // `core/op/AGetOpTest.canGetUnknownElement` nests one. Without this the sentinel object itself would be moved into a
        // register by `aget` and the comparison would silently become engine-vs-engine instead of
        // engine-vs-oracle.
        val state = CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") }
        val spec = state.arrays.getValue(0)
        val elements = CoreState.elementsOf(spec)
        assertEquals(Value.Unknown(VirtualType.of("I")), elements[0])
        assertEquals(Value.Known(5, VirtualType.of("I")), elements[1])
    }

    @Test
    fun elementsOfUsesTheHostArraysOwnClassNotADisagreeingArrayDescriptorTag() {
        // Object over tag, the OTHER direction from the sentinel test above: when a genuine guest array
        // exists (no UNKNOWN element), its OWN class decides the component type even if the declared tag
        // is a different, compatible array descriptor -- the covariant shape `canPutStringInObjectArray`
        // seeds: a String[] tagged "[Ljava/lang/Object;". The superseded rule preferred the tag whenever
        // it was itself an array descriptor, which would have answered Object here instead of String.
        val state = CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") }
        val elements = CoreState.elementsOf(state.arrays.getValue(0))
        assertEquals(Value.Known("Arrakis", VirtualType.of("Ljava/lang/String;")), elements[0])
    }


    @Test
    fun anArrayExpectationWhoseLengthDiffersFails() {
        // Closes a previously-unpinned assertion: CoreTester's array loop asserts length before
        // elements, but every enabled array test used equal-length pairs, so that assertEquals could be
        // deleted with nothing failing. The version of this test that would have covered it needed
        // Task 6's new-array and was @Disabled; identity() (spine.smali) needs no array opcode at all,
        // so this pins it now instead of waiting. Element 0 deliberately AGREES (both 1) so only the
        // length check, never the element loop, can catch the mismatch -- a discriminating case, per
        // this plan's repeated point about vacuous guards.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, intArrayOf(1, 2, 3), "[I") },
                CoreState().apply { setRegisters(0, intArrayOf(1), "[I") },
            )
        }
        assertTrue(failure.message!!.contains("length of register"), failure.message!!)
    }

    @Test
    fun aDeclaredArrayTypeAcceptsALegallyCovariantRuntimeArray() {
        // The whole point of Fix 1: `String[] <: Object[]` is legal covariance, so a test may declare
        // "[Ljava/lang/Object;" while the array's true runtime type is [Ljava/lang/String; -- exactly
        // `core/op/APutOpTest.canPutStringInObjectArray`'s shape, migrated in Plan 7 Task 8 and now also the
        // seed shape of `…aCovariantlyTaggedArrayStillThrowsForAnIncompatibleComponent`. An EQUALITY check
        // on the declared-vs-runtime type would reject this verified, legitimate input as a harness
        // defect rather than accept it as the assignability it actually is.
        CoreTester.test(
            SPINE, "identity()Ljava/lang/Object;",
            CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") },
            CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") },
        )
    }

    @Test
    fun aPrimitiveDeclaredArrayTypeRejectsAReferenceComponentRuntimeArray() {
        // Fix 1's invariance half, in the concrete shape Plan 6's brief surfaced as a finding rather than a
        // fix. The CORPUS's `canPutConstZeroNullObject` declared `arrayType = "[I"` while seeding an actual
        // host String[] and running `aput-object` -- a variant/component mismatch no verified dex can reach
        // (aput-object requires a reference-component array; DALVIK-SEMANTICS.md's aget*/aput* table, VERIFIER
        // tier). **Past tense deliberately: the corpus file was deleted in Plan 7 Task 9, and the surviving
        // `core/op/APutOpTest.canPutConstZeroNullObject` declares "[Ljava/lang/String;"** -- Task 8's one
        // authorized departure, made BECAUSE of this check, so citing that name here would point at a test
        // showing the opposite of the shape described. Reproduced structurally with a
        // zero-opcode fixture: primitive arrays are invariant, so a declared "[I" can never be satisfied
        // by a runtime [Ljava/lang/String;, and the assignability check must fail loudly rather than
        // silently pick a winner.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, arrayOf<String?>(null), "[I") },
                CoreState().apply { setRegisters(0, arrayOf<String?>(null), "[I") },
            )
        }
        assertTrue(failure.message!!.contains("is not assignable to declared type"), failure.message!!)
    }

    @Test
    fun aSentineledArraySeedAllocatesWithTheDeclaredTypeNotTheObjectsErasedClass() {
        // B2 (Plan 6 whole-branch review): CoreState.elementsOf already derived the component type from
        // the declared tag when a sentinel is present, but CoreTester.execute allocated the ARRAY
        // OBJECT's own type from CoreState.arrayTypeOf(spec.hostArray) unconditionally -- which for a
        // sentineled seed is ALWAYS [Ljava/lang/Object;, because a primitive IntArray has no slot for the
        // UNKNOWN sentinel object, so Kotlin backs `arrayOf(CoreState.UNKNOWN, 5)` with a plain Object[]
        // no matter what the seed conceptually means. So the allocated TrackedArray's own .type disagreed
        // with the elements elementsOf derived for it -- exactly what HeapObject.TrackedArray's KDoc says
        // cannot happen ("established at allocation from something that cannot lie").
        //
        // No manual heap inspection needed to catch a regression here: the existing assignability check
        // this same class's Fix-1 tests exercise (`CoreTester.kt`'s `isSubtypeOf(array.type, spec.type)`)
        // already fails loudly on the unfixed behavior -- isSubtypeOf([Ljava/lang/Object;, [I) is false,
        // since primitive arrays are invariant -- so a plain `CoreTester.test` round-trip is enough.
        CoreTester.test(
            SPINE, "identity()Ljava/lang/Object;",
            CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") },
            CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") },
        )
    }

    @Test
    fun aSeedAliasedAsTheExpectationIsStillRejectedWithArrays() {
        // The other vacuity guard that has to keep working: passing ONE CoreState as both seed and
        // expectation makes every assertion "the seed survived". `require(initial !== expected)` catches it
        // by identity and must not be loosened while adding the arrays branch.
        //
        // Live now despite naming `Lnew_array_test;` (Task 6's new-array is not implemented yet): the
        // identity guard fires and throws IllegalArgumentException BEFORE `test` ever calls `execute`, so
        // this never touches the unimplemented opcode. Verified empirically -- see task-5-report.md.
        val state = CoreState().apply { setRegisters(0, IntArray(1), "[I") }
        assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test("Lnew_array_test;", "createIntegerArray()V", state, state)
        }
    }

    @Test
    fun testRefusesTheCorpusNegativeResultRegisterIdiomForArrays() {
        // The array counterpart of testRefusesTheCorpusNegativeResultRegisterIdiom above, and it was not
        // hypothetical: all four of `opcode/FilledNewArrayTest`'s `IntegrationTest` tests wrote exactly
        // `expected.setRegisters(ExecutionState.RESULT_REGISTER, elements, "[I")` -- the old engine's
        // negative-index result idiom applied to an ARRAY. Guard 2 has to see `expected.arrays.keys` too, or
        // those four migrations would have landed silently in `registerToArray[-1]`, which nothing ever
        // reads. **It fired, and it worked**: Plan 7 Task 11 migrated those four to
        // `core/op/FilledNewArrayOpTest` against new fixture methods with a `move-result-object`, asserting
        // the real register the slot was moved into. Cited by FILE and TEST NAME rather than by line range,
        // because that file is now deleted and a line range would resolve against whatever moved into it.
        val expected = CoreState().apply { setRegisters(-1, IntArray(1), "[I") }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
        }
        assertTrue(failure.message!!.contains("RESULT_REGISTER"), failure.message!!)
    }

    @Test
    fun testRefusesAnExpectedArrayRegisterThatDoesNotExist() {
        // The array counterpart of testRefusesAnExpectedRegisterThatDoesNotExist above: an out-of-range
        // ARRAY register must fail the same way a scalar one does. Without extending the range check to
        // `expected.arrays.keys`, a mistyped array register would instead read Value.Top at execution
        // time, fail the `is Known` check inside the array loop, and report a confusing "should hold a
        // tracked array, but was Top" instead of pointing at the register number itself.
        val expected = CoreState().apply { setRegisters(999, IntArray(1), "[I") }
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
        }
        assertTrue(failure.message!!.contains("outside"), failure.message!!)
    }

    @Test
    fun anArrayExpectationComparesEveryElementNotJustTheFirst() {
        // Load-bearing for this task's own mutation check (Step 6): a loop truncated to compare only
        // element 0 needs a MULTI-element mismatch to catch it, which none of the other live array tests
        // provide -- `aHostArraySeedBecomesATrackedArrayWithPerElementValues` inspects the frame directly
        // instead of calling `test`, `aSeedAliasedAsTheExpectationIsStillRejectedWithArrays` throws at the
        // identity guard before `execute` ever runs, and the length/assignability tests above each seed a
        // single-element array. Without a live, DISCRIMINATING case here, mutating the loop to compare
        // only element 0 would have nothing to kill it -- exactly the vacuous-guard shape this plan's
        // brief has warned about five times already. `identity()` (spine.smali) is a zero-array-opcode
        // fixture built for this: it returns v0 unchanged, so seeding `[1, 2]` and expecting `[1, 999]`
        // (element 0 agrees, element 1 does not) means a correct, full-array comparison must fail -- and
        // a loop truncated to element 0 would miss it and pass.
        assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, intArrayOf(1, 2), "[I") },
                CoreState().apply { setRegisters(0, intArrayOf(1, 999), "[I") },
            )
        }
    }

    // ------------------------------------------------------------------------------------------
    // testCaught: the CAUGHT-throw assertion, pinned in BOTH directions before anything uses it.
    // Twelve tests across Plan 7's Tasks 6, 7, 9 and 10 depend on it, so a vacuous assertion here
    // becomes twelve vacuous assertions -- the same multiplier the file's header describes.
    // ------------------------------------------------------------------------------------------

    @Test
    fun testCaughtAcceptsAThrowTheMethodsOwnHandlerCaught() {
        // `divIntWithCatch()V` (binary_math_test.smali:102-119) is `.locals 2`, `div-int v0, v0, v1` @0
        // (format 23x = 2 code units), `return-void` @2, `move-exception v0` @3, `goto :return` @4. A
        // known zero divisor makes the ArithmeticException DEFINITE, the declared
        // Ljava/lang/ArithmeticException; handler catches it, and the run returns -- exactly the shape
        // the corpus's `Tester.testSimpleException` asserted and `testThrows` asserts the opposite of.
        //
        // No `unreachedAddress` here, and the reason is a fixture fact rather than an omission: this
        // method's fall-through address (2) is ALSO where `:return` sits, which the handler's `goto`
        // jumps to, so 2 is reached on the caught path. The parameter is pinned separately below.
        CoreTester.testCaught(
            BINARY_MATH, "divIntWithCatch()V",
            CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
            exceptionType = "Ljava/lang/ArithmeticException;",
            message = "/ by zero",
            caughtInRegister = 0,
        )
    }

    @Test
    fun testCaughtRejectsAThrowThatEscaped() {
        // The other direction, and the whole reason this helper exists. `divInt()V` is the SAME
        // instruction with NO handler (binary_math_test.smali:70-76), so the identical ArithmeticException
        // escapes and produces an `Outcome.Threw`. A `testSimpleException -> testThrows` substitution
        // looked correct precisely because it was checked against handler-free fixtures like this one; a
        // helper that accepted both shapes would let every `…WithCatch()V` migration assert nothing about
        // which one it got.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divInt()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                message = "/ by zero",
                caughtInRegister = 0,
            )
        }
        assertTrue(
            failure.message!!.contains("ESCAPED"),
            "must fail on the escaped-vs-caught half, not on a downstream consensus read: ${failure.message}",
        )
    }

    @Test
    fun testCaughtRejectsAnUnreachedAddressThatWasReached() {
        // Bug class: `unreachedAddress` accepted and then never checked. It carries the "did not fall
        // through" half of every migrated claim that passes it, and without this the whole block could be
        // deleted with nothing dying -- every caller passes an address that genuinely is unreached, so they
        // all keep passing. Address 2 of `divIntWithCatch()V` IS reached (it is `:return`, which the
        // handler's `goto` targets), so a helper that honors the parameter must reject it.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                caughtInRegister = 0,
                unreachedAddress = 2,
            )
        }
        assertTrue(
            failure.message!!.contains("must not also explore the fall-through at 2"),
            "must fail on the unreached-address half: ${failure.message}",
        )
    }

    @Test
    fun testCaughtRejectsACaughtThrowWithADifferentMessage() {
        // Bug class: the message read but never compared. Every caller passes the message the engine
        // already produces, so deleting the comparison would leave all of them green -- and the migrated
        // claims whose whole content is an exception message (`ArrayLengthOpTest`'s "Attempt to get length
        // of null array", and the [ART]-shaped NegativeArraySizeException text) would silently stop
        // asserting it.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                message = "not the message ART emits",
                caughtInRegister = 0,
            )
        }
        assertTrue(failure.message!!.contains("/ by zero"), "must compare the messages: ${failure.message}")
    }

    @Test
    fun testCaughtRejectsACaughtThrowOfADifferentType() {
        // The type half, mirroring `testThrowsRejectsAThrowOfADifferentType`: a helper that only checked
        // "the register holds something" would accept an unrelated caught exception, so a migration could
        // pass on an exception the engine synthesized for the wrong reason.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/NullPointerException;",
                caughtInRegister = 0,
            )
        }
        assertTrue(
            failure.message!!.contains("should hold the caught Ljava/lang/NullPointerException;"),
            "must fail on the caught TYPE: ${failure.message}",
        )
    }

    private companion object {
        private const val SPINE = "Lspine/spine;"
        private const val BINARY_MATH = "Lbinary_math_test;"
    }
}
