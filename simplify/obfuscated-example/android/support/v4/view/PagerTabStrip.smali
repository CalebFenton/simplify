.class public Landroid/support/v4/view/PagerTabStrip;
.super Landroid/support/v4/view/PagerTitleStrip;
.source "PagerTabStrip.java"


# static fields
.field private static final FULL_UNDERLINE_HEIGHT:I = 0x1

.field private static final INDICATOR_HEIGHT:I = 0x3

.field private static final MIN_PADDING_BOTTOM:I = 0x6

.field private static final MIN_STRIP_HEIGHT:I = 0x20

.field private static final MIN_TEXT_SPACING:I = 0x40

.field private static final TAB_PADDING:I = 0x10

.field private static final TAB_SPACING:I = 0x20

.field private static final TAG:Ljava/lang/String; = "PagerTabStrip"


# instance fields
.field private mDrawFullUnderline:Z

.field private mDrawFullUnderlineSet:Z

.field private mFullUnderlineHeight:I

.field private mIgnoreTap:Z

.field private mIndicatorColor:I

.field private mIndicatorHeight:I

.field private mInitialMotionX:F

.field private mInitialMotionY:F

.field private mMinPaddingBottom:I

.field private mMinStripHeight:I

.field private mMinTextSpacing:I

.field private mTabAlpha:I

.field private mTabPadding:I

.field private final mTabPaint:Landroid/graphics/Paint;

.field private final mTempRect:Landroid/graphics/Rect;

.field private mTouchSlop:I


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 78
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/view/PagerTabStrip;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 79
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 7
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    const/4 v5, 0x0

    #@2
    const/high16 v3, 0x3f000000    # 0.5f

    #@4
    .line 82
    invoke-direct {p0, p1, p2}, Landroid/support/v4/view/PagerTitleStrip;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@7
    .line 63
    new-instance v1, Landroid/graphics/Paint;

    #@9
    invoke-direct {v1}, Landroid/graphics/Paint;-><init>()V

    #@c
    iput-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@e
    .line 64
    new-instance v1, Landroid/graphics/Rect;

    #@10
    invoke-direct {v1}, Landroid/graphics/Rect;-><init>()V

    #@13
    iput-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTempRect:Landroid/graphics/Rect;

    #@15
    .line 66
    const/16 v1, 0xff

    #@17
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabAlpha:I

    #@19
    .line 68
    iput-boolean v5, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@1b
    .line 69
    iput-boolean v5, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderlineSet:Z

    #@1d
    .line 84
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTextColor:I

    #@1f
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@21
    .line 85
    iget-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@23
    iget v2, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@25
    invoke-virtual {v1, v2}, Landroid/graphics/Paint;->setColor(I)V

    #@28
    .line 89
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@2b
    move-result-object v1

    #@2c
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@2f
    move-result-object v1

    #@30
    iget v0, v1, Landroid/util/DisplayMetrics;->density:F

    #@32
    .line 90
    .local v0, "density":F
    const/high16 v1, 0x40400000    # 3.0f

    #@34
    mul-float/2addr v1, v0

    #@35
    add-float/2addr v1, v3

    #@36
    float-to-int v1, v1

    #@37
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorHeight:I

    #@39
    .line 91
    const/high16 v1, 0x40c00000    # 6.0f

    #@3b
    mul-float/2addr v1, v0

    #@3c
    add-float/2addr v1, v3

    #@3d
    float-to-int v1, v1

    #@3e
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mMinPaddingBottom:I

    #@40
    .line 92
    const/high16 v1, 0x42800000    # 64.0f

    #@42
    mul-float/2addr v1, v0

    #@43
    float-to-int v1, v1

    #@44
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mMinTextSpacing:I

    #@46
    .line 93
    const/high16 v1, 0x41800000    # 16.0f

    #@48
    mul-float/2addr v1, v0

    #@49
    add-float/2addr v1, v3

    #@4a
    float-to-int v1, v1

    #@4b
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@4d
    .line 94
    const/high16 v1, 0x3f800000    # 1.0f

    #@4f
    mul-float/2addr v1, v0

    #@50
    add-float/2addr v1, v3

    #@51
    float-to-int v1, v1

    #@52
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mFullUnderlineHeight:I

    #@54
    .line 95
    const/high16 v1, 0x42000000    # 32.0f

    #@56
    mul-float/2addr v1, v0

    #@57
    add-float/2addr v1, v3

    #@58
    float-to-int v1, v1

    #@59
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mMinStripHeight:I

    #@5b
    .line 96
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@5e
    move-result-object v1

    #@5f
    invoke-virtual {v1}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@62
    move-result v1

    #@63
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTouchSlop:I

    #@65
    .line 99
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingLeft()I

    #@68
    move-result v1

    #@69
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingTop()I

    #@6c
    move-result v2

    #@6d
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingRight()I

    #@70
    move-result v3

    #@71
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingBottom()I

    #@74
    move-result v4

    #@75
    invoke-virtual {p0, v1, v2, v3, v4}, Landroid/support/v4/view/PagerTabStrip;->setPadding(IIII)V

    #@78
    .line 100
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getTextSpacing()I

    #@7b
    move-result v1

    #@7c
    invoke-virtual {p0, v1}, Landroid/support/v4/view/PagerTabStrip;->setTextSpacing(I)V

    #@7f
    .line 102
    invoke-virtual {p0, v5}, Landroid/support/v4/view/PagerTabStrip;->setWillNotDraw(Z)V

    #@82
    .line 104
    iget-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mPrevText:Landroid/widget/TextView;

    #@84
    invoke-virtual {v1, v6}, Landroid/widget/TextView;->setFocusable(Z)V

    #@87
    .line 105
    iget-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mPrevText:Landroid/widget/TextView;

    #@89
    new-instance v2, Landroid/support/v4/view/PagerTabStrip$1;

    #@8b
    invoke-direct {v2, p0}, Landroid/support/v4/view/PagerTabStrip$1;-><init>(Landroid/support/v4/view/PagerTabStrip;)V

    #@8e
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@91
    .line 112
    iget-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mNextText:Landroid/widget/TextView;

    #@93
    invoke-virtual {v1, v6}, Landroid/widget/TextView;->setFocusable(Z)V

    #@96
    .line 113
    iget-object v1, p0, Landroid/support/v4/view/PagerTabStrip;->mNextText:Landroid/widget/TextView;

    #@98
    new-instance v2, Landroid/support/v4/view/PagerTabStrip$2;

    #@9a
    invoke-direct {v2, p0}, Landroid/support/v4/view/PagerTabStrip$2;-><init>(Landroid/support/v4/view/PagerTabStrip;)V

    #@9d
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@a0
    .line 120
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getBackground()Landroid/graphics/drawable/Drawable;

    #@a3
    move-result-object v1

    #@a4
    if-nez v1, :cond_0

    #@a6
    .line 121
    iput-boolean v6, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@a8
    .line 123
    :cond_0
    return-void
