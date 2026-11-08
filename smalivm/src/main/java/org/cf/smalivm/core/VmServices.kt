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

    /**
     * Starts a run of [view] over the current program snapshot. [entryFrame] defaults to unknown parameters,
     * except a constructor's receiver is a fresh uninitialized object when its field layout is known. A
     * supplied frame is copied, so the caller may start several sessions from one seeded frame.
     * [entryClassState] says what the run assumes about classes the entry path has not triggered; the default
     * treats [view] as reachable from arbitrary program state.
     */
    fun newSession(
        view: MethodView,
        limits: Limits = Limits(),
        entryFrame: Frame? = null,
        entryClassState: EntryClassState = EntryClassState.ARBITRARY,
    ): ExecutionSession = openSession(view, limits, entryFrame, null, OpBuilder::build, entryClassState)

    /**
     * [newSession] with a replacement decoder and, when [backends] is non-null, replacement invocation
     * backends: the one way to run an op or a backend that fails in ways no real opcode or shipping backend
     * can (a host `Throwable` or [VirtualMachineError] mid-call, an arm combination no opcode produces), which
     * scheduler failure-containment tests need.
     */
    internal fun newSession(
        view: MethodView,
        limits: Limits,
        entryFrame: Frame?,
        backends: List<Backend>? = null,
        decode: (Int, MethodView, ProgramSnapshotResolver) -> Op,
    ): ExecutionSession = openSession(view, limits, entryFrame, backends, decode, EntryClassState.ARBITRARY)

    private fun openSession(
        view: MethodView,
        limits: Limits,
        entryFrame: Frame?,
        backends: List<Backend>?,
        decode: (Int, MethodView, ProgramSnapshotResolver) -> Op,
        entryClassState: EntryClassState,
    ): ExecutionSession {
        val snapshot = classIndex.currentSnapshot
        require(snapshot.owns(view)) { "Method view is not current in this program workspace: ${view.signature}" }
        require(entryFrame == null || entryFrame.belongsTo(view)) {
            "Entry frame belongs to another program or method revision: ${view.signature}"
        }
        val resolver = classIndex.resolver(snapshot)
        val frame = entryFrame?.sanitizedCopyForPublicEntry() ?: Frame.entryFrame(view, resolver)
        frame.enterSession(entryClassState, ClassInitPlanner(resolver))
        return ExecutionSession(
            view,
            this,
            limits,
            frame,
            resolver,
            decode,
            if (backends == null) Invoker(resolver, invocationServices) else Invoker(resolver, backends),
        )
    }
}
