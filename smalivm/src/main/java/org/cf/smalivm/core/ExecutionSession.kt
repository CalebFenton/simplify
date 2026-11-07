package org.cf.smalivm.core

import java.util.IdentityHashMap

/**
 * Exploration bounds. All values must be non-negative, and all are opt-out with `0`.
 * [maxExecutionTimeMillis] defaults to disabled
 * because a wall-clock bound makes results non-reproducible, and reproducibility is what the
 * differential harness depends on -- with the default the session never reads the clock at all.
 *
 * A limit is a *bound*, never a correctness mechanism. A run-level or class-initializer abort makes the run
 * [CompletionStatus.Partial]. An ordinary nested callee abort can become a conservative `STRONG` opaque caller
 * continuation, preserving the approved invocation boundary without trusting the callee result.
 * That claim is only true at every *moment* of a run because [CompletionStatus] has a third case:
 * [ExecutionSession.completion] answers [CompletionStatus.InProgress] while paths are still
 * scheduled, so an exploration that has not YET tripped a bound is not mistaken for a complete one
 * either -- which matters because the snapshot a debugger takes mid-run is precisely the one whose
 * bound has not fired yet. [maxAddressVisits] permits exactly that many visits and aborts on the next
 * one, matching the engine being replaced (`VirtualMachine.kt`'s
 * `newAddressVisitCount > maxAddressVisits`).
 */
data class Limits(
    val maxAddressVisits: Int = 500,
    val maxSteps: Int = 1_000_000,
    val maxExecutionTimeMillis: Long = 0,
    /**
     * How deep the [CallStack] may go before a callee's entry aborts with [AbortReason.CALL_DEPTH]
     * (plan resolved decision 10). Default **20**, matching the engine being replaced
     * (`VirtualMachine.kt`'s `DEFAULT_MAX_CALL_DEPTH`, corroborated by `execution-engine.md:171-180`).
     *
     * **This bounds [Activation.frameCount], which is 1-based, NOT [ExecutionNode.callDepth], which is
     * 0-based.** Getting that wrong is how an earlier version of this bound permitted 21 frames where the
     * oracle permits 20: the oracle's `ExecutionNode.callDepth` counts a `parent == null` node as its own
     * entrypoint, so its top-level frame is depth 1 (`execution-engine.md`: "top-level depth is one"),
     * while this engine's `depth` starts at 0. So at the default, **exactly 20 frames run and the 21st
     * aborts** -- a frame whose `depth` is 20 does NOT run. Do not restate either value as "the same as
     * the oracle's `callDepth`"; two KDocs and a task report each claimed that and each was wrong.
     *
     * **This is the bound that stops invocation taking the suite down.**
     * A nested call makes self-recursion expressible for the first time, and the old engine has no
     * recursion detection beyond depth either (`grep -nw` for `recursi`-prefixed words over
     * `VirtualMachine.kt` and the `opcode/` package finds nothing). Without it a recursive guest method
     * hangs or overflows the host stack. At top level or during class initialization the abort is `Partial`;
     * an ordinary caller can conservatively continue through the existing opaque fallback.
     */
    val maxCallDepth: Int = 20,
) {
    init {
        require(maxAddressVisits >= 0) { "maxAddressVisits must be non-negative: $maxAddressVisits" }
        require(maxSteps >= 0) { "maxSteps must be non-negative: $maxSteps" }
        require(maxExecutionTimeMillis >= 0) {
            "maxExecutionTimeMillis must be non-negative: $maxExecutionTimeMillis"
        }
        require(maxCallDepth >= 0) { "maxCallDepth must be non-negative: $maxCallDepth" }
    }
}

/**
 * One successor to schedule: an address in [activation]'s method, where its [Frame] comes from
 * ([frameOrigin]), what to do if it cannot be built ([onUnbuildable]), plus a [seed] applied to that
 * successor's OWN frame. The seed exists so a successor can carry something the parent's frame must not (a
 * handler's `caught` slot, a callee's bound parameters, a resumed caller's result slot), without the
 * parent's frame ever being the thing mutated.
 *
 * [onUnbuildable] and [frameOrigin] both have **no default**, on purpose: each is a soundness decision, and
 * a default would let a new successor site inherit a policy nobody chose for it.
 */
internal class SuccessorSpec(
    val address: Int,
    val activation: Activation,
    val onUnbuildable: OnUnbuildable,
    val frameOrigin: FrameOrigin,
    val seed: (Frame) -> Unit = {},
) {
    val view: MethodView get() = activation.view
}

/**
 * Where a successor's [Frame] comes from. A sealed type rather than a flag because picking the wrong origin
 * is silent: a copied frame where a callee needs a fresh entry frame, or the reverse, still runs.
 */
internal sealed interface FrameOrigin {
    /**
     * Ordinary control flow inside one activation: a **copy** of the parent node's post-op frame, never the
     * instance, so a speculative path cannot inherit or clobber a sibling's writes.
     */
    data object Continuation : FrameOrigin

    /**
     * A call: a fresh callee entry frame, parameters seeded by the [SuccessorSpec.seed], **sharing the
     * caller's heap id space** via `Frame.calleeEntryFrame`. Not derivable from the parent's frame -- the
     * callee has its own registers -- which is why this cannot be a copy.
     */
    data object CalleeEntry : FrameOrigin

    /** A call outcome partition already merged into a caller-shaped frame. */
    class CallerPartition(val frame: Frame) : FrameOrigin

    /** An opaque call's collapsed post-call frame, copied for each successor that resumes from it. */
    class OpaqueResult(val frame: Frame) : FrameOrigin
}

/**
 * What to do when THIS successor cannot be built (a malformed address, a decode failure).
 *
 * [ALL_OR_NOTHING] abandons the whole transition that declared it -- every successor it would have
 * scheduled, including ones already built, plus its ending: a malformed try-block table makes every
 * clause in that table suspect, so routing part of it would leave a Complete-looking run holding paths
 * no consumer can account for. [SKIP_ONE] keeps the buildable siblings and records the lost one as its
 * own aborted path: a malformed fall-through is no reason to discard a feasible exceptional branch, but
 * the loss must still make the run Partial or a truncated exploration reports as complete.
 *
 * **Per successor, and applied only within its own transition's [Committable] group.** Both halves were
 * arrived at by getting them wrong first, so both are worth keeping:
 *
 *  1. The policy used to be one value per `commit`, chosen by `transitions.size > 1`. That proxy
 *     silently weakened throw routing the moment an op returned a normal transition alongside a throw:
 *     Task 8's `div-int` inside a try block returns `[Continue, Throw]`, so its handler fork got
 *     [SKIP_ONE] -- one clause of a malformed table dropped, the normal branch kept -- while a bare
 *     `throw` in the SAME try block against the SAME table got [ALL_OR_NOTHING]. Two policies for one
 *     condition, chosen by an unrelated sibling, and
 *     `ThrowRoutingTest.oneMalformedHandlerAbortsTheWholeForkRatherThanCommittingHalfOfIt` could not
 *     see it because a bare `throw` still has `transitions.size == 1`.
 *  2. No *scalar* per-transition policy fixes that either, because it still has to be folded into one
 *     value per commit and neither fold is correct: "[ALL_OR_NOTHING] wins" discards a feasible
 *     exceptional branch when a fall-through is malformed, which is what [SKIP_ONE] exists to prevent,
 *     and "[SKIP_ONE] wins" is the bug above.
 *  3. Per-successor policies over a FLAT successor list are still not enough: [ALL_OR_NOTHING] then has
 *     to abort out of `commit` and takes with it successors belonging to OTHER transitions -- doing more
 *     than this enum claims, since a fall-through target is not a clause in the throw's table. `commit`
 *     therefore builds and commits per group; see its KDoc for the resulting combination rule.
 *
 * The three properties this buys, each pinned by a test in `CommitTest`: a throw's handler fork stays
 * all-or-nothing regardless of what sibling transitions exist; a malformed fall-through does not
 * discard a feasible exceptional branch; a malformed handler table does not discard a feasible normal
 * branch.
 */
internal enum class OnUnbuildable { ALL_OR_NOTHING, SKIP_ONE }

/**
 * One run's scheduler, and the only execution driver in the system (spec §5.1, §5.5): the
 * optimizer, the debugger (Plan 10) and the tests all advance execution through [step]. Everything
 * mutable about a run lives here -- frontier, visit counters, outcomes -- so runs are independent
 * and re-entrant, unlike the old engine where the queue and resume maps lived on the VM.
 *
 * Execution is a depth-first walk of the unrolled tree: the frontier is LIFO, and an op returning
 * several transitions forks -- as does a throw whose type is not known ([planThrow]) -- each
 * successor getting its **own** [Frame] copy. Because it is a tree walk and not a single concrete
 * path, [step] visits every explored path -- the honest model for a VM that executes with unknowns.
 *
 * Every one of those forks funnels through [commit], which is the single place a node's successors and
 * its ending are applied. Read its KDoc before changing anything about scheduling: the two-phase split
 * it enforces is what keeps a fork atomic and keeps a terminal slot write out of a sibling's frame.
 *
 * The walk is deterministic by construction: insertion-ordered maps and sets everywhere, a fixed
 * transition order (see [step]), and no clock read unless [Limits.maxExecutionTimeMillis] is
 * enabled. Two runs of the same input therefore produce identical node orders and identical results.
 */
