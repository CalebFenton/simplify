package org.cf.smalivm.core

/**
 * Resolves, binds, and selects execution for one call without writing scheduler state.
 *
 * [prepare] is pure and runs before receiver-null handling. It resolves the target and converts register
 * words into a [PreparedInvocation]. [plan] runs later and is the first operation that can call a [Backend].
 * [ExecutionSession] keeps ownership of null routing, class-init scheduling, frame construction, effects,
 * frontier changes, and result application.
 */
class Invoker internal constructor(
    internal val resolver: ProgramSnapshotResolver,
    private val backends: List<Backend>,
    private val classInit: ClassInitPlanner,
    private val onEvent: (String) -> Unit,
) {

    internal constructor(classIndex: ClassIndex) : this(
        InvocationServices(classIndex).capture(classIndex.resolver(classIndex.currentSnapshot)),
    )

    internal constructor(services: SnapshotInvocationServices) :
        this(
            services.resolver,
            listOf(
                EmulateBackend(services.emulators),
                ApplicationBytecodeBackend(services.resolver),
                ReflectBackend(services.reflectionPolicy, services.resolver),
                FrameworkBytecodeBackend(services.resolver),
            ),
            ClassInitPlanner(services.resolver),
            {},
        )

    internal constructor(classIndex: ClassIndex, backends: List<Backend>) :
        this(classIndex.resolver(classIndex.currentSnapshot), backends)

    internal constructor(classIndex: ClassIndex, backends: List<Backend>, onEvent: (String) -> Unit) :
        this(classIndex.resolver(classIndex.currentSnapshot), backends, onEvent)

    internal constructor(resolver: ProgramSnapshotResolver, backends: List<Backend>) :
        this(resolver, backends, ClassInitPlanner(resolver), {})

    internal constructor(
        resolver: ProgramSnapshotResolver,
        backends: List<Backend>,
        onEvent: (String) -> Unit,
    ) : this(resolver, backends, ClassInitPlanner(resolver), onEvent)

    /** Resolves and logically binds [call]. This method never probes a backend or changes run state. */
    fun prepare(
        call: Invocation,
        callerClass: VirtualType = call.target.definingClass,
        receiverType: VirtualType? = null,
    ): InvocationPreparation {
        onEvent("resolve")
        val resolved = resolver.resolveInvocation(call.target, call.kind, callerClass, receiverType)
        if (resolved is InvocationLookup.Ambiguous) {
            return InvocationPreparation.Opaque(call.target.returnType, resolved.candidates)
        }
        resolved as InvocationLookup.Resolved
        val method = when (val lookup = resolved.lookup) {
            MethodLookup.NoSuchClass -> return InvocationPreparation.ThrowResolution(
                VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            )
            MethodLookup.NoSuchMethod -> return InvocationPreparation.ThrowResolution(
                VirtualType.of("Ljava/lang/NoSuchMethodError;"),
            )
            is MethodLookup.Runnable -> lookup.method
            is MethodLookup.NoBody -> lookup.method
        }

        onEvent("bind")
        val expectedWords = method.parameterTypes.sumOf { if (it.isWide) 2 else 1 } +
            if (call.kind == InvokeKind.STATIC) 0 else 1
        if (call.registerFrameValues.size != expectedWords) {
            return InvocationPreparation.Opaque(
                method.returnType,
                resolved.candidates,
                "${call.target} was invoked with ${call.registerFrameValues.size} argument register(s), but " +
                    "${method.target} requires $expectedWords",
            )
        }

        var word = 0
        val receiver = if (call.kind == InvokeKind.STATIC) {
            null
        } else {
            bindFrameValue(call.registerFrameValues[word++], method.target.definingClass)
        }
        val arguments = ArrayList<FrameValue>(method.parameterTypes.size)
        for (parameterType in method.parameterTypes) {
            arguments.add(bindFrameValue(call.registerFrameValues[word], parameterType))
            word += if (parameterType.isWide) 2 else 1
        }
        return InvocationPreparation.Ready(
            PreparedInvocation.fromFrameValues(
                method.target,
                callerClass,
                call.kind,
                method.returnType,
                method.parameterTypes,
                receiver,
                arguments,
            ),
            resolved.lookup,
            resolved.candidates,
        )
    }

    /** Checks the prepared receiver after resolution and binding, but before backend selection. */
    internal fun receiverIsNull(preparation: InvocationPreparation.Ready): Boolean {
        onEvent("null-check")
        return preparation.invocation.receiver?.readsAsNullReference == true
    }

    /** Returns the applicable runtime class-init target before any backend is probed. */
    internal fun applicableClassInitTarget(call: PreparedInvocation): VirtualType? =
        call.classInitTarget.also { if (it != null) onEvent("class-init-trigger") }

    /** Selects a backend for an already prepared call. The scheduler applies the returned plan. */
    fun plan(preparation: InvocationPreparation.Ready): InvocationPlan =
        plan(preparation, ::isMaterializableWithoutHeap) { false }

    internal fun plan(
        preparation: InvocationPreparation.Ready,
        hostMaterializable: (Value) -> Boolean,
        trackedReference: (Value) -> Boolean,
    ): InvocationPlan {
        val declines = mutableListOf<String>()
        val metadata = preparation.invocation.probeMetadata(hostMaterializable, trackedReference)
        for (backend in backends) {
            when (val probe = backend.probe(metadata, preparation.lookup)) {
                is BackendProbe.Declined -> declines += "${backend.name}: ${probe.diagnostic}"
                is BackendProbe.Denied -> return InvocationPlan.Opaque(
                    preparation.invocation.returnType,
                    preparation.candidates,
                    probe.diagnostic,
                )
                is BackendProbe.Schedule -> return planGuest(preparation.invocation, probe)
                is BackendProbe.Execute -> {
                    val executor = probe.boundExecutor ?: run {
                        require(backend is SynchronousBackend) {
                            "Backend ${backend.name} returned Execute but is not synchronous"
                        }
                        BoundSynchronousExecutor(backend)
                    }
                    return InvocationPlan.Execute(preparation.invocation, executor, probe.effect)
                }
            }
        }
        onEvent("opaque")
        return InvocationPlan.Opaque(
            preparation.invocation.returnType,
            preparation.candidates,
            declines.joinToString("; ").ifEmpty { "no invocation backend is configured" },
        )
    }

    private fun isMaterializableWithoutHeap(value: Value): Boolean =
        value is Value.Known && value.raw !is HeapRef && (value.raw == null || value.raw.isDirectHostImmutable())

    private fun planGuest(call: PreparedInvocation, schedule: BackendProbe.Schedule): InvocationPlan {
        val values = ArrayList<FrameValue>(
            call.argumentFrameValues.size + if (call.receiverFrameValue == null) 0 else 1,
        )
        call.receiverFrameValue?.let(values::add)
        values.addAll(call.argumentFrameValues)
        val bindings = schedule.view.parameters.zip(values) { parameter, value ->
            ParameterBinding(parameter.register, value)
        }
        return InvocationPlan.RunGuest(schedule.view, schedule.entryAddress, bindings)
    }

    private fun bindFrameValue(value: FrameValue, parameterType: VirtualType): FrameValue {
        val bound = bindValue(value.value, parameterType)
        return if (bound == value.value) value else FrameValue.plain(bound)
    }

    private fun bindValue(value: Value, parameterType: VirtualType): Value {
        if (
            parameterType.isReference && value is Value.Known &&
            value.type.descriptor == "I" && Coerce.toInt(value.raw) == 0
        ) {
            return Value.Known(null, parameterType)
        }
        if (value !is Value.Known || !parameterType.isPrimitive) return value
        val raw = value.raw
        val rebound = when (parameterType.descriptor) {
            "Z" -> Coerce.toInt(raw)?.let { it != 0 }
            "B" -> Coerce.toInt(raw)?.toByte()
            "C" -> Coerce.toInt(raw)?.toChar()
            "S" -> Coerce.toInt(raw)?.toShort()
            "I" -> Coerce.toInt(raw)
            "J" -> Coerce.toLong(raw)
            "F" -> Coerce.toFloat(raw)
            "D" -> Coerce.toDouble(raw)
            else -> null
        }
        return if (rebound == null) Value.Unknown(parameterType) else Value.Known(rebound, parameterType)
    }

    /**
     * What initializing [type] requires on the path [state] describes -- see [ClassInitPlan], and
     * [ClassInitPlanner.plan] for the walk.
     *
     * Takes the read-only [ClassState] rather than the [Frame]: the planner must not be able to mark
     * anything, for the same reason [Invocation] carries values rather than registers. The scheduler applies
     * the plan, to a successor's own frame.
     */
    internal fun planClassInit(state: Frame, type: VirtualType): ClassInitPlan = classInit.plan(state, type)
}

