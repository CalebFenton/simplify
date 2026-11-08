# `invoke-static` naming a method on an absent class. Method resolution resolves the defining class
# first ([JVMS] 5.4.3.3). Read with the control Fx_InvokePresentType.
# Expected: THREW java.lang.NoClassDefFoundError.
.class public LFx_InvokeAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    invoke-static {}, Lcom/example/DefinitelyMissing;->absentMethod()Ljava/lang/Object;

    # Only reached if the call somehow resolved and returned.
    move-result-object v0
    return-object v0
.end method
