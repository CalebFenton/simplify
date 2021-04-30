package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderSparseSwitchPayload
import org.jf.dexlib2.builder.instruction.BuilderSwitchElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SwitchPayloadOpTest {
    companion object {
        private const val ADDRESS = 0
    }

    @Nested
    inner class UnitTest {
        private lateinit var location: MethodLocation
        private lateinit var instruction: BuilderSparseSwitchPayload

        @BeforeEach
        fun setUp() {
            instruction = mockk {
                every { codeUnits } returns 0
            }
            location = mockk {
                every { codeAddress } returns ADDRESS
            }
            every { location.instruction } returns instruction
            every { instruction.location } returns location
        }

        @Test
        fun payloadOpHasExpectedToString() {
            every { instruction.opcode } returns Opcode.PACKED_SWITCH_PAYLOAD

            val element1: BuilderSwitchElement = mockk {
                every { key } returns 1
                every { offset } returns 10
            }
            val element2: BuilderSwitchElement = mockk {
                every { key } returns 2
                every { offset } returns 20
            }
            val elements: MutableList<BuilderSwitchElement> = mutableListOf(element1, element2)
            every { instruction.switchElements } returns elements

            val op = SwitchPayloadOp.build(location, mockk(), mockk(), mockk())
            assertEquals(
                "packed-switch-payload [${element1.key} -> :addr_${element1.offset}, ${element2.key} -> :addr_${element2.offset}]",
                op.toString()
            )
        }
    }
}
