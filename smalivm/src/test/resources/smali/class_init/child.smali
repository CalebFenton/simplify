.class public Lclass_init/child;
.super Lclass_init/parent;

# The bottom of the superclass-first hierarchy; see `grandparent.smali`. Triggering this class
# runs three `<clinit>`s, grandparent's first and this one's last.
#
# `touch()V` lets a method entry trigger the same hierarchy, a different scheduler path
# (`planInvoke` rather than `planInitClass`) into the same planner.

.method public static constructor <clinit>()V
    .registers 1
    nop
    return-void
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
