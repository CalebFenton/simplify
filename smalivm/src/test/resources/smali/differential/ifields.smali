# Instance-field fixtures: iget/iput on fresh objects and on definitely-null receivers.
.class public Ldifferential/ifields;
.super Ljava/lang/Object;

.field public value:I

# A write followed by a read of the same fresh object yields the written value.
.method public static putThenGetInt()I
    .locals 2
    new-instance v0, Ldifferential/ifields;
    const/16 v1, 0x42
    iput v1, v0, Ldifferential/ifields;->value:I
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# A fresh object's unwritten field is its type default (JLS 12.5).
.method public static getDefaultInt()I
    .locals 2
    new-instance v0, Ldifferential/ifields;
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# A definitely-null receiver: iget throws NullPointerException, so nothing returns.
.method public static getFromNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# A definitely-null receiver: iput throws NullPointerException and stores nothing.
.method public static putIntoNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    const/16 v1, 0x2a
    iput v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method
