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
import org.junit.jupiter.api.Test

/**
 * `new-instance` (spec §4.2): a fresh, **uninitialized** tracked object -- a `Known(HeapRef, T)` into
 * the shared heap, never a live host instance. Migrated from `org.cf.smalivm.opcode.NewInstanceOpTest`
 * -- both of its tests, per Task 13 Step 5's triage (2 of 2 re-express) -- plus the cases the corpus
 * never covered: the absent-class resolution failure (Task 15's family; the plan's Task 15 Step 4
 * explicitly deferred `new-instance`'s case to this task), the class-init trigger through the REAL op
 * rather than `ClassInitTest`'s stub, and adjudication 8's two-unrelated-allocations distinctness.
 *
 * **What "uninitialized" is in the new model** (resolved decision 8): the oracle expressed it as a
 * register holding `type/UninitializedInstance(type)`; the core expresses it as the heap object's own
 * flag -- `HeapObject.TrackedObject(type, uninitialized = true)`. The flag is SET here and cleared
 * nowhere in this build: clearing happens when an `<init>` whose receiver is this ref returns, which
 * needs `invoke-direct` and is therefore Task 20's (see `NewInstanceOp`'s KDoc). Values are
 * byte-identical to the oracle's claims; only the harness-inexpressible mechanism changed.
 *
 * **Adjudication 8 (the alias divergence) is deliberately NOT ported.** The oracle propagates
 * initialization to alias registers through `ExecutionState.update`, which matches by `equals`, and
 * `type/UninitializedInstance.java:17-29` compares TYPE ONLY -- so initializing one `new-instance
 * Lfoo;` rewrites every register holding any uninitialized `Lfoo;`, including an unrelated
 * allocation's. The core's heap identity is the id ([HeapRef]), which
 * [twoAllocationsOfOneTypeAreDistinctUnrelatedObjects] pins; the registry half is
 * `Fixtures.intendedDivergences`' `ratchet-alias-unrelated-allocation`, and the oracle's defective
 * answer is measured (not described) by
 * `NewCoreDifferentialTest.theOraclesTypeEqualityAliasingRewritesAnUnrelatedAllocation`.
 */
class NewInstanceOpTest {

