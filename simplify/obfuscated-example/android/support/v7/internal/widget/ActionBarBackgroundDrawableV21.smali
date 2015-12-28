.class Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;
.super Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;
.source "ActionBarBackgroundDrawableV21.java"


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/ActionBarContainer;)V
    .locals 0
    .param p1, "container"    # Landroid/support/v7/internal/widget/ActionBarContainer;

    #@0
    .prologue
    .line 10
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;-><init>(Landroid/support/v7/internal/widget/ActionBarContainer;)V

    #@3
    .line 11
    return-void
.end method


# virtual methods
.method public getOutline(Landroid/graphics/Outline;)V
    .locals 1
    .param p1, "outline"    # Landroid/graphics/Outline;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param

    #@0
    .prologue
    .line 15
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2
    iget-boolean v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@4
    if-eqz v0, :cond_1

    #@6
    .line 16
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@8
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@a
    if-eqz v0, :cond_0

    #@c
    .line 17
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@e
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@10
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->getOutline(Landroid/graphics/Outline;)V

    #@13
    .line 25
    :cond_0
    :goto_0
    return-void

    #@14
    .line 21
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@16
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@18
    if-eqz v0, :cond_0

    #@1a
    .line 22
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;->mContainer:Landroid/support/v7/internal/widget/ActionBarContainer;

    #@1c
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->getOutline(Landroid/graphics/Outline;)V

    #@21
    goto :goto_0
.end method
