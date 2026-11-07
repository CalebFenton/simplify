package org.cf.smalivm.core

/** Reads the semantic guest cause. The scheduler owns the heap lookup. */
internal class ThrowableGetCauseEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        requireNotNull(call.receiver) { "Throwable.getCause requires a receiver" }
        return IntrinsicCallResult(IntrinsicOperation.ReadThrowableCause)
    }
}
