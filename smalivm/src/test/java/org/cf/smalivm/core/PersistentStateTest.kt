package org.cf.smalivm.core

import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeoutPreemptively

/** Persistent frame, heap, and class-init state: isolation between paths and copy cost independent of size. */
class PersistentStateTest {
    private val classIndex = CoreTester.classIndex
    private val vm = CoreTester.vmServices
    private val int = VirtualType.of("I")
    private val intArray = VirtualType.of("[I")
    private val objectType = VirtualType.of("Lpersistent_state/object;")
    private val field = "${objectType.descriptor}->value:I"

    @Test
    fun copiesTakeConstantTimeRegardlessOfLiveStateSize() {
        // Bug class: a frame copy walks its registers, heap, or class store. Oracle: with 100,000 live heap
        // objects and 10,000 statics, 100,000 copies that each walked the state visit over 10^10 entries;
        // persistent-root copies finish within the bound.
        val frame = populatedFrame(heapObjects = 100_000, statics = 10_000)

        var copied = frame
        assertTimeoutPreemptively(Duration.ofSeconds(20)) {
            repeat(100_000) { copied = copied.copy() }
        }

        assertLiveState(copied, frame)
        copied.classes.setStaticField(STATIC_FIELD, Value.Known(2, int))
        assertEquals(Value.Known(1, int), frame.getStaticField(objectType, STATIC_FIELD))
    }

    @Test
    fun longNoWriteExecutionDoesNotCopyLiveStatePerNode() {
        // Bug class: each scheduled node copies the live heap. Oracle: a loop of over 1,000 nodes over a
        // 100,000-object seeded heap copies more than 10^8 entries per run if successors copy state.
        val view = view(SPINE, "spinForever()V")
        val seeded = Frame.entryFrame(view)
        repeat(100_000) { seeded.allocateObject(objectType, mapOf(field to Value.Known(it, int))) }
        val liveRef = seedLiveState(seeded)

        val graph = assertTimeoutPreemptively(Duration.ofSeconds(20)) {
            vm.newSession(view, Limits(maxAddressVisits = 1_000, maxSteps = 5_000), seeded).run()
        }

        val nodes = graph.nodePile(0) + graph.nodePile(1)
        assertTrue(nodes.size > 1_000, "the trace must be long enough to expose per-node state copying")
        val liveObject = seeded.heapObject(liveRef)
        for (node in nodes) {
            assertSame(liveObject, node.frame.heapObject(liveRef))
            assertLiveState(node.frame, liveRef)
        }
    }

    @Test
    fun oneRegisterWriteIsInvisibleToParentAndSibling() {
        val parent = frame().also { it.assign(0, Value.Known(1, int)) }
        val sibling = parent.copy()
        val changed = parent.copy()

        changed.assign(0, Value.Known(2, int))

        assertEquals(Value.Known(1, int), parent.peek(0))
        assertEquals(Value.Known(1, int), sibling.peek(0))
        assertEquals(Value.Known(2, int), changed.peek(0))
    }

    @Test
    fun newlyAssignedRegisterIsInvisibleToParentAndSibling() {
        val parent = frame()
        val sibling = parent.copy()
        val changed = parent.copy()

        changed.assign(1, Value.Known(2, int))

        assertNull(parent.peek(1))
        assertNull(sibling.peek(1))
        assertEquals(Value.Known(2, int), changed.peek(1))
        assertEquals(setOf(1), changed.registersAssigned())
        assertEquals(emptySet<Int>(), parent.registersAssigned())
        assertEquals(emptySet<Int>(), sibling.registersAssigned())
    }

    @Test
    fun childReadRecordsLivenessOnlyOnTheChild() {
        val parent = frame().also { it.assign(0, Value.Known(1, int)) }
        val sibling = parent.copy()
        val child = parent.copy()

        assertEquals(Value.Known(1, int), child.read(0))

        assertEquals(emptySet<Int>(), parent.registersRead())
        assertEquals(emptySet<Int>(), sibling.registersRead())
        assertEquals(setOf(0), child.registersRead())
    }

