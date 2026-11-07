.class Lif_test;
.super Ljava/lang/Object;

.method public static ifEqual()V
    .locals 2

    if-eq v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifNotEqual()V
    .locals 2

    if-ne v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifLessThan()V
    .locals 2

    if-lt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterOrEqual()V
    .locals 2

    if-ge v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterThan()V
    .locals 2

    if-gt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifLessOrEqual()V
    .locals 2

    if-le v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifEqualZero()V
    .locals 1

    if-eqz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifNotEqualZero()V
    .locals 1

    if-nez v0, :end

    nop

    :end
    return-void
.end method

.method public static ifLessThanZero()V
    .locals 1

    if-ltz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterOrEqualZero()V
    .locals 1

    if-gez v0, :end

    nop

    :end
    return-void
.end method
.method public static ifGreaterThanZero()V
    .locals 1

    if-gtz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifLessOrEqualZero()V
    .locals 1

    if-lez v0, :end

    nop

    :end
    return-void
.end method

.method public static ifEqIntToByte()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

.method public static ifEqByteToInt()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v2, v0, :end

    nop

    :end
    return-void
.end method

# --- Plan 8 Task 4 additions. Everything above is the corpus fixture, untouched. ---

# THE SAME-ADDRESS FORK: an unprovable predicate whose branch TARGET IS the fall-through, which is an
# ordinary opaque-predicate emission and what `switch` also produces when a case offset is +3. Two
# transitions naming ONE address, so `commit` builds two nodes there with independent frame copies --
# the shape `CARRY-FORWARD.md`'s Plan 8 item 1 claimed could not be expressed. Note the offset is +2
# (one 21t instruction), not 0: [BC] forbids a zero branch offset for the `if` rows too.
.method public static ifEqualZeroToFallThrough(I)V
    .registers 2

    if-eqz v1, :next

    :next
    return-void
.end method

# PATH EXPLOSION, made observable. Nine sequential diamonds over ONE unprovable parameter, no loop:
# 2^9 = 512 paths reach the final address and `ExecutionSession.addressVisits` is session-GLOBAL, so
# the 500-visit budget is consumed by sibling paths rather than by loop iterations and the method
# self-truncates. Nine `if-eqz` on an unknown parameter is unremarkable obfuscated code.
.method public static nineUnknownDiamonds(I)V
    .registers 2

    if-eqz v1, :d1
    nop
    :d1
    if-eqz v1, :d2
    nop
    :d2
    if-eqz v1, :d3
    nop
    :d3
    if-eqz v1, :d4
    nop
    :d4
    if-eqz v1, :d5
    nop
    :d5
    if-eqz v1, :d6
    nop
    :d6
    if-eqz v1, :d7
    nop
    :d7
    if-eqz v1, :d8
    nop
    :d8
    if-eqz v1, :d9
    nop
    :d9
    return-void
.end method

# TWO `const-class` INSTRUCTIONS ON ONE CLASS YIELD THE SAME REFERENCE, so this `if-eq` has ONE
# successor and forking it would explore a branch no device can take. [VER] states it as a
# parenthetical in its monitor-verification section: "the result values from two const-class
# instructions on the same class will be the same reference, but the verifier doesn't recognize this".
# The verifier being conservative about it is what makes it a RUNTIME guarantee this engine may lean on.
#
# `const-class` lands in Task 10, so today this decodes to an UnsupportedOp and the run is Partial --
# which is what `IfOpTest.twoConstClassResultsForOneClassMustBeOneReferenceAndNotAFork` asserts, on
# purpose, so it FAILS ON ARRIVAL the day Task 10 lands the opcode and forces the identity-stability
# decision to be made deliberately rather than inherited from whatever its cache happens to do.
.method public static ifEqualTwoConstClassesOfOneClass()V
    .registers 2

    const-class v0, Lif_test;
    const-class v1, Lif_test;

    if-eq v0, v1, :same

    nop

    :same
    return-void
.end method

