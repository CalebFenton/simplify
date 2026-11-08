package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `fill-array-data` (op 26, format **31t** = THREE code units, so the successor of an op at 0 is address
 * **3** and addresses 1 and 2 do not exist). Migrated from `org.cf.smalivm.opcode.FillArrayDataOpTest` --
 * all ten of its tests, ported verbatim; that file is deleted by this commit, and the engine it drove is
 * described in the past tense everywhere below.
 *
 * **The payload address is DATA, not code -- design decision 3, and it is SPEC-backed rather than merely
 * convenient.** The engine being replaced jumped to the `.array-data` pseudo-instruction and let a second op
 * run there, which then read its own return address and its target register off its PARENT node's state
 * (`opcode/FillArrayDataPayloadOp.kt`'s `getPsuedoInstructionReturnAddress()` /
 * `parent.registersAssigned.toList()[0]`) -- the hidden control state spec §5.2 says must stop. This core
 * decodes the payload at BUILD time and does the whole fill in one step, returning `Continue`.
 * [BC]'s "General design" is the grounding: pseudo-instructions holding payloads *"must never be
 * encountered during the normal flow of execution"*, and **[CON] B22** makes it a verifier constraint --
 * *"The **packed-switch-data, sparse-switch-data**, and fill-array-data pseudo-instructions must not be
 * reachable by control flow."* Quotes and tiers: `docs/DALVIK-SEMANTICS.md#fill-array-data-op-26-31t-and-the-array-data-payload`.
 * Pinned by [thePayloadAddressIsDataAndIsNeverExecuted].
 *
 * **NEITHER exception here is INHERITED, and that is unusual for this plan.** Every other array opcode's
 * NPE / AIOOBE / `ArrayStoreException` comes from [JVMS] §6.5 by way of [VER], because `aget`/`aput`/
 * `new-array`/`array-length` all have JVM counterparts. **`fill-array-data` has none** -- the JVM has no
 * payload pseudo-instruction at all, since `tableswitch`/`lookupswitch` carry their data inline -- so there
 * is nothing to inherit and the tiers are:
 *
 *  - **null array -> `NullPointerException`: [ART] ONLY, genuinely unspecified.** ART's message is
 *    *"null array in FILL_ARRAY_DATA"* and its null check precedes its length check. The zero-idiom half of
 *    the row IS **SPEC** ([BC]: *"In terms of bitwise representation, `(Object) null == (int) 0`"*), which is
 *    why `const/4 v0, 0` then `fill-array-data` is a definite NPE here
 *    ([aDalvikZeroArrayThrowsTheSameDefiniteNullPointerException]).
 *  - **payload longer than the array -> `ArrayIndexOutOfBoundsException`: the REQUIREMENT is SPEC**
 *    ([BC]: the table *"must contain no more elements than will fit in the array"*), **the failure mode is
 *    [ART] ONLY** (message *"failed FILL_ARRAY_DATA; length=%d, index=%d"*, whose second number is the
 *    element COUNT rather than a failing index -- because ART length-checks and then does a single `memcpy`,
 *    so the store is all-or-nothing). [aPayloadLongerThanTheArrayThrowsWithoutStoringAnything].
 *
 * **`Opcode.canThrow()` is WRONG for this opcode and must never be consulted.** dexlib2 2.5.2 declares
 * `FILL_ARRAY_DATA.flags == 0x0004` -- `CAN_CONTINUE` only, no `CAN_THROW`, so `canThrow()` answers
 * **false** -- while ART throws the two exceptions above from it. Throw edges here are decided from the
 * op's own semantics; `canContinue()` is the only flag the core reads for it, and that one IS correct.
 *
 * **Neither exception message is asserted, and neither is synthesized with one.** The corpus asserts no
 * message for this opcode (the engine being replaced never synthesized either exception at all -- it let a
 * host `ArrayIndexOutOfBoundsException` out of `java.lang.reflect.Array.set` kill the whole run), nothing
 * downstream reads a message, and [RT] records that runtimes deliberately changed their array-exception
 * text. ART's two strings are recorded in `docs/DALVIK-SEMANTICS.md` so a later plan may adopt them; that
 * would be an improvement, not a fix, and no expectation here blocks it.
 *
 * **The element conversion is normalized by payload WIDTH first and then by component type**, with every
 * case named -- see [FillArrayDataOp]'s KDoc for the table and for why that is simpler and more total than
 * the two-stage version it replaces (which had no `Byte` branch and got `[B` right only through its
 * `else`). The ten migrated fixtures cover all four widths and all eight primitive components.
 */
class FillArrayDataOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    companion object {
        private const val CLASS_NAME = "Lfill_array_data_test;"
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }

    @BeforeEach
    fun setUp() {
        expected = CoreState()
        initial = CoreState()
    }

    // ----------------------------------------------------------------------------------------------
    // The ten migrated tests. Values byte-identical to the corpus; the only changes are
    // TestState -> CoreState and Tester.test -> CoreTester.test.
    // ----------------------------------------------------------------------------------------------

    @Test
    fun canFillArrayDataBoolean() {
        initial.setRegisters(0, BooleanArray(4), "[Z")
        expected.setRegisters(0, booleanArrayOf(true, true, false, true), "[Z")
        CoreTester.test(CLASS_NAME, "fillArrayDataBoolean()V", initial, expected)
    }

    /**
     * ART copies the payload's bytes into the array (`FillArrayData`'s `memcpy`,
     * `runtime/entrypoints/entrypoint_utils.cc`), so a payload byte `2` sits in a boolean element as `2`. A
     * `Boolean` raw cannot hold that byte, so the element is unknown rather than a confident `false`.
     */
    @Test
    fun aBooleanPayloadByteOtherThanZeroOrOneFillsAnUnknownElement() {
        initial.setRegisters(0, BooleanArray(2), "[Z")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataBooleanTwo()V", initial)
        val trusted = graph.trusted()
        assertNotNull(trusted, "the fill completes on every path")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        val boolean = VirtualType.of("Z")
        assertEquals(listOf(Value.Unknown(boolean), Value.Known(true, boolean)), listOf(array.element(0), array.element(1)))
    }

    @Test
    fun canFillArrayDataByte() {
        initial.setRegisters(0, ByteArray(4), "[B")
        expected.setRegisters(0, byteArrayOf(0xa, 0xb, 0xc, 0xd), "[B")
        CoreTester.test(CLASS_NAME, "fillArrayDataByte()V", initial, expected)
    }

    @Test
    fun canFillArrayDataChar() {
        initial.setRegisters(0, CharArray(3), "[C")
        expected.setRegisters(0, charArrayOf('a', 'b', 'c'), "[C")
        CoreTester.test(CLASS_NAME, "fillArrayDataChar()V", initial, expected)
    }

    @Test
    fun canFillArrayDataDouble() {
        initial.setRegisters(0, DoubleArray(2), "[D")
        expected.setRegisters(0, doubleArrayOf(0.1, 1.5), "[D")
        CoreTester.test(CLASS_NAME, "fillArrayDataDouble()V", initial, expected)
    }

    @Test
    fun canFillArrayDataFloat() {
        initial.setRegisters(0, FloatArray(2), "[F")
        expected.setRegisters(0, floatArrayOf(1f, 2f), "[F")
        CoreTester.test(CLASS_NAME, "fillArrayDataFloat()V", initial, expected)
    }

    @Test
    fun canFillArrayDataInt() {
        initial.setRegisters(0, IntArray(5), "[I")
        expected.setRegisters(0, intArrayOf(1, 2, 3, 4, 5), "[I")
        CoreTester.test(CLASS_NAME, "fillArrayDataInt()V", initial, expected)
    }

    @Test
    fun canFillArrayDataLongWithLongs() {
        initial.setRegisters(0, LongArray(3), "[J")
        expected.setRegisters(0, longArrayOf(0x1000000000L, 0x2000000000L, 0x3L), "[J")
        CoreTester.test(CLASS_NAME, "fillArrayDataLongWithLongs()V", initial, expected)
    }

    @Test
    fun canFillArrayDataLongWithInts() {
        initial.setRegisters(0, LongArray(3), "[J")
        expected.setRegisters(0, longArrayOf(0x10, 0x20, 0x30), "[J")
        CoreTester.test(CLASS_NAME, "fillArrayDataLongWithInts()V", initial, expected)
    }

    @Test
    fun canFillArrayDataShortWithShorts() {
        initial.setRegisters(0, ShortArray(3), "[S")
        expected.setRegisters(0, shortArrayOf(100, 200, 5), "[S")
        CoreTester.test(CLASS_NAME, "fillArrayDataShortWithShorts()V", initial, expected)
    }

    @Test
    fun canFillArrayDataShortWithInts() {
        initial.setRegisters(0, ShortArray(3), "[S")
        expected.setRegisters(0, shortArrayOf(0x10, 0x20, 0x30), "[S")
        CoreTester.test(CLASS_NAME, "fillArrayDataShortWithInts()V", initial, expected)
    }

    // ----------------------------------------------------------------------------------------------
    // New coverage: the payload as data, and the rows the corpus never exercised.
    // ----------------------------------------------------------------------------------------------

    /**
     * Bug class: the payload address executing as code. The core decodes the payload at build time and
     * returns Continue, so the pseudo-instruction is never reached -- which is the whole of design decision
     * 3 and is also why `reachedAddresses` needs a projection convention (Task 10 Step 5). Asserted
     * positively (the fill happened) AND negatively (the payload did not run), with the completion gate
     * written out, because an absent address is a dead-code claim.
     */
    @Test
    fun thePayloadAddressIsDataAndIsNeverExecuted() {
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", CoreState().apply {
            setRegisters(0, IntArray(5), "[I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val payload = graph.methodView.payloadAddresses.single()
        assertFalse(graph.wasReached(payload), "a payload is data, not code")
        assertTrue(graph.wasReached(0), "and the fill itself ran")
    }

    /**
     * Bug class: a payload longer than the array. The engine being replaced did not check it -- `Array.set`
     * raised ArrayIndexOutOfBoundsException and, since its payload op caught nothing, the throwable escaped
     * and killed the engine rather than becoming a guest exception.
     *
     * Tiers, because the requirement and the failure mode come from different documents. The REQUIREMENT is
     * SPEC: [BC]'s fill-array-data text says the table "must contain no more elements than will fit in the
     * array". That it fails as an AIOOBE is [ART] ONLY -- `fill-array-data` has no JVM counterpart, so there
     * is nothing to inherit, and ART's message is "failed FILL_ARRAY_DATA; length=%d, index=%d" (whose second
     * number is the element COUNT, not a failing index). ART's own null check precedes its length check.
     *
     * "WITHOUT STORING ANYTHING" is the half the name claims and the first draft never checked, so it is
     * asserted below. It is also groundable rather than assumed: ART's FillArrayData
     * (runtime/entrypoints/entrypoint_utils.cc:175-194 -- fetched as raw source and base64-decoded during
     * Task 10, not read through a summarizing tool) does the length check at :183-189 and then a single
     * `memcpy` at :192, so the store is all-or-nothing -- [ART], and the reason its message can report a
     * count instead of an index.
     *
     * `testCaught`, not `testThrows`: the fixture below declares a Ljava/lang/Exception; handler, so the
     * AIOOBE is caught and there is no Outcome.Threw to find.
     *
     * **`testCaught`'s "nothing escaped" half is filtered to [exceptionType], so it is VACUOUSLY TRUE for an
     * escape of any other type** (`CoreTester.testCaught`, the `escaped` filter). The reliance is benign here
     * -- a spurious second fork would be caught by the same `Ljava/lang/Exception;` handler and would make
     * `terminatingConsensus(1)` ambiguous, failing `testCaught`'s own type assertion -- but that is a chain of
     * reasoning rather than an assertion, and the next author to copy this idiom onto a HANDLER-FREE fixture
     * would silently lose the protection. So the escape set is asserted outright below (review Minor 9).
     */
    @Test
    fun aPayloadLongerThanTheArrayThrowsWithoutStoringAnything() {
        val seed = { CoreState().apply { setRegisters(0, IntArray(2), "[I") } }
        CoreTester.testCaught(
            CLASS_NAME, "fillArrayDataIntWithCatch()V", seed(),
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            caughtInRegister = 1,     // `move-exception v1` -- v0 must keep the array; see the fixture
            // Added to the brief's call: the throw is DEFINITE, so the fall-through must be dead. Derived
            // below rather than trusted -- 31t is THREE code units, so the successor of the op at 0 is 3.
            unreachedAddress = 3,
        )
        // The second half of the name. Without these two lines the test is one assertion short of what it
        // claims, and a fill that stored the first two elements and THEN threw would pass it.
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataIntWithCatch()V", seed())
        assertEquals(3, graph.methodView.nextAddress(0), "fill-array-data is 31t = THREE code units")
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "NOTHING escaped, of any type -- said outright rather than left to testCaught's type-filtered half",
        )
        // Row 4's effect level, pinned here because this test already has the graph. A definite throw stores
        // nothing, so there is no effect to account for -- the same reasoning as row 1's.
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "a definite bounds exception stores nothing, so it declares no effect",
        )
        val trusted = graph.trusted()!!
        val ref = (trusted.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        val zero = Value.Known(0, VirtualType.of("I"))
        assertEquals(zero, array.element(0), "nothing stored: still the seed's 0")
        assertEquals(zero, array.element(1), "and the payload's second element did not land either")
    }

    /**
     * **Bug class: the mirror defect -- forking a provably INFEASIBLE throw, which invents a reachable
     * handler and tells a deobfuscator a dead `catch` is live.** This is the one test that makes
     * [FillArrayDataOp]'s `payloadElements.isNotEmpty()` guard load-bearing; without it the guard could be
     * deleted with 858 of 858 tests still green (measured in review as MX), and that guard is the *only*
     * thing standing between the three-way fork on the unprovable-array row and the defect this plan has
     * already shipped twice.
     *
     * **Why a zero-element payload is the input that proves it.** [BC]'s requirement is that the table
     * *"must contain no more elements than will fit in the array"*. An empty table satisfies that for EVERY
     * array length -- including 0 -- so the bounds exception is infeasible for this payload no matter what
     * the array register turns out to hold. Every other payload in this fixture has 2-5 elements, so for
     * each of them an unknown array might genuinely be too short and the fork is correct.
     *
     * Both halves are asserted, because the guard has to hold on the ambiguous row AND not accidentally
     * suppress a real fill:
     *  - unseeded (v0 is `Value.Top`, row 2's worst case) -> exactly `[NPE]` escapes, no bounds exception;
     *  - seeded with a ZERO-LENGTH tracked array -> row 5, the fill loop no-ops, nothing throws at all.
     *    That second half is the direct demonstration of "cannot overrun any array, including a length-0
     *    one", which is the guard's whole justification rather than a restatement of it.
     */
    @Test
    fun aZeroElementPayloadCannotOverrunAnyArraySoNoBoundsExceptionIsForked() {
        val unprovable = CoreTester.execute(CLASS_NAME, "fillArrayDataEmptyPayload()V")
        assertEquals(CompletionStatus.Complete, unprovable.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(unprovable),
            "an empty table fits every array, so the bounds throw is INFEASIBLE and must not be forked",
        )
        assertEquals(
            1, unprovable.nodePile(0).first().children.size,
            "one successor -- the fall-through; the NPE is a terminal, not a child",
        )

        val emptyArray = CoreTester.execute(CLASS_NAME, "fillArrayDataEmptyPayload()V", CoreState().apply {
            setRegisters(0, IntArray(0), "[I")
        })
        assertEquals(CompletionStatus.Complete, emptyArray.completion())
        assertTrue(
            CoreTester.escapingTypes(emptyArray).isEmpty(),
            "zero elements fit a zero-length array exactly, so nothing throws -- not even bounds",
        )
        assertEquals(
            SideEffectLevel.NONE, emptyArray.explored.sideEffectLevel(0),
            "and a tracked fill of nothing is still modelled exactly",
        )
    }

    /**
     * **Row 5's effect level.** The heap models a tracked fill exactly, but aliases make a non-empty fill
     * observable, so it is `WEAK`. `STRONG` remains reserved for writes whose target cannot be named.
     *
     * Both queries are asserted: the per-address one, and the whole-method one a strategy actually reads. The
     * unreached payload address contributes `NONE` rather than poisoning the method maximum, which is worth
     * pinning too -- an `UnsupportedOp` at a payload address would answer `STRONG`
     * (`ExecutionGraph.sideEffectLevel`'s unsupported rule), so this assertion also fails if the payload ever
     * starts being executed.
     */
    @Test
    fun aFillIntoATrackedArrayDeclaresAnObservableWeakEffect() {
        val graph = graph("fillArrayDataInt()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "an alias can observe the tracked fill")
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        assertEquals(
            SideEffectLevel.WEAK, trusted!!.methodSideEffectLevel(),
            "and the unreached payload address contributes NONE rather than poisoning the method maximum",
        )
    }

    /**
     * **The SPEC row no corpus fixture covers**, added rather than left unpinned: an array LONGER than the
     * payload is legal, only the leading elements are set, and *"the remainder is left alone"* -- **SPEC**
     * ([BC]). All ten migrated fixtures size their array exactly to their payload, so a fill that zeroed or
     * `Unknown`-ed the tail would pass every one of them.
     */
    @Test
    fun anArrayLongerThanThePayloadKeepsItsTrailingElements() {
        initial.setRegisters(0, IntArray(7), "[I")
        expected.setRegisters(0, intArrayOf(1, 2, 3, 4, 5, 0, 0), "[I")
        CoreTester.test(CLASS_NAME, "fillArrayDataInt()V", initial, expected)
    }

    /**
     * **The ordering claim, asserted as an EXCLUSION rather than an existence.** ART's null check precedes
     * its length check ([ART]), so a null array throws the NPE and the bounds exception is **not also
     * forked** -- even though this seed's five-element payload would not fit the `[I` a non-null array of
     * length 0 or 1 might be, because a null array has no length to compare against at all.
     *
     * `escapingTypes` and the child count are the only two queries that can see that. `testThrows` cannot:
     * its assertion is `matching.isNotEmpty()`, so "NPE before AIOOBE" written as `testThrows(NPE)` passes
     * whether or not the op also forks the AIOOBE -- the defect class that cost Plan 7 Task 9 a Critical
     * finding (appending a spurious fork killed **0 of 844** tests). Both mutations that would break the
     * order here ADD a fork, and both kill this test.
     */
    @Test
    fun aNullArrayThrowsNullPointerExceptionBeforeAnyBoundsExceptionAndStoresNothing() {
        initial.setRegisters(0, null, "[I")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is an outcome, not a truncation")
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph),
            "the NPE strictly PRECEDES the length check, so no bounds exception is forked",
        )
        assertEquals(0, graph.nodePile(0).first().children.size, "a definite throw has no successor to explore")
        assertEquals(3, graph.methodView.nextAddress(0), "fill-array-data is 31t = THREE code units")
        assertFalse(graph.wasReached(3), "and no fall-through")
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "nothing was stored, so there is no effect to account for",
        )
    }

    /**
     * The other spelling of Dalvik's null, which `Value.isNull` cannot see: **SPEC** -- [BC], *"In terms of
     * bitwise representation, `(Object) null == (int) 0`"*. `const/4 v0, 0x0` then `fill-array-data` is the
     * cheapest hostile input in the whole plan: it passes ART's verifier (the verifier skips its checks when
     * the register holds the null constant), throws NPE at runtime, and dexlib2 reports
     * `canThrow() == false` for the instruction.
     */
    @Test
    fun aDalvikZeroArrayThrowsTheSameDefiniteNullPointerException() {
        initial.setRegisters(0, 0, "I")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertEquals(0, graph.nodePile(0).first().children.size)
    }

    /**
     * The SAME definite NPE for a NON-zero primitive array register -- the half the zero-only predicate
     * dropped.
     *
     * **The reason a primitive-typed register is null has nothing to do with its VALUE**, which is what the
     * `isDalvikNull`-only predicate got wrong until Plan 7 Task 12's fix round. ART fetches an object operand
     * with `GetVRegReference`, `ShadowFrame` keeps a PARALLEL reference array, and every primitive write
     * clears the matching slot -- `SetVReg`/`SetVRegFloat`/`SetVRegLong`/`SetVRegDouble` all end in
     * `References()[i].Clear()` (`shadow_frame.h:190-228`), and ART states the invariant in its own prose at
     * `:401-408`: *"when a primitive is stored in vX, the second (reference) part of the array will be
     * null"*. **[ART]'s SWITCH interpreter**, and stable across releases (`android14-release` is identical).
     * So `const/4 v0, 0x5`
     * is exactly as null as `const/4 v0, 0x0`, and testing only for zero DROPPED a definite NPE -- an
     * unexplored feasible path on a `Complete` run, spec §2.2's worst outcome. See
     * [org.cf.smalivm.core.readsAsNullReference] for the full chain.
     *
     * **TIER, corrected 2026-08-04: this row is VERIFIER, not [ART].** ART's *default* interpreter is
     * `nterp`, which reads this operand out of the RAW dex-register array (`mterp/x86_64ng/array.S:125-132`)
     * and hands a non-zero value to `art_quick_handle_fill_data` as though it were an array pointer -- so the
     * two tiers CONTRADICT each other on exactly the value this test seeds. The **zero** spellings are
     * corroborated by BOTH tiers (`art::FillArrayData`'s own null check catches them either way), which is a
     * strengthening. **This expectation does not change**: a definite NPE is the sound direction and matches
     * the switch tier exactly. Only the citation changes. See `docs/DALVIK-SEMANTICS.md`'s *ART has TWO
     * interpreter tiers*.
     */
    @Test
    fun aNonZeroPrimitiveArrayThrowsTheSameDefiniteNullPointerException() {
        initial.setRegisters(0, 5, "I")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertEquals(0, graph.nodePile(0).first().children.size, "no fall-through: the throw is definite")
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "nothing was stored, so there is no effect to account for",
        )
    }

    /**
     * **Bug class: an unexplored feasible path on a run reporting `Complete`** -- spec §2.2's worst outcome.
     * With the array register `Unknown`/`Top`, THREE outcomes are feasible and all three are explored: the
     * array may be null (NPE), it may be non-null but shorter than the five-element payload (AIOOBE), or it
     * may be long enough (the fall-through). Dropping either throw would hide a path a real device takes.
     *
     * **This corrects the brief, which prescribed `[Continue, Throw(NPE)]` and omitted the AIOOBE.** The
     * omission is inconsistent with `APutOp`'s own unprovable-array row, which forks both for exactly this
     * reason, and it is the unsound direction: the AIOOBE is not provably infeasible here, so refusing to
     * explore it would leave a live `catch (ArrayIndexOutOfBoundsException)` looking dead.
     *
     * **`SideEffectLevel.STRONG`, and it is load-bearing rather than decorative.** `Frame.entryFrame` seeds
     * every parameter `Unknown` and no opcode in this build turns a parameter into a tracked array, so
     * `void f(int[] a) { … fill-array-data a, … }` -- the ordinary shape of a static initializer's array
     * fill in real code -- arrives HERE. Without the declaration such a method reports `Complete` *and*
     * `methodSideEffectLevel() == NONE`, the pair `AnalysisContracts`' KDoc says would let a strategy delete
     * a method whose whole observable behaviour is a write its caller can see. While the opcode was an
     * `UnsupportedOp` the engine answered `STRONG` and refused trust altogether, so landing it without this
     * would have replaced a sound refusal with an unsound answer.
     *
     * Unseeded rather than parameter-seeded on purpose: `fillArrayDataInt()V` is `.locals 1` with no
     * parameters, so v0 is genuinely unassigned and `frame.read(0)` is `Value.Top` -- the weakest thing the
     * lattice has, and the row's real worst case.
     *
     * **The ORDER in the list assertion below is INCIDENTAL and is NOT a semantic claim** -- unlike
     * [aNullArrayThrowsNullPointerExceptionBeforeAnyBoundsExceptionAndStoresNothing], where null-before-length
     * is a grounded [ART] fact. On an unprovable array neither exception is "first": both are speculative and
     * neither has been decided yet. List equality is used anyway because it is the shape that catches a
     * DROPPED or an ADDED fork, which set equality and `isNotEmpty()` both miss; if a later change reorders
     * the transition list for a good reason, update this expectation rather than reading it as a
     * fidelity regression.
     */
    @Test
    fun anUnprovableArrayForksBothFeasibleExceptionsAndDeclaresAStrongEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(graph),
            "both throws are feasible when nothing is known about the array, and both escape this fixture " +
                "(the ORDER is incidental -- see the KDoc; list equality is what catches a dropped or extra fork)",
        )
        assertEquals(
            1, graph.nodePile(0).first().children.size,
            "one SUCCESSOR -- the fall-through; the two escaping throws are terminals, not children",
        )
        assertTrue(graph.wasReached(3), "the fall-through is feasible too and must be explored")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the fill landed in an object the engine cannot name, so no heap state records it",
        )
    }

    /**
     * **Bug class: collapsing "not a tracked array" into "may be null"** and forking exceptions for a value
     * the engine just PROVED non-null. A `Known` non-zero raw with no tracked array behind it is
     * verifier-rejected input ([CON] B1) and therefore **VERIFIER -> the engine's choice**; the choice is
     * the same one `AGetOp`, `APutOp` and `ArrayLengthOp` make, and the four must stay consistent: NO NPE
     * (provably non-null) and NO AIOOBE (a bounds exception needs a length this input does not have).
     * A single `tracked == null` fork in any of the four invents a reachable handler.
     *
     * `STRONG` all the same: ART would `memcpy` the payload over whatever those bytes are, so the write
     * happened somewhere unmodellable. That is an EFFECT, not a value.
     *
     * **The array register is NOT degraded, and this is the falsifiable half.** `fill-array-data` writes
     * into the array OBJECT, never into the register, so v0's `Known` is provably unchanged; rewriting it
     * `Unknown` would discard a provable constant and account for nothing -- the ruling Task 9 made for
     * `aput` row 3, applied here.
     */
    @Test
    fun aProvablyNonNullNonArrayFillsNothingForksNothingAndDeclaresAStrongEffect() {
        initial.setRegisters(0, "not an array", "Ljava/lang/String;")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "a Known REFERENCE with a non-null raw is provably non-null and has no length, so both throws are infeasible",
        )
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor: the fall-through")
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0))
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so the single path returns")
        assertEquals(
            Value.Known("not an array", STRING), trusted!!.terminatingConsensus(0),
            "the register is provably unchanged -- the fill goes to memory, not to v0",
        )
    }

    /**
     * The op's static liveness declaration, on its own terms. `fill-array-data` **reads** the array
     * register and **assigns nothing**: the fill goes into the array object, so [FillArrayDataOp.execute]
     * calls `frame.assign` nowhere.
     *
     * **This corrects the brief, which prescribed `registersAssigned = setOf(arrayRegister)` plus a
     * reassignment on two rows.** It is the same claim Task 9 adjudicated for `aput` and rejected as a
     * maintainer ruling: on the unprovable row `Unknown(array.type)` reconstructs the identical lattice
     * point (and is not even constructible for `Value.Top`, which carries no type), and on the
     * non-tracked-array row it discards a provable constant. The engine being replaced *did* assign the
     * register, but only as the channel its payload op used to find its target -- the hidden control state
     * design decision 3 deletes, so mirroring it would copy the syntax and drop the reason.
     */
    @Test
    fun theFillReadsTheArrayRegisterAndAssignsNothing() {
        assertEquals(setOf(0), graph("fillArrayDataInt()V").registersRead(0))
        assertEquals(emptySet<Int>(), graph("fillArrayDataInt()V").registersAssigned(0))
    }

    private fun graph(methodDescriptor: String): ExecutionGraph =
        CoreTester.execute(CLASS_NAME, methodDescriptor, CoreState().apply { setRegisters(0, IntArray(5), "[I") })
}
