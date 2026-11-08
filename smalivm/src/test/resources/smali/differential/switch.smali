# packed-switch / sparse-switch fixtures. Where a fork is under test every arm assigns the same
# value, so the joined result stays known; where the taken arm matters, arms assign different values.
.class public Ldifferential/switch;
.super Ljava/lang/Object;

# A known key that matches. 22 is the middle case, so an off-by-one in either direction differs.
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

# A known key that matches nothing falls through ([BC] packed-switch). 9 is above 1..3; the
# returned 99 is reachable no other way.
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

# Sparse table with ascending keys, the only verifiable shape; keys are far apart so the table is
# genuinely sparse.
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

# The selector is a parameter so every arm is explored; every arm assigns 7, so the joined result
# is a known 7.
.method public static unknownSelectorEveryArmAgrees(I)I
    .locals 1

    # The switch is not at address 0, so "offset from the switch" and "offset from the method start"
    # differ.
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

# A sparse table whose keys descend, which no verifier admits ([CON] A8, [ART]) but obfuscated input
# can contain. Selector 1 is found by a key lookup, but ART binary search falls through, so every
# arm and the fall-through are explored. Every arm assigns 7.
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

# v0 is seeded to null, whose bits are 0 ([BC] General design), so case 0 is the only feasible arm
# and the fall-through is infeasible. The table contains key 0 for that reason. Both arms assign 7.
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

# v0 is seeded to 2.9f. The selector is the register's 32 bits (0x4039999A), not the numeric value 2,
# so a device falls through; the bits are not provable here, so every arm is explored. Every arm
# assigns 7.
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
