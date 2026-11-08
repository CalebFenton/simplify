.class public Lclass_init/failing;
.super Ljava/lang/Object;

# A failing `<clinit>`: its throw escapes, so the class becomes `Failed` rather than
# `Initialized`, and the triggering path does not continue.
#
# `const/4 v0, 0x0` then `throw v0` is a definite NullPointerException: a register whose last
# write was primitive reads as null, and `throw` of null raises NPE ([JVMS] 6.5 `athrow`).
#
# Addresses: @0 const/4 (11n, 1 unit), @1 throw (11x, 1 unit).

.method public static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x0
    throw v0
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
