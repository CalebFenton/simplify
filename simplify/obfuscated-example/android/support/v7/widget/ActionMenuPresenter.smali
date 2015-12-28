.class public Landroid/support/v7/widget/ActionMenuPresenter;
.super Landroid/support/v7/internal/view/menu/BaseMenuPresenter;
.source "ActionMenuPresenter.java"

# interfaces
.implements Landroid/support/v4/view/ActionProvider$SubUiVisibilityListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/ActionMenuPresenter$1;,
        Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;,
        Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;,
        Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;,
        Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;,
        Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;,
        Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;,
        Landroid/support/v7/widget/ActionMenuPresenter$SavedState;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "ActionMenuPresenter"


# instance fields
.field private final mActionButtonGroups:Landroid/util/SparseBooleanArray;

.field private mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

.field private mActionItemWidthLimit:I

.field private mExpandedActionViewsExclusive:Z

.field private mMaxItems:I

.field private mMaxItemsSet:Z

.field private mMinCellSize:I

.field mOpenSubMenuId:I

.field private mOverflowButton:Landroid/view/View;

.field private mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

.field private mPopupCallback:Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;

.field final mPopupPresenterCallback:Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;

.field private mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

.field private mReserveOverflow:Z

.field private mReserveOverflowSet:Z

.field private mScrapActionButtonView:Landroid/view/View;

.field private mStrictWidthLimit:Z

.field private mWidthLimit:I

.field private mWidthLimitSet:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 86
    sget v0, Landroid/support/v7/appcompat/R$layout;->abc_action_menu_layout:I

    #@2
    sget v1, Landroid/support/v7/appcompat/R$layout;->abc_action_menu_item_layout:I

    #@4
    invoke-direct {p0, p1, v0, v1}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;-><init>(Landroid/content/Context;II)V

    #@7
    .line 72
    new-instance v0, Landroid/util/SparseBooleanArray;

    #@9
    invoke-direct {v0}, Landroid/util/SparseBooleanArray;-><init>()V

    #@c
    iput-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonGroups:Landroid/util/SparseBooleanArray;

    #@e
    .line 82
    new-instance v0, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;

    #@10
    const/4 v1, 0x0

    #@11
    invoke-direct {v0, p0, v1}, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$1;)V

    #@14
    iput-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPopupPresenterCallback:Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;

    #@16
    .line 87
    return-void
.end method

