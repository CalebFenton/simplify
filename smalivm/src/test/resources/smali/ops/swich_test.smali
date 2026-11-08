.class public Lswitch_test;
.super Ljava/lang/Object;

.method public static packedSwitch()V
    .locals 1

    # If switch is first op, it masks if offset addresses are calculated correctly.
    # @0
    goto :switch

    :switch
    # @1
    packed-switch v0, :pswitch_data_0

    # @4
    return-void

    :pswitch_0
    # @5
    return-void

    :pswitch_1
    # @6
    return-void

    :pswitch_2
    # @7
    return-void

    # @8
    nop # for alignment

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static sparseSwitch()V
    .locals 1

    # If switch is first op, it masks if offset addresses are calculated correctly.
    goto :switch # @0

    :switch
    sparse-switch v0, :sswitch_data_0 # @1

    return-void # @4

    :sswitch_0
    return-void # @5

    :sswitch_1
    return-void # @6

    nop # @7, for alignment

    :sswitch_data_0
    .sparse-switch # @8
        0x1 -> :sswitch_0
        0x2 -> :sswitch_1
    .end sparse-switch
.end method

# ------------------------------------------------------------------------------------------------
# Added by Plan 8 Task 6, which lands `packed-switch`/`sparse-switch` in the clean core. Every method
# below keeps the two above's convention of NOT putting the switch at address 0 -- their own comment
# says why, and it is a real trap: with the switch at 0, "offset relative to the switch opcode"
# (which is what [BC] specifies) is indistinguishable from "offset relative to the method start".
#
# Addresses are NOT commented inline here. The two methods above do carry inline `# @n` comments and
# `packedSwitch()V`'s DISAGREE WITH THE ASSEMBLED CODE -- its `nop # for alignment` at "@8" does not
# survive assembly (the payload is 4-byte aligned at @8 already, three `return-void`s after the
# switch), so the payload is at 8 and the nop is not there at all. `sparseSwitch()V`'s comments are
# correct because it needs its nop. Rather than add more comments that can rot, every address this
# task depends on is asserted from the assembled `MethodView` in
# `core/op/SwitchOpTest.theseFixturesHaveTheAddressesEveryExpectationInThisFileIsWrittenAgainst`.
# ------------------------------------------------------------------------------------------------

# A table containing KEY 0, which neither method above has (`packedSwitch()V` starts at 0x1,
# `sparseSwitch()V` uses 0x1/0x2). Without it a definitely-NULL selector is unobservable: [BC]'s
# *"In terms of bitwise representation, `(Object) null == (int) 0`"* makes a null selector's bits
# provably 0, so it must DISPATCH to case 0 -- and against a table with no key 0 that answer and the
# wrong one ("a null is a reference, so no case matches") both fall through.
.method public static packedSwitchIncludingKeyZero()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_0
    return-void

    :pswitch_1
    return-void

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# A case whose target IS the fall-through, i.e. an offset of +3 (`31t` is three code units). Legal,
# and an obfuscator emits it as an opaque predicate. It is the switch's version of the same-address
# fork `CommitTest.aBranchWhoseTargetIsItsFallThroughBuildsTwoNodesAtThatOneAddress` covers for `if`:
# without dedup against the plain fall-through an unprovable selector yields TWO SuccessorSpecs at one
# address, so two nodes, two Outcomes and double that address's visit budget.
.method public static packedSwitchWhoseCaseTargetsItsFallThrough()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    :pswitch_0
    return-void

    :pswitch_1
    return-void

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# A sparse table whose keys DESCEND, which is the input that separates ART's binary search from a map
# lookup. [CON] A8 requires *"The corresponding table must be consistent and sorted low-to-high"* and
# [ART] is stricter still (`if (UNLIKELY(key <= last_key))` in `CheckAndMarkSwitchTargets`), so this
# method is VERIFIER-invalid by construction -- which is exactly why it needs a fixture: no verified
# input can reach the disagreement, and obfuscated input is not verified.
#
# For selector 1 the search reads `keys[0] == 2`, goes left, and finds nothing, so [ART] falls
# through; a map finds key 1 and takes its arm. For selector 2 the search hits on its first probe and
# the two dispatches agree, which is the control.
.method public static sparseSwitchWithDescendingKeys()V
    .locals 1

    nop

    sparse-switch v0, :sswitch_data_0

    return-void

    :sswitch_0
    return-void

    :sswitch_1
    return-void

    :sswitch_data_0
    .sparse-switch
        0x2 -> :sswitch_0
        0x1 -> :sswitch_1
    .end sparse-switch
.end method

# `size == 0`, which the payload layout permits -- the field is a `ushort` and [BC] does not forbid 0 --
# and which [ART] handles explicitly in both dispatch functions
# (`// Empty packed switch, move forward by 3 (size of PACKED_SWITCH).` and
# `// Return length of SPARSE_SWITCH if size is 0.`). That it is assemblable from smali at all was
# MEASURED rather than assumed: `.packed-switch 0x1` with no labels produces a four-code-unit payload
# whose element list is empty.
.method public static packedSwitchWithEmptyTable()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_data_0
    .packed-switch 0x1
    .end packed-switch
.end method

# DUPLICATE KEYS, which [ART] rejects harder than [CON] A8 does (`if (UNLIKELY(key <= last_key))` in
# `CheckAndMarkSwitchTargets` fails on `<=`, not just on `<`) and which obfuscated input can still
# contain. It is the input a `Map<Int, Int>` CANNOT represent: a map keeps one entry per key, so one of
# these two arms disappears silently -- which is half of why `SwitchOp` carries an ordered `List<Case>`.
# ART's binary search finds key 1 on its first probe, so the fall-through is not a successor; both arms
# are, because a linear-scan runtime would take the first and the search takes whichever it lands on.
.method public static sparseSwitchWithDuplicateKeys()V
    .locals 1

    nop

    sparse-switch v0, :sswitch_data_0

    return-void

    :sswitch_0
    return-void

    :sswitch_1
    return-void

    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_0
        0x1 -> :sswitch_1
    .end sparse-switch
.end method
