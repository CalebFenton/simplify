package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Task 43 composition evidence that is not established by one focused boundary test.
 *
 * Backend ordering and terminal no-retry behavior remain in [SynchronousInvocationTest]. Debugger and
 * class-initialization shape remain in [BackendInvocationTest] and [ClassForNameIntegrationTest].
 * Persistent sharing remains in [PersistentStateTest]. Clone/compare, constructor, throw/failure, field,
 * framework-static, effect, and unbounded-path rules remain in their focused suites. This class adds only the
 * missing public composition: opaque reachable-state collapse followed by authorized reflection on disconnected
 * state in the same guest execution.
 */
class HostExecutionIntegrationTest {
    @Test
    fun opaqueCallPreservesDisconnectedStateForALaterAuthorizedHostCall() {
        // Bug class: opaque(A) invalidates disconnected B, so the later authorized B.length call becomes opaque.
        // Oracle: only objects alias-connected to A can be mutated; StringBuilder("visible").length is 7.
        val classIndex = CoreTester.classIndex
        val fixture = "Linvoke_backend_test;"
        val descriptor = "opaqueThenAuthorizedLength(Ljava/lang/Object;Ljava/lang/StringBuilder;)I"
        val length = MethodSignature(
            VirtualType.of("Ljava/lang/StringBuilder;"),
            "length",
            "()I",
        )
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeMethods = setOf(length)))
        val view = classIndex.requireMethodView(fixture, descriptor)
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(
            listOf(
                HostSeed(Value.Known(arrayListOf("affected"), VirtualType.OBJECT)),
                HostSeed(Value.Known(StringBuilder("visible"), VirtualType.of("Ljava/lang/StringBuilder;"))),
            ),
        ).map { it as Value.Known }
        frame.assign(view.parameters[0].register, admitted[0])
        frame.assign(view.parameters[1].register, admitted[1])

        val graph = vm.newSession(view, entryFrame = frame).run()
        val affected = graph.trusted()!!.terminatingHeapObject(admitted[0].raw as HeapRef).agreed() as HeapObject.HostObject
        val preserved = graph.trusted()!!.terminatingHeapObject(admitted[1].raw as HeapRef).agreed() as HeapObject.HostObject

        assertEquals(Value.Known(7, VirtualType.of("I")), (graph.outcomes.single() as Outcome.Returned).value)
        assertEquals(HostGraphContents.Unknown, affected.contents)
        assertTrue(preserved.contents is HostGraphContents.Known)
        assertEquals("visible", CoreTester.terminatingHostObject(graph, admitted[1].raw as HeapRef).toString())
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun opaqueTrackedObjectReferenceUnknownByDescriptorInvalidatesPossibleHostAlias() {
        // Bug class: a reference field tagged as primitive is skipped, preserving a host value it may alias.
        // Oracle: the field descriptor, not its loose Value tag, makes Unknown a possible reference alias.
        val result = opaqueThenLength { frame ->
            val owner = VirtualType.of("Lopaque_unknown_holder;")
            val child = ClassIndex.fieldDescriptorOf(owner, "child", "Ljava/lang/Object;")
            val ref = frame.allocateObject(owner, mapOf(child to Value.Unknown(VirtualType.of("I")))).raw
            Value.Known(ref, VirtualType.of("I"))
        }

        assertEquals(Value.Unknown(VirtualType.of("I")), result.returned)
        assertEquals(HostGraphContents.Unknown, result.disconnected.contents)
    }

    @Test
    fun opaqueTrackedArrayReferenceUnknownByComponentInvalidatesPossibleHostAlias() {
        // Bug class: an object-array element tagged as primitive is skipped during opaque reachability.
        // Oracle: the tracked array component is Object, so Unknown may alias the later StringBuilder receiver.
        val result = opaqueThenLength { frame ->
            val type = VirtualType.of("[Ljava/lang/Object;")
            val ref = frame.allocateArray(type, arrayOf(Value.Unknown(VirtualType.of("I")))).raw
            Value.Known(ref, VirtualType.of("I"))
        }

        assertEquals(Value.Unknown(VirtualType.of("I")), result.returned)
        assertEquals(HostGraphContents.Unknown, result.disconnected.contents)
    }

    @Test
    fun opaqueTrackedPrimitiveUnknownDoesNotInvalidateDisconnectedHostState() {
        // Bug class: every nested Unknown forces full host invalidation, including primitive-only cells.
        // Oracle: an int field cannot contain a host reference, regardless of its loose reference tag.
        val result = opaqueThenLength { frame ->
            val owner = VirtualType.of("Lopaque_unknown_holder;")
            val count = ClassIndex.fieldDescriptorOf(owner, "count", "I")
            val ref = frame.allocateObject(
                owner,
                mapOf(count to Value.Unknown(VirtualType.of("Ljava/lang/Object;"))),
            ).raw
            Value.Known(ref, VirtualType.of("I"))
        }

        assertEquals(Value.Known(7, VirtualType.of("I")), result.returned)
        assertTrue(result.disconnected.contents is HostGraphContents.Known)
    }

    private fun opaqueThenLength(argument: (Frame) -> Value): OpaqueLengthResult {
        val classIndex = CoreTester.classIndex
        val fixture = "Linvoke_backend_test;"
        val descriptor = "opaqueThenAuthorizedLength(Ljava/lang/Object;Ljava/lang/StringBuilder;)I"
        val length = MethodSignature(
            VirtualType.of("Ljava/lang/StringBuilder;"),
            "length",
            "()I",
        )
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeMethods = setOf(length)))
        val view = classIndex.requireMethodView(fixture, descriptor)
        val frame = Frame.entryFrame(view)
        val builder = frame.heap.admitHostSeeds(
            listOf(HostSeed(Value.Known(StringBuilder("visible"), VirtualType.of("Ljava/lang/StringBuilder;")))),
        ).single() as Value.Known
        frame.assign(view.parameters[0].register, argument(frame))
        frame.assign(view.parameters[1].register, builder)

        val graph = vm.newSession(view, entryFrame = frame).run()
        val disconnected = graph.trusted()!!.terminatingHeapObject(builder.raw as HeapRef).agreed() as HeapObject.HostObject
        return OpaqueLengthResult((graph.outcomes.single() as Outcome.Returned).value, disconnected)
    }

    private data class OpaqueLengthResult(
        val returned: Value?,
        val disconnected: HeapObject.HostObject,
    )

}
