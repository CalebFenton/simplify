package org.cf.smalivm.core

// The provability helpers, in ONE place because they are ONE rule asked three ways. All three now live
// here: [provableInt] ("can these 32 bits be read as an Int?"), [sameReference] ("are these two
// registers the same reference?") and [castOutcome] ("is the value in this register an instance of C?"),
// which Plan 8's Tasks 8 and 9 both consume so they cannot disagree about one value shape. They live
// together rather than inside their ops because an earlier revision of this plan asked the provability
// question four times in four tasks and the four were mutually inconsistent in three places.

/**
 * What the engine can prove about the **32 bits** a comparison reads out of a register.
 *
 * **The rule, stated once for every caller (spec §2.2).** A question about a register is provable
 * exactly when every bit the question reads is determined by the value the lattice holds. Otherwise it
 * is unprovable, and an unprovable *successor set* explores **more** (never fewer) while an unprovable
 * *value* answers [Value.Unknown]. Neither direction licenses the other: "unprovable" never means
 * "pick one", and "provable" never means "fork anyway".
 *
 * **Three answers, not two, and the third one is why this is a sealed type instead of an `Int?`.** A
 * live pointer is provably **non-zero** without being any particular `Int`, so it can decide a
 * zero-comparison and nothing else. The plan's Global Constraints spelled this helper
 * `provableInt(value): Int?` while *also* requiring that row, and the two cannot both hold -- a
 * nullable `Int` has no way to say "non-zero but unknown" except by inventing a sentinel value, which
 * is exactly the confident-wrong-constant failure the rule exists to prevent. So the rule is unchanged
 * and there is still exactly ONE implementation of it; only the return type is wider than the plan's
 * sketch. Recorded here rather than silently, because a later reader comparing the two will otherwise
 * think a row was dropped.
 *
 * Making it a sealed type also does the work a KDoc cannot: a caller has to write a branch for
 * [NonZero], so "I forgot that a reference is not an integer" is a compile error rather than a wrong
 * answer (spec: type-enforce a soundness rule rather than documenting it).
 */
internal sealed interface ProvableInt {
    /** These 32 bits are exactly [bits], read signed -- which is how Dalvik compares them. */
    data class Exactly(val bits: Int) : ProvableInt

    /**
     * A live pointer: provably **not** zero, and provably not any *particular* `Int`.
     *
     * **SPEC**, and it is the whole licence for `if-eqz` doubling as a null test -- [BC], "General
     * design": *"In terms of bitwise representation, `(Object) null == (int) 0`."* A non-null reference
     * is therefore never bitwise zero, so `if-nez vRef` on a definitely-allocated reference is
     * provably **true** and `if-eqz vRef` provably **false**. Forking those would invent an
     * **infeasible** path, which is the mirror of the unsoundness this file's rule is mostly about and
     * is graded equally (spec §2.2).
     */
    data object NonZero : ProvableInt

    /** Not determined by what the lattice holds: [Value.Unknown], [Value.Top], or a `Float`/`Double` raw. */
    data object Unprovable : ProvableInt
}

