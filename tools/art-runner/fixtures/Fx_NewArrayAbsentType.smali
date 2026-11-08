# New-array whose COMPONENT type is ABSENT.
# Lcom/example/DefinitelyMissing; resolves against nothing on the boot classpath. Allocating
# [Lcom/example/DefinitelyMissing; forces resolution of the component type, which fails. The
# resolution-failure family's shared question: Error or Exception, and which type? Left to propagate.
# Read WITH the control Fx_NewArrayPresentType, which allocates [Ljava/lang/String; successfully.
# Expected: THREW java.lang.NoClassDefFoundError (measured; a LinkageError, an Error not an Exception).
.class public LFx_NewArrayAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v1, 0x1
    new-array v0, v1, [Lcom/example/DefinitelyMissing;

    # Only reached if allocation unexpectedly succeeded.
    return-object v0
.end method