    @Test
    fun allocationAfterForkIsPathLocalAndSharesUntouchedObjectVersions() {
        val parent = frame()
        val untouchedRef = parent.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val sibling = parent.copy()
        val changed = parent.copy()
        val untouched = parent.heapObject(untouchedRef)

        val allocatedRef = changed.allocateArray(intArray, 2).raw as HeapRef

        assertNull(parent.heapObject(allocatedRef))
        assertNull(sibling.heapObject(allocatedRef))
        assertTrue(changed.heapObject(allocatedRef) is HeapObject.TrackedArray)
        assertSame(untouched, sibling.heapObject(untouchedRef))
        assertSame(untouched, changed.heapObject(untouchedRef))

        val siblingAllocation = sibling.allocateArray(intArray, 1).raw as HeapRef
        assertTrue(allocatedRef != siblingAllocation, "forks must retain one shared monotonic HeapIds allocator")
    }

    @Test
    fun oneArrayWriteReplacesOnlyTheWrittenObjectVersion() {
        val parent = frame()
        val changedRef = parent.allocateArray(intArray, 2).raw as HeapRef
        val untouchedRef = parent.allocateObject(objectType, mapOf(field to Value.Known(3, int))).raw as HeapRef
        val sibling = parent.copy()
        val changed = parent.copy()
        val changedBefore = parent.heapObject(changedRef)
        val untouchedBefore = parent.heapObject(untouchedRef)

        assertTrue(changed.heap.setArrayElement(changedRef, 0, Value.Known(7, int)))

        assertNotSame(changedBefore, changed.heapObject(changedRef))
        assertNotSame(contentsRoot(parent, changedRef), contentsRoot(changed, changedRef))
        assertSame(untouchedBefore, changed.heapObject(untouchedRef))
        assertSame(contentsRoot(parent, untouchedRef), contentsRoot(changed, untouchedRef))
        assertEquals(Value.Known(0, int), parent.heap.arrayAt(changedRef)!!.element(0))
        assertEquals(Value.Known(0, int), sibling.heap.arrayAt(changedRef)!!.element(0))
        assertEquals(Value.Known(7, int), changed.heap.arrayAt(changedRef)!!.element(0))
    }

    @Test
    fun oneFieldWriteReplacesOnlyTheWrittenObjectVersion() {
        val parent = frame()
        val changedRef = parent.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val untouchedRef = parent.allocateArray(intArray, 1).raw as HeapRef
        val sibling = parent.copy()
        val changed = parent.copy()
        val changedBefore = parent.heapObject(changedRef)
        val untouchedBefore = parent.heapObject(untouchedRef)

        assertTrue(changed.heap.setField(changedRef, field, Value.Known(9, int)))

        assertNotSame(changedBefore, changed.heapObject(changedRef))
        assertNotSame(contentsRoot(parent, changedRef), contentsRoot(changed, changedRef))
        assertSame(untouchedBefore, changed.heapObject(untouchedRef))
        assertSame(contentsRoot(parent, untouchedRef), contentsRoot(changed, untouchedRef))
        assertEquals(Value.Known(1, int), parent.getField(changedRef, field))
        assertEquals(Value.Known(1, int), sibling.getField(changedRef, field))
        assertEquals(Value.Known(9, int), changed.getField(changedRef, field))
    }

    @Test
    fun lifecycleAndStaticWritesStayOnTheirOwnPath() {
        val parent = frame()
        parent.classes.setStaticField(STATIC_FIELD, Value.Known(1, int))

        val lifecycleChanged = parent.copy()
        lifecycleChanged.classes.complete(objectType)
        val staticChanged = parent.copy()
        staticChanged.classes.setStaticField(STATIC_FIELD, Value.Known(2, int))

        assertEquals(ClassInitState.INITIALIZED, lifecycleChanged.initState(objectType))
        assertEquals(Value.Known(1, int), lifecycleChanged.getStaticField(objectType, STATIC_FIELD))
        // An entry frame's untriggered class reads by the default arbitrary-entry state.
        assertEquals(ClassInitState.UNKNOWN, staticChanged.initState(objectType))
        assertEquals(Value.Known(2, int), staticChanged.getStaticField(objectType, STATIC_FIELD))
        assertEquals(ClassInitState.UNKNOWN, parent.initState(objectType))
        assertEquals(Value.Known(1, int), parent.getStaticField(objectType, STATIC_FIELD))
    }

