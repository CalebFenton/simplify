.class public Landroid/support/v4/media/TransportMediator;
.super Landroid/support/v4/media/TransportController;
.source "TransportMediator.java"


# static fields
.field public static final FLAG_KEY_MEDIA_FAST_FORWARD:I = 0x40

.field public static final FLAG_KEY_MEDIA_NEXT:I = 0x80

.field public static final FLAG_KEY_MEDIA_PAUSE:I = 0x10

.field public static final FLAG_KEY_MEDIA_PLAY:I = 0x4

.field public static final FLAG_KEY_MEDIA_PLAY_PAUSE:I = 0x8

.field public static final FLAG_KEY_MEDIA_PREVIOUS:I = 0x1

.field public static final FLAG_KEY_MEDIA_REWIND:I = 0x2

.field public static final FLAG_KEY_MEDIA_STOP:I = 0x20

.field public static final KEYCODE_MEDIA_PAUSE:I = 0x7f

.field public static final KEYCODE_MEDIA_PLAY:I = 0x7e

.field public static final KEYCODE_MEDIA_RECORD:I = 0x82


# instance fields
.field final mAudioManager:Landroid/media/AudioManager;

.field final mCallbacks:Landroid/support/v4/media/TransportPerformer;

.field final mContext:Landroid/content/Context;

.field final mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

.field final mDispatcherState:Ljava/lang/Object;

.field final mKeyEventCallback:Landroid/view/KeyEvent$Callback;

.field final mListeners:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/media/TransportStateListener;",
            ">;"
        }
    .end annotation
.end field

.field final mTransportKeyCallback:Landroid/support/v4/media/TransportMediatorCallback;

.field final mView:Landroid/view/View;


# direct methods
.method public constructor <init>(Landroid/app/Activity;Landroid/support/v4/media/TransportPerformer;)V
    .locals 1
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "callbacks"    # Landroid/support/v4/media/TransportPerformer;

    #@0
    .prologue
    .line 152
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0, p2}, Landroid/support/v4/media/TransportMediator;-><init>(Landroid/app/Activity;Landroid/view/View;Landroid/support/v4/media/TransportPerformer;)V

    #@4
    .line 153
    return-void
.end method

