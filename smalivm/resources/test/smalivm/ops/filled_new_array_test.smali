.class Lfilled_new_array_test;
.super Ljava/lang/Object;

.method public static filledNewArray()V
    .locals 3

    filled-new-array {v0, v1, v2}, [I

    return-void
.end method

.method public static filledNewArrayRange()V
    .locals 6

    filled-new-array/range {v0 .. v5}, [I

    return-void
.end method
