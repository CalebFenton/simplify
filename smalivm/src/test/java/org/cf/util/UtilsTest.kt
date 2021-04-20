package org.cf.util

import org.jf.dexlib2.writer.builder.BuilderTypeList
import org.jf.dexlib2.writer.builder.BuilderTypeReference
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.lang.Boolean
import java.util.*
import kotlin.Array
import kotlin.IntArray
import kotlin.String
import kotlin.Throws
import kotlin.Unit
import kotlin.arrayOfNulls

class UtilsTest {
    private var rng: Random? = null

    @BeforeEach
    fun setUp() {
        rng = Random()
    }

    @get:Test
    val arrayDimensionStringWithNonArrayObjectReturnsEmptyString: Unit
        get() {
            val value = "some string"
            val expected = ""
            val actual = Utils.getArrayDimensionString(value)
            Assertions.assertEquals(expected, actual)
        }

    @get:Test
    val arrayDimensionStringWith1DArrayReturnsExpected: Unit
        get() {
            val value = IntArray(5)
            val expected = "[5]"
            val actual = Utils.getArrayDimensionString(value)
            Assertions.assertEquals(expected, actual)
        }

    @get:Test
    val arrayDimensionStringWith3DArrayReturnsExpected: Unit
        get() {
            val value = Array(5) { Array(4) { arrayOfNulls<String>(3) } }
            val expected = "[5][4][3]"
            val actual = Utils.getArrayDimensionString(value)
            Assertions.assertEquals(expected, actual)
        }

