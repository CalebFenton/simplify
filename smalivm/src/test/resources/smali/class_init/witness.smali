.class public Lclass_init/witness;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE ORDINARY GUEST `<clinit>` (Plan 9 Task 16): a real body, run as a nested activation
# through the same Invoker/VirtualBackend seam an ordinary call uses (design decision 1).
#
# The `<clinit>` ALLOCATES an array rather than doing nothing, and that is the observable
# the whole "nested call, shared heap" claim rests on. Static fields are Task 18's, so a
# `<clinit>` has no other way to leave a trace this task can read -- and an allocation is
# the stronger evidence anyway: `Frame.calleeEntryFrame`/`resumedWith` share the heap ID
# SPACE, so the object this initializer allocates must be visible from the caller's resumed
# frame under an id that cannot collide with any of the caller's own.
#
# Addresses, derived from the assembled instructions and not assumed:
#   <clinit>: @0 const/4 (11n, 1 unit), @1 new-array (22c, 2 units), @3 return-void.
# ---------------------------------------------------------------------------------------

.method public static constructor <clinit>()V
    .registers 2
    const/4 v0, 0x3
    new-array v1, v0, [I
    return-void
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
