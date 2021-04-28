package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester.test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SPutOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lsput_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun putStaticBoolean() {
        initial.setRegisters(0, true, "Z")
        expected.setFields(CLASS_NAME, "myBoolean:Z", true)
        test(CLASS_NAME, "putStaticBoolean()V", initial, expected)
    }

    @Test
    fun putStaticByte() {
        initial.setRegisters(0, 0xFF.toByte(), "B")
        expected.setFields(CLASS_NAME, "myByte:B", 0xFF.toByte())
        test(CLASS_NAME, "putStaticByte()V", initial, expected)
    }

    @Test
    fun putStaticChar() {
        initial.setRegisters(0, '!', "C")
        expected.setFields(CLASS_NAME, "myChar:C", '!')
        test(CLASS_NAME, "putStaticChar()V", initial, expected)
    }

    @Test
    fun putStaticInt() {
        initial.setRegisters(0, 0x42, "I")
        expected.setFields(CLASS_NAME, "myInt:I", 0x42)
        test(CLASS_NAME, "putStaticInt()V", initial, expected)
    }

    @Test
    fun putStaticObject() {
        initial.setRegisters(0, "Do not pray for an easy life", "Ljava/lang/String;")
        expected.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life")
        test(CLASS_NAME, "putStaticObject()V", initial, expected)
    }

    @Test
    fun putStaticShort() {
        initial.setRegisters(0, 0x4242.toShort(), "S")
        expected.setFields(CLASS_NAME, "myShort:S", 0x4242.toShort())
        test(CLASS_NAME, "putStaticShort()V", initial, expected)
    }

    @Test
    fun putStaticWide() {
        initial.setRegisters(0, 0xFFFFFFFFFL, "J")
        expected.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL)
        test(CLASS_NAME, "putStaticWide()V", initial, expected)
    }
}
