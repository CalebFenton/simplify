package org.cf.smalivm.core

/** Retains enum constructor receivers without canonical-name or global-map substitution. */
internal class EnumInitEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        require(call.arguments.size == 2) { "Enum.<init> requires name and ordinal arguments" }
        return IntrinsicCallResult(
            IntrinsicOperation.InitializeEnumReceiver(
                name = call.arguments[0],
                ordinal = call.arguments[1],
            ),
        )
    }
}
