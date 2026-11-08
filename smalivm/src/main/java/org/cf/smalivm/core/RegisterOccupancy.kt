package org.cf.smalivm.core

/**
 * Whether any path in scope holds a value in a register: which of the facts behind a [Value.Top] consensus
 * applies. The pairings a consumer may act on are in `docs/architecture/graph-optimizer-contract.md`.
 */
enum class RegisterOccupancy {
    /**
     * At least one path holds a value here: a [Value.Known], a [Value.Unknown], or a real [Value.Top] that a
     * `move` from an untouched register put there. With a [Value.Top] consensus this means live and unknowable.
     */
    HELD,

    /**
     * Paths in scope exist and none holds a readable value here. Not "free": the high half of a live wide pair
     * and a register outside the method also answer this. [ExecutionGraph.availableRegisters] answers "free".
     */
    NEVER_ASSIGNED,

    /**
     * There is no path in scope: no op ran at the address, or, for the terminating query, no path returned. It
     * does not separate "never attempted" from "stopped by a bound"; [ExecutionGraph.wasAttempted] does.
     */
    NO_PATH,
}
