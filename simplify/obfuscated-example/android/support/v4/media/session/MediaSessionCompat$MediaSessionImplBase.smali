.class Landroid/support/v4/media/session/MediaSessionCompat$MediaSessionImplBase;
.super Ljava/lang/Object;
.source "MediaSessionCompat.java"

# interfaces
.implements Landroid/support/v4/media/session/MediaSessionCompat$MediaSessionImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaSessionCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "MediaSessionImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 480
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getMediaSession()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 529
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getSessionToken()Landroid/support/v4/media/session/MediaSessionCompat$Token;
    .locals 1

    #@0
    .prologue
    .line 516
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public isActive()Z
    .locals 1

    #@0
    .prologue
    .line 503
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public release()V
    .locals 0

    #@0
    .prologue
    .line 512
    return-void
.end method

.method public sendSessionEvent(Ljava/lang/String;Landroid/os/Bundle;)V
    .locals 0
    .param p1, "event"    # Ljava/lang/String;
    .param p2, "extras"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 508
    return-void
.end method

.method public setActive(Z)V
    .locals 0
    .param p1, "active"    # Z

    #@0
    .prologue
    .line 499
    return-void
.end method

.method public setCallback(Landroid/support/v4/media/session/MediaSessionCompat$Callback;Landroid/os/Handler;)V
    .locals 0
    .param p1, "callback"    # Landroid/support/v4/media/session/MediaSessionCompat$Callback;
    .param p2, "handler"    # Landroid/os/Handler;

    #@0
    .prologue
    .line 483
    return-void
.end method

.method public setFlags(I)V
    .locals 0
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 487
    return-void
.end method

.method public setMetadata(Landroid/support/v4/media/MediaMetadataCompat;)V
    .locals 0
    .param p1, "metadata"    # Landroid/support/v4/media/MediaMetadataCompat;

    #@0
    .prologue
    .line 525
    return-void
.end method

.method public setPlaybackState(Landroid/support/v4/media/session/PlaybackStateCompat;)V
    .locals 0
    .param p1, "state"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 521
    return-void
.end method

.method public setPlaybackToLocal(I)V
    .locals 0
    .param p1, "stream"    # I

    #@0
    .prologue
    .line 491
    return-void
.end method

.method public setPlaybackToRemote(Landroid/support/v4/media/VolumeProviderCompat;)V
    .locals 0
    .param p1, "volumeProvider"    # Landroid/support/v4/media/VolumeProviderCompat;

    #@0
    .prologue
    .line 495
    return-void
.end method
