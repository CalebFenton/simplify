package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.IdentityHashMap
import sun.misc.Unsafe

/** The JVM's raw field access, which reads and writes host fields without running host code. */
internal object HostUnsafe {
    val unsafe: Unsafe = run {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        field.get(null) as Unsafe
    }
}

/**
 * Deep-clones one complete path-local host graph while preserving every stable node id.
 *
 * Copies through a worklist, never the host call stack, because a guest can build chains deeper than the host stack.
 */
internal fun cloneHostGraph(state: HostGraphState): HostGraphState {
    if (state.nodeCount == 0) return state
    return hostBoundary({ throw HostGraphRepresentationException("Cannot clone the selected host graph", it) }) {
        val copies = IdentityHashMap<Any, Any>()
        val unfilled = ArrayList<Any>()
        val cloned = state.objectsInIdOrder().map { copyOf(it, copies, unfilled) }
        while (unfilled.isNotEmpty()) {
            val copy = unfilled.removeAt(unfilled.lastIndex)
            val raw = unfilled.removeAt(unfilled.lastIndex)
            fill(raw, copy, copies, unfilled)
        }
        state.clonedWith(cloned)
    }
}

/** Returns the copy of [raw]; a first sight allocates it empty and appends `raw, copy` to [unfilled]. */
@Suppress("DEPRECATION")
private fun copyOf(raw: Any, copies: IdentityHashMap<Any, Any>, unfilled: MutableList<Any>): Any {
    if (raw.isDirectHostImmutable()) return raw
    copies[raw]?.let { return it }
    val copy = when {
        raw.javaClass.isArray -> ReflectArray.newInstance(raw.javaClass.componentType, ReflectArray.getLength(raw))
        // JDK reflection filters a reflected method's or constructor's VM-owned fields (its class, slot, modifiers)
        // from getDeclaredFields(), so a field-by-field copy is a broken member. Allocate a real member copy, then
        // preserve its mutable accessibility and visible graph.
        raw is Method -> raw.declaringClass.declaredMethods.first { it == raw }.apply {
            isAccessible = raw.isAccessible
        }
        raw is Constructor<*> -> raw.declaringClass.declaredConstructors.first { it == raw }.apply {
            isAccessible = raw.isAccessible
        }
        else -> HostUnsafe.unsafe.allocateInstance(raw.javaClass)
    }
    copies[raw] = copy
    unfilled.add(raw)
    unfilled.add(copy)
    return copy
}

private fun fill(raw: Any, copy: Any, copies: IdentityHashMap<Any, Any>, unfilled: MutableList<Any>) {
    if (!raw.javaClass.isArray) {
        for (field in instanceFields(raw.javaClass)) copyField(field, raw, copy, copies, unfilled)
    } else if (raw.javaClass.componentType.isPrimitive) {
        System.arraycopy(raw, 0, copy, 0, ReflectArray.getLength(raw))
    } else {
        for (index in 0 until ReflectArray.getLength(raw)) {
            val element = ReflectArray.get(raw, index)
            ReflectArray.set(copy, index, if (element == null) null else copyOf(element, copies, unfilled))
        }
    }
}

@Suppress("DEPRECATION")
private fun copyField(
    field: Field,
    source: Any,
    target: Any,
    copies: IdentityHashMap<Any, Any>,
    unfilled: MutableList<Any>,
) {
    val unsafe = HostUnsafe.unsafe
    val offset = unsafe.objectFieldOffset(field)
    when (field.type) {
        java.lang.Boolean.TYPE -> unsafe.putBoolean(target, offset, unsafe.getBoolean(source, offset))
        java.lang.Byte.TYPE -> unsafe.putByte(target, offset, unsafe.getByte(source, offset))
        java.lang.Character.TYPE -> unsafe.putChar(target, offset, unsafe.getChar(source, offset))
        java.lang.Short.TYPE -> unsafe.putShort(target, offset, unsafe.getShort(source, offset))
        java.lang.Integer.TYPE -> unsafe.putInt(target, offset, unsafe.getInt(source, offset))
        java.lang.Long.TYPE -> unsafe.putLong(target, offset, unsafe.getLong(source, offset))
        java.lang.Float.TYPE -> unsafe.putFloat(target, offset, unsafe.getFloat(source, offset))
        java.lang.Double.TYPE -> unsafe.putDouble(target, offset, unsafe.getDouble(source, offset))
        else -> {
            val value = unsafe.getObject(source, offset)
            unsafe.putObject(target, offset, if (value == null) null else copyOf(value, copies, unfilled))
        }
    }
}

internal class HostGraphRepresentationException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
