.class public Lmethodedit/Adversarial;
.super Ljava/lang/Object;
.source "method_edit_adversarial.smali"

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static opaque(I)I
    .registers 2
    const/4 v0, 0x0
    if-eq p0, v0, :zero
    const/4 v0, 0x2
    goto :done
    :zero
    const/4 v0, 0x1
    :done
    return v0
.end method

.method public static reflectionInTry()I
    .registers 2
    :try_start
    const-string v0, "java.lang.String"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :handler
    if-eqz v0, :failed
    const/4 v0, 0x1
    return v0
    :failed
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x0
    return v0
.end method

.method public parameters(IJLjava/lang/Object;)J
    .registers 5
    return-wide p2
.end method

.method public static payload([B)V
    .registers 1
    return-void
.end method

.method public static debug()V
    .registers 1
    return-void
.end method

.method public static handlers()V
    .registers 1
    nop
    return-void
    move-exception v0
    return-void
.end method

.method public static retention()Ljava/lang/Class;
    .registers 1
    const-class v0, Lmethodedit/Seed;
    return-object v0
.end method
