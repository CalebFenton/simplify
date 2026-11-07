# WORKLIST ROW 4 -- F/D divide-by-zero routing (JVM counterparts fdiv/ddiv/frem/drem).
# div-float of a positive value by NEGATIVE zero. This is the IEEE-754 crux: the sign of the
# zero divisor flips the sign of the infinity, and no ArithmeticException is thrown (unlike the
# integral div-by-zero, which does throw). -0.0f is built as neg-float(0.0f) so the operand is
# unambiguously a float to the verifier.
# [JVMS] fdiv (IEEE-754): (+x) / (-0.0) = -Infinity.
# Expected: java.lang.Float(-Infinity).
.class public LFx_DivFloatPosByNegZero;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    int-to-float v0, v0        # +1.0f

    const/4 v1, 0x0
    int-to-float v1, v1        # 0.0f
    neg-float v1, v1           # -0.0f

    div-float v2, v0, v1       # (+1.0f) / (-0.0f)

    invoke-static {v2}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;
    move-result-object v3
    return-object v3
.end method
