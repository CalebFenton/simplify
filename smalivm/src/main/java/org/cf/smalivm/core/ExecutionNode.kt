package org.cf.smalivm.core

/**
 * One op execution on one explored path (spec §4.3). Carries **only** control-flow/trace meaning
 * plus the [Frame] that op produced. It is deliberately not state/heap/call ancestry as well: the
 * old engine's single parent edge encoded all four at once, which is how a caller's catch handler
 * ended up inheriting a callee's locals.
 *
 * What [frame] holds follows [progress]. It belongs to this node alone -- [ExecutionSession] hands every
 * successor a fresh [Frame.copy], so no two nodes ever share one.
 *
 * [view] is which method this node belongs to. It lives on the *node*, not only on the session, so
 * the scheduler decodes each successor against the node's own method rather than the session's entry
 * method -- which is what lets an `Invoke` transition enqueue a callee node straight onto the one
 * frontier instead of nesting a second execution loop, which would re-split `step()` and stop Plan 10's
 * debugger single-stepping into a callee.
 *
 * **[view] is read off [activation] rather than stored twice.** A node's method and the activation whose
 * pile and visit budget it belongs to are the same fact, and two copies of one fact is how a callee node
 * ends up decoded against one method and recorded against another.
 */
class ExecutionNode internal constructor(
    val address: Int,
    val op: Op,
    frame: Frame,
    val parent: ExecutionNode?,
    /**
     * Which **call** this node belongs to (spec §4.3). Separate from [parent], which is a control-flow
     * edge: the old engine's single parent edge encoded both at once, which is how a caller's catch
     * handler ended up inheriting a callee's locals.
     */
    val activation: Activation,
) {
    var frame: Frame = frame
        internal set

    val view: MethodView get() = activation.view
    private val mutableChildren = mutableListOf<ExecutionNode>()

    /** How far [op] got on this node; see [OpProgress]. */
    var progress: OpProgress = OpProgress.Pending
        private set

    /** Successors actually explored from this node; more than one means the path forked. */
    val children: List<ExecutionNode> get() = mutableChildren

    /** The path this node ended, with its exact terminal state; null for a node that only continued. */
    private var ending: ActivationExit? = null

    /**
     * Set when this node ends a path: returned, threw, or aborted. Null for a node that only continued; a node
     * that both escaped and forked into handlers carries the escape outcome. A node ends at most one path, so
     * when one op ends several, the others are recorded without a node of their own.
     *
     * The path is the ACTIVATION's. A callee's `return` carries [Outcome.Returned] here while the run's path
     * continues in the caller at a resumed node this one is the parent of, so `ExecutionSession.outcomes` has
     * no matching entry. Only the entry activation's endings are also run outcomes.
     */
    val outcome: Outcome? get() = ending?.outcome

    /** The state this node's path ended in, or [frame] for a node that ended no path. */
    internal val terminalFrame: Frame get() = ending?.frame ?: frame

    /**
     * How much [op] may have perturbed observable state this engine does not model, on **this** path
     * (spec §4.5: effects are recorded on the node, never on the op).
     *
     * The scheduler raises it from the effect each returned arm's update declares, and from the
     * invocation, class-initialization, and synchronized-entry rules it owns. Successor nodes start at
     * `NONE`, so the level describes this address rather than every address below it. A node whose op
     * never ran, or whose `execute` raised a host failure, keeps `NONE`.
     *
     * Read `ExploredPaths.sideEffectLevel` unless you want one path: it maxes this over the address's pile
     * and adds `STRONG` for a path that aborted for a reason that [AbortReason.impliesStrongEffect].
     */
    var sideEffectLevel: SideEffectLevel = SideEffectLevel.NONE
        internal set

    /** Candidate implementations recorded when receiver dispatch cannot choose one target. */
    var dispatchCandidates: Set<String> = emptySet()
        internal set

    /** Human-readable backend/binding refusal retained when resolved decision 6 continues opaquely. */
    var invocationDiagnostic: String? = null
        internal set

    /**
     * Frames deep this node sits, **0-based**: 0 in the run's own entry activation, 1 inside a method it
     * called, and so on. Read off [activation] rather than counted along [parent], because the parent chain
     * is control flow -- and because reading a stored depth avoids the ancestry walk whose exception
     * parentage `execution-engine.md:180` records as able to distort the oracle's count.
     *
     * ⚠️ **NOT the same origin as the old engine's `ExecutionNode.callDepth`, which is 1-based**, and an
     * earlier revision of this KDoc claimed it was. [Limits.maxCallDepth] is compared against
     * [Activation.frameCount], which is the value that does share the oracle's origin; see
     * [Activation.depth].
     */
    val callDepth: Int get() = activation.depth

    /**
     * True for the node a **call enters at**: the node whose activation was created by the call its parent
     * made. Where the call-depth bound fires, matching the old engine, which checks it only at a callee's
     * entrypoint (`VirtualMachine.kt`'s `current.isEntrypoint` branch) rather than at every step. True for
     * the run's own entry node too (no parent, no caller), harmlessly: its frame count is 1.
     *
     * **The predicate is "my caller is my parent's activation", not "my activation differs from my
     * parent's"**, and the weaker form was wrong in a way the name hid: a caller-RESUME node also has an
     * activation different from its parent's (its parent is the callee's returning node), so the weaker form
     * called a *return* a call entry. Harmless for the depth bound -- a resume node's frame count is lower
     * than the one that was already checked on the way in -- but it is a false claim, and
     * `core/CallStackTest` asserts this property as though it meant "call entry".
     *
     * A continuation cannot satisfy it: that would need `activation.caller === activation`, a cycle
     * [Activation] cannot have.
     *
     * **A capability the oracle does not have, worth recording rather than leaving invisible.** The oracle's
     * `isEntrypoint` is `address == 0 && parent.method != method`, so for **direct self-recursion** the
     * callee's entry node has the same method as its parent and answers `false` -- which means the oracle
     * skips the call-depth check entirely for the one shape that most needs it. Keying on activation identity
     * instead has no such blind spot, which is why `core/CallStackTest`'s recursive fixture terminates here.
     */
    val isCallEntry: Boolean get() = activation.caller === parent?.activation

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
sealed interface OpProgress {
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
