.class Landroid/support/v4/widget/MaterialProgressDrawable$EndCurveInterpolator;
.super Landroid/view/animation/AccelerateDecelerateInterpolator;
.source "MaterialProgressDrawable.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/MaterialProgressDrawable;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "EndCurveInterpolator"
.end annotation


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 699
    invoke-direct {p0}, Landroid/view/animation/AccelerateDecelerateInterpolator;-><init>()V

    #@3
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/widget/MaterialProgressDrawable$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/widget/MaterialProgressDrawable$1;

    #@0
    .prologue
    .line 699
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$EndCurveInterpolator;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getInterpolation(F)F
    .locals 3
    .param p1, "input"    # F

    #@0
    .prologue
    .line 702
    const/4 v0, 0x0

    #@1
    const/high16 v1, 0x3f000000    # 0.5f

    #@3
    sub-float v1, p1, v1

    #@5
    const/high16 v2, 0x40000000    # 2.0f

    #@7
    mul-float/2addr v1, v2

    #@8
    invoke-static {v0, v1}, Ljava/lang/Math;->max(FF)F

    #@b
    move-result v0

    #@c
    invoke-super {p0, v0}, Landroid/view/animation/AccelerateDecelerateInterpolator;->getInterpolation(F)F

    #@f
    move-result v0

    #@10
    return v0
.end method
