# Capability probe: Tier A reports ART's own verifier verdict.
#
# WHAT IT PROVES: a VERIFIER-INVALID class does NOT abort the VM. ART sets the whole class
# to `kErrorResolved`, `Class.forName` throws a CATCHABLE `java.lang.VerifyError`, and the
# message is ART's own verifier diagnostic, verbatim. Measured:
#
#   THREW java.lang.VerifyError  Verifier rejected class Fx_VerifyErrorBadReturn:
#   java.lang.Object Fx_VerifyErrorBadReturn.run() failed to verify: ...
#   [0x2] unexpected non-category 1 return type Reference: java.lang.Object
#
# That means Tier A can answer "WOULD THE VERIFIER EVEN ACCEPT THIS?" -- and answer it
# with ART's reason, at the offending bytecode offset -- rather than only "what does this
# valid program compute?". Every VERIFIER-tier row in `DALVIK-SEMANTICS.md` is a row where
# the engine had to invent a design choice under spec 2.2 because no measurement was
# available. This is the measurement, and it applies to the whole tier.
#
# THE VIOLATION: `run()` is declared to return `Ljava/lang/Object;` but the body uses
# `return` (the 32-bit primitive form) rather than `return-object`. The verifier catches
# the category mismatch at offset 0x2.
#
# KEEPING IT IN THE SHARED FIXTURE DEX IS DELIBERATE, and verified safe: verification is
# per-class, so this class's rejection does not disturb the other fixtures assembled
# beside it. Measured -- a batch containing this class plus valid ones reports all of them.
#
# THIS FILE WILL NOT ROUND-TRIP THROUGH baksmali AND IS NOT SUPPOSED TO.

.class public LFx_VerifyErrorBadReturn;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    const-string v0, "not an object as far as `return` is concerned"
    return v0
.end method
