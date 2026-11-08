package org.cf.smalivm.core

/**
 * What the engine can prove about "is the value in this register an instance of `C`?". `instance-of` and
 * `check-cast` map it differently:
 *
 * | outcome | `instance-of` | `check-cast` |
 * |---|---|---|
 * | [ReadsAsNull] | `0` | succeeds |
 * | [Definitely] | `1` | succeeds |
 * | [Never] | `0` | throws `ClassCastException` |
 * | [NullOrInstance] | unknown | succeeds |
 * | [Unprovable] | unknown | forks |
 */
internal sealed interface CastOutcome {
    /** The register reads as a null reference ([Value.readsAsNullReference]). */
    data object ReadsAsNull : CastOutcome

    /** Some type the value is known to have satisfies the target. */
    data object Definitely : CastOutcome

    /** Provably not an instance, and provably not null. */
    data object Never : CastOutcome

    /**
     * Not a particular object, but its tag satisfies the target, so it holds null or an instance: every
     * content passes a cast, while an instance test cannot answer.
     */
    data object NullOrInstance : CastOutcome

    /**
     * Nothing decides it: an unknown whose tag does not satisfy, `Top`, a known reference whose class cannot
     * be determined, or a class walk that cannot answer.
     */
    data object Unprovable : CastOutcome
}
