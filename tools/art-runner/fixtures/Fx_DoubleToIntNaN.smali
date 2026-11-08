# Double->Int narrowing, the genuinely D-tagged verifier-valid case.
# The source register holds a real double (const-wide of the NaN bit pattern), so this is a
# true narrowing conversion, NOT the sub-word bit-reinterpretation the section separates out.
# [JLS] 5.1.3: a narrowing double->int with a NaN source yields 0.
# Expected: java.lang.Integer(0).
.class public LFx_DoubleToIntNaN;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const-wide v0, 0x7ff8000000000000L   # canonical NaN
    double-to-int v2, v0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
