package org.cf.smalivm.core

/** Transaction-bound request for a logical code position. */
class PendingCodeAnchor internal constructor(private val transactionIdentity: Any) {
    internal fun belongsTo(identity: Any): Boolean = transactionIdentity === identity
}
