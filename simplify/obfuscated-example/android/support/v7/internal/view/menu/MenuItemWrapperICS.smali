.class public Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;
.super Landroid/support/v7/internal/view/menu/BaseMenuWrapper;
.source "MenuItemWrapperICS.java"

# interfaces
.implements Landroid/view/MenuItem;


# annotations
.annotation build Landroid/annotation/TargetApi;
    value = 0xe
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;,
        Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;,
        Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnActionExpandListenerWrapper;,
        Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnMenuItemClickListenerWrapper;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/support/v7/internal/view/menu/BaseMenuWrapper",
        "<",
        "Landroid/support/v4/internal/view/SupportMenuItem;",
        ">;",
        "Landroid/view/MenuItem;"
    }
.end annotation


# static fields
.field static final LOG_TAG:Ljava/lang/String; = "MenuItemWrapper"


# instance fields
.field private mSetExclusiveCheckableMethod:Ljava/lang/reflect/Method;


# direct methods
.method constructor <init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "object"    # Landroid/support/v4/internal/view/SupportMenuItem;

    #@0
    .prologue
    .line 49
    invoke-direct {p0, p1, p2}, Landroid/support/v7/internal/view/menu/BaseMenuWrapper;-><init>(Landroid/content/Context;Ljava/lang/Object;)V

    #@3
    .line 50
    return-void
.end method


# virtual methods
.method public collapseActionView()Z
    .locals 1

    #@0
    .prologue
    .line 281
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->collapseActionView()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method createActionProviderWrapper(Landroid/view/ActionProvider;)Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;
    .locals 2
    .param p1, "provider"    # Landroid/view/ActionProvider;

    #@0
    .prologue
    .line 309
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mContext:Landroid/content/Context;

    #@4
    invoke-direct {v0, p0, v1, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;-><init>(Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;Landroid/content/Context;Landroid/view/ActionProvider;)V

    #@7
    return-object v0
.end method

.method public expandActionView()Z
    .locals 1

    #@0
    .prologue
    .line 276
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->expandActionView()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getActionProvider()Landroid/view/ActionProvider;
    .locals 2

    #@0
    .prologue
    .line 267
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v1}, Landroid/support/v4/internal/view/SupportMenuItem;->getSupportActionProvider()Landroid/support/v4/view/ActionProvider;

    #@7
    move-result-object v0

    #@8
    .line 268
    .local v0, "provider":Landroid/support/v4/view/ActionProvider;
    instance-of v1, v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 269
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;

    #@e
    .end local v0    # "provider":Landroid/support/v4/view/ActionProvider;
    iget-object v1, v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;->mInner:Landroid/view/ActionProvider;

    #@10
    .line 271
    :goto_0
    return-object v1

    #@11
    .restart local v0    # "provider":Landroid/support/v4/view/ActionProvider;
    :cond_0
    const/4 v1, 0x0

    #@12
    goto :goto_0
.end method

.method public getActionView()Landroid/view/View;
    .locals 2

    #@0
    .prologue
    .line 251
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v1}, Landroid/support/v4/internal/view/SupportMenuItem;->getActionView()Landroid/view/View;

    #@7
    move-result-object v0

    #@8
    .line 252
    .local v0, "actionView":Landroid/view/View;
    instance-of v1, v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 253
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;

    #@e
    .end local v0    # "actionView":Landroid/view/View;
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;->getWrappedView()Landroid/view/View;

    #@11
    move-result-object v0

    #@12
    .line 255
    :cond_0
    return-object v0
.end method

.method public getAlphabeticShortcut()C
    .locals 1

    #@0
    .prologue
    .line 148
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getAlphabeticShortcut()C

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getGroupId()I
    .locals 1

    #@0
    .prologue
    .line 59
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getGroupId()I

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getIcon()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 109
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getIcon()Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getIntent()Landroid/content/Intent;
    .locals 1

    #@0
    .prologue
    .line 120
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getIntent()Landroid/content/Intent;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getItemId()I
    .locals 1

    #@0
    .prologue
    .line 54
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getItemId()I

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getMenuInfo()Landroid/view/ContextMenu$ContextMenuInfo;
    .locals 1

    #@0
    .prologue
    .line 213
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getMenuInfo()Landroid/view/ContextMenu$ContextMenuInfo;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getNumericShortcut()C
    .locals 1

    #@0
    .prologue
    .line 137
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getNumericShortcut()C

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getOrder()I
    .locals 1

    #@0
    .prologue
    .line 64
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getOrder()I

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getSubMenu()Landroid/view/SubMenu;
    .locals 1

    #@0
    .prologue
    .line 201
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getSubMenu()Landroid/view/SubMenu;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->getSubMenuWrapper(Landroid/view/SubMenu;)Landroid/view/SubMenu;

    #@b
    move-result-object v0

    #@c
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 81
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getTitle()Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getTitleCondensed()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 92
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->getTitleCondensed()Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public hasSubMenu()Z
    .locals 1

    #@0
    .prologue
    .line 196
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->hasSubMenu()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isActionViewExpanded()Z
    .locals 1

    #@0
    .prologue
    .line 286
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->isActionViewExpanded()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isCheckable()Z
    .locals 1

    #@0
    .prologue
    .line 159
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->isCheckable()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isChecked()Z
    .locals 1

    #@0
    .prologue
    .line 170
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->isChecked()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 191
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->isEnabled()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isVisible()Z
    .locals 1

    #@0
    .prologue
    .line 180
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0}, Landroid/support/v4/internal/view/SupportMenuItem;->isVisible()Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public setActionProvider(Landroid/view/ActionProvider;)Landroid/view/MenuItem;
    .locals 2
    .param p1, "provider"    # Landroid/view/ActionProvider;

    #@0
    .prologue
    .line 260
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    if-eqz p1, :cond_0

    #@6
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->createActionProviderWrapper(Landroid/view/ActionProvider;)Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;

    #@9
    move-result-object v1

    #@a
    :goto_0
    invoke-interface {v0, v1}, Landroid/support/v4/internal/view/SupportMenuItem;->setSupportActionProvider(Landroid/support/v4/view/ActionProvider;)Landroid/support/v4/internal/view/SupportMenuItem;

    #@d
    .line 262
    return-object p0

    #@e
    .line 260
    :cond_0
    const/4 v1, 0x0

    #@f
    goto :goto_0
