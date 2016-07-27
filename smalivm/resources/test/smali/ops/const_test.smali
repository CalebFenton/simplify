.class Lconst_test;
.super Ljava/lang/Object;

.method public static const4()V
    .locals 1

    const/4 v0, -0x3

    return-void
.end method

.method public static const16()V
    .locals 1

    const/16 v0, 0x4242

    return-void
.end method

.method public static const16Negative()V
    .locals 1

    const/16 v0, -0xf

    return-void
.end method

.method public static const()V
    .locals 1

    const v0, 0x42424242

    return-void
.end method


.method public static constHigh16()V
    .locals 1

    const/high16 v0, 0x42420000

    return-void
.end method

.method public static constWide16()V
    .locals 2

    const-wide/16 v0, 0x4242

    return-void
.end method

.method public static constWide32()V
    .locals 2

    const-wide/32 v0, 0x1b7740

    return-void
.end method

.method public static constWide()V
    .locals 2

    const-wide v0, 0x4242424242424242L

    return-void
.end method

.method public static constWideHigh16()V
    .locals 2

    const-wide/high16 v0, 0x4242000000000000L

    return-void
.end method

.method public static constString()V
    .locals 1

    const-string v0, "When I need to identify rebels, I look for men with principles."

    return-void
.end method

.method public static constStringJumbo()V
    .locals 1

    const-string/jumbo v0, "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles."

    return-void
.end method

.method public static constClassLocal()V
    .locals 1

    const-class v0, Lconst_test;

    return-void
.end method

.method public static constClassRemote()V
    .locals 1

    const-class v0, Ljava/lang/Object;

    return-void
.end method

.method public static constClassUnknown()V
    .locals 1

    :try_start_0
    const-class v0, Lunknown/class;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method
