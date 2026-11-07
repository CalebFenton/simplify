package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * `instance-of` (op 32, format 22c = TWO code units). Migrated from
 * `org.cf.smalivm.opcode.InstanceOfOpTest` -- all seven tests, byte-identical expectations, keeping the
 * `@Nested` structure -- plus the rows the corpus never reached.
 *
 * **SPEC** [BC]: *"Store in the given destination register `1` if the indicated reference is an instance
 * of the given type, or `0` if not."* Result type is always `Z`; nothing branches
 * (`docs/DALVIK-SEMANTICS.md#instance-of-22c`).
 *
 * **The seven migrated tests do not cover a single one of the four rows that were WRONG in this plan's
 * first revision**, which is why the added tests below are the point of this file rather than a garnish:
 * a nullable `Unknown`, a primitive-typed source, a primitive type operand, and an array against
 * `Cloneable`/`Serializable`. `docs/architecture/opcodes.md:200` lists *"nullable unknown `instance-of`"*
 * among the behaviours no active verifier-valid VM test covers, and that was measured to be exactly true
 * here.
 */
class InstanceOfOpTest {
    companion object {
        private const val CLASS_NAME = "Linstanceof_test;"

        /** `instance-of` is `22c`, two code units, so the `return-void` is at 2 and not at 1. */
        private const val ADDRESS_INSTANCE_OF = 0
        private const val ADDRESS_RETURN = 2

        private val BOOL = VirtualType.of("Z")
        private val OBJECT = "Ljava/lang/Object;"
        private val STRING = "Ljava/lang/String;"
    }

