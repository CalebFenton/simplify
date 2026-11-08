package org.cf.smalivm.core

import java.lang.reflect.Modifier
import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ReflectedMemberQueryTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun reportsHeapBackedMethodByFullDescriptorAndModifiers() {
        val graph = graphOf("getStringLengthMethod()Ljava/lang/reflect/Method;")
        val address = graph.methodView.addresses.single { graph.methodView.opcodeAt(it) == Opcode.MOVE_RESULT_OBJECT }
        val expected = ReflectedMethodMetadata(
            "Ljava/lang/String;->length()I",
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
            "Lorg/cf/smalivm/DummyClass;->publicInstanceField:I",
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
                "Ljava/lang/String;->length()I",
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
        val same = execute(METHOD_ON_EITHER_BRANCH, 1 to string("length"), 2 to string("length"))
        val join = same.addressOf(Opcode.NOP)
        val sameMoveResults = same.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(2, same.nodePile(join).size, "both branches reach the join")
        assertEquals(LENGTH, same.reflectedMethodOverExploredPaths(join, 0))
        assertEquals(LENGTH, same.trusted()!!.reflectedMethod(join, 0))
        assertEquals(LENGTH, same.reflectedMethodOverExploredPaths(sameMoveResults, 0))
        assertEquals(LENGTH, same.trusted()!!.reflectedMethod(intArrayOf(sameMoveResults[0], join), 0))

        val different = execute(METHOD_ON_EITHER_BRANCH, 1 to string("length"), 2 to string("isEmpty"))
        val moveResults = different.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(LENGTH, different.reflectedMethodOverExploredPaths(moveResults[0], 0), "one branch alone agrees")
        assertNull(different.reflectedMethodOverExploredPaths(join, 0))
        assertNull(different.trusted()!!.reflectedMethod(join, 0))
        assertNull(different.reflectedMethodOverExploredPaths(moveResults, 0))
        assertNull(different.trusted()!!.reflectedMethod(moveResults, 0))

        val memberAndAbsent = execute(MEMBERS_ON_ONE_BRANCH)
        val methodMoveResult = memberAndAbsent.addressesOf(Opcode.MOVE_RESULT_OBJECT).first()
        val oneBranchJoin = memberAndAbsent.addressOf(Opcode.NOP)
        assertEquals(LENGTH, memberAndAbsent.reflectedMethodOverExploredPaths(methodMoveResult, 0))
        assertNull(memberAndAbsent.reflectedMethodOverExploredPaths(oneBranchJoin, 0))
        assertNull(memberAndAbsent.trusted()!!.reflectedMethod(oneBranchJoin, 0))
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

    /**
     * The loop's revisit of the nop is stopped by the address limit before it runs, and its frame holds
     * String.isEmpty. Only the executed first visit, holding String.length, may vote.
     */
    @Test
    fun anUnexecutedNodeDoesNotVote() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "methodChangesOnLoopRevisit()V",
            limits = Limits(maxAddressVisits = 1, maxSteps = 100),
        )
        val loop = graph.addressOf(Opcode.NOP)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertEquals(2, graph.nodePile(loop).size, "the executed visit and the limit-stopped revisit are both piled")

        assertEquals(LENGTH, graph.reflectedMethodOverExploredPaths(loop, 0))
    }

    @Test
    fun methodDescriptorIncludesPrimitiveReferenceArrayAndVoidForms() {
        val method = ReflectedMethodDescriptorFixture::class.java.declaredMethods.single { it.name == "allTypes" }

        assertEquals(
            "Lorg/cf/smalivm/core/ReflectedMethodDescriptorFixture;->" +
                "allTypes(ZBCSIJFDLjava/lang/Object;[I[[Ljava/lang/String;)V",
            method.toReflectedMethodMetadataOrNull()?.descriptor,
        )
    }

    @Test
    fun fieldDisagreementNonMembersAndAbsentRegistersReturnNoMetadata() {
        val same = execute(FIELD_ON_EITHER_BRANCH, 1 to string("publicInstanceField"), 2 to string("publicInstanceField"))
        val join = same.addressOf(Opcode.NOP)
        val sameMoveResults = same.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(2, same.nodePile(join).size, "both branches reach the join")
        assertEquals(PUBLIC_FIELD, same.reflectedFieldOverExploredPaths(join, 0))
        assertEquals(PUBLIC_FIELD, same.reflectedFieldOverExploredPaths(sameMoveResults, 0))
        assertEquals(PUBLIC_FIELD, same.trusted()!!.reflectedField(intArrayOf(sameMoveResults[0], join), 0))

        val different = execute(
            FIELD_ON_EITHER_BRANCH,
            1 to string("publicInstanceField"),
            2 to string("protectedInstanceField"),
        )
        val moveResults = different.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(PUBLIC_FIELD, different.reflectedFieldOverExploredPaths(moveResults[0], 0), "one branch alone agrees")
        assertNull(different.reflectedFieldOverExploredPaths(join, 0))
        assertNull(different.reflectedFieldOverExploredPaths(moveResults, 0))
        assertNull(different.trusted()!!.reflectedField(moveResults, 0))

        val memberAndAbsent = execute(MEMBERS_ON_ONE_BRANCH)
        val fieldMoveResult = memberAndAbsent.addressesOf(Opcode.MOVE_RESULT_OBJECT).last()
        val oneBranchJoin = memberAndAbsent.addressOf(Opcode.NOP)
        assertEquals(PUBLIC_FIELD, memberAndAbsent.reflectedFieldOverExploredPaths(fieldMoveResult, 1))
        assertNull(memberAndAbsent.reflectedFieldOverExploredPaths(oneBranchJoin, 1))
        assertNull(memberAndAbsent.trusted()!!.reflectedField(oneBranchJoin, 1))

        // v1 holds the String class constant and v2 a tracked Class[]: values, but not members.
        val nonMembers = execute(METHOD_ON_EITHER_BRANCH, 1 to string("length"), 2 to string("length"))
        val nonMemberJoin = nonMembers.addressOf(Opcode.NOP)
        val branch = nonMembers.addressOf(Opcode.IF_EQZ)
        for (register in 1..2) {
            assertNull(nonMembers.reflectedMethodOverExploredPaths(nonMemberJoin, register))
            assertNull(nonMembers.reflectedFieldOverExploredPaths(nonMemberJoin, register))
        }
        assertNull(nonMembers.reflectedMethodOverExploredPaths(branch, 0), "v0 is unassigned before the branch")
        assertNull(nonMembers.reflectedFieldOverExploredPaths(branch, 0))
        assertNull(nonMembers.reflectedMethodOverExploredPaths(999, 0), "no such address")
        assertNull(nonMembers.reflectedFieldOverExploredPaths(999, 0))
    }

    @Test
    fun mixedMemberKindsReturnNoMetadata() {
        val graph = execute("methodOrField(Z)V")
        val join = graph.addressOf(Opcode.NOP)
        val moveResults = graph.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(LENGTH, graph.reflectedMethodOverExploredPaths(moveResults[0], 0), "the method branch alone")
        assertEquals(PUBLIC_FIELD, graph.reflectedFieldOverExploredPaths(moveResults[1], 0), "the field branch alone")

        assertNull(graph.reflectedMethodOverExploredPaths(join, 0))
        assertNull(graph.reflectedFieldOverExploredPaths(join, 0))
    }

    @Test
    fun arrayLengthRequiresTrackedArraysOfOneLengthOnEveryPath() {
        val same = execute(ARRAY_ON_EITHER_BRANCH, 1 to int(3), 2 to int(3))
        val join = intArrayOf(same.addressOf(Opcode.NOP))
        assertEquals(2, same.nodePile(join.single()).size, "both branches reach the join")

        assertEquals(3, same.arrayLengthOverExploredPaths(join, 0))
        assertEquals(3, same.trusted()!!.arrayLength(join, 0))

        val different = execute(ARRAY_ON_EITHER_BRANCH, 1 to int(2), 2 to int(3))
        assertNull(different.arrayLengthOverExploredPaths(join, 0))
        assertNull(different.trusted()!!.arrayLength(join, 0))
    }

    /**
     * Per-address member queries read every activation of the method, including recursive ones: the
     * entry activation alone holds String.length and publicInstanceField, the recursive one isEmpty and
     * protectedInstanceField at the same addresses, so neither query can name one member.
     */
    @Test
    fun recursiveActivationValuesVoteInPerAddressMemberQueries() {
        val recursive = execute(RECURSIVE_LOOKUP, 0 to int(1))
        val (fieldMoveResult, methodMoveResult) = recursive.addressesOf(Opcode.MOVE_RESULT_OBJECT)
        assertEquals(2, recursive.nodePile(methodMoveResult).size, "the recursive activation reached the lookup")

        assertNull(recursive.reflectedMethodOverExploredPaths(methodMoveResult, 0))
        assertNull(recursive.trusted()!!.reflectedMethod(methodMoveResult, 0))
        assertNull(recursive.reflectedFieldOverExploredPaths(fieldMoveResult, 3))
        assertNull(recursive.trusted()!!.reflectedField(fieldMoveResult, 3))

        val leaf = execute(RECURSIVE_LOOKUP, 0 to int(0))
        assertEquals(
            ReflectedMethodMetadata(
                "Ljava/lang/String;->isEmpty()Z",
                String::class.java.getDeclaredMethod("isEmpty").modifiers,
                String::class.java.modifiers,
            ),
            leaf.reflectedMethodOverExploredPaths(methodMoveResult, 0),
            "without recursion the one activation names its method",
        )
        assertEquals(
            ReflectedFieldMetadata(
                "Lorg/cf/smalivm/DummyClass;->protectedInstanceField:I",
                Modifier.PROTECTED or Modifier.FINAL,
            ),
            leaf.reflectedFieldOverExploredPaths(fieldMoveResult, 3),
        )
    }

    private fun graphOf(descriptor: String): ExecutionGraph =
        CoreTester.vmServices.newSession(classIndex.requireMethodView("Lmethod_reflector_test;", descriptor)).run()

    /** Runs [descriptor] of [CLASS_NAME], seeding each `(parameter index, value)` pair. */
    private fun execute(descriptor: String, vararg parameterSeeds: Pair<Int, Value>): ExecutionGraph {
        val view = classIndex.requireMethodView(CLASS_NAME, descriptor)
        val seeds = parameterSeeds.associate { (index, value) -> view.parameters[index].register to value }
        val graph = CoreTester.execute(CLASS_NAME, descriptor, seeds)
        assertEquals(CompletionStatus.Complete, graph.completion())
        return graph
    }

    private fun ExecutionGraph.addressesOf(opcode: Opcode): IntArray =
        methodView.addresses.filter { methodView.opcodeAt(it) == opcode }.toIntArray()

    private fun ExecutionGraph.addressOf(opcode: Opcode): Int = addressesOf(opcode).single()

    private fun string(raw: String) = Value.Known(raw, VirtualType.of("Ljava/lang/String;"))

    private fun int(raw: Int) = Value.Known(raw, VirtualType.of("I"))

    private companion object {
        const val CLASS_NAME = "Lreflected_member_lookups;"
        const val METHOD_ON_EITHER_BRANCH = "methodOnEitherBranch(ZLjava/lang/String;Ljava/lang/String;)V"
        const val FIELD_ON_EITHER_BRANCH = "fieldOnEitherBranch(ZLjava/lang/String;Ljava/lang/String;)V"
        const val MEMBERS_ON_ONE_BRANCH = "membersOnOneBranch(Z)V"
        const val ARRAY_ON_EITHER_BRANCH = "arrayOnEitherBranch(ZII)V"
        const val RECURSIVE_LOOKUP = "recursiveLookup(I)V"

        val LENGTH = ReflectedMethodMetadata(
            "Ljava/lang/String;->length()I",
            String::class.java.getDeclaredMethod("length").modifiers,
            String::class.java.modifiers,
        )
        val PUBLIC_FIELD = ReflectedFieldMetadata(
            "Lorg/cf/smalivm/DummyClass;->publicInstanceField:I",
            Modifier.PUBLIC or Modifier.FINAL,
        )
    }
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
