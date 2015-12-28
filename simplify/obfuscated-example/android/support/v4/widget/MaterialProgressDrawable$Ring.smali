.class Landroid/support/v4/widget/MaterialProgressDrawable$Ring;
.super Ljava/lang/Object;
.source "MaterialProgressDrawable.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/MaterialProgressDrawable;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "Ring"
.end annotation


# instance fields
.field private mAlpha:I

.field private mArrow:Landroid/graphics/Path;

.field private mArrowHeight:I

.field private final mArrowPaint:Landroid/graphics/Paint;

.field private mArrowScale:F

.field private mArrowWidth:I

.field private mBackgroundColor:I

.field private final mCallback:Landroid/graphics/drawable/Drawable$Callback;

.field private final mCirclePaint:Landroid/graphics/Paint;

.field private mColorIndex:I

.field private mColors:[I

.field private mEndTrim:F

.field private final mPaint:Landroid/graphics/Paint;

.field private mRingCenterRadius:D

.field private mRotation:F

.field private mShowArrow:Z

.field private mStartTrim:F

.field private mStartingEndTrim:F

.field private mStartingRotation:F

.field private mStartingStartTrim:F

.field private mStrokeInset:F

.field private mStrokeWidth:F

.field private final mTempBounds:Landroid/graphics/RectF;


# direct methods
.method public constructor <init>(Landroid/graphics/drawable/Drawable$Callback;)V
    .locals 3
    .param p1, "callback"    # Landroid/graphics/drawable/Drawable$Callback;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 429
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 399
    new-instance v0, Landroid/graphics/RectF;

    #@7
    invoke-direct {v0}, Landroid/graphics/RectF;-><init>()V

    #@a
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mTempBounds:Landroid/graphics/RectF;

    #@c
    .line 400
    new-instance v0, Landroid/graphics/Paint;

    #@e
    invoke-direct {v0}, Landroid/graphics/Paint;-><init>()V

    #@11
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@13
    .line 401
    new-instance v0, Landroid/graphics/Paint;

    #@15
    invoke-direct {v0}, Landroid/graphics/Paint;-><init>()V

    #@18
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowPaint:Landroid/graphics/Paint;

    #@1a
    .line 405
    iput v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartTrim:F

    #@1c
    .line 406
    iput v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mEndTrim:F

    #@1e
    .line 407
    iput v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@20
    .line 408
    const/high16 v0, 0x40a00000    # 5.0f

    #@22
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeWidth:F

    #@24
    .line 409
    const/high16 v0, 0x40200000    # 2.5f

    #@26
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@28
    .line 426
    new-instance v0, Landroid/graphics/Paint;

    #@2a
    invoke-direct {v0}, Landroid/graphics/Paint;-><init>()V

    #@2d
    iput-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCirclePaint:Landroid/graphics/Paint;

    #@2f
    .line 430
    iput-object p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCallback:Landroid/graphics/drawable/Drawable$Callback;

    #@31
    .line 432
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@33
    sget-object v1, Landroid/graphics/Paint$Cap;->SQUARE:Landroid/graphics/Paint$Cap;

    #@35
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setStrokeCap(Landroid/graphics/Paint$Cap;)V

    #@38
    .line 433
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@3a
    invoke-virtual {v0, v2}, Landroid/graphics/Paint;->setAntiAlias(Z)V

    #@3d
    .line 434
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@3f
    sget-object v1, Landroid/graphics/Paint$Style;->STROKE:Landroid/graphics/Paint$Style;

    #@41
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setStyle(Landroid/graphics/Paint$Style;)V

    #@44
    .line 436
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowPaint:Landroid/graphics/Paint;

    #@46
    sget-object v1, Landroid/graphics/Paint$Style;->FILL:Landroid/graphics/Paint$Style;

    #@48
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setStyle(Landroid/graphics/Paint$Style;)V

    #@4b
    .line 437
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowPaint:Landroid/graphics/Paint;

    #@4d
    invoke-virtual {v0, v2}, Landroid/graphics/Paint;->setAntiAlias(Z)V

    #@50
    .line 438
    return-void
.end method

.method private drawTriangle(Landroid/graphics/Canvas;FFLandroid/graphics/Rect;)V
    .locals 8
    .param p1, "c"    # Landroid/graphics/Canvas;
    .param p2, "startAngle"    # F
    .param p3, "sweepAngle"    # F
    .param p4, "bounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 481
    iget-boolean v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mShowArrow:Z

    #@2
    if-eqz v3, :cond_0

    #@4
    .line 482
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@6
    if-nez v3, :cond_1

    #@8
    .line 483
    new-instance v3, Landroid/graphics/Path;

    #@a
    invoke-direct {v3}, Landroid/graphics/Path;-><init>()V

    #@d
    iput-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@f
    .line 484
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@11
    sget-object v4, Landroid/graphics/Path$FillType;->EVEN_ODD:Landroid/graphics/Path$FillType;

    #@13
    invoke-virtual {v3, v4}, Landroid/graphics/Path;->setFillType(Landroid/graphics/Path$FillType;)V

    #@16
    .line 491
    :goto_0
    iget v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@18
    float-to-int v3, v3

    #@19
    div-int/lit8 v3, v3, 0x2

    #@1b
    int-to-float v3, v3

    #@1c
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@1e
    mul-float v0, v3, v4

    #@20
    .line 492
    .local v0, "inset":F
    iget-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@22
    const-wide/16 v6, 0x0

    #@24
    invoke-static {v6, v7}, Ljava/lang/Math;->cos(D)D

    #@27
    move-result-wide v6

    #@28
    mul-double/2addr v4, v6

    #@29
    invoke-virtual {p4}, Landroid/graphics/Rect;->exactCenterX()F

    #@2c
    move-result v3

    #@2d
    float-to-double v6, v3

    #@2e
    add-double/2addr v4, v6

    #@2f
    double-to-float v1, v4

    #@30
    .line 493
    .local v1, "x":F
    iget-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@32
    const-wide/16 v6, 0x0

    #@34
    invoke-static {v6, v7}, Ljava/lang/Math;->sin(D)D

    #@37
    move-result-wide v6

    #@38
    mul-double/2addr v4, v6

    #@39
    invoke-virtual {p4}, Landroid/graphics/Rect;->exactCenterY()F

    #@3c
    move-result v3

    #@3d
    float-to-double v6, v3

    #@3e
    add-double/2addr v4, v6

    #@3f
    double-to-float v2, v4

    #@40
    .line 499
    .local v2, "y":F
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@42
    const/4 v4, 0x0

    #@43
    const/4 v5, 0x0

    #@44
    invoke-virtual {v3, v4, v5}, Landroid/graphics/Path;->moveTo(FF)V

    #@47
    .line 500
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@49
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowWidth:I

    #@4b
    int-to-float v4, v4

    #@4c
    iget v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@4e
    mul-float/2addr v4, v5

    #@4f
    const/4 v5, 0x0

    #@50
    invoke-virtual {v3, v4, v5}, Landroid/graphics/Path;->lineTo(FF)V

    #@53
    .line 501
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@55
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowWidth:I

    #@57
    int-to-float v4, v4

    #@58
    iget v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@5a
    mul-float/2addr v4, v5

    #@5b
    const/high16 v5, 0x40000000    # 2.0f

    #@5d
    div-float/2addr v4, v5

    #@5e
    iget v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowHeight:I

    #@60
    int-to-float v5, v5

    #@61
    iget v6, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@63
    mul-float/2addr v5, v6

    #@64
    invoke-virtual {v3, v4, v5}, Landroid/graphics/Path;->lineTo(FF)V

    #@67
    .line 503
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@69
    sub-float v4, v1, v0

    #@6b
    invoke-virtual {v3, v4, v2}, Landroid/graphics/Path;->offset(FF)V

    #@6e
    .line 504
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@70
    invoke-virtual {v3}, Landroid/graphics/Path;->close()V

    #@73
    .line 506
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowPaint:Landroid/graphics/Paint;

    #@75
    iget-object v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColors:[I

    #@77
    iget v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColorIndex:I

    #@79
    aget v4, v4, v5

    #@7b
    invoke-virtual {v3, v4}, Landroid/graphics/Paint;->setColor(I)V

    #@7e
    .line 507
    add-float v3, p2, p3

    #@80
    const/high16 v4, 0x40a00000    # 5.0f

    #@82
    sub-float/2addr v3, v4

    #@83
    invoke-virtual {p4}, Landroid/graphics/Rect;->exactCenterX()F

    #@86
    move-result v4

    #@87
    invoke-virtual {p4}, Landroid/graphics/Rect;->exactCenterY()F

    #@8a
    move-result v5

    #@8b
    invoke-virtual {p1, v3, v4, v5}, Landroid/graphics/Canvas;->rotate(FFF)V

    #@8e
    .line 509
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@90
    iget-object v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowPaint:Landroid/graphics/Paint;

    #@92
    invoke-virtual {p1, v3, v4}, Landroid/graphics/Canvas;->drawPath(Landroid/graphics/Path;Landroid/graphics/Paint;)V

    #@95
    .line 511
    .end local v0    # "inset":F
    .end local v1    # "x":F
    .end local v2    # "y":F
    :cond_0
    return-void

    #@96
    .line 486
    :cond_1
    iget-object v3, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrow:Landroid/graphics/Path;

    #@98
    invoke-virtual {v3}, Landroid/graphics/Path;->reset()V

    #@9b
    goto/16 :goto_0
.end method

.method private invalidateSelf()V
    .locals 2

    #@0
    .prologue
    .line 692
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCallback:Landroid/graphics/drawable/Drawable$Callback;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-interface {v0, v1}, Landroid/graphics/drawable/Drawable$Callback;->invalidateDrawable(Landroid/graphics/drawable/Drawable;)V

    #@6
    .line 693
    return-void
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;Landroid/graphics/Rect;)V
    .locals 8
    .param p1, "c"    # Landroid/graphics/Canvas;
    .param p2, "bounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    const/high16 v5, 0x43b40000    # 360.0f

    #@2
    .line 459
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mTempBounds:Landroid/graphics/RectF;

    #@4
    .line 460
    .local v1, "arcBounds":Landroid/graphics/RectF;
    invoke-virtual {v1, p2}, Landroid/graphics/RectF;->set(Landroid/graphics/Rect;)V

    #@7
    .line 461
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@9
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@b
    invoke-virtual {v1, v0, v4}, Landroid/graphics/RectF;->inset(FF)V

    #@e
    .line 463
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartTrim:F

    #@10
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@12
    add-float/2addr v0, v4

    #@13
    mul-float v2, v0, v5

    #@15
    .line 464
    .local v2, "startAngle":F
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mEndTrim:F

    #@17
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@19
    add-float/2addr v0, v4

    #@1a
    mul-float v6, v0, v5

    #@1c
    .line 465
    .local v6, "endAngle":F
    sub-float v3, v6, v2

    #@1e
    .line 467
    .local v3, "sweepAngle":F
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@20
    iget-object v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColors:[I

    #@22
    iget v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColorIndex:I

    #@24
    aget v4, v4, v5

    #@26
    invoke-virtual {v0, v4}, Landroid/graphics/Paint;->setColor(I)V

    #@29
    .line 468
    const/4 v4, 0x0

    #@2a
    iget-object v5, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@2c
    move-object v0, p1

    #@2d
    invoke-virtual/range {v0 .. v5}, Landroid/graphics/Canvas;->drawArc(Landroid/graphics/RectF;FFZLandroid/graphics/Paint;)V

    #@30
    .line 470
    invoke-direct {p0, p1, v2, v3, p2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->drawTriangle(Landroid/graphics/Canvas;FFLandroid/graphics/Rect;)V

    #@33
    .line 472
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mAlpha:I

    #@35
    const/16 v4, 0xff

    #@37
    if-ge v0, v4, :cond_0

    #@39
    .line 473
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCirclePaint:Landroid/graphics/Paint;

    #@3b
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mBackgroundColor:I

    #@3d
    invoke-virtual {v0, v4}, Landroid/graphics/Paint;->setColor(I)V

    #@40
    .line 474
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCirclePaint:Landroid/graphics/Paint;

    #@42
    iget v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mAlpha:I

    #@44
    rsub-int v4, v4, 0xff

    #@46
    invoke-virtual {v0, v4}, Landroid/graphics/Paint;->setAlpha(I)V

    #@49
    .line 475
    invoke-virtual {p2}, Landroid/graphics/Rect;->exactCenterX()F

    #@4c
    move-result v0

    #@4d
    invoke-virtual {p2}, Landroid/graphics/Rect;->exactCenterY()F

    #@50
    move-result v4

    #@51
    invoke-virtual {p2}, Landroid/graphics/Rect;->width()I

    #@54
    move-result v5

    #@55
    div-int/lit8 v5, v5, 0x2

    #@57
    int-to-float v5, v5

    #@58
    iget-object v7, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mCirclePaint:Landroid/graphics/Paint;

    #@5a
    invoke-virtual {p1, v0, v4, v5, v7}, Landroid/graphics/Canvas;->drawCircle(FFFLandroid/graphics/Paint;)V

    #@5d
    .line 478
    :cond_0
    return-void
.end method

.method public getAlpha()I
    .locals 1

    #@0
    .prologue
    .line 556
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mAlpha:I

    #@2
    return v0
.end method

.method public getCenterRadius()D
    .locals 2

    #@0
    .prologue
    .line 639
    iget-wide v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@2
    return-wide v0
.end method

.method public getEndTrim()F
    .locals 1

    #@0
    .prologue
    .line 600
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mEndTrim:F

    #@2
    return v0
.end method

.method public getInsets()F
    .locals 1

    #@0
    .prologue
    .line 627
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@2
    return v0
.end method

.method public getRotation()F
    .locals 1

    #@0
    .prologue
    .line 611
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@2
    return v0
.end method

.method public getStartTrim()F
    .locals 1

    #@0
    .prologue
    .line 581
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartTrim:F

    #@2
    return v0
.end method

.method public getStartingEndTrim()F
    .locals 1

    #@0
    .prologue
    .line 589
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingEndTrim:F

    #@2
    return v0
.end method

.method public getStartingRotation()F
    .locals 1

    #@0
    .prologue
    .line 666
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingRotation:F

    #@2
    return v0
.end method

.method public getStartingStartTrim()F
    .locals 1

    #@0
    .prologue
    .line 585
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingStartTrim:F

    #@2
    return v0
.end method

.method public getStrokeWidth()F
    .locals 1

    #@0
    .prologue
    .line 570
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeWidth:F

    #@2
    return v0
.end method

.method public goToNextColor()V
    .locals 2

    #@0
    .prologue
    .line 537
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColorIndex:I

    #@2
    add-int/lit8 v0, v0, 0x1

    #@4
    iget-object v1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColors:[I

    #@6
    array-length v1, v1

    #@7
    rem-int/2addr v0, v1

    #@8
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColorIndex:I

    #@a
    .line 538
    return-void
.end method

.method public resetOriginals()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 683
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingStartTrim:F

    #@3
    .line 684
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingEndTrim:F

    #@5
    .line 685
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingRotation:F

    #@7
    .line 686
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setStartTrim(F)V

    #@a
    .line 687
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setEndTrim(F)V

    #@d
    .line 688
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setRotation(F)V

    #@10
    .line 689
    return-void
.end method

.method public setAlpha(I)V
    .locals 0
    .param p1, "alpha"    # I

    #@0
    .prologue
    .line 549
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mAlpha:I

    #@2
    .line 550
    return-void
.end method

.method public setArrowDimensions(FF)V
    .locals 1
    .param p1, "width"    # F
    .param p2, "height"    # F

    #@0
    .prologue
    .line 451
    float-to-int v0, p1

    #@1
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowWidth:I

    #@3
    .line 452
    float-to-int v0, p2

    #@4
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowHeight:I

    #@6
    .line 453
    return-void
.end method

.method public setArrowScale(F)V
    .locals 1
    .param p1, "scale"    # F

    #@0
    .prologue
    .line 656
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@2
    cmpl-float v0, p1, v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 657
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mArrowScale:F

    #@8
    .line 658
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@b
    .line 660
    :cond_0
    return-void
.end method

.method public setBackgroundColor(I)V
    .locals 0
    .param p1, "color"    # I

    #@0
    .prologue
    .line 441
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mBackgroundColor:I

    #@2
    .line 442
    return-void
.end method

.method public setCenterRadius(D)V
    .locals 1
    .param p1, "centerRadius"    # D

    #@0
    .prologue
    .line 635
    iput-wide p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@2
    .line 636
    return-void
.end method

.method public setColorFilter(Landroid/graphics/ColorFilter;)V
    .locals 1
    .param p1, "filter"    # Landroid/graphics/ColorFilter;

    #@0
    .prologue
    .line 541
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/Paint;->setColorFilter(Landroid/graphics/ColorFilter;)Landroid/graphics/ColorFilter;

    #@5
    .line 542
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@8
    .line 543
    return-void
.end method

.method public setColorIndex(I)V
    .locals 0
    .param p1, "index"    # I

    #@0
    .prologue
    .line 529
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColorIndex:I

    #@2
    .line 530
    return-void
.end method

.method public setColors([I)V
    .locals 1
    .param p1, "colors"    # [I
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param

    #@0
    .prologue
    .line 519
    iput-object p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mColors:[I

    #@2
    .line 521
    const/4 v0, 0x0

    #@3
    invoke-virtual {p0, v0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setColorIndex(I)V

    #@6
    .line 522
    return-void
.end method

.method public setEndTrim(F)V
    .locals 0
    .param p1, "endTrim"    # F

    #@0
    .prologue
    .line 594
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mEndTrim:F

    #@2
    .line 595
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@5
    .line 596
    return-void
.end method

.method public setInsets(II)V
    .locals 7
    .param p1, "width"    # I
    .param p2, "height"    # I

    #@0
    .prologue
    const/high16 v6, 0x40000000    # 2.0f

    #@2
    .line 615
    invoke-static {p1, p2}, Ljava/lang/Math;->min(II)I

    #@5
    move-result v2

    #@6
    int-to-float v1, v2

    #@7
    .line 617
    .local v1, "minEdge":F
    iget-wide v2, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@9
    const-wide/16 v4, 0x0

    #@b
    cmpg-double v2, v2, v4

    #@d
    if-lez v2, :cond_0

    #@f
    const/4 v2, 0x0

    #@10
    cmpg-float v2, v1, v2

    #@12
    if-gez v2, :cond_1

    #@14
    .line 618
    :cond_0
    iget v2, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeWidth:F

    #@16
    div-float/2addr v2, v6

    #@17
    float-to-double v2, v2

    #@18
    invoke-static {v2, v3}, Ljava/lang/Math;->ceil(D)D

    #@1b
    move-result-wide v2

    #@1c
    double-to-float v0, v2

    #@1d
    .line 622
    .local v0, "insets":F
    :goto_0
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeInset:F

    #@1f
    .line 623
    return-void

    #@20
    .line 620
    .end local v0    # "insets":F
    :cond_1
    div-float v2, v1, v6

    #@22
    float-to-double v2, v2

    #@23
    iget-wide v4, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRingCenterRadius:D

    #@25
    sub-double/2addr v2, v4

    #@26
    double-to-float v0, v2

    #@27
    .restart local v0    # "insets":F
    goto :goto_0
.end method

.method public setRotation(F)V
    .locals 0
    .param p1, "rotation"    # F

    #@0
    .prologue
    .line 605
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@2
    .line 606
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@5
    .line 607
    return-void
.end method

.method public setShowArrow(Z)V
    .locals 1
    .param p1, "show"    # Z

    #@0
    .prologue
    .line 646
    iget-boolean v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mShowArrow:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 647
    iput-boolean p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mShowArrow:Z

    #@6
    .line 648
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@9
    .line 650
    :cond_0
    return-void
.end method

.method public setStartTrim(F)V
    .locals 0
    .param p1, "startTrim"    # F

    #@0
    .prologue
    .line 575
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartTrim:F

    #@2
    .line 576
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@5
    .line 577
    return-void
.end method

.method public setStrokeWidth(F)V
    .locals 1
    .param p1, "strokeWidth"    # F

    #@0
    .prologue
    .line 563
    iput p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStrokeWidth:F

    #@2
    .line 564
    iget-object v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mPaint:Landroid/graphics/Paint;

    #@4
    invoke-virtual {v0, p1}, Landroid/graphics/Paint;->setStrokeWidth(F)V

    #@7
    .line 565
    invoke-direct {p0}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->invalidateSelf()V

    #@a
    .line 566
    return-void
.end method

.method public storeOriginals()V
    .locals 1

    #@0
    .prologue
    .line 674
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartTrim:F

    #@2
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingStartTrim:F

    #@4
    .line 675
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mEndTrim:F

    #@6
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingEndTrim:F

    #@8
    .line 676
    iget v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mRotation:F

    #@a
    iput v0, p0, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->mStartingRotation:F

    #@c
    .line 677
    return-void
.end method
