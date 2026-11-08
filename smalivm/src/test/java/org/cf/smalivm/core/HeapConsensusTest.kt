package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Heap reconciliation (spec §6.3, §6.5). `terminatingConsensus` answering `Known(HeapRef)` proves the
 * returning paths agree about WHICH object a register denotes; these tests are about the separate and
 * strictly harder question of whether they agree about its CONTENTS.
 */
class HeapConsensusTest {

    private val INT = VirtualType.of("I")
    private val INT_ARRAY = VirtualType.of("[I")

    @Test
    fun aSingleReturningPathReportsItsArrayVerbatim() {
        val graph = CoreTester.execute("Lnew_array_test;", "createIntegerArray()V", CoreState().apply {
            setRegisters(0, 2, "I")
        })
        val trusted = graph.trusted()
        assertNotNull(trusted, "an unforked run must complete")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(2, array.length)
        assertEquals(Value.Known(0, INT), array.element(0))
    }

    @Test
    fun elementsTwoReturningPathsDisagreeAboutJoinToUnknown() {
        // THE POINT OF THIS TASK. Both paths carry the SAME HeapRef -- one allocation before the fork --
        // so `agreesWith` is true and `terminatingConsensus` keeps the Known ref. Their heaps hold
        // different elements. Reading either path's heap would be a confident wrong answer; the join
        // must answer Unknown for the disagreeing index and keep the agreeing one.
        //
        // Expected values from spec §4.1's join table: Known(7,I) ⊔ Known(9,I) -> types join, values
        // disagree -> Unknown(I); Known(0,I) ⊔ Known(0,I) -> agree -> Known(0,I).
        val graph = CoreTester.execute(CLASS_NAME, "forkThenWriteDifferentElements(I)V")
        val trusted = graph.trusted()
        assertNotNull(trusted, "a throw fork whose arms both return still completes")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(2, array.length)
        assertEquals(Value.Unknown(INT), array.element(0))
        assertEquals(Value.Known(0, INT), array.element(1))
    }

    @Test
    fun aRefNoReturningPathKnowsAboutIsAbsentRatherThanEmpty() {
        // Absent, not a zero-length array: fabricating a shape for an object no path allocated is
        // exactly the "invent an answer" failure the lattice exists to prevent. `returnFive()I` allocates nothing.
        val graph = CoreTester.execute("Lspine/spine;", "returnFive()I")
        assertNotNull(graph.trusted(), "an unforked, opcode-supported run must complete")
        assertEquals(Agreement.Absent, graph.trusted()!!.terminatingHeapObject(HeapRef(9999)))
    }

    @Test
    fun aRunWithNoReturningPathAnswersNoPathRatherThanAbsent() {
        // The only path aborts on an unsupported opcode, so there is no returning path to ask about any ref.
        val graph = CoreTester.execute("Lspine/spine;", "unsupportedWideFilledArray()I")
        assertNull(graph.trusted(), "an UNSUPPORTED_OP abort must refuse trust")
        assertEquals(Agreement.NoPath, graph.explored.terminatingHeapObject(HeapRef(0)))
    }

    // ------------------------------------------------------------------------------------------
    // Hand-built reconciliation tests. Written when nothing in the tree could produce two REAL
    // returning paths carrying one array, because `new-array` and `aput` were both unimplemented and
    // elementsTwoReturningPathsDisagreeAboutJoinToUnknown -- the actual point of Plan 6's heap task --
    // was therefore @Disabled. Plan 7's Tasks 6 and 8 landed those two opcodes and that test is now
    // LIVE, so these no longer close a coverage gap; they are KEPT because they still test something
    // the smali-driven test cannot -- reconciliation in isolation, including two shapes real execution
    // cannot construct at all (two independent HeapStores colliding on one id).
    //
    // The technique, the same way ExecutionGraphTest.consensusRangeChecksAgainstTheNodesOwnViewNotTheGraphsMethodView does:
    // both ExecutionNode and ExecutionGraph constructors are `internal` (reachable from this module),
    // so a graph can be hand-assembled from frames built with Plan 6's own test-visible primitives
    // (Frame.allocateArray/allocateThrowable/writeArrayElement, Frame.copy) with no
    // opcode involved at all. This is a DIFFERENT concern from the end-to-end smali test above -- a
    // narrow unit test of the reconciliation logic in isolation, not a duplicate of a whole-pipeline
    // test that happens to exercise the same rule.
    // ------------------------------------------------------------------------------------------

