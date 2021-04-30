package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.Value
import org.cf.smalivm2.VirtualMachine2
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.builder.instruction.BuilderInstruction3rc
import org.jf.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Nested
    inner class UnitTestFilledNewArray {
        private lateinit var instruction: BuilderInstruction35c
        private lateinit var location: MethodLocation
        private lateinit var state: ExecutionState
        private lateinit var node: ExecutionNode
        private lateinit var vm: VirtualMachine2
        private val ADDRESS = 0
        private val REGISTER_C = 0
        private val REGISTER_D = 1
        private val REGISTER_E = 2
        private val REGISTER_F = 3
        private val REGISTER_G = 4

        @BeforeEach
        fun setUp() {
            state = mockk(relaxed = true)
            node = mockk()
            every { node.state } returns state
            instruction = mockk {
                every { codeUnits } returns 0
                every { registerC } returns REGISTER_C
                every { registerD } returns REGISTER_D
                every { registerE } returns REGISTER_E
                every { registerF } returns REGISTER_F
                every { registerG } returns REGISTER_G
                every { opcode } returns Opcode.FILLED_NEW_ARRAY
                every { reference } returns ImmutableTypeReference("[I")
            }
            location = mockk {
                every { codeAddress } returns ADDRESS
            }
            every { location.instruction } returns instruction
            every { instruction.location } returns location
        }

        @Test
        fun testFiveIntegersGivesExpectedArray() {
            doTest(42, -42, 42, -42, 42)
        }

        @Test
        fun testFourIntegersGivesExpectedArray() {
            doTest(3, 5, 7, 11)
        }

        @Test
        fun testOneIntegerGivesExpectedArray() {
            doTest(1)
        }

        @Test
        fun testThreeIntegersGivesExpectedArray() {
            doTest(1, 2, 3)
        }

        @Test
        fun testThreeNumbersGivesExpectedArray() {
            val number1: Short = 42
            val number2: Byte = 35
            val number3 = 10
            doTest(number1, number2, number3)
        }

        @Test
        fun testTwoIntegersGivesExpectedArray() {
            doTest(1, 2)
        }

        @Test
        fun testUnknownValueGivesUnknownArray() {
            every { instruction.registerCount } returns 2
            every { state.readRegister(REGISTER_C) } returns Value.wrap(3, "I")
            every { state.readRegister(REGISTER_D) } returns Value.unknown("I")

            val op = FilledNewArrayOp.build(location, mockk(), mockk(), mockk())
            val children = op.execute(node)

            Tester.verifyContinueChild(children)
            verify(exactly = 1) { state.readRegister(REGISTER_C) }
            verify(exactly = 1) { state.readRegister(REGISTER_D) }
            val resultRegister = slot<Value>()
            verify(exactly = 1) { state.assignResultRegister(capture(resultRegister)) }
            assertTrue(resultRegister.captured.raw is UnknownValue)
            assertEquals("[I", resultRegister.captured.type)
            assertEquals("filled-new-array {r0, r1}, [I", op.toString())
        }

        private fun doTest(vararg values: Number) {
            every { instruction.registerCount } returns values.size
            val registers = arrayOf(REGISTER_C, REGISTER_D, REGISTER_E, REGISTER_F, REGISTER_G)
            values.forEachIndexed { index, number ->
                every { state.readRegister(registers[index]) } returns Value.wrap(number, "I")
            }

            val op = FilledNewArrayOp.build(location, mockk(), mockk(), mockk())
            val children = op.execute(node)

            Tester.verifyContinueChild(children)
            values.forEachIndexed { index, _ ->
                verify(exactly = 1) { state.readRegister(registers[index]) }
            }
            val expected = values.map { it.toInt() }.toIntArray()
            verify(exactly = 1) { state.assignResultRegister(expected, "[I") }
        }
    }

    @Nested
    inner class UnitTestFilledNewArrayRange {
        private lateinit var instruction: BuilderInstruction3rc
        private lateinit var location: MethodLocation
        private lateinit var state: ExecutionState
        private lateinit var node: ExecutionNode
        private val ADDRESS = 0

        @BeforeEach
        fun setUp() {
            state = mockk(relaxed = true)
            node = mockk()
            every { node.state } returns state
            instruction = mockk {
                every { codeUnits } returns 0
                every { opcode } returns Opcode.FILLED_NEW_ARRAY_RANGE
                every { reference } returns ImmutableTypeReference("[I")
                every { startRegister } returns 0
            }

            location = mockk {
                every { codeAddress } returns ADDRESS
            }
            every { location.instruction } returns instruction
            every { instruction.location } returns location
        }

        @Test
        fun testSixIntegersGivesExpectedArray() {
            doTest(42, -42, 42, -42, 42, -42)
        }

        @Test
        fun testUnknownValueGivesUnknownArray() {
            every { instruction.registerCount } returns 6
            for (i in 0..5) {
                every { state.readRegister(i) } returns if (i == 3) Value.unknown("I") else Value.wrap(i, "I")
            }
            val op = FilledNewArrayOp.build(location, mockk(), mockk(), mockk())
            val children = op.execute(node)

            Tester.verifyContinueChild(children)
            for (i in 0..5) {
                verify(exactly = 1) { state.readRegister(i) }
            }
            val resultRegister = slot<Value>()
            verify(exactly = 1) { state.assignResultRegister(capture(resultRegister)) }
            assertTrue(resultRegister.captured.raw is UnknownValue)
            assertEquals("[I", resultRegister.captured.type)
        }

        private fun doTest(vararg values: Number) {
            every { instruction.registerCount } returns values.size
            val expected = IntArray(values.size)
            for (index in expected.indices) {
                expected[index] = values[index].toInt()
                val value = Value.wrap(expected[index], "I")
                every { state.readRegister(index) } returns value
            }
            val op = FilledNewArrayOp.build(location, mockk(), mockk(), mockk())
            val children = op.execute(node)

            Tester.verifyContinueChild(children)
            for (i in expected.indices) {
                verify(exactly = 1) { state.readRegister(i) }
            }
            verify(exactly = 1) { state.assignResultRegister(expected, "[I") }
            assertEquals("filled-new-array/range {r0 .. r5}, [I", op.toString())
        }
    }
}
