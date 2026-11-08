package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The port of `org.cf.smalivm.TestStaticFieldAccess` (3 tests; the commented-out integration suite the
 * triage note assigns to Plan 9's static-field task) -- static state observed ACROSS activations,
 * where the per-op files (`core/op/SGetOpTest`, `core/op/SPutOpTest`) observe one op at a time.
 *
 * **What re-expression means here, test by test, because two of the three could not port verbatim:**
 *
 *  - [canGetFieldsWhileInitializing] keeps the original's fixture (`Lstatic_fields_test;`'s
 *    `<clinit>`) and all three expected values byte-identical (0, 0, 1). Only the TRIGGER moved: the
 *    original ran `stubMethod()V` and relied on the oracle harness initializing the ENTRY class
 *    (`VirtualMachine.spawnEntrypointState`); the core models entry as part of invocation (Plan 9
 *    Tasks 20+ own the top-level entry trigger), so the trigger here is a `sget` -- the same
 *    `<clinit>`, one instruction later.
 *  - [canAccessFieldAfterCollapsingMultiverse] and [canAccessFieldAfterCollapsingMultiverseTwice]
 *    asserted a `new-instance Ljava/util/ArrayList;` + `<init>` + `add(...)` round trip through a
 *    static, across one and two levels of `invoke-static` respectively. `invoke-*` is Tasks 20-21 and
 *    the emulated `ArrayList` is Plan 11, so the UNCHANGED-INTENT claims are re-expressed on the
 *    machinery that exists: the tracked object is an array, the nested activations are `<clinit>`s (a
 *    `<clinit>` is a real nested call through the same seam -- design decision 1), and the original's
 *    closing assertion -- `assertEquals(item, fieldItem)`, the register consensus and the field
 *    consensus denote ONE object -- is kept verbatim in the new model. What is genuinely deferred and
 *    NOT claimed here: the invoke-borne multiverse collapse itself (the callee forking and the caller
 *    reconciling), which re-lands with Task 20's `invoke` and Plan 11's backends against the original
 *    smali methods (`callMethodThenGetField()V` and its sibling, still in the fixture untouched).
 *
 * Values that survive: `0`/`0`/`1` for the `<clinit>` trio, `"added item!"` as the stored element,
 * size 1, and field-consensus == register-consensus.
 */
class StaticFieldAccessTest {

    /**
     * A `<clinit>` reads a static of its OWN class mid-initialization and sees the PREPARED value,
     * then overwrites another: `sget alwaysZero` (prepared 0) -> `sput testInt1` (0), then
     * `sput testInt2` (1). JVMS §5.4.2 preparation running BEFORE the initializer is the whole
     * content -- a build that seeded on completion would read `Top` inside the `<clinit>` and then
     * clobber its writes (`ClassInitStore.prepare`'s KDoc names this test as its pin). Also the
     * mutate-to-prove witness for `sget`'s class-init trigger (MEASURED,
     * `Fx_StaticFieldDoesInitialize`): an op that skips `Transition.InitClass` never runs the
     * `<clinit>`, and all three field expectations plus the register go red.
     */
    @Test
    fun canGetFieldsWhileInitializing() {
        val expected = CoreState().apply {
            setFields(CLASS_NAME, "alwaysZero:I", 0, "testInt1:I", 0, "testInt2:I", 1)
            setRegisters(0, 1, "I")
        }
        CoreTester.test(CLASS_NAME, "getTestInt2()I", expected)
    }

    @Test
    fun canAccessFieldAfterCollapsingMultiverse() {
        val graph = CoreTester.execute(CLASS_NAME, "storeThenMutateThenGetArray()[Ljava/lang/String;")
        assertOneMutatedSharedObject(
            graph,
            fieldClass = CLASS_NAME,
            fieldDescriptor = "$CLASS_NAME->myArray:[Ljava/lang/String;",
            expectedInitializers = listOf(CLASS_NAME),
        )
    }

