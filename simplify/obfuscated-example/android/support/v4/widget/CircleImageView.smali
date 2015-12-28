.class Landroid/support/v4/widget/CircleImageView;
.super Landroid/widget/ImageView;
.source "CircleImageView.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/CircleImageView$OvalShadow;
    }
.end annotation


# static fields
.field private static final FILL_SHADOW_COLOR:I = 0x3d000000

.field private static final KEY_SHADOW_COLOR:I = 0x1e000000

.field private static final SHADOW_ELEVATION:I = 0x4

.field private static final SHADOW_RADIUS:F = 3.5f

.field private static final X_OFFSET:F = 0.0f

.field private static final Y_OFFSET:F = 1.75f


# instance fields
.field private mListener:Landroid/view/animation/Animation$AnimationListener;

.field private mShadowRadius:I


# direct methods
.method public constructor <init>(Landroid/content/Context;IF)V
    .locals 12
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "color"    # I
    .param p3, "radius"    # F

    #@0
    .prologue
    .line 53
    invoke-direct {p0, p1}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;)V

    #@3
    .line 54
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getContext()Landroid/content/Context;

    #@6
    move-result-object v7

    #@7
    invoke-virtual {v7}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@a
    move-result-object v7

    #@b
    invoke-virtual {v7}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@e
    move-result-object v7

    #@f
    iget v1, v7, Landroid/util/DisplayMetrics;->density:F

    #@11
    .line 55
    .local v1, "density":F
    mul-float v7, p3, v1

    #@13
    const/high16 v8, 0x40000000    # 2.0f

    #@15
    mul-float/2addr v7, v8

    #@16
    float-to-int v2, v7

    #@17
    .line 56
    .local v2, "diameter":I
    const/high16 v7, 0x3fe00000    # 1.75f

    #@19
    mul-float/2addr v7, v1

    #@1a
    float-to-int v6, v7

    #@1b
    .line 57
    .local v6, "shadowYOffset":I
    const/4 v7, 0x0

    #@1c
    mul-float/2addr v7, v1

    #@1d
    float-to-int v5, v7

    #@1e
    .line 59
    .local v5, "shadowXOffset":I
    const/high16 v7, 0x40600000    # 3.5f

    #@20
    mul-float/2addr v7, v1

    #@21
    float-to-int v7, v7

    #@22
    iput v7, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@24
    .line 62
    invoke-direct {p0}, Landroid/support/v4/widget/CircleImageView;->elevationSupported()Z

    #@27
    move-result v7

    #@28
    if-eqz v7, :cond_0

    #@2a
    .line 63
    new-instance v0, Landroid/graphics/drawable/ShapeDrawable;

    #@2c
    new-instance v7, Landroid/graphics/drawable/shapes/OvalShape;

    #@2e
    invoke-direct {v7}, Landroid/graphics/drawable/shapes/OvalShape;-><init>()V

    #@31
    invoke-direct {v0, v7}, Landroid/graphics/drawable/ShapeDrawable;-><init>(Landroid/graphics/drawable/shapes/Shape;)V

    #@34
    .line 64
    .local v0, "circle":Landroid/graphics/drawable/ShapeDrawable;
    const/high16 v7, 0x40800000    # 4.0f

    #@36
    mul-float/2addr v7, v1

    #@37
    invoke-static {p0, v7}, Landroid/support/v4/view/ViewCompat;->setElevation(Landroid/view/View;F)V

    #@3a
    .line 75
    :goto_0
    invoke-virtual {v0}, Landroid/graphics/drawable/ShapeDrawable;->getPaint()Landroid/graphics/Paint;

    #@3d
    move-result-object v7

    #@3e
    invoke-virtual {v7, p2}, Landroid/graphics/Paint;->setColor(I)V

    #@41
    .line 76
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/CircleImageView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@44
    .line 77
    return-void

    #@45
    .line 66
    .end local v0    # "circle":Landroid/graphics/drawable/ShapeDrawable;
    :cond_0
    new-instance v3, Landroid/support/v4/widget/CircleImageView$OvalShadow;

    #@47
    iget v7, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@49
    invoke-direct {v3, p0, v7, v2}, Landroid/support/v4/widget/CircleImageView$OvalShadow;-><init>(Landroid/support/v4/widget/CircleImageView;II)V

    #@4c
    .line 67
    .local v3, "oval":Landroid/graphics/drawable/shapes/OvalShape;
    new-instance v0, Landroid/graphics/drawable/ShapeDrawable;

    #@4e
    invoke-direct {v0, v3}, Landroid/graphics/drawable/ShapeDrawable;-><init>(Landroid/graphics/drawable/shapes/Shape;)V

    #@51
    .line 68
    .restart local v0    # "circle":Landroid/graphics/drawable/ShapeDrawable;
    const/4 v7, 0x1

    #@52
    invoke-virtual {v0}, Landroid/graphics/drawable/ShapeDrawable;->getPaint()Landroid/graphics/Paint;

    #@55
    move-result-object v8

    #@56
    invoke-static {p0, v7, v8}, Landroid/support/v4/view/ViewCompat;->setLayerType(Landroid/view/View;ILandroid/graphics/Paint;)V

    #@59
    .line 69
    invoke-virtual {v0}, Landroid/graphics/drawable/ShapeDrawable;->getPaint()Landroid/graphics/Paint;

    #@5c
    move-result-object v7

    #@5d
    iget v8, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@5f
    int-to-float v8, v8

    #@60
    int-to-float v9, v5

    #@61
    int-to-float v10, v6

    #@62
    const/high16 v11, 0x1e000000

    #@64
    invoke-virtual {v7, v8, v9, v10, v11}, Landroid/graphics/Paint;->setShadowLayer(FFFI)V

    #@67
    .line 71
    iget v4, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@69
    .line 73
    .local v4, "padding":I
    invoke-virtual {p0, v4, v4, v4, v4}, Landroid/support/v4/widget/CircleImageView;->setPadding(IIII)V

    #@6c
    goto :goto_0