    @Test
    fun widePairsAndSpecialSlotLifecyclesSurviveSharedRegisterRoots() {
        val parent = frame("returnWide()J")
        val wide = VirtualType.of("J")
        val exception = VirtualType.of("Ljava/lang/RuntimeException;")
        parent.assign(0, Value.Known(42L, wide))
        parent.setResult(Value.Known(1, int))
        parent.setCaught(Value.Known("caught", exception))
        parent.setReturnValue(FrameValue.plain(Value.Known(2, int)))
        parent.setThrown(Value.Known("thrown", exception))
        val child = parent.copy()

        assertEquals(Value.Known(42L, wide), child.peek(0))
        assertNull(child.peek(1), "a shared root must retain the wide-high marker")
        assertEquals(Value.Known(1, int), child.takeResult()?.value)
        assertEquals(Value.Known("caught", exception), child.takeCaught()?.value)
        assertNull(child.takeResult()?.value)
        assertNull(child.takeCaught()?.value)
        assertEquals(Value.Known(1, int), parent.takeResult()?.value, "child consumption must not clear its parent")
        assertEquals(Value.Known("caught", exception), parent.takeCaught()?.value)
        assertEquals(Value.Known(2, int), child.returnValueOrNull())
        assertEquals(Value.Known("thrown", exception), child.thrownOrNull())
    }

    @Test
    fun observerReadsRecordNoLivenessAndCreateNoObjectVersions() {
        // Observers read through the ExecutionState facade, which records no liveness and creates no versions.
        val frame = frame().also {
            it.assign(0, Value.Known(1, int))
            it.classes.complete(objectType)
            it.classes.setStaticField(STATIC_FIELD, Value.Known(4, int))
        }
        val ref = frame.allocateObject(objectType, mapOf(field to Value.Known(3, int))).raw as HeapRef
        val arrayRef = frame.allocateArray(intArray, 2).raw as HeapRef
        frame.heap.setArrayElement(arrayRef, 0, Value.Known(7, int))
        val versions = listOf(ref, arrayRef).map { frame.heapObject(it) }
        repeat(10) {
            frame.peekRegister(0)
            frame.readRegister(0)
            frame.registersAssigned()
            frame.registersRead()
            frame.getField(ref, field)
            frame.heapObject(ref)
            assertEquals(Value.Known(7, int), frame.heap.arrayAt(arrayRef)!!.element(0))
            assertEquals(listOf(Value.Known(7, int), Value.Known(0, int)), frame.heap.arrayAt(arrayRef)!!.elements())
            frame.initState(objectType)
            frame.isClassInitialized(objectType)
            frame.getStaticField(objectType, STATIC_FIELD)
            frame.toString()
        }
        assertEquals(emptySet<Int>(), frame.registersRead())
        assertObjectVersionsSame(frame, listOf(ref, arrayRef), versions)

        val debuggerSession = vm.newSession(view(SPINE, "nopThenConst()I"))
        val debugger = Debugger(debuggerSession)
        val debugFrame = debugger.cursor!!.frame
        val debugRead = debugFrame.registersRead()
        repeat(10) {
            debugger.currentState?.peekRegister(0)
            debugger.currentState?.readRegister(0)
            debugger.currentState?.registersAssigned()
            debugger.currentState?.initState(objectType)
        }
        assertEquals(debugRead, debugFrame.registersRead())

        val graphView = view(SPINE, "returnFive()I")
        val graphSeed = Frame.entryFrame(graphView)
        val graphRef = seedLiveState(graphSeed)
        val graphArrayRef = graphSeed.allocateArray(intArray, 2).raw as HeapRef
        graphSeed.heap.setArrayElement(graphArrayRef, 0, Value.Known(8, int))
        val graph = vm.newSession(graphView, Limits(), graphSeed).run()
        val graphFrames = graph.methodView.addresses.flatMap { graph.nodePile(it).map(ExecutionNode::frame) }
        val graphRefs = listOf(graphRef, graphArrayRef)
        val graphReads = graphFrames.map(Frame::registersRead)
        val graphVersions = graphFrames.map { frame -> graphRefs.map { frame.heapObject(it) } }
        val trusted = graph.trusted()!!
        repeat(10) {
            graph.explored.consensus(0, 0)
            graph.explored.registerValues(0, 0)
            graph.explored.terminatingConsensus(0)
            val exploredObject = graph.explored.terminatingHeapObject(graphRef).agreed() as HeapObject.TrackedObject
            assertEquals(Value.Known(3, int), exploredObject.field(field))
            val exploredArray =
                graph.explored.terminatingHeapObject(graphArrayRef).agreed() as HeapObject.TrackedArray
            assertEquals(Value.Known(8, int), exploredArray.element(0))
            assertEquals(
                Value.Known(4, int),
                graph.explored.terminatingStaticFieldConsensus(objectType, STATIC_FIELD),
            )
            val trustedObject = trusted.terminatingHeapObject(graphRef).agreed() as HeapObject.TrackedObject
            assertEquals(Value.Known(3, int), trustedObject.field(field))
            val trustedArray = trusted.terminatingHeapObject(graphArrayRef).agreed() as HeapObject.TrackedArray
            assertEquals(Value.Known(8, int), trustedArray.element(0))
            assertEquals(
                Value.Known(4, int),
                trusted.terminatingStaticFieldConsensus(objectType, STATIC_FIELD),
            )
        }
        graphFrames.forEachIndexed { index, observed ->
            assertEquals(graphReads[index], observed.registersRead())
            assertObjectVersionsSame(observed, graphRefs, graphVersions[index])
        }
    }

