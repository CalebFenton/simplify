package org.cf.smalivm.core

/** Why [this] outcome aborted, or null when it did not abort. */
internal val Outcome?.abortReason: AbortReason? get() = (this as? Outcome.Aborted)?.reason

/** The host throwable [this] outcome aborted on, or null when it is not a host-failure abort. */
internal val Outcome?.hostFailure: Throwable? get() = (this as? Outcome.Aborted)?.cause
