.class Lorg/cf/obfuscated/Base64$Decoder;
.super Lorg/cf/obfuscated/Base64$Coder;
.source "Base64.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/cf/obfuscated/Base64;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "Decoder"
.end annotation


# static fields
.field private static final DECODE:[I

.field private static final DECODE_WEBSAFE:[I

.field private static final EQUALS:I = -0x2

.field private static final SKIP:I = -0x1


# instance fields
.field private final alphabet:[I

.field private state:I

.field private value:I


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    const/16 v1, 0x100

    #@2
    .line 181
    new-array v0, v1, [I

    #@4
    fill-array-data v0, :array_0

    #@7
    sput-object v0, Lorg/cf/obfuscated/Base64$Decoder;->DECODE:[I

    #@9
    .line 204
    new-array v0, v1, [I

    #@b
    fill-array-data v0, :array_1

    #@e
    sput-object v0, Lorg/cf/obfuscated/Base64$Decoder;->DECODE_WEBSAFE:[I

    #@10
    return-void

    #@11
    .line 181
    nop

    #@12
    :array_0
    .array-data 4
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        0x3e
        -0x1
        -0x1
        -0x1
        0x3f
        0x34
        0x35
        0x36
        0x37
        0x38
        0x39
        0x3a
        0x3b
        0x3c
        0x3d
        -0x1
        -0x1
        -0x1
        -0x2
        -0x1
        -0x1
        -0x1
        0x0
        0x1
        0x2
        0x3
        0x4
        0x5
        0x6
        0x7
        0x8
        0x9
        0xa
        0xb
        0xc
        0xd
        0xe
        0xf
        0x10
        0x11
        0x12
        0x13
        0x14
        0x15
        0x16
        0x17
        0x18
        0x19
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        0x1a
        0x1b
        0x1c
        0x1d
        0x1e
        0x1f
        0x20
        0x21
        0x22
        0x23
        0x24
        0x25
        0x26
        0x27
        0x28
        0x29
        0x2a
        0x2b
        0x2c
        0x2d
        0x2e
        0x2f
        0x30
        0x31
        0x32
        0x33
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
    .end array-data

    #@216
    .line 204
    :array_1
    .array-data 4
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        0x3e
        -0x1
        -0x1
        0x34
        0x35
        0x36
        0x37
        0x38
        0x39
        0x3a
        0x3b
        0x3c
        0x3d
        -0x1
        -0x1
        -0x1
        -0x2
        -0x1
        -0x1
        -0x1
        0x0
        0x1
        0x2
        0x3
        0x4
        0x5
        0x6
        0x7
        0x8
        0x9
        0xa
        0xb
        0xc
        0xd
        0xe
        0xf
        0x10
        0x11
        0x12
        0x13
        0x14
        0x15
        0x16
        0x17
        0x18
        0x19
        -0x1
        -0x1
        -0x1
        -0x1
        0x3f
        -0x1
        0x1a
        0x1b
        0x1c
        0x1d
        0x1e
        0x1f
        0x20
        0x21
        0x22
        0x23
        0x24
        0x25
        0x26
        0x27
        0x28
        0x29
        0x2a
        0x2b
        0x2c
        0x2d
        0x2e
        0x2f
        0x30
        0x31
        0x32
        0x33
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
        -0x1
    .end array-data
.end method

.method public constructor <init>(I[B)V
    .locals 2
    .param p1, "flags"    # I
    .param p2, "output"    # [B

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 241
    invoke-direct {p0}, Lorg/cf/obfuscated/Base64$Coder;-><init>()V

    #@4
    .line 242
    iput-object p2, p0, Lorg/cf/obfuscated/Base64$Decoder;->output:[B

    #@6
    .line 244
    and-int/lit8 v0, p1, 0x8

    #@8
    if-nez v0, :cond_0

    #@a
    sget-object v0, Lorg/cf/obfuscated/Base64$Decoder;->DECODE:[I

    #@c
    :goto_0
    iput-object v0, p0, Lorg/cf/obfuscated/Base64$Decoder;->alphabet:[I

    #@e
    .line 245
    iput v1, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@10
    .line 246
    iput v1, p0, Lorg/cf/obfuscated/Base64$Decoder;->value:I

    #@12
    .line 247
    return-void

    #@13
    .line 244
    :cond_0
    sget-object v0, Lorg/cf/obfuscated/Base64$Decoder;->DECODE_WEBSAFE:[I

    #@15
    goto :goto_0
.end method


# virtual methods
.method public maxOutputSize(I)I
    .locals 1
    .param p1, "len"    # I

    #@0
    .prologue
    .line 254
    mul-int/lit8 v0, p1, 0x3

    #@2
    div-int/lit8 v0, v0, 0x4

    #@4
    add-int/lit8 v0, v0, 0xa

    #@6
    return v0
.end method

.method public process([BIIZ)Z
    .locals 11
    .param p1, "input"    # [B
    .param p2, "offset"    # I
    .param p3, "len"    # I
    .param p4, "finish"    # Z

    #@0
    .prologue
    .line 264
    iget v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@2
    const/4 v10, 0x6

    #@3
    if-ne v9, v10, :cond_0

    #@5
    const/4 v9, 0x0

    #@6
    .line 437
    :goto_0
    return v9

    #@7
    .line 266
    :cond_0
    move v5, p2

    #@8
    .line 267
    .local v5, "p":I
    add-int/2addr p3, p2

    #@9
    .line 274
    iget v7, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@b
    .line 275
    .local v7, "state":I
    iget v8, p0, Lorg/cf/obfuscated/Base64$Decoder;->value:I

    #@d
    .line 276
    .local v8, "value":I
    const/4 v2, 0x0

    #@e
    .line 277
    .local v2, "op":I
    iget-object v4, p0, Lorg/cf/obfuscated/Base64$Decoder;->output:[B

    #@10
    .line 278
    .local v4, "output":[B
    iget-object v0, p0, Lorg/cf/obfuscated/Base64$Decoder;->alphabet:[I

    #@12
    .line 280
    .local v0, "alphabet":[I
    :goto_1
    if-ge v5, p3, :cond_c

    #@14
    .line 295
    if-nez v7, :cond_2

    #@16
    .line 296
    :goto_2
    add-int/lit8 v9, v5, 0x4

    #@18
    if-gt v9, p3, :cond_1

    #@1a
    aget-byte v9, p1, v5

    #@1c
    and-int/lit16 v9, v9, 0xff

    #@1e
    aget v9, v0, v9

    #@20
    shl-int/lit8 v9, v9, 0x12

    #@22
    add-int/lit8 v10, v5, 0x1

    #@24
    aget-byte v10, p1, v10

    #@26
    and-int/lit16 v10, v10, 0xff

    #@28
    aget v10, v0, v10

    #@2a
    shl-int/lit8 v10, v10, 0xc

    #@2c
    or-int/2addr v9, v10

    #@2d
    add-int/lit8 v10, v5, 0x2

    #@2f
    aget-byte v10, p1, v10

    #@31
    and-int/lit16 v10, v10, 0xff

    #@33
    aget v10, v0, v10

    #@35
    shl-int/lit8 v10, v10, 0x6

    #@37
    or-int/2addr v9, v10

    #@38
    add-int/lit8 v10, v5, 0x3

    #@3a
    aget-byte v10, p1, v10

    #@3c
    and-int/lit16 v10, v10, 0xff

    #@3e
    aget v10, v0, v10

    #@40
    or-int v8, v9, v10

    #@42
    if-ltz v8, :cond_1

    #@44
    .line 301
    add-int/lit8 v9, v2, 0x2

    #@46
    int-to-byte v10, v8

    #@47
    aput-byte v10, v4, v9

    #@49
    .line 302
    add-int/lit8 v9, v2, 0x1

    #@4b
    shr-int/lit8 v10, v8, 0x8

    #@4d
    int-to-byte v10, v10

    #@4e
    aput-byte v10, v4, v9

    #@50
    .line 303
    shr-int/lit8 v9, v8, 0x10

    #@52
    int-to-byte v9, v9

    #@53
    aput-byte v9, v4, v2

    #@55
    .line 304
    add-int/lit8 v2, v2, 0x3

    #@57
    .line 305
    add-int/lit8 v5, v5, 0x4

    #@59
    goto :goto_2

    #@5a
    .line 307
    :cond_1
    if-lt v5, p3, :cond_2

    #@5c
    move v3, v2

    #@5d
    .line 393
    .end local v2    # "op":I
    .local v3, "op":I
    :goto_3
    if-nez p4, :cond_b

    #@5f
    .line 396
    iput v7, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@61
    .line 397
    iput v8, p0, Lorg/cf/obfuscated/Base64$Decoder;->value:I

    #@63
    .line 398
    iput v3, p0, Lorg/cf/obfuscated/Base64$Decoder;->op:I

    #@65
    .line 399
    const/4 v9, 0x1

    #@66
    goto :goto_0

    #@67
    .line 315
    .end local v3    # "op":I
    .restart local v2    # "op":I
    :cond_2
    add-int/lit8 v6, v5, 0x1

    #@69
    .end local v5    # "p":I
    .local v6, "p":I
    aget-byte v9, p1, v5

    #@6b
    and-int/lit16 v9, v9, 0xff

    #@6d
    aget v1, v0, v9

    #@6f
    .line 317
    .local v1, "d":I
    packed-switch v7, :pswitch_data_0

    #@72
    :cond_3
    :goto_4
    move v5, v6

    #@73
    .line 391
    .end local v6    # "p":I
    .restart local v5    # "p":I
    goto :goto_1

    #@74
    .line 319
    .end local v5    # "p":I
    .restart local v6    # "p":I
    :pswitch_0
    if-ltz v1, :cond_4

    #@76
    .line 320
    move v8, v1

    #@77
    .line 321
    add-int/lit8 v7, v7, 0x1

    #@79
    goto :goto_4

    #@7a
    .line 322
    :cond_4
    const/4 v9, -0x1

    #@7b
    if-eq v1, v9, :cond_3

    #@7d
    .line 323
    const/4 v9, 0x6

    #@7e
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@80
    .line 324
    const/4 v9, 0x0

    #@81
    goto :goto_0

    #@82
    .line 329
    :pswitch_1
    if-ltz v1, :cond_5

    #@84
    .line 330
    shl-int/lit8 v9, v8, 0x6

    #@86
    or-int v8, v9, v1

    #@88
    .line 331
    add-int/lit8 v7, v7, 0x1

    #@8a
    goto :goto_4

    #@8b
    .line 332
    :cond_5
    const/4 v9, -0x1

    #@8c
    if-eq v1, v9, :cond_3

    #@8e
    .line 333
    const/4 v9, 0x6

    #@8f
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@91
    .line 334
    const/4 v9, 0x0

    #@92
    goto/16 :goto_0

    #@94
    .line 339
    :pswitch_2
    if-ltz v1, :cond_6

    #@96
    .line 340
    shl-int/lit8 v9, v8, 0x6

    #@98
    or-int v8, v9, v1

    #@9a
    .line 341
    add-int/lit8 v7, v7, 0x1

    #@9c
    goto :goto_4

    #@9d
    .line 342
    :cond_6
    const/4 v9, -0x2

    #@9e
    if-ne v1, v9, :cond_7

    #@a0
    .line 345
    add-int/lit8 v3, v2, 0x1

    #@a2
    .end local v2    # "op":I
    .restart local v3    # "op":I
    shr-int/lit8 v9, v8, 0x4

    #@a4
    int-to-byte v9, v9

    #@a5
    aput-byte v9, v4, v2

    #@a7
    .line 346
    const/4 v7, 0x4

    #@a8
    move v2, v3

    #@a9
    .end local v3    # "op":I
    .restart local v2    # "op":I
    goto :goto_4

    #@aa
    .line 347
    :cond_7
    const/4 v9, -0x1

    #@ab
    if-eq v1, v9, :cond_3

    #@ad
    .line 348
    const/4 v9, 0x6

    #@ae
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@b0
    .line 349
    const/4 v9, 0x0

    #@b1
    goto/16 :goto_0

    #@b3
    .line 354
    :pswitch_3
    if-ltz v1, :cond_8

    #@b5
    .line 356
    shl-int/lit8 v9, v8, 0x6

    #@b7
    or-int v8, v9, v1

    #@b9
    .line 357
    add-int/lit8 v9, v2, 0x2

    #@bb
    int-to-byte v10, v8

    #@bc
    aput-byte v10, v4, v9

    #@be
    .line 358
    add-int/lit8 v9, v2, 0x1

    #@c0
    shr-int/lit8 v10, v8, 0x8

    #@c2
    int-to-byte v10, v10

    #@c3
    aput-byte v10, v4, v9

    #@c5
    .line 359
    shr-int/lit8 v9, v8, 0x10

    #@c7
    int-to-byte v9, v9

    #@c8
    aput-byte v9, v4, v2

    #@ca
    .line 360
    add-int/lit8 v2, v2, 0x3

    #@cc
    .line 361
    const/4 v7, 0x0

    #@cd
    goto :goto_4

    #@ce
    .line 362
    :cond_8
    const/4 v9, -0x2

    #@cf
    if-ne v1, v9, :cond_9

    #@d1
    .line 365
    add-int/lit8 v9, v2, 0x1

    #@d3
    shr-int/lit8 v10, v8, 0x2

    #@d5
    int-to-byte v10, v10

    #@d6
    aput-byte v10, v4, v9

    #@d8
    .line 366
    shr-int/lit8 v9, v8, 0xa

    #@da
    int-to-byte v9, v9

    #@db
    aput-byte v9, v4, v2

    #@dd
    .line 367
    add-int/lit8 v2, v2, 0x2

    #@df
    .line 368
    const/4 v7, 0x5

    #@e0
    goto :goto_4

    #@e1
    .line 369
    :cond_9
    const/4 v9, -0x1

    #@e2
    if-eq v1, v9, :cond_3

    #@e4
    .line 370
    const/4 v9, 0x6

    #@e5
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@e7
    .line 371
    const/4 v9, 0x0

    #@e8
    goto/16 :goto_0

    #@ea
    .line 376
    :pswitch_4
    const/4 v9, -0x2

    #@eb
    if-ne v1, v9, :cond_a

    #@ed
    .line 377
    add-int/lit8 v7, v7, 0x1

    #@ef
    goto :goto_4

    #@f0
    .line 378
    :cond_a
    const/4 v9, -0x1

    #@f1
    if-eq v1, v9, :cond_3

    #@f3
    .line 379
    const/4 v9, 0x6

    #@f4
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@f6
    .line 380
    const/4 v9, 0x0

    #@f7
    goto/16 :goto_0

    #@f9
    .line 385
    :pswitch_5
    const/4 v9, -0x1

    #@fa
    if-eq v1, v9, :cond_3

    #@fc
    .line 386
    const/4 v9, 0x6

    #@fd
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@ff
    .line 387
    const/4 v9, 0x0

    #@100
    goto/16 :goto_0

    #@102
    .line 405
    .end local v1    # "d":I
    .end local v2    # "op":I
    .end local v6    # "p":I
    .restart local v3    # "op":I
    .restart local v5    # "p":I
    :cond_b
    packed-switch v7, :pswitch_data_1

    #@105
    move v2, v3

    #@106
    .line 435
    .end local v3    # "op":I
    .restart local v2    # "op":I
    :goto_5
    iput v7, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@108
    .line 436
    iput v2, p0, Lorg/cf/obfuscated/Base64$Decoder;->op:I

    #@10a
    .line 437
    const/4 v9, 0x1

    #@10b
    goto/16 :goto_0

    #@10d
    .end local v2    # "op":I
    .restart local v3    # "op":I
    :pswitch_6
    move v2, v3

    #@10e
    .line 408
    .end local v3    # "op":I
    .restart local v2    # "op":I
    goto :goto_5

    #@10f
    .line 412
    .end local v2    # "op":I
    .restart local v3    # "op":I
    :pswitch_7
    const/4 v9, 0x6

    #@110
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@112
    .line 413
    const/4 v9, 0x0

    #@113
    goto/16 :goto_0

    #@115
    .line 417
    :pswitch_8
    add-int/lit8 v2, v3, 0x1

    #@117
    .end local v3    # "op":I
    .restart local v2    # "op":I
    shr-int/lit8 v9, v8, 0x4

    #@119
    int-to-byte v9, v9

    #@11a
    aput-byte v9, v4, v3

    #@11c
    goto :goto_5

    #@11d
    .line 422
    .end local v2    # "op":I
    .restart local v3    # "op":I
    :pswitch_9
    add-int/lit8 v2, v3, 0x1

    #@11f
    .end local v3    # "op":I
    .restart local v2    # "op":I
    shr-int/lit8 v9, v8, 0xa

    #@121
    int-to-byte v9, v9

    #@122
    aput-byte v9, v4, v3

    #@124
    .line 423
    add-int/lit8 v3, v2, 0x1

    #@126
    .end local v2    # "op":I
    .restart local v3    # "op":I
    shr-int/lit8 v9, v8, 0x2

    #@128
    int-to-byte v9, v9

    #@129
    aput-byte v9, v4, v2

    #@12b
    move v2, v3

    #@12c
    .line 424
    .end local v3    # "op":I
    .restart local v2    # "op":I
    goto :goto_5

    #@12d
    .line 427
    .end local v2    # "op":I
    .restart local v3    # "op":I
    :pswitch_a
    const/4 v9, 0x6

    #@12e
    iput v9, p0, Lorg/cf/obfuscated/Base64$Decoder;->state:I

    #@130
    .line 428
    const/4 v9, 0x0

    #@131
    goto/16 :goto_0

    #@133
    .end local v3    # "op":I
    .restart local v2    # "op":I
    :cond_c
    move v3, v2

    #@134
    .end local v2    # "op":I
    .restart local v3    # "op":I
    goto/16 :goto_3

    #@136
    .line 317
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
    .end packed-switch

    #@146
    .line 405
    :pswitch_data_1
    .packed-switch 0x0
        :pswitch_6
        :pswitch_7
        :pswitch_8
        :pswitch_9
        :pswitch_a
    .end packed-switch
.end method
