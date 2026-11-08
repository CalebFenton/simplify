.class public Lclass_init/reentrant;
.super Ljava/lang/Object;

# A `<clinit>` that triggers its own class's initialization through the `new-instance` at @0.
# The trigger sees `Initializing` and does not re-enter; without that guard the run recurses
# to `Partial(CALL_DEPTH)` where the answer is `Complete`.
#
# Addresses: @0 new-instance (21c, 2 units), @2 return-void.

.method public static constructor <clinit>()V
    .registers 1
    new-instance v0, Lclass_init/reentrant;
    return-void
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
