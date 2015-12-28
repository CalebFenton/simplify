.class public abstract Landroid/support/v4/media/session/MediaControllerCompat$TransportControls;
.super Ljava/lang/Object;
.source "MediaControllerCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaControllerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "TransportControls"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 301
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 302
    return-void
.end method


# virtual methods
.method public abstract fastForward()V
.end method

.method public abstract pause()V
.end method

.method public abstract play()V
.end method

.method public abstract rewind()V
.end method

.method public abstract seekTo(J)V
.end method

.method public abstract setRating(Landroid/support/v4/media/RatingCompat;)V
.end method

.method public abstract skipToNext()V
.end method

.method public abstract skipToPrevious()V
.end method

.method public abstract stop()V
.end method
