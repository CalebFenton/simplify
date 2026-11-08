.class public Linitializer_effects/preexisting;
.super Ljava/lang/Object;


# Writes into an array allocated before the initialization started.
.method static constructor <clinit>()V
    .registers 2
    sget-object v0, Linitializer_effects/shared;->array:[I
    const/4 v1, 0x0
    aput v1, v0, v1
    return-void
.end method

.method public static first()I
    .registers 1
    const/4 v0, 0x1
    return v0
.end method
