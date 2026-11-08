# Fixtures where register reads at one address differ by cause: the paths disagree, the register
# was never assigned, no path reached the address, or the frame genuinely holds Top.
.class public Ldifferential/branch;
.super Ljava/lang/Object;

# v0 is assigned I on one arm and a String on the other (paths disagree); v1 is never assigned;
# v2 is the parameter, held but not assigned. Both arms reach the return-void at 6, so the join
# there is over two nodes.
.method public static disagreeingArms(I)V
    .registers 3

    if-eqz v2, :other

    const/4 v0, 0x1

    goto :end

    :other
    const-string v0, "x"

    :end
    return-void
.end method

# The predicate is provably zero, so address 3 is never attempted: v1 is unassigned at the reached
# address 4 and unreachable at 3. The branch offset is +3, not 0 ([BC] if-test).
.method public static provablyZeroSkipsAnAddress()V
    .registers 2

    const/4 v0, 0x0

    if-eqz v0, :taken

    const/4 v1, 0x1

    :taken
    return-void
.end method

# v0 holds a real Top that `move` copied from v2, which nothing ever wrote; v1 is never assigned.
.method public static moveFromAnUntouchedRegister()V
    .registers 3

    move v0, v2

    return-void
.end method

# The only path throws, so no path returns normally.
.method public static neverReturnsNormally(Ljava/lang/RuntimeException;)V
    .registers 2

    throw v1
.end method

# Two arms return different constants on an unknown predicate, so the returning paths join to
# Unknown(I). The selector is a parameter so both arms are explored; 3 and 4, not 0 and 1, so
# neither arm is indistinguishable from an unwritten register.
.method public static returnsDifferentConstants(I)I
    .registers 2

    if-eqz v1, :other

    const/4 v0, 0x3

    return v0

    :other
    const/4 v0, 0x4

    return v0
.end method

# A known predicate that falls through: 42 on the fall-through, 99 if the branch were wrongly
# taken. Nothing forks.
.method public static knownBranchFallsThrough()I
    .registers 1

    const/4 v0, 0x1

    if-eqz v0, :skip

    const/16 v0, 0x2a

    return v0

    :skip
    const/16 v0, 0x63

    return v0
.end method

# On an unknown parameter `if-eqz` forks into a backward-branch loop and a sibling that returns 42.
# The loop exceeds the per-address visit limit, which ends only the looping path; the sibling still
# returns. The `goto` targets the `nop`, not itself: a zero-offset `goto` is an encoding [BC] rejects.
.method public static loopWithNonLoopingSibling(I)I
    .registers 1

    if-eqz v0, :return_sibling

    :loop
    nop

    goto :loop

    :return_sibling
    const/16 v0, 0x2a

    return v0
.end method
