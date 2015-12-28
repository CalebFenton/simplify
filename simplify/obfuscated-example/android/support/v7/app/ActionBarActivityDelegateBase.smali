.class Landroid/support/v7/app/ActionBarActivityDelegateBase;
.super Landroid/support/v7/app/ActionBarActivityDelegate;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;,
        Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;,
        Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;,
        Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "ActionBarActivityDelegateBase"


# instance fields
.field private mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

.field mActionMode:Landroid/support/v7/view/ActionMode;

.field mActionModePopup:Landroid/widget/PopupWindow;

.field mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

.field private mClosingActionMenu:Z

.field private mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

.field private mEnableDefaultActionBarUp:Z

.field private mFeatureIndeterminateProgress:Z

.field private mFeatureProgress:Z

.field private mInvalidatePanelMenuFeatures:I

.field private mInvalidatePanelMenuPosted:Z

.field private final mInvalidatePanelMenuRunnable:Ljava/lang/Runnable;

.field private mPanelMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;

.field private mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

.field private mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

.field mShowActionModePopup:Ljava/lang/Runnable;

.field private mStatusGuard:Landroid/view/View;

.field private mSubDecor:Landroid/view/ViewGroup;

.field private mSubDecorInstalled:Z

.field private mTempRect1:Landroid/graphics/Rect;

.field private mTempRect2:Landroid/graphics/Rect;

.field private mTitleToSet:Ljava/lang/CharSequence;

.field private mToolbarListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

