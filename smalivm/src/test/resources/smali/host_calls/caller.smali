.class public Lhost_calls/caller;
.super Ljava/lang/Object;

.method private static guest()V
    .registers 0
    return-void
.end method

.method public static calls()V
    .registers 2
    new-instance v0, Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V
    const-string v1, "x"
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-static {}, Lhost_calls/caller;->guest()V
    invoke-static {}, Ljava/lang/System;->gc()V
    return-void
.end method
