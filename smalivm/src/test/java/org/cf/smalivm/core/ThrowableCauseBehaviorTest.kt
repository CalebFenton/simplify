package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Plan 12 cause-root, merge, host-materialization, and EX-10 host-import contracts. */
class ThrowableCauseBehaviorTest {
    @Test
    fun unknownThrowableGetCauseReturnsUnknown() {
        val graph = CoreTester.execute(
            "Lthrowable_cause_test;",
            "getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;",
        )

        assertEquals(
            listOf(Outcome.Returned(Value.Unknown(VirtualType.of("Ljava/lang/Throwable;")))),
            graph.outcomes,
        )
    }

    @Test
    fun guestGetCauseReturnsTheExactCauseIdentityAfterPersistentCopies() {
        val view = CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;",
        "getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;",)
        val frame = Frame.entryFrame(view)
        val causeType = VirtualType.of("Ljava/lang/NullPointerException;")
        val wrapperType = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val causeHost = NullPointerException("cause")
        val cause = frame.allocateThrowable(causeType, causeHost)
        val wrapperHost = ExceptionInInitializerError(causeHost)
        val wrapper = frame.allocateThrowable(wrapperType, wrapperHost, cause)
        frame.assign(view.parameters.single().register, wrapper)

        val graph = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame.copy()).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(cause)), graph.outcomes)
        assertSame(causeHost, wrapperHost.cause, "the host mirror must carry the semantic guest cause")
        assertTrue(frame.isHostMaterializable(wrapper), "a known cause chain has one complete host form")
    }

    @Test
    fun throwableCauseRemainsAGuestRootForSelectionAndOpaqueReachability() {
        val view = view("getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;")
        val frame = Frame.entryFrame(view)
        val cause = frame.allocateThrowable(NPE, NullPointerException("cause"))
        val wrapper = frame.allocateThrowable(
            EIIE,
            ExceptionInInitializerError(frame.hostObjectAt(cause.raw as HeapRef) as Throwable),
            cause,
        )
        frame.assign(view.parameters.single().register, wrapper)

        val checkpoint = frame.detachHostGraph(emptyList())
        val detachedWrapper = frame.hostObjectAt(wrapper.raw as HeapRef) as Throwable
        val detachedCause = frame.hostObjectAt(cause.raw as HeapRef)
        assertTrue(detachedCause is NullPointerException, "selection must keep the guest cause's host mirror")
        assertSame(detachedCause, detachedWrapper.cause, "the detached graph must keep the wrapper-to-cause edge")
        frame.restoreHostGraph(checkpoint)

        frame.collapseReachable(listOf(wrapper), listOf(THROWABLE))
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(wrapper.raw as HeapRef) as HeapObject.GuestThrowable).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(cause.raw as HeapRef) as HeapObject.GuestThrowable).contents)
    }

    @Test
    fun throwableMergeTreatsMissingAndDifferentCausesConservatively() {
        val base = Frame.entryFrame(view("getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;"))
        val cause = base.allocateThrowable(NPE, NullPointerException("cause"))
        val host = ExceptionInInitializerError(base.hostObjectAt(cause.raw as HeapRef) as Throwable)
        val wrapper = base.allocateThrowable(EIIE, host)
        val modeled = base.copy().also { it.setThrowableCause(wrapper.raw as HeapRef, cause) }

        val missing = base.mergedCalleeState(listOf(base, modeled), CoreTester.classIndex)
        assertEquals(Value.Top, (missing.heapObject(wrapper.raw as HeapRef) as HeapObject.GuestThrowable).cause)

        val first = independentCauseFrame(causeFirst = true)
        val second = independentCauseFrame(causeFirst = false)
        val merged = first.frame.mergedCalleeState(listOf(first.frame, second.frame), CoreTester.classIndex)
        val mergedWrapper = merged.heapObject(first.wrapperRef) as HeapObject.GuestThrowable
        assertEquals(Value.Unknown(NPE), mergedWrapper.cause)
    }

    @Test
    fun unknownModeledCauseDoesNotBecomeGuestNullFromTheHostMirror() {
        val view = CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;",
        "getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;",)
        val frame = Frame.entryFrame(view)
        val host = IllegalStateException("outer", IllegalArgumentException("host-only cause"))
        val throwable = frame.allocateThrowable(VirtualType.of("Ljava/lang/IllegalStateException;"), host)
        frame.assign(view.parameters.single().register, throwable)

        val graph = VmServices(CoreTester.classIndex).newSession(view, entryFrame = frame).run()

        assertEquals(
            listOf(Outcome.Returned(Value.Unknown(VirtualType.of("Ljava/lang/Throwable;")))),
            graph.outcomes,
        )
    }

    @Test
    fun reflectedNestedCauseSurvivesCatchGetCauseAndRethrow() {
        val target = MethodSignature(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwNested",
            "()V",
        )
        val vm = VmServices(
            CoreTester.classIndex,
            ReflectionPolicy(exactSafeMethods = setOf(target)),
        )

        val session = vm.newSession(
            CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;",
            "catchHostCauseAndRethrow()V",),
        )
        val graph = session.run()
        val thrown = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        val causeRef = thrown.raw as HeapRef
        val terminal = requireNotNull(session.currentNode).terminalFrame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(VirtualType.of("Ljava/lang/IllegalArgumentException;"), terminal.heapObject(causeRef)?.type)
        assertEquals("cause", (terminal.hostObjectAt(causeRef) as Throwable).message)
        assertEquals(Value.Known(null, VirtualType.of("Ljava/lang/Throwable;")), terminal.throwableCause(causeRef))
    }

    @Test
    fun reflectedCauseCycleImportsEachIdentityOnceAndPreservesTheAlias() {
        val target = MethodSignature(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwCauseCycle",
            "()V",
        )
        val vm = VmServices(
            CoreTester.classIndex,
            ReflectionPolicy(exactSafeMethods = setOf(target)),
        )
        val view = CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;",
        "catchHostCauseCycle()Ljava/lang/Throwable;",)
        val session = vm.newSession(view)

        val graph = session.run()
        val outcome = graph.outcomes.single()
        assertTrue(outcome is Outcome.Returned, "expected the cycle root to return, got $outcome")
        val returned = (outcome as Outcome.Returned).value as Value.Known
        val outerRef = returned.raw as HeapRef
        val outer = graph.explored.terminatingHeapObject(outerRef).agreed() as HeapObject.GuestThrowable
        val cause = outer.cause as Value.Known
        val causeRef = cause.raw as HeapRef
        val causeObject = graph.explored.terminatingHeapObject(causeRef).agreed() as HeapObject.GuestThrowable
        val outerHost = CoreTester.terminatingHostObject(graph, outerRef) as Throwable
        val causeHost = CoreTester.terminatingHostObject(graph, causeRef) as Throwable

        assertNotEquals(outerRef, causeRef)
        assertEquals(Value.Known(outerRef, outer.type), causeObject.cause)
        assertEquals(2, setOf(outerRef, causeRef).size, "the two-node cycle must keep two throwable refs")
        assertSame(causeHost, outerHost.cause)
        assertSame(outerHost, causeHost.cause)
    }

    @Test
    fun selectedHostThrowableImportReusesTheRootAndPreservesNestedAliasedCycle() {
        val target = MethodSignature(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwProvided",
            "(Ljava/lang/Throwable;)V",
        )
        val vm = VmServices(CoreTester.classIndex, ReflectionPolicy(exactSafeMethods = setOf(target)))
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
        frame.setThrowableCause(causeRef, root)
        frame.assign(view.parameters.single().register, root)
        val probeBefore = probeNextHeapId(frame)

        val session = vm.newSession(view, entryFrame = frame)
        val graph = session.run()

        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val terminalNode = requireNotNull(session.currentNode)
        val terminal = terminalNode.terminalFrame
        assertEquals(rootRef, returned.raw, "the selected throw must reuse its source throwable ref")
        assertEquals(causeValue, terminal.throwableCause(rootRef), "the nested cause must reuse its source ref")
        assertEquals(root, terminal.throwableCause(causeRef), "the imported cause cycle must close on the source ref")
        assertEquals(
            probeBefore + 1,
            probeNextHeapId(terminal),
            "the cycle import must allocate no duplicate refs: only the before-probe consumed an id",
        )
        val importedOuter = terminal.hostObjectAt(rootRef) as LinkedCauseThrowable
        val importedCause = terminal.hostObjectAt(causeRef) as LinkedCauseThrowable
        assertSame(importedCause, importedOuter.cause, "the selected graph must preserve the nested alias")
        assertSame(importedOuter, importedCause.cause, "the selected graph must preserve the cycle")
        assertEquals(rootRef, terminal.refForHostNode(requireNotNull(terminal.hostNodeId(importedOuter))))
        assertEquals(causeRef, terminal.refForHostNode(requireNotNull(terminal.hostNodeId(importedCause))))
        assertTrue(terminal.isHostMaterializable(returned))
    }

    @Test
    fun normallyReturnedHostThrowableImportsItsCauseIdentityAndNullTerminator() {
        val target = MethodSignature(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "returnNestedThrowable",
            "()Ljava/lang/Throwable;",
        )
        val vm = VmServices(CoreTester.classIndex, ReflectionPolicy(exactSafeMethods = setOf(target)))
        val session = vm.newSession(view("returnedThrowable()Ljava/lang/Throwable;"))

        val graph = session.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val rootRef = returned.raw as HeapRef
        val root = graph.explored.terminatingHeapObject(rootRef).agreed() as HeapObject.GuestThrowable
        val cause = requireNotNull(root.cause) as Value.Known
        val causeRef = cause.raw as HeapRef
        val causeObject = graph.explored.terminatingHeapObject(causeRef).agreed() as HeapObject.GuestThrowable
        val rootHost = CoreTester.terminatingHostObject(graph, rootRef) as Throwable
        val causeHost = CoreTester.terminatingHostObject(graph, causeRef) as Throwable
        val terminalNode = requireNotNull(session.currentNode)
        val terminal = terminalNode.terminalFrame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNotEquals(rootRef, causeRef)
        assertEquals(Value.Known(null, THROWABLE), causeObject.cause)
        assertSame(causeHost, rootHost.cause)
        assertTrue(terminal.isHostMaterializable(returned))
    }

    private fun independentCauseFrame(causeFirst: Boolean): IndependentCauseFrame {
        val frame = Frame.entryFrame(view("getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;"))
        val first = frame.allocateThrowable(NPE, NullPointerException("first"))
        val second = frame.allocateThrowable(NPE, NullPointerException("second"))
        val cause = if (causeFirst) first else second
        val wrapper = frame.allocateThrowable(
            EIIE,
            ExceptionInInitializerError(frame.hostObjectAt(cause.raw as HeapRef) as Throwable),
            cause,
        )
        return IndependentCauseFrame(frame, wrapper.raw as HeapRef)
    }

    private data class IndependentCauseFrame(val frame: Frame, val wrapperRef: HeapRef)

    private fun view(descriptor: String): MethodView = CoreTester.classIndex.requireMethodView("Lthrowable_cause_test;", descriptor)

    private class LinkedCauseThrowable(message: String) : RuntimeException(message) {
        var linkedCause: Throwable? = null
        override val cause: Throwable? get() = linkedCause
    }

    private companion object {
        val THROWABLE = VirtualType.of("Ljava/lang/Throwable;")
        val NPE = VirtualType.of("Ljava/lang/NullPointerException;")
        val EIIE = VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val LINKED_CAUSE = VirtualType.of("Lorg/cf/smalivm/core/ThrowableCauseBehaviorTest\$LinkedCauseThrowable;")
    }
}