.end method

.method private elevationSupported()Z
    .locals 2

    #@0
    .prologue
    .line 80
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-lt v0, v1, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method


# virtual methods
.method public onAnimationEnd()V
    .locals 2

    #@0
    .prologue
    .line 106
    invoke-super {p0}, Landroid/widget/ImageView;->onAnimationEnd()V

    #@3
    .line 107
    iget-object v0, p0, Landroid/support/v4/widget/CircleImageView;->mListener:Landroid/view/animation/Animation$AnimationListener;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 108
    iget-object v0, p0, Landroid/support/v4/widget/CircleImageView;->mListener:Landroid/view/animation/Animation$AnimationListener;

    #@9
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getAnimation()Landroid/view/animation/Animation;

    #@c
    move-result-object v1

    #@d
    invoke-interface {v0, v1}, Landroid/view/animation/Animation$AnimationListener;->onAnimationEnd(Landroid/view/animation/Animation;)V

    #@10
    .line 110
    :cond_0
    return-void
.end method

.method public onAnimationStart()V
    .locals 2

    #@0
    .prologue
    .line 98
    invoke-super {p0}, Landroid/widget/ImageView;->onAnimationStart()V

    #@3
    .line 99
    iget-object v0, p0, Landroid/support/v4/widget/CircleImageView;->mListener:Landroid/view/animation/Animation$AnimationListener;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 100
    iget-object v0, p0, Landroid/support/v4/widget/CircleImageView;->mListener:Landroid/view/animation/Animation$AnimationListener;

    #@9
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getAnimation()Landroid/view/animation/Animation;

    #@c
    move-result-object v1

    #@d
    invoke-interface {v0, v1}, Landroid/view/animation/Animation$AnimationListener;->onAnimationStart(Landroid/view/animation/Animation;)V

    #@10
    .line 102
    :cond_0
    return-void
.end method

.method protected onMeasure(II)V
    .locals 3
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 85
    invoke-super {p0, p1, p2}, Landroid/widget/ImageView;->onMeasure(II)V

    #@3
    .line 86
    invoke-direct {p0}, Landroid/support/v4/widget/CircleImageView;->elevationSupported()Z

    #@6
    move-result v0

    #@7
    if-nez v0, :cond_0

    #@9
    .line 87
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getMeasuredWidth()I

    #@c
    move-result v0

    #@d
    iget v1, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@f
    mul-int/lit8 v1, v1, 0x2

    #@11
    add-int/2addr v0, v1

    #@12
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getMeasuredHeight()I

    #@15
    move-result v1

    #@16
    iget v2, p0, Landroid/support/v4/widget/CircleImageView;->mShadowRadius:I

    #@18
    mul-int/lit8 v2, v2, 0x2

    #@1a
    add-int/2addr v1, v2

    #@1b
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/widget/CircleImageView;->setMeasuredDimension(II)V

    #@1e
    .line 90
    :cond_0
    return-void
.end method

.method public setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/view/animation/Animation$AnimationListener;

    #@0
    .prologue
    .line 93
    iput-object p1, p0, Landroid/support/v4/widget/CircleImageView;->mListener:Landroid/view/animation/Animation$AnimationListener;

    #@2
    .line 94
    return-void
.end method

.method public setBackgroundColor(I)V
    .locals 3
    .param p1, "colorRes"    # I

    #@0
    .prologue
    .line 116
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getBackground()Landroid/graphics/drawable/Drawable;

    #@3
    move-result-object v1

    #@4
    instance-of v1, v1, Landroid/graphics/drawable/ShapeDrawable;

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 117
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getResources()Landroid/content/res/Resources;

    #@b
    move-result-object v0

    #@c
    .line 118
    .local v0, "res":Landroid/content/res/Resources;
    invoke-virtual {p0}, Landroid/support/v4/widget/CircleImageView;->getBackground()Landroid/graphics/drawable/Drawable;

    #@f
    move-result-object v1

    #@10
    check-cast v1, Landroid/graphics/drawable/ShapeDrawable;

    #@12
    invoke-virtual {v1}, Landroid/graphics/drawable/ShapeDrawable;->getPaint()Landroid/graphics/Paint;

    #@15
    move-result-object v1

    #@16
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getColor(I)I

    #@19
    move-result v2

    #@1a
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setColor(I)V

    #@1d
    .line 120
    .end local v0    # "res":Landroid/content/res/Resources;
    :cond_0
    return-void
.end method
