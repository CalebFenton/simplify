package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * The twelve binary arithmetic/logical operators across four operand domains.
 *
 * Both the operator and the domain come from the mnemonic, parsed by hyphen-segment rather than by
 * prefix: `rsub-int` must not be read as `sub` (the oracle gets away with `startsWith` only because
 * "rsub-int" happens not to start with "sub", which is luck rather than design).
 *
 * The right-hand operand is a [Operand.Register] or, for the `/lit8` and `/lit16` forms, an
 * [Operand.Literal] typed `I` -- Dalvik's literal forms are int-only.
 *
 * Divide-by-zero routing lives in [armsFor]; see its KDoc.
 */
internal class BinaryMathOp(
    address: Int,
    override val name: String,
    val destRegister: Int,
    val lhsRegister: Int,
    val rhs: Operand,
    private val operator: Operator,
    private val domain: VirtualType,
) : Op(address) {

    /** The right-hand operand: another register, or a literal baked into the instruction. */
    sealed interface Operand {
        data class Register(val register: Int) : Operand
        data class Literal(val value: Int) : Operand
    }

    enum class Operator { ADD, SUB, MUL, DIV, REM, AND, OR, XOR, SHL, SHR, USHR, RSUB }

    override val registersRead: Set<Int> = when (rhs) {
        is Operand.Register -> setOf(lhsRegister, rhs.register)
        is Operand.Literal -> setOf(lhsRegister)
    }

    // Both halves for a wide (long) result: the high half is not free scratch (see
    // Op.destinationRegisters and ExecutionGraph.availableRegisters).
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, domain.isWide)

    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.read(lhsRegister)
        val right = when (rhs) {
            is Operand.Register -> frame.read(rhs.register)
            is Operand.Literal -> Value.Known(rhs.value, INT)
        }
        val arms = armsFor(right)
        // Dalvik does not write the destination register when the division throws, so a path that
        // DEFINITELY throws must not either: the throwing node is piled and counted as executed, so
        // `consensus(address, destRegister)` would otherwise report `Unknown(domain)` where the pre-op
        // value still stands -- and a handler successor copies this same post-op frame, so it would see
        // it too. Mostly that is a harmless move up the lattice, but not when the destination held a
        // reference: `Unknown("I")` over a `Ljava/lang/String;` is type-wrong, not merely imprecise.
        //
        // A `[Continue, Throw]` fork stays exact: only the continue arm carries the destination update,
        // while the throw arm retains the pre-op value.
        val result = computed(lhs, right) ?: Value.Unknown(domain)
        return OpExecution.of(arms.map { arm ->
            if (arm is OpArm.Continue) arm.copy(update = OpUpdate.assign(destRegister, result)) else arm
        })
    }

    /**
     * The computed result, or null when it is not provable -- an unknown or uncoercible operand, or a
     * divisor this op cannot read at its own width ([knownDivisor]) -- which the caller turns into
     * `Unknown(domain)` rather than a guess.
     *
     * A division by a known zero returns null too, because there is no result: the path throws
     * instead. [armsFor] is what distinguishes "no result because unknown" from "no result
     * because it throws", and both route the zero question through the ONE [knownDivisor] below so
     * they cannot disagree about it -- see that function's KDoc for the defect that made this
     * necessary.
     */
    private fun computed(lhs: Value, rhs: Value): Value? {
        if (lhs !is Value.Known || rhs !is Value.Known) return null
        // Hoisted out of the domain branches on purpose: it is the same question [armsFor] asks,
        // so it is asked through the same function rather than re-derived per branch.
        //
        // 🔴 KNOWN WIDTH CONFLATION, recorded by Plan 8 Task 3 fix round 2 rather than left to be
        // discovered. The `"J"` branch below reads BOTH operands through `Coerce.toLong`, i.e. at 64 bits --
        // but for SHL/SHR/USHR the second operand is Dalvik's 32-bit shift DISTANCE, and [longResult]'s own
        // comment says so ("Long shifts take an int distance"). Harmless today only because `Coerce.toLong`
        // sign-extends a narrow raw; it becomes a live defect the moment anything makes that widen refuse,
        // which is precisely what the (declined) extension of the bit read to the integral widths would do.
        //
        // AND THE COVERAGE IS INVERTED, which is the part worth knowing: every core-only long-shift test in
        // `core/op/BinaryMathOpTest` seeds the distance as a `Long` under a `"J"` tag (`canShlLong`,
        // `canShlLong2Addr`, `canShlLongWithShiftDistanceAboveThirtyOne`, `canShrLong`, `canShrLong2Addr`,
        // `canUshrLong`, `canUshrLong2Addr` -- all seven), so the op-level suite ENCODES the conflation
        // rather than catching it. The one witness that the distance is narrow in practice is the
        // differential fixture `binary-shl-long`, which seeds `33` under `"I"`. Consequence for whoever
        // fixes this: a CORRECT change here looks like a regression in exactly one place, and that place is
        // `newCoreAgreesWithOracleOnEverySupportedFixture`, not any test named after the opcode.
        if (canDivideByZero() && knownDivisor(rhs).let { it == null || it == 0L }) return null
        // The four integral reads below answer null for a Float/Double raw as well as for a non-numeric
        // one -- a register read at a width whose bits `Coerce` cannot see (see `Coerce.toInt`). Both
        // reasons land on the same `return null`, i.e. `Unknown(domain)`, which is right for both: an
        // unprovable value is not a value (spec §2.2). Unlike [knownDivisor] this is not a path question
        // -- these operators have one successor either way -- so refusing costs a fold, never a path.
        val raw: Any = when (domain.descriptor) {
            "I" -> {
                val a = Coerce.toInt(lhs.raw) ?: return null
                val b = Coerce.toInt(rhs.raw) ?: return null
                intResult(a, b)
            }
            "J" -> {
                val a = Coerce.toLong(lhs.raw) ?: return null
                val b = Coerce.toLong(rhs.raw) ?: return null
                longResult(a, b)
            }
            else -> return floatingResult(lhs, rhs)
        }
        return Value.Known(raw, domain)
    }

    private fun intResult(a: Int, b: Int): Any = when (operator) {
        Operator.ADD -> a + b
        Operator.SUB -> a - b
        Operator.MUL -> a * b
        Operator.DIV -> a / b
        Operator.REM -> a % b
        Operator.AND -> a and b
        Operator.OR -> a or b
        Operator.XOR -> a xor b
        // Dalvik uses the low 5 bits of the shift distance; Kotlin's Int shifts already mask to 5,
        // so this `and` is belt-and-braces rather than load-bearing.
        Operator.SHL -> a shl (b and 0x1f)
        Operator.SHR -> a shr (b and 0x1f)
        Operator.USHR -> a ushr (b and 0x1f)
        Operator.RSUB -> b - a
    }

    private fun longResult(a: Long, b: Long): Any = when (operator) {
        Operator.ADD -> a + b
        Operator.SUB -> a - b
        Operator.MUL -> a * b
        Operator.DIV -> a / b
        Operator.REM -> a % b
        Operator.AND -> a and b
        Operator.OR -> a or b
        Operator.XOR -> a xor b
        // Long shifts take an int distance and Kotlin masks it to 6 bits, matching Dalvik -- no
        // explicit mask needed (and none should be added: a 5-bit mask here would silently turn a
        // shift-by-33 into a shift-by-1).
        Operator.SHL -> a shl b.toInt()
        Operator.SHR -> a shr b.toInt()
        Operator.USHR -> a ushr b.toInt()
        // rsub has no long form in Dalvik, so OpBuilder can never build this pair.
        Operator.RSUB -> error("rsub-long does not exist; decode built $name")
    }

    /**
     * Float and double: five operators only. Dalvik has no `and`/`or`/`xor`/shift forms for them, so
     * the `else` branch is unreachable through [OpBuilder] and loud rather than silent.
     *
     * Division by zero does NOT throw here -- it yields +/-Infinity, and `0.0/0.0` yields NaN. That is
     * why [canDivideByZero] excludes these two domains, and therefore why [computed]'s zero guard never
     * fires on the way to this function.
     */
    private fun floatingResult(lhs: Value.Known, rhs: Value.Known): Value? = when (domain.descriptor) {
        "F" -> {
            val a = Coerce.toFloat(lhs.raw) ?: return null
            val b = Coerce.toFloat(rhs.raw) ?: return null
            Value.Known(
                when (operator) {
                    Operator.ADD -> a + b
                    Operator.SUB -> a - b
                    Operator.MUL -> a * b
                    Operator.DIV -> a / b
                    Operator.REM -> a % b
                    else -> error("$operator has no float form; decode built $name")
                },
                domain,
            )
        }
        "D" -> {
            val a = Coerce.toDouble(lhs.raw) ?: return null
            val b = Coerce.toDouble(rhs.raw) ?: return null
            Value.Known(
                when (operator) {
                    Operator.ADD -> a + b
                    Operator.SUB -> a - b
                    Operator.MUL -> a * b
                    Operator.DIV -> a / b
                    Operator.REM -> a % b
                    else -> error("$operator has no double form; decode built $name")
                },
                domain,
            )
        }
        else -> error("Unexpected binary math domain ${domain.descriptor} in $name")
    }

    /**
     * Divide-by-zero routing, and the engine's first fork across two transitions.
     *
     * Three cases, and the middle one is the reason this VM exists:
     *  - a **known** zero divisor: the path definitely throws, so `[Throw]` alone -- continuing would
     *    explore a path Dalvik never takes;
     *  - a divisor whose zero-ness is **not provable** with an integral `div`/`rem` -- unknown,
     *    uncoercible, or a `Float`/`Double` raw this engine cannot read at the op's own width (see
     *    [knownDivisor]): both outcomes are feasible, so `[Continue, Throw]`. Forking is not a precision
     *    nicety here: dropping the exceptional path would leave a handler unexplored on a run still
     *    reporting `Complete`, and dropping the normal path would invent a throw (spec §2.2, more forks
     *    never fewer);
     *  - anything else: `[Continue]`.
     *
     * Only the DIVISOR is consulted, never the result. A known non-zero divisor cannot throw whatever
     * the dividend is -- integer overflow does not throw in Dalvik, `Integer.MIN_VALUE / -1` simply
     * yields `Integer.MIN_VALUE` -- so an unknown dividend over a known non-zero divisor gets one
     * transition. The oracle instead forks on `result.isUnknown` (`opcode/BinaryMathOp.kt:64`), which
     * explores an infeasible throw for exactly that input; see `BinaryMathOpTest`'s
     * `anUnknownDividendOverAKnownNonZeroDivisorDoesNotFork`.
     *
     * `Continue` is listed first to keep arm construction deterministic and match the reading order of the
     * code. Public outcome order is deliberately not a semantic contract.
     *
     * INTENDED DIVERGENCE from the oracle, in both directions, because its condition
     * (`opcode/BinaryMathOp.kt:64`) asks about the wrong two things:
     *  - it forks only for `DIV`, never `REM`, although `rem-int`/`rem-long` by zero throws identically
     *    -- and its own `doIntegerOperation` catches `ArithmeticException` for both (`:185`). So on an
     *    unknown divisor it explores one fewer feasible path than exists while reporting the run
     *    complete, which is the soundness class this rewrite exists to remove;
     *  - it gates on `result.isUnknown`, true when EITHER operand is unknown, so it forks an infeasible
     *    throw for the unknown-dividend case this KDoc's third paragraph describes.
     *
     * Both are recorded in `Fixtures.intendedDivergences` as `ORACLE_MISJUDGES_FEASIBILITY`
     * (`binary-rem-int-unknown-divisor` and `binary-div-int-unknown-dividend`), which is what makes them
     * checkable claims rather than prose: each entry names the exact addresses the oracle misses or
     * invents, and the registry test fails if either stops being true.
     */
    private fun armsFor(rhs: Value): List<OpArm> {
        if (!canDivideByZero()) return listOf(OpArm.Continue())
        return when (knownDivisor(rhs)) {
            null -> listOf(OpArm.Continue(), divideByZeroArm())
            0L -> listOf(divideByZeroArm())
            else -> listOf(OpArm.Continue())
        }
    }

    /**
     * Whether a zero divisor could make this operation throw at all: only integral `div`/`rem`, since
     * `F`/`D` division by zero yields +/-Infinity or NaN.
     *
     * The domain half is load-bearing rather than defensive. [knownDivisor] coerces to `Long`, and
     * `Coerce.toLong(0.5f)` is `0L` -- so a zero check reached from the float or double domain would
     * fire on divisors that are not zero, turning `22.9f / 0.5f` into a thrown `ArithmeticException`.
     *
     * **That sentence's reasoning covers a second case this guard does not, and the missing half lost a
     * feasible path.** The trap was [Coerce]'s numeric NARROWING, not the float *domain*: an `I`/`J`
     * domain op **reading** a `Float`/`Double` raw hit the identical `Coerce.toInt(0.5f) == 0` and
     * routed [armsFor] to `[Throw]` alone -- one arm, no `Continue`, on a run still
     * reporting `Complete` -- while Dalvik reads that register's BITS (`0x3F000000 == 1056964608` for
     * `0.5f`) and does not throw. Reachable with this build's opcode set alone: `div-float` or
     * `int-to-float` leaves a genuinely-computed `Float` raw in a register and any `div-int`/`rem-int`
     * reading it lands here; `Coerce.toLong(0.5) == 0L` routed `div-long` the same way. That half is now
     * closed **inside [Coerce] itself** rather than here -- `Coerce.toInt` answers null for a `Float`/
     * `Double` raw, so [knownDivisor] answers "not provable" (null, both paths explored) without a local
     * key of its own. Plan 8 Task 3 moved it there so the other thirteen arithmetic reads got the same
     * rule; the past tense above is deliberate.
     *
     * **Why that half had to be fixed rather than recorded, and why no differential could have found
     * it.** The oracle has the *same* defect -- it reaches the same `0` divisor through
     * `Utils.getIntegerValue` -> `castToPrimitive` -> `intValue()`, throws, and catches -- so both
     * engines drop the same feasible path and **the differential is structurally blind to this class**.
     * Every other soundness finding in Plan 4 was found by disagreement; this one could not be, which
     * is exactly why it is a fix here and not a note.
     */
    private fun canDivideByZero(): Boolean =
        (operator == Operator.DIV || operator == Operator.REM) &&
            (domain.descriptor == "I" || domain.descriptor == "J")

    /**
     * The divisor's provable value **at this op's own arithmetic width**, or null when it is not
     * provable and both outcomes are feasible -- either because the value is not [Value.Known] at all,
     * or because its raw is not coercible to a number (malformed input, expected for a deobfuscator;
     * see [Coerce]).
     *
     * **The width is load-bearing, and asking at the wrong one lost a feasible path.** This used to
     * coerce unconditionally to `Long` while [computed] coerced the same raw to `Int` for the `I`
     * domain, on the stated (and false) reasoning that the two cannot disagree about zero-ness.
     * [Coerce.toInt] **narrows** to the low 32 bits while [Coerce.toLong] **widens**, so a `div-int`
     * whose divisor raw is a `Long` 2^32 read `Coerce.toInt` = 0 -- "no result, the path throws" --
     * and `Coerce.toLong` = 2^32 -- "continue normally". Net: `Unknown(I)` assigned, one transition,
     * no `Transition.Throw`, and the run still reporting `Complete` while Dalvik reads that register as
     * a 32-bit int whose value is zero and throws. An unexplored feasible path on a fully-trusted run.
     * (The hole is `Long`-raw-specific: `Double`/`Float` `toInt` saturates rather than truncating, so
     * `toInt == 0` iff `|x| < 1` iff `toLong == 0`, and `Boolean`/`Char` are narrow already -- which is
     * why the float-domain guard's `Coerce.toLong(0.5f) == 0L` trap did not surface it.)
     *
     * Reading at the domain's own width is not a compromise between two candidate answers: Dalvik's
     * `div-int` reads a 32-bit register, so the `Int` answer IS the right one and there is no ambiguity
     * to fork on. Forking instead would explore a normal path Dalvik cannot take -- the mirror image of
     * the oracle's over-approximation this class already declines to copy.
     *
     * **A `Float`/`Double` raw is the one case the domain's own width cannot answer at all, so it
     * forks -- and since Plan 8 Task 3 the refusal that makes it fork lives in [Coerce], not here.**
     * [Coerce] used to perform a numeric *narrowing* (JLS 5.1.3: round toward zero, saturate, `NaN -> 0`)
     * where Dalvik *reinterprets* the register's bits -- two different operations that agree for a `Long`
     * raw, because narrowing `Long -> Int` IS bit-truncation on the JVM (which is what makes the width
     * alignment above sound), and disagree for a computed float: every divisor with `0 < |x| < 1` narrows
     * to zero, so a width-only check answered "definitely throws" for a register Dalvik reads as a large
     * non-zero int. "Cannot tell" is the answer that ships and more forks is the safe direction (spec 2.2)
     * -- but note the reason is **not** that the engine cannot model the read: since Task 3's fix round 1 it
     * demonstrably can (`Coerce.toFloat`/`toDouble` do), and this width's refusal is a labelled precision
     * loss with a named blocker in `Coerce.toInt`'s KDoc. Here alone the refusal is also the *fork-adding*
     * direction, so it is safe in the path sense whatever happens to the value sense. The alternative -- keeping the narrowed answer -- is the one
     * thing this VM must never do: assert a specific outcome it cannot prove, and drop the other one from
     * a run that still reports `Complete`.
     *
     * **This is the one of Task 3's fourteen guarded reads that is a PATH question rather than a value
     * question**, so it is the one where the refusal *adds* a transition. The other thirteen sit on
     * single-successor ops, where refusing changes only whether a fold happens. Worth stating because the
     * two directions are graded equally: over-refusing on a path question would invent an infeasible
     * throw, and under-refusing here dropped a feasible one.
     *
     * The raw's RUNTIME type is what decides, not the declared tag -- and [Coerce] cannot make the other
     * mistake, because it is handed a raw and never sees a tag. `const-wide` ships
     * `Value.Known(Long, "D")` (`core/Op.kt`'s `buildConst`), a `D`-tagged raw whose bits `Coerce` and
     * Dalvik DO agree about; keyed to the tag, the refusal would fire on that quirk and turn
     * `divIntByWideZeroLowHalf()V`'s DEFINITE throw into a `[Continue, Throw]` fork -- not a wrong answer,
     * but a precision loss that undoes the point of the width fix two paragraphs up, and one no
     * existence-of-a-throw assertion can see. `BinaryMathOpTest.aWideDivisorWhoseLowThirtyTwoBitsAreZeroThrows`
     * asserts the fork ARITY for exactly that reason, and
     * `CoerceTest.aWideRawUnderAFloatingPointTagStillCoercesBecauseTheRuntimeTypeIsWhatDecides` pins the
     * unit-level half.
     *
     * The residual -- a bit-reinterpreting read, which would make this question provable again and turn
     * the fork back into one transition -- **exists in the engine now** (`Coerce.toFloat`/`toDouble`, fix
     * round 1) and is merely not wired to this width; it is no longer "available in principle".
     * The by-name enumeration residual
     * (`BigDecimal("0.5").toInt()` is `0` too) now lives with the rule, in `Coerce.toInt`'s KDoc.
     */
    private fun knownDivisor(rhs: Value): Long? {
        if (rhs !is Value.Known) return null
        // The Float/Double refusal is [Coerce]'s, not this function's, and there is deliberately no local
        // copy of it: Plan 8 Task 3 moved the rule into `Coerce.toInt` for all fourteen arithmetic reads,
        // and a second spelling here is exactly the divergence Plan 7's closeout found across four array
        // call sites. Subsumption was established by measurement rather than by reading -- deleting the
        // local `if (rhs.raw is Float || rhs.raw is Double) return null` left 898 of 898 green, including
        // both of the tests written for it -- and the reverse mutation now kills them through `Coerce`.
        return when (domain.descriptor) {
            "I" -> Coerce.toInt(rhs.raw)?.toLong()
            else -> Coerce.toLong(rhs.raw)
        }
    }

    /**
     * The thrown value, as a tracked heap object (spec §4.1: a VM-tracked object's raw is a [HeapRef],
     * never the live host object). The host `ArithmeticException` rides inside the object, matching the
     * oracle's shape (`opcode/BinaryMathOp.kt:59-61` wraps the caught host exception) so the corpus's
     * message assertions port with one indirection.
     *
     * Two synthesized exceptions get distinct ids and so do not `agreesWith` each other: two throwing
     * paths join to `Unknown(ArithmeticException)` rather than to one shared exception. That is CORRECT,
     * not a residual imprecision -- two paths that each throw a fresh exception do not throw the same
     * object in Dalvik. Pinned by `BinaryMathOpTest.twoSeparatelySynthesizedExceptionsStillDoNotAgree`.
     */
    private fun divideByZeroArm(): OpArm.Throw = OpArm.Throw(
        evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(ARITHMETIC_EXCEPTION, "/ by zero")),
    )

    override fun toString(): String {
        val right = when (rhs) {
            is Operand.Register -> "r${rhs.register}"
            is Operand.Literal -> "0x${Integer.toHexString(rhs.value)}"
        }
        return "$name r$destRegister, r$lhsRegister, $right"
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")

        /**
         * Named here rather than built per throw so the descriptor is written once: handler resolution
         * compares this type against every `catch` clause, so a typo would silently stop matching
         * `catch (ArithmeticException)` and route every division fork to the unhandled escape instead.
         */
        val ARITHMETIC_EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/ArithmeticException;")
    }
}
