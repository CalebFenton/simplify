.class public Lreflective_invocation;
.super Ljava/lang/Object;

.method public static textToString()Ljava/lang/Object;
    .registers 5
    const-string v3, "toString"
    const-string v4, "text"
    const-class v0, Ljava/lang/String;
    const/4 v1, 0x0
    new-array v2, v1, [Ljava/lang/Class;
    invoke-virtual {v0, v3, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    invoke-virtual {v0, v4, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3
    return-object v3
.end method

.method public static textLength()I
    .registers 5
    const-string v3, "length"
    const-string v4, "text"
    const-class v0, Ljava/lang/String;
    const/4 v1, 0x0
    new-array v2, v1, [Ljava/lang/Class;
    invoke-virtual {v0, v3, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    invoke-virtual {v0, v4, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3
    check-cast v3, Ljava/lang/Integer;
    invoke-virtual {v3}, Ljava/lang/Integer;->intValue()I
    move-result v3
    return v3
.end method

.method public static nullReceiverLength()Ljava/lang/Object;
    .registers 5
    const-string v3, "length"
    const/4 v4, 0x0
    const-class v0, Ljava/lang/String;
    const/4 v1, 0x0
    new-array v2, v1, [Ljava/lang/Class;
    invoke-virtual {v0, v3, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    invoke-virtual {v0, v4, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3
    return-object v3
.end method

.method public static staticInterface()Ljava/lang/Object;
    .registers 4
    const-class v0, Ljava/util/Comparator;
    const-string v1, "naturalOrder"
    const/4 v2, 0x0
    new-array v3, v2, [Ljava/lang/Class;
    invoke-virtual {v0, v1, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    invoke-virtual {v0, v2, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3
    return-object v3
.end method

.method public static throwingTarget()Ljava/lang/Object;
    .registers 5
    const-class v0, Ljava/lang/Integer;
    const-string v1, "parseInt"
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Class;
    const/4 v3, 0x0
    const-class v4, Ljava/lang/String;
    aput-object v4, v2, v3
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Object;
    const-string v4, "not an integer"
    aput-object v4, v2, v3
    invoke-virtual {v0, v3, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v4
    return-object v4
.end method

.method public static caughtThrowingTarget()Ljava/lang/Object;
    .registers 5
    const-class v0, Ljava/lang/Integer;
    const-string v1, "parseInt"
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Class;
    const/4 v3, 0x0
    const-class v4, Ljava/lang/String;
    aput-object v4, v2, v3
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Object;
    const-string v4, "not an integer"
    aput-object v4, v2, v3
    :start
    invoke-virtual {v0, v3, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v4
    :end
    return-object v4
    .catch Ljava/lang/reflect/InvocationTargetException; {:start .. :end} :caught
    :caught
    move-exception v4
    return-object v4
.end method

.method public static wrongArgumentCount()Ljava/lang/Object;
    .registers 5
    const-class v0, Ljava/lang/String;
    const-string v1, "length"
    const/4 v2, 0x0
    new-array v3, v2, [Ljava/lang/Class;
    invoke-virtual {v0, v1, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v2, 0x1
    new-array v3, v2, [Ljava/lang/Object;
    const-string v1, "text"
    invoke-virtual {v0, v1, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v4
    return-object v4
.end method

.method private static addInts(II)I
    .registers 3
    add-int v0, p0, p1
    return v0
.end method

.method private static widenToLong(J)J
    .registers 2
    return-wide p0
.end method

.method public static applicationIntegerArguments()Ljava/lang/Object;
    .registers 5
    const-class v0, Lreflective_invocation;
    const-string v1, "addInts"
    const/4 v2, 0x2
    new-array v2, v2, [Ljava/lang/Class;
    sget-object v3, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    const/4 v4, 0x0
    aput-object v3, v2, v4
    const/4 v4, 0x1
    aput-object v3, v2, v4
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v2, 0x2
    new-array v2, v2, [Ljava/lang/Object;
    const/16 v3, 0x28
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    const/4 v4, 0x0
    aput-object v3, v2, v4
    const/4 v3, 0x2
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    const/4 v4, 0x1
    aput-object v3, v2, v4
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static applicationWidenedArgument()Ljava/lang/Object;
    .registers 5
    const-class v0, Lreflective_invocation;
    const-string v1, "widenToLong"
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Class;
    sget-object v3, Ljava/lang/Long;->TYPE:Ljava/lang/Class;
    const/4 v4, 0x0
    aput-object v3, v2, v4
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v2, 0x1
    new-array v2, v2, [Ljava/lang/Object;
    const/4 v3, 0x5
    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    aput-object v3, v2, v4
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
