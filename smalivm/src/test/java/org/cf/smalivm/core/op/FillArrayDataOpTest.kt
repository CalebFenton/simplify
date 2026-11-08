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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `fill-array-data` (op 26, format 31t: three code units, so the successor of an op at 0 is address 3). Expected
 * values follow `docs/DALVIK-SEMANTICS.md#fill-array-data-op-26-31t-and-the-array-data-payload`: the payload is
 * data decoded at build time, and both exceptions are ART's `FillArrayData` with its messages. dexlib2's
 * `Opcode.canThrow()` is false for this opcode although ART throws from it, so the engine never consults it.
 */
class FillArrayDataOpTest {
    private lateinit var initial: CoreState

    companion object {
        private const val CLASS_NAME = "Lfill_array_data_test;"
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }

    @BeforeEach
    fun setUp() {
        initial = CoreState()
    }

    /**
     * ART copies the payload's bytes into the array, so a payload byte `2` sits in a boolean element as `2`. A
     * `Boolean` raw cannot hold that byte, so the element is unknown rather than a confident `false`.
     */
    @Test
    fun aBooleanPayloadByteOtherThanZeroOrOneFillsAnUnknownElement() {
        initial.setRegisters(0, BooleanArray(2), "[Z")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataBooleanTwo()V", initial)
        val trusted = graph.trusted()
        assertNotNull(trusted, "the fill completes on every path")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        val boolean = VirtualType.of("Z")
        assertEquals(listOf(Value.Unknown(boolean), Value.Known(true, boolean)), listOf(array.element(0), array.element(1)))
    }

    /**
     * Every payload width and primitive component. [BC] requires the table to fit and leaves an array's tail
     * alone when the array is longer than the table, so the `[I` of length 7 keeps its two trailing zeros.
     */
    @TestFactory
    fun theFillStoresEachPayloadElementInTheArray(): List<DynamicTest> = listOf(
        fill("fillArrayDataBoolean()V", BooleanArray(4), booleanArrayOf(true, true, false, true), "[Z"),
        fill("fillArrayDataByte()V", ByteArray(4), byteArrayOf(0xa, 0xb, 0xc, 0xd), "[B"),
        fill("fillArrayDataChar()V", CharArray(3), charArrayOf('a', 'b', 'c'), "[C"),
        fill("fillArrayDataDouble()V", DoubleArray(2), doubleArrayOf(0.1, 1.5), "[D"),
        fill("fillArrayDataFloat()V", FloatArray(2), floatArrayOf(1f, 2f), "[F"),
        fill("fillArrayDataInt()V", IntArray(5), intArrayOf(1, 2, 3, 4, 5), "[I"),
        fill("fillArrayDataInt()V", IntArray(7), intArrayOf(1, 2, 3, 4, 5, 0, 0), "[I"),
        fill("fillArrayDataLongWithLongs()V", LongArray(3), longArrayOf(0x1000000000L, 0x2000000000L, 0x3L), "[J"),
        fill("fillArrayDataLongWithInts()V", LongArray(3), longArrayOf(0x10, 0x20, 0x30), "[J"),
        fill("fillArrayDataShortWithShorts()V", ShortArray(3), shortArrayOf(100, 200, 5), "[S"),
        fill("fillArrayDataShortWithInts()V", ShortArray(3), shortArrayOf(0x10, 0x20, 0x30), "[S"),
    )

    private fun fill(method: String, seed: Any, filled: Any, type: String): DynamicTest =
        DynamicTest.dynamicTest("$method into $type of ${java.lang.reflect.Array.getLength(seed)}") {
            CoreTester.test(
                CLASS_NAME, method,
                CoreState().apply { setRegisters(0, seed, type) },
                CoreState().apply { setRegisters(0, filled, type) },
            )
        }

