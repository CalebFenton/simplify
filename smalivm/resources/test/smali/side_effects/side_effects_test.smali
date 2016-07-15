.class Lside_effects_test;
.super Ljava/lang/Object;

.field public instanceString:Ljava/lang/String;

.method public static emptyMethod()V
  .locals 0

  return-void
.end method

.method public static constOps()V
  .locals 1

  const/4 v0, -0x3
  const/16 v0, 0x4242
  const/16 v0, -0xf
  const v0, 0x42424242
  const/high16 v0, 0x42420000
  const-wide/16 v0, 0x4242
  const-wide/32 v0, 0x1b7740
  const-wide v0, 0x4242424242424242L
  const-wide/high16 v0, 0x4242000000000000L
  const-string v0, "When I need to identify rebels, I look for men with principles"
  const-string/jumbo v0, "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles"
  const-class v0, Lside_effects_test;

  return-void
.end method

.method public static invokeWhitelistedMethods()V
  .locals 2

  new-instance v0, Ljava/lang/StringBuilder;
  const-string v1, "init with me plz plz"
  invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

  const-string v1, "append me plz"
  invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  move-result-object v0

  invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
  return-void
  #move-result-object v1
  #return-object v1
.end method

.method public static invokeMethodWithNoSideEffects()V
  .locals 0

  invoke-static {}, Lside_effects_test;->constOps()V
  return-void
.end method

.method public static newInstanceNonLocalWhitelistedClass()V
  .locals 1

  new-instance v0, Ljava/lang/StringBuilder;

  return-void
.end method

.method public static newInstanceOfMethodWithStaticInitializerWithNoSideEffects()V
  .locals 1

  new-instance v0, Lno_side_effect;

  return-void
.end method

.method public static newInstanceOfMethodWithNoStaticInitializer()V
  .locals 1

  new-instance v0, Lside_effects_test;

  return-void
.end method

.method public static invokeSideEffectMethod(Ljava/io/OutputStream;[B)V
  .locals 0

  invoke-static {p0, p1}, Lside_effects_test;->writeOutputStream(Ljava/io/OutputStream;[B)V

  return-void
.end method

.method public static newInstanceOfClassWithStaticInitializerWithStrongSideEffects()V
  .locals 1

  new-instance v0, Lstrong_side_effect;

  return-void
.end method

.method public static newInstanceOfClassWithStaticInitializerWithWeakSideEffects()V
  .locals 1

  new-instance v0, Lweak_side_effect;

  return-void
.end method

.method public static invokeOfNonAnalyzableMethod()V
  .locals 0

  invoke-static {}, Lside_effects_test;->invokeOfNonAnalyzableMethod()V

  return-void
.end method

.method public modifyInstanceMember()V
  .locals 2

  sput p0, Lside_effects_test;->instanceString:Ljava/lang/String;

  return-void
.end method

.method public static writeOutputStream(Ljava/io/OutputStream;[B)V
  .locals 0

  invoke-virtual {p0, p1}, Ljava/io/OutputStream;->write([B)V
  invoke-virtual {p0}, Ljava/io/OutputStream;->close()V

  return-void
.end method
