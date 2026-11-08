.class public Lconstants/Constants;
.super Ljava/lang/Object;

.field public static final known:I = 0x2a
.field public static final knownBoolean:Z = 0x1
.field public static final knownByte:B = 0x2
.field public static final knownShort:S = 0x3
.field public static final knownChar:C = 0x41
.field public static final knownLong:J = 0x4L
.field public static final knownString:Ljava/lang/String; = "static string"
.field public static initTrigger:I
.field public static final nullable:Ljava/lang/String;
.field public static mutableText:Ljava/lang/String;

.method public static computedInteger()I
    .locals 2
    const/4 v0, 0x2
    const/4 v1, 0x3
    add-int v0, v0, v1
    move v1, v0
    return v1
.end method

.method public static decrypt(Ljava/lang/String;)Ljava/lang/String;
    .locals 1
    const-string v0, "Tell me of your homeworld, Usul."
    return-object v0
.end method

.method public static readmeDecryptorShape()Ljava/lang/String;
    .locals 1
    const-string v0, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="
    invoke-static {v0}, Lconstants/Constants;->decrypt(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static computedFloat()F
    .locals 1
    const v0, 0x3f800001
    neg-float v0, v0
    return v0
.end method

.method public static computedDouble()D
    .locals 2
    const-wide v0, 0x3ff0000000000001L
    neg-double v0, v0
    return-wide v0
.end method

.method public static computedNegativeZeroFloat()F
    .locals 1
    const/4 v0, 0x0
    neg-float v0, v0
    return v0
.end method

.method public static computedNegativeZeroDouble()D
    .locals 2
    const-wide/16 v0, 0x0
    neg-double v0, v0
    return-wide v0
.end method

.method public static computedNaNFloat()F
    .locals 1
    const v0, 0x7fc01234
    neg-float v0, v0
    return v0
.end method

.method public static computedNaNDouble()D
    .locals 2
    const-wide v0, 0x7ff8000000001234L
    neg-double v0, v0
    return-wide v0
.end method

.method public static computedLong()J
    .locals 4
    const-wide/16 v0, 0x7
    const-wide/16 v2, 0x9
    add-long v0, v0, v2
    return-wide v0
.end method

.method public static wideDestination255()J
    .registers 257
    const-wide/16 v0, 0x7
    const-wide/16 v2, 0x9
    add-long v255, v0, v2
    return-wide v255
.end method

.method public static destinationAboveLiteralLimit()I
    .registers 257
    const/4 v0, 0x7
    move/16 v256, v0
    move/from16 v1, v256
    return v1
.end method

.method private static classValue()Ljava/lang/Class;
    .locals 1
    const-class v0, Ljava/lang/String;
    return-object v0
.end method

.method public static computedClass()Ljava/lang/Class;
    .locals 1
    invoke-static {}, Lconstants/Constants;->classValue()Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method private static nullValue()Ljava/lang/String;
    .locals 1
    sget-object v0, Lconstants/Constants;->nullable:Ljava/lang/String;
    return-object v0
.end method

.method public static mutableStaticRead()Ljava/lang/String;
    .locals 1
    sget-object v0, Lconstants/Constants;->mutableText:Ljava/lang/String;
    return-object v0
.end method

.method public static computedNull()Ljava/lang/String;
    .locals 1
    invoke-static {}, Lconstants/Constants;->nullValue()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static disagreeingPaths(I)I
    .locals 2
    if-eqz p0, :zero
    const/4 v0, 0x1
    goto :join
    :zero
    const/4 v0, 0x2
    :join
    move v1, v0
    return v1
.end method

.method public static knownAget()I
    .locals 3
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    const/4 v2, 0x7
    aput v2, v0, v1
    aget v2, v0, v1
    return v2
.end method

.method public static knownSget()I
    .locals 1
    const/4 v0, 0x0
    sput v0, Lconstants/Constants;->initTrigger:I
    sget v0, Lconstants/Constants;->known:I
    return v0
.end method

.method public static knownDivision()I
    .locals 2
    const/4 v0, 0x6
    const/4 v1, 0x2
    div-int v0, v0, v1
    return v0
.end method

.method public static knownRemainder()I
    .locals 2
    const/4 v0, 0x7
    const/4 v1, 0x3
    rem-int v0, v0, v1
    return v0
.end method

.method public static knownCheckCast()Ljava/lang/String;
    .locals 1
    const-string v0, "safe"
    check-cast v0, Ljava/lang/String;
    return-object v0
.end method

.method public static knownArrayLength()I
    .locals 2
    const/4 v0, 0x1
    new-array v0, v0, [I
    array-length v1, v0
    return v1
.end method

.method private static arrayValue()[I
    .locals 1
    const/4 v0, 0x1
    new-array v0, v0, [I
    return-object v0
.end method

.method public static computedMutableArray()[I
    .locals 1
    invoke-static {}, Lconstants/Constants;->arrayValue()[I
    move-result-object v0
    return-object v0
.end method

.method public static partialLoop()V
    .locals 1
    const/4 v0, 0x1
    :loop
    add-int/lit8 v0, v0, 0x1
    goto :loop
.end method

.method public static builderNarrow()I
    .locals 1
    const/16 v0, 0x123
    return v0
.end method

.method public static builderWide()J
    .locals 2
    const-wide/16 v0, 0x0
    return-wide v0
.end method

.method public static builderObject()Ljava/lang/Object;
    .locals 1
    const/4 v0, 0x0
    return-object v0
.end method

.method public static knownAgetString()Ljava/lang/String;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/String;
    const/4 v1, 0x0
    const-string v2, "array string"
    aput-object v2, v0, v1
    aget-object v3, v0, v1
    return-object v3
.end method

.method public static knownAgetClass()Ljava/lang/Class;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Class;
    const/4 v1, 0x0
    const-class v2, Ljava/lang/String;
    aput-object v2, v0, v1
    aget-object v3, v0, v1
    return-object v3
.end method

.method public static knownAgetObjectString()Ljava/lang/Object;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Object;
    const/4 v1, 0x0
    const-string v2, "object array string"
    aput-object v2, v0, v1
    aget-object v3, v0, v1
    return-object v3
.end method

.method public static allAgetOpcodes()J
    .locals 5
    const/4 v0, 0x1
    const/4 v1, 0x0
    new-array v2, v0, [I
    aget v3, v2, v1
    new-array v2, v0, [Z
    aget-boolean v3, v2, v1
    new-array v2, v0, [B
    aget-byte v3, v2, v1
    new-array v2, v0, [C
    aget-char v3, v2, v1
    new-array v2, v0, [S
    aget-short v3, v2, v1
    new-array v2, v0, [Ljava/lang/Object;
    aget-object v3, v2, v1
    new-array v2, v0, [J
    aget-wide v3, v2, v1
    return-wide v3
.end method

.method public static allSgetOpcodes()Ljava/lang/Class;
    .locals 2
    const/4 v0, 0x0
    sput v0, Lconstants/Constants;->initTrigger:I
    sput v0, Lconstants/ClassStatics;->initTrigger:I
    sget v0, Lconstants/Constants;->known:I
    sget-boolean v0, Lconstants/Constants;->knownBoolean:Z
    sget-byte v0, Lconstants/Constants;->knownByte:B
    sget-char v0, Lconstants/Constants;->knownChar:C
    sget-short v0, Lconstants/Constants;->knownShort:S
    sget-wide v0, Lconstants/Constants;->knownLong:J
    sget-object v0, Lconstants/ClassStatics;->knownClass:Ljava/lang/Class;
    return-object v0
.end method

.method private static intResult()I
    .locals 1
    const/4 v0, 0x5
    return v0
.end method

.method private static wideResult()J
    .locals 2
    const-wide/16 v0, 0x6
    return-wide v0
.end method

.method private static objectResult()Ljava/lang/String;
    .locals 1
    const-string v0, "move result"
    return-object v0
.end method

.method public static allMoveResultOpcodes()J
    .locals 4
    invoke-static {}, Lconstants/Constants;->intResult()I
    move-result v0
    invoke-static {}, Lconstants/Constants;->objectResult()Ljava/lang/String;
    move-result-object v0
    invoke-static {}, Lconstants/Constants;->wideResult()J
    move-result-wide v2
    return-wide v2
.end method

.method public static allNarrowMoveEncodings()I
    .registers 258
    const/4 v0, 0x7
    move v1, v0
    move/16 v256, v1
    move/from16 v2, v256
    move/16 v255, v256
    return v255
.end method

.method public static allObjectMoveEncodings()Ljava/lang/String;
    .registers 258
    const-string v0, "move object"
    move-object v1, v0
    move-object/16 v256, v1
    move-object/from16 v2, v256
    move-object/16 v255, v256
    return-object v255
.end method

.method public static allWideMoveEncodings()J
    .registers 258
    const-wide/16 v0, 0x9
    move-wide v2, v0
    move-wide/16 v256, v2
    move-wide/from16 v4, v256
    move-wide/16 v254, v256
    return-wide v254
.end method

.method public static caughtMoveExceptionRemains()Ljava/lang/Throwable;
    .locals 4
    :try_start
    const/4 v0, 0x0
    const/4 v1, 0x0
    aget v2, v0, v1
    :try_end
    const/4 v3, 0x0
    return-object v3
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    :handler
    move-exception v3
    return-object v3
.end method

.method public static unknownMove(I)I
    .locals 1
    move v0, p0
    return v0
.end method

.method public static unknownBinary(I)I
    .locals 1
    const/4 v0, 0x2
    add-int v0, v0, p0
    return v0
.end method

.method public static unknownAgetArray([I)I
    .locals 2
    const/4 v0, 0x0
    aget v1, p0, v0
    return v1
.end method

.method public static unknownAgetIndex(I)I
    .locals 3
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x7
    const/4 v2, 0x0
    aput v1, v0, v2
    aget v1, v0, p0
    return v1
.end method

.method public static knownCmp()I
    .locals 5
    const-wide/16 v0, 0x1
    const-wide/16 v2, 0x2
    cmp-long v4, v0, v2
    return v4
.end method

.method public static boxedInteger()Ljava/lang/Integer;
    .locals 1
    const/4 v0, 0x1
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method

# 2 + 3 folds; boxing the unknown sum with p0 must not stop the method from being optimized.
.method public static boxedParameterSum(I)Ljava/lang/Integer;
    .locals 2
    const/4 v0, 0x2
    const/4 v1, 0x3
    add-int v0, v0, v1
    add-int v0, v0, p0
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method

.method public static unreachedMove()I
    .locals 1
    const/4 v0, 0x1
    goto :end
    move v0, v0
    :end
    return v0
.end method

.method public static allUnaryOpcodes()S
    .locals 8
    const/4 v0, 0x2
    neg-int v1, v0
    not-int v1, v0
    int-to-long v2, v0
    int-to-float v1, v0
    int-to-double v2, v0
    int-to-byte v1, v0
    int-to-char v1, v0
    int-to-short v1, v0
    const-wide/16 v2, 0x2
    neg-long v4, v2
    not-long v4, v2
    long-to-int v1, v2
    long-to-float v1, v2
    long-to-double v4, v2
    const v0, 0x40000000
    neg-float v1, v0
    float-to-int v1, v0
    float-to-long v2, v0
    float-to-double v2, v0
    const-wide v2, 0x4000000000000000L
    neg-double v4, v2
    double-to-int v1, v2
    double-to-long v4, v2
    double-to-float v1, v2
    int-to-short v1, v0
    return v1
.end method

.method public static allBinaryOpcodes()D
    .locals 8
    const/16 v0, 0x6
    const/4 v1, 0x2
    add-int v2, v0, v1
    add-int/2addr v0, v1
    add-int/lit8 v2, v1, 0x2
    add-int/lit16 v2, v1, 0x2
    sub-int v2, v0, v1
    sub-int/2addr v0, v1
    rsub-int v2, v1, 0x6
    rsub-int/lit8 v2, v1, 0x6
    mul-int v2, v0, v1
    mul-int/2addr v0, v1
    mul-int/lit8 v2, v1, 0x2
    mul-int/lit16 v2, v1, 0x2
    div-int v2, v0, v1
    div-int/2addr v0, v1
    div-int/lit8 v2, v1, 0x2
    div-int/lit16 v2, v1, 0x2
    rem-int v2, v0, v1
    rem-int/2addr v0, v1
    rem-int/lit8 v2, v1, 0x2
    rem-int/lit16 v2, v1, 0x2
    and-int v2, v0, v1
    and-int/2addr v0, v1
    and-int/lit8 v2, v1, 0x2
    and-int/lit16 v2, v1, 0x2
    or-int v2, v0, v1
    or-int/2addr v0, v1
    or-int/lit8 v2, v1, 0x2
    or-int/lit16 v2, v1, 0x2
    xor-int v2, v0, v1
    xor-int/2addr v0, v1
    xor-int/lit8 v2, v1, 0x2
    xor-int/lit16 v2, v1, 0x2
    shl-int v2, v0, v1
    shl-int/2addr v0, v1
    shl-int/lit8 v2, v1, 0x2
    shr-int v2, v0, v1
    shr-int/2addr v0, v1
    shr-int/lit8 v2, v1, 0x2
    ushr-int v2, v0, v1
    ushr-int/2addr v0, v1
    ushr-int/lit8 v2, v1, 0x2

    const-wide/16 v0, 0x6
    const-wide/16 v2, 0x2
    add-long v4, v0, v2
    add-long/2addr v0, v2
    sub-long v4, v0, v2
    sub-long/2addr v0, v2
    mul-long v4, v0, v2
    mul-long/2addr v0, v2
    div-long v4, v0, v2
    div-long/2addr v0, v2
    rem-long v4, v0, v2
    rem-long/2addr v0, v2
    and-long v4, v0, v2
    and-long/2addr v0, v2
    or-long v4, v0, v2
    or-long/2addr v0, v2
    xor-long v4, v0, v2
    xor-long/2addr v0, v2
    shl-long v4, v0, v2
    shl-long/2addr v0, v2
    shr-long v4, v0, v2
    shr-long/2addr v0, v2
    ushr-long v4, v0, v2
    ushr-long/2addr v0, v2

    const v0, 0x40c00000
    const v1, 0x40000000
    add-float v2, v0, v1
    add-float/2addr v0, v1
    sub-float v2, v0, v1
    sub-float/2addr v0, v1
    mul-float v2, v0, v1
    mul-float/2addr v0, v1
    div-float v2, v0, v1
    div-float/2addr v0, v1
    rem-float v2, v0, v1
    rem-float/2addr v0, v1

    const-wide v0, 0x4018000000000000L
    const-wide v2, 0x4000000000000000L
    add-double v4, v0, v2
    add-double/2addr v0, v2
    sub-double v4, v0, v2
    sub-double/2addr v0, v2
    mul-double v4, v0, v2
    mul-double/2addr v0, v2
    div-double v4, v0, v2
    div-double/2addr v0, v2
    rem-double v4, v0, v2
    rem-double/2addr v0, v2
    return-wide v4
.end method
