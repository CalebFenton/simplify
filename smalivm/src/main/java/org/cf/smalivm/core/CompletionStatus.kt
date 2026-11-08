package org.cf.smalivm.core

import java.util.Collections

/**
 * Whether a run explored everything it set out to: the gate for optimizer trust. Trust is all
 * or nothing, so [Partial] has no "mostly done" accessor.
 */
public sealed interface CompletionStatus {
    /** Every path this run set out to explore ran to an end, and none of them aborted. */
    public data object Complete : CompletionStatus

    /**
     * At least one path stopped short of a normal end. Truncation is permanent, so this outranks [InProgress]
     * even while paths are still scheduled. [reasons] is a non-empty immutable snapshot.
     */
    public class Partial(reasons: Set<AbortReason>) : CompletionStatus {
        init {
            require(reasons.isNotEmpty()) { "Partial requires at least one abort reason" }
        }

        internal val reasons: Set<AbortReason> = Collections.unmodifiableSet(LinkedHashSet(reasons))

        override fun equals(other: Any?): Boolean =
            this === other || other is Partial && reasons == other.reasons

        override fun hashCode(): Int = reasons.hashCode()

        override fun toString(): String = "Partial(reasons=$reasons)"
    }

    /**
     * Nothing has aborted yet and paths are still scheduled: a mid-run snapshot such as the debugger takes.
     * Distinct from [Partial] because nothing was truncated, and distinct from [Complete] because a path not yet
     * walked can still abort or disagree.
     */
    public data object InProgress : CompletionStatus
}

/** A strategy may act on consensus only from a run whose exploration completed; every trust check routes here. */
public val CompletionStatus.canTrustConsensus: Boolean
    get() = when (this) {
        is CompletionStatus.Complete -> true
        is CompletionStatus.Partial -> false
        is CompletionStatus.InProgress -> false
    }