    @Test
    fun terminatingHeapObjectReportsASingleReturningPathsArrayVerbatim() {
        // M10 (Plan 6 whole-branch review): the hand-built counterpart of
        // aSingleReturningPathReportsItsArrayVerbatim above, covering the SAME claim -- a lone
        // returning path's array is reported as-is, no join needed -- which at the time meant covering
        // it without waiting on Plan 7's new-array/aput (both since landed, in Tasks 6 and 8, so that
        // test is live). Every OTHER hand-built graph in this file uses TWO returning paths; this was
        // the single-path case with no live coverage at all.
        val view = spineView("returnFive()I")
        val seed = Frame.entryFrame(view)
        val ref = seed.allocateArray(INT_ARRAY, 2).raw as HeapRef
        seed.writeArrayElement(ref, 0, Value.Known(7, INT))
        // index 1 is left at its allocation default (Known(0, INT)).

        val graph = completeGraphOf(view, returnedNodeAt(view, seed))

        val array = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(2, array.length)
        assertEquals(Value.Known(7, INT), array.element(0))
        assertEquals(Value.Known(0, INT), array.element(1))
    }

    @Test
    fun terminatingHeapObjectJoinsDisagreeingElementsAndKeepsAgreeingOnes() {
        // The hand-built counterpart of elementsTwoReturningPathsDisagreeAboutJoinToUnknown above,
        // same expected values (spec §4.1's join table), same shape (one array allocated BEFORE the
        // fork so both paths carry the same HeapRef, per HeapStore.copy()'s id-preserving contract) --
        // just built directly rather than through div-int/new-array/aput.
        val view = spineView("returnFive()I")
        val seed = Frame.entryFrame(view)
        val ref = seed.allocateArray(INT_ARRAY, 2).raw as HeapRef
        val pathA = seed.copy()
        val pathB = seed.copy()
        pathA.writeArrayElement(ref, 0, Value.Known(7, INT))
        pathB.writeArrayElement(ref, 0, Value.Known(9, INT))
        // index 1 is left at its allocation default (Known(0, INT)) on both paths.

        val graph = completeGraphOf(view, returnedNodeAt(view, pathA), returnedNodeAt(view, pathB))

        val array = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        assertEquals(2, array.length)
        assertEquals(
            Value.Unknown(INT), array.element(0),
            "7 vs 9 disagree -- must join to Unknown, never silently pick one path's value",
        )
        assertEquals(
            Value.Known(0, INT), array.element(1),
            "both paths left this at the same default -- must stay Known, not be narrowed just because a sibling index disagreed",
        )
    }

    @Test
    fun terminatingHeapObjectDisagreesWhenReturningPathsDisagreeAboutArrayShape() {
        // A length mismatch under one id cannot arise from real execution (HeapIds' shared counter gives two
        // allocations distinct ids), so two INDEPENDENT HeapStores whose counters both start at 0 construct it
        // directly: their first allocations coincide on HeapRef(0) as two different objects.
        val view = spineView("returnFive()I")
        val ref = HeapRef(0)
        val frameA = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also { it.allocateArray(INT_ARRAY, 2) }
        val frameB = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also { it.allocateArray(INT_ARRAY, 3) }

        val graph = completeGraphOf(view, returnedNodeAt(view, frameA), returnedNodeAt(view, frameB))

        assertEquals(
            Agreement.Disagree,
            graph.trusted()!!.terminatingHeapObject(ref),
            "two returning paths disagreeing about an array's length must never yield one path's shape",
        )
    }

    @Test
    fun terminatingHeapObjectPreservesAGuestThrowablesIdentityAcrossAFork() {
        // The realistic case: ONE throwable synthesized before a fork. The immutable heap-object version
        // is shared by every persistent heap root, so every path holds the identical host instance under the
        // shared id -- this is what makes the identity check in the GuestThrowable branch
        // (ExecutionGraph.kt:172-177) an invariant rather than a guess, and it needs pinning too: that
        // branch is dead code today (no op calls Frame.allocateThrowable yet -- BinaryMathOp's
        // ArithmeticException becomes a heap object only in Task 4), so nothing else in the suite
        // exercises the happy path this guard is built to recognize.
        val view = spineView("returnFive()I")
        val type = VirtualType.of("Ljava/lang/ArithmeticException;")
        val seed = Frame.entryFrame(view)
        val sharedThrowable = ArithmeticException("/ by zero")
        val ref = seed.allocateThrowable(type, sharedThrowable).raw as HeapRef

        val graph = completeGraphOf(view, returnedNodeAt(view, seed.copy()), returnedNodeAt(view, seed.copy()))

        val agreed = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.GuestThrowable
        assertTrue(agreed.contents is HostGraphContents.Known)
        assertTrue(
            graph.terminatingHostObjectOverExploredPaths(ref) === sharedThrowable,
            "a fork must preserve the SAME synthesized instance",
        )
    }

