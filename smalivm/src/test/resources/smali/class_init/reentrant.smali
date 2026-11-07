.class public Lclass_init/reentrant;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE RE-ENTRY FIXTURE (Plan 9 Task 16, design decision 2): a `<clinit>` that TRIGGERS ITS
# OWN CLASS's initialization from inside itself.
#
# The `nop` at @0 is a STUB SITE: `ClassInitTest` replaces it with a trigger for
# `Lclass_init/reentrant;` -- this very class -- through `ExecutionSession`'s `opFactory`
# seam, which is the same seam `InvokerTest` uses and for the same reason (the ops that
# trigger initialization in real bytecode are `sget`/`sput` (Task 18) and `new-instance`
# (Task 17), neither of which exists yet).
#
# What must happen: the trigger sees `Initializing`, not `Uninitialized`, and does NOT
# re-enter. Remove the guard and this recurses until `Limits.maxCallDepth` truncates the
# run -- `Partial(CALL_DEPTH)` where the answer is `Complete`.
#
# A `nop` and not a `return-void` as the stub site, so the method still has an instruction
# AFTER the trigger to fall through to: the class-init prelude resumes at the trigger's own
# address, so the trigger executes twice and the second pass must have somewhere to go.
#
# Addresses: @0 nop (10x, 1 unit), @1 return-void.
# ---------------------------------------------------------------------------------------

.method public static constructor <clinit>()V
    .registers 1
    nop
    return-void
.end method

.method public static touch()V
    .registers 1
    return-void
.end method
