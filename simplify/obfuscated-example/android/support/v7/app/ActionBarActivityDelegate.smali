.class abstract Landroid/support/v7/app/ActionBarActivityDelegate;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegate.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/app/ActionBarActivityDelegate$ActionBarDrawableToggleImpl;
    }
.end annotation


# static fields
.field static final METADATA_UI_OPTIONS:Ljava/lang/String; = "android.support.UI_OPTIONS"

.field private static final TAG:Ljava/lang/String; = "ActionBarActivityDelegate"


# instance fields
.field private mActionBar:Landroid/support/v7/app/ActionBar;

.field final mActivity:Landroid/support/v7/app/ActionBarActivity;

.field final mDefaultWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

.field mHasActionBar:Z

.field private mIsDestroyed:Z

.field mIsFloating:Z

.field private mMenuInflater:Landroid/view/MenuInflater;

.field mOverlayActionBar:Z

.field mOverlayActionMode:Z

.field private mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivity;)V
    .locals 1
    .param p1, "activity"    # Landroid/support/v7/app/ActionBarActivity;

    #@0
    .prologue
    .line 111
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 71
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegate$1;

    #@5
    invoke-direct {v0, p0}, Landroid/support/v7/app/ActionBarActivityDelegate$1;-><init>(Landroid/support/v7/app/ActionBarActivityDelegate;)V

    #@8
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mDefaultWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@a
    .line 112
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@c
    .line 113
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mDefaultWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@e
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@10
    .line 114
    return-void
.end method

