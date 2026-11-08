package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Monitor-exit behavior through completion, outcomes, handler sentinels, effects, and op metadata. */
class MonitorExitOpTest {
    @Test
    fun aKnownNonNullReceiverContinuesWithAWeakEffectAndOnlyReadsItsOperand() {
        val graph = execute(Value.Known("receiver", OBJECT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.getOp(0) is MonitorExitOp, "monitor-exit must not decode as MonitorEnterOp")
        assertTrue(graph.wasReached(1), "the feasible monitor operation must continue")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0))
        assertEquals(setOf(REGISTER), graph.registersRead(0))
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
    }

    @Test
    fun anUnknownReceiverReturnsNormallyAndRoutesItsNpeAtTheAdvancedPc() {
        val graph = CoreTester.execute(CLASS_NAME, "exitBeforeSingleInstructionTry(Ljava/lang/Object;)I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "the feasible NPE is caught")
        assertEquals(
            setOf(Outcome.Returned(Value.Known(1, INT)), Outcome.Returned(Value.Known(2, INT))),
            graph.outcomes.toSet(),
            "exactly one normal return and one caught-NPE return must survive",
        )
        assertEquals(2, graph.outcomes.size)
        assertTrue(graph.wasReached(1), "unknown can be non-null, so normal continuation is feasible")
        assertTrue(graph.wasReached(3), "the unknown null arm searches at the advanced pc and enters the handler")
        assertEquals(SideEffectLevel.WEAK, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun aDefiniteNullThrowsWithoutContinuingOrRecordingTheMonitorEffect() {
        val graph = execute(Value.Known(null, OBJECT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "a definite null has no feasible continuation")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0))
    }

    @Test
    fun aKnownNonZeroPrimitiveUsesTheVerifierTierNullReadChoice() {
        val graph = execute(Value.Known(5, INT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "Known(5, I) reads as a definite null reference in this engine")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0))
    }

    @Test
    fun aKnownIntegralZeroUsesTheVerifierValidTwoTierNullRow() {
        val graph = execute(Value.Known(0, INT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "Known(0, I) has no sibling continuation")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0))
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
    }

    @Test
    fun anExitExceptionSearchesAtTheAdvancedPcAndIsCaughtByTheFollowingTry() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "exitBeforeSingleInstructionTry(Ljava/lang/Object;)I",
            mapOf(REGISTER to Value.Known(null, OBJECT)),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(1, graph.outcomes.count { it is Outcome.Returned })
        assertEquals(0, graph.outcomes.count { it is Outcome.Threw })
        assertFalse(graph.wasReached(1), "handler search at the advanced pc must not execute that instruction")
        assertTrue(graph.wasReached(3), "the following one-instruction try catches monitor-exit's NPE")
        val caught = graph.trusted()!!.consensus(3, 0)
        assertTrue(caught is Value.Known, "move-exception must receive the thrown state: $caught")
        assertEquals(NPE, (caught as Value.Known).type.descriptor)
        assertEquals(Value.Known(2, INT), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun anExitAtTheTryRangeEndEscapesBecauseItsAdvancedPcIsOutsideTheRange() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "exitAtTryRangeEnd(Ljava/lang/Object;)I",
            mapOf(REGISTER to Value.Known(null, OBJECT)),
        )

        assertSingleEscapingNpe(graph)
        assertFalse(graph.wasReached(2), "the exceptional path must not execute the normal return")
        assertFalse(graph.wasReached(3), "the advanced pc equals the try end, so its handler is out of range")
    }

    @Test
    fun aTerminalExitWithoutAHandlerEscapesCleanlyInsteadOfFallingOffTheMethod() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "terminalExitWithoutHandler(Ljava/lang/Object;)V",
            mapOf(0 to Value.Known(null, OBJECT)),
        )

        assertSingleEscapingNpe(graph)
    }

    @Test
    fun anExitBeforeAPayloadUsesThePayloadAddressForHandlerSearchWithoutExecutingIt() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "exitBeforePayload(Ljava/lang/Object;)I",
            mapOf(REGISTER to Value.Known(null, OBJECT)),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
        assertFalse(graph.wasReached(2), "the payload is a handler-search pc, never an executed child")
        assertTrue(graph.wasReached(9), "the payload address must route to the following handler")
    }

    @Test
    fun aNestedCalleeExitReachesTheCallerCatchAllWithAConcreteNpe() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "callNestedExit(Ljava/lang/Object;)I",
            mapOf(REGISTER to Value.Known(null, OBJECT)),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
        assertFalse(graph.wasReached(3), "the caller's normal sibling must not run after the callee throws")
        assertTrue(graph.wasReached(5), "the caller catch-all handles the callee's escaping NPE")
        val caught = graph.trusted()!!.terminatingConsensus(1) as Value.Known
        val ref = caught.raw as HeapRef
        assertEquals(VirtualType.of(NPE), caught.type)
        assertTrue(graph.terminatingHostObjectOverExploredPaths(ref) is NullPointerException)
    }

    private fun assertSingleEscapingNpe(graph: org.cf.smalivm.core.ExecutionGraph) {
        assertEquals(CompletionStatus.Complete, graph.completion(), "the finished throw leaves no open path")
        val thrown = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(VirtualType.of(NPE), thrown.type)
        assertTrue(thrown.raw is HeapRef, "the guest NPE must retain a concrete dynamic ref")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
    }

    private fun execute(receiver: Value) = CoreTester.execute(CLASS_NAME, METHOD, mapOf(REGISTER to receiver))

    private companion object {
        const val CLASS_NAME = "Lmonitor_test;"
        const val METHOD = "exit(Ljava/lang/Object;)V"
        const val REGISTER = 2
        const val NPE = "Ljava/lang/NullPointerException;"
        val OBJECT = VirtualType.of("Ljava/lang/Object;")
        val INT = VirtualType.of("I")
    }
}
