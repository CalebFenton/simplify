.class public Linvoke_task20;
.super Ljava/lang/Object;

.method public static staticReturn()I
    .locals 1
    invoke-static {}, Linvoke_task20;->seven()I
    move-result v0
    return v0
.end method

.method private static seven()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

.method public static zeroBecomesNull()Ljava/lang/Object;
    .locals 1
    const/4 v0, 0x0
    invoke-static {v0}, Linvoke_task20;->identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method private static identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0
    return-object p0
.end method

.method public static wideRange()J
    .locals 8
    const-wide/16 v0, 0x1
    const-wide/16 v2, 0x2
    const-wide/16 v4, 0x3
    const/4 v6, 0x4
    invoke-static/range {v0 .. v6}, Linvoke_task20;->lastWide(JJJI)J
    move-result-wide v0
    return-wide v0
.end method

.method private static lastWide(JJJI)J
    .locals 0
    return-wide p4
.end method

.method public static nullVirtual()I
    .locals 1
    const/4 v0, 0x0
    invoke-virtual {v0}, Ljava/lang/String;->length()I
    move-result v0
    return v0
.end method

.method public static missingBeforeNull()V
    .locals 1
    const/4 v0, 0x0
    invoke-virtual {v0}, Linvoke_task20;->missing()V
    return-void
.end method

.method public static absentClass()V
    .locals 0
    invoke-static {}, Linvoke_task20_absent;->missing()V
    return-void
.end method

.method public static opaqueArray()[I
    .locals 5
    const/4 v0, 0x1
    new-array v1, v0, [I
    const/4 v2, 0x0
    const/4 v3, 0x5
    aput v3, v1, v2
    invoke-static {v1, v2, v1, v2, v0}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V
    return-object v1
.end method

.method public static opaqueNested()Linvoke_nested_object;
    .locals 6
    new-instance v0, Linvoke_nested_object;
    new-instance v1, Linvoke_nested_object;
    move-object v2, v0
    iput-object v1, v0, Linvoke_nested_object;->child:Linvoke_nested_object;
    const/4 v3, 0x5
    iput v3, v1, Linvoke_nested_object;->value:I
    const/4 v4, 0x0
    const/4 v5, 0x1
    invoke-static {v0, v4, v0, v4, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V
    return-object v0
.end method

.method public static opaqueCycle()Linvoke_nested_object;
    .locals 5
    new-instance v0, Linvoke_nested_object;
    iput-object v0, v0, Linvoke_nested_object;->child:Linvoke_nested_object;
    const/4 v1, 0x0
    const/4 v2, 0x1
    invoke-static {v0, v1, v0, v1, v2}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V
    return-object v0
.end method

.method public static initOneOnly()Linvoke_ctor_target;
    .locals 2
    new-instance v0, Linvoke_ctor_target;
    new-instance v1, Linvoke_ctor_target;
    invoke-direct {v0}, Linvoke_ctor_target;-><init>()V
    return-object v1
.end method

.method public static initAlias()Linvoke_ctor_target;
    .locals 2
    new-instance v0, Linvoke_ctor_target;
    move-object v1, v0
    invoke-direct {v0}, Linvoke_ctor_target;-><init>()V
    return-object v1
.end method

.method public static inheritedConstructorIsMissing()V
    .locals 1
    new-instance v0, Linvoke_ctor_child;
    invoke-direct {v0}, Linvoke_ctor_child;-><init>()V
    return-void
.end method

.method public static knownVirtual()I
    .locals 1
    new-instance v0, Linvoke_dispatch_child;
    invoke-virtual {v0}, Linvoke_dispatch_base;->value()I
    move-result v0
    return v0
.end method

.method public static uniqueUnknown(Linvoke_unique_base;)I
    .locals 0
    invoke-virtual {p0}, Linvoke_unique_base;->value()I
    move-result p0
    return p0
.end method

.method public static ambiguousUnknown(Linvoke_dispatch_base;)I
    .locals 0
    invoke-virtual {p0}, Linvoke_dispatch_base;->value()I
    move-result p0
    return p0
.end method

.method public static interfaceUnknown(Linvoke_single_interface;)I
    .locals 0
    invoke-interface {p0}, Linvoke_single_interface;->value()I
    move-result p0
    return p0
.end method

.method public static directRange()V
    .locals 1
    new-instance v0, Linvoke_ctor_target;
    invoke-direct/range {v0 .. v0}, Linvoke_ctor_target;-><init>()V
    return-void
.end method

.method public static virtualRange()I
    .locals 1
    new-instance v0, Linvoke_dispatch_child;
    invoke-virtual/range {v0 .. v0}, Linvoke_dispatch_base;->value()I
    move-result v0
    return v0
.end method

.method public static interfaceRange(Linvoke_single_interface;)I
    .locals 0
    invoke-interface/range {p0 .. p0}, Linvoke_single_interface;->value()I
    move-result p0
    return p0
.end method

.method public static mixedReturnAndThrow(I)I
    .locals 3
    new-instance v0, Linvoke_merge_object;
    :try_start
    invoke-static {v0, p0}, Linvoke_task20;->mutateThenReturnOrThrow(Linvoke_merge_object;I)V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    iget v1, v0, Linvoke_merge_object;->value:I
    return v1
    :handler
    move-exception v2
    iget v1, v0, Linvoke_merge_object;->value:I
    return v1
.end method

.method private static mutateThenReturnOrThrow(Linvoke_merge_object;I)V
    .locals 2
    if-eqz p1, :normal
    const/4 v0, 0x2
    iput v0, p0, Linvoke_merge_object;->value:I
    const/4 v1, 0x0
    throw v1
    :normal
    const/4 v0, 0x1
    iput v0, p0, Linvoke_merge_object;->value:I
    return-void
.end method

.method public static throwingConstructorStaysUninitialized()Linvoke_throw_ctor;
    .locals 2
    new-instance v0, Linvoke_throw_ctor;
    :try_start
    invoke-direct {v0}, Linvoke_throw_ctor;-><init>()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    return-object v0
    :handler
    move-exception v1
    return-object v0
.end method
