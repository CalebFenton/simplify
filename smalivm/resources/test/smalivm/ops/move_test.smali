.class Lmove_test;
.super Ljava/lang/Object;

.method public static moveRegisterPrimitive()V
    .locals 2

    move v1, v0

    return-void
.end method

.method public static moveRegisterObject()V
    .locals 2

    move-object v1, v0

    return-void
.end method

.method public static moveResult()V
    .locals 1

    move-result v0

    return-void
.end method

.method public static moveException()V
    .locals 1

    move-exception v0

    return-void
.end method
