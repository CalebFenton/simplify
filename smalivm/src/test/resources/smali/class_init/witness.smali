.class public Lclass_init/witness;
.super Ljava/lang/Object;

# An ordinary guest `<clinit>`, run as a nested activation through the same invoker path as a
# call. It allocates an array: caller and initializer share the heap id space, so the object
# must be visible from the caller's resumed frame under an id that does not collide with the
# caller's own.
#
# Addresses, derived from the assembled instructions:
#   <clinit>: @0 const/4 (11n, 1 unit), @1 new-array (22c, 2 units), @3 return-void.

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
