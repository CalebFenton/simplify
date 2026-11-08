package org.cf.smalivm.core.op

import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The binary arithmetic and logical instructions in every operand domain and instruction shape, and their
 * divide-by-zero routing. Semantics: `docs/DALVIK-SEMANTICS.md`, "Binary operations".
 */
class BinaryMathOpTest {

    companion object {
        private const val CLASS_NAME = "Lbinary_math_test;"
    }

    /**
     * `add-int v0, v0, v1` names v0 as its destination, so only `registersRead` shows the read of v1. Bug
     * class: dropping the right-hand operand register from liveness.
     */
    @Test
    fun addIntReportsBothOperandRegistersAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "addInt()V")
        assertEquals(setOf(0, 1), graph.registersRead(0))
    }

    /**
     * Integral `div`/`rem` by zero throws `ArithmeticException("divide by zero")`, a provably non-zero divisor
     * cannot throw, and float/double division never throws.
     *
     * Addresses: the plain `divInt()V`/`divLong()V`/`remInt()V` methods are the op at @0 and `return-void` at
     * @2; the `*WithCatch()V` methods add the handler at @3 and its `goto` back to the return at @4.
     */
    @Nested
    @DisplayName("with a divisor that may be zero")
    inner class DivideByZero {
        private lateinit var initial: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        /**
         * Bug class: continuing past a division that definitely throws, which explores a path Dalvik never
         * takes and lets a fabricated quotient vote into consensus.
         */
        @Test
        fun aKnownZeroIntDivisorThrowsAndDoesNotContinue() {
            initial.setRegisters(0, 12, "I", 1, 0, "I")
            CoreTester.testThrows(
                CLASS_NAME, "divInt()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "divide by zero",
            )
            // `testThrows` also passes for a `[Continue, Throw]` fork, so the fall-through is asserted here.
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            // An unreached address proves dead code only on a run that explored everything.
            assertEquals(
                CompletionStatus.Complete, graph.completion(),
                "the fall-through is only provably unexplored on a run that explored everything",
            )
            assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        }

        /** The `long` domain checks its divisor separately from `int`. */
        @Test
        fun aKnownZeroLongDivisorThrows() {
            initial.setRegisters(0, 12L, "J", 2, 0L, "J")
            CoreTester.testThrows(
                CLASS_NAME, "divLong()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "divide by zero",
            )
        }

        /**
         * `rem-int` computes the remainder of the same division, so it throws the same `ArithmeticException`.
         * Bug class: treating divide-by-zero as a property of the `div` mnemonic rather than of the division.
         */
        @Test
        fun aKnownZeroIntModulusThrows() {
            initial.setRegisters(0, 12, "I", 1, 0, "I")
            CoreTester.testThrows(
                CLASS_NAME, "remInt()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "divide by zero",
            )
        }

        /**
         * Bug class: the zero-divisor guard recognizing `rem` in the `int` domain but not in `long`, which
         * neither [aKnownZeroIntModulusThrows] nor [aKnownZeroLongDivisorThrows] can catch alone.
         */
        @Test
        fun aKnownZeroLongModulusThrows() {
            initial.setRegisters(0, 12L, "J", 2, 0L, "J")
            CoreTester.testThrows(
                CLASS_NAME, "remLong()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "divide by zero",
            )
        }

        /**
         * An unknown divisor makes both a normal return and a throw feasible, so a run that reports Complete
         * must contain both. Bug class: an unexplored feasible path on a trusted run. `divInt()V` has no
         * handler, so the throw leaves the method as [Outcome.Threw].
         */
        @Test
        fun anUnknownIntDivisorExploresBothTheThrowAndTheReturn() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion(), "neither path aborts, so nothing truncates")
            assertTrue(graph.outcomes.any { it is Outcome.Threw }, "the feasible throw must be explored")
            assertTrue(graph.outcomes.any { it is Outcome.Returned }, "the feasible normal return must be explored")
        }

        /**
         * `rem-int` by zero throws as `div-int` does, so an unknown divisor forks it the same way. Bug class:
         * forking only for `div`, which drops a feasible throw on a run that still reports Complete.
         */
        @Test
        fun anUnknownIntModulusDivisorForksLikeDivisionDoes() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val graph = CoreTester.execute(CLASS_NAME, "remInt()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertTrue(graph.outcomes.any { it is Outcome.Threw }, "rem by an unknown divisor may throw")
            assertTrue(graph.outcomes.any { it is Outcome.Returned }, "and may equally not")
        }

        /**
         * The normal path's quotient is [Value.Unknown]: the divisor was not known, so neither is the result.
         * Bug class: routing the fork correctly while fabricating a constant for the branch that did not
         * throw. Asserted at the fall-through @2, because @0's frame is shared with the throwing path.
         */
        @Test
        fun theNormalPathOfTheForkAssignsAnUnknownQuotient() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            val trusted = graph.trusted()
            assertNotNull(trusted, "nothing aborted, so the run is Complete and carries a trusted view")
            assertEquals(Value.Unknown(VirtualType.of("I")), trusted!!.consensus(2, 0))
        }

        /**
         * A division that throws does not write its destination, so a definite throw leaves the pre-op value
         * standing. Bug class: a throwing node assigning `Unknown(domain)`, which is type-wrong when the
         * register held a reference and is copied into any handler's frame.
         */
        @Test
        fun aDefinitelyThrowingDivisionLeavesItsDestinationRegisterAlone() {
            initial.setRegisters(0, 12, "I", 1, 0, "I")
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            val trusted = graph.trusted()
            assertNotNull(trusted, "the throw is the only path and it ends cleanly, so the run is Complete")
            assertEquals(Value.Known(12, VirtualType.of("I")), trusted!!.consensus(0, 0))
        }

        /**
         * The zero test reads the divisor at the arithmetic's width. `divIntByWideZeroLowHalf()V`'s divisor is
         * a `const-wide` 2^32, whose low 32 bits are 0, and `div-int` reads one 32-bit register, so it throws.
         * Bug class: asking the zero question at 64 bits while computing at 32, which assigns `Unknown(I)` and
         * continues without the throw on a run reporting Complete.
         */
        @Test
        fun aWideDivisorWhoseLowThirtyTwoBitsAreZeroThrows() {
            CoreTester.testThrows(
                CLASS_NAME, "divIntByWideZeroLowHalf()V",
                exceptionType = "Ljava/lang/ArithmeticException;", message = "divide by zero",
            )
            val graph = CoreTester.execute(CLASS_NAME, "divIntByWideZeroLowHalf()V")
            // From the instruction formats: const/16 @0, const-wide @2, div-int @7, return-void @9. Asserted so
            // the @9 claim below cannot go vacuous if the fixture changes.
            assertArrayEquals(intArrayOf(0, 2, 7, 9), graph.addresses(), "fixture layout moved: $graph")
            assertEquals(CompletionStatus.Complete, graph.completion(), "the throw is the only path: $graph")
            assertEquals(1, graph.outcomes.size, "a definite throw has exactly one outcome: $graph")
            assertFalse(graph.wasReached(9), "and the fall-through past the division must not run")
        }

        /**
         * An `I`-domain divide reading a computed `Float` divisor reads its 32 bits: `0.5f` is
         * `0x3F000000 == 1056964608`, so `10 / 1056964608` is `0` and nothing throws. A value conversion would
         * read `0` and route the op to a throw alone.
         */
        @Test
        fun anIntDivideReadingAComputedFloatDivisorDividesByItsBits() {
            initial.setRegisters(0, 10, "I", 1, 0.5f, "F")
            CoreTester.test(CLASS_NAME, "divInt()V", initial, CoreState().apply { setRegisters(0, 0, "I") })
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            assertEquals(listOf(Outcome.Returned(null)), graph.outcomes, "a non-zero divisor cannot throw: $graph")
        }

        /**
         * The `long` case: `div-long v0, v0, v2` reads the 64 bits at v2/v3, which for `0.5` are
         * `0x3FE0000000000000 == 4602678819172646912`, so `10 / that` is `0` and nothing throws.
         */
        @Test
        fun aLongDivideReadingAComputedDoubleDivisorDividesByItsBits() {
            initial.setRegisters(0, 10L, "J", 2, 0.5, "D")
            CoreTester.test(CLASS_NAME, "divLong()V", initial, CoreState().apply { setRegisters(0, 0L, "J") })
            val graph = CoreTester.execute(CLASS_NAME, "divLong()V", initial)
            assertEquals(listOf(Outcome.Returned(null)), graph.outcomes, "a non-zero divisor cannot throw: $graph")
        }

        /**
         * A zero float divisor produces one outcome, not a fork. `0f`'s bits are an integral zero, so an
         * integral zero check reached from the float domain would fork a throw float division never makes.
         * [aFloatDivisionByZeroYieldsInfinity] pins the value.
         */
        @Test
        fun aZeroFloatDivisorDoesNotFork() {
            initial.setRegisters(0, 1f, "F", 1, 0f, "F")
            val graph = CoreTester.execute(CLASS_NAME, "divFloat()V", initial)
            assertEquals(1, graph.outcomes.size, "float division by zero has exactly one outcome: $graph")
            assertTrue(graph.outcomes.single() is Outcome.Returned, "and it is a normal return")
        }

        /** Float division by zero yields Infinity rather than throwing, so the divide-by-zero fork is integral-only. */
        @Test
        fun aFloatDivisionByZeroYieldsInfinity() {
            initial.setRegisters(0, 1f, "F", 1, 0f, "F")
            CoreTester.test(
                CLASS_NAME, "divFloat()V", initial,
                CoreState().apply { setRegisters(0, Float.POSITIVE_INFINITY, "F") },
            )
        }

        /** The `double` case of [aFloatDivisionByZeroYieldsInfinity]. */
        @Test
        fun aDoubleDivisionByZeroYieldsInfinity() {
            initial.setRegisters(0, 1.0, "D", 2, 0.0, "D")
            CoreTester.test(
                CLASS_NAME, "divDouble()V", initial,
                CoreState().apply { setRegisters(0, Double.POSITIVE_INFINITY, "D") },
            )
        }

        /** A float division by zero cannot throw, so its handler at @3 is never reached. */
        @Test
        fun aFloatDivisionByZeroNeverReachesItsHandler() {
            initial.setRegisters(0, 5f, "F", 1, 0f, "F")
            CoreTester.testVisitation(CLASS_NAME, "divFloatWithCatch()V", initial, intArrayOf(0, 2))
        }

        /** The `double` case of [aFloatDivisionByZeroNeverReachesItsHandler]. */
        @Test
        fun aDoubleDivisionByZeroNeverReachesItsHandler() {
            initial.setRegisters(0, 5.0, "D", 2, 0.0, "D")
            CoreTester.testVisitation(CLASS_NAME, "divDoubleWithCatch()V", initial, intArrayOf(0, 2))
        }

        /** Bug class: an unknown divisor exploring only one feasible path, leaving the handler unvisited. */
        @Test
        fun anUnknownIntDivisorVisitsTheExceptionHandler() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            CoreTester.testVisitation(CLASS_NAME, "divIntWithCatch()V", initial, intArrayOf(0, 2, 3, 4))
        }

        /** The `long` case of [anUnknownIntDivisorVisitsTheExceptionHandler]. */
        @Test
        fun anUnknownLongDivisorVisitsTheExceptionHandler() {
            initial.setRegisters(0, 5L, "J", 2, CoreState.UNKNOWN, "J")
            CoreTester.testVisitation(CLASS_NAME, "divLongWithCatch()V", initial, intArrayOf(0, 2, 3, 4))
        }

        /**
         * Bug class: forking on "the result is unknown" instead of "the divisor may be zero". A known non-zero
         * divisor cannot throw whatever the dividend is, so the handler at @3 is infeasible for this input.
         */
        @Test
        fun anUnknownDividendOverAKnownNonZeroDivisorDoesNotFork() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I", 1, 5, "I")
            CoreTester.testVisitation(CLASS_NAME, "divIntWithCatch()V", initial, intArrayOf(0, 2))
        }

        /**
         * `divIntFallsOffTheEnd()V`'s fall-through runs off the end of the method and its exceptional branch
         * reaches a real handler. Bug classes: discarding the feasible branch because its sibling was
         * malformed, and reporting the lost path as Complete or as a host failure instead of malformed input.
         */
        @Test
        fun aMalformedFallThroughDoesNotDiscardTheFeasibleExceptionalBranch() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val graph = CoreTester.execute(CLASS_NAME, "divIntFallsOffTheEnd()V", initial)
            assertEquals(
                CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)),
                graph.completion(),
                "the lost fall-through must make the run Partial as malformed input, not as a host failure",
            )
            // @1 is the handler (jumped over by the leading `goto`), @2 its `return-void`.
            assertTrue(graph.wasReached(1), "the feasible exceptional branch must still be explored")
            assertTrue(graph.outcomes.any { it is Outcome.Returned }, "and must run to its end")
        }

        /**
         * Each synthesized `ArithmeticException` is its own object, so two escaping throws carry distinct heap
         * refs. The fixture routes both through an unloadable catch type: a matching clause would catch both
         * definitely, and a definitely-caught throw never becomes an [Outcome.Threw], leaving nothing to compare.
         */
        @Test
        fun twoSynthesizedExceptionsAreDistinctObjects() {
            val graph = CoreTester.execute(CLASS_NAME, "divIntTwiceByZeroWithCatch()V", CoreState().apply {
                setRegisters(0, 10, "I", 1, 0, "I")
            })
            val refs = graph.outcomes.filterIsInstance<Outcome.Threw>()
                .mapNotNull { (it.exception as? Value.Known)?.raw as? HeapRef }
            // Both throws must escape, or the distinctness check below passes vacuously.
            assertEquals(2, refs.size, "expected both synthesized exceptions to escape: $refs")
            assertEquals(refs.size, refs.distinct().size, "each synthesis is its own object: $refs")
        }
    }

    @Nested
    @DisplayName("reading operands written at another type")
    inner class OperandReads {
        /**
         * `add-int` reads boolean, byte, char, and short registers as the 32-bit ints they hold, and an unknown
         * operand makes the sum unknown. Expected values are Kotlin `Int` arithmetic on the seeds.
         */
        @TestFactory
        fun addIntReadsEveryIntLikeOperand(): List<DynamicTest> = listOf(
            fold("addInt()V", int('a'.code + 0xf), 0, 'a', "C", 1, 0xf.toByte(), "B"),
            fold("addInt()V", int(1 + 11), 0, true, "Z", 1, 11, "I"),
            fold("addInt()V", int(-3 + 0xf), 0, -3, "I", 1, 0xf.toByte(), "B"),
            fold("addInt()V", int('$'.code + 11), 0, '$', "C", 1, 11, "I"),
            fold("addInt()V", int(5 + 11), 0, 5.toShort(), "S", 1, 11, "I"),
            fold("addInt()V", unknown("I"), 0, CoreState.UNKNOWN, "C", 1, 5, "I"),
            fold("addInt()V", unknown("I"), 0, CoreState.UNKNOWN, "C", 1, CoreState.UNKNOWN, "B"),
            fold("addInt()V", unknown("I"), 0, -3, "I", 1, CoreState.UNKNOWN, "I"),
        )

        /**
         * `add-int` reads v0's 32 bits as a signed int; `5.9f` is `0x40BCCCCD`, i.e. 1086115021, so a device
         * answers `1086115028`. A JLS §5.1.3 value conversion of the raw would answer 12.
         */
        @Test
        fun anIntDomainOperatorReadingAComputedFloatRawAddsItsBits() {
            CoreTester.test(
                CLASS_NAME, "addInt()V",
                CoreState().apply { setRegisters(0, 5.9f, "F", 1, 7, "I") },
                CoreState().apply { setRegisters(0, 1086115028, "I") },
            )
        }

        /**
         * The `long` counterpart answers differently: `add-long` reads v0/v1 as 64 bits, `5.9f` supplies only
         * 32, and the engine does not hold v1, so the sum is `Unknown`. A value conversion would answer 12.
         */
        @Test
        fun aLongDomainOperatorReadingAComputedFloatRawIsNotProvable() {
            CoreTester.test(
                CLASS_NAME, "addLong()V",
                CoreState().apply { setRegisters(0, 5.9f, "F", 2, 7L, "J") },
                CoreState().apply { setRegisters(0, CoreState.UNKNOWN, "J") },
            )
        }

        /**
         * A compiler loads a float literal with `const v0, 0x3f800000`, which stores `Known(1065353216, I)`
         * because `const` serves int, char, byte, short, and float alike. A device reads those 32 bits as float
         * bits, so the sum is `1.0f + 1.0f = 2.0f`. Bug class: reading the raw by value, which folds the
         * verifier-valid sum to a definite `2.1307064E9`.
         */
        @Test
        fun anAddFloatOnConstLoadedLiteralBitsAnswersWhatADeviceAnswers() {
            CoreTester.test(
                CLASS_NAME, "addFloat()V",
                CoreState().apply { setRegisters(0, 1065353216, "I", 1, 1065353216, "I") },
                CoreState().apply { setRegisters(0, 2.0f, "F") },
            )
        }

        /**
         * The `double` case, read through a different coercion: `const-wide v0, 0x4000000000000000` stores
         * `Known(4611686018427387904, J)`, whose 64 bits are `2.0`, so the sum is `4.0`. Read by value it
         * would be `9.223372036854776E18`.
         */
        @Test
        fun anAddDoubleOnConstWideLiteralBitsAnswersWhatADeviceAnswers() {
            CoreTester.test(
                CLASS_NAME, "addDouble()V",
                CoreState().apply { setRegisters(0, 4611686018427387904L, "J", 2, 4611686018427387904L, "J") },
                CoreState().apply { setRegisters(0, 4.0, "D") },
            )
        }
    }

    /**
     * Each operator folds known operands to the same value in every instruction shape that encodes it: 23x,
     * `/2addr`, and for `int` the `/lit8` and `/lit16` forms. Expected values are Kotlin arithmetic on the
     * same operands, which implements the JVMS operators that "Binary operations" in
     * `docs/DALVIK-SEMANTICS.md` records as inherited. A literal row's expected value spells out the literal
     * its fixture method encodes.
     */
    @Nested
    @DisplayName("with known operands")
    inner class KnownOperands {
        @TestFactory
        fun intOperatorsFoldInEveryShape(): List<DynamicTest> = listOf(
            ints(-3, 7, -3 + 7, "addInt()V", "addInt2Addr()V"),
            ints(-3, 7, -3 - 7, "subInt()V", "subInt2Addr()V"),
            ints(-3, 7, -3 * 7, "mulInt()V", "mulInt2Addr()V"),
            ints(12, 3, 12 / 3, "divInt()V", "divInt2Addr()V"),
            ints(7, 5, 7 % 5, "remInt()V", "remInt2Addr()V"),
            ints(6, 3, 6 and 3, "andInt()V", "andInt2Addr()V"),
            ints(5, 2, 5 or 2, "orInt()V", "orInt2Addr()V"),
            ints(7, 2, 7 xor 2, "xorInt()V", "xorInt2Addr()V"),
            ints(5, 2, 5 shl 2, "shlInt()V", "shlInt2Addr()V"),
            ints(7, 2, 7 shr 2, "shrInt()V", "shrInt2Addr()V"),
            ints(-14, 2, -14 ushr 2, "ushrInt()V", "ushrInt2Addr()V"),
        ).flatten() + listOf(
            intLiteral("addIntLit8()V", 5, 5 + 0xf),
            intLiteral("addIntLit16()V", -0xf, -0xf + 0xff),
            intLiteral("RSubIntLit8()V", 7, 0xf - 7),
            intLiteral("RSubInt()V", 7, 0xff - 7),
            intLiteral("mulIntLit8()V", -3, -3 * 0xf),
            intLiteral("mulIntLit16()V", -3, -3 * 0xff),
            intLiteral("divIntLit8()V", 0x10, 0x10 / 0xf),
            intLiteral("divIntLit16()V", 0x100, 0x100 / 0xff),
            intLiteral("remIntLit8()V", 0x10, 0x10 % 0xf),
            intLiteral("remIntLit16()V", 0x100, 0x100 % 0xff),
            intLiteral("andIntLit8()V", 0x1e, 0x1e and 0xf),
            intLiteral("andIntLit16()V", 0x1fe, 0x1fe and 0xff),
            intLiteral("orIntLit8()V", 0x150, 0x150 or 0xf),
            intLiteral("orIntLit16()V", 0x150, 0x150 or 0xff),
            intLiteral("xorIntLit8()V", 7, 7 xor 0x3),
            intLiteral("xorIntLit16()V", 7, 7 xor 0x10),
            intLiteral("shlIntLit8()V", 3, 3 shl 0x2),
            intLiteral("shrIntLit8()V", 7, 7 shr 0x2),
            intLiteral("ushrIntLit8()V", -14, -14 ushr 0x2),
        )

        /** A shift distance is one 32-bit register, so it is seeded as `I`. */
        @TestFactory
        fun longOperatorsFoldInEveryShape(): List<DynamicTest> = listOf(
            longs(0x100000000L, 0x200000000L, 0x100000000L + 0x200000000L, "addLong()V", "addLong2Addr()V"),
            longs(0x100000000L, 0x200000000L, 0x100000000L - 0x200000000L, "subLong()V", "subLong2Addr()V"),
            longs(0x300000000L, 3L, 0x300000000L * 3L, "mulLong()V", "mulLong2Addr()V"),
            longs(0x300000000L, 3L, 0x300000000L / 3L, "divLong()V", "divLong2Addr()V"),
            longs(0x300000123L, 3L, 0x300000123L % 3L, "remLong()V", "remLong2Addr()V"),
            longs(0x12345abcdL, 0x1234567890L, 0x12345abcdL and 0x1234567890L, "andLong()V", "andLong2Addr()V"),
            longs(0x12345abcdL, 0x1234567890L, 0x12345abcdL or 0x1234567890L, "orLong()V", "orLong2Addr()V"),
            longs(0x12345abcdL, 0x1234567890L, 0x12345abcdL xor 0x1234567890L, "xorLong()V", "xorLong2Addr()V"),
            longShifts(0x10000L, 5, 0x10000L shl 5, "shlLong()V", "shlLong2Addr()V"),
            longShifts(0x10000L, 5, 0x10000L shr 5, "shrLong()V", "shrLong2Addr()V"),
            longShifts(0x10000L, 5, 0x10000L ushr 5, "ushrLong()V", "ushrLong2Addr()V"),
        ).flatten()

        @TestFactory
        fun floatOperatorsFoldInEveryShape(): List<DynamicTest> = listOf(
            floats(0.5f, 20.5f, 0.5f + 20.5f, "addFloat()V", "addFloat2Addr()V"),
            floats(0.5f, 20.5f, 0.5f - 20.5f, "subFloat()V", "subFloat2Addr()V"),
            floats(0.5f, 20.5f, 0.5f * 20.5f, "mulFloat()V", "mulFloat2Addr()V"),
            floats(22.9f, 0.5f, 22.9f / 0.5f, "divFloat()V", "divFloat2Addr()V"),
            floats(22.9f, 0.5f, 22.9f % 0.5f, "remFloat()V", "remFloat2Addr()V"),
        ).flatten()

        @TestFactory
        fun doubleOperatorsFoldInEveryShape(): List<DynamicTest> = listOf(
            doubles(0.5, 20.5, 0.5 + 20.5, "addDouble()V", "addDouble2Addr()V"),
            doubles(0.5, 20.5, 0.5 - 20.5, "subDouble()V", "subDouble2Addr()V"),
            doubles(0.5, 20.5, 0.5 * 20.5, "mulDouble()V", "mulDouble2Addr()V"),
            doubles(22.9, 0.5, 22.9 / 0.5, "divDouble()V", "divDouble2Addr()V"),
            doubles(22.9, 0.5, 22.9 % 0.5, "remDouble()V", "remDouble2Addr()V"),
        ).flatten()

        /**
         * Bug class: masking a long shift's distance to the `int` rule's 5 bits instead of 6, which computes
         * `shl 1` instead of `shl 33`. Expected value: 2^33, hand-computed.
         */
        @Test
        fun aLongShiftKeepsADistanceAboveThirtyOne() {
            CoreTester.test(
                CLASS_NAME, "shlLong()V",
                CoreState().apply { setRegisters(0, 1L, "J", 2, 33, "I") },
                CoreState().apply { setRegisters(0, 8589934592L, "J") },
            )
        }

        private fun ints(lhs: Int, rhs: Int, expected: Int, vararg methods: String) =
            methods.map { fold(it, int(expected), 0, lhs, "I", 1, rhs, "I") }

        private fun intLiteral(method: String, lhs: Int, expected: Int) = fold(method, int(expected), 0, lhs, "I")

        private fun longs(lhs: Long, rhs: Long, expected: Long, vararg methods: String) =
            methods.map { fold(it, long(expected), 0, lhs, "J", 2, rhs, "J") }

        private fun longShifts(lhs: Long, distance: Int, expected: Long, vararg methods: String) =
            methods.map { fold(it, long(expected), 0, lhs, "J", 2, distance, "I") }

        private fun floats(lhs: Float, rhs: Float, expected: Float, vararg methods: String) =
            methods.map { fold(it, Value.Known(expected, VirtualType.of("F")), 0, lhs, "F", 1, rhs, "F") }

        private fun doubles(lhs: Double, rhs: Double, expected: Double, vararg methods: String) =
            methods.map { fold(it, Value.Known(expected, VirtualType.of("D")), 0, lhs, "D", 2, rhs, "D") }
    }

    /**
     * Two's-complement boundaries, where a plausible wrong implementation (widened arithmetic, a host
     * `Math.*Exact`, a 6-bit int shift mask, a throw on `MIN_VALUE / -1`) disagrees with Dalvik. Dalvik's
     * integral ops are the JVM's: JLS 15.17.2 (`MIN_VALUE / -1` overflows to `MIN_VALUE`, no exception),
     * JLS 15.17.3 (the remainder takes the dividend's sign and `MIN_VALUE % -1` is 0), JLS 15.19 (an int
     * shift uses the distance's low 5 bits, a long shift its low 6), and JLS 15.18.2/15.17.1 (add, sub and
     * mul wrap). Every expected value is written out by hand from those rules.
     */
    @Nested
    @DisplayName("at two's-complement boundaries")
    inner class IntegralBoundaries {
        @TestFactory
        fun minValueOverMinusOneOverflowsWithoutThrowingInEveryDivRemForm(): List<DynamicTest> = listOf(
            fold("divInt()V", int(Int.MIN_VALUE), 0, Int.MIN_VALUE, "I", 1, -1, "I"),
            fold("divInt2Addr()V", int(Int.MIN_VALUE), 0, Int.MIN_VALUE, "I", 1, -1, "I"),
            fold("divIntLit8ByMinusOne()V", int(Int.MIN_VALUE), 0, Int.MIN_VALUE, "I"),
            fold("divIntLit16ByMinusOne()V", int(Int.MIN_VALUE), 0, Int.MIN_VALUE, "I"),
            fold("remInt()V", int(0), 0, Int.MIN_VALUE, "I", 1, -1, "I"),
            fold("remInt2Addr()V", int(0), 0, Int.MIN_VALUE, "I", 1, -1, "I"),
            fold("remIntLit8ByMinusOne()V", int(0), 0, Int.MIN_VALUE, "I"),
            fold("remIntLit16ByMinusOne()V", int(0), 0, Int.MIN_VALUE, "I"),
            fold("divLong()V", long(Long.MIN_VALUE), 0, Long.MIN_VALUE, "J", 2, -1L, "J"),
            fold("divLong2Addr()V", long(Long.MIN_VALUE), 0, Long.MIN_VALUE, "J", 2, -1L, "J"),
            fold("remLong()V", long(0L), 0, Long.MIN_VALUE, "J", 2, -1L, "J"),
            fold("remLong2Addr()V", long(0L), 0, Long.MIN_VALUE, "J", 2, -1L, "J"),
        )

        /**
         * JLS 15.17.2-3: the quotient rounds toward zero and the remainder takes the dividend's sign. `10 / 4`
         * is 2 where rounding half up gives 3, and a dividend smaller than its divisor gives 0.
         */
        @TestFactory
        fun theRemainderTakesTheDividendsSignAndTheQuotientTruncatesTowardZero(): List<DynamicTest> = listOf(
            fold("remInt()V", int(-1), 0, -7, "I", 1, 3, "I"),
            fold("remInt()V", int(1), 0, 7, "I", 1, -3, "I"),
            fold("remIntLit8()V", int(-2), 0, -17, "I"),
            fold("divInt()V", int(-3), 0, -7, "I", 1, 2, "I"),
            fold("divInt()V", int(2), 0, 10, "I", 1, 4, "I"),
            fold("remLong()V", long(-1L), 0, -7L, "J", 2, 3L, "J"),
            fold("remLong()V", long(1L), 0, 7L, "J", 2, -3L, "J"),
            fold("divLong()V", long(-3L), 0, -7L, "J", 2, 2L, "J"),
            fold("divLong()V", long(0L), 0, 1120403456L, "J", 2, 1149239296L, "J"),
        )

        /** JLS 15.19: an int shift reads the low 5 bits of its distance, so 32 is 0, 33 is 1 and -1 is 31. */
        @TestFactory
        fun anIntShiftMasksItsDistanceToFiveBits(): List<DynamicTest> = listOf(
            fold("shlInt()V", int(1), 0, 1, "I", 1, 32, "I"),
            fold("shlInt()V", int(2), 0, 1, "I", 1, 33, "I"),
            fold("shlInt()V", int(Int.MIN_VALUE), 0, 1, "I", 1, -1, "I"),
            fold("shrInt()V", int(-4), 0, -8, "I", 1, 33, "I"),
            fold("ushrInt()V", int(1), 0, -1, "I", 1, -1, "I"),
            fold("ushrInt2Addr()V", int(-1), 0, -1, "I", 1, 32, "I"),
            fold("shlIntLit8By33()V", int(2), 0, 1, "I"),
            fold("ushrIntLit8ByMinusOne()V", int(1), 0, -1, "I"),
        )

        /** JLS 15.19: a long shift reads the low 6 bits of its 32-bit distance, so 64 is 0 and -1 is 63. */
        @TestFactory
        fun aLongShiftMasksItsDistanceToSixBits(): List<DynamicTest> = listOf(
            fold("shlLong()V", long(1L), 0, 1L, "J", 2, 64, "I"),
            fold("shlLong()V", long(2L), 0, 1L, "J", 2, 65, "I"),
            fold("shlLong()V", long(Long.MIN_VALUE), 0, 1L, "J", 2, -1, "I"),
            fold("shrLong()V", long(-1L), 0, Long.MIN_VALUE, "J", 2, 127, "I"),
            fold("ushrLong()V", long(1L), 0, -1L, "J", 2, 127, "I"),
            fold("ushrLong2Addr()V", long(-1L), 0, -1L, "J", 2, 64, "I"),
        )

        /**
         * JLS 15.18.2 and 15.17.1: the result is the low 32 (or 64) bits of the exact result. Hand values:
         * `MAX + 0xff` is `MIN + 254`; `MAX * 15` is `15 * 2^31 - 15`, which is `2^31 - 15` mod 2^32;
         * `0xff - MIN` is `2^31 + 255`, which is `MIN + 255`.
         */
        @TestFactory
        fun addSubAndMulWrapRatherThanSaturateOrThrow(): List<DynamicTest> = listOf(
            fold("addInt()V", int(Int.MIN_VALUE), 0, Int.MAX_VALUE, "I", 1, 1, "I"),
            fold("subInt()V", int(Int.MAX_VALUE), 0, Int.MIN_VALUE, "I", 1, 1, "I"),
            fold("mulInt()V", int(-2), 0, Int.MAX_VALUE, "I", 1, 2, "I"),
            fold("mulInt()V", int(0), 0, 0x10000, "I", 1, 0x10000, "I"),
            fold("addIntLit16()V", int(-2147483394), 0, Int.MAX_VALUE, "I"),
            fold("mulIntLit8()V", int(2147483633), 0, Int.MAX_VALUE, "I"),
            fold("RSubInt()V", int(-2147483393), 0, Int.MIN_VALUE, "I"),
            fold("addLong()V", long(Long.MIN_VALUE), 0, Long.MAX_VALUE, "J", 2, 1L, "J"),
            fold("subLong()V", long(Long.MAX_VALUE), 0, Long.MIN_VALUE, "J", 2, 1L, "J"),
            fold("mulLong()V", long(-2L), 0, Long.MAX_VALUE, "J", 2, 2L, "J"),
        )
    }

    /** Runs [method] on the `(register, raw, type)` [seeds] and asserts v0 folds to [expected] with no throw. */
    private fun fold(method: String, expected: Value, vararg seeds: Any?): DynamicTest =
        DynamicTest.dynamicTest("$method ${seeds.toList()} -> $expected") {
            val graph = CoreTester.execute(CLASS_NAME, method, CoreState().apply { setRegisters(*seeds) })
            assertEquals(CompletionStatus.Complete, graph.completion())
            assertEquals(
                emptyList<Outcome.Threw>(),
                graph.outcomes.filterIsInstance<Outcome.Threw>(),
                "only a zero divisor throws",
            )
            assertEquals(expected, graph.trusted()!!.terminatingConsensus(0))
        }

    private fun int(raw: Int) = Value.Known(raw, VirtualType.of("I"))

    private fun long(raw: Long) = Value.Known(raw, VirtualType.of("J"))

    private fun unknown(type: String) = Value.Unknown(VirtualType.of(type))
}
