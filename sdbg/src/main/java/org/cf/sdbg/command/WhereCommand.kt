package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "where", aliases = ["w"], mixinStandardHelpOptions = true,
        description = ["Shows current instruction index and which function you are in"])
class WhereCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        val cursor = debugger.cursor
        if (cursor == null) {
            parent.out.println("no current instruction; execution finished")
        } else {
            parent.out.println("#${debugger.currentInstructionIndex} in ${cursor.view.signature}")
        }
    }
}
