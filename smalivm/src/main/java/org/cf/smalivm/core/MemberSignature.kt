package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference

/** Which Dalvik invoke instruction family a call site uses; resolution rules differ per kind. */
internal enum class InvokeKind { STATIC, DIRECT, SUPER, VIRTUAL, INTERFACE }

/**
 * A method or field signature that does not follow the smali member reference syntax. [offset] is how far into
 * [input] reading got before [reason] stopped it, so a caller trying several grammars can report the one that
 * matched the most text.
 */
public class MalformedSignatureException internal constructor(
    internal val input: String,
    public val offset: Int,
    public val reason: String,
    cause: Throwable? = null,
) : IllegalArgumentException("Malformed signature '$input': $reason", cause)

/**
 * A method as dex references it: the class the reference names, the method name, the parameter types and the
 * return type. [toString] is the smali spelling `Lpkg/Owner;->name(I[J)V`, which [parse] reads back; two signatures
 * are equal when that spelling is.
 */
public class MethodSignature(
    public val definingClass: VirtualType,
    internal val name: String,
    parameterTypes: List<VirtualType>,
    internal val returnType: VirtualType,
) : Comparable<MethodSignature> {
    internal val parameterTypes: List<VirtualType> = java.util.Collections.unmodifiableList(parameterTypes.toList())

    /** The dex proto, `(I[J)V`. */
    internal val proto: String = this.parameterTypes.joinToString("", "(", ")") { it.descriptor } + returnType.descriptor

    /** [name] followed by [proto], e.g. `bar(I)V`: the key one class declares a method under. */
    internal val nameAndProto: String = name + proto

    private val text = "${definingClass.descriptor}->$nameAndProto"

    init {
        val nameOffset = definingClass.descriptor.length + 2
        if (!isMemberName(name)) throw MalformedSignatureException(text, nameOffset, "'$name' is not a member name")
        if (this.parameterTypes.any(VirtualType::isVoid)) {
            throw MalformedSignatureException(text, nameOffset + name.length, "a parameter type cannot be V")
        }
    }

    /** Parses [proto], `(I[J)V`, into parameter and return types; throws [MalformedSignatureException]. */
    public constructor(definingClass: VirtualType, name: String, proto: String) :
        this(definingClass, name, SignatureReader(proto).wholeProto())

    private constructor(definingClass: VirtualType, name: String, proto: ParsedProto) :
        this(definingClass, name, proto.parameterTypes, proto.returnType)

    /** An instance initializer, `<init>`. */
    public val isConstructor: Boolean get() = name == CONSTRUCTOR_NAME

    /** A static initializer, `<clinit>`. */
    internal val isClassInitializer: Boolean get() = name == CLASS_INITIALIZER_NAME

    /**
     * The invoke register words of a call to this method: the receiver first unless [kind] is static, then each
     * parameter, a wide parameter taking two words.
     */
    internal fun argumentWords(kind: InvokeKind): ArgumentWords {
        val arguments = ArrayList<ArgumentWords.Argument>()
        var word = 0
        if (kind != InvokeKind.STATIC) arguments += ArgumentWords.Argument(definingClass, word++)
        for (type in parameterTypes) {
            arguments += ArgumentWords.Argument(type, word)
            word += if (type.isWide) 2 else 1
        }
        return ArgumentWords(arguments, word)
    }

    override fun equals(other: Any?): Boolean = other is MethodSignature && other.text == text
    override fun hashCode(): Int = text.hashCode()
    override fun compareTo(other: MethodSignature): Int = text.compareTo(other.text)
    override fun toString(): String = text

    public companion object {
        internal const val CONSTRUCTOR_NAME = "<init>"
        internal const val CLASS_INITIALIZER_NAME = "<clinit>"

        /** Parses `Lpkg/Owner;->name(params)return`; throws [MalformedSignatureException]. */
        public fun parse(text: String): MethodSignature = SignatureReader(text).wholeMethod(owner = null)

        /** Parses `name(params)return` as a member of [owner]; throws [MalformedSignatureException]. */
        public fun parseMember(owner: VirtualType, text: String): MethodSignature = SignatureReader(text).wholeMethod(owner)

        /** The signature of a dexlib2 method reference; throws [MalformedSignatureException] for a malformed one. */
        public fun of(reference: MethodReference): MethodSignature {
            val input = "${reference.definingClass}->${reference.name}" +
                reference.parameterTypes.joinToString("", "(", ")") + reference.returnType
            return MethodSignature(
                referenceType(reference.definingClass, input),
                reference.name,
                reference.parameterTypes.map { referenceType(it, input) },
                referenceType(reference.returnType, input),
            )
        }

        internal fun classInitializer(type: VirtualType): MethodSignature =
            MethodSignature(type, CLASS_INITIALIZER_NAME, emptyList(), VirtualType.of("V"))
    }
}

/**
 * A field as dex references it: the class the reference names, the field name and its type. [toString] is the
 * smali spelling `Lpkg/Owner;->name:I`, which [parse] reads back.
 */
public data class FieldSignature(val definingClass: VirtualType, val name: String, val type: VirtualType) {
    init {
        if (!isMemberName(name) || type.isVoid) {
            throw MalformedSignatureException(
                toString(),
                definingClass.descriptor.length + 2,
                if (type.isVoid) "a field type cannot be V" else "'$name' is not a member name",
            )
        }
    }