    /**
     * The payload is data: the fill happens at the op and the payload address is never reached. The completion
     * check matters because an absent address is a dead-code claim.
     */
    @Test
    fun thePayloadAddressIsDataAndIsNeverExecuted() {
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", CoreState().apply {
            setRegisters(0, IntArray(5), "[I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val payload = graph.methodView.payloadAddresses.single()
        assertFalse(graph.wasReached(payload), "a payload is data, not code")
        assertTrue(graph.wasReached(0), "and the fill itself ran")
    }

    /**
     * A payload longer than the array: ART length-checks before a single `memcpy`, so it stores nothing, and
     * the message's second number is the element count. The fixture catches the exception.
     */
    @Test
    fun aPayloadLongerThanTheArrayThrowsWithoutStoringAnything() {
        val seed = { CoreState().apply { setRegisters(0, IntArray(2), "[I") } }
        CoreTester.testCaught(
            CLASS_NAME, "fillArrayDataIntWithCatch()V", seed(),
            exceptionType = "Ljava/lang/ArrayIndexOutOfBoundsException;",
            message = "failed FILL_ARRAY_DATA; length=2, index=5",
            caughtInRegister = 1,     // `move-exception v1` -- v0 must keep the array; see the fixture
            unreachedAddress = 3,     // 31t is three code units
        )
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataIntWithCatch()V", seed())
        assertEquals(3, graph.methodView.nextAddress(0), "fill-array-data is 31t, three code units")
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "nothing escapes, of any type",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "a definite bounds exception stores nothing, so it declares no effect",
        )
        val trusted = graph.trusted()!!
        val ref = (trusted.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val array = trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
        val zero = Value.Known(0, VirtualType.of("I"))
        assertEquals(zero, array.element(0), "nothing stored: still the seed's 0")
        assertEquals(zero, array.element(1), "and the payload's second element did not land either")
    }

    /**
     * An empty table fits every array, including a zero-length one, so the bounds exception is infeasible
     * whatever the array register holds. Forking it would make a dead `catch` look live. Unseeded, only the NPE
     * escapes; seeded with a zero-length tracked array, nothing throws.
     */
    @Test
    fun aZeroElementPayloadCannotOverrunAnyArraySoNoBoundsExceptionIsForked() {
        val unprovable = CoreTester.execute(CLASS_NAME, "fillArrayDataEmptyPayload()V")
        assertEquals(CompletionStatus.Complete, unprovable.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(unprovable),
            "an empty table fits every array, so the bounds throw is infeasible",
        )
        assertEquals(
            1, unprovable.nodePile(0).first().children.size,
            "one successor, the fall-through; the NPE is a terminal, not a child",
        )

        val emptyArray = CoreTester.execute(CLASS_NAME, "fillArrayDataEmptyPayload()V", CoreState().apply {
            setRegisters(0, IntArray(0), "[I")
        })
        assertEquals(CompletionStatus.Complete, emptyArray.completion())
        assertTrue(
            CoreTester.escapingTypes(emptyArray).isEmpty(),
            "zero elements fit a zero-length array, so nothing throws",
        )
        assertEquals(
            SideEffectLevel.NONE, emptyArray.explored.sideEffectLevel(0),
            "and a tracked fill of nothing is still modelled exactly",
        )
    }

    /**
     * Aliases can observe a non-empty fill of a tracked array, so it is `WEAK`. The unreached payload address
     * contributes `NONE` to the method maximum; an executed payload would contribute `STRONG` as an unsupported op.
     */
    @Test
    fun aFillIntoATrackedArrayDeclaresAnObservableWeakEffect() {
        val graph = graph("fillArrayDataInt()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "an alias can observe the tracked fill")
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so every path returns")
        assertEquals(
            SideEffectLevel.WEAK, trusted!!.methodSideEffectLevel(),
            "and the unreached payload address contributes NONE rather than poisoning the method maximum",
        )
    }

    /**
     * ART's null check precedes its length check, so a null array throws only the NPE even though the
     * five-element payload might not fit. `testThrows` passes whenever the NPE is among the throws, so the
     * escape list and child count assert that no bounds exception is also forked.
     */
    @Test
    fun aNullArrayThrowsNullPointerExceptionBeforeAnyBoundsExceptionAndStoresNothing() {
        initial.setRegisters(0, null, "[I")
        CoreTester.testThrows(
            CLASS_NAME, "fillArrayDataInt()V", initial,
            exceptionType = "Ljava/lang/NullPointerException;",
            message = "null array in FILL_ARRAY_DATA",
        )
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is an outcome, not a truncation")
        assertEquals(
            listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph),
            "the NPE precedes the length check, so no bounds exception is forked",
        )
        assertEquals(0, graph.nodePile(0).first().children.size, "a definite throw has no successor to explore")
        assertEquals(3, graph.methodView.nextAddress(0), "fill-array-data is 31t, three code units")
        assertFalse(graph.wasReached(3), "and no fall-through")
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "nothing was stored, so there is no effect to account for",
        )
    }

