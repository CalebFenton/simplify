.class public Lreflective_call/other/Outsider;
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

.method public static invokePublicMethod()Ljava/lang/Object;
    .locals 3
    const-string v0, "value"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    new-instance v1, Lreflective_call/Target;
    invoke-direct {v1}, Lreflective_call/Target;-><init>()V
    const/4 v2, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeAfterSetAccessible()Ljava/lang/Object;
    .locals 2
    const-string v0, "packageValue"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x1
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
