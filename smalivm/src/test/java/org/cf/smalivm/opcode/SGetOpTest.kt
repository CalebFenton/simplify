package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester.test
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SGetOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lsget_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun staticBoolean() {
        initial.setFields(CLASS_NAME, "myBoolean:Z", true)
        expected.setRegisters(0, true, "Z")
        test(CLASS_NAME, "getStaticBoolean()V", initial, expected)
    }

    @Test
    fun staticBooleanLiteral() {
        expected.setRegisters(0, true, "Z")
        test(CLASS_NAME, "getStaticBooleanLiteral()V", initial, expected)
    }

    @Test
    fun staticBooleanUninitialized() {
        expected.setRegisters(0, false, "Z")
        test(CLASS_NAME, "getStaticBoolean()V", initial, expected)
    }

    @Test
    fun staticByte() {
        initial.setFields(CLASS_NAME, "myByte:B", 0xf.toByte())
        expected.setRegisters(0, 0xf.toByte(), "B")
        test(CLASS_NAME, "getStaticByte()V", initial, expected)
    }

    @Test
    fun staticByteLiteral() {
        expected.setRegisters(0, 0xf.toByte(), "B")
        test(CLASS_NAME, "getStaticByteLiteral()V", initial, expected)
    }

    @Test
    fun staticByteUninitialized() {
        expected.setRegisters(0, 0.toByte(), "B")
        test(CLASS_NAME, "getStaticByte()V", initial, expected)
    }

    @Test
    fun staticChar() {
        initial.setFields(CLASS_NAME, "myChar:C", 'x')
        expected.setRegisters(0, 'x', "C")
        test(CLASS_NAME, "getStaticChar()V", initial, expected)
    }

    @Test
    fun staticCharLiteral() {
        expected.setRegisters(0, 'x', "C")
        test(CLASS_NAME, "getStaticCharLiteral()V", initial, expected)
    }

    @Test
    fun staticCharUninitialized() {
        expected.setRegisters(0, '\u0000', "C")
        test(CLASS_NAME, "getStaticChar()V", initial, expected)
    }

    @Test
    fun staticDoubleLiteral() {
        expected.setRegisters(0, 10000000000.0, "D")
        test(CLASS_NAME, "getStaticDoubleLiteral()V", initial, expected)
    }

    @Test
    fun staticDoubleUninitialized() {
        expected.setRegisters(0, 0.0, "D")
        test(CLASS_NAME, "getStaticDouble()V", initial, expected)
    }

    @Test
    fun staticFloatLiteral() {
        expected.setRegisters(0, 1.1f, "F")
        test(CLASS_NAME, "getStaticFloatLiteral()V", initial, expected)
    }

    @Test
    fun staticFloatUninitialized() {
        expected.setRegisters(0, 0f, "F")
        test(CLASS_NAME, "getStaticFloat()V", initial, expected)
    }

    @Test
    fun staticInt() {
        initial.setFields(CLASS_NAME, "myInt:I", 0x42)
        expected.setRegisters(0, 0x42, "I")
        test(CLASS_NAME, "getStaticInt()V", initial, expected)
    }

    @Test
    fun staticIntLiteral() {
        expected.setRegisters(0, 0x42, "I")
        test(CLASS_NAME, "getStaticIntLiteral()V", initial, expected)
    }

    @Test
    fun staticIntUninitialized() {
        expected.setRegisters(0, 0, "I")
        test(CLASS_NAME, "getStaticInt()V", initial, expected)
    }

    @Test
    fun staticLong() {
        initial.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL)
        expected.setRegisters(0, 0xFFFFFFFFFL, "J")
        test(CLASS_NAME, "getStaticLong()V", initial, expected)
    }

    @Test
    fun staticLongLiteral() {
        expected.setRegisters(0, 0xFFFFFFFFFL, "J")
        test(CLASS_NAME, "getStaticLongLiteral()V", initial, expected)
    }

    @Test
    fun staticLongUninitialized() {
        expected.setRegisters(0, 0L, "J")
        test(CLASS_NAME, "getStaticLong()V", initial, expected)
    }

    @Test
    fun staticShort() {
        initial.setFields(CLASS_NAME, "myShort:S", 0x100.toShort())
        expected.setRegisters(0, 0x100.toShort(), "S")
        test(CLASS_NAME, "getStaticShort()V", initial, expected)
    }

    @Test
    fun staticShortLiteral() {
        expected.setRegisters(0, 0x100.toShort(), "S")
        test(CLASS_NAME, "getStaticShortLiteral()V", initial, expected)
    }

    @Test
    fun staticShortUninitialized() {
        expected.setRegisters(0, 0.toShort(), "S")
        test(CLASS_NAME, "getStaticShort()V", initial, expected)
    }

    @Test
    fun staticString() {
        initial.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "They tried and died.")
        expected.setRegisters(0, "They tried and died.", "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    @Test
    fun staticStringLiteral() {
        expected.setRegisters(0, "life, what's life?", "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticStringLiteral()V", initial, expected)
    }

    @Test
    fun staticStringUninitialized() {
        expected.setRegisters(0, null, "Ljava/lang/String;")
        test(CLASS_NAME, "getStaticString()V", initial, expected)
    }

    @Test
    fun staticUnknownClassFieldThrowsException() {
        expected.setRegisters(0, UnknownValue(), "I")
        val exception = assertThrows<RuntimeException> {
            test(CLASS_NAME, "getStaticUnknownClassField()V", expected)
        }
        assertEquals(exception.message, "Can't find Smali file for Lsome/unknown/classzzzzz;")
    }

    @Test
    fun staticWhitelistedClassField() {
        expected.setRegisters(0, Int.MAX_VALUE, "I")
        test(CLASS_NAME, "getStaticWhitelistedClassField()V", expected)
    }
}
