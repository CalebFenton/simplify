.class public Lorg/cf/obfuscated/XORCrypt;
.super Ljava/lang/Object;
.source "XORCrypt.java"


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

.method public static decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .locals 7
    .param p0, "data"    # Ljava/lang/String;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 20
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    #@3
    move-result-object v0

    #@4
    .line 21
    .local v0, "dataBytes":[B
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    #@7
    move-result-object v2

    #@8
    .line 23
    .local v2, "keyBytes":[B
    const/4 v3, 0x0

    #@9
    .line 24
    .local v3, "keyPointer":I
    const/4 v4, 0x0

    #@a
    .line 25
    .local v4, "keyPointerAdd":B
    const/4 v1, 0x0

    #@b
    .local v1, "i":I
    :goto_0
    array-length v5, v0

    #@c
    if-ge v1, v5, :cond_0

    #@e
    .line 26
    aget-byte v4, v0, v1

    #@10
    .line 27
    aget-byte v5, v0, v1

    #@12
    aget-byte v6, v2, v3

    #@14
    xor-int/2addr v5, v6

    #@15
    int-to-byte v5, v5

    #@16
    aput-byte v5, v0, v1

    #@18
    .line 28
    add-int/2addr v3, v4

    #@19
    .line 29
    array-length v5, v2

    #@1a
    rem-int/2addr v3, v5

    #@1b
    .line 25
    add-int/lit8 v1, v1, 0x1

    #@1d
    goto :goto_0

    #@1e
    .line 32
    :cond_0
    new-instance v5, Ljava/lang/String;

    #@20
    invoke-direct {v5, v0}, Ljava/lang/String;-><init>([B)V

    #@23
    return-object v5
.end method

.method public static encode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .locals 6
    .param p0, "data"    # Ljava/lang/String;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 6
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    #@3
    move-result-object v0

    #@4
    .line 7
    .local v0, "dataBytes":[B
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    #@7
    move-result-object v2

    #@8
    .line 9
    .local v2, "keyBytes":[B
    const/4 v3, 0x0

    #@9
    .line 10
    .local v3, "keyPointer":I
    const/4 v1, 0x0

    #@a
    .local v1, "i":I
    :goto_0
    array-length v4, v0

    #@b
    if-ge v1, v4, :cond_0

    #@d
    .line 11
    aget-byte v4, v0, v1

    #@f
    aget-byte v5, v2, v3

    #@11
    xor-int/2addr v4, v5

    #@12
    int-to-byte v4, v4

    #@13
    aput-byte v4, v0, v1

    #@15
    .line 12
    aget-byte v4, v0, v1

    #@17
    add-int/2addr v3, v4

    #@18
    .line 13
    array-length v4, v2

    #@19
    rem-int/2addr v3, v4

    #@1a
    .line 10
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 16
    :cond_0
    new-instance v4, Ljava/lang/String;

    #@1f
    invoke-direct {v4, v0}, Ljava/lang/String;-><init>([B)V

    #@22
    return-object v4
.end method

.method public static main([Ljava/lang/String;)V
    .locals 0
    .param p0, "argv"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 42
    return-void
.end method
