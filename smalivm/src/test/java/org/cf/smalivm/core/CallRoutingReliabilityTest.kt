package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CallRoutingReliabilityTest {
    @Test
    fun schedulerInvariantFailureDuringRoutingEscapesLoudly() {
        val view = view()
        val session = ExecutionSession(
            methodView = view,
            vmServices = CoreTester.vmServices,
            limits = Limits(),
            entryFrame = Frame.entryFrame(view),
            routingInvariant = { error("injected routing invariant") },
        )

        val failure = assertThrows<IllegalStateException> { session.run() }

        assertEquals("injected routing invariant", failure.message)
        assertTrue(session.outcomes.none { it == Outcome.Aborted(AbortReason.HOST_FAILURE) })
        assertEquals(0, session.callPropagationMetrics().retainedFrames)
        assertEquals(0, session.callPropagationMetrics().retainedCollectionCapacity)
        assertEquals(0, session.callPropagationMetrics().pendingRoutingCommands)
    }

    @Test
    fun schedulerWorkLimitCancelsPendingRoutingAndReleasesCollectorState() {
        val session = CoreTester.vmServices.newSession(
            view(),
            Limits(maxSteps = 4),
        )

        val graph = session.run()

        assertAbortedRouting(session, graph, AbortReason.METHOD_LIMIT)
        assertEquals(1, session.callPropagationMetrics().routingWorkChecks)
        assertEquals(0, session.callPropagationMetrics().routingCommands)
        assertEquals(5, session.callPropagationMetrics().schedulerWork)
    }

    @Test
    fun timeoutAtPendingRoutingCancelsEveryCompletionBeforeMergeOrResolution() {
        lateinit var session: ExecutionSession
        val view = view()
        session = ExecutionSession(
            methodView = view,
            vmServices = CoreTester.vmServices,
            limits = Limits(maxExecutionTimeMillis = 1),
            entryFrame = Frame.entryFrame(view),
            clockMillis = {
                if (session.callPropagationMetrics().pendingRoutingCommands > 0) 2 else 0
            },
        )

        val graph = session.run()

        assertAbortedRouting(session, graph, AbortReason.TIMEOUT)
        assertEquals(1, session.callPropagationMetrics().routingWorkChecks)
        assertEquals(0, session.callPropagationMetrics().routingCommands)
    }

    @Test
    fun methodLimitCancelsCollectedReturnThrowAndOpenPathExactlyOnce() {
        val session = partialCompletionSession(Limits(maxAddressVisits = 0, maxSteps = 6))

        val graph = session.run()

        assertPartialCollectionCancelled(session, graph, AbortReason.METHOD_LIMIT)
    }

    @Test
    fun timeoutCancelsCollectedReturnThrowAndOpenPathExactlyOnce() {
        lateinit var session: ExecutionSession
        session = partialCompletionSession(
            limits = Limits(maxAddressVisits = 0, maxExecutionTimeMillis = 1),
            clockMillis = {
                if (session.callPropagationMetrics().retainedFrames == 2) 2 else 0
            },
        )

        val graph = session.run()

        assertPartialCollectionCancelled(session, graph, AbortReason.TIMEOUT)
    }

    private fun assertAbortedRouting(
        session: ExecutionSession,
        graph: ExecutionGraph,
        reason: AbortReason,
    ) {
        assertEquals(CompletionStatus.Partial(setOf(reason)), graph.completion())
        assertEquals(listOf(Outcome.Aborted(reason)), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        val metrics = session.callPropagationMetrics()
        assertEquals(0, metrics.openPaths)
        assertEquals(0, metrics.retainedFrames)
        assertEquals(0, metrics.retainedCollectionCapacity)
        assertEquals(0, metrics.pendingRoutingCommands)
        assertEquals(0, metrics.partitionMerges)
        assertEquals(0, metrics.handlerResolutions)
        assertTrue(session.isFinished)
        val nodes = allNodes(graph.nodePile(0).single())
        assertEquals(4, nodes.count { it.opExecutionStarted })
        assertFalse(nodes.any { !it.opExecutionStarted }, "routing must not publish a debugger-visible fake node")
    }

    private fun assertPartialCollectionCancelled(
        session: ExecutionSession,
        graph: ExecutionGraph,
        reason: AbortReason,
    ) {
        assertEquals(CompletionStatus.Partial(setOf(reason)), graph.completion())
        assertEquals(List(3) { Outcome.Aborted(reason) }, graph.outcomes)
        val calleeTerminals = allNodes(graph.nodePile(0).single()).filter {
            it.view.signature == "$OWNER->partialCompletionCallee(I)I" && it.outcome != null
        }
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Returned })
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Threw })
        assertEquals(1, calleeTerminals.count { it.outcome == Outcome.Aborted(reason) })
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        val metrics = session.callPropagationMetrics()
        assertEquals(0, metrics.openPaths)
        assertEquals(0, metrics.retainedFrames)
        assertEquals(0, metrics.retainedCollectionCapacity)
        assertEquals(0, metrics.pendingRoutingCommands)
        assertTrue(session.isFinished)
    }

    private fun partialCompletionSession(
        limits: Limits,
        clockMillis: () -> Long = System::currentTimeMillis,
    ): ExecutionSession {
        val caller = CoreTester.classIndex.requireMethodView(OWNER, "partialCompletionCaller(I)I")
        val callee = CoreTester.classIndex.requireMethodView(OWNER, "partialCompletionCallee(I)I")
        val returnAddress = callee.addresses.first { callee.opcodeAt(it).name.startsWith("const") }
        val throwAddress = callee.addresses.filter { callee.opcodeAt(it).name.startsWith("const") }[1]
        val loopAddress = callee.addresses.single { callee.opcodeAt(it).name.startsWith("goto") }
        return ExecutionSession(
            methodView = caller,
            vmServices = CoreTester.vmServices,
            limits = limits,
            entryFrame = Frame.entryFrame(caller),
            opFactory = { address, currentView, index ->
                if (currentView.signature == callee.signature && address == 0) {
                    object : Op(address) {
                        override val name = "return-throw-or-loop"

                        override fun execute(frame: Frame): OpExecution = listOf(
                            Transition.Jump(returnAddress),
                            Transition.Jump(throwAddress),
                            Transition.Jump(loopAddress),
                        ).asOpExecution()
                    }
                } else {
                    OpBuilder.build(address, currentView, index)
                }
            },
            clockMillis = clockMillis,
        )
    }

    private fun allNodes(root: ExecutionNode): List<ExecutionNode> = buildList {
        fun visit(node: ExecutionNode) {
            add(node)
            node.children.forEach(::visit)
        }
        visit(root)
    }

    private fun view(): MethodView = CoreTester.classIndex.requireMethodView(OWNER, "outerThreeFrames()I")

    private companion object {
        const val OWNER = "Lexception_propagation_test;"
    }
}
