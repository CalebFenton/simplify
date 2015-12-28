.class interface abstract Landroid/support/v4/media/session/MediaSessionCompat$MediaSessionImpl;
.super Ljava/lang/Object;
.source "MediaSessionCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaSessionCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "MediaSessionImpl"
.end annotation


# virtual methods
.method public abstract getMediaSession()Ljava/lang/Object;
.end method

.method public abstract getSessionToken()Landroid/support/v4/media/session/MediaSessionCompat$Token;
.end method

.method public abstract isActive()Z
.end method

.method public abstract release()V
.end method

.method public abstract sendSessionEvent(Ljava/lang/String;Landroid/os/Bundle;)V
.end method

.method public abstract setActive(Z)V
.end method

.method public abstract setCallback(Landroid/support/v4/media/session/MediaSessionCompat$Callback;Landroid/os/Handler;)V
.end method

.method public abstract setFlags(I)V
.end method

.method public abstract setMetadata(Landroid/support/v4/media/MediaMetadataCompat;)V
.end method

.method public abstract setPlaybackState(Landroid/support/v4/media/session/PlaybackStateCompat;)V
.end method

.method public abstract setPlaybackToLocal(I)V
.end method

.method public abstract setPlaybackToRemote(Landroid/support/v4/media/VolumeProviderCompat;)V
.end method
