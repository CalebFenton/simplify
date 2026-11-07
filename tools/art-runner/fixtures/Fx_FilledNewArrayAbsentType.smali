# WORKLIST ROW 14 -- filled-new-array naming an ABSENT type (via the resolution PROCEDURE, not a
# counterpart instruction). Lcom/example/DefinitelyMissing; resolves against nothing; building
# [Lcom/example/DefinitelyMissing; forces resolution, which fails. One null element (assignable to
# any reference component), so the only failure is the type resolution itself. Left to propagate.
# Read WITH the control Fx_FilledNewArrayPresentType, which builds [Ljava/lang/String; successfully.
# Expected: THREW java.lang.NoClassDefFoundError (measured).
.class public LFx_FilledNewArrayAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x0
    filled-new-array {v0}, [Lcom/example/DefinitelyMissing;
    move-result-object v1

    # Only reached if the build unexpectedly succeeded.
    return-object v1
.end method
