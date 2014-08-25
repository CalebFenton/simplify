.class Lnew_array_test;
.super Ljava/lang/Object;

.method public static TestNewArrayPrimitive()V
    .locals 1

    new-array v0, v0, [I

    return-void
.end method

.method public static TestNewArrayPrimitiveMultidimensional()V
    .locals 1

    new-array v0, v0, [[I

    return-void
.end method

.method public static TestNewArrayLocal()V
    .locals 1

    new-array v0, v0, [Lnew_array_test;

    return-void
.end method

.method public static TestNewArrayLocalMultidimensional()V
    .locals 1

    new-array v0, v0, [[Lnew_array_test;

    return-void
.end method
