.class Larray_length_test;
.super Ljava/lang/Object;

.method public static getLength()V
    .locals 1

    array-length v0, v0

    return-void
.end method

.method public static getLengthWithCatch()V
    .locals 2

    :try_start_0
    array-length v0, v0
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
