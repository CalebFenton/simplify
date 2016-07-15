.class Ldead_removal_strategy_test;
.super Ljava/lang/Object;

.method public constructor <init>()V
  .locals 0

  invoke-direct {p0}, Ljava/lang/Object;-><init>()V

  return-void
.end method

.method public static unusedAssignment()I
  .locals 1

  const/4 v0, 0x1
  const/4 v0, 0x2

  return v0
.end method

.method public static deadCode()V
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

.method public static deadCodeWithStrongSideEffect()V
  .locals 1

  goto :return

  invoke-static {}, Ldead_removal_strategy_test;->hasStrongSideEffects()V

  :return
  return-void
.end method

.method public static deadOpWithLabel()I
  .locals 2

  const/4 v0, 0x1
  if-gtz v0, :return

  const/4 v0, 0x1

  :return
  const/4 v1, 0x1

  return v0
.end method

.method public static reassignedInOnlyOneMultiverse(I)I
  .locals 2

  const/4 v0, 0x1

  const/4 v1, 0x0
  if-ge p0, v1, :return

  const/4 v0, 0x0

  :return
  return v0
.end method

.method public static uselessGoto()V
  .locals 0

  goto :return

  :return
  return-void
.end method

.method public static deadTryCatchBlock()V
  .locals 1

  :try_start_1
  const/4 v0, 0x2

  :try_end_1
  .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

  :catch_1
  :end
  return-void
.end method

.method public static unusedResultNoSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Ldead_removal_strategy_test;->addOneNoSideEffects(I)I

  return v0
.end method

.method public static unusedResultWithSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Ldead_removal_strategy_test;->addOneWithSideEffects(I)I

  return v0
.end method

.method private static addOneNoSideEffects(I)I
  .locals 0

  add-int/2addr p0, p0

  return p0
.end method

.method private static addOneWithSideEffects(I)I
  .locals 0

  invoke-static {}, Ldead_removal_strategy_test;->hasStrongSideEffects()V
  add-int/2addr p0, p0

  return p0
.end method

.method public static moveP0IntoV0With30Locals(I)V
    .locals 30

    # invoke-static needs 4 bit registers, so gotta move from p0 to v0
    move-object/from16 v0, p0
    invoke-static {v0}, Ldead_removal_strategy_test;->emptyMethod(I)V

    # the crux. optimizer must see p0 is not used, but know v0 is, so don't remove the move-object/from16.
    move-object/from16 p0, v0

    return-object p0
.end method

.method public static appendV0ToV1StringBuilderAndReturnResult()V
  .locals 5

  invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  move-result-object v1

  return-object v1
.end method

.method private static hasNopPadding()V
  .locals 1

  #@0
  const/16 v0, 0x1

  #@2
  new-array v0, v0, [B

  #@4
  fill-array-data v0, :array_0

  #@7
  # if this is removed, nop @b is not needed and is removed by dexlib
  fill-array-data v0, :array_0

  #@a
  return-void

  #@b
  nop

  #@c
  :array_0
  .array-data 1
      0x1t
  .end array-data
.end method

.method public static moveResult()Ljava/lang/String;
  .locals 1

  invoke-static {}, Ldead_removal_strategy_test;->getString()Ljava/lang/String;
  move-result v0

  invoke-static {}, Ldead_removal_strategy_test;->getString()Ljava/lang/String;
  move-result v0

  const-string v0, "other stringy"
  return-object v0
.end method

.method public static getString()Ljava/lang/String;
  .locals 1

  const-string v0, "stringy"
  return-object v0
.end method


.method private static hasStrongSideEffects()V
  .locals 4

  new-instance v0, Ljava/io/File;
  const-string v1, "nope this should not exist"
  invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

  invoke-virtual {v0}, Ljava/io/File;->delete()Z
  move-result v0

  return-void
.end method

.method private static emptyMethod(I)V
  .locals 0

  return-void
.end method