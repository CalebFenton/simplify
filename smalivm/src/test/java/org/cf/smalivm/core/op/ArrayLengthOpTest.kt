package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.Outcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `array-length` (12x). The length comes from the array object, not the register's tag, so some seeds below tag
 * an `Integer[]` or `String[]` as `[I`. Expected values follow `docs/DALVIK-SEMANTICS.md`, "`array-length`".
 */
class ArrayLengthOpTest {
    companion object {
        private const val CLASS_NAME = "Larray_length_test;"
        private const val NPE_MESSAGE = "Attempt to get length of null array"
    }

    private fun length(name: String, array: Any, type: String, expected: Any): DynamicTest =
        DynamicTest.dynamicTest(name) {
            val initial = CoreState().apply { setRegisters(0, array, type) }
            val expectedState = CoreState().apply { setRegisters(0, expected, "I") }
            CoreTester.test(CLASS_NAME, "getLength()V", initial, expectedState)
        }

    @TestFactory
    fun theLengthIsTheArrayObjectsEntryCount(): List<DynamicTest> = listOf(
        length("empty Integer[] tagged [I", arrayOf<Int>(), "[I", 0),
        length("Integer[] tagged [I", arrayOf(1, 2, 3), "[I", 3),
        length("String[] tagged [I", arrayOf("isn't", "this", "where"), "[I", 3),
        length("empty int[]", intArrayOf(), "[I", 0),
        length("int[]", intArrayOf(1, 2, 3), "[I", 3),
        length("long[]", longArrayOf(1, 2, 3, 4), "[J", 4),
        length("short[]", shortArrayOf(1, 2), "[S", 2),
    )

    @TestFactory
    fun anUnknownArrayHasAnUnknownLength(): List<DynamicTest> = listOf(
        length("unknown Integer[]", CoreState.UNKNOWN, "[Ljava/lang/Integer;", CoreState.UNKNOWN),
        length("unknown int[]", CoreState.UNKNOWN, "[I", CoreState.UNKNOWN),
    )

    /**
     * Bug class: swapped 12x operands (`B|A|op`, A the destination). Swapped, the op reads the non-array v1,
     * answers `Unknown(I)` into v2 and clobbers the array register, so both v1 and v2 are asserted.
     * `intArrayOf` because the surviving array register is type-checked and `Integer[]` is not a `[I`.
     */
    @Test
    fun theDestinationAndArrayOperandsAreNotInterchangeable() {
        val initial = CoreState().apply { setRegisters(0, 0x1177, "I", 1, 0x2288, "I", 2, intArrayOf(1, 2, 3), "[I") }
        val expected = CoreState().apply { setRegisters(0, 0x1177, "I", 1, 3, "I", 2, intArrayOf(1, 2, 3), "[I") }
        CoreTester.test(CLASS_NAME, "getLengthIntoADifferentRegister()V", initial, expected)
    }

    /**
     * Bug class: forking an NPE for a tracked array, which is provably non-null, so a deobfuscator would see a
     * reachable `catch (NullPointerException)` that Dalvik never enters. Also pins the result as one narrow register.
     */
    @Test
    fun aTrackedArrayIsProvablyNonNullSoNoNullPointerExceptionIsForked() {
        val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
            setRegisters(0, arrayOf(1, 2, 3), "[I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor")
        assertTrue(graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(), "nothing may throw")
        assertEquals(setOf(0), graph.registersAssigned(0), "a narrow I result owns one register")
    }

    /**
     * A non-null reference that is not an array is verifier-excluded input; the engine answers `Unknown(I)` and
     * forks no NPE. Bug class: treating "not a tracked array" as "may be null".
     */
    @Test
    fun aKnownNonArrayIsProvablyNonNullSoItsLengthIsUnknownAndNothingThrows() {
        val initial = CoreState().apply { setRegisters(0, "not an array at all", "Ljava/lang/String;") }
        val expected = CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
            setRegisters(0, "not an array at all", "Ljava/lang/String;")
        })
        CoreTester.test(CLASS_NAME, "getLength()V", initial, expected)
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "a Known reference with a non-null raw is provably non-null, so the NPE is infeasible",
        )
    }

    /** Bug class: not forking the NPE for an array that may be null, leaving a feasible path unexplored. */
    @Test
    fun anUnprovableArrayForksTheNullPointerExceptionAsWellAsTheLengthRead() {
        val graph = CoreTester.execute(CLASS_NAME, "getLength()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is not a truncation")
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one normal successor")
        assertEquals(1, graph.outcomes.filterIsInstance<Outcome.Threw>().size, "and exactly one escaping throw")
    }

    /**
     * Each seed reads as a null reference: a null raw, and any primitive-typed register (switch interpreter; see
     * `docs/DALVIK-SEMANTICS.md`, "Reading a register as a reference"). A non-zero primitive is verifier-excluded
     * input. `getLengthWithCatch()V` catches the NPE into v0, so the following `nop` at address 1 is unreached.
     */
    @TestFactory
    fun aNullArrayThrowsNullPointerException(): List<DynamicTest> = listOf(
        Triple("null short[]", null, "[S"),
        Triple("integral zero", 0, "I"),
        Triple("non-zero primitive", 5, "I"),
    ).map { (name, value, type) ->
        DynamicTest.dynamicTest(name) {
            val initial = CoreState().apply { setRegisters(0, value, type) }
            CoreTester.testCaught(
                CLASS_NAME, "getLengthWithCatch()V", initial,
                exceptionType = "Ljava/lang/NullPointerException;",
                message = NPE_MESSAGE,
                caughtInRegister = 0,
                unreachedAddress = 1,
            )
        }
    }
}
