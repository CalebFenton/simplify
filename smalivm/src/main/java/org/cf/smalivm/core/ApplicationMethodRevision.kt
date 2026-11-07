package org.cf.smalivm.core

/** Opaque revision token for one immutable application method definition. */
class ApplicationMethodRevision internal constructor(
    private val programIdentity: ProgramIdentity,
    internal val methodSignature: String,
) {
    internal fun belongsTo(identity: ProgramIdentity): Boolean = programIdentity === identity

    internal fun identifies(signature: String): Boolean = methodSignature == signature
}
