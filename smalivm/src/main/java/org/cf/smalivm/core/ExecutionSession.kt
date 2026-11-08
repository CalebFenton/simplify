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
    private val resolver: ProgramSnapshotResolver,
    /** How an address in a method becomes an [Op]; [VmServices.newSession] passes the real decoder. */
    private val decode: (Int, MethodView, ProgramSnapshotResolver) -> Op,
    private val invoker: Invoker,
) {
    private val throwableHierarchy: ThrowableHierarchy = resolver.hierarchy
    private val frontierNodes = ArrayDeque<ExecutionNode>()
    private val pendingCallRoutings = ArrayDeque<PendingCallRouting>()

    /** The run's ended paths and truncation reasons: the entry activation's [PathSink]. */
    private val runOutcomes = RunOutcomes()

    /**
     * The run's outermost [Activation]. Every node the session ever schedules belongs to this one or to an
     * activation reachable from it through [Activation.caller], so this is the root of the call tree as well
     * as the thing [graph] describes.
     */
    private val entryActivation = Activation.entry(methodView, runOutcomes)

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

    private var steps = 0
    private var deadlineMillis = 0L

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
    internal val callStack: CallStack get() = CallStack.of(currentNode?.activation ?: entryActivation)

    init {
        // entryFrame.copy(), never the caller's instance: a caller that seeds registers and then
        // starts two sessions (or reads its frame afterwards) must not have it mutated underneath.
        //
        // A failure to decode the entry point escapes construction rather than becoming an abort:
        // there is no explored path to abort yet, and a session that cannot decode its own first
        // instruction has nothing to run. Every failure from the first [step] onward is contained.
        frontierNodes.addLast(nodeAt(0, entryActivation, entryFrame.copy(), parent = null))
    }

    /** Nodes scheduled but not yet executed, next one last (LIFO). */
    internal val frontier: List<ExecutionNode> get() = frontierNodes.toList()

    /** The next LIFO node without allocating the public frontier snapshot. */
    internal val nextNode: ExecutionNode? get() = frontierNodes.lastOrNull()

    /** A stable snapshot with one [Outcome] per path that ended: returned, threw, or aborted. */
    val outcomes: List<Outcome> get() = runOutcomes.outcomes.toList()

    val isFinished: Boolean get() = frontierNodes.isEmpty() && pendingCallRoutings.isEmpty()

    /**
     * Whether this run explored everything it set out to, as of the moment it is asked (spec §5.1).
     *
     * Any abort -- limit, unsupported opcode, host failure, at any call depth -- makes it
     * [CompletionStatus.Partial], which gates consumer trust in consensus (spec §6.4). With nothing aborted, an
     * unfinished run is [CompletionStatus.InProgress] and only a finished one is [CompletionStatus.Complete], so a
     * mid-run [graph] snapshot never hands out [ExecutionGraph.trusted].
     *
     * Partial outranks InProgress because truncation is permanent: a status must never improve with further
     * stepping (`DebuggerTest.breakpointSnapshotsRemainUntrustedUntilCleanCompletionAndAfterAbort`).
     */
    fun completion(): CompletionStatus = when {
        runOutcomes.abortReasons.isNotEmpty() -> CompletionStatus.Partial(runOutcomes.abortReasons.toSet())
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
     * failure either -- it is a [Value] arriving as an [OpArm.Throw], never a Kotlin throw.
     */
    fun step(): ExecutionNode? {
        if (limits.maxExecutionTimeMillis > 0 && deadlineMillis == 0L) {
            deadlineMillis = System.currentTimeMillis() + limits.maxExecutionTimeMillis
        }
        drainPendingCallRoutings()
        if (frontierNodes.isEmpty()) return null
        val node = frontierNodes.removeLast()
        currentNode = node

        // Piled before the limit check: a node a bound stops before its op runs still belongs in this
        // address's pile, so a consumer can find it. [ExecutionNode.progress] says whether it ran.
        addToPile(node)

        if (exceededLimits(node)) {
            drainPendingCallRoutings()
            return node
        }

        // The host-failure boundary around foreign code (spec §7). It must go through hostFailureOutcome, which
        // rethrows a VirtualMachineError: converted, it would not drop the frontier, and the loop would re-fail
        // up to maxSteps. InvocationClassInitTest drives this boundary with a scripted StackOverflowError.
        val execution = try {
            node.op.execute(node.frame)
        } catch (failure: Throwable) {
            val aborted = hostFailureOutcome(failure)
            node.executeFailed()
            end(node, ActivationExit.Aborted(node.frame, aborted))
            null
        }
        // A synchronized method's entry acquires a monitor. The activation records it as well, because
        // `ExecutionGraph` describes one activation and the drained callee's declared effect moves onto the
        // caller's invoke node (spec §4.5: an effect is a statement about ONE address).
        if (node.isCallEntry && node.view.isSynchronized) {
            node.sideEffectLevel = maxOf(node.sideEffectLevel, SideEffectLevel.WEAK)
            node.activation.raiseDeclaredEffect(SideEffectLevel.WEAK)
        }
        if (execution == null) {
            drainPendingCallRoutings()
            return node
        }
        node.executeReturned()
        // All transitions commit together, so a fork cannot half-apply, end a node twice, or leak a terminal
        // slot write into a sibling's frame. Public outcome order is not a semantic contract.
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
     * fires it may end SEVERAL: [commit] stamps the first ending on the node and records the others without
     * a node -- allowed only when every requested ending is a guest throw (an `aget` ambiguous about both
     * nullness and range). A host failure created while preparing or planning one arm is separate from this
     * check because it must not discard a feasible sibling.
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
            // One op may end several paths only when every ending is a guest throw: an `aget` that can prove
            // neither non-null nor in-range raises two exceptions from one read. The node stamps one and
            // `commit` records the rest as endings with no node. Any other mix, such as a Return beside a
            // Throw, is two different outcomes and stays an error. `enders.isEmpty()` lets a solo ending of
            // any kind through; the rule applies to what the op requested, not to routed outcomes.
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
                val footprint = sourceFrame.resolveFootprint(
                    ready.fold(OpUpdateFootprint.EMPTY) { combined, preparedArm ->
                        combined.combine(preparedArm.arm.reconciliationFootprint)
                    },
                )
                node.frame = sourceFrame.reconcileOpArms(ready.map { it.frame }, footprint, resolver.hierarchy)
            }
        }
        // The mixed-ending check above is about what the OP asked for, so it runs over the planned
        // transitions here and not over whatever survives building. A group abandoned by ALL_OR_NOTHING
        // drops its own ending too (see [commit]), which can only reduce the count.
        commit(node, groups)
    }

    private fun prepareArm(arm: OpArm, sourceFrame: Frame, node: ExecutionNode): PreparedOpArm {
        val armFrame = sourceFrame.copy()
        return try {
            val applied = arm.update.applyTo(armFrame)
            applied.result?.let(armFrame::setResult)
            if (arm is OpArm.MoveFromSlot) {
                when (arm.source) {
                    SpecialSlotSource.RESULT -> armFrame.assign(
                        arm.register,
                        armFrame.takeResult() ?: FrameValue.plain(Value.Top),
                    )
                    SpecialSlotSource.CAUGHT -> armFrame.assign(
                        arm.register,
                        armFrame.takeCaught() ?: FrameValue.plain(Value.Top),
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
            PreparedOpArm.Aborted(arm, armFrame, hostFailureOutcome(failure))
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
     * A path one transition ends. Whether it is a run outcome or a callee exit is decided by the ending
     * activation's [PathSink], not by the planner.
     */
    private sealed interface Ending {
        val exit: ActivationExit

        /** A path the op's own transition ended. */
        class Requested(override val exit: ActivationExit) : Ending

        /** A host failure confined to one op arm, so the other arms of the same execution still commit. */
        class ArmFailure(override val exit: ActivationExit.Aborted) : Ending
    }

    /** A transition that ends its path on [reason], in [frame]. */
    private fun abortedTransition(frame: Frame, reason: AbortReason): Committable =
        Committable(emptyList(), Ending.Requested(ActivationExit.Aborted(frame, Outcome.Aborted(reason))))

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
        is PreparedOpArm.Aborted ->
            Committable(emptyList(), Ending.ArmFailure(ActivationExit.Aborted(prepared.frame, prepared.outcome)))
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
        ).also { node.observeNormalContinuation() }
        // `OnUnbuildable.SKIP_ONE`: a malformed fall-through target is no reason to discard a feasible
        // exceptional sibling. An arm-local register write already landed on this arm's own frame in
        // [prepareArm], so siblings never see it.
        is OpArm.MoveFromSlot -> Committable(
            listOf(
                SuccessorSpec(
                    node.view.nextAddress(node.address), node.activation,
                    OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                ),
            ),
            null,
        ).also { node.observeNormalContinuation() }
        is OpArm.Jump -> Committable(
            listOf(
                SuccessorSpec(arm.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation),
            ),
            null,
        ).also { node.observeNormalContinuation() }
        is OpArm.Return -> planReturn(node, arm.frameValue)
        is OpArm.Throw -> planThrow(
            node,
            requireNotNull(prepared.throwPath),
            throwAddress = when (arm.lookupPc) {
                ExceptionPc.AT_INSTRUCTION -> node.address
                ExceptionPc.AFTER_INSTRUCTION -> node.view.nextAddress(node.address)
            },
        ).also { node.observeExceptionalContinuation() }
        is OpArm.Abort -> abortedTransition(node.frame, arm.reason).also { node.observeExceptionalContinuation() }
        is OpArm.Invoke -> planInvoke(node, arm)
        is OpArm.InitClass -> planInitClass(node, arm.type)
        is OpArm.ProjectStatic -> planStaticProjection(node, arm)
    }

    /** Authorizes one framework static read, reads the host field, and imports the result. */
    private fun planStaticProjection(
        node: ExecutionNode,
        transition: OpArm.ProjectStatic,
    ): Committable = planContained(node) {
        val field = transition.field
        val cell = node.frame.classes.staticCell(field.toString())
        require(cell?.provenance == StaticCellProvenance.SEEDED_DEFAULT && cell.value is Value.Unknown) {
            "Static projection requested for a cell that is not an unresolved prepared seed: $field ($cell)"
        }
        if (!resolver.hierarchy.isFrameworkClass(transition.field.definingClass)) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.field.type))
        }
        val initializationChain = try {
            resolver.hostInitializationChain(transition.field.definingClass)
        } catch (_: ClassLoadFailure) {
            raiseSynchronousEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.field.type))
        }
        val decision = vmServices.reflectionPolicy.decideStaticRead(field, initializationChain)
        raiseSynchronousEffect(node, SideEffectLevel.STRONG)
        if (decision is ReflectionDecision.Denied) {
            node.invocationDiagnostic = "host static policy denied $field by ${decision.rule}"
            return@planContained staticProjectionContinuation(node, transition, Value.Unknown(transition.field.type))
        }
        when (val result = readHostStatic(resolver.classLoader, field)) {
            is HostFieldRead.Returned -> staticProjectionContinuation(node, transition, result.value)
            is HostFieldRead.Unsupported -> {
                node.invocationDiagnostic = result.diagnostic
                staticProjectionContinuation(node, transition, Value.Unknown(transition.field.type))
            }
            is HostFieldRead.TargetThrew -> planThrow(node, normalizeThrow(result.throwable, node.frame))
            is HostFieldRead.InfrastructureFailure -> hostFailureArm(node, result.cause)
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
                frame.classes.setHostProjection(transition.field.toString(), value)
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
                frame.classes.setHostProjection(transition.field.toString(), value)
                frame.assign(transition.register, value)
            },
        ),
        null,
    )

    /**
     * Plans the class-init an op asked for: initialize this class, then run the op again at its own address.
     *
     * Inside [planContained] because [ClassInitPlanner] walks the superclass chain, and
     * [TypeHierarchy.superclassOf] raises [ClassLoadFailure] for an ancestor absent from the loaded set, which must
     * end the path and not the run.
     */
    private fun planInitClass(node: ExecutionNode, type: VirtualType): Committable =
        planContained(node) {
            require(node.frame.classInitAction(type) == ClassInitAction.StartInitialization) {
                "InitClass requires StartInitialization for ${type.descriptor}"
            }
            val plan = invoker.planClassInit(node.frame, type)
            // An op emits InitClass only for StartInitialization, so a no-op plan is a defect; tolerating it would
            // re-execute the trigger forever.
            require(!plan.isNoOp) {
                "The op at ${node.address} in ${node.view.signature} asked to initialize ${type.descriptor}, " +
                    "which needs nothing done (${node.frame.initState(type)})."
            }
            plan.classInitPreludeOrRetry(node)
        }

    /**
     * Applies the path's [ClassInitAction] for an invoke or emulator trigger of [type]: null to proceed, the failed
     * class's error, or the initialization work. A plan that settles everything is applied in place unless
     * [retryWhenSettled] asks for the trigger to run again on a successor.
     */
    private fun planClassInitTrigger(
        node: ExecutionNode,
        type: VirtualType,
        retryWhenSettled: Boolean = false,
    ): Committable? = when (val action = node.frame.classInitAction(type)) {
        ClassInitAction.Proceed -> null
        is ClassInitAction.ThrowFailed -> planThrow(
            node,
            normalizeThrow(ThrowEvidence.Fresh(action.recipe), node.frame),
        )
        ClassInitAction.StartInitialization -> {
            val plan = invoker.planClassInit(node.frame, type)
            require(!plan.isNoOp) { "Class-init request for ${type.descriptor} produced no work" }
            when {
                plan.malformedInitializer != null || plan.dependencyFailure != null ->
                    plan.classInitPreludeOrRetry(node)
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

    /** Ends a malformed path, fails on a failed ancestor, schedules an initializer, or applies settled marks. */
    private fun ClassInitPlan.classInitPreludeOrRetry(node: ExecutionNode): Committable {
        if (malformedInitializer != null) {
            return abortedTransition(node.frame, AbortReason.MALFORMED_INPUT)
        }
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
     * The nested `<clinit>` activation the plan calls for, or null when it calls for none. It is an ordinary
     * nested call on this session's frontier, so a `<clinit>`'s own throw consults its own try blocks. Its
     * [ReturnSite] resumes at the trigger's own address: initialization is a prelude to the triggering
     * instruction, so the trigger asks again and the next class down the chain starts.
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
        val path = normalizeThrow(ThrowEvidence.Fresh(failure.recipe), node.frame)
        node.frame.failClassInitDependency(failure, path.subject, throwableHierarchy)
        return planThrow(node, path)
    }

    /**
     * Raises the triggering address to [SideEffectLevel.STRONG] when [plan] settled a class whose `<clinit>`
     * this build declined to run -- resolved decision 7's framework rule.
     *
     * Written on the node directly because no op arm carries it: the plan is made here, after every arm's
     * update was applied. The activation chain is raised too, exactly as [prepareArm] does for an arm's
     * declared effect, so a CALLER whose callee triggered an unmodelled initializer does not report `NONE`
     * for its own addresses.
     */
    private fun declareSkippedInitializerEffect(node: ExecutionNode, plan: ClassInitPlan) {
        if (!plan.skippedAnInitializer) return
        if (SideEffectLevel.STRONG > node.sideEffectLevel) node.sideEffectLevel = SideEffectLevel.STRONG
        node.activation.raiseDeclaredEffect(SideEffectLevel.STRONG)
    }

    /**
     * Plans a call: [Invoker] prepares and selects it, then this session turns the result into successors.
     *
     * Runs inside [planContained] because reading the argument registers (an obfuscated call site can name a
     * register the caller does not have) and resolving the target (the class model raises for input it cannot
     * parse) can throw, and either must abort only this path.
     *
     * The callee runs as a nested [Activation] on this session's frontier, so `step()` can single-step into it
     * and guest recursion never recurses on the host stack. Its entry uses [OnUnbuildable.ALL_OR_NOTHING]: a
     * call has exactly one successor, so the stricter policy costs nothing.
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
            val preparation = when (val prepared = invoker.prepare(call, node.view.definingClass, receiverType)) {
                is InvocationPreparation.ThrowResolution -> return@planContained planThrow(
                    node,
                    normalizeThrow(ThrowEvidence.Fresh(prepared.recipe), node.frame),
                )
                is OpaqueInvocation -> return@planContained planOpaque(
                    node,
                    transition,
                    prepared,
                    arguments,
                    transition.opaqueArgumentTypes(arguments.size),
                    receiverType,
                )
                is InvocationPreparation.Ready -> prepared
            }
            if (receiverIsNull(transition.kind, preparation.invocation.receiver)) {
                return@planContained planInvokeNullReceiver(node, transition)
            }
            // ART's `ThrowInvocationTimeError`: a linked method that cannot be invoked throws after the null check.
            val invocationTimeError = when (val lookup = preparation.lookup) {
                is MethodLookup.DefaultConflict ->
                    GuestThrowables.conflictingDefaultMethods(preparation.invocation.target)
                is MethodLookup.AbstractSelection -> GuestThrowables.abstractMethod(lookup.method.target, receiverType)
                else -> null
            }
            if (invocationTimeError != null) {
                val evidence = ThrowEvidence.Fresh(invocationTimeError)
                return@planContained planThrow(node, normalizeThrow(evidence, node.frame))
            }
            val initType = invoker.applicableClassInitTarget(preparation.invocation)
            if (initType != null) {
                planClassInitTrigger(node, initType)?.let { return@planContained it }
            }
            if (preparation.isSynchronized) {
                raiseSynchronousEffect(node, SideEffectLevel.WEAK)
            }
            when (
                val plan = invoker.plan(preparation, node.frame)
            ) {
                is InvocationPlan.RunGuest -> {
                    val guestPlan = plan
                    val receiver = preparation.invocation.receiver
                    // The resume address is computed HERE, from the CALLER's own node in the CALLER's own
                    // method -- the only `nextAddress` shape decision 5 permits -- and carried on the
                    // callee's activation until its `return` needs it.
                    val constructorReceiver = if (
                        transition.target.isConstructor && receiverType == plan.view.definingClass
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
                                    frame.assign(it.register, it.frameValue)
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
                is OpaqueInvocation -> planOpaque(
                    node,
                    transition,
                    plan,
                    preparation.invocation.opaqueArguments(),
                    preparation.invocation.opaqueArgumentTypes(),
                    receiverType,
                )
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
                // Only initialization re-executes the call, so only that answer needs host state left unchanged.
                if (
                    comparison != HostGraphComparison.Equal &&
                    node.frame.classInitAction(result.type) == ClassInitAction.StartInitialization
                ) {
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
            addAll(returnSite.invokeNode.frame.guestRootValues())
            current = current.caller ?: break
        }
    }

    /**
     * Finalizes [selected]'s host graph. A comparison the graphs cannot complete raises the effect to `STRONG`; one
     * stopped by a host failure rethrows it, so the containment boundary ends the path as `HOST_FAILURE` with it.
     */
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
            is HostGraphComparison.Unprovable -> {
                raiseSynchronousEffect(node, SideEffectLevel.STRONG)
                comparison.cause?.let { throw it }
            }
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
            val source = node.frame.heap.arrayAt(ref)
                ?: error("Tracked-array clone receiver $ref is not an array")
            val clone = node.frame.allocateArray(source.type, source.elements().toTypedArray())
            synchronousContinuation(node, clone, constructorReceiver = null)
        }
        is IntrinsicOperation.InitializeEnumReceiver -> {
            val ref = (plan.invocation.receiver as? Value.Known)?.raw as? HeapRef
                ?: error("Enum initialization has no known receiver ref")
            require(
                node.frame.heap.setField(
                    ref,
                    "Ljava/lang/Enum;->name:Ljava/lang/String;",
                    operation.name.toSchedulerValue(VirtualType.of("Ljava/lang/String;")),
                ),
            ) { "Enum initialization receiver $ref has no name field" }
            require(
                node.frame.heap.setField(
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
            synchronousContinuation(node, fieldGetResult(value, operation.field.fieldType), constructorReceiver = null)
        }
        IntrinsicOperation.ReadThrowableCause -> {
            val receiver = plan.invocation.receiver
            val causeType = GuestThrowables.THROWABLE
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
        is IntrinsicOperation.SetTrackedFieldAccessible -> {
            val ref = (plan.invocation.receiver as? Value.Known)?.raw as? HeapRef
                ?: error("Field.setAccessible has no known tracked receiver ref")
            val current = node.frame.heap.trackedFieldAt(ref)
                ?: error("Field.setAccessible receiver $ref is not a tracked field")
            require(current.metadata == operation.metadata) {
                "Field.setAccessible metadata changed between selection and application"
            }
            require(node.frame.heap.setTrackedFieldAccessibility(ref, operation.accessibility)) {
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
        if (!call.target.isConstructor) return null
        val ref = (call.receiver as? Value.Known)?.raw as? HeapRef ?: return null
        val runtimeType = node.frame.heapObject(ref)?.type ?: return null
        if (!resolver.hierarchy.isSubtypeOf(runtimeType, call.target.definingClass)) return null
        val enclosingConstructor = node.view.signature.isConstructor
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
                    constructorReceiver?.let(frame.heap::markInitialized)
                    value?.let(frame::setResult)
                },
            ),
            null,
        ).also { node.observeNormalContinuation() }
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
                        Value.Known(frame.heap.importHostNode(sourceNode, value.type, frame.heap.hostObjectAt(sourceNode)!!), value.type)
                    }
                    else -> {
                        val nodeId = retainedSourceNodeId(frame, selected, raw)
                        when {
                            nodeId != null -> Value.Known(frame.heap.importHostNode(nodeId, value.type, raw), value.type)
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
            } ?: Value.Known(null, GuestThrowables.THROWABLE)
            frame.heap.setThrowableCause(ref, cause)
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
                    val retained = requireNotNull(frame.heap.hostObjectAt(sourceNode))
                    return frame.heap.importHostNode(sourceNode, type, retained)
                }
            }
            retainedSourceNodeId(frame, selected, raw)?.let { nodeId ->
                return frame.heap.importHostNode(nodeId, type, raw)
            }
        }
        frame.heap.findHostObject(raw)?.let { return it }
        frame.heap.hostNodeId(raw)?.let { nodeId ->
            return frame.heap.importHostNode(nodeId, type, raw)
        }
        return frame.allocateThrowable(type, raw).raw as HeapRef
    }

    private fun retainedSourceNodeId(
        frame: Frame,
        selected: SelectedSynchronousCall,
        raw: Any,
    ): HostGraphNodeId? {
        val source = selected.sourceNodeId(raw)
        if (source != null && frame.heap.hostObjectAt(source) === raw) return source
        return frame.heap.hostNodeId(raw)
    }

    private fun retainedSourceRef(
        frame: Frame,
        selected: SelectedSynchronousCall,
        raw: Any,
        comparison: HostGraphComparison,
    ): HeapRef? {
        val sourceRef = selected.sourceHeapRef(raw) ?: return null
        val nodeId = (frame.heapObject(sourceRef) as? HostBacked)?.knownNodeId ?: return null
        if (frame.heap.refForNode(nodeId) != sourceRef) return null
        val currentRaw = frame.heap.hostObjectAt(nodeId) ?: return null
        if (currentRaw === raw) return sourceRef
        val sourceNode = selected.sourceNodeId(raw)
        return if (comparison == HostGraphComparison.Equal && sourceNode == nodeId) sourceRef else null
    }

    /** Applies an [OpaqueInvocation] whether binding or backend selection produced it. */
    private fun planOpaque(
        node: ExecutionNode,
        transition: OpArm.Invoke,
        opaque: OpaqueInvocation,
        arguments: List<Value>,
        argumentTypes: List<VirtualType>,
        receiverType: VirtualType?,
    ): Committable {
        if (receiverIsNull(transition.kind, arguments.firstOrNull())) return planInvokeNullReceiver(node, transition)
        node.dispatchCandidates = opaque.candidates
        node.invocationDiagnostic = opaque.diagnostic
        node.sideEffectLevel = maxOf(node.sideEffectLevel, SideEffectLevel.STRONG)
        node.activation.raiseDeclaredEffect(SideEffectLevel.STRONG)
        node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
        return opaqueContinuation(node, transition, arguments, argumentTypes, receiverType, opaque.returnType)
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
            transition.target.isConstructor && receiverType == transition.target.definingClass
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
            result.heap.setTrackedFieldAccessibility(ref, FieldAccessibility.UNKNOWN)
        }
        result.heap.collapseReachable(arguments, argumentTypes)
        constructorReceiver?.let(result.heap::markInitialized)
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
                node.frame.heap.trackedFieldAt(it)?.accessibility == FieldAccessibility.UNKNOWN
            }
        } ?: return null
        return Committable(
            listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN).map { accessibility ->
                SuccessorSpec(
                    node.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation,
                ) { frame -> frame.heap.setTrackedFieldAccessibility(ref, accessibility) }
            },
            null,
        )
    }

    private fun planInvokeNullReceiver(node: ExecutionNode, transition: OpArm.Invoke): Committable =
        planThrow(
            node,
            normalizeThrow(
                ThrowEvidence.Fresh(GuestThrowables.nullInvokeReceiver(transition.target, transition.kind)),
                node.frame,
            ),
        )

    /**
     * Plans a `return`. The value goes into the resumed caller frame's `result` slot, where the following
     * `move-result*` reads it ([Frame.takeResult]). The callee's declared side effects are raised onto the
     * invoke node when its activation drains, so `ExploredPaths.sideEffectLevel` reports them at the address
     * that caused them (see [Activation.declaredEffect]).
     *
     * The returning node carries [Outcome.Returned] either way. In the entry activation that is a run outcome;
     * in a callee it is an exit that resumes the caller once the activation drains, where compatible return
     * states merge and then expand back to one caller continuation each, so duplicate path results remain
     * observable.
     */
    private fun planReturn(node: ExecutionNode, value: FrameValue?): Committable =
        Committable(emptyList(), Ending.Requested(ActivationExit.Returned(node.frame, value)))

    /**
     * The ONE place a node's successors and ending are committed. Two phases, and the split is the
     * whole point:
     *
     *  1. **Build: mutates no frame and ends no path.** Every successor's frame is copied from [node]'s
     *     post-op state BEFORE any terminal slot write below. Without this ordering a `[Continue,
     *     Throw]` fork hands the normal-path child a frame already carrying `thrown` -- a path that did
     *     not throw looking like one that did, which is a wrong answer rather than a loud failure.
     *     ([buildSuccessor] can rethrow a [VirtualMachineError] out of this phase; that ends no path.)
     *  2. **Commit.** Children attached, frontier pushed, node ended AT MOST ONCE. The op may still have
     *     asked for SEVERAL endings (every one a guest throw -- [applyAll] enforces that); the first is
     *     stamped on the node, and every ending goes to the activation's [PathSink] with the lost paths.
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
     * **Every lost path carries its own [ActivationExit.Aborted] from [buildSuccessor]**, so an off-the-end
     * jump stays MALFORMED_INPUT and a decode bug stays HOST_FAILURE with its throwable. `OnUnbuildable`
     * decides how much is abandoned, never whose fault the loss was.
     *
     * Groups stay separate: with one flat successor list, an [OnUnbuildable.ALL_OR_NOTHING] failure would
     * discard successors already built for an [OnUnbuildable.SKIP_ONE] group, such as the fall-through child
     * of a `[Continue, Throw]` whose handler table is malformed (spec §2.2: more forks, never fewer).
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
        val endings = ArrayList<ActivationExit>(1)
        val armFailures = ArrayList<ActivationExit>(0)
        val lost = ArrayList<ActivationExit.Aborted>(0)
        for (group in groups) {
            val fromGroup = ArrayList<ExecutionNode>(group.successors.size)
            var abandonedBecause: ActivationExit.Aborted? = null
            val skipped = ArrayList<ActivationExit.Aborted>(0)
            for (spec in group.successors) {
                when (val attempt = buildSuccessor(spec, node, group.armFrame ?: node.frame)) {
                    is SuccessorAttempt.Built -> fromGroup.add(attempt.node)
                    is SuccessorAttempt.Unbuildable -> when (spec.onUnbuildable) {
                        // Stop building this group: everything it would route is suspect, including
                        // specs already built above, which are dropped with it.
                        OnUnbuildable.ALL_OR_NOTHING -> {
                            abandonedBecause = attempt.exit
                            break
                        }
                        OnUnbuildable.SKIP_ONE -> skipped.add(attempt.exit)
                    }
                }
            }
            if (abandonedBecause != null) {
                // One lost path for the whole abandoned group, with the failing spec's own reason: the group's
                // routing is one indivisible decision, so a malformed 2-clause table reports ONE abort
                // (`ThrowRoutingTest.oneMalformedHandlerAbortsTheWholeForkRatherThanCommittingHalfOfIt`).
                // `skipped` is absorbed into it; no group mixes both policies (`Continue`/`Jump` groups are
                // single-spec SKIP_ONE, `Throw` groups uniformly ALL_OR_NOTHING).
                //
                // The group's ending is dropped too. For a throw the unhandled residual comes from the same
                // malformed table as the handlers, so it is exactly as suspect (see [planThrow]).
                lost.add(abandonedBecause)
                continue
            }
            built.addAll(fromGroup)
            lost.addAll(skipped)
            when (val ending = group.ending) {
                is Ending.ArmFailure -> armFailures.add(ending.exit)
                is Ending.Requested -> endings.add(ending.exit)
                null -> Unit
            }
        }
        // Prefer an op-requested ending for the node itself. Arm-local failures still end paths, but placing
        // them last lets a direct Return sibling keep the node's terminal frame.
        endings.addAll(armFailures)

        if (built.isEmpty() && endings.isEmpty()) {
            // `lost` is not empty: the `require`s above forbid a group offering neither a successor nor an
            // ending. The first loss, in the op's declared transition order, is the node's own ending; the
            // others are still one ended path each.
            node.endWith(lost.first())
            replacePath(node, node.activation, 0, lost)
            return
        }

        // --- phase 2: commit ---
        built.forEach { node.addChild(it) }
        built.asReversed().forEach { frontierNodes.addLast(it) }
        // Slot writes happen after every successor frame was copied, so no sibling inherits `thrown`, and
        // `Frame.thrownOrNull() != null` means the node whose frame it is ended by throwing. A handler gets
        // the thrown value through its own `caught` seed, never by inheritance.
        for (exit in endings) {
            when (exit) {
                is ActivationExit.Returned -> exit.frame.setReturnValue(exit.frameValue)
                is ActivationExit.Threw -> exit.frame.setThrown(exit.exception)
                is ActivationExit.Aborted -> Unit
            }
        }
        // A node ends at most once, so it carries the first ending; the others end paths with no node.
        endings.firstOrNull()?.let(node::endWith)
        replacePath(node, node.activation, built.size, endings + lost)
    }

    /**
     * Replaces one open path of [activation] with [openPaths] successors and the [ended] paths. The
     * activation's [PathSink] decides what an ended path is: a run outcome for the entry activation, an exit
     * the caller resumes from otherwise. A callee path that aborted leaves its call incomplete, so every
     * enclosing invoke node becomes `STRONG` (spec §2.2: an unprovable effect errs upward).
     */
    private fun replacePath(
        anchor: ExecutionNode,
        activation: Activation,
        openPaths: Int,
        ended: List<ActivationExit>,
    ) {
        if (ended.any { it is ActivationExit.Aborted }) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
        // A drained callee is routed from the step loop rather than recursively through the host stack.
        activation.sink.replacePath(openPaths, ended)?.let { batch ->
            pendingCallRoutings.addLast(PendingCallRouting(anchor, activation, batch))
        }
    }

    /** Ends [node]'s own path, and only that path, with [exit]. */
    private fun end(node: ExecutionNode, exit: ActivationExit) {
        node.endWith(exit)
        replacePath(node, node.activation, 0, listOf(exit))
    }

    private class PendingCallRouting(
        val anchor: ExecutionNode,
        val activation: Activation,
        val batch: CompletionBatch,
    )

    /** Runs completion-routing commands without exposing them as executable nodes. */
    private fun drainPendingCallRoutings() {
        while (pendingCallRoutings.isNotEmpty()) {
            val limit = schedulerLimitReason()
            if (limit != null) {
                abortRun(null, limit)
                return
            }
            val pending = pendingCallRoutings.removeLast()
            val exits = pending.batch.exitsSnapshot()
            try {
                routeCallCompletion(pending.anchor, pending.activation, exits)
            } finally {
                pending.batch.release()
            }
        }
    }

    /**
     * Applies the class-init owner's initializer-exit rule ([failInitializer]) before the trigger's caller performs
     * handler lookup. A model failure while transforming one exit aborts only that exit, so an independent return
     * or throw from the same initializer survives.
     */
    private fun transformInitializerThrows(
        context: ClassInitContext,
        exits: List<ActivationExit.Threw>,
    ): List<ActivationExit> = buildList {
        for (exit in exits) {
            val transformed = try {
                context.failInitializer(exit, throwableHierarchy)
            } catch (failure: Throwable) {
                add(ActivationExit.Aborted(exit.frame, hostFailureOutcome(failure)))
                continue
            }
            addAll(transformed)
        }
    }

    /** Synthetic outcome fork: one normal child and one child per caller handler partition. */
    private fun routeCallCompletion(anchor: ExecutionNode, activation: Activation, exits: List<ActivationExit>) {
        val site = requireNotNull(activation.returnSite)
        val caller = requireNotNull(activation.caller)
        site.invokeNode.sideEffectLevel = maxOf(site.invokeNode.sideEffectLevel, activation.declaredEffect)
        val returned = exits.filterIsInstance<ActivationExit.Returned>()
        var threw = exits.filterIsInstance<ActivationExit.Threw>()
        var aborted = exits.filterIsInstance<ActivationExit.Aborted>()
        aborted.forEach { runOutcomes.markTruncated(it.outcome.reason) }
        val classInitContext = activation.classInitContext
        if (classInitContext == null) {
            if (returned.isNotEmpty()) site.invokeNode.observeNormalContinuation()
            if (threw.isNotEmpty() || aborted.isNotEmpty()) {
                site.invokeNode.observeExceptionalContinuation()
            }
        }

        // An ordinary abort makes only that outcome partition opaque. Return and throw siblings remain exact.
        if (classInitContext == null && aborted.isNotEmpty()) {
            site.invokeNode.sideEffectLevel = SideEffectLevel.STRONG
            activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
        }

        if (classInitContext != null && threw.isNotEmpty()) {
            val transformed = transformInitializerThrows(classInitContext, threw)
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
                    site.constructorReceiver?.let(frame.heap::markInitialized)
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
                ActivationExit.Aborted(
                    site.invokeNode.frame.mergedCalleeState(listOf(exit.frame), resolver.hierarchy),
                    exit.outcome,
                )
            }
        }
        val throwGroups = mutableListOf<CallerThrowGroup>()
        val keyedExits = threw.map { exit -> KeyedCallerThrow(exit, callerThrowSourceKey(exit)) }
        val sourcePartitions = keyedExits.groupBy(KeyedCallerThrow::key)
        val sourceRoutings = LinkedHashMap<CallerThrowSourceKey, CallerSourceRouting>()
        for ((key, keyedPartition) in sourcePartitions) {
            val callerFrame = mergeCallPartition(site, keyedPartition.map { it.exit.frame })
            val mergedSubject = mergedCallerThrowSubject(key, callerFrame, keyedPartition.map { it.exit })
            // The invoke node is suspended, so a contained failure carries one abort token per original exit into
            // this completion instead of ending the node.
            sourceRoutings[key] = when (
                val resolution = resolveHandlers(
                    site.invokeNode.address,
                    mergedSubject,
                    site.invokeNode.view,
                    throwableHierarchy,
                )
            ) {
                is HandlerResolution.Routed -> CallerSourceRouting.Routed(callerFrame, resolution)
                HandlerResolution.MalformedMetadata -> {
                    activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
                    CallerSourceRouting.Aborted(
                        ActivationExit.Aborted(callerFrame, Outcome.Aborted(AbortReason.MALFORMED_INPUT)),
                    )
                }
                is HandlerResolution.HostFailure -> {
                    val exit = ActivationExit.Aborted(callerFrame, hostFailureOutcome(resolution.failure))
                    activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
                    CallerSourceRouting.Aborted(exit)
                }
            }
        }
        for ((_, key) in keyedExits) {
            when (val routing = requireNotNull(sourceRoutings[key])) {
                is CallerSourceRouting.Aborted -> routingAborts += routing.exit
                is CallerSourceRouting.Routed -> {
                    val routeSpecs = routing.resolution.routes.map { route ->
                        SuccessorSpec(
                            route.handlerAddress,
                            caller,
                            OnUnbuildable.ALL_OR_NOTHING,
                            FrameOrigin.CallerPartition(routing.frame),
                        ) { frame -> frame.setCaught(route.subject.frameValue) }
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
            routingAborts,
            throwGroups,
        )
        if (rejected) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    private fun mergeCallPartition(site: ReturnSite, frames: List<Frame>): Frame =
        site.invokeNode.frame.mergedCalleeState(frames, resolver.hierarchy)

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
                subject.domain.hashCode(),
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
            routingDomain?.hashCode() ?: 0,
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

    private sealed interface CallerSourceRouting {
        class Aborted(val exit: ActivationExit.Aborted) : CallerSourceRouting
        class Routed(val frame: Frame, val resolution: HandlerResolution.Routed) : CallerSourceRouting
    }

    private class CallerThrowGroup(
        val abortFrame: Frame,
        val successors: List<SuccessorSpec>,
        val escape: ActivationExit.Threw?,
    )

    private class SyntheticSchedule(
        val built: Int,
        val rejected: List<ActivationExit.Aborted>,
        val escapes: List<ActivationExit.Threw>,
    )

    private fun scheduleSyntheticPartitions(
        anchor: ExecutionNode,
        specs: List<SuccessorSpec>,
        throwGroups: List<CallerThrowGroup>,
    ): SyntheticSchedule {
        val built = mutableListOf<ExecutionNode>()
        val rejected = mutableListOf<ActivationExit.Aborted>()
        val escapes = mutableListOf<ActivationExit.Threw>()
        for (spec in specs) {
            when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                is SuccessorAttempt.Built -> built += attempt.node
                is SuccessorAttempt.Unbuildable -> rejected += attempt.exit
            }
        }
        for (group in throwGroups) {
            val groupBuilt = mutableListOf<ExecutionNode>()
            var rejection: ActivationExit.Aborted? = null
            for (spec in group.successors) {
                when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                    is SuccessorAttempt.Built -> groupBuilt += attempt.node
                    is SuccessorAttempt.Unbuildable -> {
                        rejection = ActivationExit.Aborted(group.abortFrame, attempt.exit.outcome)
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

    /**
     * Replaces one suspended caller path with the successors that were actually built, plus its escapes and
     * aborts. True when any partition aborted.
     */
    private fun installSyntheticPartitions(
        anchor: ExecutionNode,
        caller: Activation,
        specs: List<SuccessorSpec>,
        routingAborts: List<ActivationExit.Aborted>,
        throwGroups: List<CallerThrowGroup>,
    ): Boolean {
        val scheduled = scheduleSyntheticPartitions(anchor, specs, throwGroups)
        replacePath(anchor, caller, scheduled.built, scheduled.escapes + routingAborts + scheduled.rejected)
        return scheduled.rejected.isNotEmpty() || routingAborts.isNotEmpty()
    }

    /**
     * What trying to turn one [SuccessorSpec] into a node produced: the node, or the lost path with its own
     * reason. A target the method does not contain is MALFORMED_INPUT; a construction that threw is
     * HOST_FAILURE carrying the throwable. Those are opposite claims about whose fault the loss is, so the
     * cause is decided here, where it is known, and only carried afterwards.
     */
    private sealed interface SuccessorAttempt {
        /** Built. Scheduled unless a sibling's [Unbuildable] abandons the whole group -- see [commit]. */
        class Built(val node: ExecutionNode) : SuccessorAttempt

        /**
         * Not built. [exit]'s frame is the exact copied, seeded successor state: result/caught slots and
         * constructor completion belong to this lost path and must reach an enclosing opaque fallback.
         */
        class Unbuildable(val exit: ActivationExit.Aborted) : SuccessorAttempt
    }

    /**
     * [spec] as a real node, or **why** it could not be built ([SuccessorAttempt]).
     * [MethodView.hasAddress] is checked before decode because decode does a `requireNotNull`: a bad
     * address must be *detected*, not caught, so no throwable is invented for a condition nothing
     * threw -- and that ordering is also what makes the two causes separable at all, since a caught
     * `IllegalArgumentException` from `requireNotNull` looks exactly like any other host failure.
     * Construction is foreign code, so its failure goes through [hostFailureOutcome] for the
     * [VirtualMachineError] rethrow. Where the lost path is recorded is the caller's decision.
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
                val malformed = Outcome.Aborted(AbortReason.MALFORMED_INPUT)
                SuccessorAttempt.Unbuildable(ActivationExit.Aborted(seeded, malformed))
            } else {
                SuccessorAttempt.Built(nodeAt(spec.address, spec.activation, seeded, node))
            }
        } catch (t: Throwable) {
            SuccessorAttempt.Unbuildable(ActivationExit.Aborted(frame ?: armFrame.copy(), hostFailureOutcome(t)))
        }
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
        Committable(emptyList(), Ending.ArmFailure(ActivationExit.Aborted(frame, hostFailureOutcome(failure))))

    /**
     * The boundary's conversion policy: an ordinary host throwable becomes `Aborted(HOST_FAILURE)` carrying it.
     * A [VirtualMachineError] is rethrown: the JVM is out of memory or stack, calling that an aborted path would
     * not be true, and because a host failure does not drop the frontier the loop would otherwise re-fail up to
     * `maxSteps`.
     */
    private fun hostFailureOutcome(failure: Throwable): Outcome.Aborted {
        if (failure is VirtualMachineError) throw failure
        return Outcome.Aborted(AbortReason.HOST_FAILURE, failure)
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
        outcomes = runOutcomes.outcomes,
        completion = completion(),
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
                end(node, ActivationExit.Aborted(node.frame, Outcome.Aborted(AbortReason.ADDRESS_LIMIT)))
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
            end(node, ActivationExit.Aborted(node.frame, Outcome.Aborted(AbortReason.CALL_DEPTH)))
            return true
        }
        return false
    }

    /** Counts one guest node or one routing command as scheduler work. Routing records no address visit. */
    private fun schedulerLimitReason(): AbortReason? {
        steps++
        if (limits.maxSteps > 0 && steps > limits.maxSteps) return AbortReason.METHOD_LIMIT
        if (deadlineMillis > 0 && System.currentTimeMillis() > deadlineMillis) return AbortReason.TIMEOUT
        return null
    }

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
            repeat(pending.batch.retainedFrames) { runOutcomes.record(Outcome.Aborted(reason)) }
            pending.batch.release()
        }
        for (activation in activations) {
            val completion = activation.sink as? CallCompletion ?: continue
            if (completion.isDrained) continue
            repeat(completion.cancelAndRelease().size) { runOutcomes.record(Outcome.Aborted(reason)) }
        }
        runOutcomes.markTruncated(reason)
        return true
    }

    private fun abortScheduledNode(node: ExecutionNode, reason: AbortReason, pile: Boolean) {
        if (pile) addToPile(node)
        val aborted = Outcome.Aborted(reason)
        if (node.outcome == null) node.endWith(ActivationExit.Aborted(node.frame, aborted))
        runOutcomes.record(aborted)
        node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    /** Materializes or imports one committed throw event exactly once on its arm-owned frame. */
    private fun normalizeThrow(evidence: ThrowEvidence, armFrame: Frame): ThrowPath {
        val subject = when (evidence) {
            is ThrowEvidence.Fresh -> evidence.recipe.materialize(armFrame, throwableHierarchy)
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
            return GuestThrowables.nullPointer().materialize(frame, throwableHierarchy)
        }
        val raw = value.raw
        if (raw is Throwable) {
            val imported = importHostThrowable(frame, HostThrowable(virtualTypeOfHostClass(raw.javaClass), raw))
            return ThrownSubject.fromHeap(imported, frame, throwableHierarchy)
        }
        if (raw !is HeapRef) return materializeNonThrowable(value, frame)
        val obj = frame.heapObject(raw) ?: return materializeNonThrowable(value, frame)
        if (obj is HeapObject.HostObject) {
            val host = frame.heap.hostObjectAt(raw)
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
        GuestThrowables.internalError("Throwing '${value.type}' that is not an instance of Throwable")
            .materialize(frame, throwableHierarchy)

    /** Plans local routes from the node's method and the event's normalized domain. */
    private fun planThrow(
        node: ExecutionNode,
        path: ThrowPath,
        throwAddress: Int = node.address,
    ): Committable {
        val resolution = when (val result = resolveHandlers(throwAddress, path.subject, node.view, throwableHierarchy)) {
            is HandlerResolution.Routed -> result
            HandlerResolution.MalformedMetadata -> return abortedTransition(path.frame, AbortReason.MALFORMED_INPUT)
            is HandlerResolution.HostFailure -> return hostFailureArm(node, result.failure, path.frame)
        }
        // Every route of one throw commits or none does: a malformed handler address makes the whole table
        // suspect, and a half-routed throw would leave paths no consumer can account for.
        return Committable(
            successors = resolution.routes.map { route ->
                SuccessorSpec(
                    route.handlerAddress,
                    node.activation,
                    OnUnbuildable.ALL_OR_NOTHING,
                    FrameOrigin.Continuation,
                ) { frame -> frame.setCaught(route.subject.frameValue) }
            },
            ending = resolution.unhandled?.let {
                Ending.Requested(ActivationExit.Threw(ThrowPath.create(path.frame, it.subject, throwableHierarchy)))
            },
        )
    }

    private fun PreparedInvocation.opaqueArguments(): List<Value> =
        listOfNotNull(receiver) + arguments

    private fun PreparedInvocation.opaqueArgumentTypes(): List<VirtualType> =
        (if (receiver == null) emptyList() else listOf(target.definingClass)) + parameterTypes

    private fun OpArm.Invoke.opaqueArgumentTypes(valueCount: Int): List<VirtualType> {
        val words = target.argumentWords(kind)
        return List(valueCount) { word -> words.typeAt(word) ?: VirtualType.OBJECT }
    }

    /**
     * Decodes [address] in [activation]'s method into a node. The method comes from the ACTIVATION -- i.e.
     * from the parent node or from the call that created it -- never from the session, which is what makes
     * cross-method execution possible without a second loop (see [ExecutionNode.view]).
     */
    private fun nodeAt(address: Int, activation: Activation, frame: Frame, parent: ExecutionNode?): ExecutionNode =
        ExecutionNode(address, decode(address, activation.view, resolver), frame, parent, activation)

    /**
     * Piles [node] in **its own activation** (`CARRY-FORWARD.md` Plan 9 item 2). A bare address was only
     * unambiguous while one session walked one method; two activations of the same method are two program
     * points too, so this is keyed by activation IDENTITY rather than by method.
     */
    private fun addToPile(node: ExecutionNode) {
        node.activation.addressToNodes.getOrPut(node.address) { mutableListOf() }.add(node)
    }

}
