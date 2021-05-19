package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.ExecutionState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MoveOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lmove_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun canMoveException() {
        val exception = "any object would do"
        initial.setRegisters(ExecutionState.EXCEPTION_REGISTER, exception, "Ljava/lang/Exception;")
        expected.setRegisters(0, exception, "Ljava/lang/Exception;")
        Tester.test(CLASS_NAME, "moveException()V", initial, expected)
    }

    @Test
    fun canMoveRegisterObjectAndCorrectlyMapRegistersToNewClone() {
        val o = Any()
        initial.setRegisters(0, o, "Ljava/lang/Object;")

        // Must invoke VM directly to ensure parse identity
        val graph = Tester.execute(CLASS_NAME, "moveRegisterObject()V", initial)
        val addresses = graph.getConnectedTerminatingAddresses()
        assertArrayEquals(addresses, intArrayOf(1), "Should terminate when expected: ${addresses.contentToString()} == {1}")
        val register0 = graph.getRegisterConsensus(1, 0)
        val register1 = graph.getRegisterConsensus(1, 1)
        assertSame(register0, register1)
        assertTrue(register0?.raw is Any)
    }

    @Test
    fun canMoveRegisterPrimitive() {
        initial.setRegisters(0, 42, "I")
        expected.setRegisters(0, 42, "I", 1, 42, "I")
        Tester.test(CLASS_NAME, "moveRegisterPrimitive()V", initial, expected)
    }

    @Test
    fun canMoveResult() {
        initial.setRegisters(ExecutionState.RESULT_REGISTER, 42, "I")
        expected.setRegisters(0, 42, "I")
        Tester.test(CLASS_NAME, "moveResult()V", initial, expected)
    }
}
