package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BreakpointTest {
    @Test
    fun negativeInstructionIndexIsRejected() {
        assertThrows<IllegalArgumentException> { Breakpoint(METHOD, -1) }
    }

    @Test
    fun zeroInstructionIndexIsValid() {
        assertEquals(0, Breakpoint(METHOD, 0).instructionIndex)
    }

    private companion object {
        val METHOD = MethodSignature.parse("Lexample;->run()V")
    }
}
