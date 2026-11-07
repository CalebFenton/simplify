package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

/**
 * Each reader answers the bits a device reads at its width. Expected values come from two's-complement
 * truncation and the JDK's raw-bits functions (`Float.floatToRawIntBits`, `Double.doubleToRawLongBits`), not
 * from the engine.
 */
class CoerceTest {
    @Test
    fun aThirtyTwoBitReadAnswersTheRegistersBits() {
        assertEquals(5, Coerce.toInt(5))
        assertEquals(5, Coerce.toInt(5.toByte()))
        assertEquals(-1, Coerce.toInt((-1).toShort()))
        assertEquals(0x62, Coerce.toInt('b'))
        assertEquals(1, Coerce.toInt(true))
        assertEquals(0, Coerce.toInt(false))
        // A Long is a register pair; a 32-bit read sees its low word, so it truncates rather than saturates.
        assertEquals(-1, Coerce.toInt(0xFFFFFFFFL))
        assertEquals(0, Coerce.toInt(0x100000000L))
        assertEquals(1, Coerce.toInt(0x100000001L))
        // An IEEE raw supplies its bit pattern: 5.9f is 0x40BCCCCD and -0.5f is 0xBF000000, where a [JLS] §5.1.3
        // value conversion would answer 5 and 0.
        assertEquals(1086115021, Coerce.toInt(5.9f))
        assertEquals(-1090519040, Coerce.toInt(-0.5f))
        assertEquals(0x7fc00000, Coerce.toInt(Float.NaN))
        // 5.9's bits are 0x401799999999999A and 0.5's are 0x3FE0000000000000; the low words are read.
        assertEquals(0x9999999A.toInt(), Coerce.toInt(5.9))
        assertEquals(0, Coerce.toInt(0.5))
    }

    /**
     * A 64-bit read needs both registers of the pair. A `Long` or `Double` raw supplies all 64 bits; a 32-bit
     * raw supplies only its own register, and the other half is `register + 1`, which this call cannot see.
     */
    @Test
    fun aSixtyFourBitReadAnswersThePairsBitsOrNothing() {
        assertEquals(0xFFFFFFFFL, Coerce.toLong(0xFFFFFFFFL))
        assertEquals(0x401799999999999AL, Coerce.toLong(5.9))
        assertEquals(Long.MIN_VALUE, Coerce.toLong(-0.0))
        for (narrow in listOf<Any>(-3, 5.toByte(), 5.toShort(), 'b', true, 5.9f)) {
            assertNull(Coerce.toLong(narrow), "$narrow")
        }
    }

    @Test
    fun aRegistersBitsAreWhatAFloatWidthReadAnswers() {
        // A float literal as a compiler emits it: `const v0, 0x3f800000` -> Known(1065353216, "I").
        assertEquals(1.0f, Coerce.toFloat(1065353216))
        assertEquals(java.lang.Float.intBitsToFloat(5), Coerce.toFloat(5))
        assertEquals(java.lang.Float.intBitsToFloat(0x62), Coerce.toFloat('b'))
        assertEquals(java.lang.Float.intBitsToFloat(1), Coerce.toFloat(true))
        assertTrue(Coerce.toFloat(-1)!!.isNaN())
        assertEquals(5.9f, Coerce.toFloat(5.9f))
        // A Double read at 32 bits is its low word, and -2.0's low word is zero.
        assertEquals(0f, Coerce.toFloat(-2.0))
    }

    @Test
    fun aRegisterPairsBitsAreWhatADoubleWidthReadAnswers() {
        // A double literal as a compiler emits it: `const-wide v0, 0x4000000000000000` -> Known(..., "J").
        assertEquals(2.0, Coerce.toDouble(4611686018427387904L))
        assertEquals(-2.0, Coerce.toDouble(-4611686018427387904L))
        assertEquals(5.9, Coerce.toDouble(5.9))
        for (narrow in listOf<Any>(5, true, 'b', 5.9f)) {
            assertNull(Coerce.toDouble(narrow), "$narrow")
        }
    }

    /**
     * A register holds one of eight boxes: `Byte`, `Short`, `Int`, `Long`, `Char`, `Boolean`, `Float`, `Double`.
     * Anything else, numeric or not, is not a register value, and every reader answers null so the caller
     * writes `Unknown` instead of a number the box's own conversion invented (`BigDecimal("7.9").toInt()` is 7).
     */
    @Test
    fun onlyTheEightRegisterBoxesAreReadable() {
        for (raw in listOf(null, "42", Any(), BigDecimal("7.9"), BigInteger.TEN, AtomicInteger(7))) {
            assertNull(Coerce.toInt(raw), "toInt($raw)")
            assertNull(Coerce.toLong(raw), "toLong($raw)")
            assertNull(Coerce.toFloat(raw), "toFloat($raw)")
            assertNull(Coerce.toDouble(raw), "toDouble($raw)")
        }
    }
}
