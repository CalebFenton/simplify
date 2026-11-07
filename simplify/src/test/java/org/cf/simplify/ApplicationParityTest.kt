package org.cf.simplify

import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ApplicationParityTest {
    @TempDir lateinit var temporaryDirectory: Path

    @Test fun obfuscatedApplicationRetainsBehaviorAfterAllStrategiesAndDexReload() {
        val fixture = File("src/test/resources/obfuscated-example")
        val before = OptimizerTester.services(fixture)
        val witnesses = listOf(
            WHITE_NOISE,
            PREFIX + "recoveredString()Ljava/lang/String;",
            PREFIX + "knownClass()Ljava/lang/Class;",
            PREFIX + "constantPredicate()I",
            PREFIX + "invokeReflective()Ljava/lang/Object;",
            PREFIX + "getReflectiveField()Ljava/lang/Object;",
        )
        val expected = witnesses.associateWith { result(before, it) }
        val output = temporaryDirectory.resolve("application.dex")
        val report = Launcher().run(SimplifyOptionsParser.parse(arrayOf(fixture.path, "--include-types", "Plan14Witness|WhiteNoise;->convolutedMethod", "--output", output.toString(), "--quiet")))
        val after = OptimizerTester.services(output.toFile())
        witnesses.forEach { assertEquals(expected[it], result(after, it), it) }
        assertEquals(0, report.failedMethodCount)
        assertEquals(0, report.skippedMethodCount)
        assertTrue(report.passLimitReachedMethods.isEmpty())
        assertEquals(8, report.targetMethodCount)
        assertEquals(8, report.optimizedMethodCount)
        assertEquals(4, report.strategyOptimizationCounts.size)
        val predicate = after.classIndex.methodView(PREFIX + "constantPredicate()I")!!
        assertFalse(predicate.instructionAddresses.any { predicate.opcodeAt(it) == Opcode.IF_EQ })
        val recovered = after.classIndex.methodView(PREFIX + "recoveredString()Ljava/lang/String;")!!
        assertTrue(hasCall(recovered, "Ljava/lang/String;", "<init>"))
        val knownClass = after.classIndex.methodView(PREFIX + "knownClass()Ljava/lang/Class;")!!
        assertTrue(hasCall(knownClass, "Ljava/lang/Class;", "forName"))
        // Reflective invocation is not a license to remove calls with weak/unknown effects.
        // The original Plan14 numerical counts depended on that unsafe rewrite and are not parity requirements.
        assertTrue(report.optimizationCounts.getValue("constantized ops") > 0)
        assertTrue(report.optimizationCounts.getValue("constantized ifs") > 0)
        val second = temporaryDirectory.resolve("application-second.dex")
        val fixpoint = Launcher().run(SimplifyOptionsParser.parse(arrayOf(output.toString(), "--include-types", "Plan14Witness|WhiteNoise;->convolutedMethod", "--output", second.toString(), "--quiet")))
        assertTrue(fixpoint.optimizationCounts.values.all { it == 0 })
        val reloaded = OptimizerTester.services(second.toFile())
        witnesses.forEach { assertEquals(expected[it], result(reloaded, it), it) }
    }

    private fun hasCall(method: MethodView, owner: String, name: String) =
        method.instructionAddresses.any {
            val reference = (method.instructionAt(it) as? ReferenceInstruction)?.reference as? MethodReference
            reference?.definingClass == owner && reference.name == name
        }

    private fun result(services: VmServices, signature: String): Any? {
        val method = requireNotNull(services.classIndex.methodView(signature))
        val graph = services.newSession(method, Limits(10_000, 1_000_000, 300_000, 50)).run()
        assertEquals(CompletionStatus.Complete, graph.completion(), signature)
        val trusted = requireNotNull(graph.trusted())
        val address = method.instructionAddresses.first { method.opcodeAt(it) in setOf(Opcode.RETURN, Opcode.RETURN_OBJECT, Opcode.RETURN_WIDE) }
        val register = (method.instructionAt(address) as OneRegisterInstruction).registerA
        val value = trusted.terminatingConsensus(register)
        if (value !is Value.Known) return value.toString()
        trusted.knownString(address, register)?.let { return it }
        return (value.raw as? Class<*>)?.name ?: value.raw
    }

    companion object {
        const val PREFIX = "Lorg/cf/obfuscated/Plan14Witness;->"
        const val WHITE_NOISE = "Lorg/cf/obfuscated/WhiteNoise;->convolutedMethod()I"
    }
}
