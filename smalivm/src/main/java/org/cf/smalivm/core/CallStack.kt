package org.cf.smalivm.core

/**
 * One **activation** of one method: the thing that exists once, per call, for as long as that call is
 * being explored (spec §4.3, §5.5). Before Plan 9 a run had exactly one, so nothing needed to name it;
 * every scheduler map was keyed by a bare address and that was unambiguous. It is not any more, and this
 * type is what makes the ambiguity impossible rather than documented against.
 *
 * **Identity, not equality, is the key** -- [Activation] deliberately defines no `equals`/`hashCode`.
 * Two calls of the same method from the same call site are two activations and must never share a node
 * pile; a structural key could not tell them apart, and self-recursion makes that case
 * routine rather than exotic.
 *
 * **Per activation, NOT per path.** A fork *inside* a method (an ambiguous throw, a `[Continue, Throw]`
 * op) leaves both successors in the SAME activation. This is the wrong home for path-local state.
 * Plan 9's class-init lifecycle is scoped per path (plan resolved decision 9, over
 * `state-model.md:160`'s existential merge leak), which is why the STATE lives in the [Frame] -- one per
 * node, one per path -- and only [classInitContext], which names what this activation initializes, lives here.
 * Putting the state here would reproduce the leak with a narrower scope.
 * A synchronous backend call creates no activation. It executes inside the caller's invoke node.
 *
 * The caller edge ([caller]) is a **call** edge, deliberately separate from [ExecutionNode.parent], which
 * is a control-flow edge. The old engine's single parent edge encoded both at once, which is how a
 * caller's catch handler ended up inheriting a callee's locals.
 */
