.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static createIntegerArray()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

# Distinct destination (v1) and length (v2) registers; every other `new-array` here is `v0, v0`, which
# cannot show a dest/length operand swap.
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

# createIntegerArray()V inside a Ljava/lang/Exception; handler: `new-array` at 0 (22c, two code units), `nop`
# at 2, `return-void` at 3, `move-exception v0` at 4.
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

# An absent component class and a length that can be seeded negative. ART checks the length before resolving
# the class (CheckArrayAlloc), unlike [JVMS] anewarray; see docs/DALVIK-SEMANTICS.md#new-array-op-23-22c.
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
