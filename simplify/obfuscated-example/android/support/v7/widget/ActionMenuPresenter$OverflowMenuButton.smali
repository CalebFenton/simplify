.class Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;
.super Landroid/support/v7/internal/widget/TintImageView;
.source "ActionMenuPresenter.java"

# interfaces
.implements Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ActionMenuPresenter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "OverflowMenuButton"
.end annotation


# instance fields
.field private final mTempPts:[F

.field final synthetic this$0:Landroid/support/v7/widget/ActionMenuPresenter;


# direct methods
.method public constructor <init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;)V
    .locals 3
    .param p2, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 587
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@3
    .line 588
    const/4 v0, 0x0

    #@4
    sget v1, Landroid/support/v7/appcompat/R$attr;->actionOverflowButtonStyle:I

    #@6
    invoke-direct {p0, p2, v0, v1}, Landroid/support/v7/internal/widget/TintImageView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@9
    .line 585
    const/4 v0, 0x2

    #@a
    new-array v0, v0, [F

    #@c
    iput-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->mTempPts:[F

    #@e
    .line 590
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->setClickable(Z)V

    #@11
    .line 591
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->setFocusable(Z)V

    #@14
    .line 592
    const/4 v0, 0x0

    #@15
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->setVisibility(I)V

    #@18
    .line 593
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->setEnabled(Z)V

    #@1b
    .line 595
    new-instance v0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton$1;

    #@1d
    invoke-direct {v0, p0, p0, p1}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton$1;-><init>(Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;Landroid/view/View;Landroid/support/v7/widget/ActionMenuPresenter;)V

    #@20
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->setOnTouchListener(Landroid/view/View$OnTouchListener;)V

    #@23
    .line 624
    return-void
.end method


# virtual methods
.method public needsDividerAfter()Z
    .locals 1

    #@0
    .prologue
    .line 644
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public needsDividerBefore()Z
    .locals 1

    #@0
    .prologue
    .line 639
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public performClick()Z
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 628
    invoke-super {p0}, Landroid/support/v7/internal/widget/TintImageView;->performClick()Z

    #@4
    move-result v0

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 634
    :goto_0
    return v1

    #@8
    .line 632
    :cond_0
    const/4 v0, 0x0

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->playSoundEffect(I)V

    #@c
    .line 633
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@e
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->showOverflowMenu()Z

    #@11
    goto :goto_0
.end method

.method protected setFrame(IIII)Z
    .locals 8
    .param p1, "l"    # I
    .param p2, "t"    # I
    .param p3, "r"    # I
    .param p4, "b"    # I

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 649
    invoke-super {p0, p1, p2, p3, p4}, Landroid/support/v7/internal/widget/TintImageView;->setFrame(IIII)Z

    #@4
    move-result v1

    #@5
    .line 652
    .local v1, "changed":Z
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getDrawable()Landroid/graphics/drawable/Drawable;

    #@8
    move-result-object v2

    #@9
    .line 653
    .local v2, "d":Landroid/graphics/drawable/Drawable;
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getBackground()Landroid/graphics/drawable/Drawable;

    #@c
    move-result-object v0

    #@d
    .line 654
    .local v0, "bg":Landroid/graphics/drawable/Drawable;
    if-eqz v2, :cond_0

    #@f
    if-eqz v0, :cond_0

    #@11
    .line 655
    iget-object v4, p0, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->mTempPts:[F

    #@13
    .line 656
    .local v4, "pts":[F
    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    #@16
    move-result-object v5

    #@17
    invoke-virtual {v5}, Landroid/graphics/Rect;->centerX()I

    #@1a
    move-result v5

    #@1b
    int-to-float v5, v5

    #@1c
    aput v5, v4, v7

    #@1e
    .line 657
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getImageMatrix()Landroid/graphics/Matrix;

    #@21
    move-result-object v5

    #@22
    invoke-virtual {v5, v4}, Landroid/graphics/Matrix;->mapPoints([F)V

    #@25
    .line 658
    aget v5, v4, v7

    #@27
    float-to-int v5, v5

    #@28
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getWidth()I

    #@2b
    move-result v6

    #@2c
    div-int/lit8 v6, v6, 0x2

    #@2e
    sub-int v3, v5, v6

    #@30
    .line 659
    .local v3, "offset":I
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getWidth()I

    #@33
    move-result v5

    #@34
    add-int/2addr v5, v3

    #@35
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuPresenter$OverflowMenuButton;->getHeight()I

    #@38
    move-result v6

    #@39
    invoke-static {v0, v3, v7, v5, v6}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspotBounds(Landroid/graphics/drawable/Drawable;IIII)V

    #@3c
    .line 662
    .end local v3    # "offset":I
    .end local v4    # "pts":[F
    :cond_0
    return v1
.end method
