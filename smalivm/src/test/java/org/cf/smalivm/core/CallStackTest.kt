package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The [CallStack] and the bound on it.
 *
 * Bug classes:
 *  (a) **No call stack at all.** Before invocation there was nothing to have one, so `ExecutionNode.callDepth`
 *      was a documented stub that was always 0 and activation ancestry did not exist. Plan 10 step-over walks
 *      `Activation.caller` by identity. The rising and falling depth here verifies the same ancestry lifecycle.
 *  (b) **Unbounded recursion.** Task 14 introduces the first nested call and therefore the first infinite
 *      recursion: a self-recursive guest method either hangs the suite or overflows the host stack unless
 *      `Limits.maxCallDepth` reports it as a truncation instead. Plan resolved decision 10; the reserved
 *      `AbortReason.CALL_DEPTH` existed with no production reference at all until this task.
 *
 * Oracle: the old engine's `DEFAULT_MAX_CALL_DEPTH = 20`, checked at a callee's entrypoint as
 * `current.callDepth > maxCallDepth` (`VirtualMachine.kt`), corroborated by `execution-engine.md:171-180`
 * whose row reads *"Checked only at entry. Exceeding the limit clears the current frame"*. It has no
 * recursion detection beyond depth, and neither does this.
 *
 * These scheduler-isolation tests still use `opFactory`. HISTORICAL: this was required before Task 20;
 * invoke decoding is now supported and its capability fixtures are in `newCoreSupported`.
 */
class CallStackTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun invokeAddressIn(view: MethodView): Int =
        view.addresses.single { view.opcodeAt(it) == Opcode.INVOKE_STATIC }

    /**
     * A session on `Lcalls/caller;->`[entryDescriptor] in which every method named as a key of [replaceIn]
     * has its own `invoke-static` replaced by a stub emitting a [Transition.Invoke] at the mapped target.
     *
     * A MAP rather than one address, because recursion needs two entries: `recursesForever()I` must have its
     * own invoke replaced as well, or the recursion stops after a single frame and the bound is never tested.
     */
    private fun sessionFor(
        entryDescriptor: String,
        limits: Limits,
        replaceIn: Map<String, MethodDescriptor>,
    ): ExecutionSession {
        val view = viewOf(CALLER, entryDescriptor)
        return ExecutionSession.of(view, classIndex, limits, Frame.entryFrame(view)) { address, v, index ->
            val target = replaceIn[v.signature]
            if (target != null && address == invokeAddressIn(v)) {
                object : Op(address) {
                    override val name = "invoke-static-stub"
                    override fun execute(frame: Frame): OpExecution =
                        listOf(Transition.Invoke(target, IntArray(0), InvokeKind.STATIC)).asOpExecution()
                }
            } else {
                OpBuilder.build(address, v, index)
            }
        }
    }

    private fun callerCallsCallee(
        entryDescriptor: String,
        calleeName: String,
        calleeProto: String,
        limits: Limits = Limits(),
    ): ExecutionSession = sessionFor(
        entryDescriptor, limits,
        replaceIn = mapOf(
            "$CALLER->$entryDescriptor" to MethodDescriptor(VirtualType.of(CALLEE), calleeName, calleeProto),
        ),
    )

    @Test
    fun theCallStackDepthRisesInsideTheCalleeAndFallsWhenItReturns() {
        // Bug class (a). `callsCalleeAndReturnsItsResult()I` is invoke @0, move-result @3, return @4;
        // `returnsSeven()I` is const @0, return @1. So the walk is
        // caller@0 (depth 0) -> callee@0, callee@1 (depth 1) -> caller@3, caller@4 (depth 0 again).
        val session = callerCallsCallee("callsCalleeAndReturnsItsResult()I", "returnsSeven", "()I")
        assertEquals(0, session.callStack.depth, "before anything runs, the stack is the entry activation alone")
        assertEquals(CALLER_RETURNS_RESULT, session.callStack.top.view.signature)

        val walk = mutableListOf<Triple<String, Int, Int>>()
        while (true) {
            val node = session.step() ?: break
            walk.add(Triple(node.view.signature.substringAfter("->"), node.address, session.callStack.depth))
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
        // The call edge is `Activation.caller`, deliberately NOT `ExecutionNode.parent`: the old engine's
        // single parent edge encoded control flow AND call ancestry at once, which is how a caller's catch
        // handler ended up inheriting a callee's locals. And one activation per CALL, not per node: the pile
        // and the visit budget hang off it, so two nodes of one call must share the object.
        val session = callerCallsCallee("callsCalleeAndReturnsItsResult()I", "returnsSeven", "()I")
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
            listOf(CALLEE_RETURNS_SEVEN, CALLER_RETURNS_RESULT), stack.frames.map { it.view.signature },
            "innermost activation first, the entry activation last",
        )
        assertEquals(1, stack.depth, "depth is frames.size - 1, so it matches ExecutionNode.callDepth")
    }

    @Test
    fun aSelfRecursiveCalleeReportsCallDepthInsteadOfHangingOrOverflowingTheHostStack() {
        // Bug class (b), at the DEFAULT limit -- which is the assertion that matters, because a fixture that
        // only terminates under a hand-tightened bound would still take a real run down.
        val session = recursiveSession(Limits())
        val graph = session.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.CALL_DEPTH)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(VirtualType.of("I")))), graph.outcomes)
    }

    @Test
    fun theCallDepthBoundPermitsExactlyTheOraclesNumberOfFrames() {
        // The off-by-one, pinned the way `addressVisitLimitPermitsExactlyItsBudget` pins the other bound --
        // and it is a FRAME COUNT, not a depth. The old engine's `callDepth` is 1-based (its top-level node is
        // its own `isEntrypoint`, `ExecutionNode.kt:67-78`; `execution-engine.md:180`: "top-level depth is
        // one") and it compares `> maxCallDepth`, so `maxCallDepth = 3` permits THREE frames there.
        // `Activation.depth` is 0-based, so comparing that against the same bound would have permitted four --
        // one frame more than the oracle, which Task 22's differential compares. `Activation.frameCount` is
        // the value with the oracle's origin.
        val session = recursiveSession(Limits(maxCallDepth = 3, maxSteps = 500))
        while (session.step()?.outcome != Outcome.Aborted(AbortReason.CALL_DEPTH)) Unit
        assertEquals(
            4, session.callStack.frames.size,
            "the bound stopped the FOURTH frame; three frames -- the oracle's three -- were allowed to run",
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
        assertEquals(Value.Unknown(int), graph.terminatingConsensusOverExploredPaths(0))
    }

    @Test
    fun aResumedCallerNodeIsNotACallEntry() {
        // `isCallEntry` used to read "my activation differs from my parent's", which is also true of a
        // caller-RESUME node -- its parent is the callee's returning node -- so the predicate called a return
        // a call entry while the name asserted otherwise. Harmless for the depth bound (a resume node's frame
        // count is lower than the one already checked on the way in), but the test above asserts this property
        // as though it meant "call entry", so it had better mean that.
        val session = callerCallsCallee("callsCalleeAndReturnsItsResult()I", "returnsSeven", "()I")
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
        // `Limits`'s own contract is "all are opt-out with 0", and a bound that silently kept applying at 0
        // would be the one limit that broke it. Kept terminating by maxSteps, which is what actually stops
        // the recursion here -- so this also demonstrates that maxSteps is a backstop and not the mechanism
        // the test above measures.
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
        // The half `planReturn` decides by looking at `Activation.returnSite`, and the reason it cannot
        // decide by comparing methods: a self-recursive method's innermost `return` and the entry
        // activation's `return` are the same instruction in the same method. This is the no-call control --
        // one activation, so the return ends the run's path exactly as it always did.
        val view = viewOf("Lspine/spine;", "returnFive()I")
        val session = ExecutionSession.of(view, classIndex, Limits(), Frame.entryFrame(view))
        session.run()
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), session.outcomes)
        assertEquals(0, session.callStack.depth)
        assertEquals(
            listOf("Lspine/spine;->returnFive()I"), session.callStack.frames.map { it.view.signature },
            "a run with no call has a one-frame stack, not an empty one",
        )
    }

    /**
     * A session on `callsRecursiveCallee()I`, whose callee calls ITSELF. The recursion is deliberately one
     * frame down rather than in the entry method, because the bound is about the CALL STACK and not about
     * the method the run started in.
     */
    private fun recursiveSession(limits: Limits): ExecutionSession {
        val recurses = MethodDescriptor(VirtualType.of(CALLEE), "recursesForever", "()I")
        return sessionFor(
            "callsRecursiveCallee()I", limits,
            replaceIn = mapOf(
                "$CALLER->callsRecursiveCallee()I" to recurses,
                "$CALLEE->recursesForever()I" to recurses,
            ),
        )
    }

    private companion object {
        const val CALLER = "Lcalls/caller;"
        const val CALLEE = "Lcalls/callee;"
        const val CALLER_RETURNS_RESULT = "Lcalls/caller;->callsCalleeAndReturnsItsResult()I"
        const val CALLEE_RETURNS_SEVEN = "Lcalls/callee;->returnsSeven()I"
    }
}
