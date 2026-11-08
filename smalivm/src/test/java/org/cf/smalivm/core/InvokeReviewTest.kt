package org.cf.smalivm.core

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InvokeReviewTest {
    private val int = VirtualType.of("I")

    @Test
    fun mixedReturnAndLimitedAbortHasTheSameOutcomesInEitherArmOrder() {
        // The two callers reach callees whose `if` declares the looping arm and the returning arm in opposite
        // orders.
        for (caller in listOf("abortArmDeclaredFirst(I)I", "returnArmDeclaredFirst(I)I")) {
            val graph = CoreTester.execute(REVIEW, caller, limits = Limits(maxAddressVisits = 3, maxSteps = 200))
            assertEquals(
                CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)),
                graph.completion(),
                caller,
            )
            assertNull(graph.trusted(), caller)
            assertEquals(
                setOf(Outcome.Returned(Value.Known(7, int)), Outcome.Returned(Value.Unknown(int))),
                graph.outcomes.toSet(),
                caller,
            )
            assertEquals(2, graph.outcomes.size, caller)
            assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        }
    }

    @Test
    fun incompatibleClassInitStatesStayOnSeparateCallerPaths() {
        val graph = CoreTester.execute(REVIEW, "conditionalThenTrigger(I)Linvoke_review_identity;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Unknown(VirtualType.of("Linvoke_review_identity;")),
            graph.trusted()!!.terminatingConsensus(0),
            "one concrete path initializes before the call returns; the other initializes after it",
        )
        assertEquals(2, graph.outcomes.filterIsInstance<Outcome.Returned>().size)
    }

    @Test
    fun invokeStaticNeverInheritsAParentClinit() {
        val graph = CoreTester.execute(REVIEW, "invokeInheritedClinit()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun invokeSuperResolvesAnUnambiguousInterfaceDefault() {
        CoreTester.assertReturns(REVIEW, "callInterfaceDefault()I", Value.Known(11, int))
    }

    @Test
    fun onlyExactKnownIntZeroCoercesToNullDuringBinding() {
        CoreTester.assertReturns(
            REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            Value.Known(5, int),
        )
        CoreTester.assertReturns(
            "Linvoke_task20;",
            "zeroBecomesNull()Ljava/lang/Object;",
            Value.Known(null, VirtualType.OBJECT),
        )
    }

    @Test
    fun arityMismatchUsesTypedOpaqueFallbackAndCollapsesArguments() {
        val graph = CoreTester.execute(REVIEW, "arityMismatchOpaque()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(int), graph.trusted()!!.terminatingConsensus(2))
        val arrayValue = graph.trusted()!!.terminatingConsensus(1) as Value.Known
        val array = graph.trusted()!!.terminatingHeapObject(arrayValue.raw as HeapRef).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(int), array.element(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        val diagnostic = graph.addresses().flatMap { graph.nodePile(it) }.mapNotNull { it.invocationDiagnostic }.single()
        assertTrue(diagnostic.contains("argument register"), diagnostic)
    }

    @Test
    fun malformedSyntheticResumeInNestedCallerDoesNotLeaveAPhantomPath() {
        val graph = CoreTester.execute(REVIEW, "nestedMalformedResume()I")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun malformedSyntheticHandlerInNestedCallerDoesNotLeaveAPhantomPath() {
        val outer = view(REVIEW, "nestedMalformedHandler()I")
        val innerSignature = "$REVIEW->innerWithHandler()V"
        val innerView = view(REVIEW, "innerWithHandler()V")
        val badHandler = innerView.tryBlocks.single().catches.single().handlerAddress
        val graph = CoreTester.vmServices.newSession(outer, Limits(), null) { address, v, resolver ->
            if (v.signature.toString() == innerSignature && address == badHandler) error("synthetic handler decode failure")
            OpBuilder.build(address, v, resolver)
        }.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(2, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun resultTransportCommittedBeforeMalformedResumeRemainsVisible() {
        val graph = CoreTester.execute(REVIEW, "readCommittedResultAfterMalformedResume()I")

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun caughtSlotConsumedBeforeMalformedResumeRetainsTheCaughtReference() {
        val graph = CoreTester.execute(REVIEW, "readCommittedCaughtAfterMalformedResume()Ljava/lang/Throwable;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(VirtualType.of("Ljava/lang/NullPointerException;"), returned.type)
        assertNotNull(CoreTester.terminatingHostObject(graph, returned.raw as HeapRef))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun constructorCompletionCommittedBeforeMalformedResumeRemainsVisible() {
        val graph = CoreTester.execute(REVIEW, "readCommittedConstructorAfterMalformedResume()Ljava/lang/Object;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val obj = graph.explored.terminatingHeapObject(returned.raw as HeapRef).agreed() as HeapObject.TrackedObject

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertFalse(obj.uninitialized)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun opaqueReferenceResultsKeepTheirExactDeclaredArrayAndClassTypes() {
        CoreTester.assertReturns(
            REVIEW,
            "callOpaqueArrayResult()[Linvoke_review_identity;",
            Value.Unknown(VirtualType.of("[Linvoke_review_identity;")),
        )
        CoreTester.assertReturns(
            REVIEW,
            "callOpaqueClassResult()Linvoke_review_identity;",
            Value.Unknown(VirtualType.of("Linvoke_review_identity;")),
        )
    }

    @Test
    fun incompleteDispatchWorldDegradesOneCandidateWithoutSampleSpecificPolicy(@TempDir root: Path) {
        Files.writeString(root.resolve("orphan.smali"), ORPHAN_SOURCE.trimIndent())
        val index = task53ClassIndex(root, INCOMPLETE_WORLD_SOURCE)
        val view = index.requireMethodView(INCOMPLETE_WORLD, "incompleteWorld(Lincomplete/base;)I")
        val graph = VmServices(index).newSession(view).run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        val invoke = graph.addresses().flatMap { graph.nodePile(it) }.single { it.dispatchCandidates.isNotEmpty() }
        assertEquals(setOf("Lincomplete/base;->value()I"), invoke.dispatchCandidates)
    }

    @Test
    fun knownThrowableSelectsOnlyItsFirstMatchingHandler() {
        CoreTester.assertReturns(REVIEW, "nestedTwoHandlers()I", Value.Known(1, int))
    }

    @Test
    fun normalConstructorAbortFallbackClearsOnlyTheExactReceiverId() {
        val graph = CoreTester.execute(
            REVIEW,
            "initializeOneOfTwoThroughAbortFallback()Linvoke_abort_ctor;",
            limits = Limits(maxAddressVisits = 3, maxSteps = 100),
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val receiver = graph.explored.terminatingConsensus(0) as Value.Known
        assertEquals(
            receiver,
            graph.explored.terminatingConsensus(1),
            "the receiver alias keeps the exact HeapRef",
        )
        val initialized = graph.explored.terminatingHeapObject(receiver.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertFalse(initialized.uninitialized, "normal opaque fallback completes this constructor call")

        val unrelated = graph.explored.terminatingConsensus(2) as Value.Known
        val unrelatedObject =
            graph.explored.terminatingHeapObject(unrelated.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertTrue(unrelatedObject.uninitialized, "an unrelated same-type object is not the constructor receiver")
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    /**
     * The outer constructor's guest super.<init> returns, its own base Object.<init> included, and then the outer
     * constructor throws. Construction never completed (JLS 12.5: an object is initialized when its constructor
     * completes normally), so the leaked receiver stays uninitialized whatever the inner calls returned.
     */
    @Test
    fun aSuperConstructorReturningInsideAThrowingOuterConstructorDoesNotCompleteTheReceiver() {
        val graph = CoreTester.execute(REVIEW, "leakFailedConstruction()Linvoke_ctor_outer;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val receiver = graph.explored.terminatingHeapObject(returned.raw as HeapRef).agreed() as HeapObject.TrackedObject

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(VirtualType.of("Linvoke_ctor_outer;"), receiver.type)
        assertTrue(receiver.uninitialized, "only the outer constructor's normal completion initializes its receiver")
    }

    @Test
    fun ambiguousDispatchCandidatesAreOrderedCopiedAndImmutable() {
        val lookup = CoreTester.classIndex.currentResolver().resolveInvocation(
            MethodSignature(VirtualType.of("Linvoke_dispatch_base;"), "value", "()I"),
            InvokeKind.VIRTUAL,
            VirtualType.of(REVIEW),
            receiverType = null,
        ) as InvocationLookup.Ambiguous
        assertEquals(
            listOf("Linvoke_dispatch_base;->value()I", "Linvoke_dispatch_child;->value()I"),
            lookup.candidates.toList(),
        )
        assertFalse(lookup.candidates.javaClass.name.contains("LinkedHashMap"), lookup.candidates.javaClass.name)
        @Suppress("UNCHECKED_CAST")
        val mutable = lookup.candidates as MutableSet<String>
        assertThrows(UnsupportedOperationException::class.java) { mutable.add("Lleak;->value()I") }
    }

    private fun view(owner: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(owner, descriptor)

    private companion object {
        const val REVIEW = "Linvoke_review;"
        const val INCOMPLETE_WORLD = "Lincomplete/base;"

        const val INCOMPLETE_WORLD_SOURCE = """
            .class public Lincomplete/base;
            .super Ljava/lang/Object;

            .method public value()I
                .locals 1
                const/4 v0, 0x7
                return v0
            .end method

            .method public static incompleteWorld(Lincomplete/base;)I
                .locals 0
                invoke-virtual {p0}, Lincomplete/base;->value()I
                move-result p0
                return p0
            .end method
        """

        /** Its superclass is absent, so the program's hierarchy is incomplete. */
        const val ORPHAN_SOURCE = """
            .class public Lincomplete/orphan;
            .super Lincomplete/absent;
        """
    }
}
