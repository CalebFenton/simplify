package org.cf.smalivm.core.op

import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `neg-*`, `not-*`, and the primitive conversions. Dalvik's conversions and negation are the JVM's: JLS §5.1.2
 * widening, JLS §5.1.3 narrowing (NaN to 0, truncation toward zero, saturation at the target's MIN/MAX, low bits
 * kept by an integral narrowing), and JLS §15.15.4 negation. Every expected value is written out from those rules,
 * not read off the op.
 */
class UnaryMathOpTest {
    /** The fixture's `neg-int v0, v0` writes the register it reads, so no value assertion can observe the read. */
    @Test
    fun negIntReportsItsSourceRegisterAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "negInt()V")
        assertEquals(setOf(0), graph.registersRead(0))
    }

    @TestFactory
    fun floatingToIntegralTruncatesTowardZeroSendsNaNToZeroAndSaturates(): List<DynamicTest> = listOf(
        unary("doubleToInt()V", 200.0, "D", 200, "I"),
        // A fractional seed separates truncation from ceil; a negative one separates it from floor.
        unary("doubleToInt()V", 11204.0345612345, "D", 11204, "I"),
        unary("doubleToInt()V", -11204.0345612345, "D", -11204, "I"),
        unary("doubleToInt()V", Double.NaN, "D", 0, "I"),
        unary("doubleToInt()V", Double.POSITIVE_INFINITY, "D", Int.MAX_VALUE, "I"),
        unary("doubleToInt()V", Double.NEGATIVE_INFINITY, "D", Int.MIN_VALUE, "I"),
        unary("doubleToInt()V", 1.0e10, "D", Int.MAX_VALUE, "I"),
        unary("doubleToInt()V", -1.0e10, "D", Int.MIN_VALUE, "I"),
        unary("doubleToInt()V", 2147483647.9, "D", Int.MAX_VALUE, "I"),
        unary("doubleToLong()V", 210.0, "D", 210L, "J"),
        unary("doubleToLong()V", 11204.0345612345, "D", 11204L, "J"),
        unary("doubleToLong()V", Double.NaN, "D", 0L, "J"),
        unary("doubleToLong()V", Double.POSITIVE_INFINITY, "D", Long.MAX_VALUE, "J"),
        unary("doubleToLong()V", Double.NEGATIVE_INFINITY, "D", Long.MIN_VALUE, "J"),
        unary("doubleToLong()V", 1.0e19, "D", Long.MAX_VALUE, "J"),
        unary("doubleToLong()V", -1.0e19, "D", Long.MIN_VALUE, "J"),
        unary("floatToInt()V", 100f, "F", 100, "I"),
        unary("floatToInt()V", 11204.0345f, "F", 11204, "I"),
        unary("floatToInt()V", Float.NaN, "F", 0, "I"),
        unary("floatToInt()V", Float.POSITIVE_INFINITY, "F", Int.MAX_VALUE, "I"),
        unary("floatToInt()V", Float.NEGATIVE_INFINITY, "F", Int.MIN_VALUE, "I"),
        unary("floatToInt()V", 3.0e9f, "F", Int.MAX_VALUE, "I"),
        unary("floatToInt()V", -3.0e9f, "F", Int.MIN_VALUE, "I"),
        unary("floatToLong()V", 110f, "F", 110L, "J"),
        unary("floatToLong()V", 11204.0345f, "F", 11204L, "J"),
        unary("floatToLong()V", Float.NaN, "F", 0L, "J"),
        unary("floatToLong()V", Float.POSITIVE_INFINITY, "F", Long.MAX_VALUE, "J"),
        unary("floatToLong()V", Float.NEGATIVE_INFINITY, "F", Long.MIN_VALUE, "J"),
        unary("floatToLong()V", 1.0e19f, "F", Long.MAX_VALUE, "J"),
        unary("floatToLong()V", -1.0e19f, "F", Long.MIN_VALUE, "J"),
    )

    @TestFactory
    fun conversionsToFloatingPointWidenExactlyOrRoundToNearest(): List<DynamicTest> = listOf(
        unary("doubleToFloat()V", 220.0, "D", 220f, "F"),
        // The nearest float to 11204.0345612345 is 11204.0341796875 (bits 0x462f1023).
        unary("doubleToFloat()V", 11204.0345612345, "D", 11204.0341796875f, "F"),
        unary("floatToDouble()V", 120f, "F", 120.0, "D"),
        // The float literal 11204.0345f stores 11204.0341796875, which widens exactly.
        unary("floatToDouble()V", 11204.0345f, "F", 11204.0341796875, "D"),
        unary("intToFloat()V", 12, "I", 12f, "F"),
        unary("intToDouble()V", 13, "I", 13.0, "D"),
        unary("longToFloat()V", 22L, "J", 22f, "F"),
        unary("longToDouble()V", 23L, "J", 23.0, "D"),
        // 112040345 needs 27 bits: exact in a double, but a route through float rounds it to 112040344.
        unary("longToDouble()V", 112040345L, "J", 112040345.0, "D"),
    )

    @TestFactory
    fun integralConversionsKeepTheLowBitsWithTheTargetsExtension(): List<DynamicTest> = listOf(
        unary("intToLong()V", 11, "I", 11L, "J"),
        unary("longToInt()V", 21L, "J", 21, "I"),
        unary("longToInt()V", 0x123456789L, "J", 0x23456789, "I"),
        unary("longToInt()V", 0x80000000L, "J", Int.MIN_VALUE, "I"),
        unary("longToInt()V", 0xFFFFFFFFL, "J", -1, "I"),
        unary("longToInt()V", Long.MIN_VALUE, "J", 0, "I"),
        unary("intToByte()V", 0x80, "I", (-128).toByte(), "B"),
        unary("intToByte()V", 0xff, "I", (-1).toByte(), "B"),
        unary("intToByte()V", -129, "I", 127.toByte(), "B"),
        unary("intToByte()V", 1000.toShort(), "S", (-24).toByte(), "B"),
        unary("intToShort()V", 0x10, "I", 0x10.toShort(), "S"),
        unary("intToShort()V", 0x8000, "I", (-32768).toShort(), "S"),
        unary("intToShort()V", 0x1ffff, "I", (-1).toShort(), "S"),
        unary("intToShort()V", -32769, "I", 32767.toShort(), "S"),
        unary("intToChar()V", 0x62, "I", 'b', "C"),
        unary("intToChar()V", 0x62.toShort(), "S", 'b', "C"),
        unary("intToChar()V", -1, "I", '\uffff', "C"),
        unary("intToChar()V", 0x10041, "I", 'A', "C"),
        unary("intToChar()V", 0x8000, "I", '\u8000', "C"),
    )

    @TestFactory
    fun negAndNotWrapInTwosComplementAndFlipTheFloatingSign(): List<DynamicTest> = listOf(
        unary("negInt()V", 0x42, "I", -0x42, "I"),
        unary("negInt()V", 0x42.toShort(), "S", -0x42, "I"),
        unary("negInt()V", Int.MIN_VALUE, "I", Int.MIN_VALUE, "I"),
        unary("notInt()V", 0x42, "I", 0x42.inv(), "I"),
        unary("notInt()V", 0x42.toShort(), "S", 0x42.inv(), "I"),
        unary("negLong()V", 0x100L, "J", -0x100L, "J"),
        unary("negLong()V", Long.MIN_VALUE, "J", Long.MIN_VALUE, "J"),
        unary("notLong()V", 100L, "J", 100L.inv(), "J"),
        unary("negFloat()V", 10.5f, "F", -10.5f, "F"),
        unary("negDouble()V", 15.1, "D", -15.1, "D"),
    )

    /**
     * An unknown source, or one with no reading at the mnemonic's width, gives `Unknown` of the result type
     * (DALVIK-SEMANTICS.md, "Unary operations").
     */
    @TestFactory
    fun unknownOrUnreadableSourceGivesUnknownOfTheResultType(): List<DynamicTest> = listOf(
        unary("negInt()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "I"),
        unary("notInt()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "I"),
        unary("intToByte()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "B"),
        unary("intToChar()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "C"),
        unary("intToShort()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "S"),
        unary("intToLong()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "J"),
        unary("intToFloat()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "F"),
        unary("intToDouble()V", CoreState.UNKNOWN, "I", CoreState.UNKNOWN, "D"),
        // A 64-bit read of a narrow raw: the high half is v1, which one value per register pair does not hold.
        unary("negDouble()V", 5, "I", CoreState.UNKNOWN, "D"),
    )

    /**
     * The op reads the register's bits at the mnemonic's width, then converts by value (DALVIK-SEMANTICS.md,
     * "Coercion and register bits"). A value conversion of the raw answers a constant no device produces.
     */
    @TestFactory
    fun sourceBitsAreReadAtTheMnemonicsWidthBeforeConverting(): List<DynamicTest> = listOf(
        // const-wide 0x4000000000000000 leaves a Long raw under a D tag; those bits are 2.0.
        unary("negDouble()V", 4611686018427387904L, "D", -2.0, "D"),
        // const 0x3f800000 leaves an Int raw; read as a float it is 1.0f.
        unary("floatToInt()V", 1065353216, "I", 1, "I"),
        // 5.9f's bits are 0x40BCCCCD (1086115021).
        unary("intToFloat()V", 5.9f, "F", 1086115021.toFloat(), "F"),
        // 5.9's bits are 0x401799999999999A; long-to-int keeps the low word.
        unary("longToInt()V", 5.9, "D", 0x9999999A.toInt(), "I"),
    )

    private fun unary(method: String, source: Any, sourceType: String, result: Any, resultType: String) =
        DynamicTest.dynamicTest("$method $source:$sourceType -> $result:$resultType") {
            val initial = CoreState().apply { setRegisters(0, source, sourceType) }
            val expected = CoreState().apply { setRegisters(0, result, resultType) }
            CoreTester.test(CLASS_NAME, method, initial, expected)
        }

    private companion object {
        const val CLASS_NAME = "Lunary_math_test;"
    }
}