    /**
     * A primitive-typed array register reads as a null reference whatever its value ([BC]: `(Object) null ==
     * (int) 0`; ART's switch interpreter clears the reference slot on every primitive write), so both seeds
     * throw a definite NPE. See `docs/DALVIK-SEMANTICS.md` on the two interpreter tiers for the non-zero case.
     */
    @TestFactory
    fun aPrimitiveArrayRegisterThrowsADefiniteNullPointerException(): List<DynamicTest> = listOf(0, 5).map { raw ->
        DynamicTest.dynamicTest("const $raw") {
            val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", CoreState().apply { setRegisters(0, raw, "I") })
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
            assertEquals(0, graph.nodePile(0).first().children.size, "no fall-through: the throw is definite")
            assertEquals(
                SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
                "nothing was stored, so there is no effect to account for",
            )
        }
    }

    /**
     * An unseeded array register is `Value.Top`, so null, too short, and long enough are all feasible and all
     * three outcomes are explored. The fill lands in an object the engine cannot name, so it is `STRONG`; this is
     * the path a parameter array takes. The order of the two escapes is incidental; list equality catches a
     * dropped or extra fork.
     */
    @Test
    fun anUnprovableArrayForksBothFeasibleExceptionsAndDeclaresAStrongEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf("Ljava/lang/NullPointerException;", "Ljava/lang/ArrayIndexOutOfBoundsException;"),
            CoreTester.escapingTypes(graph),
            "both throws are feasible when nothing is known about the array",
        )
        assertEquals(
            1, graph.nodePile(0).first().children.size,
            "one successor, the fall-through; the two escaping throws are terminals, not children",
        )
        assertTrue(graph.wasReached(3), "the fall-through is feasible too and must be explored")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the fill goes to an object the engine cannot name, so no heap state records it",
        )
    }

    /**
     * A `Known` non-null raw with no tracked array behind it is verifier-rejected input ([CON] B1), so the engine
     * chooses, consistently with `aget`, `aput`, and `array-length`: no NPE (provably non-null) and no bounds
     * exception (no length). ART would still copy the payload somewhere, so the effect is `STRONG`, and the
     * register keeps its `Known` value because the fill writes memory, not v0.
     */
    @Test
    fun aProvablyNonNullNonArrayFillsNothingForksNothingAndDeclaresAStrongEffect() {
        initial.setRegisters(0, "not an array", "Ljava/lang/String;")
        val graph = CoreTester.execute(CLASS_NAME, "fillArrayDataInt()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            CoreTester.escapingTypes(graph).isEmpty(),
            "a Known reference with a non-null raw is provably non-null and has no length",
        )
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor: the fall-through")
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0))
        val trusted = graph.trusted()
        assertNotNull(trusted, "nothing threw, so the single path returns")
        assertEquals(
            Value.Known("not an array", STRING), trusted!!.terminatingConsensus(0),
            "the fill goes to memory, not to v0",
        )
    }

    /** The fill reads the array register and writes the array object, never a register. */
    @Test
    fun theFillReadsTheArrayRegisterAndAssignsNothing() {
        assertEquals(setOf(0), graph("fillArrayDataInt()V").registersRead(0))
        assertEquals(emptySet<Int>(), graph("fillArrayDataInt()V").registersAssigned(0))
    }

    private fun graph(methodDescriptor: String): ExecutionGraph =
        CoreTester.execute(CLASS_NAME, methodDescriptor, CoreState().apply { setRegisters(0, IntArray(5), "[I") })
}
