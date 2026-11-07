# WORKLIST ROW 24 -- "Subtype testing, in its entirety" (class B, the broadest item on
# the INHERITED validation worklist, because every type op runs this algorithm).
#
# THE CLAIM: [JLS] 4.10.3 says every array type implements `Cloneable` and
# `Serializable`, so `int[] instanceof Cloneable` must be 1. The only Android
# corroboration `DALVIK-SEMANTICS.md` cites for the whole subtype-testing section is
# AOSP `dx`'s `Merger.isPossiblyAssignableFrom` -- a BUILD TOOL, not the runtime -- and
# the section says so itself. That is precisely the shape of citation that failed three
# times in one day, and it is what this fixture replaces with a measurement.
#
# WHY THIS ONE FIRST: it is the single cheapest row-24 cell, and the current engine's
# `VirtualArray.instanceOf` answers 0 for it. So it is also a live disagreement, not
# just an unmeasured claim.
#
# VERIFIER-VALID, deliberately: v0 holds a genuine reference when `instance-of` reads it
# and the type operand is a reference type, so neither hard-failure branch of
# `method_verifier.cc`'s `instance-of` case fires. It links and runs on stock ART with
# no flags. (A fixture that is verifier-INVALID is a different kind of probe -- see
# `Fx_VerifyErrorBadReturn`.)
#
# SELF-CONTAINED: `new-array` builds the operand. Nothing is injected.

.class public LFx_IntArrayIsCloneable;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v1, 0x1
    new-array v0, v1, [I
    instance-of v0, v0, Ljava/lang/Cloneable;

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
