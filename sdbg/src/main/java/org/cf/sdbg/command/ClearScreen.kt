package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.io.IOException
import java.util.concurrent.Callable

/**
 * Command that clears the screen.
 */
@CommandLine.Command(name = "cls", aliases = ["clear"], mixinStandardHelpOptions = true, version = ["1.0"], description = ["Clears the screen"])
class ClearScreen : Callable<Void?> {
    @ParentCommand
    var parent: CliCommands? = null

    @Throws(IOException::class)
    override fun call(): Void? {
        parent!!.reader!!.clearScreen()
        return null
    }
}
