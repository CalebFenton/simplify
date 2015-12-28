.class public abstract Landroid/support/v4/media/session/MediaSessionCompat$Callback;
.super Ljava/lang/Object;
.source "MediaSessionCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/MediaSessionCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "Callback"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/session/MediaSessionCompat$Callback$StubApi21;
    }
.end annotation


# instance fields
.field final mCallbackObj:Ljava/lang/Object;


# direct methods
.method public constructor <init>()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 268
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 269
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@6
    const/16 v1, 0x15

    #@8
    if-lt v0, v1, :cond_0

    #@a
    .line 270
    new-instance v0, Landroid/support/v4/media/session/MediaSessionCompat$Callback$StubApi21;

    #@c
    invoke-direct {v0, p0, v2}, Landroid/support/v4/media/session/MediaSessionCompat$Callback$StubApi21;-><init>(Landroid/support/v4/media/session/MediaSessionCompat$Callback;Landroid/support/v4/media/session/MediaSessionCompat$1;)V

    #@f
    invoke-static {v0}, Landroid/support/v4/media/session/MediaSessionCompatApi21;->createCallback(Landroid/support/v4/media/session/MediaSessionCompatApi21$Callback;)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    iput-object v0, p0, Landroid/support/v4/media/session/MediaSessionCompat$Callback;->mCallbackObj:Ljava/lang/Object;

    #@15
    .line 274
    :goto_0
    return-void

    #@16
    .line 272
    :cond_0
    iput-object v2, p0, Landroid/support/v4/media/session/MediaSessionCompat$Callback;->mCallbackObj:Ljava/lang/Object;

    #@18
    goto :goto_0
.end method


# virtual methods
.method public onCommand(Ljava/lang/String;Landroid/os/Bundle;Landroid/os/ResultReceiver;)V
    .locals 0
    .param p1, "command"    # Ljava/lang/String;
    .param p2, "extras"    # Landroid/os/Bundle;
    .param p3, "cb"    # Landroid/os/ResultReceiver;

    #@0
    .prologue
    .line 286
    return-void
.end method

.method public onFastForward()V
    .locals 0

    #@0
    .prologue
    .line 326
    return-void
.end method

.method public onMediaButtonEvent(Landroid/content/Intent;)Z
    .locals 1
    .param p1, "mediaButtonEvent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 295
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public onPause()V
    .locals 0

    #@0
    .prologue
    .line 308
    return-void
.end method

.method public onPlay()V
    .locals 0

    #@0
    .prologue
    .line 302
    return-void
.end method

.method public onRewind()V
    .locals 0

    #@0
    .prologue
    .line 332
    return-void
.end method

.method public onSeekTo(J)V
    .locals 0
    .param p1, "pos"    # J

    #@0
    .prologue
    .line 346
    return-void
.end method

.method public onSetRating(Landroid/support/v4/media/RatingCompat;)V
    .locals 0
    .param p1, "rating"    # Landroid/support/v4/media/RatingCompat;

    #@0
    .prologue
    .line 354
    return-void
.end method

.method public onSkipToNext()V
    .locals 0

    #@0
    .prologue
    .line 314
    return-void
.end method

.method public onSkipToPrevious()V
    .locals 0

    #@0
    .prologue
    .line 320
    return-void
.end method

.method public onStop()V
    .locals 0

    #@0
    .prologue
    .line 338
    return-void
.end method
