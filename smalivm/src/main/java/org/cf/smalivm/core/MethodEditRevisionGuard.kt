package org.cf.smalivm.core

internal class MethodEditRevisionGuard private constructor(
    val methodSignature: MethodSignature,
    val methodRevision: ApplicationMethodRevision?,
    val programRevision: ProgramRevision?,
) {
    fun validate(workspace: ProgramWorkspace): MethodEditRejectionReason? {
        val current = workspace.currentSnapshot
        if (programRevision != null && current.programRevision !== programRevision) {
            return MethodEditRejectionReason.StaleProgramRevision
        }
        val currentMethod = current.applicationMethod(methodSignature)
        if (methodRevision == null ||
            !methodRevision.belongsTo(workspace.programIdentity) ||
            currentMethod?.methodRevision !== methodRevision
        ) {
            return MethodEditRejectionReason.StaleMethodRevision
        }
        return null
    }

    companion object {
        fun methodOnly(view: MethodView): MethodEditRevisionGuard =
            MethodEditRevisionGuard(view.signature, view.methodRevision, null)

        fun trustedGraph(graph: TrustedGraphView): MethodEditRevisionGuard =
            MethodEditRevisionGuard(graph.methodView.signature, graph.methodRevision, graph.programRevision)
    }
}
