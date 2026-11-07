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
    .locals 6

    #@0
    .prologue
    const/4 v5, 0x2

    #@1
    const/4 v4, 0x1

    #@2
    const/4 v3, 0x0

    #@3
    .line 11
    new-instance v0, Ljava/lang/String;

    #@5
    const/16 v1, 0xa

    #@7
    new-array v1, v1, [C

    #@9
    fill-array-data v1, :array_0

    #@c
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([C)V

    #@f
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->xorKey:Ljava/lang/String;

    #@11
    .line 12
    new-instance v0, Ljava/lang/String;

    #@13
    const/16 v1, 0x9

    #@15
    new-array v1, v1, [C

    #@17
    fill-array-data v1, :array_1

    #@1a
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([C)V

    #@1d
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->desKey:Ljava/lang/String;

    #@1f
    .line 14
    const/4 v0, 0x7

    #@20
    new-array v0, v0, [Ljava/lang/String;

    #@22
    const-string v1, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="

    #@24
    aput-object v1, v0, v3

    #@26
    const-string v1, "V2hhdCBkbyB5b3UgY2FsbCB0aGUgbW91c2Ugc2hhZG93IG9uIHRoZSBzZWNvbmQgbW9vbj8="

    #@28
    aput-object v1, v0, v4

    #@2a
    const-string v1, "U25vdyBDcmFzaA=="

    #@2c
    aput-object v1, v0, v5

    #@2e
    const/4 v1, 0x3

    #@2f
    const-string v2, "TmV1cm9tYW5jZXI="

    #@31
    aput-object v2, v0, v1

    #@33
    const/4 v1, 0x4

    #@34
    const-string v2, "VGhlIEhpdGNoaGlrZXIncyBHdWlkZSB0byB0aGUgR2FsYXh5"

    #@36
    aput-object v2, v0, v1

    #@38
    const/4 v1, 0x5

    #@39
    const-string v2, "U3RyYW5nZXIgaW4gYSBTdHJhbmdlIExhbmQ="

    #@3b
    aput-object v2, v0, v1

    #@3d
    const/4 v1, 0x6

    #@3e
    const-string v2, "RHVuZSwgbG9s"

    #@40
    aput-object v2, v0, v1

    #@42
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    #@44
    .line 27
    new-array v0, v5, [[B

    #@46
    const/16 v1, 0xc

    #@48
    new-array v1, v1, [B

    #@4a
    fill-array-data v1, :array_2

    #@4d
    aput-object v1, v0, v3

    #@4f
    const/16 v1, 0x11

    #@51
    new-array v1, v1, [B

    #@53
    fill-array-data v1, :array_3

    #@56
    aput-object v1, v0, v4

    #@58
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    #@5a
    .line 34
    new-array v0, v5, [[B

    #@5c
    const/16 v1, 0x20

    #@5e
    new-array v1, v1, [B

    #@60
    fill-array-data v1, :array_4

    #@63
    aput-object v1, v0, v3

    #@65
    const/16 v1, 0x10

    #@67
    new-array v1, v1, [B

    #@69
    fill-array-data v1, :array_5

    #@6c
    aput-object v1, v0, v4

    #@6e
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    #@70
    .line 41
    invoke-static {}, Lorg/cf/obfuscated/StringHolder;->cacheStrings()Ljava/util/Map;

    #@73
    move-result-object v0

    #@74
    sput-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    #@76
    return-void

    #@77
    .line 11
    nop

    #@78
    :array_0
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

    #@86
    .line 12
    :array_1
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

    #@93
    .line 27
    nop

    #@94
    :array_2
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

    #@9e
    :array_3
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

    #@ab
    .line 34
    nop

    #@ac
    :array_4
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

    #@c0
    :array_5
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
    .locals 0

    #@0
    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method private static cacheStrings()Ljava/util/Map;
    .locals 15
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

    #@0
    .prologue
    .line 46
    new-instance v1, Ljava/util/HashMap;

    #@2
    sget-object v13, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    #@4
    array-length v13, v13

    #@5
    sget-object v14, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    #@7
    array-length v14, v14

    #@8
    add-int/2addr v13, v14

    #@9
    sget-object v14, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    #@b
    array-length v14, v14

    #@c
    add-int/2addr v13, v14

    #@d
    invoke-direct {v1, v13}, Ljava/util/HashMap;-><init>(I)V

    #@10
    .line 50
    .local v1, "cache":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/String;>;"
    const/4 v10, 0x0

    #@11
    .line 51
    .local v10, "pos":I
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->base64edStrings:[Ljava/lang/String;

    #@13
    .local v0, "arr$":[Ljava/lang/String;
    array-length v9, v0

    #@14
    .local v9, "len$":I
    const/4 v8, 0x0

    #@15
    .local v8, "i$":I
    move v11, v10

    #@16
    .end local v10    # "pos":I
    .local v11, "pos":I
    :goto_0
    if-ge v8, v9, :cond_0

    #@18
    aget-object v12, v0, v8

    #@1a
    .line 52
    .local v12, "string":Ljava/lang/String;
    const/4 v13, 0x0

    #@1b
    invoke-static {v12, v13}, Landroid/util/Base64;->decode(Ljava/lang/String;I)[B

    #@1e
    move-result-object v2

    #@1f
    .line 53
    .local v2, "data":[B
    const/4 v3, 0x0

    #@20
    .line 55
    .local v3, "decoded":Ljava/lang/String;
    :try_start_0
    new-instance v4, Ljava/lang/String;

    #@22
    const-string v13, "UTF-8"

    #@24
    invoke-direct {v4, v2, v13}, Ljava/lang/String;-><init>([BLjava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@27
    .end local v3    # "decoded":Ljava/lang/String;
    .local v4, "decoded":Ljava/lang/String;
    move-object v3, v4

    #@28
    .line 59
    .end local v4    # "decoded":Ljava/lang/String;
    .restart local v3    # "decoded":Ljava/lang/String;
    :goto_1
    add-int/lit8 v10, v11, 0x1

    #@2a
    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@2d
    move-result-object v13

    #@2e
    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@31
    .line 51
    add-int/lit8 v8, v8, 0x1

    #@33
    move v11, v10

    #@34
    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_0

    #@35
    .line 56
    :catch_0
    move-exception v5

    #@36
    .line 57
    .local v5, "e":Ljava/lang/Exception;
    invoke-virtual {v5}, Ljava/lang/Exception;->printStackTrace()V

    #@39
    goto :goto_1

    #@3a
    .line 62
    .end local v2    # "data":[B
    .end local v3    # "decoded":Ljava/lang/String;
    .end local v5    # "e":Ljava/lang/Exception;
    .end local v12    # "string":Ljava/lang/String;
    :cond_0
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->xoredStrings:[[B

    #@3c
    .local v0, "arr$":[[B
    array-length v9, v0

    #@3d
    const/4 v8, 0x0

    #@3e
    :goto_2
    if-ge v8, v9, :cond_1

    #@40
    aget-object v6, v0, v8

    #@42
    .line 63
    .local v6, "encoded":[B
    const/4 v3, 0x0

    #@43
    .line 65
    .restart local v3    # "decoded":Ljava/lang/String;
    :try_start_1
    new-instance v13, Ljava/lang/String;

    #@45
    const-string v14, "UTF-8"

    #@47
    invoke-direct {v13, v6, v14}, Ljava/lang/String;-><init>([BLjava/lang/String;)V

    #@4a
    sget-object v14, Lorg/cf/obfuscated/StringHolder;->xorKey:Ljava/lang/String;

    #@4c
    invoke-static {v13, v14}, Lorg/cf/obfuscated/XORCrypt;->decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    :try_end_1
    .catch Ljava/io/UnsupportedEncodingException; {:try_start_1 .. :try_end_1} :catch_1

    #@4f
    move-result-object v3

    #@50
    .line 69
    :goto_3
    add-int/lit8 v10, v11, 0x1

    #@52
    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@55
    move-result-object v13

    #@56
    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@59
    .line 62
    add-int/lit8 v8, v8, 0x1

    #@5b
    move v11, v10

    #@5c
    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_2

    #@5d
    .line 66
    :catch_1
    move-exception v5

    #@5e
    .line 67
    .local v5, "e":Ljava/io/UnsupportedEncodingException;
    invoke-virtual {v5}, Ljava/io/UnsupportedEncodingException;->printStackTrace()V

    #@61
    goto :goto_3

    #@62
    .line 73
    .end local v3    # "decoded":Ljava/lang/String;
    .end local v5    # "e":Ljava/io/UnsupportedEncodingException;
    .end local v6    # "encoded":[B
    :cond_1
    :try_start_2
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->desedStrings:[[B

    #@64
    array-length v9, v0

    #@65
    const/4 v8, 0x0

    #@66
    :goto_4
    if-ge v8, v9, :cond_2

    #@68
    aget-object v7, v0, v8

    #@6a
    .line 74
    .local v7, "encrypted":[B
    sget-object v13, Lorg/cf/obfuscated/StringHolder;->desKey:Ljava/lang/String;

    #@6c
    invoke-static {v7, v13}, Lorg/cf/obfuscated/DESCrypt;->decrypt([BLjava/lang/String;)Ljava/lang/String;
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_2

    #@6f
    move-result-object v3

    #@70
    .line 75
    .restart local v3    # "decoded":Ljava/lang/String;
    add-int/lit8 v10, v11, 0x1

    #@72
    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    :try_start_3
    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@75
    move-result-object v13

    #@76
    invoke-interface {v1, v13, v3}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_3
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_3

    #@79
    .line 73
    add-int/lit8 v8, v8, 0x1

    #@7b
    move v11, v10

    #@7c
    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    goto :goto_4

    #@7d
    .end local v3    # "decoded":Ljava/lang/String;
    .end local v7    # "encrypted":[B
    :cond_2
    move v10, v11

    #@7e
    .line 85
    .end local v11    # "pos":I
    .restart local v10    # "pos":I
    :goto_5
    return-object v1

    #@7f
    .line 77
    .end local v10    # "pos":I
    .restart local v11    # "pos":I
    :catch_2
    move-exception v5

    #@80
    move v10, v11

    #@81
    .line 78
    .end local v11    # "pos":I
    .local v5, "e":Ljava/lang/Exception;
    .restart local v10    # "pos":I
    :goto_6
    invoke-virtual {v5}, Ljava/lang/Exception;->printStackTrace()V

    #@84
    goto :goto_5

    #@85
    .line 77
    .end local v5    # "e":Ljava/lang/Exception;
    .restart local v3    # "decoded":Ljava/lang/String;
    .restart local v7    # "encrypted":[B
    :catch_3
    move-exception v5

    #@86
    goto :goto_6
.end method

.method public static get(I)Ljava/lang/String;
    .locals 2
    .param p0, "index"    # I

    #@0
    .prologue
    .line 89
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    #@2
    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@5
    move-result-object v1

    #@6
    invoke-interface {v0, v1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Ljava/lang/String;

    #@c
    return-object v0
.end method

.method public static set(ILjava/lang/String;)V
    .locals 2
    .param p0, "index"    # I
    .param p1, "newValue"    # Ljava/lang/String;

    #@0
    .prologue
    .line 93
    sget-object v0, Lorg/cf/obfuscated/StringHolder;->strings:Ljava/util/Map;

    #@2
    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@5
    move-result-object v1

    #@6
    invoke-interface {v0, v1, p1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@9
    .line 94
    return-void
.end method
