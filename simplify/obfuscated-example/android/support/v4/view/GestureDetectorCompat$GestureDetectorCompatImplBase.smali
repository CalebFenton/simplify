.class Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;
.super Ljava/lang/Object;
.source "GestureDetectorCompat.java"

# interfaces
.implements Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/GestureDetectorCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "GestureDetectorCompatImplBase"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler;
    }
.end annotation


# static fields
.field private static final DOUBLE_TAP_TIMEOUT:I

.field private static final LONGPRESS_TIMEOUT:I

.field private static final LONG_PRESS:I = 0x2

.field private static final SHOW_PRESS:I = 0x1

.field private static final TAP:I = 0x3

.field private static final TAP_TIMEOUT:I


# instance fields
.field private mAlwaysInBiggerTapRegion:Z

.field private mAlwaysInTapRegion:Z

.field private mCurrentDownEvent:Landroid/view/MotionEvent;

.field private mDeferConfirmSingleTap:Z

.field private mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

.field private mDoubleTapSlopSquare:I

.field private mDownFocusX:F

.field private mDownFocusY:F

.field private final mHandler:Landroid/os/Handler;

.field private mInLongPress:Z

.field private mIsDoubleTapping:Z

.field private mIsLongpressEnabled:Z

.field private mLastFocusX:F

.field private mLastFocusY:F

.field private final mListener:Landroid/view/GestureDetector$OnGestureListener;

.field private mMaximumFlingVelocity:I

.field private mMinimumFlingVelocity:I

.field private mPreviousUpEvent:Landroid/view/MotionEvent;

.field private mStillDown:Z

.field private mTouchSlopSquare:I

.field private mVelocityTracker:Landroid/view/VelocityTracker;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 62
    invoke-static {}, Landroid/view/ViewConfiguration;->getLongPressTimeout()I

    #@3
    move-result v0

    #@4
    sput v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->LONGPRESS_TIMEOUT:I

    #@6
    .line 63
    invoke-static {}, Landroid/view/ViewConfiguration;->getTapTimeout()I

    #@9
    move-result v0

    #@a
    sput v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->TAP_TIMEOUT:I

    #@c
    .line 64
    invoke-static {}, Landroid/view/ViewConfiguration;->getDoubleTapTimeout()I

    #@f
    move-result v0

    #@10
    sput v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->DOUBLE_TAP_TIMEOUT:I

    #@12
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/view/GestureDetector$OnGestureListener;Landroid/os/Handler;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "listener"    # Landroid/view/GestureDetector$OnGestureListener;
    .param p3, "handler"    # Landroid/os/Handler;

    #@0
    .prologue
    .line 152
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 153
    if-eqz p3, :cond_1

    #@5
    .line 154
    new-instance v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler;

    #@7
    invoke-direct {v0, p0, p3}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler;-><init>(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;Landroid/os/Handler;)V

    #@a
    iput-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@c
    .line 158
    :goto_0
    iput-object p2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@e
    .line 159
    instance-of v0, p2, Landroid/view/GestureDetector$OnDoubleTapListener;

    #@10
    if-eqz v0, :cond_0

    #@12
    .line 160
    check-cast p2, Landroid/view/GestureDetector$OnDoubleTapListener;

    #@14
    .end local p2    # "listener":Landroid/view/GestureDetector$OnGestureListener;
    invoke-virtual {p0, p2}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->setOnDoubleTapListener(Landroid/view/GestureDetector$OnDoubleTapListener;)V

    #@17
    .line 162
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->init(Landroid/content/Context;)V

    #@1a
    .line 163
    return-void

    #@1b
    .line 156
    .restart local p2    # "listener":Landroid/view/GestureDetector$OnGestureListener;
    :cond_1
    new-instance v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler;

    #@1d
    invoke-direct {v0, p0}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler;-><init>(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)V

    #@20
    iput-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@22
    goto :goto_0
.end method

.method static synthetic access$000(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)Landroid/view/MotionEvent;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@2
    return-object v0
.end method

.method static synthetic access$100(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)Landroid/view/GestureDetector$OnGestureListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;

    #@0
    .prologue
    .line 56
    invoke-direct {p0}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->dispatchLongPress()V

    #@3
    return-void
.end method

.method static synthetic access$300(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)Landroid/view/GestureDetector$OnDoubleTapListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;

    #@0
    .prologue
    .line 56
    iget-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mStillDown:Z

    #@2
    return v0
.end method

