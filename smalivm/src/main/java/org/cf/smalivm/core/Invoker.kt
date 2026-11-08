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
) {
    private val classInit = ClassInitPlanner(resolver)

    /** The production backends in selection order, with the emulators [services] holds when the session starts. */
    internal constructor(resolver: ProgramSnapshotResolver, services: InvocationServices) : this(
        resolver,
        listOf(
            EmulateBackend(services.sessionEmulators(resolver)),
            ApplicationBytecodeBackend(resolver),
            ReflectBackend(services.reflectionPolicy, resolver),
            FrameworkBytecodeBackend(resolver),
        ),
    )

    /** Resolves and logically binds [call]. This method never probes a backend or changes run state. */
    fun prepare(
        call: Invocation,
        callerClass: VirtualType,
        receiverType: VirtualType? = null,
    ): InvocationPreparation {
        val resolved = resolver.resolveInvocation(call.target, call.kind, callerClass, receiverType)
        if (resolved is InvocationLookup.Ambiguous) {
            return OpaqueInvocation(call.target.returnType, resolved.candidates)
        }
        resolved as InvocationLookup.Resolved
        val method = when (val lookup = resolved.lookup) {
            MethodLookup.NoSuchClass -> return InvocationPreparation.ThrowResolution(
                GuestThrowables.unresolvedClass(call.target.definingClass, resolver.hierarchy),
            )
            MethodLookup.NoSuchMethod -> return InvocationPreparation.ThrowResolution(
                GuestThrowables.noSuchMethod(call.target, call.kind),
            )
            is MethodLookup.Runnable -> lookup.method
            is MethodLookup.NoBody -> lookup.method
            is MethodLookup.DefaultConflict -> lookup.method
            is MethodLookup.AbstractSelection -> lookup.method
        }

        val words = method.target.argumentWords(call.kind)
        if (call.registerFrameValues.size != words.wordCount) {
            return OpaqueInvocation(
                method.target.returnType,
                resolved.candidates,
                "${call.target} was invoked with ${call.registerFrameValues.size} argument register(s), but " +
                    "${method.target} requires ${words.wordCount}",
            )
        }
        val bound = words.arguments.map { bindFrameValue(call.registerFrameValues[it.word], it.type) }
        val receiver = if (call.kind == InvokeKind.STATIC) null else bound.first()
        return InvocationPreparation.Ready(
            PreparedInvocation.fromFrameValues(
                method.target,
                callerClass,
                call.kind,
                method.target.returnType,
                method.target.parameterTypes,
                receiver,
                if (receiver == null) bound else bound.drop(1),
            ),
            resolved.lookup,
            resolved.candidates,
        )
    }

    /** Returns the applicable runtime class-init target before any backend is probed. */
    internal fun applicableClassInitTarget(call: PreparedInvocation): VirtualType? = call.classInitTarget

    /** Selects a backend for an already prepared call. The scheduler applies the returned plan. */
    internal fun plan(preparation: InvocationPreparation.Ready, frame: Frame): InvocationPlan {
        val declines = mutableListOf<String>()
        val metadata = preparation.invocation.probeMetadata(frame)
        for (backend in backends) {
            when (val probe = backend.probe(metadata, preparation.lookup)) {
                is BackendProbe.Declined -> declines += "${backend.name}: ${probe.diagnostic}"
                is BackendProbe.Denied -> return OpaqueInvocation(
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
        return OpaqueInvocation(
            preparation.invocation.returnType,
            preparation.candidates,
            declines.joinToString("; ").ifEmpty { "no invocation backend is configured" },
        )
    }

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

    /** What initializing [type] requires on the path [state] describes. The scheduler applies the plan. */
    internal fun planClassInit(state: Frame, type: VirtualType): ClassInitPlan = classInit.plan(state, type)
}

/** Whether a call of [kind] whose receiver word holds [receiver] throws NullPointerException before dispatch. */
internal fun receiverIsNull(kind: InvokeKind, receiver: Value?): Boolean =
    kind != InvokeKind.STATIC && receiver?.readsAsNullReference == true

/** The runtime type dispatch selects by: the tracked receiver object's type, or null for a static call. */
internal fun receiverRuntimeType(kind: InvokeKind, receiver: Value?, frame: Frame): VirtualType? =
    if (kind == InvokeKind.STATIC) null
    else ((receiver as? Value.Known)?.raw as? HeapRef)?.let { frame.heapObject(it)?.type }

/**
 * What a linked method that cannot be invoked throws after the receiver null check (ART's
 * `ThrowInvocationTimeError`), or null when the call proceeds.
 */
internal fun InvocationPreparation.Ready.invocationTimeError(receiverType: VirtualType?): ThrowableRecipe? =
    when (lookup) {
        is MethodLookup.DefaultConflict -> GuestThrowables.conflictingDefaultMethods(invocation.target)
        is MethodLookup.AbstractSelection -> GuestThrowables.abstractMethod(lookup.method.target, receiverType)
        else -> null
    }

/** The values an opaque call can reach: the receiver, then the arguments. */
internal fun PreparedInvocation.opaqueArguments(): List<Value> = listOfNotNull(receiver) + arguments

internal fun PreparedInvocation.opaqueArgumentTypes(): List<VirtualType> =
    (if (receiver == null) emptyList() else listOf(target.definingClass)) + parameterTypes

/** The declared type of each of [valueCount] register words of a call site whose target never bound. */
internal fun OpArm.Invoke.opaqueArgumentTypes(valueCount: Int): List<VirtualType> {
    val words = target.argumentWords(kind)
    return List(valueCount) { word -> words.typeAt(word) ?: VirtualType.OBJECT }
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
            is MethodLookup.DefaultConflict, is MethodLookup.AbstractSelection, MethodLookup.NoSuchClass,
            MethodLookup.NoSuchMethod -> false
        }
    }

    /** Resolution failed before binding; the call throws [recipe]. */
    class ThrowResolution internal constructor(internal val recipe: ThrowableRecipe) : InvocationPreparation
}

/** One argument's landing place in the callee's frame. Public inspection unwraps [value]. */
class ParameterBinding internal constructor(val register: Int, internal val frameValue: FrameValue) {
    val value: Value get() = frameValue.value
}

/** What the [Invoker] decided about one call. Sealed so the scheduler handles every case. */
sealed interface InvocationPlan {
    /**
     * Run [view] as a nested [Activation], entering at [entryAddress] with [parameterBindings] seeded into
     * the callee's own frame. The callee's frame shares the caller's heap id space; the scheduler builds it
     * through `Frame.calleeEntryFrame`, so no plan can choose otherwise.
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
}

/**
 * A call no backend models: resolution was ambiguous, the call site's arity contradicts the resolved method,
 * policy denied it, or every backend declined. The same answer whether binding or backend selection found it.
 */
class OpaqueInvocation internal constructor(
    val returnType: VirtualType,
    val candidates: Set<String>,
    val diagnostic: String? = null,
) : InvocationPreparation, InvocationPlan
