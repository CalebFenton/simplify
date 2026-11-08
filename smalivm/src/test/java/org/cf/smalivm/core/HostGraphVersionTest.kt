package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Versioning of the host object graph across guest mirror writes, forks, merges and selected-call
 * finalization.
 */
internal class HostGraphVersionTest : DirectHostTestBase() {
    @Test
    fun mirroredGuestWriteAfterForkDetachesOnlyTheWritingPath() {
        val parent = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val raw = intArrayOf(1)
        val admitted = parent.heap.admitHostSeeds(listOf(HostSeed(Value.Known(raw, intArrayType)))).single() as Value.Known
        val ref = admitted.raw as HeapRef
        val sibling = parent.copy()
        val writing = parent.copy()

        writing.heap.setArrayElement(ref, 0, Value.Known(7, intType))

        assertSame(parent.hostGraphVersionToken(), sibling.hostGraphVersionToken())
        assertSame(parent.hostGraphVersionToken(), writing.hostGraphVersionToken())
        assertEquals(Value.Known(1, intType), parent.heap.arrayAt(ref)!!.element(0))
        assertEquals(Value.Known(1, intType), sibling.heap.arrayAt(ref)!!.element(0))
        assertEquals(Value.Known(7, intType), writing.heap.arrayAt(ref)!!.element(0))
        assertEquals(1, (parent.heap.hostObjectAt(ref) as IntArray)[0])
        assertEquals(1, (sibling.heap.hostObjectAt(ref) as IntArray)[0])
        assertEquals(1, (writing.heap.hostObjectAt(ref) as IntArray)[0], "the mirror stays stale until selected detach")
        assertTrue(writing.heap.arrayAt(ref)!!.mirrorDirty)
    }

    @Test
    fun unrepresentableMirroredGuestWriteInvalidatesMirrorButKeepsAuthoritativeLatticeWrite() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val value = frame.heap.admitHostSeeds(listOf(HostSeed(Value.Known(intArrayOf(1), intArrayType)))).single()
            as Value.Known
        val ref = value.raw as HeapRef

        frame.heap.setArrayElement(ref, 0, Value.Unknown(intType))

