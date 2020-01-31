package org.cf.sdbg.command

import org.cf.smalivm.exception.UnhandledVirtualException
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "step", aliases = ["s"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Step to next line of code including stepping into methods"])
class StepCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        try {
            val currentOp = debugger.currentOp
            parent.out.println("${debugger.currentIndex}:>\t${currentOp}")
            val node = debugger.step()
            node ?: parent.out.println("execution finished")
        } catch (e: UnhandledVirtualException) {
            e.printStackTrace(parent.out)
        }
    }
}
