.class Laget_test;
.super Ljava/lang/Object;

.method public static getUninitializedInt()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static get()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static getWithCatch()V
    .locals 2

    :try_start_0
    aget v0, v0, v1
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method

.method public static getWide()V
    .locals 4

    aget-wide v0, v0, v1

    return-void
.end method

.method public static getObject()V
    .locals 3

    aget-object v0, v0, v1

    return-void
.end method

.method public static getBoolean()V
    .locals 3

    aget-boolean v0, v0, v1

    return-void
.end method

.method public static getByte()V
    .locals 3

    aget-byte v0, v0, v1

    return-void
.end method

.method public static getChar()V
    .locals 3

    aget-char v0, v0, v1

    return-void
.end method

.method public static getShort()V
    .locals 3

    aget-short v0, v0, v1

    return-void
.end method
