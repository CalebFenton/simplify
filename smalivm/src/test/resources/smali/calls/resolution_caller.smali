.class public Lcalls/resolution_caller;
.super Ljava/lang/Object;

# Call sites the invocation tests need beyond `Lcalls/caller;`. Addresses are method-local.

# @0 invoke-static of a method on a class the program never ships.
.method public static callsAbsentClass()I
    .registers 1
    invoke-static {}, Lcalls/absent;->gone()I
    move-result v0
    return v0
.end method

# Passes one argument register to `returnsSeven()I`, which takes none.
.method public static passesAnArgumentToANoArgumentCallee()I
    .registers 1
    const/4 v0, 0x6
    invoke-static {v0}, Lcalls/callee;->returnsSeven()I
    move-result v0
    return v0
.end method

# An `if-eqz` whose target is its own fall-through: with p0 unknown both arms reach the invoke, so two
# sibling paths arrive at one call site.
.method public static forksThenCallsReturnsSeven(I)I
    .registers 2
    if-eqz p0, :call
    :call
    invoke-static {}, Lcalls/callee;->returnsSeven()I
    move-result v0
    return v0
.end method

.method public static forksThenCallsNativeCallee(I)I
    .registers 2
    if-eqz p0, :call
    :call
    invoke-static {}, Lcalls/callee;->nativeCallee()I
    move-result v0
    return v0
.end method
