.class Lbinary_math_test;
.super Ljava/lang/Object;


################ Integer ################
.method public static AddInt()V
    .locals 2

    add-int v0, v0, v1

    return-void
.end method

.method public static AddInt2Addr()V
    .locals 2

    add-int/2addr v0, v1

    return-void
.end method

.method public static AddIntLit8()V
    .locals 1

    add-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static AddIntLit16()V
    .locals 1

    add-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static MulInt()V
    .locals 2

    mul-int v0, v0, v1

    return-void
.end method

.method public static MulInt2Addr()V
    .locals 2

    mul-int/2addr v0, v1

    return-void
.end method

.method public static MulIntLit8()V
    .locals 1

    mul-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static MulIntLit16()V
    .locals 1

    mul-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static DivInt()V
    .locals 2

    div-int v0, v0, v1

    return-void
.end method

.method public static DivIntWithCatch()V
    .locals 2

    :try_start_0
    # Floating point percision literals do not throw ArithmeticException
    div-int v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static DivInt2Addr()V
    .locals 2

    div-int/2addr v0, v1

    return-void
.end method

.method public static DivIntLit8()V
    .locals 1

    div-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static DivIntLit16()V
    .locals 1

    div-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static RemInt()V
    .locals 2

    rem-int v0, v0, v1

    return-void
.end method

.method public static RemInt2Addr()V
    .locals 2

    rem-int/2addr v0, v1

    return-void
.end method

.method public static RemIntLit8()V
    .locals 1

    rem-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static RemIntLit16()V
    .locals 1

    rem-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static AndInt()V
    .locals 2

    and-int v0, v0, v1

    return-void
.end method

.method public static AndInt2Addr()V
    .locals 2

    and-int/2addr v0, v1

    return-void
.end method

.method public static AndIntLit8()V
    .locals 1

    and-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static AndIntLit16()V
    .locals 1

    and-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static OrInt()V
    .locals 2

    or-int v0, v0, v1

    return-void
.end method

.method public static OrInt2Addr()V
    .locals 2

    or-int/2addr v0, v1

    return-void
.end method

.method public static OrIntLit8()V
    .locals 1

    or-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static OrIntLit16()V
    .locals 1

    or-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static XorInt()V
    .locals 2

    xor-int v0, v0, v1

    return-void
.end method

.method public static XorInt2Addr()V
    .locals 2

    xor-int/2addr v0, v1

    return-void
.end method

.method public static XorIntLit8()V
    .locals 1

    xor-int/lit8 v0, v0, 0x3

    return-void
.end method

.method public static XorIntLit16()V
    .locals 1

    xor-int/lit16 v0, v0, 0x10

    return-void
.end method

.method public static ShlInt()V
    .locals 2

    shl-int v0, v0, v1

    return-void
.end method

.method public static ShlInt2Addr()V
    .locals 2

    shl-int/2addr v0, v1

    return-void
.end method

.method public static ShlIntLit8()V
    .locals 1

    shl-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static ShrInt()V
    .locals 2

    shr-int v0, v0, v1

    return-void
.end method

.method public static ShrInt2Addr()V
    .locals 2

    shr-int/2addr v0, v1

    return-void
.end method

.method public static ShrIntLit8()V
    .locals 1

    shr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static UshrInt()V
    .locals 2

    ushr-int v0, v0, v1

    return-void
.end method

.method public static UshrInt2Addr()V
    .locals 2

    ushr-int/2addr v0, v1

    return-void
.end method

.method public static UshrIntLit8()V
    .locals 1

    ushr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static SubInt()V
    .locals 2

    sub-int v0, v0, v1

    return-void
.end method

.method public static SubInt2Addr()V
    .locals 2

    sub-int/2addr v0, v1

    return-void
.end method

.method public static RSubInt()V
    .locals 2

    rsub-int v0, v0, 0xff

    return-void
.end method

.method public static RSubIntLit8()V
    .locals 1

    rsub-int/lit8 v0, v0, 0xf

    return-void
.end method




################ Long ################
.method public static AddLong()V
    .locals 4

    add-long v0, v0, v2

    return-void
.end method

.method public static AddLong2Addr()V
    .locals 4

    add-long/2addr v0, v2

    return-void
.end method

.method public static SubLong()V
    .locals 4

    sub-long v0, v0, v2

    return-void
.end method

