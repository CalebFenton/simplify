# Subtype matrix cell: LongArr check-cast Serializable -> expected 1 (cast succeeds).
# [JLS] JLS 4.10.3 lists exactly Cloneable and java.io.Serializable as the interfaces every array implements, and lists the primitive-component row explicitly.
# check-cast is observed by CATCHING ClassCastException: success falls through and returns boxed 1,
# a caught CCE returns boxed 0 -- the same 1/0 grammar as the instance-of cells, so the two ops are
# directly comparable.
# Cited: Tier A, -Xint, art_standalone 0_git20251009-r2.
.class public LFx_Subtype_CheckCast_LongArr_Serializable;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v1, 0x1
    new-array v0, v1, [J

    :try_start_0
    check-cast v0, Ljava/io/Serializable;
    :try_end_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_cce

    const/4 v1, 0x1
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2

    :catch_cce
    const/4 v1, 0x0
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
