package org.cf.smalivm.core

/**
 * The guest throwables ops synthesize, so every op that raises one failure names the same type with the same
 * message. A message that would name a value the engine does not know (an unprovable index) is omitted rather
 * than invented.
 */
internal object GuestThrowables {
    val ARITHMETIC: VirtualType = VirtualType.of("Ljava/lang/ArithmeticException;")
    val ARRAY_INDEX_OUT_OF_BOUNDS: VirtualType = VirtualType.of("Ljava/lang/ArrayIndexOutOfBoundsException;")
    val ARRAY_STORE: VirtualType = VirtualType.of("Ljava/lang/ArrayStoreException;")
    val CLASS_CAST: VirtualType = VirtualType.of("Ljava/lang/ClassCastException;")
    val INTERNAL_ERROR: VirtualType = VirtualType.of("Ljava/lang/InternalError;")
    val NEGATIVE_ARRAY_SIZE: VirtualType = VirtualType.of("Ljava/lang/NegativeArraySizeException;")
    val NO_CLASS_DEF_FOUND: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
    val NO_SUCH_FIELD: VirtualType = VirtualType.of("Ljava/lang/NoSuchFieldError;")
    val NULL_POINTER: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")

    fun throwing(recipe: ThrowableRecipe, update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
        OpArm.Throw(update, ThrowEvidence.Fresh(recipe))

    fun divideByZero(): ThrowableRecipe = ThrowableRecipe.of(ARITHMETIC, "/ by zero")

    fun nullPointer(message: String? = null): ThrowableRecipe = ThrowableRecipe.of(NULL_POINTER, message)

    /** [index] is null for a fork on an unprovable index, where no offending index exists. */
    fun arrayIndexOutOfBounds(index: Int?): ThrowableRecipe =
        ThrowableRecipe.of(ARRAY_INDEX_OUT_OF_BOUNDS, index?.toString())

    /** [valueType] is null when the stored value carries no type at all. */
    fun arrayStore(valueType: VirtualType?): ThrowableRecipe =
        ThrowableRecipe.of(ARRAY_STORE, valueType?.let(::binaryNameOf))

    /** [valueType] is null when the cast value carries no type at all. */
    fun classCast(valueType: VirtualType?, target: VirtualType): ThrowableRecipe =
        ThrowableRecipe.of(CLASS_CAST, valueType?.let { "${binaryNameOf(it)} cannot be cast to ${binaryNameOf(target)}" })

    /** [size] is null for a fork on an unprovable length. */
    fun negativeArraySize(size: Int?): ThrowableRecipe = ThrowableRecipe.of(NEGATIVE_ARRAY_SIZE, size?.toString())

    /** The message is the absent class's binary name, with array dimensions peeled: `[[Lfoo/Bar;` -> `foo.Bar`. */
    fun noClassDefFound(absentType: VirtualType): ThrowableRecipe {
        var base = absentType
        while (base.isArray) base = requireNotNull(base.componentType)
        return ThrowableRecipe.of(NO_CLASS_DEF_FOUND, base.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.'))
    }

    fun noSuchField(fieldDescriptor: String): ThrowableRecipe = ThrowableRecipe.of(NO_SUCH_FIELD, fieldDescriptor)

    fun internalError(message: String): ThrowableRecipe = ThrowableRecipe.of(INTERNAL_ERROR, message)

    /** `java.lang.Class.getName` of a descriptor: `I` -> `int`, `Lfoo/Bar;` -> `foo.Bar`, `[Lfoo/Bar;` -> `[Lfoo.Bar;`. */
    private fun binaryNameOf(type: VirtualType): String = when (val descriptor = type.descriptor) {
        "Z" -> "boolean"
        "B" -> "byte"
        "C" -> "char"
        "S" -> "short"
        "I" -> "int"
        "J" -> "long"
        "F" -> "float"
        "D" -> "double"
        "V" -> "void"
        else ->
            if (descriptor.startsWith("[")) descriptor.replace('/', '.')
            else descriptor.substring(1, descriptor.length - 1).replace('/', '.')
    }
}

/**
 * A type operand naming a class absent from the loaded set: a guest `NoClassDefFoundError`, thrown before the
 * op reads any register. It is an `Error`, so a guest `catch (Exception)` does not catch it. Definite rather
 * than forked, because the loaded class set is the whole program.
 */
internal fun resolutionFailure(absentType: VirtualType): OpExecution =
    OpExecution.single(GuestThrowables.throwing(GuestThrowables.noClassDefFound(absentType)))

/** A field reference whose class resolved but declares no such field: a guest `NoSuchFieldError`. */
internal fun noSuchFieldFailure(fieldDescriptor: String): OpExecution =
    OpExecution.single(GuestThrowables.throwing(GuestThrowables.noSuchField(fieldDescriptor)))
