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

class ObfuscatedApplicationTest {
    @TempDir lateinit var temporaryDirectory: Path

    @Test fun obfuscatedApplicationRetainsBehaviorAfterAllStrategiesAndDexReload() {
        val fixture = File("src/test/resources/obfuscated-example")
        // Expected values come from the fixture source: WhiteNoise.convolutedMethod() returns ten * five, and
        // recoveredString() decodes the bytes of "secretMethod".
        val expected = mapOf(
            WHITE_NOISE to 50,
            PREFIX + "recoveredString()Ljava/lang/String;" to "secretMethod",
            PREFIX + "knownClass()Ljava/lang/Class;" to "java.lang.String",
            PREFIX + "constantPredicate()I" to 0,
            PREFIX + "getReflectiveField()Ljava/lang/Object;" to "field value",
        )
        val before = OptimizerTester.services(fixture)
        expected.forEach { (method, value) -> assertEquals(value, result(before, method), method) }
        val output = temporaryDirectory.resolve("application.dex")
        val report = Launcher().run(SimplifyOptionsParser.parse(arrayOf(fixture.path, "--include-types", "OptimizationWitness|WhiteNoise;->convolutedMethod", "--output", output.toString(), "--quiet")))
        val after = OptimizerTester.services(output.toFile())
        expected.forEach { (method, value) -> assertEquals(value, result(after, method), method) }
        assertEquals(0, report.failedMethodCount)
        assertEquals(0, report.skippedMethodCount)
        assertTrue(report.passLimitReachedMethods.isEmpty())
        assertEquals(report.targetMethodCount, report.optimizedMethodCount)
        val predicate = after.classIndex.methodView(PREFIX + "constantPredicate()I")!!
        assertFalse(predicate.instructionAddresses.any { predicate.opcodeAt(it) == Opcode.IF_EQ })
        // The decoded string folds into its literal, and the allocation that decoded it goes away.
        val recovered = after.classIndex.methodView(PREFIX + "recoveredString()Ljava/lang/String;")!!
        assertFalse(hasCall(recovered, "Ljava/lang/String;", "<init>"))
        assertTrue(recovered.instructionAddresses.any { recovered.opcodeAt(it) == Opcode.CONST_STRING })
        // String's initialization is unobservable, so loading it by name becomes const-class.
        val knownClass = after.classIndex.methodView(PREFIX + "knownClass()Ljava/lang/Class;")!!
        assertFalse(hasCall(knownClass, "Ljava/lang/Class;", "forName"))
        assertTrue(knownClass.instructionAddresses.any { knownClass.opcodeAt(it) == Opcode.CONST_CLASS })
        // The engine does not resolve this Method.invoke, so the call stays reflective.
        val reflective = after.classIndex.methodView(PREFIX + "invokeReflective()Ljava/lang/Object;")!!
        assertTrue(hasCall(reflective, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(report.optimizationCounts.getValue("constantized ops") > 0)
        assertTrue(report.optimizationCounts.getValue("constantized ifs") > 0)
        val second = temporaryDirectory.resolve("application-second.dex")
        val fixpoint = Launcher().run(SimplifyOptionsParser.parse(arrayOf(output.toString(), "--include-types", "OptimizationWitness|WhiteNoise;->convolutedMethod", "--output", second.toString(), "--quiet")))
        assertTrue(fixpoint.optimizationCounts.values.all { it == 0 })
        val reloaded = OptimizerTester.services(second.toFile())
        expected.forEach { (method, value) -> assertEquals(value, result(reloaded, method), method) }
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
        (trusted.knownString(address, register) as? Agreement.Agreed)?.let { return it.value }
        return (value.raw as? Class<*>)?.name ?: value.raw
    }

    companion object {
        const val PREFIX = "Lorg/cf/obfuscated/OptimizationWitness;->"
        const val WHITE_NOISE = "Lorg/cf/obfuscated/WhiteNoise;->convolutedMethod()I"
    }
}