    @Test
    fun calleeEntryAndResumeShareStoresButKeepRegistersMethodLocal() {
        val method = view(CALLER, "callsVoidCallee()I")
        val seeded = Frame.entryFrame(method)
        val liveRef = seedLiveState(seeded)
        seeded.read(0)
        val session = vm.newSession(method, Limits(), seeded)
        val debugger = Debugger(session)
        val invoke = debugger.stepInto()!!
        assertEquals(1, invoke.address)
        val continuation = debugger.stepOver()!!
        val calleeEntry = invoke.children.single()

        assertLiveState(invoke.frame, liveRef)
        assertLiveState(calleeEntry.frame, liveRef)
        assertLiveState(continuation.frame, liveRef)
        val liveObject = invoke.frame.heapObject(liveRef)
        assertSame(liveObject, calleeEntry.frame.heapObject(liveRef))
        assertSame(liveObject, continuation.frame.heapObject(liveRef))
        assertEquals(Value.Known(3, int), invoke.frame.peek(0))
        assertEquals(setOf(0), invoke.frame.registersAssigned())
        assertEquals(setOf(0), invoke.frame.registersRead())
        assertNull(calleeEntry.frame.peek(0), "callee locals must not resolve through caller registers")
        assertEquals(Value.Known(3, int), continuation.frame.peek(0))
        assertEquals(setOf(0), continuation.frame.registersAssigned())
        assertEquals(setOf(0), continuation.frame.registersRead())
    }

    @Test
    fun classInitRetrySharesCallerRegistersAndHeapWithoutLeakingInitializerLocals() {
        val method = view(INIT_CALLER, "construct()V")
        val seeded = Frame.entryFrame(method)
        seeded.assign(0, Value.Known(55, int))
        val liveRef = seedLiveState(seeded)
        val session = vm.newSession(method, Limits(), seeded)
        val debugger = Debugger(session)
        val trigger = debugger.cursor!!
        val retry = debugger.stepOver()!!
        val initializerEntry = trigger.children.single()
        val initializerReturn = requireNotNull(retry.parent)

        assertEquals(1, retry.frame.addressVisitCount(trigger.address), "class-init retry retains caller path history")
        assertEquals(ClassInitState.INITIALIZING, initializerReturn.frame.initState(INIT_TARGET_TYPE))
        assertEquals(ClassInitState.INITIALIZED, retry.frame.initState(INIT_TARGET_TYPE))
        assertLiveState(trigger.frame, liveRef)
        assertLiveState(initializerEntry.frame, liveRef)
        assertLiveState(retry.frame, liveRef)
        assertSame(trigger.frame.heapObject(liveRef), retry.frame.heapObject(liveRef))
        assertEquals(Value.Known(55, int), trigger.frame.peek(0))
        assertEquals(
            Value.Known(0, int),
            initializerEntry.frame.peek(0),
            "initializer register v0 must hold its own sget result, not caller v0",
        )
        assertEquals(Value.Known(55, int), retry.frame.peek(0))
        assertEquals(OpProgress.Pending, retry.progress, "the retry remains pre-op scheduler work")
    }

