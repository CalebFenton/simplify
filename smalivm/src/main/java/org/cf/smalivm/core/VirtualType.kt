package org.cf.smalivm.core

/**
 * A validated Dalvik type descriptor (`I`, `J`, `Ljava/lang/String;`, `[[I`, ...).
 *
 * Replaces the old engine's raw `type: String` tag (spec §4.1): construction validates
 * against the descriptor grammar and throws instead of repairing. Carries *shape*
 * classification only (primitive/wide/reference/array/void); hierarchy questions
 * (assignability, common supertype) need a class manager and arrive with the spine.
 *
 * Equality is structural on [descriptor].
 */
class VirtualType private constructor(val descriptor: String) {
    val isVoid: Boolean get() = descriptor == "V"
    val isPrimitive: Boolean get() = descriptor.length == 1 && descriptor[0] in PRIMITIVE_CODES
    /** Wide primitives occupy two register slots (spec §4.1). */
    val isWide: Boolean get() = descriptor == "J" || descriptor == "D"
    val isArray: Boolean get() = descriptor[0] == '['
    /** Reference kinds: class types and arrays (arrays of primitives included). */
    val isReference: Boolean get() = isArray || descriptor[0] == 'L'
    /** The element type for arrays (`[[I` -> `[I`); null for non-arrays. */
    val componentType: VirtualType? get() = if (isArray) VirtualType(descriptor.substring(1)) else null

    override fun equals(other: Any?): Boolean = other is VirtualType && other.descriptor == descriptor
    override fun hashCode(): Int = descriptor.hashCode()
    override fun toString(): String = descriptor

    companion object {
        private const val PRIMITIVE_CODES = "ZBSCIJFD"

        /** Dex format, TypeDescriptor: an array descriptor has at most 255 dimensions. */
        internal const val MAX_ARRAY_DIMENSIONS = 255

        val OBJECT: VirtualType = VirtualType("Ljava/lang/Object;")

        /**
         * Parses and validates [descriptor] against the dex format's TypeDescriptor grammar; throws
         * [IllegalArgumentException] if malformed or deeper than [MAX_ARRAY_DIMENSIONS].
         */
        fun of(descriptor: String): VirtualType {
            require(isValid(descriptor)) { "Malformed type descriptor: '$descriptor'" }
            return VirtualType(descriptor)
        }

        private fun isValid(descriptor: String): Boolean {
            var i = 0
            while (i < descriptor.length && descriptor[i] == '[') i++
            if (i == descriptor.length) return false // dangling '[' or empty
            if (i > MAX_ARRAY_DIMENSIONS) return false
            val rest = descriptor.substring(i)
            return when (rest[0]) {
                'V' -> rest == "V" && i == 0 // void is never an array component
                in PRIMITIVE_CODES -> rest.length == 1
                'L' -> rest.length > 2 && rest.endsWith(";") && isFullClassName(rest, 1, rest.length - 1)
                else -> false
            }
        }

        /** Dex format FullClassName: one or more non-empty SimpleNames separated by `/`. */
        private fun isFullClassName(text: String, start: Int, end: Int): Boolean {
            var segmentIsEmpty = true
            var i = start
            while (i < end) {
                val codePoint = text.codePointAt(i)
                when {
                    codePoint == '/'.code -> if (segmentIsEmpty) return false else segmentIsEmpty = true
                    isSimpleNameChar(codePoint) -> segmentIsEmpty = false
                    else -> return false
                }
                i += Character.charCount(codePoint)
            }
            return !segmentIsEmpty
        }

        /**
         * Dex format SimpleNameChar, admitting the DEX 040 additions (space, U+00A0, U+2000..U+200A, U+202F)
         * whatever the input's version. A lone surrogate is in none of the ranges.
         */
        private fun isSimpleNameChar(codePoint: Int): Boolean = when (codePoint) {
            in 'A'.code..'Z'.code, in 'a'.code..'z'.code, in '0'.code..'9'.code -> true
            ' '.code, '$'.code, '-'.code, '_'.code -> true
            in 0x00a0..0x1fff, in 0x2000..0x200a, in 0x2010..0x2027, 0x202f, in 0x2030..0xd7ff -> true
            in 0xe000..0xffef, in 0x10000..0x10ffff -> true
            else -> false
        }
    }
}

/** The JVM binary name of a reference type, in the form `Class.forName` accepts. */
internal fun VirtualType.hostClassName(): String = when {
    isArray -> descriptor.replace('/', '.')
    else -> descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
}

/** The host class for [type]; reference types load through [classLoader] without initialization. */
internal fun VirtualType.hostClass(classLoader: ClassLoader): Class<*> = when (descriptor) {
    "Z" -> java.lang.Boolean.TYPE
    "B" -> java.lang.Byte.TYPE
    "C" -> java.lang.Character.TYPE
    "S" -> java.lang.Short.TYPE
    "I" -> java.lang.Integer.TYPE
    "J" -> java.lang.Long.TYPE
    "F" -> java.lang.Float.TYPE
    "D" -> java.lang.Double.TYPE
    "V" -> java.lang.Void.TYPE
    else -> Class.forName(hostClassName(), false, classLoader)
}

/** The descriptor of [hostClass], primitives and void included. */
internal fun virtualTypeOfHostClass(hostClass: Class<*>): VirtualType = when (hostClass) {
    java.lang.Boolean.TYPE -> VirtualType.of("Z")
    java.lang.Byte.TYPE -> VirtualType.of("B")
    java.lang.Character.TYPE -> VirtualType.of("C")
    java.lang.Short.TYPE -> VirtualType.of("S")
    java.lang.Integer.TYPE -> VirtualType.of("I")
    java.lang.Long.TYPE -> VirtualType.of("J")
    java.lang.Float.TYPE -> VirtualType.of("F")
    java.lang.Double.TYPE -> VirtualType.of("D")
    java.lang.Void.TYPE -> VirtualType.of("V")
    else -> {
        val name = hostClass.name.replace('.', '/')
        VirtualType.of(if (hostClass.isArray) name else "L$name;")
    }
}
