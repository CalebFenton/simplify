package org.cf.smalivm.core

/** One VM's invocation configuration: its reflection policy and its emulator registry. */
public class InvocationServices internal constructor(
    internal val classIndex: ClassIndex,
    internal val reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
) {
    private val defaultRegistrations =
        defaultEmulatorRegistrations(classIndex.resolver(classIndex.currentSnapshot), reflectionPolicy)
    /** Emulator registrations, defaults included. */
    public val emulators: EmulatorRegistry = EmulatorRegistry(defaultRegistrations)

    /**
     * The registrations one session runs with. Default emulators read the class model, so they are rebuilt over
     * the session's [resolver]; every registration made through [emulators], a replacement of a default included,
     * keeps its current position. Registering later does not reach a started session.
     */
    internal fun sessionEmulators(resolver: ProgramSnapshotResolver): EmulatorRegistry {
        val registered = emulators.registrations().filter { registration ->
            defaultRegistrations.none { it === registration }
        }
        return EmulatorRegistry(defaultEmulatorRegistrations(resolver, reflectionPolicy) + registered)
    }
}
