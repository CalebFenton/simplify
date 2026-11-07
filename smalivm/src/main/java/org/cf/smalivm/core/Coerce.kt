package org.cf.smalivm.core

/**
 * Resolves a register's raw host value to a host primitive for arithmetic.
 *
 * This exists because **Dalvik's type tags are deliberately ambiguous**: one `const` opcode serves
 * int/char/byte/short/float and one `const-wide` serves long/double, so a register tagged `I` may
 * hold a `Character`, a `Short`, or a `Boolean` (which Dalvik represents as 1/0), and an `add-int`
 * must read all of them as numbers. The oracle does this through
 * `org/cf/util/Utils.java:56-104` (`castToPrimitive`); this is the same table, made total.
 *
 * **Null means "not coercible", and that is the whole contract.** A non-numeric raw in an arithmetic
 * op is malformed input -- expected input for a deobfuscator -- and the sound answer is
 * [Value.Unknown] of the result type: it keeps the path exploring rather than truncating it, and it
 * never invents a constant (spec §2.2). The oracle instead returns the value unchanged and blows up
 * on the following cast, which the host-failure boundary would turn into `Aborted(HOST_FAILURE)`; that
 * is an intended divergence, registered in `Fixtures.intendedDivergences` as
 * `binary-add-int-uncoercible-operand` and `cmp-float-uncoercible-operand` -- one per op family that
 * calls in here, because each family crashes at its own call site.
 *
 * **Every function here answers ONE question: "what does this register hold, read at width W?"** -- and
 * "not at that width" is a legitimate answer, spelled null. An earlier revision of this KDoc said the object
 * answered *two* questions, a by-value arithmetic conversion ([JLS] §5.1.3 narrowing) alongside a bit-reading
 * one, and kept [toInt] on the first while [toProvableInt] took the second. That split was a **scope
 * boundary**, not a contract: Plan 7 could only afford to fix the four array readers, so it left the ten
 * arithmetic ones reading the narrowing answer and said so in as many words. Plan 8 Task 3 removed the
 * boundary, and the measurement that decided it is that **no consumer of the narrowing answer exists** --
 * every one of the fourteen call sites across `BinaryMathOp`, `UnaryMathOp` and `CmpOp` wants the register
 * read, exactly as the five array sites do. So the rule lives in [toInt], once, and [toProvableInt] and
 * [toArrayIndex] are the names its callers use for it.
 *
 * **What "read at width W" refuses, and the boundary is narrow.** Only a `Float` or `Double` raw, and only at
 * the integral widths ([toInt], and [toLong] through it). Dalvik's 32-bit register holds
 * `boolean`/`byte`/`char`/`short`/`int` **identically** -- one domain, and reading them as `Int` is what the
 * runtime does -- while `Long` read at 32 bits *is* bit truncation, which `.toInt()` already performs. An
 * IEEE-754 value is the one case where the host box's numeric value and the register's bits are different
 * numbers.
 *
 * **Round 1 of Plan 8 Task 3's review reversed the reason that difference is resolved by refusing.** The
 * engine CAN read a register's bits -- [java.lang.Float.floatToRawIntBits] and friends are pure JDK calls on
 * a raw this object holds in full -- and [toFloat]/[toDouble] now do exactly that (see their KDocs, and the
 * measurements there). What survives is the *availability* rule: a read refuses only when the engine does not
 * hold all W of the bits, which happens for a 64-bit read of a narrow raw.
 *
 * **[toInt]/[toLong] still REFUSE a floating-point raw rather than reinterpreting it, and that is now a known
 * PRECISION LOSS rather than the only sound answer** -- a deliberate, priced, separable follow-up rather than
 * a claim that no answer exists. [toInt]'s KDoc carries the price and the one thing that blocks it.
 */
