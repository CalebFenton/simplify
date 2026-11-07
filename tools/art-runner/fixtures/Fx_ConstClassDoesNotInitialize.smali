# THE EXPERIMENT: does `const-class` trigger static initialization?
#
# This is the claim `docs/DALVIK-SEMANTICS.md` records for `const-class`, and until this
# fixture existed it had NEVER been measured -- it was argued from ART source (the
# `CONST_CLASS` handler body does nothing but resolve and store) with [JLS] 12.4.1 as
# corroboration. The engine's `ClassIndex.loadClassConstant` resolves WITHOUT initializing
# on the strength of that argument, so this fixture is what makes the choice evidence.
#
# Method: `Probe_ClinitWitnessA`'s `<clinit>` prints a line. If `const-class` initializes,
# `CLINIT_RAN  Probe_ClinitWitnessA` appears in stdout BEFORE this fixture's own
# observation line. If it does not, the line is absent.
#
# READ IT WITH THE CONTROL. Absence of output proves nothing on its own -- a witness that
# never fires looks identical to a witness that was never triggered.
# `Fx_StaticFieldDoesInitialize` triggers ITS witness through a `sget`, which [JLS] 12.4.1
# lists as a definite trigger, so that fixture MUST print. The pair is the measurement:
# one silent, one loud, same mechanism.
#
# Returns the resolved class's name so the run also proves `const-class` actually produced
# a usable `Class` object rather than silently failing -- otherwise "no CLINIT_RAN" could
# mean "the opcode never ran".
.class public LFx_ConstClassDoesNotInitialize;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    # THE OPCODE UNDER TEST.
    const-class v0, LProbe_ClinitWitnessA;

    # Proof the operand resolved: ask the Class object for its name.
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v1

    return-object v1
.end method
