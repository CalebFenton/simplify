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
    if-eq v5, v7, :cond_2

    #@9
    .line 10
    array-length v5, p0

    #@a
    div-int/lit8 v5, v5, 0x4

    #@c
    sub-int v5, v4, v5

    #@e
    new-array v0, v5, [B

    #@10
    .line 12
    .local v0, "dest":[B
    const/4 v2, 0x0

    #@11
    .local v2, "idx":I
    :goto_1
    array-length v5, p0

    #@12
    if-lt v2, v5, :cond_3

    #@14
    .line 29
    const/4 v3, 0x0

    #@15
    .local v3, "sidx":I
    const/4 v1, 0x0

    #@16
    .local v1, "didx":I
    :goto_2
    array-length v5, v0

    #@17
    add-int/lit8 v5, v5, -0x2

    #@19
    if-lt v1, v5, :cond_a

    #@1b
    .line 34
    array-length v5, v0

    #@1c
    if-ge v1, v5, :cond_0

    #@1e
    .line 35
    aget-byte v5, p0, v3

    #@20
    shl-int/lit8 v5, v5, 0x2

    #@22
    and-int/lit16 v5, v5, 0xff

    #@24
    add-int/lit8 v6, v3, 0x1

    #@26
    aget-byte v6, p0, v6

    #@28
    ushr-int/lit8 v6, v6, 0x4

    #@2a
    and-int/lit8 v6, v6, 0x3

    #@2c
    or-int/2addr v5, v6

    #@2d
    int-to-byte v5, v5

    #@2e
    aput-byte v5, v0, v1

    #@30
    .line 37
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@32
    array-length v5, v0

    #@33
    if-ge v1, v5, :cond_1

    #@35
    .line 38
    add-int/lit8 v5, v3, 0x1

    #@37
    aget-byte v5, p0, v5

    #@39
    shl-int/lit8 v5, v5, 0x4

    #@3b
    and-int/lit16 v5, v5, 0xff

    #@3d
    add-int/lit8 v6, v3, 0x2

    #@3f
    aget-byte v6, p0, v6

    #@41
    ushr-int/lit8 v6, v6, 0x2

    #@43
    and-int/lit8 v6, v6, 0xf

    #@45
    or-int/2addr v5, v6

    #@46
    int-to-byte v5, v5

    #@47
    aput-byte v5, v0, v1

    #@49
    .line 41
    :cond_1
    return-object v0

    #@4a
    .line 8
    .end local v0    # "dest":[B
    .end local v1    # "didx":I
    .end local v2    # "idx":I
    .end local v3    # "sidx":I
    :cond_2
    add-int/lit8 v4, v4, -0x1

    #@4c
    goto :goto_0

    #@4d
    .line 13
    .restart local v0    # "dest":[B
    .restart local v2    # "idx":I
    :cond_3
    aget-byte v5, p0, v2

    #@4f
    if-ne v5, v7, :cond_5

    #@51
    .line 14
    const/4 v5, 0x0

    #@52
    aput-byte v5, p0, v2

    #@54
    .line 12
    :cond_4
    :goto_3
    add-int/lit8 v2, v2, 0x1

    #@56
    goto :goto_1

    #@57
    .line 15
    :cond_5
    aget-byte v5, p0, v2

    #@59
    const/16 v6, 0x2f

    #@5b
    if-ne v5, v6, :cond_6

    #@5d
    .line 16
    const/16 v5, 0x3f

    #@5f
    aput-byte v5, p0, v2

    #@61
    goto :goto_3

    #@62
    .line 17
    :cond_6
    aget-byte v5, p0, v2

    #@64
    const/16 v6, 0x2b

    #@66
    if-ne v5, v6, :cond_7

    #@68
    .line 18
    const/16 v5, 0x3e

    #@6a
    aput-byte v5, p0, v2

    #@6c
    goto :goto_3

    #@6d
    .line 19
    :cond_7
    aget-byte v5, p0, v2

    #@6f
    const/16 v6, 0x30

    #@71
    if-lt v5, v6, :cond_8

    #@73
    aget-byte v5, p0, v2

    #@75
    const/16 v6, 0x39

    #@77
    if-gt v5, v6, :cond_8

    #@79
    .line 20
    aget-byte v5, p0, v2

    #@7b
    add-int/lit8 v5, v5, 0x4

    #@7d
    int-to-byte v5, v5

    #@7e
    aput-byte v5, p0, v2

    #@80
    goto :goto_3

    #@81
    .line 21
    :cond_8
    aget-byte v5, p0, v2

    #@83
    const/16 v6, 0x61

    #@85
    if-lt v5, v6, :cond_9

    #@87
    aget-byte v5, p0, v2

    #@89
    const/16 v6, 0x7a

    #@8b
    if-gt v5, v6, :cond_9

    #@8d
    .line 22
    aget-byte v5, p0, v2

    #@8f
    add-int/lit8 v5, v5, -0x47

    #@91
    int-to-byte v5, v5

    #@92
    aput-byte v5, p0, v2

    #@94
    goto :goto_3

    #@95
    .line 23
    :cond_9
    aget-byte v5, p0, v2

    #@97
    const/16 v6, 0x41

    #@99
    if-lt v5, v6, :cond_4

    #@9b
    aget-byte v5, p0, v2

    #@9d
    const/16 v6, 0x5a

    #@9f
    if-gt v5, v6, :cond_4

    #@a1
    .line 24
    aget-byte v5, p0, v2

    #@a3
    add-int/lit8 v5, v5, -0x41

    #@a5
    int-to-byte v5, v5

    #@a6
    aput-byte v5, p0, v2

    #@a8
    goto :goto_3

    #@a9
    .line 30
    .restart local v1    # "didx":I
    .restart local v3    # "sidx":I
    :cond_a
    aget-byte v5, p0, v3

    #@ab
    shl-int/lit8 v5, v5, 0x2

    #@ad
    and-int/lit16 v5, v5, 0xff

    #@af
    add-int/lit8 v6, v3, 0x1

    #@b1
    aget-byte v6, p0, v6

    #@b3
    ushr-int/lit8 v6, v6, 0x4

    #@b5
    and-int/lit8 v6, v6, 0x3

    #@b7
    or-int/2addr v5, v6

    #@b8
    int-to-byte v5, v5

    #@b9
    aput-byte v5, v0, v1

    #@bb
    .line 31
    add-int/lit8 v5, v1, 0x1

    #@bd
    add-int/lit8 v6, v3, 0x1

    #@bf
    aget-byte v6, p0, v6

    #@c1
    shl-int/lit8 v6, v6, 0x4

    #@c3
    and-int/lit16 v6, v6, 0xff

    #@c5
    add-int/lit8 v7, v3, 0x2

    #@c7
    aget-byte v7, p0, v7

    #@c9
    ushr-int/lit8 v7, v7, 0x2

    #@cb
    and-int/lit8 v7, v7, 0xf

    #@cd
    or-int/2addr v6, v7

    #@ce
    int-to-byte v6, v6

    #@cf
    aput-byte v6, v0, v5

    #@d1
    .line 32
    add-int/lit8 v5, v1, 0x2

    #@d3
    add-int/lit8 v6, v3, 0x2

    #@d5
    aget-byte v6, p0, v6

    #@d7
    shl-int/lit8 v6, v6, 0x6

    #@d9
    and-int/lit16 v6, v6, 0xff

    #@db
    add-int/lit8 v7, v3, 0x3

    #@dd
    aget-byte v7, p0, v7

    #@df
    and-int/lit8 v7, v7, 0x3f

    #@e1
    or-int/2addr v6, v7

    #@e2
    int-to-byte v6, v6

    #@e3
    aput-byte v6, v0, v5

    #@e5
    .line 29
    add-int/lit8 v3, v3, 0x4

    #@e7
    add-int/lit8 v1, v1, 0x3

    #@e9
    goto/16 :goto_2
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
    if-lt v4, v5, :cond_1

    #@1a
    .line 68
    const/4 v3, 0x0

    #@1b
    .local v3, "idx":I
    :goto_2
    array-length v5, v1

    #@1c
    if-lt v3, v5, :cond_2

    #@1e
    .line 82
    array-length v5, v1

    #@1f
    add-int/lit8 v3, v5, -0x1

    #@21
    :goto_3
    array-length v5, p0

    #@22
    mul-int/lit8 v5, v5, 0x4

    #@24
    div-int/lit8 v5, v5, 0x3

    #@26
    if-gt v3, v5, :cond_7

    #@28
    .line 86
    new-instance v5, Ljava/lang/String;

    #@2a
    invoke-direct {v5, v1}, Ljava/lang/String;-><init>([B)V

    #@2d
    goto :goto_0

    #@2e
    .line 62
    .end local v3    # "idx":I
    :cond_1
    aget-byte v5, v0, v4

    #@30
    ushr-int/lit8 v5, v5, 0x2

    #@32
    and-int/lit8 v5, v5, 0x3f

    #@34
    int-to-byte v5, v5

    #@35
    aput-byte v5, v1, v2

    #@37
    .line 63
    add-int/lit8 v5, v2, 0x1

    #@39
    add-int/lit8 v6, v4, 0x1

    #@3b
    aget-byte v6, v0, v6

    #@3d
    ushr-int/lit8 v6, v6, 0x4

    #@3f
    and-int/lit8 v6, v6, 0xf

    #@41
    aget-byte v7, v0, v4

    #@43
    shl-int/lit8 v7, v7, 0x4

    #@45
    and-int/lit8 v7, v7, 0x3f

    #@47
    or-int/2addr v6, v7

    #@48
    int-to-byte v6, v6

    #@49
    aput-byte v6, v1, v5

    #@4b
    .line 64
    add-int/lit8 v5, v2, 0x2

    #@4d
    add-int/lit8 v6, v4, 0x2

    #@4f
    aget-byte v6, v0, v6

    #@51
    ushr-int/lit8 v6, v6, 0x6

    #@53
    and-int/lit8 v6, v6, 0x3

    #@55
    add-int/lit8 v7, v4, 0x1

    #@57
    aget-byte v7, v0, v7

    #@59
    shl-int/lit8 v7, v7, 0x2

    #@5b
    and-int/lit8 v7, v7, 0x3f

    #@5d
    or-int/2addr v6, v7

    #@5e
    int-to-byte v6, v6

    #@5f
    aput-byte v6, v1, v5

    #@61
    .line 65
    add-int/lit8 v5, v2, 0x3

    #@63
    add-int/lit8 v6, v4, 0x2

    #@65
    aget-byte v6, v0, v6

    #@67
    and-int/lit8 v6, v6, 0x3f

    #@69
    int-to-byte v6, v6

    #@6a
    aput-byte v6, v1, v5

    #@6c
    .line 61
    add-int/lit8 v4, v4, 0x3

    #@6e
    add-int/lit8 v2, v2, 0x4

    #@70
    goto :goto_1

    #@71
    .line 69
    .restart local v3    # "idx":I
    :cond_2
    aget-byte v5, v1, v3

    #@73
    const/16 v6, 0x1a

    #@75
    if-ge v5, v6, :cond_3

    #@77
    .line 70
    aget-byte v5, v1, v3

    #@79
    add-int/lit8 v5, v5, 0x41

    #@7b
    int-to-byte v5, v5

    #@7c
    aput-byte v5, v1, v3

    #@7e
    .line 68
    :goto_4
    add-int/lit8 v3, v3, 0x1

    #@80
    goto :goto_2

    #@81
    .line 71
    :cond_3
    aget-byte v5, v1, v3

    #@83
    const/16 v6, 0x34

    #@85
    if-ge v5, v6, :cond_4

    #@87
    .line 72
    aget-byte v5, v1, v3

    #@89
    add-int/lit8 v5, v5, 0x61

    #@8b
    add-int/lit8 v5, v5, -0x1a

    #@8d
    int-to-byte v5, v5

    #@8e
    aput-byte v5, v1, v3

    #@90
    goto :goto_4

    #@91
    .line 73
    :cond_4
    aget-byte v5, v1, v3

    #@93
    const/16 v6, 0x3e

    #@95
    if-ge v5, v6, :cond_5

    #@97
    .line 74
    aget-byte v5, v1, v3

    #@99
    add-int/lit8 v5, v5, 0x30

    #@9b
    add-int/lit8 v5, v5, -0x34

    #@9d
    int-to-byte v5, v5

    #@9e
    aput-byte v5, v1, v3

    #@a0
    goto :goto_4

    #@a1
    .line 75
    :cond_5
    aget-byte v5, v1, v3

    #@a3
    const/16 v6, 0x3f

    #@a5
    if-ge v5, v6, :cond_6

    #@a7
    .line 76
    const/16 v5, 0x2b

    #@a9
    aput-byte v5, v1, v3

    #@ab
    goto :goto_4

    #@ac
    .line 78
    :cond_6
    const/16 v5, 0x2f

    #@ae
    aput-byte v5, v1, v3

    #@b0
    goto :goto_4

    #@b1
    .line 83
    :cond_7
    const/16 v5, 0x3d

    #@b3
    aput-byte v5, v1, v3

    #@b5
    .line 82
    add-int/lit8 v3, v3, -0x1

    #@b7
    goto/16 :goto_3
.end method