internal object Coerce {
    /**
     * A register's raw read as a 32-bit `Int`, or null when those bits cannot be read that way.
     *
     * **The `Float`/`Double` row is the whole rule and it is a soundness fix**, landed by Plan 8 Task 3 for
     * the arithmetic readers after Plan 7's closeout landed it for the array ones. [JLS] §5.1.3's narrowing
     * conversion -- round toward zero, saturate at `Int` bounds, `NaN -> 0` -- is a conversion of the
     * mathematical *value*, while a device reads the register's *bits*: `5.9f` is `0x40BCCCCD`, i.e.
     * 1086115021, and `-0.5f` is `0xBF000000`, i.e. -1090519040. Narrowing answered `5` and `0`. The two are
     * unrelated, and the engine's answer was a **confident `Known`**, which is what constant propagation
     * folds into rewritten bytecode. Refusing invents nothing, and **spec §2.2** makes it [Value.Unknown] at
     * the call site.
     *
     * **THE REFUSAL IS A PRECISION LOSS, NOT THE ONLY SOUND ANSWER, and an earlier revision of this KDoc
     * claimed otherwise.** It said over-refusal "cannot arise here because there is no bit-exact answer to
     * give up". That is **false**: `floatToRawIntBits(5.9f)` is exactly 1086115021, which is exactly what a
     * device reads, so the answer IS available and refusing gives up a fold this engine could make. The
     * refusal is the safe direction and it is what ships today; it is not the precise one. Over-refusal is
     * how a deobfuscator stops being useful, so the honest statement is: this row trades a wrong constant for
     * no constant, where a third option (the right constant) exists and is not yet taken.
     *
     * **Why not taken yet, priced.** Measured on top of the round-1 [toFloat]/[toDouble] fix: making this
     * function reinterpret (`is Float -> floatToRawIntBits(raw)`, `is Double ->
     * doubleToRawLongBits(raw).toInt()`) plus giving [toLong] the matching `Double` row kills **13** tests,
     * with **zero** migrated expectations moved and the differential agreement sweep still green.
     *
     * **But "all 13 written by Task 3" was FALSE and it understated the scope -- dated with `git log -S`:**
     * 8 of the 13 PREDATE Task 3. Five are Plan 7's shipped array pins (`f477bed`, `9fd6beb`:
     * `AGetOpTest`/`APutOpTest`'s index pins, `APutOpTest`'s value pin, `NewArrayOpTest`'s length pin,
     * `FilledNewArrayOpTest`'s element pin), one is Plan 7's `CoerceTest` boundary pin, one is Plan 4's
     * divisor-fork pin (`142c4fb`) and one is the pre-existing registry test. **So the extension would also
     * reverse Plan 7's array fix**, because [toProvableInt] delegates here -- a decision about another plan's
     * soundness work rather than a cleanup of this one's, which strengthens the case for not taking it
     * unilaterally. (The same defect class as the "156" figure: a count asserted as a measurement.)
     *
     * What blocks it outright is one measured unsoundness, in [toLong] rather than here: the fully symmetric rule refuses a 64-bit read of
     * a NARROW raw, and `BinaryMathOp`'s `J` domain reads the shift DISTANCE of `shl-long`/`shr-long`/
     * `ushr-long` through [toLong] even though Dalvik reads it as a 32-bit int -- `BinaryMathOp.longResult`'s
     * own comment says "Long shifts take an int distance". So that rule turns `binary-shl-long`'s
     * `Known(8589934592)` into `Unknown(J)` and breaks `newCoreAgreesWithOracleOnEverySupportedFixture` on
     * **verifier-VALID** input: over-refusal, measured, on the one fixture that exercises it. Closing this
     * needs `BinaryMathOp` to read that operand at its own width first, which is a separate change.
     *
     * **And the registry consequence, so whoever takes it is not surprised.** Under the reinterpreting rule
     * this function answers a *right* constant where the oracle answers a wrong one, so
     * `unary-int-to-float-computed-float-raw` stops being a
     * `DivergenceShape.NEW_CORE_MORE_SOUND` (oracle-KNOWN/core-UNKNOWN) and becomes a value *correction*,
     * which needs the field that shape's KDoc says D9 also needs. The shape itself stays witnessed: a
     * 64-bit read of a narrow raw is a genuine refusal and `neg-double` on `Known(5, "I")` is **already
     * registered** as `unary-neg-double-narrow-raw` (fix round 2), so the shape keeps its witness
     * automatically and nothing needs re-pointing.
     *
     * At two of the array sites the narrowed answer did something worse than invent a constant -- it *dropped
     * a feasible path*: a `new-array` length of `Known(-0.5f)` narrowed to `0`, so `length < 0` was false and
     * the op allocated and continued where a device throws `NegativeArraySizeException`; and
     * `BinaryMathOp.knownDivisor` answered "definitely zero" for every divisor with `0 < |x| < 1`, emitting
     * `[Throw]` alone on a run still reporting `Complete`. The arithmetic *value* readers are not that class
     * -- see the boundary note below -- but they share the cause.
     *
     * **The raw's RUNTIME type is what is tested, and this function cannot make the other mistake**: it is
     * handed a raw and never sees a register's declared tag, so it cannot misfire on the sanctioned
     * `const-wide` quirk (`core/Op.kt`'s `buildConst` ships `Value.Known(Long, "D")`, a `D`-tagged raw whose
     * bits `Coerce` and Dalvik agree about). Keyed to the tag, an integral read of that register would turn a
     * definite answer into a fork -- a precision loss on ordinary input. Pinned by
     * `CoerceTest.aWideRawUnderAFloatingPointTagStillCoercesBecauseTheRuntimeTypeIsWhatDecides` and, at the op
     * level, by `core/op/BinaryMathOpTest.aWideDivisorWhoseLowThirtyTwoBitsAreZeroThrows`'s fork-ARITY
     * assertion.
     *
     * **Which side of the soundness line each affected read sits on**, because over-refusal is the equally
     * real mirror defect: `neg-int`, `not-int`, every `int-to-*`, both integral `BinaryMathOp` domains and
     * `cmp-long` all have exactly ONE successor whatever they compute, so refusing explores neither more nor
     * fewer paths -- it trades a wrong constant for no constant, and (see above) for a right constant it
     * could have had. Only `BinaryMathOp.knownDivisor` is a *path* question, and there the refusal **adds** a
     * transition, which is unambiguously the sound direction.
     *
     * **Everything else [toInt] accepts is exact**, because for those the answer IS the register's low 32
     * bits: [JLS] §4.2's integral types plus `Boolean` (Dalvik genuinely represents a boolean as an integer
     * literal in a register) plus `Char`. Dalvik's tags are ambiguous, so all of them really do arrive
     * (`core/op/AGetOpTest.canGetWithShortIndex`, `core/op/APutOpTest.canPutIntegerWithShortIndex`,
     * `core/op/NewArrayOpTest.canCreateIntegerArrayWithShortTypeLengthValue`,
     * `core/op/FilledNewArrayOpTest.testShortParametersCreatesArrayWithExpectedContents`,
     * `core/op/BinaryMathOpTest.canAddByteAndChar`).
     *
     * **Tiering.** No verified dex can put a `Float`/`Double` raw in a register any of these reads consumes
     * (**VERIFIER** -- [CON] B1's general *"arguments... must always match the instruction"*, sharpened for
     * the array index and stored-value cases by [ART] `VerifyAGet`/`VerifyAPut` and for the element case by
     * [ART] `VerifyFilledNewArray`; deliberately no [ART] symbol is claimed for `new-array`'s length, because
     * there is no `VerifyNewArray` in ART -- `FilledNewArrayOp`'s KDoc records the word-boundary grep). So
     * there is no behavior to match and this is the engine's choice; **spec §2.2** then decides it. Reachable
     * with this build's opcode set regardless: `int-to-float` or `div-float` leaves a computed `Float` raw in
     * a register and any `int-*`/`long-*` op reading it arrives here.
     *
     * **This is NOT the boundary [isDalvikNull] draws, and an earlier revision said it was** (found while
     * executing Plan 7 Task 12, which consumes `isDalvikNull` for `throw`). The two differ on exactly one
     * type: this function admits `Boolean` and [isDalvikNull] **deliberately excludes** it -- its own KDoc
     * says so and `ValueTest.everyIntegralZeroIsDalviksNullAndNothingElseIs` pins the exclusion in both
     * directions. They answer different questions: this one asks "can these bits be read as an int", where a
     * `Z`-tagged register genuinely can be; [isDalvikNull] asks whether [BC]'s `(Object) null == (int) 0`
     * licenses treating the bits as a null REFERENCE, and no `const` opcode produces a `Z`-tagged zero for it
     * to apply to. Reading the sentence the other way would make `throw` of a `Z`-tagged `false` look like a
     * definite `NullPointerException`, which it is not.
     *
     * **RESIDUAL, inherited rather than introduced:** `Float`/`Double` are enumerated BY NAME, so the rule is
     * total only for the raws this build can produce. Any future non-bit-truncating `Number` reopens it
     * (`BigDecimal("0.5").toInt()` is `0` too), and Plan 9's host-method returns are the first thing that can
     * park an arbitrary one in a register.
     *
     * Pinned by `CoerceTest.aFloatingPointRawIsNotProvableAtAnyIntegralWidth` and
     * `aFloatingPointRawIsRefusedAtBothIntegralWidths`, plus one op-level test per family, which is the half
     * that matters: **measured, a guard here with no op-level test kills only `CoerceTest` pins**, so nothing
     * would catch the change in either direction. `core/op/UnaryMathOpTest`'s
     * `anIntToFloatReadingAComputedFloatRawIsNotProvableRatherThanTruncatedByValue` and
     * `aLongToIntReadingAComputedDoubleRawIsNotProvableRatherThanTruncatedByValue`;
     * `core/op/BinaryMathOpTest`'s `anIntDomainOperatorReadingAComputedFloatRawIsNotProvable` and
     * `aLongDomainOperatorReadingAComputedFloatRawIsNotProvable`;
     * `core/op/CmpOpTest.aCmpLongReadingAComputedFloatRawIsNotProvable`; and for the path half,
     * `core/op/BinaryMathOpTest`'s `anIntDivideReadingAComputedFloatDivisorStillExploresTheNormalPath` and
     * `aLongDivideReadingAComputedDoubleDivisorStillExploresTheNormalPath`.
     *
     * Registered as an intended divergence -- `unary-int-to-float-computed-float-raw`,
     * `DivergenceShape.NEW_CORE_MORE_SOUND` -- because **no differential could have found this**: the oracle
     * narrows identically, through `Value.toInteger` -> `Utils.getIntegerValue` -> `castToPrimitive` ->
     * `intValue()` (`org/cf/util/Utils.java`'s `castToPrimitive`), so before this fix both engines agreed
     * while both were wrong.
     */
    fun toInt(raw: Any?): Int? = when (raw) {
        // FIRST, and before `is Number`, which both of these satisfy.
        is Float, is Double -> null
        is Number -> raw.toInt()
        is Boolean -> if (raw) 1 else 0
        is Char -> raw.code
        else -> null
    }

