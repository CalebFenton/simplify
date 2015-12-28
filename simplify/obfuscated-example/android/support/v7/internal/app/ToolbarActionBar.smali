.class public Landroid/support/v7/internal/app/ToolbarActionBar;
.super Landroid/support/v7/app/ActionBar;
.source "ToolbarActionBar.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;,
        Landroid/support/v7/internal/app/ToolbarActionBar$PanelMenuPresenterCallback;,
        Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;,
        Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;
    }
.end annotation


# instance fields
.field private mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

.field private mLastMenuVisibility:Z

.field private mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

.field private mMenuCallbackSet:Z

.field private final mMenuClicker:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

.field private final mMenuInvalidator:Ljava/lang/Runnable;

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

.field private mToolbar:Landroid/support/v7/widget/Toolbar;

.field private mToolbarMenuPrepared:Z

.field private mWindow:Landroid/view/Window;

.field private mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;


# direct methods
.method public constructor <init>(Landroid/support/v7/widget/Toolbar;Ljava/lang/CharSequence;Landroid/view/Window;Landroid/support/v7/internal/app/WindowCallback;)V
    .locals 2
    .param p1, "toolbar"    # Landroid/support/v7/widget/Toolbar;
    .param p2, "title"    # Ljava/lang/CharSequence;
    .param p3, "window"    # Landroid/view/Window;
    .param p4, "windowCallback"    # Landroid/support/v7/internal/app/WindowCallback;

    #@0
    .prologue
    .line 82
    invoke-direct {p0}, Landroid/support/v7/app/ActionBar;-><init>()V

    #@3
    .line 60
    new-instance v0, Ljava/util/ArrayList;

    #@5
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@8
    iput-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@a
    .line 66
    new-instance v0, Landroid/support/v7/internal/app/ToolbarActionBar$1;

    #@c
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/ToolbarActionBar$1;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V

    #@f
    iput-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuInvalidator:Ljava/lang/Runnable;

    #@11
    .line 73
    new-instance v0, Landroid/support/v7/internal/app/ToolbarActionBar$2;

    #@13
    invoke-direct {v0, p0}, Landroid/support/v7/internal/app/ToolbarActionBar$2;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V

    #@16
    iput-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuClicker:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

    #@18
    .line 83
    iput-object p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1a
    .line 84
    new-instance v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@1c
    const/4 v1, 0x0

    #@1d
    invoke-direct {v0, p1, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;-><init>(Landroid/support/v7/widget/Toolbar;Z)V

    #@20
    iput-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@22
    .line 85
    new-instance v0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;

    #@24
    invoke-direct {v0, p0, p4}, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/WindowCallback;)V

    #@27
    iput-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@29
    .line 86
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2b
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2d
    invoke-interface {v0, v1}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V

    #@30
    .line 87
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuClicker:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

    #@32
    invoke-virtual {p1, v0}, Landroid/support/v7/widget/Toolbar;->setOnMenuItemClickListener(Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;)V

    #@35
    .line 88
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@37
    invoke-interface {v0, p2}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@3a
    .line 90
    iput-object p3, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindow:Landroid/view/Window;

    #@3c
    .line 91
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    return-object v0
.end method

