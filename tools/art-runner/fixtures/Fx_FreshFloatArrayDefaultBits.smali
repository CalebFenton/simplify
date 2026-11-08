# Fresh-array default value for float[], read as RAW bits.
# Allocates float[1] and reads element 0's raw bits with Float.floatToRawIntBits (NOT the
# canonicalizing floatToIntBits -- the sign bit is the whole question). The falsifiable claim is
# that the default is POSITIVE zero (0x00000000), not negative zero (0x80000000).
# [JVMS] 2.3.2 / [JLS] 4.12.5: the default value for float is (positive) zero.
# Expected: java.lang.Integer(0).  (-0.0f would read as -2147483648.)
.class public LFx_FreshFloatArrayDefaultBits;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 5

    const/4 v0, 0x1
    new-array v0, v0, [F        # fresh float[1]
    const/4 v1, 0x0
    aget v2, v0, v1            # element 0, the default value

    invoke-static {v2}, Ljava/lang/Float;->floatToRawIntBits(F)I
    move-result v3
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v4
    return-object v4
.end method
