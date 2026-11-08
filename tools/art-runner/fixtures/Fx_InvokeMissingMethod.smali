# A missing method on a present class: java.lang.String resolves and the method does not.
# Read with the controls Fx_InvokePresentType (the method exists) and Fx_InvokeAbsentType (the class
# is absent). Expected: THREW java.lang.NoSuchMethodError.
.class public LFx_InvokeMissingMethod;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    invoke-static {}, Ljava/lang/String;->definitelyNotAMethod()Ljava/lang/Object;

    # Only reached if the call somehow resolved and returned.
    move-result-object v0
    return-object v0
.end method
