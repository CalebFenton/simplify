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

# quiet's initializer runs at the new-instance (address 1), which then runs again.
.method public static triggerAfterLiteral()V
    .registers 1
    const/4 v0, 0x0
    new-instance v0, Linit_trigger/quiet;
    return-void
.end method

# The const/4 at address 3 runs right after the call returns.
.method public static opAfterACall()V
    .registers 1
    invoke-static {}, Linit_trigger/driver;->allocateObject()V
    const/4 v0, 0x0
    return-void
.end method