.method static synthetic access$100(Landroid/support/v7/internal/app/ToolbarActionBar;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z

    #@2
    return v0
.end method

.method static synthetic access$1000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/view/Window;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindow:Landroid/view/Window;

    #@2
    return-object v0
.end method

.method static synthetic access$102(Landroid/support/v7/internal/app/ToolbarActionBar;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 52
    iput-boolean p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z

    #@2
    return p1
.end method

.method static synthetic access$200(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/widget/DecorToolbar;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/internal/app/ToolbarActionBar;)Ljava/lang/Runnable;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuInvalidator:Ljava/lang/Runnable;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/widget/Toolbar;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/view/Menu;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;

    #@0
    .prologue
    .line 52
    invoke-direct {p0}, Landroid/support/v7/internal/app/ToolbarActionBar;->getMenu()Landroid/view/Menu;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$600(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/view/Menu;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;
    .param p1, "x1"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 52
    invoke-direct {p0, p1}, Landroid/support/v7/internal/app/ToolbarActionBar;->getListMenuView(Landroid/view/Menu;)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method private getListMenuView(Landroid/view/Menu;)Landroid/view/View;
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 499
    if-eqz p1, :cond_0

    #@3
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@5
    if-nez v1, :cond_1

    #@7
    .line 506
    :cond_0
    :goto_0
    return-object v0

    #@8
    .line 503
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@a
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->getAdapter()Landroid/widget/ListAdapter;

    #@d
    move-result-object v1

    #@e
    invoke-interface {v1}, Landroid/widget/ListAdapter;->getCount()I

    #@11
    move-result v1

    #@12
    if-lez v1, :cond_0

    #@14
    .line 504
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@16
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@18
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@1b
    move-result-object v0

    #@1c
    check-cast v0, Landroid/view/View;

    #@1e
    goto :goto_0
.end method

.method private getMenu()Landroid/view/Menu;
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 550
    iget-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuCallbackSet:Z

    #@3
    if-nez v0, :cond_0

    #@5
    .line 551
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@7
    new-instance v1, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;

    #@9
    invoke-direct {v1, p0, v3}, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/ToolbarActionBar$1;)V

    #@c
    new-instance v2, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;

    #@e
    invoke-direct {v2, p0, v3}, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/ToolbarActionBar$1;)V

    #@11
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->setMenuCallbacks(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@14
    .line 552
    const/4 v0, 0x1

    #@15
    iput-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuCallbackSet:Z

    #@17
    .line 554
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@19
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getMenu()Landroid/view/Menu;

    #@1c
    move-result-object v0

    #@1d
    return-object v0
.end method


# virtual methods
.method public addOnMenuVisibilityListener(Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@0
    .prologue
    .line 479
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@5
    .line 480
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 343
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;I)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I

    #@0
    .prologue
    .line 355
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;IZ)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I
    .param p3, "setSelected"    # Z

    #@0
    .prologue
    .line 361
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;Z)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "setSelected"    # Z

    #@0
    .prologue
    .line 349
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public collapseActionView()Z
    .locals 1

    #@0
    .prologue
    .line 444
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->hasExpandedActionView()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 445
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->collapseActionView()V

    #@d
    .line 446
    const/4 v0, 0x1

    #@e
    .line 448
    :goto_0
    return v0

    #@f
    :cond_0
    const/4 v0, 0x0

    #@10
    goto :goto_0
.end method

.method public dispatchMenuVisibilityChanged(Z)V
    .locals 3
    .param p1, "isVisible"    # Z

    #@0
    .prologue
    .line 487
    iget-boolean v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mLastMenuVisibility:Z

    #@2
    if-ne p1, v2, :cond_1

    #@4
    .line 496
    :cond_0
    return-void

    #@5
    .line 490
    :cond_1
    iput-boolean p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mLastMenuVisibility:Z

    #@7
    .line 492
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@c
    move-result v0

    #@d
    .line 493
    .local v0, "count":I
    const/4 v1, 0x0

    #@e
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@10
    .line 494
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@12
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@15
    move-result-object v2

    #@16
    check-cast v2, Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@18
    invoke-interface {v2, p1}, Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;->onMenuVisibilityChanged(Z)V

    #@1b
    .line 493
    add-int/lit8 v1, v1, 0x1

    #@1d
    goto :goto_0
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 304
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

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
    .line 332
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

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
    .line 156
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

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
    .line 408
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getHeight()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getNavigationItemCount()I
    .locals 1

    #@0
    .prologue
    .line 233
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getNavigationMode()I
    .locals 1

    #@0
    .prologue
    .line 319
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getSelectedNavigationIndex()I
    .locals 1

    #@0
    .prologue
    .line 228
    const/4 v0, -0x1

    #@1
    return v0
.end method

