.class Larray_length_test;
.super Ljava/lang/Object;

.method public static getLength()V
    .locals 1

    array-length v0, v0

    return-void
.end method

# Distinct operand registers. `getLength()V` is `array-length v0, v0`, where a
# dest/array swap in `OpBuilder.buildArrayLength` is invisible. `.locals 3` makes v1 (destination)
# and v2 (array) distinct from each other and from v0, which the test seeds as an untouched witness.
.method public static getLengthIntoADifferentRegister()V
    .locals 3

    array-length v1, v2

    return-void
.end method

.method public static getLengthWithCatch()V
    .locals 2

    :try_start_0
    array-length v0, v0
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

.method public static forkFreshThrowAcrossUnknownHandler([I)Ljava/lang/Object;
    .locals 2

    :try_start
    array-length v0, p0
    :try_end
    .catch Lspine/absent; {:try_start .. :try_end} :handler

    const/4 v1, 0x0
    return-object v1

    :handler
    move-exception v1
    return-object v1
.end method
