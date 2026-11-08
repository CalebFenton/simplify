package org.cf.smalivm.core

/**
 * One run's scheduler, and the only execution driver: the optimizer, the debugger, and the tests all advance
 * execution through [step]. Every mutable fact of a run lives here or in one of its [Activation]s, so runs are
 * independent and re-entrant.
 *
 * Execution is a depth-first walk of the unrolled tree: the frontier is LIFO, and an op returning several arms
 * forks, each successor getting its own [Frame] copy. Every fork funnels through [commit], the single place a node's
 * successors and ending are applied.
 *
 * The walk is deterministic: insertion-ordered maps and sets, a fixed arm order, and no clock read unless
 * [Limits.maxExecutionTimeMillis] is enabled. Two runs of the same input produce identical node orders and results.
 */
public class ExecutionSession internal constructor(
    public val methodView: MethodView,
    internal val vmServices: VmServices,
    private val limits: Limits,
    entryFrame: Frame,
    private val resolver: ProgramSnapshotResolver,
    /** How an address in a method becomes an [Op]; [VmServices.newSession] passes the real decoder. */
    private val decode: (Int, MethodView, ProgramSnapshotResolver) -> Op,
    private val invoker: Invoker,
    private val heapPressure: HeapPressure,
) {
    private val hierarchy: HierarchySnapshot = resolver.hierarchy
    private val frontierNodes = ArrayDeque<ExecutionNode>()
    private val pendingCallRoutings = ArrayDeque<PendingCallRouting>()

    /** The run's ended paths and truncation reasons: the entry activation's [PathSink]. */
    private val runOutcomes = RunOutcomes()
    private val callRouter = CallRouter(hierarchy, runOutcomes)

    /** The run's outermost [Activation]: the root of the call tree, and the one [graph] describes. */
    private val entryActivation =
        Activation.entry(methodView, runOutcomes, entryConstructorReceiver(methodView, entryFrame))

    /**
     * Every [Activation] this run has created, entry first, in creation order. [graph] unions the piles of every
     * activation of the entry method, and call-tree edges point only from callee to caller, so this list is the
     * only way to reach them.
     */
    private val activations = mutableListOf(entryActivation)

    private var steps = 0
    private var deadlineMillis = 0L

    /**
     * The [HeapPressure.collectionsOverThreshold] count this run has accounted for: the count when it started, so
     * a collection that ended before the run never stops it, then the count after each confirmation that found
     * the live data under the threshold.
     */
    private var heapPressureSeen = heapPressure.collectionsOverThreshold()

    /**
     * The node [step] most recently dequeued. After a limit abort it is the node the limit stopped, whose op never
     * ran and whose [ExecutionNode.frame] is therefore still pre-op.
     */
    internal var currentNode: ExecutionNode? = null
        private set

    /**
     * The call stack of the node [step] most recently dequeued, innermost activation first, or the entry activation
     * alone before anything has run. Per path: two scheduled paths can sit at different depths at once.
     */
    internal val callStack: CallStack get() = CallStack.of(currentNode?.activation ?: entryActivation)

    init {
        // A copy, so a caller that seeds one frame for several sessions never sees it mutated. A failure to decode
        // the entry point escapes construction: there is no explored path to abort yet.
        frontierNodes.addLast(nodeAt(0, entryActivation, entryFrame.copy(), parent = null))
    }

    /** Nodes scheduled but not yet executed, next one last (LIFO). */
    internal val frontier: List<ExecutionNode> get() = frontierNodes.toList()

    /** The next LIFO node without allocating the public frontier snapshot. */
    internal val nextNode: ExecutionNode? get() = frontierNodes.lastOrNull()

    /** A stable snapshot with one [Outcome] per path that ended: returned, threw, or aborted. */
    public val outcomes: List<Outcome> get() = runOutcomes.outcomes.toList()

    internal val isFinished: Boolean get() = frontierNodes.isEmpty() && pendingCallRoutings.isEmpty()

    /**
     * Whether this run explored everything it set out to, as of the moment it is asked. Any abort at any call
     * depth makes it [CompletionStatus.Partial], which outranks [CompletionStatus.InProgress] because truncation is
     * permanent: a status never improves with further stepping. Only a finished run is [CompletionStatus.Complete].
     */
    public fun completion(): CompletionStatus = when {
        runOutcomes.abortReasons.isNotEmpty() -> CompletionStatus.Partial(runOutcomes.abortReasons.toSet())
        !isFinished -> CompletionStatus.InProgress
        else -> CompletionStatus.Complete
    }

    /**
     * Advances execution by one node and returns it, or null when the run is finished.
     *
     * Every call into code this loop does not own -- an op's `execute`, a decode, a class-model lookup -- goes
     * through a host-failure boundary, so an ordinary host `Throwable` ends only that path as `HOST_FAILURE`. The
     * scheduler's own bookkeeping is outside that boundary: a bug here must surface, not become one more aborted
     * path. A guest exception is never a Kotlin throw; it arrives as an [OpArm.Throw].
     */
    internal fun step(): ExecutionNode? {
        if (limits.maxExecutionTimeMillis > 0 && deadlineMillis == 0L) {
            deadlineMillis = System.currentTimeMillis() + limits.maxExecutionTimeMillis
        }
        drainPendingCallRoutings()
        if (frontierNodes.isEmpty()) return null
        val node = frontierNodes.removeLast()
        currentNode = node

        // Piled before the limit check: a node a bound stops before its op runs still belongs in this address's
        // pile. [ExecutionNode.progress] says whether it ran.
        addToPile(node)

        if (exceededLimits(node)) {
            drainPendingCallRoutings()
            return node
        }

        // A VirtualMachineError must escape: converted, it would not drop the frontier, and the loop would re-fail
        // up to maxSteps.
        val execution = try {
            node.op.execute(node.frame)
        } catch (failure: Throwable) {
            val aborted = hostFailureOutcome(failure)
            node.executeFailed()
            end(node, ActivationExit.Aborted(node.frame, aborted))
            null
        }
        // A synchronized method's entry acquires a monitor.
        if (node.isCallEntry && node.view.isSynchronized) raiseEffect(node, SideEffectLevel.WEAK)
        if (execution == null) {
            drainPendingCallRoutings()
            return node
        }
        node.executeReturned()
        applyAll(execution, node)
        drainPendingCallRoutings()
        return node
    }

    /**
     * Turns an op's whole arm list into one [commit], so a fork cannot half-apply, end a node twice, or leak a
     * terminal slot write into a sibling's frame. Each arm is its own [Committable] group, which scopes an
     * [OnUnbuildable.ALL_OR_NOTHING] abort to the arm that declared it.
     *
     * A node ends at most one path, but its op may end several when every requested ending is a guest throw (an
     * `aget` ambiguous about both nullness and range): [commit] stamps the first on the node. A host failure while
     * preparing or planning one arm ends only that arm, so it never discards a feasible sibling.
     */
    private fun applyAll(execution: OpExecution, node: ExecutionNode) {
        val sourceFrame = node.frame
        if (execution.arms.size == 1) {
            val prepared = prepareArm(execution.arms.single(), sourceFrame, node)
            node.frame = prepared.frame
            val group = planArm(prepared, node)
            group.armFrame = prepared.frame
            commit(node, listOf(group))
            return
        }
        val prepared = execution.arms.map { prepareArm(it, sourceFrame, node) }
        val groups = ArrayList<Committable>(prepared.size)
        val enders = ArrayList<OpArm>(1)
        for (preparedArm in prepared) {
            node.frame = preparedArm.frame
            val arm = preparedArm.arm
            val part = planArm(preparedArm, node)
            part.armFrame = preparedArm.frame
            groups.add(part)
            if (part.ending == null || part.ending is Ending.ArmFailure) continue
            // The rule applies to what the op requested, not to routed outcomes, so it runs over the planned arms.
            require(enders.isEmpty() || (arm is OpArm.Throw && enders.all { it is OpArm.Throw })) {
                "Mixed path-ending transitions at ${node.address} in ${node.view.signature}: $enders and $arm. " +
                    "Several endings from one op are supported only when all of them are guest throws."
            }
            enders.add(arm)
        }
        val ready = prepared.filterIsInstance<PreparedOpArm.Ready>()
        // A failed preparation has its own exact terminal frame. It cannot contribute a trustworthy footprint or
        // state to the source summary, and including its invalid footprint can fail again.
        node.frame = when (ready.size) {
            0 -> sourceFrame
            1 -> ready.single().frame
            else -> {
                val footprint = sourceFrame.resolveFootprint(
                    ready.fold(OpUpdateFootprint.EMPTY) { combined, preparedArm ->
                        combined.combine(preparedArm.arm.reconciliationFootprint)
                    },
                )
                sourceFrame.reconcileOpArms(ready.map { it.frame }, footprint, hierarchy)
            }
        }
        commit(node, groups)
    }

    private fun prepareArm(arm: OpArm, sourceFrame: Frame, node: ExecutionNode): PreparedOpArm {
        val armFrame = sourceFrame.copy()
        return hostBoundary<PreparedOpArm>({ PreparedOpArm.Aborted(arm, armFrame, hostFailureOutcome(it)) }) {
            val applied = arm.update.applyTo(armFrame)
            applied.result?.let(armFrame::setResult)
            if (arm is OpArm.MoveFromSlot) {
                val moved = when (arm.source) {
                    SpecialSlotSource.RESULT -> armFrame.takeResult()
                    SpecialSlotSource.CAUGHT -> armFrame.takeCaught()
                }
                armFrame.assign(arm.register, moved ?: FrameValue.plain(Value.Top))
            }
            raiseEffect(node, applied.effect)
            val path = if (arm is OpArm.Throw) normalizeThrow(arm.evidence, armFrame, hierarchy) else null
            PreparedOpArm.Ready(arm, armFrame, path)
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

    /** A transition continuing [node]'s path at [address] of its own method, in a copy of the arm frame. */
    private fun continueAt(node: ExecutionNode, address: Int, seed: (Frame) -> Unit = {}): Committable =
        Committable(
            listOf(SuccessorSpec(address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation, seed)),
            null,
        )

    /**
     * Plans one arm, including each successor's own [OnUnbuildable] policy. Ordinary control flow is
     * [OnUnbuildable.SKIP_ONE]: a malformed jump or fall-through target is no reason to discard a feasible sibling.
     * Throw routing is [OnUnbuildable.ALL_OR_NOTHING] ([planThrow]).
     */
    private fun planArm(prepared: PreparedOpArm, node: ExecutionNode): Committable {
        if (prepared is PreparedOpArm.Aborted) {
            return Committable(emptyList(), Ending.ArmFailure(ActivationExit.Aborted(prepared.frame, prepared.outcome)))
        }
        prepared as PreparedOpArm.Ready
        return when (val arm = prepared.arm) {
            // [prepareArm] already applied the slot move to this arm's own frame, so siblings never see it.
            is OpArm.Continue, is OpArm.MoveFromSlot ->
                continueAt(node, node.view.nextAddress(node.address)).also { node.observeNormalContinuation() }
            is OpArm.Jump -> continueAt(node, arm.address).also { node.observeNormalContinuation() }
            is OpArm.Return ->
                Committable(emptyList(), Ending.Requested(ActivationExit.Returned(node.frame, arm.frameValue)))
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
        val unknown: (Frame) -> Value = { Value.Unknown(transition.field.type) }
        if (!hierarchy.isFrameworkClass(transition.field.definingClass)) {
            raiseEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjection(node, transition, unknown)
        }
        val initializationChain = try {
            resolver.hostInitializationChain(transition.field.definingClass)
        } catch (_: ClassLoadFailure) {
            raiseEffect(node, SideEffectLevel.STRONG)
            return@planContained staticProjection(node, transition, unknown)
        }
        val decision = vmServices.reflectionPolicy.decideStaticRead(field, initializationChain)
        raiseEffect(node, SideEffectLevel.STRONG)
        if (decision is ReflectionDecision.Denied) {
            node.invocationDiagnostic = "host static policy denied $field by ${decision.rule}"
            return@planContained staticProjection(node, transition, unknown)
        }
        when (val result = readHostStatic(resolver.classLoader, field)) {
            is HostFieldRead.Returned -> staticProjection(node, transition) { it.importHostValue(result.value) }
            is HostFieldRead.Unsupported -> {
                node.invocationDiagnostic = result.diagnostic
                staticProjection(node, transition, unknown)
            }
            is HostFieldRead.TargetThrew -> {
                val thrown = node.frame.importHostThrowable(result.throwable)
                planThrow(node, normalizeThrow(ThrowEvidence.Existing(thrown), node.frame, hierarchy))
            }
            is HostFieldRead.InfrastructureFailure -> hostFailureArm(node, result.cause)
        }
    }

    /** Continues after a static projection whose value [project] computes on the successor's own frame. */
    private fun staticProjection(
        node: ExecutionNode,
        transition: OpArm.ProjectStatic,
        project: (Frame) -> Value,
    ): Committable = continueAt(node, node.view.nextAddress(node.address)) { frame ->
        val value = project(frame)
        frame.classes.setHostProjection(transition.field.toString(), value)
        frame.assign(transition.register, value)
    }

    /**
     * Plans the class-init an op asked for: initialize this class, then run the op again at its own address.
     * Contained because [ClassInitPlanner] walks the superclass chain, and an ancestor absent from the program
     * raises [ClassLoadFailure], which must end the path and not the run.
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
        is ClassInitAction.ThrowFailed -> planFreshThrow(node, action.recipe)
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
        if (malformedInitializer != null) return abortedTransition(node.frame, AbortReason.MALFORMED_INPUT)
        dependencyFailure?.let { failure ->
            node.frame.applyClassInit(this)
            val path = normalizeThrow(ThrowEvidence.Fresh(failure.recipe), node.frame, hierarchy)
            node.frame.failClassInitDependency(failure, path.subject, hierarchy)
            return planThrow(node, path)
        }
        return initializerPrelude(node) ?: run {
            declareSkippedInitializerEffect(node, this)
            continueAt(node, node.address) { frame -> frame.applyClassInit(this) }
        }
    }

    /**
     * The nested `<clinit>` activation the plan calls for, or null when it calls for none. It is an ordinary nested
     * call on this session's frontier, so a `<clinit>`'s own throw consults its own try blocks. Its [ReturnSite]
     * resumes at the trigger's own address: initialization is a prelude to the triggering instruction, so the
     * trigger asks again and the next class down the chain starts.
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

    /**
     * Raises the triggering address to [SideEffectLevel.STRONG] when [plan] settled a class whose `<clinit>` the
     * analysis does not run. The activation chain is raised too, so a caller whose callee triggered an unmodelled
     * initializer does not report `NONE` for its own addresses.
     */
    private fun declareSkippedInitializerEffect(node: ExecutionNode, plan: ClassInitPlan) {
        if (plan.skippedAnInitializer) raiseEffect(node, SideEffectLevel.STRONG)
    }

    /**
     * Plans a call: [Invoker] prepares and selects it, then this session turns the result into successors.
     *
     * Contained because reading the argument registers (an obfuscated call site can name a register the caller does
     * not have) and resolving the target (the class model raises for input it cannot parse) can throw, and either
     * must abort only this path. A guest callee runs as a nested [Activation] on this session's frontier, so guest
     * recursion never recurses on the host stack.
     */
    private fun planInvoke(node: ExecutionNode, transition: OpArm.Invoke): Committable =
        planContained(node) {
            val frameArguments = transition.argumentRegisters.map { node.frame.readFrameValue(it) }
            // Resolution receives atomic carriers. Accepted host/emulator and opaque execution are explicit
            // conservative-forgetting boundaries; a selected guest call transports the carriers unchanged.
            val arguments = frameArguments.map(FrameValue::value)
            val call = Invocation.fromFrameValues(transition.target, transition.kind, frameArguments)
            val receiverType = receiverRuntimeType(transition.kind, arguments.firstOrNull(), node.frame)
            val preparation = when (val prepared = invoker.prepare(call, node.view.definingClass, receiverType)) {
                is InvocationPreparation.ThrowResolution -> return@planContained planFreshThrow(node, prepared.recipe)
                is OpaqueInvocation -> return@planContained planOpaque(
                    node,
                    transition,
                    prepared,
                    arguments,
                    transition.opaqueArgumentTypes(arguments.size),
                )
                is InvocationPreparation.Ready -> prepared
            }
            val invocation = preparation.invocation
            if (receiverIsNull(transition.kind, invocation.receiver)) {
                return@planContained planInvokeNullReceiver(node, transition)
            }
            preparation.invocationTimeError(receiverType)?.let { return@planContained planFreshThrow(node, it) }
            invoker.applicableClassInitTarget(invocation)?.let { type ->
                planClassInitTrigger(node, type)?.let { return@planContained it }
            }
            if (preparation.isSynchronized) raiseEffect(node, SideEffectLevel.WEAK)
            when (val plan = invoker.plan(preparation, node.frame)) {
                is InvocationPlan.RunGuest -> planGuestCall(node, invocation, plan)
                is InvocationPlan.Execute -> {
                    planAccessibilityFork(node, plan.invocation)?.let { return@planContained it }
                    node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
                    executeSynchronous(node, plan)
                }
                is OpaqueInvocation -> planOpaque(
                    node,
                    transition,
                    plan,
                    invocation.opaqueArguments(),
                    invocation.opaqueArgumentTypes(),
                )
            }
        }

    /**
     * Enters [plan]'s callee as a nested [Activation]. The resume address is computed here, in the caller's own
     * method, and carried on the callee's [ReturnSite] until its `return` needs it.
     */
    private fun planGuestCall(
        node: ExecutionNode,
        invocation: PreparedInvocation,
        plan: InvocationPlan.RunGuest,
    ): Committable {
        val receiverRef = (invocation.receiver as? Value.Known)?.raw as? HeapRef
        val activation = Activation(
            plan.view,
            node.activation,
            ReturnSite(
                node,
                node.view.nextAddress(node.address),
                completedReceiver(invocation, node),
                fallbackArguments = invocation.opaqueArguments(),
                fallbackArgumentTypes = invocation.opaqueArgumentTypes(),
                fallbackReturnType = invocation.returnType,
            ),
            constructing = receiverRef.takeIf { invocation.target.isConstructor },
        )
        activations.add(activation)
        return Committable(
            listOf(
                SuccessorSpec(plan.entryAddress, activation, OnUnbuildable.ALL_OR_NOTHING, FrameOrigin.CalleeEntry) {
                    frame -> plan.parameterBindings.forEach { frame.assign(it.register, it.frameValue) }
                },
            ),
            null,
        )
    }

    /** Records an accepted backend's effect, runs it on this path, then applies its direct result. */
    private fun executeSynchronous(node: ExecutionNode, plan: InvocationPlan.Execute): Committable {
        raiseEffect(node, plan.effect)
        val selected = hostBoundary({ return hostFailureArm(node, it) }) {
            plan.select(node.frame, resolver.classLoader, node.activation.suspendedCallerRoots())
        }
        val result = hostBoundary({ return failedSelectedCall(node, selected, it) }) { selected.execute() }
        if (result is SynchronousCallResult.TargetThrew && result.throwable.throwable is VirtualMachineError) {
            throw result.throwable.throwable
        }
        return when (result) {
            is SynchronousCallResult.Returned -> {
                val comparison = finalizeSelectedCall(node, selected)
                val value = when (val returned = result.value) {
                    HostReturn.Void -> null
                    is HostReturn.Value -> selected.importResult(node.frame, returned.value, comparison)
                }
                synchronousContinuation(node, value, completedReceiver(plan.invocation, node))
            }
            is SynchronousCallResult.TargetThrew -> {
                val comparison = finalizeSelectedCall(node, selected)
                val thrown = selected.importThrowable(node.frame, result.throwable, comparison)
                planThrow(node, normalizeThrow(ThrowEvidence.Existing(thrown), node.frame, hierarchy))
            }
            is SynchronousCallResult.NeedsClassInit -> {
                // The call re-executes once the class is initialized, so this attempt must leave no trace.
                selected.discard(node.frame)
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
                val operation = result.operation
                val completed = if (operation is IntrinsicOperation.InitializeEnumReceiver) {
                    completedReceiver(plan.invocation, node)
                } else {
                    null
                }
                synchronousContinuation(node, operation.applyTo(node.frame, plan.invocation), completed)
            }
            is ConstructedHostObjectResult -> {
                val ref = completedReceiver(plan.invocation, node) ?: return failedSelectedCall(
                    node,
                    selected,
                    IllegalArgumentException("Reflected constructor has no tracked receiver"),
                )
                hostBoundary({
                    raiseEffect(node, SideEffectLevel.STRONG)
                    return failedSelectedCall(node, selected, it)
                }) {
                    selected.installConstructedHostObject(node.frame, ref, result.instance)
                }
                finalizeSelectedCall(node, selected)
                synchronousContinuation(node, value = null, constructorReceiver = null)
            }
        }
    }

    /**
     * Finalizes [selected]'s host graph and raises the effect by what the call did to guest-visible host state: a
     * change is `WEAK`, like a guest field or array write, and a comparison the graphs cannot complete is `STRONG`.
     * One stopped by a host failure rethrows it, so the containment boundary ends the path as `HOST_FAILURE` with it.
     */
    private fun finalizeSelectedCall(node: ExecutionNode, selected: SelectedSynchronousCall): HostGraphComparison {
        val comparison = hostBoundary({
            raiseEffect(node, SideEffectLevel.STRONG)
            throw it
        }) {
            selected.finalize(node.frame)
        }
        when (comparison) {
            HostGraphComparison.Equal -> Unit
            HostGraphComparison.Different -> raiseEffect(node, SideEffectLevel.WEAK)
            is HostGraphComparison.Unprovable -> {
                raiseEffect(node, SideEffectLevel.STRONG)
                comparison.cause?.let { throw it }
            }
        }
        return comparison
    }

    /** Ends the path on [failure] after finalizing [selected]; a finalization failure is suppressed into [failure]. */
    private fun failedSelectedCall(
        node: ExecutionNode,
        selected: SelectedSynchronousCall,
        failure: Throwable,
    ): Committable {
        hostBoundary({ if (it !== failure) failure.addSuppressed(it) }) {
            finalizeSelectedCall(node, selected)
            Unit
        }
        return hostFailureArm(node, failure)
    }

    /** The object a call of [call] at [node] finishes constructing ([Activation.initializedReceiver]). */
    private fun completedReceiver(call: PreparedInvocation, node: ExecutionNode): HeapRef? =
        node.activation.initializedReceiver(call.target, call.receiver, node.frame, hierarchy)

    private fun synchronousContinuation(
        node: ExecutionNode,
        value: Value?,
        constructorReceiver: HeapRef?,
    ): Committable =
        continueAt(node, node.view.nextAddress(node.address)) { frame ->
            constructorReceiver?.let(frame.heap::markInitialized)
            value?.let(frame::setResult)
        }.also { node.observeNormalContinuation() }

    /** Applies an [OpaqueInvocation] whether binding or backend selection produced it. */
    private fun planOpaque(
        node: ExecutionNode,
        transition: OpArm.Invoke,
        opaque: OpaqueInvocation,
        arguments: List<Value>,
        argumentTypes: List<VirtualType>,
    ): Committable {
        val receiver = if (transition.kind == InvokeKind.STATIC) null else arguments.firstOrNull()
        if (receiverIsNull(transition.kind, receiver)) return planInvokeNullReceiver(node, transition)
        node.dispatchCandidates = opaque.candidates
        node.invocationDiagnostic = opaque.diagnostic
        raiseEffect(node, SideEffectLevel.STRONG)
        node.frame.forgetRefinements(transition.argumentRegisters.asIterable())
        val completed = node.activation.initializedReceiver(transition.target, receiver, node.frame, hierarchy)
        return Committable(
            listOf(
                opaqueCallSuccessor(
                    node.frame,
                    node.view.nextAddress(node.address),
                    node.activation,
                    arguments,
                    argumentTypes,
                    completed,
                    opaque.returnType,
                ),
            ),
            null,
        )
    }

    /**
     * Re-executes a call once per definite accessibility of the first [FieldAccessibility.UNKNOWN] tracked field
     * it passes, so a backend only ever reads a definite one. Null when the call passes none.
     */
    private fun planAccessibilityFork(node: ExecutionNode, invocation: PreparedInvocation): Committable? {
        val ref = invocation.opaqueArguments().firstNotNullOfOrNull { value ->
            ((value as? Value.Known)?.raw as? HeapRef)?.takeIf {
                node.frame.heap.trackedFieldAt(it)?.accessibility == FieldAccessibility.UNKNOWN
            }
        } ?: return null
        return Committable(
            listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN).map { accessibility ->
                SuccessorSpec(node.address, node.activation, OnUnbuildable.SKIP_ONE, FrameOrigin.Continuation) {
                    frame -> frame.heap.setTrackedFieldAccessibility(ref, accessibility)
                }
            },
            null,
        )
    }

    private fun planInvokeNullReceiver(node: ExecutionNode, transition: OpArm.Invoke): Committable =
        planFreshThrow(node, GuestThrowables.nullInvokeReceiver(transition.target, transition.kind))

    /**
     * The one place a node's successors and ending are committed, in two phases:
     *
     *  1. Build, mutating no frame and ending no path. Every successor's frame is copied from [node]'s post-op
     *     state before any terminal slot write, so the normal child of a `[Continue, Throw]` fork never carries
     *     `thrown`.
     *  2. Commit. Children attached, frontier pushed, node ended at most once. The first requested ending is
     *     stamped on the node; every ending goes to the activation's [PathSink] with the lost paths.
     *
     * An [OnUnbuildable] policy applies only within the [Committable] that declared it: an abandoned group
     * contributes no children, no ending, and one lost path, and every other group commits as if alone. Every lost
     * path carries its own [ActivationExit.Aborted] from [buildSuccessor], so an off-the-end jump stays
     * MALFORMED_INPUT and a decode bug stays HOST_FAILURE.
     */
    private fun commit(node: ExecutionNode, groups: List<Committable>) {
        // Every transition must go somewhere: a node with neither successor nor ending is a silently dropped path,
        // invisible to every ExecutionGraph query. A covering catch-all, the only thing that makes a throw's escape
        // infeasible, is itself a route, so no input reaches these; they guard the planners.
        require(groups.any { it.successors.isNotEmpty() || it.ending != null }) {
            "Op at ${node.address} in ${node.view.signature} would be committed nowhere"
        }
        require(groups.all { it.successors.isNotEmpty() || it.ending != null }) {
            "A transition of the op at ${node.address} in ${node.view.signature} would go nowhere"
        }

        // Phase 1: build, one group at a time.
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
                        OnUnbuildable.ALL_OR_NOTHING -> {
                            abandonedBecause = attempt.exit
                            break
                        }
                        OnUnbuildable.SKIP_ONE -> skipped.add(attempt.exit)
                    }
                }
            }
            if (abandonedBecause != null) {
                // One lost path for the whole group, with the failing spec's own reason; its ending goes too, since a
                // throw's unhandled residual comes from the same malformed table as its handlers. No group mixes
                // both policies, so `skipped` is absorbed.
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
        // Arm-local failures still end paths, but last, so a direct Return sibling keeps the node's terminal frame.
        endings.addAll(armFailures)

        if (built.isEmpty() && endings.isEmpty()) {
            // `lost` is not empty here. The first loss, in declared arm order, is the node's own ending.
            node.endWith(lost.first())
            replacePath(node, node.activation, 0, lost)
            return
        }

        // Phase 2: commit.
        schedule(node, built)
        // Slot writes happen after every successor frame was copied, so no sibling inherits `thrown`, and
        // `Frame.thrownOrNull() != null` means the node whose frame it is ended by throwing.
        for (exit in endings) {
            when (exit) {
                is ActivationExit.Returned -> exit.frame.setReturnValue(exit.frameValue)
                is ActivationExit.Threw -> exit.frame.setThrown(exit.exception)
                is ActivationExit.Aborted -> Unit
            }
        }
        endings.firstOrNull()?.let(node::endWith)
        replacePath(node, node.activation, built.size, endings + lost)
    }

    /** Attaches [built] under [parent] and pushes it so the first built node is explored first. */
    private fun schedule(parent: ExecutionNode, built: List<ExecutionNode>) {
        built.forEach(parent::addChild)
        built.asReversed().forEach(frontierNodes::addLast)
    }

    /**
     * Replaces one open path of [activation] with [openPaths] successors and the [ended] paths. The activation's
     * [PathSink] decides what an ended path is: a run outcome for the entry activation, an exit the caller resumes
     * from otherwise. A callee path that aborted leaves its call incomplete, so every enclosing invoke node becomes
     * `STRONG`.
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
            schedulerLimitReason()?.let {
                abortRun(null, it)
                return
            }
            val pending = pendingCallRoutings.removeLast()
            val exits = pending.batch.exitsSnapshot()
            try {
                resumeCaller(pending.anchor, pending.activation, exits)
            } finally {
                pending.batch.release()
            }
        }
    }

    /**
     * Replaces the suspended caller path of [activation]'s call with the continuations [CallRouter] plans for its
     * [exits]: every buildable resume, every throw group that builds whole, their escapes, and the aborts.
     */
    private fun resumeCaller(anchor: ExecutionNode, activation: Activation, exits: List<ActivationExit>) {
        val routing = callRouter.route(activation, exits)
        val built = mutableListOf<ExecutionNode>()
        val rejected = mutableListOf<ActivationExit.Aborted>()
        val escapes = mutableListOf<ActivationExit.Threw>()
        for (spec in routing.resumes) {
            when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                is SuccessorAttempt.Built -> built += attempt.node
                is SuccessorAttempt.Unbuildable -> rejected += attempt.exit
            }
        }
        groups@ for (group in routing.throwGroups) {
            val groupBuilt = mutableListOf<ExecutionNode>()
            for (spec in group.successors) {
                when (val attempt = buildSuccessor(spec, anchor, anchor.frame)) {
                    is SuccessorAttempt.Built -> groupBuilt += attempt.node
                    is SuccessorAttempt.Unbuildable -> {
                        rejected += ActivationExit.Aborted(group.abortFrame, attempt.exit.outcome)
                        continue@groups
                    }
                }
            }
            built += groupBuilt
            group.escape?.let(escapes::add)
        }
        schedule(anchor, built)
        replacePath(anchor, routing.caller, built.size, escapes + routing.aborts + rejected)
        if (rejected.isNotEmpty()) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    /**
     * What trying to turn one [SuccessorSpec] into a node produced: the node, or the lost path with its own
     * reason. A target the method does not contain is MALFORMED_INPUT; a construction that threw is HOST_FAILURE
     * carrying the throwable. The cause is decided here, where it is known, and only carried afterwards.
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
     * [spec] as a real node, or why it could not be built. [MethodView.hasAddress] is checked before decode, so a bad
     * address is detected rather than caught: no throwable is invented for a condition nothing threw, and the two
     * causes stay separable. The frame comes from [SuccessorSpec.frameOrigin] and is never an existing node's own
     * instance; the spec's seed writes only that new frame.
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
        hostBoundary({ hostFailureArm(node, it) }, body)

    private fun hostFailureArm(node: ExecutionNode, failure: Throwable): Committable =
        Committable(emptyList(), Ending.ArmFailure(ActivationExit.Aborted(node.frame, hostFailureOutcome(failure))))

    /** Raises [node]'s own effect, and its activation chain's declared effect, to at least [level]. */
    private fun raiseEffect(node: ExecutionNode, level: SideEffectLevel) {
        node.sideEffectLevel = maxOf(node.sideEffectLevel, level)
        node.activation.raiseDeclaredEffect(level)
    }

    /** Runs to completion and returns the immutable result. */
    public fun run(): ExecutionGraph {
        while (step() != null) Unit
        return graph()
    }

    /**
     * An immutable snapshot of the run so far. Safe to call mid-run: the snapshot carries [completion] as of now,
     * which is [CompletionStatus.InProgress] while any path is still scheduled, so [ExecutionGraph.trusted] is null
     * and no consumer can act on a walk that has not finished.
     */
    internal fun graph(): ExecutionGraph = ExecutionGraph(
        methodView = methodView,
        // A callee's nodes are in neither map. A recursive activation of this same method is in the first, because
        // reachability and per-address consensus must see every activation that executed an address, and excluded
        // from the second, because what this run returned is the entry activation's alone.
        addressToNodes = unionedPileSnapshot(),
        addressToNodesInThisRunsActivation = entryActivation.pileSnapshot(),
        outcomes = runOutcomes.outcomes,
        completion = completion(),
        superclasses = hierarchy,
    )

    /**
     * Every activation of the entry method's piles, merged by address, in activation creation order and then each
     * activation's piling order. Application activations match by captured program and method revision; framework
     * activations match by catalog-scoped framework identity.
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
     * A step, time or memory limit ends the whole run; an address-visit or call-depth limit ends only the offending
     * path, so sibling paths keep exploring. Address visits are checked first, then call depth at a callee's entry
     * only.
     */
    private fun exceededLimits(node: ExecutionNode): Boolean {
        schedulerLimitReason()?.let { return abortRun(node, it) }
        // Per path: sibling reconvergence is not a loop and cannot consume another path's budget.
        if (limits.maxAddressVisits > 0 && node.frame.recordAddressVisit(node.address) > limits.maxAddressVisits) {
            end(node, ActivationExit.Aborted(node.frame, Outcome.Aborted(AbortReason.ADDRESS_LIMIT)))
            return true
        }
        if (limits.maxCallDepth > 0 && node.isCallEntry && node.activation.frameCount > limits.maxCallDepth) {
            end(node, ActivationExit.Aborted(node.frame, Outcome.Aborted(AbortReason.CALL_DEPTH)))
            return true
        }
        return false
    }

    /**
     * Counts one guest node or one routing command as scheduler work. Routing records no address visit. Memory is
     * checked here rather than by catching [OutOfMemoryError], which can surface in any allocation, the JVM's own
     * included, and leaves no state worth trusting. A collection that reported pressure since the last check costs
     * one whole-heap collection to confirm; the run stops only when the heap's live data is over the threshold,
     * and its graph becomes garbage once the caller drops it.
     */
    private fun schedulerLimitReason(): AbortReason? {
        steps++
        if (limits.maxSteps > 0 && steps > limits.maxSteps) return AbortReason.METHOD_LIMIT
        if (deadlineMillis > 0 && System.currentTimeMillis() > deadlineMillis) return AbortReason.TIMEOUT
        if (heapPressure.collectionsOverThreshold() != heapPressureSeen) {
            if (heapPressure.liveDataOverThreshold()) return AbortReason.MEMORY_LIMIT
            heapPressureSeen = heapPressure.collectionsOverThreshold()
        }
        return null
    }

    /**
     * Ends the whole run on [reason]: the dequeued node plus every node still scheduled, each one aborted path, so
     * the outcomes never understate how much exploration was cut. Drained LIFO so the order is reproducible. Nodes
     * drained from the frontier are piled here, because none of them went through [step]'s piling. Exits a callee
     * collected but never routed are ended call paths with no node, so each is recorded too.
     */
    private fun abortRun(node: ExecutionNode?, reason: AbortReason): Boolean {
        node?.let { abortScheduledNode(it, reason) }
        while (frontierNodes.isNotEmpty()) {
            val abandoned = frontierNodes.removeLast()
            addToPile(abandoned)
            abortScheduledNode(abandoned, reason)
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

    private fun abortScheduledNode(node: ExecutionNode, reason: AbortReason) {
        val aborted = Outcome.Aborted(reason)
        if (node.outcome == null) node.endWith(ActivationExit.Aborted(node.frame, aborted))
        runOutcomes.record(aborted)
        node.activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
    }

    private fun planFreshThrow(node: ExecutionNode, recipe: ThrowableRecipe): Committable =
        planThrow(node, normalizeThrow(ThrowEvidence.Fresh(recipe), node.frame, hierarchy))

    /**
     * Routes [path] through [node]'s own handlers. Every route of one throw commits or none does: a malformed
     * handler address makes the whole table suspect, and a half-routed throw would leave paths no consumer can
     * account for.
     */
    private fun planThrow(node: ExecutionNode, path: ThrowPath, throwAddress: Int = node.address): Committable {
        val resolution = when (val result = resolveHandlers(throwAddress, path.subject, node.view, hierarchy)) {
            is HandlerResolution.Routed -> result
            HandlerResolution.MalformedMetadata -> return abortedTransition(path.frame, AbortReason.MALFORMED_INPUT)
        }
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
                Ending.Requested(ActivationExit.Threw(ThrowPath.create(path.frame, it.subject, hierarchy)))
            },
        )
    }

    /**
     * Decodes [address] in [activation]'s method into a node. The method comes from the activation, never from the
     * session, which is what lets one loop execute across methods.
     */
    private fun nodeAt(address: Int, activation: Activation, frame: Frame, parent: ExecutionNode?): ExecutionNode =
        ExecutionNode(address, decode(address, activation.view, resolver), frame, parent, activation)

    /** Piles [node] in its own activation: two activations of one method are two program points. */
    private fun addToPile(node: ExecutionNode) {
        node.activation.addressToNodes.getOrPut(node.address) { mutableListOf() }.add(node)
    }
}

/**
 * The host-failure boundary's conversion: an ordinary host throwable becomes `Aborted(HOST_FAILURE)` carrying it.
 * A [VirtualMachineError] is rethrown, because the JVM is out of memory or stack and calling that an aborted path
 * would not be true.
 */
internal fun hostFailureOutcome(failure: Throwable): Outcome.Aborted {
    if (failure is VirtualMachineError) throw failure
    return Outcome.Aborted(AbortReason.HOST_FAILURE, failure)
}

/** The object a constructor entry point is constructing, or null when [view] is not a constructor. */
private fun entryConstructorReceiver(view: MethodView, entryFrame: Frame): HeapRef? =
    if (view.isStatic || !view.signature.isConstructor) null
    else (entryFrame.peek(view.firstParameterRegister) as? Value.Known)?.raw as? HeapRef
