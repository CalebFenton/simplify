.class public abstract Lskeleton/Skeleton;
.super Ljava/lang/Object;

.method public static aNoOp()V
    .registers 0

    return-void
.end method

.method public abstract bodyless()V
.end method

.method public static changed()I
    .registers 1

    const/4 v0, 0x0
    return v0
.end method

.method public static loop()V
    .registers 0

    :loop
    goto :loop
.end method
