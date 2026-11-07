.class public Lorg/cf/obfuscated/MathCrypt;
.super Ljava/lang/Object;
.source "MathCrypt.java"


# static fields
.field private static primes:[I

.field private static sieveCount:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 9
    const/16 v0, 0x3e8

    #@2
    sput v0, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    #@4
    .line 10
    invoke-static {}, Lorg/cf/obfuscated/MathCrypt;->getPrimes()[I

    #@7
    move-result-object v0

    #@8
    sput-object v0, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@a
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static decode(III)I
    .locals 2
    .param p0, "x"    # I
    .param p1, "y"    # I
    .param p2, "z"    # I

    #@0
    .prologue
    .line 46
    sget-object v1, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@2
    aget v0, v1, p0

    #@4
    .line 47
    .local v0, "result":I
    sget-object v1, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@6
    aget v1, v1, p1

    #@8
    mul-int/2addr v0, v1

    #@9
    .line 48
    shl-int/lit8 v0, v0, 0x2

    #@b
    .line 49
    sub-int/2addr v0, p2

    #@c
    .line 51
    return v0
.end method

.method public static decode([I)I
    .locals 3
    .param p0, "xyz"    # [I

    #@0
    .prologue
    .line 42
    const/4 v0, 0x0

    #@1
    aget v0, p0, v0

    #@3
    const/4 v1, 0x1

    #@4
    aget v1, p0, v1

    #@6
    const/4 v2, 0x2

    #@7
    aget v2, p0, v2

    #@9
    invoke-static {v0, v1, v2}, Lorg/cf/obfuscated/MathCrypt;->decode(III)I

    #@c
    move-result v0

    #@d
    return v0
.end method

.method public static encode(I)[I
    .locals 6
    .param p0, "num"    # I

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 56
    const/4 v3, 0x3

    #@3
    new-array v0, v3, [I

    #@5
    .line 58
    .local v0, "encoded":[I
    new-instance v1, Ljava/util/Random;

    #@7
    invoke-direct {v1}, Ljava/util/Random;-><init>()V

    #@a
    .line 59
    .local v1, "rnd":Ljava/util/Random;
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@c
    array-length v3, v3

    #@d
    invoke-virtual {v1, v3}, Ljava/util/Random;->nextInt(I)I

    #@10
    move-result v3

    #@11
    aput v3, v0, v4

    #@13
    .line 60
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@15
    array-length v3, v3

    #@16
    invoke-virtual {v1, v3}, Ljava/util/Random;->nextInt(I)I

    #@19
    move-result v3

    #@1a
    aput v3, v0, v5

    #@1c
    .line 62
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@1e
    aget v4, v0, v4

    #@20
    aget v3, v3, v4

    #@22
    sget-object v4, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    #@24
    aget v5, v0, v5

    #@26
    aget v4, v4, v5

    #@28
    mul-int v2, v3, v4

    #@2a
    .line 63
    .local v2, "wankershim":I
    shl-int/lit8 v2, v2, 0x2

    #@2c
    .line 64
    const/4 v3, 0x2

    #@2d
    sub-int v4, v2, p0

    #@2f
    aput v4, v0, v3

    #@31
    .line 66
    return-object v0
.end method

.method private static getPrimes()[I
    .locals 12

    #@0
    .prologue
    .line 14
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    #@2
    int-to-double v10, v10

    #@3
    invoke-static {v10, v11}, Ljava/lang/Math;->sqrt(D)D

    #@6
    move-result-wide v10

    #@7
    double-to-int v9, v10

    #@8
    .line 15
    .local v9, "stop":I
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    #@a
    add-int/lit8 v10, v10, 0x1

    #@c
    new-array v3, v10, [Z

    #@e
    .line 16
    .local v3, "isComposite":[Z
    const/4 v5, 0x2

    #@f
    .local v5, "m":I
    :goto_0
    if-gt v5, v9, :cond_1

    #@11
    .line 17
    aget-boolean v10, v3, v5

    #@13
    if-nez v10, :cond_0

    #@15
    .line 19
    mul-int v4, v5, v5

    #@17
    .local v4, "k":I
    :goto_1
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    #@19
    if-gt v4, v10, :cond_0

    #@1b
    .line 20
    const/4 v10, 0x1

    #@1c
    aput-boolean v10, v3, v4

    #@1e
    .line 19
    add-int/2addr v4, v5

    #@1f
    goto :goto_1

    #@20
    .line 16
    .end local v4    # "k":I
    :cond_0
    add-int/lit8 v5, v5, 0x1

    #@22
    goto :goto_0

    #@23
    .line 25
    :cond_1
    new-instance v7, Ljava/util/LinkedList;

    #@25
    invoke-direct {v7}, Ljava/util/LinkedList;-><init>()V

    #@28
    .line 26
    .local v7, "primeList":Ljava/util/List;, "Ljava/util/List<Ljava/lang/Integer;>;"
    const/4 v5, 0x0

    #@29
    :goto_2
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    #@2b
    if-gt v5, v10, :cond_3

    #@2d
    .line 27
    aget-boolean v10, v3, v5

    #@2f
    if-nez v10, :cond_2

    #@31
    .line 28
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@34
    move-result-object v10

    #@35
    invoke-interface {v7, v10}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@38
    .line 26
    :cond_2
    add-int/lit8 v5, v5, 0x1

    #@3a
    goto :goto_2

    #@3b
    .line 32
    :cond_3
    invoke-interface {v7}, Ljava/util/List;->size()I

    #@3e
    move-result v10

    #@3f
    new-array v8, v10, [I

    #@41
    .line 33
    .local v8, "primes":[I
    const/4 v1, 0x0

    #@42
    .line 34
    .local v1, "index":I
    invoke-interface {v7}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    #@45
    move-result-object v0

    #@46
    .local v0, "i$":Ljava/util/Iterator;
    :goto_3
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@49
    move-result v10

    #@4a
    if-eqz v10, :cond_4

    #@4c
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@4f
    move-result-object v6

    #@50
    check-cast v6, Ljava/lang/Integer;

    #@52
    .line 35
    .local v6, "prime":Ljava/lang/Integer;
    add-int/lit8 v2, v1, 0x1

    #@54
    .end local v1    # "index":I
    .local v2, "index":I
    invoke-virtual {v6}, Ljava/lang/Integer;->intValue()I

    #@57
    move-result v10

    #@58
    aput v10, v8, v1

    #@5a
    move v1, v2

    #@5b
    .line 36
    .end local v2    # "index":I
    .restart local v1    # "index":I
    goto :goto_3

    #@5c
    .line 38
    .end local v6    # "prime":Ljava/lang/Integer;
    :cond_4
    return-object v8
.end method

.method public static main([Ljava/lang/String;)V
    .locals 0
    .param p0, "argv"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 73
    return-void
.end method
