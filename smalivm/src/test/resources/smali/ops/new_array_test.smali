.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static createIntegerArray()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

.method public static create2DIntegerArray()V
    .locals 1

    new-array v0, v0, [[I

    return-void
.end method

.method public static createLocalInstanceArray()V
    .locals 1

    new-array v0, v0, [Lnew_array_test;

    return-void
.end method

.method public static create2DLocalInstanceArray()V
    .locals 1

    new-array v0, v0, [[Lnew_array_test;

    return-void
.end method

.method public static createNonExistentArrayClass()V
    .locals 1

    :try_start_0
    new-array v0, v0, [Ldoes/not/exist;
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
