.class Landroid/support/v7/app/ActionBarActivityDelegate$1;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegate.java"

# interfaces
.implements Landroid/support/v7/internal/app/WindowCallback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarActivityDelegate;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegate;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegate;)V
    .locals 0

    #@0
    .prologue
    .line 71
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onCreatePanelMenu(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 79
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->superOnCreatePanelMenu(ILandroid/view/Menu;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public onCreatePanelView(I)Landroid/view/View;
    .locals 1
    .param p1, "featureId"    # I

    #@0
    .prologue
    .line 104
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public onMenuItemSelected(ILandroid/view/MenuItem;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menuItem"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 74
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public onMenuOpened(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->onMenuOpened(ILandroid/view/Menu;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public onPanelClosed(ILandroid/view/Menu;)V
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->onPanelClosed(ILandroid/view/Menu;)V

    #@7
    .line 90
    return-void
.end method

.method public onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menuView"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 84
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    invoke-virtual {v0, p1, p2, p3}, Landroid/support/v7/app/ActionBarActivity;->superOnPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public startActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 1
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 99
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->startSupportActionModeFromWindow(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method
