.class public Landroid/support/v7/internal/view/menu/SubMenuBuilder;
.super Landroid/support/v7/internal/view/menu/MenuBuilder;
.source "SubMenuBuilder.java"

# interfaces
.implements Landroid/view/SubMenu;


# instance fields
.field private mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

.field private mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "parentMenu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p3, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 38
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;-><init>(Landroid/content/Context;)V

    #@3
    .line 40
    iput-object p2, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5
    .line 41
    iput-object p3, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@7
    .line 42
    return-void
.end method


# virtual methods
.method public collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 130
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method dispatchMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 84
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@8
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_1

    #@e
    :cond_0
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public expandItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 1
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 125
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->expandItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getActionViewStatesKey()Ljava/lang/String;
    .locals 3

    #@0
    .prologue
    .line 135
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    if-eqz v1, :cond_0

    #@4
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@6
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getItemId()I

    #@9
    move-result v0

    #@a
    .line 136
    .local v0, "itemId":I
    :goto_0
    if-nez v0, :cond_1

    #@c
    .line 137
    const/4 v1, 0x0

    #@d
    .line 139
    :goto_1
    return-object v1

    #@e
    .line 135
    .end local v0    # "itemId":I
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0

    #@10
    .line 139
    .restart local v0    # "itemId":I
    :cond_1
    new-instance v1, Ljava/lang/StringBuilder;

    #@12
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@15
    invoke-super {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getActionViewStatesKey()Ljava/lang/String;

    #@18
    move-result-object v2

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    const-string v2, ":"

    #@1f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v1

    #@23
    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@26
    move-result-object v1

    #@27
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2a
    move-result-object v1

    #@2b
    goto :goto_1
.end method

.method public getItem()Landroid/view/MenuItem;
    .locals 1

    #@0
    .prologue
    .line 69
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    return-object v0
.end method

.method public getParentMenu()Landroid/view/Menu;
    .locals 1

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method public getRootMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 1

    #@0
    .prologue
    .line 79
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method public isQwertyMode()Z
    .locals 1

    #@0
    .prologue
    .line 51
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->isQwertyMode()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isShortcutsVisible()Z
    .locals 1

    #@0
    .prologue
    .line 61
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->isShortcutsVisible()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V
    .locals 1
    .param p1, "callback"    # Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@0
    .prologue
    .line 74
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@5
    .line 75
    return-void
.end method

.method public setHeaderIcon(I)Landroid/view/SubMenu;
    .locals 1
    .param p1, "iconRes"    # I

    #@0
    .prologue
    .line 104
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    invoke-super {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderIconInt(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@b
    .line 105
    return-object p0
.end method

.method public setHeaderIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/SubMenu;
    .locals 0
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 99
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderIconInt(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3
    .line 100
    return-object p0
.end method

.method public setHeaderTitle(I)Landroid/view/SubMenu;
    .locals 1
    .param p1, "titleRes"    # I

    #@0
    .prologue
    .line 114
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@b
    move-result-object v0

    #@c
    invoke-super {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderTitleInt(Ljava/lang/CharSequence;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@f
    .line 115
    return-object p0
.end method

.method public setHeaderTitle(Ljava/lang/CharSequence;)Landroid/view/SubMenu;
    .locals 0
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 109
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderTitleInt(Ljava/lang/CharSequence;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3
    .line 110
    return-object p0
.end method

.method public setHeaderView(Landroid/view/View;)Landroid/view/SubMenu;
    .locals 0
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 119
    invoke-super {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderViewInt(Landroid/view/View;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3
    .line 120
    return-object p0
.end method

.method public setIcon(I)Landroid/view/SubMenu;
    .locals 1
    .param p1, "iconRes"    # I

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIcon(I)Landroid/view/MenuItem;

    #@5
    .line 95
    return-object p0
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/SubMenu;
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;

    #@5
    .line 90
    return-object p0
.end method

.method public setQwertyMode(Z)V
    .locals 1
    .param p1, "isQwerty"    # Z

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setQwertyMode(Z)V

    #@5
    .line 47
    return-void
.end method

.method public setShortcutsVisible(Z)V
    .locals 1
    .param p1, "shortcutsVisible"    # Z

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->mParentMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setShortcutsVisible(Z)V

    #@5
    .line 57
    return-void
.end method
