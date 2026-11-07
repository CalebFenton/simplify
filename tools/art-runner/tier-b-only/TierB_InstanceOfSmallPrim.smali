# TIER-B-ONLY PROBE. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# SMOKING-GUN VARIANT: a SMALL non-zero primitive (0x2a = 42). Small enough that the FIRST
# dereference lands at a low address, so if nterp uses the raw register value as the object
# pointer, the fault address is the value itself (0x2a) -- direct evidence the raw bits are the
# pointer, not a second-level garbage deref. 0x2a < the null-check window, so ART's fault
# handler will treat it as an implicit NullPointerException check on address 0x2a.
.class public LTierB_InstanceOfSmallPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 4

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    const v2, 0x2a
    instance-of v3, v2, Ljava/lang/Object;

    const-string v1, "TIERB_INSTANCEOF_RESULT="
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->println(I)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
