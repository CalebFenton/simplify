package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.core.StaticFieldLookup
import org.cf.smalivm.core.VirtualType
import org.cf.util.ClassNameUtils
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "print", aliases = ["p"], mixinStandardHelpOptions = true,
        description = ["Print value stored in variable or static field"])
class PrintCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["register | parameter | class->field:type"])
    var target: String? = null

    override fun run() {
        val state = debugger.currentState
        if (state == null) {
            parent.out.println("no current frame; execution finished")
            return
        }
        val value = target
        if (value == null) {
            parent.out.println("invalid target; expected register or static field")
            return
        }
        val registerTarget = value.lowercase()
        if (REGISTER.matches(registerTarget)) {
            val number = registerTarget.substring(1).toIntOrNull()
            val parameter = registerTarget.startsWith('p')
            val register = number?.toLong()?.plus(if (parameter) currentView.firstParameterRegister else 0)
            if (register == null || register !in 0 until state.registers.registerCount.toLong()) {
                parent.out.println("invalid target; register out of range")
                return
            }
            val name = if (parameter) "p$number(r$register)" else "v$register"
            parent.out.println("$name: ${state.readRegister(register.toInt())}")
            return
        }
        if (ClassNameUtils.guessReferenceType(value) == ClassNameUtils.ReferenceType.INTERNAL_FIELD_DESCRIPTOR) {
            val owner = value.substringBefore("->")
            val field = value.substringAfter("->")
            val name = field.substringBefore(':')
            val type = field.substringAfter(':')
            val resolved = Main.services.classIndex.resolveStaticField(VirtualType.of(owner), name, type)
            if (resolved is StaticFieldLookup.Resolved) {
                parent.out.println("${resolved.descriptor}: ${state.getStaticField(resolved.definingClass, resolved.descriptor)}")
                return
            }
            parent.out.println("invalid target; static field not found")
            return
        }
        parent.out.println("invalid target; unable to parse")
    }

    private companion object {
        val REGISTER = Regex("[vpr]\\d+")
    }
}
