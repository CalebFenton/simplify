package org.cf.smalivm.core

/** One VM's invocation configuration: its reflection policy and its emulator registry. */
class InvocationServices internal constructor(
    val classIndex: ClassIndex,
    val reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
) {
    private val defaultRegistrations =
        defaultEmulatorRegistrations(classIndex.resolver(classIndex.currentSnapshot), reflectionPolicy)
    val emulators = EmulatorRegistry(defaultRegistrations)

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
