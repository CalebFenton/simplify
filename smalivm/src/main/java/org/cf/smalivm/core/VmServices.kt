package org.cf.smalivm.core

/**
 * Owns all services for one VM and is the public factory for execution sessions.
 *
 * Execute with `val graph = services.newSession(view).run()`, then inspect [ExecutionGraph.completion] and
 * [ExecutionGraph.outcomes]. Each outcome represents one ended path.
 */
class VmServices(
    val classIndex: ClassIndex,
    internal val reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
) {
    val invocationServices = InvocationServices(classIndex, reflectionPolicy)
    val methodEditor = MethodEditor(classIndex.programWorkspace)
    val currentSnapshot: ProgramSnapshot get() = classIndex.currentSnapshot

    fun newSession(
        view: MethodView,
        limits: Limits = Limits(),
        entryFrame: Frame? = null,
    ): ExecutionSession {
        val snapshot = classIndex.currentSnapshot
        require(snapshot.owns(view)) { "Method view is not current in this program workspace: ${view.signature}" }
        require(entryFrame == null || entryFrame.belongsTo(view)) {
            "Entry frame belongs to another program or method revision: ${view.signature}"
        }
        return ExecutionSession.of(
            view,
            this,
            classIndex.resolver(snapshot),
            limits,
            entryFrame?.sanitizedCopyForPublicEntry(),
        )
    }
}
