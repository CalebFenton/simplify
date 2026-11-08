.class public Lsuperclass_constructor/own_number;
.super Ljava/lang/Number;

# Number is abstract, so only the chained call on this object can run its constructor.
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Number;-><init>()V
    return-void
.end method

.method public static constructIsOwnClass()Z
    .registers 1
    new-instance v0, Lsuperclass_constructor/own_number;
    invoke-direct {v0}, Lsuperclass_constructor/own_number;-><init>()V
    instance-of v0, v0, Lsuperclass_constructor/own_number;
    return v0
.end method
