.class final Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarActivityDelegateBase;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "PanelMenuPresenterCallback"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;


# direct methods
.method private constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V
    .locals 0

    #@0
    .prologue
    .line 1333
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/app/ActionBarActivityDelegateBase;
    .param p2, "x1"    # Landroid/support/v7/app/ActionBarActivityDelegateBase$1;

    #@0
    .prologue
    .line 1333
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;-><init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V

    #@3
    return-void
.end method


# virtual methods
.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 6
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1336
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getRootMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@4
    move-result-object v2

    #@5
    .line 1337
    .local v2, "parentMenu":Landroid/view/Menu;
    if-eq v2, p1, :cond_2

    #@7
    move v0, v3

    #@8
    .line 1338
    .local v0, "isSubMenu":Z
    :goto_0
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@a
    if-eqz v0, :cond_0

    #@c
    move-object p1, v2

    #@d
    .end local p1    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_0
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->findMenuPanel(Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    invoke-static {v4, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$600(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/view/Menu;)Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;

    #@10
    move-result-object v1

    #@11
    .line 1339
    .local v1, "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    if-eqz v1, :cond_1

    #@13
    .line 1340
    if-eqz v0, :cond_3

    #@15
    .line 1341
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@17
    iget v5, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;->featureId:I

    #@19
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->callOnPanelClosed(ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V
    invoke-static {v4, v5, v1, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$700(Landroid/support/v7/app/ActionBarActivityDelegateBase;ILandroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Landroid/view/Menu;)V

    #@1c
    .line 1342
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@1e
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V
    invoke-static {v4, v1, v3}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$800(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@21
    .line 1350
    :cond_1
    :goto_1
    return-void

    #@22
    .line 1337
    .end local v0    # "isSubMenu":Z
    .end local v1    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    .restart local p1    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_2
    const/4 v0, 0x0

    #@23
    goto :goto_0

    #@24
    .line 1346
    .end local p1    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    .restart local v0    # "isSubMenu":Z
    .restart local v1    # "panel":Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;
    :cond_3
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@26
    iget-object v3, v3, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@28
    invoke-virtual {v3}, Landroid/support/v7/app/ActionBarActivity;->closeOptionsMenu()V

    #@2b
    .line 1347
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2d
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->closePanel(Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V
    invoke-static {v3, v1, p2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$800(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelFeatureState;Z)V

    #@30
    goto :goto_1
.end method

.method public onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z
    .locals 2
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 1354
    if-nez p1, :cond_0

    #@2
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@4
    iget-boolean v1, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mHasActionBar:Z

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 1355
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@a
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;

    #@d
    move-result-object v0

    #@e
    .line 1356
    .local v0, "cb":Landroid/support/v7/internal/app/WindowCallback;
    if-eqz v0, :cond_0

    #@10
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$PanelMenuPresenterCallback;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@12
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->isDestroyed()Z

    #@15
    move-result v1

    #@16
    if-nez v1, :cond_0

    #@18
    .line 1357
    const/16 v1, 0x8

    #@1a
    invoke-interface {v0, v1, p1}, Landroid/support/v7/internal/app/WindowCallback;->onMenuOpened(ILandroid/view/Menu;)Z

    #@1d
    .line 1360
    .end local v0    # "cb":Landroid/support/v7/internal/app/WindowCallback;
    :cond_0
    const/4 v1, 0x1

    #@1e
    return v1
.end method
