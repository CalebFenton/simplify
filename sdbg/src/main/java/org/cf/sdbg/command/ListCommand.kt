package org.cf.sdbg.command

import org.cf.smalivm.context.ExecutionGraph
import org.cf.util.ClassNameUtils
import org.cf.util.ClassNameUtils.ReferenceType
import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.Adaptors.MethodDefinition
import org.jf.baksmali.BaksmaliOptions
import org.jf.dexlib2.util.ReferenceUtil
import org.jf.util.IndentingWriter
import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.io.StringWriter
import java.lang.Integer.min
import java.util.stream.IntStream

sealed class ListTarget
data class ListTargetClass(val classDescriptor: String) : ListTarget()
data class ListTargetMethod(val methodSignature: String) : ListTarget()
data class ListTargetRange(val start: Int = -1, val stop: Int? = null) : ListTarget()
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
        if (target == null) {
            return ListTargetMethod(debugger.currentMethodSignature)
        }
        val guessedType = ClassNameUtils.guessReferenceType(target)
        return when {
            target == "-" -> ListTargetRange(start = debugger.currentIndex)
            guessedType == ReferenceType.INTERNAL_CLASS_DESCRIPTOR -> ListTargetClass(target)
            guessedType == ReferenceType.INTERNAL_METHOD_SIGNATURE -> ListTargetMethod(target)
            guessedType == ReferenceType.INTERNAL_METHOD_DESCRIPTOR -> {
                val className = debugger.executionGraph.method.className
                ListTargetMethod("$className->$target")
            }
            target.contains(",") -> {
                val parts = target.split(',', limit = 2)
                val start = parts[0].toIntOrNull() ?: return ListTargetInvalid
                val stop = parts[1].toIntOrNull() ?: return ListTargetInvalid
                ListTargetRange(start, stop)
            }
            target.toIntOrNull() != null -> ListTargetRange(start = target.toInt())
            else -> ListTargetInvalid
        }
    }

    private fun getIndexPostfix(methodSignature: String, lineNumber: Int): String {
        val sb: StringBuilder = StringBuilder()
        if (debugger.isBreakpoint(methodSignature, lineNumber)) {
            sb.append('b')
        }
        if (methodSignature == debugger.currentMethodSignature && lineNumber == debugger.currentIndex) {
            sb.append('>')
        }
        return sb.toString()
    }

    private fun printMethod(graph: ExecutionGraph, start: Int, stop: Int) {
        val smaliLines = graph.toSmali(true).split('\n')
        val realStop = if (start == stop) stop + 1 else min(stop, smaliLines.size)
        val method = graph.method

        val classDef = graph.method.definingClass.classDef
        val options = BaksmaliOptions()
        options.sequentialLabels = true
        options.codeOffsets = true
        options.localsDirective = true
        val classDefinition = ClassDefinition(options, classDef);
        val methodDefinition = MethodDefinition(classDefinition, method.methodDefinition, method.methodDefinition.implementation!!)
        val methodString = getMethodString(methodDefinition, start, realStop)
        parent.out.println(methodString)
    }

    private fun getMethodString(methodDefinition: MethodDefinition, start: Int, stop: Int): String {
        val stringWriter = StringWriter()
        val writer = IndentingWriter(stringWriter)
        methodDefinition.writeTo(writer)
        val rawLines = stringWriter.toString().split("\n").toMutableList()
        val signature = ReferenceUtil.getMethodDescriptor(methodDefinition.method)

        // I like seeing the full method signature.
        val parts = rawLines[0].split(' ').toMutableList()
        parts[parts.size - 1] = signature
        rawLines[0] = parts.joinToString(" ")

        var index = 0
        IntStream.range(0, rawLines.size).forEach { n: Int ->
            run {
                if (rawLines[n].startsWith(" ")) {
                    val trimLine = rawLines[n].trim()
                    if (trimLine.startsWith("#@")) {
                        rawLines[n] = "\n"

                        val sb = StringBuilder()
                        sb.append("$index:${getIndexPostfix(signature, index)}")
                        while (sb.length < 8) {
                            sb.append(' ')
                        }
                        val prefix = sb.toString()
                        val postfix = "  $trimLine"
                        val instructionLine = "${rawLines[n + 1].replaceFirst("    ", prefix)}$postfix"
                        if (index < start || index > stop) {
                            rawLines[n + 1] = "\n"
                        } else {
                            rawLines[n + 1] = instructionLine
                        }
                        index++
                    } else {
                        rawLines[n] = rawLines[n].replace("    ", "        ")
                    }
                }
            }
        }

        return rawLines
                .filter { l -> l.trim().isNotEmpty() }
                .joinToString("\n")
    }

    private fun printClass(classDescriptor: String) {
        val options = BaksmaliOptions()
        options.sequentialLabels = true
        options.codeOffsets = true
        options.localsDirective = true
        val classDef = debugger.classManager.getVirtualClass(classDescriptor).classDef
        val classDefinition = ClassDefinition(options, classDef)
        val writer = IndentingWriter(parent.out)
        classDefinition.writeTo(writer)
    }

    private fun printInvalid(message: String) {
        parent.out.println("invalid target; $message")
    }

    private fun printTarget(target: String?) {
        when (val parsedTarget = parseTarget(target)) {
            is ListTargetClass -> {
                val virtualType = debugger.classManager.getVirtualClass(parsedTarget.classDescriptor)
                if (virtualType == null) {
                    printInvalid("class descriptor not found")
                    return
                }
                printClass(parsedTarget.classDescriptor)
            }
            is ListTargetMethod -> {
                val virtualMethod = debugger.classManager.getMethod(parsedTarget.methodSignature)
                if (virtualMethod == null) {
                    printInvalid("method signature not found")
                    return
                }
                val graph = debugger.virtualMachine.spawnInstructionGraph(virtualMethod)
                printMethod(graph, 0, Int.MAX_VALUE)
            }
            is ListTargetRange -> {
                val start = parsedTarget.start
                val stop = parsedTarget.stop
                if (stop != null) {
                    if (start > stop) {
                        printInvalid("start# > end#")
                        return
                    } else if (start < 0) {
                        printInvalid("start# < 0")
                        return
                    }
                    printMethod(debugger.executionGraph, parsedTarget.start, parsedTarget.stop)
                } else {
                    if (start < 0) {
                        printInvalid("line-number < 0")
                        return
                    }
                    printMethod(debugger.executionGraph, parsedTarget.start, parsedTarget.start)
                }
            }
            is ListTargetInvalid -> {
                printInvalid("unable to parse")
                return
            }
        }
    }

    override fun run() {
        printTarget(target)
    }
}
