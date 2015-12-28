.class Landroid/support/v4/view/ViewCompatHC;
.super Ljava/lang/Object;
.source "ViewCompatHC.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getAlpha(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 29
    invoke-virtual {p0}, Landroid/view/View;->getAlpha()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static getFrameTime()J
    .locals 2

    #@0
    .prologue
    .line 25
    invoke-static {}, Landroid/animation/ValueAnimator;->getFrameDelay()J

    #@3
    move-result-wide v0

    #@4
    return-wide v0
.end method

.method public static getLayerType(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 37
    invoke-virtual {p0}, Landroid/view/View;->getLayerType()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getMeasuredHeightAndState(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 49
    invoke-virtual {p0}, Landroid/view/View;->getMeasuredHeightAndState()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getMeasuredState(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 53
    invoke-virtual {p0}, Landroid/view/View;->getMeasuredState()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getMeasuredWidthAndState(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 45
    invoke-virtual {p0}, Landroid/view/View;->getMeasuredWidthAndState()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPivotX(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 141
    invoke-virtual {p0}, Landroid/view/View;->getPivotX()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPivotY(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 145
    invoke-virtual {p0}, Landroid/view/View;->getPivotY()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getRotation(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 73
    invoke-virtual {p0}, Landroid/view/View;->getRotation()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getRotationX(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 77
    invoke-virtual {p0}, Landroid/view/View;->getRotationX()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getRotationY(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 81
    invoke-virtual {p0}, Landroid/view/View;->getRotationY()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getScaleX(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 85
    invoke-virtual {p0}, Landroid/view/View;->getScaleX()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getScaleY(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 89
    invoke-virtual {p0}, Landroid/view/View;->getScaleY()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getTranslationX(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 57
    invoke-virtual {p0}, Landroid/view/View;->getTranslationX()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getTranslationY(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 61
    invoke-virtual {p0}, Landroid/view/View;->getTranslationY()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getX(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 65
    invoke-virtual {p0}, Landroid/view/View;->getX()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getY(Landroid/view/View;)F
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 69
    invoke-virtual {p0}, Landroid/view/View;->getY()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static jumpDrawablesToCurrentState(Landroid/view/View;)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 149
    invoke-virtual {p0}, Landroid/view/View;->jumpDrawablesToCurrentState()V

    #@3
    .line 150
    return-void
.end method

.method public static resolveSizeAndState(III)I
    .locals 1
    .param p0, "size"    # I
    .param p1, "measureSpec"    # I
    .param p2, "childMeasuredState"    # I

    #@0
    .prologue
    .line 41
    invoke-static {p0, p1, p2}, Landroid/view/View;->resolveSizeAndState(III)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static setAlpha(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 101
    invoke-virtual {p0, p1}, Landroid/view/View;->setAlpha(F)V

    #@3
    .line 102
    return-void
.end method

.method public static setLayerType(Landroid/view/View;ILandroid/graphics/Paint;)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "layerType"    # I
    .param p2, "paint"    # Landroid/graphics/Paint;

    #@0
    .prologue
    .line 33
    invoke-virtual {p0, p1, p2}, Landroid/view/View;->setLayerType(ILandroid/graphics/Paint;)V

    #@3
    .line 34
    return-void
.end method

.method public static setPivotX(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 133
    invoke-virtual {p0, p1}, Landroid/view/View;->setPivotX(F)V

    #@3
    .line 134
    return-void
.end method

.method public static setPivotY(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 137
    invoke-virtual {p0, p1}, Landroid/view/View;->setPivotY(F)V

    #@3
    .line 138
    return-void
.end method

.method public static setRotation(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 113
    invoke-virtual {p0, p1}, Landroid/view/View;->setRotation(F)V

    #@3
    .line 114
    return-void
.end method

.method public static setRotationX(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 117
    invoke-virtual {p0, p1}, Landroid/view/View;->setRotationX(F)V

    #@3
    .line 118
    return-void
.end method

.method public static setRotationY(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 121
    invoke-virtual {p0, p1}, Landroid/view/View;->setRotationY(F)V

    #@3
    .line 122
    return-void
.end method

.method public static setScaleX(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 125
    invoke-virtual {p0, p1}, Landroid/view/View;->setScaleX(F)V

    #@3
    .line 126
    return-void
.end method

.method public static setScaleY(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 129
    invoke-virtual {p0, p1}, Landroid/view/View;->setScaleY(F)V

    #@3
    .line 130
    return-void
.end method

.method public static setTranslationX(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 93
    invoke-virtual {p0, p1}, Landroid/view/View;->setTranslationX(F)V

    #@3
    .line 94
    return-void
.end method

.method public static setTranslationY(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 97
    invoke-virtual {p0, p1}, Landroid/view/View;->setTranslationY(F)V

    #@3
    .line 98
    return-void
.end method

.method public static setX(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 105
    invoke-virtual {p0, p1}, Landroid/view/View;->setX(F)V

    #@3
    .line 106
    return-void
.end method

.method public static setY(Landroid/view/View;F)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "value"    # F

    #@0
    .prologue
    .line 109
    invoke-virtual {p0, p1}, Landroid/view/View;->setY(F)V

    #@3
    .line 110
    return-void
.end method
