.class public Landroid/support/v7/internal/view/menu/ListMenuPresenter;
.super Ljava/lang/Object;
.source "ListMenuPresenter.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuPresenter;
.implements Landroid/widget/AdapterView$OnItemClickListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "ListMenuPresenter"

.field public static final VIEWS_TAG:Ljava/lang/String; = "android:menu:list"


# instance fields
.field mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

.field private mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

.field mContext:Landroid/content/Context;

.field private mId:I

.field mInflater:Landroid/view/LayoutInflater;

.field private mItemIndexOffset:I

.field mItemLayoutRes:I

.field mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

.field mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

.field mThemeRes:I


# direct methods
.method public constructor <init>(II)V
    .locals 0
    .param p1, "itemLayoutRes"    # I
    .param p2, "themeRes"    # I

    #@0
    .prologue
    .line 77
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 78
    iput p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mItemLayoutRes:I

    #@5
    .line 79
    iput p2, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mThemeRes:I

    #@7
    .line 80
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "itemLayoutRes"    # I

    #@0
    .prologue
    .line 67
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p2, v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;-><init>(II)V

    #@4
    .line 68
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@6
    .line 69
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@8
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@b
    move-result-object v0

    #@c
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@e
    .line 70
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@0
    .prologue
    .line 40
    iget v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mItemIndexOffset:I

    #@2
    return v0
.end method


