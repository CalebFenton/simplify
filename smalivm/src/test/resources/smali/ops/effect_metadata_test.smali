.class public Leffect_metadata_test;
.super Ljava/lang/Object;

.field private static volatile volatileValue:I
.field private static plainValue:I

.method public static declared-synchronized synchronizedPure()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

.method public static callSynchronized()I
    .locals 1
    invoke-static {}, Leffect_metadata_test;->synchronizedPure()I
    move-result v0
    return v0
.end method

.method public static readVolatile()I
    .locals 1
    sget v0, Leffect_metadata_test;->volatileValue:I
    return v0
.end method

.method public static callVolatileRead()I
    .locals 1
    invoke-static {}, Leffect_metadata_test;->readVolatile()I
    move-result v0
    return v0
.end method

.method public static writeVolatile()V
    .locals 1
    const/4 v0, 0x1
    sput v0, Leffect_metadata_test;->volatileValue:I
    return-void
.end method

.method public static readPlain()I
    .locals 1
    sget v0, Leffect_metadata_test;->plainValue:I
    return v0
.end method
