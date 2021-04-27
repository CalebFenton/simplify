package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.VirtualMachine2
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FilledNewArrayTest {
    companion object {
        private const val CLASS_NAME = "Lfilled_new_array_test;"
        private const val METHOD_NAME = "filledNewArray()V"
    }

    @Nested
    inner class IntegrationTest {
        private lateinit var expected: TestState
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            expected = TestState()
            initial = TestState()
        }

        @Test
        fun testIntegerParametersCreatesArrayWithExpectedContents() {
            val elements = intArrayOf(2, 3, 5)
            initial.setRegisters(0, elements[0], "I", 1, elements[1], "I", 2, elements[2], "I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, elements, "[I")
            Tester.test(CLASS_NAME, METHOD_NAME, initial, expected)
        }

        @Test
        fun testNewArrayRangeWithIntegerParametersCreatesArrayWithExpectedContents() {
            val elements = intArrayOf(2, 3, 5, 7, 11, 13)
            initial.setRegisters(
                0, elements[0], "I", 1, elements[1], "I", 2, elements[2], "I", 3, elements[3], "I", 4,
                elements[4], "I", 5, elements[5], "I"
            )
            expected.setRegisters(ExecutionState.RESULT_REGISTER, elements, "[I")
            Tester.test(CLASS_NAME, "filledNewArrayRange()V", initial, expected)
        }

        @Test
        fun testShortParametersCreatesArrayWithExpectedContents() {
            val elements = arrayOf<Short>(2, 3, 5)
            val intElements = intArrayOf(2, 3, 5)
            initial.setRegisters(0, elements[0], "S", 1, elements[1], "S", 2, elements[2], "S")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, intElements, "[I")
            Tester.test(CLASS_NAME, METHOD_NAME, initial, expected)
        }

        @Test
        fun testUnknownElementParameterReturnsUnknownValueOfIntegerArrayType() {
            initial.setRegisters(0, 2, "I", 1, 3, "I", 2, UnknownValue(), "I")
            expected.setRegisters(ExecutionState.RESULT_REGISTER, UnknownValue(), "[I")
            Tester.test(CLASS_NAME, METHOD_NAME, initial, expected)
        }
    }

    //    @ExtendWith(MockitoExtension::class)
//    @MockitoSettings(strictness = Strictness.LENIENT)
    @Nested
    inner class UnitTestFilledNewArray {
        private lateinit var instruction: BuilderInstruction35c
        private lateinit var location: MethodLocation
        private lateinit var state: ExecutionState
        private lateinit var node: ExecutionNode
        private lateinit var op: UnaryMathOp
        private lateinit var vm: VirtualMachine2
        private val ADDRESS = 0
        private val REGISTER_C = 0
        private val REGISTER_D = 1
        private val REGISTER_E = 2
        private val REGISTER_F = 3
        private val REGISTER_G = 4

//        private var addressToLocation: TIntObjectMap<MethodLocation>? = null
//        private var instruction: BuilderInstruction? = null
//        private var itemC: HeapItem? = null
//        private var itemD: HeapItem? = null
//        private var itemE: HeapItem? = null
//        private var itemF: HeapItem? = null
//        private var itemG: HeapItem? = null
//        private var location: MethodLocation? = null
//        private var mState: MethodState? = null
//        private var node: ExecutionNode? = null
//        private var op: FilledNewArrayOp? = null
//        private var opFactory: FilledNewArrayOpFactory? = null
//        private var vm: VirtualMachine? = null

        @BeforeEach
        fun setUp() {
            vm = mockk()
            state = mockk(relaxed = true)
            node = mockk()
            every { node.state } returns state
            instruction = mockk {
                every { codeUnits } returns 0
//                every { registerA } returns REGISTER_A
//                every { registerB } returns REGISTER_B
            }
            location = mockk {
                every { codeAddress } returns ADDRESS
            }
            every { location.instruction } returns instruction
            every { instruction.location } returns location

//            vm = Mockito.mock(VirtualMachineImpl::class.java)
//            mState = Mockito.mock(MethodState::class.java)
//            node = Mockito.mock(ExecutionNode::class.java)
//            itemC = Mockito.mock(HeapItem::class.java)
//            itemD = Mockito.mock(HeapItem::class.java)
//            itemE = Mockito.mock(HeapItem::class.java)
//            itemF = Mockito.mock(HeapItem::class.java)
//            itemG = Mockito.mock(HeapItem::class.java)
//            Mockito.`when`(mState.readRegister(REGISTER_C)).thenReturn(itemC)
//            Mockito.`when`(mState.readRegister(REGISTER_D)).thenReturn(itemD)
//            Mockito.`when`(mState.readRegister(REGISTER_E)).thenReturn(itemE)
//            Mockito.`when`(mState.readRegister(REGISTER_F)).thenReturn(itemF)
//            Mockito.`when`(mState.readRegister(REGISTER_G)).thenReturn(itemG)
//            Mockito.`when`(itemC.isUnknown()).thenReturn(false)
//            Mockito.`when`(itemD.isUnknown()).thenReturn(false)
//            Mockito.`when`(itemE.isUnknown()).thenReturn(false)
//            Mockito.`when`(itemF.isUnknown()).thenReturn(false)
//            Mockito.`when`(itemG.isUnknown()).thenReturn(false)
//            location = Mockito.mock<MethodLocation>(MethodLocation::class.java)
//            instruction = Mockito.mock<BuilderInstruction>(
//                BuilderInstruction::class.java, Mockito.withSettings().extraInterfaces(
//                    Instruction35c::class.java,
//                    VariableRegisterInstruction::class.java, ReferenceInstruction::class.java
//                )
//            )
//            Mockito.`when`<Instruction>(location.getInstruction()).thenReturn(instruction)
//            Mockito.`when`<Int>(location.getCodeAddress()).thenReturn(ADDRESS)
//            Mockito.`when`<MethodLocation>(instruction.getLocation()).thenReturn(location)
//            Mockito.`when`<Int>(instruction.getCodeUnits()).thenReturn(0)
//            Mockito.`when`<Int>((instruction as Instruction35c?).getRegisterC()).thenReturn(REGISTER_C)
//            Mockito.`when`<Int>((instruction as Instruction35c?).getRegisterD()).thenReturn(REGISTER_D)
//            Mockito.`when`<Int>((instruction as Instruction35c?).getRegisterE()).thenReturn(REGISTER_E)
//            Mockito.`when`<Int>((instruction as Instruction35c?).getRegisterF()).thenReturn(REGISTER_F)
//            Mockito.`when`<Int>((instruction as Instruction35c?).getRegisterG()).thenReturn(REGISTER_G)
//            val ref: Reference = ImmutableTypeReference("[I")
//            Mockito.`when`<Reference>((instruction as ReferenceInstruction?).getReference()).thenReturn(ref)
//            addressToLocation = TIntObjectHashMap<MethodLocation>()
//            addressToLocation.put(ADDRESS, location)
//            opFactory = FilledNewArrayOpFactory()
        }

//        @Test
//        fun testFiveIntegersGivesExpectedArray() {
//            doTest(42, -42, 42, -42, 42)
//        }
//
//        @Test
//        fun testFourIntegersGivesExpectedArray() {
//            doTest(3, 5, 7, 11)
//        }
//
//        @Test
//        fun testOneIntegerGivesExpectedArray() {
//            doTest(1)
//        }
//
//        @Test
//        fun testThreeIntegersGivesExpectedArray() {
//            doTest(1, 2, 3)
//        }
//
//        @Test
//        fun testThreeNumbersGivesExpectedArray() {
//            val number1: Short = 42
//            val number2: Byte = 35
//            val number3 = 10
//            doTest(number1, number2, number3)
//        }
//
//        @Test
//        fun testTwoIntegersGivesExpectedArray() {
//            doTest(1, 2)
//        }
//
//        @Test
//        fun testUnknownValueGivesUnknownArray() {
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FILLED_NEW_ARRAY)
//            Mockito.`when`<Int>((instruction as VariableRegisterInstruction?).getRegisterCount()).thenReturn(2)
//            Mockito.`when`(itemD.getValue()).thenReturn(UnknownValue())
//            Mockito.`when`(itemD.isUnknown()).thenReturn(true)
//            Mockito.`when`(itemC.getValue()).thenReturn(3)
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_D))
//            Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//            val setItem: ArgumentCaptor<HeapItem> = ArgumentCaptor.forClass(HeapItem::class.java)
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignResultRegister(setItem.capture())
//            assertEquals(UnknownValue::class.java, setItem.getValue().getValue().getClass())
//            assertEquals("[I", setItem.getValue().getType())
//            Assertions.assertEquals("filled-new-array {r0, r1}, [I", op.toString())
//        }
//
//        private fun doTest(vararg values: Number) {
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FILLED_NEW_ARRAY)
//            Mockito.`when`<Int>((instruction as VariableRegisterInstruction?).getRegisterCount()).thenReturn(values.size)
//            when (values.size) {
//                5 -> {
//                    Mockito.`when`(itemG.getValue()).thenReturn(values[4])
//                    Mockito.`when`(itemF.getValue()).thenReturn(values[3])
//                    Mockito.`when`(itemE.getValue()).thenReturn(values[2])
//                    Mockito.`when`(itemD.getValue()).thenReturn(values[1])
//                    Mockito.`when`(itemC.getValue()).thenReturn(values[0])
//                }
//                4 -> {
//                    Mockito.`when`(itemF.getValue()).thenReturn(values[3])
//                    Mockito.`when`(itemE.getValue()).thenReturn(values[2])
//                    Mockito.`when`(itemD.getValue()).thenReturn(values[1])
//                    Mockito.`when`(itemC.getValue()).thenReturn(values[0])
//                }
//                3 -> {
//                    Mockito.`when`(itemE.getValue()).thenReturn(values[2])
//                    Mockito.`when`(itemD.getValue()).thenReturn(values[1])
//                    Mockito.`when`(itemC.getValue()).thenReturn(values[0])
//                }
//                2 -> {
//                    Mockito.`when`(itemD.getValue()).thenReturn(values[1])
//                    Mockito.`when`(itemC.getValue()).thenReturn(values[0])
//                }
//                1 -> Mockito.`when`(itemC.getValue()).thenReturn(values[0])
//            }
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            when (values.size) {
//                5 -> {
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_G))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_F))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_E))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_D))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//                }
//                4 -> {
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_F))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_E))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_D))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//                }
//                3 -> {
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_E))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_D))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//                }
//                2 -> {
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_D))
//                    Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//                }
//                1 -> Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(REGISTER_C))
//            }
//            val expected = IntArray(values.size)
//            for (i in expected.indices) {
//                expected[i] = values[i].toInt()
//            }
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignResultRegister(ArgumentMatchers.eq(expected), ArgumentMatchers.eq("[I"))
//        }
//
//    }
//
//    @ExtendWith(MockitoExtension::class)
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    class UnitTestFilledNewArrayRange {
//        private var addressToLocation: TIntObjectMap<MethodLocation>? = null
//        private var instruction: BuilderInstruction? = null
//        private var location: MethodLocation? = null
//        private var mState: MethodState? = null
//        private var node: ExecutionNode? = null
//        private var op: FilledNewArrayOp? = null
//        private var opFactory: FilledNewArrayOpFactory? = null
//        private var vm: VirtualMachine? = null
//
//        @BeforeEach
//        fun setUp() {
//            vm = Mockito.mock(VirtualMachineImpl::class.java)
//            node = Mockito.mock(ExecutionNode::class.java)
//            mState = Mockito.mock(MethodState::class.java)
//            location = Mockito.mock<MethodLocation>(MethodLocation::class.java)
//            instruction = Mockito.mock<BuilderInstruction>(
//                BuilderInstruction::class.java, Mockito.withSettings().extraInterfaces(
//                    Instruction3rc::class.java,
//                    VariableRegisterInstruction::class.java, ReferenceInstruction::class.java, RegisterRangeInstruction::class.java
//                )
//            )
//            Mockito.`when`<Instruction>(location.getInstruction()).thenReturn(instruction)
//            Mockito.`when`<Int>(location.getCodeAddress()).thenReturn(ADDRESS)
//            Mockito.`when`<MethodLocation>(instruction.getLocation()).thenReturn(location)
//            Mockito.`when`<Int>(instruction.getCodeUnits()).thenReturn(0)
//            val ref: Reference = ImmutableTypeReference("[I")
//            Mockito.`when`<Reference>((instruction as ReferenceInstruction?).getReference()).thenReturn(ref)
//            addressToLocation = TIntObjectHashMap<MethodLocation>()
//            addressToLocation.put(ADDRESS, location)
//            opFactory = FilledNewArrayOpFactory()
//        }
//
//        @Test
//        fun testSixIntegersGivesExpectedArray() {
//            doTest(42, -42, 42, -42, 42, -42)
//        }
//
//        @Test
//        fun testUnknownValueGivesUnknownArray() {
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FILLED_NEW_ARRAY_RANGE)
//            Mockito.`when`<Int>((instruction as VariableRegisterInstruction?).getRegisterCount()).thenReturn(6)
//            Mockito.`when`<Int>((instruction as RegisterRangeInstruction?).getStartRegister()).thenReturn(0)
//            for (i in 0..5) {
//                val item: HeapItem = Mockito.mock(HeapItem::class.java)
//                Mockito.`when`(item.getValue()).thenReturn(if (i == 3) UnknownValue() else i)
//                Mockito.`when`(item.getType()).thenReturn("I")
//                Mockito.`when`(mState.readRegister(i)).thenReturn(item)
//            }
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            for (i in 0..5) {
//                Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(i))
//            }
//            val setItem: ArgumentCaptor<HeapItem> = ArgumentCaptor.forClass(HeapItem::class.java)
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignResultRegister(setItem.capture())
//            assertEquals(UnknownValue::class.java, setItem.getValue().getValue().getClass())
//            assertEquals("[I", setItem.getValue().getType())
//            Assertions.assertEquals("filled-new-array/range {r0 .. r5}, [I", op.toString())
//        }
//
//        private fun doTest(vararg values: Number) {
//            Mockito.`when`<Opcode>(instruction.getOpcode()).thenReturn(Opcode.FILLED_NEW_ARRAY_RANGE)
//            Mockito.`when`<Int>((instruction as VariableRegisterInstruction?).getRegisterCount()).thenReturn(values.size)
//            Mockito.`when`<Int>((instruction as RegisterRangeInstruction?).getStartRegister()).thenReturn(0)
//            val expected = IntArray(values.size)
//            for (i in expected.indices) {
//                expected[i] = values[i].toInt()
//                val item: HeapItem = Mockito.mock(HeapItem::class.java)
//                Mockito.`when`(item.getValue()).thenReturn(expected[i])
//                Mockito.`when`(item.getType()).thenReturn("I")
//                Mockito.`when`(mState.readRegister(i)).thenReturn(item)
//            }
//            op = opFactory.create(location, addressToLocation, vm)
//            op!!.execute(node, mState)
//            for (i in expected.indices) {
//                Mockito.verify<Any>(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(i))
//            }
//            Mockito.verify<Any>(mState, Mockito.times(1)).assignResultRegister(ArgumentMatchers.eq(expected), ArgumentMatchers.eq("[I"))
//        }
    }
}
