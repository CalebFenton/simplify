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
        // Both paths carry the SAME HeapRef -- one allocation before the fork --
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

    @Test
    fun terminatingHeapObjectPreservesAGuestThrowablesIdentityAcrossAFork() {
        // One throwable exists before the fork; every returning path shares its heap object and host instance.
        val view = forkView()
        val type = VirtualType.of("Ljava/lang/ArithmeticException;")
        val seed = Frame.entryFrame(view)
        val sharedThrowable = ArithmeticException("/ by zero")
        val ref = seed.allocateThrowable(type, sharedThrowable).raw as HeapRef

        val graph = CoreTester.vmServices.newSession(view, entryFrame = seed).run()

        assertEquals(2, graph.outcomes.size)
        val agreed = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.GuestThrowable
        assertTrue(agreed.contents is HostGraphContents.Known)
        assertTrue(
            CoreTester.terminatingHostObject(graph, ref) === sharedThrowable,
            "a fork must preserve the SAME synthesized instance",
        )
    }

    @Test
    fun terminatingThrowableConsensusPreservesItsGuestCauseIdentity() {
        val view = forkView()
        val frame = Frame.entryFrame(view)
        val causeType = VirtualType.of("Ljava/lang/NullPointerException;")
        val wrapperType = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val cause = frame.allocateThrowable(causeType, NullPointerException("cause"))
        val wrapper = frame.allocateThrowable(
            wrapperType,
            ExceptionInInitializerError(frame.hostObjectAt(cause.raw as HeapRef) as Throwable),
            cause,
        )

        val graph = CoreTester.vmServices.newSession(view, entryFrame = frame).run()

        assertEquals(2, graph.outcomes.size)
        val consensus = graph.trusted()!!.terminatingHeapObject(wrapper.raw as HeapRef).agreed()
            as HeapObject.GuestThrowable
        assertEquals(cause, consensus.cause)
    }

    @Test
    fun objectMissingFromOneReturningPathMakesObjectConsensusUnavailable() {
        // Only one arm allocates: the paths disagree about whether the object exists, so no contents answer.
        val graph = CoreTester.execute(FORK_CLASS, "allocateOnOnePath(I)Ljava/lang/Object;")
        val allocated = graph.outcomes.mapNotNull {
            ((it as Outcome.Returned).value as? Value.Known)?.raw as? HeapRef
        }.single()
        val allocatingPath = graph.addresses().asSequence()
            .flatMap { graph.nodePile(it).asSequence() }
            .single { node -> ((node.outcome as? Outcome.Returned)?.value as? Value.Known)?.raw == allocated }
        assertNotNull(allocatingPath.terminalFrame.heapObject(allocated))

        assertEquals(
            Agreement.Absent,
            graph.trusted()!!.terminatingHeapObject(allocated),
            "one returning path lacks the object, so no object-content consensus is available",
        )
    }

    @Test
    fun aReflectedFieldWhoseAccessibilityDiffersAcrossReturningPathsJoinsToUnknownAccessibility() {
        // One Field object, made accessible on one returning path only. The executable heap merge never sees
        // this pair (callers partition by accessibility first); the terminating query joins it, and the join of
        // two definite accessibilities is UNKNOWN, never either path's own.
        val view = CoreTester.classIndex.requireMethodView(FORK_CLASS, "setAccessibleOnOnePath(ILjava/lang/reflect/Field;)V")
        val owner = VirtualType.of("Lheap_consensus/owner;")
        val metadata = TrackedFieldValue(
            owner, "secret", "${owner.descriptor}->secret:I", INT, java.lang.reflect.Modifier.PRIVATE,
            FieldVisibility.PRIVATE, FieldStorage.INSTANCE, TrackedFieldDomain.Virtual,
        )
        val seed = Frame.entryFrame(view)
        val field = seed.allocateTrackedField(metadata)
        seed.assign(view.registerCount - 1, field)
        val ref = field.raw as HeapRef

        val graph = CoreTester.vmServices.newSession(view, entryFrame = seed).run()

        assertEquals(2, graph.outcomes.size)
        val joined = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.TrackedField
        assertEquals(metadata, joined.metadata)
        assertEquals(FieldAccessibility.UNKNOWN, joined.accessibility)
    }

    private fun forkView(): MethodView = CoreTester.classIndex.requireMethodView(FORK_CLASS, "forkThenReturn(I)V")

    private companion object {
        private const val CLASS_NAME = "Lheap_consensus_test;"
        private const val FORK_CLASS = "Lheap_consensus_fork;"
    }
}
