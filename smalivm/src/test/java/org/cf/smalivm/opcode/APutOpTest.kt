package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.VMTester
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.type.UnknownValue
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class APutOpTest {
    private lateinit var initial: TestState
    private lateinit var expected: TestState

    companion object {
        private const val CLASS_NAME = "Laput_test;"
    }

    @BeforeEach
    fun setUp() {
        initial = TestState()
        expected = TestState()
    }

    @Test
    fun canInsertLocalClassAndClassIntoSameArray() {
        val valueType: String = CommonTypes.CLASS
        val arrayType = "[$valueType"
        val array: Array<Class<*>?> = arrayOfNulls(2)
        val index1 = 0
        val index2 = 1
        val value1: Class<*> = String::class.java
        val binaryClassName: String = ClassNameUtils.internalToBinary(CLASS_NAME)
        val classLoader: ClassLoader = VMTester.spawnVM().classLoader
        val value2 = classLoader.loadClass(binaryClassName)
        initial.setRegisters(0, array, arrayType, 1, index1, "I", 2, value1, valueType, 3, index2, "I", 4, value2, valueType)
        expected.setRegisters(0, arrayOf(value1, value2), arrayType)
        VMTester.test(CLASS_NAME, "putObjects()V", initial, expected)
    }

    @Test
    fun canArrayIntoObjectArray() {
        initial.setRegisters(0, arrayOfNulls<Any>(1), "[" + CommonTypes.OBJECT, 1, 0, "I", 2, ByteArray(1), "[B")
        expected.setRegisters(0, arrayOf<Any>(ByteArray(1)), "[" + CommonTypes.OBJECT)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutBoolean() {
        initial.setRegisters(0, BooleanArray(1), "[Z", 1, 0, "I", 2, 0x1, "Z")
        expected.setRegisters(0, booleanArrayOf(true), "[Z")
        VMTester.test(CLASS_NAME, "putBoolean()V", initial, expected)
    }

    @Test
    fun canPutBooleanWithShortValue() {
        val value: Short = 0x1
        initial.setRegisters(0, BooleanArray(1), "[Z", 1, 0, "I", 2, value, "Z")
        expected.setRegisters(0, booleanArrayOf(true), "[Z")
        VMTester.test(CLASS_NAME, "putBoolean()V", initial, expected)
    }

    @Test
    fun canPutByte() {
        val value: Byte = 0xf
        initial.setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, value, "B")
        expected.setRegisters(0, byteArrayOf(value), "[B")
        VMTester.test(CLASS_NAME, "putByte()V", initial, expected)
    }

    @Test
    fun canPutByteFromInt() {
        val value = 0xf
        initial.setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, value, "B")
        expected.setRegisters(0, byteArrayOf(value.toByte()), "[B")
        VMTester.test(CLASS_NAME, "putByte()V", initial, expected)
    }

    @Test
    fun canPutChar() {
        initial.setRegisters(0, CharArray(1), "[C", 1, 0, "I", 2, '$', "C")
        expected.setRegisters(0, charArrayOf('$'), "[C")
        VMTester.test(CLASS_NAME, "putChar()V", initial, expected)
    }

    @Test
    fun canPutCharFromInt() {
        initial.setRegisters(0, CharArray(1), "[C", 1, 0, "I", 2, '$'.toInt(), "I")
        expected.setRegisters(0, charArrayOf('$'), "[C")
        VMTester.test(CLASS_NAME, "putChar()V", initial, expected)
    }

    @Test
    fun canPutConstZeroNullObject() {
        val valueType = "I"
        val arrayType = "[$valueType"
        val array: Array<String?> = arrayOfNulls(1)
        val index = 0
        val value = 0
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf<String?>(null), arrayType)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutIntegerWithShortIndex() {
        val index: Short = 0
        initial.setRegisters(0, IntArray(1), "[I", 1, index, "S", 2, 4, "I")
        expected.setRegisters(0, intArrayOf(4), "[I")
        VMTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun canPutStringInStringArray() {
        val valueType: String = CommonTypes.STRING
        val arrayType = "[$valueType"
        val array = arrayOfNulls<String>(1)
        val index = 0
        val value = "Arrakis, Dune, desert planet..."
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf(value), arrayType)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutStringInObjectArray() {
        val valueType: String = CommonTypes.STRING
        val arrayType = "[" + CommonTypes.OBJECT
        val array: Array<String?> = arrayOfNulls(1)
        val index = 0
        val value = "Arrakis, Dune, desert planet..."
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        expected.setRegisters(0, arrayOf(value), arrayType)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutStringArrayIn2DStringArray() {
        val valueType = "[" + CommonTypes.STRING
        val arrayType = "[$valueType"
        val array: Array<Array<String?>?> = arrayOfNulls(1)
        val index = 0
        val value = arrayOf<String?>("Arrakis, Dune, desert planet...")
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        val expectedValue: Array<Array<String?>?> = arrayOfNulls(1)
        expectedValue[0] = value
        expected.setRegisters(0, expectedValue, arrayType)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutStringArrayIn2DObjectArray() {
        val valueType = "[" + CommonTypes.STRING
        val arrayType = "[[" + CommonTypes.OBJECT
        val array: Array<Array<String?>?> = arrayOfNulls(1)
        val index = 0
        val value = arrayOf<String?>("Arrakis, Dune, desert planet...")
        initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType)
        val expectedValue: Array<Array<String?>?> = arrayOfNulls(1)
        expectedValue[0] = value
        expected.setRegisters(0, expectedValue, arrayType)
        VMTester.test(CLASS_NAME, "putObject()V", initial, expected)
    }

    @Test
    fun canPutShort() {
        val value: Short = 0x42
        initial.setRegisters(0, ShortArray(1), "[S", 1, 0, "I", 2, value, "S")
        expected.setRegisters(0, shortArrayOf(value), "[S")
        VMTester.test(CLASS_NAME, "putShort()V", initial, expected)
    }

    @Test
    fun canPutShortWithIntegerValue() {
        val value = 0x42
        initial.setRegisters(0, ShortArray(1), "[S", 1, 0, "I", 2, value, "I")
        expected.setRegisters(0, shortArrayOf(value.toShort()), "[S")
        VMTester.test(CLASS_NAME, "putShort()V", initial, expected)
    }

    @Test
    fun canPutIntoUnknownValueOfObjectTypeWithoutThrowingException() {
        initial.setRegisters(0, UnknownValue(), CommonTypes.OBJECT, 1, 0, "I", 2, 0x5, "I")
        expected.setRegisters(0, UnknownValue(), CommonTypes.OBJECT)
        VMTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun canPutUnknownValue() {
        // TODO: Ideally, setting an element unknown shouldn't set entire array unknown.
        // This is tricky to handle gracefully. See APutOp for more details.
        initial.setRegisters(0, IntArray(1), "[I", 1, 0, "I", 2, UnknownValue(), "I")
        expected.setRegisters(0, UnknownValue(), "[I")
        VMTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun canPutWideWithDouble() {
        val value = 100000000000.0
        initial.setRegisters(0, DoubleArray(1), "[D", 1, 0, "I", 2, value, "D")
        expected.setRegisters(0, doubleArrayOf(value), "[D")
        VMTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    @Test
    fun canPutWideWithFloat() {
        val value = 10.45f
        initial.setRegisters(0, FloatArray(1), "[F", 1, 0, "I", 2, value, "F")
        expected.setRegisters(0, floatArrayOf(value), "[F")
        VMTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    @Test
    fun canPutWideWithLong() {
        val value = 10000000000L
        initial.setRegisters(0, LongArray(1), "[J", 1, 0, "I", 2, value, "J")
        expected.setRegisters(0, longArrayOf(value), "[J")
        VMTester.test(CLASS_NAME, "putWide()V", initial, expected)
    }

    @Test
    fun canPutWithInteger() {
        initial.setRegisters(0, IntArray(1), "[I", 1, 0, "I", 2, 4, "I")
        expected.setRegisters(0, intArrayOf(4), "[I")
        VMTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun canPutWithUnknownIndex() {
        initial.setRegisters(0, IntArray(1), "[I", 1, UnknownValue(), "I", 2, 5, "I")
        expected.setRegisters(0, UnknownValue(), "[I")
        VMTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    @Test
    fun nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I", 2, 0, "I")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", NullPointerException::class.java, initial, 2)
    }

    @Test
    fun outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, 0, "I")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", ArrayIndexOutOfBoundsException::class.java, initial, 2)
    }

    @Test
    fun incompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", ArrayStoreException::class.java, initial, 2)
    }

    @Test
    fun outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, "wrong type", "Ljava/lang/String;")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", ArrayStoreException::class.java, initial, 2)
    }

    @Test
    fun unknownValueItemWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I", 2, UnknownValue(), "Ljava/lang/String;")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", ArrayStoreException::class.java, initial, 2)
    }

    @Test
    fun unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, UnknownValue(), "[I", 1, 0, "I", 2, "wrong type", "Ljava/lang/String;")
        VMTester.testSimpleException(CLASS_NAME, "putWithCatch()V", ArrayStoreException::class.java, initial, 2)
    }

    @Test
    fun unknownValueItemMakesArrayUnknownAndThrowsExceptions() {
        initial.setRegisters(0, IntArray(5), "[I", 1, 0, "I", 2, UnknownValue(), "I")
        val graph = VMTester.execute(CLASS_NAME, "putWithCatch()V", initial)
        val putNode = graph.getNodePile(0)[0]
        assertEquals(3, putNode.children.size)

        val exceptions = putNode.children.mapNotNull { it.state.peekExceptionRegister() }
        assertEquals(2, exceptions.size)

        val exceptionClasses = exceptions.map { it.raw!!.javaClass }
        val expectedClasses = listOf(ArrayIndexOutOfBoundsException::class.java, NullPointerException::class.java)
        assertTrue(exceptionClasses.containsAll(expectedClasses))
    }
}
