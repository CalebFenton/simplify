.class Lmethod_backed_graph_test;
.super Ljava/lang/Object;

.method private static verySimple()V
  .locals 5

  const/4 v0, 0x0
  const/4 v1, 0x1
  const/4 v2, 0x2
  const/4 v3, 0x3
  const/4 v4, 0x4

  return-void
.end method

.method private static hasGotoAndOneNop()V
    .locals 0

    goto :return

    nop

    :return
    return-void
.end method

.method private static hasNoNopPadding()V
    .locals 1

    #@0
    const/16 v0, 0x1

    #@2
    new-array v0, v0, [B

    #@4
    fill-array-data v0, :array_0

    #@7
    return-void

    #@8
    :array_0
    .array-data 1
        0x1t
    .end array-data
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

.method private static hasGoto16AndManyNops()V
    .locals 0

    goto/16 :return

    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop

    :return
    return-void
.end method
