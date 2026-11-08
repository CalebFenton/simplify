# Control for Fx_SGetAbsentType: `sget` of java.lang.Integer.MAX_VALUE, a field whose class resolves.
# Expected: java.lang.Integer(2147483647).
.class public LFx_SGetPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    sget v0, Ljava/lang/Integer;->MAX_VALUE:I

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1
    return-object v1
.end method
