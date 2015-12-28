.class Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;
.super Landroid/support/v7/widget/WindowCallbackWrapper;
.source "ToolbarActionBar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/ToolbarActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ToolbarCallbackWrapper"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/app/ToolbarActionBar;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/support/v7/internal/app/WindowCallback;)V
    .locals 0
    .param p2, "wrapped"    # Landroid/support/v7/internal/app/WindowCallback;

    #@0
    .prologue
    .line 510
    iput-object p1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@2
    .line 511
    invoke-direct {p0, p2}, Landroid/support/v7/widget/WindowCallbackWrapper;-><init>(Landroid/support/v7/internal/app/WindowCallback;)V

    #@5
    .line 512
    return-void
.end method


# virtual methods
.method public onCreatePanelView(I)Landroid/view/View;
    .locals 3
    .param p1, "featureId"    # I

    #@0
    .prologue
    .line 526
    packed-switch p1, :pswitch_data_0

    #@3
    .line 545
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/widget/WindowCallbackWrapper;->onCreatePanelView(I)Landroid/view/View;

    #@6
    move-result-object v1

    #@7
    :goto_0
    return-object v1

    #@8
    .line 528
    :pswitch_0
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@a
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$100(Landroid/support/v7/internal/app/ToolbarActionBar;)Z

    #@d
    move-result v1

    #@e
    if-nez v1, :cond_1

    #@10
    .line 530
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@12
    invoke-virtual {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->populateOptionsMenu()V

    #@15
    .line 531
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@17
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbar:Landroid/support/v7/widget/Toolbar;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$400(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/widget/Toolbar;

    #@1a
    move-result-object v1

    #@1b
    iget-object v2, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@1d
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mMenuInvalidator:Ljava/lang/Runnable;
    invoke-static {v2}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$300(Landroid/support/v7/internal/app/ToolbarActionBar;)Ljava/lang/Runnable;

    #@20
    move-result-object v2

    #@21
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/Toolbar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@24
    .line 534
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@26
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$100(Landroid/support/v7/internal/app/ToolbarActionBar;)Z

    #@29
    move-result v1

    #@2a
    if-eqz v1, :cond_0

    #@2c
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@2e
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@31
    move-result-object v1

    #@32
    if-eqz v1, :cond_0

    #@34
    .line 536
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@36
    # invokes: Landroid/support/v7/internal/app/ToolbarActionBar;->getMenu()Landroid/view/Menu;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$500(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/view/Menu;

    #@39
    move-result-object v0

    #@3a
    .line 538
    .local v0, "menu":Landroid/view/Menu;
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@3c
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@3f
    move-result-object v1

    #@40
    const/4 v2, 0x0

    #@41
    invoke-interface {v1, p1, v2, v0}, Landroid/support/v7/internal/app/WindowCallback;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@44
    move-result v1

    #@45
    if-eqz v1, :cond_0

    #@47
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@49
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$000(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/app/WindowCallback;

    #@4c
    move-result-object v1

    #@4d
    invoke-interface {v1, p1, v0}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@50
    move-result v1

    #@51
    if-eqz v1, :cond_0

    #@53
    .line 540
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@55
    # invokes: Landroid/support/v7/internal/app/ToolbarActionBar;->getListMenuView(Landroid/view/Menu;)Landroid/view/View;
    invoke-static {v1, v0}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$600(Landroid/support/v7/internal/app/ToolbarActionBar;Landroid/view/Menu;)Landroid/view/View;

    #@58
    move-result-object v1

    #@59
    goto :goto_0

    #@5a
    .line 526
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
    .end packed-switch
.end method

.method public onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 3
    .param p1, "featureId"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 516
    invoke-super {p0, p1, p2, p3}, Landroid/support/v7/widget/WindowCallbackWrapper;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@3
    move-result v0

    #@4
    .line 517
    .local v0, "result":Z
    if-eqz v0, :cond_0

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$100(Landroid/support/v7/internal/app/ToolbarActionBar;)Z

    #@b
    move-result v1

    #@c
    if-nez v1, :cond_0

    #@e
    .line 518
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@10
    # getter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mDecorToolbar:Landroid/support/v7/internal/widget/DecorToolbar;
    invoke-static {v1}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$200(Landroid/support/v7/internal/app/ToolbarActionBar;)Landroid/support/v7/internal/widget/DecorToolbar;

    #@13
    move-result-object v1

    #@14
    invoke-interface {v1}, Landroid/support/v7/internal/widget/DecorToolbar;->setMenuPrepared()V

    #@17
    .line 519
    iget-object v1, p0, Landroid/support/v7/internal/app/ToolbarActionBar$ToolbarCallbackWrapper;->this$0:Landroid/support/v7/internal/app/ToolbarActionBar;

    #@19
    const/4 v2, 0x1

    #@1a
    # setter for: Landroid/support/v7/internal/app/ToolbarActionBar;->mToolbarMenuPrepared:Z
    invoke-static {v1, v2}, Landroid/support/v7/internal/app/ToolbarActionBar;->access$102(Landroid/support/v7/internal/app/ToolbarActionBar;Z)Z

    #@1d
    .line 521
    :cond_0
    return v0
.end method
