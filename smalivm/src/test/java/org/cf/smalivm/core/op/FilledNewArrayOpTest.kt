package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.cf.smalivm.core.agreesWith
import org.cf.smalivm.core.join
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `filled-new-array` (op 24, 35c) and `/range` (op 25, 3rc), both three code units: in every fixture the op is at
 * 0, the `move-result-object` at 3, and `return-void` at 4. The result lives in the result slot, so each test
 * reads it from the register `move-result-object` moves it into. Expected values follow
 * `docs/DALVIK-SEMANTICS.md#filled-new-array-op-24-35c-and-range-op-25-3rc`.
 */
class FilledNewArrayOpTest {
    private lateinit var initial: CoreState

    companion object {
        private const val CLASS_NAME = "Lfilled_new_array_test;"
        private val INT = VirtualType.of("I")
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }

    @BeforeEach
    fun setUp() {
        initial = CoreState()
    }

    /**
     * Each element register `vi` lands in slot `i` as its 32 bits (ART `GetVReg`), so a `Short` or `Byte` raw
     * under an `I` component becomes an `Int`. Element values are distinct so a register-order defect shows, and
     * the rows cover every `35c` register count plus the `3rc` form.
     */
    @TestFactory
    fun eachIntElementLandsInItsSlotInRegisterOrder(): List<DynamicTest> = listOf(
        ints("filledNewArrayOne()V", intArrayOf(101), movedInto = 1),
        ints("filledNewArrayTwo()V", intArrayOf(101, 102), movedInto = 2),
        ints("filledNewArrayThenMoveResult()V", intArrayOf(101, 102, 103), movedInto = 3),
        ints("filledNewArrayFour()V", intArrayOf(101, 102, 103, 104), movedInto = 4),
        ints("filledNewArrayFive()V", intArrayOf(101, 102, 103, 104, 105), movedInto = 5),
        ints("filledNewArrayRangeThenMoveResult()V", intArrayOf(2, 3, 5, 7, 11, 13), movedInto = 6),
        elements(
            "shorts", "filledNewArrayThenMoveResult()V",
            listOf<Pair<Any, String>>(2.toShort() to "S", 3.toShort() to "S", 5.toShort() to "S"),
            intArrayOf(2, 3, 5), movedInto = 3,
        ),
        elements(
            "mixed boxed classes", "filledNewArrayThenMoveResult()V",
            listOf<Pair<Any, String>>(42.toShort() to "S", 35.toByte() to "B", 10 to "I"),
            intArrayOf(42, 35, 10), movedInto = 3,
        ),
    )

    private fun ints(method: String, values: IntArray, movedInto: Int): DynamicTest =
        elements("${values.size} ints", method, values.map { it to "I" }, values, movedInto)

    private fun elements(
        label: String,
        method: String,
        seeds: List<Pair<Any, String>>,
        stored: IntArray,
        movedInto: Int,
    ): DynamicTest = DynamicTest.dynamicTest("$method, $label") {
        val seed = CoreState()
        seeds.forEachIndexed { register, (raw, tag) -> seed.setRegister(register, raw, tag) }
        CoreTester.test(CLASS_NAME, method, seed, CoreState().apply { setRegisters(movedInto, stored, "[I") })
    }

    /**
     * An unprovable element makes only its own slot `Unknown(I)`; the array stays a tracked `[I` with every
     * provable slot kept, in both instruction forms.
     */
    @TestFactory
    fun anUnprovableElementLeavesEveryOtherSlotKnown(): List<DynamicTest> = listOf(
        unprovable("filledNewArrayThenMoveResult()V", listOf(2, 3, null), movedInto = 3),
        unprovable("filledNewArrayRangeThenMoveResult()V", listOf(2, null, 5, 7, 11, 13), movedInto = 6),
    )

    private fun unprovable(method: String, values: List<Int?>, movedInto: Int): DynamicTest =
        DynamicTest.dynamicTest(method) {
            val seed = CoreState()
            values.forEachIndexed { register, raw -> seed.setRegister(register, raw ?: CoreState.UNKNOWN, "I") }
            val graph = CoreTester.execute(CLASS_NAME, method, seed)
            assertEquals(CompletionStatus.Complete, graph.completion())
            val array = trackedArray(graph, movedInto)
            assertEquals(VirtualType.of("[I"), array.type, "the array's own type comes from the instruction")
            assertEquals(
                values.map { if (it == null) Value.Unknown(INT) else Value.Known(it, INT) }, array.elements(),
                "per-slot unknown-ness, not a whole-array Unknown",
            )
        }

