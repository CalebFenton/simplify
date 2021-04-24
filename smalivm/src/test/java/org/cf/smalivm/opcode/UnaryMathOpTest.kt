package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.experimental.inv

class UnaryMathOpTest {
    companion object {
        private val CLASS_NAME = "Lunary_math_test;"
    }

    @Nested
    inner class DoubleValue {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun testDoubleToFloat() {
            initial.setRegisters(0, 220.0, "D")
            expected.setRegisters(0, 220f, "F")
            Tester.test(CLASS_NAME, "doubleToFloat()V", initial, expected)
        }

        @Test
        fun testDoubleToInt() {
            initial.setRegisters(0, 200.0, "D")
            expected.setRegisters(0, 200, "I")
            Tester.test(CLASS_NAME, "doubleToInt()V", initial, expected)
        }

        @Test
        fun testDoubleToLong() {
            initial.setRegisters(0, 210.0, "D")
            expected.setRegisters(0, 210L, "J")
            Tester.test(CLASS_NAME, "doubleToLong()V", initial, expected)
        }

        @Test
        fun testNegDouble() {
            initial.setRegisters(0, 15.1, "D")
            expected.setRegisters(0, -15.1, "D")
            Tester.test(CLASS_NAME, "negDouble()V", initial, expected)
        }
    }

    @Nested
    inner class FloatValue {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun testFloatToDouble() {
            initial.setRegisters(0, 120f, "F")
            expected.setRegisters(0, 120.0, "D")
            Tester.test(CLASS_NAME, "floatToDouble()V", initial, expected)
        }

        @Test
        fun testFloatToInt() {
            initial.setRegisters(0, 100f, "F")
            expected.setRegisters(0, 100, "I")
            Tester.test(CLASS_NAME, "floatToInt()V", initial, expected)
        }

        @Test
        fun testFloatToLong() {
            initial.setRegisters(0, 110f, "F")
            expected.setRegisters(0, 110L, "J")
            Tester.test(CLASS_NAME, "floatToLong()V", initial, expected)
        }

        @Test
        fun testNegFloat() {
            initial.setRegisters(0, 10.5f, "F")
            expected.setRegisters(0, -10.5f, "F")
            Tester.test(CLASS_NAME, "negFloat()V", initial, expected)
        }
    }

    @Nested
    inner class IntValue {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun testIntToByte() {
            val value = 128
            initial.setRegisters(0, value, "I")
            expected.setRegisters(0, value.toByte(), "B")
            Tester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testIntToByteWithShort() {
            val value: Short = 1000
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.toByte(), "B")
            Tester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testIntToChar() {
            initial.setRegisters(0, 0x62, "I")
            expected.setRegisters(0, 0x62.toChar(), "C")
            Tester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testIntToCharWithShort() {
            val value: Short = 0x62
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.toInt().toChar(), "C")
            Tester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testIntToDouble() {
            initial.setRegisters(0, 13, "I")
            expected.setRegisters(0, 13.0, "D")
            Tester.test(CLASS_NAME, "intToDouble()V", initial, expected)
        }

        @Test
        fun testIntToFloat() {
            initial.setRegisters(0, 12, "I")
            expected.setRegisters(0, 12f, "F")
            Tester.test(CLASS_NAME, "intToFloat()V", initial, expected)
        }

        @Test
        fun testIntToLong() {
            initial.setRegisters(0, 11, "I")
            expected.setRegisters(0, 11L, "J")
            Tester.test(CLASS_NAME, "intToLong()V", initial, expected)
        }

        @Test
        fun testIntToShort() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 0x10.toShort(), "S")
            Tester.test(CLASS_NAME, "intToShort()V", initial, expected)
        }

        @Test
        fun testNegInt() {
            initial.setRegisters(0, 0x42, "I")
            expected.setRegisters(0, -0x42, "I")
            Tester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNegIntWithShort() {
            val value: Short = 0x42
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, -value, "I")
            Tester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNegUnknownInt() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "negInt()V", initial, expected)
        }

        @Test
        fun testNotInt() {
            initial.setRegisters(0, 0x42, "I")
            expected.setRegisters(0, 0x42.inv(), "I")
            Tester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testNotIntWithShort() {
            val value: Short = 0x42
            initial.setRegisters(0, value, "S")
            expected.setRegisters(0, value.inv().toInt(), "I")
            Tester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testNotUnknownInt() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "notInt()V", initial, expected)
        }

