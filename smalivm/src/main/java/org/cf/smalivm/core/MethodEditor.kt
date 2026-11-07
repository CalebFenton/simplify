package org.cf.smalivm.core

/** Stateless service that starts isolated method edit transactions. */
class MethodEditor internal constructor(private val workspace: ProgramWorkspace) {
    fun beginEdit(methodView: MethodView): MethodEditTransaction = MethodEditTransaction(
        workspace,
        methodView,
        MethodEditRevisionGuard.methodOnly(methodView),
    )

    fun beginEdit(trustedGraph: TrustedGraphView): MethodEditTransaction = MethodEditTransaction(
        workspace,
        trustedGraph.methodView,
        MethodEditRevisionGuard.trustedGraph(trustedGraph),
    )
}
