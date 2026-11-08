# Double->Int narrowing, genuinely D-tagged. Source is -0.5 (neg-double of 0.5).
# [JLS] 5.1.3: a narrowing double->int rounds TOWARD ZERO; -0.5 -> 0 (not -1).
# Expected: java.lang.Integer(0).
.class public LFx_DoubleToIntNegHalf;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const-wide v0, 0x3fe0000000000000L   # 0.5
    neg-double v0, v0                     # -0.5
    double-to-int v2, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
