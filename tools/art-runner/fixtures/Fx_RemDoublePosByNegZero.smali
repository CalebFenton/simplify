# Rem-double (JVM counterpart drem). (+1.0) rem (-0.0).
# [JVMS] drem (IEEE-754 remainder): remainder by zero is NaN regardless of the zero's sign.
# Expected: java.lang.Double(NaN).
.class public LFx_RemDoublePosByNegZero;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 7

    const/4 v6, 0x1
    int-to-double v0, v6       # v0:v1 = +1.0
    const/4 v6, 0x0
    int-to-double v2, v6       # v2:v3 = +0.0
    neg-double v2, v2          # v2:v3 = -0.0
    rem-double v4, v0, v2      # v4:v5

    invoke-static {v4, v5}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;
    move-result-object v6
    return-object v6
.end method
