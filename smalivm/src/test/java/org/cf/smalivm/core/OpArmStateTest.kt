package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Plan 12 Task 45 exact-arm ownership and deterministic traversal contracts. */
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
        val sourceEnding = requireNotNull(source.endingFrame)
        val extraEnding = session.unattributedEndingFramesSnapshot().single()
        val refs = throws.map { (it.exception as Value.Known).raw as HeapRef }
        val sourceRef = refs.single { sourceEnding.heapObject(it) != null }
        val extraRef = refs.single { extraEnding.heapObject(it) != null }

        assertNull(source.frame.heapObject(sourceRef), "the source summary does not own one arm's allocation")
        assertEquals(
            throws.single { (it.exception as Value.Known).raw == extraRef }.exception,
            extraEnding.thrownValue(),
        )
        assertNull(extraEnding.heapObject(sourceRef), "separate throw events do not share arm allocations")
        assertNotNull(sourceEnding.heapObject(sourceRef))
        assertNotNull(extraEnding.heapObject(extraRef))
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
        assertEquals(2, session.opExecutionMetrics().committedThrowableAllocations)
    }

    @Test
    fun oneFreshThrowEventSharesOneRefAcrossCatchAndEscapeWithoutLeakingToNormal() {
        // EX-04 and the arm invariant: routing copies one normalized event; it never materializes per route.
        val view = view("Larray_length_test;", "forkFreshThrowAcrossUnknownHandler([I)Ljava/lang/Object;")
        val session = VmServices(CoreTester.classIndex).newSession(view)

        val graph = session.run()

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
        assertEquals(0, session.opExecutionMetrics().preArmThrowableAllocations)
        assertEquals(1, session.opExecutionMetrics().committedThrowableAllocations)
    }

    @Test
    fun knownIndexAputReconcilesOneCellAndSingleArmAputReconcilesNone() {
        // Plan 12 footprint invariant: cost follows named cells, not container size.
        val view = view("Laput_test;", "putObject()V")
        val absentArrayType = VirtualType.of("[Lspine/absent;")
        val ambiguousFrame = Frame.entryFrame(view).also {
            it.assign(0, it.allocateArray(absentArrayType, 10_000))
            it.assign(1, Value.Known(5_000, INT))
            it.assign(2, Value.Known("value", STRING))
        }
        val ambiguous = VmServices(CoreTester.classIndex).newSession(view, entryFrame = ambiguousFrame)

        ambiguous.run()

        assertEquals(
            2,
            ambiguous.opExecutionMetrics().persistentStateEntriesVisited,
            "two arms each read one named array cell; array length is irrelevant",
        )

        val intView = view("Laput_test;", "put()V")
        val exactFrame = Frame.entryFrame(intView).also {
            it.assign(0, it.allocateArray(INT_ARRAY, 10_000))
            it.assign(1, Value.Known(5_000, INT))
            it.assign(2, Value.Known(7, INT))
        }
        val exact = VmServices(CoreTester.classIndex).newSession(intView, entryFrame = exactFrame)

        exact.run()

        assertEquals(0, exact.opExecutionMetrics().persistentStateEntriesVisited)
        assertEquals(0, exact.opExecutionMetrics().reconciliationFootprintsResolved)
        assertEquals(2, exact.opExecutionMetrics().armCopies, "aput and return each use the single-arm fast path")
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
        frame.writeArrayElement(ref, 0, old)
        frame.assign(view.parameters[0].register, Value.Known("new", VirtualType.of("Ljava/lang/String;")))
        frame.assign(view.parameters[1].register, array)
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        assertEquals(setOf(old, stored), graph.outcomes.map { (it as Outcome.Returned).value }.toSet())
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(0, session.opExecutionMetrics().preArmThrowableAllocations)
        assertEquals(1, session.opExecutionMetrics().committedThrowableAllocations)
    }

    @Test
    fun unknownIndexAputVisitsExactlyItsRealAffectedRange() {
        // Plan 12 footprint invariant: uncertain indices name only the real array range.
        val view = view("Laput_test;", "put()V")
        val length = 64
        val frame = Frame.entryFrame(view).also {
            it.assign(0, it.allocateArray(INT_ARRAY, length))
            it.assign(1, Value.Unknown(INT))
            it.assign(2, Value.Known(7, INT))
        }
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        session.run()

        assertEquals(
            length * 2,
            session.opExecutionMetrics().persistentStateEntriesVisited,
            "normal and exceptional arms vote once for each genuinely uncertain array cell",
        )
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
    fun objectAndStaticReconciliationVisitOnlyNamedCells() {
        // Plan 12 footprint invariant: unrelated roots and cells are not traversed.
        val realView = view("Lspine/spine;", "returnWide()J")
        val view = MethodView(
            realView.signature,
            realView.definingClass,
            realView.returnType,
            realView.isStatic,
            10_000,
            realView.parameters,
            realView.tryBlocks,
            realView.addresses.associateWith(realView::instructionAt),
            programRevision = realView.programRevision,
            methodRevision = realView.methodRevision,
        )
        val owner = VirtualType.of("Lspine/spine;")
        val fields = (0 until 1_000).associate { "field$it:I" to Value.Known(it, INT) }
        val frame = Frame.entryFrame(view).also { large ->
            repeat(10_000) { large.assign(it, Value.Known(it, INT)) }
        }
        val ref = (frame.allocateObject(owner, fields).raw as HeapRef)
        repeat(1_000) {
            frame.allocateArray(INT_ARRAY, 8)
            frame.allocateObject(owner, mapOf("unrelated:I" to Value.Known(it, INT)))
            frame.writeStaticField("Lspine/spine;->unrelated$it:I", Value.Known(it, INT))
        }
        val staticDescriptor = "Lspine/spine;->named:I"
        frame.writeStaticField(staticDescriptor, Value.Known(1, INT))
        val fork = object : Op(0) {
            override val name: String = "granular-footprint-fork"

            override fun execute(frame: Frame): OpExecution = OpExecution.of(
                OpArm.Continue(
                    OpUpdate.writeField(ref, "field0:I", Value.Known(7, INT))
                        .then(OpUpdate.writeStatic(staticDescriptor, Value.Known(7, INT))),
                ),
                OpArm.Throw(
                    evidence = ThrowEvidence.Fresh(
                        ThrowableRecipe.of(VirtualType.of("Ljava/lang/NullPointerException;")),
                    ),
                ),
            )
        }
        val session = ExecutionSession.of(view, CoreTester.classIndex, Limits(), frame) { address, current, index ->
            if (address == 0) fork else OpBuilder.build(address, current, index)
        }

        val source = session.step()!!

        assertEquals(4, session.opExecutionMetrics().persistentStateEntriesVisited)
        assertEquals(Value.Known(999, INT), source.frame.getField(ref, "field999:I"))
        assertEquals(Value.Known(9_999, INT), source.frame.peek(9_999))
        assertEquals(Value.Unknown(INT), source.frame.getField(ref, "field0:I"))
        assertEquals(Value.Unknown(INT), source.frame.getStaticField(owner, staticDescriptor))
    }

    @Test
    fun reconciliationIncludesWideHighHalvesAndClearedOldPartners() {
        // Plan 12 wide-partner invariant: low/high pairs reconcile as one register value.
        val view = view("Lspine/spine;", "returnWide()J")
        val wide = VirtualType.of("J")
        val frame = Frame.entryFrame(view).also { it.assign(0, Value.Known(42L, wide)) }
        val fork = object : Op(0) {
            override val name: String = "wide-partner-fork"

            override fun execute(frame: Frame): OpExecution = OpExecution.of(
                OpArm.Continue(OpUpdate.assign(1, Value.Known(7, INT))),
                OpArm.Throw(
                    evidence = ThrowEvidence.Fresh(
                        ThrowableRecipe.of(VirtualType.of("Ljava/lang/NullPointerException;")),
                    ),
                ),
            )
        }
        val session = ExecutionSession.of(view, CoreTester.classIndex, Limits(), frame) { address, current, index ->
            if (address == 0) fork else OpBuilder.build(address, current, index)
        }

        val source = session.step()!!

        assertEquals(Value.Top, source.frame.peek(0), "one arm cleared the old wide low half")
        assertEquals(Value.Top, source.frame.peek(1), "the old high half is not left as an orphan marker")
        assertEquals(4, session.opExecutionMetrics().persistentStateEntriesVisited)
    }

    @Test
    fun newArrayInitializesEachDefaultCellOnceAndUsesNoReconciliation() {
        // Plan 12 array-construction invariant: one persistent default initialization pass.
        val length = 10_000
        val view = view("Lnew_array_test;", "createIntegerArray()V")
        val frame = Frame.entryFrame(view).also { it.assign(0, Value.Known(length, INT)) }
        val session = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame)

        val graph = session.run()

        assertEquals(0, session.opExecutionMetrics().persistentStateEntriesVisited)
        val returned = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single()
        assertEquals(
            length,
            returned.endingFrame!!.stateSharingSnapshot().copies.arrayAllocationElementsInitialized,
        )
        assertTrue(graph.trusted()!!.terminatingConsensus(0) is Value.Known)
    }

    private fun view(owner: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(owner, descriptor)

    private companion object {
        val INT = VirtualType.of("I")
        val INT_ARRAY = VirtualType.of("[I")
        val STRING = VirtualType.of("Ljava/lang/String;")
    }
}
