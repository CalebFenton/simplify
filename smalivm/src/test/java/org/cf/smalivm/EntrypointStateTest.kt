package org.cf.smalivm

import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EntrypointStateTest {

    @Test
    fun ordinaryInstanceEntrypointUsesUnknownReceiver() {
        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, "someString()Ljava/lang/String;")
        val state = CoreTester.vmServices.newSession(view).frontier.single().frame

        assertEquals(2, state.registerCount)
        assertEquals(1, view.parameters.size)
        assertEquals(1, view.parameters.sumOf { if (it.type.isWide) 2 else 1 })
        assertEquals(1, view.parameters.single().register)
        assertEquals(Value.Unknown(VirtualType.of(CLASS_NAME)), state.read(1))
    }

    @Test
    fun constructorEntrypointUsesUninitializedReceiver() {
        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, "<init>()V")
        val state = CoreTester.vmServices.newSession(view).frontier.single().frame

        assertEquals(1, state.registerCount)
        assertEquals(1, view.parameters.size)
        assertEquals(1, view.parameters.sumOf { if (it.type.isWide) 2 else 1 })
        assertEquals(0, view.parameters.single().register)

        val receiver = state.read(0) as Value.Known
        assertEquals(VirtualType.of(CLASS_NAME), receiver.type)
        val instance = state.heapObject(receiver.raw as HeapRef) as HeapObject.TrackedObject
        assertEquals(VirtualType.of(CLASS_NAME), instance.type)
        assertTrue(instance.uninitialized)
    }

    @Test
    fun constructorEntrySeedsFieldsWithoutMutatingItsInitialFrameDuringExecution() {
        val owner = "Lorg/cf/smalivm/DummyClass;"
        val type = VirtualType.of(owner)
        val view = CoreTester.classIndex.requireMethodView(owner, "<init>()V")
        val session = CoreTester.vmServices.newSession(view)
        val entry = session.frontier.single().frame
        val receiver = entry.read(view.firstParameterRegister) as Value.Known
        val ref = receiver.raw as HeapRef
        val initial = entry.heapObject(ref) as HeapObject.TrackedObject
        val expectedFields = mapOf(
            "publicInstanceField" to 0x20,
            "protectedInstanceField" to 0x21,
            "packagePrivateInstanceField" to 0,
            "privateInstanceField" to 0x22,
        )
        assertEquals(type, receiver.type)
        assertTrue(entry.registersAssigned().isEmpty(), "entry seeding is not a guest assignment")
        for (field in expectedFields.keys) {
            assertEquals(Value.Known(0, VirtualType.of("I")), initial.field("$owner->$field:I"))
        }

        val trusted = requireNotNull(session.run().trusted())
        val result = trusted.terminatingHeapObject(ref) as HeapObject.TrackedObject
        for ((field, value) in expectedFields) {
            assertEquals(Value.Known(value, VirtualType.of("I")), result.field("$owner->$field:I"))
            assertEquals(Value.Known(0, VirtualType.of("I")), initial.field("$owner->$field:I"))
        }
        assertTrue(initial.uninitialized)
    }

    @Test
    fun anExplicitConstructorReceiverRemainsTheCallersInput() {
        val view = CoreTester.classIndex.requireMethodView(CLASS_NAME, "<init>()V")
        val frame = Frame.entryFrame(view)
        val expected = Value.Unknown(VirtualType.of(CLASS_NAME))
        frame.assign(view.firstParameterRegister, expected)

        val session = CoreTester.vmServices.newSession(view, entryFrame = frame)

        assertEquals(expected, session.frontier.single().frame.read(view.firstParameterRegister))
    }

    private companion object {
        const val CLASS_NAME = "Lchild_class;"
    }
}
