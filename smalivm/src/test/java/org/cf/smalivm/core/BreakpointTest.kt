package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BreakpointTest {
    @Test
    fun emptyOrBlankMethodSignatureIsRejected() {
        assertThrows<IllegalArgumentException> { Breakpoint("", 0) }
        assertThrows<IllegalArgumentException> { Breakpoint("   ", 0) }
    }

    @Test
    fun negativeInstructionIndexIsRejected() {
        assertThrows<IllegalArgumentException> { Breakpoint(METHOD, -1) }
    }

    @Test
    fun zeroInstructionIndexIsValid() {
        assertEquals(0, Breakpoint(METHOD, 0).instructionIndex)
    }

    @Test
    fun equalValuesAreEqual() {
        assertEquals(Breakpoint(METHOD, 2), Breakpoint(METHOD, 2))
    }

    @Test
    fun changedMethodSignatureIsNotEqual() {
        assertNotEquals(Breakpoint(METHOD, 2), Breakpoint("Lother;->run()V", 2))
    }

    @Test
    fun changedInstructionIndexIsNotEqual() {
        assertNotEquals(Breakpoint(METHOD, 2), Breakpoint(METHOD, 3))
    }

    private companion object {
        const val METHOD = "Lexample;->run()V"
    }
}
