.class public final Lconst_test;
.super Ljava/lang/Object;

.method public static TestConst4()I
    .locals 1

    const/4 v0, -0x3

    const/4 v1, 0x7

    return v0
.end method

.method public static TestConst16()I
    .locals 1

    const/16 v0, 0x4242

    return v0
.end method


.method public static TestConst()I
    .locals 1

    const v0, 0x42424242

    return v0
.end method


.method public static TestConstHigh16()I
    .locals 1

    const/high16 v0, 0x42420000

    return v0
.end method

.method public static TestConstWide16()J
    .locals 2

    const-wide/16 v0, 0x4242

    return v0
.end method

.method public static TestConstWide32()J
    .locals 2

    const-wide/32 v0, 0x1b7740

    return v0
.end method

.method public static TestConstWide()J
    .locals 2

    const-wide v0, 0x4242424242424242L

    return v0
.end method

.method public static TestConstWideHigh16()J
    .locals 2

    const-wide/high16 v0, 0x4242000000000000L

    return v0
.end method

.method public static TestConstString()I
    .locals 1

    const-string v0, "When I need to identify rebels, I look for men with principles"

    return v0
.end method

.method public static TestConstStringJumbo()Ljava/lang/String;
    .locals 1

    const-string/jumbo v0, "When I need to identify rebels, I look for men with principles"

    return v0
.end method

.method public static TestConstClass()Ljava/lang/Class;
    .locals 1

    const-class v0, Lconst_test;

    return v0
.end method

