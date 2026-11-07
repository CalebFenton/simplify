package org.cf.smalivm.core

import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Bug classes (audit + spec §5.1): (a) per-run scheduler state living on the VM, so a second
 * execution inherited the first one's queue and stale frames; (b) an untyped `aborted` boolean, so
 * a truncated exploration was indistinguishable from a complete one; (c) an escaping host exception
 * poisoning the whole run instead of just its path. Oracle: Dalvik control flow for the fixture
 * methods plus spec §4.4/§6.4 (one Outcome per explored path; any abort makes completion Partial).
 */
class ExecutionSessionTest {
    private val classManager =
        ClassManagerFactory().build("src/test/resources/smali", DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)))
    private val classIndex = ClassIndex(classManager)
    private val int = VirtualType.of("I")

    private fun session(descriptor: String, limits: Limits = Limits()) =
        ExecutionSession.of(classIndex.requireMethodView("Lspine/spine;", descriptor), classIndex, limits)

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
        // `moveChain()I` calls nothing, so every node sits in the run's own entry activation and callDepth
        // is 0 throughout. Pinned rather than assumed, because `callDepth` is a real value now (Plan 9 Task
        // 14) rather than the stub it used to be -- a depth that crept above 0 for a call-free method would
        // mean the scheduler was minting activations it should not, and `Limits.maxCallDepth` would then
        // truncate ordinary straight-line methods. The rising-and-falling half is `core/CallStackTest`'s.
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
        // Bug class: an unimplemented opcode either taking the run down, or -- worse -- being treated
        // as a no-op so the path continues with stale registers and returns a confidently wrong value.
        // Oracle: Op.kt/UnsupportedOp (Abort does not touch the frame) + spec §4.4 (an aborted path
        // stops there). This is the session half of that contract, which Task 4 could not prove.
        val s = session("unsupportedWideFilledArray()I")
        s.step()                            // const/4 v0, 0x3
        val aborted = s.step()!!            // filled-new-array {v0}, [J -- deliberately unsupported
        assertEquals(Outcome.Aborted(AbortReason.UNSUPPORTED_OP), aborted.outcome)
        assertEquals(emptyList<ExecutionNode>(), aborted.children, "an aborted path enqueues no successor")
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
        // Bug class: computing completion from the ABORT SET ALONE, so "nothing aborted" reads as
        // "explored everything" -- which is false for every moment before the frontier drains. A fresh
        // session has executed literally nothing and still answered `Complete`, and `graph()` is
        // documented as safe to call mid-run, so that answer was reachable by a consumer (Plan 10's
        // debugger) rather than merely internal. `completion()` must therefore consult the FRONTIER as
        // well as the abort set. Oracle: spec §5.1 (one honest completion status per run) and spec §9
        // (trust requires the relevant paths to have been explored -- a scheduled path was not).
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
        // The parameter is Unknown at entry, so the thrown value is the unknown parameter itself --
        // carried as a Value, never reconstructed from a class name (spec §7).
        assertEquals(
            Value.Unknown(VirtualType.of("Ljava/lang/RuntimeException;")),
            (outcome as Outcome.Threw).exception,
        )
    }

    @Test
    fun addressVisitLimitAbortsThePath() {
        // Oracle: spec §4.4 -- exceeding a limit is Aborted(ADDRESS_LIMIT), not a wrong answer.
        // `spinForever()V` is a `goto` loop, the only way to revisit an address with the opcodes this
        // plan implements, so it is also the only method that can actually exceed a per-address budget.
        // maxSteps is set low as a backstop: if the address limit ever stops working this fails on
        // METHOD_LIMIT instead of hanging.
        val s = session("spinForever()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        while (s.step() != null) Unit
        assertTrue(s.completion() is CompletionStatus.Partial)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), s.completion())
        assertEquals(listOf(Outcome.Aborted(AbortReason.ADDRESS_LIMIT)), s.outcomes)
    }

    @Test
    fun addressVisitLimitPermitsExactlyItsBudget() {
        // The other side of the off-by-one, and the reason the test above needed a looping fixture: a
        // budget of N permits N visits and aborts on the N+1th. A limit that tripped one visit early
        // would mark every straight-line method Partial and silently disable the optimizer's trust
        // gate everywhere. Oracle: the engine being replaced uses `newAddressVisitCount >
        // maxAddressVisits` (VirtualMachine.kt:160), and Task 9's differential compares against it.
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
        // The Task 6 piling decision, from the SESSION side. abortRun's drain loop ends every node
        // still scheduled when a METHOD_LIMIT/TIMEOUT fires, none of which ever run their op -- but
        // before this fix, only the single node step() had just dequeued got added to a pile at all;
        // every OTHER drained sibling vanished as an unqueried ExecutionNode.children entry, with its
        // Aborted outcome unattributable to any address graph.nodePile could show. No Plan 3 op forks
        // yet (ambiguity is Task 7's job), so the frontier never naturally holds more than one node;
        // this test manufactures that condition with the same internal-enqueue seam
        // `successorsAreDecodedAgainstTheNodesOwnMethodNotTheSessionsEntryMethod` below uses, so the
        // drain loop actually has more than one victim to pool. Oracle: spec §6.4 (a limit-aborted
        // node must be attributable to the address it happened at) plus ExecutionGraph's contract
        // that nodePile is unfiltered while wasReached is not.
        val view = viewOf("returnFive()I") // addresses: 0 = const/4 v0,0x5, 1 = return v0
        val s = ExecutionSession.of(view, classIndex, Limits(maxSteps = 1))
        val root = s.frontier.single() // address 0, not yet stepped
        // Two synthetic siblings queued before any stepping begins, mimicking what a forking op will
        // do from Task 7 on. The frontier is LIFO, so child2 (address 0) is dequeued and executes
        // first; its own child then trips the step limit, leaving BOTH child1 (address 1) and root
        // (address 0) still sitting in the frontier for the SAME abortRun call to drain.
        s.enqueue(1, root)
        val child1 = root.children[0]
        s.enqueue(0, root)
        val child2 = root.children[1]

        while (s.step() != null) Unit

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), s.completion())
        val graph = s.graph()
        assertTrue(
            graph.nodePile(0).any { it === root },
            "root was only ever drained, never individually stepped -- it must still be piled",
        )
        assertTrue(
            graph.nodePile(1).any { it === child1 },
            "child1 was drained alongside root in the SAME abortRun call -- the loop must pile every victim, not just the first",
        )
        assertFalse(graph.wasReached(1), "neither node ever piled at address 1 executed its op")
        assertTrue(graph.wasReached(0), "child2 -- the OTHER node piled at address 0 -- genuinely executed before the limit hit")
    }

    /**
     * A node whose op **never ran** declares nothing. `maxAddressVisits = 0` is not how it is
     * provoked -- `0` disables the bound -- so this uses a step limit of 1 against a two-instruction
     * method: the second node is dequeued,
     * the bound fires before `execute`, and its level must still be `NONE`.
     *
     * That is exactly why `ExecutionGraph.sideEffectLevel` needs no executed-only filter, and it is asserted
     * rather than assumed because the transfer is now unconditional -- so the property rests on construction
     * rather than on the transfer being skipped.
     *
     * **Measured honesty about what this half is worth: the address-1 assertion has NO sole killer**, because
     * TWO independent facts protect it -- a fresh `Frame.copy` carries no level, and the limit exit returns
     * before the transfer line. Breaking it needs a two-site mutation, and the enclosing test dies anyway
     * under P2 (transfer deleted) and P3 (the graph ignores declarations) via its address-0 half. Stated
     * rather than left as an implied claim of coverage.
     */
    @Test
    fun aNodeWhoseOpNeverRanDeclaresNoSideEffect() {
        val view = viewOf("nopThenConst()I")
        val alwaysDeclares = object : Op(0) {
            override val name = "always-declares"
            override fun execute(frame: Frame): OpExecution {
                frame.recordSideEffect(SideEffectLevel.STRONG)
                return listOf(Transition.Continue).asOpExecution()
            }
        }
        val s = ExecutionSession.of(view, classIndex, Limits(maxSteps = 1), Frame.entryFrame(view)) { address, v, index ->
            if (address == 0) alwaysDeclares else OpBuilder.build(address, v, index)
        }
        while (s.step() != null) Unit
        val graph = s.graph()
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(0), "address 0's op ran and declared")
        assertEquals(
            SideEffectLevel.NONE, graph.sideEffectLevel(1),
            "address 1 was dequeued but the step limit stopped it before execute, so it declared nothing",
        )
    }

    @Test
    fun aDeclaredSideEffectSurvivesAnOpThatThrowsAHostThrowable() {
        val view = viewOf("returnFive()I")
        val declaresThenThrows = object : Op(0) {
            override val name = "declares-then-throws"
            override fun execute(frame: Frame): OpExecution {
                frame.recordSideEffect(SideEffectLevel.STRONG)
                throw IllegalStateException("boom, after declaring")
            }
        }
        val session = ExecutionSession.of(
            view,
            classIndex,
            Limits(),
            Frame.entryFrame(view),
        ) { address, current, index ->
            if (address == 0) declaresThenThrows else OpBuilder.build(address, current, index)
        }

        val node = session.step()!!
        val graph = session.graph()

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), node.outcome)
        assertEquals(SideEffectLevel.STRONG, node.sideEffectLevel)
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertNull(graph.trusted())
    }

    @Test
    fun controlFlowRunningOffTheEndOfAMethodAbortsInsteadOfCrashing() {
        // Bug class: a fallthrough or jump to an address that does not exist throwing out of the
        // scheduler and killing the run. `fallsOffTheEnd()V` is a single `nop`: Dalvik would reject it,
        // but obfuscators emit unreachable and malformed tails, so the containment has to be real.
        // Oracle: spec §4.4 -- unrepresentable means Aborted, never a wrong answer and never a crash.
        //
        // The reason is MALFORMED_INPUT, and this test used to assert HOST_FAILURE -- it was the one
        // place in the suite that encoded the defect `AbortReason.MALFORMED_INPUT` was added to fix.
        // A tail with no next instruction is malformed obfuscator output, i.e. a statement about the
        // INPUT; HOST_FAILURE means this engine or its host threw where it should not have, and
        // reporting a bad tail as one made a genuine engine bug indistinguishable in aggregate from
        // ordinary hostile input.
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
            ExecutionSession.of(wide, classIndex, Limits(), mismatchedFrame)
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
        // Bug class (a) again, re-entering through the public door: `eachSessionIsIndependent` only
        // covers entryFrame = null, so an aliased caller-supplied frame let one run mutate the seed
        // that the caller -- or a second session -- was about to reuse. Oracle: spec §4.2/§9 (no
        // sharing of mutable state across paths, and a fortiori across runs).
        val view = classIndex.requireMethodView("Lspine/spine;", "moveChain()I")
        val seeded = Frame.entryFrame(view).also { it.assign(0, Value.Known(111, int)) }
        val first = ExecutionSession.of(view, classIndex, Limits(), seeded)
        val second = ExecutionSession.of(view, classIndex, Limits(), seeded)

        first.run()
        assertEquals(Value.Known(111, int), seeded.peek(0), "the run must not overwrite the caller's seed")
        assertNull(seeded.peek(1), "nor write the caller's other registers")
        // The second session still starts from the seed, so it sees v0 = 9 only because its own const
        // ran -- proof it inherited nothing from the first run.
        while (second.step() != null) Unit
        assertEquals(first.outcomes, second.outcomes)
    }

    @Test
    fun successorsAreDecodedAgainstTheNodesOwnMethodNotTheSessionsEntryMethod() {
        // Bug class: hard-coding the session's entry method into the three decode sites (`apply`'s
        // nextAddress, `enqueue`'s hasAddress, `nodeAt`'s OpBuilder.build), which is what forces Plan 9
        // to nest a child session per call -- a second execution loop wearing recursion as a disguise,
        // which re-splits step() so Plan 10 can no longer single-step into a callee. Nothing in THIS plan
        // needs cross-method decode (one session walks one method), so without this test all three
        // sites revert to `methodView` with the whole suite green and the Plan 9 prerequisite is
        // silently gone. Oracle: spec §5.1 (one driver) plus ExecutionNode.view's contract.
        //
        // The two methods are chosen so every site discriminates. Entry `returnFive()I` has addresses
        // {0: const/4 (1 unit), 1: return}; foreign `nopThenConst()I` has {0,1: nop, 2: const/16 (TWO
        // units), 4: return}. So at address 2 the entry method has no instruction at all, and address 2
        // is 2 units wide in the foreign method but would be 1 in a narrow-const method.
        val entry = viewOf("returnFive()I")
        val foreign = viewOf("nopThenConst()I")
        val s = ExecutionSession.of(entry, classIndex, Limits())
        // No public API can produce a node belonging to another method; that is Plan 9's job. Build one
        // directly and schedule its successor, exactly as an Invoke transition eventually will.
        val foreignParent = ExecutionNode(
            address = 1,
            op = OpBuilder.build(1, foreign, classIndex),
            frame = Frame.entryFrame(foreign),
            parent = null,
            activation = Activation.entry(foreign),
        )
        s.enqueue(2, foreignParent)

        val child = foreignParent.children.single()
        assertSame(foreign, child.view, "the successor belongs to its parent's method, not the session's")
        assertEquals(2, child.address, "address 2 does not exist in the session's entry method")
        assertEquals(
            OpBuilder.build(2, foreign, classIndex).name, child.op.name,
            "the op must be the one at address 2 in the FOREIGN method",
        )
        assertNull(foreignParent.outcome, "scheduling into another method is not a malformed-flow abort")

        // Executing it exercises the third site: falling through uses the foreign method's instruction
        // widths, so const/16 at 2 continues to 4 -- and 4 exists only in the foreign method.
        assertSame(child, s.step(), "the scheduled foreign node is next (LIFO)")
        val grandchild = child.children.single()
        assertEquals(4, grandchild.address, "nextAddress must come from the node's own method (2 + 2 units)")
        assertSame(foreign, grandchild.view)
        // Two paths are still scheduled here (the session's own entry node and the foreign
        // grandchild), so the honest status mid-walk is InProgress -- "nothing aborted" is not the same
        // claim as "finished", which is exactly the distinction CompletionStatus.InProgress draws.
        // Draining it is what turns that into Complete, and both paths must end cleanly for the
        // original assertion ("nothing was mis-resolved") to hold.
        assertEquals(CompletionStatus.InProgress, s.completion())
        while (s.step() != null) Unit
        assertEquals(CompletionStatus.Complete, s.completion(), "no path aborted; nothing was mis-resolved")
    }

    @Test
    fun twoRunsOfTheSameInputExploreInTheSameOrder() {
        // Bug class: HashSet/HashMap iteration or wall-clock reads in an output path, which makes node
        // order and results irreproducible -- and Task 9's differential can only attribute a divergence
        // to the engine if both runs of the same input are identical. Oracle: spec §2.2 determinism.
        //
        // Honest scope: today's determinism is structural (LIFO ArrayDeque frontier, LinkedHashMap /
        // linkedSetOf recording, no clock read at the default limits) and `moveChain()I` is a single
        // straight-line path, so this cannot currently fail. It becomes load-bearing at Task 7's first
        // fork, when exploration order is decided by the frontier rather than by there being one option.
        fun order(): Pair<List<Int>, List<Outcome>> {
            val s = session("moveChain()I")
            val addresses = mutableListOf<Int>()
            while (true) addresses.add((s.step() ?: break).address)
            return addresses to s.outcomes
        }
        assertEquals(order(), order())
    }

    @Test
    fun runIsExactlyTheStepLoop() {
        // `run()` and a hand-written step loop are the same walk -- the whole point of having ONE
        // driver (spec §5.1). If they diverged, the optimizer and the debugger would see different
        // executions of the same method.
        val stepped = session("gotoSkip()I")
        while (stepped.step() != null) Unit
        val ran = session("gotoSkip()I").run()
        assertEquals(stepped.outcomes, ran.outcomes)
        assertEquals(CompletionStatus.Complete, stepped.completion())
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
