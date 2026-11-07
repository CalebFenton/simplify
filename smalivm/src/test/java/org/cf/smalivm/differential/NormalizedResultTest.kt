package org.cf.smalivm.differential

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NormalizedResultTest {
    private fun result(reg0: NormalizedValue) = NormalizedResult(
        registers = mapOf(0 to reg0),
        reachedAddresses = setOf(0, 1),
        unhandledExceptions = emptyList(),
        aborted = false,
    )

    @Test
    fun identicalResultsHaveNoDifferences() {
        val a = result(NormalizedValue(ValueKind.KNOWN, "I", "5"))
        val b = result(NormalizedValue(ValueKind.KNOWN, "I", "5"))
        assertTrue(a.diff(b).identical)
    }

    @Test
    fun differingRegisterValueIsReported() {
        val a = result(NormalizedValue(ValueKind.KNOWN, "I", "5"))
        val b = result(NormalizedValue(ValueKind.KNOWN, "I", "6"))
        val d = a.diff(b)
        assertFalse(d.identical)
        assertEquals(1, d.differences.size)
        assertTrue(d.differences[0].contains("register 0"))
    }

    @Test
    fun differingAbortedAndReachedAreReported() {
        val a = result(NormalizedValue(ValueKind.KNOWN, "I", "5"))
        val b = a.copy(aborted = true, reachedAddresses = setOf(0))
        val d = a.diff(b)
        assertFalse(d.identical)
        assertEquals(2, d.differences.size)
    }
}
