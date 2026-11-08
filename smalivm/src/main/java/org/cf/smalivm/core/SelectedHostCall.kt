package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Collections
import java.util.IdentityHashMap

/** Selected host state for one accepted synchronous backend. */
class SelectedHostCall private constructor(
    val target: MethodSignature,
    val callerClass: VirtualType,
    val kind: InvokeKind,
    val returnType: VirtualType,
    val parameterTypes: List<VirtualType>,
    val receiver: Any?,
    val arguments: List<Any?>,
    private val emulatorValues: EmulatorValues?,
    private val checkpoint: HostGraphCheckpoint,
    private val sourceNodeIds: IdentityHashMap<Any, HostGraphNodeId>,
    private val sourceHeapRefs: IdentityHashMap<Any, HeapRef>,
    private val arrays: List<MaterializedArray>,
) {
    internal fun sourceNodeId(raw: Any): HostGraphNodeId? = sourceNodeIds[raw]
    internal fun sourceHeapRef(raw: Any): HeapRef? = sourceHeapRefs[raw]

    internal fun installConstructedHostObject(frame: Frame, ref: HeapRef, raw: Any) {
        frame.heap.verifyHostGraphCheckpoint(checkpoint)
        require(frame.heap.installConstructedHostObject(ref, raw)) {
            "Cannot install reflected constructor result at $ref"
        }
    }

    internal fun emulatorInvocation(): EmulatorInvocation =
        requireNotNull(emulatorValues) { "Direct Java backend values are not emulator boundary values" }.invocation

    /** Finalizes array copy-back and exact graph comparison on every non-fatal execution path. */
    internal fun finalize(frame: Frame): HostGraphComparison {
        frame.heap.verifyHostGraphCheckpoint(checkpoint)
        hostBoundary({ failure ->
            frame.finalizeHostGraph(checkpoint)
            throw failure
        }) {
            copyArraysBack(frame)
        }
        return frame.finalizeHostGraph(checkpoint)
    }

    /** Validates every mirror before applying any lattice update. */
    private fun copyArraysBack(frame: Frame) {
        val transaction = frame.heap.transactionCheckpoint()
        try {
            val pending = arrays.map { materialized ->
                val component = requireNotNull(materialized.type.componentType)
                val raws = List(ReflectArray.getLength(materialized.raw)) { ReflectArray.get(materialized.raw, it) }
                require(raws.size == materialized.length) {
                    "Host array ${materialized.ref} changed length"
                }
                PendingArray(materialized, component, raws)
            }
            for (array in pending) {
                array.raws.forEach {
                    validateCopiedElement(it, array.component, array.materialized.raw.javaClass.componentType)
                }
            }

            val newRoots = mutableListOf<Any>()
            val seenNewRoots = IdentityHashMap<Any, Boolean>()
            for (raw in pending.flatMap(PendingArray::raws)) {
                if (
                    raw != null && !raw.isDirectHostImmutable() && sourceNodeId(raw) == null &&
                    frame.heap.hostNodeId(raw) == null && seenNewRoots.put(raw, true) == null
                ) {
                    newRoots += raw
                }
            }
            val batchImports = frame.heap.importHostObjects(newRoots)

            val converted = pending.map { array ->
                ArrayReplacement(
                    array.materialized.ref,
                    array.raws.mapIndexed { index, raw ->
                        importArrayElement(
                            frame,
                            raw,
                            array.component,
                            array.materialized.elements[index],
                            batchImports,
                        )
                    },
                )
            }
            frame.heap.applyArrayReplacementsAtomically(converted)
        } catch (failure: Throwable) {
            hostBoundary({ rollbackFailure -> if (rollbackFailure !== failure) failure.addSuppressed(rollbackFailure) }) {
                frame.heap.rollbackFailedCopyback(transaction, arrays.map { it.ref }.toSet())
            }
            throw failure
        }
    }

    private fun importArrayElement(
        frame: Frame,
        raw: Any?,
        component: VirtualType,
        original: Value,
        batchImports: IdentityHashMap<Any, HeapRef>,
    ): Value = when {
        raw == null || raw.isDirectHostImmutable() -> when {
            component.isPrimitive -> Value.Known(raw, component)
            original is Value.Known && sameDirectValue(raw, original.raw) -> original
            raw == null -> Value.Known(null, component)
            else -> Value.Known(raw, virtualTypeOfHostClass(raw.javaClass))
        }
        else -> {
            val nodeId = sourceNodeId(raw) ?: frame.heap.hostNodeId(raw)
            val ref = if (nodeId != null) {
                frame.heap.refForNode(nodeId) ?: frame.heap.importHostNode(nodeId, component, raw)
            } else {
                requireNotNull(batchImports[raw]) { "New host value was not admitted in the copy-back batch" }
            }
            if (original is Value.Known && original.raw == ref) original
            else Value.Known(ref, requireNotNull(frame.heapObject(ref)).type)
        }
    }

    private fun sameDirectValue(left: Any?, right: Any?): Boolean = when {
        left == null || right == null -> left == null && right == null
        left.javaClass != right.javaClass -> false
        left is Float -> left.toRawBits() == (right as Float).toRawBits()
        left is Double -> left.toRawBits() == (right as Double).toRawBits()
        else -> left == right
    }

    private class PendingArray(
        val materialized: MaterializedArray,
        val component: VirtualType,
        val raws: List<Any?>,
    )

    internal class MaterializedArray(
        val ref: HeapRef,
        val type: VirtualType,
        val length: Int,
        val elements: List<Value>,
        val raw: Any,
    )

    private class EmulatorValues(val invocation: EmulatorInvocation)

    internal companion object {
        fun from(
            call: PreparedInvocation,
            frame: Frame,
            classLoader: ClassLoader,
            suspendedCallerRoots: List<Value> = emptyList(),
        ): SelectedHostCall {
            val checkpoint = frame.detachHostGraph(
                listOfNotNull(call.receiver) + call.arguments + suspendedCallerRoots,
            )
            try {
                val materializer = ArrayMaterializer(frame, classLoader)
                val receiver = if (call.kind == InvokeKind.DIRECT && call.target.isConstructor) {
                    null
                } else {
                    call.receiver?.let(materializer::select)
                }
                val arguments = call.arguments.map(materializer::select)
                return selectedCall(
                    call,
                    frame,
                    checkpoint,
                    materializer,
                    receiver,
                    Collections.unmodifiableList(arguments),
                    emulatorValues = null,
                )
            } catch (failure: Throwable) {
                frame.heap.restoreHostGraph(checkpoint)
                throw failure
            }
        }

        internal fun forEmulator(
            call: PreparedInvocation,
            frame: Frame,
            classLoader: ClassLoader,
            mode: EmulatorArgumentMode,
            suspendedCallerRoots: List<Value> = emptyList(),
        ): SelectedHostCall {
            val checkpoint = frame.detachHostGraph(
                listOfNotNull(call.receiver) + call.arguments + suspendedCallerRoots,
            )
            try {
                val materializer = ArrayMaterializer(frame, classLoader)
                fun select(value: Value, expectedType: VirtualType): EmulatorValue {
                    if (mode.exposesTrackedReferences) {
                        val ref = (value as? Value.Known)?.raw as? HeapRef
                        val tracked = ref?.let(frame::heapObject)
                        if (tracked is HeapObject.TrackedField) {
                            return TrackedFieldEmulatorValue(tracked.metadata, tracked.accessibility)
                        }
                        if (tracked != null && tracked !is HeapObject.HostObject) {
                            return EmulatorValue.TrackedReference(tracked.type)
                        }
                    }
                    if (frame.isHostMaterializable(value)) {
                        val type = value.type ?: expectedType
                        return EmulatorValue.Known(type, materializer.select(value))
                    }
                    if (!mode.allowsUnknownArguments) {
                        throw SelectedHostCallUnavailable(
                            "Accepted ordinary emulator cannot execute unmaterializable value $value",
                        )
                    }
                    return EmulatorValue.Unknown(value.type ?: expectedType)
                }

                val receiver = call.receiver?.let { select(it, call.target.definingClass) }
                val arguments = call.arguments.zip(call.parameterTypes) { value, type -> select(value, type) }
                val invocation = EmulatorInvocation(
                    call.target,
                    call.callerClass,
                    call.kind,
                    call.returnType,
                    call.parameterTypes,
                    receiver,
                    arguments,
                    frame.classes.statusSnapshot(),
                )
                return selectedCall(
                    call,
                    frame,
                    checkpoint,
                    materializer,
                    receiver = null,
                    arguments = emptyList(),
                    emulatorValues = EmulatorValues(invocation),
                )
            } catch (failure: Throwable) {
                frame.heap.restoreHostGraph(checkpoint)
                throw failure
            }
        }

        private fun selectedCall(
            call: PreparedInvocation,
            frame: Frame,
            checkpoint: HostGraphCheckpoint,
            materializer: ArrayMaterializer,
            receiver: Any?,
            arguments: List<Any?>,
            emulatorValues: EmulatorValues?,
        ): SelectedHostCall {
            materializer.includeAllMirrors()
            val sourceNodes = IdentityHashMap<Any, HostGraphNodeId>()
            val sourceRefs = IdentityHashMap<Any, HeapRef>()
            for ((id, raw) in frame.heap.hostGraphObjects()) {
                val nodeId = HostGraphNodeId(id)
                sourceNodes[raw] = nodeId
                frame.heap.refForNode(nodeId)?.let { sourceRefs[raw] = it }
            }
            return SelectedHostCall(
                call.target,
                call.callerClass,
                call.kind,
                call.returnType,
                Collections.unmodifiableList(call.parameterTypes.toList()),
                receiver,
                arguments,
                emulatorValues,
                checkpoint,
                sourceNodes,
                sourceRefs,
                materializer.arrays.toList(),
            )
        }
    }
}

