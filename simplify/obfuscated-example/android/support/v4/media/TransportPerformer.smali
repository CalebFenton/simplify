.class public abstract Landroid/support/v4/media/TransportPerformer;
.super Ljava/lang/Object;
.source "TransportPerformer.java"


# static fields
.field static final AUDIOFOCUS_GAIN:I = 0x1

.field static final AUDIOFOCUS_GAIN_TRANSIENT:I = 0x2

.field static final AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:I = 0x3

.field static final AUDIOFOCUS_LOSS:I = -0x1

.field static final AUDIOFOCUS_LOSS_TRANSIENT:I = -0x2

.field static final AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:I = -0x3


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public onAudioFocusChange(I)V
    .locals 9
    .param p1, "focusChange"    # I

    #@0
    .prologue
    .line 188
    const/4 v7, 0x0

    #@1
    .line 189
    .local v7, "keyCode":I
    packed-switch p1, :pswitch_data_0

    #@4
    .line 196
    :goto_0
    if-eqz v7, :cond_0

    #@6
    .line 197
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@9
    move-result-wide v2

    #@a
    .line 198
    .local v2, "now":J
    new-instance v1, Landroid/view/KeyEvent;

    #@c
    const/4 v6, 0x0

    #@d
    const/4 v8, 0x0

    #@e
    move-wide v4, v2

    #@f
    invoke-direct/range {v1 .. v8}, Landroid/view/KeyEvent;-><init>(JJIII)V

    #@12
    invoke-virtual {p0, v7, v1}, Landroid/support/v4/media/TransportPerformer;->onMediaButtonDown(ILandroid/view/KeyEvent;)Z

    #@15
    .line 199
    new-instance v1, Landroid/view/KeyEvent;

    #@17
    const/4 v6, 0x1

    #@18
    const/4 v8, 0x0

    #@19
    move-wide v4, v2

    #@1a
    invoke-direct/range {v1 .. v8}, Landroid/view/KeyEvent;-><init>(JJIII)V

    #@1d
    invoke-virtual {p0, v7, v1}, Landroid/support/v4/media/TransportPerformer;->onMediaButtonUp(ILandroid/view/KeyEvent;)Z

    #@20
    .line 201
    .end local v2    # "now":J
    :cond_0
    return-void

    #@21
    .line 193
    :pswitch_0
    const/16 v7, 0x7f

    #@23
    goto :goto_0

    #@24
    .line 189
    :pswitch_data_0
    .packed-switch -0x1
        :pswitch_0
    .end packed-switch
.end method

.method public onGetBufferPercentage()I
    .locals 1

    #@0
    .prologue
    .line 76
    const/16 v0, 0x64

    #@2
    return v0
.end method

.method public abstract onGetCurrentPosition()J
.end method

.method public abstract onGetDuration()J
.end method

.method public onGetTransportControlFlags()I
    .locals 1

    #@0
    .prologue
    .line 98
    const/16 v0, 0x3c

    #@2
    return v0
.end method

.method public abstract onIsPlaying()Z
.end method

.method public onMediaButtonDown(ILandroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 127
    sparse-switch p1, :sswitch_data_0

    #@4
    .line 145
    :goto_0
    return v1

    #@5
    .line 129
    :sswitch_0
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onStart()V

    #@8
    goto :goto_0

    #@9
    .line 132
    :sswitch_1
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onPause()V

    #@c
    goto :goto_0

    #@d
    .line 135
    :sswitch_2
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onStop()V

    #@10
    goto :goto_0

    #@11
    .line 139
    :sswitch_3
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onIsPlaying()Z

    #@14
    move-result v0

    #@15
    if-eqz v0, :cond_0

    #@17
    .line 140
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onPause()V

    #@1a
    goto :goto_0

    #@1b
    .line 142
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/media/TransportPerformer;->onStart()V

    #@1e
    goto :goto_0

    #@1f
    .line 127
    nop

    #@20
    :sswitch_data_0
    .sparse-switch
        0x4f -> :sswitch_3
        0x55 -> :sswitch_3
        0x56 -> :sswitch_2
        0x7e -> :sswitch_0
        0x7f -> :sswitch_1
    .end sparse-switch
.end method

.method public onMediaButtonUp(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 164
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public abstract onPause()V
.end method

.method public abstract onSeekTo(J)V
.end method

.method public abstract onStart()V
.end method

.method public abstract onStop()V
.end method
