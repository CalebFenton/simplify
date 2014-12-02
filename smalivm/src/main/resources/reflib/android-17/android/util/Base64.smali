.class public Landroid/util/Base64;
.super Ljava/lang/Object;
.source "Base64.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/util/Base64$Encoder;,
        Landroid/util/Base64$Decoder;,
        Landroid/util/Base64$Coder;
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
    .registers 1

    .prologue
    .line 27
    const-class v0, Landroid/util/Base64;

    invoke-virtual {v0}, Ljava/lang/Class;->desiredAssertionStatus()Z

    move-result v0

    if-nez v0, :cond_c

    const/4 v0, 0x1

    :goto_9
    sput-boolean v0, Landroid/util/Base64;->$assertionsDisabled:Z

    return-void

    :cond_c
    const/4 v0, 0x0

    goto :goto_9
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 740
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decode(Ljava/lang/String;I)[B
    .registers 3
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "flags"    # I

    .prologue
    .line 118
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    invoke-static {v0, p1}, Landroid/util/Base64;->decode([BI)[B

    move-result-object v0

    return-object v0
.end method

.method public static decode([BI)[B
    .registers 4
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    .prologue
    .line 136
    const/4 v0, 0x0

    array-length v1, p0

    invoke-static {p0, v0, v1, p1}, Landroid/util/Base64;->decode([BIII)[B

    move-result-object v0

    return-object v0
.end method

.method public static decode([BIII)[B
    .registers 9
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    .prologue
    const/4 v4, 0x0

    .line 158
    new-instance v0, Landroid/util/Base64$Decoder;

    mul-int/lit8 v2, p2, 0x3

    div-int/lit8 v2, v2, 0x4

    new-array v2, v2, [B

    invoke-direct {v0, p3, v2}, Landroid/util/Base64$Decoder;-><init>(I[B)V

    .line 160
    .local v0, "decoder":Landroid/util/Base64$Decoder;
    const/4 v2, 0x1

    invoke-virtual {v0, p0, p1, p2, v2}, Landroid/util/Base64$Decoder;->process([BIIZ)Z

    move-result v2

    if-nez v2, :cond_1b

    .line 161
    new-instance v2, Ljava/lang/IllegalArgumentException;

    const-string v3, "bad base-64"

    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 165
    :cond_1b
    iget v2, v0, Landroid/util/Base64$Decoder;->op:I

    iget-object v3, v0, Landroid/util/Base64$Decoder;->output:[B

    array-length v3, v3

    if-ne v2, v3, :cond_25

    .line 166
    iget-object v1, v0, Landroid/util/Base64$Decoder;->output:[B

    .line 173
    :goto_24
    return-object v1

    .line 171
    :cond_25
    iget v2, v0, Landroid/util/Base64$Decoder;->op:I

    new-array v1, v2, [B

    .line 172
    .local v1, "temp":[B
    iget-object v2, v0, Landroid/util/Base64$Decoder;->output:[B

    iget v3, v0, Landroid/util/Base64$Decoder;->op:I

    invoke-static {v2, v4, v1, v4, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    goto :goto_24
.end method

.method public static encode([BI)[B
    .registers 4
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    .prologue
    .line 494
    const/4 v0, 0x0

    array-length v1, p0

    invoke-static {p0, v0, v1, p1}, Landroid/util/Base64;->encode([BIII)[B

    move-result-object v0

    return-object v0
.end method

.method public static encode([BIII)[B
    .registers 9
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    .prologue
    const/4 v3, 0x1

    .line 510
    new-instance v0, Landroid/util/Base64$Encoder;

    const/4 v2, 0x0

    invoke-direct {v0, p3, v2}, Landroid/util/Base64$Encoder;-><init>(I[B)V

    .line 513
    .local v0, "encoder":Landroid/util/Base64$Encoder;
    div-int/lit8 v2, p2, 0x3

    mul-int/lit8 v1, v2, 0x4

    .line 516
    .local v1, "output_len":I
    iget-boolean v2, v0, Landroid/util/Base64$Encoder;->do_padding:Z

    if-eqz v2, :cond_3d

    .line 517
    rem-int/lit8 v2, p2, 0x3

    if-lez v2, :cond_15

    .line 518
    add-int/lit8 v1, v1, 0x4

    .line 529
    :cond_15
    :goto_15
    :pswitch_15
    iget-boolean v2, v0, Landroid/util/Base64$Encoder;->do_newline:Z

    if-eqz v2, :cond_28

    if-lez p2, :cond_28

    .line 530
    add-int/lit8 v2, p2, -0x1

    div-int/lit8 v2, v2, 0x39

    add-int/lit8 v4, v2, 0x1

    iget-boolean v2, v0, Landroid/util/Base64$Encoder;->do_cr:Z

    if-eqz v2, :cond_49

    const/4 v2, 0x2

    :goto_26
    mul-int/2addr v2, v4

    add-int/2addr v1, v2

    .line 534
    :cond_28
    new-array v2, v1, [B

    iput-object v2, v0, Landroid/util/Base64$Encoder;->output:[B

    .line 535
    invoke-virtual {v0, p0, p1, p2, v3}, Landroid/util/Base64$Encoder;->process([BIIZ)Z

    .line 537
    sget-boolean v2, Landroid/util/Base64;->$assertionsDisabled:Z

    if-nez v2, :cond_4b

    iget v2, v0, Landroid/util/Base64$Encoder;->op:I

    if-eq v2, v1, :cond_4b

    new-instance v2, Ljava/lang/AssertionError;

    invoke-direct {v2}, Ljava/lang/AssertionError;-><init>()V

    throw v2

    .line 521
    :cond_3d
    rem-int/lit8 v2, p2, 0x3

    packed-switch v2, :pswitch_data_4e

    goto :goto_15

    .line 523
    :pswitch_43
    add-int/lit8 v1, v1, 0x2

    goto :goto_15

    .line 524
    :pswitch_46
    add-int/lit8 v1, v1, 0x3

    goto :goto_15

    :cond_49
    move v2, v3

    .line 530
    goto :goto_26

    .line 539
    :cond_4b
    iget-object v2, v0, Landroid/util/Base64$Encoder;->output:[B

    return-object v2

    .line 521
    :pswitch_data_4e
    .packed-switch 0x0
        :pswitch_15
        :pswitch_43
        :pswitch_46
    .end packed-switch
.end method

.method public static encodeToString([BI)Ljava/lang/String;
    .registers 6
    .param p0, "input"    # [B
    .param p1, "flags"    # I

    .prologue
    .line 456
    :try_start_0
    new-instance v1, Ljava/lang/String;

    invoke-static {p0, p1}, Landroid/util/Base64;->encode([BI)[B

    move-result-object v2

    const-string v3, "US-ASCII"

    invoke-direct {v1, v2, v3}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_b
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_0 .. :try_end_b} :catch_c

    return-object v1

    .line 457
    :catch_c
    move-exception v0

    .line 459
    .local v0, "e":Ljava/io/UnsupportedEncodingException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v1
.end method

.method public static encodeToString([BIII)Ljava/lang/String;
    .registers 8
    .param p0, "input"    # [B
    .param p1, "offset"    # I
    .param p2, "len"    # I
    .param p3, "flags"    # I

    .prologue
    .line 477
    :try_start_0
    new-instance v1, Ljava/lang/String;

    invoke-static {p0, p1, p2, p3}, Landroid/util/Base64;->encode([BIII)[B

    move-result-object v2

    const-string v3, "US-ASCII"

    invoke-direct {v1, v2, v3}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_b
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_0 .. :try_end_b} :catch_c

    return-object v1

    .line 478
    :catch_c
    move-exception v0

    .line 480
    .local v0, "e":Ljava/io/UnsupportedEncodingException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v1
.end method
