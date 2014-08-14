.class Lmove_test;
.super Ljava/lang/Object;

.method public static TestMoveRegisterPrimitive()V
    .locals 2

    move v1, v0

    return-void
.end method

.method public static TestMoveRegisterObject()V
    .locals 2

    move-object v1, v0

    return-void
.end method

.method public static TestMoveResult()V
    .locals 1

    move-result v0

    return-void
.end method

.method public static TestMoveException()V
    .locals 1

    move-exception v0

    return-void
.end method
