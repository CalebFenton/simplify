.class Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;
.super Ljava/lang/Object;
.source "AutoScrollHelper.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/AutoScrollHelper;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "ClampedScroller"
.end annotation


# instance fields
.field private mDeltaTime:J

.field private mDeltaX:I

.field private mDeltaY:I

.field private mEffectiveRampDown:I

.field private mRampDownDuration:I

.field private mRampUpDuration:I

.field private mStartTime:J

.field private mStopTime:J

.field private mStopValue:F

.field private mTargetVelocityX:F

.field private mTargetVelocityY:F


# direct methods
.method public constructor <init>()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 743
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 744
    const-wide/high16 v0, -0x8000000000000000L

    #@6
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@8
    .line 745
    const-wide/16 v0, -0x1

    #@a
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@c
    .line 746
    const-wide/16 v0, 0x0

    #@e
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaTime:J

    #@10
    .line 747
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaX:I

    #@12
    .line 748
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaY:I

    #@14
    .line 749
    return-void
.end method

.method private getValueAt(J)F
    .locals 11
    .param p1, "currentTime"    # J

    #@0
    .prologue
    const/high16 v10, 0x3f800000    # 1.0f

    #@2
    const/4 v4, 0x0

    #@3
    .line 787
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@5
    cmp-long v5, p1, v6

    #@7
    if-gez v5, :cond_0

    #@9
    .line 794
    :goto_0
    return v4

    #@a
    .line 789
    :cond_0
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@c
    const-wide/16 v8, 0x0

    #@e
    cmp-long v5, v6, v8

    #@10
    if-ltz v5, :cond_1

    #@12
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@14
    cmp-long v5, p1, v6

    #@16
    if-gez v5, :cond_2

    #@18
    .line 790
    :cond_1
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@1a
    sub-long v2, p1, v6

    #@1c
    .line 791
    .local v2, "elapsedSinceStart":J
    const/high16 v5, 0x3f000000    # 0.5f

    #@1e
    long-to-float v6, v2

    #@1f
    iget v7, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mRampUpDuration:I

    #@21
    int-to-float v7, v7

    #@22
    div-float/2addr v6, v7

    #@23
    # invokes: Landroid/support/v4/widget/AutoScrollHelper;->constrain(FFF)F
    invoke-static {v6, v4, v10}, Landroid/support/v4/widget/AutoScrollHelper;->access$900(FFF)F

    #@26
    move-result v4

    #@27
    mul-float/2addr v4, v5

    #@28
    goto :goto_0

    #@29
    .line 793
    .end local v2    # "elapsedSinceStart":J
    :cond_2
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@2b
    sub-long v0, p1, v6

    #@2d
    .line 794
    .local v0, "elapsedSinceEnd":J
    iget v5, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopValue:F

    #@2f
    sub-float v5, v10, v5

    #@31
    iget v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopValue:F

    #@33
    long-to-float v7, v0

    #@34
    iget v8, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mEffectiveRampDown:I

    #@36
    int-to-float v8, v8

    #@37
    div-float/2addr v7, v8

    #@38
    # invokes: Landroid/support/v4/widget/AutoScrollHelper;->constrain(FFF)F
    invoke-static {v7, v4, v10}, Landroid/support/v4/widget/AutoScrollHelper;->access$900(FFF)F

    #@3b
    move-result v4

    #@3c
    mul-float/2addr v4, v6

    #@3d
    add-float/2addr v4, v5

    #@3e
    goto :goto_0
.end method

.method private interpolateValue(F)F
    .locals 2
    .param p1, "value"    # F

    #@0
    .prologue
    .line 807
    const/high16 v0, -0x3f800000    # -4.0f

    #@2
    mul-float/2addr v0, p1

    #@3
    mul-float/2addr v0, p1

    #@4
    const/high16 v1, 0x40800000    # 4.0f

    #@6
    mul-float/2addr v1, p1

    #@7
    add-float/2addr v0, v1

    #@8
    return v0
.end method


# virtual methods
.method public computeScrollDelta()V
    .locals 10

    #@0
    .prologue
    .line 818
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaTime:J

    #@2
    const-wide/16 v8, 0x0

    #@4
    cmp-long v6, v6, v8

    #@6
    if-nez v6, :cond_0

    #@8
    .line 819
    new-instance v6, Ljava/lang/RuntimeException;

    #@a
    const-string v7, "Cannot compute scroll delta before calling start()"

    #@c
    invoke-direct {v6, v7}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@f
    throw v6

    #@10
    .line 822
    :cond_0
    invoke-static {}, Landroid/view/animation/AnimationUtils;->currentAnimationTimeMillis()J

    #@13
    move-result-wide v0

    #@14
    .line 823
    .local v0, "currentTime":J
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->getValueAt(J)F

    #@17
    move-result v5

    #@18
    .line 824
    .local v5, "value":F
    invoke-direct {p0, v5}, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->interpolateValue(F)F

    #@1b
    move-result v4

    #@1c
    .line 825
    .local v4, "scale":F
    iget-wide v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaTime:J

    #@1e
    sub-long v2, v0, v6

    #@20
    .line 827
    .local v2, "elapsedSinceDelta":J
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaTime:J

    #@22
    .line 828
    long-to-float v6, v2

    #@23
    mul-float/2addr v6, v4

    #@24
    iget v7, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityX:F

    #@26
    mul-float/2addr v6, v7

    #@27
    float-to-int v6, v6

    #@28
    iput v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaX:I

    #@2a
    .line 829
    long-to-float v6, v2

    #@2b
    mul-float/2addr v6, v4

    #@2c
    iget v7, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityY:F

    #@2e
    mul-float/2addr v6, v7

    #@2f
    float-to-int v6, v6

    #@30
    iput v6, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaY:I

    #@32
    .line 830
    return-void
