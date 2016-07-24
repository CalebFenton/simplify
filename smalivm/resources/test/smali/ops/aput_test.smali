.class Laput_test;
.super Ljava/lang/Object;

.method public static put()V
    .locals 3

    aput v2, v0, v1 # v0[v1] = v2

    return-void
.end method

.method public static putWithCatch()V
    .locals 3

    :try_start_0
    aput v2, v0, v1 # v0[v1] = v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method

.method public static putWide()V
    .locals 3

    aput-wide v2, v0, v1

    return-void
.end method

.method public static putObject()V
    .locals 3

    aput-object v2, v0, v1

    return-void
.end method

.method public static putObjects()V
    .locals 3

    aput-object v2, v0, v1
    aput-object v4, v0, v3

    return-void
.end method

.method public static putBoolean()V
    .locals 3

    aput-boolean v2, v0, v1

    return-void
.end method

.method public static putByte()V
    .locals 3

    aput-byte v2, v0, v1

    return-void
.end method

.method public static putChar()V
    .locals 3

    aput-char v2, v0, v1

    return-void
.end method

.method public static putShort()V
    .locals 3

    aput-short v2, v0, v1

    return-void
.end method
