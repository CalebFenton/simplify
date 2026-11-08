package org.cf.smalivm.core

/** Opaque identity of one immutable program snapshot. */
public class ProgramRevision private constructor(
    private val programIdentity: ProgramIdentity,
    private val sequence: Long,
) {
    internal fun belongsTo(identity: ProgramIdentity): Boolean = programIdentity === identity

    internal fun isAfter(other: ProgramRevision): Boolean =
        programIdentity === other.programIdentity && sequence > other.sequence

    internal companion object {
        fun create(programIdentity: ProgramIdentity, sequence: Long): ProgramRevision =
            ProgramRevision(programIdentity, sequence)
    }
}
