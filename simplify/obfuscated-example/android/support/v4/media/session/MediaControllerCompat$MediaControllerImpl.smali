.class interface abstract Landroid/support/v4/media/session/MediaControllerCompat$MediaControllerImpl;
.super Ljava/lang/Object;
.source "MediaControllerCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaControllerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "MediaControllerImpl"
.end annotation


# virtual methods
.method public abstract dispatchMediaButtonEvent(Landroid/view/KeyEvent;)Z
.end method

.method public abstract getMediaController()Ljava/lang/Object;
.end method

.method public abstract getMetadata()Landroid/support/v4/media/MediaMetadataCompat;
.end method

.method public abstract getPlaybackInfo()Landroid/support/v4/media/session/MediaControllerCompat$PlaybackInfo;
.end method

.method public abstract getPlaybackState()Landroid/support/v4/media/session/PlaybackStateCompat;
.end method

.method public abstract getRatingType()I
.end method

.method public abstract getTransportControls()Landroid/support/v4/media/session/MediaControllerCompat$TransportControls;
.end method

.method public abstract registerCallback(Landroid/support/v4/media/session/MediaControllerCompat$Callback;Landroid/os/Handler;)V
.end method

.method public abstract sendCommand(Ljava/lang/String;Landroid/os/Bundle;Landroid/os/ResultReceiver;)V
.end method

.method public abstract unregisterCallback(Landroid/support/v4/media/session/MediaControllerCompat$Callback;)V
.end method
