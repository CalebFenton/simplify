package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.VMTester
import org.cf.smalivm.type.UnknownValue
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ArrayLengthOpTest {
    companion object {
        private const val CLASS_NAME = "Larray_length_test;"

        private fun testException(methodDescriptor: String, exceptionClass: Class<*>, exceptionMessage: String, initial: TestState) {
            val graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial)
            val item = graph.getTerminatingRegisterConsensus(0)!!
            assertEquals(exceptionClass, item.value!!.javaClass)
            assertEquals(ClassNameUtils.toInternal(exceptionClass), item.type)
            assertEquals(exceptionMessage, (item.value as Throwable).message)
            assertFalse(graph.wasAddressReached(1), "Should not reach next instruction in non-exception execution path")
            val node = graph.getNodePile(0)[0]
            assertEquals(0, node.state.registersAssigned.size)
        }
    }

    @Nested
    @DisplayName("with object arrays")
    inner class ObjectArrays {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canGetLengthForEmptyIntegerArray() {
            initial.setRegisters(0, arrayOf<Int>(), "[I")
            expected.setRegisters(0, 0, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForIntegerArray() {
            initial.setRegisters(0, arrayOf(1, 2, 3), "[I")
            expected.setRegisters(0, 3, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForStringArray() {
            initial.setRegisters(0, arrayOf("isn't", "this", "where"), "[I")
            expected.setRegisters(0, 3, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForUnknownValueOfIntegerType() {
            initial.setRegisters(0, UnknownValue(), "[Ljava/lang/Integer;")
            expected.setRegisters(0, UnknownValue(), "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForUnknownValueOfPrimitiveType() {
            initial.setRegisters(0, UnknownValue(), "[I")
            expected.setRegisters(0, UnknownValue(), "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            initial = TestState()
            expected = TestState()
        }
    }

    @Nested
    @DisplayName("with primitive arrays")
    inner class PrimitiveArrays {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canGetLengthForEmptyIntArray() {
            initial.setRegisters(0, intArrayOf(), "[I")
            expected.setRegisters(0, 0, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForIntArray() {
            initial.setRegisters(0, intArrayOf(1, 2, 3), "[I")
            expected.setRegisters(0, 3, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForLongArray() {
            initial.setRegisters(0, longArrayOf(1, 2, 3, 4), "[J")
            expected.setRegisters(0, 4, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun canGetLengthForShortArray() {
            initial.setRegisters(0, shortArrayOf(1, 2), "[S")
            expected.setRegisters(0, 2, "I")
            VMTester.test(CLASS_NAME, "getLength()V", initial, expected)
        }

        @Test
        fun nullArrayThrowsExpectedException() {
            initial.setRegisters(0, null, "[S")
            testException("getLengthWithCatch()V", NullPointerException::class.java, "Attempt to get length of null array", initial)
        }

        @BeforeEach
        fun setUp() {
            initial = TestState()
            expected = TestState()
        }
    }
}
