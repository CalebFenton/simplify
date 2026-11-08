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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * The seven `aput*` opcodes (ops 4b-51, format 23x = **TWO** code units, so the successor of an op at 0 is
 * address 2 and address 1 does not exist). Migrated from `org.cf.smalivm.opcode.APutOpTest` -- **all thirty
 * of its tests**, twenty-seven ported verbatim and three superseded by the precision tests below. That file
 * is deleted as of the `ArrayStoreException` commit; the engine it drove is described in the past tense
 * everywhere below, and no citation here points into it.
 *
 * **`aput` is not `aget` mirrored.** `registerA` is the **value** register, not a destination, so
 * [org.cf.smalivm.core.Op.destinationRegisters] is never involved; all three operands are read; and the
 * stored element is the value as the array's component holds it, so `aput-boolean` of `0x1` stores `true`.
 *
 * **Grounding.** [BC]'s entire specification for all fourteen `aget*`/`aput*` opcodes is one shared sentence
 * -- *"Perform the identified array operation at the identified index of the given array, loading or storing
 * into the value register."* -- and it names **no** exception, here or anywhere else for arrays (the whole
 * document names exactly two, neither of them an array exception). So every `NullPointerException` and
 * `ArrayIndexOutOfBoundsException` below is **INHERITED** from [JVMS] §6.5 `iastore`/`aastore`, legitimised
 * by [VER]'s *"generally the same set of checks as are described in The Java Virtual Machine Specification,
 * Second Edition, including the updates planned for the Third Edition"* -- quoted through the edition
 * because the bridge is same-DOCUMENT, not same-edition ([VER] names the Second/Third Editions; the [JVMS]
 * citations here are SE21, and §6.5's array rules are unchanged across those editions, which is why the
 * inheritance holds). Nothing here says "Dalvik throws". Full quotes and tiers:
 * `docs/DALVIK-SEMANTICS.md#aget--aput-ops-4451-23x`.
 *
 * **The one coercion row that is SPEC rather than corpus** is the null idiom: [BC], *"In terms of bitwise
 * representation, `(Object) null == (int) 0`"*, which is the same fact as
 * [org.cf.smalivm.core.isDalvikNull] and is why `aput-object` of `Known(0, "I")` stores `null`.
 *
 * **`ArrayStoreException` sits at TWO different points in the exception order, because the two refusals have
 * different evidence tiers, and the tests below are named for which half they pin.**
 *  - `APutOp`'s **shape** refusal (exactly one of the value and the component is primitive) goes **FIRST**,
 *    ahead of the null and bounds rows. That is a **VERIFIER -> engine's choice**: a reference register as
 *    the source of an int-like `aput` is `VERIFY_ERROR_BAD_CLASS_HARD` ([CON] B1; [ART] `VerifyAPut`), so no
 *    verified dex reaches it and there is no Dalvik behaviour to be faithful to. Pinned three times over by
 *    [outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters],
 *    [unknownValueItemWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters] and
 *    [unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters].
 *  - The real `aastore` **assignability** refusal goes **LAST**, after null and after bounds:
 *    **INHERITED**, because [JVMS] §6.5 `aastore`'s *"Otherwise"* chaining makes NPE -> AIOOBE -> ASE
 *    normative -- the same normative device Plan 7's design decision 4 relies on for `new-array`. Pinned by
 *    [aVerifiedObjectStoreWithABadIndexThrowsBoundsBeforeArrayStore].
 *
 * **ASE comes from `aput-object` and nothing else** (**INHERITED** + [ART]): [JVMS] §6.5 lists exactly NPE
 * and AIOOBE for `iastore`/`bastore`, and `aastore` is the only array-store instruction that lists ASE at
 * all -- *"if arrayref is not null and the actual type of the non-null value is not assignment compatible
 * with the actual type of the components of the array, aastore throws an ArrayStoreException"*. So **no
 * store whose value and component are both primitive throws ASE, whatever the mnemonic**, including
 * `aput-wide` of a `J` into a `[D` ([aWideStoreOfALongIntoADoubleArrayThrowsNothingBecauseBothSidesArePrimitive]).
 * The four corpus fixtures that DO expect an ASE from a plain `aput` on an `[I` are not counter-examples:
 * they are verifier-invalid, so the rule above does not reach them and the engine's refusal there is its own
 * choice rather than a fidelity claim.
 */
class APutOpTest {
    private lateinit var initial: CoreState
    private lateinit var expected: CoreState

    @BeforeEach
    fun setUp() {
        initial = CoreState()
        expected = CoreState()
    }

    // ------------------------------------------------------------------------------------------
    // The 21 ported migrations.
    // ------------------------------------------------------------------------------------------

    /**
     * The only migrated test that needs a **guest** `java.lang.Class`: `Tester.spawnVM().classLoader`
     * becomes [CoreTester.classLoader] (added in Plan 6 for exactly this test). The expected values are
     * unchanged.
     *
     * `putObjects()V` was `.locals 3` while both its body and this seed name v3 and v4 --
     * `Frame.assign` range-`require`s and throws where the oracle's map-backed `pokeRegister` tolerated
     * it, so this went red on arrival. Fixed in the fixture (`.locals 5`), which moves no instruction:
     * `aput-object`@0, `aput-object`@2 (23x = two code units), `return-void`@4.
     */
    @Test
    fun canInsertLocalClassAndClassIntoSameArray() {
        val valueType = "Ljava/lang/Class;"
        val arrayType = "[$valueType"
        val array: Array<Class<*>?> = arrayOfNulls(2)
        val index1 = 0
        val index2 = 1
        val value1: Class<*> = String::class.java
        val value2: Class<*> = CoreTester.classLoader.loadClass("aput_test")
        initial.setRegisters(0, array, arrayType, 1, index1, "I", 2, value1, valueType, 3, index2, "I", 4, value2, valueType)
        expected.setRegisters(0, arrayOf(value1, value2), arrayType)
        CoreTester.test(CLASS_NAME, "putObjects()V", initial, expected)
    }

    /**
     * An array stored INTO an array, so the stored element is itself a `Known` whose raw is a [HeapRef]
     * -- see [assertStoredArrayElements] for why a plain `assertEquals` cannot express that.
     *
     * The stored element's declared type is the OUTER array's component (`Ljava/lang/Object;`), not the
     * value register's tag (`[B`): object over tag.
     */
    @Test
    fun canArrayIntoObjectArray() {
        initial.setRegisters(0, arrayOfNulls<Any>(1), "[Ljava/lang/Object;", 1, 0, "I", 2, ByteArray(1), "[B")
        assertStoredArrayElements(
            "putObject()V", initial,
            outerRegister = 0, outerLength = 1, index = 0,
            storedElementType = "Ljava/lang/Object;",
            expectedInnerElements = listOf(Value.Known(0.toByte(), BYTE)),
        )
    }

    /** `aput-boolean` of the integer literal `0x1` stores `true`. */
    @Test
    fun canPutBoolean() {
        initial.setRegisters(0, BooleanArray(1), "[Z", 1, 0, "I", 2, 0x1, "Z")
        expected.setRegisters(0, booleanArrayOf(true), "[Z")
        CoreTester.test(CLASS_NAME, "putBoolean()V", initial, expected)
    }

    /** ...and a `Short` 1 is the same 1: Dalvik's tags are ambiguous, so the raw goes through `Coerce`. */
    @Test
    fun canPutBooleanWithShortValue() {
        val value: Short = 0x1
        initial.setRegisters(0, BooleanArray(1), "[Z", 1, 0, "I", 2, value, "Z")
        expected.setRegisters(0, booleanArrayOf(true), "[Z")
        CoreTester.test(CLASS_NAME, "putBoolean()V", initial, expected)
    }

    /**
     * A boolean element keeps the low byte of the register: ART's `APUT_BOOLEAN` is
     * `HandleAPut<mirror::BooleanArray>(GetVReg(A()))`, whose `SetWithoutChecks` takes a `uint8_t`
     * (`runtime/interpreter/interpreter_switch_impl-inl.h`). So `0x101` stores `1`, i.e. `true`, where an
     * `== 1` test on the whole register reads `false`.
     */
    @Test
    fun aBooleanStoreKeepsTheLowByteOfTheRegister() {
        initial.setRegisters(0, BooleanArray(1), "[Z", 1, 0, "I", 2, 0x101, "I")
        expected.setRegisters(0, booleanArrayOf(true), "[Z")
        CoreTester.test(CLASS_NAME, "putBoolean()V", initial, expected)
    }

    /**
     * `aput-object` reads its value as a reference, so a register that holds an `Integer` object stores
     * that object even when the object's value is zero; only a register whose last write was a primitive
     * reads as `null` (ART's `GetVRegReference`). `iput-object` and `sput-object` apply the same rule.
     */
    @Test
    fun anObjectStoreOfAReferenceToABoxedZeroStoresTheObjectNotNull() {
        val boxedZero: Any = Integer.valueOf(0)
        initial.setRegisters(0, arrayOfNulls<Any>(1), "[Ljava/lang/Object;", 1, 0, "I", 2, boxedZero, "Ljava/lang/Integer;")
        expected.setRegisters(0, arrayOf(boxedZero), "[Ljava/lang/Object;")
        CoreTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutByte() {
        val value: Byte = 0xf
        initial.setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, value, "B")
        expected.setRegisters(0, byteArrayOf(value), "[B")
        CoreTester.test(CLASS_NAME, "putByte()V", initial, expected)
    }

