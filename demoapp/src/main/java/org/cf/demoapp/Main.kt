package org.cf.demoapp

import java.io.File
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VmServices
import org.cf.smalivm.core.canTrustConsensus
import org.cf.smalivm.core.joinAll

/** Embedding examples that execute the guest smali of org.cf.demosmali in smalivm. */
object Main {
    private const val GUEST_CLASS = "Lorg/cf/demosmali/Main;"

    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size <= 1) { "Usage: java -jar demoapp.jar [smali-directory|dex|apk]" }
        val input = inputPath(args.firstOrNull())
        println("Demo input: ${input.absolutePath}")
        val services = VmServices(ClassIndex.open(input), demoReflectionPolicy())

        // The registration belongs to this VM alone. Printing is an explicit host side effect,
        // not permission to reflect arbitrary I/O methods or execute the guest on the host JVM.
        registerPrintlnEmulator(services)
        val main = method(services, "main([Ljava/lang/String;)V")
        val printParameter = method(services, "printParameter(I)V")
        val parameterLogic = method(services, "parameterLogic(I)I")

        // This fixture does not inspect args; the entry frame leaves the array unknown.
        execute(services, main, Frame.entryFrame(main), "Guest main")

        val printFrame = Frame.entryFrame(printParameter)
        val printArgument = printParameter.parameters.single()
        printFrame.assign(printArgument.register, Value.Known(42, printArgument.type))
        execute(services, printParameter, printFrame, "Known print parameter 42")

        // p0 is the unread receiver, kept typed but unknown; p1 is the integer argument.
        val receiver = parameterLogic.parameters[0]
        val argument = parameterLogic.parameters[1]
        val unknownFrame = Frame.entryFrame(parameterLogic)
        unknownFrame.assign(receiver.register, Value.Unknown(receiver.type))
        unknownFrame.assign(argument.register, Value.Unknown(argument.type))
        val unknownGraph = execute(services, parameterLogic, unknownFrame, "Unknown parameter")
        printReturnConsensus(services, unknownGraph, "Unknown parameter")

        val knownFrame = Frame.entryFrame(parameterLogic)
        knownFrame.assign(receiver.register, Value.Unknown(receiver.type))
        knownFrame.assign(argument.register, Value.Known(10, argument.type))
        val knownGraph = execute(services, parameterLogic, knownFrame, "Known parameter 10")
        printReturnConsensus(services, knownGraph, "Known parameter 10")
    }

    private fun inputPath(explicitPath: String?): File {
        if (explicitPath != null) {
            return File(explicitPath).also {
                require(it.exists()) { "Demo input does not exist: ${it.absolutePath}" }
            }
        }
        // Support both the repository root and :demoapp:run's module working directory.
        return listOf(File("demoapp/resources"), File("resources")).firstOrNull {
            File(it, "org/cf/demosmali/Main.smali").isFile
        } ?: error("Demo fixtures not found. Pass the path to demoapp/resources (or a DEX/APK containing the demo classes).")
    }

    private fun method(services: VmServices, descriptor: String): MethodView =
        requireNotNull(services.classIndex.methodView("$GUEST_CLASS->$descriptor")) {
            "Input is missing demo method $GUEST_CLASS->$descriptor"
        }

    private fun execute(services: VmServices, method: MethodView, frame: Frame, label: String): ExecutionGraph {
        val graph = services.newSession(method, entryFrame = frame).run()
        // A returned void main does not imply every nested operation produced a known value.
        println("$label: ${graph.completion()}")
        graph.outcomes.filterNot { it is Outcome.Returned }.forEach { println("  $it") }
        return graph
    }

    private fun printReturnConsensus(services: VmServices, graph: ExecutionGraph, label: String) {
        if (!graph.completion().canTrustConsensus) {
            println("$label: return consensus unavailable (incomplete exploration)")
            return
        }
        // Join actual normal-return outcomes, not a hard-coded physical return register.
        val returns = graph.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull { it.value }
        val consensus = joinAll(returns, services.classIndex)
        println("$label: return consensus = ${consensus ?: "no normal return"}")
    }
}
