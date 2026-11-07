# ROW 24 subtype matrix cell: StringArr instance-of Object -> expected 1 (is an instance).
# [JLS] Every array is a reference type and Object is the supertype of all reference types (JLS 4.10.3: 'Object >1 P[]' for a primitive component P, and the reference-component row likewise).
# Self-contained: new-array builds the operand; the 0/1 is boxed with Integer.valueOf so the device
# and the oracle both observe it (these box, so they do NOT run on the clean core).
# Cited: Tier A, -Xint, art_standalone 0_git20251009-r2.
.class public LFx_Subtype_InstanceOf_StringArr_Object;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v1, 0x1
    new-array v0, v1, [Ljava/lang/String;
    instance-of v0, v0, Ljava/lang/Object;

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
