package org.cf.simplify

import org.cf.simplify.strategy.OptimizationStrategy
import org.cf.simplify.strategy.StrategyInput
import org.cf.smalivm.core.*
import org.cf.smalivm.type.ClassManagerFactory
import java.io.File
import java.nio.file.Files

object OptimizerTester {
    fun services(path: File = File("src/test/resources/smali"), reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults()): VmServices =
        VmServices(ClassIndex(ClassManagerFactory().build(path, 20)), reflectionPolicy)

    fun execute(services: VmServices, signature: String, initial: Map<Int, Value> = emptyMap()): ExecutionGraph {
        val method = requireNotNull(services.classIndex.methodView(signature))
        val frame = Frame.entryFrame(method)
        initial.forEach { (register, value) -> frame.assign(register, value) }
        return services.newSession(method, Limits(), frame).run()
    }

    fun optimize(
        className: String,
        methodDescriptor: String,
        strategy: OptimizationStrategy,
        initial: Map<Int, Value> = emptyMap(),
        reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
    ): Optimization {
        val services = services(reflectionPolicy = reflectionPolicy)
        val signature = "$className->$methodDescriptor"
        val graph = execute(services, signature, initial)
        val input = StrategyInput(graph, services.methodEditor)
        val result = try {
            strategy.perform(input)
            input.transaction.commit()
        } finally {
            if (input.transaction.state == MethodEditTransactionState.OPEN) input.transaction.discard()
        }
        return Optimization(services, signature, graph, input, result, reflectionPolicy)
    }

    class Optimization internal constructor(
        val services: VmServices,
        private val signature: String,
        val graph: ExecutionGraph,
        val input: StrategyInput,
        val commitResult: MethodEditCommitResult,
        private val reflectionPolicy: ReflectionPolicy,
    ) {
        val method: MethodView get() = requireNotNull(services.classIndex.methodView(signature))
        fun reexecute(initial: Map<Int, Value> = emptyMap()) = execute(services, signature, initial)
        fun dexRoundTrip(initial: Map<Int, Value> = emptyMap()): ExecutionGraph {
            val file = Files.createTempFile("simplify-roundtrip-", ".dex").toFile()
            return try {
                ProgramDexWriter().write(services.currentSnapshot, file)
                execute(OptimizerTester.services(file, reflectionPolicy), signature, initial)
            } finally {
                Files.deleteIfExists(file.toPath())
            }
        }
    }
}
