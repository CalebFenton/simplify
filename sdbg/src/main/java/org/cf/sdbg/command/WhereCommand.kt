package org.cf.sdbg.command

import picocli.CommandLine

@CommandLine.Command(name = "where", aliases = ["w"], mixinStandardHelpOptions = true,
        description = ["Shows current instruction index and which function you are in"])
internal class WhereCommand : DebuggerCommand() {
    override fun run() {
        val cursor = debugger.cursor
        if (cursor == null) {
            out.println("no current instruction; execution finished")
        } else {
            out.println("#${debugger.currentInstructionIndex} in ${cursor.view.signature}")
        }
    }
}
