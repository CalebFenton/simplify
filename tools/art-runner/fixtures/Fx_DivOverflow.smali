# Sanity fixture: proves the runner executes real bytecode.
#
# `Integer.MIN_VALUE / -1` overflows, and [BC]'s `div-int` inherits [JVMS] `idiv`'s rule that the
# result is the dividend: -2147483648. Java arithmetic without the overflow case gets 2147483648.
#
# Self-contained: a device cannot seed registers, so every fixture builds its operands with `const`.

.class public LFx_DivOverflow;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const v0, -0x80000000
    const/4 v1, -0x1
    div-int v2, v0, v1

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v3
    return-object v3
.end method
