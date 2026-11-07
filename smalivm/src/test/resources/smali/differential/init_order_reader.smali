.class public Ldifferential/init_order_reader;
.super Ljava/lang/Object;

.method public static initializeParentOnly()I
    .locals 1
    new-instance v0, Ldifferential/init_order_parent;
    sget v0, Ldifferential/init_order_witness;->marker:I
    return v0
.end method

.method public static initializeChild()I
    .locals 1
    new-instance v0, Ldifferential/init_order_child;
    sget v0, Ldifferential/init_order_witness;->marker:I
    return v0
.end method
