.class public Lclass_init/heir;
.super Lclass_init/grandparent;

# Declares no `<clinit>` while its superclass declares one. Static initializers are not
# inherited, so an ancestor-searching method lookup of `<clinit>()V` would answer grandparent's
# initializer and mark heir initialized without running anything of its own. This fixture
# separates that lookup from the declared-only `lookupClassInitializer`.

.method public static touch()V
    .registers 1
    return-void
.end method
