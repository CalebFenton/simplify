package org.cf.smalivm.core

import java.lang.reflect.Method

/**
 * `AccessibleObject.setAccessible(boolean)`: a path-local accessibility replacement for one tracked Field identity,
 * or the flag of a host `Method`, which the path's host graph holds like any other host state. libcore's
 * `setAccessible0` refuses only some `Constructor`s, so a Method's flag is set unconditionally.
 */
internal class FieldSetAccessibleEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val enabled = ((call.arguments.singleOrNull() as? EmulatorValue.Known)?.javaValue as? Boolean)
            ?: return SynchronousCallResult.InfrastructureFailure(
                IllegalArgumentException("AccessibleObject.setAccessible requires one known boolean"),
            )
        return when (val receiver = call.receiver) {
            is TrackedFieldEmulatorValue -> IntrinsicCallResult(
                IntrinsicOperation.SetTrackedFieldAccessible(
                    receiver.metadata,
                    if (enabled) FieldAccessibility.OVERRIDDEN else FieldAccessibility.LANGUAGE_CHECKS,
                ),
            )
            is EmulatorValue.Known -> when (val method = receiver.javaValue) {
                is Method -> hostBoundary({ SynchronousCallResult.InfrastructureFailure(it) }) {
                    method.isAccessible = enabled
                    SynchronousCallResult.Returned(HostReturn.Void)
                }
                else -> SynchronousCallResult.Returned(HostReturn.Void)
            }
            else -> SynchronousCallResult.Returned(HostReturn.Void)
        }
    }
}
