.class public Landroid/support/v7/internal/view/StandaloneActionMode;
.super Landroid/support/v7/view/ActionMode;
.source "StandaloneActionMode.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;


# instance fields
.field private mCallback:Landroid/support/v7/view/ActionMode$Callback;

.field private mContext:Landroid/content/Context;

.field private mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

.field private mCustomView:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference",
            "<",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field private mFinished:Z

.field private mFocusable:Z

.field private mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/support/v7/internal/widget/ActionBarContextView;Landroid/support/v7/view/ActionMode$Callback;Z)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "view"    # Landroid/support/v7/internal/widget/ActionBarContextView;
    .param p3, "callback"    # Landroid/support/v7/view/ActionMode$Callback;
    .param p4, "isFocusable"    # Z

    #@0
    .prologue
    .line 48
    invoke-direct {p0}, Landroid/support/v7/view/ActionMode;-><init>()V

    #@3
    .line 49
    iput-object p1, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContext:Landroid/content/Context;

    #@5
    .line 50
    iput-object p2, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@7
    .line 51
    iput-object p3, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@9
    .line 53
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@b
    invoke-direct {v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;-><init>(Landroid/content/Context;)V

    #@e
    const/4 v1, 0x1

    #@f
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setDefaultShowAsAction(I)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@12
    move-result-object v0

    #@13
    iput-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@15
    .line 54
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@17
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@1a
    .line 55
    iput-boolean p4, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mFocusable:Z

    #@1c
    .line 56
    return-void
.end method


# virtual methods
.method public finish()V
    .locals 2

    #@0
    .prologue
    .line 102
    iget-boolean v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mFinished:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 109
    :goto_0
    return-void

    #@5
    .line 105
    :cond_0
    const/4 v0, 0x1

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mFinished:Z

    #@8
    .line 107
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@a
    const/16 v1, 0x20

    #@c
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->sendAccessibilityEvent(I)V

    #@f
    .line 108
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@11
    invoke-interface {v0, p0}, Landroid/support/v7/view/ActionMode$Callback;->onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V

    #@14
    goto :goto_0
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 128
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCustomView:Ljava/lang/ref/WeakReference;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCustomView:Ljava/lang/ref/WeakReference;

    #@6
    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Landroid/view/View;

    #@c
    :goto_0
    return-object v0

    #@d
    :cond_0
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public getMenu()Landroid/view/Menu;
    .locals 1

    #@0
    .prologue
    .line 113
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method public getMenuInflater()Landroid/view/MenuInflater;
    .locals 2

    #@0
    .prologue
    .line 133
    new-instance v0, Landroid/view/MenuInflater;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContext:Landroid/content/Context;

    #@4
    invoke-direct {v0, v1}, Landroid/view/MenuInflater;-><init>(Landroid/content/Context;)V

    #@7
    return-object v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 123
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getSubtitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 118
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getTitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public invalidate()V
    .locals 2

    #@0
    .prologue
    .line 97
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    invoke-interface {v0, p0, v1}, Landroid/support/v7/view/ActionMode$Callback;->onPrepareActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z

    #@7
    .line 98
    return-void
.end method

.method public isTitleOptional()Z
    .locals 1

    #@0
    .prologue
    .line 86
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->isTitleOptional()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isUiFocusable()Z
    .locals 1

    #@0
    .prologue
    .line 161
    iget-boolean v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mFocusable:Z

    #@2
    return v0
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 141
    return-void
.end method

.method public onCloseSubMenu(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    .line 153
    return-void
.end method

.method public onMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 137
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    invoke-interface {v0, p0, p2}, Landroid/support/v7/view/ActionMode$Callback;->onActionItemClicked(Landroid/support/v7/view/ActionMode;Landroid/view/MenuItem;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onMenuModeChange(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 156
    invoke-virtual {p0}, Landroid/support/v7/internal/view/StandaloneActionMode;->invalidate()V

    #@3
    .line 157
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->showOverflowMenu()Z

    #@8
    .line 158
    return-void
.end method

.method public onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z
    .locals 3
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 144
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->hasVisibleItems()Z

    #@4
    move-result v0

    #@5
    if-nez v0, :cond_0

    #@7
    .line 149
    :goto_0
    return v2

    #@8
    .line 148
    :cond_0
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuPopupHelper;

    #@a
    iget-object v1, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContext:Landroid/content/Context;

    #@c
    invoke-direct {v0, v1, p1}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;-><init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@f
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;->show()V

    #@12
    goto :goto_0
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 91
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setCustomView(Landroid/view/View;)V

    #@5
    .line 92
    if-eqz p1, :cond_0

    #@7
    new-instance v0, Ljava/lang/ref/WeakReference;

    #@9
    invoke-direct {v0, p1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    #@c
    :goto_0
    iput-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mCustomView:Ljava/lang/ref/WeakReference;

    #@e
    .line 93
    return-void

    #@f
    .line 92
    :cond_0
    const/4 v0, 0x0

    #@10
    goto :goto_0
.end method

.method public setSubtitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 75
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/StandaloneActionMode;->setSubtitle(Ljava/lang/CharSequence;)V

    #@9
    .line 76
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setSubtitle(Ljava/lang/CharSequence;)V

    #@5
    .line 66
    return-void
.end method

.method public setTitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 70
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/StandaloneActionMode;->setTitle(Ljava/lang/CharSequence;)V

    #@9
    .line 71
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 60
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setTitle(Ljava/lang/CharSequence;)V

    #@5
    .line 61
    return-void
.end method

.method public setTitleOptionalHint(Z)V
    .locals 1
    .param p1, "titleOptional"    # Z

    #@0
    .prologue
    .line 80
    invoke-super {p0, p1}, Landroid/support/v7/view/ActionMode;->setTitleOptionalHint(Z)V

    #@3
    .line 81
    iget-object v0, p0, Landroid/support/v7/internal/view/StandaloneActionMode;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setTitleOptional(Z)V

    #@8
    .line 82
    return-void
.end method
