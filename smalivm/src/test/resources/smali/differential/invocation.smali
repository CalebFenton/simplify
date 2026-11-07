.class public Ldifferential/invocation;
.super Ljava/lang/Object;

.field public static partitionValue:I

.method public static callStatic()I
    .locals 1
    invoke-static {}, Ldifferential/invocation_target;->staticValue()I
    move-result v0
    return v0
.end method

.method public static callDirect()I
    .locals 2
    new-instance v0, Ldifferential/invocation_target;
    invoke-direct {v0}, Ldifferential/invocation_target;->directValue()I
    move-result v1
    return v1
.end method

.method public static callVirtual()I
    .locals 2
    new-instance v0, Ldifferential/invocation_target;
    invoke-virtual {v0}, Ldifferential/invocation_target;->virtualValue()I
    move-result v1
    return v1
.end method

.method public static callInterface()I
    .locals 2
    new-instance v0, Ldifferential/invocation_impl;
    invoke-interface {v0}, Ldifferential/invocation_interface;->interfaceValue()I
    move-result v1
    return v1
.end method

.method public static callHandledThrow()I
    .locals 2
    :try_start
    invoke-static {}, Ldifferential/invocation_target;->alwaysThrows()V
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :handler
    const/4 v0, -0x1
    return v0

    :handler
    move-exception v1
    const/16 v0, 0x16
    return v0
.end method

.method public static callEscapingThrow()V
    .locals 0
    invoke-static {}, Ldifferential/invocation_target;->alwaysThrows()V
    return-void
.end method

.method public static callAbortedArrayCallee()[I
    .locals 4
    const/4 v0, 0x1
    new-array v1, v0, [I
    const/4 v2, 0x0
    const/4 v3, 0x5
    aput v3, v1, v2
    invoke-static {v1}, Ldifferential/invocation_target;->loopReturningArray([I)[I
    move-result-object v3
    return-object v3
.end method

.method public static ambiguousReceiver(Linvoke_dispatch_base;)I
    .locals 1
    const/4 v0, 0x1
    invoke-virtual {p0}, Linvoke_dispatch_base;->value()I
    move-result p0
    if-ne p0, v0, :other
    const/4 p0, 0x7
    return p0

    :other
    const/4 p0, 0x7
    return p0
.end method

.method public static nullReceiverUsesHandler()I
    .locals 3
    const/4 v0, 0x0
    :try_start
    invoke-virtual {v0}, Ldifferential/invocation_target;->touch()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v1, 0x7
    goto :return

    :handler
    move-exception v2
    const/4 v1, 0x7

    :return
    return v1
.end method

.method public static returnPartitionIsNotContaminated(I)I
    .locals 1
    invoke-static {p0}, Ldifferential/invocation_target;->returnOrThrow(I)V
    sget v0, Ldifferential/invocation;->partitionValue:I
    return v0
.end method

.method public static handledPartitionIsNotContaminated(I)I
    .locals 2
    :try_start
    invoke-static {p0}, Ldifferential/invocation_target;->returnOrThrow(I)V
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :handler
    const/4 v0, 0x2
    return v0

    :handler
    move-exception v1
    sget v0, Ldifferential/invocation;->partitionValue:I
    return v0
.end method

.method public static arrayPartitionsDoNotContaminate(I)I
    .locals 5
    const/4 v0, 0x1
    new-array v0, v0, [I
    :try_start
    invoke-static {v0, p0}, Ldifferential/invocation_target;->mutateArrayThenReturnOrThrow([II)V
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :handler
    const/4 v1, 0x0
    aget v2, v0, v1
    const/4 v3, 0x1
    if-ne v2, v3, :normal_other
    const/16 v2, 0xa
    goto :return
    :normal_other
    const/16 v2, 0xa
    goto :return

    :handler
    move-exception v4
    const/4 v1, 0x0
    aget v2, v0, v1
    const/4 v3, 0x2
    if-ne v2, v3, :handler_other
    const/16 v2, 0xa
    goto :return
    :handler_other
    const/16 v2, 0xa

    :return
    return v2
.end method

.method public static opaqueCollapseKeepsReference()Ldifferential/invocation_target;
    .locals 1
    new-instance v0, Ldifferential/invocation_target;
    invoke-static {v0}, Ldifferential/invocation_target;->loopWithObject(Ldifferential/invocation_target;)V
    return-object v0
.end method

.method public static missingClass()V
    .locals 0
    invoke-static {}, Ldifferential/absent;->missing()V
    return-void
.end method

.method public static missingMethod()V
    .locals 0
    invoke-static {}, Ldifferential/invocation_target;->missing()V
    return-void
.end method

.method public static entryPointTruncatesAfterCall(I)I
    .locals 2
    invoke-static {}, Ldifferential/invocation_target;->staticValue()I
    move-result v0
    if-eqz p0, :return
    :loop
    goto :loop

    :return
    return v0
.end method

.method public static firstDeclaredHandlerWins()I
    .locals 2
    const/4 v0, 0x1
    const/4 v1, 0x0
    :try_start
    div-int v0, v0, v1
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :broad
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :nearest
    const/4 v0, 0x7
    goto :return

    :broad
    move-exception v1
    const/4 v0, 0x7
    goto :return

    :nearest
    move-exception v1
    const/4 v0, 0x7

    :return
    return v0
.end method
