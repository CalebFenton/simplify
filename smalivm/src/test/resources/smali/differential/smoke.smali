# The differential harness's own fixture class, as opposed to `differential/ratchet.smali` (capability
# ratchets) and `ops/*.smali` (the migrated behavioral corpus). Methods here exist to be OBSERVED through
# `NormalizedResult`, so each one is built around a specific projection claim -- a repr shape, a
# `ValueKind`, or a handler-reachability fact -- rather than around an opcode's semantics.
#
# Not every method here is swept. Three groups, and a reader must check which list a method is in:
#  - `Fixtures.newCoreSupported`: both engines run it and must AGREE.
#  - `Fixtures.intendedDivergences`: both engines run it and must NOT agree, with the adjudication recorded.
#  - neither: driven by a standalone `Fixture` built inside a single test, because the ORACLE dies on it
#    (`opcode/ThrowOp.kt:20`) and only the core's answer is observable at all.
.class public Ldifferential/smoke;
.super Ljava/lang/Object;

.method public static returnFive()I
    .registers 1
    const/4 v0, 0x5
    return v0
.end method

.method public static nestedArray()[[I
    .registers 4
    const/4 v0, 0x1
    new-array v0, v0, [[I

    const/4 v1, 0x2
    new-array v1, v1, [I

    const/4 v2, 0x0
    const/4 v3, 0x1
    aput v3, v1, v2

    const/4 v2, 0x1
    const/4 v3, 0x2
    aput v3, v1, v2

    const/4 v2, 0x0
    aput-object v1, v0, v2

    return-object v0
.end method

.method public static newObject()Ljava/lang/Object;
    .registers 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

.method public static selfReferentialArray()[Ljava/lang/Object;
    .registers 2
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0
    aput-object v0, v0, v1

    return-object v0
.end method

# The SHARED-BUT-ACYCLIC half of the cycle guard, and the case `selfReferentialArray()` above cannot reach:
# ONE inner array reachable from TWO sibling slots of the outer one. There is no cycle, so the repr must
# render the inner array FULLY BOTH TIMES -- `[[7], [7]]`, never `[[7], [...]]`.
#
# What makes that true is the `visiting.remove(...)` in the `finally` of BOTH engines' recursive repr
# (`NewCoreVm.heapRepr`, keyed by heap id; `ResultProjection.arrayRepr`, keyed by host identity). Deleting
# either one turns the guard from "am I inside my own rendering" into "have I ever been rendered", which is
# a different and wrong question -- and until this fixture existed, deleting it passed every test in the
# suite, because `selfReferentialArray()` re-enters the SAME array and so cannot tell the two apart.
#
# AND THIS FIXTURE CATCHES ONLY THE CORE'S DELETION -- measured, 2 dead for the core and 0 for the oracle.
# The oracle deep-copies host arrays per node (`ExecutionState.kt:483`), so by the time this method returns
# `outer[0] !== outer[1]` and its guard is never re-entered: its `[[7], [7]]` is correct for a different
# reason than the core's. The oracle's half is pinned by a DIRECT `ResultProjection.reprOf` call inside
# `anArrayReachableFromTwoSiblingSlotsRendersFullyBothTimesOnBothEngines`. Do not read the paragraph above
# as "and now both are covered".
#
# v2 holds index 0 for both the inner store and the first outer store, which is why there is no second
# `const/4 v2, 0x0`: fewer instructions is fewer addresses to re-derive, and 7 vs 0 in the inner slot means
# a repr that lost the store would read `[[0], [0]]` rather than looking correct.
.method public static sharedInnerArray()[[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [[I

    const/4 v1, 0x1
    new-array v1, v1, [I

    const/4 v2, 0x0
    const/4 v3, 0x7
    aput v3, v1, v2

    aput-object v1, v0, v2
    const/4 v2, 0x1
    aput-object v1, v0, v2

    return-object v0
.end method

# The first fixture in `Fixtures.newCoreSupported` that projects `ValueKind.NULL`, which until now no swept
# method could produce at all: `Value.Known(null, T)` is the only thing that projects it, and a fresh
# reference array's elements are exactly that (`HeapStore.defaultElementValue`). `const/4 v0, 0x0` --
# Dalvik's null IDIOM -- is `Known(0, I)` and projects KNOWN, so the idiom is not a substitute.
#
# `ops/aget_test.smali`'s `getObjectFromFreshArray()V` asserts the same SEMANTICS core-only, and does not
# satisfy the sweep's coverage guard: the sweep runs `Fixtures.newCoreSupported` and that file is not in it.
#
# v2 rather than v0 for the destination so the three registers are distinct: with `aget-object v0, v0, v1`
# the destination and the array collapse to one number and a dest/array transposition would be invisible.
.method public static nullElementFromFreshArray()Ljava/lang/Object;
    .registers 3
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0
    aget-object v2, v0, v1

    return-object v2
.end method

# `fill-array-data` over an array the METHOD allocates, which is what makes it sweepable at all.
# `NewCoreDifferentialTest.payloadPseudoAddressesAreExcludedFromBOTHEnginesReachedSets` has to seed a HOST
# array into a register, and the two engines project that asymmetrically (the oracle keeps the live array in
# the register; the core sees a raw that is not a `HeapRef` and takes its unmodellable-fill row), so it
# observes no registers at all and isolates the coverage claim. Allocating with `new-array` first gives BOTH
# engines a real array of their own kind, so the values are comparable and the payload-exclusion convention
# is exercised by the ordinary sweep rather than only by that one hand-built test.
#
# Three elements into a length-3 array: exactly filled, so no bounds exception is feasible on either engine
# ([BC]: the table "must contain no more elements than will fit in the array"). 1/2/3 rather than 0/0/0 --
# a fresh `[I` is already all zeros, so a zero payload could not tell a fill from a no-op.
.method public static fillArrayDataRoundTrip()[I
    .registers 1
    const/4 v0, 0x3
    new-array v0, v0, [I

    fill-array-data v0, :data

    return-object v0

    :data
    .array-data 4
        0x1
        0x2
        0x3
    .end array-data
.end method

# ------------------------------------------------------------------------------------------------------
# The three `DivergenceShape.NEW_CORE_MORE_PRECISE` fixtures. Each stores or fills an array where ONE
# operand is unprovable, and the engine being replaced answers by marking the WHOLE ARRAY unknown
# (`opcode/APutOp.kt:40-42`, `opcode/FilledNewArrayOp.kt:46-47`). The core keeps the array `Known` and
# marks only what it cannot prove, so its answer is strictly lower in the lattice.
#
# THE UNPROVABLE OPERAND IS A METHOD PARAMETER, NOT A SEED, and that is forced rather than stylistic:
# `Fixture.seededRegisters` takes KNOWN values only, deliberately, so that a divergence in the seeding
# cannot masquerade as a divergence in the op. Both engines seed a declared parameter to "unknown of the
# declared type" natively. See `SmaliVmUnderTest.kt`'s `Fixture` KDoc.
#
# Each array is length TWO with the store landing on ONE slot, so the sharper answer is visible as a
# per-element fact: an engine that marked every element unknown would render `[?, ?]` where the core
# renders `[?, 0]`. A length-1 array could not tell those apart.
# ------------------------------------------------------------------------------------------------------

# UNKNOWN VALUE. The array is provably a fresh `[I`, the index is provably 0, and only the STORED value is
# unprovable -- so the write landed on exactly slot 0 and nothing else changed.
.method public static aputUnknownValue(I)[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x0
    aput p0, v0, v1

    return-object v0
.end method

# THE SAME OVER-FORK, MADE VISIBLE AS ADDRESSES (Plan 8 Task 11, closing the `## Unowned and cross-cutting`
# aput row). `opcode/APutOp.kt:38-41` leaves `mayThrow = true` on the unknown-operand branch, so the
# oracle's `finishOp(true)` appends an NPE and a bounds exception even though this array is a provably
# fresh non-null `[I` and the index is provably 0 -- both INFEASIBLE. Here a `catch (Exception)` turns that
# blanket fork into reached HANDLER ADDRESSES, so the divergence is `ORACLE_MISJUDGES_FEASIBILITY`
# (`oracleWronglyExplores`) rather than the `unhandledExceptions` the handler-less `aputUnknownValue` shows
# (`theOraclesBlanketAputForkIsVisibleAsUnhandledExceptionsRatherThanAsAddresses`). The core proves both
# infeasible and never schedules the handler, so it reaches strictly fewer addresses.
#
# `move-exception` targets v2, which NOBODY observes -- required, because that shape's branch asserts
# `oracleResult.registers == coreResult.registers`, and the catch slot holds the caught exception on the
# oracle's forked paths and nothing on the core (Task 9 Step 4's construction, and why
# `remIntUnknownDivisorWithCatch`/`unknownCastMayThrow` bind their catch slots to an unobserved register).
# The OBSERVED register is v1 = `Known(0, I)` on every returning path of BOTH engines, so it agrees.
#
# `.registers 4`, p0 = v3 (the unknown stored value). Addresses derived from the encoding, not assumed:
# `const/4` @0; `new-array` (22c, 2 units) @1; `const/4` @3; `aput` (23x, 2 units) @4 [try range];
# `return-void` @6; `move-exception` (11x) @7 [:catch]; `goto` (10t) @8 -> @6.
.method public static aputUnknownValueWithCatch(I)V
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x0

    :try_start
    aput p0, v0, v1
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :catch

    :return
    return-void

    :catch
    move-exception v2

    goto :return
.end method

# UNKNOWN INDEX. The mirror case: the stored value is provably 7 and the INDEX is unprovable, so the write
# landed on exactly one slot and we cannot say which -- every slot afterwards is either its old value or 7,
# which is what `join` says. The core also forks the bounds exception here, which the value assertion does
# not see; the registry entry records that separately.
.method public static aputUnknownIndex(I)[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x7
    aput v1, v0, p0

    return-object v0
.end method

# UNKNOWN ELEMENT of a `filled-new-array`. Same defect one op over, and this one has no array, index or
# count operand at all -- the array is created by the instruction -- so nothing forks on either engine and
# the divergence is PURELY the value. That makes it the cleanest of the three.
#
# `move-result-object` is required: the constructed array goes to the result slot, which no
# `ExecutionGraph` query exposes ([BC]: "must be moved to a register with an immediately subsequent
# move-result-object instruction").
.method public static filledNewArrayUnknownElement(I)[I
    .registers 3
    const/4 v0, 0x1

    filled-new-array {v0, p0}, [I
    move-result-object v1

    return-object v1
.end method

# A REFERENCE COMPONENT, which the sweep had no fixture for at all until the whole-branch review's C1 --
# the reason the C1 value defect was differentially invisible for a whole plan (and the oracle shares it,
# so agreement here would have been coincidental on both sides). Two elements, deliberately of different
# kinds, because each pins a different half:
#
#  - v0 is a real reference (`const-string`), which is the `verbatim` half and also what makes the ORACLE
#    coarse: `opcode/FilledNewArrayOp`'s loop tests `value.raw is Number`, a String is not one, so it
#    discards the whole array as `Value.unknown("[I")`. That UNKNOWN-vs-KNOWN gap is what the
#    NEW_CORE_MORE_PRECISE branch asserts.
#  - v1 is `const/4 0x0`, Dalvik's ONLY spelling of a null literal, i.e. this method is `new String[]{"x",
#    null}` -- verifier-VALID, ordinary d8 output. ART fills a reference component with
#    `GetVRegReference` (`interpreter_common.cc:1463-1464`), which yields nullptr for a register whose
#    last write was primitive, so the element is `null` and the core's repr is `[x, null]`. Before C1 was
#    fixed the core answered `[x, 0]` -- a boxed Integer under a reference component.
#
# The repr is NOT what the shape's branch compares (it asserts ValueKind, deliberately -- see
# DivergenceShape.NEW_CORE_MORE_PRECISE), so the null element is pinned by NAME in
# `NewCoreDifferentialTest.aReferenceComponentFilledNewArraySpellsItsNullElementAsDalvikReadsIt`.
#
# `.registers 3` so v0/v1 are the elements and v2 is the observed destination; `()V` rather than a
# `[Ljava/lang/String;` return, because the oracle assigns the result `[I` regardless of the declared type
# and a return-type mismatch would be a second, unrelated difference in the same fixture.
.method public static filledNewArrayOfStringsWithNullElement()V
    .registers 3
    const-string v0, "x"

    const/4 v1, 0x0

    filled-new-array {v0, v1}, [Ljava/lang/String;
    move-result-object v2

    return-void
.end method

# ------------------------------------------------------------------------------------------------------
# HANDLER REACHABILITY for the NPE Plan 7 Task 12 synthesizes, projected through the differential harness
# rather than through `ExecutionGraph` directly. CORE-ONLY: the oracle dies at `opcode/ThrowOp.kt:20`
# casting the register's raw to `Throwable`, so neither method is in `Fixtures` at all and both are driven
# by a standalone `Fixture` inside one test.
#
# FOUR clauses, exactly ONE of which matches, and that is a deliberate design rather than a shortcut.
# `resolveHandler` takes the FIRST matching clause in declaration order, so a fixture declaring both
# `Ljava/lang/Exception;` and `Ljava/lang/RuntimeException;` would have one of them dead for SHADOWING
# reasons rather than for TYPE reasons -- which is not the claim. With one matching clause the reached set
# does not depend on clause order at all, so this cannot pass or fail for a reason about dexlib2's
# `.catch` ordering.
#
# The other two live cases are pinned elsewhere and are deliberately not duplicated here:
# `catch (RuntimeException)` by `Lspine/spine;->throwNullConstTwoHandlers()` (ThrowRoutingTest), and
# a covering catch-all by `Lspine/spine;->throwCatchAll()`.
# ------------------------------------------------------------------------------------------------------

# `const/4 v0, 0x0` -- Dalvik's null idiom ([BC]: "In terms of bitwise representation, (Object) null ==
# (int) 0"). A definite, message-less NullPointerException, so:
#   - `catch (Exception)` at :an_exception is LIVE (an NPE is an Exception);
#   - `catch (IOException)`, `catch (Error)` and `catch (IllegalStateException)` are DEAD -- all three were
#     reported LIVE before Task 12, which told a deobfuscator three blocks are reachable that no device can
#     enter;
#   - and nothing escapes, which is the fourth thing that used to be reported and is now correctly absent.
.method public static throwNullIdiomFourClauses()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x0
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_an_npe
    .catch Ljava/lang/Error; {:try_start .. :try_end} :not_an_error
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :not_an_ise
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :an_exception

    :not_an_npe
    const-string v0, "io"
    return-object v0

    :not_an_error
    const-string v0, "error"
    return-object v0

    :not_an_ise
    const-string v0, "ise"
    return-object v0

    :an_exception
    move-exception v0
    return-object v0
.end method

# The SAME method with one nibble changed -- 0x5 instead of 0x0 -- and the whole claim is that the outcome
# does not depend on that nibble. ART's `THROW()` fetches its operand with `GetVRegReference`, `ShadowFrame`
# keeps a PARALLEL reference array, and every primitive write clears the matching slot, so ANY
# primitive-typed register reads as the null reference whatever its value (see `Value.readsAsNullReference`
# and `docs/DALVIK-SEMANTICS.md`'s `throw` section for the verified chain).
#
# So the reached set and the unhandled projection must be IDENTICAL to the `0x0` method's. Before Task 12
# this method's `0x5` forked all four clauses plus the escape.
.method public static throwNonZeroConstFourClauses()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x5
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_an_npe
    .catch Ljava/lang/Error; {:try_start .. :try_end} :not_an_error
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :not_an_ise
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :an_exception

    :not_an_npe
    const-string v0, "io"
    return-object v0

    :not_an_error
    const-string v0, "error"
    return-object v0

    :not_an_ise
    const-string v0, "ise"
    return-object v0

    :an_exception
    move-exception v0
    return-object v0
.end method
