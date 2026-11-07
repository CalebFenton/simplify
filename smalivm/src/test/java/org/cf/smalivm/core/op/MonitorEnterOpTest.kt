package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MonitorEnterOpTest {
    @Test
    fun aKnownNonNullReceiverContinuesWithAWeakEffectAndOnlyReadsItsOperand() {
        val graph = execute(Value.Known("receiver", OBJECT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.getOp(0) is MonitorEnterOp)
        assertTrue(graph.wasReached(1), "the feasible monitor operation must continue")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.WEAK, graph.sideEffectLevel(0))
        assertEquals(setOf(REGISTER), graph.registersRead(0))
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
    }

    @Test
    fun anUnknownReceiverPreservesTheContinueAndNullOutcomes() {
        val graph = CoreTester.execute(CLASS_NAME, METHOD)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(1), "unknown can be non-null, so continuation is feasible")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph), "unknown can also be null")
        assertEquals(1, graph.outcomes.count { it is Outcome.Returned })
        assertEquals(1, graph.outcomes.count { it is Outcome.Threw })
        assertEquals(SideEffectLevel.WEAK, graph.sideEffectLevel(0))
    }

    @Test
    fun aDefiniteNullThrowsWithoutContinuingOrRecordingTheMonitorEffect() {
        val graph = execute(Value.Known(null, OBJECT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "a definite null has no feasible continuation")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(0))
    }

    @Test
    fun aKnownNonZeroPrimitiveUsesTheVerifierTierNullReadChoice() {
        val graph = execute(Value.Known(5, INT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "Known(5, I) reads as a definite null reference in this engine")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(0))
    }

    @Test
    fun aKnownIntegralZeroUsesTheVerifierValidTwoTierNullRow() {
        val graph = execute(Value.Known(0, INT))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(1), "Known(0, I) has no sibling continuation")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(0))
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
    }

    @Test
    fun anEnterExceptionSearchesAtTheMonitorInstructionAndEscapesTheFollowingTry() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "enterBeforeSingleInstructionTry(Ljava/lang/Object;)I",
            mapOf(REGISTER to Value.Known(null, OBJECT)),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(0, graph.outcomes.count { it is Outcome.Returned })
        assertEquals(1, graph.outcomes.count { it is Outcome.Threw })
        assertFalse(graph.wasReached(1), "the exceptional arm must not execute the normal sibling")
        assertFalse(graph.wasReached(3), "the following one-instruction try does not cover monitor-enter")
    }

    private fun execute(receiver: Value) = CoreTester.execute(CLASS_NAME, METHOD, mapOf(REGISTER to receiver))

    private companion object {
        const val CLASS_NAME = "Lmonitor_test;"
        const val METHOD = "enter(Ljava/lang/Object;)V"
        const val REGISTER = 3
        const val NPE = "Ljava/lang/NullPointerException;"
        val OBJECT = VirtualType.of("Ljava/lang/Object;")
        val INT = VirtualType.of("I")
    }
}
