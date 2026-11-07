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
import org.cf.smalivm.core.agreesWith
import org.cf.smalivm.core.join
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `filled-new-array` (op 24, **35c**) and `/range` (op 25, **3rc**) -- both THREE code units, so in every
 * fixture here the op is at 0, the `move-result-object` at **3**, and `return-void` at **4**. Derived from
 * the fixture with a throwaway `MethodView` print before any address was written down, then re-asserted in
 * the tests themselves through `methodView.nextAddress(0)`.
 *
 * Migrated from `org.cf.smalivm.opcode.FilledNewArrayTest`'s four behavioral integration tests, with
 * unchanged expected values. Additional behavioral assertions recovered from the former mock-based
 * tests are in the "restored from the mockk nests" section below.
 *
 * **Where the value is read from changed, and the value did not.** The corpus asserted through
 * `ExecutionState.RESULT_REGISTER`, a NEGATIVE pseudo-register in the engine's collapsed register map, which
 * `CoreTester.test` refuses outright with a message telling the migrator to *"assert the register a
 * `move-result`/`move-exception` moved the slot INTO instead"*. The existing two fixture methods have no
 * `move-result-object` at all, so new `…ThenMoveResult` methods were added rather than the existing two
 * edited. Per the Global Constraints, a change in where the harness reads a value from is not a change to the
 * value.
 *
 * **[BC] is unusually explicit for this opcode and three of its clauses reshape the tests:**
 * *"Construct an array of the given type and size, filling it with the supplied contents. The type must be an
 * array type. The array's contents must be single-word (that is, no arrays of long or double, but
 * **reference types are acceptable**)."*
 *  - so `[Ljava/lang/String;` is **legal** -- [aReferenceComponentArrayStoresItsElementsVerbatim];
 *  - so `[J`/`[D` are **SPEC-forbidden** and decode to `UnsupportedOp`
 *    (`core/OpBuilderTest.aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure`);
 *  - and "never throws" is false: see [FilledNewArrayOp]'s KDoc for the per-exception tiering, with the
 *    JVM-counterpart question answered for each. Two rows throw here
 *    ([anUnloadableComponentTypeThrowsNoClassDefFoundError],
 *    [aSingleWordPrimitiveComponentArtDoesNotImplementThrowsInternalError]) and four named exceptions are
 *    **unreachable** ([nothingIsForkedBecauseThisOpHasNoArrayIndexOrCountToBeWrong]).
 *
 * **`filledNewArray()V` and `filledNewArrayRange()V` are deliberately left untouched and unasserted.** They
 * are the corpus's original two methods, they have no `move-result-object`, and every value they build lives
 * only in the result slot -- which no graph query exposes. They now EXECUTE rather than abort, so they are no
 * longer inert, but nothing here reads them: the `…ThenMoveResult` pair carries the same instructions where
 * the result is observable.
 */
class FilledNewArrayOpTest {
    private lateinit var expected: CoreState
    private lateinit var initial: CoreState

    companion object {
        private const val CLASS_NAME = "Lfilled_new_array_test;"
        private val INT = VirtualType.of("I")
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }

    @BeforeEach
    fun setUp() {
        expected = CoreState()
        initial = CoreState()
    }

    // ----------------------------------------------------------------------------------------------
    // The four migrated tests. Values byte-identical to the corpus; the changes are TestState ->
    // CoreState, Tester.test -> CoreTester.test, and the read moving from the result pseudo-register
    // to the register `move-result-object` puts it in.
    // ----------------------------------------------------------------------------------------------

    @Test
    fun testIntegerParametersCreatesArrayWithExpectedContents() {
        val elements = intArrayOf(2, 3, 5)
        initial.setRegisters(0, elements[0], "I", 1, elements[1], "I", 2, elements[2], "I")
        expected.setRegisters(3, elements, "[I")
        CoreTester.test(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial, expected)
    }

    @Test
    fun testNewArrayRangeWithIntegerParametersCreatesArrayWithExpectedContents() {
        val elements = intArrayOf(2, 3, 5, 7, 11, 13)
        initial.setRegisters(
            0, elements[0], "I", 1, elements[1], "I", 2, elements[2], "I", 3, elements[3], "I", 4,
            elements[4], "I", 5, elements[5], "I"
        )
        expected.setRegisters(6, elements, "[I")
        CoreTester.test(CLASS_NAME, "filledNewArrayRangeThenMoveResult()V", initial, expected)
    }

