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

# Same-address fork: an unprovable predicate whose branch target is the fall-through, an ordinary
# opaque-predicate emission. `commit` builds two nodes at that one address with independent frames.
# The offset is +2 (one 21t instruction), not 0: [BC] forbids a zero branch offset.
.method public static ifEqualZeroToFallThrough(I)V
    .registers 2

    if-eqz v1, :next

    :next
    return-void
.end method

# Path explosion without a loop: nine sequential diamonds over one unprovable parameter send 2^9 paths
# to the final address. Address visits are path-local, so loop-free siblings do not exhaust the budget.
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

# Two `const-class` instructions on one class yield the same reference ([VER], monitor-verification
# section), so this `if-eq` has one successor and forking it would explore a branch no device takes.
.method public static ifEqualTwoConstClassesOfOneClass()V
    .registers 2

    const-class v0, Lif_test;
    const-class v1, Lif_test;

    if-eq v0, v1, :same

    nop

    :same
    return-void
.end method

# Reconciliation, disagreeing half: one arm assigns v0 and the other does not, so per-address
# consensus at the join is their join (Top) rather than either arm's value.
.method public static ifEqualZeroSkippingAnAssignment(I)V
    .registers 2

    if-eqz v1, :skip

    const/4 v0, 0x1

    :skip
    return-void
.end method

# Reconciliation, agreeing half: both arms assign the same value, so consensus at the join is that
# value and a consumer can fold it even though the predicate was never decided.
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

# Room for two wide pairs: `ifEqual()V` has `.locals 2`, so a `J`/`D` seed in v1 has no register for
# its high half. Same three addresses as `ifEqual()V`; only the operand registers differ. The operand
# types are not verifier-valid (`if-eq` wants both refs or both integral), so these answers are
# engine choices rather than fidelity targets.
.method public static ifEqualWithRoomForWidePairs()V
    .locals 4

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

# Both fixtures below are true diamonds: the branch target is reachable only by the jump, so a fork
# shows up as one extra reached address, and both arms leave the observed register v0 untouched.
# A pointer's magnitude is not something the lattice holds, so both comparisons fork.

# `if-lt` over two distinct live references. Different literals on purpose: equal literals intern to
# one object, which makes the comparison provable.
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

# `if-eq` between a live reference and a non-zero integer. A pointer is provably non-zero, which
# decides a comparison against 0 and nothing else, so this forks. The `*z` row against zero is
# definite (`IfOpTest.testIfObjectNotEqualZero`).
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
