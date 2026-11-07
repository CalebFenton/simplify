.class public Ltask66/DeadCode;
.super Ljava/lang/Object;

.field public static visible:I
.field public static volatile volatileVisible:I
.field public static mutableBranch:I

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static overwritten()I
    .locals 1
    const/4 v0, 0x1
    const/4 v0, 0x2
    return v0
.end method

.method public static overwrittenWide()J
    .locals 2
    const-wide/16 v0, 0x1
    const-wide/16 v0, 0x2
    return-wide v0
.end method

.method public static liveOnBranch(I)I
    .locals 1
    const/4 v0, 0x1
    if-eqz p0, :return
    const/4 v0, 0x2
    :return
    return v0
.end method

.method private static pureInt(I)I
    .locals 0
    add-int/2addr p0, p0
    return p0
.end method

.method public static unusedMoveResult()I
    .locals 2
    const/4 v0, 0x4
    invoke-static {v0}, Ltask66/DeadCode;->pureInt(I)I
    move-result v1
    return v0
.end method

.method public static unusedMoveResultRange()I
    .locals 6
    const/4 v0, 0x4
    invoke-static/range {v0 .. v0}, Ltask66/DeadCode;->pureInt(I)I
    move-result v1
    return v0
.end method

.method private static mixedReturnOrThrow(I)I
    .locals 1
    if-eqz p0, :normal
    const/4 v0, 0x0
    throw v0
    :normal
    const/16 v0, 0x9
    return v0
.end method

.method public static mixedCallMustRemain(I)I
    .locals 1
    invoke-static {p0}, Ltask66/DeadCode;->mixedReturnOrThrow(I)I
    move-result v0
    const/4 v0, 0x3
    return v0
.end method

.method private static alwaysThrows()I
    .locals 1
    const/4 v0, 0x0
    throw v0
.end method

.method public static catchesThrowingNone()I
    .locals 1
    :try_start
    invoke-static {}, Ltask66/DeadCode;->alwaysThrows()I
    move-result v0
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x5
    return v0
.end method

.method public static escapingThrowingNone()I
    .locals 1
    invoke-static {}, Ltask66/DeadCode;->alwaysThrows()I
    move-result v0
    return v0
.end method

.method private static visibleWrite(I)V
    .locals 0
    sput p0, Ltask66/DeadCode;->visible:I
    return-void
.end method

.method private static mutateArgument([I)V
    .locals 2
    const/4 v0, 0x0
    const/16 v1, 0x9
    aput v1, p0, v0
    return-void
.end method

.method public static mutableArgumentCall()[I
    .locals 3
    const/4 v0, 0x1
    new-array v1, v0, [I
    invoke-static {v1}, Ltask66/DeadCode;->mutateArgument([I)V
    return-object v1
.end method

.method private static strongNoArgs()V
    .locals 0
    invoke-static {}, Ljava/lang/System;->gc()V
    return-void
.end method

.method private static declared-synchronized synchronizedPure()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

.method public static synchronizedCallMustRemain()I
    .locals 1
    invoke-static {}, Ltask66/DeadCode;->synchronizedPure()I
    move-result v0
    const/4 v0, 0x2
    return v0
.end method

.method private static readVolatile()I
    .locals 1
    sget v0, Ltask66/DeadCode;->volatileVisible:I
    return v0
.end method

.method public static volatileReadCallMustRemain()I
    .locals 1
    invoke-static {}, Ltask66/DeadCode;->readVolatile()I
    move-result v0
    const/4 v0, 0x3
    return v0
.end method

.method public static reachedStrongCall()V
    .locals 0
    invoke-static {}, Ltask66/DeadCode;->strongNoArgs()V
    return-void
.end method

.method public static sideEffectingCall()I
    .locals 1
    const/4 v0, 0x6
    invoke-static {v0}, Ltask66/DeadCode;->visibleWrite(I)V
    return v0
.end method

.method public static constructorCall()Ljava/lang/Object;
    .locals 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

.method public static unusedConstructor()V
    .locals 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static classInitializingCall()I
    .locals 1
    invoke-static {}, Ltask66/Initializer;->pure()I
    move-result v0
    const/4 v0, 0x4
    return v0
.end method

.method public static staticRootMutation()I
    .locals 1
    invoke-static {}, Ltask66/Initializer;->rootArray()[I
    move-result-object v0
    invoke-static {}, Ltask66/Initializer;->mutateRoot()V
    invoke-static {}, Ltask66/Initializer;->readRoot()I
    move-result v0
    return v0
.end method

.method public static wideValueSurvivesThrow()J
    .locals 4
    const-wide/16 v0, 0x42
    const/4 v2, 0x0
    const/4 v3, 0x0
    :try_start
    aget-wide v0, v2, v3
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-wide v0
    :handler
    move-exception v2
    return-wide v0
.end method

.method public static unreachableStrong()V
    .locals 0
    goto :return
    invoke-static {}, Ljava/lang/System;->gc()V
    :return
    return-void
.end method

.method public static mutableStaticMakesOneBranchLookDead()V
    .locals 1
    sget v0, Ltask66/DeadCode;->mutableBranch:I
    if-eqz v0, :return
    invoke-static {}, Ljava/lang/System;->gc()V
    :return
    return-void
.end method

.method public static partialWithUnreachableStrong()V
    .locals 0
    :loop
    nop
    goto :loop
    invoke-static {}, Ljava/lang/System;->gc()V
    return-void
.end method

.method public static switchTarget()I
    .locals 2
    const/4 v1, 0x1
    sparse-switch v1, :switch_data
    const/4 v0, 0x3
    goto :return
    :case
    const/4 v0, 0x7
    :return
    return v0
    :switch_data
    .sparse-switch
        0x1 -> :case
    .end sparse-switch
.end method

.method public static arrayPayload()[B
    .locals 2
    const/4 v0, 0x2
    new-array v1, v0, [B
    fill-array-data v1, :array_data
    return-object v1
    :array_data
    .array-data 1
        0x4t
        0x5t
    .end array-data
.end method

.method public static unreachablePayloadOwner()V
    .locals 1
    goto :return
    sparse-switch v0, :switch_data
    :case
    nop
    :return
    return-void
    :switch_data
    .sparse-switch
        0x1 -> :case
    .end sparse-switch
.end method

.method public static unreachableTargetAndHandler()V
    .locals 1
    goto :return
    :try_start
    if-eqz v0, :target
    throw v0
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    :target
    const/4 v0, 0x1
    goto :return
    :handler
    move-exception v0
    :return
    return-void
.end method

.method public static unreachableLoopGraph()V
    .locals 1
    goto :return
    :loop
    if-eqz v0, :loop
    :return
    return-void
.end method
