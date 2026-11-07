.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static createIntegerArray()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

# DISTINCT operand registers, which every other `new-array` in the whole fixture tree lacks -- all of
# them are `v0, v0`, so `dest`, `length`, `registersRead` and `registersAssigned` collapse to one number
# and a dest/length swap in `OpBuilder.buildNewArray` is invisible to every assertion in the suite.
# Real obfuscated code emits `new-array v3, v5, [B` constantly.
#
# `.locals 3` so v1 (the destination) and v2 (the length) both exist and are distinct from v0. Kept as a
# separate method for the usual reason: the three tests already driving createIntegerArray()V must not
# have their addresses or register layout changed underneath them.
.method public static createIntegerArrayIntoADifferentRegister()V
    .locals 3

    new-array v1, v2, [I

    return-void
.end method

.method public static create2DIntegerArray()V
    .locals 1

    new-array v0, v0, [[I

    return-void
.end method

.method public static createLocalInstanceArray()V
    .locals 1

    new-array v0, v0, [Lnew_array_test;

    return-void
.end method

.method public static create2DLocalInstanceArray()V
    .locals 1

    new-array v0, v0, [[Lnew_array_test;

    return-void
.end method

.method public static createNonExistentArrayClass()V
    .locals 1

    :try_start_0
    new-array v0, v0, [Ldoes/not/exist;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method

# A NEW method rather than a try block added to createIntegerArray()V, which three tests already
# assert against and one of which is visitation-shaped (sensitive to every address). Shaped exactly
# like createNonExistentArrayClass()V above so the only difference between the two is the type
# operand: `new-array` at 0 (format 22c = 2 code units), the `nop` at 2, `return-void` at 3,
# `move-exception v0` at 4.
#
# Exists for the negative-length case, which no corpus fixture covered: the oracle lets
# Array.newInstance's NegativeArraySizeException escape and kill the engine, because its
# NewArrayOp.buildInstance catches only ClassNotFoundException.
.method public static createIntegerArrayWithCatch()V
    .locals 1

    :try_start_0
    new-array v0, v0, [I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method

# Both bad at once: the component class is absent AND a negative length can be seeded into v0. The
# only witness for the ordering divergence recorded in docs/DALVIK-SEMANTICS.md#new-array-op-23-22c --
# [JVMS] 6.5 anewarray resolves BEFORE checking the count, ART's CheckArrayAlloc checks the count
# first, and this engine follows ART.
#
# Deliberately a separate method rather than reusing createNonExistentArrayClass()V, which it is
# otherwise byte-identical to: that method carries the exception-TYPE claim (a NoClassDefFoundError
# escapes a Ljava/lang/Exception; handler) and this one carries the ORDERING claim. One fixture per
# claim means editing either cannot silently change the other's test -- the same reason
# createIntegerArray()V was left alone above.
.method public static createBothBadArrayClass()V
    .locals 1

    :try_start_0
    new-array v0, v0, [Ldoes/not/exist;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method
