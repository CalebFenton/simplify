package org.cf.smalivm.core

/** Transaction-local identity of one executable instruction in the baseline body. */
internal class BaselineInstructionId(
    private val transactionIdentity: Any,
    internal val ordinal: Int,
) {
    internal fun belongsTo(identity: Any): Boolean = transactionIdentity === identity
}
