.class public Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;
.super Landroid/support/v7/view/ActionMode;
.source "WindowDecorActionBar.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/WindowDecorActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "ActionModeImpl"
.end annotation


# instance fields
.field private mCallback:Landroid/support/v7/view/ActionMode$Callback;

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

.field private mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

.field final synthetic this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/app/WindowDecorActionBar;Landroid/support/v7/view/ActionMode$Callback;)V
    .locals 2
    .param p2, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 951
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-direct {p0}, Landroid/support/v7/view/ActionMode;-><init>()V

    #@5
    .line 952
    iput-object p2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@7
    .line 953
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    invoke-virtual {p1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getThemedContext()Landroid/content/Context;

    #@c
    move-result-object v1

    #@d
    invoke-direct {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;-><init>(Landroid/content/Context;)V

    #@10
    const/4 v1, 0x1

    #@11
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setDefaultShowAsAction(I)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@14
    move-result-object v0

    #@15
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@17
    .line 955
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@19
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@1c
    .line 956
    return-void
.end method


# virtual methods
.method public dispatchOnCreate()Z
    .locals 2

    #@0
    .prologue
    .line 1010
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@5
    .line 1012
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    invoke-interface {v0, p0, v1}, Landroid/support/v7/view/ActionMode$Callback;->onCreateActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@c
    move-result v0

    #@d
    .line 1014
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@f
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@12
    return v0

    #@13
    :catchall_0
    move-exception v0

    #@14
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@16
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@19
    throw v0
.end method

.method public finish()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    const/4 v2, 0x0

    #@2
    .line 970
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@4
    iget-object v0, v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@6
    if-eq v0, p0, :cond_0

    #@8
    .line 997
    :goto_0
    return-void

    #@9
    .line 979
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@b
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenByApp:Z
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$700(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z

    #@e
    move-result v0

    #@f
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@11
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mHiddenBySystem:Z
    invoke-static {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$800(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z

    #@14
    move-result v1

    #@15
    # invokes: Landroid/support/v7/internal/app/WindowDecorActionBar;->checkShowingFlags(ZZZ)Z
    invoke-static {v0, v1, v2}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$900(ZZZ)Z

    #@18
    move-result v0

    #@19
    if-nez v0, :cond_1

    #@1b
    .line 982
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@1d
    iput-object p0, v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredDestroyActionMode:Landroid/support/v7/view/ActionMode;

    #@1f
    .line 983
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@21
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@23
    iput-object v1, v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mDeferredModeDestroyCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@25
    .line 987
    :goto_1
    iput-object v3, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@27
    .line 988
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@29
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/app/WindowDecorActionBar;->animateToMode(Z)V

    #@2c
    .line 991
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2e
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@31
    move-result-object v0

    #@32
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->closeMode()V

    #@35
    .line 992
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@37
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1100(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/DecorToolbar;

    #@3a
    move-result-object v0

    #@3b
    invoke-interface {v0}, Landroid/support/v7/internal/widget/DecorToolbar;->getViewGroup()Landroid/view/ViewGroup;

    #@3e
    move-result-object v0

    #@3f
    const/16 v1, 0x20

    #@41
    invoke-virtual {v0, v1}, Landroid/view/ViewGroup;->sendAccessibilityEvent(I)V

    #@44
    .line 994
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@46
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$600(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@49
    move-result-object v0

    #@4a
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@4c
    iget-boolean v1, v1, Landroid/support/v7/internal/app/WindowDecorActionBar;->mHideOnContentScroll:Z

    #@4e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->setHideOnContentScrollEnabled(Z)V

    #@51
    .line 996
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@53
    iput-object v3, v0, Landroid/support/v7/internal/app/WindowDecorActionBar;->mActionMode:Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;

    #@55
    goto :goto_0

    #@56
    .line 985
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@58
    invoke-interface {v0, p0}, Landroid/support/v7/view/ActionMode$Callback;->onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V

    #@5b
    goto :goto_1
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 1067
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCustomView:Ljava/lang/ref/WeakReference;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCustomView:Ljava/lang/ref/WeakReference;

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
    .line 965
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method public getMenuInflater()Landroid/view/MenuInflater;
    .locals 2

    #@0
    .prologue
    .line 960
    new-instance v0, Landroid/support/v7/internal/view/SupportMenuInflater;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@4
    invoke-virtual {v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getThemedContext()Landroid/content/Context;

    #@7
    move-result-object v1

    #@8
    invoke-direct {v0, v1}, Landroid/support/v7/internal/view/SupportMenuInflater;-><init>(Landroid/content/Context;)V

    #@b
    return-object v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1051
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getSubtitle()Ljava/lang/CharSequence;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1046
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getTitle()Ljava/lang/CharSequence;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method public invalidate()V
    .locals 2

    #@0
    .prologue
    .line 1001
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@5
    .line 1003
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@9
    invoke-interface {v0, p0, v1}, Landroid/support/v7/view/ActionMode$Callback;->onPrepareActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@c
    .line 1005
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@e
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@11
    .line 1007
    return-void

    #@12
    .line 1005
    :catchall_0
    move-exception v0

    #@13
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@15
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@18
    throw v0
.end method

.method public isTitleOptional()Z
    .locals 1

    #@0
    .prologue
    .line 1062
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->isTitleOptional()Z

    #@9
    move-result v0

    #@a
    return v0
.end method

.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 1079
    return-void
.end method

.method public onCloseSubMenu(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    .line 1095
    return-void
.end method

.method public onMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 1071
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1072
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@6
    invoke-interface {v0, p0, p2}, Landroid/support/v7/view/ActionMode$Callback;->onActionItemClicked(Landroid/support/v7/view/ActionMode;Landroid/view/MenuItem;)Z

    #@9
    move-result v0

    #@a
    .line 1074
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public onMenuModeChange(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 1098
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1103
    :goto_0
    return-void

    #@5
    .line 1101
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->invalidate()V

    #@8
    .line 1102
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@a
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->showOverflowMenu()Z

    #@11
    goto :goto_0
.end method

.method public onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z
    .locals 3
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1082
    iget-object v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCallback:Landroid/support/v7/view/ActionMode$Callback;

    #@3
    if-nez v1, :cond_1

    #@5
    .line 1083
    const/4 v0, 0x0

    #@6
    .line 1091
    :cond_0
    :goto_0
    return v0

    #@7
    .line 1086
    :cond_1
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->hasVisibleItems()Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 1090
    new-instance v1, Landroid/support/v7/internal/view/menu/MenuPopupHelper;

    #@f
    iget-object v2, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@11
    invoke-virtual {v2}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getThemedContext()Landroid/content/Context;

    #@14
    move-result-object v2

    #@15
    invoke-direct {v1, v2, p1}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;-><init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@18
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;->show()V

    #@1b
    goto :goto_0
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1020
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setCustomView(Landroid/view/View;)V

    #@9
    .line 1021
    new-instance v0, Ljava/lang/ref/WeakReference;

    #@b
    invoke-direct {v0, p1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    #@e
    iput-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->mCustomView:Ljava/lang/ref/WeakReference;

    #@10
    .line 1022
    return-void
.end method

.method public setSubtitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1041
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->setSubtitle(Ljava/lang/CharSequence;)V

    #@11
    .line 1042
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1026
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setSubtitle(Ljava/lang/CharSequence;)V

    #@9
    .line 1027
    return-void
.end method

.method public setTitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1036
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->setTitle(Ljava/lang/CharSequence;)V

    #@11
    .line 1037
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1031
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setTitle(Ljava/lang/CharSequence;)V

    #@9
    .line 1032
    return-void
.end method

.method public setTitleOptionalHint(Z)V
    .locals 1
    .param p1, "titleOptional"    # Z

    #@0
    .prologue
    .line 1056
    invoke-super {p0, p1}, Landroid/support/v7/view/ActionMode;->setTitleOptionalHint(Z)V

    #@3
    .line 1057
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$ActionModeImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@5
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextView:Landroid/support/v7/internal/widget/ActionBarContextView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContextView;

    #@8
    move-result-object v0

    #@9
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setTitleOptional(Z)V

    #@c
    .line 1058
    return-void
.end method
