# Differential fixtures for `packed-switch` / `sparse-switch` (Plan 8 Task 6). MEASURED before writing
# them: `grep -in 'switch' Fixtures.kt` returned **zero** and `ratchet.smali`'s six methods contain no
# switch, so this family had no capability ratchet to retire and no differential coverage at all --
# unlike the `if` family, which retired `ratchet-branch-on-zero`. Adding is therefore the only way to
# close the "capability grew, coverage did not" hole for this family.
#
# THE AGREEING FIXTURES ARE NOT ARBITRARY. Each arm of every method assigns the SAME value where the two
# engines must agree about a fork, because the sweep compares terminating consensus: with different
# values per arm a fork would join to "nothing known" on both sides and agreement would prove nothing
# about the fork at all. Where the engines must agree about WHICH arm was taken, the arms assign
# DIFFERENT values, so taking the wrong one is a value difference and not only an address difference.
.class public Ldifferential/switch;
.super Ljava/lang/Object;

# A KNOWN key that matches: exactly one arm may run, and the returned value says which. 22, not 11 or 33:
# the middle case, so an off-by-one in either direction is a different number.
.method public static packedKnownMatch()I
    .locals 1

    const/4 v0, 0x2

    packed-switch v0, :pswitch_data_0

    const/16 v0, 0x63

    return v0

    :pswitch_0
    const/16 v0, 0xb

    return v0

    :pswitch_1
    const/16 v0, 0x16

    return v0

    :pswitch_2
    const/16 v0, 0x21

    return v0

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

# A KNOWN key that matches NOTHING, which is the [BC] fall-through row -- *"or fall through to the next
# instruction if there is no match"* -- and the one both engines could get wrong by dispatching on a
# nearby key. 9 is outside 1..3 on the high side; the returned 99 is reachable no other way.
.method public static packedKnownNoMatch()I
    .locals 1

    const/16 v0, 0x9

    packed-switch v0, :pswitch_data_0

    const/16 v0, 0x63

    return v0

    :pswitch_0
    const/16 v0, 0xb

    return v0

    :pswitch_1
    const/16 v0, 0x16

    return v0

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# The sparse spelling of the same claim, on an ASCENDING table -- i.e. the only shape verified input can
# have, where ART's binary search and a map lookup agree by construction. Keys are far apart (0x1, 0x64)
# so the table is genuinely sparse rather than a packed table in disguise.
.method public static sparseKnownMatch()I
    .locals 1

    const/16 v0, 0x64

    sparse-switch v0, :sswitch_data_0

    const/16 v0, 0x63

    return v0

    :sswitch_0
    const/16 v0, 0xb

    return v0

    :sswitch_1
    const/16 v0, 0x16

    return v0

    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_0
        0x64 -> :sswitch_1
    .end sparse-switch
.end method

# AN UNPROVABLE SELECTOR, which is what makes this family's fork differentially covered. The selector is
# a METHOD PARAMETER rather than a seeded register, deliberately: `Fixture.seededRegisters` is
# Known-only, and each engine would otherwise construct its own unknown from its own value model.
#
# Every arm assigns 7, so the fork's terminating consensus is a foldable `7` on both engines and the
# comparison is about coverage AND value rather than about "nothing known" agreeing with "nothing known".
.method public static unknownSelectorEveryArmAgrees(I)I
    .locals 1

    # Never put the switch at address 0 -- "offset from the switch" and "offset from the method start"
    # would be the same number. Same convention as `ops/swich_test.smali`.
    nop

    packed-switch p0, :pswitch_data_0

    const/4 v0, 0x7

    return v0

    :pswitch_0
    const/4 v0, 0x7

    return v0

    :pswitch_1
    const/4 v0, 0x7

    return v0

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# THE REGISTERED DIVERGENCE (`switch-sparse-descending-keys`). A sparse table whose keys DESCEND, which
# no verifier admits -- [CON] A8 requires it *"sorted low-to-high"* and [ART] rejects `key <= last_key`
# outright -- and which obfuscated input can therefore contain.
#
# Selector 1 is the separating input. [ART] binary-searches the raw key array: it reads `keys[0] == 2`,
# goes left, finds nothing, and FALLS THROUGH. The oracle uses one `HashMap.containsKey` for both switch
# kinds (`opcode/SwitchPayloadOp.kt:34`), finds key 1, and takes that arm confidently. The clean core
# explores BOTH, because which dispatch a runtime uses is an unprovable question about feasibility.
#
# Every arm assigns 7, so the observed register agrees on both engines and the recorded divergence is a
# PATH-SET divergence only -- which is what `ORACLE_MISJUDGES_FEASIBILITY`'s branch requires.
.method public static sparseDescendingKeys()I
    .locals 1

    const/4 v0, 0x1

    sparse-switch v0, :sswitch_data_0

    const/4 v0, 0x7

    return v0

    :sswitch_0
    const/4 v0, 0x7

    return v0

    :sswitch_1
    const/4 v0, 0x7

    return v0

    :sswitch_data_0
    .sparse-switch
        0x2 -> :sswitch_0
        0x1 -> :sswitch_1
    .end sparse-switch
.end method

# THE SECOND REGISTERED DIVERGENCE (`switch-null-selector-against-key-zero`), and it diverges in BOTH
# §2.2 directions from one row -- which is why it needed a table containing key `0`.
#
# v0 is SEEDED to a null reference. [BC]'s "General design" makes its bits provably `0` (*"In terms of
# bitwise representation, `(Object) null == (int) 0`"*), so case 0 is the only feasible arm and the
# fall-through is infeasible. The oracle does the opposite of both: `Utils.getIntegerValue(null)` returns
# null, the boxed value reaches `Map.containsKey(Object)` WITHOUT unboxing (settled by disassembling the
# compiled `execute`, because reading the Kotlin invites the wrong answer), `HashMap.containsKey(null)` is
# false, and it falls through -- missing a feasible arm AND taking an infeasible one.
#
# Both arms assign 7, so the observed register agrees and the recorded divergence is a path-set one only.
.method public static nullSelectorAgainstKeyZero()I
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    const/4 v0, 0x7

    return v0

    :pswitch_0
    const/4 v0, 0x7

    return v0

    :pswitch_1
    const/4 v0, 0x7

    return v0

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# THE THIRD REGISTERED DIVERGENCE (`switch-float-selector-read-by-value`), registry D6 -- the same
# cross-domain defect as `if`'s, at a second op family.
#
# v0 is SEEDED to `2.9f`. A device reads the register's 32 BITS, which for `2.9f` are `0x4039999A` and
# match no small key, so it falls through. The oracle reads the host box's numeric VALUE through
# `Utils.getIntegerValue` (`opcode/SwitchPayloadOp.kt:33`), gets `2`, and dispatches to case 2 --
# confidently, and to an arm the runtime cannot take, while never exploring the fall-through the runtime
# does take. The core cannot prove the bits (Plan 8 Task 3: a `Float` raw is not readable at 32 bits
# without a bit reinterpretation `Coerce` deliberately does not do here), so it explores every arm.
#
# Every arm assigns 7, so the observed register agrees and the divergence is a path-set one only.
.method public static floatSelectorReadByValue()I
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    const/4 v0, 0x7

    return v0

    :pswitch_0
    const/4 v0, 0x7

    return v0

    :pswitch_1
    const/4 v0, 0x7

    return v0

    :pswitch_2
    const/4 v0, 0x7

    return v0

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method