.method public getSelectedTab()Landroid/support/v7/app/ActionBar$Tab;
    .locals 2

    #@0
    .prologue
    .line 391
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 314
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getSubtitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTabAt(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "index"    # I

    #@0
    .prologue
    .line 397
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public getTabCount()I
    .locals 1

    #@0
    .prologue
    .line 403
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getThemedContext()Landroid/content/Context;
    .locals 1

    #@0
    .prologue
    .line 161
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 309
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getTitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getWrappedWindowCallback()Landroid/support/v7/internal/app/WindowCallback;
    .locals 1

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    return-object v0
.end method

.method public hide()V
    .locals 2

    #@0
    .prologue
    .line 422
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    const/16 v1, 0x8

    #@4
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setVisibility(I)V

    #@7
    .line 423
    return-void
.end method

.method public invalidateOptionsMenu()Z
    .locals 2

    #@0
    .prologue
    .line 437
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuInvalidator:Ljava/lang/Runnable;

    #@4
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@7
    .line 438
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@9
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuInvalidator:Ljava/lang/Runnable;

    #@b
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewCompat;->postOnAnimation(Landroid/view/View;Ljava/lang/Runnable;)V

    #@e
    .line 439
    const/4 v0, 0x1

    #@f
    return v0
.end method

.method public isShowing()Z
    .locals 1

    #@0
    .prologue
    .line 427
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getVisibility()I

    #@5
    move-result v0

    #@6
    if-nez v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_0
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method public isTitleTruncated()Z
    .locals 1

    #@0
    .prologue
    .line 166
    invoke-super {p0}, Landroid/support/v7/app/ActionBar;->isTitleTruncated()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public newTab()Landroid/support/v7/app/ActionBar$Tab;
    .locals 2

    #@0
    .prologue
    .line 337
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 0
    .param p1, "config"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 201
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBar;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@3
    .line 202
    return-void
.end method

.method public onMenuKeyEvent(Landroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 472
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getAction()I

    #@4
    move-result v0

    #@5
    if-ne v0, v1, :cond_0

    #@7
    .line 473
    invoke-virtual {p0}, Landroid/support/v7/internal/app/ToolbarActionBar;->openOptionsMenu()Z

    #@a
    .line 475
    :cond_0
    return v1
.end method

.method public openOptionsMenu()Z
    .locals 1

    #@0
    .prologue
    .line 432
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->showOverflowMenu()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method populateOptionsMenu()V
    .locals 5

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 452
    invoke-direct {p0}, Landroid/support/v7/internal/app/ToolbarActionBar;->getMenu()Landroid/view/Menu;

    #@4
    move-result-object v1

    #@5
    .line 453
    .local v1, "menu":Landroid/view/Menu;
    instance-of v2, v1, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@7
    if-eqz v2, :cond_0

    #@9
    move-object v2, v1

    #@a
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@c
    move-object v0, v2

    #@d
    .line 454
    .local v0, "mb":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_0
    if-eqz v0, :cond_1

    #@f
    .line 455
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@12
    .line 458
    :cond_1
    :try_start_0
    invoke-interface {v1}, Landroid/view/Menu;->clear()V

    #@15
    .line 459
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@17
    const/4 v3, 0x0

    #@18
    invoke-interface {v2, v3, v1}, Landroid/support/v7/internal/app/WindowCallback;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@1b
    move-result v2

    #@1c
    if-eqz v2, :cond_2

    #@1e
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@20
    const/4 v3, 0x0

    #@21
    const/4 v4, 0x0

    #@22
    invoke-interface {v2, v3, v4, v1}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@25
    move-result v2

    #@26
    if-nez v2, :cond_3

    #@28
    .line 461
    :cond_2
    invoke-interface {v1}, Landroid/view/Menu;->clear()V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@2b
    .line 464
    :cond_3
    if-eqz v0, :cond_4

    #@2d
    .line 465
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@30
    .line 468
    :cond_4
    return-void

    #@31
    .line 464
    :catchall_0
    move-exception v2

    #@32
    if-eqz v0, :cond_5

    #@34
    .line 465
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@37
    :cond_5
    throw v2
.end method

.method public removeAllTabs()V
    .locals 2

    #@0
    .prologue
    .line 379
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public removeOnMenuVisibilityListener(Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v7/app/ActionBar$OnMenuVisibilityListener;

    #@0
    .prologue
    .line 483
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuVisibilityListeners:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@5
    .line 484
    return-void
.end method

.method public removeTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 367
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public removeTabAt(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 373
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public selectTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 2
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 385
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "Tabs are not supported in toolbar action bars"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 299
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 300
    return-void
.end method

.method public setCustomView(I)V
    .locals 3
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 110
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v1}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-static {v1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@9
    move-result-object v0

    #@a
    .line 111
    .local v0, "inflater":Landroid/view/LayoutInflater;
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@c
    const/4 v2, 0x0

    #@d
    invoke-virtual {v0, p1, v1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setCustomView(Landroid/view/View;)V

    #@14
    .line 112
    return-void
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, -0x2

    #@1
    .line 99
    new-instance v0, Landroid/support/v7/app/ActionBar$LayoutParams;

    #@3
    invoke-direct {v0, v1, v1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(II)V

    #@6
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->setCustomView(Landroid/view/View;Landroid/support/v7/app/ActionBar$LayoutParams;)V

    #@9
    .line 100
    return-void
.end method

.method public setCustomView(Landroid/view/View;Landroid/support/v7/app/ActionBar$LayoutParams;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "layoutParams"    # Landroid/support/v7/app/ActionBar$LayoutParams;

    #@0
    .prologue
    .line 104
    invoke-virtual {p1, p2}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 105
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@5
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setCustomView(Landroid/view/View;)V

    #@8
    .line 106
    return-void
.end method

.method public setDefaultDisplayHomeAsUpEnabled(Z)V
    .locals 0
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 187
    return-void
.end method

.method public setDisplayHomeAsUpEnabled(Z)V
    .locals 2
    .param p1, "showHomeAsUp"    # Z

    #@0
    .prologue
    const/4 v1, 0x4

    #@1
    .line 284
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@7
    .line 285
    return-void

    #@8
    .line 284
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
    .line 263
    const/4 v0, -0x1

    #@1
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@4
    .line 264
    return-void
.end method

.method public setDisplayOptions(II)V
    .locals 4
    .param p1, "options"    # I
    .param p2, "mask"    # I

    #@0
    .prologue
    .line 268
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->getDisplayOptions()I

    #@5
    move-result v0

    #@6
    .line 269
    .local v0, "currentOptions":I
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@8
    and-int v2, p1, p2

    #@a
    xor-int/lit8 v3, p2, -0x1

    #@c
    and-int/2addr v3, v0

    #@d
    or-int/2addr v2, v3

    #@e
    invoke-interface {v1, v2}, Landroid/support/v7/internal/widget/DecorToolbar;->setDisplayOptions(I)V

    #@11
    .line 270
    return-void
.end method

.method public setDisplayShowCustomEnabled(Z)V
    .locals 2
    .param p1, "showCustom"    # Z

    #@0
    .prologue
    const/16 v1, 0x10

    #@2
    .line 294
    if-eqz p1, :cond_0

    #@4
    move v0, v1

    #@5
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@8
    .line 295
    return-void

    #@9
    .line 294
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
    .line 279
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@7
    .line 280
    return-void

    #@8
    .line 279
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
    .line 289
    if-eqz p1, :cond_0

    #@4
    move v0, v1

    #@5
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@8
    .line 290
    return-void

    #@9
    .line 289
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
    .line 274
    if-eqz p1, :cond_0

    #@3
    move v0, v1

    #@4
    :goto_0
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->setDisplayOptions(II)V

    #@7
    .line 275
    return-void

    #@8
    .line 274
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
    .line 151
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/view/ViewCompat;->setElevation(Landroid/view/View;F)V

    #@5
    .line 152
    return-void
.end method

.method public setHomeActionContentDescription(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 191
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationContentDescription(I)V

    #@5
    .line 192
    return-void
.end method

.method public setHomeActionContentDescription(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "description"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 181
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@5
    .line 182
    return-void
.end method

.method public setHomeAsUpIndicator(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 176
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(I)V

    #@5
    .line 177
    return-void
.end method

.method public setHomeAsUpIndicator(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "indicator"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 171
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 172
    return-void
.end method

.method public setHomeButtonEnabled(Z)V
    .locals 0
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 147
    return-void
.end method

.method public setIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 116
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(I)V

    #@5
    .line 117
    return-void
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 121
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 122
    return-void
.end method

.method public setListMenuPresenter(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)V
    .locals 4
    .param p1, "listMenuPresenter"    # Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 558
    invoke-direct {p0}, Landroid/support/v7/internal/app/ToolbarActionBar;->getMenu()Landroid/view/Menu;

    #@4
    move-result-object v1

    #@5
    .line 560
    .local v1, "menu":Landroid/view/Menu;
    instance-of v2, v1, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@7
    if-eqz v2, :cond_1

    #@9
    move-object v0, v1

    #@a
    .line 561
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@c
    .line 563
    .local v0, "mb":Landroid/support/v7/internal/view/menu/MenuBuilder;
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@e
    if-eqz v2, :cond_0

    #@10
    .line 565
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@12
    invoke-virtual {v2, v3}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@15
    .line 566
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@17
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V

    #@1a
    .line 569
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mListMenuPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@1c
    .line 571
    if-eqz p1, :cond_1

    #@1e
    .line 572
    new-instance v2, Landroid/support/v7/internal/app/ToolbarActionBar$PanelMenuPresenterCallback;

    #@20
    invoke-direct {v2, p0, v3}, Landroid/support/v7/internal/app/ToolbarActionBar$PanelMenuPresenterCallback;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/ToolbarActionBar$1;)V

    #@23
    invoke-virtual {p1, v2}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@26
    .line 573
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V

    #@29
    .line 576
    .end local v0    # "mb":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_1
    return-void
.end method

.method public setListNavigationCallbacks(Landroid/widget/SpinnerAdapter;Landroid/support/v7/app/ActionBar$OnNavigationListener;)V
    .locals 2
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;
    .param p2, "callback"    # Landroid/support/v7/app/ActionBar$OnNavigationListener;

    #@0
    .prologue
    .line 211
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    new-instance v1, Landroid/support/v7/internal/app/NavItemSelectedListener;

    #@4
    invoke-direct {v1, p2}, Landroid/support/v7/internal/app/NavItemSelectedListener;-><init>(Landroid/support/v7/app/ActionBar$OnNavigationListener;)V

    #@7
    invoke-interface {v0, p1, v1}, Landroid/support/v7/internal/widget/DecorToolbar;->setDropdownParams(Landroid/widget/SpinnerAdapter;Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;)V

    #@a
    .line 212
    return-void
.end method

.method public setLogo(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 126
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setLogo(I)V

    #@5
    .line 127
    return-void
.end method

.method public setLogo(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "logo"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 131
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 132
    return-void
.end method

.method public setNavigationMode(I)V
    .locals 2
    .param p1, "mode"    # I

    #@0
    .prologue
    .line 324
    const/4 v0, 0x2

    #@1
    if-ne p1, v0, :cond_0

    #@3
    .line 325
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@5
    const-string v1, "Tabs not supported in this configuration"

    #@7
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@a
    throw v0

    #@b
    .line 327
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@d
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setNavigationMode(I)V

    #@10
    .line 328
    return-void
.end method

.method public setSelectedNavigationItem(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 216
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getNavigationMode()I

    #@5
    move-result v0

    #@6
    packed-switch v0, :pswitch_data_0

    #@9
    .line 221
    new-instance v0, Ljava/lang/IllegalStateException;

    #@b
    const-string v1, "setSelectedNavigationIndex not valid for current navigation mode"

    #@d
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@10
    throw v0

    #@11
    .line 218
    :pswitch_0
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@13
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setDropdownSelectedPosition(I)V

    #@16
    .line 224
    return-void

    #@17
    .line 216
    nop

    #@18
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
    .end packed-switch
.end method

.method public setShowHideAnimationEnabled(Z)V
    .locals 0
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 197
    return-void
.end method

.method public setSplitBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 142
    return-void
.end method

.method public setStackedBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 137
    return-void
.end method

.method public setSubtitle(I)V
    .locals 2
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 258
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz p1, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    :goto_0
    invoke-interface {v1, v0}, Landroid/support/v7/internal/widget/DecorToolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@11
    .line 259
    return-void

    #@12
    .line 258
    :cond_0
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 253
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@5
    .line 254
    return-void
.end method

.method public setTitle(I)V
    .locals 2
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 243
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    if-eqz p1, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@6
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    :goto_0
    invoke-interface {v1, v0}, Landroid/support/v7/internal/widget/DecorToolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@11
    .line 244
    return-void

    #@12
    .line 243
    :cond_0
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 238
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@5
    .line 239
    return-void
.end method

.method public setWindowTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 248
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/DecorToolbar;->setWindowTitle(Ljava/lang/CharSequence;)V

    #@5
    .line 249
    return-void
.end method

.method public show()V
    .locals 2

    #@0
    .prologue
    .line 415
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setVisibility(I)V

    #@6
    .line 416
    return-void
.end method

.method public startActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 1
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 206
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/app/WindowCallback;->startActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method
