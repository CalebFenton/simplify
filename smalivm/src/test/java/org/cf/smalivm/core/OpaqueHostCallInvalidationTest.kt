package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * What an opaque host call invalidates: the argument's reachable component when connectivity is provable,
 * otherwise the whole host graph.
 */
internal class OpaqueHostCallInvalidationTest : DirectHostTestBase() {
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
        // Bug class: a graph already known incomplete is taken as proof that two roots are disconnected.
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
        val result = executeHostFixture(
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
        val result = executeHostFixture(
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
}
