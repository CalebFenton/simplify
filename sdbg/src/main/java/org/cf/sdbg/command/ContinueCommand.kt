package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

@CommandLine.Command(name = "continue", aliases = ["c"], mixinStandardHelpOptions = true,
        description = ["Continue executing until next break point"])
class ContinueCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "number",
            description = ["Ignore this many breakpoints. Useful when inside a loop."])
    var target: String? = null

    override fun run() {
        var number = if (target == null) 0 else target?.toIntOrNull()
        if (number == null || number < 0) {
            parent.out.println("invalid target; expected a nonnegative breakpoint count")
            return
        }
        while (number >= 0) {
            debugger.runToBreakpoint()
            if (debugger.isFinished) {
                printFinished(parent)
                break
            } else if (debugger.isAtBreakpoint()) {
                parent.out.println("hit breakpoint ${debugger.cursor!!.view.signature} @ ${debugger.currentInstructionIndex}")
                number--
            }
        }
    }
}
