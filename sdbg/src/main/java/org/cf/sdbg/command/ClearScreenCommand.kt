package org.cf.sdbg.command

import org.jline.utils.InfoCmp
import picocli.CommandLine

@CommandLine.Command(name = "cls", aliases = ["clear"], mixinStandardHelpOptions = true,
        description = ["Clears the screen"])
internal class ClearScreenCommand : DebuggerCommand() {
    override fun run() {
        parent.terminal.puts(InfoCmp.Capability.clear_screen)
        parent.terminal.flush()
    }
}
