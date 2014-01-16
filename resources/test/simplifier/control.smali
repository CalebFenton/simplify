.class public final Lcontrol;
.super Ljava/lang/Object;

.method public static infinite_depth()V
    .locals 0

    :place
    goto :forever

    :forever
    goto :place

    return-void
.end method

.method public static packed_switch(I)V
    .locals 1

	packed-switch p1, :pswitch_data_0

	:pswitch_0
	const/4 v0, 0x0

	goto :end

	:pswitch_1
	const/4 v0, 0x1

	goto :end

	:pswitch_2
	const/4 v0, 0x2

	goto :end

	:end
	return v0

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
        :pswitch_2
    .end packed-switch
.end method

.method public static sparse_switch(I)V
    .locals 1

	sparse-switch p1, :sswitch_data_0

	:sswitch_0
	const/4 v0, 0x0

	goto :end

	:sswitch_1
	const/4 v0, 0x1

	goto :end

	:sswitch_2
	const/4 v0, 0x2

	goto :end

	:end
	return v0

    :sswitch_data_0
    .sparse-switch
        -0x1 -> :sswitch_1
        0x0 -> :sswitch_0
        0x3 -> :sswitch_2
    .end sparse-switch
.end method

.method public trycatch(I)I
    .locals 6

    .prologue
    .line 43
    move-object v0, p0

    move v1, p1

    const/4 v5, 0x0

    move-object v2, v5

    .line 44
    const/4 v5, 0x1

    move v3, v5

    .line 46
    move-object v5, v2

    :try_start_0
    invoke-virtual {v5}, Ljava/lang/String;->length()I
    :try_end_0
    .catch Ljava/lang/NullPointerException; {:try_start_0 .. :try_end_0} :catch_0

    move-result v5

    .line 47
    const/4 v5, 0x3

    move v3, v5

    .line 50
    .line 52
    :goto_0
    move v5, v3

    move v0, v5

    return v0

    .line 48
    :catch_0
    move-exception v5

    move-object v4, v5

    .line 49
    const/4 v5, 0x5

    move v3, v5

    goto :goto_0
.end method
