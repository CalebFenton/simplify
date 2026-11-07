.class public Lscheduler_regression;
.super Ljava/lang/Object;

.field public static state:I

.method public static callForkingCallee()I
    .registers 1
    invoke-static {}, Lscheduler_regression;->forkingCallee()V
    sget v0, Lscheduler_regression;->state:I
    return v0
.end method

.method public static forkingCallee()V
    .registers 0
    nop
    return-void
.end method

.method public static armFailureSibling()I
    .registers 1
    nop
    return v0
.end method
