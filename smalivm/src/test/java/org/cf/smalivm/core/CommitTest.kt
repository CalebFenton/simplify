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

    /** An op that returns whatever arms the test wants, at whatever address it is built for. */
    private class Scripted(address: Int, private val arms: List<OpArm>) : Op(address) {
        override val name = "scripted"
        override fun execute(frame: Frame): OpExecution = OpExecution.of(arms)
    }

    private fun sessionWith(
        methodDescriptor: String,
        at: Int,
        arms: List<OpArm>,
    ): ExecutionSession =
        sessionOver(
            CoreTester.classIndex.requireMethodView(spine, methodDescriptor),
            at,
            arms,
        )

    private fun sessionOver(view: MethodView, at: Int, arms: List<OpArm>): ExecutionSession =
        CoreTester.vmServices.newSession(view, Limits(), null) { address, current, resolver ->
            if (address == at) Scripted(address, arms) else OpBuilder.build(address, current, resolver)
        }

    /**
     * A session whose decoder throws at [decodeFailsAt]: the only way to reach a successor that cannot be
     * built because this engine's own code failed, as opposed to a malformed target, which is detected
     * before decode. No fixture provokes it, because `OpBuilder.build` succeeds for every instruction in the
     * corpus.
     *
     * [decodeFailsAt] must not be the entry address: the session decodes the root node during
     * construction, outside any successor-building boundary.
     */
    private fun sessionWithUndecodableAddress(
        methodDescriptor: String,
        at: Int,
        arms: List<OpArm>,
        decodeFailsAt: Int,
    ): ExecutionSession {
        val view = CoreTester.classIndex.requireMethodView(spine, methodDescriptor)
        return CoreTester.vmServices.newSession(view, Limits(), null) { address, current, resolver ->
            when (address) {
                at -> Scripted(address, arms)
                decodeFailsAt -> throw IllegalStateException("decode bug")
                else -> OpBuilder.build(address, current, resolver)
            }
        }
    }

    private fun throwing(value: Value): OpArm = OpArm.Throw(evidence = ThrowEvidence.Existing(value))

    /** A fixture method with try blocks built from its view; a handler at [MethodView.endAddress] names no instruction. */
    private fun withTryBlocks(methodDescriptor: String, tryBlocks: (MethodView) -> List<TryBlock>): MethodView {
        val real = CoreTester.classIndex.requireMethodView(spine, methodDescriptor)
        return CoreTester.classIndex.rebuiltView(real, tryBlocks(real))
    }

    /**
     * A malformed target in one branch of a fork must not discard the other, feasible branch -- but the
     * lost path must still make the run Partial, or a truncated exploration reports as complete.
     */
    @Test
    fun anUnbuildableSiblingIsLostAsPartialWhileTheFeasibleBranchStillRuns() {
        val offTheEnd = 9999
        val session = sessionWith(
            "nopThenConst()I", at = 0,
            listOf(OpArm.Jump(offTheEnd), OpArm.Continue()),
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
     * ALL_OR_NOTHING is scoped to the arm that declared it. A malformed try-block table makes every clause
     * in **that table** suspect -- and nothing else. The fall-through target of a sibling `Continue` never
     * consults the table, so discarding it would lose a fully resolved normal-execution path, which is the
     * shape a `div-int` inside a try block has.
     *
     * The reason is [AbortReason.MALFORMED_INPUT]: `OnUnbuildable` decides HOW MUCH is abandoned, never WHY,
     * and a handler address the method does not contain is the bytecode naming something impossible.
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
        val graph = sessionOver(view, at = 0, listOf(OpArm.Continue(), throwing(exception))).run()

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
     * The throw's handler fork is ALL_OR_NOTHING **within itself**, whatever sibling arms exist. One
     * malformed clause means NONE of that table's handlers are routed and its escape conclusion is dropped
     * too -- the escape reads the same table, so "escapes unhandled" is exactly as suspect as "caught at the
     * method end".
     *
     * The good handler's address must be one the NORMAL path cannot reach. In `gotoSkip()I` (`const/4`@0,
     * `goto`@1 -> 3, the skipped `const/4`@2, `return v0`@3) address 2 is reachable only by routing.
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
            listOf(OpArm.Continue(), throwing(Value.Unknown(objectType))),
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
            listOf(OpArm.Jump(9999), OpArm.Jump(8888)),
        ).run()
        assertEquals(
            2,
            graph.outcomes.count { it == Outcome.Aborted(AbortReason.MALFORMED_INPUT) },
            "two feasible branches died, so two Outcomes -- not one standing in for both",
        )
        assertEquals(2, graph.outcomes.size)
    }

    /**
     * An op execution with no arms would leave its node with no outcome and no children: a silently
     * dropped path on a run that still reports Complete. Spec §4.4 requires one Outcome per explored path.
     */
    @Test
    fun anOpThatReturnsNoArmsFailsLoudlyInsteadOfDroppingItsPathSilently() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            OpExecution.of(emptyList())
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
            listOf(OpArm.Return.fromValue(null), throwing(thrown)),
        )

        val failure = assertThrows(IllegalArgumentException::class.java) { session.step() }

        assertTrue(failure.message!!.contains("Mixed path-ending transitions"))
    }

    // --- MALFORMED_INPUT: which of the two unbuildable-successor causes is a bug in THIS codebase ---

    /**
     * A jump to an address the method does not contain is bad INPUT: obfuscated APKs emit off-the-end
     * targets routinely, and reporting them as [AbortReason.HOST_FAILURE] would make a genuine engine bug
     * indistinguishable from hostile-but-valid input.
     *
     * `gotoSkip()I` because its address 2 is reachable ONLY by a jump, which the sibling test below needs.
     * `Returned(Value.Top)` because the scripted op replaced the `const/4` that would have assigned v0.
     */
    @Test
    fun aTargetAddressTheMethodDoesNotContainIsMalformedInputAndNotAHostFailure() {
        val graph = sessionWith("gotoSkip()I", at = 0, listOf(OpArm.Jump(9999), OpArm.Continue())).run()
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
     * Decode is THIS engine's code, so a decode that throws is a genuine [AbortReason.HOST_FAILURE] and must
     * stay one. Same fixture, same fork shape, same surviving sibling as the test above; only the cause
     * differs.
     */
    @Test
    fun aSuccessorWhoseDecodeThrowsIsStillAHostFailure() {
        val graph = sessionWithUndecodableAddress(
            "gotoSkip()I", at = 0, listOf(OpArm.Jump(2), OpArm.Continue()), decodeFailsAt = 2,
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
     * Both causes in ONE fork: each lost path keeps its own reason, which a single loss counter with one
     * chosen reason cannot express. The public outcome list does not promise an order.
     */
    @Test
    fun oneForkThatLosesAPathToEachCauseRecordsTheRightReasonForEachOne() {
        val graph = sessionWithUndecodableAddress(
            "gotoSkip()I", at = 0,
            listOf(OpArm.Jump(9999), OpArm.Jump(2), OpArm.Continue()),
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
     * `ExecutionGraph.sideEffectLevel` derives `STRONG` from `Aborted(UNSUPPORTED_OP)` and from nothing
     * else; [AbortReason.MALFORMED_INPUT] deliberately does **not** join that rule. The op at the recording
     * address ran to completion and declared its own level; the failure is detected before any successor
     * frame is built, so no half-mutation is possible; and the truncation is carried by `Partial` and a
     * null `trusted()`. A spurious `STRONG` stops every constant fold. The outcome assertion proves the
     * malformed target was detected, so `NONE` is not a vacuous result.
     */
    @Test
    fun aMalformedTargetDeclaresNoSideEffectAtTheAddressThatNamedIt() {
        val graph = sessionWith("nopThenConst()I", at = 0, listOf(OpArm.Jump(9999))).run()
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
