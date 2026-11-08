.class public Lcalls/callee;
.super Ljava/lang/Object;

# The callee half of the invocation fixtures. Addresses are method-local: adding whole methods is
# address-safe, editing instructions inside one shifts that method, and tests pin exact addresses.
#
# `returnsSeven()I` must keep `const/4` at @0 and `return` at @1, the same layout as
# `Lcalls/caller;->callsCalleeAndReturnsItsOwn()I`. The overlap is the collision fixture for
# InvokerTest: only overlapping addresses distinguish per-activation keying from a bare-address map.

# @0 const/4 (11n, 1 unit), @1 return (11x, 1 unit)
.method public static returnsSeven()I
    .registers 1
    const/4 v0, 0x7
    return v0
.end method

# return-void: the smallest callee there is, and the one that proves a caller resumes even
# with no value to carry back.
.method public static returnsNothing()V
    .registers 1
    return-void
.end method

# Two distinct return nodes with no heap or class write on either path.
.method public static returnsFromEitherPath(I)V
    .registers 1
    if-eqz v0, :zero
    return-void
    :zero
    return-void
.end method

# takes one narrow argument and returns it unchanged. Parameters live in the highest
# registers, so with `.registers 1` the single parameter is v0 (p0).
.method public static identityInt(I)I
    .registers 1
    return v0
.end method

# A wide parameter occupies two registers. With `.registers 2` the long parameter is the pair
# (v0, v1) = p0.
.method public static identityLong(J)J
    .registers 2
    return-wide v0
.end method

# allocates a TWO-element array, so the object it puts on the heap is distinguishable from
# the caller's one-element array by content as well as by id.
.method public static allocatesTwoElementArray()[I
    .registers 2
    const/4 v0, 0x2
    new-array v1, v0, [I
    return-object v1
.end method

# Unbounded self-recursion; `Limits.maxCallDepth` turns it into a truncated exploration.
.method public static recursesForever()I
    .registers 1
    invoke-static {}, Lcalls/callee;->recursesForever()I
    move-result v0
    return v0
.end method

# A looping callee, so a per-activation visit budget has something to trip. The label is on the nop
# because Dalvik rejects a goto with a zero offset.
.method public static spinsForever()V
    .registers 1
    :loop
    nop
    goto :loop
.end method

# A callee whose throw escapes it, for callers that do or do not catch it.
.method public static throwsUncaught()I
    .registers 1
    const/4 v0, 0x0
    throw v0
.end method

# Declares a STRONG side effect and still returns normally; a caller must not report NONE.
#
# The array register holds a known non-HeapRef reference: non-null (no NPE fork) and untracked (no
# bounds fork), so the write is STRONG with a plain fall-through. `const/4 v1, 0x0` would not do: a
# primitive-typed register reads as null, a definite NullPointerException with no side effect.
.method public static strongEffect()V
    .registers 3
    const/4 v0, 0x0
    const-string v1, "not-an-array"
    const/4 v2, 0x0
    aput v0, v1, v2
    return-void
.end method

# No body: unless an emulator is registered, the call continues opaquely with an Unknown result.
.method public static native nativeCallee()I
.end method
