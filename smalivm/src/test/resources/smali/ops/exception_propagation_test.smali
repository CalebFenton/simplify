.class public Lexception_propagation_test;
.super Ljava/lang/Object;

.field public value:I
.field public static state:I
.field public static saved:Ljava/lang/Throwable;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static throwNpe()V
    .locals 1
    const/4 v0, 0x0
    throw v0
.end method

.method public static localCatch()I
    .locals 2
    :try_start
    const/4 v0, 0x0
    throw v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    :handler
    move-exception v1
    const/4 v0, 0x1
    return v0
.end method

.method public static oneCaller()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x2
    return v0
.end method

.method public static middle()V
    .locals 0
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    return-void
.end method

.method public static outerThreeFrames()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->middle()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x3
    return v0
.end method

.method public static middleStops()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x4
    return v0
.end method

.method public static outerIntermediateStops()I
    .locals 1
    invoke-static {}, Lexception_propagation_test;->middleStops()I
    move-result v0
    return v0
.end method

.method public static callerRegisterSurvives()I
    .locals 2
    const/4 v0, 0x7
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v1
    return v0
.end method

.method public static callerAddressVisitsSurvive()I
    .locals 3
    const/4 v0, 0x0
    :visited
    add-int/lit8 v0, v0, 0x1
    const/4 v1, 0x1
    if-gt v0, v1, :return
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    :handler
    move-exception v2
    goto :visited
    :return
    return v0
.end method

.method public static writeObjectAndStaticThenThrow(Lexception_propagation_test;)V
    .locals 2
    const/4 v0, 0x5
    sput v0, Lexception_propagation_test;->state:I
    const/4 v0, 0x6
    iput v0, p0, Lexception_propagation_test;->value:I
    const/4 v1, 0x0
    throw v1
.end method

.method public static callerSeesPreThrowWrites()I
    .locals 4
    new-instance v0, Lexception_propagation_test;
    invoke-direct {v0}, Lexception_propagation_test;-><init>()V
    :try_start
    invoke-static {v0}, Lexception_propagation_test;->writeObjectAndStaticThenThrow(Lexception_propagation_test;)V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v1, 0x0
    return v1
    :handler
    move-exception v1
    iget v2, v0, Lexception_propagation_test;->value:I
    sget v3, Lexception_propagation_test;->state:I
    add-int v2, v2, v3
    return v2
.end method

.method public static middleRethrows()V
    .locals 1
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-void
    :handler
    move-exception v0
    sput-object v0, Lexception_propagation_test;->saved:Ljava/lang/Throwable;
    throw v0
.end method

.method public static outerReturnsRethrown()Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {}, Lexception_propagation_test;->middleRethrows()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static invokeOnlyRange()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/16 v0, 0x8
    return v0
.end method

.method public static resumeOnlyRange()I
    .locals 2
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_start
    const/4 v0, 0x0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v1
    const/16 v0, 0x9
    return v0
.end method

.method public static returnOrThrow(I)I
    .locals 1
    if-eqz p0, :return
    const/4 v0, 0x0
    throw v0
    :return
    const/4 v0, 0x1
    return v0
.end method

.method public static callerKeepsEveryEnding(I)I
    .locals 2
    :try_start
    invoke-static {p0}, Lexception_propagation_test;->returnOrThrow(I)I
    move-result v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x2
    return v0
.end method

.method public static twoReturns(I)I
    .locals 1
    if-eqz p0, :one
    const/4 v0, 0x2
    return v0
    :one
    const/4 v0, 0x1
    return v0
.end method

.method public static callerKeepsBothReturns(I)I
    .locals 1
    invoke-static {p0}, Lexception_propagation_test;->twoReturns(I)I
    move-result v0
    return v0
.end method

.method public static sameRefWithDifferentClassState(ILjava/lang/Throwable;)V
    .locals 1
    if-eqz p0, :throw
    sget-object v0, Linvoke_review_init_target;->identity:Linvoke_review_identity;
    :throw
    throw p1
.end method