/**
 * The one implementation of "what are this register's low 32 bits?", built on [Coerce.toProvableInt]
 * -- which already answers it for a raw -- plus the [Value]-level rows the lattice adds.
 *
 * **The row order is load-bearing and each row states which one it must beat.** Getting it wrong
 * produced a defect in *each* soundness direction while this plan was being written, so:
 *
 *  1. A reference-tagged `Known(null)` is `Exactly(0)`; every other reference-tagged known value is
 *     [ProvableInt.NonZero]. This guard precedes raw-number coercion, so boxed numbers stay pointers.
 *  2. `Known(HeapRef, T)` -> [ProvableInt.NonZero]. A tracked object's identity is a heap id, so
 *     [Coerce] would refuse the raw and the `else` row below would answer [ProvableInt.Unprovable] --
 *     which would fork `if-nez` on a definitely-allocated array. That is the invents-a-live-branch
 *     defect, and `core/op/IfOpTest.testIfPrimitiveArrayNotEqualZero` is the migrated test that catches it.
 *  3. `Float`/`Double` raw -> [ProvableInt.Unprovable], and it MUST precede row 5. `Coerce.toProvableInt`
 *     refuses these (Plan 8 Task 3: a device reads the register's *bits*, `1.0f` is `0x3F800000`, and
 *     reading the host box's numeric *value* answered `1`), so without this row a refused `Float` would
 *     fall into row 5 and be reported as a live pointer -- an integral raw laundered into a reference.
 *  4. any raw `Coerce.toProvableInt` accepts -> `Exactly(that)`. One domain: Dalvik's 32-bit register
 *     holds `boolean`/`byte`/`char`/`short`/`int` identically, and a `Long` read at 32 bits is bit
 *     truncation, which `.toInt()` performs.
 *  5. any other host object -> [ProvableInt.NonZero]. A live pointer that is not tracked (an interned
 *     `String`, a host `Throwable`, a `java.lang.Class`).
 *  6. [Value.Unknown] / [Value.Top] -> [ProvableInt.Unprovable].
 *
 * **Deliberately NOT [Value.readsAsNullReference]**, which answers a different question and would be
 * wrong here: its second disjunct makes `Known(5, "I")` read as a null *reference*, which is right for
 * `check-cast` (ART's `ShadowFrame` clears the reference half on every primitive write, and where that tier
 * and nterp disagree the engine chooses the null read anyway -- see that predicate's KDoc for the tier split)
 * and wrong for `if-eqz`, whose 32 bits are `5`. Two questions, two predicates. **Note that nterp's raw read
 * is the `if-eqz` answer, not the `check-cast` one** -- which is precisely why these must stay two predicates
 * and not be unified on the strength of "one interpreter reads it that way".
 */
internal fun provableInt(value: Value): ProvableInt = when (value) {
    is Value.Unknown, Value.Top -> ProvableInt.Unprovable
    is Value.Known -> if (value.type.isReference) {
        if (value.raw == null) ProvableInt.Exactly(0) else ProvableInt.NonZero
    } else when (val raw = value.raw) {
        null -> ProvableInt.Exactly(0)
        is HeapRef -> ProvableInt.NonZero
        // BEFORE the Coerce row, which refuses these, and before the host-object row, which would
        // otherwise read a refused Float as a pointer. See row 3 of this function's KDoc.
        is Float, is Double -> ProvableInt.Unprovable
        else -> Coerce.toProvableInt(raw)?.let { ProvableInt.Exactly(it) } ?: ProvableInt.NonZero
    }
}

/**
 * "Are these two registers the same reference?" -- pointer identity, which is genuinely what `if-eq`
 * asks at the machine level.
 *
 * Null means "at least one side is not a definite value", i.e. the question cannot be answered.
 *
 * **Both spellings of identity, in one helper, because this engine has two representations of one
 * thing.** A VM-tracked mutable's raw is a [HeapRef] (spec §4.2), so identity for it is heap-id
 * equality; everything else keeps a host object as its raw, so identity for those is host `===`. A
 * [HeapRef] never equals a non-[HeapRef] raw: they cannot be the same guest object, which is exactly
 * what `core/op/IfOpTest.testIfNotEqualWithStringAndArrayReferenceIsTrue` (a `String` against an `int[]`)
 * depends on.
 *
 * **The declared tag is ignored entirely.** Task 9 re-tags a register on a successful `check-cast`, and
 * [ART] *replaces* rather than intersects the tag, so `new-array v0; move-object v1, v0; check-cast v1,
 * Ljava/lang/Object;; if-eq v0, v1` must still see two identical pointers as equal. A tag comparison
 * would answer "unequal".
 *
 * **NOT [Value.agreesWith], and the difference is a soundness bug in each direction.** That function
 * answers *semantic agreement for consensus*, a different concept with a different owner (spec §2
 * principle 4):
 *  - it falls through to host `==` rather than `===`, so two equal-content non-identical `String`s
 *    agree -- which makes `if-ne` answer false, leaves the taken branch unexplored, and reaches the
 *    join address without the branch address on a run reporting `Complete`. That is the worst outcome
 *    this engine produces, and `core/op/IfOpTest.testIfNotEqualWithTwoDifferentStringReferencesIsTrue` is the
 *    migrated test that catches it (its own comment says it builds the two strings deliberately);
 *  - its `type != other.type` guard is tag-sensitive, which is the `check-cast` case above.
 *
 * Note for the record that spec §2 principle 6's *"value equality never rides on mutable host
 * identity"* is about **consensus** and does not forbid `===` here: `if-eq` really is a pointer
 * comparison, and the heap-id row is what keeps a *tracked* mutable off host identity (the audit's
 * cloned-array-diverges defect, `docs/architecture/state-model.md:138`).
 *
 * **CALLERS MUST GATE THIS ON BOTH OPERANDS BEING PROVABLY LIVE POINTERS**
 * ([ProvableInt.NonZero]), which is what [org.cf.smalivm.core.op.IfOp] does. This function is a pure
 * identity test and cannot tell a pointer from a boxed primitive, so ungated it answers `false` for
 * `Known(1.0f, "F")` against `Known(1.0f, "F")` -- two distinct host boxes of one value -- and a caller
 * reading that as "definitely unequal" would fold a confident wrong answer for exactly the input Task 3
 * proved unprovable. Ungated it would also answer `false` for `Known(1000, "I")` against
 * `Known(1000, "I")`, since `Integer.valueOf` only caches -128..127. The gate is pinned by
 * `core/op/IfOpTest.aFloatRawIsNotAPointerSoIdentityNeverDecidesIt`.
 */