    @Test
    fun identicalGuestCalleeReturnPathsReuseUnchangedObjectVersions() {
        val method = view(CALLER, "callsTwoPathVoidCallee(I)V")
        val seeded = Frame.entryFrame(method)
        val liveRef = seedLiveState(seeded)
        seeded.assign(0, Value.Unknown(int))
        seeded.read(0)
        val graph = vm.newSession(method, Limits(), seeded).run()
        val invoke = graph.nodePile(0).single()
        val calleeReturns = descendants(invoke).filter {
            it.view.signature.toString() == "$CALLEE->returnsFromEitherPath(I)V" && it.outcome is Outcome.Returned
        }
        val callerReturns = graph.nodePile(3).filter { it.outcome is Outcome.Returned }
        assertEquals(2, calleeReturns.size)
        assertEquals(2, callerReturns.size)

        val liveObject = invoke.frame.heapObject(liveRef)
        for (returned in calleeReturns) {
            assertLiveState(returned.frame, liveRef)
            assertSame(liveObject, returned.frame.heapObject(liveRef))
        }
        for (callerReturn in callerReturns) {
            assertLiveState(callerReturn.frame, liveRef)
            assertSame(liveObject, callerReturn.frame.heapObject(liveRef))
            assertEquals(Value.Unknown(int), callerReturn.frame.peek(0))
            assertEquals(setOf(0), callerReturn.frame.registersAssigned())
            assertEquals(setOf(0), callerReturn.frame.registersRead())
        }
    }

    @Test
    fun mergeJoinsHeapAndStaticValuesWhilePreservingLifecycleAndUntouchedIdentity() {
        val caller = frame()
        val arrayRef = caller.allocateArray(intArray, 1).raw as HeapRef
        val objectRef = caller.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val untouchedRef = caller.allocateObject(objectType, mapOf(field to Value.Known(5, int))).raw as HeapRef
        val untouched = caller.heapObject(untouchedRef)
        caller.classes.complete(objectType)
        caller.classes.setStaticField(STATIC_FIELD, Value.Known(1, int))
        val first = caller.calleeEntryFrame(view(CALLEE, "returnsNothing()V"))
        val second = caller.calleeEntryFrame(view(CALLEE, "returnsNothing()V"))
        first.heap.setArrayElement(arrayRef, 0, Value.Known(7, int))
        second.heap.setArrayElement(arrayRef, 0, Value.Known(9, int))
        first.heap.setField(objectRef, field, Value.Known(7, int))
        second.heap.setField(objectRef, field, Value.Known(9, int))
        first.classes.setStaticField(STATIC_FIELD, Value.Known(7, int))
        second.classes.setStaticField(STATIC_FIELD, Value.Known(9, int))

        val merged = caller.mergedCalleeState(listOf(first, second), classIndex)

        assertEquals(Value.Unknown(int), merged.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Unknown(int), merged.getField(objectRef, field))
        assertEquals(Value.Unknown(int), merged.getStaticField(objectType, STATIC_FIELD))
        assertEquals(ClassInitState.INITIALIZED, merged.initState(objectType))
        assertSame(untouched, merged.heapObject(untouchedRef))
        assertSame(contentsRoot(caller, untouchedRef), contentsRoot(merged, untouchedRef))
        assertEquals(Value.Known(0, int), caller.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Known(1, int), caller.getField(objectRef, field))
        assertEquals(Value.Known(1, int), caller.getStaticField(objectType, STATIC_FIELD))
    }

    @Test
    fun graphDisagreementReusesGraphIndependentObjectsAndDegradesHostContents() {
        val caller = frame()
        val trackedRef = caller.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val tracked = caller.heapObject(trackedRef)
        val hostType = VirtualType.of("Ljava/lang/StringBuilder;")
        val hostRef = caller.heap.allocateHostObject(hostType, StringBuilder("value"))
        val first = caller.copy()
        val second = caller.copy()
        val selected = SelectedHostCall.from(
            PreparedInvocation(
                MethodSignature(VirtualType.of(CALLEE), "host", "(Ljava/lang/Object;)V"),
                VirtualType.of(CALLER),
                InvokeKind.STATIC,
                VirtualType.of("V"),
                listOf(hostType),
                null,
                listOf(Value.Known(hostRef, hostType)),
            ),
            first,
            classIndex.classLoader,
        )
        (selected.arguments.single() as StringBuilder).append("-changed")
        selected.finalize(first)

        val merged = caller.mergedCalleeState(listOf(first, second), classIndex)

        assertSame(tracked, merged.heapObject(trackedRef))
        assertEquals(HostGraphContents.Unknown, (merged.heapObject(hostRef) as HeapObject.HostObject).contents)
    }