    internal fun toFieldReference(): FieldReference = ImmutableFieldReference(definingClass.descriptor, name, type.descriptor)

    override fun toString(): String = ClassIndex.fieldDescriptorOf(definingClass, name, type.descriptor)

    public companion object {
        /** Parses `Lpkg/Owner;->name:type`; throws [MalformedSignatureException]. */
        public fun parse(text: String): FieldSignature = SignatureReader(text).wholeField()

        /** The signature of a dexlib2 field reference; throws [MalformedSignatureException] for a malformed one. */
        public fun of(reference: FieldReference): FieldSignature {
            val input = "${reference.definingClass}->${reference.name}:${reference.type}"
            return FieldSignature(
                referenceType(reference.definingClass, input),
                reference.name,
                referenceType(reference.type, input),
            )
        }
    }
}

/** Logical arguments of one invoke and the first register word each occupies. */
internal class ArgumentWords(val arguments: List<Argument>, val wordCount: Int) {
    class Argument(val type: VirtualType, val word: Int)

    /** The type of the argument occupying [word], or null past the last word. */
    fun typeAt(word: Int): VirtualType? = arguments.lastOrNull { it.word <= word && word < wordCount }?.type
}

/**
 * Dex `MemberName`: a simple name, or one wrapped in angle brackets as `<init>` and `<clinit>` are. A simple name
 * never contains a character the signature syntax uses as a delimiter.
 */
private fun isMemberName(name: String): Boolean {
    val simple = if (name.length > 2 && name.first() == '<' && name.last() == '>') {
        name.substring(1, name.length - 1)
    } else {
        name
    }
    return simple.isNotEmpty() && simple.none { it in NAME_DELIMITERS || it == '<' || it == '>' }
}

private const val NAME_DELIMITERS = "():;/[."

private fun referenceType(descriptor: CharSequence, input: String): VirtualType = try {
    VirtualType.of(descriptor.toString())
} catch (failure: IllegalArgumentException) {
    throw MalformedSignatureException(input, input.indexOf(descriptor.toString()), "'$descriptor' is not a type descriptor", failure)
}

/** A proto as [SignatureReader] reads it, before a name and an owner join it. */
internal class ParsedProto(val parameterTypes: List<VirtualType>, val returnType: VirtualType)

/** The one reader of the smali member reference syntax. */
private class SignatureReader(private val text: String) {
    private var position = 0

    fun wholeMethod(owner: VirtualType?): MethodSignature {
        val definingClass = owner ?: ownerAndArrow()
        val name = memberName(expected = '(')
        val proto = proto()
        expectEnd()
        return MethodSignature(definingClass, name, proto.parameterTypes, proto.returnType)
    }

    fun wholeField(): FieldSignature {
        val definingClass = ownerAndArrow()
        val name = memberName(expected = ':')
        position++
        val type = type()
        if (type.isVoid) fail("a field type cannot be V")
        expectEnd()
        return FieldSignature(definingClass, name, type)
    }

    fun wholeProto(): ParsedProto = proto().also { expectEnd() }

    private fun ownerAndArrow(): VirtualType {
        val owner = type()
        if (!text.startsWith("->", position)) fail("expected '->' after the owner type")
        position += 2
        return owner
    }

    /** Reads up to the first delimiter, which must be [expected]; leaves [position] on it. */
    private fun memberName(expected: Char): String {
        val start = position
        while (position < text.length && text[position] !in NAME_DELIMITERS) position++
        if (position == text.length || text[position] != expected) fail("expected '$expected' after the member name")
        val name = text.substring(start, position)
        if (!isMemberName(name)) fail("'$name' is not a member name")
        return name
    }

    private fun proto(): ParsedProto {
        if (position == text.length || text[position] != '(') fail("expected '(' to open the parameter types")
        position++
        val parameters = ArrayList<VirtualType>()
        while (true) {
            if (position == text.length) fail("expected ')' after the parameter types")
            if (text[position] == ')') break
            val parameter = type()
            if (parameter.isVoid) fail("a parameter type cannot be V")
            parameters += parameter
        }
        position++
        return ParsedProto(parameters, type())
    }

    /** One type descriptor: array dimensions, then a primitive code or a class descriptor through its `;`. */
    private fun type(): VirtualType {
        val start = position
        while (position < text.length && text[position] == '[') position++
        if (position == text.length) fail("expected a type descriptor")
        if (text[position] == 'L') {
            val end = text.indexOf(';', position)
            if (end < 0) {
                position = text.length
                fail("unterminated class descriptor '${text.substring(start)}'")
            }
            position = end + 1
        } else {
            position++
        }
        val descriptor = text.substring(start, position)
        return try {
            VirtualType.of(descriptor)
        } catch (failure: IllegalArgumentException) {
            throw MalformedSignatureException(text, position, "'$descriptor' is not a type descriptor", failure)
        }
    }

    private fun expectEnd() {
        if (position != text.length) fail("unexpected '${text.substring(position)}' after the type")
    }

    private fun fail(reason: String): Nothing = throw MalformedSignatureException(text, position, reason)
}
