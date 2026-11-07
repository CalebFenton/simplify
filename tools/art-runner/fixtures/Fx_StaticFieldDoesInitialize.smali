# THE CONTROL for `Fx_ConstClassDoesNotInitialize`, and it is not optional.
#
# It triggers `Probe_ClinitWitnessB`'s `<clinit>` through a `sget` on a non-constant static
# field -- which [JLS] 12.4.1 lists explicitly as a trigger ("A static field declared by T
# is used and the field is not a constant variable"). So this fixture MUST print
# `CLINIT_RAN  Probe_ClinitWitnessB`.
#
# Without it, the const-class fixture's silence is worthless: it would be indistinguishable
# from a witness whose `println` does not work, a `<clinit>` the assembler dropped, or
# stdout being swallowed. This fixture proves the witness mechanism fires on this runtime,
# so that the other fixture's silence means what it claims.
#
# Returns the field's value (9) so a wrong-but-quiet read is also visible.
.class public LFx_StaticFieldDoesInitialize;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    # A definite initialization trigger, per [JLS] 12.4.1.
    sget v0, LProbe_ClinitWitnessB;->marker:I

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1

    return-object v1
.end method
