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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import org.cf.smalivm.core.requireMethodView

class InvokeOpTest {
    /**
     * Each expected value is what the fixture callee computes from its arguments. The rows cover static, virtual,
     * and range calls, wide argument words, and an integral zero bound to a reference parameter (which becomes null).
     */
    @TestFactory
    fun aCallReturnsWhatItsCalleeComputes(): List<DynamicTest> = listOf(
        returns(CLASS_NAME, "staticReturn()I", mapOf(0 to known(7))),
        returns(CLASS_NAME, "wideRange()J", mapOf(0 to Value.Known(3L, type("J")))),
        returns(
            CLASS_NAME, "zeroBecomesNull()Ljava/lang/Object;",
            mapOf(0 to Value.Known(null, type("Ljava/lang/Object;"))),
        ),
        // Dispatch uses the receiver's tracked runtime type.
        returns(CLASS_NAME, "knownVirtual()I", mapOf(0 to known(2))),
        returns(STATIC, "invokeReturnInt()V", mapOf(0 to known(7))),
        returns(STATIC, "invokeReturnParameter()V", mapOf(0 to known(5)), initial = mapOf(0 to known(5))),
        returns(
            STATIC, "invokeReturnUninitializedField()V",
            mapOf(0 to Value.Known(null, type("Ljava/lang/String;"))),
        ),
        returns(VIRTUAL, "invokeReturnInt()V", mapOf(0 to known(7))),
        returns(VIRTUAL, "invokeReturnParameter()V", mapOf(1 to known(5), 2 to known(5)), initial = mapOf(1 to known(5))),
        returns(
            VIRTUAL, "invokeRangeManyParameters()V", mapOf(8 to known(3)),
            initial = mapOf(
                1 to Value.Known(0x100L, type("J")),
                3 to Value.Known(0x200L, type("J")),
                5 to Value.Known(0x300L, type("J")),
                7 to known(3),
            ),
        ),
    )

