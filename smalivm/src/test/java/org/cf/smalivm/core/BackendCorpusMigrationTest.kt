package org.cf.smalivm.core

import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BackendCorpusMigrationTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun reflectedListMutationPreservesAliasAndReturn() {
        // Bug class: host return import allocates a second ref for an argument already reachable through a list.
        // Oracle: List.add/get and Plan 11 Task 35 require one scheduler HeapRef for one VM-owned graph node.
        val list = LinkedList<Any>()
        val inserted = StringBuilder("inserted")
        val run = execute(
            INVOKE_FIXTURE,
            "reflectedListMutation(Ljava/util/List;Ljava/lang/Object;)Ljava/lang/Object;",
            initial = linkedMapOf(
                1 to Value.Known(list, LIST),
                2 to Value.Known(inserted, OBJECT),
            ),
        )
        val listRef = (run.seeded.getValue(1) as Value.Known).raw as HeapRef
        val insertedRef = (run.seeded.getValue(2) as Value.Known).raw as HeapRef
        val returned = run.graph.returnedValue() as Value.Known
        val terminal = run.session.currentNode!!.frame
        val nested = (terminal.heap.hostObjectAt(listRef) as List<*>).single()!!
        val nestedRef = terminal.heap.refForNode(requireNotNull(terminal.heap.hostNodeId(nested)))

        assertEquals(insertedRef, returned.raw)
        assertEquals(insertedRef, (run.graph.trusted()!!.terminatingConsensus(2) as Value.Known).raw)
        assertEquals(insertedRef, nestedRef)
        assertTrue(list.isEmpty(), "selected reflection must mutate the detached VM graph, not the seed")
    }

    @Test
    fun zeroPrimitiveBindsAsNullForReferenceParameter() {
        // Bug class: integer zero is passed as boxed data when the exact parameter requires a reference.
        // Oracle: Plan 11 binding rule converts primitive I zero to Dalvik null only at a reference parameter.
        val run = execute(
            INVOKE_FIXTURE,
            "reflectedListMutation(Ljava/util/List;Ljava/lang/Object;)Ljava/lang/Object;",
            initial = linkedMapOf(
                1 to Value.Known(LinkedList<Any?>(), LIST),
                2 to Value.Known(0, INT),
            ),
        )

        assertEquals(Value.Known(null, OBJECT), run.graph.returnedValue())
    }

    @Test
    fun boxedIntegerReferenceIsNotNull() {
        // Bug class: a reference-tagged boxed Integer(0) is mistaken for primitive zero and converted to null.
        // Oracle: Plan 11 Task 35 says a reference-tagged Number is a live reference regardless of numeric value.
        for (value in listOf(5, 0)) {
            val run = execute(
                INVOKE_FIXTURE,
                "reflectedListMutation(Ljava/util/List;Ljava/lang/Object;)Ljava/lang/Object;",
                initial = linkedMapOf(
                    1 to Value.Known(LinkedList<Any?>(), LIST),
                    2 to Value.Known(value, INTEGER),
                ),
            )

            assertEquals(Value.Known(value, INTEGER), run.graph.returnedValue())
        }
    }

    @Test
    fun primitiveZeroStaysPrimitiveDuringHostCall() {
        // Bug class: the zero-to-null reference rule leaks into an exact primitive Boolean parameter.
        // Oracle: Boolean.valueOf(false) returns Boolean.FALSE and its Z parameter consumes primitive zero.
        val target = method(HOST_FIXTURE, "boxBoolean", "(Z)Ljava/lang/Object;")
        val run = execute(
            INVOKE_FIXTURE,
            "primitiveZero(I)Ljava/lang/Object;",
            vm(setOf(target)),
            initial = mapOf(1 to Value.Known(0, INT)),
        )

        assertEquals(Value.Known(false, BOOLEAN), run.graph.returnedValue())
    }

    @Test
    fun reflectedStringBuilderAppendImportsMutation() {
        // Bug class: a wide J is split into two logical arguments or the returned receiver gets a new ref.
        // Oracle: StringBuilder.append(long) takes one J value, mutates its receiver, and returns that receiver.
        for ((value, expected) in listOf(0x1234L to "value=4660", 0x1_0000_0002L to "value=4294967298")) {
            val original = StringBuilder("value=")
            val run = execute(
                INVOKE_FIXTURE,
                "appendLong(Ljava/lang/StringBuilder;J)Ljava/lang/StringBuilder;",
                initial = linkedMapOf(
                    0 to Value.Known(original, BUILDER),
                    1 to Value.Known(value, LONG),
                ),
            )
            val receiverRef = (run.seeded.getValue(0) as Value.Known).raw as HeapRef
            val returned = run.graph.returnedValue() as Value.Known

            assertEquals(receiverRef, returned.raw)
            assertEquals(expected, run.session.currentNode!!.frame.heap.hostObjectAt(receiverRef).toString())
            assertEquals("value=", original.toString())
        }
    }

    @Test
    fun aliasMutationUpdatesAllRegisters() {
        // Bug class: guest-call reconciliation updates the parameter register but loses a caller alias.
        // Oracle: Plan 11 heap identity makes both caller registers observe one mutated StringBuilder ref.
        val run = execute(
            INVOKE_FIXTURE,
            "mutateThroughAlias(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            initial = mapOf(1 to Value.Known(StringBuilder(PREFIX), BUILDER)),
        )
        val expectedRef = (run.seeded.getValue(1) as Value.Known).raw
        val trusted = run.graph.trusted()!!

        assertEquals(expectedRef, (trusted.terminatingConsensus(0) as Value.Known).raw)
        assertEquals(expectedRef, (trusted.terminatingConsensus(1) as Value.Known).raw)
        assertEquals(expectedRef, (run.graph.returnedValue() as Value.Known).raw)
        assertEquals(FULL_TEXT, run.session.currentNode!!.frame.heap.hostObjectAt(expectedRef as HeapRef).toString())
    }

    @Test
    fun hostMutationUpdatesTrackedArgument() {
        // Bug class: a reflected static mutator changes only its detached Java clone and drops the imported graph.
        // Oracle: selected host-call finalization retains a changed VM-owned graph at the argument's existing ref.
        val run = execute(
            INVOKE_FIXTURE,
            "mutateTrackedArgument(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            initial = mapOf(0 to Value.Known(StringBuilder(PREFIX), BUILDER)),
        )
        val ref = (run.seeded.getValue(0) as Value.Known).raw as HeapRef

        assertEquals(ref, (run.graph.returnedValue() as Value.Known).raw)
        assertEquals(FULL_TEXT, run.session.currentNode!!.frame.heap.hostObjectAt(ref).toString())
    }

    @Test
    fun hostMutationUsesLogicalParameterPosition() {
        // Bug class: mutation import uses register width or first-mutable position instead of logical parameter three.
        // Oracle: descriptor (JILjava/lang/StringBuilder;)V binds J once and the builder as logical argument three.
        val target = method(HOST_FIXTURE, "mutateThird", "(JILjava/lang/StringBuilder;)V")
        val run = execute(
            INVOKE_FIXTURE,
            "mutateThirdLogicalParameter(JILjava/lang/StringBuilder;)Ljava/lang/StringBuilder;",
            vm(setOf(target)),
            linkedMapOf(
                1 to Value.Known(0x1_0000_0002L, LONG),
                3 to Value.Known(7, INT),
                4 to Value.Known(StringBuilder(PREFIX), BUILDER),
            ),
        )
        val ref = (run.seeded.getValue(4) as Value.Known).raw as HeapRef
        val trusted = run.graph.trusted()!!

        assertEquals(ref, (run.graph.returnedValue() as Value.Known).raw)
        assertEquals(ref, (trusted.terminatingConsensus(0) as Value.Known).raw)
        assertEquals(ref, (trusted.terminatingConsensus(4) as Value.Known).raw)
        assertEquals(FULL_TEXT, run.session.currentNode!!.frame.heap.hostObjectAt(ref).toString())
    }

    @Test
    fun unknownArrayCloneReturnsTypedUnknown() {
        // Bug class: an unknown [I clone is typed as Object or allocated as a falsely known array.
        // Oracle: Plan 11 array-clone intrinsic returns Unknown([I) without inventing identity or elements.
        // Divergence: the original method name says known clone, but its exact body seeds UnknownValue.
        val run = execute(
            INVOKE_FIXTURE,
            "cloneArray([I)[I",
            initial = mapOf(1 to Value.Unknown(INT_ARRAY)),
        )

        assertEquals(Value.Unknown(INT_ARRAY), run.graph.returnedValue())
    }

    @Test
    fun knownArrayCloneReturnsFreshCopy() {
        // Bug class: array clone returns the source ref or fails to copy one tracked element.
        // Oracle: JLS array clone semantics require a fresh array with equal shallow element values.
        // Divergence: the original method name says unknown, but its exact body seeds intArrayOf(1, 2, 3).
        val run = execute(
            INVOKE_FIXTURE,
            "cloneArray([I)[I",
            initial = mapOf(1 to Value.Known(intArrayOf(1, 2, 3), INT_ARRAY)),
        )

        assertFreshIntArrayClone(run)
    }

    @Test
    fun objectCloneOnArrayReturnsFreshCopy() {
        // Bug class: Object.clone on an array bypasses the tracked-array intrinsic and returns an opaque Object.
        // Oracle: JVM array clone semantics depend on receiver runtime type, not the Object method owner.
        val run = execute(
            INVOKE_FIXTURE,
            "cloneArrayAsObject([I)[I",
            initial = mapOf(1 to Value.Known(intArrayOf(1, 2, 3), INT_ARRAY)),
        )

        assertFreshIntArrayClone(run)
    }

    @Test
    fun nullArrayCloneRoutesGuestNpe() {
        // Bug class: null array clone reaches a backend or becomes an infrastructure failure.
        // Oracle: Dalvik invoke null-receiver handling routes guest NullPointerException before backend execution.
        val run = execute(
            INVOKE_FIXTURE,
            "cloneArray([I)[I",
            initial = mapOf(1 to Value.Known(null, INT_ARRAY)),
            expectedCompletion = CompletionStatus.Complete,
        )
        val thrown = (run.graph.outcomes.single() as Outcome.Threw).exception as Value.Known

        assertEquals(NULL_POINTER, thrown.type)
        assertTrue(run.graph.addresses().flatMap(run.graph::nodePile).all { it.hostFailure == null })
    }

    @Test
    fun primitiveArrayComponentTypeIsInt() {
        // Bug class: component lookup uses a loose Object tag and returns Object or null for a tracked int array.
        // Oracle: Class.getComponentType on int[].class is exactly int.class.
        val view = view(INVOKE_FIXTURE, "primitiveArrayComponent(Ljava/lang/Object;)Ljava/lang/Class;")
        val vm = VmServices(classIndex)
        val frame = Frame.entryFrame(view)
        val arrayRef = frame.allocateArray(INT_ARRAY, arrayOf(Value.Known(3, INT))).raw as HeapRef
        frame.assign(1, Value.Known(arrayRef, OBJECT))
        val run = execute(view, vm, frame)

        assertEquals(Value.Known(Int::class.javaPrimitiveType, CLASS), run.graph.returnedValue())
    }

    @Test
    fun getClassUsesTrackedRuntimeType() {
        // Bug class: Object.getClass reports the loose register tag instead of the tracked heap runtime type.
        // Oracle: JVM getClass and Plan 11 both use receiver runtime class; no raw guest object is materialized.
        val view = view(INVOKE_FIXTURE, "trackedRuntimeClass(Ljava/lang/Object;)Ljava/lang/Class;")
        val vm = VmServices(classIndex)
        val frame = Frame.entryFrame(view)
        val runtimeType = VirtualType.of("Lhash_code;")
        val receiver = frame.allocateObject(runtimeType, classIndex.currentResolver().instanceFieldDefaults(runtimeType)!!)
        frame.assign(1, Value.Known(receiver.raw, OBJECT))
        val run = execute(view, vm, frame)
        val returned = (run.graph.returnedValue() as Value.Known).raw as Class<*>

        assertEquals("hash_code", returned.name)
        assertSame(classIndex.classLoader, returned.classLoader)
    }

    @Test
    fun provenHostCallsOnInitializedClassesRetainNoSideEffects() {
        // Bug class: safe-class authorization either implies blanket NONE or leaves these three proven calls STRONG.
        // Oracle: P11-SE-001 names this exact constructor/append/toString composition and no other safe-class call.
        val targets = setOf(
            method(BUILDER, "<init>", "(Ljava/lang/String;)V"),
            method(BUILDER, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            method(BUILDER, "toString", "()Ljava/lang/String;"),
        )
        val policy = ReflectionPolicy.defaults()
        targets.forEach { assertEquals(SideEffectLevel.NONE, policy.effectOf(it), it.toString()) }
        assertEquals(
            SideEffectLevel.STRONG,
            policy.effectOf(method(BUILDER, "capacity", "()I")),
            "safe-class authorization is not a purity proof",
        )

        val vm = VmServices(classIndex, policy)
        val view = view(SIDE_EFFECT_FIXTURE, "provenHostCalls()Ljava/lang/String;")
        val frame = Frame.entryFrame(view)
        // Measure the calls' effects, not skipped framework initialization on first allocation.
        generateSequence(BUILDER, classIndex::superclassOf).forEach(frame.classes::complete)
        val run = execute(view, vm, frame)

        assertEquals(Value.Known("seed-value", STRING), run.graph.returnedValue())
        assertEquals(SideEffectLevel.NONE, run.graph.explored.methodSideEffectLevel())
    }

    @Test
    fun reflectedBoxedZeroAndArbitraryNumberUseReferenceIfAndSwitchSemantics() {
        // Bug class: reflected Number returns are read as integral zero by real if-eqz or packed-switch ops.
        // Oracle: Plan 11 reference tags control nullness and switch feasibility, independent of Number.intValue().
        val targets = setOf(
            method(HOST_FIXTURE, "boxedZero", "()Ljava/lang/Number;"),
            method(HOST_FIXTURE, "arbitraryNumber", "()Ljava/lang/Number;"),
        )
        val owner = vm(targets)

        val boxed = execute(INVOKE_FIXTURE, "boxedZeroUsesReferenceBranches()I", owner)
        val arbitrary = execute(INVOKE_FIXTURE, "arbitraryNumberUsesReferenceBranches()I", owner)

        assertEquals(Value.Known(1, INT), boxed.graph.returnedValue())
        assertEquals(Value.Known(1, INT), arbitrary.graph.returnedValue())
    }

    @Test
    fun reflectedRuntimeHostTypeControlsObjectArrayStore() {
        // Bug class: aput-object rejects a reflected StringBuilder because its return descriptor is Object.
        // Oracle: JVM array-store checks use runtime class; StringBuilder implements CharSequence.
        val target = method(HOST_FIXTURE, "builderAsObject", "()Ljava/lang/Object;")
        val run = execute(INVOKE_FIXTURE, "runtimeHostTypeControlsArrayStore()Ljava/lang/Object;", vm(setOf(target)))
        val returned = run.graph.returnedValue() as Value.Known

        assertTrue(returned.raw is HeapRef)
        assertEquals(BUILDER, run.session.currentNode!!.frame.heapObject(returned.raw as HeapRef)!!.type)
        assertEquals("runtime", run.session.currentNode!!.frame.heap.hostObjectAt(returned.raw as HeapRef).toString())
    }

    @Test
    fun synchronousReflectedMutationUsesOneDebuggerStepWithoutSyntheticActivation() {
        // Bug class: synchronous reflection creates a fake activation or delays imported mutation past one step.
        // Oracle: Plan 11 debugger contract makes one accepted synchronous invoke one scheduler step.
        val view = view(INVOKE_FIXTURE, "appendLong(Ljava/lang/StringBuilder;J)Ljava/lang/StringBuilder;")
        val vm = VmServices(classIndex)
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(BUILDER, StringBuilder("step"))
        frame.assign(0, Value.Known(ref, BUILDER))
        frame.assign(1, Value.Known(9L, LONG))
        val session = vm.newSession(view, entryFrame = frame)
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val continuation = debugger.stepInto()!!

        assertSame(invoke, debugger.lastNode)
        assertSame(invoke.activation, continuation.activation)
        assertEquals(1, invoke.children.size)
        assertEquals("step9", continuation.frame.heap.hostObjectAt(ref).toString())
        assertFalse(continuation.opExecutionStarted)
    }


    @Test
    fun overwrittenHostReturnsDoNotGrowLaterSelectedGraphs() {
        // Bug class: returned objects remain host-graph roots after their result register is overwritten.
        // Oracle: only the third builder is guest-reachable at length(), so the terminal graph holds one object.
        val fresh = method(HOST_FIXTURE, "freshBuilder", "(I)Ljava/lang/StringBuilder;")
        val length = method(BUILDER, "length", "()I")
        val run = execute(INVOKE_FIXTURE, "overwriteReturnedHosts()I", vm(setOf(fresh, length)))
        val terminal = requireNotNull(run.session.currentNode).frame

        assertEquals(Value.Known(3, INT), run.graph.returnedValue())
        assertEquals(1, terminal.heap.hostGraphObjects().size)
    }

    @Test
    fun suspendedCallerRootsSurviveGuestCalleeHostExecution() {
        // Bug class: selected compaction sees only callee registers and discards B held by the suspended caller.
        // Oracle: B remains live on the call chain, so caller resume returns its exact length seven.
        val length = method(BUILDER, "length", "()I")
        val vm = vm(setOf(length))
        val view = view(INVOKE_FIXTURE, "suspendedCallerKeepsOtherRoot(Ljava/lang/StringBuilder;Ljava/lang/StringBuilder;)I")
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(StringBuilder("alpha"), BUILDER)),
                HostSeed(Value.Known(StringBuilder("bravo!!"), BUILDER)),
            ),
        )
        view.parameters.zip(admitted).forEach { (parameter, value) -> frame.assign(parameter.register, value) }

        val run = execute(view, vm, frame)

        assertEquals(Value.Known(7, INT), run.graph.returnedValue())
        admitted.forEach { value ->
            val ref = (value as Value.Known).raw as HeapRef
            assertTrue(requireNotNull(run.session.currentNode).frame.isHostMaterializable(value))
            assertTrue(run.session.currentNode!!.frame.heap.hostObjectAt(ref) is StringBuilder)
        }
    }

    @Test
    fun siblingCallerPathsDoNotShareSuspendedRootInvalidation() {
        // Bug class: one activation imports or prunes B into a sibling that never allocated or referenced B.
        // Oracle: B exists only on its branch while suspended across the guest call; the sibling has no B HeapObject.
        val fresh = method(HOST_FIXTURE, "freshBuilder", "(I)Ljava/lang/StringBuilder;")
        val length = method(BUILDER, "length", "()I")
        val vm = vm(setOf(fresh, length))
        val view = view(INVOKE_FIXTURE, "siblingBranchLocalRoot(ILjava/lang/StringBuilder;)I")
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(StringBuilder("alpha"), BUILDER))),
        ).single()
        frame.assign(view.parameters[1].register, admitted)

        val graph = vm.newSession(view, entryFrame = frame).run()
        val terminals = graph.methodView.terminalInstructionAddresses.flatMap(graph::nodePile)
            .filter { it.outcome is Outcome.Returned }
        val withLocal = terminals.single { (it.outcome as Outcome.Returned).value == Value.Known(3, INT) }
        val withoutLocal = terminals.single { (it.outcome as Outcome.Returned).value == Value.Known(5, INT) }
        val localRef = ((withLocal.frame.peek(0) as Value.Known).raw as HeapRef)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(setOf(Value.Known(3, INT), Value.Known(5, INT)), graph.outcomes.map { (it as Outcome.Returned).value }.toSet())
        assertTrue((withLocal.frame.heapObject(localRef) as HeapObject.HostObject).contents is HostGraphContents.Known)
        assertEquals(null, withoutLocal.frame.heapObject(localRef))
    }

    private fun assertFreshIntArrayClone(run: FixtureRun) {
        val source = run.seeded.values.single() as Value.Known
        val clone = run.graph.returnedValue() as Value.Known
        val sourceRef = source.raw as HeapRef
        val cloneRef = clone.raw as HeapRef
        val frame = run.session.currentNode!!.frame

        assertNotEquals(sourceRef, cloneRef)
        assertEquals(INT_ARRAY, clone.type)
        assertEquals(frame.heap.arrayAt(sourceRef)!!.elements(), frame.heap.arrayAt(cloneRef)!!.elements())
    }

    private fun execute(
        className: String,
        descriptor: String,
        vm: VmServices = VmServices(classIndex),
        initial: Map<Int, Value> = emptyMap(),
        expectedCompletion: CompletionStatus = CompletionStatus.Complete,
    ): FixtureRun {
        val view = view(className, descriptor)
        val frame = Frame.entryFrame(view)
        val entries = initial.entries.toList()
        val admitted = frame.heap.admitHostSeeds(entries.map { HostSeed(it.value) })
        val seeded = entries.zip(admitted).associate { (entry, value) -> entry.key to value }
        seeded.forEach(frame::assign)
        return execute(view, vm, frame, seeded, expectedCompletion)
    }

    private fun execute(
        view: MethodView,
        vm: VmServices,
        frame: Frame,
        seeded: Map<Int, Value> = emptyMap(),
        expectedCompletion: CompletionStatus = CompletionStatus.Complete,
    ): FixtureRun {
        val session = vm.newSession(view, entryFrame = frame)
        val graph = session.run()
        assertEquals(expectedCompletion, graph.completion(), invocationDiagnostics(graph))
        return FixtureRun(session, graph, seeded)
    }

    private fun vm(exactSafe: Set<MethodDescriptor>): VmServices =
        VmServices(classIndex, ReflectionPolicy(exactSafeMethods = exactSafe))

    private fun view(owner: String, descriptor: String): MethodView =
        classIndex.requireMethodView(owner, descriptor)

    private fun method(owner: VirtualType, name: String, proto: String) = MethodDescriptor(owner, name, proto)

    private fun ExecutionGraph.returnedValue(): Value? {
        val outcome = outcomes.single()
        check(outcome is Outcome.Returned) { "$outcome: ${invocationDiagnostics(this)}" }
        return outcome.value
    }

    private fun invocationDiagnostics(graph: ExecutionGraph): String = graph.addresses()
        .flatMap(graph::nodePile)
        .joinToString { "${it.view.signature}@${it.address}:${it.invocationDiagnostic}:${it.hostFailure}" }

    private data class FixtureRun(
        val session: ExecutionSession,
        val graph: ExecutionGraph,
        val seeded: Map<Int, Value>,
    )

    private companion object {
        const val INVOKE_FIXTURE = "Linvoke_backend_test;"
        const val SIDE_EFFECT_FIXTURE = "Lbackend_side_effect_test;"
        const val PREFIX = "duty is heavy as a mountain"
        const val FULL_TEXT = "$PREFIX, death as light as a feather"
        val OBJECT = VirtualType.OBJECT
        val INT = VirtualType.of("I")
        val LONG = VirtualType.of("J")
        val INT_ARRAY = VirtualType.of("[I")
        val STRING = VirtualType.of("Ljava/lang/String;")
        val BOOLEAN = VirtualType.of("Ljava/lang/Boolean;")
        val INTEGER = VirtualType.of("Ljava/lang/Integer;")
        val LIST = VirtualType.of("Ljava/util/List;")
        val BUILDER = VirtualType.of("Ljava/lang/StringBuilder;")
        val CLASS = VirtualType.of("Ljava/lang/Class;")
        val NULL_POINTER = VirtualType.of("Ljava/lang/NullPointerException;")
        val HOST_FIXTURE = VirtualType.of("Lorg/cf/smalivm/core/BackendCorpusHostFixture;")
    }
}

class BackendCorpusHostFixture {
    companion object {
        @JvmStatic
        fun mutate(builder: StringBuilder) {
            builder.append("-mutated")
        }

        @JvmStatic
        fun boxBoolean(value: Boolean): Any = java.lang.Boolean.valueOf(value)

        @JvmStatic
        fun mutateThird(value: Long, marker: Int, builder: StringBuilder) {
            check(value == 0x1_0000_0002L && marker == 7)
            builder.append(", death as light as a feather")
        }

        @JvmStatic
        fun boxedZero(): Number = 0

        @JvmStatic
        fun arbitraryNumber(): Number = AtomicInteger(0)

        @JvmStatic
        fun builderAsObject(): Any = StringBuilder("runtime")

        @JvmStatic
        fun mutateThenThrow(builder: StringBuilder) {
            builder.append("-before-throw")
            throw IllegalStateException("target")
        }

        @JvmStatic
        fun pureLength(value: String): Int = value.length

        @JvmStatic
        fun freshBuilder(value: Int): StringBuilder = StringBuilder(value.toString())

        @JvmStatic
        fun opaqueEntrySetValue(entry: MutableMap.MutableEntry<Any?, Any?>, value: Any?) {
            entry.setValue(value)
        }

    }
}
