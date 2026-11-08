package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The core executing real Dalvik bytecode end to end, observed only through the public query surface.
 * Oracle: the smali source of `Lspine/spine;` and Dalvik semantics for each opcode.
 */
class SpineBehaviorTest {
    private val spine = "Lspine/spine;"
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val string = VirtualType.of("Ljava/lang/String;")

    @TestFactory
    fun constantReachesTheReturnRegister(): List<DynamicTest> = listOf(
        Triple("returnFive()I", Value.Known(5, int), 0),
        Triple("nopThenConst()I", Value.Known(42, int), 0),
        Triple("moveChain()I", Value.Known(9, int), 2),
        Triple("returnWide()J", Value.Known(0x123456789L, long), 0),
        Triple("returnWideSixteen()J", Value.Known(0x2aL, long), 0),
        Triple("returnString()Ljava/lang/String;", Value.Known("spine", string), 0),
    ).map { (method, expected, register) ->
        DynamicTest.dynamicTest(method) { CoreTester.assertReturns(spine, method, expected, register) }
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
    fun voidMethodReturnsNoValue() {
        val graph = CoreTester.execute(spine, "returnNothing()V")
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun seededRegisterIsVisibleToTheMethod() {
        val thrown = Value.Known(IllegalStateException("boom"), VirtualType.of("Ljava/lang/RuntimeException;"))
        val graph = CoreTester.execute(
            spine,
            "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;",
            initialRegisters = mapOf(1 to thrown),
        )
        val returned = graph.trusted()!!.terminatingConsensus(0) as Value.Known
        val ref = returned.raw as HeapRef
        assertEquals(thrown.type, returned.type)
        val heapObject = graph.trusted()!!.terminatingHeapObject(ref).agreed()
        assertTrue(heapObject is HeapObject.GuestThrowable)
        assertTrue((heapObject as HeapObject.GuestThrowable).contents is HostGraphContents.Known)
        val host = CoreTester.terminatingHostObject(graph, ref)
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
        // On a Partial run `wasReached == false` cannot tell unreachable code from code the run never got to,
        // so the harness must refuse before comparing addresses. unsupportedWideFilledArray() aborts at its
        // malformed [J operand, so its return at 4 is never reached.
        val reachedBeforeTheAbort = intArrayOf(0, 1)
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.assertVisited(spine, "unsupportedWideFilledArray()I", reachedBeforeTheAbort)
        }
        // The expected set is the correct one, so only the completion check can have fired.
        assertTrue(
            failure.message!!.contains("explored everything"),
            "must fail on the completion check, not on the address comparison: ${failure.message}",
        )
    }

    @Test
    fun aMalformedUnsupportedInstructionIsNotTrustedForConsensus() {
        // A partial exploration hands out no trusted view; the effect level is read through the ungated `explored`.
        val graph = CoreTester.execute(spine, "unsupportedWideFilledArray()I")
        assertFalse(graph.completion().canTrustConsensus)
        assertNull(graph.trusted(), "a partial exploration hands out no view whose answers look actionable")
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }
}
