.class Landroid/support/v4/view/ViewPropertyAnimatorCompatJellybeanMr2;
.super Ljava/lang/Object;
.source "ViewPropertyAnimatorCompatJellybeanMr2.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getInterpolator(Landroid/view/View;)Landroid/view/animation/Interpolator;
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 23
    invoke-virtual {p0}, Landroid/view/View;->animate()Landroid/view/ViewPropertyAnimator;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/view/ViewPropertyAnimator;->getInterpolator()Landroid/animation/TimeInterpolator;

    #@7
    move-result-object v0

    #@8
    check-cast v0, Landroid/view/animation/Interpolator;

    #@a
    return-object v0
.end method
