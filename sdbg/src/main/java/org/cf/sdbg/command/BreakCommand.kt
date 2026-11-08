package org.cf.sdbg.command

import org.cf.sdbg.SmaliReference
import org.cf.smalivm.core.Breakpoint
import org.cf.smalivm.core.MethodLookup
import org.cf.smalivm.core.MethodSignature
import picocli.CommandLine

@CommandLine.Command(name = "break", aliases = ["b"], mixinStandardHelpOptions = true,
        description = ["Suspend program at specified function, instruction index, or index offset"])
internal class BreakCommand : DebuggerCommand() {
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["index-number | +offset | -offset | method | class->method"])
    var target: String? = null

    private sealed interface Target {
        /** An instruction index in the current method, not yet checked against its bounds. */
        data class Index(val index: Long) : Target
        data class Method(val signature: MethodSignature) : Target
        data class Invalid(val reason: String) : Target
    }

    private fun parseTarget(target: String?): Target {
        val current = debugger.currentInstructionIndex
        if (target == null) return current?.let { Target.Index(it.toLong()) } ?: Target.Invalid(NO_CURRENT_INSTRUCTION)
        if (INDEX.matches(target)) return target.toLongOrNull()?.let(Target::Index) ?: Target.Invalid(INDEX_OUT_OF_RANGE)
        if (OFFSET.matches(target)) {
            current ?: return Target.Invalid(NO_CURRENT_INSTRUCTION)
            val offset = target.toLongOrNull() ?: return Target.Invalid(INDEX_OUT_OF_RANGE)
            return Target.Index(current + offset)
        }
        return when (val reference = SmaliReference.parse(target, currentView.definingClass)) {
            is SmaliReference.Method -> Target.Method(reference.signature)
            is SmaliReference.Type -> Target.Invalid("expected a method, got class ${reference.type}")
            is SmaliReference.Field -> Target.Invalid("expected a method, got field ${reference.signature}")
            is SmaliReference.Malformed -> Target.Invalid(reference.reason)
        }
    }

    override fun run() {
        when (val parsed = parseTarget(target)) {
            is Target.Method -> {
                val lookup = debugSession.services.classIndex.lookupMethod(parsed.signature)
                val method = (lookup as? MethodLookup.Runnable)?.view
                if (method == null || method.instructionCount == 0) {
                    printInvalid("concrete method not found")
                    return
                }
                debugger.addBreakpoint(Breakpoint(method.signature, 0))
                out.println("breakpoint set for ${method.signature}")
            }
            is Target.Index -> {
                val method = currentView
                if (parsed.index !in 0 until method.instructionCount) {
                    printInvalid(INDEX_OUT_OF_RANGE)
                    return
                }
                debugger.addBreakpoint(Breakpoint(method.signature, parsed.index.toInt()))
                out.println("breakpoint set for ${method.signature} @ ${parsed.index}")
            }
            is Target.Invalid -> printInvalid(parsed.reason)
        }
    }

    private companion object {
        const val NO_CURRENT_INSTRUCTION = "no current instruction"
        const val INDEX_OUT_OF_RANGE = "index out of range"
        val INDEX = Regex("\\d+")
        val OFFSET = Regex("[+-]\\d+")
    }
}
