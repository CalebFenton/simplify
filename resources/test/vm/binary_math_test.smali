.class public Lbinary_math_test;
.super Ljava/lang/Object;

.method public static TestAddInt()V
    .locals 2

    const/4 v0, -0x3
    const/4 v1, 0x7

    add-int v0, v0, v1

    # -3 + 7 = 4
    return-void
.end method

.method public static TestSubInt()V
    .locals 2

    const/4 v0, -0x3
    const/4 v1, 0x7

    sub-int v0, v1, v0

    # 7 - (-3) = 10
    return-void
.end method

.method public static TestMulInt()V
    .locals 2

    const/4 v0, -0x3
    const/4 v1, 0x7

    mul-int v0, v0, v1

    # -3 * 7 = -15
    return-void
.end method

.method public static TestDivInt()V
    .locals 2

    const/4 v0, 0x6
    const/4 v1, 0x2

    div-int v0, v0, v1

    # 6 / 2 = 3
    return-void
.end method

.method public static TestRemInt()V
    .locals 2

    const/4 v0, 0x7
    const/4 v1, 0x5

    rem-int v0, v0, v1

    # 7 % 5 = 2
    return-void
.end method

.method public static TestAndInt()V
    .locals 2

    const/4 v0, 0x2
    const/4 v1, 0x7

    and-int v0, v0, v1

    # 7 & 2 == 2
    return-void
.end method

.method public static TestOrInt()V
    .locals 2

    const/4 v0, 0x5
    const/4 v1, 0x2

    or-int v0, v0, v1

    # 101 (5) | 010 (2) == 111 (7)
    return-void
.end method

.method public static TestXorInt()V
    .locals 2

    const/4 v0, 0x7
    const/4 v1, 0x2

    xor-int v0, v0, v1

    # 111 (7) ^ 010 (2) = 101 (5)
    return-void
.end method

.method public static TestShlInt()V
    .locals 2

    const/4 v0, 0x5
    const/4 v1, 0x2

    shl-int v0, v0, v1

    # 5 << 2 = 20
    return-void
.end method

.method public static TestShrInt()V
    .locals 2

    const/4 v0, 0x7
    const/4 v1, 0x2

    shr-int v0, v0, v1

    # 7 >> 2 == 1
    return-void
.end method

.method public static TestUshrInt()V
    .locals 2

    const/4 v0, 0x7
    const/4 v1, 0x2

    and-int v0, v0, v1

    # 7 >>> 2 == 2
    return-void
.end method