internal fun sameReference(a: Value, b: Value): Boolean? {
    if (a !is Value.Known || b !is Value.Known) return null
    val x = a.raw
    val y = b.raw
    return when {
        // A tracked object's identity is its heap id; a HeapRef and a host object are never one object.
        x is HeapRef || y is HeapRef -> x is HeapRef && y is HeapRef && x.id == y.id
        else -> x === y
    }
}

/**
 * "Is a value of this type an instance of that type?" -- the narrow capability [castOutcome] needs, so
 * `core/op/` can ask it without naming [ClassIndex] and therefore without a path to the oracle's type
 * model (the two-tier dependency rule).
 *
 * A THIRD `fun interface` rather than a widening of [TypeHierarchy], for the reason that one records in
 * its own KDoc and Task 7 measured: `TypeHierarchy` is a `fun interface` whose SAM conversion the tests
 * use, so a second abstract method breaks every lambda fake, and *assignability of a thrown class to a
 * catch clause* is a different concept from *runtime instance-of over arrays and primitives*
 * (plan design decision 4). [ClassIndex] implements all three; `OpBuilder` passes the same object under
 * whichever capability names the question being asked.
 */
fun interface InstanceOfTest {
    /** See `ClassIndex.isInstanceOf` for the six rows and for the [ClassLoadFailure] it may raise. */
    fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean
}