# RECONCILIATION, the disagreeing half: one arm assigns v0 and the other does not, so the two nodes at
# the join address hold different values for it and per-address consensus is their JOIN (Top) rather
# than either arm's. This is the fork's whole purpose -- explore both, reconcile at the join -- and it
# is the shape `nop`-only corpus fixtures cannot show, because a `nop` assigns nothing on either arm.
.method public static ifEqualZeroSkippingAnAssignment(I)V
    .registers 2

    if-eqz v1, :skip

    const/4 v0, 0x1

    :skip
    return-void
.end method

# RECONCILIATION, the agreeing half: both arms assign the SAME value, so consensus at the join is that
# value and a `Complete` run lets a consumer fold it even though the predicate was never decided. The
# payoff the whole engine exists for.
.method public static ifEqualZeroAssigningOneOnBothArms(I)V
    .registers 2

    if-eqz v1, :skip

    const/4 v0, 0x1

    goto :end

    :skip
    const/4 v0, 0x1

    :end
    return-void
.end method

# ROOM FOR TWO WIDE PAIRS, and the reason it has to exist: `ifEqual()V` above has `.locals 2`, so a
# `J`/`D`-tagged seed in v1 has nowhere to put the pair's high half -- `Frame.assign` reserves
# `register + 1`, which is out of range, and the seed throws before the op runs. The oracle's flat
# string-keyed state has no high half at all, so the corpus could express a register file no device can
# have: `if-eq v0, v1` with a long in v0 and a double in v1 needs (v0,v1) AND (v1,v2) at once.
#
# Same three addresses as `ifEqual()V` (`22t` at 0, `nop` at 2, `return-void` at 3), so the migrated
# visitation expectations apply unchanged; only the operand REGISTERS differ, which is a change in where
# the harness reads a value and not a change to the value. The operand TYPES are still not
# verifier-valid (`if-eq` wants both refs or both integral, and `IsIntegralTypes` excludes `kLongLo`,
# `kFloat` and `kDoubleLo`) -- that is the recorded CORPUS-tier property of these fixtures, and it is
# what makes their answers engine choices rather than fidelity targets.
.method public static ifEqualWithRoomForWidePairs()V
    .locals 4

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

# --- the two DIFFERENTIAL witnesses for Task 4's engine choices (fix round 1) ---
#
# Both are TRUE DIAMONDS: the branch target is reachable only by the jump, so a fork shows up as one
# extra reached address, and both arms are inert with respect to v0 -- the observed register -- so the
# two engines agree about every value and differ only in the path set. That is the construction
# `Fixtures.intendedDivergences`' ORACLE_MISJUDGES_FEASIBILITY entries already use.
#
# The oracle applies `lhs.raw === rhs.raw` for EVERY ifType whenever either raw is non-numeric
# (`opcode/IfOp.kt:56-58`, `:65-67`), so it derives `cmp ∈ {0, 1}` and answers both of these
# DEFINITELY. The core forks, because a pointer's magnitude is not something the lattice holds. Without
# these two fixtures the difference would be invisible to the only instrument that watches for a
# reconciliation toward the oracle.

# `if-lt` over two distinct live references. Different literals on purpose: equal literals would be
# interned to one object and the comparison would be provable (identical pointer), which is a different
# row. Oracle: cmp = 1, so `if-lt` is definitely false and the taken arm at address 9 is never scheduled.
.method public static branchOnOrderingOverTwoDistinctReferences()V
    .locals 3

    const/4 v0, 0x1

    const-string v1, "alpha"

    const-string v2, "beta"

    if-lt v1, v2, :taken

    nop

    goto :end

    :taken
    nop

    :end
    return-void
.end method

# `if-eq` between a live reference and a NON-ZERO integer. Oracle: `"alpha" === Integer(5)` is false, so
# cmp = 1 and `if-eq` is definitely false; the taken arm at address 8 is never scheduled. A pointer is
# provably non-ZERO, which decides a comparison against 0 and nothing else -- it is not provably
# different from 5, so the core forks. (Against zero it does NOT fork: that is the `*z` null-check row,
# and `core/op/IfOpTest.testIfObjectNotEqualZero` pins it definite.)
.method public static branchOnEqualityBetweenAReferenceAndANonZeroInt()V
    .locals 3

    const/4 v0, 0x1

    const-string v1, "alpha"

    const/4 v2, 0x5

    if-eq v1, v2, :taken

    nop

    goto :end

    :taken
    nop

    :end
    return-void
.end method
