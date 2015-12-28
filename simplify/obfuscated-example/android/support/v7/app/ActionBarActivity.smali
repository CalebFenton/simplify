.class public Landroid/support/v7/app/ActionBarActivity;
.super Landroid/support/v4/app/FragmentActivity;
.source "ActionBarActivity.java"

# interfaces
.implements Landroid/support/v4/app/ActionBarDrawerToggle$DelegateProvider;
.implements Landroid/support/v4/app/TaskStackBuilder$SupportParentable;
.implements Landroid/support/v7/app/ActionBar$Callback;
.implements Landroid/support/v7/app/ActionBarDrawerToggle$TmpDelegateProvider;


# instance fields
.field private mDelegate:Landroid/support/v7/app/ActionBarActivityDelegate;


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 59
    invoke-direct {p0}, Landroid/support/v4/app/FragmentActivity;-><init>()V

    #@3
    return-void
.end method

.method private getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;
    .locals 1

    #@0
    .prologue
    .line 556
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivity;->mDelegate:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 557
    invoke-static {p0}, Landroid/support/v7/app/ActionBarActivityDelegate;->createDelegate(Landroid/support/v7/app/ActionBarActivity;)Landroid/support/v7/app/ActionBarActivityDelegate;

    #@7
    move-result-object v0

    #@8
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivity;->mDelegate:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@a
    .line 559
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivity;->mDelegate:Landroid/support/v7/app/ActionBarActivityDelegate;

    #@c
    return-object v0
.end method


# virtual methods
.method public addContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 117
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->addContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@7
    .line 118
    return-void
.end method

.method public final getDrawerToggleDelegate()Landroid/support/v4/app/ActionBarDrawerToggle$Delegate;
    .locals 1

    #@0
    .prologue
    .line 504
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getDrawerToggleDelegate()Landroid/support/v4/app/ActionBarDrawerToggle$Delegate;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getMenuInflater()Landroid/view/MenuInflater;
    .locals 1

    #@0
    .prologue
    .line 97
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getMenuInflater()Landroid/view/MenuInflater;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getSupportActionBar()Landroid/support/v7/app/ActionBar;
    .locals 1

    #@0
    .prologue
    .line 73
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getSupportParentActivityIntent()Landroid/content/Intent;
    .locals 1

    #@0
    .prologue
    .line 466
    invoke-static {p0}, Landroid/support/v4/app/NavUtils;->getParentActivityIntent(Landroid/app/Activity;)Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getV7DrawerToggleDelegate()Landroid/support/v7/app/ActionBarDrawerToggle$Delegate;
    .locals 1
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    #@0
    .prologue
    .line 513
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getV7DrawerToggleDelegate()Landroid/support/v7/app/ActionBarDrawerToggle$Delegate;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public invalidateOptionsMenu()V
    .locals 1

    #@0
    .prologue
    .line 206
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->supportInvalidateOptionsMenu()V

    #@7
    .line 207
    return-void
.end method

.method public onBackPressed()V
    .locals 1

    #@0
    .prologue
    .line 297
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->onBackPressed()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    .line 298
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    #@d
    .line 300
    :cond_0
    return-void
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 1
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 128
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@3
    .line 129
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@a
    .line 130
    return-void
.end method

.method public final onContentChanged()V
    .locals 1

    #@0
    .prologue
    .line 534
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->onContentChanged()V

    #@7
    .line 535
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 122
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->onCreate(Landroid/os/Bundle;)V

    #@3
    .line 123
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->onCreate(Landroid/os/Bundle;)V

    #@a
    .line 124
    return-void
.end method

