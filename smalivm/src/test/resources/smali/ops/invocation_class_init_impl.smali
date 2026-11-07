.class public Linvocation_class_init_impl;
.super Ljava/lang/Object;
.implements Linvocation_class_init_interface;

.field public static runs:I

.method public static constructor <clinit>()V
    .locals 1
    sget v0, Linvocation_class_init_impl;->runs:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Linvocation_class_init_impl;->runs:I
    return-void
.end method

.method public touch()V
    .locals 0
    return-void
.end method
