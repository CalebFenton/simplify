# WORKLIST ROW 12, FOURTH CELL -- AIOOBE strictly precedes ASE, proved a SECOND way: with the index
# NEGATIVE rather than too large.
#
# THIS FIXTURE'S CELL: a valid, non-null `String[1]`, an index of -1, AND an unassignable `Object`
# value -- three conditions, one instruction, exactly one winner:
#
#   AIOOBE -> the bounds check runs before the store's assignability check, and a negative index is
#             out of range (as `Fx_AputNegativeIndex` shows on its own). The inherited [JVMS]
#             `aastore` "Otherwise" chaining holds for a negative index too.
#   ASE    -> assignability is checked before bounds, and the row-12 ordering is backwards.
#
# `Fx_AputBadIndexIncompatibleValue` is the same cell with a TOO-LARGE index (5); this is its
# NEGATIVE-index twin, so the pair shows AIOOBE-before-ASE does not depend on the direction the index
# leaves the range.
#
# SELF-CONTAINED: allocates its own array and its own incompatible value (`Fx_AputObjectIncompatible`'s
# `new-instance` + `invoke-direct` pattern).

.class public LFx_AputNegativeIndexIncompatibleValue;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    new-array v1, v0, [Ljava/lang/String;

    const/4 v2, -0x1

    new-instance v3, Ljava/lang/Object;
    invoke-direct {v3}, Ljava/lang/Object;-><init>()V

    # THE OPCODE UNDER TEST: index -1 into a length-1 array, storing an `Object`.
    aput-object v3, v1, v2

    const-string v0, "NO_THROW"
    return-object v0
.end method
