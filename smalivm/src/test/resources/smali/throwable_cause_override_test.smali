.class public Lthrowable_cause_override_test;
.super Ljava/lang/Exception;

.method public constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Exception;-><init>()V

    return-void
.end method

.method public getCause()Ljava/lang/Throwable;
    .locals 0

    return-object p0
.end method

.method public static overrideReturnsReceiver()I
    .locals 2

    new-instance v0, Lthrowable_cause_override_test;
    invoke-direct {v0}, Lthrowable_cause_override_test;-><init>()V
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v1
    if-ne v0, v1, :other
    const/4 v0, 0x1
    return v0

    :other
    const/4 v0, 0x0
    return v0
.end method
