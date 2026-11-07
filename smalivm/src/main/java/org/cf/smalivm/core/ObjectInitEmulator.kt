package org.cf.smalivm.core

/** Completes Object construction without replacing the tracked receiver. */
internal class ObjectInitEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult =
        SynchronousCallResult.Returned(HostReturn.Void)
}
