package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Completion-gated trust: trust-bearing queries live on [TrustedGraphView], reachable only through
 * [ExecutionGraph.trusted], which is null unless exploration finished. The raw twins stay on
 * [ExecutionGraph] under `...OverExploredPaths` names for consumers that want the truncated picture.
 *
 * Oracle: spec §9 (completion-gated trust) and `CompletionStatus.canTrustConsensus`.
 */
class TrustedGraphViewTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val vm = VmServices(classIndex)
    private val int = VirtualType.of("I")

    private fun sessionOf(descriptor: String, limits: Limits = Limits()): ExecutionSession =
        vm.newSession(classIndex.requireMethodView("Lspine/spine;", descriptor), limits)

    private fun graphOf(descriptor: String, limits: Limits = Limits()): ExecutionGraph =
        sessionOf(descriptor, limits).run()

    @Test
    fun aCompleteRunYieldsATrustedView() {
        assertNotNull(graphOf("returnFive()I").trusted(), "a Complete exploration is exactly what may be trusted")
    }

    @Test
    fun normalContinuationRejectsCaughtAndEscapingExceptions() {
        assertTrue(graphOf("returnFive()I").trusted()!!.didEveryExecutionContinueNormally(0))
        val caught = graphOf("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        assertFalse(caught.trusted()!!.didEveryExecutionContinueNormally(0))
        val escaping = graphOf("throwUncaught(Ljava/lang/RuntimeException;)V")
        assertFalse(escaping.trusted()!!.didEveryExecutionContinueNormally(0))
    }

    @Test
    fun normalContinuationCoversCompletedCallsButNotUnexecutedNodes() {
        val call = vm.newSession(
            classIndex.requireMethodView("Lcalls/caller;", "callsCalleeAndReturnsItsResult()I"),
        ).run()
        assertTrue(call.trusted()!!.didEveryExecutionContinueNormally(0))
        val partial = graphOf("loopRevisitChangesRegister()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        val stoppedAddress = partial.addresses().first { address ->
            partial.nodePile(address).any { !it.opExecutionStarted }
        }
        assertFalse(partial.didEveryExecutionContinueNormallyOverExploredPaths(stoppedAddress))
        assertNull(partial.trusted())
    }

    @Test
    fun aPartialRunYieldsNoTrustedView() {
        // The gate itself. Mutation that must fail here: `trusted()` returning the view
        // unconditionally, or testing anything other than CompletionStatus.canTrustConsensus.
        val g = graphOf("spinForever()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), g.completion())
        assertNull(g.trusted(), "a truncated exploration must not hand out a view whose answers look actionable")
        // Why `trusted()` needs no reason channel of its own (the design decision this pins): the
        // refusal reason is already one non-optional call away on the same object, and duplicating it
        // into the gate's return type would be a second representation of the same fact.
        assertEquals(
            setOf(AbortReason.ADDRESS_LIMIT),
            (g.completion() as CompletionStatus.Partial).reasons,
            "completion() already explains the refusal, so the gate itself stays a nullable view",
        )
    }

    @Test
    fun everyAbortReasonRefusesTrustNotJustLimits() {
        // Trust is all-or-nothing (CompletionStatus's own KDoc: no boolean that could be read as
        // "mostly done"). Bug class: a gate that special-cased one abort reason -- e.g. treating
        // UNSUPPORTED_OP as harmless because "that opcode does not matter here" -- would hand out a
        // trusted view for a run that never walked the rest of the method.
        assertNull(
            graphOf("unsupportedWideFilledArray()I").trusted(),
            "an unimplemented opcode truncated a path, so nothing about this run is actionable",
        )
        assertNull(
            graphOf("nopThenConst()I", Limits(maxSteps = 1)).trusted(),
            "a step budget truncated the run just as surely as an address budget",
        )
    }

    @Test
    fun aPartialRunStillComputesAConfidentAnswerReachableOnlyThroughTheRawAccessor() {
        // The exact scenario from Task 6's review that motivated this gate, kept as its permanent
        // witness: on a run that stopped at a bound, consensus genuinely still reports a confident
        // Known and the method still reports NO side effects. Both answers are correct ABOUT THE
        // PATHS THAT WERE WALKED and wrong to act on, which is why the fix had to be about
        // reachability rather than about the values. Oracle: the fixture's Dalvik semantics -- the one
        // executed visit to the move genuinely assigned v1 = 1, and none of this method's opcodes has
        // any observable effect.
        val g = graphOf("loopRevisitChangesRegister()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        val moveAddress = g.methodView.addresses.first { g.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.MOVE }
        assertNull(g.trusted(), "the run stopped at the address budget, so no query on it is actionable")
        assertEquals(
            Value.Known(1, int), g.consensusOverExploredPaths(moveAddress, 1),
            "the truncated picture is still available -- a consumer must just name it deliberately",
        )
        assertEquals(
            SideEffectLevel.NONE, g.methodSideEffectLevelOverExploredPaths(),
            "and so is the effect level, which is the query that would wrongly authorize deleting this method",
        )
    }

    // --- the gate in TIME, not just in kind: a mid-run snapshot ---

    @Test
    fun aMidRunSnapshotHandsOutNoTrustedViewEvenWhenNothingHasAborted() {
        // Bug class: a gate that only refuses ABORTED runs, so it is bypassable in TIME. Every query on
        // an `ExecutionGraph` reads the nodes recorded SO FAR, and `ExecutionSession.graph()` is
        // documented as safe to call mid-run because Plan 10's debugger is that consumer -- so with
        // completion computed from the abort set alone, a snapshot taken before the frontier drained
        // reported `Complete` and handed out a fully actionable view of a partial walk. Nothing had
        // aborted, which is exactly why the abort set could not see it. Oracle: spec §9 -- consensus is
        // trustworthy only when the relevant paths were explored, and a path still sitting in the
        // frontier has not been.
        val s = sessionOf("returnFive()I")
        s.step()                            // const/4 v0, 0x5 ran; the `return v0` at address 1 has not
        val midRun = s.graph()
        assertEquals(CompletionStatus.InProgress, midRun.completion(), "one path is still scheduled")
        assertNull(midRun.trusted(), "a snapshot of an unfinished walk must not look actionable")
        // The same run, finished, IS trustworthy -- so this refusal is about WHEN the snapshot was
        // taken, not a gate that has become permanently paranoid.
        val finished = s.run()
        assertNotNull(finished.trusted(), "draining the frontier is what earns trust")
        assertEquals(Value.Known(5, int), finished.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aMidRunSnapshotNeverAuthorizesWhatTheFinishedRunRefuses() {
        // THE DEMONSTRATED CASE, kept as its permanent witness -- the only path found by which a
        // confident wrong answer could reach a consumer, and it went THROUGH the gate rather than
        // around it. Step `unsupportedWideFilledArray()I` exactly ONCE and snapshot it: the abort set is still
        // empty (the wide `filled-new-array` that poisons the run is scheduled, not executed), so before this
        // fix `completion()` said Complete, `trusted()` was non-null, and it answered
        // `methodSideEffectLevel() == NONE` plus `consensus(0, 0) == Known(3, I)` -- "no side effects,
        // safe to delete", from an exploration that demonstrably had not finished. The finished run
        // refuses both. Oracle: spec §6.4/§9 plus the fixture's own decode status --
        // wide-component `filled-new-array` is unsupported in this build, so the effect is unknowable and must err
        // upward (spec §4.5).
        //
        // The single step still executes `const/4 v0, 0x3` at address 0. The old capability ratchet is
        // retired; this clearly named malformed-input fixture keeps the same leading const, so `Known(3, I)` below is
        // the same fact it always was (verified against the assembled layout, not assumed).
        //
        // Written so it fails if `completion()` ever stops consulting the frontier: every assertion
        // below is about the SPECIFIC answers a mid-run trusted view used to hand out, so recovering
        // one of them fails here rather than merely changing a status enum.
        val s = sessionOf("unsupportedWideFilledArray()I")
        assertNotNull(s.step(), "const/4 v0, 0x3")
        val midRun = s.graph()
        assertEquals(CompletionStatus.InProgress, midRun.completion())
        assertNull(
            midRun.trusted(),
            "a snapshot whose frontier still holds the aborting op must not authorize anything",
        )
        assertEquals(
            SideEffectLevel.NONE, midRun.methodSideEffectLevelOverExploredPaths(),
            "the dangerous answer is genuinely still computable -- it just has to be asked for by its ungated name",
        )
        assertEquals(
            Value.Known(3, int), midRun.consensusOverExploredPaths(0, 0),
            "so is the constant, which is what a propagation pass would have folded",
        )

        val finished = s.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), finished.completion())
        assertNull(finished.trusted(), "and the finished run refuses what the snapshot was handing out")
        assertEquals(
            SideEffectLevel.STRONG, finished.methodSideEffectLevelOverExploredPaths(),
            "an unimplemented opcode could have done anything, so effects err upward",
        )
    }

    @Test
    fun theGateIsReachableThroughTheConsumerFacingInterface() {
        // Bug class: putting `trusted()` only on the concrete class. Strategies code against
        // ExecutionGraphView (AnalysisContracts' whole reason to exist), so a gate absent from the
        // interface would leave every interface-typed consumer with nothing but the raw accessors --
        // i.e. exactly the pre-gate situation, silently. This assignment is the assertion: it does not
        // compile if `trusted()` moves off the interface.
        val view: ExecutionGraphView = graphOf("returnFive()I")
        assertNotNull(view.trusted(), "a strategy holding only the interface must still be able to ask for trust")
        assertEquals(
            Value.Known(5, int), view.consensusOverExploredPaths(0, 0),
            "and the raw twin belongs to the interface too, so a differential adapter can take an ExecutionGraphView",
        )
    }
}
