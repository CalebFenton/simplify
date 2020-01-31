package org.cf.sdbg.command

import org.cf.util.ClassNameUtils
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class BreakTarget
data class BreakTargetMethod(val methodSignature: String) : BreakTarget()
data class BreakTargetIndex(val index: Int) : BreakTarget()
object BreakTargetInvalid : BreakTarget()

@CommandLine.Command(name = "break", aliases = ["b"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Suspend program at specified function, instruction index, or index offset"])
class BreakCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["index-number | method | class->method | +offset | -offset"])
    var target: String? = null

    private fun parseTarget(target: String?): BreakTarget {
        if (target == null) {
            return BreakTargetIndex(debugger.currentIndex)
        }
        val guessedType = ClassNameUtils.guessReferenceType(target)
        return when {
            target.startsWith('-') || target.startsWith('+') -> {
                val offset = target.toIntOrNull() ?: return BreakTargetInvalid
                BreakTargetIndex(debugger.currentIndex + offset)
            }
            guessedType == ClassNameUtils.ReferenceType.INTERNAL_METHOD_SIGNATURE -> BreakTargetMethod(target)
            guessedType == ClassNameUtils.ReferenceType.INTERNAL_METHOD_DESCRIPTOR -> {
                val className = debugger.executionGraph.method.className
                BreakTargetMethod("$className->$target")
            }
            target.toIntOrNull() != null -> BreakTargetIndex(index = target.toInt())
            else -> BreakTargetInvalid
        }
    }

    private fun printInvalid(message: String) {
        parent.out.println("invalid target; $message")
    }

    override fun run() {
        when (val parsedTarget = parseTarget(target)) {
            is BreakTargetMethod -> {
                val method = debugger.classManager.getMethod(parsedTarget.methodSignature)
                if (method == null) {
                    printInvalid("method not found")
                    return
                }
                if (!method.hasImplementation()) {
                    printInvalid("method has no implementation")
                }
                debugger.addBreakpoint(parsedTarget.methodSignature, 1)
                parent.out.println("breakpoint set for ${parsedTarget.methodSignature}")
            }
            is BreakTargetIndex -> {
                val method = debugger.currentMethod
                if (method.implementation.instructions.size < parsedTarget.index - 1) {
                    parent.out.println("instr size: ${method.implementation.instructions.size}")
                    printInvalid("index out of range")
                    return
                }
                debugger.addBreakpoint(debugger.currentMethodSignature, parsedTarget.index)
                parent.out.println("breakpoint set for ${debugger.currentMethodSignature} @ ${parsedTarget.index}")
            }
            is BreakTargetInvalid -> {
                printInvalid("unable to parse")
                return
            }
        }
    }
}
