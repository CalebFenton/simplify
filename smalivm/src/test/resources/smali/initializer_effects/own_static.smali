.class public Linitializer_effects/own_static;
.super Ljava/lang/Object;
.field private static table:[I


# Writes only its own initializer-only static and an array it allocates.
.method static constructor <clinit>()V
    .registers 3
    const/4 v0, 0x2
    new-array v0, v0, [I
    const/4 v1, 0x7
    const/4 v2, 0x0
    aput v1, v0, v2
    sput-object v0, Linitializer_effects/own_static;->table:[I
    return-void
.end method

.method public static first()I
    .registers 2
    sget-object v0, Linitializer_effects/own_static;->table:[I
    const/4 v1, 0x0
    aget v0, v0, v1
    return v0
.end method
