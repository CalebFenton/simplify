# PLAN 9 TASK 15 STEP 4c CONTROL -- `invoke-static` on a PRESENT class with a PRESENT method.
# The control for Fx_InvokeAbsentType and Fx_InvokeMissingMethod: same opcode, a defining class and a
# method that both resolve, so a batch in which invocation itself is broken would show here too.
# Expected: java.lang.String(1).
.class public LFx_InvokePresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x1
    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
    move-result-object v1
    return-object v1
.end method
