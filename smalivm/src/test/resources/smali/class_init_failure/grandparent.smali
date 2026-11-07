.class public Lclass_init_failure/grandparent;
.super Ljava/lang/Object;

.method public static constructor <clinit>()V
    .registers 2
    sget v0, Lclass_init_failure/state;->grandparentAttempts:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lclass_init_failure/state;->grandparentAttempts:I
    const/4 v1, 0x0
    throw v1
.end method
