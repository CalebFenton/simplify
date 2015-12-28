.class public Landroid/support/v4/widget/SlidingPaneLayout;
.super Landroid/view/ViewGroup;
.source "SlidingPaneLayout.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/SlidingPaneLayout$1;,
        Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;,
        Landroid/support/v4/widget/SlidingPaneLayout$AccessibilityDelegate;,
        Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJBMR1;,
        Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;,
        Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;,
        Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;,
        Landroid/support/v4/widget/SlidingPaneLayout$SavedState;,
        Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;,
        Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;,
        Landroid/support/v4/widget/SlidingPaneLayout$SimplePanelSlideListener;,
        Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;
    }
.end annotation


# static fields
.field private static final DEFAULT_FADE_COLOR:I = -0x33333334

.field private static final DEFAULT_OVERHANG_SIZE:I = 0x20

.field static final IMPL:Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;

.field private static final MIN_FLING_VELOCITY:I = 0x190

.field private static final TAG:Ljava/lang/String; = "SlidingPaneLayout"


# instance fields
.field private mCanSlide:Z

.field private mCoveredFadeColor:I

.field private final mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

.field private mFirstLayout:Z

.field private mInitialMotionX:F

.field private mInitialMotionY:F

.field private mIsUnableToDrag:Z

.field private final mOverhangSize:I

.field private mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

.field private mParallaxBy:I

.field private mParallaxOffset:F

.field private final mPostedRunnables:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;",
            ">;"
        }
    .end annotation
.end field

.field private mPreservedOpenState:Z

.field private mShadowDrawableLeft:Landroid/graphics/drawable/Drawable;

.field private mShadowDrawableRight:Landroid/graphics/drawable/Drawable;

.field private mSlideOffset:F

.field private mSlideRange:I

.field private mSlideableView:Landroid/view/View;

.field private mSliderFadeColor:I

