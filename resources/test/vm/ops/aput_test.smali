.class Laput_test;
.super Ljava/lang/Object;

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