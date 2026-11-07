package org.cf.smalivm.core.op

import java.util.concurrent.TimeUnit
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.MAX_TRACKED_ARRAY_LENGTH
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

/**
 * `new-array` (spec §4.2). Migrated from `org.cf.smalivm.opcode.NewArrayOpTest` -- all seven of its
 * tests, with `TestState`/`Tester`/`UnknownValue()` swapped for `CoreState`/[CoreTester]/
 * [CoreState.UNKNOWN] -- plus the cases the corpus never covered (a negative or unprovable length, and a
 * length too large to track).
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
        // An unknown length means an unknown array, and the register keeps the instruction's declared type.
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
     * Bug class: dropping a feasible NegativeArraySizeException. An unknown length may be negative, and ART
     * throws NegativeArraySizeException for any negative count (`CheckArrayAlloc`, cited in
     * docs/DALVIK-SEMANTICS.md's new-array section), so both the allocation and the throw are feasible.
     * `createIntegerArray()V` has no handler, so the speculative throw escapes and is visible in `outcomes`.
     */
    @Test
    fun anUnknownLengthForksNegativeArraySizeExceptionBesideAnUnknownArray() {
        val expected = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
        val seed = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, expected)
        CoreTester.testThrows(CLASS_NAME, "createIntegerArray()V", seed, NEGATIVE_ARRAY_SIZE)
    }

    /**
     * A length register holding a computed `Float` is read by its 32 bits: `-0.5f` is `0xBF000000`, i.e.
     * -1090519040, so `new-array` definitely throws `NegativeArraySizeException`. A value conversion would read
     * length 0 and allocate an empty array that every later `array-length` folds to 0.
     */
    @Test
    fun aFloatLengthIsReadByItsBits() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", CoreState().apply { setRegisters(0, -0.5f, "F") })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NegativeArraySizeException;"), CoreTester.escapingTypes(graph))
        assertEquals(1, graph.outcomes.size, "length -1090519040 is definitely negative: $graph")
    }

    /**
     * Bug class: an absent component class hiding the speculative NegativeArraySizeException. The size is
     * checked before the class (see [aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst]),
     * so an unknown length makes both throws feasible. The fixture's `Ljava/lang/Exception;` handler catches
     * the NegativeArraySizeException and cannot catch the NoClassDefFoundError, which escapes.
     */
    @Test
    fun anUnknownLengthWithAnAbsentComponentForksBothThrows() {
        val seed = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.testCaught(
            CLASS_NAME, "createBothBadArrayClass()V", seed,
            exceptionType = NEGATIVE_ARRAY_SIZE,
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        CoreTester.testThrows(
            CLASS_NAME, "createBothBadArrayClass()V", seed,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "does.not.exist",
        )
    }

    /**
     * Bug class: allocating one slot per element for a hostile length. `0x7fffffff` slots exhaust the host;
     * the `OutOfMemoryError` is a `VirtualMachineError`, which kills the whole run instead of one path.
     * Over [MAX_TRACKED_ARRAY_LENGTH] the array is not tracked, and the length is provably non-negative, so
     * nothing forks.
     */
    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun theLargestIntLengthYieldsAnUnknownArrayInsteadOfExhaustingTheHost() {
        assertLengthIsNotTracked(Int.MAX_VALUE)
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun aLengthJustOverTheTrackedBoundYieldsAnUnknownArray() {
        assertLengthIsNotTracked(MAX_TRACKED_ARRAY_LENGTH + 1)
    }

    /** Bug class: an off-by-one bound that refuses the largest length it admits. */
    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun aLengthAtTheTrackedBoundIsAllocated() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", CoreState().apply {
            setRegisters(0, MAX_TRACKED_ARRAY_LENGTH, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        val consensus = trusted.terminatingConsensus(0)
        assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked array: $consensus")
        val array = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef)
        assertEquals(MAX_TRACKED_ARRAY_LENGTH, (array as HeapObject.TrackedArray).length)
    }

    private fun assertLengthIsNotTracked(length: Int) {
        val seed = CoreState().apply { setRegisters(0, length, "I") }
        val want = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, want)
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", seed)
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "a non-negative length cannot throw",
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
        private const val NEGATIVE_ARRAY_SIZE = "Ljava/lang/NegativeArraySizeException;"
    }
}
