package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * How objects returned by a selected host call map back to HeapRefs, and how finalization prunes
 * unreachable graph nodes.
 */
class HostReturnAdmissionTest : DirectHostTestBase() {
    @Test
    fun selectedMutationPrunesAnUnownedChildButKeepsAChildWithItsOwnHeapRef() {
        // Bug class: refresh retains every historical node even after no VM-owned root can reach it.
        // Oracle: graph membership is reachability from known HeapRefs; a separately owned child remains a root.
        val unownedChild = MutableChild(1)
        val unownedParent = MutableParent(unownedChild)
        val unownedFrame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val unownedValue = unownedFrame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(unownedParent, objectType))),
        ).single() as Value.Known
        val unownedCall = SelectedHostCall.from(
            prepared(listOf(objectType), listOf(unownedValue)),
            unownedFrame,
            CoreTester.classIndex.classLoader,
        )
        (unownedCall.arguments.single() as MutableParent).child = null

        assertEquals(HostGraphComparison.Different, unownedCall.finalize(unownedFrame))
        assertEquals(1, unownedFrame.heap.hostGraphObjects().size)
        assertEquals(null, unownedFrame.heap.hostNodeId(unownedChild))

        val ownedChild = MutableChild(2)
        val ownedParent = MutableParent(ownedChild)
        val ownedFrame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val owned = ownedFrame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(ownedParent, objectType)),
                HostSeed(Value.Known(ownedChild, objectType)),
            ),
        ).map { it as Value.Known }
        val parentRef = owned[0].raw as HeapRef
        val childRef = owned[1].raw as HeapRef
        val ownedCall = SelectedHostCall.from(
            prepared(listOf(objectType, objectType), listOf(owned[0], owned[1])),
            ownedFrame,
            CoreTester.classIndex.classLoader,
        )
        (ownedCall.arguments.first() as MutableParent).child = null

        assertEquals(HostGraphComparison.Different, ownedCall.finalize(ownedFrame))
        assertEquals(2, ownedFrame.heap.hostGraphObjects().size)
        assertEquals(parentRef, ownedFrame.heap.findHostObject(ownedFrame.heap.hostObjectAt(parentRef)!!))
        assertEquals(childRef, ownedFrame.heap.findHostObject(ownedFrame.heap.hostObjectAt(childRef)!!))
    }

    @Test
    fun repeatedSelectedReplacementKeepsTheGraphBounded() {
        // Bug class: each selected mutation leaves the replaced child in the graph, growing every later clone.
        // Oracle: one parent and its current child are the only reachable nodes after every replacement.
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val admitted = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(MutableParent(MutableChild(0)), objectType))),
        ).single() as Value.Known
        val ref = admitted.raw as HeapRef

        repeat(4) { value ->
            val selected = SelectedHostCall.from(
                prepared(listOf(objectType), listOf(Value.Known(ref, objectType))),
                frame,
                CoreTester.classIndex.classLoader,
            )
            (selected.arguments.single() as MutableParent).child = MutableChild(value + 1)
            assertEquals(HostGraphComparison.Different, selected.finalize(frame))
            assertEquals(2, frame.heap.hostGraphObjects().size)
            assertEquals(ref, frame.heap.findHostObject(frame.heap.hostObjectAt(ref)!!))
        }
    }

    @Test
    fun returnedChildPrunedDuringFinalizationIsAdmittedAfterwardWithANewRef() {
        // Bug class: compaction drops an unowned returned child, then stale node-id import aborts the path.
        // Oracle: the result itself becomes a new VM root after finalization; the former parent ref stays stable.
        val view = hostView("detachChild(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val parentRef = frame.heap.allocateHostObject(objectType, MutableParent(MutableChild(9)))
        frame.assign(0, Value.Known(parentRef, objectType))
        val run = hostVm.newSession(view, entryFrame = frame)
        val graph = run.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val returnedRef = returned.raw as HeapRef
        val terminal = requireNotNull(graph.trusted()).terminatingHeapObject(parentRef).agreed() as HeapObject.HostObject

        assertNotEquals(parentRef, returnedRef)
        assertTrue(terminal.contents is HostGraphContents.Known)
        assertEquals(null, (CoreTester.terminatingHostObject(graph, parentRef) as MutableParent).child)
        assertEquals(9, (CoreTester.terminatingHostObject(graph, returnedRef) as MutableChild).value)
    }

    @Test
    fun removedOwnedChildReturnGetsANewRefAndLaterMaterializesExactly() {
        // Bug class: sourceHeapRef resurrects a pruned child at its stale ref with Unknown contents.
        // Oracle: removal ends the old root; the returned object becomes a new root at the next allocator id.
        val view = hostView("detachHolderChild(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val child = StringBuilder("returned")
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(HostChildHolder(child), objectType)),
                HostSeed(Value.Known(child, builderType)),
            ),
        ).map { it as Value.Known }
        val oldChildRef = admitted[1].raw as HeapRef
        // The probe consumes its id, so the run's next allocation receives the id after it.
        val expectedNewId = probeNextHeapId(frame) + 1
        frame.assign(0, admitted[0])

        val run = hostVm.newSession(view, entryFrame = frame)
        val graph = run.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val newRef = returned.raw as HeapRef
        val terminal = requireNotNull(graph.trusted())

        assertEquals(expectedNewId, newRef.id)
        assertNotEquals(oldChildRef, newRef)
        assertEquals(HostGraphContents.Unknown, (terminal.terminatingHeapObject(oldChildRef).agreed() as HeapObject.HostObject).contents)
        assertEquals("returned", CoreTester.terminatingHostObject(graph, newRef).toString())
        assertEquals(newRef.id + 1, probeNextHeapId(requireNotNull(run.currentNode).frame))

        val terminalFrame = requireNotNull(run.currentNode).frame
        val selected = SelectedHostCall.from(
            prepared(listOf(builderType), listOf(Value.Known(newRef, builderType))),
            terminalFrame,
            CoreTester.classIndex.classLoader,
        )
        assertEquals("returned", (selected.arguments.single() as StringBuilder).toString())
        selected.finalize(terminalFrame)
    }

    @Test
    fun removedOwnedThrowableUsesANewThrowableRef() {
        // Bug class: selected throw import reuses a pruned GuestThrowable ref whose contents became Unknown.
        // Oracle: the thrown raw object is admitted as a new GuestThrowable root with the next scheduler id.
        val type = VirtualType.of("Ljava/lang/IllegalStateException;")
        val view = hostView("throwHolderChild(Ljava/lang/Object;)V")
        val frame = Frame.entryFrame(view)
        val child = IllegalStateException("removed")
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(HostChildHolder(child), objectType)),
                HostSeed(Value.Known(child, type)),
            ),
        ).map { it as Value.Known }
        val parent = admitted[0]
        val oldChildRef = admitted[1].raw as HeapRef
        // The probe consumes its id, so the run's next allocation receives the id after it.
        val expectedNewId = probeNextHeapId(frame) + 1
        frame.assign(0, parent)

        val node = hostVm.newSession(view, entryFrame = frame).step()!!
        val thrown = node.outcome as Outcome.Threw
        val newRef = (thrown.exception as Value.Known).raw as HeapRef

        assertEquals(expectedNewId, newRef.id)
        assertNotEquals(oldChildRef, newRef)
        assertEquals(HostGraphContents.Unknown, (node.frame.heapObject(oldChildRef) as HeapObject.GuestThrowable).contents)
        assertTrue((node.frame.heapObject(newRef) as HeapObject.GuestThrowable).contents is HostGraphContents.Known)
        assertEquals("removed", (node.frame.heap.hostObjectAt(newRef) as IllegalStateException).message)
    }

    @Test
    fun stillOwnedReturnedChildReusesItsStableRef() {
        // Bug class: rejecting every detached raw identity allocates a second ref even while parent still owns child.
        // Oracle: unchanged ownership and known node mapping preserve the original child HeapRef.
        val view = hostView("holderChild(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val child = StringBuilder("owned")
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(HostChildHolder(child), objectType)),
                HostSeed(Value.Known(child, builderType)),
            ),
        ).map { it as Value.Known }
        frame.assign(0, admitted[0])

        val returned = (hostVm.newSession(view, entryFrame = frame).run().outcomes.single()
            as Outcome.Returned).value as Value.Known

        assertEquals(admitted[1].raw, returned.raw)
    }

    @Test
    fun removedOwnedArrayMirrorReturnGetsANewTrackedArrayRef() {
        // Bug class: a pruned source array ref is reused after its mirror became Unknown.
        // Oracle: return admission allocates a new tracked mirror while preserving exact lattice elements.
        val view = hostView("detachHolderChild(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val child = intArrayOf(2, 3)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(HostChildHolder(child), objectType)),
                HostSeed(Value.Known(child, intArrayType)),
            ),
        ).map { it as Value.Known }
        val oldRef = admitted[1].raw as HeapRef
        // The probe consumes its id, so the run's next allocation receives the id after it.
        val expectedNewId = probeNextHeapId(frame) + 1
        frame.assign(0, admitted[0])

        val run = hostVm.newSession(view, entryFrame = frame)
        val graph = run.run()
        val newRef = (((graph.outcomes.single() as Outcome.Returned).value as Value.Known).raw as HeapRef)
        val terminalFrame = requireNotNull(run.currentNode).frame

        assertEquals(expectedNewId, newRef.id)
        assertNotEquals(oldRef, newRef)
        assertEquals(HostGraphContents.Unknown, terminalFrame.heap.arrayAt(oldRef)!!.contents)
        assertEquals(listOf(Value.Known(2, intType), Value.Known(3, intType)), terminalFrame.heap.arrayAt(newRef)!!.elements())
        assertTrue(terminalFrame.isHostMaterializable(Value.Known(newRef, intArrayType)))
    }

    @Test
    fun stringBuilderMutationIsVisibleThroughCallerAliases() {
        val builder = StringBuilder("start")
        val view = hostView("appendChangedThroughAlias(Ljava/lang/StringBuilder;Ljava/lang/Object;)V")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(builderType, builder)
        frame.assign(0, Value.Known(ref, builderType))
        frame.assign(1, Value.Known(ref, objectType))

        val graph = hostVm.newSession(view, entryFrame = frame).run()
        val returning = graph.nodePile(view.terminalInstructionAddresses.single()).single().frame
        val stored = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.HostObject

        assertEquals(Value.Known(ref, builderType), returning.peek(0))
        assertEquals(Value.Known(ref, objectType), returning.peek(1))
        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals("start-changed", CoreTester.terminatingHostObject(graph, ref).toString())
    }

    @Test
    fun listMutationAndReturnedAliasUseTheSameJavaObjectAndHeapRef() {
        val list = arrayListOf<Any>()
        val listType = VirtualType.of("Ljava/util/ArrayList;")
        val view = view("Linvoke_reflected_test;", "addToListAndGet()V")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(listType, list)
        frame.assign(0, Value.Known(ref, VirtualType.of("Ljava/util/List;")))
        frame.assign(1, Value.Known(ref, objectType))
        frame.assign(2, Value.Known(0, intType))

        val graph = hostVm.newSession(view, entryFrame = frame).run()
        val returning = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame
        val stored = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.HostObject
        val storedList = CoreTester.terminatingHostObject(graph, ref) as List<*>

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(stored.contents is HostGraphContents.Known)
        // `get(0)` returns the list itself, so the result is the list's own ref tagged with its runtime class.
        assertEquals(Value.Known(ref, listType), returning.peek(0))
        assertSame(storedList, storedList.single())
    }

    @Test
    fun returnedUnreachableHistoricalObjectGetsANewReachableRef() {
        val view = hostView("returnStored()Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val hiddenRef = frame.heap.allocateHostObject(builderType, StringBuilder("hidden"))
        val session = hostVm.newSession(view, entryFrame = frame)
        DirectHostCallsFixture.stored = session.frontier.single().frame.heap.hostObjectAt(hiddenRef)!!

        val returned = (session.run().outcomes.single() as Outcome.Returned).value as Value.Known

        assertNotEquals(hiddenRef, returned.raw)
        assertEquals("hidden", session.currentNode!!.frame.heap.hostObjectAt(returned.raw as HeapRef).toString())
    }

    @Test
    fun returnedNestedObjectGetsOneRefForItsExistingGraphNode() {
        val child = StringBuilder("nested")
        val container = arrayListOf<Any>(child)
        val view = hostView("firstElement(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val containerRef = frame.heap.allocateHostObject(objectType, container)
        frame.assign(0, Value.Known(containerRef, objectType))

        val graph = hostVm.newSession(view, entryFrame = frame).run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val returnedRef = returned.raw as HeapRef

        assertNotEquals(containerRef, returnedRef)
        assertEquals("nested", CoreTester.terminatingHostObject(graph, returnedRef).toString())
    }

    @Test
    fun newMutableReturnGetsAFreshSchedulerIdWithoutCollision() {
        val view = hostView("newBuilder()Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val existing = frame.heap.allocateHostObject(objectType, arrayListOf("existing"))
        val graph = hostVm.newSession(view, entryFrame = frame).run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val returnedRef = returned.raw as HeapRef
        val stored = graph.trusted()!!.terminatingHeapObject(returnedRef).agreed() as HeapObject.HostObject

        assertEquals(0, existing.id)
        assertEquals(1, returnedRef.id)
        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals("new", CoreTester.terminatingHostObject(graph, returnedRef).toString())
        val terminal = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame
        val later = terminal.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap()).raw as HeapRef
        assertEquals(returnedRef.id + 1, later.id, "the returned object advances the session allocator")
    }

    @Test
    fun mutableReturnKeepsDeclaredValueTagAndStoresRuntimeHeapType() {
        // The reflection backend tags a reference return with its runtime class, so only a backend that reports
        // the declared tag reaches the case where the value tag and the stored heap type differ.
        val backend = DirectBackend {
            SynchronousCallResult.Returned(
                HostReturn.Value(HostValue.Reference(StringBuilder("runtime"), objectType)),
            )
        }
        val view = hostView("newBuilder()Ljava/lang/Object;")
        val graph = session(view, VmServices(CoreTester.classIndex), Frame.entryFrame(view), listOf(backend)).run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val ref = returned.raw as HeapRef

        assertEquals(objectType, returned.type)
        assertEquals(builderType, graph.trusted()!!.terminatingHeapObject(ref).agreed().type)
    }

    @Test
    fun unchangedObjectArrayReferenceKeepsItsOriginalElementTagAndObjectVersion() {
        val frame = Frame.entryFrame(view("Lcalls/caller;", "callsNativeCallee()I"))
        val builder = StringBuilder("same")
        val raw = arrayOf<Any>(builder)
        val arrayValue = frame.heap.admitHostSeeds(listOf(HostSeed(Value.Known(raw, objectArrayType)))).single()
            as Value.Known
        val ref = arrayValue.raw as HeapRef
        val before = frame.heapObject(ref)
        val original = frame.heap.arrayAt(ref)!!.element(0)
        val selected = SelectedHostCall.from(
            prepared(listOf(objectArrayType), listOf(arrayValue)),
            frame,
            CoreTester.classIndex.classLoader,
        )
        selected.finalize(frame)

        assertEquals(original, frame.heap.arrayAt(ref)!!.element(0))
        assertEquals(objectType, (frame.heap.arrayAt(ref)!!.element(0) as Value.Known).type)
        assertSame(before, frame.heapObject(ref))
    }

    @Test
    fun unchangedImmutableAndNullArrayElementsKeepTagsWhileClearingDirtyMirrorState() {
        val frame = Frame.entryFrame(view("Lcalls/caller;", "callsNativeCallee()I"))
        val raw = arrayOf<Any?>("same", null)
        val arrayValue = frame.heap.admitHostSeeds(listOf(HostSeed(Value.Known(raw, objectArrayType)))).single()
            as Value.Known
        val ref = arrayValue.raw as HeapRef
        val charSequence = VirtualType.of("Ljava/lang/CharSequence;")
        val string = VirtualType.of("Ljava/lang/String;")
        frame.heap.setArrayElements(
            ref,
            0,
            listOf(Value.Known("same", charSequence), Value.Known(null, string)),
        )
        val before = frame.heapObject(ref)
        val selected = SelectedHostCall.from(
            prepared(listOf(objectArrayType), listOf(arrayValue)),
            frame,
            CoreTester.classIndex.classLoader,
        )

        selected.finalize(frame)

        assertEquals(Value.Known("same", charSequence), frame.heap.arrayAt(ref)!!.element(0))
        assertEquals(Value.Known(null, string), frame.heap.arrayAt(ref)!!.element(1))
        assertNotSame(before, frame.heapObject(ref))
        assertFalse(frame.heap.arrayAt(ref)!!.mirrorDirty)
    }

    @Test
    fun backendCannotForgeAHeapRefReturn() {
        val backend = DirectBackend {
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Reference(HeapRef(999), objectType)))
        }
        val session = SynchronousInvocationFixtures.session(
            "Linvoke_call;",
            "callOpaqueClassResult()Linvoke_call_identity;",
            listOf(backend),
        )

        session.step()

        assertEquals(AbortReason.HOST_FAILURE, session.currentNode!!.outcome.abortReason)
    }

    @Test
    fun backendCannotHideSchedulerObjectsInsideAnExistingHostContainer() {
        val backend = DirectBackend { call ->
            @Suppress("UNCHECKED_CAST")
            (call.arguments.single() as MutableList<Any>).add(HeapRef(999))
            SynchronousCallResult.Returned(HostReturn.Void)
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(objectType, arrayListOf<Any>())
        frame.assign(0, Value.Known(ref, objectType))

        val node = session(view, vm, frame, listOf(backend)).step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertTrue(node.outcome.hostFailure is HostGraphOwnershipException)
        assertEquals(HostGraphContents.Unknown, (node.frame.heapObject(ref) as HeapObject.HostObject).contents)
        assertEquals(null, node.frame.heap.hostObjectAt(ref))
    }
}
