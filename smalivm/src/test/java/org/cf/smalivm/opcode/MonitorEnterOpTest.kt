package org.cf.smalivm.opcode

import io.mockk.every
import io.mockk.mockk
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.instruction.BuilderInstruction11x
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonitorEnterOpTest {
    @Test
    fun monitorEnterHasExpectedToString() {
        val instruction: BuilderInstruction11x = mockk {
            every { registerA } returns 3
            every { opcode } returns Opcode.MONITOR_ENTER
        }
        val location: MethodLocation = mockk()
        every { location.instruction } returns instruction
        val op = MonitorEnterOp.build(location, mockk(), mockk(), mockk())
        assertEquals("monitor-enter r3", op.toString())
    }
}
