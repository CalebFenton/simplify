.class final Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;
.super Ljava/lang/Object;
.source "ToolbarActionBar.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/ToolbarActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "ActionMenuPresenterCallback"
.end annotation


# instance fields
.field private mClosingActionMenu:Z

.field final synthetic this$0:Landroid/support/v7/internal/app/ToolbarActionBar;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V
    .locals 0

    #@0
    .prologue
    .line 578
    iput-object p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/ToolbarActionBar$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/app/ToolbarActionBar;
    .param p2, "x1"    # Landroid/support/v7/internal/app/ToolbarActionBar$1;

    #@0
    .prologue
    .line 578
    invoke-direct {p0, p1}, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V

    #@3
    return-void
.end method


# virtual methods
.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 2
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 592
    iget-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->mClosingActionMenu:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 602
    :goto_0
    return-void

    #@5
    .line 596
    :cond_0
    const/4 v0, 0x1

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->mClosingActionMenu:Z

    #@8
    .line 597
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@a
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$400(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/widget/Toolbar;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->dismissPopupMenus()V

    #@11
    .line 598
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@13
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@16
    move-result-object v0

    #@17
    if-eqz v0, :cond_1

    #@19
    .line 599
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@1b
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@1e
    move-result-object v0

    #@1f
    const/16 v1, 0x8

    #@21
    invoke-interface {v0, v1, p1}, Landroid/support/v7/internal/app/WindowCallback;->onPanelClosed(ILandroid/view/Menu;)V

    #@24
    .line 601
    :cond_1
    const/4 v0, 0x0

    #@25
    iput-boolean v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->mClosingActionMenu:Z

    #@27
    goto :goto_0
.end method

.method public onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z
    .locals 2
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 583
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@5
    move-result-object v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 584
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ActionMenuPresenterCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@a
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@d
    move-result-object v0

    #@e
    const/16 v1, 0x8

    #@10
    invoke-interface {v0, v1, p1}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@13
    .line 585
    const/4 v0, 0x1

    #@14
    .line 587
    :goto_0
    return v0

    #@15
    :cond_0
    const/4 v0, 0x0

    #@16
    goto :goto_0
.end method
