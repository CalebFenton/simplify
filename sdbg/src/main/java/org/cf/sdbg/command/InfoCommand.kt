package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "info", aliases = ["i"], mixinStandardHelpOptions = true,
        description = ["List information about target"])
class InfoCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["break | breakpoints | registers"])
    var target: String? = null

    private enum class InfoTargetType {
        REGISTERS, BREAK, INVALID
    }

    private fun parseTarget(target: String?): InfoTargetType {
        return when {
            target == null -> InfoTargetType.REGISTERS
            target.lowercase() == "registers" -> InfoTargetType.REGISTERS
            target.lowercase() == "break" -> InfoTargetType.BREAK
            target.lowercase() == "breakpoints" -> InfoTargetType.BREAK
            else -> InfoTargetType.INVALID
        }
    }

    private fun printRegisterInfo() {
        val state = debugger.currentState
        if (state == null) {
            parent.out.println("no current frame; execution finished")
            return
        }
        for (register in 0 until state.registers.registerCount) {
            val parameter = register - currentView.firstParameterRegister
            val name = if (parameter >= 0) "v$register(p$parameter)" else "v$register"
            parent.out.println("$name: ${state.readRegister(register)}")
        }
    }

    private fun printBreakInfo() {
        val breakpoints = debugger.breakpoints.sortedWith(compareBy({ it.methodSignature }, { it.instructionIndex }))
        parent.out.println("breakpoint count=${breakpoints.size}")
        if (breakpoints.isNotEmpty()) {
            val lines = breakpoints.joinToString("\n") { "${it.methodSignature} @ ${it.instructionIndex}" }
            parent.out.println(lines)
        }
    }

    override fun run() {
        val parsedTarget = parseTarget(target)
        when (parsedTarget) {
            InfoTargetType.REGISTERS -> printRegisterInfo()
            InfoTargetType.BREAK -> printBreakInfo()
            InfoTargetType.INVALID -> {
                parent.out.println("invalid target")
                return
            }
        }
    }

}
