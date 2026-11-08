package org.cf.smalivm.core

/**
 * One op execution on one explored path (spec §4.3). Carries **only** control-flow/trace meaning
 * plus the [Frame] that op produced. It is deliberately not state/heap/call ancestry as well: the
 * old engine's single parent edge encoded all four at once, which is how a caller's catch handler
 * ended up inheriting a callee's locals.
 *
 * [frame] is pre-op while this node waits on the frontier. After [opExecutionStarted] becomes true,
 * it contains whatever state [op] produced before it returned or threw. It belongs to this node alone --
 * [ExecutionSession] hands every successor a fresh [Frame.copy], so no two nodes ever share one.
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

    /** True once [ExecutionSession] is about to call [op].`execute`; false for a pre-op limit abort. */
    var opExecutionStarted: Boolean = false
        internal set

    /** Successors actually explored from this node; more than one means the path forked. */
    val children: List<ExecutionNode> get() = mutableChildren

    /**
     * Set when this node ends a path: returned, threw, or aborted. Null for a node that only
     * continued; a node that both escaped and forked into handlers carries the escape outcome (see
     * [endWith]).
     *
     * An op whose ambiguity ends SEVERAL paths at once (Task 7's `aget`, unproven-null array at an
     * unproven-in-range index) still leaves only its first ending here: a node ends at most one path,
     * so the rest are recorded unattributed, with no node of their own (see
     * `ExecutionSession.recordUnattributedEnding`).
     *
     * **What "a path" means here is the ACTIVATION's path, which is not always the RUN's.** A callee's
     * `return` ends that activation and carries [Outcome.Returned] here -- while the run's own path
     * continues, in the caller, at the resumed node this one is the parent of. So a node can hold
     * `Returned` and still have [children], and `ExecutionSession.outcomes` will not have a matching entry:
     * that list is one `Outcome` per path of the RUN. Only the entry activation's `return` is both.
     */
    var outcome: Outcome? = null
        private set

    /** Exact state of this terminal path. [frame] can remain a conservative multi-arm source summary. */
    internal var endingFrame: Frame? = null
        private set

    /** The state this node's path ended in, or [frame] for a node that ended no path. */
    internal val terminalFrame: Frame get() = endingFrame ?: frame

    /**
     * The host `Throwable` that ended a path at this node, when the corresponding [Outcome] is
     * `Aborted(HOST_FAILURE)`. Kept so a host bug converted into a bounded path abort is still
     * debuggable: swallowing it would leave an `Aborted(HOST_FAILURE)` with no way to find out what
     * actually broke. Null when the abort was *detected* rather than caught (malformed control flow
     * off the end of a method).
     *
     * Usually that outcome is this node's own [outcome], but not necessarily: under
     * `OnUnbuildable.SKIP_ONE` a decode failure in ONE successor of a fork aborts only that lost path
     * while this node keeps its live siblings, so a non-null `hostFailure` can sit on a node whose
     * own [outcome] is null (see `ExecutionSession.buildSuccessor`/`recordLostPath`). This node is
     * where the failure HAPPENED, which is the question a debugger asks; it is not a claim that this
     * node ended.
     *
     * Consequently this holds **at most one** throwable per node, not one per lost path: if several
     * successors of the same fork fail to decode, each overwrites the previous and only the last
     * survives. Every loss still gets its own [Outcome], so nothing is under-counted where it matters
     * -- but do not read this field as "the throwable for lost path N".
     */
    var hostFailure: Throwable? = null
        internal set

    /**
     * How much [op] may have perturbed observable state this engine does not model, on **this** path
     * (spec §4.5: effects are recorded on the node, never on the op).
     *
     * The scheduler raises it from the effect each returned arm's update declares, and from the
     * invocation, class-initialization, and synchronized-entry rules it owns. Successor nodes start at
     * `NONE`, so the level describes this address rather than every address below it. A node whose op
     * never ran, or whose `execute` raised a host failure, keeps `NONE`.
     *
     * **Read `ExecutionGraph.sideEffectLevel` rather than this field**, unless you specifically want one
     * path: that query maxes this over the whole node pile and folds in the separate
     * `Aborted(UNSUPPORTED_OP)` rule, which is a claim about an op that never ran and so could never have
     * declared anything.
     */
    var sideEffectLevel: SideEffectLevel = SideEffectLevel.NONE
        internal set

    /** Candidate implementations recorded when receiver dispatch cannot choose one target. */
    var dispatchCandidates: Set<String> = emptySet()
        internal set

    /** Human-readable backend/binding refusal retained when resolved decision 6 continues opaquely. */
    var invocationDiagnostic: String? = null
        internal set

    /** Scheduler evidence used by the graph's normal-continuation query. */
    internal var normalContinuationObserved: Boolean = false
    internal var exceptionalContinuationObserved: Boolean = false

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
     * Stamps this node's terminal [outcome]. A node ends **at most one** path, so a second stamp is a
     * scheduler bug rather than a state to tolerate: it would double-count in
     * [ExecutionSession.outcomes] *and* silently replace one meaning with another -- a genuinely
     * thrown path that then hit a malformed handler address would report `Aborted` instead of
     * `Threw`. Loud on purpose, and deliberately **outside** the session's host-failure boundary so
     * that it surfaces as a bug instead of being swallowed as one more aborted path.
     *
     * A node may legitimately carry an outcome *and* [children] (from Task 7 on, an ambiguous throw
     * both escapes and forks into handlers); what is forbidden is ending twice. When the SAME op ends
     * more than one path (every ending a guest throw -- `ExecutionSession.applyAll` enforces that mix),
     * only the first reaches here; the rest are recorded through `ExecutionSession.recordUnattributedEnding`
     * instead of a second call to this method.
     */
    internal fun endWith(outcome: Outcome, exactFrame: Frame = frame) {
        require(this.outcome == null) {
            "$this already ended a path; cannot also end with $outcome"
        }
        this.outcome = outcome
        endingFrame = exactFrame
    }

    internal fun addChild(child: ExecutionNode) = mutableChildren.add(child)

    override fun toString(): String =
        "ExecutionNode($op, outcome=$outcome${hostFailure?.let { ", hostFailure=$it" } ?: ""})"
}
