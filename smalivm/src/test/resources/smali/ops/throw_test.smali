.class Lthrow_test;
.super Ljava/lang/Object;

.method public static throwNullPointerException()V
  .locals 1

  :try_start_1
  # Throw a null pointer exception
  const/4 v0, 0x0
  invoke-virtual {v0}, Ljava/lang/String;->length()I
  :try_end_1
  .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

  return-void

  :catch_1
  move-exception v0
  throw v0
.end method