/**
 * What the engine can prove about *"is the value in this register an instance of `C`?"* -- the question
 * `instance-of` answers directly and `check-cast` answers with the null case inverted.
 *
 * **FIVE cases, not the three the plan's Global Constraints sketched, and each extra one is a soundness
 * rule made into a compile error.** The sketch was `Definitely / Never / Unprovable`.
 *
 * The **fourth** ([ReadsAsNull]) exists because a register that reads as a null reference cannot be folded
 * into any of the three: null gives the two consumers *opposite* answers -- `instance-of` stores `0`, while
 * a cast **succeeds**, since null passes any cast. Collapsed into `Never`, a `check-cast` that forgot its
 * own null row would synthesize a definite `ClassCastException` for `Known(5, "I")`: an invented infeasible
 * throw, which is verbatim the whole-branch Critical Plan 7 shipped in `filled-new-array`.
 *
 * The **fifth** ([NullOrInstance]) was added by Plan 8 Task 9, **which is where [Unprovable]'s own KDoc
 * sends it** (*"If Task 9 needs the two reasons told apart structurally, add a fifth case here rather than
 * re-deriving the walk locally"*). It exists because `Unprovable` was doing two jobs that this file's
 * second consumer has to tell apart: *"the value may be null and may be anything"* against *"the value is
 * null or an instance of the target, and nothing else"*. For `instance-of` both are `Unknown(Z)`; for
 * `check-cast` the second is a **provable success** (design decision 5), and collapsing them made
 * `check-cast v0, Ljava/lang/Object;` on an `Unknown` fork a cast that cannot fail -- the mirror defect,
 * graded equally with unsoundness (spec §2.2) and shipped twice in Plan 7.
 *
 * As separate cases, forgetting either is a non-exhaustive `when` and the compiler says no (spec:
 * type-enforce a soundness rule rather than documenting it -- the same widening [ProvableInt] made to the
 * same plan's other sketch, in this file, for the same reason).
 *
 * **How each op reads it. Written here, once, because the two ops' mappings differ substantively on TWO
 * rows -- [ReadsAsNull] and [NullOrInstance] -- and a Task 9 implementer who reads "they differ on the null
 * row" and stops will get the second one wrong.** On [Definitely] and [Never] the two ops reach the SAME
 * conclusion about what is provable and merely translate it into their own domain (a boolean versus a
 * re-tag or a throw). On the other two they reach **opposite** conclusions from the same outcome, for two
 * unrelated reasons:
 *  - [ReadsAsNull]: a null is an instance of nothing and passes every cast, so `0` versus a **success**.
 *    Grounded in `Value.readsAsNullReference`'s own argument, NOT in design decision 5.
 *  - [NullOrInstance]: **this is where design decision 5 lives, and it is its own case for exactly that
 *    reason.** A cast on an `Unknown(T)` whose `T` satisfies the target **provably succeeds**, because
 *    `Unknown(T)` holds null or a `T` or a subtype and all three pass; an instance test on the same value
 *    cannot answer, because null and a `T` give OPPOSITE booleans. Folding this into [Unprovable] --
 *    which is how Task 8 shipped it -- makes `check-cast` fork a cast that cannot fail unless it
 *    re-derives the type test locally, and re-deriving it is what [Unprovable]'s KDoc forbids.
 *
 * | outcome | `instance-of` | `check-cast` (Task 9) |
 * |---|---|---|
 * | [ReadsAsNull] | `Known(false, Z)` -- INHERITED, [JVMS] `instanceof`: *"If objectref is null ... pushes an int result of 0"* | **succeeds**; [ART] `interpreter_switch_impl-inl.h:830`'s `obj != nullptr` conjunct short-circuits before `!obj->InstanceOf(c)` **in the same expression**, so a null never reaches the cast test; resolution (`:820-825`) and its `nullptr` bail (`:826-828`) precede both, and that bail is a RESOLUTION failure rather than a null register. Matches `docs/DALVIK-SEMANTICS.md#check-cast-21c` |
 * | [Definitely] | `Known(true, Z)` | succeeds, register re-tagged |
 * | [Never] | `Known(false, Z)` | a **definite** `ClassCastException` |
 * | [NullOrInstance] | `Unknown(Z)` | **succeeds**, register re-tagged -- an `Unknown(T)` holds null or a `T` or a subtype of `T`, and all three pass a cast to a type `T` satisfies |
 * | [Unprovable] | `Unknown(Z)` | a **FORK**: `listOf(Continue, Throw(ClassCastException))` |
 *
 * The asymmetry in the [NullOrInstance] row is design decision 5 and it has the oracle's own source as
 * evidence: `CheckCastOp.isInstance` short-circuits on null *before* its type loop
 * (`opcode/CheckCastOp.kt:55-57`) because for a cast a null and a satisfying declared type agree, while for
 * `instance-of` they give `0` and `1`. That is why `docs/architecture/opcodes.md:173` files Correctness
 * Defect 18 against `InstanceOfOp` and files nothing of the kind against `CheckCastOp`'s
 * satisfying-unknown path.
 */
