.class public Lnumeric;
.super Ljava/lang/Object;

.method public static simple1()I
    .locals 1

    const/4 v0, 0x1

    const/4 v0, 0x5

    const/4 v0, 0x7

    return v0
.end method

.method public static simple2()I
    .locals 1

    const/4 v0, 0x2

    const/4 v0, 0x5

    const/16 v0, 0x42

    return v0
.end method

.method public static trim_unused1()V
    .locals 2

    const/4 v0, 0x2

    const/4 v1, 0x7

    return-void
.end method

.method public static simple_ops1()I
    .locals 3

    const/4 v2, 0x7

    return v2
.end method

.method public static intra_method1(I)I
    .locals 3

    const/4 v0, 0x1

    const/4 v1, 0x3

    goto :someLabel

    :otherLabel
    add-int v0, v0, p0

    goto :end

    :someLabel
    add-int v2, v0, v1

    add-int v2, v2, v2

    goto :otherLabel

    :end
    return v2
.end method

.method public static double_loop(I)I
    .locals 5

    .prologue
    const/16 v4, 0x5

    const/4 v1, 0x0

    .line 79
    move v3, v1

    move v0, v1

    .line 81
    :goto_0
    if-ge v3, v4, :cond_1

    move v2, v0

    move v0, v1

    .line 82
    :goto_1
    if-ge v0, v4, :cond_0

    .line 83
    add-int/lit8 v2, v2, 0x1

    .line 82
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 81
    :cond_0
    add-int/lit8 v0, v3, 0x1

    move v3, v0

    move v0, v2

    goto :goto_0

    .line 87
    :cond_1
    return v0
.end method

.method public crazyloop(I)I
    .locals 3

    .prologue
    const/4 v0, 0x0

    .line 91
    move v1, v0

    .line 92
    :goto_0
    const/16 v2, 0xa

    if-ge v1, v2, :cond_0

    .line 93
    add-int/lit8 v2, v0, 0x1

    .line 92
    add-int/lit8 v0, v1, 0x1

    move v1, v0

    move v0, v2

    goto :goto_0

    .line 96
    :cond_0
    if-lez v0, :cond_1

    .line 97
    add-int/2addr v0, v0

    .line 99
    :cond_1
    return v0
.end method