.method private constructor <init>(Landroid/app/Activity;Landroid/view/View;Landroid/support/v4/media/TransportPerformer;)V
    .locals 5
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "view"    # Landroid/view/View;
    .param p3, "callbacks"    # Landroid/support/v4/media/TransportPerformer;

    #@0
    .prologue
    .line 159
    invoke-direct {p0}, Landroid/support/v4/media/TransportController;-><init>()V

    #@3
    .line 55
    new-instance v0, Ljava/util/ArrayList;

    #@5
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@8
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@a
    .line 57
    new-instance v0, Landroid/support/v4/media/TransportMediator$1;

    #@c
    invoke-direct {v0, p0}, Landroid/support/v4/media/TransportMediator$1;-><init>(Landroid/support/v4/media/TransportMediator;)V

    #@f
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mTransportKeyCallback:Landroid/support/v4/media/TransportMediatorCallback;

    #@11
    .line 130
    new-instance v0, Landroid/support/v4/media/TransportMediator$2;

    #@13
    invoke-direct {v0, p0}, Landroid/support/v4/media/TransportMediator$2;-><init>(Landroid/support/v4/media/TransportMediator;)V

    #@16
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mKeyEventCallback:Landroid/view/KeyEvent$Callback;

    #@18
    .line 160
    if-eqz p1, :cond_1

    #@1a
    move-object v0, p1

    #@1b
    :goto_0
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mContext:Landroid/content/Context;

    #@1d
    .line 161
    iput-object p3, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@1f
    .line 162
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mContext:Landroid/content/Context;

    #@21
    const-string v1, "audio"

    #@23
    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@26
    move-result-object v0

    #@27
    check-cast v0, Landroid/media/AudioManager;

    #@29
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mAudioManager:Landroid/media/AudioManager;

    #@2b
    .line 163
    if-eqz p1, :cond_0

    #@2d
    invoke-virtual {p1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    #@30
    move-result-object v0

    #@31
    invoke-virtual {v0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@34
    move-result-object p2

    #@35
    .end local p2    # "view":Landroid/view/View;
    :cond_0
    iput-object p2, p0, Landroid/support/v4/media/TransportMediator;->mView:Landroid/view/View;

    #@37
    .line 164
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mView:Landroid/view/View;

    #@39
    invoke-static {v0}, Landroid/support/v4/view/KeyEventCompat;->getKeyDispatcherState(Landroid/view/View;)Ljava/lang/Object;

    #@3c
    move-result-object v0

    #@3d
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mDispatcherState:Ljava/lang/Object;

    #@3f
    .line 165
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@41
    const/16 v1, 0x12

    #@43
    if-lt v0, v1, :cond_2

    #@45
    .line 166
    new-instance v0, Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@47
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mContext:Landroid/content/Context;

    #@49
    iget-object v2, p0, Landroid/support/v4/media/TransportMediator;->mAudioManager:Landroid/media/AudioManager;

    #@4b
    iget-object v3, p0, Landroid/support/v4/media/TransportMediator;->mView:Landroid/view/View;

    #@4d
    iget-object v4, p0, Landroid/support/v4/media/TransportMediator;->mTransportKeyCallback:Landroid/support/v4/media/TransportMediatorCallback;

    #@4f
    invoke-direct {v0, v1, v2, v3, v4}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;-><init>(Landroid/content/Context;Landroid/media/AudioManager;Landroid/view/View;Landroid/support/v4/media/TransportMediatorCallback;)V

    #@52
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@54
    .line 171
    :goto_1
    return-void

    #@55
    .line 160
    .restart local p2    # "view":Landroid/view/View;
    :cond_1
    invoke-virtual {p2}, Landroid/view/View;->getContext()Landroid/content/Context;

    #@58
    move-result-object v0

    #@59
    goto :goto_0

    #@5a
    .line 169
    .end local p2    # "view":Landroid/view/View;
    :cond_2
    const/4 v0, 0x0

    #@5b
    iput-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@5d
    goto :goto_1
.end method

.method public constructor <init>(Landroid/view/View;Landroid/support/v4/media/TransportPerformer;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "callbacks"    # Landroid/support/v4/media/TransportPerformer;

    #@0
    .prologue
    .line 156
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, v0, p1, p2}, Landroid/support/v4/media/TransportMediator;-><init>(Landroid/app/Activity;Landroid/view/View;Landroid/support/v4/media/TransportPerformer;)V

    #@4
    .line 157
    return-void
.end method

.method private getListeners()[Landroid/support/v4/media/TransportStateListener;
    .locals 2

    #@0
    .prologue
    .line 212
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v1

    #@6
    if-gtz v1, :cond_0

    #@8
    .line 213
    const/4 v0, 0x0

    #@9
    .line 217
    :goto_0
    return-object v0

    #@a
    .line 215
    :cond_0
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@c
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@f
    move-result v1

    #@10
    new-array v0, v1, [Landroid/support/v4/media/TransportStateListener;

    #@12
    .line 216
    .local v0, "listeners":[Landroid/support/v4/media/TransportStateListener;
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@14
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@17
    goto :goto_0
.end method

.method static isMediaKey(I)Z
    .locals 1
    .param p0, "keyCode"    # I

    #@0
    .prologue
    .line 112
    sparse-switch p0, :sswitch_data_0

    #@3
    .line 127
    const/4 v0, 0x0

    #@4
    :goto_0
    return v0

    #@5
    .line 124
    :sswitch_0
    const/4 v0, 0x1

    #@6
    goto :goto_0

    #@7
    .line 112
    nop

    #@8
    :sswitch_data_0
    .sparse-switch
        0x4f -> :sswitch_0
        0x55 -> :sswitch_0
        0x56 -> :sswitch_0
        0x57 -> :sswitch_0
        0x58 -> :sswitch_0
        0x59 -> :sswitch_0
        0x5a -> :sswitch_0
        0x5b -> :sswitch_0
        0x7e -> :sswitch_0
        0x7f -> :sswitch_0
        0x82 -> :sswitch_0
    .end sparse-switch
.end method

.method private pushControllerState()V
    .locals 5

    #@0
    .prologue
    .line 239
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 240
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@6
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@8
    invoke-virtual {v1}, Landroid/support/v4/media/TransportPerformer;->onIsPlaying()Z

    #@b
    move-result v1

    #@c
    iget-object v2, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@e
    invoke-virtual {v2}, Landroid/support/v4/media/TransportPerformer;->onGetCurrentPosition()J

    #@11
    move-result-wide v2

    #@12
    iget-object v4, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@14
    invoke-virtual {v4}, Landroid/support/v4/media/TransportPerformer;->onGetTransportControlFlags()I

    #@17
    move-result v4

    #@18
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->refreshState(ZJI)V

    #@1b
    .line 244
    :cond_0
    return-void
.end method

.method private reportPlayingChanged()V
    .locals 5

    #@0
    .prologue
    .line 221
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->getListeners()[Landroid/support/v4/media/TransportStateListener;

    #@3
    move-result-object v4

    #@4
    .line 222
    .local v4, "listeners":[Landroid/support/v4/media/TransportStateListener;
    if-eqz v4, :cond_0

    #@6
    .line 223
    move-object v0, v4

    #@7
    .local v0, "arr$":[Landroid/support/v4/media/TransportStateListener;
    array-length v2, v0

    #@8
    .local v2, "len$":I
    const/4 v1, 0x0

    #@9
    .local v1, "i$":I
    :goto_0
    if-ge v1, v2, :cond_0

    #@b
    aget-object v3, v0, v1

    #@d
    .line 224
    .local v3, "listener":Landroid/support/v4/media/TransportStateListener;
    invoke-virtual {v3, p0}, Landroid/support/v4/media/TransportStateListener;->onPlayingChanged(Landroid/support/v4/media/TransportController;)V

    #@10
    .line 223
    add-int/lit8 v1, v1, 0x1

    #@12
    goto :goto_0

    #@13
    .line 227
    .end local v0    # "arr$":[Landroid/support/v4/media/TransportStateListener;
    .end local v1    # "i$":I
    .end local v2    # "len$":I
    .end local v3    # "listener":Landroid/support/v4/media/TransportStateListener;
    :cond_0
    return-void
.end method

.method private reportTransportControlsChanged()V
    .locals 5

    #@0
    .prologue
    .line 230
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->getListeners()[Landroid/support/v4/media/TransportStateListener;

    #@3
    move-result-object v4

    #@4
    .line 231
    .local v4, "listeners":[Landroid/support/v4/media/TransportStateListener;
    if-eqz v4, :cond_0

    #@6
    .line 232
    move-object v0, v4

    #@7
    .local v0, "arr$":[Landroid/support/v4/media/TransportStateListener;
    array-length v2, v0

    #@8
    .local v2, "len$":I
    const/4 v1, 0x0

    #@9
    .local v1, "i$":I
    :goto_0
    if-ge v1, v2, :cond_0

    #@b
    aget-object v3, v0, v1

    #@d
    .line 233
    .local v3, "listener":Landroid/support/v4/media/TransportStateListener;
    invoke-virtual {v3, p0}, Landroid/support/v4/media/TransportStateListener;->onTransportControlsChanged(Landroid/support/v4/media/TransportController;)V

    #@10
    .line 232
    add-int/lit8 v1, v1, 0x1

    #@12
    goto :goto_0

    #@13
    .line 236
    .end local v0    # "arr$":[Landroid/support/v4/media/TransportStateListener;
    .end local v1    # "i$":I
    .end local v2    # "len$":I
    .end local v3    # "listener":Landroid/support/v4/media/TransportStateListener;
    :cond_0
    return-void
.end method


# virtual methods
.method public destroy()V
    .locals 1

    #@0
    .prologue
    .line 341
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->destroy()V

    #@5
    .line 342
    return-void
.end method

.method public dispatchKeyEvent(Landroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 200
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mKeyEventCallback:Landroid/view/KeyEvent$Callback;

    #@2
    iget-object v1, p0, Landroid/support/v4/media/TransportMediator;->mDispatcherState:Ljava/lang/Object;

    #@4
    invoke-static {p1, v0, v1, p0}, Landroid/support/v4/view/KeyEventCompat;->dispatch(Landroid/view/KeyEvent;Landroid/view/KeyEvent$Callback;Ljava/lang/Object;Ljava/lang/Object;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getBufferPercentage()I
    .locals 1

    #@0
    .prologue
    .line 316
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onGetBufferPercentage()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getCurrentPosition()J
    .locals 2

    #@0
    .prologue
    .line 301
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onGetCurrentPosition()J

    #@5
    move-result-wide v0

    #@6
    return-wide v0
.end method

.method public getDuration()J
    .locals 2

    #@0
    .prologue
    .line 296
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onGetDuration()J

    #@5
    move-result-wide v0

    #@6
    return-wide v0
.end method

.method public getRemoteControlClient()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 190
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->getRemoteControlClient()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getTransportControlFlags()I
    .locals 1

    #@0
    .prologue
    .line 332
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onGetTransportControlFlags()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isPlaying()Z
    .locals 1

    #@0
    .prologue
    .line 311
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onIsPlaying()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public pausePlaying()V
    .locals 1

    #@0
    .prologue
    .line 272
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 273
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->pausePlaying()V

    #@9
    .line 275
    :cond_0
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@b
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onPause()V

    #@e
    .line 276
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->pushControllerState()V

    #@11
    .line 277
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->reportPlayingChanged()V

    #@14
    .line 278
    return-void
.end method

.method public refreshState()V
    .locals 0

    #@0
    .prologue
    .line 247
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->pushControllerState()V

    #@3
    .line 248
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->reportPlayingChanged()V

    #@6
    .line 249
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->reportTransportControlsChanged()V

    #@9
    .line 250
    return-void
.end method

.method public registerStateListener(Landroid/support/v4/media/TransportStateListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/media/TransportStateListener;

    #@0
    .prologue
    .line 204
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@5
    .line 205
    return-void
.end method

.method public seekTo(J)V
    .locals 1
    .param p1, "pos"    # J

    #@0
    .prologue
    .line 306
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/media/TransportPerformer;->onSeekTo(J)V

    #@5
    .line 307
    return-void
.end method

.method public startPlaying()V
    .locals 1

    #@0
    .prologue
    .line 258
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 259
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->startPlaying()V

    #@9
    .line 261
    :cond_0
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@b
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onStart()V

    #@e
    .line 262
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->pushControllerState()V

    #@11
    .line 263
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->reportPlayingChanged()V

    #@14
    .line 264
    return-void
.end method

.method public stopPlaying()V
    .locals 1

    #@0
    .prologue
    .line 286
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 287
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mController:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->stopPlaying()V

    #@9
    .line 289
    :cond_0
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mCallbacks:Landroid/support/v4/media/TransportPerformer;

    #@b
    invoke-virtual {v0}, Landroid/support/v4/media/TransportPerformer;->onStop()V

    #@e
    .line 290
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->pushControllerState()V

    #@11
    .line 291
    invoke-direct {p0}, Landroid/support/v4/media/TransportMediator;->reportPlayingChanged()V

    #@14
    .line 292
    return-void
.end method

.method public unregisterStateListener(Landroid/support/v4/media/TransportStateListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/media/TransportStateListener;

    #@0
    .prologue
    .line 208
    iget-object v0, p0, Landroid/support/v4/media/TransportMediator;->mListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@5
    .line 209
    return-void
.end method