.method public onCreatePanelMenu(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 233
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onCreatePanelMenu(ILandroid/view/Menu;)Z

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
    .line 146
    if-nez p1, :cond_0

    #@2
    .line 147
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->onCreatePanelView(I)Landroid/view/View;

    #@9
    move-result-object v0

    #@a
    .line 149
    :goto_0
    return-object v0

    #@b
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->onCreatePanelView(I)Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    goto :goto_0
.end method

.method public onCreateSupportNavigateUpTaskStack(Landroid/support/v4/app/TaskStackBuilder;)V
    .locals 0
    .param p1, "builder"    # Landroid/support/v4/app/TaskStackBuilder;

    #@0
    .prologue
    .line 385
    invoke-virtual {p1, p0}, Landroid/support/v4/app/TaskStackBuilder;->addParentStack(Landroid/app/Activity;)Landroid/support/v4/app/TaskStackBuilder;

    #@3
    .line 386
    return-void
.end method

.method public onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    .locals 2
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "context"    # Landroid/content/Context;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p3, "attrs"    # Landroid/util/AttributeSet;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param

    #@0
    .prologue
    .line 547
    invoke-super {p0, p1, p2, p3}, Landroid/support/v4/app/FragmentActivity;->onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 548
    .local v0, "result":Landroid/view/View;
    if-eqz v0, :cond_0

    #@6
    .line 552
    .end local v0    # "result":Landroid/view/View;
    :goto_0
    return-object v0

    #@7
    .restart local v0    # "result":Landroid/view/View;
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@a
    move-result-object v1

    #@b
    invoke-virtual {v1, p1, p2, p3}, Landroid/support/v7/app/ActionBarActivityDelegate;->createView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    goto :goto_0
.end method

.method protected onDestroy()V
    .locals 1

    #@0
    .prologue
    .line 169
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onDestroy()V

    #@3
    .line 170
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->destroy()V

    #@a
    .line 171
    return-void
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 524
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onKeyDown(ILandroid/view/KeyEvent;)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 525
    const/4 v0, 0x1

    #@7
    .line 527
    :goto_0
    return v0

    #@8
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@b
    move-result-object v0

    #@c
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onKeyDown(ILandroid/view/KeyEvent;)Z

    #@f
    move-result v0

    #@10
    goto :goto_0
.end method

.method public onKeyShortcut(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 518
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onKeyShortcut(ILandroid/view/KeyEvent;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public final onMenuItemSelected(ILandroid/view/MenuItem;)Z
    .locals 3
    .param p1, "featureId"    # I
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 155
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    #@3
    move-result v1

    #@4
    if-eqz v1, :cond_0

    #@6
    .line 156
    const/4 v1, 0x1

    #@7
    .line 164
    :goto_0
    return v1

    #@8
    .line 159
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivity;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@b
    move-result-object v0

    #@c
    .line 160
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    invoke-interface {p2}, Landroid/view/MenuItem;->getItemId()I

    #@f
    move-result v1

    #@10
    const v2, 0x102002c

    #@13
    if-ne v1, v2, :cond_1

    #@15
    if-eqz v0, :cond_1

    #@17
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBar;->getDisplayOptions()I

    #@1a
    move-result v1

    #@1b
    and-int/lit8 v1, v1, 0x4

    #@1d
    if-eqz v1, :cond_1

    #@1f
    .line 162
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivity;->onSupportNavigateUp()Z

    #@22
    move-result v1

    #@23
    goto :goto_0

    #@24
    .line 164
    :cond_1
    const/4 v1, 0x0

    #@25
    goto :goto_0
.end method

.method public onMenuOpened(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 248
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onMenuOpened(ILandroid/view/Menu;)Z

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
    .line 243
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onPanelClosed(ILandroid/view/Menu;)V

    #@7
    .line 244
    return-void
.end method

.method protected onPostResume()V
    .locals 1

    #@0
    .prologue
    .line 140
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onPostResume()V

    #@3
    .line 141
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->onPostResume()V

    #@a
    .line 142
    return-void
.end method

.method protected onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 256
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 238
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2, p3}, Landroid/support/v7/app/ActionBarActivityDelegate;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public onPrepareSupportNavigateUpTaskStack(Landroid/support/v4/app/TaskStackBuilder;)V
    .locals 0
    .param p1, "builder"    # Landroid/support/v4/app/TaskStackBuilder;

    #@0
    .prologue
    .line 404
    return-void
.end method

.method protected onStop()V
    .locals 1

    #@0
    .prologue
    .line 134
    invoke-super {p0}, Landroid/support/v4/app/FragmentActivity;->onStop()V

    #@3
    .line 135
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->onStop()V

    #@a
    .line 136
    return-void
.end method

.method public onSupportActionModeFinished(Landroid/support/v7/view/ActionMode;)V
    .locals 0
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    .line 225
    return-void
.end method

.method public onSupportActionModeStarted(Landroid/support/v7/view/ActionMode;)V
    .locals 0
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    .line 216
    return-void
.end method

.method public onSupportContentChanged()V
    .locals 0

    #@0
    .prologue
    .line 542
    return-void
.end method

.method public onSupportNavigateUp()Z
    .locals 4

    #@0
    .prologue
    .line 430
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivity;->getSupportParentActivityIntent()Landroid/content/Intent;

    #@3
    move-result-object v2

    #@4
    .line 432
    .local v2, "upIntent":Landroid/content/Intent;
    if-eqz v2, :cond_1

    #@6
    .line 433
    invoke-virtual {p0, v2}, Landroid/support/v7/app/ActionBarActivity;->supportShouldUpRecreateTask(Landroid/content/Intent;)Z

    #@9
    move-result v3

    #@a
    if-eqz v3, :cond_0

    #@c
    .line 434
    invoke-static {p0}, Landroid/support/v4/app/TaskStackBuilder;->create(Landroid/content/Context;)Landroid/support/v4/app/TaskStackBuilder;

    #@f
    move-result-object v0

    #@10
    .line 435
    .local v0, "b":Landroid/support/v4/app/TaskStackBuilder;
    invoke-virtual {p0, v0}, Landroid/support/v7/app/ActionBarActivity;->onCreateSupportNavigateUpTaskStack(Landroid/support/v4/app/TaskStackBuilder;)V

    #@13
    .line 436
    invoke-virtual {p0, v0}, Landroid/support/v7/app/ActionBarActivity;->onPrepareSupportNavigateUpTaskStack(Landroid/support/v4/app/TaskStackBuilder;)V

    #@16
    .line 437
    invoke-virtual {v0}, Landroid/support/v4/app/TaskStackBuilder;->startActivities()V

    #@19
    .line 440
    :try_start_0
    invoke-static {p0}, Landroid/support/v4/app/ActivityCompat;->finishAffinity(Landroid/app/Activity;)V
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0

    #@1c
    .line 451
    .end local v0    # "b":Landroid/support/v4/app/TaskStackBuilder;
    :goto_0
    const/4 v3, 0x1

    #@1d
    .line 453
    :goto_1
    return v3

    #@1e
    .line 441
    .restart local v0    # "b":Landroid/support/v4/app/TaskStackBuilder;
    :catch_0
    move-exception v1

    #@1f
    .line 444
    .local v1, "e":Ljava/lang/IllegalStateException;
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivity;->finish()V

    #@22
    goto :goto_0

    #@23
    .line 449
    .end local v0    # "b":Landroid/support/v4/app/TaskStackBuilder;
    .end local v1    # "e":Ljava/lang/IllegalStateException;
    :cond_0
    invoke-virtual {p0, v2}, Landroid/support/v7/app/ActionBarActivity;->supportNavigateUpTo(Landroid/content/Intent;)V

    #@26
    goto :goto_0

    #@27
    .line 453
    :cond_1
    const/4 v3, 0x0

    #@28
    goto :goto_1
.end method

.method protected onTitleChanged(Ljava/lang/CharSequence;I)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;
    .param p2, "color"    # I

    #@0
    .prologue
    .line 175
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onTitleChanged(Ljava/lang/CharSequence;I)V

    #@3
    .line 176
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->onTitleChanged(Ljava/lang/CharSequence;)V

    #@a
    .line 177
    return-void
.end method

.method public setContentView(I)V
    .locals 1
    .param p1, "layoutResID"    # I

    #@0
    .prologue
    .line 102
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setContentView(I)V

    #@7
    .line 103
    return-void
.end method

.method public setContentView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 107
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setContentView(Landroid/view/View;)V

    #@7
    .line 108
    return-void
.end method

.method public setContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 112
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivityDelegate;->setContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@7
    .line 113
    return-void
.end method

.method public setSupportActionBar(Landroid/support/v7/widget/Toolbar;)V
    .locals 1
    .param p1, "toolbar"    # Landroid/support/v7/widget/Toolbar;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 92
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setSupportActionBar(Landroid/support/v7/widget/Toolbar;)V

    #@7
    .line 93
    return-void
.end method

.method public setSupportProgress(I)V
    .locals 1
    .param p1, "progress"    # I

    #@0
    .prologue
    .line 358
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setSupportProgress(I)V

    #@7
    .line 359
    return-void
.end method

.method public setSupportProgressBarIndeterminate(Z)V
    .locals 1
    .param p1, "indeterminate"    # Z

    #@0
    .prologue
    .line 342
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setSupportProgressBarIndeterminate(Z)V

    #@7
    .line 343
    return-void
.end method

.method public setSupportProgressBarIndeterminateVisibility(Z)V
    .locals 1
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 327
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setSupportProgressBarIndeterminateVisibility(Z)V

    #@7
    .line 328
    return-void
.end method

.method public setSupportProgressBarVisibility(Z)V
    .locals 1
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 313
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->setSupportProgressBarVisibility(Z)V

    #@7
    .line 314
    return-void
.end method

.method public startSupportActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 1
    .param p1, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 228
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->startSupportActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method superAddContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;
    .param p2, "lp"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 272
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->addContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 273
    return-void
.end method

.method superOnCreatePanelMenu(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "frameworkMenu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 276
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method superOnMenuOpened(ILandroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 292
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onMenuOpened(ILandroid/view/Menu;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method superOnPanelClosed(ILandroid/view/Menu;)V
    .locals 0
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 288
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onPanelClosed(ILandroid/view/Menu;)V

    #@3
    .line 289
    return-void
.end method

.method superOnPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 284
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method superOnPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 280
    invoke-super {p0, p1, p2, p3}, Landroid/support/v4/app/FragmentActivity;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method superSetContentView(I)V
    .locals 0
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 260
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->setContentView(I)V

    #@3
    .line 261
    return-void
.end method

.method superSetContentView(Landroid/view/View;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 264
    invoke-super {p0, p1}, Landroid/support/v4/app/FragmentActivity;->setContentView(Landroid/view/View;)V

    #@3
    .line 265
    return-void
.end method

.method superSetContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 0
    .param p1, "v"    # Landroid/view/View;
    .param p2, "lp"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 268
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->setContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 269
    return-void
.end method

.method public supportInvalidateOptionsMenu()V
    .locals 1

    #@0
    .prologue
    .line 199
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBarActivityDelegate;->supportInvalidateOptionsMenu()V

    #@7
    .line 200
    return-void
.end method

.method public supportNavigateUpTo(Landroid/content/Intent;)V
    .locals 0
    .param p1, "upIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 499
    invoke-static {p0, p1}, Landroid/support/v4/app/NavUtils;->navigateUpTo(Landroid/app/Activity;Landroid/content/Intent;)V

    #@3
    .line 500
    return-void
.end method

.method public supportRequestWindowFeature(I)Z
    .locals 1
    .param p1, "featureId"    # I

    #@0
    .prologue
    .line 194
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;->getDelegate()Landroid/support/v7/app/ActionBarActivityDelegate;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/app/ActionBarActivityDelegate;->supportRequestWindowFeature(I)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public supportShouldUpRecreateTask(Landroid/content/Intent;)Z
    .locals 1
    .param p1, "targetIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 483
    invoke-static {p0, p1}, Landroid/support/v4/app/NavUtils;->shouldUpRecreateTask(Landroid/app/Activity;Landroid/content/Intent;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method