    /**
     * A register's raw as an `Int` **when the register's own bits provably read as that Int**, under the name
     * the array ops use for that question -- a one-line delegation to [toInt], which now IS the rule. Five
     * call sites: [toArrayIndex] (an index, for `AGetOp` and `APutOp`), `NewArrayOp`'s length, `APutOp`'s
     * int-like stored value, and `FilledNewArrayOp`'s `I`-typed elements.
     *
     * **It used to carry the `Float`/`Double` row itself, and Plan 8 Task 3 moved that row into [toInt].**
     * The row was here rather than there because Plan 7 could only afford to fix the array readers, so it
     * fixed them and left the ten arithmetic readers reading [toInt]'s narrowing answer -- a scope boundary,
     * stated as one in this KDoc's previous revision. Task 3 removed the boundary, at which point two
     * functions with identical behavior would have been two spellings of one question, which is the exact
     * divergence Plan 7's closeout found across four call sites and lifted into one function to stop.
     *
     * Kept as a named question rather than deleted, on the same reasoning [toArrayIndex] is kept: the array
     * ops' own prose says "not provable", and a delegation whose name states the claim is what makes the five
     * call sites readable. **Do not re-add a `Float`/`Double` row here** -- one rule, one place, and a second
     * copy is what let the sibling sites diverge in the first place.
     *
     * The boundary, its tiering, the wrong answers it removes and the soundness-direction argument all live
     * in [toInt]. Pinned by `CoerceTest.aFloatingPointRawIsNotProvableAtAnyIntegralWidth` plus one test per
     * site: `core/op/AGetOpTest.aFloatIndexIsNotProvableRatherThanTruncatedByValue`, its `APutOpTest` twin,
     * `core/op/APutOpTest.aFloatValueIsNotProvableRatherThanTruncatedByValue`,
     * `core/op/NewArrayOpTest.aFloatLengthIsNotProvableRatherThanTruncatedToZero` and
     * `core/op/FilledNewArrayOpTest.aFloatElementIsNotProvableRatherThanTruncatedByValue`.
     */
    fun toProvableInt(raw: Any?): Int? = toInt(raw)

