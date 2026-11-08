package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Materializing tracked arrays as Java arrays for a host call and copying their mutation back. */
internal class HostArrayCopyBackTest : DirectHostTestBase() {
    @Test
    fun primitiveAndObjectArraysMaterializeOnceAndCopyBack() {
        val view = view("Lcalls/caller;", "callsNativeCallee()I")
        val frame = Frame.entryFrame(view)
        val ints = frame.allocateArray(
            intArrayType,
            arrayOf(Value.Known(1, intType), Value.Known(2, intType)),
        ).raw as HeapRef
        val objects = frame.allocateArray(
            objectArrayType,
            arrayOf(Value.Known("old", objectType)),
        ).raw as HeapRef
        val call = prepared(
            listOf(intArrayType, intArrayType, objectArrayType),
            listOf(
                Value.Known(ints, intArrayType),
                Value.Known(ints, intArrayType),
                Value.Known(objects, objectArrayType),
            ),
        )
        val selected = SelectedHostCall.from(call, frame, CoreTester.classIndex.classLoader)

        assertSame(selected.arguments[0], selected.arguments[1])
        (selected.arguments[0] as IntArray)[1] = 9
        val inserted = StringBuilder("inserted")
        (selected.arguments[2] as Array<Any?>)[0] = inserted
        selected.finalize(frame)

        assertEquals(Value.Known(9, intType), frame.heap.arrayAt(ints)!!.element(1))
        val insertedValue = frame.heap.arrayAt(objects)!!.element(0) as Value.Known
        val insertedRef = insertedValue.raw as HeapRef
        assertEquals(builderType, insertedValue.type)
        assertEquals(builderType, frame.heapObject(insertedRef)!!.type)
        assertSame(inserted, frame.heap.hostObjectAt(insertedRef))
    }

