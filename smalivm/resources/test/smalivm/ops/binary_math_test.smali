.class Lbinary_math_test;
.super Ljava/lang/Object;


################ Integer ################
.method public static addInt()V
    .locals 2

    add-int v0, v0, v1

    return-void
.end method

.method public static addInt2Addr()V
    .locals 2

    add-int/2addr v0, v1

    return-void
.end method

.method public static addIntLit8()V
    .locals 1

    add-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static addIntLit16()V
    .locals 1

    add-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static mulInt()V
    .locals 2

    mul-int v0, v0, v1

    return-void
.end method

.method public static mulInt2Addr()V
    .locals 2

    mul-int/2addr v0, v1

    return-void
.end method

.method public static mulIntLit8()V
    .locals 1

    mul-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static mulIntLit16()V
    .locals 1

    mul-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static divInt()V
    .locals 2

    div-int v0, v0, v1

    return-void
.end method

.method public static divInt2Addr()V
    .locals 2

    div-int/2addr v0, v1

    return-void
.end method

.method public static divIntLit8()V
    .locals 1

    div-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static divIntLit16()V
    .locals 1

    div-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static divIntWithCatch()V
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

.method public static remInt()V
    .locals 2

    rem-int v0, v0, v1

    return-void
.end method

.method public static remInt2Addr()V
    .locals 2

    rem-int/2addr v0, v1

    return-void
.end method

.method public static remIntLit8()V
    .locals 1

    rem-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static remIntLit16()V
    .locals 1

    rem-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static andInt()V
    .locals 2

    and-int v0, v0, v1

    return-void
.end method

.method public static andInt2Addr()V
    .locals 2

    and-int/2addr v0, v1

    return-void
.end method

.method public static andIntLit8()V
    .locals 1

    and-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static andIntLit16()V
    .locals 1

    and-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static orInt()V
    .locals 2

    or-int v0, v0, v1

    return-void
.end method

.method public static orInt2Addr()V
    .locals 2

    or-int/2addr v0, v1

    return-void
.end method

.method public static orIntLit8()V
    .locals 1

    or-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static orIntLit16()V
    .locals 1

    or-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static xorInt()V
    .locals 2

    xor-int v0, v0, v1

    return-void
.end method

.method public static xorInt2Addr()V
    .locals 2

    xor-int/2addr v0, v1

    return-void
.end method

.method public static xorIntLit8()V
    .locals 1

    xor-int/lit8 v0, v0, 0x3

    return-void
.end method

.method public static xorIntLit16()V
    .locals 1

    xor-int/lit16 v0, v0, 0x10

    return-void
.end method

.method public static shlInt()V
    .locals 2

    shl-int v0, v0, v1

    return-void
.end method

.method public static shlInt2Addr()V
    .locals 2

    shl-int/2addr v0, v1

    return-void
.end method

.method public static shlIntLit8()V
    .locals 1

    shl-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static shrInt()V
    .locals 2

    shr-int v0, v0, v1

    return-void
.end method

.method public static shrInt2Addr()V
    .locals 2

    shr-int/2addr v0, v1

    return-void
.end method

.method public static shrIntLit8()V
    .locals 1

    shr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static ushrInt()V
    .locals 2

    ushr-int v0, v0, v1

    return-void
.end method

.method public static ushrInt2Addr()V
    .locals 2

    ushr-int/2addr v0, v1

    return-void
.end method

.method public static ushrIntLit8()V
    .locals 1

    ushr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static subInt()V
    .locals 2

    sub-int v0, v0, v1

    return-void
.end method

.method public static subInt2Addr()V
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
.method public static addLong()V
    .locals 4

    add-long v0, v0, v2

    return-void
.end method

.method public static addLong2Addr()V
    .locals 4

    add-long/2addr v0, v2

    return-void
.end method

.method public static subLong()V
    .locals 4

    sub-long v0, v0, v2

    return-void
.end method

.method public static subLong2Addr()V
    .locals 2

    sub-long/2addr v0, v2

    return-void
.end method

.method public static mulLong()V
    .locals 4

    mul-long v0, v0, v2

    return-void
.end method

.method public static mulLong2Addr()V
    .locals 2

    mul-long/2addr v0, v2

    return-void