internal class SelectedSynchronousCall(
    private val call: SelectedHostCall,
    private val executor: BoundSynchronousExecutor,
) {
    fun execute(): SynchronousCallResult = executor.execute(call)

    fun finalize(frame: Frame): HostGraphComparison = call.finalize(frame)

    fun installConstructedHostObject(frame: Frame, ref: HeapRef, raw: Any) =
        call.installConstructedHostObject(frame, ref, raw)

    /**
     * [value], returned by this call, as a guest value on [frame]. A host object the call was selected with keeps
     * its ref; a new mutable one is admitted. [comparison] is the call's finalized host-graph comparison.
     */
    fun importResult(frame: Frame, value: HostValue, comparison: HostGraphComparison): Value.Known {
        if (value !is HostValue.Reference) return frame.importHostValue(value)
        val raw = value.value
        requireHostOwned(raw)
        if (raw is Throwable) return importThrowable(frame, HostThrowable(value.type, raw), comparison)
        if (raw.isDirectHostImmutable()) return Value.Known(raw, value.type)
        trackedRef(frame, raw, value.type, comparison)?.let { return Value.Known(it, value.type) }
        return if (raw.javaClass.isArray) frame.admitReturnedHostArray(raw, value.type)
        else frame.admitReturnedHostValue(raw, value.type)
    }

    /** [throwable], thrown by this call, imported onto [frame] with the identity rules of [importResult]. */
    fun importThrowable(frame: Frame, throwable: HostThrowable, comparison: HostGraphComparison): Value.Known =
        frame.importHostThrowable(throwable) { raw, type -> trackedRef(frame, raw, type, comparison) }

    /** The ref [raw] keeps on [frame] because this call was selected with it, or null for a new host object. */
    private fun trackedRef(frame: Frame, raw: Any, type: VirtualType, comparison: HostGraphComparison): HeapRef? {
        retainedSourceRef(frame, raw, comparison)?.let { return it }
        if (comparison == HostGraphComparison.Equal) {
            call.sourceNodeId(raw)?.let { source ->
                return frame.heap.importHostNode(source, type, requireNotNull(frame.heap.hostObjectAt(source)))
            }
        }
        val source = call.sourceNodeId(raw)?.takeIf { frame.heap.hostObjectAt(it) === raw }
            ?: frame.heap.hostNodeId(raw)
        return source?.let { frame.heap.importHostNode(it, type, raw) }
    }

    private fun retainedSourceRef(frame: Frame, raw: Any, comparison: HostGraphComparison): HeapRef? {
        val sourceRef = call.sourceHeapRef(raw) ?: return null
        val nodeId = (frame.heapObject(sourceRef) as? HostBacked)?.knownNodeId ?: return null
        if (frame.heap.refForNode(nodeId) != sourceRef) return null
        val currentRaw = frame.heap.hostObjectAt(nodeId) ?: return null
        if (currentRaw === raw) return sourceRef
        return if (comparison == HostGraphComparison.Equal && call.sourceNodeId(raw) == nodeId) sourceRef else null
    }
}

