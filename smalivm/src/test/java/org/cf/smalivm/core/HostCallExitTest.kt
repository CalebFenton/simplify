package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

/**
 * Every way a selected host call can end other than returning, and what each does to the path and the
 * mutated host graph.
 */
internal class HostCallExitTest : DirectHostTestBase() {
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

    @TestFactory
    fun aNonFatalExitAfterMutationAbortsThePathAndKeepsTheMutation(): List<DynamicTest> {
        // Each exit reaches finalization after the backend mutated the receiver, so the mutation is imported
        // and the path ends in HOST_FAILURE. A class-init request is retried only from an unchanged graph.
        val infrastructure = IllegalStateException("infrastructure")
        val escaped = IllegalStateException("escaped")
        val needsInit = SynchronousCallResult.NeedsClassInit(VirtualType.of("Linvocation_class_init_target;"))
        val rows: List<Triple<String, Throwable?, () -> SynchronousCallResult>> = listOf(
            Triple("infrastructure failure", infrastructure) { SynchronousCallResult.InfrastructureFailure(infrastructure) },
            Triple("escaped host throwable", escaped) { throw escaped },
            Triple("class-init request", null) { needsInit },
        )
        return rows.map { (name, expectedFailure, exit) ->
            DynamicTest.dynamicTest(name) {
                val backend = DirectBackend { call ->
                    (call.receiver as StringBuilder).append("-changed")
                    exit()
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
                if (expectedFailure != null) assertSame(expectedFailure, node.outcome.hostFailure)
                assertEquals("value-changed", node.frame.heap.hostObjectAt(ref).toString())
                assertTrue(node.children.isEmpty())
            }
        }
    }

    @TestFactory
    fun aVirtualMachineErrorEscapesTheStepUnwrapped(): List<DynamicTest> {
        // A VirtualMachineError from the backend result, the target, or finalization is never turned into
        // a path outcome or a suppressed exception.
        class FatalIterable : Iterable<Any> {
            var fatal = false

            override fun iterator(): Iterator<Any> {
                if (fatal) throw StackOverflowError("finalization ran")
                return emptyList<Any>().iterator()
            }
        }

        fun identitySession(backend: DirectBackend): ExecutionSession {
            val view = identityView()
            val frame = Frame.entryFrame(view)
            val ref = frame.heap.allocateHostObject(objectType, FatalIterable())
            frame.assign(0, Value.Known(ref, objectType))
            return session(view, VmServices(CoreTester.classIndex), frame, listOf(backend))
        }

        return listOf(
            DynamicTest.dynamicTest("fatal infrastructure failure, before finalization") {
                val fatal = StackOverflowError("fatal infrastructure")
                val session = SynchronousInvocationFixtures.nativeCallSession(
                    DirectBackend { SynchronousCallResult.InfrastructureFailure(fatal) },
                )
                assertSame(fatal, assertThrows<StackOverflowError> { session.step() })
            },
            DynamicTest.dynamicTest("fatal target throw, before finalization") {
                val targetFatal = StackOverflowError("fatal target result")
                val session = identitySession(
                    DirectBackend { call ->
                        (call.arguments.single() as FatalIterable).fatal = true
                        SynchronousCallResult.TargetThrew(
                            HostThrowable(virtualTypeOfHostClass(targetFatal.javaClass), targetFatal),
                        )
                    },
                )
                assertSame(targetFatal, assertThrows<StackOverflowError> { session.step() })
                assertEquals(null, session.currentNode!!.outcome)
                assertTrue(session.currentNode!!.children.isEmpty())
            },
            DynamicTest.dynamicTest("fatal error during finalization after a backend failure") {
                val backendFailure = IllegalStateException("ordinary backend failure")
                val session = identitySession(
                    DirectBackend { call ->
                        (call.arguments.single() as FatalIterable).fatal = true
                        throw backendFailure
                    },
                )
                assertThrows<StackOverflowError> { session.step() }
                assertEquals(0, backendFailure.suppressed.size)
            },
        )
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
}
