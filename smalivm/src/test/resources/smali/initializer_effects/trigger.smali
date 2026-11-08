.class public Linitializer_effects/trigger;
.super Ljava/lang/Object;


# Each method's first instruction that triggers a class initialization is at address 0, except preexisting()I,
# whose invoke is at address 5 (const/4 1 unit, new-array 2, sput-object 2).
.method public static ownStatic()I
    .registers 1
    invoke-static {}, Linitializer_effects/own_static;->first()I
    move-result v0
    return v0
.end method

.method public static otherStatic()I
    .registers 1
    invoke-static {}, Linitializer_effects/other_static;->first()I
    move-result v0
    return v0
.end method

.method public static preexisting()I
    .registers 1
    const/4 v0, 0x1
    new-array v0, v0, [I
    sput-object v0, Linitializer_effects/shared;->array:[I
    invoke-static {}, Linitializer_effects/preexisting;->first()I
    move-result v0
    return v0
.end method

.method public static opaque()I
    .registers 1
    invoke-static {}, Linitializer_effects/opaque;->first()I
    move-result v0
    return v0
.end method

.method public static throwing()I
    .registers 1
    invoke-static {}, Linitializer_effects/throwing;->first()I
    move-result v0
    return v0
.end method

.method public static unobservableFramework()V
    .registers 1
    new-instance v0, Ljava/lang/Integer;
    return-void
.end method

.method public static observableFramework()V
    .registers 1
    new-instance v0, Ljava/util/Random;
    return-void
.end method
