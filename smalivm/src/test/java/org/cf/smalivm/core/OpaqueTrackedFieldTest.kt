package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

class OpaqueTrackedFieldTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun oneOpaquePrivateFieldForksAtItsReadToExactReadAndIllegalAccessPaths() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaquePrivateStaticThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ONE)
        val ref = fieldRef(invoke.frame, 0)
        val read = unknownRead(graph, ref)

        assertEquals(FieldAccessibility.UNKNOWN, invoke.children.single().frame.heap.trackedFieldAt(ref)!!.accessibility)
        assertEquals(
            listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN),
            read.children.map { it.frame.heap.trackedFieldAt(ref)!!.accessibility },
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph))
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun duplicateAliasesKeepOneRefAndForkOnceAtTheirRead() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaqueDuplicateAliasesThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ALIASES)
        val ref = fieldRef(invoke.frame, 0)
        val successor = invoke.children.single()
        val read = unknownRead(graph, ref)

        assertEquals(ref, fieldRef(successor.frame, 0))
        assertEquals(ref, fieldRef(successor.frame, 1))
        assertEquals(FieldAccessibility.UNKNOWN, successor.frame.heap.trackedFieldAt(ref)!!.accessibility)
        assertEquals(FieldAccessibility.LANGUAGE_CHECKS, invoke.frame.heap.trackedFieldAt(ref)!!.accessibility)
        assertEquals(
            listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN),
            read.children.map { it.frame.heap.trackedFieldAt(ref)!!.accessibility },
        )
        assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph))
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun unreadReachableFieldsBecomeUnknownWithoutForking() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaqueTwoFields()V")
        val invoke = opaqueInvoke(graph, OPAQUE_TWO)
        val refs = listOf(fieldRef(invoke.frame, 0), fieldRef(invoke.frame, 1))
        val successor = invoke.children.single()

        assertEquals(
            listOf(FieldAccessibility.UNKNOWN, FieldAccessibility.UNKNOWN),
            refs.map { successor.frame.heap.trackedFieldAt(it)!!.accessibility },
        )
        assertEquals(listOf<Value?>(null), returnedValues(graph))
    }

    @Test
    fun opaqueReachabilityFindsFieldsThroughArraysAndTrackedObjects() {
        for (method in listOf("opaqueArrayWrappedFieldThenGet()I", "opaqueObjectWrappedFieldThenGet()I")) {
            val graph = CoreTester.execute(UNRELATED.descriptor, method)

            assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph), method)
            assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph), method)
        }
    }

    @Test
    fun publicFieldReadsTheSameValueUnderEitherAccessibility() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaquePublicStaticThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ONE)

        assertEquals(1, invoke.children.size)
        assertEquals(listOf(Value.Known(0x10, INT), Value.Known(0x10, INT)), returnedValues(graph))
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
    }

    @Test
    @Timeout(value = 60, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun manyReachableFieldsDoNotMultiplyOpaqueSuccessors() {
        // Forty fields reach the opaque call through one array; only the last one is read afterwards.
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaqueManyFieldsThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_OBJECT)

        assertEquals(1, invoke.children.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph))
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun opaqueSuccessorCollapsesReachableStateAndLeavesTheCallerFrameUnchanged() {
        val vm = VmServices(classIndex)
        val entry = view("opaqueMixedCall(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I")
        val frame = Frame.entryFrame(entry)
        val field = frame.allocateTrackedField(privateStaticField())
        val fieldRef = field.raw as HeapRef
        val array = frame.allocateArray(INT_ARRAY, arrayOf(Value.Known(7, INT)))
        val arrayRef = array.raw as HeapRef
        val objectValue = frame.allocateObject(DECLARING, requireNotNull(classIndex.currentResolver().instanceFieldDefaults(DECLARING)))
        val objectRef = objectValue.raw as HeapRef
        assertTrue(frame.heap.setField(objectRef, PUBLIC_INSTANCE, Value.Known(8, INT)))
        val hostRef = frame.heap.allocateHostObject(BUILDER, StringBuilder("host"))
        val arguments = listOf(field, array, objectValue, Value.Known(hostRef, BUILDER))
        entry.parameters.zip(arguments).forEach { (parameter, value) -> frame.assign(parameter.register, value) }
        val refs = listOf(fieldRef, arrayRef, objectRef, hostRef)
        val objectsBefore = refs.map { frame.heapObject(it) }
        val session = vm.newSession(entry, entryFrame = frame)

        val graph = session.run()
        val invoke = opaqueInvoke(graph, OPAQUE_MIXED)
        val child = invoke.children.single()

        assertEquals(listOf(Value.Unknown(INT)), returnedValues(graph))
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertTrue(invoke.invocationDiagnostic?.isNotBlank() == true)
        assertEquals(Value.Unknown(INT), child.frame.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Unknown(INT), child.frame.getField(objectRef, PUBLIC_INSTANCE))
        assertTrue((child.frame.heapObject(hostRef) as HeapObject.HostObject).contents == HostGraphContents.Unknown)
        assertNull(child.frame.heap.hostObjectAt(hostRef))
        assertEquals(FieldAccessibility.UNKNOWN, child.frame.heap.trackedFieldAt(fieldRef)!!.accessibility)

        assertEquals(FieldAccessibility.LANGUAGE_CHECKS, frame.heap.trackedFieldAt(fieldRef)!!.accessibility)
        assertEquals(Value.Known(7, INT), frame.heap.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Known(8, INT), frame.getField(objectRef, PUBLIC_INSTANCE))
        assertTrue(frame.heap.hostObjectAt(hostRef) is StringBuilder)
        assertEquals(arguments, entry.parameters.map { frame.peek(it.register) })
        refs.zip(objectsBefore).forEach { (ref, before) -> assertSame(before, frame.heapObject(ref)) }
    }

    private fun opaqueInvoke(graph: ExecutionGraph, target: MethodDescriptor): ExecutionNode = graph.addresses()
        .flatMap(graph::nodePile)
        .single { it.invocationTarget() == target }

    private fun fieldRef(frame: Frame, register: Int): HeapRef =
        (frame.peek(register) as Value.Known).raw as HeapRef

    /** The read node that met [ref] with unknown accessibility; its children are the definite re-reads. */
    private fun unknownRead(graph: ExecutionGraph, ref: HeapRef): ExecutionNode = graph.addresses()
        .flatMap(graph::nodePile)
        .single {
            it.invocationTarget() == FIELD_GET &&
                it.frame.heap.trackedFieldAt(ref)?.accessibility == FieldAccessibility.UNKNOWN
        }

    private fun returnedValues(graph: ExecutionGraph): List<Value?> =
        graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }

    private fun privateStaticField(): TrackedFieldValue = TrackedFieldValue(
        DECLARING,
        "privateStaticField",
        "${DECLARING.descriptor}->privateStaticField:I",
        INT,
        java.lang.reflect.Modifier.PRIVATE or java.lang.reflect.Modifier.STATIC,
        FieldVisibility.PRIVATE,
        FieldStorage.STATIC,
        TrackedFieldDomain.Virtual,
    )

    private fun view(method: String): MethodView =
        classIndex.requireMethodView(UNRELATED.descriptor, method)

    private companion object {
        val INT = VirtualType.of("I")
        val INT_ARRAY = VirtualType.of("[I")
        val BUILDER = VirtualType.of("Ljava/lang/StringBuilder;")
        val DECLARING = VirtualType.of("Lorg/cf/smalivm/DummyClass;")
        val UNRELATED = VirtualType.of("Lorg/cf/smalivm/unrelated/UnrelatedClass;")
        val ILLEGAL_ACCESS = VirtualType.of("Ljava/lang/IllegalAccessException;")
        val OPAQUE_ONE = MethodDescriptor(UNRELATED, "opaqueOne", "(Ljava/lang/reflect/Field;)V")
        val OPAQUE_ALIASES = MethodDescriptor(
            UNRELATED,
            "opaqueAliases",
            "(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V",
        )
        val OPAQUE_TWO = MethodDescriptor(
            UNRELATED,
            "opaqueTwo",
            "(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V",
        )
        val OPAQUE_MIXED = MethodDescriptor(
            UNRELATED,
            "opaqueMixed",
            "(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I",
        )
        val OPAQUE_OBJECT = MethodDescriptor(UNRELATED, "opaqueObject", "(Ljava/lang/Object;)V")
        val FIELD_GET = MethodDescriptor(
            VirtualType.of("Ljava/lang/reflect/Field;"),
            "get",
            "(Ljava/lang/Object;)Ljava/lang/Object;",
        )
        const val PUBLIC_INSTANCE = "Lorg/cf/smalivm/DummyClass;->publicInstanceField:I"
    }
}

private fun ExecutionNode.invocationTarget(): MethodDescriptor? {
    val invoke = op as? org.cf.smalivm.core.op.InvokeOp ?: return null
    return (invoke.execute(frame).arms.single() as OpArm.Invoke).target
}
