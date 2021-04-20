package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.VMTester.test
import org.cf.smalivm.VMTester.testVisitation
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BinaryMathOpTest {

    companion object {
        private const val CLASS_NAME = "Lbinary_math_test;"
    }

    @Nested
    @DisplayName("with binary doubles")
    inner class DoubleMath {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canAddDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 + 20.5, "D")
            test(CLASS_NAME, "addDouble()V", initial, expected)
        }

        @Test
        fun canAddDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 + 20.5, "D")
            test(CLASS_NAME, "addDouble2Addr()V", initial, expected)
        }

        @Test
        fun canDivDouble() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 / 0.5, "D")
            test(CLASS_NAME, "divDouble()V", initial, expected)
        }

        @Test
        fun canDivDouble2Addr() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 / 0.5, "D")
            test(CLASS_NAME, "divDouble2Addr()V", initial, expected)
        }

        @Test
        fun canDivDoubleWithDivisionByZero() {
            initial.setRegisters(0, 5.0, "D", 1, 0.0, "D")
            // Floats and doubles do not throw exceptions for div0
            val expected = intArrayOf(0, 2)
            testVisitation(CLASS_NAME, "divDoubleWithCatch()V", initial, expected)
        }

        @Test
        fun canMulDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 * 20.5, "D")
            test(CLASS_NAME, "mulDouble()V", initial, expected)
        }

        @Test
        fun canMulDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 * 20.5, "D")
            test(CLASS_NAME, "mulDouble2Addr()V", initial, expected)
        }

        @Test
        fun canRemDouble() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 % 0.5, "D")
            test(CLASS_NAME, "remDouble()V", initial, expected)
        }

        @Test
        fun canRemDouble2Addr() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 % 0.5, "D")
            test(CLASS_NAME, "remDouble2Addr()V", initial, expected)
        }

        @Test
        fun canSubDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 - 20.5, "D")
            test(CLASS_NAME, "subDouble()V", initial, expected)
        }

        @Test
        fun canSubDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 - 20.5, "D")
            test(CLASS_NAME, "subDouble2Addr()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }
    }

    @Nested
    @DisplayName("with binary floats")
    inner class FloatMath {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canAddFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f + 20.5f, "F")
            test(CLASS_NAME, "addFloat()V", initial, expected)
        }

        @Test
        fun canAddFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f + 20.5f, "F")
            test(CLASS_NAME, "addFloat2Addr()V", initial, expected)
        }

        @Test
        fun canDivFloat() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f / 0.5f, "F")
            test(CLASS_NAME, "divFloat()V", initial, expected)
        }

        @Test
        fun canDivFloat2Addr() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f / 0.5f, "F")
            test(CLASS_NAME, "divFloat2Addr()V", initial, expected)
        }

        @Test
        fun canDivFloatWithDivisionByZero() {
            initial.setRegisters(0, 5f, "F", 1, 0f, "F")
            // Floats and doubles do not throw exceptions for div0
            val expected = intArrayOf(0, 2)
            testVisitation(CLASS_NAME, "divFloatWithCatch()V", initial, expected)
        }

        @Test
        fun canMulFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f * 20.5f, "F")
            test(CLASS_NAME, "mulFloat()V", initial, expected)
        }

        @Test
        fun canMulFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f * 20.5f, "F")
            test(CLASS_NAME, "mulFloat2Addr()V", initial, expected)
        }

        @Test
        fun canRemFloat() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f % 0.5f, "F")
            test(CLASS_NAME, "remFloat()V", initial, expected)
        }

        @Test
        fun canRemFloat2Addr() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f % 0.5f, "F")
            test(CLASS_NAME, "remFloat2Addr()V", initial, expected)
        }

        @Test
        fun canSubFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f - 20.5f, "F")
            test(CLASS_NAME, "subFloat()V", initial, expected)
        }

        @Test
        fun canSubFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f - 20.5f, "F")
            test(CLASS_NAME, "subFloat2Addr()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            initial = TestState()
            expected = TestState()
        }
    }

    @Nested
    @DisplayName("with binary integers")
    inner class IntegerMath {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canAddByteAndChar() {
            val b: Byte = 0xf
            initial.setRegisters(0, 'a', "C", 1, b, "B")
            expected.setRegisters(0, 'a'.toByte() + b, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 + 7, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 + 7, "I")
            test(Companion.CLASS_NAME, "addInt2Addr()V", initial, expected)
        }

        @Test
        fun canAddIntAndBoolean() {
            val value = true
            initial.setRegisters(0, value, "Z", 1, 11, "I")
            expected.setRegisters(0, 1 + 11, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndByte() {
            val b: Byte = 0xf
            initial.setRegisters(0, -3, "I", 1, b, "B")
            expected.setRegisters(0, 12, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndChar() {
            // Compiler will actually produce something like this.
            initial.setRegisters(0, "$"[0], "C", 1, 11, "I")
            expected.setRegisters(0, "$"[0].toInt() + 11, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndShort() {
            val value: Short = 5
            initial.setRegisters(0, value, "S", 1, 11, "I")
            expected.setRegisters(0, value + 11, "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownChar() {
            initial.setRegisters(0, UnknownValue(), "C", 1, 5, "I")
            expected.setRegisters(0, UnknownValue(), "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownCharAndByte() {
            initial.setRegisters(0, UnknownValue(), "C", 1, UnknownValue(), "B")
            expected.setRegisters(0, UnknownValue(), "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownInt() {
            initial.setRegisters(0, -3, "I", 1, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "I")
            test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntLit16() {
            initial.setRegisters(0, -0xf, "I")
            expected.setRegisters(0, -0xf + 0xff, "I")
            test(Companion.CLASS_NAME, "addIntLit16()V", initial, expected)
        }

        @Test
        fun canAddIntLit8() {
            initial.setRegisters(0, -0xf, "I")
            expected.setRegisters(0, -0xf + 0xf, "I")
            test(Companion.CLASS_NAME, "addIntLit8()V", initial, expected)
        }

        @Test
        fun canAndInt() {
            initial.setRegisters(0, 2, "I", 1, 7, "I")
            expected.setRegisters(0, 2 and 7, "I")
            test(Companion.CLASS_NAME, "andInt()V", initial, expected)
        }

        @Test
        fun canAndInt2Addr() {
            initial.setRegisters(0, 2, "I", 1, 7, "I")
            expected.setRegisters(0, 2 and 7, "I")
            test(Companion.CLASS_NAME, "andInt2Addr()V", initial, expected)
        }

        @Test
        fun canAndIntLit16() {
            initial.setRegisters(0, 2, "I")
            expected.setRegisters(0, 2 and 0xff, "I")
            test(Companion.CLASS_NAME, "andIntLit16()V", initial, expected)
        }

        @Test
        fun canAndIntLit8() {
            initial.setRegisters(0, 2, "I")
            expected.setRegisters(0, 2 and 0xf, "I")
            test(Companion.CLASS_NAME, "andIntLit8()V", initial, expected)
        }

        @Test
        fun canDivInt() {
            initial.setRegisters(0, 12, "I", 1, 3, "I")
            expected.setRegisters(0, 12 / 3, "I")
            test(Companion.CLASS_NAME, "divInt()V", initial, expected)
        }

        @Test
        fun canDivInt2Addr() {
            initial.setRegisters(0, 12, "I", 1, 3, "I")
            expected.setRegisters(0, 12 / 3, "I")
            test(Companion.CLASS_NAME, "divInt2Addr()V", initial, expected)
        }

        @Test
        fun canDivIntLit16() {
            initial.setRegisters(0, 0x100, "I")
            expected.setRegisters(0, 1, "I")
            test(Companion.CLASS_NAME, "divIntLit16()V", initial, expected)
        }

        @Test
        fun canDivIntLit8() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 1, "I")
            test(Companion.CLASS_NAME, "divIntLit8()V", initial, expected)
        }

        @Test
        fun canDivIntWithCatchWithUnknownValueVisitsExceptionHandler() {
            initial.setRegisters(0, UnknownValue(), "I", 1, 5, "I")
            val expected = intArrayOf(0, 2, 3, 4)
            testVisitation(Companion.CLASS_NAME, "divIntWithCatch()V", initial, expected)
        }

        @Test
        fun canMulInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 * 7, "I")
            test(Companion.CLASS_NAME, "mulInt()V", initial, expected)
        }

        @Test
        fun canMulInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 10, "I")
            expected.setRegisters(0, -3 * 10, "I")
            test(Companion.CLASS_NAME, "mulInt2Addr()V", initial, expected)
        }

        @Test
        fun canMulIntLit16() {
            initial.setRegisters(0, -3, "I")
            expected.setRegisters(0, -3 * 0xff, "I")
            test(Companion.CLASS_NAME, "mulIntLit16()V", initial, expected)
        }

        @Test
        fun canMulIntLit8() {
            initial.setRegisters(0, -3, "I")
            expected.setRegisters(0, -3 * 0xf, "I")
            test(Companion.CLASS_NAME, "mulIntLit8()V", initial, expected)
        }

        @Test
        fun canOrInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 or 2, "I")
            test(Companion.CLASS_NAME, "orInt()V", initial, expected)
        }

        @Test
        fun canOrInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 or 2, "I")
            test(Companion.CLASS_NAME, "orInt2Addr()V", initial, expected)
        }

        @Test
        fun canOrIntLit16() {
            initial.setRegisters(0, 5, "I")
            expected.setRegisters(0, 5 or 0xff, "I")
            test(Companion.CLASS_NAME, "orIntLit16()V", initial, expected)
        }

        @Test
        fun canOrIntLit8() {
            initial.setRegisters(0, 5, "I")
            expected.setRegisters(0, 5 or 0xf, "I")
            test(Companion.CLASS_NAME, "orIntLit8()V", initial, expected)
        }

        @Test
        fun canRemInt() {
            initial.setRegisters(0, 7, "I", 1, 5, "I")
            expected.setRegisters(0, 7 % 5, "I")
            test(Companion.CLASS_NAME, "remInt()V", initial, expected)
        }

        @Test
        fun canRemInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 5, "I")
            expected.setRegisters(0, 7 % 5, "I")
            test(Companion.CLASS_NAME, "remInt2Addr()V", initial, expected)
        }

        @Test
        fun canRemIntLit16() {
            initial.setRegisters(0, 0x100, "I")
            expected.setRegisters(0, 0x100 % 0xff, "I")
            test(Companion.CLASS_NAME, "remIntLit16()V", initial, expected)
        }

        @Test
        fun canRemIntLit8() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 0x10 % 0xf, "I")
            test(Companion.CLASS_NAME, "remIntLit8()V", initial, expected)
        }

        @Test
        fun canRSubInt() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 0xff - 7, "I")
            test(Companion.CLASS_NAME, "RSubInt()V", initial, expected)
        }

        @Test
        fun canRSubIntLit8() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 0xf - 7, "I")
            test(Companion.CLASS_NAME, "RSubIntLit8()V", initial, expected)
        }

        @Test
        fun canShlInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 shl 2, "I")
            test(Companion.CLASS_NAME, "shlInt()V", initial, expected)
        }

        @Test
        fun canShlInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 shl 2, "I")
            test(Companion.CLASS_NAME, "shlInt2Addr()V", initial, expected)
        }

        @Test
        fun canShlIntLit8() {
            initial.setRegisters(0, 3, "I")
            expected.setRegisters(0, 3 shl 0x2, "I")
            test(Companion.CLASS_NAME, "shlIntLit8()V", initial, expected)
        }

        @Test
        fun canShrInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            test(Companion.CLASS_NAME, "shrInt()V", initial, expected)
        }

        @Test
        fun canShrInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            test(Companion.CLASS_NAME, "shrInt2Addr()V", initial, expected)
        }

        @Test
        fun canShrIntLit8() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            test(Companion.CLASS_NAME, "shrIntLit8()V", initial, expected)
        }

        @Test
        fun canSubInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 - 7, "I")
            test(Companion.CLASS_NAME, "subInt()V", initial, expected)
        }

        @Test
        fun canSubInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 - 7, "I")
            test(Companion.CLASS_NAME, "subInt2Addr()V", initial, expected)
        }

        @Test
        fun canUshrInt() {
            initial.setRegisters(0, -14, "I", 1, 2, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            test(Companion.CLASS_NAME, "ushrInt()V", initial, expected)
        }

        @Test
        fun canUshrInt2Addr() {
            initial.setRegisters(0, -14, "I", 1, 2, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            test(Companion.CLASS_NAME, "ushrInt2Addr()V", initial, expected)
        }

        @Test
        fun canUshrIntLit8() {
            initial.setRegisters(0, -14, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            test(Companion.CLASS_NAME, "ushrIntLit8()V", initial, expected)
        }

        @Test
        fun canXorInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 xor 2, "I")
            test(Companion.CLASS_NAME, "xorInt()V", initial, expected)
        }

        @Test
        fun canXorInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 xor 2, "I")
            test(Companion.CLASS_NAME, "xorInt2Addr()V", initial, expected)
        }

        @Test
        fun canXorIntLit16() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 7 xor 0x10, "I")
            test(Companion.CLASS_NAME, "xorIntLit16()V", initial, expected)
        }

        @Test
        fun canXorIntLit8() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 7 xor 3, "I")
            test(Companion.CLASS_NAME, "xorIntLit8()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            initial = TestState()
            expected = TestState()
        }
    }

    @Nested
    @DisplayName("with binary longs")
    inner class LongMath {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canAddLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J")
            test(CLASS_NAME, "addLong()V", initial, expected)
        }

        @Test
        fun canAddLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J")
            test(CLASS_NAME, "addLong2Addr()V", initial, expected)
        }

        @Test
        fun canAndLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL and 0x1234567890L, "J")
            test(CLASS_NAME, "andLong()V", initial, expected)
        }

        @Test
        fun canAndLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL and 0x1234567890L, "J")
            test(CLASS_NAME, "andLong2Addr()V", initial, expected)
        }

        @Test
        fun canDivLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L / 3L, "J")
            test(CLASS_NAME, "divLong()V", initial, expected)
        }

        @Test
        fun canDivLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L / 3L, "J")
            test(CLASS_NAME, "divLong2Addr()V", initial, expected)
        }

        @Test
        fun canDivLongWithCatchWithUnkownValueVisitsExceptionHandler() {
            initial.setRegisters(0, 5L, "J", 1, UnknownValue(), "J")
            val expected = intArrayOf(0, 2, 3, 4)
            testVisitation(CLASS_NAME, "divLongWithCatch()V", initial, expected)
        }

        @Test
        fun canMulLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L * 3L, "J")
            test(CLASS_NAME, "mulLong()V", initial, expected)
        }

        @Test
        fun canMulLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L * 3L, "J")
            test(CLASS_NAME, "mulLong2Addr()V", initial, expected)
        }

        @Test
        fun canOrLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL or 0x1234567890L, "J")
            test(CLASS_NAME, "orLong()V", initial, expected)
        }

        @Test
        fun canOrLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL or 0x1234567890L, "J")
            test(CLASS_NAME, "orLong2Addr()V", initial, expected)
        }

        @Test
        fun canRemLong() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000123L % 3L, "J")
            test(CLASS_NAME, "remLong()V", initial, expected)
        }

        @Test
        fun canRemLong2Addr() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000123L % 3L, "J")
            test(CLASS_NAME, "remLong2Addr()V", initial, expected)
        }

        @Test
        fun canShlLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shl 5, "J")
            test(CLASS_NAME, "shlLong()V", initial, expected)
        }

        @Test
        fun canShlLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shl 5, "J")
            test(CLASS_NAME, "shlLong2Addr()V", initial, expected)
        }

        @Test
        fun canShrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shr 5, "J")
            test(CLASS_NAME, "shrLong()V", initial, expected)
        }

        @Test
        fun canShrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shr 5, "J")
            test(CLASS_NAME, "shrLong2Addr()V", initial, expected)
        }

        @Test
        fun canSubLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J")
            test(CLASS_NAME, "subLong()V", initial, expected)
        }

        @Test
        fun canSubLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J")
            test(CLASS_NAME, "subLong2Addr()V", initial, expected)
        }

        @Test
        fun canUshrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L ushr 5, "J")
            test(CLASS_NAME, "ushrLong()V", initial, expected)
        }

        @Test
        fun canUshrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L ushr 5, "J")
            test(CLASS_NAME, "ushrLong2Addr()V", initial, expected)
        }

        @Test
        fun canXorLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL xor 0x1234567890L, "J")
            test(CLASS_NAME, "xorLong()V", initial, expected)
        }

        @Test
        fun canXorLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL xor 0x1234567890L, "J")
            test(CLASS_NAME, "xorLong2Addr()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            initial = TestState()
            expected = TestState()
        }
    }

