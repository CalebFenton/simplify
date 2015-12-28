.class public Landroid/support/v4/widget/ContentLoadingProgressBar;
.super Landroid/widget/ProgressBar;
.source "ContentLoadingProgressBar.java"


# static fields
.field private static final MIN_DELAY:I = 0x1f4

.field private static final MIN_SHOW_TIME:I = 0x1f4


# instance fields
.field private final mDelayedHide:Ljava/lang/Runnable;

.field private final mDelayedShow:Ljava/lang/Runnable;

.field private mDismissed:Z

.field private mPostedHide:Z

.field private mPostedShow:Z

.field private mStartTime:J


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 65
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/widget/ContentLoadingProgressBar;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 66
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 69
    invoke-direct {p0, p1, p2, v2}, Landroid/widget/ProgressBar;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 34
    const-wide/16 v0, -0x1

    #@6
    iput-wide v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mStartTime:J

    #@8
    .line 36
    iput-boolean v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedHide:Z

    #@a
    .line 38
    iput-boolean v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedShow:Z

    #@c
    .line 40
    iput-boolean v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDismissed:Z

    #@e
    .line 42
    new-instance v0, Landroid/support/v4/widget/ContentLoadingProgressBar$1;

    #@10
    invoke-direct {v0, p0}, Landroid/support/v4/widget/ContentLoadingProgressBar$1;-><init>(Landroid/support/v4/widget/ContentLoadingProgressBar;)V

    #@13
    iput-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedHide:Ljava/lang/Runnable;

    #@15
    .line 52
    new-instance v0, Landroid/support/v4/widget/ContentLoadingProgressBar$2;

    #@17
    invoke-direct {v0, p0}, Landroid/support/v4/widget/ContentLoadingProgressBar$2;-><init>(Landroid/support/v4/widget/ContentLoadingProgressBar;)V

    #@1a
    iput-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedShow:Ljava/lang/Runnable;

    #@1c
    .line 70
    return-void
.end method

.method static synthetic access$002(Landroid/support/v4/widget/ContentLoadingProgressBar;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/ContentLoadingProgressBar;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 30
    iput-boolean p1, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedHide:Z

    #@2
    return p1
.end method

.method static synthetic access$102(Landroid/support/v4/widget/ContentLoadingProgressBar;J)J
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/ContentLoadingProgressBar;
    .param p1, "x1"    # J

    #@0
    .prologue
    .line 30
    iput-wide p1, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mStartTime:J

    #@2
    return-wide p1
.end method

.method static synthetic access$202(Landroid/support/v4/widget/ContentLoadingProgressBar;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/ContentLoadingProgressBar;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 30
    iput-boolean p1, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedShow:Z

    #@2
    return p1
.end method

.method static synthetic access$300(Landroid/support/v4/widget/ContentLoadingProgressBar;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/ContentLoadingProgressBar;

    #@0
    .prologue
    .line 30
    iget-boolean v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDismissed:Z

    #@2
    return v0
.end method

.method private removeCallbacks()V
    .locals 1

    #@0
    .prologue
    .line 85
    iget-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedHide:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@5
    .line 86
    iget-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedShow:Ljava/lang/Runnable;

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@a
    .line 87
    return-void
.end method


# virtual methods
.method public hide()V
    .locals 10

    #@0
    .prologue
    const-wide/16 v8, 0x1f4

    #@2
    const/4 v6, 0x1

    #@3
    .line 95
    iput-boolean v6, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDismissed:Z

    #@5
    .line 96
    iget-object v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedShow:Ljava/lang/Runnable;

    #@7
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@a
    .line 97
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    #@d
    move-result-wide v2

    #@e
    iget-wide v4, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mStartTime:J

    #@10
    sub-long v0, v2, v4

    #@12
    .line 98
    .local v0, "diff":J
    cmp-long v2, v0, v8

    #@14
    if-gez v2, :cond_0

    #@16
    iget-wide v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mStartTime:J

    #@18
    const-wide/16 v4, -0x1

    #@1a
    cmp-long v2, v2, v4

    #@1c
    if-nez v2, :cond_2

    #@1e
    .line 102
    :cond_0
    const/16 v2, 0x8

    #@20
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/ContentLoadingProgressBar;->setVisibility(I)V

    #@23
    .line 112
    :cond_1
    :goto_0
    return-void

    #@24
    .line 107
    :cond_2
    iget-boolean v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedHide:Z

    #@26
    if-nez v2, :cond_1

    #@28
    .line 108
    iget-object v2, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedHide:Ljava/lang/Runnable;

    #@2a
    sub-long v4, v8, v0

    #@2c
    invoke-virtual {p0, v2, v4, v5}, Landroid/support/v4/widget/ContentLoadingProgressBar;->postDelayed(Ljava/lang/Runnable;J)Z

    #@2f
    .line 109
    iput-boolean v6, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedHide:Z

    #@31
    goto :goto_0
.end method

.method public onAttachedToWindow()V
    .locals 0

    #@0
    .prologue
    .line 74
    invoke-super {p0}, Landroid/widget/ProgressBar;->onAttachedToWindow()V

    #@3
    .line 75
    invoke-direct {p0}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks()V

    #@6
    .line 76
    return-void
.end method

.method public onDetachedFromWindow()V
    .locals 0

    #@0
    .prologue
    .line 80
    invoke-super {p0}, Landroid/widget/ProgressBar;->onDetachedFromWindow()V

    #@3
    .line 81
    invoke-direct {p0}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks()V

    #@6
    .line 82
    return-void
.end method

.method public show()V
    .locals 4

    #@0
    .prologue
    .line 120
    const-wide/16 v0, -0x1

    #@2
    iput-wide v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mStartTime:J

    #@4
    .line 121
    const/4 v0, 0x0

    #@5
    iput-boolean v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDismissed:Z

    #@7
    .line 122
    iget-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedHide:Ljava/lang/Runnable;

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ContentLoadingProgressBar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@c
    .line 123
    iget-boolean v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedShow:Z

    #@e
    if-nez v0, :cond_0

    #@10
    .line 124
    iget-object v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mDelayedShow:Ljava/lang/Runnable;

    #@12
    const-wide/16 v2, 0x1f4

    #@14
    invoke-virtual {p0, v0, v2, v3}, Landroid/support/v4/widget/ContentLoadingProgressBar;->postDelayed(Ljava/lang/Runnable;J)Z

    #@17
    .line 125
    const/4 v0, 0x1

    #@18
    iput-boolean v0, p0, Landroid/support/v4/widget/ContentLoadingProgressBar;->mPostedShow:Z

    #@1a
    .line 127
    :cond_0
    return-void
.end method
