package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "step", aliases = ["s"], mixinStandardHelpOptions = true,
        description = ["Step to next line of code including stepping into methods"])
class StepCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        val cursor = debugger.cursor
        val index = debugger.currentInstructionIndex
        if (cursor != null && index != null) {
            parent.out.println("$index:>\t${cursor.op}")
            debugger.stepInto()
        }
        if (debugger.isFinished) printFinished(parent)
    }
}
