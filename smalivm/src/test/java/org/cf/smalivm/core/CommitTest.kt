package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Focused commit tests for malformed targets, atomic routing, and path-loss accounting. */
class CommitTest {
    private val spine = "Lspine/spine;"
    private val INT = VirtualType.of("I")

    /** An op that returns whatever transitions the test wants, at whatever address it is built for. */
    private class Scripted(address: Int, private val transitions: List<Transition>) : Op(address) {
        override val name = "scripted"
        override fun execute(frame: Frame): OpExecution = transitions.asOpExecution()
    }

    private fun sessionWith(
        methodDescriptor: String,
        at: Int,
        transitions: List<Transition>,
    ): ExecutionSession =
        sessionOver(
            CoreTester.classIndex.requireMethodView(spine, methodDescriptor),
            at,
            transitions,
        )

    private fun sessionOver(view: MethodView, at: Int, transitions: List<Transition>): ExecutionSession =
        ExecutionSession.of(
            view, CoreTester.classIndex, Limits(), Frame.entryFrame(view),
        ) { address, v, index ->
            if (address == at) Scripted(address, transitions) else OpBuilder.build(address, v, index)
        }

    /**
     * A session whose op factory THROWS at [decodeFailsAt], and the only way to reach
     * `buildSuccessor`'s *other* unbuildable cause on purpose. A malformed target address is a
     * statement about the input and is detected before decode; a decode that throws is this engine's
     * own code failing, and no fixture can provoke that -- `OpBuilder.build` succeeds for every
     * instruction the corpus contains, which is why the two causes were indistinguishable long enough
     * to become a defect. Injecting it through the same `opFactory` seam the [Scripted] op uses keeps
     * both causes reachable from one fork shape, which is what makes the distinction testable in the
     * direction that matters.
     *
     * [decodeFailsAt] must not be the entry address: `ExecutionSession.of` decodes the root node
     * eagerly, outside any successor-building boundary.
     */
    private fun sessionWithUndecodableAddress(
        methodDescriptor: String,
        at: Int,
        transitions: List<Transition>,
        decodeFailsAt: Int,
    ): ExecutionSession {
        val view = CoreTester.classIndex.requireMethodView(spine, methodDescriptor)
        return ExecutionSession.of(
            view, CoreTester.classIndex, Limits(), Frame.entryFrame(view),
        ) { address, v, index ->
            when (address) {
                at -> Scripted(address, transitions)
                decodeFailsAt -> throw IllegalStateException("decode bug")
                else -> OpBuilder.build(address, v, index)
            }
        }
    }

    /** A fixture method with try blocks built from its view; a handler at [MethodView.endAddress] names no instruction. */
    private fun withTryBlocks(methodDescriptor: String, tryBlocks: (MethodView) -> List<TryBlock>): MethodView {
        val real = CoreTester.classIndex.requireMethodView(spine, methodDescriptor)
        return CoreTester.classIndex.rebuiltView(real, tryBlocks(real))
    }

    /**
     * DEFECT 1 (half-applied fork), plus the SKIP_ONE policy. A malformed target in one branch of a
     * genuine cross-transition fork must not discard the other, feasible branch -- but the lost path
     * must still make the run Partial, or a truncated exploration reports as complete.
     */
    @Test
    fun anUnbuildableSiblingIsLostAsPartialWhileTheFeasibleBranchStillRuns() {
        val offTheEnd = 9999
        val session = sessionWith(
            "nopThenConst()I", at = 0,
            listOf(Transition.Jump(offTheEnd), Transition.Continue),
        )
        val graph = session.run()
        assertTrue(graph.wasReached(1), "the feasible branch must still be explored")
        val completion = graph.completion()
        assertTrue(completion is CompletionStatus.Partial, "a lost feasible path must make the run Partial")
        assertTrue(
            AbortReason.MALFORMED_INPUT in (completion as CompletionStatus.Partial).reasons,
            "the lost path is a malformed target, which is MALFORMED_INPUT, got: ${completion.reasons}",
        )
    }

