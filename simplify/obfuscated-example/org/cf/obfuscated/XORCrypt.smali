.class public Lorg/cf/obfuscated/XORCrypt;
.super Ljava/lang/Object;
.source "XORCrypt.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .registers 9
    .param p0, "data"    # Ljava/lang/String;
    .param p1, "key"    # Ljava/lang/String;

    .prologue
    .line 20
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    .line 21
    .local v0, "dataBytes":[B
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v2

    .line 23
    .local v2, "keyBytes":[B
    const/4 v3, 0x0

    .line 24
    .local v3, "keyPointer":I
    const/4 v4, 0x0

    .line 25
    .local v4, "keyPointerAdd":B
    const/4 v1, 0x0

    .local v1, "i":I
    :goto_b
    array-length v5, v0

    if-ge v1, v5, :cond_1e

    .line 26
    aget-byte v4, v0, v1

    .line 27
    aget-byte v5, v0, v1

    aget-byte v6, v2, v3

    xor-int/2addr v5, v6

    int-to-byte v5, v5

    aput-byte v5, v0, v1

    .line 28
    add-int/2addr v3, v4

    .line 29
    array-length v5, v2

    rem-int/2addr v3, v5

    .line 25
    add-int/lit8 v1, v1, 0x1

    goto :goto_b

    .line 32
    :cond_1e
    new-instance v5, Ljava/lang/String;

    invoke-direct {v5, v0}, Ljava/lang/String;-><init>([B)V

    return-object v5
.end method

.method public static encode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .registers 8
    .param p0, "data"    # Ljava/lang/String;
    .param p1, "key"    # Ljava/lang/String;

    .prologue
    .line 6
    invoke-virtual {p0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    .line 7
    .local v0, "dataBytes":[B
    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v2

    .line 9
    .local v2, "keyBytes":[B
    const/4 v3, 0x0

    .line 10
    .local v3, "keyPointer":I
    const/4 v1, 0x0

    .local v1, "i":I
    :goto_a
    array-length v4, v0

    if-ge v1, v4, :cond_1d

    .line 11
    aget-byte v4, v0, v1

    aget-byte v5, v2, v3

    xor-int/2addr v4, v5

    int-to-byte v4, v4

    aput-byte v4, v0, v1

    .line 12
    aget-byte v4, v0, v1

    add-int/2addr v3, v4

    .line 13
    array-length v4, v2

    rem-int/2addr v3, v4

    .line 10
    add-int/lit8 v1, v1, 0x1

    goto :goto_a

    .line 16
    :cond_1d
    new-instance v4, Ljava/lang/String;

    invoke-direct {v4, v0}, Ljava/lang/String;-><init>([B)V

    return-object v4
.end method

.method public static main([Ljava/lang/String;)V
    .registers 1
    .param p0, "argv"    # [Ljava/lang/String;

    .prologue
    .line 42
    return-void
.end method
