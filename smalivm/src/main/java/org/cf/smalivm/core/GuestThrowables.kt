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
    ABSTRACT_METHOD("Ljava/lang/AbstractMethodError;", { message, _ -> AbstractMethodError(message) }),
    ARITHMETIC("Ljava/lang/ArithmeticException;", { message, _ -> ArithmeticException(message) }),
    ARRAY_INDEX_OUT_OF_BOUNDS(
        "Ljava/lang/ArrayIndexOutOfBoundsException;",
        { message, _ -> ArrayIndexOutOfBoundsException(message) },
    ),
    ARRAY_STORE("Ljava/lang/ArrayStoreException;", { message, _ -> ArrayStoreException(message) }),
    CLASS_CAST("Ljava/lang/ClassCastException;", { message, _ -> ClassCastException(message) }),
    CLASS_CIRCULARITY("Ljava/lang/ClassCircularityError;", { message, _ -> ClassCircularityError(message) }),
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
 * The guest throwables the engine synthesizes, with ART's message texts, so every site that raises one failure
 * names the same type with the same message. A text that names a value the engine does not know (an unprovable
 * index or length, a runtime class it cannot determine) is omitted rather than invented; a text built only from
 * the instruction and its resolved operands is attached to speculative throws too.
 */
internal object GuestThrowables {
    val THROWABLE: VirtualType = VirtualType.of("Ljava/lang/Throwable;")
    val ERROR: VirtualType = VirtualType.of("Ljava/lang/Error;")

    fun throwing(recipe: ThrowableRecipe, update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
        OpArm.Throw(update, ThrowEvidence.Fresh(recipe))

    fun divideByZero(): ThrowableRecipe = ThrowableRecipe(SynthesizedThrowable.ARITHMETIC, "/ by zero")

    /** ART's `throw` of a null reference, which carries no message (`ThrowNullPointerException()`). */
    fun nullPointer(): ThrowableRecipe = ThrowableRecipe(SynthesizedThrowable.NULL_POINTER)

    /** ART's per-instruction texts: `ThrowNullPointerExceptionFromDexPC`, and `FillArrayData`'s own. */
    enum class NullOperand(val message: String) {
        ARRAY_READ("Attempt to read from null array"),
        ARRAY_WRITE("Attempt to write to null array"),
        ARRAY_LENGTH("Attempt to get length of null array"),
        FILL_ARRAY_DATA("null array in FILL_ARRAY_DATA"),
        MONITOR("Attempt to do a synchronize operation on a null object"),
    }

    fun nullPointer(operand: NullOperand): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NULL_POINTER, operand.message)

    /** ART's `ThrowNullPointerExceptionForFieldAccess` over the resolved field and the running [method]. */
    fun nullFieldAccess(field: InstanceFieldLookup.Resolved, method: MethodSignature, isRead: Boolean): ThrowableRecipe {
        val access = if (isRead) "read from" else "write to"
        return ThrowableRecipe(
            SynthesizedThrowable.NULL_POINTER,
            "Attempt to $access field '${prettyDescriptor(field.type)} ${prettyDescriptor(field.definingClass)}.${field.field.name}' " +
                "on a null object reference in method '${prettyMethod(method)}'",
        )
    }

