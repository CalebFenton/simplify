# Double->Int narrowing, genuinely D-tagged. Source is +Infinity (bit pattern).
# [JLS] 5.1.3: a narrowing double->int saturates at the Int bounds; +Infinity -> Integer.MAX_VALUE.
# Expected: java.lang.Integer(2147483647).
.class public LFx_DoubleToIntPosInf;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const-wide v0, 0x7ff0000000000000L   # +Infinity
    double-to-int v2, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
