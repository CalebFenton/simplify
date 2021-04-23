package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CmpOpTest {

    companion object {
        private const val CLASS_NAME = "Lcmp_test;"
    }

    @Nested
    inner class CmpDouble {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @Test
        fun canCmpgDoubleWithEqual() {
            initial.setRegisters(0, 4.5, "D", 2, 4.5, "D")
            expected.setRegisters(0, 0, "I")
            Tester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5, "D", 2, 0.5, "D")
            expected.setRegisters(0, 1, "I")
            Tester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithLessThan() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, -1, "I")
            Tester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithNaN() {
            initial.setRegisters(0, 0.5, "D", 2, Double.NaN, "D")
            expected.setRegisters(0, 1, "I")
            Tester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmpgDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "D", 2, UnknownValue(), "D")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(Companion.CLASS_NAME, "cmpgDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithEqual() {
            initial.setRegisters(0, 4.5, "D", 2, 4.5, "D")
            expected.setRegisters(0, 0, "I")
            Tester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5, "D", 2, 0.5, "D")
            expected.setRegisters(0, 1, "I")
            Tester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithLessThan() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, -1, "I")
            Tester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithNaN() {
            initial.setRegisters(0, 0.5, "D", 2, Double.NaN, "D")
            expected.setRegisters(0, -1, "I")
            Tester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @Test
        fun canCmplDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "D", 2, UnknownValue(), "D")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(Companion.CLASS_NAME, "cmplDouble()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }
    }

    @Nested
    inner class FloatCmp {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canCmpgFloatWithEqual() {
            initial.setRegisters(0, 4.5f, "F", 1, 4.5f, "F")
            expected.setRegisters(0, 0, "I")
            Tester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 1, "I")
            Tester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithLessThan() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, -1, "I")
            Tester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithNaN() {
            initial.setRegisters(0, 0.5f, "F", 1, Float.NaN, "F")
            expected.setRegisters(0, 1, "I")
            Tester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmpgFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "F", 1, UnknownValue(), "F")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "cmpgFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithEqual() {
            initial.setRegisters(0, 4.5f, "F", 1, 4.5f, "F")
            expected.setRegisters(0, 0, "I")
            Tester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 1, "I")
            Tester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithLessThan() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, -1, "I")
            Tester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithNaN() {
            initial.setRegisters(0, 0.5f, "F", 1, Float.NaN, "F")
            expected.setRegisters(0, -1, "I")
            Tester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @Test
        fun canCmplFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5f, "F", 1, UnknownValue(), "F")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "cmplFloat()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }
    }

    @Nested
    inner class LongCmp {
        private lateinit var initial: TestState
        private lateinit var expected: TestState

        @Test
        fun canCmplLongWithEqual() {
            initial.setRegisters(0, 0x100L, "J", 2, 0x100L, "J")
            expected.setRegisters(0, 0, "I")
            Tester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmplLongWithGreaterThan() {
            initial.setRegisters(0, 0x100000L, "J", 2, 0x10L, "J")
            expected.setRegisters(0, 1, "I")
            Tester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmplLongWithUnknownArgument() {
            initial.setRegisters(0, 0x100L, "J", 2, UnknownValue(), "J")
            expected.setRegisters(0, UnknownValue(), "I")
            Tester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @Test
        fun canCmpLongWithLessThan() {
            initial.setRegisters(0, 0x10L, "J", 2, 0x100000L, "J")
            expected.setRegisters(0, -1, "I")
            Tester.test(CLASS_NAME, "cmpLong()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }
    }

//    @ExtendWith(MockitoExtension::class)
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    class UnitTest {
//        private var addressToLocation: TIntObjectMap<MethodLocation>? = null
//        private var instruction: BuilderInstruction? = null
//        private var itemB: HeapItem? = null
//        private var itemC: HeapItem? = null
//        private var location: MethodLocation? = null
//        private var mState: MethodState? = null
//        private var node: ExecutionNode? = null
//        private var op: CmpOp? = null
//        private var opFactory: CmpOpFactory? = null
//        private var setItem: ArgumentCaptor<HeapItem>? = null
//        private var vm: VirtualMachine? = null
//        @Test
//        fun canArg1IsUnknownReturnsUnknown() {
//            val value1: Any = UnknownValue()
//            Mockito.`when`(itemB.isUnknown()).thenReturn(true)
//            val value2 = 1149239296L
//            Mockito.`when`(itemB.getValue()).thenReturn(value1)
//            Mockito.`when`(itemC.getValue()).thenReturn(value2)
//            Mockito.`when`(itemB.getType()).thenReturn("J")
//            Mockito.`when`(itemC.getType()).thenReturn("J")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            val cmpValue: Any = UnknownValue()
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignRegister(ArgumentMatchers.eq(REGISTER_A), setItem.capture())
//            assertEquals(cmpValue.javaClass, setItem.getValue().getValue().getClass())
//            assertEquals(CMP_TYPE, setItem.getValue().getType())
//            Assertions.assertEquals("cmp-long r0, r2, r4", op.toString())
//        }
//
//        @Test
//        fun canCompareDouble() {
//            val value1 = 11204.0345612345
//            val value2 = 11492.3929612345
//            val cmpValue = java.lang.Double.compare(value1, value2)
//            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", cmpValue)
//        }
//
//        @Test
//        fun canCompareDoubleGreaterWithArg1NaN() {
//            val value1 = Double.NaN
//            val value2 = 11492.3929612345
//            doTest(value1, value2, Opcode.CMPG_DOUBLE, "D", 1)
//        }
//
//        @Test
//        fun canCompareDoubleLessWithArg1NaN() {
//            val value1 = Double.NaN
//            val value2 = 11492.3929612345
//            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", -1)
//        }
//
//        @Test
//        fun canCompareFloat() {
//            val value1 = 11204.03456f
//            val value2 = 11492.39296f
//            val cmpValue = java.lang.Float.compare(value1, value2)
//            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", cmpValue)
//        }
//
//        @Test
//        fun canCompareFloatGreaterWithArg1NaN() {
//            val value1 = Float.NaN
//            val value2 = 11492.39296f
//            doTest(value1, value2, Opcode.CMPG_FLOAT, "F", 1)
//        }
//
//        @Test
//        fun canCompareFloatLessWithArg1NaN() {
//            val value1 = Float.NaN
//            val value2 = 11492.39296f
//            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", -1)
//        }
//
//        @Test
//        fun canCompareLong() {
//            val value1 = 1120403456L
//            val value2 = 1149239296L
//            val cmpValue = java.lang.Long.compare(value1, value2)
//            doTest(value1, value2, Opcode.CMP_LONG, "J", cmpValue)
//        }
//
//        @BeforeEach
//        fun setUp() {
//            vm = Mockito.mock(VirtualMachineImpl::class.java)
//            mState = Mockito.mock(MethodState::class.java)
//            node = Mockito.mock(ExecutionNode::class.java)
//            setItem = ArgumentCaptor.forClass(HeapItem::class.java)
//            itemB = Mockito.mock(HeapItem::class.java)
//            Mockito.`when`(itemB.isUnknown()).thenReturn(false)
//            Mockito.`when`(mState.readRegister(REGISTER_B)).thenReturn(itemB)
//            itemC = Mockito.mock(HeapItem::class.java)
//            Mockito.`when`(itemC.isUnknown()).thenReturn(false)
//            Mockito.`when`(mState.readRegister(REGISTER_C)).thenReturn(itemC)
//            location = Mockito.mock<MethodLocation>(MethodLocation::class.java)
//            instruction =
//                Mockito.mock<BuilderInstruction>(BuilderInstruction::class.java, Mockito.withSettings().extraInterfaces(Instruction23x::class.java))
//            Mockito.`when`<Instruction>(location.getInstruction()).thenReturn(instruction)
//            Mockito.`when`<Int>(location.getCodeAddress()).thenReturn(ADDRESS)
//            Mockito.`when`<MethodLocation>(instruction.getLocation()).thenReturn(location)
//            Mockito.`when`<Int>(instruction.getCodeUnits()).thenReturn(0)
//            Mockito.`when`<Int>((instruction as Instruction23x?).getRegisterA()).thenReturn(REGISTER_A)
//            Mockito.`when`<Int>((instruction as Instruction23x?).getRegisterB()).thenReturn(REGISTER_B)
//            Mockito.`when`<Int>((instruction as Instruction23x?).getRegisterC()).thenReturn(REGISTER_C)
//            addressToLocation = TIntObjectHashMap<MethodLocation>()
//            addressToLocation.put(ADDRESS, location)
//            opFactory = CmpOpFactory()
//        }
//
//        @Test
//        fun testArg2IsUnknownReturnsUnknown() {
//            val value1 = 1149239296L
//            val value2: Any = UnknownValue()
//            Mockito.`when`(itemC.isUnknown()).thenReturn(true)
//            Mockito.`when`(itemB.getValue()).thenReturn(value1)
//            Mockito.`when`(itemC.getValue()).thenReturn(value2)
//            Mockito.`when`(itemB.getType()).thenReturn("J")
//            Mockito.`when`(itemC.getType()).thenReturn("J")
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            val cmpValue: Any = UnknownValue()
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignRegister(ArgumentMatchers.eq(REGISTER_A), setItem.capture())
//            assertEquals(cmpValue.javaClass, setItem.getValue().getValue().getClass())
//            assertEquals(CMP_TYPE, setItem.getValue().getType())
//        }
//
//        private fun doTest(value1: Any, value2: Any, opcode: Opcode, inputType: String, cmpValue: Int) {
//            Mockito.`when`(itemB.getValue()).thenReturn(value1)
//            Mockito.`when`(itemC.getValue()).thenReturn(value2)
//            Mockito.`when`(itemB.getType()).thenReturn(inputType)
//            Mockito.`when`(itemC.getType()).thenReturn(inputType)
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(opcode)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignRegister(ArgumentMatchers.eq(REGISTER_A), setItem.capture())
//            assertEquals(cmpValue, setItem.getValue().getValue())
//            assertEquals(CMP_TYPE, setItem.getValue().getType())
//        }
//
//        companion object {
//            private const val ADDRESS = 0
//            private const val CMP_TYPE = "I"
//            private const val REGISTER_A = 0
//            private const val REGISTER_B = 2
//            private const val REGISTER_C = 4
//        }
//    }
}
