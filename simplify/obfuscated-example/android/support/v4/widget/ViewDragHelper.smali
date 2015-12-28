.class public Landroid/support/v4/widget/ViewDragHelper;
.super Ljava/lang/Object;
.source "ViewDragHelper.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/ViewDragHelper$Callback;
    }
.end annotation


# static fields
.field private static final BASE_SETTLE_DURATION:I = 0x100

.field public static final DIRECTION_ALL:I = 0x3

.field public static final DIRECTION_HORIZONTAL:I = 0x1

.field public static final DIRECTION_VERTICAL:I = 0x2

.field public static final EDGE_ALL:I = 0xf

.field public static final EDGE_BOTTOM:I = 0x8

.field public static final EDGE_LEFT:I = 0x1

.field public static final EDGE_RIGHT:I = 0x2

.field private static final EDGE_SIZE:I = 0x14

.field public static final EDGE_TOP:I = 0x4

.field public static final INVALID_POINTER:I = -0x1

.field private static final MAX_SETTLE_DURATION:I = 0x258

.field public static final STATE_DRAGGING:I = 0x1

.field public static final STATE_IDLE:I = 0x0

.field public static final STATE_SETTLING:I = 0x2

.field private static final TAG:Ljava/lang/String; = "ViewDragHelper"

.field private static final sInterpolator:Landroid/view/animation/Interpolator;


# instance fields
.field private mActivePointerId:I

.field private final mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

.field private mCapturedView:Landroid/view/View;

.field private mDragState:I