.method static synthetic access$200(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@2
    return-object v0
.end method

.method static synthetic access$202(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;)Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;
    .param p1, "x1"    # Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@0
    .prologue
    .line 53
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@2
    return-object p1
.end method

.method static synthetic access$300(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@2
    return-object v0
.end method

.method static synthetic access$302(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;)Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;
    .param p1, "x1"    # Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@0
    .prologue
    .line 53
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@2
    return-object p1
.end method

.method static synthetic access$400(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@2
    return-object v0
.end method

.method static synthetic access$600(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/internal/view/menu/MenuView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@2
    return-object v0
.end method

.method static synthetic access$700(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@2
    return-object v0
.end method

.method static synthetic access$702(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;)Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;
    .param p1, "x1"    # Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@0
    .prologue
    .line 53
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@2
    return-object p1
.end method

.method static synthetic access$800(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method static synthetic access$900(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/internal/view/menu/MenuView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@2
    return-object v0
.end method

.method private findViewForItem(Landroid/view/MenuItem;)Landroid/view/View;
    .locals 6
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 282
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@3
    check-cast v3, Landroid/view/ViewGroup;

    #@5
    .line 283
    .local v3, "parent":Landroid/view/ViewGroup;
    if-nez v3, :cond_1

    #@7
    move-object v0, v5

    #@8
    .line 293
    :cond_0
    :goto_0
    return-object v0

    #@9
    .line 285
    :cond_1
    invoke-virtual {v3}, Landroid/view/ViewGroup;->getChildCount()I

    #@c
    move-result v1

    #@d
    .line 286
    .local v1, "count":I
    const/4 v2, 0x0

    #@e
    .local v2, "i":I
    :goto_1
    if-ge v2, v1, :cond_3

    #@10
    .line 287
    invoke-virtual {v3, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@13
    move-result-object v0

    #@14
    .line 288
    .local v0, "child":Landroid/view/View;
    instance-of v4, v0, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@16
    if-eqz v4, :cond_2

    #@18
    move-object v4, v0

    #@19
    check-cast v4, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@1b
    invoke-interface {v4}, Landroid/support/v7/internal/view/menu/MenuView$ItemView;->getItemData()Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@1e
    move-result-object v4

    #@1f
    if-eq v4, p1, :cond_0

    #@21
    .line 286
    :cond_2
    add-int/lit8 v2, v2, 0x1

    #@23
    goto :goto_1

    #@24
    .end local v0    # "child":Landroid/view/View;
    :cond_3
    move-object v0, v5

    #@25
    .line 293
    goto :goto_0
.end method


# virtual methods
.method public bindItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/support/v7/internal/view/menu/MenuView$ItemView;)V
    .locals 4
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .param p2, "itemView"    # Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@0
    .prologue
    .line 184
    const/4 v2, 0x0

    #@1
    invoke-interface {p2, p1, v2}, Landroid/support/v7/internal/view/menu/MenuView$ItemView;->initialize(Landroid/support/v7/internal/view/menu/MenuItemImpl;I)V

    #@4
    .line 186
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@6
    check-cast v1, Landroid/support/v7/widget/ActionMenuView;

    #@8
    .local v1, "menuView":Landroid/support/v7/widget/ActionMenuView;
    move-object v0, p2

    #@9
    .line 187
    check-cast v0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@b
    .line 188
    .local v0, "actionItemView":Landroid/support/v7/internal/view/menu/ActionMenuItemView;
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setItemInvoker(Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;)V

    #@e
    .line 190
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPopupCallback:Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;

    #@10
    if-nez v2, :cond_0

    #@12
    .line 191
    new-instance v2, Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;

    #@14
    const/4 v3, 0x0

    #@15
    invoke-direct {v2, p0, v3}, Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$1;)V

    #@18
    iput-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPopupCallback:Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;

    #@1a
    .line 193
    :cond_0
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPopupCallback:Landroid/support/v7/widget/ActionMenuPresenter$ActionMenuPopupCallback;

    #@1c
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setPopupCallback(Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;)V

    #@1f
    .line 194
    return-void
.end method

.method public dismissPopupMenus()Z
    .locals 2

    #@0
    .prologue
    .line 342
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@3
    move-result v0

    #@4
    .line 343
    .local v0, "result":Z
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideSubMenus()Z

    #@7
    move-result v1

    #@8
    or-int/2addr v0, v1

    #@9
    .line 344
    return v0
.end method

.method public filterLeftoverView(Landroid/view/ViewGroup;I)Z
    .locals 2
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "childIndex"    # I

    #@0
    .prologue
    .line 256
    invoke-virtual {p1, p2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@6
    if-ne v0, v1, :cond_0

    #@8
    const/4 v0, 0x0

    #@9
    .line 257
    :goto_0
    return v0

    #@a
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->filterLeftoverView(Landroid/view/ViewGroup;I)Z

    #@d
    move-result v0

    #@e
    goto :goto_0
.end method

.method public flagActionItems()Z
    .locals 29

    #@0
    .prologue
    .line 379
    move-object/from16 v0, p0

    #@2
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    move-object/from16 v27, v0

    #@6
    invoke-virtual/range {v27 .. v27}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getVisibleItems()Ljava/util/ArrayList;

    #@9
    move-result-object v25

    #@a
    .line 380
    .local v25, "visibleItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    invoke-virtual/range {v25 .. v25}, Ljava/util/ArrayList;->size()I

    #@d
    move-result v15

    #@e
    .line 381
    .local v15, "itemsSize":I
    move-object/from16 v0, p0

    #@10
    iget v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItems:I

    #@12
    move/from16 v17, v0

    #@14
    .line 382
    .local v17, "maxActions":I
    move-object/from16 v0, p0

    #@16
    iget v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionItemWidthLimit:I

    #@18
    move/from16 v26, v0

    #@1a
    .line 383
    .local v26, "widthLimit":I
    const/16 v27, 0x0

    #@1c
    const/16 v28, 0x0

    #@1e
    invoke-static/range {v27 .. v28}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@21
    move-result v20

    #@22
    .line 384
    .local v20, "querySpec":I
    move-object/from16 v0, p0

    #@24
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@26
    move-object/from16 v19, v0

    #@28
    check-cast v19, Landroid/view/ViewGroup;

    #@2a
    .line 386
    .local v19, "parent":Landroid/view/ViewGroup;
    const/16 v22, 0x0

    #@2c
    .line 387
    .local v22, "requiredItems":I
    const/16 v21, 0x0

    #@2e
    .line 388
    .local v21, "requestedItems":I
    const/4 v8, 0x0

    #@2f
    .line 389
    .local v8, "firstActionWidth":I
    const/4 v10, 0x0

    #@30
    .line 390
    .local v10, "hasOverflow":Z
    const/4 v11, 0x0

    #@31
    .local v11, "i":I
    :goto_0
    if-ge v11, v15, :cond_3

    #@33
    .line 391
    move-object/from16 v0, v25

    #@35
    invoke-virtual {v0, v11}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@38
    move-result-object v14

    #@39
    check-cast v14, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@3b
    .line 392
    .local v14, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->requiresActionButton()Z

    #@3e
    move-result v27

    #@3f
    if-eqz v27, :cond_1

    #@41
    .line 393
    add-int/lit8 v22, v22, 0x1

    #@43
    .line 399
    :goto_1
    move-object/from16 v0, p0

    #@45
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mExpandedActionViewsExclusive:Z

    #@47
    move/from16 v27, v0

    #@49
    if-eqz v27, :cond_0

    #@4b
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionViewExpanded()Z

    #@4e
    move-result v27

    #@4f
    if-eqz v27, :cond_0

    #@51
    .line 402
    const/16 v17, 0x0

    #@53
    .line 390
    :cond_0
    add-int/lit8 v11, v11, 0x1

    #@55
    goto :goto_0

    #@56
    .line 394
    :cond_1
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->requestsActionButton()Z

    #@59
    move-result v27

    #@5a
    if-eqz v27, :cond_2

    #@5c
    .line 395
    add-int/lit8 v21, v21, 0x1

    #@5e
    goto :goto_1

    #@5f
    .line 397
    :cond_2
    const/4 v10, 0x1

    #@60
    goto :goto_1

    #@61
    .line 407
    .end local v14    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_3
    move-object/from16 v0, p0

    #@63
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@65
    move/from16 v27, v0

    #@67
    if-eqz v27, :cond_5

    #@69
    if-nez v10, :cond_4

    #@6b
    add-int v27, v22, v21

    #@6d
    move/from16 v0, v27

    #@6f
    move/from16 v1, v17

    #@71
    if-le v0, v1, :cond_5

    #@73
    .line 409
    :cond_4
    add-int/lit8 v17, v17, -0x1

    #@75
    .line 411
    :cond_5
    sub-int v17, v17, v22

    #@77
    .line 413
    move-object/from16 v0, p0

    #@79
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonGroups:Landroid/util/SparseBooleanArray;

    #@7b
    move-object/from16 v23, v0

    #@7d
    .line 414
    .local v23, "seenGroups":Landroid/util/SparseBooleanArray;
    invoke-virtual/range {v23 .. v23}, Landroid/util/SparseBooleanArray;->clear()V

    #@80
    .line 416
    const/4 v4, 0x0

    #@81
    .line 417
    .local v4, "cellSize":I
    const/4 v7, 0x0

    #@82
    .line 418
    .local v7, "cellsRemaining":I
    move-object/from16 v0, p0

    #@84
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@86
    move/from16 v27, v0

    #@88
    if-eqz v27, :cond_6

    #@8a
    .line 419
    move-object/from16 v0, p0

    #@8c
    iget v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMinCellSize:I

    #@8e
    move/from16 v27, v0

    #@90
    div-int v7, v26, v27

    #@92
    .line 420
    move-object/from16 v0, p0

    #@94
    iget v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMinCellSize:I

    #@96
    move/from16 v27, v0

    #@98
    rem-int v5, v26, v27

    #@9a
    .line 421
    .local v5, "cellSizeRemaining":I
    move-object/from16 v0, p0

    #@9c
    iget v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mMinCellSize:I

    #@9e
    move/from16 v27, v0

    #@a0
    div-int v28, v5, v7

    #@a2
    add-int v4, v27, v28

    #@a4
    .line 425
    .end local v5    # "cellSizeRemaining":I
    :cond_6
    const/4 v11, 0x0

    #@a5
    :goto_2
    if-ge v11, v15, :cond_1d

    #@a7
    .line 426
    move-object/from16 v0, v25

    #@a9
    invoke-virtual {v0, v11}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@ac
    move-result-object v14

    #@ad
    check-cast v14, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@af
    .line 428
    .restart local v14    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->requiresActionButton()Z

    #@b2
    move-result v27

    #@b3
    if-eqz v27, :cond_b

    #@b5
    .line 429
    move-object/from16 v0, p0

    #@b7
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@b9
    move-object/from16 v27, v0

    #@bb
    move-object/from16 v0, p0

    #@bd
    move-object/from16 v1, v27

    #@bf
    move-object/from16 v2, v19

    #@c1
    invoke-virtual {v0, v14, v1, v2}, Landroid/support/v7/widget/ActionMenuPresenter;->getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@c4
    move-result-object v24

    #@c5
    .line 430
    .local v24, "v":Landroid/view/View;
    move-object/from16 v0, p0

    #@c7
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@c9
    move-object/from16 v27, v0

    #@cb
    if-nez v27, :cond_7

    #@cd
    .line 431
    move-object/from16 v0, v24

    #@cf
    move-object/from16 v1, p0

    #@d1
    iput-object v0, v1, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@d3
    .line 433
    :cond_7
    move-object/from16 v0, p0

    #@d5
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@d7
    move/from16 v27, v0

    #@d9
    if-eqz v27, :cond_a

    #@db
    .line 434
    const/16 v27, 0x0

    #@dd
    move-object/from16 v0, v24

    #@df
    move/from16 v1, v20

    #@e1
    move/from16 v2, v27

    #@e3
    invoke-static {v0, v4, v7, v1, v2}, Landroid/support/v7/widget/ActionMenuView;->measureChildForCells(Landroid/view/View;IIII)I

    #@e6
    move-result v27

    #@e7
    sub-int v7, v7, v27

    #@e9
    .line 439
    :goto_3
    invoke-virtual/range {v24 .. v24}, Landroid/view/View;->getMeasuredWidth()I

    #@ec
    move-result v18

    #@ed
    .line 440
    .local v18, "measuredWidth":I
    sub-int v26, v26, v18

    #@ef
    .line 441
    if-nez v8, :cond_8

    #@f1
    .line 442
    move/from16 v8, v18

    #@f3
    .line 444
    :cond_8
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@f6
    move-result v9

    #@f7
    .line 445
    .local v9, "groupId":I
    if-eqz v9, :cond_9

    #@f9
    .line 446
    const/16 v27, 0x1

    #@fb
    move-object/from16 v0, v23

    #@fd
    move/from16 v1, v27

    #@ff
    invoke-virtual {v0, v9, v1}, Landroid/util/SparseBooleanArray;->put(IZ)V

    #@102
    .line 448
    :cond_9
    const/16 v27, 0x1

    #@104
    move/from16 v0, v27

    #@106
    invoke-virtual {v14, v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIsActionButton(Z)V

    #@109
    .line 425
    .end local v9    # "groupId":I
    .end local v18    # "measuredWidth":I
    .end local v24    # "v":Landroid/view/View;
    :goto_4
    add-int/lit8 v11, v11, 0x1

    #@10b
    goto :goto_2

    #@10c
    .line 437
    .restart local v24    # "v":Landroid/view/View;
    :cond_a
    move-object/from16 v0, v24

    #@10e
    move/from16 v1, v20

    #@110
    move/from16 v2, v20

    #@112
    invoke-virtual {v0, v1, v2}, Landroid/view/View;->measure(II)V

    #@115
    goto :goto_3

    #@116
    .line 449
    .end local v24    # "v":Landroid/view/View;
    :cond_b
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->requestsActionButton()Z

    #@119
    move-result v27

    #@11a
    if-eqz v27, :cond_1c

    #@11c
    .line 452
    invoke-virtual {v14}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@11f
    move-result v9

    #@120
    .line 453
    .restart local v9    # "groupId":I
    move-object/from16 v0, v23

    #@122
    invoke-virtual {v0, v9}, Landroid/util/SparseBooleanArray;->get(I)Z

    #@125
    move-result v12

    #@126
    .line 454
    .local v12, "inGroup":Z
    if-gtz v17, :cond_c

    #@128
    if-eqz v12, :cond_14

    #@12a
    :cond_c
    if-lez v26, :cond_14

    #@12c
    move-object/from16 v0, p0

    #@12e
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@130
    move/from16 v27, v0

    #@132
    if-eqz v27, :cond_d

    #@134
    if-lez v7, :cond_14

    #@136
    :cond_d
    const/4 v13, 0x1

    #@137
    .line 457
    .local v13, "isAction":Z
    :goto_5
    if-eqz v13, :cond_11

    #@139
    .line 458
    move-object/from16 v0, p0

    #@13b
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@13d
    move-object/from16 v27, v0

    #@13f
    move-object/from16 v0, p0

    #@141
    move-object/from16 v1, v27

    #@143
    move-object/from16 v2, v19

    #@145
    invoke-virtual {v0, v14, v1, v2}, Landroid/support/v7/widget/ActionMenuPresenter;->getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@148
    move-result-object v24

    #@149
    .line 459
    .restart local v24    # "v":Landroid/view/View;
    move-object/from16 v0, p0

    #@14b
    iget-object v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@14d
    move-object/from16 v27, v0

    #@14f
    if-nez v27, :cond_e

    #@151
    .line 460
    move-object/from16 v0, v24

    #@153
    move-object/from16 v1, p0

    #@155
    iput-object v0, v1, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@157
    .line 462
    :cond_e
    move-object/from16 v0, p0

    #@159
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@15b
    move/from16 v27, v0

    #@15d
    if-eqz v27, :cond_15

    #@15f
    .line 463
    const/16 v27, 0x0

    #@161
    move-object/from16 v0, v24

    #@163
    move/from16 v1, v20

    #@165
    move/from16 v2, v27

    #@167
    invoke-static {v0, v4, v7, v1, v2}, Landroid/support/v7/widget/ActionMenuView;->measureChildForCells(Landroid/view/View;IIII)I

    #@16a
    move-result v6

    #@16b
    .line 465
    .local v6, "cells":I
    sub-int/2addr v7, v6

    #@16c
    .line 466
    if-nez v6, :cond_f

    #@16e
    .line 467
    const/4 v13, 0x0

    #@16f
    .line 472
    .end local v6    # "cells":I
    :cond_f
    :goto_6
    invoke-virtual/range {v24 .. v24}, Landroid/view/View;->getMeasuredWidth()I

    #@172
    move-result v18

    #@173
    .line 473
    .restart local v18    # "measuredWidth":I
    sub-int v26, v26, v18

    #@175
    .line 474
    if-nez v8, :cond_10

    #@177
    .line 475
    move/from16 v8, v18

    #@179
    .line 478
    :cond_10
    move-object/from16 v0, p0

    #@17b
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@17d
    move/from16 v27, v0

    #@17f
    if-eqz v27, :cond_17

    #@181
    .line 479
    if-ltz v26, :cond_16

    #@183
    const/16 v27, 0x1

    #@185
    :goto_7
    and-int v13, v13, v27

    #@187
    .line 486
    .end local v18    # "measuredWidth":I
    .end local v24    # "v":Landroid/view/View;
    :cond_11
    :goto_8
    if-eqz v13, :cond_19

    #@189
    if-eqz v9, :cond_19

    #@18b
    .line 487
    const/16 v27, 0x1

    #@18d
    move-object/from16 v0, v23

    #@18f
    move/from16 v1, v27

    #@191
    invoke-virtual {v0, v9, v1}, Landroid/util/SparseBooleanArray;->put(IZ)V

    #@194
    .line 501
    :cond_12
    if-eqz v13, :cond_13

    #@196
    add-int/lit8 v17, v17, -0x1

    #@198
    .line 503
    :cond_13
    invoke-virtual {v14, v13}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIsActionButton(Z)V

    #@19b
    goto/16 :goto_4

    #@19d
    .line 454
    .end local v13    # "isAction":Z
    :cond_14
    const/4 v13, 0x0

    #@19e
    goto :goto_5

    #@19f
    .line 470
    .restart local v13    # "isAction":Z
    .restart local v24    # "v":Landroid/view/View;
    :cond_15
    move-object/from16 v0, v24

    #@1a1
    move/from16 v1, v20

    #@1a3
    move/from16 v2, v20

    #@1a5
    invoke-virtual {v0, v1, v2}, Landroid/view/View;->measure(II)V

    #@1a8
    goto :goto_6

    #@1a9
    .line 479
    .restart local v18    # "measuredWidth":I
    :cond_16
    const/16 v27, 0x0

    #@1ab
    goto :goto_7

    #@1ac
    .line 482
    :cond_17
    add-int v27, v26, v8

    #@1ae
    if-lez v27, :cond_18

    #@1b0
    const/16 v27, 0x1

    #@1b2
    :goto_9
    and-int v13, v13, v27

    #@1b4
    goto :goto_8

    #@1b5
    :cond_18
    const/16 v27, 0x0

    #@1b7
    goto :goto_9

    #@1b8
    .line 488
    .end local v18    # "measuredWidth":I
    .end local v24    # "v":Landroid/view/View;
    :cond_19
    if-eqz v12, :cond_12

    #@1ba
    .line 490
    const/16 v27, 0x0

    #@1bc
    move-object/from16 v0, v23

    #@1be
    move/from16 v1, v27

    #@1c0
    invoke-virtual {v0, v9, v1}, Landroid/util/SparseBooleanArray;->put(IZ)V

    #@1c3
    .line 491
    const/16 v16, 0x0

    #@1c5
    .local v16, "j":I
    :goto_a
    move/from16 v0, v16

    #@1c7
    if-ge v0, v11, :cond_12

    #@1c9
    .line 492
    move-object/from16 v0, v25

    #@1cb
    move/from16 v1, v16

    #@1cd
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@1d0
    move-result-object v3

    #@1d1
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@1d3
    .line 493
    .local v3, "areYouMyGroupie":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@1d6
    move-result v27

    #@1d7
    move/from16 v0, v27

    #@1d9
    if-ne v0, v9, :cond_1b

    #@1db
    .line 495
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionButton()Z

    #@1de
    move-result v27

    #@1df
    if-eqz v27, :cond_1a

    #@1e1
    add-int/lit8 v17, v17, 0x1

    #@1e3
    .line 496
    :cond_1a
    const/16 v27, 0x0

    #@1e5
    move/from16 v0, v27

    #@1e7
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIsActionButton(Z)V

    #@1ea
    .line 491
    :cond_1b
    add-int/lit8 v16, v16, 0x1

    #@1ec
    goto :goto_a

    #@1ed
    .line 506
    .end local v3    # "areYouMyGroupie":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .end local v9    # "groupId":I
    .end local v12    # "inGroup":Z
    .end local v13    # "isAction":Z
    .end local v16    # "j":I
    :cond_1c
    const/16 v27, 0x0

    #@1ef
    move/from16 v0, v27

    #@1f1
    invoke-virtual {v14, v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIsActionButton(Z)V

    #@1f4
    goto/16 :goto_4

    #@1f6
    .line 509
    .end local v14    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1d
    const/16 v27, 0x1

    #@1f8
    return v27
.end method

.method public getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 4
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 168
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getActionView()Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 169
    .local v0, "actionView":Landroid/view/View;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasCollapsibleActionView()Z

    #@9
    move-result v3

    #@a
    if-eqz v3, :cond_1

    #@c
    .line 170
    :cond_0
    invoke-super {p0, p1, p2, p3}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@f
    move-result-object v0

    #@10
    .line 172
    :cond_1
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionViewExpanded()Z

    #@13
    move-result v3

    #@14
    if-eqz v3, :cond_3

    #@16
    const/16 v3, 0x8

    #@18
    :goto_0
    invoke-virtual {v0, v3}, Landroid/view/View;->setVisibility(I)V

    #@1b
    move-object v2, p3

    #@1c
    .line 174
    check-cast v2, Landroid/support/v7/widget/ActionMenuView;

    #@1e
    .line 175
    .local v2, "menuParent":Landroid/support/v7/widget/ActionMenuView;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@21
    move-result-object v1

    #@22
    .line 176
    .local v1, "lp":Landroid/view/ViewGroup$LayoutParams;
    invoke-virtual {v2, v1}, Landroid/support/v7/widget/ActionMenuView;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@25
    move-result v3

    #@26
    if-nez v3, :cond_2

    #@28
    .line 177
    invoke-virtual {v2, v1}, Landroid/support/v7/widget/ActionMenuView;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@2b
    move-result-object v3

    #@2c
    invoke-virtual {v0, v3}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@2f
    .line 179
    :cond_2
    return-object v0

    #@30
    .line 172
    .end local v1    # "lp":Landroid/view/ViewGroup$LayoutParams;
    .end local v2    # "menuParent":Landroid/support/v7/widget/ActionMenuView;
    :cond_3
    const/4 v3, 0x0

    #@31
    goto :goto_0
.end method

.method public getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;
    .locals 2
    .param p1, "root"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 161
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@3
    move-result-object v0

    #@4
    .local v0, "result":Landroid/support/v7/internal/view/menu/MenuView;
    move-object v1, v0

    #@5
    .line 162
    check-cast v1, Landroid/support/v7/widget/ActionMenuView;

    #@7
    invoke-virtual {v1, p0}, Landroid/support/v7/widget/ActionMenuView;->setPresenter(Landroid/support/v7/widget/ActionMenuPresenter;)V

    #@a
    .line 163
    return-object v0
.end method

.method public hideOverflowMenu()Z
    .locals 4

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 323
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@3
    if-eqz v1, :cond_0

    #@5
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@7
    if-eqz v1, :cond_0

    #@9
    .line 324
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@b
    check-cast v1, Landroid/view/View;

    #@d
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@f
    invoke-virtual {v1, v3}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@12
    .line 325
    const/4 v1, 0x0

    #@13
    iput-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@15
    move v1, v2

    #@16
    .line 334
    :goto_0
    return v1

    #@17
    .line 329
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@19
    .line 330
    .local v0, "popup":Landroid/support/v7/internal/view/menu/MenuPopupHelper;
    if-eqz v0, :cond_1

    #@1b
    .line 331
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;->dismiss()V

    #@1e
    move v1, v2

    #@1f
    .line 332
    goto :goto_0

    #@20
    .line 334
    :cond_1
    const/4 v1, 0x0

    #@21
    goto :goto_0
.end method

.method public hideSubMenus()Z
    .locals 1

    #@0
    .prologue
    .line 353
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 354
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->dismiss()V

    #@9
    .line 355
    const/4 v0, 0x1

    #@a
    .line 357
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 8
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    const/4 v6, 0x0

    #@2
    .line 91
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@5
    .line 93
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@8
    move-result-object v1

    #@9
    .line 95
    .local v1, "res":Landroid/content/res/Resources;
    invoke-static {p1}, Landroid/support/v7/internal/view/ActionBarPolicy;->get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;

    #@c
    move-result-object v0

    #@d
    .line 96
    .local v0, "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    iget-boolean v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflowSet:Z

    #@f
    if-nez v4, :cond_0

    #@11
    .line 97
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->showsOverflowMenuButton()Z

    #@14
    move-result v4

    #@15
    iput-boolean v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@17
    .line 100
    :cond_0
    iget-boolean v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mWidthLimitSet:Z

    #@19
    if-nez v4, :cond_1

    #@1b
    .line 101
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getEmbeddedMenuWidthLimit()I

    #@1e
    move-result v4

    #@1f
    iput v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mWidthLimit:I

    #@21
    .line 105
    :cond_1
    iget-boolean v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItemsSet:Z

    #@23
    if-nez v4, :cond_2

    #@25
    .line 106
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getMaxActionButtons()I

    #@28
    move-result v4

    #@29
    iput v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItems:I

    #@2b
    .line 109
    :cond_2
    iget v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mWidthLimit:I

    #@2d
    .line 110
    .local v3, "width":I
    iget-boolean v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@2f
    if-eqz v4, :cond_4

    #@31
    .line 111
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@33
    if-nez v4, :cond_3

    #@35
    .line 112
    new-instance v4, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;

    #@37
    iget-object v5, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mSystemContext:Landroid/content/Context;

    #@39
    invoke-direct {v4, p0, v5}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;)V

    #@3c
    iput-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@3e
    .line 113
    invoke-static {v6, v6}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@41
    move-result v2

    #@42
    .line 114
    .local v2, "spec":I
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@44
    invoke-virtual {v4, v2, v2}, Landroid/view/View;->measure(II)V

    #@47
    .line 116
    .end local v2    # "spec":I
    :cond_3
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@49
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@4c
    move-result v4

    #@4d
    sub-int/2addr v3, v4

    #@4e
    .line 121
    :goto_0
    iput v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionItemWidthLimit:I

    #@50
    .line 123
    const/high16 v4, 0x42600000    # 56.0f

    #@52
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@55
    move-result-object v5

    #@56
    iget v5, v5, Landroid/util/DisplayMetrics;->density:F

    #@58
    mul-float/2addr v4, v5

    #@59
    float-to-int v4, v4

    #@5a
    iput v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMinCellSize:I

    #@5c
    .line 126
    iput-object v7, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mScrapActionButtonView:Landroid/view/View;

    #@5e
    .line 127
    return-void

    #@5f
    .line 118
    :cond_4
    iput-object v7, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@61
    goto :goto_0
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 368
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 364
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowPopup:Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;->isShowing()Z

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

.method public isOverflowReserved()Z
    .locals 1

    #@0
    .prologue
    .line 375
    iget-boolean v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@2
    return v0
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 514
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter;->dismissPopupMenus()Z

    #@3
    .line 515
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@6
    .line 516
    return-void
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 2
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 130
    iget-boolean v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItemsSet:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 131
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mContext:Landroid/content/Context;

    #@6
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v0

    #@a
    sget v1, Landroid/support/v7/appcompat/R$integer;->abc_max_action_buttons:I

    #@c
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getInteger(I)I

    #@f
    move-result v0

    #@10
    iput v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItems:I

    #@12
    .line 134
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@14
    if-eqz v0, :cond_1

    #@16
    .line 135
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@18
    const/4 v1, 0x1

    #@19
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@1c
    .line 137
    :cond_1
    return-void
.end method

.method public onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 5
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 527
    move-object v1, p1

    #@1
    check-cast v1, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;

    #@3
    .line 528
    .local v1, "saved":Landroid/support/v7/widget/ActionMenuPresenter$SavedState;
    iget v3, v1, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;->openSubMenuId:I

    #@5
    if-lez v3, :cond_0

    #@7
    .line 529
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    iget v4, v1, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;->openSubMenuId:I

    #@b
    invoke-virtual {v3, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItem(I)Landroid/view/MenuItem;

    #@e
    move-result-object v0

    #@f
    .line 530
    .local v0, "item":Landroid/view/MenuItem;
    if-eqz v0, :cond_0

    #@11
    .line 531
    invoke-interface {v0}, Landroid/view/MenuItem;->getSubMenu()Landroid/view/SubMenu;

    #@14
    move-result-object v2

    #@15
    check-cast v2, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@17
    .line 532
    .local v2, "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@1a
    .line 535
    .end local v0    # "item":Landroid/view/MenuItem;
    .end local v2    # "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    :cond_0
    return-void
.end method

.method public onSaveInstanceState()Landroid/os/Parcelable;
    .locals 2

    #@0
    .prologue
    .line 520
    new-instance v0, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;

    #@2
    invoke-direct {v0}, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;-><init>()V

    #@5
    .line 521
    .local v0, "state":Landroid/support/v7/widget/ActionMenuPresenter$SavedState;
    iget v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOpenSubMenuId:I

    #@7
    iput v1, v0, Landroid/support/v7/widget/ActionMenuPresenter$SavedState;->openSubMenuId:I

    #@9
    .line 522
    return-object v0
.end method

.method public onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z
    .locals 5
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 261
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->hasVisibleItems()Z

    #@4
    move-result v3

    #@5
    if-nez v3, :cond_1

    #@7
    .line 278
    :cond_0
    :goto_0
    return v2

    #@8
    .line 263
    :cond_1
    move-object v1, p1

    #@9
    .line 264
    .local v1, "topSubMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    :goto_1
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getParentMenu()Landroid/view/Menu;

    #@c
    move-result-object v3

    #@d
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@f
    if-eq v3, v4, :cond_2

    #@11
    .line 265
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getParentMenu()Landroid/view/Menu;

    #@14
    move-result-object v1

    #@15
    .end local v1    # "topSubMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    check-cast v1, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@17
    .restart local v1    # "topSubMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    goto :goto_1

    #@18
    .line 267
    :cond_2
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getItem()Landroid/view/MenuItem;

    #@1b
    move-result-object v3

    #@1c
    invoke-direct {p0, v3}, Landroid/support/v7/widget/ActionMenuPresenter;->findViewForItem(Landroid/view/MenuItem;)Landroid/view/View;

    #@1f
    move-result-object v0

    #@20
    .line 268
    .local v0, "anchor":Landroid/view/View;
    if-nez v0, :cond_3

    #@22
    .line 269
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@24
    if-eqz v3, :cond_0

    #@26
    .line 270
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@28
    .line 273
    :cond_3
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getItem()Landroid/view/MenuItem;

    #@2b
    move-result-object v2

    #@2c
    invoke-interface {v2}, Landroid/view/MenuItem;->getItemId()I

    #@2f
    move-result v2

    #@30
    iput v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOpenSubMenuId:I

    #@32
    .line 274
    new-instance v2, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@34
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mContext:Landroid/content/Context;

    #@36
    invoke-direct {v2, p0, v3, p1}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V

    #@39
    iput-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@3b
    .line 275
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@3d
    invoke-virtual {v2, v0}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->setAnchorView(Landroid/view/View;)V

    #@40
    .line 276
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@42
    invoke-virtual {v2}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->show()V

    #@45
    .line 277
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@48
    .line 278
    const/4 v2, 0x1

    #@49
    goto :goto_0
.end method

.method public onSubUiVisibilityChanged(Z)V
    .locals 2
    .param p1, "isVisible"    # Z

    #@0
    .prologue
    .line 539
    if-eqz p1, :cond_0

    #@2
    .line 541
    const/4 v0, 0x0

    #@3
    invoke-super {p0, v0}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@6
    .line 545
    :goto_0
    return-void

    #@7
    .line 543
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    const/4 v1, 0x0

    #@a
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@d
    goto :goto_0
.end method

.method public setExpandedActionViewsExclusive(Z)V
    .locals 0
    .param p1, "isExclusive"    # Z

    #@0
    .prologue
    .line 156
    iput-boolean p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mExpandedActionViewsExclusive:Z

    #@2
    .line 157
    return-void
.end method

.method public setItemLimit(I)V
    .locals 1
    .param p1, "itemCount"    # I

    #@0
    .prologue
    .line 151
    iput p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItems:I

    #@2
    .line 152
    const/4 v0, 0x1

    #@3
    iput-boolean v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMaxItemsSet:Z

    #@5
    .line 153
    return-void
.end method

.method public setMenuView(Landroid/support/v7/widget/ActionMenuView;)V
    .locals 1
    .param p1, "menuView"    # Landroid/support/v7/widget/ActionMenuView;

    #@0
    .prologue
    .line 548
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@2
    .line 549
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    invoke-virtual {p1, v0}, Landroid/support/v7/widget/ActionMenuView;->initialize(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@7
    .line 550
    return-void
.end method

.method public setReserveOverflow(Z)V
    .locals 1
    .param p1, "reserveOverflow"    # Z

    #@0
    .prologue
    .line 146
    iput-boolean p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@2
    .line 147
    const/4 v0, 0x1

    #@3
    iput-boolean v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflowSet:Z

    #@5
    .line 148
    return-void
.end method

.method public setWidthLimit(IZ)V
    .locals 1
    .param p1, "width"    # I
    .param p2, "strict"    # Z

    #@0
    .prologue
    .line 140
    iput p1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mWidthLimit:I

    #@2
    .line 141
    iput-boolean p2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mStrictWidthLimit:Z

    #@4
    .line 142
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mWidthLimitSet:Z

    #@7
    .line 143
    return-void
.end method

.method public shouldIncludeItem(ILandroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "childIndex"    # I
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 198
    invoke-virtual {p2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionButton()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public showOverflowMenu()Z
    .locals 6

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    .line 301
    iget-boolean v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@3
    if-eqz v1, :cond_0

    #@5
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@8
    move-result v1

    #@9
    if-nez v1, :cond_0

    #@b
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@d
    if-eqz v1, :cond_0

    #@f
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@11
    if-eqz v1, :cond_0

    #@13
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@15
    if-nez v1, :cond_0

    #@17
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@19
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getNonActionItems()Ljava/util/ArrayList;

    #@1c
    move-result-object v1

    #@1d
    invoke-virtual {v1}, Ljava/util/ArrayList;->isEmpty()Z

    #@20
    move-result v1

    #@21
    if-nez v1, :cond_0

    #@23
    .line 303
    new-instance v0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;

    #@25
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mContext:Landroid/content/Context;

    #@27
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@29
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@2b
    move-object v1, p0

    #@2c
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/View;Z)V

    #@2f
    .line 304
    .local v0, "popup":Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;
    new-instance v1, Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@31
    invoke-direct {v1, p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;)V

    #@34
    iput-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@36
    .line 306
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@38
    check-cast v1, Landroid/view/View;

    #@3a
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mPostedOpenRunnable:Landroid/support/v7/widget/ActionMenuPresenter$OpenOverflowRunnable;

    #@3c
    invoke-virtual {v1, v2}, Landroid/view/View;->post(Ljava/lang/Runnable;)Z

    #@3f
    .line 310
    const/4 v1, 0x0

    #@40
    invoke-super {p0, v1}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@43
    .line 314
    .end local v0    # "popup":Landroid/support/v7/widget/ActionMenuPresenter$OverflowPopup;
    :goto_0
    return v5

    #@44
    :cond_0
    const/4 v5, 0x0

    #@45
    goto :goto_0
.end method

.method public updateMenuView(Z)V
    .locals 12
    .param p1, "cleared"    # Z

    #@0
    .prologue
    const/4 v10, 0x1

    #@1
    const/4 v11, 0x0

    #@2
    .line 203
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@4
    check-cast v9, Landroid/view/View;

    #@6
    invoke-virtual {v9}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@9
    move-result-object v5

    #@a
    check-cast v5, Landroid/view/ViewGroup;

    #@c
    .line 204
    .local v5, "menuViewParent":Landroid/view/ViewGroup;
    if-eqz v5, :cond_0

    #@e
    .line 205
    invoke-static {v5}, Landroid/support/v7/internal/transition/ActionBarTransition;->beginDelayedTransition(Landroid/view/ViewGroup;)V

    #@11
    .line 207
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->updateMenuView(Z)V

    #@14
    .line 209
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@16
    check-cast v9, Landroid/view/View;

    #@18
    invoke-virtual {v9}, Landroid/view/View;->requestLayout()V

    #@1b
    .line 211
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@1d
    if-eqz v9, :cond_2

    #@1f
    .line 212
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@21
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getActionItems()Ljava/util/ArrayList;

    #@24
    move-result-object v0

    #@25
    .line 213
    .local v0, "actionItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@28
    move-result v1

    #@29
    .line 214
    .local v1, "count":I
    const/4 v3, 0x0

    #@2a
    .local v3, "i":I
    :goto_0
    if-ge v3, v1, :cond_2

    #@2c
    .line 215
    invoke-virtual {v0, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@2f
    move-result-object v9

    #@30
    check-cast v9, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@32
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getSupportActionProvider()Landroid/support/v4/view/ActionProvider;

    #@35
    move-result-object v8

    #@36
    .line 216
    .local v8, "provider":Landroid/support/v4/view/ActionProvider;
    if-eqz v8, :cond_1

    #@38
    .line 217
    invoke-virtual {v8, p0}, Landroid/support/v4/view/ActionProvider;->setSubUiVisibilityListener(Landroid/support/v4/view/ActionProvider$SubUiVisibilityListener;)V

    #@3b
    .line 214
    :cond_1
    add-int/lit8 v3, v3, 0x1

    #@3d
    goto :goto_0

    #@3e
    .line 222
    .end local v0    # "actionItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    .end local v1    # "count":I
    .end local v3    # "i":I
    .end local v8    # "provider":Landroid/support/v4/view/ActionProvider;
    :cond_2
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@40
    if-eqz v9, :cond_7

    #@42
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@44
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getNonActionItems()Ljava/util/ArrayList;

    #@47
    move-result-object v6

    #@48
    .line 225
    .local v6, "nonActionItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    :goto_1
    const/4 v2, 0x0

    #@49
    .line 226
    .local v2, "hasOverflow":Z
    iget-boolean v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@4b
    if-eqz v9, :cond_3

    #@4d
    if-eqz v6, :cond_3

    #@4f
    .line 227
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@52
    move-result v1

    #@53
    .line 228
    .restart local v1    # "count":I
    if-ne v1, v10, :cond_9

    #@55
    .line 229
    invoke-virtual {v6, v11}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@58
    move-result-object v9

    #@59
    check-cast v9, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@5b
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionViewExpanded()Z

    #@5e
    move-result v9

    #@5f
    if-nez v9, :cond_8

    #@61
    move v2, v10

    #@62
    .line 235
    .end local v1    # "count":I
    :cond_3
    :goto_2
    if-eqz v2, :cond_b

    #@64
    .line 236
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@66
    if-nez v9, :cond_4

    #@68
    .line 237
    new-instance v9, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;

    #@6a
    iget-object v10, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mSystemContext:Landroid/content/Context;

    #@6c
    invoke-direct {v9, p0, v10}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;)V

    #@6f
    iput-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@71
    .line 239
    :cond_4
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@73
    invoke-virtual {v9}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@76
    move-result-object v7

    #@77
    check-cast v7, Landroid/view/ViewGroup;

    #@79
    .line 240
    .local v7, "parent":Landroid/view/ViewGroup;
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@7b
    if-eq v7, v9, :cond_6

    #@7d
    .line 241
    if-eqz v7, :cond_5

    #@7f
    .line 242
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@81
    invoke-virtual {v7, v9}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@84
    .line 244
    :cond_5
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@86
    check-cast v4, Landroid/support/v7/widget/ActionMenuView;

    #@88
    .line 245
    .local v4, "menuView":Landroid/support/v7/widget/ActionMenuView;
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@8a
    invoke-virtual {v4}, Landroid/support/v7/widget/ActionMenuView;->generateOverflowButtonLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@8d
    move-result-object v10

    #@8e
    invoke-virtual {v4, v9, v10}, Landroid/support/v7/widget/ActionMenuView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@91
    .line 251
    .end local v4    # "menuView":Landroid/support/v7/widget/ActionMenuView;
    .end local v7    # "parent":Landroid/view/ViewGroup;
    :cond_6
    :goto_3
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@93
    check-cast v9, Landroid/support/v7/widget/ActionMenuView;

    #@95
    iget-boolean v10, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mReserveOverflow:Z

    #@97
    invoke-virtual {v9, v10}, Landroid/support/v7/widget/ActionMenuView;->setOverflowReserved(Z)V

    #@9a
    .line 252
    return-void

    #@9b
    .line 222
    .end local v2    # "hasOverflow":Z
    .end local v6    # "nonActionItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    :cond_7
    const/4 v6, 0x0

    #@9c
    goto :goto_1

    #@9d
    .restart local v1    # "count":I
    .restart local v2    # "hasOverflow":Z
    .restart local v6    # "nonActionItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    :cond_8
    move v2, v11

    #@9e
    .line 229
    goto :goto_2

    #@9f
    .line 231
    :cond_9
    if-lez v1, :cond_a

    #@a1
    move v2, v10

    #@a2
    :goto_4
    goto :goto_2

    #@a3
    :cond_a
    move v2, v11

    #@a4
    goto :goto_4

    #@a5
    .line 247
    .end local v1    # "count":I
    :cond_b
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@a7
    if-eqz v9, :cond_6

    #@a9
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@ab
    invoke-virtual {v9}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@ae
    move-result-object v9

    #@af
    iget-object v10, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@b1
    if-ne v9, v10, :cond_6

    #@b3
    .line 248
    iget-object v9, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@b5
    check-cast v9, Landroid/view/ViewGroup;

    #@b7
    iget-object v10, p0, Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;

    #@b9
    invoke-virtual {v9, v10}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@bc
    goto :goto_3
.end method
