# Does a bare `new-instance` trigger static initialization? Probe_ClinitWitnessC's `<clinit>` prints
# `CLINIT_RAN Probe_ClinitWitnessC` if it runs.
#
# The allocation is bare: calling `<init>` is a method entry, which triggers initialization in its own
# right ([JLS] 12.4.1) and would hide which opcode ran the `<clinit>`. The uninitialized reference is
# never read, so the fixture returns a constant marker.
#
# Controls: Fx_StaticFieldDoesInitialize proves the witness mechanism fires (a `sget` trigger), and
# Fx_NewInstanceAbsentType proves the opcode executes and resolves its operand.
.class public LFx_NewInstanceDoesInitialize;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    # THE OPCODE UNDER TEST.
    new-instance v0, LProbe_ClinitWitnessC;

    const-string v1, "NEW_INSTANCE_RAN"
    return-object v1
.end method
