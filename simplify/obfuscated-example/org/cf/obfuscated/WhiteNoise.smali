.class public Lorg/cf/obfuscated/WhiteNoise;
.super Ljava/lang/Object;
.source "WhiteNoise.java"


# static fields
.field private static final five:I = 0x5

.field private static final ten:I = 0xa


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static convolutedMethod()I
    .registers 2

    .prologue
    .line 28
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepA1()I

    move-result v0

    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepB1()I

    move-result v1

    mul-int/2addr v0, v1

    return v0
.end method

.method public static messyMethod()V
    .registers 6

    .prologue
    const/4 v5, 0x3

    const/4 v4, 0x2

    .line 11
    new-instance v0, Ljava/lang/Integer;

    const/16 v2, 0x3039

    invoke-direct {v0, v2}, Ljava/lang/Integer;-><init>(I)V

    .line 12
    .local v0, "i":Ljava/lang/Integer;
    new-instance v2, Ljava/lang/String;

    new-array v3, v4, [C

    fill-array-data v3, :array_da

    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 13
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    mul-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 14
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    const/16 v3, 0x18

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    add-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 15
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    div-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 16
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    const/16 v3, 0x8

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    sub-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 17
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->realTarget()V

    .line 18
    new-instance v2, Ljava/lang/String;

    new-array v3, v4, [C

    fill-array-data v3, :array_e0

    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 19
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    new-array v1, v2, [B

    .line 20
    .local v1, "noise":[B
    new-instance v2, Ljava/lang/String;

    new-array v3, v4, [C

    fill-array-data v3, :array_e6

    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 21
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    const/4 v3, 0x4

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    mul-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 22
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    const/16 v3, 0xc

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    add-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 23
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    div-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 24
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v2

    const/4 v3, 0x6

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    move-result v3

    sub-int/2addr v2, v3

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 25
    return-void

    .line 12
    nop

    :array_da
    .array-data 2
        0x31s
        0x30s
    .end array-data

    .line 18
    :array_e0
    .array-data 2
        0x33s
        0x39s
    .end array-data

    .line 20
    :array_e6
    .array-data 2
        0x32s
        0x34s
    .end array-data
.end method

.method private static realTarget()V
    .registers 0

    .prologue
    .line 32
    invoke-static {}, Ljava/lang/System;->gc()V

    .line 33
    return-void
.end method

.method private static stepA1()I
    .registers 1

    .prologue
    .line 36
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepA2()I

    move-result v0

    return v0
.end method

.method private static stepA2()I
    .registers 1

    .prologue
    .line 40
    const/16 v0, 0xa

    return v0
.end method

.method private static stepB1()I
    .registers 1

    .prologue
    .line 44
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepB2()I

    move-result v0

    return v0
.end method

.method private static stepB2()I
    .registers 1

    .prologue
    .line 48
    const/4 v0, 0x5

    return v0
.end method
