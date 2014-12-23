.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static TestNewIntegerArray()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

.method public static TestNewIntegerArrayArray()V
    .locals 1

    new-array v0, v0, [[I

    return-void
.end method

.method public static TestNewLocalClassArray()V
    .locals 1

    new-array v0, v0, [Lnew_array_test;

    return-void
.end method

.method public static TestNewLocalClassArrayArray()V
    .locals 1

    new-array v0, v0, [[Lnew_array_test;

    return-void
.end method
