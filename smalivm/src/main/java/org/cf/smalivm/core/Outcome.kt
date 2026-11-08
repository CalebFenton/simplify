package org.cf.smalivm.core

/**
 * Why one explored path stopped short of a normal end (spec §4.4).
 *
 * [ADDRESS_LIMIT], [METHOD_LIMIT], [CALL_DEPTH] and [TIMEOUT] fire before an op's `execute` runs, so no
 * half-completed mutation is possible on those paths; [HOST_FAILURE] can fire in the middle of one.
 *
 * Every abort makes the run [CompletionStatus.Partial], hence `ExecutionGraph.trusted()` null. An abort inside
 * an ordinary callee also lets the caller continue past the call through a conservative `STRONG` opaque
 * continuation instead of ending the caller's path.
 */
enum class AbortReason {
    ADDRESS_LIMIT, METHOD_LIMIT, CALL_DEPTH, TIMEOUT,

    /**
     * The address holds a real instruction this build cannot execute. The only reason that
     * [impliesStrongEffect]: nothing is known about what the instruction would have done.
     */
    UNSUPPORTED_OP,

    /**
     * This engine, or its host, threw where it should not have: a `Throwable` out of an op's `execute`, a
     * decode, or handler resolution (spec §7). The only reason that means a bug in this codebase, and the
     * only one whose [Outcome.Aborted] carries a [Outcome.Aborted.cause].
     *
     * Kept apart from [MALFORMED_INPUT] so the rate of host failures stays a signal: obfuscated input
     * produces malformed targets routinely.
     */
    HOST_FAILURE,

    /**
     * The bytecode names a jump, fall-through or exception-handler target the method does not contain.
     * Nothing threw and nothing in this engine is broken.
     *
     * Does not [impliesStrongEffect]: the op at the recording address ran and declared its own effect, and the
     * missing target is detected before any successor frame is built. Handlers abandoned with a malformed table
     * stay unexplored, which the `Partial` status already covers
     * (`CommitTest.aMalformedTargetDeclaresNoSideEffectAtTheAddressThatNamedIt`). A malformed
     * `fill-array-data` payload stays [UNSUPPORTED_OP] (see `Op.kt`'s `buildFillArrayData`).
     */
    MALFORMED_INPUT,
    ;

    /** Whether a path aborted for this reason makes its address's side-effect level `STRONG`. */
    val impliesStrongEffect: Boolean get() = this == UNSUPPORTED_OP
}

/**
 * The result of one explored path (spec §4.4). [Threw] carries the actual guest throwable [Value], identity
 * preserved end-to-end.
 *
 * Read outcomes from the snapshot returned by `vmServices.newSession(view).run()`. Treat that list as a
 * multiset: path multiplicity is semantic, but traversal order is not. This does not promise deep immutability
 * of a [Value]'s payload.
 */
sealed interface Outcome {
    /** Normal return; [value] is null for void methods. */
    data class Returned(val value: Value?) : Outcome
    data class Threw(val exception: Value) : Outcome

    /** [cause] is the host throwable, present exactly when [reason] is [AbortReason.HOST_FAILURE]. */
    data class Aborted(val reason: AbortReason, val cause: Throwable? = null) : Outcome {
        init {
            require((reason == AbortReason.HOST_FAILURE) == (cause != null)) {
                "A host-failure abort carries its throwable and no other abort does: $reason, $cause"
            }
        }
    }
}
