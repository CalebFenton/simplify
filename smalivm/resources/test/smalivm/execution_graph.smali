.class public Lexecution_graph;
.super Ljava/lang/Object;


# direct methods
.method public static terminatingAddresses()V
    .locals 2

    #@0
    packed-switch v0, :pswitch_data_0

    #@3
    sparse-switch v0, :sswitch_data_0

    #@6
    return-void

    #@7
    :pswitch_0
    return v0

    #@8
    :pswitch_1
    return-void

    #@9
    :sswitch_0
    return-wide v0

    #@a
    :sswitch_1
    return-object v0

    #@b
    goto :goto_0

    #@c
    throw v0

    #@d
    nop

    #@e
    :goto_0
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch

    #@16
    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_0
        0x2 -> :sswitch_1
    .end sparse-switch
.end method