        assertEquals(Value.Unknown(intType), frame.heap.arrayAt(ref)!!.element(0))
        assertTrue(frame.heap.arrayAt(ref)!!.contents is HostGraphContents.Known)
        assertTrue(frame.heap.arrayAt(ref)!!.mirrorDirty)
        assertEquals(1, (frame.heap.hostObjectAt(ref) as IntArray)[0])
        assertThrows<HostGraphRepresentationException> {
            SelectedHostCall.from(
                prepared(listOf(intArrayType), listOf(value)),
                frame,
                CoreTester.classIndex.classLoader,
            )
        }
        assertThrows<IllegalArgumentException> {
            frame.heap.setArrayElement(ref, 2, Value.Known(3, intType))
        }
    }

    @Test
    fun observingANonEmptyHostGraphCreatesNoVersions() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val ref = frame.heap.allocateHostObject(builderType, StringBuilder("observed"))
        val graphVersion = frame.hostGraphVersionToken()
        val objectVersion = frame.heapObject(ref)
        val raw = frame.heap.hostObjectAt(ref)

        repeat(100) {
            frame.heapObject(ref)
            frame.heap.hostObjectAt(ref)
            frame.heap.hostNodeId(frame.heap.hostObjectAt(ref)!!)
            frame.toString()
        }

        assertSame(graphVersion, frame.hostGraphVersionToken())
        assertSame(objectVersion, frame.heapObject(ref))
        assertSame(raw, frame.heap.hostObjectAt(ref))
    }

    @Test
    fun unchangedSelectedCallDiscardsItsCloneAndRestoresTheOriginalGraphVersion() {
        val seed = StringBuilder("same")
        val view = hostView("recordReceived(Ljava/lang/Object;)V")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(builderType, seed)
        frame.assign(0, Value.Known(ref, builderType))
        val session = hostVm.newSession(view, entryFrame = frame)
        val before = session.frontier.single().frame.hostGraphVersionToken()

        val graph = session.run()
        val after = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame

        assertNotSame(seed, DirectHostCallsFixture.received, "the backend runs on a detached clone")
        assertSame(before, after.hostGraphVersionToken())
    }

    @Test
    fun selectedCallFinalizationIsOneShotAndOwnedByItsFrame() {
        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(builderType, StringBuilder("owned"))
        val selected = SelectedHostCall.from(
            prepared(listOf(builderType), listOf(Value.Known(ref, builderType))),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val wrongFrame = frame.copy()

        assertThrows<IllegalStateException> { selected.finalize(wrongFrame) }
        selected.finalize(frame)
        assertThrows<IllegalStateException> { selected.finalize(frame) }
    }

    @Test
    fun changedSelectedCallRetainsTheDetachedGraphVersion() {
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            null,
            builderType,
            StringBuilder("value"),
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }
        val before = session.frontier.single().frame.hostGraphVersionToken()

        val graph = session.run()
        assertEquals(
            CompletionStatus.Complete,
            graph.completion(),
            "comparison failure must not become host failure: ${graph.nodePile(0).single().outcome.hostFailure}",
        )
        val returned = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame

        assertNotSame(before, returned.hostGraphVersionToken())
        assertEquals("value1", returned.heap.hostObjectAt(ref).toString(), "StringBuilder.append(J) appends the decimal long")
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "append changed the builder")
    }

    @Test
    fun unprovableSelectedGraphRetainsTheDetachedVersionWithoutHostFailure() {
        val atomicType = VirtualType.of("Ljava/util/concurrent/atomic/AtomicInteger;")
        val view = hostView("ignore(Ljava/lang/Object;)V")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(atomicType, java.util.concurrent.atomic.AtomicInteger(0))
        frame.assign(0, Value.Known(ref, objectType))
        val session = hostVm.newSession(view, entryFrame = frame)
        val before = session.frontier.single().frame.hostGraphVersionToken()

        val graph = session.run()
        assertEquals(
            CompletionStatus.Complete,
            graph.completion(),
            "comparison failure must not become host failure: ${graph.nodePile(0).single().outcome.hostFailure}",
        )
        val returned = requireNotNull(session.currentNode).frame

        assertNotSame(before, returned.hostGraphVersionToken())
        assertTrue(returned.heapObject(ref) is HeapObject.HostObject)
    }

    @Test
    fun calleeMergeKeepsSameGraphKnownAndDegradesDifferentGraphsToUnknownContents() {
        val caller = Frame.entryFrame(view("Lcalls/caller;", "callsVoidCallee()I"))
        val ref = caller.heap.allocateHostObject(builderType, StringBuilder("value"))
        val calleeView = view("Lcalls/callee;", "returnsNothing()V")
        val first = caller.calleeEntryFrame(calleeView)
        val second = caller.calleeEntryFrame(calleeView)

        val same = caller.mergedCalleeState(listOf(first, second), CoreTester.classIndex)
        assertTrue((same.heapObject(ref) as HeapObject.HostObject).contents is HostGraphContents.Known)
        assertEquals("value", same.heap.hostObjectAt(ref).toString())

        val selected = SelectedHostCall.from(
            prepared(listOf(builderType), listOf(Value.Known(ref, builderType))),
            first,
            CoreTester.classIndex.classLoader,
        )
        (selected.arguments.single() as StringBuilder).append("-first")
        selected.finalize(first)
        val changed = caller.mergedCalleeState(listOf(first, second), CoreTester.classIndex)

        assertEquals(HostGraphContents.Unknown, (changed.heapObject(ref) as HeapObject.HostObject).contents)
        assertEquals(builderType, changed.heapObject(ref)!!.type)
        assertEquals(null, changed.heap.hostObjectAt(ref))
    }

    @Test
    fun guestMirrorWritesDeferOneGraphDetachAndSynchronizeEveryAlias() {
        // Bug class: every guest array write clones the complete host graph to update its Java mirror.
        // Oracle: lattice writes only mark the mirror dirty; one selected detach synchronizes all aliases once.
        val rawArray = intArrayOf(0)
        val holder = MirrorHolder(rawArray, Array(9_997) { MutableBox(it) })
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val holderValue = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(holder, objectType))),
        ).single() as Value.Known
        val holderRef = holderValue.raw as HeapRef
        val arrayRef = requireNotNull(frame.heap.findHostObject(rawArray))
        val sibling = frame.copy()
        val graphVersion = frame.hostGraphVersionToken()

        repeat(100) { value -> assertTrue(frame.heap.setArrayElement(arrayRef, 0, Value.Known(value, intType))) }

        assertSame(graphVersion, frame.hostGraphVersionToken(), "lattice writes must not clone or replace the graph")
        assertSame(holder, frame.heap.hostObjectAt(holderRef))
        assertTrue(frame.heap.arrayAt(arrayRef)!!.mirrorDirty)
        assertEquals(Value.Known(99, intType), frame.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Known(0, intType), sibling.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(0, rawArray[0])
        assertEquals(10_000, frame.heap.hostGraphObjects().size)

        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectType, intArrayType),
                listOf(holderValue, Value.Known(arrayRef, intArrayType)),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val selectedHolder = selected.arguments[0] as MirrorHolder
        val selectedArray = selected.arguments[1] as IntArray

        assertNotSame(holder, selectedHolder, "one selected detach runs the backend on a clone")
        assertSame(selectedHolder.array, selectedArray)
        assertEquals(99, selectedArray[0])
        assertFalse(frame.heap.arrayAt(arrayRef)!!.mirrorDirty)
        selected.finalize(frame)
        assertEquals(99, (frame.heap.hostObjectAt(holderRef) as MirrorHolder).array[0])
        assertEquals(0, (sibling.heap.hostObjectAt(holderRef) as MirrorHolder).array[0])
    }
}
