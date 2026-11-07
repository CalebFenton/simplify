package org.cf.smalivm.core

/** How an emulator registration selects a resolved method. */
sealed interface EmulatorMatcher {
    data class Exact(val target: MethodDescriptor) : EmulatorMatcher

    class Predicate(
        val id: String,
        private val predicate: (MethodDescriptor) -> Boolean,
    ) : EmulatorMatcher {
        init {
            require(id.isNotBlank()) { "An emulator predicate needs a stable non-blank ID" }
        }

        internal fun matches(target: MethodDescriptor): Boolean = predicate(target)
    }
}

/** Input capabilities declared by an emulator. */
data class EmulatorCapability(
    val handlesUnknownArguments: Boolean,
    val receivesTrackedReferences: Boolean = false,
)

/** One atomic emulator registration. Replacement changes handler, capability, and effect together. */
data class EmulatorRegistration(
    val matcher: EmulatorMatcher,
    val capability: EmulatorCapability,
    val effect: SideEffectLevel,
    val handler: EmulatorHandler,
)

/** Deterministic per-VM emulator registrations. */
class EmulatorRegistry(initialRegistrations: Iterable<EmulatorRegistration> = emptyList()) {
    private val exactRegistrations = linkedMapOf<MethodDescriptor, EmulatorRegistration>()
    private val predicateRegistrations = mutableListOf<EmulatorRegistration>()

    init {
        initialRegistrations.forEach(::register)
    }

    /** Adds or replaces one registration without changing an existing matcher's position. */
    fun register(registration: EmulatorRegistration) {
        when (val matcher = registration.matcher) {
            is EmulatorMatcher.Exact -> exactRegistrations[matcher.target] = registration
            is EmulatorMatcher.Predicate -> {
                val replacementIndex = predicateRegistrations.indexOfFirst {
                    (it.matcher as EmulatorMatcher.Predicate).id == matcher.id
                }
                if (replacementIndex < 0) {
                    predicateRegistrations.add(registration)
                } else {
                    predicateRegistrations[replacementIndex] = registration
                }
            }
        }
    }

    /** Finds an exact registration first, then the first matching predicate in registration order. */
    fun find(target: MethodDescriptor): EmulatorRegistration? =
        exactRegistrations[target]
            ?: predicateRegistrations.firstOrNull {
                (it.matcher as EmulatorMatcher.Predicate).matches(target)
            }

    /** Stable snapshot with exact registrations first, followed by predicate registration order. */
    fun registrations(): List<EmulatorRegistration> =
        exactRegistrations.values.toList() + predicateRegistrations
}
