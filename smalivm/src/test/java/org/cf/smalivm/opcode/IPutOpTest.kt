package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.Value
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c
import org.jf.dexlib2.immutable.reference.ImmutableFieldReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IPutOpTest {
    companion object {
        private const val ADDRESS = 0
        private const val REGISTER_A = 0
        private const val REGISTER_B = 2
    }

    private lateinit var instruction: BuilderInstruction22c
    private lateinit var location: MethodLocation
    private lateinit var state: ExecutionState
    private lateinit var node: ExecutionNode

    @BeforeEach
    fun setUp() {
        state = mockk(relaxed = true) {
            every { readRegister(REGISTER_A) } returns mockk()
            every { readRegister(REGISTER_B) } returns mockk()
        }
        node = mockk(relaxed = true)
        every { node.state } returns state
        location = mockk {
            every { codeAddress } returns ADDRESS
        }
        instruction = mockk {
            every { codeUnits } returns 0
            every { registerA } returns REGISTER_A
            every { registerB } returns REGISTER_B
            every { reference } returns ImmutableFieldReference("Lsome/class;", "someMethod", "I")
        }
        every { instruction.location } returns location
        every { location.instruction } returns instruction
    }

    @Test
    fun testIGetReturnsUnknownValueOfCorrectType() {
        every { instruction.opcode } returns Opcode.IPUT
        val op = IPutOp.build(location, mockk(), mockk(), mockk())
        op.execute(node)

        verify(exactly = 1) { state.readRegister(REGISTER_A) }
        verify(exactly = 1) { state.readRegister(REGISTER_B) }
        val resultRegister = slot<Value>()
        verify(exactly = 1) { state.assignRegister(REGISTER_B, ofType(Value::class)) }
        assertEquals("iput r$REGISTER_A, r$REGISTER_B, Lsome/class;->someMethod:I", op.toString())
    }
}
