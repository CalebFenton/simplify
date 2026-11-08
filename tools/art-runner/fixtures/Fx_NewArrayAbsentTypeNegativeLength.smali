# Ordering: an absent component type AND a negative length on one new-array. ART's CheckArrayAlloc
# tests the count before resolving the type, the reverse of [JVMS] 6.5 anewarray's listed order; the
# answer decides whether a catch (Exception) is reachable (NegativeArraySizeException is an Exception,
# NoClassDefFoundError an Error). Read with Fx_NewArrayAbsentType and Fx_NewArrayPresentType, which
# show each condition throws on its own.
# Expected: THREW java.lang.NegativeArraySizeException.
.class public LFx_NewArrayAbsentTypeNegativeLength;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v1, -0x1
    new-array v0, v1, [Lcom/example/DefinitelyMissing;

    # Only reached if neither condition threw.
    return-object v0
.end method
