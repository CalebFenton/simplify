.class final Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;
.super Ljava/lang/Object;
.source "ToolbarActionBar.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/ToolbarActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "MenuBuilderCallback"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/app/ToolbarActionBar;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V
    .locals 0

    #@0
    .prologue
    .line 625
    iput-object p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

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
    .line 625
    invoke-direct {p0, p1}, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;-><init>(Landroid/support/v7/internal/app/ToolbarActionBar;)V

    #@3
    return-void
.end method


# virtual methods
.method public onMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 629
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public onMenuModeChange(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 4
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    const/16 v3, 0x8

    #@2
    .line 634
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@4
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@7
    move-result-object v0

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 635
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@c
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$400(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/widget/Toolbar;

    #@f
    move-result-object v0

    #@10
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->isOverflowMenuShowing()Z

    #@13
    move-result v0

    #@14
    if-eqz v0, :cond_1

    #@16
    .line 636
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@18
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@1b
    move-result-object v0

    #@1c
    invoke-interface {v0, v3, p1}, Landroid/support/v7/internal/app/WindowCallback;->onPanelClosed(ILandroid/view/Menu;)V

    #@1f
    .line 642
    :cond_0
    :goto_0
    return-void

    #@20
    .line 637
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@22
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@25
    move-result-object v0

    #@26
    const/4 v1, 0x0

    #@27
    const/4 v2, 0x0

    #@28
    invoke-interface {v0, v1, v2, p1}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@2b
    move-result v0

    #@2c
    if-eqz v0, :cond_0

    #@2e
    .line 639
    iget-object v0, p0, Landroid/support/v7/internal/app/ToolbarActionBar$MenuBuilderCallback;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@30
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@33
    move-result-object v0

    #@34
    invoke-interface {v0, v3, p1}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@37
    goto :goto_0
.end method
