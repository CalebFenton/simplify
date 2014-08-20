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

	packed-switch p0, :pswitch_data_0

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

	sparse-switch p0, :sswitch_data_0

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
