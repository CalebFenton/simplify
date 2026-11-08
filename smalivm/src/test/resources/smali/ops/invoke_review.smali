.class public Linvoke_review;
.super Ljava/lang/Object;

.field public static committedInt:I
.field public static committedObject:Ljava/lang/Object;
.field public static committedThrowable:Ljava/lang/Throwable;

.method public static abortArmDeclaredFirst(I)I
    .locals 1
    invoke-static {p0}, Linvoke_review_callee;->loopOrReturnDeclaredFirst(I)I
    move-result v0
    return v0
.end method

.method public static returnArmDeclaredFirst(I)I
    .locals 1
    invoke-static {p0}, Linvoke_review_callee;->returnOrLoopDeclaredFirst(I)I
    move-result v0
    return v0
.end method

.method public static nonzeroIntPassedAsReference()Ljava/lang/Object;
    .locals 1
    const/4 v0, 0x5
    invoke-static {v0}, Linvoke_review_callee;->identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static arityMismatchOpaque()I
    .locals 3
    const/4 v0, 0x1
    new-array v1, v0, [I
    const/4 v2, 0x5
    const/4 v0, 0x0
    aput v2, v1, v0
    invoke-static {v1}, Linvoke_review_callee;->seven()I
    move-result v2
    return v2
.end method

.method public static conditionalThenTrigger(I)Linvoke_review_identity;
    .locals 1
    invoke-static {p0}, Linvoke_review_callee;->conditionallyInitialize(I)V
    sget-object v0, Linvoke_review_init_target;->identity:Linvoke_review_identity;
    return-object v0
.end method

.method public static readAbortedClinit()I
    .locals 1
    sget v0, Linvoke_review_abort_clinit;->value:I
    return v0
.end method

.method public static invokeInheritedClinit()V
    .locals 0
    invoke-static {}, Linvoke_review_clinit_child;-><clinit>()V
    return-void
.end method

.method public static callInterfaceDefault()I
    .locals 1
    new-instance v0, Linvoke_review_default_impl;
    invoke-direct {v0}, Linvoke_review_default_impl;-><init>()V
    invoke-virtual {v0}, Linvoke_review_default_impl;->callDefault()I
    move-result v0
    return v0
.end method

.method public static malformedResumeInner()V
    .locals 0
    invoke-static {}, Linvoke_review_callee;->returnsVoid()V
.end method

.method public static nestedMalformedResume()I
    .locals 1
    invoke-static {}, Linvoke_review;->malformedResumeInner()V
    const/4 v0, 0x1
    return v0
.end method

.method public static innerWithHandler()V
    .locals 1
    :try_start
    invoke-static {}, Linvoke_review_callee;->throwsNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-void
    :handler
    move-exception v0
    return-void
.end method

.method public static nestedMalformedHandler()I
    .locals 1
    invoke-static {}, Linvoke_review;->innerWithHandler()V
    const/4 v0, 0x2
    return v0
.end method

.method public static malformedResultInner()I
    .locals 0
    invoke-static {}, Linvoke_review_callee;->seven()I
.end method

.method public static nestedMalformedResult()I
    .locals 1
    invoke-static {}, Linvoke_review;->malformedResultInner()I
    move-result v0
    return v0
.end method

.method public static malformedConstructorInner()V
    .locals 1
    new-instance v0, Linvoke_ctor_target;
    invoke-direct {v0}, Linvoke_ctor_target;-><init>()V
.end method

.method public static nestedMalformedConstructor()I
    .locals 1
    invoke-static {}, Linvoke_review;->malformedConstructorInner()V
    const/4 v0, 0x3
    return v0
.end method

.method public static committedResultInner()V
    .locals 1
    invoke-static {}, Linvoke_review_callee;->seven()I
    move-result v0
    sput v0, Linvoke_review;->committedInt:I
    invoke-static {}, Linvoke_review_callee;->returnsVoid()V
.end method

.method public static readCommittedResultAfterMalformedResume()I
    .locals 1
    invoke-static {}, Linvoke_review;->committedResultInner()V
    sget v0, Linvoke_review;->committedInt:I
    return v0
.end method

.method public static committedCaughtInner()V
    .locals 1
    :try_start
    invoke-static {}, Linvoke_review_callee;->throwsNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-void
    :handler
    move-exception v0
    sput-object v0, Linvoke_review;->committedThrowable:Ljava/lang/Throwable;
    invoke-static {}, Linvoke_review_callee;->returnsVoid()V
.end method

.method public static readCommittedCaughtAfterMalformedResume()Ljava/lang/Throwable;
    .locals 1
    invoke-static {}, Linvoke_review;->committedCaughtInner()V
    sget-object v0, Linvoke_review;->committedThrowable:Ljava/lang/Throwable;
    return-object v0
.end method

.method public static committedConstructorInner()V
    .locals 1
    new-instance v0, Linvoke_ctor_target;
    sput-object v0, Linvoke_review;->committedObject:Ljava/lang/Object;
    invoke-direct {v0}, Linvoke_ctor_target;-><init>()V
.end method

.method public static readCommittedConstructorAfterMalformedResume()Ljava/lang/Object;
    .locals 1
    invoke-static {}, Linvoke_review;->committedConstructorInner()V
    sget-object v0, Linvoke_review;->committedObject:Ljava/lang/Object;
    return-object v0
.end method

.method public static callOpaqueArrayResult()[Linvoke_review_identity;
    .locals 1
    invoke-static {}, Linvoke_review_callee;->opaqueArrayResult()[Linvoke_review_identity;
    move-result-object v0
    return-object v0
.end method

.method public static callOpaqueClassResult()Linvoke_review_identity;
    .locals 1
    invoke-static {}, Linvoke_review_callee;->opaqueClassResult()Linvoke_review_identity;
    move-result-object v0
    return-object v0
.end method

.method public static innerWithTwoHandlers()I
    .locals 2
    :try_start
    invoke-static {}, Linvoke_review_callee;->throwsNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :npe
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    const/4 v0, 0x0
    return v0
    :npe
    move-exception v1
    const/4 v0, 0x1
    return v0
    :runtime
    move-exception v1
    const/4 v0, 0x2
    return v0
.end method

.method public static nestedTwoHandlers()I
    .locals 1
    invoke-static {}, Linvoke_review;->innerWithTwoHandlers()I
    move-result v0
    return v0
.end method

.method public static incompleteWorld(Linvoke_unique_base;)I
    .locals 0
    invoke-virtual {p0}, Linvoke_unique_base;->value()I
    move-result p0
    return p0
.end method

.method public static initializeOneOfTwoThroughAbortFallback()Linvoke_abort_ctor;
    .locals 3
    new-instance v0, Linvoke_abort_ctor;
    move-object v1, v0
    new-instance v2, Linvoke_abort_ctor;
    invoke-direct {v0}, Linvoke_abort_ctor;-><init>()V
    return-object v1
.end method

# Catches the outer constructor's throw and returns the receiver it failed to construct.
.method public static leakFailedConstruction()Linvoke_ctor_outer;
    .locals 1
    new-instance v0, Linvoke_ctor_outer;
    :try_start
    invoke-direct {v0}, Linvoke_ctor_outer;-><init>()V
    :try_end
    .catchall {:try_start .. :try_end} :handler
    return-object v0
    :handler
    return-object v0
.end method
