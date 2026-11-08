# Double->Int narrowing, genuinely D-tagged. Source is a FINITE value below the
# Int range: -(2^32) = -4294967296.0, built via long-to-double then neg-double.
# [JLS] 5.1.3: a narrowing double->int saturates; a finite value < Integer.MIN_VALUE -> MIN_VALUE.
# Expected: java.lang.Integer(-2147483648).
.class public LFx_DoubleToIntBelowMin;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const-wide v0, 0x100000000L   # 2^32 as a long
    long-to-double v0, v0          # 4294967296.0
    neg-double v0, v0              # -4294967296.0
    double-to-int v2, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
