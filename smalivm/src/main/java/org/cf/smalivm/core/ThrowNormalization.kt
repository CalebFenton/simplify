package org.cf.smalivm.core

/**
 * Turns one committed throw event into its [ThrowPath] on the arm-owned [frame], exactly once. A fresh recipe is
 * allocated; an existing value is classified: null throws `NullPointerException`, a value that is not a throwable
 * throws `InternalError`, a host throwable is imported, and an unknown value becomes a symbolic subject.
 */
internal fun normalizeThrow(evidence: ThrowEvidence, frame: Frame, hierarchy: ThrowableHierarchy): ThrowPath {
    val subject = when (evidence) {
        is ThrowEvidence.Fresh -> evidence.recipe.materialize(frame, hierarchy)
        is ThrowEvidence.Existing -> when (val value = evidence.frameValue) {
            is FrameValue.Plain -> existingThrowSubject(value.value, frame, hierarchy)
            is FrameValue.RefinedThrowable -> ThrownSubject.fromFrameValue(value, frame, hierarchy)
        }
    }
    subject.concreteRef?.let { ref ->
        require(frame.heapObject(ref) != null) { "Normalized throw ref $ref is absent from its arm frame" }
    }
    return ThrowPath.create(frame, subject, hierarchy)
}

private fun existingThrowSubject(value: Value, frame: Frame, hierarchy: ThrowableHierarchy): ThrownSubject {
    if (value !is Value.Known) return ThrownSubject.symbolic(value, symbolicThrowableDomain(value, hierarchy))
    if (value.readsAsNullReference) return GuestThrowables.nullPointer().materialize(frame, hierarchy)
    val raw = value.raw
    if (raw is Throwable) {
        val imported = frame.importHostThrowable(HostThrowable(virtualTypeOfHostClass(raw.javaClass), raw))
        return ThrownSubject.fromHeap(imported, frame, hierarchy)
    }
    val ref = raw as? HeapRef ?: return nonThrowable(value, frame, hierarchy)
    val obj = frame.heapObject(ref) ?: return nonThrowable(value, frame, hierarchy)
    val host = if (obj is HeapObject.HostObject) frame.heap.hostObjectAt(ref) else null
    if (host != null && host !is Throwable) return nonThrowable(value, frame, hierarchy)
    if (hierarchy.facts(obj.type).throwableRelation == TypeRelation.NO) return nonThrowable(value, frame, hierarchy)
    return ThrownSubject.fromHeap(value, frame, hierarchy)
}

private fun symbolicThrowableDomain(value: Value, hierarchy: ThrowableHierarchy): ThrowableDomain {
    val sourceType = value.type
    if (sourceType == null || !sourceType.isReference) return ThrowableDomain.any()
    return when (hierarchy.facts(sourceType).throwableRelation) {
        TypeRelation.YES -> ThrowableDomain.constrainedTo(sourceType, hierarchy)
        TypeRelation.NO -> ThrowableDomain.any()
        TypeRelation.UNKNOWN -> ThrowableDomain.unresolved(sourceType, hierarchy)
    }
}

private fun nonThrowable(value: Value.Known, frame: Frame, hierarchy: ThrowableHierarchy): ThrownSubject =
    GuestThrowables.internalError("Throwing '${value.type}' that is not an instance of Throwable")
        .materialize(frame, hierarchy)