.end method

.method public getDeltaX()I
    .locals 1

    #@0
    .prologue
    .line 856
    iget v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaX:I

    #@2
    return v0
.end method

.method public getDeltaY()I
    .locals 1

    #@0
    .prologue
    .line 864
    iget v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaY:I

    #@2
    return v0
.end method

.method public getHorizontalDirection()I
    .locals 2

    #@0
    .prologue
    .line 844
    iget v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityX:F

    #@2
    iget v1, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityX:F

    #@4
    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    #@7
    move-result v1

    #@8
    div-float/2addr v0, v1

    #@9
    float-to-int v0, v0

    #@a
    return v0
.end method

.method public getVerticalDirection()I
    .locals 2

    #@0
    .prologue
    .line 848
    iget v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityY:F

    #@2
    iget v1, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityY:F

    #@4
    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    #@7
    move-result v1

    #@8
    div-float/2addr v0, v1

    #@9
    float-to-int v0, v0

    #@a
    return v0
.end method

.method public isFinished()Z
    .locals 6

    #@0
    .prologue
    .line 782
    iget-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@2
    const-wide/16 v2, 0x0

    #@4
    cmp-long v0, v0, v2

    #@6
    if-lez v0, :cond_0

    #@8
    invoke-static {}, Landroid/view/animation/AnimationUtils;->currentAnimationTimeMillis()J

    #@b
    move-result-wide v0

    #@c
    iget-wide v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@e
    iget v4, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mEffectiveRampDown:I

    #@10
    int-to-long v4, v4

    #@11
    add-long/2addr v2, v4

    #@12
    cmp-long v0, v0, v2

    #@14
    if-lez v0, :cond_0

    #@16
    const/4 v0, 0x1

    #@17
    :goto_0
    return v0

    #@18
    :cond_0
    const/4 v0, 0x0

    #@19
    goto :goto_0
.end method

.method public requestStop()V
    .locals 5

    #@0
    .prologue
    .line 775
    invoke-static {}, Landroid/view/animation/AnimationUtils;->currentAnimationTimeMillis()J

    #@3
    move-result-wide v0

    #@4
    .line 776
    .local v0, "currentTime":J
    iget-wide v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@6
    sub-long v2, v0, v2

    #@8
    long-to-int v2, v2

    #@9
    const/4 v3, 0x0

    #@a
    iget v4, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mRampDownDuration:I

    #@c
    # invokes: Landroid/support/v4/widget/AutoScrollHelper;->constrain(III)I
    invoke-static {v2, v3, v4}, Landroid/support/v4/widget/AutoScrollHelper;->access$800(III)I

    #@f
    move-result v2

    #@10
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mEffectiveRampDown:I

    #@12
    .line 777
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->getValueAt(J)F

    #@15
    move-result v2

    #@16
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopValue:F

    #@18
    .line 778
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@1a
    .line 779
    return-void
.end method

.method public setRampDownDuration(I)V
    .locals 0
    .param p1, "durationMillis"    # I

    #@0
    .prologue
    .line 756
    iput p1, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mRampDownDuration:I

    #@2
    .line 757
    return-void
.end method

.method public setRampUpDuration(I)V
    .locals 0
    .param p1, "durationMillis"    # I

    #@0
    .prologue
    .line 752
    iput p1, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mRampUpDuration:I

    #@2
    .line 753
    return-void
.end method

.method public setTargetVelocity(FF)V
    .locals 0
    .param p1, "x"    # F
    .param p2, "y"    # F

    #@0
    .prologue
    .line 839
    iput p1, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityX:F

    #@2
    .line 840
    iput p2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mTargetVelocityY:F

    #@4
    .line 841
    return-void
.end method

.method public start()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 763
    invoke-static {}, Landroid/view/animation/AnimationUtils;->currentAnimationTimeMillis()J

    #@4
    move-result-wide v0

    #@5
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@7
    .line 764
    const-wide/16 v0, -0x1

    #@9
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopTime:J

    #@b
    .line 765
    iget-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStartTime:J

    #@d
    iput-wide v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaTime:J

    #@f
    .line 766
    const/high16 v0, 0x3f000000    # 0.5f

    #@11
    iput v0, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mStopValue:F

    #@13
    .line 767
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaX:I

    #@15
    .line 768
    iput v2, p0, Landroid/support/v4/widget/AutoScrollHelper$ClampedScroller;->mDeltaY:I

    #@17
    .line 769
    return-void
.end method
