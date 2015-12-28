.class public Landroid/support/v7/internal/app/WindowDecorActionBar;
.super Landroid/support/v7/app/ActionBar;
.source "WindowDecorActionBar.java"

# interfaces
.implements Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;,
        Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;
    }
.end annotation


# static fields
.field static final synthetic $assertionsDisabled:Z

.field private static final ALLOW_SHOW_HIDE_ANIMATIONS:Z

.field private static final CONTEXT_DISPLAY_NORMAL:I = 0x0

.field private static final CONTEXT_DISPLAY_SPLIT:I = 0x1

.field private static final INVALID_POSITION:I = -0x1

.field private static final TAG:Ljava/lang/String; = "WindowDecorActionBar"


# instance fields
.field mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

.field private mActivity:Landroid/support/v4/app/FragmentActivity;

.field private mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

.field private mContentAnimations:Z

.field private mContentView:Landroid/view/View;

.field private mContext:Landroid/content/Context;

.field private mContextDisplayMode:I

.field private mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

.field private mCurWindowVisibility:I

.field private mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

.field private mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

.field mDeferredDestroyActionMode:Landroid/support/v7/view/ActionMode;

.field mDeferredModeDestroyCallback:Landroid/support/v7/view/ActionMode$Callback;

.field private mDialog:Landroid/app/Dialog;

.field private mDisplayHomeAsUpSet:Z

.field private mHasEmbeddedTabs:Z

.field private mHiddenByApp:Z

.field private mHiddenBySystem:Z

.field final mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

.field mHideOnContentScroll:Z

.field private mLastMenuVisibility:Z

.field private mMenuVisibilityListeners:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;",
            ">;"
        }
    .end annotation
.end field

.field private mNowShowing:Z

.field private mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

.field private mSavedTabPosition:I

.field private mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

.field private mShowHideAnimationEnabled:Z

.field final mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

.field private mShowingForMode:Z

.field private mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

.field private mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

.field private mTabs:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;",
            ">;"
        }
    .end annotation
.end field

.field private mThemedContext:Landroid/content/Context;

.field private mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field final mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;


# direct methods
.method static constructor <clinit>()V
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 75
    const-class v0, Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@4
    invoke-virtual {v0}, Ljava/lang/Class;->desiredAssertionStatus()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    move v0, v1

    #@b
    :goto_0
    sput-boolean v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->$assertionsDisabled:Z

    #@d
    .line 82
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@f
    const/16 v3, 0xe

    #@11
    if-lt v0, v3, :cond_1

    #@13
    :goto_1
    sput-boolean v1, Landroid/support/v7/internal/app/WindowDecorActionBar;->ALLOW_SHOW_HIDE_ANIMATIONS:Z

    #@15
    return-void

    #@16
    :cond_0
    move v0, v2

    #@17
    .line 75
    goto :goto_0

    #@18
    :cond_1
    move v1, v2

    #@19
    .line 82
    goto :goto_1
.end method

