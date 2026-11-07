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
         * Parses and validates [descriptor]; throws [IllegalArgumentException] if malformed or deeper than
         * [MAX_ARRAY_DIMENSIONS].
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
                'L' -> rest.length > 2 && rest.endsWith(";") &&
                    rest.substring(1, rest.length - 1).none { it == ';' || it == '.' } // dex: never '.'
                else -> false
            }
        }
    }
}
