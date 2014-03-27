.class Laget_test;
.super Ljava/lang/Object;

.method public static TestArrayGetUninitializedInt()V
    .locals 2

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    aget v0, v0, v1

    return-void
.end method

.method public static TestArrayGet()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    const/4 v2, 0x4
    aput v2, v0, v1 # v0[0] = 4

    aget v0, v0, v1

    return-void
.end method

.method public static TestArrayGetWide()V
    .locals 4

    const/4 v0, 0x1
    new-array v0, v0, [J

    const/4 v1, 0x0
    const-wide v2, 0x1000000000L
    aput-wide v2, v0, v1 # v0[0] = 4

    aget-wide v2, v0, v1

    return-void
.end method

.method public static TestArrayGetObject()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [Laget_test;

    const/4 v1, 0x0
    new-instance v2, Laget_test;
    aput-object v2, v0, v1 # v0[0] = UninitializedInstance("Laget_test;");

    aget-object v0, v0, v1

    return-void
.end method

.method public static TestArrayGetBoolean()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [Z

    const/4 v1, 0x0
    const/4 v2, 0x1
    aput-boolean v2, v0, v1 # v0[0] = true

    aget-boolean v0, v0, v1

    return-void
.end method

.method public static TestArrayGetByte()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [B

    const/4 v1, 0x0
    const/4 v2, 0x5
    aput-byte v2, v0, v1 # v0[0] = 5

    aget-byte v0, v0, v1

    return-void
.end method

.method public static TestArrayGetChar()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [C

    const/4 v1, 0x0
    const/16 v2, 0x61
    aput-char v2, v0, v1 # v0[0] = 'a'

    aget-char v0, v0, v1

    return-void
.end method

.method public static TestArrayGetShort()V
    .locals 3

    const/4 v0, 0x1
    new-array v0, v0, [I

    const/4 v1, 0x0
    const/16 v2, 0x61
    aput-short v2, v0, v1 # v0[0] = 61

    aget-short v0, v0, v1

    return-void
.end method
