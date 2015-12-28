.class public Landroid/support/v4/media/session/MediaControllerCompatApi21$PlaybackInfo;
.super Ljava/lang/Object;
.source "MediaControllerCompatApi21.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaControllerCompatApi21;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "PlaybackInfo"
.end annotation


# static fields
.field private static final FLAG_SCO:I = 0x4

.field private static final STREAM_BLUETOOTH_SCO:I = 0x6

.field private static final STREAM_SYSTEM_ENFORCED:I = 0x7


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 118
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getAudioAttributes(Ljava/lang/Object;)Landroid/media/AudioAttributes;
    .locals 1
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 124
    check-cast p0, Landroid/media/session/MediaController$PlaybackInfo;

    #@2
    .end local p0    # "volumeInfoObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaController$PlaybackInfo;->getAudioAttributes()Landroid/media/AudioAttributes;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getCurrentVolume(Ljava/lang/Object;)I
    .locals 1
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 141
    check-cast p0, Landroid/media/session/MediaController$PlaybackInfo;

    #@2
    .end local p0    # "volumeInfoObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaController$PlaybackInfo;->getCurrentVolume()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getLegacyAudioStream(Ljava/lang/Object;)I
    .locals 2
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 128
    invoke-static {p0}, Landroid/support/v4/media/session/MediaControllerCompatApi21$PlaybackInfo;->getAudioAttributes(Ljava/lang/Object;)Landroid/media/AudioAttributes;

    #@3
    move-result-object v0

    #@4
    .line 129
    .local v0, "attrs":Landroid/media/AudioAttributes;
    invoke-static {v0}, Landroid/support/v4/media/session/MediaControllerCompatApi21$PlaybackInfo;->toLegacyStreamType(Landroid/media/AudioAttributes;)I

    #@7
    move-result v1

    #@8
    return v1
.end method

.method public static getMaxVolume(Ljava/lang/Object;)I
    .locals 1
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 137
    check-cast p0, Landroid/media/session/MediaController$PlaybackInfo;

    #@2
    .end local p0    # "volumeInfoObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaController$PlaybackInfo;->getMaxVolume()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getPlaybackType(Ljava/lang/Object;)I
    .locals 1
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 120
    check-cast p0, Landroid/media/session/MediaController$PlaybackInfo;

    #@2
    .end local p0    # "volumeInfoObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaController$PlaybackInfo;->getPlaybackType()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getVolumeControl(Ljava/lang/Object;)I
    .locals 1
    .param p0, "volumeInfoObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 133
    check-cast p0, Landroid/media/session/MediaController$PlaybackInfo;

    #@2
    .end local p0    # "volumeInfoObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaController$PlaybackInfo;->getVolumeControl()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method private static toLegacyStreamType(Landroid/media/AudioAttributes;)I
    .locals 4
    .param p0, "aa"    # Landroid/media/AudioAttributes;

    #@0
    .prologue
    const/4 v2, 0x4

    #@1
    const/4 v0, 0x3

    #@2
    const/4 v1, 0x1

    #@3
    .line 152
    invoke-virtual {p0}, Landroid/media/AudioAttributes;->getFlags()I

    #@6
    move-result v3

    #@7
    and-int/lit8 v3, v3, 0x1

    #@9
    if-ne v3, v1, :cond_0

    #@b
    .line 154
    const/4 v0, 0x7

    #@c
    .line 185
    :goto_0
    :pswitch_0
    return v0

    #@d
    .line 156
    :cond_0
    invoke-virtual {p0}, Landroid/media/AudioAttributes;->getFlags()I

    #@10
    move-result v3

    #@11
    and-int/lit8 v3, v3, 0x4

    #@13
    if-ne v3, v2, :cond_1

    #@15
    .line 157
    const/4 v0, 0x6

    #@16
    goto :goto_0

    #@17
    .line 161
    :cond_1
    invoke-virtual {p0}, Landroid/media/AudioAttributes;->getUsage()I

    #@1a
    move-result v3

    #@1b
    packed-switch v3, :pswitch_data_0

    #@1e
    goto :goto_0

    #@1f
    .line 170
    :pswitch_1
    const/4 v0, 0x0

    #@20
    goto :goto_0

    #@21
    :pswitch_2
    move v0, v1

    #@22
    .line 168
    goto :goto_0

    #@23
    .line 172
    :pswitch_3
    const/16 v0, 0x8

    #@25
    goto :goto_0

    #@26
    :pswitch_4
    move v0, v2

    #@27
    .line 174
    goto :goto_0

    #@28
    .line 176
    :pswitch_5
    const/4 v0, 0x2

    #@29
    goto :goto_0

    #@2a
    .line 182
    :pswitch_6
    const/4 v0, 0x5

    #@2b
    goto :goto_0

    #@2c
    .line 161
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_3
        :pswitch_4
        :pswitch_6
        :pswitch_5
        :pswitch_6
        :pswitch_6
        :pswitch_6
        :pswitch_6
        :pswitch_0
        :pswitch_0
        :pswitch_2
        :pswitch_0
    .end packed-switch
.end method
