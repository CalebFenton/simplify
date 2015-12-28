.class public Lorg/cf/obfuscated/Base64;
.super Ljava/lang/Object;
.source "Base64.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lorg/cf/obfuscated/Base64$Encoder;,
        Lorg/cf/obfuscated/Base64$Decoder;,
        Lorg/cf/obfuscated/Base64$Coder;
    }
.end annotation


# static fields
.field static final synthetic $assertionsDisabled:Z

.field public static final CRLF:I = 0x4

.field public static final DEFAULT:I = 0x0

.field public static final NO_CLOSE:I = 0x10

.field public static final NO_PADDING:I = 0x1

.field public static final NO_WRAP:I = 0x2

.field public static final URL_SAFE:I = 0x8


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 27
    const-class v0, Lorg/cf/obfuscated/Base64;

    #@2
    invoke-virtual {v0}, Ljava/lang/Class;->desiredAssertionStatus()Z

    #@5
    move-result v0

    #@6
    if-nez v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    sput-boolean v0, Lorg/cf/obfuscated/Base64;->$assertionsDisabled:Z

    #@b
    return-void

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 740
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static decode(Ljava/lang/String;I)[B
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 118
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1}, Lorg/cf/obfuscated/Base64;->decode([BI)[B

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static decode([BI)[B
    .locals 2
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 136
    const/4 v0, 0x0

    #@1
    array-length v1, p0

    #@2
    invoke-static {p0, v0, v1, p1}, Lorg/cf/obfuscated/Base64;->decode([BIII)[B

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static decode([BIII)[B
    .locals 5
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 158
    new-instance v0, Lorg/cf/obfuscated/Base64$Decoder;

    #@3
    mul-int/lit8 v2, p2, 0x3

    #@5
    div-int/lit8 v2, v2, 0x4

    #@7
    new-array v2, v2, [B

    #@9
    invoke-direct {v0, p3, v2}, Lorg/cf/obfuscated/Base64$Decoder;-><init>(I[B)V

    #@c
    .line 160
    .local v0, "decoder":Lorg/cf/obfuscated/Base64$Decoder;
    const/4 v2, 0x1

    #@d
    invoke-virtual {v0, p0, p1, p2, v2}, Lorg/cf/obfuscated/Base64$Decoder;->process([BIIZ)Z

    #@10
    move-result v2

    #@11
    if-nez v2, :cond_0

    #@13
    .line 161
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@15
    const-string v3, "bad base-64"

    #@17
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@1a
    throw v2

    #@1b
    .line 165
    :cond_0
    iget v2, v0, Lorg/cf/obfuscated/Base64$Decoder;->op:I

    #@1d
    iget-object v3, v0, Lorg/cf/obfuscated/Base64$Decoder;->output:[B

    #@1f
    array-length v3, v3

    #@20
    if-ne v2, v3, :cond_1

    #@22
    .line 166
    iget-object v1, v0, Lorg/cf/obfuscated/Base64$Decoder;->output:[B

    #@24
    .line 173
    :goto_0
    return-object v1

    #@25
    .line 171
    :cond_1
    iget v2, v0, Lorg/cf/obfuscated/Base64$Decoder;->op:I

    #@27
    new-array v1, v2, [B

    #@29
    .line 172
    .local v1, "temp":[B
    iget-object v2, v0, Lorg/cf/obfuscated/Base64$Decoder;->output:[B

    #@2b
    iget v3, v0, Lorg/cf/obfuscated/Base64$Decoder;->op:I

    #@2d
    invoke-static {v2, v4, v1, v4, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@30
    goto :goto_0
.end method

.method public static encode([BI)[B
    .locals 2
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 494
    const/4 v0, 0x0

    #@1
    array-length v1, p0

    #@2
    invoke-static {p0, v0, v1, p1}, Lorg/cf/obfuscated/Base64;->encode([BIII)[B

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static encode([BIII)[B
    .locals 5
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 510
    new-instance v0, Lorg/cf/obfuscated/Base64$Encoder;

    #@3
    const/4 v2, 0x0

    #@4
    invoke-direct {v0, p3, v2}, Lorg/cf/obfuscated/Base64$Encoder;-><init>(I[B)V

    #@7
    .line 513
    .local v0, "encoder":Lorg/cf/obfuscated/Base64$Encoder;
    div-int/lit8 v2, p2, 0x3

    #@9
    mul-int/lit8 v1, v2, 0x4

    #@b
    .line 516
    .local v1, "output_len":I
    iget-boolean v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->do_padding:Z

    #@d
    if-eqz v2, :cond_2

    #@f
    .line 517
    rem-int/lit8 v2, p2, 0x3

    #@11
    if-lez v2, :cond_0

    #@13
    .line 518
    add-int/lit8 v1, v1, 0x4

    #@15
    .line 529
    :cond_0
    :goto_0
    :pswitch_0
    iget-boolean v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->do_newline:Z

    #@17
    if-eqz v2, :cond_1

    #@19
    if-lez p2, :cond_1

    #@1b
    .line 530
    add-int/lit8 v2, p2, -0x1

    #@1d
    div-int/lit8 v2, v2, 0x39

    #@1f
    add-int/lit8 v4, v2, 0x1

    #@21
    iget-boolean v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->do_cr:Z

    #@23
    if-eqz v2, :cond_3

    #@25
    const/4 v2, 0x2

    #@26
    :goto_1
    mul-int/2addr v2, v4

    #@27
    add-int/2addr v1, v2

    #@28
    .line 534
    :cond_1
    new-array v2, v1, [B

    #@2a
    iput-object v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->output:[B

    #@2c
    .line 535
    invoke-virtual {v0, p0, p1, p2, v3}, Lorg/cf/obfuscated/Base64$Encoder;->process([BIIZ)Z

    #@2f
    .line 537
    sget-boolean v2, Lorg/cf/obfuscated/Base64;->$assertionsDisabled:Z

    #@31
    if-nez v2, :cond_4

    #@33
    iget v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->op:I

    #@35
    if-eq v2, v1, :cond_4

    #@37
    new-instance v2, Ljava/lang/AssertionError;

    #@39
    invoke-direct {v2}, Ljava/lang/AssertionError;-><init>()V

    #@3c
    throw v2

    #@3d
    .line 521
    :cond_2
    rem-int/lit8 v2, p2, 0x3

    #@3f
    packed-switch v2, :pswitch_data_0

    #@42
    goto :goto_0

    #@43
    .line 523
    :pswitch_1
    add-int/lit8 v1, v1, 0x2

    #@45
    goto :goto_0

    #@46
    .line 524
    :pswitch_2
    add-int/lit8 v1, v1, 0x3

    #@48
    goto :goto_0

    #@49
    :cond_3
    move v2, v3

    #@4a
    .line 530
    goto :goto_1

    #@4b
    .line 539
    :cond_4
    iget-object v2, v0, Lorg/cf/obfuscated/Base64$Encoder;->output:[B

    #@4d
    return-object v2

    #@4e
    .line 521
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static encodeToString([BI)Ljava/lang/String;
    .locals 4
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 456
    :try_start_0
    new-instance v1, Ljava/lang/String;

    #@2
    invoke-static {p0, p1}, Lorg/cf/obfuscated/Base64;->encode([BI)[B

    #@5
    move-result-object v2

    #@6
    const-string v3, "US-ASCII"

    #@8
    invoke-direct {v1, v2, v3}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_0
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_0 .. :try_end_0} :catch_0

    #@b
    return-object v1

    #@c
    .line 457
    :catch_0
    move-exception v0

    #@d
    .line 459
    .local v0, "e":Ljava/io/UnsupportedEncodingException;
    new-instance v1, Ljava/lang/AssertionError;

    #@f
    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    #@12
    throw v1
.end method

.method public static encodeToString([BIII)Ljava/lang/String;
    .locals 4
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    #@0
    .prologue
    .line 477
    :try_start_0
    new-instance v1, Ljava/lang/String;

    #@2
    invoke-static {p0, p1, p2, p3}, Lorg/cf/obfuscated/Base64;->encode([BIII)[B

    #@5
    move-result-object v2

    #@6
    const-string v3, "US-ASCII"

    #@8
    invoke-direct {v1, v2, v3}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_0
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_0 .. :try_end_0} :catch_0

    #@b
    return-object v1

    #@c
    .line 478
    :catch_0
    move-exception v0

    #@d
    .line 480
    .local v0, "e":Ljava/io/UnsupportedEncodingException;
    new-instance v1, Ljava/lang/AssertionError;

    #@f
    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    #@12
    throw v1
.end method
