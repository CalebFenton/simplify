package org.cf.smalivm.core

/** Mutable publication authority for exactly one imported application program. */
class ProgramWorkspace private constructor(
    private val revisionAuthority: ProgramRevisionAuthority,
    initialSnapshot: ProgramSnapshot,
) {
    internal val programIdentity: ProgramIdentity get() = revisionAuthority.programIdentity
    var currentSnapshot: ProgramSnapshot = initialSnapshot
        private set

    /**
     * Installs a candidate that [MethodEditTransaction.commit] has already validated; the transaction is the one
     * gate for method and DEX-limit rules. Publication checks only that the candidate succeeds the current snapshot.
     */
    internal fun publish(candidateSnapshot: ProgramSnapshot) {
        require(candidateSnapshot.programIdentity === programIdentity) {
            "Cannot publish a snapshot from another program workspace"
        }
        require(candidateSnapshot.programRevision.isAfter(currentSnapshot.programRevision)) {
            "Candidate snapshot must have a new program revision"
        }
        require(candidateSnapshot.predecessorRevision === currentSnapshot.programRevision) {
            "Candidate snapshot was built from a stale program revision"
        }
        require(candidateSnapshot.applicationClasses.values.all { definition ->
            definition.methods.all { it.methodRevision.belongsTo(programIdentity) }
        }) { "Candidate snapshot contains a method revision from another workspace" }
        require(candidateSnapshot.frameworkCatalog === currentSnapshot.frameworkCatalog) {
            "Cannot replace the frozen framework catalog during application publication"
        }
        require(candidateSnapshot.outputApiPolicy === currentSnapshot.outputApiPolicy) {
            "Cannot replace the output API policy during method publication"
        }
        require(candidateSnapshot.hierarchy === currentSnapshot.hierarchy) {
            "Cannot replace the hierarchy during method publication"
        }
        require(
            currentSnapshot.stringInstructionEncoding != StringInstructionEncoding.CONST_STRING_JUMBO ||
                candidateSnapshot.stringInstructionEncoding == StringInstructionEncoding.CONST_STRING_JUMBO,
        ) { "Cannot reduce jumbo string encoding after publication" }
        currentSnapshot = candidateSnapshot
    }

    internal fun newProgramRevision(): ProgramRevision = revisionAuthority.newProgramRevision()

    internal fun newApplicationMethodRevision(methodSignature: String): ApplicationMethodRevision =
        revisionAuthority.newApplicationMethodRevision(methodSignature)

    internal companion object {
        fun fromInitialSnapshot(
            snapshot: ProgramSnapshot,
            revisionAuthority: ProgramRevisionAuthority,
        ): ProgramWorkspace {
            require(snapshot.programIdentity === revisionAuthority.programIdentity)
            return ProgramWorkspace(revisionAuthority, snapshot)
        }
    }
}

internal class ProgramRevisionAuthority private constructor(
    val programIdentity: ProgramIdentity,
    private var nextProgramSequence: Long,
) {
    constructor() : this(ProgramIdentity(), 0)

    fun newProgramRevision(): ProgramRevision =
        ProgramRevision.create(programIdentity, nextProgramSequence++)

    fun newApplicationMethodRevision(methodSignature: String): ApplicationMethodRevision =
        ApplicationMethodRevision(programIdentity, methodSignature)
}
