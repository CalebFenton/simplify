.class public Lcalls/caller;
.super Ljava/lang/Object;

# The caller half of the invocation fixtures. Every `invoke-static` is a real 35c instruction
# (three code units). Addresses are method-local: whole-method insertions are address-safe,
# editing instructions inside a method is not, and tests pin exact addresses.

# Collision fixture. Addresses, derived from the assembled instructions:
# @0 const/4 (11n, 1 unit), @1 invoke-static (35c, 3 units), @4 return (11x, 1 unit).
#
# So this method has an op at address 0 AND an op at address 1, and so does
# `Lcalls/callee;->returnsSeven()I` -- whose op at 1 is its `return`. Two things become
# observable that a non-overlapping pair could not show:
#   - address 0 holds `const/4 v0, 0x1` here and `const/4 v0, 0x7` there, so one shared pile
#     mixes two different program points that write the same register different values;
#   - the callee's returning node sits at address 1, which this method also has, so under a
#     bare-address key it votes in this method's `terminatingConsensus` -- inventing
#     agreement about a value this method never returned.
#
# The return value is the caller's own constant (v0 = 1), not the callee's (7): equal values
# would make a contaminated join indistinguishable from a clean one.
.method public static callsCalleeAndReturnsItsOwn()I
    .registers 1
    const/4 v0, 0x1
    invoke-static {}, Lcalls/callee;->returnsSeven()I
    return v0
.end method

# the ordinary shape: call, move the result, return it. @0 invoke-static (3 units),
# @3 move-result (1 unit), @4 return.
.method public static callsCalleeAndReturnsItsResult()I
    .registers 1
    invoke-static {}, Lcalls/callee;->returnsSeven()I
    move-result v0
    return v0
.end method

# a void callee, so the caller has no `move-result` at all.
.method public static callsVoidCallee()I
    .registers 1
    const/4 v0, 0x3
    invoke-static {}, Lcalls/callee;->returnsNothing()V
    return v0
.end method

# The callee forks but changes no heap or class state on either return path. Persistent-state tests
# use it to prove that call partition merge preserves the shared store roots.
.method public static callsTwoPathVoidCallee(I)V
    .registers 1
    invoke-static {v0}, Lcalls/callee;->returnsFromEitherPath(I)V
    return-void
.end method

# passes ONE narrow argument. @0 const/4, @1 invoke-static, @4 move-result, @5 return.
.method public static passesAnArgument()I
    .registers 1
    const/4 v0, 0x6
    invoke-static {v0}, Lcalls/callee;->identityInt(I)I
    move-result v0
    return v0
.end method

# passes a wide argument: two argument registers in the instruction, one parameter.
# @0 const-wide/16 (21s, 2 units), @2 invoke-static (35c, 3
# units), @5 move-result-wide, @6 return-wide.
.method public static passesAWideArgument()J
    .registers 2
    const-wide/16 v0, 0x2a
    invoke-static {v0, v1}, Lcalls/callee;->identityLong(J)J
    move-result-wide v0
    return-wide v0
.end method

# Heap-id fixture. The caller allocates, then the callee allocates; their `HeapRef`s must be
# distinct. `HeapRef` is a structural data class, so a callee frame minting a fresh id space
# from 0 would give both objects id 0 and heap consensus would treat them as one.
#
# Both refs are observable from the caller's returning node: v1 keeps the caller's array and
# v0 receives the callee's.
.method public static allocatesThenCalleeAllocates()[I
    .registers 2
    const/4 v0, 0x1
    new-array v1, v0, [I
    invoke-static {}, Lcalls/callee;->allocatesTwoElementArray()[I
    move-result-object v0
    return-object v0
.end method

# Recursive-entry fixture: a method that calls itself, so a second activation of the entry method
# exists. Seeded with v1 non-zero it recurses once; the nested activation sees v1 = 0 and takes
# :base, which the entry activation never reaches.
#   - :base executes only in the nested activation, so `wasReached` scoped to the entry activation
#     would falsely prove it dead on a `Complete` run.
#   - the activations return different values (5 from :base, 6 after the add), so unioning the
#     terminating consensus would answer Unknown where the answer is the constant 6.
#   - v1 differs between them (1 vs 0), so per-address consensus at @0 must answer Unknown.
#
# Addresses, derived from the assembled instructions and not assumed: @0 if-eqz (21t, 2 units),
# @2 const/4 (11n, 1), @3 invoke-static (35c, 3), @6 move-result (11x, 1), @7 add-int/lit8 (22b, 2),
# @9 return (11x, 1), then :base at @10 const/4 and @11 return. The test reads :base off the `if-eqz`'s
# resolved branch target rather than trusting these numbers.
.method public static recursesOnceThenAddsOne(I)I
    .registers 2
    if-eqz v1, :base
    const/4 v0, 0x0
    invoke-static {v0}, Lcalls/caller;->recursesOnceThenAddsOne(I)I
    move-result v0
    add-int/lit8 v0, v0, 0x1
    return v0
    :base
    const/4 v0, 0x5
    return v0
.end method

# calls the self-recursive callee, so recursion starts one frame down: the depth bound is about
# the call stack, not the entry method.
.method public static callsRecursiveCallee()I
    .registers 1
    invoke-static {}, Lcalls/callee;->recursesForever()I
    move-result v0
    return v0
.end method

# calls the looping callee: the callee's own visit budget must still be spent.
.method public static callsSpinningCallee()I
    .registers 1
    const/4 v0, 0x2
    invoke-static {}, Lcalls/callee;->spinsForever()V
    return v0
.end method

# the caller has a handler that catches what the callee throws, so the throw neither escapes
# this method nor lets the normal path resume.
.method public static callsThrowingCalleeWithCatch()I
    .registers 1
    :try_start
    invoke-static {}, Lcalls/callee;->throwsUncaught()I
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    move-result v0
    return v0
    :handler
    const/4 v0, 0x5
    return v0
.end method

.method public static callsThrowingCalleeWithCompetingCatch()I
    .registers 1
    :try_start
    invoke-static {}, Lcalls/callee;->throwsUncaught()I
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :npe
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :throwable
    move-result v0
    return v0
    :throwable
    const/4 v0, 0x6
    return v0
    :npe
    const/4 v0, 0x5
    return v0
.end method

# calls a callee that declares a STRONG side effect. This caller's own ops declare nothing, so
# without propagation the method would report NONE.
.method public static callsStrongEffectCallee()I
    .registers 1
    const/4 v0, 0x4
    invoke-static {}, Lcalls/callee;->strongEffect()V
    return v0
.end method

# calls a method with no body: no backend runs it, so the call continues opaquely rather than
# building a MethodView for a bodyless method.
.method public static callsNativeCallee()I
    .registers 1
    invoke-static {}, Lcalls/callee;->nativeCallee()I
    move-result v0
    return v0
.end method

# calls a method that does not exist on a class that does: guest NoSuchMethodError.
.method public static callsAbsentMethod()I
    .registers 1
    invoke-static {}, Lcalls/callee;->noSuchMethod()I
    move-result v0
    return v0
.end method
