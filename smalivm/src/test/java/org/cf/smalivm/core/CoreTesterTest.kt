package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * [CoreTester]'s facade sits under most core tests, so each test here pins a way the facade could pass
 * while proving nothing. Expected values come from the fixture's Dalvik semantics, never from the engine.
 *
 * `spinForever()V` is the truncation fixture: its unbounded backward `goto` exhausts the default
 * per-address budget, so the completion gate is tested against truncation in general.
 */
class CoreTesterTest {

    @Test
    fun consensusFromATruncatedRunIsRefused() {
        val expected = CoreState().apply { setRegisters(0, 0, "I") }
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(SPINE, "spinForever()V", CoreState(), expected)
        }
        assertTrue(
            failure.message!!.contains("exploration must complete before any consensus may be trusted"),
            "must fail on the completion gate, not on a value comparison: ${failure.message}",
        )
    }

    @Test
    fun anEmptyExpectationIsRefused() {
        // An empty expectation would assert only that the run completed.
        val failure = assertThrows(IllegalArgumentException::class.java) {
            CoreTester.test(SPINE, "returnFive()I", CoreState(), CoreState())
        }
        assertTrue(failure.message!!.contains("at least one expected register"), failure.message!!)
    }

    @TestFactory
    fun aSeedPassedAsItsOwnExpectationIsRefused(): List<DynamicTest> {
        // The same object as seed and expectation reduces every assertion to "the seed survived".
        // `throwCaught` writes only v0, so the seeded v1 would be echoed back unchanged.
        val scalar = CoreState().apply {
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        val array = CoreState().apply { setRegisters(0, IntArray(1), "[I") }
        return listOf(
            Triple("scalar", "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;", scalar),
            Triple("array", "identity()Ljava/lang/Object;", array),
        ).map { (name, method, state) ->
            DynamicTest.dynamicTest(name) {
                val failure = assertThrows(IllegalArgumentException::class.java) {
                    CoreTester.test(SPINE, method, state, state)
                }
                assertTrue(failure.message!!.contains("both seed and expectation"), failure.message!!)
            }
        }
    }

    @TestFactory
    fun anExpectedRegisterOutsideTheFrameIsRefused(): List<DynamicTest> {
        // `valueIn` answers Top for an out-of-range register, so an expectation of Top would pass for any
        // mistyped register. `returnFive()I` is `.registers 1`.
        return listOf(
            "scalar" to CoreState().apply { setRegister(999, Value.Top) },
            "negative" to CoreState().apply { setRegister(-1, Value.Top) },
            "array" to CoreState().apply { setRegisters(999, IntArray(1), "[I") },
        ).map { (name, expected) ->
            DynamicTest.dynamicTest(name) {
                val failure = assertThrows(IllegalArgumentException::class.java) {
                    CoreTester.test(SPINE, "returnFive()I", CoreState(), expected)
                }
                assertTrue(failure.message!!.contains("outside"), failure.message!!)
            }
        }
    }

    @Test
    fun aThrowFromATruncatedRunIsRefused() {
        // "Some path threw X" survives truncation, but callers pair it with negative claims that do not.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testThrows(
                SPINE, "spinForever()V", CoreState(),
                exceptionType = "Ljava/lang/ArithmeticException;",
            )
        }
        assertTrue(
            failure.message!!.contains("the other half needs a finished run"),
            "must fail on the completion gate, not on the absent throw: ${failure.message}",
        )
    }

    @Test
    fun visitationReportsExactlyTheAddressesThatRan() {
        // `gotoSkip()I` is @0 const/4, @1 goto, @2 const/4, @3 return. The jumped-over @2 separates the
        // executed address set from the static one. The truncation gate on this path is covered by
        // `SpineBehaviorTest.assertVisitedRefusesToReadCoverageFromATruncatedRun`.
        CoreTester.testVisitation(SPINE, "gotoSkip()I", CoreState(), intArrayOf(0, 1, 3))
    }

    @Test
    fun throwsMatchesTheThrownTypeAndItsMessage() {
        val initial = CoreState().apply {
            // `.registers 2` and one reference parameter, so p0 is v1.
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        CoreTester.testThrows(
            SPINE, "throwUncaught(Ljava/lang/RuntimeException;)V", initial,
            exceptionType = "Ljava/lang/RuntimeException;", message = "boom",
        )
    }

    @Test
    fun throwsReadsTheExactMultiArmEndingFrame() {
        CoreTester.testThrows(
            "Ldifferential/exceptions;",
            "ambiguousLocalDivide(I)V",
            CoreState(),
            exceptionType = "Ljava/lang/ArithmeticException;",
            message = "divide by zero",
        )
    }

    @Test
    fun throwsRejectsAThrowOfADifferentType() {
        val initial = CoreState().apply {
            setRegister(1, RuntimeException("boom"), "Ljava/lang/RuntimeException;")
        }
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testThrows(
                SPINE, "throwUncaught(Ljava/lang/RuntimeException;)V", initial,
                exceptionType = "Ljava/io/IOException;",
            )
        }
        assertTrue(failure.message!!.contains("expected a Ljava/io/IOException;"), failure.message!!)
    }

    @Test
    fun aHostArraySeedBecomesATrackedArrayWithPerElementValues() {
        // A seeded host array must land in the heap as a tracked array, not as a live host raw that
        // Frame.copy would share between siblings. `frame.peek(0)` reads the post-op frame, so the fixture
        // must leave its array operand alone: `aget v2, v0, v1` does.
        val graph = CoreTester.execute("Laget_test;", "getWithThreeDistinctRegisters()V", CoreState().apply {
            setRegisters(0, intArrayOf(0x42), "[I", 1, 0, "I")
        })
        val seeded = graph.nodePile(0).first().frame.peek(0) as Value.Known
        assertTrue(seeded.raw is HeapRef, "a seeded array must be tracked, not a live host raw: ${seeded.raw}")
        val array = graph.nodePile(0).first().frame.heap.arrayAt(seeded.raw as HeapRef)!!
        assertEquals(1, array.length)
        assertEquals(Value.Known(0x42, VirtualType.of("I")), array.element(0))
    }

    @Test
    fun setRegisterTwoArgOverloadClearsAnyPriorArraySeedForThatRegister() {
        // Otherwise register 0 sits in both maps and CoreTester.test asserts two exclusive claims about it.
        val state = CoreState().apply {
            setRegisters(0, IntArray(1), "[I")
            setRegister(0, Value.Unknown(VirtualType.of("I")))
        }
        assertTrue(state.arrays.isEmpty(), "the 2-arg setRegister must clear a prior array seed: ${state.arrays}")
        assertEquals(Value.Unknown(VirtualType.of("I")), state.getRegister(0))
    }

    @Test
    fun theUnknownSentinelIsHonoredPerElementInsideAHostArray() {
        // A sentinel nested in a host array must become Unknown, not a raw object the engine then copies.
        val state = CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") }
        val spec = state.arrays.getValue(0)
        val elements = CoreState.elementsOf(spec)
        assertEquals(Value.Unknown(VirtualType.of("I")), elements[0])
        assertEquals(Value.Known(5, VirtualType.of("I")), elements[1])
    }

    @Test
    fun elementsOfUsesTheHostArraysOwnClassNotADisagreeingArrayDescriptorTag() {
        // Without a sentinel, the host array's own class decides the component type: a String[] tagged
        // "[Ljava/lang/Object;" holds Strings.
        val state = CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") }
        val elements = CoreState.elementsOf(state.arrays.getValue(0))
        assertEquals(Value.Known("Arrakis", VirtualType.of("Ljava/lang/String;")), elements[0])
    }

    @Test
    fun anArrayExpectationWhoseLengthDiffersFails() {
        // Element 0 agrees, so only the length check can catch the mismatch.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, intArrayOf(1, 2, 3), "[I") },
                CoreState().apply { setRegisters(0, intArrayOf(1), "[I") },
            )
        }
        assertTrue(failure.message!!.contains("length of register"), failure.message!!)
    }

    @Test
    fun aDeclaredArrayTypeAcceptsALegallyCovariantRuntimeArray() {
        // String[] <: Object[], so an equality check on declared-vs-runtime type would reject legal input.
        CoreTester.test(
            SPINE, "identity()Ljava/lang/Object;",
            CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") },
            CoreState().apply { setRegisters(0, arrayOf("Arrakis"), "[Ljava/lang/Object;") },
        )
    }

    @Test
    fun aPrimitiveDeclaredArrayTypeRejectsAReferenceComponentRuntimeArray() {
        // Primitive arrays are invariant, so a declared "[I" can never hold a runtime String[]; no verified
        // dex reaches this shape (DALVIK-SEMANTICS.md, aget*/aput* table).
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, arrayOf<String?>(null), "[I") },
                CoreState().apply { setRegisters(0, arrayOf<String?>(null), "[I") },
            )
        }
        assertTrue(failure.message!!.contains("is not assignable to declared type"), failure.message!!)
    }

    @Test
    fun aSentineledArraySeedAllocatesWithTheDeclaredTypeNotTheObjectsErasedClass() {
        // `arrayOf(UNKNOWN, 5)` is an Object[] on the host, but the seed means `[I`. Allocating with the
        // host class would make the round-trip's assignability check fail, since `[Object` is not a `[I`.
        CoreTester.test(
            SPINE, "identity()Ljava/lang/Object;",
            CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") },
            CoreState().apply { setRegisters(0, arrayOf(CoreState.UNKNOWN, 5), "[I") },
        )
    }

    @Test
    fun anArrayExpectationComparesEveryElementNotJustTheFirst() {
        // `identity()` returns v0 unchanged; element 0 agrees and element 1 does not.
        assertThrows(AssertionError::class.java) {
            CoreTester.test(
                SPINE, "identity()Ljava/lang/Object;",
                CoreState().apply { setRegisters(0, intArrayOf(1, 2), "[I") },
                CoreState().apply { setRegisters(0, intArrayOf(1, 999), "[I") },
            )
        }
    }

    @Test
    fun caughtAcceptsAThrowTheMethodsOwnHandlerCaught() {
        // `divIntWithCatch()V`: div-int @0, return-void @2, move-exception v0 @3, goto :return @4. A zero
        // divisor makes the ArithmeticException definite and the declared handler catches it. Address 2
        // is reached on the caught path too, so no `unreachedAddress` applies.
        CoreTester.testCaught(
            BINARY_MATH, "divIntWithCatch()V",
            CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
            exceptionType = "Ljava/lang/ArithmeticException;",
            message = "divide by zero",
            caughtInRegister = 0,
        )
    }

    @Test
    fun caughtRejectsAThrowThatEscaped() {
        // `divInt()V` is the same instruction with no handler, so the exception escapes.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divInt()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                message = "divide by zero",
                caughtInRegister = 0,
            )
        }
        assertTrue(
            failure.message!!.contains("ESCAPED"),
            "must fail on the escaped-vs-caught half, not on a downstream consensus read: ${failure.message}",
        )
    }

    @Test
    fun caughtRejectsAnUnreachedAddressThatWasReached() {
        // Address 2 of `divIntWithCatch()V` is `:return`, which the handler's `goto` reaches.
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                caughtInRegister = 0,
                unreachedAddress = 2,
            )
        }
        assertTrue(
            failure.message!!.contains("must not also explore the fall-through at 2"),
            "must fail on the unreached-address half: ${failure.message}",
        )
    }

    @Test
    fun caughtRejectsACaughtThrowWithADifferentMessage() {
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/ArithmeticException;",
                message = "not the message ART emits",
                caughtInRegister = 0,
            )
        }
        assertTrue(failure.message!!.contains("divide by zero"), "must compare the messages: ${failure.message}")
    }

    @Test
    fun caughtRejectsACaughtThrowOfADifferentType() {
        val failure = assertThrows(AssertionError::class.java) {
            CoreTester.testCaught(
                BINARY_MATH, "divIntWithCatch()V",
                CoreState().apply { setRegisters(0, 12, "I", 1, 0, "I") },
                exceptionType = "Ljava/lang/NullPointerException;",
                caughtInRegister = 0,
            )
        }
        assertTrue(
            failure.message!!.contains("should hold the caught Ljava/lang/NullPointerException;"),
            "must fail on the caught TYPE: ${failure.message}",
        )
    }

    private companion object {
        private const val SPINE = "Lspine/spine;"
        private const val BINARY_MATH = "Lbinary_math_test;"
    }
}
