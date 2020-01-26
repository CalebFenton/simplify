package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.exception.UnhandledVirtualException
import org.cf.smalivm.opcode.InvokeOp
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "step", aliases = ["s"], mixinStandardHelpOptions = true, version = ["1.0"], description = ["Step to next line of code including stepping into functions"])
class Step : Runnable {
    @ParentCommand
    var parent: CliCommands? = null

    override fun run() {
        try {
            val currentOp = Main.debugger!!.currentOp
            parent!!.out!!.println((currentOp.index + 1).toString() + ":>\t" + currentOp.toString())
            if (currentOp is InvokeOp) {
                val invokeOp = currentOp
                invokeOp.isDebugMode = true
                Main.debugger!!.step()
                val invokedMethodExecutor = invokeOp.debuggedMethodExecutor
                Main.debugger!!.stepIntoInvoke(invokedMethodExecutor, invokeOp)
            } else {
                Main.debugger!!.step()
            }
        } catch (e: UnhandledVirtualException) {
            e.printStackTrace(parent!!.out)
        }
    }
}
