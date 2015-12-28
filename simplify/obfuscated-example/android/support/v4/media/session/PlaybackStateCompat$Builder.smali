.class public final Landroid/support/v4/media/session/PlaybackStateCompat$Builder;
.super Ljava/lang/Object;
.source "PlaybackStateCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/session/PlaybackStateCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Builder"
.end annotation


# instance fields
.field private mActions:J

.field private mBufferedPosition:J

.field private mErrorMessage:Ljava/lang/CharSequence;

.field private mPosition:J

.field private mRate:F

.field private mState:I

.field private mUpdateTime:J


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 422
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 423
    return-void
.end method

.method public constructor <init>(Landroid/support/v4/media/session/PlaybackStateCompat;)V
    .locals 2
    .param p1, "source"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 431
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 432
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$100(Landroid/support/v4/media/session/PlaybackStateCompat;)I

    #@6
    move-result v0

    #@7
    iput v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mState:I

    #@9
    .line 433
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$200(Landroid/support/v4/media/session/PlaybackStateCompat;)J

    #@c
    move-result-wide v0

    #@d
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mPosition:J

    #@f
    .line 434
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$300(Landroid/support/v4/media/session/PlaybackStateCompat;)F

    #@12
    move-result v0

    #@13
    iput v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mRate:F

    #@15
    .line 435
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$400(Landroid/support/v4/media/session/PlaybackStateCompat;)J

    #@18
    move-result-wide v0

    #@19
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mUpdateTime:J

    #@1b
    .line 436
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$500(Landroid/support/v4/media/session/PlaybackStateCompat;)J

    #@1e
    move-result-wide v0

    #@1f
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mBufferedPosition:J

    #@21
    .line 437
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$600(Landroid/support/v4/media/session/PlaybackStateCompat;)J

    #@24
    move-result-wide v0

    #@25
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mActions:J

    #@27
    .line 438
    # getter for: Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;
    invoke-static {p1}, Landroid/support/v4/media/session/PlaybackStateCompat;->access$700(Landroid/support/v4/media/session/PlaybackStateCompat;)Ljava/lang/CharSequence;

    #@2a
    move-result-object v0

    #@2b
    iput-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mErrorMessage:Ljava/lang/CharSequence;

    #@2d
    .line 439
    return-void
.end method


# virtual methods
.method public build()Landroid/support/v4/media/session/PlaybackStateCompat;
    .locals 13

    #@0
    .prologue
    .line 515
    new-instance v0, Landroid/support/v4/media/session/PlaybackStateCompat;

    #@2
    iget v1, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mState:I

    #@4
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mPosition:J

    #@6
    iget-wide v4, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mBufferedPosition:J

    #@8
    iget v6, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mRate:F

    #@a
    iget-wide v7, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mActions:J

    #@c
    iget-object v9, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mErrorMessage:Ljava/lang/CharSequence;

    #@e
    iget-wide v10, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mUpdateTime:J

    #@10
    const/4 v12, 0x0

    #@11
    invoke-direct/range {v0 .. v12}, Landroid/support/v4/media/session/PlaybackStateCompat;-><init>(IJJFJLjava/lang/CharSequence;JLandroid/support/v4/media/session/PlaybackStateCompat$1;)V

    #@14
    return-object v0
.end method

.method public setActions(J)V
    .locals 1
    .param p1, "capabilities"    # J

    #@0
    .prologue
    .line 500
    iput-wide p1, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mActions:J

    #@2
    .line 501
    return-void
.end method

.method public setBufferedPosition(J)V
    .locals 1
    .param p1, "bufferPosition"    # J

    #@0
    .prologue
    .line 481
    iput-wide p1, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mBufferedPosition:J

    #@2
    .line 482
    return-void
.end method

.method public setErrorMessage(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "errorMessage"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 508
    iput-object p1, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mErrorMessage:Ljava/lang/CharSequence;

    #@2
    .line 509
    return-void
.end method

.method public setState(IJF)V
    .locals 2
    .param p1, "state"    # I
    .param p2, "position"    # J
    .param p4, "playbackRate"    # F

    #@0
    .prologue
    .line 469
    iput p1, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mState:I

    #@2
    .line 470
    iput-wide p2, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mPosition:J

    #@4
    .line 471
    iput p4, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mRate:F

    #@6
    .line 472
    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    #@9
    move-result-wide v0

    #@a
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat$Builder;->mUpdateTime:J

    #@c
    .line 473
    return-void
.end method
