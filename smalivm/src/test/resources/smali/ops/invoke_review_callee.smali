.class public Linvoke_review_callee;
.super Ljava/lang/Object;

.method public static loopOrReturnDeclaredFirst(I)I
    .locals 1
    if-eqz p0, :return
    :loop
    goto :loop
    :return
    const/4 v0, 0x7
    return v0
.end method

.method public static returnOrLoopDeclaredFirst(I)I
    .locals 1
    if-nez p0, :loop
    const/4 v0, 0x7
    return v0
    :loop
    goto :loop
.end method

.method public static identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0
    return-object p0
.end method

.method public static seven()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

.method public static conditionallyInitialize(I)V
    .locals 1
    if-eqz p0, :return
    sget-object v0, Linvoke_review_init_target;->identity:Linvoke_review_identity;
    :return
    return-void
.end method

.method public static returnsVoid()V
    .locals 0
    return-void
.end method

.method public static throwsNpe()V
    .locals 1
    const/4 v0, 0x0
    throw v0
.end method

.method public static bindingMatrix(ZBCSIJFDLjava/lang/Object;)V
    .locals 0
    return-void
.end method

.method public static native opaqueArrayResult()[Linvoke_review_identity;
.end method

.method public static native opaqueClassResult()Linvoke_review_identity;
.end method
