.class public Lorg/cf/obfuscated/MathCrypt;
.super Ljava/lang/Object;
.source "MathCrypt.java"


# static fields
.field private static primes:[I

.field private static sieveCount:I


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 9
    const/16 v0, 0x3e8

    sput v0, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    .line 10
    invoke-static {}, Lorg/cf/obfuscated/MathCrypt;->getPrimes()[I

    move-result-object v0

    sput-object v0, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decode(III)I
    .registers 5
    .param p0, "x"    # I
    .param p1, "y"    # I
    .param p2, "z"    # I

    .prologue
    .line 46
    sget-object v1, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    aget v0, v1, p0

    .line 47
    .local v0, "result":I
    sget-object v1, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    aget v1, v1, p1

    mul-int/2addr v0, v1

    .line 48
    shl-int/lit8 v0, v0, 0x2

    .line 49
    sub-int/2addr v0, p2

    .line 51
    return v0
.end method

.method public static decode([I)I
    .registers 4
    .param p0, "xyz"    # [I

    .prologue
    .line 42
    const/4 v0, 0x0

    aget v0, p0, v0

    const/4 v1, 0x1

    aget v1, p0, v1

    const/4 v2, 0x2

    aget v2, p0, v2

    invoke-static {v0, v1, v2}, Lorg/cf/obfuscated/MathCrypt;->decode(III)I

    move-result v0

    return v0
.end method

.method public static encode(I)[I
    .registers 7
    .param p0, "num"    # I

    .prologue
    const/4 v5, 0x1

    const/4 v4, 0x0

    .line 56
    const/4 v3, 0x3

    new-array v0, v3, [I

    .line 58
    .local v0, "encoded":[I
    new-instance v1, Ljava/util/Random;

    invoke-direct {v1}, Ljava/util/Random;-><init>()V

    .line 59
    .local v1, "rnd":Ljava/util/Random;
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    array-length v3, v3

    invoke-virtual {v1, v3}, Ljava/util/Random;->nextInt(I)I

    move-result v3

    aput v3, v0, v4

    .line 60
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    array-length v3, v3

    invoke-virtual {v1, v3}, Ljava/util/Random;->nextInt(I)I

    move-result v3

    aput v3, v0, v5

    .line 62
    sget-object v3, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    aget v4, v0, v4

    aget v3, v3, v4

    sget-object v4, Lorg/cf/obfuscated/MathCrypt;->primes:[I

    aget v5, v0, v5

    aget v4, v4, v5

    mul-int v2, v3, v4

    .line 63
    .local v2, "wankershim":I
    shl-int/lit8 v2, v2, 0x2

    .line 64
    const/4 v3, 0x2

    sub-int v4, v2, p0

    aput v4, v0, v3

    .line 66
    return-object v0
.end method

.method private static getPrimes()[I
    .registers 12

    .prologue
    .line 14
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    int-to-double v10, v10

    invoke-static {v10, v11}, Ljava/lang/Math;->sqrt(D)D

    move-result-wide v10

    double-to-int v9, v10

    .line 15
    .local v9, "stop":I
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    add-int/lit8 v10, v10, 0x1

    new-array v3, v10, [Z

    .line 16
    .local v3, "isComposite":[Z
    const/4 v5, 0x2

    .local v5, "m":I
    :goto_f
    if-gt v5, v9, :cond_23

    .line 17
    aget-boolean v10, v3, v5

    if-nez v10, :cond_20

    .line 19
    mul-int v4, v5, v5

    .local v4, "k":I
    :goto_17
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    if-gt v4, v10, :cond_20

    .line 20
    const/4 v10, 0x1

    aput-boolean v10, v3, v4

    .line 19
    add-int/2addr v4, v5

    goto :goto_17

    .line 16
    .end local v4    # "k":I
    :cond_20
    add-int/lit8 v5, v5, 0x1

    goto :goto_f

    .line 25
    :cond_23
    new-instance v7, Ljava/util/LinkedList;

    invoke-direct {v7}, Ljava/util/LinkedList;-><init>()V

    .line 26
    .local v7, "primeList":Ljava/util/List;, "Ljava/util/List<Ljava/lang/Integer;>;"
    const/4 v5, 0x0

    :goto_29
    sget v10, Lorg/cf/obfuscated/MathCrypt;->sieveCount:I

    if-gt v5, v10, :cond_3b

    .line 27
    aget-boolean v10, v3, v5

    if-nez v10, :cond_38

    .line 28
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    invoke-interface {v7, v10}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 26
    :cond_38
    add-int/lit8 v5, v5, 0x1

    goto :goto_29

    .line 32
    :cond_3b
    invoke-interface {v7}, Ljava/util/List;->size()I

    move-result v10

    new-array v8, v10, [I

    .line 33
    .local v8, "primes":[I
    const/4 v1, 0x0

    .line 34
    .local v1, "index":I
    invoke-interface {v7}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v0

    .local v0, "i$":Ljava/util/Iterator;
    :goto_46
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v10

    if-eqz v10, :cond_5c

    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/lang/Integer;

    .line 35
    .local v6, "prime":Ljava/lang/Integer;
    add-int/lit8 v2, v1, 0x1

    .end local v1    # "index":I
    .local v2, "index":I
    invoke-virtual {v6}, Ljava/lang/Integer;->intValue()I

    move-result v10

    aput v10, v8, v1

    move v1, v2

    .line 36
    .end local v2    # "index":I
    .restart local v1    # "index":I
    goto :goto_46

    .line 38
    .end local v6    # "prime":Ljava/lang/Integer;
    :cond_5c
    return-object v8
.end method

.method public static main([Ljava/lang/String;)V
    .registers 1
    .param p0, "argv"    # [Ljava/lang/String;

    .prologue
    .line 73
    return-void
.end method
