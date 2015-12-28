.class Landroid/support/v4/media/session/MediaSessionCompatApi21;
.super Ljava/lang/Object;
.source "MediaSessionCompatApi21.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/session/MediaSessionCompatApi21$CallbackProxy;,
        Landroid/support/v4/media/session/MediaSessionCompatApi21$Callback;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 109
    return-void
.end method

.method public static createCallback(Landroid/support/v4/media/session/MediaSessionCompatApi21$Callback;)Ljava/lang/Object;
    .locals 1
    .param p0, "callback"    # Landroid/support/v4/media/session/MediaSessionCompatApi21$Callback;

    #@0
    .prologue
    .line 45
    new-instance v0, Landroid/support/v4/media/session/MediaSessionCompatApi21$CallbackProxy;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/media/session/MediaSessionCompatApi21$CallbackProxy;-><init>(Landroid/support/v4/media/session/MediaSessionCompatApi21$Callback;)V

    #@5
    return-object v0
.end method

.method public static createSession(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/Object;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 34
    new-instance v0, Landroid/media/session/MediaSession;

    #@2
    invoke-direct {v0, p0, p1}, Landroid/media/session/MediaSession;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    #@5
    return-object v0
.end method

.method public static getSessionToken(Ljava/lang/Object;)Landroid/os/Parcelable;
    .locals 1
    .param p0, "sessionObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 84
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaSession;->getSessionToken()Landroid/media/session/MediaSession$Token;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static isActive(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "sessionObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 72
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaSession;->isActive()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static release(Ljava/lang/Object;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 80
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/session/MediaSession;->release()V

    #@5
    .line 81
    return-void
.end method

.method public static sendSessionEvent(Ljava/lang/Object;Ljava/lang/String;Landroid/os/Bundle;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "event"    # Ljava/lang/String;
    .param p2, "extras"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 76
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/media/session/MediaSession;->sendSessionEvent(Ljava/lang/String;Landroid/os/Bundle;)V

    #@5
    .line 77
    return-void
.end method

.method public static setActive(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "active"    # Z

    #@0
    .prologue
    .line 68
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/session/MediaSession;->setActive(Z)V

    #@5
    .line 69
    return-void
.end method

.method public static setCallback(Ljava/lang/Object;Ljava/lang/Object;Landroid/os/Handler;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "callbackObj"    # Ljava/lang/Object;
    .param p2, "handler"    # Landroid/os/Handler;

    #@0
    .prologue
    .line 49
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/session/MediaSession$Callback;

    #@4
    .end local p1    # "callbackObj":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/media/session/MediaSession;->setCallback(Landroid/media/session/MediaSession$Callback;Landroid/os/Handler;)V

    #@7
    .line 50
    return-void
.end method

.method public static setFlags(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "flags"    # I

    #@0
    .prologue
    .line 53
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/session/MediaSession;->setFlags(I)V

    #@5
    .line 54
    return-void
.end method

.method public static setMetadata(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "metadataObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 92
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/MediaMetadata;

    #@4
    .end local p1    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/session/MediaSession;->setMetadata(Landroid/media/MediaMetadata;)V

    #@7
    .line 93
    return-void
.end method

.method public static setPlaybackState(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "stateObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 88
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/session/PlaybackState;

    #@4
    .end local p1    # "stateObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/session/MediaSession;->setPlaybackState(Landroid/media/session/PlaybackState;)V

    #@7
    .line 89
    return-void
.end method

.method public static setPlaybackToLocal(Ljava/lang/Object;I)V
    .locals 2
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "stream"    # I

    #@0
    .prologue
    .line 58
    new-instance v0, Landroid/media/AudioAttributes$Builder;

    #@2
    invoke-direct {v0}, Landroid/media/AudioAttributes$Builder;-><init>()V

    #@5
    .line 59
    .local v0, "bob":Landroid/media/AudioAttributes$Builder;
    invoke-virtual {v0, p1}, Landroid/media/AudioAttributes$Builder;->setLegacyStreamType(I)Landroid/media/AudioAttributes$Builder;

    #@8
    .line 60
    check-cast p0, Landroid/media/session/MediaSession;

    #@a
    .end local p0    # "sessionObj":Ljava/lang/Object;
    invoke-virtual {v0}, Landroid/media/AudioAttributes$Builder;->build()Landroid/media/AudioAttributes;

    #@d
    move-result-object v1

    #@e
    invoke-virtual {p0, v1}, Landroid/media/session/MediaSession;->setPlaybackToLocal(Landroid/media/AudioAttributes;)V

    #@11
    .line 61
    return-void
.end method

.method public static setPlaybackToRemote(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "sessionObj"    # Ljava/lang/Object;
    .param p1, "volumeProviderObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 64
    check-cast p0, Landroid/media/session/MediaSession;

    #@2
    .end local p0    # "sessionObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/VolumeProvider;

    #@4
    .end local p1    # "volumeProviderObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/session/MediaSession;->setPlaybackToRemote(Landroid/media/VolumeProvider;)V

    #@7
    .line 65
    return-void
.end method

.method public static verifySession(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 2
    .param p0, "mediaSession"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 38
    instance-of v0, p0, Landroid/media/session/MediaSession;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 39
    return-object p0

    #@5
    .line 41
    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@7
    const-string v1, "mediaSession is not a valid MediaSession object"

    #@9
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@c
    throw v0
.end method
