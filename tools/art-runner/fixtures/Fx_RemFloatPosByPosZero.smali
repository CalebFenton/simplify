# WORKLIST ROW 4 -- rem-float (JVM counterpart frem). (+1.0f) rem (+0.0f).
# [JVMS] frem (IEEE-754 remainder): any finite value modulo zero is the invalid operation -> NaN;
# no exception is thrown, unlike the integral rem-int.
# Expected: java.lang.Float(NaN).
.class public LFx_RemFloatPosByPosZero;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    int-to-float v0, v0        # +1.0f
    const/4 v1, 0x0
    int-to-float v1, v1        # +0.0f
    rem-float v2, v0, v1

    invoke-static {v2}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;
    move-result-object v3
    return-object v3
.end method