    @Test
    fun terminatingThrowableConsensusPreservesItsGuestCauseIdentity() {
        val view = spineView("returnFive()I")
        val frame = Frame.entryFrame(view)
        val causeType = VirtualType.of("Ljava/lang/NullPointerException;")
        val wrapperType = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val cause = frame.allocateThrowable(causeType, NullPointerException("cause"))
        val wrapper = frame.allocateThrowable(
            wrapperType,
            ExceptionInInitializerError(frame.hostObjectAt(cause.raw as HeapRef) as Throwable),
            cause,
        )

        val graph = completeGraphOf(view, returnedNodeAt(view, frame.copy()), returnedNodeAt(view, frame.copy()))

        val consensus = graph.trusted()!!.terminatingHeapObject(wrapper.raw as HeapRef).agreed()
            as HeapObject.GuestThrowable
        assertEquals(cause, consensus.cause)
    }

    @Test
    fun terminatingThrowableConsensusJoinsDistinctKnownCauseRefsToUnknown() {
        val view = spineView("returnFive()I")
        val causeType = VirtualType.of("Ljava/lang/NullPointerException;")
        val wrapperType = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val fillerType = VirtualType.of("Lheap_consensus/filler;")

        val frameA = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore())
        val hostCauseA = NullPointerException("first")
        val causeA = frameA.allocateThrowable(causeType, hostCauseA)
        frameA.allocateObject(fillerType, emptyMap())
        val wrapperA = frameA.allocateThrowable(wrapperType, ExceptionInInitializerError(hostCauseA), causeA)

