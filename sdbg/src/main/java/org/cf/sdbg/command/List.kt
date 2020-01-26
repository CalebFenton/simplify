package org.cf.sdbg.command

import org.cf.sdbg.Main
import org.cf.smalivm.context.ExecutionGraph
import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.lang.Integer.min
import java.util.stream.Collectors.joining
import java.util.stream.IntStream

sealed class ParsedTarget
data class ParsedTargetClass(val className: String) : ParsedTarget()
data class ParsedTargetMethod(val methodSignature: String) : ParsedTarget()
data class ParsedTargetRange(val start: Int = -1, val stop: Int? = null) : ParsedTarget()
object ParsedTargetInvalid : ParsedTarget()

@CommandLine.Command(name = "list", aliases = ["l"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["List source code"])
class List : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["line-number | start#,end# | method | class->method"])
    var target: String? = null


    private fun parseTarget(target: String?): ParsedTarget {
        return when {
            target == null -> ParsedTargetMethod(Main.debugger.currentMethodSignature)
            target == "-" -> ParsedTargetRange(start = Main.debugger.currentIndex + 1)
            target.contains("(") -> {
                if (target.contains("->")) {
                    ParsedTargetMethod(target)
                } else {
                    val className = Main.debugger.executionGraph.method.className
                    ParsedTargetMethod("$className->$target")
                }
            }
            target.contains(",") -> {
                val parts = target.split(',', limit = 2)
                val start = parts[0].toIntOrNull() ?: return ParsedTargetInvalid
                val stop = parts[1].toIntOrNull() ?: return ParsedTargetInvalid
                ParsedTargetRange(start, stop)
            }
            target.startsWith("L") || target.startsWith("[") -> ParsedTargetClass(target)
            target.toIntOrNull() != null -> ParsedTargetRange(start = target.toInt())
            else -> ParsedTargetInvalid
        }
    }

    private fun printMethod(graph: ExecutionGraph, start: Int, stop: Int) {
        val smaliLines = graph.toSmali(true).split('\n')
        val realStop = if (start == stop) stop + 1 else min(stop, smaliLines.size)
        val currentIndex = Main.debugger.currentNode.index + 1
        val targetLines = IntStream.range(start, realStop + 1)
                .mapToObj { n: Int -> "$n${if (n == currentIndex) ":>" else ":"}\t${smaliLines[n - 1]}" }
                .collect(joining("\n"))
        parent.out.println(targetLines)

    }

    private fun printInvalid(message: String) {
        parent.out.println("invalid target; $message")
    }

    private fun printTarget(target: String?) {
        when (val parsedTarget = parseTarget(target)) {
            is ParsedTargetClass -> {
                val virtualType = Main.debugger.classManager.getVirtualClass(parsedTarget.className)
                if (virtualType == null) {
                    printInvalid("class name not found")
                    return
                }
                parent.out.println("not implemented yet! lol")
            }
            is ParsedTargetMethod -> {
                val virtualMethod = Main.debugger.classManager.getMethod(parsedTarget.methodSignature)
                if (virtualMethod == null) {
                    printInvalid("method signature not found")
                    return
                }
                val graph = Main.debugger.virtualMachine.spawnInstructionGraph(virtualMethod)
                printMethod(graph, 1, Int.MAX_VALUE)
            }
            is ParsedTargetRange -> {
                val start = parsedTarget.start
                val stop = parsedTarget.stop
                if (stop != null) {
                    if (start > stop) {
                        printInvalid("start# > end#")
                        return
                    } else if (start <= 0) {
                        printInvalid("start# <= 0")
                        return
                    }
                    printMethod(Main.debugger.executionGraph, parsedTarget.start, parsedTarget.stop)
                } else {
                    if (start <= 0) {
                        printInvalid("line-number <= 0")
                        return
                    }
                    printMethod(Main.debugger.executionGraph, parsedTarget.start, parsedTarget.start)
                }
            }
            is ParsedTargetInvalid -> {
                printInvalid("unable to parse")
                return
            }
        }
    }

    override fun run() {
        printTarget(target)
    }
}
