package org.cf.smalivm.core

/** Terminal result of one method edit transaction. */
sealed interface MethodEditCommitResult {
    class Committed internal constructor(
        val editedMethodView: MethodView,
        val programRevision: ProgramRevision,
        createdMethodRevisionsBySignature: Map<String, ApplicationMethodRevision>,
        codeAddressesByPendingAnchor: Map<PendingCodeAnchor, Int>,
    ) : MethodEditCommitResult {
        val createdMethodRevisionsBySignature: Map<String, ApplicationMethodRevision> =
            java.util.Collections.unmodifiableMap(LinkedHashMap(createdMethodRevisionsBySignature))
        val codeAddressesByPendingAnchor: Map<PendingCodeAnchor, Int> =
            java.util.Collections.unmodifiableMap(LinkedHashMap(codeAddressesByPendingAnchor))
    }

    data object Unchanged : MethodEditCommitResult

    data class Rejected(val reason: MethodEditRejectionReason) : MethodEditCommitResult
}
