.class Loptimize_test;
.super Ljava/lang/Object;

.field public instanceString:Ljava/lang/String;

.method public static UnusedAssignment()I
  .locals 1

  const/4 v0, 0x1
  const/4 v0, 0x2

  return v0
.end method

.method public static DeadCode()I
  .locals 1

  const/4 v0, 0x1

  goto :return

  add-int/2addr v0, v0
  nop
  nop
  nop

  :return
  return v0
.end method

.method public static UnusedResultNoSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Loptimize_test;->AddOneNoSideEffects(I)I

  return v0
.end method

.method public static UnusedResultWithSideEffects()I
  .locals 1

  const/4 v0, 0x2
  invoke-static {v0}, Loptimize_test;->AddOneWithSideEffects(I)I

  return v0
.end method

# TODO: Long, Float, Double, Byte, Char, Class and String
.method public static KnownIntForBinaryMathOp()I
  .locals 1

  const/4 v0, 0x1
  add-int/2addr v0, v0

  return v0
.end method

.method public static KnownIntForMove()I
    .locals 2

    const/4 v0, 0x5
    move v1, v0

    return v1
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

.method public static UseUknownParameter(I)I
    .locals 1

    const/4 v0, 0x0
    add-int/2addr v0, v1
    return v0
.end method

.method private static AddOneNoSideEffects(I)I
  .locals 0

  add-int/2addr p0, p0

  return p0
.end method

.method private static AddOneWithSideEffects(I)I
  .locals 0

  invoke-static {}, Lunknown_class;->UnknownMethodHasSideEffects()V
  add-int/2addr p0, p0

  return p0
.end method

# The move-objects aren't me trolling. They're from unoptimized dx output.
# They're useful for breaking some assumptions, so they're kept.
.method public static StringBuilderHelloWorld()Ljava/lang/String;
    .locals 5

    .prologue
    .line 43
    new-instance v2, Ljava/lang/StringBuilder;

    move-object v4, v2
    move-object v2, v4
    move-object v3, v4

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V
    move-object v1, v2

    .line 45
    move-object v2, v1
    const-string v3, "hello"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 47
    move-object v2, v1
    const-string v3, ","
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 49
    move-object v2, v1
    const-string v3, "world!"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 51
    move-object v2, v1
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v2
    move-object v0, v2

    return-object v0
.end method