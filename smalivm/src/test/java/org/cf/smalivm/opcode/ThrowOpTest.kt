package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.ExecutionState
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ThrowOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lthrow_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun canThrowNullPointerException() {
        val graph = Tester.execute(CLASS_NAME, "throwNullPointerException()V", initial)
        val item = graph.getTerminatingRegisterConsensus(0)
        assertTrue(item!!.raw is NullPointerException)
        assertEquals(ClassNameUtils.toInternal(NullPointerException::class.java), item.type)
        val throwItem = graph.getTerminatingRegisterConsensus(ExecutionState.THROW_REGISTER)
        assertEquals(item, throwItem)
        Tester.test(CLASS_NAME, "throwNullPointerException()V", expected)
    }
}