    /**
     * An **array index**'s raw as an `Int`, or null for "not provable as an index" -- the single
     * implementation `AGetOp` and `APutOp` both call, so the two families cannot answer it differently.
     *
     * It is [toProvableInt] under the index's own name, and the boundary, its tiering and the whole argument
     * live in [toInt]. Kept as a named question because both ops' prose names it and because "not provable as
     * an index" is the claim their rows make; **do not re-add a `Float`/`Double` row here** -- a second copy
     * of one rule is what let the three sibling sites diverge in the first place.
     *
     * Pinned by `core/op/AGetOpTest.aFloatIndexIsNotProvableRatherThanTruncatedByValue` and its `APutOpTest`
     * twin.
     */
    fun toArrayIndex(raw: Any?): Int? = toProvableInt(raw)

    /**
     * A register pair's raw read as a 64-bit `Long`, or null when those bits cannot be read that way.
     *
     * **The gate on [toInt] is load-bearing twice over and must not be "simplified".** It rejects a
     * non-numeric raw, AND it inherits [toInt]'s `Float`/`Double` refusal -- so the 64-bit read gets the
     * 32-bit read's provability rule without a second copy of it. The gate's numeric *value* is discarded for
     * a `Number` raw, which makes replacing it with a bare coercibility test (`raw is Number || raw is
     * Boolean || raw is Char`) look like a tidy-up; that edit would silently un-guard the whole `J` domain of
     * `BinaryMathOp` and `UnaryMathOp` plus `cmp-long` -- eight of Task 3's fourteen read sites, and the ones
     * no review or carry-forward entry had ever named. Pinned by
     * `CoerceTest.aFloatingPointRawIsRefusedAtBothIntegralWidths`.
     *
     * **What it does NOT model, stated so it is not mistaken for a guarantee:** a *narrow* raw read at 64
     * bits. Dalvik reads the register pair, so the high half is a register this call has never seen; the
     * engine's one-`Value`-per-wide-pair representation cannot express that at all, so a `Boolean`/`Char`/
     * `Int` raw here widens by value. That is the pre-existing representation, not a Task 3 decision, and it
     * is what `CoerceTest.charactersCoerceToTheirCodePoint` pins.
     */
    fun toLong(raw: Any?): Long? = toInt(raw)?.let { narrow ->
        // A Long must not round-trip through Int, which would truncate it. (A Double never reaches here:
        // the gate above refuses it -- see this function's KDoc.)
        when (raw) {
            is Number -> raw.toLong()
            else -> narrow.toLong()
        }
    }

