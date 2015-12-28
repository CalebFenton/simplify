.class Landroid/support/v7/internal/widget/DrawableWrapper;
.super Landroid/graphics/drawable/Drawable;
.source "DrawableWrapper.java"

# interfaces
.implements Landroid/graphics/drawable/Drawable$Callback;


# instance fields
.field private final mDrawable:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "drawable"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 40
    invoke-direct {p0}, Landroid/graphics/drawable/Drawable;-><init>()V

    #@3
    .line 41
    iput-object p1, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@5
    .line 42
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@7
    invoke-virtual {v0, p0}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@a
    .line 43
    return-void
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;)V
    .locals 1
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 47
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@5
    .line 48
    return-void
.end method

.method public getChangingConfigurations()I
    .locals 1

    #@0
    .prologue
    .line 63
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getChangingConfigurations()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getCurrent()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 107
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getCurrent()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getIntrinsicHeight()I
    .locals 1

    #@0
    .prologue
    .line 132
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getIntrinsicWidth()I
    .locals 1

    #@0
    .prologue
    .line 127
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getMinimumHeight()I
    .locals 1

    #@0
    .prologue
    .line 142
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getMinimumHeight()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getMinimumWidth()I
    .locals 1

    #@0
    .prologue
    .line 137
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getMinimumWidth()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getOpacity()I
    .locals 1

    #@0
    .prologue
    .line 117
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getOpacity()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getPadding(Landroid/graphics/Rect;)Z
    .locals 1
    .param p1, "padding"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 147
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getState()[I
    .locals 1

    #@0
    .prologue
    .line 98
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getState()[I

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTransparentRegion()Landroid/graphics/Region;
    .locals 1

    #@0
    .prologue
    .line 122
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getTransparentRegion()Landroid/graphics/Region;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public invalidateDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 154
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/DrawableWrapper;->invalidateSelf()V

    #@3
    .line 155
    return-void
.end method

.method public isAutoMirrored()Z
    .locals 1

    #@0
    .prologue
    .line 183
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0}, Landroid/support/v4/graphics/drawable/DrawableCompat;->isAutoMirrored(Landroid/graphics/drawable/Drawable;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isStateful()Z
    .locals 1

    #@0
    .prologue
    .line 88
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->isStateful()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public jumpToCurrentState()V
    .locals 1

    #@0
    .prologue
    .line 102
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0}, Landroid/support/v4/graphics/drawable/DrawableCompat;->jumpToCurrentState(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 103
    return-void
.end method

.method protected onLevelChange(I)Z
    .locals 1
    .param p1, "level"    # I

    #@0
    .prologue
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setLevel(I)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public scheduleDrawable(Landroid/graphics/drawable/Drawable;Ljava/lang/Runnable;J)V
    .locals 1
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;
    .param p2, "what"    # Ljava/lang/Runnable;
    .param p3, "when"    # J

    #@0
    .prologue
    .line 161
    invoke-virtual {p0, p2, p3, p4}, Landroid/support/v7/internal/widget/DrawableWrapper;->scheduleSelf(Ljava/lang/Runnable;J)V

    #@3
    .line 162
    return-void
.end method

.method public setAlpha(I)V
    .locals 1
    .param p1, "alpha"    # I

    #@0
    .prologue
    .line 78
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    #@5
    .line 79
    return-void
.end method

.method public setAutoMirrored(Z)V
    .locals 1
    .param p1, "mirrored"    # Z

    #@0
    .prologue
    .line 178
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setAutoMirrored(Landroid/graphics/drawable/Drawable;Z)V

    #@5
    .line 179
    return-void
.end method

.method public setBounds(IIII)V
    .locals 1
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 52
    invoke-super {p0, p1, p2, p3, p4}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@3
    .line 53
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@5
    invoke-virtual {v0, p1, p2, p3, p4}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@8
    .line 54
    return-void
.end method

.method public setChangingConfigurations(I)V
    .locals 1
    .param p1, "configs"    # I

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setChangingConfigurations(I)V

    #@5
    .line 59
    return-void
.end method

.method public setColorFilter(Landroid/graphics/ColorFilter;)V
    .locals 1
    .param p1, "cf"    # Landroid/graphics/ColorFilter;

    #@0
    .prologue
    .line 83
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setColorFilter(Landroid/graphics/ColorFilter;)V

    #@5
    .line 84
    return-void
.end method

.method public setDither(Z)V
    .locals 1
    .param p1, "dither"    # Z

    #@0
    .prologue
    .line 68
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setDither(Z)V

    #@5
    .line 69
    return-void
.end method

.method public setFilterBitmap(Z)V
    .locals 1
    .param p1, "filter"    # Z

    #@0
    .prologue
    .line 73
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setFilterBitmap(Z)V

    #@5
    .line 74
    return-void
.end method

.method public setHotspot(FF)V
    .locals 1
    .param p1, "x"    # F
    .param p2, "y"    # F

    #@0
    .prologue
    .line 203
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1, p2}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspot(Landroid/graphics/drawable/Drawable;FF)V

    #@5
    .line 204
    return-void
.end method

.method public setHotspotBounds(IIII)V
    .locals 1
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 208
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1, p2, p3, p4}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspotBounds(Landroid/graphics/drawable/Drawable;IIII)V

    #@5
    .line 209
    return-void
.end method

.method public setState([I)Z
    .locals 1
    .param p1, "stateSet"    # [I

    #@0
    .prologue
    .line 93
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public setTint(I)V
    .locals 1
    .param p1, "tint"    # I

    #@0
    .prologue
    .line 188
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setTint(Landroid/graphics/drawable/Drawable;I)V

    #@5
    .line 189
    return-void
.end method

.method public setTintList(Landroid/content/res/ColorStateList;)V
    .locals 1
    .param p1, "tint"    # Landroid/content/res/ColorStateList;

    #@0
    .prologue
    .line 193
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setTintList(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;)V

    #@5
    .line 194
    return-void
.end method

.method public setTintMode(Landroid/graphics/PorterDuff$Mode;)V
    .locals 1
    .param p1, "tintMode"    # Landroid/graphics/PorterDuff$Mode;

    #@0
    .prologue
    .line 198
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setTintMode(Landroid/graphics/drawable/Drawable;Landroid/graphics/PorterDuff$Mode;)V

    #@5
    .line 199
    return-void
.end method

.method public setVisible(ZZ)Z
    .locals 1
    .param p1, "visible"    # Z
    .param p2, "restart"    # Z

    #@0
    .prologue
    .line 112
    invoke-super {p0, p1, p2}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/internal/widget/DrawableWrapper;->mDrawable:Landroid/graphics/drawable/Drawable;

    #@8
    invoke-virtual {v0, p1, p2}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_1

    #@e
    :cond_0
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public unscheduleDrawable(Landroid/graphics/drawable/Drawable;Ljava/lang/Runnable;)V
    .locals 0
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;
    .param p2, "what"    # Ljava/lang/Runnable;

    #@0
    .prologue
    .line 168
    invoke-virtual {p0, p2}, Landroid/support/v7/internal/widget/DrawableWrapper;->unscheduleSelf(Ljava/lang/Runnable;)V

    #@3
    .line 169
    return-void
.end method