.method public constructor <init>(Landroid/app/Dialog;)V
    .locals 2
    .param p1, "dialog"    # Landroid/app/Dialog;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 182
    invoke-direct {p0}, Landroid/support/v7/app/ActionBar;-><init>()V

    #@4
    .line 97
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@b
    .line 100
    const/4 v0, -0x1

    #@c
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@e
    .line 109
    new-instance v0, Ljava/util/ArrayList;

    #@10
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@13
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@15
    .line 120
    const/4 v0, 0x0

    #@16
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@18
    .line 122
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@1a
    .line 127
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@1c
    .line 135
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;

    #@1e
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$1;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@21
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@23
    .line 155
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$2;

    #@25
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$2;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@28
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2a
    .line 163
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$3;

    #@2c
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$3;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@2f
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@31
    .line 183
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDialog:Landroid/app/Dialog;

    #@33
    .line 184
    invoke-virtual {p1}, Landroid/app/Dialog;->getWindow()Landroid/view/Window;

    #@36
    move-result-object v0

    #@37
    invoke-virtual {v0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@3a
    move-result-object v0

    #@3b
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->init(Landroid/view/View;)V

    #@3e
    .line 185
    return-void
.end method

.method public constructor <init>(Landroid/support/v7/app/ActionBarActivity;Z)V
    .locals 4
    .param p1, "activity"    # Landroid/support/v7/app/ActionBarActivity;
    .param p2, "overlayMode"    # Z

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 172
    invoke-direct {p0}, Landroid/support/v7/app/ActionBar;-><init>()V

    #@4
    .line 97
    new-instance v2, Ljava/util/ArrayList;

    #@6
    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@b
    .line 100
    const/4 v2, -0x1

    #@c
    iput v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@e
    .line 109
    new-instance v2, Ljava/util/ArrayList;

    #@10
    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    #@13
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@15
    .line 120
    const/4 v2, 0x0

    #@16
    iput v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@18
    .line 122
    iput-boolean v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@1a
    .line 127
    iput-boolean v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@1c
    .line 135
    new-instance v2, Landroid/support/v7/internal/app/WindowDecorActionBar$1;

    #@1e
    invoke-direct {v2, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$1;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@21
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@23
    .line 155
    new-instance v2, Landroid/support/v7/internal/app/WindowDecorActionBar$2;

    #@25
    invoke-direct {v2, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$2;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@28
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2a
    .line 163
    new-instance v2, Landroid/support/v7/internal/app/WindowDecorActionBar$3;

    #@2c
    invoke-direct {v2, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$3;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@2f
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@31
    .line 173
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@33
    .line 174
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@36
    move-result-object v1

    #@37
    .line 175
    .local v1, "window":Landroid/view/Window;
    invoke-virtual {v1}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@3a
    move-result-object v0

    #@3b
    .line 176
    .local v0, "decor":Landroid/view/View;
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->init(Landroid/view/View;)V

    #@3e
    .line 177
    if-nez p2, :cond_0

    #@40
    .line 178
    const v2, 0x1020002

    #@43
    invoke-virtual {v0, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@46
    move-result-object v2

    #@47
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@49
    .line 180
    :cond_0
    return-void
.end method

.method public constructor <init>(Landroid/view/View;)V
    .locals 2
    .param p1, "layout"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 191
    invoke-direct {p0}, Landroid/support/v7/app/ActionBar;-><init>()V

    #@4
    .line 97
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@b
    .line 100
    const/4 v0, -0x1

    #@c
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@e
    .line 109
    new-instance v0, Ljava/util/ArrayList;

    #@10
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@13
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@15
    .line 120
    const/4 v0, 0x0

    #@16
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@18
    .line 122
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@1a
    .line 127
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@1c
    .line 135
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;

    #@1e
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$1;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@21
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@23
    .line 155
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$2;

    #@25
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$2;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@28
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2a
    .line 163
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$3;

    #@2c
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$3;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@2f
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@31
    .line 192
    sget-boolean v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->$assertionsDisabled:Z

    #@33
    if-nez v0, :cond_0

    #@35
    invoke-virtual {p1}, Landroid/view/View;->isInEditMode()Z

    #@38
    move-result v0

    #@39
    if-nez v0, :cond_0

    #@3b
    new-instance v0, Ljava/lang/AssertionError;

    #@3d
    invoke-direct {v0}, Ljava/lang/AssertionError;-><init>()V

    #@40
    throw v0

    #@41
    .line 193
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->init(Landroid/view/View;)V

    #@44
    .line 194
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@2
    return v0
.end method

.method static synthetic access$100(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method static synthetic access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    return-object v0
.end method

.method static synthetic access$1100(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/DecorToolbar;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    return-object v0
.end method

.method static synthetic access$1200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/content/Context;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method static synthetic access$1300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/internal/app/WindowDecorActionBar;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I

    #@2
    return v0
.end method

.method static synthetic access$502(Landroid/support/v7/internal/app/WindowDecorActionBar;Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;
    .param p1, "x1"    # Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@0
    .prologue
    .line 75
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@2
    return-object p1
.end method

.method static synthetic access$600(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    return-object v0
.end method

.method static synthetic access$700(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@2
    return v0
.end method

.method static synthetic access$800(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@0
    .prologue
    .line 75
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@2
    return v0
.end method

.method static synthetic access$900(ZZZ)Z
    .locals 1
    .param p0, "x0"    # Z
    .param p1, "x1"    # Z
    .param p2, "x2"    # Z

    #@0
    .prologue
    .line 75
    invoke-static {p0, p1, p2}, Landroid/support/v7/internal/app/WindowDecorActionBar;->checkShowingFlags(ZZZ)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method private static checkShowingFlags(ZZZ)Z
    .locals 1
    .param p0, "hiddenByApp"    # Z
    .param p1, "hiddenBySystem"    # Z
    .param p2, "showingForMode"    # Z

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 738
    if-eqz p2, :cond_1

    #@3
    .line 743
    :cond_0
    :goto_0
    return v0

    #@4
    .line 740
    :cond_1
    if-nez p0, :cond_2

    #@6
    if-eqz p1, :cond_0

    #@8
    .line 741
    :cond_2
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method private cleanupTabs()V
    .locals 1

    #@0
    .prologue
    .line 430
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 431
    const/4 v0, 0x0

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@8
    .line 433
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@a
    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    #@d
    .line 434
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@f
    if-eqz v0, :cond_1

    #@11
    .line 435
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@13
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeAllTabs()V

    #@16
    .line 437
    :cond_1
    const/4 v0, -0x1

    #@17
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@19
    .line 438
    return-void
.end method

.method private configureTab(Landroid/support/v7/app/ActionBar$Tab;I)V
    .locals 6
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I

    #@0
    .prologue
    .line 531
    move-object v3, p1

    #@1
    check-cast v3, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@3
    .line 532
    .local v3, "tabi":Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;
    invoke-virtual {v3}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getCallback()Landroid/support/v7/app/ActionBar$TabListener;

    #@6
    move-result-object v0

    #@7
    .line 534
    .local v0, "callback":Landroid/support/v7/app/ActionBar$TabListener;
    if-nez v0, :cond_0

    #@9
    .line 535
    new-instance v4, Ljava/lang/IllegalStateException;

    #@b
    const-string v5, "Action Bar Tab must have a Callback"

    #@d
    invoke-direct {v4, v5}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@10
    throw v4

    #@11
    .line 538
    :cond_0
    invoke-virtual {v3, p2}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setPosition(I)V

    #@14
    .line 539
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@16
    invoke-virtual {v4, p2, v3}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    #@19
    .line 541
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@1b
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@1e
    move-result v1

    #@1f
    .line 542
    .local v1, "count":I
    add-int/lit8 v2, p2, 0x1

    #@21
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_1

    #@23
    .line 543
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@25
    invoke-virtual {v4, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@28
    move-result-object v4

    #@29
    check-cast v4, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@2b
    invoke-virtual {v4, v2}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setPosition(I)V

    #@2e
    .line 542
    add-int/lit8 v2, v2, 0x1

    #@30
    goto :goto_0

    #@31
    .line 545
    :cond_1
    return-void
.end method

.method private ensureTabsExist()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 296
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@3
    if-eqz v1, :cond_0

    #@5
    .line 317
    :goto_0
    return-void

    #@6
    .line 300
    :cond_0
    new-instance v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@8
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@a
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;-><init>(Landroid/content/Context;)V

    #@d
    .line 302
    .local v0, "tabScroller":Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@f
    if-eqz v1, :cond_1

    #@11
    .line 303
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@14
    .line 304
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@16
    invoke-interface {v1, v0}, Landroid/support/v7/internal/widget/DecorToolbar;->setEmbeddedTabView(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@19
    .line 316
    :goto_1
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@1b
    goto :goto_0

    #@1c
    .line 306
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getNavigationMode()I

    #@1f
    move-result v1

    #@20
    const/4 v2, 0x2

    #@21
    if-ne v1, v2, :cond_3

    #@23
    .line 307
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@26
    .line 308
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@28
    if-eqz v1, :cond_2

    #@2a
    .line 309
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2c
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@2f
    .line 314
    :cond_2
    :goto_2
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@31
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->setTabContainer(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@34
    goto :goto_1

    #@35
    .line 312
    :cond_3
    const/16 v1, 0x8

    #@37
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@3a
    goto :goto_2
.end method

.method private getDecorToolbar(Landroid/view/View;)Landroid/support/v7/internal/widget/DecorToolbar;
    .locals 3
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 243
    instance-of v0, p1, Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 244
    check-cast p1, Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    .line 246
    .end local p1    # "view":Landroid/view/View;
    :goto_0
    return-object p1

    #@7
    .line 245
    .restart local p1    # "view":Landroid/view/View;
    :cond_0
    instance-of v0, p1, Landroid/support/v7/widget/Toolbar;

    #@9
    if-eqz v0, :cond_1

    #@b
    .line 246
    check-cast p1, Landroid/support/v7/widget/Toolbar;

    #@d
    .end local p1    # "view":Landroid/view/View;
    invoke-virtual {p1}, Landroid/support/v7/widget/Toolbar;->getWrapper()Landroid/support/v7/internal/widget/DecorToolbar;

    #@10
    move-result-object p1

    #@11
    goto :goto_0

    #@12
    .line 248
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

.method private hideForActionMode()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 691
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowingForMode:Z

    #@3
    if-eqz v0, :cond_1

    #@5
    .line 692
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowingForMode:Z

    #@7
    .line 693
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 694
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@d
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setShowingForActionMode(Z)V

    #@10
    .line 696
    :cond_0
    invoke-direct {p0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@13
    .line 698
    :cond_1
    return-void
.end method

.method private init(Landroid/view/View;)V
    .locals 11
    .param p1, "decor"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    const/4 v7, 0x0

    #@2
    .line 197
    sget v5, Landroid/support/v7/appcompat/R$id;->decor_content_parent:I

    #@4
    invoke-virtual {p1, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@7
    move-result-object v5

    #@8
    check-cast v5, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@a
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@c
    .line 198
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@e
    if-eqz v5, :cond_0

    #@10
    .line 199
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@12
    invoke-virtual {v5, p0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setActionBarVisibilityCallback(Landroid/support/v7/internal/widget/ActionBarOverlayLayout$ActionBarVisibilityCallback;)V

    #@15
    .line 201
    :cond_0
    sget v5, Landroid/support/v7/appcompat/R$id;->action_bar:I

    #@17
    invoke-virtual {p1, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@1a
    move-result-object v5

    #@1b
    invoke-direct {p0, v5}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getDecorToolbar(Landroid/view/View;)Landroid/support/v7/internal/widget/DecorToolbar;

    #@1e
    move-result-object v5

    #@1f
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@21
    .line 202
    sget v5, Landroid/support/v7/appcompat/R$id;->action_context_bar:I

    #@23
    invoke-virtual {p1, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@26
    move-result-object v5

    #@27
    check-cast v5, Landroid/support/v7/internal/widget/ActionBarContextView;

    #@29
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2b
    .line 204
    sget v5, Landroid/support/v7/appcompat/R$id;->action_bar_container:I

    #@2d
    invoke-virtual {p1, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@30
    move-result-object v5

    #@31
    check-cast v5, Landroid/support/v7/internal/widget/ActionBarContainer;

    #@33
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@35
    .line 207
    sget v5, Landroid/support/v7/appcompat/R$id;->split_action_bar:I

    #@37
    invoke-virtual {p1, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@3a
    move-result-object v5

    #@3b
    check-cast v5, Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3d
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3f
    .line 209
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@41
    if-eqz v5, :cond_1

    #@43
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@45
    if-eqz v5, :cond_1

    #@47
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@49
    if-nez v5, :cond_2

    #@4b
    .line 210
    :cond_1
    new-instance v5, Ljava/lang/IllegalStateException;

    #@4d
    new-instance v6, Ljava/lang/StringBuilder;

    #@4f
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@52
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@55
    move-result-object v7

    #@56
    invoke-virtual {v7}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@59
    move-result-object v7

    #@5a
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@5d
    move-result-object v6

    #@5e
    const-string v7, " can only be used "

    #@60
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@63
    move-result-object v6

    #@64
    const-string v7, "with a compatible window decor layout"

    #@66
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@69
    move-result-object v6

    #@6a
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@6d
    move-result-object v6

    #@6e
    invoke-direct {v5, v6}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@71
    throw v5

    #@72
    .line 214
    :cond_2
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@74
    invoke-interface {v5}, Landroid/support/v7/internal/widget/DecorToolbar;->getContext()Landroid/content/Context;

    #@77
    move-result-object v5

    #@78
    iput-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@7a
    .line 215
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@7c
    invoke-interface {v5}, Landroid/support/v7/internal/widget/DecorToolbar;->isSplit()Z

    #@7f
    move-result v5

    #@80
    if-eqz v5, :cond_7

    #@82
    move v5, v6

    #@83
    :goto_0
    iput v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I

    #@85
    .line 219
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@87
    invoke-interface {v5}, Landroid/support/v7/internal/widget/DecorToolbar;->getDisplayOptions()I

    #@8a
    move-result v2

    #@8b
    .line 220
    .local v2, "current":I
    and-int/lit8 v5, v2, 0x4

    #@8d
    if-eqz v5, :cond_8

    #@8f
    move v4, v6

    #@90
    .line 221
    .local v4, "homeAsUp":Z
    :goto_1
    if-eqz v4, :cond_3

    #@92
    .line 222
    iput-boolean v6, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDisplayHomeAsUpSet:Z

    #@94
    .line 225
    :cond_3
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@96
    invoke-static {v5}, Landroid/support/v7/internal/view/ActionBarPolicy;->get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;

    #@99
    move-result-object v1

    #@9a
    .line 226
    .local v1, "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ActionBarPolicy;->enableHomeButtonByDefault()Z

    #@9d
    move-result v5

    #@9e
    if-nez v5, :cond_4

    #@a0
    if-eqz v4, :cond_9

    #@a2
    :cond_4
    move v5, v6

    #@a3
    :goto_2
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setHomeButtonEnabled(Z)V

    #@a6
    .line 227
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ActionBarPolicy;->hasEmbeddedTabs()Z

    #@a9
    move-result v5

    #@aa
    invoke-direct {p0, v5}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setHasEmbeddedTabs(Z)V

    #@ad
    .line 229
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@af
    const/4 v8, 0x0

    #@b0
    sget-object v9, Landroid/support/v7/appcompat/R$styleable;->ActionBar:[I

    #@b2
    sget v10, Landroid/support/v7/appcompat/R$attr;->actionBarStyle:I

    #@b4
    invoke-virtual {v5, v8, v9, v10, v7}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@b7
    move-result-object v0

    #@b8
    .line 232
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v5, Landroid/support/v7/appcompat/R$styleable;->ActionBar_hideOnContentScroll:I

    #@ba
    invoke-virtual {v0, v5, v7}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@bd
    move-result v5

    #@be
    if-eqz v5, :cond_5

    #@c0
    .line 233
    invoke-virtual {p0, v6}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setHideOnContentScrollEnabled(Z)V

    #@c3
    .line 235
    :cond_5
    sget v5, Landroid/support/v7/appcompat/R$styleable;->ActionBar_elevation:I

    #@c5
    invoke-virtual {v0, v5, v7}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@c8
    move-result v3

    #@c9
    .line 236
    .local v3, "elevation":I
    if-eqz v3, :cond_6

    #@cb
    .line 237
    int-to-float v5, v3

    #@cc
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setElevation(F)V

    #@cf
    .line 239
    :cond_6
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@d2
    .line 240
    return-void

    #@d3
    .end local v0    # "a":Landroid/content/res/TypedArray;
    .end local v1    # "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    .end local v2    # "current":I
    .end local v3    # "elevation":I
    .end local v4    # "homeAsUp":Z
    :cond_7
    move v5, v7

    #@d4
    .line 215
    goto :goto_0

    #@d5
    .restart local v2    # "current":I
    :cond_8
    move v4, v7

    #@d6
    .line 220
    goto :goto_1

    #@d7
    .restart local v1    # "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    .restart local v4    # "homeAsUp":Z
    :cond_9
    move v5, v7

    #@d8
    .line 226
    goto :goto_2
.end method

.method private setHasEmbeddedTabs(Z)V
    .locals 5
    .param p1, "hasEmbeddedTabs"    # Z

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v1, 0x1

    #@2
    const/4 v2, 0x0

    #@3
    .line 271
    iput-boolean p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@5
    .line 273
    iget-boolean v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@7
    if-nez v3, :cond_1

    #@9
    .line 274
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@b
    invoke-interface {v3, v4}, Landroid/support/v7/internal/widget/DecorToolbar;->setEmbeddedTabView(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@e
    .line 275
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@10
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@12
    invoke-virtual {v3, v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->setTabContainer(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@15
    .line 280
    :goto_0
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getNavigationMode()I

    #@18
    move-result v3

    #@19
    const/4 v4, 0x2

    #@1a
    if-ne v3, v4, :cond_2

    #@1c
    move v0, v1

    #@1d
    .line 281
    .local v0, "isInTabMode":Z
    :goto_1
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@1f
    if-eqz v3, :cond_0

    #@21
    .line 282
    if-eqz v0, :cond_3

    #@23
    .line 283
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@25
    invoke-virtual {v3, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@28
    .line 284
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2a
    if-eqz v3, :cond_0

    #@2c
    .line 285
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2e
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@31
    .line 291
    :cond_0
    :goto_2
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@33
    iget-boolean v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@35
    if-nez v3, :cond_4

    #@37
    if-eqz v0, :cond_4

    #@39
    move v3, v1

    #@3a
    :goto_3
    invoke-interface {v4, v3}, Landroid/support/v7/internal/widget/DecorToolbar;->setCollapsible(Z)V

    #@3d
    .line 292
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@3f
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@41
    if-nez v4, :cond_5

    #@43
    if-eqz v0, :cond_5

    #@45
    :goto_4
    invoke-virtual {v3, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setHasNonEmbeddedTabs(Z)V

    #@48
    .line 293
    return-void

    #@49
    .line 277
    .end local v0    # "isInTabMode":Z
    :cond_1
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@4b
    invoke-virtual {v3, v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->setTabContainer(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@4e
    .line 278
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@50
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@52
    invoke-interface {v3, v4}, Landroid/support/v7/internal/widget/DecorToolbar;->setEmbeddedTabView(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@55
    goto :goto_0

    #@56
    :cond_2
    move v0, v2

    #@57
    .line 280
    goto :goto_1

    #@58
    .line 288
    .restart local v0    # "isInTabMode":Z
    :cond_3
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@5a
    const/16 v4, 0x8

    #@5c
    invoke-virtual {v3, v4}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@5f
    goto :goto_2

    #@60
    :cond_4
    move v3, v2

    #@61
    .line 291
    goto :goto_3

    #@62
    :cond_5
    move v1, v2

    #@63
    .line 292
    goto :goto_4
.end method

.method private showForActionMode()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 666
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowingForMode:Z

    #@3
    if-nez v0, :cond_1

    #@5
    .line 667
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowingForMode:Z

    #@7
    .line 668
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 669
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@d
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setShowingForActionMode(Z)V

    #@10
    .line 671
    :cond_0
    const/4 v0, 0x0

    #@11
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@14
    .line 673
    :cond_1
    return-void
.end method

.method private updateVisibility(Z)V
    .locals 4
    .param p1, "fromSystem"    # Z

    #@0
    .prologue
    .line 749
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@2
    iget-boolean v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@4
    iget-boolean v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowingForMode:Z

    #@6
    invoke-static {v1, v2, v3}, Landroid/support/v7/internal/app/WindowDecorActionBar;->checkShowingFlags(ZZZ)Z

    #@9
    move-result v0

    #@a
    .line 752
    .local v0, "shown":Z
    if-eqz v0, :cond_1

    #@c
    .line 753
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@e
    if-nez v1, :cond_0

    #@10
    .line 754
    const/4 v1, 0x1

    #@11
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@13
    .line 755
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->doShow(Z)V

    #@16
    .line 763
    :cond_0
    :goto_0
    return-void

    #@17
    .line 758
    :cond_1
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@19
    if-eqz v1, :cond_0

    #@1b
    .line 759
    const/4 v1, 0x0

    #@1c
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@1e
    .line 760
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->doHide(Z)V

    #@21
    goto :goto_0
.end method


# virtual methods
.method public addOnMenuVisibilityListener(Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@0
    .prologue
    .line 346
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@5
    .line 347
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 1
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 549
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0}, Ljava/util/ArrayList;->isEmpty()Z

    #@5
    move-result v0

    #@6
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->addTab(Landroid/support/v7/app/ActionBar$Tab;Z)V

    #@9
    .line 550
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;I)V
    .locals 1
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I

    #@0
    .prologue
    .line 554
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0}, Ljava/util/ArrayList;->isEmpty()Z

    #@5
    move-result v0

    #@6
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->addTab(Landroid/support/v7/app/ActionBar$Tab;IZ)V

    #@9
    .line 555
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;IZ)V
    .locals 1
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I
    .param p3, "setSelected"    # Z

    #@0
    .prologue
    .line 569
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->ensureTabsExist()V

    #@3
    .line 570
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@5
    invoke-virtual {v0, p1, p2, p3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->addTab(Landroid/support/v7/app/ActionBar$Tab;IZ)V

    #@8
    .line 571
    invoke-direct {p0, p1, p2}, Landroid/support/v7/internal/app/WindowDecorActionBar;->configureTab(Landroid/support/v7/app/ActionBar$Tab;I)V

    #@b
    .line 572
    if-eqz p3, :cond_0

    #@d
    .line 573
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@10
    .line 575
    :cond_0
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;Z)V
    .locals 1
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "setSelected"    # Z

    #@0
    .prologue
    .line 559
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->ensureTabsExist()V

    #@3
    .line 560
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@5
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->addTab(Landroid/support/v7/app/ActionBar$Tab;Z)V

    #@8
    .line 561
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@a
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@d
    move-result v0

    #@e
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->configureTab(Landroid/support/v7/app/ActionBar$Tab;I)V

    #@11
    .line 562
    if-eqz p2, :cond_0

    #@13
    .line 563
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@16
    .line 565
    :cond_0
    return-void
.end method

.method public animateToMode(Z)V
    .locals 4
    .param p1, "toActionMode"    # Z

    #@0
    .prologue
    const/16 v1, 0x8

    #@2
    const/4 v2, 0x0

    #@3
    .line 870
    if-eqz p1, :cond_0

    #@5
    .line 871
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->showForActionMode()V

    #@8
    .line 876
    :goto_0
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@a
    if-eqz p1, :cond_1

    #@c
    move v0, v1

    #@d
    :goto_1
    invoke-interface {v3, v0}, Landroid/support/v7/internal/widget/DecorToolbar;->animateToVisibility(I)V

    #@10
    .line 877
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@12
    if-eqz p1, :cond_2

    #@14
    :goto_2
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->animateToVisibility(I)V

    #@17
    .line 879
    return-void

    #@18
    .line 873
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->hideForActionMode()V

    #@1b
    goto :goto_0

    #@1c
    :cond_1
    move v0, v2

    #@1d
    .line 876
    goto :goto_1

    #@1e
    :cond_2
    move v2, v1

    #@1f
    .line 877
    goto :goto_2
.end method

.method public collapseActionView()Z
    .locals 1

    #@0
    .prologue
    .line 936
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hasExpandedActionView()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    .line 937
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@e
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->collapseActionView()V

    #@11
    .line 938
    const/4 v0, 0x1

    #@12
    .line 940
    :goto_0
    return v0

    #@13
    :cond_0
    const/4 v0, 0x0

    #@14
    goto :goto_0
.end method

.method completeDeferredDestroyActionMode()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 320
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredModeDestroyCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 321
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredModeDestroyCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredDestroyActionMode:Landroid/support/v7/view/ActionMode;

    #@9
    invoke-interface {v0, v1}, Landroid/support/v7/view/ActionMode$Callback;->onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V

    #@c
    .line 322
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredDestroyActionMode:Landroid/support/v7/view/ActionMode;

    #@e
    .line 323
    iput-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredModeDestroyCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@10
    .line 325
    :cond_0
    return-void
.end method

.method public dispatchMenuVisibilityChanged(Z)V
    .locals 3
    .param p1, "isVisible"    # Z

    #@0
    .prologue
    .line 354
    iget-boolean v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mLastMenuVisibility:Z

    #@2
    if-ne p1, v2, :cond_1

    #@4
    .line 363
    :cond_0
    return-void

    #@5
    .line 357
    :cond_1
    iput-boolean p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mLastMenuVisibility:Z

    #@7
    .line 359
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@c
    move-result v0

    #@d
    .line 360
    .local v0, "count":I
    const/4 v1, 0x0

    #@e
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@10
    .line 361
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@12
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@15
    move-result-object v2

    #@16
    check-cast v2, Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@18
    invoke-interface {v2, p1}, Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;->onMenuVisibilityChanged(Z)V

    #@1b
    .line 360
    add-int/lit8 v1, v1, 0x1

    #@1d
    goto :goto_0
.end method

.method public doHide(Z)V
    .locals 7
    .param p1, "fromSystem"    # Z

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    const/high16 v5, 0x3f800000    # 1.0f

    #@3
    .line 827
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@5
    if-eqz v4, :cond_0

    #@7
    .line 828
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@9
    invoke-virtual {v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->cancel()V

    #@c
    .line 831
    :cond_0
    iget v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@e
    if-nez v4, :cond_5

    #@10
    sget-boolean v4, Landroid/support/v7/internal/app/WindowDecorActionBar;->ALLOW_SHOW_HIDE_ANIMATIONS:Z

    #@12
    if-eqz v4, :cond_5

    #@14
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowHideAnimationEnabled:Z

    #@16
    if-nez v4, :cond_1

    #@18
    if-eqz p1, :cond_5

    #@1a
    .line 833
    :cond_1
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1c
    invoke-static {v4, v5}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@1f
    .line 834
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@21
    invoke-virtual {v4, v6}, Landroid/support/v7/internal/widget/ActionBarContainer;->setTransitioning(Z)V

    #@24
    .line 835
    new-instance v1, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@26
    invoke-direct {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@29
    .line 836
    .local v1, "anim":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2b
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@2e
    move-result v4

    #@2f
    neg-int v4, v4

    #@30
    int-to-float v2, v4

    #@31
    .line 837
    .local v2, "endingY":F
    if-eqz p1, :cond_2

    #@33
    .line 838
    const/4 v4, 0x2

    #@34
    new-array v3, v4, [I

    #@36
    fill-array-data v3, :array_0

    #@39
    .line 839
    .local v3, "topLeft":[I
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3b
    invoke-virtual {v4, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getLocationInWindow([I)V

    #@3e
    .line 840
    aget v4, v3, v6

    #@40
    int-to-float v4, v4

    #@41
    sub-float/2addr v2, v4

    #@42
    .line 842
    .end local v3    # "topLeft":[I
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@44
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@47
    move-result-object v4

    #@48
    invoke-virtual {v4, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4b
    move-result-object v0

    #@4c
    .line 843
    .local v0, "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@4e
    invoke-virtual {v0, v4}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setUpdateListener(Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@51
    .line 844
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@54
    .line 845
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@56
    if-eqz v4, :cond_3

    #@58
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@5a
    if-eqz v4, :cond_3

    #@5c
    .line 846
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@5e
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@61
    move-result-object v4

    #@62
    invoke-virtual {v4, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@65
    move-result-object v4

    #@66
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@69
    .line 848
    :cond_3
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6b
    if-eqz v4, :cond_4

    #@6d
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6f
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@72
    move-result v4

    #@73
    if-nez v4, :cond_4

    #@75
    .line 849
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@77
    invoke-static {v4, v5}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@7a
    .line 850
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@7c
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@7f
    move-result-object v4

    #@80
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@82
    invoke-virtual {v5}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@85
    move-result v5

    #@86
    int-to-float v5, v5

    #@87
    invoke-virtual {v4, v5}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@8a
    move-result-object v4

    #@8b
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@8e
    .line 852
    :cond_4
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@90
    const v5, 0x10a0005

    #@93
    invoke-static {v4, v5}, Landroid/view/animation/AnimationUtils;->loadInterpolator(Landroid/content/Context;I)Landroid/view/animation/Interpolator;

    #@96
    move-result-object v4

    #@97
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@9a
    .line 854
    const-wide/16 v4, 0xfa

    #@9c
    invoke-virtual {v1, v4, v5}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setDuration(J)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@9f
    .line 855
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@a1
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a4
    .line 856
    iput-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a6
    .line 857
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@a9
    .line 861
    .end local v0    # "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .end local v1    # "anim":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .end local v2    # "endingY":F
    :goto_0
    return-void

    #@aa
    .line 859
    :cond_5
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@ac
    const/4 v5, 0x0

    #@ad
    invoke-interface {v4, v5}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationEnd(Landroid/view/View;)V

    #@b0
    goto :goto_0

    #@b1
    .line 838
    nop

    #@b2
    :array_0
    .array-data 4
        0x0
        0x0
    .end array-data
.end method

.method public doShow(Z)V
    .locals 9
    .param p1, "fromSystem"    # Z

    #@0
    .prologue
    const/high16 v8, 0x3f800000    # 1.0f

    #@2
    const/4 v5, 0x1

    #@3
    const/4 v7, 0x0

    #@4
    const/4 v6, 0x0

    #@5
    .line 766
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@7
    if-eqz v4, :cond_0

    #@9
    .line 767
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@b
    invoke-virtual {v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->cancel()V

    #@e
    .line 769
    :cond_0
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@10
    invoke-virtual {v4, v7}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@13
    .line 771
    iget v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@15
    if-nez v4, :cond_6

    #@17
    sget-boolean v4, Landroid/support/v7/internal/app/WindowDecorActionBar;->ALLOW_SHOW_HIDE_ANIMATIONS:Z

    #@19
    if-eqz v4, :cond_6

    #@1b
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowHideAnimationEnabled:Z

    #@1d
    if-nez v4, :cond_1

    #@1f
    if-eqz p1, :cond_6

    #@21
    .line 774
    :cond_1
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@23
    invoke-static {v4, v6}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@26
    .line 775
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@28
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@2b
    move-result v4

    #@2c
    neg-int v4, v4

    #@2d
    int-to-float v2, v4

    #@2e
    .line 776
    .local v2, "startingY":F
    if-eqz p1, :cond_2

    #@30
    .line 777
    const/4 v4, 0x2

    #@31
    new-array v3, v4, [I

    #@33
    fill-array-data v3, :array_0

    #@36
    .line 778
    .local v3, "topLeft":[I
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@38
    invoke-virtual {v4, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getLocationInWindow([I)V

    #@3b
    .line 779
    aget v4, v3, v5

    #@3d
    int-to-float v4, v4

    #@3e
    sub-float/2addr v2, v4

    #@3f
    .line 781
    .end local v3    # "topLeft":[I
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@41
    invoke-static {v4, v2}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@44
    .line 782
    new-instance v1, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@46
    invoke-direct {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@49
    .line 783
    .local v1, "anim":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@4b
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4e
    move-result-object v4

    #@4f
    invoke-virtual {v4, v6}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@52
    move-result-object v0

    #@53
    .line 784
    .local v0, "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mUpdateListener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@55
    invoke-virtual {v0, v4}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setUpdateListener(Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@58
    .line 785
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@5b
    .line 786
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@5d
    if-eqz v4, :cond_3

    #@5f
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@61
    if-eqz v4, :cond_3

    #@63
    .line 787
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@65
    invoke-static {v4, v2}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@68
    .line 788
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@6a
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6d
    move-result-object v4

    #@6e
    invoke-virtual {v4, v6}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@71
    move-result-object v4

    #@72
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@75
    .line 790
    :cond_3
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@77
    if-eqz v4, :cond_4

    #@79
    iget v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I

    #@7b
    if-ne v4, v5, :cond_4

    #@7d
    .line 791
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@7f
    iget-object v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@81
    invoke-virtual {v5}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@84
    move-result v5

    #@85
    int-to-float v5, v5

    #@86
    invoke-static {v4, v5}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@89
    .line 792
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@8b
    invoke-virtual {v4, v7}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@8e
    .line 793
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@90
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@93
    move-result-object v4

    #@94
    invoke-virtual {v4, v6}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@97
    move-result-object v4

    #@98
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@9b
    .line 795
    :cond_4
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@9d
    const v5, 0x10a0006

    #@a0
    invoke-static {v4, v5}, Landroid/view/animation/AnimationUtils;->loadInterpolator(Landroid/content/Context;I)Landroid/view/animation/Interpolator;

    #@a3
    move-result-object v4

    #@a4
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a7
    .line 797
    const-wide/16 v4, 0xfa

    #@a9
    invoke-virtual {v1, v4, v5}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setDuration(J)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@ac
    .line 805
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@ae
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@b1
    .line 806
    iput-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@b3
    .line 807
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@b6
    .line 821
    .end local v0    # "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .end local v1    # "anim":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .end local v2    # "startingY":F
    :goto_0
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@b8
    if-eqz v4, :cond_5

    #@ba
    .line 822
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@bc
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@bf
    .line 824
    :cond_5
    return-void

    #@c0
    .line 809
    :cond_6
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@c2
    invoke-static {v4, v8}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@c5
    .line 810
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@c7
    invoke-static {v4, v6}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@ca
    .line 811
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@cc
    if-eqz v4, :cond_7

    #@ce
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@d0
    if-eqz v4, :cond_7

    #@d2
    .line 812
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;

    #@d4
    invoke-static {v4, v6}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@d7
    .line 814
    :cond_7
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@d9
    if-eqz v4, :cond_8

    #@db
    iget v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I

    #@dd
    if-ne v4, v5, :cond_8

    #@df
    .line 815
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@e1
    invoke-static {v4, v8}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@e4
    .line 816
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@e6
    invoke-static {v4, v6}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@e9
    .line 817
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@eb
    invoke-virtual {v4, v7}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@ee
    .line 819
    :cond_8
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@f0
    const/4 v5, 0x0

    #@f1
    invoke-interface {v4, v5}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationEnd(Landroid/view/View;)V

    #@f4
    goto :goto_0

    #@f5
    .line 777
    nop

    #@f6
    :array_0
    .array-data 4
        0x0
        0x0
    .end array-data
.end method

.method public enableContentAnimations(Z)V
    .locals 0
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 654
    iput-boolean p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z

    #@2
    .line 655
    return-void
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 483
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getCustomView()Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getDisplayOptions()I
    .locals 1

    #@0
    .prologue
    .line 499
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getDisplayOptions()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getElevation()F
    .locals 1

    #@0
    .prologue
    .line 263
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->getElevation(Landroid/view/View;)F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getHeight()I
    .locals 1

    #@0
    .prologue
    .line 650
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getHideOffset()I
    .locals 1

    #@0
    .prologue
    .line 724
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->getActionBarHideOffset()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getNavigationItemCount()I
    .locals 1

    #@0
    .prologue
    .line 1261
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@5
    move-result v0

    #@6
    packed-switch v0, :pswitch_data_0

    #@9
    .line 1267
    const/4 v0, 0x0

    #@a
    :goto_0
    return v0

    #@b
    .line 1263
    :pswitch_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@10
    move-result v0

    #@11
    goto :goto_0

    #@12
    .line 1265
    :pswitch_1
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@14
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getDropdownItemCount()I

    #@17
    move-result v0

    #@18
    goto :goto_0

    #@19
    .line 1261
    nop

    #@1a
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getNavigationMode()I
    .locals 1

    #@0
    .prologue
    .line 495
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getSelectedNavigationIndex()I
    .locals 2

    #@0
    .prologue
    const/4 v0, -0x1

    #@1
    .line 1249
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@3
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@6
    move-result v1

    #@7
    packed-switch v1, :pswitch_data_0

    #@a
    .line 1255
    :cond_0
    :goto_0
    return v0

    #@b
    .line 1251
    :pswitch_0
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@d
    if-eqz v1, :cond_0

    #@f
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@11
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getPosition()I

    #@14
    move-result v0

    #@15
    goto :goto_0

    #@16
    .line 1253
    :pswitch_1
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@18
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getDropdownSelectedPosition()I

    #@1b
    move-result v0

    #@1c
    goto :goto_0

    #@1d
    .line 1249
    nop

    #@1e
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getSelectedTab()Landroid/support/v7/app/ActionBar$Tab;
    .locals 1

    #@0
    .prologue
    .line 645
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@2
    return-object v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 491
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getSubtitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTabAt(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 1308
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/support/v7/app/ActionBar$Tab;

    #@8
    return-object v0
.end method

.method public getTabCount()I
    .locals 1

    #@0
    .prologue
    .line 1273
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getThemedContext()Landroid/content/Context;
    .locals 5

    #@0
    .prologue
    .line 882
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mThemedContext:Landroid/content/Context;

    #@2
    if-nez v3, :cond_0

    #@4
    .line 883
    new-instance v1, Landroid/util/TypedValue;

    #@6
    invoke-direct {v1}, Landroid/util/TypedValue;-><init>()V

    #@9
    .line 884
    .local v1, "outValue":Landroid/util/TypedValue;
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@b
    invoke-virtual {v3}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@e
    move-result-object v0

    #@f
    .line 885
    .local v0, "currentTheme":Landroid/content/res/Resources$Theme;
    sget v3, Landroid/support/v7/appcompat/R$attr;->actionBarWidgetTheme:I

    #@11
    const/4 v4, 0x1

    #@12
    invoke-virtual {v0, v3, v1, v4}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@15
    .line 886
    iget v2, v1, Landroid/util/TypedValue;->resourceId:I

    #@17
    .line 888
    .local v2, "targetThemeRes":I
    if-eqz v2, :cond_1

    #@19
    .line 889
    new-instance v3, Landroid/view/ContextThemeWrapper;

    #@1b
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@1d
    invoke-direct {v3, v4, v2}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@20
    iput-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mThemedContext:Landroid/content/Context;

    #@22
    .line 894
    .end local v0    # "currentTheme":Landroid/content/res/Resources$Theme;
    .end local v1    # "outValue":Landroid/util/TypedValue;
    .end local v2    # "targetThemeRes":I
    :cond_0
    :goto_0
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mThemedContext:Landroid/content/Context;

    #@24
    return-object v3

    #@25
    .line 891
    .restart local v0    # "currentTheme":Landroid/content/res/Resources$Theme;
    .restart local v1    # "outValue":Landroid/util/TypedValue;
    .restart local v2    # "targetThemeRes":I
    :cond_1
    iget-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@27
    iput-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mThemedContext:Landroid/content/Context;

    #@29
    goto :goto_0
.end method

.method getTintManager()Landroid/support/v7/internal/widget/TintManager;
    .locals 2

    #@0
    .prologue
    .line 1347
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1348
    new-instance v0, Landroid/support/v7/internal/widget/TintManager;

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@8
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/TintManager;-><init>(Landroid/content/Context;)V

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@d
    .line 1350
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@f
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 487
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getTitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public hasIcon()Z
    .locals 1

    #@0
    .prologue
    .line 1323
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hasIcon()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public hasLogo()Z
    .locals 1

    #@0
    .prologue
    .line 1337
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->hasLogo()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public hide()V
    .locals 1

    #@0
    .prologue
    .line 684
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 685
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@7
    .line 686
    const/4 v0, 0x0

    #@8
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@b
    .line 688
    :cond_0
    return-void
.end method

.method public hideForSystem()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 701
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@3
    if-nez v0, :cond_0

    #@5
    .line 702
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@7
    .line 703
    invoke-direct {p0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@a
    .line 705
    :cond_0
    return-void
.end method

.method public isHideOnContentScrollEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 719
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->isHideOnContentScrollEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isShowing()Z
    .locals 2

    #@0
    .prologue
    .line 864
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getHeight()I

    #@3
    move-result v0

    #@4
    .line 866
    .local v0, "height":I
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mNowShowing:Z

    #@6
    if-eqz v1, :cond_1

    #@8
    if-eqz v0, :cond_0

    #@a
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getHideOffset()I

    #@d
    move-result v1

    #@e
    if-ge v1, v0, :cond_1

    #@10
    :cond_0
    const/4 v1, 0x1

    #@11
    :goto_0
    return v1

    #@12
    :cond_1
    const/4 v1, 0x0

    #@13
    goto :goto_0
.end method

.method public isTitleTruncated()Z
    .locals 1

    #@0
    .prologue
    .line 899
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->isTitleTruncated()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public newTab()Landroid/support/v7/app/ActionBar$Tab;
    .locals 1

    #@0
    .prologue
    .line 579
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V

    #@5
    return-object v0
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 1
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 267
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@2
    invoke-static {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->hasEmbeddedTabs()Z

    #@9
    move-result v0

    #@a
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setHasEmbeddedTabs(Z)V

    #@d
    .line 268
    return-void
.end method

.method public onContentScrollStarted()V
    .locals 1

    #@0
    .prologue
    .line 924
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 925
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->cancel()V

    #@9
    .line 926
    const/4 v0, 0x0

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@c
    .line 928
    :cond_0
    return-void
.end method

.method public onContentScrollStopped()V
    .locals 0

    #@0
    .prologue
    .line 932
    return-void
.end method

.method public onWindowVisibilityChanged(I)V
    .locals 0
    .param p1, "visibility"    # I

    #@0
    .prologue
    .line 328
    iput p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurWindowVisibility:I

    #@2
    .line 329
    return-void
.end method

.method public removeAllTabs()V
    .locals 0

    #@0
    .prologue
    .line 426
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->cleanupTabs()V

    #@3
    .line 427
    return-void
.end method

.method public removeOnMenuVisibilityListener(Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@0
    .prologue
    .line 350
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@5
    .line 351
    return-void
.end method

.method public removeTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 1
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 584
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBar$Tab;->getPosition()I

    #@3
    move-result v0

    #@4
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->removeTabAt(I)V

    #@7
    .line 585
    return-void
.end method

.method public removeTabAt(I)V
    .locals 7
    .param p1, "position"    # I

    #@0
    .prologue
    .line 589
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    if-nez v4, :cond_1

    #@4
    .line 610
    :cond_0
    :goto_0
    return-void

    #@5
    .line 594
    :cond_1
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@7
    if-eqz v4, :cond_3

    #@9
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@b
    invoke-virtual {v4}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getPosition()I

    #@e
    move-result v3

    #@f
    .line 596
    .local v3, "selectedTabPosition":I
    :goto_1
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@11
    invoke-virtual {v4, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeTabAt(I)V

    #@14
    .line 597
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@16
    invoke-virtual {v4, p1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@19
    move-result-object v2

    #@1a
    check-cast v2, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@1c
    .line 598
    .local v2, "removedTab":Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;
    if-eqz v2, :cond_2

    #@1e
    .line 599
    const/4 v4, -0x1

    #@1f
    invoke-virtual {v2, v4}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setPosition(I)V

    #@22
    .line 602
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@24
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@27
    move-result v1

    #@28
    .line 603
    .local v1, "newTabCount":I
    move v0, p1

    #@29
    .local v0, "i":I
    :goto_2
    if-ge v0, v1, :cond_4

    #@2b
    .line 604
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@2d
    invoke-virtual {v4, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@30
    move-result-object v4

    #@31
    check-cast v4, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@33
    invoke-virtual {v4, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setPosition(I)V

    #@36
    .line 603
    add-int/lit8 v0, v0, 0x1

    #@38
    goto :goto_2

    #@39
    .line 594
    .end local v0    # "i":I
    .end local v1    # "newTabCount":I
    .end local v2    # "removedTab":Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;
    .end local v3    # "selectedTabPosition":I
    :cond_3
    iget v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@3b
    goto :goto_1

    #@3c
    .line 607
    .restart local v0    # "i":I
    .restart local v1    # "newTabCount":I
    .restart local v2    # "removedTab":Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;
    .restart local v3    # "selectedTabPosition":I
    :cond_4
    if-ne v3, p1, :cond_0

    #@3e
    .line 608
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@40
    invoke-virtual {v4}, Ljava/util/ArrayList;->isEmpty()Z

    #@43
    move-result v4

    #@44
    if-eqz v4, :cond_5

    #@46
    const/4 v4, 0x0

    #@47
    :goto_3
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@4a
    goto :goto_0

    #@4b
    :cond_5
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@4d
    const/4 v5, 0x0

    #@4e
    add-int/lit8 v6, p1, -0x1

    #@50
    invoke-static {v5, v6}, Ljava/lang/Math;->max(II)I

    #@53
    move-result v5

    #@54
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@57
    move-result-object v4

    #@58
    check-cast v4, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@5a
    goto :goto_3
.end method

.method public selectTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 4
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    const/4 v1, -0x1

    #@1
    .line 614
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getNavigationMode()I

    #@4
    move-result v2

    #@5
    const/4 v3, 0x2

    #@6
    if-eq v2, v3, :cond_2

    #@8
    .line 615
    if-eqz p1, :cond_0

    #@a
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBar$Tab;->getPosition()I

    #@d
    move-result v1

    #@e
    :cond_0
    iput v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@10
    .line 641
    .end local p1    # "tab":Landroid/support/v7/app/ActionBar$Tab;
    :cond_1
    :goto_0
    return-void

    #@11
    .line 619
    .restart local p1    # "tab":Landroid/support/v7/app/ActionBar$Tab;
    :cond_2
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@13
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorToolbar;->getViewGroup()Landroid/view/ViewGroup;

    #@16
    move-result-object v2

    #@17
    invoke-virtual {v2}, Landroid/view/ViewGroup;->isInEditMode()Z

    #@1a
    move-result v2

    #@1b
    if-eqz v2, :cond_4

    #@1d
    const/4 v0, 0x0

    #@1e
    .line 622
    .local v0, "trans":Landroid/support/v4/app/FragmentTransaction;
    :goto_1
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@20
    if-ne v2, p1, :cond_5

    #@22
    .line 623
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@24
    if-eqz v1, :cond_3

    #@26
    .line 624
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@28
    invoke-virtual {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getCallback()Landroid/support/v7/app/ActionBar$TabListener;

    #@2b
    move-result-object v1

    #@2c
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@2e
    invoke-interface {v1, v2, v0}, Landroid/support/v7/app/ActionBar$TabListener;->onTabReselected(Landroid/support/v7/app/ActionBar$Tab;Landroid/support/v4/app/FragmentTransaction;)V

    #@31
    .line 625
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@33
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBar$Tab;->getPosition()I

    #@36
    move-result v2

    #@37
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->animateToTab(I)V

    #@3a
    .line 638
    .end local p1    # "tab":Landroid/support/v7/app/ActionBar$Tab;
    :cond_3
    :goto_2
    if-eqz v0, :cond_1

    #@3c
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentTransaction;->isEmpty()Z

    #@3f
    move-result v1

    #@40
    if-nez v1, :cond_1

    #@42
    .line 639
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentTransaction;->commit()I

    #@45
    goto :goto_0

    #@46
    .line 619
    .end local v0    # "trans":Landroid/support/v4/app/FragmentTransaction;
    .restart local p1    # "tab":Landroid/support/v7/app/ActionBar$Tab;
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@48
    invoke-virtual {v2}, Landroid/support/v4/app/FragmentActivity;->getSupportFragmentManager()Landroid/support/v4/app/FragmentManager;

    #@4b
    move-result-object v2

    #@4c
    invoke-virtual {v2}, Landroid/support/v4/app/FragmentManager;->beginTransaction()Landroid/support/v4/app/FragmentTransaction;

    #@4f
    move-result-object v2

    #@50
    invoke-virtual {v2}, Landroid/support/v4/app/FragmentTransaction;->disallowAddToBackStack()Landroid/support/v4/app/FragmentTransaction;

    #@53
    move-result-object v0

    #@54
    goto :goto_1

    #@55
    .line 628
    .restart local v0    # "trans":Landroid/support/v4/app/FragmentTransaction;
    :cond_5
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@57
    if-eqz p1, :cond_6

    #@59
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBar$Tab;->getPosition()I

    #@5c
    move-result v1

    #@5d
    :cond_6
    invoke-virtual {v2, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setTabSelected(I)V

    #@60
    .line 629
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@62
    if-eqz v1, :cond_7

    #@64
    .line 630
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@66
    invoke-virtual {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getCallback()Landroid/support/v7/app/ActionBar$TabListener;

    #@69
    move-result-object v1

    #@6a
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@6c
    invoke-interface {v1, v2, v0}, Landroid/support/v7/app/ActionBar$TabListener;->onTabUnselected(Landroid/support/v7/app/ActionBar$Tab;Landroid/support/v4/app/FragmentTransaction;)V

    #@6f
    .line 632
    :cond_7
    check-cast p1, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@71
    .end local p1    # "tab":Landroid/support/v7/app/ActionBar$Tab;
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@73
    .line 633
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@75
    if-eqz v1, :cond_3

    #@77
    .line 634
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@79
    invoke-virtual {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->getCallback()Landroid/support/v7/app/ActionBar$TabListener;

    #@7c
    move-result-object v1

    #@7d
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSelectedTab:Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;

    #@7f
    invoke-interface {v1, v2, v0}, Landroid/support/v7/app/ActionBar$TabListener;->onTabSelected(Landroid/support/v7/app/ActionBar$Tab;Landroid/support/v4/app/FragmentTransaction;)V

    #@82
    goto :goto_2
.end method

.method public setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 469
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContainer;->setPrimaryBackground(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 470
    return-void
.end method

.method public setCustomView(I)V
    .locals 3
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 367
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getThemedContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@7
    move-result-object v0

    #@8
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@a
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->getViewGroup()Landroid/view/ViewGroup;

    #@d
    move-result-object v1

    #@e
    const/4 v2, 0x0

    #@f
    invoke-virtual {v0, p1, v1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@12
    move-result-object v0

    #@13
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setCustomView(Landroid/view/View;)V

    #@16
    .line 369
    return-void
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1233
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setCustomView(Landroid/view/View;)V

    #@5
    .line 1234
    return-void
.end method

.method public setCustomView(Landroid/view/View;Landroid/support/v7/app/ActionBar$LayoutParams;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "layoutParams"    # Landroid/support/v7/app/ActionBar$LayoutParams;

    #@0
    .prologue
    .line 1238
    invoke-virtual {p1, p2}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 1239
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setCustomView(Landroid/view/View;)V

    #@8
    .line 1240
    return-void
.end method

.method public setDefaultDisplayHomeAsUpEnabled(Z)V
    .locals 1
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 1341
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDisplayHomeAsUpSet:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1342
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayHomeAsUpEnabled(Z)V

    #@7
    .line 1344
    :cond_0
    return-void
.end method

.method public setDisplayHomeAsUpEnabled(Z)V
    .locals 2
    .param p1, "showHomeAsUp"    # Z

    #@0
    .prologue
    const/4 v1, 0x4

    #@1
    .line 383
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayOptions(II)V

    #@7
    .line 384
    return-void

    #@8
    .line 383
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public setDisplayOptions(I)V
    .locals 1
    .param p1, "options"    # I

    #@0
    .prologue
    .line 454
    and-int/lit8 v0, p1, 0x4

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 455
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDisplayHomeAsUpSet:Z

    #@7
    .line 457
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@9
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setDisplayOptions(I)V

    #@c
    .line 458
    return-void
.end method

.method public setDisplayOptions(II)V
    .locals 4
    .param p1, "options"    # I
    .param p2, "mask"    # I

    #@0
    .prologue
    .line 461
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->getDisplayOptions()I

    #@5
    move-result v0

    #@6
    .line 462
    .local v0, "current":I
    and-int/lit8 v1, p2, 0x4

    #@8
    if-eqz v1, :cond_0

    #@a
    .line 463
    const/4 v1, 0x1

    #@b
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDisplayHomeAsUpSet:Z

    #@d
    .line 465
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@f
    and-int v2, p1, p2

    #@11
    xor-int/lit8 v3, p2, -0x1

    #@13
    and-int/2addr v3, v0

    #@14
    or-int/2addr v2, v3

    #@15
    invoke-interface {v1, v2}, Landroid/support/v7/internal/widget/DecorToolbar;->setDisplayOptions(I)V

    #@18
    .line 466
    return-void
.end method

.method public setDisplayShowCustomEnabled(Z)V
    .locals 2
    .param p1, "showCustom"    # Z

    #@0
    .prologue
    const/16 v1, 0x10

    #@2
    .line 393
    if-eqz p1, :cond_0

    #@4
    move v0, v1

    #@5
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayOptions(II)V

    #@8
    .line 394
    return-void

    #@9
    .line 393
    :cond_0
    const/4 v0, 0x0

    #@a
    goto :goto_0
.end method

.method public setDisplayShowHomeEnabled(Z)V
    .locals 2
    .param p1, "showHome"    # Z

    #@0
    .prologue
    const/4 v1, 0x2

    #@1
    .line 378
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayOptions(II)V

    #@7
    .line 379
    return-void

    #@8
    .line 378
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public setDisplayShowTitleEnabled(Z)V
    .locals 2
    .param p1, "showTitle"    # Z

    #@0
    .prologue
    const/16 v1, 0x8

    #@2
    .line 388
    if-eqz p1, :cond_0

    #@4
    move v0, v1

    #@5
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayOptions(II)V

    #@8
    .line 389
    return-void

    #@9
    .line 388
    :cond_0
    const/4 v0, 0x0

    #@a
    goto :goto_0
.end method

.method public setDisplayUseLogoEnabled(Z)V
    .locals 2
    .param p1, "useLogo"    # Z

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 373
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setDisplayOptions(II)V

    #@7
    .line 374
    return-void

    #@8
    .line 373
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public setElevation(F)V
    .locals 1
    .param p1, "elevation"    # F

    #@0
    .prologue
    .line 255
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/view/ViewCompat;->setElevation(Landroid/view/View;F)V

    #@5
    .line 256
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 257
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@b
    invoke-static {v0, p1}, Landroid/support/v4/view/ViewCompat;->setElevation(Landroid/view/View;F)V

    #@e
    .line 259
    :cond_0
    return-void
.end method

.method public setHideOffset(I)V
    .locals 2
    .param p1, "offset"    # I

    #@0
    .prologue
    .line 729
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@4
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->isInOverlayMode()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    .line 730
    new-instance v0, Ljava/lang/IllegalStateException;

    #@c
    const-string v1, "Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to set a non-zero hide offset"

    #@e
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@11
    throw v0

    #@12
    .line 733
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@14
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setActionBarHideOffset(I)V

    #@17
    .line 734
    return-void
.end method

.method public setHideOnContentScrollEnabled(Z)V
    .locals 2
    .param p1, "hideOnContentScroll"    # Z

    #@0
    .prologue
    .line 709
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@4
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->isInOverlayMode()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    .line 710
    new-instance v0, Ljava/lang/IllegalStateException;

    #@c
    const-string v1, "Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll"

    #@e
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@11
    throw v0

    #@12
    .line 713
    :cond_0
    iput-boolean p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideOnContentScroll:Z

    #@14
    .line 714
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@16
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setHideOnContentScrollEnabled(Z)V

    #@19
    .line 715
    return-void
.end method

.method public setHomeActionContentDescription(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 919
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationContentDescription(I)V

    #@5
    .line 920
    return-void
.end method

.method public setHomeActionContentDescription(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "description"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 914
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@5
    .line 915
    return-void
.end method

.method public setHomeAsUpIndicator(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 909
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationIcon(I)V

    #@5
    .line 910
    return-void
.end method

.method public setHomeAsUpIndicator(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "indicator"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 904
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 905
    return-void
.end method

.method public setHomeButtonEnabled(Z)V
    .locals 1
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 398
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setHomeButtonEnabled(Z)V

    #@5
    .line 399
    return-void
.end method

.method public setIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1314
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(I)V

    #@5
    .line 1315
    return-void
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1319
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 1320
    return-void
.end method

.method public setListNavigationCallbacks(Landroid/widget/SpinnerAdapter;Landroid/support/v7/app/ActionBar$OnNavigationListener;)V
    .locals 2
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;
    .param p2, "callback"    # Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@0
    .prologue
    .line 1244
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    new-instance v1, Landroid/support/v7/internal/app/NavItemSelectedListener;

    #@4
    invoke-direct {v1, p2}, Landroid/support/v7/internal/app/NavItemSelectedListener;-><init>(Landroid/support/v7/app/ActionBar$OnNavigationListener;)V

    #@7
    invoke-interface {v0, p1, v1}, Landroid/support/v7/internal/widget/DecorToolbar;->setDropdownParams(Landroid/widget/SpinnerAdapter;Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;)V

    #@a
    .line 1245
    return-void
.end method

.method public setLogo(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1328
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setLogo(I)V

    #@5
    .line 1329
    return-void
.end method

.method public setLogo(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "logo"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1333
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 1334
    return-void
.end method

.method public setNavigationMode(I)V
    .locals 7
    .param p1, "mode"    # I

    #@0
    .prologue
    const/4 v6, 0x2

    #@1
    const/4 v2, 0x1

    #@2
    const/4 v5, -0x1

    #@3
    const/4 v3, 0x0

    #@4
    .line 1278
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@9
    move-result v0

    #@a
    .line 1279
    .local v0, "oldMode":I
    packed-switch v0, :pswitch_data_0

    #@d
    .line 1286
    :goto_0
    if-eq v0, p1, :cond_0

    #@f
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@11
    if-nez v1, :cond_0

    #@13
    .line 1287
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@15
    if-eqz v1, :cond_0

    #@17
    .line 1288
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@19
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@1c
    .line 1291
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@1e
    invoke-interface {v1, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationMode(I)V

    #@21
    .line 1292
    packed-switch p1, :pswitch_data_1

    #@24
    .line 1302
    :cond_1
    :goto_1
    iget-object v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@26
    if-ne p1, v6, :cond_2

    #@28
    iget-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@2a
    if-nez v1, :cond_2

    #@2c
    move v1, v2

    #@2d
    :goto_2
    invoke-interface {v4, v1}, Landroid/support/v7/internal/widget/DecorToolbar;->setCollapsible(Z)V

    #@30
    .line 1303
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@32
    if-ne p1, v6, :cond_3

    #@34
    iget-boolean v4, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHasEmbeddedTabs:Z

    #@36
    if-nez v4, :cond_3

    #@38
    :goto_3
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setHasNonEmbeddedTabs(Z)V

    #@3b
    .line 1304
    return-void

    #@3c
    .line 1281
    :pswitch_0
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getSelectedNavigationIndex()I

    #@3f
    move-result v1

    #@40
    iput v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@42
    .line 1282
    const/4 v1, 0x0

    #@43
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@46
    .line 1283
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@48
    const/16 v4, 0x8

    #@4a
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@4d
    goto :goto_0

    #@4e
    .line 1294
    :pswitch_1
    invoke-direct {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->ensureTabsExist()V

    #@51
    .line 1295
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@53
    invoke-virtual {v1, v3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@56
    .line 1296
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@58
    if-eq v1, v5, :cond_1

    #@5a
    .line 1297
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@5c
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setSelectedNavigationItem(I)V

    #@5f
    .line 1298
    iput v5, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSavedTabPosition:I

    #@61
    goto :goto_1

    #@62
    :cond_2
    move v1, v3

    #@63
    .line 1302
    goto :goto_2

    #@64
    :cond_3
    move v2, v3

    #@65
    .line 1303
    goto :goto_3

    #@66
    .line 1279
    :pswitch_data_0
    .packed-switch 0x2
        :pswitch_0
    .end packed-switch

    #@6c
    .line 1292
    :pswitch_data_1
    .packed-switch 0x2
        :pswitch_1
    .end packed-switch
.end method

.method public setSelectedNavigationItem(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 412
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@5
    move-result v0

    #@6
    packed-switch v0, :pswitch_data_0

    #@9
    .line 420
    new-instance v0, Ljava/lang/IllegalStateException;

    #@b
    const-string v1, "setSelectedNavigationIndex not valid for current navigation mode"

    #@d
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@10
    throw v0

    #@11
    .line 414
    :pswitch_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabs:Ljava/util/ArrayList;

    #@13
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@16
    move-result-object v0

    #@17
    check-cast v0, Landroid/support/v7/app/ActionBar$Tab;

    #@19
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@1c
    .line 423
    :goto_0
    return-void

    #@1d
    .line 417
    :pswitch_1
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@1f
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setDropdownSelectedPosition(I)V

    #@22
    goto :goto_0

    #@23
    .line 412
    nop

    #@24
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public setShowHideAnimationEnabled(Z)V
    .locals 1
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 339
    iput-boolean p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mShowHideAnimationEnabled:Z

    #@2
    .line 340
    if-nez p1, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 341
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->cancel()V

    #@d
    .line 343
    :cond_0
    return-void
.end method

.method public setSplitBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 477
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 478
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContainer;->setSplitBackground(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 480
    :cond_0
    return-void
.end method

.method public setStackedBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 473
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContainer;->setStackedBackground(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 474
    return-void
.end method

.method public setSubtitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 408
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@9
    .line 409
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 450
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@5
    .line 451
    return-void
.end method

.method public setTitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 403
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->setTitle(Ljava/lang/CharSequence;)V

    #@9
    .line 404
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 441
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@5
    .line 442
    return-void
.end method

.method public setWindowTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 446
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@5
    .line 447
    return-void
.end method

.method public show()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 659
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 660
    iput-boolean v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z

    #@7
    .line 661
    invoke-direct {p0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@a
    .line 663
    :cond_0
    return-void
.end method

.method public showForSystem()V
    .locals 1

    #@0
    .prologue
    .line 676
    iget-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 677
    const/4 v0, 0x0

    #@5
    iput-boolean v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z

    #@7
    .line 678
    const/4 v0, 0x1

    #@8
    invoke-direct {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->updateVisibility(Z)V

    #@b
    .line 680
    :cond_0
    return-void
.end method

.method public startActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 4
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 503
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@4
    if-eqz v1, :cond_0

    #@6
    .line 504
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@8
    invoke-virtual {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->finish()V

    #@b
    .line 507
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@d
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setHideOnContentScrollEnabled(Z)V

    #@10
    .line 508
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@12
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->killMode()V

    #@15
    .line 509
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@17
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;-><init>(Landroid/support/v7/internal/app/WindowDecorActionBar;Landroid/support/v7/view/ActionMode$Callback;)V

    #@1a
    .line 510
    .local v0, "mode":Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->dispatchOnCreate()Z

    #@1d
    move-result v1

    #@1e
    if-eqz v1, :cond_2

    #@20
    .line 511
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->invalidate()V

    #@23
    .line 512
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@25
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->initForMode(Landroid/support/v7/view/ActionMode;)V

    #@28
    .line 513
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/app/WindowDecorActionBar;->animateToMode(Z)V

    #@2b
    .line 514
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2d
    if-eqz v1, :cond_1

    #@2f
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I

    #@31
    if-ne v1, v3, :cond_1

    #@33
    .line 516
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@35
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@38
    move-result v1

    #@39
    if-eqz v1, :cond_1

    #@3b
    .line 517
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3d
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@40
    .line 518
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@42
    if-eqz v1, :cond_1

    #@44
    .line 519
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@46
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@49
    .line 523
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@4b
    const/16 v2, 0x20

    #@4d
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->sendAccessibilityEvent(I)V

    #@50
    .line 524
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@52
    .line 527
    .end local v0    # "mode":Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;
    :goto_0
    return-object v0

    #@53
    .restart local v0    # "mode":Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;
    :cond_2
    const/4 v0, 0x0

    #@54
    goto :goto_0
.end method
