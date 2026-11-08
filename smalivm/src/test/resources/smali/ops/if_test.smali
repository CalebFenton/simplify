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

# An unprovable branch whose target is the next instruction: both transitions name one address, and each
# gets its own node and frame copy there. The offset is +2, not 0, because [BC] forbids a zero `if` offset.
.method public static ifEqualZeroToFallThrough(I)V
    .registers 2

    if-eqz v1, :next

    :next
    return-void
.end method

# Nine sequential diamonds over one unknown parameter, no loop: 2^9 = 512 paths reach the final address.
# Address visits are counted per path, so sibling paths do not exhaust the visit limit.
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

# [VER], on monitor verification: "the result values from two const-class instructions on the same class
# will be the same reference, but the verifier doesn't recognize this". So this `if-eq` has one successor.
.method public static ifEqualTwoConstClassesOfOneClass()V
    .registers 2

    const-class v0, Lif_test;
    const-class v1, Lif_test;

    if-eq v0, v1, :same

    nop

    :same
    return-void
.end method

# One arm assigns v0 and the other does not, so the two nodes at the join hold different values and
# per-address consensus is their join (Top).
.method public static ifEqualZeroSkippingAnAssignment(I)V
    .registers 2

    if-eqz v1, :skip

    const/4 v0, 0x1

    :skip
    return-void
.end method

# Both arms assign the same value, so consensus at the join is that value even though the predicate is
# undecided.
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

# `ifEqual()V` with room for `J`/`D` operands: a wide seed occupies its register and the next, so the
# operands are v0 and v2. Same addresses as `ifEqual()V`: the `22t` at 0, `nop` at 2, `return-void` at 3.
.method public static ifEqualWithRoomForWidePairs()V
    .locals 4

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

# Core regressions in `differential/Fixtures.kt`: true diamonds whose taken arm is reachable only by the
# jump, so a fork shows as one extra reached address. Both inputs are verifier-rejected engine choices.

# `if-lt` over two distinct live references; distinct literals, because one interned literal would make
# the pointers identical and the comparison provable. A fork reaches the taken arm at 9.
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

# `if-eq` between a live reference and the integer 5. A pointer is provably non-zero, which decides a
# comparison against 0 only, so this forks and reaches the taken arm at 8.
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
