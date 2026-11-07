.class public Lclass_without_default_constructor_checks_null;
.super Ljava/lang/Object;

.method public constructor <init>([C)V
    .locals 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    if-nez p1, :return

    new-instance v0, Ljava/lang/NullPointerException;
    const-string/jumbo v1, "You'll never reach the Dark Tower!"
    invoke-direct {v0, v1}, Ljava/lang/NullPointerException;-><init>(Ljava/lang/String;)V
    throw v0

    :return
    return-void
.end method
