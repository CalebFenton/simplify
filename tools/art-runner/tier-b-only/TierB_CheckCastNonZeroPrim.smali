# TIER-B-ONLY PROBE. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# The probe: put 0x11223344 (non-zero primitive) in a register, then `check-cast` it to Object.
# Verifier-invalid (VERIFY_ERROR_BAD_CLASS_HARD on a non-reference register), so -Xverify:none.
#   - switch interpreter (-Xint): reads nullptr -> (Object) null succeeds -> SURVIVED prints.
#   - nterp (default):            reads raw 0x11223344 -> derefs MIRROR_OBJECT_CLASS_OFFSET on it.
.class public LTierB_CheckCastNonZeroPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 3

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    # NON-ZERO primitive in v2, then check-cast it to a reference type.
    const v2, 0x11223344
    check-cast v2, Ljava/lang/Object;

    # Only reached if the cast did NOT crash (null casts to anything; a real object would too).
    const-string v1, "TIERB_CHECKCAST_SURVIVED"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
