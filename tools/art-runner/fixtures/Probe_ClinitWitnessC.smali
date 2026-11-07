# The THIRD class-initialization witness, for `Fx_NewInstanceDoesInitialize` -- Plan 9
# Task 16 Step 0.
#
# Identical to `Probe_ClinitWitnessA`/`Probe_ClinitWitnessB` apart from the name it prints.
# A witness per question is the invariant those two files established and it is what makes
# a SILENT result mean anything: every fixture in a batch shares one runtime, so a class
# initialized by one fixture is already initialized for every later one. If this fixture's
# question reused witness A, `Fx_ConstClassDoesNotInitialize` (which runs FIRST under the
# batch's byte order) would leave A uninitialized and the reading would still work by luck
# -- and the day a fixture name sorted differently the experiment would silently invert.
# One witness, one question.
#
# NOT named `Fx_*`, so `RunFixtures` never invokes it directly -- `Class.forName`
# initializes what it loads, which would answer this fixture's question for it.
.class public LProbe_ClinitWitnessC;
.super Ljava/lang/Object;

# A static field for the same reason A and B have one: it makes a `sget` on this class a
# legitimate trigger ([JLS] 12.4.1), so the witness can be re-used as its own control.
# Deliberately NOT `final` with a compile-time constant value, which would make it a
# constant variable and would NOT trigger initialization.
.field public static marker:I

# A no-argument constructor so that `new-instance` of this class is a WELL-FORMED
# allocation rather than one the verifier could reject for a reason unrelated to the
# question. The fixture deliberately never CALLS it: `invoke-direct <init>` is a method
# entry, and a non-`<clinit>` method entry triggers its own defining class's init in its
# own right -- so calling it would make the measurement unable to say which opcode ran the
# `<clinit>`, which is the entire question.
.method public constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method static constructor <clinit>()V
    .locals 2

    const/16 v0, 0xb
    sput v0, LProbe_ClinitWitnessC;->marker:I

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "CLINIT_RAN\tProbe_ClinitWitnessC"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    return-void
.end method
