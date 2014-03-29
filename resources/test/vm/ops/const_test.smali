.class Lconst_test;
.super Ljava/lang/Object;

.method public static TestConst4()V
    .locals 1

    const/4 v0, -0x3

    return-void
.end method

.method public static TestConst16()V
    .locals 1

    const/16 v0, 0x4242

    return-void
.end method


.method public static TestConst()V
    .locals 1

    const v0, 0x42424242

    return-void
.end method


.method public static TestConstHigh16()V
    .locals 1

    const/high16 v0, 0x42420000

    return-void
.end method

.method public static TestConstWide16()V
    .locals 2

    const-wide/16 v0, 0x4242

    return-void
.end method

.method public static TestConstWide32()V
    .locals 2

    const-wide/32 v0, 0x1b7740

    return-void
.end method

.method public static TestConstWide()V
    .locals 2

    const-wide v0, 0x4242424242424242L

    return-void
.end method

.method public static TestConstWideHigh16()V
    .locals 2

    const-wide/high16 v0, 0x4242000000000000L

    return-void
.end method

.method public static TestConstString()V
    .locals 1

    const-string v0, "When I need to identify rebels, I look for men with principles"

    return-void
.end method

.method public static TestConstStringJumbo()V
    .locals 1

    const-string/jumbo v0, "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles"

    return-void
.end method

.method public static TestConstClassLocal()V
    .locals 1

    const-class v0, Lconst_test;

    return-void
.end method

.method public static TestConstClassRemote()V
    .locals 1

    const-class v0, Ljava/lang/Object;

    return-void
.end method

