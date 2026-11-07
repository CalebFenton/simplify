# TIER-B-ONLY SANITY ANCHOR. VERIFIER-VALID. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# SANITY: a REAL object, `instance-of Object` -> 1, on BOTH tiers, no crash. This one is
# actually verifier-VALID (a real reference in the register), so it also confirms the op and
# harness behave normally when fed a legitimate reference -- the baseline the divergence departs
# from. It still lives here, quarantined, so it never enters the corpus enumeration.
.class public LTierB_InstanceOfRealObject;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 4

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    new-instance v2, Ljava/lang/Object;
    invoke-direct {v2}, Ljava/lang/Object;-><init>()V
    instance-of v3, v2, Ljava/lang/Object;

    const-string v1, "TIERB_INSTANCEOF_RESULT="
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->println(I)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
