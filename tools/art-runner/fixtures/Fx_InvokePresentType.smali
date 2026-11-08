# Control for Fx_InvokeAbsentType and Fx_InvokeMissingMethod: the same opcode with a class and a
# method that both resolve. Expected: java.lang.String(1).
.class public LFx_InvokePresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x1
    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
    move-result-object v1
    return-object v1
.end method
