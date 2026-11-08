package org.cf.sdbg.command

import picocli.CommandLine

@CommandLine.Command(name = "continue", aliases = ["c"], mixinStandardHelpOptions = true,
        description = ["Continue executing until next break point"])
internal class ContinueCommand : DebuggerCommand() {
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "number",
            description = ["Ignore this many breakpoints. Useful when inside a loop."])
    var target: String? = null

    override fun run() {
        val text = target
        val count = if (text == null) 0 else text.toIntOrNull()
        if (count == null || count < 0) {
            printInvalid("expected a nonnegative breakpoint count")
            return
        }
        var ignored = count
        while (ignored >= 0) {
            debugger.runToBreakpoint()
            if (debugger.isFinished) {
                printFinished()
                break
            } else if (debugger.isAtBreakpoint()) {
                out.println("hit breakpoint ${debugger.cursor!!.view.signature} @ ${debugger.currentInstructionIndex}")
                ignored--
            }
        }
    }
}