    /**
     * The seven migrated tests, unchanged in expectation. `TestState` -> [CoreState] and `Tester` ->
     * [CoreTester] is the whole diff; a host array seed becomes a tracked array (spec §4.2), so the
     * "value type" these rows depend on is read off the HEAP OBJECT rather than off `raw.javaClass` --
     * which is the one place the core's reproduction of the oracle's `Value.declaredAndValueTypeNames`
     * is not a copy (a `HeapRef`'s `javaClass` is `HeapRef`).
     */
    @Nested
    @DisplayName("migrated from the oracle's InstanceOfOpTest")
    inner class Migrated {
        private lateinit var expected: CoreState
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun intArray2DIsInstanceOfObjectArray() {
            initial.setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT)
            expected.setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT, 1, true, "Z")
            CoreTester.test(CLASS_NAME, "instanceOfObjectArray()V", initial, expected)
        }

        @Test
        fun intArray2DIsNotInstanceOf2DObjectArray() {
            initial.setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT)
            expected.setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT, 1, false, "Z")
            CoreTester.test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected)
        }

        @Test
        fun intArrayWithObjectAsDeclaredTypeIsInstanceOfIntArray() {
            initial.setRegisters(0, IntArray(0), OBJECT)
            expected.setRegisters(0, IntArray(0), OBJECT, 1, true, "Z")
            CoreTester.test(CLASS_NAME, "instanceOfIntArray()V", initial, expected)
        }

        @Test
        fun objectIsNotInstanceOfString() {
            initial.setRegisters(0, IntArray(0), OBJECT)
            expected.setRegisters(0, IntArray(0), OBJECT, 1, false, "Z")
            CoreTester.test(CLASS_NAME, "instanceOfString()V", initial, expected)
        }

        /**
         * **The one migrated test whose expectation cannot name register 0, and the reason is a harness
         * rule rather than a behavioral change.** The seed is byte-identical to the corpus's -- a
         * `String[][]` tagged `Ljava/lang/String;` -- and that is the whole point of the test: the DECLARED
         * tag does not satisfy `[[Ljava/lang/Object;` while the array object's own type does, so this is
         * the migrated test that proves the value type is consulted at all.
         *
         * But `CoreTester.test`'s array branch asserts `isSubtypeOf(runtimeType, declaredTag)`, and this
         * pair is **incomparable in both directions**: a `String[][]` is not a `String`, and a `String` is
         * not a `String[][]`. Plan 6 chose assignability deliberately (a corpus tag may be more GENERAL
         * than the object -- `[Ljava/lang/Object;` for a `[Ljava/lang/String;` -- which is legal
         * covariance), and this corpus tag is not loose but impossible: no verifier would admit it. Same
         * class as the `"J"`-tagged `long[]` Plan 7 Task 7 found in `AGetOpTest.canGetWide`.
         *
         * So register 0 is dropped from the EXPECTATION only. Nothing behavioral is lost: `instance-of`
         * writes `vA` and nothing else, so `expected.setRegisters(0, <the seed>, <the tag>)` asserted "the
         * seed survived" -- which the other five array-seeded migrated tests still assert, because their
         * tags ARE assignable. The value being migrated, `Known(true, Z)` in v1, is byte-identical.
         */
        @Test
        fun stringArray2DIsInstanceOf2DObjectArray() {
            initial.setRegisters(0, arrayOfNulls<Array<String>>(0), STRING)
            expected.setRegisters(1, true, "Z")
            CoreTester.test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected)
        }

        @Test
        fun stringIsInstanceOfObject() {
            initial.setRegisters(0, "great maker", STRING)
            expected.setRegisters(0, "great maker", STRING, 1, true, "Z")
            CoreTester.test(CLASS_NAME, "instanceOfObject()V", initial, expected)
        }

        @Test
        fun unknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType() {
            initial.setRegisters(0, CoreState.UNKNOWN, OBJECT)
            expected.setRegisters(0, CoreState.UNKNOWN, OBJECT, 1, CoreState.UNKNOWN, "Z")
            CoreTester.test(CLASS_NAME, "instanceOfString()V", initial, expected)
        }
    }

    /**
     * 🔴 **THE SOUNDNESS FIX, and the direction matters: this test asserts `Unknown`, not `Known(true)`.**
     *
     * `docs/architecture/opcodes.md:173` files the opposite rule as the reference implementation's
     * **Correctness Defect 18**: *"`instance-of` can return definitely true for an unknown declared
     * subtype even though the value may be null."* The oracle's type-set loop
     * (`opcode/InstanceOfOp.kt:34-40`) runs BEFORE its `isUnknown` test (`:41`), so
     * `Unknown("Ljava/lang/String;")` against `Ljava/lang/String;` folds `Known(true, Z)` there.
     *
     * Why that is wrong rather than merely bolder: `core/Value.kt` defines [Value.Unknown] as *"value not
     * known; type approximately known. **May** be null"*, and `Frame.entryFrame` seeds **every reference
     * parameter** that way -- so for `void f(String s) { s instanceof String }` the folded `true` is a
     * constant that every null argument contradicts. INHERITED, [JVMS] `instanceof`: *"If objectref is
     * null, the instanceof instruction pushes an int result of 0 as an int onto the operand stack."*
     * One row folds a wrong **value** and deletes a feasible **path**, because the `Known(true, Z)` is
     * what the next `if-nez` reads.
     *
     * **The mirror direction is live too and is why this is one test and not a blanket rule:** answering
     * `Unknown` where the type genuinely decides throws away a fold. It does not decide here -- the
     * precision loss is exactly the null case -- and `Known` sources still fold, which the migrated
     * tests above and [aKnownReferenceStillFolds] pin.
     *
     * Migration cost of the fix: **zero**, measured. The only `Unknown`-valued migrated test
     * (`unknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType`) declares `Ljava/lang/Object;` against
     * target `Ljava/lang/String;`, where the oracle's type test FAILS, so the oracle already answers
     * `Unknown(Z)` and no migrated expectation changes.
     */
    @Test
    fun anUnknownWhoseDeclaredTypeSatisfiesTheTargetIsStillUnknownBecauseItMayBeNull() {
        val initial = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) }
        val expected = CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, STRING)
            setRegister(1, Value.Unknown(BOOL))
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", initial, expected)

        // ...and the same for the target every obfuscated method tests against. `Ljava/lang/Object;` is
        // the row where the oracle needs no ancestor walk at all to reach its wrong answer
        // (`type/VirtualClass.java:146-148`'s short-circuit), so it is the shape that would fold
        // `Known(true)` most confidently.
        val againstObject = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) }
        val expectedAgainstObject = CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, STRING)
            setRegister(1, Value.Unknown(BOOL))
        }
        CoreTester.test(CLASS_NAME, "instanceOfObject()V", againstObject, expectedAgainstObject)
    }

    /**
     * The control for the test above: a `Known` reference whose type satisfies the target DOES fold, so
     * the refusal above is about nullability and not about the engine having stopped deciding.
     *
     * Without this, "answer `Unknown` for an `Unknown`" and "answer `Unknown` for everything" are the
     * same passing test, and the second is the mirror soundness defect (a deobfuscator loses the fold).
     * `Value.Top` is here too because it is the other lattice point that decides nothing, and because
     * `Top` carries **no type at all** -- an implementation reading `value.type` unconditionally would
     * NPE on it rather than answering.
     */
    @Test
    fun aKnownReferenceStillFolds() {
        val known = CoreState().apply { setRegisters(0, "great maker", STRING) }
        val expectedKnown = CoreState().apply {
            setRegisters(0, "great maker", STRING)
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", known, expectedKnown)

        val top = CoreState().apply { setRegister(0, Value.Top) }
        val expectedTop = CoreState().apply {
            setRegister(0, Value.Top)
            setRegister(1, Value.Unknown(BOOL))
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", top, expectedTop)
    }

    /**
     * 🔴 **The null row is keyed on `Value.readsAsNullReference`, NOT on `raw == null`** -- and this is
     * the test that tells the two apart.
     *
     * `docs/DALVIK-SEMANTICS.md#instance-of-22c--opcodeinstanceofopkt` files the oracle's version as **D9**
     * -- a `DALVIK-SEMANTICS.md` reconciliation id, NOT an `opcodes.md` one; that document's only
     * `instance-of` defect is 18, the nullable-`Unknown` case this file's other test cites. Its null row is
     * `instance.raw == null` (`opcode/InstanceOfOp.kt:30`), so `Known(5, "I")` skips it, the type set is
     * `{"I", "Ljava/lang/Integer;"}`, `Ljava/lang/Integer;` IS an `Object`, and the oracle folds
     * **`Known(true, Z)`** -- a confidently wrong constant.
     *
     * **[ART]'s SWITCH interpreter** answers `0`: `INSTANCE_OF` fetches the operand with
     * `GetVRegReference(B())`
     * (`interpreter_switch_impl-inl.h:847`), and `ShadowFrame`'s reference half is cleared by **every**
     * primitive write (`shadow_frame.h:401-408`, and the `References()[i].Clear()` in each of the four
     * `SetVReg*`), so a register whose last write was primitive reads as `nullptr` and
     * `SetVReg(A(), 0)`.
     *
     * The primitive-write and null-raw cases answer zero. A boxed `Integer(0)` under a reference descriptor
     * is a live object and answers one. The reference-type guard keeps its numeric payload out of Dalvik bit
     * reasoning.
     */
    @Test
    fun nullLikeValuesAnswerZeroAndAReferenceTaggedBoxedZeroAnswersOne() {
        // A primitive-typed Known -- the D9 shape, and the only one of the three the oracle gets wrong.
        val primitiveTyped = CoreState().apply { setRegisters(0, 5, "I") }
        val expectedPrimitiveTyped = CoreState().apply {
            setRegisters(0, 5, "I")
            setRegisters(1, false, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfObject()V", primitiveTyped, expectedPrimitiveTyped)

        // A definite null reference. INHERITED ([JVMS] `instanceof`), and the oracle agrees here.
        val definiteNull = CoreState().apply { setRegisters(0, null, STRING) }
        val expectedDefiniteNull = CoreState().apply {
            setRegisters(0, null, STRING)
            setRegisters(1, false, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", definiteNull, expectedDefiniteNull)

        // A boxed Integer(0) under a reference tag is a live object, not primitive register bits.
        val boxedZero = CoreState().apply { setRegisters(0, 0, STRING) }
        val expectedBoxedZero = CoreState().apply {
            setRegisters(0, 0, STRING)
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", boxedZero, expectedBoxedZero)
    }

    /**
     * **A PRIMITIVE type operand answers `0` unconditionally, and the row must beat the unknown-source
     * row.** SPEC, [BC]'s note on the `instance-of` row, verbatim: *"Since B must always be a reference
     * (and not a primitive value), this will always result in `0` being stored if C refers to a primitive
     * type."*
     *
     * Driven with an UNKNOWN source on purpose: that is the one seed that distinguishes the row order.
     * With the primitive-target row placed after the unknown-source row -- which is where this plan's
     * first revision had it -- `instance-of vA, vUnknown, I` answers `Unknown(Z)`, contradicting a SPEC
     * sentence with an engine choice. A `Known` source is driven too, so the row is not pinned only by
     * its interaction with another one.
     *
     * **This DIVERGES from the oracle, which answers `Unknown` for the unknown-source case**: its type
     * loop asks `VirtualPrimitive.instanceOf` (`type/VirtualPrimitive.java:35-40`, `equals`-only), which
     * answers false for a `Ljava/lang/Object;` candidate against `I`, so it falls through to its
     * `isUnknown` branch. Not registered as a differential divergence because a primitive type operand is
     * **unverifiable input** ([ART]'s verifier rejects the class outright), so no fixture in the
     * differential corpus can hold one without also making the fixture something no device would run --
     * the divergence is pinned here, at the op, where it belongs.
     */
    @Test
    fun aPrimitiveTypeOperandAlwaysStoresZeroEvenForAnUnknownSource() {
        val unknownSource = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, OBJECT) }
        val expectedUnknownSource = CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, OBJECT)
            setRegisters(1, false, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfPrimitiveInt()V", unknownSource, expectedUnknownSource)

        // A Known INT source against target `I`: reflexively "the same type", and still 0. The oracle's
        // `VirtualPrimitive.instanceOf` is `equals`, so it would answer 1 here -- pinned at the predicate
        // level by `core/ClassIndexTest.aPrimitiveReflexivePairIsWhereTheTwoPredicatesDisagreeMostVisibly`.
        val knownInt = CoreState().apply { setRegisters(0, 5, "I") }
        val expectedKnownInt = CoreState().apply {
            setRegisters(0, 5, "I")
            setRegisters(1, false, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfPrimitiveInt()V", knownInt, expectedKnownInt)
    }

    /**
     * **Array covariance, three cases, and each needs a DIFFERENT fixture method than an earlier revision
     * of this plan named.** Verified against the fixture rather than inferred: `instanceOfIntArray()V`
     * puts `[I` in the TARGET position and serves none of these three questions.
     *
     *  - `[Ljava/lang/String;` against `[Ljava/lang/Object;` -> **1**. Covariance: `String <: Object`
     *    implies `String[] <: Object[]` (JLS §4.10.3, quoted at
     *    `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`).
     *  - `[I` against `[Ljava/lang/Object;` -> **0**. A primitive-component array is INVARIANT: its only
     *    array-typed supertype chain goes through `Ljava/lang/Object;` directly, never through
     *    `[Ljava/lang/Object;`. This is the case whose two answers are one row apart in the same rule, so
     *    a "reference arrays and primitive arrays behave alike" implementation passes the first bullet and
     *    fails this one.
     *  - `[I` against `Ljava/lang/Object;` -> **1**. Every array is an `Object`.
     *
     * The register TAG lies in all three (the corpus tags array registers loosely on purpose), so these
     * also pin that the answer comes from the array OBJECT's type and not from the declared tag.
     */
    @Test
    fun arrayCovarianceHoldsForReferenceComponentsAndNotForPrimitiveOnes() {
        val stringArray = CoreState().apply { setRegisters(0, arrayOfNulls<String>(0), OBJECT) }
        val expectedStringArray = CoreState().apply {
            setRegisters(0, arrayOfNulls<String>(0), OBJECT)
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfObjectArray()V", stringArray, expectedStringArray)

        val intArrayAgainstObjectArray = CoreState().apply { setRegisters(0, IntArray(0), OBJECT) }
        val expectedIntArrayAgainstObjectArray = CoreState().apply {
            setRegisters(0, IntArray(0), OBJECT)
            setRegisters(1, false, "Z")
        }
        CoreTester.test(
            CLASS_NAME, "instanceOfObjectArray()V",
            intArrayAgainstObjectArray, expectedIntArrayAgainstObjectArray,
        )

        val intArrayAgainstObject = CoreState().apply { setRegisters(0, IntArray(0), OBJECT) }
        val expectedIntArrayAgainstObject = CoreState().apply {
            setRegisters(0, IntArray(0), OBJECT)
            setRegisters(1, true, "Z")
        }
        CoreTester.test(
            CLASS_NAME, "instanceOfObject()V",
            intArrayAgainstObject, expectedIntArrayAgainstObject,
        )
    }

    /**
     * 🔴 **Every array is `Cloneable` and `Serializable`, DELIBERATELY UNLIKE THE ORACLE, and this must
     * never be "reconciled".** Plan 8 Task 7's registered divergence, reaching the op level for the first
     * time.
     *
     * INHERITED: JLS §4.10.3 lists exactly those two interfaces as the ones every array implements, and
     * lists the row for primitive components explicitly. Android's own toolchain agrees -- AOSP
     * `Merger.isPossiblyAssignableFrom`: *"all arrays are assignable to `Serializable` and `Cloneable`"*.
     * The oracle's `VirtualArray.instanceOf` (`type/VirtualArray.java:129-141`) is `equals` plus
     * `getAncestors()` membership and `getImmediateAncestors()` (`:52-74`) never adds either interface on
     * either branch, so it folds `Known(false, Z)` here -- measured against the oracle's own class model
     * by `core/ClassIndexTest.everyArrayIsAnInstanceOfCloneableAndSerializableUnlikeTheOracle`, and
     * measured end-to-end by the `types-int-array-is-cloneable` registry entry.
     *
     * `type/VirtualArrayTest` **encodes** the oracle's omission (`docs/architecture/type-and-dex.md:234`),
     * so prior art for the wrong answer looks like confirmation. It is not.
     */
    @Test
    fun everyArrayIsSerializableUnlikeTheOracle() {
        val primitiveComponent = CoreState().apply { setRegisters(0, IntArray(0), OBJECT) }
        val expectedPrimitiveComponent = CoreState().apply {
            setRegisters(0, IntArray(0), OBJECT)
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfSerializable()V", primitiveComponent, expectedPrimitiveComponent)

        // A reference component reaches the same answer by the same row, and a two-dimensional one proves
        // the rule is about being an array rather than about the component's own interfaces. Tagged
        // TRUTHFULLY here, unlike the migrated `stringArray2DIsInstanceOf2DObjectArray` whose corpus tag is
        // incomparable to its seed -- this test is new, so there is no byte-identical expectation to keep.
        val referenceComponent = CoreState().apply {
            setRegisters(0, arrayOfNulls<Array<String>>(0), "[[Ljava/lang/String;")
        }
        val expectedReferenceComponent = CoreState().apply {
            setRegisters(0, arrayOfNulls<Array<String>>(0), "[[Ljava/lang/String;")
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfSerializable()V", referenceComponent, expectedReferenceComponent)

        // And the row is NOT "any reference is Serializable", which is what makes the two assertions above
        // about ARRAYS rather than about a predicate that answers `true` for everything. A plain
        // `java.lang.Object` is the control and it has to be: `java.lang.String` IS `Serializable`
        // (measured -- it was this test's first control and it answered `true`), as are the boxed
        // primitives and `StringBuilder`, so most obvious candidates are not controls at all. Only register
        // 1 is asserted because the seed's identity is not the subject here.
        val notAnArray = CoreState().apply { setRegisters(0, Any(), OBJECT) }
        val expectedNotAnArray = CoreState().apply { setRegisters(1, false, "Z") }
        CoreTester.test(CLASS_NAME, "instanceOfSerializable()V", notAnArray, expectedNotAnArray)
    }

    /**
     * **An unanswerable subtype question answers `Unknown(Z)`, never `false`.**
     *
     * `ClassIndex.isInstanceOf` is deliberately **not total**: its last row walks the candidate's
     * ancestors and raises [org.cf.smalivm.core.ClassLoadFailure] when the class model cannot complete the
     * walk -- the normal case in an obfuscated APK. Absorbing that into `false` would fold a confidently
     * wrong constant, which is the same defect class as D9 arriving through a different door. Grounded on
     * spec §2.2 **alone**: nothing in [BC] speaks to an unanswerable subtype question. That is a SEPARATE
     * rule from the primitive-target row above, whose `false` **is** [BC]-grounded and definite -- the two
     * are stated separately because merging them is what let one wrong argument cover both in Task 7's
     * first hand-off.
     *
     * The seed's declared tag names a class this input does not ship, and its raw's class (`String`) does
     * not satisfy the `[Ljava/lang/String;` target either -- so the type set is `{unanswerable, false}` and
     * the whole answer turns on how the unanswerable half is absorbed. `instanceOfStringArray()V` is the
     * one corpus method no migrated test references; it is kept (per the brief) and this is its use.
     */
    @Test
    fun anUnanswerableSubtypeQuestionIsAmbiguityRatherThanFalse() {
        val absentDeclaredType = CoreState().apply { setRegisters(0, "great maker", "Lobf/absent;") }
        val expected = CoreState().apply {
            setRegisters(0, "great maker", "Lobf/absent;")
            setRegister(1, Value.Unknown(BOOL))
        }
        CoreTester.test(CLASS_NAME, "instanceOfStringArray()V", absentDeclaredType, expected)
    }

    /**
     * **A SATISFYING type anywhere in the set wins over an unanswerable one**, which is why the walk
     * collects rather than stopping at the first raise.
     *
     * Same seed shape as the test above -- declared tag absent from this input, raw a `String` -- against
     * a target the raw's class DOES satisfy. The declared tag is tried first (it is the oracle's order and
     * this core keeps it), so an implementation that let the first [org.cf.smalivm.core.ClassLoadFailure]
     * escape or short-circuit would answer `Unknown(Z)` here -- or abort the path outright -- and lose a
     * fold that is genuinely provable: the value IS a `String`. The mirror of the test above, in the
     * over-refusal direction, which this plan grades equally (spec §2.2).
     *
     * **The target is `Ljava/lang/String;` and NOT `Ljava/lang/Object;`, which was this test's first
     * version and proved nothing.** Measured: against `Ljava/lang/Object;` the walk never raises at all,
     * because `ClassIndex.isInstanceOf`'s row 3 short-circuits an `Object` target to `candidate.isReference`
     * with no class load -- so the mutation that lets a [org.cf.smalivm.core.ClassLoadFailure] escape left
     * the test green. Found by running that mutation rather than by reading, which is the only way this
     * class of vacuity shows up.
     */
    @Test
    fun aSatisfyingValueTypeSurvivesAnUnanswerableDeclaredTag() {
        val absentDeclaredType = CoreState().apply { setRegisters(0, "great maker", "Lobf/absent;") }
        val expected = CoreState().apply {
            setRegisters(0, "great maker", "Lobf/absent;")
            setRegisters(1, true, "Z")
        }
        CoreTester.test(CLASS_NAME, "instanceOfString()V", absentDeclaredType, expected)
    }

    /**
     * **`instance-of` never branches: ONE successor, always.** `Format22c`, `canContinue=true`. The only
     * throw a device produces here is a *resolution* failure, and since Plan 9 Task 15 this core produces it
     * too -- as a **definite** throw with zero successors, which is still not a branch (see
     * [anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource], and
     * `OpBuilderTest.anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal` for the decode
     * half). This test's fixture names a PRESENT type, so no resolution row is live in it.
     *
     * Asserted through the three channels that can actually see fork arity -- the node pile at the
     * following address, the op's own child count, and `graph.outcomes` -- because a migrated expectation
     * cannot: `terminatingConsensus` is a join, idempotent when the arms agree, and every arm here would
     * agree. Measured five consecutive times across Plans 6-8.
     *
     * The `Unknown` source is the seed that matters: it is the one row where a "when in doubt, fork"
     * implementation would emit two transitions, and an invented infeasible path is graded equally with a
     * missed feasible one (spec §2.2).
     */
    @Test
    fun aTypeTestNeverForks() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, STRING)
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "nothing aborted, so the arity below is real")
        assertEquals(1, graph.nodePile(ADDRESS_INSTANCE_OF).size, "one node at the instance-of itself")
        assertEquals(
            1, graph.nodePile(ADDRESS_INSTANCE_OF).single().children.size,
            "and it declares exactly ONE successor -- a type test decides a value, it does not choose a path",
        )
        assertEquals(1, graph.nodePile(ADDRESS_RETURN).size, "so the return is reached once, not twice")
        assertEquals(1, graph.outcomes.size, "and the run has exactly one outcome")
    }

    /**
     * **Side effects: NONE.** A type test writes one register and touches nothing this engine does not
     * own -- and `instance-of` does not even initialise the class it names, on the strength of [ART]'s WHOLE
     * `INSTANCE_OF` handler body (`interpreter_switch_impl-inl.h:837-850`: resolve, `nullptr` bail, one
     * `GetVRegReference(B())`, one `SetVReg`, `return true` -- no allocation, no field access, no other call).
     * So the resolution genuinely is effect-free and there is no `<clinit>` for the engine to be missing.
     *
     * ⚠️ **This used to rest on `can_run_clinit = false` at `:842`, and that is a non-sequitur.** The flag
     * bounds one call site, not an opcode: `NEW_INSTANCE` (`:862-894`) passes the same `false` at the same
     * argument position and DOES initialise, downstream in `AllocObjectFromCode`
     * (`entrypoints/entrypoint_utils-inl.h:236-252`). See `docs/DALVIK-SEMANTICS.md#instance-of-22c`.
     *
     * A spurious `STRONG` here would silently stop **every** constant fold in any method containing a type
     * test, because `methodSideEffectLevel` maxes over addresses -- Plan 7 Task 10 measured that exact
     * defect and its documented `NONE` row killed 0 of 858 tests, which is why the absence is *declared*
     * and pinned rather than left implicit.
     *
     * Every row is driven, not just one, because `recordSideEffect` is a per-op call and a single row
     * could carry it: the folding row, the refusing row, the null row, and the array-interface row.
     */
    @Test
    fun aTypeTestDeclaresNoSideEffectOnEveryRow() {
        val seeds = listOf(
            "a folded true" to CoreState().apply { setRegisters(0, "great maker", STRING) },
            "a refused unknown" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) },
            "a null source" to CoreState().apply { setRegisters(0, null, STRING) },
            "a primitive-typed source" to CoreState().apply { setRegisters(0, 5, "I") },
        )
        for ((label, seed) in seeds) {
            val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", seed)
            assertEquals(
                SideEffectLevel.NONE, graph.sideEffectLevel(ADDRESS_INSTANCE_OF),
                "$label: a type test mutates nothing outside its own frame",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$label: nothing threw and nothing aborted")
            assertEquals(
                SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
                "$label: and the whole-method query a strategy actually reads agrees",
            )
        }

        // The array-interface row separately: it is the one row that answers from a descriptor STRING
        // comparison with no class load at all, so it is the row where an implementer might reach for a
        // side-effect marker to record "I did not consult the class model".
        val arrayGraph = CoreTester.execute(CLASS_NAME, "instanceOfSerializable()V", CoreState().apply {
            setRegisters(0, IntArray(0), OBJECT)
        })
        assertEquals(SideEffectLevel.NONE, arrayGraph.sideEffectLevel(ADDRESS_INSTANCE_OF))
    }

    /**
     * The read/assignment metadata, which on this engine is the **sole** guarantor of a correct
     * free-register answer: `ExecutionGraph.availableRegisters` is purely static, so these two sets are
     * what `docs/architecture/graph-optimizer-contract.md:136`'s **Dead removal** row -- the consumer that
     * *"reads read/assignment metadata"* -- acts on.
     *
     * Non-vacuous by construction, per `core/op/FilledNewArrayOpTest`'s recorded trap: a bare
     * `assertEquals(emptySet(), registersAssigned(0))` passes HARDEST when nothing works, because the base
     * `Op` answers `emptySet()`. So this gates on `Complete`, asserts `wasReached` positively, and asserts
     * a NON-empty set at the instance-of address against an empty one at the `return-void`.
     */
    @Test
    fun aTypeTestReadsItsSourceAndAssignsItsDestination() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, "great maker", STRING)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_INSTANCE_OF), "the op ran, so the sets below describe a live op")
        assertEquals(setOf(1), graph.registersAssigned(ADDRESS_INSTANCE_OF), "vA is the destination")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(ADDRESS_RETURN),
            "the CONTROL: an op that assigns nothing answers the empty set, which is also what an " +
                "unbuilt op would answer at the address above",
        )
        val op = graph.nodePile(ADDRESS_INSTANCE_OF).single().op
        assertEquals(setOf(0), op.registersRead, "vB is the source, and it is a real read rather than a hint")
        assertEquals(setOf(1), op.registersAssigned)
    }

    /**
     * 🔴 **AN ABSENT TARGET TYPE IS A DEFINITE GUEST `NoClassDefFoundError`, AND IT FIRES ON A NULL SOURCE**
     * (Plan 9 Task 15, the ROW-23 requirement; Plan 8 answered `Aborted(UNSUPPORTED_OP)` here and this test
     * did not exist).
     *
     * Two claims, and the null seed is what makes the second one testable:
     *
     * 1. **The TYPE.** A `LinkageError`/`Error` -- so a guest `catch (Exception)` cannot catch it, which is
     *    the handler-reachability consequence the whole requirement is about (tested end-to-end in
     *    `core/op/ConstOpTest.aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo`).
     *    `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     *    `[DEVICE](Tier B, nterp default, API 36)` --
     *    `tools/art-runner/fixtures/Fx_InstanceOfAbsentType.smali` against `Fx_InstanceOfPresentType.smali`;
     *    the family is tiered once at
     *    [`DALVIK-SEMANTICS.md`'s "Resolution failure is one family"](../../../../../../../../../docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once).
     * 2. **The ORDERING: resolution precedes the null shortcut.** [CastOutcome.ReadsAsNull] folds a definite
     *    `0` for a null source, so an op that read its register first would answer `Known(false, Z)` and
     *    complete -- exactly what [JVMS] `instanceof` prescribes (*"If objectref is null, the instanceof
     *    instruction pushes an int result of 0"*) and exactly what a device does NOT do. That is the one
     *    inversion an implementation written from [JVMS] gets wrong, and this assertion is what catches the
     *    two lines of `InstanceOfOp.execute` being swapped.
     *
     * **The CONTROL is in this test rather than by cross-reference:** the same null source against a PRESENT
     * target type must fold `0` and complete. Without it, "a null source throws" also passes against a build
     * where every `instance-of` on a null throws.
     */
    @Test
    fun anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource() {
        val nullSource = CoreState().apply { setRegisters(0, null, OBJECT) }

        // THE CONTROL: a definite null against a PRESENT type folds a definite 0 and completes.
        CoreTester.test(
            CLASS_NAME, "instanceOfString()V", nullSource,
            CoreState().apply {
                setRegisters(0, null, OBJECT)
                setRegisters(1, false, "Z")
            },
        )

        CoreTester.testThrows(
            CLASS_NAME, "instanceOfAbsentClass()V", nullSource,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfAbsentClass()V", nullSource)
        assertEquals(CompletionStatus.Complete, graph.completion(), "so the negatives below are dead-code claims")
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type",
        )
        assertEquals(
            0, graph.nodePile(ADDRESS_INSTANCE_OF).single().children.size,
            "definite, not a fork -- and a definite throw is still not a BRANCH, which is this op's invariant",
        )
        assertTrue(!graph.wasReached(ADDRESS_RETURN), "the fall-through the JVMS ordering would take is dead")
        // The destination register is never written: the observable form of "before any register read/write".
        // Deliberately read off the throwing node's own frame rather than through a terminating projection --
        // there is no returning path to project. `peek` is the UNASSIGNED-distinguishing read (it answers
        // `null` where `read` answers `Value.Top`), so a null here is stronger than `Top` would be: it says
        // the slot was never held at all, not merely that it holds nothing known.
        assertEquals(
            null, graph.nodePile(ADDRESS_INSTANCE_OF).first().frame.peek(1),
            "no boolean was folded, so nothing downstream can act on an invented 0",
        )
    }

    /**
     * **`toString` prefixes BOTH registers, deliberately unlike the oracle.**
     * `docs/architecture/opcodes.md:174` records **Correctness Defect 19**: *"`InstanceOfOp.toString()`
     * omits the `r` prefix on its source register"* -- verified at `opcode/InstanceOfOp.kt:50`,
     * `"$name r$destRegister, $arg1Register, $referenceType"`. None of the seven migrated tests asserts
     * `toString`, so the defect has no corpus protection at all and the new one is simply correct.
     */
    @Test
    fun toStringPrefixesBothRegisters() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, "great maker", STRING)
        })
        assertEquals(
            "instance-of r1, r0, Ljava/lang/String;",
            graph.nodePile(ADDRESS_INSTANCE_OF).single().op.toString(),
        )
    }
}
