.class Lorg/cf/test/ExceptionalCode;
.super Ljava/lang/Object;


.method public static createAndThrowException()V
    .locals 1

    new-instance v0, Lorg/cf/test/CustomException;
    invoke-direct {v0}, Lorg/cf/test/CustomException;-><init>()V

    throw v0

    return-void
.end method


.method public static callsExceptionalMethod()V
  .locals 1

  :try_start_1
  invoke-static {}, Lorg/cf/test/ExceptionalCode;->createAndThrowException()V
  :try_end_1
  .catch Lorg/cf/test/CustomException; {:try_start_1 .. :try_end_1} :catch_1
  .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_2

  return-void

  :catch_1
  move-exception v0
  throw v0

  :catch_2
  move-exception v0
  throw v0
.end method
