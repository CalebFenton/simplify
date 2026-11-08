package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/** `monitor-enter` and `monitor-exit`; the semantics are DALVIK-SEMANTICS.md's `monitor-enter` / `monitor-exit`. */
class MonitorOpTest {
    private enum class Monitor(val method: String, val register: Int, val opClass: Class<out MonitorOp>) {
        ENTER("enter(Ljava/lang/Object;)V", 3, MonitorEnterOp::class.java),
        EXIT("exit(Ljava/lang/Object;)V", 2, MonitorExitOp::class.java),
    }

    @TestFactory
    fun aKnownNonNullReceiverContinuesWithAWeakEffectAndOnlyReadsItsOperand(): List<DynamicTest> =
        Monitor.entries.map { monitor ->
            DynamicTest.dynamicTest(monitor.name) {
                val graph = execute(monitor, Value.Known("receiver", OBJECT))

                assertEquals(CompletionStatus.Complete, graph.completion())
                assertTrue(monitor.opClass.isInstance(graph.getOp(0)), "decoded as ${graph.getOp(0)}")
                assertTrue(graph.wasReached(1), "the feasible monitor operation must continue")
                assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
                assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0))
                assertEquals(setOf(monitor.register), graph.registersRead(0))
                assertEquals(emptySet<Int>(), graph.registersAssigned(0))
            }
        }

    @TestFactory
    fun anUnknownReceiverKeepsTheContinueAndNullOutcomes(): List<DynamicTest> = Monitor.entries.map { monitor ->
        DynamicTest.dynamicTest(monitor.name) {
            val graph = CoreTester.execute(CLASS_NAME, monitor.method)

            assertEquals(CompletionStatus.Complete, graph.completion())
            assertTrue(graph.wasReached(1), "unknown can be non-null, so continuation is feasible")
            assertEquals(listOf(NPE), CoreTester.escapingTypes(graph), "unknown can also be null")
            assertEquals(1, graph.outcomes.count { it is Outcome.Returned })
            assertEquals(1, graph.outcomes.count { it is Outcome.Threw })
            assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0))
        }
    }

    /**
     * A register that reads as null throws, per DALVIK-SEMANTICS.md's "Reading a register as a reference":
     * null and `Known(0, I)` are null on ART, and `Known(5, I)` is the verifier-excluded case the engine also
     * reads as null. The message is ART's monitor text from `ThrowNullPointerExceptionFromDexPC`
     * (`common_throws.cc`).
     */
    @TestFactory
    fun aReceiverThatReadsAsNullThrowsWithoutContinuingOrRecordingTheMonitorEffect(): List<DynamicTest> =
        Monitor.entries.flatMap { monitor ->
            listOf(Value.Known(null, OBJECT), Value.Known(0, INT), Value.Known(5, INT)).map { receiver ->
                DynamicTest.dynamicTest("${monitor.name} $receiver") {
                    val graph = execute(monitor, receiver)

                    assertEquals(CompletionStatus.Complete, graph.completion())
                    assertFalse(graph.wasReached(1), "a null receiver has no feasible continuation")
                    assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
                    assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0))
                    assertEquals(emptySet<Int>(), graph.registersAssigned(0))
                    CoreTester.testThrows(
                        CLASS_NAME, monitor.method, CoreState().apply { setRegister(monitor.register, receiver) },
                        exceptionType = NPE,
                        message = "Attempt to do a synchronize operation on a null object",
                    )
                }
            }
        }

    @Test
    fun anEnterExceptionSearchesAtTheMonitorInstructionAndEscapesTheFollowingTry() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "enterBeforeSingleInstructionTry(Ljava/lang/Object;)I",
            mapOf(Monitor.ENTER.register to Value.Known(null, OBJECT)),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
        assertEquals(0, graph.outcomes.count { it is Outcome.Returned })
        assertEquals(1, graph.outcomes.count { it is Outcome.Threw })
        assertFalse(graph.wasReached(1), "the exceptional arm must not execute the normal sibling")
        assertFalse(graph.wasReached(3), "the following one-instruction try does not cover monitor-enter")
    }

    /** [BC] `monitor-exit`: an exception is thrown "as if the pc has already advanced past the instruction". */
    @Test
    fun anExitExceptionSearchesAtTheAdvancedPcAndIsCaughtByTheFollowingTry() {
        val graph = executeExit("exitBeforeSingleInstructionTry(Ljava/lang/Object;)I")

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
    fun anUnknownExitReceiverReturnsNormallyAndRoutesItsNpeAtTheAdvancedPc() {
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
    fun anExitAtTheTryRangeEndEscapesBecauseItsAdvancedPcIsOutsideTheRange() {
        val graph = executeExit("exitAtTryRangeEnd(Ljava/lang/Object;)I")

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
        val graph = executeExit("exitBeforePayload(Ljava/lang/Object;)I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
        assertFalse(graph.wasReached(2), "the payload is a handler-search pc, never an executed child")
        assertTrue(graph.wasReached(9), "the payload address must route to the following handler")
    }

    @Test
    fun aNestedCalleeExitReachesTheCallerCatchAllWithAConcreteNpe() {
        val graph = executeExit("callNestedExit(Ljava/lang/Object;)I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
        assertFalse(graph.wasReached(3), "the caller's normal sibling must not run after the callee throws")
        assertTrue(graph.wasReached(5), "the caller catch-all handles the callee's escaping NPE")
        val caught = graph.trusted()!!.terminatingConsensus(1) as Value.Known
        val ref = caught.raw as HeapRef
        assertEquals(VirtualType.of(NPE), caught.type)
        assertTrue(CoreTester.terminatingHostObject(graph, ref) is NullPointerException)
    }

    private fun assertSingleEscapingNpe(graph: ExecutionGraph) {
        assertEquals(CompletionStatus.Complete, graph.completion(), "the finished throw leaves no open path")
        val thrown = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(VirtualType.of(NPE), thrown.type)
        assertTrue(thrown.raw is HeapRef, "the guest NPE must retain a concrete dynamic ref")
        assertEquals(listOf(NPE), CoreTester.escapingTypes(graph))
    }

    private fun execute(monitor: Monitor, receiver: Value) =
        CoreTester.execute(CLASS_NAME, monitor.method, mapOf(monitor.register to receiver))

    /** A definite-null receiver in `p0`, which is `v2` in every three-register exit fixture. */
    private fun executeExit(method: String) =
        CoreTester.execute(CLASS_NAME, method, mapOf(Monitor.EXIT.register to Value.Known(null, OBJECT)))

    private companion object {
        const val CLASS_NAME = "Lmonitor_test;"
        const val NPE = "Ljava/lang/NullPointerException;"
        val OBJECT = VirtualType.of("Ljava/lang/Object;")
        val INT = VirtualType.of("I")
    }
}