.method public static callerKeepsSameRefStates(ILjava/lang/Throwable;)Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {p0, p1}, Lexception_propagation_test;->sameRefWithDifferentClassState(ILjava/lang/Throwable;)V
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static sameRefWithVisibleState(ILexception_propagation_test;Ljava/lang/Throwable;)V
    .locals 2
    if-eqz p0, :second
    const/4 v0, 0x1
    sput v0, Lexception_propagation_test;->state:I
    iput v0, p1, Lexception_propagation_test;->value:I
    sget-object v1, Linvoke_review_init_target;->identity:Linvoke_review_identity;
    goto :throw
    :second
    const/4 v0, 0x2
    sput v0, Lexception_propagation_test;->state:I
    iput v0, p1, Lexception_propagation_test;->value:I
    :throw
    throw p2
.end method

.method public static callerObservesSameRefStates(ILexception_propagation_test;Ljava/lang/Throwable;)I
    .locals 3
    :try_start
    invoke-static {p0, p1, p2}, Lexception_propagation_test;->sameRefWithVisibleState(ILexception_propagation_test;Ljava/lang/Throwable;)V
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    sput-object v0, Lexception_propagation_test;->saved:Ljava/lang/Throwable;
    iget v1, p1, Lexception_propagation_test;->value:I
    sget v2, Lexception_propagation_test;->state:I
    add-int v1, v1, v2
    return v1
.end method

.method public static twoDistinctNpes(I)V
    .locals 1
    if-eqz p0, :second
    const/4 v0, 0x0
    throw v0
    :second
    const/4 v0, 0x0
    throw v0
.end method

.method public static callerReturnsDistinctNpes(I)Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {p0}, Lexception_propagation_test;->twoDistinctNpes(I)V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static fourEndings(II)I
    .locals 1
    if-eqz p0, :left
    if-eqz p1, :return_one
    const/4 v0, 0x3
    sput v0, Lexception_propagation_test;->state:I
    const/4 v0, 0x0
    throw v0
    :return_one
    const/4 v0, 0x1
    return v0
    :left
    if-eqz p1, :return_two
    const/4 v0, 0x4
    sput v0, Lexception_propagation_test;->state:I
    const/4 v0, 0x0
    throw v0
    :return_two
    const/4 v0, 0x2
    return v0
.end method

.method public static callerKeepsFourEndings(II)I
    .locals 2
    :try_start
    invoke-static {p0, p1}, Lexception_propagation_test;->fourEndings(II)I
    move-result v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v1
    sget v0, Lexception_propagation_test;->state:I
    return v0
.end method

.method public static recursiveDistinct(I)Ljava/lang/Throwable;
    .locals 3
    if-nez p0, :recurse
    const/4 v0, 0x0
    throw v0
    :recurse
    add-int/lit8 v0, p0, -0x1
    :child_start
    invoke-static {v0}, Lexception_propagation_test;->recursiveDistinct(I)Ljava/lang/Throwable;
    move-result-object v0
    :child_end
    .catch Ljava/lang/NullPointerException; {:child_start .. :child_end} :child_handler
    return-object v0
    :child_handler
    move-exception v0
    const/4 v1, 0x1
    if-ne p0, v1, :return_caught
    :fresh_start
    const/4 v2, 0x0
    throw v2
    :fresh_end
    .catch Ljava/lang/NullPointerException; {:fresh_start .. :fresh_end} :fresh_handler
    :fresh_handler
    move-exception v0
    sput-object v0, Lexception_propagation_test;->saved:Ljava/lang/Throwable;
    throw v0
    :return_caught
    return-object v0
.end method

.method public static recursionWitness()Ljava/lang/Throwable;
    .locals 1
    const/4 v0, 0x2
    invoke-static {v0}, Lexception_propagation_test;->recursiveDistinct(I)Ljava/lang/Throwable;
    move-result-object v0
    return-object v0
.end method

.method public static recursiveLeaf(II)Ljava/lang/Throwable;
    .locals 2
    if-lez p1, :throw
    add-int/lit8 v0, p1, -0x1
    :try_start
    invoke-static {p0, v0}, Lexception_propagation_test;->recursiveLeaf(II)Ljava/lang/Throwable;
    move-result-object v1
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-object v1
    :handler
    move-exception v1
    return-object v1
    :throw
    const/4 v0, 0x0
    throw v0
