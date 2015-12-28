.class Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;
.super Ljava/lang/Object;
.source "ActionBarDrawerToggle.java"

# interfaces
.implements Landroid/support/v7/app/ActionBarDrawerToggle$Delegate;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarDrawerToggle;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "JellybeanMr2Delegate"
.end annotation


# instance fields
.field final mActivity:Landroid/app/Activity;


# direct methods
.method private constructor <init>(Landroid/app/Activity;)V
    .locals 0
    .param p1, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 554
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 555
    iput-object p1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->mActivity:Landroid/app/Activity;

    #@5
    .line 556
    return-void
.end method

.method synthetic constructor <init>(Landroid/app/Activity;Landroid/support/v7/app/ActionBarDrawerToggle$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/app/Activity;
    .param p2, "x1"    # Landroid/support/v7/app/ActionBarDrawerToggle$1;

    #@0
    .prologue
    .line 550
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;-><init>(Landroid/app/Activity;)V

    #@3
    return-void
.end method


# virtual methods
.method public getActionBarThemedContext()Landroid/content/Context;
    .locals 3

    #@0
    .prologue
    .line 569
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->mActivity:Landroid/app/Activity;

    #@2
    invoke-virtual {v2}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@5
    move-result-object v0

    #@6
    .line 571
    .local v0, "actionBar":Landroid/app/ActionBar;
    if-eqz v0, :cond_0

    #@8
    .line 572
    invoke-virtual {v0}, Landroid/app/ActionBar;->getThemedContext()Landroid/content/Context;

    #@b
    move-result-object v1

    #@c
    .line 576
    .local v1, "context":Landroid/content/Context;
    :goto_0
    return-object v1

    #@d
    .line 574
    .end local v1    # "context":Landroid/content/Context;
    :cond_0
    iget-object v1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->mActivity:Landroid/app/Activity;

    #@f
    .restart local v1    # "context":Landroid/content/Context;
    goto :goto_0
.end method

.method public getThemeUpIndicator()Landroid/graphics/drawable/Drawable;
    .locals 7

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 560
    invoke-virtual {p0}, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->getActionBarThemedContext()Landroid/content/Context;

    #@4
    move-result-object v2

    #@5
    const/4 v3, 0x0

    #@6
    const/4 v4, 0x1

    #@7
    new-array v4, v4, [I

    #@9
    const v5, 0x101030b

    #@c
    aput v5, v4, v6

    #@e
    const v5, 0x10102ce

    #@11
    invoke-virtual {v2, v3, v4, v5, v6}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@14
    move-result-object v0

    #@15
    .line 562
    .local v0, "a":Landroid/content/res/TypedArray;
    invoke-virtual {v0, v6}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@18
    move-result-object v1

    #@19
    .line 563
    .local v1, "result":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@1c
    .line 564
    return-object v1
.end method

.method public setActionBarDescription(I)V
    .locals 2
    .param p1, "contentDescRes"    # I

    #@0
    .prologue
    .line 590
    iget-object v1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->mActivity:Landroid/app/Activity;

    #@2
    invoke-virtual {v1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@5
    move-result-object v0

    #@6
    .line 591
    .local v0, "actionBar":Landroid/app/ActionBar;
    if-eqz v0, :cond_0

    #@8
    .line 592
    invoke-virtual {v0, p1}, Landroid/app/ActionBar;->setHomeActionContentDescription(I)V

    #@b
    .line 594
    :cond_0
    return-void
.end method

.method public setActionBarUpIndicator(Landroid/graphics/drawable/Drawable;I)V
    .locals 2
    .param p1, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p2, "contentDescRes"    # I

    #@0
    .prologue
    .line 581
    iget-object v1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$JellybeanMr2Delegate;->mActivity:Landroid/app/Activity;

    #@2
    invoke-virtual {v1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@5
    move-result-object v0

    #@6
    .line 582
    .local v0, "actionBar":Landroid/app/ActionBar;
    if-eqz v0, :cond_0

    #@8
    .line 583
    invoke-virtual {v0, p1}, Landroid/app/ActionBar;->setHomeAsUpIndicator(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 584
    invoke-virtual {v0, p2}, Landroid/app/ActionBar;->setHomeActionContentDescription(I)V

    #@e
    .line 586
    :cond_0
    return-void
.end method
