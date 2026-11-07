# TIER-B-ONLY CONTROL. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# AGREEMENT CONTROL: a ZERO primitive check-cast. Both tiers should survive (null casts to
# anything), neither should crash. Anchors the divergence to the NON-ZERO value.
.class public LTierB_CheckCastZeroPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 3

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    const v2, 0x0
    check-cast v2, Ljava/lang/Object;

    const-string v1, "TIERB_CHECKCAST_SURVIVED"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
