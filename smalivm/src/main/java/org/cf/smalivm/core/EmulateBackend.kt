package org.cf.smalivm.core

/** Executes immutable handlers selected from one VM owner's registry. */
internal class EmulateBackend(private val registry: EmulatorRegistry) : Backend {
    override val name: String get() = "emulator"

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
        val registration = registry.find(call.target)
            ?: return BackendProbe.Declined("no emulator is registered for ${call.target}")
        val values = listOfNotNull(call.receiver) + call.arguments
        if (values.any { !registration.capability.canHandle(it) }) {
            return BackendProbe.Declined("${call.target} has an unknown or unmaterializable argument")
        }
        val mode = EmulatorArgumentMode(
            registration.capability.handlesUnknownArguments,
            registration.capability.receivesTrackedReferences,
        )
        val executor = BoundSynchronousExecutor(mode) { selected ->
            registration.handler.execute(selected.emulatorInvocation())
        }
        return BackendProbe.Execute.bound(registration.effect, executor)
    }

    private fun EmulatorCapability.canHandle(argument: ProbeArgument): Boolean =
        argument.isHostMaterializable ||
            handlesUnknownArguments ||
            (receivesTrackedReferences && argument.trackedType != null)
}
