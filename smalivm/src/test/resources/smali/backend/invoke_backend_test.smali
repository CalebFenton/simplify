.class public Linvoke_backend_test;
.super Ljava/lang/Object;

.method public static reflectedListMutation(Ljava/util/List;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1

    invoke-interface {p0, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    const/4 v0, 0x0
    invoke-interface {p0, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static primitiveZero(I)Ljava/lang/Object;
    .locals 1

    invoke-static {p0}, Lorg/cf/smalivm/core/BackendHostFixture;->boxBoolean(Z)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static appendLong(Ljava/lang/StringBuilder;J)Ljava/lang/StringBuilder;
    .locals 0

    invoke-virtual {p0, p1, p2}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;
    move-result-object p0
    return-object p0
.end method

.method public static mutateTrackedArgument(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;
    .locals 0

    invoke-static {p0}, Linvoke_backend_test;->appendLegacySuffix(Ljava/lang/StringBuilder;)V
    return-object p0
.end method

.method public static mutateThroughAlias(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;
    .locals 1

    move-object v0, p0
    invoke-static {p0}, Linvoke_backend_test;->appendLegacySuffix(Ljava/lang/StringBuilder;)V
    return-object v0
.end method

.method private static appendLegacySuffix(Ljava/lang/StringBuilder;)V
    .locals 1

    const-string v0, ", death as light as a feather"
    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    const/4 p0, 0x1
    return-void
.end method

.method public static mutateThirdLogicalParameter(JILjava/lang/StringBuilder;)Ljava/lang/StringBuilder;
    .locals 1

    move-object v0, p3
    invoke-static {p0, p1, p2, p3}, Lorg/cf/smalivm/core/BackendHostFixture;->mutateThird(JILjava/lang/StringBuilder;)V
    return-object v0
.end method

.method public static cloneArray([I)[I
    .locals 1

    invoke-virtual {p0}, [I->clone()Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static cloneArrayAsObject([I)[I
    .locals 1

    invoke-virtual {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static primitiveArrayComponent(Ljava/lang/Object;)Ljava/lang/Class;
    .locals 1

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v0
    invoke-virtual {v0}, Ljava/lang/Class;->getComponentType()Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static trackedRuntimeClass(Ljava/lang/Object;)Ljava/lang/Class;
    .locals 1

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static boxedZeroUsesReferenceBranches()I
    .locals 2

    invoke-static {}, Lorg/cf/smalivm/core/BackendHostFixture;->boxedZero()Ljava/lang/Number;
    move-result-object v0
    if-eqz v0, :bad
    packed-switch v0, :switch_data
    const/4 v1, 0x1
    return v1

    :bad
    const/4 v1, 0x0
    return v1

    :switch_data
    .packed-switch 0x0
        :bad
    .end packed-switch
.end method

.method public static arbitraryNumberUsesReferenceBranches()I
    .locals 2

    invoke-static {}, Lorg/cf/smalivm/core/BackendHostFixture;->arbitraryNumber()Ljava/lang/Number;
    move-result-object v0
    if-eqz v0, :bad
    packed-switch v0, :switch_data
    const/4 v1, 0x1
    return v1

    :bad
    const/4 v1, 0x0
    return v1

    :switch_data
    .packed-switch 0x0
        :bad
    .end packed-switch
.end method

.method public static runtimeHostTypeControlsArrayStore()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x1
    new-array v1, v0, [Ljava/lang/CharSequence;
    invoke-static {}, Lorg/cf/smalivm/core/BackendHostFixture;->builderAsObject()Ljava/lang/Object;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    aget-object v0, v1, v3
    return-object v0
.end method

.method public static opaqueThenAuthorizedLength(Ljava/lang/Object;Ljava/lang/StringBuilder;)I
    .locals 1

    invoke-static {p0}, Lorg/cf/smalivm/core/BackendHostFixture;->opaque(Ljava/lang/Object;)V
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->length()I
    move-result v0
    return v0
.end method

.method public static deniedThreadLocal(Ljava/lang/ThreadLocal;Ljava/lang/Object;)V
    .locals 0

    invoke-virtual {p0, p1}, Ljava/lang/ThreadLocal;->set(Ljava/lang/Object;)V
    return-void
.end method

.method public static deniedLocale(Ljava/util/Locale;)V
    .locals 0

    invoke-static {p0}, Ljava/util/Locale;->setDefault(Ljava/util/Locale;)V
    return-void
.end method

.method public static deniedUrl(Ljava/net/URL;)V
    .locals 1

    invoke-virtual {p0}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;
    move-result-object v0
    return-void
.end method

.method public static deniedMapCompute(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/BiFunction;)V
    .locals 1

    invoke-interface {p0, p1, p2}, Ljava/util/Map;->compute(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;
    move-result-object v0
    return-void
.end method

.method public static deniedMapForEach(Ljava/util/Map;Ljava/util/function/BiConsumer;)V
    .locals 0

    invoke-interface {p0, p1}, Ljava/util/Map;->forEach(Ljava/util/function/BiConsumer;)V
    return-void
.end method

.method public static opaqueSubListClearThenParentSize(Ljava/util/List;Ljava/util/List;)I
    .locals 1

    invoke-interface {p0}, Ljava/util/List;->clear()V
    invoke-interface {p1}, Ljava/util/List;->size()I
    move-result v0
    return v0
.end method

.method public static opaqueMapViewClearThenParentSize(Ljava/util/Collection;Ljava/util/Map;)I
    .locals 1

    invoke-interface {p0}, Ljava/util/Collection;->clear()V
    invoke-interface {p1}, Ljava/util/Map;->size()I
    move-result v0
    return v0
.end method

.method public static deniedUuidRandom()V
    .locals 1

    invoke-static {}, Ljava/util/UUID;->randomUUID()Ljava/util/UUID;
    move-result-object v0
    return-void
.end method

.method public static deniedRandomConstructor()V
    .locals 1

    new-instance v0, Ljava/util/Random;
    invoke-direct {v0}, Ljava/util/Random;-><init>()V
    return-void
.end method

.method public static overwriteReturnedHosts()I
    .locals 2

    const/4 v1, 0x1
    invoke-static {v1}, Lorg/cf/smalivm/core/BackendHostFixture;->freshBuilder(I)Ljava/lang/StringBuilder;
    move-result-object v0

    const/16 v1, 0x16
    invoke-static {v1}, Lorg/cf/smalivm/core/BackendHostFixture;->freshBuilder(I)Ljava/lang/StringBuilder;
    move-result-object v0

    const/16 v1, 0x14d
    invoke-static {v1}, Lorg/cf/smalivm/core/BackendHostFixture;->freshBuilder(I)Ljava/lang/StringBuilder;
    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->length()I
    move-result v1
    return v1
.end method

.method public static dirtyArrayNeedsInit([I)I
    .locals 1

    invoke-static {p0}, Lorg/cf/smalivm/core/BackendHostFixture;->arrayNeedsInit([I)I
    move-result v0
    return v0
.end method

.method public static guestHostLength(Ljava/lang/StringBuilder;)V
    .locals 1

    invoke-virtual {p0}, Ljava/lang/StringBuilder;->length()I
    move-result v0
    return-void
.end method

.method public static suspendedCallerKeepsOtherRoot(Ljava/lang/StringBuilder;Ljava/lang/StringBuilder;)I
    .locals 1

    invoke-static {p0}, Linvoke_backend_test;->guestHostLength(Ljava/lang/StringBuilder;)V
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->length()I
    move-result v0
    return v0
.end method

.method public static siblingCallersKeepIndependentRoots(ILjava/lang/StringBuilder;Ljava/lang/StringBuilder;)I
    .locals 1

    if-eqz p0, :use_first
    invoke-static {p1}, Linvoke_backend_test;->guestHostLength(Ljava/lang/StringBuilder;)V
    invoke-virtual {p2}, Ljava/lang/StringBuilder;->length()I
    move-result v0
    return v0

    :use_first
    invoke-static {p2}, Linvoke_backend_test;->guestHostLength(Ljava/lang/StringBuilder;)V
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->length()I
    move-result v0
    return v0
.end method

.method public static siblingBranchLocalRoot(ILjava/lang/StringBuilder;)I
    .locals 2

    if-eqz p0, :without_local

    const/16 v1, 0x309
    invoke-static {v1}, Lorg/cf/smalivm/core/BackendHostFixture;->freshBuilder(I)Ljava/lang/StringBuilder;
    move-result-object v0
    invoke-static {p1}, Linvoke_backend_test;->guestHostLength(Ljava/lang/StringBuilder;)V
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->length()I
    move-result v1
    return v1

    :without_local
    invoke-static {p1}, Linvoke_backend_test;->guestHostLength(Ljava/lang/StringBuilder;)V
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->length()I
    move-result v1
    return v1
.end method

.method public static opaqueEntrySetValueThenMapGet(Ljava/util/Map$Entry;Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1

    invoke-static {p0, p3}, Lorg/cf/smalivm/core/BackendHostFixture;->opaqueEntrySetValue(Ljava/util/Map$Entry;Ljava/lang/Object;)V
    invoke-interface {p1, p2}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static deadIncompleteThenStringLength(Ljava/lang/String;)I
    .locals 1

    invoke-virtual {p0}, Ljava/lang/String;->length()I
    move-result v0
    return v0
.end method

.method public static liveIncompleteThenStringLength(Ljava/lang/Object;Ljava/lang/String;)I
    .locals 1

    invoke-virtual {p1}, Ljava/lang/String;->length()I
    move-result v0
    return v0
.end method
