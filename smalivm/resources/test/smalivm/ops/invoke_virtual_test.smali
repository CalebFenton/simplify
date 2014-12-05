.class Linvoke_virtual_test;
.super Ljava/lang/Object;

# direct methods
.method constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static InvokeReturnVoid()V
  .locals 1

  invoke-virtual {v0}, Linvoke_virtual_test;->ReturnVoid()V

  return-void
.end method

.method public static InvokeReturnInt()V
  .locals 1

  invoke-virtual {v0}, Linvoke_virtual_test;->ReturnInt()I

  return-void
.end method

.method public static InvokeReturnParameter()V
  .locals 2

  invoke-virtual {v0, v1}, Linvoke_virtual_test;->ReturnParameter(I)I

  return-void
.end method

.method public static InvokeGetComponentType()V
  .locals 1

  const-class v0, [I
  invoke-virtual {v0}, Ljava/lang/Class;->getComponentType()Ljava/lang/Class;

  return-void
.end method

.method public static InvokeRangeManyParameters()V
  .locals 8

  # v0 - instance
  # v1 - I
  # v2+v3 - J
  # v4+v5 - J
  # v6+v7 - J
  invoke-virtual/range {v0 .. v7}, Linvoke_virtual_test;->ManyParametersReturnsLast(JJJI)I

  return-void
.end method

.method private ReturnVoid()V
    .locals 0

    return-void
.end method

.method private ReturnInt()I
    .locals 1

    const/4 v0, 0x7

    return v0
.end method

.method private ReturnParameter(I)I
    .locals 1

    move v0, p1

    return v0
.end method

.method private ManyParametersReturnsLast(JJJI)I
  .locals 0

  return v7
.end method