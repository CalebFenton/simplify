package org.cf.smalivm.core.op

import java.nio.file.Files
import java.nio.file.Path
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.OpBuilder
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.currentResolver
import org.cf.smalivm.core.heapRefsOf
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.VmServices
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InvokeOpTest {
    @Test
    fun staticInvokeReturnsThroughMoveResult() {
        CoreTester.assertReturns(CLASS_NAME, "staticReturn()I", known(7))
    }

    @Test
    fun rangeBindingAccountsForEveryWideWord() {
        CoreTester.assertReturns(CLASS_NAME, "wideRange()J", Value.Known(3L, type("J")))
    }

    @Test
    fun integralZeroPassedToAReferenceParameterBecomesNull() {
        CoreTester.assertReturns(
            CLASS_NAME,
            "zeroBecomesNull()Ljava/lang/Object;",
            Value.Known(null, type("Ljava/lang/Object;")),
        )
    }

    @Test
    fun knownReceiverDispatchUsesItsTrackedRuntimeType() {
        CoreTester.assertReturns(CLASS_NAME, "knownVirtual()I", known(2))
    }

    /**
     * One loaded implementation decides the call only when the program cannot load more classes. The shared
     * fixture tree calls `Class.forName`, so the same call there stays unknown.
     */
    @Test
    fun unknownReceiverWithOneLoadedImplementationRecoversItsConstant(@TempDir root: Path) {
        for (name in listOf("invoke_task20", "invoke_unique_base", "invoke_single_interface", "invoke_single_impl")) {
            Files.copy(Path.of(CoreTester.TEST_CLASS_PATH, "ops", "$name.smali"), root.resolve("$name.smali"))
        }
        val closed = VmServices(ClassIndex.open(root.toFile()))
        fun closedReturn(descriptor: String): Value {
            val graph = closed.newSession(closed.classIndex.requireMethodView(CLASS_NAME, descriptor)).run()
            return graph.trusted()!!.terminatingConsensus(0)
        }

        assertEquals(known(7), closedReturn("uniqueUnknown(Linvoke_unique_base;)I"))
        assertEquals(known(9), closedReturn("interfaceUnknown(Linvoke_single_interface;)I"))
        CoreTester.assertReturns(CLASS_NAME, "uniqueUnknown(Linvoke_unique_base;)I", Value.Unknown(type("I")))
    }

    @Test
    fun unknownReceiverWithSeveralImplementationsCollapsesWithoutGuessing() {
        val graph = CoreTester.execute(CLASS_NAME, "ambiguousUnknown(Linvoke_dispatch_base;)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(type("I")), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        val invoke = graph.addresses().flatMap { graph.nodePile(it) }
            .single { it.dispatchCandidates.isNotEmpty() }
        assertEquals(
            setOf(
                "Linvoke_dispatch_base;->value()I",
                "Linvoke_dispatch_child;->value()I",
            ),
            invoke.dispatchCandidates,
        )
    }

    @Test
    fun definiteNullReceiverHasOnlyTheGuestNpeOutcome() {
        val graph = invokeRun("nullVirtual()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFreshInvokeThrowable(
            graph,
            expectedType = "Ljava/lang/NullPointerException;",
            expectedClass = NullPointerException::class.java,
            expectedMessage = "invoke receiver is null",
        )
        assertFalse(graph.wasReached(4), "a definite NPE must not also reach move-result")
    }

    @Test
    fun resolutionFailurePrecedesReceiverNull() {
        val graph = invokeRun("missingBeforeNull()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFreshInvokeThrowable(
            graph,
            expectedType = "Ljava/lang/NoSuchMethodError;",
            expectedClass = NoSuchMethodError::class.java,
            expectedMessage = "missing()V",
        )
        assertTrue("Ljava/lang/NullPointerException;" !in CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(4))
    }

    @Test
    fun absentDefiningClassThrowsGuestNoClassDefFoundError() {
        // ART raises one message for an absent class whichever opcode resolves it (`ClassLinker::DoResolveType`), so
        // an invoke names it as `new-instance` and the other resolving ops do.
        val graph = invokeRun("absentClass()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFreshInvokeThrowable(
            graph,
            expectedType = "Ljava/lang/NoClassDefFoundError;",
            expectedClass = NoClassDefFoundError::class.java,
            expectedMessage = "invoke_task20_absent",
        )
    }

    @Test
    fun opaqueCallPreservesArrayIdentityAndCollapsesItsElements() {
        val graph = CoreTester.execute(CLASS_NAME, "opaqueArray()[I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val value = graph.trusted()!!.terminatingConsensus(1)
        assertTrue(value is Value.Known && value.raw is HeapRef, "the caller keeps its HeapRef: $value")
        val array = graph.trusted()!!.terminatingHeapObject((value as Value.Known).raw as HeapRef).agreed()
        assertTrue(array is HeapObject.TrackedArray)
        assertEquals(1, (array as HeapObject.TrackedArray).length)
        assertEquals(Value.Unknown(type("I")), array.element(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun opaqueCollapseReachesFieldsNestedAliasesAndCyclesWithoutReplacingRefs() {
        val graph = CoreTester.execute(CLASS_NAME, "opaqueNested()Linvoke_nested_object;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        val root = trusted.terminatingConsensus(0) as Value.Known
        val child = trusted.terminatingConsensus(1) as Value.Known
        assertEquals(root, trusted.terminatingConsensus(2), "the root alias keeps the same HeapRef")
        val rootObject = trusted.terminatingHeapObject(root.raw as HeapRef).agreed() as HeapObject.TrackedObject
        val childObject = trusted.terminatingHeapObject(child.raw as HeapRef).agreed() as HeapObject.TrackedObject
        assertEquals(Value.Unknown(type("Linvoke_nested_object;")), rootObject.field("Linvoke_nested_object;->child:Linvoke_nested_object;"))
        assertEquals(Value.Unknown(type("I")), childObject.field("Linvoke_nested_object;->value:I"))

        val cycle = CoreTester.execute(CLASS_NAME, "opaqueCycle()Linvoke_nested_object;")
        assertEquals(CompletionStatus.Complete, cycle.completion(), "the reachable cycle guard must terminate")
        val cycleRef = (cycle.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val cycleObject = cycle.trusted()!!.terminatingHeapObject(cycleRef).agreed() as HeapObject.TrackedObject
        assertEquals(
            Value.Unknown(type("Linvoke_nested_object;")),
            cycleObject.field("Linvoke_nested_object;->child:Linvoke_nested_object;"),
        )
    }

    @Test
    fun constructorClearsOnlyTheInitializedObjectAndAliasesObserveIt() {
        val other = CoreTester.execute(CLASS_NAME, "initOneOnly()Linvoke_ctor_target;")
        assertEquals(CompletionStatus.Complete, other.completion())
        assertTrackedObject(other.trusted()!!.terminatingConsensus(1), other, uninitialized = true)

        val alias = CoreTester.execute(CLASS_NAME, "initAlias()Linvoke_ctor_target;")
        assertEquals(CompletionStatus.Complete, alias.completion())
        assertTrackedObject(alias.trusted()!!.terminatingConsensus(1), alias, uninitialized = false)
    }

    @Test
    fun constructorsAreNeverInherited() {
        val graph = CoreTester.execute(CLASS_NAME, "inheritedConstructorIsMissing()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun returnAndThrowStatesRouteToSeparateCallerPartitions() {
        val graph = CoreTester.execute(CLASS_NAME, "mixedReturnAndThrow(I)I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        assertEquals(
            mapOf(known(1) to 1, known(2) to 1),
            returned.groupingBy { it }.eachCount(),
            "normal continuation sees only return mutation; handler sees only pre-throw mutation",
        )
    }

    @Test
    fun aThrowingConstructorDoesNotClearTheReceiver() {
        val graph = CoreTester.execute(CLASS_NAME, "throwingConstructorStaysUninitialized()Linvoke_throw_ctor;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrackedObject(graph.trusted()!!.terminatingConsensus(0), graph, uninitialized = true)
    }

    // Host-object and array-clone invocation cases live in BackendCorpusMigrationTest.

    @Test
    fun objectClassInitializationCreatesCorrectClass() {
        val graph = CoreTester.execute("Lhash_code;", "createInstanceForInvokeTest()Lhash_code;")
        assertInitializedObject(graph, 0, "Lhash_code;")
    }

    @Test
    fun enumClassInitializationCreatesCorrectClass() {
        val graph = CoreTester.execute("Lextends_enum;", "createForInvokeTest()Lextends_enum;")
        assertInitializedObject(graph, 0, "Lextends_enum;")
    }

    @Test
    fun initStringWithByteArrayWithUnknownParameter() {
        val initial = CoreState().apply { setRegister(1, Value.Unknown(type("[B"))) }
        val graph = CoreTester.execute("Linvoke_reflected_test;", "initStringWithByteArray()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(type("[B")), graph.trusted()!!.terminatingConsensus(1))
        assertInitializedObject(graph, 0, "Ljava/lang/String;") // adjudication 9: preserve the ref
    }

    @Test
    fun invokeLongValueOfWithLongGetsCorrectParameters() {
        val initial = CoreState().apply { setRegisters(0, 0x1000L, "J") }
        CoreTester.test(
            "Linvoke_reflected_test;",
            "invokeLongValueOf()V",
            initial,
            CoreState().apply { setRegisters(0, 0x1000L, "J") },
        )
    }

    @Test
    fun invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanKnownArgumentsTypeRetainsSpecificType() {
        val initial = CoreState().apply {
            setRegisters(0, byteArrayOf(1, 2), "[B", 1, 0, "I", 2, byteArrayOf(3, 4), "[B", 3, 0, "I", 4, 2, "I")
        }
        val graph = CoreTester.execute(STATIC, "invokeArrayCopy()V", initial)
        assertOpaqueByteArrays(graph)
    }

    @Test
    fun invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanUnknownArgumentsTypeRetainsSpecificType() {
        val initial = CoreState().apply {
            setRegister(0, Value.Unknown(type("[B")))
            setRegisters(1, 0, "I")
            setRegister(2, Value.Unknown(type("[B")))
            setRegisters(3, 0, "I")
        }
        val graph = CoreTester.execute(STATIC, "invokeArrayCopy()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(type("[B")), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(Value.Unknown(type("[B")), graph.trusted()!!.terminatingConsensus(2))
    }

    @Test
    fun invokeMethodOutsideClassThatAccessesThisClassReturnsExpectedValue() {
        val initial = CoreState().apply {
            setFields(STATIC, "sometimes_initialized:Ljava/lang/String;", "i have been initialized")
        }
        val graph = CoreTester.execute(STATIC, "invokeMethodOutsideClassThatAccessesThisClass()V", initial)
        assertEquals(Value.Known("i have been initialized", type("Ljava/lang/String;")), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(
            Value.Known("Uhhh, about 11, sir.", type("Ljava/lang/String;")),
            graph.trusted()!!.terminatingStaticFieldConsensus(
                type("Lclass_with_static_init;"), "Lclass_with_static_init;->string:Ljava/lang/String;",
            ),
        )
    }

    @Test
    fun invokeMutateStaticClassFieldNonDeterministicallyPropagatesUnknown() {
        val graph = CoreTester.execute(
            STATIC,
            "invokeMutateStaticClassFieldTrackedNonDeterministically(I)V",
        )
        val array = staticArray(graph)
        assertEquals(Value.Known(0, type("I")), array.element(0))
        assertEquals(Value.Unknown(type("I")), array.element(1)) // adjudication 8 + heap identity
        assertEquals(Value.Known(3, type("I")), array.element(2))
    }

    @Test
    fun invokeMutateStaticClassFieldPropagatesChanges() {
        val array = staticArray(CoreTester.execute(STATIC, "invokeMutateStaticClassFieldTracked()V"))
        assertEquals(listOf(0, 3, 3), array.elements().map { (it as Value.Known).raw })
    }

    @Test
    fun invokeMutateStringDoesNotMutateParameter() {
        val initial = CoreState().apply { setRegisters(0, "not mutated", "Ljava/lang/String;") }
        CoreTester.test(
            STATIC,
            "invokeMutateString()V",
            initial,
            CoreState().apply { setRegisters(0, "not mutated", "Ljava/lang/String;") },
        )
    }

    @Test
    fun invokeReturnIntReturnsInt() {
        CoreTester.assertReturns(STATIC, "invokeReturnInt()V", known(7), register = 0)
    }

    @Test
    fun invokeReturnParameterReturnsParameter() {
        CoreTester.assertReturns(
            STATIC, "invokeReturnParameter()V", known(5), register = 0,
            initialRegisters = mapOf(0 to known(5)),
        )
    }

    @Test
    fun invokeReturnUninitializedFieldReturnsNull() {
        CoreTester.assertReturns(
            STATIC,
            "invokeReturnUninitializedField()V",
            Value.Known(null, type("Ljava/lang/String;")),
        )
    }

    @Test
    fun invokeReturnVoidReturnsVoid() {
        val graph = CoreTester.execute(STATIC, "invokeReturnVoid()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.outcomes.all { it == Outcome.Returned(null) })
        assertTrue(graph.registersAssigned(0).isEmpty(), "void call has no result register")
    }

    @Test
    fun knownMutableParametersAreMutatedWithDeterministicExecution() {
        val initial = CoreState().apply { setRegisters(0, intArrayOf(5), "[I", 1, 0, "I") }
        val expected = CoreState().apply { setRegisters(0, intArrayOf(0), "[I", 1, 0, "I") }
        CoreTester.test(STATIC, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial, expected)
    }

    @Test
    fun knownMutableParametersAreMutatedWithNonDeterministicExecution() {
        val initial = CoreState().apply {
            setRegisters(0, intArrayOf(5), "[I")
            setRegister(1, Value.Unknown(type("I")))
        }
        val graph = CoreTester.execute(STATIC, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial)
        val ref = (graph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(Value.Unknown(type("I")), array.element(0))
    }

    @Test
    fun possiblyUninitializedClassHasUnknownFieldValues() {
        val graph = CoreTester.execute(STATIC, "initializeClassIfArgumentIsZero(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Top,
            graph.trusted()!!.terminatingStaticFieldConsensus(
                type("Lclass_with_static_init;"), "Lclass_with_static_init;->string:Ljava/lang/String;",
            ),
            "resolved decision 9 preserves the absent class-state vote instead of existentially initializing",
        )
    }

    @Test
    fun alwaysThrownExceptionIsBubbledUp() {
        val graph = CoreTester.execute(STATIC, "invokeMethodWhichThrowsNullPointerException()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun sometimesThrownExceptionExecutesExceptionalAndNormalExecutionPaths() {
        val graph = CoreTester.execute(STATIC, "invokeMethodWhichMayThrowNullPointerException()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.outcomes.contains(Outcome.Returned(null)))
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun canCallInitOfClassWithDefaultConstructorAndGetNewInstance() {
        assertInitializedObject(CoreTester.execute(DIRECT, "callInitOfClassWithDefaultConstructor()V"), 0, "Lclass_with_default_constructor;")
    }

    @Test
    fun canCallInitOfClassWithoutDefaultConstructorAndGetNewInstance() {
        val initial = CoreState().apply { setRegisters(1, charArrayOf('4', '2'), "[C") }
        val graph = CoreTester.execute(DIRECT, "callInitOfClassWithoutDefaultConstructor()V", initial)
        assertInitializedObject(graph, 0, "Lclass_without_default_constructor;")
        assertExactCharArray(graph, 1, 2)
    }

    @Test
    fun canCallInitOfClassWithoutDefaultConstructorWhichChecksForNullArgumentAndGetNewInstance() {
        val initial = CoreState().apply { setRegisters(1, charArrayOf('4', '2'), "[C") }
        val graph = CoreTester.execute(DIRECT, "callInitOfClassWithoutDefaultConstructorWhichChecksForNullArgument()V", initial)
        assertInitializedObject(graph, 0, "Lclass_without_default_constructor_checks_null;")
        assertExactCharArray(graph, 1, 2)
    }

    @Test
    fun instanceInitializerWorksAsExpected() {
        assertInitializedObject(CoreTester.execute("Lhash_code;", "createInstanceForInvokeTest()Lhash_code;"), 0, "Lhash_code;")
    }

    @Test
    fun virtualInvokeReturnIntReturnsInt() {
        CoreTester.assertReturns(VIRTUAL, "invokeReturnInt()V", known(7), register = 0)
    }

    @Test
    fun virtualInvokeReturnParameterReturnsParameter() {
        val graph = CoreTester.execute(VIRTUAL, "invokeReturnParameter()V", mapOf(1 to known(5)))
        assertEquals(known(5), graph.trusted()!!.terminatingConsensus(1))
        assertEquals(known(5), graph.trusted()!!.terminatingConsensus(2))
    }

    @Test
    fun virtualInvokeReturnVoidReturnsVoid() {
        val graph = CoreTester.execute(VIRTUAL, "invokeReturnVoid()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.outcomes.all { it == Outcome.Returned(null) })
    }

    @Test
    fun invokeVirtualManyParametersThrowsNoExceptions() {
        val initial = mapOf(
            1 to Value.Known(0x100L, type("J")),
            3 to Value.Known(0x200L, type("J")),
            5 to Value.Known(0x300L, type("J")),
            7 to known(3),
        )
        CoreTester.assertReturns(VIRTUAL, "invokeRangeManyParameters()V", known(3), register = 8, initialRegisters = initial)
    }

    @Test
    fun invokeNonExistentMethodThrowsException() {
        val graph = CoreTester.execute(STATIC, "invokeNonExistentMethod()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun hasExpectedToString() {
        val view = CoreTester.classIndex.requireMethodView(STATIC, "invokeReturnVoid()V")
        val op = OpBuilder.build(0, view, CoreTester.classIndex.currentResolver())
        assertEquals("invoke-static {}, Linvoke_static_test;->returnVoid()V", op.toString())
    }

    private fun assertTrackedObject(value: Value, graph: org.cf.smalivm.core.ExecutionGraph, uninitialized: Boolean) {
        assertTrue(value is Value.Known && value.raw is HeapRef, "expected tracked object, got $value")
        val obj = graph.trusted()!!.terminatingHeapObject((value as Value.Known).raw as HeapRef).agreed()
        assertTrue(obj is HeapObject.TrackedObject)
        assertEquals(uninitialized, (obj as HeapObject.TrackedObject).uninitialized)
    }

    private fun invokeRun(descriptor: String): ExecutionGraph {
        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, descriptor)
        return CoreTester.vmServices.newSession(view).run()
    }

    private fun assertFreshInvokeThrowable(
        graph: ExecutionGraph,
        expectedType: String,
        expectedClass: Class<out Throwable>,
        expectedMessage: String?,
    ) {
        val outcome = graph.outcomes.single() as Outcome.Threw
        val thrown = outcome.exception as Value.Known
        val ref = thrown.raw as HeapRef
        val throwingNode = graph.addresses().flatMap(graph::nodePile).single { it.outcome is Outcome.Threw }
        val endingFrame = requireNotNull(throwingNode.endingFrame)
        val host = endingFrame.hostObjectAt(ref) as Throwable

        assertEquals(VirtualType.of(expectedType), thrown.type)
        assertEquals(thrown, endingFrame.thrownOrNull(), "the outcome and terminal frame must retain one guest identity")
        assertEquals(expectedClass, host.javaClass)
        assertEquals(expectedMessage, host.message)
        // Exactly one guest throwable exists on the throwing path: none was allocated before an arm committed
        // and the committed event materialized it once.
        val throwables = heapRefsOf(endingFrame).filter { endingFrame.heapObject(it) is HeapObject.GuestThrowable }
        assertEquals(listOf(ref), throwables)
    }

    private fun assertInitializedObject(
        graph: org.cf.smalivm.core.ExecutionGraph,
        register: Int,
        descriptor: String,
    ) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        val value = graph.trusted()!!.terminatingConsensus(register)
        assertTrue(value is Value.Known && value.raw is HeapRef && value.type == type(descriptor), "tracked $descriptor: $value")
        val obj = graph.trusted()!!.terminatingHeapObject((value as Value.Known).raw as HeapRef).agreed()
        assertTrue(obj is HeapObject.TrackedObject)
        assertFalse((obj as HeapObject.TrackedObject).uninitialized, "the matching constructor returned normally")
    }

    private fun assertOpaqueByteArrays(graph: org.cf.smalivm.core.ExecutionGraph) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        for (register in listOf(0, 2)) {
            val value = graph.trusted()!!.terminatingConsensus(register)
            assertTrue(value is Value.Known && value.raw is HeapRef && value.type == type("[B"), "register $register: $value")
            val alias = graph.trusted()!!.terminatingConsensus(if (register == 0) 5 else 6)
            assertEquals(value, alias, "opaque collapse preserves root identity and aliases")
            val array = graph.trusted()!!.terminatingHeapObject((value as Value.Known).raw as HeapRef).agreed() as HeapObject.TrackedArray
            assertEquals(listOf(Value.Unknown(type("B")), Value.Unknown(type("B"))), array.elements())
        }
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    private fun assertExactCharArray(graph: org.cf.smalivm.core.ExecutionGraph, register: Int, aliasRegister: Int) {
        val value = graph.trusted()!!.terminatingConsensus(register)
        assertEquals(value, graph.trusted()!!.terminatingConsensus(aliasRegister), "constructor keeps exact array ref")
        val ref = (value as Value.Known).raw as HeapRef
        val array = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(
            listOf(Value.Known('4', type("C")), Value.Known('2', type("C"))),
            array.elements(),
        )
    }

    private fun staticArray(graph: org.cf.smalivm.core.ExecutionGraph): HeapObject.TrackedArray {
        assertEquals(CompletionStatus.Complete, graph.completion())
        val value = graph.trusted()!!.terminatingStaticFieldConsensus(
            type(STATIC), "$STATIC->mutable:[I",
        ) as Value.Known
        return graph.trusted()!!.terminatingHeapObject(value.raw as HeapRef).agreed() as HeapObject.TrackedArray
    }

    private fun known(value: Int) = Value.Known(value, type("I"))
    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        const val CLASS_NAME = "Linvoke_task20;"
        const val STATIC = "Linvoke_static_test;"
        const val DIRECT = "Linvoke_direct_test;"
        const val VIRTUAL = "Linvoke_virtual_test;"
    }
}