.end method

.method public static recursiveFork(I)Ljava/lang/Throwable;
    .locals 3
    if-eqz p0, :first
    const/4 v0, 0x2
    goto :call
    :first
    const/4 v0, 0x1
    :call
    const/4 v1, 0x1
    invoke-static {v0, v1}, Lexception_propagation_test;->recursiveLeaf(II)Ljava/lang/Throwable;
    move-result-object v2
    sput v0, Lexception_propagation_test;->state:I
    return-object v2
.end method

.method public static recursionMergeWitness(I)Ljava/lang/Throwable;
    .locals 1
    invoke-static {p0}, Lexception_propagation_test;->recursiveFork(I)Ljava/lang/Throwable;
    move-result-object v0
    return-object v0
.end method

.method public static callerCatchesThrowable()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x1
    return v0
.end method

.method public static callerReturnsSymbolicCaught()Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static partialCompletionCallee(I)I
    .locals 1
    nop
    :return
    const/4 v0, 0x7
    return v0
    :throw
    const/4 v0, 0x0
    throw v0
    :loop
    goto :loop
.end method

.method public static partialCompletionCaller(I)I
    .locals 2
    :try_start
    invoke-static {p0}, Lexception_propagation_test;->partialCompletionCallee(I)I
    move-result v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return v0
    :handler
    move-exception v1
    const/16 v0, 0x8
    return v0
.end method

.method public static effectLeaf([I)V
    .locals 2
    const/4 v0, 0x1
    const/4 v1, 0x0
    aput v0, p0, v1
    const/4 v0, 0x0
    throw v0
.end method

.method public static effectMiddle([I)V
    .locals 0
    invoke-static {p0}, Lexception_propagation_test;->effectLeaf([I)V
    return-void
.end method

.method public static effectOuter([I)I
    .locals 2
    :try_start
    invoke-static {p0}, Lexception_propagation_test;->effectMiddle([I)V
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x3
    return v0
.end method

.method public static manySymbolicHandlers()I
    .locals 2
    :try_start
    invoke-static {}, Lexception_propagation_test;->throwNpe()V
    :try_end
    .catch Lmissing/H00; {:try_start .. :try_end} :handler
    .catch Lmissing/H01; {:try_start .. :try_end} :handler
    .catch Lmissing/H02; {:try_start .. :try_end} :handler
    .catch Lmissing/H03; {:try_start .. :try_end} :handler
    .catch Lmissing/H04; {:try_start .. :try_end} :handler
    .catch Lmissing/H05; {:try_start .. :try_end} :handler
    .catch Lmissing/H06; {:try_start .. :try_end} :handler
    .catch Lmissing/H07; {:try_start .. :try_end} :handler
    .catch Lmissing/H08; {:try_start .. :try_end} :handler
    .catch Lmissing/H09; {:try_start .. :try_end} :handler
    .catch Lmissing/H10; {:try_start .. :try_end} :handler
    .catch Lmissing/H11; {:try_start .. :try_end} :handler
    .catch Lmissing/H12; {:try_start .. :try_end} :handler
    .catch Lmissing/H13; {:try_start .. :try_end} :handler
    .catch Lmissing/H14; {:try_start .. :try_end} :handler
    .catch Lmissing/H15; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v1
    const/4 v0, 0x1
    return v0
.end method

.method public static rethrowRequiredMissingCatch(Ljava/lang/Throwable;)V
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Lmissing/CallerPartition; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    throw v0
.end method

.method public static callerRoutesOpposingMissingCatchResiduals(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {p0}, Lexception_propagation_test;->rethrowRequiredMissingCatch(Ljava/lang/Throwable;)V
    :try_end
    .catch Lmissing/CallerPartition; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static throwArgument(Ljava/lang/Throwable;)V
    .locals 0
    throw p0
.end method

.method public static callerEscapesLooseTag(Ljava/lang/Throwable;)V
    .locals 0
    invoke-static {p0}, Lexception_propagation_test;->throwArgument(Ljava/lang/Throwable;)V
    return-void
.end method
