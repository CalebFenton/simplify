.class public Lclass_init_failure/parent;
.super Lclass_init_failure/grandparent;

.method public static constructor <clinit>()V
    .registers 1
    sget v0, Lclass_init_failure/state;->parentAttempts:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lclass_init_failure/state;->parentAttempts:I
    return-void
.end method
