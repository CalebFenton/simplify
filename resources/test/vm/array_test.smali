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

.method public static TestFilledNewArrayPrimitive()V
    .locals 4

    const/4 v0, 0x1
    const/4 v1, 0x2
    const/4 v2, 0x3
    const/4 v3, 0x4
    const/4 v4, 0x5

    filled-new-array {v0, v1, v2, v3, v4}, [I

    return-void
.end method

.method public static TestFilledNewArrayLocal()V
    .locals 4

    const/4 v0, 0x1
    const/4 v1, 0x2
    const/4 v2, 0x3

    filled-new-array {v0, v1, v2}, [Larray_test;

    return-void
.end method

.method public static TestFillArrayDataInt()V
    .locals 1

    const/4 v0, 0x5
    new-array v0, v0, [I

    fill-array-data v0, :array_0

    return-void

    nop

    :array_0
    .array-data 4
        0x1
        0x2
        0x3
        0x4
        0x5
    .end array-data
.end method

.method public static TestFillArrayDataChar()V
    .locals 1

    const/4 v0, 0x3
    new-array v0, v0, [C

    fill-array-data v0, :array_0

    return-void

    nop

    :array_0
    .array-data 2
        0x61s
        0x62s
        0x63s
    .end array-data
.end method

.method public static TestFillArrayDataShort()V
    .locals 1

    const/4 v0, 0x3
    new-array v0, v0, [S

    fill-array-data v0, :array_0

    return-void

    nop

    :array_0
    .array-data 2
        0x64s
        0xc8s
        0x5s
    .end array-data
.end method

.method public static TestFillArrayDataFloat()V
    .locals 1

    const/4 v0, 0x2
    new-array v0, v0, [F

    fill-array-data v0, :array_0

    return-void

    nop

    :array_0
    .array-data 4
        0x3f800000
        0x40000000
    .end array-data
.end method

.method public static TestFillArrayDataDouble()V
    .locals 1

    const/4 v0, 0x2
    new-array v0, v0, [D

    fill-array-data v0, :array_0

    return-void

    nop

    :array_0
    .array-data 8
        0x3fb999999999999aL
        0x3ff8000000000000L
    .end array-data
.end method

