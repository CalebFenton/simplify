package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * What a backend sees when probing and executing a call: heap metadata during the probe, Java objects only
 * when selected.
 */
internal class BackendProbeTest : DirectHostTestBase() {
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
}
