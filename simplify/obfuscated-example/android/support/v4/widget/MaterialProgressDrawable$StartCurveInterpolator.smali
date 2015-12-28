.class Landroid/support/v4/widget/MaterialProgressDrawable$StartCurveInterpolator;
.super Landroid/view/animation/AccelerateDecelerateInterpolator;
.source "MaterialProgressDrawable.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/MaterialProgressDrawable;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "StartCurveInterpolator"
.end annotation


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 709
    invoke-direct {p0}, Landroid/view/animation/AccelerateDecelerateInterpolator;-><init>()V

    #@3
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/widget/MaterialProgressDrawable$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/widget/MaterialProgressDrawable$1;

    #@0
    .prologue
    .line 709
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$StartCurveInterpolator;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getInterpolation(F)F
    .locals 2
    .param p1, "input"    # F

    #@0
    .prologue
    .line 712
    const/high16 v0, 0x3f800000    # 1.0f

    #@2
    const/high16 v1, 0x40000000    # 2.0f

    #@4
    mul-float/2addr v1, p1

    #@5
    invoke-static {v0, v1}, Ljava/lang/Math;->min(FF)F

    #@8
    move-result v0

    #@9
    invoke-super {p0, v0}, Landroid/view/animation/AccelerateDecelerateInterpolator;->getInterpolation(F)F

    #@c
    move-result v0

    #@d
    return v0
.end method
