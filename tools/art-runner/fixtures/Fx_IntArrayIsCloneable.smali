# Every array type implements `Cloneable` and `Serializable` ([JLS] 4.10.3), so
# `int[] instanceof Cloneable` is 1. The only Android citation for subtype testing is AOSP `dx`'s
# `Merger.isPossiblyAssignableFrom`, a build tool, so this measures the runtime.
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
