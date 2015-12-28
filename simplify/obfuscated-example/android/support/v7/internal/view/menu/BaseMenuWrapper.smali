.class abstract Landroid/support/v7/internal/view/menu/BaseMenuWrapper;
.super Landroid/support/v7/internal/view/menu/BaseWrapper;
.source "BaseMenuWrapper.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Landroid/support/v7/internal/view/menu/BaseWrapper",
        "<TT;>;"
    }
.end annotation


# instance fields
.field final mContext:Landroid/content/Context;

.field private mMenuItems:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Landroid/support/v4/internal/view/SupportMenuItem;",
            "Landroid/view/MenuItem;",
            ">;"
        }
    .end annotation
.end field

.field private mSubMenus:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Landroid/support/v4/internal/view/SupportSubMenu;",
            "Landroid/view/SubMenu;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>(Landroid/content/Context;Ljava/lang/Object;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "TT;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 37
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    .local p2, "object":Ljava/lang/Object;, "TT;"
    invoke-direct {p0, p2}, Landroid/support/v7/internal/view/menu/BaseWrapper;-><init>(Ljava/lang/Object;)V

    #@3
    .line 38
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mContext:Landroid/content/Context;

    #@5
    .line 39
    return-void
.end method


# virtual methods
.method final getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;
    .locals 3
    .param p1, "menuItem"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 42
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    instance-of v2, p1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v2, :cond_2

    #@4
    move-object v0, p1

    #@5
    .line 43
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@7
    .line 46
    .local v0, "supportMenuItem":Landroid/support/v4/internal/view/SupportMenuItem;
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@9
    if-nez v2, :cond_0

    #@b
    .line 47
    new-instance v2, Landroid/support/v4/util/ArrayMap;

    #@d
    invoke-direct {v2}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@10
    iput-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@12
    .line 51
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@14
    invoke-interface {v2, p1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@17
    move-result-object v1

    #@18
    check-cast v1, Landroid/view/MenuItem;

    #@1a
    .line 53
    .local v1, "wrappedItem":Landroid/view/MenuItem;
    if-nez v1, :cond_1

    #@1c
    .line 55
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mContext:Landroid/content/Context;

    #@1e
    invoke-static {v2, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperFactory;->wrapSupportMenuItem(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)Landroid/view/MenuItem;

    #@21
    move-result-object v1

    #@22
    .line 56
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@24
    invoke-interface {v2, v0, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@27
    .line 61
    .end local v0    # "supportMenuItem":Landroid/support/v4/internal/view/SupportMenuItem;
    .end local v1    # "wrappedItem":Landroid/view/MenuItem;
    :cond_1
    :goto_0
    return-object v1

    #@28
    :cond_2
    move-object v1, p1

    #@29
    goto :goto_0
.end method

.method final getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;
    .locals 3
    .param p1, "subMenu"    # Landroid/view/SubMenu;

    #@0
    .prologue
    .line 65
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    instance-of v2, p1, Landroid/support/v4/internal/view/SupportSubMenu;

    #@2
    if-eqz v2, :cond_2

    #@4
    move-object v0, p1

    #@5
    .line 66
    check-cast v0, Landroid/support/v4/internal/view/SupportSubMenu;

    #@7
    .line 69
    .local v0, "supportSubMenu":Landroid/support/v4/internal/view/SupportSubMenu;
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@9
    if-nez v2, :cond_0

    #@b
    .line 70
    new-instance v2, Landroid/support/v4/util/ArrayMap;

    #@d
    invoke-direct {v2}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@10
    iput-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@12
    .line 73
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@14
    invoke-interface {v2, v0}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@17
    move-result-object v1

    #@18
    check-cast v1, Landroid/view/SubMenu;

    #@1a
    .line 75
    .local v1, "wrappedMenu":Landroid/view/SubMenu;
    if-nez v1, :cond_1

    #@1c
    .line 76
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mContext:Landroid/content/Context;

    #@1e
    invoke-static {v2, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperFactory;->wrapSupportSubMenu(Landroid/content/Context;Landroid/support/v4/internal/view/SupportSubMenu;)Landroid/view/SubMenu;

    #@21
    move-result-object v1

    #@22
    .line 77
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@24
    invoke-interface {v2, v0, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@27
    .line 81
    .end local v0    # "supportSubMenu":Landroid/support/v4/internal/view/SupportSubMenu;
    .end local v1    # "wrappedMenu":Landroid/view/SubMenu;
    :cond_1
    :goto_0
    return-object v1

    #@28
    :cond_2
    move-object v1, p1

    #@29
    goto :goto_0
.end method

.method final internalClear()V
    .locals 1

    #@0
    .prologue
    .line 86
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 87
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@6
    invoke-interface {v0}, Ljava/util/Map;->clear()V

    #@9
    .line 89
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@b
    if-eqz v0, :cond_1

    #@d
    .line 90
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mSubMenus:Ljava/util/Map;

    #@f
    invoke-interface {v0}, Ljava/util/Map;->clear()V

    #@12
    .line 92
    :cond_1
    return-void
.end method

.method final internalRemoveGroup(I)V
    .locals 3
    .param p1, "groupId"    # I

    #@0
    .prologue
    .line 95
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@2
    if-nez v2, :cond_1

    #@4
    .line 108
    :cond_0
    return-void

    #@5
    .line 99
    :cond_1
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@7
    invoke-interface {v2}, Ljava/util/Map;->keySet()Ljava/util/Set;

    #@a
    move-result-object v2

    #@b
    invoke-interface {v2}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    #@e
    move-result-object v0

    #@f
    .line 102
    .local v0, "iterator":Ljava/util/Iterator;, "Ljava/util/Iterator<Landroid/support/v4/internal/view/SupportMenuItem;>;"
    :cond_2
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@12
    move-result v2

    #@13
    if-eqz v2, :cond_0

    #@15
    .line 103
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@18
    move-result-object v1

    #@19
    check-cast v1, Landroid/view/MenuItem;

    #@1b
    .line 104
    .local v1, "menuItem":Landroid/view/MenuItem;
    invoke-interface {v1}, Landroid/view/MenuItem;->getGroupId()I

    #@1e
    move-result v2

    #@1f
    if-ne p1, v2, :cond_2

    #@21
    .line 105
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    #@24
    goto :goto_0
.end method

.method final internalRemoveItem(I)V
    .locals 3
    .param p1, "id"    # I

    #@0
    .prologue
    .line 111
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseMenuWrapper;, "Landroid/support/v7/internal/view/menu/BaseMenuWrapper<TT;>;"
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@2
    if-nez v2, :cond_1

    #@4
    .line 125
    :cond_0
    :goto_0
    return-void

    #@5
    .line 115
    :cond_1
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;->mMenuItems:Ljava/util/Map;

    #@7
    invoke-interface {v2}, Ljava/util/Map;->keySet()Ljava/util/Set;

    #@a
    move-result-object v2

    #@b
    invoke-interface {v2}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    #@e
    move-result-object v0

    #@f
    .line 118
    .local v0, "iterator":Ljava/util/Iterator;, "Ljava/util/Iterator<Landroid/support/v4/internal/view/SupportMenuItem;>;"
    :cond_2
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@12
    move-result v2

    #@13
    if-eqz v2, :cond_0

    #@15
    .line 119
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@18
    move-result-object v1

    #@19
    check-cast v1, Landroid/view/MenuItem;

    #@1b
    .line 120
    .local v1, "menuItem":Landroid/view/MenuItem;
    invoke-interface {v1}, Landroid/view/MenuItem;->getItemId()I

    #@1e
    move-result v2

    #@1f
    if-ne p1, v2, :cond_2

    #@21
    .line 121
    invoke-interface {v0}, Ljava/util/Iterator;->remove()V

    #@24
    goto :goto_0
.end method
