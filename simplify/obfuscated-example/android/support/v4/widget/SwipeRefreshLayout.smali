.class public Landroid/support/v4/widget/SwipeRefreshLayout;
.super Landroid/view/ViewGroup;
.source "SwipeRefreshLayout.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;
    }
.end annotation


# static fields
.field private static final ALPHA_ANIMATION_DURATION:I = 0x12c

.field private static final ANIMATE_TO_START_DURATION:I = 0xc8

.field private static final ANIMATE_TO_TRIGGER_DURATION:I = 0xc8

.field private static final CIRCLE_BG_LIGHT:I = -0x50506

.field private static final CIRCLE_DIAMETER:I = 0x28

.field private static final CIRCLE_DIAMETER_LARGE:I = 0x38

.field private static final DECELERATE_INTERPOLATION_FACTOR:F = 2.0f

.field public static final DEFAULT:I = 0x1

.field private static final DEFAULT_CIRCLE_TARGET:I = 0x40

.field private static final DRAG_RATE:F = 0.5f

.field private static final INVALID_POINTER:I = -0x1

.field public static final LARGE:I = 0x0

.field private static final LAYOUT_ATTRS:[I

.field private static final LOG_TAG:Ljava/lang/String;

.field private static final MAX_ALPHA:I = 0xff

.field private static final MAX_PROGRESS_ANGLE:F = 0.8f

.field private static final SCALE_DOWN_DURATION:I = 0x96

.field private static final STARTING_PROGRESS_ALPHA:I = 0x4c


# instance fields
.field private mActivePointerId:I

.field private mAlphaMaxAnimation:Landroid/view/animation/Animation;

.field private mAlphaStartAnimation:Landroid/view/animation/Animation;

.field private final mAnimateToCorrectPosition:Landroid/view/animation/Animation;

.field private final mAnimateToStartPosition:Landroid/view/animation/Animation;

.field private mCircleHeight:I

.field private mCircleView:Landroid/support/v4/widget/CircleImageView;

.field private mCircleViewIndex:I

.field private mCircleWidth:I

.field private mCurrentTargetOffsetTop:I

.field private final mDecelerateInterpolator:Landroid/view/animation/DecelerateInterpolator;

.field protected mFrom:I

.field private mInitialMotionY:F

.field private mIsBeingDragged:Z

.field private mListener:Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;

.field private mMediumAnimationDuration:I

.field private mNotify:Z

.field private mOriginalOffsetCalculated:Z

.field protected mOriginalOffsetTop:I

.field private mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

.field private mRefreshListener:Landroid/view/animation/Animation$AnimationListener;

.field private mRefreshing:Z

.field private mReturningToStart:Z

.field private mScale:Z

.field private mScaleAnimation:Landroid/view/animation/Animation;

.field private mScaleDownAnimation:Landroid/view/animation/Animation;

.field private mScaleDownToStartAnimation:Landroid/view/animation/Animation;

.field private mSpinnerFinalOffset:F

.field private mStartingScale:F

.field private mTarget:Landroid/view/View;

.field private mTotalDragDistance:F

.field private mTouchSlop:I

.field private mUsingCustomStart:Z


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 64
    const-class v0, Landroid/support/v4/widget/SwipeRefreshLayout;

    #@2
    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    sput-object v0, Landroid/support/v4/widget/SwipeRefreshLayout;->LOG_TAG:Ljava/lang/String;

    #@8
    .line 113
    const/4 v0, 0x1

    #@9
    new-array v0, v0, [I

    #@b
    const/4 v1, 0x0

    #@c
    const v2, 0x101000e

    #@f
    aput v2, v0, v1

    #@11
    sput-object v0, Landroid/support/v4/widget/SwipeRefreshLayout;->LAYOUT_ATTRS:[I

    #@13
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 258
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 259
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 7
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    const/4 v3, -0x1

    #@2
    const/high16 v5, 0x42200000    # 40.0f

    #@4
    const/4 v4, 0x0

    #@5
    .line 268
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@8
    .line 95
    iput-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@a
    .line 97
    const/high16 v2, -0x40800000    # -1.0f

    #@c
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@e
    .line 101
    iput-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetCalculated:Z

    #@10
    .line 105
    iput v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@12
    .line 118
    iput v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@14
    .line 149
    new-instance v2, Landroid/support/v4/widget/SwipeRefreshLayout$1;

    #@16
    invoke-direct {v2, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$1;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@19
    iput-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshListener:Landroid/view/animation/Animation$AnimationListener;

    #@1b
    .line 842
    new-instance v2, Landroid/support/v4/widget/SwipeRefreshLayout$6;

    #@1d
    invoke-direct {v2, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$6;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@20
    iput-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToCorrectPosition:Landroid/view/animation/Animation;

    #@22
    .line 865
    new-instance v2, Landroid/support/v4/widget/SwipeRefreshLayout$7;

    #@24
    invoke-direct {v2, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$7;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@27
    iput-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToStartPosition:Landroid/view/animation/Animation;

    #@29
    .line 270
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@2c
    move-result-object v2

    #@2d
    invoke-virtual {v2}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@30
    move-result v2

    #@31
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTouchSlop:I

    #@33
    .line 272
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getResources()Landroid/content/res/Resources;

    #@36
    move-result-object v2

    #@37
    const v3, 0x10e0001

    #@3a
    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getInteger(I)I

    #@3d
    move-result v2

    #@3e
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mMediumAnimationDuration:I

    #@40
    .line 275
    invoke-virtual {p0, v4}, Landroid/support/v4/widget/SwipeRefreshLayout;->setWillNotDraw(Z)V

    #@43
    .line 276
    new-instance v2, Landroid/view/animation/DecelerateInterpolator;

    #@45
    const/high16 v3, 0x40000000    # 2.0f

    #@47
    invoke-direct {v2, v3}, Landroid/view/animation/DecelerateInterpolator;-><init>(F)V

    #@4a
    iput-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mDecelerateInterpolator:Landroid/view/animation/DecelerateInterpolator;

    #@4c
    .line 278
    sget-object v2, Landroid/support/v4/widget/SwipeRefreshLayout;->LAYOUT_ATTRS:[I

    #@4e
    invoke-virtual {p1, p2, v2}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@51
    move-result-object v0

    #@52
    .line 279
    .local v0, "a":Landroid/content/res/TypedArray;
    invoke-virtual {v0, v4, v6}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@55
    move-result v2

    #@56
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setEnabled(Z)V

    #@59
    .line 280
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@5c
    .line 282
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getResources()Landroid/content/res/Resources;

    #@5f
    move-result-object v2

    #@60
    invoke-virtual {v2}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@63
    move-result-object v1

    #@64
    .line 283
    .local v1, "metrics":Landroid/util/DisplayMetrics;
    iget v2, v1, Landroid/util/DisplayMetrics;->density:F

    #@66
    mul-float/2addr v2, v5

    #@67
    float-to-int v2, v2

    #@68
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleWidth:I

    #@6a
    .line 284
    iget v2, v1, Landroid/util/DisplayMetrics;->density:F

    #@6c
    mul-float/2addr v2, v5

    #@6d
    float-to-int v2, v2

    #@6e
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleHeight:I

    #@70
    .line 286
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->createProgressView()V

    #@73
    .line 287
    invoke-static {p0, v6}, Landroid/support/v4/view/ViewCompat;->setChildrenDrawingOrderEnabled(Landroid/view/ViewGroup;Z)V

    #@76
    .line 289
    const/high16 v2, 0x42800000    # 64.0f

    #@78
    iget v3, v1, Landroid/util/DisplayMetrics;->density:F

    #@7a
    mul-float/2addr v2, v3

    #@7b
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@7d
    .line 290
    iget v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@7f
    iput v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@81
    .line 291
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/widget/SwipeRefreshLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@2
    return v0
.end method

.method static synthetic access$100(Landroid/support/v4/widget/SwipeRefreshLayout;)Landroid/support/v4/widget/MaterialProgressDrawable;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2
    return-object v0
.end method

.method static synthetic access$1000(Landroid/support/v4/widget/SwipeRefreshLayout;Landroid/view/animation/Animation$AnimationListener;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 58
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->startScaleDownAnimation(Landroid/view/animation/Animation$AnimationListener;)V

    #@3
    return-void
.end method

.method static synthetic access$1100(Landroid/support/v4/widget/SwipeRefreshLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mUsingCustomStart:Z

    #@2
    return v0
.end method

.method static synthetic access$1200(Landroid/support/v4/widget/SwipeRefreshLayout;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@2
    return v0
.end method

.method static synthetic access$1300(Landroid/support/v4/widget/SwipeRefreshLayout;F)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # F

    #@0
    .prologue
    .line 58
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->moveToStart(F)V

    #@3
    return-void
.end method

.method static synthetic access$1400(Landroid/support/v4/widget/SwipeRefreshLayout;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mStartingScale:F

    #@2
    return v0
.end method

.method static synthetic access$200(Landroid/support/v4/widget/SwipeRefreshLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mNotify:Z

    #@2
    return v0
.end method

.method static synthetic access$300(Landroid/support/v4/widget/SwipeRefreshLayout;)Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mListener:Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v4/widget/SwipeRefreshLayout;)Landroid/support/v4/widget/CircleImageView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v4/widget/SwipeRefreshLayout;I)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 58
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->setColorViewAlpha(I)V

    #@3
    return-void
.end method

.method static synthetic access$600(Landroid/support/v4/widget/SwipeRefreshLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@2
    return v0
.end method

.method static synthetic access$700(Landroid/support/v4/widget/SwipeRefreshLayout;F)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # F

    #@0
    .prologue
    .line 58
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->setAnimationProgress(F)V

    #@3
    return-void
.end method

.method static synthetic access$800(Landroid/support/v4/widget/SwipeRefreshLayout;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;

    #@0
    .prologue
    .line 58
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@2
    return v0
.end method

.method static synthetic access$802(Landroid/support/v4/widget/SwipeRefreshLayout;I)I
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 58
    iput p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@2
    return p1
.end method

.method static synthetic access$900(Landroid/support/v4/widget/SwipeRefreshLayout;IZ)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SwipeRefreshLayout;
    .param p1, "x1"    # I
    .param p2, "x2"    # Z

    #@0
    .prologue
    .line 58
    invoke-direct {p0, p1, p2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setTargetOffsetTopAndBottom(IZ)V

    #@3
    return-void
.end method

.method private animateOffsetToCorrectPosition(ILandroid/view/animation/Animation$AnimationListener;)V
    .locals 4
    .param p1, "from"    # I
    .param p2, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 814
    iput p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mFrom:I

    #@2
    .line 815
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToCorrectPosition:Landroid/view/animation/Animation;

    #@4
    invoke-virtual {v0}, Landroid/view/animation/Animation;->reset()V

    #@7
    .line 816
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToCorrectPosition:Landroid/view/animation/Animation;

    #@9
    const-wide/16 v2, 0xc8

    #@b
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@e
    .line 817
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToCorrectPosition:Landroid/view/animation/Animation;

    #@10
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mDecelerateInterpolator:Landroid/view/animation/DecelerateInterpolator;

    #@12
    invoke-virtual {v0, v1}, Landroid/view/animation/Animation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@15
    .line 818
    if-eqz p2, :cond_0

    #@17
    .line 819
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@19
    invoke-virtual {v0, p2}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@1c
    .line 821
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@1e
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@21
    .line 822
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@23
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToCorrectPosition:Landroid/view/animation/Animation;

    #@25
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@28
    .line 823
    return-void
.end method

.method private animateOffsetToStartPosition(ILandroid/view/animation/Animation$AnimationListener;)V
    .locals 4
    .param p1, "from"    # I
    .param p2, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 826
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 828
    invoke-direct {p0, p1, p2}, Landroid/support/v4/widget/SwipeRefreshLayout;->startScaleDownReturnToStartAnimation(ILandroid/view/animation/Animation$AnimationListener;)V

    #@7
    .line 840
    :goto_0
    return-void

    #@8
    .line 830
    :cond_0
    iput p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mFrom:I

    #@a
    .line 831
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToStartPosition:Landroid/view/animation/Animation;

    #@c
    invoke-virtual {v0}, Landroid/view/animation/Animation;->reset()V

    #@f
    .line 832
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToStartPosition:Landroid/view/animation/Animation;

    #@11
    const-wide/16 v2, 0xc8

    #@13
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@16
    .line 833
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToStartPosition:Landroid/view/animation/Animation;

    #@18
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mDecelerateInterpolator:Landroid/view/animation/DecelerateInterpolator;

    #@1a
    invoke-virtual {v0, v1}, Landroid/view/animation/Animation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@1d
    .line 834
    if-eqz p2, :cond_1

    #@1f
    .line 835
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@21
    invoke-virtual {v0, p2}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@24
    .line 837
    :cond_1
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@26
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@29
    .line 838
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2b
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAnimateToStartPosition:Landroid/view/animation/Animation;

    #@2d
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@30
    goto :goto_0
.end method

.method private createProgressView()V
    .locals 4

    #@0
    .prologue
    const v3, -0x50506

    #@3
    .line 309
    new-instance v0, Landroid/support/v4/widget/CircleImageView;

    #@5
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getContext()Landroid/content/Context;

    #@8
    move-result-object v1

    #@9
    const/high16 v2, 0x41a00000    # 20.0f

    #@b
    invoke-direct {v0, v1, v3, v2}, Landroid/support/v4/widget/CircleImageView;-><init>(Landroid/content/Context;IF)V

    #@e
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@10
    .line 310
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable;

    #@12
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getContext()Landroid/content/Context;

    #@15
    move-result-object v1

    #@16
    invoke-direct {v0, v1, p0}, Landroid/support/v4/widget/MaterialProgressDrawable;-><init>(Landroid/content/Context;Landroid/view/View;)V

    #@19
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@1b
    .line 311
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@1d
    invoke-virtual {v0, v3}, Landroid/support/v4/widget/MaterialProgressDrawable;->setBackgroundColor(I)V

    #@20
    .line 312
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@22
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@24
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@27
    .line 313
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@29
    const/16 v1, 0x8

    #@2b
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->setVisibility(I)V

    #@2e
    .line 314
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@30
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;->addView(Landroid/view/View;)V

    #@33
    .line 315
    return-void
.end method

.method private ensureTarget()V
    .locals 3

    #@0
    .prologue
    .line 505
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@2
    if-nez v2, :cond_0

    #@4
    .line 506
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getChildCount()I

    #@8
    move-result v2

    #@9
    if-ge v1, v2, :cond_0

    #@b
    .line 507
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->getChildAt(I)Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    .line 508
    .local v0, "child":Landroid/view/View;
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@11
    invoke-virtual {v0, v2}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@14
    move-result v2

    #@15
    if-nez v2, :cond_1

    #@17
    .line 509
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@19
    .line 514
    .end local v0    # "child":Landroid/view/View;
    .end local v1    # "i":I
    :cond_0
    return-void

    #@1a
    .line 506
    .restart local v0    # "child":Landroid/view/View;
    .restart local v1    # "i":I
    :cond_1
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0
.end method

.method private getMotionEventY(Landroid/view/MotionEvent;I)F
    .locals 2
    .param p1, "ev"    # Landroid/view/MotionEvent;
    .param p2, "activePointerId"    # I

    #@0
    .prologue
    .line 656
    invoke-static {p1, p2}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@3
    move-result v0

    #@4
    .line 657
    .local v0, "index":I
    if-gez v0, :cond_0

    #@6
    .line 658
    const/high16 v1, -0x40800000    # -1.0f

    #@8
    .line 660
    :goto_0
    return v1

    #@9
    :cond_0
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@c
    move-result v1

    #@d
    goto :goto_0
.end method

.method private isAlphaUsedForScale()Z
    .locals 2

    #@0
    .prologue
    .line 329
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xb

    #@4
    if-ge v0, v1, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method private isAnimationRunning(Landroid/view/animation/Animation;)Z
    .locals 1
    .param p1, "animation"    # Landroid/view/animation/Animation;

    #@0
    .prologue
    .line 669
    if-eqz p1, :cond_0

    #@2
    invoke-virtual {p1}, Landroid/view/animation/Animation;->hasStarted()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    invoke-virtual {p1}, Landroid/view/animation/Animation;->hasEnded()Z

    #@b
    move-result v0

    #@c
    if-nez v0, :cond_0

    #@e
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_0
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method private moveToStart(F)V
    .locals 5
    .param p1, "interpolatedTime"    # F

    #@0
    .prologue
    .line 859
    const/4 v1, 0x0

    #@1
    .line 860
    .local v1, "targetTop":I
    iget v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mFrom:I

    #@3
    iget v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@5
    iget v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mFrom:I

    #@7
    sub-int/2addr v3, v4

    #@8
    int-to-float v3, v3

    #@9
    mul-float/2addr v3, p1

    #@a
    float-to-int v3, v3

    #@b
    add-int v1, v2, v3

    #@d
    .line 861
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@f
    invoke-virtual {v2}, Landroid/support/v4/widget/CircleImageView;->getTop()I

    #@12
    move-result v2

    #@13
    sub-int v0, v1, v2

    #@15
    .line 862
    .local v0, "offset":I
    const/4 v2, 0x0

    #@16
    invoke-direct {p0, v0, v2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setTargetOffsetTopAndBottom(IZ)V

    #@19
    .line 863
    return-void
.end method

.method private onSecondaryPointerUp(Landroid/view/MotionEvent;)V
    .locals 4
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 906
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@3
    move-result v2

    #@4
    .line 907
    .local v2, "pointerIndex":I
    invoke-static {p1, v2}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@7
    move-result v1

    #@8
    .line 908
    .local v1, "pointerId":I
    iget v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@a
    if-ne v1, v3, :cond_0

    #@c
    .line 911
    if-nez v2, :cond_1

    #@e
    const/4 v0, 0x1

    #@f
    .line 912
    .local v0, "newPointerIndex":I
    :goto_0
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@12
    move-result v3

    #@13
    iput v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@15
    .line 914
    .end local v0    # "newPointerIndex":I
    :cond_0
    return-void

    #@16
    .line 911
    :cond_1
    const/4 v0, 0x0

    #@17
    goto :goto_0
.end method

.method private setAnimationProgress(F)V
    .locals 1
    .param p1, "progress"    # F

    #@0
    .prologue
    .line 384
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->isAlphaUsedForScale()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 385
    const/high16 v0, 0x437f0000    # 255.0f

    #@8
    mul-float/2addr v0, p1

    #@9
    float-to-int v0, v0

    #@a
    invoke-direct {p0, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;->setColorViewAlpha(I)V

    #@d
    .line 390
    :goto_0
    return-void

    #@e
    .line 387
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@10
    invoke-static {v0, p1}, Landroid/support/v4/view/ViewCompat;->setScaleX(Landroid/view/View;F)V

    #@13
    .line 388
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@15
    invoke-static {v0, p1}, Landroid/support/v4/view/ViewCompat;->setScaleY(Landroid/view/View;F)V

    #@18
    goto :goto_0
.end method

.method private setColorViewAlpha(I)V
    .locals 1
    .param p1, "targetAlpha"    # I

    #@0
    .prologue
    .line 186
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->getBackground()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    #@9
    .line 187
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@b
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable;->setAlpha(I)V

    #@e
    .line 188
    return-void
.end method

.method private setRefreshing(ZZ)V
    .locals 2
    .param p1, "refreshing"    # Z
    .param p2, "notify"    # Z

    #@0
    .prologue
    .line 393
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 394
    iput-boolean p2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mNotify:Z

    #@6
    .line 395
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->ensureTarget()V

    #@9
    .line 396
    iput-boolean p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@b
    .line 397
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@d
    if-eqz v0, :cond_1

    #@f
    .line 398
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@11
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshListener:Landroid/view/animation/Animation$AnimationListener;

    #@13
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->animateOffsetToCorrectPosition(ILandroid/view/animation/Animation$AnimationListener;)V

    #@16
    .line 403
    :cond_0
    :goto_0
    return-void

    #@17
    .line 400
    :cond_1
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshListener:Landroid/view/animation/Animation$AnimationListener;

    #@19
    invoke-direct {p0, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;->startScaleDownAnimation(Landroid/view/animation/Animation$AnimationListener;)V

    #@1c
    goto :goto_0
.end method

.method private setTargetOffsetTopAndBottom(IZ)V
    .locals 2
    .param p1, "offset"    # I
    .param p2, "requiresUpdate"    # Z

    #@0
    .prologue
    .line 897
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->bringToFront()V

    #@5
    .line 898
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@7
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/CircleImageView;->offsetTopAndBottom(I)V

    #@a
    .line 899
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@c
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->getTop()I

    #@f
    move-result v0

    #@10
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@12
    .line 900
    if-eqz p2, :cond_0

    #@14
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@16
    const/16 v1, 0xb

    #@18
    if-ge v0, v1, :cond_0

    #@1a
    .line 901
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->invalidate()V

    #@1d
    .line 903
    :cond_0
    return-void
.end method

.method private startAlphaAnimation(II)Landroid/view/animation/Animation;
    .locals 4
    .param p1, "startingAlpha"    # I
    .param p2, "endingAlpha"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 429
    iget-boolean v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@3
    if-eqz v2, :cond_0

    #@5
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->isAlphaUsedForScale()Z

    #@8
    move-result v2

    #@9
    if-eqz v2, :cond_0

    #@b
    move-object v0, v1

    #@c
    .line 445
    :goto_0
    return-object v0

    #@d
    .line 432
    :cond_0
    new-instance v0, Landroid/support/v4/widget/SwipeRefreshLayout$4;

    #@f
    invoke-direct {v0, p0, p1, p2}, Landroid/support/v4/widget/SwipeRefreshLayout$4;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;II)V

    #@12
    .line 440
    .local v0, "alpha":Landroid/view/animation/Animation;
    const-wide/16 v2, 0x12c

    #@14
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@17
    .line 442
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@19
    invoke-virtual {v2, v1}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@1c
    .line 443
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@1e
    invoke-virtual {v1}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@21
    .line 444
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@23
    invoke-virtual {v1, v0}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@26
    goto :goto_0
.end method

.method private startProgressAlphaMaxAnimation()V
    .locals 2

    #@0
    .prologue
    .line 423
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable;->getAlpha()I

    #@5
    move-result v0

    #@6
    const/16 v1, 0xff

    #@8
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->startAlphaAnimation(II)Landroid/view/animation/Animation;

    #@b
    move-result-object v0

    #@c
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAlphaMaxAnimation:Landroid/view/animation/Animation;

    #@e
    .line 424
    return-void
.end method

.method private startProgressAlphaStartAnimation()V
    .locals 2

    #@0
    .prologue
    .line 419
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable;->getAlpha()I

    #@5
    move-result v0

    #@6
    const/16 v1, 0x4c

    #@8
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->startAlphaAnimation(II)Landroid/view/animation/Animation;

    #@b
    move-result-object v0

    #@c
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAlphaStartAnimation:Landroid/view/animation/Animation;

    #@e
    .line 420
    return-void
.end method

.method private startScaleDownAnimation(Landroid/view/animation/Animation$AnimationListener;)V
    .locals 4
    .param p1, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 406
    new-instance v0, Landroid/support/v4/widget/SwipeRefreshLayout$3;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$3;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@5
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownAnimation:Landroid/view/animation/Animation;

    #@7
    .line 412
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownAnimation:Landroid/view/animation/Animation;

    #@9
    const-wide/16 v2, 0x96

    #@b
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@e
    .line 413
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@10
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@13
    .line 414
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@15
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@18
    .line 415
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@1a
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownAnimation:Landroid/view/animation/Animation;

    #@1c
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@1f
    .line 416
    return-void
.end method

.method private startScaleDownReturnToStartAnimation(ILandroid/view/animation/Animation$AnimationListener;)V
    .locals 4
    .param p1, "from"    # I
    .param p2, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 874
    iput p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mFrom:I

    #@2
    .line 875
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->isAlphaUsedForScale()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_1

    #@8
    .line 876
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@a
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable;->getAlpha()I

    #@d
    move-result v0

    #@e
    int-to-float v0, v0

    #@f
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mStartingScale:F

    #@11
    .line 880
    :goto_0
    new-instance v0, Landroid/support/v4/widget/SwipeRefreshLayout$8;

    #@13
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$8;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@16
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownToStartAnimation:Landroid/view/animation/Animation;

    #@18
    .line 888
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownToStartAnimation:Landroid/view/animation/Animation;

    #@1a
    const-wide/16 v2, 0x96

    #@1c
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@1f
    .line 889
    if-eqz p2, :cond_0

    #@21
    .line 890
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@23
    invoke-virtual {v0, p2}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@26
    .line 892
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@28
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@2b
    .line 893
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2d
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleDownToStartAnimation:Landroid/view/animation/Animation;

    #@2f
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@32
    .line 894
    return-void

    #@33
    .line 878
    :cond_1
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@35
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->getScaleX(Landroid/view/View;)F

    #@38
    move-result v0

    #@39
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mStartingScale:F

    #@3b
    goto :goto_0
.end method

.method private startScaleUpAnimation(Landroid/view/animation/Animation$AnimationListener;)V
    .locals 4
    .param p1, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 358
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->setVisibility(I)V

    #@6
    .line 359
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@8
    const/16 v1, 0xb

    #@a
    if-lt v0, v1, :cond_0

    #@c
    .line 363
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@e
    const/16 v1, 0xff

    #@10
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable;->setAlpha(I)V

    #@13
    .line 365
    :cond_0
    new-instance v0, Landroid/support/v4/widget/SwipeRefreshLayout$2;

    #@15
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SwipeRefreshLayout$2;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@18
    iput-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleAnimation:Landroid/view/animation/Animation;

    #@1a
    .line 371
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleAnimation:Landroid/view/animation/Animation;

    #@1c
    iget v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mMediumAnimationDuration:I

    #@1e
    int-to-long v2, v1

    #@1f
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@22
    .line 372
    if-eqz p1, :cond_1

    #@24
    .line 373
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@26
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/CircleImageView;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@29
    .line 375
    :cond_1
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2b
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->clearAnimation()V

    #@2e
    .line 376
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@30
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScaleAnimation:Landroid/view/animation/Animation;

    #@32
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->startAnimation(Landroid/view/animation/Animation;)V

    #@35
    .line 377
    return-void
.end method


# virtual methods
.method public canChildScrollUp()Z
    .locals 5

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 584
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    const/16 v4, 0xe

    #@6
    if-ge v3, v4, :cond_3

    #@8
    .line 585
    iget-object v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@a
    instance-of v3, v3, Landroid/widget/AbsListView;

    #@c
    if-eqz v3, :cond_2

    #@e
    .line 586
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@10
    check-cast v0, Landroid/widget/AbsListView;

    #@12
    .line 587
    .local v0, "absListView":Landroid/widget/AbsListView;
    invoke-virtual {v0}, Landroid/widget/AbsListView;->getChildCount()I

    #@15
    move-result v3

    #@16
    if-lez v3, :cond_1

    #@18
    invoke-virtual {v0}, Landroid/widget/AbsListView;->getFirstVisiblePosition()I

    #@1b
    move-result v3

    #@1c
    if-gtz v3, :cond_0

    #@1e
    invoke-virtual {v0, v2}, Landroid/widget/AbsListView;->getChildAt(I)Landroid/view/View;

    #@21
    move-result-object v3

    #@22
    invoke-virtual {v3}, Landroid/view/View;->getTop()I

    #@25
    move-result v3

    #@26
    invoke-virtual {v0}, Landroid/widget/AbsListView;->getPaddingTop()I

    #@29
    move-result v4

    #@2a
    if-ge v3, v4, :cond_1

    #@2c
    .line 594
    .end local v0    # "absListView":Landroid/widget/AbsListView;
    :cond_0
    :goto_0
    return v1

    #@2d
    .restart local v0    # "absListView":Landroid/widget/AbsListView;
    :cond_1
    move v1, v2

    #@2e
    .line 587
    goto :goto_0

    #@2f
    .line 591
    .end local v0    # "absListView":Landroid/widget/AbsListView;
    :cond_2
    iget-object v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@31
    invoke-virtual {v3}, Landroid/view/View;->getScrollY()I

    #@34
    move-result v3

    #@35
    if-gtz v3, :cond_0

    #@37
    move v1, v2

    #@38
    goto :goto_0

    #@39
    .line 594
    :cond_3
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@3b
    const/4 v2, -0x1

    #@3c
    invoke-static {v1, v2}, Landroid/support/v4/view/ViewCompat;->canScrollVertically(Landroid/view/View;I)Z

    #@3f
    move-result v1

    #@40
    goto :goto_0
.end method

.method protected getChildDrawingOrder(II)I
    .locals 1
    .param p1, "childCount"    # I
    .param p2, "i"    # I

    #@0
    .prologue
    .line 294
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@2
    if-gez v0, :cond_1

    #@4
    .line 304
    .end local p2    # "i":I
    :cond_0
    :goto_0
    return p2

    #@5
    .line 296
    .restart local p2    # "i":I
    :cond_1
    add-int/lit8 v0, p1, -0x1

    #@7
    if-ne p2, v0, :cond_2

    #@9
    .line 298
    iget p2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@b
    goto :goto_0

    #@c
    .line 299
    :cond_2
    iget v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@e
    if-lt p2, v0, :cond_0

    #@10
    .line 301
    add-int/lit8 p2, p2, 0x1

    #@12
    goto :goto_0
.end method

.method public isRefreshing()Z
    .locals 1

    #@0
    .prologue
    .line 499
    iget-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@2
    return v0
.end method

.method public onInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 10
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    const/4 v8, -0x1

    #@2
    const/high16 v7, -0x40800000    # -1.0f

    #@4
    const/4 v4, 0x0

    #@5
    .line 600
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->ensureTarget()V

    #@8
    .line 602
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@b
    move-result v0

    #@c
    .line 604
    .local v0, "action":I
    iget-boolean v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@e
    if-eqz v5, :cond_0

    #@10
    if-nez v0, :cond_0

    #@12
    .line 605
    iput-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@14
    .line 608
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->isEnabled()Z

    #@17
    move-result v5

    #@18
    if-eqz v5, :cond_1

    #@1a
    iget-boolean v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@1c
    if-nez v5, :cond_1

    #@1e
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->canChildScrollUp()Z

    #@21
    move-result v5

    #@22
    if-nez v5, :cond_1

    #@24
    iget-boolean v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@26
    if-eqz v5, :cond_2

    #@28
    .line 652
    :cond_1
    :goto_0
    return v4

    #@29
    .line 613
    :cond_2
    packed-switch v0, :pswitch_data_0

    #@2c
    .line 652
    :cond_3
    :goto_1
    :pswitch_0
    iget-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@2e
    goto :goto_0

    #@2f
    .line 615
    :pswitch_1
    iget v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@31
    iget-object v6, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@33
    invoke-virtual {v6}, Landroid/support/v4/widget/CircleImageView;->getTop()I

    #@36
    move-result v6

    #@37
    sub-int/2addr v5, v6

    #@38
    invoke-direct {p0, v5, v9}, Landroid/support/v4/widget/SwipeRefreshLayout;->setTargetOffsetTopAndBottom(IZ)V

    #@3b
    .line 616
    invoke-static {p1, v4}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@3e
    move-result v5

    #@3f
    iput v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@41
    .line 617
    iput-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@43
    .line 618
    iget v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@45
    invoke-direct {p0, p1, v5}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMotionEventY(Landroid/view/MotionEvent;I)F

    #@48
    move-result v1

    #@49
    .line 619
    .local v1, "initialMotionY":F
    cmpl-float v5, v1, v7

    #@4b
    if-eqz v5, :cond_1

    #@4d
    .line 622
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mInitialMotionY:F

    #@4f
    .line 625
    .end local v1    # "initialMotionY":F
    :pswitch_2
    iget v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@51
    if-ne v5, v8, :cond_4

    #@53
    .line 626
    sget-object v5, Landroid/support/v4/widget/SwipeRefreshLayout;->LOG_TAG:Ljava/lang/String;

    #@55
    const-string v6, "Got ACTION_MOVE event but don\'t have an active pointer id."

    #@57
    invoke-static {v5, v6}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@5a
    goto :goto_0

    #@5b
    .line 630
    :cond_4
    iget v5, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@5d
    invoke-direct {p0, p1, v5}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMotionEventY(Landroid/view/MotionEvent;I)F

    #@60
    move-result v2

    #@61
    .line 631
    .local v2, "y":F
    cmpl-float v5, v2, v7

    #@63
    if-eqz v5, :cond_1

    #@65
    .line 634
    iget v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mInitialMotionY:F

    #@67
    sub-float v3, v2, v4

    #@69
    .line 635
    .local v3, "yDiff":F
    iget v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTouchSlop:I

    #@6b
    int-to-float v4, v4

    #@6c
    cmpl-float v4, v3, v4

    #@6e
    if-lez v4, :cond_3

    #@70
    iget-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@72
    if-nez v4, :cond_3

    #@74
    .line 636
    iput-boolean v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@76
    .line 637
    iget-object v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@78
    const/16 v5, 0x4c

    #@7a
    invoke-virtual {v4, v5}, Landroid/support/v4/widget/MaterialProgressDrawable;->setAlpha(I)V

    #@7d
    goto :goto_1

    #@7e
    .line 642
    .end local v2    # "y":F
    .end local v3    # "yDiff":F
    :pswitch_3
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->onSecondaryPointerUp(Landroid/view/MotionEvent;)V

    #@81
    goto :goto_1

    #@82
    .line 647
    :pswitch_4
    iput-boolean v4, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@84
    .line 648
    iput v8, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@86
    goto :goto_1

    #@87
    .line 613
    nop

    #@88
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_4
        :pswitch_2
        :pswitch_4
        :pswitch_0
        :pswitch_0
        :pswitch_3
    .end packed-switch
.end method

.method protected onLayout(ZIIII)V
    .locals 14
    .param p1, "changed"    # Z
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "right"    # I
    .param p5, "bottom"    # I

    #@0
    .prologue
    .line 527
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMeasuredWidth()I

    #@3
    move-result v8

    #@4
    .line 528
    .local v8, "width":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMeasuredHeight()I

    #@7
    move-result v7

    #@8
    .line 529
    .local v7, "height":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getChildCount()I

    #@b
    move-result v9

    #@c
    if-nez v9, :cond_1

    #@e
    .line 548
    :cond_0
    :goto_0
    return-void

    #@f
    .line 532
    :cond_1
    iget-object v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@11
    if-nez v9, :cond_2

    #@13
    .line 533
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->ensureTarget()V

    #@16
    .line 535
    :cond_2
    iget-object v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@18
    if-eqz v9, :cond_0

    #@1a
    .line 538
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@1c
    .line 539
    .local v0, "child":Landroid/view/View;
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingLeft()I

    #@1f
    move-result v2

    #@20
    .line 540
    .local v2, "childLeft":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingTop()I

    #@23
    move-result v3

    #@24
    .line 541
    .local v3, "childTop":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingLeft()I

    #@27
    move-result v9

    #@28
    sub-int v9, v8, v9

    #@2a
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingRight()I

    #@2d
    move-result v10

    #@2e
    sub-int v4, v9, v10

    #@30
    .line 542
    .local v4, "childWidth":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingTop()I

    #@33
    move-result v9

    #@34
    sub-int v9, v7, v9

    #@36
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingBottom()I

    #@39
    move-result v10

    #@3a
    sub-int v1, v9, v10

    #@3c
    .line 543
    .local v1, "childHeight":I
    add-int v9, v2, v4

    #@3e
    add-int v10, v3, v1

    #@40
    invoke-virtual {v0, v2, v3, v9, v10}, Landroid/view/View;->layout(IIII)V

    #@43
    .line 544
    iget-object v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@45
    invoke-virtual {v9}, Landroid/support/v4/widget/CircleImageView;->getMeasuredWidth()I

    #@48
    move-result v6

    #@49
    .line 545
    .local v6, "circleWidth":I
    iget-object v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@4b
    invoke-virtual {v9}, Landroid/support/v4/widget/CircleImageView;->getMeasuredHeight()I

    #@4e
    move-result v5

    #@4f
    .line 546
    .local v5, "circleHeight":I
    iget-object v9, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@51
    div-int/lit8 v10, v8, 0x2

    #@53
    div-int/lit8 v11, v6, 0x2

    #@55
    sub-int/2addr v10, v11

    #@56
    iget v11, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@58
    div-int/lit8 v12, v8, 0x2

    #@5a
    div-int/lit8 v13, v6, 0x2

    #@5c
    add-int/2addr v12, v13

    #@5d
    iget v13, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@5f
    add-int/2addr v13, v5

    #@60
    invoke-virtual {v9, v10, v11, v12, v13}, Landroid/support/v4/widget/CircleImageView;->layout(IIII)V

    #@63
    goto :goto_0
.end method

.method public onMeasure(II)V
    .locals 6
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/high16 v5, 0x40000000    # 2.0f

    #@2
    .line 552
    invoke-super {p0, p1, p2}, Landroid/view/ViewGroup;->onMeasure(II)V

    #@5
    .line 553
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@7
    if-nez v1, :cond_0

    #@9
    .line 554
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->ensureTarget()V

    #@c
    .line 556
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@e
    if-nez v1, :cond_2

    #@10
    .line 577
    :cond_1
    :goto_0
    return-void

    #@11
    .line 559
    :cond_2
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTarget:Landroid/view/View;

    #@13
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMeasuredWidth()I

    #@16
    move-result v2

    #@17
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingLeft()I

    #@1a
    move-result v3

    #@1b
    sub-int/2addr v2, v3

    #@1c
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingRight()I

    #@1f
    move-result v3

    #@20
    sub-int/2addr v2, v3

    #@21
    invoke-static {v2, v5}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@24
    move-result v2

    #@25
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getMeasuredHeight()I

    #@28
    move-result v3

    #@29
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingTop()I

    #@2c
    move-result v4

    #@2d
    sub-int/2addr v3, v4

    #@2e
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getPaddingBottom()I

    #@31
    move-result v4

    #@32
    sub-int/2addr v3, v4

    #@33
    invoke-static {v3, v5}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@36
    move-result v3

    #@37
    invoke-virtual {v1, v2, v3}, Landroid/view/View;->measure(II)V

    #@3a
    .line 563
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@3c
    iget v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleWidth:I

    #@3e
    invoke-static {v2, v5}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@41
    move-result v2

    #@42
    iget v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleHeight:I

    #@44
    invoke-static {v3, v5}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@47
    move-result v3

    #@48
    invoke-virtual {v1, v2, v3}, Landroid/support/v4/widget/CircleImageView;->measure(II)V

    #@4b
    .line 565
    iget-boolean v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mUsingCustomStart:Z

    #@4d
    if-nez v1, :cond_3

    #@4f
    iget-boolean v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetCalculated:Z

    #@51
    if-nez v1, :cond_3

    #@53
    .line 566
    const/4 v1, 0x1

    #@54
    iput-boolean v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetCalculated:Z

    #@56
    .line 567
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@58
    invoke-virtual {v1}, Landroid/support/v4/widget/CircleImageView;->getMeasuredHeight()I

    #@5b
    move-result v1

    #@5c
    neg-int v1, v1

    #@5d
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@5f
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@61
    .line 569
    :cond_3
    const/4 v1, -0x1

    #@62
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@64
    .line 571
    const/4 v0, 0x0

    #@65
    .local v0, "index":I
    :goto_1
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getChildCount()I

    #@68
    move-result v1

    #@69
    if-ge v0, v1, :cond_1

    #@6b
    .line 572
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getChildAt(I)Landroid/view/View;

    #@6e
    move-result-object v1

    #@6f
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@71
    if-ne v1, v2, :cond_4

    #@73
    .line 573
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleViewIndex:I

    #@75
    goto :goto_0

    #@76
    .line 571
    :cond_4
    add-int/lit8 v0, v0, 0x1

    #@78
    goto :goto_1
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 28
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 674
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@3
    move-result v4

    #@4
    .line 676
    .local v4, "action":I
    move-object/from16 v0, p0

    #@6
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@8
    move/from16 v21, v0

    #@a
    if-eqz v21, :cond_0

    #@c
    if-nez v4, :cond_0

    #@e
    .line 677
    const/16 v21, 0x0

    #@10
    move/from16 v0, v21

    #@12
    move-object/from16 v1, p0

    #@14
    iput-boolean v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@16
    .line 680
    :cond_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->isEnabled()Z

    #@19
    move-result v21

    #@1a
    if-eqz v21, :cond_1

    #@1c
    move-object/from16 v0, p0

    #@1e
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mReturningToStart:Z

    #@20
    move/from16 v21, v0

    #@22
    if-nez v21, :cond_1

    #@24
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->canChildScrollUp()Z

    #@27
    move-result v21

    #@28
    if-eqz v21, :cond_2

    #@2a
    .line 682
    :cond_1
    const/16 v21, 0x0

    #@2c
    .line 810
    :goto_0
    return v21

    #@2d
    .line 685
    :cond_2
    packed-switch v4, :pswitch_data_0

    #@30
    .line 810
    :cond_3
    :goto_1
    :pswitch_0
    const/16 v21, 0x1

    #@32
    goto :goto_0

    #@33
    .line 687
    :pswitch_1
    const/16 v21, 0x0

    #@35
    move-object/from16 v0, p1

    #@37
    move/from16 v1, v21

    #@39
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@3c
    move-result v21

    #@3d
    move/from16 v0, v21

    #@3f
    move-object/from16 v1, p0

    #@41
    iput v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@43
    .line 688
    const/16 v21, 0x0

    #@45
    move/from16 v0, v21

    #@47
    move-object/from16 v1, p0

    #@49
    iput-boolean v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@4b
    goto :goto_1

    #@4c
    .line 692
    :pswitch_2
    move-object/from16 v0, p0

    #@4e
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@50
    move/from16 v21, v0

    #@52
    move-object/from16 v0, p1

    #@54
    move/from16 v1, v21

    #@56
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@59
    move-result v13

    #@5a
    .line 693
    .local v13, "pointerIndex":I
    if-gez v13, :cond_4

    #@5c
    .line 694
    sget-object v21, Landroid/support/v4/widget/SwipeRefreshLayout;->LOG_TAG:Ljava/lang/String;

    #@5e
    const-string v22, "Got ACTION_MOVE event but have an invalid active pointer id."

    #@60
    invoke-static/range {v21 .. v22}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@63
    .line 695
    const/16 v21, 0x0

    #@65
    goto :goto_0

    #@66
    .line 698
    :cond_4
    move-object/from16 v0, p1

    #@68
    invoke-static {v0, v13}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@6b
    move-result v20

    #@6c
    .line 699
    .local v20, "y":F
    move-object/from16 v0, p0

    #@6e
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mInitialMotionY:F

    #@70
    move/from16 v21, v0

    #@72
    sub-float v21, v20, v21

    #@74
    const/high16 v22, 0x3f000000    # 0.5f

    #@76
    mul-float v12, v21, v22

    #@78
    .line 700
    .local v12, "overscrollTop":F
    move-object/from16 v0, p0

    #@7a
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@7c
    move/from16 v21, v0

    #@7e
    if-eqz v21, :cond_3

    #@80
    .line 701
    move-object/from16 v0, p0

    #@82
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@84
    move-object/from16 v21, v0

    #@86
    const/16 v22, 0x1

    #@88
    invoke-virtual/range {v21 .. v22}, Landroid/support/v4/widget/MaterialProgressDrawable;->showArrow(Z)V

    #@8b
    .line 702
    move-object/from16 v0, p0

    #@8d
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@8f
    move/from16 v21, v0

    #@91
    div-float v11, v12, v21

    #@93
    .line 703
    .local v11, "originalDragPercent":F
    const/16 v21, 0x0

    #@95
    cmpg-float v21, v11, v21

    #@97
    if-gez v21, :cond_5

    #@99
    .line 704
    const/16 v21, 0x0

    #@9b
    goto :goto_0

    #@9c
    .line 706
    :cond_5
    const/high16 v21, 0x3f800000    # 1.0f

    #@9e
    invoke-static {v11}, Ljava/lang/Math;->abs(F)F

    #@a1
    move-result v22

    #@a2
    invoke-static/range {v21 .. v22}, Ljava/lang/Math;->min(FF)F

    #@a5
    move-result v6

    #@a6
    .line 707
    .local v6, "dragPercent":F
    float-to-double v0, v6

    #@a7
    move-wide/from16 v22, v0

    #@a9
    const-wide v24, 0x3fd999999999999aL    # 0.4

    #@ae
    sub-double v22, v22, v24

    #@b0
    const-wide/16 v24, 0x0

    #@b2
    invoke-static/range {v22 .. v25}, Ljava/lang/Math;->max(DD)D

    #@b5
    move-result-wide v22

    #@b6
    move-wide/from16 v0, v22

    #@b8
    double-to-float v0, v0

    #@b9
    move/from16 v21, v0

    #@bb
    const/high16 v22, 0x40a00000    # 5.0f

    #@bd
    mul-float v21, v21, v22

    #@bf
    const/high16 v22, 0x40400000    # 3.0f

    #@c1
    div-float v5, v21, v22

    #@c3
    .line 708
    .local v5, "adjustedPercent":F
    invoke-static {v12}, Ljava/lang/Math;->abs(F)F

    #@c6
    move-result v21

    #@c7
    move-object/from16 v0, p0

    #@c9
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@cb
    move/from16 v22, v0

    #@cd
    sub-float v8, v21, v22

    #@cf
    .line 709
    .local v8, "extraOS":F
    move-object/from16 v0, p0

    #@d1
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mUsingCustomStart:Z

    #@d3
    move/from16 v21, v0

    #@d5
    if-eqz v21, :cond_b

    #@d7
    move-object/from16 v0, p0

    #@d9
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@db
    move/from16 v21, v0

    #@dd
    move-object/from16 v0, p0

    #@df
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@e1
    move/from16 v22, v0

    #@e3
    move/from16 v0, v22

    #@e5
    int-to-float v0, v0

    #@e6
    move/from16 v22, v0

    #@e8
    sub-float v15, v21, v22

    #@ea
    .line 711
    .local v15, "slingshotDist":F
    :goto_2
    const/16 v21, 0x0

    #@ec
    const/high16 v22, 0x40000000    # 2.0f

    #@ee
    mul-float v22, v22, v15

    #@f0
    move/from16 v0, v22

    #@f2
    invoke-static {v8, v0}, Ljava/lang/Math;->min(FF)F

    #@f5
    move-result v22

    #@f6
    div-float v22, v22, v15

    #@f8
    invoke-static/range {v21 .. v22}, Ljava/lang/Math;->max(FF)F

    #@fb
    move-result v19

    #@fc
    .line 713
    .local v19, "tensionSlingshotPercent":F
    const/high16 v21, 0x40800000    # 4.0f

    #@fe
    div-float v21, v19, v21

    #@100
    move/from16 v0, v21

    #@102
    float-to-double v0, v0

    #@103
    move-wide/from16 v22, v0

    #@105
    const/high16 v21, 0x40800000    # 4.0f

    #@107
    div-float v21, v19, v21

    #@109
    move/from16 v0, v21

    #@10b
    float-to-double v0, v0

    #@10c
    move-wide/from16 v24, v0

    #@10e
    const-wide/high16 v26, 0x4000000000000000L    # 2.0

    #@110
    invoke-static/range {v24 .. v27}, Ljava/lang/Math;->pow(DD)D

    #@113
    move-result-wide v24

    #@114
    sub-double v22, v22, v24

    #@116
    move-wide/from16 v0, v22

    #@118
    double-to-float v0, v0

    #@119
    move/from16 v21, v0

    #@11b
    const/high16 v22, 0x40000000    # 2.0f

    #@11d
    mul-float v18, v21, v22

    #@11f
    .line 715
    .local v18, "tensionPercent":F
    mul-float v21, v15, v18

    #@121
    const/high16 v22, 0x40000000    # 2.0f

    #@123
    mul-float v7, v21, v22

    #@125
    .line 717
    .local v7, "extraMove":F
    move-object/from16 v0, p0

    #@127
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@129
    move/from16 v21, v0

    #@12b
    mul-float v22, v15, v6

    #@12d
    add-float v22, v22, v7

    #@12f
    move/from16 v0, v22

    #@131
    float-to-int v0, v0

    #@132
    move/from16 v22, v0

    #@134
    add-int v17, v21, v22

    #@136
    .line 720
    .local v17, "targetY":I
    move-object/from16 v0, p0

    #@138
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@13a
    move-object/from16 v21, v0

    #@13c
    invoke-virtual/range {v21 .. v21}, Landroid/support/v4/widget/CircleImageView;->getVisibility()I

    #@13f
    move-result v21

    #@140
    if-eqz v21, :cond_6

    #@142
    .line 721
    move-object/from16 v0, p0

    #@144
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@146
    move-object/from16 v21, v0

    #@148
    const/16 v22, 0x0

    #@14a
    invoke-virtual/range {v21 .. v22}, Landroid/support/v4/widget/CircleImageView;->setVisibility(I)V

    #@14d
    .line 723
    :cond_6
    move-object/from16 v0, p0

    #@14f
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@151
    move/from16 v21, v0

    #@153
    if-nez v21, :cond_7

    #@155
    .line 724
    move-object/from16 v0, p0

    #@157
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@159
    move-object/from16 v21, v0

    #@15b
    const/high16 v22, 0x3f800000    # 1.0f

    #@15d
    invoke-static/range {v21 .. v22}, Landroid/support/v4/view/ViewCompat;->setScaleX(Landroid/view/View;F)V

    #@160
    .line 725
    move-object/from16 v0, p0

    #@162
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@164
    move-object/from16 v21, v0

    #@166
    const/high16 v22, 0x3f800000    # 1.0f

    #@168
    invoke-static/range {v21 .. v22}, Landroid/support/v4/view/ViewCompat;->setScaleY(Landroid/view/View;F)V

    #@16b
    .line 727
    :cond_7
    move-object/from16 v0, p0

    #@16d
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@16f
    move/from16 v21, v0

    #@171
    cmpg-float v21, v12, v21

    #@173
    if-gez v21, :cond_c

    #@175
    .line 728
    move-object/from16 v0, p0

    #@177
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@179
    move/from16 v21, v0

    #@17b
    if-eqz v21, :cond_8

    #@17d
    .line 729
    move-object/from16 v0, p0

    #@17f
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@181
    move/from16 v21, v0

    #@183
    div-float v21, v12, v21

    #@185
    move-object/from16 v0, p0

    #@187
    move/from16 v1, v21

    #@189
    invoke-direct {v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->setAnimationProgress(F)V

    #@18c
    .line 731
    :cond_8
    move-object/from16 v0, p0

    #@18e
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@190
    move-object/from16 v21, v0

    #@192
    invoke-virtual/range {v21 .. v21}, Landroid/support/v4/widget/MaterialProgressDrawable;->getAlpha()I

    #@195
    move-result v21

    #@196
    const/16 v22, 0x4c

    #@198
    move/from16 v0, v21

    #@19a
    move/from16 v1, v22

    #@19c
    if-le v0, v1, :cond_9

    #@19e
    move-object/from16 v0, p0

    #@1a0
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAlphaStartAnimation:Landroid/view/animation/Animation;

    #@1a2
    move-object/from16 v21, v0

    #@1a4
    move-object/from16 v0, p0

    #@1a6
    move-object/from16 v1, v21

    #@1a8
    invoke-direct {v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->isAnimationRunning(Landroid/view/animation/Animation;)Z

    #@1ab
    move-result v21

    #@1ac
    if-nez v21, :cond_9

    #@1ae
    .line 734
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->startProgressAlphaStartAnimation()V

    #@1b1
    .line 736
    :cond_9
    const v21, 0x3f4ccccd    # 0.8f

    #@1b4
    mul-float v16, v5, v21

    #@1b6
    .line 737
    .local v16, "strokeStart":F
    move-object/from16 v0, p0

    #@1b8
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@1ba
    move-object/from16 v21, v0

    #@1bc
    const/16 v22, 0x0

    #@1be
    const v23, 0x3f4ccccd    # 0.8f

    #@1c1
    move/from16 v0, v23

    #@1c3
    move/from16 v1, v16

    #@1c5
    invoke-static {v0, v1}, Ljava/lang/Math;->min(FF)F

    #@1c8
    move-result v23

    #@1c9
    invoke-virtual/range {v21 .. v23}, Landroid/support/v4/widget/MaterialProgressDrawable;->setStartEndTrim(FF)V

    #@1cc
    .line 738
    move-object/from16 v0, p0

    #@1ce
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@1d0
    move-object/from16 v21, v0

    #@1d2
    const/high16 v22, 0x3f800000    # 1.0f

    #@1d4
    move/from16 v0, v22

    #@1d6
    invoke-static {v0, v5}, Ljava/lang/Math;->min(FF)F

    #@1d9
    move-result v22

    #@1da
    invoke-virtual/range {v21 .. v22}, Landroid/support/v4/widget/MaterialProgressDrawable;->setArrowScale(F)V

    #@1dd
    .line 746
    .end local v16    # "strokeStart":F
    :cond_a
    :goto_3
    const/high16 v21, -0x41800000    # -0.25f

    #@1df
    const v22, 0x3ecccccd    # 0.4f

    #@1e2
    mul-float v22, v22, v5

    #@1e4
    add-float v21, v21, v22

    #@1e6
    const/high16 v22, 0x40000000    # 2.0f

    #@1e8
    mul-float v22, v22, v18

    #@1ea
    add-float v21, v21, v22

    #@1ec
    const/high16 v22, 0x3f000000    # 0.5f

    #@1ee
    mul-float v14, v21, v22

    #@1f0
    .line 747
    .local v14, "rotation":F
    move-object/from16 v0, p0

    #@1f2
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@1f4
    move-object/from16 v21, v0

    #@1f6
    move-object/from16 v0, v21

    #@1f8
    invoke-virtual {v0, v14}, Landroid/support/v4/widget/MaterialProgressDrawable;->setProgressRotation(F)V

    #@1fb
    .line 748
    move-object/from16 v0, p0

    #@1fd
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@1ff
    move/from16 v21, v0

    #@201
    sub-int v21, v17, v21

    #@203
    const/16 v22, 0x1

    #@205
    move-object/from16 v0, p0

    #@207
    move/from16 v1, v21

    #@209
    move/from16 v2, v22

    #@20b
    invoke-direct {v0, v1, v2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setTargetOffsetTopAndBottom(IZ)V

    #@20e
    goto/16 :goto_1

    #@210
    .line 709
    .end local v7    # "extraMove":F
    .end local v14    # "rotation":F
    .end local v15    # "slingshotDist":F
    .end local v17    # "targetY":I
    .end local v18    # "tensionPercent":F
    .end local v19    # "tensionSlingshotPercent":F
    :cond_b
    move-object/from16 v0, p0

    #@212
    iget v15, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@214
    goto/16 :goto_2

    #@216
    .line 740
    .restart local v7    # "extraMove":F
    .restart local v15    # "slingshotDist":F
    .restart local v17    # "targetY":I
    .restart local v18    # "tensionPercent":F
    .restart local v19    # "tensionSlingshotPercent":F
    :cond_c
    move-object/from16 v0, p0

    #@218
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@21a
    move-object/from16 v21, v0

    #@21c
    invoke-virtual/range {v21 .. v21}, Landroid/support/v4/widget/MaterialProgressDrawable;->getAlpha()I

    #@21f
    move-result v21

    #@220
    const/16 v22, 0xff

    #@222
    move/from16 v0, v21

    #@224
    move/from16 v1, v22

    #@226
    if-ge v0, v1, :cond_a

    #@228
    move-object/from16 v0, p0

    #@22a
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mAlphaMaxAnimation:Landroid/view/animation/Animation;

    #@22c
    move-object/from16 v21, v0

    #@22e
    move-object/from16 v0, p0

    #@230
    move-object/from16 v1, v21

    #@232
    invoke-direct {v0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->isAnimationRunning(Landroid/view/animation/Animation;)Z

    #@235
    move-result v21

    #@236
    if-nez v21, :cond_a

    #@238
    .line 743
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->startProgressAlphaMaxAnimation()V

    #@23b
    goto :goto_3

    #@23c
    .line 754
    .end local v5    # "adjustedPercent":F
    .end local v6    # "dragPercent":F
    .end local v7    # "extraMove":F
    .end local v8    # "extraOS":F
    .end local v11    # "originalDragPercent":F
    .end local v12    # "overscrollTop":F
    .end local v13    # "pointerIndex":I
    .end local v15    # "slingshotDist":F
    .end local v17    # "targetY":I
    .end local v18    # "tensionPercent":F
    .end local v19    # "tensionSlingshotPercent":F
    .end local v20    # "y":F
    :pswitch_3
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@23f
    move-result v9

    #@240
    .line 755
    .local v9, "index":I
    move-object/from16 v0, p1

    #@242
    invoke-static {v0, v9}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@245
    move-result v21

    #@246
    move/from16 v0, v21

    #@248
    move-object/from16 v1, p0

    #@24a
    iput v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@24c
    goto/16 :goto_1

    #@24e
    .line 760
    .end local v9    # "index":I
    :pswitch_4
    invoke-direct/range {p0 .. p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->onSecondaryPointerUp(Landroid/view/MotionEvent;)V

    #@251
    goto/16 :goto_1

    #@253
    .line 765
    :pswitch_5
    move-object/from16 v0, p0

    #@255
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@257
    move/from16 v21, v0

    #@259
    const/16 v22, -0x1

    #@25b
    move/from16 v0, v21

    #@25d
    move/from16 v1, v22

    #@25f
    if-ne v0, v1, :cond_e

    #@261
    .line 766
    const/16 v21, 0x1

    #@263
    move/from16 v0, v21

    #@265
    if-ne v4, v0, :cond_d

    #@267
    .line 767
    sget-object v21, Landroid/support/v4/widget/SwipeRefreshLayout;->LOG_TAG:Ljava/lang/String;

    #@269
    const-string v22, "Got ACTION_UP event but don\'t have an active pointer id."

    #@26b
    invoke-static/range {v21 .. v22}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@26e
    .line 769
    :cond_d
    const/16 v21, 0x0

    #@270
    goto/16 :goto_0

    #@272
    .line 771
    :cond_e
    move-object/from16 v0, p0

    #@274
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@276
    move/from16 v21, v0

    #@278
    move-object/from16 v0, p1

    #@27a
    move/from16 v1, v21

    #@27c
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@27f
    move-result v13

    #@280
    .line 772
    .restart local v13    # "pointerIndex":I
    move-object/from16 v0, p1

    #@282
    invoke-static {v0, v13}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@285
    move-result v20

    #@286
    .line 773
    .restart local v20    # "y":F
    move-object/from16 v0, p0

    #@288
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mInitialMotionY:F

    #@28a
    move/from16 v21, v0

    #@28c
    sub-float v21, v20, v21

    #@28e
    const/high16 v22, 0x3f000000    # 0.5f

    #@290
    mul-float v12, v21, v22

    #@292
    .line 774
    .restart local v12    # "overscrollTop":F
    const/16 v21, 0x0

    #@294
    move/from16 v0, v21

    #@296
    move-object/from16 v1, p0

    #@298
    iput-boolean v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mIsBeingDragged:Z

    #@29a
    .line 775
    move-object/from16 v0, p0

    #@29c
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@29e
    move/from16 v21, v0

    #@2a0
    cmpl-float v21, v12, v21

    #@2a2
    if-lez v21, :cond_f

    #@2a4
    .line 776
    const/16 v21, 0x1

    #@2a6
    const/16 v22, 0x1

    #@2a8
    move-object/from16 v0, p0

    #@2aa
    move/from16 v1, v21

    #@2ac
    move/from16 v2, v22

    #@2ae
    invoke-direct {v0, v1, v2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setRefreshing(ZZ)V

    #@2b1
    .line 805
    :goto_4
    const/16 v21, -0x1

    #@2b3
    move/from16 v0, v21

    #@2b5
    move-object/from16 v1, p0

    #@2b7
    iput v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mActivePointerId:I

    #@2b9
    .line 806
    const/16 v21, 0x0

    #@2bb
    goto/16 :goto_0

    #@2bd
    .line 779
    :cond_f
    const/16 v21, 0x0

    #@2bf
    move/from16 v0, v21

    #@2c1
    move-object/from16 v1, p0

    #@2c3
    iput-boolean v0, v1, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@2c5
    .line 780
    move-object/from16 v0, p0

    #@2c7
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2c9
    move-object/from16 v21, v0

    #@2cb
    const/16 v22, 0x0

    #@2cd
    const/16 v23, 0x0

    #@2cf
    invoke-virtual/range {v21 .. v23}, Landroid/support/v4/widget/MaterialProgressDrawable;->setStartEndTrim(FF)V

    #@2d2
    .line 781
    const/4 v10, 0x0

    #@2d3
    .line 782
    .local v10, "listener":Landroid/view/animation/Animation$AnimationListener;
    move-object/from16 v0, p0

    #@2d5
    iget-boolean v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@2d7
    move/from16 v21, v0

    #@2d9
    if-nez v21, :cond_10

    #@2db
    .line 783
    new-instance v10, Landroid/support/v4/widget/SwipeRefreshLayout$5;

    #@2dd
    .end local v10    # "listener":Landroid/view/animation/Animation$AnimationListener;
    move-object/from16 v0, p0

    #@2df
    invoke-direct {v10, v0}, Landroid/support/v4/widget/SwipeRefreshLayout$5;-><init>(Landroid/support/v4/widget/SwipeRefreshLayout;)V

    #@2e2
    .line 802
    .restart local v10    # "listener":Landroid/view/animation/Animation$AnimationListener;
    :cond_10
    move-object/from16 v0, p0

    #@2e4
    iget v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@2e6
    move/from16 v21, v0

    #@2e8
    move-object/from16 v0, p0

    #@2ea
    move/from16 v1, v21

    #@2ec
    invoke-direct {v0, v1, v10}, Landroid/support/v4/widget/SwipeRefreshLayout;->animateOffsetToStartPosition(ILandroid/view/animation/Animation$AnimationListener;)V

    #@2ef
    .line 803
    move-object/from16 v0, p0

    #@2f1
    iget-object v0, v0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2f3
    move-object/from16 v21, v0

    #@2f5
    const/16 v22, 0x0

    #@2f7
    invoke-virtual/range {v21 .. v22}, Landroid/support/v4/widget/MaterialProgressDrawable;->showArrow(Z)V

    #@2fa
    goto :goto_4

    #@2fb
    .line 685
    nop

    #@2fc
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_2
        :pswitch_5
        :pswitch_0
        :pswitch_3
        :pswitch_4
    .end packed-switch
.end method

.method public requestDisallowInterceptTouchEvent(Z)V
    .locals 0
    .param p1, "b"    # Z

    #@0
    .prologue
    .line 666
    return-void
.end method

.method public varargs setColorScheme([I)V
    .locals 0
    .param p1, "colors"    # [I
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 463
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/SwipeRefreshLayout;->setColorSchemeResources([I)V

    #@3
    .line 464
    return-void
.end method

.method public varargs setColorSchemeColors([I)V
    .locals 1
    .param p1, "colors"    # [I

    #@0
    .prologue
    .line 490
    invoke-direct {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->ensureTarget()V

    #@3
    .line 491
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@5
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable;->setColorSchemeColors([I)V

    #@8
    .line 492
    return-void
.end method

.method public varargs setColorSchemeResources([I)V
    .locals 4
    .param p1, "colorResIds"    # [I

    #@0
    .prologue
    .line 474
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v2

    #@4
    .line 475
    .local v2, "res":Landroid/content/res/Resources;
    array-length v3, p1

    #@5
    new-array v0, v3, [I

    #@7
    .line 476
    .local v0, "colorRes":[I
    const/4 v1, 0x0

    #@8
    .local v1, "i":I
    :goto_0
    array-length v3, p1

    #@9
    if-ge v1, v3, :cond_0

    #@b
    .line 477
    aget v3, p1, v1

    #@d
    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getColor(I)I

    #@10
    move-result v3

    #@11
    aput v3, v0, v1

    #@13
    .line 476
    add-int/lit8 v1, v1, 0x1

    #@15
    goto :goto_0

    #@16
    .line 479
    :cond_0
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SwipeRefreshLayout;->setColorSchemeColors([I)V

    #@19
    .line 480
    return-void
.end method

.method public setDistanceToTriggerSync(I)V
    .locals 1
    .param p1, "distance"    # I

    #@0
    .prologue
    .line 522
    int-to-float v0, p1

    #@1
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mTotalDragDistance:F

    #@3
    .line 523
    return-void
.end method

.method public setOnRefreshListener(Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;

    #@0
    .prologue
    .line 322
    iput-object p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mListener:Landroid/support/v4/widget/SwipeRefreshLayout$OnRefreshListener;

    #@2
    .line 323
    return-void
.end method

.method public setProgressBackgroundColor(I)V
    .locals 2
    .param p1, "colorRes"    # I

    #@0
    .prologue
    .line 454
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/CircleImageView;->setBackgroundColor(I)V

    #@5
    .line 455
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@7
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getResources()Landroid/content/res/Resources;

    #@a
    move-result-object v1

    #@b
    invoke-virtual {v1, p1}, Landroid/content/res/Resources;->getColor(I)I

    #@e
    move-result v1

    #@f
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable;->setBackgroundColor(I)V

    #@12
    .line 456
    return-void
.end method

.method public setProgressViewEndTarget(ZI)V
    .locals 1
    .param p1, "scale"    # Z
    .param p2, "end"    # I

    #@0
    .prologue
    .line 226
    int-to-float v0, p2

    #@1
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@3
    .line 227
    iput-boolean p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@5
    .line 228
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->invalidate()V

    #@a
    .line 229
    return-void
.end method

.method public setProgressViewOffset(ZII)V
    .locals 2
    .param p1, "scale"    # Z
    .param p2, "start"    # I
    .param p3, "end"    # I

    #@0
    .prologue
    .line 205
    iput-boolean p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mScale:Z

    #@2
    .line 206
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@4
    const/16 v1, 0x8

    #@6
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/CircleImageView;->setVisibility(I)V

    #@9
    .line 207
    iput p2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@b
    iput p2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@d
    .line 208
    int-to-float v0, p3

    #@e
    iput v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@10
    .line 209
    const/4 v0, 0x1

    #@11
    iput-boolean v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mUsingCustomStart:Z

    #@13
    .line 210
    iget-object v0, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@15
    invoke-virtual {v0}, Landroid/support/v4/widget/CircleImageView;->invalidate()V

    #@18
    .line 211
    return-void
.end method

.method public setRefreshing(Z)V
    .locals 4
    .param p1, "refreshing"    # Z

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 339
    if-eqz p1, :cond_1

    #@3
    iget-boolean v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@5
    if-eq v1, p1, :cond_1

    #@7
    .line 341
    iput-boolean p1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshing:Z

    #@9
    .line 342
    const/4 v0, 0x0

    #@a
    .line 343
    .local v0, "endTarget":I
    iget-boolean v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mUsingCustomStart:Z

    #@c
    if-nez v1, :cond_0

    #@e
    .line 344
    iget v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@10
    iget v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mOriginalOffsetTop:I

    #@12
    int-to-float v2, v2

    #@13
    add-float/2addr v1, v2

    #@14
    float-to-int v0, v1

    #@15
    .line 348
    :goto_0
    iget v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCurrentTargetOffsetTop:I

    #@17
    sub-int v1, v0, v1

    #@19
    const/4 v2, 0x1

    #@1a
    invoke-direct {p0, v1, v2}, Landroid/support/v4/widget/SwipeRefreshLayout;->setTargetOffsetTopAndBottom(IZ)V

    #@1d
    .line 350
    iput-boolean v3, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mNotify:Z

    #@1f
    .line 351
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mRefreshListener:Landroid/view/animation/Animation$AnimationListener;

    #@21
    invoke-direct {p0, v1}, Landroid/support/v4/widget/SwipeRefreshLayout;->startScaleUpAnimation(Landroid/view/animation/Animation$AnimationListener;)V

    #@24
    .line 355
    .end local v0    # "endTarget":I
    :goto_1
    return-void

    #@25
    .line 346
    .restart local v0    # "endTarget":I
    :cond_0
    iget v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mSpinnerFinalOffset:F

    #@27
    float-to-int v0, v1

    #@28
    goto :goto_0

    #@29
    .line 353
    .end local v0    # "endTarget":I
    :cond_1
    invoke-direct {p0, p1, v3}, Landroid/support/v4/widget/SwipeRefreshLayout;->setRefreshing(ZZ)V

    #@2c
    goto :goto_1
.end method

.method public setSize(I)V
    .locals 3
    .param p1, "size"    # I

    #@0
    .prologue
    .line 235
    if-eqz p1, :cond_0

    #@2
    const/4 v1, 0x1

    #@3
    if-eq p1, v1, :cond_0

    #@5
    .line 250
    :goto_0
    return-void

    #@6
    .line 238
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/widget/SwipeRefreshLayout;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v1

    #@a
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@d
    move-result-object v0

    #@e
    .line 239
    .local v0, "metrics":Landroid/util/DisplayMetrics;
    if-nez p1, :cond_1

    #@10
    .line 240
    const/high16 v1, 0x42600000    # 56.0f

    #@12
    iget v2, v0, Landroid/util/DisplayMetrics;->density:F

    #@14
    mul-float/2addr v1, v2

    #@15
    float-to-int v1, v1

    #@16
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleWidth:I

    #@18
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleHeight:I

    #@1a
    .line 247
    :goto_1
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@1c
    const/4 v2, 0x0

    #@1d
    invoke-virtual {v1, v2}, Landroid/support/v4/widget/CircleImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@20
    .line 248
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@22
    invoke-virtual {v1, p1}, Landroid/support/v4/widget/MaterialProgressDrawable;->updateSizes(I)V

    #@25
    .line 249
    iget-object v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleView:Landroid/support/v4/widget/CircleImageView;

    #@27
    iget-object v2, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mProgress:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@29
    invoke-virtual {v1, v2}, Landroid/support/v4/widget/CircleImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@2c
    goto :goto_0

    #@2d
    .line 242
    :cond_1
    const/high16 v1, 0x42200000    # 40.0f

    #@2f
    iget v2, v0, Landroid/util/DisplayMetrics;->density:F

    #@31
    mul-float/2addr v1, v2

    #@32
    float-to-int v1, v1

    #@33
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleWidth:I

    #@35
    iput v1, p0, Landroid/support/v4/widget/SwipeRefreshLayout;->mCircleHeight:I

    #@37
    goto :goto_1
.end method
