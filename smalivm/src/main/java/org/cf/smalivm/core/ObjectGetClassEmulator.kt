package org.cf.smalivm.core

/** Implements Object.getClass from the tracked runtime type. */
internal class ObjectGetClassEmulator(private val classIndex: ClassIndex) : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val receiver = requireNotNull(call.receiver) { "Object.getClass requires a receiver" }
        val runtimeType = when (receiver) {
            is TrackedFieldEmulatorValue -> receiver.type
            is EmulatorValue.TrackedReference -> receiver.type
            is EmulatorValue.Known -> virtualTypeOfHostClass(
                requireNotNull(receiver.javaValue) { "Object.getClass receiver is null" }.javaClass,
            )
            is EmulatorValue.Unknown -> return returnUnknown(call.returnType)
        }
        val classToken = classIndex.loadClassConstant(runtimeType.descriptor)
            ?: return returnUnknown(call.returnType)
        return SynchronousCallResult.Returned(
            HostReturn.Value(HostValue.Reference(classToken, call.returnType)),
        )
    }
}
