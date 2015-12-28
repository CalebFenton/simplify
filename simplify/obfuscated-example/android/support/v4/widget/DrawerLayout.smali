.class public Landroid/support/v4/widget/DrawerLayout;
.super Landroid/view/ViewGroup;
.source "DrawerLayout.java"

# interfaces
.implements Landroid/support/v4/widget/DrawerLayoutImpl;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;,
        Landroid/support/v4/widget/DrawerLayout$AccessibilityDelegate;,
        Landroid/support/v4/widget/DrawerLayout$LayoutParams;,
        Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;,
        Landroid/support/v4/widget/DrawerLayout$SavedState;,
        Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplApi21;,
        Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplBase;,
        Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;,
        Landroid/support/v4/widget/DrawerLayout$SimpleDrawerListener;,
        Landroid/support/v4/widget/DrawerLayout$DrawerListener;,
        Landroid/support/v4/widget/DrawerLayout$EdgeGravity;,
        Landroid/support/v4/widget/DrawerLayout$LockMode;,
        Landroid/support/v4/widget/DrawerLayout$State;
    }
.end annotation


# static fields
.field private static final ALLOW_EDGE_LOCK:Z = false

.field private static final CAN_HIDE_DESCENDANTS:Z

.field private static final CHILDREN_DISALLOW_INTERCEPT:Z = true

.field private static final DEFAULT_SCRIM_COLOR:I = -0x67000000

.field static final IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

