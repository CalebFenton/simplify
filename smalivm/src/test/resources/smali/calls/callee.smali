.class public Lcalls/callee;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# The CALLEE half of Plan 9 Task 14's fixtures. Addresses are METHOD-LOCAL, so adding or
# reordering whole methods here is address-safe; editing instructions INSIDE one shifts the
# rest of that method and several tests pin exact addresses.
#
# The address LAYOUT of `returnsSeven()I` is load-bearing and must not change: address 0
# holds a `const/4` and address 1 holds the `return`, which is exactly the layout
# `Lcalls/caller;->callsCalleeAndReturnsItsOwn()I` also has at 0 and 1. That coincidence IS
# the collision fixture -- see InvokerTest's collision tests. A caller-and-callee pair whose
# addresses did not overlap could not tell per-activation keying from a bare-address map.
# ---------------------------------------------------------------------------------------

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

# takes one narrow argument and returns it unchanged. Parameters live in the HIGHEST
# registers, so with `.registers 1` the single parameter is v0 (p0).
.method public static identityInt(I)I
    .registers 1
    return v0
.end method

# a WIDE parameter, which occupies TWO registers -- the trap the plan's Global Constraints
# warn about (`getParameterTypeNames()` sums to 2 for J/D and 1 otherwise). With
# `.registers 2` the long parameter is the pair (v0, v1) = p0.
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

# unbounded self-recursion: the first thing invocation makes possible and the reason
# `Limits.maxCallDepth` exists. Without the bound this hangs the suite (or overflows the
# host stack) instead of reporting a truncated exploration.
.method public static recursesForever()I
    .registers 1
    invoke-static {}, Lcalls/callee;->recursesForever()I
    move-result v0
    return v0
.end method

# a callee that LOOPS, so a per-activation visit budget still has something to trip. Without this
# fixture "each activation gets its own budget" could be satisfied by never counting at all -- the
# caller-and-callee test would pass with the whole bound deleted. The label is on the nop, not on the
# goto: a goto with a zero offset is an encoding Dalvik rejects.
.method public static spinsForever()V
    .registers 1
    :loop
    nop
    goto :loop
.end method

# a callee that THROWS instead of returning. Cross-frame unwind is Plan 12's, so the new
# core must abort this path rather than report the throw as escaping the CALLER -- which
# would drop the caller's own handlers as feasible paths.
.method public static throwsUncaught()I
    .registers 1
    const/4 v0, 0x0
    throw v0
.end method

# a callee that declares a STRONG side effect AND still returns normally. The caller must not report
# NONE for a method that calls this.
#
# The array register holds a `Known` REFERENCE whose raw is not a HeapRef, which is `APutOp`'s row 3:
# provably non-null (so no NPE fork), no tracked array behind it (so no length and no bounds fork), and
# therefore a write that landed somewhere unmodellable -- `STRONG` with a plain fall-through and nothing
# thrown. The obvious `const/4 v1, 0x0` would NOT do: a primitive-typed register reads as a null
# reference, which is a DEFINITE NullPointerException and no side effect at all.
.method public static strongEffect()V
    .registers 3
    const/4 v0, 0x0
    const-string v1, "not-an-array"
    const/4 v2, 0x0
    aput v0, v1, v2
    return-void
.end method

# no implementation at all: Plan 11's backends own it, and until they exist the Invoker must
# refuse conservatively rather than crash trying to build a MethodView for a bodyless method.
.method public static native nativeCallee()I
.end method
