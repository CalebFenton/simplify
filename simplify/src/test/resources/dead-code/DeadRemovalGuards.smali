# run() returns Integer(3342) when every guarded method keeps its normal and throwing behaviour,
# and a distinct negative code naming the first check that failed.
.class public LDeadRemovalGuards;
.super Ljava/lang/Object;

.field private static mutableBranch:I

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
    invoke-static {p0}, LDeadRemovalGuards;->mixedReturnOrThrow(I)I
    move-result v0
    const/4 v0, 0x3
    return v0
.end method

.method public static deadArrayReadMustRemain([II)I
    .locals 1
    aget v0, p0, p1
    const/4 v0, 0x3
    return v0
.end method

.method public static unusedClassForNameResultMustRemain()I
    .locals 1
    const-string v0, "java.lang.String"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    const/4 v0, 0x3
    return v0
.end method

.method public static reachedDefinitionUsedOnlyByMutableBranch(I)I
    .locals 1
    const/4 p0, 0x7
    sget v0, LDeadRemovalGuards;->mutableBranch:I
    if-eqz v0, :zero
    return p0
    :zero
    const/4 p0, 0x0
    return p0
.end method

.method private static mixedCallReturns()I
    .locals 1
    const/4 v0, 0x0
    invoke-static {v0}, LDeadRemovalGuards;->mixedCallMustRemain(I)I
    move-result v0
    return v0
.end method

.method private static mixedCallThrowCaught()I
    .locals 1
    :try_start
    const/4 v0, 0x1
    invoke-static {v0}, LDeadRemovalGuards;->mixedCallMustRemain(I)I
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :caught
    const/4 v0, -0x1
    return v0
    :caught
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method private static arrayReadReturns()I
    .locals 2
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    invoke-static {v0, v1}, LDeadRemovalGuards;->deadArrayReadMustRemain([II)I
    move-result v0
    return v0
.end method

.method private static arrayReadThrowCaught()I
    .locals 2
    :try_start
    const/4 v0, 0x0
    const/4 v1, 0x0
    invoke-static {v0, v1}, LDeadRemovalGuards;->deadArrayReadMustRemain([II)I
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :caught
    const/4 v0, -0x1
    return v0
    :caught
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method private static branchOnZero()I
    .locals 1
    const/4 v0, 0x0
    sput v0, LDeadRemovalGuards;->mutableBranch:I
    const/4 v0, 0x5
    invoke-static {v0}, LDeadRemovalGuards;->reachedDefinitionUsedOnlyByMutableBranch(I)I
    move-result v0
    return v0
.end method

.method private static branchOnOne()I
    .locals 1
    const/4 v0, 0x1
    sput v0, LDeadRemovalGuards;->mutableBranch:I
    const/4 v0, 0x5
    invoke-static {v0}, LDeadRemovalGuards;->reachedDefinitionUsedOnlyByMutableBranch(I)I
    move-result v0
    return v0
.end method

.method public static run()Ljava/lang/Object;
    .locals 2
    invoke-static {}, LDeadRemovalGuards;->mixedCallReturns()I
    move-result v0
    const/4 v1, 0x3
    if-ne v0, v1, :mixed_call_failure

    invoke-static {}, LDeadRemovalGuards;->mixedCallThrowCaught()I
    move-result v0
    const/4 v1, 0x1
    if-ne v0, v1, :mixed_call_throw_failure

    invoke-static {}, LDeadRemovalGuards;->arrayReadReturns()I
    move-result v0
    const/4 v1, 0x3
    if-ne v0, v1, :array_read_failure

    invoke-static {}, LDeadRemovalGuards;->arrayReadThrowCaught()I
    move-result v0
    const/4 v1, 0x1
    if-ne v0, v1, :array_read_throw_failure

    invoke-static {}, LDeadRemovalGuards;->branchOnZero()I
    move-result v0
    if-nez v0, :branch_zero_failure

    invoke-static {}, LDeadRemovalGuards;->branchOnOne()I
    move-result v0
    const/4 v1, 0x7
    if-ne v0, v1, :branch_one_failure

    const/16 v0, 0xd0e
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0

    :mixed_call_failure
    const/4 v0, -0x1
    goto :box_failure
    :mixed_call_throw_failure
    const/4 v0, -0x2
    goto :box_failure
    :array_read_failure
    const/4 v0, -0x5
    goto :box_failure
    :array_read_throw_failure
    const/4 v0, -0x6
    goto :box_failure
    :branch_zero_failure
    const/4 v0, -0x3
    goto :box_failure
    :branch_one_failure
    const/4 v0, -0x4
    :box_failure
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method