    /**
     * ALL_OR_NOTHING is scoped to the transition that declared it. A malformed try-block table makes
     * every clause in **that table** suspect -- and nothing else. The fall-through target of a sibling
     * `Continue` is not a clause in that table, so it is a fully resolved normal-execution path that
     * never consults the table at all, and discarding it is a precision loss against spec §2.2's "more
     * forks never fewer", as for a `div-int` inside a try block.
     *
     * Bug class: building every transition's successors into ONE flat list, so ALL_OR_NOTHING has to
     * abort out of `commit` and takes already-built successors of other transitions with it. Oracle:
     * `OnUnbuildable.ALL_OR_NOTHING`'s own stated rationale ("every clause in that table"), which is
     * narrower than "everything this op wanted".
     *
     * The reason is [AbortReason.MALFORMED_INPUT], and that ruling is deliberate: `OnUnbuildable`
     * decides HOW MUCH is abandoned, never WHY. A handler address the method does not contain is the
     * bytecode naming something impossible, exactly as an off-the-end jump is, and the table-wide
     * distrust argument for abandoning the whole group is untouched by what the loss is labelled.
     * Conflating the policy with the cause would rebuild the ambiguity the reason exists to remove.
     */
    @Test
    fun aMalformedHandlerTableDoesNotDiscardAFeasibleNormalBranch() {
        val runtimeException = VirtualType.of("Ljava/lang/RuntimeException;")
        // A KNOWN thrown type, so handler search is definite: the one clause matches and its handler
        // address is the only successor the throw contributes -- and it is not an instruction.
        val exception = Value.Known(RuntimeException("boom"), runtimeException)
        val view = withTryBlocks("nopThenConst()I") {
            listOf(TryBlock(0, 1, listOf(CatchClause(runtimeException, it.endAddress)), null))
        }
        val graph = sessionOver(view, at = 0, listOf(Transition.Continue, Transition.Throw(exception))).run()

        assertTrue(
            graph.wasReached(1),
            "the normal branch does not consult the try-block table, so a malformed table cannot kill it",
        )
        assertEquals(
            setOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT), Outcome.Returned(Value.Known(42, INT))),
            graph.outcomes.toSet(),
            "one abort for the abandoned throw, and the normal branch runs to its return",
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
    }

    /**
     * The other side of the same scoping, and the property the `transitions.size` proxy destroyed: the
     * throw's handler fork is still ALL_OR_NOTHING **within itself**, no matter what sibling transitions
     * exist. One malformed clause means NONE of that table's handlers are routed and its escape
     * conclusion is dropped too -- `escapesUnhandled` reads the same table, so "escapes unhandled" is
     * exactly as suspect as "caught at the method end".
     *
     * The good handler's address must be one the NORMAL path cannot reach, or "the good handler was
     * committed" and "the normal branch was committed" are the same observation. `gotoSkip()I` is the
     * only fixture that provides one: `const/4`@0, `goto`@1 (target 3), the SKIPPED `const/4`@2,
     * `return v0`@3 -- so address 2 is genuinely unreachable by execution and reaching it can only mean
     * the handler was routed. (`nopThenConst()I` is straight-line, so its address 2 is reached by
     * ordinary fall-through and an earlier draft of this test failed for that reason, not for the bug.)
     *
     * Oracle: `ThrowRoutingTest.oneMalformedHandlerAbortsTheWholeForkRatherThanCommittingHalfOfIt`'s
     * policy, which that test can only pin for a bare throw (`transitions.size == 1`).
     */
    @Test
    fun aPartlyMalformedHandlerTableRoutesNoneOfItsHandlersEvenWhenANormalSiblingSurvives() {
        val objectType = VirtualType.of("Ljava/lang/Object;")
        // Unknown thrown type -> ambiguous, so EVERY covering handler plus the escape is feasible and
        // the table's good clause at address 2 would be routed if the policy were per-clause.
        val view = withTryBlocks("gotoSkip()I") {
            listOf(
                TryBlock(
                    0, 1,
                    listOf(
                        CatchClause(VirtualType.of("Ljava/lang/RuntimeException;"), 2), // the skipped const
                        CatchClause(VirtualType.of("Ljava/io/IOException;"), it.endAddress), // malformed
                    ),
                    null,
                ),
            )
        }
        val graph = sessionOver(
            view, at = 0,
            listOf(Transition.Continue, Transition.Throw(Value.Unknown(objectType))),
        ).run()

        assertFalse(
            graph.wasReached(2),
            "the well-formed clause of a malformed table must NOT be routed -- that is the half-applied fork",
        )
        assertTrue(
            graph.outcomes.none { it is Outcome.Threw },
            "and the escape, which reads the same table, is dropped with it",
        )
        assertTrue(graph.wasReached(1), "while the normal branch, which reads no table, survives")
        // Returned(Top): the scripted op REPLACED `const/4 v0, 0x1` at address 0, so v0 is never
        // assigned and the `return v0` at address 3 reads an unassigned register.
        assertEquals(
            setOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT), Outcome.Returned(Value.Top)),
            graph.outcomes.toSet(),
            "ONE abort for the whole abandoned routing, not one per clause",
        )
        assertEquals(2, graph.outcomes.size)
    }

    /** Spec §4.4 requires one public outcome for each path lost to a malformed target. */
    @Test
    fun everyBranchOfAFullyUnbuildableForkGetsItsOwnOutcome() {
        val graph = sessionWith(
            "nopThenConst()I", at = 0,
            listOf(Transition.Jump(9999), Transition.Jump(8888)),
        ).run()
        assertEquals(
            2,
            graph.outcomes.count { it == Outcome.Aborted(AbortReason.MALFORMED_INPUT) },
            "two feasible branches died, so two Outcomes -- not one standing in for both",
        )
        assertEquals(2, graph.outcomes.size)
    }

    /**
     * `Op.execute`'s contract says the transition list is never empty. Before `commit`, an op that
     * broke it left its node with no outcome and no children -- invisible to every [ExecutionGraph]
     * query, i.e. a silently dropped path on a run that still reports Complete, which is the worst
     * outcome this engine can have. Oracle: spec §4.4 (one Outcome per explored path) plus `Op.execute`'s
     * own "Never empty".
     */
    @Test
    fun anOpThatReturnsNoTransitionsFailsLoudlyInsteadOfDroppingItsPathSilently() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            emptyList<Transition>().asOpExecution()
        }
        assertTrue(
            error.message!!.contains("must contain at least one arm"),
            "the protocol must reject an empty execution before scheduling, got: ${error.message}",
        )
    }

    @Test
    fun mixedReturnAndThrowArmsFailLoudlyAtTheSchedulerBoundary() {
        val thrown = Value.Known(NullPointerException(), VirtualType.of("Ljava/lang/NullPointerException;"))
        val session = sessionWith(
            "returnFive()I",
            at = 0,
            listOf(Transition.Return(null), Transition.Throw(thrown)),
        )

        val failure = assertThrows(IllegalArgumentException::class.java) { session.step() }

        assertTrue(failure.message!!.contains("Mixed path-ending transitions"))
    }

    // --- MALFORMED_INPUT: which of `buildSuccessor`'s two failures is a bug in THIS codebase ---

    /**
     * A jump to an address the method does not contain is bad INPUT, and this is the whole point of
     * [AbortReason.MALFORMED_INPUT] existing. An obfuscated APK -- the expected input for this tool --
     * emits off-the-end targets routinely, and while every one of them reported
     * [AbortReason.HOST_FAILURE] a genuine engine bug was indistinguishable in aggregate from
     * hostile-but-valid input: the difference between "our VM is broken" and "this APK is doing
     * something deliberate".
     *
     * `gotoSkip()I` rather than `nopThenConst()I` because its address 2 is reachable ONLY by a jump
     * (`const/4`@0, `goto`@1 -> 3, the skipped `const/4`@2, `return v0`@3), which the sibling test
     * below needs and which keeps both tests on one fork shape. `Returned(Value.Top)` because the
     * scripted op replaced the `const/4` that would have assigned v0.
     */
    @Test
    fun aTargetAddressTheMethodDoesNotContainIsMalformedInputAndNotAHostFailure() {
        val graph = sessionWith("gotoSkip()I", at = 0, listOf(Transition.Jump(9999), Transition.Continue)).run()
        assertEquals(
            setOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT), Outcome.Returned(Value.Top)),
            graph.outcomes.toSet(),
            "an address this method does not contain is a statement about the input, not about this engine",
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(
            graph.trusted(),
            "and the new reason must poison the trust gate exactly as every other abort does",
        )
        assertTrue(graph.wasReached(1), "the SKIP_ONE contract: the feasible sibling branch still runs")
    }

    /**
     * The direction that matters, and the reason a blanket relabel of "the null path" would have been
     * worse than the defect: decode is THIS engine's code, so a decode that throws is a genuine
     * [AbortReason.HOST_FAILURE] and must stay one. Relabelling it would destroy the one signal that
     * means "bug in this codebase", which is the entire point of splitting the reason.
     *
     * Same fixture, same fork shape, same surviving sibling as the test above; only the cause differs.
     */
    @Test
    fun aSuccessorWhoseDecodeThrowsIsStillAHostFailure() {
        val graph = sessionWithUndecodableAddress(
            "gotoSkip()I", at = 0, listOf(Transition.Jump(2), Transition.Continue), decodeFailsAt = 2,
        ).run()
        assertEquals(
            setOf(Outcome.Aborted(AbortReason.HOST_FAILURE), Outcome.Returned(Value.Top)),
            graph.outcomes.toSet(),
            "a throwable out of decode is this engine failing, and MALFORMED_INPUT must not absorb it",
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertTrue(graph.wasReached(1), "the same SKIP_ONE contract on the same fork shape")
    }

    /**
     * Both causes in ONE fork, which is the shape no pair of separate tests can pin and the shape that
     * an `Int` loss counter provably cannot express: `commit` used to fold both causes into one
     * `lost: Int` and then choose a single reason for every loss it recorded. Each lost path must keep
     * its reason; the public outcome list does not promise an order.
     *
     * This is the test that dies if the two causes are ever collapsed again -- in either direction --
     * and the reason `commit` carries a `List<AbortReason>` rather than a count.
     */
    @Test
    fun oneForkThatLosesAPathToEachCauseRecordsTheRightReasonForEachOne() {
        val graph = sessionWithUndecodableAddress(
            "gotoSkip()I", at = 0,
            listOf(Transition.Jump(9999), Transition.Jump(2), Transition.Continue),
            decodeFailsAt = 2,
        ).run()
        assertEquals(1, graph.outcomes.count { it == Outcome.Aborted(AbortReason.MALFORMED_INPUT) })
        assertEquals(1, graph.outcomes.count { it == Outcome.Aborted(AbortReason.HOST_FAILURE) })
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Top) })
        assertEquals(3, graph.outcomes.size)
        assertEquals(
            CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT, AbortReason.HOST_FAILURE)),
            graph.completion(),
            "both reasons reach the completion status; neither hides behind the other",
        )
        assertTrue(graph.wasReached(1), "and the one feasible branch of the three still runs")
    }

    /**
     * THE SIDE-EFFECT RULING, pinned rather than left to a comment. `ExecutionGraph.sideEffectLevel`
     * derives `STRONG` from `Aborted(UNSUPPORTED_OP)` and from nothing else, and
     * [AbortReason.MALFORMED_INPUT] deliberately does **not** join that rule:
     *
     *  - the op at the recording address ran to completion and declared its own level, so rule 1 is
     *    already accurate there -- unlike `UNSUPPORTED_OP`, where no real op ran and what it would
     *    have done is unknown;
     *  - the failure is detected by `MethodView.hasAddress` BEFORE any successor frame is built, so no
     *    half-mutation is possible -- which is the exact property Plan 7 used to keep `ADDRESS_LIMIT`,
     *    `METHOD_LIMIT` and `TIMEOUT` out of the rule, and the property `HOST_FAILURE` lacks;
     *  - there is no unknown op to over-approximate, because the named address is not in the method at
     *    all and so can never be an address any query reports on;
     *  - the truncation itself is already fully accounted for by `Partial` and a null `trusted()`,
     *    asserted below.
     *
     * A spurious `STRONG` is not a harmless over-approximation: Plan 7 Task 10 measured that it
     * silently stops every constant fold. The outcome assertion proves that the malformed target was
     * detected, so `NONE` is not a vacuous result.
     */
    @Test
    fun aMalformedTargetDeclaresNoSideEffectAtTheAddressThatNamedIt() {
        val graph = sessionWith("nopThenConst()I", at = 0, listOf(Transition.Jump(9999))).run()
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
        assertEquals(
            SideEffectLevel.NONE, graph.sideEffectLevel(0),
            "the op at 0 ran and declared NONE; a target that does not exist mutated nothing",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths(),
            "and the method-wide fold says the same -- MALFORMED_INPUT is not an UNSUPPORTED_OP",
        )
        assertNull(graph.trusted(), "the truncation is carried by the trust gate, not by a fake effect")
    }
}
