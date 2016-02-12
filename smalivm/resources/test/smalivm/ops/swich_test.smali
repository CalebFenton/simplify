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
