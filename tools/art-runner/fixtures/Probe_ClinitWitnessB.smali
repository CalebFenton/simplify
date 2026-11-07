# The second class-initialization witness, for `Fx_StaticFieldDoesInitialize` -- the
# CONTROL of the const-class experiment.
#
# Identical to `Probe_ClinitWitnessA` apart from the name it prints. Two separate witness
# classes is the point: see that file's comment.
.class public LProbe_ClinitWitnessB;
.super Ljava/lang/Object;

.field public static marker:I

.method static constructor <clinit>()V
    .locals 2

    # `const/16`, not `const/4`: a `const/4` literal is 4-bit SIGNED, so it only reaches
    # -8..7 and the assembler rejects 9 outright ("Invalid literal value").
    const/16 v0, 0x9
    sput v0, LProbe_ClinitWitnessB;->marker:I

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "CLINIT_RAN\tProbe_ClinitWitnessB"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    return-void
.end method
