.class public Linvoke_ctor_outer;
.super Linvoke_ctor_middle;

# Completes its guest super.<init>, then throws, so construction of the receiver never completes.
.method public constructor <init>()V
    .locals 1
    invoke-direct {p0}, Linvoke_ctor_middle;-><init>()V
    const/4 v0, 0x0
    throw v0
.end method
