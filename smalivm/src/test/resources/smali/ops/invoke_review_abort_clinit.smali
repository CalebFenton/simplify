.class public Linvoke_review_abort_clinit;
.super Ljava/lang/Object;

.field public static value:I = 0x5

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x0
    # Keep this Task 20 abort probe on the same deliberate unsupported instruction as the spine ratchet.
    filled-new-array {v0}, [J
    return-void
.end method
