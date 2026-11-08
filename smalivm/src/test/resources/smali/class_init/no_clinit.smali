.class public Lclass_init/no_clinit;
.super Ljava/lang/Object;

# Declares no `<clinit>`, so initializing it moves Uninitialized -> Initialized with no nested
# run. Object declares no `<clinit>` in the captured framework either, so the trigger's
# side-effect level must not rise to STRONG.

.method public static touch()V
    .registers 1
    return-void
.end method
