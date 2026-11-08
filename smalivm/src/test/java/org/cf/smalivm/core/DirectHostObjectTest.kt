package org.cf.smalivm.core

import java.math.BigDecimal
import java.math.BigInteger
import org.cf.smalivm.core.op.APutOp
import org.cf.smalivm.core.op.IfComparison
import org.cf.smalivm.core.op.IfOp
import org.cf.smalivm.core.op.SwitchDispatch
import org.cf.smalivm.core.op.SwitchOp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DirectHostObjectTest {
    private val objectType = VirtualType.of("Ljava/lang/Object;")
    private val builderType = VirtualType.of("Ljava/lang/StringBuilder;")
    private val stringType = VirtualType.of("Ljava/lang/String;")
    private val intType = VirtualType.of("I")
    private val intArrayType = VirtualType.of("[I")
    private val objectArrayType = VirtualType.of("[Ljava/lang/Object;")
    private val appendLong = MethodSignature(builderType, "append", "(J)Ljava/lang/StringBuilder;")
    private val hostCallsType = VirtualType.of("Lorg/cf/smalivm/core/DirectHostCallsFixture;")

    /** The production backends, authorized for the host fixture classes and the JDK calls the guest fixtures make. */
    private val hostVm = VmServices(
        CoreTester.classIndex,
        ReflectionPolicy(
            exactSafeMethods = setOf(
                appendLong,
                MethodSignature(VirtualType.of("Ljava/util/ArrayList;"), "add", "(Ljava/lang/Object;)Z"),
                MethodSignature(VirtualType.of("Ljava/util/ArrayList;"), "get", "(I)Ljava/lang/Object;"),
            ),
            safeClasses = setOf(hostCallsType, VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;")),
            methodEffects = mapOf(
                appendLong to SideEffectLevel.NONE,
                MethodSignature(hostCallsType, "appendThenThrow", "(Ljava/lang/StringBuilder;)V") to SideEffectLevel.NONE,
            ),
        ),
    )

    @Test
    @Suppress("DEPRECATION")
    fun reflectedMethodClonesRemainInvocableAndIsolateAccessibility() {
        val method = String::class.java.getDeclaredMethod("length")
        val aliases = arrayOf(method, method)
        val source = HostGraphState.empty().addRoots(listOf(method, aliases)).state
        val clone = cloneHostGraph(source)
        val copied = clone.objectAt(requireNotNull(source.nodeIdOf(method))) as java.lang.reflect.Method
        val copiedAliases = clone.objectAt(requireNotNull(source.nodeIdOf(aliases))) as Array<*>

        assertNotSame(method, copied)
        assertSame(copied, copiedAliases[0])
        assertSame(copied, copiedAliases[1])
        assertEquals(4, copied.invoke("text"))
        assertEquals(HostGraphComparison.Equal, HostGraphComparator().compare(source, clone))

        copied.isAccessible = true
        assertFalse(method.isAccessible)
        assertEquals(HostGraphComparison.Different, HostGraphComparator().compare(source, clone))
        val next = cloneHostGraph(clone)
        val copiedAgain = next.objectAt(requireNotNull(source.nodeIdOf(method))) as java.lang.reflect.Method
        assertTrue(copiedAgain.isAccessible)
        assertEquals(4, copiedAgain.invoke("text"))
    }

    @Test
    fun mutableSeedIsStoredBehindAHeapRef() {
        val frame = Frame.entryFrame(view("Linvoke_reflected_test;", "invokeBooleanValueOf()V"))
        val builder = StringBuilder("seed")

        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(builder, objectType)),
                HostSeed(Value.Known(builder, objectType)),
            ),
        )
        val seeded = admitted[0] as Value.Known
        val ref = seeded.raw as HeapRef

        val stored = frame.heapObject(ref) as HeapObject.HostObject
        assertSame(builder, frame.heap.hostObjectAt(ref))
        assertEquals(builderType, stored.type)
        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals(ref, (admitted[1] as Value.Known).raw)
    }

    @Test
    fun oneGraphClonePreservesNestedAliasesAndCycles() {
        class Node(var next: Node? = null)

        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val frame = Frame.entryFrame(view)
        val shared = Node()
        val first = arrayListOf(shared)
        val second = arrayListOf(shared)
        shared.next = shared
        val firstRef = frame.heap.allocateHostObject(objectType, first)
        val secondRef = frame.heap.allocateHostObject(objectType, second)
        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectType, objectType),
                listOf(Value.Known(firstRef, objectType), Value.Known(secondRef, objectType)),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        val copiedFirst = selected.arguments[0] as ArrayList<*>
        val copiedSecond = selected.arguments[1] as ArrayList<*>
        val copiedShared = copiedFirst.single() as Node

        assertNotSame(copiedFirst, copiedSecond)
        assertSame(copiedShared, copiedSecond.single())
        assertSame(copiedShared, copiedShared.next)
        assertNotSame(shared, copiedShared)
        selected.finalize(frame)
    }

    @Test
    fun siblingCopiesDoNotShareMutableHostRoots() {
        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val parent = Frame.entryFrame(view)
        val ref = parent.heap.allocateHostObject(builderType, StringBuilder("a"))
        val first = parent.copy()
        val second = parent.copy()
        val call = prepared(listOf(builderType), listOf(Value.Known(ref, builderType)))
        val firstSelected = SelectedHostCall.from(call, first, CoreTester.classIndex.classLoader)
        val secondSelected = SelectedHostCall.from(call, second, CoreTester.classIndex.classLoader)
        val firstBuilder = firstSelected.arguments.single() as StringBuilder
        val secondBuilder = secondSelected.arguments.single() as StringBuilder
        firstBuilder.append('b')
        firstSelected.finalize(first)
        secondSelected.finalize(second)

        assertEquals("ab", firstBuilder.toString())
        assertEquals("a", secondBuilder.toString())
        assertNotSame(firstBuilder, secondBuilder)
    }

    @Test
    fun equalDistinctHostObjectsKeepDistinctSchedulerIds() {
        val heap = HeapStore(HeapIds())

        val first = heap.allocateHostObject(objectType, arrayListOf(1))
        val second = heap.allocateHostObject(objectType, arrayListOf(1))

        assertNotEquals(first, second)
    }

    @Test
    fun batchAdmissionPreservesRepeatedRootsAndAnArrayNestedInAContainer() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val array = intArrayOf(1, 2)
        val container = arrayListOf<Any>(array)

        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(container, objectType)),
                HostSeed(Value.Known(array, intArrayType)),
                HostSeed(Value.Known(array, objectType)),
            ),
        ).map { it as Value.Known }
        val arrayRef = admitted[1].raw as HeapRef

        assertEquals(arrayRef, admitted[2].raw)
        assertTrue(frame.heapObject(arrayRef) is HeapObject.TrackedArray)
        val selected = SelectedHostCall.from(
            prepared(
                listOf(objectType, intArrayType),
                listOf(admitted[0], admitted[1]),
            ),
            frame,
            CoreTester.classIndex.classLoader,
        )
        assertSame((selected.arguments[0] as List<*>).single(), selected.arguments[1])
        selected.finalize(frame)
    }

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
        assertEquals(
            SideEffectLevel.NONE,
            graph.explored.sideEffectLevel(0),
            "the policy declares append NONE and exact changed state is fully reconciled",
        )
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
    fun opaqueCollapseInvalidatesSeparatelyReferencedNestedAliases() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val child = StringBuilder("child")
        val container = arrayListOf<Any>(child)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(container, objectType)),
                HostSeed(Value.Known(child, builderType)),
            ),
        ).map { it as Value.Known }
        val containerRef = admitted[0].raw as HeapRef
        val childRef = admitted[1].raw as HeapRef

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(objectType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(containerRef) as HeapObject.HostObject).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(childRef) as HeapObject.HostObject).contents)
        assertEquals(null, frame.heap.hostObjectAt(containerRef))
        assertEquals(null, frame.heap.hostObjectAt(childRef))
    }

    @Test
    fun opaqueCollapseInvalidatesOtherRootsThatReachTheSameChild() {
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val child = StringBuilder("shared")
        val firstRoot = arrayListOf<Any>(child)
        val secondRoot = arrayListOf<Any>(child)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(firstRoot, objectType)),
                HostSeed(Value.Known(secondRoot, objectType)),
                HostSeed(Value.Known(child, builderType)),
            ),
        ).map { it as Value.Known }
        val firstRef = admitted[0].raw as HeapRef
        val secondRef = admitted[1].raw as HeapRef
        val childRef = admitted[2].raw as HeapRef

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(objectType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(firstRef) as HeapObject.HostObject).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(secondRef) as HeapObject.HostObject).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(childRef) as HeapObject.HostObject).contents)
    }

    @Test
    fun opaqueCollapsePreservesDisconnectedHostObjectsAndTheOriginalSibling() {
        // Bug class: an opaque call invalidates every host object instead of only the argument's alias component.
        // Oracle: opaque invocation degrades reachable argument state; a disconnected VM object cannot be mutated.
        val parent = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val affected = arrayListOf<Any>(StringBuilder("affected"))
        val disconnected = StringBuilder("disconnected")
        val admitted = parent.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(affected, objectType)),
                HostSeed(Value.Known(disconnected, builderType)),
            ),
        ).map { it as Value.Known }
        val affectedRef = admitted[0].raw as HeapRef
        val disconnectedRef = admitted[1].raw as HeapRef
        val disconnectedNode = requireNotNull(parent.heap.hostNodeId(disconnected))
        val sibling = parent.copy()

        sibling.heap.collapseReachable(listOf(admitted[0], admitted[0]), listOf(objectType, objectType))

        assertEquals(HostGraphContents.Unknown, (sibling.heapObject(affectedRef) as HeapObject.HostObject).contents)
        assertTrue((sibling.heapObject(disconnectedRef) as HeapObject.HostObject).contents is HostGraphContents.Known)
        assertSame(disconnected, sibling.heap.hostObjectAt(disconnectedRef))
        val compactedNode = requireNotNull(sibling.heap.hostNodeId(disconnected))
        assertEquals(HostGraphNodeId(0), compactedNode)
        assertEquals(disconnectedRef, sibling.heap.refForNode(compactedNode))
        assertEquals(disconnectedNode, parent.heap.hostNodeId(disconnected))
        assertTrue(sibling.isHostMaterializable(Value.Known(disconnectedRef, builderType)))
        assertSame(affected, parent.heap.hostObjectAt(affectedRef))
        assertSame(disconnected, parent.heap.hostObjectAt(disconnectedRef))
    }

    @Test
    fun opaqueCollapsePreservesADisconnectedMirroredArrayExactly() {
        // Bug class: an opaque object argument erases elements and the Java mirror of an unrelated tracked array.
        // Oracle: the argument component has no forward or reverse edge to the disconnected array.
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val affected = StringBuilder("affected")
        val disconnected = intArrayOf(3, 5, 8)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(affected, builderType)),
                HostSeed(Value.Known(disconnected, intArrayType)),
            ),
        ).map { it as Value.Known }
        val arrayRef = admitted[1].raw as HeapRef
        val before = frame.heap.arrayAt(arrayRef)!!.elements()

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(builderType))

        assertEquals(before, frame.heap.arrayAt(arrayRef)!!.elements())
        assertTrue(frame.heap.arrayAt(arrayRef)!!.contents is HostGraphContents.Known)
        assertSame(disconnected, frame.heap.hostObjectAt(arrayRef))
        assertTrue(frame.isHostMaterializable(admitted[1]))
    }

    @Test
    fun unknownReferenceArgumentFallsBackToCompleteHostGraphInvalidation() {
        // Bug class: an unknown reference can alias any VM-owned host root but invalidates none.
        // Oracle: unknown reference connectivity is unprovable, so the conservative fallback is the complete graph.
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val first = frame.heap.allocateHostObject(builderType, StringBuilder("first"))
        val second = frame.heap.allocateHostObject(builderType, StringBuilder("second"))

        frame.heap.collapseReachable(listOf(Value.Unknown(objectType)), listOf(objectType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(first) as HeapObject.HostObject).contents)
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(second) as HeapObject.HostObject).contents)
    }

    @Test
    fun argumentWithUnknownHostContentsFallsBackToCompleteHostGraphInvalidation() {
        // Bug class: a known ref with unknown contents is treated as a disconnected empty component.
        // Oracle: its hidden Java edges can reach any graph node, so connectivity is unprovable.
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val first = frame.heap.allocateHostObject(builderType, StringBuilder("first"))
        val second = frame.heap.allocateHostObject(builderType, StringBuilder("second"))
        val firstValue = Value.Known(first, builderType)

        frame.heap.collapseReachable(listOf(firstValue), listOf(builderType))
        assertTrue((frame.heapObject(second) as HeapObject.HostObject).contents is HostGraphContents.Known)

        frame.heap.collapseReachable(listOf(firstValue), listOf(builderType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(second) as HeapObject.HostObject).contents)
    }

    @Test
    fun incompleteHostGraphFallsBackToCompleteInvalidation() {
        // Bug class: a graph already known incomplete is used to prove two roots disconnected.
        // Oracle: a hidden class prevents safe edge enumeration, so no disconnected component is provable.
        val captured = StringBuilder("captured")
        val incompleteRoot = Runnable { captured.append('!') }
        assertFalse(HostGraphState.empty().addRoots(listOf(incompleteRoot)).state.isComplete)

        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val disconnected = StringBuilder("disconnected")
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(incompleteRoot, objectType)),
                HostSeed(Value.Known(disconnected, builderType)),
            ),
        ).map { it as Value.Known }
        val disconnectedRef = admitted[1].raw as HeapRef

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(objectType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(disconnectedRef) as HeapObject.HostObject).contents)
    }

    @Test
    fun incompleteOrUnenumerableHostGraphFallsBackToCompleteInvalidation() {
        // Bug class: a partial connectivity walk preserves a disconnected root on evidence it could not enumerate.
        // Oracle: incomplete graph ownership cannot prove disconnection, so every host root must become unknown.
        class FailingIterable(private val child: Any) : Iterable<Any> {
            var fail = false

            override fun iterator(): Iterator<Any> {
                check(!fail) { "edges unavailable" }
                return listOf(child).iterator()
            }
        }

        val view = view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;")
        val disconnected = StringBuilder("disconnected")
        val failing = FailingIterable(StringBuilder("child"))
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(failing, objectType)),
                HostSeed(Value.Known(disconnected, builderType)),
            ),
        ).map { it as Value.Known }
        val disconnectedRef = admitted[1].raw as HeapRef
        failing.fail = true

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(objectType))

        assertEquals(HostGraphContents.Unknown, (frame.heapObject(disconnectedRef) as HeapObject.HostObject).contents)
        assertFalse(frame.isHostMaterializable(admitted[1]))
    }

    @Test
    fun deadIncompleteHostGraphDoesNotBlockUnrelatedSelectedExecution() {
        // Bug class: global graph incompleteness is checked before dead roots are pruned.
        // Oracle: an unreachable Runnable cannot affect exact String.length on the current guest path.
        val classIndex = CoreTester.classIndex
        val vm = VmServices(classIndex)
        val view = classIndex.requireMethodView(
            "Linvoke_backend_test;", "deadIncompleteThenStringLength(Ljava/lang/String;)I",
        )
        val frame = Frame.entryFrame(view)
        val captured = StringBuilder("hidden")
        val deadRef = frame.heap.allocateHostObject(objectType, Runnable { captured.append('!') })
        frame.assign(view.parameters.single().register, Value.Known("visible", stringType))

        val graph = vm.newSession(view, entryFrame = frame).run()

        assertEquals(Value.Known(7, intType), (graph.outcomes.single() as Outcome.Returned).value)
        assertEquals(HostGraphContents.Unknown, (requireNotNull(graph.trusted()).terminatingHeapObject(deadRef).agreed() as HeapObject.HostObject).contents)
    }

    @Test
    fun liveIncompleteHostGraphStillRejectsSelectedRepresentation() {
        // Bug class: pruning dead incomplete roots accidentally blesses the same shape while it remains live.
        // Oracle: a live hidden-class graph is unprovable, so selected host representation stays unavailable.
        val classIndex = CoreTester.classIndex
        val vm = VmServices(classIndex)
        val view = classIndex.requireMethodView(
            "Linvoke_backend_test;",
            "liveIncompleteThenStringLength(Ljava/lang/Object;Ljava/lang/String;)I",
        )
        val frame = Frame.entryFrame(view)
        val captured = StringBuilder("hidden")
        val live = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(Runnable { captured.append('!') }, objectType))),
        ).single()
        frame.assign(view.parameters[0].register, live)
        frame.assign(view.parameters[1].register, Value.Known("visible", stringType))

        val graph = vm.newSession(view, entryFrame = frame).run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertEquals(listOf(AbortReason.HOST_FAILURE), graph.outcomes.map { it.abortReason })
    }

    @Test
    fun opaqueSubListViewCannotPreserveAStaleParentSize() {
        // Bug class: SubList enumeration omits its hidden parent, so opaque clear preserves stale parent state.
        // Oracle: JDK view connectivity is unprovable; full invalidation prevents exact parent.size recovery.
        val parent = arrayListOf("first", "second")
        val result = executeViewThenSize(
            "opaqueSubListClearThenParentSize(Ljava/util/List;Ljava/util/List;)I",
            listOf(parent.subList(0, 1), parent),
            listOf(VirtualType.of("Ljava/util/List;"), VirtualType.of("Ljava/util/List;")),
        )

        assertEquals(Value.Unknown(intType), result)
        assertEquals(2, parent.size)
    }

    @Test
    fun opaqueMapKeyViewCannotPreserveAStaleParentSize() {
        // Bug class: a JDK map key view hides its backing map while reporting complete iterable connectivity.
        // Oracle: the view is unprovable, so later exact-safe Map.size cannot return the stale constant two.
        val parent = hashMapOf("first" to 1, "second" to 2)
        val result = executeViewThenSize(
            "opaqueMapViewClearThenParentSize(Ljava/util/Collection;Ljava/util/Map;)I",
            listOf(parent.keys, parent),
            listOf(VirtualType.of("Ljava/util/Collection;"), VirtualType.of("Ljava/util/Map;")),
        )

        assertEquals(Value.Unknown(intType), result)
        assertEquals(2, parent.size)
    }

    @Test
    fun opaqueLiveMapEntryMutationCannotPreserveAStaleBackingValue() {
        // Bug class: maps expose only key/value children, so a live entry root is disconnected from its map.
        // Oracle: owning HashMap entries are stable graph nodes; opaque setValue invalidates the backing map.
        val maps = listOf<MutableMap<String, String>>(
            hashMapOf("key" to "old"),
            linkedMapOf("key" to "old"),
        )
        for (map in maps) {
            val entry = map.entries.single()
            val result = executeHostFixture(
                "opaqueEntrySetValueThenMapGet(Ljava/util/Map\$Entry;Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                listOf(entry, map, "key", "new"),
                listOf(
                    VirtualType.of("Ljava/util/Map\$Entry;"),
                    VirtualType.of("Ljava/util/Map;"),
                    objectType,
                    objectType,
                ),
            )

            assertEquals(Value.Unknown(objectType), result, map.javaClass.name)
            assertEquals("old", map.getValue("key"), "host seed must remain unchanged")
        }
    }

    @Test
    fun opaqueCompactionRemovesInvalidatedComponentsFromLaterHostClones() {
        // Bug class: invalidated raw nodes remain in HostGraphState and are cloned by unrelated later calls.
        // Oracle: after each opaque component is removed, only known roots and their aliases remain cloneable.
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val first = arrayListOf<Any>(StringBuilder("first-child"))
        val second = arrayListOf<Any>(StringBuilder("second-child"))
        val retained = MutableBox(7)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(first, objectType)),
                HostSeed(Value.Known(second, objectType)),
                HostSeed(Value.Known(retained, objectType)),
            ),
        ).map { it as Value.Known }
        val firstRef = admitted[0].raw as HeapRef
        val secondRef = admitted[1].raw as HeapRef
        val retainedRef = admitted[2].raw as HeapRef

        frame.heap.collapseReachable(listOf(admitted[0]), listOf(objectType))
        assertEquals(3, frame.heap.hostGraphObjects().size, "the second component and retained root must remain")
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(firstRef) as HeapObject.HostObject).contents)

        frame.heap.collapseReachable(listOf(admitted[1]), listOf(objectType))
        assertEquals(1, frame.heap.hostGraphObjects().size, "only the retained root must remain")
        assertEquals(HostGraphNodeId(0), frame.heap.hostNodeId(frame.heap.hostObjectAt(retainedRef)!!))
        assertEquals(retainedRef, frame.heap.refForNode(HostGraphNodeId(0)))
        assertEquals(HostGraphContents.Unknown, (frame.heapObject(secondRef) as HeapObject.HostObject).contents)

        val selected = SelectedHostCall.from(
            prepared(listOf(objectType), listOf(Value.Known(retainedRef, objectType))),
            frame,
            CoreTester.classIndex.classLoader,
        )

        assertEquals(7, (selected.arguments.single() as MutableBox).value)
        selected.finalize(frame)
    }

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

    @Test
    fun mutableBigNumberSubclassesUseTrackedIdentityAndSiblingIsolation() {
        // Bug class: an `is BigInteger/BigDecimal` check stores mutable subclasses directly in Value.Known.
        // Oracle: only the exact JDK immutable classes are direct; subclass fields are mutable graph state.
        val raws = listOf<MutableHostNumber>(
            MutableHostBigInteger("7"),
            MutableHostBigDecimal("7.5"),
        )
        for (raw in raws) {
            assertFalse(raw.isDirectHostImmutable(), raw.javaClass.name)
            val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
            val admitted = frame.heap.admitHostSeeds(
                listOf(HostSeed(Value.Known(raw, objectType))),
            ).single() as Value.Known
            val ref = admitted.raw as HeapRef
            val sibling = frame.copy()
            val selected = SelectedHostCall.from(
                prepared(listOf(objectType), listOf(admitted)),
                sibling,
                CoreTester.classIndex.classLoader,
            )

            (selected.arguments.single() as MutableHostNumber).note.append("-changed")
            selected.finalize(sibling)

            assertEquals("seed", (frame.heap.hostObjectAt(ref) as MutableHostNumber).note.toString())
            assertEquals("seed-changed", (sibling.heap.hostObjectAt(ref) as MutableHostNumber).note.toString())
        }
        assertTrue(BigInteger.ONE.isDirectHostImmutable())
        assertTrue(BigDecimal.ONE.isDirectHostImmutable())
    }

    @Test
    fun mutableEnumUsesTrackedIdentityAndSiblingIsolation() {
        // Bug class: every Enum is treated as immutable, so a mutable enum field leaks across sibling paths.
        // Oracle: an enum runtime class is not an exact scalar class and its instance fields are graph state.
        MutableHostEnum.VALUE.note = StringBuilder("seed")
        val frame = Frame.entryFrame(view("Linvoke_call;", "callOpaqueClassResult()Linvoke_call_identity;"))
        val admitted = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(MutableHostEnum.VALUE, objectType))),
        ).single() as Value.Known
        val ref = admitted.raw as HeapRef
        val sibling = frame.copy()
        val selected = SelectedHostCall.from(
            prepared(listOf(objectType), listOf(admitted)),
            sibling,
            CoreTester.classIndex.classLoader,
        )

        (selected.arguments.single() as MutableHostEnum).note.append("-changed")
        selected.finalize(sibling)

        assertEquals("seed", (frame.heap.hostObjectAt(ref) as MutableHostEnum).note.toString())
        assertEquals("seed-changed", (sibling.heap.hostObjectAt(ref) as MutableHostEnum).note.toString())
        assertFalse(MutableHostEnum.VALUE.isDirectHostImmutable())
    }

    @Test
    fun probesSeeHeapMetadataAndOnlyTheSelectedBackendSeesJavaObjects() {
        val builder = StringBuilder("a")
        var selected = false
        val decline = DirectBackend(
            name = "decline",
            probeBlock = { call ->
                assertTrue(call.receiver!!.isLatticeKnown)
                assertTrue(call.receiver!!.isHostMaterializable)
                assertEquals(builderType, call.receiver!!.type)
                assertTrue((listOf<Any?>(call.receiver) + call.arguments).none { it is Value || it is HeapRef })
                BackendProbe.Declined("not mine")
            },
        ) { error("declined backend executed") }
        val accept = DirectBackend(
            name = "accept",
            probeBlock = { call ->
                assertFalse(selected)
                assertTrue(call.receiver!!.isLatticeKnown)
                assertTrue(call.receiver!!.isHostMaterializable)
                BackendProbe.Execute(SideEffectLevel.WEAK)
            },
        ) { call ->
            selected = true
            assertTrue(call.receiver is StringBuilder)
            assertEquals(42L, call.arguments.single())
            (call.receiver as StringBuilder).append("b")
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Reference(call.receiver, builderType)))
        }
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(decline, accept),
            builderType,
            builder,
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(42L, VirtualType.of("J")))
        }

        val graph = session.run()
        val stored = graph.trusted()!!.terminatingHeapObject(ref).agreed() as HeapObject.HostObject

        assertTrue(selected)
        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals("ab", CoreTester.terminatingHostObject(graph, ref).toString())
        assertEquals(SideEffectLevel.WEAK, graph.nodePile(0).single().sideEffectLevel)
    }

    @Test
    fun declineOnlyMutableCallFallsBackOpaqueAndPrunesTheHostGraph() {
        val backend = DirectBackend(
            probeBlock = { BackendProbe.Declined("declined before execution") },
        ) { error("declined backend executed") }
        val (session, _) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(backend),
            builderType,
            StringBuilder("value"),
        ) { frame, ref ->
            frame.assign(0, Value.Known(ref, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }
        val before = session.frontier.single().frame.hostGraphVersionToken()

        session.run()
        val after = requireNotNull(session.currentNode).frame

        assertTrue(after.hostGraphVersionToken() !== before, "opaque fallback must prune its invalidated graph")
        assertTrue(after.heap.hostGraphObjects().isEmpty())
    }

    @Test
    fun probeMetadataSeparatesKnownFromHostMaterializable() {
        val backend = DirectBackend(
            probeBlock = { call ->
                assertTrue(call.arguments.single().isLatticeKnown)
                assertFalse(call.arguments.single().isHostMaterializable)
                BackendProbe.Declined("unknown host contents")
            },
        ) { error("backend must not execute") }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(builderType, StringBuilder("opaque"))
        frame.heap.collapseReachable(listOf(Value.Known(ref, builderType)), listOf(builderType))
        frame.assign(0, Value.Known(ref, builderType))

        session(view, vm, frame, listOf(backend)).run()
    }

    @Test
    fun probeMetadataMarksTrackedGuestObjectsWithoutJavaFormUnmaterializable() {
        val backend = DirectBackend(
            probeBlock = { call ->
                assertTrue(call.arguments.single().isLatticeKnown)
                assertFalse(call.arguments.single().isHostMaterializable)
                BackendProbe.Declined("tracked guest object has no Java form")
            },
        ) { error("backend must not execute") }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val tracked = frame.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap())
        frame.assign(0, tracked)

        session(view, vm, frame, listOf(backend)).run()
    }

    @Test
    fun acceptedHostSetupFailureIsInfrastructureFailureWithoutBackendRetry() {
        val accepted = DirectBackend { error("setup must fail before execution") }
        var laterProbes = 0
        val later = DirectBackend(
            name = "later",
            probeBlock = {
                laterProbes++
                BackendProbe.Execute(SideEffectLevel.NONE)
            },
        ) { error("later backend must not execute") }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val tracked = frame.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap())
        frame.assign(0, tracked)

        val node = session(view, vm, frame, listOf(accepted, later)).step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertEquals(0, laterProbes)
    }

    @Test
    fun acceptedUnknownOrTopValueNeverReachesSynchronousExecution() {
        for (value in listOf<Value>(Value.Unknown(objectType), Value.Top)) {
            var executions = 0
            val backend = DirectBackend(
                probeBlock = { call ->
                    assertFalse(call.arguments.single().isLatticeKnown)
                    assertFalse(call.arguments.single().isHostMaterializable)
                    assertTrue((call.arguments.single() as Any) !is Value)
                    BackendProbe.Execute(SideEffectLevel.NONE)
                },
            ) {
                executions++
                SynchronousCallResult.Returned(HostReturn.Void)
            }
            val vm = VmServices(CoreTester.classIndex)
            val view = identityView()
            val frame = Frame.entryFrame(view)
            frame.assign(0, value)

            val node = session(view, vm, frame, listOf(backend)).step()!!

            assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
            assertEquals(0, executions)
        }
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
    fun targetThrowKeepsMutationAndTheExactThrowable() {
        val view = hostView("appendThenThrow(Ljava/lang/StringBuilder;)V")
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(builderType, StringBuilder("value"))
        frame.assign(0, Value.Known(ref, builderType))

        val node = hostVm.newSession(view, entryFrame = frame).step()!!
        val stored = node.frame.heapObject(ref) as HeapObject.HostObject
        val thrown = node.outcome as Outcome.Threw
        val thrownRef = (thrown.exception as Value.Known).raw as HeapRef

        assertTrue(stored.contents is HostGraphContents.Known)
        assertEquals("value-before-throw", node.frame.heap.hostObjectAt(ref).toString())
        assertEquals(
            SideEffectLevel.NONE,
            node.sideEffectLevel,
            "a target throw does not erase NONE or turn reconciled mutation into an external effect",
        )
        assertEquals(VirtualType.of("Ljava/lang/IllegalStateException;"), thrown.exception.type)
        assertSame(DirectHostCallsFixture.target, node.frame.heap.hostObjectAt(thrownRef))
    }

    @Test
    fun infrastructureFailureKeepsMutationOnTheAbortedPath() {
        val failure = IllegalStateException("infrastructure")
        val backend = DirectBackend { call ->
            (call.receiver as StringBuilder).append("-before-failure")
            SynchronousCallResult.InfrastructureFailure(failure)
        }
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(backend),
            builderType,
            StringBuilder("value"),
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }

        val node = session.step()!!

        assertEquals("value-before-failure", node.frame.heap.hostObjectAt(ref).toString())
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE, failure), node.outcome)
    }

    @Test
    fun fatalInfrastructureFailureRethrowsBeforeFinalization() {
        val fatal = StackOverflowError("fatal infrastructure")
        val backend = DirectBackend {
            SynchronousCallResult.InfrastructureFailure(fatal)
        }
        val session = SynchronousInvocationFixtures.nativeCallSession(backend)

        assertSame(fatal, assertThrows<StackOverflowError> { session.step() })
    }

    @Test
    fun targetThrowVirtualMachineErrorEscapesBeforeFinalization() {
        class FatalIterable : Iterable<Any> {
            var failRefresh = false

            override fun iterator(): Iterator<Any> {
                if (failRefresh) throw StackOverflowError("finalization ran")
                return emptyList<Any>().iterator()
            }
        }

        val targetFatal = StackOverflowError("fatal target result")
        val backend = DirectBackend { call ->
            (call.arguments.single() as FatalIterable).failRefresh = true
            SynchronousCallResult.TargetThrew(
                HostThrowable(virtualTypeOfHostClass(targetFatal.javaClass), targetFatal),
            )
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(objectType, FatalIterable())
        frame.assign(0, Value.Known(ref, objectType))
        val session = session(view, vm, frame, listOf(backend))

        assertSame(targetFatal, assertThrows<StackOverflowError> { session.step() })
        assertEquals(null, session.currentNode!!.outcome)
        assertTrue(session.currentNode!!.children.isEmpty())
    }

    @Test
    fun finalizationStoppedByAHostFailureEndsThePathWithThatFailure() {
        // Bug class: a host failure while re-walking the graph after a returned call is reported as Unprovable and
        // the path continues, so the failure is lost. Oracle: the argument's own accessor throws during the walk,
        // and that throwable is the cause of the path's HOST_FAILURE abort.
        class EntryWalkFailure : RuntimeException("entry state cannot be read")
        class BreakingEntry : Map.Entry<Any?, Any?> {
            var broken = false
            override val key: Any? get() = if (broken) throw EntryWalkFailure() else null
            override val value: Any? get() = null
        }
        val backend = DirectBackend { call ->
            (call.arguments.single() as BreakingEntry).broken = true
            SynchronousCallResult.Returned(HostReturn.Void)
        }
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(objectType, BreakingEntry())
        frame.assign(0, Value.Known(ref, objectType))

        val node = session(view, VmServices(CoreTester.classIndex), frame, listOf(backend)).step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertTrue(node.outcome.hostFailure is EntryWalkFailure, "cause: ${node.outcome.hostFailure}")
    }

    @Test
    fun backendFailureRemainsPrimaryWhenFinalizationAlsoFails() {
        val primary = IllegalStateException("backend failed")
        val backend = DirectBackend { call ->
            (call.arguments.single() as Array<Any?>)[0] = HeapRef(999)
            throw primary
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(objectArrayType, arrayOf(Value.Known("old", objectType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, objectType))

        val node = session(view, vm, frame, listOf(backend)).step()!!

        assertSame(primary, node.outcome.hostFailure)
        assertEquals(1, primary.suppressed.size)
        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertEquals(Value.Unknown(objectType), node.frame.heap.arrayAt(ref)!!.element(0))
    }

    @Test
    fun ordinaryEscapedHostThrowableFinalizesMutationBeforeHostFailureRouting() {
        val escaped = IllegalStateException("escaped")
        val backend = DirectBackend { call ->
            (call.receiver as StringBuilder).append("-before-escape")
            throw escaped
        }
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(backend),
            builderType,
            StringBuilder("value"),
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }

        val node = session.step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE, escaped), node.outcome)
        assertEquals("value-before-escape", node.frame.heap.hostObjectAt(ref).toString())
    }

    @Test
    fun virtualMachineErrorDuringFinalizationEscapesInsteadOfBecomingSuppressed() {
        class FatalIterable : Iterable<Any> {
            var fatal = false
            private val values = mutableListOf<Any>()

            override fun iterator(): Iterator<Any> {
                if (fatal) throw StackOverflowError("fatal refresh")
                return values.iterator()
            }
        }

        val backendFailure = IllegalStateException("ordinary backend failure")
        val backend = DirectBackend { call ->
            (call.arguments.single() as FatalIterable).fatal = true
            throw backendFailure
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = identityView()
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(objectType, FatalIterable())
        frame.assign(0, Value.Known(ref, objectType))
        val session = session(view, vm, frame, listOf(backend))

        assertThrows<StackOverflowError> { session.step() }
        assertEquals(0, backendFailure.suppressed.size)
    }

    @Test
    fun needsClassInitAfterMutationBecomesHostFailureInsteadOfRetry() {
        val backend = DirectBackend { call ->
            (call.receiver as StringBuilder).append("-changed")
            SynchronousCallResult.NeedsClassInit(VirtualType.of("Linvocation_class_init_target;"))
        }
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(backend),
            builderType,
            StringBuilder("value"),
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }

        val node = session.step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertEquals("value-changed", node.frame.heap.hostObjectAt(ref).toString())
        assertTrue(node.children.isEmpty())
    }

    @Test
    fun needsClassInitAfterArrayMaterializationBecomesHostFailure() {
        val backend = DirectBackend {
            SynchronousCallResult.NeedsClassInit(VirtualType.of("Linvocation_class_init_target;"))
        }
        val vm = VmServices(CoreTester.classIndex)
        val view = view("Linvoke_static_test;", "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V")
        val frame = Frame.entryFrame(view)
        val ref = frame.allocateArray(intArrayType, arrayOf(Value.Known(1, intType))).raw as HeapRef
        frame.assign(0, Value.Known(ref, intArrayType))
        frame.assign(1, Value.Known(0, intType))

        val node = session(view, vm, frame, listOf(backend)).step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertTrue(node.children.isEmpty())
    }

    @Test
    fun unchangedMutableGraphCanRequestClassInitAndRetry() {
        var executions = 0
        val backend = DirectBackend { call ->
            executions++
            assertEquals("value", call.receiver.toString())
            if (executions == 1) {
                SynchronousCallResult.NeedsClassInit(VirtualType.of("Linvocation_class_init_target;"))
            } else {
                SynchronousCallResult.Returned(HostReturn.Void)
            }
        }
        val (session, ref) = seededHostSession(
            "Linvoke_reflected_test;",
            "invokeStringBuilderAppendWithLong()V",
            listOf(backend),
            builderType,
            StringBuilder("value"),
        ) { frame, hostRef ->
            frame.assign(0, Value.Known(hostRef, builderType))
            frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        }

        val graph = session.run()

        assertEquals(2, executions)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals("value", requireNotNull(session.currentNode).frame.heap.hostObjectAt(ref).toString())
    }

    @Test
    fun throwableGraphsForkMergeAndReturnByTheOriginalRef() {
        class MutableThrowable(var marker: Int) : RuntimeException("mutable")

        val parent = Frame.entryFrame(view("Lcalls/caller;", "callsVoidCallee()I"))
        val host = MutableThrowable(1)
        val type = VirtualType.of("Ljava/lang/RuntimeException;")
        val ref = parent.allocateThrowable(type, host).raw as HeapRef
        val first = parent.copy()
        val second = parent.copy()
        val same = parent.mergedCalleeState(listOf(first, second), CoreTester.classIndex)
        assertTrue((same.heapObject(ref) as HeapObject.GuestThrowable).contents is HostGraphContents.Known)

        val selected = SelectedHostCall.from(
            prepared(listOf(type), listOf(Value.Known(ref, type))),
            first,
            CoreTester.classIndex.classLoader,
        )
        (selected.arguments.single() as MutableThrowable).marker = 2
        selected.finalize(first)
        assertEquals(1, (second.heap.hostObjectAt(ref) as MutableThrowable).marker)
        assertEquals(2, (first.heap.hostObjectAt(ref) as MutableThrowable).marker)

        val merged = parent.mergedCalleeState(listOf(first, second), CoreTester.classIndex)
        assertEquals(HostGraphContents.Unknown, (merged.heapObject(ref) as HeapObject.GuestThrowable).contents)

        val entry = hostView("identityObject(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(entry)
        val existing = frame.allocateThrowable(type, MutableThrowable(7)).raw as HeapRef
        frame.assign(0, Value.Known(existing, type))
        val returned = (
            hostVm.newSession(entry, entryFrame = frame).run().outcomes.single()
                as Outcome.Returned
            ).value
            as Value.Known
        assertEquals(existing, returned.raw)
    }

    @Test
    fun targetThrowOfAnInputThrowableReusesItsExistingRef() {
        val type = VirtualType.of("Ljava/lang/IllegalStateException;")
        val view = hostView("throwAndRecord(Ljava/lang/Throwable;)V")
        val frame = Frame.entryFrame(view)
        val existing = frame.allocateThrowable(type, IllegalStateException("input")).raw as HeapRef
        frame.assign(0, Value.Known(existing, type))
        val graphNodes = frame.heap.hostGraphObjects().size
        val probeBefore = probeNextHeapId(frame)

        val node = hostVm.newSession(view, entryFrame = frame).step()!!
        val thrown = node.outcome as Outcome.Threw

        assertEquals(existing, (thrown.exception as Value.Known).raw)
        assertSame(DirectHostCallsFixture.received, node.frame.heap.hostObjectAt(existing))
        assertEquals(
            probeBefore + 1,
            probeNextHeapId(node.frame),
            "the selected throwable clone must reuse its source ref",
        )
        assertEquals(
            graphNodes,
            node.frame.heap.hostGraphObjects().size,
            "the selected throwable clone must not remain as a second host graph",
        )
    }

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

    @Test
    fun referenceTaggedBoxedZeroIsNonNullInRealIfAndSwitchExecution() {
        val graph = hostVm.newSession(hostView("boxedZero()Ljava/lang/Object;")).run()
        val frame = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame
        val ifOp = IfOp(0, "if-eqz", IfComparison.EQ, 2, 0, null)
        val switch = SwitchOp(
            0,
            "packed-switch",
            0,
            listOf(SwitchOp.Case(0, 3)),
            2,
            SwitchDispatch.PACKED,
        )

        assertEquals(listOf(OpArm.Continue()), ifOp.execute(frame).arms)
        assertEquals(listOf(OpArm.Continue()), switch.execute(frame).arms)
    }

    @Test
    fun arbitraryReferenceTaggedNumberIsNeverReadAsIntegralBits() {
        val number = object : Number() {
            override fun toByte() = 0.toByte()
            override fun toDouble() = 0.0
            override fun toFloat() = 0f
            override fun toInt() = 0
            override fun toLong() = 0L
            override fun toShort() = 0.toShort()
        }
        DirectHostCallsFixture.stored = number
        val graph = hostVm.newSession(hostView("returnStored()Ljava/lang/Object;")).run()
        val frame = graph.nodePile(graph.methodView.terminalInstructionAddresses.single()).single().frame
        val ifOp = IfOp(0, "if-eqz", IfComparison.EQ, 2, 0, null)

        assertEquals(listOf(OpArm.Continue()), ifOp.execute(frame).arms)
    }

    @Test
    fun hostObjectsRuntimeTypeOverridesLooseTagForObjectArrayStore() {
        val frame = Frame.entryFrame(view("Linvoke_reflected_test;", "invokeStringBuilderAppendWithLong()V"))
        val charSequenceArray = VirtualType.of("[Ljava/lang/CharSequence;")
        val arrayRef = frame.allocateArray(
            charSequenceArray,
            arrayOf(Value.Known(null, VirtualType.of("Ljava/lang/CharSequence;"))),
        ).raw as HeapRef
        val builderRef = frame.heap.allocateHostObject(builderType, StringBuilder("value"))
        frame.assign(0, Value.Known(builderRef, objectType))
        frame.assign(1, Value.Known(arrayRef, charSequenceArray))
        frame.assign(2, Value.Known(0, intType))
        val op = APutOp(
            0,
            "aput-object",
            0,
            1,
            2,
            true,
            CoreTester.classIndex.currentSnapshot.hierarchy,
        )

        val arm = op.execute(frame).arms.single() as OpArm.Continue
        arm.update.applyTo(frame)
        assertEquals(Value.Known(builderRef, VirtualType.of("Ljava/lang/CharSequence;")), frame.heap.arrayAt(arrayRef)!!.element(0))
    }

    @Test
    fun directImmutableRuntimeTypeOverridesLooseTagForObjectArrayStore() {
        val frame = Frame.entryFrame(view("Linvoke_reflected_test;", "invokeStringBuilderAppendWithLong()V"))
        val charSequence = VirtualType.of("Ljava/lang/CharSequence;")
        val charSequenceArray = VirtualType.of("[Ljava/lang/CharSequence;")
        val arrayRef = frame.allocateArray(
            charSequenceArray,
            arrayOf(Value.Known(null, charSequence)),
        ).raw as HeapRef
        frame.assign(0, Value.Known("value", objectType))
        frame.assign(1, Value.Known(arrayRef, charSequenceArray))
        frame.assign(2, Value.Known(0, intType))
        val op = APutOp(
            0,
            "aput-object",
            0,
            1,
            2,
            true,
            CoreTester.classIndex.currentSnapshot.hierarchy,
        )

        val arm = op.execute(frame).arms.single() as OpArm.Continue
        arm.update.applyTo(frame)
        assertEquals(Value.Known("value", charSequence), frame.heap.arrayAt(arrayRef)!!.element(0))
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

    private fun prepared(parameterTypes: List<VirtualType>, arguments: List<Value>) = PreparedInvocation(
        MethodSignature(VirtualType.of("Lcalls/callee;"), "host", "()V"),
        VirtualType.of("Lcalls/caller;"),
        InvokeKind.STATIC,
        VirtualType.of("V"),
        parameterTypes,
        null,
        arguments,
    )

    private fun executeViewThenSize(
        descriptor: String,
        raws: List<Any>,
        types: List<VirtualType>,
    ): Value? {
        return executeHostFixture(descriptor, raws, types)
    }

    private fun executeHostFixture(
        descriptor: String,
        raws: List<Any>,
        types: List<VirtualType>,
    ): Value? {
        val classIndex = CoreTester.classIndex
        val vm = VmServices(classIndex)
        val view = classIndex.requireMethodView("Linvoke_backend_test;", descriptor)
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(raws.zip(types) { raw, type -> HostSeed(Value.Known(raw, type)) })
        view.parameters.zip(admitted).forEach { (parameter, value) -> frame.assign(parameter.register, value) }
        val graph = vm.newSession(view, entryFrame = frame).run()
        val outcome = graph.outcomes.single()
        check(outcome is Outcome.Returned) { "$outcome: ${graph.addresses().flatMap(graph::nodePile).map { it.invocationDiagnostic }}" }
        return outcome.value
    }

    private fun view(className: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(className, descriptor)

    private fun hostView(descriptor: String): MethodView = view("Ldirect_host_calls;", descriptor)

    private fun identityView(): MethodView = hostView("identityObject(Ljava/lang/Object;)Ljava/lang/Object;")

    /** Seeds one host object; null [backends] runs the production backends under [hostVm]'s policy. */
    private fun seededHostSession(
        className: String,
        descriptor: String,
        backends: List<Backend>?,
        type: VirtualType,
        raw: Any,
        seed: (Frame, HeapRef) -> Unit,
    ): Pair<ExecutionSession, HeapRef> {
        val view = view(className, descriptor)
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(type, raw)
        seed(frame, ref)
        return session(view, hostVm, frame, backends) to ref
    }

    /** A session whose invocations go only to [backends]: fault injection no production backend produces. */
    private fun session(
        view: MethodView,
        vm: VmServices,
        frame: Frame,
        backends: List<Backend>?,
    ): ExecutionSession = vm.newSession(view, Limits(), frame, backends, OpBuilder::build)

    private class DirectBackend(
        override val name: String = "direct",
        private val probeBlock: (BackendInvocation) -> BackendProbe = { BackendProbe.Execute(SideEffectLevel.NONE) },
        private val executeBlock: (SelectedHostCall) -> SynchronousCallResult,
    ) : SynchronousBackend {
        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = probeBlock(call)

        override fun execute(call: SelectedHostCall): SynchronousCallResult = executeBlock(call)
    }

    private interface MutableHostNumber {
        val note: StringBuilder
    }

    private class MutableHostBigInteger(value: String) : BigInteger(value), MutableHostNumber {
        override val note = StringBuilder("seed")
        override fun toByte(): Byte = toInt().toByte()
        override fun toShort(): Short = toInt().toShort()
    }

    private class MutableHostBigDecimal(value: String) : BigDecimal(value), MutableHostNumber {
        override val note = StringBuilder("seed")
        override fun toByte(): Byte = toInt().toByte()
        override fun toShort(): Short = toInt().toShort()
    }

    private enum class MutableHostEnum {
        VALUE {
            override var note = StringBuilder("seed")
        };

        abstract var note: StringBuilder
    }

    private class MutableBox(var value: Int)
    internal class MutableParent(var child: MutableChild?)
    internal class MutableChild(var value: Int)
    private class MirrorHolder(var array: IntArray, val extras: Array<MutableBox>)
    internal class HostChildHolder(var child: Any?)
}

/** Host methods the guest fixture `Ldirect_host_calls;` calls through the production ReflectBackend. */
object DirectHostCallsFixture {
    var received: Any? = null
    var stored: Any? = null
    val target = IllegalStateException("target")

    @JvmStatic fun recordReceived(value: Any) { received = value }
    @JvmStatic fun ignore(@Suppress("UNUSED_PARAMETER") value: Any) {}

    @JvmStatic fun detachChild(parent: Any): Any {
        val owner = parent as DirectHostObjectTest.MutableParent
        val child = requireNotNull(owner.child)
        owner.child = null
        return child
    }

    @JvmStatic fun detachHolderChild(holder: Any): Any {
        val owner = holder as DirectHostObjectTest.HostChildHolder
        val child = requireNotNull(owner.child)
        owner.child = null
        return child
    }

    @JvmStatic fun throwHolderChild(holder: Any) { throw detachHolderChild(holder) as Throwable }
    @JvmStatic fun holderChild(holder: Any): Any = requireNotNull((holder as DirectHostObjectTest.HostChildHolder).child)
    @JvmStatic fun appendChanged(builder: StringBuilder) { builder.append("-changed") }
    @JvmStatic fun returnStored(): Any = requireNotNull(stored)
    @JvmStatic fun firstElement(list: Any): Any = requireNotNull((list as List<*>).single())
    @JvmStatic fun newBuilder(): Any = StringBuilder("new")

    @JvmStatic fun appendThenThrow(builder: StringBuilder) {
        builder.append("-before-throw")
        throw target
    }

    @JvmStatic fun throwAndRecord(throwable: Throwable) {
        received = throwable
        throw throwable
    }

    @JvmStatic fun setFirstThenThrow(array: IntArray) {
        array[0] = 7
        throw IllegalStateException("target")
    }

    @JvmStatic fun newIntArray(): Any = intArrayOf(2, 3)

    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun storeNewIntArray(outer: Any) { (outer as Array<Any?>)[0] = intArrayOf(6) }

    @JvmStatic fun boxedZero(): Any = 0
}