class ExecutionSession internal constructor(
    val methodView: MethodView,
    val vmServices: VmServices,
    private val limits: Limits,
    entryFrame: Frame,
    private val resolver: ProgramSnapshotResolver = vmServices.classIndex.resolver(vmServices.classIndex.currentSnapshot),
    /**
     * How an address becomes an [Op]. Defaults to real decode, and is overridable ONLY so a test can
     * inject an op that returns transitions no real opcode in this build produces yet -- which is the
     * only way to pin [commit]'s ordering guarantees before Task 8's divide-by-zero fork exists, and
     * the only way to make an op throw a [VirtualMachineError] at all (see [hostFailureOutcome]).
     *
     * **Not a production extension point**, and that is enforced rather than merely documented:
     * reaching this parameter requires the `internal` five-argument [Companion.of], while the public
     * four-argument one always passes the real decoder. Backends arrive in Plan 11 and do not route
     * through here.
     */
    private val opFactory: (Int, MethodView, ProgramSnapshotResolver) -> Op = OpBuilder::build,
    private val invoker: Invoker = Invoker(vmServices.invocationServices.capture(resolver)),
    private val clockMillis: () -> Long = System::currentTimeMillis,
    /** The session's throwable model. Overridden only to exercise model-failure containment. */
    private val throwableHierarchy: ThrowableHierarchy = resolver.throwableHierarchy,
    private val routingInvariant: () -> Unit = {},
) {
    private val throwableType = VirtualType.of("Ljava/lang/Throwable;")
    private val frontierNodes = ArrayDeque<ExecutionNode>()
    private val pendingCallRoutings = ArrayDeque<PendingCallRouting>()

    /**
     * The run's outermost [Activation]. Every node the session ever schedules belongs to this one or to an
     * activation reachable from it through [Activation.caller], so this is the root of the call tree as well
     * as the thing [graph] describes.
     */
    private val entryActivation = Activation.entry(methodView)

    /**
     * Every [Activation] this run has created, entry first, in creation order.
     *
     * Needed because [graph] must union the piles of every activation of the ENTRY method -- a recursive call
     * makes a second one, and scoping reachability to the entry activation alone answered `wasReached(a) ==
     * false` on a `Complete` run for an address that had executed (see [ExecutionGraph]'s class KDoc). The
     * call tree's edges point from callee to CALLER ([Activation.caller]), so there is no way to walk down it;
     * a list is the cheapest thing that can be walked at all. One entry per call, the same order of magnitude
     * as the piles themselves.
     */
    private val activations = mutableListOf(entryActivation)

    // Node piles and per-address visit counts used to live HERE, each keyed by a bare `Int`
    // (`CARRY-FORWARD.md` Plan 9 items 1 and 2). Both now live on the `Activation`, because a bare address
    // is only unambiguous while one session walks one method: a caller's address 0 and a callee's address 0
    // are different program points, and merging them lets a callee's own `Outcome.Returned` node vote in the
    // caller's return consensus (item 2) while giving N loop-free callees one shared visit budget (item 1).
    // See `Activation.addressToNodes`, Frame's path-local address visits, and InvokerTest's collision tests.

    private val recordedOutcomes = mutableListOf<Outcome>()
    private val unattributedEndingFrames = mutableListOf<Frame>()
    private val abortReasons = linkedSetOf<AbortReason>()

    // TASK 6 PILING DECISION: a node whose op never ran (a bound stopped it right after dequeue,
    // before `execute()`) is still added to `addressToNodes` below -- see `step()` and `abortRun()`
    // -- so a consumer can find and attribute its abort by address instead of it vanishing as some
    // sibling's unqueried `children` entry. This set is what keeps that safe: it is the ONLY
    // authority for "did this node's op actually run", because `outcome`/`children` alone cannot
    // distinguish a terminal-after-running node from a limit-aborted-before-running one (both can
    // be non-null-outcome, childless). `ExecutionGraph` filters every FRAME-VALUE-bearing query
    // (`wasReached`, `consensusOverExploredPaths`, `registerValuesOverExploredPaths`) through this
    // set; `nodePile` does not, by
    // design, so the raw pile stays visible, and liveness (`getOp`, `registersRead`,
    // `registersAssigned`) does not either -- decode is static and never touches a `Frame`, so no
    // such filter is needed there. Identity-keyed: `ExecutionNode` defines no `equals`/`hashCode`,
    // which is exactly what membership here needs -- two nodes at the same address must never be
    // confused with each other.
    private val executedNodes = linkedSetOf<ExecutionNode>()

    private var steps = 0
    private var deadlineMillis = 0L
    private var opArmCopies = 0
    private var opStateEntriesVisited = 0
    private var reconciliationFootprintsResolved = 0
    private var preArmThrowableAllocations = 0
    private var committedThrowableAllocations = 0
    private var callPartitionMerges = 0
    private var sourcePartitionMerges = 0
    private var sourceThrowPartitions = 0
    private var sourceThrowExits = 0
    private var callerHandlerResolutions = 0
    private var callerHandlerClausesVisited = 0
    private var callerHandlerRelationRequests = 0
    private var callerHandlerRoutes = 0
    private val sourceDomainHashMetrics = ThrowableDomainHashMetricsCounter()
    private var routingWorkChecks = 0
    private var routingCommands = 0

    internal fun opExecutionMetrics(): OpExecutionMetrics = OpExecutionMetrics(
        opArmCopies,
        opStateEntriesVisited,
        reconciliationFootprintsResolved,
        preArmThrowableAllocations,
        committedThrowableAllocations,
    )

    internal fun callPropagationMetrics(): CallPropagationMetrics {
        val metrics = activations.mapNotNull { it.callCompletion?.metrics() }
        val pendingFrames = pendingCallRoutings.sumOf { it.batch.retainedFrames }
        val pendingCapacity = pendingCallRoutings.sumOf { it.batch.retainedCollectionCapacity }
        return CallPropagationMetrics(
            openPaths = metrics.sumOf { it.openPaths },
            retainedFrames = metrics.sumOf { it.retainedFrames } + pendingFrames,
            retainedCollectionCapacity = metrics.sumOf { it.retainedCollectionCapacity } + pendingCapacity,
            pendingRoutingCommands = pendingCallRoutings.size,
            routingWorkChecks = routingWorkChecks,
            routingCommands = routingCommands,
            schedulerWork = steps,
            partitionMerges = callPartitionMerges,
            sourcePartitionMerges = sourcePartitionMerges,
            sourceThrowPartitions = sourceThrowPartitions,
            sourceThrowExits = sourceThrowExits,
            handlerResolutions = callerHandlerResolutions,
            handlerClausesVisited = callerHandlerClausesVisited,
            handlerRelationRequests = callerHandlerRelationRequests,
            handlerRoutes = callerHandlerRoutes,
            sourceDomainHashReads = sourceDomainHashMetrics.hashReads,
            sourceDomainHashEntryVisits = sourceDomainHashMetrics.entryVisits,
        )
    }

    internal fun unattributedEndingFramesSnapshot(): List<Frame> = unattributedEndingFrames.toList()

    /**
     * The node [step] most recently *dequeued*. Usually the node it just executed -- but after a limit
     * abort it is the node the limit stopped, whose op never ran and whose [ExecutionNode.frame] is
     * therefore still pre-op. Plan 10's debugger reads this field, so the distinction matters there.
     */
    var currentNode: ExecutionNode? = null
        private set

    /**
     * The call stack as of now (spec §5.5), innermost activation first: the stack of the node [step] most
     * recently dequeued, or -- before anything has run -- the entry activation alone.
     *
     * A **per-path** notion read through the current node, not one mutable stack the session pushes and pops:
     * execution here is a depth-first walk of an unrolled tree, so two scheduled paths can sit at different
     * depths at once and a single stack could only describe one of them. Plan 10's step-over identifies
     * descendants by walking [Activation.caller] ancestry from the frontier node, not by comparing depth.
     */
    val callStack: CallStack get() = CallStack.of(currentNode?.activation ?: entryActivation)

    init {
        require(invoker.resolver.snapshot === resolver.snapshot) {
            "The session Invoker must use the session's captured program snapshot"
        }
        // entryFrame.copy(), never the caller's instance: a caller that seeds registers and then
        // starts two sessions (or reads its frame afterwards) must not have it mutated underneath.
        // Copying here rather than in `of` covers every construction path, including this internal
        // constructor.
        //
        // A failure to decode the entry point escapes construction rather than becoming an abort:
        // there is no explored path to abort yet, and a session that cannot decode its own first
        // instruction has nothing to run. Every failure from the first [step] onward is contained.
        frontierNodes.addLast(nodeAt(0, entryActivation, entryFrame.copy(), parent = null))
    }

    /** Nodes scheduled but not yet executed, next one last (LIFO). */
    val frontier: List<ExecutionNode> get() = frontierNodes.toList()

    /** The next LIFO node without allocating the public frontier snapshot. */
    internal val nextNode: ExecutionNode? get() = frontierNodes.lastOrNull()

    /** A stable snapshot with one [Outcome] per path that ended: returned, threw, or aborted. */
    val outcomes: List<Outcome> get() = recordedOutcomes.toList()

    val isFinished: Boolean get() = frontierNodes.isEmpty() && pendingCallRoutings.isEmpty()

    /**
     * Whether this run explored everything it set out to, **as of the moment it is asked** -- spec
     * §5.1's "a real, single completion status per run", three-valued so that the single status is
     * honest at every moment of the run and not only after it ends.
     *
     * Any run-level abort -- limit, unsupported opcode, host failure -- makes it [CompletionStatus.Partial],
     * which is what gates consumer trust in consensus (spec §6.4). Nested ordinary-callee aborts discharged
     * through opaque continuation are not run-level aborts. With nothing aborted, an
     * unfinished run is [CompletionStatus.InProgress] and only a finished one is
     * [CompletionStatus.Complete].
     *
     * **This reads the FRONTIER as well as the abort set, and that is the load-bearing part.**
     * [graph] is documented as safe to call mid-run because Plan 10's debugger is that consumer, so
     * while this answered from `abortReasons` alone, a mid-run snapshot reported `Complete` and
     * handed out a non-null [ExecutionGraph.trusted] -- authorizing exactly what the finished run
     * refuses. [CompletionStatus.InProgress]'s KDoc records the demonstrated case; do not
     * "simplify" the `!isFinished` branch away.
     *
     * The order is deliberate: an already non-empty abort set outranks a non-empty frontier, because
     * truncation is permanent and no amount of further stepping can un-truncate an abandoned path.
     * Answering `InProgress` there would be a status that IMPROVES to `Complete` later, which is the
     * same bug wearing the new case as a disguise (pinned by
     * `DebuggerTest.breakpointSnapshotsRemainUntrustedUntilCleanCompletionAndAfterAbort`).
     */
    fun completion(): CompletionStatus = when {
        abortReasons.isNotEmpty() -> CompletionStatus.Partial(abortReasons.toSet())
        !isFinished -> CompletionStatus.InProgress
        else -> CompletionStatus.Complete
    }

    /**
     * Advances execution by one node and returns it, or null when the run is finished.
     *
     * Every call into code this loop does not own -- an op's `execute`, a decode, and from Task 7 a
     * class-model lookup -- goes through a host-failure boundary, so an ordinary host `Throwable`
     * becomes `Aborted(HOST_FAILURE)` for that path only (spec §7) and never leaves `step()`. The
     * scheduler's own bookkeeping is deliberately *outside* that boundary: a bug in this file must
     * surface, not disguise itself as one more aborted path. A *guest* exception is not a host
     * failure either -- it is a [Value] arriving as [Transition.Throw], never a Kotlin throw.
     */
    fun step(): ExecutionNode? {
        if (limits.maxExecutionTimeMillis > 0 && deadlineMillis == 0L) {
            deadlineMillis = currentTimeMillis() + limits.maxExecutionTimeMillis
        }
        drainPendingCallRoutings()
        if (frontierNodes.isEmpty()) return null
        val node = frontierNodes.removeLast()
        currentNode = node

        // Piled unconditionally, BEFORE the limit check (the Task 6 piling decision): a node the
        // scheduler stops before running its op still belongs in this address's pile, so a
        // consumer can find it. `executedNodes` -- not this call -- is what later distinguishes
        // "piled" from "actually ran".
        addToPile(node)

        // A node stopped by a limit never ran its op, so it is not recorded as having executed.
        if (exceededLimits(node)) {
            drainPendingCallRoutings()
            return node
        }

        node.opExecutionStarted = true
        val allocationsBeforeExecution = node.frame.throwableAllocationCount()
        val execution = contained(node) { node.op.execute(node.frame) }
        preArmThrowableAllocations +=
            node.frame.throwableAllocationCount() - allocationsBeforeExecution
        // The effect the op declared, moved off the frame and onto the node HERE -- after `execute` and
        // BEFORE `applyAll` copies any successor frame. That order is the whole mechanism: an effect is a
        // statement about ONE address (spec §4.5), so it must not still be on the frame when children are
        // built. `Frame.copy` also declines to carry it, so this is belt and braces on purpose -- the
        // failure it prevents is silent (`sideEffectLevel(a)` answering STRONG for every address after the
        // first unprovable store, which no existing assertion would notice).
        //
        // UNCONDITIONAL, and BEFORE the null check, which is the whole of it: `contained` converts a host
        // `Throwable` out of `execute` into `Aborted(HOST_FAILURE)` and answers null, so an op that declared
        // STRONG and *then* threw used to have its declaration stranded on a frame nobody reads again --
        // `methodSideEffectLevelOverExploredPaths()` answered NONE for a run in which an op said STRONG,
        // which is the "answer where you should refuse" direction the whole mechanism exists to close. That
        // path is mitigated by the run being `Partial` (so the trust gate refuses) and is unreachable through
        // today's `APutOp`, but Plan 9's `invoke` will have a lot of code between declaring and returning.
        // Safe for the other two early exits above as well: a node whose op never ran never called
        // `recordSideEffect`, and its frame is a fresh `copy()` whose level is NONE by construction, so this
        // line reads NONE for them -- which is why it can be unconditional rather than guarded.
        node.sideEffectLevel = node.frame.takeSideEffectLevel()
        if (node.isCallEntry && node.view.isSynchronized) {
            node.sideEffectLevel = maxOf(node.sideEffectLevel, SideEffectLevel.WEAK)
        }
        // ...and the same declaration recorded on the ACTIVATION, and on every activation that called it.
        // `ExecutionGraph` describes one activation, so without this a caller whose CALLEE declared STRONG
        // would report NONE for every one of its own addresses -- "safe to delete" for a method whose call
        // perturbed state the engine does not model, which is the answer-where-you-should-refuse direction.
        // `planReturn` is what moves it onto the invoke node, so the effect lands on the address that caused
        // it rather than on the whole method (spec §4.5: an effect is a statement about ONE address).
        node.activation.raiseDeclaredEffect(node.sideEffectLevel)
        if (execution == null) {
            drainPendingCallRoutings()
            return node
        }
        executedNodes.add(node)
        // The WHOLE list, in one commit. It used to be `for (t in transitions.asReversed()) apply(t,
        // node)`, which committed each transition as it went and carried the three defects the Plan 3
        // deferred note catalogues -- a half-applied fork, a double-ended node, and a terminal slot
        // write leaking into a sibling's frame copy. All three are closed by [commit]'s two phases, and
        // the deterministic exploration order that loop's `asReversed()` used to provide now lives there too.
        // Public outcome order is deliberately not a semantic contract.
        applyAll(execution, node)
        drainPendingCallRoutings()
        return node
    }

    /**
     * Turns an op's whole transition list into ONE [commit]. This is what makes a fork atomic: the
     * old per-transition `apply` committed as it went, so two transitions could half-apply a fork,
     * double-end a node, or leak a terminal slot write into a sibling's frame copy.
     *
     * The transitions are handed over as one [Committable] PER TRANSITION rather than flattened into a
     * single successor list, because that grouping is what scopes an [OnUnbuildable.ALL_OR_NOTHING]
     * abort to the transition that declared it -- see [commit].
     *
     * A node still ends **at most one** path ([ExecutionNode.endWith] allows one), but the op that
     * fires it may end SEVERAL: [commit] stamps the first ending on the node and records any others
     * through [recordUnattributedEnding] -- allowed only when every requested ending is a guest throw
     * (Task 7's `aget`, ambiguous about both nullness and range). A host failure created while preparing
     * or planning one arm is separate from this check because it must not discard a feasible sibling.
     */
    private fun applyAll(execution: OpExecution, node: ExecutionNode) {
        val sourceFrame = node.frame
        if (execution.arms.size == 1) {
            val prepared = prepareArm(execution.arms.single(), sourceFrame, node)
            node.frame = prepared.frame
            val group = planFor(prepared, node)
            group.armFrame = prepared.frame
            commit(node, listOf(group))
            return
        }
        val prepared = mutableListOf<PreparedOpArm>()
        for (arm in execution.arms) {
            prepared += prepareArm(arm, sourceFrame, node)
        }
        val groups = ArrayList<Committable>(prepared.size)
        val enders = ArrayList<OpArm>(1)
        for (preparedArm in prepared) {
            node.frame = preparedArm.frame
            val arm = preparedArm.arm
            val part = planFor(preparedArm, node)
            part.armFrame = preparedArm.frame
            groups.add(part)
            if (part.ending == null || part.ending is Ending.ArmFailure) continue
            // ONE op may end SEVERAL paths, but only when every one of them is a `Transition.Throw`: an
            // `aget` that can neither prove its array non-null nor its index in range raises two different
            // exceptions, both of which escape a method with no try block, and both of which are the
            // same op's ambiguity about the same read. A node stamps one of them (ExecutionNode.endWith
            // is once-per-node by design) and `commit` records the rest as endings with no node --
            // exactly the shape `recordLostPath` already uses for a fork's unbuildable successor.
            //
            // Any OTHER mix stays loud, and Plan 9's is the one that matters: a Return beside a Throw is
            // not one op's ambiguity, it is two outcomes of a CALLEE, and modelling it as two endings of
            // the invoke node would lose which is which. Two successors of a synthetic fork is still the
            // likely answer there (plan resolved decision 3 confirms it, and keeps this check loud).
            //
            // `enders.isEmpty()` is what makes this a MIXING check rather than a Throw-only one: a
            // solo ending -- a plain `return`, a lone abort -- has nothing to mix with yet, so it is
            // never constrained by this require no matter its own kind. The constraint bites only once
            // a SECOND ending shows up for the same op.
            //
            // Checked over op arms rather than resulting outcomes. What the op requested is the thing this
            // rule constrains; activation routing must not change that classification.
            require(enders.isEmpty() || (arm is OpArm.Throw && enders.all { it is OpArm.Throw })) {
                "Mixed path-ending transitions at ${node.address} in ${node.view.signature}: " +
                    "$enders and $arm. Several endings from one op are supported only when all of " +
                    "them are guest throws (one op, several exceptions). Plan 9's unknown-callee invoke " +
                    "is the first op that wants [Return, Throw]; it owes an explicit decision about how " +
                    "they are modeled (most likely two successors of a synthetic fork, not two endings)."
            }
            enders.add(arm)
        }
        val ready = prepared.filterIsInstance<PreparedOpArm.Ready>()
        // A failed preparation has its own exact terminal frame. It cannot contribute a trustworthy
        // footprint or state to the source summary, and including its invalid footprint can fail again.
        when (ready.size) {
            0 -> node.frame = sourceFrame
            1 -> node.frame = ready.single().frame
            else -> {
                reconciliationFootprintsResolved++
                val footprint = sourceFrame.resolveFootprint(
                    ready.fold(OpUpdateFootprint.EMPTY) { combined, preparedArm ->
                        combined.combine(preparedArm.arm.reconciliationFootprint)
                    },
                )
                val reconciliation = sourceFrame.reconcileOpArms(
                    ready.map { it.frame },
                    footprint,
                    resolver.hierarchy,
                )
                node.frame = reconciliation.frame
                opStateEntriesVisited += reconciliation.cellsVisited
            }
        }
        // The mixed-ending check above is about what the OP asked for, so it runs over the planned
        // transitions here and not over whatever survives building. A group abandoned by ALL_OR_NOTHING
        // drops its own ending too (see [commit]), which can only reduce the count.
        commit(node, groups)
    }

    private fun prepareArm(arm: OpArm, sourceFrame: Frame, node: ExecutionNode): PreparedOpArm {
        val armFrame = sourceFrame.copyForOpArm()
        opArmCopies++
        return try {
            val applied = arm.update.applyTo(armFrame)
            applied.result?.let(armFrame::setResult)
            if (arm is OpArm.MoveFromSlot) {
                when (arm.source) {
                    SpecialSlotSource.RESULT -> armFrame.assignFrameValue(
                        arm.register,
                        armFrame.takeResultFrameValue() ?: FrameValue.plain(Value.Top),
                    )
                    SpecialSlotSource.CAUGHT -> armFrame.assignFrameValue(
                        arm.register,
                        armFrame.takeCaughtFrameValue() ?: FrameValue.plain(Value.Top),
                    )
                }
            }
            if (applied.effect > node.sideEffectLevel) node.sideEffectLevel = applied.effect
            node.activation.raiseDeclaredEffect(applied.effect)
            val path = if (arm is OpArm.Throw) normalizeThrow(arm.evidence, armFrame) else null
            PreparedOpArm.Ready(arm, armFrame, path)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            PreparedOpArm.Aborted(arm, armFrame, hostFailureOutcome(node, failure))
        }
    }

    private sealed interface PreparedOpArm {
        val arm: OpArm
        val frame: Frame

        data class Ready(
            override val arm: OpArm,
            override val frame: Frame,
            val throwPath: ThrowPath?,
        ) : PreparedOpArm

        data class Aborted(
            override val arm: OpArm,
            override val frame: Frame,
            val outcome: Outcome.Aborted,
        ) : PreparedOpArm
    }

    /** What one transition contributes to its node's commit. */
    private class Committable(val successors: List<SuccessorSpec>, val ending: Ending?) {
        var armFrame: Frame? = null
    }

    /**
     * A node's terminal [outcome], plus **whose** path it ends -- and that second half is a sealed case
     * rather than a boolean because the two are recorded differently and confusing them is silent.
     *
     * [ExecutionSession.outcomes] is one `Outcome` per path of the RUN (spec §4.4). A node's
     * [ExecutionNode.outcome] is one per path of its own ACTIVATION. Those coincide for everything except a
     * callee's `return`, where the activation is over and the run's path carries on in the caller -- so that
     * one stamps the node and adds nothing to the run's list.
     */
    private sealed interface Ending {
        val outcome: Outcome
        val frame: Frame
        val returnValue: FrameValue? get() = null
        val activationExit: ActivationExit? get() = null

        /**
         * A class whose `<clinit>` this ending FAILED, marked on the ending node's frame -- or null, which is
         * every ending but one.
         *
         * **Carried on the ending rather than written where it is decided, and the deferral is the point.**
         * [escapeEnding] is reached during PLANNING, and `commit` phase 1 copies every successor's frame from
         * the node's post-op frame afterwards -- so marking there would put `Failed` into the frame of each
         * HANDLER successor of the same ambiguous throw, i.e. onto paths on which the `<clinit>` catches its
         * own exception and goes on to return normally. That is a wrong answer in the worst direction: those
         * paths would then skip the rest of initialization. Phase 2 runs after every frame is copied, which is
         * the ordering the `thrown` slot write already relies on for the identical reason.
         */
        /** The ordinary case: a path of the run ended here, so [ExecutionSession.outcomes] gains an entry. */
        class RunPath(
            override val outcome: Outcome,
            override val frame: Frame,
            override val returnValue: FrameValue? = null,
        ) : Ending

        /** A host failure confined to one op arm rather than the other arms of the same execution. */
        class ArmFailure(
            override val outcome: Outcome.Aborted,
            override val frame: Frame,
        ) : Ending

        /**
         * A callee's terminal path. The node carries its [Outcome] while the run continues in the caller.
         * [activationExit] becomes a completion vote only when commit reaches phase 2.
         */
        class ActivationOnly(
            override val outcome: Outcome,
            override val frame: Frame,
            override val returnValue: FrameValue? = null,
            override val activationExit: ActivationExit,
        ) : Ending
    }

    /**
     * Plans ONE transition, including each successor's own [OnUnbuildable] policy -- which is where
     * that policy is chosen. Ordinary control flow is [OnUnbuildable.SKIP_ONE]: a malformed jump or
     * fall-through target is no reason to discard a feasible sibling branch. Throw routing states
     * [OnUnbuildable.ALL_OR_NOTHING] for itself in [planThrow], for the table-wide reason recorded
     * there.
     *
     * Preparation and planning failures become an ending for this arm, so sibling arms still commit.
     */
    private fun planFor(prepared: PreparedOpArm, node: ExecutionNode): Committable = when (prepared) {
        is PreparedOpArm.Aborted -> Committable(emptyList(), Ending.ArmFailure(prepared.outcome, prepared.frame))
        is PreparedOpArm.Ready -> planFor(prepared, node)
    }

    private fun planFor(prepared: PreparedOpArm.Ready, node: ExecutionNode): Committable = when (val arm = prepared.arm) {
        is OpArm.Continue -> Committable(
            listOf(
                SuccessorSpec(
                    node.view.nextAddress(node.address), node.activation,
                    OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                ),
            ),
            null,
        ).also { node.normalContinuationObserved = true }
        // The same fall-through, plus a register write that belongs to THIS arm alone. It goes through
        // [SuccessorSpec.seed] -- the seam `commit`'s phase-2 comment already names as the way a successor
        // carries state its siblings must not -- so it lands on this child's own frame copy and never on the
        // forking node's. [Transition.ContinueAssigning]'s KDoc carries the argument and the defect it
        // removes; `OnUnbuildable.SKIP_ONE` is unchanged, because a malformed fall-through target is still no
        // reason to discard a feasible exceptional sibling.
        is OpArm.MoveFromSlot -> Committable(
            listOf(
                SuccessorSpec(
                    node.view.nextAddress(node.address), node.activation,
                    OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                ),
            ),
            null,
        ).also { node.normalContinuationObserved = true }
        is OpArm.Jump -> Committable(
            listOf(
                SuccessorSpec(arm.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation),
            ),
            null,
        ).also { node.normalContinuationObserved = true }
        is OpArm.Return -> planReturn(node, arm.frameValue)
        is OpArm.Throw -> planThrow(
            node,
            requireNotNull(prepared.throwPath),
            throwAddress = when (arm.lookupPc) {
                ExceptionPc.AT_INSTRUCTION -> node.address
                ExceptionPc.AFTER_INSTRUCTION -> node.view.nextAddress(node.address)
            },
        ).also { node.exceptionalContinuationObserved = true }
        is OpArm.Abort -> Committable(emptyList(), Ending.RunPath(Outcome.Aborted(arm.reason), node.frame)).also {
            node.exceptionalContinuationObserved = true
        }
        is OpArm.Invoke -> planInvoke(node, arm)
        is OpArm.InitClass -> planInitClass(node, arm.type)
        is OpArm.ProjectStatic -> planStaticProjection(node, arm)
    }

    /** Authorizes and applies one framework static read without exposing scheduler state to the service. */
    private fun planStaticProjection(
        node: ExecutionNode,
        transition: OpArm.ProjectStatic,
    ): Committable = planContained(node) {
        val field = staticFieldDescriptor(
            transition.definingClass,
            transition.fieldDescriptor,
            transition.fieldType,
        )
        val cell = node.frame.staticCell(transition.fieldDescriptor)
        require(cell?.provenance == StaticCellProvenance.SEEDED_DEFAULT && cell.value is Value.Unknown) {
            "Static projection requested for a cell that is not an unresolved prepared seed: $field ($cell)"
        }
        if (!resolver.isFrameworkClass(transition.definingClass)) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.fieldType))
        }
        val initializationChain = try {
            resolver.hostInitializationChain(transition.definingClass)
        } catch (_: ClassLoadFailure) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.fieldType))
        }
        val decision = vmServices.reflectionPolicy.decideStaticRead(field, initializationChain)
        raiseSynchronousEffect(node, SideEffectLevel.STRONG)
        if (decision is ReflectionDecision.Denied) {
            node.invocationDiagnostic = "host static policy denied $field by ${decision.rule}"
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.fieldType))
        }
        when (val result = vmServices.hostStaticService.read(HostStaticRequest(field))) {
            is HostStaticResult.Returned -> staticProjectionContinuation(node, transition, result.value)
            is HostStaticResult.Unsupported -> {
                node.invocationDiagnostic = result.diagnostic
                staticProjectionContinuation(node, transition, Value.Unknown(transition.fieldType))
            }
            is HostStaticResult.TargetThrew ->
                planThrow(
                    node,
                    normalizeThrow(
                        result.throwable,
                        node.frame,
                    ),
                )
            is HostStaticResult.InfrastructureFailure -> {
                if (result.cause is VirtualMachineError) throw result.cause
                hostFailureArm(node, result.cause)
            }
            is HostStaticResult.Fatal -> throw result.cause
        }
    }

    private fun staticProjectionContinuation(
        node: ExecutionNode,
        transition: OpArm.ProjectStatic,
        hostValue: HostValue,
    ): Committable = Committable(
        listOf(
            SuccessorSpec(
                node.view.nextAddress(node.address),
                node.activation,
                OnUnbuildable.SKIP_ONE,
                FrameOrigin.Continuation,
            ) { frame ->
                val value = importHostStaticValue(frame, hostValue)
                frame.writeHostProjection(transition.fieldDescriptor, value)
                frame.assign(transition.register, value)
            },
        ),
        null,
    )

    private fun staticProjectionContinuation(
        node: ExecutionNode,
        transition: OpArm.ProjectStatic,
        value: Value,
    ): Committable = Committable(
        listOf(
            SuccessorSpec(
                node.view.nextAddress(node.address),
                node.activation,
                OnUnbuildable.SKIP_ONE,
                FrameOrigin.Continuation,
            ) { frame ->
                frame.writeHostProjection(transition.fieldDescriptor, value)
                frame.assign(transition.register, value)
            },
        ),
        null,
    )

    /**
     * Plans the class-init an op asked for: *"initialize this class, then run me again"*.
     *
     * **The op re-executes at its OWN address**, which is the old engine's `execute`/`resume` split without
     * the split (`opcode/SGetOp.kt:23-35` returns `staticInitClass(...)` and the VM later calls
     * `op.resume(node)` on the same node). Re-running instead of resuming costs one extra node in that
     * address's pile and buys an op that is one function rather than two -- and the extra node exists in the
     * nested-`<clinit>` case regardless, because the trigger cannot be the same node as the one the callee
     * returns to.
     *
     * ⚠️ **TASKS 17-19, READ THIS.** That extra node is in `executedNodes`, so it VOTES in
     * `ExecutionGraph.consensusOverExploredPaths(address, register)` for the trigger's own address -- and on
     * the first pass the op assigned nothing, so it contributes `Value.Top` and collapses the per-address
     * consensus for a register the second pass assigns perfectly. Harmless for `terminatingConsensus` (which
     * reads returning nodes, and is what `CoreTester.test` asserts through) and for the class-init tests here;
     * it is `sget`'s per-address assertions that will meet it. The fix, if one is wanted, is a DECODE-time
     * one: `OpBuilder` knows the class set, so an op whose whole superclass chain has no guest `<clinit>` and
     * no framework member need never emit this transition at all.
     *
     * Inside [planContained] because [ClassInitPlanner] walks the superclass chain, and
     * [ClassIndex.superclassOf] raises [ClassLoadFailure] for a class whose ancestors are not in the loaded
     * set -- routine obfuscated input, which must abort the path and not the run. `planFor` itself runs
     * OUTSIDE the boundary (plan design decision 5), so the wrapper has to be here.
     */
    private fun planInitClass(node: ExecutionNode, type: VirtualType): Committable =
        planContained(node) {
            require(node.frame.classInitAction(type) == ClassInitAction.StartInitialization) {
                "InitClass requires StartInitialization for ${type.descriptor}"
            }
            val plan = invoker.planClassInit(node.frame, type)
            // Loud rather than tolerated, and NOT defensive: the lenient handling of a no-op plan is an
            // infinite loop -- the trigger re-executes at its own address, asks again, and gets the same
            // answer. An op asks for a ClassInitAction before emitting this transition, and
            // that predicate is false for exactly the three states a plan can find nothing to do in, so this
            // is unreachable through a correct op. It is inside the host-failure boundary, so an incorrect
            // one aborts one path with the throwable on the node instead of hanging the suite.
            require(!plan.isNoOp) {
                "The op at ${node.address} in ${node.view.signature} asked to initialize ${type.descriptor}, " +
                    "which needs nothing done (${node.frame.initState(type)}). An op must consult " +
                    "StartInitialization before returning Transition.InitClass, or it will re-execute forever."
            }
            plan.classInitPreludeOrRetry(node)
        }

    private fun planClassInitTrigger(
        node: ExecutionNode,
        type: VirtualType,
        retryWhenSettled: Boolean = false,
    ): Committable? = when (val action = node.frame.classInitAction(type)) {
        ClassInitAction.Proceed -> null
        is ClassInitAction.ThrowFailed -> planThrow(
            node,
            normalizeThrow(
                ThrowEvidence.Fresh(
                    ThrowableRecipe.of(
                        VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
                        type.descriptor,
                        action.failure,
                    ),
                ),
                node.frame,
            ),
        )
        ClassInitAction.StartInitialization -> {
            val plan = invoker.planClassInit(node.frame, type)
            require(!plan.isNoOp) { "Class-init request for ${type.descriptor} produced no work" }
            when {
                plan.dependencyFailure != null -> plan.classInitPreludeOrRetry(node)
                plan.context != null -> plan.initializerPrelude(node)
                retryWhenSettled -> plan.classInitPreludeOrRetry(node)
                else -> {
                    declareSkippedInitializerEffect(node, plan)
                    node.frame.applyClassInit(plan)
                    null
                }
            }
        }
    }

    /** Schedules visible initializer work, or applies settled marks before retrying the trigger. */
    private fun ClassInitPlan.classInitPreludeOrRetry(node: ExecutionNode): Committable {
        dependencyFailure?.let { return dependencyFailure(node, this, it) }
        return initializerPrelude(node)
            ?: run {
                declareSkippedInitializerEffect(node, this)
                Committable(
                    listOf(
                        SuccessorSpec(
                            node.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                        ) { frame -> frame.applyClassInit(this) },
                    ),
                    null,
                )
            }
    }

    /**
     * The nested `<clinit>` activation [plan] calls for, or null when it calls for none.
     *
     * **A real nested call through the same seam** (design decision 1, spec §11.3): the same [Activation], the
     * same [CallStack], the same [FrameOrigin.CalleeEntry] frame (so the same heap id space), the same
     * [Limits.maxCallDepth] bound, and the same single [step] loop -- so Plan 10's debugger single-steps into a
     * `<clinit>` exactly as it does into any other callee, and a `<clinit>`'s own throw consults the
     * `<clinit>`'s own try blocks.
     *
     * **[ReturnSite]'s resume address is the trigger's OWN address, not the one after it**, which is the whole
     * mechanism: initialization is a *prelude* to the triggering instruction rather than a step past it. So a
     * hierarchy is initialized top-down by the trigger simply asking again (see [ClassInitPlan]), and an
     * `invoke` whose callee's class needed initializing runs its callee on the second pass. Computed from the
     * caller's own node in the caller's own method, which design decision 5 requires -- and unlike
     * `nextAddress` it cannot fail, because this address is the one currently executing.
     */
    private fun ClassInitPlan.initializerPrelude(node: ExecutionNode): Committable? {
        val context = context ?: return null
        val initializer = context.activeInitializer
        declareSkippedInitializerEffect(node, this)
        val activation = Activation(
            initializer.view,
            node.activation,
            ReturnSite(node, node.address),
            classInitContext = context,
        )
        activations.add(activation)
        return Committable(
            listOf(
                SuccessorSpec(
                    initializer.entryAddress, activation, OnUnbuildable.ALL_OR_NOTHING, FrameOrigin.CalleeEntry,
                ) { frame -> frame.applyClassInit(this) },
            ),
            null,
        )
    }

    private fun dependencyFailure(
        node: ExecutionNode,
        plan: ClassInitPlan,
        failure: ClassInitDependencyFailure,
    ): Committable {
        node.frame.applyClassInit(plan)
        val path = normalizeThrow(
            ThrowEvidence.Fresh(
                ThrowableRecipe.of(
                    VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
                    failure.failedAncestor.descriptor,
                    failure.storedFailure,
                ),
            ),
            node.frame,
        )
        node.frame.failClassInitDependency(failure, path.subject, throwableHierarchy)
        return planThrow(node, path)
    }

    /**
     * Raises the triggering address to [SideEffectLevel.STRONG] when [plan] settled a class whose `<clinit>`
     * this build declined to run -- resolved decision 7's framework rule.
     *
     * Written on the NODE rather than through `Frame.recordSideEffect`, and that is forced rather than chosen:
     * [step] moves the frame's declaration onto the node as soon as `execute` has run, which is strictly
     * before `planFor` is reached, so a declaration made here would never be collected. The activation chain
     * is raised too, exactly as [step] does for an op's own declaration, so a CALLER whose callee triggered an
     * unmodelled initializer does not report `NONE` for its own addresses.
     */
    private fun declareSkippedInitializerEffect(node: ExecutionNode, plan: ClassInitPlan) {
        if (!plan.skippedAnInitializer) return
        if (SideEffectLevel.STRONG > node.sideEffectLevel) node.sideEffectLevel = SideEffectLevel.STRONG
        node.activation.raiseDeclaredEffect(SideEffectLevel.STRONG)
    }

    /**
     * Plans a call: [Invoker] prepares and selects it, then this session turns the result into successors.
     *
     * **Wrapped in [planContained], which is what plan design decision 5 requires here.** Two things inside
     * reach code this loop does not own and `planFor` runs OUTSIDE the host-failure boundary: reading the
     * argument registers (`Frame.read` range-checks and throws for a register the caller does not have,
     * which an obfuscated call site can name), and resolving the target against the snapshot, which raises
     * for input it cannot answer. Both must abort the path, not the run. The callee's
     * ENTRY ADDRESS is handled by the other half of the same decision: [buildSuccessor] checks
     * `MethodView.hasAddress` before decoding, so a bodyless or truncated callee is `MALFORMED_INPUT`.
     *
     * The callee runs as a nested [Activation] on **this** session's frontier -- no child session, which is
     * what `CARRY-FORWARD.md` Plan 9 item 4 predicted `ExecutionNode.view` already made possible, and what
     * keeps `step()` single-steppable into a callee (Plan 10) and the host stack out of the recursion.
     *
     * [OnUnbuildable.ALL_OR_NOTHING] for the callee's entry: a call is one indivisible routing decision with
     * exactly one successor, so the two policies are indistinguishable here (see [enqueue]) and this states
     * the stricter one rather than relying on that.
     */
    private fun planInvoke(node: ExecutionNode, transition: OpArm.Invoke): Committable =
        planContained(node) {
            val frameArguments = transition.argumentRegisters.map { node.frame.readFrameValue(it) }
            // Resolution receives atomic carriers. Accepted host/emulator and opaque execution are explicit
            // conservative-forgetting boundaries; a selected guest call transports the carriers unchanged.
            val arguments = frameArguments.map(FrameValue::value)
            val call = Invocation.fromFrameValues(transition.target, transition.kind, frameArguments)
            val receiverType = if (transition.kind == InvokeKind.STATIC) null else {
                val receiver = arguments.firstOrNull()
                val ref = (receiver as? Value.Known)?.raw as? HeapRef
                ref?.let { node.frame.heapObject(it)?.type }
            }
            val preparation = invoker.prepare(call, node.view.definingClass, receiverType)
            when (preparation) {
                is InvocationPreparation.ThrowResolution -> {
                    val message = when (preparation.exceptionType.descriptor) {
                        "Ljava/lang/NoClassDefFoundError;" -> transition.target.definingClass.descriptor
                        "Ljava/lang/NoSuchMethodError;" -> transition.target.descriptor
                        else -> error("Unsupported invoke resolution throwable: ${preparation.exceptionType}")
                    }
                    return@planContained planThrow(
                        node,
                        normalizeThrow(
                            ThrowEvidence.Fresh(
                                ThrowableRecipe.of(preparation.exceptionType, message),
                            ),
                            node.frame,
                        ),
                    )
                }
                is InvocationPreparation.Opaque -> {
                    val receiver = arguments.firstOrNull()
                    if (transition.kind != InvokeKind.STATIC && receiver?.readsAsNullReference == true) {
                        return@planContained planInvokeNullReceiver(node)
                    }
                    node.dispatchCandidates = preparation.candidates
                    node.invocationDiagnostic = preparation.diagnostic
                    node.sideEffectLevel = maxOf(node.sideEffectLevel, SideEffectLevel.STRONG)
                    node.activation.raiseDeclaredEffect(SideEffectLevel.STRONG)
                    node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
                    return@planContained opaqueContinuation(
                        node,
                        transition,
                        arguments,
                        transition.opaqueArgumentTypes(arguments.size),
                        receiverType,
                        preparation.returnType,
                    )
                }
                is InvocationPreparation.Ready -> Unit
            }
            preparation as InvocationPreparation.Ready
            if (invoker.receiverIsNull(preparation)) return@planContained planInvokeNullReceiver(node)
            if (preparation.lookup is MethodLookup.DefaultConflict) {
                val target = preparation.invocation.target
                return@planContained planThrow(
                    node,
                    normalizeThrow(
                        ThrowEvidence.Fresh(
                            ThrowableRecipe.of(
                                VirtualType.of("Ljava/lang/IncompatibleClassChangeError;"),
                                "Conflicting default method implementations ${target.definingClass.descriptor}->" +
                                    target.descriptor,
                            ),
                        ),
                        node.frame,
                    ),
                )
            }
            val initType = invoker.applicableClassInitTarget(preparation.invocation)
            if (initType != null) {
                planClassInitTrigger(node, initType)?.let { return@planContained it }
            }
            if (preparation.isSynchronized) {
                raiseSynchronousEffect(node, SideEffectLevel.WEAK)
            }
            when (
                val plan = invoker.plan(
                    preparation,
                    node.frame::isHostMaterializable,
                    node.frame::isTrackedReference,
                )
            ) {
                is InvocationPlan.RunGuest -> {
                    val guestPlan = plan
                    val receiver = preparation.invocation.receiver
                    // The resume address is computed HERE, from the CALLER's own node in the CALLER's own
                    // method -- the only `nextAddress` shape decision 5 permits -- and carried on the
                    // callee's activation until its `return` needs it.
                    val constructorReceiver = if (
                        transition.target.name == "<init>" && receiverType == plan.view.definingClass
                    ) {
                        (arguments.firstOrNull() as? Value.Known)?.raw as? HeapRef
                    } else null
                    val activation = Activation(
                        guestPlan.view,
                        node.activation,
                        ReturnSite(
                            node,
                            node.view.nextAddress(node.address),
                            constructorReceiver,
                            fallbackArguments = preparation.invocation.opaqueArguments(),
                            fallbackArgumentTypes = preparation.invocation.opaqueArgumentTypes(),
                            fallbackReturnType = preparation.invocation.returnType,
                        ),
                    )
                    activations.add(activation)
                    Committable(
                        listOf(
                            SuccessorSpec(
                                guestPlan.entryAddress,
                                activation,
                                OnUnbuildable.ALL_OR_NOTHING,
                                FrameOrigin.CalleeEntry,
                            ) { frame ->
                                guestPlan.parameterBindings.forEach {
                                    frame.assignFrameValue(it.register, it.frameValue)
                                }
                            },
                        ),
                        null,
                    )
                }
                is InvocationPlan.Execute -> {
                    planAccessibilityFork(node, plan.invocation)?.let { return@planContained it }
                    node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
                    executeSynchronous(node, plan)
                }
                is InvocationPlan.Opaque -> {
                    node.dispatchCandidates = plan.candidates
                    node.invocationDiagnostic = plan.diagnostic
                    node.sideEffectLevel = maxOf(node.sideEffectLevel, SideEffectLevel.STRONG)
                    node.activation.raiseDeclaredEffect(SideEffectLevel.STRONG)
                    node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
                    opaqueContinuation(
                        node,
                        transition,
                        preparation.invocation.opaqueArguments(),
                        preparation.invocation.opaqueArgumentTypes(),
                        receiverType,
                        plan.returnType,
                    )
                }
            }
        }

    /** Records an accepted backend's effect, unboxes this path, then applies its direct result. */
    private fun executeSynchronous(
        node: ExecutionNode,
        plan: InvocationPlan.Execute,
    ): Committable {
        raiseSynchronousEffect(node, plan.effect)
        val selected = try {
            plan.select(node.frame, resolver.classLoader, suspendedCallerRoots(node.activation))
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return hostFailureArm(node, failure)
        }
        val result = try {
            selected.execute()
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return failedSelectedCall(node, selected, failure)
        }
        if (result is SynchronousCallResult.TargetThrew && result.throwable.throwable is VirtualMachineError) {
            throw result.throwable.throwable
        }
        return when (result) {
            is SynchronousCallResult.Returned -> {
                val comparison = finalizeSelectedCall(node, selected)
                synchronousReturn(node, selected, plan, result, comparison)
            }
            is SynchronousCallResult.TargetThrew -> {
                val comparison = finalizeSelectedCall(node, selected)
                planThrow(
                    node,
                    normalizeThrow(
                        result.throwable,
                        node.frame,
                        selected,
                        comparison,
                    ),
                )
            }
            is SynchronousCallResult.NeedsClassInit -> {
                val comparison = finalizeSelectedCall(node, selected)
                if (comparison != HostGraphComparison.Equal) {
                    val failure = IllegalStateException(
                        "Synchronous backend requested class initialization after changing host state",
                    )
                    return hostFailureArm(node, failure)
                }
                requireNotNull(planClassInitTrigger(node, result.type, retryWhenSettled = true)) {
                    "Synchronous backend requested class initialization for a class that can proceed: ${result.type}"
                }
            }
            is SynchronousCallResult.InfrastructureFailure -> {
                if (result.cause is VirtualMachineError) throw result.cause
                failedSelectedCall(node, selected, result.cause)
            }
            is IntrinsicCallResult -> {
                finalizeSelectedCall(node, selected)
                synchronousIntrinsic(node, plan, result.operation)
            }
            is ConstructedHostObjectResult -> {
                synchronousConstructor(node, selected, plan, result.instance)
            }
        }
    }

    private fun raiseSynchronousEffect(node: ExecutionNode, effect: SideEffectLevel) {
        node.sideEffectLevel = maxOf(node.sideEffectLevel, effect)
        node.activation.raiseDeclaredEffect(effect)
    }

    private fun suspendedCallerRoots(activation: Activation): List<Value> = buildList {
        var current = activation
        while (true) {
            val returnSite = current.returnSite ?: break
            addAll(returnSite.invokeNode.frame.guestRootValuesSnapshot())
            current = current.caller ?: break
        }
    }

    private fun finalizeSelectedCall(
        node: ExecutionNode,
        selected: SelectedSynchronousCall,
    ): HostGraphComparison {
        val comparison = try {
            selected.finalize(node.frame)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            throw failure
        }
        when (comparison) {
            HostGraphComparison.Equal, HostGraphComparison.Different -> Unit
            HostGraphComparison.Unprovable -> raiseSynchronousEffect(node, SideEffectLevel.STRONG)
        }
        return comparison
    }

    private fun synchronousConstructor(
        node: ExecutionNode,
        selected: SelectedSynchronousCall,
        plan: InvocationPlan.Execute,
        instance: Any,
    ): Committable {
        val ref = try {
            requireNotNull(constructorReceiver(plan.invocation, node)) {
                "Reflected constructor has no tracked receiver"
            }
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return failedSelectedCall(node, selected, failure)
        }
        try {
            selected.installConstructedHostObject(node.frame, ref, instance)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            return failedSelectedCall(node, selected, failure)
        }
        finalizeSelectedCall(node, selected)
        return synchronousContinuation(node, value = null, constructorReceiver = null)
    }

    private fun failedSelectedCall(
        node: ExecutionNode,
        selected: SelectedSynchronousCall,
        failure: Throwable,
    ): Committable {
        try {
            finalizeSelectedCall(node, selected)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (finalizationFailure: Throwable) {
            if (finalizationFailure !== failure) failure.addSuppressed(finalizationFailure)
        }
        return hostFailureArm(node, failure)
    }

    private fun synchronousReturn(
        node: ExecutionNode,
        selected: SelectedSynchronousCall,
        plan: InvocationPlan.Execute,
        result: SynchronousCallResult.Returned,
        comparison: HostGraphComparison,
    ): Committable {
        val value = when (val returned = result.value) {
            HostReturn.Void -> null
            is HostReturn.Value -> importHostValue(node.frame, selected, returned.value, comparison)
        }
        val constructorReceiver = constructorReceiver(plan.invocation, node)
        return synchronousContinuation(node, value, constructorReceiver)
    }

    private fun synchronousIntrinsic(
        node: ExecutionNode,
        plan: InvocationPlan.Execute,
        operation: IntrinsicOperation,
    ): Committable = when (operation) {
        is IntrinsicOperation.AllocateTrackedField -> {
            val field = node.frame.allocateTrackedField(operation.metadata)
            synchronousContinuation(node, field, constructorReceiver = null)
        }
        IntrinsicOperation.CloneArrayReceiver -> {
            val ref = (plan.invocation.receiver as? Value.Known)?.raw as? HeapRef
                ?: error("Tracked-array clone has no known receiver ref")
            val source = node.frame.arrayAt(ref)
                ?: error("Tracked-array clone receiver $ref is not an array")
            val clone = node.frame.allocateArray(source.type, source.elements().toTypedArray())
            synchronousContinuation(node, clone, constructorReceiver = null)
        }
        is IntrinsicOperation.InitializeEnumReceiver -> {
            val ref = (plan.invocation.receiver as? Value.Known)?.raw as? HeapRef
                ?: error("Enum initialization has no known receiver ref")
            require(
                node.frame.writeField(
                    ref,
                    "Ljava/lang/Enum;->name:Ljava/lang/String;",
                    operation.name.toSchedulerValue(VirtualType.of("Ljava/lang/String;")),
                ),
            ) { "Enum initialization receiver $ref has no name field" }
            require(
                node.frame.writeField(
                    ref,
                    "Ljava/lang/Enum;->ordinal:I",
                    operation.ordinal.toSchedulerValue(VirtualType.of("I")),
                ),
            ) { "Enum initialization receiver $ref has no ordinal field" }
            synchronousContinuation(
                node,
                value = null,
                constructorReceiver = constructorReceiver(plan.invocation, node),
            )
        }
        is IntrinsicOperation.ReadVirtualField -> {
            val value = if (operation.field.storage == FieldStorage.INSTANCE) {
                val receiver = plan.invocation.arguments.singleOrNull()
                    ?: error("Virtual instance field read has no receiver argument")
                val ref = (receiver as? Value.Known)?.raw as? HeapRef
                    ?: error("Virtual field read has no known receiver ref")
                node.frame.getField(ref, operation.field.descriptor)
            } else {
                node.frame.getStaticField(operation.field.declaringType, operation.field.descriptor)
            }
            synchronousContinuation(node, value, constructorReceiver = null)
        }
        IntrinsicOperation.ReadThrowableCause -> {
            val receiver = plan.invocation.receiver
            val causeType = VirtualType.of("Ljava/lang/Throwable;")
            val value = when (receiver) {
                is Value.Known -> {
                    val ref = receiver.raw as? HeapRef
                    if (ref == null || node.frame.heapObject(ref) == null) Value.Unknown(causeType)
                    else node.frame.throwableCause(ref)
                }
                is Value.Unknown, Value.Top, null -> Value.Unknown(causeType)
            }
            synchronousContinuation(node, value, constructorReceiver = null)
        }
        is IntrinsicOperation.RouteFailedClassInit -> requireNotNull(
            planClassInitTrigger(node, operation.type, retryWhenSettled = false),
        ) { "Failed class ${operation.type.descriptor} produced no guest failure" }
        is IntrinsicOperation.SetTrackedFieldAccessible -> {
            val ref = (plan.invocation.receiver as? Value.Known)?.raw as? HeapRef
                ?: error("Field.setAccessible has no known tracked receiver ref")
            val current = node.frame.trackedFieldAt(ref)
                ?: error("Field.setAccessible receiver $ref is not a tracked field")
            require(current.metadata == operation.metadata) {
                "Field.setAccessible metadata changed between selection and application"
            }
            require(node.frame.setTrackedFieldAccessibility(ref, operation.accessibility)) {
                "Field.setAccessible could not update $ref"
            }
            synchronousContinuation(node, value = null, constructorReceiver = null)
        }
        is IntrinsicOperation.ReturnUnknown -> synchronousContinuation(
            node,
            Value.Unknown(operation.type),
            constructorReceiver = null,
        )
    }

    private fun EmulatorValue.toSchedulerValue(expectedType: VirtualType): Value = when (this) {
        is EmulatorValue.Known -> when {
            expectedType.descriptor == "Ljava/lang/String;" && (javaValue == null || javaValue is String) ->
                Value.Known(javaValue, expectedType)
            expectedType.descriptor == "I" && javaValue is Int -> Value.Known(javaValue, expectedType)
            else -> Value.Unknown(expectedType)
        }
        is TrackedFieldEmulatorValue, is EmulatorValue.TrackedReference, is EmulatorValue.Unknown ->
            Value.Unknown(expectedType)
    }

    private fun constructorReceiver(call: PreparedInvocation, node: ExecutionNode): HeapRef? {
        if (call.target.name != "<init>") return null
        val ref = (call.receiver as? Value.Known)?.raw as? HeapRef ?: return null
        val runtimeType = node.frame.heapObject(ref)?.type ?: return null
        if (!resolver.isSubtypeOf(runtimeType, call.target.definingClass)) return null
        val enclosingConstructor = node.view.signature.substringAfter("->").startsWith("<init>(")
        // The outer guest constructor owns final completion. Marking after its base call would make a later
        // throw from the outer constructor look like successful initialization.
        if (
            enclosingConstructor &&
            node.view.definingClass == runtimeType &&
            call.target.definingClass != runtimeType
        ) {
            return null
        }
        return ref
    }

    private fun synchronousContinuation(
        node: ExecutionNode,
        value: Value?,
        constructorReceiver: HeapRef?,
    ): Committable {
        return Committable(
            listOf(
                SuccessorSpec(
                    node.view.nextAddress(node.address),
                    node.activation,
                    OnUnbuildable.SKIP_ONE,
                    FrameOrigin.Continuation,
                ) { frame ->
                    constructorReceiver?.let(frame::markInitialized)
                    value?.let(frame::setResult)
                },
            ),
            null,
        ).also { node.normalContinuationObserved = true }
    }

    private fun importHostValue(
        frame: Frame,
        selected: SelectedSynchronousCall,
        value: HostValue,
        comparison: HostGraphComparison,
    ): Value.Known = when (value) {
        is HostValue.Primitive -> Value.Known(value.value, value.type)
        is HostValue.TypedNull -> Value.Known(null, value.type)
        is HostValue.Reference -> {
            val raw = value.value
            requireHostOwned(raw)
            if (raw is Throwable) {
                importHostThrowable(frame, HostThrowable(value.type, raw), selected, comparison)
            } else if (raw.isDirectHostImmutable()) {
                Value.Known(raw, value.type)
            } else {
                val sourceRef = retainedSourceRef(frame, selected, raw, comparison)
                when {
                    sourceRef != null -> Value.Known(sourceRef, value.type)
                    comparison == HostGraphComparison.Equal && selected.sourceNodeId(raw) != null -> {
                        val sourceNode = requireNotNull(selected.sourceNodeId(raw))
                        Value.Known(frame.importHostNode(sourceNode, value.type, frame.hostObjectAt(sourceNode)!!), value.type)
                    }
                    else -> {
                        val nodeId = retainedSourceNodeId(frame, selected, raw)
                        when {
                            nodeId != null -> Value.Known(frame.importHostNode(nodeId, value.type, raw), value.type)
                            raw.javaClass.isArray -> frame.admitReturnedHostArray(raw, value.type)
                            else -> frame.admitReturnedHostValue(raw, value.type)
                        }
                    }
                }
            }
        }
    }

    private fun importHostStaticValue(frame: Frame, value: HostValue): Value.Known = when (value) {
        is HostValue.Primitive -> Value.Known(value.value, value.type)
        is HostValue.TypedNull -> Value.Known(null, value.type)
        is HostValue.Reference -> {
            requireHostOwned(value.value)
            when {
                value.value is Throwable -> importHostThrowable(
                    frame,
                    HostThrowable(value.type, value.value),
                )
                value.value.isDirectHostImmutable() -> Value.Known(value.value, value.type)
                value.value.javaClass.isArray -> frame.admitReturnedHostArray(value.value, value.type)
                else -> frame.admitReturnedHostValue(value.value, value.type)
            }
        }
    }

    /** Imports one host throwable graph while preserving identity, aliases, and cycles. */
    private fun importHostThrowable(
        frame: Frame,
        throwable: HostThrowable,
        selected: SelectedSynchronousCall? = null,
        comparison: HostGraphComparison? = null,
    ): Value.Known {
        require((selected == null) == (comparison == null)) {
            "Selected-call throwable import requires both identity mapping and graph comparison"
        }
        val imported = IdentityHashMap<Throwable, HeapRef>()

        fun importOne(raw: Throwable, type: VirtualType): Value.Known {
            imported[raw]?.let { return Value.Known(it, type) }
            val ref = importHostThrowableRef(frame, raw, type, selected, comparison)
            imported[raw] = ref
            val cause = raw.cause?.let { nested ->
                importOne(nested, virtualTypeOfHostClass(nested.javaClass))
            } ?: Value.Known(null, throwableType)
            frame.setThrowableCause(ref, cause)
            return Value.Known(ref, type)
        }

        val runtimeType = virtualTypeOfHostClass(throwable.throwable.javaClass)
        return importOne(throwable.throwable, runtimeType)
    }

    private fun importHostThrowableRef(
        frame: Frame,
        raw: Throwable,
        type: VirtualType,
        selected: SelectedSynchronousCall?,
        comparison: HostGraphComparison?,
    ): HeapRef {
        if (selected != null && comparison != null) {
            retainedSourceRef(frame, selected, raw, comparison)?.let { return it }
            if (comparison == HostGraphComparison.Equal) {
                val sourceNode = selected.sourceNodeId(raw)
                if (sourceNode != null) {
                    val retained = requireNotNull(frame.hostObjectAt(sourceNode))
                    return frame.importHostNode(sourceNode, type, retained)
                }
            }
            retainedSourceNodeId(frame, selected, raw)?.let { nodeId ->
                return frame.importHostNode(nodeId, type, raw)
            }
        }
        frame.findHostObject(raw)?.let { return it }
        frame.hostNodeId(raw)?.let { nodeId ->
            return frame.importHostNode(nodeId, type, raw)
        }
        return frame.allocateThrowable(type, raw).raw as HeapRef
    }

    private fun retainedSourceNodeId(
        frame: Frame,
        selected: SelectedSynchronousCall,
        raw: Any,
    ): HostGraphNodeId? {
        val source = selected.sourceNodeId(raw)
        if (source != null && frame.hostObjectAt(source) === raw) return source
        return frame.hostNodeId(raw)
    }

    private fun retainedSourceRef(
        frame: Frame,
        selected: SelectedSynchronousCall,
        raw: Any,
        comparison: HostGraphComparison,
    ): HeapRef? {
        val sourceRef = selected.sourceHeapRef(raw) ?: return null
        val nodeId = when (val obj = frame.heapObject(sourceRef)) {
            is HeapObject.HostObject -> (obj.contents as? HostGraphContents.Known)?.nodeId
            is HeapObject.GuestThrowable -> (obj.contents as? HostGraphContents.Known)?.nodeId
            is HeapObject.TrackedArray -> (obj.mirror as? HostGraphContents.Known)?.nodeId
            is HeapObject.TrackedField, is HeapObject.TrackedObject, null -> null
        } ?: return null
        if (frame.refForHostNode(nodeId) != sourceRef) return null
        val currentRaw = frame.hostObjectAt(nodeId) ?: return null
        if (currentRaw === raw) return sourceRef
        val sourceNode = selected.sourceNodeId(raw)
        return if (comparison == HostGraphComparison.Equal && sourceNode == nodeId) sourceRef else null
    }

    private fun opaqueContinuation(
        node: ExecutionNode,
        transition: OpArm.Invoke,
        arguments: List<Value>,
        argumentTypes: List<VirtualType>,
        receiverType: VirtualType?,
        returnType: VirtualType,
    ): Committable {
        val receiver = arguments.firstOrNull()
        val constructorReceiver = if (
            transition.target.name == "<init>" && receiverType == transition.target.definingClass
        ) {
            (receiver as? Value.Known)?.raw as? HeapRef
        } else null
        return Committable(
            listOf(
                opaqueSuccessor(
                    node.frame,
                    node.view.nextAddress(node.address),
                    node.activation,
                    arguments,
                    argumentTypes,
                    constructorReceiver,
                    returnType,
                ),
            ),
            null,
        )
    }

    /**
     * Collapses what an opaque call can reach. The callee may toggle any reachable tracked field's
     * accessibility, so each becomes [FieldAccessibility.UNKNOWN] and [planAccessibilityFork] splits it only
     * where a backend reads it.
     */
    private fun opaqueSuccessor(
        source: Frame,
        address: Int,
        activation: Activation,
        arguments: List<Value>,
        argumentTypes: List<VirtualType>,
        constructorReceiver: HeapRef?,
        returnType: VirtualType,
    ): SuccessorSpec {
        val result = source.copy()
        for (ref in result.reachableTrackedFields(arguments)) {
            result.setTrackedFieldAccessibility(ref, FieldAccessibility.UNKNOWN)
        }
        result.collapseReachable(arguments, argumentTypes)
        constructorReceiver?.let(result::markInitialized)
        if (returnType.descriptor != "V") result.setResult(Value.Unknown(returnType))
        return SuccessorSpec(address, activation, OnUnbuildable.SKIP_ONE, FrameOrigin.OpaqueResult(result))
    }

    /**
     * Re-executes a call once per definite accessibility of the first [FieldAccessibility.UNKNOWN] tracked
     * field it passes, so a backend only ever reads a definite one. Null when the call passes none.
     */
    private fun planAccessibilityFork(node: ExecutionNode, invocation: PreparedInvocation): Committable? {
        val ref = (listOfNotNull(invocation.receiver) + invocation.arguments).firstNotNullOfOrNull { value ->
            ((value as? Value.Known)?.raw as? HeapRef)?.takeIf {
                node.frame.trackedFieldAt(it)?.accessibility == FieldAccessibility.UNKNOWN
            }
        } ?: return null
        return Committable(
            listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN).map { accessibility ->
                SuccessorSpec(
                    node.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                ) { frame -> frame.setTrackedFieldAccessibility(ref, accessibility) }
            },
            null,
        )
    }

    private fun planInvokeNullReceiver(node: ExecutionNode): Committable =
        planThrow(
            node,
            normalizeThrow(
                ThrowEvidence.Fresh(
                    ThrowableRecipe.of(
                        VirtualType.of("Ljava/lang/NullPointerException;"),
                        "invoke receiver is null",
                    ),
                ),
                node.frame,
            ),
        )

    /**
     * Plans a `return`: either a path of the run ends, or the **caller** resumes.
     *
     * Which one is decided by [Activation.returnSite], not by comparing methods: a self-recursive method's
     * innermost `return` and the entry activation's `return` are the same instruction in the same method, and
     * only the activation can tell them apart.
     *
     * Two things travel to the caller and the split is deliberate. The **value** goes into the resumed
     * frame's `result` slot, which is where the immediately following `move-result*` reads it from
     * ([Frame.takeResult]). Any other next instruction expires it. The callee's **declared
     * side effects** are raised onto the INVOKE node, so `ExecutionGraph.sideEffectLevel` reports them at the
     * address that caused them; see [Activation.declaredEffect] for the hole that closes.
     *
     * **The callee's returning node still carries [Outcome.Returned]** ([Ending.ActivationOnly]) even though
     * no path of the run ended. The activation is over, but its ordered exit token resumes the caller later.
     *
     * Compatible return state merges once after the activation drains. Ordered tokens then expand back to one
     * caller continuation each, so duplicate path results remain observable.
     */
    private fun planReturn(node: ExecutionNode, value: FrameValue?): Committable {
        if (node.activation.returnSite == null) {
            return Committable(emptyList(), Ending.RunPath(Outcome.Returned(value?.value), node.frame, returnValue = value))
        }
        return Committable(
            emptyList(),
            Ending.ActivationOnly(
                Outcome.Returned(value?.value),
                node.frame,
                returnValue = value,
                activationExit = ActivationExit.Returned(node.frame, value),
            ),
        )
    }

    /**
     * The ONE place a node's successors and ending are committed. Two phases, and the split is the
     * whole point:
     *
     *  1. **Build: mutates no frame and ends no path.** Every successor's frame is copied from [node]'s
     *     post-op state BEFORE any terminal slot write below. Without this ordering a `[Continue,
     *     Throw]` fork hands the normal-path child a frame already carrying `thrown` -- a path that did
     *     not throw looking like one that did, which is a wrong answer rather than a loud failure.
     *     (Stated that way rather than "mutates nothing", which would not be true: [buildSuccessor]
     *     records a decode throwable on [node] via [hostFailureOutcome], and a [VirtualMachineError]
     *     rethrows out of this phase. Neither touches a [Frame] and neither ends a path, which is the
     *     invariant the ordering above actually needs.)
     *  2. **Commit.** Children attached, frontier pushed, node ended AT MOST ONCE. `record` is
     *     reached from exactly one place per call, so a fork can no longer double-end a node. The op
     *     may still have asked for SEVERAL endings (every one a guest throw -- [applyAll] enforces
     *     that); only the first is stamped here, and the rest are recorded via
     *     [recordUnattributedEnding], which ends its path without a node of its own.
     *
     * Successors are pushed in REVERSE because the frontier is LIFO: the first spec is the first path
     * explored. Callers order their specs meaningfully (ascending handler address, normal before
     * exceptional).
     *
     * **The combination rule, stated because there is one.** Each spec carries its own
     * [SuccessorSpec.onUnbuildable], and the rule is that a policy applies **only within the
     * [Committable] that declared it**: an abandoned group contributes nothing -- no children, no
     * terminal, one lost path -- and every other group commits exactly as if it had been alone. Groups
     * do not interact.
     *
     * **And every lost path carries its OWN [AbortReason], never a reason this function chose.** The
     * losses used to be a `lost: Int`, so all three of the recording sites below had to name one
     * reason for every loss and named [AbortReason.HOST_FAILURE] -- which made an off-the-end jump,
     * routine in the obfuscated input this engine exists for, indistinguishable in aggregate from a bug
     * in this codebase. [buildSuccessor] now reports *why* it failed and this function only forwards
     * it. `OnUnbuildable` still decides how MUCH is abandoned; it never decides whose fault the loss
     * was.
     *
     * Building `groups` as one flat successor list is what makes that false, and it is the mistake to
     * not re-make: [OnUnbuildable.ALL_OR_NOTHING] then has to abort out of [commit] entirely, taking
     * with it any successor already built for an earlier [OnUnbuildable.SKIP_ONE] group. Concretely
     * `[Continue, Throw]` with a malformed handler table killed the fall-through child too -- a fully
     * resolved normal-execution path that never consults the try-block table at all. Not unsound (the
     * abort still makes the run Partial), but a precision loss against spec §2.2's "more forks never
     * fewer", in exactly the shape Task 8's `div-int` inside a try block has.
     */
    private fun commit(node: ExecutionNode, groups: List<Committable>) {
        // A node must go SOMEWHERE. Neither a successor nor an ending leaves it invisible to every
        // ExecutionGraph query -- a silently dropped path, the exact unsoundness this file guards.
        require(groups.any { it.successors.isNotEmpty() || it.ending != null }) {
            "Op at ${node.address} in ${node.view.signature} would be committed nowhere"
        }
        // And so must every individual transition, which is the old `route` invariant ("a throw must be
        // routed SOMEWHERE") restored now that groups exist to carry it: the whole-node check above
        // cannot see one empty group among several. Unreachable through any input -- a covering
        // catch-all, the only thing that makes a throw's escape infeasible, is itself always a
        // candidate handler -- so this is a loud invariant rather than a tested branch, and it sits
        // outside the host-failure boundary so a later edit to the escape rule fails the suite instead
        // of quietly losing a feasible path.
        require(groups.all { it.successors.isNotEmpty() || it.ending != null }) {
            "A transition of the op at ${node.address} in ${node.view.signature} would go nowhere"
        }

        // --- phase 1: build, one group at a time ---
        val built = ArrayList<ExecutionNode>(groups.sumOf { it.successors.size })
        val endings = ArrayList<Ending>(1)
        val armFailures = ArrayList<Ending.ArmFailure>(0)
        // One entry per LOST PATH, carrying that path's own reason -- not a count. A count could not
        // say why: [buildSuccessor] fails for two causes that mean opposite things about whose fault
        // it is (see [SuccessorAttempt]), and a single `lost: Int` forced all three recording sites
        // below to pick one reason for every loss. They picked [AbortReason.HOST_FAILURE], so every
        // off-the-end jump in an obfuscated APK -- the expected input -- reported as an engine bug.
        val rejected = ArrayList<SuccessorAttempt.Unbuildable>(0)
        for (group in groups) {
            val fromGroup = ArrayList<ExecutionNode>(group.successors.size)
            var abandonedBecause: SuccessorAttempt.Unbuildable? = null
            val skipped = ArrayList<SuccessorAttempt.Unbuildable>(0)
            for (spec in group.successors) {
                when (val attempt = buildSuccessor(spec, node, group.armFrame ?: node.frame)) {
                    is SuccessorAttempt.Built -> fromGroup.add(attempt.node)
                    is SuccessorAttempt.Unbuildable -> when (spec.onUnbuildable) {
                        // Stop building this group: everything it would route is suspect, including
                        // specs already built above, which are dropped with it.
                        OnUnbuildable.ALL_OR_NOTHING -> {
                            abandonedBecause = attempt
                            break
                        }
                        OnUnbuildable.SKIP_ONE -> skipped.add(attempt)
                    }
                }
            }
            if (abandonedBecause != null) {
                // One lost path for the whole abandoned group, not one per spec: all-or-nothing treats
                // the group's routing as a single indivisible decision, and that decision failed once.
                // It is also what keeps a malformed 2-clause table reporting ONE abort, which is the
                // behavior `ThrowRoutingTest.oneMalformedHandlerAbortsTheWholeForkRatherThanCommitting
                // HalfOfIt` has pinned since Plan 3.
                //
                // The REASON is the failing spec's own, because `OnUnbuildable` decides how much is
                // abandoned and never whose fault the loss was. A malformed handler address is
                // therefore MALFORMED_INPUT, exactly as an off-the-end jump is.
                //
                // `skipped` is dropped here along with `fromGroup`, and that is the standing
                // group-mixing latency (`CARRY-FORWARD.md`): in a group mixing both policies, losses
                // recorded before the abandonment are absorbed into this single entry -- now their
                // reasons as well as their count. Unreachable today (`Continue`/`Jump` groups are
                // single-spec SKIP_ONE, `Throw` groups uniformly ALL_OR_NOTHING) and NOT fixed by this
                // list: absorbing them is a property of dropping `skipped`, not of the counter's type.
                // Left as-is because "one indivisible decision, one abort" is the stated policy and
                // there is no seam to test a mixed group; `rejected.addAll(skipped)` is the whole
                // fix if a later plan decides the policy should change.
                rejected.add(abandonedBecause)
                // The group's TERMINAL is dropped too. For a throw that is the point rather than a side
                // effect: the unhandled residual comes from the same malformed table as the handlers, so
                // "this escapes unhandled" is exactly as suspect as "this is caught at 9999" -- they
                // must agree about one table or neither may be trusted (see [planThrow]).
                continue
            }
            built.addAll(fromGroup)
            rejected.addAll(skipped)
            when (val ending = group.ending) {
                is Ending.ArmFailure -> armFailures.add(ending)
                null -> Unit
                else -> endings.add(ending)
            }
        }
        // Prefer an op-requested ending for the node itself. Arm-local failures still become outcomes,
        // but placing them last also lets a direct Return sibling retain the node's terminal frame.
        endings.addAll(armFailures)

        // Every group was abandoned or emptied and there is no surviving ending: the node itself must
        // end, or it would carry neither outcome nor children.
        if (built.isEmpty() && endings.isEmpty()) {
            // `rejected` cannot be empty here: `groups` is non-empty and the `require` above forbids
            // a group offering neither a successor nor a terminal, so a group that contributed nothing
            // to `built` or `endings` contributed at least one loss. `first()` rather than a fallback
            // reason on purpose -- inventing one would launder a scheduler bug into an abort, and this
            // function fails loudly on those instead (the two `require`s above sit outside the
            // host-failure boundary for the same reason).
            //
            // The FIRST reason is the node's, matching how `endings.first()` is chosen below: both
            // follow the op's declared transition order. Note that `node.hostFailure` may hold a
            // throwable belonging to a DIFFERENT lost path of the same node -- the imprecision
            // [hostFailureOutcome] already documents -- so read `node.outcome` for this path's reason.
            val first = rejected.first()
            if (node.activation.callCompletion == null) {
                record(node, Outcome.Aborted(first.reason), first.frame)
            } else {
                node.endWith(Outcome.Aborted(first.reason), first.frame)
                node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
            }
            // The node's OWN ending accounts for exactly ONE of the lost paths; any others still need
            // their own Outcome, because spec §4.4 is one Outcome per ended path and a node ends once.
            // Without this, `[Jump(9999), Jump(8888)]` reported ONE abort for TWO dead paths while
            // `[Jump(9999), Continue]` reported one per loss -- the same loss counted two ways.
            if (node.activation.callCompletion == null) {
                for (i in 1 until rejected.size) recordLostPath(node, rejected[i])
            } else {
                updateCallCompletion(
                    node,
                    emptyList(),
                    rejected.map { ActivationExit.Aborted(it.frame, it.reason) },
                )
            }
            return
        }

        // --- phase 2: commit ---
        built.forEach { node.addChild(it) }
        built.asReversed().forEach { frontierNodes.addLast(it) }
        if (node.activation.callCompletion == null) rejected.forEach { recordLostPath(node, it) }
        if (endings.isNotEmpty()) {
            // Slot writes happen HERE, after every sibling frame has already been copied. That
            // ordering IS the crosstalk fix, and it changes one behavior deliberately:
            //
            // PLAN 10 NOTE (moved here from the old `handlerNode`, restated for this ordering). A
            // handler successor's frame no longer inherits the throwing path's `thrown` slot, because
            // every successor above is copied before `setThrown` runs. Previously it did inherit it --
            // `Frame.copy` copies the slot and nothing clears it -- so a handler node's frame carried
            // BOTH `thrown` and `caught`. Under this ordering `Frame.thrownOrNull() != null` means
            // "the node whose frame this is ended by throwing", which is a claim worth having, but
            // read [ExecutionNode.outcome] if that is the question you are actually asking. Plan 12
            // owns cross-frame unwind and may still want the thrown value visible while unwinding;
            // if so it must carry it explicitly (a [SuccessorSpec.seed]), not by inheritance --
            // inheritance is what made the normal successor of a `[Continue, Throw]` fork look like a
            // throwing path, and picking that by accident is how the audit's exception defects
            // happened.
            //
            // The FIRST ending is the one this node carries. The rest ended paths too and each needs
            // its own Outcome (spec §4.4: one per ended path), but a node ends at most once
            // (ExecutionNode.endWith), and stamping a second would silently replace one meaning with
            // another. So they are recorded without a node -- the same shape `recordLostPath` uses,
            // except that a guest throw is NOT a truncation and must not add an AbortReason (see
            // [recordUnattributedEnding]).
            for (ending in endings) {
                when (val outcome = ending.outcome) {
                    is Outcome.Returned -> ending.frame.setReturnFrameValue(ending.returnValue)
                    is Outcome.Threw -> ending.frame.setThrown(outcome.exception)
                    is Outcome.Aborted -> Unit
                }
            }
            val first = endings.first()
            // Whose path ended decides HOW it is recorded, and the compiler makes that a choice: a callee's
            // `return` stamps the node without adding to `outcomes`, because the run's path did not end.
            when (first) {
                is Ending.RunPath, is Ending.ArmFailure -> {
                    if (node.activation.callCompletion != null && first.outcome is Outcome.Aborted) {
                        node.endWith(first.outcome, first.frame)
                        node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
                    } else {
                        record(node, first.outcome, first.frame)
                    }
                }
                is Ending.ActivationOnly -> node.endWith(first.outcome, first.frame)
            }
            endings.drop(1).forEach { recordUnattributedEnding(node, it) }
        }
        val activationExits = buildList {
            endings.mapNotNullTo(this) { it.activationExit }
            if (node.activation.callCompletion != null) {
                endings.mapNotNullTo(this) { ending ->
                    val aborted = ending.outcome as? Outcome.Aborted ?: return@mapNotNullTo null
                    if (ending is Ending.ActivationOnly) null else ActivationExit.Aborted(ending.frame, aborted.reason)
                }
            }
            if (node.activation.callCompletion != null) {
                rejected.mapTo(this) { ActivationExit.Aborted(it.frame, it.reason) }
            }
        }
        updateCallCompletion(node, built, activationExits)
    }

    /** Replaces one activation path and routes its immutable completion batch only after all paths end. */
    private fun updateCallCompletion(
        node: ExecutionNode,
        built: List<ExecutionNode>,
        exits: List<ActivationExit> = emptyList(),
    ) {
        val completion = node.activation.callCompletion ?: return
        val sameActivation = built.count { it.activation === node.activation }
        val nestedCalls = built.count { it.activation.caller === node.activation }
        completion.replacePath(sameActivation + nestedCalls, exits)
        completion.drainIfComplete()?.let { routeOrEnqueueCallCompletion(node, node.activation, it) }
    }

    private class PendingCallRouting(
        val anchor: ExecutionNode,
        val activation: Activation,
        val batch: CompletionBatch,
    )

    /** Defers a newly completed parent boundary instead of recursing through the host stack. */
    private fun routeOrEnqueueCallCompletion(
        anchor: ExecutionNode,
        activation: Activation,
        batch: CompletionBatch,
    ) {
        pendingCallRoutings.addLast(PendingCallRouting(anchor, activation, batch))
    }

    /** Runs completion-routing commands without exposing them as executable nodes. */
    private fun drainPendingCallRoutings() {
        while (pendingCallRoutings.isNotEmpty()) {
            routingWorkChecks++
            val limit = schedulerLimitReason()
            if (limit != null) {
                abortRun(null, limit)
                return
            }
            val pending = pendingCallRoutings.removeLast()
            val exits = pending.batch.exitsSnapshot()
            try {
                routingCommands++
                routingInvariant()
                routeCallCompletion(pending.anchor, pending.activation, exits)
            } finally {
                pending.batch.release()
            }
        }
    }

    /**
     * Applies JLS 12.4.2 steps 11-12 before the trigger's caller performs handler lookup.
     *
     * Each source exit is failure-atomic. Transformation uses copied frames, and the failed lifecycle is
     * written only after every outward throw for that exit is valid. An ordinary model failure therefore
     * aborts only that exit and cannot discard an independent return or throw from the same initializer.
     */
    private fun transformInitializerThrows(
        node: ExecutionNode,
        context: ClassInitContext,
        exits: List<ActivationExit.Threw>,
    ): List<ActivationExit> = buildList {
        for (exit in exits) {
            val transformed = try {
                transformInitializerThrow(context, exit)
            } catch (failure: Throwable) {
                val abort = hostFailureOutcome(node, failure)
                add(ActivationExit.Aborted(exit.frame, abort.reason))
                continue
            }
            addAll(transformed)
        }
    }

    private fun transformInitializerThrow(
        context: ClassInitContext,
        exit: ActivationExit.Threw,
    ): List<ActivationExit.Threw> {
        val partition = partitionThrowableDomain(
            exit.subject.domain,
            VirtualType.of("Ljava/lang/Error;"),
            throwableHierarchy,
        )
        val transformed = buildList {
            for (domain in listOfNotNull(partition.matching, partition.residual)) {
                val frame = exit.frame.copy()
                val source = exit.subject.refinedTo(domain, throwableHierarchy)
                val outward = if (domain === partition.matching) {
                    source
                } else {
                    materializeRecipe(
                        ThrowableRecipe.of(
                            VirtualType.of("Ljava/lang/ExceptionInInitializerError;"),
                            cause = source,
                        ),
                        frame,
                    )
                }
                add(ActivationExit.Threw(ThrowPath.create(frame, outward, throwableHierarchy)))
            }
        }
        transformed.forEach { it.frame.failClassInit(context, it.subject, throwableHierarchy) }
        return transformed
    }

    /** Synthetic outcome fork: one normal child and one child per caller handler partition. */
    private fun routeCallCompletion(anchor: ExecutionNode, activation: Activation, exits: List<ActivationExit>) {
        val site = requireNotNull(activation.returnSite)
        val caller = requireNotNull(activation.caller)
        site.invokeNode.sideEffectLevel = maxOf(site.invokeNode.sideEffectLevel, activation.declaredEffect)
        val returned = exits.filterIsInstance<ActivationExit.Returned>()
        var threw = exits.filterIsInstance<ActivationExit.Threw>()
        var aborted = exits.filterIsInstance<ActivationExit.Aborted>()
        aborted.forEach { abortReasons += it.reason }
        val classInitContext = activation.classInitContext
        if (classInitContext == null) {
            if (returned.isNotEmpty()) site.invokeNode.normalContinuationObserved = true
            if (threw.isNotEmpty() || aborted.isNotEmpty()) {
                site.invokeNode.exceptionalContinuationObserved = true
            }
        }

        // An ordinary abort makes only that outcome partition opaque. Return and throw siblings remain exact.
        if (classInitContext == null && aborted.isNotEmpty()) {
            site.invokeNode.sideEffectLevel = SideEffectLevel.STRONG
            activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
        }

        if (classInitContext != null && threw.isNotEmpty()) {
            val transformed = transformInitializerThrows(site.invokeNode, classInitContext, threw)
            threw = transformed.filterIsInstance<ActivationExit.Threw>()
            aborted += transformed.filterIsInstance<ActivationExit.Aborted>()
        }
        val specs = mutableListOf<SuccessorSpec>()

        if (returned.isNotEmpty()) {
            data class ReturnPartition(
                val state: CallCompatibilityKey,
                val frameValue: FrameValue?,
            )
            val partitions = returned.groupBy { exit ->
                ReturnPartition(
                    exit.frame.callCompatibilityKey(),
                    exit.frameValue,
                )
            }
            val mergedPartitions = partitions.mapValues { (_, paths) ->
                val merged = mergeCallPartition(site, paths.map { it.frame })
                classInitContext?.let(merged::completeClassInit)
                merged
            }
            for (exit in returned) {
                val key = ReturnPartition(exit.frame.callCompatibilityKey(), exit.frameValue)
                specs += SuccessorSpec(
                    site.resumeAddress,
                    caller,
                    OnUnbuildable.SKIP_ONE,
                    FrameOrigin.CallerPartition(requireNotNull(mergedPartitions[key])),
                ) { frame ->
                    exit.frameValue?.let(frame::setResult)
                    site.constructorReceiver?.let(frame::markInitialized)
                }
            }
        }

        if (classInitContext == null && aborted.isNotEmpty()) {
            val arguments = requireNotNull(site.fallbackArguments)
            val argumentTypes = requireNotNull(site.fallbackArgumentTypes)
            val returnType = requireNotNull(site.fallbackReturnType)
            val partitions = aborted.groupBy { it.frame.callCompatibilityKey() }
            val successorByPartition = partitions.mapValues { (_, partition) ->
                opaqueSuccessor(
                    mergeCallPartition(site, partition.map { it.frame }),
                    site.resumeAddress,
                    caller,
                    arguments,
                    argumentTypes,
                    site.constructorReceiver,
                    returnType,
                )
            }
            for (exit in aborted) {
                specs += requireNotNull(successorByPartition[exit.frame.callCompatibilityKey()])
            }
        }

        val routingAborts = mutableListOf<ActivationExit.Aborted>()
        if (classInitContext != null && aborted.isNotEmpty()) {
            site.invokeNode.sideEffectLevel = SideEffectLevel.STRONG
            activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
            aborted.mapTo(routingAborts) { exit ->
                ActivationExit.Aborted(site.invokeNode.frame.resumedWith(exit.frame), exit.reason)
            }
        }
        val throwGroups = mutableListOf<CallerThrowGroup>()
        val keyedExits = threw.map { exit -> KeyedCallerThrow(exit, callerThrowSourceKey(exit)) }
        val sourcePartitions = keyedExits.groupBy(KeyedCallerThrow::key)
        sourceThrowPartitions += sourcePartitions.size
        sourceThrowExits += threw.size
        val sourceRoutings = LinkedHashMap<CallerThrowSourceKey, CallerSourceRouting>()
        for ((key, keyedPartition) in sourcePartitions) {
            val callerFrame = mergeSourceThrowPartition(site, keyedPartition.map { it.exit.frame })
            val mergedSubject = mergedCallerThrowSubject(key, callerFrame, keyedPartition.map { it.exit })
            // Same containment policy as local lookup. The invoke node is suspended, so `contained` cannot
            // record it as ended here; carry one abort token per original exit into this completion instead.
            val resolution = try {
                resolveHandlers(
                    site.invokeNode.address,
                    mergedSubject,
                    site.invokeNode.view,
                    throwableHierarchy,
                )
            } catch (failure: Throwable) {
                val abort = hostFailureOutcome(site.invokeNode, failure)
                activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
                sourceRoutings[key] = CallerSourceRouting.Aborted(callerFrame, abort.reason)
                continue
            }
            recordCallerResolverMetrics(resolution.metrics)
            if (resolution is HandlerResolution.MalformedMetadata) {
                activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
                sourceRoutings[key] = CallerSourceRouting.Aborted(callerFrame, AbortReason.MALFORMED_INPUT)
                continue
            }
            sourceRoutings[key] = CallerSourceRouting.Routed(callerFrame, resolution as HandlerResolution.Routed)
        }
        for ((_, key) in keyedExits) {
            when (val routing = requireNotNull(sourceRoutings[key])) {
                is CallerSourceRouting.Aborted -> {
                    routingAborts += ActivationExit.Aborted(routing.frame, routing.reason)
                }
                is CallerSourceRouting.Routed -> {
                    val routeSpecs = routing.resolution.routes.map { route ->
                        SuccessorSpec(
                            route.handlerAddress,
                            caller,
                            OnUnbuildable.ALL_OR_NOTHING,
                            FrameOrigin.CallerPartition(routing.frame),
                        ) { frame -> frame.setCaught(route.subject) }
                    }
                    val escape = routing.resolution.unhandled?.let { unhandled ->
                        ActivationExit.Threw(
                            ThrowPath.create(
                                routing.frame,
                                unhandled.subject,
                                throwableHierarchy,
                            ),
                        )
                    }
                    throwGroups += CallerThrowGroup(routing.frame, routeSpecs, escape)
                }
            }
        }

        val rejected = installSyntheticPartitions(
            anchor,
            caller,
            specs,
            emptyList(),
            routingAborts,
            throwGroups,
        )
        if (rejected) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    private fun mergeCallPartition(site: ReturnSite, frames: List<Frame>): Frame {
        callPartitionMerges++
        return site.invokeNode.frame.mergedCalleeState(frames, resolver.hierarchy)
    }

    private fun mergeSourceThrowPartition(site: ReturnSite, frames: List<Frame>): Frame {
        sourcePartitionMerges++
        return mergeCallPartition(site, frames)
    }

    /** Pre-resolution source identity. Handler addresses are deliberately absent. */
    private sealed interface CallerThrowSourceKey {
        val state: CallCompatibilityKey

        class Concrete(
            val frameValue: FrameValue.RefinedThrowable,
            val routingDomain: ThrowableDomain?,
            override val state: CallCompatibilityKey,
            domainHash: Int,
        ) : CallerThrowSourceKey {
            val value = checkNotNull(frameValue.value as? Value.Known) {
                "A concrete caller throw key must hold a known value"
            }
            private val cachedHash = 31 * (31 * value.hashCode() + domainHash) + state.hashCode()

            override fun equals(other: Any?): Boolean = this === other || other is Concrete &&
                value == other.value &&
                routingDomain == other.routingDomain &&
                state == other.state

            override fun hashCode(): Int = cachedHash
        }

        class Symbolic(
            val domain: ThrowableDomain,
            override val state: CallCompatibilityKey,
            domainHash: Int,
        ) : CallerThrowSourceKey {
            private val cachedHash = 31 * domainHash + state.hashCode()

            override fun equals(other: Any?): Boolean = this === other || other is Symbolic &&
                domain == other.domain && state == other.state

            override fun hashCode(): Int = cachedHash
        }
    }

    private fun callerThrowSourceKey(exit: ActivationExit.Threw): CallerThrowSourceKey {
        val subject = exit.subject
        if (subject.concreteRef == null) {
            return CallerThrowSourceKey.Symbolic(
                subject.domain,
                exit.frame.callCompatibilityKey(),
                subject.domain.sourceKeyHash(sourceDomainHashMetrics),
            )
        }
        // An exact runtime type decides every present class relation. Only unresolved required/excluded
        // roots can distinguish routes for the same concrete ref; keep those partitions without treating
        // an equivalent concrete upper-bound refinement as a different source.
        val routingDomain = subject.domain.takeIf {
            it.unresolvedRequiredRoots.isNotEmpty() || it.excludedRoots.isNotEmpty()
        }
        return CallerThrowSourceKey.Concrete(
            subject.frameValue,
            routingDomain,
            exit.frame.callCompatibilityKey(),
            routingDomain?.sourceKeyHash(sourceDomainHashMetrics) ?: 0,
        )
    }

    private data class KeyedCallerThrow(
        val exit: ActivationExit.Threw,
        val key: CallerThrowSourceKey,
    )

    private fun mergedCallerThrowSubject(
        key: CallerThrowSourceKey,
        frame: Frame,
        exits: List<ActivationExit.Threw>,
    ): ThrownSubject = when (key) {
        is CallerThrowSourceKey.Concrete -> {
            if (key.routingDomain == null) {
                ThrownSubject.fromHeap(
                    key.value,
                    frame,
                    throwableHierarchy,
                )
            } else {
                ThrownSubject.fromFrameValue(key.frameValue, frame, throwableHierarchy)
            }
        }
        is CallerThrowSourceKey.Symbolic -> {
            val joined = requireNotNull(joinAllFrameValues(exits.map { it.subject.frameValue }, resolver.hierarchy))
            check(joined is FrameValue.RefinedThrowable && joined.domain == key.domain) {
                "A symbolic source partition lost its shared throwable domain"
            }
            ThrownSubject.symbolic(joined.value, key.domain)
        }
    }

    private fun recordCallerResolverMetrics(metrics: HandlerResolverMetrics) {
        callerHandlerResolutions++
        callerHandlerClausesVisited += metrics.clausesVisited
        callerHandlerRelationRequests += metrics.relationRequests
        callerHandlerRoutes += metrics.routes
    }

    private sealed interface CallerSourceRouting {
        class Aborted(val frame: Frame, val reason: AbortReason) : CallerSourceRouting
        class Routed(val frame: Frame, val resolution: HandlerResolution.Routed) : CallerSourceRouting
    }

    private class CallerThrowGroup(
        val abortFrame: Frame,
        val successors: List<SuccessorSpec>,
        val escape: ActivationExit.Threw?,
    )

    private class RejectedSynthetic(val frame: Frame, val reason: AbortReason)
    private class SyntheticSchedule(
        val built: Int,
        val rejected: List<RejectedSynthetic>,
        val escapes: List<ActivationExit.Threw>,
    )

    private fun scheduleSyntheticPartitions(
        anchor: ExecutionNode,
        specs: List<SuccessorSpec>,
        throwGroups: List<CallerThrowGroup>,
    ): SyntheticSchedule {
        val built = mutableListOf<ExecutionNode>()
        val rejected = mutableListOf<RejectedSynthetic>()
        val escapes = mutableListOf<ActivationExit.Threw>()
        for (spec in specs) {
            when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                is SuccessorAttempt.Built -> built += attempt.node
                is SuccessorAttempt.Unbuildable -> {
                    rejected += RejectedSynthetic(attempt.frame, attempt.reason)
                }
            }
        }
        for (group in throwGroups) {
            val groupBuilt = mutableListOf<ExecutionNode>()
            var rejection: RejectedSynthetic? = null
            for (spec in group.successors) {
                when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                    is SuccessorAttempt.Built -> groupBuilt += attempt.node
                    is SuccessorAttempt.Unbuildable -> {
                        rejection = RejectedSynthetic(group.abortFrame, attempt.reason)
                        break
                    }
                }
            }
            if (rejection == null) {
                built += groupBuilt
                group.escape?.let(escapes::add)
            } else {
                rejected += rejection
            }
        }
        built.forEach(anchor::addChild)
        built.asReversed().forEach(frontierNodes::addLast)
        return SyntheticSchedule(built.size, rejected, escapes)
    }

    /** Replace one suspended caller path with only the successors that were actually built. */
    private fun installSyntheticPartitions(
        anchor: ExecutionNode,
        caller: Activation,
        specs: List<SuccessorSpec>,
        escapes: List<ActivationExit.Threw>,
        routingAborts: List<ActivationExit.Aborted> = emptyList(),
        throwGroups: List<CallerThrowGroup> = emptyList(),
    ): Boolean {
        val scheduled = scheduleSyntheticPartitions(anchor, specs, throwGroups)
        val committedEscapes = escapes + scheduled.escapes
        val parent = caller.callCompletion
        if (parent == null) {
            committedEscapes.forEach { recordedOutcomes += Outcome.Threw(it.path.subject.value) }
            routingAborts.forEach {
                recordedOutcomes += Outcome.Aborted(it.reason)
                abortReasons += it.reason
            }
            scheduled.rejected.forEach {
                recordedOutcomes += Outcome.Aborted(it.reason)
                abortReasons += it.reason
            }
            return scheduled.rejected.isNotEmpty() || routingAborts.isNotEmpty()
        }
        val exits = buildList<ActivationExit> {
            addAll(committedEscapes)
            addAll(routingAborts)
            scheduled.rejected.mapTo(this) { ActivationExit.Aborted(it.frame, it.reason) }
        }
        parent.replacePath(scheduled.built, exits)
        parent.drainIfComplete()?.let { routeOrEnqueueCallCompletion(anchor, caller, it) }
        return scheduled.rejected.isNotEmpty() || routingAborts.isNotEmpty()
    }

    /**
     * What trying to turn one [SuccessorSpec] into a node produced: the node, or the **reason** it
     * could not be built.
     *
     * A nullable `ExecutionNode?` stood here and could not do the job. `null` meant two different
     * things -- an address this method does not contain, and a successor whose construction threw --
     * and [commit], which is where the abort is actually recorded, had no way to tell them apart. So
     * it recorded both as [AbortReason.HOST_FAILURE] at all three of its recording sites, and every
     * off-the-end jump in an obfuscated APK (the expected input for this tool) became
     * indistinguishable in aggregate from a genuine bug in this codebase. The two causes are opposite
     * claims about whose fault a lost path is, so collapsing them destroys the only signal that means
     * "look at our code" -- and a blanket relabel in the other direction would have destroyed it just
     * as thoroughly.
     *
     * A type rather than a comment, per the spec's "type-enforce the rule instead of documenting it":
     * the cause is decided at the one place that can know it and merely *carried* afterwards.
     */
    private sealed interface SuccessorAttempt {
        /** Built. Scheduled unless a sibling's [Unbuildable] abandons the whole group -- see [commit]. */
        class Built(val node: ExecutionNode) : SuccessorAttempt

        /**
         * Not built. [reason] is the diagnostic and [frame] is the exact copied, seeded successor state.
         * Result/caught slots and constructor completion belong to this rejected path and must reach an
         * enclosing opaque fallback.
         */
        class Unbuildable(val reason: AbortReason, val frame: Frame) : SuccessorAttempt
    }

    /**
     * [spec] as a real node, or **why** it could not be built ([SuccessorAttempt]).
     * [MethodView.hasAddress] is checked before decode because decode does a `requireNotNull`: a bad
     * address must be *detected*, not caught, so no throwable is invented for a condition nothing
     * threw -- and that ordering is also what makes the two causes separable at all, since a caught
     * `IllegalArgumentException` from `requireNotNull` looks exactly like any other host failure.
     * Construction is foreign code, so its failure goes through [hostFailureOutcome] for the
     * [VirtualMachineError] rethrow and to keep the real throwable on the node; that call's returned
     * `Outcome` is discarded because WHERE the abort is recorded is [commit]'s decision -- only the
     * reason and exact rejected frame travel back in the result.
     *
     * The frame comes from [SuccessorSpec.frameOrigin] and is **never** an existing node's own instance:
     * normal and exceptional successors must not share state, or a speculative path would inherit or clobber
     * the other's writes (the audit's shared-mutated-state defect). [spec]'s seed then writes only that new
     * frame. All origins produce a fresh [Frame]; see [FrameOrigin] for which state each carries over.
     */
    private fun buildSuccessor(spec: SuccessorSpec, node: ExecutionNode, armFrame: Frame): SuccessorAttempt {
        var frame: Frame? = null
        return try {
            val seeded = when (val origin = spec.frameOrigin) {
                FrameOrigin.Continuation -> armFrame.copy()
                FrameOrigin.CalleeEntry -> armFrame.calleeEntryFrame(spec.view)
                is FrameOrigin.CallerPartition -> origin.frame.copy()
                is FrameOrigin.OpaqueResult -> origin.frame.copy()
            }
            frame = seeded
            spec.seed(seeded)
            if (!spec.view.hasAddress(spec.address)) {
                SuccessorAttempt.Unbuildable(AbortReason.MALFORMED_INPUT, seeded)
            } else {
                SuccessorAttempt.Built(nodeAt(spec.address, spec.activation, seeded, node))
            }
        } catch (t: Throwable) {
            hostFailureOutcome(node, t)
            SuccessorAttempt.Unbuildable(AbortReason.HOST_FAILURE, frame ?: armFrame.copy())
        }
    }

    /**
     * A path that ended before it ever had a node. At top level it is a run abort. Inside an ordinary
     * callee it is an [ActivationExit.Aborted], so that outcome partition uses opaque fallback.
     */
    private fun recordLostPath(node: ExecutionNode, rejected: SuccessorAttempt.Unbuildable) {
        node.activation.callCompletion?.let { completion ->
            completion.replacePath(
                replacementOpenPaths = 0,
                exits = listOf(ActivationExit.Aborted(rejected.frame, rejected.reason)),
            )
            node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
            completion.drainIfComplete()?.let { routeOrEnqueueCallCompletion(node, node.activation, it) }
            return
        }
        recordedOutcomes.add(Outcome.Aborted(rejected.reason))
        abortReasons.add(rejected.reason)
        // [node] is the FORKING node, which is why this takes one at all: a lost path has no node of its own,
        // but it was lost inside [node]'s activation, so if that is a callee the enclosing call did not
        // complete on this path either. See [accountForIncompleteCall].
        accountForIncompleteCall(node, Outcome.Aborted(rejected.reason))
    }

    /**
     * A path that ended at a node which had already ended one. The full [Ending] is retained because a
     * callee's activation-only throw belongs to its [CallCompletion], not the run's global outcomes.
     *
     * Distinct from [recordLostPath] in the one way that matters: **a guest throw is a guest-visible outcome,
     * not a truncation, so it must not add an [AbortReason]** --
     * `OpArmStateTest.unknownArrayReadProducesTwoDistinctThrowableOutcomes` pins that. An
     * `Aborted` reaching here IS a truncation
     * and does add one. A callee escape is activation-only and returns above before this run-level recording.
     * A callee arm failure also returns above because [updateCallCompletion] records its activation exit.
     * The `when` below keeps that distinction explicit.
     *
     * [Outcome.Returned] cannot reach here: [applyAll]'s `require` allows a second ending only when every
     * path-ending transition is a `Transition.Throw`, and a throw never plans a `Returned`. Loud rather than
     * silently tolerated, for the same reason that `require` is.
     */
    private fun recordUnattributedEnding(node: ExecutionNode, ending: Ending) {
        val outcome = ending.outcome
        if (ending is Ending.ActivationOnly) return
        if (node.activation.callCompletion != null && outcome is Outcome.Aborted) return
        unattributedEndingFrames += ending.frame
        recordedOutcomes.add(outcome)
        accountForIncompleteCall(node, outcome)
        when (outcome) {
            is Outcome.Threw -> Unit
            is Outcome.Aborted -> abortReasons.add(outcome.reason)
            is Outcome.Returned -> error(
                "A second ending of one node cannot be a return: only several guest throws may end several " +
                    "paths at one node, and a callee's return continues the run's path rather than ending it",
            )
        }
    }

    /**
     * The host-failure boundary (spec §7): runs one call into foreign code and converts ordinary host
     * throwables into `Aborted(HOST_FAILURE)` on [node]'s path, returning null. [VirtualMachineError]
     * escapes. Kept around individual foreign calls so scheduler invariants still fail loudly.
     */
    private inline fun <T> contained(node: ExecutionNode, body: () -> T): T? =
        try {
            body()
        } catch (t: Throwable) {
            // DO NOT INLINE THIS CONVERSION. It must go through hostFailureOutcome, which is where the
            // VirtualMachineError rethrow lives. Inlining it -- even keeping the `node.hostFailure = t`
            // half, which is what a "simplify this" refactor would do -- restores a hang: a JVM-fatal
            // error would be converted into Aborted(HOST_FAILURE), the frontier is deliberately not
            // dropped for a host failure, and the loop would re-fail up to maxSteps (10^6 by default),
            // emitting a million identical aborts instead of dying fast.
            // InvocationClassInitTest drives this exact boundary with a scripted StackOverflowError.
            record(node, hostFailureOutcome(node, t))
            null
        }

    /** Converts a planning failure into one arm-local abort on the arm frame currently installed on [node]. */
    private inline fun planContained(node: ExecutionNode, body: () -> Committable): Committable =
        try {
            body()
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            hostFailureArm(node, failure)
        }

    private fun hostFailureArm(node: ExecutionNode, failure: Throwable, frame: Frame = node.frame): Committable =
        Committable(emptyList(), Ending.ArmFailure(hostFailureOutcome(node, failure), frame))

    /**
     * The boundary's conversion policy. A [VirtualMachineError] is **rethrown**: the JVM is out of
     * memory or stack, calling that an aborted path would not be true, and -- because a host failure
     * deliberately does not drop the frontier (per-path containment is the whole point) -- the loop
     * would otherwise keep stepping and re-failing up to `maxSteps`, turning a fast crash into a long
     * hang emitting a million identical aborts.
     *
     * `internal` rather than private because the direct policy test also distinguishes ordinary failures.
     * InvocationClassInitTest separately pins [contained]'s real call site with a scripted
     * [StackOverflowError].
     *
     * [node] is where the failure HAPPENED, not necessarily a node that ends: under
     * [OnUnbuildable.SKIP_ONE] this records on a forking node that keeps its live siblings. When
     * several successors of one fork fail to decode, each call overwrites
     * [ExecutionNode.hostFailure] and only the LAST throwable survives -- acceptable because the field
     * is a debugging aid and every loss still gets its own [Outcome] via `recordLostPath`, but it means
     * the field is "a throwable from this node", not "the throwable for a particular lost path".
     * A lost path whose reason is [AbortReason.MALFORMED_INPUT] never reaches here at all, so the
     * field can also be null on a node that ended by aborting, or hold a throwable belonging to a
     * *sibling* lost path -- read [ExecutionNode.outcome] for a path's own reason.
     */
    internal fun hostFailureOutcome(node: ExecutionNode, t: Throwable): Outcome.Aborted {
        if (t is VirtualMachineError) throw t
        node.hostFailure = t
        return Outcome.Aborted(AbortReason.HOST_FAILURE)
    }

    /** Runs to completion and returns the immutable result. */
    fun run(): ExecutionGraph {
        while (step() != null) Unit
        return graph()
    }

    /**
     * An immutable snapshot of the run so far. Safe to call mid-run (e.g. from a debugger) -- and safe
     * in the sense that matters, not merely "does not throw": the snapshot carries [completion] as of
     * now, which is [CompletionStatus.InProgress] while any path is still scheduled, so
     * [ExecutionGraph.trusted] is null on a mid-run snapshot and no consumer can act on a walk that
     * has not finished. That is the whole reason [CompletionStatus.InProgress] exists -- see its KDoc.
     */
    fun graph(): ExecutionGraph = ExecutionGraph(
        methodView = methodView,
        // TWO maps, and the asymmetry is `ExecutionGraph`'s class KDoc, not a detail. A CALLEE's nodes are in
        // neither -- folding those in is the collision `CARRY-FORWARD.md` Plan 9 item 2 describes -- but a
        // RECURSIVE activation of this same method is unioned into the first and excluded from the second:
        // reachability and per-address consensus must see every activation that executed an address (a `false`
        // from `wasReached` on a Complete run is a documented dead-code PROOF), while "what this run returned"
        // is the entry activation's alone.
        addressToNodes = unionedPileSnapshot(),
        addressToNodesInThisRunsActivation = entryActivation.pileSnapshot(),
        outcomes = recordedOutcomes,
        completion = completion(),
        executedNodes = executedNodes.toSet(),
        superclasses = resolver.hierarchy,
    )

    /**
     * Every activation of the ENTRY method's piles, merged by address, oldest node first.
     *
     * Application activations match by captured program and method revision. Framework activations match by
     * catalog-scoped framework identity. Fresh projections of one captured method still match.
     *
     * Node ORDER within an address follows [activations]' creation order, then each activation's own piling
     * order -- both deterministic, so the merged pile is reproducible and any join folded over it is too.
     */
    private fun unionedPileSnapshot(): Map<Int, List<ExecutionNode>> {
        val ofThisMethod = activations.filter { it.view.matchesActivationMethod(methodView) }
        if (ofThisMethod.size == 1) return entryActivation.pileSnapshot()
        val merged = LinkedHashMap<Int, MutableList<ExecutionNode>>()
        for (activation in ofThisMethod) {
            for ((address, nodes) in activation.addressToNodes) {
                merged.getOrPut(address) { mutableListOf() }.addAll(nodes)
            }
        }
        return merged.mapValues { it.value.toList() }
    }

    /**
     * Bounds, not semantics: a step or time limit ends the whole run, while an address-visit or call-depth
     * limit ends only the offending path so sibling paths keep exploring. A host failure ends only its path
     * too -- per-path containment is the point of the boundary, so it must never drop the frontier.
     *
     * Checked in the old engine's order (`VirtualMachine.step`): address visits, then -- at a callee's
     * entrypoint only -- call depth.
     */
    private fun exceededLimits(node: ExecutionNode): Boolean {
        schedulerLimitReason()?.let { return abortRun(node, it) }
        if (limits.maxAddressVisits > 0) {
            // Per path. Sibling reconvergence is not a loop and cannot consume another path's budget.
            // Frame copies share the persistent root until this one path records its next visit.
            val visits = node.frame.recordAddressVisit(node.address)
            if (visits > limits.maxAddressVisits) {
                record(node, Outcome.Aborted(AbortReason.ADDRESS_LIMIT))
                return true
            }
        }
        // Plan resolved decision 10, and the bound that makes invocation safe to land at all: a nested call
        // is the first thing that can recurse, and nothing else in this build can. Checked where the old
        // engine checks it -- at a call's ENTRY node only, never at every step (`VirtualMachine.kt`'s
        // `current.isEntrypoint` branch). Ends only this path, which is the old engine's "clears the current
        // frame" and is what leaves the caller's other paths explorable.
        //
        // Compared against `frameCount`, NOT `callDepth`, and the difference is one whole frame. The oracle's
        // `callDepth` is 1-BASED -- its top-level node is its own `isEntrypoint` because `parent == null`, so
        // it counts itself (`ExecutionNode.kt:67-78`; `execution-engine.md:180`: "top-level depth is one").
        // `Activation.depth` is 0-based, so `depth > 20` permits 21 frames where the oracle permits 20.
        // `frameCount` is the value that shares the oracle's origin, and Task 22's differential compares
        // limit behavior, so the origins have to match rather than merely be documented.
        if (limits.maxCallDepth > 0 && node.isCallEntry && node.activation.frameCount > limits.maxCallDepth) {
            record(node, Outcome.Aborted(AbortReason.CALL_DEPTH))
            return true
        }
        return false
    }

    /** Counts one guest node or one routing command as scheduler work. Routing records no address visit. */
    private fun schedulerLimitReason(): AbortReason? {
        steps++
        if (limits.maxSteps > 0 && steps > limits.maxSteps) return AbortReason.METHOD_LIMIT
        if (deadlineMillis > 0 && currentTimeMillis() > deadlineMillis) return AbortReason.TIMEOUT
        return null
    }

    private fun currentTimeMillis(): Long = clockMillis()

    /**
     * Ends the whole run on [reason]: the dequeued node plus **every** node still scheduled. Dropping
     * the frontier truncates one path per scheduled node, so each gets its own [Outcome] -- one
     * `Outcome` per ended path holds even when the ending is a limit, and under-reporting here would
     * understate how much exploration was cut (spec §6.4). Drained LIFO so the order is reproducible.
     *
     * [node] is already piled by [step]'s unconditional `addToPile` before this is ever called; every
     * OTHER node drained from the frontier below never went through that call (it was never step()'s
     * "currently dequeued" node), so it is piled here instead -- same piling decision, same reason:
     * none of these ever ran their op, but none should vanish as an unqueried sibling either.
     *
     * Collected activation exits are ended call paths with no frontier node, so cancellation records each
     * one. An open path of a completion is a frontier node or a nested activation, recorded by its own owner.
     */
    private fun abortRun(node: ExecutionNode?, reason: AbortReason): Boolean {
        node?.let { abortScheduledNode(it, reason, pile = false) }
        while (frontierNodes.isNotEmpty()) {
            val abandoned = frontierNodes.removeLast()
            abortScheduledNode(abandoned, reason, pile = true)
        }
        while (pendingCallRoutings.isNotEmpty()) {
            val pending = pendingCallRoutings.removeLast()
            pending.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
            repeat(pending.batch.retainedFrames) { recordedOutcomes += Outcome.Aborted(reason) }
            pending.batch.release()
        }
        for (activation in activations) {
            val completion = activation.callCompletion ?: continue
            if (completion.metrics().drained) continue
            repeat(completion.cancelAndRelease().size) { recordedOutcomes += Outcome.Aborted(reason) }
        }
        abortReasons += reason
        return true
    }

    private fun abortScheduledNode(node: ExecutionNode, reason: AbortReason, pile: Boolean) {
        if (pile) addToPile(node)
        if (node.outcome == null) node.endWith(Outcome.Aborted(reason))
        recordedOutcomes += Outcome.Aborted(reason)
        node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    /** Materializes or imports one committed throw event exactly once on its arm-owned frame. */
    internal fun normalizeThrow(evidence: ThrowEvidence, armFrame: Frame): ThrowPath {
        val subject = when (evidence) {
            is ThrowEvidence.Fresh -> materializeRecipe(evidence.recipe, armFrame)
            is ThrowEvidence.Existing -> when (val value = evidence.frameValue) {
                is FrameValue.Plain -> normalizeExistingThrow(value.value, armFrame)
                is FrameValue.RefinedThrowable ->
                    ThrownSubject.fromFrameValue(value, armFrame, throwableHierarchy)
            }
        }
        subject.concreteRef?.let { ref ->
            require(armFrame.heapObject(ref) != null) { "Normalized throw ref $ref is absent from its arm frame" }
        }
        return ThrowPath.create(armFrame, subject, throwableHierarchy)
    }

    private fun normalizeThrow(
        throwable: HostThrowable,
        armFrame: Frame,
        selected: SelectedSynchronousCall? = null,
        comparison: HostGraphComparison? = null,
    ): ThrowPath = normalizeThrow(
        ThrowEvidence.Existing(importHostThrowable(armFrame, throwable, selected, comparison)),
        armFrame,
    )

    private fun normalizeExistingThrow(value: Value, frame: Frame): ThrownSubject {
        if (value !is Value.Known) return ThrownSubject.symbolic(value, symbolicThrowableDomain(value))
        if (value.readsAsNullReference) {
            return materializeRecipe(
                ThrowableRecipe.of(VirtualType.of("Ljava/lang/NullPointerException;")),
                frame,
            )
        }
        val raw = value.raw
        if (raw is Throwable) {
            val imported = importHostThrowable(frame, HostThrowable(virtualTypeOfHostClass(raw.javaClass), raw))
            return ThrownSubject.fromHeap(imported, frame, throwableHierarchy)
        }
        if (raw !is HeapRef) return materializeNonThrowable(value, frame)
        val obj = frame.heapObject(raw) ?: return materializeNonThrowable(value, frame)
        if (obj is HeapObject.HostObject) {
            val host = frame.hostObjectAt(raw)
            if (host != null && host !is Throwable) return materializeNonThrowable(value, frame)
        }
        val relation = throwableHierarchy.facts(obj.type).throwableRelation
        if (relation == TypeRelation.NO) return materializeNonThrowable(value, frame)
        return ThrownSubject.fromHeap(value, frame, throwableHierarchy)
    }

    private fun symbolicThrowableDomain(value: Value): ThrowableDomain {
        val sourceType = value.type ?: return ThrowableDomain.any()
        if (!sourceType.isReference) return ThrowableDomain.any()
        return when (throwableHierarchy.facts(sourceType).throwableRelation) {
            TypeRelation.YES -> ThrowableDomain.constrainedTo(sourceType, throwableHierarchy)
            TypeRelation.NO -> ThrowableDomain.any()
            TypeRelation.UNKNOWN -> ThrowableDomain.unresolved(sourceType, throwableHierarchy)
        }
    }

    private fun materializeNonThrowable(value: Value.Known, frame: Frame): ThrownSubject =
        materializeRecipe(
            ThrowableRecipe.of(
                VirtualType.of("Ljava/lang/InternalError;"),
                "Throwing '${value.type}' that is not an instance of Throwable",
            ),
            frame,
        )

    private fun materializeRecipe(recipe: ThrowableRecipe, frame: Frame): ThrownSubject {
        val cause = recipe.cause?.value ?: Value.Known(null, throwableType)
        val hostCause = ((cause as? Value.Known)?.raw as? HeapRef)?.let(frame::hostObjectAt) as? Throwable
        val host = when (recipe.type.descriptor) {
            "Ljava/lang/ArithmeticException;" -> ArithmeticException(recipe.message)
            "Ljava/lang/ArrayIndexOutOfBoundsException;" -> ArrayIndexOutOfBoundsException(recipe.message)
            "Ljava/lang/ArrayStoreException;" -> ArrayStoreException(recipe.message)
            "Ljava/lang/ClassCastException;" -> ClassCastException(recipe.message)
            "Ljava/lang/ExceptionInInitializerError;" ->
                if (hostCause == null) ExceptionInInitializerError() else ExceptionInInitializerError(hostCause)
            "Ljava/lang/IncompatibleClassChangeError;" -> IncompatibleClassChangeError(recipe.message)
            "Ljava/lang/InternalError;" -> InternalError(recipe.message)
            "Ljava/lang/NegativeArraySizeException;" -> NegativeArraySizeException(recipe.message)
            "Ljava/lang/NoClassDefFoundError;" -> NoClassDefFoundError(recipe.message)
            "Ljava/lang/NoSuchFieldError;" -> NoSuchFieldError(recipe.message)
            "Ljava/lang/NoSuchMethodError;" -> NoSuchMethodError(recipe.message)
            "Ljava/lang/NullPointerException;" -> NullPointerException(recipe.message)
            else -> error("No throwable materializer exists for ${recipe.type}")
        }
        val value = frame.allocateThrowable(recipe.type, host, cause)
        val subject = ThrownSubject.fromHeap(value, frame, throwableHierarchy)
        committedThrowableAllocations++
        return subject
    }

    /** Plans local routes from the node's method and the event's normalized domain. */
    private fun planThrow(
        node: ExecutionNode,
        path: ThrowPath,
        throwAddress: Int = node.address,
    ): Committable {
        val resolution = try {
            resolveHandlers(throwAddress, path.subject, node.view, throwableHierarchy)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            return hostFailureArm(node, failure, path.frame)
        }
        if (resolution is HandlerResolution.MalformedMetadata) {
            return Committable(
                emptyList(),
                Ending.RunPath(Outcome.Aborted(AbortReason.MALFORMED_INPUT), path.frame),
            )
        }
        resolution as HandlerResolution.Routed
        // ALL_OR_NOTHING, stated HERE rather than inferred by the caller: a malformed try-block table
        // makes every clause in that table suspect, so routing part of it would leave a
        // Complete-looking run holding paths no consumer can account for. Because the policy travels
        // with the successor, this holds whatever else the op returned alongside the throw -- which is
        // exactly what a `transitions.size` proxy could not promise (see [OnUnbuildable]).
        return Committable(
            successors = resolution.routes.map { route ->
                SuccessorSpec(
                    route.handlerAddress,
                    node.activation,
                    OnUnbuildable.ALL_OR_NOTHING,
                    FrameOrigin.Continuation,
                ) { frame -> frame.setCaught(route.subject) }
            },
            ending = resolution.unhandled?.let {
                escapeEnding(node, ThrowPath.create(path.frame, it.subject, throwableHierarchy))
            },
        )
    }

    /** Ends an entry path or carries one residual into the existing caller completion. */
    private fun escapeEnding(
        node: ExecutionNode,
        path: ThrowPath,
    ): Ending {
        val subject = path.subject
        val exception = subject.value
        return if (node.activation.returnSite == null) {
            Ending.RunPath(Outcome.Threw(exception), path.frame)
        } else {
            Ending.ActivationOnly(
                Outcome.Threw(exception),
                path.frame,
                activationExit = ActivationExit.Threw(path),
            )
        }
    }

    private fun PreparedInvocation.opaqueArguments(): List<Value> =
        listOfNotNull(receiver) + arguments

    private fun PreparedInvocation.opaqueArgumentTypes(): List<VirtualType> =
        (if (receiver == null) emptyList() else listOf(target.definingClass)) + parameterTypes

    private fun OpArm.Invoke.opaqueArgumentTypes(valueCount: Int): List<VirtualType> {
        val expanded = buildList {
            if (kind != InvokeKind.STATIC) add(target.definingClass)
            for (type in target.parameterTypes()) {
                add(type)
                if (type.isWide) add(type)
            }
        }
        return List(valueCount) { index -> expanded.getOrElse(index) { VirtualType.OBJECT } }
    }

    /**
     * Schedules the single successor at [address] *within [parent]'s own method*, which is what makes
     * cross-method execution possible without a second loop (see [ExecutionNode.view]).
     *
     * `internal` rather than private so a test can schedule a node carrying a foreign [MethodView] and
     * observe that decode follows the node, not the session: no public API can build such a node, and
     * without that test all three node-scoped decode sites revert to `methodView` with a green suite.
     */
    internal fun enqueue(address: Int, parent: ExecutionNode) =
        commit(
            parent,
            // ALL_OR_NOTHING vs SKIP_ONE is unobservable for a lone successor in a lone group with no
            // ending -- both end up recording one `Aborted` on the parent, under the same reason, since
            // the reason comes from the failure's cause and not from the policy -- so this states the
            // stricter one rather than relying on that coincidence holding.
            listOf(
                Committable(
                    listOf(
                        SuccessorSpec(
                            address, parent.activation, OnUnbuildable.ALL_OR_NOTHING, FrameOrigin.Continuation,
                        ),
                    ),
                    null,
                ),
            ),
        )

    /**
     * Decodes [address] in [activation]'s method into a node. The method comes from the ACTIVATION -- i.e.
     * from the parent node or from the call that created it -- never from the session, which is what makes
     * cross-method execution possible without a second loop (see [ExecutionNode.view]).
     */
    private fun nodeAt(address: Int, activation: Activation, frame: Frame, parent: ExecutionNode?): ExecutionNode =
        ExecutionNode(address, opFactory(address, activation.view, resolver), frame, parent, activation)

    /**
     * Records a path ending: exactly one call per ended path, so [outcomes] has one entry per path.
     * [ExecutionNode.endWith] enforces the once-per-node half of that and is intentionally not
     * contained, so a scheduler bug that ends one path twice fails loudly instead of being converted
     * into yet another aborted path.
     */
    private fun record(node: ExecutionNode, outcome: Outcome, exactFrame: Frame = node.frame) {
        node.endWith(outcome, exactFrame)
        if (outcome is Outcome.Aborted && node.activation.callCompletion != null) {
            val completion = requireNotNull(node.activation.callCompletion)
            completion.replacePath(0, listOf(ActivationExit.Aborted(exactFrame, outcome.reason)))
            completion.drainIfComplete()?.let { routeOrEnqueueCallCompletion(node, node.activation, it) }
            return
        }
        recordedOutcomes.add(outcome)
        if (outcome is Outcome.Aborted) abortReasons.add(outcome.reason)
        accountForIncompleteCall(node, outcome)
    }

    /**
     * A path that ended inside a **callee** without returning means the enclosing call did not complete, so
     * every invoke node above it declares `STRONG`: an incomplete call is unanalyzable by definition, and
     * spec §2.2's direction for an unprovable side effect is `STRONG`.
     *
     * **Every incomplete ending, not only one that declared something.** The endings that reach here from a
     * non-entry activation are an address-visit or call-depth truncation, a step/time limit drain, a host
     * failure, and a `Transition.Abort`. A modeled guest escape uses activation completion and does not raise
     * effects by itself. A callee's own `return` does NOT reach here -- it is an
     * [Ending.ActivationOnly] that goes through `ExecutionNode.endWith` directly -- so this needs no
     * "did it return" test beyond the guard below, which exists only so a future caller cannot break that.
     *
     * Without this the caller's invoke address read `NONE`, i.e. *"no side effects, safe to delete"*, through
     * `ExecutionGraph.methodSideEffectLevelOverExploredPaths` -- an **ungated** query -- for a call this
     * engine could not run to completion. It was also a regression against the pre-invocation baseline, where
     * `planFor`'s no-op `Invoke` branch put the abort on the invoke node itself and `sideEffectLevel`'s
     * `UNSUPPORTED_OP -> STRONG` rule answered `STRONG`. That rule cannot fire now, because the abort is on a
     * callee node and `ExecutionGraph` is scoped to activations of its own method.
     */
    private fun accountForIncompleteCall(node: ExecutionNode, outcome: Outcome) {
        if (outcome is Outcome.Returned) return
        node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    /**
     * Piles [node] in **its own activation** (`CARRY-FORWARD.md` Plan 9 item 2). A bare address was only
     * unambiguous while one session walked one method; two activations of the same method are two program
     * points too, so this is keyed by activation IDENTITY rather than by method.
     */
    private fun addToPile(node: ExecutionNode) {
        node.activation.addressToNodes.getOrPut(node.address) { mutableListOf() }.add(node)
    }

    companion object {
        /**
         * Starts a run of [view]. [entryFrame] defaults to unknown parameters, except a constructor's
         * receiver is a fresh uninitialized object when its field layout is known. Explicit seeds are retained.
         * It is **copied**, so the caller keeps its own instance and may start several sessions from
         * one seeded frame.
         *
         * Internal construction for [VmServices.newSession], the only public session factory. It always
         * passes the real decoder. The op-injection seam is the overload below.
         */
        internal fun of(
            view: MethodView,
            vmServices: VmServices,
            limits: Limits = Limits(),
            entryFrame: Frame? = null,
        ): ExecutionSession {
            val resolver = capture(view, vmServices, entryFrame)
            return of(view, vmServices, resolver, limits, entryFrame)
        }

        internal fun of(
            view: MethodView,
            vmServices: VmServices,
            resolver: ProgramSnapshotResolver,
            limits: Limits = Limits(),
            entryFrame: Frame? = null,
        ): ExecutionSession = ExecutionSession(
            view,
            vmServices,
            limits,
            entryFrame ?: Frame.entryFrame(view, vmServices.hostObjectCloner, resolver.classIndex),
            resolver,
        )

        /** Convenience construction for internal callers that do not share per-VM registrations. */
        internal fun of(
            view: MethodView,
            classIndex: ClassIndex,
            limits: Limits = Limits(),
            entryFrame: Frame? = null,
        ): ExecutionSession = of(view, VmServices(classIndex), limits, entryFrame)

        /**
         * The op-injection seam: [of] plus the [opFactory] described on the constructor parameter of
         * the same name. `internal` so the seam is closed by the language rather than by a KDoc, and
         * [opFactory] deliberately has **no default** -- that is what makes an existing four-argument
         * call resolve unambiguously to the public overload above, and what stops this one from being a
         * seam-less second door into the scheduler.
         */
        internal fun of(
            view: MethodView,
            vmServices: VmServices,
            limits: Limits,
            entryFrame: Frame?,
            opFactory: (Int, MethodView, ClassIndex) -> Op,
        ): ExecutionSession {
            val resolver = capture(view, vmServices, entryFrame)
            return ExecutionSession(
                view,
                vmServices,
                limits,
                entryFrame ?: Frame.entryFrame(view, vmServices.hostObjectCloner, resolver.classIndex),
                resolver,
                { address, currentView, _ -> opFactory(address, currentView, resolver.classIndex) },
            )
        }

        internal fun of(
            view: MethodView,
            classIndex: ClassIndex,
            limits: Limits,
            entryFrame: Frame?,
            opFactory: (Int, MethodView, ClassIndex) -> Op,
        ): ExecutionSession = of(view, VmServices(classIndex), limits, entryFrame, opFactory)

        internal fun of(
            view: MethodView,
            vmServices: VmServices,
            limits: Limits,
            entryFrame: Frame?,
            opFactory: (Int, MethodView, ClassIndex) -> Op,
            invoker: Invoker,
        ): ExecutionSession {
            val resolver = capture(view, vmServices, entryFrame)
            return ExecutionSession(
                view,
                vmServices,
                limits,
                entryFrame ?: Frame.entryFrame(view, vmServices.hostObjectCloner, resolver.classIndex),
                resolver,
                { address, currentView, _ -> opFactory(address, currentView, resolver.classIndex) },
                invoker,
            )
        }

        internal fun of(
            view: MethodView,
            classIndex: ClassIndex,
            limits: Limits,
            entryFrame: Frame?,
            opFactory: (Int, MethodView, ClassIndex) -> Op,
            invoker: Invoker,
        ): ExecutionSession = of(view, VmServices(classIndex), limits, entryFrame, opFactory, invoker)

        private fun capture(
            view: MethodView,
            vmServices: VmServices,
            entryFrame: Frame?,
        ): ProgramSnapshotResolver {
            val snapshot = vmServices.classIndex.currentSnapshot
            require(snapshot.owns(view)) { "Method view is not current in this program workspace: ${view.signature}" }
            require(entryFrame == null || entryFrame.belongsTo(view)) {
                "Entry frame belongs to another program or method revision: ${view.signature}"
            }
            return vmServices.classIndex.resolver(snapshot)
        }
    }
}
