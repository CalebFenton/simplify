.class public Lclass_init/failing;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE FAILING `<clinit>` (Plan 9 Task 16, design decision 2's fourth state): an initializer
# whose throw escapes it, so its class becomes `Failed` rather than `Initialized`.
#
# `const/4 v0, 0x0` then `throw v0` is a DEFINITE NullPointerException, not an ambiguity: a
# register whose last write was primitive reads as a null reference, and `ThrowOp`
# synthesizes the NPE [JVMS] 6.5 `athrow` documents for a null operand (see
# `ExecutionSession.resolvableTypeOf`). `Lcalls/callee;->throwsUncaught()I` is the same
# shape, and it is the only way to throw here at all -- `new-instance` is Task 17's.
#
# What must happen: the class is marked `Failed` on the failing path, and the triggering
# path aborts (cross-frame unwind, and therefore the `ExceptionInInitializerError` a device
# raises, is Plan 12's -- resolved decision 4).
#
# Addresses: @0 const/4 (11n, 1 unit), @1 throw (11x, 1 unit).
# ---------------------------------------------------------------------------------------

.method public static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x0
    throw v0
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
