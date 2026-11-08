package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * A guest throwable's cause: it is a guest heap root, it merges conservatively, and a host throwable imported
 * through reflection keeps its cause identities, aliases, and cycles.
 */
class ThrowableCauseBehaviorTest {
    @TestFactory
    fun getCauseOfAnUnmodelledCauseReturnsUnknown(): List<DynamicTest> = listOf<Pair<String, (Frame, MethodView) -> Unit>>(
        "unseeded parameter" to { _, _ -> },
        // The host mirror has a cause the guest never modelled; it must not read as a guest null.
        "host-only cause" to { frame, view ->
            val host = IllegalStateException("outer", IllegalArgumentException("host-only cause"))
            val throwable = frame.allocateThrowable(VirtualType.of("Ljava/lang/IllegalStateException;"), host)
            frame.assign(view.parameters.single().register, throwable)
        },
    ).map { (name, seed) ->
        DynamicTest.dynamicTest(name) {
            val view = view(GET_CAUSE)
            val frame = Frame.entryFrame(view).also { seed(it, view) }
            val graph = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame).run()
            assertEquals(listOf(Outcome.Returned(Value.Unknown(THROWABLE))), graph.outcomes)
        }
    }

    @Test
    fun guestGetCauseReturnsTheExactCauseIdentityAfterPersistentCopies() {
        val view = view(GET_CAUSE)
        val frame = Frame.entryFrame(view)
        val causeHost = NullPointerException("cause")
        val cause = frame.allocateThrowable(NPE, causeHost)
        val wrapper = frame.allocateThrowable(EIIE, ExceptionInInitializerError(causeHost), cause)
        frame.assign(view.parameters.single().register, wrapper)

        val graph = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame.copy()).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(cause)), graph.outcomes)
        assertTrue(frame.isHostMaterializable(wrapper), "a known cause chain has one complete host form")
    }

    @Test
    fun throwableCauseRemainsAGuestRootForSelectionAndOpaqueReachability() {
        val view = view(GET_CAUSE)
        val frame = Frame.entryFrame(view)
        val cause = frame.allocateThrowable(NPE, NullPointerException("cause"))
        val wrapper = frame.allocateThrowable(
            EIIE,
            ExceptionInInitializerError(frame.heap.hostObjectAt(cause.raw as HeapRef) as Throwable),
            cause,
        )
        frame.assign(view.parameters.single().register, wrapper)

        val checkpoint = frame.detachHostGraph(emptyList())
        val detachedWrapper = frame.heap.hostObjectAt(wrapper.raw as HeapRef) as Throwable
        val detachedCause = frame.heap.hostObjectAt(cause.raw as HeapRef)
        assertTrue(detachedCause is NullPointerException, "selection must keep the guest cause's host mirror")
        assertSame(detachedCause, detachedWrapper.cause, "the detached graph must keep the wrapper-to-cause edge")
        frame.heap.restoreHostGraph(checkpoint)

        frame.heap.collapseReachable(listOf(wrapper), listOf(THROWABLE))
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(wrapper.raw as HeapRef) as HeapObject.GuestThrowable).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(cause.raw as HeapRef) as HeapObject.GuestThrowable).contents)
    }

    @Test
    fun throwableMergeTreatsMissingAndDifferentCausesConservatively() {
        val base = Frame.entryFrame(view(GET_CAUSE))
        val cause = base.allocateThrowable(NPE, NullPointerException("cause"))
        val host = ExceptionInInitializerError(base.heap.hostObjectAt(cause.raw as HeapRef) as Throwable)
        val wrapper = base.allocateThrowable(EIIE, host)
        val modeled = base.copy().also { it.heap.setThrowableCause(wrapper.raw as HeapRef, cause) }

        val missing = base.mergedCalleeState(listOf(base, modeled), CoreTester.classIndex)
        assertEquals(Value.Top, (missing.heapObject(wrapper.raw as HeapRef) as HeapObject.GuestThrowable).cause)

        // One throwable whose cause differs by path joins to a cause neither path can name.
        val first = base.allocateThrowable(NPE, NullPointerException("first"))
        val second = base.allocateThrowable(NPE, NullPointerException("second"))
        val mergedWrapper = HeapStore.mergeObject(
            listOf(
                HeapObject.GuestThrowable(EIIE, HostGraphContents.Unknown, first),
                HeapObject.GuestThrowable(EIIE, HostGraphContents.Unknown, second),
            ),
            CoreTester.classIndex,
            sameGraph = true,
        ) as HeapObject.GuestThrowable
        assertEquals(Value.Unknown(NPE), mergedWrapper.cause)
    }

    @Test
    fun reflectedNestedCauseSurvivesCatchGetCauseAndRethrow() {
        val session = reflectingVm("throwNested", "()V").newSession(view("catchHostCauseAndRethrow()V"))
        val graph = session.run()
        val thrown = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        val causeRef = thrown.raw as HeapRef
        val terminal = requireNotNull(session.currentNode).terminalFrame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(VirtualType.of("Ljava/lang/IllegalArgumentException;"), terminal.heapObject(causeRef)?.type)
        assertEquals("cause", (terminal.heap.hostObjectAt(causeRef) as Throwable).message)
        assertEquals(Value.Known(null, THROWABLE), terminal.throwableCause(causeRef))
    }

    @Test
    fun reflectedCauseCycleImportsEachIdentityOnceAndPreservesTheAlias() {
        val graph = reflectingVm("throwCauseCycle", "()V")
            .newSession(view("catchHostCauseCycle()Ljava/lang/Throwable;"))
            .run()
        val outcome = graph.outcomes.single()
        assertTrue(outcome is Outcome.Returned, "expected the cycle root to return, got $outcome")
        val returned = (outcome as Outcome.Returned).value as Value.Known
        val outerRef = returned.raw as HeapRef
        val outer = graph.explored.terminatingHeapObject(outerRef).agreed() as HeapObject.GuestThrowable
        val causeRef = (outer.cause as Value.Known).raw as HeapRef
        val causeObject = graph.explored.terminatingHeapObject(causeRef).agreed() as HeapObject.GuestThrowable
        val outerHost = CoreTester.terminatingHostObject(graph, outerRef) as Throwable
        val causeHost = CoreTester.terminatingHostObject(graph, causeRef) as Throwable

        assertNotEquals(outerRef, causeRef, "the two-node cycle must keep two throwable refs")
        assertEquals(Value.Known(outerRef, outer.type), causeObject.cause)
        assertSame(causeHost, outerHost.cause)
        assertSame(outerHost, causeHost.cause)
    }

    @Test
    fun selectedHostThrowableImportReusesTheRootAndPreservesNestedAliasedCycle() {
        val vm = reflectingVm("throwProvided", "(Ljava/lang/Throwable;)V")
        val view = view("catchProvided(Ljava/lang/Throwable;)Ljava/lang/Throwable;")
        val frame = Frame.entryFrame(view)
        val outer = LinkedCauseThrowable("outer")
        val cause = LinkedCauseThrowable("cause")
        outer.linkedCause = cause
        cause.linkedCause = outer
        val causeValue = frame.allocateThrowable(LINKED_CAUSE, cause)
        val causeRef = causeValue.raw as HeapRef
        val root = frame.allocateThrowable(LINKED_CAUSE, outer, causeValue)
        val rootRef = root.raw as HeapRef
        frame.heap.setThrowableCause(causeRef, root)
        frame.assign(view.parameters.single().register, root)
        val probeBefore = probeNextHeapId(frame)

        val session = vm.newSession(view, entryFrame = frame)
        val graph = session.run()

        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val terminal = requireNotNull(session.currentNode).terminalFrame
        assertEquals(rootRef, returned.raw, "the selected throw must reuse its source throwable ref")
        assertEquals(causeValue, terminal.throwableCause(rootRef), "the nested cause must reuse its source ref")
        assertEquals(root, terminal.throwableCause(causeRef), "the imported cause cycle must close on the source ref")
        assertEquals(
            probeBefore + 1,
            probeNextHeapId(terminal),
            "the cycle import must allocate no duplicate refs: only the before-probe consumed an id",
        )
        val importedOuter = terminal.heap.hostObjectAt(rootRef) as LinkedCauseThrowable
        val importedCause = terminal.heap.hostObjectAt(causeRef) as LinkedCauseThrowable
        assertSame(importedCause, importedOuter.cause, "the selected graph must preserve the nested alias")
        assertSame(importedOuter, importedCause.cause, "the selected graph must preserve the cycle")
        assertEquals(rootRef, terminal.heap.refForNode(requireNotNull(terminal.heap.hostNodeId(importedOuter))))
        assertEquals(causeRef, terminal.heap.refForNode(requireNotNull(terminal.heap.hostNodeId(importedCause))))
        assertTrue(terminal.isHostMaterializable(returned))
    }

    @Test
    fun normallyReturnedHostThrowableImportsItsCauseIdentityAndNullTerminator() {
        val session = reflectingVm("returnNestedThrowable", "()Ljava/lang/Throwable;")
            .newSession(view("returnedThrowable()Ljava/lang/Throwable;"))

        val graph = session.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val rootRef = returned.raw as HeapRef
        val root = graph.explored.terminatingHeapObject(rootRef).agreed() as HeapObject.GuestThrowable
        val causeRef = (requireNotNull(root.cause) as Value.Known).raw as HeapRef
        val causeObject = graph.explored.terminatingHeapObject(causeRef).agreed() as HeapObject.GuestThrowable
        val rootHost = CoreTester.terminatingHostObject(graph, rootRef) as Throwable
        val causeHost = CoreTester.terminatingHostObject(graph, causeRef) as Throwable
        val terminal = requireNotNull(session.currentNode).terminalFrame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNotEquals(rootRef, causeRef)
        assertEquals(Value.Known(null, THROWABLE), causeObject.cause)
        assertSame(causeHost, rootHost.cause)
        assertTrue(terminal.isHostMaterializable(returned))
    }

    private fun view(descriptor: String): MethodView = CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;", descriptor)

    /** A VM whose reflection policy allows exactly one `ReflectBackendFixture` method. */
    private fun reflectingVm(name: String, descriptor: String): VmServices {
        val target = MethodSignature(VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"), name, descriptor)
        return VmServices(CoreTester.classIndex, ReflectionPolicy(exactSafeMethods = setOf(target)))
    }

    private class LinkedCauseThrowable(message: String) : RuntimeException(message) {
        var linkedCause: Throwable? = null
        override val cause: Throwable? get() = linkedCause
    }

    private companion object {
        const val GET_CAUSE = "getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;"
        val THROWABLE = VirtualType.of("Ljava/lang/Throwable;")
        val NPE = VirtualType.of("Ljava/lang/NullPointerException;")
        val EIIE = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val LINKED_CAUSE = VirtualType.of("Lorg/cf/smalivm/core/ThrowableCauseBehaviorTest\$LinkedCauseThrowable;")
    }
}
