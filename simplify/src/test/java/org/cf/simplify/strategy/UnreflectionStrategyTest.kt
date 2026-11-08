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
    private val reflectionInvoke = MethodSignature(VirtualType.of("Ljava/lang/reflect/Method;"), "invoke",
        "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
    // A reflective call's effect is its reflected target's, so the pure targets are the ones declared pure.
    private val purePolicy = ReflectionPolicy(
        safeClasses = setOf("Ljava/lang/Class;", "Ljava/lang/reflect/Method;", "Ljava/lang/String;", "Ljava/lang/Integer;", "Ljava/util/Comparator;")
            .map(VirtualType::of).toSet(),
        methodEffects = listOf(
            MethodSignature(VirtualType.of("Ljava/lang/String;"), "toString", "()Ljava/lang/String;"),
            MethodSignature(VirtualType.of("Ljava/lang/String;"), "length", "()I"),
            MethodSignature(VirtualType.of("Ljava/util/Comparator;"), "naturalOrder", "()Ljava/util/Comparator;"),
        ).associateWith { SideEffectLevel.NONE },
    )

    private val invocationOwner = "Lreflection_invocation_regression;"

    private fun invokeFixture(descriptor: String) =
        OptimizerTester.optimize(invocationOwner, descriptor, UnreflectionStrategy(), purePolicy)

    private fun optimize(name: String) = OptimizerTester.optimize(owner, name, UnreflectionStrategy())

    /** Scratch registers renumber the method's own registers, so read the value the method returned. */
    private fun returned(graph: ExecutionGraph): Any? =
        ((graph.outcomes.single() as Outcome.Returned).value as Value.Known).raw

    @Test fun provenSuccessfulReferenceInvocationUsesOriginalResultDestinationAndRoundTrips() {
        val optimized = invokeFixture("textToString()Ljava/lang/Object;")
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimized.method, "Ljava/lang/String;", "toString"))
        assertEquals("text", returned(optimized.reexecute()))
        assertEquals("text", returned(optimized.dexRoundTrip()))
    }

    @Test fun primitiveResultIsBoxedAndScratchRegistersRemainValid() {
        val optimized = invokeFixture("textLength()I")
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimized.method, "Ljava/lang/Integer;", "valueOf"))
        // "text".length() boxed by Integer.valueOf, as Method.invoke boxes a primitive result.
        assertEquals(4, returned(optimized.reexecute()))
        assertEquals(4, returned(optimized.dexRoundTrip()))
    }

    @Test fun staticInterfaceMethodUsesStaticRatherThanInterfaceInvoke() {
        val optimized = invokeFixture("staticInterface()Ljava/lang/Object;")
        assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasOpcode(optimized.method, Opcode.INVOKE_STATIC))
        assertFalse(hasOpcode(optimized.method, Opcode.INVOKE_INTERFACE))
    }

    @Test fun defaultOpaqueReflectionAndUnknownMethodAreNotLowered() {
        val opaque = OptimizerTester.optimize(invocationOwner, "textLength()I", UnreflectionStrategy())
        assertTrue(hasMethod(opaque.method, "Ljava/lang/reflect/Method;", "invoke"))
        assertTrue(hasMethod(optimize("call$invokeDescriptor").method, "Ljava/lang/reflect/Method;", "invoke"))
    }

    @Test fun throwingTargetRetainsInvocationTargetExceptionWrappingWithAndWithoutHandler() {
        for (name in listOf("throwingTarget", "caughtThrowingTarget")) {
            val optimized = invokeFixture("$name()Ljava/lang/Object;")
            assertTrue(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
            for (graph in listOf(optimized.reexecute(), optimized.dexRoundTrip())) {
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
        val nullReceiver = invokeFixture("nullReceiverLength()Ljava/lang/Object;")
        val wrongCount = invokeFixture("wrongArgumentCount()Ljava/lang/Object;")
        for (optimized in listOf(nullReceiver, wrongCount)) {
            assertTrue(hasMethod(optimized.method, "Ljava/lang/reflect/Method;", "invoke"))
        }
        assertEquals("Ljava/lang/NullPointerException;",
            (nullReceiver.reexecute().outcomes.single() as Outcome.Threw).exception.type?.descriptor)
        assertEquals("Ljava/lang/IllegalArgumentException;",
            (wrongCount.reexecute().outcomes.single() as Outcome.Threw).exception.type?.descriptor)
    }

    @Test fun sameOwnerStaticFieldsLowerToTheReadOfTheirWidthAndPreserveBoxing() {
        for ((descriptor, opcode, box) in listOf(
            Triple("readNumber()Ljava/lang/Object;", Opcode.SGET, "Ljava/lang/Integer;"),
            Triple("readFlag()Ljava/lang/Object;", Opcode.SGET_BOOLEAN, "Ljava/lang/Boolean;"),
            Triple("readText()Ljava/lang/Object;", Opcode.SGET_OBJECT, null),
            Triple("readNumberIgnored()V", Opcode.SGET, "Ljava/lang/Integer;"),
        )) {
            val optimized = optimize(descriptor)
            assertFalse(hasMethod(optimized.method, "Ljava/lang/reflect/Field;", "get"), descriptor)
            assertTrue(hasOpcode(optimized.method, opcode), descriptor)
            if (box != null) assertTrue(hasMethod(optimized.method, box, "valueOf"), descriptor)
            assertEquals(CompletionStatus.Complete, optimized.dexRoundTrip().completion(), descriptor)
        }
    }

    @Test fun instanceAndOtherOwnerFieldsRemainReflective() {
        for (descriptor in listOf("readInstance()Ljava/lang/Object;", "readOtherOwner()Ljava/lang/Object;")) {
            assertTrue(hasMethod(optimize(descriptor).method, "Ljava/lang/reflect/Field;", "get"))
        }
    }

    private fun hasOpcode(method: MethodView, opcode: Opcode) = method.instructionAddresses.any { method.opcodeAt(it) == opcode }
    private fun hasMethod(method: MethodView, owner: String, name: String) = method.instructionAddresses.any {
        val reference = (method.instructionAt(it) as? ReferenceInstruction)?.reference as? MethodReference
        reference?.definingClass == owner && reference.name == name
    }
}
