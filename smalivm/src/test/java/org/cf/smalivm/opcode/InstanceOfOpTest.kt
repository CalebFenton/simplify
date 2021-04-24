package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester.test
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

object InstanceOfOpTest {
    private const val CLASS_NAME = "Linstanceof_test;"

    class IntegrationTest {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun intArray2DIsInstanceOfObjectArray() {
            initial.setRegisters(0, arrayOfNulls<IntArray>(0), CommonTypes.OBJECT)
            expected.setRegisters(0, arrayOfNulls<IntArray>(0), CommonTypes.OBJECT, 1, true, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOfObjectArray()V", initial, expected)
        }

        @Test
        fun intArray2DIsNotInstanceOf2DObjectArray() {
            initial.setRegisters(0, arrayOfNulls<IntArray>(0), CommonTypes.OBJECT)
            expected.setRegisters(0, arrayOfNulls<IntArray>(0), CommonTypes.OBJECT, 1, false, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected)
        }

        @Test
        fun intArrayWithObjectAsDeclaredTypeIsInstanceOfIntArray() {
            initial.setRegisters(0, IntArray(0), CommonTypes.OBJECT)
            expected.setRegisters(0, IntArray(0), CommonTypes.OBJECT, 1, true, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOfIntArray()V", initial, expected)
        }

        @Test
        fun objectIsNotInstanceOfString() {
            initial.setRegisters(0, IntArray(0), CommonTypes.OBJECT)
            expected.setRegisters(0, IntArray(0), CommonTypes.OBJECT, 1, false, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOfString()V", initial, expected)
        }

        @Test
        fun stringArray2DIsInstanceOf2DObjectArray() {
            initial.setRegisters(0, arrayOfNulls<Array<String>>(0), CommonTypes.STRING)
            expected.setRegisters(0, arrayOfNulls<Array<String>>(0), CommonTypes.STRING, 1, true, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOf2DObjectArray()V", initial, expected)
        }

        @Test
        fun stringIsInstanceOfObject() {
            initial.setRegisters(0, "great maker", CommonTypes.STRING)
            expected.setRegisters(0, "great maker", CommonTypes.STRING, 1, true, CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOfObject()V", initial, expected)
        }

        @Test
        fun unknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType() {
            initial.setRegisters(0, UnknownValue(), CommonTypes.OBJECT)
            expected.setRegisters(0, UnknownValue(), CommonTypes.OBJECT, 1, UnknownValue(), CommonTypes.BOOL)
            test(CLASS_NAME, "instanceOfString()V", initial, expected)
        }
    }
}
