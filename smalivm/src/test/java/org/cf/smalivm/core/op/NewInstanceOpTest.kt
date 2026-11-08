package org.cf.smalivm.core.op

import org.cf.smalivm.core.ClassInitState
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.ExecutionNode
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.cf.smalivm.core.agreesWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `new-instance` allocates a fresh tracked object, a `Known(HeapRef, T)` into the shared heap rather than a host
 * instance, flagged uninitialized until an `<init>` whose receiver is that ref returns (see `NewInstanceOp`).
 */
class NewInstanceOpTest {

    @TestFactory
    fun anAllocationIsAnUninitializedTrackedObjectOfItsTypeOperand(): List<DynamicTest> = listOf(
        "newLocalInstance()V" to CLASS_NAME,
        "newNonLocalInstance()V" to "Ljava/lang/Integer;",
    ).map { (method, type) ->
        DynamicTest.dynamicTest(type) {
            val initial = CoreState().apply { setRegisters(0, 1, "I") }
            val graph = CoreTester.execute(CLASS_NAME, method, initial)
            assertUninitializedInstance(graph, register = 0, type = VirtualType.of(type))
        }
    }

    /** [register] holds a `Known(HeapRef, [type])` whose heap object is an uninitialized [type]. */
    private fun assertUninitializedInstance(graph: ExecutionGraph, register: Int, type: VirtualType) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val consensus = trusted!!.terminatingConsensus(register)
        assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked object: $consensus")
        assertEquals(type, consensus.type, "the register tag is the instruction's type operand")
        val obj = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef).agreed()
        assertTrue(obj is HeapObject.TrackedObject, "no tracked object behind the ref; got $obj")
        assertEquals(type, (obj as HeapObject.TrackedObject).type)
        assertTrue(obj.uninitialized, "a fresh allocation is uninitialized until its <init> returns")
    }

    /**
     * The fixture class and its superclass `Ljava/lang/Object;` declare no `<clinit>`, so the class-init
     * prelude skips nothing and the allocation records no effect; a spurious `STRONG` would stop every fold
     * in a method that allocates.
     */
    @Test
    fun aFreshAllocationOfAClassWithNoInitializerDeclaresNoSideEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "newLocalInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    /** A framework class is marked initialized without running its `<clinit>`. */
    @Test
    fun theFrameworkConservativeInitMarksIntegerWithoutRunningItsClinit() {
        val graph = CoreTester.execute(CLASS_NAME, "newNonLocalInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "a framework <clinit> is never run")
        assertEquals(
            ClassInitState.INITIALIZED, initStateAtReturn(graph, VirtualType.of("Ljava/lang/Integer;")),
            "but the class is marked initialized, so a second trigger has nothing to do",
        )
    }

    /**
     * An app class with a guest `<clinit>` runs it as a prelude, then the op re-executes at its own address
     * and allocates. Exactly two nodes sit at that address: one means the init never ran, three or more means
     * the second pass did not see `INITIALIZED`.
     */
    @Test
    fun aNewInstanceOfAClassWithAGuestClinitRunsTheInitializerThenAllocates() {
        val witness = VirtualType.of("Lclass_init/witness;")
        val graph = CoreTester.execute(CLASS_NAME, "newWitnessInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(witness.descriptor), initializersRun(graph),
            "exactly one nested <clinit> ran, as a prelude to the new-instance",
        )
        assertEquals(ClassInitState.INITIALIZED, initStateAtReturn(graph, witness))
        assertEquals(2, graph.nodePile(0).size, "the trigger re-executes once the prelude returns")
        // The second pass did the op's work: r0 holds the uninitialized instance.
        assertUninitializedInstance(graph, register = 0, type = witness)
    }

    /**
     * An absent class throws a guest `NoClassDefFoundError` with ART's `"Failed resolution of: <descriptor>"`
     * (DALVIK-SEMANTICS.md, `Fx_NewInstanceAbsentType`). Resolution fails before the class-init trigger;
     * triggering init of an absent class would raise `ClassLoadFailure` and abort the path as `Partial`, which
     * the completion assertion rules out.
     */
    @Test
    fun anAbsentClassThrowsNoClassDefFoundErrorWithoutFallingThrough() {
        CoreTester.testThrows(
            CLASS_NAME, "newAbsentClassInstance()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "Failed resolution of: Ldoes/not/exist;",
        )
        // Same shape as NewArrayOpTest.anAbsentComponentClassThrowsNoClassDefFoundError. The assignment set is
        // static, read off the op, so it is {0} even though the throwing path writes nothing.
        val graph = CoreTester.execute(CLASS_NAME, "newAbsentClassInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow reference result owns one register")
        assertTrue(
            graph.nodePile(0).first().frame.peek(0) == null,
            "a throwing new-instance must leave its destination register untouched",
        )
    }

    /**
     * Heap identity is the allocation id ([HeapRef]), not the type: two allocations of one type are distinct
     * objects whose values never agree, so marking one initialized cannot touch the other.
     */
    @Test
    fun twoAllocationsOfOneTypeAreDistinctUnrelatedObjects() {
        val type = VirtualType.of(CLASS_NAME)
        val graph = CoreTester.execute(CLASS_NAME, "newTwoLocalInstances()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val first = trusted!!.terminatingConsensus(0)
        val second = trusted.terminatingConsensus(1)
        assertTrue(first is Value.Known && (first as Value.Known).raw is HeapRef, "r0 must be tracked: $first")
        assertTrue(second is Value.Known && (second as Value.Known).raw is HeapRef, "r1 must be tracked: $second")
        val firstRef = (first as Value.Known).raw as HeapRef
        val secondRef = (second as Value.Known).raw as HeapRef
        assertNotEquals(firstRef, secondRef, "two allocations of one type must be two heap identities")
        assertNotSame(
            trusted.terminatingHeapObject(firstRef).agreed(), trusted.terminatingHeapObject(secondRef).agreed(),
            "and two objects, so a mark on one is invisible to the other",
        )
        assertFalse(
            first.agreesWith(second),
            "two unrelated allocations must never agree",
        )
        // To every other query they are alike: same type, both uninitialized.
        for (ref in listOf(firstRef, secondRef)) {
            val obj = trusted.terminatingHeapObject(ref).agreed()
            assertTrue(obj is HeapObject.TrackedObject && obj.type == type && obj.uninitialized)
        }
    }

    // A graph's piles describe the entry activation only, so a `<clinit>`'s nodes are found by walking
    // `ExecutionNode.children`, as in ClassInitTest.

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    /** Classes whose `<clinit>` ran as a class-init prelude, in run order. */
    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<org.cf.smalivm.core.Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    /** [type]'s lifecycle state on the entry method's returning path. */
    private fun initStateAtReturn(graph: ExecutionGraph, type: VirtualType): ClassInitState {
        val returning = allNodes(graph).filter { it.outcome is Outcome.Returned && it.activation.caller == null }
        assertTrue(returning.isNotEmpty(), "no returning node in the entry activation")
        return returning.map { it.frame.initState(type) }.distinct().single()
    }

    private companion object {
        private const val CLASS_NAME = "Lnew_instance_test;"
    }
}
