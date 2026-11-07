# A3 and A11 original-versus-rewritten Tier A witness. Expected: java.lang.Integer(3342).
.class public LFx_DeadRemovalRemediation;
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
    invoke-static {p0}, LFx_DeadRemovalRemediation;->mixedReturnOrThrow(I)I
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
    sget v0, LFx_DeadRemovalRemediation;->mutableBranch:I
    if-eqz v0, :zero
    return p0
    :zero
    const/4 p0, 0x0
    return p0
.end method

.method private static a3Normal()I
    .locals 1
    const/4 v0, 0x0
    invoke-static {v0}, LFx_DeadRemovalRemediation;->mixedCallMustRemain(I)I
    move-result v0
    return v0
.end method

.method private static a3ThrowCaught()I
    .locals 1
    :try_start
    const/4 v0, 0x1
    invoke-static {v0}, LFx_DeadRemovalRemediation;->mixedCallMustRemain(I)I
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :caught
    const/4 v0, -0x1
    return v0
    :caught
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method private static a3AssignmentNormal()I
    .locals 2
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    invoke-static {v0, v1}, LFx_DeadRemovalRemediation;->deadArrayReadMustRemain([II)I
    move-result v0
    return v0
.end method

.method private static a3AssignmentThrowCaught()I
    .locals 2
    :try_start
    const/4 v0, 0x0
    const/4 v1, 0x0
    invoke-static {v0, v1}, LFx_DeadRemovalRemediation;->deadArrayReadMustRemain([II)I
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :caught
    const/4 v0, -0x1
    return v0
    :caught
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method private static a11Zero()I
    .locals 1
    const/4 v0, 0x0
    sput v0, LFx_DeadRemovalRemediation;->mutableBranch:I
    const/4 v0, 0x5
    invoke-static {v0}, LFx_DeadRemovalRemediation;->reachedDefinitionUsedOnlyByMutableBranch(I)I
    move-result v0
    return v0
.end method

.method private static a11One()I
    .locals 1
    const/4 v0, 0x1
    sput v0, LFx_DeadRemovalRemediation;->mutableBranch:I
    const/4 v0, 0x5
    invoke-static {v0}, LFx_DeadRemovalRemediation;->reachedDefinitionUsedOnlyByMutableBranch(I)I
    move-result v0
    return v0
.end method

.method public static run()Ljava/lang/Object;
    .locals 2
    invoke-static {}, LFx_DeadRemovalRemediation;->a3Normal()I
    move-result v0
    const/4 v1, 0x3
    if-ne v0, v1, :a3_normal_failure

    invoke-static {}, LFx_DeadRemovalRemediation;->a3ThrowCaught()I
    move-result v0
    const/4 v1, 0x1
    if-ne v0, v1, :a3_throw_failure

    invoke-static {}, LFx_DeadRemovalRemediation;->a3AssignmentNormal()I
    move-result v0
    const/4 v1, 0x3
    if-ne v0, v1, :a3_assignment_normal_failure

    invoke-static {}, LFx_DeadRemovalRemediation;->a3AssignmentThrowCaught()I
    move-result v0
    const/4 v1, 0x1
    if-ne v0, v1, :a3_assignment_throw_failure

    invoke-static {}, LFx_DeadRemovalRemediation;->a11Zero()I
    move-result v0
    if-nez v0, :a11_zero_failure

    invoke-static {}, LFx_DeadRemovalRemediation;->a11One()I
    move-result v0
    const/4 v1, 0x7
    if-ne v0, v1, :a11_one_failure

    const/16 v0, 0xd0e
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0

    :a3_normal_failure
    const/4 v0, -0x1
    goto :box_failure
    :a3_throw_failure
    const/4 v0, -0x2
    goto :box_failure
    :a3_assignment_normal_failure
    const/4 v0, -0x5
    goto :box_failure
    :a3_assignment_throw_failure
    const/4 v0, -0x6
    goto :box_failure
    :a11_zero_failure
    const/4 v0, -0x3
    goto :box_failure
    :a11_one_failure
    const/4 v0, -0x4
    :box_failure
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method
