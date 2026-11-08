package org.cf.smalivm.core

import java.util.Collections

/** One emulator-only argument. Scheduler values and heap references never cross this boundary. */
public sealed interface EmulatorValue {
    /** The argument's declared type. */
    public val type: VirtualType

    /** A known argument carried as [javaValue]. */
    public data class Known(
        override val type: VirtualType,
        val javaValue: Any?,
    ) : EmulatorValue

    /** An argument whose value is not known. */
    public data class Unknown(override val type: VirtualType) : EmulatorValue

    /** A heap-backed value exposing only its tracked runtime type. */
    public data class TrackedReference(override val type: VirtualType) : EmulatorValue

}

/** Immutable field facts copied from scheduler-owned heap state. */
internal data class TrackedFieldEmulatorValue(
    val metadata: TrackedFieldValue,
    val accessibility: FieldAccessibility,
) : EmulatorValue {
    init {
        require(accessibility != FieldAccessibility.UNKNOWN) {
            "Analysis-only tracked-field accessibility cannot enter emulator execution"
        }
    }

    override val type: VirtualType = VirtualType.of("Ljava/lang/reflect/Field;")
}

/** Immutable input for one emulator handler. */
public class EmulatorInvocation internal constructor(
    internal val target: MethodSignature,
    internal val callerClass: VirtualType,
    internal val kind: InvokeKind,
    internal val returnType: VirtualType,
    parameterTypes: List<VirtualType>,
    internal val receiver: EmulatorValue?,
    arguments: List<EmulatorValue>,
    internal val classInitStatus: ClassInitStatusSnapshot,
) {
    internal val parameterTypes: List<VirtualType> = Collections.unmodifiableList(parameterTypes.toList())
    /** The call arguments, receiver first for instance methods. */
    public val arguments: List<EmulatorValue> = Collections.unmodifiableList(arguments.toList())
}

/** Trusted synchronous emulator code with no scheduler or mutable VM state access. */
public fun interface EmulatorHandler {
    /** Runs [call] and reports its result. */
    public fun execute(call: EmulatorInvocation): SynchronousCallResult
}

internal fun defaultEmulatorRegistrations(
    resolver: ProgramSnapshotResolver,
    reflectionPolicy: ReflectionPolicy,
): List<EmulatorRegistration> {
    val textUtilsTarget = MethodSignature(
        VirtualType.of("Landroid/text/TextUtils;"),
        "isEmpty",
        "(Ljava/lang/CharSequence;)Z",
    )
    val textUtilsHandler = EmulatorHandler { call ->
        val value = call.arguments.single() as EmulatorValue.Known
        val sequence = value.javaValue as CharSequence?
        SynchronousCallResult.Returned(
            HostReturn.Value(
                HostValue.Primitive(sequence == null || sequence.isEmpty(), VirtualType.of("Z")),
            ),
        )
    }
    return listOf(
        EmulatorRegistration(
            EmulatorMatcher.Predicate("class-field-lookup") { target ->
                target.definingClass == VirtualType.of("Ljava/lang/Class;") &&
                    target.name in setOf("getField", "getDeclaredField") &&
                    target.proto == "(Ljava/lang/String;)Ljava/lang/reflect/Field;"
            },
            EmulatorCapability(handlesUnknownArguments = true),
            SideEffectLevel.NONE,
            FieldLookupEmulator(resolver, reflectionPolicy),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodSignature(
                    VirtualType.of("Ljava/lang/reflect/Field;"),
                    "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            SideEffectLevel.STRONG,
            FieldGetEmulator(resolver, reflectionPolicy),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Predicate("tracked-field-set-accessible") { target ->
                target.definingClass in setOf(
                    VirtualType.of("Ljava/lang/reflect/AccessibleObject;"),
                    VirtualType.of("Ljava/lang/reflect/Field;"),
                ) && target.name == "setAccessible" && target.proto == "(Z)V"
            },
            EmulatorCapability(handlesUnknownArguments = false, receivesTrackedReferences = true),
            SideEffectLevel.WEAK,
            FieldSetAccessibleEmulator(),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodSignature(
                    VirtualType.of("Ljava/lang/Class;"),
                    "forName",
                    "(Ljava/lang/String;)Ljava/lang/Class;",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = false),
            SideEffectLevel.NONE,
            ClassForNameEmulator(resolver),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(textUtilsTarget),
            EmulatorCapability(handlesUnknownArguments = false),
            SideEffectLevel.STRONG,
            textUtilsHandler,
        ),
        EmulatorRegistration(
            EmulatorMatcher.Predicate("tracked-array-clone") { target ->
                target.definingClass.isArray &&
                    target.name == "clone" &&
                    target.proto == "()Ljava/lang/Object;"
            },
            EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            SideEffectLevel.NONE,
            ArrayCloneEmulator(),
        ),
        // An override of getCause is ordinary bytecode; only the Throwable implementations read the cause slot.
        throwableCauseRegistration(
            MethodSignature(GuestThrowables.THROWABLE, "getCause", "()Ljava/lang/Throwable;"),
        ),
        throwableCauseRegistration(
            MethodSignature(
                SynthesizedThrowable.EXCEPTION_IN_INITIALIZER.type,
                "getException",
                "()Ljava/lang/Throwable;",
            ),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Predicate("object-get-class") { target ->
                (target.definingClass == VirtualType.OBJECT || target.definingClass.isArray) &&
                    target.name == "getClass" &&
                    target.proto == "()Ljava/lang/Class;"
            },
            EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            SideEffectLevel.NONE,
            ObjectGetClassEmulator(resolver),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodSignature(
                    VirtualType.of("Ljava/lang/Class;"),
                    "getComponentType",
                    "()Ljava/lang/Class;",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = true),
            SideEffectLevel.NONE,
            ClassGetComponentTypeEmulator(),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(MethodSignature(VirtualType.OBJECT, "<init>", "()V")),
            EmulatorCapability(handlesUnknownArguments = false, receivesTrackedReferences = true),
            SideEffectLevel.NONE,
            ObjectInitEmulator(),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodSignature(
                    VirtualType.of("Ljava/lang/Enum;"),
                    "<init>",
                    "(Ljava/lang/String;I)V",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            SideEffectLevel.NONE,
            EnumInitEmulator(),
        ),
    )
}

private fun throwableCauseRegistration(target: MethodSignature) = EmulatorRegistration(
    EmulatorMatcher.Exact(target),
    EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
    SideEffectLevel.NONE,
    ThrowableGetCauseEmulator(),
)