        @Test
        fun testUnknownIntToByte() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "B")
            Tester.test(CLASS_NAME, "intToByte()V", initial, expected)
        }

        @Test
        fun testUnknownIntToChar() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "C")
            Tester.test(CLASS_NAME, "intToChar()V", initial, expected)
        }

        @Test
        fun testUnknownIntToDouble() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "D")
            Tester.test(CLASS_NAME, "intToDouble()V", initial, expected)
        }

        @Test
        fun testUnknownIntToFloat() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "F")
            Tester.test(CLASS_NAME, "intToFloat()V", initial, expected)
        }

        @Test
        fun testUnknownIntToLong() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "J")
            Tester.test(CLASS_NAME, "intToLong()V", initial, expected)
        }

        @Test
        fun testUnknownIntToShort() {
            initial.setRegisters(0, UnknownValue(), "I")
            expected.setRegisters(0, UnknownValue(), "S")
            Tester.test(CLASS_NAME, "intToShort()V", initial, expected)
        }
    }

    @Nested
    inner class LongValue {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun testLongToDouble() {
            initial.setRegisters(0, 23L, "J")
            expected.setRegisters(0, 23.0, "D")
            Tester.test(CLASS_NAME, "longToDouble()V", initial, expected)
        }

        @Test
        fun testLongToFloat() {
            initial.setRegisters(0, 22L, "J")
            expected.setRegisters(0, 22f, "F")
            Tester.test(CLASS_NAME, "longToFloat()V", initial, expected)
        }

        @Test
        fun testLongToInt() {
            initial.setRegisters(0, 21L, "J")
            expected.setRegisters(0, 21, "I")
            Tester.test(CLASS_NAME, "longToInt()V", initial, expected)
        }

        @Test
        fun testNegLong() {
            initial.setRegisters(0, 0x100L, "J")
            expected.setRegisters(0, -0x100L, "J")
            Tester.test(CLASS_NAME, "negLong()V", initial, expected)
        }

        @Test
        fun testNotLong() {
            initial.setRegisters(0, 100L, "J")
            expected.setRegisters(0, 100L.inv(), "J")
            Tester.test(CLASS_NAME, "notLong()V", initial, expected)
        }
    }

//    @ExtendWith(MockitoExtension::class)
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    class UnitTest {
//        private var addressToLocation: TIntObjectMap<MethodLocation>? = null
//        private var instruction: BuilderInstruction? = null
//        private var item: HeapItem? = null
//        private var location: MethodLocation? = null
//        private var mState: MethodState? = null
//        private var node: ExecutionNode? = null
//        private var op: UnaryMathOp? = null
//        private var opFactory: UnaryMathOpFactory? = null
//        private var vm: VirtualMachine? = null
//
//        @Test
//        fun canDoubleToFloat() {
//            val value = 11204.0345612345
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("D")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_FLOAT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toFloat(), "F")))
//            Assertions.assertEquals("double-to-float r" + REGISTER_A + ", r" + REGISTER_B, op.toString())
//        }
//
//        @Test
//        fun canDoubleToInt() {
//            val value = 11204.0345612345
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("D")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_INT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toInt(), "I")))
//        }
//
//        @Test
//        fun canDoubleToLong() {
//            val value = 11204.0345612345
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("D")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toLong(), "J")))
//        }
//
//        @Test
//        fun canFloatToDouble() {
//            val value = 11204.0345f
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("F")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_DOUBLE)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toDouble(), "D")))
//        }
//
//        @Test
//        fun canFloatToInt() {
//            val value = 11204.0345f
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("F")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_INT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toInt(), "I")))
//        }
//
//        @Test
//        fun canFloatToLong() {
//            val value = 11204.0345f
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("F")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toLong(), "J")))
//        }
//
//        @Test
//        fun canIntToByte() {
//            val value = 10
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("I")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.INT_TO_BYTE)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toByte(), "B")))
//        }
//
//        @Test
//        fun canLongToDouble() {
//            val value = 112040345L
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("J")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_DOUBLE)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toDouble(), "D")))
//        }
//
//        @Test
//        fun canLongToFloat() {
//            val value = 112040345L
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("J")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_FLOAT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toFloat(), "F")))
//        }
//
//        @Test
//        fun canLongToInt() {
//            val value = 112040345L
//            Mockito.`when`(item.getValue()).thenReturn(value)
//            Mockito.`when`(item.getType()).thenReturn("J")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_INT)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1))
//                .assignRegister(ArgumentMatchers.eq(REGISTER_A), ArgumentMatchers.eq(HeapItem(value.toInt(), "I")))
//        }
//
//        @BeforeEach
//        fun setUp() {
//            vm = Mockito.mock(VirtualMachineImpl::class.java)
//            mState = Mockito.mock(MethodState::class.java)
//            node = Mockito.mock(ExecutionNode::class.java)
//            item = Mockito.mock(HeapItem::class.java)
//            Mockito.`when`(mState.readRegister(REGISTER_B)).thenReturn(item)
//            location = Mockito.mock<MethodLocation>(MethodLocation::class.java)
//            instruction =
//                Mockito.mock<BuilderInstruction>(BuilderInstruction::class.java, Mockito.withSettings().extraInterfaces(Instruction12x::class.java))
//            Mockito.`when`<Instruction>(location.getInstruction()).thenReturn(instruction)
//            Mockito.`when`<Int>(location.getCodeAddress()).thenReturn(ADDRESS)
//            Mockito.`when`<MethodLocation>(instruction.getLocation()).thenReturn(location)
//            Mockito.`when`<Int>(instruction.getCodeUnits()).thenReturn(0)
//            Mockito.`when`<Int>((instruction as Instruction12x?).getRegisterA()).thenReturn(REGISTER_A)
//            Mockito.`when`<Int>((instruction as Instruction12x?).getRegisterB()).thenReturn(REGISTER_B)
//            addressToLocation = TIntObjectHashMap<MethodLocation>()
//            addressToLocation.put(ADDRESS, location)
//            opFactory = UnaryMathOpFactory()
//        }
//
//        companion object {
//            private const val ADDRESS = 0
//            private const val REGISTER_A = 0
//            private const val REGISTER_B = 0
//        }
//    }
}
