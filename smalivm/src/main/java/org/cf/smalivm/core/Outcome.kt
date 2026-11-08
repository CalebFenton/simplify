package org.cf.smalivm.core

/**
 * Why one explored path stopped short of a normal end.
 *
 * [ADDRESS_LIMIT], [METHOD_LIMIT], [CALL_DEPTH] and [TIMEOUT] fire before an op's `execute` runs, so no
 * half-completed mutation is possible on those paths; [HOST_FAILURE] can fire in the middle of one.
 *
 * Every abort makes the run [CompletionStatus.Partial], hence `ExecutionGraph.trusted()` null. An abort inside
 * an ordinary callee also lets the caller continue past the call through a conservative `STRONG` opaque
 * continuation instead of ending the caller's path.
 */
public enum class AbortReason {
    ADDRESS_LIMIT, METHOD_LIMIT, CALL_DEPTH, TIMEOUT,

    /**
     * The address holds a real instruction this build cannot execute. The only reason that
     * [impliesStrongEffect]: nothing is known about what the instruction would have done.
     */
    UNSUPPORTED_OP,

    /**
     * This engine, or its host, threw where it should not have: a `Throwable` out of an op's `execute`, a
     * decode, or handler resolution. The only reason that means a bug in this codebase, and the only one whose
     * [Outcome.Aborted] carries a [Outcome.Aborted.cause]. Kept apart from [MALFORMED_INPUT] so host failures
     * stay a signal: obfuscated input produces malformed targets routinely.
     */
    HOST_FAILURE,

    /**
     * The bytecode names a jump, fall-through or exception-handler target the method does not contain.
     * Does not [impliesStrongEffect]: the op at the recording address ran and declared its own effect, and the
     * missing target is detected before any successor frame is built. A malformed `fill-array-data` payload
     * decodes to [UNSUPPORTED_OP] instead.
     */
    MALFORMED_INPUT,
    ;

    /** Whether a path aborted for this reason makes its address's side-effect level `STRONG`. */
    internal val impliesStrongEffect: Boolean get() = this == UNSUPPORTED_OP
}

/**
 * The result of one explored path. [Threw] carries the actual guest throwable [Value], identity preserved.
 * [ExecutionGraph.outcomes] is a multiset: path multiplicity is semantic, but traversal order is not. A
 * [Value]'s payload is not promised to be deeply immutable.
 */
public sealed interface Outcome {
    /** Normal return; [value] is null for void methods. */
    public data class Returned(val value: Value?) : Outcome
    /** Abrupt completion with [exception]. */
    public data class Threw(val exception: Value) : Outcome

    /** [cause] is the host throwable, present exactly when [reason] is [AbortReason.HOST_FAILURE]. */
    public data class Aborted(val reason: AbortReason, val cause: Throwable? = null) : Outcome {
        init {
            require((reason == AbortReason.HOST_FAILURE) == (cause != null)) {
                "A host-failure abort carries its throwable and no other abort does: $reason, $cause"
            }
        }
    }
}
