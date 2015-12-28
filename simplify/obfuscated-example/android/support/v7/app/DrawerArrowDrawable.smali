.class abstract Landroid/support/v7/app/DrawerArrowDrawable;
.super Landroid/graphics/drawable/Drawable;
.source "DrawerArrowDrawable.java"


# static fields
.field private static final ARROW_HEAD_ANGLE:F


# instance fields
.field private final mBarGap:F

.field private final mBarSize:F

.field private final mBarThickness:F

.field private final mMiddleArrowSize:F

.field private final mPaint:Landroid/graphics/Paint;

.field private final mPath:Landroid/graphics/Path;

.field private mProgress:F

.field private final mSize:I

.field private final mSpin:Z

.field private final mTopBottomArrowSize:F

.field private mVerticalMirror:Z


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 38
    const-wide v0, 0x4046800000000000L    # 45.0

    #@5
    invoke-static {v0, v1}, Ljava/lang/Math;->toRadians(D)D

    #@8
    move-result-wide v0

    #@9
    double-to-float v0, v0

    #@a
    sput v0, Landroid/support/v7/app/DrawerArrowDrawable;->ARROW_HEAD_ANGLE:F

    #@c
    return-void
.end method

.method constructor <init>(Landroid/content/Context;)V
    .locals 9
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v8, 0x1

    #@1
    const/4 v7, 0x0

    #@2
    const/4 v6, 0x0

    #@3
    .line 63
    invoke-direct {p0}, Landroid/graphics/drawable/Drawable;-><init>()V

    #@6
    .line 35
    new-instance v1, Landroid/graphics/Paint;

    #@8
    invoke-direct {v1}, Landroid/graphics/Paint;-><init>()V

    #@b
    iput-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@d
    .line 52
    new-instance v1, Landroid/graphics/Path;

    #@f
    invoke-direct {v1}, Landroid/graphics/Path;-><init>()V

    #@12
    iput-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@14
    .line 56
    iput-boolean v7, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mVerticalMirror:Z

    #@16
    .line 64
    invoke-virtual {p1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@19
    move-result-object v1

    #@1a
    const/4 v2, 0x0

    #@1b
    sget-object v3, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle:[I

    #@1d
    sget v4, Landroid/support/v7/appcompat/R$attr;->drawerArrowStyle:I

    #@1f
    sget v5, Landroid/support/v7/appcompat/R$style;->Base_Widget_AppCompat_DrawerArrowToggle:I

    #@21
    invoke-virtual {v1, v2, v3, v4, v5}, Landroid/content/res/Resources$Theme;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@24
    move-result-object v0

    #@25
    .line 68
    .local v0, "typedArray":Landroid/content/res/TypedArray;
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@27
    invoke-virtual {v1, v8}, Landroid/graphics/Paint;->setAntiAlias(Z)V

    #@2a
    .line 69
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@2c
    sget v2, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_color:I

    #@2e
    invoke-virtual {v0, v2, v7}, Landroid/content/res/TypedArray;->getColor(II)I

    #@31
    move-result v2

    #@32
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setColor(I)V

    #@35
    .line 70
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_drawableSize:I

    #@37
    invoke-virtual {v0, v1, v7}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@3a
    move-result v1

    #@3b
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mSize:I

    #@3d
    .line 71
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_barSize:I

    #@3f
    invoke-virtual {v0, v1, v6}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@42
    move-result v1

    #@43
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarSize:F

    #@45
    .line 72
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_topBottomBarArrowSize:I

    #@47
    invoke-virtual {v0, v1, v6}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@4a
    move-result v1

    #@4b
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mTopBottomArrowSize:F

    #@4d
    .line 74
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_thickness:I

    #@4f
    invoke-virtual {v0, v1, v6}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@52
    move-result v1

    #@53
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarThickness:F

    #@55
    .line 75
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_gapBetweenBars:I

    #@57
    invoke-virtual {v0, v1, v6}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@5a
    move-result v1

    #@5b
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarGap:F

    #@5d
    .line 76
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_spinBars:I

    #@5f
    invoke-virtual {v0, v1, v8}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@62
    move-result v1

    #@63
    iput-boolean v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mSpin:Z

    #@65
    .line 77
    sget v1, Landroid/support/v7/appcompat/R$styleable;->DrawerArrowToggle_middleBarArrowSize:I

    #@67
    invoke-virtual {v0, v1, v6}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@6a
    move-result v1

    #@6b
    iput v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mMiddleArrowSize:F

    #@6d
    .line 79
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@70
    .line 81
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@72
    sget-object v2, Landroid/graphics/Paint$Style;->STROKE:Landroid/graphics/Paint$Style;

    #@74
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setStyle(Landroid/graphics/Paint$Style;)V

    #@77
    .line 82
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@79
    sget-object v2, Landroid/graphics/Paint$Join;->ROUND:Landroid/graphics/Paint$Join;

    #@7b
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setStrokeJoin(Landroid/graphics/Paint$Join;)V

    #@7e
    .line 83
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@80
    sget-object v2, Landroid/graphics/Paint$Cap;->SQUARE:Landroid/graphics/Paint$Cap;

    #@82
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setStrokeCap(Landroid/graphics/Paint$Cap;)V

    #@85
    .line 84
    iget-object v1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@87
    iget v2, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarThickness:F

    #@89
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setStrokeWidth(F)V

    #@8c
    .line 85
    return-void
.end method

.method private static lerp(FFF)F
    .locals 1
    .param p0, "a"    # F
    .param p1, "b"    # F
    .param p2, "t"    # F

    #@0
    .prologue
    .line 190
    sub-float v0, p1, p0

    #@2
    mul-float/2addr v0, p2

    #@3
    add-float/2addr v0, p0

    #@4
    return v0
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;)V
    .locals 18
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 98
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/app/DrawerArrowDrawable;->getBounds()Landroid/graphics/Rect;

    #@3
    move-result-object v6

    #@4
    .line 99
    .local v6, "bounds":Landroid/graphics/Rect;
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/app/DrawerArrowDrawable;->isLayoutRtl()Z

    #@7
    move-result v8

    #@8
    .line 101
    .local v8, "isRtl":Z
    move-object/from16 v0, p0

    #@a
    iget v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarSize:F

    #@c
    move-object/from16 v0, p0

    #@e
    iget v14, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mTopBottomArrowSize:F

    #@10
    move-object/from16 v0, p0

    #@12
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@14
    invoke-static {v13, v14, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@17
    move-result v4

    #@18
    .line 102
    .local v4, "arrowSize":F
    move-object/from16 v0, p0

    #@1a
    iget v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarSize:F

    #@1c
    move-object/from16 v0, p0

    #@1e
    iget v14, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mMiddleArrowSize:F

    #@20
    move-object/from16 v0, p0

    #@22
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@24
    invoke-static {v13, v14, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@27
    move-result v10

    #@28
    .line 104
    .local v10, "middleBarSize":F
    const/4 v13, 0x0

    #@29
    move-object/from16 v0, p0

    #@2b
    iget v14, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarThickness:F

    #@2d
    const/high16 v15, 0x40000000    # 2.0f

    #@2f
    div-float/2addr v14, v15

    #@30
    move-object/from16 v0, p0

    #@32
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@34
    invoke-static {v13, v14, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@37
    move-result v9

    #@38
    .line 106
    .local v9, "middleBarCut":F
    const/4 v13, 0x0

    #@39
    sget v14, Landroid/support/v7/app/DrawerArrowDrawable;->ARROW_HEAD_ANGLE:F

    #@3b
    move-object/from16 v0, p0

    #@3d
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@3f
    invoke-static {v13, v14, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@42
    move-result v11

    #@43
    .line 109
    .local v11, "rotation":F
    if-eqz v8, :cond_1

    #@45
    const/4 v13, 0x0

    #@46
    move v14, v13

    #@47
    :goto_0
    if-eqz v8, :cond_2

    #@49
    const/high16 v13, 0x43340000    # 180.0f

    #@4b
    :goto_1
    move-object/from16 v0, p0

    #@4d
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@4f
    invoke-static {v14, v13, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@52
    move-result v7

    #@53
    .line 110
    .local v7, "canvasRotate":F
    move-object/from16 v0, p0

    #@55
    iget v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarGap:F

    #@57
    move-object/from16 v0, p0

    #@59
    iget v14, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mBarThickness:F

    #@5b
    add-float/2addr v13, v14

    #@5c
    const/4 v14, 0x0

    #@5d
    move-object/from16 v0, p0

    #@5f
    iget v15, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@61
    invoke-static {v13, v14, v15}, Landroid/support/v7/app/DrawerArrowDrawable;->lerp(FFF)F

    #@64
    move-result v12

    #@65
    .line 111
    .local v12, "topBottomBarOffset":F
    move-object/from16 v0, p0

    #@67
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@69
    invoke-virtual {v13}, Landroid/graphics/Path;->rewind()V

    #@6c
    .line 113
    neg-float v13, v10

    #@6d
    const/high16 v14, 0x40000000    # 2.0f

    #@6f
    div-float v2, v13, v14

    #@71
    .line 115
    .local v2, "arrowEdge":F
    move-object/from16 v0, p0

    #@73
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@75
    add-float v14, v2, v9

    #@77
    const/4 v15, 0x0

    #@78
    invoke-virtual {v13, v14, v15}, Landroid/graphics/Path;->moveTo(FF)V

    #@7b
    .line 116
    move-object/from16 v0, p0

    #@7d
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@7f
    sub-float v14, v10, v9

    #@81
    const/4 v15, 0x0

    #@82
    invoke-virtual {v13, v14, v15}, Landroid/graphics/Path;->rLineTo(FF)V

    #@85
    .line 118
    float-to-double v14, v4

    #@86
    float-to-double v0, v11

    #@87
    move-wide/from16 v16, v0

    #@89
    invoke-static/range {v16 .. v17}, Ljava/lang/Math;->cos(D)D

    #@8c
    move-result-wide v16

    #@8d
    mul-double v14, v14, v16

    #@8f
    invoke-static {v14, v15}, Ljava/lang/Math;->round(D)J

    #@92
    move-result-wide v14

    #@93
    long-to-float v5, v14

    #@94
    .line 119
    .local v5, "arrowWidth":F
    float-to-double v14, v4

    #@95
    float-to-double v0, v11

    #@96
    move-wide/from16 v16, v0

    #@98
    invoke-static/range {v16 .. v17}, Ljava/lang/Math;->sin(D)D

    #@9b
    move-result-wide v16

    #@9c
    mul-double v14, v14, v16

    #@9e
    invoke-static {v14, v15}, Ljava/lang/Math;->round(D)J

    #@a1
    move-result-wide v14

    #@a2
    long-to-float v3, v14

    #@a3
    .line 122
    .local v3, "arrowHeight":F
    move-object/from16 v0, p0

    #@a5
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@a7
    invoke-virtual {v13, v2, v12}, Landroid/graphics/Path;->moveTo(FF)V

    #@aa
    .line 123
    move-object/from16 v0, p0

    #@ac
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@ae
    invoke-virtual {v13, v5, v3}, Landroid/graphics/Path;->rLineTo(FF)V

    #@b1
    .line 126
    move-object/from16 v0, p0

    #@b3
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@b5
    neg-float v14, v12

    #@b6
    invoke-virtual {v13, v2, v14}, Landroid/graphics/Path;->moveTo(FF)V

    #@b9
    .line 127
    move-object/from16 v0, p0

    #@bb
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@bd
    neg-float v14, v3

    #@be
    invoke-virtual {v13, v5, v14}, Landroid/graphics/Path;->rLineTo(FF)V

    #@c1
    .line 128
    move-object/from16 v0, p0

    #@c3
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@c5
    const/4 v14, 0x0

    #@c6
    const/4 v15, 0x0

    #@c7
    invoke-virtual {v13, v14, v15}, Landroid/graphics/Path;->moveTo(FF)V

    #@ca
    .line 129
    move-object/from16 v0, p0

    #@cc
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@ce
    invoke-virtual {v13}, Landroid/graphics/Path;->close()V

    #@d1
    .line 131
    invoke-virtual/range {p1 .. p1}, Landroid/graphics/Canvas;->save()I

    #@d4
    .line 134
    move-object/from16 v0, p0

    #@d6
    iget-boolean v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mSpin:Z

    #@d8
    if-eqz v13, :cond_4

    #@da
    .line 135
    move-object/from16 v0, p0

    #@dc
    iget-boolean v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mVerticalMirror:Z

    #@de
    xor-int/2addr v13, v8

    #@df
    if-eqz v13, :cond_3

    #@e1
    const/4 v13, -0x1

    #@e2
    :goto_2
    int-to-float v13, v13

    #@e3
    mul-float/2addr v13, v7

    #@e4
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerX()I

    #@e7
    move-result v14

    #@e8
    int-to-float v14, v14

    #@e9
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerY()I

    #@ec
    move-result v15

    #@ed
    int-to-float v15, v15

    #@ee
    move-object/from16 v0, p1

    #@f0
    invoke-virtual {v0, v13, v14, v15}, Landroid/graphics/Canvas;->rotate(FFF)V

    #@f3
    .line 140
    :cond_0
    :goto_3
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerX()I

    #@f6
    move-result v13

    #@f7
    int-to-float v13, v13

    #@f8
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerY()I

    #@fb
    move-result v14

    #@fc
    int-to-float v14, v14

    #@fd
    move-object/from16 v0, p1

    #@ff
    invoke-virtual {v0, v13, v14}, Landroid/graphics/Canvas;->translate(FF)V

    #@102
    .line 141
    move-object/from16 v0, p0

    #@104
    iget-object v13, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPath:Landroid/graphics/Path;

    #@106
    move-object/from16 v0, p0

    #@108
    iget-object v14, v0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@10a
    move-object/from16 v0, p1

    #@10c
    invoke-virtual {v0, v13, v14}, Landroid/graphics/Canvas;->drawPath(Landroid/graphics/Path;Landroid/graphics/Paint;)V

    #@10f
    .line 143
    invoke-virtual/range {p1 .. p1}, Landroid/graphics/Canvas;->restore()V

    #@112
    .line 144
    return-void

    #@113
    .line 109
    .end local v2    # "arrowEdge":F
    .end local v3    # "arrowHeight":F
    .end local v5    # "arrowWidth":F
    .end local v7    # "canvasRotate":F
    .end local v12    # "topBottomBarOffset":F
    :cond_1
    const/high16 v13, -0x3ccc0000    # -180.0f

    #@115
    move v14, v13

    #@116
    goto/16 :goto_0

    #@118
    :cond_2
    const/4 v13, 0x0

    #@119
    goto/16 :goto_1

    #@11b
    .line 135
    .restart local v2    # "arrowEdge":F
    .restart local v3    # "arrowHeight":F
    .restart local v5    # "arrowWidth":F
    .restart local v7    # "canvasRotate":F
    .restart local v12    # "topBottomBarOffset":F
    :cond_3
    const/4 v13, 0x1

    #@11c
    goto :goto_2

    #@11d
    .line 137
    :cond_4
    if-eqz v8, :cond_0

    #@11f
    .line 138
    const/high16 v13, 0x43340000    # 180.0f

    #@121
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerX()I

    #@124
    move-result v14

    #@125
    int-to-float v14, v14

    #@126
    invoke-virtual {v6}, Landroid/graphics/Rect;->centerY()I

    #@129
    move-result v15

    #@12a
    int-to-float v15, v15

    #@12b
    move-object/from16 v0, p1

    #@12d
    invoke-virtual {v0, v13, v14, v15}, Landroid/graphics/Canvas;->rotate(FFF)V

    #@130
    goto :goto_3
.end method

.method public getIntrinsicHeight()I
    .locals 1

    #@0
    .prologue
    .line 164
    iget v0, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mSize:I

    #@2
    return v0
.end method

.method public getIntrinsicWidth()I
    .locals 1

    #@0
    .prologue
    .line 169
    iget v0, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mSize:I

    #@2
    return v0
.end method

.method public getOpacity()I
    .locals 1

    #@0
    .prologue
    .line 174
    const/4 v0, -0x3

    #@1
    return v0
.end method

.method public getProgress()F
    .locals 1

    #@0
    .prologue
    .line 178
    iget v0, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@2
    return v0
.end method

.method public isAutoMirrored()Z
    .locals 1

    #@0
    .prologue
    .line 154
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method abstract isLayoutRtl()Z
.end method

.method public setAlpha(I)V
    .locals 1
    .param p1, "i"    # I

    #@0
    .prologue
    .line 148
    iget-object v0, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/Paint;->setAlpha(I)V

    #@5
    .line 149
    return-void
.end method

.method public setColorFilter(Landroid/graphics/ColorFilter;)V
    .locals 1
    .param p1, "colorFilter"    # Landroid/graphics/ColorFilter;

    #@0
    .prologue
    .line 159
    iget-object v0, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mPaint:Landroid/graphics/Paint;

    #@2
    invoke-virtual {v0, p1}, Landroid/graphics/Paint;->setColorFilter(Landroid/graphics/ColorFilter;)Landroid/graphics/ColorFilter;

    #@5
    .line 160
    return-void
.end method

.method public setProgress(F)V
    .locals 0
    .param p1, "progress"    # F

    #@0
    .prologue
    .line 182
    iput p1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mProgress:F

    #@2
    .line 183
    invoke-virtual {p0}, Landroid/support/v7/app/DrawerArrowDrawable;->invalidateSelf()V

    #@5
    .line 184
    return-void
.end method

.method protected setVerticalMirror(Z)V
    .locals 0
    .param p1, "verticalMirror"    # Z

    #@0
    .prologue
    .line 93
    iput-boolean p1, p0, Landroid/support/v7/app/DrawerArrowDrawable;->mVerticalMirror:Z

    #@2
    .line 94
    return-void
.end method
