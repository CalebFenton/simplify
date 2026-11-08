package org.cf.smalivm.core

/** Stateless service that starts isolated method edit transactions. */
public class MethodEditor internal constructor(private val workspace: ProgramWorkspace) {
    /** Starts an edit of [methodView] that rejects commit if the method changed. */
    public fun beginEdit(methodView: MethodView): MethodEditTransaction = MethodEditTransaction(
        workspace,
        methodView,
        MethodEditRevisionGuard.methodOnly(methodView),
    )

    /** Starts an edit based on [trustedGraph] that rejects commit if the program changed. */
    public fun beginEdit(trustedGraph: TrustedGraphView): MethodEditTransaction = MethodEditTransaction(
        workspace,
        trustedGraph.methodView,
        MethodEditRevisionGuard.trustedGraph(trustedGraph),
    )
}