    /**
     * The `Short` case, and the one migrated test that pins the coercion: Dalvik's type tags are ambiguous,
     * so a register tagged `I` legitimately holds a `Short`, and the array's slots must still be `Int`s.
     * That is `Coerce.toInt`, **ORACLE** parity with `castValue`.
     */
    @Test
    fun testShortParametersCreatesArrayWithExpectedContents() {
        val elements = arrayOf<Short>(2, 3, 5)
        val intElements = intArrayOf(2, 3, 5)
        initial.setRegisters(0, elements[0], "S", 1, elements[1], "S", 2, elements[2], "S")
        expected.setRegisters(3, intElements, "[I")
        CoreTester.test(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial, expected)
    }

    /**
     * **Precision improvement #3, and the one migrated value that changed** -- authorized by the Global
     * Constraints' third sanctioned exception and registered by Task 13 alongside Task 8's two. The engine
     * being replaced discarded the WHOLE array as one `Unknown("[I")` the moment any element was unknown
     * (`foundUnknown` short-circuited the result); this one keeps every provable slot and makes only the
     * unprovable slot `Unknown(I)`. The array itself is still a fully tracked object of the declared type.
     *
     * Asserted through the heap rather than through `CoreTester.test`, because `CoreState.HostArray` cannot
     * express "a tracked array two of whose three elements are known" as a host array plus a tag: the sentinel
     * form `arrayOf(2, 3, CoreState.UNKNOWN)` would make `trueTypeOf` fall back to the declared tag, which is
     * right, but the element loop is clearer read directly.
     */
    @Test
    fun testUnknownElementParameterKeepsEveryProvableSlot() {
        initial.setRegisters(0, 2, "I", 1, 3, "I", 2, CoreState.UNKNOWN, "I")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val array = trackedArray(graph, register = 3)
        assertEquals(VirtualType.of("[I"), array.type, "the array's own type comes from the instruction")
        assertEquals(
            listOf(Value.Known(2, INT), Value.Known(3, INT), Value.Unknown(INT)), array.elements(),
            "per-SLOT unknown-ness, not a whole-array Unknown",
        )
    }

    // ----------------------------------------------------------------------------------------------
    // Restored from the mockk nests, per the adjudication note. Each of these covers a decode shape or
    // a value the four migrated tests above cannot reach; each has a named mutation in the note.
    // ----------------------------------------------------------------------------------------------

    /**
     * The `Instruction35c` register-count shapes the corpus covered only through mocks. `filledNewArray()V`
     * names three registers, so it exercises `registerC`/`registerD`/`registerE` and NOTHING else: a decoder
     * that swapped `registerF` and `registerG`, or that dropped the five-register case, was invisible to every
     * behavioral test in the suite. All five counts in one test because they are one claim about one decode
     * path -- the note's rows `UnitTestFilledNewArray#1..4,6`.
     *
     * The elements are deliberately **distinct and asymmetric** (`101, 102, 103, 104, 105`), not the mockk
     * tests' `42, -42, 42, -42, 42`: a palindromic seed cannot witness a register-order defect, which is the
     * whole point of the rows being restored. The mockk seeds' own values carried no other claim -- they were
     * arbitrary `Number`s -- so nothing byte-identical is being departed from here.
     */
    @Test
    fun everyInstruction35cRegisterCountDecodesItsOwnRegistersInOrder() {
        assertDecodes("filledNewArrayOne()V", intArrayOf(101), movedInto = 1)
        assertDecodes("filledNewArrayTwo()V", intArrayOf(101, 102), movedInto = 2)
        assertDecodes("filledNewArrayThenMoveResult()V", intArrayOf(101, 102, 103), movedInto = 3)
        assertDecodes("filledNewArrayFour()V", intArrayOf(101, 102, 103, 104), movedInto = 4)
        assertDecodes("filledNewArrayFive()V", intArrayOf(101, 102, 103, 104, 105), movedInto = 5)
    }

