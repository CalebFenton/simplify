package org.cf.smalivm.core

/** Terminal result of one method edit transaction. */
public sealed interface MethodEditCommitResult {
    /** The edit was applied. */
    public class Committed internal constructor(
        internal val editedMethodView: MethodView,
        internal val programRevision: ProgramRevision,
        createdMethodRevisionsBySignature: Map<MethodSignature, ApplicationMethodRevision>,
        codeAddressesByPendingAnchor: Map<PendingCodeAnchor, Int>,
    ) : MethodEditCommitResult {
        internal val createdMethodRevisionsBySignature: Map<MethodSignature, ApplicationMethodRevision> =
            java.util.Collections.unmodifiableMap(LinkedHashMap(createdMethodRevisionsBySignature))
        internal val codeAddressesByPendingAnchor: Map<PendingCodeAnchor, Int> =
            java.util.Collections.unmodifiableMap(LinkedHashMap(codeAddressesByPendingAnchor))
    }

    /** The edit had no commands. */
    public data object Unchanged : MethodEditCommitResult

    /** The edit was refused for [reason]; the program is unchanged. */
    public data class Rejected(val reason: MethodEditRejectionReason) : MethodEditCommitResult
}