.field private final mTmpRect:Landroid/graphics/Rect;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 198
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 199
    .local v0, "deviceVersion":I
    const/16 v1, 0x11

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 200
    new-instance v1, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJBMR1;

    #@8
    invoke-direct {v1}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJBMR1;-><init>()V

    #@b
    sput-object v1, Landroid/support/v4/widget/SlidingPaneLayout;->IMPL:Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;

    #@d
    .line 206
    :goto_0
    return-void

    #@e
    .line 201
    :cond_0
    const/16 v1, 0x10

    #@10
    if-lt v0, v1, :cond_1

    #@12
    .line 202
    new-instance v1, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;

    #@14
    invoke-direct {v1}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;-><init>()V

    #@17
    sput-object v1, Landroid/support/v4/widget/SlidingPaneLayout;->IMPL:Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;

    #@19
    goto :goto_0

    #@1a
    .line 204
    :cond_1
    new-instance v1, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;

    #@1c
    invoke-direct {v1}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;-><init>()V

    #@1f
    sput-object v1, Landroid/support/v4/widget/SlidingPaneLayout;->IMPL:Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;

    #@21
    goto :goto_0
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 250
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/widget/SlidingPaneLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 251
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 254
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v4/widget/SlidingPaneLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 255
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/high16 v4, 0x3f000000    # 0.5f

    #@3
    .line 258
    invoke-direct {p0, p1, p2, p3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 108
    const v2, -0x33333334

    #@9
    iput v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@b
    .line 188
    iput-boolean v3, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@d
    .line 190
    new-instance v2, Landroid/graphics/Rect;

    #@f
    invoke-direct {v2}, Landroid/graphics/Rect;-><init>()V

    #@12
    iput-object v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@14
    .line 192
    new-instance v2, Ljava/util/ArrayList;

    #@16
    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    #@19
    iput-object v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@1b
    .line 260
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1e
    move-result-object v2

    #@1f
    invoke-virtual {v2}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@22
    move-result-object v2

    #@23
    iget v0, v2, Landroid/util/DisplayMetrics;->density:F

    #@25
    .line 261
    .local v0, "density":F
    const/high16 v2, 0x42000000    # 32.0f

    #@27
    mul-float/2addr v2, v0

    #@28
    add-float/2addr v2, v4

    #@29
    float-to-int v2, v2

    #@2a
    iput v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mOverhangSize:I

    #@2c
    .line 263
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@2f
    move-result-object v1

    #@30
    .line 265
    .local v1, "viewConfig":Landroid/view/ViewConfiguration;
    const/4 v2, 0x0

    #@31
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/SlidingPaneLayout;->setWillNotDraw(Z)V

    #@34
    .line 267
    new-instance v2, Landroid/support/v4/widget/SlidingPaneLayout$AccessibilityDelegate;

    #@36
    invoke-direct {v2, p0}, Landroid/support/v4/widget/SlidingPaneLayout$AccessibilityDelegate;-><init>(Landroid/support/v4/widget/SlidingPaneLayout;)V

    #@39
    invoke-static {p0, v2}, Landroid/support/v4/view/ViewCompat;->setAccessibilityDelegate(Landroid/view/View;Landroid/support/v4/view/AccessibilityDelegateCompat;)V

    #@3c
    .line 268
    invoke-static {p0, v3}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@3f
    .line 270
    new-instance v2, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;

    #@41
    const/4 v3, 0x0

    #@42
    invoke-direct {v2, p0, v3}, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;-><init>(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/support/v4/widget/SlidingPaneLayout$1;)V

    #@45
    invoke-static {p0, v4, v2}, Landroid/support/v4/widget/ViewDragHelper;->create(Landroid/view/ViewGroup;FLandroid/support/v4/widget/ViewDragHelper$Callback;)Landroid/support/v4/widget/ViewDragHelper;

    #@48
    move-result-object v2

    #@49
    iput-object v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@4b
    .line 271
    iget-object v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@4d
    const/high16 v3, 0x43c80000    # 400.0f

    #@4f
    mul-float/2addr v3, v0

    #@50
    invoke-virtual {v2, v3}, Landroid/support/v4/widget/ViewDragHelper;->setMinVelocity(F)V

    #@53
    .line 272
    return-void
.end method

.method static synthetic access$100(Landroid/support/v4/widget/SlidingPaneLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mIsUnableToDrag:Z

    #@2
    return v0
.end method

.method static synthetic access$1000(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/view/View;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;
    .param p1, "x1"    # Landroid/view/View;

    #@0
    .prologue
    .line 89
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout;->invalidateChildRegion(Landroid/view/View;)V

    #@3
    return-void
.end method

.method static synthetic access$1100(Landroid/support/v4/widget/SlidingPaneLayout;)Ljava/util/ArrayList;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/support/v4/widget/ViewDragHelper;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v4/widget/SlidingPaneLayout;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@2
    return v0
.end method

.method static synthetic access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method static synthetic access$502(Landroid/support/v4/widget/SlidingPaneLayout;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 89
    iput-boolean p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@2
    return p1
.end method

.method static synthetic access$600(Landroid/support/v4/widget/SlidingPaneLayout;I)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 89
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout;->onPanelDragged(I)V

    #@3
    return-void
.end method

.method static synthetic access$700(Landroid/support/v4/widget/SlidingPaneLayout;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static synthetic access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;

    #@0
    .prologue
    .line 89
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@2
    return v0
.end method

.method private closePane(Landroid/view/View;I)Z
    .locals 2
    .param p1, "pane"    # Landroid/view/View;
    .param p2, "initialVelocity"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 859
    iget-boolean v1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@3
    if-nez v1, :cond_0

    #@5
    const/4 v1, 0x0

    #@6
    invoke-virtual {p0, v1, p2}, Landroid/support/v4/widget/SlidingPaneLayout;->smoothSlideTo(FI)Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_1

    #@c
    .line 860
    :cond_0
    iput-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@e
    .line 861
    const/4 v0, 0x1

    #@f
    .line 863
    :cond_1
    return v0
.end method

.method private dimChildView(Landroid/view/View;FI)V
    .locals 9
    .param p1, "v"    # Landroid/view/View;
    .param p2, "mag"    # F
    .param p3, "fadeColor"    # I

    #@0
    .prologue
    const/4 v8, 0x2

    #@1
    .line 968
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v4

    #@5
    check-cast v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@7
    .line 970
    .local v4, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    const/4 v5, 0x0

    #@8
    cmpl-float v5, p2, v5

    #@a
    if-lez v5, :cond_3

    #@c
    if-eqz p3, :cond_3

    #@e
    .line 971
    const/high16 v5, -0x1000000

    #@10
    and-int/2addr v5, p3

    #@11
    ushr-int/lit8 v0, v5, 0x18

    #@13
    .line 972
    .local v0, "baseAlpha":I
    int-to-float v5, v0

    #@14
    mul-float/2addr v5, p2

    #@15
    float-to-int v3, v5

    #@16
    .line 973
    .local v3, "imag":I
    shl-int/lit8 v5, v3, 0x18

    #@18
    const v6, 0xffffff

    #@1b
    and-int/2addr v6, p3

    #@1c
    or-int v1, v5, v6

    #@1e
    .line 974
    .local v1, "color":I
    iget-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@20
    if-nez v5, :cond_0

    #@22
    .line 975
    new-instance v5, Landroid/graphics/Paint;

    #@24
    invoke-direct {v5}, Landroid/graphics/Paint;-><init>()V

    #@27
    iput-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@29
    .line 977
    :cond_0
    iget-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@2b
    new-instance v6, Landroid/graphics/PorterDuffColorFilter;

    #@2d
    sget-object v7, Landroid/graphics/PorterDuff$Mode;->SRC_OVER:Landroid/graphics/PorterDuff$Mode;

    #@2f
    invoke-direct {v6, v1, v7}, Landroid/graphics/PorterDuffColorFilter;-><init>(ILandroid/graphics/PorterDuff$Mode;)V

    #@32
    invoke-virtual {v5, v6}, Landroid/graphics/Paint;->setColorFilter(Landroid/graphics/ColorFilter;)Landroid/graphics/ColorFilter;

    #@35
    .line 978
    invoke-static {p1}, Landroid/support/v4/view/ViewCompat;->getLayerType(Landroid/view/View;)I

    #@38
    move-result v5

    #@39
    if-eq v5, v8, :cond_1

    #@3b
    .line 979
    iget-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@3d
    invoke-static {p1, v8, v5}, Landroid/support/v4/view/ViewCompat;->setLayerType(Landroid/view/View;ILandroid/graphics/Paint;)V

    #@40
    .line 981
    :cond_1
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout;->invalidateChildRegion(Landroid/view/View;)V

    #@43
    .line 990
    .end local v0    # "baseAlpha":I
    .end local v1    # "color":I
    .end local v3    # "imag":I
    :cond_2
    :goto_0
    return-void

    #@44
    .line 982
    :cond_3
    invoke-static {p1}, Landroid/support/v4/view/ViewCompat;->getLayerType(Landroid/view/View;)I

    #@47
    move-result v5

    #@48
    if-eqz v5, :cond_2

    #@4a
    .line 983
    iget-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@4c
    if-eqz v5, :cond_4

    #@4e
    .line 984
    iget-object v5, v4, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@50
    const/4 v6, 0x0

    #@51
    invoke-virtual {v5, v6}, Landroid/graphics/Paint;->setColorFilter(Landroid/graphics/ColorFilter;)Landroid/graphics/ColorFilter;

    #@54
    .line 986
    :cond_4
    new-instance v2, Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;

    #@56
    invoke-direct {v2, p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;-><init>(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/view/View;)V

    #@59
    .line 987
    .local v2, "dlr":Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;
    iget-object v5, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@5b
    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@5e
    .line 988
    invoke-static {p0, v2}, Landroid/support/v4/view/ViewCompat;->postOnAnimation(Landroid/view/View;Ljava/lang/Runnable;)V

    #@61
    goto :goto_0
.end method

.method private invalidateChildRegion(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 1038
    sget-object v0, Landroid/support/v4/widget/SlidingPaneLayout;->IMPL:Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;

    #@2
    invoke-interface {v0, p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImpl;->invalidateChildRegion(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/view/View;)V

    #@5
    .line 1039
    return-void
.end method

.method private isLayoutRtlSupport()Z
    .locals 2

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1642
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4
    move-result v1

    #@5
    if-ne v1, v0, :cond_0

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method private onPanelDragged(I)V
    .locals 10
    .param p1, "newLeft"    # I

    #@0
    .prologue
    .line 940
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@2
    if-nez v7, :cond_0

    #@4
    .line 942
    const/4 v7, 0x0

    #@5
    iput v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@7
    .line 965
    :goto_0
    return-void

    #@8
    .line 945
    :cond_0
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@b
    move-result v1

    #@c
    .line 946
    .local v1, "isLayoutRtl":Z
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@e
    invoke-virtual {v7}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@11
    move-result-object v2

    #@12
    check-cast v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@14
    .line 948
    .local v2, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@16
    invoke-virtual {v7}, Landroid/view/View;->getWidth()I

    #@19
    move-result v0

    #@1a
    .line 949
    .local v0, "childWidth":I
    if-eqz v1, :cond_3

    #@1c
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@1f
    move-result v7

    #@20
    sub-int/2addr v7, p1

    #@21
    sub-int v4, v7, v0

    #@23
    .line 951
    .local v4, "newStart":I
    :goto_1
    if-eqz v1, :cond_4

    #@25
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@28
    move-result v5

    #@29
    .line 952
    .local v5, "paddingStart":I
    :goto_2
    if-eqz v1, :cond_5

    #@2b
    iget v3, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@2d
    .line 953
    .local v3, "lpMargin":I
    :goto_3
    add-int v6, v5, v3

    #@2f
    .line 955
    .local v6, "startBound":I
    sub-int v7, v4, v6

    #@31
    int-to-float v7, v7

    #@32
    iget v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@34
    int-to-float v8, v8

    #@35
    div-float/2addr v7, v8

    #@36
    iput v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@38
    .line 957
    iget v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@3a
    if-eqz v7, :cond_1

    #@3c
    .line 958
    iget v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@3e
    invoke-direct {p0, v7}, Landroid/support/v4/widget/SlidingPaneLayout;->parallaxOtherViews(F)V

    #@41
    .line 961
    :cond_1
    iget-boolean v7, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@43
    if-eqz v7, :cond_2

    #@45
    .line 962
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@47
    iget v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@49
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@4b
    invoke-direct {p0, v7, v8, v9}, Landroid/support/v4/widget/SlidingPaneLayout;->dimChildView(Landroid/view/View;FI)V

    #@4e
    .line 964
    :cond_2
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@50
    invoke-virtual {p0, v7}, Landroid/support/v4/widget/SlidingPaneLayout;->dispatchOnPanelSlide(Landroid/view/View;)V

    #@53
    goto :goto_0

    #@54
    .end local v3    # "lpMargin":I
    .end local v4    # "newStart":I
    .end local v5    # "paddingStart":I
    .end local v6    # "startBound":I
    :cond_3
    move v4, p1

    #@55
    .line 949
    goto :goto_1

    #@56
    .line 951
    .restart local v4    # "newStart":I
    :cond_4
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@59
    move-result v5

    #@5a
    goto :goto_2

    #@5b
    .line 952
    .restart local v5    # "paddingStart":I
    :cond_5
    iget v3, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@5d
    goto :goto_3
.end method

.method private openPane(Landroid/view/View;I)Z
    .locals 2
    .param p1, "pane"    # Landroid/view/View;
    .param p2, "initialVelocity"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 867
    iget-boolean v1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@3
    if-nez v1, :cond_0

    #@5
    const/high16 v1, 0x3f800000    # 1.0f

    #@7
    invoke-virtual {p0, v1, p2}, Landroid/support/v4/widget/SlidingPaneLayout;->smoothSlideTo(FI)Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_1

    #@d
    .line 868
    :cond_0
    iput-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@f
    .line 871
    :goto_0
    return v0

    #@10
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method private parallaxOtherViews(F)V
    .locals 12
    .param p1, "slideOffset"    # F

    #@0
    .prologue
    const/high16 v11, 0x3f800000    # 1.0f

    #@2
    .line 1186
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@5
    move-result v4

    #@6
    .line 1187
    .local v4, "isLayoutRtl":Z
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@8
    invoke-virtual {v9}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@b
    move-result-object v7

    #@c
    check-cast v7, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@e
    .line 1188
    .local v7, "slideLp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    iget-boolean v9, v7, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@10
    if-eqz v9, :cond_2

    #@12
    if-eqz v4, :cond_1

    #@14
    iget v9, v7, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@16
    :goto_0
    if-gtz v9, :cond_2

    #@18
    const/4 v1, 0x1

    #@19
    .line 1190
    .local v1, "dimViews":Z
    :goto_1
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@1c
    move-result v0

    #@1d
    .line 1191
    .local v0, "childCount":I
    const/4 v3, 0x0

    #@1e
    .local v3, "i":I
    :goto_2
    if-ge v3, v0, :cond_6

    #@20
    .line 1192
    invoke-virtual {p0, v3}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@23
    move-result-object v8

    #@24
    .line 1193
    .local v8, "v":Landroid/view/View;
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@26
    if-ne v8, v9, :cond_3

    #@28
    .line 1191
    :cond_0
    :goto_3
    add-int/lit8 v3, v3, 0x1

    #@2a
    goto :goto_2

    #@2b
    .line 1188
    .end local v0    # "childCount":I
    .end local v1    # "dimViews":Z
    .end local v3    # "i":I
    .end local v8    # "v":Landroid/view/View;
    :cond_1
    iget v9, v7, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@2d
    goto :goto_0

    #@2e
    :cond_2
    const/4 v1, 0x0

    #@2f
    goto :goto_1

    #@30
    .line 1195
    .restart local v0    # "childCount":I
    .restart local v1    # "dimViews":Z
    .restart local v3    # "i":I
    .restart local v8    # "v":Landroid/view/View;
    :cond_3
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxOffset:F

    #@32
    sub-float v9, v11, v9

    #@34
    iget v10, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@36
    int-to-float v10, v10

    #@37
    mul-float/2addr v9, v10

    #@38
    float-to-int v6, v9

    #@39
    .line 1196
    .local v6, "oldOffset":I
    iput p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxOffset:F

    #@3b
    .line 1197
    sub-float v9, v11, p1

    #@3d
    iget v10, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@3f
    int-to-float v10, v10

    #@40
    mul-float/2addr v9, v10

    #@41
    float-to-int v5, v9

    #@42
    .line 1198
    .local v5, "newOffset":I
    sub-int v2, v6, v5

    #@44
    .line 1200
    .local v2, "dx":I
    if-eqz v4, :cond_4

    #@46
    neg-int v2, v2

    #@47
    .end local v2    # "dx":I
    :cond_4
    invoke-virtual {v8, v2}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@4a
    .line 1202
    if-eqz v1, :cond_0

    #@4c
    .line 1203
    if-eqz v4, :cond_5

    #@4e
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxOffset:F

    #@50
    sub-float/2addr v9, v11

    #@51
    :goto_4
    iget v10, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCoveredFadeColor:I

    #@53
    invoke-direct {p0, v8, v9, v10}, Landroid/support/v4/widget/SlidingPaneLayout;->dimChildView(Landroid/view/View;FI)V

    #@56
    goto :goto_3

    #@57
    :cond_5
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxOffset:F

    #@59
    sub-float v9, v11, v9

    #@5b
    goto :goto_4

    #@5c
    .line 1207
    .end local v5    # "newOffset":I
    .end local v6    # "oldOffset":I
    .end local v8    # "v":Landroid/view/View;
    :cond_6
    return-void
.end method

.method private static viewIsOpaque(Landroid/view/View;)Z
    .locals 5
    .param p0, "v"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 408
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->isOpaque(Landroid/view/View;)Z

    #@5
    move-result v3

    #@6
    if-eqz v3, :cond_1

    #@8
    .line 419
    :cond_0
    :goto_0
    return v1

    #@9
    .line 413
    :cond_1
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@b
    const/16 v4, 0x12

    #@d
    if-lt v3, v4, :cond_2

    #@f
    move v1, v2

    #@10
    goto :goto_0

    #@11
    .line 415
    :cond_2
    invoke-virtual {p0}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    #@14
    move-result-object v0

    #@15
    .line 416
    .local v0, "bg":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_3

    #@17
    .line 417
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getOpacity()I

    #@1a
    move-result v3

    #@1b
    const/4 v4, -0x1

    #@1c
    if-eq v3, v4, :cond_0

    #@1e
    move v1, v2

    #@1f
    goto :goto_0

    #@20
    :cond_3
    move v1, v2

    #@21
    .line 419
    goto :goto_0
.end method


# virtual methods
.method protected canScroll(Landroid/view/View;ZIII)Z
    .locals 11
    .param p1, "v"    # Landroid/view/View;
    .param p2, "checkV"    # Z
    .param p3, "dx"    # I
    .param p4, "x"    # I
    .param p5, "y"    # I

    #@0
    .prologue
    .line 1221
    instance-of v0, p1, Landroid/view/ViewGroup;

    #@2
    if-eqz v0, :cond_1

    #@4
    move-object v7, p1

    #@5
    .line 1222
    check-cast v7, Landroid/view/ViewGroup;

    #@7
    .line 1223
    .local v7, "group":Landroid/view/ViewGroup;
    invoke-virtual {p1}, Landroid/view/View;->getScrollX()I

    #@a
    move-result v9

    #@b
    .line 1224
    .local v9, "scrollX":I
    invoke-virtual {p1}, Landroid/view/View;->getScrollY()I

    #@e
    move-result v10

    #@f
    .line 1225
    .local v10, "scrollY":I
    invoke-virtual {v7}, Landroid/view/ViewGroup;->getChildCount()I

    #@12
    move-result v6

    #@13
    .line 1227
    .local v6, "count":I
    add-int/lit8 v8, v6, -0x1

    #@15
    .local v8, "i":I
    :goto_0
    if-ltz v8, :cond_1

    #@17
    .line 1230
    invoke-virtual {v7, v8}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@1a
    move-result-object v1

    #@1b
    .line 1231
    .local v1, "child":Landroid/view/View;
    add-int v0, p4, v9

    #@1d
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@20
    move-result v2

    #@21
    if-lt v0, v2, :cond_0

    #@23
    add-int v0, p4, v9

    #@25
    invoke-virtual {v1}, Landroid/view/View;->getRight()I

    #@28
    move-result v2

    #@29
    if-ge v0, v2, :cond_0

    #@2b
    add-int v0, p5, v10

    #@2d
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@30
    move-result v2

    #@31
    if-lt v0, v2, :cond_0

    #@33
    add-int v0, p5, v10

    #@35
    invoke-virtual {v1}, Landroid/view/View;->getBottom()I

    #@38
    move-result v2

    #@39
    if-ge v0, v2, :cond_0

    #@3b
    const/4 v2, 0x1

    #@3c
    add-int v0, p4, v9

    #@3e
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@41
    move-result v3

    #@42
    sub-int v4, v0, v3

    #@44
    add-int v0, p5, v10

    #@46
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@49
    move-result v3

    #@4a
    sub-int v5, v0, v3

    #@4c
    move-object v0, p0

    #@4d
    move v3, p3

    #@4e
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/widget/SlidingPaneLayout;->canScroll(Landroid/view/View;ZIII)Z

    #@51
    move-result v0

    #@52
    if-eqz v0, :cond_0

    #@54
    .line 1235
    const/4 v0, 0x1

    #@55
    .line 1240
    .end local v1    # "child":Landroid/view/View;
    .end local v6    # "count":I
    .end local v7    # "group":Landroid/view/ViewGroup;
    .end local v8    # "i":I
    .end local v9    # "scrollX":I
    .end local v10    # "scrollY":I
    .end local p3    # "dx":I
    :goto_1
    return v0

    #@56
    .line 1227
    .restart local v1    # "child":Landroid/view/View;
    .restart local v6    # "count":I
    .restart local v7    # "group":Landroid/view/ViewGroup;
    .restart local v8    # "i":I
    .restart local v9    # "scrollX":I
    .restart local v10    # "scrollY":I
    .restart local p3    # "dx":I
    :cond_0
    add-int/lit8 v8, v8, -0x1

    #@58
    goto :goto_0

    #@59
    .line 1240
    .end local v1    # "child":Landroid/view/View;
    .end local v6    # "count":I
    .end local v7    # "group":Landroid/view/ViewGroup;
    .end local v8    # "i":I
    .end local v9    # "scrollX":I
    .end local v10    # "scrollY":I
    :cond_1
    if-eqz p2, :cond_3

    #@5b
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@5e
    move-result v0

    #@5f
    if-eqz v0, :cond_2

    #@61
    .end local p3    # "dx":I
    :goto_2
    invoke-static {p1, p3}, Landroid/support/v4/view/ViewCompat;->canScrollHorizontally(Landroid/view/View;I)Z

    #@64
    move-result v0

    #@65
    if-eqz v0, :cond_3

    #@67
    const/4 v0, 0x1

    #@68
    goto :goto_1

    #@69
    .restart local p3    # "dx":I
    :cond_2
    neg-int p3, p3

    #@6a
    goto :goto_2

    #@6b
    .end local p3    # "dx":I
    :cond_3
    const/4 v0, 0x0

    #@6c
    goto :goto_1
.end method

.method public canSlide()Z
    .locals 1
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 926
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@2
    return v0
.end method

.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1265
    instance-of v0, p1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

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

.method public closePane()Z
    .locals 2

    #@0
    .prologue
    .line 907
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->closePane(Landroid/view/View;I)Z

    #@6
    move-result v0

    #@7
    return v0
.end method

.method public computeScroll()V
    .locals 2

    #@0
    .prologue
    .line 1076
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@2
    const/4 v1, 0x1

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/ViewDragHelper;->continueSettling(Z)Z

    #@6
    move-result v0

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 1077
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@b
    if-nez v0, :cond_1

    #@d
    .line 1078
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@f
    invoke-virtual {v0}, Landroid/support/v4/widget/ViewDragHelper;->abort()V

    #@12
    .line 1084
    :cond_0
    :goto_0
    return-void

    #@13
    .line 1082
    :cond_1
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@16
    goto :goto_0
.end method

.method dispatchOnPanelClosed(Landroid/view/View;)V
    .locals 1
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 346
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 347
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;->onPanelClosed(Landroid/view/View;)V

    #@9
    .line 349
    :cond_0
    const/16 v0, 0x20

    #@b
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SlidingPaneLayout;->sendAccessibilityEvent(I)V

    #@e
    .line 350
    return-void
.end method

.method dispatchOnPanelOpened(Landroid/view/View;)V
    .locals 1
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 339
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 340
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;->onPanelOpened(Landroid/view/View;)V

    #@9
    .line 342
    :cond_0
    const/16 v0, 0x20

    #@b
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SlidingPaneLayout;->sendAccessibilityEvent(I)V

    #@e
    .line 343
    return-void
.end method

.method dispatchOnPanelSlide(Landroid/view/View;)V
    .locals 2
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 333
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 334
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@6
    iget v1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@8
    invoke-interface {v0, p1, v1}, Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;->onPanelSlide(Landroid/view/View;F)V

    #@b
    .line 336
    :cond_0
    return-void
.end method

.method public draw(Landroid/graphics/Canvas;)V
    .locals 10
    .param p1, "c"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    .line 1152
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->draw(Landroid/graphics/Canvas;)V

    #@4
    .line 1153
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@7
    move-result v1

    #@8
    .line 1155
    .local v1, "isLayoutRtl":Z
    if-eqz v1, :cond_1

    #@a
    .line 1156
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mShadowDrawableRight:Landroid/graphics/drawable/Drawable;

    #@c
    .line 1161
    .local v4, "shadowDrawable":Landroid/graphics/drawable/Drawable;
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@f
    move-result v8

    #@10
    if-le v8, v9, :cond_2

    #@12
    invoke-virtual {p0, v9}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@15
    move-result-object v5

    #@16
    .line 1162
    .local v5, "shadowView":Landroid/view/View;
    :goto_1
    if-eqz v5, :cond_0

    #@18
    if-nez v4, :cond_3

    #@1a
    .line 1183
    :cond_0
    :goto_2
    return-void

    #@1b
    .line 1158
    .end local v4    # "shadowDrawable":Landroid/graphics/drawable/Drawable;
    .end local v5    # "shadowView":Landroid/view/View;
    :cond_1
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mShadowDrawableLeft:Landroid/graphics/drawable/Drawable;

    #@1d
    .restart local v4    # "shadowDrawable":Landroid/graphics/drawable/Drawable;
    goto :goto_0

    #@1e
    .line 1161
    :cond_2
    const/4 v5, 0x0

    #@1f
    goto :goto_1

    #@20
    .line 1167
    .restart local v5    # "shadowView":Landroid/view/View;
    :cond_3
    invoke-virtual {v5}, Landroid/view/View;->getTop()I

    #@23
    move-result v7

    #@24
    .line 1168
    .local v7, "top":I
    invoke-virtual {v5}, Landroid/view/View;->getBottom()I

    #@27
    move-result v0

    #@28
    .line 1170
    .local v0, "bottom":I
    invoke-virtual {v4}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@2b
    move-result v6

    #@2c
    .line 1173
    .local v6, "shadowWidth":I
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@2f
    move-result v8

    #@30
    if-eqz v8, :cond_4

    #@32
    .line 1174
    invoke-virtual {v5}, Landroid/view/View;->getRight()I

    #@35
    move-result v2

    #@36
    .line 1175
    .local v2, "left":I
    add-int v3, v2, v6

    #@38
    .line 1181
    .local v3, "right":I
    :goto_3
    invoke-virtual {v4, v2, v7, v3, v0}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@3b
    .line 1182
    invoke-virtual {v4, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@3e
    goto :goto_2

    #@3f
    .line 1177
    .end local v2    # "left":I
    .end local v3    # "right":I
    :cond_4
    invoke-virtual {v5}, Landroid/view/View;->getLeft()I

    #@42
    move-result v3

    #@43
    .line 1178
    .restart local v3    # "right":I
    sub-int v2, v3, v6

    #@45
    .restart local v2    # "left":I
    goto :goto_3
.end method

.method protected drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z
    .locals 7
    .param p1, "canvas"    # Landroid/graphics/Canvas;
    .param p2, "child"    # Landroid/view/View;
    .param p3, "drawingTime"    # J

    #@0
    .prologue
    .line 994
    invoke-virtual {p2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v1

    #@4
    check-cast v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@6
    .line 996
    .local v1, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    const/4 v4, 0x2

    #@7
    invoke-virtual {p1, v4}, Landroid/graphics/Canvas;->save(I)I

    #@a
    move-result v3

    #@b
    .line 998
    .local v3, "save":I
    iget-boolean v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@d
    if-eqz v4, :cond_0

    #@f
    iget-boolean v4, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->slideable:Z

    #@11
    if-nez v4, :cond_0

    #@13
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@15
    if-eqz v4, :cond_0

    #@17
    .line 1000
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@19
    invoke-virtual {p1, v4}, Landroid/graphics/Canvas;->getClipBounds(Landroid/graphics/Rect;)Z

    #@1c
    .line 1001
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@1f
    move-result v4

    #@20
    if-eqz v4, :cond_1

    #@22
    .line 1002
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@24
    iget-object v5, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@26
    iget v5, v5, Landroid/graphics/Rect;->left:I

    #@28
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@2a
    invoke-virtual {v6}, Landroid/view/View;->getRight()I

    #@2d
    move-result v6

    #@2e
    invoke-static {v5, v6}, Ljava/lang/Math;->max(II)I

    #@31
    move-result v5

    #@32
    iput v5, v4, Landroid/graphics/Rect;->left:I

    #@34
    .line 1006
    :goto_0
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@36
    invoke-virtual {p1, v4}, Landroid/graphics/Canvas;->clipRect(Landroid/graphics/Rect;)Z

    #@39
    .line 1009
    :cond_0
    sget v4, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3b
    const/16 v5, 0xb

    #@3d
    if-lt v4, v5, :cond_2

    #@3f
    .line 1010
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/ViewGroup;->drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z

    #@42
    move-result v2

    #@43
    .line 1032
    .local v2, "result":Z
    :goto_1
    invoke-virtual {p1, v3}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@46
    .line 1034
    return v2

    #@47
    .line 1004
    .end local v2    # "result":Z
    :cond_1
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@49
    iget-object v5, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mTmpRect:Landroid/graphics/Rect;

    #@4b
    iget v5, v5, Landroid/graphics/Rect;->right:I

    #@4d
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@4f
    invoke-virtual {v6}, Landroid/view/View;->getLeft()I

    #@52
    move-result v6

    #@53
    invoke-static {v5, v6}, Ljava/lang/Math;->min(II)I

    #@56
    move-result v5

    #@57
    iput v5, v4, Landroid/graphics/Rect;->right:I

    #@59
    goto :goto_0

    #@5a
    .line 1012
    :cond_2
    iget-boolean v4, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@5c
    if-eqz v4, :cond_5

    #@5e
    iget v4, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@60
    const/4 v5, 0x0

    #@61
    cmpl-float v4, v4, v5

    #@63
    if-lez v4, :cond_5

    #@65
    .line 1013
    invoke-virtual {p2}, Landroid/view/View;->isDrawingCacheEnabled()Z

    #@68
    move-result v4

    #@69
    if-nez v4, :cond_3

    #@6b
    .line 1014
    const/4 v4, 0x1

    #@6c
    invoke-virtual {p2, v4}, Landroid/view/View;->setDrawingCacheEnabled(Z)V

    #@6f
    .line 1016
    :cond_3
    invoke-virtual {p2}, Landroid/view/View;->getDrawingCache()Landroid/graphics/Bitmap;

    #@72
    move-result-object v0

    #@73
    .line 1017
    .local v0, "cache":Landroid/graphics/Bitmap;
    if-eqz v0, :cond_4

    #@75
    .line 1018
    invoke-virtual {p2}, Landroid/view/View;->getLeft()I

    #@78
    move-result v4

    #@79
    int-to-float v4, v4

    #@7a
    invoke-virtual {p2}, Landroid/view/View;->getTop()I

    #@7d
    move-result v5

    #@7e
    int-to-float v5, v5

    #@7f
    iget-object v6, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimPaint:Landroid/graphics/Paint;

    #@81
    invoke-virtual {p1, v0, v4, v5, v6}, Landroid/graphics/Canvas;->drawBitmap(Landroid/graphics/Bitmap;FFLandroid/graphics/Paint;)V

    #@84
    .line 1019
    const/4 v2, 0x0

    #@85
    .restart local v2    # "result":Z
    goto :goto_1

    #@86
    .line 1021
    .end local v2    # "result":Z
    :cond_4
    const-string v4, "SlidingPaneLayout"

    #@88
    new-instance v5, Ljava/lang/StringBuilder;

    #@8a
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@8d
    const-string v6, "drawChild: child view "

    #@8f
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@92
    move-result-object v5

    #@93
    invoke-virtual {v5, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@96
    move-result-object v5

    #@97
    const-string v6, " returned null drawing cache"

    #@99
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@9c
    move-result-object v5

    #@9d
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@a0
    move-result-object v5

    #@a1
    invoke-static {v4, v5}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@a4
    .line 1022
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/ViewGroup;->drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z

    #@a7
    move-result v2

    #@a8
    .restart local v2    # "result":Z
    goto :goto_1

    #@a9
    .line 1025
    .end local v0    # "cache":Landroid/graphics/Bitmap;
    .end local v2    # "result":Z
    :cond_5
    invoke-virtual {p2}, Landroid/view/View;->isDrawingCacheEnabled()Z

    #@ac
    move-result v4

    #@ad
    if-eqz v4, :cond_6

    #@af
    .line 1026
    const/4 v4, 0x0

    #@b0
    invoke-virtual {p2, v4}, Landroid/view/View;->setDrawingCacheEnabled(Z)V

    #@b3
    .line 1028
    :cond_6
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/ViewGroup;->drawChild(Landroid/graphics/Canvas;Landroid/view/View;J)Z

    #@b6
    move-result v2

    #@b7
    .restart local v2    # "result":Z
    goto :goto_1
.end method

.method protected generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 1253
    new-instance v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@2
    invoke-direct {v0}, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;-><init>()V

    #@5
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1270
    new-instance v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1258
    instance-of v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@2
    if-eqz v0, :cond_0

    #@4
    new-instance v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@6
    check-cast p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@8
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;-><init>(Landroid/view/ViewGroup$MarginLayoutParams;)V

    #@b
    :goto_0
    return-object v0

    #@c
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_0
    new-instance v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@e
    invoke-direct {v0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@11
    goto :goto_0
.end method

.method public getCoveredFadeColor()I
    .locals 1

    #@0
    .prologue
    .line 325
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCoveredFadeColor:I

    #@2
    return v0
.end method

.method public getParallaxDistance()I
    .locals 1

    #@0
    .prologue
    .line 292
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@2
    return v0
.end method

.method public getSliderFadeColor()I
    .locals 1

    #@0
    .prologue
    .line 308
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@2
    return v0
.end method

.method isDimmed(Landroid/view/View;)Z
    .locals 4
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1244
    if-nez p1, :cond_1

    #@3
    .line 1248
    :cond_0
    :goto_0
    return v1

    #@4
    .line 1247
    :cond_1
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@7
    move-result-object v0

    #@8
    check-cast v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@a
    .line 1248
    .local v0, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    iget-boolean v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@c
    if-eqz v2, :cond_0

    #@e
    iget-boolean v2, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@10
    if-eqz v2, :cond_0

    #@12
    iget v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@14
    const/4 v3, 0x0

    #@15
    cmpl-float v2, v2, v3

    #@17
    if-lez v2, :cond_0

    #@19
    const/4 v1, 0x1

    #@1a
    goto :goto_0
.end method

.method public isOpen()Z
    .locals 2

    #@0
    .prologue
    .line 917
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@6
    const/high16 v1, 0x3f800000    # 1.0f

    #@8
    cmpl-float v0, v0, v1

    #@a
    if-nez v0, :cond_1

    #@c
    :cond_0
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_1
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isSlideable()Z
    .locals 1

    #@0
    .prologue
    .line 936
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@2
    return v0
.end method

.method protected onAttachedToWindow()V
    .locals 1

    #@0
    .prologue
    .line 424
    invoke-super {p0}, Landroid/view/ViewGroup;->onAttachedToWindow()V

    #@3
    .line 425
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@6
    .line 426
    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 4

    #@0
    .prologue
    .line 430
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@3
    .line 431
    const/4 v3, 0x1

    #@4
    iput-boolean v3, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@6
    .line 433
    const/4 v2, 0x0

    #@7
    .local v2, "i":I
    iget-object v3, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@c
    move-result v0

    #@d
    .local v0, "count":I
    :goto_0
    if-ge v2, v0, :cond_0

    #@f
    .line 434
    iget-object v3, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@11
    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@14
    move-result-object v1

    #@15
    check-cast v1, Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;

    #@17
    .line 435
    .local v1, "dlr":Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;
    invoke-virtual {v1}, Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;->run()V

    #@1a
    .line 433
    add-int/lit8 v2, v2, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 437
    .end local v1    # "dlr":Landroid/support/v4/widget/SlidingPaneLayout$DisableLayerRunnable;
    :cond_0
    iget-object v3, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPostedRunnables:Ljava/util/ArrayList;

    #@1f
    invoke-virtual {v3}, Ljava/util/ArrayList;->clear()V

    #@22
    .line 438
    return-void
.end method

.method public onInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 13
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 759
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@3
    move-result v0

    #@4
    .line 762
    .local v0, "action":I
    iget-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@6
    if-nez v9, :cond_0

    #@8
    if-nez v0, :cond_0

    #@a
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@d
    move-result v9

    #@e
    const/4 v10, 0x1

    #@f
    if-le v9, v10, :cond_0

    #@11
    .line 764
    const/4 v9, 0x1

    #@12
    invoke-virtual {p0, v9}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@15
    move-result-object v5

    #@16
    .line 765
    .local v5, "secondChild":Landroid/view/View;
    if-eqz v5, :cond_0

    #@18
    .line 766
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@1a
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@1d
    move-result v10

    #@1e
    float-to-int v10, v10

    #@1f
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@22
    move-result v11

    #@23
    float-to-int v11, v11

    #@24
    invoke-virtual {v9, v5, v10, v11}, Landroid/support/v4/widget/ViewDragHelper;->isViewUnder(Landroid/view/View;II)Z

    #@27
    move-result v9

    #@28
    if-nez v9, :cond_2

    #@2a
    const/4 v9, 0x1

    #@2b
    :goto_0
    iput-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@2d
    .line 771
    .end local v5    # "secondChild":Landroid/view/View;
    :cond_0
    iget-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@2f
    if-eqz v9, :cond_1

    #@31
    iget-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mIsUnableToDrag:Z

    #@33
    if-eqz v9, :cond_3

    #@35
    if-eqz v0, :cond_3

    #@37
    .line 772
    :cond_1
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@39
    invoke-virtual {v9}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@3c
    .line 773
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onInterceptTouchEvent(Landroid/view/MotionEvent;)Z

    #@3f
    move-result v9

    #@40
    .line 814
    :goto_1
    return v9

    #@41
    .line 766
    .restart local v5    # "secondChild":Landroid/view/View;
    :cond_2
    const/4 v9, 0x0

    #@42
    goto :goto_0

    #@43
    .line 776
    .end local v5    # "secondChild":Landroid/view/View;
    :cond_3
    const/4 v9, 0x3

    #@44
    if-eq v0, v9, :cond_4

    #@46
    const/4 v9, 0x1

    #@47
    if-ne v0, v9, :cond_5

    #@49
    .line 777
    :cond_4
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@4b
    invoke-virtual {v9}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@4e
    .line 778
    const/4 v9, 0x0

    #@4f
    goto :goto_1

    #@50
    .line 781
    :cond_5
    const/4 v4, 0x0

    #@51
    .line 783
    .local v4, "interceptTap":Z
    packed-switch v0, :pswitch_data_0

    #@54
    .line 812
    :cond_6
    :goto_2
    :pswitch_0
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@56
    invoke-virtual {v9, p1}, Landroid/support/v4/widget/ViewDragHelper;->shouldInterceptTouchEvent(Landroid/view/MotionEvent;)Z

    #@59
    move-result v3

    #@5a
    .line 814
    .local v3, "interceptForDrag":Z
    if-nez v3, :cond_7

    #@5c
    if-eqz v4, :cond_8

    #@5e
    :cond_7
    const/4 v9, 0x1

    #@5f
    goto :goto_1

    #@60
    .line 785
    .end local v3    # "interceptForDrag":Z
    :pswitch_1
    const/4 v9, 0x0

    #@61
    iput-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mIsUnableToDrag:Z

    #@63
    .line 786
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@66
    move-result v7

    #@67
    .line 787
    .local v7, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@6a
    move-result v8

    #@6b
    .line 788
    .local v8, "y":F
    iput v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionX:F

    #@6d
    .line 789
    iput v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionY:F

    #@6f
    .line 791
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@71
    iget-object v10, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@73
    float-to-int v11, v7

    #@74
    float-to-int v12, v8

    #@75
    invoke-virtual {v9, v10, v11, v12}, Landroid/support/v4/widget/ViewDragHelper;->isViewUnder(Landroid/view/View;II)Z

    #@78
    move-result v9

    #@79
    if-eqz v9, :cond_6

    #@7b
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@7d
    invoke-virtual {p0, v9}, Landroid/support/v4/widget/SlidingPaneLayout;->isDimmed(Landroid/view/View;)Z

    #@80
    move-result v9

    #@81
    if-eqz v9, :cond_6

    #@83
    .line 793
    const/4 v4, 0x1

    #@84
    goto :goto_2

    #@85
    .line 799
    .end local v7    # "x":F
    .end local v8    # "y":F
    :pswitch_2
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@88
    move-result v7

    #@89
    .line 800
    .restart local v7    # "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@8c
    move-result v8

    #@8d
    .line 801
    .restart local v8    # "y":F
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionX:F

    #@8f
    sub-float v9, v7, v9

    #@91
    invoke-static {v9}, Ljava/lang/Math;->abs(F)F

    #@94
    move-result v1

    #@95
    .line 802
    .local v1, "adx":F
    iget v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionY:F

    #@97
    sub-float v9, v8, v9

    #@99
    invoke-static {v9}, Ljava/lang/Math;->abs(F)F

    #@9c
    move-result v2

    #@9d
    .line 803
    .local v2, "ady":F
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@9f
    invoke-virtual {v9}, Landroid/support/v4/widget/ViewDragHelper;->getTouchSlop()I

    #@a2
    move-result v6

    #@a3
    .line 804
    .local v6, "slop":I
    int-to-float v9, v6

    #@a4
    cmpl-float v9, v1, v9

    #@a6
    if-lez v9, :cond_6

    #@a8
    cmpl-float v9, v2, v1

    #@aa
    if-lez v9, :cond_6

    #@ac
    .line 805
    iget-object v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@ae
    invoke-virtual {v9}, Landroid/support/v4/widget/ViewDragHelper;->cancel()V

    #@b1
    .line 806
    const/4 v9, 0x1

    #@b2
    iput-boolean v9, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mIsUnableToDrag:Z

    #@b4
    .line 807
    const/4 v9, 0x0

    #@b5
    goto :goto_1

    #@b6
    .line 814
    .end local v1    # "adx":F
    .end local v2    # "ady":F
    .end local v6    # "slop":I
    .end local v7    # "x":F
    .end local v8    # "y":F
    .restart local v3    # "interceptForDrag":Z
    :cond_8
    const/4 v9, 0x0

    #@b7
    goto :goto_1

    #@b8
    .line 783
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
        :pswitch_2
    .end packed-switch
.end method

.method protected onLayout(ZIIII)V
    .locals 28
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 654
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@3
    move-result v12

    #@4
    .line 655
    .local v12, "isLayoutRtl":Z
    if-eqz v12, :cond_1

    #@6
    .line 656
    move-object/from16 v0, p0

    #@8
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@a
    move-object/from16 v25, v0

    #@c
    const/16 v26, 0x2

    #@e
    invoke-virtual/range {v25 .. v26}, Landroid/support/v4/widget/ViewDragHelper;->setEdgeTrackingEnabled(I)V

    #@11
    .line 660
    :goto_0
    sub-int v23, p4, p2

    #@13
    .line 661
    .local v23, "width":I
    if-eqz v12, :cond_2

    #@15
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@18
    move-result v19

    #@19
    .line 662
    .local v19, "paddingStart":I
    :goto_1
    if-eqz v12, :cond_3

    #@1b
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@1e
    move-result v18

    #@1f
    .line 663
    .local v18, "paddingEnd":I
    :goto_2
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingTop()I

    #@22
    move-result v20

    #@23
    .line 665
    .local v20, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@26
    move-result v6

    #@27
    .line 666
    .local v6, "childCount":I
    move/from16 v24, v19

    #@29
    .line 667
    .local v24, "xStart":I
    move/from16 v16, v24

    #@2b
    .line 669
    .local v16, "nextXStart":I
    move-object/from16 v0, p0

    #@2d
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@2f
    move/from16 v25, v0

    #@31
    if-eqz v25, :cond_0

    #@33
    .line 670
    move-object/from16 v0, p0

    #@35
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@37
    move/from16 v25, v0

    #@39
    if-eqz v25, :cond_4

    #@3b
    move-object/from16 v0, p0

    #@3d
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@3f
    move/from16 v25, v0

    #@41
    if-eqz v25, :cond_4

    #@43
    const/high16 v25, 0x3f800000    # 1.0f

    #@45
    :goto_3
    move/from16 v0, v25

    #@47
    move-object/from16 v1, p0

    #@49
    iput v0, v1, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@4b
    .line 673
    :cond_0
    const/4 v11, 0x0

    #@4c
    .local v11, "i":I
    :goto_4
    if-ge v11, v6, :cond_b

    #@4e
    .line 674
    move-object/from16 v0, p0

    #@50
    invoke-virtual {v0, v11}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@53
    move-result-object v4

    #@54
    .line 676
    .local v4, "child":Landroid/view/View;
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@57
    move-result v25

    #@58
    const/16 v26, 0x8

    #@5a
    move/from16 v0, v25

    #@5c
    move/from16 v1, v26

    #@5e
    if-ne v0, v1, :cond_5

    #@60
    .line 673
    :goto_5
    add-int/lit8 v11, v11, 0x1

    #@62
    goto :goto_4

    #@63
    .line 658
    .end local v4    # "child":Landroid/view/View;
    .end local v6    # "childCount":I
    .end local v11    # "i":I
    .end local v16    # "nextXStart":I
    .end local v18    # "paddingEnd":I
    .end local v19    # "paddingStart":I
    .end local v20    # "paddingTop":I
    .end local v23    # "width":I
    .end local v24    # "xStart":I
    :cond_1
    move-object/from16 v0, p0

    #@65
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@67
    move-object/from16 v25, v0

    #@69
    const/16 v26, 0x1

    #@6b
    invoke-virtual/range {v25 .. v26}, Landroid/support/v4/widget/ViewDragHelper;->setEdgeTrackingEnabled(I)V

    #@6e
    goto :goto_0

    #@6f
    .line 661
    .restart local v23    # "width":I
    :cond_2
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@72
    move-result v19

    #@73
    goto :goto_1

    #@74
    .line 662
    .restart local v19    # "paddingStart":I
    :cond_3
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@77
    move-result v18

    #@78
    goto :goto_2

    #@79
    .line 670
    .restart local v6    # "childCount":I
    .restart local v16    # "nextXStart":I
    .restart local v18    # "paddingEnd":I
    .restart local v20    # "paddingTop":I
    .restart local v24    # "xStart":I
    :cond_4
    const/16 v25, 0x0

    #@7b
    goto :goto_3

    #@7c
    .line 680
    .restart local v4    # "child":Landroid/view/View;
    .restart local v11    # "i":I
    :cond_5
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@7f
    move-result-object v13

    #@80
    check-cast v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@82
    .line 682
    .local v13, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@85
    move-result v10

    #@86
    .line 683
    .local v10, "childWidth":I
    const/16 v17, 0x0

    #@88
    .line 685
    .local v17, "offset":I
    iget-boolean v0, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->slideable:Z

    #@8a
    move/from16 v25, v0

    #@8c
    if-eqz v25, :cond_8

    #@8e
    .line 686
    iget v0, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@90
    move/from16 v25, v0

    #@92
    iget v0, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@94
    move/from16 v26, v0

    #@96
    add-int v15, v25, v26

    #@98
    .line 687
    .local v15, "margin":I
    sub-int v25, v23, v18

    #@9a
    move-object/from16 v0, p0

    #@9c
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mOverhangSize:I

    #@9e
    move/from16 v26, v0

    #@a0
    sub-int v25, v25, v26

    #@a2
    move/from16 v0, v16

    #@a4
    move/from16 v1, v25

    #@a6
    invoke-static {v0, v1}, Ljava/lang/Math;->min(II)I

    #@a9
    move-result v25

    #@aa
    sub-int v25, v25, v24

    #@ac
    sub-int v22, v25, v15

    #@ae
    .line 689
    .local v22, "range":I
    move/from16 v0, v22

    #@b0
    move-object/from16 v1, p0

    #@b2
    iput v0, v1, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@b4
    .line 690
    if-eqz v12, :cond_6

    #@b6
    iget v14, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@b8
    .line 691
    .local v14, "lpMargin":I
    :goto_6
    add-int v25, v24, v14

    #@ba
    add-int v25, v25, v22

    #@bc
    div-int/lit8 v26, v10, 0x2

    #@be
    add-int v25, v25, v26

    #@c0
    sub-int v26, v23, v18

    #@c2
    move/from16 v0, v25

    #@c4
    move/from16 v1, v26

    #@c6
    if-le v0, v1, :cond_7

    #@c8
    const/16 v25, 0x1

    #@ca
    :goto_7
    move/from16 v0, v25

    #@cc
    iput-boolean v0, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@ce
    .line 693
    move/from16 v0, v22

    #@d0
    int-to-float v0, v0

    #@d1
    move/from16 v25, v0

    #@d3
    move-object/from16 v0, p0

    #@d5
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@d7
    move/from16 v26, v0

    #@d9
    mul-float v25, v25, v26

    #@db
    move/from16 v0, v25

    #@dd
    float-to-int v0, v0

    #@de
    move/from16 v21, v0

    #@e0
    .line 694
    .local v21, "pos":I
    add-int v25, v21, v14

    #@e2
    add-int v24, v24, v25

    #@e4
    .line 695
    move/from16 v0, v21

    #@e6
    int-to-float v0, v0

    #@e7
    move/from16 v25, v0

    #@e9
    move-object/from16 v0, p0

    #@eb
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@ed
    move/from16 v26, v0

    #@ef
    move/from16 v0, v26

    #@f1
    int-to-float v0, v0

    #@f2
    move/from16 v26, v0

    #@f4
    div-float v25, v25, v26

    #@f6
    move/from16 v0, v25

    #@f8
    move-object/from16 v1, p0

    #@fa
    iput v0, v1, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@fc
    .line 705
    .end local v14    # "lpMargin":I
    .end local v15    # "margin":I
    .end local v21    # "pos":I
    .end local v22    # "range":I
    :goto_8
    if-eqz v12, :cond_a

    #@fe
    .line 706
    sub-int v25, v23, v24

    #@100
    add-int v8, v25, v17

    #@102
    .line 707
    .local v8, "childRight":I
    sub-int v7, v8, v10

    #@104
    .line 713
    .local v7, "childLeft":I
    :goto_9
    move/from16 v9, v20

    #@106
    .line 714
    .local v9, "childTop":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@109
    move-result v25

    #@10a
    add-int v5, v9, v25

    #@10c
    .line 715
    .local v5, "childBottom":I
    move/from16 v0, v20

    #@10e
    invoke-virtual {v4, v7, v0, v8, v5}, Landroid/view/View;->layout(IIII)V

    #@111
    .line 717
    invoke-virtual {v4}, Landroid/view/View;->getWidth()I

    #@114
    move-result v25

    #@115
    add-int v16, v16, v25

    #@117
    goto/16 :goto_5

    #@119
    .line 690
    .end local v5    # "childBottom":I
    .end local v7    # "childLeft":I
    .end local v8    # "childRight":I
    .end local v9    # "childTop":I
    .restart local v15    # "margin":I
    .restart local v22    # "range":I
    :cond_6
    iget v14, v13, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@11b
    goto :goto_6

    #@11c
    .line 691
    .restart local v14    # "lpMargin":I
    :cond_7
    const/16 v25, 0x0

    #@11e
    goto :goto_7

    #@11f
    .line 696
    .end local v14    # "lpMargin":I
    .end local v15    # "margin":I
    .end local v22    # "range":I
    :cond_8
    move-object/from16 v0, p0

    #@121
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@123
    move/from16 v25, v0

    #@125
    if-eqz v25, :cond_9

    #@127
    move-object/from16 v0, p0

    #@129
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@12b
    move/from16 v25, v0

    #@12d
    if-eqz v25, :cond_9

    #@12f
    .line 697
    const/high16 v25, 0x3f800000    # 1.0f

    #@131
    move-object/from16 v0, p0

    #@133
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@135
    move/from16 v26, v0

    #@137
    sub-float v25, v25, v26

    #@139
    move-object/from16 v0, p0

    #@13b
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@13d
    move/from16 v26, v0

    #@13f
    move/from16 v0, v26

    #@141
    int-to-float v0, v0

    #@142
    move/from16 v26, v0

    #@144
    mul-float v25, v25, v26

    #@146
    move/from16 v0, v25

    #@148
    float-to-int v0, v0

    #@149
    move/from16 v17, v0

    #@14b
    .line 698
    move/from16 v24, v16

    #@14d
    goto :goto_8

    #@14e
    .line 700
    :cond_9
    move/from16 v24, v16

    #@150
    goto :goto_8

    #@151
    .line 709
    :cond_a
    sub-int v7, v24, v17

    #@153
    .line 710
    .restart local v7    # "childLeft":I
    add-int v8, v7, v10

    #@155
    .restart local v8    # "childRight":I
    goto :goto_9

    #@156
    .line 720
    .end local v4    # "child":Landroid/view/View;
    .end local v7    # "childLeft":I
    .end local v8    # "childRight":I
    .end local v10    # "childWidth":I
    .end local v13    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    .end local v17    # "offset":I
    :cond_b
    move-object/from16 v0, p0

    #@158
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@15a
    move/from16 v25, v0

    #@15c
    if-eqz v25, :cond_e

    #@15e
    .line 721
    move-object/from16 v0, p0

    #@160
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@162
    move/from16 v25, v0

    #@164
    if-eqz v25, :cond_f

    #@166
    .line 722
    move-object/from16 v0, p0

    #@168
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@16a
    move/from16 v25, v0

    #@16c
    if-eqz v25, :cond_c

    #@16e
    .line 723
    move-object/from16 v0, p0

    #@170
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@172
    move/from16 v25, v0

    #@174
    move-object/from16 v0, p0

    #@176
    move/from16 v1, v25

    #@178
    invoke-direct {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->parallaxOtherViews(F)V

    #@17b
    .line 725
    :cond_c
    move-object/from16 v0, p0

    #@17d
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@17f
    move-object/from16 v25, v0

    #@181
    invoke-virtual/range {v25 .. v25}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@184
    move-result-object v25

    #@185
    check-cast v25, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@187
    move-object/from16 v0, v25

    #@189
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@18b
    move/from16 v25, v0

    #@18d
    if-eqz v25, :cond_d

    #@18f
    .line 726
    move-object/from16 v0, p0

    #@191
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@193
    move-object/from16 v25, v0

    #@195
    move-object/from16 v0, p0

    #@197
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F

    #@199
    move/from16 v26, v0

    #@19b
    move-object/from16 v0, p0

    #@19d
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@19f
    move/from16 v27, v0

    #@1a1
    move-object/from16 v0, p0

    #@1a3
    move-object/from16 v1, v25

    #@1a5
    move/from16 v2, v26

    #@1a7
    move/from16 v3, v27

    #@1a9
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/widget/SlidingPaneLayout;->dimChildView(Landroid/view/View;FI)V

    #@1ac
    .line 734
    :cond_d
    move-object/from16 v0, p0

    #@1ae
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@1b0
    move-object/from16 v25, v0

    #@1b2
    move-object/from16 v0, p0

    #@1b4
    move-object/from16 v1, v25

    #@1b6
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->updateObscuredViewsVisibility(Landroid/view/View;)V

    #@1b9
    .line 737
    :cond_e
    const/16 v25, 0x0

    #@1bb
    move/from16 v0, v25

    #@1bd
    move-object/from16 v1, p0

    #@1bf
    iput-boolean v0, v1, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@1c1
    .line 738
    return-void

    #@1c2
    .line 730
    :cond_f
    const/4 v11, 0x0

    #@1c3
    :goto_a
    if-ge v11, v6, :cond_d

    #@1c5
    .line 731
    move-object/from16 v0, p0

    #@1c7
    invoke-virtual {v0, v11}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@1ca
    move-result-object v25

    #@1cb
    const/16 v26, 0x0

    #@1cd
    move-object/from16 v0, p0

    #@1cf
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@1d1
    move/from16 v27, v0

    #@1d3
    move-object/from16 v0, p0

    #@1d5
    move-object/from16 v1, v25

    #@1d7
    move/from16 v2, v26

    #@1d9
    move/from16 v3, v27

    #@1db
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/widget/SlidingPaneLayout;->dimChildView(Landroid/view/View;FI)V

    #@1de
    .line 730
    add-int/lit8 v11, v11, 0x1

    #@1e0
    goto :goto_a
.end method

.method protected onMeasure(II)V
    .locals 31
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 442
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v25

    #@4
    .line 443
    .local v25, "widthMode":I
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@7
    move-result v27

    #@8
    .line 444
    .local v27, "widthSize":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@b
    move-result v12

    #@c
    .line 445
    .local v12, "heightMode":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@f
    move-result v13

    #@10
    .line 447
    .local v13, "heightSize":I
    const/high16 v29, 0x40000000    # 2.0f

    #@12
    move/from16 v0, v25

    #@14
    move/from16 v1, v29

    #@16
    if-eq v0, v1, :cond_5

    #@18
    .line 448
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isInEditMode()Z

    #@1b
    move-result v29

    #@1c
    if-eqz v29, :cond_4

    #@1e
    .line 453
    const/high16 v29, -0x80000000

    #@20
    move/from16 v0, v25

    #@22
    move/from16 v1, v29

    #@24
    if-ne v0, v1, :cond_3

    #@26
    .line 454
    const/high16 v25, 0x40000000    # 2.0f

    #@28
    .line 476
    :cond_0
    :goto_0
    const/16 v16, 0x0

    #@2a
    .line 477
    .local v16, "layoutHeight":I
    const/16 v18, -0x1

    #@2c
    .line 478
    .local v18, "maxLayoutHeight":I
    sparse-switch v12, :sswitch_data_0

    #@2f
    .line 487
    :goto_1
    const/16 v23, 0x0

    #@31
    .line 488
    .local v23, "weightSum":F
    const/4 v4, 0x0

    #@32
    .line 489
    .local v4, "canSlide":Z
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@35
    move-result v29

    #@36
    sub-int v29, v27, v29

    #@38
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@3b
    move-result v30

    #@3c
    sub-int v24, v29, v30

    #@3e
    .line 490
    .local v24, "widthAvailable":I
    move/from16 v26, v24

    #@40
    .line 491
    .local v26, "widthRemaining":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@43
    move-result v6

    #@44
    .line 493
    .local v6, "childCount":I
    const/16 v29, 0x2

    #@46
    move/from16 v0, v29

    #@48
    if-le v6, v0, :cond_1

    #@4a
    .line 494
    const-string v29, "SlidingPaneLayout"

    #@4c
    const-string v30, "onMeasure: More than two child views are not supported."

    #@4e
    invoke-static/range {v29 .. v30}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@51
    .line 498
    :cond_1
    const/16 v29, 0x0

    #@53
    move-object/from16 v0, v29

    #@55
    move-object/from16 v1, p0

    #@57
    iput-object v0, v1, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@59
    .line 502
    const/4 v15, 0x0

    #@5a
    .local v15, "i":I
    :goto_2
    if-ge v15, v6, :cond_f

    #@5c
    .line 503
    move-object/from16 v0, p0

    #@5e
    invoke-virtual {v0, v15}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@61
    move-result-object v5

    #@62
    .line 504
    .local v5, "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@65
    move-result-object v17

    #@66
    check-cast v17, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@68
    .line 506
    .local v17, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@6b
    move-result v29

    #@6c
    const/16 v30, 0x8

    #@6e
    move/from16 v0, v29

    #@70
    move/from16 v1, v30

    #@72
    if-ne v0, v1, :cond_7

    #@74
    .line 507
    const/16 v29, 0x0

    #@76
    move/from16 v0, v29

    #@78
    move-object/from16 v1, v17

    #@7a
    iput-boolean v0, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->dimWhenOffset:Z

    #@7c
    .line 502
    :cond_2
    :goto_3
    add-int/lit8 v15, v15, 0x1

    #@7e
    goto :goto_2

    #@7f
    .line 455
    .end local v4    # "canSlide":Z
    .end local v5    # "child":Landroid/view/View;
    .end local v6    # "childCount":I
    .end local v15    # "i":I
    .end local v16    # "layoutHeight":I
    .end local v17    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    .end local v18    # "maxLayoutHeight":I
    .end local v23    # "weightSum":F
    .end local v24    # "widthAvailable":I
    .end local v26    # "widthRemaining":I
    :cond_3
    if-nez v25, :cond_0

    #@81
    .line 456
    const/high16 v25, 0x40000000    # 2.0f

    #@83
    .line 457
    const/16 v27, 0x12c

    #@85
    goto :goto_0

    #@86
    .line 460
    :cond_4
    new-instance v29, Ljava/lang/IllegalStateException;

    #@88
    const-string v30, "Width must have an exact value or MATCH_PARENT"

    #@8a
    invoke-direct/range {v29 .. v30}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@8d
    throw v29

    #@8e
    .line 462
    :cond_5
    if-nez v12, :cond_0

    #@90
    .line 463
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isInEditMode()Z

    #@93
    move-result v29

    #@94
    if-eqz v29, :cond_6

    #@96
    .line 467
    if-nez v12, :cond_0

    #@98
    .line 468
    const/high16 v12, -0x80000000

    #@9a
    .line 469
    const/16 v13, 0x12c

    #@9c
    goto :goto_0

    #@9d
    .line 472
    :cond_6
    new-instance v29, Ljava/lang/IllegalStateException;

    #@9f
    const-string v30, "Height must not be UNSPECIFIED"

    #@a1
    invoke-direct/range {v29 .. v30}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@a4
    throw v29

    #@a5
    .line 480
    .restart local v16    # "layoutHeight":I
    .restart local v18    # "maxLayoutHeight":I
    :sswitch_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingTop()I

    #@a8
    move-result v29

    #@a9
    sub-int v29, v13, v29

    #@ab
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingBottom()I

    #@ae
    move-result v30

    #@af
    sub-int v18, v29, v30

    #@b1
    move/from16 v16, v18

    #@b3
    .line 481
    goto/16 :goto_1

    #@b5
    .line 483
    :sswitch_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingTop()I

    #@b8
    move-result v29

    #@b9
    sub-int v29, v13, v29

    #@bb
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingBottom()I

    #@be
    move-result v30

    #@bf
    sub-int v18, v29, v30

    #@c1
    goto/16 :goto_1

    #@c3
    .line 511
    .restart local v4    # "canSlide":Z
    .restart local v5    # "child":Landroid/view/View;
    .restart local v6    # "childCount":I
    .restart local v15    # "i":I
    .restart local v17    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    .restart local v23    # "weightSum":F
    .restart local v24    # "widthAvailable":I
    .restart local v26    # "widthRemaining":I
    :cond_7
    move-object/from16 v0, v17

    #@c5
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@c7
    move/from16 v29, v0

    #@c9
    const/16 v30, 0x0

    #@cb
    cmpl-float v29, v29, v30

    #@cd
    if-lez v29, :cond_8

    #@cf
    .line 512
    move-object/from16 v0, v17

    #@d1
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@d3
    move/from16 v29, v0

    #@d5
    add-float v23, v23, v29

    #@d7
    .line 516
    move-object/from16 v0, v17

    #@d9
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@db
    move/from16 v29, v0

    #@dd
    if-eqz v29, :cond_2

    #@df
    .line 520
    :cond_8
    move-object/from16 v0, v17

    #@e1
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@e3
    move/from16 v29, v0

    #@e5
    move-object/from16 v0, v17

    #@e7
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@e9
    move/from16 v30, v0

    #@eb
    add-int v14, v29, v30

    #@ed
    .line 521
    .local v14, "horizontalMargin":I
    move-object/from16 v0, v17

    #@ef
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@f1
    move/from16 v29, v0

    #@f3
    const/16 v30, -0x2

    #@f5
    move/from16 v0, v29

    #@f7
    move/from16 v1, v30

    #@f9
    if-ne v0, v1, :cond_a

    #@fb
    .line 522
    sub-int v29, v24, v14

    #@fd
    const/high16 v30, -0x80000000

    #@ff
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@102
    move-result v10

    #@103
    .line 532
    .local v10, "childWidthSpec":I
    :goto_4
    move-object/from16 v0, v17

    #@105
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@107
    move/from16 v29, v0

    #@109
    const/16 v30, -0x2

    #@10b
    move/from16 v0, v29

    #@10d
    move/from16 v1, v30

    #@10f
    if-ne v0, v1, :cond_c

    #@111
    .line 533
    const/high16 v29, -0x80000000

    #@113
    move/from16 v0, v18

    #@115
    move/from16 v1, v29

    #@117
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@11a
    move-result v8

    #@11b
    .line 540
    .local v8, "childHeightSpec":I
    :goto_5
    invoke-virtual {v5, v10, v8}, Landroid/view/View;->measure(II)V

    #@11e
    .line 541
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@121
    move-result v9

    #@122
    .line 542
    .local v9, "childWidth":I
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@125
    move-result v7

    #@126
    .line 544
    .local v7, "childHeight":I
    const/high16 v29, -0x80000000

    #@128
    move/from16 v0, v29

    #@12a
    if-ne v12, v0, :cond_9

    #@12c
    move/from16 v0, v16

    #@12e
    if-le v7, v0, :cond_9

    #@130
    .line 545
    move/from16 v0, v18

    #@132
    invoke-static {v7, v0}, Ljava/lang/Math;->min(II)I

    #@135
    move-result v16

    #@136
    .line 548
    :cond_9
    sub-int v26, v26, v9

    #@138
    .line 549
    if-gez v26, :cond_e

    #@13a
    const/16 v29, 0x1

    #@13c
    :goto_6
    move/from16 v0, v29

    #@13e
    move-object/from16 v1, v17

    #@140
    iput-boolean v0, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->slideable:Z

    #@142
    or-int v4, v4, v29

    #@144
    .line 550
    move-object/from16 v0, v17

    #@146
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->slideable:Z

    #@148
    move/from16 v29, v0

    #@14a
    if-eqz v29, :cond_2

    #@14c
    .line 551
    move-object/from16 v0, p0

    #@14e
    iput-object v5, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@150
    goto/16 :goto_3

    #@152
    .line 524
    .end local v7    # "childHeight":I
    .end local v8    # "childHeightSpec":I
    .end local v9    # "childWidth":I
    .end local v10    # "childWidthSpec":I
    :cond_a
    move-object/from16 v0, v17

    #@154
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@156
    move/from16 v29, v0

    #@158
    const/16 v30, -0x1

    #@15a
    move/from16 v0, v29

    #@15c
    move/from16 v1, v30

    #@15e
    if-ne v0, v1, :cond_b

    #@160
    .line 525
    sub-int v29, v24, v14

    #@162
    const/high16 v30, 0x40000000    # 2.0f

    #@164
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@167
    move-result v10

    #@168
    .restart local v10    # "childWidthSpec":I
    goto :goto_4

    #@169
    .line 528
    .end local v10    # "childWidthSpec":I
    :cond_b
    move-object/from16 v0, v17

    #@16b
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@16d
    move/from16 v29, v0

    #@16f
    const/high16 v30, 0x40000000    # 2.0f

    #@171
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@174
    move-result v10

    #@175
    .restart local v10    # "childWidthSpec":I
    goto :goto_4

    #@176
    .line 534
    :cond_c
    move-object/from16 v0, v17

    #@178
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@17a
    move/from16 v29, v0

    #@17c
    const/16 v30, -0x1

    #@17e
    move/from16 v0, v29

    #@180
    move/from16 v1, v30

    #@182
    if-ne v0, v1, :cond_d

    #@184
    .line 535
    const/high16 v29, 0x40000000    # 2.0f

    #@186
    move/from16 v0, v18

    #@188
    move/from16 v1, v29

    #@18a
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@18d
    move-result v8

    #@18e
    .restart local v8    # "childHeightSpec":I
    goto :goto_5

    #@18f
    .line 537
    .end local v8    # "childHeightSpec":I
    :cond_d
    move-object/from16 v0, v17

    #@191
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@193
    move/from16 v29, v0

    #@195
    const/high16 v30, 0x40000000    # 2.0f

    #@197
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@19a
    move-result v8

    #@19b
    .restart local v8    # "childHeightSpec":I
    goto :goto_5

    #@19c
    .line 549
    .restart local v7    # "childHeight":I
    .restart local v9    # "childWidth":I
    :cond_e
    const/16 v29, 0x0

    #@19e
    goto :goto_6

    #@19f
    .line 556
    .end local v5    # "child":Landroid/view/View;
    .end local v7    # "childHeight":I
    .end local v8    # "childHeightSpec":I
    .end local v9    # "childWidth":I
    .end local v10    # "childWidthSpec":I
    .end local v14    # "horizontalMargin":I
    .end local v17    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    :cond_f
    if-nez v4, :cond_10

    #@1a1
    const/16 v29, 0x0

    #@1a3
    cmpl-float v29, v23, v29

    #@1a5
    if-lez v29, :cond_1e

    #@1a7
    .line 557
    :cond_10
    move-object/from16 v0, p0

    #@1a9
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mOverhangSize:I

    #@1ab
    move/from16 v29, v0

    #@1ad
    sub-int v11, v24, v29

    #@1af
    .line 559
    .local v11, "fixedPanelWidthLimit":I
    const/4 v15, 0x0

    #@1b0
    :goto_7
    if-ge v15, v6, :cond_1e

    #@1b2
    .line 560
    move-object/from16 v0, p0

    #@1b4
    invoke-virtual {v0, v15}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@1b7
    move-result-object v5

    #@1b8
    .line 562
    .restart local v5    # "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@1bb
    move-result v29

    #@1bc
    const/16 v30, 0x8

    #@1be
    move/from16 v0, v29

    #@1c0
    move/from16 v1, v30

    #@1c2
    if-ne v0, v1, :cond_12

    #@1c4
    .line 559
    :cond_11
    :goto_8
    add-int/lit8 v15, v15, 0x1

    #@1c6
    goto :goto_7

    #@1c7
    .line 566
    :cond_12
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1ca
    move-result-object v17

    #@1cb
    check-cast v17, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@1cd
    .line 568
    .restart local v17    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@1d0
    move-result v29

    #@1d1
    const/16 v30, 0x8

    #@1d3
    move/from16 v0, v29

    #@1d5
    move/from16 v1, v30

    #@1d7
    if-eq v0, v1, :cond_11

    #@1d9
    .line 572
    move-object/from16 v0, v17

    #@1db
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@1dd
    move/from16 v29, v0

    #@1df
    if-nez v29, :cond_14

    #@1e1
    move-object/from16 v0, v17

    #@1e3
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@1e5
    move/from16 v29, v0

    #@1e7
    const/16 v30, 0x0

    #@1e9
    cmpl-float v29, v29, v30

    #@1eb
    if-lez v29, :cond_14

    #@1ed
    const/16 v22, 0x1

    #@1ef
    .line 573
    .local v22, "skippedFirstPass":Z
    :goto_9
    if-eqz v22, :cond_15

    #@1f1
    const/16 v20, 0x0

    #@1f3
    .line 574
    .local v20, "measuredWidth":I
    :goto_a
    if-eqz v4, :cond_19

    #@1f5
    move-object/from16 v0, p0

    #@1f7
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@1f9
    move-object/from16 v29, v0

    #@1fb
    move-object/from16 v0, v29

    #@1fd
    if-eq v5, v0, :cond_19

    #@1ff
    .line 575
    move-object/from16 v0, v17

    #@201
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@203
    move/from16 v29, v0

    #@205
    if-gez v29, :cond_11

    #@207
    move/from16 v0, v20

    #@209
    if-gt v0, v11, :cond_13

    #@20b
    move-object/from16 v0, v17

    #@20d
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@20f
    move/from16 v29, v0

    #@211
    const/16 v30, 0x0

    #@213
    cmpl-float v29, v29, v30

    #@215
    if-lez v29, :cond_11

    #@217
    .line 579
    :cond_13
    if-eqz v22, :cond_18

    #@219
    .line 582
    move-object/from16 v0, v17

    #@21b
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@21d
    move/from16 v29, v0

    #@21f
    const/16 v30, -0x2

    #@221
    move/from16 v0, v29

    #@223
    move/from16 v1, v30

    #@225
    if-ne v0, v1, :cond_16

    #@227
    .line 583
    const/high16 v29, -0x80000000

    #@229
    move/from16 v0, v18

    #@22b
    move/from16 v1, v29

    #@22d
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@230
    move-result v8

    #@231
    .line 596
    .restart local v8    # "childHeightSpec":I
    :goto_b
    const/high16 v29, 0x40000000    # 2.0f

    #@233
    move/from16 v0, v29

    #@235
    invoke-static {v11, v0}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@238
    move-result v10

    #@239
    .line 598
    .restart local v10    # "childWidthSpec":I
    invoke-virtual {v5, v10, v8}, Landroid/view/View;->measure(II)V

    #@23c
    goto :goto_8

    #@23d
    .line 572
    .end local v8    # "childHeightSpec":I
    .end local v10    # "childWidthSpec":I
    .end local v20    # "measuredWidth":I
    .end local v22    # "skippedFirstPass":Z
    :cond_14
    const/16 v22, 0x0

    #@23f
    goto :goto_9

    #@240
    .line 573
    .restart local v22    # "skippedFirstPass":Z
    :cond_15
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@243
    move-result v20

    #@244
    goto :goto_a

    #@245
    .line 585
    .restart local v20    # "measuredWidth":I
    :cond_16
    move-object/from16 v0, v17

    #@247
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@249
    move/from16 v29, v0

    #@24b
    const/16 v30, -0x1

    #@24d
    move/from16 v0, v29

    #@24f
    move/from16 v1, v30

    #@251
    if-ne v0, v1, :cond_17

    #@253
    .line 586
    const/high16 v29, 0x40000000    # 2.0f

    #@255
    move/from16 v0, v18

    #@257
    move/from16 v1, v29

    #@259
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@25c
    move-result v8

    #@25d
    .restart local v8    # "childHeightSpec":I
    goto :goto_b

    #@25e
    .line 589
    .end local v8    # "childHeightSpec":I
    :cond_17
    move-object/from16 v0, v17

    #@260
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@262
    move/from16 v29, v0

    #@264
    const/high16 v30, 0x40000000    # 2.0f

    #@266
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@269
    move-result v8

    #@26a
    .restart local v8    # "childHeightSpec":I
    goto :goto_b

    #@26b
    .line 593
    .end local v8    # "childHeightSpec":I
    :cond_18
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@26e
    move-result v29

    #@26f
    const/high16 v30, 0x40000000    # 2.0f

    #@271
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@274
    move-result v8

    #@275
    .restart local v8    # "childHeightSpec":I
    goto :goto_b

    #@276
    .line 600
    .end local v8    # "childHeightSpec":I
    :cond_19
    move-object/from16 v0, v17

    #@278
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@27a
    move/from16 v29, v0

    #@27c
    const/16 v30, 0x0

    #@27e
    cmpl-float v29, v29, v30

    #@280
    if-lez v29, :cond_11

    #@282
    .line 602
    move-object/from16 v0, v17

    #@284
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->width:I

    #@286
    move/from16 v29, v0

    #@288
    if-nez v29, :cond_1c

    #@28a
    .line 604
    move-object/from16 v0, v17

    #@28c
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@28e
    move/from16 v29, v0

    #@290
    const/16 v30, -0x2

    #@292
    move/from16 v0, v29

    #@294
    move/from16 v1, v30

    #@296
    if-ne v0, v1, :cond_1a

    #@298
    .line 605
    const/high16 v29, -0x80000000

    #@29a
    move/from16 v0, v18

    #@29c
    move/from16 v1, v29

    #@29e
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2a1
    move-result v8

    #@2a2
    .line 619
    .restart local v8    # "childHeightSpec":I
    :goto_c
    if-eqz v4, :cond_1d

    #@2a4
    .line 621
    move-object/from16 v0, v17

    #@2a6
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@2a8
    move/from16 v29, v0

    #@2aa
    move-object/from16 v0, v17

    #@2ac
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@2ae
    move/from16 v30, v0

    #@2b0
    add-int v14, v29, v30

    #@2b2
    .line 622
    .restart local v14    # "horizontalMargin":I
    sub-int v21, v24, v14

    #@2b4
    .line 623
    .local v21, "newWidth":I
    const/high16 v29, 0x40000000    # 2.0f

    #@2b6
    move/from16 v0, v21

    #@2b8
    move/from16 v1, v29

    #@2ba
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2bd
    move-result v10

    #@2be
    .line 625
    .restart local v10    # "childWidthSpec":I
    move/from16 v0, v20

    #@2c0
    move/from16 v1, v21

    #@2c2
    if-eq v0, v1, :cond_11

    #@2c4
    .line 626
    invoke-virtual {v5, v10, v8}, Landroid/view/View;->measure(II)V

    #@2c7
    goto/16 :goto_8

    #@2c9
    .line 607
    .end local v8    # "childHeightSpec":I
    .end local v10    # "childWidthSpec":I
    .end local v14    # "horizontalMargin":I
    .end local v21    # "newWidth":I
    :cond_1a
    move-object/from16 v0, v17

    #@2cb
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@2cd
    move/from16 v29, v0

    #@2cf
    const/16 v30, -0x1

    #@2d1
    move/from16 v0, v29

    #@2d3
    move/from16 v1, v30

    #@2d5
    if-ne v0, v1, :cond_1b

    #@2d7
    .line 608
    const/high16 v29, 0x40000000    # 2.0f

    #@2d9
    move/from16 v0, v18

    #@2db
    move/from16 v1, v29

    #@2dd
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2e0
    move-result v8

    #@2e1
    .restart local v8    # "childHeightSpec":I
    goto :goto_c

    #@2e2
    .line 611
    .end local v8    # "childHeightSpec":I
    :cond_1b
    move-object/from16 v0, v17

    #@2e4
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->height:I

    #@2e6
    move/from16 v29, v0

    #@2e8
    const/high16 v30, 0x40000000    # 2.0f

    #@2ea
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2ed
    move-result v8

    #@2ee
    .restart local v8    # "childHeightSpec":I
    goto :goto_c

    #@2ef
    .line 615
    .end local v8    # "childHeightSpec":I
    :cond_1c
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@2f2
    move-result v29

    #@2f3
    const/high16 v30, 0x40000000    # 2.0f

    #@2f5
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2f8
    move-result v8

    #@2f9
    .restart local v8    # "childHeightSpec":I
    goto :goto_c

    #@2fa
    .line 630
    :cond_1d
    const/16 v29, 0x0

    #@2fc
    move/from16 v0, v29

    #@2fe
    move/from16 v1, v26

    #@300
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@303
    move-result v28

    #@304
    .line 631
    .local v28, "widthToDistribute":I
    move-object/from16 v0, v17

    #@306
    iget v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->weight:F

    #@308
    move/from16 v29, v0

    #@30a
    move/from16 v0, v28

    #@30c
    int-to-float v0, v0

    #@30d
    move/from16 v30, v0

    #@30f
    mul-float v29, v29, v30

    #@311
    div-float v29, v29, v23

    #@313
    move/from16 v0, v29

    #@315
    float-to-int v3, v0

    #@316
    .line 632
    .local v3, "addedWidth":I
    add-int v29, v20, v3

    #@318
    const/high16 v30, 0x40000000    # 2.0f

    #@31a
    invoke-static/range {v29 .. v30}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@31d
    move-result v10

    #@31e
    .line 634
    .restart local v10    # "childWidthSpec":I
    invoke-virtual {v5, v10, v8}, Landroid/view/View;->measure(II)V

    #@321
    goto/16 :goto_8

    #@323
    .line 640
    .end local v3    # "addedWidth":I
    .end local v5    # "child":Landroid/view/View;
    .end local v8    # "childHeightSpec":I
    .end local v10    # "childWidthSpec":I
    .end local v11    # "fixedPanelWidthLimit":I
    .end local v17    # "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    .end local v20    # "measuredWidth":I
    .end local v22    # "skippedFirstPass":Z
    .end local v28    # "widthToDistribute":I
    :cond_1e
    move/from16 v20, v27

    #@325
    .line 641
    .restart local v20    # "measuredWidth":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingTop()I

    #@328
    move-result v29

    #@329
    add-int v29, v29, v16

    #@32b
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingBottom()I

    #@32e
    move-result v30

    #@32f
    add-int v19, v29, v30

    #@331
    .line 643
    .local v19, "measuredHeight":I
    move-object/from16 v0, p0

    #@333
    move/from16 v1, v20

    #@335
    move/from16 v2, v19

    #@337
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/widget/SlidingPaneLayout;->setMeasuredDimension(II)V

    #@33a
    .line 644
    move-object/from16 v0, p0

    #@33c
    iput-boolean v4, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@33e
    .line 646
    move-object/from16 v0, p0

    #@340
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@342
    move-object/from16 v29, v0

    #@344
    invoke-virtual/range {v29 .. v29}, Landroid/support/v4/widget/ViewDragHelper;->getViewDragState()I

    #@347
    move-result v29

    #@348
    if-eqz v29, :cond_1f

    #@34a
    if-nez v4, :cond_1f

    #@34c
    .line 648
    move-object/from16 v0, p0

    #@34e
    iget-object v0, v0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@350
    move-object/from16 v29, v0

    #@352
    invoke-virtual/range {v29 .. v29}, Landroid/support/v4/widget/ViewDragHelper;->abort()V

    #@355
    .line 650
    :cond_1f
    return-void

    #@356
    .line 478
    :sswitch_data_0
    .sparse-switch
        -0x80000000 -> :sswitch_1
        0x40000000 -> :sswitch_0
    .end sparse-switch
.end method

.method protected onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 2
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 1285
    move-object v0, p1

    #@1
    check-cast v0, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;

    #@3
    .line 1286
    .local v0, "ss":Landroid/support/v4/widget/SlidingPaneLayout$SavedState;
    invoke-virtual {v0}, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@6
    move-result-object v1

    #@7
    invoke-super {p0, v1}, Landroid/view/ViewGroup;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@a
    .line 1288
    iget-boolean v1, v0, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;->isOpen:Z

    #@c
    if-eqz v1, :cond_0

    #@e
    .line 1289
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->openPane()Z

    #@11
    .line 1293
    :goto_0
    iget-boolean v1, v0, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;->isOpen:Z

    #@13
    iput-boolean v1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@15
    .line 1294
    return-void

    #@16
    .line 1291
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->closePane()Z

    #@19
    goto :goto_0
.end method

.method protected onSaveInstanceState()Landroid/os/Parcelable;
    .locals 3

    #@0
    .prologue
    .line 1275
    invoke-super {p0}, Landroid/view/ViewGroup;->onSaveInstanceState()Landroid/os/Parcelable;

    #@3
    move-result-object v1

    #@4
    .line 1277
    .local v1, "superState":Landroid/os/Parcelable;
    new-instance v0, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;

    #@6
    invoke-direct {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 1278
    .local v0, "ss":Landroid/support/v4/widget/SlidingPaneLayout$SavedState;
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isSlideable()Z

    #@c
    move-result v2

    #@d
    if-eqz v2, :cond_0

    #@f
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isOpen()Z

    #@12
    move-result v2

    #@13
    :goto_0
    iput-boolean v2, v0, Landroid/support/v4/widget/SlidingPaneLayout$SavedState;->isOpen:Z

    #@15
    .line 1280
    return-object v0

    #@16
    .line 1278
    :cond_0
    iget-boolean v2, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@18
    goto :goto_0
.end method

.method protected onSizeChanged(IIII)V
    .locals 1
    .param p1, "w"    # I
    .param p2, "h"    # I
    .param p3, "oldw"    # I
    .param p4, "oldh"    # I

    #@0
    .prologue
    .line 742
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/ViewGroup;->onSizeChanged(IIII)V

    #@3
    .line 744
    if-eq p1, p3, :cond_0

    #@5
    .line 745
    const/4 v0, 0x1

    #@6
    iput-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mFirstLayout:Z

    #@8
    .line 747
    :cond_0
    return-void
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 11
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 819
    iget-boolean v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@2
    if-nez v7, :cond_1

    #@4
    .line 820
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@7
    move-result v4

    #@8
    .line 855
    :cond_0
    :goto_0
    return v4

    #@9
    .line 823
    :cond_1
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@b
    invoke-virtual {v7, p1}, Landroid/support/v4/widget/ViewDragHelper;->processTouchEvent(Landroid/view/MotionEvent;)V

    #@e
    .line 825
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    #@11
    move-result v0

    #@12
    .line 826
    .local v0, "action":I
    const/4 v4, 0x1

    #@13
    .line 828
    .local v4, "wantTouchEvents":Z
    and-int/lit16 v7, v0, 0xff

    #@15
    packed-switch v7, :pswitch_data_0

    #@18
    goto :goto_0

    #@19
    .line 830
    :pswitch_0
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@1c
    move-result v5

    #@1d
    .line 831
    .local v5, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@20
    move-result v6

    #@21
    .line 832
    .local v6, "y":F
    iput v5, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionX:F

    #@23
    .line 833
    iput v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionY:F

    #@25
    goto :goto_0

    #@26
    .line 838
    .end local v5    # "x":F
    .end local v6    # "y":F
    :pswitch_1
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@28
    invoke-virtual {p0, v7}, Landroid/support/v4/widget/SlidingPaneLayout;->isDimmed(Landroid/view/View;)Z

    #@2b
    move-result v7

    #@2c
    if-eqz v7, :cond_0

    #@2e
    .line 839
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@31
    move-result v5

    #@32
    .line 840
    .restart local v5    # "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@35
    move-result v6

    #@36
    .line 841
    .restart local v6    # "y":F
    iget v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionX:F

    #@38
    sub-float v1, v5, v7

    #@3a
    .line 842
    .local v1, "dx":F
    iget v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mInitialMotionY:F

    #@3c
    sub-float v2, v6, v7

    #@3e
    .line 843
    .local v2, "dy":F
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@40
    invoke-virtual {v7}, Landroid/support/v4/widget/ViewDragHelper;->getTouchSlop()I

    #@43
    move-result v3

    #@44
    .line 844
    .local v3, "slop":I
    mul-float v7, v1, v1

    #@46
    mul-float v8, v2, v2

    #@48
    add-float/2addr v7, v8

    #@49
    mul-int v8, v3, v3

    #@4b
    int-to-float v8, v8

    #@4c
    cmpg-float v7, v7, v8

    #@4e
    if-gez v7, :cond_0

    #@50
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@52
    iget-object v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@54
    float-to-int v9, v5

    #@55
    float-to-int v10, v6

    #@56
    invoke-virtual {v7, v8, v9, v10}, Landroid/support/v4/widget/ViewDragHelper;->isViewUnder(Landroid/view/View;II)Z

    #@59
    move-result v7

    #@5a
    if-eqz v7, :cond_0

    #@5c
    .line 847
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@5e
    const/4 v8, 0x0

    #@5f
    invoke-direct {p0, v7, v8}, Landroid/support/v4/widget/SlidingPaneLayout;->closePane(Landroid/view/View;I)Z

    #@62
    goto :goto_0

    #@63
    .line 828
    nop

    #@64
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method public openPane()Z
    .locals 2

    #@0
    .prologue
    .line 889
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-direct {p0, v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->openPane(Landroid/view/View;I)Z

    #@6
    move-result v0

    #@7
    return v0
.end method

.method public requestChildFocus(Landroid/view/View;Landroid/view/View;)V
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "focused"    # Landroid/view/View;

    #@0
    .prologue
    .line 751
    invoke-super {p0, p1, p2}, Landroid/view/ViewGroup;->requestChildFocus(Landroid/view/View;Landroid/view/View;)V

    #@3
    .line 752
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isInTouchMode()Z

    #@6
    move-result v0

    #@7
    if-nez v0, :cond_0

    #@9
    iget-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@b
    if-nez v0, :cond_0

    #@d
    .line 753
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@f
    if-ne p1, v0, :cond_1

    #@11
    const/4 v0, 0x1

    #@12
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z

    #@14
    .line 755
    :cond_0
    return-void

    #@15
    .line 753
    :cond_1
    const/4 v0, 0x0

    #@16
    goto :goto_0
.end method

.method setAllChildrenVisible()V
    .locals 5

    #@0
    .prologue
    .line 399
    const/4 v2, 0x0

    #@1
    .local v2, "i":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@4
    move-result v1

    #@5
    .local v1, "childCount":I
    :goto_0
    if-ge v2, v1, :cond_1

    #@7
    .line 400
    invoke-virtual {p0, v2}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 401
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@e
    move-result v3

    #@f
    const/4 v4, 0x4

    #@10
    if-ne v3, v4, :cond_0

    #@12
    .line 402
    const/4 v3, 0x0

    #@13
    invoke-virtual {v0, v3}, Landroid/view/View;->setVisibility(I)V

    #@16
    .line 399
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@18
    goto :goto_0

    #@19
    .line 405
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    return-void
.end method

.method public setCoveredFadeColor(I)V
    .locals 0
    .param p1, "color"    # I

    #@0
    .prologue
    .line 318
    iput p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCoveredFadeColor:I

    #@2
    .line 319
    return-void
.end method

.method public setPanelSlideListener(Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@0
    .prologue
    .line 329
    iput-object p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mPanelSlideListener:Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;

    #@2
    .line 330
    return-void
.end method

.method public setParallaxDistance(I)V
    .locals 0
    .param p1, "parallaxBy"    # I

    #@0
    .prologue
    .line 282
    iput p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mParallaxBy:I

    #@2
    .line 283
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->requestLayout()V

    #@5
    .line 284
    return-void
.end method

.method public setShadowDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 1095
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout;->setShadowDrawableLeft(Landroid/graphics/drawable/Drawable;)V

    #@3
    .line 1096
    return-void
.end method

.method public setShadowDrawableLeft(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1105
    iput-object p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mShadowDrawableLeft:Landroid/graphics/drawable/Drawable;

    #@2
    .line 1106
    return-void
.end method

.method public setShadowDrawableRight(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1115
    iput-object p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mShadowDrawableRight:Landroid/graphics/drawable/Drawable;

    #@2
    .line 1116
    return-void
.end method

.method public setShadowResource(I)V
    .locals 1
    .param p1, "resId"    # I
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 1126
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SlidingPaneLayout;->setShadowDrawable(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 1127
    return-void
.end method

.method public setShadowResourceLeft(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1136
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SlidingPaneLayout;->setShadowDrawableLeft(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 1137
    return-void
.end method

.method public setShadowResourceRight(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1146
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/SlidingPaneLayout;->setShadowDrawableRight(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 1147
    return-void
.end method

.method public setSliderFadeColor(I)V
    .locals 0
    .param p1, "color"    # I

    #@0
    .prologue
    .line 301
    iput p1, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSliderFadeColor:I

    #@2
    .line 302
    return-void
.end method

.method public smoothSlideClosed()V
    .locals 0
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 897
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->closePane()Z

    #@3
    .line 898
    return-void
.end method

.method public smoothSlideOpen()V
    .locals 0
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 879
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->openPane()Z

    #@3
    .line 880
    return-void
.end method

.method smoothSlideTo(FI)Z
    .locals 9
    .param p1, "slideOffset"    # F
    .param p2, "velocity"    # I

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 1048
    iget-boolean v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mCanSlide:Z

    #@3
    if-nez v6, :cond_1

    #@5
    .line 1071
    :cond_0
    :goto_0
    return v5

    #@6
    .line 1053
    :cond_1
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@9
    move-result v1

    #@a
    .line 1054
    .local v1, "isLayoutRtl":Z
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@c
    invoke-virtual {v6}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@f
    move-result-object v2

    #@10
    check-cast v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@12
    .line 1057
    .local v2, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    if-eqz v1, :cond_2

    #@14
    .line 1058
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@17
    move-result v6

    #@18
    iget v7, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@1a
    add-int v3, v6, v7

    #@1c
    .line 1059
    .local v3, "startBound":I
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@1e
    invoke-virtual {v6}, Landroid/view/View;->getWidth()I

    #@21
    move-result v0

    #@22
    .line 1060
    .local v0, "childWidth":I
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@25
    move-result v6

    #@26
    int-to-float v6, v6

    #@27
    int-to-float v7, v3

    #@28
    iget v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@2a
    int-to-float v8, v8

    #@2b
    mul-float/2addr v8, p1

    #@2c
    add-float/2addr v7, v8

    #@2d
    int-to-float v8, v0

    #@2e
    add-float/2addr v7, v8

    #@2f
    sub-float/2addr v6, v7

    #@30
    float-to-int v4, v6

    #@31
    .line 1066
    .end local v0    # "childWidth":I
    .local v4, "x":I
    :goto_1
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;

    #@33
    iget-object v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@35
    iget-object v8, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;

    #@37
    invoke-virtual {v8}, Landroid/view/View;->getTop()I

    #@3a
    move-result v8

    #@3b
    invoke-virtual {v6, v7, v4, v8}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@3e
    move-result v6

    #@3f
    if-eqz v6, :cond_0

    #@41
    .line 1067
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->setAllChildrenVisible()V

    #@44
    .line 1068
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@47
    .line 1069
    const/4 v5, 0x1

    #@48
    goto :goto_0

    #@49
    .line 1062
    .end local v3    # "startBound":I
    .end local v4    # "x":I
    :cond_2
    invoke-virtual {p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@4c
    move-result v6

    #@4d
    iget v7, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@4f
    add-int v3, v6, v7

    #@51
    .line 1063
    .restart local v3    # "startBound":I
    int-to-float v6, v3

    #@52
    iget v7, p0, Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I

    #@54
    int-to-float v7, v7

    #@55
    mul-float/2addr v7, p1

    #@56
    add-float/2addr v6, v7

    #@57
    float-to-int v4, v6

    #@58
    .restart local v4    # "x":I
    goto :goto_1
.end method

.method updateObscuredViewsVisibility(Landroid/view/View;)V
    .locals 21
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 353
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z

    #@3
    move-result v12

    #@4
    .line 354
    .local v12, "isLayoutRtl":Z
    if-eqz v12, :cond_1

    #@6
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@9
    move-result v19

    #@a
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@d
    move-result v20

    #@e
    sub-int v15, v19, v20

    #@10
    .line 356
    .local v15, "startBound":I
    :goto_0
    if-eqz v12, :cond_2

    #@12
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@15
    move-result v10

    #@16
    .line 358
    .local v10, "endBound":I
    :goto_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingTop()I

    #@19
    move-result v17

    #@1a
    .line 359
    .local v17, "topBound":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getHeight()I

    #@1d
    move-result v19

    #@1e
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingBottom()I

    #@21
    move-result v20

    #@22
    sub-int v3, v19, v20

    #@24
    .line 364
    .local v3, "bottomBound":I
    if-eqz p1, :cond_3

    #@26
    invoke-static/range {p1 .. p1}, Landroid/support/v4/widget/SlidingPaneLayout;->viewIsOpaque(Landroid/view/View;)Z

    #@29
    move-result v19

    #@2a
    if-eqz v19, :cond_3

    #@2c
    .line 365
    invoke-virtual/range {p1 .. p1}, Landroid/view/View;->getLeft()I

    #@2f
    move-result v13

    #@30
    .line 366
    .local v13, "left":I
    invoke-virtual/range {p1 .. p1}, Landroid/view/View;->getRight()I

    #@33
    move-result v14

    #@34
    .line 367
    .local v14, "right":I
    invoke-virtual/range {p1 .. p1}, Landroid/view/View;->getTop()I

    #@37
    move-result v16

    #@38
    .line 368
    .local v16, "top":I
    invoke-virtual/range {p1 .. p1}, Landroid/view/View;->getBottom()I

    #@3b
    move-result v2

    #@3c
    .line 373
    .local v2, "bottom":I
    :goto_2
    const/4 v11, 0x0

    #@3d
    .local v11, "i":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildCount()I

    #@40
    move-result v5

    #@41
    .local v5, "childCount":I
    :goto_3
    if-ge v11, v5, :cond_0

    #@43
    .line 374
    move-object/from16 v0, p0

    #@45
    invoke-virtual {v0, v11}, Landroid/support/v4/widget/SlidingPaneLayout;->getChildAt(I)Landroid/view/View;

    #@48
    move-result-object v4

    #@49
    .line 376
    .local v4, "child":Landroid/view/View;
    move-object/from16 v0, p1

    #@4b
    if-ne v4, v0, :cond_4

    #@4d
    .line 396
    .end local v4    # "child":Landroid/view/View;
    :cond_0
    return-void

    #@4e
    .line 354
    .end local v2    # "bottom":I
    .end local v3    # "bottomBound":I
    .end local v5    # "childCount":I
    .end local v10    # "endBound":I
    .end local v11    # "i":I
    .end local v13    # "left":I
    .end local v14    # "right":I
    .end local v15    # "startBound":I
    .end local v16    # "top":I
    .end local v17    # "topBound":I
    :cond_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@51
    move-result v15

    #@52
    goto :goto_0

    #@53
    .line 356
    .restart local v15    # "startBound":I
    :cond_2
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@56
    move-result v19

    #@57
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@5a
    move-result v20

    #@5b
    sub-int v10, v19, v20

    #@5d
    goto :goto_1

    #@5e
    .line 370
    .restart local v3    # "bottomBound":I
    .restart local v10    # "endBound":I
    .restart local v17    # "topBound":I
    :cond_3
    const/4 v2, 0x0

    #@5f
    .restart local v2    # "bottom":I
    move/from16 v16, v2

    #@61
    .restart local v16    # "top":I
    move v14, v2

    #@62
    .restart local v14    # "right":I
    move v13, v2

    #@63
    .restart local v13    # "left":I
    goto :goto_2

    #@64
    .line 381
    .restart local v4    # "child":Landroid/view/View;
    .restart local v5    # "childCount":I
    .restart local v11    # "i":I
    :cond_4
    if-eqz v12, :cond_5

    #@66
    move/from16 v19, v10

    #@68
    :goto_4
    invoke-virtual {v4}, Landroid/view/View;->getLeft()I

    #@6b
    move-result v20

    #@6c
    invoke-static/range {v19 .. v20}, Ljava/lang/Math;->max(II)I

    #@6f
    move-result v7

    #@70
    .line 383
    .local v7, "clampedChildLeft":I
    invoke-virtual {v4}, Landroid/view/View;->getTop()I

    #@73
    move-result v19

    #@74
    move/from16 v0, v17

    #@76
    move/from16 v1, v19

    #@78
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@7b
    move-result v9

    #@7c
    .line 384
    .local v9, "clampedChildTop":I
    if-eqz v12, :cond_6

    #@7e
    move/from16 v19, v15

    #@80
    :goto_5
    invoke-virtual {v4}, Landroid/view/View;->getRight()I

    #@83
    move-result v20

    #@84
    invoke-static/range {v19 .. v20}, Ljava/lang/Math;->min(II)I

    #@87
    move-result v8

    #@88
    .line 386
    .local v8, "clampedChildRight":I
    invoke-virtual {v4}, Landroid/view/View;->getBottom()I

    #@8b
    move-result v19

    #@8c
    move/from16 v0, v19

    #@8e
    invoke-static {v3, v0}, Ljava/lang/Math;->min(II)I

    #@91
    move-result v6

    #@92
    .line 388
    .local v6, "clampedChildBottom":I
    if-lt v7, v13, :cond_7

    #@94
    move/from16 v0, v16

    #@96
    if-lt v9, v0, :cond_7

    #@98
    if-gt v8, v14, :cond_7

    #@9a
    if-gt v6, v2, :cond_7

    #@9c
    .line 390
    const/16 v18, 0x4

    #@9e
    .line 394
    .local v18, "vis":I
    :goto_6
    move/from16 v0, v18

    #@a0
    invoke-virtual {v4, v0}, Landroid/view/View;->setVisibility(I)V

    #@a3
    .line 373
    add-int/lit8 v11, v11, 0x1

    #@a5
    goto :goto_3

    #@a6
    .end local v6    # "clampedChildBottom":I
    .end local v7    # "clampedChildLeft":I
    .end local v8    # "clampedChildRight":I
    .end local v9    # "clampedChildTop":I
    .end local v18    # "vis":I
    :cond_5
    move/from16 v19, v15

    #@a8
    .line 381
    goto :goto_4

    #@a9
    .restart local v7    # "clampedChildLeft":I
    .restart local v9    # "clampedChildTop":I
    :cond_6
    move/from16 v19, v10

    #@ab
    .line 384
    goto :goto_5

    #@ac
    .line 392
    .restart local v6    # "clampedChildBottom":I
    .restart local v8    # "clampedChildRight":I
    :cond_7
    const/16 v18, 0x0

    #@ae
    .restart local v18    # "vis":I
    goto :goto_6
.end method