    /**
     * [BC] allows reference components, and ART's reference fill stores the register's reference itself, so
     * a `[Ljava/lang/String;` holds its elements without coercion. `[[I` takes the same path.
     */
    @Test
    fun aReferenceComponentArrayStoresItsElementsVerbatim() {
        initial.setRegisters(0, "one", "Ljava/lang/String;", 1, "two", "Ljava/lang/String;")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val array = trackedArray(graph, register = 2)
        assertEquals(
            VirtualType.of("[Ljava/lang/String;"), array.type,
            "the result's type is the instruction's",
        )
        assertEquals(
            listOf(Value.Known("one", STRING), Value.Known("two", STRING)), array.elements(),
            "the raws are the references themselves, without coercion",
        )
    }

    /**
     * ART's switch interpreter fills a reference component through `GetVRegReference`, which yields null for a
     * register last written as a primitive, so every primitive seed stores `null`. `0` is ordinary `d8` output
     * for `new String[]{null}`; the non-zero seeds are verifier-rejected, and `nterp` would store their raw bits
     * as a pointer.
     */
    @TestFactory
    fun aPrimitiveElementUnderAReferenceComponentIsNull(): List<DynamicTest> =
        listOf<Pair<Any, String>>(0 to "I", 5 to "I", 5.9f to "F", true to "Z").map { (raw, tag) ->
            DynamicTest.dynamicTest("$tag $raw") {
                val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", CoreState().apply {
                    setRegisters(0, raw, tag, 1, raw, tag)
                })
                assertEquals(CompletionStatus.Complete, graph.completion())
                val array = trackedArray(graph, register = 2)
                assertEquals(VirtualType.of("[Ljava/lang/String;"), array.type, "the result's type is the instruction's")
                assertEquals(listOf(Value.Known(null, STRING), Value.Known(null, STRING)), array.elements())
            }
        }

    /**
     * `filled-new-array` and `aput-object` must spell a null element the same way, or a join of a path through
     * each loses the constant. Driven through both ops, seeded as `APutOpTest` seeds `putObject()V`.
     */
    @Test
    fun theTwoWaysOfSpellingANullElementAgreeSoAJoinKeepsTheFold() {
        initial.setRegisters(0, 0, "I", 1, 0, "I")
        val filled = trackedArray(
            CoreTester.execute(CLASS_NAME, "filledNewArrayOfStrings()V", initial),
            register = 2,
        ).elements()[0]

        val putState = CoreState()
        putState.setRegisters(0, arrayOf<String?>(null), "[Ljava/lang/String;", 1, 0, "I", 2, 0, "I")
        val putGraph = CoreTester.execute("Laput_test;", "putObject()V", putState)
        assertEquals(CompletionStatus.Complete, putGraph.completion())
        val stored = putGraph.trusted()!!.terminatingHeapObject((putGraph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef,).agreed()
        assertTrue(stored is HeapObject.TrackedArray, "aput-object's array should be tracked; got $stored")
        val aput = (stored as HeapObject.TrackedArray).elements()[0]

        assertEquals(aput, filled, "the two ops must spell Dalvik's null element the same way")
        assertTrue(filled.agreesWith(aput), "and the spellings must agree, or join degrades to Unknown")
        assertEquals(filled, join(filled, aput, CoreTester.classIndex), "so the join keeps the fold instead of answering Unknown")
    }

    /**
     * Both definite throws: ART does not implement single-word primitive components other than `int`
     * (`InternalError`, message naming the component by `PrettyDescriptor`), and an absent component class fails
     * resolution (`NoClassDefFoundError`, DEVICE `Fx_FilledNewArrayAbsentType`). Both are `Error`s, so no
     * `catch (Exception)` handler applies; neither forks a normal path.
     */
    @TestFactory
    fun aDefiniteThrowDoesNotReachTheMoveResult(): List<DynamicTest> = listOf(
        definiteThrow(
            "filledNewArrayOfBytes()V", "Ljava/lang/InternalError;",
            "Found type byte; filled-new-array not implemented for anything but 'int'",
        ),
        definiteThrow(
            "filledNewArrayOfMissingClass()V", "Ljava/lang/NoClassDefFoundError;",
            "Failed resolution of: [Ldoes/not/exist;",
        ),
    )

    private fun definiteThrow(method: String, exceptionType: String, message: String): DynamicTest =
        DynamicTest.dynamicTest(exceptionType) {
            val seed = { CoreState().apply { setRegisters(0, 1, "I", 1, 2, "I") } }
            CoreTester.testThrows(CLASS_NAME, method, seed(), exceptionType = exceptionType, message = message)
            val graph = CoreTester.execute(CLASS_NAME, method, seed())
            assertEquals(CompletionStatus.Complete, graph.completion())
            // `wasReached` is also false for an address that does not exist, so the successor is pinned first.
            assertEquals(3, graph.methodView.nextAddress(0), "filled-new-array is 35c, three code units")
            assertFalse(graph.wasReached(3), "a definite throw must not also explore the move-result-object")
            assertEquals(listOf(exceptionType), CoreTester.escapingTypes(graph), "exactly one throw, of exactly this type")
            assertEquals(0, graph.nodePile(0).first().children.size, "a definite throw has no successor")
        }

    /**
     * The op creates its array, so no NPE; it has no index, so no bounds exception; its count is an unsigned
     * instruction field, so no `NegativeArraySizeException`; and ART's reference fill does no store check. The
     * seed is all `Value.Top`, the input under which a defensive fork would appear.
     */
    @Test
    fun nothingIsForkedBecauseThisOpHasNoArrayIndexOrCountToBeWrong() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "no operand of this op can be null, out of range, or negative",
        )
        assertEquals(
            1, graph.nodePile(0).first().children.size,
            "exactly one successor: the fall-through to the move-result-object",
        )
        assertTrue(graph.wasReached(3), "and it really is explored")
        val array = trackedArray(graph, register = 3)
        assertEquals(3, array.length)
        assertEquals(List(3) { Value.Unknown(INT) }, array.elements())
    }

    /**
     * A fresh array mutates nothing pre-existing, so every arm declares `NONE`; a spurious `STRONG` would stop
     * strategies folding constant arrays.
     */
    @Test
    fun aFreshlyAllocatedArrayDeclaresNoSideEffectOnEveryRow() {
        val known = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", CoreState().apply {
            setRegisters(0, 2, "I", 1, 3, "I", 2, 5, "I")
        })
        assertEquals(SideEffectLevel.NONE, known.explored.sideEffectLevel(0), "a fresh array mutates nothing")
        val trusted = known.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        assertEquals(
            SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
            "and the whole-method query a strategy actually reads agrees",
        )

        val unknown = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(
            SideEffectLevel.NONE, unknown.explored.sideEffectLevel(0),
            "an unprovable element is a lattice point in a tracked slot, not an unmodellable write",
        )

        val internalError = CoreTester.execute(CLASS_NAME, "filledNewArrayOfBytes()V")
        assertEquals(SideEffectLevel.NONE, internalError.explored.sideEffectLevel(0), "a definite throw allocates nothing")
        val missingClass = CoreTester.execute(CLASS_NAME, "filledNewArrayOfMissingClass()V")
        assertEquals(SideEffectLevel.NONE, missingClass.explored.sideEffectLevel(0), "and neither does the other one")
    }

    /**
     * An `I`-component element register holding a `Float` fills its slot with the register's 32 bits: `5.9f` is
     * `0x40BCCCCD`, i.e. 1086115021. Verifier-excluded input ([CON] B1).
     */
    @Test
    fun aFloatElementIsReadByItsBits() {
        initial.setRegisters(0, 2, "I", 1, 5.9f, "F", 2, 7, "I")
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val array = trackedArray(graph, register = 3)
        assertEquals(
            listOf(Value.Known(2, INT), Value.Known(1086115021, INT), Value.Known(7, INT)), array.elements(),
        )
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0))
    }

    /**
     * Every element register is read even after an unprovable one: `registersRead` feeds
     * `ExecutionGraph.availableRegisters`, so an unreported read frees a live register.
     */
    @Test
    fun everyElementRegisterIsReportedAsReadEvenAfterAnUnprovableOne() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "I", 1, 3, "I", 2, 5, "I")
        })
        assertEquals(setOf(0, 1, 2), graph.registersRead(0))
    }

    /**
     * The result slot is not a register, so the op assigns none. The completion and reach checks keep the empty
     * set from passing for an op that never ran.
     */
    @Test
    fun theResultSlotIsNotAnAssignedRegister() {
        val graph = CoreTester.execute(CLASS_NAME, "filledNewArrayThenMoveResult()V")
        assertEquals(CompletionStatus.Complete, graph.completion(), "an unbuilt op would satisfy the empty-set claim trivially")
        assertTrue(graph.wasReached(0), "and so would a node that never executed")
        assertEquals(emptySet<Int>(), graph.registersAssigned(0))
        assertEquals(setOf(3), graph.registersAssigned(3), "the move-result-object is what assigns v3")
    }

    /** The tracked array every returning path agrees `register` holds. */
    private fun trackedArray(graph: ExecutionGraph, register: Int): HeapObject.TrackedArray {
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val consensus = trusted!!.terminatingConsensus(register)
        assertTrue(
            consensus is Value.Known && consensus.raw is HeapRef,
            "register $register should hold a tracked array, but was $consensus",
        )
        val actual = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef).agreed()
        assertTrue(actual is HeapObject.TrackedArray, "no array behind the ref; got $actual")
        return actual as HeapObject.TrackedArray
    }
}