    /** A void call returns normally and assigns no result register. */
    @TestFactory
    fun aVoidCallAssignsNoRegister(): List<DynamicTest> = listOf(
        STATIC to 0,
        VIRTUAL to 5,
    ).map { (className, invokeAt) ->
        DynamicTest.dynamicTest("$className invokeReturnVoid()V") {
            val graph = CoreTester.execute(className, "invokeReturnVoid()V")
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertTrue(graph.outcomes.all { it == Outcome.Returned(null) })
            assertTrue(graph.registersAssigned(invokeAt).isEmpty(), "void call has no result register")
        }
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

    /**
     * Each failing call throws one fresh guest throwable carrying ART's message: `ThrowNullPointerExceptionForMethodAccess`
     * and `ThrowNoSuchMethodError` (`common_throws.cc`) and `DoResolveType`'s text, as the device recordings of
     * `Fx_InvokeMissingMethodNullReceiver` and `Fx_InvokeAbsentType` show. Resolution precedes the receiver null check.
     */
    @TestFactory
    fun aFailingCallThrowsOneGuestThrowableWithArtsMessage(): List<DynamicTest> = listOf(
        failingCall(
            "nullVirtual()I", NullPointerException::class.java,
            "Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference",
            moveResultAt = 4,
        ),
        failingCall(
            "missingBeforeNull()V", NoSuchMethodError::class.java,
            "No virtual method missing()V in class Linvoke_task20; or its super classes",
            moveResultAt = 4,
        ),
        failingCall(
            "absentClass()V", NoClassDefFoundError::class.java,
            "Failed resolution of: Linvoke_task20_absent;",
            moveResultAt = null,
        ),
    )

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

    /** `new-instance` then the matching `<init>` returning normally leaves an initialized tracked object. */
    @TestFactory
    fun aConstructorCallInitializesItsObject(): List<DynamicTest> = listOf(
        Triple("Lhash_code;", "createInstanceForInvokeTest()Lhash_code;", "Lhash_code;"),
        Triple("Lextends_enum;", "createForInvokeTest()Lextends_enum;", "Lextends_enum;"),
        Triple(DIRECT, "callInitOfClassWithDefaultConstructor()V", "Lclass_with_default_constructor;"),
    ).map { (className, method, descriptor) ->
        DynamicTest.dynamicTest("$className $method") {
            assertInitializedObject(CoreTester.execute(className, method), 0, descriptor)
        }
    }

    /** The constructor receives the caller's char array by reference and leaves its contents intact. */
    @TestFactory
    fun aConstructorWithAnArrayArgumentInitializesItsObjectAndKeepsTheArray(): List<DynamicTest> = listOf(
        "callInitOfClassWithoutDefaultConstructor()V" to "Lclass_without_default_constructor;",
        "callInitOfClassWithoutDefaultConstructorWhichChecksForNullArgument()V" to
            "Lclass_without_default_constructor_checks_null;",
    ).map { (method, descriptor) ->
        DynamicTest.dynamicTest(method) {
            val initial = CoreState().apply { setRegisters(1, charArrayOf('4', '2'), "[C") }
            val graph = CoreTester.execute(DIRECT, method, initial)
            assertInitializedObject(graph, 0, descriptor)
            assertExactCharArray(graph, 1, 2)
        }
    }

    /** `String(byte[])` with an unknown argument still initializes its receiver and leaves the argument unknown. */
    @Test
    fun aStringConstructorWithAnUnknownArgumentInitializesTheReceiver() {
        val initial = CoreState().apply { setRegister(1, Value.Unknown(type("[B"))) }
        val graph = CoreTester.execute("Linvoke_reflected_test;", "initStringWithByteArray()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(type("[B")), graph.trusted()!!.terminatingConsensus(1))
        assertInitializedObject(graph, 0, "Ljava/lang/String;")
    }

    @Test
    fun aWideArgumentSurvivesACallToLongValueOf() {
        val initial = CoreState().apply { setRegisters(0, 0x1000L, "J") }
        CoreTester.test(
            "Linvoke_reflected_test;",
            "invokeLongValueOf()V",
            initial,
            CoreState().apply { setRegisters(0, 0x1000L, "J") },
        )
    }

    /**
     * `System.arraycopy` declares `Object` parameters. An opaque call keeps each argument's more specific `[B` type
     * and its identity, and collapses the elements it may have written.
     */
    @Test
    fun anOpaqueCallKeepsAKnownArgumentsSpecificTypeAndIdentity() {
        val initial = CoreState().apply {
            setRegisters(0, byteArrayOf(1, 2), "[B", 1, 0, "I", 2, byteArrayOf(3, 4), "[B", 3, 0, "I", 4, 2, "I")
        }
        val graph = CoreTester.execute(STATIC, "invokeArrayCopy()V", initial)
        assertOpaqueByteArrays(graph)
    }

    @Test
    fun anOpaqueCallKeepsAnUnknownArgumentsSpecificType() {
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
    fun aCalleeInAnotherClassInitializesThatClassAndReadsTheCallersStatics() {
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
    fun aStaticArrayMutatedOnOnePathJoinsToUnknown() {
        val graph = CoreTester.execute(
            STATIC,
            "invokeMutateStaticClassFieldTrackedNonDeterministically(I)V",
        )
        val array = staticArray(graph)
        assertEquals(Value.Known(0, type("I")), array.element(0))
        assertEquals(Value.Unknown(type("I")), array.element(1))
        assertEquals(Value.Known(3, type("I")), array.element(2))
    }

    @Test
    fun aStaticArrayMutatedByTheCalleeIsSeenByTheCaller() {
        val array = staticArray(CoreTester.execute(STATIC, "invokeMutateStaticClassFieldTracked()V"))
        assertEquals(listOf(0, 3, 3), array.elements().map { (it as Value.Known).raw })
    }

    @Test
    fun aCalleeCannotMutateTheCallersString() {
        val initial = CoreState().apply { setRegisters(0, "not mutated", "Ljava/lang/String;") }
        CoreTester.test(
            STATIC,
            "invokeMutateString()V",
            initial,
            CoreState().apply { setRegisters(0, "not mutated", "Ljava/lang/String;") },
        )
    }

    @Test
    fun aKnownArrayArgumentIsMutatedWhenTheCalleesBranchIsDecided() {
        val initial = CoreState().apply { setRegisters(0, intArrayOf(5), "[I", 1, 0, "I") }
        val expected = CoreState().apply { setRegisters(0, intArrayOf(0), "[I", 1, 0, "I") }
        CoreTester.test(STATIC, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial, expected)
    }

    @Test
    fun aKnownArrayArgumentJoinsToUnknownWhenTheCalleesBranchIsUndecided() {
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
    fun aClassInitializedOnOnlyOnePathHasNoAgreedFieldValue() {
        val graph = CoreTester.execute(STATIC, "initializeClassIfArgumentIsZero(I)V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Top,
            graph.trusted()!!.terminatingStaticFieldConsensus(
                type("Lclass_with_static_init;"), "Lclass_with_static_init;->string:Ljava/lang/String;",
            ),
            "the path that skips initialization casts no vote for the field, so the paths do not agree",
        )
    }

    /**
     * A callee that always throws escapes its throwable; `<init>` is never inherited, so a constructor the class
     * does not declare is `NoSuchMethodError`; a callee in an absent class is `NoClassDefFoundError`.
     */
    @TestFactory
    fun aDefiniteThrowEscapesTheCaller(): List<DynamicTest> = listOf(
        Triple(STATIC, "invokeMethodWhichThrowsNullPointerException()V", "Ljava/lang/NullPointerException;"),
        Triple(CLASS_NAME, "inheritedConstructorIsMissing()V", "Ljava/lang/NoSuchMethodError;"),
        Triple(STATIC, "invokeNonExistentMethod()V", "Ljava/lang/NoClassDefFoundError;"),
    ).map { (className, method, thrown) ->
        DynamicTest.dynamicTest("$method -> $thrown") {
            val graph = CoreTester.execute(className, method)
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(listOf(thrown), CoreTester.escapingTypes(graph))
        }
    }

    @Test
    fun aCalleeThatMayThrowExploresBothTheThrowAndTheReturn() {
        val graph = CoreTester.execute(STATIC, "invokeMethodWhichMayThrowNullPointerException()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.outcomes.contains(Outcome.Returned(null)))
        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun toStringRendersTheInstruction() {
        val view = CoreTester.classIndex.requireMethodView(STATIC, "invokeReturnVoid()V")
        val op = OpBuilder.build(0, view, CoreTester.classIndex.currentResolver())
        assertEquals("invoke-static {}, Linvoke_static_test;->returnVoid()V", op.toString())
    }

    private fun returns(
        className: String,
        method: String,
        expected: Map<Int, Value>,
        initial: Map<Int, Value> = emptyMap(),
    ): DynamicTest = DynamicTest.dynamicTest("$className $method -> $expected") {
        val graph = CoreTester.execute(className, method, initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        for ((register, value) in expected) assertEquals(value, trusted.terminatingConsensus(register), "r$register")
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

    private fun failingCall(
        method: String,
        expectedClass: Class<out Throwable>,
        expectedMessage: String,
        moveResultAt: Int?,
    ): DynamicTest = DynamicTest.dynamicTest("$method -> ${expectedClass.simpleName}") {
        val graph = invokeRun(method)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFreshInvokeThrowable(
            graph,
            expectedType = "L" + expectedClass.name.replace('.', '/') + ";",
            expectedClass = expectedClass,
            expectedMessage = expectedMessage,
        )
        if (moveResultAt != null) assertFalse(graph.wasReached(moveResultAt), "a definite throw must not continue")
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
        val endingFrame = throwingNode.terminalFrame
        val host = endingFrame.heap.hostObjectAt(ref) as Throwable

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
