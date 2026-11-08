package org.cf.sdbg.command

import org.cf.sdbg.SmaliReference
import org.cf.smalivm.core.MethodSignature
import picocli.CommandLine

@CommandLine.Command(name = "list", aliases = ["l"], mixinStandardHelpOptions = true,
        description = ["List source code"])
internal class ListCommand : DebuggerCommand() {
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["- | index-number | start#,end# | class | method | class->method"])
    var target: String? = null

    private sealed interface Target {
        data class Class(val descriptor: String) : Target
        data class Method(val signature: MethodSignature) : Target
        /** An inclusive instruction range in the current method, not yet checked against its bounds. */
        data class Range(val start: Long, val stop: Long) : Target
        data class Invalid(val reason: String) : Target
    }

    private fun parseTarget(target: String?): Target {
        if (target == null) return Target.Method(currentView.signature)
        if (target == "-") {
            val index = debugger.currentInstructionIndex?.toLong() ?: return Target.Invalid("no current instruction")
            return Target.Range(index, index)
        }
        RANGE.matchEntire(target)?.let { match ->
            val start = match.groupValues[1].toLongOrNull() ?: return Target.Invalid(RANGE_OUT_OF_BOUNDS)
            val stop = match.groupValues[2].ifEmpty { match.groupValues[1] }.toLongOrNull()
                ?: return Target.Invalid(RANGE_OUT_OF_BOUNDS)
            return Target.Range(start, stop)
        }
        return when (val reference = SmaliReference.parse(target, currentView.definingClass)) {
            is SmaliReference.Type -> Target.Class(reference.type.descriptor)
            is SmaliReference.Method -> Target.Method(reference.signature)
            is SmaliReference.Field -> Target.Invalid("expected a class or method, got field ${reference.signature}")
            is SmaliReference.Malformed -> Target.Invalid(reference.reason)
        }
    }

    override fun run() {
        val listing = debugSession.listing
        when (val parsed = parseTarget(target)) {
            is Target.Class -> {
                if (!listing.hasClass(parsed.descriptor)) {
                    printInvalid("class descriptor not found")
                    return
                }
                listing.writeClass(out, parsed.descriptor, debugger)
            }
            is Target.Method -> {
                if (!listing.writeMethod(out, parsed.signature, debugger)) printInvalid("method signature not found")
            }
            is Target.Range -> {
                if (parsed.start > parsed.stop || parsed.stop >= currentView.instructionCount) {
                    printInvalid(RANGE_OUT_OF_BOUNDS)
                    return
                }
                listing.writeMethod(out, currentView.signature, debugger, parsed.start.toInt(), parsed.stop.toInt())
            }
            is Target.Invalid -> printInvalid(parsed.reason)
        }
    }

    private companion object {
        const val RANGE_OUT_OF_BOUNDS = "instruction range out of bounds"
        /** `index` or `start,stop`; both bounds are inclusive. */
        val RANGE = Regex("(\\d+)(?:,(\\d+))?")
    }
}
