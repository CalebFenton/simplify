# Ordering: a missing method AND a null receiver on one invoke. The throw that arrives names the
# order, and it decides which handler is reachable: NullPointerException is an Exception,
# NoSuchMethodError an Error. Read with Fx_InvokeMissingMethod (no receiver).
# Expected: THREW java.lang.NoSuchMethodError if resolution precedes the null check.
.class public LFx_InvokeMissingMethodNullReceiver;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x0
    invoke-virtual {v0}, Ljava/lang/String;->definitelyNotAMethod()Ljava/lang/Object;

    # Only reached if neither condition threw.
    move-result-object v1
    return-object v1
.end method
