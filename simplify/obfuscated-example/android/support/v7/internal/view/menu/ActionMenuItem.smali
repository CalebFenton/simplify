.class public Landroid/support/v7/internal/view/menu/ActionMenuItem;
.super Ljava/lang/Object;
.source "ActionMenuItem.java"

# interfaces
.implements Landroid/support/v4/internal/view/SupportMenuItem;


# static fields
.field private static final CHECKABLE:I = 0x1

.field private static final CHECKED:I = 0x2

.field private static final ENABLED:I = 0x10

.field private static final EXCLUSIVE:I = 0x4

.field private static final HIDDEN:I = 0x8

.field private static final NO_ICON:I


# instance fields
.field private final mCategoryOrder:I

.field private mClickListener:Landroid/view/MenuItem$OnMenuItemClickListener;

.field private mContext:Landroid/content/Context;

.field private mFlags:I

.field private final mGroup:I

.field private mIconDrawable:Landroid/graphics/drawable/Drawable;

.field private mIconResId:I

.field private final mId:I

.field private mIntent:Landroid/content/Intent;

.field private final mOrdering:I

.field private mShortcutAlphabeticChar:C

.field private mShortcutNumericChar:C

.field private mTitle:Ljava/lang/CharSequence;

.field private mTitleCondensed:Ljava/lang/CharSequence;


