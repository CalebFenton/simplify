package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.io.IOException
import java.util.concurrent.Callable

@CommandLine.Command(name = "cls", aliases = ["clear"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Clears the screen"])
class ClearScreen : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands

    @Throws(IOException::class)
    override fun run() {
        parent.reader.clearScreen()
    }
}
