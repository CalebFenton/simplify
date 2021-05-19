package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import org.cf.smalivm.TestState
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.Value
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction22t
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.Boolean
import kotlin.Int
import kotlin.IntArray
import kotlin.byteArrayOf
import kotlin.intArrayOf

class IfOpTest {
    private val ADDRESS_IF = 0
    private val ADDRESS_NOP = 2
    private val ADDRESS_RETURN = 3
    private val CLASS_NAME = "Lif_test;"
    private val IF_ALL_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN)
    private val IF_FALSE_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_NOP, ADDRESS_RETURN)
    private val IF_TRUE_VISITATIONS = intArrayOf(ADDRESS_IF, ADDRESS_RETURN)

    @Nested
    inner class CompareIdenticalPrimitiveValueTypes {
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        @Test
        fun testIfEqualWithOneAndZeroIsFalse() {
            initial.setRegisters(0, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithTwoEqualIntegersIsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithTwoUnequalIntegersIsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithZeroAndZeroIsTrue() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualZeroWith0ByteIsTrue() {
            initial.setRegisters(0, 0x0.toByte(), "B")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualWithOneAndZeroIsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithNegativeOneIsFalse() {
            initial.setRegisters(0, -1, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterOrEqualZeroWithZeroIsTrue() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanWith1And0IsTrue() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterThan()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfGreaterThanZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifGreaterThanZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith0And0IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualWith1And0IsFalse() {
            initial.setRegisters(0, 1, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWith0IsTrue() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWithNegative1IsTrue() {
            initial.setRegisters(0, -1, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessOrEqualZeroWithOneIsFalse() {
            initial.setRegisters(0, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessOrEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanWith0And1IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessThan()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfLessThanZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfLessThanZeroWithNegativeOneIsTrue() {
            initial.setRegisters(0, -1, "I")
            Tester.testVisitation(CLASS_NAME, "ifLessThanZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWith0And0IsFalse() {
            initial.setRegisters(0, 0, "I", 1, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWith0And1IsTrue() {
            initial.setRegisters(0, 0, "I", 1, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWith0IsFalse() {
            initial.setRegisters(0, 0, "I")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_FALSE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWith7ByteIsTrue() {
            initial.setRegisters(0, 0x7.toByte(), "B")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWithOneIsTrue() {
            initial.setRegisters(0, 1, "I")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }

    @Nested
    inner class CompareObjectReferences {
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        @Test
        fun testIdenticalObjectReferencesAreEqual() {
            val obj = "object string"
            initial.setRegisters(0, obj, "Ljava/lang/String;", 1, obj, "Ljava/lang/String;")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfFalseBooleanEqualZero() {
            initial.setRegisters(0, Boolean.FALSE, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWithStringAndArrayReferenceIsTrue() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;", 1, IntArray(0), "[I")
            Tester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualWithTwoDifferentStringReferencesIsTrue() {
            val obj1 = "object string"
            // Need to get crafty or javac will be smart enough to use same literal for both objects
            val obj2 = StringBuilder(obj1).toString()
            initial.setRegisters(0, obj1, "Ljava/lang/String;", 1, obj2, "Ljava/lang/String;")
            Tester.testVisitation(CLASS_NAME, "ifNotEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNullEqualZero() {
            initial.setRegisters(0, null, "I")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfObjectNotEqualZero() {
            initial.setRegisters(0, "object string", "Ljava/lang/String;")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfPrimitiveArrayNotEqualZero() {
            initial.setRegisters(0, byteArrayOf(0x1), "[B")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfTrueBooleanNotEqualZero() {
            initial.setRegisters(0, Boolean.TRUE, "Z")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfUnknownIntegerTakesBothPaths() {
            initial.setRegisters(0, UnknownValue(), "I")
            val graph = Tester.execute(CLASS_NAME, "ifEqualZero()V", initial)
            Tester.testVisitation(graph, IF_ALL_VISITATIONS)
            assertEquals(1, graph.getNodePile(ADDRESS_NOP).size)
            // Two separate execution paths should reach the return op
            assertEquals(2, graph.getNodePile(ADDRESS_RETURN).size)
        }

        @Test
        fun testIfZeroIntegerEqualZero() {
            initial.setRegisters(0, Integer.valueOf(0), "I")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }

    @Nested
    inner class UnitTest {
        private lateinit var instruction: BuilderInstruction22t
        private lateinit var location: MethodLocation
        private lateinit var state: ExecutionState
        private lateinit var node: ExecutionNode
        private val ADDRESS = 0
        private val ARG1_REGISTER = 2
        private val ARG2_REGISTER = 4

        @BeforeEach
        fun setUp() {
            state = mockk(relaxed = true)
            node = mockk()
            every { node.state } returns state
            location = mockk {
                every { codeAddress } returns ADDRESS
            }
        }

        @Test
        fun hasExpectedToStringValue() {
            val value = 0
            every { state.readRegister(ARG1_REGISTER) } returns Value.wrap(value, "D")
            every { state.readRegister(ARG2_REGISTER) } returns Value.wrap(value, "D")
            instruction = buildInstruction22t(Opcode.IF_GE, 0)
            val op = IfOp.build(location, mockk(), mockk(), mockk())
            op.execute(node)
            assertEquals("if-ge r2, r4, :addr_0", op.toString())
        }

        private fun buildInstruction22t(opcode: Opcode, offset: Int): BuilderInstruction22t {
            instruction = mockk {
                every { codeUnits } returns 0
                every { codeOffset } returns offset
                every { registerA } returns ARG1_REGISTER
                every { registerB } returns ARG2_REGISTER
            }
            every { instruction.opcode } returns opcode
            every { instruction.location } returns location
            every { location.instruction } returns instruction
            return instruction
        }
    }

    @Nested
    inner class ValueTypeCombinations {
        private lateinit var initial: TestState

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        @Test
        fun testIfEqualWithBooleanAndChar() {
            initial.setRegisters(0, true, "Z", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndBoolean() {
            initial.setRegisters(0, 1.toByte(), "B", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndChar() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndDouble() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1.0, "D")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndFloat() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1f, "F")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndLong() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1L, "J")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithByteAndShort() {
            initial.setRegisters(0, 1.toByte(), "B", 1, 1.toShort(), "S")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithDoubleAndBoolean() {
            initial.setRegisters(0, 1.0, "D", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithDoubleAndChar() {
            initial.setRegisters(0, 1.0, "D", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithFloatAndBoolean() {
            initial.setRegisters(0, 1f, "F", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithFloatAndChar() {
            initial.setRegisters(0, 1f, "F", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithFloatAndDouble() {
            // 3.2 is tricky, 3.2F != 3.2D && Double.compareTo(3.2F, 3.2D)
            initial.setRegisters(0, 3.2f, "F", 1, 3.2, "D")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndBoolean() {
            initial.setRegisters(0, 1, "I", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndByte() {
            initial.setRegisters(0, 1, "I", 1, 1.toByte(), "B")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndChar() {
            initial.setRegisters(0, 1, "I", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndDouble() {
            initial.setRegisters(0, 1, "I", 1, 1.0, "D")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndFloat() {
            initial.setRegisters(0, 1, "I", 1, 1f, "F")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndLong() {
            initial.setRegisters(0, 1, "I", 1, 1L, "J")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithIntAndShort() {
            initial.setRegisters(0, 1, "I", 1, 1.toShort(), "S")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndBoolean() {
            initial.setRegisters(0, 1L, "J", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndChar() {
            initial.setRegisters(0, 1L, "J", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndDouble() {
            initial.setRegisters(0, 1L, "J", 1, 1.0, "D")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithLongAndFloat() {
            initial.setRegisters(0, 1L, "J", 1, 1f, "F")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndBoolean() {
            initial.setRegisters(0, 1.toShort(), "S", 1, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndChar() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1.toChar(), "C")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndDouble() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1.0, "D")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndFloat() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1f, "F")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualWithShortAndLong() {
            initial.setRegisters(0, 1.toShort(), "S", 1, 1L, "J")
            Tester.testVisitation(CLASS_NAME, "ifEqual()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfEqualZeroWithFalseIsTrue() {
            initial.setRegisters(0, false, "Z")
            Tester.testVisitation(CLASS_NAME, "ifEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }

        @Test
        fun testIfNotEqualZeroWithTrueIsTrue() {
            initial.setRegisters(0, true, "Z")
            Tester.testVisitation(CLASS_NAME, "ifNotEqualZero()V", initial, IF_TRUE_VISITATIONS)
        }
    }
}
