package org.cf.smalivm.differential

import java.util.IdentityHashMap

/** Stable presentation shared by the core projection and the ART recording. */
object ResultProjection {

    fun reprOf(raw: Any): String = reprOf(raw, IdentityHashMap())

    fun exceptionEntry(binaryClassName: String, message: String?): String = "$binaryClassName:$message"

    /** Only the observables the ART recording holds: exception types, completion, and the returned value. */
    fun deviceComparable(result: NormalizedResult): NormalizedResult = NormalizedResult(
        registers = emptyMap(),
        reachedAddresses = emptySet(),
        unhandledExceptions = result.unhandledExceptions.map { it.substringBefore(':') }.distinct().sorted(),
        aborted = result.aborted,
        returnValue = result.returnValue?.let { it.copy(type = binaryName(it.type)) },
    )

    private fun binaryName(type: String?): String? = when {
        type == null -> null
        type.startsWith("L") && type.endsWith(";") -> type.substring(1, type.length - 1).replace('/', '.')
        else -> type
    }

    private fun reprOf(raw: Any, visiting: IdentityHashMap<Any, Boolean>): String = when {
        raw.javaClass.isArray -> arrayRepr(raw, visiting)
        hasNoOverriddenToString(raw) -> "<instance ${raw.javaClass.name}>"
        else -> raw.toString()
    }

    private fun hasNoOverriddenToString(raw: Any): Boolean =
        raw.javaClass.getMethod("toString").declaringClass == java.lang.Object::class.java

    private fun arrayRepr(raw: Any, visiting: IdentityHashMap<Any, Boolean>): String {
        if (visiting.containsKey(raw)) return "[...]"
        visiting[raw] = true
        try {
            val len = java.lang.reflect.Array.getLength(raw)
            return (0 until len).map { index ->
                java.lang.reflect.Array.get(raw, index)?.let { reprOf(it, visiting) } ?: "null"
            }.toString()
        } finally {
            visiting.remove(raw)
        }
    }
}
