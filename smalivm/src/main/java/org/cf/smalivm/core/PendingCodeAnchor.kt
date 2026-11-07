package org.cf.smalivm.core

/** Transaction-bound request for a logical code position. */
class PendingCodeAnchor internal constructor(
    private val transactionIdentity: Any,
    private val sequence: Int,
) {
    internal fun belongsTo(identity: Any): Boolean = transactionIdentity === identity

    override fun toString(): String = "pending-anchor[$sequence]"
}
