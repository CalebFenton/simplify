package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** `Method.invoke` of an application method runs the target as a guest call with ART `InvokeMethod`'s rules. */
class ReflectiveCallTest {
    @Test
    fun staticTargetRunsAndItsPrimitiveResultIsBoxed() {
        val graph = CoreTester.execute(TARGET, "invokeStaticBoxed()Ljava/lang/Object;")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(42, INTEGER), returned(graph))
    }

    @Test
    fun callSiteTakesTheTargetsEffectAndContinuesNormally() {
        val pure = CoreTester.execute(TARGET, "invokeStaticBoxed()Ljava/lang/Object;").trusted()!!
        val writing = CoreTester.execute(TARGET, "invokeTouch()V").trusted()!!

        assertEquals(SideEffectLevel.NONE, pure.sideEffectLevel(invokeAddress(pure)))
        assertTrue(pure.didEveryExecutionContinueNormally(invokeAddress(pure)))
        assertEquals(SideEffectLevel.WEAK, writing.sideEffectLevel(invokeAddress(writing)))
    }

    @Test
    fun primitiveParametersUnboxWithWideningConversion() {
        assertEquals(
            Value.Known(42, INTEGER),
            returned(CoreTester.execute(TARGET, "invokeUnboxesWideningShortAndChar()Ljava/lang/Object;")),
        )
        assertEquals(
            Value.Known(5L, VirtualType.of("Ljava/lang/Long;")),
            returned(CoreTester.execute(TARGET, "invokeWidensIntegerToLong()Ljava/lang/Object;")),
        )
    }

    @Test
    fun primitiveParameterRejectsNullAndAWrapperThatDoesNotWiden() {
        CoreTester.testThrows(
            TARGET,
            "invokePrimitiveWithNull()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "method reflective_call.Target.addInts argument 1 has type int, got null",
        )
        CoreTester.testThrows(
            TARGET,
            "invokePrimitiveWithWrongWrapper()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "method reflective_call.Target.addInts argument 1 has type int, got java.lang.Long",
        )
    }

    @Test
    fun referenceParameterTakesNullAndRejectsANonInstance() {
        assertEquals(
            Value.Known(null, VirtualType.of("Ljava/lang/String;")),
            returned(CoreTester.execute(TARGET, "invokeReferenceWithNull()Ljava/lang/Object;")),
        )
        CoreTester.testThrows(
            TARGET,
            "invokeReferenceWithWrongType()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "method reflective_call.Target.echo argument 1 has type java.lang.String, got java.lang.Integer",
        )
    }

    @Test
    fun argumentCountMismatchThrowsIllegalArgument() {
        CoreTester.testThrows(
            TARGET,
            "invokeWithTooFewArguments()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "Wrong number of arguments; expected 2, got 1",
        )
        CoreTester.testThrows(
            TARGET,
            "invokeWithNullArgumentArray()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "Wrong number of arguments; expected 2, got 0",
        )
    }

    @Test
    fun methodInvokesOwnFailureRoutesThroughTheCallersHandlers() {
        CoreTester.testCaught(
            TARGET,
            "catchArgumentCountFailure()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "Wrong number of arguments; expected 2, got 0",
        )
    }

    @Test
    fun voidTargetReturnsNull() {
        assertEquals(Value.Known(null, VirtualType.OBJECT), returned(CoreTester.execute(TARGET, "invokeVoid()Ljava/lang/Object;")))
    }

    @Test
    fun targetThrowLeavesAsInvocationTargetExceptionWithTheThrowAsCause() {
        val escaping = CoreTester.execute(TARGET, "invokeFailing()Ljava/lang/Object;")
        val caught = CoreTester.execute(TARGET, "catchFailingCause()Ljava/lang/Object;")

        assertEquals(CompletionStatus.Complete, escaping.completion())
        assertEquals(listOf(INVOCATION_TARGET), CoreTester.escapingTypes(escaping))
        assertEquals(CompletionStatus.Complete, caught.completion())
        assertEquals(VirtualType.of("Ljava/lang/IllegalStateException;"), (returned(caught) as Value.Known).type)
    }

    @Test
    fun instanceTargetDispatchesOnTheReceiversRuntimeClass() {
        assertEquals(Value.Known(8, INTEGER), returned(CoreTester.execute(TARGET, "invokeVirtualOnChild()Ljava/lang/Object;")))
    }

    @Test
    fun instanceTargetRejectsANullOrUnrelatedReceiverWithoutWrapping() {
        CoreTester.testThrows(
            TARGET,
            "invokeInstanceOnNull()Ljava/lang/Object;",
            exceptionType = "Ljava/lang/NullPointerException;",
            message = "null receiver",
        )
        CoreTester.testThrows(
            TARGET,
            "invokeInstanceOnUnrelated()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ARGUMENT,
            message = "Expected receiver of type reflective_call.Target, but got java.lang.String",
        )
    }

    @Test
    fun staticTargetInitializesItsDeclaringClassFirst() {
        val graph = CoreTester.execute(
            "Lreflective_call/Initialized;",
            "invokeRead()Ljava/lang/Object;",
            entryClassState = EntryClassState.FRESH_PROCESS,
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(5, INTEGER), returned(graph))
        assertEquals(listOf("Lreflective_call/Initialized;"), CoreTester.initializersRun(graph))
    }

    @Test
    fun accessCheckAdmitsTheDeclaringClassPackageAndPublicMembers() {
        assertEquals(
            Value.Known(3, INTEGER),
            returned(CoreTester.execute("Lreflective_call/Neighbor;", "invokePackageMethod()Ljava/lang/Object;")),
        )
        assertEquals(
            Value.Known(7, INTEGER),
            returned(CoreTester.execute(OUTSIDER, "invokePublicMethod()Ljava/lang/Object;")),
        )
    }

    @Test
    fun accessCheckRejectsAnInaccessibleMember() {
        CoreTester.testThrows(
            "Lreflective_call/Neighbor;",
            "invokePrivateMethod()Ljava/lang/Object;",
            exceptionType = ILLEGAL_ACCESS,
            message = "Class java.lang.Class<reflective_call.Neighbor> cannot access private static  method " +
                "void reflective_call.Target.nothing() of class java.lang.Class<reflective_call.Target>",
        )
        CoreTester.testThrows(OUTSIDER, "invokePackageMethod()Ljava/lang/Object;", exceptionType = ILLEGAL_ACCESS)
    }

    @Test
    fun aMethodMadeAccessibleSkipsTheAccessCheck() {
        assertEquals(
            Value.Known(3, INTEGER),
            returned(CoreTester.execute(OUTSIDER, "invokeAfterSetAccessible()Ljava/lang/Object;")),
        )
    }

    @Test
    fun anUnknownArgumentArrayStaysOpaque() {
        val graph = CoreTester.execute(TARGET, "invokeWithArguments([Ljava/lang/Object;)Ljava/lang/Object;")
        val trusted = graph.trusted()!!

        assertEquals(Value.Unknown(VirtualType.OBJECT), returned(graph))
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(SideEffectLevel.STRONG, trusted.sideEffectLevel(invokeAddress(trusted)))
    }

    private fun returned(graph: ExecutionGraph): Value? = graph.outcomes.filterIsInstance<Outcome.Returned>().single().value

    /** The address of the method's one `Method.invoke`. */
    private fun invokeAddress(paths: ExploredPaths): Int = paths.methodView.instructionAddresses.single { address ->
        val instruction = paths.methodView.instructionAt(address) as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
        instruction?.reference?.toString() == METHOD_INVOKE
    }

    private companion object {
        const val TARGET = "Lreflective_call/Target;"
        const val OUTSIDER = "Lreflective_call/other/Outsider;"
        const val ILLEGAL_ARGUMENT = "Ljava/lang/IllegalArgumentException;"
        const val ILLEGAL_ACCESS = "Ljava/lang/IllegalAccessException;"
        const val INVOCATION_TARGET = "Ljava/lang/reflect/InvocationTargetException;"
        const val METHOD_INVOKE = "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"
        val INTEGER: VirtualType = VirtualType.of("Ljava/lang/Integer;")
    }
}
