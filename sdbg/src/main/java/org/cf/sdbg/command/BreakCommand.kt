package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.core.Breakpoint
import org.cf.smalivm.core.MethodLookup
import org.cf.smalivm.core.VirtualType
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class BreakTarget
data class BreakTargetMethod(val methodSignature: String) : BreakTarget()
data class BreakTargetIndex(val index: Int) : BreakTarget()
object BreakTargetInvalid : BreakTarget()

@CommandLine.Command(name = "break", aliases = ["b"], mixinStandardHelpOptions = true, version = [Main.version],
        description = ["Suspend program at specified function, instruction index, or index offset"])
class BreakCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["index-number | method | class->method | +offset | -offset"])
    var target: String? = null

    private fun parseTarget(target: String?): BreakTarget {
        if (target == null) {
            return debugger.currentInstructionIndex?.let { BreakTargetIndex(it) } ?: BreakTargetInvalid
        }
        val guessedType = ReferenceType.guess(target)
        return when {
            target.startsWith('-') || target.startsWith('+') -> {
                val offset = target.toIntOrNull() ?: return BreakTargetInvalid
                val index = debugger.currentInstructionIndex ?: return BreakTargetInvalid
                val resolved = index.toLong() + offset
                if (resolved !in 0..Int.MAX_VALUE.toLong()) return BreakTargetInvalid
                BreakTargetIndex(resolved.toInt())
            }
            guessedType == ReferenceType.INTERNAL_METHOD_SIGNATURE -> BreakTargetMethod(target)
            guessedType == ReferenceType.INTERNAL_METHOD_DESCRIPTOR -> {
                val className = currentView.definingClass.descriptor
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
                val lookup = Main.services.classIndex.lookupMethod(
                    VirtualType.of(parsedTarget.methodSignature.substringBefore("->")),
                    parsedTarget.methodSignature.substringAfter("->"))
                val method = (lookup as? MethodLookup.Runnable)?.view
                if (method == null || method.instructionCount == 0) {
                    printInvalid("concrete method not found")
                    return
                }
                debugger.addBreakpoint(Breakpoint(method.signature, 0))
                parent.out.println("breakpoint set for ${method.signature}")
            }
            is BreakTargetIndex -> {
                val method = currentView
                if (parsedTarget.index !in 0 until method.instructionCount) {
                    printInvalid("index out of range")
                    return
                }
                debugger.addBreakpoint(Breakpoint(method.signature, parsedTarget.index))
                parent.out.println("breakpoint set for ${method.signature} @ ${parsedTarget.index}")
            }
            is BreakTargetInvalid -> {
                printInvalid("unable to parse")
                return
            }
        }
    }
}
