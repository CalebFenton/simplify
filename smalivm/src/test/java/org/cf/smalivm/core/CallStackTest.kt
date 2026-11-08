package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The [CallStack] and the bound on it. Ancestry follows `Activation.caller` by identity, and a self-recursive
 * guest method must end in `AbortReason.CALL_DEPTH` rather than hang or overflow the host stack.
 *
 * `Limits.maxCallDepth` counts frames and is checked only at a callee's entry; there is no recursion detection
 * beyond depth.
 */
class CallStackTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun callerSession(entryDescriptor: String, limits: Limits = Limits()): ExecutionSession =
        CoreTester.vmServices.newSession(viewOf(CALLER, entryDescriptor), limits)

    @Test
    fun theCallStackDepthRisesInsideTheCalleeAndFallsWhenItReturns() {
        // `callsCalleeAndReturnsItsResult()I` is invoke @0, move-result @3, return @4;
        // `returnsSeven()I` is const @0, return @1. So the walk is
        // caller@0 (depth 0) -> callee@0, callee@1 (depth 1) -> caller@3, caller@4 (depth 0 again).
        val session = callerSession("callsCalleeAndReturnsItsResult()I")
        assertEquals(0, session.callStack.depth, "before anything runs, the stack is the entry activation alone")
        assertEquals(CALLER_RETURNS_RESULT, session.callStack.top.view.signature.toString())

        val walk = mutableListOf<Triple<String, Int, Int>>()
        while (true) {
            val node = session.step() ?: break
            walk.add(Triple(node.view.signature.nameAndProto, node.address, session.callStack.depth))
            assertEquals(node.callDepth, session.callStack.depth, "the session's stack is the current node's")
        }

        assertEquals(
            listOf(
                Triple("callsCalleeAndReturnsItsResult()I", 0, 0),
                Triple("returnsSeven()I", 0, 1),
                Triple("returnsSeven()I", 1, 1),
                Triple("callsCalleeAndReturnsItsResult()I", 3, 0),
                Triple("callsCalleeAndReturnsItsResult()I", 4, 0),
            ),
            walk,
            "depth rises entering the callee and FALLS on return with activation ancestry",
        )
        assertEquals(CompletionStatus.Complete, session.completion())
    }

    @Test
    fun aCalleesActivationNamesItsCallerAndTheCallerIsSharedByEveryNodeOfTheCall() {
        // The call edge is `Activation.caller`, not `ExecutionNode.parent`: one edge for both control flow and
        // call ancestry would let a caller's handler inherit a callee's locals. There is one activation per call,
        // not per node, because the pile and the visit budget hang off it.
        val session = callerSession("callsCalleeAndReturnsItsResult()I")
        val nodes = mutableListOf<ExecutionNode>()
        while (true) nodes.add(session.step() ?: break)

        val callerNodes = nodes.filter { it.callDepth == 0 }
        val calleeNodes = nodes.filter { it.callDepth == 1 }
        assertEquals(3, callerNodes.size)
        assertEquals(2, calleeNodes.size)
        assertSame(
            calleeNodes[0].activation, calleeNodes[1].activation,
            "both of the callee's nodes belong to ONE activation",
        )
        assertSame(
            callerNodes[0].activation, callerNodes[2].activation,
            "and the resumed caller node is back in the caller's ORIGINAL activation, not a new one",
        )
        assertSame(callerNodes[0].activation, calleeNodes[0].activation.caller)
        val stack = CallStack.of(calleeNodes[0].activation)
        assertEquals(
            listOf(CALLEE_RETURNS_SEVEN, CALLER_RETURNS_RESULT), stack.frames.map { it.view.signature.toString() },
            "innermost activation first, the entry activation last",
        )
        assertEquals(1, stack.depth, "depth is frames.size - 1, so it matches ExecutionNode.callDepth")
    }

    @Test
    fun aSelfRecursiveCalleeReportsCallDepthInsteadOfHangingOrOverflowingTheHostStack() {
        // At the default limit: a fixture that only terminates under a hand-tightened bound would still take a
        // real run down.
        val session = recursiveSession(Limits())
        val graph = session.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.CALL_DEPTH)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(VirtualType.of("I")))), graph.outcomes)
    }

    @Test
    fun theCallDepthBoundCountsFramesNotDepth() {
        // `maxCallDepth = 3` permits three frames. `Activation.depth` is 0-based, so comparing it against the
        // bound would permit four; `Activation.frameCount` is what the bound compares.
        val session = recursiveSession(Limits(maxCallDepth = 3, maxSteps = 500))
        while (session.step()?.outcome != Outcome.Aborted(AbortReason.CALL_DEPTH)) Unit
        assertEquals(
            4, session.callStack.frames.size,
            "the bound stopped the fourth frame; three frames were allowed to run",
        )
        assertEquals(4, session.callStack.top.frameCount, "and frameCount is what the bound compared")
        assertEquals(3, session.callStack.depth, "the 0-based depth of that fourth frame is 3")
        assertEquals(
            Outcome.Aborted(AbortReason.CALL_DEPTH), session.currentNode!!.outcome,
            "the abort is stamped on the callee's entry node, where the bound is checked",
        )
        assertTrue(session.currentNode!!.isCallEntry, "checked at a call's ENTRY, not at every step")
        val graph = session.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.CALL_DEPTH)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(Value.Unknown(int), graph.explored.terminatingConsensus(0))
    }

    @Test
    fun aResumedCallerNodeIsNotACallEntry() {
        // "My activation differs from my parent's" is also true of a resumed caller node, whose parent is the
        // callee's returning node; `isCallEntry` must not call a return a call entry.
        val session = callerSession("callsCalleeAndReturnsItsResult()I")
        val nodes = mutableListOf<ExecutionNode>()
        while (true) nodes.add(session.step() ?: break)
        assertEquals(
            listOf(0 to 0, 1 to 0),
            nodes.filter { it.isCallEntry }.map { it.callDepth to it.address },
            "exactly TWO call entries -- the run's own entry node (depth 0) and the callee's (depth 1), " +
                "each at its method's address 0 -- and neither of them the resume",
        )
        val resumed = nodes.single { it.callDepth == 0 && it.parent?.callDepth == 1 }
        assertFalse(resumed.isCallEntry, "the resumed caller node is a RETURN, not a call entry")
    }

    @Test
    fun aZeroCallDepthBoundIsOptedOutOfLikeEveryOtherLimit() {
        // `Limits` contract: every limit is opted out with 0. maxSteps is what stops the recursion here, which
        // also shows it is a backstop and not the mechanism the test above measures.
        val session = recursiveSession(Limits(maxCallDepth = 0, maxSteps = 40))
        val graph = session.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), graph.completion())
        assertTrue(
            session.callStack.depth > 3,
            "with the bound off, recursion ran until the STEP limit stopped it: depth ${session.callStack.depth}",
        )
    }

    @Test
    fun theEntryActivationsReturnStillEndsAPathOfTheRun() {
        // `planReturn` decides by `Activation.returnSite`, not by comparing methods: a self-recursive method's
        // innermost `return` and the entry activation's `return` are the same instruction. This is the no-call
        // control: one activation, so the return ends the run's path.
        val session = CoreTester.vmServices.newSession(viewOf("Lspine/spine;", "returnFive()I"))
        session.run()
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), session.outcomes)
        assertEquals(0, session.callStack.depth)
        assertEquals(
            listOf("Lspine/spine;->returnFive()I"), session.callStack.frames.map { it.view.signature.toString() },
            "a run with no call has a one-frame stack, not an empty one",
        )
    }

    /**
     * A session on `callsRecursiveCallee()I`, whose callee calls itself. The recursion is one frame down, not in
     * the entry method, because the bound is about the call stack rather than the method the run started in.
     */
    private fun recursiveSession(limits: Limits): ExecutionSession = callerSession("callsRecursiveCallee()I", limits)

    private companion object {
        const val CALLER = "Lcalls/caller;"
        const val CALLER_RETURNS_RESULT = "Lcalls/caller;->callsCalleeAndReturnsItsResult()I"
        const val CALLEE_RETURNS_SEVEN = "Lcalls/callee;->returnsSeven()I"
    }
}