internal sealed interface CastOutcome {
    /**
     * The register reads as `null` when read **as a reference** -- see [Value.readsAsNullReference], which
     * is wider than "the raw is null". Its own case because the two consumers answer it in opposite
     * directions; see [CastOutcome]'s table.
     *
     * **This used to say the predicate "is [ART]'s actual rule", and that is two claims of unequal strength**
     * (corrected 2026-08-04). ART ships two interpreters that read a register differently, so the predicate's
     * rows split: a **null raw** and Dalvik's **integral zero** under a reference tag are the rule in **both**
     * tiers -- the switch interpreter's `GetVRegReference` yields `nullptr` and nterp's raw read finds zero
     * bits and branches the same way -- while a **non-zero primitive** (`Known(5, "I")`) is a row the two
     * tiers **contradict** each other on, and is therefore **VERIFIER** tier: this project's spec §2.2 choice
     * on input `runtime/verifier/method_verifier.cc:3156-3158` hard-fails. Nothing about this outcome or
     * either consumer's mapping changes; the wording did, because *"[ART]'s actual rule"* is not available
     * for a row on which ART's own tiers disagree. See [Value.readsAsNullReference]'s KDoc and
     * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers* subsection.
     */
    data object ReadsAsNull : CastOutcome

    /** Provably an instance: some type the value is known to have satisfies the target. */
    data object Definitely : CastOutcome

    /** Provably **not** an instance, and provably not null either -- the two are separate cases here. */
    data object Never : CastOutcome

    /**
     * The value is **null or an instance of the target, and nothing else**: it is not [Value.Known], so no
     * particular object is proved, but its declared tag provably satisfies the target and a
     * [Value.Unknown]`(T)` holds null, a `T`, or a subtype of `T` -- every one of which passes a cast.
     *
     * **A disjunction, not an ambiguity, and that is the whole distinction from [Unprovable].** For
     * `instance-of` the disjunction is genuinely undecided (null answers `0`, an instance answers `1`), so
     * it maps to `Unknown(Z)` exactly as [Unprovable] does. For `check-cast` **both disjuncts succeed**, so
     * it is provable: one transition, register re-tagged. Design decision 5, with the oracle's own source
     * as evidence -- see [CastOutcome]'s table.
     *
     * **Only reachable for a non-[Value.Known] value.** A `Known` whose tag satisfies is [Definitely]
     * (row 4 tests the tag first), and a `Known` that reads as null is [ReadsAsNull] (row 1).
     */
    data object NullOrInstance : CastOutcome

    /**
     * Neither, and for one of exactly three reasons: the value is [Value.Unknown] whose tag does **not**
     * satisfy the target (so it may be null, may be a satisfying subtype, and may be neither), it is
     * [Value.Top] (nothing known at all, not even a tag), or the class model could not answer
     * ([ClassLoadFailure]). All three are spec §2.2 ambiguity: an unprovable value answers [Value.Unknown]
     * and unprovable feasibility explores more.
     *
     * **The three reasons are deliberately NOT distinguished FURTHER, and `instance-of` is why that is safe:
     * all answer `Unknown(Z)`.** `check-cast` answers all three with the same **FORK**, so no test in this
     * build can witness a difference either. Two shapes that reach here are worth naming anyway, because an
     * implementation that tried to refine this row would break on them -- these were REQUIREMENTS on Task 9
     * and they are why [NullOrInstance] is computed *here* rather than in the op:
     *  - **A [Value.Known] that reached this row via [ClassLoadFailure] with nothing satisfying.**
     *    Re-consulting `isInstanceOf` re-enters the *same* call on the *same* candidate and **re-raises**.
     *    Doing that without a `catch` lets a [ClassLoadFailure] escape into `Aborted(HOST_FAILURE)` --
     *    indistinguishable from an engine bug, which is the pair `CARRY-FORWARD.md`'s Plan 9 item 5 exists
     *    to prevent. Pinned by
     *    `core/op/CheckCastOpTest.anUnanswerableClassWalkForksInsteadOfRaisingOrInventingAThrow`.
     *  - **[Value.Top].** Its `type` is `null`, so there is no tag to test at all and the answer is an
     *    unconditional FORK. Pinned by
     *    `core/op/CheckCastOpTest.aTopRegisterForksAndTheSuccessArmIsStillRefined`.
     *
     * **One known over-approximation, recorded rather than fixed:** a [Value.Unknown] whose *tag* is a
     * PRIMITIVE (`Frame.entryFrame` seeds `void f(int x)`'s parameter that way) reaches this row and forks,
     * where a device reads the register's reference half as `nullptr` and the cast succeeds -- the same
     * argument [Value.readsAsNullReference] makes for a `Known`, whose primitive clause requires `Known`.
     * Left alone deliberately, and the cost is provably nil rather than merely small: a `check-cast` whose
     * source register the verifier types as `int` is input a device **refuses to link**, from the shared
     * `CHECK_CAST`/`INSTANCE_OF` verifier case -- `runtime/verifier/method_verifier.cc:3156-3158`, verbatim:
     * *"`} else if (!orig_type.IsReferenceTypes()) { Fail(VERIFY_ERROR_BAD_CLASS_HARD) << opcode << " on
     * non-reference in v" << orig_type_reg; return false;`"* (the same function whose `:3122-3128` comment
     * and `:3165` write `core/op/CheckCastOp` cites for the re-tag). So there is no runtime behaviour to
     * match here at all; widening `readsAsNullReference` would change six other ops' behaviour; and a fork
     * is the over-approximating direction rather than a wrong value.
     */
    data object Unprovable : CastOutcome
}

