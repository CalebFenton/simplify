.class public Larray_test;
.super Ljava/lang/Object;

.method public static TestNewArrayPrimitive()V
    .locals 1

    const/4 v0, 0x1
    new-array v0, v0, [I

    return-void
.end method

.method public static TestNewArrayMultiDimensionalPrimitive()V
    .locals 1

    const/4 v0, 0x2
    new-array v0, v0, [[[I

    return-void
.end method

.method public static TestNewArrayLocal()V
    .locals 1

    const/4 v0, 0x1
    new-array v0, v0, [Larray_test;

    return-void
.end method

.method public static TestNewArrayMultiDimensionalLocal()V
    .locals 1

    const/4 v0, 0x2
    new-array v0, v0, [[[Larray_test;

    return-void
.end method

.method public static TestArrayPut()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    const/4 v2, 0x4
    aput v2, v0, v1 # v0[0] = 4

    return-void
.end method

.method public static TestArrayPutWide()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [J

    const/4 v1, 0x0
    const-wide v2, 0x5
    aput-wide v2, v0, v1 # v0[0] = 5

    return-void
.end method

.method public static TestArrayGetUninitializedPrimitive()V
    .locals 2

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    aget v0, v0, v1

    return-void
.end method

.method public static TestArrayGetPrimitive()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    const/4 v2, 0x4
    aput v2, v0, v1 # v0[0] = 4

    aget v0, v0, v1

    return-void
.end method

.method public static TestArrayGetLocal()V
    .locals 1

    return-void
.end method

.method public static TestFilledNewArrayLocal()V
    .locals 4

    const/4 v0, 0x1

    const/4 v1, 0x2

    const/4 v2, 0x3

    const/4 v3, 0x4

    const/4 v4, 0x5

    filled-new-array {v0, v1, v2, v3, v4}, [I

    #result register should have a new int[1][2][3][4][5]

    return-void
.end method