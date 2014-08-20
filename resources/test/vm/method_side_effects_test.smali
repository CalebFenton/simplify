.class Lmethod_side_effects_test;
.super Ljava/lang/Object;

.field public instanceString:Ljava/lang/String;

.method public static EmptyMethod()V
  .locals 0

  return-void
.end method

.method public static ConstOps()V
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
  const-class v0, Lmethod_side_effects_test;

  return-void
.end method

.method public static InvokeWhitelistedMethods()V
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

.method public static InvokeMethodWithNoSideEffects()V
  .locals 0

  invoke-static {}, Lmethod_side_effects_test;->ConstOps()V
  return-void
.end method

.method public static NewInstanceNonLocalWhitelistedClass()V
  .locals 1

  new-instance v0, Ljava/lang/StringBuilder;

  return-void
.end method

.method public static NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V
  .locals 1

  new-instance v0, Lmethod_side_effects_test;

  return-void
.end method

.method public static NewInstanceOfMethodWithNoStaticInitializer()V
  .locals 1

  new-instance v0, Lmethod_side_effects_test;

  return-void
.end method

.method public static WriteOutputStream(Ljava/lang/OutputStream;[B)V
  .locals 0

  invoke-virtual {p0, p1}, Ljava/io/OutputStream;->write([B)V
  invoke-virtual {p0}, Ljava/io/OutputStream;->close()V

  return-void
.end method

.method public static InvokeUnknownMethod()V
  .locals 0

  invoke-static {}, Lthis/class/should/not/exist/refridgerator;->chill123()V

  return-void
.end method

.method public static InvokeSideEffectMethod(Ljava/lang/OutputStream;[B)V
  .locals 0

  invoke-static {p0, p1}, Lmethod_side_effects_test;->WriteOutputStream(Ljava/lang/OutputStream;[B)V

  return-void
.end method

.method public static NewInstanceNonLocalNonWhitelistedClass()V
  .locals 1

  new-instance v0, Ljava/lang/Thread;

  return-void
.end method

.method public static NewInstanceOfClassWithStaticInitializerWithStrongSideEffects()V
  .locals 1

  new-instance v0, Lstrong_side_effect;

  return-void
.end method

.method public static NewInstanceOfClassWithStaticInitializerWithWeakSideEffects()V
  .locals 1

  new-instance v0, Lweak_side_effect;

  return-void
.end method

.method public static InvokeOfNonAnalyzableMethod()V
  .locals 0

  invoke-static {}, Lmethod_side_effects_test;->InvokeOfNonAnalyzableMethod()V

  return-void
.end method

.method public ModifyInstanceMember()V
  .locals 2

  sput p0, Lmethod_side_effects_test;->instanceString:Ljava/lang/String;

  return-void
.end method