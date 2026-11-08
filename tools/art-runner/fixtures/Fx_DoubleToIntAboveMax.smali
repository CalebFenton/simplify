# Double->Int narrowing, genuinely D-tagged. Source is a FINITE value above the
# Int range: 2^32 = 4294967296.0, built exactly via long-to-double so no bit pattern is hand-derived.
# [JLS] 5.1.3: a narrowing double->int saturates; a finite value > Integer.MAX_VALUE -> MAX_VALUE.
# Expected: java.lang.Integer(2147483647).
.class public LFx_DoubleToIntAboveMax;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const-wide v0, 0x100000000L   # 2^32 as a long
    long-to-double v0, v0          # 4294967296.0
    double-to-int v2, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