    /**
     * A register's raw read as a 32-bit `float`: the register's **bits**, reinterpreted, or null when those
     * bits are not all available.
     *
     * **This REVERSES a design decision, and it reverses it because both of that decision's premises were
     * measured false.** `CARRY-FORWARD.md`'s cross-domain-raw-read entry settled *"answer `Unknown`; do not
     * bit-reinterpret"* on two grounds, and neither survives:
     *  1. *"It would reopen all 156 arithmetic expectations Plan 4 migrated."* **Measured: ZERO.** Making
     *     this read bit-exact kills exactly three tests, all of them `CoerceTest` rows that spelled out the
     *     by-value table; no op-level test and no migrated expectation moves. The 156 tests seed a `Float`
     *     raw for a float-domain op and a `Double` raw for a double-domain op, which is the same-domain case
     *     this function passes through untouched.
     *  2. *"There is no fidelity target, because these reads only arise in bytecode a verifier rejects."*
     *     **False here, and that is what makes this the more severe half.** `const v0, 0x3f800000` is how a
     *     float literal reaches a register -- `core/Op.kt`'s `buildConst` stores it as
     *     `Value.Known(1065353216, "I")` -- so `add-float v1, v0, v0` on ORDINARY COMPILER OUTPUT arrives
     *     here with an integral raw. Reading it by value answered `2.1307064E9`; a device answers `2.0f`.
     *     That is a confident wrong constant on verifier-VALID dex, which outranks the VERIFIER-tier half
     *     [toInt] guards.
     *
     * A third premise was also offered and is simply not true: *"the core has no bit view of a register."*
     * [java.lang.Float.intBitsToFloat] is a pure JDK call on a raw this object is already handed in full.
     *
     * **The rule, stated once for all four readers.** A read at width W answers the register's W bits
     * reinterpreted in W's domain, and answers null only when the engine does not hold all W bits. The bits
     * a raw supplies are exact and total: an integral raw supplies its own two's-complement bits (32 for
     * `Int`/`Short`/`Byte`/`Char`/`Boolean`, 64 for `Long`), a `Float` supplies
     * [java.lang.Float.floatToRawIntBits], a `Double` supplies [java.lang.Double.doubleToRawLongBits]. So
     * every raw this build produces has determinable **low 32** bits, which is why this function refuses
     * nothing except a non-numeric raw.
     *
     * **`is Float -> raw` is the same-domain pass-through, not an exception to the rule**:
     * `intBitsToFloat(floatToRawIntBits(x))` is `x` for every `x`, NaN payloads included (that is what the
     * `Raw` variants guarantee), so the row is the rule written without a round trip.
     *
     * **The conversion opcodes are unaffected and that is the point.** `float-to-int` READS at float width
     * (here) and then CONVERTS by value in `UnaryMathOp.convertFromFloat` -- [JLS] §5.1.3 narrowing is
     * genuinely what `float-to-int` does to the value it read. Read by bits, convert by value; the two are
     * different steps and only the first one changed. `int-to-float` never reaches this function at all.
     *
     * Pinned by `CoerceTest.aRegistersBitsAreWhatAFloatWidthReadAnswers` plus one op-level test per family:
     * `core/op/BinaryMathOpTest.anAddFloatOnConstLoadedLiteralBitsAnswersWhatADeviceAnswers`,
     * `core/op/UnaryMathOpTest.aFloatToIntOnConstLoadedLiteralBitsReadsTheBitsThenConverts`, and
     * `core/op/CmpOpTest.aCmpDoubleOnConstWideLiteralBitsOrdersTheDoublesNotTheirBitPatterns` for the
     * 64-bit sibling.
     */
    fun toFloat(raw: Any?): Float? = when (raw) {
        // Same-domain pass-through; equal to reinterpreting its own raw bits, round trip omitted.
        is Float -> raw
        // A wide raw read at NARROW width: the low half of the pair, which is what a 32-bit read sees.
        is Double -> java.lang.Float.intBitsToFloat(java.lang.Double.doubleToRawLongBits(raw).toInt())
        // Every integral raw, through the one function that answers "these 32 bits as an int".
        else -> toInt(raw)?.let { java.lang.Float.intBitsToFloat(it) }
    }

