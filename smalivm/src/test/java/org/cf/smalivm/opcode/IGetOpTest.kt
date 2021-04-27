package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.cf.smalivm.Tester
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.Value
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c
import org.jf.dexlib2.immutable.reference.ImmutableFieldReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IGetOpTest {
    private lateinit var instruction: BuilderInstruction22c
    private lateinit var location: MethodLocation
    private lateinit var state: ExecutionState
    private lateinit var node: ExecutionNode

    companion object {
        private const val ADDRESS = 0
        private const val REGISTER_A = 0
        private const val REGISTER_B = 2
    }

    @BeforeEach
    fun setUp() {
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
            every { opcode } returns Opcode.IGET
            every { reference } returns ImmutableFieldReference("Lsome/class;", "someMethod", "I")
        }
        every { instruction.location } returns location
        every { location.instruction } returns instruction
    }

    @Test
    fun testIGetReturnsUnknownValueOfCorrectType() {
        every { state.readRegister(REGISTER_B) } returns mockk()
        val op = IGetOp.build(location, mockk(), mockk(), mockk())
        val children = op.execute(node)
        Tester.verifyContinueChild(children)
        verify(exactly = 1) { state.readRegister(REGISTER_B) }
        val resultRegister = slot<Value>()
        verify(exactly = 1) { state.assignRegister(REGISTER_A, capture(resultRegister)) }
        assertTrue(resultRegister.captured.raw is UnknownValue)
        assertEquals("I", resultRegister.captured.type)
        assertEquals("iget r$REGISTER_A, r$REGISTER_B, Lsome/class;->someMethod:I", op.toString())
    }
}
