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

# Every method below keeps the convention of not putting the switch at address 0: there, "offset
# relative to the switch opcode" ([BC]) is indistinguishable from "offset relative to the method start".
# Addresses are not commented inline; `SwitchOpTest.theseFixturesHaveTheAddressesEveryExpectationInThisFileIsWrittenAgainst`
# asserts them from the assembled code. The inline `# @8` alignment nop in `packedSwitch()V` above does
# not survive assembly.

# A table containing key 0, which neither method above has. [BC]: "(Object) null == (int) 0", so a
# definitely-null selector dispatches to case 0; without key 0 that answer and "no case matches" both
# fall through.
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

# A case whose target is the fall-through (offset +3; `31t` is three code units), an opaque-predicate
# emission. Without dedup against the plain fall-through an unprovable selector would yield two
# successors at one address. `IfOpTest.aBranchWhoseTargetIsItsFallThroughBuildsTwoNodesAtThatOneAddress`
# covers the `if` version.
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

# A sparse table whose keys descend, which is the input that separates ART's binary search from a map
# lookup. [CON] A8 requires *"The corresponding table must be consistent and sorted low-to-high"* and
# [ART] is stricter still (`if (UNLIKELY(key <= last_key))` in `CheckAndMarkSwitchTargets`), so this
# method is verifier-invalid by construction -- which is exactly why it needs a fixture: no verified
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

# `size == 0`: the payload layout permits it and [ART] handles it explicitly in both dispatch
# functions. `.packed-switch 0x1` with no labels assembles to a four-code-unit payload.
.method public static packedSwitchWithEmptyTable()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_data_0
    .packed-switch 0x1
    .end packed-switch
.end method

# Duplicate keys, which [ART] rejects harder than [CON] A8 does (`if (UNLIKELY(key <= last_key))` in
# `CheckAndMarkSwitchTargets` fails on `<=`, not just on `<`) and which obfuscated input can still
# contain. It is the input a `Map<Int, Int>` cannot represent: a map keeps one entry per key, so one of
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

# The largest `first_key` ART's verifier admits for two cases (`first_key + size - 1` must not overflow):
# keys 0x7ffffffe and 0x7fffffff. A selector of Integer.MIN_VALUE is one past the last key only in
# wrapped arithmetic, and ART's `index = test_val - first_key` is 2 for it, out of range.
.method public static packedSwitchEndingAtIntMax()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_0
    return-void

    :pswitch_1
    return-void

    :pswitch_data_0
    .packed-switch 0x7ffffffe
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# One step past what the verifier admits: keys 0x7fffffff and, wrapped, 0x80000000. Unverified input, so
# ART's interpreter decides, and its `test_val - first_key` wraps too.
.method public static packedSwitchWrappingPastIntMax()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_0
    return-void

    :pswitch_1
    return-void

    :pswitch_data_0
    .packed-switch 0x7fffffff
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

# Strictly ascending in signed order, which is not ascending in unsigned order: 0x7fffffff sorts below
# the negative keys unsigned, so a binary search that compares keys unsigned cannot find it.
.method public static sparseSwitchWithNegativeKeys()V
    .locals 1

    nop

    sparse-switch v0, :sswitch_data_0

    return-void

    :sswitch_0
    return-void

    :sswitch_1
    return-void

    :sswitch_2
    return-void

    :sswitch_3
    return-void

    :sswitch_data_0
    .sparse-switch
        -0x80000000 -> :sswitch_0
        -0x2 -> :sswitch_1
        -0x1 -> :sswitch_2
        0x7fffffff -> :sswitch_3
    .end sparse-switch
.end method
