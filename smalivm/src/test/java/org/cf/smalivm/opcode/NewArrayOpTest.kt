package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Array

class NewArrayOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lnew_array_test;"
        private fun testException(methodDescriptor: String, exceptionClass: Class<*>, exceptionMessage: String, initial: TestState) {
            val graph = Tester.execute(CLASS_NAME, methodDescriptor, initial)
            val item = graph.getTerminatingRegisterConsensus(0)!!

            assertEquals(exceptionClass, item.raw!!.javaClass)
            assertEquals(ClassNameUtils.toInternal(exceptionClass), item.type)
            assertEquals(exceptionMessage, (item.raw as Throwable).message)
            assertFalse(graph.wasAddressReached(2), "Should not reach next instruction in non-exception execution path")
            val node = graph.getNodePile(0)[0]
            assertEquals(0, node.state.registersAssigned.size)
        }
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun canCreate2DIntegerArray() {
        val length = 3
        initial.setRegisters(0, length, "I")
        expected.setRegisters(0, arrayOfNulls<IntArray>(length), "[[I")
        Tester.test(CLASS_NAME, "create2DIntegerArray()V", initial, expected)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun canCreate2DLocalInstanceArray() {
        val length = 5
        initial.setRegisters(0, length, "I")
        val graph = Tester.execute(CLASS_NAME, "create2DLocalInstanceArray()V", initial)
        val consensus = graph.getTerminatingRegisterConsensus(0)!!
        assertEquals("[[$CLASS_NAME", consensus.type)
        assertEquals(length, Array.getLength(consensus.raw))
        val actualClass: Class<*> = consensus.raw!!.javaClass
        assertEquals("[[$CLASS_NAME", actualClass.name)
    }

    @Test
    fun canCreateIntegerArray() {
        val length = 1
        initial.setRegisters(0, length, "I")
        expected.setRegisters(0, IntArray(length), "[I")
        Tester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreateIntegerArrayWithShortTypeLengthValue() {
        val length: Short = 1
        initial.setRegisters(0, length, "S")
        expected.setRegisters(0, IntArray(length.toInt()), "[I")
        Tester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreateIntegerArrayWithUnknownLengthValue() {
        val length: Any = UnknownValue()
        initial.setRegisters(0, length, "I")
        expected.setRegisters(0, length, "[I")
        Tester.test(CLASS_NAME, "createIntegerArray()V", initial, expected)
    }

    @Test
    fun canCreateLocalInstanceArray() {
        val length = 1
        initial.setRegisters(0, length, "I")
        val graph = Tester.execute(CLASS_NAME, "createLocalInstanceArray()V", initial)
        val consensus = graph.getTerminatingRegisterConsensus(0)!!
        assertEquals("[$CLASS_NAME", consensus.type)
        assertEquals(length, Array.getLength(consensus.raw))
        val actualClass: Class<*> = consensus.raw!!.javaClass
        assertEquals("[$CLASS_NAME", actualClass.name)
    }

    @Test
    fun nonExistentClassNameThrowsException() {
        initial.setRegisters(0, 1, "I")
        testException("createNonExistentArrayClass()V", ClassNotFoundException::class.java, "does.not.exist", initial)
    }
}
