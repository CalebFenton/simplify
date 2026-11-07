.class public Linvoke_review_default_impl;
.super Ljava/lang/Object;
.implements Linvoke_review_default_iface;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public callDefault()I
    .locals 1
    invoke-super {p0}, Linvoke_review_default_iface;->value()I
    move-result v0
    return v0
.end method
