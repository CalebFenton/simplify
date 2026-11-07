.class Larray_length_test;
.super Ljava/lang/Object;

.method public static getLength()V
    .locals 1

    array-length v0, v0

    return-void
.end method

# DISTINCT operand registers. `getLength()V` above is `array-length v0, v0`, and so is every other
# `array-length` in the fixture tree, which collapses `destRegister` and `arrayRegister` to one number and
# makes a swap in `OpBuilder.buildArrayLength` invisible to every assertion in the suite. Real code emits
# `array-length v1, v2` constantly.
#
# `.locals 3` so v1 (the destination) and v2 (the array) are distinct from each other and from v0, which
# the test seeds as an untouched witness. Kept as a separate method so the fifteen tests already driving
# `getLength()V` keep their addresses and register layout.
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
