.class public Lclass_init/catching;
.super Ljava/lang/Object;

# A `<clinit>` that catches its own exception. It pins when the `Failed` mark is applied:
# successor frames are copied from the post-op frame before terminal writes, so a mark made
# when the escape is planned would also land on the handler successors, where the initializer
# catches the exception and completes. Those paths would wrongly read the class as `Failed`.
#
# The `nop` at @0 is a stub site: `ClassInitTest` replaces it with a throw of an unresolvable
# value, the one shape where one op has both a caught and an escaping successor
# (`ExecutionSession.planThrow`). A `const/4` + `throw` is a definite NullPointerException,
# whose covering handler leaves no escape.
#
# The catch is `RuntimeException`, not a catch-all: a catch-all would make the escape
# infeasible and leave no `Failed` mark to misplace.
#
# Addresses: @0 nop (10x, 1 unit), @1 return-void, @2 move-exception (11x, 1), @3 return-void.

.method public static constructor <clinit>()V
    .registers 1
    :try_start
    nop
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    return-void

    :handler
    move-exception v0
    return-void
.end method
