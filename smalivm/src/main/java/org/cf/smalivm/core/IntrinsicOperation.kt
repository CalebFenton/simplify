package org.cf.smalivm.core

/**
 * A heap operation an emulator requests without seeing heap identity or lattice values. The scheduler applies it
 * to the invoke node's frame through [applyTo].
 */
internal sealed interface IntrinsicOperation {
    data class AllocateTrackedField(val metadata: TrackedFieldValue) : IntrinsicOperation
    data object CloneArrayReceiver : IntrinsicOperation
    data class InitializeEnumReceiver(
        val name: EmulatorValue,
        val ordinal: EmulatorValue,
    ) : IntrinsicOperation
    data class ReadVirtualField(val field: TrackedFieldValue) : IntrinsicOperation
    data object ReadThrowableCause : IntrinsicOperation
    data class SetTrackedFieldAccessible(
        val metadata: TrackedFieldValue,
        val accessibility: FieldAccessibility,
    ) : IntrinsicOperation
    data class ReturnUnknown(val type: VirtualType) : IntrinsicOperation

    /** Applies this operation's heap writes to [frame] and returns the call's result, or null for a void call. */
    fun applyTo(frame: Frame, invocation: PreparedInvocation): Value? = when (this) {
        is AllocateTrackedField -> frame.allocateTrackedField(metadata)
        CloneArrayReceiver -> {
            val ref = invocation.receiverRef("Tracked-array clone")
            val source = frame.heap.arrayAt(ref) ?: error("Tracked-array clone receiver $ref is not an array")
            frame.allocateArray(source.type, source.elements().toTypedArray())
        }
        is InitializeEnumReceiver -> {
            val ref = invocation.receiverRef("Enum initialization")
            require(frame.heap.setField(ref, ENUM_NAME, name.toGuestValue(STRING))) {
                "Enum initialization receiver $ref has no name field"
            }
            require(frame.heap.setField(ref, ENUM_ORDINAL, ordinal.toGuestValue(INT))) {
                "Enum initialization receiver $ref has no ordinal field"
            }
            null
        }
        is ReadVirtualField -> {
            val value = if (field.storage == FieldStorage.INSTANCE) {
                val receiver = invocation.arguments.singleOrNull()
                    ?: error("Virtual instance field read has no receiver argument")
                val ref = (receiver as? Value.Known)?.raw as? HeapRef
                    ?: error("Virtual field read has no known receiver ref")
                frame.getField(ref, field.descriptor)
            } else {
                frame.getStaticField(field.declaringType, field.descriptor)
            }
            fieldGetResult(value, field.fieldType)
        }
        ReadThrowableCause -> when (val receiver = invocation.receiver) {
            is Value.Known -> {
                val ref = receiver.raw as? HeapRef
                if (ref == null || frame.heapObject(ref) == null) Value.Unknown(GuestThrowables.THROWABLE)
                else frame.throwableCause(ref)
            }
            is Value.Unknown, Value.Top, null -> Value.Unknown(GuestThrowables.THROWABLE)
        }
        is SetTrackedFieldAccessible -> {
            val ref = invocation.receiverRef("Field.setAccessible")
            val current = frame.heap.trackedFieldAt(ref)
                ?: error("Field.setAccessible receiver $ref is not a tracked field")
            require(current.metadata == metadata) {
                "Field.setAccessible metadata changed between selection and application"
            }
            require(frame.heap.setTrackedFieldAccessibility(ref, accessibility)) {
                "Field.setAccessible could not update $ref"
            }
            null
        }
        is ReturnUnknown -> Value.Unknown(type)
    }
}

private const val ENUM_NAME = "Ljava/lang/Enum;->name:Ljava/lang/String;"
private const val ENUM_ORDINAL = "Ljava/lang/Enum;->ordinal:I"
private val STRING = VirtualType.of("Ljava/lang/String;")
private val INT = VirtualType.of("I")

private fun PreparedInvocation.receiverRef(operation: String): HeapRef =
    (receiver as? Value.Known)?.raw as? HeapRef ?: error("$operation has no known receiver ref")

/** An emulator's enum name or ordinal as the field's guest value; anything else is unknown. */
private fun EmulatorValue.toGuestValue(expectedType: VirtualType): Value = when (this) {
    is EmulatorValue.Known -> when {
        expectedType == STRING && (javaValue == null || javaValue is String) -> Value.Known(javaValue, expectedType)
        expectedType == INT && javaValue is Int -> Value.Known(javaValue, expectedType)
        else -> Value.Unknown(expectedType)
    }
    is TrackedFieldEmulatorValue, is EmulatorValue.TrackedReference, is EmulatorValue.Unknown ->
        Value.Unknown(expectedType)
}

/** An emulator's request for an [IntrinsicOperation]. */
internal data class IntrinsicCallResult(val operation: IntrinsicOperation) : SynchronousCallResult
