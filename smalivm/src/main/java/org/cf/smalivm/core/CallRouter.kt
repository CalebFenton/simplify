package org.cf.smalivm.core

/**
 * Turns a drained callee's exits into the caller's continuations: one resumed successor per return, an opaque
 * continuation per aborted partition, and the caller's handler routes for each thrown partition. Exits whose caller
 * states are compatible merge into one caller frame first, then expand back to one successor per exit, so duplicate
 * path results stay observable. A reflective call's exits first pass through its [ReturnSite.reflectiveReturn]:
 * results are boxed and throws leave wrapped, so the caller's handlers see what `Method.invoke` throws.
 *
 * Writes only effect levels, continuation observations, and run truncation; the session builds and schedules what
 * [route] returns.
 */
internal class CallRouter(
    private val hierarchy: HierarchySnapshot,
    private val runOutcomes: RunOutcomes,
) {
    /** The caller continuations of [activation]'s drained [exits]. */
    fun route(activation: Activation, exits: List<ActivationExit>): CallRouting {
        val site = requireNotNull(activation.returnSite)
        val caller = requireNotNull(activation.caller)
        val invokeNode = site.invokeNode
        invokeNode.sideEffectLevel = maxOf(invokeNode.sideEffectLevel, activation.declaredEffect)
        val returned = exits.filterIsInstance<ActivationExit.Returned>()
        var threw = exits.filterIsInstance<ActivationExit.Threw>()
        var aborted = exits.filterIsInstance<ActivationExit.Aborted>()
        val reflective = site.reflectiveReturn
        if (reflective != null && threw.isNotEmpty()) {
            val wrapped = reflective.wrapThrows(threw, hierarchy)
            threw = wrapped.filterIsInstance<ActivationExit.Threw>()
            aborted += wrapped.filterIsInstance<ActivationExit.Aborted>()
        }
        aborted.forEach { runOutcomes.markTruncated(it.outcome.reason) }
        // An aborted callee path leaves its call incomplete; so does a routing abort below.
        if (aborted.isNotEmpty()) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
        val classInitContext = activation.classInitContext
        if (classInitContext == null) {
            if (returned.isNotEmpty()) invokeNode.observeNormalContinuation()
            if (threw.isNotEmpty() || aborted.isNotEmpty()) invokeNode.observeExceptionalContinuation()
        } else if (threw.isNotEmpty()) {
            val transformed = transformInitializerThrows(classInitContext, threw)
            threw = transformed.filterIsInstance<ActivationExit.Threw>()
            aborted += transformed.filterIsInstance<ActivationExit.Aborted>()
        }

        val resumes = mutableListOf<SuccessorSpec>()
        if (returned.isNotEmpty()) {
            data class ReturnPartition(val state: CallCompatibilityKey, val frameValue: FrameValue?)

            val merged = returned.groupBy { ReturnPartition(it.frame.callCompatibilityKey(), it.frameValue) }
                .mapValues { (_, paths) ->
                    mergeIntoCaller(site, paths.map { it.frame }).also { frame ->
                        classInitContext?.let(frame::completeClassInit)
                    }
                }
            for (exit in returned) {
                val partition = ReturnPartition(exit.frame.callCompatibilityKey(), exit.frameValue)
                resumes += SuccessorSpec(
                    site.resumeAddress,
                    caller,
                    OnUnbuildable.SKIP_ONE,
                    FrameOrigin.CallerPartition(requireNotNull(merged[partition])),
                ) { frame ->
                    if (reflective == null) exit.frameValue?.let(frame::setResult)
                    else frame.setResult(reflective.result(exit.frameValue))
                    site.constructorReceiver?.let(frame.heap::markInitialized)
                }
            }
        }

        val aborts = mutableListOf<ActivationExit.Aborted>()
        if (classInitContext == null) {
            // An ordinary abort makes only its own partition opaque; return and throw siblings stay exact.
            val opaque = aborted.groupBy { it.frame.callCompatibilityKey() }.mapValues { (_, partition) ->
                opaqueCallSuccessor(
                    mergeIntoCaller(site, partition.map { it.frame }),
                    site.resumeAddress,
                    caller,
                    requireNotNull(site.fallbackArguments),
                    requireNotNull(site.fallbackArgumentTypes),
                    site.constructorReceiver,
                    requireNotNull(site.fallbackReturnType),
                )
            }
            aborted.mapTo(resumes) { requireNotNull(opaque[it.frame.callCompatibilityKey()]) }
        } else {
            aborted.mapTo(aborts) { ActivationExit.Aborted(mergeIntoCaller(site, listOf(it.frame)), it.outcome) }
        }

        val keyed = threw.map { KeyedCallerThrow(it, callerThrowSourceKey(it)) }
        val routings = LinkedHashMap<CallerThrowSourceKey, CallerSourceRouting>()
        for ((key, partition) in keyed.groupBy(KeyedCallerThrow::key)) {
            val callerFrame = mergeIntoCaller(site, partition.map { it.exit.frame })
            val subject = mergedCallerThrowSubject(key, callerFrame, partition.map { it.exit })
            // The invoke node is suspended, so malformed handler metadata aborts each original exit in this
            // completion instead of ending the node.
            val resolution = resolveHandlers(invokeNode.address, subject, invokeNode.view, hierarchy)
            routings[key] = when (resolution) {
                is HandlerResolution.Routed -> CallerSourceRouting.Routed(callerFrame, resolution)
                HandlerResolution.MalformedMetadata -> CallerSourceRouting.Aborted(
                    ActivationExit.Aborted(callerFrame, Outcome.Aborted(AbortReason.MALFORMED_INPUT)),
                )
            }
        }
        val throwGroups = mutableListOf<CallerThrowGroup>()
        for ((_, key) in keyed) {
            when (val routing = requireNotNull(routings[key])) {
                is CallerSourceRouting.Aborted -> aborts += routing.exit
                is CallerSourceRouting.Routed -> throwGroups += CallerThrowGroup(
                    routing.frame,
                    routing.resolution.routes.map { route ->
                        SuccessorSpec(
                            route.handlerAddress,
                            caller,
                            OnUnbuildable.ALL_OR_NOTHING,
                            FrameOrigin.CallerPartition(routing.frame),
                        ) { frame -> frame.setCaught(route.subject.frameValue) }
                    },
                    routing.resolution.unhandled?.let {
                        ActivationExit.Threw(ThrowPath.create(routing.frame, it.subject, hierarchy))
                    },
                )
            }
        }
        if (aborts.isNotEmpty()) activation.raiseCallSiteEffect(SideEffectLevel.STRONG)
        return CallRouting(caller, resumes, throwGroups, aborts)
    }

    private fun mergeIntoCaller(site: ReturnSite, frames: List<Frame>): Frame =
        site.invokeNode.frame.mergedCalleeState(frames, hierarchy)

    /**
     * Applies the class-init owner's initializer-exit rule ([ClassInitContext.failInitializer]) before the trigger's
     * caller looks up handlers. A model failure while transforming one exit aborts only that exit.
     */
    private fun transformInitializerThrows(
        context: ClassInitContext,
        exits: List<ActivationExit.Threw>,
    ): List<ActivationExit> = buildList {
        for (exit in exits) {
            hostBoundary({ add(ActivationExit.Aborted(exit.frame, hostFailureOutcome(it))) }) {
                addAll(context.failInitializer(exit, hierarchy))
            }
        }
    }

    /** Pre-resolution source identity of a thrown exit. Handler addresses are deliberately absent. */
    private sealed interface CallerThrowSourceKey {
        val state: CallCompatibilityKey

        class Concrete(
            val frameValue: FrameValue.RefinedThrowable,
            val routingDomain: ThrowableDomain?,
            override val state: CallCompatibilityKey,
        ) : CallerThrowSourceKey {
            val value = checkNotNull(frameValue.value as? Value.Known) {
                "A concrete caller throw key must hold a known value"
            }
            private val cachedHash = 31 * (31 * value.hashCode() + (routingDomain?.hashCode() ?: 0)) + state.hashCode()

            override fun equals(other: Any?): Boolean = this === other || other is Concrete &&
                value == other.value &&
                routingDomain == other.routingDomain &&
                state == other.state

            override fun hashCode(): Int = cachedHash
        }

        class Symbolic(val domain: ThrowableDomain, override val state: CallCompatibilityKey) : CallerThrowSourceKey {
            private val cachedHash = 31 * domain.hashCode() + state.hashCode()

            override fun equals(other: Any?): Boolean = this === other || other is Symbolic &&
                domain == other.domain && state == other.state

            override fun hashCode(): Int = cachedHash
        }
    }

    private fun callerThrowSourceKey(exit: ActivationExit.Threw): CallerThrowSourceKey {
        val subject = exit.subject
        if (subject.concreteRef == null) {
            return CallerThrowSourceKey.Symbolic(subject.domain, exit.frame.callCompatibilityKey())
        }
        // An exact runtime type decides every present class relation. Only unresolved required or excluded roots can
        // distinguish routes for one concrete ref, so an equivalent concrete upper-bound refinement is the same source.
        val routingDomain = subject.domain.takeIf {
            it.unresolvedRequiredRoots.isNotEmpty() || it.excludedRoots.isNotEmpty()
        }
        return CallerThrowSourceKey.Concrete(subject.frameValue, routingDomain, exit.frame.callCompatibilityKey())
    }

    private data class KeyedCallerThrow(val exit: ActivationExit.Threw, val key: CallerThrowSourceKey)

    private fun mergedCallerThrowSubject(
        key: CallerThrowSourceKey,
        frame: Frame,
        exits: List<ActivationExit.Threw>,
    ): ThrownSubject = when (key) {
        is CallerThrowSourceKey.Concrete ->
            if (key.routingDomain == null) ThrownSubject.fromHeap(key.value, frame, hierarchy)
            else ThrownSubject.fromFrameValue(key.frameValue, frame, hierarchy)
        is CallerThrowSourceKey.Symbolic -> {
            val joined = requireNotNull(joinAllFrameValues(exits.map { it.subject.frameValue }, hierarchy))
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
}

/**
 * A drained callee's continuations in [caller]. [resumes] are built one by one; each [CallerThrowGroup] is built
 * all or nothing. [aborts] are caller paths that ended while routing.
 */
internal class CallRouting(
    val caller: Activation,
    val resumes: List<SuccessorSpec>,
    val throwGroups: List<CallerThrowGroup>,
    val aborts: List<ActivationExit.Aborted>,
)

/** One thrown partition's caller handler routes and its escape. A rejected route aborts it in [abortFrame]. */
internal class CallerThrowGroup(
    val abortFrame: Frame,
    val successors: List<SuccessorSpec>,
    val escape: ActivationExit.Threw?,
)

/**
 * The successor an opaque call resumes at: a copy of [source] where everything [arguments] reach is collapsed,
 * every reachable tracked field's accessibility is unknown, [constructed] is complete, and the result is unknown.
 */
internal fun opaqueCallSuccessor(
    source: Frame,
    address: Int,
    activation: Activation,
    arguments: List<Value>,
    argumentTypes: List<VirtualType>,
    constructed: HeapRef?,
    returnType: VirtualType,
): SuccessorSpec {
    val result = source.copy()
    for (ref in result.reachableTrackedFields(arguments)) {
        result.heap.setTrackedFieldAccessibility(ref, FieldAccessibility.UNKNOWN)
    }
    result.heap.collapseReachable(arguments, argumentTypes)
    constructed?.let(result.heap::markInitialized)
    if (returnType.descriptor != "V") result.setResult(Value.Unknown(returnType))
    return SuccessorSpec(address, activation, OnUnbuildable.SKIP_ONE, FrameOrigin.OpaqueResult(result))
}
