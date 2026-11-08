package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Bug classes: (a) per-run scheduler state shared across executions; (b) a truncated exploration
 * indistinguishable from a complete one; (c) an escaping host exception ending the whole run instead of
 * its path. Oracle: Dalvik control flow for the fixture methods; one Outcome per explored path, and any
 * abort makes completion Partial.
 */
class ExecutionSessionTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val vm = VmServices(classIndex)
    private val int = VirtualType.of("I")

    private fun session(descriptor: String, limits: Limits = Limits()) =
        vm.newSession(classIndex.requireMethodView("Lspine/spine;", descriptor), limits)

    private fun viewOf(descriptor: String) =
        classIndex.requireMethodView("Lspine/spine;", descriptor)

    @Test
    fun stepAdvancesOneOpAtATimeThenFinishes() {
        val s = session("returnFive()I")
        assertFalse(s.isFinished)
        val first = s.step()!!
        assertEquals(0, first.address)
        assertEquals(Value.Known(5, int), first.frame.peek(0), "a node's frame is the state AFTER its op ran")
        val second = s.step()!!
        assertEquals(1, second.address)
        assertNull(s.step(), "no more work")
        assertTrue(s.isFinished)
    }

    @Test
    fun returningPathProducesReturnedOutcome() {
        val s = session("returnFive()I")
        while (s.step() != null) Unit
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), s.outcomes)
    }

    @Test
    fun returnVoidProducesReturnedWithNoValue() {
        val s = session("returnNothing()V")
        while (s.step() != null) Unit
        assertEquals(listOf(Outcome.Returned(null)), s.outcomes)
    }

    @Test
    fun childFrameIsIndependentOfItsParent() {
        // Bug class: forked successors sharing one mutable state object.
        val s = session("moveChain()I")
        val first = s.step()!!          // const/16 v0, 0x9
        val second = s.step()!!         // move v1, v0
        assertNotSame(first.frame, second.frame, "each node owns its own frame")
        assertNull(first.frame.peek(1), "the parent must not see the child's write")
        assertEquals(Value.Known(9, int), second.frame.peek(1))
    }


    @Test
    fun gotoSkipsTheJumpedOverAddress() {
        val s = session("gotoSkip()I")
        val executed = mutableListOf<Int>()
        while (true) { executed.add((s.step() ?: break).address) }
        // const/4 v0,0x1 -> goto -> return. The skipped const/4 v0,0x2 must never execute.
        val view = s.methodView
        val skipped = view.addresses.first { it > 0 && view.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.CONST_4 }
        assertFalse(executed.contains(skipped), "the jumped-over const must not execute")
        assertEquals(listOf(Outcome.Returned(Value.Known(1, int))), s.outcomes)
    }

    @Test
    fun nopsFallThroughInOrder() {
        val s = session("nopThenConst()I")
        val executed = mutableListOf<Int>()
        while (true) { executed.add((s.step() ?: break).address) }
        assertEquals(s.methodView.addresses.toList(), executed, "a straight-line method executes every address once, in order")
    }

    @Test
    fun everyNodeLinksToItsParent() {
        val s = session("moveChain()I")
        val nodes = mutableListOf<ExecutionNode>()
        while (true) { nodes.add(s.step() ?: break) }
        assertNull(nodes.first().parent)
        for (i in 1 until nodes.size) {
            assertSame(nodes[i - 1], nodes[i].parent, "node ${nodes[i].address} must link to its predecessor")
            assertTrue(nodes[i - 1].children.contains(nodes[i]))
        }
        // A call-free method has one activation; a depth above 0 would let `Limits.maxCallDepth` truncate
        // straight-line methods. Rising and falling depth is `CallStackTest`'s subject.
        assertTrue(nodes.all { it.callDepth == 0 }, "a call-free method has one activation, at depth 0")
    }

    @Test
    fun malformedUnsupportedInstructionAbortsOnlyThatPathAndPoisonsCompletion() {
        val s = session("unsupportedWideFilledArray()I")
        while (s.step() != null) Unit
        assertEquals(listOf(Outcome.Aborted(AbortReason.UNSUPPORTED_OP)), s.outcomes)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), s.completion())
        assertFalse(s.completion().canTrustConsensus, "a truncated exploration must not be trusted")
    }

    @Test
    fun anAbortEndsItsOwnPathWithoutTouchingTheFrameOrEnqueuingSuccessors() {
        // Bug class: an unimplemented opcode taking the run down, or acting as a no-op so the path
        // continues with stale registers. Oracle: an Abort arm does not touch the frame and ends its path.
        val s = session("unsupportedWideFilledArray()I")
        s.step()                            // const/4 v0, 0x3
        val aborted = s.step()!!            // filled-new-array {v0}, [J -- deliberately unsupported
        assertEquals(Outcome.Aborted(AbortReason.UNSUPPORTED_OP), aborted.outcome)
        assertEquals(emptyList<ExecutionNode>(), aborted.children, "an aborted path schedules no successor")
        assertEquals(Value.Known(3, int), aborted.frame.peek(0), "the aborting op must not have written v0")
        assertNull(s.step(), "the path ended at the abort, and the session is still finishable")
    }

    @Test
    fun completeExplorationReportsComplete() {
        val s = session("returnFive()I")
        while (s.step() != null) Unit
        assertEquals(CompletionStatus.Complete, s.completion())
        assertTrue(s.completion().canTrustConsensus)
    }

    @Test
    fun aSessionThatHasNotFinishedReportsInProgressNotComplete() {
        // Bug class: completion computed from the abort set alone, so a run with work still scheduled
        // reads as Complete. `graph()` is safe to call mid-run, so a consumer can observe this.
        val s = session("returnFive()I")
        assertFalse(s.isFinished, "the entry node is scheduled and nothing has run")
        assertEquals(CompletionStatus.InProgress, s.completion(), "nothing aborted, but nothing finished either")
        assertFalse(s.completion().canTrustConsensus)
        s.step()
        assertEquals(CompletionStatus.InProgress, s.completion(), "the return at address 1 is still scheduled")
        while (s.step() != null) Unit
        assertEquals(CompletionStatus.Complete, s.completion(), "and InProgress is not sticky: draining it completes")
    }

    @Test
    fun throwWithNoHandlerEndsThePathAsThrew() {
        val s = session("throwUncaught(Ljava/lang/RuntimeException;)V")
        while (s.step() != null) Unit
        val outcome = s.outcomes.single()
        assertTrue(outcome is Outcome.Threw, "an unhandled throw ends the path as Threw, not Aborted")
        // The parameter is Unknown at entry, so the thrown value is that parameter, carried as a Value.
        assertEquals(
            Value.Unknown(VirtualType.of("Ljava/lang/RuntimeException;")),
            (outcome as Outcome.Threw).exception,
        )
    }

    @Test
    fun addressVisitLimitAbortsThePath() {
        // Exceeding a limit is Aborted(ADDRESS_LIMIT), not a wrong answer. `spinForever()V` is a `goto`
        // loop; maxSteps is a backstop so a broken address limit fails on METHOD_LIMIT instead of hanging.
        val s = session("spinForever()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        while (s.step() != null) Unit
        assertTrue(s.completion() is CompletionStatus.Partial)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), s.completion())
        assertEquals(listOf(Outcome.Aborted(AbortReason.ADDRESS_LIMIT)), s.outcomes)
    }

    @Test
    fun addressVisitLimitPermitsExactlyItsBudget() {
        // The other side of the off-by-one: a budget of N permits N visits and aborts on the N+1th. A limit
        // that tripped one visit early would mark every straight-line method Partial.
        val s = session("nopThenConst()I", Limits(maxAddressVisits = 1, maxSteps = 100))
        while (s.step() != null) Unit
        assertEquals(CompletionStatus.Complete, s.completion(), "each address is visited exactly once, so a budget of 1 suffices")
        assertEquals(listOf(Outcome.Returned(Value.Known(42, int))), s.outcomes)
    }

    @Test
    fun stepLimitAbortsTheRun() {
        val s = session("nopThenConst()I", Limits(maxSteps = 2))
        while (s.step() != null) Unit
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), s.completion())
    }

    @Test
    fun aDrainedFrontierNodeIsStillPiledEvenThoughItNeverRan() {
        // When a METHOD_LIMIT fires, abortRun ends every scheduled node without running its op; each must
        // still be piled at its own address or its Aborted outcome is unattributable.
        // `returnsOrThrows(I)I` forks at `if-eqz p0`@0 into `const/4`@2 and `new-instance`@4. With one
        // step allowed, the next dequeued successor trips the limit and the other is drained by the same
        // abortRun call. nodePile is unfiltered; wasReached counts executed nodes only.
        val s = session("returnsOrThrows(I)I", Limits(maxSteps = 1))

        while (s.step() != null) Unit

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), s.completion())
        val graph = s.graph()
        assertEquals(1, graph.nodePile(2).size, "the fall-through successor must be piled although it never ran")
        assertEquals(1, graph.nodePile(4).size, "the branch successor must be piled although it never ran")
        assertFalse(graph.wasReached(2), "the fall-through successor never executed its op")
        assertFalse(graph.wasReached(4), "the branch successor never executed its op")
        assertTrue(graph.wasReached(0), "the forking if-eqz executed before the limit hit")
    }

    /**
     * A node whose op never ran declares nothing. The step limit of 1 stops the second node after it is
     * dequeued and before `execute`, while the first op declares `STRONG`.
     */
    @Test
    fun aNodeWhoseOpNeverRanDeclaresNoSideEffect() {
        val view = viewOf("nopThenConst()I")
        val alwaysDeclares = object : Op(0) {
            override val name = "always-declares"
            override fun execute(frame: Frame): OpExecution =
                OpExecution.single(OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)))
        }
        val s = vm.newSession(view, Limits(maxSteps = 1), null) { address, current, resolver ->
            if (address == 0) alwaysDeclares else OpBuilder.build(address, current, resolver)
        }
        while (s.step() != null) Unit
        val graph = s.graph()
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0), "address 0's op ran and declared")
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(1),
            "address 1 was dequeued but the step limit stopped it before execute, so it declared nothing",
        )
    }

    @Test
    fun controlFlowRunningOffTheEndOfAMethodAbortsInsteadOfCrashing() {
        // A tail with no next instruction is malformed input, not a host failure. Dalvik would reject
        // `fallsOffTheEnd()V` (a single `nop`), but obfuscators emit such tails.
        val s = session("fallsOffTheEnd()V")
        while (s.step() != null) Unit
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), s.outcomes)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), s.completion())
    }

    @Test
    fun aForeignMethodEntryFrameIsRejectedBeforeExecution() {
        val narrow = classIndex.requireMethodView("Lspine/spine;", "returnFive()I")     // .registers 1
        val wide = classIndex.requireMethodView("Lspine/spine;", "returnWide()J")       // .registers 2
        val mismatchedFrame = Frame.entryFrame(narrow)

        assertThrows(IllegalArgumentException::class.java) {
            vm.newSession(wide, Limits(), mismatchedFrame)
        }
    }

    @Test
    fun eachSessionIsIndependent() {
        // Bug class (a): per-run state on the VM leaking between executions.
        val first = session("returnFive()I")
        while (first.step() != null) Unit
        val second = session("returnFive()I")
        while (second.step() != null) Unit
        assertEquals(first.outcomes, second.outcomes)
        assertEquals(1, second.outcomes.size, "a fresh session must not inherit the previous run's outcomes")
    }

    @Test
    fun aCallerSuppliedEntryFrameIsCopiedNotAliased() {
        // Bug class (a) through a caller-supplied entry frame: an aliased frame let one run mutate the
        // seed a second session reuses.
        val view = classIndex.requireMethodView("Lspine/spine;", "moveChain()I")
        val seeded = Frame.entryFrame(view).also { it.assign(0, Value.Known(111, int)) }
        val first = vm.newSession(view, Limits(), seeded)
        val second = vm.newSession(view, Limits(), seeded)

        first.run()
        assertEquals(Value.Known(111, int), seeded.peek(0), "the run must not overwrite the caller's seed")
        assertNull(seeded.peek(1), "nor write the caller's other registers")
        // The second session still starts from the seed, so it sees v0 = 9 only because its own const
        // ran -- proof it inherited nothing from the first run.
        while (second.step() != null) Unit
        assertEquals(first.outcomes, second.outcomes)
    }

    @Test
    fun graphIsAnImmutableSnapshotOfTheRunSoFar() {
        // Bug class: handing out a live view of the scheduler's mutable recording, so a debugger's
        // snapshot silently kept changing under it (and a consumer could mutate the run).
        val s = session("returnFive()I")
        s.step()
        val midRun = s.graph()
        assertEquals(emptyList<Outcome>(), midRun.outcomes, "nothing had ended after one step")
        while (s.step() != null) Unit
        assertEquals(emptyList<Outcome>(), midRun.outcomes, "the snapshot must not have changed")
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), s.graph().outcomes)
    }
}
