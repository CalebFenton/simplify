package org.cf.smalivm.core

import java.util.Collections

/** One emulator-only argument. Scheduler values and heap references never cross this boundary. */
sealed interface EmulatorValue {
    val type: VirtualType

    data class Known(
        override val type: VirtualType,
        val javaValue: Any?,
    ) : EmulatorValue

    data class Unknown(override val type: VirtualType) : EmulatorValue

    /** A heap-backed value exposing only its tracked runtime type. */
    data class TrackedReference(override val type: VirtualType) : EmulatorValue

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
class EmulatorInvocation internal constructor(
    val target: MethodDescriptor,
    val callerClass: VirtualType,
    val kind: InvokeKind,
    val returnType: VirtualType,
    parameterTypes: List<VirtualType>,
    val receiver: EmulatorValue?,
    arguments: List<EmulatorValue>,
    val classInitStatus: ClassInitStatusSnapshot,
) {
    val parameterTypes: List<VirtualType> = Collections.unmodifiableList(parameterTypes.toList())
    val arguments: List<EmulatorValue> = Collections.unmodifiableList(arguments.toList())
}

/** Trusted synchronous emulator code with no scheduler or mutable VM state access. */
fun interface EmulatorHandler {
    fun execute(call: EmulatorInvocation): SynchronousCallResult
}

internal val DEFAULT_EMULATOR_FRAMEWORK_TYPES = setOf(
    "Landroid/text/TextUtils;",
    "Ljava/lang/Class;",
    "Ljava/lang/Enum;",
    "Ljava/lang/ExceptionInInitializerError;",
    "Ljava/lang/Object;",
    "Ljava/lang/Throwable;",
    "Ljava/lang/reflect/AccessibleObject;",
    "Ljava/lang/reflect/Field;",
)

internal fun defaultEmulatorRegistrations(
    classIndex: ClassIndex,
    reflectionPolicy: ReflectionPolicy,
): List<EmulatorRegistration> {
    val textUtilsTarget = MethodDescriptor(
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
            FieldLookupEmulator(classIndex, reflectionPolicy),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodDescriptor(
                    VirtualType.of("Ljava/lang/reflect/Field;"),
                    "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            SideEffectLevel.STRONG,
            FieldGetEmulator(classIndex, reflectionPolicy),
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
                MethodDescriptor(
                    VirtualType.of("Ljava/lang/Class;"),
                    "forName",
                    "(Ljava/lang/String;)Ljava/lang/Class;",
                ),
            ),
            EmulatorCapability(handlesUnknownArguments = false),
            SideEffectLevel.NONE,
            ClassForNameEmulator(classIndex),
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
            MethodDescriptor(VirtualType.of("Ljava/lang/Throwable;"), "getCause", "()Ljava/lang/Throwable;"),
        ),
        throwableCauseRegistration(
            MethodDescriptor(
                VirtualType.of("Ljava/lang/ExceptionInInitializerError;"),
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
            ObjectGetClassEmulator(classIndex),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodDescriptor(
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
            EmulatorMatcher.Exact(MethodDescriptor(VirtualType.OBJECT, "<init>", "()V")),
            EmulatorCapability(handlesUnknownArguments = false, receivesTrackedReferences = true),
            SideEffectLevel.NONE,
            ObjectInitEmulator(),
        ),
        EmulatorRegistration(
            EmulatorMatcher.Exact(
                MethodDescriptor(
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

private fun throwableCauseRegistration(target: MethodDescriptor) = EmulatorRegistration(
    EmulatorMatcher.Exact(target),
    EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
    SideEffectLevel.NONE,
    ThrowableGetCauseEmulator(),
)