    /**
     * A register pair's raw read as a 64-bit `double`: the pair's **bits**, reinterpreted, or null when the
     * engine does not hold all 64.
     *
     * The 64-bit sibling of [toFloat], reversing the same decision for the same measured reasons, and with
     * the same verifier-VALID witness: `const-wide v0, 0x4000000000000000` stores
     * `Value.Known(4611686018427387904L, "D")` (`core/Op.kt`'s `buildConst` -- `const-wide` is the one
     * opcode that gives a `D` tag to a `Long` raw, because it serves `long` and `double` both), so
     * `add-double v0, v0, v2` on ordinary compiler output arrives with a `Long` raw. Read by value it
     * answered `9.223372036854776E18`; a device answers `4.0`. `neg-double` on the same register answered
     * `-4.611686018427388E18` where a device answers `-2.0`.
     *
     * **This is also the row that makes the `Known(Long, "D")` quirk a FEATURE rather than something to be
     * tolerated.** The quirk exists because `const-wide` cannot say which type it loaded; reading the raw's
     * bits is exactly the read that does not need to know.
     *
     * **A NARROW raw answers null, and that is a genuine "not provable" rather than an over-refusal.** A
     * 64-bit read needs 64 bits; an `Int`/`Char`/`Boolean` raw supplies 32 and a `Float` raw supplies 32,
     * and the other 32 live in `register + 1` -- a register this call has never seen and which the op did
     * not even declare as read. The engine's one-`Value`-per-wide-pair representation cannot express the
     * question at this layer, so answering anything at all would invent bits. By value it answered `-5.0`
     * for `neg-double` on `Known(5, "I")`, which is a number no device produces. Cost of the refusal,
     * measured: two `CoerceTest` rows (`toDouble(5)`, `toDouble(true)`) and nothing op-level; witnessed by
     * `core/op/UnaryMathOpTest.aNegDoubleReadingANarrowRawIsNotProvableBecauseTheHighHalfIsAnotherRegister`.
     *
     * **RESIDUAL, and it is an OVER-refusal rather than [toInt]'s under-refusal -- the mirror of that
     * function's by-name note.** The accepted set is enumerated by name (`is Double`, `is Long`), so this
     * refuses every other `Number` including one that supplies all 64 bits: a hypothetical 64-bit box, or
     * anything a future host-method return parks in a register (Plan 9 is the first thing that can). For
     * such a raw the bits ARE available and this would still answer null, which throws away a determinable
     * fold -- the direction spec principle 2a names. Total only for the raws this build produces, exactly
     * like [toInt]'s residual, and in the opposite direction: [toInt] would *invent* for a future
     * non-bit-truncating `Number`, this would *refuse* for a future wide one.
     *
     * Pinned by `CoerceTest.aRegisterPairsBitsAreWhatADoubleWidthReadAnswers` plus
     * `core/op/BinaryMathOpTest.anAddDoubleOnConstWideLiteralBitsAnswersWhatADeviceAnswers` and
     * `core/op/UnaryMathOpTest.aNegDoubleOnConstWideLiteralBitsAnswersWhatADeviceAnswers`. The divergence it
     * creates against the oracle is registered as `unary-neg-double-narrow-raw`
     * (`DivergenceShape.NEW_CORE_MORE_SOUND`), not left silent.
     */
    fun toDouble(raw: Any?): Double? = when (raw) {
        is Double -> raw
        is Long -> java.lang.Double.longBitsToDouble(raw)
        // 32 of the 64 bits live in `register + 1`, which this call cannot see. See the KDoc.
        else -> null
    }

    /**
     * The value a store of [value] into a slot of [type] holds. A `Z`/`B`/`C`/`S` slot keeps the low 8 or 16
     * bits of the 32-bit register, as ART's field stores do (`GetFieldValue` casts the vreg with
     * `static_cast<uint8_t>`, `<int8_t>`, `<uint16_t>` and `<int16_t>`); any other slot stores [value]
     * unchanged. A narrow store answers `Unknown(type)` when the register has no 32-bit reading, and for a
     * boolean whose low byte is neither 0 nor 1, which a device keeps but a `Boolean` raw cannot hold.
     */
    fun narrowStore(value: Value, type: VirtualType): Value {
        val narrow = type.descriptor.singleOrNull()?.takeIf { it in "ZBCS" } ?: return value
        val bits = (value as? Value.Known)?.let { toInt(it.raw) } ?: return Value.Unknown(type)
        val raw: Any = when (narrow) {
            'Z' -> when (bits and 0xff) {
                0 -> false
                1 -> true
                else -> return Value.Unknown(type)
            }
            'B' -> bits.toByte()
            'C' -> bits.toChar()
            else -> bits.toShort()
        }
        return Value.Known(raw, type)
    }
}
