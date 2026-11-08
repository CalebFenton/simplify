package org.cf.smalivm.core

/**
 * Owns all services for one VM and is the public factory for execution sessions.
 *
 * Execute with `val graph = services.newSession(view).run()`, then inspect [ExecutionGraph.completion] and
 * [ExecutionGraph.outcomes]. Each outcome represents one ended path.
 */
public class VmServices(
    public val classIndex: ClassIndex,
    internal val reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
) {
    /** Backends and emulators that sessions invoke through. */
    public val invocationServices: InvocationServices = InvocationServices(classIndex, reflectionPolicy)
    /** Starts method edits against the current program. */
    public val methodEditor: MethodEditor = MethodEditor(classIndex.programWorkspace)
    /** The program revision new sessions run against. */
    public val currentSnapshot: ProgramSnapshot get() = classIndex.currentSnapshot

    /**
     * Starts a run of [view] over the current program snapshot. [entryFrame] defaults to unknown parameters,
     * except a constructor's receiver is a fresh uninitialized object when its field layout is known. A
     * supplied frame is copied, so the caller may start several sessions from one seeded frame.
     */
    public fun newSession(
        view: MethodView,
        limits: Limits = Limits(),
        entryFrame: Frame? = null,
    ): ExecutionSession = newSession(view, limits, entryFrame, decode = OpBuilder::build)

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
    ): ExecutionSession {
        val snapshot = classIndex.currentSnapshot
        require(snapshot.owns(view)) { "Method view is not current in this program workspace: ${view.signature}" }
        require(entryFrame == null || entryFrame.belongsTo(view)) {
            "Entry frame belongs to another program or method revision: ${view.signature}"
        }
        val resolver = classIndex.resolver(snapshot)
        return ExecutionSession(
            view,
            this,
            limits,
            entryFrame?.sanitizedCopyForPublicEntry() ?: Frame.entryFrame(view, resolver),
            resolver,
            decode,
            if (backends == null) Invoker(resolver, invocationServices) else Invoker(resolver, backends),
        )
    }
}
