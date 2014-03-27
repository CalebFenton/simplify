.class Lfilled_new_array_test;
.super Ljava/lang/Object;

.method public static TestFilledNewArrayPrimitive()V
    .locals 4

    const/4 v0, 0x1
    const/4 v1, 0x2
    const/4 v2, 0x3
    const/4 v3, 0x4
    const/4 v4, 0x5

    filled-new-array {v0, v1, v2, v3, v4}, [I

    return-void
.end method

.method public static TestFilledNewArrayLocal()V
    .locals 4

    const/4 v0, 0x1
    const/4 v1, 0x2
    const/4 v2, 0x3

    filled-new-array {v0, v1, v2}, [Lfilled_new_array_test;

    return-void
.end method