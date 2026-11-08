.class public Ltask65/ConstantIdentity;
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
    invoke-static {}, Ltask65/ConstantIdentity;->distinctString()Ljava/lang/String;
    move-result-object v0
    const-string v1, "identity witness"
    if-eq v0, v1, :aliased
    const/4 v0, 0x1
    return v0
    :aliased
    const/4 v0, 0x0
    return v0
.end method

.method public static hostString()Ljava/lang/String;
    .locals 1
    const v0, 0x75bcd15
    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
    move-result-object v0
    return-object v0
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
    return-object v1
    :done
    move-object v1, v0
    return-object v1
.end method

.method public static copyAtBranchJoin(I)Ljava/lang/String;
    .locals 2
    const-string v0, "branch literal"
    if-eqz p0, :join
    const-string v0, "branch literal"
    :join
    move-object v1, v0
    return-object v1
.end method
