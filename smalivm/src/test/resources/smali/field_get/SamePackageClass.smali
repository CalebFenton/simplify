.class public Lorg/cf/smalivm/SamePackageClass;
.super Ljava/lang/Object;

.method public static dummyMethod()V
    .locals 0
    return-void
.end method

.method public static readProtectedInstance()I
    .locals 4
    new-instance v0, Lorg/cf/smalivm/DummyClass;
    invoke-direct {v0}, Lorg/cf/smalivm/DummyClass;-><init>()V
    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "protectedInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1
    invoke-virtual {v1, v0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static matrix(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 0
    const/4 p0, 0x0
    return p0
.end method
