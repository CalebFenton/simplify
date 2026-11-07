package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Bug class: the untyped `aborted` boolean — optimizers constant-folded from graphs whose
 * exploration timed out, trusting consensus over paths that were never walked (spec §6.4,
 * §9 completion-gated trust). Oracle: the spec's rule — consensus is trustworthy only
 * from a Complete exploration; ANY abort reason poisons trust.
 */
class CompletionStatusTest {
    @Test
    fun completeExplorationCanBeTrusted() {
        assertTrue(CompletionStatus.Complete.canTrustConsensus)
    }

    @Test
    fun anyPartialExplorationCannotBeTrusted() {
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
        val expected = CompletionStatus.Partial(setOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH))

        source.clear()

        assertEquals(setOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH), status.reasons)
        assertEquals(expected, status)
        assertEquals(expected.hashCode(), status.hashCode())
        @Suppress("UNCHECKED_CAST")
        val mutableView = status.reasons as MutableSet<AbortReason>
        assertThrows(UnsupportedOperationException::class.java) {
            mutableView.clear()
        }
    }

    @Test
    fun partialEqualityAndHashCodeAreStructural() {
        val first = CompletionStatus.Partial(linkedSetOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH))
        val second = CompletionStatus.Partial(linkedSetOf(AbortReason.CALL_DEPTH, AbortReason.TIMEOUT))

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun anUnfinishedExplorationCannotBeTrusted() {
        // Bug class: refusing trust only for ABORTS, so a run that simply has not finished yet looks
        // trustworthy. `ExecutionSession.graph()` is documented as safe to call mid-run (Plan 10's
        // debugger), and a mid-run snapshot with nothing aborted has an EMPTY abort set -- so the
        // moment trust is decided from abort reasons alone, an unfinished exploration hands out
        // actionable answers. Oracle: spec §9 -- consensus may be acted on only when the relevant
        // paths were fully explored, and paths still sitting in the frontier were not explored at all.
        assertFalse(CompletionStatus.InProgress.canTrustConsensus, "paths are still scheduled")
    }

    @Test
    fun partialCarriesAllReasonsForDiagnostics() {
        val status = CompletionStatus.Partial(setOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH))
        assertEquals(setOf(AbortReason.TIMEOUT, AbortReason.CALL_DEPTH), status.reasons)
    }
}
