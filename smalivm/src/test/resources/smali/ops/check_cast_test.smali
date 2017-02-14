.class Lcheck_cast_test;
.super Ljava/lang/Object;

.method public static castToObject()V
    .locals 1

    check-cast v0, Ljava/lang/Object;

    return-void
.end method

.method public static castToString()V
    .locals 1

    check-cast v0, Ljava/lang/String;

    return-void
.end method

.method public static castToStringWithCatch()V
    .locals 2

    :try_start_0
    check-cast v0, Ljava/lang/String;
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
