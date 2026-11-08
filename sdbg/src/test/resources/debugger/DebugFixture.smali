.class public abstract LDebugFixture;
.super Ljava/lang/Object;
.source "DebugFixture.java"

.field public static total:I

.method public abstract bodyless()V
.end method

.method public static exercise()I
    .locals 2
    const/4 v0, 0x2
    const/4 v1, 0x3
    invoke-static {v0, v1}, LDebugFixture;->add(II)I
    move-result v0
    sput v0, LDebugFixture;->total:I
    return v0
.end method

.method public static add(II)I
    .locals 1
    add-int v0, p0, p1
    return v0
.end method

.method public static packed()I
    .locals 1
    .prologue
    .line 10
    const/4 v0, 0x0
    .local v0, "value":I
    packed-switch v0, :switch_data
    goto :after_data
    :case
    .line 11
    const/4 v0, 0x1
    goto :after_data
    :switch_data
    .packed-switch 0x0
        :case
    .end packed-switch
    :after_data
    .line 12
    return v0
.end method

.method public static array()[I
    .locals 2
    const/4 v0, 0x2
    new-array v1, v0, [I
    fill-array-data v1, :array_data
    goto :after_data
    :array_data
    .array-data 4
        0x12
        0x34
    .end array-data
    :after_data
    return-object v1
.end method

.method public static sparse()I
    .locals 1
    const/4 v0, 0x2
    sparse-switch v0, :switch_data
    goto :after_data
    :case
    const/4 v0, 0x3
    goto :after_data
    :switch_data
    .sparse-switch
        0x2 -> :case
    .end sparse-switch
    :after_data
    return v0
.end method

.method public static frameworkCall()Ljava/lang/Object;
    .locals 1
    new-instance v0, LDebugFixtureList;
    invoke-direct {v0}, LDebugFixtureList;-><init>()V
    return-object v0
.end method
