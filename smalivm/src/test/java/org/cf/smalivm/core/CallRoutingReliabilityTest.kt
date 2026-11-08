package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CallRoutingReliabilityTest {
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
    fun methodLimitCancelsCollectedReturnThrowAndOpenPathExactlyOnce() {
        val session = partialCompletionSession(Limits(maxAddressVisits = 0, maxSteps = 6))

        val graph = session.run()

        assertPartialCollectionCancelled(session, graph, AbortReason.METHOD_LIMIT)
    }

    @Test
    fun methodLimitCancelsManyOpenActivationsInOnePass() {
        // Each recursion level is still open when the step limit fires, so cancellation must visit each
        // completion once rather than rescan every activation per open completion. One pass costs about eight
        // times as much for eight times the open activations; a rescan per completion costs about sixty-four times.
        val view = CoreTester.classIndex.requireMethodView("Lcalls/caller;", "callsRecursiveCallee()I")
        fun deepSession(steps: Int) = CoreTester.vmServices.newSession(view, Limits(maxCallDepth = 0, maxSteps = steps))

        assertCostRatio(24.0, { deepSession(DEEP_STEPS) }, { deepSession(8 * DEEP_STEPS) }) { it.run() }

        val session = deepSession(8 * DEEP_STEPS)
        val graph = session.run()
        assertEquals(8 * DEEP_STEPS, session.callStack.depth, "every step entered one more open activation")
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
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        // Routing never ran: no caller resume node exists, and every reached node executed its op.
        assertTrue(session.isFinished)
        val nodes = allNodes(graph.nodePile(0).single())
        assertEquals(4, nodes.count { it.progress is OpProgress.Executed })
        assertTrue(nodes.none { it.progress == OpProgress.Pending }, "routing must not publish a debugger-visible fake node")
    }

    private fun assertPartialCollectionCancelled(
        session: ExecutionSession,
        graph: ExecutionGraph,
        reason: AbortReason,
    ) {
        assertEquals(CompletionStatus.Partial(setOf(reason)), graph.completion())
        assertEquals(List(3) { Outcome.Aborted(reason) }, graph.outcomes)
        val calleeTerminals = allNodes(graph.nodePile(0).single()).filter {
            it.view.signature.toString() == CALLEE && it.outcome != null
        }
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Returned })
        assertEquals(1, calleeTerminals.count { it.outcome is Outcome.Threw })
        assertEquals(1, calleeTerminals.count { it.outcome == Outcome.Aborted(reason) })
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        assertTrue(session.isFinished)
    }

    private fun partialCompletionSession(limits: Limits): ExecutionSession {
        val caller = CoreTester.classIndex.requireMethodView(OWNER, "partialCompletionCaller(I)I")
        val callee = CoreTester.classIndex.requireMethodView(OWNER, "partialCompletionCallee(I)I")
        val returnAddress = callee.addresses.first { callee.opcodeAt(it).name.startsWith("const") }
        val throwAddress = callee.addresses.filter { callee.opcodeAt(it).name.startsWith("const") }[1]
        val loopAddress = callee.addresses.single { callee.opcodeAt(it).name.startsWith("goto") }
        // No real opcode forks three ways by jumping, so the callee's entry op is scripted.
        val fork = object : Op(0) {
            override val name = "return-throw-or-loop"

            override fun execute(frame: Frame): OpExecution = OpExecution.of(
                OpArm.Jump(returnAddress),
                OpArm.Jump(throwAddress),
                OpArm.Jump(loopAddress),
            )
        }
        return CoreTester.vmServices.newSession(caller, limits, null) { address, current, resolver ->
            if (current.signature == callee.signature && address == 0) fork
            else OpBuilder.build(address, current, resolver)
        }
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
        const val CALLEE = "$OWNER->partialCompletionCallee(I)I"
        const val DEEP_STEPS = 5_000
    }
}
