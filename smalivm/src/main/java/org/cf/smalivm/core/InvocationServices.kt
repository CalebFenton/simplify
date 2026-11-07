package org.cf.smalivm.core

/** Invocation services bound to one exact class-index identity. */
class InvocationServices internal constructor(
    val classIndex: ClassIndex,
    val reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
) {
    private val defaultRegistrations = defaultEmulatorRegistrations(classIndex, reflectionPolicy)
    val emulators = EmulatorRegistry(defaultRegistrations)

    internal fun capture(resolver: ProgramSnapshotResolver): SnapshotInvocationServices {
        val capturedDefaults = defaultEmulatorRegistrations(resolver.classIndex, reflectionPolicy)
        val customRegistrations = emulators.registrations().filter { registration ->
            defaultRegistrations.none { it === registration }
        }
        return SnapshotInvocationServices(
            resolver,
            reflectionPolicy,
            EmulatorRegistry(capturedDefaults + customRegistrations),
        )
    }
}

internal class SnapshotInvocationServices(
    val resolver: ProgramSnapshotResolver,
    val reflectionPolicy: ReflectionPolicy,
    val emulators: EmulatorRegistry,
)
