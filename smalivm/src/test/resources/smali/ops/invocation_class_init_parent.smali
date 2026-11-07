.class public Linvocation_class_init_parent;
.super Ljava/lang/Object;

.field public static runs:I

.method public static constructor <clinit>()V
    .locals 1
    sget v0, Linvocation_class_init_parent;->runs:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Linvocation_class_init_parent;->runs:I
    return-void
.end method

.method public static staticTouch()V
    .locals 0
    return-void
.end method

.method public virtualTouch()V
    .locals 0
    return-void
.end method
