package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Collections
import java.util.IdentityHashMap

/** Selected host state for one accepted synchronous backend. */
class SelectedHostCall private constructor(
    val target: MethodDescriptor,
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
        frame.verifyHostGraphCheckpoint(checkpoint)
        require(frame.installConstructedHostObject(ref, raw)) {
            "Cannot install reflected constructor result at $ref"
        }
    }

    internal fun emulatorInvocation(): EmulatorInvocation =
        requireNotNull(emulatorValues) { "Direct Java backend values are not emulator boundary values" }.invocation

    /** Finalizes array copy-back and exact graph comparison on every non-fatal execution path. */
    internal fun finalize(frame: Frame): HostGraphComparison {
        frame.verifyHostGraphCheckpoint(checkpoint)
        try {
            copyArraysBack(frame)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            frame.finalizeHostGraph(checkpoint)
            throw failure
        }
        return frame.finalizeHostGraph(checkpoint)
    }

    /** Validates every mirror before applying any lattice update. */
    private fun copyArraysBack(frame: Frame) {
        val transaction = frame.heapTransactionCheckpoint()
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
                    frame.hostNodeId(raw) == null && seenNewRoots.put(raw, true) == null
                ) {
                    newRoots += raw
                }
            }
            val batchImports = frame.admitReturnedHostValues(newRoots)

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
            frame.applyArrayReplacementsAtomically(converted)
        } catch (failure: Throwable) {
            try {
                frame.rollbackFailedCopyback(transaction, arrays.map { it.ref }.toSet())
            } catch (fatal: VirtualMachineError) {
                throw fatal
            } catch (rollbackFailure: Throwable) {
                if (rollbackFailure !== failure) failure.addSuppressed(rollbackFailure)
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
            val nodeId = sourceNodeId(raw) ?: frame.hostNodeId(raw)
            val ref = if (nodeId != null) {
                frame.refForHostNode(nodeId) ?: frame.importHostNode(nodeId, component, raw)
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
                val receiver = if (call.kind == InvokeKind.DIRECT && call.target.name == "<init>") {
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
                frame.restoreHostGraph(checkpoint)
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
                    frame.classInitStatusSnapshot(),
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
                frame.restoreHostGraph(checkpoint)
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
            for ((id, raw) in frame.hostGraphObjects()) {
                val nodeId = HostGraphNodeId(id)
                sourceNodes[raw] = nodeId
                frame.refForHostNode(nodeId)?.let { sourceRefs[raw] = it }
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

    fun sourceNodeId(raw: Any): HostGraphNodeId? = call.sourceNodeId(raw)
    fun sourceHeapRef(raw: Any): HeapRef? = call.sourceHeapRef(raw)

    fun installConstructedHostObject(frame: Frame, ref: HeapRef, raw: Any) =
        call.installConstructedHostObject(frame, ref, raw)
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
        for (mirror in frame.mirroredArrays()) {
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
            is HeapObject.HostObject -> frame.hostObjectAt(raw)
                ?: throw SelectedHostCallUnavailable("Host object $raw has unknown contents")
            is HeapObject.GuestThrowable -> frame.hostObjectAt(raw)
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
        when (tracked.mirror) {
            is HostGraphContents.Known -> frame.hostObjectAt(ref)?.let {
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
            ReflectArray.newInstance(hostClass(component, classLoader), tracked.length)
        } catch (e: ClassNotFoundException) {
            throw SelectedHostCallUnavailable("No JVM array component for $component", e)
        }
        materializedByRef[ref] = raw
        frame.setArrayMirror(ref, raw)
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

private fun hostClass(type: VirtualType, classLoader: ClassLoader): Class<*> = when (type.descriptor) {
    "Z" -> Boolean::class.javaPrimitiveType!!
    "B" -> Byte::class.javaPrimitiveType!!
    "C" -> Char::class.javaPrimitiveType!!
    "S" -> Short::class.javaPrimitiveType!!
    "I" -> Int::class.javaPrimitiveType!!
    "J" -> Long::class.javaPrimitiveType!!
    "F" -> Float::class.javaPrimitiveType!!
    "D" -> Double::class.javaPrimitiveType!!
    else -> Class.forName(type.hostClassName(), false, classLoader)
}

private fun VirtualType.hostClassName(): String = when {
    descriptor.startsWith("[") -> descriptor.replace('/', '.')
    else -> descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
}

internal fun virtualTypeOfHostClass(hostClass: Class<*>): VirtualType {
    val name = hostClass.name.replace('.', '/')
    return VirtualType.of(if (name.startsWith("[")) name else "L$name;")
}

internal fun virtualTypeOfHostFieldClass(hostClass: Class<*>): VirtualType = when (hostClass) {
    java.lang.Boolean.TYPE -> VirtualType.of("Z")
    java.lang.Byte.TYPE -> VirtualType.of("B")
    java.lang.Character.TYPE -> VirtualType.of("C")
    java.lang.Short.TYPE -> VirtualType.of("S")
    java.lang.Integer.TYPE -> VirtualType.of("I")
    java.lang.Long.TYPE -> VirtualType.of("J")
    java.lang.Float.TYPE -> VirtualType.of("F")
    java.lang.Double.TYPE -> VirtualType.of("D")
    else -> virtualTypeOfHostClass(hostClass)
}
