package org.cf.sdbg.command

import org.cf.sdbg.Main
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "info", aliases = ["i"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Info"])
class Info : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands

    override fun run() {
        val currentNode = Main.debugger.currentNode
        val context = currentNode.context
        val methodState = context.methodState.toString(false)
        parent.out.println(methodState)
    }
}
