# WORKLIST ROW 22 -- check-cast naming an ABSENT type. Lcom/example/DefinitelyMissing; resolves
# against nothing. Per ART's CHECK_CAST handler the type is resolved BEFORE the null-source shortcut,
# so even a null source drives resolution, which fails. Source is null to prove the throw comes from
# resolution and not from a genuine ClassCastException. Left to propagate.
# Read WITH the control Fx_CheckCastPresentType (java.lang.String, passes).
# Expected: THREW java.lang.NoClassDefFoundError (measured; resolution precedes the null pass-through).
.class public LFx_CheckCastAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    const/4 v0, 0x0
    check-cast v0, Lcom/example/DefinitelyMissing;

    # Only reached if check-cast passed a null through without resolving.
    return-object v0
.end method
