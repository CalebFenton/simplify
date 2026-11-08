package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The seven `aget*` opcodes (format 23x, two code units, so the successor of an op at 0 is address 2).
 * Exceptions and forks follow `docs/DALVIK-SEMANTICS.md` section "`aget*` / `aput*` (ops 44–51, 23x)"; one op
 * serves all seven variants because the element's value and type come from the tracked array.
 */
class AGetOpTest {
    private data class GetCase(
        val name: String,
        val method: String,
        val array: Any?,
        val arrayType: String,
        val index: Any,
        val element: Any?,
        val elementType: String,
    )

    /**
     * Each variant reads the element verbatim, typed by the array object. `canGetObject`'s register tag is the
     * component type, not an array descriptor, and the op must not care. The wide row seeds `[J` because a `J`
     * tag on a reference is impossible and `Frame.assign` would give it a high half that clobbers the index.
     */
    @TestFactory
    fun canGetEachVariant(): List<DynamicTest> = listOf(
        GetCase("int", "get()V", intArrayOf(0x42), "[I", 0, 0x42, "I"),
        GetCase("boolean", "getBoolean()V", booleanArrayOf(true), "[Z", 0, true, "Z"),
        GetCase("byte", "getByte()V", byteArrayOf(0xe), "[B", 0, 0xe.toByte(), "B"),
        GetCase("char", "getChar()V", charArrayOf('a'), "[C", 0, 'a', "C"),
        GetCase("short", "getShort()V", shortArrayOf(0x42), "[S", 0, 0x42.toShort(), "S"),
        GetCase("object", "getObject()V", arrayOf("stringy"), "Ljava/lang/String;", 0, "stringy", "Ljava/lang/String;"),
        GetCase("wide", "getWide()V", longArrayOf(0x10000000000L), "[J", 0, 0x10000000000L, "J"),
        GetCase("uninitialized int", "getUninitializedInt()V", IntArray(1), "[I", 0, 0, "I"),
        // Element-wise precision: only element 0 is unknown, and element 1 stays known.
        GetCase("unknown element", "get()V", arrayOf(CoreState.UNKNOWN, 5), "[I", 0, CoreState.UNKNOWN, "I"),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply { setRegisters(0, case.array, case.arrayType, 1, case.index, "I") }
            val expected = CoreState().apply { setRegisters(0, case.element, case.elementType) }
            CoreTester.test(CLASS_NAME, case.method, initial, expected)
        }
    }

    /**
     * v0 holds the element and v1 still holds the `Short` index: `aget` does not clobber its index, and an
     * index register may hold any integral box under any integral tag.
     */
    @Test
    fun canGetWithShortIndex() {
        val index: Short = 0
        val initial = CoreState().apply { setRegisters(0, intArrayOf(0x42), "[I", 1, index, "S") }
        val expected = CoreState().apply { setRegisters(0, 0x42, "I", 1, index, "S") }
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
    }

    private data class DefiniteThrowCase(
        val name: String,
        val array: Any?,
        val arrayType: String,
        val index: Any,
        val exceptionType: String,
        val message: String?,
    )

    /**
     * Definite throws, caught by `getWithCatch()V`'s `Ljava/lang/Exception;` handler. A primitive-typed array
     * register reads as null whatever it holds (section "Reading a register as a reference"); a negative index
     * is an ordinary bounds exception because ART compares unsigned. Messages are ART's
     * (`ThrowNullPointerExceptionFromDexPC`, `ThrowArrayIndexOutOfBoundsException`).
     */
    @TestFactory
    fun definiteThrowsAreCaughtAndAssignNothing(): List<DynamicTest> = listOf(
        DefiniteThrowCase("null array", null, "[I", 0, NPE, "Attempt to read from null array"),
        DefiniteThrowCase("integral zero array", 0, "I", 0, NPE, null),
        DefiniteThrowCase("non-zero primitive array", 5, "I", 0, NPE, null),
        DefiniteThrowCase("index past the end", IntArray(5), "[I", 10, AIOOBE, "length=5; index=10"),
        DefiniteThrowCase("negative index", intArrayOf(1, 2, 3), "[I", -1, AIOOBE, "length=3; index=-1"),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply { setRegisters(0, case.array, case.arrayType, 1, case.index, "I") }
            CoreTester.testCaught(
                CLASS_NAME, "getWithCatch()V", initial,
                exceptionType = case.exceptionType,
                message = case.message,
                caughtInRegister = 0,
                unreachedAddress = 2,
            )
            if (case.arrayType == "[I") assertThrowingAgetAssignedNothing(initial)
        }
    }

    private data class ForkCase(
        val name: String,
        val array: Any?,
        val index: Any,
        val indexType: String,
        val escaping: List<String>,
        val readIsFeasible: Boolean,
    )

    /**
     * Fork shape on the handler-free `get()V`, where every throw escapes. An unprovable array forks the NPE
     * and the AIOOBE before the index is consulted; a provable array forks only the AIOOBE, since an NPE fork
     * would make an unreachable handler reachable. A zero-length array and a `Float` index read by its bits
     * (`2.9f` is 1077516698) are definite bounds exceptions with no feasible read.
     */
    @TestFactory
    fun unprovableOperandsForkOnlyFeasibleExceptions(): List<DynamicTest> = listOf(
        ForkCase("unknown array, known index", CoreState.UNKNOWN, 0, "I", listOf(NPE, AIOOBE), true),
        ForkCase("unknown array, unknown index", CoreState.UNKNOWN, CoreState.UNKNOWN, "I", listOf(NPE, AIOOBE), true),
        ForkCase("provable array, unknown index", intArrayOf(1, 2), CoreState.UNKNOWN, "I", listOf(AIOOBE), true),
        ForkCase("zero-length array, unknown index", intArrayOf(), CoreState.UNKNOWN, "I", listOf(AIOOBE), false),
        ForkCase("float index read by its bits", intArrayOf(0x11, 0x22, 0x33), 2.9f, "F", listOf(AIOOBE), false),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply { setRegisters(0, case.array, "[I", 1, case.index, case.indexType) }
            val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion(), "guest throws are not truncations")
            assertEquals(case.escaping, CoreTester.escapingTypes(graph), "escaping throws, in transition order")
            assertEquals(case.readIsFeasible, graph.wasReached(2), "whether the read itself is feasible")
            if (case.readIsFeasible) {
                val expected = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
                CoreTester.test(CLASS_NAME, "get()V", initial, expected)
            }
        }
    }

    /**
     * With an unprovable index the element type is derived, and it must come from the array object
     * (`String[]`), not the `[Ljava/lang/Object;` register tag.
     */
    @Test
    fun theElementTypeOfAnUnprovableIndexComesFromTheArrayObjectNotTheRegisterTag() {
        val initial = CoreState().apply {
            setRegisters(0, arrayOf("a", "b"), "[Ljava/lang/Object;", 1, CoreState.UNKNOWN, "I")
        }
        val expected = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/String;") }
        CoreTester.test(CLASS_NAME, "getObject()V", initial, expected)
    }

    /**
     * Bug class: an operand transposition in `buildAGet` (`vAA` destination, `vBB` array, `vCC` index). Three
     * distinct registers with distinct values make each of the three swaps change a register value.
     */
    @Test
    fun theThreeOperandsAreNotInterchangeable() {
        val initial = CoreState().apply { setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 2, "I") }
        val expected = CoreState().apply {
            setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 2, "I", 2, 0x33, "I")
        }
        CoreTester.test(CLASS_NAME, "getWithThreeDistinctRegisters()V", initial, expected)
    }

    /**
     * Bug class: forking exceptions for a value the engine proved non-null. A `Known` reference with no tracked
     * array is the not-tracked case (**VERIFIER**): no exception, an unknown element typed by the tag.
     */
    @Test
    fun aKnownNonArrayIsProvablyNonNullSoTheElementIsUnknownAndNothingThrows() {
        val initial = CoreState().apply { setRegisters(0, "not an array at all", "Ljava/lang/String;", 1, 0, "I") }
        val expected = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "Ljava/lang/String;") }
        CoreTester.test(CLASS_NAME, "get()V", initial, expected)
        val graph = CoreTester.execute(CLASS_NAME, "get()V", initial)
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor; both throws are infeasible")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "a non-null reference with no length throws nothing")
    }

    /**
     * An unassigned array register is `Top`, so the result is `Top` rather than an invented type. Only
     * `frame.peek` distinguishes an assigned `Top` from an unassigned register.
     */
    @Test
    fun anArrayRegisterWithNoTypeAtAllYieldsTopRatherThanAFabricatedType() {
        val graph = CoreTester.execute(CLASS_NAME, "get()V", CoreState())
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Top, graph.nodePile(0).first().frame.peek(0), "the destination is assigned Top")
        assertEquals(
            listOf(NPE, AIOOBE), CoreTester.escapingTypes(graph),
            "a Top array is no more provable than an Unknown one",
        )
    }

    /** Bug class: a fresh reference array's element is definitely null, not `Unknown`. */
    @Test
    fun readingAFreshReferenceArrayGivesDefinitelyNull() {
        val expected = CoreState().apply {
            setRegister(0, Value.Known(null, VirtualType.of("Ljava/lang/Object;")))
        }
        CoreTester.test(CLASS_NAME, "getObjectFromFreshArray()V", CoreState(), expected)
    }

    /**
     * Bug class: a wide destination naming only its low half, leaving the high half offered as free scratch.
     * The fixture keeps destination and operands disjoint so `registersRead` cannot satisfy the assertion.
     */
    @Test
    fun agetWideOwnsBothHalvesOfItsDestination() {
        val graph = CoreTester.execute(CLASS_NAME, "getWideIntoDisjointPair()V", CoreState().apply {
            setRegisters(0, longArrayOf(7L, 8L), "[J", 1, 0, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(setOf(3, 4), graph.registersAssigned(0), "the wide destination owns v3 and v4")
        assertEquals(setOf(0, 1), graph.registersRead(0), "and the operands are a disjoint set")
    }

    /**
     * `registersAssigned` is static, so it pins the narrow width declaration; the destination still holding
     * the seeded array reference after the throwing node shows the throw happened before any assignment.
     */
    private fun assertThrowingAgetAssignedNothing(initial: CoreState) {
        val graph = CoreTester.execute(CLASS_NAME, "getWithCatch()V", initial)
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow aget's result owns exactly one register")
        val destinationAfter = graph.nodePile(0).first().frame.peek(0)
        assertEquals(
            VirtualType.of("[I"), (destinationAfter as? Value.Known)?.type,
            "a throwing aget must leave its destination holding the array, not an element: $destinationAfter",
        )
        val raw = (destinationAfter as Value.Known).raw
        assertTrue(
            raw == null || raw is HeapRef,
            "the surviving value must still be the seeded array reference (or its null), not an element: $raw",
        )
    }

    private companion object {
        private const val CLASS_NAME = "Laget_test;"
        private const val NPE = "Ljava/lang/NullPointerException;"
        private const val AIOOBE = "Ljava/lang/ArrayIndexOutOfBoundsException;"
    }
}
