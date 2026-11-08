package org.cf.simplify

import org.cf.simplify.strategy.OptimizationStrategy
import org.cf.smalivm.core.*
import java.io.File
import java.nio.file.Files

object OptimizerTester {
    fun services(path: File = File("src/test/resources/smali"), reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults()): VmServices =
        VmServices(ClassIndex.open(path, 20), reflectionPolicy)

    fun execute(services: VmServices, signature: String): ExecutionGraph =
        services.newSession(requireNotNull(services.classIndex.methodView(signature)), Limits()).run()

    /** Runs [strategy] through [Optimizer] for one committed pass over a fixture in `src/test/resources/smali`. */
    fun optimize(
        className: String,
        methodDescriptor: String,
        strategy: OptimizationStrategy,
        reflectionPolicy: ReflectionPolicy = ReflectionPolicy.defaults(),
    ): Optimization {
        val services = services(reflectionPolicy = reflectionPolicy)
        val signature = "$className->$methodDescriptor"
        val result = Optimizer(services, Limits(), 1, listOf(strategy)).optimize(signature)
        return Optimization(services, signature, result, reflectionPolicy)
    }

    class Optimization internal constructor(
        val services: VmServices,
        private val signature: String,
        val result: Optimizer.Result,
        private val reflectionPolicy: ReflectionPolicy,
    ) {
        val method: MethodView get() = requireNotNull(services.classIndex.methodView(signature))
        fun reexecute() = execute(services, signature)
        fun dexRoundTrip(): ExecutionGraph {
            val file = Files.createTempFile("simplify-roundtrip-", ".dex").toFile()
            return try {
                ProgramDexWriter().write(services.currentSnapshot, file)
                execute(OptimizerTester.services(file, reflectionPolicy), signature)
            } finally {
                Files.deleteIfExists(file.toPath())
            }
        }
    }
}
