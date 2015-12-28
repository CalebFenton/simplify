.class public abstract Landroid/support/v7/internal/view/menu/BaseMenuPresenter;
.super Ljava/lang/Object;
.source "BaseMenuPresenter.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuPresenter;


# instance fields
.field private mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

.field protected mContext:Landroid/content/Context;

.field private mId:I

.field protected mInflater:Landroid/view/LayoutInflater;

.field private mItemLayoutRes:I

.field protected mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

.field private mMenuLayoutRes:I

.field protected mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

.field protected mSystemContext:Landroid/content/Context;

.field protected mSystemInflater:Landroid/view/LayoutInflater;


# direct methods
.method public constructor <init>(Landroid/content/Context;II)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "menuLayoutRes"    # I
    .param p3, "itemLayoutRes"    # I

    #@0
    .prologue
    .line 57
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 58
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mSystemContext:Landroid/content/Context;

    #@5
    .line 59
    invoke-static {p1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@8
    move-result-object v0

    #@9
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mSystemInflater:Landroid/view/LayoutInflater;

    #@b
    .line 60
    iput p2, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuLayoutRes:I

    #@d
    .line 61
    iput p3, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mItemLayoutRes:I

    #@f
    .line 62
    return-void
.end method


# virtual methods
.method protected addItemView(Landroid/view/View;I)V
    .locals 2
    .param p1, "itemView"    # Landroid/view/View;
    .param p2, "childIndex"    # I

    #@0
    .prologue
    .line 129
    invoke-virtual {p1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/view/ViewGroup;

    #@6
    .line 130
    .local v0, "currentParent":Landroid/view/ViewGroup;
    if-eqz v0, :cond_0

    #@8
    .line 131
    invoke-virtual {v0, p1}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@b
    .line 133
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@d
    check-cast v1, Landroid/view/ViewGroup;

    #@f
    invoke-virtual {v1, p1, p2}, Landroid/view/ViewGroup;->addView(Landroid/view/View;I)V

    #@12
    .line 134
    return-void
.end method

.method public abstract bindItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/support/v7/internal/view/menu/MenuView$ItemView;)V
.end method

.method public collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 227
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public createItemView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView$ItemView;
    .locals 3
    .param p1, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 161
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mSystemInflater:Landroid/view/LayoutInflater;

    #@2
    iget v1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mItemLayoutRes:I

    #@4
    const/4 v2, 0x0

    #@5
    invoke-virtual {v0, v1, p1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@8
    move-result-object v0

    #@9
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@b
    return-object v0
.end method

.method public expandItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 223
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method protected filterLeftoverView(Landroid/view/ViewGroup;I)Z
    .locals 1
    .param p1, "parent"    # Landroid/view/ViewGroup;
    .param p2, "childIndex"    # I

    #@0
    .prologue
    .line 143
    invoke-virtual {p1, p2}, Landroid/view/ViewGroup;->removeViewAt(I)V

    #@3
    .line 144
    const/4 v0, 0x1

    #@4
    return v0
.end method

.method public flagActionItems()Z
    .locals 1

    #@0
    .prologue
    .line 219
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getCallback()Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;
    .locals 1

    #@0
    .prologue
    .line 152
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    return-object v0
.end method

.method public getId()I
    .locals 1

    #@0
    .prologue
    .line 231
    iget v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mId:I

    #@2
    return v0
.end method

.method public getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 2
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 177
    instance-of v1, p2, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@2
    if-eqz v1, :cond_0

    #@4
    move-object v0, p2

    #@5
    .line 178
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@7
    .line 182
    .local v0, "itemView":Landroid/support/v7/internal/view/menu/MenuView$ItemView;
    :goto_0
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->bindItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/support/v7/internal/view/menu/MenuView$ItemView;)V

    #@a
    .line 183
    check-cast v0, Landroid/view/View;

    #@c
    .end local v0    # "itemView":Landroid/support/v7/internal/view/menu/MenuView$ItemView;
    return-object v0

    #@d
    .line 180
    :cond_0
    invoke-virtual {p0, p3}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->createItemView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@10
    move-result-object v0

    #@11
    .restart local v0    # "itemView":Landroid/support/v7/internal/view/menu/MenuView$ItemView;
    goto :goto_0
.end method

.method public getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;
    .locals 3
    .param p1, "root"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 73
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 74
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mSystemInflater:Landroid/view/LayoutInflater;

    #@6
    iget v1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuLayoutRes:I

    #@8
    const/4 v2, 0x0

    #@9
    invoke-virtual {v0, v1, p1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuView;

    #@f
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@11
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@13
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@15
    invoke-interface {v0, v1}, Landroid/support/v7/internal/view/menu/MenuView;->initialize(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@18
    .line 76
    const/4 v0, 0x1

    #@19
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->updateMenuView(Z)V

    #@1c
    .line 79
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@1e
    return-object v0
.end method

.method public initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 66
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mContext:Landroid/content/Context;

    #@2
    .line 67
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mContext:Landroid/content/Context;

    #@4
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@7
    move-result-object v0

    #@8
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@a
    .line 68
    iput-object p2, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@c
    .line 69
    return-void
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 206
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 207
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@6
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@9
    .line 209
    :cond_0
    return-void
.end method

.method public onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    .line 212
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 213
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z

    #@9
    move-result v0

    #@a
    .line 215
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V
    .locals 0
    .param p1, "cb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@0
    .prologue
    .line 148
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    .line 149
    return-void
.end method

.method public setId(I)V
    .locals 0
    .param p1, "id"    # I

    #@0
    .prologue
    .line 235
    iput p1, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mId:I

    #@2
    .line 236
    return-void
.end method

.method public shouldIncludeItem(ILandroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "childIndex"    # I
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 202
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public updateMenuView(Z)V
    .locals 10
    .param p1, "cleared"    # Z

    #@0
    .prologue
    .line 86
    iget-object v7, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;

    #@2
    check-cast v7, Landroid/view/ViewGroup;

    #@4
    .line 87
    .local v7, "parent":Landroid/view/ViewGroup;
    if-nez v7, :cond_1

    #@6
    .line 120
    :cond_0
    return-void

    #@7
    .line 89
    :cond_1
    const/4 v0, 0x0

    #@8
    .line 90
    .local v0, "childIndex":I
    iget-object v9, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@a
    if-eqz v9, :cond_6

    #@c
    .line 91
    iget-object v9, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@e
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuBuilder;->flagActionItems()V

    #@11
    .line 92
    iget-object v9, p0, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@13
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getVisibleItems()Ljava/util/ArrayList;

    #@16
    move-result-object v8

    #@17
    .line 93
    .local v8, "visibleItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@1a
    move-result v4

    #@1b
    .line 94
    .local v4, "itemCount":I
    const/4 v2, 0x0

    #@1c
    .local v2, "i":I
    :goto_0
    if-ge v2, v4, :cond_6

    #@1e
    .line 95
    invoke-virtual {v8, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@21
    move-result-object v3

    #@22
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@24
    .line 96
    .local v3, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {p0, v0, v3}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->shouldIncludeItem(ILandroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@27
    move-result v9

    #@28
    if-eqz v9, :cond_4

    #@2a
    .line 97
    invoke-virtual {v7, v0}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@2d
    move-result-object v1

    #@2e
    .line 98
    .local v1, "convertView":Landroid/view/View;
    instance-of v9, v1, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@30
    if-eqz v9, :cond_5

    #@32
    move-object v9, v1

    #@33
    check-cast v9, Landroid/support/v7/internal/view/menu/MenuView$ItemView;

    #@35
    invoke-interface {v9}, Landroid/support/v7/internal/view/menu/MenuView$ItemView;->getItemData()Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@38
    move-result-object v6

    #@39
    .line 100
    .local v6, "oldItem":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    invoke-virtual {p0, v3, v1, v7}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->getItemView(Landroid/support/v7/internal/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@3c
    move-result-object v5

    #@3d
    .line 101
    .local v5, "itemView":Landroid/view/View;
    if-eq v3, v6, :cond_2

    #@3f
    .line 103
    const/4 v9, 0x0

    #@40
    invoke-virtual {v5, v9}, Landroid/view/View;->setPressed(Z)V

    #@43
    .line 104
    invoke-static {v5}, Landroid/support/v4/view/ViewCompat;->jumpDrawablesToCurrentState(Landroid/view/View;)V

    #@46
    .line 106
    :cond_2
    if-eq v5, v1, :cond_3

    #@48
    .line 107
    invoke-virtual {p0, v5, v0}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->addItemView(Landroid/view/View;I)V

    #@4b
    .line 109
    :cond_3
    add-int/lit8 v0, v0, 0x1

    #@4d
    .line 94
    .end local v1    # "convertView":Landroid/view/View;
    .end local v5    # "itemView":Landroid/view/View;
    .end local v6    # "oldItem":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_4
    add-int/lit8 v2, v2, 0x1

    #@4f
    goto :goto_0

    #@50
    .line 98
    .restart local v1    # "convertView":Landroid/view/View;
    :cond_5
    const/4 v6, 0x0

    #@51
    goto :goto_1

    #@52
    .line 115
    .end local v1    # "convertView":Landroid/view/View;
    .end local v2    # "i":I
    .end local v3    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .end local v4    # "itemCount":I
    .end local v8    # "visibleItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    :cond_6
    :goto_2
    invoke-virtual {v7}, Landroid/view/ViewGroup;->getChildCount()I

    #@55
    move-result v9

    #@56
    if-ge v0, v9, :cond_0

    #@58
    .line 116
    invoke-virtual {p0, v7, v0}, Landroid/support/v7/internal/view/menu/BaseMenuPresenter;->filterLeftoverView(Landroid/view/ViewGroup;I)Z

    #@5b
    move-result v9

    #@5c
    if-nez v9, :cond_6

    #@5e
    .line 117
    add-int/lit8 v0, v0, 0x1

    #@60
    goto :goto_2
.end method
