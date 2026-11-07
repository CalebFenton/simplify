package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `new-array` (spec §4.2). Migrated from `org.cf.smalivm.opcode.NewArrayOpTest` -- all seven of its
 * tests, with `TestState`/`Tester`/`UnknownValue()` swapped for `CoreState`/[CoreTester]/
 * [CoreState.UNKNOWN] -- plus the cases the corpus never covered (a negative length, and the
 * deliberate choice not to fork on an unknown one).
 *
 * Grounding, with tiers, is `docs/DALVIK-SEMANTICS.md#new-array-op-23-22c`. [BC]'s complete text for
 * this opcode is *"Construct a new array of the indicated type and size. The type must be an array
 * type."* -- it names no exception, here or anywhere else for arrays, so every exception below is
 * **INHERITED** from [JVMS] §6.5 rather than something "Dalvik throws".
 */
class NewArrayOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    @BeforeEach
    fun setUp() {
        expected = CoreState()
        initial = CoreState()
    }

    @Test
    fun canCreate2DIntegerArray() {
        // Ported VERBATIM, and the expected value is exactly right rather than merely close: the
        // component of `[[I` is `[I`, a reference, so `defaultElementValue` gives `Known(null, [I)` for
        // each element, and the host `arrayOfNulls<IntArray>(3)` the corpus wrote converts to three
        // `Known(null, [I)` elements through `CoreState.elementsOf`. The values MATCH.
        val length = 3
        initial.setRegisters(0, length, "I")
        expected.setRegisters(0, arrayOfNulls<IntArray>(length), "[[I")
        CoreTester.test(CLASS_NAME, "create2DIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreate2DLocalInstanceArray() {
        // NON-MECHANICAL substitution. The oracle asserted `Array.getLength(consensus.raw)` and
        // `consensus.raw.javaClass.name` -- host reflection on the raw, which no longer applies: the raw
        // of a VM-tracked mutable is a HeapRef (spec §4.1), never the live host array. The host-`Class`
        // assertion is now MEANINGLESS rather than merely relocated, because the core never loads a host
        // class for an array at all -- which is itself the point of design decision 4.
        //
        // The CLAIM is unchanged: the declared type carries the dimensions and the length is the seed.
        // Only the mechanism is -- the tracked array's own `type` and `length`, read through heap
        // consensus.
        val length = 5
        initial.setRegisters(0, length, "I")
        val graph = CoreTester.execute(CLASS_NAME, "create2DLocalInstanceArray()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val consensus = trusted!!.terminatingConsensus(0)
        assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked array: $consensus")
        assertEquals(VirtualType.of("[[$CLASS_NAME"), consensus.type)
        val array = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef)
        assertTrue(array is HeapObject.TrackedArray, "no array behind the ref; got $array")
        assertEquals(length, (array as HeapObject.TrackedArray).length)
        assertEquals(VirtualType.of("[[$CLASS_NAME"), array.type)
    }

    @Test
    fun canCreateIntegerArray() {
        val length = 1
        initial.setRegisters(0, length, "I")
        expected.setRegisters(0, IntArray(length), "[I")
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    /**
     * Pins `buildNewArray`'s OPERAND ORDER, which nothing else in the suite can see.
     *
     * Bug class: every `new-array` in the whole fixture tree is `v0, v0`, so `destRegister` and
     * `lengthRegister` collapse to one number and swapping `registers.registerA`/`registerB` in
     * `OpBuilder.buildNewArray` leaves the entire suite green. `22c`'s two operands are genuinely
     * ordered -- [FMT] gives `22c` as `B|A|op CCCC` with A the destination -- and real obfuscated code
     * emits `new-array v3, v5, [B` constantly, so an inverted decoder would mis-execute ordinary input
     * while every test passed.
     *
     * v0 is seeded and never named by the instruction, so it is a witness that the op writes only where
     * it should. Swapping the operands makes the length come from unseeded v1 (`Top`, hence an unknown
     * length) and the array land in v2, so BOTH v1 and v2 below are wrong and the failure is loud.
     */
    @Test
    fun theDestinationAndLengthOperandsAreNotInterchangeable() {
        val length = 3
        initial.setRegisters(0, 0x1177, "I", 2, length, "I")
        expected.setRegisters(0, 0x1177, "I", 1, IntArray(length), "[I", 2, length, "I")
        CoreTester.test(CLASS_NAME, "createIntegerArrayIntoADifferentRegister()V", initial, expected)
    }

    @Test
    fun canCreateIntegerArrayWithShortTypeLengthValue() {
        // Dalvik's type tags are deliberately ambiguous, so a length register may hold a Short under any
        // integral tag; `Coerce.toInt` is the one table that reads all of them.
        val length: Short = 1
        initial.setRegisters(0, length, "S")
        expected.setRegisters(0, IntArray(length.toInt()), "[I")
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreateIntegerArrayWithUnknownLengthValue() {
        // The migrated form of the unknown-length case: an unknown length means an unknown array, and the
        // register keeps the instruction's declared type. `anUnknownLengthYieldsAnUnknownArrayWithoutForking`
        // below is this claim PLUS the fork half, which `CoreTester.test` cannot see.
        initial.setRegisters(0, CoreState.UNKNOWN, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "[I")
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreateLocalInstanceArray() {
        // Same non-mechanical substitution as `canCreate2DLocalInstanceArray`, one dimension down; see
        // that test's comment for why the host-`Class` half of the oracle's assertion is gone rather
        // than relocated.
        val length = 1
        initial.setRegisters(0, length, "I")
        val graph = CoreTester.execute(CLASS_NAME, "createLocalInstanceArray()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val consensus = trusted!!.terminatingConsensus(0)
        assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked array: $consensus")
        assertEquals(VirtualType.of("[$CLASS_NAME"), consensus.type)
        val array = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef)
        assertTrue(array is HeapObject.TrackedArray, "no array behind the ref; got $array")
        assertEquals(length, (array as HeapObject.TrackedArray).length)
        assertEquals(VirtualType.of("[$CLASS_NAME"), array.type)
    }

    /**
     * The exception TYPE changed, deliberately, and it is registered as an oracle defect
     * (`Fixtures.intendedDivergences`, `new-array-unloadable-component`). The oracle raises a guest
     * `ClassNotFoundException` because `Utils.buildArray` propagates the HOST loader's exception from
     * `ClassUtils.getClass`; the runtime raises a linking `Error`. **INHERITED** ([JVMS] §6.5
     * `anewarray`: *"During resolution of the symbolic reference to the class, array, or interface type,
     * any of the exceptions documented in §5.4.3.1 can be thrown"*), and §5.4.3.1's resolution failures
     * are `NoClassDefFoundError` and friends.
     *
     * **`testThrows` is right for this ONE test and wrong for every other `…WithCatch`-shaped
     * migration.** `createNonExistentArrayClass()V` declares a `Ljava/lang/Exception;` handler, so it
     * would normally catch -- but `NoClassDefFoundError` **extends `Error`**, which `catch (Exception)`
     * cannot catch, so the throw ESCAPES. That is precisely the handler-reachability consequence design
     * decision 4 argues for, showing up as a change in which harness helper the test needs.
     *
     * **Two things follow, and both are stated rather than left implicit.** The fixture's `:catch_0`
     * block is now DEAD CODE under the core -- a `move-exception` that never runs. And the oracle's
     * *"the exception lands in v0"* half of the claim is therefore **DROPPED, not migrated**: with no
     * returning path, `terminatingConsensus(0)` has nothing to read. A silently narrowed claim is how a
     * migration checks less than its original without anyone noticing.
     */
    @Test
    fun nonExistentClassNameThrowsException() {
        initial.setRegisters(0, 1, "I")
        CoreTester.testThrows(
            CLASS_NAME, "createNonExistentArrayClass()V", initial,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "does.not.exist",
        )
        // The negative half, written out by hand: `testThrows` deliberately says nothing about whether a
        // normal path also ran, and a `wasReached` negative is only a dead-code claim on a run that
        // explored everything -- so the completion gate is NOT redundant with the one inside `testThrows`.
        // `new-array` is format 22c = 2 code units, so the `nop` sits at 2.
        val graph = CoreTester.execute(CLASS_NAME, "createNonExistentArrayClass()V", initial)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "the fall-through is only provably unexplored on a run that explored everything",
        )
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")

        // BRIEF DEFECT, resolved here rather than reproduced. The brief asked for
        // `assertEquals(emptySet<Int>(), graph.registersAssigned(0))` while its own justification said
        // `registersAssigned` "is static, so it reads off `Op` rather than a frame" -- and those two
        // statements contradict each other. `ExecutionGraph.registersAssigned` really is static (it
        // returns `op.registersAssigned`), so for a `new-array` it is `{0}` on every path including the
        // throwing one, and the brief's expectation would fail. Both halves are kept honestly instead:
        //  - the STATIC declaration, which is a real claim about decode-time width (an `isWide = true`
        //    here would answer `{0, 1}`) but is NOT the oracle's claim;
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow reference result owns one register")
        //  - and the oracle's ACTUAL claim ("a throwing new-array assigns nothing"), re-expressed
        //    through the post-op frame. It cannot be `frame.registersAssigned()`: `CoreTester` seeds
        //    through `Frame.assign`, which records the seeded register there, where the oracle's
        //    `pokeRegister` did not (the asymmetry `Fixture.seededRegisters`' KDoc documents). So the
        //    claim is asserted where it is observable and discriminating -- v0 still holds the LENGTH,
        //    not an array ref, because the throw happened before any assignment.
        assertEquals(
            Value.Known(1, VirtualType.of("I")), graph.nodePile(0).first().frame.peek(0),
            "a throwing new-array must leave its destination register untouched",
        )
    }

    /**
     * Bug class: a negative length. `Utils.buildArray` lets `Array.newInstance` raise
     * NegativeArraySizeException, and the oracle's `buildInstance` catches only ClassNotFoundException
     * (the host loader's), so it escapes and kills the engine -- no corpus test covers it. That Dalvik
     * throws NegativeArraySizeException at all is INHERITED ([JVMS] 6.5 newarray/anewarray, "Otherwise,
     * if count is less than zero"); Dalvik's own reference names no array exception anywhere.
     *
     * The MESSAGE is [ART]-tier, not spec: neither [BC] nor JLS specifies any message. ART's
     * `CheckArrayAlloc` calls the `int` overload of `ThrowNegativeArraySizeException`, which formats
     * `StringPrintf("%d", size)` -- the decimal length below. Both quotes are in
     * docs/DALVIK-SEMANTICS.md's new-array section (`runtime/common_throws.cc:389-392`), which is this
     * repo's verified-quote registry; the citation used to live only here, i.e. it was unbacked.
     * Asserted so it cannot drift silently -- NOT because a message is evidence of fidelity, which
     * docs/DALVIK-SEMANTICS.md is explicit that it never is.
     *
     * `testCaught`, not `testThrows`: `createIntegerArrayWithCatch()V` declares a Ljava/lang/Exception;
     * handler and NegativeArraySizeException is a RuntimeException, so the throw is CAUGHT and produces no
     * Outcome.Threw. This is the shape the corpus's testSimpleException asserted, which is why testCaught
     * exists.
     */
    @Test
    fun aKnownNegativeLengthThrowsNegativeArraySizeException() {
        CoreTester.testCaught(
            CLASS_NAME, "createIntegerArrayWithCatch()V",
            CoreState().apply { setRegisters(0, -1, "I") },
            exceptionType = "Ljava/lang/NegativeArraySizeException;",
            message = "-1",
            caughtInRegister = 0,          // `move-exception v0` -- read it off the fixture
            unreachedAddress = 2,          // new-array is 22c = 2 units, so the `nop` sits at 2
        )
    }

    /**
     * Bug class: forking a throw the length makes infeasible. A known non-negative length proves the
     * allocation succeeds, so `[Continue]` alone is correct and a `[Continue, Throw]` here would tell a
     * deobfuscator a handler is reachable that Dalvik can never take -- the over-approximating wrong
     * answer `Fixtures`' binary-div-int-unknown-dividend entry exists to keep honest.
     *
     * **The successor count is the assertion that bites; the no-escape one below it cannot fail on this
     * fixture and says so.** `createIntegerArrayWithCatch()V` declares a `Ljava/lang/Exception;` handler and
     * `NegativeArraySizeException` is a `RuntimeException`, so a spuriously forked NASE would be CAUGHT and
     * produce no `Outcome.Threw` at all -- invisible to `outcomes`, and visible only as a second child.
     * Contrast `ArrayLengthOpTest.aTrackedArrayIsProvablyNonNullSoNoNullPointerExceptionIsForked`, where the
     * same line sits on the handler-FREE `getLength()V` and is genuinely discriminating.
     */
    @Test
    fun aKnownNonNegativeLengthAllocatesWithoutForking() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArrayWithCatch()V", CoreState().apply {
            setRegisters(0, 2, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor")
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "no throw may ESCAPE -- a fork this fixture's handler CATCHES is caught by the successor count above",
        )
    }

    /**
     * Bug class: an unknown length forking NegativeArraySizeException on every allocation. This test pins a
     * DROPPED FEASIBLE PATH, which is unique in this plan and must be labelled as such rather than dressed
     * up as soundness: a negative length really is possible under an unknown, so the throw really is
     * reachable, and the core deliberately does not explore it. Tier: ENGINE CHOICE resting on an
     * UNMEASURED empirical premise (that obfuscators do not emit catch(NegativeArraySizeException)); the
     * cost argument is that forking doubles the path count of every unknown-length allocation. It is also
     * the oracle's answer (`NewArrayOp.kt:39-40` returns unknown and does not fork), so the differential
     * agrees -- which proves only that the two engines agree, per DALVIK-SEMANTICS.md's rule 1.
     *
     * The asymmetry with ArrayLengthOp's NPE fork is deliberate and is argued, not assumed: there a
     * possibly-null array is pervasive AND catch(Exception) catches an NPE, so the handler is routinely
     * live. Counting NASE handlers across the corpus and an APK sample would upgrade this to CORPUS; until
     * someone does, it is a recorded liability. Written down so a later plan changes it on purpose rather
     * than by reflex.
     */
    @Test
    fun anUnknownLengthYieldsAnUnknownArrayWithoutForking() {
        val expected = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
        val seed = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, expected)
        // STRENGTHENING over the brief, because the brief's version did not assert what its own name
        // claims: `CoreTester.test` reads the consensus of RETURNING paths, so a `[Continue, Throw]` here
        // would leave the returning path's value at `Unknown([I)` and pass unchanged. The dropped fork is
        // the whole point of the test, so it is asserted rather than described.
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", seed)
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor -- the fork is dropped")
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "the NegativeArraySizeException fork is deliberately not explored under an unknown length",
        )
    }

    /**
     * **The length half of the bit-vs-value boundary `Coerce.toProvableInt` draws, and the Critical this fix
     * closed.** A length register holding `Known(-0.5f, "F")` used to reach `Coerce.toInt`, which truncates BY
     * VALUE: `(-0.5f).toInt()` is **0**, so the negative-length row's `length < 0` test was FALSE, the op
     * allocated a **zero-length `[I`** and reported `Complete`. A device reads the register's 32 bits --
     * `-0.5f` is `0xBF000000`, i.e. **-1090519040** -- and throws `NegativeArraySizeException`. So the old
     * answer invented a constant no runtime produces, and it invented the most misleading one available: a
     * fully `Known` empty array, which every later `array-length` folds to 0 and every later `aget`/`aput`
     * turns into a *definite* bounds exception (`APutOpTest.aZeroLengthArrayCannotBeStoredIntoAtAnyIndexSoTheFallThroughIsInfeasible`
     * is that row).
     *
     * **VERIFIER -> engine's choice**, so spec §2.2 decides it: a float cannot reach a `new-array` size
     * register in verified dex ([CON] B1's general *"arguments... must always match the instruction"*), so
     * there is no Dalvik behaviour to match. **No [ART] verifier symbol is named on purpose** -- there is no
     * `VerifyNewArray` in ART (see [FilledNewArrayOp]'s KDoc, which records the word-boundary grep), and
     * naming a plausible-looking one is how this plan family shipped an invented citation before.
     *
     * **WHICH ASSERTION DISCRIMINATES, and an honest statement of what this fix does NOT do.** The
     * `CoreTester.test` call is the whole of the fix's witness: pre-fix v0 held `Known(HeapRef, "[I")` for a
     * length-0 array, so it fails there and passes here. The fork-shape lines below **passed before the fix
     * too**, and they are kept for a different reason: they pin that a refused length lands on the SAME row an
     * `Unknown` length lands on ([anUnknownLengthYieldsAnUnknownArrayWithoutForking]), which deliberately does
     * **not** fork `NegativeArraySizeException`. So this fix removes an invented constant; it does **not** add
     * the throw. The NASE for a non-provable length remains a knowingly dropped feasible path, filed in
     * `CARRY-FORWARD.md` under the unknown-length entry, and the measurement that would settle it (counting
     * `catch (NegativeArraySizeException)` clauses in a real APK) is unchanged by this test.
     */
    @Test
    fun aFloatLengthIsNotProvableRatherThanTruncatedToZero() {
        val seed = CoreState().apply { setRegisters(0, -0.5f, "F") }
        val want = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, want)
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", seed)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "and it completes -- a silent zero-length array on a Complete run was the whole defect",
        )
        assertEquals(
            1, graph.nodePile(0).first().children.size,
            "a refused length takes the unknown-length row, which forks nothing (filed, not fixed here)",
        )
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "so no NegativeArraySizeException escapes either -- see this test's KDoc",
        )
    }

    /**
     * Bug class: silently following [JVMS] instead of ART on a both-bad new-array. The INHERITED order
     * resolves FIRST ([JVMS] 6.5 anewarray's "Linking Exceptions" block precedes its "Otherwise, if count
     * is less than zero"); ART inverts it (CheckArrayAlloc, entrypoint_utils-inl.h:292-322). We follow ART
     * because no device runs the JVMS order, and the consequence is observable: under our answer the
     * reachable handler is the one catching NegativeArraySizeException (an Exception), and under a strict
     * JVMS reading it is the one catching a linking Error, which catch(Exception) cannot catch at all.
     *
     * **Which half of [CoreTester.testCaught] actually discriminates, corrected after being read off a real
     * mutation rather than reasoned about.** An inverted order throws `NoClassDefFoundError`, which this
     * fixture's `Ljava/lang/Exception;` handler cannot catch, so the throw escapes and NO path returns --
     * `terminatingConsensus(0)` answers `Value.Top` and the **caught-type** assertion fails
     * ("...should hold the caught Ljava/lang/NegativeArraySizeException; ... but was Top"). It does **not**
     * fail on the "nothing escaped" half: that check is deliberately filtered to [exceptionType]
     * (see `testCaught`'s KDoc), so an escape of a DIFFERENT type is invisible to it and that assertion is
     * vacuously true here. Stated precisely because twelve tests across Tasks 6/7/9/10 inherit this helper
     * and this is the only place in the family that explains how it discriminates.
     */
    @Test
    fun aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst() {
        CoreTester.testCaught(
            CLASS_NAME, "createBothBadArrayClass()V",
            CoreState().apply { setRegisters(0, -1, "I") },
            exceptionType = "Ljava/lang/NegativeArraySizeException;",
            message = "-1",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    private companion object {
        private const val CLASS_NAME = "Lnew_array_test;"
    }
}
