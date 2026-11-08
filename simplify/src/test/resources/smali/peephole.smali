.class public Lpeephole;
.super Ljava/lang/Object;

.method public static taken()I
    .registers 1
    const/4 v0, 0x0
    if-eqz v0, :done
    const/4 v0, 0x1
    :done
    return v0
.end method

.method public static fallthrough()I
    .registers 1
    const/4 v0, 0x1
    if-eqz v0, :done
    const/4 v0, 0x2
    :done
    return v0
.end method

.method public static ownClass()Ljava/lang/Class;
    .registers 2
    const-string v0, "peephole"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

.method public static otherClass()Ljava/lang/Class;
    .registers 2
    const-string v0, "java.lang.String"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

.method public static cast(Ljava/lang/String;)Ljava/lang/String;
    .registers 1
    check-cast p0, Ljava/lang/String;
    return-object p0
.end method

.method public static activeCast(Ljava/lang/Object;)Ljava/lang/String;
    .registers 1
    check-cast p0, Ljava/lang/String;
    return-object p0
.end method

.method public getRuntimeClass()Ljava/lang/Class;
    .registers 2
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static stringIdentity()Ljava/lang/String;
    .registers 2
    new-instance v0, Ljava/lang/String;
    const-string v1, "identity"
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>(Ljava/lang/String;)V
    return-object v0
.end method

.method public static unknownPredicate(I)I
    .registers 2
    const/4 v0, 0x0
    if-eqz p0, :done
    const/4 v0, 0x1
    :done
    return v0
.end method
