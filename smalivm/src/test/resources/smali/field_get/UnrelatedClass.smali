.class public Lorg/cf/smalivm/unrelated/UnrelatedClass;
.super Ljava/lang/Object;

.method public static dummyMethod()V
    .locals 0
    return-void
.end method

.method public static readPublicInstance()I
    .locals 4
    new-instance v0, Lorg/cf/smalivm/DummyClass;
    invoke-direct {v0}, Lorg/cf/smalivm/DummyClass;-><init>()V
    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "publicInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1
    invoke-virtual {v1, v0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static readPrivateInstanceDenied()I
    .locals 4
    new-instance v0, Lorg/cf/smalivm/DummyClass;
    invoke-direct {v0}, Lorg/cf/smalivm/DummyClass;-><init>()V
    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "privateInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1
    invoke-virtual {v1, v0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static readPrivateInstanceWithAliasOverride()I
    .locals 5
    new-instance v0, Lorg/cf/smalivm/DummyClass;
    invoke-direct {v0}, Lorg/cf/smalivm/DummyClass;-><init>()V
    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "privateInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1
    move-object v2, v1
    const/4 v3, 0x1
    invoke-virtual {v2, v3}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    invoke-virtual {v1, v0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static readPrivateStaticWithOverride()I
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x1
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static readInitializedStaticWithOverride()I
    .locals 4
    const-class v0, Lorg/cf/smalivm/StaticInitField;
    const-string v1, "value"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x1
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static duplicateLookupDoesNotShareOverride()I
    .locals 5
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v2
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v3
    const/4 v4, 0x1
    invoke-virtual {v2, v4}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    const/4 v4, 0x0
    invoke-virtual {v3, v4}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static branchOverride(I)I
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    if-eqz p0, :get
    const/4 v1, 0x1
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    :get
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method private static maybeSetAccessible(Ljava/lang/reflect/Field;I)Ljava/lang/reflect/Field;
    .locals 1
    if-eqz p1, :return
    const/4 v0, 0x1
    invoke-virtual {p0, v0}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    :return
    return-object p0
.end method

.method public static branchOverrideThroughCall(I)I
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    invoke-static {v0, p0}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->maybeSetAccessible(Ljava/lang/reflect/Field;I)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static readPublicInstanceFromNull()I
    .locals 3
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "publicInstanceField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static matrix(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 0
    const/4 p0, 0x0
    return p0
.end method

.method private static native opaqueOne(Ljava/lang/reflect/Field;)V
.end method

.method private static native opaqueAliases(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V
.end method

.method private static native opaqueTwo(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V
.end method

.method private static native opaqueObject(Ljava/lang/Object;)V
.end method

.method private static native opaqueMixed(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I
.end method

.method public static opaquePrivateStaticThenGet()I
    .locals 3
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    invoke-static {v0}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueOne(Ljava/lang/reflect/Field;)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaqueDuplicateAliasesThenGet()I
    .locals 3
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    move-object v1, v0
    invoke-static {v0, v1}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueAliases(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V
    nop
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaqueTwoFields()V
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const-class v1, Lorg/cf/smalivm/StaticInitField;
    const-string v2, "value"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1
    invoke-static {v0, v1}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueTwo(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V
    return-void
.end method

.method public static opaqueArrayWrappedFieldThenGet()I
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/reflect/Field;
    const/4 v2, 0x0
    aput-object v0, v1, v2
    invoke-static {v1}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueObject(Ljava/lang/Object;)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaqueObjectWrappedFieldThenGet()I
    .locals 4
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "privateStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    new-instance v1, Lorg/cf/smalivm/FieldBox;
    invoke-direct {v1}, Lorg/cf/smalivm/FieldBox;-><init>()V
    iput-object v0, v1, Lorg/cf/smalivm/FieldBox;->field:Ljava/lang/reflect/Field;
    invoke-static {v1}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueObject(Ljava/lang/Object;)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaquePublicStaticThenGet()I
    .locals 3
    const-class v0, Lorg/cf/smalivm/DummyClass;
    const-string v1, "publicStaticField"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    invoke-static {v0}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueOne(Ljava/lang/reflect/Field;)V
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaqueManyFieldsThenGet()I
    .locals 6
    const/16 v0, 0xc
    new-array v1, v0, [Ljava/lang/reflect/Field;
    const/4 v2, 0x0
    const-class v3, Lorg/cf/smalivm/DummyClass;
    const-string v4, "privateStaticField"
    :loop
    if-ge v2, v0, :call
    invoke-virtual {v3, v4}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v5
    aput-object v5, v1, v2
    add-int/lit8 v2, v2, 0x1
    goto :loop
    :call
    invoke-static {v1}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueObject(Ljava/lang/Object;)V
    const/4 v2, 0x0
    invoke-virtual {v5, v2}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result v0
    return v0
.end method

.method public static opaqueMixedCall(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I
    .locals 0
    invoke-static {p0, p1, p2, p3}, Lorg/cf/smalivm/unrelated/UnrelatedClass;->opaqueMixed(Ljava/lang/reflect/Field;[ILorg/cf/smalivm/DummyClass;Ljava/lang/StringBuilder;)I
    nop
    move-result p0
    return p0
.end method
