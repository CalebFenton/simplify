package org.cf.smalivm.differential

/** A normalized execution observation, from either the current core or an independent ART recording. */
interface SmaliVmUnderTest {
    val name: String

    /**
     * Core registers are consensus over returning entry paths. Payload pseudo-addresses are excluded.
     * UNKNOWN is a typed unknown; ABSENT means no returning path held the register; CONFLICTED means
     * a held value has no compatible joined value. Do not observe the reserved high half of a wide pair.
     * Reprs erase object identity but preserve array contents/cycles and uninitialized object state.
     * Exceptions use binary class name plus ':' plus message (literal 'null' when unavailable).
     * Return values come from outcomes, not an assumed register, and describe runtime reference types.
     * A throwing-only or void run has no return value; a known null return is ValueKind.NULL.
     * Truncated exploration is reported as aborted and must not be mistaken for trusted consensus.
     * Device recordings contain only returns or exception types; compare via deviceComparable.
     */
    fun run(fixture: Fixture): NormalizedResult
}

data class Fixture(
    val id: String,
    val className: String,
    val methodDescriptor: String,
    val observeRegisters: Set<Int> = setOf(0),
    /** Register -> (host seed, Dalvik descriptor); admitted as one graph to preserve aliases. */
    val seededRegisters: Map<Int, Pair<Any?, String>> = emptyMap(),
)
