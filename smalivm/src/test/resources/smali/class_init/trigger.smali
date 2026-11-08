.class public Lclass_init/trigger;
.super Ljava/lang/Object;

# Method-entry triggers of class initialization, each through a real `invoke-static` (35c, three
# code units). Addresses are method-local: adding whole methods is address-safe, editing
# instructions inside one is not, and tests pin exact addresses.

# Method-entry trigger of a class with a guest `<clinit>`.
# @0 invoke-static (35c, 3 units), @3 const/4 (1), @4 return (1).
.method public static callsWitnessTouch()I
    .registers 1
    invoke-static {}, Lclass_init/witness;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# Method-entry trigger of a class with no guest `<clinit>`. Nothing runs, so the marks land on
# the callee's entry frame and the invoke is not re-executed: one node at @0.
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsNoClinitTouch()I
    .registers 1
    invoke-static {}, Lclass_init/no_clinit;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# Method-entry trigger three classes deep, superclass first through `planInvoke`.
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsChildTouch()I
    .registers 1
    invoke-static {}, Lclass_init/child;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# A direct call to `<clinit>`, which obfuscator output contains. ART's static-call predicate
# excludes a class initializer, so the call must not trigger `witness`'s initialization;
# otherwise the same `<clinit>` would also run as a prelude and apply its writes twice. The
# `Initializing` guard cannot catch this: the class is still `Uninitialized` at the call site.
#
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsWitnessClinitDirectly()I
    .registers 1
    invoke-static {}, Lclass_init/witness;-><clinit>()V
    const/4 v0, 0x7
    return v0
.end method

# Method-entry trigger of a class whose `<clinit>` throws. The path cannot continue: the
# caller's `const/4`/`return` never execute.
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsFailingTouch()I
    .registers 1
    invoke-static {}, Lclass_init/failing;->touch()V
    const/4 v0, 0x7
    return v0
.end method
