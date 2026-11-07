# TIER-B-ONLY PROBE. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
#
# This file lives in tools/art-runner/tier-b-only/ -- a SIBLING of fixtures/, never under it --
# on purpose. It measures the ONE question Tier A cannot answer: what a default-mode modern-ART
# device (nterp) does when a dereferencing op reads a register holding a NON-ZERO primitive where
# a reference is expected. See tools/art-runner/tier-b-only/run-tier-b-probes.sh and Task 9.
#
# It is NOT under fixtures/ because the in-JVM engines build their class model from
# DeviceFixtures.classPath (= tools/art-runner/fixtures) via Utils.getFilesWithSmaliExtension,
# which uses java.nio.file.Files.walk -- RECURSIVE. A verifier-invalid class anywhere under
# fixtures/ would be parsed into the engine class model and break DeviceFixturesTest's
# 79-class enumeration. run-batch.sh's `fixtures/*.smali` glob and DeviceFixtures.files'
# listFiles() are non-recursive and would NOT see a subdir, but the classPath walk would.
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
