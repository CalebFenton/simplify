.class public Lclass_init/trigger;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# METHOD-ENTRY triggers of class initialization: every method here reaches its callee
# through a real `invoke-static` (35c, three code units).
#
# Addresses are METHOD-LOCAL, so adding whole methods is address-safe; editing instructions
# inside one shifts the rest of that method and several tests pin exact addresses.
# ---------------------------------------------------------------------------------------

# METHOD-ENTRY trigger of a class WITH a guest `<clinit>`.
# @0 invoke-static (35c, 3 units), @3 const/4 (1), @4 return (1).
.method public static callsWitnessTouch()I
    .registers 1
    invoke-static {}, Lclass_init/witness;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# METHOD-ENTRY trigger of a class with NO guest `<clinit>`. Nothing runs, so the marks land
# on the callee's own entry frame and the invoke is NOT re-executed: one node at @0.
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsNoClinitTouch()I
    .registers 1
    invoke-static {}, Lclass_init/no_clinit;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# METHOD-ENTRY trigger three classes deep (divergence 4 through `planInvoke` rather than
# through `planInitClass`). @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsChildTouch()I
    .registers 1
    invoke-static {}, Lclass_init/child;->touch()V
    const/4 v0, 0x7
    return v0
.end method

# THE `<clinit>`-ENTRY CARVE-OUT. Bytecode may call a `<clinit>` directly, and obfuscator
# output does. ART's static-call predicate explicitly excludes a class initializer.
#
# Without the carve-out this call would trigger `witness`'s own initialization, which
# schedules the SAME `<clinit>` as a prelude: the initializer would run TWICE on one path
# and its writes would be applied twice. The `Initializing` guard cannot cover it -- at the
# call site the class is still `Uninitialized`, because nothing has marked it yet.
#
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsWitnessClinitDirectly()I
    .registers 1
    invoke-static {}, Lclass_init/witness;-><clinit>()V
    const/4 v0, 0x7
    return v0
.end method

# METHOD-ENTRY trigger of a class whose `<clinit>` THROWS. The initializer fails, so this
# path cannot continue -- the caller's `const/4`/`return` must never execute.
# @0 invoke-static (3 units), @3 const/4, @4 return.
.method public static callsFailingTouch()I
    .registers 1
    invoke-static {}, Lclass_init/failing;->touch()V
    const/4 v0, 0x7
    return v0
.end method