    @Test
    fun trackedArrayCopiesBackWhenTargetThrows() {
        val view = hostView("setFirstThenThrow([I)V")
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(intArrayType, arrayOf(Value.Known(1, intType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, intArrayType))
        val session = hostVm.newSession(view, entryFrame = frame)

        val node = session.step()!!

        assertEquals(Value.Known(7, intType), node.frame.heap.arrayAt(ref)!!.element(0))
    }

    @Test
    fun returnedMaterializedArrayMapsBackToItsSourceRef() {
        val view = hostView("identityObject(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(intArrayType, arrayOf(Value.Known(4, intType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, objectType))
        val graph = hostVm.newSession(view, entryFrame = frame).run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertEquals(ref, returned.raw)
    }

    @Test
    fun copyBackFailureIsAtomicAndAbortsThePath() {
        val backend = DirectBackend { call ->
            val array = call.arguments.single() as Array<Any?>
            array[0] = "changed"
            array[1] = HeapRef(999)
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Reference(array, objectType)))
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(
            objectArrayType,
            arrayOf(Value.Known("first", objectType), Value.Known("second", objectType)),
        ).raw as HeapRef
        frame.assign(0, Value.Known(ref, objectType))
        val failed = session(view, vm, frame, listOf(backend)).step()!!
        val tracked = failed.frame.heap.arrayAt(ref)!!

        assertEquals(AbortReason.HOST_FAILURE, failed.outcome.abortReason)
        assertEquals(Value.Unknown(objectType), tracked.element(0))
        assertEquals(Value.Unknown(objectType), tracked.element(1))
    }

    @Test
    fun twoArrayCopyBackValidatesBothBeforeApplyingEither() {
        val frame = Frame.entryFrame(view("Lcalls/caller;", "callsNativeCallee()I"))
        val first = frame.allocateArray(objectArrayType, arrayOf(Value.Known("first", objectType))).raw as HeapRef
        val second = frame.allocateArray(objectArrayType, arrayOf(Value.Known("old", objectType))).raw as HeapRef
        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectArrayType, objectArrayType),
                listOf(Value.Known(first, objectArrayType), Value.Known(second, objectArrayType)),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        // The probe consumes its id, so the first import during finalize receives the id after it.
        val partiallyImportedRef = HeapRef(probeNextHeapId(frame) + 1)
        (selected.arguments[0] as Array<Any?>)[0] = StringBuilder("new")
        (selected.arguments[1] as Array<Any?>)[0] = arrayListOf(HeapRef(999))

        assertThrows<HostGraphOwnershipException> { selected.finalize(frame) }

        assertEquals(Value.Unknown(objectType), frame.heap.arrayAt(first)!!.element(0))
        assertEquals(Value.Unknown(objectType), frame.heap.arrayAt(second)!!.element(0))
        assertEquals(null, frame.heapObject(partiallyImportedRef), "rollback must remove the partially imported ref")
        val later = frame.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap()).raw as HeapRef
        assertTrue(later.id >= partiallyImportedRef.id, "failed imports may leave gaps but never reuse a live id")
    }

    @Test
    fun guestAndHostWritesKeepANestedContainerArrayAliasCoherent() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val array = intArrayOf(1)
        val container = arrayListOf<Any>(array)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(container, objectType)),
                HostSeed(Value.Known(array, intArrayType)),
            ),
        ).map { it as Value.Known }
        val containerRef = admitted[0].raw as HeapRef
        val arrayRef = admitted[1].raw as HeapRef

        frame.heap.setArrayElement(arrayRef, 0, Value.Known(4, intType))
        val selected = SelectedHostCall.from(
            prepared(listOf(objectType), listOf(Value.Known(containerRef, objectType))),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val selectedArray = (selected.arguments.single() as List<*>).single() as IntArray
        assertEquals(4, selectedArray[0])
        selectedArray[0] = 7
        selected.finalize(frame)

        assertEquals(Value.Known(7, intType), frame.heap.arrayAt(arrayRef)!!.element(0))
        assertSame(
            (frame.heap.hostObjectAt(containerRef) as List<*>).single(),
            frame.heap.hostObjectAt(arrayRef),
        )
    }

    @Test
    fun infrastructureFailureCopiesBackArrayMutationBeforeAborting() {
        val failure = IllegalStateException("array failure")
        val backend = DirectBackend { call ->
            (call.arguments[0] as IntArray)[0] = 8
            SynchronousCallResult.InfrastructureFailure(failure)
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = view("Linvoke_static_test;", "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V")
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(intArrayType, arrayOf(Value.Known(1, intType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, intArrayType))
        frame.assign(1, Value.Known(0, intType))

        val node = session(view, vm, frame, listOf(backend)).step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE, failure), node.outcome)
        assertEquals(Value.Known(8, intType), node.frame.heap.arrayAt(ref)!!.element(0))
    }

    @Test
    fun ordinaryEscapedThrowableCopiesBackArrayMutationBeforeAborting() {
        val escaped = IllegalStateException("escaped array failure")
        val backend = DirectBackend { call ->
            (call.arguments[0] as IntArray)[0] = 9
            throw escaped
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = view("Linvoke_static_test;", "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V")
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(intArrayType, arrayOf(Value.Known(1, intType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, intArrayType))
        frame.assign(1, Value.Known(0, intType))

        val node = session(view, vm, frame, listOf(backend)).step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE, escaped), node.outcome)
        assertEquals(Value.Known(9, intType), node.frame.heap.arrayAt(ref)!!.element(0))
    }

    @Test
    fun newReturnedArrayAndNestedReturnedArrayUseTrackedArrayVariants() {
        val directGraph = hostVm.newSession(hostView("newIntArray()Ljava/lang/Object;")).run()
        val directRef = ((directGraph.outcomes.single() as Outcome.Returned).value as Value.Known).raw as HeapRef
        assertTrue(directGraph.trusted()!!.terminatingHeapObject(directRef).agreed() is HeapObject.TrackedArray)

        val view = hostView("storeNewIntArray(Ljava/lang/Object;)V")
        val frame = Frame.entryFrame(view)
        val outer = frame.allocateArray(objectArrayType, arrayOf(Value.Known(null, objectType))).raw as HeapRef
        frame.assign(0, Value.Known(outer, objectType))
        val nestedGraph = hostVm.newSession(view, entryFrame = frame).run()
        val terminal = nestedGraph.nodePile(nestedGraph.methodView.terminalInstructionAddresses.single()).single().frame
        val nestedRef = (terminal.heap.arrayAt(outer)!!.element(0) as Value.Known).raw as HeapRef
        assertTrue(terminal.heapObject(nestedRef) is HeapObject.TrackedArray)
        assertEquals(Value.Known(6, intType), terminal.heap.arrayAt(nestedRef)!!.element(0))
    }

    @Test
    fun newArraysInOneCopyBackShareOneBatchIdentity() {
        val frame = Frame.entryFrame(view("Lcalls/caller;", "callsNativeCallee()I"))
        val outer = frame.allocateArray(
            objectArrayType,
            arrayOf(Value.Known(null, objectType), Value.Known(null, objectType)),
        ).raw as HeapRef
        val selected = SelectedHostCall.from(
            prepared(listOf(objectArrayType), listOf(Value.Known(outer, objectArrayType))),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val nested = intArrayOf(4)
        val rawOuter = selected.arguments.single() as Array<Any?>
        rawOuter[0] = nested
        rawOuter[1] = nested

        selected.finalize(frame)

        val first = frame.heap.arrayAt(outer)!!.element(0) as Value.Known
        val second = frame.heap.arrayAt(outer)!!.element(1) as Value.Known
        assertEquals(first.raw, second.raw)
        assertTrue(frame.heapObject(first.raw as HeapRef) is HeapObject.TrackedArray)
    }
}
