.class public Ldifferential/invocation_target;
.super Ljava/lang/Object;

.field public value:I

.method public static staticValue()I
    .locals 1
    const/16 v0, 0xb
    return v0
.end method

.method private directValue()I
    .locals 1
    const/16 v0, 0xc
    return v0
.end method

.method public virtualValue()I
    .locals 1
    const/16 v0, 0xd
    return v0
.end method

.method public touch()V
    .locals 0
    return-void
.end method

.method public static alwaysThrows()V
    .locals 2
    const/4 v0, 0x1
    const/4 v1, 0x0
    div-int v0, v0, v1
    return-void
.end method

.method public static returnOrThrow(I)V
    .locals 2
    if-eqz p0, :returns
    const/4 v0, 0x2
    sput v0, Ldifferential/invocation;->partitionValue:I
    const/4 v0, 0x1
    const/4 v1, 0x0
    div-int v0, v0, v1
    return-void

    :returns
    const/4 v0, 0x1
    sput v0, Ldifferential/invocation;->partitionValue:I
    return-void
.end method

.method public static mutateArrayThenReturnOrThrow([II)V
    .locals 3
    const/4 v0, 0x0
    if-eqz p1, :returns
    const/4 v1, 0x2
    aput v1, p0, v0
    const/4 v1, 0x1
    const/4 v2, 0x0
    div-int v1, v1, v2
    return-void

    :returns
    const/4 v1, 0x1
    aput v1, p0, v0
    return-void
.end method

.method public static loopForever()V
    .locals 0
    :loop
    goto :loop
.end method

.method public static loopReturningArray([I)[I
    .locals 0
    :loop
    goto :loop
.end method

.method public static loopWithObject(Ldifferential/invocation_target;)V
    .locals 0
    :loop
    goto :loop
.end method
