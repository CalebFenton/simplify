# Fixtures for the THREE-way `Value.Top` conflation (Plan 8 Task 5): "never assigned", "the paths
# disagree", and a `Value.Top` that genuinely lives in a frame. Every method here exists so that some
# pair of `ExecutionGraph` queries answers differently about two registers whose `consensus` is the
# SAME `Value.Top` -- so the file is organised by which pair, not by opcode.
#
# It lives in `differential/` rather than in `ops/` because one of these methods is also a projection
# fixture (`NewCoreVm.normalize` maps `Value.Top` to a `ValueKind`, and which one it maps to is now a
# function of these queries). It is deliberately NOT in `ops/if_test.smali`: 71 migrated tests depend on
# that file's address map, and its two newest methods are differential fixtures already.
.class public Ldifferential/branch;
.super Ljava/lang/Object;

# (a) THE PATHS DISAGREE, and (b) NOBODY EVER TOUCHED IT -- at the SAME reached address, so one
# `consensus` call answers `Top` for both and only the new query tells them apart.
#
# `.registers 3` for one `I` parameter puts the parameter in v2 and leaves v0/v1 as locals:
#   v0 -- assigned on BOTH arms, with types the join calls incompatible (`I` against
#         `Ljava/lang/String;`), so `consensus(6, 0)` is `Top` because the paths DISAGREE.
#   v1 -- assigned by nothing at all, so `consensus(6, 1)` is `Top` because it was NEVER ASSIGNED.
#   v2 -- the parameter, seeded `Unknown(I)` by `Frame.entryFrame`, which then CLEARS `assigned`. So
#         it is held-but-not-assigned: the case that makes `Frame.registersAssigned()` the wrong raw
#         material for this query and `Frame.peek` the right one.
#
# Addresses: `if-eqz` (21t, 2 units) at 0; `const/4` at 2; `goto` at 3; `const-string` (21c, 2 units)
# at 4; `return-void` at 6. Both arms reach 6, so the join there is over two nodes and is not
# idempotent -- which every `if` fixture in `ops/if_test.smali` fails to be, because they all put a
# `nop` on the fall-through.
.method public static disagreeingArms(I)V
    .registers 3

    if-eqz v2, :other

    const/4 v0, 0x1

    goto :end

    :other
    const-string v0, "x"

    :end
    return-void
.end method

# (c) AN ADDRESS NO PATH EVER ATTEMPTED, next to (b) FOR THE SAME REGISTER, so the two are
# distinguishable only by the third reading.
#
# `const/4 v0, 0x0` makes the predicate provably zero, so `if-eqz` emits ONE transition and address 3
# is never even attempted. v1 is therefore:
#   at address 4 (reached)        -- never assigned
#   at address 3 (never attempted) -- no path to ask at all
# and `consensus` answers `Top` for both.
#
# Addresses: `const/4` at 0; `if-eqz` (2 units) at 1; `const/4` at 3; `return-void` at 4. The branch
# offset is +3, so [BC]'s "the branch offset must not be 0" holds.
.method public static provablyZeroSkipsAnAddress()V
    .registers 2

    const/4 v0, 0x0

    if-eqz v0, :taken

    const/4 v1, 0x1

    :taken
    return-void
.end method

# A `Value.Top` THAT GENUINELY LIVES IN A FRAME (`MoveOp`'s `Source.Register` arm reads through
# `Frame.read`, which is `peek(r) ?: Value.Top`), against a register nothing ever wrote. All three
# registers below answer `Top` from `consensus` AND `{Top}` from `registerValues`, so this is the
# method that shows the new query is not a re-spelling of either:
#   v0 -- HELD: it holds a real `Value.Top` that `move` put there.
#   v1 -- NEVER ASSIGNED.
#   v2 -- NEVER ASSIGNED, and the register `move` read to produce v0's `Top`.
#
# `.registers 3` on a static `()V`, so there is no parameter and every register starts empty.
# Addresses: `move` (12x, 1 unit) at 0; `return-void` at 1.
.method public static moveFromAnUntouchedRegister()V
    .registers 3

    move v0, v2

    return-void
.end method

# (d) A METHOD THAT NEVER RETURNS NORMALLY. Its only path throws, so the returning-node set is empty
# and `terminatingConsensus` answers `Top` for every register -- the same observable as "the returning
# paths agreed on nothing". `didAnyPathReturn` is the fact that separates them, and it is register-free
# on purpose: it stays answerable for a method with no registers at all.
.method public static neverReturnsNormally(Ljava/lang/RuntimeException;)V
    .registers 2

    throw v1
.end method

# ==================================================================================================
# Added by Plan 8 Task 11 (differential coverage). These are BRANCH fixtures for the sweep, not for the
# `Value.Top` conflation above, so read them as differential agreement/measurement cases.
#
# NONE of them is aliasing coverage, and that is a limit of the whole sweep rather than of these
# fixtures: `repr` compares CONTENT, `SmaliVmUnderTest.run` collapses identity deliberately, and the
# oracle deep-copies host arrays per node -- so no branching fixture, here or anywhere, can present the
# two engines a repeated reference. See `NewCoreDifferentialTest`'s class KDoc and the aliasing row in
# `CARRY-FORWARD.md`'s `## Unowned and cross-cutting` section.
# ==================================================================================================

