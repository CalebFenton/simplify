package org.cf.smalivm.core.op

import java.util.concurrent.TimeUnit
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.MAX_TRACKED_ARRAY_LENGTH
import org.cf.smalivm.core.Outcome
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
import org.junit.jupiter.api.Timeout

/**
 * `new-array` (op 23, 22c: two code units, so the fixtures' fall-through is at 2). Expected values follow
 * `docs/DALVIK-SEMANTICS.md#new-array-op-23-22c`: [BC] names no exception, so `NegativeArraySizeException` and
 * the resolution failure are inherited from [JVMS] §6.5, with ART's messages and ART's length-before-class order.
 */
class NewArrayOpTest {
    private lateinit var initial: CoreState

    @BeforeEach
    fun setUp() {
        initial = CoreState()
    }

    /**
     * A known length allocates a tracked array of fresh elements: `0` for a primitive component and `null` for
     * a reference one (`[[I`'s component `[I`). A `Short` length is read as an `Int` through `Coerce.toInt`.
     */
    @TestFactory
    fun aKnownLengthAllocatesAnArrayOfDefaultElements(): List<DynamicTest> = listOf(
        allocates("createIntegerArray()V", 1 to "I", IntArray(1), "[I"),
        allocates("createIntegerArray()V", 1.toShort() to "S", IntArray(1), "[I"),
        allocates("create2DIntegerArray()V", 3 to "I", arrayOfNulls<IntArray>(3), "[[I"),
    )

    private fun allocates(method: String, length: Pair<Any, String>, array: Any, type: String): DynamicTest =
        DynamicTest.dynamicTest("$method, length ${length.first} as ${length.second}") {
            CoreTester.test(
                CLASS_NAME, method,
                CoreState().apply { setRegisters(0, length.first, length.second) },
                CoreState().apply { setRegisters(0, array, type) },
            )
        }

