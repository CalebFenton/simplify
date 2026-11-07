# SANITY FIXTURE. Not a worklist row -- this is the canary that says "the oracle is
# wired up and executing real bytecode", so it belongs in every batch.
#
# `Integer.MIN_VALUE / -1` overflows: the mathematical answer 2147483648 is not
# representable, and [BC]'s `div-int` row inherits [JVMS] `idiv`'s rule that the result
# is the dividend itself. Anything that constant-folds this in Java arithmetic without
# the overflow case gets 2147483648 or throws; a real Dalvik gives -2147483648.
#
# SELF-CONTAINED: builds both operands with `const`. A device cannot seed registers, so
# every fixture here must, and roughly half of this repo's in-JVM fixtures cannot be
# ported for exactly that reason.

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
