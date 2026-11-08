package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Consensus is trustworthy only from a Complete exploration: any abort reason, or paths still in the
 * frontier, means some paths were never walked.
 */
class CompletionStatusTest {
    @Test
    fun onlyACompleteExplorationCanBeTrusted() {
        assertTrue(CompletionStatus.Complete.canTrustConsensus)
        // A mid-run snapshot has no abort reason, so trust cannot be decided from abort reasons alone.
        assertFalse(CompletionStatus.InProgress.canTrustConsensus, "paths are still scheduled")
        for (reason in AbortReason.entries) {
            val status = CompletionStatus.Partial(setOf(reason))
            assertFalse(status.canTrustConsensus, "Partial($reason) must not be trusted")
        }
    }

    @Test
    fun partialRejectsEmptyReasons() {
        assertThrows(IllegalArgumentException::class.java) {
            CompletionStatus.Partial(emptySet())
        }
    }

    @Test
    fun partialOwnsAnImmutableSnapshotOfReasons() {
        val source = linkedSetOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH)
        val status = CompletionStatus.Partial(source)

        source.clear()

        assertEquals(setOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH), status.reasons)
        @Suppress("UNCHECKED_CAST")
        val mutableView = status.reasons as MutableSet<AbortReason>
        assertThrows(UnsupportedOperationException::class.java) {
            mutableView.clear()
        }
    }

    @Test
    fun partialEqualityAndHashCodeIgnoreReasonOrder() {
        val first = CompletionStatus.Partial(linkedSetOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH))
        val second = CompletionStatus.Partial(linkedSetOf(AbortReason.CALL_DEPTH, AbortReason.TIMEOUT))

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}
