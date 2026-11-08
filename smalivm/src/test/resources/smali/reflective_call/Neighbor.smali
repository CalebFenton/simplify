.class public Lreflective_call/Neighbor;
.super Ljava/lang/Object;

.method public static invokePackageMethod()Ljava/lang/Object;
    .locals 2
    const-string v0, "packageValue"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokePrivateMethod()Ljava/lang/Object;
    .locals 2
    const-string v0, "nothing"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