    @Test
    fun canCreateLocalInstance() {
        // Migrated: the oracle seeded r0 with an int and asserted it ends as
        // `UninitializedInstance(Lnew_instance_test;)` under tag `Lnew_instance_test;`. Same claim,
        // heap-indirected: the register tag is the consensus VALUE's type, the "uninitialized
        // instance" is the tracked object's own type + flag.
        val initial = CoreState().apply { setRegisters(0, 1, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "newLocalInstance()V", initial)
        assertUninitializedInstance(graph, register = 0, type = VirtualType.of(CLASS_NAME))
    }

    @Test
    fun canCreateNonLocalInstance() {
        // Integer has a framework <clinit>, so allocation uses conservative initialization.
        // This test checks the instance; theFrameworkConservativeInitMarksIntegerWithoutRunningItsClinit
        // separately checks the initialization policy.
        val initial = CoreState().apply { setRegisters(0, 1, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "newNonLocalInstance()V", initial)
        assertUninitializedInstance(graph, register = 0, type = VirtualType.of("Ljava/lang/Integer;"))
    }

    /**
     * The migrated pair's shared claim: [register] holds a `Known(HeapRef, [type])` whose heap object
     * is a [HeapObject.TrackedObject] of [type] with the uninitialized flag SET. Read through
     * `trusted()` + heap consensus, the same consumer path `NewArrayOpTest`'s local-instance
     * migrations read through.
     */
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
        assertTrue(
            obj.uninitialized,
            "a fresh allocation is UNINITIALIZED until an <init> whose receiver is this ref returns " +
                "(resolved decision 8; the clearing is Task 20's, with invoke-direct)",
        )
    }

    /**
     * Side effects: NONE, pinned rather than assumed -- the [FilledNewArrayOp]/[ThrowOp] precedent. A
     * fresh allocation perturbs nothing the engine does not model exactly, and `NewInstanceOp` declares
     * no effect on any arm. The fixture class declares no `<clinit>` and extends
     * `Ljava/lang/Object;` (which declares none either), so the class-init prelude settles with
     * nothing skipped and the SCHEDULER declares nothing -- a spurious STRONG here would stop every
     * fold in any method that allocates.
     */
    @Test
    fun aFreshAllocationOfAClassWithNoInitializerDeclaresNoSideEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "newLocalInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    /**
     * Resolved decision 7 through the REAL op (ClassInitTest pins it at the planner and through a
     * stub): a framework class is marked initialized and its `<clinit>` is NEVER run, and because a
     * real initializer was skipped, the triggering address is STRONG -- whatever `Integer.<clinit>`
     * would have written is unmodelled state (spec §2.2 errs upward).
     */
    @Test
    fun theFrameworkConservativeInitMarksIntegerWithoutRunningItsClinit() {
        val graph = CoreTester.execute(CLASS_NAME, "newNonLocalInstance()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph), "a framework <clinit> is NEVER run")
        assertEquals(
            ClassInitState.INITIALIZED, initStateAtReturn(graph, VirtualType.of("Ljava/lang/Integer;")),
            "but the class IS marked initialized, so a second trigger has nothing to do",
        )
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "an initializer that exists and did not run is unmodelled state, so the trigger is STRONG",
        )
    }

    /**
     * THE CLASS-INIT INTEGRATION PROOF: `new-instance` of an app class WITH a guest `<clinit>` runs
     * the initializer through the real op's `OpArm.InitClass`. `Lclass_init/witness;` is the
     * ordinary-`<clinit>` fixture; the trigger
     * protocol re-executes the op at its own address once the prelude returns, so the pile at the
     * `new-instance` address holds TWO nodes (first pass asks, second pass allocates) -- asserted
     * exactly, because a wrong count in either direction is a broken trigger (0 re-executions means
     * the init never ran; 2+ means the op did not see INITIALIZED on its second pass and looped).
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
        // ...and the second pass did the op's real work: r0 holds the uninitialized instance.
        assertUninitializedInstance(graph, register = 0, type = witness)
    }

    /**
     * `new-instance` of a class absent from the loaded set throws a guest `NoClassDefFoundError` --
     * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`, fixture `Fx_NewInstanceAbsentType` (Task 16 Step 0).
     * The message is ART's `DoResolveType` text, `"Failed resolution of: %s"` over the descriptor, as the
     * Tier-A recording shows for `Fx_NewInstanceAbsentType`.
     *
     * The resolution failure must come BEFORE the class-init trigger, and that ordering is what the
     * completion gate below actually discriminates: an op that asked to initialize `Ldoes/not/exist;`
     * would send `ClassInitPlanner` walking `superclassOf` on an absent class, which raises
     * `ClassLoadFailure` and ABORTS the path (`Partial`) instead of throwing the guest error.
     */
    @Test
    fun anAbsentClassThrowsNoClassDefFoundErrorWithoutFallingThrough() {
        CoreTester.testThrows(
            CLASS_NAME, "newAbsentClassInstance()V",
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "Failed resolution of: Ldoes/not/exist;",
        )
        // The negative halves, NewArrayOpTest.nonExistentClassNameThrowsException's shape: the
        // fall-through is only provably unexplored on a run that explored everything, and a throwing
        // new-instance must leave its destination register untouched (the STATIC declaration is
        // still {0} -- registersAssigned reads off the op, not a frame).
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
     * ADJUDICATION 8's core half: two `new-instance` of ONE type are two DISTINCT objects. Under the
     * oracle's type-equality identity (`UninitializedInstance.equals` compares type only) the two
     * registers hold "the same" value, so a later mark on one rewrites the other; under id-based heap
     * identity each register keeps its own object -- the refs differ, the heap holds two objects, and
     * the two values do NOT agree (their join answers `Unknown`, never a single `Known`).
     *
     * Also the mutate-to-prove target for allocation identity: an `allocateObject` minting anything
     * but a fresh id from the shared [org.cf.smalivm.core.HeapIds] counter (a bare constant, a
     * per-store counter) hands both registers one ref and dies here.
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
            "and two objects -- a mark on one must be invisible to the other",
        )
        assertFalse(
            first.agreesWith(second),
            "two unrelated allocations must never agree -- type-equality identity is the oracle's " +
                "alias defect (adjudication 8), registered in Fixtures.intendedDivergences",
        )
        // Both are the same kind of thing to every OTHER query: same type, both uninitialized.
        for (ref in listOf(firstRef, secondRef)) {
            val obj = trusted.terminatingHeapObject(ref).agreed()
            assertTrue(obj is HeapObject.TrackedObject && obj.type == type && obj.uninitialized)
        }
    }

    // ------------------------------------------------------------------------------------------
    // Harness -- the minimal subset of ClassInitTest's activation-walking helpers. Walked down
    // ExecutionNode.children because a graph describes ONE activation by design and a <clinit>'s
    // nodes are invisible to its queries; see ClassInitTest.allNodes for the full argument.
    // ------------------------------------------------------------------------------------------

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    /** Classes whose `<clinit>` RAN as a class-init prelude, in run order; see ClassInitTest.initializersRun. */
    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val seen = mutableListOf<org.cf.smalivm.core.Activation>()
        for (node in allNodes(graph)) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    /** [type]'s lifecycle state on the entry method's returning path; see ClassInitTest.initStateAtReturn. */
    private fun initStateAtReturn(graph: ExecutionGraph, type: VirtualType): ClassInitState {
        val returning = allNodes(graph).filter { it.outcome is Outcome.Returned && it.activation.caller == null }
        assertTrue(returning.isNotEmpty(), "no returning node in the entry activation")
        return returning.map { it.frame.initState(type) }.distinct().single()
    }

    private companion object {
        private const val CLASS_NAME = "Lnew_instance_test;"
    }
}
