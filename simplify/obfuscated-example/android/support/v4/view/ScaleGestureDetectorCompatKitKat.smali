.class Landroid/support/v4/view/ScaleGestureDetectorCompatKitKat;
.super Ljava/lang/Object;
.source "ScaleGestureDetectorCompatKitKat.java"


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 30
    return-void
.end method

.method public static isQuickScaleEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "scaleGestureDetector"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 37
    check-cast p0, Landroid/view/ScaleGestureDetector;

    #@2
    .end local p0    # "scaleGestureDetector":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/ScaleGestureDetector;->isQuickScaleEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static setQuickScaleEnabled(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "scaleGestureDetector"    # Ljava/lang/Object;
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 33
    check-cast p0, Landroid/view/ScaleGestureDetector;

    #@2
    .end local p0    # "scaleGestureDetector":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/ScaleGestureDetector;->setQuickScaleEnabled(Z)V

    #@5
    .line 34
    return-void
.end method
