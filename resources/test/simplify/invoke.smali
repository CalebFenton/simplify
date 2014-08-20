.class Linvoke;
.super Ljava/lang/Object;

.method public virtual_caller()I
    .locals 3

    .prologue
    .line 4
    move-object v0, p0

    move-object v1, v0

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Linvoke;->virtual_callee(I)I

    move-result v1

    move v0, v1

    return v0
.end method

.method public virtual_callee(I)I
    .locals 4

    .prologue
    .line 8
    move-object v0, p0

    move v1, p1

    move v2, v1

    const/4 v3, 0x5

    mul-int/lit8 v2, v2, 0x5

    move v0, v2

    return v0
.end method

.method public static_caller()I
    .locals 2

    .prologue
    .line 4
    const/4 v0, 0x1

    invoke-static {v0}, Linvoke;->static_callee(I)I

    move-result v1

    move v0, v1

    return v0
.end method

.method public static_callee(I)I
    .locals 4

    .prologue
    .line 8
    move v1, p1

    move v2, v1

    const/4 v3, 0x5

    mul-int/lit8 v2, v2, 0x5

    move v0, v2

    return v0
.end method