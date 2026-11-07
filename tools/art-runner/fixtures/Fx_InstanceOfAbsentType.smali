# WORKLIST ROW 20 -- instance-of naming an ABSENT type. Lcom/example/DefinitelyMissing; resolves
# against nothing. Per ART's INSTANCE_OF handler the type is resolved BEFORE the null-source shortcut,
# so even a null source drives resolution, which fails. Source is null to prove the throw comes from
# resolution and not from the object. Left to propagate.
# Read WITH the control Fx_InstanceOfPresentType (java.lang.String, answers 1).
# Expected: THREW java.lang.NoClassDefFoundError (measured; resolution precedes the null shortcut).
.class public LFx_InstanceOfAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v0, 0x0
    instance-of v1, v0, Lcom/example/DefinitelyMissing;

    # Only reached if instance-of answered without resolving (would be 0 for a null source).
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
