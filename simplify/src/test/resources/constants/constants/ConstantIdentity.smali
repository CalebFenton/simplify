.class public Lconstants/ConstantIdentity;
.super Ljava/lang/Object;

.method private static distinctString()Ljava/lang/String;
    .locals 2
    const-string v0, "identity witness"
    new-instance v1, Ljava/lang/String;
    invoke-direct {v1, v0}, Ljava/lang/String;-><init>(Ljava/lang/String;)V
    return-object v1
.end method

.method public static run()I
    .locals 2
    invoke-static {}, Lconstants/ConstantIdentity;->distinctString()Ljava/lang/String;
    move-result-object v0
    const-string v1, "identity witness"
    if-eq v0, v1, :aliased
    const/4 v0, 0x1
    return v0
    :aliased
    const/4 v0, 0x0
    return v0
.end method

.method public static passedAndReturned()Ljava/lang/String;
    .locals 2
    const v0, 0x75bcd15
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    invoke-virtual {v0}, Ljava/lang/String;->length()I
    move-result v1
    return-object v0
.end method

.method public static comparedOrPassed()I
    .locals 4
    const v0, 0x75bcd15
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v1
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v2
    invoke-virtual {v2}, Ljava/lang/String;->length()I
    move-result v3
    const-string v2, "123456789"
    if-eq v1, v2, :same
    return v3
    :same
    const/4 v3, 0x0
    return v3
.end method

.method public static lockedThroughCopyOrReturned()Ljava/lang/String;
    .locals 3
    const v0, 0x75bcd15
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v1
    move-object v2, v1
    monitor-enter v2
    monitor-exit v2
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static nullTested()Ljava/lang/String;
    .locals 1
    const v0, 0x75bcd15
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    if-eqz v0, :missing
    return-object v0
    :missing
    const-string v0, "missing"
    return-object v0
.end method

.method public static constructedFromBytes()Ljava/lang/String;
    .locals 2
    new-instance v0, Ljava/lang/String;
    const/4 v1, 0x2
    new-array v1, v1, [B
    fill-array-data v1, :bytes
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([B)V
    return-object v0

    :bytes
    .array-data 1
        0x6ft 0x6bt
    .end array-data
.end method

.method public static constructedAndCompared()I
    .locals 3
    new-instance v0, Ljava/lang/String;
    const/4 v1, 0x2
    new-array v1, v1, [B
    fill-array-data v1, :bytes
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([B)V
    const-string v2, "ok"
    if-eq v0, v2, :same
    const/4 v0, 0x1
    return v0
    :same
    const/4 v0, 0x0
    return v0

    :bytes
    .array-data 1
        0x6ft 0x6bt
    .end array-data
.end method

.method public static mergedLiteralFirst(I)Ljava/lang/String;
    .locals 3
    const-string v0, "123456789"
    if-eqz p0, :join
    const v1, 0x75bcd15
    invoke-static {v1}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    :join
    move-object v2, v0
    return-object v2
.end method

.method public static mergedFreshFirst(I)Ljava/lang/String;
    .locals 3
    const v1, 0x75bcd15
    invoke-static {v1}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    if-eqz p0, :join
    const-string v0, "123456789"
    :join
    move-object v2, v0
    return-object v2
.end method

.method public static linearCopies()Ljava/lang/String;
    .locals 4
    const-string v0, "linear literal"
    const/4 v3, 0x1
    move-object v1, v0
    const/4 v0, 0x2
    move-object v2, v1
    return-object v2
.end method

.method public static copyOnIfFallthrough(I)Ljava/lang/String;
    .locals 2
    const-string v0, "branch literal"
    if-eqz p0, :done
    move-object v1, v0
    if-ne v1, v0, :done
    :done
    return-object v0
.end method

.method public static copyAtBranchJoin(I)Ljava/lang/String;
    .locals 2
    const-string v0, "branch literal"
    if-eqz p0, :join
    const-string v0, "branch literal"
    :join
    move-object v1, v0
    if-ne v1, v0, :done
    :done
    return-object v1
.end method
