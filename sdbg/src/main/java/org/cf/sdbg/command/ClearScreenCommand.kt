package org.cf.sdbg.command

import org.jline.utils.InfoCmp
import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.io.IOException

@CommandLine.Command(name = "cls", aliases = ["clear"], mixinStandardHelpOptions = true,
        description = ["Clears the screen"])
class ClearScreenCommand : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands

    @Throws(IOException::class)
    override fun run() {
        val terminal = parent.reader.terminal
        terminal.puts(InfoCmp.Capability.clear_screen)
        terminal.flush()
    }
}