/**
 * The ONE implementation of the instance test over a [Value], for both of Plan 8's type ops.
 *
 * **The row order, and each row states what it must beat.**
 *  1. [Value.readsAsNullReference] -> [CastOutcome.ReadsAsNull]. **FIRST**, and this is the one ordering
 *     decision in this function that changes an answer. `docs/DALVIK-SEMANTICS.md#check-cast-21c` files
 *     [BC]'s *"this will necessarily fail at runtime ... if B refers to a primitive type"* as a
 *     **documented-but-wrong** sentence for a null register, because ART tests null first -- so
 *     `check-cast vNull, I` succeeds and must not reach row 2. For `instance-of` the two rows are
 *     measured **inert** against each other (both answer `0`), so putting null first costs nothing and is
 *     required by the other consumer. Plan 8's Global Constraints state the same ordering as a
 *     requirement on Task 9; implementing it here means Task 9 inherits it instead of restating it.
 *  2. a **primitive or `void`** target -> [CastOutcome.Never]. **SPEC**, [BC]'s `instance-of` row:
 *     *"Since B must always be a reference (and not a primitive value), this will always result in `0`
 *     being stored if C refers to a primitive type."* It must precede row 3, or an unknown source against
 *     a primitive target answers `Unknown` where [BC] says the result is always `0` -- an engine choice
 *     contradicting a spec sentence, which is where an earlier revision of this plan had it.
 *  3. [Value.Unknown] / [Value.Top] -> **never [CastOutcome.Definitely]**, and this is the row Correctness
 *     Defect 18 lives on. It splits three ways:
 *      - [Value.Top], or a tag whose walk raised [ClassLoadFailure] -> [CastOutcome.Unprovable];
 *      - a tag that **does** satisfy the target -> [CastOutcome.NullOrInstance]. 🔴 Deliberately NOT
 *        `Definitely`: an `Unknown` **may be null** (`core/Value.kt`'s own definition) and `Frame.entryFrame`
 *        seeds every reference parameter that way, so `instance-of` must not fold `1`. The oracle does fold
 *        it and `docs/architecture/opcodes.md:173` files that as **Correctness Defect 18**; registered as a
 *        divergence rather than reconciled. `check-cast` reads the same case as a provable success, because
 *        for a cast the two disjuncts agree (design decision 5);
 *      - a tag that does not satisfy -> [CastOutcome.Unprovable], the genuinely ambiguous shape.
 *  4. a `Known` reference: the target is tested against **every** type the value is known to have (see
 *     [declaredAndValueTypes]) and a single satisfying one is decisive, because both members are true
 *     descriptions of one runtime object.
 *
 * **A [ClassLoadFailure] does not abort the walk, and that is deliberate in the SOUND direction of a
 * choice with two wrong answers.** `ClassIndex.isInstanceOf` is not total; its last row walks the
 * candidate's ancestors and raises for a class this input does not ship, which is the normal case in an
 * obfuscated APK. Letting the first raise escape would lose a fold that another member of the set proves
 * outright (over-refusal, the mirror defect), and absorbing it into `false` would fold a confidently wrong
 * constant. So a raise is remembered, the walk continues, and the answer is [CastOutcome.Unprovable] only
 * if nothing satisfied. Grounded on spec §2.2 **alone** -- nothing in [BC] speaks to an unanswerable
 * subtype question -- which is a SEPARATE rule from row 2's [BC]-grounded `false`. Task 7's first hand-off
 * merged the two and one wrong argument covered both; they are stated apart on purpose.
 *
 * Only [ClassLoadFailure] is caught, never `RuntimeException`: a bug in this engine's own lattice logic
 * must still reach the caller as itself, which is the boundary `ClassIndex` draws for the same reason.
 */
