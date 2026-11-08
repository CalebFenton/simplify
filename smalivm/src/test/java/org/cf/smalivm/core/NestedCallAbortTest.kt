package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NestedCallAbortTest {
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

    private fun view(owner: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(owner, descriptor)

    private companion object {
        const val REVIEW = "Linvoke_review;"
    }
}
