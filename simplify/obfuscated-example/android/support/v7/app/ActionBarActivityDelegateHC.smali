.class Landroid/support/v7/app/ActionBarActivityDelegateHC;
.super Landroid/support/v7/app/ActionBarActivityDelegateBase;
.source "ActionBarActivityDelegateHC.java"

# interfaces
.implements Landroid/support/v7/internal/widget/NativeActionModeAwareLayout$OnActionModeForChildListener;


# annotations
.annotation build Landroid/annotation/TargetApi;
    value = 0xb
.end annotation


# instance fields
.field private mNativeActionModeAwareLayout:Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivity;)V
    .locals 0
    .param p1, "activity"    # Landroid/support/v7/app/ActionBarActivity;

    #@0
    .prologue
    .line 35
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;-><init>(Landroid/support/v7/app/ActionBarActivity;)V

    #@3
    .line 36
    return-void
.end method


# virtual methods
.method onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 53
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method onSubDecorInstalled()V
    .locals 2

    #@0
    .prologue
    .line 41
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateHC;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@2
    const v1, 0x1020002

    #@5
    invoke-virtual {v0, v1}, Landroid/support/v7/app/ActionBarActivity;->findViewById(I)Landroid/view/View;

    #@8
    move-result-object v0

    #@9
    check-cast v0, Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;

    #@b
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateHC;->mNativeActionModeAwareLayout:Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;

    #@d
    .line 45
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateHC;->mNativeActionModeAwareLayout:Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;

    #@f
    if-eqz v0, :cond_0

    #@11
    .line 46
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateHC;->mNativeActionModeAwareLayout:Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;

    #@13
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/widget/NativeActionModeAwareLayout;->setActionModeForChildListener(Landroid/support/v7/internal/widget/NativeActionModeAwareLayout$OnActionModeForChildListener;)V

    #@16
    .line 48
    :cond_0
    return-void
.end method

.method public startActionModeForChild(Landroid/view/View;Landroid/view/ActionMode$Callback;)Landroid/view/ActionMode;
    .locals 4
    .param p1, "originalView"    # Landroid/view/View;
    .param p2, "callback"    # Landroid/view/ActionMode$Callback;

    #@0
    .prologue
    .line 59
    invoke-virtual {p1}, Landroid/view/View;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    .line 62
    .local v0, "context":Landroid/content/Context;
    new-instance v2, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;

    #@6
    invoke-direct {v2, v0, p2}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;-><init>(Landroid/content/Context;Landroid/view/ActionMode$Callback;)V

    #@9
    invoke-virtual {p0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateHC;->startSupportActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@c
    move-result-object v1

    #@d
    .line 65
    .local v1, "supportActionMode":Landroid/support/v7/view/ActionMode;
    if-eqz v1, :cond_0

    #@f
    .line 67
    new-instance v2, Landroid/support/v7/internal/view/SupportActionModeWrapper;

    #@11
    iget-object v3, p0, Landroid/support/v7/app/ActionBarActivityDelegateHC;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@13
    invoke-direct {v2, v3, v1}, Landroid/support/v7/internal/view/SupportActionModeWrapper;-><init>(Landroid/content/Context;Landroid/support/v7/view/ActionMode;)V

    #@16
    .line 69
    :goto_0
    return-object v2

    #@17
    :cond_0
    const/4 v2, 0x0

    #@18
    goto :goto_0
.end method
