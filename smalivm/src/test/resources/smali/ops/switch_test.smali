.class public Lswitch_test;
.super Ljava/lang/Object;

.method public static packedSwitch()V
    .locals 1

    # The switch is not at address 0, so offsets relative to the switch differ from offsets relative to
    # the method start.
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

    # The assembled payload is at @8; this nop is not emitted because @8 is already 4-byte aligned.
    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static sparseSwitch()V
    .locals 1

    # The switch is not at address 0; see packedSwitch()V.
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

# Every method below also keeps its switch at address 1.

# A table containing key 0. A null selector's bits are 0 ([BC]: "(Object) null == (int) 0"), so it must
# dispatch to case 0; without a key-0 case that answer and "no case matches" both fall through.
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

# A case whose target is the fall-through (offset +3, the size of `31t`); obfuscators emit this as an
# opaque predicate. The successor must be deduplicated against the plain fall-through.
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

# Sparse keys in descending order, which [CON] A8 and the ART verifier reject but obfuscated input can
# contain. ART's binary search falls through for selector 1 (it reads keys[0] == 2 and goes left) where
# a map lookup would take the arm; selector 2 hits on the first probe in both.
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

# An empty table (`size == 0`): the payload layout permits it and ART's dispatch handles it by moving
# past the switch.
.method public static packedSwitchWithEmptyTable()V
    .locals 1

    nop

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_data_0
    .packed-switch 0x1
    .end packed-switch
.end method

# Duplicate keys, which the ART verifier rejects (`key <= last_key`) but obfuscated input can contain.
# A map would drop one arm. ART's binary search finds key 1 on its first probe, so the fall-through is
# not a successor and either arm may be taken.
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

# Keys ascending in signed order but not unsigned order: a search that compares keys unsigned cannot
# find 0x7fffffff.
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
