package org.cf.smalivm.core

/**
 * One op execution on one explored path, plus the [Frame] that op produced. [parent] is the control-flow edge
 * only; call ancestry lives on [activation], so a caller's catch handler never inherits a callee's locals.
 *
 * What [frame] holds follows [progress]. It belongs to this node alone: [ExecutionSession] hands every
 * successor a fresh [Frame.copy].
 *
 * [view] is read off [activation], so the scheduler decodes each successor against the node's own method. That
 * lets an `Invoke` enqueue a callee node onto the one frontier instead of nesting a second execution loop.
 */
public class ExecutionNode internal constructor(
    public val address: Int,
    public val op: Op,
    frame: Frame,
    internal val parent: ExecutionNode?,
    /** Which call this node belongs to. Separate from [parent], which is a control-flow edge. */
    internal val activation: Activation,
) {
    /** Register state after [op] ran. */
    public var frame: Frame = frame
        internal set

    /** The method this node executes in. */
    public val view: MethodView get() = activation.view
    private val mutableChildren = mutableListOf<ExecutionNode>()

    /** How far [op] got on this node; see [OpProgress]. */
    internal var progress: OpProgress = OpProgress.Pending
        private set

    /** Successors actually explored from this node; more than one means the path forked. */
    internal val children: List<ExecutionNode> get() = mutableChildren

    /** The path this node ended, with its exact terminal state; null for a node that only continued. */
    private var ending: ActivationExit? = null

    /**
     * Set when this node ends a path: returned, threw, or aborted. Null for a node that only continued; a node
     * that both escaped and forked into handlers carries the escape outcome. A node ends at most one path, so
     * when one op ends several, the others are recorded without a node of their own.
     *
     * The path is the activation's. A callee's `return` carries [Outcome.Returned] here while the run's path
     * continues in the caller, so only the entry activation's endings are also run outcomes.
     */
    internal val outcome: Outcome? get() = ending?.outcome

    /** The state this node's path ended in, or [frame] for a node that ended no path. */
    internal val terminalFrame: Frame get() = ending?.frame ?: frame

    /**
     * How much [op] may have perturbed observable state this engine does not model, on this path. Effects are
     * recorded on the node, never on the op.
     *
     * The scheduler raises it from the effect each returned arm's update declares, and from the
     * invocation, class-initialization, and synchronized-entry rules it owns. Successor nodes start at
     * `NONE`, so the level describes this address rather than every address below it. A node whose op
     * never ran, or whose `execute` raised a host failure, keeps `NONE`.
     *
     * Read `ExploredPaths.sideEffectLevel` unless you want one path: it maxes this over the address's pile
     * and adds `STRONG` for a path that aborted for a reason that [AbortReason.impliesStrongEffect].
     */
    internal var sideEffectLevel: SideEffectLevel = SideEffectLevel.NONE
        internal set

    /** Candidate implementations recorded when receiver dispatch cannot choose one target. */
    internal var dispatchCandidates: Set<String> = emptySet()
        internal set

    /** Why a host call or host field read was refused, retained when the path continues opaquely. */
    internal var invocationDiagnostic: String? = null
        internal set

    /**
     * Frames deep this node sits, 0-based: 0 in the run's own entry activation, 1 inside a method it called.
     * [Limits.maxCallDepth] bounds the 1-based [Activation.frameCount] instead.
     */
    internal val callDepth: Int get() = activation.depth

    /**
     * True for the node a call enters at: its activation's caller is its parent's activation. A caller's resume
     * node also changes activation but is not a call entry. Keyed on activation identity, so direct
     * self-recursion still counts as a call entry. True for the run's own entry node (no parent, no caller).
     * Gates the call-depth bound and the synchronized-method entry effect.
     */
    internal val isCallEntry: Boolean get() = activation.caller === parent?.activation

    /**
     * Stamps the path this node ends. A second stamp is a scheduler bug, not a state to tolerate: it would
     * replace one meaning with another, so a thrown path that then hit a malformed handler address would
     * report `Aborted` instead of `Threw`. It is outside the session's host-failure boundary so that it fails
     * loudly instead of becoming one more aborted path.
     */
    internal fun endWith(exit: ActivationExit) {
        require(ending == null) { "$this already ended a path; cannot also end with ${exit.outcome}" }
        ending = exit
    }

    /** [op]'s `execute` returned. */
    internal fun executeReturned() {
        check(progress == OpProgress.Pending) { "$this already left Pending: $progress" }
        progress = OpProgress.Executed()
    }

    /** [op]'s `execute` threw a host failure. */
    internal fun executeFailed() {
        check(progress == OpProgress.Pending) { "$this already left Pending: $progress" }
        progress = OpProgress.Failed
    }

    /** The scheduler continued this node's path at a following instruction. */
    internal fun observeNormalContinuation() {
        executed().continuedNormally = true
    }

    /** The scheduler routed an exception, or an abort, out of this node. */
    internal fun observeExceptionalContinuation() {
        executed().continuedExceptionally = true
    }

    private fun executed(): OpProgress.Executed =
        progress as? OpProgress.Executed ?: error("$this observed a continuation before execute returned")

    internal fun addChild(child: ExecutionNode) = mutableChildren.add(child)

    override fun toString(): String = "ExecutionNode($op, outcome=$outcome)"
}

/** How far a node's op got. Each node moves out of [Pending] at most once. */
internal sealed interface OpProgress {
    /** `execute` was not entered: the node is scheduled, or a bound stopped it first. Its frame is pre-op. */
    data object Pending : OpProgress

    /** `execute` threw a host failure. The frame holds whatever the op wrote before it threw. */
    data object Failed : OpProgress

    /**
     * `execute` returned, so the frame is this address's post-op state. Records which continuations the
     * scheduler then took, for `ExploredPaths.didEveryExecutionContinueNormally`.
     */
    class Executed internal constructor() : OpProgress {
        internal var continuedNormally: Boolean = false
        internal var continuedExceptionally: Boolean = false

        override fun toString(): String = "Executed"
    }
}
