package org.cf.smalivm.opcode

import org.cf.smalivm.TestState
import org.cf.smalivm.Tester.testVisitation
import org.cf.smalivm.type.UnknownValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SwitchOpTest {
    companion object {
        private const val CLASS_NAME = "Lswitch_test;"
    }

    @Nested
    inner class PackedSwitch {
        private lateinit var initial: TestState
        private val METHOD_NAME = "packedSwitch()V"

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        @Test
        fun testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "Z")
            val expectedAddresses = intArrayOf(0, 1, 5, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        @Test
        fun testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "S")
            val expectedAddresses = intArrayOf(0, 1, 4, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        @Test
        fun testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegisters(0, UnknownValue(), "I")
            val expectedAddresses = intArrayOf(0, 1, 4, 5, 6, 7, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }
    }

    @Nested
    inner class SparseSwitch {
        private lateinit var initial: TestState
        private val METHOD_NAME = "sparseSwitch()V"

        @BeforeEach
        fun setUp() {
            initial = TestState()
        }

        @Test
        fun testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "B")
            val expectedAddresses = intArrayOf(0, 1, 5, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        @Test
        fun testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "I")
            val expectedAddresses = intArrayOf(0, 1, 4, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        @Test
        fun testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegisters(0, UnknownValue(), "I")
            val expectedAddresses = intArrayOf(0, 1, 4, 5, 6, 8)
            testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }
    }
}
