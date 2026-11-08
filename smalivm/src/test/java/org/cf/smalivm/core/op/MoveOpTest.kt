package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `move*` behavior over `ops/move_test.smali`.
 *
 * The register-move methods are two one-unit instructions: `@0` the move, `@1` `return-void`.
 * `moveResult` takes its slot from a real static call and `moveException` from a real caught `throw`.
 */
class MoveOpTest {
    private lateinit var initial: CoreState
    private lateinit var expected: CoreState

    @BeforeEach
    fun setUp() {
        initial = CoreState()
        expected = CoreState()
    }

    @Test
    fun movingAPrimitiveCopiesItIntoTheDestination() {
        initial.setRegisters(0, 42, "I")
        expected.setRegisters(0, 42, "I", 1, 42, "I")
        CoreTester.test(CLASS_NAME, "moveRegisterPrimitive()V", initial, expected)
    }

    /**
     * `move-exception` binds the very object the `throw` raised: Dalvik hands the handler the thrown
     * reference, so the caught register and the thrown register hold the same tracked object.
     */
    @Test
    fun moveExceptionBindsTheThrownObject() {
        val graph = CoreTester.execute(CLASS_NAME, "moveException()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes, "the throw is caught, so nothing escapes")
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val caught = trusted!!.terminatingConsensus(0)
        assertTrue(caught is Value.Known && caught.raw is HeapRef, "v0 must hold the caught throwable, was $caught")
        assertEquals(VirtualType.of("Ljava/lang/Exception;"), (caught as Value.Known).type)
        assertEquals(trusted.terminatingConsensus(1), caught, "the caught value is the thrown object itself")
    }

    @Test
    fun moveResultTakesTheCalleesReturnValue() {
        expected.setRegisters(0, 42, "I")
        CoreTester.test(CLASS_NAME, "moveResult()V", initial, expected)
    }

    /**
     * A `move` of an unknown copies the unknown rather than inventing a value. The expected value is a
     * literal [Value.Unknown], not `CoreState.UNKNOWN`, so the harness's sentinel mapping is checked here
     * instead of being applied to both sides of the comparison.
     */
    @Test
    fun movingAnUnknownYieldsAnUnknownInBothRegisters() {
        val unknownInt = Value.Unknown(VirtualType.of("I"))
        initial.setRegisters(0, CoreState.UNKNOWN, "I")
        val graph = CoreTester.execute(CLASS_NAME, "moveRegisterPrimitive()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(unknownInt, trusted!!.terminatingConsensus(0), "the source register is still unknown")
        assertEquals(unknownInt, trusted.terminatingConsensus(1), "and so is the register moved into")
    }

    /** Both registers hold the same tracked object after the move, and the path ends at the `return-void`. */
    @Test
    fun movingAnObjectLeavesBothRegistersHoldingTheSameValue() {
        val objectType = VirtualType.of("Ljava/lang/Object;")
        val seeded = Value.Known(Any(), objectType)
        initial.setRegister(0, seeded)
        val graph = CoreTester.execute(CLASS_NAME, "moveRegisterObject()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val source = trusted!!.consensus(1, 0) as Value.Known
        val alias = trusted.consensus(1, 1) as Value.Known
        assertEquals(source, alias)
        assertTrue(source.raw is HeapRef)
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
    }

    companion object {
        private const val CLASS_NAME = "Lmove_test;"
    }
}
