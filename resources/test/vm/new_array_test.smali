.class public final Lnew_array_test;
.super Ljava/lang/Object;

.method public static TestNewArrayPrimitive()[I
    .locals 1

    const/4 v0, 0x1

    new-array v0, v0, [I

    return v0
.end method

.method public static TestNewArrayLocal()I
    .locals 1

    const/4 v0, 0x1

    new-array v0, v0, [Lnew_array_test;

    # -3 + 7 = 4
    return v0
.end method
