.class public Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
.super Landroid/view/ViewGroup;
.source "ActionBarOverlayLayout.java"

# interfaces
.implements Landroid/support/v7/internal/widget/DecorContentParent;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;,
        Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    }
.end annotation


# static fields
.field static final ATTRS:[I

.field private static final TAG:Ljava/lang/String; = "ActionBarOverlayLayout"


# instance fields
.field private final ACTION_BAR_ANIMATE_DELAY:I

.field private mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

.field private mActionBarHeight:I

.field private mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

.field private mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

.field private final mAddActionBarHideOffset:Ljava/lang/Runnable;

.field private mAnimatingForFling:Z

.field private final mBaseContentInsets:Landroid/graphics/Rect;

.field private final mBaseInnerInsets:Landroid/graphics/Rect;

.field private final mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

.field private mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

.field private final mContentInsets:Landroid/graphics/Rect;

.field private mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

.field private mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

.field private mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

.field private mFlingEstimator:Landroid/support/v4/widget/ScrollerCompat;

.field private mHasNonEmbeddedTabs:Z

.field private mHideOnContentScroll:Z

.field private mHideOnContentScrollReference:I

.field private mIgnoreWindowContentOverlay:Z

.field private final mInnerInsets:Landroid/graphics/Rect;

.field private final mLastBaseContentInsets:Landroid/graphics/Rect;

.field private final mLastInnerInsets:Landroid/graphics/Rect;

.field private mLastSystemUiVisibility:I

.field private mOverlayMode:Z

.field private final mRemoveActionBarHideOffset:Ljava/lang/Runnable;

.field private final mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

.field private mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

.field private mWindowVisibility:I


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 161
    const/4 v0, 0x2

    #@1
    new-array v0, v0, [I

    #@3
    const/4 v1, 0x0

    #@4
    sget v2, Landroid/support/v7/appcompat/R$attr;->actionBarSize:I

    #@6
    aput v2, v0, v1

    #@8
    const/4 v1, 0x1

    #@9
    const v2, 0x1010059

    #@c
    aput v2, v0, v1

    #@e
    sput-object v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->ATTRS:[I

    #@10
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 167
    invoke-direct {p0, p1}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;)V

    #@3
    .line 55
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowVisibility:I

    #@6
    .line 75
    new-instance v0, Landroid/graphics/Rect;

    #@8
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@d
    .line 76
    new-instance v0, Landroid/graphics/Rect;

    #@f
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@12
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastBaseContentInsets:Landroid/graphics/Rect;

    #@14
    .line 77
    new-instance v0, Landroid/graphics/Rect;

    #@16
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@19
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@1b
    .line 78
    new-instance v0, Landroid/graphics/Rect;

    #@1d
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@20
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseInnerInsets:Landroid/graphics/Rect;

    #@22
    .line 79
    new-instance v0, Landroid/graphics/Rect;

    #@24
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@27
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@29
    .line 80
    new-instance v0, Landroid/graphics/Rect;

    #@2b
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@2e
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastInnerInsets:Landroid/graphics/Rect;

    #@30
    .line 84
    const/16 v0, 0x258

    #@32
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->ACTION_BAR_ANIMATE_DELAY:I

    #@34
    .line 91
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;

    #@36
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@39
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@3b
    .line 106
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$2;

    #@3d
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$2;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@40
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@42
    .line 121
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;

    #@44
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@47
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mRemoveActionBarHideOffset:Ljava/lang/Runnable;

    #@49
    .line 133
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;

    #@4b
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@4e
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAddActionBarHideOffset:Ljava/lang/Runnable;

    #@50
    .line 168
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->init(Landroid/content/Context;)V

    #@53
    .line 169
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 172
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@3
    .line 55
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowVisibility:I

    #@6
    .line 75
    new-instance v0, Landroid/graphics/Rect;

    #@8
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@d
    .line 76
    new-instance v0, Landroid/graphics/Rect;

    #@f
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@12
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastBaseContentInsets:Landroid/graphics/Rect;

    #@14
    .line 77
    new-instance v0, Landroid/graphics/Rect;

    #@16
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@19
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@1b
    .line 78
    new-instance v0, Landroid/graphics/Rect;

    #@1d
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@20
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseInnerInsets:Landroid/graphics/Rect;

    #@22
    .line 79
    new-instance v0, Landroid/graphics/Rect;

    #@24
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@27
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@29
    .line 80
    new-instance v0, Landroid/graphics/Rect;

    #@2b
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@2e
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastInnerInsets:Landroid/graphics/Rect;

    #@30
    .line 84
    const/16 v0, 0x258

    #@32
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->ACTION_BAR_ANIMATE_DELAY:I

    #@34
    .line 91
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;

    #@36
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@39
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@3b
    .line 106
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$2;

    #@3d
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$2;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@40
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@42
    .line 121
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;

    #@44
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@47
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mRemoveActionBarHideOffset:Ljava/lang/Runnable;

    #@49
    .line 133
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;

    #@4b
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;-><init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@4e
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAddActionBarHideOffset:Ljava/lang/Runnable;

    #@50
    .line 173
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->init(Landroid/content/Context;)V

    #@53
    .line 174
    return-void
.end method

.method static synthetic access$002(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    .param p1, "x1"    # Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@0
    .prologue
    .line 50
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2
    return-object p1
.end method

.method static synthetic access$102(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 50
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAnimatingForFling:Z

    #@2
    return p1
.end method

.method static synthetic access$202(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    .param p1, "x1"    # Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@0
    .prologue
    .line 50
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2
    return-object p1
.end method

.method static synthetic access$300(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@0
    .prologue
    .line 50
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    return-void
.end method

.method static synthetic access$400(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@0
    .prologue
    .line 50
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@0
    .prologue
    .line 50
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    return-object v0
.end method

.method static synthetic access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@0
    .prologue
    .line 50
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    return-object v0
.end method

.method static synthetic access$700(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@0
    .prologue
    .line 50
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    return-object v0
.end method

.method private addActionBarHideOffset()V
    .locals 1

    #@0
    .prologue
    .line 660
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    .line 661
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAddActionBarHideOffset:Ljava/lang/Runnable;

    #@5
    invoke-interface {v0}, Ljava/lang/Runnable;->run()V

    #@8
    .line 662
    return-void
.end method

.method private applyInsets(Landroid/view/View;Landroid/graphics/Rect;ZZZZ)Z
    .locals 4
    .param p1, "view"    # Landroid/view/View;
    .param p2, "insets"    # Landroid/graphics/Rect;
    .param p3, "left"    # Z
    .param p4, "top"    # Z
    .param p5, "bottom"    # Z
    .param p6, "right"    # Z

    #@0
    .prologue
    .line 293
    const/4 v0, 0x0

    #@1
    .line 294
    .local v0, "changed":Z
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v1

    #@5
    check-cast v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@7
    .line 295
    .local v1, "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    if-eqz p3, :cond_0

    #@9
    iget v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@b
    iget v3, p2, Landroid/graphics/Rect;->left:I

    #@d
    if-eq v2, v3, :cond_0

    #@f
    .line 296
    const/4 v0, 0x1

    #@10
    .line 297
    iget v2, p2, Landroid/graphics/Rect;->left:I

    #@12
    iput v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@14
    .line 299
    :cond_0
    if-eqz p4, :cond_1

    #@16
    iget v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@18
    iget v3, p2, Landroid/graphics/Rect;->top:I

    #@1a
    if-eq v2, v3, :cond_1

    #@1c
    .line 300
    const/4 v0, 0x1

    #@1d
    .line 301
    iget v2, p2, Landroid/graphics/Rect;->top:I

    #@1f
    iput v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@21
    .line 303
    :cond_1
    if-eqz p6, :cond_2

    #@23
    iget v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->rightMargin:I

    #@25
    iget v3, p2, Landroid/graphics/Rect;->right:I

    #@27
    if-eq v2, v3, :cond_2

    #@29
    .line 304
    const/4 v0, 0x1

    #@2a
    .line 305
    iget v2, p2, Landroid/graphics/Rect;->right:I

    #@2c
    iput v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->rightMargin:I

    #@2e
    .line 307
    :cond_2
    if-eqz p5, :cond_3

    #@30
    iget v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@32
    iget v3, p2, Landroid/graphics/Rect;->bottom:I

    #@34
    if-eq v2, v3, :cond_3

    #@36
    .line 308
    const/4 v0, 0x1

    #@37
    .line 309
    iget v2, p2, Landroid/graphics/Rect;->bottom:I

    #@39
    iput v2, v1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@3b
    .line 311
    :cond_3
    return v0
.end method

.method private getDecorToolbar(Landroid/view/View;)Landroid/support/v7/internal/widget/DecorToolbar;
    .locals 3
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 589
    instance-of v0, p1, Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 590
    check-cast p1, Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    .line 592
    .end local p1    # "view":Landroid/view/View;
    :goto_0
    return-object p1

    #@7
    .line 591
    .restart local p1    # "view":Landroid/view/View;
    :cond_0
    instance-of v0, p1, Landroid/support/v7/widget/Toolbar;

    #@9
    if-eqz v0, :cond_1

    #@b
    .line 592
    check-cast p1, Landroid/support/v7/widget/Toolbar;

    #@d
    .end local p1    # "view":Landroid/view/View;
    invoke-virtual {p1}, Landroid/support/v7/widget/Toolbar;->getWrapper()Landroid/support/v7/internal/widget/DecorToolbar;

    #@10
    move-result-object p1

    #@11
    goto :goto_0

    #@12
    .line 594
    .restart local p1    # "view":Landroid/view/View;
    :cond_1
    new-instance v0, Ljava/lang/IllegalStateException;

    #@14
    new-instance v1, Ljava/lang/StringBuilder;

    #@16
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@19
    const-string v2, "Can\'t make a decor toolbar out of "

    #@1b
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1e
    move-result-object v1

    #@1f
    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@22
    move-result-object v2

    #@23
    invoke-virtual {v2}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@26
    move-result-object v2

    #@27
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2a
    move-result-object v1

    #@2b
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2e
    move-result-object v1

    #@2f
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@32
    throw v0
.end method

.method private haltActionBarHideOffsetAnimations()V
    .locals 1

    #@0
    .prologue
    .line 634
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mRemoveActionBarHideOffset:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@5
    .line 635
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAddActionBarHideOffset:Ljava/lang/Runnable;

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@a
    .line 636
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@c
    if-eqz v0, :cond_0

    #@e
    .line 637
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@10
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->cancel()V

    #@13
    .line 639
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@15
    if-eqz v0, :cond_1

    #@17
    .line 640
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@19
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->cancel()V

    #@1c
    .line 642
    :cond_1
    return-void
.end method

.method private init(Landroid/content/Context;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 177
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {v1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@9
    move-result-object v1

    #@a
    sget-object v4, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->ATTRS:[I

    #@c
    invoke-virtual {v1, v4}, Landroid/content/res/Resources$Theme;->obtainStyledAttributes([I)Landroid/content/res/TypedArray;

    #@f
    move-result-object v0

    #@10
    .line 178
    .local v0, "ta":Landroid/content/res/TypedArray;
    invoke-virtual {v0, v3, v3}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@13
    move-result v1

    #@14
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarHeight:I

    #@16
    .line 179
    invoke-virtual {v0, v2}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@19
    move-result-object v1

    #@1a
    iput-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@1c
    .line 180
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@1e
    if-nez v1, :cond_0

    #@20
    move v1, v2

    #@21
    :goto_0
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setWillNotDraw(Z)V

    #@24
    .line 181
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@27
    .line 183
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@2a
    move-result-object v1

    #@2b
    iget v1, v1, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@2d
    const/16 v4, 0x13

    #@2f
    if-ge v1, v4, :cond_1

    #@31
    :goto_1
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mIgnoreWindowContentOverlay:Z

    #@33
    .line 186
    invoke-static {p1}, Landroid/support/v4/widget/ScrollerCompat;->create(Landroid/content/Context;)Landroid/support/v4/widget/ScrollerCompat;

    #@36
    move-result-object v1

    #@37
    iput-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mFlingEstimator:Landroid/support/v4/widget/ScrollerCompat;

    #@39
    .line 187
    return-void

    #@3a
    :cond_0
    move v1, v3

    #@3b
    .line 180
    goto :goto_0

    #@3c
    :cond_1
    move v2, v3

    #@3d
    .line 183
    goto :goto_1
.end method

.method private postAddActionBarHideOffset()V
    .locals 4

    #@0
    .prologue
    .line 650
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    .line 651
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAddActionBarHideOffset:Ljava/lang/Runnable;

    #@5
    const-wide/16 v2, 0x258

    #@7
    invoke-virtual {p0, v0, v2, v3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->postDelayed(Ljava/lang/Runnable;J)Z

    #@a
    .line 652
    return-void
.end method

.method private postRemoveActionBarHideOffset()V
    .locals 4

    #@0
    .prologue
    .line 645
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    .line 646
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mRemoveActionBarHideOffset:Ljava/lang/Runnable;

    #@5
    const-wide/16 v2, 0x258

    #@7
    invoke-virtual {p0, v0, v2, v3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->postDelayed(Ljava/lang/Runnable;J)Z

    #@a
    .line 647
    return-void
.end method

.method private removeActionBarHideOffset()V
    .locals 1

    #@0
    .prologue
    .line 655
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    .line 656
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mRemoveActionBarHideOffset:Ljava/lang/Runnable;

    #@5
    invoke-interface {v0}, Ljava/lang/Runnable;->run()V

    #@8
    .line 657
    return-void
.end method

.method private shouldHideActionBarOnFling(FF)Z
    .locals 10
    .param p1, "velocityX"    # F
    .param p2, "velocityY"    # F

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 665
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mFlingEstimator:Landroid/support/v4/widget/ScrollerCompat;

    #@3
    float-to-int v4, p2

    #@4
    const/high16 v7, -0x80000000

    #@6
    const v8, 0x7fffffff

    #@9
    move v2, v1

    #@a
    move v3, v1

    #@b
    move v5, v1

    #@c
    move v6, v1

    #@d
    invoke-virtual/range {v0 .. v8}, Landroid/support/v4/widget/ScrollerCompat;->fling(IIIIIIII)V

    #@10
    .line 666
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mFlingEstimator:Landroid/support/v4/widget/ScrollerCompat;

    #@12
    invoke-virtual {v0}, Landroid/support/v4/widget/ScrollerCompat;->getFinalY()I

    #@15
    move-result v9

    #@16
    .line 667
    .local v9, "finalY":I
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@18
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@1b
    move-result v0

    #@1c
    if-le v9, v0, :cond_0

    #@1e
    const/4 v1, 0x1

    #@1f
    :cond_0
    return v1
.end method


# virtual methods
.method public canShowOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 741
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 742
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->canShowOverflowMenu()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 363
    instance-of v0, p1, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@2
    return v0
.end method

.method public dismissPopups()V
    .locals 1

    #@0
    .prologue
    .line 795
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 796
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->dismissPopupMenus()V

    #@8
    .line 797
    return-void
.end method

.method public draw(Landroid/graphics/Canvas;)V
    .locals 5
    .param p1, "c"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 509
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->draw(Landroid/graphics/Canvas;)V

    #@4
    .line 510
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@6
    if-eqz v2, :cond_0

    #@8
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mIgnoreWindowContentOverlay:Z

    #@a
    if-nez v2, :cond_0

    #@c
    .line 511
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@e
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@11
    move-result v2

    #@12
    if-nez v2, :cond_1

    #@14
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@16
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->getBottom()I

    #@19
    move-result v2

    #@1a
    int-to-float v2, v2

    #@1b
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1d
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getTranslationY(Landroid/view/View;)F

    #@20
    move-result v3

    #@21
    add-float/2addr v2, v3

    #@22
    const/high16 v3, 0x3f000000    # 0.5f

    #@24
    add-float/2addr v2, v3

    #@25
    float-to-int v0, v2

    #@26
    .line 514
    .local v0, "top":I
    :goto_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@28
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getWidth()I

    #@2b
    move-result v3

    #@2c
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@2e
    invoke-virtual {v4}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@31
    move-result v4

    #@32
    add-int/2addr v4, v0

    #@33
    invoke-virtual {v2, v1, v0, v3, v4}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@36
    .line 516
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowContentOverlay:Landroid/graphics/drawable/Drawable;

    #@38
    invoke-virtual {v1, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@3b
    .line 518
    .end local v0    # "top":I
    :cond_0
    return-void

    #@3c
    :cond_1
    move v0, v1

    #@3d
    .line 511
    goto :goto_0
.end method

.method protected fitSystemWindows(Landroid/graphics/Rect;)Z
    .locals 10
    .param p1, "insets"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 316
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 318
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getWindowSystemUiVisibility(Landroid/view/View;)I

    #@6
    move-result v9

    #@7
    .line 319
    .local v9, "vis":I
    and-int/lit16 v0, v9, 0x100

    #@9
    if-eqz v0, :cond_3

    #@b
    const/4 v8, 0x1

    #@c
    .line 320
    .local v8, "stable":Z
    :goto_0
    move-object v2, p1

    #@d
    .line 323
    .local v2, "systemInsets":Landroid/graphics/Rect;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@f
    const/4 v3, 0x1

    #@10
    const/4 v4, 0x1

    #@11
    const/4 v5, 0x0

    #@12
    const/4 v6, 0x1

    #@13
    move-object v0, p0

    #@14
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->applyInsets(Landroid/view/View;Landroid/graphics/Rect;ZZZZ)Z

    #@17
    move-result v7

    #@18
    .line 324
    .local v7, "changed":Z
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1a
    if-eqz v0, :cond_0

    #@1c
    .line 325
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1e
    const/4 v3, 0x1

    #@1f
    const/4 v4, 0x0

    #@20
    const/4 v5, 0x1

    #@21
    const/4 v6, 0x1

    #@22
    move-object v0, p0

    #@23
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->applyInsets(Landroid/view/View;Landroid/graphics/Rect;ZZZZ)Z

    #@26
    move-result v0

    #@27
    or-int/2addr v7, v0

    #@28
    .line 328
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseInnerInsets:Landroid/graphics/Rect;

    #@2a
    invoke-virtual {v0, v2}, Landroid/graphics/Rect;->set(Landroid/graphics/Rect;)V

    #@2d
    .line 329
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseInnerInsets:Landroid/graphics/Rect;

    #@2f
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@31
    invoke-static {p0, v0, v1}, Landroid/support/v7/internal/widget/ViewUtils;->computeFitSystemWindows(Landroid/view/View;Landroid/graphics/Rect;Landroid/graphics/Rect;)V

    #@34
    .line 330
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastBaseContentInsets:Landroid/graphics/Rect;

    #@36
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@38
    invoke-virtual {v0, v1}, Landroid/graphics/Rect;->equals(Ljava/lang/Object;)Z

    #@3b
    move-result v0

    #@3c
    if-nez v0, :cond_1

    #@3e
    .line 331
    const/4 v7, 0x1

    #@3f
    .line 332
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastBaseContentInsets:Landroid/graphics/Rect;

    #@41
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@43
    invoke-virtual {v0, v1}, Landroid/graphics/Rect;->set(Landroid/graphics/Rect;)V

    #@46
    .line 335
    :cond_1
    if-eqz v7, :cond_2

    #@48
    .line 336
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->requestLayout()V

    #@4b
    .line 343
    :cond_2
    const/4 v0, 0x1

    #@4c
    return v0

    #@4d
    .line 319
    .end local v2    # "systemInsets":Landroid/graphics/Rect;
    .end local v7    # "changed":Z
    .end local v8    # "stable":Z
    :cond_3
    const/4 v8, 0x0

    #@4e
    goto :goto_0
.end method

.method protected generateDefaultLayoutParams()Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    .locals 2

    #@0
    .prologue
    const/4 v1, -0x1

    #@1
    .line 348
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@3
    invoke-direct {v0, v1, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;-><init>(II)V

    #@6
    return-object v0
.end method

.method protected bridge synthetic generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 50
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->generateDefaultLayoutParams()Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 353
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method public bridge synthetic generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 50
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 358
    new-instance v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@2
    invoke-direct {v0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@5
    return-object v0
.end method

.method public getActionBarHideOffset()I
    .locals 1

    #@0
    .prologue
    .line 617
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->getTranslationY(Landroid/view/View;)F

    #@9
    move-result v0

    #@a
    float-to-int v0, v0

    #@b
    neg-int v0, v0

    #@c
    :goto_0
    return v0

    #@d
    :cond_0
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 684
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 685
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getTitle()Ljava/lang/CharSequence;

    #@8
    move-result-object v0

    #@9
    return-object v0
.end method

.method public hasIcon()Z
    .locals 1

    #@0
    .prologue
    .line 711
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 712
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hasIcon()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method public hasLogo()Z
    .locals 1

    #@0
    .prologue
    .line 717
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 718
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hasLogo()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 765
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 766
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hideOverflowMenu()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method public initFeature(I)V
    .locals 1
    .param p1, "windowFeature"    # I

    #@0
    .prologue
    .line 690
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 691
    sparse-switch p1, :sswitch_data_0

    #@6
    .line 702
    :goto_0
    return-void

    #@7
    .line 693
    :sswitch_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@9
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->initProgress()V

    #@c
    goto :goto_0

    #@d
    .line 696
    :sswitch_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@f
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->initIndeterminateProgress()V

    #@12
    goto :goto_0

    #@13
    .line 699
    :sswitch_2
    const/4 v0, 0x1

    #@14
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setOverlayMode(Z)V

    #@17
    goto :goto_0

    #@18
    .line 691
    :sswitch_data_0
    .sparse-switch
        0x2 -> :sswitch_0
        0x5 -> :sswitch_1
        0x9 -> :sswitch_2
    .end sparse-switch
.end method

.method public isHideOnContentScrollEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 613
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@2
    return v0
.end method

.method public isInOverlayMode()Z
    .locals 1

    #@0
    .prologue
    .line 222
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mOverlayMode:Z

    #@2
    return v0
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 753
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 754
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->isOverflowMenuShowPending()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 747
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 748
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->isOverflowMenuShowing()Z

    #@8
    move-result v0

    #@9
    return v0
.end method

.method protected onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 2
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 251
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x8

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 252
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@9
    .line 254
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getContext()Landroid/content/Context;

    #@c
    move-result-object v0

    #@d
    invoke-direct {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->init(Landroid/content/Context;)V

    #@10
    .line 255
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@13
    .line 256
    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 0

    #@0
    .prologue
    .line 191
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@3
    .line 192
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@6
    .line 193
    return-void
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
    .line 478
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getChildCount()I

    #@3
    move-result v3

    #@4
    .line 480
    .local v3, "count":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingLeft()I

    #@7
    move-result v8

    #@8
    .line 481
    .local v8, "parentLeft":I
    sub-int v12, p4, p2

    #@a
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingRight()I

    #@d
    move-result v13

    #@e
    sub-int v9, v12, v13

    #@10
    .line 483
    .local v9, "parentRight":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingTop()I

    #@13
    move-result v10

    #@14
    .line 484
    .local v10, "parentTop":I
    sub-int v12, p5, p3

    #@16
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingBottom()I

    #@19
    move-result v13

    #@1a
    sub-int v7, v12, v13

    #@1c
    .line 486
    .local v7, "parentBottom":I
    const/4 v5, 0x0

    #@1d
    .local v5, "i":I
    :goto_0
    if-ge v5, v3, :cond_2

    #@1f
    .line 487
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getChildAt(I)Landroid/view/View;

    #@22
    move-result-object v0

    #@23
    .line 488
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@26
    move-result v12

    #@27
    const/16 v13, 0x8

    #@29
    if-eq v12, v13, :cond_0

    #@2b
    .line 489
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2e
    move-result-object v6

    #@2f
    check-cast v6, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@31
    .line 491
    .local v6, "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    invoke-virtual {v0}, Landroid/view/View;->getMeasuredWidth()I

    #@34
    move-result v11

    #@35
    .line 492
    .local v11, "width":I
    invoke-virtual {v0}, Landroid/view/View;->getMeasuredHeight()I

    #@38
    move-result v4

    #@39
    .line 494
    .local v4, "height":I
    iget v12, v6, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@3b
    add-int v1, v8, v12

    #@3d
    .line 496
    .local v1, "childLeft":I
    iget-object v12, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3f
    if-ne v0, v12, :cond_1

    #@41
    .line 497
    sub-int v12, v7, v4

    #@43
    iget v13, v6, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@45
    sub-int v2, v12, v13

    #@47
    .line 502
    .local v2, "childTop":I
    :goto_1
    add-int v12, v1, v11

    #@49
    add-int v13, v2, v4

    #@4b
    invoke-virtual {v0, v1, v2, v12, v13}, Landroid/view/View;->layout(IIII)V

    #@4e
    .line 486
    .end local v1    # "childLeft":I
    .end local v2    # "childTop":I
    .end local v4    # "height":I
    .end local v6    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    .end local v11    # "width":I
    :cond_0
    add-int/lit8 v5, v5, 0x1

    #@50
    goto :goto_0

    #@51
    .line 499
    .restart local v1    # "childLeft":I
    .restart local v4    # "height":I
    .restart local v6    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    .restart local v11    # "width":I
    :cond_1
    iget v12, v6, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@53
    add-int v2, v10, v12

    #@55
    .restart local v2    # "childTop":I
    goto :goto_1

    #@56
    .line 505
    .end local v0    # "child":Landroid/view/View;
    .end local v1    # "childLeft":I
    .end local v2    # "childTop":I
    .end local v4    # "height":I
    .end local v6    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    .end local v11    # "width":I
    :cond_2
    return-void
.end method

.method protected onMeasure(II)V
    .locals 17
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 368
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 370
    const/4 v11, 0x0

    #@4
    .line 371
    .local v11, "maxHeight":I
    const/4 v12, 0x0

    #@5
    .line 372
    .local v12, "maxWidth":I
    const/4 v9, 0x0

    #@6
    .line 374
    .local v9, "childState":I
    const/4 v15, 0x0

    #@7
    .line 375
    .local v15, "topInset":I
    const/4 v8, 0x0

    #@8
    .line 377
    .local v8, "bottomInset":I
    move-object/from16 v0, p0

    #@a
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@c
    const/4 v4, 0x0

    #@d
    const/4 v6, 0x0

    #@e
    move-object/from16 v1, p0

    #@10
    move/from16 v3, p1

    #@12
    move/from16 v5, p2

    #@14
    invoke-virtual/range {v1 .. v6}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@17
    .line 378
    move-object/from16 v0, p0

    #@19
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1b
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1e
    move-result-object v10

    #@1f
    check-cast v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@21
    .line 379
    .local v10, "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    move-object/from16 v0, p0

    #@23
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@25
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredWidth()I

    #@28
    move-result v1

    #@29
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@2b
    add-int/2addr v1, v2

    #@2c
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->rightMargin:I

    #@2e
    add-int/2addr v1, v2

    #@2f
    invoke-static {v12, v1}, Ljava/lang/Math;->max(II)I

    #@32
    move-result v12

    #@33
    .line 381
    move-object/from16 v0, p0

    #@35
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@37
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@3a
    move-result v1

    #@3b
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@3d
    add-int/2addr v1, v2

    #@3e
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@40
    add-int/2addr v1, v2

    #@41
    invoke-static {v11, v1}, Ljava/lang/Math;->max(II)I

    #@44
    move-result v11

    #@45
    .line 383
    move-object/from16 v0, p0

    #@47
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@49
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@4c
    move-result v1

    #@4d
    invoke-static {v9, v1}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@50
    move-result v9

    #@51
    .line 387
    move-object/from16 v0, p0

    #@53
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@55
    if-eqz v1, :cond_0

    #@57
    .line 388
    move-object/from16 v0, p0

    #@59
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@5b
    const/4 v4, 0x0

    #@5c
    const/4 v6, 0x0

    #@5d
    move-object/from16 v1, p0

    #@5f
    move/from16 v3, p1

    #@61
    move/from16 v5, p2

    #@63
    invoke-virtual/range {v1 .. v6}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@66
    .line 389
    move-object/from16 v0, p0

    #@68
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6a
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@6d
    move-result-object v10

    #@6e
    .end local v10    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    check-cast v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@70
    .line 390
    .restart local v10    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    move-object/from16 v0, p0

    #@72
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@74
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredWidth()I

    #@77
    move-result v1

    #@78
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@7a
    add-int/2addr v1, v2

    #@7b
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->rightMargin:I

    #@7d
    add-int/2addr v1, v2

    #@7e
    invoke-static {v12, v1}, Ljava/lang/Math;->max(II)I

    #@81
    move-result v12

    #@82
    .line 392
    move-object/from16 v0, p0

    #@84
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@86
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@89
    move-result v1

    #@8a
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@8c
    add-int/2addr v1, v2

    #@8d
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@8f
    add-int/2addr v1, v2

    #@90
    invoke-static {v11, v1}, Ljava/lang/Math;->max(II)I

    #@93
    move-result v11

    #@94
    .line 394
    move-object/from16 v0, p0

    #@96
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@98
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@9b
    move-result v1

    #@9c
    invoke-static {v9, v1}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@9f
    move-result v9

    #@a0
    .line 398
    :cond_0
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getWindowSystemUiVisibility(Landroid/view/View;)I

    #@a3
    move-result v16

    #@a4
    .line 399
    .local v16, "vis":I
    move/from16 v0, v16

    #@a6
    and-int/lit16 v1, v0, 0x100

    #@a8
    if-eqz v1, :cond_4

    #@aa
    const/4 v13, 0x1

    #@ab
    .line 401
    .local v13, "stable":Z
    :goto_0
    if-eqz v13, :cond_5

    #@ad
    .line 404
    move-object/from16 v0, p0

    #@af
    iget v15, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarHeight:I

    #@b1
    .line 405
    move-object/from16 v0, p0

    #@b3
    iget-boolean v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHasNonEmbeddedTabs:Z

    #@b5
    if-eqz v1, :cond_1

    #@b7
    .line 406
    move-object/from16 v0, p0

    #@b9
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@bb
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getTabContainer()Landroid/view/View;

    #@be
    move-result-object v14

    #@bf
    .line 407
    .local v14, "tabs":Landroid/view/View;
    if-eqz v14, :cond_1

    #@c1
    .line 409
    move-object/from16 v0, p0

    #@c3
    iget v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarHeight:I

    #@c5
    add-int/2addr v15, v1

    #@c6
    .line 418
    .end local v14    # "tabs":Landroid/view/View;
    :cond_1
    :goto_1
    move-object/from16 v0, p0

    #@c8
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@ca
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->isSplit()Z

    #@cd
    move-result v1

    #@ce
    if-eqz v1, :cond_2

    #@d0
    .line 420
    move-object/from16 v0, p0

    #@d2
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@d4
    if-eqz v1, :cond_2

    #@d6
    .line 421
    if-eqz v13, :cond_6

    #@d8
    .line 422
    move-object/from16 v0, p0

    #@da
    iget v8, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarHeight:I

    #@dc
    .line 433
    :cond_2
    :goto_2
    move-object/from16 v0, p0

    #@de
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@e0
    move-object/from16 v0, p0

    #@e2
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseContentInsets:Landroid/graphics/Rect;

    #@e4
    invoke-virtual {v1, v2}, Landroid/graphics/Rect;->set(Landroid/graphics/Rect;)V

    #@e7
    .line 434
    move-object/from16 v0, p0

    #@e9
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@eb
    move-object/from16 v0, p0

    #@ed
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBaseInnerInsets:Landroid/graphics/Rect;

    #@ef
    invoke-virtual {v1, v2}, Landroid/graphics/Rect;->set(Landroid/graphics/Rect;)V

    #@f2
    .line 435
    move-object/from16 v0, p0

    #@f4
    iget-boolean v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mOverlayMode:Z

    #@f6
    if-nez v1, :cond_7

    #@f8
    if-nez v13, :cond_7

    #@fa
    .line 436
    move-object/from16 v0, p0

    #@fc
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@fe
    iget v2, v1, Landroid/graphics/Rect;->top:I

    #@100
    add-int/2addr v2, v15

    #@101
    iput v2, v1, Landroid/graphics/Rect;->top:I

    #@103
    .line 437
    move-object/from16 v0, p0

    #@105
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@107
    iget v2, v1, Landroid/graphics/Rect;->bottom:I

    #@109
    add-int/2addr v2, v8

    #@10a
    iput v2, v1, Landroid/graphics/Rect;->bottom:I

    #@10c
    .line 442
    :goto_3
    move-object/from16 v0, p0

    #@10e
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@110
    move-object/from16 v0, p0

    #@112
    iget-object v3, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContentInsets:Landroid/graphics/Rect;

    #@114
    const/4 v4, 0x1

    #@115
    const/4 v5, 0x1

    #@116
    const/4 v6, 0x1

    #@117
    const/4 v7, 0x1

    #@118
    move-object/from16 v1, p0

    #@11a
    invoke-direct/range {v1 .. v7}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->applyInsets(Landroid/view/View;Landroid/graphics/Rect;ZZZZ)Z

    #@11d
    .line 444
    move-object/from16 v0, p0

    #@11f
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastInnerInsets:Landroid/graphics/Rect;

    #@121
    move-object/from16 v0, p0

    #@123
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@125
    invoke-virtual {v1, v2}, Landroid/graphics/Rect;->equals(Ljava/lang/Object;)Z

    #@128
    move-result v1

    #@129
    if-nez v1, :cond_3

    #@12b
    .line 448
    move-object/from16 v0, p0

    #@12d
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastInnerInsets:Landroid/graphics/Rect;

    #@12f
    move-object/from16 v0, p0

    #@131
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@133
    invoke-virtual {v1, v2}, Landroid/graphics/Rect;->set(Landroid/graphics/Rect;)V

    #@136
    .line 450
    move-object/from16 v0, p0

    #@138
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@13a
    move-object/from16 v0, p0

    #@13c
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@13e
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ContentFrameLayout;->dispatchFitSystemWindows(Landroid/graphics/Rect;)V

    #@141
    .line 453
    :cond_3
    move-object/from16 v0, p0

    #@143
    iget-object v2, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@145
    const/4 v4, 0x0

    #@146
    const/4 v6, 0x0

    #@147
    move-object/from16 v1, p0

    #@149
    move/from16 v3, p1

    #@14b
    move/from16 v5, p2

    #@14d
    invoke-virtual/range {v1 .. v6}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@150
    .line 454
    move-object/from16 v0, p0

    #@152
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@154
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ContentFrameLayout;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@157
    move-result-object v10

    #@158
    .end local v10    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    check-cast v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;

    #@15a
    .line 455
    .restart local v10    # "lp":Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;
    move-object/from16 v0, p0

    #@15c
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@15e
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ContentFrameLayout;->getMeasuredWidth()I

    #@161
    move-result v1

    #@162
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->leftMargin:I

    #@164
    add-int/2addr v1, v2

    #@165
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->rightMargin:I

    #@167
    add-int/2addr v1, v2

    #@168
    invoke-static {v12, v1}, Ljava/lang/Math;->max(II)I

    #@16b
    move-result v12

    #@16c
    .line 457
    move-object/from16 v0, p0

    #@16e
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@170
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ContentFrameLayout;->getMeasuredHeight()I

    #@173
    move-result v1

    #@174
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->topMargin:I

    #@176
    add-int/2addr v1, v2

    #@177
    iget v2, v10, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$LayoutParams;->bottomMargin:I

    #@179
    add-int/2addr v1, v2

    #@17a
    invoke-static {v11, v1}, Ljava/lang/Math;->max(II)I

    #@17d
    move-result v11

    #@17e
    .line 459
    move-object/from16 v0, p0

    #@180
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@182
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@185
    move-result v1

    #@186
    invoke-static {v9, v1}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@189
    move-result v9

    #@18a
    .line 463
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingLeft()I

    #@18d
    move-result v1

    #@18e
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingRight()I

    #@191
    move-result v2

    #@192
    add-int/2addr v1, v2

    #@193
    add-int/2addr v12, v1

    #@194
    .line 464
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingTop()I

    #@197
    move-result v1

    #@198
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getPaddingBottom()I

    #@19b
    move-result v2

    #@19c
    add-int/2addr v1, v2

    #@19d
    add-int/2addr v11, v1

    #@19e
    .line 467
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getSuggestedMinimumHeight()I

    #@1a1
    move-result v1

    #@1a2
    invoke-static {v11, v1}, Ljava/lang/Math;->max(II)I

    #@1a5
    move-result v11

    #@1a6
    .line 468
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getSuggestedMinimumWidth()I

    #@1a9
    move-result v1

    #@1aa
    invoke-static {v12, v1}, Ljava/lang/Math;->max(II)I

    #@1ad
    move-result v12

    #@1ae
    .line 470
    move/from16 v0, p1

    #@1b0
    invoke-static {v12, v0, v9}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@1b3
    move-result v1

    #@1b4
    shl-int/lit8 v2, v9, 0x10

    #@1b6
    move/from16 v0, p2

    #@1b8
    invoke-static {v11, v0, v2}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@1bb
    move-result v2

    #@1bc
    move-object/from16 v0, p0

    #@1be
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setMeasuredDimension(II)V

    #@1c1
    .line 474
    return-void

    #@1c2
    .line 399
    .end local v13    # "stable":Z
    :cond_4
    const/4 v13, 0x0

    #@1c3
    goto/16 :goto_0

    #@1c5
    .line 412
    .restart local v13    # "stable":Z
    :cond_5
    move-object/from16 v0, p0

    #@1c7
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1c9
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@1cc
    move-result v1

    #@1cd
    const/16 v2, 0x8

    #@1cf
    if-eq v1, v2, :cond_1

    #@1d1
    .line 415
    move-object/from16 v0, p0

    #@1d3
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1d5
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@1d8
    move-result v15

    #@1d9
    goto/16 :goto_1

    #@1db
    .line 424
    :cond_6
    move-object/from16 v0, p0

    #@1dd
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1df
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@1e2
    move-result v8

    #@1e3
    goto/16 :goto_2

    #@1e5
    .line 439
    :cond_7
    move-object/from16 v0, p0

    #@1e7
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@1e9
    iget v2, v1, Landroid/graphics/Rect;->top:I

    #@1eb
    add-int/2addr v2, v15

    #@1ec
    iput v2, v1, Landroid/graphics/Rect;->top:I

    #@1ee
    .line 440
    move-object/from16 v0, p0

    #@1f0
    iget-object v1, v0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mInnerInsets:Landroid/graphics/Rect;

    #@1f2
    iget v2, v1, Landroid/graphics/Rect;->bottom:I

    #@1f4
    add-int/2addr v2, v8

    #@1f5
    iput v2, v1, Landroid/graphics/Rect;->bottom:I

    #@1f7
    goto/16 :goto_3
.end method

.method public onNestedFling(Landroid/view/View;FFZ)Z
    .locals 2
    .param p1, "target"    # Landroid/view/View;
    .param p2, "velocityX"    # F
    .param p3, "velocityY"    # F
    .param p4, "consumed"    # Z

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 567
    iget-boolean v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@3
    if-eqz v1, :cond_0

    #@5
    if-nez p4, :cond_1

    #@7
    .line 568
    :cond_0
    const/4 v0, 0x0

    #@8
    .line 576
    :goto_0
    return v0

    #@9
    .line 570
    :cond_1
    invoke-direct {p0, p2, p3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->shouldHideActionBarOnFling(FF)Z

    #@c
    move-result v1

    #@d
    if-eqz v1, :cond_2

    #@f
    .line 571
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->addActionBarHideOffset()V

    #@12
    .line 575
    :goto_1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAnimatingForFling:Z

    #@14
    goto :goto_0

    #@15
    .line 573
    :cond_2
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->removeActionBarHideOffset()V

    #@18
    goto :goto_1
.end method

.method public onNestedScroll(Landroid/view/View;IIII)V
    .locals 1
    .param p1, "target"    # Landroid/view/View;
    .param p2, "dxConsumed"    # I
    .param p3, "dyConsumed"    # I
    .param p4, "dxUnconsumed"    # I
    .param p5, "dyUnconsumed"    # I

    #@0
    .prologue
    .line 546
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScrollReference:I

    #@2
    add-int/2addr v0, p3

    #@3
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScrollReference:I

    #@5
    .line 547
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScrollReference:I

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setActionBarHideOffset(I)V

    #@a
    .line 548
    return-void
.end method

.method public onNestedScrollAccepted(Landroid/view/View;Landroid/view/View;I)V
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "target"    # Landroid/view/View;
    .param p3, "axes"    # I

    #@0
    .prologue
    .line 535
    invoke-super {p0, p1, p2, p3}, Landroid/view/ViewGroup;->onNestedScrollAccepted(Landroid/view/View;Landroid/view/View;I)V

    #@3
    .line 536
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getActionBarHideOffset()I

    #@6
    move-result v0

    #@7
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScrollReference:I

    #@9
    .line 537
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@c
    .line 538
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 539
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@12
    invoke-interface {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->onContentScrollStarted()V

    #@15
    .line 541
    :cond_0
    return-void
.end method

.method public onStartNestedScroll(Landroid/view/View;Landroid/view/View;I)Z
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "target"    # Landroid/view/View;
    .param p3, "axes"    # I

    #@0
    .prologue
    .line 527
    and-int/lit8 v0, p3, 0x2

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_1

    #@c
    .line 528
    :cond_0
    const/4 v0, 0x0

    #@d
    .line 530
    :goto_0
    return v0

    #@e
    :cond_1
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@10
    goto :goto_0
.end method

.method public onStopNestedScroll(Landroid/view/View;)V
    .locals 2
    .param p1, "target"    # Landroid/view/View;

    #@0
    .prologue
    .line 552
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onStopNestedScroll(Landroid/view/View;)V

    #@3
    .line 553
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@5
    if-eqz v0, :cond_0

    #@7
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAnimatingForFling:Z

    #@9
    if-nez v0, :cond_0

    #@b
    .line 554
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScrollReference:I

    #@d
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@f
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@12
    move-result v1

    #@13
    if-gt v0, v1, :cond_2

    #@15
    .line 555
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->postRemoveActionBarHideOffset()V

    #@18
    .line 560
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@1a
    if-eqz v0, :cond_1

    #@1c
    .line 561
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@1e
    invoke-interface {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->onContentScrollStopped()V

    #@21
    .line 563
    :cond_1
    return-void

    #@22
    .line 557
    :cond_2
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->postAddActionBarHideOffset()V

    #@25
    goto :goto_0
.end method

.method public onWindowSystemUiVisibilityChanged(I)V
    .locals 7
    .param p1, "visible"    # I

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 259
    sget v5, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    const/16 v6, 0x10

    #@6
    if-lt v5, v6, :cond_0

    #@8
    .line 260
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onWindowSystemUiVisibilityChanged(I)V

    #@b
    .line 262
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@e
    .line 263
    iget v5, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastSystemUiVisibility:I

    #@10
    xor-int v1, v5, p1

    #@12
    .line 264
    .local v1, "diff":I
    iput p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastSystemUiVisibility:I

    #@14
    .line 265
    and-int/lit8 v5, p1, 0x4

    #@16
    if-nez v5, :cond_4

    #@18
    move v0, v3

    #@19
    .line 266
    .local v0, "barVisible":Z
    :goto_0
    and-int/lit16 v5, p1, 0x100

    #@1b
    if-eqz v5, :cond_5

    #@1d
    move v2, v3

    #@1e
    .line 267
    .local v2, "stable":Z
    :goto_1
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@20
    if-eqz v5, :cond_2

    #@22
    .line 271
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@24
    if-nez v2, :cond_6

    #@26
    :goto_2
    invoke-interface {v5, v3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->enableContentAnimations(Z)V

    #@29
    .line 272
    if-nez v0, :cond_1

    #@2b
    if-nez v2, :cond_7

    #@2d
    :cond_1
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@2f
    invoke-interface {v3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->showForSystem()V

    #@32
    .line 275
    :cond_2
    :goto_3
    and-int/lit16 v3, v1, 0x100

    #@34
    if-eqz v3, :cond_3

    #@36
    .line 276
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@38
    if-eqz v3, :cond_3

    #@3a
    .line 277
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@3d
    .line 280
    :cond_3
    return-void

    #@3e
    .end local v0    # "barVisible":Z
    .end local v2    # "stable":Z
    :cond_4
    move v0, v4

    #@3f
    .line 265
    goto :goto_0

    #@40
    .restart local v0    # "barVisible":Z
    :cond_5
    move v2, v4

    #@41
    .line 266
    goto :goto_1

    #@42
    .restart local v2    # "stable":Z
    :cond_6
    move v3, v4

    #@43
    .line 271
    goto :goto_2

    #@44
    .line 273
    :cond_7
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@46
    invoke-interface {v3}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->hideForSystem()V

    #@49
    goto :goto_3
.end method

.method protected onWindowVisibilityChanged(I)V
    .locals 1
    .param p1, "visibility"    # I

    #@0
    .prologue
    .line 284
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onWindowVisibilityChanged(I)V

    #@3
    .line 285
    iput p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowVisibility:I

    #@5
    .line 286
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 287
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@b
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->onWindowVisibilityChanged(I)V

    #@e
    .line 289
    :cond_0
    return-void
.end method

.method pullChildren()V
    .locals 1

    #@0
    .prologue
    .line 580
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 581
    sget v0, Landroid/support/v7/appcompat/R$id;->action_bar_activity_content:I

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->findViewById(I)Landroid/view/View;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@c
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mContent:Landroid/support/v7/internal/widget/ContentFrameLayout;

    #@e
    .line 582
    sget v0, Landroid/support/v7/appcompat/R$id;->action_bar_container:I

    #@10
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->findViewById(I)Landroid/view/View;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/support/v7/internal/widget/ActionBarContainer;

    #@16
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@18
    .line 583
    sget v0, Landroid/support/v7/appcompat/R$id;->action_bar:I

    #@1a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->findViewById(I)Landroid/view/View;

    #@1d
    move-result-object v0

    #@1e
    invoke-direct {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getDecorToolbar(Landroid/view/View;)Landroid/support/v7/internal/widget/DecorToolbar;

    #@21
    move-result-object v0

    #@22
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@24
    .line 584
    sget v0, Landroid/support/v7/appcompat/R$id;->split_action_bar:I

    #@26
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->findViewById(I)Landroid/view/View;

    #@29
    move-result-object v0

    #@2a
    check-cast v0, Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2c
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2e
    .line 586
    :cond_0
    return-void
.end method

.method public restoreToolbarHierarchyState(Landroid/util/SparseArray;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 789
    .local p1, "toolbarStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 790
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->restoreHierarchyState(Landroid/util/SparseArray;)V

    #@8
    .line 791
    return-void
.end method

.method public saveToolbarHierarchyState(Landroid/util/SparseArray;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 783
    .local p1, "toolbarStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 784
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->saveHierarchyState(Landroid/util/SparseArray;)V

    #@8
    .line 785
    return-void
.end method

.method public setActionBarHideOffset(I)V
    .locals 5
    .param p1, "offset"    # I

    #@0
    .prologue
    .line 621
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@3
    .line 622
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@5
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@8
    move-result v2

    #@9
    .line 623
    .local v2, "topHeight":I
    const/4 v3, 0x0

    #@a
    invoke-static {p1, v2}, Ljava/lang/Math;->min(II)I

    #@d
    move-result v4

    #@e
    invoke-static {v3, v4}, Ljava/lang/Math;->max(II)I

    #@11
    move-result p1

    #@12
    .line 624
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@14
    neg-int v4, p1

    #@15
    int-to-float v4, v4

    #@16
    invoke-static {v3, v4}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@19
    .line 625
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1b
    if-eqz v3, :cond_0

    #@1d
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1f
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@22
    move-result v3

    #@23
    const/16 v4, 0x8

    #@25
    if-eq v3, v4, :cond_0

    #@27
    .line 627
    int-to-float v3, p1

    #@28
    int-to-float v4, v2

    #@29
    div-float v1, v3, v4

    #@2b
    .line 628
    .local v1, "fOffset":F
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2d
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@30
    move-result v3

    #@31
    int-to-float v3, v3

    #@32
    mul-float/2addr v3, v1

    #@33
    float-to-int v0, v3

    #@34
    .line 629
    .local v0, "bOffset":I
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@36
    int-to-float v4, v0

    #@37
    invoke-static {v3, v4}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@3a
    .line 631
    .end local v0    # "bOffset":I
    .end local v1    # "fOffset":F
    :cond_0
    return-void
.end method

.method public setActionBarVisibilityCallback(Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;)V
    .locals 3
    .param p1, "cb"    # Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@0
    .prologue
    .line 196
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@2
    .line 197
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getWindowToken()Landroid/os/IBinder;

    #@5
    move-result-object v1

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 200
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarVisibilityCallback:Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;

    #@a
    iget v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mWindowVisibility:I

    #@c
    invoke-interface {v1, v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;->onWindowVisibilityChanged(I)V

    #@f
    .line 201
    iget v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastSystemUiVisibility:I

    #@11
    if-eqz v1, :cond_0

    #@13
    .line 202
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mLastSystemUiVisibility:I

    #@15
    .line 203
    .local v0, "newVis":I
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->onWindowSystemUiVisibilityChanged(I)V

    #@18
    .line 204
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@1b
    .line 207
    .end local v0    # "newVis":I
    :cond_0
    return-void
.end method

.method public setHasNonEmbeddedTabs(Z)V
    .locals 0
    .param p1, "hasNonEmbeddedTabs"    # Z

    #@0
    .prologue
    .line 226
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHasNonEmbeddedTabs:Z

    #@2
    .line 227
    return-void
.end method

.method public setHideOnContentScrollEnabled(Z)V
    .locals 1
    .param p1, "hideOnContentScroll"    # Z

    #@0
    .prologue
    .line 600
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@2
    if-eq p1, v0, :cond_1

    #@4
    .line 601
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mHideOnContentScroll:Z

    #@6
    .line 602
    if-nez p1, :cond_1

    #@8
    .line 603
    invoke-static {}, Landroid/support/v7/internal/VersionUtils;->isAtLeastL()Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_0

    #@e
    .line 604
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->stopNestedScroll()V

    #@11
    .line 606
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V

    #@14
    .line 607
    const/4 v0, 0x0

    #@15
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setActionBarHideOffset(I)V

    #@18
    .line 610
    :cond_1
    return-void
.end method

.method public setIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 723
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 724
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(I)V

    #@8
    .line 725
    return-void
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 729
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 730
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@8
    .line 731
    return-void
.end method

.method public setLogo(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 735
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 736
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setLogo(I)V

    #@8
    .line 737
    return-void
.end method

.method public setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V
    .locals 1
    .param p1, "menu"    # Landroid/view/Menu;
    .param p2, "cb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@0
    .prologue
    .line 777
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 778
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/widget/DecorToolbar;->setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@8
    .line 779
    return-void
.end method

.method public setMenuPrepared()V
    .locals 1

    #@0
    .prologue
    .line 771
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 772
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->setMenuPrepared()V

    #@8
    .line 773
    return-void
.end method

.method public setOverlayMode(Z)V
    .locals 2
    .param p1, "overlayMode"    # Z

    #@0
    .prologue
    .line 210
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mOverlayMode:Z

    #@2
    .line 216
    if-eqz p1, :cond_0

    #@4
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getContext()Landroid/content/Context;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {v0}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@b
    move-result-object v0

    #@c
    iget v0, v0, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@e
    const/16 v1, 0x13

    #@10
    if-ge v0, v1, :cond_0

    #@12
    const/4 v0, 0x1

    #@13
    :goto_0
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mIgnoreWindowContentOverlay:Z

    #@15
    .line 219
    return-void

    #@16
    .line 216
    :cond_0
    const/4 v0, 0x0

    #@17
    goto :goto_0
.end method

.method public setShowingForActionMode(Z)V
    .locals 0
    .param p1, "showing"    # Z

    #@0
    .prologue
    .line 248
    return-void
.end method

.method public setUiOptions(I)V
    .locals 0
    .param p1, "uiOptions"    # I

    #@0
    .prologue
    .line 707
    return-void
.end method

.method public setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V
    .locals 1
    .param p1, "cb"    # Landroid/support/v7/internal/app/WindowCallback;

    #@0
    .prologue
    .line 672
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 673
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V

    #@8
    .line 674
    return-void
.end method

.method public setWindowTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 678
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 679
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@8
    .line 680
    return-void
.end method

.method public shouldDelayChildPressedState()Z
    .locals 1

    #@0
    .prologue
    .line 522
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 759
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->pullChildren()V

    #@3
    .line 760
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->showOverflowMenu()Z

    #@8
    move-result v0

    #@9
    return v0
.end method