# TWO ARMS RETURNING DIFFERENT CONSTANTS on an UNKNOWN predicate -- the `joinAll`-actually-joins case
# `CARRY-FORWARD.md` Plan 8 item 4 records as never having existed. `join(Known(3, I), Known(4, I))` is a
# GENUINELY DIVERGENT pair, so this is the first swept fixture where `joinAll` calls `join` on two
# unequal values rather than folding one returning node untouched. Spec §4.1: two different known ints
# join to `Unknown(I)`, which the oracle's `getTerminatingRegisterConsensus` reaches too (two disagreeing
# knowns -> `UnknownValue` of type `I`), so this AGREES and belongs in `newCoreSupported`.
#
# It is also what makes the `ValueJoin` `Joined -> Unknown(type)` rule differentially load-bearing:
# mutating it to `Joined -> Top` turns the core's v0 into a `Top`/HELD, which projects CONFLICTED where
# the oracle still answers UNKNOWN, so the sweep goes red. See
# `NewCoreDifferentialTest.forkedReturnPathsProjectTheirJoinedConsensus`.
#
# The selector is a PARAMETER (unknown), not a seed: `Fixture.seededRegisters` is Known-only, and a known
# selector would take exactly one arm and never join. 3 and 4, not 0 and 1: a zero arm would be
# indistinguishable from an unwritten register. `.registers 2`, p0 = v1; v0 is the returned value.
# Addresses: `if-eqz` (21t, 2 units) @0; `const/4` @2; `return` @3; `const/4` @4; `return` @5. Offset +4.
.method public static returnsDifferentConstants(I)I
    .registers 2

    if-eqz v1, :other

    const/4 v0, 0x3

    return v0

    :other
    const/4 v0, 0x4

    return v0
.end method

# A KNOWN predicate that FALLS THROUGH, the direction `ratchet-branch-on-zero` (`branchOnZero()I`, which
# takes the branch on a provable zero) does not cover. `const/4 v0, 0x1` then `if-eqz v0` is provably
# NOT taken, so exactly one arm runs and the returned value says which: 42 on the fall-through, 99 if an
# engine wrongly took the branch. Both engines agree, and nothing forks (the predicate is provable) -- so
# like `branchOnZero` this proves the definite-branch DIRECTION and is explicitly NOT fork coverage.
# `.registers 1`; v0 is the predicate then the returned value; default observe {0}.
# Addresses (decoded, not assumed): `const/4` @0; `if-eqz` (21t, 2 units) @1; `const/16` (2 units) @3;
# `return` @5; `const/16` @6 [:skip, unreached]; `return` @8. Offset +5.
.method public static knownBranchFallsThrough()I
    .registers 1

    const/4 v0, 0x1

    if-eqz v0, :skip

    const/16 v0, 0x2a

    return v0

    :skip
    const/16 v0, 0x63

    return v0
.end method

# A BACKWARD BRANCH FORMING A LOOP, with a NON-LOOPING SIBLING path -- the visit-limit fixture Task 11
# Step 1 owes with STATED assertions rather than "so the limit gets exercised". It is NOT swept (its run
# is `Partial`, which no `newCoreSupported` fixture may be); it is driven directly against an
# `ExecutionGraph` by `NewCoreDifferentialTest.aBackwardBranchLoopTruncatesOnlyItsOwnPathAndItsSiblingSurvives`.
#
# On an UNKNOWN parameter `if-eqz` forks: `Continue` (the fall-through, which is `:loop`) is explored
# FIRST (IfOp lists `[Continue, Jump]` and the LIFO frontier pops the first-declared last-pushed), so the
# engine descends into the loop while the `Jump` arm -- the sibling `return 42` -- waits in the frontier.
# The loop revisits address 2 until the per-address visit budget trips `ADDRESS_LIMIT`, which
# `ExecutionSession.exceededLimits` records with `record(node, Aborted(ADDRESS_LIMIT))` -- ending ONLY the
# looping path, deliberately (`ExecutionSession.kt`'s `exceededLimits`/`abortRun` split), so the sibling
# in the frontier still runs and returns. `Partial(ADDRESS_LIMIT)` makes `trusted()` null. The old engine
# cleared the whole frame on this and then read `aborted == false`
# (`docs/architecture/execution-engine.md:178`, `:184`); the sibling-survival assertion is the regression
# guard against that.
#
# `.registers 1`, p0 = v0 (unknown). Addresses: `if-eqz` (21t, 2 units) @0 -> fall-through @2, taken @4;
# `nop` (10x) @2 [:loop]; `goto :loop` (10t) @3 -> @2; `const/16` @4 [sibling]; `return` @6. The `goto`
# target is the `nop`, not itself: a zero-offset `goto` is an encoding [BC] rejects.
.method public static loopWithNonLoopingSibling(I)I
    .registers 1

    if-eqz v0, :return_sibling

    :loop
    nop

    goto :loop

    :return_sibling
    const/16 v0, 0x2a

    return v0
.end method