.end method

.method public static divLong()V
    .locals 4

    div-long v0, v0, v2

    return-void
.end method

.method public static divLong2Addr()V
    .locals 2

    div-long/2addr v0, v2

    return-void
.end method

.method public static divLongWithCatch()V
    .locals 2

    :try_start_0
    div-long v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static remLong()V
    .locals 4

    rem-long v0, v0, v2

    return-void
.end method

.method public static remLong2Addr()V
    .locals 2

    rem-long/2addr v0, v2

    return-void
.end method

.method public static andLong()V
    .locals 4

    and-long v0, v0, v2

    return-void
.end method

.method public static andLong2Addr()V
    .locals 2

    and-long/2addr v0, v2

    return-void
.end method

.method public static orLong()V
    .locals 4

    or-long v0, v0, v2

    return-void
.end method

.method public static orLong2Addr()V
    .locals 2

    or-long/2addr v0, v2

    return-void
.end method

.method public static xorLong()V
    .locals 4

    xor-long v0, v0, v2

    return-void
.end method

.method public static xorLong2Addr()V
    .locals 2

    xor-long/2addr v0, v2

    return-void
.end method

.method public static shlLong()V
    .locals 4

    shl-long v0, v0, v2

    return-void
.end method

.method public static shlLong2Addr()V
    .locals 2

    shl-long/2addr v0, v2

    return-void
.end method

.method public static shrLong()V
    .locals 4

    shr-long v0, v0, v2

    return-void
.end method

.method public static shrLong2Addr()V
    .locals 2

    shr-long/2addr v0, v2

    return-void
.end method

.method public static ushrLong()V
    .locals 4

    ushr-long v0, v0, v2

    return-void
.end method

.method public static ushrLong2Addr()V
    .locals 2

    ushr-long/2addr v0, v2

    return-void
.end method




################ Float ################
.method public static addFloat()V
    .locals 2

    add-float v0, v0, v1

    return-void
.end method

.method public static addFloat2Addr()V
    .locals 2

    add-float/2addr v0, v1

    return-void
.end method

.method public static subFloat()V
    .locals 2

    sub-float v0, v0, v1

    return-void
.end method

.method public static subFloat2Addr()V
    .locals 2

    sub-float/2addr v0, v1

    return-void
.end method

.method public static mulFloat()V
    .locals 2

    mul-float v0, v0, v1

    return-void
.end method

.method public static mulFloat2Addr()V
    .locals 2

    mul-float/2addr v0, v1

    return-void
.end method

.method public static divFloat()V
    .locals 2

    div-float v0, v0, v1

    return-void
.end method

.method public static divFloat2Addr()V
    .locals 2

    div-float/2addr v0, v1

    return-void
.end method

.method public static divFloatWithCatch()V
    .locals 2

    :try_start_0
    div-float v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method


.method public static remFloat()V
    .locals 2

    rem-float v0, v0, v1

    return-void
.end method

.method public static remFloat2Addr()V
    .locals 2

    rem-float/2addr v0, v1

    return-void
.end method




################ Double ################
.method public static addDouble()V
    .locals 4

    add-double v0, v0, v2

    return-void
.end method

.method public static addDouble2Addr()V
    .locals 4

    add-double/2addr v0, v2

    return-void
.end method

.method public static subDouble()V
    .locals 4

    sub-double v0, v0, v2

    return-void
.end method

.method public static subDouble2Addr()V
    .locals 4

    sub-double/2addr v0, v2

    return-void
.end method

.method public static mulDouble()V
    .locals 4

    mul-double v0, v0, v2

    return-void
.end method

.method public static mulDouble2Addr()V
    .locals 4

    mul-double/2addr v0, v2

    return-void
.end method

.method public static divDouble()V
    .locals 4

    div-double v0, v0, v2

    return-void
.end method

.method public static divDouble2Addr()V
    .locals 4

    div-double/2addr v0, v2

    return-void
.end method

.method public static divDoubleWithCatch()V
    .locals 2

    :try_start_0
    div-double v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static remDouble()V
    .locals 4

    rem-double v0, v0, v2

    return-void
.end method

.method public static remDouble2Addr()V
    .locals 4

    rem-double/2addr v0, v2

    return-void
.end method
