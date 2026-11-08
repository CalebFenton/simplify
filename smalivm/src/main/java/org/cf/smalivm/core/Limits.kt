package org.cf.smalivm.core

/**
 * Exploration bounds. All values must be non-negative, and `0` disables a bound.
 *
 * A limit is a bound, never a correctness mechanism. A run-level or class-initializer abort makes the run
 * [CompletionStatus.Partial]; an ordinary nested callee abort becomes a conservative `STRONG` opaque caller
 * continuation. [maxExecutionTimeMillis] is disabled by default because a wall-clock bound makes results
 * non-reproducible; with it disabled the session never reads the clock. [maxAddressVisits] permits exactly that
 * many visits of one address on one path and aborts the next one.
 */
public data class Limits(
    val maxAddressVisits: Int = 500,
    val maxSteps: Int = 1_000_000,
    val maxExecutionTimeMillis: Long = 0,
    /**
     * Aborts a callee's entry with [AbortReason.CALL_DEPTH] when its [Activation.frameCount] exceeds this. The
     * count is 1-based, so the default permits exactly 20 frames. This is the only bound on guest recursion.
     */
    val maxCallDepth: Int = 20,
) {
    init {
        require(maxAddressVisits >= 0) { "maxAddressVisits must be non-negative: $maxAddressVisits" }
        require(maxSteps >= 0) { "maxSteps must be non-negative: $maxSteps" }
        require(maxExecutionTimeMillis >= 0) {
            "maxExecutionTimeMillis must be non-negative: $maxExecutionTimeMillis"
        }
        require(maxCallDepth >= 0) { "maxCallDepth must be non-negative: $maxCallDepth" }
    }
}