    @Test
    @Throws(ClassNotFoundException::class)
    fun buildArrayOf1DPrimitiveClassArrayReturnsExpected() {
        val arrayType = "[I"
        val length = 1 + rng!!.nextInt(100)
        val expected = IntArray(length)
        val actual = Utils.buildArray(arrayType, length)
        Assertions.assertArrayEquals(expected, actual as IntArray)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun buildArrayOf2DPrimitiveClassArrayReturnsExpected() {
        val arrayType = "[[I"
        val length = 1 + rng!!.nextInt(100)
        val expected = arrayOfNulls<IntArray>(length)
        val actual = Utils.buildArray(arrayType, length)
        Assertions.assertArrayEquals(expected, actual as Array<IntArray?>)
    }

    @Test
    @Throws(ClassNotFoundException::class)
    fun buildArrayOf3DNonLocalClassArrayReturnsExpected() {
        val arrayType = "[[[Ljava/lang/String;"
        val length = 1 + rng!!.nextInt(100)
        val expected: Array<Array<Array<String>>?> = arrayOfNulls(length)
        val actual = Utils.buildArray(arrayType, length)
        Assertions.assertArrayEquals(expected, actual as Array<Array<Array<String>>?>)
    }

    @Test
    fun registerSizeWithEmptyListReturnsZero() {
        val typeNames: List<String> = LinkedList()
        val expected = 0
        val actual = Utils.getRegisterSize(typeNames)
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun registerSizeWithTwoOneRegisterTypesReturnsTwo() {
        val typeNames: MutableList<String> = LinkedList()
        typeNames.add("I") // 1
        typeNames.add("J") // 2
        typeNames.add("D") // 2
        val expected = 5
        val actual = Utils.getRegisterSize(typeNames)
        Assertions.assertEquals(expected, actual)
    }

//    @Test
//    fun shiftIntegerMapKeysShiftsUpAsExpected() {
//        val intToObject: TIntObjectMap<String> = TIntObjectHashMap()
//        intToObject.put(1, "one")
//        intToObject.put(2, "two")
//        intToObject.put(3, "three")
//        Utils.shiftIntegerMapKeys(1, 2, intToObject)
//        Assertions.assertEquals(3, intToObject.size())
//        Assertions.assertEquals("one", intToObject[1])
//        Assertions.assertEquals("two", intToObject[4])
//        Assertions.assertEquals("three", intToObject[5])
//    }
//
//    @Test
//    fun shiftIntegerMapKeysRemovesWhenShiftingDown() {
//        val intToObject: TIntObjectMap<String> = TIntObjectHashMap()
//        intToObject.put(1, "one")
//        intToObject.put(2, "two")
//        intToObject.put(3, "three")
//        Utils.shiftIntegerMapKeys(1, -1, intToObject)
//        Assertions.assertEquals(2, intToObject.size())
//        Assertions.assertEquals("two", intToObject[1])
//        Assertions.assertEquals("three", intToObject[2])
//    }

    @Test
    fun builderTypeListToStringListReturnsExpected() {
        val types: MutableList<BuilderTypeReference> = LinkedList()
        val typeRef1 = Mockito.mock(BuilderTypeReference::class.java)
        val typeRef2 = Mockito.mock(BuilderTypeReference::class.java)
        Mockito.`when`(typeRef1.type).thenReturn("typeRef1")
        Mockito.`when`(typeRef2.type).thenReturn("typeRef2")
        types.add(typeRef1)
        types.add(typeRef2)
        val typeList = BuilderTypeList(types)
        val typeNames = Utils.builderTypeListToTypeNames(typeList)
        Assertions.assertEquals(2, typeNames.size)
        Assertions.assertEquals("typeRef1", typeNames[0])
        Assertions.assertEquals("typeRef2", typeNames[1])
    }

    @Test
    fun registerSizeWithBuilderTypeListReturnsExpectedValue() {
        val types: MutableList<BuilderTypeReference> = LinkedList()
        val typeRef1 = Mockito.mock(BuilderTypeReference::class.java)
        val typeRef2 = Mockito.mock(BuilderTypeReference::class.java)
        Mockito.`when`(typeRef1.type).thenReturn("I")
        Mockito.`when`(typeRef2.type).thenReturn("J")
        types.add(typeRef1)
        types.add(typeRef2)
        val typeList = BuilderTypeList(types)
        val registerSize = Utils.getRegisterSize(typeList)
        Assertions.assertEquals(3, registerSize)
    }

    @Test
    fun castToPrimitiveWithBooleanReturnsExpectedValues() {
        Assertions.assertEquals(true, Utils.castToPrimitive(Boolean.TRUE, "Z"))
        Assertions.assertEquals(false, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Boolean;"))
        Assertions.assertEquals(1.toByte(), Utils.castToPrimitive(Boolean.TRUE, "B"))
        Assertions.assertEquals(0.toByte(), Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Byte;"))
        Assertions.assertEquals(1, Utils.castToPrimitive(Boolean.TRUE, "I"))
        Assertions.assertEquals(0, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Integer;"))
        Assertions.assertEquals(1.toShort(), Utils.castToPrimitive(Boolean.TRUE, "S"))
        Assertions.assertEquals(0.toShort(), Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Short;"))
    }

    @Test
    fun castToPrimitiveWithCharacterReturnsExpectedValues() {
        val value1 = 'a'
        val value2 = 'b'
        Assertions.assertEquals(value1, Utils.castToPrimitive(value1, "C"))
        Assertions.assertEquals(value2, Utils.castToPrimitive(Character.valueOf(value2), "Ljava/lang/Character;"))
        Assertions.assertEquals(true, Utils.castToPrimitive(1.toChar(), "Z"))
        Assertions.assertEquals(false, Utils.castToPrimitive(0.toChar(), "Ljava/lang/Boolean;"))
        Assertions.assertEquals(value1.toByte(), Utils.castToPrimitive(value1, "B"))
        Assertions.assertEquals(value2.toByte(), Utils.castToPrimitive(value2, "Ljava/lang/Byte;"))
        Assertions.assertEquals(value1.toInt(), Utils.castToPrimitive(value1, "I"))
        Assertions.assertEquals(value2.toInt(), Utils.castToPrimitive(value2, "Ljava/lang/Integer;"))
        Assertions.assertEquals(value1.toShort(), Utils.castToPrimitive(value1, "S"))
        Assertions.assertEquals(value2.toShort(), Utils.castToPrimitive(value2, "Ljava/lang/Short;"))
    }

    @Test
    fun castToPrimitiveWithNumberReturnsExpectedValues() {
        val value1 = 0
        val value2 = 1
        Assertions.assertEquals(value1.toByte(), Utils.castToPrimitive(value1.toByte(), "B"))
        Assertions.assertEquals(value2.toByte(), Utils.castToPrimitive(value2.toByte(), "Ljava/lang/Byte;"))
        Assertions.assertEquals(value1.toDouble(), Utils.castToPrimitive(value1.toDouble(), "D"))
        Assertions.assertEquals(value2.toDouble(), Utils.castToPrimitive(value2.toDouble(), "Ljava/lang/Double;"))
        Assertions.assertEquals(value1.toFloat(), Utils.castToPrimitive(value1.toFloat(), "F"))
        Assertions.assertEquals(value2.toFloat(), Utils.castToPrimitive(value2.toFloat(), "Ljava/lang/Float;"))
        Assertions.assertEquals(value1, Utils.castToPrimitive(value1, "I"))
        Assertions.assertEquals(value2, Utils.castToPrimitive(value2, "Ljava/lang/Integer;"))
        Assertions.assertEquals(value1.toLong(), Utils.castToPrimitive(value1.toLong(), "J"))
        Assertions.assertEquals(value2.toLong(), Utils.castToPrimitive(value2.toLong(), "Ljava/lang/Long;"))
        Assertions.assertEquals(value1.toShort(), Utils.castToPrimitive(value1.toShort(), "S"))
        Assertions.assertEquals(value2.toShort(), Utils.castToPrimitive(value2.toShort(), "Ljava/lang/Short;"))
        Assertions.assertEquals(value1.toChar(), Utils.castToPrimitive(value1, "C"))
        Assertions.assertEquals(value2.toChar(), Utils.castToPrimitive(value2, "Ljava/lang/Character;"))
        Assertions.assertEquals(true, Utils.castToPrimitive(1, "Z"))
        Assertions.assertEquals(false, Utils.castToPrimitive(0, "Ljava/lang/Boolean;"))
    }
}
