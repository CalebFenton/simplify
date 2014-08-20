.class Ldead_detector_test;
.super Ljava/lang/Object;

.method public static UnusedAssignment()I
  .locals 1

  const/4 v0, 0x1
  const/4 v0, 0x2

  return v0
.end method

.method public static DeadCode()V
  .locals 1

  const/4 v0, 0x1

  goto :return

  add-int/2addr v0, v0
  nop
  nop
  nop

  :return
  return-void
.end method

.method public static UselessGoto()V
  .locals 0

  goto :return

  :return
  return-void
.end method


.method public static UnusedResultNoSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Lconstant_builder_test;->AddOneNoSideEffects(I)I

  return v0
.end method

.method public static UnusedResultWithSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Lconstant_builder_test;->AddOneWithSideEffects(I)I

  return v0
.end method
