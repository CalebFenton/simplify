# Fresh-array default value for double[], read as RAW bits.
# Allocates double[1] and reads element 0's raw bits with Double.doubleToRawLongBits (NOT the
# canonicalizing doubleToLongBits -- the sign bit is the whole question). The falsifiable claim is
# that the default is POSITIVE zero (0x0000000000000000), not negative zero (0x8000000000000000).
# [JVMS] 2.3.2 / [JLS] 4.12.5: the default value for double is (positive) zero.
# Expected: java.lang.Long(0).  (-0.0 would read as -9223372036854775808.)
.class public LFx_FreshDoubleArrayDefaultBits;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 7

    const/4 v0, 0x1
    new-array v0, v0, [D        # fresh double[1]
    const/4 v1, 0x0
    aget-wide v2, v0, v1        # element 0, the default value (wide -> v2:v3)

    invoke-static {v2, v3}, Ljava/lang/Double;->doubleToRawLongBits(D)J
    move-result-wide v4
    invoke-static {v4, v5}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
    move-result-object v6
    return-object v6
.end method
