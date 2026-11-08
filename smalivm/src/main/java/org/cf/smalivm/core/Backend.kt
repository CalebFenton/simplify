package org.cf.smalivm.core

import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Collections

/** Scheduler-local call input with one atomic value per register word in the invoke instruction. */
class Invocation private constructor(
    val target: MethodSignature,
    val kind: InvokeKind,
    internal val registerFrameValues: List<FrameValue>,
    @Suppress("UNUSED_PARAMETER") atomic: Unit,
) {
    constructor(target: MethodSignature, kind: InvokeKind, registerValues: List<Value>) :
        this(target, kind, registerValues.map(FrameValue::plain), Unit)

    val registerValues: List<Value> = registerFrameValues.map(FrameValue::value)

    companion object {
        internal fun fromFrameValues(
            target: MethodSignature,
            kind: InvokeKind,
            registerValues: List<FrameValue>,
        ): Invocation = Invocation(target, kind, registerValues, Unit)
    }
}

/** One resolved call at the backend boundary, with one value per logical parameter. */
class PreparedInvocation private constructor(
    val target: MethodSignature,
    val callerClass: VirtualType,
    val kind: InvokeKind,
    val returnType: VirtualType,
    val parameterTypes: List<VirtualType>,
    internal val receiverFrameValue: FrameValue?,
    internal val argumentFrameValues: List<FrameValue>,
    @Suppress("UNUSED_PARAMETER") atomic: Unit,
) {
    constructor(
        target: MethodSignature,
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
            target: MethodSignature,
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

/**
 * Probe-only argument facts. This type contains no Java value, scheduler [Value], or [HeapRef].
 * [reflectedTarget] is what a reflected `Method` or `Constructor` value names.
 */
data class ProbeArgument(
    val type: VirtualType?,
    val isLatticeKnown: Boolean,
    val isHostMaterializable: Boolean,
    val isNullReference: Boolean,
    val isTrackedReference: Boolean,
    val reflectedTarget: ReflectedTarget? = null,
)

/** A reflected method or constructor; invoking it initializes its class when it is static or a constructor. */
data class ReflectedTarget(val method: MethodSignature, val initializesClass: Boolean)

/** Immutable metadata used before one backend accepts execution. */
class BackendInvocation internal constructor(
    val target: MethodSignature,
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

internal fun PreparedInvocation.probeMetadata(frame: Frame): BackendInvocation = BackendInvocation(
    target,
    callerClass,
    kind,
    returnType,
    parameterTypes.toList(),
    receiver?.toProbeArgument(frame),
    arguments.map { it.toProbeArgument(frame) },
)

private fun Value.toProbeArgument(frame: Frame): ProbeArgument = ProbeArgument(
    type,
    isLatticeKnown = this is Value.Known,
    isHostMaterializable = frame.isHostMaterializable(this),
    isNullReference = readsAsNullReference,
    isTrackedReference = frame.isTrackedReference(this),
    reflectedTarget = ((this as? Value.Known)?.raw as? HeapRef)
        ?.let { frame.heap.hostObjectAt(it) as? Executable }
        ?.reflectedTargetOrNull(),
)

/** What a reflected method or constructor names, or null when the host cannot describe it. */
internal fun Executable.reflectedTargetOrNull(): ReflectedTarget? = hostBoundary({ null }) {
    val owner = virtualTypeOfHostClass(declaringClass)
    val parameters = parameterTypes.map(::virtualTypeOfHostClass)
    when (this) {
        is Method -> ReflectedTarget(
            MethodSignature(owner, name, parameters, virtualTypeOfHostClass(returnType)),
            initializesClass = Modifier.isStatic(modifiers),
        )
        is Constructor<*> -> ReflectedTarget(
            MethodSignature(owner, MethodSignature.CONSTRUCTOR_NAME, parameters, VirtualType.of("V")),
            initializesClass = true,
        )
        else -> null
    }
}

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

/** A value the host produced for [declaredType]; a reference keeps its runtime class. */
internal fun Any?.toHostValue(declaredType: VirtualType): HostValue = when {
    declaredType.isPrimitive -> HostValue.Primitive(requireNotNull(this), declaredType)
    this == null -> HostValue.TypedNull(declaredType)
    else -> HostValue.Reference(this, virtualTypeOfHostClass(javaClass))
}

/** [throwable] reaches the guest as thrown by the target, tagged with its runtime class. */
internal fun targetThrew(throwable: Throwable): SynchronousCallResult.TargetThrew =
    SynchronousCallResult.TargetThrew(HostThrowable(virtualTypeOfHostClass(throwable.javaClass), throwable))

/** The call completes with an unknown value of [type]. */
internal fun returnUnknown(type: VirtualType): SynchronousCallResult =
    IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(type))

/**
 * Runs host code at the analysis boundary. A [VirtualMachineError] means the analysing JVM itself is failing,
 * so it escapes; every other throwable becomes [onFailure]'s answer.
 */
internal inline fun <T> hostBoundary(onFailure: (Throwable) -> T, block: () -> T): T = try {
    block()
} catch (fatal: VirtualMachineError) {
    throw fatal
} catch (failure: Throwable) {
    onFailure(failure)
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

/** Schedules runnable application bytecode. */
internal class ApplicationBytecodeBackend(private val resolver: ProgramSnapshotResolver) : Backend {
    override val name: String get() = "application-bytecode"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = when (lookup) {
        is MethodLookup.Runnable -> if (resolver.hierarchy.isFrameworkClass(lookup.view.definingClass)) {
            BackendProbe.Declined("framework method ${lookup.view.signature} has no runnable guest body")
        } else {
            BackendProbe.Schedule(lookup.view, ENTRY_ADDRESS)
        }
        MethodLookup.NoSuchClass -> BackendProbe.Declined("${call.target.definingClass} is not loaded")
        MethodLookup.NoSuchMethod -> BackendProbe.Declined("${call.target} is not loaded")
        is MethodLookup.NoBody, is MethodLookup.DefaultConflict, is MethodLookup.AbstractSelection ->
            BackendProbe.Declined("${call.target} has no application bytecode body")
    }

    private companion object {
        const val ENTRY_ADDRESS = 0
    }
}

/** Schedules a captured bundled-framework body after host policy declines execution. */
internal class FrameworkBytecodeBackend(private val resolver: ProgramSnapshotResolver) : Backend {
    override val name: String get() = "framework-bytecode"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = when (lookup) {
        is MethodLookup.Runnable -> if (resolver.hierarchy.isFrameworkClass(lookup.view.definingClass)) {
            BackendProbe.Schedule(lookup.view, ENTRY_ADDRESS)
        } else {
            BackendProbe.Declined("${lookup.view.signature} is an application method")
        }
        MethodLookup.NoSuchClass -> BackendProbe.Declined("${call.target.definingClass} is not loaded")
        MethodLookup.NoSuchMethod -> BackendProbe.Declined("${call.target} is not loaded")
        is MethodLookup.NoBody, is MethodLookup.DefaultConflict, is MethodLookup.AbstractSelection ->
            BackendProbe.Declined("${call.target} has no framework bytecode body")
    }

    private companion object {
        const val ENTRY_ADDRESS = 0
    }
}
