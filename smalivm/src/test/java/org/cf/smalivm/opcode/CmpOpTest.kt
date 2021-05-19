package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.Value
import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction23x
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Nested
    inner class UnitTest {
        private lateinit var instruction: BuilderInstruction23x
        private lateinit var location: MethodLocation
        private lateinit var state: ExecutionState
        private lateinit var node: ExecutionNode
        private lateinit var op: CmpOp
        private lateinit var vm: VirtualMachine
        private val ADDRESS = 0
        private val CMP_TYPE = "I"
        private val REGISTER_A = 0
        private val REGISTER_B = 2
        private val REGISTER_C = 4

        @BeforeEach
        fun setUp() {
            vm = mockk()
            state = mockk(relaxed = true)
            node = mockk()
            every { node.state } returns state
            location = mockk {
                every { codeAddress } returns ADDRESS
            }
            instruction = mockk {
                every { codeUnits } returns 0
                every { registerA } returns REGISTER_A
                every { registerB } returns REGISTER_B
                every { registerC } returns REGISTER_C
            }
            every { instruction.location } returns location
            every { location.instruction } returns instruction
        }

        @Test
        fun canArg1IsUnknownReturnsUnknown() {
            val value1 = UnknownValue()
            val value2 = 1149239296L
            doTest(value1, value2, Opcode.CMP_LONG, "J", UnknownValue())
            assertEquals("cmp-long r0, r2, r4", op.toString())
        }

        @Test
        fun canCompareDouble() {
            val value1 = 11204.0345612345
            val value2 = 11492.3929612345
            val cmpValue = value1.compareTo(value2)
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", cmpValue)
        }

        @Test
        fun canCompareDoubleGreaterWithArg1NaN() {
            val value1 = Double.NaN
            val value2 = 11492.3929612345
            doTest(value1, value2, Opcode.CMPG_DOUBLE, "D", 1)
        }

        @Test
        fun canCompareDoubleLessWithArg1NaN() {
            val value1 = Double.NaN
            val value2 = 11492.3929612345
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", -1)
        }

        @Test
        fun canCompareFloat() {
            val value1 = 11204.03456f
            val value2 = 11492.39296f
            val cmpValue = value1.compareTo(value2)
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", cmpValue)
        }

        @Test
        fun canCompareFloatGreaterWithArg1NaN() {
            val value1 = Float.NaN
            val value2 = 11492.39296f
            doTest(value1, value2, Opcode.CMPG_FLOAT, "F", 1)
        }

        @Test
        fun canCompareFloatLessWithArg1NaN() {
            val value1 = Float.NaN
            val value2 = 11492.39296f
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", -1)
        }

        @Test
        fun canCompareLong() {
            val value1 = 1120403456L
            val value2 = 1149239296L
            val cmpValue = value1.compareTo(value2)
            doTest(value1, value2, Opcode.CMP_LONG, "J", cmpValue)
        }

        @Test
        fun testArg2IsUnknownReturnsUnknown() {
            val value1 = 1149239296L
            val value2 = UnknownValue()
            doTest(value1, value2, Opcode.CMP_LONG, "J", UnknownValue())
        }

        private fun doTest(value1: Any, value2: Any, opcode: Opcode, inputType: String, cmpValue: Any) {
            every { state.readRegister(REGISTER_B) } returns Value.wrap(value1, inputType)
            every { state.readRegister(REGISTER_C) } returns Value.wrap(value2, inputType)
            every { instruction.opcode } returns opcode

            op = CmpOp.build(location, mockk(), mockk(), mockk())
            val children = op.execute(node)

            Tester.verifyContinueChild(children)
            val registerA = slot<Value>()
            verify(exactly = 1) { state.assignRegister(REGISTER_A, capture(registerA)) }
            if (cmpValue is UnknownValue) {
                assertTrue(registerA.captured.raw is UnknownValue)
            } else {
                assertEquals(cmpValue, registerA.captured.raw)
            }
            assertEquals(CMP_TYPE, registerA.captured.type)
        }
    }
}
