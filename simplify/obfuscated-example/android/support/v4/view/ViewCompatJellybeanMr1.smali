.class Landroid/support/v4/view/ViewCompatJellybeanMr1;
.super Ljava/lang/Object;
.source "ViewCompatJellybeanMr1.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getLabelFor(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 28
    invoke-virtual {p0}, Landroid/view/View;->getLabelFor()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getLayoutDirection(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 40
    invoke-virtual {p0}, Landroid/view/View;->getLayoutDirection()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPaddingEnd(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 52
    invoke-virtual {p0}, Landroid/view/View;->getPaddingEnd()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPaddingStart(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 48
    invoke-virtual {p0}, Landroid/view/View;->getPaddingStart()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getWindowSystemUiVisibility(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 60
    invoke-virtual {p0}, Landroid/view/View;->getWindowSystemUiVisibility()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static setLabelFor(Landroid/view/View;I)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "id"    # I

    #@0
    .prologue
    .line 32
    invoke-virtual {p0, p1}, Landroid/view/View;->setLabelFor(I)V

    #@3
    .line 33
    return-void
.end method

.method public static setLayerPaint(Landroid/view/View;Landroid/graphics/Paint;)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "paint"    # Landroid/graphics/Paint;

    #@0
    .prologue
    .line 36
    invoke-virtual {p0, p1}, Landroid/view/View;->setLayerPaint(Landroid/graphics/Paint;)V

    #@3
    .line 37
    return-void
.end method

.method public static setLayoutDirection(Landroid/view/View;I)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "layoutDirection"    # I

    #@0
    .prologue
    .line 44
    invoke-virtual {p0, p1}, Landroid/view/View;->setLayoutDirection(I)V

    #@3
    .line 45
    return-void
.end method

.method public static setPaddingRelative(Landroid/view/View;IIII)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "start"    # I
    .param p2, "top"    # I
    .param p3, "end"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 56
    invoke-virtual {p0, p1, p2, p3, p4}, Landroid/view/View;->setPaddingRelative(IIII)V

    #@3
    .line 57
    return-void
.end method
