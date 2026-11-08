package org.cf.sdbg.command

import picocli.CommandLine

@CommandLine.Command(name = "info", aliases = ["i"], mixinStandardHelpOptions = true,
        description = ["List information about target"])
internal class InfoCommand : DebuggerCommand() {
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["break | breakpoints | registers"])
    var target: String? = null

    private fun printRegisterInfo() {
        val state = debugger.currentState
        if (state == null) {
            out.println("no current frame; execution finished")
            return
        }
        for (register in 0 until state.registers.registerCount) {
            val parameter = register - currentView.firstParameterRegister
            val name = if (parameter >= 0) "v$register(p$parameter)" else "v$register"
            out.println("$name: ${state.readRegister(register)}")
        }
    }

    private fun printBreakInfo() {
        val breakpoints = debugger.breakpoints.sortedWith(compareBy({ it.methodSignature }, { it.instructionIndex }))
        out.println("breakpoint count=${breakpoints.size}")
        breakpoints.forEach { out.println("${it.methodSignature} @ ${it.instructionIndex}") }
    }

    override fun run() {
        when (target?.lowercase()) {
            null, "registers" -> printRegisterInfo()
            "break", "breakpoints" -> printBreakInfo()
            else -> printInvalid("expected break, breakpoints, or registers")
        }
    }
}
