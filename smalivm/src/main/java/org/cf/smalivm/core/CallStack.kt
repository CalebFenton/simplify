package org.cf.smalivm.core

/**
 * One activation of one method: the thing that exists once per call, for as long as that call is explored.
 *
 * Identity is the key: [Activation] defines no `equals`/`hashCode`, because two calls of one method from one
 * call site, which self-recursion makes routine, must never share a node pile.
 *
 * Per activation, not per path: a fork inside a method leaves both successors in the same activation, so
 * path-local state such as the class-init lifecycle lives in the [Frame]. A synchronous backend call creates no
 * activation; it executes inside the caller's invoke node.
 *
 * The [caller] edge is a call edge, separate from the control-flow edge [ExecutionNode.parent], so a caller's
 * handler never inherits a callee's locals.
 */
internal class Activation private constructor(
    /** Which method this activation is running. [ExecutionNode.view] reads it from here. */
    internal val view: MethodView,
    /** The activation that called this one; null for the run's own entry activation. */
    internal val caller: Activation?,
    /** Where the caller continues when this activation returns; null for the entry activation. */
    internal val returnSite: ReturnSite?,
    /**
     * The class this activation is the `<clinit>` of, when the scheduler synthesized it as a class-init prelude;
     * null for every ordinary activation. A return marks that class initialized and an escaping throw marks it
     * failed. It names the class the trigger requested, which is not always the `<clinit>`'s declaring class.
     */
    internal val classInitContext: ClassInitContext?,
    /** Where this activation's ended paths go: the run's outcomes for the entry activation, its caller's otherwise. */
    internal val sink: PathSink,
    /** The object this activation's `<init>` is constructing; null unless it runs a constructor on a tracked ref. */
    private val constructing: HeapRef?,
) {
    /** A call from [caller], which resumes at [returnSite]. */
    internal constructor(
        view: MethodView,
        caller: Activation,
        returnSite: ReturnSite,
        classInitContext: ClassInitContext? = null,
        constructing: HeapRef? = null,
    ) : this(view, caller, returnSite, classInitContext, CallCompletion(), constructing)

    /** Frames deep this activation sits, 0-based: 0 for the entry activation, as [ExecutionNode.callDepth]. */
    internal val depth: Int = caller?.depth?.plus(1) ?: 0

    /** How many frames the call stack holds at this activation, 1-based; [Limits.maxCallDepth] bounds this. */
    internal val frameCount: Int get() = depth + 1

    /**
     * Every node this activation ever dequeued, by address. A caller's address and a callee's address are
     * different program points, so a callee's terminal node never votes in its caller's consensus. Unfiltered,
     * like [ExecutionGraph.nodePile]: it holds nodes the scheduler stopped before their op ran.
     */
    internal val addressToNodes = LinkedHashMap<Int, MutableList<ExecutionNode>>()

    /**
     * The highest side-effect level any node in this activation declared, propagated up the [caller] chain. An
     * [ExecutionGraph] describes one activation, so without it a caller whose callee had an unmodelled effect would
     * report `NONE` for its own addresses. The caller's invoke node takes at least this level when the call drains.
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
     * The object a call of [target] on [receiver] from this activation finishes constructing, or null. An `<init>`
     * completes its receiver only when no constructor on the same object is still running here or in any caller:
     * that outer constructor can still throw, so only its own return completes the object. The receiver's runtime
     * type must be provably a subtype of [target]'s class.
     */
    internal fun initializedReceiver(
        target: MethodSignature,
        receiver: Value?,
        frame: Frame,
        hierarchy: TypeHierarchy,
    ): HeapRef? {
        if (!target.isConstructor) return null
        val ref = (receiver as? Value.Known)?.raw as? HeapRef ?: return null
        if (generateSequence(this) { it.caller }.any { it.constructing == ref }) return null
        val runtimeType = frame.heapObject(ref)?.type ?: return null
        val constructs = try {
            hierarchy.isSubtypeOf(runtimeType, target.definingClass)
        } catch (_: ClassLoadFailure) {
            false
        }
        return ref.takeIf { constructs }
    }

    /** The guest roots of every suspended caller frame, innermost first; a host call must not detach them. */
    internal fun suspendedCallerRoots(): List<Value> = buildList {
        var current: Activation? = this@Activation
        while (current != null) {
            val returnSite = current.returnSite ?: break
            addAll(returnSite.invokeNode.frame.guestRootValues())
            current = current.caller
        }
    }

    /**
     * Raises the invoke node of this activation's call, and of every call enclosing it, to at least [level].
     * [declaredEffect] reaches an invoke node only when the callee drains; a callee path that aborts leaves the
     * call, and every call enclosing it, incomplete with unknown effects, and the abort sits on a callee node no
     * caller graph sees. The walk is unconditional and bounded by [Limits.maxCallDepth].
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

    internal companion object {
        /** The run's outermost activation: no caller, and each of its ended paths is one of [outcomes]. */
        internal fun entry(view: MethodView, outcomes: RunOutcomes, constructing: HeapRef? = null): Activation =
            Activation(view, caller = null, returnSite = null, classInitContext = null, sink = outcomes, constructing)
    }
}

/** One ended path of an activation, with the exact state it ended in. */
internal sealed interface ActivationExit {
    val frame: Frame
    val outcome: Outcome

    class Returned(
        override val frame: Frame,
        val frameValue: FrameValue?,
    ) : ActivationExit {
        override val outcome: Outcome.Returned = Outcome.Returned(frameValue?.value)
    }

