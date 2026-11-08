package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File

/**
 * The value queries a consumer acts on are reachable as a [TrustedGraphView] only through
 * [ExecutionGraph.trusted], which is null unless the run's completion is `canTrustConsensus`; the same queries
 * over a truncated or unfinished run are spelled `graph.explored`.
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
            partial.nodePile(address).any { it.progress == OpProgress.Pending }
        }
        assertFalse(partial.explored.didEveryExecutionContinueNormally(stoppedAddress))
        assertNull(partial.trusted())
    }

    @Test
    fun aClassInitializationTriggerContinuesNormallyOnlyWhenItsInitializationSettles() {
        fun driver(descriptor: String): ExecutionGraph =
            vm.newSession(classIndex.requireMethodView("Linit_trigger/driver;", descriptor)).run()
        // Object has no initializer to run; quiet's initializer runs and returns. Either way the op runs again.
        for (descriptor in listOf("allocateObject()V", "allocateQuiet()V")) {
            assertTrue(driver(descriptor).trusted()!!.didEveryExecutionContinueNormally(0), descriptor)
        }
        assertFalse(driver("allocateThrowing()V").trusted()!!.didEveryExecutionContinueNormally(0))
        val dependent = driver("allocateDependentAfterFailure()V")
        val afterFailure = dependent.addresses().last { dependent.methodView.opcodeAt(it) == Opcode.NEW_INSTANCE }
        assertFalse(dependent.trusted()!!.didEveryExecutionContinueNormally(afterFailure))
    }

    @Test
    fun parentAddressesSkipCalleesAndAClassInitializationsFirstAttempt() {
        fun driver(descriptor: String): TrustedGraphView =
            vm.newSession(classIndex.requireMethodView("Linit_trigger/driver;", descriptor)).run().trusted()!!
        assertArrayEquals(intArrayOf(0), driver("triggerAfterLiteral()V").parentAddresses(1))
        assertArrayEquals(intArrayOf(0), driver("opAfterACall()V").parentAddresses(3))
    }

    @Test
    fun onlyAPolicyAuthorizedHostCallCountsAsAHostMethod() {
        val view = classIndex.requireMethodView("Lhost_calls/caller;", "calls()V")
        val trusted = vm.newSession(view).run().trusted()!!
        val ranHost = view.instructionAddresses.toList().mapNotNull { address ->
            val called = ((view.instructionAt(address) as? ReferenceInstruction)?.reference as? MethodReference)?.name
            called?.let { it to trusted.didEveryExecutionRunHostMethod(address) }
        }.toMap()
        // append and the StringBuilder constructor run on the host; guest() runs as bytecode; policy denies gc().
        assertEquals(mapOf("<init>" to true, "append" to true, "guest" to false, "gc" to false), ranHost)
        assertFalse(trusted.didEveryExecutionRunHostMethod(0), "new-instance runs no call")
    }

    /** Trust is all-or-nothing: no abort reason is treated as harmless. */
    @TestFactory
    fun everyAbortReasonRefusesTrust(): List<DynamicTest> = listOf(
        Triple("spinForever()V", Limits(maxAddressVisits = 1, maxSteps = 100), AbortReason.ADDRESS_LIMIT),
        Triple("nopThenConst()I", Limits(maxSteps = 1), AbortReason.METHOD_LIMIT),
        Triple("unsupportedWideFilledArray()I", Limits(), AbortReason.UNSUPPORTED_OP),
    ).map { (descriptor, limits, reason) ->
        DynamicTest.dynamicTest(reason.name) {
            val graph = graphOf(descriptor, limits)
            assertEquals(CompletionStatus.Partial(setOf(reason)), graph.completion())
            assertNull(graph.trusted(), "a truncated exploration must not hand out an actionable view")
        }
    }

    @Test
    fun aPartialRunStillComputesAConfidentAnswerReachableOnlyThroughTheRawAccessor() {
        // On a run stopped at a bound, consensus still reports a confident Known and the method still reports no
        // side effects. Both are correct about the walked paths and wrong to act on, which is why the gate is on
        // reachability rather than on the values. The one executed visit to the move assigns v1 = 1, and no
        // opcode in this method has an observable effect.
        val g = graphOf("loopRevisitChangesRegister()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        val moveAddress = g.methodView.addresses.first { g.methodView.opcodeAt(it) == Opcode.MOVE }
        assertNull(g.trusted(), "the run stopped at the address budget, so no query on it is actionable")
        assertEquals(Value.Known(1, int), g.explored.consensus(moveAddress, 1))
        assertEquals(SideEffectLevel.NONE, g.explored.methodSideEffectLevel())
    }

    @Test
    fun aMidRunSnapshotHandsOutNoTrustedViewEvenWhenNothingHasAborted() {
        // `ExecutionSession.graph()` is safe to call mid-run. A path still in the frontier has not been explored,
        // so completion must consult the frontier, not only the abort set.
        val s = sessionOf("returnFive()I")
        s.step() // const/4 v0, 0x5 ran; the `return v0` at address 1 has not
        val midRun = s.graph()
        assertEquals(CompletionStatus.InProgress, midRun.completion(), "one path is still scheduled")
        assertNull(midRun.trusted(), "a snapshot of an unfinished walk must not look actionable")
        val finished = s.run()
        assertNotNull(finished.trusted(), "draining the frontier is what earns trust")
        assertEquals(Value.Known(5, int), finished.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aMidRunSnapshotNeverAuthorizesWhatTheFinishedRunRefuses() {
        // After one step the unsupported wide `filled-new-array` is scheduled, not executed, so the abort set is
        // empty while the explored view says "no side effects" and folds `const/4 v0, 0x3`. The finished run
        // aborts, and an unsupported opcode's effect errs upward to STRONG.
        val s = sessionOf("unsupportedWideFilledArray()I")
        assertNotNull(s.step(), "const/4 v0, 0x3")
        val midRun = s.graph()
        assertEquals(CompletionStatus.InProgress, midRun.completion())
        assertNull(midRun.trusted(), "a snapshot whose frontier still holds the aborting op must not authorize anything")
        assertEquals(SideEffectLevel.NONE, midRun.explored.methodSideEffectLevel())
        assertEquals(Value.Known(3, int), midRun.explored.consensus(0, 0))

        val finished = s.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), finished.completion())
        assertNull(finished.trusted())
        assertEquals(SideEffectLevel.STRONG, finished.explored.methodSideEffectLevel())
    }

    @Test
    fun isDeadProvesOnlyAnExecutableInstructionNoPathReached() {
        // gotoSkip()I jumps over one const/4, so on this complete run that instruction alone is dead.
        val graph = graphOf("gotoSkip()I")
        val trusted = requireNotNull(graph.trusted())
        val skipped = graph.methodView.addresses.first { it > 0 && graph.methodView.opcodeAt(it) == Opcode.CONST_4 }
        assertTrue(trusted.isDead(skipped), "the jumped-over const never ran")
        assertEquals(
            listOf(skipped),
            graph.methodView.instructionAddresses.filter(trusted::isDead),
            "every other instruction ran",
        )
        assertFalse(trusted.isDead(999), "an address the method does not have is not dead code")
    }
}
