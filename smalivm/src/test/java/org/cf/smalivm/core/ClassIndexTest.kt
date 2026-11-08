package org.cf.smalivm.core

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * Bug class: the core algebra reaching around [ClassIndex] into a second class model, and handler
 * resolution having no real subtype oracle. Oracle: the real Android/JDK class hierarchy --
 * NullPointerException IS-A RuntimeException IS-A Exception.
 */
class ClassIndexTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))

    private fun t(descriptor: String) = VirtualType.of(descriptor)

    @Test
    fun subtypeIsReflexive() {
        val npe = t("Ljava/lang/NullPointerException;")
        assertTrue(classIndex.isSubtypeOf(npe, npe))
    }

    @Test
    fun subtypeFollowsRealHierarchy() {
        val npe = t("Ljava/lang/NullPointerException;")
        assertTrue(classIndex.isSubtypeOf(npe, t("Ljava/lang/RuntimeException;")))
        assertTrue(classIndex.isSubtypeOf(npe, t("Ljava/lang/Exception;")))
        assertTrue(classIndex.isSubtypeOf(npe, t("Ljava/lang/Object;")))
    }

    @Test
    fun unrelatedTypesAreNotSubtypes() {
        assertFalse(classIndex.isSubtypeOf(t("Ljava/lang/RuntimeException;"), t("Ljava/lang/NullPointerException;")))
        assertFalse(classIndex.isSubtypeOf(t("Ljava/io/IOException;"), t("Ljava/lang/RuntimeException;")))
    }

    @Test
    fun primitivesAreOnlySubtypesOfThemselves() {
        // Soundness: no primitive/reference bridging (spec 4.1 -- those types join to Top).
        assertTrue(classIndex.isSubtypeOf(t("I"), t("I")))
        assertFalse(classIndex.isSubtypeOf(t("I"), t("Ljava/lang/Object;")))
        assertFalse(classIndex.isSubtypeOf(t("I"), t("J")))
    }

    @Test
    fun anUnloadableClassReportsATypedLoadFailure() {
        // Bug class: signalling "this class is not in the program" with a bare RuntimeException, which is
        // indistinguishable from an engine bug, so a caller that answers the first conservatively must
        // swallow the second too. `Lspine/absent;` is well-formed with no smali behind it, the normal case
        // in an obfuscated APK.
        val absent = t("Lspine/absent;")
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.isSubtypeOf(absent, t("Ljava/lang/RuntimeException;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
        // Either operand, in either position.
        assertThrows(ClassLoadFailure::class.java) {
            classIndex.isSubtypeOf(t("Ljava/lang/RuntimeException;"), absent)
        }
        // And the pure lattice answers still short-circuit BEFORE any load is attempted, so an
        // unloadable class is not made to look loadable by reflexivity or by primitive comparison.
        assertTrue(classIndex.isSubtypeOf(absent, absent), "reflexive: no class model reach at all")
        assertFalse(classIndex.isSubtypeOf(t("I"), absent), "no primitive/reference bridging")
    }

    @Test
    fun aPrimitiveComponentArrayIsNotASubtypeOfAnObjectArray() {
        // M6 (Plan 6 whole-branch review): the direction CoreTester.kt's assignability comment names but
        // this file never pinned. Primitive arrays are invariant, so int[] is not an Object[] -- but NOT
        // because isSubtypeOf's own isReference guard rejects the pair (VirtualType.isReference counts
        // arrays, including [I, as references, so that guard never fires here). The real enforcement
        // falls out of VirtualArray.getImmediateAncestors one level down: a primitive-component array's
        // only immediate ancestor is plain Object, never an array type, so [I's whole ancestor set never
        // contains [Ljava/lang/Object;. The other direction ([Ljava/lang/String; <: [Ljava/lang/Object;,
        // legal covariance) is pinned in CoreTesterTest.aDeclaredArrayTypeAcceptsALegallyCovariantRuntimeArray.
        assertFalse(classIndex.isSubtypeOf(t("[I"), t("[Ljava/lang/Object;")))
    }

    @Test
    fun anAbsentDescriptorIsNotLoadableWhileAPresentOneAndEveryPrimitiveIs() {
        // The premise NewArrayOp's NoClassDefFoundError rests on. Asserted rather than assumed: if the
        // class model answered a bare, non-throwing "absent" instead, `isLoadable` would silently answer
        // true and `new-array [Ldoes/not/exist;` would allocate instead of throwing.
        assertFalse(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[Ldoes/not/exist;")))
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[Lnew_array_test;")))
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[[I")), "a primitive component is always loadable")
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("I")), "and so is a bare primitive")
    }

    // ---- isInstanceOf (Plan 8 Task 7) ----------------------------------------------------------
    //
    // Bug class: porting `instance-of`/`check-cast` onto `isSubtypeOf` because the two questions rhyme.
    // They are two predicates with different contracts (plan design decision 4) and the tests below pin
    // TWO independent behavioural differences, so the pair cannot later be collapsed by someone who
    // reads them as duplicates.

    @Test
    fun anyReferenceIsAnInstanceOfObjectWithoutLoadingItsAncestors() {
        // The case `isSubtypeOf` cannot serve, and the reason `ClassIndex` grows a second predicate at
        // all. `Lspine/absent;` is a well-formed descriptor with no smali behind it -- the normal case in
        // an obfuscated APK -- so the ancestor walk `isSubtypeOf` needs cannot run. `instance-of v1, v0,
        // Ljava/lang/Object;` still has an answer on a device: 1 for any non-null reference, whatever its
        // class is, because Object is the supertype of every reference type
        // (`DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`, [JVMS] `checkcast`: "If T is a class
        // type, then T must be Object"). Turning that cheap `true` into a load failure would make the
        // commonest obfuscated shape unanswerable.
        assertTrue(classIndex.isInstanceOf(t("Lspine/absent;"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/lang/Object;")))
    }

    @Test
    fun aPrimitiveTargetIsNeverAnInstanceAndNeitherIsAPrimitiveCandidate() {
        // SPEC, [BC]'s `instance-of` row: "this will always result in 0 ... if C refers to a primitive
        // type". Both operand positions are covered deliberately, because which of the oracle's THREE
        // `instanceOf` implementations runs is decided by the CANDIDATE's class (design decision 4), and
        // `type/VirtualPrimitive.instanceOf` (`:35-40`) is `equals`-only -- so the oracle answers TRUE for
        // the reflexive primitive pair. The core answers false: see
        // `aPrimitiveReflexivePairIsWhereTheTwoPredicatesDisagreeMostVisibly`.
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/Integer;"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/Object;"), t("V")))
        // primitive CANDIDATE, reference target: a register whose last write was primitive reads as a
        // null reference, and a null is an instance of nothing (`core/Value.kt`'s readsAsNullReference).
        assertFalse(classIndex.isInstanceOf(t("I"), t("Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("I"), t("Ljava/lang/Integer;")))
    }

    @Test
    fun everyArrayIsAnInstanceOfCloneableAndSerializable() {
        // INHERITED (JLS §4.10.3 via [JVMS] `checkcast`, quoted at
        // `DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`: arrays implement "exactly Cloneable
        // and java.io.Serializable", and that row is listed for PRIMITIVE components explicitly).
        // Corroborated by Android's own toolchain -- AOSP `Merger.isPossiblyAssignableFrom`
        // (dalvik2/dx/src/com/android/dx/cf/code/Merger.java), fetched raw: "does not assume anything
        // about class hierarchy other than that the type Object is the supertype of all reference types
        // and all arrays are assignable to Serializable and Cloneable", and, in the array branch, "Other
        // than Object (handled above), array types are assignable only to Serializable and Cloneable".
        val cloneable = t("Ljava/lang/Cloneable;")
        val serializable = t("Ljava/io/Serializable;")
        for (array in listOf("[I", "[[I", "[Ljava/lang/String;", "[[Ljava/lang/String;")) {
            assertTrue(classIndex.isInstanceOf(t(array), cloneable), "$array is Cloneable")
            assertTrue(classIndex.isInstanceOf(t(array), serializable), "$array is Serializable")
        }
        // A non-array is NOT given the free pass: String really does implement Serializable (so `true`
        // here comes from the hierarchy) while StringBuilder does not implement Cloneable, and if the
        // rows were keyed on the target alone both would answer true.
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), serializable))
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/StringBuilder;"), cloneable))
    }

    @Test
    fun arrayCovarianceAndInvarianceAreBothPreservedFromTheOracle() {
        // The half the oracle gets RIGHT, pinned beside the half it does not, so a future edit to the
        // divergence rows cannot silently take covariance with it. JLS §4.10.3: `S[] >1 T[]` iff
        // `S >1 T` (references), and `Object >1 P[]` for a primitive P -- but `int[]` is NOT an
        // `Object[]`, because that rule needs BOTH components to be reference types.
        assertTrue(classIndex.isInstanceOf(t("[I"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[Ljava/lang/String;"), t("[Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[[Ljava/lang/String;"), t("[[Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("[Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("[J")))
        // and a class is never an instance of an array type (`VirtualClass.java:150-153`)
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("[Ljava/lang/String;")))
    }

    @Test
    fun theRow24SubtypeMatrixAgreesWithTheDeviceAtEveryCell() {
        // ROW 24 -- "subtype testing, in its entirety", the broadest row on the INHERITED worklist because
        // every type op runs this predicate. The whole 4x4 grid, MEASURED on real ART and reproduced here:
        // candidate in {String[], Object[], int[], long[]} x target in {Object, Cloneable, Serializable,
        // Object[]}. This is the core half of the device measurement in `tools/art-runner`
        // (`Fx_Subtype_*` / `Fx_IntArrayIsCloneable`, Tier A, -Xint, art_standalone 0_git20251009-r2); the
        // device answered `instance-of` and `check-cast` IDENTICALLY at every cell, so this one predicate
        // -- `castOutcome` delegates `check-cast` to it -- governs both ops and pinning it here pins both.
        //
        // The grid deliberately includes the cells the [JLS] rule makes SURPRISING, because the obvious ones
        // cannot falsify anything: `int[]`/`long[]` instanceof `Object[]` -> false (a primitive-component
        // array is invariant and is NOT a reference array -- JLS 4.10.3 needs BOTH components to be
        // references), `long[]` instanceof `Cloneable`/`Serializable` -> true (arrays implement both, the
        // primitive-component row listed explicitly), `String[]` instanceof `Object[]` -> true (covariance).
        //
        // The core agrees with the device on all 16 -- and thus all 32 io+cc cells -- measured, none
        // diverges. That agreement is NOT the oracle's: `VirtualArray.instanceOf` omits both interfaces, so
        // the ORACLE folds `false` for every Cloneable/Serializable array cell (the registered
        // `types-int-array-is-cloneable` divergence, and the reason the device sweep DIVERGES oracle-vs-device
        // on those sixteen cells). `everyArrayIsAnInstanceOfCloneableAndSerializableUnlikeTheOracle` above
        // measures the oracle's omission directly; this test is the grid the device pins.
        //
        // Two mutations must break this (Step 5): removing `Cloneable` from `ARRAY_SUPERINTERFACES` reds the
        // Cloneable column, and dropping row 4 of `isInstanceOf` (delegating arrays structurally) reds the
        // Cloneable AND Serializable columns. The `Object[]` column is answered structurally by row 6 either
        // way, so it is robust to both -- which is correct, and is why it is pinned by direct assertion here.
        val objectT = t("Ljava/lang/Object;")
        val cloneable = t("Ljava/lang/Cloneable;")
        val serializable = t("Ljava/io/Serializable;")
        val objectArray = t("[Ljava/lang/Object;")
        // candidate -> expected across {Object, Cloneable, Serializable, Object[]}
        data class Row(
            val candidate: String,
            val isObject: Boolean,
            val isCloneable: Boolean,
            val isSerializable: Boolean,
            val isObjectArray: Boolean,
        )
        val matrix = listOf(
            Row("[Ljava/lang/String;", true, true, true, true), // reference component: covariant to Object[]
            Row("[Ljava/lang/Object;", true, true, true, true), // reflexive at Object[]
            Row("[I", true, true, true, false), // primitive component: invariant, NOT an Object[]
            Row("[J", true, true, true, false), // primitive component: invariant, NOT an Object[]
        )
        for ((candidate, isObject, isCloneable, isSerializable, isObjectArray) in matrix) {
            val c = t(candidate)
            assertEquals(isObject, classIndex.isInstanceOf(c, objectT), "$candidate instance-of Object")
            assertEquals(isCloneable, classIndex.isInstanceOf(c, cloneable), "$candidate instance-of Cloneable")
            assertEquals(isSerializable, classIndex.isInstanceOf(c, serializable), "$candidate instance-of Serializable")
            assertEquals(isObjectArray, classIndex.isInstanceOf(c, objectArray), "$candidate instance-of Object[]")
        }
    }

    @Test
    fun instanceOfFollowsTheRealHierarchyIncludingInterfaces() {
        // The delegation half: for an ordinary class candidate the oracle's `VirtualClass.instanceOf`
        // answer is kept verbatim, interfaces included. `instance-of` DOES consult interfaces -- it is
        // only `joinTypes` that excludes them (see `TypeJoinTest`), and conflating the two would be a
        // soundness change, not a tidying.
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Ljava/lang/CharSequence;")))
        assertTrue(classIndex.isInstanceOf(t("Lchild_class;"), t("Lparent_class;")))
        assertTrue(classIndex.isInstanceOf(t("Lgrandparent_class;"), t("Lgrandparent_interface;")))
        assertTrue(classIndex.isInstanceOf(t("Lchild_class;"), t("Lgrandparent_interface;")))
        assertFalse(classIndex.isInstanceOf(t("Lparent_class;"), t("Lchild_class;")))
    }

    @Test
    fun aPrimitiveReflexivePairIsWhereTheTwoPredicatesDisagreeMostVisibly() {
        // TWO independent differences between `isInstanceOf` and `isSubtypeOf`, so the two predicates
        // cannot be collapsed into one with a mode flag (design decision 4). Both directions are
        // asserted on the SAME pair of arguments, which is what makes "these are duplicates" fail to
        // compile as an argument.
        //
        // (1) The reflexive primitive pair. `isSubtypeOf(I, I)` is true (a type is its own ancestor, and
        //     `aput`'s component check needs that). `isInstanceOf(I, I)` is false, because [BC] says a
        //     primitive `instance-of` target always answers 0.
        assertTrue(classIndex.isSubtypeOf(t("I"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("I"), t("I")))
        // (2) An unloadable candidate against Object. `isSubtypeOf` must NOT invent an answer -- doing so
        //     would silently un-catch a handler -- so it raises; `isInstanceOf` answers without loading.
        val absent = t("Lspine/absent;")
        assertThrows(ClassLoadFailure::class.java) { classIndex.isSubtypeOf(absent, t("Ljava/lang/Object;")) }
        assertTrue(classIndex.isInstanceOf(absent, t("Ljava/lang/Object;")))
    }

    @Test
    fun instanceOfReportsATypedLoadFailureWhenItGenuinelyHasToWalk() {
        // `isInstanceOf` is not total: an unanswerable question is ambiguity, never `false`, because
        // `false` would fold a wrong `instance-of` constant and invent a definite `ClassCastException`
        // (spec §2.2). The Object short-circuit keeps this off the common path.
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.isInstanceOf(t("Lspine/absent;"), t("Ljava/lang/String;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
        // An absent TARGET fails the same way, and in the same direction.
        assertThrows(ClassLoadFailure::class.java) {
            classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Lspine/absent;"))
        }
        // But an array candidate against the three Android array supertypes still answers, because those
        // rows are pure descriptor comparisons: `[Lobf/absent;` is Cloneable on a device even though its
        // component class was never shipped.
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/lang/Cloneable;")))
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/io/Serializable;")))
    }

    // ---- superclassOf (Plan 8 Task 7) ----------------------------------------------------------

    @Test
    fun theSuperclassChainIsReadFromTheClassDefAndNeverFromTheAncestorSet() {
        // `superclassOf` reads the declared superclass -- ONE string, no set, no iteration order.
        assertEquals(t("Lparent_class;"), classIndex.superclassOf(t("Lchild_class;")))
        assertEquals(t("Lgrandparent_class;"), classIndex.superclassOf(t("Lparent_class;")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("Lgrandparent_class;")))
        assertNull(classIndex.superclassOf(t("Ljava/lang/Object;")), "the chain terminates at Object")
        // `Lgrandparent_class;` implements `Lgrandparent_interface;`, so the interface IS a supertype --
        // asserted here, because the whole point of reading the declared superclass is that the interface
        // is reachable the other way and must not be on the chain.
        assertTrue(classIndex.isSubtypeOf(t("Lgrandparent_class;"), t("Lgrandparent_interface;")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("Lgrandparent_class;")))
    }

    @Test
    fun everyArrayHasObjectAsItsSuperclassAndPrimitivesHaveNone() {
        // Arrays: JLS §4.10.3's "Object >1 P[]", and the same for a reference component -- an array's
        // superCLASS is Object; its array-typed supertypes are a covariance relation, not a chain, and
        // `joinTypes` handles them component-wise instead (see `TypeJoinTest`).
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[I")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[[Ljava/lang/String;")))
        // And no load is attempted for the component, so an array of an absent class still answers.
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[Lspine/absent;")))
        assertNull(classIndex.superclassOf(t("I")))
        assertNull(classIndex.superclassOf(t("V")))
    }

    @Test
    fun anUnloadableClassReportsALoadFailureFromSuperclassOfToo() {
        // Same contract as `isSubtypeOf`: the class model failing to answer is a statement about the
        // INPUT and must be distinguishable from an engine bug. What the two CALLERS do with it differs
        // -- `resolveHandler` propagates, `joinTypes` degrades to `Object` -- which is only a choice
        // because the failure is reported rather than swallowed here.
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.superclassOf(t("Lspine/absent;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
    }

    @Test
    fun directInterfacesOfAnAbsentClassIsALoadFailureNotAnEmptyList() {
        // Bug class: answering "implements nothing" for a class the program lacks. A member walk that took the
        // empty list would report a definite miss for a member the absent class may declare; `superclassOf`
        // already refuses the same question, so both parent queries share its failure rule.
        val hierarchy = classIndex.currentSnapshot.hierarchy
        assertThrows(ClassLoadFailure::class.java) { hierarchy.directInterfaces(t("Lspine/absent;")) }
        assertEquals(listOf(t("Lgrandparent_interface;")), hierarchy.directInterfaces(t("Lgrandparent_class;")))
        assertEquals(emptyList<VirtualType>(), hierarchy.directInterfaces(t("[Lspine/absent;")))
    }

    @Test
    fun superclassMethodWinsBeforeADeclaredInterfaceMethod(@TempDir temp: Path) {
        val sources = mapOf(
            "I.smali" to """
                .class public interface abstract Lresolution/I;
                .super Ljava/lang/Object;
                .method public f()I
                    .registers 1
                    const/4 v0, 0x1
                    return v0
                .end method
            """,
            "G.smali" to """
                .class public Lresolution/G;
                .super Ljava/lang/Object;
                .method public f()I
                    .registers 1
                    const/4 v0, 0x2
                    return v0
                .end method
            """,
            "P.smali" to """
                .class public Lresolution/P;
                .super Lresolution/G;
            """,
            "C.smali" to """
                .class public Lresolution/C;
                .super Lresolution/P;
                .implements Lresolution/I;
            """,
        )
        sources.forEach { (name, source) -> Files.writeString(temp.resolve(name), source.trimIndent()) }
        val index = ClassIndex.open(temp.toFile())

        val lookup = index.lookupMethod(VirtualType.of("Lresolution/C;"), "f()I") as MethodLookup.Runnable

        assertEquals(VirtualType.of("Lresolution/G;"), lookup.method.target.definingClass)
    }

    @Test
    fun everyDynamicLoadInvokeFormIndependentlyMakesDispatchIncomplete(@TempDir temp: Path) {
        for ((id, instruction) in DYNAMIC_SITE_VARIANTS) {
            val ownerPath = Files.createDirectory(temp.resolve(id))
            val index = dynamicIndex(ownerPath, instruction)
            assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE), id)
        }
    }

    @Test
    fun staticEvidenceScansUnreachableMethodsInEveryLoadedApplicationClass(@TempDir temp: Path) {
        val index = dynamicIndex(temp, CLASS_FOR_NAME_SITE)

        assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))
    }

    @Test
    fun incompleteWorldDegradesOneCandidateAndRetainsItsDiagnosticSet(@TempDir temp: Path) {
        val index = dynamicIndex(temp, CLASS_FOR_NAME_SITE)

        val lookup = index.currentResolver().resolveInvocation(DYNAMIC_TARGET, InvokeKind.INTERFACE, DYNAMIC_CALLER, receiverType = null)

        assertTrue(lookup is InvocationLookup.Ambiguous)
        assertEquals(setOf("Ldynamic/OnlyImplementation;->value()I"), (lookup as InvocationLookup.Ambiguous).candidates)
    }

    @Test
    fun incompleteSingleCandidateSetIsRecordedOnTheOpaqueInvokeNode(@TempDir temp: Path) {
        val index = dynamicIndex(temp, CLASS_FOR_NAME_SITE)
        val view = index.requireMethodView(DYNAMIC_CALLER.descriptor, "call(Ldynamic/Dispatch;)I")
        val frame = Frame.entryFrame(view).also {
            it.assign(view.parameters.single().register, Value.Unknown(DYNAMIC_INTERFACE))
        }

        val graph = VmServices(index).newSession(view, entryFrame = frame).run()
        val invokeAddress = view.addresses.single { view.opcodeAt(it) == Opcode.INVOKE_INTERFACE }
        val invoke = graph.nodePile(invokeAddress).single()

        assertEquals(setOf("Ldynamic/OnlyImplementation;->value()I"), invoke.dispatchCandidates)
        assertEquals(Value.Unknown(VirtualType.of("I")), (graph.outcomes.single() as Outcome.Returned).value)
    }

    @Test
    fun finalClassesAndArraysStayCompleteWhenDynamicLoadEvidenceExists(@TempDir temp: Path) {
        val index = dynamicIndex(temp, CLASS_FOR_NAME_SITE)

        assertTrue(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_FINAL))
        assertTrue(index.currentResolver().dispatchWorldIsComplete(VirtualType.of("[I")))
        assertTrue(index.currentResolver().dispatchWorldIsComplete(VirtualType.of("[Ljava/lang/String;")))
        assertTrue(
            index.currentResolver().resolveInvocation(DYNAMIC_FINAL_TARGET, InvokeKind.VIRTUAL, DYNAMIC_SITE, receiverType = null) is
                InvocationLookup.Resolved,
        )
        assertTrue(
            index.currentResolver().resolveInvocation(ARRAY_CLONE_TARGET, InvokeKind.VIRTUAL, DYNAMIC_SITE, receiverType = null) is
                InvocationLookup.Resolved,
        )
        assertTrue(
            index.currentResolver().resolveInvocation(REFERENCE_ARRAY_CLONE_TARGET, InvokeKind.VIRTUAL, DYNAMIC_SITE, receiverType = null) is
                InvocationLookup.Resolved,
        )
    }

    @Test
    fun ownerBuiltWithSiteRemainsIncompleteAfterKnownClassForNameLoadsAcrossSessions(@TempDir temp: Path) {
        val index = dynamicIndex(temp, CLASS_FOR_NAME_SITE)
        assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))

        val tokens = loadKnownIndexedClassAcrossTwoSessions(index)

        assertSame(tokens.first(), tokens.last())
        assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertTrue(index.currentResolver().resolveInvocation(DYNAMIC_TARGET, InvokeKind.INTERFACE, DYNAMIC_SITE, null) is InvocationLookup.Ambiguous)
    }

    @Test
    fun unrelatedFrameworkEvidenceDoesNotDegradeApplicationDispatchAcrossSessions(@TempDir temp: Path) {
        val index = dynamicIndex(temp, "return-void")
        assertTrue(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))

        val tokens = loadKnownIndexedClassAcrossTwoSessions(index)
        val lookup = index.currentResolver().resolveInvocation(DYNAMIC_TARGET, InvokeKind.INTERFACE, DYNAMIC_CALLER, receiverType = null)

        assertSame(tokens.first(), tokens.last())
        assertTrue(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertTrue(lookup is InvocationLookup.Resolved)
        assertEquals(setOf("Ldynamic/OnlyImplementation;->value()I"), (lookup as InvocationLookup.Resolved).candidates)
    }

    @Test
    fun missingApplicationSuperclassMakesNonFinalDispatchIncomplete(@TempDir temp: Path) {
        val index = dynamicIndex(temp, "return-void", MISSING_SUPERCLASS)

        assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertTrue(
            index.lookupMethod(VirtualType.of("Ldynamic/MissingParentChild;"), "missing()V") is
                MethodLookup.NoSuchClass,
        )
    }

    @Test
    fun missingApplicationInterfaceMakesNonFinalDispatchIncomplete(@TempDir temp: Path) {
        val index = dynamicIndex(temp, "return-void", MISSING_INTERFACE)

        assertFalse(index.currentResolver().dispatchWorldIsComplete(DYNAMIC_INTERFACE))
        assertTrue(
            index.lookupMethod(VirtualType.of("Ldynamic/MissingInterfaceImplementor;"), "missing()V") is
                MethodLookup.NoSuchClass,
        )
    }

    /** ART rejects a class whose superclass or interface chain reaches itself (ClassCircularityError). */
    @Test
    fun classesOnAnInheritanceCycleAreNotLoadable(@TempDir temp: Path) {
        val index = indexOf(temp, CYCLE_A, CYCLE_B, CYCLE_CHILD, CYCLE_CALLER, INTERFACE_CYCLE_I, INTERFACE_CYCLE_J)

        assertFalse(index.currentSnapshot.hierarchy.isLoadable(t("Lcycle/A;")))
        assertFalse(index.currentSnapshot.hierarchy.isLoadable(t("Lcycle/B;")))
        assertFalse(index.currentSnapshot.hierarchy.isLoadable(t("Lcycle/I;")))
        assertFalse(index.currentSnapshot.hierarchy.isLoadable(t("Lcycle/J;")))
        assertTrue(index.currentSnapshot.hierarchy.isLoadable(t("Lcycle/Caller;")))
        assertFalse(index.currentResolver().dispatchWorldIsComplete(t("Lcycle/Caller;")))
    }

    /**
     * ART resolves a class on a longer cycle in `EnsureResolved`, which throws `ClassCircularityError` naming the
     * class being resolved; `LoadSuperAndInterfaces` names a class that extends itself. A method, field or type
     * reference reaches it. `Class.forName` wraps every lookup failure in `ClassNotFoundException`
     * (`Class_classForName`).
     */
    @Test
    fun usingAClassOnAnInheritanceCycleThrowsClassCircularityError(@TempDir temp: Path) {
        val index = indexOf(temp, CYCLE_A, CYCLE_B, CYCLE_SELF, CYCLE_CALLER, INTERFACE_CYCLE_I, INTERFACE_CYCLE_J)

        for ((method, message) in listOf(
            "call()V" to "cycle.A",
            "newInstance()V" to "cycle.A",
            "readStatic()V" to "cycle.A",
            "constInterface()V" to "cycle.I",
            "newSelf()V" to "Class cycle.Self extends itself",
        )) {
            val graph = run(index, "Lcycle/Caller;", method)
            assertEquals(listOf("Ljava/lang/ClassCircularityError;"), CoreTester.escapingTypes(graph), method)
            assertEquals(message, escapingMessage(graph), method)
        }
        val forName = run(index, "Lcycle/Caller;", "forName()V")
        assertEquals(listOf("Ljava/lang/ClassNotFoundException;"), CoreTester.escapingTypes(forName))
        assertEquals("cycle.A", escapingMessage(forName))
    }

    /**
     * Selection on the receiver's class that finds only an abstract method throws `AbstractMethodError` after the
     * receiver null check (`ArtMethod::ThrowInvocationTimeError`, `ThrowAbstractMethodError`).
     */
    @Test
    fun anAbstractSelectionThrowsAbstractMethodErrorOnInvocation(@TempDir temp: Path) {
        val index = indexOf(temp, ABSTRACT_SIDE, ABSTRACT_ONLY, ABSTRACT_BASE, CONCRETE_WITHOUT_VALUE)

        val interfaceCall = run(index, "Ldm/AbstractOnly;", "call()I")
        assertEquals(listOf("Ljava/lang/AbstractMethodError;"), CoreTester.escapingTypes(interfaceCall))
        assertEquals(
            "abstract method \"int dm.AbstractSide.value()\" on receiver java.lang.Class<dm.AbstractOnly>",
            escapingMessage(interfaceCall),
        )
        val virtualCall = run(index, "Ldm/ConcreteWithoutValue;", "call()I")
        assertEquals(listOf("Ljava/lang/AbstractMethodError;"), CoreTester.escapingTypes(virtualCall))
        assertEquals(
            "abstract method \"int dm.AbstractBase.value()\" on receiver java.lang.Class<dm.ConcreteWithoutValue>",
            escapingMessage(virtualCall),
        )
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"),
            CoreTester.escapingTypes(run(index, "Ldm/AbstractOnly;", "callOnNull()I")),
        )
        val unknownReceiver = run(index, "Ldm/AbstractOnly;", "callUnknown(Ldm/AbstractSide;)I")
        assertTrue(
            CoreTester.escapingTypes(unknownReceiver).none { it == "Ljava/lang/AbstractMethodError;" },
            "an unknown receiver may be any implementor, so resolution to the abstract method is not a selection",
        )
    }

    @Test
    fun hostInitializationChainTerminatesBelowASuperclassCycle(@TempDir temp: Path) {
        val index = indexOf(temp, CYCLE_A, CYCLE_B, CYCLE_CHILD)

        assertTimeoutPreemptively(Duration.ofSeconds(10)) {
            assertThrows(ClassLoadFailure::class.java) { index.currentResolver().hostInitializationChain(t("Lcycle/Child;")) }
        }
    }

    /**
     * JVMS 5.4.3.2 and ART `Class::FindStaticField`: the declaring class, then its direct superinterfaces
     * recursively, then the superclass.
     */
    @Test
    fun staticFieldResolutionSearchesSuperinterfacesBeforeTheSuperclass(@TempDir temp: Path) {
        val index = indexOf(temp, STATIC_IFACE, STATIC_SUBIFACE, STATIC_PARENT, STATIC_CHILD, STATIC_IMPL)

        val throughChild = index.resolveStaticField(t("Lsf/Child;"), "X", "I") as StaticFieldLookup.Resolved
        val throughImplementor = index.resolveStaticField(t("Lsf/Impl;"), "X", "I") as StaticFieldLookup.Resolved

        assertEquals(t("Lsf/Iface;"), throughChild.definingClass)
        assertEquals("Lsf/Iface;->X:I", throughChild.descriptor)
        assertEquals(t("Lsf/Iface;"), throughImplementor.definingClass)
        assertTrue(index.resolveStaticField(t("Lsf/Impl;"), "X", "J") is StaticFieldLookup.NoSuchField)
    }

    /** ART throws `IncompatibleClassChangeError` when a default-method conflict is invoked, after the null check. */
    @Test
    fun twoMaximallySpecificDefaultsAreAConflictThatThrowsOnInvocation(@TempDir temp: Path) {
        val index = indexOf(temp, DEFAULT_LEFT, DEFAULT_RIGHT, DEFAULT_BOTH)

        assertTrue(index.lookupMethod(t("Ldm/Both;"), "value()I") is MethodLookup.DefaultConflict)
        assertEquals(
            listOf("Ljava/lang/IncompatibleClassChangeError;"),
            CoreTester.escapingTypes(run(index, "Ldm/Both;", "call()I")),
        )
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"),
            CoreTester.escapingTypes(run(index, "Ldm/Both;", "callOnNull()I")),
        )
    }

    @Test
    fun theSingleNonAbstractMaximalInterfaceMethodIsSelected(@TempDir temp: Path) {
        val index = indexOf(temp, DEFAULT_RIGHT, ABSTRACT_SIDE, MIXED)

        val lookup = index.lookupMethod(t("Ldm/Mixed;"), "value()I") as MethodLookup.Runnable

        assertEquals(t("Ldm/Right;"), lookup.method.target.definingClass)
        assertEquals(Value.Known(2, t("I")), (run(index, "Ldm/Mixed;", "call()I").outcomes.single() as Outcome.Returned).value)
    }

    /** JVMS 5.4.3.3: private and static interface methods are not maximally-specific candidates. */
    @Test
    fun staticAndPrivateInterfaceMethodsAreNotInherited(@TempDir temp: Path) {
        val index = indexOf(temp, DEFAULT_RIGHT, STATIC_SIDE, PRIVATE_SIDE, ONLY_STATIC, ONLY_PRIVATE, STATIC_AND_DEFAULT)

        assertEquals(MethodLookup.NoSuchMethod, index.lookupMethod(t("Ldm/OnlyStatic;"), "value()I"))
        assertEquals(MethodLookup.NoSuchMethod, index.lookupMethod(t("Ldm/OnlyPrivate;"), "value()I"))
        val selected = index.lookupMethod(t("Ldm/StaticAndDefault;"), "value()I") as MethodLookup.Runnable
        assertEquals(t("Ldm/Right;"), selected.method.target.definingClass)
    }

    /** Framework subclasses of a framework type receive the call too, so one app override is not the target. */
    @Test
    fun oneApplicationOverrideOfAFrameworkMethodLeavesDispatchAmbiguous(@TempDir temp: Path) {
        val index = indexOf(temp, TO_STRING_OVERRIDE)
        val target = MethodDescriptor(VirtualType.OBJECT, "toString", "()Ljava/lang/String;")

        val lookup = index.currentResolver().resolveInvocation(target, InvokeKind.VIRTUAL, t("Lfw/Override;"), receiverType = null)

        assertTrue(lookup is InvocationLookup.Ambiguous, "a String receiver does not run the app override")
        val view = index.requireMethodView("Lfw/Override;", "call(Ljava/lang/Object;)Ljava/lang/String;")
        val frame = Frame.entryFrame(view).also {
            it.assign(view.parameters.single().register, Value.Known("device", t("Ljava/lang/String;")))
        }
        val returned = VmServices(index).newSession(view, entryFrame = frame).run().outcomes.single() as Outcome.Returned
        assertNotEquals(Value.Known("override", t("Ljava/lang/String;")), returned.value)

        val control = indexOf(Files.createDirectory(temp.resolve("control")), TO_STRING_INHERITED)
        val inherited = control.currentResolver().resolveInvocation(target, InvokeKind.VIRTUAL, t("Lfw/Plain;"), receiverType = null)
        assertTrue(inherited is InvocationLookup.Resolved, "no app class changes Object.toString's dispatch")
    }

    @Test
    fun anInstantiableClassWithABodylessSelectionIsADispatchCandidate(@TempDir temp: Path) {
        val index = indexOf(temp, NATIVE_BASE, NATIVE_SUB)
        val target = MethodDescriptor(t("Lnb/Base;"), "f", "()I")

        val lookup = index.currentResolver().resolveInvocation(target, InvokeKind.VIRTUAL, t("Lnb/Base;"), receiverType = null)

        assertTrue(lookup is InvocationLookup.Ambiguous, "a Base receiver runs the native Base.f, not Sub.f")
        assertEquals(setOf("Lnb/Base;->f()I", "Lnb/Sub;->f()I"), (lookup as InvocationLookup.Ambiguous).candidates)
    }

    /** `Class.forName` semantics: `java.util.Map.Entry` is not the binary name of `Map$Entry`. */
    @Test
    fun classConstantsAreLoadedByExactBinaryName() {
        assertNull(classIndex.currentResolver().loadClassConstant("Ljava/util/Map/Entry;"))
        assertSame(java.util.Map.Entry::class.java, classIndex.currentResolver().loadClassConstant("Ljava/util/Map\$Entry;"))
        assertSame(Integer.TYPE, classIndex.currentResolver().loadClassConstant("I"))
    }

    private fun indexOf(directory: Path, vararg sources: String): ClassIndex {
        sources.forEachIndexed { index, source ->
            Files.writeString(directory.resolve("source$index.smali"), source.trimIndent())
        }
        return ClassIndex.open(directory.toFile())
    }

    private fun run(index: ClassIndex, className: String, descriptor: String): ExecutionGraph =
        VmServices(index).newSession(index.requireMethodView(className, descriptor)).run()

    /** The message of the one throwable that escapes [graph], read from the heap of the node that threw it. */
    private fun escapingMessage(graph: ExecutionGraph): String? {
        val exception = graph.outcomes.filterIsInstance<Outcome.Threw>().single().exception as Value.Known
        return when (val raw = exception.raw) {
            is HeapRef -> graph.addresses().asSequence()
                .flatMap { graph.nodePile(it).asSequence() }
                .filter { it.outcome is Outcome.Threw }
                .firstNotNullOf { it.terminalFrame.hostObjectAt(raw) as? Throwable }
                .message
            is Throwable -> raw.message
            else -> error("no throwable behind $raw")
        }
    }

    private fun loadKnownIndexedClassAcrossTwoSessions(index: ClassIndex): List<Class<*>> {
        val owner = VmServices(index)
        val view = index.requireMethodView(DYNAMIC_CALLER.descriptor, "call(Ldynamic/Dispatch;)I")
        val loadedType = VirtualType.of("Ldynamic/OnlyImplementation;")
        val target = MethodDescriptor(
            VirtualType.of("Ljava/lang/Class;"),
            "forName",
            "(Ljava/lang/String;)Ljava/lang/Class;",
        )
        return List(2) {
            val frame = owner.newSession(view).frontier.single().frame
            frame.completeClassInit(loadedType)
            val result = ClassForNameEmulator(index.currentResolver()).execute(
                EmulatorInvocation(
                    target,
                    DYNAMIC_CALLER,
                    InvokeKind.STATIC,
                    VirtualType.of("Ljava/lang/Class;"),
                    listOf(VirtualType.of("Ljava/lang/String;")),
                    null,
                    listOf(EmulatorValue.Known(VirtualType.of("Ljava/lang/String;"), "dynamic.OnlyImplementation")),
                    frame.classInitStatusSnapshot(),
                ),
            ) as SynchronousCallResult.Returned
            ((result.value as HostReturn.Value).value as HostValue.Reference).value as Class<*>
        }
    }

    private fun dynamicIndex(temp: Path, siteInstruction: String, extraClass: String? = null): ClassIndex {
        Files.writeString(temp.resolve("base.smali"), DYNAMIC_BASE)
        Files.writeString(temp.resolve("implementation.smali"), DYNAMIC_IMPLEMENTATION)
        Files.writeString(temp.resolve("final.smali"), DYNAMIC_FINAL_CLASS)
        Files.writeString(temp.resolve("site.smali"), DYNAMIC_SITE_CLASS.replace("SITE_INSTRUCTION", siteInstruction))
        Files.writeString(temp.resolve("caller.smali"), DYNAMIC_CALLER_CLASS)
        extraClass?.let { Files.writeString(temp.resolve("extra.smali"), it) }
        return ClassIndex.open(temp.toFile())
    }

    private companion object {
        val DYNAMIC_INTERFACE = VirtualType.of("Ldynamic/Dispatch;")
        val DYNAMIC_FINAL = VirtualType.of("Ldynamic/Final;")
        val DYNAMIC_SITE = VirtualType.of("Ldynamic/UnrelatedSite;")
        val DYNAMIC_CALLER = VirtualType.of("Ldynamic/Caller;")
        val DYNAMIC_TARGET = MethodDescriptor(DYNAMIC_INTERFACE, "value", "()I")
        val DYNAMIC_FINAL_TARGET = MethodDescriptor(DYNAMIC_FINAL, "value", "()I")
        val ARRAY_CLONE_TARGET = MethodDescriptor(VirtualType.of("[I"), "clone", "()Ljava/lang/Object;")
        val REFERENCE_ARRAY_CLONE_TARGET =
            MethodDescriptor(VirtualType.of("[Ljava/lang/String;"), "clone", "()Ljava/lang/Object;")

        const val CLASS_FOR_NAME_SITE =
            "invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;"
        val DYNAMIC_SITE_VARIANTS = linkedMapOf(
            "class-for-name-one" to CLASS_FOR_NAME_SITE,
            "class-for-name-one-range" to
                "invoke-static/range {v0 .. v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;",
            "class-for-name-three" to
                "invoke-static {v0, v1, v2}, Ljava/lang/Class;->forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
            "class-for-name-three-range" to
                "invoke-static/range {v0 .. v2}, Ljava/lang/Class;->forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
            "class-loader" to
                "invoke-virtual {v0, v1}, Ljava/lang/ClassLoader;->loadClass(Ljava/lang/String;)Ljava/lang/Class;",
            "class-loader-range" to
                "invoke-virtual/range {v0 .. v1}, Ljava/lang/ClassLoader;->loadClass(Ljava/lang/String;)Ljava/lang/Class;",
            "dex-class-loader" to
                "invoke-direct {v0, v1, v2, v3, v4}, Ldalvik/system/DexClassLoader;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V",
            "dex-class-loader-range" to
                "invoke-direct/range {v0 .. v4}, Ldalvik/system/DexClassLoader;-><init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V",
            "path-class-loader" to
                "invoke-direct {v0, v1, v2}, Ldalvik/system/PathClassLoader;-><init>(Ljava/lang/String;Ljava/lang/ClassLoader;)V",
            "path-class-loader-range" to
                "invoke-direct/range {v0 .. v2}, Ldalvik/system/PathClassLoader;-><init>(Ljava/lang/String;Ljava/lang/ClassLoader;)V",
        )

        val DYNAMIC_BASE = """
            .class public interface abstract Ldynamic/Dispatch;
            .super Ljava/lang/Object;

            .method public abstract value()I
            .end method
        """.trimIndent()

        val DYNAMIC_IMPLEMENTATION = """
            .class public Ldynamic/OnlyImplementation;
            .super Ljava/lang/Object;
            .implements Ldynamic/Dispatch;

            .method public value()I
                .locals 1
                const/4 v0, 0x1
                return v0
            .end method
        """.trimIndent()

        val DYNAMIC_FINAL_CLASS = """
            .class public final Ldynamic/Final;
            .super Ljava/lang/Object;

            .method public value()I
                .locals 1
                const/4 v0, 0x2
                return v0
            .end method
        """.trimIndent()

        val DYNAMIC_SITE_CLASS = """
            .class public Ldynamic/UnrelatedSite;
            .super Ljava/lang/Object;

            .method public static neverCalled()V
                .locals 5
                SITE_INSTRUCTION
                return-void
            .end method
        """.trimIndent()

        val DYNAMIC_CALLER_CLASS = """
            .class public Ldynamic/Caller;
            .super Ljava/lang/Object;

            .method public static call(Ldynamic/Dispatch;)I
                .locals 1
                invoke-interface {p0}, Ldynamic/Dispatch;->value()I
                move-result v0
                return v0
            .end method
        """.trimIndent()

        val MISSING_SUPERCLASS = """
            .class public Ldynamic/MissingParentChild;
            .super Ldynamic/AbsentParent;
        """.trimIndent()

        val MISSING_INTERFACE = """
            .class public Ldynamic/MissingInterfaceImplementor;
            .super Ljava/lang/Object;
            .implements Ldynamic/AbsentInterface;
        """.trimIndent()

        const val CYCLE_A = """
            .class public Lcycle/A;
            .super Lcycle/B;

            .method public static f()V
                .registers 0
                return-void
            .end method
        """

        const val CYCLE_B = """
            .class public Lcycle/B;
            .super Lcycle/A;
        """

        const val CYCLE_CHILD = """
            .class public Lcycle/Child;
            .super Lcycle/A;
        """

        const val CYCLE_CALLER = """
            .class public Lcycle/Caller;
            .super Ljava/lang/Object;

            .method public static call()V
                .registers 0
                invoke-static {}, Lcycle/A;->f()V
                return-void
            .end method

            .method public static newInstance()V
                .registers 1
                new-instance v0, Lcycle/A;
                return-void
            .end method

            .method public static readStatic()V
                .registers 1
                sget v0, Lcycle/A;->x:I
                return-void
            .end method

            .method public static constInterface()V
                .registers 1
                const-class v0, Lcycle/I;
                return-void
            .end method

            .method public static forName()V
                .registers 1
                const-string v0, "cycle.A"
                invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
                return-void
            .end method

            .method public static newSelf()V
                .registers 1
                new-instance v0, Lcycle/Self;
                return-void
            .end method
        """

        const val CYCLE_SELF = """
            .class public Lcycle/Self;
            .super Lcycle/Self;
        """

        const val INTERFACE_CYCLE_I = """
            .class public interface abstract Lcycle/I;
            .super Ljava/lang/Object;
            .implements Lcycle/J;
        """

        const val INTERFACE_CYCLE_J = """
            .class public interface abstract Lcycle/J;
            .super Ljava/lang/Object;
            .implements Lcycle/I;
        """

        const val STATIC_IFACE = """
            .class public interface abstract Lsf/Iface;
            .super Ljava/lang/Object;
            .field public static final X:I = 0x1
        """

        const val STATIC_SUBIFACE = """
            .class public interface abstract Lsf/SubIface;
            .super Ljava/lang/Object;
            .implements Lsf/Iface;
        """

        const val STATIC_PARENT = """
            .class public Lsf/Parent;
            .super Ljava/lang/Object;
            .field public static X:I
        """

        const val STATIC_CHILD = """
            .class public Lsf/Child;
            .super Lsf/Parent;
            .implements Lsf/SubIface;
        """

        const val STATIC_IMPL = """
            .class public Lsf/Impl;
            .super Ljava/lang/Object;
            .implements Lsf/Iface;
        """

        const val DEFAULT_LEFT = """
            .class public interface abstract Ldm/Left;
            .super Ljava/lang/Object;

            .method public value()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
        """

        const val DEFAULT_RIGHT = """
            .class public interface abstract Ldm/Right;
            .super Ljava/lang/Object;

            .method public value()I
                .registers 2
                const/4 v0, 0x2
                return v0
            .end method
        """

        const val DEFAULT_BOTH = """
            .class public Ldm/Both;
            .super Ljava/lang/Object;
            .implements Ldm/Left;
            .implements Ldm/Right;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Ldm/Both;
                invoke-direct {v0}, Ldm/Both;-><init>()V
                invoke-virtual {v0}, Ldm/Both;->value()I
                move-result v0
                return v0
            .end method

            .method public static callOnNull()I
                .registers 1
                const/4 v0, 0x0
                invoke-virtual {v0}, Ldm/Both;->value()I
                move-result v0
                return v0
            .end method
        """

        const val ABSTRACT_SIDE = """
            .class public interface abstract Ldm/AbstractSide;
            .super Ljava/lang/Object;

            .method public abstract value()I
            .end method
        """

        const val ABSTRACT_ONLY = """
            .class public Ldm/AbstractOnly;
            .super Ljava/lang/Object;
            .implements Ldm/AbstractSide;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Ldm/AbstractOnly;
                invoke-direct {v0}, Ldm/AbstractOnly;-><init>()V
                invoke-interface {v0}, Ldm/AbstractSide;->value()I
                move-result v0
                return v0
            .end method

            .method public static callOnNull()I
                .registers 1
                const/4 v0, 0x0
                invoke-interface {v0}, Ldm/AbstractSide;->value()I
                move-result v0
                return v0
            .end method

            .method public static callUnknown(Ldm/AbstractSide;)I
                .registers 1
                invoke-interface {p0}, Ldm/AbstractSide;->value()I
                move-result v0
                return v0
            .end method
        """

        const val ABSTRACT_BASE = """
            .class public abstract Ldm/AbstractBase;
            .super Ljava/lang/Object;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public abstract value()I
            .end method
        """

        const val CONCRETE_WITHOUT_VALUE = """
            .class public Ldm/ConcreteWithoutValue;
            .super Ldm/AbstractBase;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ldm/AbstractBase;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Ldm/ConcreteWithoutValue;
                invoke-direct {v0}, Ldm/ConcreteWithoutValue;-><init>()V
                invoke-virtual {v0}, Ldm/AbstractBase;->value()I
                move-result v0
                return v0
            .end method
        """

        const val MIXED = """
            .class public Ldm/Mixed;
            .super Ljava/lang/Object;
            .implements Ldm/AbstractSide;
            .implements Ldm/Right;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Ldm/Mixed;
                invoke-direct {v0}, Ldm/Mixed;-><init>()V
                invoke-virtual {v0}, Ldm/Mixed;->value()I
                move-result v0
                return v0
            .end method
        """

        const val STATIC_SIDE = """
            .class public interface abstract Ldm/StaticSide;
            .super Ljava/lang/Object;

            .method public static value()I
                .registers 1
                const/4 v0, 0x3
                return v0
            .end method
        """

        const val PRIVATE_SIDE = """
            .class public interface abstract Ldm/PrivateSide;
            .super Ljava/lang/Object;

            .method private value()I
                .registers 2
                const/4 v0, 0x4
                return v0
            .end method
        """

        const val ONLY_STATIC = """
            .class public Ldm/OnlyStatic;
            .super Ljava/lang/Object;
            .implements Ldm/StaticSide;
        """

        const val ONLY_PRIVATE = """
            .class public Ldm/OnlyPrivate;
            .super Ljava/lang/Object;
            .implements Ldm/PrivateSide;
        """

        const val STATIC_AND_DEFAULT = """
            .class public Ldm/StaticAndDefault;
            .super Ljava/lang/Object;
            .implements Ldm/StaticSide;
            .implements Ldm/Right;
        """

        const val TO_STRING_OVERRIDE = """
            .class public Lfw/Override;
            .super Ljava/lang/Object;

            .method public toString()Ljava/lang/String;
                .registers 2
                const-string v0, "override"
                return-object v0
            .end method

            .method public static call(Ljava/lang/Object;)Ljava/lang/String;
                .registers 2
                invoke-virtual {p0}, Ljava/lang/Object;->toString()Ljava/lang/String;
                move-result-object v0
                return-object v0
            .end method
        """

        const val TO_STRING_INHERITED = """
            .class public Lfw/Plain;
            .super Ljava/lang/Object;
        """

        const val NATIVE_BASE = """
            .class public Lnb/Base;
            .super Ljava/lang/Object;

            .method public native f()I
            .end method
        """

        const val NATIVE_SUB = """
            .class public Lnb/Sub;
            .super Lnb/Base;

            .method public f()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
        """
    }
}
