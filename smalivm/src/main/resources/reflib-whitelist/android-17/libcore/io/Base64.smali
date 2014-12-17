.class public final Llibcore/io/Base64;
.super Ljava/lang/Object;
.source "Base64.java"


# static fields
.field private static final map:[B


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 128
    const/16 v0, 0x40

    new-array v0, v0, [B

    fill-array-data v0, :array_a

    sput-object v0, Llibcore/io/Base64;->map:[B

    return-void

    :array_a
    .array-data 1
        0x41t
        0x42t
        0x43t
        0x44t
        0x45t
        0x46t
        0x47t
        0x48t
        0x49t
        0x4at
        0x4bt
        0x4ct
        0x4dt
        0x4et
        0x4ft
        0x50t
        0x51t
        0x52t
        0x53t
        0x54t
        0x55t
        0x56t
        0x57t
        0x58t
        0x59t
        0x5at
        0x61t
        0x62t
        0x63t
        0x64t
        0x65t
        0x66t
        0x67t
        0x68t
        0x69t
        0x6at
        0x6bt
        0x6ct
        0x6dt
        0x6et
        0x6ft
        0x70t
        0x71t
        0x72t
        0x73t
        0x74t
        0x75t
        0x76t
        0x77t
        0x78t
        0x79t
        0x7at
        0x30t
        0x31t
        0x32t
        0x33t
        0x34t
        0x35t
        0x36t
        0x37t
        0x38t
        0x39t
        0x2bt
        0x2ft
    .end array-data
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 33
    return-void
.end method

.method public static decode([B)[B
    .registers 2
    .param p0, "in"    # [B

    .prologue
    .line 36
    array-length v0, p0

    invoke-static {p0, v0}, Llibcore/io/Base64;->decode([BI)[B

    move-result-object v0

    return-object v0
.end method

.method public static decode([BI)[B
    .registers 15
    .param p0, "in"    # [B
    .param p1, "len"    # I

    .prologue
    .line 41
    div-int/lit8 v11, p1, 0x4

    mul-int/lit8 v4, v11, 0x3

    .line 43
    .local v4, "length":I
    if-nez v4, :cond_9

    .line 44
    sget-object v10, Llibcore/util/EmptyArray;->BYTE:[B

    .line 125
    :goto_8
    return-object v10

    .line 47
    :cond_9
    new-array v5, v4, [B

    .line 49
    .local v5, "out":[B
    const/4 v8, 0x0

    .line 54
    .local v8, "pad":I
    :goto_c
    add-int/lit8 v11, p1, -0x1

    aget-byte v1, p0, v11

    .line 56
    .local v1, "chr":B
    const/16 v11, 0xa

    if-eq v1, v11, :cond_20

    const/16 v11, 0xd

    if-eq v1, v11, :cond_20

    const/16 v11, 0x20

    if-eq v1, v11, :cond_20

    const/16 v11, 0x9

    if-ne v1, v11, :cond_23

    .line 53
    :cond_20
    :goto_20
    add-int/lit8 p1, p1, -0x1

    goto :goto_c

    .line 60
    :cond_23
    const/16 v11, 0x3d

    if-ne v1, v11, :cond_2a

    .line 61
    add-int/lit8 v8, v8, 0x1

    goto :goto_20

    .line 67
    :cond_2a
    const/4 v6, 0x0

    .line 69
    .local v6, "outIndex":I
    const/4 v3, 0x0

    .line 71
    .local v3, "inIndex":I
    const/4 v0, 0x0

    .line 73
    .local v0, "bits":I
    const/4 v9, 0x0

    .line 74
    .local v9, "quantum":I
    const/4 v2, 0x0

    .local v2, "i":I
    move v7, v6

    .end local v6    # "outIndex":I
    .local v7, "outIndex":I
    :goto_30
    if-ge v2, p1, :cond_9a

    .line 75
    aget-byte v1, p0, v2

    .line 77
    const/16 v11, 0xa

    if-eq v1, v11, :cond_bc

    const/16 v11, 0xd

    if-eq v1, v11, :cond_bc

    const/16 v11, 0x20

    if-eq v1, v11, :cond_bc

    const/16 v11, 0x9

    if-ne v1, v11, :cond_49

    move v6, v7

    .line 74
    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    :goto_45
    add-int/lit8 v2, v2, 0x1

    move v7, v6

    .end local v6    # "outIndex":I
    .restart local v7    # "outIndex":I
    goto :goto_30

    .line 81
    :cond_49
    const/16 v11, 0x41

    if-lt v1, v11, :cond_73

    const/16 v11, 0x5a

    if-gt v1, v11, :cond_73

    .line 85
    add-int/lit8 v0, v1, -0x41

    .line 104
    :goto_53
    shl-int/lit8 v11, v9, 0x6

    int-to-byte v12, v0

    or-int v9, v11, v12

    .line 105
    rem-int/lit8 v11, v3, 0x4

    const/4 v12, 0x3

    if-ne v11, v12, :cond_ba

    .line 107
    add-int/lit8 v6, v7, 0x1

    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    shr-int/lit8 v11, v9, 0x10

    int-to-byte v11, v11

    aput-byte v11, v5, v7

    .line 108
    add-int/lit8 v7, v6, 0x1

    .end local v6    # "outIndex":I
    .restart local v7    # "outIndex":I
    shr-int/lit8 v11, v9, 0x8

    int-to-byte v11, v11

    aput-byte v11, v5, v6

    .line 109
    add-int/lit8 v6, v7, 0x1

    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    int-to-byte v11, v9

    aput-byte v11, v5, v7

    .line 111
    :goto_70
    add-int/lit8 v3, v3, 0x1

    goto :goto_45

    .line 86
    .end local v6    # "outIndex":I
    .restart local v7    # "outIndex":I
    :cond_73
    const/16 v11, 0x61

    if-lt v1, v11, :cond_7e

    const/16 v11, 0x7a

    if-gt v1, v11, :cond_7e

    .line 90
    add-int/lit8 v0, v1, -0x47

    goto :goto_53

    .line 91
    :cond_7e
    const/16 v11, 0x30

    if-lt v1, v11, :cond_89

    const/16 v11, 0x39

    if-gt v1, v11, :cond_89

    .line 95
    add-int/lit8 v0, v1, 0x4

    goto :goto_53

    .line 96
    :cond_89
    const/16 v11, 0x2b

    if-ne v1, v11, :cond_90

    .line 97
    const/16 v0, 0x3e

    goto :goto_53

    .line 98
    :cond_90
    const/16 v11, 0x2f

    if-ne v1, v11, :cond_97

    .line 99
    const/16 v0, 0x3f

    goto :goto_53

    .line 101
    :cond_97
    const/4 v10, 0x0

    goto/16 :goto_8

    .line 113
    :cond_9a
    if-lez v8, :cond_b0

    .line 115
    mul-int/lit8 v11, v8, 0x6

    shl-int/2addr v9, v11

    .line 117
    add-int/lit8 v6, v7, 0x1

    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    shr-int/lit8 v11, v9, 0x10

    int-to-byte v11, v11

    aput-byte v11, v5, v7

    .line 118
    const/4 v11, 0x1

    if-ne v8, v11, :cond_b1

    .line 119
    add-int/lit8 v7, v6, 0x1

    .end local v6    # "outIndex":I
    .restart local v7    # "outIndex":I
    shr-int/lit8 v11, v9, 0x8

    int-to-byte v11, v11

    aput-byte v11, v5, v6

    :cond_b0
    move v6, v7

    .line 123
    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    :cond_b1
    new-array v10, v6, [B

    .line 124
    .local v10, "result":[B
    const/4 v11, 0x0

    const/4 v12, 0x0

    invoke-static {v5, v11, v10, v12, v6}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    goto/16 :goto_8

    .end local v6    # "outIndex":I
    .end local v10    # "result":[B
    .restart local v7    # "outIndex":I
    :cond_ba
    move v6, v7

    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    goto :goto_70

    .end local v6    # "outIndex":I
    .restart local v7    # "outIndex":I
    :cond_bc
    move v6, v7

    .end local v7    # "outIndex":I
    .restart local v6    # "outIndex":I
    goto :goto_45
.end method

.method public static encode([B)Ljava/lang/String;
    .registers 11
    .param p0, "in"    # [B

    .prologue
    const/16 v9, 0x3d

    .line 136
    array-length v6, p0

    add-int/lit8 v6, v6, 0x2

    mul-int/lit8 v6, v6, 0x4

    div-int/lit8 v4, v6, 0x3

    .line 137
    .local v4, "length":I
    new-array v5, v4, [B

    .line 138
    .local v5, "out":[B
    const/4 v2, 0x0

    .local v2, "index":I
    array-length v6, p0

    array-length v7, p0

    rem-int/lit8 v7, v7, 0x3

    sub-int v0, v6, v7

    .line 139
    .local v0, "end":I
    const/4 v1, 0x0

    .local v1, "i":I
    move v3, v2

    .end local v2    # "index":I
    .local v3, "index":I
    :goto_14
    if-ge v1, v0, :cond_65

    .line 140
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v1

    and-int/lit16 v7, v7, 0xff

    shr-int/lit8 v7, v7, 0x2

    aget-byte v6, v6, v7

    aput-byte v6, v5, v3

    .line 141
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v1

    and-int/lit8 v7, v7, 0x3

    shl-int/lit8 v7, v7, 0x4

    add-int/lit8 v8, v1, 0x1

    aget-byte v8, p0, v8

    and-int/lit16 v8, v8, 0xff

    shr-int/lit8 v8, v8, 0x4

    or-int/2addr v7, v8

    aget-byte v6, v6, v7

    aput-byte v6, v5, v2

    .line 142
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    add-int/lit8 v7, v1, 0x1

    aget-byte v7, p0, v7

    and-int/lit8 v7, v7, 0xf

    shl-int/lit8 v7, v7, 0x2

    add-int/lit8 v8, v1, 0x2

    aget-byte v8, p0, v8

    and-int/lit16 v8, v8, 0xff

    shr-int/lit8 v8, v8, 0x6

    or-int/2addr v7, v8

    aget-byte v6, v6, v7

    aput-byte v6, v5, v3

    .line 143
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    add-int/lit8 v7, v1, 0x2

    aget-byte v7, p0, v7

    and-int/lit8 v7, v7, 0x3f

    aget-byte v6, v6, v7

    aput-byte v6, v5, v2

    .line 139
    add-int/lit8 v1, v1, 0x3

    goto :goto_14

    .line 145
    :cond_65
    array-length v6, p0

    rem-int/lit8 v6, v6, 0x3

    packed-switch v6, :pswitch_data_d6

    :goto_6b
    move v2, v3

    .line 159
    .end local v3    # "index":I
    .restart local v2    # "index":I
    :goto_6c
    new-instance v6, Ljava/lang/String;

    const/4 v7, 0x0

    sget-object v8, Ljava/nio/charset/Charsets;->US_ASCII:Ljava/nio/charset/Charset;

    invoke-direct {v6, v5, v7, v2, v8}, Ljava/lang/String;-><init>([BIILjava/nio/charset/Charset;)V

    return-object v6

    .line 147
    .end local v2    # "index":I
    .restart local v3    # "index":I
    :pswitch_75
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v0

    and-int/lit16 v7, v7, 0xff

    shr-int/lit8 v7, v7, 0x2

    aget-byte v6, v6, v7

    aput-byte v6, v5, v3

    .line 148
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v0

    and-int/lit8 v7, v7, 0x3

    shl-int/lit8 v7, v7, 0x4

    aget-byte v6, v6, v7

    aput-byte v6, v5, v2

    .line 149
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    aput-byte v9, v5, v3

    .line 150
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    aput-byte v9, v5, v2

    move v2, v3

    .line 151
    .end local v3    # "index":I
    .restart local v2    # "index":I
    goto :goto_6c

    .line 153
    .end local v2    # "index":I
    .restart local v3    # "index":I
    :pswitch_9b
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v0

    and-int/lit16 v7, v7, 0xff

    shr-int/lit8 v7, v7, 0x2

    aget-byte v6, v6, v7

    aput-byte v6, v5, v3

    .line 154
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    aget-byte v7, p0, v0

    and-int/lit8 v7, v7, 0x3

    shl-int/lit8 v7, v7, 0x4

    add-int/lit8 v8, v0, 0x1

    aget-byte v8, p0, v8

    and-int/lit16 v8, v8, 0xff

    shr-int/lit8 v8, v8, 0x4

    or-int/2addr v7, v8

    aget-byte v6, v6, v7

    aput-byte v6, v5, v2

    .line 155
    add-int/lit8 v2, v3, 0x1

    .end local v3    # "index":I
    .restart local v2    # "index":I
    sget-object v6, Llibcore/io/Base64;->map:[B

    add-int/lit8 v7, v0, 0x1

    aget-byte v7, p0, v7

    and-int/lit8 v7, v7, 0xf

    shl-int/lit8 v7, v7, 0x2

    aget-byte v6, v6, v7

    aput-byte v6, v5, v3

    .line 156
    add-int/lit8 v3, v2, 0x1

    .end local v2    # "index":I
    .restart local v3    # "index":I
    aput-byte v9, v5, v2

    goto :goto_6b

    .line 145
    nop

    :pswitch_data_d6
    .packed-switch 0x1
        :pswitch_75
        :pswitch_9b
    .end packed-switch
.end method