    /** ART's `ThrowNullPointerExceptionForMethodAccess` over the call site's referenced [target]. */
    fun nullInvokeReceiver(target: MethodSignature, kind: InvokeKind): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.NULL_POINTER,
        "Attempt to invoke ${kind.artName} method '${prettyMethod(target)}' on a null object reference",
    )

    /** ART's `ThrowArrayIndexOutOfBoundsException`; a null [length] or [index] omits the message. */
    fun arrayIndexOutOfBounds(length: Int?, index: Int?): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.ARRAY_INDEX_OUT_OF_BOUNDS,
        if (length != null && index != null) "length=$length; index=$index" else null,
    )

    /** ART's `FillArrayData` overrun, whose second number is the payload's element [count]. */
    fun fillArrayDataOverrun(length: Int?, count: Int): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.ARRAY_INDEX_OUT_OF_BOUNDS,
        length?.let { "failed FILL_ARRAY_DATA; length=$it, index=$count" },
    )

    /** ART's `ThrowArrayStoreException` over the runtime classes; an unknown one omits the message. */
    fun arrayStore(valueClass: VirtualType?, arrayClass: VirtualType?): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.ARRAY_STORE,
        if (valueClass != null && arrayClass != null) {
            "${prettyDescriptor(valueClass)} cannot be stored in an array of type ${prettyDescriptor(arrayClass)}"
        } else {
            null
        },
    )

    /** ART's `ThrowClassCastException` over the object's runtime class; an unknown one omits the message. */
    fun classCast(objectClass: VirtualType?, target: VirtualType): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.CLASS_CAST,
        objectClass?.let { "${prettyDescriptor(it)} cannot be cast to ${prettyDescriptor(target)}" },
    )

    /** [size] is null for a fork on an unprovable length. */
    fun negativeArraySize(size: Int?): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NEGATIVE_ARRAY_SIZE, size?.toString())

    /** ART's `ClassLinker::DoResolveType` text over the unresolvable descriptor, array dimensions included. */
    fun noClassDefFound(absentType: VirtualType): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_CLASS_DEF_FOUND, "Failed resolution of: ${absentType.descriptor}")

    /**
     * A reference to [type], whose base class is not in the loaded set. A class on an inheritance cycle fails to
     * load with ART's `ClassCircularityError`: `LoadSuperAndInterfaces` names a direct self-reference, and a longer
     * cycle fails in `EnsureResolved`, which names the class being resolved. Any other absent class is
     * [noClassDefFound].
     */
    fun unresolvedClass(type: VirtualType, hierarchy: HierarchySnapshot): ThrowableRecipe {
        val circularity = hierarchy.circularityOf(type) ?: return noClassDefFound(type)
        var base = type
        while (base.isArray) base = requireNotNull(base.componentType)
        val message = when (circularity) {
            ClassCircularity.EXTENDS_ITSELF -> "Class ${prettyDescriptor(base)} extends itself"
            ClassCircularity.IMPLEMENTS_ITSELF -> "Class ${prettyDescriptor(base)} implements itself"
            ClassCircularity.THROUGH_ANOTHER_CLASS -> prettyDescriptor(base)
        }
        return ThrowableRecipe(SynthesizedThrowable.CLASS_CIRCULARITY, message)
    }

    /**
     * A trigger of [failedClass], whose initialization failed earlier with [storedFailure]. ART
     * `ClassLinker::ThrowEarlierClassFailure` throws `NoClassDefFoundError(PrettyDescriptor)` with the stored
     * error as its cause.
     */
    fun earlierClassFailure(failedClass: VirtualType, storedFailure: ThrownSubject): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_CLASS_DEF_FOUND, prettyDescriptor(failedClass), storedFailure)

    /** JLS 12.4.2: an initializer that completes abruptly with a non-`Error` leaves through this wrapper. */
    fun exceptionInInitializer(cause: ThrownSubject): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.EXCEPTION_IN_INITIALIZER, cause = cause)

    fun noSuchField(fieldDescriptor: String): ThrowableRecipe =
        ThrowableRecipe(SynthesizedThrowable.NO_SUCH_FIELD, fieldDescriptor)

    /** ART's `ThrowNoSuchMethodError` over the call site's referenced [method] and invoke [kind]. */
    fun noSuchMethod(method: MethodSignature, kind: InvokeKind): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.NO_SUCH_METHOD,
        "No ${kind.artName} method ${method.nameAndProto} in class ${method.definingClass.descriptor} or its super classes",
    )

    /** A call that resolves to more than one maximally specific default method. */
    fun conflictingDefaultMethods(method: MethodSignature): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.INCOMPATIBLE_CLASS_CHANGE,
        "Conflicting default method implementations $method",
    )

    /**
     * ART's `ThrowAbstractMethodError` when the method selected for a receiver of class [receiverClass] is the
     * abstract [method]: `abstract method "<PrettyMethod>" on receiver <PrettyClass>`. A receiver whose class the
     * engine does not know omits the message.
     */
    fun abstractMethod(method: MethodSignature, receiverClass: VirtualType?): ThrowableRecipe = ThrowableRecipe(
        SynthesizedThrowable.ABSTRACT_METHOD,
        receiverClass?.let {
            "abstract method \"${prettyMethod(method)}\" on receiver java.lang.Class<${prettyDescriptor(it)}>"
        },
    )

    fun internalError(message: String): ThrowableRecipe = ThrowableRecipe(SynthesizedThrowable.INTERNAL_ERROR, message)

    /** ART's `PrettyDescriptor`: `I` -> `int`, `Lfoo/Bar;` -> `foo.Bar`, `[[Lfoo/Bar;` -> `foo.Bar[][]`. */
    private fun prettyDescriptor(type: VirtualType): String {
        var dimensions = 0
        var base = type
        while (base.isArray) {
            dimensions++
            base = requireNotNull(base.componentType)
        }
        val name = when (val descriptor = base.descriptor) {
            "Z" -> "boolean"
            "B" -> "byte"
            "C" -> "char"
            "S" -> "short"
            "I" -> "int"
            "J" -> "long"
            "F" -> "float"
            "D" -> "double"
            "V" -> "void"
            else -> descriptor.substring(1, descriptor.length - 1).replace('/', '.')
        }
        return name + "[]".repeat(dimensions)
    }

    /** ART's `PrettyMethod` with its signature: `int java.lang.String.indexOf(int, int)`. */
    private fun prettyMethod(method: MethodSignature): String {
        val returnType = prettyDescriptor(method.returnType)
        val parameters = method.parameterTypes.joinToString(", ", transform = ::prettyDescriptor)
        return "$returnType ${prettyDescriptor(method.definingClass)}.${method.name}($parameters)"
    }

    /** The `InvokeType` name ART prints in invoke failures. */
    private val InvokeKind.artName: String
        get() = when (this) {
            InvokeKind.STATIC -> "static"
            InvokeKind.DIRECT -> "direct"
            InvokeKind.SUPER -> "super"
            InvokeKind.VIRTUAL -> "virtual"
            InvokeKind.INTERFACE -> "interface"
        }
}

/**
 * A reference to a class absent from the loaded set throws [failure], the error
 * [GuestThrowables.unresolvedClass] chose, before the op reads any register. It is an `Error`, so a guest
 * `catch (Exception)` does not catch it. Definite rather than forked, because the loaded class set is the whole
 * program.
 */
internal fun resolutionFailure(failure: ThrowableRecipe): OpExecution =
    OpExecution.single(GuestThrowables.throwing(failure))

/** A field reference whose class resolved but declares no such field: a guest `NoSuchFieldError`. */
internal fun noSuchFieldFailure(fieldDescriptor: String): OpExecution =
    OpExecution.single(GuestThrowables.throwing(GuestThrowables.noSuchField(fieldDescriptor)))
