package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ProvableInt
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.provableInt
import org.cf.smalivm.core.sameReference

/**
 * Which way the two operands must compare for the branch to be taken. `EQ` serves `if-eq` and
 * `if-eqz`, and so on for all twelve: the `*z` form differs only in where its right-hand operand comes
 * from, never in the test.
 */
internal enum class IfComparison {
    EQ, NE, LT, GE, GT, LE;

    /** Whether the branch is taken, for a comparison the engine DECIDED (negative / zero / positive). */
    fun holdsFor(cmp: Int): Boolean = when (this) {
        EQ -> cmp == 0
        NE -> cmp != 0
        LT -> cmp < 0
        GE -> cmp >= 0
        GT -> cmp > 0
        LE -> cmp <= 0
    }

    /**
     * Whether the branch is taken when the operands are provably **unequal** but not by how much --
     * two distinct pointers, or a null against a live one. Only the equality pair can answer that;
     * an ordering question over two values whose magnitudes are unknown is genuinely undecided, and
     * `null` here is what makes [IfOp] fork rather than guess. Answering `false` for `LT` instead
     * would leave a feasible arm unexplored (spec §2.2).
     */
    fun holdsForUnequal(): Boolean? = when (this) {
        EQ -> false
        NE -> true
        LT, GE, GT, LE -> null
    }
}

/**
 * The twelve conditional branches: `if-eq`/`if-ne`/`if-lt`/`if-ge`/`if-gt`/`if-le` (`22t`, two
 * registers) and their `*z` forms (`21t`, one register against the integer zero, which
 * [rhsRegister]` == null` selects).
 *
 * **SPEC** [BC], and this is the whole of it: *"Branch to the given destination if the given two
 * registers' values compare as specified"*, and for the `*z` forms *"if the given register's value
 * compares with 0 as specified"*. **[BC] never names the operand type for any of the twelve**, and at
 * runtime none of them type-checks anything -- they compare raw 32-bit register contents, **signed**
 * (`docs/DALVIK-SEMANTICS.md#if-eq--if-ne--if-lt--if-ge--if-gt--if-le-22t-and-the-z-forms-21t`). The
 * one type-related thing [BC] does say is the sentence everything below rests on: *"In terms of bitwise
 * representation, `(Object) null == (int) 0`."* That is why Dalvik needs no `if_acmpeq`/`ifnull`.
 *
 * **THIS IS THE FIRST VALUE-AMBIGUITY FORK IN THE ENGINE**, and the mechanism the whole thing exists
 * for: an unprovable predicate explores both arms and lets per-address consensus reconcile them, while
 * a *provable* one must take exactly one. Both mistakes are wrong answers and are graded equally (spec
 * §2.2): forking a decidable comparison tells a deobfuscator a dead arm is reachable, and deciding an
 * undecidable one leaves a feasible arm unexplored on a run reporting `Complete`.
 *
 * **The decision procedure APPLIES the provability rule; it does not restate it.** See
 * [org.cf.smalivm.core.provableInt] and [org.cf.smalivm.core.sameReference]. Its rows are in [decide],
 * where they are argued one by one -- and they are pairwise **disjoint**, so unlike the table this plan
 * repaired there is no "first matching row wins" for a later edit to break.
 *
 * **Throws nothing.** All twelve carry `CAN_CONTINUE` and no `CAN_THROW` in dexlib2, and there is no
 * exception any of them could raise: every operand is a register read.
 *
 * **Side effects: NONE, and `recordSideEffect` is called nowhere in this file.** A branch writes no
 * register and touches no state outside its own [Frame] -- it only chooses a successor. A spurious
 * `STRONG` here would silently stop every constant fold in any method containing a branch, since
 * `ExecutionGraph.methodSideEffectLevelOverExploredPaths` maxes over addresses, which is most methods
 * in a real APK. Pinned by `core/op/IfOpTest.aBranchDeclaresNoSideEffectOnEveryRow`.
 *
 * **Assigns nothing, and the reason is not `FilledNewArrayOp`'s.** [registersAssigned] stays the
 * inherited `emptySet()` because an `if` genuinely writes no register at all -- not because its
 * destination is a `Frame` slot a `Set<Int>` cannot name, which is why `filled-new-array` leaves it
 * empty. The oracle declares `registersAssignedCount = 1` (`opcode/IfOp.kt:37`) for an op that assigns
 * nothing; that is harmless *there* (`docs/architecture/state-model.md:80` records those counts as
 * *"only initial-capacity hints ... not effect contracts"* and names `IfOp` as its example, and its
 * `getAvailableRegisters` derives liveness from the runtime-recorded sets instead), and it is **not**
 * harmless here: `ExecutionGraph.availableRegisters` is purely static, so [registersRead] and
 * [registersAssigned] are the *sole* guarantors of a correct free-register answer
 * (`docs/architecture/graph-optimizer-contract.md:136` is the consumer -- **Dead removal**, the row that
 * *"reads read/assignment metadata"*, not `:138`'s **Unreflection**, which consumes register layout; the
 * slip was found and corrected by Plan 8 Task 6's fix round 2. `:254` records that the old
 * query *"deterministically force-unwraps queue exhaustion for any ordinary non-leaf traversal"*, i.e.
 * crashed on any branching method, which is why the new one is static). **Do not "restore parity" with
 * the oracle's two counts.** `docs/architecture/state-model.md:290` lists *"Assertions that opcode
 * read/assignment counts are capacity hints rather than effect contracts"* among the audit's MISSING
 * test cases, so `core/op/IfOpTest.aBranchReadsItsOperandsAndAssignsNothing` closes an audited gap.
 *
 * The oracle's read count is wrong in the other direction and for a subtler reason worth recording:
 * `registersReadCount = if (compareToZero) 1 else 2` (`opcode/IfOp.kt:36`) is a **class-body
 * initializer**, so it runs during primary-constructor init -- *before* the secondary constructor's
 * `compareToZero = false` at `:33` -- and is therefore `1` for **both** forms.
 *
 * **[targetAddress] is a RESOLVED code address bound to the decoded method revision.**
 * `OpBuilder` reads the immutable method body's control target. After an edit, fresh execution decodes
 * the new body instead of reusing an old op. Existing sessions retain their original decoded targets.
 * `docs/architecture/graph-optimizer-contract.md:248` records that exact coupling breaking the old
 * editor: *"Branch and payload targets remain cached from the old revision because op recreation is
 * disabled"*.
 */
