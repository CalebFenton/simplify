package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvokeCallerStateTest {
    private val int = VirtualType.of("I")

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
    fun knownThrowableSelectsOnlyItsFirstMatchingHandler() {
        CoreTester.assertReturns(REVIEW, "nestedTwoHandlers()I", Value.Known(1, int))
    }

    @Test
    fun aSuperConstructorReturningInsideAThrowingOuterConstructorDoesNotCompleteTheReceiver() {
        val graph = CoreTester.execute(REVIEW, "leakFailedConstruction()Linvoke_ctor_outer;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val receiver = graph.explored.terminatingHeapObject(returned.raw as HeapRef).agreed() as HeapObject.TrackedObject

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(VirtualType.of("Linvoke_ctor_outer;"), receiver.type)
        assertTrue(receiver.uninitialized, "only the outer constructor's normal completion initializes its receiver")
    }

    private companion object {
        const val REVIEW = "Linvoke_review;"
    }
}
