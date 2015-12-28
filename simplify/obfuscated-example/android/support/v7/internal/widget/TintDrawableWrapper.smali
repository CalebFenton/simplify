.class Landroid/support/v7/internal/widget/TintDrawableWrapper;
.super Landroid/support/v7/internal/widget/DrawableWrapper;
.source "TintDrawableWrapper.java"


# instance fields
.field private mCurrentColor:I

.field private final mTintMode:Landroid/graphics/PorterDuff$Mode;

.field private final mTintStateList:Landroid/content/res/ColorStateList;


# direct methods
.method public constructor <init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;)V
    .locals 1
    .param p1, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p2, "tintStateList"    # Landroid/content/res/ColorStateList;

    #@0
    .prologue
    .line 36
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->DEFAULT_MODE:Landroid/graphics/PorterDuff$Mode;

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/TintDrawableWrapper;-><init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;Landroid/graphics/PorterDuff$Mode;)V

    #@5
    .line 37
    return-void
.end method

.method public constructor <init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;Landroid/graphics/PorterDuff$Mode;)V
    .locals 0
    .param p1, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p2, "tintStateList"    # Landroid/content/res/ColorStateList;
    .param p3, "tintMode"    # Landroid/graphics/PorterDuff$Mode;

    #@0
    .prologue
    .line 41
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/DrawableWrapper;-><init>(Landroid/graphics/drawable/Drawable;)V

    #@3
    .line 42
    iput-object p2, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintStateList:Landroid/content/res/ColorStateList;

    #@5
    .line 43
    iput-object p3, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintMode:Landroid/graphics/PorterDuff$Mode;

    #@7
    .line 44
    return-void
.end method

.method private updateTint([I)Z
    .locals 3
    .param p1, "state"    # [I

    #@0
    .prologue
    .line 59
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintStateList:Landroid/content/res/ColorStateList;

    #@2
    if-eqz v1, :cond_1

    #@4
    .line 60
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintStateList:Landroid/content/res/ColorStateList;

    #@6
    iget v2, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mCurrentColor:I

    #@8
    invoke-virtual {v1, p1, v2}, Landroid/content/res/ColorStateList;->getColorForState([II)I

    #@b
    move-result v0

    #@c
    .line 61
    .local v0, "color":I
    iget v1, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mCurrentColor:I

    #@e
    if-eq v0, v1, :cond_1

    #@10
    .line 62
    if-eqz v0, :cond_0

    #@12
    .line 63
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintMode:Landroid/graphics/PorterDuff$Mode;

    #@14
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/widget/TintDrawableWrapper;->setColorFilter(ILandroid/graphics/PorterDuff$Mode;)V

    #@17
    .line 67
    :goto_0
    iput v0, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mCurrentColor:I

    #@19
    .line 68
    const/4 v1, 0x1

    #@1a
    .line 71
    .end local v0    # "color":I
    :goto_1
    return v1

    #@1b
    .line 65
    .restart local v0    # "color":I
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/TintDrawableWrapper;->clearColorFilter()V

    #@1e
    goto :goto_0

    #@1f
    .line 71
    .end local v0    # "color":I
    :cond_1
    const/4 v1, 0x0

    #@20
    goto :goto_1
.end method


# virtual methods
.method public isStateful()Z
    .locals 1

    #@0
    .prologue
    .line 48
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintStateList:Landroid/content/res/ColorStateList;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintDrawableWrapper;->mTintStateList:Landroid/content/res/ColorStateList;

    #@6
    invoke-virtual {v0}, Landroid/content/res/ColorStateList;->isStateful()Z

    #@9
    move-result v0

    #@a
    if-nez v0, :cond_1

    #@c
    :cond_0
    invoke-super {p0}, Landroid/support/v7/internal/widget/DrawableWrapper;->isStateful()Z

    #@f
    move-result v0

    #@10
    if-eqz v0, :cond_2

    #@12
    :cond_1
    const/4 v0, 0x1

    #@13
    :goto_0
    return v0

    #@14
    :cond_2
    const/4 v0, 0x0

    #@15
    goto :goto_0
.end method

.method public setState([I)Z
    .locals 2
    .param p1, "stateSet"    # [I

    #@0
    .prologue
    .line 53
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/DrawableWrapper;->setState([I)Z

    #@3
    move-result v0

    #@4
    .line 54
    .local v0, "handled":Z
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/TintDrawableWrapper;->updateTint([I)Z

    #@7
    move-result v1

    #@8
    if-nez v1, :cond_0

    #@a
    if-eqz v0, :cond_1

    #@c
    :cond_0
    const/4 v0, 0x1

    #@d
    .line 55
    :goto_0
    return v0

    #@e
    .line 54
    :cond_1
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method
