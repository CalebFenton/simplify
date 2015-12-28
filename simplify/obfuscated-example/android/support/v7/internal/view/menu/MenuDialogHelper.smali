.class public Landroid/support/v7/internal/view/menu/MenuDialogHelper;
.super Ljava/lang/Object;
.source "MenuDialogHelper.java"

# interfaces
.implements Landroid/content/DialogInterface$OnClickListener;
.implements Landroid/content/DialogInterface$OnDismissListener;
.implements Landroid/content/DialogInterface$OnKeyListener;
.implements Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;


# instance fields
.field private mDialog:Landroid/app/AlertDialog;

.field private mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

.field mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

.field private mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 43
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 44
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5
    .line 45
    return-void
.end method


# virtual methods
.method public dismiss()V
    .locals 1

    #@0
    .prologue
    .line 141
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 142
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@6
    invoke-virtual {v0}, Landroid/app/AlertDialog;->dismiss()V

    #@9
    .line 144
    :cond_0
    return-void
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .locals 3
    .param p1, "dialog"    # Landroid/content/DialogInterface;
    .param p2, "which"    # I

    #@0
    .prologue
    .line 170
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@4
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->getAdapter()Landroid/widget/ListAdapter;

    #@7
    move-result-object v0

    #@8
    invoke-interface {v0, p2}, Landroid/widget/ListAdapter;->getItem(I)Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@e
    const/4 v2, 0x0

    #@f
    invoke-virtual {v1, v0, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;I)Z

    #@12
    .line 171
    return-void
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 153
    if-nez p2, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    if-ne p1, v0, :cond_1

    #@6
    .line 154
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->dismiss()V

    #@9
    .line 156
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@b
    if-eqz v0, :cond_2

    #@d
    .line 157
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@f
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@12
    .line 159
    :cond_2
    return-void
.end method

.method public onDismiss(Landroid/content/DialogInterface;)V
    .locals 3
    .param p1, "dialog"    # Landroid/content/DialogInterface;

    #@0
    .prologue
    .line 148
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    const/4 v2, 0x1

    #@5
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@8
    .line 149
    return-void
.end method

