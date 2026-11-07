package org.cf.smalivm.differential


/** Stable presentation shared by the core and the independent device recordings. */
object ResultProjection {

    fun reprOf(raw: Any): String = reprOf(raw, java.util.IdentityHashMap())

    fun exceptionEntry(binaryClassName: String, message: String?): String = "$binaryClassName:$message"

    /** Only observables actually present in the ART recording; preserve completion and real returns. */
    fun deviceComparable(result: NormalizedResult): NormalizedResult = NormalizedResult(
        registers = emptyMap(),
        reachedAddresses = emptySet(),
        unhandledExceptions = result.unhandledExceptions.map(::strippedExceptionType).distinct().sorted(),
        aborted = result.aborted,
        returnValue = result.returnValue?.let { it.copy(type = binaryName(it.type)) },
    )

    fun strippedExceptionType(entry: String): String = entry.substringBefore(':')

    fun binaryName(type: String?): String? = when {
        type == null -> null
        type.startsWith("L") && type.endsWith(";") -> type.substring(1, type.length - 1).replace('/', '.')
        else -> type
    }

    fun listRepr(parts: List<String>): String = parts.toString()



    private fun reprOf(raw: Any, visiting: java.util.IdentityHashMap<Any, Boolean>): String = when {
        raw.javaClass.isArray -> arrayRepr(raw, visiting)
        hasNoOverriddenToString(raw) -> "<instance ${raw.javaClass.name}>"
        else -> raw.toString()
    }

    private fun hasNoOverriddenToString(raw: Any): Boolean =
        raw.javaClass.getMethod("toString").declaringClass == java.lang.Object::class.java

    private fun arrayRepr(raw: Any, visiting: java.util.IdentityHashMap<Any, Boolean>): String {
        if (visiting.containsKey(raw)) return "[...]"
        visiting[raw] = true
        try {
            val len = java.lang.reflect.Array.getLength(raw)
            return listRepr((0 until len).map { elementRepr(java.lang.reflect.Array.get(raw, it), visiting) })
        } finally {
            visiting.remove(raw)
        }
    }

    private fun elementRepr(element: Any?, visiting: java.util.IdentityHashMap<Any, Boolean>): String =
        if (element == null) "null" else reprOf(element, visiting)
}
