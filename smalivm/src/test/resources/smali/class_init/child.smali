.class public Lclass_init/child;
.super Lclass_init/parent;

# The bottom of the hierarchy divergence 4 is about; see `grandparent.smali`. Triggering
# THIS class must run three `<clinit>`s, grandparent's first and this one's last.
#
# `touch()V` exists so the same hierarchy can also be triggered by a METHOD ENTRY
# (`execution-engine.md:151-153`) rather than only by an op, which is a different code path
# in the scheduler (`planInvoke` vs `planInitClass`) reaching the same planner.

.method public static constructor <clinit>()V
    .registers 1
    nop
    return-void
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