.end method


# virtual methods
.method public getDrawFullUnderline()Z
    .locals 1

    #@0
    .prologue
    .line 212
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@2
    return v0
.end method

.method getMinHeight()I
    .locals 2

    #@0
    .prologue
    .line 217
    invoke-super {p0}, Landroid/support/v4/view/PagerTitleStrip;->getMinHeight()I

    #@3
    move-result v0

    #@4
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mMinStripHeight:I

    #@6
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@9
    move-result v0

    #@a
    return v0
.end method

.method public getTabIndicatorColor()I
    .locals 1

    #@0
    .prologue
    .line 149
    iget v0, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@2
    return v0
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 12
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    const v11, 0xffffff

    #@3
    .line 259
    invoke-super {p0, p1}, Landroid/support/v4/view/PagerTitleStrip;->onDraw(Landroid/graphics/Canvas;)V

    #@6
    .line 261
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getHeight()I

    #@9
    move-result v7

    #@a
    .line 262
    .local v7, "height":I
    move v6, v7

    #@b
    .line 263
    .local v6, "bottom":I
    iget-object v0, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@d
    invoke-virtual {v0}, Landroid/widget/TextView;->getLeft()I

    #@10
    move-result v0

    #@11
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@13
    sub-int v8, v0, v1

    #@15
    .line 264
    .local v8, "left":I
    iget-object v0, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@17
    invoke-virtual {v0}, Landroid/widget/TextView;->getRight()I

    #@1a
    move-result v0

    #@1b
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@1d
    add-int v9, v0, v1

    #@1f
    .line 265
    .local v9, "right":I
    iget v0, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorHeight:I

    #@21
    sub-int v10, v6, v0

    #@23
    .line 267
    .local v10, "top":I
    iget-object v0, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@25
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mTabAlpha:I

    #@27
    shl-int/lit8 v1, v1, 0x18

    #@29
    iget v2, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@2b
    and-int/2addr v2, v11

    #@2c
    or-int/2addr v1, v2

    #@2d
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setColor(I)V

    #@30
    .line 268
    int-to-float v1, v8

    #@31
    int-to-float v2, v10

    #@32
    int-to-float v3, v9

    #@33
    int-to-float v4, v6

    #@34
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@36
    move-object v0, p1

    #@37
    invoke-virtual/range {v0 .. v5}, Landroid/graphics/Canvas;->drawRect(FFFFLandroid/graphics/Paint;)V

    #@3a
    .line 270
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@3c
    if-eqz v0, :cond_0

    #@3e
    .line 271
    iget-object v0, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@40
    const/high16 v1, -0x1000000

    #@42
    iget v2, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@44
    and-int/2addr v2, v11

    #@45
    or-int/2addr v1, v2

    #@46
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setColor(I)V

    #@49
    .line 272
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingLeft()I

    #@4c
    move-result v0

    #@4d
    int-to-float v1, v0

    #@4e
    iget v0, p0, Landroid/support/v4/view/PagerTabStrip;->mFullUnderlineHeight:I

    #@50
    sub-int v0, v7, v0

    #@52
    int-to-float v2, v0

    #@53
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getWidth()I

    #@56
    move-result v0

    #@57
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getPaddingRight()I

    #@5a
    move-result v3

    #@5b
    sub-int/2addr v0, v3

    #@5c
    int-to-float v3, v0

    #@5d
    int-to-float v4, v7

    #@5e
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@60
    move-object v0, p1

    #@61
    invoke-virtual/range {v0 .. v5}, Landroid/graphics/Canvas;->drawRect(FFFFLandroid/graphics/Paint;)V

    #@64
    .line 275
    :cond_0
    return-void
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 6
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 222
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    #@5
    move-result v0

    #@6
    .line 223
    .local v0, "action":I
    if-eqz v0, :cond_0

    #@8
    iget-boolean v5, p0, Landroid/support/v4/view/PagerTabStrip;->mIgnoreTap:Z

    #@a
    if-eqz v5, :cond_0

    #@c
    .line 254
    :goto_0
    return v3

    #@d
    .line 229
    :cond_0
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@10
    move-result v1

    #@11
    .line 230
    .local v1, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@14
    move-result v2

    #@15
    .line 231
    .local v2, "y":F
    packed-switch v0, :pswitch_data_0

    #@18
    :cond_1
    :goto_1
    move v3, v4

    #@19
    .line 254
    goto :goto_0

    #@1a
    .line 233
    :pswitch_0
    iput v1, p0, Landroid/support/v4/view/PagerTabStrip;->mInitialMotionX:F

    #@1c
    .line 234
    iput v2, p0, Landroid/support/v4/view/PagerTabStrip;->mInitialMotionY:F

    #@1e
    .line 235
    iput-boolean v3, p0, Landroid/support/v4/view/PagerTabStrip;->mIgnoreTap:Z

    #@20
    goto :goto_1

    #@21
    .line 239
    :pswitch_1
    iget v3, p0, Landroid/support/v4/view/PagerTabStrip;->mInitialMotionX:F

    #@23
    sub-float v3, v1, v3

    #@25
    invoke-static {v3}, Ljava/lang/Math;->abs(F)F

    #@28
    move-result v3

    #@29
    iget v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTouchSlop:I

    #@2b
    int-to-float v5, v5

    #@2c
    cmpl-float v3, v3, v5

    #@2e
    if-gtz v3, :cond_2

    #@30
    iget v3, p0, Landroid/support/v4/view/PagerTabStrip;->mInitialMotionY:F

    #@32
    sub-float v3, v2, v3

    #@34
    invoke-static {v3}, Ljava/lang/Math;->abs(F)F

    #@37
    move-result v3

    #@38
    iget v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTouchSlop:I

    #@3a
    int-to-float v5, v5

    #@3b
    cmpl-float v3, v3, v5

    #@3d
    if-lez v3, :cond_1

    #@3f
    .line 241
    :cond_2
    iput-boolean v4, p0, Landroid/support/v4/view/PagerTabStrip;->mIgnoreTap:Z

    #@41
    goto :goto_1

    #@42
    .line 246
    :pswitch_2
    iget-object v3, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@44
    invoke-virtual {v3}, Landroid/widget/TextView;->getLeft()I

    #@47
    move-result v3

    #@48
    iget v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@4a
    sub-int/2addr v3, v5

    #@4b
    int-to-float v3, v3

    #@4c
    cmpg-float v3, v1, v3

    #@4e
    if-gez v3, :cond_3

    #@50
    .line 247
    iget-object v3, p0, Landroid/support/v4/view/PagerTabStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@52
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@54
    invoke-virtual {v5}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@57
    move-result v5

    #@58
    add-int/lit8 v5, v5, -0x1

    #@5a
    invoke-virtual {v3, v5}, Landroid/support/v4/view/ViewPager;->setCurrentItem(I)V

    #@5d
    goto :goto_1

    #@5e
    .line 248
    :cond_3
    iget-object v3, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@60
    invoke-virtual {v3}, Landroid/widget/TextView;->getRight()I

    #@63
    move-result v3

    #@64
    iget v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@66
    add-int/2addr v3, v5

    #@67
    int-to-float v3, v3

    #@68
    cmpl-float v3, v1, v3

    #@6a
    if-lez v3, :cond_1

    #@6c
    .line 249
    iget-object v3, p0, Landroid/support/v4/view/PagerTabStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@6e
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@70
    invoke-virtual {v5}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@73
    move-result v5

    #@74
    add-int/lit8 v5, v5, 0x1

    #@76
    invoke-virtual {v3, v5}, Landroid/support/v4/view/ViewPager;->setCurrentItem(I)V

    #@79
    goto :goto_1

    #@7a
    .line 231
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_2
        :pswitch_1
    .end packed-switch
