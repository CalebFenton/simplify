.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static NewIntegerArray()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

.method public static NewIntegerArrayArray()V
    .locals 1

    new-array v0, v0, [[I

    return-void
.end method

.method public static NewLocalClassArray()V
    .locals 1

    new-array v0, v0, [Lnew_array_test;

    return-void
.end method

.method public static NewLocalClassArrayArray()V
    .locals 1

    new-array v0, v0, [[Lnew_array_test;

    return-void
.end method
