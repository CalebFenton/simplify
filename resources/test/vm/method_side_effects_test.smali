.class Lmethod_side_effects_test;
.super Ljava/lang/Object;

.field public instanceString:Ljava/lang/String;

.method public static EmptyMethod()V
  .locals 0

  nop
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
  const-class v0, Lmethod_side_effects;

  return-void
.end method

.method public static NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V
  .locals 1

  new-instance v0, Lno_side_effects;

  return-void
.end method

.method public static NewInstanceOfMethodWithNoStaticInitializer()V
  .locals 1

  new-instance v0, Lmethod_side_effects;

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

  invoke-static {p0, p1}, Lmethod_side_effects;->WriteOutputStream(Ljava/lang/OutputStream;[B)V

  return-void
.end method

.method public static NewInstanceNonLocalNonWhitelistedClass()V
  .locals 1

  new-instance v0, Ljava/lang/Thread;

  return-void
.end method

.method public static NewInstanceOfMethodWithStaticInitializerWithStrongSideEffects()V
  .locals 1

  new-instance v0, Lstrong_side_effects;

  return-void
.end method

.method public static NewInstanceOfMethodWithStaticInitializerWithWeakSideEffects()V
  .locals 1

  new-instance v0, Lweak_side_effects;

  return-void
.end method

.method public static InvokeOfNonAnalyzableMethod()V
  .locals 0

  invoke-static {}, Lmethod_side_effects;->InvokeOfNonAnalyzableMethod()V

  return-void
.end method

.method public static ModifyInstanceMember()V
  .locals 1

  const-string v0, "nothing to see here"
  sput p0, Lmethod_side_effects;->instanceString:Ljava/lang/String;

  return-void
.end method