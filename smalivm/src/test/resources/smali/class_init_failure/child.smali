.class public Lclass_init_failure/child;
.super Lclass_init_failure/parent;

.method public static constructor <clinit>()V
    .registers 1
    sget v0, Lclass_init_failure/state;->childAttempts:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lclass_init_failure/state;->childAttempts:I
    return-void
.end method
