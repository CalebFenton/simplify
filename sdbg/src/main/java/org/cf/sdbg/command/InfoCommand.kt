package org.cf.sdbg.command

import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class InfoTarget
data class InfoTargetClass(val classDescriptor: String) : InfoTarget()
data class InfoTargetMethod(val methodSignature: String) : InfoTarget()
data class InfoTargetRange(val start: Int = -1, val stop: Int? = null) : InfoTarget()
object InfoTargetInvalid : InfoTarget()

@CommandLine.Command(name = "info", aliases = ["i"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["List information about target"])
class InfoCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["break | breakpoints | registers"])
    var target: String? = null

    private enum class InfoTargetType {
        REGISTERS, BREAK, INVALID
    }

    private fun parseTarget(target: String?): InfoTargetType {
        return when {
            target == null -> InfoTargetType.REGISTERS
            target.toLowerCase() == "registers" -> InfoTargetType.REGISTERS
            target.toLowerCase() == "break" -> InfoTargetType.BREAK
            target.toLowerCase() == "breakpoints" -> InfoTargetType.BREAK
            else -> InfoTargetType.INVALID
        }
    }

    private fun printRegisterInfo() {
        val currentNode = debugger.currentNode
        val context = currentNode.context
        val methodState = context.methodState.toString(false)
        parent.out.println(methodState)
    }

    private fun printBreakInfo() {
        val breakpoints = debugger.breakpoints.sorted()
        parent.out.println("breakpoint count=${breakpoints.size}, list:\n${breakpoints.joinToString { "\n" }}")
    }

    override fun run() {
        val parsedTarget = parseTarget(target)
        when (parsedTarget) {
            InfoTargetType.REGISTERS -> printRegisterInfo()
            InfoTargetType.BREAK -> printBreakInfo()
            InfoTargetType.INVALID -> {
                parent.out.println("invalid target")
                return
            }
        }
    }

}