internal fun castOutcome(
    value: Value,
    target: VirtualType,
    types: InstanceOfTest,
    heapObjectTypeOf: (HeapRef) -> VirtualType?,
): CastOutcome {
    if (value.readsAsNullReference) return CastOutcome.ReadsAsNull
    if (!target.isReference) return CastOutcome.Never
    if (value !is Value.Known) {
        // Row 3. The tag is the ONLY thing known about the value, and it is an upper bound: satisfying the
        // target makes every possible content pass a cast, while failing to satisfy proves nothing at all,
        // because the content may still be a satisfying SUBTYPE of the tag. Hence the asymmetry -- a
        // satisfying tag is a named outcome, a failing one is ambiguity. `Value.Top` has no tag to ask.
        val tag = value.type ?: return CastOutcome.Unprovable
        val satisfied = try {
            types.isInstanceOf(tag, target)
        } catch (e: ClassLoadFailure) {
            // Same rule as the Known walk below, for the same reason: an unanswerable subtype question is
            // ambiguity, never `false`. There is only one candidate here, so there is nothing to continue to.
            return CastOutcome.Unprovable
        }
        return if (satisfied) CastOutcome.NullOrInstance else CastOutcome.Unprovable
    }
    var unanswerable = false
    for (candidate in declaredAndValueTypes(value, heapObjectTypeOf)) {
        val satisfied = try {
            types.isInstanceOf(candidate, target)
        } catch (e: ClassLoadFailure) {
            unanswerable = true
            continue
        }
        if (satisfied) return CastOutcome.Definitely
    }
    return if (unanswerable) CastOutcome.Unprovable else CastOutcome.Never
}

