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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * The twelve binary math/logic operators across four operand domains and three instruction shapes
 * (register/register/register, `/2addr`, and literal), plus [DivideByZero] -- the divide-by-zero
 * routing that makes `div`/`rem` the first ops in this engine to return TWO transitions.
 *
 * **Nothing here runs against the oracle any more.** The last three oracle-backed tests were migrated
 * in this task, and two of them needed their fixture method repaired first:
 *
 *  - [IntegerMath.canDivIntWithCatchWithUnknownValueVisitsExceptionHandler] was waiting on the fork.
 *    Its SEED moved -- see the note in its body -- because the oracle reaches the handler for an
 *    infeasible reason; its expected addresses are byte-identical.
 *  - [LongMath.canDivLongWithCatchWithUnkownValueVisitsExceptionHandler] was waiting on the fork too,
 *    and on `divLongWithCatch()V`'s overlapping wide register pairs (`div-long v0, v0, v1` under
 *    `.locals 2`: the lhs pair is {v0,v1} and the rhs pair is {v1,v2}). The fixture's rhs operand now
 *    reads v2 under `.locals 4`; see that method's comment for the derivation.
 *  - [DoubleMath.canDivDoubleWithDivisionByZero] was never waiting on the fork at all -- float and
 *    double division by zero yields +/-Infinity and never throws -- only on the identical
 *    register-pair defect in `divDoubleWithCatch()V`, repaired the same way.
 *
 * Both repairs leave every code address unchanged (`Instruction23x` is two code units whichever
 * registers it names), so no address-asserting expectation moved. `divIntWithCatch()V` and
 * `divFloatWithCatch()V` are narrow and were verified defect-free rather than assumed so.
 */
class BinaryMathOpTest {

    companion object {
        private const val CLASS_NAME = "Lbinary_math_test;"
    }

    /**
     * Liveness pin, matching the shape of UnaryMathOp's and CmpOp's: `add-int v0, v0, v1`
     * (Instruction23x) aliases its destination to its LHS operand (v0), so `registersAssigned` alone
     * already reports v0 as touched. The RHS operand (v1) is a DIFFERENT register that
     * `registersAssigned` never names, so dropping it from `registersRead` is a real liveness loss
     * that only a query reading `registersRead` directly -- never a value assertion -- can see.
     */
    @Test
    fun addIntReportsBothOperandRegistersAsRead() {
        val graph = CoreTester.execute(CLASS_NAME, "addInt()V")
        assertEquals(setOf(0, 1), graph.registersRead(0))
    }

    /**
     * Divide-by-zero routing: the first op in this engine to return two transitions, and therefore the
     * first real exercise of the atomic commit primitive.
     *
     * Every expectation below has Dalvik itself as its oracle, not engine output: integral `div`/`rem`
     * by zero throws `ArithmeticException("/ by zero")`, a known non-zero divisor cannot throw whatever
     * the dividend is (integer overflow does not throw -- `Integer.MIN_VALUE / -1` yields
     * `Integer.MIN_VALUE`), and float/double division by zero yields +/-Infinity or NaN and never
     * throws at all.
     *
     * Addresses: the plain `divInt()V`/`divLong()V`/`remInt()V` methods are the math op at @0
     * (`Instruction23x`, two code units) followed by `return-void` at @2, and the `*WithCatch()V` ones
     * add the handler at @3 and its `goto` back to the return at @4.
     * [aMalformedFallThroughDoesNotDiscardTheFeasibleExceptionalBranch]'s fixture is deliberately laid
     * out differently and states its own addresses.
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
         * Bug class: continuing past a division that DEFINITELY throws, which explores a path Dalvik
         * never takes and lets a fabricated quotient vote into consensus.
         */
        @Test
        fun aKnownZeroIntDivisorThrowsAndDoesNotContinue() {
            initial.setRegisters(0, 12, "I", 1, 0, "I")
            CoreTester.testThrows(
                CLASS_NAME, "divInt()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "/ by zero",
            )
            // The "and does not continue" half has to be asserted separately: `testThrows`
            // deliberately says nothing about whether a normal path also ran (see its KDoc), so
            // returning `[Continue, Throw]` for a KNOWN zero would satisfy the call above unchanged.
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            // The completion gate is NOT redundant with `testThrows`'s: an absent address is a
            // dead-code claim, and `wasReached` answers false identically for a genuinely unexplored
            // fall-through and for one a truncated run never got to (`CoreTester.testVisitation`'s
            // KDoc). Hand-written `wasReached` negatives need the gate written out by hand.
            assertEquals(
                CompletionStatus.Complete, graph.completion(),
                "the fall-through is only provably unexplored on a run that explored everything",
            )
            assertFalse(graph.wasReached(2), "a definite throw must not also explore the fall-through")
        }

        /** The `J` half. A separate domain in [BinaryMathOp], so a separate feasible-throw claim. */
        @Test
        fun aKnownZeroLongDivisorThrows() {
            initial.setRegisters(0, 12L, "J", 2, 0L, "J")
            CoreTester.testThrows(
                CLASS_NAME, "divLong()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "/ by zero",
            )
        }

        /**
         * `rem` by a known zero throws too -- `rem-int` computes the remainder of the same division, so
         * Dalvik raises the identical `ArithmeticException`. Bug class: treating divide-by-zero as a
         * property of the `div` mnemonic rather than of the division.
         */
        @Test
        fun aKnownZeroIntModulusThrows() {
            initial.setRegisters(0, 12, "I", 1, 0, "I")
            CoreTester.testThrows(
                CLASS_NAME, "remInt()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "/ by zero",
            )
        }

        /**
         * `rem` by a known zero throws for LONG too, mirroring [aKnownZeroIntModulusThrows]. Restores
         * the INTENT of the deleted mockk test
         * `longModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters`, which the design
         * spec names as a "fix, don't port" false positive (spec §10): it constructed `DIV_LONG`, not
         * `REM_LONG` -- a copy-paste of its own DIV sibling -- so `rem-long`'s own zero-divisor behavior
         * went untested both before this migration and after it; neither `canRemLong` nor
         * `canRemLong2Addr` uses a zero divisor. Bug class: `canDivideByZero()`'s operator and domain
         * guards agreeing for some combinations but not others -- e.g. recognizing REM for `I` but not
         * `J` -- which neither the existing INT-domain REM test nor the existing LONG-domain DIV test
         * can catch on its own; see Mutation evidence.
         */
        @Test
        fun aKnownZeroLongModulusThrows() {
            initial.setRegisters(0, 12L, "J", 2, 0L, "J")
            CoreTester.testThrows(
                CLASS_NAME, "remLong()V", initial,
                exceptionType = "Ljava/lang/ArithmeticException;", message = "/ by zero",
            )
        }

