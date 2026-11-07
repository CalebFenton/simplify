# A2 direct-guard original-versus-rewritten Tier A witness. Expected: java.lang.Integer(162).
.class public LFx_ConstantPropagationGuards;
.super Ljava/lang/Object;

.field public static initTrigger:I
.field public static volatile volatileValue:I = 0x7

.method public static mixedNormalThrowDivision(I)I
    .locals 2
    if-eqz p0, :zero
    const/4 v1, 0x1
    goto :divide
    :zero
    const/4 v1, 0x0
    :divide
    const/4 v0, 0x0
    div-int v0, v0, v1
    return v0
.end method

.method public static knownVolatileRead()I
    .locals 1
    const/4 v0, 0x0
    sput v0, LFx_ConstantPropagationGuards;->initTrigger:I
    sget v0, LFx_ConstantPropagationGuards;->volatileValue:I
    return v0
.end method

.method public static classForNameMoveResultMustRemain()Ljava/lang/Class;
    .locals 2
    const-string v0, "java.lang.String"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

.method private static normalBehavior()I
    .locals 1
    const/4 v0, 0x1
    invoke-static {v0}, LFx_ConstantPropagationGuards;->mixedNormalThrowDivision(I)I
    move-result v0
    return v0
.end method

.method private static throwingBehavior()I
    .locals 1
    :try_start
    const/4 v0, 0x0
    invoke-static {v0}, LFx_ConstantPropagationGuards;->mixedNormalThrowDivision(I)I
    move-result v0
    :try_end
    .catch Ljava/lang/ArithmeticException; {:try_start .. :try_end} :caught
    return v0
    :caught
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static run()Ljava/lang/Object;
    .locals 2
    invoke-static {}, LFx_ConstantPropagationGuards;->normalBehavior()I
    move-result v0
    if-nez v0, :normal_failure

    invoke-static {}, LFx_ConstantPropagationGuards;->throwingBehavior()I
    move-result v0
    const/4 v1, 0x1
    if-ne v0, v1, :throw_failure

    const/16 v0, 0xa2
    goto :box

    :normal_failure
    const/4 v0, -0x1
    goto :box

    :throw_failure
    const/4 v0, -0x2

    :box
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method