    /**
     * The two-level variant: the trigger runs TWO nested activations before the write lands --
     * the superclass's `<clinit>` first, then the class's own (adjudication 4's ancestors-first
     * order, asserted exactly) -- so the pile at the `sput-object` address holds THREE nodes: first
     * pass plans the parent's prelude, second pass the child's, third pass writes. A wrong count in
     * either direction is a broken trigger (the `NewInstanceOpTest` re-execution pin, one level
     * deeper).
     */
    @Test
    fun canAccessFieldAfterCollapsingMultiverseTwice() {
        val graph = CoreTester.execute(CHILD, "storeMutateGetThroughTwoInitializers()[Ljava/lang/String;")
        assertOneMutatedSharedObject(
            graph,
            fieldClass = CHILD,
            fieldDescriptor = "$CHILD->myArray:[Ljava/lang/String;",
            expectedInitializers = listOf(PARENT, CHILD),
        )
        assertEquals(
            3, graph.nodePile(3).size,
            "the sput-object at address 3 re-executes once per initializer prelude: plan parent, " +
                "plan child, then write",
        )
    }

    /**
     * The shared claim of the two multiverse ports, unchanged from the original in what it asserts
     * about VALUES and IDENTITY:
     *  1. the register holds a tracked object with exactly one element, `"added item!"` (the
     *     original's `list.size() == 1` / `list.get(0)`);
     *  2. the mutation made THROUGH THE REGISTER after the store is visible THROUGH THE FIELD -- the
     *     field holds a reference, never a copy;
     *  3. the original's closing line verbatim: the field consensus and the register consensus are
     *     the same value (`assertEquals(item, fieldItem)`) -- for tracked objects, `Value.Known`
     *     equality is heap-id equality, so this IS the identity claim;
     *  4. the named nested activations ran in the named order, so the agreement in (3) held across a
     *     call boundary and back (`ClassInitStore`'s threading -- the store rides
     *     `Frame.calleeEntryFrame` in and `Frame.resumedWith` back).
     */
    private fun assertOneMutatedSharedObject(
        graph: ExecutionGraph,
        fieldClass: String,
        fieldDescriptor: String,
        expectedInitializers: List<String>,
    ) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            expectedInitializers, initializersRun(graph),
            "the named <clinit> preludes must have run, in ancestors-first order, between the " +
                "allocation and the read-back",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val register = trusted!!.terminatingConsensus(0)
        assertTrue(
            register is Value.Known && (register as Value.Known).raw is HeapRef,
            "r0 must hold the tracked array read back from the field: $register",
        )
        val array = trusted.terminatingHeapObject((register as Value.Known).raw as HeapRef).agreed()
        assertTrue(array is HeapObject.TrackedArray, "no tracked array behind the ref; got $array")
        assertEquals(1, (array as HeapObject.TrackedArray).length)
        assertEquals(
            Value.Known("added item!", VirtualType.of("Ljava/lang/String;")), array.element(0),
            "the aput-object AFTER the sput-object must be visible through the field: the cell holds " +
                "a reference to the object, never a snapshot of it",
        )
        assertEquals(
            register,
            trusted.terminatingStaticFieldConsensus(VirtualType.of(fieldClass), fieldDescriptor),
            "the original's closing claim: the register consensus and the field consensus are ONE " +
                "value -- same heap identity, not merely equal contents",
        )
    }

    // Minimal activation-walking helper, the same copy NewInstanceOpTest and core/op/SGetOpTest carry:
    // a graph describes ONE activation by design, so a <clinit>'s activations are reachable only
    // through ExecutionNode.children.
    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        val seen = mutableListOf<Activation>()
        for (node in out) if (seen.none { it === node.activation }) seen.add(node.activation)
        return seen.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    private companion object {
        private const val CLASS_NAME = "Lstatic_fields_test;"
        private const val PARENT = "Lstatic_fields_parent_test;"
        private const val CHILD = "Lstatic_fields_child_test;"
    }
}
