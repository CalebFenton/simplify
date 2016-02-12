.class Lnew_instance_test;
.super Ljava/lang/Object;

.method public static newLocalInstance()V
    .locals 1

    new-instance v0, Lnew_instance_test;

    return-void
.end method

.method public static newNonLocalInstance()V
    .locals 1

    new-instance v0, Ljava/lang/Integer;

    return-void
.end method
