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

    invoke-static {}, Lmove_test;->fortyTwo()I

    move-result v0

    return-void
.end method

.method private static fortyTwo()I
    .locals 1

    const/16 v0, 0x2a

    return v0
.end method

.method public static moveException()V
    .locals 2

    new-instance v1, Ljava/lang/Exception;

    invoke-direct {v1}, Ljava/lang/Exception;-><init>()V

    :try_start_0
    throw v1
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :handler_0

    :handler_0
    move-exception v0

    return-void
.end method
