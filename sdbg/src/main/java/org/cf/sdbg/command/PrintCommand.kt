package org.cf.sdbg.command

import org.cf.sdbg.SmaliReference
import org.cf.smalivm.core.StaticFieldLookup
import picocli.CommandLine

@CommandLine.Command(name = "print", aliases = ["p"], mixinStandardHelpOptions = true,
        description = ["Print value stored in variable or static field"])
internal class PrintCommand : DebuggerCommand() {
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["register | parameter | class->field:type"])
    var target: String? = null

    override fun run() {
        val state = debugger.currentState
        if (state == null) {
            out.println("no current frame; execution finished")
            return
        }
        val value = target
        if (value == null) {
            printInvalid(EXPECTED)
            return
        }
        val registerTarget = value.lowercase()
        if (REGISTER.matches(registerTarget)) {
            val number = registerTarget.substring(1).toLongOrNull()
            val parameter = registerTarget.startsWith('p')
            val register = number?.plus(if (parameter) currentView.firstParameterRegister else 0)
            if (register == null || register !in 0 until state.registerCount) {
                printInvalid("register out of range")
                return
            }
            val name = if (parameter) "p$number(r$register)" else "v$register"
            out.println("$name: ${state.readRegister(register.toInt())}")
            return
        }
        when (val reference = SmaliReference.parse(value)) {
            is SmaliReference.Field -> {
                val resolved = debugSession.services.classIndex.resolveStaticField(
                    reference.owner, reference.name, reference.type.descriptor)
                if (resolved is StaticFieldLookup.Resolved) {
                    out.println("${resolved.descriptor}: ${state.getStaticField(resolved.definingClass, resolved.descriptor)}")
                } else {
                    printInvalid("static field not found")
                }
            }
            is SmaliReference.Malformed -> printInvalid(reference.reason)
            is SmaliReference.Type, is SmaliReference.Method -> printInvalid(EXPECTED)
        }
    }

    private companion object {
        const val EXPECTED = "expected a register, parameter, or Lpkg/Name;->field:type"
        val REGISTER = Regex("[vpr]\\d+")
    }
}
