package org.cf.smalivm.core

import java.util.Collections

/** Scheduler-local call input with one atomic value per register word in the invoke instruction. */
class Invocation private constructor(
    val target: MethodDescriptor,
    val kind: InvokeKind,
    internal val registerFrameValues: List<FrameValue>,
    @Suppress("UNUSED_PARAMETER") atomic: Unit,
) {
    constructor(target: MethodDescriptor, kind: InvokeKind, registerValues: List<Value>) :
        this(target, kind, registerValues.map(FrameValue::plain), Unit)

    val registerValues: List<Value> = registerFrameValues.map(FrameValue::value)

    companion object {
        internal fun fromFrameValues(
            target: MethodDescriptor,
            kind: InvokeKind,
            registerValues: List<FrameValue>,
        ): Invocation = Invocation(target, kind, registerValues, Unit)
    }
}

/** One resolved call at the backend boundary, with one value per logical parameter. */
class PreparedInvocation private constructor(
    val target: MethodDescriptor,
    val callerClass: VirtualType,
    val kind: InvokeKind,
    val returnType: VirtualType,
    val parameterTypes: List<VirtualType>,
    internal val receiverFrameValue: FrameValue?,
    internal val argumentFrameValues: List<FrameValue>,
    @Suppress("UNUSED_PARAMETER") atomic: Unit,
) {
    constructor(
        target: MethodDescriptor,
        callerClass: VirtualType,
        kind: InvokeKind,
        returnType: VirtualType,
        parameterTypes: List<VirtualType>,
        receiver: Value?,
        arguments: List<Value>,
    ) : this(
        target,
        callerClass,
        kind,
        returnType,
        parameterTypes,
        receiver?.let(FrameValue::plain),
        arguments.map(FrameValue::plain),
        Unit,
    )

    val receiver: Value? = receiverFrameValue?.value
    val arguments: List<Value> = argumentFrameValues.map(FrameValue::value)

    companion object {
        internal fun fromFrameValues(
            target: MethodDescriptor,
            callerClass: VirtualType,
            kind: InvokeKind,
            returnType: VirtualType,
            parameterTypes: List<VirtualType>,
            receiver: FrameValue?,
            arguments: List<FrameValue>,
        ): PreparedInvocation = PreparedInvocation(
            target,
            callerClass,
            kind,
            returnType,
            parameterTypes,
            receiver,
            arguments,
            Unit,
        )
    }
}

/** Probe-only argument facts. This type contains no Java value, scheduler [Value], or [HeapRef]. */
data class ProbeArgument(
    val type: VirtualType?,
    val isLatticeKnown: Boolean,
    val isHostMaterializable: Boolean,
    val isNullReference: Boolean,
    val isTrackedReference: Boolean,
)

/** Immutable metadata used before one backend accepts execution. */
class BackendInvocation internal constructor(
    val target: MethodDescriptor,
    val callerClass: VirtualType,
    val kind: InvokeKind,
    val returnType: VirtualType,
    parameterTypes: List<VirtualType>,
    val receiver: ProbeArgument?,
    arguments: List<ProbeArgument>,
) {
    val parameterTypes: List<VirtualType> = Collections.unmodifiableList(parameterTypes.toList())
    val arguments: List<ProbeArgument> = Collections.unmodifiableList(arguments.toList())
}

internal fun PreparedInvocation.probeMetadata(
    hostMaterializable: (Value) -> Boolean,
    trackedReference: (Value) -> Boolean,
): BackendInvocation = BackendInvocation(
    target,
    callerClass,
    kind,
    returnType,
    parameterTypes.toList(),
    receiver?.toProbeArgument(hostMaterializable, trackedReference),
    arguments.map { it.toProbeArgument(hostMaterializable, trackedReference) },
)

private fun Value.toProbeArgument(
    hostMaterializable: (Value) -> Boolean,
    trackedReference: (Value) -> Boolean,
): ProbeArgument = ProbeArgument(
    type,
    isLatticeKnown = this is Value.Known,
    isHostMaterializable = hostMaterializable(this),
    isNullReference = readsAsNullReference,
    isTrackedReference = trackedReference(this),
)

/** A value returned by a synchronous backend. Mutable references carry their actual JVM object. */
sealed interface HostValue {
    val type: VirtualType

    data class Primitive(val value: Any, override val type: VirtualType) : HostValue {
        init {
            val valid = when (type.descriptor) {
                "Z" -> value is Boolean
                "B" -> value is Byte
                "C" -> value is Char
                "S" -> value is Short
                "I" -> value is Int
                "J" -> value is Long
                "F" -> value is Float
                "D" -> value is Double
                else -> false
            }
            require(valid) { "$value is not a canonical primitive for ${type.descriptor}" }
        }
    }

    data class TypedNull(override val type: VirtualType) : HostValue {
        init {
            require(type.isReference) { "Typed host null requires a reference type, not $type" }
        }
    }

    data class Reference(
        val value: Any,
        override val type: VirtualType,
    ) : HostValue {
        init {
            require(type.isReference) { "Host reference requires a reference type, not $type" }
        }
    }
}

/** A returned value. Void is distinct from a returned typed null. */
sealed interface HostReturn {
    data object Void : HostReturn
    data class Value(val value: HostValue) : HostReturn
}

/** A guest-visible target throwable. [type] is the backend's declared tag; JVM identity sets runtime type. */
data class HostThrowable(val type: VirtualType, val throwable: Throwable) {
    init {
        require(type.isReference) { "Imported host throwable requires a reference type, not $type" }
    }
}

