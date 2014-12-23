.class public Lorg/cf/obfuscated/StringHolder;
.super Ljava/lang/Object;
.source "StringHolder.java"


# static fields
.field private static final base64edStrings:[Ljava/lang/String;

.field public static final desKey:Ljava/lang/String;

.field private static desedStrings:[[B

.field private static final strings:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/Integer;",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field public static final xorKey:Ljava/lang/String;

.field private static xoredStrings:[[B


# direct methods
.method static constructor <clinit>()V
    .registers 6

    .prologue
    const/4 v5, 0x2

    const/4 v4, 0x1

    const/4 v3, 0x0

    .line 11
    new-instance v0, Ljava/lang/String;

    const/16 v1, 0xa

    new-array v1, v1, [C

    fill-array-data v1, :array_78

    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([C)V

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->xorKey:Ljava/lang/String;

    .line 12
    new-instance v0, Ljava/lang/String;

    const/16 v1, 0x9

    new-array v1, v1, [C

    fill-array-data v1, :array_86

    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([C)V

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->desKey:Ljava/lang/String;

    .line 14
    const/4 v0, 0x7

    new-array v0, v0, [Ljava/lang/String;

    const-string v1, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="

    aput-object v1, v0, v3

    const-string v1, "V2hhdCBkbyB5b3UgY2FsbCB0aGUgbW91c2Ugc2hhZG93IG9uIHRoZSBzZWNvbmQgbW9vbj8="

    aput-object v1, v0, v4

    const-string v1, "U25vdyBDcmFzaA=="

    aput-object v1, v0, v5

    const/4 v1, 0x3

    const-string v2, "TmV1cm9tYW5jZXI="

    aput-object v2, v0, v1

    const/4 v1, 0x4

    const-string v2, "VGhlIEhpdGNoaGlrZXIncyBHdWlkZSB0byB0aGUgR2FsYXh5"

    aput-object v2, v0, v1

    const/4 v1, 0x5

    const-string v2, "U3RyYW5nZXIgaW4gYSBTdHJhbmdlIExhbmQ="

    aput-object v2, v0, v1

    const/4 v1, 0x6

    const-string v2, "RHVuZSwgbG9s"

    aput-object v2, v0, v1

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    .line 27
    new-array v0, v5, [[B

    const/16 v1, 0xc

    new-array v1, v1, [B

    fill-array-data v1, :array_94

    aput-object v1, v0, v3

    const/16 v1, 0x11

    new-array v1, v1, [B

    fill-array-data v1, :array_9e

    aput-object v1, v0, v4

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    .line 34
    new-array v0, v5, [[B

    const/16 v1, 0x20

    new-array v1, v1, [B

    fill-array-data v1, :array_ac

    aput-object v1, v0, v3

    const/16 v1, 0x10

    new-array v1, v1, [B

    fill-array-data v1, :array_c0

    aput-object v1, v0, v4

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    .line 41
    invoke-static {}, Lorg/cf/obfuscated/StringHolder;->cacheStrings()Ljava/util/Map;

    move-result-object v0

    sput-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    return-void

    .line 11
    nop

    :array_78
    .array-data 2
        0x73s
        0x65s
        0x78s
        0x79s
        0x20s
        0x74s
        0x69s
        0x6ds
        0x65s
        0x21s
    .end array-data

    .line 12
    :array_86
    .array-data 2
        0x6ds
        0x61s
        0x6cs
        0x6as
        0x75s
        0x61s
        0x72s
        0x65s
        0x7as
    .end array-data

    .line 27
    nop

    :array_94
    .array-data 1
        0x0t
        0x16t
        0x1bt
        0x53t
        0x1dt
        0x11t
        0x28t
        0x0t
        0x11t
        0x1ct
        0x16t
        0x10t
    .end array-data

    :array_9e
    .array-data 1
        0x19t
        0x15t
        0x1ft
        0xct
        0xft
        0x4ct
        0x12t
        0xbt
        0x46t
        0xft
        0x69t
        0x4ft
        0x11t
        0x11t
        0x1ft
        0x1ct
        0x17t
    .end array-data

    .line 34
    nop

    :array_ac
    .array-data 1
        -0x14t
        0x77t
        0x73t
        0x2t
        -0x32t
        -0x18t
        0x6at
        0x21t
        0x3dt
        -0x53t
        0x58t
        0x4bt
        -0x4ct
        0x0t
        0x68t
        0x1dt
        0x4et
        -0x79t
        -0x6et
        0x79t
        0x6ft
        0xct
        -0x4et
        0x27t
        -0x74t
        -0x6bt
        0x27t
        -0x3ft
        0x31t
        -0xbt
        -0x12t
        0x7ft
    .end array-data

    :array_c0
    .array-data 1
        -0x76t
        0x3et
        -0x54t
        0x12t
        0x71t
        -0x44t
        -0x75t
        0x78t
        0x33t
        -0x14t
        -0x3t
        0x11t
        -0x18t
        0x2ct
        -0x50t
        -0x24t
    .end array-data
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static cacheStrings()Ljava/util/Map;
    .registers 15
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/Integer;",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation

    .prologue
    .line 46
    new-instance v1, Ljava/util/HashMap;

    sget-object v13, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    array-length v13, v13

    sget-object v14, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    array-length v14, v14

    add-int/2addr v13, v14

    sget-object v14, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    array-length v14, v14

    add-int/2addr v13, v14

    invoke-direct {v1, v13}, Ljava/util/HashMap;-><init>(I)V

    .line 50
    .local v1, "cache":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/String;>;"
    const/4 v10, 0x0

    .line 51
    .local v10, "pos":I
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    .local v0, "arr$":[Ljava/lang/String;
    array-length v9, v0

    .local v9, "len$":I
    const/4 v8, 0x0

    .local v8, "i$":I
    move v11, v10

    .end local v10    # "pos":I
    .local v11, "pos":I
    :goto_16
    if-ge v8, v9, :cond_3a

    aget-object v12, v0, v8

    .line 52
    .local v12, "string":Ljava/lang/String;
    const/4 v13, 0x0

    invoke-static {v12, v13}, Landroid/util/Base64;->decode(Ljava/lang/String;I)[B

    move-result-object v2

    .line 53
    .local v2, "data":[B
    const/4 v3, 0x0

    .line 55
    .local v3, "decoded":Ljava/lang/String;
    :try_start_20
    new-instance v4, Ljava/lang/String;

    const-string v13, "UTF-8"

    invoke-direct {v4, v2, v13}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_27
    .catch Ljava/lang/Exception; {:try_start_20 .. :try_end_27} :catch_35

    .end local v3    # "decoded":Ljava/lang/String;
    .local v4, "decoded":Ljava/lang/String;
    move-object v3, v4

    .line 59
    .end local v4    # "decoded":Ljava/lang/String;
    .restart local v3    # "decoded":Ljava/lang/String;
    :goto_28
    add-int/lit8 v10, v11, 0x1

    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v13

    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 51
    add-int/lit8 v8, v8, 0x1

    move v11, v10

    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_16

    .line 56
    :catch_35
    move-exception v5

    .line 57
    .local v5, "e":Ljava/lang/Exception;
    invoke-virtual {v5}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_28

    .line 62
    .end local v2    # "data":[B
    .end local v3    # "decoded":Ljava/lang/String;
    .end local v5    # "e":Ljava/lang/Exception;
    .end local v12    # "string":Ljava/lang/String;
    :cond_3a
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    .local v0, "arr$":[[B
    array-length v9, v0

    const/4 v8, 0x0

    :goto_3e
    if-ge v8, v9, :cond_62

    aget-object v6, v0, v8

    .line 63
    .local v6, "encoded":[B
    const/4 v3, 0x0

    .line 65
    .restart local v3    # "decoded":Ljava/lang/String;
    :try_start_43
    new-instance v13, Ljava/lang/String;

    const-string v14, "UTF-8"

    invoke-direct {v13, v6, v14}, Ljava/lang/String;-><init>([BLjava/lang/String;)V

    sget-object v14, Lorg/cf/obfuscated/StringHolder;->xorKey:Ljava/lang/String;

    invoke-static {v13, v14}, Lorg/cf/obfuscated/XORCrypt;->decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    :try_end_4f
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_43 .. :try_end_4f} :catch_5d

    move-result-object v3

    .line 69
    :goto_50
    add-int/lit8 v10, v11, 0x1

    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v13

    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 62
    add-int/lit8 v8, v8, 0x1

    move v11, v10

    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_3e

    .line 66
    :catch_5d
    move-exception v5

    .line 67
    .local v5, "e":Ljava/io/UnsupportedEncodingException;
    invoke-virtual {v5}, Ljava/io/UnsupportedEncodingException;->printStackTrace()V

    goto :goto_50

    .line 73
    .end local v3    # "decoded":Ljava/lang/String;
    .end local v5    # "e":Ljava/io/UnsupportedEncodingException;
    .end local v6    # "encoded":[B
    :cond_62
    :try_start_62
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    array-length v9, v0

    const/4 v8, 0x0

    :goto_66
    if-ge v8, v9, :cond_7d

    aget-object v7, v0, v8

    .line 74
    .local v7, "encrypted":[B
    sget-object v13, Lorg/cf/obfuscated/StringHolder;->desKey:Ljava/lang/String;

    invoke-static {v7, v13}, Lorg/cf/obfuscated/DESCrypt;->decrypt([BLjava/lang/String;)Ljava/lang/String;
    :try_end_6f
    .catch Ljava/lang/Exception; {:try_start_62 .. :try_end_6f} :catch_7f

    move-result-object v3

    .line 75
    .restart local v3    # "decoded":Ljava/lang/String;
    add-int/lit8 v10, v11, 0x1

    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    :try_start_72
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v13

    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_79
    .catch Ljava/lang/Exception; {:try_start_72 .. :try_end_79} :catch_85

    .line 73
    add-int/lit8 v8, v8, 0x1

    move v11, v10

    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_66

    .end local v3    # "decoded":Ljava/lang/String;
    .end local v7    # "encrypted":[B
    :cond_7d
    move v10, v11

    .line 85
    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    :goto_7e
    return-object v1

    .line 77
    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    :catch_7f
    move-exception v5

    move v10, v11

    .line 78
    .end local v11    # "pos":I
    .local v5, "e":Ljava/lang/Exception;
    .restart local v10    # "pos":I
    :goto_81
    invoke-virtual {v5}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_7e

    .line 77
    .end local v5    # "e":Ljava/lang/Exception;
    .restart local v3    # "decoded":Ljava/lang/String;
    .restart local v7    # "encrypted":[B
    :catch_85
    move-exception v5

    goto :goto_81
.end method

.method public static get(I)Ljava/lang/String;
    .registers 3
    .param p0, "index"    # I

    .prologue
    .line 89
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    return-object v0
.end method

.method public static set(ILjava/lang/String;)V
    .registers 4
    .param p0, "index"    # I
    .param p1, "newValue"    # Ljava/lang/String;

    .prologue
    .line 93
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1, p1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 94
    return-void
.end method