        /**
         * The soundness claim, stated over OUTCOMES rather than addresses: an unknown divisor makes both
         * a normal return and a throw feasible, so a run that reports Complete must contain both. Bug
         * class: an unexplored feasible path on a fully-trusted run -- the worst outcome this engine can
         * have (spec §2.2, more forks never fewer). `divInt()V` has no handler, so the exceptional path
         * leaves the method and shows up as [Outcome.Threw] instead of as a visited address.
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
         * `rem` forks on an ambiguous divisor exactly as `div` does, because `rem-int` by zero throws
         * exactly as `div-int` does.
         *
         * INTENDED DIVERGENCE from the oracle, which forks only for `DIV` (`opcode/BinaryMathOp.kt:64`
         * tests `mathOperator == MathOperator.DIV`) even though its own `doIntegerOperation` catches
         * `ArithmeticException` for `REM` too (`:185`). On an unknown divisor the oracle therefore
         * explores one fewer feasible path than exists while still reporting the run complete -- which
         * is the soundness class this rewrite exists to remove.
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
         * The normal path's quotient is [Value.Unknown], never a guess: the divisor was not known, so
         * neither is the result. Bug class: routing the fork correctly while still fabricating a
         * constant for the branch that did not throw.
         *
         * Asserted at address **2**, the fall-through successor, not at the forking node's own address:
         * @0's frame is shared with the throwing path, so an assertion there is one address away from
         * this test's own claim about "the normal path".
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
         * Dalvik does not write a division's destination register when the division throws, so a path
         * that DEFINITELY throws must leave the pre-op value standing. Bug class: a throwing node
         * reporting `Unknown(domain)` for its destination -- harmless-looking as a move up the lattice,
         * but type-wrong rather than merely imprecise when the register held a reference, and visible
         * both through `consensus` here and to any handler successor, which copies this same post-op
         * frame.
         *
         * The `[Continue, Throw]` fork deliberately still assigns; see [BinaryMathOp.execute].
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
         * The zero test must be asked at the SAME width the arithmetic is performed at.
         * `divIntByWideZeroLowHalf()V`'s divisor is written by a `const-wide` as the 64-bit 2^32, so
         * reading it as an `Int` gives 0 (the low 32 bits) while reading it as a `Long` gives 2^32.
         * Dalvik reads a `div-int` operand as a 32-bit register, so it throws.
         *
         * Bug class, and it was real: asking the routing question at 64-bit width while `computed` asked
         * it at 32-bit produced "no result because the path throws" AND "continue normally" at once --
         * `Unknown(I)` assigned, one transition, no `Transition.Throw`, run reporting `Complete`. An
         * unexplored feasible path on a fully-trusted run. Needs no seeded frame: `const-wide` puts the
         * ambiguous raw there, which is the shape Dalvik's overloaded const opcodes actually produce.
         *
         * **This is also the ONLY test that keys `knownDivisor`'s `Float`/`Double` guard to the raw's
         * RUNTIME type rather than to its declared tag, so the arity half below is load-bearing.**
         * `const-wide` ships `Value.Known(Long, "D")` (`Op.kt`'s type-tag table): a `D` TAG over a raw
         * that is a `java.lang.Long`. A guard written as `rhs.type.descriptor == "F" || == "D"` would
         * therefore fire here and answer "not provable", turning this definite throw into a
         * `[Continue, Throw]` FORK -- which `testThrows` alone cannot see, because it asserts a throw
         * EXISTS and a fork still contains one, on a still-`Complete` run. The tag key does not un-throw
         * this method, it over-approximates it, which is exactly why only an ARITY assertion catches it.
         * Same shape as [aKnownZeroIntDivisorThrowsAndDoesNotContinue] and [aZeroFloatDivisorDoesNotFork].
         */
        @Test
        fun aWideDivisorWhoseLowThirtyTwoBitsAreZeroThrows() {
            CoreTester.testThrows(
                CLASS_NAME, "divIntByWideZeroLowHalf()V",
                exceptionType = "Ljava/lang/ArithmeticException;", message = "/ by zero",
            )
            val graph = CoreTester.execute(CLASS_NAME, "divIntByWideZeroLowHalf()V")
            // Derived from the instruction formats, and asserted rather than trusted so the @9 claim
            // below cannot go vacuous if the fixture is edited: const/16 @0 (Format21s, 2 code units),
            // const-wide @2 (Format51l, 5), div-int @7 (Instruction23x, 2), return-void @9.
            assertArrayEquals(intArrayOf(0, 2, 7, 9), graph.addresses(), "fixture layout moved: $graph")
            assertEquals(CompletionStatus.Complete, graph.completion(), "the throw is the only path: $graph")
            assertEquals(1, graph.outcomes.size, "a definite throw has exactly ONE outcome: $graph")
            assertFalse(graph.wasReached(9), "and the fall-through past the division must not run")
        }