internal class SelectedHostCallUnavailable(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

private class ArrayMaterializer(
    private val frame: Frame,
    private val classLoader: ClassLoader,
) {
    val arrays = mutableListOf<SelectedHostCall.MaterializedArray>()
    private val materializedByRef = mutableMapOf<HeapRef, Any>()

    fun includeAllMirrors() {
        for (mirror in frame.heap.mirroredArrays()) {
            if (materializedByRef.putIfAbsent(mirror.ref, mirror.raw) == null) {
                arrays += SelectedHostCall.MaterializedArray(
                    mirror.ref,
                    mirror.type,
                    mirror.length,
                    mirror.elements,
                    mirror.raw,
                )
            }
        }
    }

    fun select(value: Value): Any? {
        if (value !is Value.Known) {
            throw SelectedHostCallUnavailable("Accepted backend cannot execute unmaterializable value $value")
        }
        return selectedRaw(value.raw)
    }

    private fun selectedRaw(raw: Any?): Any? {
        if (raw !is HeapRef) {
            if (raw != null && !raw.isDirectHostImmutable()) {
                throw SelectedHostCallUnavailable("Mutable JVM values must be stored behind HeapRef")
            }
            return raw
        }
        return when (val heapObject = requireNotNull(frame.heapObject(raw)) { "No heap object for $raw" }) {
            is HeapObject.HostObject -> frame.heap.hostObjectAt(raw)
                ?: throw SelectedHostCallUnavailable("Host object $raw has unknown contents")
            is HeapObject.GuestThrowable -> frame.heap.hostObjectAt(raw)
                ?: throw SelectedHostCallUnavailable("Guest throwable $raw has unknown contents")
            is HeapObject.TrackedArray -> materialize(raw, heapObject)
            is HeapObject.TrackedField ->
                throw SelectedHostCallUnavailable("A tracked field has no direct JVM object")
            is HeapObject.TrackedObject ->
                throw SelectedHostCallUnavailable("A tracked guest object has no JVM object for host execution")
        }
    }

    private fun materialize(ref: HeapRef, tracked: HeapObject.TrackedArray): Any {
        materializedByRef[ref]?.let { return it }
        when (tracked.contents) {
            is HostGraphContents.Known -> frame.heap.hostObjectAt(ref)?.let {
                materializedByRef[ref] = it
                arrays += SelectedHostCall.MaterializedArray(ref, tracked.type, tracked.length, tracked.elements(), it)
                return it
            }
            HostGraphContents.Unknown ->
                throw SelectedHostCallUnavailable("Tracked array $ref has an unknown Java mirror")
            null -> Unit
        }
        val component = requireNotNull(tracked.type.componentType)
        val raw = try {
            ReflectArray.newInstance(component.hostClass(classLoader), tracked.length)
        } catch (e: ClassNotFoundException) {
            throw SelectedHostCallUnavailable("No JVM array component for $component", e)
        }
        materializedByRef[ref] = raw
        frame.heap.setArrayMirror(ref, raw)
        arrays += SelectedHostCall.MaterializedArray(ref, tracked.type, tracked.length, tracked.elements(), raw)
        for (index in 0 until tracked.length) {
            val element = tracked.element(index)
            if (element !is Value.Known) {
                throw SelectedHostCallUnavailable("Tracked array $ref has an unknown element at $index")
            }
            try {
                ReflectArray.set(raw, index, selectedRaw(element.raw))
            } catch (e: IllegalArgumentException) {
                throw SelectedHostCallUnavailable("Tracked array $ref has an incompatible element at $index", e)
            }
        }
        return raw
    }
}

internal fun Any?.isDirectHostImmutable(): Boolean = when (this) {
    null -> true
    else -> javaClass in DIRECT_HOST_IMMUTABLE_CLASSES
}

private val DIRECT_HOST_IMMUTABLE_CLASSES: Set<Class<*>> = setOf(
    String::class.java,
    Class::class.java,
    java.lang.Boolean::class.java,
    java.lang.Byte::class.java,
    java.lang.Character::class.java,
    java.lang.Short::class.java,
    java.lang.Integer::class.java,
    java.lang.Long::class.java,
    java.lang.Float::class.java,
    java.lang.Double::class.java,
    BigInteger::class.java,
    BigDecimal::class.java,
)

private fun validateCopiedElement(raw: Any?, component: VirtualType, hostComponent: Class<*>) {
    if (raw == null) {
        require(component.isReference) { "A primitive host array produced null" }
        return
    }
    requireHostOwned(raw)
    require(hostComponent.isPrimitive || hostComponent.isInstance(raw)) {
        "${raw.javaClass.name} is not compatible with ${hostComponent.name}"
    }
}
