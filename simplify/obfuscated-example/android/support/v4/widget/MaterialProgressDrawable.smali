.class Landroid/support/v4/widget/MaterialProgressDrawable;
.super Landroid/graphics/drawable/Drawable;
.source "MaterialProgressDrawable.java"

# interfaces
.implements Landroid/graphics/drawable/Animatable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/MaterialProgressDrawable$StartCurveInterpolator;,
        Landroid/support/v4/widget/MaterialProgressDrawable$EndCurveInterpolator;,
        Landroid/support/v4/widget/MaterialProgressDrawable$Ring;,
        Landroid/support/v4/widget/MaterialProgressDrawable$ProgressDrawableSize;
    }
.end annotation


# static fields
.field private static final ANIMATION_DURATION:I = 0x535

.field private static final ARROW_HEIGHT:I = 0x5

.field private static final ARROW_HEIGHT_LARGE:I = 0x6

.field private static final ARROW_OFFSET_ANGLE:F = 5.0f

.field private static final ARROW_WIDTH:I = 0xa

.field private static final ARROW_WIDTH_LARGE:I = 0xc

.field private static final CENTER_RADIUS:F = 8.75f

.field private static final CENTER_RADIUS_LARGE:F = 12.5f

.field private static final CIRCLE_DIAMETER:I = 0x28

.field private static final CIRCLE_DIAMETER_LARGE:I = 0x38

.field static final DEFAULT:I = 0x1

.field private static final EASE_INTERPOLATOR:Landroid/view/animation/Interpolator;

.field private static final END_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

.field static final LARGE:I = 0x0

.field private static final LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

.field private static final MAX_PROGRESS_ARC:F = 0.8f

.field private static final NUM_POINTS:F = 5.0f

.field private static final START_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

.field private static final STROKE_WIDTH:F = 2.5f

.field private static final STROKE_WIDTH_LARGE:F = 3.0f


# instance fields
.field private final COLORS:[I

.field private mAnimation:Landroid/view/animation/Animation;

.field private final mAnimators:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/animation/Animation;",
            ">;"
        }
    .end annotation
.end field

.field private final mCallback:Landroid/graphics/drawable/Drawable$Callback;

.field mFinishing:Z

.field private mHeight:D

.field private mParent:Landroid/view/View;

.field private mResources:Landroid/content/res/Resources;

.field private final mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

.field private mRotation:F

.field private mRotationCount:F

