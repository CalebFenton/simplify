.class Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;
.super Landroid/graphics/drawable/Drawable;
.source "ActionBarBackgroundDrawable.java"


# instance fields
.field final mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/ActionBarContainer;)V
    .locals 0
    .param p1, "container"    # Landroid/support/v7/internal/widget/ActionBarContainer;

    #@0
    .prologue
    .line 11
    invoke-direct {p0}, Landroid/graphics/drawable/Drawable;-><init>()V

    #@3
    .line 12
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@5
    .line 13
    return-void
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;)V
    .locals 1
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 17
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    iget-boolean v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@4
    if-eqz v0, :cond_1

    #@6
    .line 18
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@8
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@a
    if-eqz v0, :cond_0

    #@c
    .line 19
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@e
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@10
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@13
    .line 29
    :cond_0
    :goto_0
    return-void

    #@14
    .line 22
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@16
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@18
    if-eqz v0, :cond_2

    #@1a
    .line 23
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1c
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@21
    .line 25
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@23
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@25
    if-eqz v0, :cond_0

    #@27
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@29
    iget-boolean v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsStacked:Z

    #@2b
    if-eqz v0, :cond_0

    #@2d
    .line 26
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2f
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@31
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@34
    goto :goto_0
.end method

.method public getOpacity()I
    .locals 1

    #@0
    .prologue
    .line 41
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public setAlpha(I)V
    .locals 0
    .param p1, "alpha"    # I

    #@0
    .prologue
    .line 33
    return-void
.end method

.method public setColorFilter(Landroid/graphics/ColorFilter;)V
    .locals 0
    .param p1, "cf"    # Landroid/graphics/ColorFilter;

    #@0
    .prologue
    .line 37
    return-void
.end method
