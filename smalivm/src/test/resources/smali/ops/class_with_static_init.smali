.class Lclass_with_static_init;
.super Ljava/lang/Object;

.field public static string:Ljava/lang/String;

.method public static constructor <clinit>()V
  .locals 1

  const-string v0, "Uhhh, about 11, sir."
  sput-object v0, Lclass_with_static_init;->string:Ljava/lang/String;

  return-void
.end method

.method public static getString()Ljava/lang/String;
  .locals 1

  sget-object v0, Lclass_with_static_init;->string:Ljava/lang/String;

  return v0
.end method

.method public static getStaticFieldFromInvokeStaticTestClass()Ljava/lang/String;
  .locals 1

  sget-object v0, Linvoke_static_test;->sometimes_initialized:Ljava/lang/String;

  return v0
.end method
