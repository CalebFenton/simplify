package org.cf.smalivm.core

import java.lang.reflect.Modifier
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ReflectedMemberQueryTest {
    private val classIndex = CoreTester.classIndex
    private val methodType = VirtualType.of("Ljava/lang/reflect/Method;")
    private val intType = VirtualType.of("I")
    private val STRING_LENGTH = ImmutableMethodReference("Ljava/lang/String;", "length", emptyList<String>(), "I")

    @Test
    fun reportsHeapBackedMethodByFullDescriptorAndModifiers() {
        val graph = graphOf("getStringLengthMethod()Ljava/lang/reflect/Method;")
        val address = graph.methodView.addresses.single { graph.methodView.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT }
        val expected = ReflectedMethodMetadata(
            STRING_LENGTH,
            String::class.java.getDeclaredMethod("length").modifiers,
            String::class.java.modifiers,
        )

        assertEquals(expected, graph.reflectedMethodOverExploredPaths(address, 0))
        assertEquals(expected, graph.trusted()!!.reflectedMethod(address, 0))
    }

    @Test
    fun reportsTrackedFieldByFullDescriptorAndModifiers() {
        val graph = graphOf("getPublicInstanceField()Ljava/lang/reflect/Field;")
        val address = graph.methodView.addresses.single { graph.methodView.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT }
        val expected = ReflectedFieldMetadata(
            ImmutableFieldReference("Lorg/cf/smalivm/DummyClass;", "publicInstanceField", "I"),
            Modifier.PUBLIC or Modifier.FINAL,
        )

        assertEquals(expected, graph.reflectedFieldOverExploredPaths(address, 0))
        assertEquals(expected, graph.trusted()!!.reflectedField(address, 0))
    }

    @Test
    fun parentAddressSetRetainsMethodBeforeReflectiveInvocation() {
        val graph = graphOf("invokeStringLengthReflectively()Ljava/lang/Object;")
        val invokeAddress = graph.methodView.addresses
            .filter { graph.methodView.opcodeAt(it) == Opcode.INVOKE_VIRTUAL }
            .last()
        val parents = graph.parentAddresses(invokeAddress)

        assertEquals(1, parents.size)
        assertEquals(Opcode.CONST_4, graph.methodView.opcodeAt(parents.single()))
        assertEquals(
            ReflectedMethodMetadata(
                STRING_LENGTH,
                String::class.java.getDeclaredMethod("length").modifiers,
                String::class.java.modifiers,
            ),
            graph.reflectedMethodOverExploredPaths(parents, 0),
        )
        assertEquals(
            graph.reflectedMethodOverExploredPaths(parents, 0),
            requireNotNull(graph.trusted()).reflectedMethod(parents, 0),
        )
    }

    @Test
    fun singleAndMultipleAddressesRequireEveryPathToHoldTheSameMethod() {
        val view = viewOf("moveChain()I")
        val secondAddress = view.addresses[1]
        val length = String::class.java.getDeclaredMethod("length")
        val isEmpty = String::class.java.getDeclaredMethod("isEmpty")
        val same = completeGraphOf(
            view,
            nodeAt(view, 0, methodFrame(view, length)),
            nodeAt(view, 0, methodFrame(view, length)),
            nodeAt(view, secondAddress, methodFrame(view, length)),
        )
        val expected = ReflectedMethodMetadata(
            STRING_LENGTH,
            length.modifiers,
            String::class.java.modifiers,
        )

        assertEquals(expected, same.reflectedMethodOverExploredPaths(0, 0))
        assertEquals(expected, same.reflectedMethodOverExploredPaths(intArrayOf(0, secondAddress), 0))
        assertEquals(expected, same.trusted()!!.reflectedMethod(intArrayOf(0, secondAddress), 0))

        val different = completeGraphOf(
            view,
            nodeAt(view, 0, methodFrame(view, length)),
            nodeAt(view, secondAddress, methodFrame(view, isEmpty)),
        )
        assertNull(different.reflectedMethodOverExploredPaths(intArrayOf(0, secondAddress), 0))
        assertNull(different.trusted()!!.reflectedMethod(intArrayOf(0, secondAddress), 0))

        val memberAndAbsent = completeGraphOf(
            view,
            nodeAt(view, 0, methodFrame(view, length)),
            nodeAt(view, 0, Frame.entryFrame(view)),
        )
        assertNull(memberAndAbsent.reflectedMethodOverExploredPaths(0, 0))
        assertNull(memberAndAbsent.trusted()!!.reflectedMethod(0, 0))
    }

    @Test
    fun branchedParentsDisagreeAboutTheMethod() {
        val graph = graphOf("invokeOneOfTwoStringMethods(Z)Ljava/lang/Object;")
        val invokeAddress = graph.methodView.addresses
            .filter { graph.methodView.opcodeAt(it) == Opcode.INVOKE_VIRTUAL }
            .last()
        val parents = graph.parentAddresses(invokeAddress)

        assertEquals(2, parents.size)
        assertNull(graph.reflectedMethodOverExploredPaths(parents, 0))
        assertNull(requireNotNull(graph.trusted()).reflectedMethod(parents, 0))
    }

    @Test
    fun anUnexecutedNodeDoesNotVote() {
        val view = viewOf("moveChain()I")
        val length = String::class.java.getDeclaredMethod("length")
        val executed = nodeAt(view, 0, methodFrame(view, length))
        val notExecuted = nodeAt(view, 0, methodFrame(view, String::class.java.getDeclaredMethod("isEmpty")))
        val graph = completeGraphOf(view, listOf(executed, notExecuted), setOf(executed))

        assertEquals(
            ReflectedMethodMetadata(
                STRING_LENGTH,
                length.modifiers,
                String::class.java.modifiers,
            ),
            graph.reflectedMethodOverExploredPaths(0, 0),
        )
    }

    @Test
    fun methodDescriptorIncludesPrimitiveReferenceArrayAndVoidForms() {
        val view = viewOf("moveChain()I")
        val method = ReflectedMethodDescriptorFixture::class.java.declaredMethods.single { it.name == "allTypes" }
        val graph = completeGraphOf(view, nodeAt(view, 0, methodFrame(view, method)))

        assertEquals(
            "Lorg/cf/smalivm/core/ReflectedMethodDescriptorFixture;->" +
                "allTypes(ZBCSIJFDLjava/lang/Object;[I[[Ljava/lang/String;)V",
            graph.reflectedMethodOverExploredPaths(0, 0)?.method?.toString(),
        )
    }

    @Test
    fun fieldDisagreementNonMembersAndAbsentRegistersReturnNoMetadata() {
        val view = viewOf("moveChain()I")
        val secondAddress = view.addresses[1]
        val firstField = trackedField("first")
        val secondField = trackedField("second")
        val sameFields = completeGraphOf(
            view,
            nodeAt(view, 0, fieldFrame(view, firstField)),
            nodeAt(view, secondAddress, fieldFrame(view, firstField)),
        )
        val expected = ReflectedFieldMetadata(
            ImmutableFieldReference(firstField.declaringType.descriptor, firstField.name, firstField.fieldType.descriptor),
            firstField.modifiers,
        )
        assertEquals(expected, sameFields.reflectedFieldOverExploredPaths(0, 0))
        assertEquals(expected, sameFields.reflectedFieldOverExploredPaths(intArrayOf(0, secondAddress), 0))
        assertEquals(expected, sameFields.trusted()!!.reflectedField(intArrayOf(0, secondAddress), 0))

        val differentFields = completeGraphOf(
            view,
            nodeAt(view, 0, fieldFrame(view, firstField)),
            nodeAt(view, secondAddress, fieldFrame(view, secondField)),
        )
        assertNull(differentFields.reflectedFieldOverExploredPaths(intArrayOf(0, secondAddress), 0))
        assertNull(differentFields.trusted()!!.reflectedField(intArrayOf(0, secondAddress), 0))

        val memberAndAbsent = completeGraphOf(
            view,
            nodeAt(view, 0, fieldFrame(view, firstField)),
            nodeAt(view, 0, Frame.entryFrame(view)),
        )
        assertNull(memberAndAbsent.reflectedFieldOverExploredPaths(0, 0))
        assertNull(memberAndAbsent.trusted()!!.reflectedField(0, 0))

        val ordinary = Frame.entryFrame(view).also {
            val ref = it.allocateHostObject(VirtualType.OBJECT, Any())
            it.assign(0, Value.Known(ref, VirtualType.OBJECT))
        }
        val absent = Frame.entryFrame(view)
        val nonMembers = completeGraphOf(
            view,
            nodeAt(view, 0, ordinary),
            nodeAt(view, secondAddress, absent),
        )
        assertNull(nonMembers.reflectedMethodOverExploredPaths(0, 0))
        assertNull(nonMembers.reflectedFieldOverExploredPaths(0, 0))
        assertNull(nonMembers.reflectedMethodOverExploredPaths(secondAddress, 0))
        assertNull(nonMembers.reflectedFieldOverExploredPaths(secondAddress, 0))
        assertNull(nonMembers.reflectedMethodOverExploredPaths(99, 0))
        assertNull(nonMembers.reflectedFieldOverExploredPaths(99, 0))
    }

    @Test
    fun mixedMemberKindsReturnNoMetadata() {
        val view = viewOf("moveChain()I")
        val graph = completeGraphOf(
            view,
            nodeAt(view, 0, methodFrame(view, String::class.java.getDeclaredMethod("length"))),
            nodeAt(view, 0, fieldFrame(view, trackedField("answer"))),
        )

        assertNull(graph.reflectedMethodOverExploredPaths(0, 0))
        assertNull(graph.reflectedFieldOverExploredPaths(0, 0))
    }

    @Test
    fun arrayLengthRequiresTrackedArraysOfOneLengthOnEveryPath() {
        val view = viewOf("moveChain()I")
        val arrayType = VirtualType.of("[Ljava/lang/Object;")
        fun frame(length: Int) = Frame.entryFrame(view).also {
            it.assign(0, it.allocateArray(arrayType, length))
        }
        val same = completeGraphOf(
            view,
            nodeAt(view, 0, frame(3)),
            nodeAt(view, 0, frame(3)),
        )

        assertEquals(3, same.arrayLengthOverExploredPaths(intArrayOf(0), 0))
        assertEquals(3, same.trusted()!!.arrayLength(intArrayOf(0), 0))

        val different = completeGraphOf(
            view,
            nodeAt(view, 0, frame(2)),
            nodeAt(view, 0, frame(3)),
        )
        assertNull(different.arrayLengthOverExploredPaths(intArrayOf(0), 0))
        assertNull(different.trusted()!!.arrayLength(intArrayOf(0), 0))
    }

    @Test
    fun recursiveActivationValuesVoteInPerAddressMemberQueries() {
        val view = viewOf("moveChain()I")
        val entryMethod = nodeAt(view, 0, methodFrame(view, String::class.java.getDeclaredMethod("length")))
        val recursiveMethod = nodeAt(view, 0, methodFrame(view, String::class.java.getDeclaredMethod("isEmpty")))
        val entryField = nodeAt(view, view.addresses[1], fieldFrame(view, trackedField("first")))
        val recursiveField = nodeAt(view, view.addresses[1], fieldFrame(view, trackedField("second")))
        val nodes = listOf(entryMethod, recursiveMethod, entryField, recursiveField)
        val graph = ExecutionGraph(
            methodView = view,
            addressToNodes = nodes.groupBy { it.address },
            addressToNodesInThisRunsActivation = mapOf(0 to listOf(entryMethod), view.addresses[1] to listOf(entryField)),
            outcomes = emptyList(),
            completion = CompletionStatus.Complete,
            executedNodes = nodes.toSet(),
            superclasses = classIndex,
        )

        assertNull(graph.reflectedMethodOverExploredPaths(0, 0))
        assertNull(graph.trusted()!!.reflectedMethod(0, 0))
        assertNull(graph.reflectedFieldOverExploredPaths(view.addresses[1], 0))
        assertNull(graph.trusted()!!.reflectedField(view.addresses[1], 0))
    }

    private fun graphOf(descriptor: String): ExecutionGraph =
        ExecutionSession.of(classIndex.requireMethodView("Lmethod_reflector_test;", descriptor), classIndex).run()

    private fun viewOf(descriptor: String): MethodView =
        classIndex.requireMethodView("Lspine/spine;", descriptor)

    private fun methodFrame(view: MethodView, method: java.lang.reflect.Method): Frame = Frame.entryFrame(view).also {
        it.assign(0, it.admitReturnedHostValue(method, methodType))
    }

    private fun fieldFrame(view: MethodView, field: TrackedFieldValue): Frame = Frame.entryFrame(view).also {
        it.assign(0, it.allocateTrackedField(field))
    }

    private fun trackedField(name: String): TrackedFieldValue {
        val owner = VirtualType.of("Lreflected/member;")
        return TrackedFieldValue(
            owner,
            name,
            ClassIndex.fieldDescriptorOf(owner, name, intType.descriptor),
            intType,
            Modifier.PUBLIC or Modifier.STATIC,
            FieldVisibility.PUBLIC,
            FieldStorage.STATIC,
            TrackedFieldDomain.Virtual,
        )
    }

    private fun nodeAt(view: MethodView, address: Int, frame: Frame): ExecutionNode = ExecutionNode(
        address,
        OpBuilder.build(address, view, classIndex),
        frame,
        parent = null,
        activation = Activation.entry(view),
    )

    private fun completeGraphOf(view: MethodView, vararg nodes: ExecutionNode): ExecutionGraph = ExecutionGraph(
        methodView = view,
        addressToNodes = nodes.groupBy { it.address },
        outcomes = emptyList(),
        completion = CompletionStatus.Complete,
        executedNodes = nodes.toSet(),
        superclasses = classIndex,
    )

    private fun completeGraphOf(
        view: MethodView,
        nodes: List<ExecutionNode>,
        executedNodes: Set<ExecutionNode>,
    ): ExecutionGraph = ExecutionGraph(
        methodView = view,
        addressToNodes = nodes.groupBy { it.address },
        outcomes = emptyList(),
        completion = CompletionStatus.Complete,
        executedNodes = executedNodes,
        superclasses = classIndex,
    )
}

private class ReflectedMethodDescriptorFixture {
    companion object {
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun allTypes(
            booleanValue: Boolean,
            byteValue: Byte,
            charValue: Char,
            shortValue: Short,
            intValue: Int,
            longValue: Long,
            floatValue: Float,
            doubleValue: Double,
            referenceValue: Any,
            primitiveArray: IntArray,
            referenceArray: Array<Array<String>>,
        ) = Unit
    }
}
