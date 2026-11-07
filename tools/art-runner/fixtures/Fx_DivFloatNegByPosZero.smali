# WORKLIST ROW 4 -- div-float (JVM counterpart fdiv). (-1.0f) / (+0.0f).
# [JVMS] fdiv (IEEE-754): a negative dividend over positive zero -> -Infinity.
# Expected: java.lang.Float(-Infinity).
.class public LFx_DivFloatNegByPosZero;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    int-to-float v0, v0        # +1.0f
    neg-float v0, v0           # -1.0f
    const/4 v1, 0x0
    int-to-float v1, v1        # +0.0f
    div-float v2, v0, v1

    invoke-static {v2}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;
    move-result-object v3
    return-object v3
.end method