.method public static SubLong2Addr()V
    .locals 2

    sub-long/2addr v0, v2

    return-void
.end method

.method public static MulLong()V
    .locals 4

    mul-long v0, v0, v2

    return-void
.end method

.method public static MulLong2Addr()V
    .locals 2

    mul-long/2addr v0, v2

    return-void
.end method

.method public static DivLong()V
    .locals 4

    div-long v0, v0, v2

    return-void
.end method

.method public static DivLong2Addr()V
    .locals 2

    div-long/2addr v0, v2

    return-void
.end method

.method public static RemLong()V
    .locals 4

    rem-long v0, v0, v2

    return-void
.end method

.method public static RemLong2Addr()V
    .locals 2

    rem-long/2addr v0, v2

    return-void
.end method

.method public static AndLong()V
    .locals 4

    and-long v0, v0, v2

    return-void
.end method

.method public static AndLong2Addr()V
    .locals 2

    and-long/2addr v0, v2

    return-void
.end method

.method public static OrLong()V
    .locals 4

    or-long v0, v0, v2

    return-void
.end method

.method public static OrLong2Addr()V
    .locals 2

    or-long/2addr v0, v2

    return-void
.end method

.method public static XorLong()V
    .locals 4

    xor-long v0, v0, v2

    return-void
.end method

.method public static XorLong2Addr()V
    .locals 2

    xor-long/2addr v0, v2

    return-void
.end method

.method public static ShlLong()V
    .locals 4

    shl-long v0, v0, v2

    return-void
.end method

.method public static ShlLong2Addr()V
    .locals 2

    shl-long/2addr v0, v2

    return-void
.end method

.method public static ShrLong()V
    .locals 4

    shr-long v0, v0, v2

    return-void
.end method

.method public static ShrLong2Addr()V
    .locals 2

    shr-long/2addr v0, v2

    return-void
.end method

.method public static UshrLong()V
    .locals 4

    ushr-long v0, v0, v2

    return-void
.end method

.method public static UshrLong2Addr()V
    .locals 2

    ushr-long/2addr v0, v2

    return-void
.end method




################ Float ################
.method public static AddFloat()V
    .locals 2

    add-float v0, v0, v1

    return-void
.end method

.method public static AddFloat2Addr()V
    .locals 2

    add-float/2addr v0, v1

    return-void
.end method

.method public static SubFloat()V
    .locals 2

    sub-float v0, v0, v1

    return-void
.end method

.method public static SubFloat2Addr()V
    .locals 2

    sub-float/2addr v0, v1

    return-void
.end method

.method public static MulFloat()V
    .locals 2

    mul-float v0, v0, v1

    return-void
.end method

.method public static MulFloat2Addr()V
    .locals 2

    mul-float/2addr v0, v1

    return-void
.end method

.method public static DivFloat()V
    .locals 2

    div-float v0, v0, v1

    return-void
.end method

.method public static DivFloat2Addr()V
    .locals 2

    div-float/2addr v0, v1

    return-void
.end method

.method public static RemFloat()V
    .locals 2

    rem-float v0, v0, v1

    return-void
.end method

.method public static RemFloat2Addr()V
    .locals 2

    rem-float/2addr v0, v1

    return-void
.end method




################ Double ################
.method public static AddDouble()V
    .locals 4

    add-double v0, v0, v2

    return-void
.end method

.method public static AddDouble2Addr()V
    .locals 4

    add-double/2addr v0, v2

    return-void
.end method

.method public static SubDouble()V
    .locals 4

    sub-double v0, v0, v2

    return-void
.end method

.method public static SubDouble2Addr()V
    .locals 4

    sub-double/2addr v0, v2

    return-void
.end method

.method public static MulDouble()V
    .locals 4

    mul-double v0, v0, v2

    return-void
.end method

.method public static MulDouble2Addr()V
    .locals 4

    mul-double/2addr v0, v2

    return-void
.end method

.method public static DivDouble()V
    .locals 4

    div-double v0, v0, v2

    return-void
.end method

.method public static DivDouble2Addr()V
    .locals 4

    div-double/2addr v0, v2

    return-void
.end method

.method public static RemDouble()V
    .locals 4

    rem-double v0, v0, v2

    return-void
.end method

.method public static RemDouble2Addr()V
    .locals 4

    rem-double/2addr v0, v2

    return-void
.end method
