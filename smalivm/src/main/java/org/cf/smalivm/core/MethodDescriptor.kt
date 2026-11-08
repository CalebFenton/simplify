package org.cf.smalivm.core

/** Which Dalvik invoke instruction family a call site uses; resolution rules differ per kind (spec §5.3). */
enum class InvokeKind { STATIC, DIRECT, SUPER, VIRTUAL, INTERFACE }

/**
 * An invoke target as written in the bytecode: the statically referenced class, the method name, and the dex
 * proto such as `(ILjava/lang/String;)V`. The `Invoker` resolves it to a method per [InvokeKind] rules; this
 * type carries no resolution result.
 */
data class MethodDescriptor(val definingClass: VirtualType, val name: String, val proto: String) {
    /**
     * [name] followed by [proto], e.g. `bar(I)V`: the key the class model looks a member up by
     * ([ClassIndex.lookupMethod]). It names no class, unlike `MethodView.signature`.
     */
    val nameAndProto: String get() = "$name$proto"

    /** The parameter types [proto] declares. Throws [IllegalArgumentException] for a malformed proto. */
    internal fun parameterTypes(): List<VirtualType> {
        val parameters = proto.substringAfter('(').substringBefore(')')
        val result = mutableListOf<VirtualType>()
        var index = 0
        while (index < parameters.length) {
            val start = index
            while (index < parameters.length && parameters[index] == '[') index++
            require(index < parameters.length) { "Malformed method proto $proto" }
            index = if (parameters[index] == 'L') parameters.indexOf(';', index) + 1 else index + 1
            require(index > start) { "Malformed method proto $proto" }
            result += VirtualType.of(parameters.substring(start, index))
        }
        return result
    }

    /**
     * The invoke register words of a call to this method: the receiver first unless [kind] is static, then each
     * parameter, a wide parameter taking two words.
     */
    internal fun argumentWords(kind: InvokeKind): ArgumentWords {
        val arguments = ArrayList<ArgumentWords.Argument>()
        var word = 0
        if (kind != InvokeKind.STATIC) arguments += ArgumentWords.Argument(definingClass, word++)
        for (type in parameterTypes()) {
            arguments += ArgumentWords.Argument(type, word)
            word += if (type.isWide) 2 else 1
        }
        return ArgumentWords(arguments, word)
    }
}

/** Logical arguments of one invoke and the first register word each occupies. */
internal class ArgumentWords(val arguments: List<Argument>, val wordCount: Int) {
    class Argument(val type: VirtualType, val word: Int)

    /** The type of the argument occupying [word], or null past the last word. */
    fun typeAt(word: Int): VirtualType? = arguments.lastOrNull { it.word <= word && word < wordCount }?.type
}
