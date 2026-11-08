package org.cf.smalivm.core

/** Requests scheduler-owned cloning for one tracked array receiver. */
internal class ArrayCloneEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val receiver = requireNotNull(call.receiver) { "Array clone requires a receiver" }
        return when (receiver) {
            is TrackedFieldEmulatorValue -> returnUnknown(call.returnType)
            is EmulatorValue.TrackedReference -> {
                require(receiver.type.isArray) { "Array clone receiver is ${receiver.type}" }
                IntrinsicCallResult(IntrinsicOperation.CloneArrayReceiver)
            }
            is EmulatorValue.Unknown -> {
                val type = when {
                    receiver.type.isArray -> receiver.type
                    call.target.definingClass.isArray -> call.target.definingClass
                    else -> call.returnType
                }
                returnUnknown(type)
            }
            is EmulatorValue.Known -> error("Mutable arrays must remain scheduler-tracked")
        }
    }
}
