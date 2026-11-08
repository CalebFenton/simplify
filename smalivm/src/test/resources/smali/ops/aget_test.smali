.class Laget_test;
.super Ljava/lang/Object;

.method public static getUninitializedInt()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static get()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static getWithCatch()V
    .locals 2

    :try_start_0
    aget v0, v0, v1
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method

.method public static getWide()V
    .locals 4

    aget-wide v0, v0, v1

    return-void
.end method

.method public static getObject()V
    .locals 3

    aget-object v0, v0, v1

    return-void
.end method

.method public static getBoolean()V
    .locals 3

    aget-boolean v0, v0, v1

    return-void
.end method

.method public static getByte()V
    .locals 3

    aget-byte v0, v0, v1

    return-void
.end method

.method public static getChar()V
    .locals 3

    aget-char v0, v0, v1

    return-void
.end method

.method public static getShort()V
    .locals 3

    aget-short v0, v0, v1

    return-void
.end method

# `getWide()V` is `aget-wide v0, v0, v1`, whose destination pair {0,1} equals its operand set. Here
# the pair and the operands are disjoint, so registersAssigned and registersRead are distinguishable.
.method public static getWideIntoDisjointPair()V
    .locals 5

    aget-wide v3, v0, v1

    return-void
.end method

# Format 23x operand order with three distinct registers (vAA = dest, vBB = array, vCC = index). The
# other `aget*` methods here are `v0, v0, v1`, where a dest/array swap is invisible, and swapping
# `getWideIntoDisjointPair()V`'s array and index leaves `registersRead` the same set {0,1}.
.method public static getWithThreeDistinctRegisters()V
    .locals 3

    aget v2, v0, v1

    return-void
.end method

# A fresh `[Ljava/lang/Object;` whose elements are definitely null, read back with `aget-object`:
# the read yields `Known(null, T)`. The differential counterpart is the curated fixture
# `smoke-null-element-from-fresh-array` (`Ldifferential/smoke;->nullElementFromFreshArray()`),
# which CoreProjectionTest projects as ValueKind.NULL; this method pins the value itself.
.method public static getObjectFromFreshArray()V
    .locals 3

    const/4 v1, 0x1
    new-array v2, v1, [Ljava/lang/Object;
    const/4 v1, 0x0
    aget-object v0, v2, v1

    return-void
.end method