    @Test
    fun canPutByteFromInt() {
        val value = 0xf
        initial.setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, value, "B")
        expected.setRegisters(0, byteArrayOf(value.toByte()), "[B")
        CoreTester.test(CLASS_NAME, "putByte()V", initial, expected)
    }

    /**
     * A `Char` raw is stored UNCHANGED, because coercion applies only to a `Number` and
     * `java.lang.Character` is not one. That is what lets `canPutStringInStringArray` and the nested-array
     * cases work at all, and it is the oracle's own rule (`opcode/APutOp.kt`'s `castValue` returns early
     * for a non-`Number`).
     */
    @Test
    fun canPutChar() {
        initial.setRegisters(0, CharArray(1), "[C", 1, 0, "I", 2, '$', "C")
        expected.setRegisters(0, charArrayOf('$'), "[C")
        CoreTester.test(CLASS_NAME, "putChar()V", initial, expected)
    }

    /** The other direction, and the one the suffix earns its keep on: an `Int` 36 becomes `'$'`. */
    @Test
    fun canPutCharFromInt() {
        // `'$'.code` rather than the corpus's deprecated `'$'.toInt()`. The same integer, 36.
        initial.setRegisters(0, CharArray(1), "[C", 1, 0, "I", 2, '$'.code, "I")
        expected.setRegisters(0, charArrayOf('$'), "[C")
        CoreTester.test(CLASS_NAME, "putChar()V", initial, expected)
    }

    /**
     * `aput-object` of `Known(0, "I")` stores `null` -- **SPEC** ([BC]: *"In terms of bitwise
     * representation, `(Object) null == (int) 0`"*), the same fact as
     * [org.cf.smalivm.core.isDalvikNull], and the one coercion row with a spec citation.
     *
     * **DOCUMENTED SINGLE-FIXTURE DEPARTURE: the DECLARED array type is `"[Ljava/lang/String;"` where the
     * corpus wrote `"[I"`.** The index, the value, the opcode and the expectation are byte-identical, and
     * the asserted *claim* is unchanged.
     *
     * Why: the corpus wrote `val valueType = "I"; val arrayType = "[$valueType"` and then seeded a host
     * `arrayOfNulls<String>(1)`, so the declared type was `[I` while the runtime array is
     * `[Ljava/lang/String;`. `CoreTester.test` asserts the runtime type is ASSIGNABLE TO the declared one,
     * and that is **false** here -- primitive arrays are invariant (**INHERITED**, [JLS] §4.10.3), pinned by
     * Plan 6's `CoreTesterTest.aPrimitiveDeclaredArrayTypeRejectsAReferenceComponentRuntimeArray`. It is an
     * authoring slip (`valueType` describes the stored VALUE, not the array) and it is the **illegal** kind
     * of declared-vs-runtime disagreement rather than legal covariance: `aput-object` requires a
     * reference-component array (**VERIFIER**; [ART] `VerifyAPut` hard-fails a primitive array type as the
     * source for `aput-object`), so no verified dex can produce this pair.
     *
     * `CoreTester`'s assignability check is what CAUGHT this and is not relaxed; `CoreState.elementsOf` is
     * not reverted to prefer the tag either, because that would type the stored `null` as
     * `Known(null, "I")` -- a null with a primitive type, worse than the fixture.
     * `AGetOpTest.canGetWide`'s `"[J"` seed is the other departure of this kind.
     */
    @Test
    fun canPutConstZeroNullObject() {
        val valueType = "I"
        val arrayType = "[Ljava/lang/String;"
        val array: Array<String?> = arrayOfNulls(1)
        val index = 0
        val value = 0
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf<String?>(null), arrayType)
        CoreTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    /** A `Short` INDEX, which is why the index goes through `Coerce` rather than an `as? Int`. */
    @Test
    fun canPutIntegerWithShortIndex() {
        val index: Short = 0
        initial.setRegisters(0, IntArray(1), "[I", 1, index, "S", 2, 4, "I")
        expected.setRegisters(0, intArrayOf(4), "[I")
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun canPutStringInStringArray() {
        val valueType = "Ljava/lang/String;"
        val arrayType = "[$valueType"
        val array = arrayOfNulls<String>(1)
        val index = 0
        val value = "Arrakis, Dune, desert planet..."
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf(value), arrayType)
        CoreTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    /**
     * The register is tagged `[Ljava/lang/Object;` while the host array really is a `String[]`, so the
     * stored element's declared type comes from the OBJECT (`Ljava/lang/String;`) and not from the tag.
     * This is the discriminating witness for object-over-tag in this file -- every other loosely-tagged
     * fixture here makes the two derivations agree.
     *
     * Legal covariance, unlike [canPutConstZeroNullObject]: `String[] <: Object[]`, so
     * `CoreTester.test`'s assignability check passes as it should.
     */
    @Test
    fun canPutStringInObjectArray() {
        val valueType = "Ljava/lang/String;"
        val arrayType = "[Ljava/lang/Object;"
        val array: Array<String?> = arrayOfNulls(1)
        val index = 0
        val value = "Arrakis, Dune, desert planet..."
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf(value), arrayType)
        CoreTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutStringArrayIn2DStringArray() {
        val valueType = "[Ljava/lang/String;"
        val arrayType = "[$valueType"
        val array: Array<Array<String?>?> = arrayOfNulls(1)
        val value = arrayOf<String?>("Arrakis, Dune, desert planet...")
        initial.setRegisters(0, array, arrayType, 1, 0, "I", 2, value, valueType)
        assertStoredArrayElements(
            "putObject()V", initial,
            outerRegister = 0, outerLength = 1, index = 0,
            storedElementType = valueType,
            expectedInnerElements = listOf(Value.Known(value[0], STRING)),
        )
    }

    /**
     * Declared `[[Ljava/lang/Object;`, runtime `[[Ljava/lang/String;`, so the stored element's type is
     * `[Ljava/lang/String;` -- object over tag again, one dimension up.
     */
    @Test
    fun canPutStringArrayIn2DObjectArray() {
        val valueType = "[Ljava/lang/String;"
        val arrayType = "[[Ljava/lang/Object;"
        val array: Array<Array<String?>?> = arrayOfNulls(1)
        val value = arrayOf<String?>("Arrakis, Dune, desert planet...")
        initial.setRegisters(0, array, arrayType, 1, 0, "I", 2, value, valueType)
        assertStoredArrayElements(
            "putObject()V", initial,
            outerRegister = 0, outerLength = 1, index = 0,
            storedElementType = valueType,
            expectedInnerElements = listOf(Value.Known(value[0], STRING)),
        )
    }

    @Test
    fun canPutShort() {
        val value: Short = 0x42
        initial.setRegisters(0, ShortArray(1), "[S", 1, 0, "I", 2, value, "S")
        expected.setRegisters(0, shortArrayOf(value), "[S")
        CoreTester.test(CLASS_NAME, "putShort()V", initial, expected)
    }

    @Test
    fun canPutShortWithIntegerValue() {
        val value = 0x42
        initial.setRegisters(0, ShortArray(1), "[S", 1, 0, "I", 2, value, "I")
        expected.setRegisters(0, shortArrayOf(value.toShort()), "[S")
        CoreTester.test(CLASS_NAME, "putShort()V", initial, expected)
    }

    /**
     * The not-`Known` array row, driven by a register whose tag is not even an array type. The register
     * stays `Unknown(Ljava/lang/Object;)`.
     *
     * **Its NAME is about the HOST, not about the fork.** "Without throwing an exception" meant the oracle
     * did not blow up; the core forks a `NullPointerException` AND an `ArrayIndexOutOfBoundsException` here,
     * and both ESCAPE, because `put()V` has no try block. Nothing is known about the array, so both throws
     * are feasible and dropping either would leave an unexplored feasible path on a `Complete` run.
     *
     * **BRIEF DEFECT (D3), resolved the way Task 7 resolved its own m1.** The brief asked for
     * `assertEquals(CompletionStatus.Complete, graph.completion())` here. That is UNFALSIFIABLE:
     * `CoreTester.test` asserts exactly it before comparing any register, so no input satisfies one and not
     * the other. The escape-TYPE list below is the checkable form of the claim the comment actually makes,
     * and it is strictly stronger -- it pins that both throws escape even though the index is KNOWN and the
     * value is KNOWN, i.e. that the unprovable-array row is reached before either is consulted.
     */
    @Test
    fun canPutIntoUnknownValueOfObjectTypeWithoutThrowingException() {
        initial.setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/Object;", 1, 0, "I", 2, 0x5, "I")
        expected.setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/Object;")
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
        val graph = CoreTester.execute(CLASS_NAME, "put()V", initial)
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(graph),
            "an unprovable array forks both exceptions, in transition order, and both escape put()V",
        )
    }

    @Test
    fun canPutWideWithDouble() {
        val value = 100000000000.0
        initial.setRegisters(0, DoubleArray(1), "[D", 1, 0, "I", 2, value, "D")
        expected.setRegisters(0, doubleArrayOf(value), "[D")
        CoreTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    /**
     * `F` is **not** wide -- `VirtualType.isWide` is `J` or `D` only -- even though the mnemonic says
     * `-wide`, so this one passed under the fixture's original `.locals 3` while its two siblings did not.
     * Worth stating because it is the kind of asymmetry that looks like a bug later.
     */
    @Test
    fun canPutWideWithFloat() {
        val value = 10.45f
        initial.setRegisters(0, FloatArray(1), "[F", 1, 0, "I", 2, value, "F")
        expected.setRegisters(0, floatArrayOf(value), "[F")
        CoreTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    @Test
    fun canPutWideWithLong() {
        val value = 10000000000L
        initial.setRegisters(0, LongArray(1), "[J", 1, 0, "I", 2, value, "J")
        expected.setRegisters(0, longArrayOf(value), "[J")
        CoreTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    @Test
    fun canPutWithInteger() {
        initial.setRegisters(0, IntArray(1), "[I", 1, 0, "I", 2, 4, "I")
        expected.setRegisters(0, intArrayOf(4), "[I")
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    // ------------------------------------------------------------------------------------------
    // Two of the three migrations whose ANSWER sharpens. The corpus's `canPutUnknownValue` and
    // `canPutWithUnknownIndex` both expected the old engine's whole-array `UnknownValue()`, which this one
    // deliberately no longer produces. The input and the question survive; only the answer changes, so they
    // count as migrated rather than dropped -- REPLACED, not ported. The third is
    // `unknownValueItemMakesOnlyItsOwnElementUnknownAndThrowsNothing`, further down, which is the same
    // divergence seen through fork shape instead of through values.
    // ------------------------------------------------------------------------------------------

    /**
     * Precision improvement #1, and **the test being replaced admitted it was one**, carrying the comment
     * *"TODO: Ideally, setting an element unknown shouldn't set entire array unknown"*. With elements as
     * lattice points it does not have to. The old engine expected `UnknownValue()/[I` for the whole array;
     * this one keeps the array `Known` and marks the one element it wrote. (Quoted without a file reference:
     * the file was deleted with the engine, and a citation into it would now resolve against this file.)
     *
     * Registered as `DivergenceShape.NEW_CORE_MORE_PRECISE` (Task 13). Strictly lower in the lattice than
     * the oracle's answer and never invents a constant, so it is safe as well as sharper -- never reconcile
     * it by making the core coarser.
     */
    @Test
    fun anUnknownValueMakesOnlyItsOwnElementUnknown() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 2), "[I", 1, 0, "I", 2, CoreState.UNKNOWN, "I")
        })
        val trusted = graph.trusted()
        assertNotNull(trusted, "a store with nothing to throw about must complete")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(INT), array.element(0), "the written slot")
        assertEquals(Value.Known(2, INT), array.element(1), "and only the written slot")
    }

    /**
     * Precision improvement #2. An unknown INDEX means the write landed on exactly one slot but we cannot
     * say which, so every slot is either its old value or the new one -- which is what `join` says.
     * `join(Known(1,I), Known(7,I))` is `Unknown(I)` and `join(Known(7,I), Known(7,I))` is `Known(7,I)`, so
     * an array whose old value already equals the stored one stays fully known: strictly better than the
     * oracle's whole-array `Unknown`, and the second element below is what proves the join is a join rather
     * than a blanket overwrite.
     */
    @Test
    fun anUnknownIndexJoinsEveryElementWithTheStoredValue() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 7), "[I", 1, CoreState.UNKNOWN, "I", 2, 7, "I")
        })
        val trusted = graph.trusted()
        assertNotNull(trusted, "the forked bounds exception is an outcome, not a truncation")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(INT), array.element(0), "1 and 7 disagree")
        assertEquals(Value.Known(7, INT), array.element(1), "7 and 7 agree, so this stays known")
    }

    // ------------------------------------------------------------------------------------------
    // Rows the corpus's 23 `can*` tests cannot see. Every one of these closes a claim that a mutation
    // would otherwise break with nothing to kill it -- measured, not assumed: the migrated corpus reads
    // RETURNING-path consensus, which an extra ESCAPING throw cannot perturb, so a migrated test is
    // evidence about values and never about how many paths exist. Fork SHAPE is only visible through
    // `outcomes` and `children`.
    //
    // Task 9 migrates the corpus's OWN definite-throw tests (`nullArrayValueThrows...`,
    // `outOfBoundsIndexThrows...`). These are deliberately named differently so both survive: the
    // corpus's carry its exact claim shape, these carry the row.
    // ------------------------------------------------------------------------------------------

    /**
     * A definitely-null array is a DEFINITE `NullPointerException` -- no fork, no store (**INHERITED**,
     * [JVMS] §6.5 `iastore`).
     *
     * The index is seeded **10** against a null array on purpose: that pins the ORDER. [JVMS] §6.5's
     * `Otherwise` chaining makes NPE strictly precede the bounds exception, so an implementation that
     * consulted the index first would leave an `ArrayIndexOutOfBoundsException` in v0 and fail here.
     *
     * `testCaught`, never `testThrows`: `putWithCatch()V` declares a `Ljava/lang/Exception;` handler around
     * the `aput`, an NPE is an `Exception`, so the throw is CAUGHT, `move-exception v0` binds it and the run
     * RETURNS -- producing no `Outcome.Threw` at all. Address **2**, not 1: 23x is TWO code units, so the
     * `nop` sits at 2 and `wasReached(1)` would be silently false (`nodePile(1)` is empty), i.e. a green
     * assertion testing nothing.
     *
     * No message asserted: the corpus asserts none. [ART] does have a per-opcode text for this one
     * (*"Attempt to write to null array"*, `common_throws.cc:657`), so a later plan may adopt it -- an
     * improvement rather than a fix, and nothing here blocks it.
     */
    @Test
    fun aDefinitelyNullArrayThrowsNullPointerExceptionBeforeLookingAtTheIndex() {
        initial.setRegisters(0, null, "[I", 1, 10, "I", 2, 0x42, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * The OTHER half of Dalvik's null, which `Value.isNull` cannot see. **SPEC** ([BC]: *"In terms of
     * bitwise representation, `(Object) null == (int) 0`"*), so `const/4 v0, 0x0` followed by `aput` is a
     * definite NPE -- but the register holds `Known(0, "I")`, whose raw is not `null`, so without
     * `Value.isDalvikNull` it would fall through to the provably-non-null rows, store nothing, and a
     * definite throw would be silently dropped. Mirrors
     * `AGetOpTest.aKnownIntegralZeroArrayIsDalviksNullAndThrowsTheSameNullPointerException` and
     * `ArrayLengthOpTest.aKnownIntegralZeroIsDalviksNullAndThrowsTheSameNullPointerException` -- three ops,
     * one predicate.
     */
    @Test
    fun aKnownIntegralZeroArrayIsDalviksNullAndThrowsTheSameNullPointerException() {
        initial.setRegisters(0, 0, "I", 1, 0, "I", 2, 0x42, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        // The effect level needs its own execute because `CoreTester.testCaught` never asserts a side-effect
        // level. A definite throw stores nothing, so it stays NONE; a spurious STRONG would silently stop
        // every constant-array fold downstream.
        assertEquals(
            SideEffectLevel.NONE, CoreTester.execute(CLASS_NAME, "putWithCatch()V", initial).explored.sideEffectLevel(0),
            "a definite NPE stores nothing, so there is no unmodelled write to account for",
        )
    }

    /**
     * The SAME definite NPE for a NON-zero primitive array register -- the half the zero-only predicate
     * dropped. Note this is the ARRAY operand: `aput`'s VALUE operand keeps `isDalvikNull` gated on the
     * `OBJECT` suffix, because that is a coercion question about the bits rather than a dereference.
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
     * `nterp`, which reads the array operand out of the RAW dex-register array
     * (`mterp/x86_64ng/array.S:58-80` for `aput`) and dereferences a non-zero value as an object pointer --
     * so the two tiers CONTRADICT each other on exactly the value this test seeds, and `VerifyAPut`'s sibling
     * check (`method_verifier.cc:5054-5055`) refuses to link it besides. The **zero** spellings are
     * corroborated by BOTH tiers, which is a strengthening. **This expectation does not change**: a definite
     * NPE is the sound direction and matches the switch tier exactly. Only the citation changes. See
     * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*.
     */
    @Test
    fun aKnownNonZeroPrimitiveArrayIsAlsoDalviksNullAndThrowsTheSameNullPointerException() {
        initial.setRegisters(0, 5, "I", 1, 0, "I", 2, 0x42, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        // Same effect-level pin as the zero spelling above, on the same row, so a mutation there kills TWO
        // tests rather than sliding past both. The row this reaches is the one whose `STRONG` -> `NONE` move
        // the fix round caused: before it, a primitive-typed array register fell through to ROW 3, which
        // declares STRONG for a write it cannot name. It now throws before storing anything.
        assertEquals(
            SideEffectLevel.NONE, CoreTester.execute(CLASS_NAME, "putWithCatch()V", initial).explored.sideEffectLevel(0),
            "a definite NPE stores nothing, so there is no unmodelled write to account for",
        )
    }

    /**
     * A provable index outside the bounds is a DEFINITE `ArrayIndexOutOfBoundsException` and **no element
     * changes** (**INHERITED**, [JVMS] §6.5). Both halves matter: the second is what tells a definite throw
     * apart from "throw but write anyway", which no value-comparing migration can see.
     *
     * Driven through `put()V` rather than `putWithCatch()V` because the array has to stay READABLE: the
     * handler's `move-exception v0` would overwrite the array register, and `terminatingHeapObject` reads
     * RETURNING paths, of which a handler-free definite throw has none. So the elements are read off the
     * THROWING node's own frame, which is the frame `execute` ran against.
     */
    @Test
    fun aProvableIndexOutsideTheBoundsThrowsDefinitelyAndWritesNoElement() {
        val seed = CoreState().apply { setRegisters(0, intArrayOf(1, 2, 3), "[I", 1, 10, "I", 2, 0x42, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "put()V", seed)
        assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is an outcome, not a truncation")
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(2), "a DEFINITE bounds exception must not also explore the fall-through")
        val frame = graph.nodePile(0).first().frame
        val ref = (frame.peek(0) as Value.Known).raw as HeapRef
        val array = frame.heapObject(ref) as HeapObject.TrackedArray
        assertEquals(
            listOf(Value.Known(1, INT), Value.Known(2, INT), Value.Known(3, INT)), array.elements(),
            "a throwing store must leave every element exactly as it found it",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "nothing was stored, so there is no unmodelled effect to declare either",
        )
    }

    /**
     * A negative index is an ORDINARY bounds exception rather than a distinct one: ART compares unsigned
     * (`Array::CheckIsValidIndex`), so `index < 0` and `index >= length` are the same check and the same
     * throw (**INHERITED**, [JVMS] §6.5). The bug class is a bare `index >= length` test, which lets `-1`
     * through to `setElement(-1, …)` and turns a guest exception into a HOST failure -- i.e.
     * `Aborted(HOST_FAILURE)` instead of an explorable path.
     *
     * **The message is a PINNED ENGINE CHOICE that deliberately does NOT match any runtime.** Nothing
     * specifies an AIOOBE message -- [BC] names no array exception at all, [JVMS] specifies no text -- and
     * the one real runtime string is [ART]'s `"length=%d; index=%d"` (`runtime/common_throws.cc:131-134`,
     * quoted verbatim in `docs/DALVIK-SEMANTICS.md`, this repo's verified-quote registry). So `"-1"` is a
     * divergence from ART, not a match, asserted only so it cannot drift silently -- a test asserting an
     * exception *message* is never evidence of Dalvik fidelity.
     */
    @Test
    fun aNegativeKnownIndexIsAnOrdinaryBoundsException() {
        initial.setRegisters(0, intArrayOf(1, 2, 3), "[I", 1, -1, "I", 2, 0x42, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            message = "-1",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * The direction "always fork" gets wrong: a PROVABLY non-null array with an unknown index forks only
     * the bounds exception. Forking the NPE too would tell a deobfuscator a `catch (NullPointerException)`
     * handler is reachable on a path Dalvik cannot take.
     *
     * The values half of this row is [anUnknownIndexJoinsEveryElementWithTheStoredValue]; this is the SHAPE
     * half, and the shape is the half no migrated test can see.
     *
     * **It also pins that a SPECULATIVE bounds exception carries NO message** (review finding F3: that claim
     * was in `APutOp`'s KDoc and nothing failed when it was reverted). There is no offending index on this
     * row -- the index is precisely what is unprovable -- so naming one would fabricate a constant, which is
     * the one thing spec §2.2 forbids outright. Contrast [aNegativeKnownIndexIsAnOrdinaryBoundsException],
     * where the index IS known and the message is the pinned engine choice. Asserting `null` here does not
     * block a later plan from adopting ART's `"length=%d; index=%d"` shape, which needs the *index*: it would
     * change the DEFINITE row's text, not this one's.
     */
    @Test
    fun aProvableArrayWithAnUnknownIndexForksOnlyTheBoundsException() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 2), "[I", 1, CoreState.UNKNOWN, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "the store itself must still be explored")
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph))
    }

    /**
     * The `Value.Top` corner of the unprovable-array row: an array register with **no type at all**. Both
     * exceptions still fork, because a `Top` array is no more provable than an `Unknown` one -- and the
     * register is **never written**, not even with `Top`.
     *
     * Reachable from ordinary smali rather than only from a seed: `put()V` is `static ()V`, so an unassigned
     * v0 really is `Top`. Asserted through `frame.peek`, which is the only query that can tell "written with
     * `Top`" from "never written" -- `Frame.read` and `ExecutionGraph.consensus` both answer `Value.Top`
     * either way, so either would pass whether or not the op assigned anything. That is what makes `assertNull`
     * here the falsifiable form of `registersAssigned == emptySet()`: reinstating any
     * `frame.assign(arrayRegister, …)` makes it non-null.
     *
     * **Renamed in fix round 1** (was `…StaysTopAndStillForksBothExceptions`). While the op degraded the array
     * register, the claim was "the derived type is `Top` rather than a fabricated `Ljava/lang/Object;`"; with
     * the degradation gone there is nothing to derive, and the stronger claim is that nothing was written.
     */
    @Test
    fun anArrayRegisterWithNoTypeAtAllStillForksBothExceptionsAndIsNeverWritten() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState())
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNull(
            graph.nodePile(0).first().frame.peek(0),
            "an aput writes no register at all -- not even Top -- and `peek` is what can see the difference",
        )
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(graph),
        )
    }

    /**
     * Bug class: collapsing "not a tracked array" into "may be null" and forking two exceptions for a value
     * the engine just PROVED non-null. It is the TYPE that makes it provable, not the value: a
     * primitive-typed `Known` reads as null on ART whatever it holds and is diverted to the definite-NPE row
     * above (Plan 7 Task 12's fix round; this KDoc used to say "a `Known` non-zero raw", which was the
     * misreading). A `Known` REFERENCE with a non-null raw is provably non-null, so the NPE is
     * infeasible, and a bounds exception needs a length this input does not have -- so `[Continue]` alone is
     * correct. `frame.arrayAt(ref)` also answers null for a `HeapRef` pointing at a `GuestThrowable`, which
     * lands in this same branch for a definitely-non-null value, which is why it must not share a fork with
     * the `Unknown` row. Same trap as
     * `AGetOpTest.aKnownNonArrayIsProvablyNonNullSoTheElementIsUnknownAndNothingThrows` and
     * `ArrayLengthOpTest.aKnownNonArrayIsProvablyNonNullSoItsLengthIsUnknownAndNothingThrows` -- keep the
     * three consistent, because a single `tracked == null` fork in any of them invents a reachable handler.
     *
     * **VERIFIER -> engine's choice.** No verified dex reaches this row ([CON] B1), and ART `DownCast`s the
     * array unchecked and writes at a mis-scaled stride -- it corrupts the wrong bytes, which is not a
     * behavior to reproduce. What the engine records instead is an **effect**: the write happened somewhere it
     * cannot model, so this address is `SideEffectLevel.STRONG`.
     *
     * **The register keeps its provable value, and that changed in fix round 1.** An earlier revision degraded
     * v0 to `Unknown(Ljava/lang/String;)`, arguing that a confident `Known` claimed knowledge the engine did
     * not have. That was a category error: the unmodellable write goes to *memory*, and `aput` does not write
     * the array register at all, so the register's value is provably unchanged and degrading it discarded a
     * provable constant while accounting for nothing. The `STRONG` level below is what actually accounts for
     * it. Consequently the v0 expectation is now a "this method changes nothing here" claim rather than a
     * transformation -- the discriminating weight sits on the successor count, the empty escape set and the
     * effect level.
     */
    @Test
    fun aKnownNonArrayIsProvablyNonNullSoNothingThrowsAndNoElementIsWritten() {
        initial.setRegisters(0, "not an array at all", "Ljava/lang/String;", 1, 0, "I", 2, 0x42, "I")
        expected.setRegisters(0, "not an array at all", "Ljava/lang/String;")
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
        val graph = CoreTester.execute(CLASS_NAME, "put()V", initial)
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor -- both throws are infeasible")
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "a Known REFERENCE with a non-null raw is provably non-null and has no length",
        )
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the store landed at a mis-scaled address in something the engine cannot name",
        )
    }

    /**
     * Pins `buildAPut`'s OPERAND ORDER for all three of format 23x's genuinely distinct registers, whose
     * MEANINGS differ from `aget`'s: `vAA` = **value**, `vBB` = array, `vCC` = index.
     *
     * `aput_test.smali`'s methods are already `v2, v0, v1`, so unlike `aget_test.smali` (whose corpus
     * methods are all `v0, v0, v1`, which hid a dest/array transposition from the entire suite) no new
     * fixture is needed -- but a test whose NAME is about operand order still is, because the coverage
     * would otherwise be incidental. Distinct VALUES in all three registers are what make each
     * transposition visible, and each is visible for its own reason:
     *  - value <-> array (`aput v0, v2, v1`): the "array" is `Known(0x44, "I")`, a provably-non-null
     *    non-array, so nothing is stored and v2 degrades to `Unknown(I)`. The array's element 1 and v2 are
     *    both wrong.
     *  - value <-> index (`aput v1, v0, v2`): the index becomes 0x44 = 68, far past the length, so the store
     *    is a DEFINITE bounds exception, no path returns, and there is no array consensus at all.
     *  - array <-> index (`aput v2, v1, v0`): the "array" is `Known(1, "I")` -- non-zero, so not Dalvik's
     *    null -- and lands in the same non-array row, degrading v1.
     *
     * `intArrayOf`, not `arrayOf`: this test asserts the ARRAY register's surviving value, so it reaches
     * `CoreTester`'s runtime-type-assignable-to-declared-type check, and a host `Integer[]` is not
     * assignable to `[I` (primitive arrays are invariant, [JLS] §4.10.3).
     */
    @Test
    fun theThreeOperandsAreNotInterchangeable() {
        initial.setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 1, "I", 2, 0x44, "I")
        expected.setRegisters(0, intArrayOf(0x11, 0x44, 0x33), "[I", 1, 1, "I", 2, 0x44, "I")
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    /**
     * The liveness declaration, which differs from every other op landed so far: all THREE operands are read
     * and **no register is assigned**, because an `aput` stores into the array OBJECT.
     * `Op.destinationRegisters` is not involved anywhere -- there is no destination and no wide pair to
     * declare.
     *
     * **This diverges from the oracle's `registersAssignedCount = 1`, and the divergence is the point.** The
     * oracle's `node.state.assignRegister(arrayRegister, array)` is load-bearing *there* because the oracle
     * keeps the live host array in the register; this core keeps a `HeapRef` there and the elements in the
     * heap, so writing it back is a no-op. Mirroring the oracle would copy the syntax and drop the reason.
     * Nothing observable depends on it: no migrated expectation reads liveness, and the differential does not
     * project it (`SmaliVmUnderTest`'s `Fixture` KDoc).
     *
     * **Changed in fix round 1** (was `…AndWritesBackOnlyTheArrayRegister`, asserting `setOf(0)`). Both halves
     * stay falsifiable whichever way it is decided: a declaration of `setOf(arrayRegister)` fails the second
     * assertion, and dropping the value register fails the first.
     *
     * Driven on a fixture whose three registers are distinct, so `registersRead` cannot be satisfied by a
     * decoder that collapsed two operands onto one number.
     */
    @Test
    fun theStoreReadsAllThreeOperandsAndAssignsNothing() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1), "[I", 1, 0, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(setOf(0, 1, 2), graph.registersRead(0), "value, array and index are all read")
        assertEquals(emptySet<Int>(), graph.registersAssigned(0), "and an aput assigns no register at all")
    }

    /**
     * **The effect level, and the reason the mechanism carrying it exists at all** (fix round 1, review
     * finding F1). A store whose array register is not provable lands on an object the engine cannot name, so
     * some *other* tracked object's contents may have changed and no heap state records it. The only sound
     * accounting for that is `SideEffectLevel.STRONG` -- spec §2.2's *"when it cannot prove the absence of a
     * side effect, the effect is `STRONG`"*, and spec §4.5's *"Aggregation errs upward."* (Both quoted
     * verbatim, comma included. An earlier revision rendered the second as "effects err upward", which is the
     * same rule in different words -- and this family has shipped a fabricated citation before, so a
     * paraphrase does not go inside quote marks.)
     *
     * **This is the common case, not a corner case, which is what makes it load-bearing.**
     * `Frame.entryFrame` seeds every parameter `Value.Unknown` and no opcode in this build turns a parameter
     * into a tracked array, so **every store into a caller-supplied array reaches this row**:
     * `void f(int[] a) { a[0] = 5; }` is the commonest `aput` in real code. Before this fix such a method
     * reported `Complete` *and* `methodSideEffectLevel() == NONE` -- exactly the pair `AnalysisContracts`'
     * own KDoc says lets a strategy *"fold a constant, or delete a method"*, here deleting a method whose
     * entire observable behaviour is a write its caller can see. And it was a REGRESSION rather than an
     * unfinished feature: while `aput` was an `UnsupportedOp` the same input aborted `Partial`, so
     * `trusted()` was null and `sideEffectLevel` answered `STRONG` -- the engine refused to answer. Landing
     * the opcode without this replaced a sound refusal with an unsound answer.
     *
     * The address-2 assertion is not padding: the level is transported on the `Frame`, so if
     * `ExecutionSession` stopped clearing it before copying successor frames (or `Frame.copy` started
     * carrying it), every descendant address would report `STRONG` too, silently.
     */
    @Test
    fun anUnprovableArrayMakesTheStoreStrongEvenThoughTheRunCompletes() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[I", 1, 0, "I", 2, 5, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "the run really does complete -- that is the danger")
        val trusted = graph.trusted()
        assertNotNull(trusted, "and it really does hand out a trusted view")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "a store through an unprovable array reference is an unmodellable write",
        )
        assertEquals(
            SideEffectLevel.STRONG, trusted!!.methodSideEffectLevel(),
            "so the method is NOT safe to delete, which is the query a strategy actually asks",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(2),
            "and the effect belongs to the store's own address, never leaking to its successors",
        )
    }

    /**
     * A store into a tracked array at a provable index is modelled exactly, but an alias can observe it.
     * `WEAK` distinguishes that known guest-visible mutation from `STRONG` unmodelled state.
     */
    @Test
    fun aProvableStoreIntoATrackedArrayDeclaresAnObservableWeakEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1), "[I", 1, 0, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "an alias can observe the tracked write")
        assertEquals(SideEffectLevel.WEAK, graph.trusted()!!.methodSideEffectLevel())
    }

    /**
     * A zero-length array cannot be stored into at ANY index, so an unprovable index there is a **definite**
     * bounds exception and the fall-through is provably infeasible (review finding F4). Forking it would keep
     * a dead block alive -- the milder half of the invents-a-reachable-path mirror defect: not as bad as
     * inventing a live `catch`, but still a path Dalvik cannot take, and this row's own reasoning ("the write
     * landed on exactly one slot") is false when there is no slot.
     *
     * `AGetOpTest.aZeroLengthArrayCannotBeReadAtAnyIndexSoTheFallThroughIsInfeasible` is the twin; the two
     * ops carry the same guard because they must agree about feasibility.
     */
    @Test
    fun aZeroLengthArrayCannotBeStoredIntoAtAnyIndexSoTheFallThroughIsInfeasible() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(), "[I", 1, CoreState.UNKNOWN, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(2), "no index can be in range, so the store cannot happen")
        assertEquals(0, graph.nodePile(0).first().children.size, "and there is no successor to explore")
    }

    /**
     * The `aput` twin of `AGetOpTest.aFloatIndexIsReadByItsBits`: `1.9f` is `0x3FF33333`, i.e. 1072902963, out
     * of bounds for a 2-element array, so the store definitely throws and no slot changes. A value conversion
     * would store into index 1. **VERIFIER**-excluded input ([CON] B1; [ART] `VerifyAPut`).
     */
    @Test
    fun aFloatIndexIsReadByItsBits() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 7), "[I", 1, 1.9f, "F", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph))
        assertEquals(1, graph.outcomes.size, "index 1072902963 is definitely out of bounds: $graph")
    }

    /**
     * `aput-byte` of a register holding a computed `Float` stores the low byte of its 32 bits: `5.9f` is
     * `0x40BCCCCD`, so the slot holds `0xCD`, i.e. -51. A value conversion would store 5. **VERIFIER**-excluded
     * input ([CON] B1; [ART] `VerifyAPut`). The effect level is asserted because the value changes and the
     * effect must not: the store lands in a tracked slot at a provable index.
     */
    @Test
    fun aFloatValueStoresTheLowByteOfItsBits() {
        initial.setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, 5.9f, "F")
        val graph = CoreTester.execute(CLASS_NAME, "putByte()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Known((-51).toByte(), BYTE), array.element(0))
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "the heap models the store, but an alias can observe it")
    }

    // ------------------------------------------------------------------------------------------
    // The 6 ported definite-throw migrations. All six drive `putWithCatch()V`, which declares a
    // `Ljava/lang/Exception;` handler around the `aput` (`aput_test.smali`), and NPE / AIOOBE / ASE are all
    // `Exception`s -- so all six are CAUGHT and produce NO `Outcome.Threw` at all. That is why they use
    // `CoreTester.testCaught` and never `testThrows`, which asserts the opposite (that the throw ESCAPED)
    // and would fail on arrival here. **The distinction is the FIXTURE's handler, not the exception type**:
    // the new tests further down drive the handler-free `putObject()V` and therefore may use `testThrows`.
    //
    // Every address and register is derived from the fixture, not from a sibling task: `aput` is 23x = TWO
    // code units, so `putWithCatch()V` is `aput`@0, `nop`@2, `return-void`@3, `move-exception v0`@4,
    // `goto`@5. Hence `unreachedAddress = 2` and `caughtInRegister = 0`; address 1 does not exist.
    // ------------------------------------------------------------------------------------------

    /**
     * A definitely-null array is a caught `NullPointerException`, and nothing is written
     * (**INHERITED**, [JVMS] §6.5 `iastore`).
     *
     * Its shape sibling in this file is
     * [aDefinitelyNullArrayThrowsNullPointerExceptionBeforeLookingAtTheIndex], which carries the ORDER claim
     * (index 10 against a null array); this one carries the corpus's exact claim, index 0 included.
     */
    @Test
    fun nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I", 2, 0, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /** The bounds half of the same shape: a provable index past the end, caught, nothing written. */
    @Test
    fun outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, 0, "I")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /**
     * The first of the four `ArrayStoreException` migrations: a `Ljava/lang/String;`-typed register stored
     * into an `[I` by a **plain** `aput`.
     *
     * **This input is verifier-invalid, and that is what makes the engine's answer a CHOICE rather than a
     * fidelity claim.** A reference register as the source of an int-like `aput` is
     * `VERIFY_ERROR_BAD_CLASS_HARD` ([CON] B1; [ART] `VerifyAPut`), so there is no Dalvik behaviour here to
     * match and the INHERITED rule that only `aastore` throws ASE does not reach this input at all. Given a
     * free choice, refusing loudly beats the alternative -- letting the store succeed would write a `String`
     * into an element the engine has typed `I`, a type lie every later `aget` propagates and no consumer can
     * detect (spec §2.2 prefers the over-approximating error). It also keeps four corpus values
     * byte-identical. Never write this down as "Dalvik throws here".
     *
     * **The message is the value type's binary name, and it is a PINNED ENGINE CHOICE.**
     * `Tester.testSimpleException` never checked a message, so this one is new rather than migrated; it
     * matches what the engine being replaced computed (`ClassNameUtils.internalToBinary(store.type)`) and it
     * deliberately does NOT match [ART], whose ASE text is a two-class *"... cannot be stored in an array of
     * type ..."* form. Asserted once, here, so it cannot drift silently; the other three ASE migrations do
     * not repeat it, because a second copy of the same pin adds no discrimination.
     */
    @Test
    fun incompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayStoreException;",
            message = "java.lang.String",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /**
     * **PIN 1 of 3 for the SHAPE refusal going FIRST**, and it is the half that is NOT the inherited order:
     * index **10** into an `IntArray(5)` *and* an incompatible reference value, and the answer is ASE rather
     * than `ArrayIndexOutOfBoundsException`.
     *
     * Say which half, because the two are easy to conflate: this pins that the **VERIFIER-tier** refusal is
     * resolved ahead of the null and bounds rows. It does **not** pin the inherited NPE -> AIOOBE -> ASE
     * order, which the `aastore`-tier refusal obeys and which
     * [aVerifiedObjectStoreWithABadIndexThrowsBoundsBeforeArrayStore] pins separately. Fold the two refusals
     * into one and exactly one of these two tests fails, whichever way you fold.
     */
    @Test
    fun outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, "wrong type", "Ljava/lang/String;")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayStoreException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /**
     * **PIN 2 of 3**, the same point with an UNKNOWN value: the shape refusal reads the value's declared
     * TYPE, never its provability, so an `Unknown(Ljava/lang/String;)` refuses exactly as a
     * `Known("wrong type", Ljava/lang/String;)` does -- and still ahead of the seeded index 10.
     */
    @Test
    fun unknownValueItemWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, CoreState.UNKNOWN, "Ljava/lang/String;")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayStoreException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /**
     * **PIN 3 of 3, and the strongest of the three**, because it makes the point by being **definite** where
     * everything else about the input is not. The array register is `Unknown([I)`, which is the
     * unprovable-array row -- the row that forks `Continue` + NPE + AIOOBE. Resolving the shape refusal first
     * replaces all three with a single ASE, and `testCaught` can only pass if the caught value every
     * returning path agrees on is *that* ASE: were the row's three transitions still produced, register 0
     * would join the caught throwable against the fall-through's array reference and there would be no
     * consensus at all.
     *
     * Sound as well as byte-identical: the component comes from the register's `[I` tag, which is only ever a
     * SUPERTYPE of the array's real component, and a value not assignable to the tag's component is not
     * assignable to any narrower one either -- so the refusal does not depend on the array being provable.
     */
    @Test
    fun unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, CoreState.UNKNOWN, "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;")
        CoreTester.testCaught(
            CLASS_NAME, "putWithCatch()V", initial,
            exceptionType = "Ljava/lang/ArrayStoreException;",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        assertHasOneChildAndDeclaresNoAssignment("putWithCatch()V", initial)
    }

    /**
     * **The third REPLACED migration** (was `unknownValueItemMakesArrayUnknownAndThrowsExceptions`), and
     * both halves of the old name are the ORACLE's answer rather than this engine's, which is why the name
     * changed with it: the input is byte-identical and only the answer moved.
     *
     * The engine being replaced marked the WHOLE array `Unknown` whenever the value was unknown, and then
     * forked `Continue` + NPE + AIOOBE off a provable, in-range store -- three children and two exception
     * children, which is what that test asserted. This core takes the ordinary in-range store row: the array
     * stays `Known`, exactly the slot that was written goes `Unknown(I)`, and **nothing forks at all**, so
     * the fixture's `Ljava/lang/Exception;` handler at address 4 is DEAD on this input. Registered as
     * `DivergenceShape.NEW_CORE_MORE_PRECISE`; the values half is
     * [anUnknownValueMakesOnlyItsOwnElementUnknown] and this is the fork-shape half, on the catching fixture.
     *
     * It also pins something only Task 9 could break: an `Unknown` value must not make the store check
     * **unanswerable**. Its declared type is `I`, the component is `I`, both are primitive, so the answer is
     * PERMITTED -- reading "the value is not provable" as "the subtype question is not answerable" would fork
     * an ASE here and light up a handler Dalvik never enters.
     */
    @Test
    fun unknownValueItemMakesOnlyItsOwnElementUnknownAndThrowsNothing() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 0, "I", 2, CoreState.UNKNOWN, "I")
        val graph = CoreTester.execute(CLASS_NAME, "putWithCatch()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "a provable in-range store of an unknown value throws nothing")
        assertEquals(1, graph.nodePile(0).first().children.size, "one successor: the fall-through, and no fork")
        assertTrue(graph.wasReached(2), "the store's own fall-through")
        assertFalse(graph.wasReached(4), "and the handler is dead on this input -- there is nothing to catch")
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(INT), array.element(0), "the written slot")
        assertEquals(
            List(4) { Value.Known(0, INT) }, (1 until 5).map { array.element(it) },
            "and only the written slot -- the oracle answered Unknown for the whole array",
        )
    }

    // ------------------------------------------------------------------------------------------
    // The ArrayStore rows no corpus test reaches. Four of these six exist because the corpus's ASE fixtures
    // are all VERIFIER-INVALID (a reference value through a plain `aput`), so they exercise the engine's own
    // choice and say nothing at all about the inherited `aastore` rule -- which is the rule a real
    // `aput-object` obeys, and the one a deobfuscator's `catch (ArrayStoreException)` depends on.
    //
    // These drive the handler-free `putObject()V` / `putWide()V`, so `testThrows` and `graph.outcomes` are
    // the right queries: the throws genuinely ESCAPE. Again, the distinction from the six migrations above
    // is the fixture's handler, not the exception type.
    // ------------------------------------------------------------------------------------------

    /**
     * **Bug class, and the one the object-over-tag rule exists to prevent: a covariant array whose REGISTER
     * TAG hides an ASE that a real device throws.** A `String[]` sitting in a register tagged
     * `[Ljava/lang/Object;` is legal covariance (**INHERITED**, [JLS] §4.10.3 -- the verifier proves the
     * declared and runtime types COMPATIBLE, never equal), and `aastore` tests the **actual** component type
     * of the array ([JVMS] §6.5), so storing an `Integer` into it throws.
     *
     * A declared-types-only check reads the component as `Ljava/lang/Object;`, permits the store, and **DROPS
     * A FEASIBLE THROW** -- the worst outcome this engine can have (spec §2.2). Discriminating by
     * construction: derive the component from the tag instead of from the tracked array and this test goes
     * green-to-red, which no other test in this file can say.
     *
     * The message is the same PINNED ENGINE CHOICE as
     * [incompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters]'s, asserted a second
     * time here only because a *different* rule produces it -- the `aastore`-tier refusal rather than the
     * shape refusal -- and one message helper serving both is worth pinning from both sides.
     */
    @Test
    fun aCovariantlyTaggedArrayStillThrowsForAnIncompatibleComponent() {
        CoreTester.testThrows(
            CLASS_NAME, "putObject()V",
            CoreState().apply {
                setRegisters(0, arrayOf("Caladan"), "[Ljava/lang/Object;", 1, 0, "I", 2, 42, "Ljava/lang/Integer;")
            },
            exceptionType = "Ljava/lang/ArrayStoreException;",
            message = "java.lang.Integer",
        )
    }

    /**
     * An unknown value's tag is only an upper bound on its class: `Unknown(Ljava/lang/Object;)` may hold a
     * `String`, so storing it into a `String[]` may succeed or throw (`aastore`, [JVMS] §6.5). Refusing it
     * from the tag alone invents a definite `ArrayStoreException` and drops the store.
     */
    @Test
    fun anUnknownValueWhoseTagIsNotAssignableMayStillBeStored() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, arrayOfNulls<String>(1), "[Ljava/lang/String;", 1, 0, "I", 2, CoreState.UNKNOWN, "Ljava/lang/Object;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/ArrayStoreException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.wasReached(2), "the store may succeed, so the fall-through is explored")
    }

    /**
     * **The inherited half of the ordering, which the corpus cannot see at all.** For a VERIFIED store the
     * order is NPE -> AIOOBE -> ASE ([JVMS] §6.5 `aastore`'s *"Otherwise"* chaining, the same normative
     * device Plan 7's design decision 4 uses for `new-array`), so an `aput-object` with BOTH a bad index and
     * an unassignable reference value throws `ArrayIndexOutOfBoundsException`, not ASE.
     *
     * The corpus covers only the mirror case --
     * [outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters],
     * a verifier-INVALID mixed store where the ASE is our own choice and goes first -- so without this test
     * the two orders are indistinguishable and an implementer folding both refusals into one `REFUSED` has a
     * 50% chance of being wrong with no test to say so.
     *
     * **"Strictly precedes" is an EXCLUSIVITY claim, and this test asserts the whole escaping list rather
     * than using [CoreTester.testThrows] for exactly that reason (review finding C1).** `testThrows` asserts
     * `matching.isNotEmpty()`, i.e. *existence* -- so appending a second escaping throw of a different type
     * is invisible to it, and this test was green while the mutation that matters (row 5's definite bounds
     * exception ALSO forking the ASE) killed nothing in a 844-test suite. An exception order can only be
     * pinned by a query that can see a throw that should not be there.
     */
    @Test
    fun aVerifiedObjectStoreWithABadIndexThrowsBoundsBeforeArrayStore() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, arrayOf("Caladan"), "[Ljava/lang/String;", 1, 10, "I", 2, 42, "Ljava/lang/Integer;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"), CoreTester.escapingTypes(graph),
            "AIOOBE strictly PRECEDES the aastore refusal -- it does not merely accompany it",
        )
        assertFalse(graph.wasReached(2), "and the store cannot happen either")
    }

    /**
     * The NPE end of the same claim, and the edge that had **no witness at all** until review finding C1: no
     * test paired a definitely-null array with a feasible `ArrayStoreException`. The three null-array tests
     * all store primitive values into an `[I`, where the store check answers PERMITTED, so a row-2 mutation
     * that appended the ASE fork was inert for every one of them.
     *
     * NPE strictly precedes ASE ([JVMS] §6.5 `aastore`'s *"Otherwise"* chaining) and a null array stores
     * nothing, so there is no store left to refuse -- an `Integer` into a `[Ljava/lang/String;` through a
     * null reference throws the `NullPointerException` and only that. Asserting the whole escaping list, not
     * `testThrows`, for the reason [aVerifiedObjectStoreWithABadIndexThrowsBoundsBeforeArrayStore] states:
     * the claim is exclusivity, and existence cannot express it.
     *
     * Driven on the handler-free `putObject()V` so the throw escapes and `graph.outcomes` can see it -- the
     * distinction from the six `testCaught` migrations is the fixture's handler, not the exception type.
     */
    @Test
    fun aNullArrayThrowsNullPointerExceptionBeforeArrayStore() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, null, "[Ljava/lang/String;", 1, 0, "I", 2, 42, "Ljava/lang/Integer;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph),
            "NPE strictly PRECEDES the aastore refusal, and a null array leaves no store to refuse",
        )
        assertFalse(graph.wasReached(2), "nor any fall-through")
    }

    /**
     * **Bug class: an unloadable component class making the subtype question unanswerable.** Unlike
     * `new-array`'s loadability check -- where the class's ABSENCE is a fact under spec §3's no-multidex
     * scope, so the throw is definite and does not fork -- *"is X a subtype of Y"* with an absent ancestor is
     * genuinely undecidable, so both outcomes are feasible and both are explored. Same adjudication as Plan
     * 3's unloadable CATCH type (`ThrowRoutingTest.anUnloadableCatchTypeIsAmbiguityNotAHostFailure`) and the
     * same reason: letting it reach the host-failure boundary instead would make the whole run `Partial` and
     * switch off every consumer's trust in the method because one descriptor was missing.
     * `Lspine/absent;` is the descriptor Plan 3's fixture already uses (`spine.smali`'s
     * `throwUnloadableCatchType`), reused so there is one absent class in the tree rather than two.
     *
     * **The brief asked for a new `putIntoUnloadableComponentArray()V` fixture and it is not needed: its
     * prescribed body is byte-identical to `putObject()V`** (`aput_test.smali` -- `.locals 3`,
     * `aput-object v2, v0, v1`, `return-void`), so adding it would have been a pure duplicate. That
     * byte-identity is the whole reason, and it is the only one.
     *
     * **A CORRECTION AN EARLIER REVISION OF THIS COMMENT GOT WRONG, kept visible because a later plan would
     * inherit it: a TRACKED array CAN carry an unloadable component type.** This test puts the unloadable
     * descriptor in the register TAG on an unprovable array, and the earlier text justified that by claiming
     * the alternative was impossible -- that `CoreState` always reads an array's true type off the host
     * array's class. It does not: `CoreState.trueTypeOf`'s own KDoc documents the exception, and when any
     * element is the `UNKNOWN` sentinel the **declared tag wins**, because there is no authoritative guest
     * array behind it. So `arrayOf(CoreState.UNKNOWN)` tagged `"[Lspine/absent;"` really does produce a
     * `TrackedArray` whose `type` is `[Lspine/absent;`, and the component-side `ClassLoadFailure` really does
     * reach a *provable* array by that route. (What IS true is the other half: `new-array [Lspine/absent;`
     * throws `NoClassDefFoundError` without allocating -- design decision 4 -- so no *executed* opcode can
     * mint one.) Nothing about coverage changes, which is why the seed was left as it is: component-side and
     * value-side failures share one `try`/`catch` and one enum value in `APutOp`.
     *
     * What the tag-side seed pins that the tracked-array one would not: this lands on the unprovable-array
     * row, so the ASE **joins** that row's NPE and AIOOBE rather than replacing them -- exactly the insertion
     * point Task 8's seam called out as the one an implementer would miss.
     *
     * Address 2, not 1: `aput-object` is 23x = TWO code units, so `return-void` is at 2 and address 1 does
     * not exist -- `wasReached(1)` would be silently false and would fail for the wrong reason.
     */
    @Test
    fun anUnloadableComponentTypeIsAmbiguityNotAHostFailure() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[Lspine/absent;", 1, 0, "I", 2, "a string", "Ljava/lang/String;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "an absent descriptor is input, not a bug")
        assertTrue(graph.wasReached(2), "the normal store must still be explored")
        assertEquals(
            listOf(
                "Ljava/lang/NullPointerException;",
                "Ljava/lang/ArrayIndexOutOfBoundsException;",
                "Ljava/lang/ArrayStoreException;",
            ),
            CoreTester.escapingTypes(graph),
            "unanswerable means BOTH outcomes are feasible, and it does not cancel the row it lands on",
        )
    }

    /** A direct immutable reference uses its exact runtime class instead of an imprecise declared tag. */
    @Test
    fun directImmutableRuntimeTypeMakesStoreDefiniteDespiteUnloadableTag() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, arrayOfNulls<String>(1), "[Ljava/lang/String;", 1, 0, "I", 2, "a string", "Lspine/absent;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "the store may well succeed, so its path is feasible")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        val trusted = graph.trusted()
        assertNotNull(trusted, "the forked ASE is an outcome, not a truncation")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(
            Value.Known("a string", STRING), array.element(0),
            "the runtime String class proves that this store succeeds",
        )
    }

    /** Runtime String identity removes store ambiguity while an unknown index still forks bounds failure. */
    @Test
    fun anUnprovableIndexWithDirectStringOnlyForksBoundsFailure() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, arrayOfNulls<String>(2), "[Ljava/lang/String;", 1, CoreState.UNKNOWN, "I", 2, "a string", "Lspine/absent;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "the store may land in range and may type-check")
        assertEquals(
            listOf("Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(graph),
            "the runtime String class proves the store type-checks",
        )
    }

    /** Runtime String identity also prevents a speculative store failure on an unmodelled array shape. */
    @Test
    fun aKnownNonArrayWithDirectStringHasNoSpeculativeStoreFailure() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, "not an array at all", "[Ljava/lang/String;", 1, 0, "I", 2, "a string", "Lspine/absent;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "nothing about this input is provable enough to refuse outright")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the write still landed somewhere the engine cannot name",
        )
    }

    /**
     * The complement of [anUnloadableComponentTypeIsAmbiguityNotAHostFailure]: when the assignability
     * question IS answerable and the answer is no, the store *definitely* throws, so the unprovable-array
     * row's `Continue` becomes **infeasible** and is not explored. The array may still be null and the index
     * may still be out of range, so those two throws stay.
     *
     * This is the mirror defect in its milder form (review finding F4's shape): forking a fall-through the
     * store cannot take keeps a dead block alive. Sound for the same reason
     * [unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters] is --
     * the tag's component is a supertype of the real one, so a refusal against it holds for any narrower
     * component. **The residual runs the other way and is recorded as a carry-forward**: the VALUE's tag is
     * likewise only a supertype of its runtime class, so a `Known(hostString, "Ljava/lang/Object;")` stored
     * into a `[Ljava/lang/String;` is refused here while `aastore` would permit it -- an invented infeasible
     * throw, the lesser of the two errors but still one. Plan 9's tracked guest objects close it, by
     * preferring the object's own type on the value side exactly as this rule already does on the array side.
     */
    @Test
    fun anUnprovableArrayWithADefinitelyUnassignableValueExploresNoNormalPath() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[Ljava/lang/String;", 1, 0, "I", 2, 42, "Ljava/lang/Integer;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(
                "Ljava/lang/NullPointerException;",
                "Ljava/lang/ArrayIndexOutOfBoundsException;",
                "Ljava/lang/ArrayStoreException;",
            ),
            CoreTester.escapingTypes(graph),
        )
        assertFalse(graph.wasReached(2), "the store cannot complete, so there is no fall-through to explore")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "STRONG even though every transition throws -- the deliberate over-approximation `APutOp`'s row 1 " +
                "explains, hedging the value-side residual that makes this refusal only as definite as a tag",
        )
    }

    /**
     * **The one shape review finding m2 turned up: Dalvik's integral zero is null only under the `OBJECT`
     * suffix**, so a **plain** `aput` of `Known(0, "I")` into a reference-component array is refused by the
     * shape rule rather than permitted by the null rule.
     *
     * The bug it prevents is subtle enough to be worth a test of its own. `isDalvikNull` recognises the
     * integer 0, and [BC]'s *"In terms of bitwise representation, `(Object) null == (int) 0`"* is a claim
     * about a REFERENCE context -- which is why `isDalvikNull`'s own KDoc says it exists for `aput-object`'s
     * null rule. Applied to every suffix, the null rule *pre-empted* the shape rule and stored
     * `Known(0, "Ljava/lang/String;")`: an `Integer` raw under a reference type, which is exactly the "type
     * lie every later `aget` propagates" that the shape refusal exists to prevent, manufactured by the one
     * rule meant to be its exception.
     *
     * Verifier-invalid input either way ([CON] B1 -- an int-like `aput` requires a primitive-component
     * array), so no fidelity claim moves and this is the engine's own choice made consistent with itself. The
     * message is the value type's binary name as always, here `"int"`, which is also the only assertion in
     * the file that exercises `binaryNameOf`'s primitive rows.
     *
     * [canPutConstZeroNullObject] is the other side and must keep passing: same zero, same reference
     * component, but through `aput-object`, where the null rule is exactly right.
     */
    @Test
    fun aPlainAputOfDalviksZeroIntoAReferenceArrayIsRefusedRatherThanStoredAsNull() {
        CoreTester.testThrows(
            CLASS_NAME, "put()V",
            CoreState().apply {
                setRegisters(0, arrayOfNulls<String>(1), "[Ljava/lang/String;", 1, 0, "I", 2, 0, "I")
            },
            exceptionType = "Ljava/lang/ArrayStoreException;",
            message = "int",
        )
    }

    /**
     * **No store whose value and component are BOTH primitive throws `ArrayStoreException`, whatever the
     * mnemonic** -- `aput-wide` of a `J` into a `[D` included. **INHERITED** + [ART]: [JVMS] §6.5 lists only
     * NPE and AIOOBE for the primitive array-store instructions, and [ART]'s `APUT_OBJECT` is the only
     * handler carrying an assignability check at all (the other six route to a `HandleAPut<T>` that has
     * none). Both halves resolve in `docs/DALVIK-SEMANTICS.md`'s
     * `ArrayStoreException comes from aput-object and nothing else` subsection, which is this repo's
     * verified-quote registry.
     *
     * **A third source was cited here and has been REMOVED as ungrounded (review finding I2), which is worth
     * recording so nobody restores it from a memory of having read it.** The clause claimed that *"`nterp`'s
     * `array.S` corroborates by calling `art_quick_aput_obj` for the object case only"*. It came from this
     * task's brief and appears nowhere in the registry, and it does not resolve: on
     * `platform/art` at `refs/heads/main`, `runtime/interpreter/mterp/arm64/array.S` and
     * `.../x86_64/array.S` both answer **HTTP 404**, while `runtime/interpreter/mterp/nterp.cc` fetches
     * (HTTP 200, raw, base64-decoded -- not through a summarizing tool) and contains **zero** occurrences of
     * `art_quick_aput_obj` and zero of `aput`. That is not evidence the underlying behaviour is wrong; it is
     * evidence the citation cannot be checked, which is the only thing that matters for a claim in the tree.
     * No registry row was added, because there was nothing verified to put in one.
     *
     * The corpus cannot see this: `canPutWideWithLong` seeds `[J`/`J` and `canPutWideWithDouble` seeds
     * `[D`/`D`, so every one of its wide fixtures has value and component IDENTICAL, and an implementation
     * that exempted only `{Z B C S I}` -- which is what the engine being replaced did, and what this plan's
     * own first draft prescribed -- would pass all three while throwing here.
     *
     * The value stored is deliberately not asserted: `PLAIN`/`WIDE` keep the raw untouched while the
     * element's declared type comes from the ARRAY, so this writes `Known(5L, "D")`, the known
     * verifier-excluded `agreesWith` precondition violation `APutOp.coerce`'s KDoc documents. That is a
     * separate question from the throw one, and this test answers only the throw.
     */
    @Test
    fun aWideStoreOfALongIntoADoubleArrayThrowsNothingBecauseBothSidesArePrimitive() {
        val graph = CoreTester.execute(CLASS_NAME, "putWide()V", CoreState().apply {
            setRegisters(0, DoubleArray(1), "[D", 1, 0, "I", 2, 5L, "J")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "a wide primitive store has no ArrayStoreException to throw")
        assertTrue(graph.wasReached(2), "and the store itself happens")
    }

    /**
     * The other way the component goes undeterminable, and the pair of answers that keeps *"cannot tell"*
     * from collapsing into either extreme. The array register is tagged `Ljava/lang/Object;` -- not an array
     * descriptor at all -- and holds no tracked object, so there is no component to test against.
     *
     *  - With a **non-null reference** value the answer is UNANSWERABLE, not PERMITTED. For verified input
     *    the suffix guarantees the shapes agree (**VERIFIER**: `aput-object` requires a reference-component
     *    array, every other suffix requires a primitive-component one), so both sides are references and the
     *    subtype question is simply unavailable -- which must not silently become "no throw".
     *  - With a **null** value the answer is PERMITTED regardless, because storing null never throws ASE:
     *    **INHERITED** ([JVMS] `aastore`: *"If value is null, then value is stored as the component of the
     *    array at index"*), corroborated by [ART]'s `CheckAssignable` being guarded by
     *    `if (object != nullptr)`. Without this half, the undeterminable-component rule would invent an
     *    infeasible ASE for `Object o = null; a[i] = o;` -- ordinary code, not a corner case.
     *
     * The two seeds differ in exactly one operand, so the pair is discriminating rather than illustrative.
     * Contrast [canPutIntoUnknownValueOfObjectTypeWithoutThrowingException], which is the same
     * undeterminable component with a PRIMITIVE value: permitted, because a primitive value can only arrive
     * through a non-`OBJECT` suffix and every one of those requires a primitive-component array.
     */
    @Test
    fun anObjectStoreThroughAnArrayRegisterWithNoArrayTypeIsUnanswerableUnlessTheValueIsNull() {
        val referenceValue = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/Object;", 1, 0, "I", 2, "a string", "Ljava/lang/String;")
        })
        assertEquals(CompletionStatus.Complete, referenceValue.completion())
        assertEquals(
            listOf(
                "Ljava/lang/NullPointerException;",
                "Ljava/lang/ArrayIndexOutOfBoundsException;",
                "Ljava/lang/ArrayStoreException;",
            ),
            CoreTester.escapingTypes(referenceValue),
            "no component to test against is not the same as nothing to throw",
        )
        val nullValue = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/Object;", 1, 0, "I", 2, null, "Ljava/lang/String;")
        })
        assertEquals(CompletionStatus.Complete, nullValue.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(nullValue),
            "storing null never throws ArrayStoreException, so nothing is unanswerable about it",
        )
    }

    // ------------------------------------------------------------------------------------------

    /**
     * The two halves of the corpus's `…HasNoChildrenAndAssignsNoRegisters` that [CoreTester.testCaught] does
     * not check, re-expressed for this engine.
     *
     * **"has no children" is the corpus's phrasing for "no fall-through", not for "no successors".** A
     * definite throw the method's own handler CATCHES has exactly one child: the handler. `unreachedAddress`
     * catches an extra fall-through from the other side; the count catches an extra fork to a *different*
     * handler address, which `testCaught`'s type-filtered escape check cannot see.
     *
     * **"assigns no registers" survives only as the STATIC claim, and the reason is a HARNESS asymmetry worth
     * writing down rather than a decision.** `Tester.testSimpleException` asserted
     * `node.state.registersAssigned.size == 0` -- a claim about what THAT execution wrote, and a real one for
     * the engine being replaced, whose `aput` did reassign the array register on its continuing paths and
     * returned before doing so on its throwing ones. It was checkable there because that harness seeded
     * registers with `pokeRegister`, which records no liveness. [CoreTester.execute] seeds with
     * `Frame.assign`, which does -- so `frame.registersAssigned()` on the `aput` node already contains
     * `{0, 1, 2}` from the seed alone, and since the array register is always among the seeded ones, an op
     * that DID write it back would be invisible. The dynamic form is therefore not merely duplicative here,
     * it is inexpressible, and asserting it would have been a green test that checks nothing.
     *
     * So the assertion below is `ExecutionGraph.registersAssigned`, which is static: it answers from the
     * decoded `Op` and is the same for every input. It still fails when the op's declaration changes
     * (`setOf(arrayRegister)` kills it), which is why it is here rather than dropped -- but it is a weaker
     * claim than the corpus's, and the two places the dynamic claim genuinely survives are
     * [theStoreReadsAllThreeOperandsAndAssignsNothing] (the declaration, on its own terms) and
     * [anArrayRegisterWithNoTypeAtAllStillForksBothExceptionsAndIsNeverWritten], whose array register is
     * UNSEEDED and whose `assertNull(frame.peek(0))` is consequently the one falsifiable "this op wrote
     * nothing" assertion in the file.
     *
     * **Named for what it observes, not for what the corpus claimed (review finding m4).** An earlier name
     * -- `assertDefiniteCaughtThrowWroteNothing` -- promised a write check that neither assertion performs:
     * reinstating a `frame.assign(arrayRegister, …)` inside `execute` while leaving the declaration
     * `emptySet()` leaves all six migrations green. The KDoc said so honestly and the name contradicted it,
     * and a later reader trusts the name.
     */
    private fun assertHasOneChildAndDeclaresNoAssignment(methodDescriptor: String, initial: CoreState) {
        val graph = CoreTester.execute(CLASS_NAME, methodDescriptor, initial)
        assertEquals(1, graph.nodePile(0).first().children.size, "a DEFINITE caught throw has one child: the handler")
        assertEquals(emptySet<Int>(), graph.registersAssigned(0), "and an aput declares no register assigned")
    }

    /**
     * Every ESCAPING throw's MESSAGE, in the same order. Read off the `aput` node's own frame rather than
     * through `terminatingHeapObject`: the throwable was synthesized there, and heap consensus reconciles only
     * RETURNING paths, which an escaping throw by definition is not.
     */
    private fun escapingMessages(graph: ExecutionGraph): List<String?> =
        graph.outcomes.filterIsInstance<Outcome.Threw>().map { threw ->
            val ref = (threw.exception as Value.Known).raw as HeapRef
            val obj = graph.nodePile(0).first().frame.heapObject(ref)
            assertTrue(obj is HeapObject.GuestThrowable, "no synthesized throwable at $ref; got $obj")
            (graph.nodePile(0).first().frame.heap.hostObjectAt(ref) as Throwable).message
        }

    /**
     * The three migrations that store an ARRAY into an array, where the stored element is itself a `Known`
     * whose raw is a [HeapRef]. A plain `assertEquals` against `CoreState`'s host-array expectation cannot
     * work for these: `CoreState.elementsOf` builds `Known(<host inner array>, component)` while the engine
     * -- correctly, per spec §4.1's "the raw is never the live host object" -- stores `Known(HeapRef, …)`.
     * So the element is resolved through heap consensus and ITS elements are compared.
     *
     * Deliberately a helper in this file rather than recursive comparison inside `CoreTester`: it is
     * specific to nested-array expectations, and `CoreTester` gaining a recursive comparator is scope creep
     * this plan does not need.
     *
     * [storedElementType] is the OUTER array object's component type, which is the point of asserting it:
     * it comes from the object, never from the value register's tag.
     */
    private fun assertStoredArrayElements(
        methodDescriptor: String,
        initial: CoreState,
        outerRegister: Int,
        outerLength: Int,
        index: Int,
        storedElementType: String,
        expectedInnerElements: List<Value>,
    ) {
        val graph = CoreTester.execute(CLASS_NAME, methodDescriptor, initial)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "exploration must complete before any consensus may be trusted",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val outerConsensus = trusted!!.terminatingConsensus(outerRegister)
        assertTrue(
            outerConsensus is Value.Known && outerConsensus.raw is HeapRef,
            "register $outerRegister should hold a tracked array, but was $outerConsensus",
        )
        val outer = trusted.terminatingHeapObject((outerConsensus as Value.Known).raw as HeapRef).agreed()
        assertTrue(outer is HeapObject.TrackedArray, "no array behind register $outerRegister; got $outer")
        assertEquals(outerLength, (outer as HeapObject.TrackedArray).length, "outer array length")
        val stored = outer.element(index)
        assertTrue(stored is Value.Known, "element $index should be a definite reference, but was $stored")
        assertEquals(
            VirtualType.of(storedElementType), (stored as Value.Known).type,
            "the stored element's declared type comes from the OUTER array object, not the value register's tag",
        )
        val innerRef = stored.raw
        assertTrue(
            innerRef is HeapRef,
            "a stored array must be a HeapRef and never a live host array (spec §4.1): $innerRef",
        )
        val inner = trusted.terminatingHeapObject(innerRef as HeapRef).agreed()
        assertTrue(inner is HeapObject.TrackedArray, "no array behind element $index; got $inner")
        assertEquals(expectedInnerElements, (inner as HeapObject.TrackedArray).elements())
    }

    /**
     * CARRY-FORWARD item 16, closed by Plan 9 Task 17 -- the task that made guest objects tracked. Rule 5
     * of the store check used to read the VALUE register's declared tag, and a tag is only ever a
     * SUPERTYPE of the runtime class (VERIFIER: the verifier proves `runtime <: declared`, never
     * equality) -- so a store `aastore` permits was REFUSED, an invented infeasible throw, the
     * "a handler is reachable when it is not" half of spec §2.2. The fix is the move the check already
     * makes on the ARRAY side: object over tag.
     *
     * The fixture builds the supertype tag with verifier-valid opcodes alone: `aput-object` into a
     * `[Ljava/lang/Object;` stores the element under the ARRAY's component type and `aget-object` reads
     * it back verbatim, so the round-tripped register is tagged `Ljava/lang/Object;` while the tracked
     * object behind the ref is `Laput_test;`. Storing THAT into a `[Laput_test;`:
     *  - by the tag: `Object` is not a subtype of `aput_test` -> REFUSED_BY_ASSIGNABILITY, a definite
     *    ASE escaping a handler-free method -- which is what this test fails with when the fix is
     *    reverted;
     *  - by the object's own type (and by JVMS `aastore`, which tests the ACTUAL class): permitted.
     */
    @Test
    fun aTrackedObjectsOwnTypeBeatsItsSupertypeTagInTheStoreCheck() {
        val graph = CoreTester.execute(CLASS_NAME, "putRoundTrippedInstanceIntoOwnTypeArray()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "aastore tests the ACTUAL class, so no ArrayStoreException is feasible here -- refusing " +
                "by the supertype TAG invents an infeasible throw (CARRY-FORWARD item 16): ${graph.outcomes}",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        // ...and the store really happened, to the SAME object the new-instance allocated: v0 holds the
        // [Laput_test; whose element 0 is the round-tripped ref.
        val arrayConsensus = trusted!!.terminatingConsensus(0)
        assertTrue(arrayConsensus is Value.Known && (arrayConsensus as Value.Known).raw is HeapRef)
        val array = trusted.terminatingHeapObject((arrayConsensus as Value.Known).raw as HeapRef).agreed()
        assertTrue(array is HeapObject.TrackedArray, "no array behind r0; got $array")
        val element = (array as HeapObject.TrackedArray).element(0)
        assertTrue(
            element is Value.Known && (element as Value.Known).raw is HeapRef,
            "the element must be the stored tracked object: $element",
        )
        assertEquals(
            VirtualType.of(CLASS_NAME), (element as Value.Known).type,
            "the stored element's declared type comes from the array's component, as every store's does",
        )
        val stored = trusted.terminatingHeapObject(element.raw as HeapRef).agreed()
        assertTrue(
            stored is HeapObject.TrackedObject && stored.type == VirtualType.of(CLASS_NAME),
            "the object behind the round trip is the allocation itself: $stored",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Laput_test;"
        private val INT = VirtualType.of("I")
        private val BYTE = VirtualType.of("B")
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }
}
