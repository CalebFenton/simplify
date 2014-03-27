.class Lfill_array_data_test;
.super Ljava/lang/Object;

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

