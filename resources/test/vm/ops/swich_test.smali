.class public Lswitch_test;
.super Ljava/lang/Object;

.method public static TestPackedSwitch()V
    .locals 1

    packed-switch v0, :pswitch_data_0

    return-void

    :pswitch_0
    return-void

    :pswitch_1
    return-void

    :pswitch_2
    return-void

    nop # for alignment

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static TestSparseSwitch()V
    .locals 1

    sparse-switch v0, :sswitch_data_0

    return-void

    :sswitch_0
    return-void

    :sswitch_1
    return-void

    nop # for alignment

    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_0
        0x2 -> :sswitch_1
    .end sparse-switch
.end method