    @Test
    fun heapMergeKeepsAnObjectWhosePathsDisagreeAboutItsConstructor() {
        // Bug class: the call merge drops an object whose callee paths disagree (one ran a delegated <init>,
        // one did not), so the caller register that still holds its ref denotes nothing. Oracle: Dalvik never
        // frees a referenced object, so it exists after the call on every path; "initialized" holds only when
        // every path established it, and each field cell joins as on any other merge.
        val caller = frame()
        val ref = caller.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val initializedPath = caller.copy()
        val uninitializedPath = caller.copy()
        assertTrue(initializedPath.heap.markInitialized(ref))
        initializedPath.heap.setField(ref, field, Value.Known(2, int))

        val merged = caller.mergedCalleeState(listOf(initializedPath, uninitializedPath), classIndex)

        val joined = merged.heapObject(ref) as HeapObject.TrackedObject
        assertTrue(joined.uninitialized)
        assertEquals(Value.Unknown(int), joined.field(field))
    }

    @Test
    fun classMergeRejectsIncompatibleLifecycleAndJoinsMissingStaticAgainstTop() {
        val initialized = ClassStore().also {
            it.complete(objectType)
            it.setStaticField(STATIC_FIELD, Value.Known(7, int))
        }
        val incompatible = ClassStore()
        assertThrows<IllegalArgumentException> {
            ClassStore.merge(listOf(initialized, incompatible), classIndex)
        }

        val missingStatic = initialized.copy()
        val withExtraStatic = initialized.copy().also {
            it.setStaticField(EXTRA_STATIC_FIELD, Value.Known(9, int))
        }
        val merged = ClassStore.merge(listOf(withExtraStatic, missingStatic), classIndex)

        assertEquals(ClassInitState.INITIALIZED, merged.initState(objectType))
        assertEquals(Value.Known(7, int), merged.getStaticField(objectType, STATIC_FIELD))
        assertEquals(Value.Top, merged.getStaticField(objectType, EXTRA_STATIC_FIELD))
    }

    @Test
    fun heapMergeDropsOnlyObjectsAllocatedAfterTheFork() {
        // Oracle: an object one callee path allocated is not reachable from any value every path shares, so the
        // merged heap leaves it out; an object the caller held exists on every path and keeps its version.
        val caller = frame()
        val untouchedRef = caller.allocateObject(objectType, mapOf(field to Value.Known(1, int))).raw as HeapRef
        val untouched = caller.heapObject(untouchedRef)
        val allocatedPath = caller.copy()
        val missingPath = caller.copy()
        val pathOnlyRef = allocatedPath.allocateArray(intArray, 1).raw as HeapRef

        val merged = caller.mergedCalleeState(listOf(allocatedPath, missingPath), classIndex)

        assertNull(merged.heapObject(pathOnlyRef), "an object absent from one outcome path must remain absent")
        assertSame(untouched, merged.heapObject(untouchedRef))
    }

    @Test
    fun heapJoinsRejectStoresFromAnotherRun() {
        // Bug class: two runs' heaps both mint HeapRef(0) for different objects, and a join treats them as one
        // object, so consensus reports contents neither run holds. Oracle: refs are comparable only within one
        // HeapIds, so a join across runs is a caller error.
        val caller = HeapStore(HeapIds())
        val callerRef = caller.allocateObject(objectType, mapOf(field to Value.Known(7, int)))
        val otherRun = HeapStore(HeapIds())
        assertEquals(callerRef, otherRun.allocateObject(objectType, mapOf(field to Value.Known(9, int))))

        assertThrows<IllegalArgumentException> { caller.merged(listOf(otherRun), classIndex) }
        assertThrows<IllegalArgumentException> {
            caller.reconciled(listOf(caller.copy(), otherRun), emptyMap(), mapOf(callerRef to setOf(field)), classIndex)
        }
    }

    @Test
    fun callCompatibilityKeySharesAFieldIndexRegardlessOfUnrelatedHeapSize() {
        // Bug class: every call-completion key scans the complete heap to find a few tracked fields.
        // Oracle: the compatibility index is a persistent root changed only by tracked-field allocation/update.
        val frame = frame()
        val fieldType = VirtualType.of("Ljava/lang/reflect/Field;")
        val declaring = VirtualType.of("Lpersistent_state/object;")
        val metadata = TrackedFieldValue(
            declaring,
            "value",
            ClassIndex.fieldDescriptorOf(declaring, "value", "I"),
            int,
            java.lang.reflect.Modifier.PUBLIC,
            FieldVisibility.PUBLIC,
            FieldStorage.INSTANCE,
            TrackedFieldDomain.Virtual,
        )
        frame.allocateTrackedField(metadata)
        repeat(10_000) { frame.allocateArray(intArray, 1) }
        val first = frame.callCompatibilityKey().trackedFields

        var last = first
        repeat(100_000) { last = frame.callCompatibilityKey().trackedFields }

        assertSame(first, last, "key reads must return the shared persistent compatibility root")
        assertEquals(1, last.size)
        assertEquals(fieldType, frame.heapObject(HeapRef(last.keys.single()))!!.type)
    }

