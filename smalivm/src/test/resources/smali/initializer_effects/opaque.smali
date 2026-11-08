.class public Linitializer_effects/opaque;
.super Ljava/lang/Object;


# Calls a method the reflection policy denies.
.method static constructor <clinit>()V
    .registers 0
    invoke-static {}, Ljava/lang/System;->gc()V
    return-void
.end method

.method public static first()I
    .registers 1
    const/4 v0, 0x1
    return v0
.end method
