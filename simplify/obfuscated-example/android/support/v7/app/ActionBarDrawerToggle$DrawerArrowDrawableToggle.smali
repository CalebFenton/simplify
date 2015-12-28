.class Landroid/support/v7/app/ActionBarDrawerToggle$DrawerArrowDrawableToggle;
.super Landroid/support/v7/app/DrawerArrowDrawable;
.source "ActionBarDrawerToggle.java"

# interfaces
.implements Landroid/support/v7/app/ActionBarDrawerToggle$DrawerToggle;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarDrawerToggle;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "DrawerArrowDrawableToggle"
.end annotation


# instance fields
.field private final mActivity:Landroid/app/Activity;


# direct methods
.method public constructor <init>(Landroid/app/Activity;Landroid/content/Context;)V
    .locals 0
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "themedContext"    # Landroid/content/Context;

    #@0
    .prologue
    .line 469
    invoke-direct {p0, p2}, Landroid/support/v7/app/DrawerArrowDrawable;-><init>(Landroid/content/Context;)V

    #@3
    .line 470
    iput-object p1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$DrawerArrowDrawableToggle;->mActivity:Landroid/app/Activity;

    #@5
    .line 471
    return-void
.end method


# virtual methods
.method public getPosition()F
    .locals 1

    #@0
    .prologue
    .line 489
    invoke-super {p0}, Landroid/support/v7/app/DrawerArrowDrawable;->getProgress()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method isLayoutRtl()Z
    .locals 2

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 484
    iget-object v1, p0, Landroid/support/v7/app/ActionBarDrawerToggle$DrawerArrowDrawableToggle;->mActivity:Landroid/app/Activity;

    #@3
    invoke-virtual {v1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    #@6
    move-result-object v1

    #@7
    invoke-virtual {v1}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@a
    move-result-object v1

    #@b
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@e
    move-result v1

    #@f
    if-ne v1, v0, :cond_0

    #@11
    :goto_0
    return v0

    #@12
    :cond_0
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method public setPosition(F)V
    .locals 1
    .param p1, "position"    # F

    #@0
    .prologue
    .line 474
    const/high16 v0, 0x3f800000    # 1.0f

    #@2
    cmpl-float v0, p1, v0

    #@4
    if-nez v0, :cond_1

    #@6
    .line 475
    const/4 v0, 0x1

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/app/ActionBarDrawerToggle$DrawerArrowDrawableToggle;->setVerticalMirror(Z)V

    #@a
    .line 479
    :cond_0
    :goto_0
    invoke-super {p0, p1}, Landroid/support/v7/app/DrawerArrowDrawable;->setProgress(F)V

    #@d
    .line 480
    return-void

    #@e
    .line 476
    :cond_1
    const/4 v0, 0x0

    #@f
    cmpl-float v0, p1, v0

    #@11
    if-nez v0, :cond_0

    #@13
    .line 477
    const/4 v0, 0x0

    #@14
    invoke-virtual {p0, v0}, Landroid/support/v7/app/ActionBarDrawerToggle$DrawerArrowDrawableToggle;->setVerticalMirror(Z)V

    #@17
    goto :goto_0
.end method
