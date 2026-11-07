# PLAN 9 TASK 15 FIX ROUND 1 -- THE ORDERING: absent COMPONENT TYPE vs NEGATIVE LENGTH on one new-array.
# Both conditions hold at once, so whichever throw arrives names the order -- the same shape
# Fx_InvokeMissingMethodNullReceiver used to settle invoke's resolution-vs-null-receiver order.
#
# WHY IT MATTERS: `core/op/NewArrayOp.kt:48-57` implements LENGTH-FIRST and says so, deliberately
# diverging from the [JVMS] 6.5 `anewarray` order (whose Linking Exceptions block precedes its
# "count is less than zero" block) on the strength of an [ART] SOURCE READING: CheckArrayAlloc
# (runtime/entrypoints/entrypoint_utils-inl.h:292-322) tests `component_count < 0` before
# GetResolvedType/ResolveType. An [ART] reading is outranked by a [DEVICE] measurement, and that op's
# own comment names the stake: "which catch clause is reachable on a both-bad new-array" --
# NegativeArraySizeException is an Exception a `catch (Exception)` DOES catch, while
# NoClassDefFoundError is an Error it does not. Opposite sides of live-versus-dead.
#
# Read WITH Fx_NewArrayAbsentType (absent type, length 1 -> NoClassDefFoundError) and
# Fx_NewArrayPresentType (present type, length 1 -> clean), which between them prove each condition
# throws on its own, so this fixture measures only the ORDER.
# Expected: THREW java.lang.NegativeArraySizeException if ART's source order holds; a
# NoClassDefFoundError instead would mean the source reading is wrong and NewArrayOp must be corrected.
.class public LFx_NewArrayAbsentTypeNegativeLength;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v1, -0x1
    new-array v0, v1, [Lcom/example/DefinitelyMissing;

    # Only reached if neither condition threw.
    return-object v0
.end method