.end method

.method public setActionView(I)Landroid/view/MenuItem;
    .locals 3
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 239
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v1, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setActionView(I)Landroid/view/MenuItem;

    #@7
    .line 241
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@9
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@b
    invoke-interface {v1}, Landroid/support/v4/internal/view/SupportMenuItem;->getActionView()Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    .line 242
    .local v0, "actionView":Landroid/view/View;
    instance-of v1, v0, Landroid/view/CollapsibleActionView;

    #@11
    if-eqz v1, :cond_0

    #@13
    .line 244
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@15
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@17
    new-instance v2, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;

    #@19
    invoke-direct {v2, v0}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;-><init>(Landroid/view/View;)V

    #@1c
    invoke-interface {v1, v2}, Landroid/support/v4/internal/view/SupportMenuItem;->setActionView(Landroid/view/View;)Landroid/view/MenuItem;

    #@1f
    .line 246
    :cond_0
    return-object p0
.end method

.method public setActionView(Landroid/view/View;)Landroid/view/MenuItem;
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 229
    instance-of v1, p1, Landroid/view/CollapsibleActionView;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 230
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;

    #@6
    invoke-direct {v0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$CollapsibleActionViewWrapper;-><init>(Landroid/view/View;)V

    #@9
    .end local p1    # "view":Landroid/view/View;
    .local v0, "view":Landroid/view/View;
    move-object p1, v0

    #@a
    .line 232
    .end local v0    # "view":Landroid/view/View;
    .restart local p1    # "view":Landroid/view/View;
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@c
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@e
    invoke-interface {v1, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setActionView(Landroid/view/View;)Landroid/view/MenuItem;

    #@11
    .line 233
    return-object p0
.end method

.method public setAlphabeticShortcut(C)Landroid/view/MenuItem;
    .locals 1
    .param p1, "alphaChar"    # C

    #@0
    .prologue
    .line 142
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setAlphabeticShortcut(C)Landroid/view/MenuItem;

    #@7
    .line 143
    return-object p0
.end method

.method public setCheckable(Z)Landroid/view/MenuItem;
    .locals 1
    .param p1, "checkable"    # Z

    #@0
    .prologue
    .line 153
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setCheckable(Z)Landroid/view/MenuItem;

    #@7
    .line 154
    return-object p0
.end method

.method public setChecked(Z)Landroid/view/MenuItem;
    .locals 1
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 164
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setChecked(Z)Landroid/view/MenuItem;

    #@7
    .line 165
    return-object p0
.end method

.method public setEnabled(Z)Landroid/view/MenuItem;
    .locals 1
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 185
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setEnabled(Z)Landroid/view/MenuItem;

    #@7
    .line 186
    return-object p0
.end method

.method public setExclusiveCheckable(Z)V
    .locals 6
    .param p1, "checkable"    # Z

    #@0
    .prologue
    .line 298
    :try_start_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mSetExclusiveCheckableMethod:Ljava/lang/reflect/Method;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 299
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@6
    check-cast v1, Landroid/support/v4/internal/view/SupportMenuItem;

    #@8
    invoke-virtual {v1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@b
    move-result-object v1

    #@c
    const-string v2, "setExclusiveCheckable"

    #@e
    const/4 v3, 0x1

    #@f
    new-array v3, v3, [Ljava/lang/Class;

    #@11
    const/4 v4, 0x0

    #@12
    sget-object v5, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    #@14
    aput-object v5, v3, v4

    #@16
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@19
    move-result-object v1

    #@1a
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mSetExclusiveCheckableMethod:Ljava/lang/reflect/Method;

    #@1c
    .line 302
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mSetExclusiveCheckableMethod:Ljava/lang/reflect/Method;

    #@1e
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@20
    const/4 v3, 0x1

    #@21
    new-array v3, v3, [Ljava/lang/Object;

    #@23
    const/4 v4, 0x0

    #@24
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@27
    move-result-object v5

    #@28
    aput-object v5, v3, v4

    #@2a
    invoke-virtual {v1, v2, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@2d
    .line 306
    :goto_0
    return-void

    #@2e
    .line 303
    :catch_0
    move-exception v0

    #@2f
    .line 304
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "MenuItemWrapper"

    #@31
    const-string v2, "Error while calling setExclusiveCheckable"

    #@33
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@36
    goto :goto_0
.end method

.method public setIcon(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "iconRes"    # I

    #@0
    .prologue
    .line 103
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setIcon(I)Landroid/view/MenuItem;

    #@7
    .line 104
    return-object p0
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 97
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;

    #@7
    .line 98
    return-object p0
.end method

.method public setIntent(Landroid/content/Intent;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 114
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setIntent(Landroid/content/Intent;)Landroid/view/MenuItem;

    #@7
    .line 115
    return-object p0
.end method

.method public setNumericShortcut(C)Landroid/view/MenuItem;
    .locals 1
    .param p1, "numericChar"    # C

    #@0
    .prologue
    .line 131
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setNumericShortcut(C)Landroid/view/MenuItem;

    #@7
    .line 132
    return-object p0
.end method

.method public setOnActionExpandListener(Landroid/view/MenuItem$OnActionExpandListener;)Landroid/view/MenuItem;
    .locals 2
    .param p1, "listener"    # Landroid/view/MenuItem$OnActionExpandListener;

    #@0
    .prologue
    .line 291
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    if-eqz p1, :cond_0

    #@6
    new-instance v1, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnActionExpandListenerWrapper;

    #@8
    invoke-direct {v1, p0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnActionExpandListenerWrapper;-><init>(Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;Landroid/view/MenuItem$OnActionExpandListener;)V

    #@b
    :goto_0
    invoke-interface {v0, v1}, Landroid/support/v4/internal/view/SupportMenuItem;->setSupportOnActionExpandListener(Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;)Landroid/support/v4/internal/view/SupportMenuItem;

    #@e
    .line 293
    return-object p0

    #@f
    .line 291
    :cond_0
    const/4 v1, 0x0

    #@10
    goto :goto_0
.end method

.method public setOnMenuItemClickListener(Landroid/view/MenuItem$OnMenuItemClickListener;)Landroid/view/MenuItem;
    .locals 2
    .param p1, "menuItemClickListener"    # Landroid/view/MenuItem$OnMenuItemClickListener;

    #@0
    .prologue
    .line 206
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    if-eqz p1, :cond_0

    #@6
    new-instance v1, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnMenuItemClickListenerWrapper;

    #@8
    invoke-direct {v1, p0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$OnMenuItemClickListenerWrapper;-><init>(Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;Landroid/view/MenuItem$OnMenuItemClickListener;)V

    #@b
    :goto_0
    invoke-interface {v0, v1}, Landroid/support/v4/internal/view/SupportMenuItem;->setOnMenuItemClickListener(Landroid/view/MenuItem$OnMenuItemClickListener;)Landroid/view/MenuItem;

    #@e
    .line 208
    return-object p0

    #@f
    .line 206
    :cond_0
    const/4 v1, 0x0

    #@10
    goto :goto_0
.end method

.method public setShortcut(CC)Landroid/view/MenuItem;
    .locals 1
    .param p1, "numericChar"    # C
    .param p2, "alphaChar"    # C

    #@0
    .prologue
    .line 125
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1, p2}, Landroid/support/v4/internal/view/SupportMenuItem;->setShortcut(CC)Landroid/view/MenuItem;

    #@7
    .line 126
    return-object p0
.end method

.method public setShowAsAction(I)V
    .locals 1
    .param p1, "actionEnum"    # I

    #@0
    .prologue
    .line 218
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setShowAsAction(I)V

    #@7
    .line 219
    return-void
.end method

.method public setShowAsActionFlags(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "actionEnum"    # I

    #@0
    .prologue
    .line 223
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setShowAsActionFlags(I)Landroid/view/MenuItem;

    #@7
    .line 224
    return-object p0
.end method

.method public setTitle(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # I

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setTitle(I)Landroid/view/MenuItem;

    #@7
    .line 76
    return-object p0
.end method

.method public setTitle(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 69
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setTitle(Ljava/lang/CharSequence;)Landroid/view/MenuItem;

    #@7
    .line 70
    return-object p0
.end method

.method public setTitleCondensed(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 86
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setTitleCondensed(Ljava/lang/CharSequence;)Landroid/view/MenuItem;

    #@7
    .line 87
    return-object p0
.end method

.method public setVisible(Z)Landroid/view/MenuItem;
    .locals 1
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 175
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;->mWrappedObject:Ljava/lang/Object;

    #@2
    check-cast v0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@4
    invoke-interface {v0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setVisible(Z)Landroid/view/MenuItem;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method
