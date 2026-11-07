package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.util.ClassNameUtils
import org.cf.util.ClassNameUtils.ReferenceType
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class ListTarget
data class ListTargetClass(val classDescriptor: String) : ListTarget()
data class ListTargetMethod(val methodSignature: String) : ListTarget()
data class ListTargetRange(val start: Int, val stop: Int = start) : ListTarget()
object ListTargetInvalid : ListTarget()

@CommandLine.Command(name = "list", aliases = ["l"], mixinStandardHelpOptions = true,
        description = ["List source code"])
class ListCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["- | index-number | start#,end# | class | method | class->method"])
    var target: String? = null

    private fun parseTarget(target: String?): ListTarget {
        if (target == null) return ListTargetMethod(currentView.signature)
        return when (ClassNameUtils.guessReferenceType(target)) {
            ReferenceType.INTERNAL_CLASS_DESCRIPTOR -> ListTargetClass(target)
            ReferenceType.INTERNAL_METHOD_SIGNATURE -> ListTargetMethod(target)
            ReferenceType.INTERNAL_METHOD_DESCRIPTOR -> ListTargetMethod("${currentView.definingClass.descriptor}->$target")
            else -> when {
                target == "-" -> debugger.currentInstructionIndex?.let { ListTargetRange(it) } ?: ListTargetInvalid
                target.contains(',') -> {
                    val parts = target.split(',', limit = 2)
                    val start = parts[0].toIntOrNull() ?: return ListTargetInvalid
                    val stop = parts[1].toIntOrNull() ?: return ListTargetInvalid
                    ListTargetRange(start, stop)
                }
                else -> target.toIntOrNull()?.let { ListTargetRange(it) } ?: ListTargetInvalid
            }
        }
    }

    override fun run() {
        when (val parsed = parseTarget(target)) {
            is ListTargetClass -> {
                if (!Main.listing.hasClass(parsed.classDescriptor)) {
                    parent.out.println("invalid target; class descriptor not found")
                    return
                }
                Main.listing.writeClass(parent.out, parsed.classDescriptor, debugger)
            }
            is ListTargetMethod -> {
                if (!Main.listing.writeMethod(parent.out, parsed.methodSignature, debugger)) {
                    parent.out.println("invalid target; method signature not found")
                }
            }
            is ListTargetRange -> {
                if (parsed.start < 0 || parsed.start > parsed.stop || parsed.stop >= currentView.instructionCount) {
                    parent.out.println("invalid target; instruction range out of bounds")
                    return
                }
                Main.listing.writeMethod(parent.out, currentView.signature, debugger, parsed.start, parsed.stop)
            }
            ListTargetInvalid -> parent.out.println("invalid target; unable to parse or no current instruction")
        }
    }
}
