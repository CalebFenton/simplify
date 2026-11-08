package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Op-arm ownership of frames and fresh throwables, and footprint-scoped reconciliation. */
class OpArmStateTest {
    @Test
    fun terminalThrowFramesOwnTheirMaterializedThrowableRefs() {
        val view = view("Laget_test;", "get()V")
        val frame = Frame.entryFrame(view).also {
            it.assign(0, Value.Unknown(INT_ARRAY))
            it.assign(1, Value.Unknown(INT))
        }
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        val throws = graph.outcomes.filterIsInstance<Outcome.Threw>()
        assertEquals(2, throws.size)
        val source = graph.nodePile(0).single()
        val sourceEnding = source.terminalFrame
        val refs = throws.map { (it.exception as Value.Known).raw as HeapRef }
        // `single`: the source ending owns its own arm's allocation and not the other throw event's.
        val sourceRef = refs.single { sourceEnding.heapObject(it) != null }

        assertNull(source.frame.heapObject(sourceRef), "the source summary does not own one arm's allocation")
        assertNotNull(sourceEnding.heapObject(sourceRef))
    }

    @Test
    fun unknownArrayReadProducesTwoDistinctThrowableOutcomes() {
        // A null array and an invalid index are independent feasible failures of this unknown read.
        val view = view("Laget_test;", "get()V")
        val frame = Frame.entryFrame(view).also {
            it.assign(0, Value.Unknown(INT_ARRAY))
            it.assign(1, Value.Unknown(INT))
        }
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        val throws = graph.outcomes.filterIsInstance<Outcome.Threw>()
        val refs = throws.map { (it.exception as Value.Known).raw as HeapRef }
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, throws.size)
        assertEquals(
            setOf(
                VirtualType.of("Ljava/lang/NullPointerException;"),
                VirtualType.of("Ljava/lang/ArrayIndexOutOfBoundsException;"),
            ),
            throws.map { it.exception.type }.toSet(),
        )
        assertEquals(2, refs.toSet().size, "independent fresh throw events must keep distinct identities")
        val sourceEnding = graph.nodePile(0).single().terminalFrame
        assertTrue(
            guestThrowables(sourceEnding).single() in refs,
            "each throw event materializes exactly one throwable, owned by its own ending frame",
        )
    }

    @Test
    fun oneFreshThrowEventSharesOneRefAcrossCatchAndEscapeWithoutLeakingToNormal() {
        // EX-04 and the arm invariant: routing copies one normalized event; it never materializes per route.
        val view = view("Larray_length_test;", "forkFreshThrowAcrossUnknownHandler([I)Ljava/lang/Object;")
        val graph = VmServices(CoreTester.classIndex).newSession(view).run()

        val escaped = (graph.outcomes.single { it is Outcome.Threw } as Outcome.Threw).exception as Value.Known
        val caught = graph.outcomes.filterIsInstance<Outcome.Returned>()
            .mapNotNull { it.value as? Value.Known }
            .single { it.raw is HeapRef }
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        assertEquals(escaped, caught, "all local routes from one throw event must share one identity")
        assertEquals(2, returned.size)
        assertTrue(returned.single { it != caught }!!.readsAsNullReference, "the normal route returns its null sentinel")
        assertEquals(3, graph.outcomes.size, "normal, caught, and escaping routes must each finish")
        assertEquals(CompletionStatus.Complete, graph.completion())
        // One event materialized once, after arm commit: the normal route's frame holds no throwable, and
        // every route of the event holds exactly the shared ref.
        val endings = graph.addresses().flatMap(graph::nodePile).filter { it.outcome != null }
        for (node in endings) {
            val expected = if (node.outcome == Outcome.Returned(returned.single { it != caught })) emptyList() else listOf(escaped.raw)
            assertEquals(expected, guestThrowables(node.terminalFrame), "${node.outcome}")
        }
    }

    @Test
    fun forkReconciliationCostFollowsNamedCellsNotStateSize() {
        // Bug class: reconciling a fork joins the whole written array, or the whole frame state, instead of the
        // cells the arms' updates name. Oracle: both frames hold the same written object, and the large one adds
        // a 2^20-element written array, 10,000 registers, and 60,000 unrelated objects, arrays and statics.
        // Named-cell reconciliation joins the same four cells in both, so a fork costs about the same; joining the
        // written array alone visits 2^20 cells per fork. The value checks catch a footprint that drops a named cell.
        val small = forkFrame(registers = 16, unrelated = 0, arrayLength = 1)
        val large = forkFrame(registers = 10_000, unrelated = 20_000, arrayLength = MAX_TRACKED_ARRAY_LENGTH)

        assertCostRatio(4.0, { List(FORKS) { forkSession(small) } }, { List(FORKS) { forkSession(large) } }) { sessions ->
            sessions.forEach { it.step() }
        }

        val source = forkSession(large).step()!!
        val arrayRef = large.arrayRef!!
        assertEquals(Value.Unknown(INT), source.frame.getField(large.objectRef, "field0:I"))
        assertEquals(Value.Unknown(INT), source.frame.getStaticField(OWNER, NAMED_STATIC))
        assertEquals(Value.Unknown(INT), source.frame.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Known(0, INT), source.frame.heap.arrayAt(arrayRef)!!.element(1))
    }

    @Test
    fun caughtKnownIndexAputKeepsTheStoreOnOnlyTheNormalArm() {
        // JVMS 6.5 aastore: a successful store changes the cell; ArrayStoreException changes nothing.
        val view = view(
            "Laput_test;",
            "putObjectAndReturnObserved(Ljava/lang/Object;[Lspine/absent;)Ljava/lang/Object;",
        )
        val component = VirtualType.of("Lspine/absent;")
        val arrayType = VirtualType.of("[Lspine/absent;")
        val frame = Frame.entryFrame(view)
        val array = frame.allocateArray(arrayType, 1)
        val ref = array.raw as HeapRef
        val old = Value.Known("old", component)
        val stored = Value.Known("new", component)
        frame.heap.setArrayElement(ref, 0, old)
        frame.assign(view.parameters[0].register, Value.Known("new", VirtualType.of("Ljava/lang/String;")))
        frame.assign(view.parameters[1].register, array)
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        assertEquals(setOf(old, stored), graph.outcomes.map { (it as Outcome.Returned).value }.toSet())
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val storeFailures = graph.addresses().flatMap(graph::nodePile).filter { it.outcome != null }
            .flatMap { guestThrowables(it.terminalFrame) }
        assertEquals(1, storeFailures.size, "the ArrayStoreException is materialized once, after arm commit")
    }

    @Test
    fun unknownIndexAputJoinsEveryRealCellInTheSourceSummary() {
        // Bug class: the footprint of an uncertain index names too few cells (the source keeps stale values)
        // or cells past the array's length (reconciliation writes out of range).
        val view = view("Laput_test;", "put()V")
        val length = 64
        val frame = Frame.entryFrame(view).also {
            it.assign(0, it.allocateArray(INT_ARRAY, length))
            it.assign(1, Value.Unknown(INT))
            it.assign(2, Value.Known(7, INT))
        }
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        // Normal arm: an unknown index joins every slot with 7. Exceptional arm: the array is unchanged. The
        // source summary therefore holds join(0, 7) in every real cell.
        val summary = graph.nodePile(0).single().frame.heap.arrayAt(frame.peek(0)!!.let { (it as Value.Known).raw as HeapRef })!!
        assertEquals(List(length) { Value.Unknown(INT) }, summary.elements())
    }

    @Test
    fun arrayUpdateFootprintsMergeAdjacentAndOverlappingRanges() {
        val ref = HeapRef(7)
        val update = OpUpdate.writeArrayElements(ref, 4, List(3) { Value.Known(it, INT) })
            .then(OpUpdate.writeArrayElements(ref, 7, List(2) { Value.Known(it, INT) }))
            .then(OpUpdate.writeArrayElements(ref, 2, List(3) { Value.Known(it, INT) }))

        assertEquals(listOf(2..8), update.footprint.arrayRanges.getValue(ref))
    }

    @Test
    fun objectAndStaticReconciliationJoinOnlyNamedCells() {
        val (view, frame, ref) = forkFrame()
        val session = CoreTester.vmServices.newSession(view, Limits(), frame) { address, current, resolver ->
            if (address == 0) namedCellFork(ref, arrayRef = null) else OpBuilder.build(address, current, resolver)
        }

        val source = session.step()!!

        assertEquals(Value.Known(999, INT), source.frame.getField(ref, "field999:I"))
        assertEquals(Value.Known(9_999, INT), source.frame.peek(9_999))
        assertEquals(Value.Unknown(INT), source.frame.getField(ref, "field0:I"))
        assertEquals(Value.Unknown(INT), source.frame.getStaticField(OWNER, NAMED_STATIC))
    }

    @Test
    fun reconciliationIncludesWideHighHalvesAndClearedOldPartners() {
        // Low/high wide pairs reconcile as one register value.
        val view = view("Lspine/spine;", "returnWide()J")
        val wide = VirtualType.of("J")
        val frame = Frame.entryFrame(view).also { it.assign(0, Value.Known(42L, wide)) }
        val fork = object : Op(0) {
            override val name: String = "wide-partner-fork"

            override fun execute(frame: Frame): OpExecution = OpExecution.of(
                OpArm.Continue(OpUpdate.assign(1, Value.Known(7, INT))),
                OpArm.Throw(
                    evidence = ThrowEvidence.Fresh(
                        GuestThrowables.nullPointer(),
                    ),
                ),
            )
        }
        val session = CoreTester.vmServices.newSession(view, Limits(), frame) { address, current, resolver ->
            if (address == 0) fork else OpBuilder.build(address, current, resolver)
        }

        val source = session.step()!!

        assertEquals(Value.Top, source.frame.peek(0), "one arm cleared the old wide low half")
        assertEquals(Value.Top, source.frame.peek(1), "the old high half is not left as an orphan marker")
    }

    @Test
    fun newArrayHoldsTheTypeDefaultInEveryCell() {
        val length = 10_000
        val view = view("Lnew_array_test;", "createIntegerArray()V")
        val frame = Frame.entryFrame(view).also { it.assign(0, Value.Known(length, INT)) }
        val graph = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame).run()

        val array = graph.trusted()!!.terminatingConsensus(0) as Value.Known
        val returned = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single()
        assertEquals(List(length) { Value.Known(0, INT) }, returned.terminalFrame.heap.arrayAt(array.raw as HeapRef)!!.elements())
    }

    /** Every guest throwable in [frame]'s heap, in allocation order. */
    private fun guestThrowables(frame: Frame): List<HeapRef> =
        heapRefsOf(frame).filter { frame.heapObject(it) is HeapObject.GuestThrowable }

    private data class ForkFrame(val view: MethodView, val frame: Frame, val objectRef: HeapRef, val arrayRef: HeapRef?)

    /** A frame holding one 1,000-field object, [unrelated] unrelated cells of each kind and an optional int array. */
    private fun forkFrame(registers: Int = 10_000, unrelated: Int = 1_000, arrayLength: Int? = null): ForkFrame {
        val view = CoreTester.classIndex.rebuiltView(view("Lspine/spine;", "returnWide()J"), registerCount = registers)
        val fields = (0 until 1_000).associate { "field$it:I" to Value.Known(it, INT) }
        val frame = Frame.entryFrame(view).also { large ->
            repeat(registers) { large.assign(it, Value.Known(it, INT)) }
        }
        val ref = frame.allocateObject(OWNER, fields).raw as HeapRef
        repeat(unrelated) {
            frame.allocateArray(INT_ARRAY, 8)
            frame.allocateObject(OWNER, mapOf("unrelated:I" to Value.Known(it, INT)))
            frame.classes.setStaticField("Lspine/spine;->unrelated$it:I", Value.Known(it, INT))
        }
        frame.classes.setStaticField(NAMED_STATIC, Value.Known(1, INT))
        val arrayRef = arrayLength?.let { frame.allocateArray(INT_ARRAY, it).raw as HeapRef }
        return ForkFrame(view, frame, ref, arrayRef)
    }

    /** A session whose entry op is [namedCellFork] over [fork]'s object and array. */
    private fun forkSession(fork: ForkFrame): ExecutionSession {
        val op = namedCellFork(fork.objectRef, fork.arrayRef)
        return CoreTester.vmServices.newSession(fork.view, Limits(), fork.frame) { address, current, resolver ->
            if (address == 0) op else OpBuilder.build(address, current, resolver)
        }
    }

    /** A two-arm op whose normal arm writes one field, one static and optionally one array cell. */
    private fun namedCellFork(ref: HeapRef, arrayRef: HeapRef?): Op = object : Op(0) {
        override val name: String = "granular-footprint-fork"

        override fun execute(frame: Frame): OpExecution {
            var update = OpUpdate.writeField(ref, "field0:I", Value.Known(7, INT))
                .then(OpUpdate.writeStatic(NAMED_STATIC, Value.Known(7, INT)))
            if (arrayRef != null) update = update.then(OpUpdate.writeArrayElements(arrayRef, 0, listOf(Value.Known(7, INT))))
            return OpExecution.of(
                OpArm.Continue(update),
                OpArm.Throw(
                    evidence = ThrowEvidence.Fresh(GuestThrowables.nullPointer()),
                ),
            )
        }
    }

    private fun view(owner: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(owner, descriptor)

    private companion object {
        val INT = VirtualType.of("I")
        val INT_ARRAY = VirtualType.of("[I")
        val STRING = VirtualType.of("Ljava/lang/String;")
        val OWNER = VirtualType.of("Lspine/spine;")
        const val NAMED_STATIC = "Lspine/spine;->named:I"
        const val FORKS = 100
    }
}
