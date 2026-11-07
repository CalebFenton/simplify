.class public Lexecution_graph;
.super Ljava/lang/Object;


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

.method public static returnsObjectOrString()Ljava/lang/Object;
    .locals 1

    if-eqz v0, :cond_1

    const-string v0, "the world was built by people no smarter than you"
    return-object v0

    :cond_1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

.method public static returnsObjectOrStringOrNull()Ljava/lang/Object;
    .locals 1

    packed-switch v0, :pswitch_data_0

    :pswitch_0
    const-string v0, "the world was built by people no smarter than you"
    return-object v0

    :pswitch_1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0

    :pswitch_2
    const/4 v0, 0x0
    return v0

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static returnsStringOrThrowsException()Ljava/lang/String;
    .locals 1

    if-eqz v0, :cond_1

    const-string v0, "the world was built by people no smarter than you"
    return-object v0

    :cond_1
    new-instance v0, Ljava/lang/Exception;
    invoke-direct {v0}, Ljava/lang/Exception;-><init>()V
    throw v0
.end method

.method public static returnsStringOrInteger()Ljava/lang/Object;
    .locals 2

    if-eqz v0, :cond_1

    const-string v0, "the world was built by people no smarter than you"
    return-object v0

    :cond_1
    new-instance v0, Ljava/lang/Integer;
    const/4 v1, 0x2
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V
    return-object v0
.end method

.method public static storesStringOrInteger()V
    .locals 2

    if-eqz v0, :cond_1

    const-string v0, "the world was built by people no smarter than you"
    return-void

    :cond_1
    new-instance v0, Ljava/lang/Integer;
    const/4 v1, 0x2
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V
    return-void
.end method

.method public static storesStringOrInt()V
    .locals 1

    if-eqz v0, :cond_1

    const-string v0, "the world was built by people no smarter than you"
    return-void

    :cond_1
    const/4 v0, 0x2
    return-void
.end method

.method public static returnsStringArrayOr2DIntArray()Ljava/lang/Object;
    .locals 2

    if-eqz v0, :cond_1
    new-array v0, v0, [Ljava/lang/String;
    return-object v0

    :cond_1
    new-array v0, v0, [[I
    return-object v0
.end method
