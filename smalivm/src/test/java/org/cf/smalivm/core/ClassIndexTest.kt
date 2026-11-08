package org.cf.smalivm.core

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/** [ClassIndex] subtype, instance-of and member-resolution queries, checked against the real Android/JDK hierarchy. */
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
        // No primitive/reference bridging: those types join to Top.
        assertTrue(classIndex.isSubtypeOf(t("I"), t("I")))
        assertFalse(classIndex.isSubtypeOf(t("I"), t("Ljava/lang/Object;")))
        assertFalse(classIndex.isSubtypeOf(t("I"), t("J")))
    }

    @Test
    fun anUnloadableClassReportsATypedLoadFailure() {
        // A class missing from the program must be distinguishable from an engine bug. `Lspine/absent;` is
        // well-formed with no smali behind it, the normal case in an obfuscated APK.
        val absent = t("Lspine/absent;")
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.isSubtypeOf(absent, t("Ljava/lang/RuntimeException;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
        // Either operand, in either position.
        assertThrows(ClassLoadFailure::class.java) {
            classIndex.isSubtypeOf(t("Ljava/lang/RuntimeException;"), absent)
        }
        // The pure lattice answers short-circuit before any load, so reflexivity and primitive comparison
        // still answer for an unloadable class.
        assertTrue(classIndex.isSubtypeOf(absent, absent), "reflexive: no class model reach at all")
        assertFalse(classIndex.isSubtypeOf(t("I"), absent), "no primitive/reference bridging")
    }

    @Test
    fun aPrimitiveComponentArrayIsNotASubtypeOfAnObjectArray() {
        // Primitive arrays are invariant. `[I`'s only immediate ancestor is Object, so its ancestor set never
        // contains `[Ljava/lang/Object;`. Covariance in the other direction is covered by
        // `CoreTesterTest.aDeclaredArrayTypeAcceptsALegallyCovariantRuntimeArray`.
        assertFalse(classIndex.isSubtypeOf(t("[I"), t("[Ljava/lang/Object;")))
    }

    @Test
    fun anAbsentDescriptorIsNotLoadableWhileAPresentOneAndEveryPrimitiveIs() {
        // NewArrayOp's NoClassDefFoundError depends on this: if `isLoadable` answered true for an absent
        // class, `new-array [Ldoes/not/exist;` would allocate instead of throwing.
        assertFalse(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[Ldoes/not/exist;")))
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[Lnew_array_test;")))
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("[[I")), "a primitive component is always loadable")
        assertTrue(CoreTester.classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of("I")), "and so is a bare primitive")
    }

    // `isInstanceOf` and `isSubtypeOf` have different contracts; the tests below pin two independent
    // differences so the predicates are not collapsed into one.

    @Test
    fun anyReferenceIsAnInstanceOfObjectWithoutLoadingItsAncestors() {
        // `instance-of v1, v0, Ljava/lang/Object;` answers 1 for any non-null reference whatever its class
        // (DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited), so it must not need the ancestor walk that
        // an absent class cannot provide.
        assertTrue(classIndex.isInstanceOf(t("Lspine/absent;"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/lang/Object;")))
    }

    @Test
    fun aPrimitiveTargetIsNeverAnInstanceAndNeitherIsAPrimitiveCandidate() {
        // Dalvik bytecode reference, `instance-of`: "this will always result in 0 ... if C refers to a
        // primitive type". Both operand positions are covered.
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/Integer;"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/Object;"), t("V")))
        // A primitive candidate reads as a null reference, and null is an instance of nothing
        // (`Value.readsAsNullReference`).
        assertFalse(classIndex.isInstanceOf(t("I"), t("Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("I"), t("Ljava/lang/Integer;")))
    }

    @Test
    fun everyArrayIsAnInstanceOfCloneableAndSerializable() {
        // Arrays implement exactly Cloneable and Serializable, primitive components included
        // (DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited).
        val cloneable = t("Ljava/lang/Cloneable;")
        val serializable = t("Ljava/io/Serializable;")
        for (array in listOf("[I", "[[I", "[Ljava/lang/String;", "[[Ljava/lang/String;")) {
            assertTrue(classIndex.isInstanceOf(t(array), cloneable), "$array is Cloneable")
            assertTrue(classIndex.isInstanceOf(t(array), serializable), "$array is Serializable")
        }
        // A non-array gets no free pass: String implements Serializable through its hierarchy, while
        // StringBuilder does not implement Cloneable.
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), serializable))
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/StringBuilder;"), cloneable))
    }

    @Test
    fun arrayCovarianceAndInvarianceAreBothPreserved() {
        // JLS §4.10.3: `S[] >1 T[]` iff `S >1 T` for references, and `Object >1 P[]` for a primitive P, but
        // `int[]` is not an `Object[]`.
        assertTrue(classIndex.isInstanceOf(t("[I"), t("Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[Ljava/lang/String;"), t("[Ljava/lang/Object;")))
        assertTrue(classIndex.isInstanceOf(t("[[Ljava/lang/String;"), t("[[Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("[Ljava/lang/Object;")))
        assertFalse(classIndex.isInstanceOf(t("[I"), t("[J")))
        // A class is never an instance of an array type.
        assertFalse(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("[Ljava/lang/String;")))
    }

    @Test
    fun theArraySubtypeMatrixAgreesWithTheDeviceAtEveryCell() {
        // Candidate in {String[], Object[], int[], long[]} x target in {Object, Cloneable, Serializable,
        // Object[]}, as measured on ART by the `tools/art-runner` subtype fixtures. ART answered `instance-of`
        // and `check-cast` identically at every cell, and `castOutcome` delegates to this predicate, so the
        // grid pins both ops. The informative cells: primitive-component arrays are not `Object[]`, and every
        // array is Cloneable and Serializable.
        val objectT = t("Ljava/lang/Object;")
        val cloneable = t("Ljava/lang/Cloneable;")
        val serializable = t("Ljava/io/Serializable;")
        val objectArray = t("[Ljava/lang/Object;")
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
            Row("[I", true, true, true, false), // primitive component: invariant
            Row("[J", true, true, true, false),
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
        // For an ordinary class candidate `instance-of` consults interfaces; only `joinTypes` excludes them
        // (see `TypeJoinTest`).
        assertTrue(classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Ljava/lang/CharSequence;")))
        assertTrue(classIndex.isInstanceOf(t("Lchild_class;"), t("Lparent_class;")))
        assertTrue(classIndex.isInstanceOf(t("Lgrandparent_class;"), t("Lgrandparent_interface;")))
        assertTrue(classIndex.isInstanceOf(t("Lchild_class;"), t("Lgrandparent_interface;")))
        assertFalse(classIndex.isInstanceOf(t("Lparent_class;"), t("Lchild_class;")))
    }

    @Test
    fun aPrimitiveReflexivePairIsWhereTheTwoPredicatesDisagreeMostVisibly() {
        // Both differences are asserted on the same arguments.
        // (1) `isSubtypeOf(I, I)` is true (a type is its own ancestor, which `aput`'s component check needs);
        //     `isInstanceOf(I, I)` is false because a primitive `instance-of` target always answers 0.
        assertTrue(classIndex.isSubtypeOf(t("I"), t("I")))
        assertFalse(classIndex.isInstanceOf(t("I"), t("I")))
        // (2) For an unloadable candidate against Object, `isSubtypeOf` raises rather than invent an answer
        //     that could un-catch a handler; `isInstanceOf` answers without loading.
        val absent = t("Lspine/absent;")
        assertThrows(ClassLoadFailure::class.java) { classIndex.isSubtypeOf(absent, t("Ljava/lang/Object;")) }
        assertTrue(classIndex.isInstanceOf(absent, t("Ljava/lang/Object;")))
    }

    @Test
    fun instanceOfReportsATypedLoadFailureWhenItGenuinelyHasToWalk() {
        // An unanswerable question is a load failure, never `false`: `false` would fold a wrong `instance-of`
        // constant and invent a definite `ClassCastException`.
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.isInstanceOf(t("Lspine/absent;"), t("Ljava/lang/String;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
        // An absent target fails the same way.
        assertThrows(ClassLoadFailure::class.java) {
            classIndex.isInstanceOf(t("Ljava/lang/String;"), t("Lspine/absent;"))
        }
        // An array candidate against the array supertypes still answers: those rows compare descriptors only.
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/lang/Cloneable;")))
        assertTrue(classIndex.isInstanceOf(t("[Lspine/absent;"), t("Ljava/io/Serializable;")))
    }

    @Test
    fun theSuperclassChainIsReadFromTheClassDefAndNeverFromTheAncestorSet() {
        // `superclassOf` reads the declared superclass, not the ancestor set.
        assertEquals(t("Lparent_class;"), classIndex.superclassOf(t("Lchild_class;")))
        assertEquals(t("Lgrandparent_class;"), classIndex.superclassOf(t("Lparent_class;")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("Lgrandparent_class;")))
        assertNull(classIndex.superclassOf(t("Ljava/lang/Object;")), "the chain terminates at Object")
        // The interface is a supertype of `Lgrandparent_class;` but must not appear on the superclass chain.
        assertTrue(classIndex.isSubtypeOf(t("Lgrandparent_class;"), t("Lgrandparent_interface;")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("Lgrandparent_class;")))
    }

    @Test
    fun everyArrayHasObjectAsItsSuperclassAndPrimitivesHaveNone() {
        // An array's superclass is Object (JLS §4.10.3); its array-typed supertypes are a covariance relation
        // that `joinTypes` handles component-wise.
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[I")))
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[[Ljava/lang/String;")))
        // No load is attempted for the component, so an array of an absent class still answers.
        assertEquals(t("Ljava/lang/Object;"), classIndex.superclassOf(t("[Lspine/absent;")))
        assertNull(classIndex.superclassOf(t("I")))
        assertNull(classIndex.superclassOf(t("V")))
    }

    @Test
    fun anUnloadableClassReportsALoadFailureFromSuperclassOfToo() {
        // Same contract as `isSubtypeOf`: callers choose what to do with a missing class (`resolveHandler`
        // propagates, `joinTypes` degrades to Object), which needs the failure reported, not swallowed.
        val failure = assertThrows(ClassLoadFailure::class.java) {
            classIndex.superclassOf(t("Lspine/absent;"))
        }
        assertTrue(failure.message!!.contains("Lspine/absent;"), "the diagnostic must name the descriptor")
    }

    @Test
    fun directInterfacesOfAnAbsentClassIsALoadFailureNotAnEmptyList() {
        // "Implements nothing" for an absent class would let a member walk report a definite miss for a member
        // the class may declare; both parent queries share `superclassOf`'s failure rule.
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

        val throughChild = index.resolveStaticField(FieldSignature(t("Lsf/Child;"), "X", t("I"))) as StaticFieldLookup.Resolved
        val throughImplementor = index.resolveStaticField(FieldSignature(t("Lsf/Impl;"), "X", t("I"))) as StaticFieldLookup.Resolved

        assertEquals(t("Lsf/Iface;"), throughChild.definingClass)
        assertEquals("Lsf/Iface;->X:I", throughChild.descriptor)
        assertEquals(t("Lsf/Iface;"), throughImplementor.definingClass)
        assertTrue(index.resolveStaticField(FieldSignature(t("Lsf/Impl;"), "X", t("J"))) is StaticFieldLookup.NoSuchField)
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

    /**
     * JVMS 5.4.3.3: a superinterface method is not maximally specific when a subinterface that also
     * declares it is among the class's superinterfaces, so `Sub.value` is selected without a conflict, in
     * either `.implements` order and whether the call site names the class or the superinterface.
     */
    @Test
    fun aSubinterfaceDefaultIsMoreSpecificThanTheDefaultItOverrides(@TempDir temp: Path) {
        val index = indexOf(temp, SPECIFIC_BASE, SPECIFIC_SUB, SPECIFIC_BASE_FIRST, SPECIFIC_SUB_FIRST)

        for (implementor in listOf("Lms/BaseFirst;", "Lms/SubFirst;")) {
            val lookup = index.lookupMethod(t(implementor), "value()I")
            assertTrue(lookup is MethodLookup.Runnable, "$implementor: $lookup")
            assertEquals(t("Lms/Sub;"), (lookup as MethodLookup.Runnable).method.target.definingClass, implementor)
            val returned = run(index, implementor, "call()I").outcomes.single() as Outcome.Returned
            assertEquals(Value.Known(2, t("I")), returned.value, implementor)
        }
    }

    /**
     * Dalvik bytecode reference, `invoke-super`: when the method_id names an interface method, the call runs
     * the most specific non-overridden version defined on that interface. The receiver's own override is
     * therefore not the target, which a virtual dispatch on the receiver would pick.
     */
    @Test
    fun invokeSuperIntoADefaultRunsTheInterfaceBodyNotTheReceiversOverride(@TempDir temp: Path) {
        val index = indexOf(temp, SUPER_DEFAULT_IFACE, SUPER_DEFAULT_IMPL)

        val returned = run(index, "Lsd/Impl;", "call()I").outcomes.single() as Outcome.Returned

        assertEquals(Value.Known(1, t("I")), returned.value)
    }

    /** Framework subclasses of a framework type receive the call too, so one app override is not the target. */
    @Test
    fun oneApplicationOverrideOfAFrameworkMethodLeavesDispatchAmbiguous(@TempDir temp: Path) {
        val index = indexOf(temp, TO_STRING_OVERRIDE)
        val target = MethodSignature(VirtualType.OBJECT, "toString", "()Ljava/lang/String;")

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
        val target = MethodSignature(t("Lnb/Base;"), "f", "()I")

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
                .firstNotNullOf { it.terminalFrame.heap.hostObjectAt(raw) as? Throwable }
                .message
            is Throwable -> raw.message
            else -> error("no throwable behind $raw")
        }
    }

    private fun loadKnownIndexedClassAcrossTwoSessions(index: ClassIndex): List<Class<*>> {
        val owner = VmServices(index)
        val view = index.requireMethodView(DYNAMIC_CALLER.descriptor, "call(Ldynamic/Dispatch;)I")
        val loadedType = VirtualType.of("Ldynamic/OnlyImplementation;")
        val target = MethodSignature(
            VirtualType.of("Ljava/lang/Class;"),
            "forName",
            "(Ljava/lang/String;)Ljava/lang/Class;",
        )
        return List(2) {
            val frame = owner.newSession(view).frontier.single().frame
            frame.classes.complete(loadedType)
            val result = ClassForNameEmulator(index.currentResolver()).execute(
                EmulatorInvocation(
                    target,
                    DYNAMIC_CALLER,
                    InvokeKind.STATIC,
                    VirtualType.of("Ljava/lang/Class;"),
                    listOf(VirtualType.of("Ljava/lang/String;")),
                    null,
                    listOf(EmulatorValue.Known(VirtualType.of("Ljava/lang/String;"), "dynamic.OnlyImplementation")),
                    frame.classes.statusSnapshot(),
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
        val DYNAMIC_TARGET = MethodSignature(DYNAMIC_INTERFACE, "value", "()I")
        val DYNAMIC_FINAL_TARGET = MethodSignature(DYNAMIC_FINAL, "value", "()I")
        val ARRAY_CLONE_TARGET = MethodSignature(VirtualType.of("[I"), "clone", "()Ljava/lang/Object;")
        val REFERENCE_ARRAY_CLONE_TARGET =
            MethodSignature(VirtualType.of("[Ljava/lang/String;"), "clone", "()Ljava/lang/Object;")

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

        const val SPECIFIC_BASE = """
            .class public interface abstract Lms/Base;
            .super Ljava/lang/Object;

            .method public value()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
        """

        const val SPECIFIC_SUB = """
            .class public interface abstract Lms/Sub;
            .super Ljava/lang/Object;
            .implements Lms/Base;

            .method public value()I
                .registers 2
                const/4 v0, 0x2
                return v0
            .end method
        """

        const val SPECIFIC_BASE_FIRST = """
            .class public Lms/BaseFirst;
            .super Ljava/lang/Object;
            .implements Lms/Base;
            .implements Lms/Sub;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Lms/BaseFirst;
                invoke-direct {v0}, Lms/BaseFirst;-><init>()V
                invoke-interface {v0}, Lms/Base;->value()I
                move-result v0
                return v0
            .end method
        """

        const val SPECIFIC_SUB_FIRST = """
            .class public Lms/SubFirst;
            .super Ljava/lang/Object;
            .implements Lms/Sub;
            .implements Lms/Base;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Lms/SubFirst;
                invoke-direct {v0}, Lms/SubFirst;-><init>()V
                invoke-virtual {v0}, Lms/SubFirst;->value()I
                move-result v0
                return v0
            .end method
        """

        const val SUPER_DEFAULT_IFACE = """
            .class public interface abstract Lsd/Iface;
            .super Ljava/lang/Object;

            .method public value()I
                .registers 2
                const/4 v0, 0x1
                return v0
            .end method
        """

        const val SUPER_DEFAULT_IMPL = """
            .class public Lsd/Impl;
            .super Ljava/lang/Object;
            .implements Lsd/Iface;

            .method public constructor <init>()V
                .registers 1
                invoke-direct {p0}, Ljava/lang/Object;-><init>()V
                return-void
            .end method

            .method public value()I
                .registers 2
                const/4 v0, 0x2
                return v0
            .end method

            .method public callSuper()I
                .registers 2
                invoke-super {p0}, Lsd/Iface;->value()I
                move-result v0
                return v0
            .end method

            .method public static call()I
                .registers 1
                new-instance v0, Lsd/Impl;
                invoke-direct {v0}, Lsd/Impl;-><init>()V
                invoke-virtual {v0}, Lsd/Impl;->callSuper()I
                move-result v0
                return v0
            .end method
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
