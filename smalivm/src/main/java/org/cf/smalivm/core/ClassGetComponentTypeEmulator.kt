package org.cf.smalivm.core

/** Implements Class.getComponentType without generic reflection. */
internal class ClassGetComponentTypeEmulator : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val receiver = requireNotNull(call.receiver) { "Class.getComponentType requires a receiver" }
        if (receiver is EmulatorValue.Unknown) {
            return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        }
        val classToken = (receiver as? EmulatorValue.Known)?.javaValue as? Class<*>
            ?: return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        val component = classToken.componentType
        val value = if (component == null) {
            HostValue.TypedNull(call.returnType)
        } else {
            HostValue.Reference(component, call.returnType)
        }
        return SynchronousCallResult.Returned(HostReturn.Value(value))
    }
}
