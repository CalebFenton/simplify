package org.cf.sdbg.command

import org.cf.smalivm.type.VirtualField
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class PrintTarget
data class PrintTargetField(val fieldDescriptor: VirtualField) : PrintTarget()
data class PrintTargetRegister(val register: Int) : PrintTarget()
object PrintTargetInvalid : PrintTarget()

@CommandLine.Command(name = "print", aliases = ["p"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["Print value stored in variable or field"])
class PrintCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["register | parameter | field"])
    var target: String? = null

    private val registerRegex = "[vpr]\\d+".toRegex()

    private fun parseTarget(target: String?): PrintTarget {
        return when {
            target == null -> PrintTargetInvalid
            target.startsWith("L") || target.startsWith("[") && target.contains("->") -> {
                val parts = target.split("->")
                val classDescriptor = parts[0]
                val fieldName = parts[1]
                val virtualClass = debugger.classManager.getVirtualClass(classDescriptor) ?: return PrintTargetInvalid
                val virtualField = virtualClass.getField(fieldName) ?: return PrintTargetInvalid
                PrintTargetField(virtualField)
//                val accessor = debugger.virtualMachine.staticFieldAccessor
//                val fieldItem = accessor.getField(debugger.currentNode.context, virtualField)
//                parent.out.println(fieldItem)
            }
            target matches registerRegex -> {
//                when {
//                    target.startsWith('v') ->
//                }
                PrintTargetInvalid
            }
            else -> PrintTargetInvalid
        }
    }

    override fun run() {
//        Main.debugger.currentNode.context
    }
}