class Activation internal constructor(
    /** Which method this activation is running. [ExecutionNode.view] reads it from here. */
    val view: MethodView,
    /** The activation that called this one; null for the run's own entry activation. */
    val caller: Activation?,
    /**
     * Where the caller continues when this activation returns; null for the entry activation, whose
     * return ends a path of the run instead (see [ExecutionSession] `planReturn`).
     */
    internal val returnSite: ReturnSite?,
    /**
     * The class this activation is the `<clinit>` **of**, when it is a class-init prelude the scheduler
     * synthesized rather than a call the bytecode made; null for every ordinary activation.
     *
     * The lifecycle's two outcome transitions read it, and neither is expressible without it. When this
     * activation RETURNS, that class becomes [ClassInitState.INITIALIZED]; when a throw escapes it, that class
     * becomes [ClassInitState.FAILED]. Nothing else can answer "which class did the initializer that just
     * finished belong to": the returning node's own `view` names the `<clinit>`'s DECLARING class, which is
     * the same thing today but would silently be the wrong thing the moment a `<clinit>` were resolved through
     * anything but a declared-only lookup ([ClassIndex.lookupClassInitializer] exists because the oracle's
     * ancestor-searching lookup does exactly that).
     *
     * It is **not** state a path carries: it is a property of the call, exactly like [view], and it is
     * per-activation for the same reason. A `<clinit>` prelude is created once per trigger, so two paths
     * through one initializer share it correctly.
     */
    internal val classInitContext: ClassInitContext? = null,
) {
    internal val callCompletion: CallCompletion? = returnSite?.let { CallCompletion() }
    /**
     * Frames deep this activation sits, **0-based**: 0 for the entry activation. The same origin as
     * [ExecutionNode.callDepth], which reads it, and as [CallStack.depth].
     *
     * ⚠️ **NOT the same origin as the old engine's `ExecutionNode.callDepth`**, and an earlier revision of
     * this KDoc claimed it was. The oracle's is **1-based** -- its top-level node is its own `isEntrypoint`
     * (`parent == null`), so it counts itself (`ExecutionNode.kt:67-78`), and `execution-engine.md:180`
     * records it as *"Entrypoint count along node ancestry; top-level depth is one"*. Comparing a 0-based
     * depth with the oracle's `> maxCallDepth` therefore permits 21 frames where the oracle permits 20.
     * [frameCount] is the value that shares the oracle's origin, and it is what the bound compares.
     */
    val depth: Int = caller?.depth?.plus(1) ?: 0

    /**
     * How many frames the call stack holds at this activation: 1 for the entry activation. **This is what
     * [Limits.maxCallDepth] bounds**, and it is the value with the same origin as the old engine's 1-based
     * `ExecutionNode.callDepth`, so `frameCount > maxCallDepth` truncates at the same frame the oracle does
     * -- which matters because Task 22's differential compares limit behavior.
     *
     * Both numbers exist rather than one because they answer different questions and each has a consumer
     * that needs its own origin: [depth] is the 0-based diagnostic value matching
     * [ExecutionNode.callDepth]'s long-standing contract, while this is a COUNT of frames and a count of
     * one thing is 1. Plan 10 step-over uses [caller] identity ancestry, not either number.
     */
    val frameCount: Int get() = depth + 1

    /**
     * Every node this activation ever dequeued, by address -- `CARRY-FORWARD.md` Plan 9 item 2. The
     * caller's address 0 and the callee's address 0 are different program points, and one pile per bare
     * address merges them: a callee's own `Outcome.Returned` node piled at a colliding address would vote
     * in the CALLER's `ExecutionGraph.terminatingConsensusOverExploredPaths`, inventing agreement about a
     * value the caller's method never returned.
     *
     * Unfiltered, exactly as [ExecutionGraph.nodePile] is: it holds nodes the scheduler stopped before
     * their op ran (the Task 6 piling decision), and `ExecutionSession.executedNodes` is what separates
     * those out.
     */
    internal val addressToNodes = LinkedHashMap<Int, MutableList<ExecutionNode>>()

    /**
     * The highest side-effect level any node in this activation declared, propagated up the [caller]
     * chain as it is raised.
     *
     * **This exists because filtering a graph's piles to one activation opens a hole in the other
     * direction.** `ExecutionGraph` describes ONE activation, so a callee's `STRONG` declaration is
     * invisible to `sideEffectLevel`/`methodSideEffectLevelOverExploredPaths` for the caller's addresses
     * -- and a caller whose callee perturbed state the engine does not model would report `NONE`, i.e.
     * "safe to delete", which is the answer-where-you-should-refuse direction spec §4.5's err-upward rule
     * exists to prevent. `ExecutionSession` reads this when the caller resumes and raises the INVOKE
     * node's own level to at least this, so the effect lands on the address that caused it.
     */
    internal var declaredEffect: SideEffectLevel = SideEffectLevel.NONE

    internal fun raiseDeclaredEffect(level: SideEffectLevel) {
        var activation: Activation? = this
        while (activation != null && level > activation.declaredEffect) {
            activation.declaredEffect = level
            activation = activation.caller
        }
    }

    /**
     * Raises the level of the invoke node of **this** activation's call, and of every call enclosing it, to
     * at least [level].
     *
     * **This is the half [declaredEffect] cannot reach, and leaving it out was a live regression.**
     * `declaredEffect` is transferred onto an invoke node when the callee **returns** -- so a callee path
     * that ends any OTHER way (an escaping throw, an address-visit or call-depth truncation, a host failure,
     * a `Transition.Abort`) transfers nothing, and because the abort is recorded on a **callee** node that
     * `ExecutionGraph` never sees, `sideEffectLevel`'s `UNSUPPORTED_OP -> STRONG` rule cannot fire either.
     * The caller's invoke address then read `NONE` -- *"no side effects, safe to delete"* -- through
     * `methodSideEffectLevelOverExploredPaths`, which is **ungated**, for a call this engine could not run
     * to completion. Measured as a regression against the pre-invocation baseline, where the invoke node
     * itself carried the abort and so answered `STRONG`.
     *
     * The whole ENCLOSING chain, not just the immediate caller: if A calls B and B's call of C truncates,
     * then neither B's call of C nor A's call of B completed, so both invoke nodes are calls whose effects
     * are unknown.
     *
     * Deliberately unconditional rather than short-circuiting on an already-high level: the walk is bounded
     * by [Limits.maxCallDepth] and a short-circuit would depend on every enclosing frame having been raised
     * by an earlier call, which is an invariant nothing enforces.
     */
    internal fun raiseCallSiteEffect(level: SideEffectLevel) {
        var activation: Activation? = this
        while (activation != null) {
            val invokeNode = activation.returnSite?.invokeNode ?: return
            if (level > invokeNode.sideEffectLevel) invokeNode.sideEffectLevel = level
            activation = activation.caller
        }
    }

    internal fun pileSnapshot(): Map<Int, List<ExecutionNode>> = addressToNodes.mapValues { it.value.toList() }

    override fun toString(): String = "Activation(${view.signature}, depth=$depth)"

    companion object {
        /** The run's own outermost activation: no caller, and its `return` ends a path of the run. */
        internal fun entry(view: MethodView): Activation = Activation(view, caller = null, returnSite = null)
    }
}

internal sealed interface ActivationExit {
    val frame: Frame

    class Returned(
        override val frame: Frame,
        val frameValue: FrameValue?,
    ) : ActivationExit {
        val value: Value? get() = frameValue?.value
    }

    class Threw(val path: ThrowPath) : ActivationExit {
        override val frame: Frame get() = path.frame
        val subject: ThrownSubject get() = path.subject
        val exception: Value get() = path.subject.value
    }

    class Aborted(
        override val frame: Frame,
        val reason: AbortReason,
    ) : ActivationExit
}

internal class CompletionBatch internal constructor(private var exits: List<ActivationExit>?) {
    val retainedFrames: Int get() = exits?.size ?: 0
    val retainedCollectionCapacity: Int get() = retainedFrames

    fun exitsSnapshot(): List<ActivationExit> = exits?.toList()
        ?: error("Cannot read a released activation completion batch")

    /** Releases every retained frame. A second release is a scheduler ownership error. */
    fun release() {
        check(exits != null) { "Activation completion batch was released more than once" }
        exits = null
    }
}

internal data class CallCompletionMetrics(
    val openPaths: Int,
    val retainedFrames: Int,
    val retainedCollectionCapacity: Int,
    val drained: Boolean,
)

