# ROW 24 subtype matrix cell: IntArr instance-of ObjectArr -> expected 0 (not an instance).
# [JLS] A primitive-component array is invariant and is NOT a reference array: the array-subtyping rule requires BOTH component types to be reference types, so this is not an Object[] (JLS 4.10.3).
# Self-contained: new-array builds the operand; the 0/1 is boxed with Integer.valueOf so the device
# and the oracle both observe it (these box, so they do NOT run on the clean core).
# Cited: Tier A, -Xint, art_standalone 0_git20251009-r2.
.class public LFx_Subtype_InstanceOf_IntArr_ObjectArr;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v1, 0x1
    new-array v0, v1, [I
    instance-of v0, v0, [Ljava/lang/Object;

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
