.class public Linit_trigger/driver;
.super Ljava/lang/Object;

.method public static allocateObject()V
    .registers 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static allocateQuiet()V
    .registers 1
    new-instance v0, Linit_trigger/quiet;
    invoke-direct {v0}, Linit_trigger/quiet;-><init>()V
    return-void
.end method

.method public static allocateThrowing()V
    .registers 1
    new-instance v0, Linit_trigger/throwing;
    return-void
.end method

.method public static allocateDependentAfterFailure()V
    .registers 1
    :start
    new-instance v0, Linit_trigger/throwing;
    :end
    .catch Ljava/lang/Throwable; {:start .. :end} :failed
    return-void
    :failed
    new-instance v0, Linit_trigger/dependent;
    return-void
.end method
