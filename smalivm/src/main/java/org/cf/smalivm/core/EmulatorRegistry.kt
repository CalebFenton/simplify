package org.cf.smalivm.core

/** How an emulator registration selects a resolved method. */
public sealed interface EmulatorMatcher {
    /** Matches exactly [target]. */
    public data class Exact(val target: MethodSignature) : EmulatorMatcher

    /** Matches methods the predicate accepts; [id] names it in diagnostics. */
    public class Predicate(
        internal val id: String,
        private val predicate: (MethodSignature) -> Boolean,
    ) : EmulatorMatcher {
        init {
            require(id.isNotBlank()) { "An emulator predicate needs a stable non-blank ID" }
        }

        internal fun matches(target: MethodSignature): Boolean = predicate(target)
    }
}

/** Input capabilities declared by an emulator. */
public data class EmulatorCapability(
    val handlesUnknownArguments: Boolean,
    val receivesTrackedReferences: Boolean = false,
)

/** One atomic emulator registration. Replacement changes handler, capability, and effect together. */
public data class EmulatorRegistration(
    val matcher: EmulatorMatcher,
    val capability: EmulatorCapability,
    val effect: SideEffectLevel,
    val handler: EmulatorHandler,
)

/** Deterministic per-VM emulator registrations. */
public class EmulatorRegistry(initialRegistrations: Iterable<EmulatorRegistration> = emptyList()) {
    private val exactRegistrations = linkedMapOf<MethodSignature, EmulatorRegistration>()
    private val predicateRegistrations = linkedMapOf<String, PredicateRegistration>()

    init {
        initialRegistrations.forEach(::register)
    }

    /** Adds or replaces one registration without changing an existing matcher's position. */
    public fun register(registration: EmulatorRegistration) {
        when (val matcher = registration.matcher) {
            is EmulatorMatcher.Exact -> exactRegistrations[matcher.target] = registration
            is EmulatorMatcher.Predicate ->
                predicateRegistrations[matcher.id] = PredicateRegistration(matcher, registration)
        }
    }

    /** Finds an exact registration first, then the first matching predicate in registration order. */
    internal fun find(target: MethodSignature): EmulatorRegistration? =
        exactRegistrations[target]
            ?: predicateRegistrations.values.firstOrNull { it.matcher.matches(target) }?.registration

    /** Stable snapshot with exact registrations first, followed by predicate registration order. */
    internal fun registrations(): List<EmulatorRegistration> =
        exactRegistrations.values.toList() + predicateRegistrations.values.map(PredicateRegistration::registration)

    private class PredicateRegistration(
        val matcher: EmulatorMatcher.Predicate,
        val registration: EmulatorRegistration,
    )
}
