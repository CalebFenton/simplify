package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * An array operand register, read as a reference the way ART's `GetVRegReference` reads it. The four cases
 * decide which of `NullPointerException` and an index exception are feasible, so every array op resolves its
 * operand here and only maps the cases to arms.
 */
internal sealed interface ArrayOperand {
    /** `Unknown` or `Top`: it may be null, and if not, any length. [tag] is null for `Top`. */
    class Unprovable(val tag: VirtualType?) : ArrayOperand

    /** Reads as a null reference ([readsAsNullReference]): a definite `NullPointerException`. */
    data object Null : ArrayOperand

    /**
     * Provably non-null with no tracked array behind it. Verified code cannot reach this, and ART's unchecked
     * `DownCast` gives no behavior to copy, so neither exception is feasible and the element is unknown.
     */
    class NotTracked(val tag: VirtualType) : ArrayOperand

    /** A tracked array; [component] is the object's own, never the register tag's. */
    class Tracked(val ref: HeapRef, val array: HeapObject.TrackedArray, val component: VirtualType) : ArrayOperand
}

internal fun Frame.readArrayOperand(register: Int): ArrayOperand {
    val value = read(register)
    if (value !is Value.Known) return ArrayOperand.Unprovable(value.type)
    if (value.readsAsNullReference) return ArrayOperand.Null
    val ref = value.raw as? HeapRef ?: return ArrayOperand.NotTracked(value.type)
    val array = arrayAt(ref) ?: return ArrayOperand.NotTracked(value.type)
    val component = checkNotNull(array.type.componentType) { "Tracked array ${array.type} at r$register has no component type" }
    return ArrayOperand.Tracked(ref, array, component)
}

/** An index operand against a tracked array. ART compares unsigned, so a negative index is out of bounds. */
internal sealed interface ArrayIndex {
    /** Not provable, so an in-bounds and an out-of-bounds access are both feasible. */
    data object Unprovable : ArrayIndex

    data class InBounds(val index: Int) : ArrayIndex

    /** [index] is null when the index is unprovable but the array is empty, so every index misses. */
    data class OutOfBounds(val index: Int?) : ArrayIndex
}

internal fun HeapObject.TrackedArray.indexOf(operand: Value): ArrayIndex {
    val index = (operand as? Value.Known)?.let { Coerce.toInt(it.raw) }
        ?: return if (length == 0) ArrayIndex.OutOfBounds(null) else ArrayIndex.Unprovable
    return if (index < 0 || index >= length) ArrayIndex.OutOfBounds(index) else ArrayIndex.InBounds(index)
}
