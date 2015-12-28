.class public Landroid/support/v7/internal/view/ActionBarPolicy;
.super Ljava/lang/Object;
.source "ActionBarPolicy.java"


# instance fields
.field private mContext:Landroid/content/Context;


# direct methods
.method private constructor <init>(Landroid/content/Context;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 41
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 42
    iput-object p1, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@5
    .line 43
    return-void
.end method

.method public static get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 38
    new-instance v0, Landroid/support/v7/internal/view/ActionBarPolicy;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v7/internal/view/ActionBarPolicy;-><init>(Landroid/content/Context;)V

    #@5
    return-object v0
.end method


# virtual methods
.method public enableHomeButtonByDefault()Z
    .locals 2

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@5
    move-result-object v0

    #@6
    iget v0, v0, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@8
    const/16 v1, 0xe

    #@a
    if-ge v0, v1, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public getEmbeddedMenuWidthLimit()I
    .locals 1

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@9
    move-result-object v0

    #@a
    iget v0, v0, Landroid/util/DisplayMetrics;->widthPixels:I

    #@c
    div-int/lit8 v0, v0, 0x2

    #@e
    return v0
.end method

.method public getMaxActionButtons()I
    .locals 2

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@5
    move-result-object v0

    #@6
    sget v1, Landroid/support/v7/appcompat/R$integer;->abc_max_action_buttons:I

    #@8
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getInteger(I)I

    #@b
    move-result v0

    #@c
    return v0
.end method

.method public getStackedTabMaxWidth()I
    .locals 2

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@5
    move-result-object v0

    #@6
    sget v1, Landroid/support/v7/appcompat/R$dimen;->abc_action_bar_stacked_tab_max_width:I

    #@8
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    #@b
    move-result v0

    #@c
    return v0
.end method

.method public getTabContainerHeight()I
    .locals 8

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 73
    iget-object v3, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@3
    const/4 v4, 0x0

    #@4
    sget-object v5, Landroid/support/v7/appcompat/R$styleable;->ActionBar:[I

    #@6
    sget v6, Landroid/support/v7/appcompat/R$attr;->actionBarStyle:I

    #@8
    invoke-virtual {v3, v4, v5, v6, v7}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@b
    move-result-object v0

    #@c
    .line 75
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v3, Landroid/support/v7/appcompat/R$styleable;->ActionBar_height:I

    #@e
    invoke-virtual {v0, v3, v7}, Landroid/content/res/TypedArray;->getLayoutDimension(II)I

    #@11
    move-result v1

    #@12
    .line 76
    .local v1, "height":I
    iget-object v3, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@14
    invoke-virtual {v3}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@17
    move-result-object v2

    #@18
    .line 77
    .local v2, "r":Landroid/content/res/Resources;
    invoke-virtual {p0}, Landroid/support/v7/internal/view/ActionBarPolicy;->hasEmbeddedTabs()Z

    #@1b
    move-result v3

    #@1c
    if-nez v3, :cond_0

    #@1e
    .line 79
    sget v3, Landroid/support/v7/appcompat/R$dimen;->abc_action_bar_stacked_max_height:I

    #@20
    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    #@23
    move-result v3

    #@24
    invoke-static {v1, v3}, Ljava/lang/Math;->min(II)I

    #@27
    move-result v1

    #@28
    .line 82
    :cond_0
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@2b
    .line 83
    return v1
.end method

.method public hasEmbeddedTabs()Z
    .locals 3

    #@0
    .prologue
    .line 62
    iget-object v1, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v1}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@5
    move-result-object v1

    #@6
    iget v0, v1, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@8
    .line 63
    .local v0, "targetSdk":I
    const/16 v1, 0x10

    #@a
    if-lt v0, v1, :cond_0

    #@c
    .line 64
    iget-object v1, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@e
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@11
    move-result-object v1

    #@12
    sget v2, Landroid/support/v7/appcompat/R$bool;->abc_action_bar_embed_tabs:I

    #@14
    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getBoolean(I)Z

    #@17
    move-result v1

    #@18
    .line 69
    :goto_0
    return v1

    #@19
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@1b
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1e
    move-result-object v1

    #@1f
    sget v2, Landroid/support/v7/appcompat/R$bool;->abc_action_bar_embed_tabs_pre_jb:I

    #@21
    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getBoolean(I)Z

    #@24
    move-result v1

    #@25
    goto :goto_0
.end method

.method public showsOverflowMenuButton()Z
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 50
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3
    const/16 v2, 0x13

    #@5
    if-lt v1, v2, :cond_1

    #@7
    .line 53
    :cond_0
    :goto_0
    return v0

    #@8
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/view/ActionBarPolicy;->mContext:Landroid/content/Context;

    #@a
    invoke-static {v1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@d
    move-result-object v1

    #@e
    invoke-static {v1}, Landroid/support/v4/view/ViewConfigurationCompat;->hasPermanentMenuKey(Landroid/view/ViewConfiguration;)Z

    #@11
    move-result v1

    #@12
    if-eqz v1, :cond_0

    #@14
    const/4 v0, 0x0

    #@15
    goto :goto_0
.end method
