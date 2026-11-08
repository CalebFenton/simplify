.class public Linitializer_effects/throwing;
.super Ljava/lang/Object;
.field private static flag:I


# Writes its own initializer-only static, then throws.
.method static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x1
    sput v0, Linitializer_effects/throwing;->flag:I
    new-instance v0, Ljava/lang/IllegalStateException;
    invoke-direct {v0}, Ljava/lang/IllegalStateException;-><init>()V
    throw v0
.end method

.method public static first()I
    .registers 1
    const/4 v0, 0x1
    return v0
.end method
