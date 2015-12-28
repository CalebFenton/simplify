.class Lorg/cf/obfuscated/Base64$Encoder;
.super Lorg/cf/obfuscated/Base64$Coder;
.source "Base64.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/cf/obfuscated/Base64;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "Encoder"
.end annotation


# static fields
.field static final synthetic $assertionsDisabled:Z

.field private static final ENCODE:[B

.field private static final ENCODE_WEBSAFE:[B

.field public static final LINE_GROUPS:I = 0x13


# instance fields
.field private final alphabet:[B

.field private count:I

.field public final do_cr:Z

.field public final do_newline:Z

.field public final do_padding:Z

.field private final tail:[B

.field tailLen:I


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    const/16 v1, 0x40

    #@2
    .line 542
    const-class v0, Lorg/cf/obfuscated/Base64;

    #@4
    invoke-virtual {v0}, Ljava/lang/Class;->desiredAssertionStatus()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    sput-boolean v0, Lorg/cf/obfuscated/Base64$Encoder;->$assertionsDisabled:Z

    #@d
    .line 554
    new-array v0, v1, [B

    #@f
    fill-array-data v0, :array_0

    #@12
    sput-object v0, Lorg/cf/obfuscated/Base64$Encoder;->ENCODE:[B

    #@14
    .line 565
    new-array v0, v1, [B

    #@16
    fill-array-data v0, :array_1

    #@19
    sput-object v0, Lorg/cf/obfuscated/Base64$Encoder;->ENCODE_WEBSAFE:[B

    #@1b
    return-void

    #@1c
    .line 542
    :cond_0
    const/4 v0, 0x0

    #@1d
    goto :goto_0

    #@1e
    .line 554
    :array_0
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

    #@42
    .line 565
    :array_1
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
        0x2dt
        0x5ft
    .end array-data
.end method

.method public constructor <init>(I[B)V
    .locals 3
    .param p1, "flags"    # I
    .param p2, "output"    # [B

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 581
    invoke-direct {p0}, Lorg/cf/obfuscated/Base64$Coder;-><init>()V

    #@5
    .line 582
    iput-object p2, p0, Lorg/cf/obfuscated/Base64$Encoder;->output:[B

    #@7
    .line 584
    and-int/lit8 v0, p1, 0x1

    #@9
    if-nez v0, :cond_0

    #@b
    move v0, v1

    #@c
    :goto_0
    iput-boolean v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_padding:Z

    #@e
    .line 585
    and-int/lit8 v0, p1, 0x2

    #@10
    if-nez v0, :cond_1

    #@12
    move v0, v1

    #@13
    :goto_1
    iput-boolean v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@15
    .line 586
    and-int/lit8 v0, p1, 0x4

    #@17
    if-eqz v0, :cond_2

    #@19
    :goto_2
    iput-boolean v1, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@1b
    .line 587
    and-int/lit8 v0, p1, 0x8

    #@1d
    if-nez v0, :cond_3

    #@1f
    sget-object v0, Lorg/cf/obfuscated/Base64$Encoder;->ENCODE:[B

    #@21
    :goto_3
    iput-object v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->alphabet:[B

    #@23
    .line 589
    const/4 v0, 0x2

    #@24
    new-array v0, v0, [B

    #@26
    iput-object v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@28
    .line 590
    iput v2, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@2a
    .line 592
    iget-boolean v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@2c
    if-eqz v0, :cond_4

    #@2e
    const/16 v0, 0x13

    #@30
    :goto_4
    iput v0, p0, Lorg/cf/obfuscated/Base64$Encoder;->count:I

    #@32
    .line 593
    return-void

    #@33
    :cond_0
    move v0, v2

    #@34
    .line 584
    goto :goto_0

    #@35
    :cond_1
    move v0, v2

    #@36
    .line 585
    goto :goto_1

    #@37
    :cond_2
    move v1, v2

    #@38
    .line 586
    goto :goto_2

    #@39
    .line 587
    :cond_3
    sget-object v0, Lorg/cf/obfuscated/Base64$Encoder;->ENCODE_WEBSAFE:[B

    #@3b
    goto :goto_3

    #@3c
    .line 592
    :cond_4
    const/4 v0, -0x1

    #@3d
    goto :goto_4
.end method


# virtual methods
.method public maxOutputSize(I)I
    .locals 1
    .param p1, "len"    # I

    #@0
    .prologue
    .line 600
    mul-int/lit8 v0, p1, 0x8

    #@2
    div-int/lit8 v0, v0, 0x5

    #@4
    add-int/lit8 v0, v0, 0xa

    #@6
    return v0
.end method

.method public process([BIIZ)Z
    .locals 14
    .param p1, "input"    # [B
    .param p2, "offset"    # I
    .param p3, "len"    # I
    .param p4, "finish"    # Z

    #@0
    .prologue
    .line 605
    iget-object v1, p0, Lorg/cf/obfuscated/Base64$Encoder;->alphabet:[B

    #@2
    .line 606
    .local v1, "alphabet":[B
    iget-object v5, p0, Lorg/cf/obfuscated/Base64$Encoder;->output:[B

    #@4
    .line 607
    .local v5, "output":[B
    const/4 v3, 0x0

    #@5
    .line 608
    .local v3, "op":I
    iget v2, p0, Lorg/cf/obfuscated/Base64$Encoder;->count:I

    #@7
    .line 610
    .local v2, "count":I
    move/from16 v6, p2

    #@9
    .line 611
    .local v6, "p":I
    add-int p3, p3, p2

    #@b
    .line 612
    const/4 v10, -0x1

    #@c
    .line 618
    .local v10, "v":I
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@e
    packed-switch v11, :pswitch_data_0

    #@11
    .line 645
    :cond_0
    :goto_0
    :pswitch_0
    const/4 v11, -0x1

    #@12
    if-eq v10, v11, :cond_15

    #@14
    .line 646
    add-int/lit8 v4, v3, 0x1

    #@16
    .end local v3    # "op":I
    .local v4, "op":I
    shr-int/lit8 v11, v10, 0x12

    #@18
    and-int/lit8 v11, v11, 0x3f

    #@1a
    aget-byte v11, v1, v11

    #@1c
    aput-byte v11, v5, v3

    #@1e
    .line 647
    add-int/lit8 v3, v4, 0x1

    #@20
    .end local v4    # "op":I
    .restart local v3    # "op":I
    shr-int/lit8 v11, v10, 0xc

    #@22
    and-int/lit8 v11, v11, 0x3f

    #@24
    aget-byte v11, v1, v11

    #@26
    aput-byte v11, v5, v4

    #@28
    .line 648
    add-int/lit8 v4, v3, 0x1

    #@2a
    .end local v3    # "op":I
    .restart local v4    # "op":I
    shr-int/lit8 v11, v10, 0x6

    #@2c
    and-int/lit8 v11, v11, 0x3f

    #@2e
    aget-byte v11, v1, v11

    #@30
    aput-byte v11, v5, v3

    #@32
    .line 649
    add-int/lit8 v3, v4, 0x1

    #@34
    .end local v4    # "op":I
    .restart local v3    # "op":I
    and-int/lit8 v11, v10, 0x3f

    #@36
    aget-byte v11, v1, v11

    #@38
    aput-byte v11, v5, v4

    #@3a
    .line 650
    add-int/lit8 v2, v2, -0x1

    #@3c
    if-nez v2, :cond_15

    #@3e
    .line 651
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@40
    if-eqz v11, :cond_1

    #@42
    add-int/lit8 v4, v3, 0x1

    #@44
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xd

    #@46
    aput-byte v11, v5, v3

    #@48
    move v3, v4

    #@49
    .line 652
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_1
    add-int/lit8 v4, v3, 0x1

    #@4b
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xa

    #@4d
    aput-byte v11, v5, v3

    #@4f
    .line 653
    const/16 v2, 0x13

    #@51
    move v7, v6

    #@52
    .line 662
    .end local v6    # "p":I
    .local v7, "p":I
    :goto_1
    add-int/lit8 v11, v7, 0x3

    #@54
    move/from16 v0, p3

    #@56
    if-gt v11, v0, :cond_3

    #@58
    .line 663
    aget-byte v11, p1, v7

    #@5a
    and-int/lit16 v11, v11, 0xff

    #@5c
    shl-int/lit8 v11, v11, 0x10

    #@5e
    add-int/lit8 v12, v7, 0x1

    #@60
    aget-byte v12, p1, v12

    #@62
    and-int/lit16 v12, v12, 0xff

    #@64
    shl-int/lit8 v12, v12, 0x8

    #@66
    or-int/2addr v11, v12

    #@67
    add-int/lit8 v12, v7, 0x2

    #@69
    aget-byte v12, p1, v12

    #@6b
    and-int/lit16 v12, v12, 0xff

    #@6d
    or-int v10, v11, v12

    #@6f
    .line 666
    shr-int/lit8 v11, v10, 0x12

    #@71
    and-int/lit8 v11, v11, 0x3f

    #@73
    aget-byte v11, v1, v11

    #@75
    aput-byte v11, v5, v4

    #@77
    .line 667
    add-int/lit8 v11, v4, 0x1

    #@79
    shr-int/lit8 v12, v10, 0xc

    #@7b
    and-int/lit8 v12, v12, 0x3f

    #@7d
    aget-byte v12, v1, v12

    #@7f
    aput-byte v12, v5, v11

    #@81
    .line 668
    add-int/lit8 v11, v4, 0x2

    #@83
    shr-int/lit8 v12, v10, 0x6

    #@85
    and-int/lit8 v12, v12, 0x3f

    #@87
    aget-byte v12, v1, v12

    #@89
    aput-byte v12, v5, v11

    #@8b
    .line 669
    add-int/lit8 v11, v4, 0x3

    #@8d
    and-int/lit8 v12, v10, 0x3f

    #@8f
    aget-byte v12, v1, v12

    #@91
    aput-byte v12, v5, v11

    #@93
    .line 670
    add-int/lit8 v6, v7, 0x3

    #@95
    .line 671
    .end local v7    # "p":I
    .restart local v6    # "p":I
    add-int/lit8 v3, v4, 0x4

    #@97
    .line 672
    .end local v4    # "op":I
    .restart local v3    # "op":I
    add-int/lit8 v2, v2, -0x1

    #@99
    if-nez v2, :cond_15

    #@9b
    .line 673
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@9d
    if-eqz v11, :cond_2

    #@9f
    add-int/lit8 v4, v3, 0x1

    #@a1
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xd

    #@a3
    aput-byte v11, v5, v3

    #@a5
    move v3, v4

    #@a6
    .line 674
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_2
    add-int/lit8 v4, v3, 0x1

    #@a8
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xa

    #@aa
    aput-byte v11, v5, v3

    #@ac
    .line 675
    const/16 v2, 0x13

    #@ae
    move v7, v6

    #@af
    .end local v6    # "p":I
    .restart local v7    # "p":I
    goto :goto_1

    #@b0
    .line 624
    .end local v4    # "op":I
    .end local v7    # "p":I
    .restart local v3    # "op":I
    .restart local v6    # "p":I
    :pswitch_1
    add-int/lit8 v11, v6, 0x2

    #@b2
    move/from16 v0, p3

    #@b4
    if-gt v11, v0, :cond_0

    #@b6
    .line 627
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@b8
    const/4 v12, 0x0

    #@b9
    aget-byte v11, v11, v12

    #@bb
    and-int/lit16 v11, v11, 0xff

    #@bd
    shl-int/lit8 v11, v11, 0x10

    #@bf
    add-int/lit8 v7, v6, 0x1

    #@c1
    .end local v6    # "p":I
    .restart local v7    # "p":I
    aget-byte v12, p1, v6

    #@c3
    and-int/lit16 v12, v12, 0xff

    #@c5
    shl-int/lit8 v12, v12, 0x8

    #@c7
    or-int/2addr v11, v12

    #@c8
    add-int/lit8 v6, v7, 0x1

    #@ca
    .end local v7    # "p":I
    .restart local v6    # "p":I
    aget-byte v12, p1, v7

    #@cc
    and-int/lit16 v12, v12, 0xff

    #@ce
    or-int v10, v11, v12

    #@d0
    .line 630
    const/4 v11, 0x0

    #@d1
    iput v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@d3
    goto/16 :goto_0

    #@d5
    .line 635
    :pswitch_2
    add-int/lit8 v11, v6, 0x1

    #@d7
    move/from16 v0, p3

    #@d9
    if-gt v11, v0, :cond_0

    #@db
    .line 637
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@dd
    const/4 v12, 0x0

    #@de
    aget-byte v11, v11, v12

    #@e0
    and-int/lit16 v11, v11, 0xff

    #@e2
    shl-int/lit8 v11, v11, 0x10

    #@e4
    iget-object v12, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@e6
    const/4 v13, 0x1

    #@e7
    aget-byte v12, v12, v13

    #@e9
    and-int/lit16 v12, v12, 0xff

    #@eb
    shl-int/lit8 v12, v12, 0x8

    #@ed
    or-int/2addr v11, v12

    #@ee
    add-int/lit8 v7, v6, 0x1

    #@f0
    .end local v6    # "p":I
    .restart local v7    # "p":I
    aget-byte v12, p1, v6

    #@f2
    and-int/lit16 v12, v12, 0xff

    #@f4
    or-int v10, v11, v12

    #@f6
    .line 640
    const/4 v11, 0x0

    #@f7
    iput v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@f9
    move v6, v7

    #@fa
    .end local v7    # "p":I
    .restart local v6    # "p":I
    goto/16 :goto_0

    #@fc
    .line 679
    .end local v3    # "op":I
    .end local v6    # "p":I
    .restart local v4    # "op":I
    .restart local v7    # "p":I
    :cond_3
    if-eqz p4, :cond_10

    #@fe
    .line 685
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@100
    sub-int v11, v7, v11

    #@102
    add-int/lit8 v12, p3, -0x1

    #@104
    if-ne v11, v12, :cond_8

    #@106
    .line 686
    const/4 v8, 0x0

    #@107
    .line 687
    .local v8, "t":I
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@109
    if-lez v11, :cond_7

    #@10b
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@10d
    add-int/lit8 v9, v8, 0x1

    #@10f
    .end local v8    # "t":I
    .local v9, "t":I
    aget-byte v11, v11, v8

    #@111
    move v8, v9

    #@112
    .end local v9    # "t":I
    .restart local v8    # "t":I
    move v6, v7

    #@113
    .end local v7    # "p":I
    .restart local v6    # "p":I
    :goto_2
    and-int/lit16 v11, v11, 0xff

    #@115
    shl-int/lit8 v10, v11, 0x4

    #@117
    .line 688
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@119
    sub-int/2addr v11, v8

    #@11a
    iput v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@11c
    .line 689
    add-int/lit8 v3, v4, 0x1

    #@11e
    .end local v4    # "op":I
    .restart local v3    # "op":I
    shr-int/lit8 v11, v10, 0x6

    #@120
    and-int/lit8 v11, v11, 0x3f

    #@122
    aget-byte v11, v1, v11

    #@124
    aput-byte v11, v5, v4

    #@126
    .line 690
    add-int/lit8 v4, v3, 0x1

    #@128
    .end local v3    # "op":I
    .restart local v4    # "op":I
    and-int/lit8 v11, v10, 0x3f

    #@12a
    aget-byte v11, v1, v11

    #@12c
    aput-byte v11, v5, v3

    #@12e
    .line 691
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_padding:Z

    #@130
    if-eqz v11, :cond_4

    #@132
    .line 692
    add-int/lit8 v3, v4, 0x1

    #@134
    .end local v4    # "op":I
    .restart local v3    # "op":I
    const/16 v11, 0x3d

    #@136
    aput-byte v11, v5, v4

    #@138
    .line 693
    add-int/lit8 v4, v3, 0x1

    #@13a
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0x3d

    #@13c
    aput-byte v11, v5, v3

    #@13e
    :cond_4
    move v3, v4

    #@13f
    .line 695
    .end local v4    # "op":I
    .restart local v3    # "op":I
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@141
    if-eqz v11, :cond_6

    #@143
    .line 696
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@145
    if-eqz v11, :cond_5

    #@147
    add-int/lit8 v4, v3, 0x1

    #@149
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xd

    #@14b
    aput-byte v11, v5, v3

    #@14d
    move v3, v4

    #@14e
    .line 697
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_5
    add-int/lit8 v4, v3, 0x1

    #@150
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xa

    #@152
    aput-byte v11, v5, v3

    #@154
    :goto_3
    move v3, v4

    #@155
    .line 719
    .end local v4    # "op":I
    .end local v8    # "t":I
    .restart local v3    # "op":I
    :cond_6
    :goto_4
    sget-boolean v11, Lorg/cf/obfuscated/Base64$Encoder;->$assertionsDisabled:Z

    #@157
    if-nez v11, :cond_f

    #@159
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@15b
    if-eqz v11, :cond_f

    #@15d
    new-instance v11, Ljava/lang/AssertionError;

    #@15f
    invoke-direct {v11}, Ljava/lang/AssertionError;-><init>()V

    #@162
    throw v11

    #@163
    .line 687
    .end local v3    # "op":I
    .end local v6    # "p":I
    .restart local v4    # "op":I
    .restart local v7    # "p":I
    .restart local v8    # "t":I
    :cond_7
    add-int/lit8 v6, v7, 0x1

    #@165
    .end local v7    # "p":I
    .restart local v6    # "p":I
    aget-byte v11, p1, v7

    #@167
    goto :goto_2

    #@168
    .line 699
    .end local v6    # "p":I
    .end local v8    # "t":I
    .restart local v7    # "p":I
    :cond_8
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@16a
    sub-int v11, v7, v11

    #@16c
    add-int/lit8 v12, p3, -0x2

    #@16e
    if-ne v11, v12, :cond_d

    #@170
    .line 700
    const/4 v8, 0x0

    #@171
    .line 701
    .restart local v8    # "t":I
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@173
    const/4 v12, 0x1

    #@174
    if-le v11, v12, :cond_b

    #@176
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@178
    add-int/lit8 v9, v8, 0x1

    #@17a
    .end local v8    # "t":I
    .restart local v9    # "t":I
    aget-byte v11, v11, v8

    #@17c
    move v8, v9

    #@17d
    .end local v9    # "t":I
    .restart local v8    # "t":I
    move v6, v7

    #@17e
    .end local v7    # "p":I
    .restart local v6    # "p":I
    :goto_5
    and-int/lit16 v11, v11, 0xff

    #@180
    shl-int/lit8 v12, v11, 0xa

    #@182
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@184
    if-lez v11, :cond_c

    #@186
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@188
    add-int/lit8 v9, v8, 0x1

    #@18a
    .end local v8    # "t":I
    .restart local v9    # "t":I
    aget-byte v11, v11, v8

    #@18c
    move v8, v9

    #@18d
    .end local v9    # "t":I
    .restart local v8    # "t":I
    :goto_6
    and-int/lit16 v11, v11, 0xff

    #@18f
    shl-int/lit8 v11, v11, 0x2

    #@191
    or-int v10, v12, v11

    #@193
    .line 703
    iget v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@195
    sub-int/2addr v11, v8

    #@196
    iput v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@198
    .line 704
    add-int/lit8 v3, v4, 0x1

    #@19a
    .end local v4    # "op":I
    .restart local v3    # "op":I
    shr-int/lit8 v11, v10, 0xc

    #@19c
    and-int/lit8 v11, v11, 0x3f

    #@19e
    aget-byte v11, v1, v11

    #@1a0
    aput-byte v11, v5, v4

    #@1a2
    .line 705
    add-int/lit8 v4, v3, 0x1

    #@1a4
    .end local v3    # "op":I
    .restart local v4    # "op":I
    shr-int/lit8 v11, v10, 0x6

    #@1a6
    and-int/lit8 v11, v11, 0x3f

    #@1a8
    aget-byte v11, v1, v11

    #@1aa
    aput-byte v11, v5, v3

    #@1ac
    .line 706
    add-int/lit8 v3, v4, 0x1

    #@1ae
    .end local v4    # "op":I
    .restart local v3    # "op":I
    and-int/lit8 v11, v10, 0x3f

    #@1b0
    aget-byte v11, v1, v11

    #@1b2
    aput-byte v11, v5, v4

    #@1b4
    .line 707
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_padding:Z

    #@1b6
    if-eqz v11, :cond_9

    #@1b8
    .line 708
    add-int/lit8 v4, v3, 0x1

    #@1ba
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0x3d

    #@1bc
    aput-byte v11, v5, v3

    #@1be
    move v3, v4

    #@1bf
    .line 710
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_9
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@1c1
    if-eqz v11, :cond_6

    #@1c3
    .line 711
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@1c5
    if-eqz v11, :cond_a

    #@1c7
    add-int/lit8 v4, v3, 0x1

    #@1c9
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xd

    #@1cb
    aput-byte v11, v5, v3

    #@1cd
    move v3, v4

    #@1ce
    .line 712
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_a
    add-int/lit8 v4, v3, 0x1

    #@1d0
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xa

    #@1d2
    aput-byte v11, v5, v3

    #@1d4
    goto :goto_3

    #@1d5
    .line 701
    .end local v6    # "p":I
    .restart local v7    # "p":I
    :cond_b
    add-int/lit8 v6, v7, 0x1

    #@1d7
    .end local v7    # "p":I
    .restart local v6    # "p":I
    aget-byte v11, p1, v7

    #@1d9
    goto :goto_5

    #@1da
    :cond_c
    add-int/lit8 v7, v6, 0x1

    #@1dc
    .end local v6    # "p":I
    .restart local v7    # "p":I
    aget-byte v11, p1, v6

    #@1de
    move v6, v7

    #@1df
    .end local v7    # "p":I
    .restart local v6    # "p":I
    goto :goto_6

    #@1e0
    .line 714
    .end local v6    # "p":I
    .end local v8    # "t":I
    .restart local v7    # "p":I
    :cond_d
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@1e2
    if-eqz v11, :cond_e

    #@1e4
    if-lez v4, :cond_e

    #@1e6
    const/16 v11, 0x13

    #@1e8
    if-eq v2, v11, :cond_e

    #@1ea
    .line 715
    iget-boolean v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@1ec
    if-eqz v11, :cond_14

    #@1ee
    add-int/lit8 v3, v4, 0x1

    #@1f0
    .end local v4    # "op":I
    .restart local v3    # "op":I
    const/16 v11, 0xd

    #@1f2
    aput-byte v11, v5, v4

    #@1f4
    .line 716
    :goto_7
    add-int/lit8 v4, v3, 0x1

    #@1f6
    .end local v3    # "op":I
    .restart local v4    # "op":I
    const/16 v11, 0xa

    #@1f8
    aput-byte v11, v5, v3

    #@1fa
    :cond_e
    move v6, v7

    #@1fb
    .end local v7    # "p":I
    .restart local v6    # "p":I
    move v3, v4

    #@1fc
    .end local v4    # "op":I
    .restart local v3    # "op":I
    goto/16 :goto_4

    #@1fe
    .line 720
    :cond_f
    sget-boolean v11, Lorg/cf/obfuscated/Base64$Encoder;->$assertionsDisabled:Z

    #@200
    if-nez v11, :cond_11

    #@202
    move/from16 v0, p3

    #@204
    if-eq v6, v0, :cond_11

    #@206
    new-instance v11, Ljava/lang/AssertionError;

    #@208
    invoke-direct {v11}, Ljava/lang/AssertionError;-><init>()V

    #@20b
    throw v11

    #@20c
    .line 725
    .end local v3    # "op":I
    .end local v6    # "p":I
    .restart local v4    # "op":I
    .restart local v7    # "p":I
    :cond_10
    add-int/lit8 v11, p3, -0x1

    #@20e
    if-ne v7, v11, :cond_12

    #@210
    .line 726
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@212
    iget v12, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@214
    add-int/lit8 v13, v12, 0x1

    #@216
    iput v13, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@218
    aget-byte v13, p1, v7

    #@21a
    aput-byte v13, v11, v12

    #@21c
    move v6, v7

    #@21d
    .end local v7    # "p":I
    .restart local v6    # "p":I
    move v3, v4

    #@21e
    .line 733
    .end local v4    # "op":I
    .restart local v3    # "op":I
    :cond_11
    :goto_8
    iput v3, p0, Lorg/cf/obfuscated/Base64$Encoder;->op:I

    #@220
    .line 734
    iput v2, p0, Lorg/cf/obfuscated/Base64$Encoder;->count:I

    #@222
    .line 736
    const/4 v11, 0x1

    #@223
    return v11

    #@224
    .line 727
    .end local v3    # "op":I
    .end local v6    # "p":I
    .restart local v4    # "op":I
    .restart local v7    # "p":I
    :cond_12
    add-int/lit8 v11, p3, -0x2

    #@226
    if-ne v7, v11, :cond_13

    #@228
    .line 728
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@22a
    iget v12, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@22c
    add-int/lit8 v13, v12, 0x1

    #@22e
    iput v13, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@230
    aget-byte v13, p1, v7

    #@232
    aput-byte v13, v11, v12

    #@234
    .line 729
    iget-object v11, p0, Lorg/cf/obfuscated/Base64$Encoder;->tail:[B

    #@236
    iget v12, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@238
    add-int/lit8 v13, v12, 0x1

    #@23a
    iput v13, p0, Lorg/cf/obfuscated/Base64$Encoder;->tailLen:I

    #@23c
    add-int/lit8 v13, v7, 0x1

    #@23e
    aget-byte v13, p1, v13

    #@240
    aput-byte v13, v11, v12

    #@242
    :cond_13
    move v6, v7

    #@243
    .end local v7    # "p":I
    .restart local v6    # "p":I
    move v3, v4

    #@244
    .end local v4    # "op":I
    .restart local v3    # "op":I
    goto :goto_8

    #@245
    .end local v3    # "op":I
    .end local v6    # "p":I
    .restart local v4    # "op":I
    .restart local v7    # "p":I
    :cond_14
    move v3, v4

    #@246
    .end local v4    # "op":I
    .restart local v3    # "op":I
    goto :goto_7

    #@247
    .end local v7    # "p":I
    .restart local v6    # "p":I
    :cond_15
    move v7, v6

    #@248
    .end local v6    # "p":I
    .restart local v7    # "p":I
    move v4, v3

    #@249
    .end local v3    # "op":I
    .restart local v4    # "op":I
    goto/16 :goto_1

    #@24b
    .line 618
    nop

    #@24c
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method
