package org.cf.sdbg.command

import org.cf.smalivm.exception.UnhandledVirtualException
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "next", aliases = ["n"], mixinStandardHelpOptions = true,
        description = ["Execute next line of code without entering methods"])
class NextCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        try {
            debugger.currentIndex
            val currentOp = debugger.currentOp
            parent.out.println("${debugger.currentIndex}:>\t${currentOp}")
            val node = debugger.step(false)
            node ?: parent.out.println("execution terminated")
        } catch (e: UnhandledVirtualException) {
            e.printStackTrace(parent.out)
        }
    }
}
