package org.cf.smalivm.core

/** Requests a path-local accessibility replacement for one tracked Field identity. */
internal class FieldSetAccessibleEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val field = call.receiver as? TrackedFieldEmulatorValue
            ?: return SynchronousCallResult.Returned(HostReturn.Void)
        val enabled = ((call.arguments.singleOrNull() as? EmulatorValue.Known)?.javaValue as? Boolean)
            ?: return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("Field.setAccessible requires one known boolean"),
            )
        return IntrinsicCallResult(
            IntrinsicOperation.SetTrackedFieldAccessible(
                field.metadata,
                if (enabled) FieldAccessibility.OVERRIDDEN else FieldAccessibility.LANGUAGE_CHECKS,
            ),
        )
    }
}