# direct methods
.method public constructor <init>(Landroid/content/Context;IIIILjava/lang/CharSequence;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "group"    # I
    .param p3, "id"    # I
    .param p4, "categoryOrder"    # I
    .param p5, "ordering"    # I
    .param p6, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 64
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 48
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconResId:I

    #@6
    .line 56
    const/16 v0, 0x10

    #@8
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@a
    .line 65
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mContext:Landroid/content/Context;

    #@c
    .line 66
    iput p3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mId:I

    #@e
    .line 67
    iput p2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mGroup:I

    #@10
    .line 68
    iput p4, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mCategoryOrder:I

    #@12
    .line 69
    iput p5, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mOrdering:I

    #@14
    .line 70
    iput-object p6, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitle:Ljava/lang/CharSequence;

    #@16
    .line 71
    return-void
.end method


# virtual methods
.method public collapseActionView()Z
    .locals 1

    #@0
    .prologue
    .line 278
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public expandActionView()Z
    .locals 1

    #@0
    .prologue
    .line 273
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getActionProvider()Landroid/view/ActionProvider;
    .locals 1

    #@0
    .prologue
    .line 247
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public getActionView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 237
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getAlphabeticShortcut()C
    .locals 1

    #@0
    .prologue
    .line 74
    iget-char v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutAlphabeticChar:C

    #@2
    return v0
.end method

.method public getGroupId()I
    .locals 1

    #@0
    .prologue
    .line 78
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mGroup:I

    #@2
    return v0
.end method

.method public getIcon()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 82
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public getIntent()Landroid/content/Intent;
    .locals 1

    #@0
    .prologue
    .line 86
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIntent:Landroid/content/Intent;

    #@2
    return-object v0
.end method

.method public getItemId()I
    .locals 1

    #@0
    .prologue
    .line 90
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mId:I

    #@2
    return v0
.end method

.method public getMenuInfo()Landroid/view/ContextMenu$ContextMenuInfo;
    .locals 1

    #@0
    .prologue
    .line 94
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getNumericShortcut()C
    .locals 1

    #@0
    .prologue
    .line 98
    iget-char v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutNumericChar:C

    #@2
    return v0
.end method

.method public getOrder()I
    .locals 1

    #@0
    .prologue
    .line 102
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mOrdering:I

    #@2
    return v0
.end method

.method public getSubMenu()Landroid/view/SubMenu;
    .locals 1

    #@0
    .prologue
    .line 106
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getSupportActionProvider()Landroid/support/v4/view/ActionProvider;
    .locals 1

    #@0
    .prologue
    .line 257
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 110
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitle:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getTitleCondensed()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 114
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitleCondensed:Ljava/lang/CharSequence;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitleCondensed:Ljava/lang/CharSequence;

    #@6
    :goto_0
    return-object v0

    #@7
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitle:Ljava/lang/CharSequence;

    #@9
    goto :goto_0
.end method

.method public hasSubMenu()Z
    .locals 1

    #@0
    .prologue
    .line 118
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public invoke()Z
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 216
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mClickListener:Landroid/view/MenuItem$OnMenuItemClickListener;

    #@3
    if-eqz v1, :cond_0

    #@5
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mClickListener:Landroid/view/MenuItem$OnMenuItemClickListener;

    #@7
    invoke-interface {v1, p0}, Landroid/view/MenuItem$OnMenuItemClickListener;->onMenuItemClick(Landroid/view/MenuItem;)Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 225
    :goto_0
    return v0

    #@e
    .line 220
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIntent:Landroid/content/Intent;

    #@10
    if-eqz v1, :cond_1

    #@12
    .line 221
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mContext:Landroid/content/Context;

    #@14
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIntent:Landroid/content/Intent;

    #@16
    invoke-virtual {v1, v2}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    #@19
    goto :goto_0

    #@1a
    .line 225
    :cond_1
    const/4 v0, 0x0

    #@1b
    goto :goto_0
.end method

.method public isActionViewExpanded()Z
    .locals 1

    #@0
    .prologue
    .line 283
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public isCheckable()Z
    .locals 1

    #@0
    .prologue
    .line 122
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v0, v0, 0x1

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public isChecked()Z
    .locals 1

    #@0
    .prologue
    .line 126
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v0, v0, 0x2

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public isEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 130
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v0, v0, 0x10

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public isVisible()Z
    .locals 1

    #@0
    .prologue
    .line 134
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v0, v0, 0x8

    #@4
    if-nez v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public setActionProvider(Landroid/view/ActionProvider;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "actionProvider"    # Landroid/view/ActionProvider;

    #@0
    .prologue
    .line 242
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public setActionView(I)Landroid/support/v4/internal/view/SupportMenuItem;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 252
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public setActionView(Landroid/view/View;)Landroid/support/v4/internal/view/SupportMenuItem;
    .locals 1
    .param p1, "actionView"    # Landroid/view/View;

    #@0
    .prologue
    .line 233
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public bridge synthetic setActionView(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "x0"    # I

    #@0
    .prologue
    .line 34
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/ActionMenuItem;->setActionView(I)Landroid/support/v4/internal/view/SupportMenuItem;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public bridge synthetic setActionView(Landroid/view/View;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "x0"    # Landroid/view/View;

    #@0
    .prologue
    .line 34
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/ActionMenuItem;->setActionView(Landroid/view/View;)Landroid/support/v4/internal/view/SupportMenuItem;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public setAlphabeticShortcut(C)Landroid/view/MenuItem;
    .locals 0
    .param p1, "alphaChar"    # C

    #@0
    .prologue
    .line 138
    iput-char p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutAlphabeticChar:C

    #@2
    .line 139
    return-object p0
.end method

.method public setCheckable(Z)Landroid/view/MenuItem;
    .locals 2
    .param p1, "checkable"    # Z

    #@0
    .prologue
    .line 143
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v1, v0, -0x2

    #@4
    if-eqz p1, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    or-int/2addr v0, v1

    #@8
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@a
    .line 144
    return-object p0

    #@b
    .line 143
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public setChecked(Z)Landroid/view/MenuItem;
    .locals 2
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 153
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v1, v0, -0x3

    #@4
    if-eqz p1, :cond_0

    #@6
    const/4 v0, 0x2

    #@7
    :goto_0
    or-int/2addr v0, v1

    #@8
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@a
    .line 154
    return-object p0

    #@b
    .line 153
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public setEnabled(Z)Landroid/view/MenuItem;
    .locals 2
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 158
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v1, v0, -0x11

    #@4
    if-eqz p1, :cond_0

    #@6
    const/16 v0, 0x10

    #@8
    :goto_0
    or-int/2addr v0, v1

    #@9
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@b
    .line 159
    return-object p0

    #@c
    .line 158
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public setExclusiveCheckable(Z)Landroid/support/v7/internal/view/menu/ActionMenuItem;
    .locals 2
    .param p1, "exclusive"    # Z

    #@0
    .prologue
    .line 148
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v1, v0, -0x5

    #@4
    if-eqz p1, :cond_0

    #@6
    const/4 v0, 0x4

    #@7
    :goto_0
    or-int/2addr v0, v1

    #@8
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@a
    .line 149
    return-object p0

    #@b
    .line 148
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public setIcon(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "iconRes"    # I

    #@0
    .prologue
    .line 169
    iput p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconResId:I

    #@2
    .line 170
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mContext:Landroid/content/Context;

    #@4
    invoke-static {v0, p1}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconDrawable:Landroid/graphics/drawable/Drawable;

    #@a
    .line 171
    return-object p0
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 163
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    .line 164
    const/4 v0, 0x0

    #@3
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIconResId:I

    #@5
    .line 165
    return-object p0
.end method

.method public setIntent(Landroid/content/Intent;)Landroid/view/MenuItem;
    .locals 0
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 175
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mIntent:Landroid/content/Intent;

    #@2
    .line 176
    return-object p0
.end method

.method public setNumericShortcut(C)Landroid/view/MenuItem;
    .locals 0
    .param p1, "numericChar"    # C

    #@0
    .prologue
    .line 180
    iput-char p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutNumericChar:C

    #@2
    .line 181
    return-object p0
.end method

.method public setOnActionExpandListener(Landroid/view/MenuItem$OnActionExpandListener;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "listener"    # Landroid/view/MenuItem$OnActionExpandListener;

    #@0
    .prologue
    .line 288
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public setOnMenuItemClickListener(Landroid/view/MenuItem$OnMenuItemClickListener;)Landroid/view/MenuItem;
    .locals 0
    .param p1, "menuItemClickListener"    # Landroid/view/MenuItem$OnMenuItemClickListener;

    #@0
    .prologue
    .line 185
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mClickListener:Landroid/view/MenuItem$OnMenuItemClickListener;

    #@2
    .line 186
    return-object p0
.end method

.method public setShortcut(CC)Landroid/view/MenuItem;
    .locals 0
    .param p1, "numericChar"    # C
    .param p2, "alphaChar"    # C

    #@0
    .prologue
    .line 190
    iput-char p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutNumericChar:C

    #@2
    .line 191
    iput-char p2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mShortcutAlphabeticChar:C

    #@4
    .line 192
    return-object p0
.end method

.method public setShowAsAction(I)V
    .locals 0
    .param p1, "show"    # I

    #@0
    .prologue
    .line 230
    return-void
.end method

.method public setShowAsActionFlags(I)Landroid/support/v4/internal/view/SupportMenuItem;
    .locals 0
    .param p1, "actionEnum"    # I

    #@0
    .prologue
    .line 267
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/ActionMenuItem;->setShowAsAction(I)V

    #@3
    .line 268
    return-object p0
.end method

.method public bridge synthetic setShowAsActionFlags(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "x0"    # I

    #@0
    .prologue
    .line 34
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/ActionMenuItem;->setShowAsActionFlags(I)Landroid/support/v4/internal/view/SupportMenuItem;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public setSupportActionProvider(Landroid/support/v4/view/ActionProvider;)Landroid/support/v4/internal/view/SupportMenuItem;
    .locals 1
    .param p1, "actionProvider"    # Landroid/support/v4/view/ActionProvider;

    #@0
    .prologue
    .line 262
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public setSupportOnActionExpandListener(Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;)Landroid/support/v4/internal/view/SupportMenuItem;
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;

    #@0
    .prologue
    .line 294
    return-object p0
.end method

.method public setTitle(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # I

    #@0
    .prologue
    .line 201
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@9
    move-result-object v0

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitle:Ljava/lang/CharSequence;

    #@c
    .line 202
    return-object p0
.end method

.method public setTitle(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 0
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 196
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitle:Ljava/lang/CharSequence;

    #@2
    .line 197
    return-object p0
.end method

.method public setTitleCondensed(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 0
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 206
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mTitleCondensed:Ljava/lang/CharSequence;

    #@2
    .line 207
    return-object p0
.end method

.method public setVisible(Z)Landroid/view/MenuItem;
    .locals 2
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 211
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@2
    and-int/lit8 v1, v0, 0x8

    #@4
    if-eqz p1, :cond_0

    #@6
    const/4 v0, 0x0

    #@7
    :goto_0
    or-int/2addr v0, v1

    #@8
    iput v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItem;->mFlags:I

    #@a
    .line 212
    return-object p0

    #@b
    .line 211
    :cond_0
    const/16 v0, 0x8

    #@d
    goto :goto_0
.end method