# virtual methods
.method public collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 182
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public expandItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 178
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public flagActionItems()Z
    .locals 1

    #@0
    .prologue
    .line 174
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getAdapter()Landroid/widget/ListAdapter;
    .locals 1

    #@0
    .prologue
    .line 121
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 122
    new-instance v0, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@6
    invoke-direct {v0, p0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;-><init>(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)V

    #@9
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@b
    .line 124
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@d
    return-object v0
.end method

.method public getId()I
    .locals 1

    #@0
    .prologue
    .line 206
    iget v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mId:I

    #@2
    return v0
.end method

.method getItemIndexOffset()I
    .locals 1

    #@0
    .prologue
    .line 157
    iget v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mItemIndexOffset:I

    #@2
    return v0
.end method

.method public getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;
    .locals 3
    .param p1, "root"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 101
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@2
    if-nez v0, :cond_1

    #@4
    .line 102
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@6
    sget v1, Landroid/support/v7/appcompat/R$layout;->abc_expanded_menu_layout:I

    #@8
    const/4 v2, 0x0

    #@9
    invoke-virtual {v0, v1, p1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@f
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@11
    .line 104
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@13
    if-nez v0, :cond_0

    #@15
    .line 105
    new-instance v0, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@17
    invoke-direct {v0, p0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;-><init>(Landroid/support/v7/internal/view/menu/ListMenuPresenter;)V

    #@1a
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@1c
    .line 107
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@1e
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@20
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/ExpandedMenuView;->setAdapter(Landroid/widget/ListAdapter;)V

    #@23
    .line 108
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@25
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/view/menu/ExpandedMenuView;->setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V

    #@28
    .line 110
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@2a
    return-object v0
.end method

.method public initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 84
    iget v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mThemeRes:I

    #@2
    if-eqz v0, :cond_2

    #@4
    .line 85
    new-instance v0, Landroid/view/ContextThemeWrapper;

    #@6
    iget v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mThemeRes:I

    #@8
    invoke-direct {v0, p1, v1}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@d
    .line 86
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@f
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@12
    move-result-object v0

    #@13
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@15
    .line 93
    :cond_0
    :goto_0
    iput-object p2, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@17
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@19
    if-eqz v0, :cond_1

    #@1b
    .line 95
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@1d
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;->notifyDataSetChanged()V

    #@20
    .line 97
    :cond_1
    return-void

    #@21
    .line 87
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@23
    if-eqz v0, :cond_0

    #@25
    .line 88
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@27
    .line 89
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@29
    if-nez v0, :cond_0

    #@2b
    .line 90
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mContext:Landroid/content/Context;

    #@2d
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@30
    move-result-object v0

    #@31
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mInflater:Landroid/view/LayoutInflater;

    #@33
    goto :goto_0
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 151
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 152
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@6
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@9
    .line 154
    :cond_0
    return-void
.end method

.method public onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V
    .locals 3
    .param p2, "view"    # Landroid/view/View;
    .param p3, "position"    # I
    .param p4, "id"    # J
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/widget/AdapterView",
            "<*>;",
            "Landroid/view/View;",
            "IJ)V"
        }
    .end annotation

    #@0
    .prologue
    .line 169
    .local p1, "parent":Landroid/widget/AdapterView;, "Landroid/widget/AdapterView<*>;"
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@4
    invoke-virtual {v1, p3}, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;->getItem(I)Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@7
    move-result-object v1

    #@8
    const/4 v2, 0x0

    #@9
    invoke-virtual {v0, v1, p0, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;Landroid/support/v7/internal/view/menu/MenuPresenter;I)Z

    #@c
    .line 170
    return-void
.end method

.method public onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 0
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 222
    check-cast p1, Landroid/os/Bundle;

    #@2
    .end local p1    # "state":Landroid/os/Parcelable;
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->restoreHierarchyState(Landroid/os/Bundle;)V

    #@5
    .line 223
    return-void
.end method

.method public onSaveInstanceState()Landroid/os/Parcelable;
    .locals 2

    #@0
    .prologue
    .line 211
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 212
    const/4 v0, 0x0

    #@5
    .line 217
    :goto_0
    return-object v0

    #@6
    .line 215
    :cond_0
    new-instance v0, Landroid/os/Bundle;

    #@8
    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    #@b
    .line 216
    .local v0, "state":Landroid/os/Bundle;
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->saveHierarchyState(Landroid/os/Bundle;)V

    #@e
    goto :goto_0
.end method

.method public onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z
    .locals 2
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    .line 139
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->hasVisibleItems()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    const/4 v0, 0x0

    #@7
    .line 146
    :goto_0
    return v0

    #@8
    .line 142
    :cond_0
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;

    #@a
    invoke-direct {v0, p1}, Landroid/support/v7/internal/view/menu/MenuDialogHelper;-><init>(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@d
    const/4 v1, 0x0

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->show(Landroid/os/IBinder;)V

    #@11
    .line 143
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@13
    if-eqz v0, :cond_1

    #@15
    .line 144
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@17
    invoke-interface {v0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z

    #@1a
    .line 146
    :cond_1
    const/4 v0, 0x1

    #@1b
    goto :goto_0
.end method

.method public restoreHierarchyState(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "inState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 194
    const-string v1, "android:menu:list"

    #@2
    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@5
    move-result-object v0

    #@6
    .line 195
    .local v0, "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    if-eqz v0, :cond_0

    #@8
    .line 196
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@a
    invoke-virtual {v1, v0}, Landroid/view/View;->restoreHierarchyState(Landroid/util/SparseArray;)V

    #@d
    .line 198
    :cond_0
    return-void
.end method

.method public saveHierarchyState(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 186
    new-instance v0, Landroid/util/SparseArray;

    #@2
    invoke-direct {v0}, Landroid/util/SparseArray;-><init>()V

    #@5
    .line 187
    .local v0, "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@7
    if-eqz v1, :cond_0

    #@9
    .line 188
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@b
    invoke-virtual {v1, v0}, Landroid/view/View;->saveHierarchyState(Landroid/util/SparseArray;)V

    #@e
    .line 190
    :cond_0
    const-string v1, "android:menu:list"

    #@10
    invoke-virtual {p1, v1, v0}, Landroid/os/Bundle;->putSparseParcelableArray(Ljava/lang/String;Landroid/util/SparseArray;)V

    #@13
    .line 191
    return-void
.end method

.method public setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V
    .locals 0
    .param p1, "cb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@0
    .prologue
    .line 134
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    .line 135
    return-void
.end method

.method public setId(I)V
    .locals 0
    .param p1, "id"    # I

    #@0
    .prologue
    .line 201
    iput p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mId:I

    #@2
    .line 202
    return-void
.end method

.method public setItemIndexOffset(I)V
    .locals 1
    .param p1, "offset"    # I

    #@0
    .prologue
    .line 161
    iput p1, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mItemIndexOffset:I

    #@2
    .line 162
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/ExpandedMenuView;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 163
    const/4 v0, 0x0

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->updateMenuView(Z)V

    #@a
    .line 165
    :cond_0
    return-void
.end method

.method public updateMenuView(Z)V
    .locals 1
    .param p1, "cleared"    # Z

    #@0
    .prologue
    .line 129
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->mAdapter:Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter$MenuAdapter;->notifyDataSetChanged()V

    #@9
    .line 130
    :cond_0
    return-void
.end method