        val frameB = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore())
        frameB.allocateObject(fillerType, emptyMap())
        val hostCauseB = NullPointerException("second")
        val causeB = frameB.allocateThrowable(causeType, hostCauseB)
        val wrapperB = frameB.allocateThrowable(wrapperType, ExceptionInInitializerError(hostCauseB), causeB)
        assertEquals(wrapperA.raw, wrapperB.raw)
        assertTrue(causeA.raw != causeB.raw)

        val graph = completeGraphOf(view, returnedNodeAt(view, frameA), returnedNodeAt(view, frameB))

        val consensus = graph.trusted()!!.terminatingHeapObject(wrapperA.raw as HeapRef).agreed()
            as HeapObject.GuestThrowable
        assertEquals(
            Value.Unknown(causeType),
            consensus.cause,
            "two distinct known cause refs must join; selecting the first path is unsound",
        )
    }

    @Test
    fun terminatingThrowableConsensusJoinsNoCauseAndKnownCauseToTop() {
        val view = spineView("returnFive()I")
        val causeType = VirtualType.of("Ljava/lang/NullPointerException;")
        val wrapperType = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val fillerType = VirtualType.of("Lheap_consensus/filler;")

        val noCause = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore())
        noCause.allocateObject(fillerType, emptyMap())
        noCause.allocateObject(fillerType, emptyMap())
        val wrapperWithoutCause = noCause.allocateThrowable(wrapperType, ExceptionInInitializerError())

        val withCause = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore())
        withCause.allocateObject(fillerType, emptyMap())
        val hostCause = NullPointerException("cause")
        val cause = withCause.allocateThrowable(causeType, hostCause)
        val wrapperWithCause = withCause.allocateThrowable(
            wrapperType,
            ExceptionInInitializerError(hostCause),
            cause,
        )
        assertEquals(wrapperWithoutCause.raw, wrapperWithCause.raw)

        val graph = completeGraphOf(view, returnedNodeAt(view, noCause), returnedNodeAt(view, withCause))

        val consensus = graph.trusted()!!.terminatingHeapObject(wrapperWithoutCause.raw as HeapRef).agreed()
            as HeapObject.GuestThrowable
        assertEquals(
            Value.Top,
            consensus.cause,
            "absence on one path and a cause on another disagree about cause presence",
        )
    }

    @Test
    fun terminatingHeapObjectRetainsThrowableTypeWithUnknownContentsWhenGraphVersionsDisagree() {
        // The defensive twin of the test above, same technique as the array shape test: two
        // INDEPENDENT HeapStores whose counters both start at 0, so two GENUINELY DIFFERENT
        // synthesized throwables coincide on HeapRef(0). HeapIds makes this impossible through any
        // real fork (Disagreement here would mean two allocations under one id), but the `===` check
        // this pins is what stands between that invariant holding and a silent "two different
        // exceptions must be the same one" answer if it ever does not -- exactly the dangerous
        // direction (agreement invented where none exists) this whole task exists to close off.
        val view = spineView("returnFive()I")
        val type = VirtualType.of("Ljava/lang/ArithmeticException;")
        val ref = HeapRef(0)
        val frameA = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also { it.allocateThrowable(type, ArithmeticException("first")) }
        val frameB = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also { it.allocateThrowable(type, ArithmeticException("second")) }

        val graph = completeGraphOf(view, returnedNodeAt(view, frameA), returnedNodeAt(view, frameB))

        val merged = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.GuestThrowable
        assertEquals(type, merged.type)
        assertEquals(HostGraphContents.Unknown, merged.contents)
    }

    @Test
    fun objectFieldPresentOnOnlyOneReturningPathJoinsAgainstTop() {
        // Ordinary execution cannot remove a declared object cell after allocation, so this shape needs
        // hand-built frames. It is still a public consensus contract: imported or future collapsed states
        // can disagree about cell presence, and skipping the absent vote would invent the known value.
        val view = spineView("returnFive()I")
        val type = VirtualType.of("Lheap_consensus/object;")
        val field = "Lheap_consensus/object;->value:I"
        val ref = HeapRef(0)
        val present = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also {
            it.allocateObject(type, mapOf(field to Value.Known(7, INT)))
        }
        val absent = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also {
            it.allocateObject(type, emptyMap())
        }

        val graph = completeGraphOf(view, returnedNodeAt(view, present), returnedNodeAt(view, absent))

        val consensus = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedObject
        assertEquals(
            Value.Top,
            consensus.field(field),
            "Known(7) joined with an absent cell contributes Top; an absent path is never skipped",
        )
    }

    @Test
    fun objectMissingFromOneReturningPathMakesObjectConsensusUnavailable() {
        // A real fork preserves pre-fork objects, so this defensive shape is hand-built. The public query
        // must still reject disagreement about whether the object exists, as its contract states.
        val view = spineView("returnFive()I")
        val type = VirtualType.of("Lheap_consensus/object;")
        val field = "Lheap_consensus/object;->value:I"
        val ref = HeapRef(0)
        val present = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore()).also {
            it.allocateObject(type, mapOf(field to Value.Known(7, INT)))
        }
        val missing = Frame.entryFrame(view, HeapStore(HeapIds()), ClassInitStore())

        val graph = completeGraphOf(view, returnedNodeAt(view, present), returnedNodeAt(view, missing))

        assertEquals(
            Agreement.Absent,
            graph.trusted()!!.terminatingHeapObject(ref),
            "one returning path lacks the object, so no object-content consensus is available",
        )
    }

    @Test
    fun aReflectedFieldWhoseAccessibilityDiffersAcrossReturningPathsJoinsToUnknownAccessibility() {
        // One Field object, made accessible on one returning path only. The executable heap merge never sees
        // this pair (callers partition by accessibility first); the terminating query joins it, and the join of
        // two definite accessibilities is UNKNOWN, never either path's own.
        val view = spineView("returnFive()I")
        val owner = VirtualType.of("Lheap_consensus/owner;")
        val metadata = TrackedFieldValue(
            owner, "secret", "${owner.descriptor}->secret:I", INT, java.lang.reflect.Modifier.PRIVATE,
            FieldVisibility.PRIVATE, FieldStorage.INSTANCE, TrackedFieldDomain.Virtual,
        )
        val seed = Frame.entryFrame(view)
        val ref = seed.allocateTrackedField(metadata).raw as HeapRef
        val checked = seed.copy()
        val overridden = seed.copy().also { it.setTrackedFieldAccessibility(ref, FieldAccessibility.OVERRIDDEN) }

        val graph = completeGraphOf(view, returnedNodeAt(view, checked), returnedNodeAt(view, overridden))

        val joined = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedField
        assertEquals(metadata, joined.metadata)
        assertEquals(FieldAccessibility.UNKNOWN, joined.accessibility)
    }

    private fun spineView(descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView("Lspine/spine;", descriptor)

    /** A returning node at [address] (default 0) whose frame is exactly what the caller built. */
    private fun returnedNodeAt(view: MethodView, frame: Frame, address: Int = 0): ExecutionNode {
        val node = ExecutionNode(
            address = address,
            op = OpBuilder.build(address, view, CoreTester.classIndex),
            frame = frame,
            parent = null,
            activation = Activation.entry(view),
        )
        node.endWith(Outcome.Returned(null))
        return node
    }

    /** A `Complete` graph piling every one of [nodes] at its own address -- callers supply only returning nodes. */
    private fun completeGraphOf(view: MethodView, vararg nodes: ExecutionNode): ExecutionGraph =
        ExecutionGraph(
            methodView = view,
            addressToNodes = nodes.groupBy { it.address },
            outcomes = nodes.map { it.outcome!! },
            completion = CompletionStatus.Complete,
            executedNodes = nodes.toSet(),
            superclasses = CoreTester.classIndex,
        )

    private companion object {
        private const val CLASS_NAME = "Lheap_consensus_test;"
    }
}