        /**
         * The mirror of [aWideDivisorWhoseLowThirtyTwoBitsAreZeroThrows], and the case asking at the
         * op's own width still cannot answer: an `I`-domain divide reading a genuinely-computed `Float`
         * raw. `Coerce.toInt(0.5f)` is `0`, so a width-only zero check answers "definitely throws" and
         * emits `[Throw]` ALONE -- no `Continue`, on a run that still reports `Complete`.
         *
         * Bug class: an unexplored feasible path on a fully-trusted run, the worst outcome this engine
         * can have (spec 2.2). Oracle: Dalvik, not engine output. `0.5f`'s bit pattern is
         * `0x3F000000 == 1056964608`, `div-int` reads those 32 bits as an int, and `10 / 1056964608` is
         * `0` -- no throw at all. The engine cannot model a bit-reinterpreting register read, so the
         * honest answer is "cannot tell", which forks; hence BOTH outcomes are asserted, the throw
         * included, because over-approximating is the safe direction and under-approximating is not.
         *
         * **No differential can catch this class**: the oracle reaches the same `0` divisor through
         * `Utils.getIntegerValue` -> `castToPrimitive` -> `intValue()` and throws too, so both engines
         * drop the same path and agree. This seeded test is the only witness available.
         *
         * Reachable through pure smali with this build's opcode set: `div-float` or `int-to-float` puts
         * a computed `Float` raw in a register and any `div-int` reading it arrives here. Seeded rather
         * than fixture-driven because the seed is the shorter, more direct statement of the same state;
         * v1's own tag is `F` while `div-int`'s domain is `I`, which is the cross-domain read itself.
         */
        @Test
        fun anIntDivideReadingAComputedFloatDivisorStillExploresTheNormalPath() {
            initial.setRegisters(0, 10, "I", 1, 0.5f, "F")
            val graph = CoreTester.execute(CLASS_NAME, "divInt()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion(), "neither path aborts: $graph")
            assertTrue(
                graph.outcomes.any { it is Outcome.Returned },
                "Dalvik reads v1 as 1056964608 and does not throw, so the normal path is feasible: $graph",
            )
            assertTrue(graph.wasReached(2), "and the fall-through address must actually have run")
            assertTrue(
                graph.outcomes.any { it is Outcome.Threw },
                "the throw is not ruled out either -- unprovable means fork, not choose",
            )
        }

        /**
         * The `J` half of [anIntDivideReadingAComputedFloatDivisorStillExploresTheNormalPath], through
         * [BinaryMathOp.knownDivisor]'s OTHER branch: `Coerce.toLong(0.5)` is `0L`, so `div-long`
         * reading a computed `Double` pair was routed to `[Throw]` alone in exactly the same way. A
         * separate domain is a separate feasible-path claim -- a guard applied to only one of the two
         * branches passes the `I` test above and still drops this path.
         *
         * Oracle: `div-long v0, v0, v2` reads the 64 bits at v2/v3, which for `0.5` are
         * `0x3FE0000000000000 == 4602678819172646912`, so `10 / that` is `0` and Dalvik does not throw.
         */
        @Test
        fun aLongDivideReadingAComputedDoubleDivisorStillExploresTheNormalPath() {
            initial.setRegisters(0, 10L, "J", 2, 0.5, "D")
            val graph = CoreTester.execute(CLASS_NAME, "divLong()V", initial)
            assertEquals(CompletionStatus.Complete, graph.completion(), "neither path aborts: $graph")
            assertTrue(
                graph.outcomes.any { it is Outcome.Returned },
                "Dalvik reads v2/v3 as 4602678819172646912 and does not throw: $graph",
            )
            assertTrue(graph.wasReached(2), "and the fall-through address must actually have run")
        }

        /**
         * A zero FLOAT divisor produces no fork at all -- one transition, one successor, one outcome.
         * Asserted as fork ARITY rather than as a value (`FloatMath` already pins the Infinity), because
         * that is the half a domain-agnostic guard would break: `Coerce.toLong(0.5f)` is `0L`, so a
         * `Long`-typed zero check reached from the float domain fires on divisors that are not zero at
         * all.
         */
        @Test
        fun aZeroFloatDivisorDoesNotFork() {
            initial.setRegisters(0, 1f, "F", 1, 0f, "F")
            val graph = CoreTester.execute(CLASS_NAME, "divFloat()V", initial)
            assertEquals(1, graph.outcomes.size, "float division by zero has exactly one outcome: $graph")
            assertTrue(graph.outcomes.single() is Outcome.Returned, "and it is a normal return")
        }

        /**
         * One branch of the fork has a malformed target -- `divIntFallsOffTheEnd()V`'s fall-through
         * runs off the end of the method -- and the other has a real handler. The feasible branch must
         * still be explored (`OnUnbuildable.SKIP_ONE`), and the lost one must make the run Partial.
         *
         * Two bug classes at once, and they pull in opposite directions: discarding the feasible
         * exceptional branch because a sibling was malformed, and reporting Complete for an exploration
         * that lost a path. `CommitTest` reaches this through the op-injection seam; this is the
         * behavioral witness that a real opcode gets there.
         *
         * It is also the ONLY witness that a real opcode's malformed target reports
         * `AbortReason.MALFORMED_INPUT` rather than `HOST_FAILURE` -- every other test of that
         * distinction drives `commit` through the injection seam, so without the exact reason asserted
         * here the whole split would rest on scripted transitions.
         */
        @Test
        fun aMalformedFallThroughDoesNotDiscardTheFeasibleExceptionalBranch() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val graph = CoreTester.execute(CLASS_NAME, "divIntFallsOffTheEnd()V", initial)
            assertEquals(
                CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)),
                graph.completion(),
                "the lost fall-through must make the run Partial, and as MALFORMED bytecode, " +
                    "not as a failure of this engine",
            )
            // @1 is the handler (jumped over by the leading `goto`), @2 its `return-void`.
            assertTrue(graph.wasReached(1), "the feasible exceptional branch must still be explored")
            assertTrue(graph.outcomes.any { it is Outcome.Returned }, "and must run to its end")
        }

        /**
         * The correction to CARRY-FORWARD's framing, pinned so nobody "fixes" it back. Two synthesized
         * exceptions get distinct heap ids and therefore do NOT agree -- which is right: two paths that
         * each throw a fresh ArithmeticException do not throw one object in Dalvik. Moving to the heap
         * makes that identity explicit; it does not and must not collapse it.
         *
         * `divIntTwiceByZeroWithCatch()V`'s own comment records why its two throws are routed through an
         * UNLOADABLE catch type rather than a matching one: a matching clause would make BOTH throws
         * DEFINITELY caught, and `ThrowRoutingTest.handlerPathIsReachedAndTheThrowingPathDoesNotEscape`
         * already pins that a definitely-caught throw never becomes an `Outcome.Threw` at all -- so this
         * assertion's own `refs` list would be permanently empty (0 == 0, vacuously true) and could never
         * fail no matter what `HeapIds` did.
         */
        @Test
        fun twoSeparatelySynthesizedExceptionsStillDoNotAgree() {
            val graph = CoreTester.execute(CLASS_NAME, "divIntTwiceByZeroWithCatch()V", CoreState().apply {
                setRegisters(0, 10, "I", 1, 0, "I")
            })
            val refs = graph.outcomes.filterIsInstance<Outcome.Threw>()
                .mapNotNull { (it.exception as? Value.Known)?.raw as? HeapRef }
            // Non-vacuousness first, or the assertion below proves nothing: `assertEquals(0, 0)` passes
            // no matter what HeapIds does. Two, not merely "at least one" -- this fixture's whole point
            // is exercising a SECOND synthesis, and one escaping throw alone cannot show two allocations
            // failing to collide.
            assertEquals(2, refs.size, "expected both synthesized exceptions to escape: $refs")
            assertEquals(refs.size, refs.distinct().size, "each synthesis is its own object: $refs")
        }
    }

    @Nested
    @DisplayName("with binary doubles")
    inner class DoubleMath {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @BeforeEach
        fun setUp() {
            expected = CoreState()
            initial = CoreState()
        }

        @Test
        fun canAddDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 + 20.5, "D")
            CoreTester.test(CLASS_NAME, "addDouble()V", initial, expected)
        }

        /**
         * The 64-bit twin of [FloatMath.anAddFloatOnConstLoadedLiteralBitsAnswersWhatADeviceAnswers], and a
         * separate claim rather than a second example: this domain reads through `Coerce.toDouble`, which is
         * a different function with a different availability rule.
         *
         * `const-wide v0, 0x4000000000000000` stores `Value.Known(4611686018427387904L, "D")` --
         * `const-wide` is the one opcode that puts a `Long` raw under a `D` tag, because it serves `long`
         * and `double` both, and `core/Op.kt`'s `buildConst` records exactly that. So this too is ordinary
         * compiler output. A device reads the pair's 64 bits as double bits: `2.0 + 2.0 = 4.0`. Read by
         * value the operand was `4.611686018427388E18` and the sum `9.223372036854776E18`.
         */
        @Test
        fun anAddDoubleOnConstWideLiteralBitsAnswersWhatADeviceAnswers() {
            initial.setRegisters(0, 4611686018427387904L, "D", 2, 4611686018427387904L, "D")
            expected.setRegisters(0, 4.0, "D")
            CoreTester.test(CLASS_NAME, "addDouble()V", initial, expected)
        }

        @Test
        fun canAddDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 + 20.5, "D")
            CoreTester.test(CLASS_NAME, "addDouble2Addr()V", initial, expected)
        }

        @Test
        fun canDivDouble() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 / 0.5, "D")
            CoreTester.test(CLASS_NAME, "divDouble()V", initial, expected)
        }

        @Test
        fun canDivDouble2Addr() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 / 0.5, "D")
            CoreTester.test(CLASS_NAME, "divDouble2Addr()V", initial, expected)
        }

        @Test
        fun canDivDoubleWithDivisionByZero() {
            // Migrated in the divide-by-zero task. It was never waiting on the fork -- doubles never
            // fork -- only on `divDoubleWithCatch()V`'s overlapping wide register pairs, which that
            // task repaired (rhs at v2 under `.locals 4`). The seed moves from v1 to v2 to follow the
            // repaired operand; the expected addresses are byte-identical to the oracle-derived ones.
            initial.setRegisters(0, 5.0, "D", 2, 0.0, "D")
            // Floats and doubles do not throw exceptions for div0
            val expectedAddresses = intArrayOf(0, 2)
            CoreTester.testVisitation(CLASS_NAME, "divDoubleWithCatch()V", initial, expectedAddresses)
        }

        /**
         * Double division by zero yields Infinity rather than throwing -- which is why the
         * divide-by-zero fork is integral-only. Without this, moving `dividesByZero` up out of the
         * integral branches would pass the whole suite.
         */
        @Test
        fun dividingADoubleByZeroYieldsInfinityRatherThanThrowing() {
            initial.setRegisters(0, 1.0, "D", 2, 0.0, "D")
            expected.setRegisters(0, Double.POSITIVE_INFINITY, "D")
            CoreTester.test(CLASS_NAME, "divDouble()V", initial, expected)
        }

        @Test
        fun canMulDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 * 20.5, "D")
            CoreTester.test(CLASS_NAME, "mulDouble()V", initial, expected)
        }

        @Test
        fun canMulDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 * 20.5, "D")
            CoreTester.test(CLASS_NAME, "mulDouble2Addr()V", initial, expected)
        }

        @Test
        fun canRemDouble() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 % 0.5, "D")
            CoreTester.test(CLASS_NAME, "remDouble()V", initial, expected)
        }

        @Test
        fun canRemDouble2Addr() {
            initial.setRegisters(0, 22.9, "D", 2, 0.5, "D")
            expected.setRegisters(0, 22.9 % 0.5, "D")
            CoreTester.test(CLASS_NAME, "remDouble2Addr()V", initial, expected)
        }

        @Test
        fun canSubDouble() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 - 20.5, "D")
            CoreTester.test(CLASS_NAME, "subDouble()V", initial, expected)
        }

        @Test
        fun canSubDouble2Addr() {
            initial.setRegisters(0, 0.5, "D", 2, 20.5, "D")
            expected.setRegisters(0, 0.5 - 20.5, "D")
            CoreTester.test(CLASS_NAME, "subDouble2Addr()V", initial, expected)
        }
    }

    @Nested
    @DisplayName("with binary floats")
    inner class FloatMath {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
            expected = CoreState()
        }

        @Test
        fun canAddFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f + 20.5f, "F")
            CoreTester.test(CLASS_NAME, "addFloat()V", initial, expected)
        }

        /**
         * `add-float` reading two registers loaded the way a compiler loads a float literal, which is
         * `const v0, 0x3f800000` -> `Value.Known(1065353216, "I")` (`core/Op.kt`'s `buildConst`; the raw is
         * an `Int` and the tag is `I`, because `const` serves int/char/byte/short/float alike).
         *
         * Bug class, and it is the more severe of the two halves Plan 8 Task 3 touched: a confident wrong
         * constant on **verifier-VALID** dex -- ordinary compiler output, not the unverifiable input
         * `Coerce.toInt`'s boundary is about. Oracle: a device reads v0's 32 bits AS float bits, so
         * `0x3f800000` is `1.0f` and the sum is `2.0f`. Read by value the operand was `1.0653532E9` and the
         * sum `2.1307064E9` -- nine orders of magnitude out, folded as a definite `Known`.
         *
         * The seed is discriminating by construction: `1065353216` is meaningless as a float VALUE and is
         * exactly `1.0f` as float BITS, so no arithmetic slip can produce `2.0f` by accident. Numbers
         * derived from `Float.intBitsToFloat` and cross-checked on a live JVM, not read off this engine.
         */
        @Test
        fun anAddFloatOnConstLoadedLiteralBitsAnswersWhatADeviceAnswers() {
            initial.setRegisters(0, 1065353216, "I", 1, 1065353216, "I")
            expected.setRegisters(0, 2.0f, "F")
            CoreTester.test(CLASS_NAME, "addFloat()V", initial, expected)
        }

        @Test
        fun canAddFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f + 20.5f, "F")
            CoreTester.test(CLASS_NAME, "addFloat2Addr()V", initial, expected)
        }

        @Test
        fun canDivFloat() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f / 0.5f, "F")
            CoreTester.test(CLASS_NAME, "divFloat()V", initial, expected)
        }

        @Test
        fun canDivFloat2Addr() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f / 0.5f, "F")
            CoreTester.test(CLASS_NAME, "divFloat2Addr()V", initial, expected)
        }

        @Test
        fun canDivFloatWithDivisionByZero() {
            initial.setRegisters(0, 5f, "F", 1, 0f, "F")
            // Floats and doubles do not throw exceptions for div0
            val expected = intArrayOf(0, 2)
            CoreTester.testVisitation(CLASS_NAME, "divFloatWithCatch()V", initial, expected)
        }

        /**
         * Float division by zero yields Infinity rather than throwing -- which is why the
         * divide-by-zero fork is integral-only. Without this, moving `dividesByZero` up out of the
         * integral branches would pass the whole suite.
         */
        @Test
        fun dividingAFloatByZeroYieldsInfinityRatherThanThrowing() {
            initial.setRegisters(0, 1f, "F", 1, 0f, "F")
            expected.setRegisters(0, Float.POSITIVE_INFINITY, "F")
            CoreTester.test(CLASS_NAME, "divFloat()V", initial, expected)
        }

        @Test
        fun canMulFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f * 20.5f, "F")
            CoreTester.test(CLASS_NAME, "mulFloat()V", initial, expected)
        }

        @Test
        fun canMulFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f * 20.5f, "F")
            CoreTester.test(CLASS_NAME, "mulFloat2Addr()V", initial, expected)
        }

        @Test
        fun canRemFloat() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f % 0.5f, "F")
            CoreTester.test(CLASS_NAME, "remFloat()V", initial, expected)
        }

        @Test
        fun canRemFloat2Addr() {
            initial.setRegisters(0, 22.9f, "F", 1, 0.5f, "F")
            expected.setRegisters(0, 22.9f % 0.5f, "F")
            CoreTester.test(CLASS_NAME, "remFloat2Addr()V", initial, expected)
        }

        @Test
        fun canSubFloat() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f - 20.5f, "F")
            CoreTester.test(CLASS_NAME, "subFloat()V", initial, expected)
        }

        @Test
        fun canSubFloat2Addr() {
            initial.setRegisters(0, 0.5f, "F", 1, 20.5f, "F")
            expected.setRegisters(0, 0.5f - 20.5f, "F")
            CoreTester.test(CLASS_NAME, "subFloat2Addr()V", initial, expected)
        }
    }

    @Nested
    @DisplayName("with binary integers")
    inner class IntegerMath {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @Test
        fun canAddByteAndChar() {
            val b: Byte = 0xf
            initial.setRegisters(0, 'a', "C", 1, b, "B")
            expected.setRegisters(0, 'a'.code.toByte() + b, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 + 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 + 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt2Addr()V", initial, expected)
        }

        /**
         * An `I`-domain operator reading a register whose raw is a genuinely-computed `Float`: the whole
         * `I` domain of this op, not just the divisor [DivideByZero] already guards.
         *
         * Bug class: a confidently wrong `Known`, which is what constant propagation folds into
         * rewritten bytecode. Oracle: Dalvik, not engine output. `add-int` reads v0's **32 bits** as a
         * signed int; `5.9f` is `0x40BCCCCD`, i.e. 1086115021, so a device answers `1086115028`.
         * `Coerce.toInt(5.9f)` performed a [JLS] §5.1.3 narrowing conversion instead and this op answered
         * a definite `Known(12)`.
         *
         * ⚠️ **The justification here was wrong and is retracted, not softened.** It read *"the engine has no
         * bit view of a register, so 'not provable' is the honest answer"*. The engine DOES have one --
         * `Float.floatToRawIntBits(5.9f)` is exactly the 1086115021 above -- so `Unknown` is the SAFE answer
         * rather than the only one, and this test pins a labelled **precision loss**. `Coerce.toInt`'s KDoc
         * carries the price and the two things that block taking it.
         *
         * Which SIDE of the soundness line, stated because over-refusal is the equally-real mirror
         * defect this plan family shipped twice: `add-int` has exactly one successor whatever it computes
         * ([BinaryMathOp.canDivideByZero] excludes it), so refusing the value explores neither more nor
         * fewer paths -- it gives up a fold the engine was never entitled to make. Contrast
         * [DivideByZero.anIntDivideReadingAComputedFloatDivisorStillExploresTheNormalPath], where the
         * same refusal has to ADD a transition, because there the narrowed answer had *dropped* one.
         *
         * **No differential could have found this**: the oracle narrows identically, through
         * `Value.toInteger` -> `Utils.getIntegerValue` -> `castToPrimitive` -> `intValue()`, so both
         * engines answer `Known(12)` and agree while both are wrong. The registry entry is
         * `unary-int-to-float-computed-float-raw`, one op family over, because the divergence is
         * `Coerce`'s rather than any single op's.
         */
        @Test
        fun anIntDomainOperatorReadingAComputedFloatRawIsNotProvable() {
            initial.setRegisters(0, 5.9f, "F", 1, 7, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndBoolean() {
            val value = true
            initial.setRegisters(0, value, "Z", 1, 11, "I")
            expected.setRegisters(0, 1 + 11, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndByte() {
            val b: Byte = 0xf
            initial.setRegisters(0, -3, "I", 1, b, "B")
            expected.setRegisters(0, 12, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndChar() {
            // Compiler will actually produce something like this.
            initial.setRegisters(0, "$"[0], "C", 1, 11, "I")
            expected.setRegisters(0, "$"[0].code + 11, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndShort() {
            val value: Short = 5
            initial.setRegisters(0, value, "S", 1, 11, "I")
            expected.setRegisters(0, value + 11, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownChar() {
            initial.setRegisters(0, CoreState.UNKNOWN, "C", 1, 5, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownCharAndByte() {
            initial.setRegisters(0, CoreState.UNKNOWN, "C", 1, CoreState.UNKNOWN, "B")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntAndUnknownInt() {
            initial.setRegisters(0, -3, "I", 1, CoreState.UNKNOWN, "I")
            expected.setRegisters(0, CoreState.UNKNOWN, "I")
            CoreTester.test(Companion.CLASS_NAME, "addInt()V", initial, expected)
        }

        @Test
        fun canAddIntLit16() {
            initial.setRegisters(0, -0xf, "I")
            expected.setRegisters(0, -0xf + 0xff, "I")
            CoreTester.test(Companion.CLASS_NAME, "addIntLit16()V", initial, expected)
        }

        @Test
        fun canAddIntLit8() {
            initial.setRegisters(0, -0xf, "I")
            expected.setRegisters(0, -0xf + 0xf, "I")
            CoreTester.test(Companion.CLASS_NAME, "addIntLit8()V", initial, expected)
        }

        @Test
        fun canAndInt() {
            initial.setRegisters(0, 2, "I", 1, 7, "I")
            expected.setRegisters(0, 2 and 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "andInt()V", initial, expected)
        }

        @Test
        fun canAndInt2Addr() {
            initial.setRegisters(0, 2, "I", 1, 7, "I")
            expected.setRegisters(0, 2 and 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "andInt2Addr()V", initial, expected)
        }

        @Test
        fun canAndIntLit16() {
            initial.setRegisters(0, 2, "I")
            expected.setRegisters(0, 2 and 0xff, "I")
            CoreTester.test(Companion.CLASS_NAME, "andIntLit16()V", initial, expected)
        }

        @Test
        fun canAndIntLit8() {
            initial.setRegisters(0, 2, "I")
            expected.setRegisters(0, 2 and 0xf, "I")
            CoreTester.test(Companion.CLASS_NAME, "andIntLit8()V", initial, expected)
        }

        @Test
        fun canDivInt() {
            initial.setRegisters(0, 12, "I", 1, 3, "I")
            expected.setRegisters(0, 12 / 3, "I")
            CoreTester.test(Companion.CLASS_NAME, "divInt()V", initial, expected)
        }

        /**
         * Bug class: rounding to the nearest integer instead of truncating toward zero. A fraction of
         * exactly 0.5 is the case that uniquely distinguishes them -- round-half-up gives 3, truncation
         * gives 2 -- and no surviving `canDivInt*` test has that shape: `12÷3=4` is exact, and
         * `canDivIntLit16`/`canDivIntLit8` (`256÷255≈1.004`, `16÷15≈1.067`) both have a fraction under
         * 0.5, where rounding and truncating agree. Restores the value claim of the deleted mockk test
         * `intDivisionWithLiteralEqualsExpected` (`10÷4`), which this task's original adjudication
         * wrongly verdicted a duplicate of `canDivIntLit16`/`canDivIntLit8` -- neither of those witnesses
         * the half-integer case. Oracle: Dalvik's `div-int` truncates toward zero, so `10÷4` is exactly
         * `2`, never the `3` naive rounding would give; written as the literal `2` rather than `10 / 4`
         * so the expected value is not derived through the same operator the mutation this test exists
         * to catch would also change.
         */
        @Test
        fun canDivIntWithExactlyHalfIntegerQuotientTruncatesTowardZero() {
            initial.setRegisters(0, 10, "I", 1, 4, "I")
            expected.setRegisters(0, 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "divInt()V", initial, expected)
        }

        @Test
        fun canDivInt2Addr() {
            initial.setRegisters(0, 12, "I", 1, 3, "I")
            expected.setRegisters(0, 12 / 3, "I")
            CoreTester.test(Companion.CLASS_NAME, "divInt2Addr()V", initial, expected)
        }

        @Test
        fun canDivIntLit16() {
            initial.setRegisters(0, 0x100, "I")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "divIntLit16()V", initial, expected)
        }

        @Test
        fun canDivIntLit8() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 1, "I")
            CoreTester.test(Companion.CLASS_NAME, "divIntLit8()V", initial, expected)
        }

        /**
         * Bug class: an ambiguous divisor exploring only ONE of two feasible paths, leaving the catch
         * handler unvisited on a run that still reports Complete. Oracle for the addresses: the
         * fixture's own control flow -- `div-int` @0 forks, the fall-through returns at @2, the handler
         * is @3 and its `goto` back to the return is @4.
         *
         * **The SEED moved and the expected addresses did not.** The corpus seeded an unknown DIVIDEND
         * over a known divisor of 5, because the oracle's fork condition tests whether the *result* is
         * unknown (`opcode/BinaryMathOp.kt:64-67`) rather than whether the *divisor* is -- so it
         * reaches @3 for an infeasible reason: `unknown / 5` cannot throw, since only a zero divisor
         * makes `div-int` throw in Dalvik. Keeping that seed would have made this test's own name false
         * here. The ambiguity moves to the divisor, which is what "an unknown value visits the
         * exception handler" actually requires; the original seed's behavior is pinned separately by
         * [anUnknownDividendOverAKnownNonZeroDivisorDoesNotFork].
         */
        @Test
        fun canDivIntWithCatchWithUnknownValueVisitsExceptionHandler() {
            initial.setRegisters(0, 12, "I", 1, CoreState.UNKNOWN, "I")
            val expectedAddresses = intArrayOf(0, 2, 3, 4)
            CoreTester.testVisitation(Companion.CLASS_NAME, "divIntWithCatch()V", initial, expectedAddresses)
        }

        /**
         * Bug class: forking on "the result is unknown" instead of "the divisor may be zero", which
         * explores a throw Dalvik cannot produce. A known non-zero divisor means `div-int` cannot throw
         * whatever the dividend is, so the handler at @3 is unreachable for this input and only @0/@2
         * run. That is a precision claim, not a soundness one -- the dropped path is infeasible.
         *
         * This is the corpus seed of [canDivIntWithCatchWithUnknownValueVisitsExceptionHandler], kept
         * with the expectation corrected from the oracle's [0, 2, 3, 4]: an INTENDED DIVERGENCE, since
         * the oracle over-approximates here in the opposite direction to its `rem` under-approximation.
         */
        @Test
        fun anUnknownDividendOverAKnownNonZeroDivisorDoesNotFork() {
            initial.setRegisters(0, CoreState.UNKNOWN, "I", 1, 5, "I")
            CoreTester.testVisitation(Companion.CLASS_NAME, "divIntWithCatch()V", initial, intArrayOf(0, 2))
        }

        @Test
        fun canMulInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 * 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "mulInt()V", initial, expected)
        }

        @Test
        fun canMulInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 10, "I")
            expected.setRegisters(0, -3 * 10, "I")
            CoreTester.test(Companion.CLASS_NAME, "mulInt2Addr()V", initial, expected)
        }

        @Test
        fun canMulIntLit16() {
            initial.setRegisters(0, -3, "I")
            expected.setRegisters(0, -3 * 0xff, "I")
            CoreTester.test(Companion.CLASS_NAME, "mulIntLit16()V", initial, expected)
        }

        @Test
        fun canMulIntLit8() {
            initial.setRegisters(0, -3, "I")
            expected.setRegisters(0, -3 * 0xf, "I")
            CoreTester.test(Companion.CLASS_NAME, "mulIntLit8()V", initial, expected)
        }

        @Test
        fun canOrInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 or 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "orInt()V", initial, expected)
        }

        @Test
        fun canOrInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 or 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "orInt2Addr()V", initial, expected)
        }

        @Test
        fun canOrIntLit16() {
            initial.setRegisters(0, 5, "I")
            expected.setRegisters(0, 5 or 0xff, "I")
            CoreTester.test(Companion.CLASS_NAME, "orIntLit16()V", initial, expected)
        }

        @Test
        fun canOrIntLit8() {
            initial.setRegisters(0, 5, "I")
            expected.setRegisters(0, 5 or 0xf, "I")
            CoreTester.test(Companion.CLASS_NAME, "orIntLit8()V", initial, expected)
        }

        @Test
        fun canRemInt() {
            initial.setRegisters(0, 7, "I", 1, 5, "I")
            expected.setRegisters(0, 7 % 5, "I")
            CoreTester.test(Companion.CLASS_NAME, "remInt()V", initial, expected)
        }

        @Test
        fun canRemInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 5, "I")
            expected.setRegisters(0, 7 % 5, "I")
            CoreTester.test(Companion.CLASS_NAME, "remInt2Addr()V", initial, expected)
        }

        @Test
        fun canRemIntLit16() {
            initial.setRegisters(0, 0x100, "I")
            expected.setRegisters(0, 0x100 % 0xff, "I")
            CoreTester.test(Companion.CLASS_NAME, "remIntLit16()V", initial, expected)
        }

        @Test
        fun canRemIntLit8() {
            initial.setRegisters(0, 0x10, "I")
            expected.setRegisters(0, 0x10 % 0xf, "I")
            CoreTester.test(Companion.CLASS_NAME, "remIntLit8()V", initial, expected)
        }

        @Test
        fun canRSubInt() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 0xff - 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "RSubInt()V", initial, expected)
        }

        @Test
        fun canRSubIntLit8() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 0xf - 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "RSubIntLit8()V", initial, expected)
        }

        @Test
        fun canShlInt() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 shl 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shlInt()V", initial, expected)
        }

        @Test
        fun canShlInt2Addr() {
            initial.setRegisters(0, 5, "I", 1, 2, "I")
            expected.setRegisters(0, 5 shl 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shlInt2Addr()V", initial, expected)
        }

        @Test
        fun canShlIntLit8() {
            initial.setRegisters(0, 3, "I")
            expected.setRegisters(0, 3 shl 0x2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shlIntLit8()V", initial, expected)
        }

        @Test
        fun canShrInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shrInt()V", initial, expected)
        }

        @Test
        fun canShrInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shrInt2Addr()V", initial, expected)
        }

        @Test
        fun canShrIntLit8() {
            // The fixture (`.locals 1`) reads only v0 -- `shr-int/lit8 v0, v0, 0x2` has no second
            // register operand, the shift distance is the literal 0x2. The oracle-derived corpus set
            // register 1 too (dead: never read, and out of range for this method's declared register
            // count), a copy-paste artifact from the register-register `canShrInt` above it that the
            // old engine's unbounded register map tolerated silently. Corrected to match its literal-
            // form siblings (canShlIntLit8, canUshrIntLit8), which seed only v0.
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 7 shr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "shrIntLit8()V", initial, expected)
        }

        @Test
        fun canSubInt() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 - 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "subInt()V", initial, expected)
        }

        @Test
        fun canSubInt2Addr() {
            initial.setRegisters(0, -3, "I", 1, 7, "I")
            expected.setRegisters(0, -3 - 7, "I")
            CoreTester.test(Companion.CLASS_NAME, "subInt2Addr()V", initial, expected)
        }

        @Test
        fun canUshrInt() {
            initial.setRegisters(0, -14, "I", 1, 2, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "ushrInt()V", initial, expected)
        }

        @Test
        fun canUshrInt2Addr() {
            initial.setRegisters(0, -14, "I", 1, 2, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "ushrInt2Addr()V", initial, expected)
        }

        @Test
        fun canUshrIntLit8() {
            initial.setRegisters(0, -14, "I")
            expected.setRegisters(0, -14 ushr 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "ushrIntLit8()V", initial, expected)
        }

        @Test
        fun canXorInt() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 xor 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "xorInt()V", initial, expected)
        }

        @Test
        fun canXorInt2Addr() {
            initial.setRegisters(0, 7, "I", 1, 2, "I")
            expected.setRegisters(0, 7 xor 2, "I")
            CoreTester.test(Companion.CLASS_NAME, "xorInt2Addr()V", initial, expected)
        }

        @Test
        fun canXorIntLit16() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 7 xor 0x10, "I")
            CoreTester.test(Companion.CLASS_NAME, "xorIntLit16()V", initial, expected)
        }

        @Test
        fun canXorIntLit8() {
            initial.setRegisters(0, 7, "I")
            expected.setRegisters(0, 7 xor 3, "I")
            CoreTester.test(Companion.CLASS_NAME, "xorIntLit8()V", initial, expected)
        }

        @BeforeEach
        fun setUp() {
            initial = CoreState()
            expected = CoreState()
        }
    }

    @Nested
    @DisplayName("with binary longs")
    inner class LongMath {
        private lateinit var initial: CoreState
        private lateinit var expected: CoreState

        @BeforeEach
        fun setUp() {
            initial = CoreState()
            expected = CoreState()
        }

        @Test
        fun canAddLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J")
            CoreTester.test(CLASS_NAME, "addLong()V", initial, expected)
        }

        @Test
        fun canAddLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L + 0x200000000L, "J")
            CoreTester.test(CLASS_NAME, "addLong2Addr()V", initial, expected)
        }

        /**
         * The `J`-domain twin of [IntegerMath.anIntDomainOperatorReadingAComputedFloatRawIsNotProvable],
         * and a **separate claim** rather than a second example: this domain reads through
         * `Coerce.toLong`, so a refusal covering only the 32-bit read leaves every `*-long` answering
         * confidently. No review or register entry named the `toLong` sites before this task.
         *
         * Oracle: `add-long` reads v0/v1 as 64 bits; `5.9f` occupies 32, so a device's answer depends on
         * a register this op does not even read -- unmodelable, which is precisely why the answer is
         * `Unknown` rather than any number. `Coerce.toLong(5.9f)` was `5L`, so this op answered a definite
         * `Known(12)`. Same side of the line as its `I`-domain twin: `add-long` has one successor either
         * way, so no path is added or lost and only an unearned fold is given up.
         */
        @Test
        fun aLongDomainOperatorReadingAComputedFloatRawIsNotProvable() {
            initial.setRegisters(0, 5.9f, "F", 2, 7L, "J")
            expected.setRegisters(0, CoreState.UNKNOWN, "J")
            CoreTester.test(CLASS_NAME, "addLong()V", initial, expected)
        }

        @Test
        fun canAndLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL and 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "andLong()V", initial, expected)
        }

        @Test
        fun canAndLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL and 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "andLong2Addr()V", initial, expected)
        }

        @Test
        fun canDivLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L / 3L, "J")
            CoreTester.test(CLASS_NAME, "divLong()V", initial, expected)
        }

        @Test
        fun canDivLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L / 3L, "J")
            CoreTester.test(CLASS_NAME, "divLong2Addr()V", initial, expected)
        }

        /**
         * Bug class: a dividend smaller in magnitude than its divisor producing a nonzero quotient --
         * e.g. rounding to the nearest integer instead of truncating toward zero. No surviving
         * `canDivLong*`/`canDivInt*` test has this shape: every other one's dividend is either an exact
         * multiple of its divisor or otherwise larger in magnitude. Restores the value claim of the
         * deleted mockk test `longDivisionWithThreeArgumentsAndResultLessThanOneEqualsZero`, whose
         * literals this keeps (traceable to the original); that test also asserted `op.toString()`,
         * restored separately in `OpBuilderTest`. Oracle: Dalvik's `div-long` truncates toward zero, so
         * a smaller-magnitude dividend is exactly 0 -- never a fraction, and never rounded up to 1.
         * Written as the literal `0L` rather than `1120403456L / 1149239296L`: expected values come
         * from the domain rule, not from an expression using the very operator the mutation this test
         * exists to catch would also change.
         */
        @Test
        fun canDivLongWithDividendSmallerThanDivisorTruncatesToZero() {
            initial.setRegisters(0, 1120403456L, "J", 2, 1149239296L, "J")
            expected.setRegisters(0, 0L, "J")
            CoreTester.test(CLASS_NAME, "divLong()V", initial, expected)
        }

        /**
         * The `J` half of [IntegerMath.canDivIntWithCatchWithUnknownValueVisitsExceptionHandler], and
         * the same bug class. The original name's "Unkown" typo is kept deliberately: the corpus name
         * is how a reviewer greps the oracle suite to confirm this migration is 1:1 and nothing was
         * silently dropped.
         *
         * The seed's ambiguity was already on the divisor here, so only its REGISTER moved -- v1 to v2
         * -- following `divLongWithCatch()V`'s repaired rhs operand. Expected addresses are
         * byte-identical to the oracle-derived ones.
         */
        @Test
        fun canDivLongWithCatchWithUnkownValueVisitsExceptionHandler() {
            initial.setRegisters(0, 5L, "J", 2, CoreState.UNKNOWN, "J")
            val expectedAddresses = intArrayOf(0, 2, 3, 4)
            CoreTester.testVisitation(CLASS_NAME, "divLongWithCatch()V", initial, expectedAddresses)
        }

        @Test
        fun canMulLong() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L * 3L, "J")
            CoreTester.test(CLASS_NAME, "mulLong()V", initial, expected)
        }

        @Test
        fun canMulLong2Addr() {
            initial.setRegisters(0, 0x300000000L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000000L * 3L, "J")
            CoreTester.test(CLASS_NAME, "mulLong2Addr()V", initial, expected)
        }

        @Test
        fun canOrLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL or 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "orLong()V", initial, expected)
        }

        @Test
        fun canOrLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL or 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "orLong2Addr()V", initial, expected)
        }

        @Test
        fun canRemLong() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000123L % 3L, "J")
            CoreTester.test(CLASS_NAME, "remLong()V", initial, expected)
        }

        @Test
        fun canRemLong2Addr() {
            initial.setRegisters(0, 0x300000123L, "J", 2, 3L, "J")
            expected.setRegisters(0, 0x300000123L % 3L, "J")
            CoreTester.test(CLASS_NAME, "remLong2Addr()V", initial, expected)
        }

        @Test
        fun canShlLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shl 5, "J")
            CoreTester.test(CLASS_NAME, "shlLong()V", initial, expected)
        }

        @Test
        fun canShlLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shl 5, "J")
            CoreTester.test(CLASS_NAME, "shlLong2Addr()V", initial, expected)
        }

        @Test
        fun canShlLongWithShiftDistanceAboveThirtyOne() {
            // Bug class: masking a wide shift's distance to the INT rule's 5 bits instead of
            // Dalvik's 6 for a wide shift -- which would compute `shl 1` instead of `shl 33`. Kotlin's
            // `Long shl Int` already masks to 6 bits on its own, so no source-level mask is needed;
            // this is the test that would catch a regression that added one anyway (e.g.
            // copy-pasting the Int path's `and 0x1f`). Oracle: Dalvik's shl-long spec (six-bit shift
            // distance), not engine output.
            initial.setRegisters(0, 1L, "J", 2, 33L, "J")
            expected.setRegisters(0, 1L shl 33, "J")
            CoreTester.test(CLASS_NAME, "shlLong()V", initial, expected)
        }

        @Test
        fun canShrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shr 5, "J")
            CoreTester.test(CLASS_NAME, "shrLong()V", initial, expected)
        }

        @Test
        fun canShrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L shr 5, "J")
            CoreTester.test(CLASS_NAME, "shrLong2Addr()V", initial, expected)
        }

        @Test
        fun canSubLong() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J")
            CoreTester.test(CLASS_NAME, "subLong()V", initial, expected)
        }

        @Test
        fun canSubLong2Addr() {
            initial.setRegisters(0, 0x100000000L, "J", 2, 0x200000000L, "J")
            expected.setRegisters(0, 0x100000000L - 0x200000000L, "J")
            CoreTester.test(CLASS_NAME, "subLong2Addr()V", initial, expected)
        }

        @Test
        fun canUshrLong() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L ushr 5, "J")
            CoreTester.test(CLASS_NAME, "ushrLong()V", initial, expected)
        }

        @Test
        fun canUshrLong2Addr() {
            initial.setRegisters(0, 0x10000L, "J", 2, 5L, "J")
            expected.setRegisters(0, 0x10000L ushr 5, "J")
            CoreTester.test(CLASS_NAME, "ushrLong2Addr()V", initial, expected)
        }

        @Test
        fun canXorLong() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL xor 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "xorLong()V", initial, expected)
        }

        @Test
        fun canXorLong2Addr() {
            initial.setRegisters(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J")
            expected.setRegisters(0, 0x12345abcdL xor 0x1234567890L, "J")
            CoreTester.test(CLASS_NAME, "xorLong2Addr()V", initial, expected)
        }
    }
}
