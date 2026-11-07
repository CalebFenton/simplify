package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OpaqueTrackedFieldTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun oneOpaquePrivateFieldForksToExactReadAndIllegalAccessPaths() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaquePrivateStaticThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ONE)

        assertEquals(2, invoke.children.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph))
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun duplicateAliasesForkOnceAndKeepOneRefPerVariant() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaqueDuplicateAliasesThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ALIASES)
        val originalRef = fieldRef(invoke.frame, 0)
        val variants = invoke.children.map { child ->
            val first = fieldRef(child.frame, 0)
            val second = fieldRef(child.frame, 1)
            assertEquals(originalRef, first)
            assertEquals(first, second)
            child.frame.trackedFieldAt(first)!!.accessibility
        }

        assertEquals(listOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN), variants)
        assertEquals(FieldAccessibility.LANGUAGE_CHECKS, invoke.frame.trackedFieldAt(originalRef)!!.accessibility)
        assertEquals(listOf(Value.Known(0x13, INT)), returnedValues(graph))
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun twoReachableFieldsProduceFourDeterministicIndependentCombinations() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaqueTwoFields()V")
        val invoke = opaqueInvoke(graph, OPAQUE_TWO)
        val firstRef = fieldRef(invoke.frame, 0)
        val secondRef = fieldRef(invoke.frame, 1)
        val combinations = invoke.children.map { child ->
            assertEquals(firstRef, fieldRef(child.frame, 0))
            assertEquals(secondRef, fieldRef(child.frame, 1))
            child.frame.trackedFieldAt(firstRef)!!.accessibility to
                child.frame.trackedFieldAt(secondRef)!!.accessibility
        }

        assertEquals(
            listOf(
                FieldAccessibility.LANGUAGE_CHECKS to FieldAccessibility.LANGUAGE_CHECKS,
                FieldAccessibility.LANGUAGE_CHECKS to FieldAccessibility.OVERRIDDEN,
                FieldAccessibility.OVERRIDDEN to FieldAccessibility.LANGUAGE_CHECKS,
                FieldAccessibility.OVERRIDDEN to FieldAccessibility.OVERRIDDEN,
            ),
            combinations,
        )
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
    fun publicFieldBehaviorIsIdenticalAcrossOpaqueAccessibilityVariants() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "opaquePublicStaticThenGet()I")
        val invoke = opaqueInvoke(graph, OPAQUE_ONE)

        assertEquals(2, invoke.children.size)
        assertEquals(listOf(Value.Known(0x10, INT), Value.Known(0x10, INT)), returnedValues(graph))
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
    }

    @Test
    fun opaqueVariantsShareUnchangedStateAndApplyTheSameCollapseAndReturnBehavior() {
        val vm = VmServices(classIndex)
        val entry = view("opaqueMixedCall(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I")
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val field = frame.allocateTrackedField(privateStaticField())
        val fieldRef = field.raw as HeapRef
        val array = frame.allocateArray(INT_ARRAY, arrayOf(Value.Known(7, INT)))
        val arrayRef = array.raw as HeapRef
        val objectValue = frame.allocateObject(DECLARING, requireNotNull(classIndex.instanceFieldDefaults(DECLARING)))
        val objectRef = objectValue.raw as HeapRef
        assertTrue(frame.writeField(objectRef, PUBLIC_INSTANCE, Value.Known(8, INT)))
        val hostRef = frame.allocateHostObject(BUILDER, StringBuilder("host"))
        val arguments = listOf(field, array, objectValue, Value.Known(hostRef, BUILDER))
        entry.parameters.zip(arguments).forEach { (parameter, value) -> frame.assign(parameter.register, value) }
        val before = frame.stateSharingSnapshot()
        val session = vm.newSession(entry, entryFrame = frame)

        val graph = session.run()
        val invoke = opaqueInvoke(graph, OPAQUE_MIXED)
        val variants = invoke.children

        assertEquals(2, variants.size)
        assertEquals(listOf(Value.Unknown(INT), Value.Unknown(INT)), returnedValues(graph))
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertTrue(invoke.invocationDiagnostic?.isNotBlank() == true)
        for (child in variants) {
            assertEquals(Value.Unknown(INT), child.frame.arrayAt(arrayRef)!!.element(0))
            assertEquals(Value.Unknown(INT), child.frame.getField(objectRef, PUBLIC_INSTANCE))
            assertTrue((child.frame.heapObject(hostRef) as HeapObject.HostObject).contents == HostGraphContents.Unknown)
            assertNull(child.frame.hostObjectAt(hostRef))
            assertTrue(child.frame.trackedFieldAt(fieldRef)!!.accessibility != FieldAccessibility.UNKNOWN)
        }

        val first = variants[0].frame.stateSharingSnapshot()
        val second = variants[1].frame.stateSharingSnapshot()
        assertSame(first.registerRoot, second.registerRoot)
        assertSame(first.assignedRoot, second.assignedRoot)
        assertSame(first.readRoot, second.readRoot)
        assertSame(first.classStateRoot, second.classStateRoot)
        assertSame(first.staticRoot, second.staticRoot)
        assertSame(first.hostGraphRoot, second.hostGraphRoot)
        assertNotSame(first.heapRoot, second.heapRoot)
        assertSame(
            variants[0].frame.heapObjectSharing(arrayRef)!!.objectToken,
            variants[1].frame.heapObjectSharing(arrayRef)!!.objectToken,
        )
        assertSame(
            variants[0].frame.heapObjectSharing(objectRef)!!.objectToken,
            variants[1].frame.heapObjectSharing(objectRef)!!.objectToken,
        )

        assertEquals(FieldAccessibility.LANGUAGE_CHECKS, frame.trackedFieldAt(fieldRef)!!.accessibility)
        assertEquals(Value.Known(7, INT), frame.arrayAt(arrayRef)!!.element(0))
        assertEquals(Value.Known(8, INT), frame.getField(objectRef, PUBLIC_INSTANCE))
        assertTrue(frame.hostObjectAt(hostRef) is StringBuilder)
        val after = frame.stateSharingSnapshot()
        assertSame(before.registerRoot, after.registerRoot)
        assertSame(before.heapRoot, after.heapRoot)
        assertEquals(0, after.copies.registerElementsCopied - before.copies.registerElementsCopied)
        assertEquals(0, after.copies.heapObjectsCopied - before.copies.heapObjectsCopied)
        assertEquals(0, after.copies.classElementsCopied - before.copies.classElementsCopied)
    }

    private fun opaqueInvoke(graph: ExecutionGraph, target: MethodDescriptor): ExecutionNode = graph.addresses()
        .flatMap(graph::nodePile)
        .single { it.invocationTarget() == target }

    private fun fieldRef(frame: Frame, register: Int): HeapRef =
        (frame.peek(register) as Value.Known).raw as HeapRef

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
        const val PUBLIC_INSTANCE = "Lorg/cf/smalivm/DummyClass;->publicInstanceField:I"
    }
}

private fun ExecutionNode.invocationTarget(): MethodDescriptor? {
    val invoke = op as? org.cf.smalivm.core.op.InvokeOp ?: return null
    return (invoke.execute(frame).arms.single() as OpArm.Invoke).target
}
