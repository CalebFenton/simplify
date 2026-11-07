package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
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
    }

    @Test
    fun schedulerWorkLimitCancelsPendingRoutingAndReleasesCollectorState() {
        val session = CoreTester.vmServices.newSession(
            view(),
            Limits(maxSteps = 4),
        )

        val graph = session.run()

        assertAbortedRouting(session, graph, AbortReason.METHOD_LIMIT)
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
                // The innermost callee's first ending completes its activation and queues its routing.
                if (session.reachedNodes().any { it.view.signature != view.signature && it.outcome != null }) 2 else 0
            },
        )

        val graph = session.run()

        assertAbortedRouting(session, graph, AbortReason.TIMEOUT)
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
                // Fire once the callee's return and throw paths have both ended while its loop path is open.
                val terminals = session.reachedNodes().filter { it.view.signature == CALLEE }.mapNotNull { it.outcome }
                if (terminals.any { it is Outcome.Returned } && terminals.any { it is Outcome.Threw }) 2 else 0
            },
        )

        val graph = session.run()

        assertPartialCollectionCancelled(session, graph, AbortReason.TIMEOUT)
    }

    @Test
    @Timeout(value = 30, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun methodLimitCancelsManyOpenActivationsInOnePass() {
        // Each recursion level is still open when the step limit fires, so cancellation must visit each
        // completion once rather than rescan every activation per open completion.
        val view = CoreTester.classIndex.requireMethodView("Lcalls/caller;", "callsRecursiveCallee()I")
        val session = CoreTester.vmServices.newSession(view, Limits(maxCallDepth = 0, maxSteps = DEEP_STEPS))

        val graph = session.run()

        assertEquals(DEEP_STEPS, session.callStack.depth, "every step entered one more open activation")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), graph.completion())
        assertEquals(listOf(Outcome.Aborted(AbortReason.METHOD_LIMIT)), graph.outcomes)
        assertTrue(session.isFinished)
    }

    private fun assertAbortedRouting(
        session: ExecutionSession,
        graph: ExecutionGraph,
        reason: AbortReason,
    ) {
        assertEquals(CompletionStatus.Partial(setOf(reason)), graph.completion())
        assertEquals(listOf(Outcome.Aborted(reason)), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        // Routing never ran: no caller resume node exists, and every reached node executed its op.
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
            it.view.signature == CALLEE && it.outcome != null
        }
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Returned })
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Threw })
        assertEquals(1, calleeTerminals.count { it.outcome == Outcome.Aborted(reason) })
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
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

    private fun ExecutionSession.reachedNodes(): List<ExecutionNode> {
        var root = currentNode ?: return emptyList()
        while (true) root = root.parent ?: break
        return allNodes(root)
    }

    private fun view(): MethodView = CoreTester.classIndex.requireMethodView(OWNER, "outerThreeFrames()I")

    private companion object {
        const val OWNER = "Lexception_propagation_test;"
        const val CALLEE = "$OWNER->partialCompletionCallee(I)I"
        const val DEEP_STEPS = 200_000
    }
}
