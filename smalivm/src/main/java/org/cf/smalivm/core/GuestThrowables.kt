package org.cf.smalivm.core

/**
 * Every throwable type the engine constructs itself, with the host object that stands for one on the heap. The
 * guest cause lives in the heap object; only `ExceptionInInitializerError` also carries it on the host object,
 * because its host constructor takes the cause.
 */
internal enum class SynthesizedThrowable(
    descriptor: String,
    val host: (message: String?, cause: Throwable?) -> Throwable,
) {
    ARITHMETIC("Ljava/lang/ArithmeticException;", { message, _ -> ArithmeticException(message) }),
    ARRAY_INDEX_OUT_OF_BOUNDS(
        "Ljava/lang/ArrayIndexOutOfBoundsException;",
        { message, _ -> ArrayIndexOutOfBoundsException(message) },
    ),
    ARRAY_STORE("Ljava/lang/ArrayStoreException;", { message, _ -> ArrayStoreException(message) }),
    CLASS_CAST("Ljava/lang/ClassCastException;", { message, _ -> ClassCastException(message) }),
    EXCEPTION_IN_INITIALIZER(
        "Ljava/lang/ExceptionInInitializerError;",
        { _, cause -> if (cause == null) ExceptionInInitializerError() else ExceptionInInitializerError(cause) },
    ),
    INCOMPATIBLE_CLASS_CHANGE(
        "Ljava/lang/IncompatibleClassChangeError;",
        { message, _ -> IncompatibleClassChangeError(message) },
    ),
    INTERNAL_ERROR("Ljava/lang/InternalError;", { message, _ -> InternalError(message) }),
    NEGATIVE_ARRAY_SIZE("Ljava/lang/NegativeArraySizeException;", { message, _ -> NegativeArraySizeException(message) }),
    NO_CLASS_DEF_FOUND("Ljava/lang/NoClassDefFoundError;", { message, _ -> NoClassDefFoundError(message) }),
    NO_SUCH_FIELD("Ljava/lang/NoSuchFieldError;", { message, _ -> NoSuchFieldError(message) }),
    NO_SUCH_METHOD("Ljava/lang/NoSuchMethodError;", { message, _ -> NoSuchMethodError(message) }),
    NULL_POINTER("Ljava/lang/NullPointerException;", { message, _ -> NullPointerException(message) }),
    ;

    val type: VirtualType = VirtualType.of(descriptor)
}

/**
 * The guest throwables the engine synthesizes, so every site that raises one failure names the same type with the
 * same message. A message that would name a value the engine does not know (an unprovable index) is omitted rather
 * than invented.
 */
internal object GuestThrowables {
    val THROWABLE: VirtualType = VirtualType.of("Ljava/lang/Throwable;")
    val ERROR: VirtualType = VirtualType.of("Ljava/lang/Error;")

    fun throwing(recipe: ThrowableRecipe, update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
        OpArm.Throw(update, ThrowEvidence.Fresh(recipe))

    fun divideByZero(): ThrowableRecipe = ThrowableRecipe(SynthesizedThrowable.ARITHMETIC, "/ by zero")

    fun nullPointer(message: String? = null): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NULL_POINTER, message)

    /** [index] is null for a fork on an unprovable index, where no offending index exists. */
    fun arrayIndexOutOfBounds(index: Int?): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.ARRAY_INDEX_OUT_OF_BOUNDS, index?.toString())

    /** [valueType] is null when the stored value carries no type at all. */
    fun arrayStore(valueType: VirtualType?): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.ARRAY_STORE, valueType?.let(::binaryNameOf))

    /** [valueType] is null when the cast value carries no type at all. */
    fun classCast(valueType: VirtualType?, target: VirtualType): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.CLASS_CAST,
        valueType?.let { "${binaryNameOf(it)} cannot be cast to ${binaryNameOf(target)}" },
    )

    /** [size] is null for a fork on an unprovable length. */
    fun negativeArraySize(size: Int?): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NEGATIVE_ARRAY_SIZE, size?.toString())

    /** The message is the absent class's binary name, with array dimensions peeled: `[[Lfoo/Bar;` -> `foo.Bar`. */
    fun noClassDefFound(absentType: VirtualType): ThrowableRecipe {
        var base = absentType
        while (base.isArray) base = requireNotNull(base.componentType)
        return ThrowableRecipe(SynthesizedThrowable.NO_CLASS_DEF_FOUND, binaryNameOf(base))
    }

    /**
     * A trigger of [failedClass], whose initialization failed earlier with [storedFailure]. ART
     * `ClassLinker::ThrowEarlierClassFailure` throws `NoClassDefFoundError(PrettyDescriptor)` with the stored
     * error as its cause, and the pretty descriptor of a class is its binary name.
     */
    fun earlierClassFailure(failedClass: VirtualType, storedFailure: ThrownSubject): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_CLASS_DEF_FOUND, binaryNameOf(failedClass), storedFailure)

    /** JLS 12.4.2: an initializer that completes abruptly with a non-`Error` leaves through this wrapper. */
    fun exceptionInInitializer(cause: ThrownSubject): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.EXCEPTION_IN_INITIALIZER, cause = cause)

    fun noSuchField(fieldDescriptor: String): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_SUCH_FIELD, fieldDescriptor)

    /** [method] is the call site's `name(proto)` descriptor. */
    fun noSuchMethod(method: MethodDescriptor): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_SUCH_METHOD, method.descriptor)

    /** A call that resolves to more than one maximally specific default method. */
    fun conflictingDefaultMethods(method: MethodDescriptor): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.INCOMPATIBLE_CLASS_CHANGE,
        "Conflicting default method implementations ${method.definingClass.descriptor}->${method.descriptor}",
    )

    fun internalError(message: String): ThrowableRecipe = ThrowableRecipe(SynthesizedThrowable.INTERNAL_ERROR, message)

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
