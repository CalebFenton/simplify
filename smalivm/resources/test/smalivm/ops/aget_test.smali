.class Laget_test;
.super Ljava/lang/Object;

.method public static ArrayGetUninitializedInt()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static ArrayGet()V
    .locals 2

    aget v0, v0, v1

    return-void
.end method

.method public static ArrayGetWithCatch()V
    .locals 2

    :try_start_0
    aget v0, v0, v1
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    goto :return
.end method

.method public static ArrayGetWide()V
    .locals 4

    aget-wide v0, v0, v1

    return-void
.end method

.method public static ArrayGetObject()V
    .locals 3

    aget-object v0, v0, v1

    return-void
.end method

.method public static ArrayGetBoolean()V
    .locals 3

    aget-boolean v0, v0, v1

    return-void
.end method

.method public static ArrayGetByte()V
    .locals 3

    aget-byte v0, v0, v1

    return-void
.end method

.method public static ArrayGetChar()V
    .locals 3

    aget-char v0, v0, v1

    return-void
.end method

.method public static ArrayGetShort()V
    .locals 3

    aget-short v0, v0, v1

    return-void
.end method
