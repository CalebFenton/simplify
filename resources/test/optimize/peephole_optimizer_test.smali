.class Lpeephole_optimizer_test;
.super Ljava/lang/Object;

.method public static InvokeClassForNameWithUnknownClass()V
  .locals 1

  const-string v0, "com.funky.unknown.class"
  invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  move-result v0

  return-void
.end method

.method public static InvokeClassForNameWithKnownClass()V
  .locals 1

  const-string v0, "java.lang.String"
  invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  move-result v0

  return-void
.end method

.method public static MethodInvokeWithUnknownMethod()V
  .locals 1


  return-void
.end method

.method public static MethodInvokeWithUnknownTarget()V
  .locals 1


  return-void
.end method

.method public static MethodInvokeWithUnknownParameters()V
  .locals 1


  return-void
.end method

.method public static MethodInvokeForKnownMethodAndTargetAndParameters()V
  .locals 3

  # Build parameter values
  const/4 v0, 0x1
  new-array v0, v0, [Ljava/lang/Object;
  const/4 v1, 0x0
  const-string v2, "hello,world!"
  aput-object v2, v0, v1 # Object[] v0[v1] = "hello,world!"

  # Build parameter types
  const/4 v3, 0x1
  new-array v3, v3, [Ljava/lang/Class;
  const-class v4, Ljava/lang/String;
  aput-object v4, v3, v1 # Class[] v3[v1] = java.lang.String.class

  # Get method
  const-class v4, Ljava/lang/StringBuilder;
  const-string v2, "append"
  invoke-virtual {v4, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
  move-result-object v3

  # StringBuilder v4 = new StringBuilder();
  new-instance v4, Ljava/lang/StringBuilder;
  invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

  # v3 = v4.append("hello,world!")
  # v3 = method, v4 = target, v0 = parameters
  invoke-virtual {v3, v4, v0}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
  move-result-object v3

  return-void
.end method

.method public static MethodInvokeForMethodWithSetVisible()V
  .locals 1

  # Should not replace method call since reflection is necessary

  return-void
.end method