    /** An array of a class under analysis is tracked with the instruction's type and the seeded length. */
    @TestFactory
    fun anArrayOfALocalClassIsTrackedWithItsDeclaredType(): List<DynamicTest> = listOf(
        "createLocalInstanceArray()V" to "[$CLASS_NAME",
        "create2DLocalInstanceArray()V" to "[[$CLASS_NAME",
    ).mapIndexed { i, (method, descriptor) ->
        DynamicTest.dynamicTest(descriptor) {
            val length = i + 1
            val graph = CoreTester.execute(CLASS_NAME, method, CoreState().apply { setRegisters(0, length, "I") })
            assertEquals(CompletionStatus.Complete, graph.completion())
            val trusted = graph.trusted()
            assertNotNull(trusted, "a Complete exploration must yield a trusted view")
            val consensus = trusted!!.terminatingConsensus(0)
            assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked array: $consensus")
            assertEquals(VirtualType.of(descriptor), consensus.type)
            val array = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef).agreed()
            assertTrue(array is HeapObject.TrackedArray, "no array behind the ref; got $array")
            assertEquals(length, (array as HeapObject.TrackedArray).length)
            assertEquals(VirtualType.of(descriptor), array.type)
        }
    }

    /**
     * `22c` is `B|A|op CCCC` with A the destination ([FMT]). Every other fixture is `new-array v0, v0`, so only
     * this one sees an operand swap; v0 is a witness the instruction never names.
     */
    @Test
    fun theDestinationAndLengthOperandsAreNotInterchangeable() {
        val length = 3
        initial.setRegisters(0, 0x1177, "I", 2, length, "I")
        val expected = CoreState().apply { setRegisters(0, 0x1177, "I", 1, IntArray(length), "[I", 2, length, "I") }
        CoreTester.test(CLASS_NAME, "createIntegerArrayIntoADifferentRegister()V", initial, expected)
    }

    /**
     * An absent component class fails resolution with `NoClassDefFoundError`, an `Error` the fixture's
     * `catch (Exception)` cannot catch, so it escapes. The op declares v0 assigned, but the throw leaves the
     * seeded length in it.
     */
    @Test
    fun anAbsentComponentClassThrowsNoClassDefFoundError() {
        initial.setRegisters(0, 1, "I")
        CoreTester.testThrows(
            CLASS_NAME, "createNonExistentArrayClass()V", initial,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "Failed resolution of: [Ldoes/not/exist;", // ART `DoResolveType`; DEVICE `Fx_NewArrayAbsentType`
        )
        val graph = CoreTester.execute(CLASS_NAME, "createNonExistentArrayClass()V", initial)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "the fall-through is only provably unexplored on a run that explored everything",
        )
        assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow reference result owns one register")
        assertEquals(
            Value.Known(1, VirtualType.of("I")), graph.nodePile(0).first().frame.peek(0),
            "a throwing new-array must leave its destination register untouched",
        )
    }

    /**
     * A negative length throws `NegativeArraySizeException` with the length in decimal (ART
     * `ThrowNegativeArraySizeException(int)`). The fixture's `catch (Exception)` handler catches it.
     */
    @Test
    fun aKnownNegativeLengthThrowsNegativeArraySizeException() {
        CoreTester.testCaught(
            CLASS_NAME, "createIntegerArrayWithCatch()V",
            CoreState().apply { setRegisters(0, -1, "I") },
            exceptionType = "Ljava/lang/NegativeArraySizeException;",
            message = "-1",
            caughtInRegister = 0,          // `move-exception v0`
            unreachedAddress = 2,          // new-array is 22c, two code units
        )
    }

    /**
     * A known non-negative length cannot throw, so nothing forks. The fixture's handler would catch a spurious
     * fork, so the successor count is the discriminating assertion.
     */
    @Test
    fun aKnownNonNegativeLengthAllocatesWithoutForking() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArrayWithCatch()V", CoreState().apply {
            setRegisters(0, 2, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor")
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "no throw escapes",
        )
    }

    /**
     * An unknown length may be negative, so the engine forks an `Unknown` array and a message-less
     * `NegativeArraySizeException`, which escapes this handler-free fixture.
     */
    @Test
    fun anUnknownLengthForksNegativeArraySizeExceptionBesideAnUnknownArray() {
        val expected = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
        val seed = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, expected)
        CoreTester.testThrows(CLASS_NAME, "createIntegerArray()V", seed, NEGATIVE_ARRAY_SIZE)
    }

    /**
     * A length register holding a `Float` is read by its 32 bits: `-0.5f` is `0xBF000000`, i.e. -1090519040, so
     * `new-array` definitely throws `NegativeArraySizeException`.
     */
    @Test
    fun aFloatLengthIsReadByItsBits() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", CoreState().apply { setRegisters(0, -0.5f, "F") })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NegativeArraySizeException;"), CoreTester.escapingTypes(graph))
        assertEquals(1, graph.outcomes.size, "length -1090519040 is definitely negative: $graph")
    }

    /**
     * With an unknown length and an absent component class both throws are feasible: the handler catches the
     * `NegativeArraySizeException` and the `NoClassDefFoundError` escapes.
     */
    @Test
    fun anUnknownLengthWithAnAbsentComponentForksBothThrows() {
        val seed = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        CoreTester.testCaught(
            CLASS_NAME, "createBothBadArrayClass()V", seed,
            exceptionType = NEGATIVE_ARRAY_SIZE,
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
        CoreTester.testThrows(
            CLASS_NAME, "createBothBadArrayClass()V", seed,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
            message = "Failed resolution of: [Ldoes/not/exist;",
        )
    }

    /**
     * The switch interpreter checks the length before resolving the class (`CheckArrayAlloc`), so a negative
     * length with an absent class throws `NegativeArraySizeException`, which the handler catches. The reversed
     * order would let `NoClassDefFoundError` escape, leaving no returning path for the caught-type check.
     */
    @Test
    fun aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst() {
        CoreTester.testCaught(
            CLASS_NAME, "createBothBadArrayClass()V",
            CoreState().apply { setRegisters(0, -1, "I") },
            exceptionType = "Ljava/lang/NegativeArraySizeException;",
            message = "-1",
            caughtInRegister = 0,
            unreachedAddress = 2,
        )
    }

    /**
     * A length over [MAX_TRACKED_ARRAY_LENGTH] yields an untracked `Unknown` array instead of allocating one
     * slot per element, which could exhaust the host; the length is non-negative, so nothing forks.
     */
    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun aLengthOverTheTrackedBoundYieldsAnUnknownArray() {
        for (length in listOf(MAX_TRACKED_ARRAY_LENGTH + 1, Int.MAX_VALUE)) {
            val seed = CoreState().apply { setRegisters(0, length, "I") }
            val want = CoreState().apply { setRegister(0, Value.Unknown(VirtualType.of("[I"))) }
            CoreTester.test(CLASS_NAME, "createIntegerArray()V", seed, want)
            val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", seed)
            assertTrue(graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(), "length $length cannot throw")
        }
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun aLengthAtTheTrackedBoundIsAllocated() {
        val graph = CoreTester.execute(CLASS_NAME, "createIntegerArray()V", CoreState().apply {
            setRegisters(0, MAX_TRACKED_ARRAY_LENGTH, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()!!
        val consensus = trusted.terminatingConsensus(0)
        assertTrue(consensus is Value.Known && consensus.raw is HeapRef, "must be a tracked array: $consensus")
        val array = trusted.terminatingHeapObject((consensus as Value.Known).raw as HeapRef).agreed()
        assertEquals(MAX_TRACKED_ARRAY_LENGTH, (array as HeapObject.TrackedArray).length)
    }

    private companion object {
        private const val CLASS_NAME = "Lnew_array_test;"
        private const val NEGATIVE_ARRAY_SIZE = "Ljava/lang/NegativeArraySizeException;"
    }
}
