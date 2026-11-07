package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "next", aliases = ["n"], mixinStandardHelpOptions = true,
        description = ["Execute next line of code without entering methods"])
class NextCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        val cursor = debugger.cursor
        val index = debugger.currentInstructionIndex
        if (cursor != null && index != null) {
            parent.out.println("$index:>\t${cursor.op}")
            debugger.stepOver()
        }
        if (debugger.isFinished) printFinished(parent)
    }
}