.field private mWidth:D


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 53
    new-instance v0, Landroid/view/animation/LinearInterpolator;

    #@3
    invoke-direct {v0}, Landroid/view/animation/LinearInterpolator;-><init>()V

    #@6
    sput-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@8
    .line 54
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable$EndCurveInterpolator;

    #@a
    invoke-direct {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$EndCurveInterpolator;-><init>(Landroid/support/v4/widget/MaterialProgressDrawable$1;)V

    #@d
    sput-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->END_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@f
    .line 55
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable$StartCurveInterpolator;

    #@11
    invoke-direct {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$StartCurveInterpolator;-><init>(Landroid/support/v4/widget/MaterialProgressDrawable$1;)V

    #@14
    sput-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->START_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@16
    .line 56
    new-instance v0, Landroid/view/animation/AccelerateDecelerateInterpolator;

    #@18
    invoke-direct {v0}, Landroid/view/animation/AccelerateDecelerateInterpolator;-><init>()V

    #@1b
    sput-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->EASE_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@1d
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/view/View;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "parent"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 112
    invoke-direct {p0}, Landroid/graphics/drawable/Drawable;-><init>()V

    #@4
    .line 76
    new-array v0, v3, [I

    #@6
    const/4 v1, 0x0

    #@7
    const/high16 v2, -0x1000000

    #@9
    aput v2, v0, v1

    #@b
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->COLORS:[I

    #@d
    .line 86
    new-instance v0, Ljava/util/ArrayList;

    #@f
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@12
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimators:Ljava/util/ArrayList;

    #@14
    .line 381
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable$3;

    #@16
    invoke-direct {v0, p0}, Landroid/support/v4/widget/MaterialProgressDrawable$3;-><init>(Landroid/support/v4/widget/MaterialProgressDrawable;)V

    #@19
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mCallback:Landroid/graphics/drawable/Drawable$Callback;

    #@1b
    .line 113
    iput-object p2, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mParent:Landroid/view/View;

    #@1d
    .line 114
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@20
    move-result-object v0

    #@21
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mResources:Landroid/content/res/Resources;

    #@23
    .line 116
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@25
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mCallback:Landroid/graphics/drawable/Drawable$Callback;

    #@27
    invoke-direct {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;-><init>(Landroid/graphics/drawable/Drawable$Callback;)V

    #@2a
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2c
    .line 117
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2e
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->COLORS:[I

    #@30
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColors([I)V

    #@33
    .line 119
    invoke-virtual {p0, v3}, Landroid/support/v4/widget/MaterialProgressDrawable;->updateSizes(I)V

    #@36
    .line 120
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable;->setupAnimators()V

    #@39
    .line 121
    return-void
.end method

.method static synthetic access$200(Landroid/support/v4/widget/MaterialProgressDrawable;FLandroid/support/v4/widget/MaterialProgressDrawable$Ring;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/MaterialProgressDrawable;
    .param p1, "x1"    # F
    .param p2, "x2"    # Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@0
    .prologue
    .line 52
    invoke-direct {p0, p1, p2}, Landroid/support/v4/widget/MaterialProgressDrawable;->applyFinishTranslation(FLandroid/support/v4/widget/MaterialProgressDrawable$Ring;)V

    #@3
    return-void
.end method

.method static synthetic access$300()Landroid/view/animation/Interpolator;
    .locals 1

    #@0
    .prologue
    .line 52
    sget-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->START_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@2
    return-object v0
.end method

.method static synthetic access$400()Landroid/view/animation/Interpolator;
    .locals 1

    #@0
    .prologue
    .line 52
    sget-object v0, Landroid/support/v4/widget/MaterialProgressDrawable;->END_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v4/widget/MaterialProgressDrawable;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/widget/MaterialProgressDrawable;

    #@0
    .prologue
    .line 52
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRotationCount:F

    #@2
    return v0
.end method

.method static synthetic access$502(Landroid/support/v4/widget/MaterialProgressDrawable;F)F
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/MaterialProgressDrawable;
    .param p1, "x1"    # F

    #@0
    .prologue
    .line 52
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRotationCount:F

    #@2
    return p1
.end method

.method private applyFinishTranslation(FLandroid/support/v4/widget/MaterialProgressDrawable$Ring;)V
    .locals 8
    .param p1, "interpolatedTime"    # F
    .param p2, "ring"    # Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@0
    .prologue
    .line 300
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingRotation()F

    #@3
    move-result v3

    #@4
    const v4, 0x3f4ccccd    # 0.8f

    #@7
    div-float/2addr v3, v4

    #@8
    float-to-double v4, v3

    #@9
    invoke-static {v4, v5}, Ljava/lang/Math;->floor(D)D

    #@c
    move-result-wide v4

    #@d
    const-wide/high16 v6, 0x3ff0000000000000L    # 1.0

    #@f
    add-double/2addr v4, v6

    #@10
    double-to-float v2, v4

    #@11
    .line 302
    .local v2, "targetRotation":F
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingStartTrim()F

    #@14
    move-result v3

    #@15
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingEndTrim()F

    #@18
    move-result v4

    #@19
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingStartTrim()F

    #@1c
    move-result v5

    #@1d
    sub-float/2addr v4, v5

    #@1e
    mul-float/2addr v4, p1

    #@1f
    add-float v1, v3, v4

    #@21
    .line 304
    .local v1, "startTrim":F
    invoke-virtual {p2, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setStartTrim(F)V

    #@24
    .line 305
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingRotation()F

    #@27
    move-result v3

    #@28
    invoke-virtual {p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingRotation()F

    #@2b
    move-result v4

    #@2c
    sub-float v4, v2, v4

    #@2e
    mul-float/2addr v4, p1

    #@2f
    add-float v0, v3, v4

    #@31
    .line 307
    .local v0, "rotation":F
    invoke-virtual {p2, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setRotation(F)V

    #@34
    .line 308
    return-void
.end method

.method private getRotation()F
    .locals 1

    #@0
    .prologue
    .line 249
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRotation:F

    #@2
    return v0
.end method

.method private setSizeParameters(DDDDFF)V
    .locals 7
    .param p1, "progressCircleWidth"    # D
    .param p3, "progressCircleHeight"    # D
    .param p5, "centerRadius"    # D
    .param p7, "strokeWidth"    # D
    .param p9, "arrowWidth"    # F
    .param p10, "arrowHeight"    # F

    #@0
    .prologue
    .line 125
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    .line 126
    .local v1, "ring":Landroid/support/v4/widget/MaterialProgressDrawable$Ring;
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mResources:Landroid/content/res/Resources;

    #@4
    invoke-virtual {v3}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@7
    move-result-object v0

    #@8
    .line 127
    .local v0, "metrics":Landroid/util/DisplayMetrics;
    iget v2, v0, Landroid/util/DisplayMetrics;->density:F

    #@a
    .line 129
    .local v2, "screenDensity":F
    float-to-double v4, v2

    #@b
    mul-double/2addr v4, p1

    #@c
    iput-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mWidth:D

    #@e
    .line 130
    float-to-double v4, v2

    #@f
    mul-double/2addr v4, p3

    #@10
    iput-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mHeight:D

    #@12
    .line 131
    double-to-float v3, p7

    #@13
    mul-float/2addr v3, v2

    #@14
    invoke-virtual {v1, v3}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setStrokeWidth(F)V

    #@17
    .line 132
    float-to-double v4, v2

    #@18
    mul-double/2addr v4, p5

    #@19
    invoke-virtual {v1, v4, v5}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setCenterRadius(D)V

    #@1c
    .line 133
    const/4 v3, 0x0

    #@1d
    invoke-virtual {v1, v3}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorIndex(I)V

    #@20
    .line 134
    mul-float v3, p9, v2

    #@22
    mul-float v4, p10, v2

    #@24
    invoke-virtual {v1, v3, v4}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setArrowDimensions(FF)V

    #@27
    .line 135
    iget-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mWidth:D

    #@29
    double-to-int v3, v4

    #@2a
    iget-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mHeight:D

    #@2c
    double-to-int v4, v4

    #@2d
    invoke-virtual {v1, v3, v4}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setInsets(II)V

    #@30
    .line 136
    return-void
.end method

.method private setupAnimators()V
    .locals 3

    #@0
    .prologue
    .line 311
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    .line 312
    .local v1, "ring":Landroid/support/v4/widget/MaterialProgressDrawable$Ring;
    new-instance v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;

    #@4
    invoke-direct {v0, p0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$1;-><init>(Landroid/support/v4/widget/MaterialProgressDrawable;Landroid/support/v4/widget/MaterialProgressDrawable$Ring;)V

    #@7
    .line 347
    .local v0, "animation":Landroid/view/animation/Animation;
    const/4 v2, -0x1

    #@8
    invoke-virtual {v0, v2}, Landroid/view/animation/Animation;->setRepeatCount(I)V

    #@b
    .line 348
    const/4 v2, 0x1

    #@c
    invoke-virtual {v0, v2}, Landroid/view/animation/Animation;->setRepeatMode(I)V

    #@f
    .line 349
    sget-object v2, Landroid/support/v4/widget/MaterialProgressDrawable;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    #@11
    invoke-virtual {v0, v2}, Landroid/view/animation/Animation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@14
    .line 350
    new-instance v2, Landroid/support/v4/widget/MaterialProgressDrawable$2;

    #@16
    invoke-direct {v2, p0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$2;-><init>(Landroid/support/v4/widget/MaterialProgressDrawable;Landroid/support/v4/widget/MaterialProgressDrawable$Ring;)V

    #@19
    invoke-virtual {v0, v2}, Landroid/view/animation/Animation;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@1c
    .line 378
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@1e
    .line 379
    return-void
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;)V
    .locals 5
    .param p1, "c"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 220
    invoke-virtual {p0}, Landroid/support/v4/widget/MaterialProgressDrawable;->getBounds()Landroid/graphics/Rect;

    #@3
    move-result-object v0

    #@4
    .line 221
    .local v0, "bounds":Landroid/graphics/Rect;
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    #@7
    move-result v1

    #@8
    .line 222
    .local v1, "saveCount":I
    iget v2, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRotation:F

    #@a
    invoke-virtual {v0}, Landroid/graphics/Rect;->exactCenterX()F

    #@d
    move-result v3

    #@e
    invoke-virtual {v0}, Landroid/graphics/Rect;->exactCenterY()F

    #@11
    move-result v4

    #@12
    invoke-virtual {p1, v2, v3, v4}, Landroid/graphics/Canvas;->rotate(FFF)V

    #@15
    .line 223
    iget-object v2, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@17
    invoke-virtual {v2, p1, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->draw(Landroid/graphics/Canvas;Landroid/graphics/Rect;)V

    #@1a
    .line 224
    invoke-virtual {p1, v1}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@1d
    .line 225
    return-void
.end method

.method public getAlpha()I
    .locals 1

    #@0
    .prologue
    .line 233
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getAlpha()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getIntrinsicHeight()I
    .locals 2

    #@0
    .prologue
    .line 210
    iget-wide v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mHeight:D

    #@2
    double-to-int v0, v0

    #@3
    return v0
.end method

.method public getIntrinsicWidth()I
    .locals 2

    #@0
    .prologue
    .line 215
    iget-wide v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mWidth:D

    #@2
    double-to-int v0, v0

    #@3
    return v0
.end method

.method public getOpacity()I
    .locals 1

    #@0
    .prologue
    .line 254
    const/4 v0, -0x3

    #@1
    return v0
.end method

.method public isRunning()Z
    .locals 5

    #@0
    .prologue
    .line 259
    iget-object v2, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimators:Ljava/util/ArrayList;

    #@2
    .line 260
    .local v2, "animators":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/animation/Animation;>;"
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    .line 261
    .local v0, "N":I
    const/4 v3, 0x0

    #@7
    .local v3, "i":I
    :goto_0
    if-ge v3, v0, :cond_1

    #@9
    .line 262
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    check-cast v1, Landroid/view/animation/Animation;

    #@f
    .line 263
    .local v1, "animator":Landroid/view/animation/Animation;
    invoke-virtual {v1}, Landroid/view/animation/Animation;->hasStarted()Z

    #@12
    move-result v4

    #@13
    if-eqz v4, :cond_0

    #@15
    invoke-virtual {v1}, Landroid/view/animation/Animation;->hasEnded()Z

    #@18
    move-result v4

    #@19
    if-nez v4, :cond_0

    #@1b
    .line 264
    const/4 v4, 0x1

    #@1c
    .line 267
    .end local v1    # "animator":Landroid/view/animation/Animation;
    :goto_1
    return v4

    #@1d
    .line 261
    .restart local v1    # "animator":Landroid/view/animation/Animation;
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@1f
    goto :goto_0

    #@20
    .line 267
    .end local v1    # "animator":Landroid/view/animation/Animation;
    :cond_1
    const/4 v4, 0x0

    #@21
    goto :goto_1
.end method

.method public setAlpha(I)V
    .locals 1
    .param p1, "alpha"    # I

    #@0
    .prologue
    .line 229
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setAlpha(I)V

    #@5
    .line 230
    return-void
.end method

.method public setArrowScale(F)V
    .locals 1
    .param p1, "scale"    # F

    #@0
    .prologue
    .line 166
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setArrowScale(F)V

    #@5
    .line 167
    return-void
.end method

.method public setBackgroundColor(I)V
    .locals 1
    .param p1, "color"    # I

    #@0
    .prologue
    .line 193
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setBackgroundColor(I)V

    #@5
    .line 194
    return-void
.end method

.method public setColorFilter(Landroid/graphics/ColorFilter;)V
    .locals 1
    .param p1, "colorFilter"    # Landroid/graphics/ColorFilter;

    #@0
    .prologue
    .line 238
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorFilter(Landroid/graphics/ColorFilter;)V

    #@5
    .line 239
    return-void
.end method

.method public varargs setColorSchemeColors([I)V
    .locals 2
    .param p1, "colors"    # [I

    #@0
    .prologue
    .line 204
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColors([I)V

    #@5
    .line 205
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@7
    const/4 v1, 0x0

    #@8
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorIndex(I)V

    #@b
    .line 206
    return-void
.end method

.method public setProgressRotation(F)V
    .locals 1
    .param p1, "rotation"    # F

    #@0
    .prologue
    .line 186
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setRotation(F)V

    #@5
    .line 187
    return-void
.end method

.method setRotation(F)V
    .locals 0
    .param p1, "rotation"    # F

    #@0
    .prologue
    .line 243
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRotation:F

    #@2
    .line 244
    invoke-virtual {p0}, Landroid/support/v4/widget/MaterialProgressDrawable;->invalidateSelf()V

    #@5
    .line 245
    return-void
.end method

.method public setStartEndTrim(FF)V
    .locals 1
    .param p1, "startAngle"    # F
    .param p2, "endAngle"    # F

    #@0
    .prologue
    .line 176
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setStartTrim(F)V

    #@5
    .line 177
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@7
    invoke-virtual {v0, p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setEndTrim(F)V

    #@a
    .line 178
    return-void
.end method

.method public showArrow(Z)V
    .locals 1
    .param p1, "show"    # Z

    #@0
    .prologue
    .line 159
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setShowArrow(Z)V

    #@5
    .line 160
    return-void
.end method

.method public start()V
    .locals 4

    #@0
    .prologue
    .line 272
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@2
    invoke-virtual {v0}, Landroid/view/animation/Animation;->reset()V

    #@5
    .line 273
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->storeOriginals()V

    #@a
    .line 275
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@c
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getEndTrim()F

    #@f
    move-result v0

    #@10
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@12
    invoke-virtual {v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartTrim()F

    #@15
    move-result v1

    #@16
    cmpl-float v0, v0, v1

    #@18
    if-eqz v0, :cond_0

    #@1a
    .line 276
    const/4 v0, 0x1

    #@1b
    iput-boolean v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mFinishing:Z

    #@1d
    .line 277
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@1f
    const-wide/16 v2, 0x29a

    #@21
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@24
    .line 278
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mParent:Landroid/view/View;

    #@26
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@28
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@2b
    .line 285
    :goto_0
    return-void

    #@2c
    .line 280
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@2e
    const/4 v1, 0x0

    #@2f
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorIndex(I)V

    #@32
    .line 281
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@34
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->resetOriginals()V

    #@37
    .line 282
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@39
    const-wide/16 v2, 0x535

    #@3b
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/Animation;->setDuration(J)V

    #@3e
    .line 283
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mParent:Landroid/view/View;

    #@40
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mAnimation:Landroid/view/animation/Animation;

    #@42
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@45
    goto :goto_0
.end method

.method public stop()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 289
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mParent:Landroid/view/View;

    #@3
    invoke-virtual {v0}, Landroid/view/View;->clearAnimation()V

    #@6
    .line 290
    const/4 v0, 0x0

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/MaterialProgressDrawable;->setRotation(F)V

    #@a
    .line 291
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@c
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setShowArrow(Z)V

    #@f
    .line 292
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@11
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorIndex(I)V

    #@14
    .line 293
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable;->mRing:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@16
    invoke-virtual {v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->resetOriginals()V

    #@19
    .line 294
    return-void
.end method

.method public updateSizes(I)V
    .locals 14
    .param p1, "size"    # I
        .annotation build Landroid/support/v4/widget/MaterialProgressDrawable$ProgressDrawableSize;
        .end annotation
    .end param

    #@0
    .prologue
    const-wide/high16 v2, 0x404c000000000000L    # 56.0

    #@2
    const-wide/high16 v12, 0x4044000000000000L    # 40.0

    #@4
    .line 146
    if-nez p1, :cond_0

    #@6
    .line 147
    const-wide/high16 v6, 0x4029000000000000L    # 12.5

    #@8
    const-wide/high16 v8, 0x4008000000000000L    # 3.0

    #@a
    const/high16 v10, 0x41400000    # 12.0f

    #@c
    const/high16 v11, 0x40c00000    # 6.0f

    #@e
    move-object v1, p0

    #@f
    move-wide v4, v2

    #@10
    invoke-direct/range {v1 .. v11}, Landroid/support/v4/widget/MaterialProgressDrawable;->setSizeParameters(DDDDFF)V

    #@13
    .line 153
    :goto_0
    return-void

    #@14
    .line 150
    :cond_0
    const-wide v6, 0x4021800000000000L    # 8.75

    #@19
    const-wide/high16 v8, 0x4004000000000000L    # 2.5

    #@1b
    const/high16 v10, 0x41200000    # 10.0f

    #@1d
    const/high16 v11, 0x40a00000    # 5.0f

    #@1f
    move-object v1, p0

    #@20
    move-wide v2, v12

    #@21
    move-wide v4, v12

    #@22
    invoke-direct/range {v1 .. v11}, Landroid/support/v4/widget/MaterialProgressDrawable;->setSizeParameters(DDDDFF)V

    #@25
    goto :goto_0
.end method
