# TIER-B-ONLY CONTROL. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# AGREEMENT CONTROL: a ZERO primitive. The doc's claim is that where the raw is ZERO the two
# tiers AGREE (nterp branches on the raw being zero and takes the null path; switch reads
# nullptr). Both should print TIERB_INSTANCEOF_RESULT=0 and neither should crash. This proves
# the harness works and anchors the divergence to the NON-ZERO value specifically.
.class public LTierB_InstanceOfZeroPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 4

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    const v2, 0x0
    instance-of v3, v2, Ljava/lang/Object;

    const-string v1, "TIERB_INSTANCEOF_RESULT="
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->println(I)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
