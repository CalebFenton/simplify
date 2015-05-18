.class Lpeephole_strategy_test;
.super Ljava/lang/Object;

.method public static ClassForName()V
  .locals 1

  :try_start_0
  invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  move-result v0
  :try_end_0
  .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

  :catch_0
  return-void
.end method

.method public static StringInit()V
  .locals 2

  invoke-direct {v0, v1}, Ljava/lang/String;-><init>([B)V

  return-void
.end method
