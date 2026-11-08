# Ordering: AIOOBE strictly precedes ASE.
#
# THIS FIXTURE'S CELL: a valid, non-null `String[1]`, an out-of-range index, AND an
# unassignable value, all at once. Three conditions, one instruction, so exactly one
# exception can win:
#
#   AIOOBE -> the inherited [JVMS] `aastore` chaining holds for this pair.
#   ASE    -> ART checks assignability before bounds, and `DALVIK-SEMANTICS.md`'s
#             ordering claim is backwards.
#
# Read this together with `Fx_AputNullArrayBadIndex`: the two together pin the full
# NPE -> AIOOBE -> ASE chain, and neither one alone can.
#
# SELF-CONTAINED: allocates its own array and its own incompatible value.

.class public LFx_AputBadIndexIncompatibleValue;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    new-array v1, v0, [Ljava/lang/String;

    const/16 v2, 0x5

    new-instance v3, Ljava/lang/Object;
    invoke-direct {v3}, Ljava/lang/Object;-><init>()V

    # THE OPCODE UNDER TEST: index 5 into a length-1 array, storing an `Object`.
    aput-object v3, v1, v2

    const-string v0, "NO_THROW"
    return-object v0
.end method
