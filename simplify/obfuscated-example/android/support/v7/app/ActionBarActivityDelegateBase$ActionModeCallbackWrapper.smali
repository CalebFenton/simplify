.class Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Landroid/support/v7/view/ActionMode$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarActivityDelegateBase;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ActionModeCallbackWrapper"
.end annotation


# instance fields
.field private mWrapped:Landroid/support/v7/view/ActionMode$Callback;

.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;


# direct methods
.method public constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;Landroid/support/v7/view/ActionMode$Callback;)V
    .locals 0
    .param p2, "wrapped"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 1292
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 1293
    iput-object p2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->mWrapped:Landroid/support/v7/view/ActionMode$Callback;

    #@7
    .line 1294
    return-void
.end method


# virtual methods
.method public onActionItemClicked(Landroid/support/v7/view/ActionMode;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 1305
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->mWrapped:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    invoke-interface {v0, p1, p2}, Landroid/support/v7/view/ActionMode$Callback;->onActionItemClicked(Landroid/support/v7/view/ActionMode;Landroid/view/MenuItem;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onCreateActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1297
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->mWrapped:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    invoke-interface {v0, p1, p2}, Landroid/support/v7/view/ActionMode$Callback;->onCreateActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V
    .locals 2
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    .line 1309
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->mWrapped:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/view/ActionMode$Callback;->onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V

    #@5
    .line 1310
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@7
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@9
    if-eqz v0, :cond_3

    #@b
    .line 1311
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@d
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@f
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivity;->getWindow()Landroid/view/Window;

    #@12
    move-result-object v0

    #@13
    invoke-virtual {v0}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@16
    move-result-object v0

    #@17
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@19
    iget-object v1, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mShowActionModePopup:Ljava/lang/Runnable;

    #@1b
    invoke-virtual {v0, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@1e
    .line 1312
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@20
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@22
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->dismiss()V

    #@25
    .line 1319
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@27
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@29
    if-eqz v0, :cond_1

    #@2b
    .line 1320
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2d
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2f
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->removeAllViews()V

    #@32
    .line 1322
    :cond_1
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@34
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@36
    if-eqz v0, :cond_2

    #@38
    .line 1324
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@3a
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@3c
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@3e
    iget-object v1, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@40
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBarActivity;->onSupportActionModeFinished(Landroid/support/v7/view/ActionMode;)V
    :try_end_0
    .catch Ljava/lang/AbstractMethodError; {:try_start_0 .. :try_end_0} :catch_0

    #@43
    .line 1329
    :cond_2
    :goto_1
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@45
    const/4 v1, 0x0

    #@46
    iput-object v1, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionMode:Landroid/support/v7/view/ActionMode;

    #@48
    .line 1330
    return-void

    #@49
    .line 1313
    :cond_3
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@4b
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@4d
    if-eqz v0, :cond_0

    #@4f
    .line 1314
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@51
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@53
    const/16 v1, 0x8

    #@55
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setVisibility(I)V

    #@58
    .line 1315
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@5a
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@5c
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getParent()Landroid/view/ViewParent;

    #@5f
    move-result-object v0

    #@60
    if-eqz v0, :cond_0

    #@62
    .line 1316
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@64
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@66
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getParent()Landroid/view/ViewParent;

    #@69
    move-result-object v0

    #@6a
    check-cast v0, Landroid/view/View;

    #@6c
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@6f
    goto :goto_0

    #@70
    .line 1325
    :catch_0
    move-exception v0

    #@71
    goto :goto_1
.end method

.method public onPrepareActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1301
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$ActionModeCallbackWrapper;->mWrapped:Landroid/support/v7/view/ActionMode$Callback;

    #@2
    invoke-interface {v0, p1, p2}, Landroid/support/v7/view/ActionMode$Callback;->onPrepareActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method
