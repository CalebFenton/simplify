package org.cf.smalivm.core

/** Opaque revision token for one immutable application method definition. */
class ApplicationMethodRevision private constructor(
    private val programIdentity: ProgramIdentity,
    internal val methodSignature: String,
    private val sequence: Long,
) {
    internal fun belongsTo(identity: ProgramIdentity): Boolean = programIdentity === identity

    internal fun identifies(signature: String): Boolean = methodSignature == signature

    internal companion object {
        fun create(
            programIdentity: ProgramIdentity,
            methodSignature: String,
            sequence: Long,
        ): ApplicationMethodRevision = ApplicationMethodRevision(programIdentity, methodSignature, sequence)
    }
}
