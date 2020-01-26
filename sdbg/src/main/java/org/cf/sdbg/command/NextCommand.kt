package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.exception.UnhandledVirtualException
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "next", aliases = ["n"], mixinStandardHelpOptions = true, version = ["1.0"], description = ["Execute next line of code without entering methods"])
class NextCommand : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        try {
            val currentOp = Main.debugger.currentOp
            parent.out.println((currentOp.index + 1).toString() + ":>\t" + currentOp.toString())
            Main.debugger.step()
        } catch (e: UnhandledVirtualException) {
            e.printStackTrace(parent.out)
        }
    }
}