    class Threw(val path: ThrowPath) : ActivationExit {
        override val frame: Frame get() = path.frame
        val subject: ThrownSubject get() = path.subject
        val exception: Value get() = path.subject.value
        override val outcome: Outcome.Threw = Outcome.Threw(exception)
    }

    class Aborted(
        override val frame: Frame,
        override val outcome: Outcome.Aborted,
    ) : ActivationExit
}

internal class CompletionBatch internal constructor(private var exits: List<ActivationExit>?) {
    val retainedFrames: Int get() = exits?.size ?: 0

    fun exitsSnapshot(): List<ActivationExit> = exits?.toList()
        ?: error("Cannot read a released activation completion batch")

    /** Releases every retained frame. A second release is a scheduler ownership error. */
    fun release() {
        check(exits != null) { "Activation completion batch was released more than once" }
        exits = null
    }
}

/** Where one activation's ended paths are recorded. */
internal sealed interface PathSink {
    /**
     * Replaces one open path with [openPaths] successors and the [ended] paths it produced. Returns the
     * activation's exits once its last open path has ended, so the caller can resume; null otherwise.
     */
    fun replacePath(openPaths: Int, ended: List<ActivationExit>): CompletionBatch?
}

/**
 * The entry activation's sink: each ended path is one run outcome. Open paths are the session's frontier,
 * so this counts none.
 */
internal class RunOutcomes : PathSink {
    private val recorded = mutableListOf<Outcome>()
    private val truncatedBy = linkedSetOf<AbortReason>()

    /** One entry per ended path of the run. */
    val outcomes: List<Outcome> get() = recorded

    /** Every reason some path of the run, at any call depth, was cut short. */
    val abortReasons: Set<AbortReason> get() = truncatedBy

    override fun replacePath(openPaths: Int, ended: List<ActivationExit>): CompletionBatch? {
        ended.forEach { record(it.outcome) }
        return null
    }

    /** Records one ended path of the run. */
    fun record(outcome: Outcome) {
        recorded += outcome
        if (outcome is Outcome.Aborted) truncatedBy += outcome.reason
    }

    /** Records that a path was cut short without ending a run path, as a discharged callee abort is. */
    fun markTruncated(reason: AbortReason) {
        truncatedBy += reason
    }
}

/** A callee activation's sink: counts its open paths and collects its exits until the last one ends. */
internal class CallCompletion : PathSink {
    private sealed interface State {
        class Collecting(
            var openPaths: Int = 1,
            val exits: ArrayList<ActivationExit> = ArrayList(),
        ) : State

        data object Released : State
    }

    private var state: State = State.Collecting()

    /** True once [replacePath] returned a batch or [cancelAndRelease] ran. */
    val isDrained: Boolean get() = state == State.Released

    /** Atomically replaces one open path, records its exits, and releases the batch when none stays open. */
    override fun replacePath(openPaths: Int, ended: List<ActivationExit>): CompletionBatch? {
        require(openPaths >= 0) { "Replacement open-path count must be non-negative" }
        val collecting = state as? State.Collecting
            ?: error("Cannot record a late activation vote after completion release")
        // A collecting completion still has an open path to replace, so the count stays non-negative.
        collecting.openPaths += openPaths - 1
        collecting.exits.addAll(ended)
        if (collecting.openPaths != 0) return null
        state = State.Released
        return CompletionBatch(collecting.exits)
    }

    /** Returns the retained exits and releases the collector's storage. */
    fun cancelAndRelease(): List<ActivationExit> {
        val collecting = state as? State.Collecting
            ?: error("Activation completion was released more than once")
        state = State.Released
        return collecting.exits
    }
}

/**
 * Where a callee's `return` sends control: back to [invokeNode]'s method, at [resumeAddress].
 *
 * [resumeAddress] is computed once, in the caller's own method, from the caller's own node. Whether the address
 * exists is decided when the resumed successor is built, which reports a missing one as `MALFORMED_INPUT` (an
 * `invoke` as a method's last instruction is malformed but routine obfuscator output).
 *
 * [invokeNode] is retained rather than its frame copied, because the caller's registers must be read at resume
 * time from the node's post-op frame, and a debugger walking the call stack wants the node.
 * [constructorReceiver] is the object a normal return completes ([Activation.initializedReceiver]); the fallback
 * values describe the call for the opaque continuation an aborted callee path resumes through. [reflectiveReturn]
 * is set when the call is a `Method.invoke` of this activation's method: it converts each exit as reflection does.
 */
internal class ReturnSite(
    val invokeNode: ExecutionNode,
    val resumeAddress: Int,
    val constructorReceiver: HeapRef? = null,
    val fallbackArguments: List<Value>? = null,
    val fallbackArgumentTypes: List<VirtualType>? = null,
    val fallbackReturnType: VirtualType? = null,
    val reflectiveReturn: ReflectiveReturn? = null,
)

/**
 * The chain of [Activation]s a node sits in, innermost first. Step-over compares [Activation.caller] identity
 * ancestry, because depth cannot distinguish sibling or recursive activations. An immutable snapshot built when
 * asked for, so an observer never perturbs the run it measures.
 */
internal class CallStack internal constructor(
    /** Innermost activation first, the run's entry activation last. Never empty. */
    val frames: List<Activation>,
) {
    /** The activation whose code is currently executing. */
    val top: Activation get() = frames.first()

    /** Frames deep the top activation sits, 0-based, as [Activation.depth]. */
    val depth: Int get() = frames.size - 1

    override fun toString(): String = "CallStack(${frames.joinToString(" <- ") { it.view.signature.toString() }})"

    internal companion object {
        fun of(top: Activation): CallStack = CallStack(generateSequence(top) { it.caller }.toList())
    }
}
