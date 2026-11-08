.class public Lsuperclass_constructor/own_state_error;
.super Ljava/lang/IllegalStateException;

# IllegalStateException is concrete and reflection-safe, so a host instance of it could replace this object.
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/IllegalStateException;-><init>()V
    return-void
.end method

.method public static constructIsOwnClass()Z
    .registers 1
    new-instance v0, Lsuperclass_constructor/own_state_error;
    invoke-direct {v0}, Lsuperclass_constructor/own_state_error;-><init>()V
    instance-of v0, v0, Lsuperclass_constructor/own_state_error;
    return v0
.end method
