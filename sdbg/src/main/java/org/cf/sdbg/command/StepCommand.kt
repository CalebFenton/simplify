package org.cf.sdbg.command

import org.cf.smalivm.core.Debugger
import picocli.CommandLine

/** Prints the instruction about to run, advances with [advance], and reports when execution has finished. */
internal sealed class SteppingCommand(private val advance: (Debugger) -> Unit) : DebuggerCommand() {
    override fun run() {
        val cursor = debugger.cursor
        val index = debugger.currentInstructionIndex
        if (cursor != null && index != null) {
            out.println("$index:>\t${cursor.op}")
            advance(debugger)
        }
        if (debugger.isFinished) printFinished()
    }
}

@CommandLine.Command(name = "step", aliases = ["s"], mixinStandardHelpOptions = true,
        description = ["Step to next line of code including stepping into methods"])
internal class StepCommand : SteppingCommand({ it.stepInto() })

@CommandLine.Command(name = "next", aliases = ["n"], mixinStandardHelpOptions = true,
        description = ["Execute next line of code without entering methods"])
internal class NextCommand : SteppingCommand({ it.stepOver() })
