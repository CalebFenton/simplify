# TIER-B-ONLY PROBE. VERIFIER-INVALID BY DESIGN. NOT A CORPUS FIXTURE.
# See TierB_InstanceOfNonZeroPrim.smali for why this is a sibling of fixtures/, not under it.
#
# SMOKING-GUN VARIANT: a HIGH non-zero primitive (0x7fabc000, ~2.14GB). Chosen to land ABOVE
# the null-check window and, ideally, in unmapped memory, so the raw-as-pointer dereference
# faults at the value itself -- a plain SIGSEGV whose fault address equals the stored constant,
# with no implicit-NPE-handler involvement. Whether it is mapped is a property of this image's
# address space, recorded by the run either way.
.class public LTierB_InstanceOfHighPrim;
.super Ljava/lang/Object;

.method public static main([Ljava/lang/String;)V
    .locals 4

    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    const-string v1, "TIERB_BEGIN"
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V

    const v2, 0x7fabc000
    instance-of v3, v2, Ljava/lang/Object;

    const-string v1, "TIERB_INSTANCEOF_RESULT="
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->print(Ljava/lang/String;)V
    invoke-virtual {v0, v3}, Ljava/io/PrintStream;->println(I)V
    invoke-virtual {v0}, Ljava/io/PrintStream;->flush()V
    return-void
.end method
