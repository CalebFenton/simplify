package org.cf.sdbg.command

import org.cf.smalivm.exception.UnhandledVirtualException
import org.cf.smalivm.opcode.InvokeOp
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "step", aliases = ["s"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Step to next line of code including stepping into functions"])
class StepCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        try {
            val currentOp = debugger.currentOp
            parent.out.println("${currentOp.index + 1}:>\t${currentOp}")
            if (currentOp is InvokeOp) {
                currentOp.isDebugMode = true
                debugger.step()
                val invokedMethodExecutor = currentOp.debuggedMethodExecutor
                debugger.stepIntoInvoke(invokedMethodExecutor, currentOp)
                currentOp.isDebugMode = false
            } else {
                val node = debugger.step()
                node ?: parent.out.println("execution terminated")
            }
        } catch (e: UnhandledVirtualException) {
            e.printStackTrace(parent.out)
        }
    }
}