/**
 * Every type a value is known to have: its **declared tag** first, then the type of the thing it actually
 * holds. The core's counterpart of the oracle's `Value.declaredAndValueTypeNames`
 * (`smalivm/src/main/java/org/cf/smalivm/Value.kt:84-89` -- the ORACLE's `Value.kt`, whose same range in
 * `core/Value.kt` lands inside a KDoc), reproduced deliberately rather than delegated.
 *
 * **Not a copy, and the difference is the whole reason it is reproduced.** For a VM-tracked object the raw
 * is a [HeapRef] (spec §4.2), so `raw.javaClass` would be `HeapRef` -- a descriptor that names this
 * engine's own bookkeeping class and satisfies nothing. The "value type" for those is the **heap object's**
 * type, which is why this takes [heapObjectTypeOf] rather than reading the raw alone. Every migrated
 * `instance-of` expectation that seeds a host array depends on it: `int[][]` tagged `Ljava/lang/Object;`
 * is an instance of `[Ljava/lang/Object;` only through the object's type, since the TAG is not.
 *
 * A `Known` whose raw is null collapses to the tag alone, matching the oracle -- though in practice the
 * null-reading row of [castOutcome] has already diverted every such value, so this arm is reached only by
 * a direct caller.
 *
 * **Both members are true descriptions of ONE object**, which is what makes a single satisfying member
 * decisive in [castOutcome]: the value type is the runtime class, and the declared tag is an upper bound
 * the verifier proved (`docs/DALVIK-SEMANTICS.md#registers-types-and-what-a-declared-type-is`), so if
 * either is an instance of the target then so is the object.
 *
 * **🔴 A value type that cannot be DETERMINED collapses this to the tag alone, and that is not free --
 * stated plainly because the asymmetry with the other unanswerable case is easy to miss.** Either source
 * of the value type can yield null: [heapObjectTypeOf] for a [HeapRef] with no heap object, and
 * [descriptorOfHostClass] for a host class no descriptor can express. When that happens the list shortens
 * silently, so [castOutcome] finds nothing satisfying and falls through to a **definite**
 * [CastOutcome.Never] -- whereas three lines away its *other* unanswerable case, [ClassLoadFailure], is
 * routed to [CastOutcome.Unprovable]. But the declared tag is only an **upper bound**, so "the tag is not an
 * instance of the target" does **not** imply the object is not: `Known(HeapRef, "Ljava/lang/Object;")`
 * against `Ljava/lang/String;` would answer `Known(false, Z)` for an object that may well be a `String`.
 *
 * **Accepted rather than fixed, because it is unreachable in this build and a speculative repair would be
 * untested code.** Measured, not assumed: `VirtualType.of`'s validator accepts every descriptor
 * [descriptorOfHostClass] can construct (`.replace('.', '/')` removes the only character it forbids in an
 * `L…;` interior, and a JVM binary name cannot contain `;`), and a [HeapRef] sitting in a register with no
 * heap object is already an engine bug rather than an input. So no test can witness the shortening, and
 * threading the fact out of here -- returning `Pair<List<VirtualType>, Boolean>`, or taking an
 * `onUnnameable` callback -- would add a branch nothing exercises.
 *
 * **It is Task 9's hazard, not this op's, and that is the reason to write it down.** `instance-of` maps
 * [CastOutcome.Never] to a harmless `Known(false, Z)`; `check-cast` maps it to a **definite
 * `ClassCastException`**, so the identical fall-through would invent an infeasible throw -- verbatim Plan
 * 7's whole-branch Critical. If Task 9 makes either null source reachable, fix it **here** rather than
 * locally.
 */
internal fun declaredAndValueTypes(
    value: Value,
    heapObjectTypeOf: (HeapRef) -> VirtualType?,
): List<VirtualType> {
    if (value !is Value.Known) return listOfNotNull(value.type)
    val declared = value.type
    val raw = value.raw ?: return listOf(declared)
    val valueType = if (raw is HeapRef) heapObjectTypeOf(raw) else descriptorOfHostClass(raw.javaClass)
    return if (valueType == null || valueType == declared) listOf(declared) else listOf(declared, valueType)
}

/**
 * A host class as a Dalvik descriptor: `java.lang.String` -> `Ljava/lang/String;`, `int[]` -> `[I`,
 * `String[]` -> `[Ljava/lang/String;` (`Class.getName` already returns array descriptors with `.`
 * separators, so the only transform is the separator).
 *
 * Null for a name no descriptor can express, rather than throwing. Throwing would turn a host class the
 * engine merely cannot name into an `Aborted(HOST_FAILURE)` for the whole path -- indistinguishable from an
 * engine bug, which is the pair `CARRY-FORWARD.md`'s Plan 9 item 5 is about.
 *
 * **But a null does NOT "cost the value type and nothing more"** -- an earlier revision of this KDoc said
 * so and it was wrong. It costs the difference between [CastOutcome.Unprovable] and a **definite**
 * [CastOutcome.Never], because [declaredAndValueTypes] shortens its list and [castOutcome] then finds
 * nothing satisfying. Read that function's KDoc for why this is accepted (the branch is unreachable here)
 * and why it is nonetheless Task 9's hazard (`check-cast` turns that `Never` into a definite throw).
 */
private fun descriptorOfHostClass(hostClass: Class<*>): VirtualType? {
    val name = hostClass.name.replace('.', '/')
    val descriptor = if (name.startsWith("[")) name else "L$name;"
    return runCatching { VirtualType.of(descriptor) }.getOrNull()
}
