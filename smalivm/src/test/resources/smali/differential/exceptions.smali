.class public Ldifferential/exceptions;
.super Ljava/lang/Object;

# The divisor is an unknown parameter, so both normal return and ArithmeticException are feasible;
# the exception arises from a local div-int, not a callee.
.method public static ambiguousLocalDivide(I)V
    .locals 1

    const/16 v0, 0xc
    div-int v0, v0, p0
    return-void
.end method
