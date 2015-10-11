.class Lconstant_propigation_strategy_test;
.super Ljava/lang/Object;

# TODO: Long, Float, Double, Byte, Char, Class and String
.method public static AddInt2Addr()V
  .locals 1

  add-int/2addr v0, v0

  return-void
.end method

.method public static MoveV0IntoV1()V
  .locals 2

  move v1, v0

  return-void
.end method

.method public static InvokeIntegerMethods()I
  .locals 2

  new-instance v0, Ljava/lang/Integer;
  const/4 v1, 0x1
  invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V
  invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
  move-result v0

  return v0
.end method

.method public static UseAddParameterToZero(I)I
  .locals 1

  const/4 v0, 0x0
  add-int/2addr v0, p0

  return v0
.end method

.method public static StaticGetIntegerMaxValue()V
  .locals 1

  sget v0, Ljava/lang/Integer;->MAX_VALUE:I

  return-void
.end method

.method public static ArrayGetFromV0AtV1ToV0()V
  .locals 2

  aget v0, v0, v1

  return-void
.end method

.method public static NonDeterministicallyStaticGetIntegerMaxValue(I)V
  .locals 1

  if-eqz p0, :end

  sget v0, Ljava/lang/Integer;->MAX_VALUE:I

  :end
  return-void
.end method

.method private static AddSelfNoSideEffects(I)I
  .locals 0

  add-int/2addr p0, p0

  return p0
.end method

.method private static AddSelfWithSideEffects(I)I
  .locals 0

  invoke-static {}, Lunknown_class;->UnknownMethodHasSideEffects()V
  add-int/2addr p0, p0

  return p0
.end method

.method private static IntegerValueOf()Ljava/lang/Integer;
  .locals 1

  invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
  move-result-object v0

  return-object v0
.end method
