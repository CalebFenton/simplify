.class public Linvocation_class_init_caller;
.super Ljava/lang/Object;

.method public static callStatic()V
    .locals 0
    invoke-static {}, Linvocation_class_init_target;->staticTouch()V
    return-void
.end method

.method public static callInheritedStatic()V
    .locals 0
    invoke-static {}, Linvocation_class_init_child;->staticTouch()V
    return-void
.end method

.method public static callVirtual(Linvocation_class_init_target;)V
    .locals 0
    invoke-virtual {p0}, Linvocation_class_init_target;->virtualTouch()V
    return-void
.end method

.method public static callInterface(Linvocation_class_init_interface;)V
    .locals 0
    invoke-interface {p0}, Linvocation_class_init_interface;->touch()V
    return-void
.end method

.method public static construct()V
    .locals 1
    new-instance v0, Linvocation_class_init_target;
    invoke-direct {v0}, Linvocation_class_init_target;-><init>()V
    return-void
.end method
