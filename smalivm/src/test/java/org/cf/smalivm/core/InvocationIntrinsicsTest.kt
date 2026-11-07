package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvocationIntrinsicsTest {
    private val classIndex = CoreTester.classIndex
    private val intType = VirtualType.of("I")
    private val intArrayType = VirtualType.of("[I")
    private val objectType = VirtualType.OBJECT
    private val classType = VirtualType.of("Ljava/lang/Class;")
    private val stringType = VirtualType.of("Ljava/lang/String;")

    @Test
    fun knownArrayCloneAllocatesFreshCopyAndAdvancesTheSessionAllocator() {
        val vm = VmServices(classIndex)
        val entry = callerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val source = frame.allocateArray(
            intArrayType,
            arrayOf(Value.Known(1, intType), Value.Unknown(intType), Value.Known(3, intType)),
        )
        val sourceRef = source.raw as HeapRef
        val sentinel = frame.allocateArray(intArrayType, arrayOf(Value.Known(77, intType)))
        val sentinelRef = sentinel.raw as HeapRef
        frame.assign(0, Value.Known(sourceRef, objectType))
        val session = invokeOnce(vm, ARRAY_CLONE, InvokeKind.VIRTUAL, frame = frame)
        val graph = session.run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val clone = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(clone is Value.Known, invocationDiagnostics(graph))
        clone as Value.Known
        val cloneRef = clone.raw as HeapRef
        assertNotEquals(sourceRef, cloneRef)
        assertNotEquals(sentinelRef, cloneRef)
        assertEquals(intArrayType, clone.type)
        assertEquals(Value.Known(77, intType), session.currentNode!!.frame.arrayAt(sentinelRef)!!.element(0))
        assertEquals(
            listOf(Value.Known(1, intType), Value.Unknown(intType), Value.Known(3, intType)),
            (session.currentNode!!.frame.arrayAt(cloneRef)!!).elements(),
        )

        val later = session.currentNode!!.frame.allocateArray(intArrayType, 0).raw as HeapRef
        assertEquals(cloneRef.id + 1, later.id, "the intrinsic must allocate through the session HeapIds")
    }

    @Test
    fun arrayAndObjectCloneUseOneTrackedArrayIntrinsic() {
        val vm = VmServices(classIndex)
        val registrations = vm.invocationServices.emulators.registrations()
            .filter { it.handler is ArrayCloneEmulator }
        assertEquals(1, registrations.size)
        assertSame(registrations.single(), vm.invocationServices.emulators.find(ARRAY_CLONE))

        for (target in listOf(ARRAY_CLONE, OBJECT_CLONE)) {
            val (session, sourceRef) = cloneSession(
                vm,
                target,
                arrayOf(Value.Known(4, intType), Value.Known(5, intType)),
            )
            val graph = session.run()
            val clone = (graph.outcomes.single() as Outcome.Returned).value
            assertTrue(clone is Value.Known, invocationDiagnostics(graph))
            val cloneRef = (clone as Value.Known).raw as HeapRef

            assertNotEquals(sourceRef, cloneRef, target.toString())
            assertEquals(
                session.currentNode!!.frame.arrayAt(sourceRef)!!.elements(),
                session.currentNode!!.frame.arrayAt(cloneRef)!!.elements(),
                target.toString(),
            )
        }
    }

    @Test
    fun mutatingACloneDoesNotChangeItsSource() {
        val vm = VmServices(classIndex)
        val (session, sourceRef) = cloneSession(
            vm,
            ARRAY_CLONE,
            arrayOf(Value.Known(7, intType), Value.Known(8, intType)),
        )
        val graph = session.run()
        val clone = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(clone is Value.Known, invocationDiagnostics(graph))
        val cloneRef = (clone as Value.Known).raw as HeapRef
        val terminal = session.currentNode!!.frame

        assertTrue(terminal.writeArrayElement(cloneRef, 0, Value.Known(99, intType)))
        assertEquals(Value.Known(7, intType), terminal.arrayAt(sourceRef)!!.element(0))
        assertEquals(Value.Known(99, intType), terminal.arrayAt(cloneRef)!!.element(0))
    }

    @Test
    fun unknownArrayCloneReturnsTypedUnknownWithoutAllocating() {
        val vm = VmServices(classIndex)
        val session = invokeOnce(vm, ARRAY_CLONE, InvokeKind.VIRTUAL, listOf(Value.Unknown(intArrayType)))
        val graph = session.run()

        assertEquals(listOf(Outcome.Returned(Value.Unknown(intArrayType))), graph.outcomes)
        assertEquals(0, session.currentNode!!.frame.nextHeapId())
        assertTrue(graph.addresses().flatMap(graph::nodePile).all { it.hostFailure == null })
    }

    @Test
    fun getClassUsesTheTrackedRuntimeTypeInsteadOfTheLooseRegisterTag() {
        val vm = VmServices(classIndex)
        val entry = callerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val runtimeType = VirtualType.of("Lhash_code;")
        val receiver = frame.allocateObject(runtimeType, classIndex.instanceFieldDefaults(runtimeType)!!)
        frame.assign(0, Value.Known(receiver.raw, objectType))
        val session = invokeOnce(vm, OBJECT_GET_CLASS, InvokeKind.VIRTUAL, frame = frame)

        val graph = session.run()
        val classToken = requireNotNull(classIndex.loadClassConstant(runtimeType.descriptor))

        assertEquals(
            Value.Known(classToken, classType),
            (graph.outcomes.single() as Outcome.Returned).value,
        )
    }

    @Test
    fun looseTaggedArrayGetClassThenComponentTypeUsesTheTrackedArrayIntrinsic() {
        val vm = VmServices(classIndex)
        val entry = wideCallerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val array = frame.allocateArray(intArrayType, 0)
        frame.assign(0, Value.Known(array.raw, objectType))
        val firstResume = entry.nextAddress(0)
        val secondResume = entry.nextAddress(firstResume)
        val session = ExecutionSession.of(entry, vm, Limits(), frame) { address, view, index ->
            when {
                view.signature != entry.signature -> OpBuilder.build(address, view, index)
                address == 0 -> invokeOp(address, OBJECT_GET_CLASS, intArrayOf(0), InvokeKind.VIRTUAL)
                address == firstResume -> invokeComponentTypeFromResult(address)
                address == secondResume -> returnResultOp(address)
                else -> OpBuilder.build(address, view, index)
            }
        }

        val graph = session.run()

        assertEquals(
            Value.Known(Int::class.javaPrimitiveType, classType),
            (graph.outcomes.single() as Outcome.Returned).value,
        )
        assertEquals(Value.Known(IntArray::class.java, classType), session.currentNode!!.frame.peek(1))
        val registration = vm.invocationServices.emulators.find(ARRAY_GET_CLASS)
        assertTrue(registration?.handler is ObjectGetClassEmulator)
        assertNull(
            vm.invocationServices.emulators.find(
                MethodDescriptor(VirtualType.of("Lhash_code;"), "getClass", "()Ljava/lang/Class;"),
            ),
        )
    }

    @Test
    fun unknownGetClassReturnsUnknownClassInsteadOfDeclaredObjectClass() {
        val vm = VmServices(classIndex)
        val graph = invokeOnce(
            vm,
            OBJECT_GET_CLASS,
            InvokeKind.VIRTUAL,
            listOf(Value.Unknown(objectType)),
        ).run()

        assertEquals(listOf(Outcome.Returned(Value.Unknown(classType))), graph.outcomes)
    }

    @Test
    fun classGetComponentTypeReturnsPrimitiveAndReferenceClassTokens() {
        val vm = VmServices(classIndex)

        val primitive = invokeOnce(
            vm,
            CLASS_GET_COMPONENT_TYPE,
            InvokeKind.VIRTUAL,
            listOf(Value.Known(IntArray::class.java, classType)),
        ).run()
        val reference = invokeOnce(
            vm,
            CLASS_GET_COMPONENT_TYPE,
            InvokeKind.VIRTUAL,
            listOf(Value.Known(Array<String>::class.java, classType)),
        ).run()

        assertEquals(
            Value.Known(Int::class.javaPrimitiveType, classType),
            (primitive.outcomes.single() as Outcome.Returned).value,
        )
        assertEquals(
            Value.Known(String::class.java, classType),
            (reference.outcomes.single() as Outcome.Returned).value,
        )
    }

    @Test
    fun classGetComponentTypeReturnsTypedNullForANonArrayClass() {
        val vm = VmServices(classIndex)
        val graph = invokeOnce(
            vm,
            CLASS_GET_COMPONENT_TYPE,
            InvokeKind.VIRTUAL,
            listOf(Value.Known(String::class.java, classType)),
        ).run()

        assertEquals(
            Value.Known(null, classType),
            (graph.outcomes.single() as Outcome.Returned).value,
        )
    }

    @Test
    fun unknownClassGetComponentTypeReturnsUnknownClassInsteadOfNull() {
        val vm = VmServices(classIndex)
        val graph = invokeOnce(
            vm,
            CLASS_GET_COMPONENT_TYPE,
            InvokeKind.VIRTUAL,
            listOf(Value.Unknown(classType)),
        ).run()

        assertEquals(listOf(Outcome.Returned(Value.Unknown(classType))), graph.outcomes)
    }

    @Test
    fun objectInitializationCompletesASubtypeWithoutReplacingItsAliasesOrAllocating() {
        val vm = VmServices(classIndex)
        val entry = wideCallerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val subtype = VirtualType.of("Lhash_code;")
        val receiver = frame.allocateObject(subtype, classIndex.instanceFieldDefaults(subtype)!!)
        val receiverRef = receiver.raw as HeapRef
        frame.assign(0, receiver)
        frame.assign(1, Value.Known(receiverRef, objectType))
        val nextHeapId = frame.nextHeapId()
        val session = invokeOnce(vm, OBJECT_INIT, InvokeKind.DIRECT, frame = frame)

        val graph = session.run()
        val terminal = session.currentNode!!.frame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertEquals(receiverRef, (terminal.peek(0) as Value.Known).raw)
        assertEquals(receiverRef, (terminal.peek(1) as Value.Known).raw)
        assertEquals(nextHeapId, terminal.nextHeapId())
        val initialized = terminal.heapObject(receiverRef) as HeapObject.TrackedObject
        assertFalse(initialized.uninitialized)
        assertNull(terminal.hostObjectAt(receiverRef), "Object.<init> must not replace the tracked receiver")
    }

    @Test
    fun enumConstructionPreservesTrackedReceiverIdentity() {
        val vm = VmServices(classIndex)
        val entry = wideCallerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val enumType = VirtualType.of("Lextends_enum;")
        val receiver = frame.allocateObject(enumType, classIndex.instanceFieldDefaults(enumType)!!)
        val receiverRef = receiver.raw as HeapRef
        frame.assign(0, receiver)
        frame.assign(1, Value.Known("not-a-canonical-enum-name", VirtualType.of("Ljava/lang/String;")))
        frame.assign(2, Value.Known(0, intType))
        frame.assign(3, Value.Known(9, intType))
        val target = method(enumType.descriptor, "<init>", "(Ljava/lang/String;II)V")
        val session = invokeOnce(
            vm,
            target,
            InvokeKind.DIRECT,
            frame = frame,
            registers = intArrayOf(0, 1, 2, 3),
        )

        val graph = session.run()
        val terminal = session.currentNode!!.frame

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(receiver, terminal.peek(0))
        val initialized = terminal.heapObject(receiverRef) as HeapObject.TrackedObject
        assertFalse(initialized.uninitialized)
        assertEquals(
            Value.Known("not-a-canonical-enum-name", stringType),
            initialized.field(ENUM_NAME_FIELD),
        )
        assertEquals(Value.Known(0, intType), initialized.field(ENUM_ORDINAL_FIELD))
        assertEquals(Value.Known(9, intType), initialized.field("Lextends_enum;->value:I"))
        assertNull(terminal.hostObjectAt(receiverRef), "enum construction must not install a host enum constant")
        assertTrue(vm.invocationServices.emulators.find(ENUM_INIT)!!.handler is EnumInitEmulator)
    }

    @Test
    fun enumInitializationWritesUnknownInheritedFieldsConservatively() {
        val vm = VmServices(classIndex)
        val entry = wideCallerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val enumType = VirtualType.of("Lextends_enum;")
        val receiver = frame.allocateObject(enumType, classIndex.instanceFieldDefaults(enumType)!!)
        val receiverRef = receiver.raw as HeapRef
        frame.assign(0, receiver)
        frame.assign(1, Value.Unknown(stringType))
        frame.assign(2, Value.Unknown(intType))
        val session = invokeOnce(
            vm,
            ENUM_INIT,
            InvokeKind.DIRECT,
            frame = frame,
            registers = intArrayOf(0, 1, 2),
        )

        val graph = session.run()
        val initialized = session.currentNode!!.frame.heapObject(receiverRef) as HeapObject.TrackedObject

        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertFalse(initialized.uninitialized)
        assertEquals(Value.Unknown(stringType), initialized.field(ENUM_NAME_FIELD))
        assertEquals(Value.Unknown(intType), initialized.field(ENUM_ORDINAL_FIELD))
    }

    @Test
    fun distinctEnumReceiversRemainIsolatedInBothConstructionOrders() {
        for (order in listOf(listOf(0, 1), listOf(1, 0))) {
            val vm = VmServices(classIndex)
            val entry = wideCallerView()
            var frame = Frame.entryFrame(entry, vm.hostObjectCloner)
            val enumType = VirtualType.of("Lextends_enum;")
            val refs = List(2) {
                frame.allocateObject(enumType, classIndex.instanceFieldDefaults(enumType)!!).raw as HeapRef
            }
            val names = listOf("first-not-canonical", "second-not-canonical")
            val ordinals = listOf(3, 11)

            for (index in order) {
                frame.assign(0, Value.Known(refs[index], enumType))
                frame.assign(1, Value.Known(names[index], stringType))
                frame.assign(2, Value.Known(ordinals[index], intType))
                val session = invokeOnce(
                    vm,
                    ENUM_INIT,
                    InvokeKind.DIRECT,
                    frame = frame,
                    registers = intArrayOf(0, 1, 2),
                )
                val graph = session.run()
                assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
                frame = session.currentNode!!.frame
            }

            for (index in refs.indices) {
                val initialized = frame.heapObject(refs[index]) as HeapObject.TrackedObject
                assertFalse(initialized.uninitialized)
                assertEquals(Value.Known(names[index], stringType), initialized.field(ENUM_NAME_FIELD))
                assertEquals(Value.Known(ordinals[index], intType), initialized.field(ENUM_ORDINAL_FIELD))
                assertNull(frame.hostObjectAt(refs[index]))
            }
            assertNotEquals(refs[0], refs[1])
        }
    }

    @Test
    fun sameNameEnumsOfDistinctRuntimeTypesRemainIsolatedInBothConstructionOrders() {
        for (order in listOf(listOf(0, 1), listOf(1, 0))) {
            val vm = VmServices(classIndex)
            val entry = wideCallerView()
            var frame = Frame.entryFrame(entry, vm.hostObjectCloner)
            val enumTypes = listOf(
                VirtualType.of("Lextends_enum;"),
                VirtualType.of("Lextends_enum_obfuscated;"),
            )
            val refs = enumTypes.map { enumType ->
                frame.allocateObject(enumType, classIndex.instanceFieldDefaults(enumType)!!).raw as HeapRef
            }
            val name = "NONE"
            val ordinals = listOf(5, 12)
            refs.forEachIndexed { index, ref ->
                frame.assign(3 + index, Value.Known(ref, enumTypes[index]))
            }

            for (index in order) {
                frame.assign(0, Value.Known(refs[index], enumTypes[index]))
                frame.assign(1, Value.Known(name, stringType))
                frame.assign(2, Value.Known(ordinals[index], intType))
                val session = invokeOnce(
                    vm,
                    ENUM_INIT,
                    InvokeKind.DIRECT,
                    frame = frame,
                    registers = intArrayOf(0, 1, 2),
                )
                val graph = session.run()
                assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
                frame = session.currentNode!!.frame
            }

            for (index in refs.indices) {
                assertEquals(Value.Known(refs[index], enumTypes[index]), frame.peek(3 + index))
                val initialized = frame.heapObject(refs[index]) as HeapObject.TrackedObject
                assertEquals(enumTypes[index], initialized.type)
                assertEquals(Value.Known(name, stringType), initialized.field(ENUM_NAME_FIELD))
                assertEquals(Value.Known(ordinals[index], intType), initialized.field(ENUM_ORDINAL_FIELD))
                assertNull(frame.hostObjectAt(refs[index]))
            }
        }
    }

    @Test
    fun intrinsicRegistrationsArePerVmAndDeclareCapabilitiesAndEffects() {
        val first = VmServices(classIndex)
        val second = VmServices(classIndex)
        assertNotSame(first.invocationServices.emulators, second.invocationServices.emulators)

        val expected = mapOf(
            ARRAY_CLONE to EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            OBJECT_GET_CLASS to EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
            CLASS_GET_COMPONENT_TYPE to EmulatorCapability(handlesUnknownArguments = true),
            OBJECT_INIT to EmulatorCapability(handlesUnknownArguments = false, receivesTrackedReferences = true),
            ENUM_INIT to EmulatorCapability(handlesUnknownArguments = true, receivesTrackedReferences = true),
        )
        for ((target, capability) in expected) {
            val firstRegistration = first.invocationServices.emulators.find(target)!!
            val secondRegistration = second.invocationServices.emulators.find(target)!!
            assertEquals(capability, firstRegistration.capability, target.toString())
            assertEquals(SideEffectLevel.NONE, firstRegistration.effect, target.toString())
            assertNotSame(firstRegistration.handler, secondRegistration.handler, target.toString())
        }
    }

    private fun cloneSession(
        vm: VmServices,
        target: MethodDescriptor,
        elements: Array<Value>,
    ): Pair<ExecutionSession, HeapRef> {
        val entry = callerView()
        val frame = Frame.entryFrame(entry, vm.hostObjectCloner)
        val source = frame.allocateArray(intArrayType, elements)
        val sourceRef = source.raw as HeapRef
        frame.assign(0, Value.Known(sourceRef, objectType))
        return invokeOnce(vm, target, InvokeKind.VIRTUAL, frame = frame) to sourceRef
    }

    private fun invokeOnce(
        vm: VmServices,
        target: MethodDescriptor,
        kind: InvokeKind,
        values: List<Value> = emptyList(),
        frame: Frame? = null,
        registers: IntArray = intArrayOf(0),
    ): ExecutionSession {
        val entry = frame?.let { if (it.registerCount > 1) wideCallerView() else callerView() } ?: callerView()
        val entryFrame = frame ?: Frame.entryFrame(entry, vm.hostObjectCloner).also { seeded ->
            values.forEachIndexed(seeded::assign)
        }
        val resumeAddress = entry.nextAddress(0)
        return ExecutionSession.of(entry, vm, Limits(), entryFrame) { address, view, index ->
            when {
                view.signature != entry.signature -> OpBuilder.build(address, view, index)
                address == 0 -> invokeOp(address, target, registers, kind)
                address == resumeAddress -> returnResultOp(address)
                else -> OpBuilder.build(address, view, index)
            }
        }
    }

    private fun invokeOp(
        address: Int,
        target: MethodDescriptor,
        registers: IntArray,
        kind: InvokeKind,
    ) = object : Op(address) {
        override val name = "invoke-intrinsic-test"
        override fun execute(frame: Frame): OpExecution =
            listOf(Transition.Invoke(target, registers, kind)).asOpExecution()
    }

    private fun returnResultOp(address: Int) = object : Op(address) {
        override val name = "return-intrinsic-test-result"
        override fun execute(frame: Frame): OpExecution =
            listOf(Transition.Return(frame.takeResult())).asOpExecution()
    }

    private fun invokeComponentTypeFromResult(address: Int) = object : Op(address) {
        override val name = "invoke-component-type-from-result"
        override fun execute(frame: Frame): OpExecution {
            frame.assign(1, requireNotNull(frame.takeResult()))
            return listOf(Transition.Invoke(CLASS_GET_COMPONENT_TYPE, intArrayOf(1), InvokeKind.VIRTUAL))
                .asOpExecution()
        }
    }

    private fun callerView(): MethodView = view(CALLER, "callsCalleeAndReturnsItsResult()I")

    private fun wideCallerView(): MethodView = view("Linvoke_virtual_test;", "invokeRangeManyParameters()V")

    private fun view(className: String, descriptor: String): MethodView =
        classIndex.requireMethodView(className, descriptor)

    private fun method(definingClass: String, name: String, proto: String) =
        MethodDescriptor(VirtualType.of(definingClass), name, proto)

    private fun invocationDiagnostics(graph: ExecutionGraph): String = graph.addresses()
        .flatMap(graph::nodePile)
        .joinToString { "${it.address}:${it.invocationDiagnostic}:${it.sideEffectLevel}:${it.hostFailure}" }

    private companion object {
        const val CALLER = "Lcalls/caller;"

        val ARRAY_CLONE = MethodDescriptor(VirtualType.of("[I"), "clone", "()Ljava/lang/Object;")
        val OBJECT_CLONE = MethodDescriptor(VirtualType.OBJECT, "clone", "()Ljava/lang/Object;")
        val OBJECT_GET_CLASS = MethodDescriptor(
            VirtualType.OBJECT,
            "getClass",
            "()Ljava/lang/Class;",
        )
        val ARRAY_GET_CLASS = MethodDescriptor(
            VirtualType.of("[I"),
            "getClass",
            "()Ljava/lang/Class;",
        )
        val CLASS_GET_COMPONENT_TYPE = MethodDescriptor(
            VirtualType.of("Ljava/lang/Class;"),
            "getComponentType",
            "()Ljava/lang/Class;",
        )
        val OBJECT_INIT = MethodDescriptor(VirtualType.OBJECT, "<init>", "()V")
        val ENUM_INIT = MethodDescriptor(
            VirtualType.of("Ljava/lang/Enum;"),
            "<init>",
            "(Ljava/lang/String;I)V",
        )
        const val ENUM_NAME_FIELD = "Ljava/lang/Enum;->name:Ljava/lang/String;"
        const val ENUM_ORDINAL_FIELD = "Ljava/lang/Enum;->ordinal:I"
    }
}
