.class public Linvoke_direct_test;
.super Ljava/lang/Object;

.method public static callInitOfClassWithDefaultConstructor()V
  .locals 1

  new-instance v0, Lclass_with_default_constructor;
  invoke-direct {v0}, Lclass_with_default_constructor;-><init>()V

  return-void
.end method

.method public static callInitOfClassWithoutDefaultConstructor()V
  .locals 3

  new-instance v0, Lclass_without_default_constructor;
  move-object v2, v1
  invoke-direct {v0, v1}, Lclass_without_default_constructor;-><init>([C)V

  return-void
.end method

.method public static callInitOfClassWithoutDefaultConstructorWhichChecksForNullArgument()V
  .locals 3

  new-instance v0, Lclass_without_default_constructor_checks_null;
  move-object v2, v1
  invoke-direct {v0, v1}, Lclass_without_default_constructor_checks_null;-><init>([C)V

  return-void
.end method
