package org.cf.sdbg.command

import org.cf.smalivm.type.VirtualField
import org.cf.util.ClassNameUtils
import picocli.CommandLine
import picocli.CommandLine.ParentCommand

sealed class PrintTarget
data class PrintTargetField(val virtualField: VirtualField) : PrintTarget()
data class PrintTargetRegister(val name: String, val register: Int) : PrintTarget()
object PrintTargetInvalid : PrintTarget()

@CommandLine.Command(name = "print", aliases = ["p"], mixinStandardHelpOptions = true,
        description = ["Print value stored in variable or field"])
class PrintCommand : DebuggerCommand() {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["register | parameter | field"])
    var target: String? = null

    private val registerRegex = "[vpr]\\d+".toRegex()

    private fun parseTarget(target: String?): PrintTarget {
        if (target == null) {
            return PrintTargetInvalid
        }
        val guessedType = ClassNameUtils.guessReferenceType(target)
        return when {
            guessedType == ClassNameUtils.ReferenceType.INTERNAL_CLASS_DESCRIPTOR -> {
                val parts = target.split("->".toRegex(), 2)
                val classDescriptor = parts[0]
                val fieldName = parts[1]
                val virtualClass = debugger.classManager.getVirtualClass(classDescriptor) ?: return PrintTargetInvalid
                val virtualField = virtualClass.getField(fieldName) ?: return PrintTargetInvalid
                PrintTargetField(virtualField)
            }
            target.toLowerCase() matches registerRegex -> {
                return when {
                    target.startsWith('p') -> {
                        val parameterStart = debugger.currentNode.context.methodState.parameterStart
                        val value = target.substring(1).toInt()
                        PrintTargetRegister("p${value}(r${value + parameterStart})", value + parameterStart)
                    }
                    else -> {
                        val value = target.substring(1).toInt()
                        PrintTargetRegister("v${value}", value)
                    }
                }
            }
            else -> PrintTargetInvalid
        }
    }

    private fun printInvalid(message: String) {
        parent.out.println("invalid target; $message")
    }

    override fun run() {
        when (val parsedTarget = parseTarget(target)) {
            is PrintTargetField -> {
                val accessor = debugger.virtualMachine.staticFieldAccessor
                val fieldItem = accessor.getField(debugger.currentNode.context, parsedTarget.virtualField)
                parent.out.println("${parsedTarget.virtualField}: $fieldItem")
            }
            is PrintTargetRegister -> {
                val methodState = debugger.currentNode.context.methodState
                val item = methodState.peekRegister(parsedTarget.register)
                parent.out.println("${parsedTarget.name}: $item")
            }
            is PrintTargetInvalid -> {
                printInvalid("unable to parse")
                return
            }
        }
    }
}
