package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.VMTester
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckCastOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lcheck_cast_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun canCastNullWithObjectTypeToString() {
        initial.setRegisters(0, null, "Ljava/lang/Object;")
        expected.setRegisters(0, null, "Ljava/lang/String;")
        VMTester.test(CLASS_NAME, "castToString()V", initial, expected)
    }

    @Test
    fun canCastStringWithObjectTypeToString() {
        initial.setRegisters(0, "great maker", "Ljava/lang/Object;")
        expected.setRegisters(0, "great maker", "Ljava/lang/String;")
        VMTester.test(CLASS_NAME, "castToString()V", initial, expected)
    }

    @Test
    fun canCastStringWithStringTypeToObject() {
        initial.setRegisters(0, "great maker", "Ljava/lang/String;")
        expected.setRegisters(0, "great maker", "Ljava/lang/Object;")
        VMTester.test(CLASS_NAME, "castToObject()V", initial, expected)
    }

    @Test
    fun objectNotOfCastTypeThrowsClassCastException() {
        initial.setRegisters(0, 0x42, "Ljava/lang/Integer;")
        VMTester.testSimpleException(
            CLASS_NAME,
            "castToStringWithCatch()V",
            ClassCastException::class.java,
            initial,
            2,
            "java.lang.Integer cannot be cast to java.lang.String"
        )
    }
}