.end method

.method public setBackgroundColor(I)V
    .locals 1
    .param p1, "color"    # I

    #@0
    .prologue
    .line 178
    invoke-super {p0, p1}, Landroid/support/v4/view/PagerTitleStrip;->setBackgroundColor(I)V

    #@3
    .line 179
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderlineSet:Z

    #@5
    if-nez v0, :cond_0

    #@7
    .line 180
    const/high16 v0, -0x1000000

    #@9
    and-int/2addr v0, p1

    #@a
    if-nez v0, :cond_1

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@f
    .line 182
    :cond_0
    return-void

    #@10
    .line 180
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 170
    invoke-super {p0, p1}, Landroid/support/v4/view/PagerTitleStrip;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@3
    .line 171
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderlineSet:Z

    #@5
    if-nez v0, :cond_0

    #@7
    .line 172
    if-nez p1, :cond_1

    #@9
    const/4 v0, 0x1

    #@a
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@c
    .line 174
    :cond_0
    return-void

    #@d
    .line 172
    :cond_1
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public setBackgroundResource(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 186
    invoke-super {p0, p1}, Landroid/support/v4/view/PagerTitleStrip;->setBackgroundResource(I)V

    #@3
    .line 187
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderlineSet:Z

    #@5
    if-nez v0, :cond_0

    #@7
    .line 188
    if-nez p1, :cond_1

    #@9
    const/4 v0, 0x1

    #@a
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@c
    .line 190
    :cond_0
    return-void

    #@d
    .line 188
    :cond_1
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public setDrawFullUnderline(Z)V
    .locals 1
    .param p1, "drawFull"    # Z

    #@0
    .prologue
    .line 199
    iput-boolean p1, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderline:Z

    #@2
    .line 200
    const/4 v0, 0x1

    #@3
    iput-boolean v0, p0, Landroid/support/v4/view/PagerTabStrip;->mDrawFullUnderlineSet:Z

    #@5
    .line 201
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->invalidate()V

    #@8
    .line 202
    return-void
.end method

.method public setPadding(IIII)V
    .locals 1
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 154
    iget v0, p0, Landroid/support/v4/view/PagerTabStrip;->mMinPaddingBottom:I

    #@2
    if-ge p4, v0, :cond_0

    #@4
    .line 155
    iget p4, p0, Landroid/support/v4/view/PagerTabStrip;->mMinPaddingBottom:I

    #@6
    .line 157
    :cond_0
    invoke-super {p0, p1, p2, p3, p4}, Landroid/support/v4/view/PagerTitleStrip;->setPadding(IIII)V

    #@9
    .line 158
    return-void
.end method

.method public setTabIndicatorColor(I)V
    .locals 2
    .param p1, "color"    # I

    #@0
    .prologue
    .line 131
    iput p1, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@2
    .line 132
    iget-object v0, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPaint:Landroid/graphics/Paint;

    #@4
    iget v1, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorColor:I

    #@6
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setColor(I)V

    #@9
    .line 133
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->invalidate()V

    #@c
    .line 134
    return-void
.end method

.method public setTabIndicatorColorResource(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 142
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getColor(I)I

    #@b
    move-result v0

    #@c
    invoke-virtual {p0, v0}, Landroid/support/v4/view/PagerTabStrip;->setTabIndicatorColor(I)V

    #@f
    .line 143
    return-void
.end method

.method public setTextSpacing(I)V
    .locals 1
    .param p1, "textSpacing"    # I

    #@0
    .prologue
    .line 162
    iget v0, p0, Landroid/support/v4/view/PagerTabStrip;->mMinTextSpacing:I

    #@2
    if-ge p1, v0, :cond_0

    #@4
    .line 163
    iget p1, p0, Landroid/support/v4/view/PagerTabStrip;->mMinTextSpacing:I

    #@6
    .line 165
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v4/view/PagerTitleStrip;->setTextSpacing(I)V

    #@9
    .line 166
    return-void
.end method

.method updateTextPositions(IFZ)V
    .locals 7
    .param p1, "position"    # I
    .param p2, "positionOffset"    # F
    .param p3, "force"    # Z

    #@0
    .prologue
    .line 279
    iget-object v2, p0, Landroid/support/v4/view/PagerTabStrip;->mTempRect:Landroid/graphics/Rect;

    #@2
    .line 280
    .local v2, "r":Landroid/graphics/Rect;
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTabStrip;->getHeight()I

    #@5
    move-result v0

    #@6
    .line 281
    .local v0, "bottom":I
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@8
    invoke-virtual {v5}, Landroid/widget/TextView;->getLeft()I

    #@b
    move-result v5

    #@c
    iget v6, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@e
    sub-int v1, v5, v6

    #@10
    .line 282
    .local v1, "left":I
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@12
    invoke-virtual {v5}, Landroid/widget/TextView;->getRight()I

    #@15
    move-result v5

    #@16
    iget v6, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@18
    add-int v3, v5, v6

    #@1a
    .line 283
    .local v3, "right":I
    iget v5, p0, Landroid/support/v4/view/PagerTabStrip;->mIndicatorHeight:I

    #@1c
    sub-int v4, v0, v5

    #@1e
    .line 285
    .local v4, "top":I
    invoke-virtual {v2, v1, v4, v3, v0}, Landroid/graphics/Rect;->set(IIII)V

    #@21
    .line 287
    invoke-super {p0, p1, p2, p3}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@24
    .line 288
    const/high16 v5, 0x3f000000    # 0.5f

    #@26
    sub-float v5, p2, v5

    #@28
    invoke-static {v5}, Ljava/lang/Math;->abs(F)F

    #@2b
    move-result v5

    #@2c
    const/high16 v6, 0x40000000    # 2.0f

    #@2e
    mul-float/2addr v5, v6

    #@2f
    const/high16 v6, 0x437f0000    # 255.0f

    #@31
    mul-float/2addr v5, v6

    #@32
    float-to-int v5, v5

    #@33
    iput v5, p0, Landroid/support/v4/view/PagerTabStrip;->mTabAlpha:I

    #@35
    .line 290
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@37
    invoke-virtual {v5}, Landroid/widget/TextView;->getLeft()I

    #@3a
    move-result v5

    #@3b
    iget v6, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@3d
    sub-int v1, v5, v6

    #@3f
    .line 291
    iget-object v5, p0, Landroid/support/v4/view/PagerTabStrip;->mCurrText:Landroid/widget/TextView;

    #@41
    invoke-virtual {v5}, Landroid/widget/TextView;->getRight()I

    #@44
    move-result v5

    #@45
    iget v6, p0, Landroid/support/v4/view/PagerTabStrip;->mTabPadding:I

    #@47
    add-int v3, v5, v6

    #@49
    .line 292
    invoke-virtual {v2, v1, v4, v3, v0}, Landroid/graphics/Rect;->union(IIII)V

    #@4c
    .line 294
    invoke-virtual {p0, v2}, Landroid/support/v4/view/PagerTabStrip;->invalidate(Landroid/graphics/Rect;)V

    #@4f
    .line 295
    return-void
.end method
