.class public Lorg/cf/obfuscated/WhiteNoise;
.super Ljava/lang/Object;
.source "WhiteNoise.java"


# static fields
.field private static final five:I = 0x5

.field private static final ten:I = 0xa


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static convolutedMethod()I
    .locals 2

    #@0
    .prologue
    .line 28
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepA1()I

    #@3
    move-result v0

    #@4
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepB1()I

    #@7
    move-result v1

    #@8
    mul-int/2addr v0, v1

    #@9
    return v0
.end method

.method public static messyMethod()V
    .locals 6

    #@0
    .prologue
    const/4 v5, 0x3

    #@1
    const/4 v4, 0x2

    #@2
    .line 11
    new-instance v0, Ljava/lang/Integer;

    #@4
    const/16 v2, 0x3039

    #@6
    invoke-direct {v0, v2}, Ljava/lang/Integer;-><init>(I)V

    #@9
    .line 12
    .local v0, "i":Ljava/lang/Integer;
    new-instance v2, Ljava/lang/String;

    #@b
    new-array v3, v4, [C

    #@d
    fill-array-data v3, :array_0

    #@10
    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    #@13
    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    #@16
    move-result v2

    #@17
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@1a
    move-result-object v0

    #@1b
    .line 13
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@1e
    move-result v2

    #@1f
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@22
    move-result-object v3

    #@23
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@26
    move-result v3

    #@27
    mul-int/2addr v2, v3

    #@28
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@2b
    move-result-object v0

    #@2c
    .line 14
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@2f
    move-result v2

    #@30
    const/16 v3, 0x18

    #@32
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@35
    move-result-object v3

    #@36
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@39
    move-result v3

    #@3a
    add-int/2addr v2, v3

    #@3b
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@3e
    move-result-object v0

    #@3f
    .line 15
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@42
    move-result v2

    #@43
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@46
    move-result-object v3

    #@47
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@4a
    move-result v3

    #@4b
    div-int/2addr v2, v3

    #@4c
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@4f
    move-result-object v0

    #@50
    .line 16
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@53
    move-result v2

    #@54
    const/16 v3, 0x8

    #@56
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@59
    move-result-object v3

    #@5a
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@5d
    move-result v3

    #@5e
    sub-int/2addr v2, v3

    #@5f
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@62
    move-result-object v0

    #@63
    .line 17
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->realTarget()V

    #@66
    .line 18
    new-instance v2, Ljava/lang/String;

    #@68
    new-array v3, v4, [C

    #@6a
    fill-array-data v3, :array_1

    #@6d
    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    #@70
    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    #@73
    move-result v2

    #@74
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@77
    move-result-object v0

    #@78
    .line 19
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@7b
    move-result v2

    #@7c
    new-array v1, v2, [B

    #@7e
    .line 20
    .local v1, "noise":[B
    new-instance v2, Ljava/lang/String;

    #@80
    new-array v3, v4, [C

    #@82
    fill-array-data v3, :array_2

    #@85
    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([C)V

    #@88
    invoke-static {v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    #@8b
    move-result v2

    #@8c
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@8f
    move-result-object v0

    #@90
    .line 21
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@93
    move-result v2

    #@94
    const/4 v3, 0x4

    #@95
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@98
    move-result-object v3

    #@99
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@9c
    move-result v3

    #@9d
    mul-int/2addr v2, v3

    #@9e
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@a1
    move-result-object v0

    #@a2
    .line 22
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@a5
    move-result v2

    #@a6
    const/16 v3, 0xc

    #@a8
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@ab
    move-result-object v3

    #@ac
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@af
    move-result v3

    #@b0
    add-int/2addr v2, v3

    #@b1
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@b4
    move-result-object v0

    #@b5
    .line 23
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@b8
    move-result v2

    #@b9
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@bc
    move-result-object v3

    #@bd
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@c0
    move-result v3

    #@c1
    div-int/2addr v2, v3

    #@c2
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@c5
    move-result-object v0

    #@c6
    .line 24
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@c9
    move-result v2

    #@ca
    const/4 v3, 0x6

    #@cb
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@ce
    move-result-object v3

    #@cf
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I

    #@d2
    move-result v3

    #@d3
    sub-int/2addr v2, v3

    #@d4
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@d7
    move-result-object v0

    #@d8
    .line 25
    return-void

    #@d9
    .line 12
    nop

    #@da
    :array_0
    .array-data 2
        0x31s
        0x30s
    .end array-data

    #@e0
    .line 18
    :array_1
    .array-data 2
        0x33s
        0x39s
    .end array-data

    #@e6
    .line 20
    :array_2
    .array-data 2
        0x32s
        0x34s
    .end array-data
.end method

.method private static realTarget()V
    .locals 0

    #@0
    .prologue
    .line 32
    invoke-static {}, Ljava/lang/System;->gc()V

    #@3
    .line 33
    return-void
.end method

.method private static stepA1()I
    .locals 1

    #@0
    .prologue
    .line 36
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepA2()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method private static stepA2()I
    .locals 1

    #@0
    .prologue
    .line 40
    const/16 v0, 0xa

    #@2
    return v0
.end method

.method private static stepB1()I
    .locals 1

    #@0
    .prologue
    .line 44
    invoke-static {}, Lorg/cf/obfuscated/WhiteNoise;->stepB2()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method private static stepB2()I
    .locals 1

    #@0
    .prologue
    .line 48
    const/4 v0, 0x5

    #@1
    return v0
.end method
