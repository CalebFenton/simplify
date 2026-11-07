# The differential's CAPABILITY RATCHET fixtures: one tiny method per opcode family the clean core
# does not implement yet. Each STILL-UNIMPLEMENTED one is registered in Fixtures.intendedDivergences
# as NEW_CORE_BOUNDED_SCOPE, and the registry test asserts the core still aborts on it -- so the day a
# plan lands that family, the assertion FAILS and the implementer is forced to move the fixture into
# Fixtures.newCoreSupported. Without this, a plan could land a whole opcode family with only core
# unit tests while the differential stayed green covering none of it.
#
# A RETIRED ratchet stays in this file rather than being deleted: once its family lands it becomes an
# ordinary supported method and makes free differential coverage, and deleting it would churn
# addresses for no gain. So membership here does NOT imply a registry entry -- check which list a
# method is in before assuming it still ratchets anything. `compareWide()I` below is the first one to
# have graduated.
#
# This class keeps retired capability fixtures as agreement coverage. There is currently no live
# NEW_CORE_BOUNDED_SCOPE entry: all 218 standard executable API-20 opcodes are supported.
#
# Each method is arranged so the FAMILY'S OWN OPCODE is the first unimplemented one: everything
# before it is const/move/return, which the spine already handles. That is what makes the abort
# attributable to the family rather than to some incidental instruction, and the registry test pins
# the aborting address per entry.
#
# Every method must also RUN CLEANLY ON THE ORACLE (the registry test asserts the oracle does not
# abort), so nothing here throws, loops, or touches an unloadable class.
.class public Ldifferential/ratchet;
.super Ljava/lang/Object;

.field private static counter:I

# `if-*` / branching (Plan 8). The first fork in the whole engine: two transitions from one op.
.method public static branchOnZero()I
    .registers 1
    const/4 v0, 0x0
    if-eqz v0, :taken
    const/4 v0, 0x1
    :taken
    return v0
.end method

# `cmp-long` -- RETIRED, and now real differential coverage rather than a ratchet: the compare family
# landed in Plan 4, both engines agree on this method, and it sits in Fixtures.newCoreSupported as
# `ratchet-compare-wide` with no registry entry. The id keeps the `ratchet-` prefix on purpose, because
# renaming it would break nothing loudly and would lose the trail back to what it used to guard.
#
# It is still worth sweeping for the reason it was written: a different OpBuilder dispatch from
# `add-int` -- a wide COMPARISON producing a narrow result -- so it exercises the wide-pair read path
# rather than the binary-math path. (`Lspine/spine;->unsupportedOpcode()I` was the ratchet for
# `add-int` itself and has graduated the same way, into `newCoreSupported` as `spine-add-int`.)
.method public static compareWide()I
    .registers 5
    const-wide/16 v0, 0x1
    const-wide/16 v2, 0x2
    cmp-long v4, v0, v2
    return v4
.end method

# arrays -- RETIRED in Plan 7 Task 8, and now real differential coverage rather than a ratchet. It sits
# in Fixtures.newCoreSupported as `ratchet-array-round-trip` with no registry entry, observing v2 (the
# `aget` destination and the returned value), and the id keeps the `ratchet-` prefix for the same reason
# `compareWide()I` above does: renaming it would break nothing loudly and would lose the trail back to
# what it used to guard.
#
# Which task retires a ratchet is not a matter of taste -- it is the task that lands the LAST opcode the
# fixture needs. Here that is `aput` at address 5: Task 6's `new-array` only moved the core's abort from
# address 1 to 5 (measured: core reached [0, 1, 3, 4, 5], oracle [0, 1, 3, 4, 5, 7, 9]), and Task 7's
# `aget` at address 7 could not move it at all, so both of NEW_CORE_BOUNDED_SCOPE's assertions still held
# through two tasks that each landed one of this method's opcodes.
#
# It is still worth sweeping for the reason it was written: a full allocate / store / load round trip
# through the VM-tracked heap, which no `spine` or `ops` fixture performs.
.method public static arrayRoundTrip()I
    .registers 3
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    const/4 v2, 0x7
    aput v2, v0, v1
    aget v2, v0, v1
    return v2
.end method

# static field access (Plan 9). No `<clinit>`, so the field starts at its default and the round trip
# is deterministic on both engines.
.method public static staticFieldRoundTrip()I
    .registers 1
    const/4 v0, 0x5
    sput v0, Ldifferential/ratchet;->counter:I
    sget v0, Ldifferential/ratchet;->counter:I
    return v0
.end method

# invoke (Plan 9). Calls a method in this same class so the target always resolves and the oracle
# never has to reflect or emulate anything.
.method public static invokeStatic()I
    .registers 1
    invoke-static {}, Ldifferential/ratchet;->branchOnZero()I
    move-result v0
    return v0
.end method

# `new-instance` (Plan 9). Kept distinct from `invokeStatic` because they are separate OpBuilder
# dispatches and separate plan milestones: allocation and call. `Ldifferential/smoke;->newObject()`
# covers the same ground in `Fixtures.all`, but that one is not in the registry and so ratchets
# nothing.
#
# ⚠️ Task 17 landed `new-instance`, so the FIRST unimplemented opcode here is now the
# `invoke-direct` at address 2 -- see the registry entry's re-scoped prose. The method body is
# deliberately unchanged: editing it would churn the oracle's and the goldens' view of it for no
# gain, and the `<init>` call is exactly what still needs ratcheting (Task 20).
.method public static newInstance()Ljava/lang/Object;
    .registers 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

# A trivial constructor, declared so `initializeOneOfTwoAllocations()V` below can initialize an
# instance of THIS class the verifier-plausible way (a `new-instance Lfoo;` must be initialized by
# foo's OWN <init>, never by a direct super call at the allocation site). The oracle runs this body
# virtually; its `invoke-direct` on Object.<init> lands on the oracle's emulated
# java_lang_Object_init, which is where adjudication 8's aliasing defect lives.
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

# ADJUDICATION 8's fixture: two UNRELATED allocations of one type, then `<init>`
# on exactly ONE of them. The oracle's ExecutionState.update matches by `equals` and
# type/UninitializedInstance compares TYPE ONLY, so initializing v1 rewrites v0 too -- an unrelated
# allocation. The core's heap identity is the id, so v0 stays its own uninitialized object.
# HISTORICAL: Task 17 registered this until Task 20 landed `invoke-direct`. Invoke decoding is now supported;
# Task 20 measured agreement and moved this fixture to newCoreSupported. NEW_CORE_BOUNDED_SCOPE is empty.
.method public static initializeOneOfTwoAllocations()V
    .registers 2
    new-instance v0, Ldifferential/ratchet;
    new-instance v1, Ldifferential/ratchet;
    invoke-direct {v1}, Ldifferential/ratchet;-><init>()V
    return-void
.end method

# Adjudication 8, second shape: the `<init>` is Object's own, so the ORACLE routes it through its
# EMULATED java_lang_Object_init -- which is where the type-equality alias rewrite lives
# (`state.assignRegister(..., updateIdentities = true)` -> `ExecutionState.update`, matching by
# `equals`, and UninitializedInstance compares TYPE ONLY). Both registers are in ONE frame here,
# which is what the oracle's single-state matching needs.
.method public static initializeOneOfTwoObjectAllocations()V
    .registers 2
    new-instance v0, Ljava/lang/Object;
    new-instance v1, Ljava/lang/Object;
    invoke-direct {v1}, Ljava/lang/Object;-><init>()V
    return-void
.end method
