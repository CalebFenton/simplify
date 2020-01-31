package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "where", aliases = ["w"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Shows current instruction index and which function you are in"])
class WhereCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        parent.out.println("#${debugger.currentIndex} in ${debugger.currentMethodSignature}")
    }
}
