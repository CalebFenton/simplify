package org.cf.sdbg.command

import org.cf.sdbg.Main
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "where", aliases = ["w"], mixinStandardHelpOptions = true, version = ["1.0"], description = ["Shows current instruction index and which function you are in"])
class Where : Runnable {
    @ParentCommand
    var parent: CliCommands? = null

    override fun run() {
        val instructionIndex = Main.debugger!!.currentIndex + 1
        val methodSignature = Main.debugger!!.currentMethodSignature
        parent!!.out!!.println("#$instructionIndex in $methodSignature")
    }
}