//    class UnitTest {
//        private var addressToLocation: TIntObjectMap<MethodLocation>? = null
//        private var location: MethodLocation? = null
//        private var mState: MethodState? = null
//        private var node: ExecutionNode? = null
//        private var op: BinaryMathOp? = null
//        private var opFactory: BinaryMathOpFactory? = null
//        private var vm: VirtualMachine? = null
//        @Test
//        fun doubleDivisionWithTwoRegistersEqualsExpected() {
//            val value1 = 1586.2
//            val value2 = 2536.9
//            val expected = value1 / value2
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "D")
//            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "D")
//            buildInstruction12x(Opcode.DIV_DOUBLE)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("D"))
//        }
//
//        @Test
//        fun floatDivisionWithTwoRegistersEqualsExpected() {
//            val value1 = 1120403456.43f
//            val value2 = 1149239296.32f
//            val expected = value1 / value2
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "F")
//            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "F")
//            buildInstruction12x(Opcode.DIV_FLOAT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("F"))
//        }
//
//        @Test
//        fun intDivisionWithLiteralEqualsExpected() {
//            val value1 = 10
//            val value2 = 4
//            val expected = value1 / value2
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I")
//            buildInstruction22s(Opcode.DIV_INT, value2)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("I"))
//        }
//
//        @Test
//        fun integerDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
//            val value1 = 10
//            val value2 = 0
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I")
//            buildInstruction22s(Opcode.DIV_INT_LIT16, value2)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            VMTester.verifyExceptionHandling(ArithmeticException::class.java, "/ by zero", node, mState)
//        }
//
//        @Test
//        fun integerModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
//            val value1 = 10
//            val value2 = 0
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "I")
//            buildInstruction22s(Opcode.REM_INT_LIT16, value2)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            VMTester.verifyExceptionHandling(ArithmeticException::class.java, "/ by zero", node, mState)
//        }
//
//        @Test
//        fun longDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
//            val value1 = 1120403456L
//            val value2 = 0L
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J")
//            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J")
//            buildInstruction23x(Opcode.DIV_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            VMTester.verifyExceptionHandling(ArithmeticException::class.java, "/ by zero", node, mState)
//        }
//
//        @Test
//        fun longDivisionWithThreeArgumentsAndResultLessThanOneEqualsZero() {
//            val value1 = 1120403456L
//            val value2 = 1149239296L
//            val expected = value1 / value2 // 0 since long division drops decimal value
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J")
//            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J")
//            buildInstruction23x(Opcode.DIV_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("J"))
//            assertEquals("div-long r" + DEST_REGISTER + ", r" + ARG1_REGISTER + ", r" + ARG2_REGISTER, op.toString())
//        }
//
//        @Test
//        fun longModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
//            val value1 = 1120403456L
//            val value2 = 0L
//            VMTester.setRegisterMock(mState, ARG1_REGISTER, value1, "J")
//            VMTester.setRegisterMock(mState, ARG2_REGISTER, value2, "J")
//            buildInstruction23x(Opcode.REM_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            VMTester.verifyExceptionHandling(ArithmeticException::class.java, "/ by zero", node, mState)
//        }
//
//        @BeforeEach
//        @JvmStatic
//        fun setUp() {
//            vm = Mockito.mock(VirtualMachine2::class.java)
//            mState = Mockito.mock(MethodState::class.java)
//            node = Mockito.mock(ExecutionNode::class.java)
//            location = Mockito.mock(MethodLocation::class.java)
//            Mockito.`when`(location.getCodeAddress()).thenReturn(ADDRESS)
//            val exceptionFactory: ExceptionFactory = Mockito.mock(ExceptionFactory::class.java)
//            Mockito.`when`(vm.getExceptionFactory()).thenReturn(exceptionFactory)
//            addressToLocation = TIntObjectHashMap()
//            addressToLocation.put(ADDRESS, location)
//            opFactory = BinaryMathOpFactory()
//        }
//
//        private fun buildInstruction12x(opcode: Opcode): BuilderInstruction12x {
//            val instruction: BuilderInstruction12x = Mockito.mock(BuilderInstruction12x::class.java)
//            setupInstruction(instruction, opcode)
//            Mockito.`when`((instruction as Instruction12x).getRegisterA()).thenReturn(ARG1_REGISTER)
//            Mockito.`when`((instruction as Instruction12x).getRegisterB()).thenReturn(ARG2_REGISTER)
//            return instruction
//        }
//
//        private fun buildInstruction22s(opcode: Opcode, value: Int): BuilderInstruction22s {
//            val instruction: BuilderInstruction22s = Mockito.mock(BuilderInstruction22s::class.java)
//            setupInstruction(instruction, opcode)
//            Mockito.`when`((instruction as Instruction22s).getRegisterA()).thenReturn(DEST_REGISTER)
//            Mockito.`when`((instruction as Instruction22s).getRegisterB()).thenReturn(ARG1_REGISTER)
//            Mockito.`when`((instruction as Instruction22s).getNarrowLiteral()).thenReturn(value)
//            return instruction
//        }
//
//        private fun buildInstruction23x(opcode: Opcode): BuilderInstruction23x {
//            val instruction = Mockito.mock(BuilderInstruction23x::class.java)
//            setupInstruction(instruction, opcode)
//            Mockito.`when`((instruction as Instruction23x).registerA).thenReturn(DEST_REGISTER)
//            Mockito.`when`((instruction as Instruction23x).registerB).thenReturn(ARG1_REGISTER)
//            Mockito.`when`((instruction as Instruction23x).registerC).thenReturn(ARG2_REGISTER)
//            return instruction
//        }
//
//        private fun setupInstruction(instruction: BuilderInstruction, opcode: Opcode) {
//            Mockito.`when`(location!!.instruction).thenReturn(instruction)
//            Mockito.`when`(instruction.location).thenReturn(location)
//            Mockito.`when`(instruction.codeUnits).thenReturn(0)
//            Mockito.`when`(instruction.opcode).thenReturn(opcode)
//        }
//
//        companion object {
//            private const val ADDRESS = 0
//            private const val ARG1_REGISTER = 2
//            private const val ARG2_REGISTER = 4
//            private const val DEST_REGISTER = 0
//        }
//    }
}
