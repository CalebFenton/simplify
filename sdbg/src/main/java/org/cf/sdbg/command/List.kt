package org.cf.sdbg.command

import org.cf.sdbg.Main
import picocli.CommandLine
import picocli.CommandLine.ParentCommand
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

@CommandLine.Command(name = "list", aliases = ["l"], mixinStandardHelpOptions = true, version = ["1.0"],
        description = ["List source code"])
class List : Runnable {
    @ParentCommand
    lateinit var parent: CliCommands
    @CommandLine.Parameters(index = "0", arity = "0..1", paramLabel = "TARGET",
            description = ["line-number | start#,end# | method | class->method"])
    var target: String? = null

    override fun run() {
        var graph = Main.debugger.executionGraph
        var start = 0
        var end = 0
        if (target != null) {
            if (target!!.contains("(")) {
                val methodSignature: String
                methodSignature = if (target!!.contains("->")) {
                    target!!
                } else {
                    val className = Main.debugger.executionGraph.method.className
                    "$className->$target"
                }
                val virtualMethod = Main.debugger.classManager.getMethod(methodSignature)
                if (virtualMethod == null) {
                    parent.out.println("invalid target; method signature not found")
                    return
                }
                graph = Main.debugger.virtualMachine.spawnInstructionGraph(virtualMethod)
            } else if (target!!.contains(",")) {
                val positions = Arrays.stream(target!!.split(",".toRegex(), 2).toTypedArray())
                        .mapToInt { s: String -> s.toInt() }
                        .toArray()
                start = positions[0]
                end = positions[1]
                if (start > end) {
                    parent.out.println("invalid target; start# > end#")
                    return
                }
                if (start <= 0) {
                    parent.out.println("invalid target; start# <= 0")
                    return
                }
            } else {
                try {
                    start = target!!.toInt()
                    end = start
                    if (start <= 0) {
                        parent.out.println("invalid target; line-number <= 0")
                        return
                    }
                } catch (ex: NumberFormatException) {
                    parent.out.println("invalid target; unable to parse line-number")
                    return
                }
            }
        }
        val theLines = graph.toSmali(true).split("\n".toRegex()).toTypedArray()
        if (start == 0) {
            end = theLines.size - 1
        } else {
            start--
            end--
            if (end >= theLines.size) {
                end = theLines.size - 1
            }
        }
        val instructionIndex = Main.debugger.currentNode.index
        val lines = IntStream.range(start, end + 1)
                .mapToObj { n: Int -> (n + 1).toString() + (if (n == instructionIndex) ":>" else ":") + "\t" + theLines[n] }
                .collect(Collectors.toList())
        val code = java.lang.String.join("\n", lines)
        parent.out.println(code)
    }
}
