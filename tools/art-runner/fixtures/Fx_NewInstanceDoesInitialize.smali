# THE EXPERIMENT: does a bare `new-instance` trigger static initialization?
#
# Plan 9 Task 16 Step 0. The plan asserted `[DEVICE]` for this three times on the strength
# of an `[ART]` SOURCE READING -- `NEW_INSTANCE` at
# `runtime/interpreter/interpreter_switch_impl-inl.h:862-894` passes `can_run_clinit=false`
# yet initializes downstream through `AllocObjectFromCode` (`:882`) -> `CheckObjectAlloc`
# -> `CheckClassInitializedForObjectAlloc` -> `EnsureInitialized` -- and no fixture existed
# in either golden set. That is the SAME opcode `docs/DALVIK-SEMANTICS.md`'s own
# "`can_run_clinit` non-sequitur" cautionary passage is about, so it is the last claim that
# should have been over-tiered. This fixture is what makes it a measurement.
#
# Method: `Probe_ClinitWitnessC`'s `<clinit>` prints a line. If `new-instance` initializes,
# `CLINIT_RAN  Probe_ClinitWitnessC` appears in stdout. If it does not, the line is absent.
#
# A BARE `new-instance` -- no `invoke-direct <init>`, deliberately. The constructor exists
# on the probe (so the allocation is well-formed) and is never called, because a
# non-`<clinit>` method entry is an initialization trigger in its OWN right
# (`docs/architecture/execution-engine.md:151-153`, [JLS] 12.4.1). Calling it would make
# the observation unable to attribute the `<clinit>` to `new-instance`, which is the whole
# question. The allocated reference is therefore UNUSED: the verifier permits an
# uninitialized reference to sit in a register unread, and it may not be touched any other
# way, which is also why the proof-the-opcode-ran line cannot come from the object itself
# (contrast `Fx_ConstClassDoesNotInitialize`, which can call `getName()` on its `Class`).
#
# READ IT WITH TWO CONTROLS. Neither is optional and they close different holes:
#  - `Fx_StaticFieldDoesInitialize` proves the WITNESS MECHANISM fires on this runtime
#    (same shape, its own witness B, triggered by a `sget` that [JLS] 12.4.1 lists
#    explicitly). Without it, a printed line's absence is indistinguishable from a
#    `println` that does not work or a `<clinit>` the assembler dropped.
#  - `Fx_NewInstanceAbsentType` proves the OPCODE genuinely executes and resolves its
#    operand here rather than being elided, by making an unresolvable operand throw.
#    Without it, "no CLINIT_RAN" could mean "the allocation never happened".
#
# Returns a constant marker, which is all a bare allocation can offer: the uninitialized
# reference cannot be observed. The `OK` row plus the absent-type control is the proof.
.class public LFx_NewInstanceDoesInitialize;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    # THE OPCODE UNDER TEST.
    new-instance v0, LProbe_ClinitWitnessC;

    const-string v1, "NEW_INSTANCE_RAN"
    return-object v1
.end method