/** Pure result of invocation resolution and logical binding. */
sealed interface InvocationPreparation {
    class Ready internal constructor(
        val invocation: PreparedInvocation,
        internal val lookup: MethodLookup,
        val candidates: Set<String>,
    ) : InvocationPreparation {
        val isSynchronized: Boolean get() = when (lookup) {
            is MethodLookup.Runnable -> lookup.method.isSynchronized
            is MethodLookup.NoBody -> lookup.method.isSynchronized
            MethodLookup.NoSuchClass, MethodLookup.NoSuchMethod -> false
        }
    }

    class ThrowResolution(val exceptionType: VirtualType) : InvocationPreparation

    class Opaque(
        val returnType: VirtualType,
        val candidates: Set<String>,
        val diagnostic: String? = null,
    ) : InvocationPreparation
}

/** One argument's landing place in the callee's frame. Public inspection unwraps [value]. */
class ParameterBinding internal constructor(val register: Int, internal val frameValue: FrameValue) {
    constructor(register: Int, value: Value) : this(register, FrameValue.plain(value))
    val value: Value get() = frameValue.value
}

/**
 * What the [Invoker] decided about one call. Sealed so the scheduler is forced to handle every case, and so
 * a later task adding one (a synthetic fork for a partitioned merge, a class-init prelude) cannot be
 * silently defaulted.
 */
