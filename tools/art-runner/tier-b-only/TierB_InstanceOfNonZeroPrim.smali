# Tier B probe, verifier-invalid by design; not a corpus fixture. It asks what default-mode modern
# ART (nterp) does when a dereferencing op reads a register holding a non-zero primitive where a
# reference is expected. See run-tier-b-probes.sh.
#
# It lives beside fixtures/, not under it: the core loads its class model from that directory
# recursively, and a verifier-invalid class there would enter the device corpus.
#
# The probe: put 0x11223344 (a distinctive, non-zero primitive) in a register, then run
# `instance-of` treating it as a reference. Verifier-invalid (a non-reference register in a
# type op is VERIFY_ERROR_BAD_CLASS_HARD), so this is only runnable under -Xverify:none.
#   - switch interpreter (-Xint): reads the CLEARED reference-array slot -> nullptr -> result 0.
#   - nterp (default):            reads the RAW dex-register array -> 0x11223344 -> derefs it.
# TIERB_BEGIN prints (and flushes) before the op so a crash is localised to the op itself.
.class public LTierB_InstanceOfNonZeroPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 4

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    # NON-ZERO primitive in v2, then dereference it as a reference.
    const v2, 0x11223344
    instance-of v3, v2, Ljava/lang/Object;

    # Only reached if nterp did NOT crash: report the value it produced.
    const-string v1, "TIERB_INSTANCEOF_RESULT="
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->println(I)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
