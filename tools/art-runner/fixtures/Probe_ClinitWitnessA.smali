# A CLASS-INITIALIZATION WITNESS. Its `<clinit>` prints a line, so the mere appearance of
# that line in a run's stdout proves the class was initialized.
#
# Used by `Fx_ConstClassDoesNotInitialize`. It has its own witness class, separate from
# `Probe_ClinitWitnessB`, because both witnesses would otherwise share a runtime: the
# control fixture initializing ONE of them would make the other's silence unprovable.
# One witness, one question.
#
# NOT named `Fx_*`, so `RunFixtures` never invokes it directly -- it is only ever
# initialized as a side effect of the fixture under test, which is the whole point.
.class public LProbe_ClinitWitnessA;
.super Ljava/lang/Object;

# A static field so that a `sget` on this class is a legitimate initialization trigger
# ([JLS] 12.4.1: "A static field declared by T is used and the field is not a constant
# variable"). Deliberately NOT `final` with a compile-time constant value, which would
# make it a constant variable and would NOT trigger initialization.
.field public static marker:I

.method static constructor <clinit>()V
    .locals 2

    const/4 v0, 0x7
    sput v0, LProbe_ClinitWitnessA;->marker:I

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "CLINIT_RAN\tProbe_ClinitWitnessA"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    return-void
.end method
