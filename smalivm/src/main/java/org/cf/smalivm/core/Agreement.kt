package org.cf.smalivm.core

/**
 * The answer of a query that asks every path in scope for one value of some kind and succeeds only when all of
 * them hold the same one. Each way of failing is its own case, so "the paths disagree" never reads like "nothing
 * ran here".
 */
public sealed interface Agreement<out T> {
    /** Every path in scope holds [value]. */
    public data class Agreed<out T>(val value: T) : Agreement<T>

    /** Every path in scope holds a value of the asked kind, and they are not all the same. */
    public data object Disagree : Agreement<Nothing>

    /** At least one path in scope holds no value of the asked kind. */
    public data object Absent : Agreement<Nothing>

    /** There is no path in scope: no op ran at the addresses asked about, or no path returned. */
    public data object NoPath : Agreement<Nothing>
}