.field private static final LAYOUT_ATTRS:[I

.field public static final LOCK_MODE_LOCKED_CLOSED:I = 0x1

.field public static final LOCK_MODE_LOCKED_OPEN:I = 0x2

.field public static final LOCK_MODE_UNLOCKED:I = 0x0

.field private static final MIN_DRAWER_MARGIN:I = 0x40

.field private static final MIN_FLING_VELOCITY:I = 0x190

.field private static final PEEK_DELAY:I = 0xa0

.field public static final STATE_DRAGGING:I = 0x1

.field public static final STATE_IDLE:I = 0x0

.field public static final STATE_SETTLING:I = 0x2

.field private static final TAG:Ljava/lang/String; = "DrawerLayout"

.field private static final TOUCH_SLOP_SENSITIVITY:F = 1.0f


# instance fields
.field private final mChildAccessibilityDelegate:Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;

.field private mChildrenCanceledTouch:Z

.field private mDisallowInterceptRequested:Z

.field private mDrawStatusBarBackground:Z

.field private mDrawerState:I

.field private mFirstLayout:Z

.field private mInLayout:Z

.field private mInitialMotionX:F

.field private mInitialMotionY:F

.field private mLastInsets:Ljava/lang/Object;

.field private final mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

.field private final mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

.field private mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

.field private mLockModeLeft:I

.field private mLockModeRight:I

.field private mMinDrawerMargin:I

.field private final mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

.field private final mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

.field private mScrimColor:I

.field private mScrimOpacity:F

.field private mScrimPaint:Landroid/graphics/Paint;

.field private mShadowLeft:Landroid/graphics/drawable/Drawable;

.field private mShadowRight:Landroid/graphics/drawable/Drawable;

.field private mStatusBarBackground:Landroid/graphics/drawable/Drawable;

.field private mTitleLeft:Ljava/lang/CharSequence;

.field private mTitleRight:Ljava/lang/CharSequence;


# direct methods
.method static constructor <clinit>()V
    .locals 5

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 159
    new-array v3, v1, [I

    #@4
    const v4, 0x10100b3

    #@7
    aput v4, v3, v2

    #@9
    sput-object v3, Landroid/support/v4/widget/DrawerLayout;->LAYOUT_ATTRS:[I

    #@b
    .line 164
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@d
    const/16 v4, 0x13

    #@f
    if-lt v3, v4, :cond_0

    #@11
    :goto_0
    sput-boolean v1, Landroid/support/v4/widget/DrawerLayout;->CAN_HIDE_DESCENDANTS:Z

    #@13
    .line 303
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@15
    .line 304
    .local v0, "version":I
    const/16 v1, 0x15

    #@17
    if-lt v0, v1, :cond_1

    #@19
    .line 305
    new-instance v1, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplApi21;

    #@1b
    invoke-direct {v1}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplApi21;-><init>()V

    #@1e
    sput-object v1, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@20
    .line 309
    :goto_1
    return-void

    #@21
    .end local v0    # "version":I
    :cond_0
    move v1, v2

    #@22
    .line 164
    goto :goto_0

    #@23
    .line 307
    .restart local v0    # "version":I
    :cond_1
    new-instance v1, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplBase;

    #@25
    invoke-direct {v1}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImplBase;-><init>()V

    #@28
    sput-object v1, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@2a
    goto :goto_1
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 314
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/widget/DrawerLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 315
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 318
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v4/widget/DrawerLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 319
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 6
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/high16 v5, 0x3f800000    # 1.0f

    #@2
    const/4 v4, 0x1

    #@3
    .line 322
    invoke-direct {p0, p1, p2, p3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 166
    new-instance v2, Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;

    #@8
    invoke-direct {v2, p0}, Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;-><init>(Landroid/support/v4/widget/DrawerLayout;)V

    #@b
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mChildAccessibilityDelegate:Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;

    #@d
    .line 171
    const/high16 v2, -0x67000000

    #@f
    iput v2, p0, Landroid/support/v4/widget/DrawerLayout;->mScrimColor:I

    #@11
    .line 173
    new-instance v2, Landroid/graphics/Paint;

    #@13
    invoke-direct {v2}, Landroid/graphics/Paint;-><init>()V

    #@16
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mScrimPaint:Landroid/graphics/Paint;

    #@18
    .line 181
    iput-boolean v4, p0, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@1a
    .line 323
    const/high16 v2, 0x40000

    #@1c
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->setDescendantFocusability(I)V

    #@1f
    .line 324
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getResources()Landroid/content/res/Resources;

    #@22
    move-result-object v2

    #@23
    invoke-virtual {v2}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@26
    move-result-object v2

    #@27
    iget v0, v2, Landroid/util/DisplayMetrics;->density:F

    #@29
    .line 325
    .local v0, "density":F
    const/high16 v2, 0x42800000    # 64.0f

    #@2b
    mul-float/2addr v2, v0

    #@2c
    const/high16 v3, 0x3f000000    # 0.5f

    #@2e
    add-float/2addr v2, v3

    #@2f
    float-to-int v2, v2

    #@30
    iput v2, p0, Landroid/support/v4/widget/DrawerLayout;->mMinDrawerMargin:I

    #@32
    .line 326
    const/high16 v2, 0x43c80000    # 400.0f

    #@34
    mul-float v1, v2, v0

    #@36
    .line 328
    .local v1, "minVel":F
    new-instance v2, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@38
    const/4 v3, 0x3

    #@39
    invoke-direct {v2, p0, v3}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;-><init>(Landroid/support/v4/widget/DrawerLayout;I)V

    #@3c
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@3e
    .line 329
    new-instance v2, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@40
    const/4 v3, 0x5

    #@41
    invoke-direct {v2, p0, v3}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;-><init>(Landroid/support/v4/widget/DrawerLayout;I)V

    #@44
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@46
    .line 331
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@48
    invoke-static {p0, v5, v2}, Landroid/support/v4/widget/ViewDragHelper;->create(Landroid/view/ViewGroup;FLandroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;

    #@4b
    move-result-object v2

    #@4c
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@4e
    .line 332
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@50
    invoke-virtual {v2, v4}, Landroid/support/v4/widget/ViewDragHelper;->setEdgeTrackingEnabled(I)V

    #@53
    .line 333
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@55
    invoke-virtual {v2, v1}, Landroid/support/v4/widget/ViewDragHelper;->setMinVelocity(F)V

    #@58
    .line 334
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@5a
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@5c
    invoke-virtual {v2, v3}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->setDragger(Landroid/support/v4/widget/ViewDragHelper;)V

    #@5f
    .line 336
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@61
    invoke-static {p0, v5, v2}, Landroid/support/v4/widget/ViewDragHelper;->create(Landroid/view/ViewGroup;FLandroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;

    #@64
    move-result-object v2

    #@65
    iput-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@67
    .line 337
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@69
    const/4 v3, 0x2

    #@6a
    invoke-virtual {v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->setEdgeTrackingEnabled(I)V

    #@6d
    .line 338
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@6f
    invoke-virtual {v2, v1}, Landroid/support/v4/widget/ViewDragHelper;->setMinVelocity(F)V

    #@72
    .line 339
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@74
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@76
    invoke-virtual {v2, v3}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->setDragger(Landroid/support/v4/widget/ViewDragHelper;)V

    #@79
    .line 342
    invoke-virtual {p0, v4}, Landroid/support/v4/widget/DrawerLayout;->setFocusableInTouchMode(Z)V

    #@7c
    .line 344
    invoke-static {p0, v4}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@7f
    .line 347
    new-instance v2, Landroid/support/v4/widget/DrawerLayout$AccessibilityDelegate;

    #@81
    invoke-direct {v2, p0}, Landroid/support/v4/widget/DrawerLayout$AccessibilityDelegate;-><init>(Landroid/support/v4/widget/DrawerLayout;)V

    #@84
    invoke-static {p0, v2}, Landroid/support/v4/view/ViewCompat;->setAccessibilityDelegate(Landroid/view/View;Landroid/support/v4/view/AccessibilityDelegateCompat;)V

    #@87
    .line 348
    const/4 v2, 0x0

    #@88
    invoke-static {p0, v2}, Landroid/support/v4/view/ViewGroupCompat;->setMotionEventSplittingEnabled(Landroid/view/ViewGroup;Z)V

    #@8b
    .line 349
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getFitsSystemWindows(Landroid/view/View;)Z

    #@8e
    move-result v2

    #@8f
    if-eqz v2, :cond_0

    #@91
    .line 350
    sget-object v2, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@93
    invoke-interface {v2, p0}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;->configureApplyInsets(Landroid/view/View;)V

    #@96
    .line 352
    :cond_0
    return-void
.end method

.method static synthetic access$100()[I
    .locals 1

    #@0
    .prologue
    .line 86
    sget-object v0, Landroid/support/v4/widget/DrawerLayout;->LAYOUT_ATTRS:[I

    #@2
    return-object v0
.end method

.method static synthetic access$200()Z
    .locals 1

    #@0
    .prologue
    .line 86
    sget-boolean v0, Landroid/support/v4/widget/DrawerLayout;->CAN_HIDE_DESCENDANTS:Z

    #@2
    return v0
.end method

.method static synthetic access$300(Landroid/support/v4/widget/DrawerLayout;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/DrawerLayout;

    #@0
    .prologue
    .line 86
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout;->findVisibleDrawer()Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$400(Landroid/view/View;)Z
    .locals 1
    .param p0, "x0"    # Landroid/view/View;

    #@0
    .prologue
    .line 86
    invoke-static {p0}, Landroid/support/v4/widget/DrawerLayout;->includeChildForAccessibility(Landroid/view/View;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method private findVisibleDrawer()Landroid/view/View;
    .locals 4

    #@0
    .prologue
    .line 1444
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@3
    move-result v1

    #@4
    .line 1445
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@5
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_1

    #@7
    .line 1446
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 1447
    .local v0, "child":Landroid/view/View;
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@e
    move-result v3

    #@f
    if-eqz v3, :cond_0

    #@11
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerVisible(Landroid/view/View;)Z

    #@14
    move-result v3

    #@15
    if-eqz v3, :cond_0

    #@17
    .line 1451
    .end local v0    # "child":Landroid/view/View;
    :goto_1
    return-object v0

    #@18
    .line 1445
    .restart local v0    # "child":Landroid/view/View;
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@1a
    goto :goto_0

    #@1b
    .line 1451
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    const/4 v0, 0x0

    #@1c
    goto :goto_1
.end method

.method static gravityToString(I)Ljava/lang/String;
    .locals 2
    .param p0, "gravity"    # I

    #@0
    .prologue
    .line 766
    and-int/lit8 v0, p0, 0x3

    #@2
    const/4 v1, 0x3

    #@3
    if-ne v0, v1, :cond_0

    #@5
    .line 767
    const-string v0, "LEFT"

    #@7
    .line 772
    :goto_0
    return-object v0

    #@8
    .line 769
    :cond_0
    and-int/lit8 v0, p0, 0x5

    #@a
    const/4 v1, 0x5

    #@b
    if-ne v0, v1, :cond_1

    #@d
    .line 770
    const-string v0, "RIGHT"

    #@f
    goto :goto_0

    #@10
    .line 772
    :cond_1
    invoke-static {p0}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@13
    move-result-object v0

    #@14
    goto :goto_0
.end method

.method private static hasOpaqueBackground(Landroid/view/View;)Z
    .locals 4
    .param p0, "v"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 983
    invoke-virtual {p0}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    #@4
    move-result-object v0

    #@5
    .line 984
    .local v0, "bg":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@7
    .line 985
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getOpacity()I

    #@a
    move-result v2

    #@b
    const/4 v3, -0x1

    #@c
    if-ne v2, v3, :cond_0

    #@e
    const/4 v1, 0x1

    #@f
    .line 987
    :cond_0
    return v1
.end method

.method private hasPeekingDrawer()Z
    .locals 4

    #@0
    .prologue
    .line 1405
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@3
    move-result v0

    #@4
    .line 1406
    .local v0, "childCount":I
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@7
    .line 1407
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v3

    #@b
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@e
    move-result-object v2

    #@f
    check-cast v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@11
    .line 1408
    .local v2, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget-boolean v3, v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->isPeeking:Z

    #@13
    if-eqz v3, :cond_0

    #@15
    .line 1409
    const/4 v3, 0x1

    #@16
    .line 1412
    .end local v2    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :goto_1
    return v3

    #@17
    .line 1406
    .restart local v2    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@19
    goto :goto_0

    #@1a
    .line 1412
    .end local v2    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_1
    const/4 v3, 0x0

    #@1b
    goto :goto_1
.end method

.method private hasVisibleDrawer()Z
    .locals 1

    #@0
    .prologue
    .line 1440
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout;->findVisibleDrawer()Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    if-eqz v0, :cond_0

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

.method private static includeChildForAccessibility(Landroid/view/View;)Z
    .locals 2
    .param p0, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1552
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getImportantForAccessibility(Landroid/view/View;)I

    #@3
    move-result v0

    #@4
    const/4 v1, 0x4

    #@5
    if-eq v0, v1, :cond_0

    #@7
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getImportantForAccessibility(Landroid/view/View;)I

    #@a
    move-result v0

    #@b
    const/4 v1, 0x2

    #@c
    if-eq v0, v1, :cond_0

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

.method private updateChildrenImportantForAccessibility(Landroid/view/View;Z)V
    .locals 4
    .param p1, "drawerView"    # Landroid/view/View;
    .param p2, "isDrawerOpen"    # Z

    #@0
    .prologue
    .line 666
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@3
    move-result v1

    #@4
    .line 667
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@5
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_3

    #@7
    .line 668
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 669
    .local v0, "child":Landroid/view/View;
    if-nez p2, :cond_0

    #@d
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@10
    move-result v3

    #@11
    if-eqz v3, :cond_1

    #@13
    :cond_0
    if-eqz p2, :cond_2

    #@15
    if-ne v0, p1, :cond_2

    #@17
    .line 673
    :cond_1
    const/4 v3, 0x1

    #@18
    invoke-static {v0, v3}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@1b
    .line 667
    :goto_1
    add-int/lit8 v2, v2, 0x1

    #@1d
    goto :goto_0

    #@1e
    .line 676
    :cond_2
    const/4 v3, 0x4

    #@1f
    invoke-static {v0, v3}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@22
    goto :goto_1

    #@23
    .line 680
    .end local v0    # "child":Landroid/view/View;
    :cond_3
    return-void
.end method


# virtual methods
.method public addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "index"    # I
    .param p3, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1524
    invoke-super {p0, p1, p2, p3}, Landroid/view/ViewGroup;->addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 1526
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->findOpenDrawer()Landroid/view/View;

    #@6
    move-result-object v0

    #@7
    .line 1527
    .local v0, "openDrawer":Landroid/view/View;
    if-nez v0, :cond_0

    #@9
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@c
    move-result v1

    #@d
    if-eqz v1, :cond_2

    #@f
    .line 1530
    :cond_0
    const/4 v1, 0x4

    #@10
    invoke-static {p1, v1}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@13
    .line 1541
    :goto_0
    sget-boolean v1, Landroid/support/v4/widget/DrawerLayout;->CAN_HIDE_DESCENDANTS:Z

    #@15
    if-nez v1, :cond_1

    #@17
    .line 1542
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mChildAccessibilityDelegate:Landroid/support/v4/widget/DrawerLayout$ChildAccessibilityDelegate;

    #@19
    invoke-static {p1, v1}, Landroid/support/v4/view/ViewCompat;->setAccessibilityDelegate(Landroid/view/View;Landroid/support/v4/view/AccessibilityDelegateCompat;)V

    #@1c
    .line 1544
    :cond_1
    return-void

    #@1d
    .line 1535
    :cond_2
    const/4 v1, 0x1

    #@1e
    invoke-static {p1, v1}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@21
    goto :goto_0
.end method

.method cancelChildViewTouch()V
    .locals 11

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 1456
    iget-boolean v2, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@3
    if-nez v2, :cond_1

    #@5
    .line 1457
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@8
    move-result-wide v0

    #@9
    .line 1458
    .local v0, "now":J
    const/4 v4, 0x3

    #@a
    const/4 v7, 0x0

    #@b
    move-wide v2, v0

    #@c
    move v6, v5

    #@d
    invoke-static/range {v0 .. v7}, Landroid/view/MotionEvent;->obtain(JJIFFI)Landroid/view/MotionEvent;

    #@10
    move-result-object v8

    #@11
    .line 1460
    .local v8, "cancelEvent":Landroid/view/MotionEvent;
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@14
    move-result v9

    #@15
    .line 1461
    .local v9, "childCount":I
    const/4 v10, 0x0

    #@16
    .local v10, "i":I
    :goto_0
    if-ge v10, v9, :cond_0

    #@18
    .line 1462
    invoke-virtual {p0, v10}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@1b
    move-result-object v2

    #@1c
    invoke-virtual {v2, v8}, Landroid/view/View;->dispatchTouchEvent(Landroid/view/MotionEvent;)Z

    #@1f
    .line 1461
    add-int/lit8 v10, v10, 0x1

    #@21
    goto :goto_0

    #@22
    .line 1464
    :cond_0
    invoke-virtual {v8}, Landroid/view/MotionEvent;->recycle()V

    #@25
    .line 1465
    const/4 v2, 0x1

    #@26
    iput-boolean v2, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@28
    .line 1467
    .end local v0    # "now":J
    .end local v8    # "cancelEvent":Landroid/view/MotionEvent;
    .end local v9    # "childCount":I
    .end local v10    # "i":I
    :cond_1
    return-void
.end method

.method checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z
    .locals 2
    .param p1, "drawerView"    # Landroid/view/View;
    .param p2, "checkFor"    # I

    #@0
    .prologue
    .line 712
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewAbsoluteGravity(Landroid/view/View;)I

    #@3
    move-result v0

    #@4
    .line 713
    .local v0, "absGravity":I
    and-int v1, v0, p2

    #@6
    if-ne v1, p2, :cond_0

    #@8
    const/4 v1, 0x1

    #@9
    :goto_0
    return v1

    #@a
    :cond_0
    const/4 v1, 0x0

    #@b
    goto :goto_0
.end method

.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1431
    instance-of v0, p1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2
    if-eqz v0, :cond_0

    #@4
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public closeDrawer(I)V
    .locals 4
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1331
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 1332
    .local v0, "drawerView":Landroid/view/View;
    if-nez v0, :cond_0

    #@6
    .line 1333
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "No drawer view found with gravity "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-static {p1}, Landroid/support/v4/widget/DrawerLayout;->gravityToString(I)Ljava/lang/String;

    #@16
    move-result-object v3

    #@17
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v2

    #@1b
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v2

    #@1f
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@22
    throw v1

    #@23
    .line 1336
    :cond_0
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->closeDrawer(Landroid/view/View;)V

    #@26
    .line 1337
    return-void
.end method

.method public closeDrawer(Landroid/view/View;)V
    .locals 4
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    .line 1305
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_0

    #@6
    .line 1306
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "View "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v2

    #@17
    const-string v3, " is not a sliding drawer"

    #@19
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v2

    #@1d
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v2

    #@21
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@24
    throw v1

    #@25
    .line 1309
    :cond_0
    iget-boolean v1, p0, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@27
    if-eqz v1, :cond_1

    #@29
    .line 1310
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2c
    move-result-object v0

    #@2d
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2f
    .line 1311
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    const/4 v1, 0x0

    #@30
    iput v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@32
    .line 1312
    const/4 v1, 0x0

    #@33
    iput-boolean v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@35
    .line 1321
    .end local v0    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@38
    .line 1322
    return-void

    #@39
    .line 1314
    :cond_1
    const/4 v1, 0x3

    #@3a
    invoke-virtual {p0, p1, v1}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@3d
    move-result v1

    #@3e
    if-eqz v1, :cond_2

    #@40
    .line 1315
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@42
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@45
    move-result v2

    #@46
    neg-int v2, v2

    #@47
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@4a
    move-result v3

    #@4b
    invoke-virtual {v1, p1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@4e
    goto :goto_0

    #@4f
    .line 1318
    :cond_2
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@51
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@54
    move-result v2

    #@55
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@58
    move-result v3

    #@59
    invoke-virtual {v1, p1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@5c
    goto :goto_0
.end method

.method public closeDrawers()V
    .locals 1

    #@0
    .prologue
    .line 1222
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers(Z)V

    #@4
    .line 1223
    return-void
.end method

.method closeDrawers(Z)V
    .locals 9
    .param p1, "peekingOnly"    # Z

    #@0
    .prologue
    .line 1226
    const/4 v5, 0x0

    #@1
    .line 1227
    .local v5, "needsInvalidate":Z
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@4
    move-result v1

    #@5
    .line 1228
    .local v1, "childCount":I
    const/4 v3, 0x0

    #@6
    .local v3, "i":I
    :goto_0
    if-ge v3, v1, :cond_3

    #@8
    .line 1229
    invoke-virtual {p0, v3}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    .line 1230
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@f
    move-result-object v4

    #@10
    check-cast v4, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@12
    .line 1232
    .local v4, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@15
    move-result v6

    #@16
    if-eqz v6, :cond_0

    #@18
    if-eqz p1, :cond_1

    #@1a
    iget-boolean v6, v4, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->isPeeking:Z

    #@1c
    if-nez v6, :cond_1

    #@1e
    .line 1228
    :cond_0
    :goto_1
    add-int/lit8 v3, v3, 0x1

    #@20
    goto :goto_0

    #@21
    .line 1236
    :cond_1
    invoke-virtual {v0}, Landroid/view/View;->getWidth()I

    #@24
    move-result v2

    #@25
    .line 1238
    .local v2, "childWidth":I
    const/4 v6, 0x3

    #@26
    invoke-virtual {p0, v0, v6}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@29
    move-result v6

    #@2a
    if-eqz v6, :cond_2

    #@2c
    .line 1239
    iget-object v6, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@2e
    neg-int v7, v2

    #@2f
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@32
    move-result v8

    #@33
    invoke-virtual {v6, v0, v7, v8}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@36
    move-result v6

    #@37
    or-int/2addr v5, v6

    #@38
    .line 1246
    :goto_2
    const/4 v6, 0x0

    #@39
    iput-boolean v6, v4, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->isPeeking:Z

    #@3b
    goto :goto_1

    #@3c
    .line 1242
    :cond_2
    iget-object v6, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@3e
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@41
    move-result v7

    #@42
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@45
    move-result v8

    #@46
    invoke-virtual {v6, v0, v7, v8}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@49
    move-result v6

    #@4a
    or-int/2addr v5, v6

    #@4b
    goto :goto_2

    #@4c
    .line 1249
    .end local v0    # "child":Landroid/view/View;
    .end local v2    # "childWidth":I
    .end local v4    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_3
    iget-object v6, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@4e
    invoke-virtual {v6}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->removeCallbacks()V

    #@51
    .line 1250
    iget-object v6, p0, Landroid/support/v4/widget/DrawerLayout;->mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@53
    invoke-virtual {v6}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->removeCallbacks()V

    #@56
    .line 1252
    if-eqz v5, :cond_4

    #@58
    .line 1253
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@5b
    .line 1255
    :cond_4
    return-void
.end method

.method public computeScroll()V
    .locals 7

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    .line 968
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@4
    move-result v0

    #@5
    .line 969
    .local v0, "childCount":I
    const/4 v3, 0x0

    #@6
    .line 970
    .local v3, "scrimOpacity":F
    const/4 v1, 0x0

    #@7
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@9
    .line 971
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@c
    move-result-object v4

    #@d
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@10
    move-result-object v4

    #@11
    check-cast v4, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@13
    iget v2, v4, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@15
    .line 972
    .local v2, "onscreen":F
    invoke-static {v3, v2}, Ljava/lang/Math;->max(FF)F

    #@18
    move-result v3

    #@19
    .line 970
    add-int/lit8 v1, v1, 0x1

    #@1b
    goto :goto_0

    #@1c
    .line 974
    .end local v2    # "onscreen":F
    :cond_0
    iput v3, p0, Landroid/support/v4/widget/DrawerLayout;->mScrimOpacity:F

    #@1e
    .line 977
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@20
    invoke-virtual {v4, v6}, Landroid/support/v4/widget/ViewDragHelper;->continueSettling(Z)Z

    #@23
    move-result v4

    #@24
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@26
    invoke-virtual {v5, v6}, Landroid/support/v4/widget/ViewDragHelper;->continueSettling(Z)Z

    #@29
    move-result v5

    #@2a
    or-int/2addr v4, v5

    #@2b
    if-eqz v4, :cond_1

    #@2d
    .line 978
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@30
    .line 980
    :cond_1
    return-void
.end method

.method dispatchOnDrawerClosed(Landroid/view/View;)V
    .locals 4
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 630
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v0

    #@5
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@7
    .line 631
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget-boolean v2, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@9
    if-eqz v2, :cond_1

    #@b
    .line 632
    iput-boolean v3, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@d
    .line 633
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@f
    if-eqz v2, :cond_0

    #@11
    .line 634
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@13
    invoke-interface {v2, p1}, Landroid/support/v4/widget/DrawerLayout$DrawerListener;->onDrawerClosed(Landroid/view/View;)V

    #@16
    .line 637
    :cond_0
    invoke-direct {p0, p1, v3}, Landroid/support/v4/widget/DrawerLayout;->updateChildrenImportantForAccessibility(Landroid/view/View;Z)V

    #@19
    .line 642
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->hasWindowFocus()Z

    #@1c
    move-result v2

    #@1d
    if-eqz v2, :cond_1

    #@1f
    .line 643
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getRootView()Landroid/view/View;

    #@22
    move-result-object v1

    #@23
    .line 644
    .local v1, "rootView":Landroid/view/View;
    if-eqz v1, :cond_1

    #@25
    .line 645
    const/16 v2, 0x20

    #@27
    invoke-virtual {v1, v2}, Landroid/view/View;->sendAccessibilityEvent(I)V

    #@2a
    .line 649
    .end local v1    # "rootView":Landroid/view/View;
    :cond_1
    return-void
.end method

.method dispatchOnDrawerOpened(Landroid/view/View;)V
    .locals 3
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 652
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v0

    #@5
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@7
    .line 653
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget-boolean v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@9
    if-nez v1, :cond_1

    #@b
    .line 654
    iput-boolean v2, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@d
    .line 655
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@f
    if-eqz v1, :cond_0

    #@11
    .line 656
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@13
    invoke-interface {v1, p1}, Landroid/support/v4/widget/DrawerLayout$DrawerListener;->onDrawerOpened(Landroid/view/View;)V

    #@16
    .line 659
    :cond_0
    invoke-direct {p0, p1, v2}, Landroid/support/v4/widget/DrawerLayout;->updateChildrenImportantForAccessibility(Landroid/view/View;Z)V

    #@19
    .line 661
    invoke-virtual {p1}, Landroid/view/View;->requestFocus()Z

    #@1c
    .line 663
    :cond_1
    return-void
.end method

.method dispatchOnDrawerSlide(Landroid/view/View;F)V
    .locals 1
    .param p1, "drawerView"    # Landroid/view/View;
    .param p2, "slideOffset"    # F

    #@0
    .prologue
    .line 683
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 684
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@6
    invoke-interface {v0, p1, p2}, Landroid/support/v4/widget/DrawerLayout$DrawerListener;->onDrawerSlide(Landroid/view/View;F)V

    #@9
    .line 686
    :cond_0
    return-void
.end method

.method protected drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z
    .locals 29
    .param p1, "canvas"    # Landroid/graphics/Canvas;
    .param p2, "child"    # Landroid/view/View;
    .param p3, "drawingTime"    # J

    #@0
    .prologue
    .line 1035
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getHeight()I

    #@3
    move-result v18

    #@4
    .line 1036
    .local v18, "height":I
    move-object/from16 v0, p0

    #@6
    move-object/from16 v1, p2

    #@8
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/DrawerLayout;->isContentView(Landroid/view/View;)Z

    #@b
    move-result v17

    #@c
    .line 1037
    .local v17, "drawingContent":Z
    const/4 v13, 0x0

    #@d
    .local v13, "clipLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@10
    move-result v14

    #@11
    .line 1039
    .local v14, "clipRight":I
    invoke-virtual/range {p1 .. p1}, Landroid/graphics/Canvas;->save()I

    #@14
    move-result v21

    #@15
    .line 1040
    .local v21, "restoreCount":I
    if-eqz v17, :cond_4

    #@17
    .line 1041
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@1a
    move-result v10

    #@1b
    .line 1042
    .local v10, "childCount":I
    const/16 v19, 0x0

    #@1d
    .local v19, "i":I
    :goto_0
    move/from16 v0, v19

    #@1f
    if-ge v0, v10, :cond_3

    #@21
    .line 1043
    move-object/from16 v0, p0

    #@23
    move/from16 v1, v19

    #@25
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@28
    move-result-object v25

    #@29
    .line 1044
    .local v25, "v":Landroid/view/View;
    move-object/from16 v0, v25

    #@2b
    move-object/from16 v1, p2

    #@2d
    if-eq v0, v1, :cond_0

    #@2f
    invoke-virtual/range {v25 .. v25}, Landroid/view/View;->getVisibility()I

    #@32
    move-result v2

    #@33
    if-nez v2, :cond_0

    #@35
    invoke-static/range {v25 .. v25}, Landroid/support/v4/widget/DrawerLayout;->hasOpaqueBackground(Landroid/view/View;)Z

    #@38
    move-result v2

    #@39
    if-eqz v2, :cond_0

    #@3b
    move-object/from16 v0, p0

    #@3d
    move-object/from16 v1, v25

    #@3f
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@42
    move-result v2

    #@43
    if-eqz v2, :cond_0

    #@45
    invoke-virtual/range {v25 .. v25}, Landroid/view/View;->getHeight()I

    #@48
    move-result v2

    #@49
    move/from16 v0, v18

    #@4b
    if-ge v2, v0, :cond_1

    #@4d
    .line 1042
    :cond_0
    :goto_1
    add-int/lit8 v19, v19, 0x1

    #@4f
    goto :goto_0

    #@50
    .line 1050
    :cond_1
    const/4 v2, 0x3

    #@51
    move-object/from16 v0, p0

    #@53
    move-object/from16 v1, v25

    #@55
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@58
    move-result v2

    #@59
    if-eqz v2, :cond_2

    #@5b
    .line 1051
    invoke-virtual/range {v25 .. v25}, Landroid/view/View;->getRight()I

    #@5e
    move-result v27

    #@5f
    .line 1052
    .local v27, "vright":I
    move/from16 v0, v27

    #@61
    if-le v0, v13, :cond_0

    #@63
    move/from16 v13, v27

    #@65
    goto :goto_1

    #@66
    .line 1054
    .end local v27    # "vright":I
    :cond_2
    invoke-virtual/range {v25 .. v25}, Landroid/view/View;->getLeft()I

    #@69
    move-result v26

    #@6a
    .line 1055
    .local v26, "vleft":I
    move/from16 v0, v26

    #@6c
    if-ge v0, v14, :cond_0

    #@6e
    move/from16 v14, v26

    #@70
    goto :goto_1

    #@71
    .line 1058
    .end local v25    # "v":Landroid/view/View;
    .end local v26    # "vleft":I
    :cond_3
    const/4 v2, 0x0

    #@72
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getHeight()I

    #@75
    move-result v3

    #@76
    move-object/from16 v0, p1

    #@78
    invoke-virtual {v0, v13, v2, v14, v3}, Landroid/graphics/Canvas;->clipRect(IIII)Z

    #@7b
    .line 1060
    .end local v10    # "childCount":I
    .end local v19    # "i":I
    :cond_4
    invoke-super/range {p0 .. p4}, Landroid/view/ViewGroup;->drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z

    #@7e
    move-result v22

    #@7f
    .line 1061
    .local v22, "result":Z
    move-object/from16 v0, p1

    #@81
    move/from16 v1, v21

    #@83
    invoke-virtual {v0, v1}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@86
    .line 1063
    move-object/from16 v0, p0

    #@88
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimOpacity:F

    #@8a
    const/4 v3, 0x0

    #@8b
    cmpl-float v2, v2, v3

    #@8d
    if-lez v2, :cond_6

    #@8f
    if-eqz v17, :cond_6

    #@91
    .line 1064
    move-object/from16 v0, p0

    #@93
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimColor:I

    #@95
    const/high16 v3, -0x1000000

    #@97
    and-int/2addr v2, v3

    #@98
    ushr-int/lit8 v9, v2, 0x18

    #@9a
    .line 1065
    .local v9, "baseAlpha":I
    int-to-float v2, v9

    #@9b
    move-object/from16 v0, p0

    #@9d
    iget v3, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimOpacity:F

    #@9f
    mul-float/2addr v2, v3

    #@a0
    float-to-int v0, v2

    #@a1
    move/from16 v20, v0

    #@a3
    .line 1066
    .local v20, "imag":I
    shl-int/lit8 v2, v20, 0x18

    #@a5
    move-object/from16 v0, p0

    #@a7
    iget v3, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimColor:I

    #@a9
    const v4, 0xffffff

    #@ac
    and-int/2addr v3, v4

    #@ad
    or-int v15, v2, v3

    #@af
    .line 1067
    .local v15, "color":I
    move-object/from16 v0, p0

    #@b1
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimPaint:Landroid/graphics/Paint;

    #@b3
    invoke-virtual {v2, v15}, Landroid/graphics/Paint;->setColor(I)V

    #@b6
    .line 1069
    int-to-float v3, v13

    #@b7
    const/4 v4, 0x0

    #@b8
    int-to-float v5, v14

    #@b9
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getHeight()I

    #@bc
    move-result v2

    #@bd
    int-to-float v6, v2

    #@be
    move-object/from16 v0, p0

    #@c0
    iget-object v7, v0, Landroid/support/v4/widget/DrawerLayout;->mScrimPaint:Landroid/graphics/Paint;

    #@c2
    move-object/from16 v2, p1

    #@c4
    invoke-virtual/range {v2 .. v7}, Landroid/graphics/Canvas;->drawRect(FFFFLandroid/graphics/Paint;)V

    #@c7
    .line 1092
    .end local v9    # "baseAlpha":I
    .end local v15    # "color":I
    .end local v20    # "imag":I
    :cond_5
    :goto_2
    return v22

    #@c8
    .line 1070
    :cond_6
    move-object/from16 v0, p0

    #@ca
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@cc
    if-eqz v2, :cond_7

    #@ce
    const/4 v2, 0x3

    #@cf
    move-object/from16 v0, p0

    #@d1
    move-object/from16 v1, p2

    #@d3
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@d6
    move-result v2

    #@d7
    if-eqz v2, :cond_7

    #@d9
    .line 1071
    move-object/from16 v0, p0

    #@db
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@dd
    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@e0
    move-result v23

    #@e1
    .line 1072
    .local v23, "shadowWidth":I
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getRight()I

    #@e4
    move-result v12

    #@e5
    .line 1073
    .local v12, "childRight":I
    move-object/from16 v0, p0

    #@e7
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@e9
    invoke-virtual {v2}, Landroid/support/v4/widget/ViewDragHelper;->getEdgeSize()I

    #@ec
    move-result v16

    #@ed
    .line 1074
    .local v16, "drawerPeekDistance":I
    const/4 v2, 0x0

    #@ee
    int-to-float v3, v12

    #@ef
    move/from16 v0, v16

    #@f1
    int-to-float v4, v0

    #@f2
    div-float/2addr v3, v4

    #@f3
    const/high16 v4, 0x3f800000    # 1.0f

    #@f5
    invoke-static {v3, v4}, Ljava/lang/Math;->min(FF)F

    #@f8
    move-result v3

    #@f9
    invoke-static {v2, v3}, Ljava/lang/Math;->max(FF)F

    #@fc
    move-result v8

    #@fd
    .line 1076
    .local v8, "alpha":F
    move-object/from16 v0, p0

    #@ff
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@101
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getTop()I

    #@104
    move-result v3

    #@105
    add-int v4, v12, v23

    #@107
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getBottom()I

    #@10a
    move-result v5

    #@10b
    invoke-virtual {v2, v12, v3, v4, v5}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@10e
    .line 1078
    move-object/from16 v0, p0

    #@110
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@112
    const/high16 v3, 0x437f0000    # 255.0f

    #@114
    mul-float/2addr v3, v8

    #@115
    float-to-int v3, v3

    #@116
    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    #@119
    .line 1079
    move-object/from16 v0, p0

    #@11b
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@11d
    move-object/from16 v0, p1

    #@11f
    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@122
    goto :goto_2

    #@123
    .line 1080
    .end local v8    # "alpha":F
    .end local v12    # "childRight":I
    .end local v16    # "drawerPeekDistance":I
    .end local v23    # "shadowWidth":I
    :cond_7
    move-object/from16 v0, p0

    #@125
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@127
    if-eqz v2, :cond_5

    #@129
    const/4 v2, 0x5

    #@12a
    move-object/from16 v0, p0

    #@12c
    move-object/from16 v1, p2

    #@12e
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@131
    move-result v2

    #@132
    if-eqz v2, :cond_5

    #@134
    .line 1081
    move-object/from16 v0, p0

    #@136
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@138
    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@13b
    move-result v23

    #@13c
    .line 1082
    .restart local v23    # "shadowWidth":I
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getLeft()I

    #@13f
    move-result v11

    #@140
    .line 1083
    .local v11, "childLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@143
    move-result v2

    #@144
    sub-int v24, v2, v11

    #@146
    .line 1084
    .local v24, "showing":I
    move-object/from16 v0, p0

    #@148
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@14a
    invoke-virtual {v2}, Landroid/support/v4/widget/ViewDragHelper;->getEdgeSize()I

    #@14d
    move-result v16

    #@14e
    .line 1085
    .restart local v16    # "drawerPeekDistance":I
    const/4 v2, 0x0

    #@14f
    move/from16 v0, v24

    #@151
    int-to-float v3, v0

    #@152
    move/from16 v0, v16

    #@154
    int-to-float v4, v0

    #@155
    div-float/2addr v3, v4

    #@156
    const/high16 v4, 0x3f800000    # 1.0f

    #@158
    invoke-static {v3, v4}, Ljava/lang/Math;->min(FF)F

    #@15b
    move-result v3

    #@15c
    invoke-static {v2, v3}, Ljava/lang/Math;->max(FF)F

    #@15f
    move-result v8

    #@160
    .line 1087
    .restart local v8    # "alpha":F
    move-object/from16 v0, p0

    #@162
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@164
    sub-int v3, v11, v23

    #@166
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getTop()I

    #@169
    move-result v4

    #@16a
    invoke-virtual/range {p2 .. p2}, Landroid/view/View;->getBottom()I

    #@16d
    move-result v5

    #@16e
    invoke-virtual {v2, v3, v4, v11, v5}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@171
    .line 1089
    move-object/from16 v0, p0

    #@173
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@175
    const/high16 v3, 0x437f0000    # 255.0f

    #@177
    mul-float/2addr v3, v8

    #@178
    float-to-int v3, v3

    #@179
    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    #@17c
    .line 1090
    move-object/from16 v0, p0

    #@17e
    iget-object v2, v0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@180
    move-object/from16 v0, p1

    #@182
    invoke-virtual {v2, v0}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@185
    goto/16 :goto_2
.end method

.method findDrawerWithGravity(I)Landroid/view/View;
    .locals 6
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 746
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v5

    #@4
    invoke-static {p1, v5}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v5

    #@8
    and-int/lit8 v0, v5, 0x7

    #@a
    .line 748
    .local v0, "absHorizGravity":I
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@d
    move-result v3

    #@e
    .line 749
    .local v3, "childCount":I
    const/4 v4, 0x0

    #@f
    .local v4, "i":I
    :goto_0
    if-ge v4, v3, :cond_1

    #@11
    .line 750
    invoke-virtual {p0, v4}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@14
    move-result-object v1

    #@15
    .line 751
    .local v1, "child":Landroid/view/View;
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewAbsoluteGravity(Landroid/view/View;)I

    #@18
    move-result v2

    #@19
    .line 752
    .local v2, "childAbsGravity":I
    and-int/lit8 v5, v2, 0x7

    #@1b
    if-ne v5, v0, :cond_0

    #@1d
    .line 756
    .end local v1    # "child":Landroid/view/View;
    .end local v2    # "childAbsGravity":I
    :goto_1
    return-object v1

    #@1e
    .line 749
    .restart local v1    # "child":Landroid/view/View;
    .restart local v2    # "childAbsGravity":I
    :cond_0
    add-int/lit8 v4, v4, 0x1

    #@20
    goto :goto_0

    #@21
    .line 756
    .end local v1    # "child":Landroid/view/View;
    .end local v2    # "childAbsGravity":I
    :cond_1
    const/4 v1, 0x0

    #@22
    goto :goto_1
.end method

.method findOpenDrawer()Landroid/view/View;
    .locals 4

    #@0
    .prologue
    .line 717
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@3
    move-result v1

    #@4
    .line 718
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@5
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_1

    #@7
    .line 719
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 720
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@e
    move-result-object v3

    #@f
    check-cast v3, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@11
    iget-boolean v3, v3, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@13
    if-eqz v3, :cond_0

    #@15
    .line 724
    .end local v0    # "child":Landroid/view/View;
    :goto_1
    return-object v0

    #@16
    .line 718
    .restart local v0    # "child":Landroid/view/View;
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@18
    goto :goto_0

    #@19
    .line 724
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    const/4 v0, 0x0

    #@1a
    goto :goto_1
.end method

.method protected generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 2

    #@0
    .prologue
    const/4 v1, -0x1

    #@1
    .line 1417
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@3
    invoke-direct {v0, v1, v1}, Landroid/support/v4/widget/DrawerLayout$LayoutParams;-><init>(II)V

    #@6
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1436
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v4/widget/DrawerLayout$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1422
    instance-of v0, p1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2
    if-eqz v0, :cond_0

    #@4
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    check-cast p1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@8
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v4/widget/DrawerLayout$LayoutParams;-><init>(Landroid/support/v4/widget/DrawerLayout$LayoutParams;)V

    #@b
    :goto_0
    return-object v0

    #@c
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_0
    instance-of v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@e
    if-eqz v0, :cond_1

    #@10
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@12
    check-cast p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@14
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v4/widget/DrawerLayout$LayoutParams;-><init>(Landroid/view/ViewGroup$MarginLayoutParams;)V

    #@17
    goto :goto_0

    #@18
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_1
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@1a
    invoke-direct {v0, p1}, Landroid/support/v4/widget/DrawerLayout$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@1d
    goto :goto_0
.end method

.method public getDrawerLockMode(I)I
    .locals 2
    .param p1, "edgeGravity"    # I

    #@0
    .prologue
    .line 526
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v1

    #@4
    invoke-static {p1, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v0

    #@8
    .line 528
    .local v0, "absGravity":I
    const/4 v1, 0x3

    #@9
    if-ne v0, v1, :cond_0

    #@b
    .line 529
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeLeft:I

    #@d
    .line 533
    :goto_0
    return v1

    #@e
    .line 530
    :cond_0
    const/4 v1, 0x5

    #@f
    if-ne v0, v1, :cond_1

    #@11
    .line 531
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeRight:I

    #@13
    goto :goto_0

    #@14
    .line 533
    :cond_1
    const/4 v1, 0x0

    #@15
    goto :goto_0
.end method

.method public getDrawerLockMode(Landroid/view/View;)I
    .locals 2
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    .line 545
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewAbsoluteGravity(Landroid/view/View;)I

    #@3
    move-result v0

    #@4
    .line 546
    .local v0, "absGravity":I
    const/4 v1, 0x3

    #@5
    if-ne v0, v1, :cond_0

    #@7
    .line 547
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeLeft:I

    #@9
    .line 551
    :goto_0
    return v1

    #@a
    .line 548
    :cond_0
    const/4 v1, 0x5

    #@b
    if-ne v0, v1, :cond_1

    #@d
    .line 549
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeRight:I

    #@f
    goto :goto_0

    #@10
    .line 551
    :cond_1
    const/4 v1, 0x0

    #@11
    goto :goto_0
.end method

.method public getDrawerTitle(I)Ljava/lang/CharSequence;
    .locals 2
    .param p1, "edgeGravity"    # I
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    #@0
    .prologue
    .line 584
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v1

    #@4
    invoke-static {p1, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v0

    #@8
    .line 586
    .local v0, "absGravity":I
    const/4 v1, 0x3

    #@9
    if-ne v0, v1, :cond_0

    #@b
    .line 587
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mTitleLeft:Ljava/lang/CharSequence;

    #@d
    .line 591
    :goto_0
    return-object v1

    #@e
    .line 588
    :cond_0
    const/4 v1, 0x5

    #@f
    if-ne v0, v1, :cond_1

    #@11
    .line 589
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mTitleRight:Ljava/lang/CharSequence;

    #@13
    goto :goto_0

    #@14
    .line 591
    :cond_1
    const/4 v1, 0x0

    #@15
    goto :goto_0
.end method

.method getDrawerViewAbsoluteGravity(Landroid/view/View;)I
    .locals 2
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    .line 707
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v1

    #@4
    check-cast v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    iget v0, v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@8
    .line 708
    .local v0, "gravity":I
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@b
    move-result v1

    #@c
    invoke-static {v0, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@f
    move-result v1

    #@10
    return v1
.end method

.method getDrawerViewOffset(Landroid/view/View;)F
    .locals 1
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    .line 699
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@8
    return v0
.end method

.method isContentView(Landroid/view/View;)Z
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1096
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@8
    if-nez v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public isDrawerOpen(I)Z
    .locals 2
    .param p1, "drawerGravity"    # I

    #@0
    .prologue
    .line 1366
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 1367
    .local v0, "drawerView":Landroid/view/View;
    if-eqz v0, :cond_0

    #@6
    .line 1368
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerOpen(Landroid/view/View;)Z

    #@9
    move-result v1

    #@a
    .line 1370
    :goto_0
    return v1

    #@b
    :cond_0
    const/4 v1, 0x0

    #@c
    goto :goto_0
.end method

.method public isDrawerOpen(Landroid/view/View;)Z
    .locals 3
    .param p1, "drawer"    # Landroid/view/View;

    #@0
    .prologue
    .line 1350
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 1351
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v1, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v2, "View "

    #@f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v1

    #@17
    const-string v2, " is not a drawer"

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v1

    #@21
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@24
    throw v0

    #@25
    .line 1353
    :cond_0
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@28
    move-result-object v0

    #@29
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2b
    iget-boolean v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@2d
    return v0
.end method

.method isDrawerView(Landroid/view/View;)Z
    .locals 3
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1100
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v2

    #@4
    check-cast v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    iget v1, v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@8
    .line 1101
    .local v1, "gravity":I
    invoke-static {p1}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@b
    move-result v2

    #@c
    invoke-static {v1, v2}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@f
    move-result v0

    #@10
    .line 1103
    .local v0, "absGravity":I
    and-int/lit8 v2, v0, 0x7

    #@12
    if-eqz v2, :cond_0

    #@14
    const/4 v2, 0x1

    #@15
    :goto_0
    return v2

    #@16
    :cond_0
    const/4 v2, 0x0

    #@17
    goto :goto_0
.end method

.method public isDrawerVisible(I)Z
    .locals 2
    .param p1, "drawerGravity"    # I

    #@0
    .prologue
    .line 1397
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 1398
    .local v0, "drawerView":Landroid/view/View;
    if-eqz v0, :cond_0

    #@6
    .line 1399
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->isDrawerVisible(Landroid/view/View;)Z

    #@9
    move-result v1

    #@a
    .line 1401
    :goto_0
    return v1

    #@b
    :cond_0
    const/4 v1, 0x0

    #@c
    goto :goto_0
.end method

.method public isDrawerVisible(Landroid/view/View;)Z
    .locals 3
    .param p1, "drawer"    # Landroid/view/View;

    #@0
    .prologue
    .line 1382
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 1383
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v1, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v2, "View "

    #@f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v1

    #@17
    const-string v2, " is not a drawer"

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v1

    #@21
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@24
    throw v0

    #@25
    .line 1385
    :cond_0
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@28
    move-result-object v0

    #@29
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2b
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@2d
    const/4 v1, 0x0

    #@2e
    cmpl-float v0, v0, v1

    #@30
    if-lez v0, :cond_1

    #@32
    const/4 v0, 0x1

    #@33
    :goto_0
    return v0

    #@34
    :cond_1
    const/4 v0, 0x0

    #@35
    goto :goto_0
.end method

.method moveDrawerToOffset(Landroid/view/View;F)V
    .locals 6
    .param p1, "drawerView"    # Landroid/view/View;
    .param p2, "slideOffset"    # F

    #@0
    .prologue
    .line 728
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewOffset(Landroid/view/View;)F

    #@3
    move-result v2

    #@4
    .line 729
    .local v2, "oldOffset":F
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@7
    move-result v4

    #@8
    .line 730
    .local v4, "width":I
    int-to-float v5, v4

    #@9
    mul-float/2addr v5, v2

    #@a
    float-to-int v3, v5

    #@b
    .line 731
    .local v3, "oldPos":I
    int-to-float v5, v4

    #@c
    mul-float/2addr v5, p2

    #@d
    float-to-int v1, v5

    #@e
    .line 732
    .local v1, "newPos":I
    sub-int v0, v1, v3

    #@10
    .line 734
    .local v0, "dx":I
    const/4 v5, 0x3

    #@11
    invoke-virtual {p0, p1, v5}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@14
    move-result v5

    #@15
    if-eqz v5, :cond_0

    #@17
    .end local v0    # "dx":I
    :goto_0
    invoke-virtual {p1, v0}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@1a
    .line 736
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/widget/DrawerLayout;->setDrawerViewOffset(Landroid/view/View;F)V

    #@1d
    .line 737
    return-void

    #@1e
    .line 734
    .restart local v0    # "dx":I
    :cond_0
    neg-int v0, v0

    #@1f
    goto :goto_0
.end method

.method protected onAttachedToWindow()V
    .locals 1

    #@0
    .prologue
    .line 783
    invoke-super {p0}, Landroid/view/ViewGroup;->onAttachedToWindow()V

    #@3
    .line 784
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@6
    .line 785
    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 777
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@3
    .line 778
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@6
    .line 779
    return-void
.end method

.method public onDraw(Landroid/graphics/Canvas;)V
    .locals 4
    .param p1, "c"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1023
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onDraw(Landroid/graphics/Canvas;)V

    #@4
    .line 1024
    iget-boolean v1, p0, Landroid/support/v4/widget/DrawerLayout;->mDrawStatusBarBackground:Z

    #@6
    if-eqz v1, :cond_0

    #@8
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 1025
    sget-object v1, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@e
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout;->mLastInsets:Ljava/lang/Object;

    #@10
    invoke-interface {v1, v2}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;->getTopInset(Ljava/lang/Object;)I

    #@13
    move-result v0

    #@14
    .line 1026
    .local v0, "inset":I
    if-lez v0, :cond_0

    #@16
    .line 1027
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@18
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@1b
    move-result v2

    #@1c
    invoke-virtual {v1, v3, v3, v2, v0}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@1f
    .line 1028
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@21
    invoke-virtual {v1, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@24
    .line 1031
    .end local v0    # "inset":I
    :cond_0
    return-void
.end method

.method public onInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 11
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 1108
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@5
    move-result v0

    #@6
    .line 1111
    .local v0, "action":I
    iget-object v8, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@8
    invoke-virtual {v8, p1}, Landroid/support/v4/widget/ViewDragHelper;->shouldInterceptTouchEvent(Landroid/view/MotionEvent;)Z

    #@b
    move-result v8

    #@c
    iget-object v9, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@e
    invoke-virtual {v9, p1}, Landroid/support/v4/widget/ViewDragHelper;->shouldInterceptTouchEvent(Landroid/view/MotionEvent;)Z

    #@11
    move-result v9

    #@12
    or-int v2, v8, v9

    #@14
    .line 1114
    .local v2, "interceptForDrag":Z
    const/4 v3, 0x0

    #@15
    .line 1116
    .local v3, "interceptForTap":Z
    packed-switch v0, :pswitch_data_0

    #@18
    .line 1150
    :cond_0
    :goto_0
    if-nez v2, :cond_1

    #@1a
    if-nez v3, :cond_1

    #@1c
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout;->hasPeekingDrawer()Z

    #@1f
    move-result v8

    #@20
    if-nez v8, :cond_1

    #@22
    iget-boolean v8, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@24
    if-eqz v8, :cond_2

    #@26
    :cond_1
    move v6, v7

    #@27
    :cond_2
    return v6

    #@28
    .line 1118
    :pswitch_0
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@2b
    move-result v4

    #@2c
    .line 1119
    .local v4, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@2f
    move-result v5

    #@30
    .line 1120
    .local v5, "y":F
    iput v4, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionX:F

    #@32
    .line 1121
    iput v5, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionY:F

    #@34
    .line 1122
    iget v8, p0, Landroid/support/v4/widget/DrawerLayout;->mScrimOpacity:F

    #@36
    const/4 v9, 0x0

    #@37
    cmpl-float v8, v8, v9

    #@39
    if-lez v8, :cond_3

    #@3b
    .line 1123
    iget-object v8, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@3d
    float-to-int v9, v4

    #@3e
    float-to-int v10, v5

    #@3f
    invoke-virtual {v8, v9, v10}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@42
    move-result-object v1

    #@43
    .line 1124
    .local v1, "child":Landroid/view/View;
    if-eqz v1, :cond_3

    #@45
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/DrawerLayout;->isContentView(Landroid/view/View;)Z

    #@48
    move-result v8

    #@49
    if-eqz v8, :cond_3

    #@4b
    .line 1125
    const/4 v3, 0x1

    #@4c
    .line 1128
    .end local v1    # "child":Landroid/view/View;
    :cond_3
    iput-boolean v6, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@4e
    .line 1129
    iput-boolean v6, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@50
    goto :goto_0

    #@51
    .line 1135
    .end local v4    # "x":F
    .end local v5    # "y":F
    :pswitch_1
    iget-object v8, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@53
    const/4 v9, 0x3

    #@54
    invoke-virtual {v8, v9}, Landroid/support/v4/widget/ViewDragHelper;->checkTouchSlop(I)Z

    #@57
    move-result v8

    #@58
    if-eqz v8, :cond_0

    #@5a
    .line 1136
    iget-object v8, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@5c
    invoke-virtual {v8}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->removeCallbacks()V

    #@5f
    .line 1137
    iget-object v8, p0, Landroid/support/v4/widget/DrawerLayout;->mRightCallback:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@61
    invoke-virtual {v8}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->removeCallbacks()V

    #@64
    goto :goto_0

    #@65
    .line 1144
    :pswitch_2
    invoke-virtual {p0, v7}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers(Z)V

    #@68
    .line 1145
    iput-boolean v6, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@6a
    .line 1146
    iput-boolean v6, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@6c
    goto :goto_0

    #@6d
    .line 1116
    nop

    #@6e
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_2
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 1471
    const/4 v0, 0x4

    #@1
    if-ne p1, v0, :cond_0

    #@3
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout;->hasVisibleDrawer()Z

    #@6
    move-result v0

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 1472
    invoke-static {p2}, Landroid/support/v4/view/KeyEventCompat;->startTracking(Landroid/view/KeyEvent;)V

    #@c
    .line 1473
    const/4 v0, 0x1

    #@d
    .line 1475
    :goto_0
    return v0

    #@e
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/view/ViewGroup;->onKeyDown(ILandroid/view/KeyEvent;)Z

    #@11
    move-result v0

    #@12
    goto :goto_0
.end method

.method public onKeyUp(ILandroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 1480
    const/4 v1, 0x4

    #@1
    if-ne p1, v1, :cond_2

    #@3
    .line 1481
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout;->findVisibleDrawer()Landroid/view/View;

    #@6
    move-result-object v0

    #@7
    .line 1482
    .local v0, "visibleDrawer":Landroid/view/View;
    if-eqz v0, :cond_0

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->getDrawerLockMode(Landroid/view/View;)I

    #@c
    move-result v1

    #@d
    if-nez v1, :cond_0

    #@f
    .line 1483
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers()V

    #@12
    .line 1485
    :cond_0
    if-eqz v0, :cond_1

    #@14
    const/4 v1, 0x1

    #@15
    .line 1487
    .end local v0    # "visibleDrawer":Landroid/view/View;
    :goto_0
    return v1

    #@16
    .line 1485
    .restart local v0    # "visibleDrawer":Landroid/view/View;
    :cond_1
    const/4 v1, 0x0

    #@17
    goto :goto_0

    #@18
    .line 1487
    .end local v0    # "visibleDrawer":Landroid/view/View;
    :cond_2
    invoke-super {p0, p1, p2}, Landroid/view/ViewGroup;->onKeyUp(ILandroid/view/KeyEvent;)Z

    #@1b
    move-result v1

    #@1c
    goto :goto_0
.end method

.method protected onLayout(ZIIII)V
    .locals 23
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 877
    const/16 v18, 0x1

    #@2
    move/from16 v0, v18

    #@4
    move-object/from16 v1, p0

    #@6
    iput-boolean v0, v1, Landroid/support/v4/widget/DrawerLayout;->mInLayout:Z

    #@8
    .line 878
    sub-int v17, p4, p2

    #@a
    .line 879
    .local v17, "width":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@d
    move-result v6

    #@e
    .line 880
    .local v6, "childCount":I
    const/4 v12, 0x0

    #@f
    .local v12, "i":I
    :goto_0
    if-ge v12, v6, :cond_9

    #@11
    .line 881
    move-object/from16 v0, p0

    #@13
    invoke-virtual {v0, v12}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@16
    move-result-object v5

    #@17
    .line 883
    .local v5, "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@1a
    move-result v18

    #@1b
    const/16 v19, 0x8

    #@1d
    move/from16 v0, v18

    #@1f
    move/from16 v1, v19

    #@21
    if-ne v0, v1, :cond_1

    #@23
    .line 880
    :cond_0
    :goto_1
    add-int/lit8 v12, v12, 0x1

    #@25
    goto :goto_0

    #@26
    .line 887
    :cond_1
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@29
    move-result-object v13

    #@2a
    check-cast v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@2c
    .line 889
    .local v13, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    move-object/from16 v0, p0

    #@2e
    invoke-virtual {v0, v5}, Landroid/support/v4/widget/DrawerLayout;->isContentView(Landroid/view/View;)Z

    #@31
    move-result v18

    #@32
    if-eqz v18, :cond_2

    #@34
    .line 890
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->leftMargin:I

    #@36
    move/from16 v18, v0

    #@38
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@3a
    move/from16 v19, v0

    #@3c
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->leftMargin:I

    #@3e
    move/from16 v20, v0

    #@40
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@43
    move-result v21

    #@44
    add-int v20, v20, v21

    #@46
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@48
    move/from16 v21, v0

    #@4a
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@4d
    move-result v22

    #@4e
    add-int v21, v21, v22

    #@50
    move/from16 v0, v18

    #@52
    move/from16 v1, v19

    #@54
    move/from16 v2, v20

    #@56
    move/from16 v3, v21

    #@58
    invoke-virtual {v5, v0, v1, v2, v3}, Landroid/view/View;->layout(IIII)V

    #@5b
    goto :goto_1

    #@5c
    .line 894
    :cond_2
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@5f
    move-result v10

    #@60
    .line 895
    .local v10, "childWidth":I
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@63
    move-result v7

    #@64
    .line 899
    .local v7, "childHeight":I
    const/16 v18, 0x3

    #@66
    move-object/from16 v0, p0

    #@68
    move/from16 v1, v18

    #@6a
    invoke-virtual {v0, v5, v1}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@6d
    move-result v18

    #@6e
    if-eqz v18, :cond_4

    #@70
    .line 900
    neg-int v0, v10

    #@71
    move/from16 v18, v0

    #@73
    int-to-float v0, v10

    #@74
    move/from16 v19, v0

    #@76
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@78
    move/from16 v20, v0

    #@7a
    mul-float v19, v19, v20

    #@7c
    move/from16 v0, v19

    #@7e
    float-to-int v0, v0

    #@7f
    move/from16 v19, v0

    #@81
    add-int v8, v18, v19

    #@83
    .line 901
    .local v8, "childLeft":I
    add-int v18, v10, v8

    #@85
    move/from16 v0, v18

    #@87
    int-to-float v0, v0

    #@88
    move/from16 v18, v0

    #@8a
    int-to-float v0, v10

    #@8b
    move/from16 v19, v0

    #@8d
    div-float v14, v18, v19

    #@8f
    .line 907
    .local v14, "newOffset":F
    :goto_2
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@91
    move/from16 v18, v0

    #@93
    cmpl-float v18, v14, v18

    #@95
    if-eqz v18, :cond_5

    #@97
    const/4 v4, 0x1

    #@98
    .line 909
    .local v4, "changeOffset":Z
    :goto_3
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@9a
    move/from16 v18, v0

    #@9c
    and-int/lit8 v16, v18, 0x70

    #@9e
    .line 911
    .local v16, "vgrav":I
    sparse-switch v16, :sswitch_data_0

    #@a1
    .line 914
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@a3
    move/from16 v18, v0

    #@a5
    add-int v19, v8, v10

    #@a7
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@a9
    move/from16 v20, v0

    #@ab
    add-int v20, v20, v7

    #@ad
    move/from16 v0, v18

    #@af
    move/from16 v1, v19

    #@b1
    move/from16 v2, v20

    #@b3
    invoke-virtual {v5, v8, v0, v1, v2}, Landroid/view/View;->layout(IIII)V

    #@b6
    .line 945
    :goto_4
    if-eqz v4, :cond_3

    #@b8
    .line 946
    move-object/from16 v0, p0

    #@ba
    invoke-virtual {v0, v5, v14}, Landroid/support/v4/widget/DrawerLayout;->setDrawerViewOffset(Landroid/view/View;F)V

    #@bd
    .line 949
    :cond_3
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@bf
    move/from16 v18, v0

    #@c1
    const/16 v19, 0x0

    #@c3
    cmpl-float v18, v18, v19

    #@c5
    if-lez v18, :cond_8

    #@c7
    const/4 v15, 0x0

    #@c8
    .line 950
    .local v15, "newVisibility":I
    :goto_5
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@cb
    move-result v18

    #@cc
    move/from16 v0, v18

    #@ce
    if-eq v0, v15, :cond_0

    #@d0
    .line 951
    invoke-virtual {v5, v15}, Landroid/view/View;->setVisibility(I)V

    #@d3
    goto/16 :goto_1

    #@d5
    .line 903
    .end local v4    # "changeOffset":Z
    .end local v8    # "childLeft":I
    .end local v14    # "newOffset":F
    .end local v15    # "newVisibility":I
    .end local v16    # "vgrav":I
    :cond_4
    int-to-float v0, v10

    #@d6
    move/from16 v18, v0

    #@d8
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@da
    move/from16 v19, v0

    #@dc
    mul-float v18, v18, v19

    #@de
    move/from16 v0, v18

    #@e0
    float-to-int v0, v0

    #@e1
    move/from16 v18, v0

    #@e3
    sub-int v8, v17, v18

    #@e5
    .line 904
    .restart local v8    # "childLeft":I
    sub-int v18, v17, v8

    #@e7
    move/from16 v0, v18

    #@e9
    int-to-float v0, v0

    #@ea
    move/from16 v18, v0

    #@ec
    int-to-float v0, v10

    #@ed
    move/from16 v19, v0

    #@ef
    div-float v14, v18, v19

    #@f1
    .restart local v14    # "newOffset":F
    goto :goto_2

    #@f2
    .line 907
    :cond_5
    const/4 v4, 0x0

    #@f3
    goto :goto_3

    #@f4
    .line 920
    .restart local v4    # "changeOffset":Z
    .restart local v16    # "vgrav":I
    :sswitch_0
    sub-int v11, p5, p3

    #@f6
    .line 921
    .local v11, "height":I
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@f8
    move/from16 v18, v0

    #@fa
    sub-int v18, v11, v18

    #@fc
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@ff
    move-result v19

    #@100
    sub-int v18, v18, v19

    #@102
    add-int v19, v8, v10

    #@104
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@106
    move/from16 v20, v0

    #@108
    sub-int v20, v11, v20

    #@10a
    move/from16 v0, v18

    #@10c
    move/from16 v1, v19

    #@10e
    move/from16 v2, v20

    #@110
    invoke-virtual {v5, v8, v0, v1, v2}, Landroid/view/View;->layout(IIII)V

    #@113
    goto :goto_4

    #@114
    .line 929
    .end local v11    # "height":I
    :sswitch_1
    sub-int v11, p5, p3

    #@116
    .line 930
    .restart local v11    # "height":I
    sub-int v18, v11, v7

    #@118
    div-int/lit8 v9, v18, 0x2

    #@11a
    .line 934
    .local v9, "childTop":I
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@11c
    move/from16 v18, v0

    #@11e
    move/from16 v0, v18

    #@120
    if-ge v9, v0, :cond_7

    #@122
    .line 935
    iget v9, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@124
    .line 939
    :cond_6
    :goto_6
    add-int v18, v8, v10

    #@126
    add-int v19, v9, v7

    #@128
    move/from16 v0, v18

    #@12a
    move/from16 v1, v19

    #@12c
    invoke-virtual {v5, v8, v9, v0, v1}, Landroid/view/View;->layout(IIII)V

    #@12f
    goto :goto_4

    #@130
    .line 936
    :cond_7
    add-int v18, v9, v7

    #@132
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@134
    move/from16 v19, v0

    #@136
    sub-int v19, v11, v19

    #@138
    move/from16 v0, v18

    #@13a
    move/from16 v1, v19

    #@13c
    if-le v0, v1, :cond_6

    #@13e
    .line 937
    iget v0, v13, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@140
    move/from16 v18, v0

    #@142
    sub-int v18, v11, v18

    #@144
    sub-int v9, v18, v7

    #@146
    goto :goto_6

    #@147
    .line 949
    .end local v9    # "childTop":I
    .end local v11    # "height":I
    :cond_8
    const/4 v15, 0x4

    #@148
    goto :goto_5

    #@149
    .line 955
    .end local v4    # "changeOffset":Z
    .end local v5    # "child":Landroid/view/View;
    .end local v7    # "childHeight":I
    .end local v8    # "childLeft":I
    .end local v10    # "childWidth":I
    .end local v13    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    .end local v14    # "newOffset":F
    .end local v16    # "vgrav":I
    :cond_9
    const/16 v18, 0x0

    #@14b
    move/from16 v0, v18

    #@14d
    move-object/from16 v1, p0

    #@14f
    iput-boolean v0, v1, Landroid/support/v4/widget/DrawerLayout;->mInLayout:Z

    #@151
    .line 956
    const/16 v18, 0x0

    #@153
    move/from16 v0, v18

    #@155
    move-object/from16 v1, p0

    #@157
    iput-boolean v0, v1, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@159
    .line 957
    return-void

    #@15a
    .line 911
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_1
        0x50 -> :sswitch_0
    .end sparse-switch
.end method

.method protected onMeasure(II)V
    .locals 23
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 789
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v18

    #@4
    .line 790
    .local v18, "widthMode":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@7
    move-result v13

    #@8
    .line 791
    .local v13, "heightMode":I
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@b
    move-result v19

    #@c
    .line 792
    .local v19, "widthSize":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@f
    move-result v14

    #@10
    .line 794
    .local v14, "heightSize":I
    const/high16 v20, 0x40000000    # 2.0f

    #@12
    move/from16 v0, v18

    #@14
    move/from16 v1, v20

    #@16
    if-ne v0, v1, :cond_0

    #@18
    const/high16 v20, 0x40000000    # 2.0f

    #@1a
    move/from16 v0, v20

    #@1c
    if-eq v13, v0, :cond_2

    #@1e
    .line 795
    :cond_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->isInEditMode()Z

    #@21
    move-result v20

    #@22
    if-eqz v20, :cond_5

    #@24
    .line 800
    const/high16 v20, -0x80000000

    #@26
    move/from16 v0, v18

    #@28
    move/from16 v1, v20

    #@2a
    if-ne v0, v1, :cond_3

    #@2c
    .line 801
    const/high16 v18, 0x40000000    # 2.0f

    #@2e
    .line 806
    :cond_1
    :goto_0
    const/high16 v20, -0x80000000

    #@30
    move/from16 v0, v20

    #@32
    if-ne v13, v0, :cond_4

    #@34
    .line 807
    const/high16 v13, 0x40000000    # 2.0f

    #@36
    .line 819
    :cond_2
    :goto_1
    move-object/from16 v0, p0

    #@38
    move/from16 v1, v19

    #@3a
    invoke-virtual {v0, v1, v14}, Landroid/support/v4/widget/DrawerLayout;->setMeasuredDimension(II)V

    #@3d
    .line 821
    move-object/from16 v0, p0

    #@3f
    iget-object v0, v0, Landroid/support/v4/widget/DrawerLayout;->mLastInsets:Ljava/lang/Object;

    #@41
    move-object/from16 v20, v0

    #@43
    if-eqz v20, :cond_6

    #@45
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getFitsSystemWindows(Landroid/view/View;)Z

    #@48
    move-result v20

    #@49
    if-eqz v20, :cond_6

    #@4b
    const/4 v3, 0x1

    #@4c
    .line 822
    .local v3, "applyInsets":Z
    :goto_2
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4f
    move-result v16

    #@50
    .line 825
    .local v16, "layoutDirection":I
    const/4 v12, 0x0

    #@51
    .line 826
    .local v12, "foundDrawers":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/DrawerLayout;->getChildCount()I

    #@54
    move-result v6

    #@55
    .line 827
    .local v6, "childCount":I
    const/4 v15, 0x0

    #@56
    .local v15, "i":I
    :goto_3
    if-ge v15, v6, :cond_d

    #@58
    .line 828
    move-object/from16 v0, p0

    #@5a
    invoke-virtual {v0, v15}, Landroid/support/v4/widget/DrawerLayout;->getChildAt(I)Landroid/view/View;

    #@5d
    move-result-object v5

    #@5e
    .line 830
    .local v5, "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@61
    move-result v20

    #@62
    const/16 v21, 0x8

    #@64
    move/from16 v0, v20

    #@66
    move/from16 v1, v21

    #@68
    if-ne v0, v1, :cond_7

    #@6a
    .line 827
    :goto_4
    add-int/lit8 v15, v15, 0x1

    #@6c
    goto :goto_3

    #@6d
    .line 802
    .end local v3    # "applyInsets":Z
    .end local v5    # "child":Landroid/view/View;
    .end local v6    # "childCount":I
    .end local v12    # "foundDrawers":I
    .end local v15    # "i":I
    .end local v16    # "layoutDirection":I
    :cond_3
    if-nez v18, :cond_1

    #@6f
    .line 803
    const/high16 v18, 0x40000000    # 2.0f

    #@71
    .line 804
    const/16 v19, 0x12c

    #@73
    goto :goto_0

    #@74
    .line 809
    :cond_4
    if-nez v13, :cond_2

    #@76
    .line 810
    const/high16 v13, 0x40000000    # 2.0f

    #@78
    .line 811
    const/16 v14, 0x12c

    #@7a
    goto :goto_1

    #@7b
    .line 814
    :cond_5
    new-instance v20, Ljava/lang/IllegalArgumentException;

    #@7d
    const-string v21, "DrawerLayout must be measured with MeasureSpec.EXACTLY."

    #@7f
    invoke-direct/range {v20 .. v21}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@82
    throw v20

    #@83
    .line 821
    :cond_6
    const/4 v3, 0x0

    #@84
    goto :goto_2

    #@85
    .line 834
    .restart local v3    # "applyInsets":Z
    .restart local v5    # "child":Landroid/view/View;
    .restart local v6    # "childCount":I
    .restart local v12    # "foundDrawers":I
    .restart local v15    # "i":I
    .restart local v16    # "layoutDirection":I
    :cond_7
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@88
    move-result-object v17

    #@89
    check-cast v17, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@8b
    .line 836
    .local v17, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    if-eqz v3, :cond_8

    #@8d
    .line 837
    move-object/from16 v0, v17

    #@8f
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@91
    move/from16 v20, v0

    #@93
    move/from16 v0, v20

    #@95
    move/from16 v1, v16

    #@97
    invoke-static {v0, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@9a
    move-result v4

    #@9b
    .line 838
    .local v4, "cgrav":I
    invoke-static {v5}, Landroid/support/v4/view/ViewCompat;->getFitsSystemWindows(Landroid/view/View;)Z

    #@9e
    move-result v20

    #@9f
    if-eqz v20, :cond_9

    #@a1
    .line 839
    sget-object v20, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@a3
    move-object/from16 v0, p0

    #@a5
    iget-object v0, v0, Landroid/support/v4/widget/DrawerLayout;->mLastInsets:Ljava/lang/Object;

    #@a7
    move-object/from16 v21, v0

    #@a9
    move-object/from16 v0, v20

    #@ab
    move-object/from16 v1, v21

    #@ad
    invoke-interface {v0, v5, v1, v4}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;->dispatchChildInsets(Landroid/view/View;Ljava/lang/Object;I)V

    #@b0
    .line 845
    .end local v4    # "cgrav":I
    :cond_8
    :goto_5
    move-object/from16 v0, p0

    #@b2
    invoke-virtual {v0, v5}, Landroid/support/v4/widget/DrawerLayout;->isContentView(Landroid/view/View;)Z

    #@b5
    move-result v20

    #@b6
    if-eqz v20, :cond_a

    #@b8
    .line 847
    move-object/from16 v0, v17

    #@ba
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->leftMargin:I

    #@bc
    move/from16 v20, v0

    #@be
    sub-int v20, v19, v20

    #@c0
    move-object/from16 v0, v17

    #@c2
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->rightMargin:I

    #@c4
    move/from16 v21, v0

    #@c6
    sub-int v20, v20, v21

    #@c8
    const/high16 v21, 0x40000000    # 2.0f

    #@ca
    invoke-static/range {v20 .. v21}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@cd
    move-result v9

    #@ce
    .line 849
    .local v9, "contentWidthSpec":I
    move-object/from16 v0, v17

    #@d0
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@d2
    move/from16 v20, v0

    #@d4
    sub-int v20, v14, v20

    #@d6
    move-object/from16 v0, v17

    #@d8
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@da
    move/from16 v21, v0

    #@dc
    sub-int v20, v20, v21

    #@de
    const/high16 v21, 0x40000000    # 2.0f

    #@e0
    invoke-static/range {v20 .. v21}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@e3
    move-result v8

    #@e4
    .line 851
    .local v8, "contentHeightSpec":I
    invoke-virtual {v5, v9, v8}, Landroid/view/View;->measure(II)V

    #@e7
    goto :goto_4

    #@e8
    .line 841
    .end local v8    # "contentHeightSpec":I
    .end local v9    # "contentWidthSpec":I
    .restart local v4    # "cgrav":I
    :cond_9
    sget-object v20, Landroid/support/v4/widget/DrawerLayout;->IMPL:Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;

    #@ea
    move-object/from16 v0, p0

    #@ec
    iget-object v0, v0, Landroid/support/v4/widget/DrawerLayout;->mLastInsets:Ljava/lang/Object;

    #@ee
    move-object/from16 v21, v0

    #@f0
    move-object/from16 v0, v20

    #@f2
    move-object/from16 v1, v17

    #@f4
    move-object/from16 v2, v21

    #@f6
    invoke-interface {v0, v1, v2, v4}, Landroid/support/v4/widget/DrawerLayout$DrawerLayoutCompatImpl;->applyMarginInsets(Landroid/view/ViewGroup$MarginLayoutParams;Ljava/lang/Object;I)V

    #@f9
    goto :goto_5

    #@fa
    .line 852
    .end local v4    # "cgrav":I
    :cond_a
    move-object/from16 v0, p0

    #@fc
    invoke-virtual {v0, v5}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@ff
    move-result v20

    #@100
    if-eqz v20, :cond_c

    #@102
    .line 853
    move-object/from16 v0, p0

    #@104
    invoke-virtual {v0, v5}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewAbsoluteGravity(Landroid/view/View;)I

    #@107
    move-result v20

    #@108
    and-int/lit8 v7, v20, 0x7

    #@10a
    .line 855
    .local v7, "childGravity":I
    and-int v20, v12, v7

    #@10c
    if-eqz v20, :cond_b

    #@10e
    .line 856
    new-instance v20, Ljava/lang/IllegalStateException;

    #@110
    new-instance v21, Ljava/lang/StringBuilder;

    #@112
    invoke-direct/range {v21 .. v21}, Ljava/lang/StringBuilder;-><init>()V

    #@115
    const-string v22, "Child drawer has absolute gravity "

    #@117
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11a
    move-result-object v21

    #@11b
    invoke-static {v7}, Landroid/support/v4/widget/DrawerLayout;->gravityToString(I)Ljava/lang/String;

    #@11e
    move-result-object v22

    #@11f
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@122
    move-result-object v21

    #@123
    const-string v22, " but this "

    #@125
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@128
    move-result-object v21

    #@129
    const-string v22, "DrawerLayout"

    #@12b
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12e
    move-result-object v21

    #@12f
    const-string v22, " already has a "

    #@131
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@134
    move-result-object v21

    #@135
    const-string v22, "drawer view along that edge"

    #@137
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13a
    move-result-object v21

    #@13b
    invoke-virtual/range {v21 .. v21}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@13e
    move-result-object v21

    #@13f
    invoke-direct/range {v20 .. v21}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@142
    throw v20

    #@143
    .line 860
    :cond_b
    move-object/from16 v0, p0

    #@145
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout;->mMinDrawerMargin:I

    #@147
    move/from16 v20, v0

    #@149
    move-object/from16 v0, v17

    #@14b
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->leftMargin:I

    #@14d
    move/from16 v21, v0

    #@14f
    add-int v20, v20, v21

    #@151
    move-object/from16 v0, v17

    #@153
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->rightMargin:I

    #@155
    move/from16 v21, v0

    #@157
    add-int v20, v20, v21

    #@159
    move-object/from16 v0, v17

    #@15b
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->width:I

    #@15d
    move/from16 v21, v0

    #@15f
    move/from16 v0, p1

    #@161
    move/from16 v1, v20

    #@163
    move/from16 v2, v21

    #@165
    invoke-static {v0, v1, v2}, Landroid/support/v4/widget/DrawerLayout;->getChildMeasureSpec(III)I

    #@168
    move-result v11

    #@169
    .line 863
    .local v11, "drawerWidthSpec":I
    move-object/from16 v0, v17

    #@16b
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->topMargin:I

    #@16d
    move/from16 v20, v0

    #@16f
    move-object/from16 v0, v17

    #@171
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->bottomMargin:I

    #@173
    move/from16 v21, v0

    #@175
    add-int v20, v20, v21

    #@177
    move-object/from16 v0, v17

    #@179
    iget v0, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->height:I

    #@17b
    move/from16 v21, v0

    #@17d
    move/from16 v0, p2

    #@17f
    move/from16 v1, v20

    #@181
    move/from16 v2, v21

    #@183
    invoke-static {v0, v1, v2}, Landroid/support/v4/widget/DrawerLayout;->getChildMeasureSpec(III)I

    #@186
    move-result v10

    #@187
    .line 866
    .local v10, "drawerHeightSpec":I
    invoke-virtual {v5, v11, v10}, Landroid/view/View;->measure(II)V

    #@18a
    goto/16 :goto_4

    #@18c
    .line 868
    .end local v7    # "childGravity":I
    .end local v10    # "drawerHeightSpec":I
    .end local v11    # "drawerWidthSpec":I
    :cond_c
    new-instance v20, Ljava/lang/IllegalStateException;

    #@18e
    new-instance v21, Ljava/lang/StringBuilder;

    #@190
    invoke-direct/range {v21 .. v21}, Ljava/lang/StringBuilder;-><init>()V

    #@193
    const-string v22, "Child "

    #@195
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@198
    move-result-object v21

    #@199
    move-object/from16 v0, v21

    #@19b
    invoke-virtual {v0, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@19e
    move-result-object v21

    #@19f
    const-string v22, " at index "

    #@1a1
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a4
    move-result-object v21

    #@1a5
    move-object/from16 v0, v21

    #@1a7
    invoke-virtual {v0, v15}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1aa
    move-result-object v21

    #@1ab
    const-string v22, " does not have a valid layout_gravity - must be Gravity.LEFT, "

    #@1ad
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1b0
    move-result-object v21

    #@1b1
    const-string v22, "Gravity.RIGHT or Gravity.NO_GRAVITY"

    #@1b3
    invoke-virtual/range {v21 .. v22}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1b6
    move-result-object v21

    #@1b7
    invoke-virtual/range {v21 .. v21}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1ba
    move-result-object v21

    #@1bb
    invoke-direct/range {v20 .. v21}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1be
    throw v20

    #@1bf
    .line 873
    .end local v5    # "child":Landroid/view/View;
    .end local v17    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_d
    return-void
.end method

.method protected onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 4
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 1492
    move-object v0, p1

    #@1
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$SavedState;

    #@3
    .line 1493
    .local v0, "ss":Landroid/support/v4/widget/DrawerLayout$SavedState;
    invoke-virtual {v0}, Landroid/support/v4/widget/DrawerLayout$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@6
    move-result-object v2

    #@7
    invoke-super {p0, v2}, Landroid/view/ViewGroup;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@a
    .line 1495
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout$SavedState;->openDrawerGravity:I

    #@c
    if-eqz v2, :cond_0

    #@e
    .line 1496
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout$SavedState;->openDrawerGravity:I

    #@10
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@13
    move-result-object v1

    #@14
    .line 1497
    .local v1, "toOpen":Landroid/view/View;
    if-eqz v1, :cond_0

    #@16
    .line 1498
    invoke-virtual {p0, v1}, Landroid/support/v4/widget/DrawerLayout;->openDrawer(Landroid/view/View;)V

    #@19
    .line 1502
    .end local v1    # "toOpen":Landroid/view/View;
    :cond_0
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout$SavedState;->lockModeLeft:I

    #@1b
    const/4 v3, 0x3

    #@1c
    invoke-virtual {p0, v2, v3}, Landroid/support/v4/widget/DrawerLayout;->setDrawerLockMode(II)V

    #@1f
    .line 1503
    iget v2, v0, Landroid/support/v4/widget/DrawerLayout$SavedState;->lockModeRight:I

    #@21
    const/4 v3, 0x5

    #@22
    invoke-virtual {p0, v2, v3}, Landroid/support/v4/widget/DrawerLayout;->setDrawerLockMode(II)V

    #@25
    .line 1504
    return-void
.end method

.method protected onSaveInstanceState()Landroid/os/Parcelable;
    .locals 4

    #@0
    .prologue
    .line 1508
    invoke-super {p0}, Landroid/view/ViewGroup;->onSaveInstanceState()Landroid/os/Parcelable;

    #@3
    move-result-object v2

    #@4
    .line 1509
    .local v2, "superState":Landroid/os/Parcelable;
    new-instance v1, Landroid/support/v4/widget/DrawerLayout$SavedState;

    #@6
    invoke-direct {v1, v2}, Landroid/support/v4/widget/DrawerLayout$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 1511
    .local v1, "ss":Landroid/support/v4/widget/DrawerLayout$SavedState;
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->findOpenDrawer()Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    .line 1512
    .local v0, "openDrawer":Landroid/view/View;
    if-eqz v0, :cond_0

    #@f
    .line 1513
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@12
    move-result-object v3

    #@13
    check-cast v3, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@15
    iget v3, v3, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@17
    iput v3, v1, Landroid/support/v4/widget/DrawerLayout$SavedState;->openDrawerGravity:I

    #@19
    .line 1516
    :cond_0
    iget v3, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeLeft:I

    #@1b
    iput v3, v1, Landroid/support/v4/widget/DrawerLayout$SavedState;->lockModeLeft:I

    #@1d
    .line 1517
    iget v3, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeRight:I

    #@1f
    iput v3, v1, Landroid/support/v4/widget/DrawerLayout$SavedState;->lockModeRight:I

    #@21
    .line 1519
    return-object v1
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 13
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 1155
    iget-object v10, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@2
    invoke-virtual {v10, p1}, Landroid/support/v4/widget/ViewDragHelper;->processTouchEvent(Landroid/view/MotionEvent;)V

    #@5
    .line 1156
    iget-object v10, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@7
    invoke-virtual {v10, p1}, Landroid/support/v4/widget/ViewDragHelper;->processTouchEvent(Landroid/view/MotionEvent;)V

    #@a
    .line 1158
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    #@d
    move-result v0

    #@e
    .line 1159
    .local v0, "action":I
    const/4 v7, 0x1

    #@f
    .line 1161
    .local v7, "wantTouchEvents":Z
    and-int/lit16 v10, v0, 0xff

    #@11
    packed-switch v10, :pswitch_data_0

    #@14
    .line 1202
    :goto_0
    :pswitch_0
    return v7

    #@15
    .line 1163
    :pswitch_1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@18
    move-result v8

    #@19
    .line 1164
    .local v8, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@1c
    move-result v9

    #@1d
    .line 1165
    .local v9, "y":F
    iput v8, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionX:F

    #@1f
    .line 1166
    iput v9, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionY:F

    #@21
    .line 1167
    const/4 v10, 0x0

    #@22
    iput-boolean v10, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@24
    .line 1168
    const/4 v10, 0x0

    #@25
    iput-boolean v10, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@27
    goto :goto_0

    #@28
    .line 1173
    .end local v8    # "x":F
    .end local v9    # "y":F
    :pswitch_2
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@2b
    move-result v8

    #@2c
    .line 1174
    .restart local v8    # "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@2f
    move-result v9

    #@30
    .line 1175
    .restart local v9    # "y":F
    const/4 v4, 0x1

    #@31
    .line 1176
    .local v4, "peekingOnly":Z
    iget-object v10, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@33
    float-to-int v11, v8

    #@34
    float-to-int v12, v9

    #@35
    invoke-virtual {v10, v11, v12}, Landroid/support/v4/widget/ViewDragHelper;->findTopChildUnder(II)Landroid/view/View;

    #@38
    move-result-object v6

    #@39
    .line 1177
    .local v6, "touchedView":Landroid/view/View;
    if-eqz v6, :cond_0

    #@3b
    invoke-virtual {p0, v6}, Landroid/support/v4/widget/DrawerLayout;->isContentView(Landroid/view/View;)Z

    #@3e
    move-result v10

    #@3f
    if-eqz v10, :cond_0

    #@41
    .line 1178
    iget v10, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionX:F

    #@43
    sub-float v1, v8, v10

    #@45
    .line 1179
    .local v1, "dx":F
    iget v10, p0, Landroid/support/v4/widget/DrawerLayout;->mInitialMotionY:F

    #@47
    sub-float v2, v9, v10

    #@49
    .line 1180
    .local v2, "dy":F
    iget-object v10, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@4b
    invoke-virtual {v10}, Landroid/support/v4/widget/ViewDragHelper;->getTouchSlop()I

    #@4e
    move-result v5

    #@4f
    .line 1181
    .local v5, "slop":I
    mul-float v10, v1, v1

    #@51
    mul-float v11, v2, v2

    #@53
    add-float/2addr v10, v11

    #@54
    mul-int v11, v5, v5

    #@56
    int-to-float v11, v11

    #@57
    cmpg-float v10, v10, v11

    #@59
    if-gez v10, :cond_0

    #@5b
    .line 1183
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->findOpenDrawer()Landroid/view/View;

    #@5e
    move-result-object v3

    #@5f
    .line 1184
    .local v3, "openDrawer":Landroid/view/View;
    if-eqz v3, :cond_0

    #@61
    .line 1185
    invoke-virtual {p0, v3}, Landroid/support/v4/widget/DrawerLayout;->getDrawerLockMode(Landroid/view/View;)I

    #@64
    move-result v10

    #@65
    const/4 v11, 0x2

    #@66
    if-ne v10, v11, :cond_1

    #@68
    const/4 v4, 0x1

    #@69
    .line 1189
    .end local v1    # "dx":F
    .end local v2    # "dy":F
    .end local v3    # "openDrawer":Landroid/view/View;
    .end local v5    # "slop":I
    :cond_0
    :goto_1
    invoke-virtual {p0, v4}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers(Z)V

    #@6c
    .line 1190
    const/4 v10, 0x0

    #@6d
    iput-boolean v10, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@6f
    goto :goto_0

    #@70
    .line 1185
    .restart local v1    # "dx":F
    .restart local v2    # "dy":F
    .restart local v3    # "openDrawer":Landroid/view/View;
    .restart local v5    # "slop":I
    :cond_1
    const/4 v4, 0x0

    #@71
    goto :goto_1

    #@72
    .line 1195
    .end local v1    # "dx":F
    .end local v2    # "dy":F
    .end local v3    # "openDrawer":Landroid/view/View;
    .end local v4    # "peekingOnly":Z
    .end local v5    # "slop":I
    .end local v6    # "touchedView":Landroid/view/View;
    .end local v8    # "x":F
    .end local v9    # "y":F
    :pswitch_3
    const/4 v10, 0x1

    #@73
    invoke-virtual {p0, v10}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers(Z)V

    #@76
    .line 1196
    const/4 v10, 0x0

    #@77
    iput-boolean v10, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@79
    .line 1197
    const/4 v10, 0x0

    #@7a
    iput-boolean v10, p0, Landroid/support/v4/widget/DrawerLayout;->mChildrenCanceledTouch:Z

    #@7c
    goto :goto_0

    #@7d
    .line 1161
    nop

    #@7e
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_2
        :pswitch_0
        :pswitch_3
    .end packed-switch
.end method

.method public openDrawer(I)V
    .locals 4
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1291
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 1292
    .local v0, "drawerView":Landroid/view/View;
    if-nez v0, :cond_0

    #@6
    .line 1293
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "No drawer view found with gravity "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-static {p1}, Landroid/support/v4/widget/DrawerLayout;->gravityToString(I)Ljava/lang/String;

    #@16
    move-result-object v3

    #@17
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v2

    #@1b
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v2

    #@1f
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@22
    throw v1

    #@23
    .line 1296
    :cond_0
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->openDrawer(Landroid/view/View;)V

    #@26
    .line 1297
    return-void
.end method

.method public openDrawer(Landroid/view/View;)V
    .locals 4
    .param p1, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 1263
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@4
    move-result v1

    #@5
    if-nez v1, :cond_0

    #@7
    .line 1264
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@9
    new-instance v2, Ljava/lang/StringBuilder;

    #@b
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@e
    const-string v3, "View "

    #@10
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13
    move-result-object v2

    #@14
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@17
    move-result-object v2

    #@18
    const-string v3, " is not a sliding drawer"

    #@1a
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1d
    move-result-object v2

    #@1e
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@21
    move-result-object v2

    #@22
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@25
    throw v1

    #@26
    .line 1267
    :cond_0
    iget-boolean v1, p0, Landroid/support/v4/widget/DrawerLayout;->mFirstLayout:Z

    #@28
    if-eqz v1, :cond_1

    #@2a
    .line 1268
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2d
    move-result-object v0

    #@2e
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@30
    .line 1269
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    const/high16 v1, 0x3f800000    # 1.0f

    #@32
    iput v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@34
    .line 1270
    iput-boolean v2, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->knownOpen:Z

    #@36
    .line 1272
    invoke-direct {p0, p1, v2}, Landroid/support/v4/widget/DrawerLayout;->updateChildrenImportantForAccessibility(Landroid/view/View;Z)V

    #@39
    .line 1281
    .end local v0    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@3c
    .line 1282
    return-void

    #@3d
    .line 1274
    :cond_1
    const/4 v1, 0x3

    #@3e
    invoke-virtual {p0, p1, v1}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@41
    move-result v1

    #@42
    if-eqz v1, :cond_2

    #@44
    .line 1275
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@46
    const/4 v2, 0x0

    #@47
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@4a
    move-result v3

    #@4b
    invoke-virtual {v1, p1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@4e
    goto :goto_0

    #@4f
    .line 1277
    :cond_2
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@51
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@54
    move-result v2

    #@55
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@58
    move-result v3

    #@59
    sub-int/2addr v2, v3

    #@5a
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@5d
    move-result v3

    #@5e
    invoke-virtual {v1, p1, v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@61
    goto :goto_0
.end method

.method public requestDisallowInterceptTouchEvent(Z)V
    .locals 1
    .param p1, "disallowIntercept"    # Z

    #@0
    .prologue
    .line 1210
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->requestDisallowInterceptTouchEvent(Z)V

    #@3
    .line 1212
    iput-boolean p1, p0, Landroid/support/v4/widget/DrawerLayout;->mDisallowInterceptRequested:Z

    #@5
    .line 1213
    if-eqz p1, :cond_0

    #@7
    .line 1214
    const/4 v0, 0x1

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->closeDrawers(Z)V

    #@b
    .line 1216
    :cond_0
    return-void
.end method

.method public requestLayout()V
    .locals 1

    #@0
    .prologue
    .line 961
    iget-boolean v0, p0, Landroid/support/v4/widget/DrawerLayout;->mInLayout:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 962
    invoke-super {p0}, Landroid/view/ViewGroup;->requestLayout()V

    #@7
    .line 964
    :cond_0
    return-void
.end method

.method public setChildInsets(Ljava/lang/Object;Z)V
    .locals 1
    .param p1, "insets"    # Ljava/lang/Object;
    .param p2, "draw"    # Z

    #@0
    .prologue
    .line 360
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout;->mLastInsets:Ljava/lang/Object;

    #@2
    .line 361
    iput-boolean p2, p0, Landroid/support/v4/widget/DrawerLayout;->mDrawStatusBarBackground:Z

    #@4
    .line 362
    if-nez p2, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getBackground()Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    if-nez v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->setWillNotDraw(Z)V

    #@10
    .line 363
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->requestLayout()V

    #@13
    .line 364
    return-void

    #@14
    .line 362
    :cond_0
    const/4 v0, 0x0

    #@15
    goto :goto_0
.end method

.method public setDrawerListener(Landroid/support/v4/widget/DrawerLayout$DrawerListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@0
    .prologue
    .line 420
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@2
    .line 421
    return-void
.end method

.method public setDrawerLockMode(I)V
    .locals 1
    .param p1, "lockMode"    # I

    #@0
    .prologue
    .line 437
    const/4 v0, 0x3

    #@1
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/widget/DrawerLayout;->setDrawerLockMode(II)V

    #@4
    .line 438
    const/4 v0, 0x5

    #@5
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/widget/DrawerLayout;->setDrawerLockMode(II)V

    #@8
    .line 439
    return-void
.end method

.method public setDrawerLockMode(II)V
    .locals 6
    .param p1, "lockMode"    # I
    .param p2, "edgeGravity"    # I

    #@0
    .prologue
    const/4 v5, 0x3

    #@1
    .line 461
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4
    move-result v4

    #@5
    invoke-static {p2, v4}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@8
    move-result v0

    #@9
    .line 463
    .local v0, "absGravity":I
    if-ne v0, v5, :cond_3

    #@b
    .line 464
    iput p1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeLeft:I

    #@d
    .line 468
    :cond_0
    :goto_0
    if-eqz p1, :cond_1

    #@f
    .line 470
    if-ne v0, v5, :cond_4

    #@11
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@13
    .line 471
    .local v1, "helper":Landroid/support/v4/widget/ViewDragHelper;
    :goto_1
    invoke-virtual {v1}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@16
    .line 473
    .end local v1    # "helper":Landroid/support/v4/widget/ViewDragHelper;
    :cond_1
    packed-switch p1, :pswitch_data_0

    #@19
    .line 488
    :cond_2
    :goto_2
    return-void

    #@1a
    .line 465
    :cond_3
    const/4 v4, 0x5

    #@1b
    if-ne v0, v4, :cond_0

    #@1d
    .line 466
    iput p1, p0, Landroid/support/v4/widget/DrawerLayout;->mLockModeRight:I

    #@1f
    goto :goto_0

    #@20
    .line 470
    :cond_4
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@22
    goto :goto_1

    #@23
    .line 475
    :pswitch_0
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@26
    move-result-object v3

    #@27
    .line 476
    .local v3, "toOpen":Landroid/view/View;
    if-eqz v3, :cond_2

    #@29
    .line 477
    invoke-virtual {p0, v3}, Landroid/support/v4/widget/DrawerLayout;->openDrawer(Landroid/view/View;)V

    #@2c
    goto :goto_2

    #@2d
    .line 481
    .end local v3    # "toOpen":Landroid/view/View;
    :pswitch_1
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@30
    move-result-object v2

    #@31
    .line 482
    .local v2, "toClose":Landroid/view/View;
    if-eqz v2, :cond_2

    #@33
    .line 483
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/DrawerLayout;->closeDrawer(Landroid/view/View;)V

    #@36
    goto :goto_2

    #@37
    .line 473
    nop

    #@38
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public setDrawerLockMode(ILandroid/view/View;)V
    .locals 4
    .param p1, "lockMode"    # I
    .param p2, "drawerView"    # Landroid/view/View;

    #@0
    .prologue
    .line 509
    invoke-virtual {p0, p2}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_0

    #@6
    .line 510
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "View "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v2

    #@17
    const-string v3, " is not a "

    #@19
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v2

    #@1d
    const-string v3, "drawer with appropriate layout_gravity"

    #@1f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v2

    #@23
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@26
    move-result-object v2

    #@27
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@2a
    throw v1

    #@2b
    .line 513
    :cond_0
    invoke-virtual {p2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2e
    move-result-object v1

    #@2f
    check-cast v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@31
    iget v0, v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->gravity:I

    #@33
    .line 514
    .local v0, "gravity":I
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/widget/DrawerLayout;->setDrawerLockMode(II)V

    #@36
    .line 515
    return-void
.end method

.method public setDrawerShadow(II)V
    .locals 1
    .param p1, "resId"    # I
    .param p2, "gravity"    # I

    #@0
    .prologue
    .line 400
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0, p2}, Landroid/support/v4/widget/DrawerLayout;->setDrawerShadow(Landroid/graphics/drawable/Drawable;I)V

    #@b
    .line 401
    return-void
.end method

.method public setDrawerShadow(Landroid/graphics/drawable/Drawable;I)V
    .locals 3
    .param p1, "shadowDrawable"    # Landroid/graphics/drawable/Drawable;
    .param p2, "gravity"    # I

    #@0
    .prologue
    .line 380
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v1

    #@4
    invoke-static {p2, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v0

    #@8
    .line 382
    .local v0, "absGravity":I
    and-int/lit8 v1, v0, 0x3

    #@a
    const/4 v2, 0x3

    #@b
    if-ne v1, v2, :cond_0

    #@d
    .line 383
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout;->mShadowLeft:Landroid/graphics/drawable/Drawable;

    #@f
    .line 384
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@12
    .line 386
    :cond_0
    and-int/lit8 v1, v0, 0x5

    #@14
    const/4 v2, 0x5

    #@15
    if-ne v1, v2, :cond_1

    #@17
    .line 387
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout;->mShadowRight:Landroid/graphics/drawable/Drawable;

    #@19
    .line 388
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@1c
    .line 390
    :cond_1
    return-void
.end method

.method public setDrawerTitle(ILjava/lang/CharSequence;)V
    .locals 2
    .param p1, "edgeGravity"    # I
    .param p2, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 565
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v1

    #@4
    invoke-static {p1, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v0

    #@8
    .line 567
    .local v0, "absGravity":I
    const/4 v1, 0x3

    #@9
    if-ne v0, v1, :cond_1

    #@b
    .line 568
    iput-object p2, p0, Landroid/support/v4/widget/DrawerLayout;->mTitleLeft:Ljava/lang/CharSequence;

    #@d
    .line 572
    :cond_0
    :goto_0
    return-void

    #@e
    .line 569
    :cond_1
    const/4 v1, 0x5

    #@f
    if-ne v0, v1, :cond_0

    #@11
    .line 570
    iput-object p2, p0, Landroid/support/v4/widget/DrawerLayout;->mTitleRight:Ljava/lang/CharSequence;

    #@13
    goto :goto_0
.end method

.method setDrawerViewOffset(Landroid/view/View;F)V
    .locals 2
    .param p1, "drawerView"    # Landroid/view/View;
    .param p2, "slideOffset"    # F

    #@0
    .prologue
    .line 689
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    .line 690
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@8
    cmpl-float v1, p2, v1

    #@a
    if-nez v1, :cond_0

    #@c
    .line 696
    :goto_0
    return-void

    #@d
    .line 694
    :cond_0
    iput p2, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@f
    .line 695
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/widget/DrawerLayout;->dispatchOnDrawerSlide(Landroid/view/View;F)V

    #@12
    goto :goto_0
.end method

.method public setScrimColor(I)V
    .locals 0
    .param p1, "color"    # I

    #@0
    .prologue
    .line 409
    iput p1, p0, Landroid/support/v4/widget/DrawerLayout;->mScrimColor:I

    #@2
    .line 410
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@5
    .line 411
    return-void
.end method

.method public setStatusBarBackground(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1007
    if-eqz p1, :cond_0

    #@2
    invoke-virtual {p0}, Landroid/support/v4/widget/DrawerLayout;->getContext()Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-static {v0, p1}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    :goto_0
    iput-object v0, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@c
    .line 1008
    return-void

    #@d
    .line 1007
    :cond_0
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public setStatusBarBackground(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "bg"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 997
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@2
    .line 998
    return-void
.end method

.method public setStatusBarBackgroundColor(I)V
    .locals 1
    .param p1, "color"    # I

    #@0
    .prologue
    .line 1018
    new-instance v0, Landroid/graphics/drawable/ColorDrawable;

    #@2
    invoke-direct {v0, p1}, Landroid/graphics/drawable/ColorDrawable;-><init>(I)V

    #@5
    iput-object v0, p0, Landroid/support/v4/widget/DrawerLayout;->mStatusBarBackground:Landroid/graphics/drawable/Drawable;

    #@7
    .line 1019
    return-void
.end method

.method updateDrawerState(IILandroid/view/View;)V
    .locals 7
    .param p1, "forGravity"    # I
    .param p2, "activeState"    # I
    .param p3, "activeDrawer"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v6, 0x2

    #@1
    const/4 v5, 0x1

    #@2
    .line 599
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout;->mLeftDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@4
    invoke-virtual {v4}, Landroid/support/v4/widget/ViewDragHelper;->getViewDragState()I

    #@7
    move-result v0

    #@8
    .line 600
    .local v0, "leftState":I
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout;->mRightDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@a
    invoke-virtual {v4}, Landroid/support/v4/widget/ViewDragHelper;->getViewDragState()I

    #@d
    move-result v2

    #@e
    .line 603
    .local v2, "rightState":I
    if-eq v0, v5, :cond_0

    #@10
    if-ne v2, v5, :cond_3

    #@12
    .line 604
    :cond_0
    const/4 v3, 0x1

    #@13
    .line 611
    .local v3, "state":I
    :goto_0
    if-eqz p3, :cond_1

    #@15
    if-nez p2, :cond_1

    #@17
    .line 612
    invoke-virtual {p3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1a
    move-result-object v1

    #@1b
    check-cast v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@1d
    .line 613
    .local v1, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget v4, v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@1f
    const/4 v5, 0x0

    #@20
    cmpl-float v4, v4, v5

    #@22
    if-nez v4, :cond_6

    #@24
    .line 614
    invoke-virtual {p0, p3}, Landroid/support/v4/widget/DrawerLayout;->dispatchOnDrawerClosed(Landroid/view/View;)V

    #@27
    .line 620
    .end local v1    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_1
    :goto_1
    iget v4, p0, Landroid/support/v4/widget/DrawerLayout;->mDrawerState:I

    #@29
    if-eq v3, v4, :cond_2

    #@2b
    .line 621
    iput v3, p0, Landroid/support/v4/widget/DrawerLayout;->mDrawerState:I

    #@2d
    .line 623
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@2f
    if-eqz v4, :cond_2

    #@31
    .line 624
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout;->mListener:Landroid/support/v4/widget/DrawerLayout$DrawerListener;

    #@33
    invoke-interface {v4, v3}, Landroid/support/v4/widget/DrawerLayout$DrawerListener;->onDrawerStateChanged(I)V

    #@36
    .line 627
    :cond_2
    return-void

    #@37
    .line 605
    .end local v3    # "state":I
    :cond_3
    if-eq v0, v6, :cond_4

    #@39
    if-ne v2, v6, :cond_5

    #@3b
    .line 606
    :cond_4
    const/4 v3, 0x2

    #@3c
    .restart local v3    # "state":I
    goto :goto_0

    #@3d
    .line 608
    .end local v3    # "state":I
    :cond_5
    const/4 v3, 0x0

    #@3e
    .restart local v3    # "state":I
    goto :goto_0

    #@3f
    .line 615
    .restart local v1    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_6
    iget v4, v1, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->onScreen:F

    #@41
    const/high16 v5, 0x3f800000    # 1.0f

    #@43
    cmpl-float v4, v4, v5

    #@45
    if-nez v4, :cond_1

    #@47
    .line 616
    invoke-virtual {p0, p3}, Landroid/support/v4/widget/DrawerLayout;->dispatchOnDrawerOpened(Landroid/view/View;)V

    #@4a
    goto :goto_1
.end method