.field private mWindowDecor:Landroid/view/ViewGroup;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivity;)V
    .locals 1
    .param p1, "activity"    # Landroid/support/v7/app/ActionBarActivity;

    #@0
    .prologue
    .line 133
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;-><init>(Landroid/support/v7/app/ActionBarActivity;)V

    #@3
    .line 111
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;

    #@5
    invoke-direct {v0, p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V

    #@8
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuRunnable:Ljava/lang/Runnable;

    #@a
    .line 134
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/app/ActionBarActivityDelegateBase;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@0
    .prologue
    .line 79
    iget v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I

    #@2
    return v0
.end method

.method static synthetic access$002(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)I
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 79
    iput p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I

    #@2
    return p1
.end method

.method static synthetic access$100(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->doInvalidatePanelMenu(I)V

    #@3
    return-void
.end method

.method static synthetic access$202(Landroid/support/v7/app/ActionBarActivityDelegateBase;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 79
    iput-boolean p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuPosted:Z

    #@2
    return p1
.end method

.method static synthetic access$300(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->updateStatusGuard(I)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static synthetic access$600(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->findMenuPanel(Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$700(Landroid/support/v7/app/ActionBarActivityDelegateBase;ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # I
    .param p2, "x2"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p3, "x3"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->callOnPanelClosed(ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V

    #@3
    return-void
.end method

.method static synthetic access$800(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p2, "x2"    # Z

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@3
    return-void
.end method

.method static synthetic access$900(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p1, "x1"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->checkCloseActionMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@3
    return-void
.end method

.method private applyFixedSizeWindow()V
    .locals 13

    #@0
    .prologue
    .line 381
    iget-object v11, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@2
    sget-object v12, Landroid/support/v7/appcompat/R$styleable;->Theme:[I

    #@4
    invoke-virtual {v11, v12}, Landroid/support/v7/app/ActionBarActivity;->obtainStyledAttributes([I)Landroid/content/res/TypedArray;

    #@7
    move-result-object v0

    #@8
    .line 383
    .local v0, "a":Landroid/content/res/TypedArray;
    const/4 v5, 0x0

    #@9
    .line 384
    .local v5, "mFixedWidthMajor":Landroid/util/TypedValue;
    const/4 v6, 0x0

    #@a
    .line 385
    .local v6, "mFixedWidthMinor":Landroid/util/TypedValue;
    const/4 v3, 0x0

    #@b
    .line 386
    .local v3, "mFixedHeightMajor":Landroid/util/TypedValue;
    const/4 v4, 0x0

    #@c
    .line 388
    .local v4, "mFixedHeightMinor":Landroid/util/TypedValue;
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedWidthMajor:I

    #@e
    invoke-virtual {v0, v11}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@11
    move-result v11

    #@12
    if-eqz v11, :cond_1

    #@14
    .line 389
    if-nez v5, :cond_0

    #@16
    new-instance v5, Landroid/util/TypedValue;

    #@18
    .end local v5    # "mFixedWidthMajor":Landroid/util/TypedValue;
    invoke-direct {v5}, Landroid/util/TypedValue;-><init>()V

    #@1b
    .line 390
    .restart local v5    # "mFixedWidthMajor":Landroid/util/TypedValue;
    :cond_0
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedWidthMajor:I

    #@1d
    invoke-virtual {v0, v11, v5}, Landroid/content/res/TypedArray;->getValue(ILandroid/util/TypedValue;)Z

    #@20
    .line 392
    :cond_1
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedWidthMinor:I

    #@22
    invoke-virtual {v0, v11}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@25
    move-result v11

    #@26
    if-eqz v11, :cond_3

    #@28
    .line 393
    if-nez v6, :cond_2

    #@2a
    new-instance v6, Landroid/util/TypedValue;

    #@2c
    .end local v6    # "mFixedWidthMinor":Landroid/util/TypedValue;
    invoke-direct {v6}, Landroid/util/TypedValue;-><init>()V

    #@2f
    .line 394
    .restart local v6    # "mFixedWidthMinor":Landroid/util/TypedValue;
    :cond_2
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedWidthMinor:I

    #@31
    invoke-virtual {v0, v11, v6}, Landroid/content/res/TypedArray;->getValue(ILandroid/util/TypedValue;)Z

    #@34
    .line 396
    :cond_3
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedHeightMajor:I

    #@36
    invoke-virtual {v0, v11}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@39
    move-result v11

    #@3a
    if-eqz v11, :cond_5

    #@3c
    .line 397
    if-nez v3, :cond_4

    #@3e
    new-instance v3, Landroid/util/TypedValue;

    #@40
    .end local v3    # "mFixedHeightMajor":Landroid/util/TypedValue;
    invoke-direct {v3}, Landroid/util/TypedValue;-><init>()V

    #@43
    .line 398
    .restart local v3    # "mFixedHeightMajor":Landroid/util/TypedValue;
    :cond_4
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedHeightMajor:I

    #@45
    invoke-virtual {v0, v11, v3}, Landroid/content/res/TypedArray;->getValue(ILandroid/util/TypedValue;)Z

    #@48
    .line 400
    :cond_5
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedHeightMinor:I

    #@4a
    invoke-virtual {v0, v11}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@4d
    move-result v11

    #@4e
    if-eqz v11, :cond_7

    #@50
    .line 401
    if-nez v4, :cond_6

    #@52
    new-instance v4, Landroid/util/TypedValue;

    #@54
    .end local v4    # "mFixedHeightMinor":Landroid/util/TypedValue;
    invoke-direct {v4}, Landroid/util/TypedValue;-><init>()V

    #@57
    .line 402
    .restart local v4    # "mFixedHeightMinor":Landroid/util/TypedValue;
    :cond_6
    sget v11, Landroid/support/v7/appcompat/R$styleable;->Theme_windowFixedHeightMinor:I

    #@59
    invoke-virtual {v0, v11, v4}, Landroid/content/res/TypedArray;->getValue(ILandroid/util/TypedValue;)Z

    #@5c
    .line 405
    :cond_7
    iget-object v11, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5e
    invoke-virtual {v11}, Landroid/support/v7/app/ActionBarActivity;->getResources()Landroid/content/res/Resources;

    #@61
    move-result-object v11

    #@62
    invoke-virtual {v11}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@65
    move-result-object v7

    #@66
    .line 406
    .local v7, "metrics":Landroid/util/DisplayMetrics;
    iget v11, v7, Landroid/util/DisplayMetrics;->widthPixels:I

    #@68
    iget v12, v7, Landroid/util/DisplayMetrics;->heightPixels:I

    #@6a
    if-ge v11, v12, :cond_c

    #@6c
    const/4 v2, 0x1

    #@6d
    .line 407
    .local v2, "isPortrait":Z
    :goto_0
    const/4 v10, -0x1

    #@6e
    .line 408
    .local v10, "w":I
    const/4 v1, -0x1

    #@6f
    .line 410
    .local v1, "h":I
    if-eqz v2, :cond_d

    #@71
    move-object v9, v6

    #@72
    .line 411
    .local v9, "tvw":Landroid/util/TypedValue;
    :goto_1
    if-eqz v9, :cond_8

    #@74
    iget v11, v9, Landroid/util/TypedValue;->type:I

    #@76
    if-eqz v11, :cond_8

    #@78
    .line 412
    iget v11, v9, Landroid/util/TypedValue;->type:I

    #@7a
    const/4 v12, 0x5

    #@7b
    if-ne v11, v12, :cond_e

    #@7d
    .line 413
    invoke-virtual {v9, v7}, Landroid/util/TypedValue;->getDimension(Landroid/util/DisplayMetrics;)F

    #@80
    move-result v11

    #@81
    float-to-int v10, v11

    #@82
    .line 419
    :cond_8
    :goto_2
    if-eqz v2, :cond_f

    #@84
    move-object v8, v3

    #@85
    .line 420
    .local v8, "tvh":Landroid/util/TypedValue;
    :goto_3
    if-eqz v8, :cond_9

    #@87
    iget v11, v8, Landroid/util/TypedValue;->type:I

    #@89
    if-eqz v11, :cond_9

    #@8b
    .line 421
    iget v11, v8, Landroid/util/TypedValue;->type:I

    #@8d
    const/4 v12, 0x5

    #@8e
    if-ne v11, v12, :cond_10

    #@90
    .line 422
    invoke-virtual {v8, v7}, Landroid/util/TypedValue;->getDimension(Landroid/util/DisplayMetrics;)F

    #@93
    move-result v11

    #@94
    float-to-int v1, v11

    #@95
    .line 428
    :cond_9
    :goto_4
    const/4 v11, -0x1

    #@96
    if-ne v10, v11, :cond_a

    #@98
    const/4 v11, -0x1

    #@99
    if-eq v1, v11, :cond_b

    #@9b
    .line 429
    :cond_a
    iget-object v11, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@9d
    invoke-virtual {v11}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@a0
    move-result-object v11

    #@a1
    invoke-virtual {v11, v10, v1}, Landroid/view/Window;->setLayout(II)V

    #@a4
    .line 432
    :cond_b
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@a7
    .line 433
    return-void

    #@a8
    .line 406
    .end local v1    # "h":I
    .end local v2    # "isPortrait":Z
    .end local v8    # "tvh":Landroid/util/TypedValue;
    .end local v9    # "tvw":Landroid/util/TypedValue;
    .end local v10    # "w":I
    :cond_c
    const/4 v2, 0x0

    #@a9
    goto :goto_0

    #@aa
    .restart local v1    # "h":I
    .restart local v2    # "isPortrait":Z
    .restart local v10    # "w":I
    :cond_d
    move-object v9, v5

    #@ab
    .line 410
    goto :goto_1

    #@ac
    .line 414
    .restart local v9    # "tvw":Landroid/util/TypedValue;
    :cond_e
    iget v11, v9, Landroid/util/TypedValue;->type:I

    #@ae
    const/4 v12, 0x6

    #@af
    if-ne v11, v12, :cond_8

    #@b1
    .line 415
    iget v11, v7, Landroid/util/DisplayMetrics;->widthPixels:I

    #@b3
    int-to-float v11, v11

    #@b4
    iget v12, v7, Landroid/util/DisplayMetrics;->widthPixels:I

    #@b6
    int-to-float v12, v12

    #@b7
    invoke-virtual {v9, v11, v12}, Landroid/util/TypedValue;->getFraction(FF)F

    #@ba
    move-result v11

    #@bb
    float-to-int v10, v11

    #@bc
    goto :goto_2

    #@bd
    :cond_f
    move-object v8, v4

    #@be
    .line 419
    goto :goto_3

    #@bf
    .line 423
    .restart local v8    # "tvh":Landroid/util/TypedValue;
    :cond_10
    iget v11, v8, Landroid/util/TypedValue;->type:I

    #@c1
    const/4 v12, 0x6

    #@c2
    if-ne v11, v12, :cond_9

    #@c4
    .line 424
    iget v11, v7, Landroid/util/DisplayMetrics;->heightPixels:I

    #@c6
    int-to-float v11, v11

    #@c7
    iget v12, v7, Landroid/util/DisplayMetrics;->heightPixels:I

    #@c9
    int-to-float v12, v12

    #@ca
    invoke-virtual {v8, v11, v12}, Landroid/util/TypedValue;->getFraction(FF)F

    #@cd
    move-result v11

    #@ce
    float-to-int v1, v11

    #@cf
    goto :goto_4
.end method

.method private callOnPanelClosed(ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "panel"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1076
    if-nez p3, :cond_1

    #@2
    .line 1078
    if-nez p2, :cond_0

    #@4
    .line 1079
    if-ltz p1, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@8
    array-length v0, v0

    #@9
    if-ge p1, v0, :cond_0

    #@b
    .line 1080
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@d
    aget-object p2, v0, p1

    #@f
    .line 1084
    :cond_0
    if-eqz p2, :cond_1

    #@11
    .line 1086
    iget-object p3, p2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@13
    .line 1091
    :cond_1
    if-eqz p2, :cond_2

    #@15
    iget-boolean v0, p2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@17
    if-nez v0, :cond_2

    #@19
    .line 1095
    :goto_0
    return-void

    #@1a
    .line 1094
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@1d
    move-result-object v0

    #@1e
    invoke-interface {v0, p1, p3}, Landroid/support/v7/internal/app/WindowCallback;->onPanelClosed(ILandroid/view/Menu;)V

    #@21
    goto :goto_0
.end method

.method private checkCloseActionMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 2
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 1032
    iget-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mClosingActionMenu:Z

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 1043
    :goto_0
    return-void

    #@5
    .line 1036
    :cond_0
    const/4 v1, 0x1

    #@6
    iput-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mClosingActionMenu:Z

    #@8
    .line 1037
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@a
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorContentParent;->dismissPopups()V

    #@d
    .line 1038
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@10
    move-result-object v0

    #@11
    .line 1039
    .local v0, "cb":Landroid/support/v7/internal/app/WindowCallback;
    if-eqz v0, :cond_1

    #@13
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@16
    move-result v1

    #@17
    if-nez v1, :cond_1

    #@19
    .line 1040
    const/16 v1, 0x8

    #@1b
    invoke-interface {v0, v1, p1}, Landroid/support/v7/internal/app/WindowCallback;->onPanelClosed(ILandroid/view/Menu;)V

    #@1e
    .line 1042
    :cond_1
    const/4 v1, 0x0

    #@1f
    iput-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mClosingActionMenu:Z

    #@21
    goto :goto_0
.end method

.method private closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V
    .locals 3
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p2, "doCallback"    # Z

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1046
    if-eqz p2, :cond_1

    #@4
    iget v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@6
    if-nez v0, :cond_1

    #@8
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@a
    if-eqz v0, :cond_1

    #@c
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@e
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorContentParent;->isOverflowMenuShowing()Z

    #@11
    move-result v0

    #@12
    if-eqz v0, :cond_1

    #@14
    .line 1048
    iget-object v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@16
    invoke-direct {p0, v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->checkCloseActionMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@19
    .line 1072
    :cond_0
    :goto_0
    return-void

    #@1a
    .line 1052
    :cond_1
    iget-boolean v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@1c
    if-eqz v0, :cond_2

    #@1e
    .line 1053
    if-eqz p2, :cond_2

    #@20
    .line 1054
    iget v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@22
    invoke-direct {p0, v0, p1, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->callOnPanelClosed(ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V

    #@25
    .line 1058
    :cond_2
    iput-boolean v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@27
    .line 1059
    iput-boolean v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isHandled:Z

    #@29
    .line 1060
    iput-boolean v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@2b
    .line 1063
    iput-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->shownPanelView:Landroid/view/View;

    #@2d
    .line 1067
    const/4 v0, 0x1

    #@2e
    iput-boolean v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshDecorView:Z

    #@30
    .line 1069
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@32
    if-ne v0, p1, :cond_0

    #@34
    .line 1070
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@36
    goto :goto_0
.end method

.method private doInvalidatePanelMenu(I)V
    .locals 5
    .param p1, "featureId"    # I

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 1161
    invoke-direct {p0, p1, v4}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@5
    move-result-object v1

    #@6
    .line 1162
    .local v1, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    const/4 v0, 0x0

    #@7
    .line 1163
    .local v0, "savedActionViewStates":Landroid/os/Bundle;
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    if-eqz v2, :cond_1

    #@b
    .line 1164
    new-instance v0, Landroid/os/Bundle;

    #@d
    .end local v0    # "savedActionViewStates":Landroid/os/Bundle;
    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    #@10
    .line 1165
    .restart local v0    # "savedActionViewStates":Landroid/os/Bundle;
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@12
    invoke-virtual {v2, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->saveActionViewStates(Landroid/os/Bundle;)V

    #@15
    .line 1166
    invoke-virtual {v0}, Landroid/os/Bundle;->size()I

    #@18
    move-result v2

    #@19
    if-lez v2, :cond_0

    #@1b
    .line 1167
    iput-object v0, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->frozenActionViewState:Landroid/os/Bundle;

    #@1d
    .line 1170
    :cond_0
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@1f
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@22
    .line 1171
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@24
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->clear()V

    #@27
    .line 1173
    :cond_1
    iput-boolean v4, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshMenuContent:Z

    #@29
    .line 1174
    iput-boolean v4, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshDecorView:Z

    #@2b
    .line 1177
    const/16 v2, 0x8

    #@2d
    if-eq p1, v2, :cond_2

    #@2f
    if-nez p1, :cond_3

    #@31
    :cond_2
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@33
    if-eqz v2, :cond_3

    #@35
    .line 1179
    invoke-direct {p0, v3, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@38
    move-result-object v1

    #@39
    .line 1180
    if-eqz v1, :cond_3

    #@3b
    .line 1181
    iput-boolean v3, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@3d
    .line 1182
    const/4 v2, 0x0

    #@3e
    invoke-direct {p0, v1, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->preparePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)Z

    #@41
    .line 1185
    :cond_3
    return-void
.end method

.method private ensureToolbarListMenuPresenter()V
    .locals 5

    #@0
    .prologue
    .line 1264
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mToolbarListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@2
    if-nez v2, :cond_0

    #@4
    .line 1266
    new-instance v1, Landroid/util/TypedValue;

    #@6
    invoke-direct {v1}, Landroid/util/TypedValue;-><init>()V

    #@9
    .line 1267
    .local v1, "outValue":Landroid/util/TypedValue;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@b
    invoke-virtual {v2}, Landroid/support/v7/app/ActionBarActivity;->getTheme()Landroid/content/res/Resources$Theme;

    #@e
    move-result-object v2

    #@f
    sget v3, Landroid/support/v7/appcompat/R$attr;->panelMenuListTheme:I

    #@11
    const/4 v4, 0x1

    #@12
    invoke-virtual {v2, v3, v1, v4}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@15
    .line 1269
    new-instance v0, Landroid/view/ContextThemeWrapper;

    #@17
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@19
    iget v2, v1, Landroid/util/TypedValue;->resourceId:I

    #@1b
    if-eqz v2, :cond_1

    #@1d
    iget v2, v1, Landroid/util/TypedValue;->resourceId:I

    #@1f
    :goto_0
    invoke-direct {v0, v3, v2}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@22
    .line 1274
    .local v0, "context":Landroid/content/Context;
    new-instance v2, Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@24
    sget v3, Landroid/support/v7/appcompat/R$layout;->abc_list_menu_item_layout:I

    #@26
    invoke-direct {v2, v0, v3}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;-><init>(Landroid/content/Context;I)V

    #@29
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mToolbarListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@2b
    .line 1277
    .end local v0    # "context":Landroid/content/Context;
    .end local v1    # "outValue":Landroid/util/TypedValue;
    :cond_0
    return-void

    #@2c
    .line 1269
    .restart local v1    # "outValue":Landroid/util/TypedValue;
    :cond_1
    sget v2, Landroid/support/v7/appcompat/R$style;->Theme_AppCompat_CompactMenu:I

    #@2e
    goto :goto_0
.end method

.method private findMenuPanel(Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .locals 5
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1098
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@2
    .line 1099
    .local v3, "panels":[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v3, :cond_0

    #@4
    array-length v0, v3

    #@5
    .line 1100
    .local v0, "N":I
    :goto_0
    const/4 v1, 0x0

    #@6
    .local v1, "i":I
    :goto_1
    if-ge v1, v0, :cond_2

    #@8
    .line 1101
    aget-object v2, v3, v1

    #@a
    .line 1102
    .local v2, "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v2, :cond_1

    #@c
    iget-object v4, v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@e
    if-ne v4, p1, :cond_1

    #@10
    .line 1106
    .end local v2    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :goto_2
    return-object v2

    #@11
    .line 1099
    .end local v0    # "N":I
    .end local v1    # "i":I
    :cond_0
    const/4 v0, 0x0

    #@12
    goto :goto_0

    #@13
    .line 1100
    .restart local v0    # "N":I
    .restart local v1    # "i":I
    .restart local v2    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_1
    add-int/lit8 v1, v1, 0x1

    #@15
    goto :goto_1

    #@16
    .line 1106
    .end local v2    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_2
    const/4 v2, 0x0

    #@17
    goto :goto_2
.end method

.method private getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .locals 5
    .param p1, "featureId"    # I
    .param p2, "required"    # Z

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 1111
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@3
    .local v0, "ar":[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v0, :cond_0

    #@5
    array-length v3, v0

    #@6
    if-gt v3, p1, :cond_2

    #@8
    .line 1112
    :cond_0
    add-int/lit8 v3, p1, 0x1

    #@a
    new-array v1, v3, [Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@c
    .line 1113
    .local v1, "nar":[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v0, :cond_1

    #@e
    .line 1114
    array-length v3, v0

    #@f
    invoke-static {v0, v4, v1, v4, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@12
    .line 1116
    :cond_1
    move-object v0, v1

    #@13
    iput-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanels:[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@15
    .line 1119
    .end local v1    # "nar":[Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_2
    aget-object v2, v0, p1

    #@17
    .line 1120
    .local v2, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-nez v2, :cond_3

    #@19
    .line 1121
    new-instance v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@1b
    .end local v2    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    invoke-direct {v2, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;-><init>(I)V

    #@1e
    .restart local v2    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    aput-object v2, v0, p1

    #@20
    .line 1123
    :cond_3
    return-object v2
.end method

.method private initializePanelContent(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)Z
    .locals 4
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 922
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3
    if-nez v2, :cond_1

    #@5
    .line 934
    :cond_0
    :goto_0
    return v1

    #@6
    .line 926
    :cond_1
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanelMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;

    #@8
    if-nez v2, :cond_2

    #@a
    .line 927
    new-instance v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;

    #@c
    const/4 v3, 0x0

    #@d
    invoke-direct {v2, p0, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$1;)V

    #@10
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanelMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;

    #@12
    .line 930
    :cond_2
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPanelMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;

    #@14
    invoke-virtual {p1, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->getListMenuView(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)Landroid/support/v7/internal/view/menu/MenuView;

    #@17
    move-result-object v0

    #@18
    .line 932
    .local v0, "menuView":Landroid/support/v7/internal/view/menu/MenuView;
    check-cast v0, Landroid/view/View;

    #@1a
    .end local v0    # "menuView":Landroid/support/v7/internal/view/menu/MenuView;
    iput-object v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->shownPanelView:Landroid/view/View;

    #@1c
    .line 934
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->shownPanelView:Landroid/view/View;

    #@1e
    if-eqz v2, :cond_0

    #@20
    const/4 v1, 0x1

    #@21
    goto :goto_0
.end method

.method private initializePanelDecor(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)V
    .locals 1
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@0
    .prologue
    .line 830
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mWindowDecor:Landroid/view/ViewGroup;

    #@2
    iput-object v0, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->decorView:Landroid/view/ViewGroup;

    #@4
    .line 831
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getActionBarThemedContext()Landroid/content/Context;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p1, v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->setStyle(Landroid/content/Context;)V

    #@b
    .line 832
    return-void
.end method

.method private initializePanelMenu(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)Z
    .locals 9
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@0
    .prologue
    const/4 v8, 0x1

    #@1
    .line 879
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@3
    .line 882
    .local v1, "context":Landroid/content/Context;
    iget v6, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@5
    if-eqz v6, :cond_0

    #@7
    iget v6, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@9
    const/16 v7, 0x8

    #@b
    if-ne v6, v7, :cond_3

    #@d
    :cond_0
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@f
    if-eqz v6, :cond_3

    #@11
    .line 884
    new-instance v4, Landroid/util/TypedValue;

    #@13
    invoke-direct {v4}, Landroid/util/TypedValue;-><init>()V

    #@16
    .line 885
    .local v4, "outValue":Landroid/util/TypedValue;
    invoke-virtual {v1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@19
    move-result-object v0

    #@1a
    .line 886
    .local v0, "baseTheme":Landroid/content/res/Resources$Theme;
    sget v6, Landroid/support/v7/appcompat/R$attr;->actionBarTheme:I

    #@1c
    invoke-virtual {v0, v6, v4, v8}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@1f
    .line 888
    const/4 v5, 0x0

    #@20
    .line 889
    .local v5, "widgetTheme":Landroid/content/res/Resources$Theme;
    iget v6, v4, Landroid/util/TypedValue;->resourceId:I

    #@22
    if-eqz v6, :cond_4

    #@24
    .line 890
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@27
    move-result-object v6

    #@28
    invoke-virtual {v6}, Landroid/content/res/Resources;->newTheme()Landroid/content/res/Resources$Theme;

    #@2b
    move-result-object v5

    #@2c
    .line 891
    invoke-virtual {v5, v0}, Landroid/content/res/Resources$Theme;->setTo(Landroid/content/res/Resources$Theme;)V

    #@2f
    .line 892
    iget v6, v4, Landroid/util/TypedValue;->resourceId:I

    #@31
    invoke-virtual {v5, v6, v8}, Landroid/content/res/Resources$Theme;->applyStyle(IZ)V

    #@34
    .line 893
    sget v6, Landroid/support/v7/appcompat/R$attr;->actionBarWidgetTheme:I

    #@36
    invoke-virtual {v5, v6, v4, v8}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@39
    .line 900
    :goto_0
    iget v6, v4, Landroid/util/TypedValue;->resourceId:I

    #@3b
    if-eqz v6, :cond_2

    #@3d
    .line 901
    if-nez v5, :cond_1

    #@3f
    .line 902
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@42
    move-result-object v6

    #@43
    invoke-virtual {v6}, Landroid/content/res/Resources;->newTheme()Landroid/content/res/Resources$Theme;

    #@46
    move-result-object v5

    #@47
    .line 903
    invoke-virtual {v5, v0}, Landroid/content/res/Resources$Theme;->setTo(Landroid/content/res/Resources$Theme;)V

    #@4a
    .line 905
    :cond_1
    iget v6, v4, Landroid/util/TypedValue;->resourceId:I

    #@4c
    invoke-virtual {v5, v6, v8}, Landroid/content/res/Resources$Theme;->applyStyle(IZ)V

    #@4f
    .line 908
    :cond_2
    if-eqz v5, :cond_3

    #@51
    .line 909
    new-instance v2, Landroid/view/ContextThemeWrapper;

    #@53
    const/4 v6, 0x0

    #@54
    invoke-direct {v2, v1, v6}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@57
    .line 910
    .end local v1    # "context":Landroid/content/Context;
    .local v2, "context":Landroid/content/Context;
    invoke-virtual {v2}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@5a
    move-result-object v6

    #@5b
    invoke-virtual {v6, v5}, Landroid/content/res/Resources$Theme;->setTo(Landroid/content/res/Resources$Theme;)V

    #@5e
    move-object v1, v2

    #@5f
    .line 914
    .end local v0    # "baseTheme":Landroid/content/res/Resources$Theme;
    .end local v2    # "context":Landroid/content/Context;
    .end local v4    # "outValue":Landroid/util/TypedValue;
    .end local v5    # "widgetTheme":Landroid/content/res/Resources$Theme;
    .restart local v1    # "context":Landroid/content/Context;
    :cond_3
    new-instance v3, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@61
    invoke-direct {v3, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;-><init>(Landroid/content/Context;)V

    #@64
    .line 915
    .local v3, "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    invoke-virtual {v3, p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@67
    .line 916
    invoke-virtual {p1, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->setMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@6a
    .line 918
    return v8

    #@6b
    .line 896
    .end local v3    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    .restart local v0    # "baseTheme":Landroid/content/res/Resources$Theme;
    .restart local v4    # "outValue":Landroid/util/TypedValue;
    .restart local v5    # "widgetTheme":Landroid/content/res/Resources$Theme;
    :cond_4
    sget v6, Landroid/support/v7/appcompat/R$attr;->actionBarWidgetTheme:I

    #@6d
    invoke-virtual {v0, v6, v4, v8}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@70
    goto :goto_0
.end method

.method private invalidatePanelMenu(I)V
    .locals 3
    .param p1, "featureId"    # I

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 1152
    iget v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I

    #@3
    shl-int v1, v2, p1

    #@5
    or-int/2addr v0, v1

    #@6
    iput v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I

    #@8
    .line 1154
    iget-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuPosted:Z

    #@a
    if-nez v0, :cond_0

    #@c
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mWindowDecor:Landroid/view/ViewGroup;

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 1155
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mWindowDecor:Landroid/view/ViewGroup;

    #@12
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuRunnable:Ljava/lang/Runnable;

    #@14
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewCompat;->postOnAnimation(Landroid/view/View;Ljava/lang/Runnable;)V

    #@17
    .line 1156
    iput-boolean v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuPosted:Z

    #@19
    .line 1158
    :cond_0
    return-void
.end method

.method private openPanel(ILandroid/view/KeyEvent;)V
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 774
    if-nez p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@4
    if-eqz v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@8
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorContentParent;->canShowOverflowMenu()Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_0

    #@e
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@10
    invoke-static {v0}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@13
    move-result-object v0

    #@14
    invoke-static {v0}, Landroid/support/v4/view/ViewConfigurationCompat;->hasPermanentMenuKey(Landroid/view/ViewConfiguration;)Z

    #@17
    move-result v0

    #@18
    if-nez v0, :cond_0

    #@1a
    .line 777
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@1c
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorContentParent;->showOverflowMenu()Z

    #@1f
    .line 781
    :goto_0
    return-void

    #@20
    .line 779
    :cond_0
    const/4 v0, 0x1

    #@21
    invoke-direct {p0, p1, v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@24
    move-result-object v0

    #@25
    invoke-direct {p0, v0, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->openPanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)V

    #@28
    goto :goto_0
.end method

.method private openPanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)V
    .locals 9
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    const/4 v5, 0x1

    #@2
    .line 785
    iget-boolean v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@4
    if-nez v7, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@9
    move-result v7

    #@a
    if-eqz v7, :cond_1

    #@c
    .line 827
    :cond_0
    :goto_0
    return-void

    #@d
    .line 791
    :cond_1
    iget v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@f
    if-nez v7, :cond_2

    #@11
    .line 792
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@13
    .line 793
    .local v2, "context":Landroid/content/Context;
    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@16
    move-result-object v7

    #@17
    invoke-virtual {v7}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    #@1a
    move-result-object v1

    #@1b
    .line 794
    .local v1, "config":Landroid/content/res/Configuration;
    iget v7, v1, Landroid/content/res/Configuration;->screenLayout:I

    #@1d
    and-int/lit8 v7, v7, 0xf

    #@1f
    const/4 v8, 0x4

    #@20
    if-ne v7, v8, :cond_3

    #@22
    move v4, v5

    #@23
    .line 796
    .local v4, "isXLarge":Z
    :goto_1
    invoke-virtual {v2}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@26
    move-result-object v7

    #@27
    iget v7, v7, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@29
    const/16 v8, 0xb

    #@2b
    if-lt v7, v8, :cond_4

    #@2d
    move v3, v5

    #@2e
    .line 799
    .local v3, "isHoneycombApp":Z
    :goto_2
    if-eqz v4, :cond_2

    #@30
    if-nez v3, :cond_0

    #@32
    .line 804
    .end local v1    # "config":Landroid/content/res/Configuration;
    .end local v2    # "context":Landroid/content/Context;
    .end local v3    # "isHoneycombApp":Z
    .end local v4    # "isXLarge":Z
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@35
    move-result-object v0

    #@36
    .line 805
    .local v0, "cb":Landroid/support/v7/internal/app/WindowCallback;
    if-eqz v0, :cond_5

    #@38
    iget v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@3a
    iget-object v8, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3c
    invoke-interface {v0, v7, v8}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@3f
    move-result v7

    #@40
    if-nez v7, :cond_5

    #@42
    .line 807
    invoke-direct {p0, p1, v5}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@45
    goto :goto_0

    #@46
    .end local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    .restart local v1    # "config":Landroid/content/res/Configuration;
    .restart local v2    # "context":Landroid/content/Context;
    :cond_3
    move v4, v6

    #@47
    .line 794
    goto :goto_1

    #@48
    .restart local v4    # "isXLarge":Z
    :cond_4
    move v3, v6

    #@49
    .line 796
    goto :goto_2

    #@4a
    .line 812
    .end local v1    # "config":Landroid/content/res/Configuration;
    .end local v2    # "context":Landroid/content/Context;
    .end local v4    # "isXLarge":Z
    .restart local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    :cond_5
    invoke-direct {p0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->preparePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)Z

    #@4d
    move-result v7

    #@4e
    if-eqz v7, :cond_0

    #@50
    .line 816
    iget-object v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->decorView:Landroid/view/ViewGroup;

    #@52
    if-eqz v7, :cond_6

    #@54
    iget-boolean v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshDecorView:Z

    #@56
    if-eqz v7, :cond_7

    #@58
    .line 817
    :cond_6
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->initializePanelDecor(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)V

    #@5b
    .line 821
    :cond_7
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->initializePanelContent(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)Z

    #@5e
    move-result v7

    #@5f
    if-eqz v7, :cond_0

    #@61
    invoke-virtual {p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->hasPanelItems()Z

    #@64
    move-result v7

    #@65
    if-eqz v7, :cond_0

    #@67
    .line 825
    iput-boolean v6, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isHandled:Z

    #@69
    .line 826
    iput-boolean v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@6b
    goto :goto_0
.end method

.method private preparePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)Z
    .locals 8
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    const/4 v4, 0x0

    #@3
    .line 938
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@6
    move-result v2

    #@7
    if-eqz v2, :cond_1

    #@9
    .line 1028
    :cond_0
    :goto_0
    return v4

    #@a
    .line 943
    :cond_1
    iget-boolean v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@c
    if-eqz v2, :cond_2

    #@e
    move v4, v3

    #@f
    .line 944
    goto :goto_0

    #@10
    .line 947
    :cond_2
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@12
    if-eqz v2, :cond_3

    #@14
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@16
    if-eq v2, p1, :cond_3

    #@18
    .line 949
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@1a
    invoke-direct {p0, v2, v4}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@1d
    .line 952
    :cond_3
    iget v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@1f
    if-eqz v2, :cond_4

    #@21
    iget v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@23
    const/16 v5, 0x8

    #@25
    if-ne v2, v5, :cond_a

    #@27
    :cond_4
    move v0, v3

    #@28
    .line 955
    .local v0, "isActionBarMenu":Z
    :goto_1
    if-eqz v0, :cond_5

    #@2a
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@2c
    if-eqz v2, :cond_5

    #@2e
    .line 958
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@30
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->setMenuPrepared()V

    #@33
    .line 962
    :cond_5
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@35
    if-eqz v2, :cond_6

    #@37
    iget-boolean v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshMenuContent:Z

    #@39
    if-eqz v2, :cond_c

    #@3b
    .line 963
    :cond_6
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3d
    if-nez v2, :cond_7

    #@3f
    .line 964
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->initializePanelMenu(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;)Z

    #@42
    move-result v2

    #@43
    if-eqz v2, :cond_0

    #@45
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@47
    if-eqz v2, :cond_0

    #@49
    .line 969
    :cond_7
    if-eqz v0, :cond_9

    #@4b
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@4d
    if-eqz v2, :cond_9

    #@4f
    .line 970
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@51
    if-nez v2, :cond_8

    #@53
    .line 971
    new-instance v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@55
    invoke-direct {v2, p0, v7}, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$1;)V

    #@58
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@5a
    .line 973
    :cond_8
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@5c
    iget-object v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5e
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@60
    invoke-interface {v2, v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@63
    .line 978
    :cond_9
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@65
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@68
    .line 979
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@6b
    move-result-object v2

    #@6c
    iget v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@6e
    iget-object v6, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@70
    invoke-interface {v2, v5, v6}, Landroid/support/v7/internal/app/WindowCallback;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@73
    move-result v2

    #@74
    if-nez v2, :cond_b

    #@76
    .line 981
    invoke-virtual {p1, v7}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->setMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@79
    .line 983
    if-eqz v0, :cond_0

    #@7b
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@7d
    if-eqz v2, :cond_0

    #@7f
    .line 985
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@81
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@83
    invoke-interface {v2, v7, v3}, Landroid/support/v7/internal/widget/DecorContentParent;->setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@86
    goto :goto_0

    #@87
    .end local v0    # "isActionBarMenu":Z
    :cond_a
    move v0, v4

    #@88
    .line 952
    goto :goto_1

    #@89
    .line 991
    .restart local v0    # "isActionBarMenu":Z
    :cond_b
    iput-boolean v4, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshMenuContent:Z

    #@8b
    .line 996
    :cond_c
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@8d
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@90
    .line 1000
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->frozenActionViewState:Landroid/os/Bundle;

    #@92
    if-eqz v2, :cond_d

    #@94
    .line 1001
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@96
    iget-object v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->frozenActionViewState:Landroid/os/Bundle;

    #@98
    invoke-virtual {v2, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->restoreActionViewStates(Landroid/os/Bundle;)V

    #@9b
    .line 1002
    iput-object v7, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->frozenActionViewState:Landroid/os/Bundle;

    #@9d
    .line 1006
    :cond_d
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@a0
    move-result-object v2

    #@a1
    iget-object v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@a3
    invoke-interface {v2, v4, v7, v5}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@a6
    move-result v2

    #@a7
    if-nez v2, :cond_f

    #@a9
    .line 1007
    if-eqz v0, :cond_e

    #@ab
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@ad
    if-eqz v2, :cond_e

    #@af
    .line 1010
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@b1
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMenuPresenterCallback:Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionMenuPresenterCallback;

    #@b3
    invoke-interface {v2, v7, v3}, Landroid/support/v7/internal/widget/DecorContentParent;->setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@b6
    .line 1012
    :cond_e
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@b8
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@bb
    goto/16 :goto_0

    #@bd
    .line 1017
    :cond_f
    if-eqz p2, :cond_10

    #@bf
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getDeviceId()I

    #@c2
    move-result v2

    #@c3
    :goto_2
    invoke-static {v2}, Landroid/view/KeyCharacterMap;->load(I)Landroid/view/KeyCharacterMap;

    #@c6
    move-result-object v1

    #@c7
    .line 1019
    .local v1, "kmap":Landroid/view/KeyCharacterMap;
    invoke-virtual {v1}, Landroid/view/KeyCharacterMap;->getKeyboardType()I

    #@ca
    move-result v2

    #@cb
    if-eq v2, v3, :cond_11

    #@cd
    move v2, v3

    #@ce
    :goto_3
    iput-boolean v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->qwertyMode:Z

    #@d0
    .line 1020
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@d2
    iget-boolean v5, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->qwertyMode:Z

    #@d4
    invoke-virtual {v2, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setQwertyMode(Z)V

    #@d7
    .line 1021
    iget-object v2, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@d9
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@dc
    .line 1024
    iput-boolean v3, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@de
    .line 1025
    iput-boolean v4, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isHandled:Z

    #@e0
    .line 1026
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@e2
    move v4, v3

    #@e3
    .line 1028
    goto/16 :goto_0

    #@e5
    .line 1017
    .end local v1    # "kmap":Landroid/view/KeyCharacterMap;
    :cond_10
    const/4 v2, -0x1

    #@e6
    goto :goto_2

    #@e7
    .restart local v1    # "kmap":Landroid/view/KeyCharacterMap;
    :cond_11
    move v2, v4

    #@e8
    .line 1019
    goto :goto_3
.end method

.method private reopenMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 8
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "toggleMenuMode"    # Z

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    const/16 v6, 0x8

    #@3
    const/4 v5, 0x1

    #@4
    const/4 v4, 0x0

    #@5
    .line 835
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@7
    if-eqz v2, :cond_5

    #@9
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@b
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->canShowOverflowMenu()Z

    #@e
    move-result v2

    #@f
    if-eqz v2, :cond_5

    #@11
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@13
    invoke-static {v2}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@16
    move-result-object v2

    #@17
    invoke-static {v2}, Landroid/support/v4/view/ViewConfigurationCompat;->hasPermanentMenuKey(Landroid/view/ViewConfiguration;)Z

    #@1a
    move-result v2

    #@1b
    if-eqz v2, :cond_0

    #@1d
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@1f
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->isOverflowMenuShowPending()Z

    #@22
    move-result v2

    #@23
    if-eqz v2, :cond_5

    #@25
    .line 839
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@28
    move-result-object v0

    #@29
    .line 841
    .local v0, "cb":Landroid/support/v7/internal/app/WindowCallback;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@2b
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->isOverflowMenuShowing()Z

    #@2e
    move-result v2

    #@2f
    if-eqz v2, :cond_1

    #@31
    if-nez p2, :cond_4

    #@33
    .line 842
    :cond_1
    if-eqz v0, :cond_3

    #@35
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@38
    move-result v2

    #@39
    if-nez v2, :cond_3

    #@3b
    .line 844
    iget-boolean v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuPosted:Z

    #@3d
    if-eqz v2, :cond_2

    #@3f
    iget v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I

    #@41
    and-int/lit8 v2, v2, 0x1

    #@43
    if-eqz v2, :cond_2

    #@45
    .line 846
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mWindowDecor:Landroid/view/ViewGroup;

    #@47
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuRunnable:Ljava/lang/Runnable;

    #@49
    invoke-virtual {v2, v3}, Landroid/view/ViewGroup;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@4c
    .line 847
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuRunnable:Ljava/lang/Runnable;

    #@4e
    invoke-interface {v2}, Ljava/lang/Runnable;->run()V

    #@51
    .line 850
    :cond_2
    invoke-direct {p0, v4, v5}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@54
    move-result-object v1

    #@55
    .line 854
    .local v1, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@57
    if-eqz v2, :cond_3

    #@59
    iget-boolean v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshMenuContent:Z

    #@5b
    if-nez v2, :cond_3

    #@5d
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5f
    invoke-interface {v0, v4, v7, v2}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@62
    move-result v2

    #@63
    if-eqz v2, :cond_3

    #@65
    .line 856
    iget-object v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@67
    invoke-interface {v0, v6, v2}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@6a
    .line 857
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@6c
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->showOverflowMenu()Z

    #@6f
    .line 876
    .end local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    .end local v1    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_3
    :goto_0
    return-void

    #@70
    .line 861
    .restart local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    :cond_4
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@72
    invoke-interface {v2}, Landroid/support/v7/internal/widget/DecorContentParent;->hideOverflowMenu()Z

    #@75
    .line 862
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@78
    move-result v2

    #@79
    if-nez v2, :cond_3

    #@7b
    .line 863
    invoke-direct {p0, v4, v5}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@7e
    move-result-object v1

    #@7f
    .line 864
    .restart local v1    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@81
    iget-object v3, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@83
    invoke-virtual {v2, v6, v3}, Landroid/support/v7/app/ActionBarActivity;->onPanelClosed(ILandroid/view/Menu;)V

    #@86
    goto :goto_0

    #@87
    .line 870
    .end local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    .end local v1    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_5
    invoke-direct {p0, v4, v5}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@8a
    move-result-object v1

    #@8b
    .line 872
    .restart local v1    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    iput-boolean v5, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->refreshDecorView:Z

    #@8d
    .line 873
    invoke-direct {p0, v1, v4}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@90
    .line 875
    invoke-direct {p0, v1, v7}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->openPanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)V

    #@93
    goto :goto_0
.end method

.method private throwFeatureRequestIfSubDecorInstalled()V
    .locals 2

    #@0
    .prologue
    .line 1280
    iget-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecorInstalled:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1281
    new-instance v0, Landroid/util/AndroidRuntimeException;

    #@6
    const-string v1, "supportRequestWindowFeature() must be called before adding content"

    #@8
    invoke-direct {v0, v1}, Landroid/util/AndroidRuntimeException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 1284
    :cond_0
    return-void
.end method

.method private updateStatusGuard(I)I
    .locals 12
    .param p1, "insetTop"    # I

    #@0
    .prologue
    const/4 v11, -0x1

    #@1
    const/4 v7, 0x0

    #@2
    .line 1194
    const/4 v6, 0x0

    #@3
    .line 1196
    .local v6, "showStatusGuard":Z
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    if-eqz v8, :cond_3

    #@7
    .line 1197
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@9
    invoke-virtual {v8}, Landroid/support/v7/internal/widget/ActionBarContextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@c
    move-result-object v8

    #@d
    instance-of v8, v8, Landroid/view/ViewGroup$MarginLayoutParams;

    #@f
    if-eqz v8, :cond_3

    #@11
    .line 1198
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@13
    invoke-virtual {v8}, Landroid/support/v7/internal/widget/ActionBarContextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@16
    move-result-object v3

    #@17
    check-cast v3, Landroid/view/ViewGroup$MarginLayoutParams;

    #@19
    .line 1200
    .local v3, "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    const/4 v4, 0x0

    #@1a
    .line 1202
    .local v4, "mlpChanged":Z
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@1c
    invoke-virtual {v8}, Landroid/support/v7/internal/widget/ActionBarContextView;->isShown()Z

    #@1f
    move-result v8

    #@20
    if-eqz v8, :cond_8

    #@22
    .line 1203
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTempRect1:Landroid/graphics/Rect;

    #@24
    if-nez v8, :cond_0

    #@26
    .line 1204
    new-instance v8, Landroid/graphics/Rect;

    #@28
    invoke-direct {v8}, Landroid/graphics/Rect;-><init>()V

    #@2b
    iput-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTempRect1:Landroid/graphics/Rect;

    #@2d
    .line 1205
    new-instance v8, Landroid/graphics/Rect;

    #@2f
    invoke-direct {v8}, Landroid/graphics/Rect;-><init>()V

    #@32
    iput-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTempRect2:Landroid/graphics/Rect;

    #@34
    .line 1207
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTempRect1:Landroid/graphics/Rect;

    #@36
    .line 1208
    .local v0, "insets":Landroid/graphics/Rect;
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTempRect2:Landroid/graphics/Rect;

    #@38
    .line 1209
    .local v1, "localInsets":Landroid/graphics/Rect;
    invoke-virtual {v0, v7, p1, v7, v7}, Landroid/graphics/Rect;->set(IIII)V

    #@3b
    .line 1211
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@3d
    invoke-static {v8, v0, v1}, Landroid/support/v7/internal/widget/ViewUtils;->computeFitSystemWindows(Landroid/view/View;Landroid/graphics/Rect;Landroid/graphics/Rect;)V

    #@40
    .line 1212
    iget v8, v1, Landroid/graphics/Rect;->top:I

    #@42
    if-nez v8, :cond_5

    #@44
    move v5, p1

    #@45
    .line 1213
    .local v5, "newMargin":I
    :goto_0
    iget v8, v3, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@47
    if-eq v8, v5, :cond_1

    #@49
    .line 1214
    const/4 v4, 0x1

    #@4a
    .line 1215
    iput p1, v3, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@4c
    .line 1217
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@4e
    if-nez v8, :cond_6

    #@50
    .line 1218
    new-instance v8, Landroid/view/View;

    #@52
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@54
    invoke-direct {v8, v9}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    #@57
    iput-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@59
    .line 1219
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@5b
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5d
    invoke-virtual {v9}, Landroid/support/v7/app/ActionBarActivity;->getResources()Landroid/content/res/Resources;

    #@60
    move-result-object v9

    #@61
    sget v10, Landroid/support/v7/appcompat/R$color;->abc_input_method_navigation_guard:I

    #@63
    invoke-virtual {v9, v10}, Landroid/content/res/Resources;->getColor(I)I

    #@66
    move-result v9

    #@67
    invoke-virtual {v8, v9}, Landroid/view/View;->setBackgroundColor(I)V

    #@6a
    .line 1221
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@6c
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@6e
    new-instance v10, Landroid/view/ViewGroup$LayoutParams;

    #@70
    invoke-direct {v10, v11, p1}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@73
    invoke-virtual {v8, v9, v11, v10}, Landroid/view/ViewGroup;->addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V

    #@76
    .line 1235
    :cond_1
    :goto_1
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@78
    if-eqz v8, :cond_7

    #@7a
    const/4 v6, 0x1

    #@7b
    .line 1241
    :goto_2
    iget-boolean v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionMode:Z

    #@7d
    if-nez v8, :cond_2

    #@7f
    if-eqz v6, :cond_2

    #@81
    .line 1242
    const/4 p1, 0x0

    #@82
    .line 1251
    .end local v0    # "insets":Landroid/graphics/Rect;
    .end local v1    # "localInsets":Landroid/graphics/Rect;
    .end local v5    # "newMargin":I
    :cond_2
    :goto_3
    if-eqz v4, :cond_3

    #@84
    .line 1252
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@86
    invoke-virtual {v8, v3}, Landroid/support/v7/internal/widget/ActionBarContextView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@89
    .line 1256
    .end local v3    # "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    .end local v4    # "mlpChanged":Z
    :cond_3
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@8b
    if-eqz v8, :cond_4

    #@8d
    .line 1257
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@8f
    if-eqz v6, :cond_9

    #@91
    :goto_4
    invoke-virtual {v8, v7}, Landroid/view/View;->setVisibility(I)V

    #@94
    .line 1260
    :cond_4
    return p1

    #@95
    .restart local v0    # "insets":Landroid/graphics/Rect;
    .restart local v1    # "localInsets":Landroid/graphics/Rect;
    .restart local v3    # "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    .restart local v4    # "mlpChanged":Z
    :cond_5
    move v5, v7

    #@96
    .line 1212
    goto :goto_0

    #@97
    .line 1225
    .restart local v5    # "newMargin":I
    :cond_6
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@99
    invoke-virtual {v8}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@9c
    move-result-object v2

    #@9d
    .line 1226
    .local v2, "lp":Landroid/view/ViewGroup$LayoutParams;
    iget v8, v2, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@9f
    if-eq v8, p1, :cond_1

    #@a1
    .line 1227
    iput p1, v2, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@a3
    .line 1228
    iget-object v8, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mStatusGuard:Landroid/view/View;

    #@a5
    invoke-virtual {v8, v2}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@a8
    goto :goto_1

    #@a9
    .end local v2    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_7
    move v6, v7

    #@aa
    .line 1235
    goto :goto_2

    #@ab
    .line 1246
    .end local v0    # "insets":Landroid/graphics/Rect;
    .end local v1    # "localInsets":Landroid/graphics/Rect;
    .end local v5    # "newMargin":I
    :cond_8
    iget v8, v3, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@ad
    if-eqz v8, :cond_2

    #@af
    .line 1247
    const/4 v4, 0x1

    #@b0
    .line 1248
    iput v7, v3, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@b2
    goto :goto_3

    #@b3
    .line 1257
    .end local v3    # "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    .end local v4    # "mlpChanged":Z
    :cond_9
    const/16 v7, 0x8

    #@b5
    goto :goto_4
.end method


# virtual methods
.method public addContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;
    .param p2, "lp"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 243
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V

    #@3
    .line 244
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5
    const v2, 0x1020002

    #@8
    invoke-virtual {v1, v2}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/view/ViewGroup;

    #@e
    .line 245
    .local v0, "contentParent":Landroid/view/ViewGroup;
    invoke-virtual {v0, p1, p2}, Landroid/view/ViewGroup;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@11
    .line 246
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@13
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->onSupportContentChanged()V

    #@16
    .line 247
    return-void
.end method

.method public createSupportActionBar()Landroid/support/v7/app/ActionBar;
    .locals 3

    #@0
    .prologue
    .line 155
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V

    #@3
    .line 156
    new-instance v0, Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@5
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@7
    iget-boolean v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionBar:Z

    #@9
    invoke-direct {v0, v1, v2}, Landroid/support/v7/internal/app/WindowDecorActionBar;-><init>(Landroid/support/v7/app/ActionBarActivity;Z)V

    #@c
    .line 157
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    iget-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mEnableDefaultActionBarUp:Z

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBar;->setDefaultDisplayHomeAsUpEnabled(Z)V

    #@11
    .line 158
    return-object v0
.end method

.method createView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    .locals 2
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "context"    # Landroid/content/Context;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p3, "attrs"    # Landroid/util/AttributeSet;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param

    #@0
    .prologue
    .line 754
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-ge v0, v1, :cond_1

    #@6
    .line 757
    const/4 v0, -0x1

    #@7
    invoke-virtual {p1}, Ljava/lang/String;->hashCode()I

    #@a
    move-result v1

    #@b
    sparse-switch v1, :sswitch_data_0

    #@e
    :cond_0
    :goto_0
    packed-switch v0, :pswitch_data_0

    #@11
    .line 770
    :cond_1
    const/4 v0, 0x0

    #@12
    :goto_1
    return-object v0

    #@13
    .line 757
    :sswitch_0
    const-string v1, "EditText"

    #@15
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@18
    move-result v1

    #@19
    if-eqz v1, :cond_0

    #@1b
    const/4 v0, 0x0

    #@1c
    goto :goto_0

    #@1d
    :sswitch_1
    const-string v1, "Spinner"

    #@1f
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@22
    move-result v1

    #@23
    if-eqz v1, :cond_0

    #@25
    const/4 v0, 0x1

    #@26
    goto :goto_0

    #@27
    :sswitch_2
    const-string v1, "CheckBox"

    #@29
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@2c
    move-result v1

    #@2d
    if-eqz v1, :cond_0

    #@2f
    const/4 v0, 0x2

    #@30
    goto :goto_0

    #@31
    :sswitch_3
    const-string v1, "RadioButton"

    #@33
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@36
    move-result v1

    #@37
    if-eqz v1, :cond_0

    #@39
    const/4 v0, 0x3

    #@3a
    goto :goto_0

    #@3b
    :sswitch_4
    const-string v1, "CheckedTextView"

    #@3d
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@40
    move-result v1

    #@41
    if-eqz v1, :cond_0

    #@43
    const/4 v0, 0x4

    #@44
    goto :goto_0

    #@45
    .line 759
    :pswitch_0
    new-instance v0, Landroid/support/v7/internal/widget/TintEditText;

    #@47
    invoke-direct {v0, p2, p3}, Landroid/support/v7/internal/widget/TintEditText;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4a
    goto :goto_1

    #@4b
    .line 761
    :pswitch_1
    new-instance v0, Landroid/support/v7/internal/widget/TintSpinner;

    #@4d
    invoke-direct {v0, p2, p3}, Landroid/support/v7/internal/widget/TintSpinner;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@50
    goto :goto_1

    #@51
    .line 763
    :pswitch_2
    new-instance v0, Landroid/support/v7/internal/widget/TintCheckBox;

    #@53
    invoke-direct {v0, p2, p3}, Landroid/support/v7/internal/widget/TintCheckBox;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@56
    goto :goto_1

    #@57
    .line 765
    :pswitch_3
    new-instance v0, Landroid/support/v7/internal/widget/TintRadioButton;

    #@59
    invoke-direct {v0, p2, p3}, Landroid/support/v7/internal/widget/TintRadioButton;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@5c
    goto :goto_1

    #@5d
    .line 767
    :pswitch_4
    new-instance v0, Landroid/support/v7/internal/widget/TintCheckedTextView;

    #@5f
    invoke-direct {v0, p2, p3}, Landroid/support/v7/internal/widget/TintCheckedTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@62
    goto :goto_1

    #@63
    .line 757
    nop

    #@64
    :sswitch_data_0
    .sparse-switch
        -0x56c015e7 -> :sswitch_4
        -0x1440b607 -> :sswitch_1
        0x2e46a6ed -> :sswitch_3
        0x5f7507c3 -> :sswitch_2
        0x63577677 -> :sswitch_0
    .end sparse-switch

    #@7a
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
    .end packed-switch
.end method

.method final ensureSubDecor()V
    .locals 11

    #@0
    .prologue
    const v10, 0x1020002

    #@3
    const/4 v9, 0x1

    #@4
    const/4 v8, 0x0

    #@5
    const/4 v7, 0x0

    #@6
    .line 255
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecorInstalled:Z

    #@8
    if-nez v5, :cond_6

    #@a
    .line 256
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mHasActionBar:Z

    #@c
    if-eqz v5, :cond_8

    #@e
    .line 262
    new-instance v2, Landroid/util/TypedValue;

    #@10
    invoke-direct {v2}, Landroid/util/TypedValue;-><init>()V

    #@13
    .line 263
    .local v2, "outValue":Landroid/util/TypedValue;
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@15
    invoke-virtual {v5}, Landroid/support/v7/app/ActionBarActivity;->getTheme()Landroid/content/res/Resources$Theme;

    #@18
    move-result-object v5

    #@19
    sget v6, Landroid/support/v7/appcompat/R$attr;->actionBarTheme:I

    #@1b
    invoke-virtual {v5, v6, v2, v9}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@1e
    .line 266
    iget v5, v2, Landroid/util/TypedValue;->resourceId:I

    #@20
    if-eqz v5, :cond_7

    #@22
    .line 267
    new-instance v4, Landroid/view/ContextThemeWrapper;

    #@24
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@26
    iget v6, v2, Landroid/util/TypedValue;->resourceId:I

    #@28
    invoke-direct {v4, v5, v6}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@2b
    .line 273
    .local v4, "themedContext":Landroid/content/Context;
    :goto_0
    invoke-static {v4}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@2e
    move-result-object v5

    #@2f
    sget v6, Landroid/support/v7/appcompat/R$layout;->abc_screen_toolbar:I

    #@31
    invoke-virtual {v5, v6, v7}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@34
    move-result-object v5

    #@35
    check-cast v5, Landroid/view/ViewGroup;

    #@37
    iput-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@39
    .line 276
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@3b
    sget v6, Landroid/support/v7/appcompat/R$id;->decor_content_parent:I

    #@3d
    invoke-virtual {v5, v6}, Landroid/view/ViewGroup;->findViewById(I)Landroid/view/View;

    #@40
    move-result-object v5

    #@41
    check-cast v5, Landroid/support/v7/internal/widget/DecorContentParent;

    #@43
    iput-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@45
    .line 278
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@47
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@4a
    move-result-object v6

    #@4b
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V

    #@4e
    .line 283
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionBar:Z

    #@50
    if-eqz v5, :cond_0

    #@52
    .line 284
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@54
    const/16 v6, 0x9

    #@56
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->initFeature(I)V

    #@59
    .line 286
    :cond_0
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mFeatureProgress:Z

    #@5b
    if-eqz v5, :cond_1

    #@5d
    .line 287
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@5f
    const/4 v6, 0x2

    #@60
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->initFeature(I)V

    #@63
    .line 289
    :cond_1
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mFeatureIndeterminateProgress:Z

    #@65
    if-eqz v5, :cond_2

    #@67
    .line 290
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@69
    const/4 v6, 0x5

    #@6a
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->initFeature(I)V

    #@6d
    .line 336
    .end local v2    # "outValue":Landroid/util/TypedValue;
    .end local v4    # "themedContext":Landroid/content/Context;
    :cond_2
    :goto_1
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@6f
    invoke-static {v5}, Landroid/support/v7/internal/widget/ViewUtils;->makeOptionalFitsSystemWindows(Landroid/view/View;)V

    #@72
    .line 339
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@74
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@76
    invoke-virtual {v5, v6}, Landroid/support/v7/app/ActionBarActivity;->superSetContentView(Landroid/view/View;)V

    #@79
    .line 343
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@7b
    invoke-virtual {v5, v10}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@7e
    move-result-object v1

    #@7f
    .line 344
    .local v1, "decorContent":Landroid/view/View;
    const/4 v5, -0x1

    #@80
    invoke-virtual {v1, v5}, Landroid/view/View;->setId(I)V

    #@83
    .line 345
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@85
    sget v6, Landroid/support/v7/appcompat/R$id;->action_bar_activity_content:I

    #@87
    invoke-virtual {v5, v6}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@8a
    move-result-object v0

    #@8b
    .line 346
    .local v0, "abcContent":Landroid/view/View;
    invoke-virtual {v0, v10}, Landroid/view/View;->setId(I)V

    #@8e
    .line 350
    instance-of v5, v1, Landroid/widget/FrameLayout;

    #@90
    if-eqz v5, :cond_3

    #@92
    .line 351
    check-cast v1, Landroid/widget/FrameLayout;

    #@94
    .end local v1    # "decorContent":Landroid/view/View;
    invoke-virtual {v1, v7}, Landroid/widget/FrameLayout;->setForeground(Landroid/graphics/drawable/Drawable;)V

    #@97
    .line 355
    :cond_3
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTitleToSet:Ljava/lang/CharSequence;

    #@99
    if-eqz v5, :cond_4

    #@9b
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@9d
    if-eqz v5, :cond_4

    #@9f
    .line 356
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@a1
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTitleToSet:Ljava/lang/CharSequence;

    #@a3
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/DecorContentParent;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@a6
    .line 357
    iput-object v7, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTitleToSet:Ljava/lang/CharSequence;

    #@a8
    .line 360
    :cond_4
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->applyFixedSizeWindow()V

    #@ab
    .line 362
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->onSubDecorInstalled()V

    #@ae
    .line 364
    iput-boolean v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecorInstalled:Z

    #@b0
    .line 371
    invoke-direct {p0, v8, v8}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@b3
    move-result-object v3

    #@b4
    .line 372
    .local v3, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@b7
    move-result v5

    #@b8
    if-nez v5, :cond_6

    #@ba
    if-eqz v3, :cond_5

    #@bc
    iget-object v5, v3, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@be
    if-nez v5, :cond_6

    #@c0
    .line 373
    :cond_5
    const/16 v5, 0x8

    #@c2
    invoke-direct {p0, v5}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->invalidatePanelMenu(I)V

    #@c5
    .line 376
    .end local v0    # "abcContent":Landroid/view/View;
    .end local v3    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_6
    return-void

    #@c6
    .line 269
    .restart local v2    # "outValue":Landroid/util/TypedValue;
    :cond_7
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@c8
    .restart local v4    # "themedContext":Landroid/content/Context;
    goto/16 :goto_0

    #@ca
    .line 293
    .end local v2    # "outValue":Landroid/util/TypedValue;
    .end local v4    # "themedContext":Landroid/content/Context;
    :cond_8
    iget-boolean v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionMode:Z

    #@cc
    if-eqz v5, :cond_9

    #@ce
    .line 294
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@d0
    invoke-static {v5}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@d3
    move-result-object v5

    #@d4
    sget v6, Landroid/support/v7/appcompat/R$layout;->abc_screen_simple_overlay_action_mode:I

    #@d6
    invoke-virtual {v5, v6, v7}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@d9
    move-result-object v5

    #@da
    check-cast v5, Landroid/view/ViewGroup;

    #@dc
    iput-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@de
    .line 301
    :goto_2
    sget v5, Landroid/os/Build$VERSION;->SDK_INT:I

    #@e0
    const/16 v6, 0x15

    #@e2
    if-lt v5, v6, :cond_a

    #@e4
    .line 304
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@e6
    new-instance v6, Landroid/support/v7/app/ActionBarActivityDelegateBase$2;

    #@e8
    invoke-direct {v6, p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase$2;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V

    #@eb
    invoke-static {v5, v6}, Landroid/support/v4/view/ViewCompat;->setOnApplyWindowInsetsListener(Landroid/view/View;Landroid/support/v4/view/OnApplyWindowInsetsListener;)V

    #@ee
    goto/16 :goto_1

    #@f0
    .line 297
    :cond_9
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@f2
    invoke-static {v5}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@f5
    move-result-object v5

    #@f6
    sget v6, Landroid/support/v7/appcompat/R$layout;->abc_screen_simple:I

    #@f8
    invoke-virtual {v5, v6, v7}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@fb
    move-result-object v5

    #@fc
    check-cast v5, Landroid/view/ViewGroup;

    #@fe
    iput-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@100
    goto :goto_2

    #@101
    .line 325
    :cond_a
    iget-object v5, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecor:Landroid/view/ViewGroup;

    #@103
    check-cast v5, Landroid/support/v7/internal/widget/FitWindowsViewGroup;

    #@105
    new-instance v6, Landroid/support/v7/app/ActionBarActivityDelegateBase$3;

    #@107
    invoke-direct {v6, p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase$3;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V

    #@10a
    invoke-interface {v5, v6}, Landroid/support/v7/internal/widget/FitWindowsViewGroup;->setOnFitSystemWindowsListener(Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;)V

    #@10d
    goto/16 :goto_1
.end method

.method getHomeAsUpIndicatorAttrId()I
    .locals 1

    #@0
    .prologue
    .line 711
    sget v0, Landroid/support/v7/appcompat/R$attr;->homeAsUpIndicator:I

    #@2
    return v0
.end method

.method public onBackPressed()Z
    .locals 3

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 675
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@3
    if-eqz v2, :cond_1

    #@5
    .line 676
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@7
    invoke-virtual {v2}, Landroid/support/v7/view/ActionMode;->finish()V

    #@a
    .line 686
    :cond_0
    :goto_0
    return v1

    #@b
    .line 681
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@e
    move-result-object v0

    #@f
    .line 682
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_2

    #@11
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBar;->collapseActionView()Z

    #@14
    move-result v2

    #@15
    if-nez v2, :cond_0

    #@17
    .line 686
    :cond_2
    const/4 v1, 0x0

    #@18
    goto :goto_0
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 2
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 188
    iget-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mHasActionBar:Z

    #@2
    if-eqz v1, :cond_0

    #@4
    iget-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mSubDecorInstalled:Z

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 191
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@b
    move-result-object v0

    #@c
    .line 192
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@e
    .line 193
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBar;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@11
    .line 196
    .end local v0    # "ab":Landroid/support/v7/app/ActionBar;
    :cond_0
    return-void
.end method

.method public onContentChanged()V
    .locals 0

    #@0
    .prologue
    .line 252
    return-void
.end method

.method onCreate(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 138
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->onCreate(Landroid/os/Bundle;)V

    #@4
    .line 140
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@6
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@9
    move-result-object v1

    #@a
    invoke-virtual {v1}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@d
    move-result-object v1

    #@e
    check-cast v1, Landroid/view/ViewGroup;

    #@10
    iput-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mWindowDecor:Landroid/view/ViewGroup;

    #@12
    .line 142
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@14
    invoke-static {v1}, Landroid/support/v4/app/NavUtils;->getParentActivityName(Landroid/app/Activity;)Ljava/lang/String;

    #@17
    move-result-object v1

    #@18
    if-eqz v1, :cond_0

    #@1a
    .line 144
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->peekSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@1d
    move-result-object v0

    #@1e
    .line 145
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-nez v0, :cond_1

    #@20
    .line 146
    iput-boolean v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mEnableDefaultActionBarUp:Z

    #@22
    .line 151
    .end local v0    # "ab":Landroid/support/v7/app/ActionBar;
    :cond_0
    :goto_0
    return-void

    #@23
    .line 148
    .restart local v0    # "ab":Landroid/support/v7/app/ActionBar;
    :cond_1
    invoke-virtual {v0, v2}, Landroid/support/v7/app/ActionBar;->setDefaultDisplayHomeAsUpEnabled(Z)V

    #@26
    goto :goto_0
.end method

.method public onCreatePanelMenu(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 503
    if-eqz p1, :cond_0

    #@2
    .line 504
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@5
    move-result-object v0

    #@6
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/app/WindowCallback;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@9
    move-result v0

    #@a
    .line 506
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public onCreatePanelView(I)Landroid/view/View;
    .locals 4
    .param p1, "featureId"    # I

    #@0
    .prologue
    .line 476
    const/4 v1, 0x0

    #@1
    .line 479
    .local v1, "panelView":Landroid/view/View;
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@3
    if-nez v3, :cond_1

    #@5
    .line 481
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@8
    move-result-object v0

    #@9
    .line 482
    .local v0, "callback":Landroid/support/v7/internal/app/WindowCallback;
    if-eqz v0, :cond_0

    #@b
    .line 483
    invoke-interface {v0, p1}, Landroid/support/v7/internal/app/WindowCallback;->onCreatePanelView(I)Landroid/view/View;

    #@e
    move-result-object v1

    #@f
    .line 486
    :cond_0
    if-nez v1, :cond_1

    #@11
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mToolbarListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@13
    if-nez v3, :cond_1

    #@15
    .line 491
    const/4 v3, 0x1

    #@16
    invoke-direct {p0, p1, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@19
    move-result-object v2

    #@1a
    .line 492
    .local v2, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    const/4 v3, 0x0

    #@1b
    invoke-direct {p0, v2, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->openPanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)V

    #@1e
    .line 493
    iget-boolean v3, v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isOpen:Z

    #@20
    if-eqz v3, :cond_1

    #@22
    .line 494
    iget-object v1, v2, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->shownPanelView:Landroid/view/View;

    #@24
    .line 498
    .end local v0    # "callback":Landroid/support/v7/internal/app/WindowCallback;
    .end local v2    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_1
    return-object v1
.end method

.method onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 749
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->onKeyShortcut(ILandroid/view/KeyEvent;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method onKeyShortcut(ILandroid/view/KeyEvent;)Z
    .locals 6
    .param p1, "keyCode"    # I
    .param p2, "ev"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    const/4 v2, 0x1

    #@2
    .line 718
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@4
    if-eqz v4, :cond_1

    #@6
    .line 719
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@8
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getKeyCode()I

    #@b
    move-result v5

    #@c
    invoke-virtual {p0, v4, v5, p2, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->performPanelShortcut(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;ILandroid/view/KeyEvent;I)Z

    #@f
    move-result v0

    #@10
    .line 721
    .local v0, "handled":Z
    if-eqz v0, :cond_1

    #@12
    .line 722
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@14
    if-eqz v3, :cond_0

    #@16
    .line 723
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@18
    iput-boolean v2, v3, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isHandled:Z

    #@1a
    .line 742
    .end local v0    # "handled":Z
    :cond_0
    :goto_0
    return v2

    #@1b
    .line 733
    :cond_1
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mPreparedPanel:Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@1d
    if-nez v4, :cond_2

    #@1f
    .line 734
    invoke-direct {p0, v3, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@22
    move-result-object v1

    #@23
    .line 735
    .local v1, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    invoke-direct {p0, v1, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->preparePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)Z

    #@26
    .line 736
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getKeyCode()I

    #@29
    move-result v4

    #@2a
    invoke-virtual {p0, v1, v4, p2, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->performPanelShortcut(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;ILandroid/view/KeyEvent;I)Z

    #@2d
    move-result v0

    #@2e
    .line 737
    .restart local v0    # "handled":Z
    iput-boolean v3, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@30
    .line 738
    if-nez v0, :cond_0

    #@32
    .end local v0    # "handled":Z
    .end local v1    # "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_2
    move v2, v3

    #@33
    .line 742
    goto :goto_0
.end method

.method public onMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 3
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 552
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@3
    move-result-object v0

    #@4
    .line 553
    .local v0, "cb":Landroid/support/v7/internal/app/WindowCallback;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@9
    move-result v2

    #@a
    if-nez v2, :cond_0

    #@c
    .line 554
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getRootMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@f
    move-result-object v2

    #@10
    invoke-direct {p0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->findMenuPanel(Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@13
    move-result-object v1

    #@14
    .line 555
    .local v1, "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v1, :cond_0

    #@16
    .line 556
    iget v2, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@18
    invoke-interface {v0, v2, p2}, Landroid/support/v7/internal/app/WindowCallback;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    #@1b
    move-result v2

    #@1c
    .line 559
    .end local v1    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :goto_0
    return v2

    #@1d
    :cond_0
    const/4 v2, 0x0

    #@1e
    goto :goto_0
.end method

.method public onMenuModeChange(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 564
    const/4 v0, 0x1

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->reopenMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@4
    .line 565
    return-void
.end method

.method onMenuOpened(ILandroid/view/Menu;)Z
    .locals 3
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 539
    const/16 v2, 0x8

    #@3
    if-ne p1, v2, :cond_1

    #@5
    .line 540
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@8
    move-result-object v0

    #@9
    .line 541
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@b
    .line 542
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBar;->dispatchMenuVisibilityChanged(Z)V

    #@e
    .line 546
    .end local v0    # "ab":Landroid/support/v7/app/ActionBar;
    :cond_0
    :goto_0
    return v1

    #@f
    :cond_1
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@11
    invoke-virtual {v1, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->superOnMenuOpened(ILandroid/view/Menu;)Z

    #@14
    move-result v1

    #@15
    goto :goto_0
.end method

.method public onPanelClosed(ILandroid/view/Menu;)V
    .locals 4
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 519
    invoke-direct {p0, p1, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getPanelState(IZ)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@4
    move-result-object v1

    #@5
    .line 520
    .local v1, "st":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v1, :cond_0

    #@7
    .line 522
    invoke-direct {p0, v1, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@a
    .line 525
    :cond_0
    const/16 v2, 0x8

    #@c
    if-ne p1, v2, :cond_2

    #@e
    .line 526
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@11
    move-result-object v0

    #@12
    .line 527
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_1

    #@14
    .line 528
    invoke-virtual {v0, v3}, Landroid/support/v7/app/ActionBar;->dispatchMenuVisibilityChanged(Z)V

    #@17
    .line 535
    .end local v0    # "ab":Landroid/support/v7/app/ActionBar;
    :cond_1
    :goto_0
    return-void

    #@18
    .line 530
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@1b
    move-result v2

    #@1c
    if-nez v2, :cond_1

    #@1e
    .line 533
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@20
    invoke-virtual {v2, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->superOnPanelClosed(ILandroid/view/Menu;)V

    #@23
    goto :goto_0
.end method

.method public onPostResume()V
    .locals 2

    #@0
    .prologue
    .line 208
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@3
    move-result-object v0

    #@4
    .line 209
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@6
    .line 210
    const/4 v1, 0x1

    #@7
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBar;->setShowHideAnimationEnabled(Z)V

    #@a
    .line 212
    :cond_0
    return-void
.end method

.method public onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 511
    if-eqz p1, :cond_0

    #@2
    .line 512
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@5
    move-result-object v0

    #@6
    invoke-interface {v0, p1, p2, p3}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@9
    move-result v0

    #@a
    .line 514
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public onStop()V
    .locals 2

    #@0
    .prologue
    .line 200
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@3
    move-result-object v0

    #@4
    .line 201
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@6
    .line 202
    const/4 v1, 0x0

    #@7
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBar;->setShowHideAnimationEnabled(Z)V

    #@a
    .line 204
    :cond_0
    return-void
.end method

.method onSubDecorInstalled()V
    .locals 0

    #@0
    .prologue
    .line 378
    return-void
.end method

.method public onTitleChanged(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 465
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 466
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorContentParent;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@9
    .line 472
    :goto_0
    return-void

    #@a
    .line 467
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@d
    move-result-object v0

    #@e
    if-eqz v0, :cond_1

    #@10
    .line 468
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@13
    move-result-object v0

    #@14
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBar;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@17
    goto :goto_0

    #@18
    .line 470
    :cond_1
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mTitleToSet:Ljava/lang/CharSequence;

    #@1a
    goto :goto_0
.end method

.method final performPanelShortcut(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;ILandroid/view/KeyEvent;I)Z
    .locals 2
    .param p1, "st"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .param p2, "keyCode"    # I
    .param p3, "event"    # Landroid/view/KeyEvent;
    .param p4, "flags"    # I

    #@0
    .prologue
    .line 1128
    invoke-virtual {p3}, Landroid/view/KeyEvent;->isSystem()Z

    #@3
    move-result v1

    #@4
    if-eqz v1, :cond_1

    #@6
    .line 1129
    const/4 v0, 0x0

    #@7
    .line 1148
    :cond_0
    :goto_0
    return v0

    #@8
    .line 1132
    :cond_1
    const/4 v0, 0x0

    #@9
    .line 1136
    .local v0, "handled":Z
    iget-boolean v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->isPrepared:Z

    #@b
    if-nez v1, :cond_2

    #@d
    invoke-direct {p0, p1, p3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->preparePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/KeyEvent;)Z

    #@10
    move-result v1

    #@11
    if-eqz v1, :cond_3

    #@13
    :cond_2
    iget-object v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@15
    if-eqz v1, :cond_3

    #@17
    .line 1138
    iget-object v1, p1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->menu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@19
    invoke-virtual {v1, p2, p3, p4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performShortcut(ILandroid/view/KeyEvent;I)Z

    #@1c
    move-result v0

    #@1d
    .line 1141
    :cond_3
    if-eqz v0, :cond_0

    #@1f
    .line 1143
    and-int/lit8 v1, p4, 0x1

    #@21
    if-nez v1, :cond_0

    #@23
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDecorContentParent:Landroid/support/v7/internal/widget/DecorContentParent;

    #@25
    if-nez v1, :cond_0

    #@27
    .line 1144
    const/4 v1, 0x1

    #@28
    invoke-direct {p0, p1, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@2b
    goto :goto_0
.end method

.method public setContentView(I)V
    .locals 3
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 225
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V

    #@3
    .line 226
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5
    const v2, 0x1020002

    #@8
    invoke-virtual {v1, v2}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/view/ViewGroup;

    #@e
    .line 227
    .local v0, "contentParent":Landroid/view/ViewGroup;
    invoke-virtual {v0}, Landroid/view/ViewGroup;->removeAllViews()V

    #@11
    .line 228
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@13
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->getLayoutInflater()Landroid/view/LayoutInflater;

    #@16
    move-result-object v1

    #@17
    invoke-virtual {v1, p1, v0}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@1a
    .line 229
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@1c
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->onSupportContentChanged()V

    #@1f
    .line 230
    return-void
.end method

.method public setContentView(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 216
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V

    #@3
    .line 217
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5
    const v2, 0x1020002

    #@8
    invoke-virtual {v1, v2}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/view/ViewGroup;

    #@e
    .line 218
    .local v0, "contentParent":Landroid/view/ViewGroup;
    invoke-virtual {v0}, Landroid/view/ViewGroup;->removeAllViews()V

    #@11
    .line 219
    invoke-virtual {v0, p1}, Landroid/view/ViewGroup;->addView(Landroid/view/View;)V

    #@14
    .line 220
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@16
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->onSupportContentChanged()V

    #@19
    .line 221
    return-void
.end method

.method public setContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;
    .param p2, "lp"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 234
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V

    #@3
    .line 235
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@5
    const v2, 0x1020002

    #@8
    invoke-virtual {v1, v2}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/view/ViewGroup;

    #@e
    .line 236
    .local v0, "contentParent":Landroid/view/ViewGroup;
    invoke-virtual {v0}, Landroid/view/ViewGroup;->removeAllViews()V

    #@11
    .line 237
    invoke-virtual {v0, p1, p2}, Landroid/view/ViewGroup;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@14
    .line 238
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@16
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivity;->onSupportContentChanged()V

    #@19
    .line 239
    return-void
.end method

.method setSupportActionBar(Landroid/support/v7/widget/Toolbar;)V
    .locals 5
    .param p1, "toolbar"    # Landroid/support/v7/widget/Toolbar;

    #@0
    .prologue
    .line 163
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@3
    move-result-object v0

    #@4
    .line 164
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    instance-of v2, v0, Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@6
    if-eqz v2, :cond_0

    #@8
    .line 165
    new-instance v2, Ljava/lang/IllegalStateException;

    #@a
    const-string v3, "This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead."

    #@c
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@f
    throw v2

    #@10
    .line 168
    :cond_0
    instance-of v2, v0, Landroid/support/v7/internal/app/ToolbarActionBar;

    #@12
    if-eqz v2, :cond_1

    #@14
    .line 170
    check-cast v0, Landroid/support/v7/internal/app/ToolbarActionBar;

    #@16
    .end local v0    # "ab":Landroid/support/v7/app/ActionBar;
    const/4 v2, 0x0

    #@17
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/app/ToolbarActionBar;->setListMenuPresenter(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)V

    #@1a
    .line 175
    :cond_1
    new-instance v1, Landroid/support/v7/internal/app/ToolbarActionBar;

    #@1c
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@1e
    invoke-virtual {v2}, Landroid/support/v7/app/ActionBarActivity;->getTitle()Ljava/lang/CharSequence;

    #@21
    move-result-object v2

    #@22
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@24
    invoke-virtual {v3}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@27
    move-result-object v3

    #@28
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mDefaultWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2a
    invoke-direct {v1, p1, v2, v3, v4}, Landroid/support/v7/internal/app/ToolbarActionBar;-><init>(Landroid/support/v7/widget/Toolbar;Ljava/lang/CharSequence;Landroid/view/Window;Landroid/support/v7/internal/app/WindowCallback;)V

    #@2d
    .line 177
    .local v1, "tbab":Landroid/support/v7/internal/app/ToolbarActionBar;
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureToolbarListMenuPresenter()V

    #@30
    .line 178
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mToolbarListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@32
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/app/ToolbarActionBar;->setListMenuPresenter(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)V

    #@35
    .line 179
    invoke-virtual {p0, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->setSupportActionBar(Landroid/support/v7/app/ActionBar;)V

    #@38
    .line 180
    invoke-virtual {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->getWrappedWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@3b
    move-result-object v2

    #@3c
    invoke-virtual {p0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V

    #@3f
    .line 181
    invoke-virtual {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->invalidateOptionsMenu()Z

    #@42
    .line 182
    return-void
.end method

.method setSupportProgress(I)V
    .locals 0
    .param p1, "progress"    # I

    #@0
    .prologue
    .line 707
    return-void
.end method

.method setSupportProgressBarIndeterminate(Z)V
    .locals 0
    .param p1, "indeterminate"    # Z

    #@0
    .prologue
    .line 702
    return-void
.end method

.method setSupportProgressBarIndeterminateVisibility(Z)V
    .locals 0
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 697
    return-void
.end method

.method setSupportProgressBarVisibility(Z)V
    .locals 0
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 692
    return-void
.end method

.method public startSupportActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 4
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 569
    if-nez p1, :cond_0

    #@2
    .line 570
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@4
    const-string v3, "ActionMode callback can not be null."

    #@6
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@9
    throw v2

    #@a
    .line 573
    :cond_0
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@c
    if-eqz v2, :cond_1

    #@e
    .line 574
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@10
    invoke-virtual {v2}, Landroid/support/v7/view/ActionMode;->finish()V

    #@13
    .line 577
    :cond_1
    new-instance v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;

    #@15
    invoke-direct {v1, p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/view/ActionMode$Callback;)V

    #@18
    .line 579
    .local v1, "wrappedCallback":Landroid/support/v7/view/ActionMode$Callback;
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@1b
    move-result-object v0

    #@1c
    .line 580
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_2

    #@1e
    .line 581
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBar;->startActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@21
    move-result-object v2

    #@22
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@24
    .line 582
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@26
    if-eqz v2, :cond_2

    #@28
    .line 583
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@2a
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@2c
    invoke-virtual {v2, v3}, Landroid/support/v7/app/ActionBarActivity;->onSupportActionModeStarted(Landroid/support/v7/view/ActionMode;)V

    #@2f
    .line 587
    :cond_2
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@31
    if-nez v2, :cond_3

    #@33
    .line 589
    invoke-virtual {p0, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->startSupportActionModeFromWindow(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@36
    move-result-object v2

    #@37
    iput-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@39
    .line 592
    :cond_3
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@3b
    return-object v2
.end method

.method startSupportActionModeFromWindow(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 11
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    const/4 v10, 0x0

    #@1
    const/4 v7, 0x1

    #@2
    const/4 v8, 0x0

    #@3
    .line 605
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@5
    if-eqz v6, :cond_0

    #@7
    .line 606
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@9
    invoke-virtual {v6}, Landroid/support/v7/view/ActionMode;->finish()V

    #@c
    .line 609
    :cond_0
    new-instance v5, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;

    #@e
    invoke-direct {v5, p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/view/ActionMode$Callback;)V

    #@11
    .line 610
    .local v5, "wrappedCallback":Landroid/support/v7/view/ActionMode$Callback;
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getActionBarThemedContext()Landroid/content/Context;

    #@14
    move-result-object v0

    #@15
    .line 612
    .local v0, "context":Landroid/content/Context;
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@17
    if-nez v6, :cond_1

    #@19
    .line 613
    iget-boolean v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mIsFloating:Z

    #@1b
    if-eqz v6, :cond_5

    #@1d
    .line 614
    new-instance v6, Landroid/support/v7/internal/widget/ActionBarContextView;

    #@1f
    invoke-direct {v6, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;-><init>(Landroid/content/Context;)V

    #@22
    iput-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@24
    .line 615
    new-instance v6, Landroid/widget/PopupWindow;

    #@26
    sget v9, Landroid/support/v7/appcompat/R$attr;->actionModePopupWindowStyle:I

    #@28
    invoke-direct {v6, v0, v10, v9}, Landroid/widget/PopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@2b
    iput-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@2d
    .line 617
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@2f
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@31
    invoke-virtual {v6, v9}, Landroid/widget/PopupWindow;->setContentView(Landroid/view/View;)V

    #@34
    .line 618
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@36
    const/4 v9, -0x1

    #@37
    invoke-virtual {v6, v9}, Landroid/widget/PopupWindow;->setWidth(I)V

    #@3a
    .line 620
    new-instance v2, Landroid/util/TypedValue;

    #@3c
    invoke-direct {v2}, Landroid/util/TypedValue;-><init>()V

    #@3f
    .line 621
    .local v2, "heightValue":Landroid/util/TypedValue;
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@41
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBarActivity;->getTheme()Landroid/content/res/Resources$Theme;

    #@44
    move-result-object v6

    #@45
    sget v9, Landroid/support/v7/appcompat/R$attr;->actionBarSize:I

    #@47
    invoke-virtual {v6, v9, v2, v7}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@4a
    .line 622
    iget v6, v2, Landroid/util/TypedValue;->data:I

    #@4c
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4e
    invoke-virtual {v9}, Landroid/support/v7/app/ActionBarActivity;->getResources()Landroid/content/res/Resources;

    #@51
    move-result-object v9

    #@52
    invoke-virtual {v9}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@55
    move-result-object v9

    #@56
    invoke-static {v6, v9}, Landroid/util/TypedValue;->complexToDimensionPixelSize(ILandroid/util/DisplayMetrics;)I

    #@59
    move-result v1

    #@5a
    .line 624
    .local v1, "height":I
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5c
    invoke-virtual {v6, v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setContentHeight(I)V

    #@5f
    .line 625
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@61
    const/4 v9, -0x2

    #@62
    invoke-virtual {v6, v9}, Landroid/widget/PopupWindow;->setHeight(I)V

    #@65
    .line 626
    new-instance v6, Landroid/support/v7/app/ActionBarActivityDelegateBase$4;

    #@67
    invoke-direct {v6, p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase$4;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V

    #@6a
    iput-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mShowActionModePopup:Ljava/lang/Runnable;

    #@6c
    .line 644
    .end local v1    # "height":I
    .end local v2    # "heightValue":Landroid/util/TypedValue;
    :cond_1
    :goto_0
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@6e
    if-eqz v6, :cond_3

    #@70
    .line 645
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@72
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActionBarContextView;->killMode()V

    #@75
    .line 646
    new-instance v3, Landroid/support/v7/internal/view/StandaloneActionMode;

    #@77
    iget-object v9, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@79
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@7b
    if-nez v6, :cond_6

    #@7d
    move v6, v7

    #@7e
    :goto_1
    invoke-direct {v3, v0, v9, v5, v6}, Landroid/support/v7/internal/view/StandaloneActionMode;-><init>(Landroid/content/Context;Landroid/support/v7/internal/widget/ActionBarContextView;Landroid/support/v7/view/ActionMode$Callback;Z)V

    #@81
    .line 648
    .local v3, "mode":Landroid/support/v7/view/ActionMode;
    invoke-virtual {v3}, Landroid/support/v7/view/ActionMode;->getMenu()Landroid/view/Menu;

    #@84
    move-result-object v6

    #@85
    invoke-interface {p1, v3, v6}, Landroid/support/v7/view/ActionMode$Callback;->onCreateActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z

    #@88
    move-result v6

    #@89
    if-eqz v6, :cond_7

    #@8b
    .line 649
    invoke-virtual {v3}, Landroid/support/v7/view/ActionMode;->invalidate()V

    #@8e
    .line 650
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@90
    invoke-virtual {v6, v3}, Landroid/support/v7/internal/widget/ActionBarContextView;->initForMode(Landroid/support/v7/view/ActionMode;)V

    #@93
    .line 651
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@95
    invoke-virtual {v6, v8}, Landroid/support/v7/internal/widget/ActionBarContextView;->setVisibility(I)V

    #@98
    .line 652
    iput-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@9a
    .line 653
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@9c
    if-eqz v6, :cond_2

    #@9e
    .line 654
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@a0
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@a3
    move-result-object v6

    #@a4
    invoke-virtual {v6}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@a7
    move-result-object v6

    #@a8
    iget-object v7, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mShowActionModePopup:Ljava/lang/Runnable;

    #@aa
    invoke-virtual {v6, v7}, Landroid/view/View;->post(Ljava/lang/Runnable;)Z

    #@ad
    .line 656
    :cond_2
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@af
    const/16 v7, 0x20

    #@b1
    invoke-virtual {v6, v7}, Landroid/support/v7/internal/widget/ActionBarContextView;->sendAccessibilityEvent(I)V

    #@b4
    .line 659
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@b6
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActionBarContextView;->getParent()Landroid/view/ViewParent;

    #@b9
    move-result-object v6

    #@ba
    if-eqz v6, :cond_3

    #@bc
    .line 660
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@be
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActionBarContextView;->getParent()Landroid/view/ViewParent;

    #@c1
    move-result-object v6

    #@c2
    check-cast v6, Landroid/view/View;

    #@c4
    invoke-static {v6}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@c7
    .line 666
    .end local v3    # "mode":Landroid/support/v7/view/ActionMode;
    :cond_3
    :goto_2
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@c9
    if-eqz v6, :cond_4

    #@cb
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@cd
    if-eqz v6, :cond_4

    #@cf
    .line 667
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@d1
    iget-object v7, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@d3
    invoke-virtual {v6, v7}, Landroid/support/v7/app/ActionBarActivity;->onSupportActionModeStarted(Landroid/support/v7/view/ActionMode;)V

    #@d6
    .line 669
    :cond_4
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@d8
    return-object v6

    #@d9
    .line 634
    :cond_5
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@db
    sget v9, Landroid/support/v7/appcompat/R$id;->action_mode_bar_stub:I

    #@dd
    invoke-virtual {v6, v9}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@e0
    move-result-object v4

    #@e1
    check-cast v4, Landroid/support/v7/internal/widget/ViewStubCompat;

    #@e3
    .line 636
    .local v4, "stub":Landroid/support/v7/internal/widget/ViewStubCompat;
    if-eqz v4, :cond_1

    #@e5
    .line 638
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@e8
    move-result-object v6

    #@e9
    invoke-virtual {v4, v6}, Landroid/support/v7/internal/widget/ViewStubCompat;->setLayoutInflater(Landroid/view/LayoutInflater;)V

    #@ec
    .line 639
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/ViewStubCompat;->inflate()Landroid/view/View;

    #@ef
    move-result-object v6

    #@f0
    check-cast v6, Landroid/support/v7/internal/widget/ActionBarContextView;

    #@f2
    iput-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@f4
    goto/16 :goto_0

    #@f6
    .end local v4    # "stub":Landroid/support/v7/internal/widget/ViewStubCompat;
    :cond_6
    move v6, v8

    #@f7
    .line 646
    goto :goto_1

    #@f8
    .line 663
    .restart local v3    # "mode":Landroid/support/v7/view/ActionMode;
    :cond_7
    iput-object v10, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@fa
    goto :goto_2
.end method

.method public supportInvalidateOptionsMenu()V
    .locals 2

    #@0
    .prologue
    .line 597
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@3
    move-result-object v0

    #@4
    .line 598
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBar;->invalidateOptionsMenu()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 601
    :goto_0
    return-void

    #@d
    .line 600
    :cond_0
    const/4 v1, 0x0

    #@e
    invoke-direct {p0, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->invalidatePanelMenu(I)V

    #@11
    goto :goto_0
.end method

.method public supportRequestWindowFeature(I)Z
    .locals 1
    .param p1, "featureId"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 437
    packed-switch p1, :pswitch_data_0

    #@4
    .line 460
    :pswitch_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivity;->requestWindowFeature(I)Z

    #@9
    move-result v0

    #@a
    :goto_0
    return v0

    #@b
    .line 439
    :pswitch_1
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->throwFeatureRequestIfSubDecorInstalled()V

    #@e
    .line 440
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mHasActionBar:Z

    #@10
    goto :goto_0

    #@11
    .line 443
    :pswitch_2
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->throwFeatureRequestIfSubDecorInstalled()V

    #@14
    .line 444
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionBar:Z

    #@16
    goto :goto_0

    #@17
    .line 447
    :pswitch_3
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->throwFeatureRequestIfSubDecorInstalled()V

    #@1a
    .line 448
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mOverlayActionMode:Z

    #@1c
    goto :goto_0

    #@1d
    .line 451
    :pswitch_4
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->throwFeatureRequestIfSubDecorInstalled()V

    #@20
    .line 452
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mFeatureProgress:Z

    #@22
    goto :goto_0

    #@23
    .line 455
    :pswitch_5
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->throwFeatureRequestIfSubDecorInstalled()V

    #@26
    .line 456
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mFeatureIndeterminateProgress:Z

    #@28
    goto :goto_0

    #@29
    .line 437
    nop

    #@2a
    :pswitch_data_0
    .packed-switch 0x2
        :pswitch_4
        :pswitch_0
        :pswitch_0
        :pswitch_5
        :pswitch_0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method
