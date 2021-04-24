package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester.test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FillArrayDataOpTest {
    private lateinit var expected: TestState
    private lateinit var initial: TestState

    companion object {
        private const val CLASS_NAME = "Lfill_array_data_test;"
    }

    @BeforeEach
    fun setUp() {
        expected = TestState()
        initial = TestState()
    }

    @Test
    fun canFillArrayDataBoolean() {
        initial.setRegisters(0, BooleanArray(4), "[Z")
        expected.setRegisters(0, booleanArrayOf(true, true, false, true), "[Z")
        test(CLASS_NAME, "fillArrayDataBoolean()V", initial, expected)
    }

    @Test
    fun canFillArrayDataByte() {
        initial.setRegisters(0, ByteArray(4), "[B")
        expected.setRegisters(0, byteArrayOf(0xa, 0xb, 0xc, 0xd), "[B")
        test(CLASS_NAME, "fillArrayDataByte()V", initial, expected)
    }

    @Test
    fun canFillArrayDataChar() {
        initial.setRegisters(0, CharArray(3), "[C")
        expected.setRegisters(0, charArrayOf('a', 'b', 'c'), "[C")
        test(CLASS_NAME, "fillArrayDataChar()V", initial, expected)
    }

    @Test
    fun canFillArrayDataDouble() {
        initial.setRegisters(0, DoubleArray(2), "[D")
        expected.setRegisters(0, doubleArrayOf(0.1, 1.5), "[D")
        test(CLASS_NAME, "fillArrayDataDouble()V", initial, expected)
    }

    @Test
    fun canFillArrayDataFloat() {
        initial.setRegisters(0, FloatArray(2), "[F")
        expected.setRegisters(0, floatArrayOf(1f, 2f), "[F")
        test(CLASS_NAME, "fillArrayDataFloat()V", initial, expected)
    }

    @Test
    fun canFillArrayDataInt() {
        initial.setRegisters(0, IntArray(5), "[I")
        expected.setRegisters(0, intArrayOf(1, 2, 3, 4, 5), "[I")
        test(CLASS_NAME, "fillArrayDataInt()V", initial, expected)
    }

    @Test
    fun canFillArrayDataLongWithLongs() {
        initial.setRegisters(0, LongArray(3), "[J")
        expected.setRegisters(0, longArrayOf(0x1000000000L, 0x2000000000L, 0x3L), "[J")
        test(CLASS_NAME, "fillArrayDataLongWithLongs()V", initial, expected)
    }

    @Test
    fun canFillArrayDataLongWithInts() {
        initial.setRegisters(0, LongArray(3), "[J")
        expected.setRegisters(0, longArrayOf(0x10, 0x20, 0x30), "[J")
        test(CLASS_NAME, "fillArrayDataLongWithInts()V", initial, expected)
    }

    @Test
    fun canFillArrayDataShortWithShorts() {
        initial.setRegisters(0, ShortArray(3), "[S")
        expected.setRegisters(0, shortArrayOf(100, 200, 5), "[S")
        test(CLASS_NAME, "fillArrayDataShortWithShorts()V", initial, expected)
    }

    @Test
    fun canFillArrayDataShortWithInts() {
        initial.setRegisters(0, ShortArray(3), "[S")
        expected.setRegisters(0, shortArrayOf(0x10, 0x20, 0x30), "[S")
        test(CLASS_NAME, "fillArrayDataShortWithInts()V", initial, expected)
    }
}
