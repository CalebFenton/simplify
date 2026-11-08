package org.cf.demoapp

import org.cf.smalivm.core.EmulatorCapability
import org.cf.smalivm.core.EmulatorHandler
import org.cf.smalivm.core.EmulatorMatcher
import org.cf.smalivm.core.EmulatorRegistration
import org.cf.smalivm.core.EmulatorValue
import org.cf.smalivm.core.HostReturn
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.SynchronousCallResult
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.VmServices

internal fun registerPrintlnEmulator(services: VmServices) {
    services.invocationServices.emulators.register(
        EmulatorRegistration(
            matcher = EmulatorMatcher.Exact(
                MethodSignature(
                    VirtualType.of("Ljava/io/PrintStream;"),
                    "println",
                    "(Ljava/lang/String;)V",
                ),
            ),
            // System.out need not be a materialized host stream: the demo deliberately redirects
            // this exact overload to its own console, including explicitly labeled unknown text.
            capability = EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            effect = SideEffectLevel.STRONG,
            handler = EmulatorHandler { call ->
                // The receiver is separate; arguments[0] is the string, not the PrintStream.
                val text = when (val argument = call.arguments.single()) {
                    is EmulatorValue.Known -> argument.javaValue as String?
                    else -> "<unknown ${argument.type}>"
                }
                println(text)
                SynchronousCallResult.Returned(HostReturn.Void)
            },
        ),
    )
}
