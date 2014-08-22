.class Lpeephole_optimizer_test;
.super Ljava/lang/Object;

.method public static InvokeClassForNameWithUnkownClass()V
  .locals 1

  const-string v0, "com.funky.unknown.class"
  invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  move-result v0

  return-void
.end method