    @Test
    fun compatibilityKeyHashAndEqualityDoNotWalkTheMaps() {
        // Bug class: CallCompatibilityKey equality/hash walks both compatibility maps at every group lookup.
        // Oracle: a million lookups by a shared-root key over two 10,000-entry maps walk 2 * 10^10 entries if
        // hashing or equality traverses; cached hashes and the shared-root equality fast path finish in the bound.
        val frame = frame()
        repeat(10_000) {
            frame.allocateTrackedField(trackedMetadata(it))
            frame.classes.complete(VirtualType.of("Lcompatibility/class$it;"))
        }
        val key = frame.callCompatibilityKey()
        val sharedKey = frame.copy().callCompatibilityKey()
        assertNotSame(key, sharedKey)
        assertEquals(key, sharedKey)
        assertEquals(key.hashCode(), sharedKey.hashCode())
        val groups = HashMap<CallCompatibilityKey, Int>()
        groups[key] = 0

        assertTimeoutPreemptively(Duration.ofSeconds(20)) {
            repeat(1_000_000) { groups[sharedKey] = groups.getValue(sharedKey) + 1 }
        }

        assertEquals(1_000_000, groups.getValue(key))
        val independentlyBuilt = frame()
        repeat(10_000) {
            independentlyBuilt.allocateTrackedField(trackedMetadata(it))
            independentlyBuilt.classes.complete(VirtualType.of("Lcompatibility/class$it;"))
        }
        val independentKey = independentlyBuilt.callCompatibilityKey()
        assertEquals(key, independentKey, "independently rebuilt equal states must converge")
        val partitions = listOf(key, independentKey).groupBy { it }
        assertEquals(1, partitions.size)
    }

    @Test
    fun addressVisitsForkPerPathAndResetOnlyAtCalleeEntry() {
        // Bug class: an activation-wide counter lets sibling paths consume each other's loop budget.
        // Oracle: a visit is path history; a callee starts a new method history and caller resume restores its own.
        val parent = frame()
        assertEquals(1, parent.recordAddressVisit(0))
        val sibling = parent.copy()
        val changed = parent.copy()

        assertEquals(2, changed.recordAddressVisit(0))

        assertEquals(1, parent.addressVisitCount(0))
        assertEquals(1, sibling.addressVisitCount(0))
        assertEquals(2, changed.addressVisitCount(0))

        val callee = parent.calleeEntryFrame(view(CALLEE, "returnsNothing()V"))
        assertEquals(0, callee.addressVisitCount(0))
        val resumed = parent.mergedCalleeState(listOf(callee), classIndex)
        assertEquals(1, resumed.addressVisitCount(0))
    }

    @Test
    fun hostCallSelectionCostIgnoresDeadHeapObjects() {
        // Bug class: every selected call scans the whole heap before host execution. Oracle: selection starts from
        // the call's receiver and arguments; 3,000 calls that each scanned 300,000 dead objects visit about 10^9
        // records.
        val frame = frame()
        val raw = intArrayOf(0)
        val arrayType = VirtualType.of("[I")
        val value = frame.heap.admitHostSeeds(listOf(HostSeed(Value.Known(raw, arrayType)))).single() as Value.Known
        val ref = value.raw as HeapRef
        frame.assign(0, value)
        repeat(300_000) { frame.allocateObject(VirtualType.of("Ldead/object$it;"), emptyMap()) }
        val invocation = PreparedInvocation(
            MethodSignature(VirtualType.of("Lcalls/callee;"), "host", "()V"),
            VirtualType.of("Lcalls/caller;"),
            InvokeKind.STATIC,
            VirtualType.of("V"),
            listOf(arrayType),
            null,
            listOf(value),
        )

        assertTimeoutPreemptively(Duration.ofSeconds(30)) {
            repeat(3_000) { iteration ->
                frame.heap.setArrayElement(ref, 0, Value.Known(iteration, int))
                val selected = SelectedHostCall.from(invocation, frame, CoreTester.classIndex.classLoader)
                assertEquals(iteration, (selected.arguments.single() as IntArray)[0])
                selected.finalize(frame)
            }
        }
    }

