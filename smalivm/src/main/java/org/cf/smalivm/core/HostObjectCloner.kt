package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.IdentityHashMap
import sun.misc.Unsafe

/** Deep-clones one complete path-local host graph while preserving every stable node id. */
class HostObjectCloner internal constructor() {
    private val unsafe: Unsafe = run {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        field.get(null) as Unsafe
    }

    internal fun cloneGraph(state: HostGraphState): HostGraphClone {
        if (state.nodeCount == 0) return HostGraphClone(state, 0)
        return try {
            val seen = IdentityHashMap<Any, Any>()
            val cloned = state.objectsInIdOrder().map { deepClone(it, seen) }
            HostGraphClone(state.clonedWith(cloned), seen.size)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (failure: Throwable) {
            throw HostGraphRepresentationException("Cannot clone the selected host graph", failure)
        }
    }

    @Suppress("DEPRECATION")
    private fun deepClone(raw: Any, seen: IdentityHashMap<Any, Any>): Any {
        if (raw.isDirectHostImmutable()) return raw
        seen[raw]?.let { return it }
        if (raw.javaClass.isArray) return cloneArray(raw, seen)

        // JDK reflection filters Method's VM-owned fields from getDeclaredFields().
        // Allocate a real member copy, then preserve its mutable accessibility and visible graph.
        val cloned = if (raw is Method) {
            raw.declaringClass.declaredMethods.first { it == raw }.apply {
                isAccessible = raw.isAccessible
            }
        } else unsafe.allocateInstance(raw.javaClass)
        seen[raw] = cloned
        for (field in instanceFields(raw.javaClass)) copyField(field, raw, cloned, seen)
        return cloned
    }

    private fun cloneArray(raw: Any, seen: IdentityHashMap<Any, Any>): Any {
        val length = ReflectArray.getLength(raw)
        val cloned = ReflectArray.newInstance(raw.javaClass.componentType, length)
        seen[raw] = cloned
        if (raw.javaClass.componentType.isPrimitive) {
            System.arraycopy(raw, 0, cloned, 0, length)
        } else {
            for (index in 0 until length) {
                val element = ReflectArray.get(raw, index)
                ReflectArray.set(cloned, index, if (element == null) null else deepClone(element, seen))
            }
        }
        return cloned
    }

    @Suppress("DEPRECATION")
    private fun copyField(
        field: Field,
        source: Any,
        target: Any,
        seen: IdentityHashMap<Any, Any>,
    ) {
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
                unsafe.putObject(target, offset, if (value == null) null else deepClone(value, seen))
            }
        }
    }
}

internal data class HostGraphClone(
    val state: HostGraphState,
    val objectsCloned: Int,
)

internal class HostGraphRepresentationException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
