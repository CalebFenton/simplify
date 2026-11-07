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

# Plan 7 addition. `getWide()V` is `aget-wide v0, v0, v1`, whose destination pair {0,1} is also its operand
# set, so a test asserting registersAssigned == {0,1} there passes even if the op returns registersRead.
# Here the pair and the operands are disjoint, which is what makes the claim checkable.
.method public static getWideIntoDisjointPair()V
    .locals 5

    aget-wide v3, v0, v1

    return-void
.end method

# Plan 7 addition, for the OPERAND ORDER of format 23x's three genuinely distinct registers (vAA = dest,
# vBB = array, vCC = index). Every other `aget*` in this file is `v0, v0, v1`, so dest and array collapse to
# one number and a decoder that swapped them leaves the whole suite green -- the same blind spot Plan 7's
# Task 6 review found for `new-array` and `array-length`. `getWideIntoDisjointPair()V` above is not enough
# on its own: swapping ITS array and index operands leaves `registersRead` the identical set {0,1}.
.method public static getWithThreeDistinctRegisters()V
    .locals 3

    aget v2, v0, v1

    return-void
.end method

# A fresh `[Ljava/lang/Object;` whose elements are DEFINITELY null, read back with `aget-object`. This is
# the first thing in the whole engine that produces `Known(null, T)`, i.e. the first ValueKind.NULL. It does
# NOT satisfy NewCoreDifferentialTest's ValueKind-coverage guard: that sweep runs Fixtures.newCoreSupported,
# and this file is not in it. **Task 13 added the differential fixture for that half** --
# `Ldifferential/smoke;->nullElementFromFreshArray()`, registered as `smoke-null-element-from-fresh-array`.
# This method carries the SEMANTICS claim and keeps it; the two are not interchangeable.
.method public static getObjectFromFreshArray()V
    .locals 3

    const/4 v1, 0x1
    new-array v2, v1, [Ljava/lang/Object;
    const/4 v1, 0x0
    aget-object v0, v2, v1

    return-void
.end method
