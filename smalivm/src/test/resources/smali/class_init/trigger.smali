.class public Lclass_init/trigger;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE TRIGGERING SIDE of Plan 9 Task 16's fixtures.
#
# Two kinds of trigger live here and they exercise DIFFERENT scheduler code:
#
#  1. A `nop` STUB SITE. `ClassInitTest` replaces it, through `ExecutionSession`'s
#     `opFactory` seam, with an op that returns `Transition.InitClass` -- the transition
#     `Outcome.kt` has declared since the spine and that Task 17's `new-instance` and Task
#     18's `sget`/`sput` will emit for real. Same seam and same reason as `InvokerTest`'s
#     `InvokeStub`: the real ops do not exist yet, and the fixtures must contain the real
#     instruction widths so the addresses survive those tasks.
#
#  2. A real `invoke-static`, for the runtime static-call trigger. HISTORICAL: before Task 20, tests
#     supplied this through `opFactory`. Invoke decoding is now supported; seam-based scheduler tests
#     remain isolated while the real instruction preserves its three-code-unit layout.
#
# Addresses are METHOD-LOCAL, so adding whole methods is address-safe; editing instructions
# inside one shifts the rest of that method and several tests pin exact addresses.
# ---------------------------------------------------------------------------------------

# ONE stub site. @0 nop (10x, 1 unit), @1 const/4 (11n, 1), @2 return (11x, 1).
#
# The trigger executes TWICE on any path where it had work to do -- the class-init prelude
# resumes at the trigger's OWN address, because initialization precedes the instruction
# rather than replacing it -- so @0 has two nodes and @1/@2 have one each.
.method public static triggersOnce()I
    .registers 1
    nop
    const/4 v0, 0x7
    return v0
.end method

# TWO stub sites for the SAME class. @0 nop, @1 nop, @2 const/4, @3 return.
#
# The second must find the class already `Initialized` and run no `<clinit>` -- design
# decision 2's "the success path does not re-run". One `<clinit>` activation, not two.
.method public static triggersTwice()I
    .registers 1
    nop
    nop
    const/4 v0, 0x7
    return v0
.end method

# THE PER-PATH FIXTURE (resolved decision 9, `state-model.md:160`). @0 if-eqz (21t, 2 units),
# @2 nop, @3 nop, @4 const/4, @5 return.
#
# @2 is a stub site reached on ONE branch only; @3 is a stub site for the SAME class reached
# on BOTH. Seed v1 `Unknown(I)` and `if-eqz` forks, so:
#   - the branch through @2 initializes there, and @3 finds it already done;
#   - the branch that skipped @2 must STILL initialize at @3.
# Exactly two `<clinit>` runs across the run, one per path. A session-global store -- or the
# old engine's existential merge, which marks the caller's class initialized if ANY terminal
# callee state had it -- gives ONE, and the path that never initialized then reads the
# class's statics as though it had.
.method public static triggersOnOneBranchThenOnBoth(I)I
    .registers 2
    if-eqz v1, :skip
    nop
    :skip
    nop
    const/4 v0, 0x7
    return v0
.end method

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
