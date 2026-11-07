package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * First light: the clean core executing real Dalvik bytecode end to end, asserted only through the
 * public query surface. Bug class this guards: a VM that cannot be driven and observed without
 * reaching into its internals (the reason ~108 tests had to be commented out). Oracle: the smali
 * source of Lspine/spine; and Dalvik semantics for each opcode.
 */
class SpineBehaviorTest {
    private val spine = "Lspine/spine;"
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val string = VirtualType.of("Ljava/lang/String;")

    @Test
    fun constantIsPropagatedToTheReturn() {
        CoreTester.assertReturns(spine, "returnFive()I", Value.Known(5, int))
    }

    @Test
    fun nopsDoNotDisturbTheConstant() {
        CoreTester.assertReturns(spine, "nopThenConst()I", Value.Known(42, int))
    }

    @Test
    fun gotoTakesTheJumpAndSkipsTheDeadConstant() {
        CoreTester.assertReturns(spine, "gotoSkip()I", Value.Known(1, int))
        val graph = CoreTester.execute(spine, "gotoSkip()I")
        val skipped = graph.methodView.addresses.first {
            it > 0 && graph.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.CONST_4
        }
        assertFalse(graph.wasReached(skipped), "code after an unconditional goto is unreachable")
    }

    @Test
    fun valueSurvivesAChainOfMoves() {
        CoreTester.assertReturns(spine, "moveChain()I", Value.Known(9, int), register = 2)
    }

    @Test
    fun wideConstantSurvivesReturnWide() {
        CoreTester.assertReturns(spine, "returnWide()J", Value.Known(0x123456789L, long))
    }

    @Test
    fun sizedWideConstantSurvivesReturnWide() {
        CoreTester.assertReturns(spine, "returnWideSixteen()J", Value.Known(0x2aL, long))
    }

    @Test
    fun stringConstantIsPropagated() {
        CoreTester.assertReturns(spine, "returnString()Ljava/lang/String;", Value.Known("spine", string))
    }

    @Test
    fun voidMethodReturnsNoValue() {
        val graph = CoreTester.execute(spine, "returnNothing()V")
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun seededRegisterIsVisibleToTheMethod() {
        // The input-state ergonomics every later plan's behavioral tests need.
        val thrown = Value.Known(IllegalStateException("boom"), VirtualType.of("Ljava/lang/RuntimeException;"))
        val graph = CoreTester.execute(
            spine,
            "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;",
            initialRegisters = mapOf(1 to thrown),
        )
        val returned = graph.trusted()!!.terminatingConsensus(0) as Value.Known
        val ref = returned.raw as HeapRef
        assertEquals(thrown.type, returned.type)
        val heapObject = graph.trusted()!!.terminatingHeapObject(ref)
        assertTrue(heapObject is HeapObject.GuestThrowable)
        assertTrue((heapObject as HeapObject.GuestThrowable).contents is HostGraphContents.Known)
        val host = graph.terminatingHostObjectOverExploredPaths(ref)
        assertTrue(host is IllegalStateException && host.message == "boom")
        assertSame(thrown.raw, host, "seeding and handler routing keep one Java identity behind the heap ref")
    }

    @Test
    fun straightLineMethodVisitsEveryAddress() {
        val graph = CoreTester.execute(spine, "nopThenConst()I")
        CoreTester.assertVisited(spine, "nopThenConst()I", graph.methodView.addresses)
    }

    @Test
    fun assertVisitedRefusesToReadCoverageFromATruncatedRun() {
        // Bug class this guards, in the HARNESS rather than the engine: `wasReached` is ungated, and on
        // a Partial run a `false` means either "genuinely unreachable" or "the run stopped before
        // getting here" -- indistinguishably. A harness that skipped the completion check would let a
        // later plan's test state a narrower expected set, watch it PASS, and record a truncated
        // exploration as a dead-code proof. unsupportedWideFilledArray() aborts at its malformed [J operand,
        // return is never reached; the narrower set below is what such a test would wrongly assert.
        // Oracle: spec §9's completion-gated trust, applied to the query a dead-code strategy reads
        // first (ExecutionGraphView.wasReached's own contract).
        val reachedBeforeTheAbort = intArrayOf(0, 1) // const/4, filled-new-array; return at 4 is not
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.assertVisited(spine, "unsupportedWideFilledArray()I", reachedBeforeTheAbort)
        }
        // Pins WHICH assertion fired. Without this the test would pass just as happily if the address
        // sets disagreed, which would prove nothing about the guard -- and the expected set above is
        // deliberately the CORRECT one, so the address comparison would succeed on its own.
        assertTrue(
            failure.message!!.contains("explored everything"),
            "must fail on the completion check, not on the address comparison: ${failure.message}",
        )
    }

    @Test
    fun aMalformedUnsupportedInstructionIsNotTrustedForConsensus() {
        // The whole point of completion-gated trust: a partial exploration must be visibly partial.
        // Since the gate landed, "visibly" is also type-level -- trusted() is null -- and the effect
        // level has to be asked for by its ungated name. See TrustedGraphViewTest for the gate itself.
        val graph = CoreTester.execute(spine, "unsupportedWideFilledArray()I")
        assertFalse(graph.completion().canTrustConsensus)
        assertNull(graph.trusted(), "a partial exploration hands out no view whose answers look actionable")
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }
}
