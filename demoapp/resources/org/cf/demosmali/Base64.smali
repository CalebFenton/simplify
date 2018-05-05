.class public Lorg/cf/demosmali/Base64;
.super Ljava/lang/Object;
.source "Base64.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static final decode(Ljava/lang/String;)[B
    .locals 2
    .param p0, "str"    # Ljava/lang/String;

    #@0
    .prologue
    .line 45
    if-nez p0, :cond_0

    #@2
    .line 46
    const/4 v1, 0x0

    #@3
    .line 50
    :goto_0
    return-object v1

    #@4
    .line 48
    :cond_0
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    #@7
    move-result-object v0

    #@8
    .line 50
    .local v0, "data":[B
    invoke-static {v0}, Lorg/cf/demosmali/Base64;->decode([B)[B

    #@b
    move-result-object v1

    #@c
    goto :goto_0
.end method

.method public static final decode([B)[B
    .locals 8
    .param p0, "data"    # [B

    #@0
    .prologue
    const/16 v7, 0x3d

    #@2
    .line 6
    array-length v4, p0

    #@3
    .line 7
    .local v4, "tail":I
    :goto_0
    add-int/lit8 v5, v4, -0x1

    #@5
    aget-byte v5, p0, v5

    #@7
    if-ne v5, v7, :cond_0

    #@9
    .line 8
    add-int/lit8 v4, v4, -0x1

    #@b
    goto :goto_0

    #@c
    .line 10
    :cond_0
    array-length v5, p0

    #@d
    div-int/lit8 v5, v5, 0x4

    #@f
    sub-int v5, v4, v5

    #@11
    new-array v0, v5, [B

    #@13
    .line 12
    .local v0, "dest":[B
    const/4 v2, 0x0

    #@14
    .local v2, "idx":I
    :goto_1
    array-length v5, p0

    #@15
    if-ge v2, v5, :cond_7

    #@17
    .line 13
    aget-byte v5, p0, v2

    #@19
    if-ne v5, v7, :cond_2

    #@1b
    .line 14
    const/4 v5, 0x0

    #@1c
    aput-byte v5, p0, v2

    #@1e
    .line 12
    :cond_1
    :goto_2
    add-int/lit8 v2, v2, 0x1

    #@20
    goto :goto_1

    #@21
    .line 15
    :cond_2
    aget-byte v5, p0, v2

    #@23
    const/16 v6, 0x2f

    #@25
    if-ne v5, v6, :cond_3

    #@27
    .line 16
    const/16 v5, 0x3f

    #@29
    aput-byte v5, p0, v2

    #@2b
    goto :goto_2

    #@2c
    .line 17
    :cond_3
    aget-byte v5, p0, v2

    #@2e
    const/16 v6, 0x2b

    #@30
    if-ne v5, v6, :cond_4

    #@32
    .line 18
    const/16 v5, 0x3e

    #@34
    aput-byte v5, p0, v2

    #@36
    goto :goto_2

    #@37
    .line 19
    :cond_4
    aget-byte v5, p0, v2

    #@39
    const/16 v6, 0x30

    #@3b
    if-lt v5, v6, :cond_5

    #@3d
    aget-byte v5, p0, v2

    #@3f
    const/16 v6, 0x39

    #@41
    if-gt v5, v6, :cond_5

    #@43
    .line 20
    aget-byte v5, p0, v2

    #@45
    add-int/lit8 v5, v5, 0x4

    #@47
    int-to-byte v5, v5

    #@48
    aput-byte v5, p0, v2

    #@4a
    goto :goto_2

    #@4b
    .line 21
    :cond_5
    aget-byte v5, p0, v2

    #@4d
    const/16 v6, 0x61

    #@4f
    if-lt v5, v6, :cond_6

    #@51
    aget-byte v5, p0, v2

    #@53
    const/16 v6, 0x7a

    #@55
    if-gt v5, v6, :cond_6

    #@57
    .line 22
    aget-byte v5, p0, v2

    #@59
    add-int/lit8 v5, v5, -0x47

    #@5b
    int-to-byte v5, v5

    #@5c
    aput-byte v5, p0, v2

    #@5e
    goto :goto_2

    #@5f
    .line 23
    :cond_6
    aget-byte v5, p0, v2

    #@61
    const/16 v6, 0x41

    #@63
    if-lt v5, v6, :cond_1

    #@65
    aget-byte v5, p0, v2

    #@67
    const/16 v6, 0x5a

    #@69
    if-gt v5, v6, :cond_1

    #@6b
    .line 24
    aget-byte v5, p0, v2

    #@6d
    add-int/lit8 v5, v5, -0x41

    #@6f
    int-to-byte v5, v5

    #@70
    aput-byte v5, p0, v2

    #@72
    goto :goto_2

    #@73
    .line 29
    :cond_7
    const/4 v3, 0x0

    #@74
    .local v3, "sidx":I
    const/4 v1, 0x0

    #@75
    .local v1, "didx":I
    :goto_3
    array-length v5, v0

    #@76
    add-int/lit8 v5, v5, -0x2

    #@78
    if-ge v1, v5, :cond_8

    #@7a
    .line 30
    aget-byte v5, p0, v3

    #@7c
    shl-int/lit8 v5, v5, 0x2

    #@7e
    and-int/lit16 v5, v5, 0xff

    #@80
    add-int/lit8 v6, v3, 0x1

    #@82
    aget-byte v6, p0, v6

    #@84
    ushr-int/lit8 v6, v6, 0x4

    #@86
    and-int/lit8 v6, v6, 0x3

    #@88
    or-int/2addr v5, v6

    #@89
    int-to-byte v5, v5

    #@8a
    aput-byte v5, v0, v1

    #@8c
    .line 31
    add-int/lit8 v5, v1, 0x1

    #@8e
    add-int/lit8 v6, v3, 0x1

    #@90
    aget-byte v6, p0, v6

    #@92
    shl-int/lit8 v6, v6, 0x4

    #@94
    and-int/lit16 v6, v6, 0xff

    #@96
    add-int/lit8 v7, v3, 0x2

    #@98
    aget-byte v7, p0, v7

    #@9a
    ushr-int/lit8 v7, v7, 0x2

    #@9c
    and-int/lit8 v7, v7, 0xf

    #@9e
    or-int/2addr v6, v7

    #@9f
    int-to-byte v6, v6

    #@a0
    aput-byte v6, v0, v5

    #@a2
    .line 32
    add-int/lit8 v5, v1, 0x2

    #@a4
    add-int/lit8 v6, v3, 0x2

    #@a6
    aget-byte v6, p0, v6

    #@a8
    shl-int/lit8 v6, v6, 0x6

    #@aa
    and-int/lit16 v6, v6, 0xff

    #@ac
    add-int/lit8 v7, v3, 0x3

    #@ae
    aget-byte v7, p0, v7

    #@b0
    and-int/lit8 v7, v7, 0x3f

    #@b2
    or-int/2addr v6, v7

    #@b3
    int-to-byte v6, v6

    #@b4
    aput-byte v6, v0, v5

    #@b6
    .line 29
    add-int/lit8 v3, v3, 0x4

    #@b8
    add-int/lit8 v1, v1, 0x3

    #@ba
    goto :goto_3

    #@bb
    .line 34
    :cond_8
    array-length v5, v0

    #@bc
    if-ge v1, v5, :cond_9

    #@be
    .line 35
    aget-byte v5, p0, v3

    #@c0
    shl-int/lit8 v5, v5, 0x2

    #@c2
    and-int/lit16 v5, v5, 0xff

    #@c4
    add-int/lit8 v6, v3, 0x1

    #@c6
    aget-byte v6, p0, v6

    #@c8
    ushr-int/lit8 v6, v6, 0x4

    #@ca
    and-int/lit8 v6, v6, 0x3

    #@cc
    or-int/2addr v5, v6

    #@cd
    int-to-byte v5, v5

    #@ce
    aput-byte v5, v0, v1

    #@d0
    .line 37
    :cond_9
    add-int/lit8 v1, v1, 0x1

    #@d2
    array-length v5, v0

    #@d3
    if-ge v1, v5, :cond_a

    #@d5
    .line 38
    add-int/lit8 v5, v3, 0x1

    #@d7
    aget-byte v5, p0, v5

    #@d9
    shl-int/lit8 v5, v5, 0x4

    #@db
    and-int/lit16 v5, v5, 0xff

    #@dd
    add-int/lit8 v6, v3, 0x2

    #@df
    aget-byte v6, p0, v6

    #@e1
    ushr-int/lit8 v6, v6, 0x2

    #@e3
    and-int/lit8 v6, v6, 0xf

    #@e5
    or-int/2addr v5, v6

    #@e6
    int-to-byte v5, v5

    #@e7
    aput-byte v5, v0, v1

    #@e9
    .line 41
    :cond_a
    return-object v0
.end method

.method public static final encode(Ljava/lang/String;)Ljava/lang/String;
    .locals 1
    .param p0, "s"    # Ljava/lang/String;

    #@0
    .prologue
    .line 90
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    #@3
    move-result-object v0

    #@4
    invoke-static {v0}, Lorg/cf/demosmali/Base64;->encode([B)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static final encode([B)Ljava/lang/String;
    .locals 8
    .param p0, "d"    # [B

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 54
    if-nez p0, :cond_0

    #@3
    .line 55
    const/4 v5, 0x0

    #@4
    .line 86
    :goto_0
    return-object v5

    #@5
    .line 57
    :cond_0
    array-length v5, p0

    #@6
    add-int/lit8 v5, v5, 0x2

    #@8
    new-array v0, v5, [B

    #@a
    .line 58
    .local v0, "data":[B
    array-length v5, p0

    #@b
    invoke-static {p0, v6, v0, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@e
    .line 59
    array-length v5, v0

    #@f
    div-int/lit8 v5, v5, 0x3

    #@11
    mul-int/lit8 v5, v5, 0x4

    #@13
    new-array v1, v5, [B

    #@15
    .line 61
    .local v1, "dest":[B
    const/4 v4, 0x0

    #@16
    .local v4, "sidx":I
    const/4 v2, 0x0

    #@17
    .local v2, "didx":I
    :goto_1
    array-length v5, p0

    #@18
    if-ge v4, v5, :cond_1

    #@1a
    .line 62
    aget-byte v5, v0, v4

    #@1c
    ushr-int/lit8 v5, v5, 0x2

    #@1e
    and-int/lit8 v5, v5, 0x3f

    #@20
    int-to-byte v5, v5

    #@21
    aput-byte v5, v1, v2

    #@23
    .line 63
    add-int/lit8 v5, v2, 0x1

    #@25
    add-int/lit8 v6, v4, 0x1

    #@27
    aget-byte v6, v0, v6

    #@29
    ushr-int/lit8 v6, v6, 0x4

    #@2b
    and-int/lit8 v6, v6, 0xf

    #@2d
    aget-byte v7, v0, v4

    #@2f
    shl-int/lit8 v7, v7, 0x4

    #@31
    and-int/lit8 v7, v7, 0x3f

    #@33
    or-int/2addr v6, v7

    #@34
    int-to-byte v6, v6

    #@35
    aput-byte v6, v1, v5

    #@37
    .line 64
    add-int/lit8 v5, v2, 0x2

    #@39
    add-int/lit8 v6, v4, 0x2

    #@3b
    aget-byte v6, v0, v6

    #@3d
    ushr-int/lit8 v6, v6, 0x6

    #@3f
    and-int/lit8 v6, v6, 0x3

    #@41
    add-int/lit8 v7, v4, 0x1

    #@43
    aget-byte v7, v0, v7

    #@45
    shl-int/lit8 v7, v7, 0x2

    #@47
    and-int/lit8 v7, v7, 0x3f

    #@49
    or-int/2addr v6, v7

    #@4a
    int-to-byte v6, v6

    #@4b
    aput-byte v6, v1, v5

    #@4d
    .line 65
    add-int/lit8 v5, v2, 0x3

    #@4f
    add-int/lit8 v6, v4, 0x2

    #@51
    aget-byte v6, v0, v6

    #@53
    and-int/lit8 v6, v6, 0x3f

    #@55
    int-to-byte v6, v6

    #@56
    aput-byte v6, v1, v5

    #@58
    .line 61
    add-int/lit8 v4, v4, 0x3

    #@5a
    add-int/lit8 v2, v2, 0x4

    #@5c
    goto :goto_1

    #@5d
    .line 68
    :cond_1
    const/4 v3, 0x0

    #@5e
    .local v3, "idx":I
    :goto_2
    array-length v5, v1

    #@5f
    if-ge v3, v5, :cond_6

    #@61
    .line 69
    aget-byte v5, v1, v3

    #@63
    const/16 v6, 0x1a

    #@65
    if-ge v5, v6, :cond_2

    #@67
    .line 70
    aget-byte v5, v1, v3

    #@69
    add-int/lit8 v5, v5, 0x41

    #@6b
    int-to-byte v5, v5

    #@6c
    aput-byte v5, v1, v3

    #@6e
    .line 68
    :goto_3
    add-int/lit8 v3, v3, 0x1

    #@70
    goto :goto_2

    #@71
    .line 71
    :cond_2
    aget-byte v5, v1, v3

    #@73
    const/16 v6, 0x34

    #@75
    if-ge v5, v6, :cond_3

    #@77
    .line 72
    aget-byte v5, v1, v3

    #@79
    add-int/lit8 v5, v5, 0x61

    #@7b
    add-int/lit8 v5, v5, -0x1a

    #@7d
    int-to-byte v5, v5

    #@7e
    aput-byte v5, v1, v3

    #@80
    goto :goto_3

    #@81
    .line 73
    :cond_3
    aget-byte v5, v1, v3

    #@83
    const/16 v6, 0x3e

    #@85
    if-ge v5, v6, :cond_4

    #@87
    .line 74
    aget-byte v5, v1, v3

    #@89
    add-int/lit8 v5, v5, 0x30

    #@8b
    add-int/lit8 v5, v5, -0x34

    #@8d
    int-to-byte v5, v5

    #@8e
    aput-byte v5, v1, v3

    #@90
    goto :goto_3

    #@91
    .line 75
    :cond_4
    aget-byte v5, v1, v3

    #@93
    const/16 v6, 0x3f

    #@95
    if-ge v5, v6, :cond_5

    #@97
    .line 76
    const/16 v5, 0x2b

    #@99
    aput-byte v5, v1, v3

    #@9b
    goto :goto_3

    #@9c
    .line 78
    :cond_5
    const/16 v5, 0x2f

    #@9e
    aput-byte v5, v1, v3

    #@a0
    goto :goto_3

    #@a1
    .line 82
    :cond_6
    array-length v5, v1

    #@a2
    add-int/lit8 v3, v5, -0x1

    #@a4
    :goto_4
    array-length v5, p0

    #@a5
    mul-int/lit8 v5, v5, 0x4

    #@a7
    div-int/lit8 v5, v5, 0x3

    #@a9
    if-le v3, v5, :cond_7

    #@ab
    .line 83
    const/16 v5, 0x3d

    #@ad
    aput-byte v5, v1, v3

    #@af
    .line 82
    add-int/lit8 v3, v3, -0x1

    #@b1
    goto :goto_4

    #@b2
    .line 86
    :cond_7
    new-instance v5, Ljava/lang/String;

    #@b4
    invoke-direct {v5, v1}, Ljava/lang/String;-><init>([B)V

    #@b7
    goto/16 :goto_0
.end method