.field private mEdgeDragsInProgress:[I

.field private mEdgeDragsLocked:[I

.field private mEdgeSize:I

.field private mInitialEdgesTouched:[I

.field private mInitialMotionX:[F

.field private mInitialMotionY:[F

.field private mLastMotionX:[F

.field private mLastMotionY:[F

.field private mMaxVelocity:F

.field private mMinVelocity:F

.field private final mParentView:Landroid/view/ViewGroup;

.field private mPointersDown:I

.field private mReleaseInProgress:Z

.field private mScroller:Landroid/support/v4/widget/ScrollerCompat;

.field private final mSetIdleRunnable:Ljava/lang/Runnable;

.field private mTouchSlop:I

.field private mTrackingEdges:I

.field private mVelocityTracker:Landroid/view/VelocityTracker;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 326
    new-instance v0, Landroid/support/v4/widget/ViewDragHelper$1;

    #@2
    invoke-direct {v0}, Landroid/support/v4/widget/ViewDragHelper$1;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/widget/ViewDragHelper;->sInterpolator:Landroid/view/animation/Interpolator;

    #@7
    return-void
.end method

.method private constructor <init>(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/support/v4/widget/ViewDragHelper$Callback;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "forParent"    # Landroid/view/ViewGroup;
    .param p3, "cb"    # Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@0
    .prologue
    .line 373
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 115
    const/4 v2, -0x1

    #@4
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@6
    .line 333
    new-instance v2, Landroid/support/v4/widget/ViewDragHelper$2;

    #@8
    invoke-direct {v2, p0}, Landroid/support/v4/widget/ViewDragHelper$2;-><init>(Landroid/support/v4/widget/ViewDragHelper;)V

    #@b
    iput-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mSetIdleRunnable:Ljava/lang/Runnable;

    #@d
    .line 374
    if-nez p2, :cond_0

    #@f
    .line 375
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@11
    const-string v3, "Parent view may not be null"

    #@13
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@16
    throw v2

    #@17
    .line 377
    :cond_0
    if-nez p3, :cond_1

    #@19
    .line 378
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@1b
    const-string v3, "Callback may not be null"

    #@1d
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@20
    throw v2

    #@21
    .line 381
    :cond_1
    iput-object p2, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@23
    .line 382
    iput-object p3, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@25
    .line 384
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@28
    move-result-object v1

    #@29
    .line 385
    .local v1, "vc":Landroid/view/ViewConfiguration;
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@2c
    move-result-object v2

    #@2d
    invoke-virtual {v2}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@30
    move-result-object v2

    #@31
    iget v0, v2, Landroid/util/DisplayMetrics;->density:F

    #@33
    .line 386
    .local v0, "density":F
    const/high16 v2, 0x41a00000    # 20.0f

    #@35
    mul-float/2addr v2, v0

    #@36
    const/high16 v3, 0x3f000000    # 0.5f

    #@38
    add-float/2addr v2, v3

    #@39
    float-to-int v2, v2

    #@3a
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@3c
    .line 388
    invoke-virtual {v1}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@3f
    move-result v2

    #@40
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@42
    .line 389
    invoke-virtual {v1}, Landroid/view/ViewConfiguration;->getScaledMaximumFlingVelocity()I

    #@45
    move-result v2

    #@46
    int-to-float v2, v2

    #@47
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@49
    .line 390
    invoke-virtual {v1}, Landroid/view/ViewConfiguration;->getScaledMinimumFlingVelocity()I

    #@4c
    move-result v2

    #@4d
    int-to-float v2, v2

    #@4e
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@50
    .line 391
    sget-object v2, Landroid/support/v4/widget/ViewDragHelper;->sInterpolator:Landroid/view/animation/Interpolator;

    #@52
    invoke-static {p1, v2}, Landroid/support/v4/widget/ScrollerCompat;->create(Landroid/content/Context;Landroid/view/animation/Interpolator;)Landroid/support/v4/widget/ScrollerCompat;

    #@55
    move-result-object v2

    #@56
    iput-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@58
    .line 392
    return-void
.end method

.method private checkNewEdgeDrag(FFII)Z
    .locals 5
    .param p1, "delta"    # F
    .param p2, "odelta"    # F
    .param p3, "pointerId"    # I
    .param p4, "edge"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1248
    invoke-static {p1}, Ljava/lang/Math;->abs(F)F

    #@4
    move-result v0

    #@5
    .line 1249
    .local v0, "absDelta":F
    invoke-static {p2}, Ljava/lang/Math;->abs(F)F

    #@8
    move-result v1

    #@9
    .line 1251
    .local v1, "absODelta":F
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@b
    aget v3, v3, p3

    #@d
    and-int/2addr v3, p4

    #@e
    if-ne v3, p4, :cond_0

    #@10
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@12
    and-int/2addr v3, p4

    #@13
    if-eqz v3, :cond_0

    #@15
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@17
    aget v3, v3, p3

    #@19
    and-int/2addr v3, p4

    #@1a
    if-eq v3, p4, :cond_0

    #@1c
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@1e
    aget v3, v3, p3

    #@20
    and-int/2addr v3, p4

    #@21
    if-eq v3, p4, :cond_0

    #@23
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@25
    int-to-float v3, v3

    #@26
    cmpg-float v3, v0, v3

    #@28
    if-gtz v3, :cond_1

    #@2a
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@2c
    int-to-float v3, v3

    #@2d
    cmpg-float v3, v1, v3

    #@2f
    if-gtz v3, :cond_1

    #@31
    .line 1261
    :cond_0
    :goto_0
    return v2

    #@32
    .line 1257
    :cond_1
    const/high16 v3, 0x3f000000    # 0.5f

    #@34
    mul-float/2addr v3, v1

    #@35
    cmpg-float v3, v0, v3

    #@37
    if-gez v3, :cond_2

    #@39
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@3b
    invoke-virtual {v3, p4}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeLock(I)Z

    #@3e
    move-result v3

    #@3f
    if-eqz v3, :cond_2

    #@41
    .line 1258
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@43
    aget v4, v3, p3

    #@45
    or-int/2addr v4, p4

    #@46
    aput v4, v3, p3

    #@48
    goto :goto_0

    #@49
    .line 1261
    :cond_2
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@4b
    aget v3, v3, p3

    #@4d
    and-int/2addr v3, p4

    #@4e
    if-nez v3, :cond_0

    #@50
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@52
    int-to-float v3, v3

    #@53
    cmpl-float v3, v0, v3

    #@55
    if-lez v3, :cond_0

    #@57
    const/4 v2, 0x1

    #@58
    goto :goto_0
.end method

.method private checkTouchSlop(Landroid/view/View;FF)Z
    .locals 7
    .param p1, "child"    # Landroid/view/View;
    .param p2, "dx"    # F
    .param p3, "dy"    # F

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 1275
    if-nez p1, :cond_1

    #@4
    move v2, v3

    #@5
    .line 1288
    :cond_0
    :goto_0
    return v2

    #@6
    .line 1278
    :cond_1
    iget-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@8
    invoke-virtual {v4, p1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewHorizontalDragRange(Landroid/view/View;)I

    #@b
    move-result v4

    #@c
    if-lez v4, :cond_2

    #@e
    move v0, v2

    #@f
    .line 1279
    .local v0, "checkHorizontal":Z
    :goto_1
    iget-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@11
    invoke-virtual {v4, p1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewVerticalDragRange(Landroid/view/View;)I

    #@14
    move-result v4

    #@15
    if-lez v4, :cond_3

    #@17
    move v1, v2

    #@18
    .line 1281
    .local v1, "checkVertical":Z
    :goto_2
    if-eqz v0, :cond_4

    #@1a
    if-eqz v1, :cond_4

    #@1c
    .line 1282
    mul-float v4, p2, p2

    #@1e
    mul-float v5, p3, p3

    #@20
    add-float/2addr v4, v5

    #@21
    iget v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@23
    iget v6, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@25
    mul-int/2addr v5, v6

    #@26
    int-to-float v5, v5

    #@27
    cmpl-float v4, v4, v5

    #@29
    if-gtz v4, :cond_0

    #@2b
    move v2, v3

    #@2c
    goto :goto_0

    #@2d
    .end local v0    # "checkHorizontal":Z
    .end local v1    # "checkVertical":Z
    :cond_2
    move v0, v3

    #@2e
    .line 1278
    goto :goto_1

    #@2f
    .restart local v0    # "checkHorizontal":Z
    :cond_3
    move v1, v3

    #@30
    .line 1279
    goto :goto_2

    #@31
    .line 1283
    .restart local v1    # "checkVertical":Z
    :cond_4
    if-eqz v0, :cond_5

    #@33
    .line 1284
    invoke-static {p2}, Ljava/lang/Math;->abs(F)F

    #@36
    move-result v4

    #@37
    iget v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@39
    int-to-float v5, v5

    #@3a
    cmpl-float v4, v4, v5

    #@3c
    if-gtz v4, :cond_0

    #@3e
    move v2, v3

    #@3f
    goto :goto_0

    #@40
    .line 1285
    :cond_5
    if-eqz v1, :cond_6

    #@42
    .line 1286
    invoke-static {p3}, Ljava/lang/Math;->abs(F)F

    #@45
    move-result v4

    #@46
    iget v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@48
    int-to-float v5, v5

    #@49
    cmpl-float v4, v4, v5

    #@4b
    if-gtz v4, :cond_0

    #@4d
    move v2, v3

    #@4e
    goto :goto_0

    #@4f
    :cond_6
    move v2, v3

    #@50
    .line 1288
    goto :goto_0
.end method

.method private clampMag(FFF)F
    .locals 3
    .param p1, "value"    # F
    .param p2, "absMin"    # F
    .param p3, "absMax"    # F

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 674
    invoke-static {p1}, Ljava/lang/Math;->abs(F)F

    #@4
    move-result v0

    #@5
    .line 675
    .local v0, "absValue":F
    cmpg-float v2, v0, p2

    #@7
    if-gez v2, :cond_1

    #@9
    move p3, v1

    #@a
    .line 677
    .end local p3    # "absMax":F
    :cond_0
    :goto_0
    return p3

    #@b
    .line 676
    .restart local p3    # "absMax":F
    :cond_1
    cmpl-float v2, v0, p3

    #@d
    if-lez v2, :cond_2

    #@f
    cmpl-float v1, p1, v1

    #@11
    if-gtz v1, :cond_0

    #@13
    neg-float p3, p3

    #@14
    goto :goto_0

    #@15
    :cond_2
    move p3, p1

    #@16
    .line 677
    goto :goto_0
.end method

.method private clampMag(III)I
    .locals 1
    .param p1, "value"    # I
    .param p2, "absMin"    # I
    .param p3, "absMax"    # I

    #@0
    .prologue
    .line 657
    invoke-static {p1}, Ljava/lang/Math;->abs(I)I

    #@3
    move-result v0

    #@4
    .line 658
    .local v0, "absValue":I
    if-ge v0, p2, :cond_1

    #@6
    const/4 p3, 0x0

    #@7
    .line 660
    .end local p3    # "absMax":I
    :cond_0
    :goto_0
    return p3

    #@8
    .line 659
    .restart local p3    # "absMax":I
    :cond_1
    if-le v0, p3, :cond_2

    #@a
    if-gtz p1, :cond_0

    #@c
    neg-int p3, p3

    #@d
    goto :goto_0

    #@e
    :cond_2
    move p3, p1

    #@f
    .line 660
    goto :goto_0
.end method

.method private clearMotionHistory()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 777
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@4
    if-nez v0, :cond_0

    #@6
    .line 788
    :goto_0
    return-void

    #@7
    .line 780
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@9
    invoke-static {v0, v1}, Ljava/util/Arrays;->fill([FF)V

    #@c
    .line 781
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@e
    invoke-static {v0, v1}, Ljava/util/Arrays;->fill([FF)V

    #@11
    .line 782
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@13
    invoke-static {v0, v1}, Ljava/util/Arrays;->fill([FF)V

    #@16
    .line 783
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@18
    invoke-static {v0, v1}, Ljava/util/Arrays;->fill([FF)V

    #@1b
    .line 784
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@1d
    invoke-static {v0, v2}, Ljava/util/Arrays;->fill([II)V

    #@20
    .line 785
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@22
    invoke-static {v0, v2}, Ljava/util/Arrays;->fill([II)V

    #@25
    .line 786
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@27
    invoke-static {v0, v2}, Ljava/util/Arrays;->fill([II)V

    #@2a
    .line 787
    iput v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@2c
    goto :goto_0
.end method

.method private clearMotionHistory(I)V
    .locals 3
    .param p1, "pointerId"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 791
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@4
    if-nez v0, :cond_0

    #@6
    .line 802
    :goto_0
    return-void

    #@7
    .line 794
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@9
    aput v1, v0, p1

    #@b
    .line 795
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@d
    aput v1, v0, p1

    #@f
    .line 796
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@11
    aput v1, v0, p1

    #@13
    .line 797
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@15
    aput v1, v0, p1

    #@17
    .line 798
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@19
    aput v2, v0, p1

    #@1b
    .line 799
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@1d
    aput v2, v0, p1

    #@1f
    .line 800
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@21
    aput v2, v0, p1

    #@23
    .line 801
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@25
    const/4 v1, 0x1

    #@26
    shl-int/2addr v1, p1

    #@27
    xor-int/lit8 v1, v1, -0x1

    #@29
    and-int/2addr v0, v1

    #@2a
    iput v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@2c
    goto :goto_0
.end method

.method private computeAxisDuration(III)I
    .locals 10
    .param p1, "delta"    # I
    .param p2, "velocity"    # I
    .param p3, "motionRange"    # I

    #@0
    .prologue
    const/high16 v9, 0x3f800000    # 1.0f

    #@2
    .line 625
    if-nez p1, :cond_0

    #@4
    .line 626
    const/4 v6, 0x0

    #@5
    .line 643
    :goto_0
    return v6

    #@6
    .line 629
    :cond_0
    iget-object v6, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@8
    invoke-virtual {v6}, Landroid/view/ViewGroup;->getWidth()I

    #@b
    move-result v5

    #@c
    .line 630
    .local v5, "width":I
    div-int/lit8 v3, v5, 0x2

    #@e
    .line 631
    .local v3, "halfWidth":I
    invoke-static {p1}, Ljava/lang/Math;->abs(I)I

    #@11
    move-result v6

    #@12
    int-to-float v6, v6

    #@13
    int-to-float v7, v5

    #@14
    div-float/2addr v6, v7

    #@15
    invoke-static {v9, v6}, Ljava/lang/Math;->min(FF)F

    #@18
    move-result v1

    #@19
    .line 632
    .local v1, "distanceRatio":F
    int-to-float v6, v3

    #@1a
    int-to-float v7, v3

    #@1b
    invoke-direct {p0, v1}, Landroid/support/v4/widget/ViewDragHelper;->distanceInfluenceForSnapDuration(F)F

    #@1e
    move-result v8

    #@1f
    mul-float/2addr v7, v8

    #@20
    add-float v0, v6, v7

    #@22
    .line 636
    .local v0, "distance":F
    invoke-static {p2}, Ljava/lang/Math;->abs(I)I

    #@25
    move-result p2

    #@26
    .line 637
    if-lez p2, :cond_1

    #@28
    .line 638
    const/high16 v6, 0x447a0000    # 1000.0f

    #@2a
    int-to-float v7, p2

    #@2b
    div-float v7, v0, v7

    #@2d
    invoke-static {v7}, Ljava/lang/Math;->abs(F)F

    #@30
    move-result v7

    #@31
    mul-float/2addr v6, v7

    #@32
    invoke-static {v6}, Ljava/lang/Math;->round(F)I

    #@35
    move-result v6

    #@36
    mul-int/lit8 v2, v6, 0x4

    #@38
    .line 643
    .local v2, "duration":I
    :goto_1
    const/16 v6, 0x258

    #@3a
    invoke-static {v2, v6}, Ljava/lang/Math;->min(II)I

    #@3d
    move-result v6

    #@3e
    goto :goto_0

    #@3f
    .line 640
    .end local v2    # "duration":I
    :cond_1
    invoke-static {p1}, Ljava/lang/Math;->abs(I)I

    #@42
    move-result v6

    #@43
    int-to-float v6, v6

    #@44
    int-to-float v7, p3

    #@45
    div-float v4, v6, v7

    #@47
    .line 641
    .local v4, "range":F
    add-float v6, v4, v9

    #@49
    const/high16 v7, 0x43800000    # 256.0f

    #@4b
    mul-float/2addr v6, v7

    #@4c
    float-to-int v2, v6

    #@4d
    .restart local v2    # "duration":I
    goto :goto_1
.end method

.method private computeSettleDuration(Landroid/view/View;IIII)I
    .locals 14
    .param p1, "child"    # Landroid/view/View;
    .param p2, "dx"    # I
    .param p3, "dy"    # I
    .param p4, "xvel"    # I
    .param p5, "yvel"    # I

    #@0
    .prologue
    .line 604
    iget v12, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@2
    float-to-int v12, v12

    #@3
    iget v13, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@5
    float-to-int v13, v13

    #@6
    move/from16 v0, p4

    #@8
    invoke-direct {p0, v0, v12, v13}, Landroid/support/v4/widget/ViewDragHelper;->clampMag(III)I

    #@b
    move-result p4

    #@c
    .line 605
    iget v12, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@e
    float-to-int v12, v12

    #@f
    iget v13, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@11
    float-to-int v13, v13

    #@12
    move/from16 v0, p5

    #@14
    invoke-direct {p0, v0, v12, v13}, Landroid/support/v4/widget/ViewDragHelper;->clampMag(III)I

    #@17
    move-result p5

    #@18
    .line 606
    invoke-static/range {p2 .. p2}, Ljava/lang/Math;->abs(I)I

    #@1b
    move-result v2

    #@1c
    .line 607
    .local v2, "absDx":I
    invoke-static/range {p3 .. p3}, Ljava/lang/Math;->abs(I)I

    #@1f
    move-result v3

    #@20
    .line 608
    .local v3, "absDy":I
    invoke-static/range {p4 .. p4}, Ljava/lang/Math;->abs(I)I

    #@23
    move-result v4

    #@24
    .line 609
    .local v4, "absXVel":I
    invoke-static/range {p5 .. p5}, Ljava/lang/Math;->abs(I)I

    #@27
    move-result v5

    #@28
    .line 610
    .local v5, "absYVel":I
    add-int v7, v4, v5

    #@2a
    .line 611
    .local v7, "addedVel":I
    add-int v6, v2, v3

    #@2c
    .line 613
    .local v6, "addedDistance":I
    if-eqz p4, :cond_0

    #@2e
    int-to-float v12, v4

    #@2f
    int-to-float v13, v7

    #@30
    div-float v9, v12, v13

    #@32
    .line 615
    .local v9, "xweight":F
    :goto_0
    if-eqz p5, :cond_1

    #@34
    int-to-float v12, v5

    #@35
    int-to-float v13, v7

    #@36
    div-float v11, v12, v13

    #@38
    .line 618
    .local v11, "yweight":F
    :goto_1
    iget-object v12, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@3a
    invoke-virtual {v12, p1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewHorizontalDragRange(Landroid/view/View;)I

    #@3d
    move-result v12

    #@3e
    move/from16 v0, p2

    #@40
    move/from16 v1, p4

    #@42
    invoke-direct {p0, v0, v1, v12}, Landroid/support/v4/widget/ViewDragHelper;->computeAxisDuration(III)I

    #@45
    move-result v8

    #@46
    .line 619
    .local v8, "xduration":I
    iget-object v12, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@48
    invoke-virtual {v12, p1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewVerticalDragRange(Landroid/view/View;)I

    #@4b
    move-result v12

    #@4c
    move/from16 v0, p3

    #@4e
    move/from16 v1, p5

    #@50
    invoke-direct {p0, v0, v1, v12}, Landroid/support/v4/widget/ViewDragHelper;->computeAxisDuration(III)I

    #@53
    move-result v10

    #@54
    .line 621
    .local v10, "yduration":I
    int-to-float v12, v8

    #@55
    mul-float/2addr v12, v9

    #@56
    int-to-float v13, v10

    #@57
    mul-float/2addr v13, v11

    #@58
    add-float/2addr v12, v13

    #@59
    float-to-int v12, v12

    #@5a
    return v12

    #@5b
    .line 613
    .end local v8    # "xduration":I
    .end local v9    # "xweight":F
    .end local v10    # "yduration":I
    .end local v11    # "yweight":F
    :cond_0
    int-to-float v12, v2

    #@5c
    int-to-float v13, v6

    #@5d
    div-float v9, v12, v13

    #@5f
    goto :goto_0

    #@60
    .line 615
    .restart local v9    # "xweight":F
    :cond_1
    int-to-float v12, v3

    #@61
    int-to-float v13, v6

    #@62
    div-float v11, v12, v13

    #@64
    goto :goto_1
.end method

.method public static create(Landroid/view/ViewGroup;FLandroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;
    .locals 3
    .param p0, "forParent"    # Landroid/view/ViewGroup;
    .param p1, "sensitivity"    # F
    .param p2, "cb"    # Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@0
    .prologue
    .line 360
    invoke-static {p0, p2}, Landroid/support/v4/widget/ViewDragHelper;->create(Landroid/view/ViewGroup;Landroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;

    #@3
    move-result-object v0

    #@4
    .line 361
    .local v0, "helper":Landroid/support/v4/widget/ViewDragHelper;
    iget v1, v0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@6
    int-to-float v1, v1

    #@7
    const/high16 v2, 0x3f800000    # 1.0f

    #@9
    div-float/2addr v2, p1

    #@a
    mul-float/2addr v1, v2

    #@b
    float-to-int v1, v1

    #@c
    iput v1, v0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@e
    .line 362
    return-object v0
.end method

.method public static create(Landroid/view/ViewGroup;Landroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;
    .locals 2
    .param p0, "forParent"    # Landroid/view/ViewGroup;
    .param p1, "cb"    # Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@0
    .prologue
    .line 347
    new-instance v0, Landroid/support/v4/widget/ViewDragHelper;

    #@2
    invoke-virtual {p0}, Landroid/view/ViewGroup;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p0, p1}, Landroid/support/v4/widget/ViewDragHelper;-><init>(Landroid/content/Context;Landroid/view/ViewGroup;Landroid/support/v4/widget/ViewDragHelper$Callback;)V

    #@9
    return-object v0
.end method

.method private dispatchViewReleased(FF)V
    .locals 4
    .param p1, "xvel"    # F
    .param p2, "yvel"    # F

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 766
    iput-boolean v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mReleaseInProgress:Z

    #@4
    .line 767
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@6
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@8
    invoke-virtual {v0, v1, p1, p2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewReleased(Landroid/view/View;FF)V

    #@b
    .line 768
    iput-boolean v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mReleaseInProgress:Z

    #@d
    .line 770
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@f
    if-ne v0, v3, :cond_0

    #@11
    .line 772
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@14
    .line 774
    :cond_0
    return-void
.end method

.method private distanceInfluenceForSnapDuration(F)F
    .locals 4
    .param p1, "f"    # F

    #@0
    .prologue
    .line 681
    const/high16 v0, 0x3f000000    # 0.5f

    #@2
    sub-float/2addr p1, v0

    #@3
    .line 682
    float-to-double v0, p1

    #@4
    const-wide v2, 0x3fde28c7460698c7L    # 0.4712389167638204

    #@9
    mul-double/2addr v0, v2

    #@a
    double-to-float p1, v0

    #@b
    .line 683
    float-to-double v0, p1

    #@c
    invoke-static {v0, v1}, Ljava/lang/Math;->sin(D)D

    #@f
    move-result-wide v0

    #@10
    double-to-float v0, v0

    #@11
    return v0
.end method

.method private dragTo(IIII)V
    .locals 8
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "dx"    # I
    .param p4, "dy"    # I

    #@0
    .prologue
    .line 1396
    move v2, p1

    #@1
    .line 1397
    .local v2, "clampedX":I
    move v3, p2

    #@2
    .line 1398
    .local v3, "clampedY":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@4
    invoke-virtual {v0}, Landroid/view/View;->getLeft()I

    #@7
    move-result v6

    #@8
    .line 1399
    .local v6, "oldLeft":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@a
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@d
    move-result v7

    #@e
    .line 1400
    .local v7, "oldTop":I
    if-eqz p3, :cond_0

    #@10
    .line 1401
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@12
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@14
    invoke-virtual {v0, v1, p1, p3}, Landroid/support/v4/widget/ViewDragHelper$Callback;->clampViewPositionHorizontal(Landroid/view/View;II)I

    #@17
    move-result v2

    #@18
    .line 1402
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@1a
    sub-int v1, v2, v6

    #@1c
    invoke-virtual {v0, v1}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@1f
    .line 1404
    :cond_0
    if-eqz p4, :cond_1

    #@21
    .line 1405
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@23
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@25
    invoke-virtual {v0, v1, p2, p4}, Landroid/support/v4/widget/ViewDragHelper$Callback;->clampViewPositionVertical(Landroid/view/View;II)I

    #@28
    move-result v3

    #@29
    .line 1406
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@2b
    sub-int v1, v3, v7

    #@2d
    invoke-virtual {v0, v1}, Landroid/view/View;->offsetTopAndBottom(I)V

    #@30
    .line 1409
    :cond_1
    if-nez p3, :cond_2

    #@32
    if-eqz p4, :cond_3

    #@34
    .line 1410
    :cond_2
    sub-int v4, v2, v6

    #@36
    .line 1411
    .local v4, "clampedDx":I
    sub-int v5, v3, v7

    #@38
    .line 1412
    .local v5, "clampedDy":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@3a
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@3c
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewPositionChanged(Landroid/view/View;IIII)V

    #@3f
    .line 1415
    .end local v4    # "clampedDx":I
    .end local v5    # "clampedDy":I
    :cond_3
    return-void
.end method

.method private ensureMotionHistorySizeForId(I)V
    .locals 10
    .param p1, "pointerId"    # I

    #@0
    .prologue
    const/4 v9, 0x0

    #@1
    .line 805
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@3
    if-eqz v7, :cond_0

    #@5
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@7
    array-length v7, v7

    #@8
    if-gt v7, p1, :cond_2

    #@a
    .line 806
    :cond_0
    add-int/lit8 v7, p1, 0x1

    #@c
    new-array v3, v7, [F

    #@e
    .line 807
    .local v3, "imx":[F
    add-int/lit8 v7, p1, 0x1

    #@10
    new-array v4, v7, [F

    #@12
    .line 808
    .local v4, "imy":[F
    add-int/lit8 v7, p1, 0x1

    #@14
    new-array v5, v7, [F

    #@16
    .line 809
    .local v5, "lmx":[F
    add-int/lit8 v7, p1, 0x1

    #@18
    new-array v6, v7, [F

    #@1a
    .line 810
    .local v6, "lmy":[F
    add-int/lit8 v7, p1, 0x1

    #@1c
    new-array v2, v7, [I

    #@1e
    .line 811
    .local v2, "iit":[I
    add-int/lit8 v7, p1, 0x1

    #@20
    new-array v0, v7, [I

    #@22
    .line 812
    .local v0, "edip":[I
    add-int/lit8 v7, p1, 0x1

    #@24
    new-array v1, v7, [I

    #@26
    .line 814
    .local v1, "edl":[I
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@28
    if-eqz v7, :cond_1

    #@2a
    .line 815
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@2c
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@2e
    array-length v8, v8

    #@2f
    invoke-static {v7, v9, v3, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@32
    .line 816
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@34
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@36
    array-length v8, v8

    #@37
    invoke-static {v7, v9, v4, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@3a
    .line 817
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@3c
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@3e
    array-length v8, v8

    #@3f
    invoke-static {v7, v9, v5, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@42
    .line 818
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@44
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@46
    array-length v8, v8

    #@47
    invoke-static {v7, v9, v6, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@4a
    .line 819
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@4c
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@4e
    array-length v8, v8

    #@4f
    invoke-static {v7, v9, v2, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@52
    .line 820
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@54
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@56
    array-length v8, v8

    #@57
    invoke-static {v7, v9, v0, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@5a
    .line 821
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@5c
    iget-object v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@5e
    array-length v8, v8

    #@5f
    invoke-static {v7, v9, v1, v9, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@62
    .line 824
    :cond_1
    iput-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@64
    .line 825
    iput-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@66
    .line 826
    iput-object v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@68
    .line 827
    iput-object v6, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@6a
    .line 828
    iput-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@6c
    .line 829
    iput-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@6e
    .line 830
    iput-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsLocked:[I

    #@70
    .line 832
    .end local v0    # "edip":[I
    .end local v1    # "edl":[I
    .end local v2    # "iit":[I
    .end local v3    # "imx":[F
    .end local v4    # "imy":[F
    .end local v5    # "lmx":[F
    .end local v6    # "lmy":[F
    :cond_2
    return-void
.end method

.method private forceSettleCapturedViewAt(IIII)Z
    .locals 11
    .param p1, "finalLeft"    # I
    .param p2, "finalTop"    # I
    .param p3, "xvel"    # I
    .param p4, "yvel"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 584
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@3
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@6
    move-result v10

    #@7
    .line 585
    .local v10, "startLeft":I
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@9
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@c
    move-result v6

    #@d
    .line 586
    .local v6, "startTop":I
    sub-int v2, p1, v10

    #@f
    .line 587
    .local v2, "dx":I
    sub-int v3, p2, v6

    #@11
    .line 589
    .local v3, "dy":I
    if-nez v2, :cond_0

    #@13
    if-nez v3, :cond_0

    #@15
    .line 591
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@17
    invoke-virtual {v1}, Landroid/support/v4/widget/ScrollerCompat;->abortAnimation()V

    #@1a
    .line 592
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@1d
    .line 600
    :goto_0
    return v0

    #@1e
    .line 596
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@20
    move-object v0, p0

    #@21
    move v4, p3

    #@22
    move v5, p4

    #@23
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/widget/ViewDragHelper;->computeSettleDuration(Landroid/view/View;IIII)I

    #@26
    move-result v9

    #@27
    .line 597
    .local v9, "duration":I
    iget-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@29
    move v5, v10

    #@2a
    move v7, v2

    #@2b
    move v8, v3

    #@2c
    invoke-virtual/range {v4 .. v9}, Landroid/support/v4/widget/ScrollerCompat;->startScroll(IIIII)V

    #@2f
    .line 599
    const/4 v0, 0x2

    #@30
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@33
    .line 600
    const/4 v0, 0x1

    #@34
    goto :goto_0
.end method

.method private getEdgesTouched(II)I
    .locals 3
    .param p1, "x"    # I
    .param p2, "y"    # I

    #@0
    .prologue
    .line 1470
    const/4 v0, 0x0

    #@1
    .line 1472
    .local v0, "result":I
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@3
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getLeft()I

    #@6
    move-result v1

    #@7
    iget v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@9
    add-int/2addr v1, v2

    #@a
    if-ge p1, v1, :cond_0

    #@c
    or-int/lit8 v0, v0, 0x1

    #@e
    .line 1473
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@10
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getTop()I

    #@13
    move-result v1

    #@14
    iget v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@16
    add-int/2addr v1, v2

    #@17
    if-ge p2, v1, :cond_1

    #@19
    or-int/lit8 v0, v0, 0x4

    #@1b
    .line 1474
    :cond_1
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@1d
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getRight()I

    #@20
    move-result v1

    #@21
    iget v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@23
    sub-int/2addr v1, v2

    #@24
    if-le p1, v1, :cond_2

    #@26
    or-int/lit8 v0, v0, 0x2

    #@28
    .line 1475
    :cond_2
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@2a
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getBottom()I

    #@2d
    move-result v1

    #@2e
    iget v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@30
    sub-int/2addr v1, v2

    #@31
    if-le p2, v1, :cond_3

    #@33
    or-int/lit8 v0, v0, 0x8

    #@35
    .line 1477
    :cond_3
    return v0
.end method

.method private releaseViewForPointerUp()V
    .locals 5

    #@0
    .prologue
    .line 1385
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@2
    const/16 v3, 0x3e8

    #@4
    iget v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@6
    invoke-virtual {v2, v3, v4}, Landroid/view/VelocityTracker;->computeCurrentVelocity(IF)V

    #@9
    .line 1386
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@b
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@d
    invoke-static {v2, v3}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@10
    move-result v2

    #@11
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@13
    iget v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@15
    invoke-direct {p0, v2, v3, v4}, Landroid/support/v4/widget/ViewDragHelper;->clampMag(FFF)F

    #@18
    move-result v0

    #@19
    .line 1389
    .local v0, "xvel":F
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1b
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@1d
    invoke-static {v2, v3}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@20
    move-result v2

    #@21
    iget v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@23
    iget v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mMaxVelocity:F

    #@25
    invoke-direct {p0, v2, v3, v4}, Landroid/support/v4/widget/ViewDragHelper;->clampMag(FFF)F

    #@28
    move-result v1

    #@29
    .line 1392
    .local v1, "yvel":F
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/ViewDragHelper;->dispatchViewReleased(FF)V

    #@2c
    .line 1393
    return-void
.end method

.method private reportNewEdgeDrags(FFI)V
    .locals 3
    .param p1, "dx"    # F
    .param p2, "dy"    # F
    .param p3, "pointerId"    # I

    #@0
    .prologue
    .line 1227
    const/4 v0, 0x0

    #@1
    .line 1228
    .local v0, "dragsStarted":I
    const/4 v1, 0x1

    #@2
    invoke-direct {p0, p1, p2, p3, v1}, Landroid/support/v4/widget/ViewDragHelper;->checkNewEdgeDrag(FFII)Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 1229
    or-int/lit8 v0, v0, 0x1

    #@a
    .line 1231
    :cond_0
    const/4 v1, 0x4

    #@b
    invoke-direct {p0, p2, p1, p3, v1}, Landroid/support/v4/widget/ViewDragHelper;->checkNewEdgeDrag(FFII)Z

    #@e
    move-result v1

    #@f
    if-eqz v1, :cond_1

    #@11
    .line 1232
    or-int/lit8 v0, v0, 0x4

    #@13
    .line 1234
    :cond_1
    const/4 v1, 0x2

    #@14
    invoke-direct {p0, p1, p2, p3, v1}, Landroid/support/v4/widget/ViewDragHelper;->checkNewEdgeDrag(FFII)Z

    #@17
    move-result v1

    #@18
    if-eqz v1, :cond_2

    #@1a
    .line 1235
    or-int/lit8 v0, v0, 0x2

    #@1c
    .line 1237
    :cond_2
    const/16 v1, 0x8

    #@1e
    invoke-direct {p0, p2, p1, p3, v1}, Landroid/support/v4/widget/ViewDragHelper;->checkNewEdgeDrag(FFII)Z

    #@21
    move-result v1

    #@22
    if-eqz v1, :cond_3

    #@24
    .line 1238
    or-int/lit8 v0, v0, 0x8

    #@26
    .line 1241
    :cond_3
    if-eqz v0, :cond_4

    #@28
    .line 1242
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeDragsInProgress:[I

    #@2a
    aget v2, v1, p3

    #@2c
    or-int/2addr v2, v0

    #@2d
    aput v2, v1, p3

    #@2f
    .line 1243
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@31
    invoke-virtual {v1, v0, p3}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeDragStarted(II)V

    #@34
    .line 1245
    :cond_4
    return-void
.end method

.method private saveInitialMotion(FFI)V
    .locals 3
    .param p1, "x"    # F
    .param p2, "y"    # F
    .param p3, "pointerId"    # I

    #@0
    .prologue
    .line 835
    invoke-direct {p0, p3}, Landroid/support/v4/widget/ViewDragHelper;->ensureMotionHistorySizeForId(I)V

    #@3
    .line 836
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@5
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@7
    aput p1, v1, p3

    #@9
    aput p1, v0, p3

    #@b
    .line 837
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@d
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@f
    aput p2, v1, p3

    #@11
    aput p2, v0, p3

    #@13
    .line 838
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@15
    float-to-int v1, p1

    #@16
    float-to-int v2, p2

    #@17
    invoke-direct {p0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->getEdgesTouched(II)I

    #@1a
    move-result v1

    #@1b
    aput v1, v0, p3

    #@1d
    .line 839
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@1f
    const/4 v1, 0x1

    #@20
    shl-int/2addr v1, p3

    #@21
    or-int/2addr v0, v1

    #@22
    iput v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@24
    .line 840
    return-void
.end method

.method private saveLastMotion(Landroid/view/MotionEvent;)V
    .locals 6
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 843
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getPointerCount(Landroid/view/MotionEvent;)I

    #@3
    move-result v1

    #@4
    .line 844
    .local v1, "pointerCount":I
    const/4 v0, 0x0

    #@5
    .local v0, "i":I
    :goto_0
    if-ge v0, v1, :cond_0

    #@7
    .line 845
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@a
    move-result v2

    #@b
    .line 846
    .local v2, "pointerId":I
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@e
    move-result v3

    #@f
    .line 847
    .local v3, "x":F
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@12
    move-result v4

    #@13
    .line 848
    .local v4, "y":F
    iget-object v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@15
    aput v3, v5, v2

    #@17
    .line 849
    iget-object v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@19
    aput v4, v5, v2

    #@1b
    .line 844
    add-int/lit8 v0, v0, 0x1

    #@1d
    goto :goto_0

    #@1e
    .line 851
    .end local v2    # "pointerId":I
    .end local v3    # "x":F
    .end local v4    # "y":F
    :cond_0
    return-void
.end method


# virtual methods
.method public abort()V
    .locals 8

    #@0
    .prologue
    .line 512
    invoke-virtual {p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@3
    .line 513
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@5
    const/4 v1, 0x2

    #@6
    if-ne v0, v1, :cond_0

    #@8
    .line 514
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@a
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrX()I

    #@d
    move-result v6

    #@e
    .line 515
    .local v6, "oldX":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@10
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrY()I

    #@13
    move-result v7

    #@14
    .line 516
    .local v7, "oldY":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@16
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->abortAnimation()V

    #@19
    .line 517
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@1b
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrX()I

    #@1e
    move-result v2

    #@1f
    .line 518
    .local v2, "newX":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@21
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrY()I

    #@24
    move-result v3

    #@25
    .line 519
    .local v3, "newY":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@27
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@29
    sub-int v4, v2, v6

    #@2b
    sub-int v5, v3, v7

    #@2d
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewPositionChanged(Landroid/view/View;IIII)V

    #@30
    .line 521
    .end local v2    # "newX":I
    .end local v3    # "newY":I
    .end local v6    # "oldX":I
    .end local v7    # "oldY":I
    :cond_0
    const/4 v0, 0x0

    #@31
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@34
    .line 522
    return-void
.end method

.method protected canScroll(Landroid/view/View;ZIIII)Z
    .locals 13
    .param p1, "v"    # Landroid/view/View;
    .param p2, "checkV"    # Z
    .param p3, "dx"    # I
    .param p4, "dy"    # I
    .param p5, "x"    # I
    .param p6, "y"    # I

    #@0
    .prologue
    .line 915
    instance-of v1, p1, Landroid/view/ViewGroup;

    #@2
    if-eqz v1, :cond_1

    #@4
    move-object v9, p1

    #@5
    .line 916
    check-cast v9, Landroid/view/ViewGroup;

    #@7
    .line 917
    .local v9, "group":Landroid/view/ViewGroup;
    invoke-virtual {p1}, Landroid/view/View;->getScrollX()I

    #@a
    move-result v11

    #@b
    .line 918
    .local v11, "scrollX":I
    invoke-virtual {p1}, Landroid/view/View;->getScrollY()I

    #@e
    move-result v12

    #@f
    .line 919
    .local v12, "scrollY":I
    invoke-virtual {v9}, Landroid/view/ViewGroup;->getChildCount()I

    #@12
    move-result v8

    #@13
    .line 921
    .local v8, "count":I
    add-int/lit8 v10, v8, -0x1

    #@15
    .local v10, "i":I
    :goto_0
    if-ltz v10, :cond_1

    #@17
    .line 924
    invoke-virtual {v9, v10}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@1a
    move-result-object v2

    #@1b
    .line 925
    .local v2, "child":Landroid/view/View;
    add-int v1, p5, v11

    #@1d
    invoke-virtual {v2}, Landroid/view/View;->getLeft()I

    #@20
    move-result v3

    #@21
    if-lt v1, v3, :cond_0

    #@23
    add-int v1, p5, v11

    #@25
    invoke-virtual {v2}, Landroid/view/View;->getRight()I

    #@28
    move-result v3

    #@29
    if-ge v1, v3, :cond_0

    #@2b
    add-int v1, p6, v12

    #@2d
    invoke-virtual {v2}, Landroid/view/View;->getTop()I

    #@30
    move-result v3

    #@31
    if-lt v1, v3, :cond_0

    #@33
    add-int v1, p6, v12

    #@35
    invoke-virtual {v2}, Landroid/view/View;->getBottom()I

    #@38
    move-result v3

    #@39
    if-ge v1, v3, :cond_0

    #@3b
    const/4 v3, 0x1

    #@3c
    add-int v1, p5, v11

    #@3e
    invoke-virtual {v2}, Landroid/view/View;->getLeft()I

    #@41
    move-result v4

    #@42
    sub-int v6, v1, v4

    #@44
    add-int v1, p6, v12

    #@46
    invoke-virtual {v2}, Landroid/view/View;->getTop()I

    #@49
    move-result v4

    #@4a
    sub-int v7, v1, v4

    #@4c
    move-object v1, p0

    #@4d
    move/from16 v4, p3

    #@4f
    move/from16 v5, p4

    #@51
    invoke-virtual/range {v1 .. v7}, Landroid/support/v4/widget/ViewDragHelper;->canScroll(Landroid/view/View;ZIIII)Z

    #@54
    move-result v1

    #@55
    if-eqz v1, :cond_0

    #@57
    .line 929
    const/4 v1, 0x1

    #@58
    .line 934
    .end local v2    # "child":Landroid/view/View;
    .end local v8    # "count":I
    .end local v9    # "group":Landroid/view/ViewGroup;
    .end local v10    # "i":I
    .end local v11    # "scrollX":I
    .end local v12    # "scrollY":I
    :goto_1
    return v1

    #@59
    .line 921
    .restart local v2    # "child":Landroid/view/View;
    .restart local v8    # "count":I
    .restart local v9    # "group":Landroid/view/ViewGroup;
    .restart local v10    # "i":I
    .restart local v11    # "scrollX":I
    .restart local v12    # "scrollY":I
    :cond_0
    add-int/lit8 v10, v10, -0x1

    #@5b
    goto :goto_0

    #@5c
    .line 934
    .end local v2    # "child":Landroid/view/View;
    .end local v8    # "count":I
    .end local v9    # "group":Landroid/view/ViewGroup;
    .end local v10    # "i":I
    .end local v11    # "scrollX":I
    .end local v12    # "scrollY":I
    :cond_1
    if-eqz p2, :cond_3

    #@5e
    move/from16 v0, p3

    #@60
    neg-int v1, v0

    #@61
    invoke-static {p1, v1}, Landroid/support/v4/view/ViewCompat;->canScrollHorizontally(Landroid/view/View;I)Z

    #@64
    move-result v1

    #@65
    if-nez v1, :cond_2

    #@67
    move/from16 v0, p4

    #@69
    neg-int v1, v0

    #@6a
    invoke-static {p1, v1}, Landroid/support/v4/view/ViewCompat;->canScrollVertically(Landroid/view/View;I)Z

    #@6d
    move-result v1

    #@6e
    if-eqz v1, :cond_3

    #@70
    :cond_2
    const/4 v1, 0x1

    #@71
    goto :goto_1

    #@72
    :cond_3
    const/4 v1, 0x0

    #@73
    goto :goto_1
.end method

.method public cancel()V
    .locals 1

    #@0
    .prologue
    .line 498
    const/4 v0, -0x1

    #@1
    iput v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@3
    .line 499
    invoke-direct {p0}, Landroid/support/v4/widget/ViewDragHelper;->clearMotionHistory()V

    #@6
    .line 501
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 502
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@c
    invoke-virtual {v0}, Landroid/view/VelocityTracker;->recycle()V

    #@f
    .line 503
    const/4 v0, 0x0

    #@10
    iput-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@12
    .line 505
    :cond_0
    return-void
.end method

.method public captureChildView(Landroid/view/View;I)V
    .locals 3
    .param p1, "childView"    # Landroid/view/View;
    .param p2, "activePointerId"    # I

    #@0
    .prologue
    .line 460
    invoke-virtual {p1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@3
    move-result-object v0

    #@4
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@6
    if-eq v0, v1, :cond_0

    #@8
    .line 461
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@a
    new-instance v1, Ljava/lang/StringBuilder;

    #@c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@f
    const-string v2, "captureChildView: parameter must be a descendant of the ViewDragHelper\'s tracked parent view ("

    #@11
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@17
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v1

    #@1b
    const-string v2, ")"

    #@1d
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20
    move-result-object v1

    #@21
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@24
    move-result-object v1

    #@25
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@28
    throw v0

    #@29
    .line 465
    :cond_0
    iput-object p1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@2b
    .line 466
    iput p2, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@2d
    .line 467
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@2f
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewCaptured(Landroid/view/View;I)V

    #@32
    .line 468
    const/4 v0, 0x1

    #@33
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@36
    .line 469
    return-void
.end method

.method public checkTouchSlop(I)Z
    .locals 3
    .param p1, "directions"    # I

    #@0
    .prologue
    .line 1306
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@2
    array-length v0, v2

    #@3
    .line 1307
    .local v0, "count":I
    const/4 v1, 0x0

    #@4
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@6
    .line 1308
    invoke-virtual {p0, p1, v1}, Landroid/support/v4/widget/ViewDragHelper;->checkTouchSlop(II)Z

    #@9
    move-result v2

    #@a
    if-eqz v2, :cond_0

    #@c
    .line 1309
    const/4 v2, 0x1

    #@d
    .line 1312
    :goto_1
    return v2

    #@e
    .line 1307
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@10
    goto :goto_0

    #@11
    .line 1312
    :cond_1
    const/4 v2, 0x0

    #@12
    goto :goto_1
.end method

.method public checkTouchSlop(II)Z
    .locals 9
    .param p1, "directions"    # I
    .param p2, "pointerId"    # I

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v5, 0x0

    #@2
    .line 1331
    invoke-virtual {p0, p2}, Landroid/support/v4/widget/ViewDragHelper;->isPointerDown(I)Z

    #@5
    move-result v6

    #@6
    if-nez v6, :cond_1

    #@8
    move v4, v5

    #@9
    .line 1348
    :cond_0
    :goto_0
    return v4

    #@a
    .line 1335
    :cond_1
    and-int/lit8 v6, p1, 0x1

    #@c
    if-ne v6, v4, :cond_2

    #@e
    move v0, v4

    #@f
    .line 1336
    .local v0, "checkHorizontal":Z
    :goto_1
    and-int/lit8 v6, p1, 0x2

    #@11
    const/4 v7, 0x2

    #@12
    if-ne v6, v7, :cond_3

    #@14
    move v1, v4

    #@15
    .line 1338
    .local v1, "checkVertical":Z
    :goto_2
    iget-object v6, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@17
    aget v6, v6, p2

    #@19
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@1b
    aget v7, v7, p2

    #@1d
    sub-float v2, v6, v7

    #@1f
    .line 1339
    .local v2, "dx":F
    iget-object v6, p0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@21
    aget v6, v6, p2

    #@23
    iget-object v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@25
    aget v7, v7, p2

    #@27
    sub-float v3, v6, v7

    #@29
    .line 1341
    .local v3, "dy":F
    if-eqz v0, :cond_4

    #@2b
    if-eqz v1, :cond_4

    #@2d
    .line 1342
    mul-float v6, v2, v2

    #@2f
    mul-float v7, v3, v3

    #@31
    add-float/2addr v6, v7

    #@32
    iget v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@34
    iget v8, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@36
    mul-int/2addr v7, v8

    #@37
    int-to-float v7, v7

    #@38
    cmpl-float v6, v6, v7

    #@3a
    if-gtz v6, :cond_0

    #@3c
    move v4, v5

    #@3d
    goto :goto_0

    #@3e
    .end local v0    # "checkHorizontal":Z
    .end local v1    # "checkVertical":Z
    .end local v2    # "dx":F
    .end local v3    # "dy":F
    :cond_2
    move v0, v5

    #@3f
    .line 1335
    goto :goto_1

    #@40
    .restart local v0    # "checkHorizontal":Z
    :cond_3
    move v1, v5

    #@41
    .line 1336
    goto :goto_2

    #@42
    .line 1343
    .restart local v1    # "checkVertical":Z
    .restart local v2    # "dx":F
    .restart local v3    # "dy":F
    :cond_4
    if-eqz v0, :cond_5

    #@44
    .line 1344
    invoke-static {v2}, Ljava/lang/Math;->abs(F)F

    #@47
    move-result v6

    #@48
    iget v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@4a
    int-to-float v7, v7

    #@4b
    cmpl-float v6, v6, v7

    #@4d
    if-gtz v6, :cond_0

    #@4f
    move v4, v5

    #@50
    goto :goto_0

    #@51
    .line 1345
    :cond_5
    if-eqz v1, :cond_6

    #@53
    .line 1346
    invoke-static {v3}, Ljava/lang/Math;->abs(F)F

    #@56
    move-result v6

    #@57
    iget v7, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@59
    int-to-float v7, v7

    #@5a
    cmpl-float v6, v6, v7

    #@5c
    if-gtz v6, :cond_0

    #@5e
    move v4, v5

    #@5f
    goto :goto_0

    #@60
    :cond_6
    move v4, v5

    #@61
    .line 1348
    goto :goto_0
.end method

.method public continueSettling(Z)Z
    .locals 9
    .param p1, "deferCallbacks"    # Z

    #@0
    .prologue
    const/4 v8, 0x2

    #@1
    const/4 v7, 0x0

    #@2
    .line 722
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@4
    if-ne v0, v8, :cond_5

    #@6
    .line 723
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@8
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->computeScrollOffset()Z

    #@b
    move-result v6

    #@c
    .line 724
    .local v6, "keepGoing":Z
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@e
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrX()I

    #@11
    move-result v2

    #@12
    .line 725
    .local v2, "x":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@14
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getCurrY()I

    #@17
    move-result v3

    #@18
    .line 726
    .local v3, "y":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@1a
    invoke-virtual {v0}, Landroid/view/View;->getLeft()I

    #@1d
    move-result v0

    #@1e
    sub-int v4, v2, v0

    #@20
    .line 727
    .local v4, "dx":I
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@22
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@25
    move-result v0

    #@26
    sub-int v5, v3, v0

    #@28
    .line 729
    .local v5, "dy":I
    if-eqz v4, :cond_0

    #@2a
    .line 730
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@2c
    invoke-virtual {v0, v4}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@2f
    .line 732
    :cond_0
    if-eqz v5, :cond_1

    #@31
    .line 733
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@33
    invoke-virtual {v0, v5}, Landroid/view/View;->offsetTopAndBottom(I)V

    #@36
    .line 736
    :cond_1
    if-nez v4, :cond_2

    #@38
    if-eqz v5, :cond_3

    #@3a
    .line 737
    :cond_2
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@3c
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@3e
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewPositionChanged(Landroid/view/View;IIII)V

    #@41
    .line 740
    :cond_3
    if-eqz v6, :cond_4

    #@43
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@45
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getFinalX()I

    #@48
    move-result v0

    #@49
    if-ne v2, v0, :cond_4

    #@4b
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@4d
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getFinalY()I

    #@50
    move-result v0

    #@51
    if-ne v3, v0, :cond_4

    #@53
    .line 743
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@55
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->abortAnimation()V

    #@58
    .line 744
    const/4 v6, 0x0

    #@59
    .line 747
    :cond_4
    if-nez v6, :cond_5

    #@5b
    .line 748
    if-eqz p1, :cond_6

    #@5d
    .line 749
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@5f
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mSetIdleRunnable:Ljava/lang/Runnable;

    #@61
    invoke-virtual {v0, v1}, Landroid/view/ViewGroup;->post(Ljava/lang/Runnable;)Z

    #@64
    .line 756
    .end local v2    # "x":I
    .end local v3    # "y":I
    .end local v4    # "dx":I
    .end local v5    # "dy":I
    .end local v6    # "keepGoing":Z
    :cond_5
    :goto_0
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@66
    if-ne v0, v8, :cond_7

    #@68
    const/4 v0, 0x1

    #@69
    :goto_1
    return v0

    #@6a
    .line 751
    .restart local v2    # "x":I
    .restart local v3    # "y":I
    .restart local v4    # "dx":I
    .restart local v5    # "dy":I
    .restart local v6    # "keepGoing":Z
    :cond_6
    invoke-virtual {p0, v7}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@6d
    goto :goto_0

    #@6e
    .end local v2    # "x":I
    .end local v3    # "y":I
    .end local v4    # "dx":I
    .end local v5    # "dy":I
    .end local v6    # "keepGoing":Z
    :cond_7
    move v0, v7

    #@6f
    .line 756
    goto :goto_1
.end method

.method public findTopChildUnder(II)Landroid/view/View;
    .locals 5
    .param p1, "x"    # I
    .param p2, "y"    # I

    #@0
    .prologue
    .line 1458
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@2
    invoke-virtual {v3}, Landroid/view/ViewGroup;->getChildCount()I

    #@5
    move-result v1

    #@6
    .line 1459
    .local v1, "childCount":I
    add-int/lit8 v2, v1, -0x1

    #@8
    .local v2, "i":I
    :goto_0
    if-ltz v2, :cond_1

    #@a
    .line 1460
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mParentView:Landroid/view/ViewGroup;

    #@c
    iget-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@e
    invoke-virtual {v4, v2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getOrderedChildIndex(I)I

    #@11
    move-result v4

    #@12
    invoke-virtual {v3, v4}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@15
    move-result-object v0

    #@16
    .line 1461
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLeft()I

    #@19
    move-result v3

    #@1a
    if-lt p1, v3, :cond_0

    #@1c
    invoke-virtual {v0}, Landroid/view/View;->getRight()I

    #@1f
    move-result v3

    #@20
    if-ge p1, v3, :cond_0

    #@22
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@25
    move-result v3

    #@26
    if-lt p2, v3, :cond_0

    #@28
    invoke-virtual {v0}, Landroid/view/View;->getBottom()I

    #@2b
    move-result v3

    #@2c
    if-ge p2, v3, :cond_0

    #@2e
    .line 1466
    .end local v0    # "child":Landroid/view/View;
    :goto_1
    return-object v0

    #@2f
    .line 1459
    .restart local v0    # "child":Landroid/view/View;
    :cond_0
    add-int/lit8 v2, v2, -0x1

    #@31
    goto :goto_0

    #@32
    .line 1466
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    const/4 v0, 0x0

    #@33
    goto :goto_1
.end method

.method public flingCapturedView(IIII)V
    .locals 9
    .param p1, "minLeft"    # I
    .param p2, "minTop"    # I
    .param p3, "maxLeft"    # I
    .param p4, "maxTop"    # I

    #@0
    .prologue
    .line 697
    iget-boolean v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mReleaseInProgress:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 698
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Cannot flingCapturedView outside of a call to Callback#onViewReleased"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 702
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mScroller:Landroid/support/v4/widget/ScrollerCompat;

    #@e
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@10
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@13
    move-result v1

    #@14
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@16
    invoke-virtual {v2}, Landroid/view/View;->getTop()I

    #@19
    move-result v2

    #@1a
    iget-object v3, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1c
    iget v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@1e
    invoke-static {v3, v4}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@21
    move-result v3

    #@22
    float-to-int v3, v3

    #@23
    iget-object v4, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@25
    iget v5, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@27
    invoke-static {v4, v5}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@2a
    move-result v4

    #@2b
    float-to-int v4, v4

    #@2c
    move v5, p1

    #@2d
    move v6, p3

    #@2e
    move v7, p2

    #@2f
    move v8, p4

    #@30
    invoke-virtual/range {v0 .. v8}, Landroid/support/v4/widget/ScrollerCompat;->fling(IIIIIIII)V

    #@33
    .line 707
    const/4 v0, 0x2

    #@34
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/ViewDragHelper;->setDragState(I)V

    #@37
    .line 708
    return-void
.end method

.method public getActivePointerId()I
    .locals 1

    #@0
    .prologue
    .line 483
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@2
    return v0
.end method

.method public getCapturedView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 475
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getEdgeSize()I
    .locals 1

    #@0
    .prologue
    .line 448
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mEdgeSize:I

    #@2
    return v0
.end method

.method public getMinVelocity()F
    .locals 1

    #@0
    .prologue
    .line 412
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@2
    return v0
.end method

.method public getTouchSlop()I
    .locals 1

    #@0
    .prologue
    .line 490
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mTouchSlop:I

    #@2
    return v0
.end method

.method public getViewDragState()I
    .locals 1

    #@0
    .prologue
    .line 421
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@2
    return v0
.end method

.method public isCapturedViewUnder(II)Z
    .locals 1
    .param p1, "x"    # I
    .param p2, "y"    # I

    #@0
    .prologue
    .line 1427
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@2
    invoke-virtual {p0, v0, p1, p2}, Landroid/support/v4/widget/ViewDragHelper;->isViewUnder(Landroid/view/View;II)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isEdgeTouched(I)Z
    .locals 3
    .param p1, "edges"    # I

    #@0
    .prologue
    .line 1361
    iget-object v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@2
    array-length v0, v2

    #@3
    .line 1362
    .local v0, "count":I
    const/4 v1, 0x0

    #@4
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@6
    .line 1363
    invoke-virtual {p0, p1, v1}, Landroid/support/v4/widget/ViewDragHelper;->isEdgeTouched(II)Z

    #@9
    move-result v2

    #@a
    if-eqz v2, :cond_0

    #@c
    .line 1364
    const/4 v2, 0x1

    #@d
    .line 1367
    :goto_1
    return v2

    #@e
    .line 1362
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@10
    goto :goto_0

    #@11
    .line 1367
    :cond_1
    const/4 v2, 0x0

    #@12
    goto :goto_1
.end method

.method public isEdgeTouched(II)Z
    .locals 1
    .param p1, "edges"    # I
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1381
    invoke-virtual {p0, p2}, Landroid/support/v4/widget/ViewDragHelper;->isPointerDown(I)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@8
    aget v0, v0, p2

    #@a
    and-int/2addr v0, p1

    #@b
    if-eqz v0, :cond_0

    #@d
    const/4 v0, 0x1

    #@e
    :goto_0
    return v0

    #@f
    :cond_0
    const/4 v0, 0x0

    #@10
    goto :goto_0
.end method

.method public isPointerDown(I)Z
    .locals 3
    .param p1, "pointerId"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 867
    iget v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mPointersDown:I

    #@3
    shl-int v2, v0, p1

    #@5
    and-int/2addr v1, v2

    #@6
    if-eqz v1, :cond_0

    #@8
    :goto_0
    return v0

    #@9
    :cond_0
    const/4 v0, 0x0

    #@a
    goto :goto_0
.end method

.method public isViewUnder(Landroid/view/View;II)Z
    .locals 2
    .param p1, "view"    # Landroid/view/View;
    .param p2, "x"    # I
    .param p3, "y"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 1440
    if-nez p1, :cond_1

    #@3
    .line 1443
    :cond_0
    :goto_0
    return v0

    #@4
    :cond_1
    invoke-virtual {p1}, Landroid/view/View;->getLeft()I

    #@7
    move-result v1

    #@8
    if-lt p2, v1, :cond_0

    #@a
    invoke-virtual {p1}, Landroid/view/View;->getRight()I

    #@d
    move-result v1

    #@e
    if-ge p2, v1, :cond_0

    #@10
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@13
    move-result v1

    #@14
    if-lt p3, v1, :cond_0

    #@16
    invoke-virtual {p1}, Landroid/view/View;->getBottom()I

    #@19
    move-result v1

    #@1a
    if-ge p3, v1, :cond_0

    #@1c
    const/4 v0, 0x1

    #@1d
    goto :goto_0
.end method

.method public processTouchEvent(Landroid/view/MotionEvent;)V
    .locals 21
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 1076
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@3
    move-result v3

    #@4
    .line 1077
    .local v3, "action":I
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@7
    move-result v4

    #@8
    .line 1079
    .local v4, "actionIndex":I
    if-nez v3, :cond_0

    #@a
    .line 1082
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@d
    .line 1085
    :cond_0
    move-object/from16 v0, p0

    #@f
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@11
    move-object/from16 v19, v0

    #@13
    if-nez v19, :cond_1

    #@15
    .line 1086
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@18
    move-result-object v19

    #@19
    move-object/from16 v0, v19

    #@1b
    move-object/from16 v1, p0

    #@1d
    iput-object v0, v1, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1f
    .line 1088
    :cond_1
    move-object/from16 v0, p0

    #@21
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@23
    move-object/from16 v19, v0

    #@25
    move-object/from16 v0, v19

    #@27
    move-object/from16 v1, p1

    #@29
    invoke-virtual {v0, v1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@2c
    .line 1090
    packed-switch v3, :pswitch_data_0

    #@2f
    .line 1224
    :cond_2
    :goto_0
    :pswitch_0
    return-void

    #@30
    .line 1092
    :pswitch_1
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getX()F

    #@33
    move-result v17

    #@34
    .line 1093
    .local v17, "x":F
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getY()F

    #@37
    move-result v18

    #@38
    .line 1094
    .local v18, "y":F
    const/16 v19, 0x0

    #@3a
    move-object/from16 v0, p1

    #@3c
    move/from16 v1, v19

    #@3e
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@41
    move-result v15

    #@42
    .line 1095
    .local v15, "pointerId":I
    move/from16 v0, v17

    #@44
    float-to-int v0, v0

    #@45
    move/from16 v19, v0

    #@47
    move/from16 v0, v18

    #@49
    float-to-int v0, v0

    #@4a
    move/from16 v20, v0

    #@4c
    move-object/from16 v0, p0

    #@4e
    move/from16 v1, v19

    #@50
    move/from16 v2, v20

    #@52
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@55
    move-result-object v16

    #@56
    .line 1097
    .local v16, "toCapture":Landroid/view/View;
    move-object/from16 v0, p0

    #@58
    move/from16 v1, v17

    #@5a
    move/from16 v2, v18

    #@5c
    invoke-direct {v0, v1, v2, v15}, Landroid/support/v4/widget/ViewDragHelper;->saveInitialMotion(FFI)V

    #@5f
    .line 1102
    move-object/from16 v0, p0

    #@61
    move-object/from16 v1, v16

    #@63
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@66
    .line 1104
    move-object/from16 v0, p0

    #@68
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@6a
    move-object/from16 v19, v0

    #@6c
    aget v7, v19, v15

    #@6e
    .line 1105
    .local v7, "edgesTouched":I
    move-object/from16 v0, p0

    #@70
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@72
    move/from16 v19, v0

    #@74
    and-int v19, v19, v7

    #@76
    if-eqz v19, :cond_2

    #@78
    .line 1106
    move-object/from16 v0, p0

    #@7a
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@7c
    move-object/from16 v19, v0

    #@7e
    move-object/from16 v0, p0

    #@80
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@82
    move/from16 v20, v0

    #@84
    and-int v20, v20, v7

    #@86
    move-object/from16 v0, v19

    #@88
    move/from16 v1, v20

    #@8a
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeTouched(II)V

    #@8d
    goto :goto_0

    #@8e
    .line 1112
    .end local v7    # "edgesTouched":I
    .end local v15    # "pointerId":I
    .end local v16    # "toCapture":Landroid/view/View;
    .end local v17    # "x":F
    .end local v18    # "y":F
    :pswitch_2
    move-object/from16 v0, p1

    #@90
    invoke-static {v0, v4}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@93
    move-result v15

    #@94
    .line 1113
    .restart local v15    # "pointerId":I
    move-object/from16 v0, p1

    #@96
    invoke-static {v0, v4}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@99
    move-result v17

    #@9a
    .line 1114
    .restart local v17    # "x":F
    move-object/from16 v0, p1

    #@9c
    invoke-static {v0, v4}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@9f
    move-result v18

    #@a0
    .line 1116
    .restart local v18    # "y":F
    move-object/from16 v0, p0

    #@a2
    move/from16 v1, v17

    #@a4
    move/from16 v2, v18

    #@a6
    invoke-direct {v0, v1, v2, v15}, Landroid/support/v4/widget/ViewDragHelper;->saveInitialMotion(FFI)V

    #@a9
    .line 1119
    move-object/from16 v0, p0

    #@ab
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@ad
    move/from16 v19, v0

    #@af
    if-nez v19, :cond_3

    #@b1
    .line 1122
    move/from16 v0, v17

    #@b3
    float-to-int v0, v0

    #@b4
    move/from16 v19, v0

    #@b6
    move/from16 v0, v18

    #@b8
    float-to-int v0, v0

    #@b9
    move/from16 v20, v0

    #@bb
    move-object/from16 v0, p0

    #@bd
    move/from16 v1, v19

    #@bf
    move/from16 v2, v20

    #@c1
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@c4
    move-result-object v16

    #@c5
    .line 1123
    .restart local v16    # "toCapture":Landroid/view/View;
    move-object/from16 v0, p0

    #@c7
    move-object/from16 v1, v16

    #@c9
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@cc
    .line 1125
    move-object/from16 v0, p0

    #@ce
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@d0
    move-object/from16 v19, v0

    #@d2
    aget v7, v19, v15

    #@d4
    .line 1126
    .restart local v7    # "edgesTouched":I
    move-object/from16 v0, p0

    #@d6
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@d8
    move/from16 v19, v0

    #@da
    and-int v19, v19, v7

    #@dc
    if-eqz v19, :cond_2

    #@de
    .line 1127
    move-object/from16 v0, p0

    #@e0
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@e2
    move-object/from16 v19, v0

    #@e4
    move-object/from16 v0, p0

    #@e6
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@e8
    move/from16 v20, v0

    #@ea
    and-int v20, v20, v7

    #@ec
    move-object/from16 v0, v19

    #@ee
    move/from16 v1, v20

    #@f0
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeTouched(II)V

    #@f3
    goto/16 :goto_0

    #@f5
    .line 1129
    .end local v7    # "edgesTouched":I
    .end local v16    # "toCapture":Landroid/view/View;
    :cond_3
    move/from16 v0, v17

    #@f7
    float-to-int v0, v0

    #@f8
    move/from16 v19, v0

    #@fa
    move/from16 v0, v18

    #@fc
    float-to-int v0, v0

    #@fd
    move/from16 v20, v0

    #@ff
    move-object/from16 v0, p0

    #@101
    move/from16 v1, v19

    #@103
    move/from16 v2, v20

    #@105
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->isCapturedViewUnder(II)Z

    #@108
    move-result v19

    #@109
    if-eqz v19, :cond_2

    #@10b
    .line 1134
    move-object/from16 v0, p0

    #@10d
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@10f
    move-object/from16 v19, v0

    #@111
    move-object/from16 v0, p0

    #@113
    move-object/from16 v1, v19

    #@115
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@118
    goto/16 :goto_0

    #@11a
    .line 1140
    .end local v15    # "pointerId":I
    .end local v17    # "x":F
    .end local v18    # "y":F
    :pswitch_3
    move-object/from16 v0, p0

    #@11c
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@11e
    move/from16 v19, v0

    #@120
    const/16 v20, 0x1

    #@122
    move/from16 v0, v19

    #@124
    move/from16 v1, v20

    #@126
    if-ne v0, v1, :cond_4

    #@128
    .line 1141
    move-object/from16 v0, p0

    #@12a
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@12c
    move/from16 v19, v0

    #@12e
    move-object/from16 v0, p1

    #@130
    move/from16 v1, v19

    #@132
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@135
    move-result v12

    #@136
    .line 1142
    .local v12, "index":I
    move-object/from16 v0, p1

    #@138
    invoke-static {v0, v12}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@13b
    move-result v17

    #@13c
    .line 1143
    .restart local v17    # "x":F
    move-object/from16 v0, p1

    #@13e
    invoke-static {v0, v12}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@141
    move-result v18

    #@142
    .line 1144
    .restart local v18    # "y":F
    move-object/from16 v0, p0

    #@144
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionX:[F

    #@146
    move-object/from16 v19, v0

    #@148
    move-object/from16 v0, p0

    #@14a
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@14c
    move/from16 v20, v0

    #@14e
    aget v19, v19, v20

    #@150
    sub-float v19, v17, v19

    #@152
    move/from16 v0, v19

    #@154
    float-to-int v10, v0

    #@155
    .line 1145
    .local v10, "idx":I
    move-object/from16 v0, p0

    #@157
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mLastMotionY:[F

    #@159
    move-object/from16 v19, v0

    #@15b
    move-object/from16 v0, p0

    #@15d
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@15f
    move/from16 v20, v0

    #@161
    aget v19, v19, v20

    #@163
    sub-float v19, v18, v19

    #@165
    move/from16 v0, v19

    #@167
    float-to-int v11, v0

    #@168
    .line 1147
    .local v11, "idy":I
    move-object/from16 v0, p0

    #@16a
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@16c
    move-object/from16 v19, v0

    #@16e
    invoke-virtual/range {v19 .. v19}, Landroid/view/View;->getLeft()I

    #@171
    move-result v19

    #@172
    add-int v19, v19, v10

    #@174
    move-object/from16 v0, p0

    #@176
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@178
    move-object/from16 v20, v0

    #@17a
    invoke-virtual/range {v20 .. v20}, Landroid/view/View;->getTop()I

    #@17d
    move-result v20

    #@17e
    add-int v20, v20, v11

    #@180
    move-object/from16 v0, p0

    #@182
    move/from16 v1, v19

    #@184
    move/from16 v2, v20

    #@186
    invoke-direct {v0, v1, v2, v10, v11}, Landroid/support/v4/widget/ViewDragHelper;->dragTo(IIII)V

    #@189
    .line 1149
    invoke-direct/range {p0 .. p1}, Landroid/support/v4/widget/ViewDragHelper;->saveLastMotion(Landroid/view/MotionEvent;)V

    #@18c
    goto/16 :goto_0

    #@18e
    .line 1152
    .end local v10    # "idx":I
    .end local v11    # "idy":I
    .end local v12    # "index":I
    .end local v17    # "x":F
    .end local v18    # "y":F
    :cond_4
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getPointerCount(Landroid/view/MotionEvent;)I

    #@191
    move-result v14

    #@192
    .line 1153
    .local v14, "pointerCount":I
    const/4 v8, 0x0

    #@193
    .local v8, "i":I
    :goto_1
    if-ge v8, v14, :cond_5

    #@195
    .line 1154
    move-object/from16 v0, p1

    #@197
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@19a
    move-result v15

    #@19b
    .line 1155
    .restart local v15    # "pointerId":I
    move-object/from16 v0, p1

    #@19d
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@1a0
    move-result v17

    #@1a1
    .line 1156
    .restart local v17    # "x":F
    move-object/from16 v0, p1

    #@1a3
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@1a6
    move-result v18

    #@1a7
    .line 1157
    .restart local v18    # "y":F
    move-object/from16 v0, p0

    #@1a9
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@1ab
    move-object/from16 v19, v0

    #@1ad
    aget v19, v19, v15

    #@1af
    sub-float v5, v17, v19

    #@1b1
    .line 1158
    .local v5, "dx":F
    move-object/from16 v0, p0

    #@1b3
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@1b5
    move-object/from16 v19, v0

    #@1b7
    aget v19, v19, v15

    #@1b9
    sub-float v6, v18, v19

    #@1bb
    .line 1160
    .local v6, "dy":F
    move-object/from16 v0, p0

    #@1bd
    invoke-direct {v0, v5, v6, v15}, Landroid/support/v4/widget/ViewDragHelper;->reportNewEdgeDrags(FFI)V

    #@1c0
    .line 1161
    move-object/from16 v0, p0

    #@1c2
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@1c4
    move/from16 v19, v0

    #@1c6
    const/16 v20, 0x1

    #@1c8
    move/from16 v0, v19

    #@1ca
    move/from16 v1, v20

    #@1cc
    if-ne v0, v1, :cond_6

    #@1ce
    .line 1172
    .end local v5    # "dx":F
    .end local v6    # "dy":F
    .end local v15    # "pointerId":I
    .end local v17    # "x":F
    .end local v18    # "y":F
    :cond_5
    invoke-direct/range {p0 .. p1}, Landroid/support/v4/widget/ViewDragHelper;->saveLastMotion(Landroid/view/MotionEvent;)V

    #@1d1
    goto/16 :goto_0

    #@1d3
    .line 1166
    .restart local v5    # "dx":F
    .restart local v6    # "dy":F
    .restart local v15    # "pointerId":I
    .restart local v17    # "x":F
    .restart local v18    # "y":F
    :cond_6
    move/from16 v0, v17

    #@1d5
    float-to-int v0, v0

    #@1d6
    move/from16 v19, v0

    #@1d8
    move/from16 v0, v18

    #@1da
    float-to-int v0, v0

    #@1db
    move/from16 v20, v0

    #@1dd
    move-object/from16 v0, p0

    #@1df
    move/from16 v1, v19

    #@1e1
    move/from16 v2, v20

    #@1e3
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@1e6
    move-result-object v16

    #@1e7
    .line 1167
    .restart local v16    # "toCapture":Landroid/view/View;
    move-object/from16 v0, p0

    #@1e9
    move-object/from16 v1, v16

    #@1eb
    invoke-direct {v0, v1, v5, v6}, Landroid/support/v4/widget/ViewDragHelper;->checkTouchSlop(Landroid/view/View;FF)Z

    #@1ee
    move-result v19

    #@1ef
    if-eqz v19, :cond_7

    #@1f1
    move-object/from16 v0, p0

    #@1f3
    move-object/from16 v1, v16

    #@1f5
    invoke-virtual {v0, v1, v15}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@1f8
    move-result v19

    #@1f9
    if-nez v19, :cond_5

    #@1fb
    .line 1153
    :cond_7
    add-int/lit8 v8, v8, 0x1

    #@1fd
    goto :goto_1

    #@1fe
    .line 1178
    .end local v5    # "dx":F
    .end local v6    # "dy":F
    .end local v8    # "i":I
    .end local v14    # "pointerCount":I
    .end local v15    # "pointerId":I
    .end local v16    # "toCapture":Landroid/view/View;
    .end local v17    # "x":F
    .end local v18    # "y":F
    :pswitch_4
    move-object/from16 v0, p1

    #@200
    invoke-static {v0, v4}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@203
    move-result v15

    #@204
    .line 1179
    .restart local v15    # "pointerId":I
    move-object/from16 v0, p0

    #@206
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@208
    move/from16 v19, v0

    #@20a
    const/16 v20, 0x1

    #@20c
    move/from16 v0, v19

    #@20e
    move/from16 v1, v20

    #@210
    if-ne v0, v1, :cond_b

    #@212
    move-object/from16 v0, p0

    #@214
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@216
    move/from16 v19, v0

    #@218
    move/from16 v0, v19

    #@21a
    if-ne v15, v0, :cond_b

    #@21c
    .line 1181
    const/4 v13, -0x1

    #@21d
    .line 1182
    .local v13, "newActivePointer":I
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getPointerCount(Landroid/view/MotionEvent;)I

    #@220
    move-result v14

    #@221
    .line 1183
    .restart local v14    # "pointerCount":I
    const/4 v8, 0x0

    #@222
    .restart local v8    # "i":I
    :goto_2
    if-ge v8, v14, :cond_a

    #@224
    .line 1184
    move-object/from16 v0, p1

    #@226
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@229
    move-result v9

    #@22a
    .line 1185
    .local v9, "id":I
    move-object/from16 v0, p0

    #@22c
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@22e
    move/from16 v19, v0

    #@230
    move/from16 v0, v19

    #@232
    if-ne v9, v0, :cond_9

    #@234
    .line 1183
    :cond_8
    add-int/lit8 v8, v8, 0x1

    #@236
    goto :goto_2

    #@237
    .line 1190
    :cond_9
    move-object/from16 v0, p1

    #@239
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@23c
    move-result v17

    #@23d
    .line 1191
    .restart local v17    # "x":F
    move-object/from16 v0, p1

    #@23f
    invoke-static {v0, v8}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@242
    move-result v18

    #@243
    .line 1192
    .restart local v18    # "y":F
    move/from16 v0, v17

    #@245
    float-to-int v0, v0

    #@246
    move/from16 v19, v0

    #@248
    move/from16 v0, v18

    #@24a
    float-to-int v0, v0

    #@24b
    move/from16 v20, v0

    #@24d
    move-object/from16 v0, p0

    #@24f
    move/from16 v1, v19

    #@251
    move/from16 v2, v20

    #@253
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@256
    move-result-object v19

    #@257
    move-object/from16 v0, p0

    #@259
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@25b
    move-object/from16 v20, v0

    #@25d
    move-object/from16 v0, v19

    #@25f
    move-object/from16 v1, v20

    #@261
    if-ne v0, v1, :cond_8

    #@263
    move-object/from16 v0, p0

    #@265
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@267
    move-object/from16 v19, v0

    #@269
    move-object/from16 v0, p0

    #@26b
    move-object/from16 v1, v19

    #@26d
    invoke-virtual {v0, v1, v9}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@270
    move-result v19

    #@271
    if-eqz v19, :cond_8

    #@273
    .line 1194
    move-object/from16 v0, p0

    #@275
    iget v13, v0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@277
    .line 1199
    .end local v9    # "id":I
    .end local v17    # "x":F
    .end local v18    # "y":F
    :cond_a
    const/16 v19, -0x1

    #@279
    move/from16 v0, v19

    #@27b
    if-ne v13, v0, :cond_b

    #@27d
    .line 1201
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->releaseViewForPointerUp()V

    #@280
    .line 1204
    .end local v8    # "i":I
    .end local v13    # "newActivePointer":I
    .end local v14    # "pointerCount":I
    :cond_b
    move-object/from16 v0, p0

    #@282
    invoke-direct {v0, v15}, Landroid/support/v4/widget/ViewDragHelper;->clearMotionHistory(I)V

    #@285
    goto/16 :goto_0

    #@287
    .line 1209
    .end local v15    # "pointerId":I
    :pswitch_5
    move-object/from16 v0, p0

    #@289
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@28b
    move/from16 v19, v0

    #@28d
    const/16 v20, 0x1

    #@28f
    move/from16 v0, v19

    #@291
    move/from16 v1, v20

    #@293
    if-ne v0, v1, :cond_c

    #@295
    .line 1210
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->releaseViewForPointerUp()V

    #@298
    .line 1212
    :cond_c
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@29b
    goto/16 :goto_0

    #@29d
    .line 1217
    :pswitch_6
    move-object/from16 v0, p0

    #@29f
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@2a1
    move/from16 v19, v0

    #@2a3
    const/16 v20, 0x1

    #@2a5
    move/from16 v0, v19

    #@2a7
    move/from16 v1, v20

    #@2a9
    if-ne v0, v1, :cond_d

    #@2ab
    .line 1218
    const/16 v19, 0x0

    #@2ad
    const/16 v20, 0x0

    #@2af
    move-object/from16 v0, p0

    #@2b1
    move/from16 v1, v19

    #@2b3
    move/from16 v2, v20

    #@2b5
    invoke-direct {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->dispatchViewReleased(FF)V

    #@2b8
    .line 1220
    :cond_d
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@2bb
    goto/16 :goto_0

    #@2bd
    .line 1090
    nop

    #@2be
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_3
        :pswitch_6
        :pswitch_0
        :pswitch_2
        :pswitch_4
    .end packed-switch
.end method

.method setDragState(I)V
    .locals 1
    .param p1, "state"    # I

    #@0
    .prologue
    .line 871
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 872
    iput p1, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@6
    .line 873
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@8
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onViewDragStateChanged(I)V

    #@b
    .line 874
    iget v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@d
    if-nez v0, :cond_0

    #@f
    .line 875
    const/4 v0, 0x0

    #@10
    iput-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@12
    .line 878
    :cond_0
    return-void
.end method

.method public setEdgeTrackingEnabled(I)V
    .locals 0
    .param p1, "edgeFlags"    # I

    #@0
    .prologue
    .line 437
    iput p1, p0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@2
    .line 438
    return-void
.end method

.method public setMinVelocity(F)V
    .locals 0
    .param p1, "minVel"    # F

    #@0
    .prologue
    .line 401
    iput p1, p0, Landroid/support/v4/widget/ViewDragHelper;->mMinVelocity:F

    #@2
    .line 402
    return-void
.end method

.method public settleCapturedViewAt(II)Z
    .locals 3
    .param p1, "finalLeft"    # I
    .param p2, "finalTop"    # I

    #@0
    .prologue
    .line 564
    iget-boolean v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mReleaseInProgress:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 565
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 569
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@e
    iget v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@10
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@13
    move-result v0

    #@14
    float-to-int v0, v0

    #@15
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@17
    iget v2, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@19
    invoke-static {v1, v2}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@1c
    move-result v1

    #@1d
    float-to-int v1, v1

    #@1e
    invoke-direct {p0, p1, p2, v0, v1}, Landroid/support/v4/widget/ViewDragHelper;->forceSettleCapturedViewAt(IIII)Z

    #@21
    move-result v0

    #@22
    return v0
.end method

.method public shouldInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 26
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 946
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@3
    move-result v4

    #@4
    .line 947
    .local v4, "action":I
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@7
    move-result v5

    #@8
    .line 949
    .local v5, "actionIndex":I
    if-nez v4, :cond_0

    #@a
    .line 952
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@d
    .line 955
    :cond_0
    move-object/from16 v0, p0

    #@f
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@11
    move-object/from16 v24, v0

    #@13
    if-nez v24, :cond_1

    #@15
    .line 956
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@18
    move-result-object v24

    #@19
    move-object/from16 v0, v24

    #@1b
    move-object/from16 v1, p0

    #@1d
    iput-object v0, v1, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1f
    .line 958
    :cond_1
    move-object/from16 v0, p0

    #@21
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@23
    move-object/from16 v24, v0

    #@25
    move-object/from16 v0, v24

    #@27
    move-object/from16 v1, p1

    #@29
    invoke-virtual {v0, v1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@2c
    .line 960
    packed-switch v4, :pswitch_data_0

    #@2f
    .line 1066
    :cond_2
    :goto_0
    :pswitch_0
    move-object/from16 v0, p0

    #@31
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@33
    move/from16 v24, v0

    #@35
    const/16 v25, 0x1

    #@37
    move/from16 v0, v24

    #@39
    move/from16 v1, v25

    #@3b
    if-ne v0, v1, :cond_a

    #@3d
    const/16 v24, 0x1

    #@3f
    :goto_1
    return v24

    #@40
    .line 962
    :pswitch_1
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getX()F

    #@43
    move-result v22

    #@44
    .line 963
    .local v22, "x":F
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getY()F

    #@47
    move-result v23

    #@48
    .line 964
    .local v23, "y":F
    const/16 v24, 0x0

    #@4a
    move-object/from16 v0, p1

    #@4c
    move/from16 v1, v24

    #@4e
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@51
    move-result v17

    #@52
    .line 965
    .local v17, "pointerId":I
    move-object/from16 v0, p0

    #@54
    move/from16 v1, v22

    #@56
    move/from16 v2, v23

    #@58
    move/from16 v3, v17

    #@5a
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->saveInitialMotion(FFI)V

    #@5d
    .line 967
    move/from16 v0, v22

    #@5f
    float-to-int v0, v0

    #@60
    move/from16 v24, v0

    #@62
    move/from16 v0, v23

    #@64
    float-to-int v0, v0

    #@65
    move/from16 v25, v0

    #@67
    move-object/from16 v0, p0

    #@69
    move/from16 v1, v24

    #@6b
    move/from16 v2, v25

    #@6d
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@70
    move-result-object v20

    #@71
    .line 970
    .local v20, "toCapture":Landroid/view/View;
    move-object/from16 v0, p0

    #@73
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@75
    move-object/from16 v24, v0

    #@77
    move-object/from16 v0, v20

    #@79
    move-object/from16 v1, v24

    #@7b
    if-ne v0, v1, :cond_3

    #@7d
    move-object/from16 v0, p0

    #@7f
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@81
    move/from16 v24, v0

    #@83
    const/16 v25, 0x2

    #@85
    move/from16 v0, v24

    #@87
    move/from16 v1, v25

    #@89
    if-ne v0, v1, :cond_3

    #@8b
    .line 971
    move-object/from16 v0, p0

    #@8d
    move-object/from16 v1, v20

    #@8f
    move/from16 v2, v17

    #@91
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@94
    .line 974
    :cond_3
    move-object/from16 v0, p0

    #@96
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@98
    move-object/from16 v24, v0

    #@9a
    aget v8, v24, v17

    #@9c
    .line 975
    .local v8, "edgesTouched":I
    move-object/from16 v0, p0

    #@9e
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@a0
    move/from16 v24, v0

    #@a2
    and-int v24, v24, v8

    #@a4
    if-eqz v24, :cond_2

    #@a6
    .line 976
    move-object/from16 v0, p0

    #@a8
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@aa
    move-object/from16 v24, v0

    #@ac
    move-object/from16 v0, p0

    #@ae
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@b0
    move/from16 v25, v0

    #@b2
    and-int v25, v25, v8

    #@b4
    move-object/from16 v0, v24

    #@b6
    move/from16 v1, v25

    #@b8
    move/from16 v2, v17

    #@ba
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeTouched(II)V

    #@bd
    goto/16 :goto_0

    #@bf
    .line 982
    .end local v8    # "edgesTouched":I
    .end local v17    # "pointerId":I
    .end local v20    # "toCapture":Landroid/view/View;
    .end local v22    # "x":F
    .end local v23    # "y":F
    :pswitch_2
    move-object/from16 v0, p1

    #@c1
    invoke-static {v0, v5}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@c4
    move-result v17

    #@c5
    .line 983
    .restart local v17    # "pointerId":I
    move-object/from16 v0, p1

    #@c7
    invoke-static {v0, v5}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@ca
    move-result v22

    #@cb
    .line 984
    .restart local v22    # "x":F
    move-object/from16 v0, p1

    #@cd
    invoke-static {v0, v5}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@d0
    move-result v23

    #@d1
    .line 986
    .restart local v23    # "y":F
    move-object/from16 v0, p0

    #@d3
    move/from16 v1, v22

    #@d5
    move/from16 v2, v23

    #@d7
    move/from16 v3, v17

    #@d9
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->saveInitialMotion(FFI)V

    #@dc
    .line 989
    move-object/from16 v0, p0

    #@de
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@e0
    move/from16 v24, v0

    #@e2
    if-nez v24, :cond_4

    #@e4
    .line 990
    move-object/from16 v0, p0

    #@e6
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialEdgesTouched:[I

    #@e8
    move-object/from16 v24, v0

    #@ea
    aget v8, v24, v17

    #@ec
    .line 991
    .restart local v8    # "edgesTouched":I
    move-object/from16 v0, p0

    #@ee
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@f0
    move/from16 v24, v0

    #@f2
    and-int v24, v24, v8

    #@f4
    if-eqz v24, :cond_2

    #@f6
    .line 992
    move-object/from16 v0, p0

    #@f8
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@fa
    move-object/from16 v24, v0

    #@fc
    move-object/from16 v0, p0

    #@fe
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mTrackingEdges:I

    #@100
    move/from16 v25, v0

    #@102
    and-int v25, v25, v8

    #@104
    move-object/from16 v0, v24

    #@106
    move/from16 v1, v25

    #@108
    move/from16 v2, v17

    #@10a
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->onEdgeTouched(II)V

    #@10d
    goto/16 :goto_0

    #@10f
    .line 994
    .end local v8    # "edgesTouched":I
    :cond_4
    move-object/from16 v0, p0

    #@111
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@113
    move/from16 v24, v0

    #@115
    const/16 v25, 0x2

    #@117
    move/from16 v0, v24

    #@119
    move/from16 v1, v25

    #@11b
    if-ne v0, v1, :cond_2

    #@11d
    .line 996
    move/from16 v0, v22

    #@11f
    float-to-int v0, v0

    #@120
    move/from16 v24, v0

    #@122
    move/from16 v0, v23

    #@124
    float-to-int v0, v0

    #@125
    move/from16 v25, v0

    #@127
    move-object/from16 v0, p0

    #@129
    move/from16 v1, v24

    #@12b
    move/from16 v2, v25

    #@12d
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@130
    move-result-object v20

    #@131
    .line 997
    .restart local v20    # "toCapture":Landroid/view/View;
    move-object/from16 v0, p0

    #@133
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@135
    move-object/from16 v24, v0

    #@137
    move-object/from16 v0, v20

    #@139
    move-object/from16 v1, v24

    #@13b
    if-ne v0, v1, :cond_2

    #@13d
    .line 998
    move-object/from16 v0, p0

    #@13f
    move-object/from16 v1, v20

    #@141
    move/from16 v2, v17

    #@143
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@146
    goto/16 :goto_0

    #@148
    .line 1006
    .end local v17    # "pointerId":I
    .end local v20    # "toCapture":Landroid/view/View;
    .end local v22    # "x":F
    .end local v23    # "y":F
    :pswitch_3
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getPointerCount(Landroid/view/MotionEvent;)I

    #@14b
    move-result v16

    #@14c
    .line 1007
    .local v16, "pointerCount":I
    const/4 v10, 0x0

    #@14d
    .local v10, "i":I
    :goto_2
    move/from16 v0, v16

    #@14f
    if-ge v10, v0, :cond_6

    #@151
    .line 1008
    move-object/from16 v0, p1

    #@153
    invoke-static {v0, v10}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@156
    move-result v17

    #@157
    .line 1009
    .restart local v17    # "pointerId":I
    move-object/from16 v0, p1

    #@159
    invoke-static {v0, v10}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@15c
    move-result v22

    #@15d
    .line 1010
    .restart local v22    # "x":F
    move-object/from16 v0, p1

    #@15f
    invoke-static {v0, v10}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@162
    move-result v23

    #@163
    .line 1011
    .restart local v23    # "y":F
    move-object/from16 v0, p0

    #@165
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionX:[F

    #@167
    move-object/from16 v24, v0

    #@169
    aget v24, v24, v17

    #@16b
    sub-float v6, v22, v24

    #@16d
    .line 1012
    .local v6, "dx":F
    move-object/from16 v0, p0

    #@16f
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mInitialMotionY:[F

    #@171
    move-object/from16 v24, v0

    #@173
    aget v24, v24, v17

    #@175
    sub-float v7, v23, v24

    #@177
    .line 1014
    .local v7, "dy":F
    move/from16 v0, v22

    #@179
    float-to-int v0, v0

    #@17a
    move/from16 v24, v0

    #@17c
    move/from16 v0, v23

    #@17e
    float-to-int v0, v0

    #@17f
    move/from16 v25, v0

    #@181
    move-object/from16 v0, p0

    #@183
    move/from16 v1, v24

    #@185
    move/from16 v2, v25

    #@187
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@18a
    move-result-object v20

    #@18b
    .line 1015
    .restart local v20    # "toCapture":Landroid/view/View;
    if-eqz v20, :cond_7

    #@18d
    move-object/from16 v0, p0

    #@18f
    move-object/from16 v1, v20

    #@191
    invoke-direct {v0, v1, v6, v7}, Landroid/support/v4/widget/ViewDragHelper;->checkTouchSlop(Landroid/view/View;FF)Z

    #@194
    move-result v24

    #@195
    if-eqz v24, :cond_7

    #@197
    const/4 v15, 0x1

    #@198
    .line 1016
    .local v15, "pastSlop":Z
    :goto_3
    if-eqz v15, :cond_8

    #@19a
    .line 1022
    invoke-virtual/range {v20 .. v20}, Landroid/view/View;->getLeft()I

    #@19d
    move-result v13

    #@19e
    .line 1023
    .local v13, "oldLeft":I
    float-to-int v0, v6

    #@19f
    move/from16 v24, v0

    #@1a1
    add-int v18, v13, v24

    #@1a3
    .line 1024
    .local v18, "targetLeft":I
    move-object/from16 v0, p0

    #@1a5
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@1a7
    move-object/from16 v24, v0

    #@1a9
    float-to-int v0, v6

    #@1aa
    move/from16 v25, v0

    #@1ac
    move-object/from16 v0, v24

    #@1ae
    move-object/from16 v1, v20

    #@1b0
    move/from16 v2, v18

    #@1b2
    move/from16 v3, v25

    #@1b4
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper$Callback;->clampViewPositionHorizontal(Landroid/view/View;II)I

    #@1b7
    move-result v11

    #@1b8
    .line 1026
    .local v11, "newLeft":I
    invoke-virtual/range {v20 .. v20}, Landroid/view/View;->getTop()I

    #@1bb
    move-result v14

    #@1bc
    .line 1027
    .local v14, "oldTop":I
    float-to-int v0, v7

    #@1bd
    move/from16 v24, v0

    #@1bf
    add-int v19, v14, v24

    #@1c1
    .line 1028
    .local v19, "targetTop":I
    move-object/from16 v0, p0

    #@1c3
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@1c5
    move-object/from16 v24, v0

    #@1c7
    float-to-int v0, v7

    #@1c8
    move/from16 v25, v0

    #@1ca
    move-object/from16 v0, v24

    #@1cc
    move-object/from16 v1, v20

    #@1ce
    move/from16 v2, v19

    #@1d0
    move/from16 v3, v25

    #@1d2
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper$Callback;->clampViewPositionVertical(Landroid/view/View;II)I

    #@1d5
    move-result v12

    #@1d6
    .line 1030
    .local v12, "newTop":I
    move-object/from16 v0, p0

    #@1d8
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@1da
    move-object/from16 v24, v0

    #@1dc
    move-object/from16 v0, v24

    #@1de
    move-object/from16 v1, v20

    #@1e0
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewHorizontalDragRange(Landroid/view/View;)I

    #@1e3
    move-result v9

    #@1e4
    .line 1032
    .local v9, "horizontalDragRange":I
    move-object/from16 v0, p0

    #@1e6
    iget-object v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@1e8
    move-object/from16 v24, v0

    #@1ea
    move-object/from16 v0, v24

    #@1ec
    move-object/from16 v1, v20

    #@1ee
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/ViewDragHelper$Callback;->getViewVerticalDragRange(Landroid/view/View;)I

    #@1f1
    move-result v21

    #@1f2
    .line 1033
    .local v21, "verticalDragRange":I
    if-eqz v9, :cond_5

    #@1f4
    if-lez v9, :cond_8

    #@1f6
    if-ne v11, v13, :cond_8

    #@1f8
    :cond_5
    if-eqz v21, :cond_6

    #@1fa
    if-lez v21, :cond_8

    #@1fc
    if-ne v12, v14, :cond_8

    #@1fe
    .line 1049
    .end local v6    # "dx":F
    .end local v7    # "dy":F
    .end local v9    # "horizontalDragRange":I
    .end local v11    # "newLeft":I
    .end local v12    # "newTop":I
    .end local v13    # "oldLeft":I
    .end local v14    # "oldTop":I
    .end local v15    # "pastSlop":Z
    .end local v17    # "pointerId":I
    .end local v18    # "targetLeft":I
    .end local v19    # "targetTop":I
    .end local v20    # "toCapture":Landroid/view/View;
    .end local v21    # "verticalDragRange":I
    .end local v22    # "x":F
    .end local v23    # "y":F
    :cond_6
    invoke-direct/range {p0 .. p1}, Landroid/support/v4/widget/ViewDragHelper;->saveLastMotion(Landroid/view/MotionEvent;)V

    #@201
    goto/16 :goto_0

    #@203
    .line 1015
    .restart local v6    # "dx":F
    .restart local v7    # "dy":F
    .restart local v17    # "pointerId":I
    .restart local v20    # "toCapture":Landroid/view/View;
    .restart local v22    # "x":F
    .restart local v23    # "y":F
    :cond_7
    const/4 v15, 0x0

    #@204
    goto :goto_3

    #@205
    .line 1039
    .restart local v15    # "pastSlop":Z
    :cond_8
    move-object/from16 v0, p0

    #@207
    move/from16 v1, v17

    #@209
    invoke-direct {v0, v6, v7, v1}, Landroid/support/v4/widget/ViewDragHelper;->reportNewEdgeDrags(FFI)V

    #@20c
    .line 1040
    move-object/from16 v0, p0

    #@20e
    iget v0, v0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@210
    move/from16 v24, v0

    #@212
    const/16 v25, 0x1

    #@214
    move/from16 v0, v24

    #@216
    move/from16 v1, v25

    #@218
    if-eq v0, v1, :cond_6

    #@21a
    .line 1045
    if-eqz v15, :cond_9

    #@21c
    move-object/from16 v0, p0

    #@21e
    move-object/from16 v1, v20

    #@220
    move/from16 v2, v17

    #@222
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/ViewDragHelper;->tryCaptureViewForDrag(Landroid/view/View;I)Z

    #@225
    move-result v24

    #@226
    if-nez v24, :cond_6

    #@228
    .line 1007
    :cond_9
    add-int/lit8 v10, v10, 0x1

    #@22a
    goto/16 :goto_2

    #@22c
    .line 1054
    .end local v6    # "dx":F
    .end local v7    # "dy":F
    .end local v10    # "i":I
    .end local v15    # "pastSlop":Z
    .end local v16    # "pointerCount":I
    .end local v17    # "pointerId":I
    .end local v20    # "toCapture":Landroid/view/View;
    .end local v22    # "x":F
    .end local v23    # "y":F
    :pswitch_4
    move-object/from16 v0, p1

    #@22e
    invoke-static {v0, v5}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@231
    move-result v17

    #@232
    .line 1055
    .restart local v17    # "pointerId":I
    move-object/from16 v0, p0

    #@234
    move/from16 v1, v17

    #@236
    invoke-direct {v0, v1}, Landroid/support/v4/widget/ViewDragHelper;->clearMotionHistory(I)V

    #@239
    goto/16 :goto_0

    #@23b
    .line 1061
    .end local v17    # "pointerId":I
    :pswitch_5
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@23e
    goto/16 :goto_0

    #@240
    .line 1066
    :cond_a
    const/16 v24, 0x0

    #@242
    goto/16 :goto_1

    #@244
    .line 960
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_3
        :pswitch_5
        :pswitch_0
        :pswitch_2
        :pswitch_4
    .end packed-switch
.end method

.method public smoothSlideViewTo(Landroid/view/View;II)Z
    .locals 3
    .param p1, "child"    # Landroid/view/View;
    .param p2, "finalLeft"    # I
    .param p3, "finalTop"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 539
    iput-object p1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@3
    .line 540
    const/4 v1, -0x1

    #@4
    iput v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@6
    .line 542
    invoke-direct {p0, p2, p3, v2, v2}, Landroid/support/v4/widget/ViewDragHelper;->forceSettleCapturedViewAt(IIII)Z

    #@9
    move-result v0

    #@a
    .line 543
    .local v0, "continueSliding":Z
    if-nez v0, :cond_0

    #@c
    iget v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mDragState:I

    #@e
    if-nez v1, :cond_0

    #@10
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@12
    if-eqz v1, :cond_0

    #@14
    .line 546
    const/4 v1, 0x0

    #@15
    iput-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@17
    .line 549
    :cond_0
    return v0
.end method

.method tryCaptureViewForDrag(Landroid/view/View;I)Z
    .locals 2
    .param p1, "toCapture"    # Landroid/view/View;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 890
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCapturedView:Landroid/view/View;

    #@3
    if-ne p1, v1, :cond_0

    #@5
    iget v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@7
    if-ne v1, p2, :cond_0

    #@9
    .line 899
    :goto_0
    return v0

    #@a
    .line 894
    :cond_0
    if-eqz p1, :cond_1

    #@c
    iget-object v1, p0, Landroid/support/v4/widget/ViewDragHelper;->mCallback:Landroid/support/v4/widget/ViewDragHelper$Callback;

    #@e
    invoke-virtual {v1, p1, p2}, Landroid/support/v4/widget/ViewDragHelper$Callback;->tryCaptureView(Landroid/view/View;I)Z

    #@11
    move-result v1

    #@12
    if-eqz v1, :cond_1

    #@14
    .line 895
    iput p2, p0, Landroid/support/v4/widget/ViewDragHelper;->mActivePointerId:I

    #@16
    .line 896
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/widget/ViewDragHelper;->captureChildView(Landroid/view/View;I)V

    #@19
    goto :goto_0

    #@1a
    .line 899
    :cond_1
    const/4 v0, 0x0

    #@1b
    goto :goto_0
.end method