internal class IfOp(
    address: Int,
    override val name: String,
    private val comparison: IfComparison,
    private val targetAddress: Int,
    private val lhsRegister: Int,
    private val rhsRegister: Int?,
) : Op(address) {

    /**
     * Both operands for the `22t` form, one for `21t` -- whose right-hand side is a literal zero rather
     * than a register, so there is nothing else to name. Real, not a capacity hint: see the class KDoc.
     */
    override val registersRead: Set<Int> =
        if (rhsRegister == null) setOf(lhsRegister) else setOf(lhsRegister, rhsRegister)

    override fun execute(frame: Frame): OpExecution {
        val lhs = frame.readRegister(lhsRegister)
        // The `*z` forms compare against the integer zero. Modelled as the ANSWER (`Exactly(0)`) rather
        // than as a synthetic `Known(0, "I")` operand: there is no register behind it, and the only rows
        // below that need a `Value` are the pointer ones, which a provable zero can never reach.
        val rhs = rhsRegister?.let { frame.readRegister(it) }
        val taken = decide(lhs, rhs)
            // AMBIGUOUS: both arms are feasible, so both are explored. `Continue` FIRST, matching the
            // oracle's `arrayOf(nextAddress, targetAddress)` (`opcode/IfOp.kt:45`) -- transitions are
            // pushed onto the LIFO frontier in reverse, so the first declared one is the first path
            // explored, and `graph.outcomes` is ordered, which makes this observable rather than
            // cosmetic (design decision 2).
            ?: return OpExecution.of(OpArm.Continue(), OpArm.Jump(targetAddress))
        return OpExecution.single(if (taken) OpArm.Jump(targetAddress) else OpArm.Continue())
    }

    /**
     * Whether the branch is taken, or **null when the predicate is unprovable** and both arms must be
     * explored.
     *
     * **THE ROWS ARE PAIRWISE DISJOINT, WHICH IS STRICTLY BETTER THAN BEING CORRECTLY ORDERED, and this
     * was measured rather than argued.** The defect this plan repaired was a table whose first row was a
     * catch-all (*"not provably readable at 32 bits"*, which every reference raw satisfies) -- so under
     * "first matching row wins" it made the reference rows **unreachable** and forked six definite
     * migrated expectations. The fix is not a better order: it is gating every row on a distinct
     * combination of [ProvableInt] cases, so no row can shadow another and there is no order to get
     * wrong. `Exactly`/`NonZero`/`Unprovable` partition the lattice, and the four gates below are
     * `(Exactly, Exactly)`, `(NonZero, NonZero)`, `(Exactly 0, NonZero)` and `(NonZero, Exactly 0)`,
     * which share no member. Measured: moving row 1 after the pointer rows kills **0** tests -- the order
     * really is presentational, and that is the property to preserve. What IS order-dependent lives one
     * level down, in [provableInt], whose `Float`/`Double` row must precede its `Coerce` row.
     *
     * **The standing pin for the disjointness is
     * `core/op/IfOpTest.everyPairOfLatticeRepresentativesDecidesTheSameWayForAllSixComparisons`** --
     * eleven lattice representatives squared, times all six comparisons, with the per-pair verdict
     * written as a literal matrix. A new or widened gate that shadows an existing one changes a cell in
     * it. **Cite that test and not a mutation measurement:** the first revision of this KDoc rested the
     * claim on a one-off measurement recorded in a task report, which the plan's closeout deletes -- and
     * review then found that one of these four gates could be deleted with a green suite, so the gate
     * coverage that measurement implied did not exist. An invariant that replaces a documented rule has
     * to be enforced by a test, not by a number in prose.
     *
     *  1. **Both operands' bits are exactly known** -> compare them as signed `Int`s. This is 48 of the
     *     71 migrated tests, and it covers two cases worth naming. It is what `(Object) null == (int) 0`
     *     requires: `Known(null)` against `Known(0, "I")` is `Exactly(0)` against `Exactly(0)`, i.e.
     *     **equal**, which is the answer the oracle gets wrong (it reaches host identity and answers
     *     `null === Integer(0)`, `false`; registry D2, and `if-eq vNull, vInt` *verifies*, so the oracle
     *     is wrong on verified input there). And it is what keeps identity away from inputs identity
     *     cannot judge: `Known(1000, "I")` against `Known(1000, "I")` is two distinct host boxes, so a
     *     row that asked `===` about them would answer "definitely unequal" for `1000 == 1000`. Note
     *     that the disjointness is what makes that safe, not the order -- an integral raw is `Exactly`
     *     and never `NonZero`, so row 2's gate cannot see it at all.
     *  2. **Both operands are provably live pointers and they are the SAME pointer** -> `cmp == 0`, and
     *     therefore **all six** comparisons are decided: `if-ge`/`if-le` on identical references are
     *     provably **true**, not ambiguous. Forking them would be the invents-a-live-branch defect.
     *  3. **Both are provably live pointers and they are DIFFERENT pointers** -> provably unequal, so
     *     `if-eq`/`if-ne` are decided and the four ordering tests are not (see
     *     [IfComparison.holdsForUnequal]). Two distinct live objects have distinct addresses; a
     *     [org.cf.smalivm.core.HeapRef] and a host object are never the same guest object. Three
     *     migrated tests rest on this.
     *  4. **One side is provably zero and the other provably a live pointer** -> provably unequal, so
     *     again `if-eq`/`if-ne` are decided. This is the `*z` null-check pair, and it is the row that
     *     makes `if-nez vRef` **true** for a definitely-allocated reference instead of a fork. It is
     *     kept separate from row 3 rather than folded into [sameReference], because the reasoning is
     *     about *bits* (zero versus non-zero) and not about pointers -- `Known(0, "I")` is not a
     *     reference at all, and asking an identity question about it is a category error that happens to
     *     give the right answer.
     *  5. **Anything else** -> unprovable, fork. That is: either operand [Value.Unknown]/[Value.Top]; a
     *     `Float` or `Double` raw, whose 32 bits are not the number the host box holds (Task 3); or a
     *     live pointer against a *non-zero* integral, where the pointer's bits are genuinely unknown.
     *
     * **Row 5's last case and the ordering half of rows 2-3 are ENGINE CHOICES, not fidelity targets**,
     * and they are made here rather than fallen into. The verifier admits neither (`if-lt` and friends
     * are integral-only with `float` excluded, `if-eq` wants both refs or both integral --
     * [ART] `runtime/verifier/method_verifier.cc:3338-3350` and `:3319-3337`), so there is no runtime
     * behavior to match and spec §2.2 decides. The oracle answers **all** of them definitely, by
     * applying `lhs.raw === rhs.raw` for every comparison whenever either raw is non-numeric
     * (`opcode/IfOp.kt:56-58`, `:65-67`), which yields `cmp ∈ {0, 1}` -- so it says `if-lt` on two
     * references is definitely false and that a reference is definitely unequal to `5`. This op forks
     * instead, because a pointer's magnitude is not something the lattice holds, and *deciding* it is
     * the direction that loses a feasible path. Recorded as a VERIFIER-tier choice in
     * `docs/DALVIK-SEMANTICS.md`'s *"Choices this plan makes"* table; no migrated expectation and no
     * differential fixture observes it (measured -- `CompareObjectReferences` uses only `if-eq`/`if-ne`/
     * `if-eqz`/`if-nez`, and no corpus seed pairs a reference with a non-zero primitive), which is why
     * it is pinned by core tests named in that table's row instead.
     */
    private fun decide(lhs: Value, rhs: Value?): Boolean? {
        val lhsBits = provableInt(lhs)
        // The `*z` form's right-hand side is the literal zero, i.e. the ANSWER is known without a
        // register. Modelled that way rather than as a synthetic `Known(0, "I")`, so no row below can
        // mistake it for an operand it could ask a pointer question about.
        val rhsBits = rhs?.let { provableInt(it) } ?: ProvableInt.Exactly(0)
        val zero = { bits: ProvableInt -> bits is ProvableInt.Exactly && bits.bits == 0 }
        return when {
            // ROW 1 -- both operands' bits exactly known.
            lhsBits is ProvableInt.Exactly && rhsBits is ProvableInt.Exactly ->
                comparison.holdsFor(lhsBits.bits.compareTo(rhsBits.bits))
            // ROWS 2 and 3 -- both operands provably live pointers, so and only so is identity the
            // question. That gate is what keeps `sameReference` -- a pure identity test that cannot tell
            // a pointer from a boxed primitive -- away from a `Float` pair, where it answers `false` and
            // would fold a confident wrong constant.
            lhsBits is ProvableInt.NonZero && rhsBits is ProvableInt.NonZero -> when (sameReference(lhs, rhs!!)) {
                true -> comparison.holdsFor(0)
                false -> comparison.holdsForUnequal()
                // Unreachable from here: both sides are `NonZero`, which only a `Value.Known` can be,
                // and `sameReference` answers null only when one side is not `Known`. Kept because the
                // helper is specified as total over the lattice for the callers Plan 9 adds, and because
                // a `when` over `Boolean?` that omitted it would not compile.
                null -> null
            }
            // ROW 4 -- one side provably zero, the other provably a live pointer, in either order. The
            // BITS differ, which is a stronger statement than "not the same pointer" and is what [BC]'s
            // `(Object) null == (int) 0` licenses directly.
            zero(lhsBits) && rhsBits is ProvableInt.NonZero -> comparison.holdsForUnequal()
            zero(rhsBits) && lhsBits is ProvableInt.NonZero -> comparison.holdsForUnequal()
            // ROW 5.
            else -> null
        }
    }

    /**
     * The oracle's format, so Plan 10's instruction listing and Simplify's grapher agree with it:
     * `if-ge r2, r4, :addr_0` for the two-register form, `if-eqz r0, :addr_3` for the `*z` form.
     * Pinned by `OpBuilderTest.opsRenderTheirOperandsInTheirDeclaredOrder`, which is where the deleted
     * mockk nest's one non-mock assertion was re-homed.
     */
    override fun toString(): String = buildString {
        append(name).append(" r").append(lhsRegister)
        if (rhsRegister != null) append(", r").append(rhsRegister)
        append(", :addr_").append(targetAddress)
    }
}