sealed interface InvocationPlan {
    /**
     * Run [view] as a nested [Activation], entering at [entryAddress] with [parameterBindings] seeded into
     * the callee's own frame. The callee's frame **shares the caller's heap id space**
     * (`CARRY-FORWARD.md` Plan 9 item 10) -- the scheduler does that through `Frame.calleeEntryFrame`, so
     * no plan can get it wrong by forgetting.
     */
    class RunGuest(
        val view: MethodView,
        val entryAddress: Int,
        val parameterBindings: List<ParameterBinding>,
    ) : InvocationPlan

    /** Execute one accepted synchronous backend. The scheduler applies the returned data. */
    class Execute internal constructor(
        val invocation: PreparedInvocation,
        private val executor: BoundSynchronousExecutor,
        val effect: SideEffectLevel,
    ) : InvocationPlan {
        internal fun select(
            frame: Frame,
            classLoader: ClassLoader,
            suspendedCallerRoots: List<Value> = emptyList(),
        ): SelectedSynchronousCall {
            val call = when (val mode = executor.emulatorArgumentMode) {
                null -> SelectedHostCall.from(invocation, frame, classLoader, suspendedCallerRoots)
                else -> SelectedHostCall.forEmulator(invocation, frame, classLoader, mode, suspendedCallerRoots)
            }
            return SelectedSynchronousCall(call, executor)
        }
    }

    class Opaque(
        val returnType: VirtualType,
        val candidates: Set<String>,
        val diagnostic: String? = null,
    ) : InvocationPlan
}

private val MethodDescriptor.returnType: VirtualType
    get() = VirtualType.of(proto.substringAfter(')'))