    private fun frame(descriptor: String = "moveChain()I"): Frame = Frame.entryFrame(view(SPINE, descriptor))

    private fun populatedFrame(heapObjects: Int, statics: Int): Frame = frame().also {
        it.assign(0, Value.Known(1, int))
        it.read(0)
        it.recordAddressVisit(0)
        it.allocateArray(intArray, 2_048)
        repeat(heapObjects) { index -> it.allocateObject(objectType, mapOf(field to Value.Known(index, int))) }
        it.heap.allocateHostObject(VirtualType.of("Ljava/lang/StringBuilder;"), StringBuilder("host"))
        it.classes.complete(objectType)
        repeat(statics) { index -> it.classes.setStaticField("${objectType.descriptor}->static$index:I", Value.Known(index, int)) }
        it.classes.setStaticField(STATIC_FIELD, Value.Known(1, int))
    }

    private fun view(className: String, descriptor: String): MethodView =
        classIndex.requireMethodView(className, descriptor)

    private fun seedLiveState(frame: Frame): HeapRef {
        val ref = frame.allocateObject(objectType, mapOf(field to Value.Known(3, int))).raw as HeapRef
        frame.classes.complete(objectType)
        frame.classes.setStaticField(STATIC_FIELD, Value.Known(4, int))
        return ref
    }

    private fun trackedMetadata(index: Int): TrackedFieldValue {
        val declaring = VirtualType.of("Lpersistent_state/fields;")
        val name = "field$index"
        return TrackedFieldValue(
            declaring,
            name,
            ClassIndex.fieldDescriptorOf(declaring, name, "I"),
            int,
            java.lang.reflect.Modifier.PUBLIC,
            FieldVisibility.PUBLIC,
            FieldStorage.INSTANCE,
            TrackedFieldDomain.Virtual,
        )
    }

    private fun assertLiveState(frame: Frame, ref: HeapRef) {
        assertEquals(Value.Known(3, int), frame.getField(ref, field))
        assertEquals(ClassInitState.INITIALIZED, frame.initState(objectType))
        assertEquals(Value.Known(4, int), frame.getStaticField(objectType, STATIC_FIELD))
    }

    /** [copied] holds every register, address visit, heap object, and class-init fact of [original]. */
    private fun assertLiveState(copied: Frame, original: Frame) {
        assertEquals(original.peek(0), copied.peek(0))
        assertEquals(original.registersRead(), copied.registersRead())
        assertEquals(original.addressVisitCount(0), copied.addressVisitCount(0))
        for (ref in heapRefsOf(original)) {
            assertSame(original.heapObject(ref), copied.heapObject(ref))
        }
        assertEquals(ClassInitState.INITIALIZED, copied.initState(objectType))
        assertEquals(original.getStaticField(objectType, STATIC_FIELD), copied.getStaticField(objectType, STATIC_FIELD))
    }

    private fun assertObjectVersionsSame(frame: Frame, refs: List<HeapRef>, expected: List<HeapObject?>) {
        refs.zip(expected).forEach { (ref, version) -> assertSame(version, frame.heapObject(ref)) }
    }

    private fun contentsRoot(frame: Frame, ref: HeapRef): Any = when (val obj = frame.heapObject(ref)) {
        is HeapObject.TrackedArray -> obj.contentsRootToken()
        is HeapObject.TrackedObject -> obj.contentsRootToken()
        else -> error("$ref has no persistent contents in $frame")
    }

    private fun descendants(root: ExecutionNode): List<ExecutionNode> {
        val pending = ArrayDeque<ExecutionNode>()
        pending.addAll(root.children)
        val result = mutableListOf<ExecutionNode>()
        while (pending.isNotEmpty()) {
            val node = pending.removeFirst()
            result += node
            pending.addAll(node.children)
        }
        return result
    }

    private companion object {
        const val SPINE = "Lspine/spine;"
        const val CALLER = "Lcalls/caller;"
        const val CALLEE = "Lcalls/callee;"
        const val INIT_CALLER = "Linvocation_class_init_caller;"
        val INIT_TARGET_TYPE = VirtualType.of("Linvocation_class_init_target;")
        const val STATIC_FIELD = "Lpersistent_state/object;->staticValue:I"
        const val EXTRA_STATIC_FIELD = "Lpersistent_state/object;->extra:I"
    }
}