    /**
     * The note's row `UnitTestFilledNewArrayRange#2` — an unprovable element in the **range** form. Verdicted
     * a structural duplicate of [testUnknownElementParameterKeepsEveryProvableSlot] (`stored` is called from
     * one site and `isRange` is read by `toString` alone, so no is-range branch can make the two differ), and
     * **asserted anyway rather than left as an argument**: four lines turn "no mutation could distinguish
     * these" from a claim into a measurement, and a structural argument is only as good as the structure it
     * describes, which a later refactor may change without noticing this note.
     *
     * Mutation M6 — restore the oracle's whole-array `Unknown` on any unprovable element — kills this
     * alongside its non-range twin, which is exactly the "duplicate" verdict made visible.
     */
    @Test
    fun theRangeFormHandlesAnUnprovableElementTheSameWayTheNonRangeFormDoes() {
        initial.setRegisters(0, 2, "I", 1, CoreState.UNKNOWN, "I", 2, 5, "I", 3, 7, "I", 4, 11, "I", 5, 13, "I")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayRangeThenMoveResult()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(2, null, 5, 7, 11, 13).map { if (it == null) Value.Unknown(INT) else Value.Known(it, INT) },
            trackedArray(graph, register = 6).elements(),
            "per-SLOT unknown-ness in the range form too",
        )
    }

    // The `Instruction3rc` decode shape is NOT restored as a test, and that is a mutation-backed verdict
    // rather than a judgement about form. The note's `UnitTestFilledNewArrayRange#1` value half is a
    // **duplicate**: a restored version of it was written, run, and then deleted once two mutations --
    // `startRegister + it + 1` and deleting the `is RegisterRangeInstruction` branch outright -- killed
    // `testNewArrayRangeWithIntegerParametersCreatesArrayWithExpectedContents` too. That migrated test seeds
    // SIX DISTINCT ascending values (`2, 3, 5, 7, 11, 13`) into `v0 .. v5` and asserts them in order, which is
    // strictly stronger than the mockk row's alternating `42, -42, …`, so nothing the deleted row could catch
    // is missed. Its `toString` half IS uncovered and is restored in `core/OpBuilderTest`.

    /**
     * `Short`, `Byte` and `Int` mixed in one instruction -- the note's row
     * `UnitTestFilledNewArray#5` (`testThreeNumbersGivesExpectedArray`), kept because its seed is what its
     * form is not: three DIFFERENT boxed classes in one array, where
     * [testShortParametersCreatesArrayWithExpectedContents] uses three `Short`s. A coercion applied only to
     * the first element, or one that read the register's declared TAG instead of the raw's class, is green
     * under a homogeneous seed and red here. Values are the mockk test's own: `42`, `35`, `10`.
     */
    @Test
    fun aMixtureOfBoxedIntegralClassesAllLandAsInts() {
        val short: Short = 42
        val byte: Byte = 35
        initial.setRegisters(0, short, "S", 1, byte, "B", 2, 10, "I")
        expected.setRegisters(3, intArrayOf(42, 35, 10), "[I")
        CoreTester.test(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial, expected)
    }

    // The two mockk `toString` rows, plus the five-register range row that has no corpus counterpart, are
    // restored in `core/OpBuilderTest.opsRenderTheirOperandsInTheirDeclaredOrder` rather than here -- that is
    // where Plan 4 put the three `toString` rows its own adjudication restored, and it is one test per
    // rendering shape across every op family instead of one per family. See the adjudication note's rows
    // `UnitTestFilledNewArray#7` and `UnitTestFilledNewArrayRange#1`.

    // ----------------------------------------------------------------------------------------------
    // New coverage: the rows the corpus could not reach, because the claims that ruled them out
    // ("anything but [I fails DEX verification", "never throws") are both false.
    // ----------------------------------------------------------------------------------------------

    /**
     * **SPEC** -- [BC]: the contents *"must be single-word (that is, no arrays of long or double, but
     * **reference types are acceptable**)"*. So a `[Ljava/lang/String;` is legal, and its elements are stored
     * **verbatim**: a reference is not an int, and `Coerce.toInt` on one is either a crash or a lie. [ART]
     * agrees in both interpreter tiers -- the switch interpreter's `DoFilledNewArray` non-`int` branch fills
     * with `GetVRegReference`, the reference itself, and nterp's own `DoFilledNewArray`
     * (`mterp/nterp.cc:751-752`) casts the raw register straight to `mirror::Object*`, which for a register
     * genuinely holding a reference is that same reference.
     *
     * The engine being replaced could not express this at all: it hardcoded `[I` as the result type and
     * `toInt()` as the element conversion, so `filled-new-array {v0, v1}, [Ljava/lang/String;` produced an
     * `[I` of two zeros (`value.raw is Number` is false for a `String`, so both slots went down its
     * unknown path).
     *
     * `[[I` takes this same row -- `VirtualType.isReference` counts an array as a reference -- and is not
     * given its own fixture because there is no second code path for it.
     *
     * **"Verbatim" is the raw of a register that really holds a REFERENCE.** A register whose last write was
     * primitive holds no reference at all, and that row is
     * [aPrimitiveElementUnderAReferenceComponentIsTheNullDalvikReads] -- the whole-branch review's C1.
     */
    @Test
    fun aReferenceComponentArrayStoresItsElementsVerbatim() {
        initial.setRegisters(0, "one", "Ljava/lang/String;", 1, "two", "Ljava/lang/String;")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val array = trackedArray(graph, register = 2)
        assertEquals(
            VirtualType.of("[Ljava/lang/String;"), array.type,
            "the result's type is the INSTRUCTION's, not a hardcoded [I",
        )
        assertEquals(
            listOf(Value.Known("one", STRING), Value.Known("two", STRING)), array.elements(),
            "the raws are the references themselves -- no coercion",
        )
    }

    /**
     * **`new String[]{null}` -- verifier-VALID, ordinary `d8` output, and the shape this opcode exists for.**
     * The whole-branch review's C1: [FilledNewArrayOp] read a primitive register's raw and stored it under the
     * array's *reference* component, where a device stores `null`.
     *
     * **[ART], fetched raw (`?format=TEXT` piped through `base64 -d`, `refs/heads/main`) and counted with
     * `awk` line numbering.** `DoFilledNewArray`'s non-`int` branch is
     * `interpreter_common.cc:1463-1464`:
     * `object_array->SetWithoutChecks</*kTransactionActive=*/ false, /*kCheckTransaction=*/ false>(i,
     * shadow_frame.GetVRegReference(src_reg));` -- so the element is whatever
     * [org.cf.smalivm.core.readsAsNullReference]'s verified chain says `GetVRegReference` yields, which for a
     * register whose last write was primitive is **`nullptr`**. `const/4 v0, 0x0` is Dalvik's only spelling of
     * a null literal, so the *ordinary* input takes this row.
     *
     * **All four seeds below are one row, not four**, because `readsAsNullReference` is true for every
     * primitive-typed `Value.Known` regardless of its raw -- the reference half of the `ShadowFrame` slot was
     * cleared by the primitive write, and the value is irrelevant. Only the first is verifier-valid; the other
     * three are what an obfuscator writes.
     *
     * ⚠️ **They are one row in this ENGINE, but NOT one row of evidence, and the sentence that used to end
     * this paragraph -- *"they agree with a device for the same one reason rather than by three separate
     * accidents"* -- over-claimed. Corrected 2026-08-04.** nterp is ART's default interpreter and it does not
     * use `interpreter_common.cc` for this opcode at all: `mterp/x86_64ng/array.S:134-148` calls
     * `NterpFilledNewArray` (`mterp/nterp.cc:758-764`) -> nterp's own `DoFilledNewArray` (`:683-756`), whose
     * reference fill at `:751-752` is `SetWithoutChecks(i,
     * reinterpret_cast<mirror::Object*>(regs[src_reg]))` -- the **raw** register cast to a pointer. So:
     *  - seed 1, `0 to "I"` (bits zero): `reinterpret_cast<mirror::Object*>(0)` is `nullptr`, so **both tiers
     *    agree** and the verifier-VALID row is doubly grounded. A strengthening.
     *  - seeds 2-4, non-zero primitives: nterp stores the integer **as a reference** into the object array.
     *    The two tiers **contradict** each other, so those rows are **VERIFIER** tier -- this project's spec
     *    §2.2 choice on input `VerifyFilledNewArray` rejects -- and **not** [ART].
     *
     * **No assertion below changes**: `null` is the sound direction, it matches the switch tier exactly, and a
     * corrupt reference is not a lattice point. Only the citation changes. See
     * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*.
     *
     * **Why this is a wrong CONFIDENT answer and not merely an imprecise one** (spec §2.2's cardinal
     * prohibition). Before the fix the element was `Known(0 /*Integer*/, Ljava/lang/String;)`, while
     * `aput-object` -- the sibling store, changed in Task 9's review finding m2 to stop producing exactly that
     * lattice point -- answers `Known(null, Ljava/lang/String;)` for the identical input.
     * [org.cf.smalivm.core.agreesWith] returns **false** between those two spellings, so a `join` over one
     * path that built the array with `filled-new-array` and one that built it with `aput-object` degraded to
     * `Unknown` and **the constant fold was lost**. That is pinned by
     * [theTwoWaysOfSpellingANullElementAgreeSoAJoinKeepsTheFold].
     */
    @Test
    fun aPrimitiveElementUnderAReferenceComponentIsTheNullDalvikReads() {
        for ((raw, tag) in listOf(0 to "I", 5 to "I", 5.9f to "F", true to "Z")) {
            initial = CoreState()
            initial.setRegisters(0, raw, tag, 1, raw, tag)
            val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion())
            val array = trackedArray(graph, register = 2)
            assertEquals(
                VirtualType.of("[Ljava/lang/String;"), array.type,
                "the result's type is still the INSTRUCTION's",
            )
            assertEquals(
                listOf(Value.Known(null, STRING), Value.Known(null, STRING)), array.elements(),
                "GetVRegReference yields nullptr for a register last written as $tag=$raw",
            )
        }
    }

    /**
     * **The consequence C1 was graded on, asserted directly rather than argued.** Two paths that both put
     * Dalvik's null reference in the same slot -- one through `filled-new-array`, one through `aput-object` --
     * must produce values that [org.cf.smalivm.core.agreesWith], or every consumer that reconciles them loses
     * a constant it had.
     *
     * Driven through the two ops rather than through hand-built `Value`s: a `Value.Known(null, STRING)` pair
     * compared to itself would pass whatever either op does, which is what made the defect invisible for a
     * whole plan. `putObject()V` is `APutOpTest`'s handler-free fixture and is seeded here exactly as
     * `canPutConstZeroNullObject` seeds it.
     */
    @Test
    fun theTwoWaysOfSpellingANullElementAgreeSoAJoinKeepsTheFold() {
        initial.setRegisters(0, 0, "I", 1, 0, "I")
        val filled = trackedArray(
            CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", initial),
            register = 2,
        ).elements()[0]

        val putState = CoreState()
        putState.setRegisters(0, arrayOf<String?>(null), "[Ljava/lang/String;", 1, 0, "I", 2, 0, "I")
        val putGraph = CoreTester.execute("Laput_test;", "putObject()V", putState)
        assertEquals(CompletionStatus.Complete, putGraph.completion())
        val stored = putGraph.trusted()!!.terminatingHeapObject(
            (putGraph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef,
        )
        assertTrue(stored is HeapObject.TrackedArray, "aput-object's array should be tracked; got $stored")
        val aput = (stored as HeapObject.TrackedArray).elements()[0]

        assertEquals(aput, filled, "the two ops must spell Dalvik's null element the same way")
        assertTrue(filled.agreesWith(aput), "and the spellings must agree, or join degrades to Unknown")
        assertEquals(filled, join(filled, aput, CoreTester.classIndex), "so the join keeps the fold instead of answering Unknown")
    }

    /**
     * **[ART] only, undocumented anywhere, and a plausible anti-analysis trick.** `[Z`/`[B`/`[C`/`[S`/`[F` are
     * "single-word" and so *look* legal per [BC], but ART does not implement them: the verifier soft-fails
     * (`VERIFY_ERROR_FILLED_NEW_ARRAY`) and the interpreter throws `java.lang.InternalError`. There is **no
     * JVM counterpart to inherit from** -- the JVM's `newarray` implements every primitive component -- so
     * this row is ART's own choice, and fidelity says throw, because a device really does.
     *
     * **`InternalError` extends `Error`**, so a `catch (Exception)` wrapper -- which obfuscator output is full
     * of -- cannot catch it. That is the same handler-reachability argument as design decision 4's, and it is
     * why `testThrows` (which requires the throw to have ESCAPED) is the right helper and no try block is
     * needed in the fixture.
     *
     * **The MESSAGE's `%s` is the COMPONENT's Java source name, not a Dalvik descriptor** -- this corrects the
     * brief, which wrote `"Found type <descriptor>; …"`. ART formats
     * `component_class->PrettyDescriptor().c_str()`, and `AppendPrettyDescriptor` maps `B` to `"byte"`, so a
     * device says *"Found type byte; …"*. Both sources were fetched as raw base64 and decoded rather than read
     * through a summarizing tool; see [FilledNewArrayOp.notImplementedMessage] and
     * `docs/DALVIK-SEMANTICS.md`.
     */
    @Test
    fun aSingleWordPrimitiveComponentArtDoesNotImplementThrowsInternalError() {
        initial.setRegisters(0, 1, "I", 1, 2, "I")
        CoreTester.testThrows(
            CLASS_NAME, "filledNewArrayOfBytes()V", initial,
            exceptionType = "Ljava/lang/InternalError;",
            message = "Found type byte; filled-new-array not implemented for anything but 'int'",
        )
        // The negative half, by hand: `testThrows` says nothing about whether a normal path also ran, and a
        // `wasReached` negative is only a dead-code claim on a run that explored everything.
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfBytes()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(3, graph.methodView.nextAddress(0), "filled-new-array is 35c = THREE code units")
        assertFalse(graph.wasReached(3), "a definite throw must not also explore the move-result-object")
        assertEquals(
            listOf("Ljava/lang/InternalError;"), CoreTester.escapingTypes(graph),
            "exactly one throw, of exactly this type",
        )
        assertEquals(0, graph.nodePile(0).first().children.size, "a definite throw has no successor")
    }

    /**
     * **INHERITED, and this is the one exception whose inheritance survives the `c0281bc` correction** -- but
     * via [JVMS] §5.4.3.1's *resolution procedure*, which is not instruction-specific, rather than via a
     * `filled-new-array` counterpart instruction (there is none: the JVM allocates with `anewarray` and fills
     * with a `dup`/`*astore` sequence). The allocation half *does* have a counterpart, `anewarray`, whose
     * §6.5 entry says *"During resolution of the symbolic reference to the class, array, or interface type,
     * any of the exceptions documented in §5.4.3.1 can be thrown"* -- and §5.4.3.1's failures are
     * `NoClassDefFoundError` and friends, all `Error`s. [ART] puts it in the same place:
     * `DoFilledNewArray` calls `ResolveVerifyAndClinit` and bails with a pending exception.
     *
     * **Deliberately the same ruling, the same reason and the same `ClassIndex.isLoadable` as `new-array`'s**
     * (design decision 4), not a second answer: absence is a FACT rather than an unknown, because spec §3
     * makes multidex a non-goal, so the throw is DEFINITE and does not fork. Forking here would explore a
     * normal path that cannot happen, for every absent class in an obfuscated APK.
     *
     * The fixture is **not** in the brief's block and was added because the `!loadable` branch would otherwise
     * be inert -- no mutation to it could kill any test, which the mutate-to-prove rule forbids. It is
     * handler-free, which is correct rather than lazy: `NoClassDefFoundError` is an `Error`, so a
     * `catch (Exception)` clause could not catch it and the try block would be dead code.
     */
    @Test
    fun anUnloadableComponentTypeThrowsNoClassDefFoundError() {
        initial.setRegisters(0, 1, "I", 1, 2, "I")
        CoreTester.testThrows(
            CLASS_NAME, "filledNewArrayOfMissingClass()V", initial,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "does.not.exist",
        )
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfMissingClass()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        // The address gate is NOT redundant with the `wasReached` line below it: `wasReached` answers false
        // for a nonexistent address just as readily as for an unexplored one (the node pile is empty), so a
        // negative on the wrong number is silently green. `filled-new-array` is 35c = THREE code units, so 3
        // is the successor and addresses 1 and 2 do not exist. Its sibling above carries the same pair.
        assertEquals(3, graph.methodView.nextAddress(0), "filled-new-array is 35c = THREE code units")
        assertFalse(graph.wasReached(3), "a definite throw must not also explore the move-result-object")
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph))
        assertEquals(0, graph.nodePile(0).first().children.size, "definite, not a fork")
    }

    /**
     * **Bug class: the mirror defect -- forking a provably INFEASIBLE throw**, which invents a reachable
     * handler and tells a deobfuscator a dead `catch` is live. This plan has shipped that defect twice, and
     * this op is the most tempting site for it yet, because a port of [APutOp]'s or [FillArrayDataOp]'s
     * unprovable-operand row would put an NPE and an AIOOBE fork here. **Neither is reachable**, and neither
     * is `NegativeArraySizeException`:
     *
     *  - **no NPE**: this op has no array OPERAND. It *creates* the array, so there is nothing to be null.
     *  - **no AIOOBE**: no index operand, and the array is allocated at exactly the element count.
     *  - **no `NegativeArraySizeException`**: the count is the instruction's own `A` field -- 4 bits for `35c`
     *    (and the format defines only registers C..G, so ≤ 5) and 8 bits for `3rc` (≤ 255). An unsigned field
     *    cannot be negative. ART has a `ThrowNegativeArraySizeException` site in `DoFilledNewArray` guarded by
     *    `if (UNLIKELY(length < 0))`, and it is dead for that reason. **SPEC**, from the instruction format --
     *    NOT carried over from `new-array` by analogy, where a negative length is a real runtime value.
     *  - **no `ArrayStoreException`**: [ART]'s reference fill is `SetWithoutChecks`, i.e. no assignability
     *    test at all; element/component agreement is a **VERIFIER** matter. So there is no counterpart to
     *    [APutOp]'s `storeCheck` here, even though this op stores references into a reference array.
     *
     * The seed is the WEAKEST input the lattice has -- every element register unassigned, so `frame.read`
     * answers `Value.Top` -- because that is the input under which a defensive "I cannot prove the operand, so
     * fork the exceptions" reflex fires. `escapingTypes` and the child count are the only queries that can see
     * an extra fork; `CoreTester.testThrows` cannot, since its assertion is existence rather than exclusivity.
     */
    @Test
    fun nothingIsForkedBecauseThisOpHasNoArrayIndexOrCountToBeWrong() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "no operand of this op can be null, out of range, or negative, so every throw is INFEASIBLE",
        )
        assertEquals(
            1, graph.nodePile(0).first().children.size,
            "exactly one successor: the fall-through to the move-result-object",
        )
        assertTrue(graph.wasReached(3), "and it really is explored")
        // Every slot is Unknown rather than the array being discarded -- precision improvement #3 again, at
        // its limit, and the reason this run still yields a tracked array at all.
        val array = trackedArray(graph, register = 3)
        assertEquals(3, array.length)
        assertEquals(List(3) { Value.Unknown(INT) }, array.elements())
    }

    /**
     * **The effect level, pinned on every row, because a spurious `STRONG` is silent and expensive.** A fully
     * provable constant array would report `Complete` *and* `STRONG`, and every consumer strategy gated on
     * `methodSideEffectLevel() == NONE` would refuse to fold it -- which is the entire deobfuscation value of
     * this opcode. That is the over-declaring mirror of the Critical this plan shipped in Task 8, and it is
     * the *sound* direction, which is exactly why nothing else would catch it.
     *
     * `NONE` is not a default here, it is a claim: this op allocates a **fresh** array, so nothing pre-existing
     * is mutated and the heap models the allocation exactly. Unlike [APutOp] and [FillArrayDataOp] there is no
     * unprovable-receiver row that could legitimately be `STRONG` -- the array is not an operand -- so
     * [FilledNewArrayOp] calls `Frame.recordSideEffect` nowhere, and the mutation that proves this test is one
     * that ADDS a declaration rather than one that removes it.
     *
     * All four rows: fully known, fully unknown, and both definite throws (which allocate nothing at all).
     */
    @Test
    fun aFreshlyAllocatedArrayDeclaresNoSideEffectOnEveryRow() {
        val known = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", CoreState().apply {
            setRegisters(0, 2, "I", 1, 3, "I", 2, 5, "I")
        })
        assertEquals(SideEffectLevel.NONE, known.sideEffectLevel(0), "a fresh array mutates nothing")
        val trusted = known.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        assertEquals(
            SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
            "and the whole-method query a strategy actually reads agrees",
        )

        val unknown = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(
            SideEffectLevel.NONE, unknown.sideEffectLevel(0),
            "an unprovable ELEMENT is a lattice point in a tracked slot, not an unmodellable write",
        )

        val internalError = CoreTester.execute(CLASS_NAME, "filledNewArrayOfBytes()V")
        assertEquals(SideEffectLevel.NONE, internalError.sideEffectLevel(0), "a definite throw allocates nothing")
        val missingClass = CoreTester.execute(CLASS_NAME, "filledNewArrayOfMissingClass()V")
        assertEquals(SideEffectLevel.NONE, missingClass.sideEffectLevel(0), "and neither does the other one")
    }

    /**
     * An `I`-component element register holding a computed `Float` fills its slot with the register's 32 bits:
     * `5.9f` is `0x40BCCCCD`, i.e. 1086115021. A value conversion would fill 5. **VERIFIER**-excluded input
     * ([CON] B1; [ART] `VerifyFilledNewArray`). `NONE` is asserted because a value change must not become an
     * effect change.
     */
    @Test
    fun aFloatElementIsReadByItsBits() {
        initial.setRegisters(0, 2, "I", 1, 5.9f, "F", 2, 7, "I")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val array = trackedArray(graph, register = 3)
        assertEquals(
            listOf(Value.Known(2, INT), Value.Known(1086115021, INT), Value.Known(7, INT)), array.elements(),
        )
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(0))
    }

    /**
     * Bug class: every element register must be READ even after the first unprovable one. The engine being
     * replaced deliberately did not break early *"since we want to signify that all registers are read
     * here"*, and it matters: `registersRead` feeds
     * [org.cf.smalivm.core.ExecutionGraph.availableRegisters], so a register the op reads but does not report
     * is a register a later consumer may reuse as scratch while it is live.
     *
     * The unprovable element is register **0**, the FIRST one, so a loop that stopped at the first unknown
     * would report the empty set rather than a nearly-complete one.
     */
    @Test
    fun everyElementRegisterIsReportedAsReadEvenAfterAnUnprovableOne() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "I", 1, 3, "I", 2, 5, "I")
        })
        assertEquals(setOf(0, 1, 2), graph.registersRead(0))
    }

    /**
     * Bug class: the result slot leaking into `registersAssigned`. It is not a register, so
     * `availableRegisters` must not learn anything from this op -- and the engine being replaced's
     * `registersAssignedCount = 1` is simply wrong about that. This op DOES produce a result, unlike `aput`
     * and `fill-array-data`, so the empty set here is not their precedent reapplied: the reason is that a
     * `Set<Int>` cannot name a typed [org.cf.smalivm.core.Frame] slot. See [FilledNewArrayOp]'s KDoc.
     *
     * THE FIRST ASSERTION IS VACUOUS ON ITS OWN and the two lines above it are what make the test real.
     * `assertEquals(emptySet(), registersAssigned(0))` passes if the op is missing, unbuilt, or decoded to an
     * `UnsupportedOp` -- i.e. it passes hardest exactly when nothing works, because
     * `ExecutionGraph.registersAssigned` reads `op.registersAssigned` statically and the base `Op` answers the
     * empty set. The completion gate rules out `Partial(UNSUPPORTED_OP)`, `wasReached(0)` rules out an
     * unexecuted node, and the address-3 assertion is the only line that distinguishes "assigns nothing" from
     * "did not run". Do not delete any of the three as redundant.
     */
    @Test
    fun theResultSlotIsNotAnAssignedRegister() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(CompletionStatus.Complete, graph.completion(), "an unbuilt op would satisfy the empty-set claim trivially")
        assertTrue(graph.wasReached(0), "and so would a node that never executed")
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
        assertEquals(setOf(3), graph.registersAssigned(3), "the move-result-object is what assigns v3")
    }

    // ----------------------------------------------------------------------------------------------
    // Helpers.
    // ----------------------------------------------------------------------------------------------

    /**
     * Seeds `elements` into the fixture's element registers in order, runs it, and asserts the array
     * `move-result-object` put in `movedInto` holds exactly them. One helper rather than five near-identical
     * tests, and it seeds by INDEX (`0..n-1`), which is what makes it a register-ORDER assertion: every
     * fixture here names `v0 .. v(n-1)` in ascending order, so element `i` must come from register `i`.
     */
    private fun assertDecodes(methodDescriptor: String, elements: IntArray, movedInto: Int) {
        val seed = CoreState()
        val want = CoreState()
        for (i in elements.indices) seed.setRegister(i, elements[i], "I")
        want.setRegisters(movedInto, elements, "[I")
        CoreTester.test(CLASS_NAME, methodDescriptor, seed, want)
    }

    /** The tracked array every returning path agrees `register` holds. */
    private fun trackedArray(graph: ExecutionGraph, register: Int): HeapObject.TrackedArray {
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val consensus = trusted!!.terminatingConsensus(register)
        assertTrue(
            consensus is Value.Known && consensus.raw is HeapRef,
            "register $register should hold a tracked array, but was $consensus",
        )
        val actual = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef)
        assertTrue(actual is HeapObject.TrackedArray, "no array behind the ref; got $actual")
        return actual as HeapObject.TrackedArray
    }
}