/** Result from one accepted synchronous backend execution. These are not scheduler [Outcome] values. */
sealed interface SynchronousCallResult {
    data class Returned(val value: HostReturn) : SynchronousCallResult
    data class TargetThrew(val throwable: HostThrowable) : SynchronousCallResult
    data class NeedsClassInit(val type: VirtualType) : SynchronousCallResult
    data class InfrastructureFailure(val cause: Throwable) : SynchronousCallResult
}

/** Pre-execution backend decision. Only [Declined] permits another backend. */
sealed interface BackendProbe {
    data class Declined(val diagnostic: String) : BackendProbe
    data class Denied(val diagnostic: String) : BackendProbe
    data class Schedule(val view: MethodView, val entryAddress: Int) : BackendProbe

    /** Accepted synchronous execution. A bound executor freezes mutable registry selection at probe time. */
    class Execute private constructor(
        val effect: SideEffectLevel,
        internal val boundExecutor: BoundSynchronousExecutor?,
    ) : BackendProbe {
        constructor(effect: SideEffectLevel) : this(effect, null)

        internal companion object {
            fun bound(effect: SideEffectLevel, executor: BoundSynchronousExecutor): Execute =
                Execute(effect, executor)
        }
    }
}

/** A scheduler operation requested without exposing heap identity or lattice values to its handler. */
internal sealed interface IntrinsicOperation {
    data class AllocateTrackedField(val metadata: TrackedFieldValue) : IntrinsicOperation
    data object CloneArrayReceiver : IntrinsicOperation
    data class InitializeEnumReceiver(
        val name: EmulatorValue,
        val ordinal: EmulatorValue,
    ) : IntrinsicOperation
    data class ReadVirtualField(val field: TrackedFieldValue) : IntrinsicOperation
    data object ReadThrowableCause : IntrinsicOperation
    data class RouteFailedClassInit(val type: VirtualType) : IntrinsicOperation
    data class SetTrackedFieldAccessible(
        val metadata: TrackedFieldValue,
        val accessibility: FieldAccessibility,
    ) : IntrinsicOperation
    data class ReturnUnknown(val type: VirtualType) : IntrinsicOperation
}

/** Internal result applied only by the scheduler. */
internal data class IntrinsicCallResult(val operation: IntrinsicOperation) : SynchronousCallResult

/** A host constructor completed. The scheduler installs [instance] at the existing receiver ref. */
internal data class ConstructedHostObjectResult(val instance: Any) : SynchronousCallResult

internal data class EmulatorArgumentMode(
    val allowsUnknownArguments: Boolean,
    val exposesTrackedReferences: Boolean,
)

/** Immutable scheduler-side adapter for one accepted synchronous execution. */
internal class BoundSynchronousExecutor(
    val emulatorArgumentMode: EmulatorArgumentMode?,
    private val executeBlock: (SelectedHostCall) -> SynchronousCallResult,
) {
    constructor(backend: SynchronousBackend) : this(null, backend::execute)

    fun execute(call: SelectedHostCall): SynchronousCallResult = executeBlock(call)
}

/** Probes one prepared invocation without receiving scheduler-owned mutable state. */
interface Backend {
    val name: String
    fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe
}

/** A backend that can execute synchronously after its [BackendProbe.Execute] is accepted. */
interface SynchronousBackend : Backend {
    fun execute(call: SelectedHostCall): SynchronousCallResult
}

/** Schedules runnable guest bytecode. Host backends are added in later Plan 11 tasks. */
internal class ApplicationBytecodeBackend(private val resolver: ProgramSnapshotResolver) : Backend {
    constructor(classIndex: ClassIndex) : this(classIndex.resolver(classIndex.currentSnapshot))

    override val name: String get() = "application-bytecode"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = when (lookup) {
        is MethodLookup.Runnable -> if (resolver.isFrameworkClass(lookup.view.definingClass)) {
            BackendProbe.Declined("framework method ${lookup.view.signature} has no runnable guest body")
        } else {
            BackendProbe.Schedule(lookup.view, ENTRY_ADDRESS)
        }
        MethodLookup.NoSuchClass -> BackendProbe.Declined("${call.target.definingClass} is not loaded")
        MethodLookup.NoSuchMethod -> BackendProbe.Declined("${call.target} is not loaded")
        is MethodLookup.NoBody -> BackendProbe.Declined("${call.target} has no application bytecode body")
    }

    private companion object {
        const val ENTRY_ADDRESS = 0
    }
}

/** Schedules a captured bundled-framework body after host policy declines execution. */
internal class FrameworkBytecodeBackend(private val resolver: ProgramSnapshotResolver) : Backend {
    constructor(classIndex: ClassIndex) : this(classIndex.resolver(classIndex.currentSnapshot))

    override val name: String get() = "framework-bytecode"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = when (lookup) {
        is MethodLookup.Runnable -> if (resolver.isFrameworkClass(lookup.view.definingClass)) {
            BackendProbe.Schedule(lookup.view, ENTRY_ADDRESS)
        } else {
            BackendProbe.Declined("${lookup.view.signature} is an application method")
        }
        MethodLookup.NoSuchClass -> BackendProbe.Declined("${call.target.definingClass} is not loaded")
        MethodLookup.NoSuchMethod -> BackendProbe.Declined("${call.target} is not loaded")
        is MethodLookup.NoBody -> BackendProbe.Declined("${call.target} has no framework bytecode body")
    }

    private companion object {
        const val ENTRY_ADDRESS = 0
    }
}
