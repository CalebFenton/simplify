package org.cf.smalivm.core

import org.cf.smalivm.core.CoreTester.initializersRun
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Static state observed across activations: an initializer reading its own class's statics, and a reference
 * stored in a static staying one object across nested `<clinit>` activations. `core/op/SGetOpTest` and
 * `core/op/SPutOpTest` observe one op at a time.
 */
class StaticFieldAccessTest {

    /**
     * `Lstatic_fields_test;-><clinit>` runs `sget alwaysZero` (prepared 0) -> `sput testInt1`, then
     * `sput testInt2` (1). JVMS §5.4.2 preparation precedes the initializer, so the read inside it sees the
     * prepared 0; seeding on completion would read `Top` there. An `sget` that skipped its class-init trigger
     * would never run the `<clinit>`, and every expectation here would fail.
     */
    @Test
    fun anInitializerReadsItsOwnPreparedStaticsBeforeWritingOthers() {
        val expected = CoreState().apply {
            setFields(CLASS_NAME, "alwaysZero:I", 0, "testInt1:I", 0, "testInt2:I", 1)
            setRegisters(0, 1, "I")
        }
        // From process start: the initializer's writes to these non-final statics are what the test reads.
        CoreTester.test(CLASS_NAME, "getTestInt2()I", expected, EntryClassState.FRESH_PROCESS)
    }

    @Test
    fun aStaticHoldsAReferenceSoAMutationThroughTheRegisterIsVisibleThroughTheField() {
        val graph = CoreTester.execute(CLASS_NAME, "storeThenMutateThenGetArray()[Ljava/lang/String;")
        assertOneMutatedSharedObject(
            graph,
            fieldClass = CLASS_NAME,
            fieldDescriptor = "$CLASS_NAME->myArray:[Ljava/lang/String;",
            expectedInitializers = listOf(CLASS_NAME),
        )
    }

    /**
     * The trigger runs the superclass's `<clinit>`, then the class's own (JLS §12.4.2 step 7, ancestors first),
     * so the `sput-object` address is executed three times: plan the parent's prelude, plan the child's, write.
     */
    @Test
    fun aStaticHoldsAReferenceAcrossTwoNestedInitializers() {
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
     * The register holds a tracked array with the one element `"added item!"`, written through the register
     * after the store and visible through the field; the field and register consensus are the same value
     * (`Value.Known` equality of a tracked object is heap-id equality); and [expectedInitializers] ran in that
     * order, so the agreement held across a call boundary and back.
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
            "the register consensus and the field consensus are one value -- same heap identity, not merely " +
                "equal contents",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Lstatic_fields_test;"
        private const val PARENT = "Lstatic_fields_parent_test;"
        private const val CHILD = "Lstatic_fields_child_test;"
    }
}
