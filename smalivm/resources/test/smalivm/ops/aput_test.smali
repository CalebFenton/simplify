.class Laput_test;
.super Ljava/lang/Object;

.method public static TestArrayPut()V
    .locals 3

    aput v2, v0, v1 # v0[v1] = v2

    return-void
.end method

.method public static TestArrayPutWide()V
    .locals 3

    aput-wide v2, v0, v1

    return-void
.end method

.method public static TestArrayPutObject()V
    .locals 3

    aput-object v2, v0, v1

    return-void
.end method

.method public static TestArrayPutBoolean()V
    .locals 3

    aput-boolean v2, v0, v1

    return-void
.end method

.method public static TestArrayPutByte()V
    .locals 3

    aput-byte v2, v0, v1

    return-void
.end method

.method public static TestArrayPutChar()V
    .locals 3

    aput-char v2, v0, v1

    return-void
.end method

.method public static TestArrayPutShort()V
    .locals 3

    aput-short v2, v0, v1

    return-void
.end method