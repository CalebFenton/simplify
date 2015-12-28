.class Landroid/support/v7/internal/view/menu/MenuWrapperICS;
.super Landroid/support/v7/internal/view/menu/BaseMenuWrapper;
.source "MenuWrapperICS.java"

# interfaces
.implements Landroid/view/Menu;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/support/v7/internal/view/menu/BaseMenuWrapper",
        "<",
        "Landroid/support/v4/internal/view/SupportMenu;",
        ">;",
        "Landroid/view/Menu;"
    }
.end annotation


# direct methods
.method constructor <init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenu;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "object"    # Landroid/support/v4/internal/view/SupportMenu;

    #@0
    .prologue
    .line 36
    invoke-direct {p0, p1, p2}, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;-><init>(Landroid/content/Context;Ljava/lang/Object;)V

    #@3
    .line 37
    return-void
.end method


# virtual methods
.method public add(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "titleRes"    # I

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->add(I)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public add(IIII)Landroid/view/MenuItem;
    .locals 1
    .param p1, "groupId"    # I
    .param p2, "itemId"    # I
    .param p3, "order"    # I
    .param p4, "titleRes"    # I

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3, p4}, Landroid/support/v4/internal/view/SupportMenu;->add(IIII)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "groupId"    # I
    .param p2, "itemId"    # I
    .param p3, "order"    # I
    .param p4, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 51
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3, p4}, Landroid/support/v4/internal/view/SupportMenu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public add(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 41
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->add(Ljava/lang/CharSequence;)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public addIntentOptions(IIILandroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I[Landroid/view/MenuItem;)I
    .locals 13
    .param p1, "groupId"    # I
    .param p2, "itemId"    # I
    .param p3, "order"    # I
    .param p4, "caller"    # Landroid/content/ComponentName;
    .param p5, "specifics"    # [Landroid/content/Intent;
    .param p6, "intent"    # Landroid/content/Intent;
    .param p7, "flags"    # I
    .param p8, "outSpecificItems"    # [Landroid/view/MenuItem;

    #@0
    .prologue
    .line 83
    const/4 v9, 0x0

    #@1
    .line 84
    .local v9, "items":[Landroid/view/MenuItem;
    if-eqz p8, :cond_0

    #@3
    .line 85
    move-object/from16 v0, p8

    #@5
    array-length v1, v0

    #@6
    new-array v9, v1, [Landroid/view/MenuItem;

    #@8
    .line 88
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@a
    check-cast v1, Landroid/support/v4/internal/view/SupportMenu;

    #@c
    move v2, p1

    #@d
    move v3, p2

    #@e
    move/from16 v4, p3

    #@10
    move-object/from16 v5, p4

    #@12
    move-object/from16 v6, p5

    #@14
    move-object/from16 v7, p6

    #@16
    move/from16 v8, p7

    #@18
    invoke-interface/range {v1 .. v9}, Landroid/support/v4/internal/view/SupportMenu;->addIntentOptions(IIILandroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I[Landroid/view/MenuItem;)I

    #@1b
    move-result v11

    #@1c
    .line 91
    .local v11, "result":I
    if-eqz v9, :cond_1

    #@1e
    .line 92
    const/4 v10, 0x0

    #@1f
    .local v10, "i":I
    array-length v12, v9

    #@20
    .local v12, "z":I
    :goto_0
    if-ge v10, v12, :cond_1

    #@22
    .line 93
    aget-object v1, v9, v10

    #@24
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@27
    move-result-object v1

    #@28
    aput-object v1, p8, v10

    #@2a
    .line 92
    add-int/lit8 v10, v10, 0x1

    #@2c
    goto :goto_0

    #@2d
    .line 97
    .end local v10    # "i":I
    .end local v12    # "z":I
    :cond_1
    return v11
.end method

.method public addSubMenu(I)Landroid/view/SubMenu;
    .locals 1
    .param p1, "titleRes"    # I

    #@0
    .prologue
    .line 66
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->addSubMenu(I)Landroid/view/SubMenu;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public addSubMenu(IIII)Landroid/view/SubMenu;
    .locals 1
    .param p1, "groupId"    # I
    .param p2, "itemId"    # I
    .param p3, "order"    # I
    .param p4, "titleRes"    # I

    #@0
    .prologue
    .line 76
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3, p4}, Landroid/support/v4/internal/view/SupportMenu;->addSubMenu(IIII)Landroid/view/SubMenu;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;
    .locals 1
    .param p1, "groupId"    # I
    .param p2, "itemId"    # I
    .param p3, "order"    # I
    .param p4, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 71
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3, p4}, Landroid/support/v4/internal/view/SupportMenu;->addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public addSubMenu(Ljava/lang/CharSequence;)Landroid/view/SubMenu;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 61
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->addSubMenu(Ljava/lang/CharSequence;)Landroid/view/SubMenu;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public clear()V
    .locals 1

    #@0
    .prologue
    .line 114
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->internalClear()V

    #@3
    .line 115
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@5
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@7
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenu;->clear()V

    #@a
    .line 116
    return-void
.end method

.method public close()V
    .locals 1

    #@0
    .prologue
    .line 155
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenu;->close()V

    #@7
    .line 156
    return-void
.end method

.method public findItem(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "id"    # I

    #@0
    .prologue
    .line 140
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->findItem(I)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public getItem(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 150
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->getItem(I)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->getMenuItemWrapper(Landroid/view/MenuItem;)Landroid/view/MenuItem;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public hasVisibleItems()Z
    .locals 1

    #@0
    .prologue
    .line 135
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenu;->hasVisibleItems()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isShortcutKey(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 165
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2}, Landroid/support/v4/internal/view/SupportMenu;->isShortcutKey(ILandroid/view/KeyEvent;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public performIdentifierAction(II)Z
    .locals 1
    .param p1, "id"    # I
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 170
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2}, Landroid/support/v4/internal/view/SupportMenu;->performIdentifierAction(II)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public performShortcut(ILandroid/view/KeyEvent;I)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;
    .param p3, "flags"    # I

    #@0
    .prologue
    .line 160
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3}, Landroid/support/v4/internal/view/SupportMenu;->performShortcut(ILandroid/view/KeyEvent;I)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public removeGroup(I)V
    .locals 1
    .param p1, "groupId"    # I

    #@0
    .prologue
    .line 108
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->internalRemoveGroup(I)V

    #@3
    .line 109
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@5
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@7
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->removeGroup(I)V

    #@a
    .line 110
    return-void
.end method

.method public removeItem(I)V
    .locals 1
    .param p1, "id"    # I

    #@0
    .prologue
    .line 102
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->internalRemoveItem(I)V

    #@3
    .line 103
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@5
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@7
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->removeItem(I)V

    #@a
    .line 104
    return-void
.end method

.method public setGroupCheckable(IZZ)V
    .locals 1
    .param p1, "group"    # I
    .param p2, "checkable"    # Z
    .param p3, "exclusive"    # Z

    #@0
    .prologue
    .line 120
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2, p3}, Landroid/support/v4/internal/view/SupportMenu;->setGroupCheckable(IZZ)V

    #@7
    .line 121
    return-void
.end method

.method public setGroupEnabled(IZ)V
    .locals 1
    .param p1, "group"    # I
    .param p2, "enabled"    # Z

    #@0
    .prologue
    .line 130
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2}, Landroid/support/v4/internal/view/SupportMenu;->setGroupEnabled(IZ)V

    #@7
    .line 131
    return-void
.end method

.method public setGroupVisible(IZ)V
    .locals 1
    .param p1, "group"    # I
    .param p2, "visible"    # Z

    #@0
    .prologue
    .line 125
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1, p2}, Landroid/support/v4/internal/view/SupportMenu;->setGroupVisible(IZ)V

    #@7
    .line 126
    return-void
.end method

.method public setQwertyMode(Z)V
    .locals 1
    .param p1, "isQwerty"    # Z

    #@0
    .prologue
    .line 175
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenu;->setQwertyMode(Z)V

    #@7
    .line 176
    return-void
.end method

.method public size()I
    .locals 1

    #@0
    .prologue
    .line 145
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenu;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenu;->size()I

    #@7
    move-result v0

    #@8
    return v0
.end method
