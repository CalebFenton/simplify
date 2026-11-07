package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * The seven `aget*` opcodes (ops 44-4a, format 23x = **TWO** code units, so the successor of an op at 0 is
 * address 2 and address 1 does not exist). Migrated from `org.cf.smalivm.opcode.AGetOpTest` -- all fourteen
 * of its tests -- plus the core cases the corpus never covered: the feasibility rows, the operand order, a
 * negative index, a definitely-null element, and the wide destination's second half.
 *
 * **Grounding.** [BC]'s entire specification for all fourteen `aget*`/`aput*` opcodes is one shared
 * sentence -- *"Perform the identified array operation at the identified index of the given array, loading
 * or storing into the value register."* -- and it names **no** exception, here or anywhere else for arrays
 * (the whole document names exactly two, neither of them an array exception). So every `NullPointerException`
 * and `ArrayIndexOutOfBoundsException` below is **INHERITED** from [JVMS] §6.5 `iaload`/`aaload`, legitimised
 * by [VER]'s *"generally the same set of checks as are described in The Java Virtual Machine Specification,
 * Second Edition, including the updates planned for the Third Edition"* -- quoted through the edition because
 * the bridge is same-DOCUMENT, not same-edition ([VER] names the Second/Third Editions; the [JVMS] citations
 * here are SE21, and §6.5's array rules are unchanged across those editions, which is why the inheritance
 * holds). Nothing here says "Dalvik throws". Full quotes and tiers:
 * `docs/DALVIK-SEMANTICS.md#aget--aput-ops-4451-23x`.
 *
 * **One op serves all seven, and that is an ENGINE CHOICE rather than an observation.** The suffix changes
 * exactly one thing for the core -- whether the destination is a wide pair -- because the element's value
 * and declared type come from the tracked array. For **verified** input that is free: the verifier proves
 * the variant and the component agree (**VERIFIER**, [CON] B1; [ART] `VerifyAGet`), and a mismatch is a
 * *hard* verification failure rather than a runtime exception. For **unverified** input -- this engine's
 * target workload -- there is no behavior to copy at all, so reading the element verbatim is *our* answer,
 * justified by spec §2.2. See [AGetOp]'s KDoc.
 */
class AGetOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    @BeforeEach
    fun setUp() {
        initial = CoreState()
        expected = CoreState()
    }

    @Test
    fun canGet() {
        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, 0, "I")
        expected.setRegisters(0, 0x42, "I")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
    }

    @Test
    fun canGetBoolean() {
        initial.setRegisters(0, booleanArrayOf(true), "[Z", 1, 0, "I")
        expected.setRegisters(0, true, "Z")
        CoreTester.test(CLASS_NAME, "getBoolean()V", initial, expected)
    }

    @Test
    fun canGetByte() {
        initial.setRegisters(0, byteArrayOf(0xe), "[B", 1, 0, "I")
        expected.setRegisters(0, 0xe.toByte(), "B")
        CoreTester.test(CLASS_NAME, "getByte()V", initial, expected)
    }

    @Test
    fun canGetChar() {
        initial.setRegisters(0, charArrayOf('a'), "[C", 1, 0, "I")
        expected.setRegisters(0, 'a', "C")
        CoreTester.test(CLASS_NAME, "getChar()V", initial, expected)
    }

    @Test
    fun canGetObject() {
        // The register tag is `Ljava/lang/String;` -- NOT an array descriptor -- for a host `String[]`, and
        // the op must not care, because the component comes from the OBJECT (`[Ljava/lang/String;`) and not
        // from the tag. Object over tag; see `HeapObject.TrackedArray`'s KDoc. This is the real witness for
        // that rule, and it is why no string surgery on the tag is needed: the oracle's
        // `array.type.replaceFirst("\\[", "")` (`opcode/AGetOp.kt:42`) is accidentally right here only
        // because there is no `[` to strip.
        val objectValue = "stringy"
        val objectArray = arrayOf(objectValue)
        val objectType = "Ljava/lang/String;"
        initial.setRegisters(0, objectArray, objectType, 1, 0, "I")
        expected.setRegisters(0, objectValue, objectType)
        CoreTester.test(CLASS_NAME, "getObject()V", initial, expected)
    }

    @Test
    fun canGetShort() {
        initial.setRegisters(0, shortArrayOf(0x42), "[S", 1, 0, "I")
        expected.setRegisters(0, 0x42.toShort(), "S")
        CoreTester.test(CLASS_NAME, "getShort()V", initial, expected)
    }

    @Test
    fun canGetUninitializedPrimitive() {
        initial.setRegisters(0, IntArray(1), "[I", 1, 0, "I")
        expected.setRegisters(0, IntArray(1)[0], "I")
        CoreTester.test(CLASS_NAME, "getUninitializedInt()V", initial, expected)
    }

    @Test
    fun canGetUnknownArray() {
        initial.setRegisters(0, CoreState.UNKNOWN, "[I", 1, 0, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "I")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
        // `get()V` has NO try block, so BOTH exceptions this unprovable array forks ESCAPE, and the run is
        // still `Complete` -- a guest throw is an OUTCOME, not a truncation. This test and
        // `canGetUnknownIndex` are the pair that proves Task 3's multi-escape `commit` works end to end: two
        // escaping throws from ONE node used to raise `IllegalArgumentException` out of `applyAll` -- outside
        // the host-failure boundary, so it killed the whole run rather than aborting a path.
        // `terminatingConsensus` reads RETURNING paths only, which is why the migrated expectation above is
        // exactly the normal path's value.
        //
        // The brief asked for `assertEquals(Complete, graph.completion())` here. That is UNFALSIFIABLE:
        // `CoreTester.test` already asserts exactly it, before comparing any register, so no input satisfies
        // one and not the other. Asserting the ESCAPE SET instead makes the claim above checkable, and it is
        // strictly stronger -- it pins that BOTH exceptions escape even though the index is KNOWN and in
        // range, i.e. that the unprovable-array row is reached BEFORE the index is looked at.
        val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            graph.outcomes.filterIsInstance<Outcome.Threw>()
                .mapNotNull { (it.exception as? Value.Known)?.type?.descriptor },
            "both throws escape a handler-free method, in transition order, even with a known in-range index",
        )
    }

    @Test
    fun canGetUnknownElement() {
        // Element-wise precision the oracle cannot express: it marks the WHOLE array unknown when one
        // element is (`opcode/APutOp.kt:40-42`). Here element 0 is `Unknown(I)` and element 1 is still
        // `Known(5, I)`, and reading index 0 answers `Unknown(I)` for free -- no special case. If a special
        // case ever seems necessary here, `CoreState.elementsOf` is what is wrong.
        initial.setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I", 1, 0, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "I")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
    }

    @Test
    fun canGetUnknownIndex() {
        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, CoreState.UNKNOWN, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "I")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
        // The other half of the Task 3 pair (see `canGetUnknownArray`): here the array is PROVABLY non-null,
        // so only the bounds exception forks, and it escapes `get()V`'s handler-free body. Asserted as the
        // escape SET rather than as `Complete` for the reason spelled out there -- a `Complete` assertion
        // duplicates `CoreTester.test`'s own gate and cannot fail. This one can: it is the MIGRATED seed
        // driving the row a spurious NPE fork would break, which is the gap the review measured (this test
        // stayed green under that mutation before this assertion existed).
        val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
        assertEquals(
            listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"),
            graph.outcomes.filterIsInstance<Outcome.Threw>()
                .mapNotNull { (it.exception as? Value.Known)?.type?.descriptor },
            "a provably non-null array forks ONLY the bounds exception",
        )
    }

    @Test
    fun canGetWide() {
        // DOCUMENTED SINGLE-FIXTURE DEPARTURE, and the only non-value edit in this file. The corpus seeded
        // the ARRAY register `"J"` (`opcode/AGetOpTest.kt:98` as of `805c425` -- that file is DELETED by the
        // commit this test arrives in, so the reference is to the blob, not to the tree).
        // `CoreTester.execute` assigns array registers
        // AFTER plain ones, as `Value.Known(ref, spec.type)` -- and `J` is wide, so `Frame.assign`'s wide
        // branch writes `WideHigh(0)` into slot 1 and destroys the index seeded one loop earlier. The index
        // becomes `Top` and the read answers `Unknown`, i.e. the test goes red on arrival.
        //
        // Why `"[J"` is not a values change: `"J"` on a register holding a `long[]` REFERENCE is not a loose
        // tag, it is an impossible one -- there is no such thing as a wide reference, and `Frame`'s wide-pair
        // bookkeeping is the code saying so (`CoreState.HostArray`'s KDoc states the same category error).
        // The ASSERTED expectation -- the element, `Known(0x10000000000L, "J")` -- is byte-identical, and the
        // array register's tag is never asserted anyway because `aget-wide v0, v0, v1` overwrites it.
        // Task 8's `canPutConstZeroNullObject` is the other departure of this kind.
        //
        // TWO OTHER FIXES WERE PROPOSED FOR THIS AND BOTH ARE REJECTED, recorded here so they are not
        // re-proposed. (Rescued by the whole-branch review's M8: they lived in a `CARRY-FORWARD.md` row that
        // `0b38fb5` deleted, while `STATUS.md` asserted the row had kept them -- leaving a pointer to nothing.
        // `b46d817`'s commit message is the only other surviving copy.)
        //  1. **"Assign the array register LAST."** Already the shipped order, and it is precisely what
        //     destroys the index: the array goes in after the index, so its wide branch overwrites slot 1.
        //     The other order wipes the array instead. Neither ordering has a slot for both.
        //  2. **A dedicated no-high-half seeding path.** Would work, and is rejected on principle rather than
        //     on cost: it buys byte-identical *input* by violating the invariant `Frame.assign` exists to
        //     maintain, so the harness would be able to build a state the engine cannot. Seeding `"[J"`
        //     changes an impossible tag into the right one and keeps the asserted value byte-identical, which
        //     is strictly less than either fix costs.
        initial.setRegisters(0, longArrayOf(0x10000000000L), "[J", 1, 0, "I")
        expected.setRegisters(0, 0x10000000000L, "J")
        CoreTester.test(CLASS_NAME, "getWide()V", initial, expected)
    }

    @Test
    fun canGetWithShortIndex() {
        // TWO expected registers, and `index.toInt()` is 0 -- so this asserts that v0 holds the element AND
        // that v1 still holds the `Short` index, i.e. that `aget` did not clobber its own index register.
        // The `Short` also needs `Coerce.toInt`: Dalvik's type tags are deliberately ambiguous, so an index
        // register may hold a Short, a Character or a Boolean under any integral tag.
        val index: Short = 0
        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, index, "S")
        expected.setRegisters(index.toInt(), 0x42, "I", 1, index, "S")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
    }

    /**
     * **INHERITED** ([JVMS] §6.5 `iaload`), and `testCaught` rather than `testThrows`: `getWithCatch()V`
     * (`ops/aget_test.smali:20-36`) declares a `Ljava/lang/Exception;` handler around the `aget`, an NPE is
     * an `Exception`, so the throw is CAUGHT, `move-exception v0` binds it, and the run RETURNS -- which
     * produces no `Outcome.Threw` at all. That is the shape the corpus's `testSimpleException` asserted and
     * the opposite of what `testThrows` asserts.
     *
     * Address **2**, not 1: `aget` is format 23x = TWO code units, so the `nop` sits at 2 and address 1 does
     * not exist. `wasReached(1)` would be silently `false` (an empty `nodePile(1)`), i.e. a green assertion
     * testing nothing.
     *
     * **No message is asserted, deliberately.** The corpus asserted none, and the contrast with
     * `ArrayLengthOp`'s NPE -- which *does* carry one -- is not accidental: there the corpus asserts the
     * string AND [ART] emits exactly it (`common_throws.cc:639-667`). [ART] also has per-opcode texts for
     * these two (*"Attempt to read from null array"* / *"…write to null array"*), so a later plan may adopt
     * them; that would be an improvement, not a fix, and nothing here is pinned in a way that blocks it.
     */
    @Test
    fun nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I")
        CoreTester.testCaught(
            CLASS_NAME, "getWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertThrowingAgetAssignedNothing()
    }

    /**
     * **INHERITED** ([JVMS] §6.5), and caught for the same reason as the NPE above -- an AIOOBE is an
     * `Exception` too, so this fixture's handler covers both and both migrate onto `testCaught`.
     *
     * **The message is a PINNED ENGINE CHOICE that deliberately does NOT match any runtime.** Nothing
     * *specifies* an AIOOBE message -- [BC] names no array exception at all, [JVMS] specifies no text -- and
     * the one real runtime string is [ART]'s `"length=%d; index=%d"`
     * (`runtime/common_throws.cc:131-134`, quoted verbatim in `docs/DALVIK-SEMANTICS.md`). So `"10"` below is
     * a divergence from ART, not a match: the corpus asserted no message, nothing downstream reads one, and
     * [RT] records that the text changed between runtimes anyway. Asserted only so it cannot drift silently
     * -- a test asserting an exception *message* is never evidence of Dalvik fidelity, per
     * `docs/DALVIK-SEMANTICS.md`'s "compare types, not strings".
     */
    @Test
    fun outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I")
        CoreTester.testCaught(
            CLASS_NAME, "getWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            message = "10",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertThrowingAgetAssignedNothing()
    }

    /**
     * The OTHER half of Dalvik's null, which `Value.isNull` cannot see, and which the brief requires be
     * tested alongside the null raw above. **SPEC** ([BC]: *"In terms of bitwise representation,
     * `(Object) null == (int) 0`"*), so `const/4 v0, 0x0` followed by `aget` is a DEFINITE NPE -- but the
     * register holds `Known(0, "I")`, whose raw is not `null`, so without `Value.isDalvikNull` it would fall
     * through to the provably-non-null rows, answer `Unknown(I)` without throwing, and a definite throw
     * would be silently dropped. Not in the corpus; `ArrayLengthOp` and Task 12 consume the same predicate.
     */
    @Test
    fun aKnownIntegralZeroArrayIsDalviksNullAndThrowsTheSameNullPointerException() {
        initial.setRegisters(0, 0, "I", 1, 0, "I")
        CoreTester.testCaught(
            CLASS_NAME, "getWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * The SAME definite NPE for a NON-zero primitive array register -- the half the zero-only predicate
     * dropped, and the more severe direction of Plan 7 Task 12's misreading: here the engine did not merely
     * invent a path, it *continued past* a definite NPE with an `Unknown` element and no throw edge at all.
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
     * `nterp`, which reads the array operand out of the RAW dex-register array (`mterp/x86_64ng/array.S:1-38`
     * for `aget`) and dereferences a non-zero value as an object pointer -- so the two tiers CONTRADICT each
     * other on exactly the value this test seeds, and `VerifyAGet` (`method_verifier.cc:5054-5055`) refuses
     * to link it besides. The **zero** spellings are corroborated by BOTH tiers, which is a strengthening.
     * **This expectation does not change**: a definite NPE is the sound direction and matches the switch tier
     * exactly. Only the citation changes. See `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*.
     */
    @Test
    fun aKnownNonZeroPrimitiveArrayIsAlsoDalviksNullAndThrowsTheSameNullPointerException() {
        initial.setRegisters(0, 5, "I", 1, 0, "I")
        CoreTester.testCaught(
            CLASS_NAME, "getWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * Object over tag, on the ONE row of this op where the component type is derived rather than carried:
     * an unprovable index answers `Unknown(component)`, and that component must come from the array
     * OBJECT's own type, never from the register's tag.
     *
     * The other rows cannot see this. Row 5 hands back `tracked.element(index)` verbatim, so its type was
     * already fixed at allocation; and every other loosely-tagged fixture in this file happens to make the
     * two derivations AGREE (`canGetObject`'s `Ljava/lang/String;` tag has no component, so
     * `tag.componentType ?: tag` returns the same descriptor the object would). Here they disagree on
     * purpose: the register is tagged `[Ljava/lang/Object;` while the host array really is a `String[]`, so
     * a tag-derived component would answer `Unknown(Ljava/lang/Object;)`.
     *
     * **Task 9 reuses this rule for the ArrayStore check**, where deriving a component from the tag when an
     * object was available is how a runtime type check passes for the wrong reason -- which is why it is
     * pinned here rather than left as a comment.
     */
    @Test
    fun theElementTypeOfAnUnprovableIndexComesFromTheArrayObjectNotTheRegisterTag() {
        initial.setRegisters(0, arrayOf("a", "b"), "[Ljava/lang/Object;", 1, CoreState.UNKNOWN, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/String;")
        CoreTester.test(CLASS_NAME, "getObject()V", initial, expected)
    }

    /**
     * A negative index is an ORDINARY bounds exception rather than a distinct one: ART compares unsigned
     * (`Array::CheckIsValidIndex`), so `index < 0` and `index >= length` are the same check and the same
     * throw (**INHERITED**, [JVMS] §6.5). Never covered by the corpus, and the bug class is a bare
     * `index >= length` test, which lets `-1` fall through to `element(-1)` and turns a guest AIOOBE into a
     * host `ArrayIndexOutOfBoundsException` -- i.e. `Aborted(HOST_FAILURE)` instead of an explorable path.
     */
    @Test
    fun aNegativeKnownIndexIsAnOrdinaryBoundsException() {
        initial.setRegisters(0, intArrayOf(1, 2, 3), "[I", 1, -1, "I")
        CoreTester.testCaught(
            CLASS_NAME, "getWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            message = "-1",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * An index register holding a computed `Float` is read by its 32 bits (`Coerce.toArrayIndex`), as ART reads
     * an index register: `2.9f` is `0x4039999A`, i.e. 1077516698, which is out of bounds for a 3-element array,
     * so the read definitely throws. A value conversion would read index 2 and answer `Known(0x33, I)` with no
     * bounds exception at all. **VERIFIER**-excluded input ([CON] B1; [ART] `VerifyAGet`).
     */
    @Test
    fun aFloatIndexIsReadByItsBits() {
        initial.setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 2.9f, "F")
        val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph))
        assertEquals(1, graph.outcomes.size, "index 1077516698 is definitely out of bounds: $graph")
    }

    /**
     * Pins `buildAGet`'s OPERAND ORDER for all THREE of format 23x's genuinely distinct registers
     * (`vAA` = destination, `vBB` = array, `vCC` = index).
     *
     * Bug class, and the one Plan 7's Task 6 review found live: every `aget*` in the corpus fixture is
     * `v0, v0, v1`, so destination and array collapse to one number and swapping
     * `registers.registerA`/`registerB` in `OpBuilder.buildAGet` leaves the entire suite green. Real code
     * emits `aget v2, v0, v1` constantly. `getWideIntoDisjointPair()V` is not a substitute: swapping ITS
     * array and index leaves `registersRead` the identical set `{0, 1}`.
     *
     * All three transpositions are visible here, each for its own reason -- which is why the assertion is on
     * VALUES and not on liveness:
     *  - dest <-> array (`aget v0, v2, v1`): v2 is unseeded, so the array is `Top`, the read answers
     *    `Value.Top` into v0, and the array register is clobbered. v0 and v2 both wrong.
     *  - dest <-> index (`aget v1, v0, v2`): the index comes from unseeded v2, so it is unprovable and the
     *    read answers `Unknown(I)` into v1. v1 and v2 both wrong.
     *  - array <-> index (`aget v2, v1, v0`): the "array" is then `Known(2, "I")` -- a non-`HeapRef` raw, so
     *    the provably-non-null-but-not-an-array row answers `Unknown(I)` without throwing. v2 wrong. Note
     *    that this transposition is SILENT in every other respect: no crash, no throw, and `registersRead`
     *    is the same set either way.
     *
     * `intArrayOf`, not `arrayOf`: this is the only test in the file to assert the ARRAY register's surviving
     * value, so it is the only one to reach `CoreTester`'s runtime-type-assignable-to-declared-type check,
     * and a host `Integer[]` is not assignable to `[I` (primitive arrays are invariant, [JLS] §4.10.3).
     */
    @Test
    fun theThreeOperandsAreNotInterchangeable() {
        initial.setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 2, "I")
        expected.setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 2, "I", 2, 0x33, "I")
        CoreTester.test(CLASS_NAME, "getWithThreeDistinctRegisters()V", initial, expected)
    }

    /**
     * Bug class, and the reason Task 3 exists: an unprovable array AND an unprovable index make three
     * successors, two of them escaping a method with no try block. Before Task 3 this raised
     * IllegalArgumentException out of `applyAll` -- outside the host-failure boundary, so it killed the
     * run rather than aborting a path. Asserted by exception TYPE, not just count, because "two throws"
     * would also be satisfied by two NPEs.
     */
    @Test
    fun anUnprovableArrayForksBothExceptionsAndTheNormalRead() {
        val graph = CoreTester.execute(CLASS_NAME, "get()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[I", 1, CoreState.UNKNOWN, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "guest throws are not truncations")
        // Address 2, not 1: `aget` is 23x = TWO code units, so `get()V`'s return-void is at 2 and address 1
        // does not exist. `wasReached(1)` would be silently false -- nodePile(1) is empty -- and this
        // assertion would fail for a reason that has nothing to do with forking.
        assertTrue(graph.wasReached(2), "the normal read must still be explored")
        val thrown = graph.outcomes.filterIsInstance<Outcome.Threw>()
            .mapNotNull { (it.exception as? Value.Known)?.type?.descriptor }
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            thrown,
            "both exceptions are feasible, in transition order",
        )
    }

    /**
     * The other direction, and the one "always fork" would get wrong: a PROVABLY non-null array with an
     * unknown index forks only the AIOOBE. Forking the NPE too would tell a deobfuscator a
     * catch(NullPointerException) handler is reachable on a path Dalvik cannot take.
     */
    @Test
    fun aProvableArrayWithAnUnknownIndexForksOnlyTheBoundsException() {
        val graph = CoreTester.execute(CLASS_NAME, "get()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 2), "[I", 1, CoreState.UNKNOWN, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val thrown = graph.outcomes.filterIsInstance<Outcome.Threw>()
            .mapNotNull { (it.exception as? Value.Known)?.type?.descriptor }
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), thrown)
    }

    /**
     * A zero-length array cannot be read at ANY index, so an unprovable index there is a **definite** bounds
     * exception and the fall-through is provably infeasible (Plan 7 Task 8 fix round 1, review finding F4).
     * Forking it keeps a dead block alive -- the milder half of the invents-a-reachable-path mirror defect:
     * not as bad as inventing a live `catch`, but still a path Dalvik cannot take.
     *
     * Added alongside the identical guard in `APutOp` rather than after it, because the array ops must agree
     * about feasibility; `APutOpTest.aZeroLengthArrayCannotBeStoredIntoAtAnyIndexSoTheFallThroughIsInfeasible`
     * is the twin. `array-length` needs no equivalent -- it reads no index.
     */
    @Test
    fun aZeroLengthArrayCannotBeReadAtAnyIndexSoTheFallThroughIsInfeasible() {
        val graph = CoreTester.execute(CLASS_NAME, "get()V", CoreState().apply {
            setRegisters(0, intArrayOf(), "[I", 1, CoreState.UNKNOWN, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"),
            graph.outcomes.filterIsInstance<Outcome.Threw>()
                .mapNotNull { (it.exception as? Value.Known)?.type?.descriptor },
        )
        assertEquals(0, graph.nodePile(0).first().children.size, "no index can be in range, so nothing continues")
    }

    /**
     * Bug class: collapsing "not a tracked array" into "may be null" and forking two exceptions for a value
     * the engine just PROVED non-null. A `Known` **reference** with a non-null raw is provably non-null, so
     * the NPE is infeasible -- and it is the TYPE that makes it provable, not the value: a primitive-typed
     * `Known` reads as null on ART whatever it holds and is diverted to the definite-NPE row above (Plan 7
     * Task 12's fix round; this KDoc used to say "a `Known` non-zero raw", which was the misreading). The
     * seed below is a `Ljava/lang/String;`, so the claim holds as stated, and a bounds exception needs a length this input does not have -- so both throws are
     * infeasible and `[Continue]` alone is correct. `frame.arrayAt(ref)` also answers null for a `HeapRef`
     * pointing at a `GuestThrowable`, which lands in this same branch for a definitely-non-null value; that
     * is why it must not share a fork with the `Unknown` row. Same trap as
     * `ArrayLengthOpTest.aKnownNonArrayIsProvablyNonNullSoItsLengthIsUnknownAndNothingThrows`.
     *
     * **VERIFIER -> engine's choice.** No verified dex reaches this row ([CON] B1), and ART does
     * `ObjPtr<ArrayType>::DownCast(a)` with no array-type check and then a mis-scaled stride -- it reads the
     * wrong bytes, which is not a behavior to reproduce. `Unknown` is the total answer, justified by spec
     * §2.2 and never by "ART does this".
     *
     * The element type falls back to the register tag (`tag.componentType ?: tag`) because there is no
     * object to ask -- a non-array tag has no component, so the tag itself is the least-wrong answer
     * available. Uninformative on purpose.
     */
    @Test
    fun aKnownNonArrayIsProvablyNonNullSoTheElementIsUnknownAndNothingThrows() {
        initial.setRegisters(0, "not an array at all", "Ljava/lang/String;", 1, 0, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/String;")
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
        val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor -- both throws are infeasible")
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "a Known REFERENCE with a non-null raw is provably non-null and has no length, so neither throw is feasible",
        )
    }

    /**
     * The `Value.Top` corner of the unprovable-array row: an array register with **no type at all**. The
     * result must be `Top`, not an `Unknown` of some fabricated type -- there is nothing to derive a
     * component from, and inventing `Ljava/lang/Object;` would claim knowledge the engine does not have.
     *
     * Reachable from ordinary smali rather than only from a seed: `get()V` is `static ()V`, so an unassigned
     * v0 really is `Top`.
     *
     * Asserted through `frame.peek`, which is the only query that can tell "assigned `Top`" from "never
     * assigned" -- `Frame.read` and `ExecutionGraph.consensus` both answer `Value.Top` for an unassigned
     * register, so either would pass on an op that assigned nothing at all.
     */
    @Test
    fun anArrayRegisterWithNoTypeAtAllYieldsTopRatherThanAFabricatedType() {
        val graph = CoreTester.execute(CLASS_NAME, "get()V", CoreState())
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Top, graph.nodePile(0).first().frame.peek(0),
            "no type at all means Top, and `peek` is what distinguishes that from unassigned",
        )
        assertEquals(
            2, graph.outcomes.filterIsInstance<Outcome.Threw>().size,
            "a Top array is no more provable than an Unknown one, so both exceptions still fork",
        )
    }

    /**
     * Bug class: reading `aget-object` from a fresh reference array must give DEFINITELY null, not
     * `Unknown`. `Known(null, T)` is a provable fact (spec §4.1) and answering `Unknown` would discard it.
     *
     * It is also the SEMANTICS half of what Task 13's ValueKind.NULL guard needs -- but NOT the fixture
     * half, and the first draft conflated them. The differential sweep runs `Fixtures.newCoreSupported`
     * only; `ops/aget_test.smali` is not in it and never will be. So this test does not satisfy that guard.
     *
     * **Task 13 added the fixture half separately, as this comment asked:**
     * `Ldifferential/smoke;->nullElementFromFreshArray()`, registered as
     * `smoke-null-element-from-fresh-array`, which is what widened
     * `NewCoreDifferentialTest.theSweepComparesRealValuesAndCannotPassVacuously`'s `producible` set to all
     * four kinds. Both survive: that one asserts the PROJECTION on both engines, this one asserts the value.
     */
    @Test
    fun readingAFreshReferenceArrayGivesDefinitelyNull() {
        val expected = CoreState().apply {
            setRegister(0, Value.Known(null, VirtualType.of("Ljava/lang/Object;")))
        }
        CoreTester.test(CLASS_NAME, "getObjectFromFreshArray()V", CoreState(), expected)
    }

    /**
     * Bug class: the WIDE destination naming only its low half, which
     * `ExecutionGraph.availableRegisters` would then offer as free scratch while the pair is live --
     * silently, because Frame.peek answers null for a high half. Op.destinationRegisters is the single
     * expression of the rule; this pins that `aget-wide` routes through it with isWide = true.
     *
     * DRIVEN BY A DISJOINT FIXTURE ON PURPOSE. The first draft used `getWide()V`, which is
     * `aget-wide v0, v0, v1`: the destination pair is {0,1} and the OPERANDS are also {0,1}, so
     * `assertEquals(setOf(0,1), registersAssigned(0))` is satisfied by an implementation that returns
     * `registersRead` -- a test that cannot fail for the reason it is named. `getWideIntoDisjointPair()V`
     * separates them, and the second assertion pins the separation so a later fixture edit cannot quietly
     * re-overlap them.
     */
    @Test
    fun agetWideOwnsBothHalvesOfItsDestination() {
        val graph = CoreTester.execute(CLASS_NAME, "getWideIntoDisjointPair()V", CoreState().apply {
            setRegisters(0, longArrayOf(7L, 8L), "[J", 1, 0, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(setOf(3, 4), graph.registersAssigned(0), "the wide destination owns v3 AND v4")
        assertEquals(setOf(0, 1), graph.registersRead(0), "and the operands are a disjoint set, so this cannot pass by confusing the two")
    }

    /**
     * The corpus's *"…AndAssignsNoRegisters"* claim, kept honestly rather than transcribed.
     *
     * **BRIEF DEFECT, the same one Task 6 resolved as D1.** The brief asked for
     * `assertEquals(emptySet<Int>(), graph.registersAssigned(0))`. That expectation is impossible:
     * `ExecutionGraph.registersAssigned(address)` returns `op.registersAssigned`, which is STATIC -- decoded
     * from the instruction and identical on every path, including a throwing one -- so for `aget v0, v0, v1`
     * it is `{0}` and always will be. The oracle's claim was about the FRAME
     * (`node.state.registersAssigned.size == 0`, `Tester.kt:187`), and that cannot be migrated literally
     * either: `CoreTester` seeds through `Frame.assign`, which records the seeded register where the
     * oracle's `pokeRegister` did not.
     *
     * So both halves are kept, split by what each actually asserts:
     *  - the static width declaration, which is a real and discriminating claim (an `isWide = true` here
     *    would answer `{0, 1}`) but is NOT the oracle's;
     *  - the oracle's actual claim, re-expressed where it is observable: the destination register still
     *    holds the seeded ARRAY after the throwing node ran, so the throw happened before any assignment.
     *    An op that assigned `Unknown` first would leave an element-typed value there instead.
     */
    private fun assertThrowingAgetAssignedNothing() {
        val graph = CoreTester.execute(CLASS_NAME, "getWithCatch()V", initial)
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow aget's result owns exactly one register")
        val destinationAfter = graph.nodePile(0).first().frame.peek(0)
        // Two independently falsifiable lines rather than one compound condition, so a failure names which
        // half broke. The TYPE is what an assign-then-throw mutation changes (`[I` -> `I`, since both callers
        // seed the array register `[I`); the raw shape is what a "throw but write the element anyway"
        // mutation changes.
        assertEquals(
            VirtualType.of("[I"), (destinationAfter as? Value.Known)?.type,
            "a throwing aget must leave its destination holding the array, not an element: $destinationAfter",
        )
        val raw = (destinationAfter as Value.Known).raw
        assertTrue(
            raw == null || raw is HeapRef,
            "the surviving value must still be the seeded array reference (or its null), not an element: $raw",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Laget_test;"
    }
}