.method public onKey(Landroid/content/DialogInterface;ILandroid/view/KeyEvent;)Z
    .locals 5
    .param p1, "dialog"    # Landroid/content/DialogInterface;
    .param p2, "keyCode"    # I
    .param p3, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 96
    const/16 v4, 0x52

    #@3
    if-eq p2, v4, :cond_0

    #@5
    const/4 v4, 0x4

    #@6
    if-ne p2, v4, :cond_2

    #@8
    .line 97
    :cond_0
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    #@b
    move-result v4

    #@c
    if-nez v4, :cond_1

    #@e
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getRepeatCount()I

    #@11
    move-result v4

    #@12
    if-nez v4, :cond_1

    #@14
    .line 99
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@16
    invoke-virtual {v4}, Landroid/app/AlertDialog;->getWindow()Landroid/view/Window;

    #@19
    move-result-object v2

    #@1a
    .line 100
    .local v2, "win":Landroid/view/Window;
    if-eqz v2, :cond_2

    #@1c
    .line 101
    invoke-virtual {v2}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@1f
    move-result-object v0

    #@20
    .line 102
    .local v0, "decor":Landroid/view/View;
    if-eqz v0, :cond_2

    #@22
    .line 103
    invoke-virtual {v0}, Landroid/view/View;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@25
    move-result-object v1

    #@26
    .line 104
    .local v1, "ds":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v1, :cond_2

    #@28
    .line 105
    invoke-virtual {v1, p3, p0}, Landroid/view/KeyEvent$DispatcherState;->startTracking(Landroid/view/KeyEvent;Ljava/lang/Object;)V

    #@2b
    .line 127
    .end local v0    # "decor":Landroid/view/View;
    .end local v1    # "ds":Landroid/view/KeyEvent$DispatcherState;
    .end local v2    # "win":Landroid/view/Window;
    :goto_0
    return v3

    #@2c
    .line 110
    :cond_1
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    #@2f
    move-result v4

    #@30
    if-ne v4, v3, :cond_2

    #@32
    invoke-virtual {p3}, Landroid/view/KeyEvent;->isCanceled()Z

    #@35
    move-result v4

    #@36
    if-nez v4, :cond_2

    #@38
    .line 111
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@3a
    invoke-virtual {v4}, Landroid/app/AlertDialog;->getWindow()Landroid/view/Window;

    #@3d
    move-result-object v2

    #@3e
    .line 112
    .restart local v2    # "win":Landroid/view/Window;
    if-eqz v2, :cond_2

    #@40
    .line 113
    invoke-virtual {v2}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@43
    move-result-object v0

    #@44
    .line 114
    .restart local v0    # "decor":Landroid/view/View;
    if-eqz v0, :cond_2

    #@46
    .line 115
    invoke-virtual {v0}, Landroid/view/View;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@49
    move-result-object v1

    #@4a
    .line 116
    .restart local v1    # "ds":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v1, :cond_2

    #@4c
    invoke-virtual {v1, p3}, Landroid/view/KeyEvent$DispatcherState;->isTracking(Landroid/view/KeyEvent;)Z

    #@4f
    move-result v4

    #@50
    if-eqz v4, :cond_2

    #@52
    .line 117
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@54
    invoke-virtual {v4, v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@57
    .line 118
    invoke-interface {p1}, Landroid/content/DialogInterface;->dismiss()V

    #@5a
    goto :goto_0

    #@5b
    .line 127
    .end local v0    # "decor":Landroid/view/View;
    .end local v1    # "ds":Landroid/view/KeyEvent$DispatcherState;
    .end local v2    # "win":Landroid/view/Window;
    :cond_2
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5d
    const/4 v4, 0x0

    #@5e
    invoke-virtual {v3, p2, p3, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performShortcut(ILandroid/view/KeyEvent;I)Z

    #@61
    move-result v3

    #@62
    goto :goto_0
.end method

.method public onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z
    .locals 1
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 163
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 164
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z

    #@9
    move-result v0

    #@a
    .line 166
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public setPresenterCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V
    .locals 0
    .param p1, "cb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@0
    .prologue
    .line 132
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    .line 133
    return-void
.end method

.method public show(Landroid/os/IBinder;)V
    .locals 7
    .param p1, "windowToken"    # Landroid/os/IBinder;

    #@0
    .prologue
    .line 54
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    .line 57
    .local v3, "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    new-instance v0, Landroid/app/AlertDialog$Builder;

    #@4
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getContext()Landroid/content/Context;

    #@7
    move-result-object v4

    #@8
    invoke-direct {v0, v4}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    #@b
    .line 61
    .local v0, "builder":Landroid/app/AlertDialog$Builder;
    new-instance v4, Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@d
    sget v5, Landroid/support/v7/appcompat/R$layout;->abc_list_menu_item_layout:I

    #@f
    sget v6, Landroid/support/v7/appcompat/R$style;->Theme_AppCompat_CompactMenu:I

    #@11
    invoke-direct {v4, v5, v6}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;-><init>(II)V

    #@14
    iput-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@16
    .line 64
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@18
    invoke-virtual {v4, p0}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@1b
    .line 65
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@1d
    iget-object v5, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@1f
    invoke-virtual {v4, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V

    #@22
    .line 66
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mPresenter:Landroid/support/v7/internal/view/menu/ListMenuPresenter;

    #@24
    invoke-virtual {v4}, Landroid/support/v7/internal/view/menu/ListMenuPresenter;->getAdapter()Landroid/widget/ListAdapter;

    #@27
    move-result-object v4

    #@28
    invoke-virtual {v0, v4, p0}, Landroid/app/AlertDialog$Builder;->setAdapter(Landroid/widget/ListAdapter;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    #@2b
    .line 69
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getHeaderView()Landroid/view/View;

    #@2e
    move-result-object v1

    #@2f
    .line 70
    .local v1, "headerView":Landroid/view/View;
    if-eqz v1, :cond_1

    #@31
    .line 72
    invoke-virtual {v0, v1}, Landroid/app/AlertDialog$Builder;->setCustomTitle(Landroid/view/View;)Landroid/app/AlertDialog$Builder;

    #@34
    .line 79
    :goto_0
    invoke-virtual {v0, p0}, Landroid/app/AlertDialog$Builder;->setOnKeyListener(Landroid/content/DialogInterface$OnKeyListener;)Landroid/app/AlertDialog$Builder;

    #@37
    .line 82
    invoke-virtual {v0}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;

    #@3a
    move-result-object v4

    #@3b
    iput-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@3d
    .line 83
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@3f
    invoke-virtual {v4, p0}, Landroid/app/AlertDialog;->setOnDismissListener(Landroid/content/DialogInterface$OnDismissListener;)V

    #@42
    .line 85
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@44
    invoke-virtual {v4}, Landroid/app/AlertDialog;->getWindow()Landroid/view/Window;

    #@47
    move-result-object v4

    #@48
    invoke-virtual {v4}, Landroid/view/Window;->getAttributes()Landroid/view/WindowManager$LayoutParams;

    #@4b
    move-result-object v2

    #@4c
    .line 86
    .local v2, "lp":Landroid/view/WindowManager$LayoutParams;
    const/16 v4, 0x3eb

    #@4e
    iput v4, v2, Landroid/view/WindowManager$LayoutParams;->type:I

    #@50
    .line 87
    if-eqz p1, :cond_0

    #@52
    .line 88
    iput-object p1, v2, Landroid/view/WindowManager$LayoutParams;->token:Landroid/os/IBinder;

    #@54
    .line 90
    :cond_0
    iget v4, v2, Landroid/view/WindowManager$LayoutParams;->flags:I

    #@56
    const/high16 v5, 0x20000

    #@58
    or-int/2addr v4, v5

    #@59
    iput v4, v2, Landroid/view/WindowManager$LayoutParams;->flags:I

    #@5b
    .line 92
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuDialogHelper;->mDialog:Landroid/app/AlertDialog;

    #@5d
    invoke-virtual {v4}, Landroid/app/AlertDialog;->show()V

    #@60
    .line 93
    return-void

    #@61
    .line 75
    .end local v2    # "lp":Landroid/view/WindowManager$LayoutParams;
    :cond_1
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getHeaderIcon()Landroid/graphics/drawable/Drawable;

    #@64
    move-result-object v4

    #@65
    invoke-virtual {v0, v4}, Landroid/app/AlertDialog$Builder;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/app/AlertDialog$Builder;

    #@68
    move-result-object v4

    #@69
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getHeaderTitle()Ljava/lang/CharSequence;

    #@6c
    move-result-object v5

    #@6d
    invoke-virtual {v4, v5}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    #@70
    goto :goto_0
.end method
