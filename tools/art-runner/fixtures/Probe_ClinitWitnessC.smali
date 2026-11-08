# Class-initialization witness for `Fx_NewInstanceDoesInitialize`; identical to Probe_ClinitWitnessA
# and B apart from the name it prints. Each question gets its own witness because every fixture in a
# batch shares one runtime, so a class initialized by one fixture stays initialized for later ones.
#
# Not named `Fx_*`, so `RunFixtures` never invokes it: `Class.forName` initializes what it loads.
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
