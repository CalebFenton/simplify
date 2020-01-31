package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "continue", aliases = ["c"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Continue executing until next break point"])
class ContinueCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "number",
            description = ["Ignore this many breakpoints. Useful when inside a loop."])
    var target: String? = null

    override fun run() {
        var number = target?.toIntOrNull() ?: 0
        while (number >= 0) {
            debugger.run()
            if (debugger.isFinished) {
                break
            } else if (debugger.isAtBreakpoint) {
                number--
            }
        }
    }
}
