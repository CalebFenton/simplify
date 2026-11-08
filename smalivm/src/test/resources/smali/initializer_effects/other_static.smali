.class public Linitializer_effects/other_static;
.super Ljava/lang/Object;


# Writes another class's static.
.method static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x3
    sput v0, Linitializer_effects/shared;->value:I
    return-void
.end method

.method public static first()I
    .registers 1
    const/4 v0, 0x1
    return v0
.end method
