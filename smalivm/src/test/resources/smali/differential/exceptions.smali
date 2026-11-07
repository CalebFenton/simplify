.class public Ldifferential/exceptions;
.super Ljava/lang/Object;

# The parameter is an unknown divisor, so both normal return and ArithmeticException are feasible.
# The exception is synthesized on one arm of the local div-int rather than in a called method.
.method public static ambiguousLocalDivide(I)V
    .locals 1

    const/16 v0, 0xc
    div-int v0, v0, p0
    return-void
.end method
