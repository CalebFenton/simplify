.class public Linvoke_call_abort_clinit;
.super Ljava/lang/Object;

.field public static value:I = 0x5

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x0
    # Abort probe: keep this on the same deliberately unsupported instruction as the spine ratchet.
    filled-new-array {v0}, [J
    return-void
.end method
