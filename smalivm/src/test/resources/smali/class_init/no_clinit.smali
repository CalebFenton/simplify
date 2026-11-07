.class public Lclass_init/no_clinit;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE COMMON CASE (Plan 9 Task 16, design decision 1): a class that declares NO `<clinit>`
# at all, so initializing it transitions Uninitialized -> Initialized with NO nested run.
#
# Object declares no <clinit> in the captured framework. With nothing to suppress, this
# superclass chain must not raise the trigger's side-effect level to STRONG.
# ---------------------------------------------------------------------------------------

.method public static touch()V
    .registers 1
    return-void
.end method