/** One activation's path accounting and terminal votes. */
internal class CallCompletion {
    private sealed interface State {
        class Collecting(
            var openPaths: Int = 1,
            val exits: ExitBuffer = ExitBuffer(),
        ) : State

        data object Released : State
    }

    private class ExitBuffer {
        private var values = arrayOfNulls<ActivationExit>(0)
        private var size = 0

        val capacity: Int get() = values.size
        val retainedFrames: Int get() = size

        fun add(exit: ActivationExit) {
            if (size == values.size) {
                values = values.copyOf(if (values.isEmpty()) 4 else values.size * 2)
            }
            values[size++] = exit
        }

        fun take(): List<ActivationExit> {
            val result = List(size) { index -> requireNotNull(values[index]) }
            values.fill(null)
            values = arrayOfNulls(0)
            size = 0
            return result
        }
    }

    private var state: State = State.Collecting()

    /** Atomically replaces one active path and records every terminal path produced by that replacement. */
    fun replacePath(replacementOpenPaths: Int, exits: List<ActivationExit> = emptyList()) {
        require(replacementOpenPaths >= 0) { "Replacement open-path count must be non-negative" }
        val collecting = state as? State.Collecting
            ?: error("Cannot record a late activation vote after completion release")
        val updated = collecting.openPaths - 1 + replacementOpenPaths
        require(updated >= 0) { "Activation open-path count cannot become negative" }
        exits.forEach(collecting.exits::add)
        collecting.openPaths = updated
    }

    /** Returns and releases the completed batch. An incomplete activation returns null. */
    fun drainIfComplete(): CompletionBatch? {
        val collecting = state as? State.Collecting
            ?: error("Activation completion was drained more than once")
        if (collecting.openPaths != 0) return null
        val batch = CompletionBatch(collecting.exits.take())
        state = State.Released
        return batch
    }

    /** Returns the retained exits and releases the collector's storage. */
    fun cancelAndRelease(): List<ActivationExit> {
        val collecting = state as? State.Collecting
            ?: error("Activation completion was released more than once")
        state = State.Released
        return collecting.exits.take()
    }

    fun metrics(): CallCompletionMetrics = when (val current = state) {
        is State.Collecting -> CallCompletionMetrics(
            current.openPaths,
            current.exits.retainedFrames,
            current.exits.capacity,
            drained = false,
        )
        State.Released -> CallCompletionMetrics(0, 0, 0, drained = true)
    }
}

/**
 * Where a callee's `return` sends control: back to [invokeNode]'s method, at [resumeAddress].
 *
 * [resumeAddress] is computed **once**, in the caller's own method, from the caller's own node -- which is
 * the only shape plan design decision 5 permits, because `MethodView.nextAddress` delegates to
 * `instructionAt`'s `requireNotNull` and `planFor` runs outside the host-failure boundary. Whether the
 * address actually exists is then decided by `ExecutionSession.buildSuccessor`'s `hasAddress` check, which
 * reports a missing one as `MALFORMED_INPUT` rather than throwing (an `invoke` as a method's last
 * instruction is malformed but routine obfuscator output).
 *
 * [invokeNode] is retained rather than its frame copied, because the caller's registers must be read at
 * RESUME time from the node's post-op frame -- and because a debugger walking the call stack wants the
 * node, not a snapshot of it.
 */
internal class ReturnSite(
    val invokeNode: ExecutionNode,
    val resumeAddress: Int,
    val constructorReceiver: HeapRef? = null,
    val fallbackArguments: List<Value>? = null,
    val fallbackArgumentTypes: List<VirtualType>? = null,
    val fallbackReturnType: VirtualType? = null,
)

/**
 * The chain of [Activation]s a node sits in, innermost first -- `session.callStack` in spec §5.5's terms.
 * Plan 10 step-over walks [Activation.caller] identity ancestry directly; depth cannot distinguish sibling
 * or recursive activations.
 *
 * A snapshot: the list is built when it is asked for, so holding one cannot keep a run's state alive in a
 * way the run then mutates underneath it. Deliberately immutable and query-only for the same reason every
 * `ExecutionGraph` query is pure -- an observer must not perturb what it measures.
 */
class CallStack internal constructor(
    /** Innermost activation first, the run's entry activation last. Never empty. */
    val frames: List<Activation>,
) {
    /** The activation whose code is currently executing. */
    val top: Activation get() = frames.first()

    /**
     * Frames deep the top activation sits, **0-based**: 0 for the entry activation, so this equals
     * [ExecutionNode.callDepth] and [Activation.depth].
     *
     * ⚠️ **Not the oracle's `ExecutionNode.callDepth`, which is 1-based** -- see [Activation.depth]. The
     * number `Limits.maxCallDepth` is compared against is [Activation.frameCount] (== `frames.size`), which
     * is the one that shares the oracle's origin. An earlier revision of this KDoc asserted the equality and
     * was wrong by one frame.
     */
    val depth: Int get() = frames.size - 1

    override fun toString(): String = "CallStack(${frames.joinToString(" <- ") { it.view.signature }})"

    internal companion object {
        fun of(top: Activation): CallStack = CallStack(generateSequence(top) { it.caller }.toList())
    }
}
