//package org.cf.smalivm.opcode
//
//import org.cf.smalivm.VMState
//import org.cf.smalivm.VMTester
//import org.cf.smalivm.context.HeapItem
//import org.cf.smalivm.type.UnknownValue
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class AGetOpTest {
//    private lateinit var expected: VMState
//    private lateinit var initial: VMState
//    @Test
//    fun canGet() {
//        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, 0, "I")
//        expected.setRegisters(0, 0x42, "I")
//        VMTester.test(CLASS_NAME, "get()V", initial, expected)
//    }
//
//    @Test
//    fun canGetBoolean() {
//        initial.setRegisters(0, booleanArrayOf(true), "[Z", 1, 0, "I")
//        expected.setRegisters(0, true, "Z")
//        VMTester.test(CLASS_NAME, "getBoolean()V", initial, expected)
//    }
//
//    @Test
//    fun canGetByte() {
//        initial.setRegisters(0, byteArrayOf(0xe), "[B", 1, 0, "I")
//        expected.setRegisters(0, 0xe.toByte(), "B")
//        VMTester.test(CLASS_NAME, "getByte()V", initial, expected)
//    }
//
//    @Test
//    fun canGetChar() {
//        initial.setRegisters(0, charArrayOf('a'), "[C", 1, 0, "I")
//        expected.setRegisters(0, 'a', "C")
//        VMTester.test(CLASS_NAME, "getChar()V", initial, expected)
//    }
//
//    @Test
//    fun canGetObject() {
//        val objectValue = "stringy"
//        val objectArray = arrayOf(objectValue)
//        val objectType = "Ljava/lang/String;"
//        initial.setRegisters(0, objectArray, objectType, 1, 0, "I")
//        expected.setRegisters(0, objectValue, objectType)
//        VMTester.test(CLASS_NAME, "getObject()V", initial, expected)
//    }
//
//    @Test
//    fun canGetShort() {
//        initial.setRegisters(0, shortArrayOf(0x42), "[S", 1, 0, "I")
//        expected.setRegisters(0, 0x42.toShort(), "S")
//        VMTester.test(CLASS_NAME, "getShort()V", initial, expected)
//    }
//
//    @Test
//    fun canGetUninitializedPrimitive() {
//        initial.setRegisters(0, IntArray(1), "[I", 1, 0, "I")
//        expected.setRegisters(0, IntArray(1)[0], "I")
//        VMTester.test(CLASS_NAME, "getUninitializedInt()V", initial, expected)
//    }
//
//    @Test
//    fun canGetUnknownArray() {
//        initial.setRegisters(0, UnknownValue(), "[I", 1, 0, "I")
//        expected.setRegisters(0, UnknownValue(), "I")
//        VMTester.test(CLASS_NAME, "get()V", initial, expected)
//    }
//
//    @Test
//    fun canGetUnknownElement() {
//        initial.setRegisters(0, arrayOf(UnknownValue(), 5), "[I", 1, 0, "I")
//        expected.setRegisters(0, UnknownValue(), "I")
//        VMTester.test(CLASS_NAME, "get()V", initial, expected)
//    }
//
//    @Test
//    fun canGetUnknownIndex() {
//        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, UnknownValue(), "I")
//        expected.setRegisters(0, UnknownValue(), "I")
//        VMTester.test(CLASS_NAME, "get()V", initial, expected)
//    }
//
//    @Test
//    fun canGetWide() {
//        initial.setRegisters(0, longArrayOf(0x10000000000L), "J", 1, 0, "I")
//        expected.setRegisters(0, 0x10000000000L, "J")
//        VMTester.test(CLASS_NAME, "getWide()V", initial, expected)
//    }
//
//    @Test
//    fun canGetWithShortIndex() {
//        val index: Short = 0
//        initial.setRegisters(0, intArrayOf(0x42), "[I", 1, index, "S")
//        expected.setRegisters(index.toInt(), 0x42, "I", 1, index, "S")
//        VMTester.test(CLASS_NAME, "get()V", initial, expected)
//    }
//
//    @Test
//    fun nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
//        initial.setRegisters(0, null, "[I", 1, 0, "I")
//        testException("getWithCatch()V", NullPointerException::class.java, initial)
//    }
//
//    @Test
//    fun outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
//        initial.setRegisters(0, IntArray(5), "[I", 1, 10, "I")
//        testException("getWithCatch()V", ArrayIndexOutOfBoundsException::class.java, initial)
//    }
//
//    @BeforeEach
//    fun setUp() {
//        initial = VMState()
//        expected = VMState()
//    }
//
//    companion object {
//        private const val CLASS_NAME = "Laget_test;"
//        private fun testException(methodDescriptor: String, exceptionClass: Class<*>, initial: VMState?) {
//            val graph: ExecutionGraph = VMTester.execute(CLASS_NAME, methodDescriptor, initial)
//            val item: HeapItem = graph.getTerminatingRegisterConsensus(0)
//            assertEquals(exceptionClass, item.getValue().getClass())
//            assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType())
//            Assertions.assertFalse(graph.wasAddressReached(2), "Should not reach next instruction in non-exception execution path")
//            val mState: MethodState = graph.getNodePile(0).get(0).getContext().getMethodState()
//            assertEquals(0, mState.getRegistersAssigned().length)
//        }
//    }
//}
