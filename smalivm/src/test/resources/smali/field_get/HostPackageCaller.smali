.class public Lorg/cf/smalivm/core/HostPackageCaller;
.super Ljava/lang/Object;

.method public static dummyMethod()V
    .locals 0
    return-void
.end method

.method public static readHostMutable(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 3
    const-class v0, Lorg/cf/smalivm/core/HostFieldFixture;
    const-string v1, "mutable"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    invoke-virtual {v0, p0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
