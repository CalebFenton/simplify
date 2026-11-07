package org.cf.simplify.strategy

import org.cf.simplify.OptimizerTester
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UnreflectionStrategyTest {
    private val owner = "Lunreflection_kotlin_regression;"
    private val invokeDescriptor = "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"
    private val reflectionInvoke = MethodDescriptor(VirtualType.of("Ljava/lang/reflect/Method;"), "invoke",
        "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
    private val purePolicy = ReflectionPolicy(
        safeClasses = setOf("Ljava/lang/Class;", "Ljava/lang/reflect/Method;", "Ljava/lang/String;", "Ljava/lang/Integer;", "Ljava/util/Comparator;")
            .map(VirtualType::of).toSet(),
        methodEffects = mapOf(reflectionInvoke to SideEffectLevel.NONE))

    private val invocationOwner = "Lreflection_invocation_regression;"
    private val stringDescriptor = "stringMethod(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;"
    private fun stringState(name: String, receiver: String?, first: Int = 4) = mapOf(
        first to Value.Known(name, VirtualType.of("Ljava/lang/String;")),
        first + 1 to Value.Known(receiver, VirtualType.of("Ljava/lang/String;")))

    private fun invokeFixture(descriptor: String, initial: Map<Int, Value> = emptyMap()) =
        OptimizerTester.optimize(invocationOwner, descriptor, UnreflectionStrategy(), initial, purePolicy)

    private fun optimize(name: String) = OptimizerTester.optimize(owner, name, UnreflectionStrategy())

    @Test fun provenSuccessfulReferenceInvocationUsesOriginalResultDestinationAndRoundTrips() {
        val optimized = invokeFixture(stringDescriptor, stringState("toString", "text"))
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimized.method, "Ljava/lang/String;", "toString"))
        assertEquals("text", (optimized.reexecute(stringState("toString", "text", optimized.method.firstParameterRegister))
            .trusted()!!.terminatingConsensus(3) as Value.Known).raw)
        assertEquals("text", (optimized.dexRoundTrip(stringState("toString", "text", optimized.method.firstParameterRegister))
            .trusted()!!.terminatingConsensus(3) as Value.Known).raw)
    }

    @Test fun primitiveResultIsBoxedAndScratchRegistersRemainValid() {
        val optimized = invokeFixture(stringDescriptor, stringState("length", "text"))
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimized.method, "Ljava/lang/Integer;", "valueOf"))
        assertTrue(hasOpcode(optimized.method, Opcode.MOVE_RESULT))
        assertEquals(CompletionStatus.Complete,
            optimized.dexRoundTrip(stringState("length", "text", optimized.method.firstParameterRegister)).completion())
    }

    @Test fun staticInterfaceMethodUsesStaticRatherThanInterfaceInvoke() {
        val optimized = invokeFixture("staticInterface()Ljava/lang/Object;")
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasOpcode(optimized.method, Opcode.INVOKE_STATIC))
        assertFalse(hasOpcode(optimized.method, Opcode.INVOKE_INTERFACE))
    }

    @Test fun defaultOpaqueReflectionAndUnknownMethodAreNotLowered() {
        val opaque = OptimizerTester.optimize(invocationOwner, stringDescriptor, UnreflectionStrategy(), stringState("length", "text"))
        assertTrue(hasMethod(opaque.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimize("call$invokeDescriptor").method, "Ljava/lang/reflect/Method;", "invoke"))
    }

    @Test fun throwingTargetRetainsInvocationTargetExceptionWrappingWithAndWithoutHandler() {
        for (name in listOf("throwingTarget", "caughtThrowingTarget")) {
            val optimized = invokeFixture("$name()Ljava/lang/Object;")
            assertTrue(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
            for (graph in listOf(optimized.graph, optimized.dexRoundTrip())) {
                assertEquals(CompletionStatus.Complete, graph.completion())
                if (name == "throwingTarget") {
                    assertEquals("Ljava/lang/reflect/InvocationTargetException;",
                        (graph.outcomes.single() as Outcome.Threw).exception.type?.descriptor)
                } else {
                    assertEquals("Ljava/lang/reflect/InvocationTargetException;",
                        graph.trusted()!!.terminatingConsensus(4).type?.descriptor)
                }
            }
        }
    }

    @Test fun nullReceiverAndWrongArgumentCountKeepReflectionValidation() {
        val nullReceiver = invokeFixture(stringDescriptor, stringState("length", null))
        val wrongCount = invokeFixture("wrongArgumentCount()Ljava/lang/Object;")
        for (optimized in listOf(nullReceiver, wrongCount)) {
            assertTrue(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
            assertTrue(optimized.graph.outcomes.single() is Outcome.Threw)
        }
        assertEquals("Ljava/lang/NullPointerException;",
            (nullReceiver.graph.outcomes.single() as Outcome.Threw).exception.type?.descriptor)
        assertEquals("Ljava/lang/IllegalArgumentException;",
            (wrongCount.graph.outcomes.single() as Outcome.Threw).exception.type?.descriptor)
    }

    @Test fun sameOwnerStaticFieldsLowerAndPreserveBoxingEvenWhenIgnored() {
        for ((descriptor, opcode) in listOf("readNumber()Ljava/lang/Object;" to Opcode.SGET,
                "readText()Ljava/lang/Object;" to Opcode.SGET_OBJECT, "readNumberIgnored()V" to Opcode.SGET)) {
            val optimized = optimize(descriptor)
            assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Field;", "get"))
            assertTrue(hasOpcode(optimized.method, opcode))
            if (opcode == Opcode.SGET) assertTrue(hasMethod(optimized.method, "Ljava/lang/Integer;", "valueOf"))
            assertEquals(CompletionStatus.Complete, optimized.dexRoundTrip().completion())
        }
    }

    @Test fun instanceAndOtherOwnerFieldsRemainReflective() {
        for (descriptor in listOf("readInstance()Ljava/lang/Object;", "readOtherOwner()Ljava/lang/Object;")) {
            assertTrue(hasMethod(optimize(descriptor).method, "Ljava/lang/reflect/Field;", "get"))
        }
    }

    @Test fun staticReadOpcodeSelectionCoversAllWidths() {
        assertEquals(listOf(Opcode.SGET, Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR,
            Opcode.SGET_OBJECT, Opcode.SGET_SHORT, Opcode.SGET_WIDE, Opcode.SGET_WIDE),
            listOf("I", "Z", "B", "C", "Ljava/lang/Object;", "S", "J", "D").map(UnreflectionStrategy::getStaticGetOpcode))
    }

    private fun hasOpcode(method: MethodView, opcode: Opcode) = method.instructionAddresses.any { method.opcodeAt(it) == opcode }
    private fun hasMethod(method: MethodView, owner: String, name: String) = method.instructionAddresses.any {
        val reference = (method.instructionAt(it) as? ReferenceInstruction)?.reference as? MethodReference
        reference?.definingClass == owner && reference.name == name
    }
}