.method static createDelegate(Landroid/support/v7/app/ActionBarActivity;)Landroid/support/v7/app/ActionBarActivityDelegate;
    .locals 2
    .param p0, "activity"    # Landroid/support/v7/app/ActionBarActivity;

    #@0
    .prologue
    .line 50
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xb

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 51
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegateHC;

    #@8
    invoke-direct {v0, p0}, Landroid/support/v7/app/ActionBarActivityDelegateHC;-><init>(Landroid/support/v7/app/ActionBarActivity;)V

    #@b
    .line 53
    :goto_0
    return-object v0

    #@c
    :cond_0
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@e
    invoke-direct {v0, p0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;-><init>(Landroid/support/v7/app/ActionBarActivity;)V

    #@11
    goto :goto_0
.end method


# virtual methods
.method abstract addContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
.end method

.method abstract createSupportActionBar()Landroid/support/v7/app/ActionBar;
.end method

.method abstract createView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    .param p2    # Landroid/content/Context;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p3    # Landroid/util/AttributeSet;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
.end method

.method final destroy()V
    .locals 1

    #@0
    .prologue
    .line 323
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mIsDestroyed:Z

    #@3
    .line 324
    return-void
.end method

.method protected final getActionBarThemedContext()Landroid/content/Context;
    .locals 2

    #@0
    .prologue
    .line 258
    const/4 v1, 0x0

    #@1
    .line 261
    .local v1, "context":Landroid/content/Context;
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@4
    move-result-object v0

    #@5
    .line 262
    .local v0, "ab":Landroid/support/v7/app/ActionBar;
    if-eqz v0, :cond_0

    #@7
    .line 263
    invoke-virtual {v0}, Landroid/support/v7/app/ActionBar;->getThemedContext()Landroid/content/Context;

    #@a
    move-result-object v1

    #@b
    .line 266
    :cond_0
    if-nez v1, :cond_1

    #@d
    .line 267
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@f
    .line 269
    :cond_1
    return-object v1
.end method

.method final getDrawerToggleDelegate()Landroid/support/v4/app/ActionBarDrawerToggle$Delegate;
    .locals 2

    #@0
    .prologue
    .line 228
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegate$ActionBarDrawableToggleImpl;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-direct {v0, p0, v1}, Landroid/support/v7/app/ActionBarActivityDelegate$ActionBarDrawableToggleImpl;-><init>(Landroid/support/v7/app/ActionBarActivityDelegate;Landroid/support/v7/app/ActionBarActivityDelegate$1;)V

    #@6
    return-object v0
.end method

.method abstract getHomeAsUpIndicatorAttrId()I
.end method

.method getMenuInflater()Landroid/view/MenuInflater;
    .locals 2

    #@0
    .prologue
    .line 140
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mMenuInflater:Landroid/view/MenuInflater;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 141
    new-instance v0, Landroid/support/v7/internal/view/SupportMenuInflater;

    #@6
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegate;->getActionBarThemedContext()Landroid/content/Context;

    #@9
    move-result-object v1

    #@a
    invoke-direct {v0, v1}, Landroid/support/v7/internal/view/SupportMenuInflater;-><init>(Landroid/content/Context;)V

    #@d
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mMenuInflater:Landroid/view/MenuInflater;

    #@f
    .line 143
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mMenuInflater:Landroid/view/MenuInflater;

    #@11
    return-object v0
.end method

.method final getSupportActionBar()Landroid/support/v7/app/ActionBar;
    .locals 1

    #@0
    .prologue
    .line 121
    iget-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mHasActionBar:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 122
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActionBar:Landroid/support/v7/app/ActionBar;

    #@6
    if-nez v0, :cond_0

    #@8
    .line 123
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarActivityDelegate;->createSupportActionBar()Landroid/support/v7/app/ActionBar;

    #@b
    move-result-object v0

    #@c
    iput-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActionBar:Landroid/support/v7/app/ActionBar;

    #@e
    .line 126
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActionBar:Landroid/support/v7/app/ActionBar;

    #@10
    return-object v0
.end method

.method final getUiOptionsFromMetadata()Ljava/lang/String;
    .locals 7

    #@0
    .prologue
    .line 241
    :try_start_0
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@2
    invoke-virtual {v4}, Landroid/support/v7/app/ActionBarActivity;->getPackageManager()Landroid/content/pm/PackageManager;

    #@5
    move-result-object v2

    #@6
    .line 242
    .local v2, "pm":Landroid/content/pm/PackageManager;
    iget-object v4, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@8
    invoke-virtual {v4}, Landroid/support/v7/app/ActionBarActivity;->getComponentName()Landroid/content/ComponentName;

    #@b
    move-result-object v4

    #@c
    const/16 v5, 0x80

    #@e
    invoke-virtual {v2, v4, v5}, Landroid/content/pm/PackageManager;->getActivityInfo(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;

    #@11
    move-result-object v1

    #@12
    .line 245
    .local v1, "info":Landroid/content/pm/ActivityInfo;
    const/4 v3, 0x0

    #@13
    .line 246
    .local v3, "uiOptions":Ljava/lang/String;
    iget-object v4, v1, Landroid/content/pm/ActivityInfo;->metaData:Landroid/os/Bundle;

    #@15
    if-eqz v4, :cond_0

    #@17
    .line 247
    iget-object v4, v1, Landroid/content/pm/ActivityInfo;->metaData:Landroid/os/Bundle;

    #@19
    const-string v5, "android.support.UI_OPTIONS"

    #@1b
    invoke-virtual {v4, v5}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@1e
    move-result-object v3

    #@1f
    .line 253
    .end local v1    # "info":Landroid/content/pm/ActivityInfo;
    .end local v2    # "pm":Landroid/content/pm/PackageManager;
    .end local v3    # "uiOptions":Ljava/lang/String;
    :cond_0
    :goto_0
    return-object v3

    #@20
    .line 250
    :catch_0
    move-exception v0

    #@21
    .line 251
    .local v0, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    const-string v4, "ActionBarActivityDelegate"

    #@23
    new-instance v5, Ljava/lang/StringBuilder;

    #@25
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@28
    const-string v6, "getUiOptionsFromMetadata: Activity \'"

    #@2a
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2d
    move-result-object v5

    #@2e
    iget-object v6, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@30
    invoke-virtual {v6}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@33
    move-result-object v6

    #@34
    invoke-virtual {v6}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@37
    move-result-object v6

    #@38
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3b
    move-result-object v5

    #@3c
    const-string v6, "\' not in manifest"

    #@3e
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@41
    move-result-object v5

    #@42
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@45
    move-result-object v5

    #@46
    invoke-static {v4, v5}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@49
    .line 253
    const/4 v3, 0x0

    #@4a
    goto :goto_0
.end method

.method final getV7DrawerToggleDelegate()Landroid/support/v7/app/ActionBarDrawerToggle$Delegate;
    .locals 2

    #@0
    .prologue
    .line 232
    new-instance v0, Landroid/support/v7/app/ActionBarActivityDelegate$ActionBarDrawableToggleImpl;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-direct {v0, p0, v1}, Landroid/support/v7/app/ActionBarActivityDelegate$ActionBarDrawableToggleImpl;-><init>(Landroid/support/v7/app/ActionBarActivityDelegate;Landroid/support/v7/app/ActionBarActivityDelegate$1;)V

    #@6
    return-object v0
.end method

.method final getWindowCallback()Landroid/support/v7/internal/app/WindowCallback;
    .locals 1

    #@0
    .prologue
    .line 319
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    return-object v0
.end method

.method final isDestroyed()Z
    .locals 1

    #@0
    .prologue
    .line 327
    iget-boolean v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mIsDestroyed:Z

    #@2
    return v0
.end method

.method abstract onBackPressed()Z
.end method

.method abstract onConfigurationChanged(Landroid/content/res/Configuration;)V
.end method

.method abstract onContentChanged()V
.end method

.method onCreate(Landroid/os/Bundle;)V
    .locals 5
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 147
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@4
    sget-object v2, Landroid/support/v7/appcompat/R$styleable;->Theme:[I

    #@6
    invoke-virtual {v1, v2}, Landroid/support/v7/app/ActionBarActivity;->obtainStyledAttributes([I)Landroid/content/res/TypedArray;

    #@9
    move-result-object v0

    #@a
    .line 149
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->Theme_windowActionBar:I

    #@c
    invoke-virtual {v0, v1}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@f
    move-result v1

    #@10
    if-nez v1, :cond_0

    #@12
    .line 150
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@15
    .line 151
    new-instance v1, Ljava/lang/IllegalStateException;

    #@17
    const-string v2, "You need to use a Theme.AppCompat theme (or descendant) with this activity."

    #@19
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1c
    throw v1

    #@1d
    .line 155
    :cond_0
    sget v1, Landroid/support/v7/appcompat/R$styleable;->Theme_windowActionBar:I

    #@1f
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@22
    move-result v1

    #@23
    if-eqz v1, :cond_1

    #@25
    .line 156
    iput-boolean v4, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mHasActionBar:Z

    #@27
    .line 158
    :cond_1
    sget v1, Landroid/support/v7/appcompat/R$styleable;->Theme_windowActionBarOverlay:I

    #@29
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@2c
    move-result v1

    #@2d
    if-eqz v1, :cond_2

    #@2f
    .line 159
    iput-boolean v4, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mOverlayActionBar:Z

    #@31
    .line 161
    :cond_2
    sget v1, Landroid/support/v7/appcompat/R$styleable;->Theme_windowActionModeOverlay:I

    #@33
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@36
    move-result v1

    #@37
    if-eqz v1, :cond_3

    #@39
    .line 162
    iput-boolean v4, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mOverlayActionMode:Z

    #@3b
    .line 164
    :cond_3
    sget v1, Landroid/support/v7/appcompat/R$styleable;->Theme_android_windowIsFloating:I

    #@3d
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@40
    move-result v1

    #@41
    iput-boolean v1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mIsFloating:Z

    #@43
    .line 165
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@46
    .line 166
    return-void
.end method

.method abstract onCreatePanelMenu(ILandroid/view/Menu;)Z
.end method

.method abstract onCreatePanelView(I)Landroid/view/View;
.end method

.method onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 222
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method abstract onKeyShortcut(ILandroid/view/KeyEvent;)Z
.end method

.method abstract onMenuOpened(ILandroid/view/Menu;)Z
.end method

.method abstract onPanelClosed(ILandroid/view/Menu;)V
.end method

.method abstract onPostResume()V
.end method

.method onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z
    .locals 2
    .param p1, "view"    # Landroid/view/View;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 198
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-ge v0, v1, :cond_0

    #@6
    .line 202
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@8
    invoke-virtual {v0, p2}, Landroid/support/v7/app/ActionBarActivity;->onPrepareOptionsMenu(Landroid/view/Menu;)Z

    #@b
    move-result v0

    #@c
    .line 204
    :goto_0
    return v0

    #@d
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActivity:Landroid/support/v7/app/ActionBarActivity;

    #@f
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/app/ActionBarActivity;->superOnPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z

    #@12
    move-result v0

    #@13
    goto :goto_0
.end method

.method abstract onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
.end method

.method abstract onStop()V
.end method

.method abstract onTitleChanged(Ljava/lang/CharSequence;)V
.end method

.method final peekSupportActionBar()Landroid/support/v7/app/ActionBar;
    .locals 1

    #@0
    .prologue
    .line 130
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActionBar:Landroid/support/v7/app/ActionBar;

    #@2
    return-object v0
.end method

.method abstract setContentView(I)V
.end method

.method abstract setContentView(Landroid/view/View;)V
.end method

.method abstract setContentView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
.end method

.method protected final setSupportActionBar(Landroid/support/v7/app/ActionBar;)V
    .locals 0
    .param p1, "actionBar"    # Landroid/support/v7/app/ActionBar;

    #@0
    .prologue
    .line 134
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mActionBar:Landroid/support/v7/app/ActionBar;

    #@2
    .line 135
    return-void
.end method

.method abstract setSupportActionBar(Landroid/support/v7/widget/Toolbar;)V
.end method

.method abstract setSupportProgress(I)V
.end method

.method abstract setSupportProgressBarIndeterminate(Z)V
.end method

.method abstract setSupportProgressBarIndeterminateVisibility(Z)V
.end method

.method abstract setSupportProgressBarVisibility(Z)V
.end method

.method final setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V
    .locals 2
    .param p1, "callback"    # Landroid/support/v7/internal/app/WindowCallback;

    #@0
    .prologue
    .line 312
    if-nez p1, :cond_0

    #@2
    .line 313
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@4
    const-string v1, "callback can not be null"

    #@6
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@9
    throw v0

    #@a
    .line 315
    :cond_0
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegate;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@c
    .line 316
    return-void
.end method

.method abstract startSupportActionMode(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
.end method

.method abstract startSupportActionModeFromWindow(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
.end method

.method abstract supportInvalidateOptionsMenu()V
.end method

.method abstract supportRequestWindowFeature(I)Z
.end method