.method static synthetic access$502(Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 56
    iput-boolean p1, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@2
    return p1
.end method

.method private cancel()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 409
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@3
    const/4 v1, 0x1

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@7
    .line 410
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@9
    const/4 v1, 0x2

    #@a
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@d
    .line 411
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@f
    const/4 v1, 0x3

    #@10
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@13
    .line 412
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@15
    invoke-virtual {v0}, Landroid/view/VelocityTracker;->recycle()V

    #@18
    .line 413
    const/4 v0, 0x0

    #@19
    iput-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1b
    .line 414
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@1d
    .line 415
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mStillDown:Z

    #@1f
    .line 416
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@21
    .line 417
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInBiggerTapRegion:Z

    #@23
    .line 418
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@25
    .line 419
    iget-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@27
    if-eqz v0, :cond_0

    #@29
    .line 420
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@2b
    .line 422
    :cond_0
    return-void
.end method

.method private cancelTaps()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 425
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@3
    const/4 v1, 0x1

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@7
    .line 426
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@9
    const/4 v1, 0x2

    #@a
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@d
    .line 427
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@f
    const/4 v1, 0x3

    #@10
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@13
    .line 428
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@15
    .line 429
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@17
    .line 430
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInBiggerTapRegion:Z

    #@19
    .line 431
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@1b
    .line 432
    iget-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@1d
    if-eqz v0, :cond_0

    #@1f
    .line 433
    iput-boolean v2, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@21
    .line 435
    :cond_0
    return-void
.end method

.method private dispatchLongPress()V
    .locals 2

    #@0
    .prologue
    .line 453
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@2
    const/4 v1, 0x3

    #@3
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@6
    .line 454
    const/4 v0, 0x0

    #@7
    iput-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@9
    .line 455
    const/4 v0, 0x1

    #@a
    iput-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@c
    .line 456
    iget-object v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@e
    iget-object v1, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@10
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnGestureListener;->onLongPress(Landroid/view/MotionEvent;)V

    #@13
    .line 457
    return-void
.end method

.method private init(Landroid/content/Context;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 166
    if-nez p1, :cond_0

    #@2
    .line 167
    new-instance v3, Ljava/lang/IllegalArgumentException;

    #@4
    const-string v4, "Context must not be null"

    #@6
    invoke-direct {v3, v4}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@9
    throw v3

    #@a
    .line 169
    :cond_0
    iget-object v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@c
    if-nez v3, :cond_1

    #@e
    .line 170
    new-instance v3, Ljava/lang/IllegalArgumentException;

    #@10
    const-string v4, "OnGestureListener must not be null"

    #@12
    invoke-direct {v3, v4}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@15
    throw v3

    #@16
    .line 172
    :cond_1
    const/4 v3, 0x1

    #@17
    iput-boolean v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsLongpressEnabled:Z

    #@19
    .line 174
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@1c
    move-result-object v0

    #@1d
    .line 175
    .local v0, "configuration":Landroid/view/ViewConfiguration;
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@20
    move-result v2

    #@21
    .line 176
    .local v2, "touchSlop":I
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledDoubleTapSlop()I

    #@24
    move-result v1

    #@25
    .line 177
    .local v1, "doubleTapSlop":I
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledMinimumFlingVelocity()I

    #@28
    move-result v3

    #@29
    iput v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMinimumFlingVelocity:I

    #@2b
    .line 178
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledMaximumFlingVelocity()I

    #@2e
    move-result v3

    #@2f
    iput v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMaximumFlingVelocity:I

    #@31
    .line 180
    mul-int v3, v2, v2

    #@33
    iput v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mTouchSlopSquare:I

    #@35
    .line 181
    mul-int v3, v1, v1

    #@37
    iput v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapSlopSquare:I

    #@39
    .line 182
    return-void
.end method

.method private isConsideredDoubleTap(Landroid/view/MotionEvent;Landroid/view/MotionEvent;Landroid/view/MotionEvent;)Z
    .locals 8
    .param p1, "firstDown"    # Landroid/view/MotionEvent;
    .param p2, "firstUp"    # Landroid/view/MotionEvent;
    .param p3, "secondDown"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 439
    iget-boolean v3, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInBiggerTapRegion:Z

    #@3
    if-nez v3, :cond_1

    #@5
    .line 449
    :cond_0
    :goto_0
    return v2

    #@6
    .line 443
    :cond_1
    invoke-virtual {p3}, Landroid/view/MotionEvent;->getEventTime()J

    #@9
    move-result-wide v4

    #@a
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getEventTime()J

    #@d
    move-result-wide v6

    #@e
    sub-long/2addr v4, v6

    #@f
    sget v3, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->DOUBLE_TAP_TIMEOUT:I

    #@11
    int-to-long v6, v3

    #@12
    cmp-long v3, v4, v6

    #@14
    if-gtz v3, :cond_0

    #@16
    .line 447
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@19
    move-result v3

    #@1a
    float-to-int v3, v3

    #@1b
    invoke-virtual {p3}, Landroid/view/MotionEvent;->getX()F

    #@1e
    move-result v4

    #@1f
    float-to-int v4, v4

    #@20
    sub-int v0, v3, v4

    #@22
    .line 448
    .local v0, "deltaX":I
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@25
    move-result v3

    #@26
    float-to-int v3, v3

    #@27
    invoke-virtual {p3}, Landroid/view/MotionEvent;->getY()F

    #@2a
    move-result v4

    #@2b
    float-to-int v4, v4

    #@2c
    sub-int v1, v3, v4

    #@2e
    .line 449
    .local v1, "deltaY":I
    mul-int v3, v0, v0

    #@30
    mul-int v4, v1, v1

    #@32
    add-int/2addr v3, v4

    #@33
    iget v4, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapSlopSquare:I

    #@35
    if-ge v3, v4, :cond_0

    #@37
    const/4 v2, 0x1

    #@38
    goto :goto_0
.end method


# virtual methods
.method public isLongpressEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 212
    iget-boolean v0, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsLongpressEnabled:Z

    #@2
    return v0
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 42
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 224
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getAction()I

    #@3
    move-result v6

    #@4
    .line 226
    .local v6, "action":I
    move-object/from16 v0, p0

    #@6
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@8
    move-object/from16 v36, v0

    #@a
    if-nez v36, :cond_0

    #@c
    .line 227
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@f
    move-result-object v36

    #@10
    move-object/from16 v0, v36

    #@12
    move-object/from16 v1, p0

    #@14
    iput-object v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@16
    .line 229
    :cond_0
    move-object/from16 v0, p0

    #@18
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1a
    move-object/from16 v36, v0

    #@1c
    move-object/from16 v0, v36

    #@1e
    move-object/from16 v1, p1

    #@20
    invoke-virtual {v0, v1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@23
    .line 231
    and-int/lit16 v0, v6, 0xff

    #@25
    move/from16 v36, v0

    #@27
    const/16 v37, 0x6

    #@29
    move/from16 v0, v36

    #@2b
    move/from16 v1, v37

    #@2d
    if-ne v0, v1, :cond_1

    #@2f
    const/16 v22, 0x1

    #@31
    .line 233
    .local v22, "pointerUp":Z
    :goto_0
    if-eqz v22, :cond_2

    #@33
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@36
    move-result v25

    #@37
    .line 236
    .local v25, "skipIndex":I
    :goto_1
    const/16 v26, 0x0

    #@39
    .local v26, "sumX":F
    const/16 v27, 0x0

    #@3b
    .line 237
    .local v27, "sumY":F
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getPointerCount(Landroid/view/MotionEvent;)I

    #@3e
    move-result v7

    #@3f
    .line 238
    .local v7, "count":I
    const/16 v18, 0x0

    #@41
    .local v18, "i":I
    :goto_2
    move/from16 v0, v18

    #@43
    if-ge v0, v7, :cond_4

    #@45
    .line 239
    move/from16 v0, v25

    #@47
    move/from16 v1, v18

    #@49
    if-ne v0, v1, :cond_3

    #@4b
    .line 238
    :goto_3
    add-int/lit8 v18, v18, 0x1

    #@4d
    goto :goto_2

    #@4e
    .line 231
    .end local v7    # "count":I
    .end local v18    # "i":I
    .end local v22    # "pointerUp":Z
    .end local v25    # "skipIndex":I
    .end local v26    # "sumX":F
    .end local v27    # "sumY":F
    :cond_1
    const/16 v22, 0x0

    #@50
    goto :goto_0

    #@51
    .line 233
    .restart local v22    # "pointerUp":Z
    :cond_2
    const/16 v25, -0x1

    #@53
    goto :goto_1

    #@54
    .line 240
    .restart local v7    # "count":I
    .restart local v18    # "i":I
    .restart local v25    # "skipIndex":I
    .restart local v26    # "sumX":F
    .restart local v27    # "sumY":F
    :cond_3
    move-object/from16 v0, p1

    #@56
    move/from16 v1, v18

    #@58
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@5b
    move-result v36

    #@5c
    add-float v26, v26, v36

    #@5e
    .line 241
    move-object/from16 v0, p1

    #@60
    move/from16 v1, v18

    #@62
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@65
    move-result v36

    #@66
    add-float v27, v27, v36

    #@68
    goto :goto_3

    #@69
    .line 243
    :cond_4
    if-eqz v22, :cond_6

    #@6b
    add-int/lit8 v12, v7, -0x1

    #@6d
    .line 244
    .local v12, "div":I
    :goto_4
    int-to-float v0, v12

    #@6e
    move/from16 v36, v0

    #@70
    div-float v14, v26, v36

    #@72
    .line 245
    .local v14, "focusX":F
    int-to-float v0, v12

    #@73
    move/from16 v36, v0

    #@75
    div-float v15, v27, v36

    #@77
    .line 247
    .local v15, "focusY":F
    const/16 v17, 0x0

    #@79
    .line 249
    .local v17, "handled":Z
    and-int/lit16 v0, v6, 0xff

    #@7b
    move/from16 v36, v0

    #@7d
    packed-switch v36, :pswitch_data_0

    #@80
    .line 405
    :cond_5
    :goto_5
    :pswitch_0
    return v17

    #@81
    .end local v12    # "div":I
    .end local v14    # "focusX":F
    .end local v15    # "focusY":F
    .end local v17    # "handled":Z
    :cond_6
    move v12, v7

    #@82
    .line 243
    goto :goto_4

    #@83
    .line 251
    .restart local v12    # "div":I
    .restart local v14    # "focusX":F
    .restart local v15    # "focusY":F
    .restart local v17    # "handled":Z
    :pswitch_1
    move-object/from16 v0, p0

    #@85
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@87
    move-object/from16 v0, p0

    #@89
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusX:F

    #@8b
    .line 252
    move-object/from16 v0, p0

    #@8d
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@8f
    move-object/from16 v0, p0

    #@91
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusY:F

    #@93
    .line 254
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->cancelTaps()V

    #@96
    goto :goto_5

    #@97
    .line 258
    :pswitch_2
    move-object/from16 v0, p0

    #@99
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@9b
    move-object/from16 v0, p0

    #@9d
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusX:F

    #@9f
    .line 259
    move-object/from16 v0, p0

    #@a1
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@a3
    move-object/from16 v0, p0

    #@a5
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusY:F

    #@a7
    .line 263
    move-object/from16 v0, p0

    #@a9
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@ab
    move-object/from16 v36, v0

    #@ad
    const/16 v37, 0x3e8

    #@af
    move-object/from16 v0, p0

    #@b1
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMaximumFlingVelocity:I

    #@b3
    move/from16 v38, v0

    #@b5
    move/from16 v0, v38

    #@b7
    int-to-float v0, v0

    #@b8
    move/from16 v38, v0

    #@ba
    invoke-virtual/range {v36 .. v38}, Landroid/view/VelocityTracker;->computeCurrentVelocity(IF)V

    #@bd
    .line 264
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@c0
    move-result v28

    #@c1
    .line 265
    .local v28, "upIndex":I
    move-object/from16 v0, p1

    #@c3
    move/from16 v1, v28

    #@c5
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@c8
    move-result v19

    #@c9
    .line 266
    .local v19, "id1":I
    move-object/from16 v0, p0

    #@cb
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@cd
    move-object/from16 v36, v0

    #@cf
    move-object/from16 v0, v36

    #@d1
    move/from16 v1, v19

    #@d3
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@d6
    move-result v33

    #@d7
    .line 267
    .local v33, "x1":F
    move-object/from16 v0, p0

    #@d9
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@db
    move-object/from16 v36, v0

    #@dd
    move-object/from16 v0, v36

    #@df
    move/from16 v1, v19

    #@e1
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@e4
    move-result v35

    #@e5
    .line 268
    .local v35, "y1":F
    const/16 v18, 0x0

    #@e7
    :goto_6
    move/from16 v0, v18

    #@e9
    if-ge v0, v7, :cond_5

    #@eb
    .line 269
    move/from16 v0, v18

    #@ed
    move/from16 v1, v28

    #@ef
    if-ne v0, v1, :cond_8

    #@f1
    .line 268
    :cond_7
    add-int/lit8 v18, v18, 0x1

    #@f3
    goto :goto_6

    #@f4
    .line 271
    :cond_8
    move-object/from16 v0, p1

    #@f6
    move/from16 v1, v18

    #@f8
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@fb
    move-result v20

    #@fc
    .line 272
    .local v20, "id2":I
    move-object/from16 v0, p0

    #@fe
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@100
    move-object/from16 v36, v0

    #@102
    move-object/from16 v0, v36

    #@104
    move/from16 v1, v20

    #@106
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@109
    move-result v36

    #@10a
    mul-float v32, v33, v36

    #@10c
    .line 273
    .local v32, "x":F
    move-object/from16 v0, p0

    #@10e
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@110
    move-object/from16 v36, v0

    #@112
    move-object/from16 v0, v36

    #@114
    move/from16 v1, v20

    #@116
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@119
    move-result v36

    #@11a
    mul-float v34, v35, v36

    #@11c
    .line 275
    .local v34, "y":F
    add-float v13, v32, v34

    #@11e
    .line 276
    .local v13, "dot":F
    const/16 v36, 0x0

    #@120
    cmpg-float v36, v13, v36

    #@122
    if-gez v36, :cond_7

    #@124
    .line 277
    move-object/from16 v0, p0

    #@126
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@128
    move-object/from16 v36, v0

    #@12a
    invoke-virtual/range {v36 .. v36}, Landroid/view/VelocityTracker;->clear()V

    #@12d
    goto/16 :goto_5

    #@12f
    .line 284
    .end local v13    # "dot":F
    .end local v19    # "id1":I
    .end local v20    # "id2":I
    .end local v28    # "upIndex":I
    .end local v32    # "x":F
    .end local v33    # "x1":F
    .end local v34    # "y":F
    .end local v35    # "y1":F
    :pswitch_3
    move-object/from16 v0, p0

    #@131
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@133
    move-object/from16 v36, v0

    #@135
    if-eqz v36, :cond_a

    #@137
    .line 285
    move-object/from16 v0, p0

    #@139
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@13b
    move-object/from16 v36, v0

    #@13d
    const/16 v37, 0x3

    #@13f
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->hasMessages(I)Z

    #@142
    move-result v16

    #@143
    .line 286
    .local v16, "hadTapMessage":Z
    if-eqz v16, :cond_9

    #@145
    move-object/from16 v0, p0

    #@147
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@149
    move-object/from16 v36, v0

    #@14b
    const/16 v37, 0x3

    #@14d
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@150
    .line 287
    :cond_9
    move-object/from16 v0, p0

    #@152
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@154
    move-object/from16 v36, v0

    #@156
    if-eqz v36, :cond_d

    #@158
    move-object/from16 v0, p0

    #@15a
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mPreviousUpEvent:Landroid/view/MotionEvent;

    #@15c
    move-object/from16 v36, v0

    #@15e
    if-eqz v36, :cond_d

    #@160
    if-eqz v16, :cond_d

    #@162
    move-object/from16 v0, p0

    #@164
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@166
    move-object/from16 v36, v0

    #@168
    move-object/from16 v0, p0

    #@16a
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mPreviousUpEvent:Landroid/view/MotionEvent;

    #@16c
    move-object/from16 v37, v0

    #@16e
    move-object/from16 v0, p0

    #@170
    move-object/from16 v1, v36

    #@172
    move-object/from16 v2, v37

    #@174
    move-object/from16 v3, p1

    #@176
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->isConsideredDoubleTap(Landroid/view/MotionEvent;Landroid/view/MotionEvent;Landroid/view/MotionEvent;)Z

    #@179
    move-result v36

    #@17a
    if-eqz v36, :cond_d

    #@17c
    .line 290
    const/16 v36, 0x1

    #@17e
    move/from16 v0, v36

    #@180
    move-object/from16 v1, p0

    #@182
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@184
    .line 292
    move-object/from16 v0, p0

    #@186
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@188
    move-object/from16 v36, v0

    #@18a
    move-object/from16 v0, p0

    #@18c
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@18e
    move-object/from16 v37, v0

    #@190
    invoke-interface/range {v36 .. v37}, Landroid/view/GestureDetector$OnDoubleTapListener;->onDoubleTap(Landroid/view/MotionEvent;)Z

    #@193
    move-result v36

    #@194
    or-int v17, v17, v36

    #@196
    .line 294
    move-object/from16 v0, p0

    #@198
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@19a
    move-object/from16 v36, v0

    #@19c
    move-object/from16 v0, v36

    #@19e
    move-object/from16 v1, p1

    #@1a0
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnDoubleTapListener;->onDoubleTapEvent(Landroid/view/MotionEvent;)Z

    #@1a3
    move-result v36

    #@1a4
    or-int v17, v17, v36

    #@1a6
    .line 301
    .end local v16    # "hadTapMessage":Z
    :cond_a
    :goto_7
    move-object/from16 v0, p0

    #@1a8
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@1aa
    move-object/from16 v0, p0

    #@1ac
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusX:F

    #@1ae
    .line 302
    move-object/from16 v0, p0

    #@1b0
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@1b2
    move-object/from16 v0, p0

    #@1b4
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusY:F

    #@1b6
    .line 303
    move-object/from16 v0, p0

    #@1b8
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@1ba
    move-object/from16 v36, v0

    #@1bc
    if-eqz v36, :cond_b

    #@1be
    .line 304
    move-object/from16 v0, p0

    #@1c0
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@1c2
    move-object/from16 v36, v0

    #@1c4
    invoke-virtual/range {v36 .. v36}, Landroid/view/MotionEvent;->recycle()V

    #@1c7
    .line 306
    :cond_b
    invoke-static/range {p1 .. p1}, Landroid/view/MotionEvent;->obtain(Landroid/view/MotionEvent;)Landroid/view/MotionEvent;

    #@1ca
    move-result-object v36

    #@1cb
    move-object/from16 v0, v36

    #@1cd
    move-object/from16 v1, p0

    #@1cf
    iput-object v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@1d1
    .line 307
    const/16 v36, 0x1

    #@1d3
    move/from16 v0, v36

    #@1d5
    move-object/from16 v1, p0

    #@1d7
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@1d9
    .line 308
    const/16 v36, 0x1

    #@1db
    move/from16 v0, v36

    #@1dd
    move-object/from16 v1, p0

    #@1df
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInBiggerTapRegion:Z

    #@1e1
    .line 309
    const/16 v36, 0x1

    #@1e3
    move/from16 v0, v36

    #@1e5
    move-object/from16 v1, p0

    #@1e7
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mStillDown:Z

    #@1e9
    .line 310
    const/16 v36, 0x0

    #@1eb
    move/from16 v0, v36

    #@1ed
    move-object/from16 v1, p0

    #@1ef
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@1f1
    .line 311
    const/16 v36, 0x0

    #@1f3
    move/from16 v0, v36

    #@1f5
    move-object/from16 v1, p0

    #@1f7
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@1f9
    .line 313
    move-object/from16 v0, p0

    #@1fb
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsLongpressEnabled:Z

    #@1fd
    move/from16 v36, v0

    #@1ff
    if-eqz v36, :cond_c

    #@201
    .line 314
    move-object/from16 v0, p0

    #@203
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@205
    move-object/from16 v36, v0

    #@207
    const/16 v37, 0x2

    #@209
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@20c
    .line 315
    move-object/from16 v0, p0

    #@20e
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@210
    move-object/from16 v36, v0

    #@212
    const/16 v37, 0x2

    #@214
    move-object/from16 v0, p0

    #@216
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@218
    move-object/from16 v38, v0

    #@21a
    invoke-virtual/range {v38 .. v38}, Landroid/view/MotionEvent;->getDownTime()J

    #@21d
    move-result-wide v38

    #@21e
    sget v40, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->TAP_TIMEOUT:I

    #@220
    move/from16 v0, v40

    #@222
    int-to-long v0, v0

    #@223
    move-wide/from16 v40, v0

    #@225
    add-long v38, v38, v40

    #@227
    sget v40, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->LONGPRESS_TIMEOUT:I

    #@229
    move/from16 v0, v40

    #@22b
    int-to-long v0, v0

    #@22c
    move-wide/from16 v40, v0

    #@22e
    add-long v38, v38, v40

    #@230
    invoke-virtual/range {v36 .. v39}, Landroid/os/Handler;->sendEmptyMessageAtTime(IJ)Z

    #@233
    .line 318
    :cond_c
    move-object/from16 v0, p0

    #@235
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@237
    move-object/from16 v36, v0

    #@239
    const/16 v37, 0x1

    #@23b
    move-object/from16 v0, p0

    #@23d
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@23f
    move-object/from16 v38, v0

    #@241
    invoke-virtual/range {v38 .. v38}, Landroid/view/MotionEvent;->getDownTime()J

    #@244
    move-result-wide v38

    #@245
    sget v40, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->TAP_TIMEOUT:I

    #@247
    move/from16 v0, v40

    #@249
    int-to-long v0, v0

    #@24a
    move-wide/from16 v40, v0

    #@24c
    add-long v38, v38, v40

    #@24e
    invoke-virtual/range {v36 .. v39}, Landroid/os/Handler;->sendEmptyMessageAtTime(IJ)Z

    #@251
    .line 319
    move-object/from16 v0, p0

    #@253
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@255
    move-object/from16 v36, v0

    #@257
    move-object/from16 v0, v36

    #@259
    move-object/from16 v1, p1

    #@25b
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnGestureListener;->onDown(Landroid/view/MotionEvent;)Z

    #@25e
    move-result v36

    #@25f
    or-int v17, v17, v36

    #@261
    .line 320
    goto/16 :goto_5

    #@263
    .line 297
    .restart local v16    # "hadTapMessage":Z
    :cond_d
    move-object/from16 v0, p0

    #@265
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@267
    move-object/from16 v36, v0

    #@269
    const/16 v37, 0x3

    #@26b
    sget v38, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->DOUBLE_TAP_TIMEOUT:I

    #@26d
    move/from16 v0, v38

    #@26f
    int-to-long v0, v0

    #@270
    move-wide/from16 v38, v0

    #@272
    invoke-virtual/range {v36 .. v39}, Landroid/os/Handler;->sendEmptyMessageDelayed(IJ)Z

    #@275
    goto/16 :goto_7

    #@277
    .line 323
    .end local v16    # "hadTapMessage":Z
    :pswitch_4
    move-object/from16 v0, p0

    #@279
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@27b
    move/from16 v36, v0

    #@27d
    if-nez v36, :cond_5

    #@27f
    .line 326
    move-object/from16 v0, p0

    #@281
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@283
    move/from16 v36, v0

    #@285
    sub-float v23, v36, v14

    #@287
    .line 327
    .local v23, "scrollX":F
    move-object/from16 v0, p0

    #@289
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@28b
    move/from16 v36, v0

    #@28d
    sub-float v24, v36, v15

    #@28f
    .line 328
    .local v24, "scrollY":F
    move-object/from16 v0, p0

    #@291
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@293
    move/from16 v36, v0

    #@295
    if-eqz v36, :cond_e

    #@297
    .line 330
    move-object/from16 v0, p0

    #@299
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@29b
    move-object/from16 v36, v0

    #@29d
    move-object/from16 v0, v36

    #@29f
    move-object/from16 v1, p1

    #@2a1
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnDoubleTapListener;->onDoubleTapEvent(Landroid/view/MotionEvent;)Z

    #@2a4
    move-result v36

    #@2a5
    or-int v17, v17, v36

    #@2a7
    goto/16 :goto_5

    #@2a9
    .line 331
    :cond_e
    move-object/from16 v0, p0

    #@2ab
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@2ad
    move/from16 v36, v0

    #@2af
    if-eqz v36, :cond_10

    #@2b1
    .line 332
    move-object/from16 v0, p0

    #@2b3
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusX:F

    #@2b5
    move/from16 v36, v0

    #@2b7
    sub-float v36, v14, v36

    #@2b9
    move/from16 v0, v36

    #@2bb
    float-to-int v9, v0

    #@2bc
    .line 333
    .local v9, "deltaX":I
    move-object/from16 v0, p0

    #@2be
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDownFocusY:F

    #@2c0
    move/from16 v36, v0

    #@2c2
    sub-float v36, v15, v36

    #@2c4
    move/from16 v0, v36

    #@2c6
    float-to-int v10, v0

    #@2c7
    .line 334
    .local v10, "deltaY":I
    mul-int v36, v9, v9

    #@2c9
    mul-int v37, v10, v10

    #@2cb
    add-int v11, v36, v37

    #@2cd
    .line 335
    .local v11, "distance":I
    move-object/from16 v0, p0

    #@2cf
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mTouchSlopSquare:I

    #@2d1
    move/from16 v36, v0

    #@2d3
    move/from16 v0, v36

    #@2d5
    if-le v11, v0, :cond_f

    #@2d7
    .line 336
    move-object/from16 v0, p0

    #@2d9
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@2db
    move-object/from16 v36, v0

    #@2dd
    move-object/from16 v0, p0

    #@2df
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@2e1
    move-object/from16 v37, v0

    #@2e3
    move-object/from16 v0, v36

    #@2e5
    move-object/from16 v1, v37

    #@2e7
    move-object/from16 v2, p1

    #@2e9
    move/from16 v3, v23

    #@2eb
    move/from16 v4, v24

    #@2ed
    invoke-interface {v0, v1, v2, v3, v4}, Landroid/view/GestureDetector$OnGestureListener;->onScroll(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z

    #@2f0
    move-result v17

    #@2f1
    .line 337
    move-object/from16 v0, p0

    #@2f3
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@2f5
    .line 338
    move-object/from16 v0, p0

    #@2f7
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@2f9
    .line 339
    const/16 v36, 0x0

    #@2fb
    move/from16 v0, v36

    #@2fd
    move-object/from16 v1, p0

    #@2ff
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@301
    .line 340
    move-object/from16 v0, p0

    #@303
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@305
    move-object/from16 v36, v0

    #@307
    const/16 v37, 0x3

    #@309
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@30c
    .line 341
    move-object/from16 v0, p0

    #@30e
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@310
    move-object/from16 v36, v0

    #@312
    const/16 v37, 0x1

    #@314
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@317
    .line 342
    move-object/from16 v0, p0

    #@319
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@31b
    move-object/from16 v36, v0

    #@31d
    const/16 v37, 0x2

    #@31f
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@322
    .line 344
    :cond_f
    move-object/from16 v0, p0

    #@324
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mTouchSlopSquare:I

    #@326
    move/from16 v36, v0

    #@328
    move/from16 v0, v36

    #@32a
    if-le v11, v0, :cond_5

    #@32c
    .line 345
    const/16 v36, 0x0

    #@32e
    move/from16 v0, v36

    #@330
    move-object/from16 v1, p0

    #@332
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInBiggerTapRegion:Z

    #@334
    goto/16 :goto_5

    #@336
    .line 347
    .end local v9    # "deltaX":I
    .end local v10    # "deltaY":I
    .end local v11    # "distance":I
    :cond_10
    invoke-static/range {v23 .. v23}, Ljava/lang/Math;->abs(F)F

    #@339
    move-result v36

    #@33a
    const/high16 v37, 0x3f800000    # 1.0f

    #@33c
    cmpl-float v36, v36, v37

    #@33e
    if-gez v36, :cond_11

    #@340
    invoke-static/range {v24 .. v24}, Ljava/lang/Math;->abs(F)F

    #@343
    move-result v36

    #@344
    const/high16 v37, 0x3f800000    # 1.0f

    #@346
    cmpl-float v36, v36, v37

    #@348
    if-ltz v36, :cond_5

    #@34a
    .line 348
    :cond_11
    move-object/from16 v0, p0

    #@34c
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@34e
    move-object/from16 v36, v0

    #@350
    move-object/from16 v0, p0

    #@352
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@354
    move-object/from16 v37, v0

    #@356
    move-object/from16 v0, v36

    #@358
    move-object/from16 v1, v37

    #@35a
    move-object/from16 v2, p1

    #@35c
    move/from16 v3, v23

    #@35e
    move/from16 v4, v24

    #@360
    invoke-interface {v0, v1, v2, v3, v4}, Landroid/view/GestureDetector$OnGestureListener;->onScroll(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z

    #@363
    move-result v17

    #@364
    .line 349
    move-object/from16 v0, p0

    #@366
    iput v14, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusX:F

    #@368
    .line 350
    move-object/from16 v0, p0

    #@36a
    iput v15, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mLastFocusY:F

    #@36c
    goto/16 :goto_5

    #@36e
    .line 355
    .end local v23    # "scrollX":F
    .end local v24    # "scrollY":F
    :pswitch_5
    const/16 v36, 0x0

    #@370
    move/from16 v0, v36

    #@372
    move-object/from16 v1, p0

    #@374
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mStillDown:Z

    #@376
    .line 356
    invoke-static/range {p1 .. p1}, Landroid/view/MotionEvent;->obtain(Landroid/view/MotionEvent;)Landroid/view/MotionEvent;

    #@379
    move-result-object v8

    #@37a
    .line 357
    .local v8, "currentUpEvent":Landroid/view/MotionEvent;
    move-object/from16 v0, p0

    #@37c
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@37e
    move/from16 v36, v0

    #@380
    if-eqz v36, :cond_15

    #@382
    .line 359
    move-object/from16 v0, p0

    #@384
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@386
    move-object/from16 v36, v0

    #@388
    move-object/from16 v0, v36

    #@38a
    move-object/from16 v1, p1

    #@38c
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnDoubleTapListener;->onDoubleTapEvent(Landroid/view/MotionEvent;)Z

    #@38f
    move-result v36

    #@390
    or-int v17, v17, v36

    #@392
    .line 383
    :cond_12
    :goto_8
    move-object/from16 v0, p0

    #@394
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mPreviousUpEvent:Landroid/view/MotionEvent;

    #@396
    move-object/from16 v36, v0

    #@398
    if-eqz v36, :cond_13

    #@39a
    .line 384
    move-object/from16 v0, p0

    #@39c
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mPreviousUpEvent:Landroid/view/MotionEvent;

    #@39e
    move-object/from16 v36, v0

    #@3a0
    invoke-virtual/range {v36 .. v36}, Landroid/view/MotionEvent;->recycle()V

    #@3a3
    .line 387
    :cond_13
    move-object/from16 v0, p0

    #@3a5
    iput-object v8, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mPreviousUpEvent:Landroid/view/MotionEvent;

    #@3a7
    .line 388
    move-object/from16 v0, p0

    #@3a9
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@3ab
    move-object/from16 v36, v0

    #@3ad
    if-eqz v36, :cond_14

    #@3af
    .line 391
    move-object/from16 v0, p0

    #@3b1
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@3b3
    move-object/from16 v36, v0

    #@3b5
    invoke-virtual/range {v36 .. v36}, Landroid/view/VelocityTracker;->recycle()V

    #@3b8
    .line 392
    const/16 v36, 0x0

    #@3ba
    move-object/from16 v0, v36

    #@3bc
    move-object/from16 v1, p0

    #@3be
    iput-object v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@3c0
    .line 394
    :cond_14
    const/16 v36, 0x0

    #@3c2
    move/from16 v0, v36

    #@3c4
    move-object/from16 v1, p0

    #@3c6
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsDoubleTapping:Z

    #@3c8
    .line 395
    const/16 v36, 0x0

    #@3ca
    move/from16 v0, v36

    #@3cc
    move-object/from16 v1, p0

    #@3ce
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@3d0
    .line 396
    move-object/from16 v0, p0

    #@3d2
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@3d4
    move-object/from16 v36, v0

    #@3d6
    const/16 v37, 0x1

    #@3d8
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@3db
    .line 397
    move-object/from16 v0, p0

    #@3dd
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@3df
    move-object/from16 v36, v0

    #@3e1
    const/16 v37, 0x2

    #@3e3
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@3e6
    goto/16 :goto_5

    #@3e8
    .line 360
    :cond_15
    move-object/from16 v0, p0

    #@3ea
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@3ec
    move/from16 v36, v0

    #@3ee
    if-eqz v36, :cond_16

    #@3f0
    .line 361
    move-object/from16 v0, p0

    #@3f2
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mHandler:Landroid/os/Handler;

    #@3f4
    move-object/from16 v36, v0

    #@3f6
    const/16 v37, 0x3

    #@3f8
    invoke-virtual/range {v36 .. v37}, Landroid/os/Handler;->removeMessages(I)V

    #@3fb
    .line 362
    const/16 v36, 0x0

    #@3fd
    move/from16 v0, v36

    #@3ff
    move-object/from16 v1, p0

    #@401
    iput-boolean v0, v1, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mInLongPress:Z

    #@403
    goto :goto_8

    #@404
    .line 363
    :cond_16
    move-object/from16 v0, p0

    #@406
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mAlwaysInTapRegion:Z

    #@408
    move/from16 v36, v0

    #@40a
    if-eqz v36, :cond_17

    #@40c
    .line 364
    move-object/from16 v0, p0

    #@40e
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@410
    move-object/from16 v36, v0

    #@412
    move-object/from16 v0, v36

    #@414
    move-object/from16 v1, p1

    #@416
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnGestureListener;->onSingleTapUp(Landroid/view/MotionEvent;)Z

    #@419
    move-result v17

    #@41a
    .line 365
    move-object/from16 v0, p0

    #@41c
    iget-boolean v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDeferConfirmSingleTap:Z

    #@41e
    move/from16 v36, v0

    #@420
    if-eqz v36, :cond_12

    #@422
    move-object/from16 v0, p0

    #@424
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@426
    move-object/from16 v36, v0

    #@428
    if-eqz v36, :cond_12

    #@42a
    .line 366
    move-object/from16 v0, p0

    #@42c
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@42e
    move-object/from16 v36, v0

    #@430
    move-object/from16 v0, v36

    #@432
    move-object/from16 v1, p1

    #@434
    invoke-interface {v0, v1}, Landroid/view/GestureDetector$OnDoubleTapListener;->onSingleTapConfirmed(Landroid/view/MotionEvent;)Z

    #@437
    goto/16 :goto_8

    #@439
    .line 370
    :cond_17
    move-object/from16 v0, p0

    #@43b
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@43d
    move-object/from16 v29, v0

    #@43f
    .line 371
    .local v29, "velocityTracker":Landroid/view/VelocityTracker;
    const/16 v36, 0x0

    #@441
    move-object/from16 v0, p1

    #@443
    move/from16 v1, v36

    #@445
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@448
    move-result v21

    #@449
    .line 372
    .local v21, "pointerId":I
    const/16 v36, 0x3e8

    #@44b
    move-object/from16 v0, p0

    #@44d
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMaximumFlingVelocity:I

    #@44f
    move/from16 v37, v0

    #@451
    move/from16 v0, v37

    #@453
    int-to-float v0, v0

    #@454
    move/from16 v37, v0

    #@456
    move-object/from16 v0, v29

    #@458
    move/from16 v1, v36

    #@45a
    move/from16 v2, v37

    #@45c
    invoke-virtual {v0, v1, v2}, Landroid/view/VelocityTracker;->computeCurrentVelocity(IF)V

    #@45f
    .line 373
    move-object/from16 v0, v29

    #@461
    move/from16 v1, v21

    #@463
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getYVelocity(Landroid/view/VelocityTracker;I)F

    #@466
    move-result v31

    #@467
    .line 375
    .local v31, "velocityY":F
    move-object/from16 v0, v29

    #@469
    move/from16 v1, v21

    #@46b
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@46e
    move-result v30

    #@46f
    .line 378
    .local v30, "velocityX":F
    invoke-static/range {v31 .. v31}, Ljava/lang/Math;->abs(F)F

    #@472
    move-result v36

    #@473
    move-object/from16 v0, p0

    #@475
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMinimumFlingVelocity:I

    #@477
    move/from16 v37, v0

    #@479
    move/from16 v0, v37

    #@47b
    int-to-float v0, v0

    #@47c
    move/from16 v37, v0

    #@47e
    cmpl-float v36, v36, v37

    #@480
    if-gtz v36, :cond_18

    #@482
    invoke-static/range {v30 .. v30}, Ljava/lang/Math;->abs(F)F

    #@485
    move-result v36

    #@486
    move-object/from16 v0, p0

    #@488
    iget v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mMinimumFlingVelocity:I

    #@48a
    move/from16 v37, v0

    #@48c
    move/from16 v0, v37

    #@48e
    int-to-float v0, v0

    #@48f
    move/from16 v37, v0

    #@491
    cmpl-float v36, v36, v37

    #@493
    if-lez v36, :cond_12

    #@495
    .line 380
    :cond_18
    move-object/from16 v0, p0

    #@497
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mListener:Landroid/view/GestureDetector$OnGestureListener;

    #@499
    move-object/from16 v36, v0

    #@49b
    move-object/from16 v0, p0

    #@49d
    iget-object v0, v0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mCurrentDownEvent:Landroid/view/MotionEvent;

    #@49f
    move-object/from16 v37, v0

    #@4a1
    move-object/from16 v0, v36

    #@4a3
    move-object/from16 v1, v37

    #@4a5
    move-object/from16 v2, p1

    #@4a7
    move/from16 v3, v30

    #@4a9
    move/from16 v4, v31

    #@4ab
    invoke-interface {v0, v1, v2, v3, v4}, Landroid/view/GestureDetector$OnGestureListener;->onFling(Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z

    #@4ae
    move-result v17

    #@4af
    goto/16 :goto_8

    #@4b1
    .line 401
    .end local v8    # "currentUpEvent":Landroid/view/MotionEvent;
    .end local v21    # "pointerId":I
    .end local v29    # "velocityTracker":Landroid/view/VelocityTracker;
    .end local v30    # "velocityX":F
    .end local v31    # "velocityY":F
    :pswitch_6
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->cancel()V

    #@4b4
    goto/16 :goto_5

    #@4b6
    .line 249
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_3
        :pswitch_5
        :pswitch_4
        :pswitch_6
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public setIsLongpressEnabled(Z)V
    .locals 0
    .param p1, "isLongpressEnabled"    # Z

    #@0
    .prologue
    .line 205
    iput-boolean p1, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mIsLongpressEnabled:Z

    #@2
    .line 206
    return-void
.end method

.method public setOnDoubleTapListener(Landroid/view/GestureDetector$OnDoubleTapListener;)V
    .locals 0
    .param p1, "onDoubleTapListener"    # Landroid/view/GestureDetector$OnDoubleTapListener;

    #@0
    .prologue
    .line 192
    iput-object p1, p0, Landroid/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase;->mDoubleTapListener:Landroid/view/GestureDetector$OnDoubleTapListener;

    #@2
    .line 193
    return-void
.end method
