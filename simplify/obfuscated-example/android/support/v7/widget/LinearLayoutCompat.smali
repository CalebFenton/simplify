.class public Landroid/support/v7/widget/LinearLayoutCompat;
.super Landroid/view/ViewGroup;
.source "LinearLayoutCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;,
        Landroid/support/v7/widget/LinearLayoutCompat$DividerMode;,
        Landroid/support/v7/widget/LinearLayoutCompat$OrientationMode;
    }
.end annotation


# static fields
.field public static final HORIZONTAL:I = 0x0

.field private static final INDEX_BOTTOM:I = 0x2

.field private static final INDEX_CENTER_VERTICAL:I = 0x0

.field private static final INDEX_FILL:I = 0x3

.field private static final INDEX_TOP:I = 0x1

.field public static final SHOW_DIVIDER_BEGINNING:I = 0x1

.field public static final SHOW_DIVIDER_END:I = 0x4

.field public static final SHOW_DIVIDER_MIDDLE:I = 0x2

.field public static final SHOW_DIVIDER_NONE:I = 0x0

.field public static final VERTICAL:I = 0x1

.field private static final VERTICAL_GRAVITY_COUNT:I = 0x4


# instance fields
.field private mBaselineAligned:Z

.field private mBaselineAlignedChildIndex:I

.field private mBaselineChildTop:I

.field private mDivider:Landroid/graphics/drawable/Drawable;

.field private mDividerHeight:I

.field private mDividerPadding:I

.field private mDividerWidth:I

.field private mGravity:I

.field private mMaxAscent:[I

.field private mMaxDescent:[I

.field private mOrientation:I

.field private mShowDividers:I

.field private mTotalLength:I

.field private mUseLargestChild:Z

.field private mWeightSum:F


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 142
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 143
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 146
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 147
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 7
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v6, -0x1

    #@2
    const/4 v5, 0x0

    #@3
    .line 150
    invoke-direct {p0, p1, p2, p3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 97
    iput-boolean v4, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAligned:Z

    #@8
    .line 106
    iput v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@a
    .line 113
    iput v5, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineChildTop:I

    #@c
    .line 117
    const v3, 0x800033

    #@f
    iput v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@11
    .line 152
    sget-object v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat:[I

    #@13
    invoke-static {p1, p2, v3, p3, v5}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@16
    move-result-object v0

    #@17
    .line 155
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_android_orientation:I

    #@19
    invoke-virtual {v0, v3, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@1c
    move-result v2

    #@1d
    .line 156
    .local v2, "index":I
    if-ltz v2, :cond_0

    #@1f
    .line 157
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->setOrientation(I)V

    #@22
    .line 160
    :cond_0
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_android_gravity:I

    #@24
    invoke-virtual {v0, v3, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@27
    move-result v2

    #@28
    .line 161
    if-ltz v2, :cond_1

    #@2a
    .line 162
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->setGravity(I)V

    #@2d
    .line 165
    :cond_1
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_android_baselineAligned:I

    #@2f
    invoke-virtual {v0, v3, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@32
    move-result v1

    #@33
    .line 166
    .local v1, "baselineAligned":Z
    if-nez v1, :cond_2

    #@35
    .line 167
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->setBaselineAligned(Z)V

    #@38
    .line 170
    :cond_2
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_android_weightSum:I

    #@3a
    const/high16 v4, -0x40800000    # -1.0f

    #@3c
    invoke-virtual {v0, v3, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getFloat(IF)F

    #@3f
    move-result v3

    #@40
    iput v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@42
    .line 172
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_android_baselineAlignedChildIndex:I

    #@44
    invoke-virtual {v0, v3, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@47
    move-result v3

    #@48
    iput v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@4a
    .line 175
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_measureWithLargestChild:I

    #@4c
    invoke-virtual {v0, v3, v5}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@4f
    move-result v3

    #@50
    iput-boolean v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mUseLargestChild:Z

    #@52
    .line 177
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_divider:I

    #@54
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@57
    move-result-object v3

    #@58
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/LinearLayoutCompat;->setDividerDrawable(Landroid/graphics/drawable/Drawable;)V

    #@5b
    .line 178
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_showDividers:I

    #@5d
    invoke-virtual {v0, v3, v5}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@60
    move-result v3

    #@61
    iput v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@63
    .line 179
    sget v3, Landroid/support/v7/appcompat/R$styleable;->LinearLayoutCompat_dividerPadding:I

    #@65
    invoke-virtual {v0, v3, v5}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@68
    move-result v3

    #@69
    iput v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@6b
    .line 181
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@6e
    .line 182
    return-void
.end method

.method private forceUniformHeight(II)V
    .locals 9
    .param p1, "count"    # I
    .param p2, "widthMeasureSpec"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1313
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getMeasuredHeight()I

    #@4
    move-result v0

    #@5
    const/high16 v2, 0x40000000    # 2.0f

    #@7
    invoke-static {v0, v2}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@a
    move-result v4

    #@b
    .line 1315
    .local v4, "uniformMeasureSpec":I
    const/4 v6, 0x0

    #@c
    .local v6, "i":I
    :goto_0
    if-ge v6, p1, :cond_1

    #@e
    .line 1316
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@11
    move-result-object v1

    #@12
    .line 1317
    .local v1, "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    #@15
    move-result v0

    #@16
    const/16 v2, 0x8

    #@18
    if-eq v0, v2, :cond_0

    #@1a
    .line 1318
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1d
    move-result-object v7

    #@1e
    check-cast v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@20
    .line 1320
    .local v7, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iget v0, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@22
    const/4 v2, -0x1

    #@23
    if-ne v0, v2, :cond_0

    #@25
    .line 1323
    iget v8, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@27
    .line 1324
    .local v8, "oldWidth":I
    invoke-virtual {v1}, Landroid/view/View;->getMeasuredWidth()I

    #@2a
    move-result v0

    #@2b
    iput v0, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@2d
    move-object v0, p0

    #@2e
    move v2, p2

    #@2f
    move v5, v3

    #@30
    .line 1327
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@33
    .line 1328
    iput v8, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@35
    .line 1315
    .end local v7    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v8    # "oldWidth":I
    :cond_0
    add-int/lit8 v6, v6, 0x1

    #@37
    goto :goto_0

    #@38
    .line 1332
    .end local v1    # "child":Landroid/view/View;
    :cond_1
    return-void
.end method

.method private forceUniformWidth(II)V
    .locals 9
    .param p1, "count"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 890
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getMeasuredWidth()I

    #@4
    move-result v0

    #@5
    const/high16 v4, 0x40000000    # 2.0f

    #@7
    invoke-static {v0, v4}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@a
    move-result v2

    #@b
    .line 892
    .local v2, "uniformMeasureSpec":I
    const/4 v6, 0x0

    #@c
    .local v6, "i":I
    :goto_0
    if-ge v6, p1, :cond_1

    #@e
    .line 893
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@11
    move-result-object v1

    #@12
    .line 894
    .local v1, "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    #@15
    move-result v0

    #@16
    const/16 v4, 0x8

    #@18
    if-eq v0, v4, :cond_0

    #@1a
    .line 895
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1d
    move-result-object v7

    #@1e
    check-cast v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@20
    .line 897
    .local v7, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iget v0, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@22
    const/4 v4, -0x1

    #@23
    if-ne v0, v4, :cond_0

    #@25
    .line 900
    iget v8, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@27
    .line 901
    .local v8, "oldHeight":I
    invoke-virtual {v1}, Landroid/view/View;->getMeasuredHeight()I

    #@2a
    move-result v0

    #@2b
    iput v0, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@2d
    move-object v0, p0

    #@2e
    move v4, p2

    #@2f
    move v5, v3

    #@30
    .line 904
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@33
    .line 905
    iput v8, v7, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@35
    .line 892
    .end local v7    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v8    # "oldHeight":I
    :cond_0
    add-int/lit8 v6, v6, 0x1

    #@37
    goto :goto_0

    #@38
    .line 909
    .end local v1    # "child":Landroid/view/View;
    :cond_1
    return-void
.end method

.method private setChildFrame(Landroid/view/View;IIII)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "width"    # I
    .param p5, "height"    # I

    #@0
    .prologue
    .line 1641
    add-int v0, p2, p4

    #@2
    add-int v1, p3, p5

    #@4
    invoke-virtual {p1, p2, p3, v0, v1}, Landroid/view/View;->layout(IIII)V

    #@7
    .line 1642
    return-void
.end method


# virtual methods
.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1737
    instance-of v0, p1, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@2
    return v0
.end method

.method drawDividersHorizontal(Landroid/graphics/Canvas;)V
    .locals 8
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 318
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@3
    move-result v1

    #@4
    .line 319
    .local v1, "count":I
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@7
    move-result v3

    #@8
    .line 320
    .local v3, "isLayoutRtl":Z
    const/4 v2, 0x0

    #@9
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_2

    #@b
    .line 321
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    .line 323
    .local v0, "child":Landroid/view/View;
    if-eqz v0, :cond_0

    #@11
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@14
    move-result v6

    #@15
    const/16 v7, 0x8

    #@17
    if-eq v6, v7, :cond_0

    #@19
    .line 324
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@1c
    move-result v6

    #@1d
    if-eqz v6, :cond_0

    #@1f
    .line 325
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@22
    move-result-object v4

    #@23
    check-cast v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@25
    .line 327
    .local v4, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    if-eqz v3, :cond_1

    #@27
    .line 328
    invoke-virtual {v0}, Landroid/view/View;->getRight()I

    #@2a
    move-result v6

    #@2b
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@2d
    add-int v5, v6, v7

    #@2f
    .line 332
    .local v5, "position":I
    :goto_1
    invoke-virtual {p0, p1, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->drawVerticalDivider(Landroid/graphics/Canvas;I)V

    #@32
    .line 320
    .end local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v5    # "position":I
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@34
    goto :goto_0

    #@35
    .line 330
    .restart local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    :cond_1
    invoke-virtual {v0}, Landroid/view/View;->getLeft()I

    #@38
    move-result v6

    #@39
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@3b
    sub-int/2addr v6, v7

    #@3c
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@3e
    sub-int v5, v6, v7

    #@40
    .restart local v5    # "position":I
    goto :goto_1

    #@41
    .line 337
    .end local v0    # "child":Landroid/view/View;
    .end local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v5    # "position":I
    :cond_2
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@44
    move-result v6

    #@45
    if-eqz v6, :cond_3

    #@47
    .line 338
    add-int/lit8 v6, v1, -0x1

    #@49
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@4c
    move-result-object v0

    #@4d
    .line 340
    .restart local v0    # "child":Landroid/view/View;
    if-nez v0, :cond_5

    #@4f
    .line 341
    if-eqz v3, :cond_4

    #@51
    .line 342
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@54
    move-result v5

    #@55
    .line 354
    .restart local v5    # "position":I
    :goto_2
    invoke-virtual {p0, p1, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->drawVerticalDivider(Landroid/graphics/Canvas;I)V

    #@58
    .line 356
    .end local v0    # "child":Landroid/view/View;
    .end local v5    # "position":I
    :cond_3
    return-void

    #@59
    .line 344
    .restart local v0    # "child":Landroid/view/View;
    :cond_4
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getWidth()I

    #@5c
    move-result v6

    #@5d
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@60
    move-result v7

    #@61
    sub-int/2addr v6, v7

    #@62
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@64
    sub-int v5, v6, v7

    #@66
    .restart local v5    # "position":I
    goto :goto_2

    #@67
    .line 347
    .end local v5    # "position":I
    :cond_5
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@6a
    move-result-object v4

    #@6b
    check-cast v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@6d
    .line 348
    .restart local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    if-eqz v3, :cond_6

    #@6f
    .line 349
    invoke-virtual {v0}, Landroid/view/View;->getLeft()I

    #@72
    move-result v6

    #@73
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@75
    sub-int/2addr v6, v7

    #@76
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@78
    sub-int v5, v6, v7

    #@7a
    .restart local v5    # "position":I
    goto :goto_2

    #@7b
    .line 351
    .end local v5    # "position":I
    :cond_6
    invoke-virtual {v0}, Landroid/view/View;->getRight()I

    #@7e
    move-result v6

    #@7f
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@81
    add-int v5, v6, v7

    #@83
    .restart local v5    # "position":I
    goto :goto_2
.end method

.method drawDividersVertical(Landroid/graphics/Canvas;)V
    .locals 8
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 291
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@3
    move-result v2

    #@4
    .line 292
    .local v2, "count":I
    const/4 v3, 0x0

    #@5
    .local v3, "i":I
    :goto_0
    if-ge v3, v2, :cond_1

    #@7
    .line 293
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@a
    move-result-object v1

    #@b
    .line 295
    .local v1, "child":Landroid/view/View;
    if-eqz v1, :cond_0

    #@d
    invoke-virtual {v1}, Landroid/view/View;->getVisibility()I

    #@10
    move-result v6

    #@11
    const/16 v7, 0x8

    #@13
    if-eq v6, v7, :cond_0

    #@15
    .line 296
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@18
    move-result v6

    #@19
    if-eqz v6, :cond_0

    #@1b
    .line 297
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1e
    move-result-object v4

    #@1f
    check-cast v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@21
    .line 298
    .local v4, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@24
    move-result v6

    #@25
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@27
    sub-int/2addr v6, v7

    #@28
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@2a
    sub-int v5, v6, v7

    #@2c
    .line 299
    .local v5, "top":I
    invoke-virtual {p0, p1, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->drawHorizontalDivider(Landroid/graphics/Canvas;I)V

    #@2f
    .line 292
    .end local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v5    # "top":I
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@31
    goto :goto_0

    #@32
    .line 304
    .end local v1    # "child":Landroid/view/View;
    :cond_1
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@35
    move-result v6

    #@36
    if-eqz v6, :cond_2

    #@38
    .line 305
    add-int/lit8 v6, v2, -0x1

    #@3a
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@3d
    move-result-object v1

    #@3e
    .line 306
    .restart local v1    # "child":Landroid/view/View;
    const/4 v0, 0x0

    #@3f
    .line 307
    .local v0, "bottom":I
    if-nez v1, :cond_3

    #@41
    .line 308
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getHeight()I

    #@44
    move-result v6

    #@45
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@48
    move-result v7

    #@49
    sub-int/2addr v6, v7

    #@4a
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@4c
    sub-int v0, v6, v7

    #@4e
    .line 313
    :goto_1
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/widget/LinearLayoutCompat;->drawHorizontalDivider(Landroid/graphics/Canvas;I)V

    #@51
    .line 315
    .end local v0    # "bottom":I
    .end local v1    # "child":Landroid/view/View;
    :cond_2
    return-void

    #@52
    .line 310
    .restart local v0    # "bottom":I
    .restart local v1    # "child":Landroid/view/View;
    :cond_3
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@55
    move-result-object v4

    #@56
    check-cast v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@58
    .line 311
    .restart local v4    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    invoke-virtual {v1}, Landroid/view/View;->getBottom()I

    #@5b
    move-result v6

    #@5c
    iget v7, v4, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@5e
    add-int v0, v6, v7

    #@60
    goto :goto_1
.end method

.method drawHorizontalDivider(Landroid/graphics/Canvas;I)V
    .locals 4
    .param p1, "canvas"    # Landroid/graphics/Canvas;
    .param p2, "top"    # I

    #@0
    .prologue
    .line 359
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@5
    move-result v1

    #@6
    iget v2, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@8
    add-int/2addr v1, v2

    #@9
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getWidth()I

    #@c
    move-result v2

    #@d
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@10
    move-result v3

    #@11
    sub-int/2addr v2, v3

    #@12
    iget v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@14
    sub-int/2addr v2, v3

    #@15
    iget v3, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@17
    add-int/2addr v3, p2

    #@18
    invoke-virtual {v0, v1, p2, v2, v3}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@1b
    .line 361
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@1d
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@20
    .line 362
    return-void
.end method

.method drawVerticalDivider(Landroid/graphics/Canvas;I)V
    .locals 5
    .param p1, "canvas"    # Landroid/graphics/Canvas;
    .param p2, "left"    # I

    #@0
    .prologue
    .line 365
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@5
    move-result v1

    #@6
    iget v2, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@8
    add-int/2addr v1, v2

    #@9
    iget v2, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@b
    add-int/2addr v2, p2

    #@c
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getHeight()I

    #@f
    move-result v3

    #@10
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@13
    move-result v4

    #@14
    sub-int/2addr v3, v4

    #@15
    iget v4, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@17
    sub-int/2addr v3, v4

    #@18
    invoke-virtual {v0, p2, v1, v2, v3}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@1b
    .line 367
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@1d
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@20
    .line 368
    return-void
.end method

.method protected generateDefaultLayoutParams()Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 3

    #@0
    .prologue
    const/4 v2, -0x2

    #@1
    .line 1720
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@3
    if-nez v0, :cond_0

    #@5
    .line 1721
    new-instance v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@7
    invoke-direct {v0, v2, v2}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@a
    .line 1725
    :goto_0
    return-object v0

    #@b
    .line 1722
    :cond_0
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@d
    const/4 v1, 0x1

    #@e
    if-ne v0, v1, :cond_1

    #@10
    .line 1723
    new-instance v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@12
    const/4 v1, -0x1

    #@13
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@16
    goto :goto_0

    #@17
    .line 1725
    :cond_1
    const/4 v0, 0x0

    #@18
    goto :goto_0
.end method

.method protected bridge synthetic generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 56
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->generateDefaultLayoutParams()Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1707
    new-instance v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1730
    new-instance v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@2
    invoke-direct {v0, p1}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@5
    return-object v0
.end method

.method public bridge synthetic generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 56
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method protected bridge synthetic generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 56
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getBaseline()I
    .locals 8

    #@0
    .prologue
    const/4 v5, -0x1

    #@1
    .line 419
    iget v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@3
    if-gez v6, :cond_1

    #@5
    .line 420
    invoke-super {p0}, Landroid/view/ViewGroup;->getBaseline()I

    #@8
    move-result v5

    #@9
    .line 467
    :cond_0
    :goto_0
    return v5

    #@a
    .line 423
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@d
    move-result v6

    #@e
    iget v7, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@10
    if-gt v6, v7, :cond_2

    #@12
    .line 424
    new-instance v5, Ljava/lang/RuntimeException;

    #@14
    const-string v6, "mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds."

    #@16
    invoke-direct {v5, v6}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@19
    throw v5

    #@1a
    .line 428
    :cond_2
    iget v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@1c
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@1f
    move-result-object v0

    #@20
    .line 429
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getBaseline()I

    #@23
    move-result v1

    #@24
    .line 431
    .local v1, "childBaseline":I
    if-ne v1, v5, :cond_3

    #@26
    .line 432
    iget v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@28
    if-eqz v6, :cond_0

    #@2a
    .line 438
    new-instance v5, Ljava/lang/RuntimeException;

    #@2c
    const-string v6, "mBaselineAlignedChildIndex of LinearLayout points to a View that doesn\'t know how to get its baseline."

    #@2e
    invoke-direct {v5, v6}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@31
    throw v5

    #@32
    .line 448
    :cond_3
    iget v2, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineChildTop:I

    #@34
    .line 450
    .local v2, "childTop":I
    iget v5, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@36
    const/4 v6, 0x1

    #@37
    if-ne v5, v6, :cond_4

    #@39
    .line 451
    iget v5, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@3b
    and-int/lit8 v4, v5, 0x70

    #@3d
    .line 452
    .local v4, "majorGravity":I
    const/16 v5, 0x30

    #@3f
    if-eq v4, v5, :cond_4

    #@41
    .line 453
    sparse-switch v4, :sswitch_data_0

    #@44
    .line 466
    .end local v4    # "majorGravity":I
    :cond_4
    :goto_1
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@47
    move-result-object v3

    #@48
    check-cast v3, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@4a
    .line 467
    .local v3, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iget v5, v3, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@4c
    add-int/2addr v5, v2

    #@4d
    add-int/2addr v5, v1

    #@4e
    goto :goto_0

    #@4f
    .line 455
    .end local v3    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .restart local v4    # "majorGravity":I
    :sswitch_0
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getBottom()I

    #@52
    move-result v5

    #@53
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getTop()I

    #@56
    move-result v6

    #@57
    sub-int/2addr v5, v6

    #@58
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@5b
    move-result v6

    #@5c
    sub-int/2addr v5, v6

    #@5d
    iget v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@5f
    sub-int v2, v5, v6

    #@61
    .line 456
    goto :goto_1

    #@62
    .line 459
    :sswitch_1
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getBottom()I

    #@65
    move-result v5

    #@66
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getTop()I

    #@69
    move-result v6

    #@6a
    sub-int/2addr v5, v6

    #@6b
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@6e
    move-result v6

    #@6f
    sub-int/2addr v5, v6

    #@70
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@73
    move-result v6

    #@74
    sub-int/2addr v5, v6

    #@75
    iget v6, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@77
    sub-int/2addr v5, v6

    #@78
    div-int/lit8 v5, v5, 0x2

    #@7a
    add-int/2addr v2, v5

    #@7b
    goto :goto_1

    #@7c
    .line 453
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_1
        0x50 -> :sswitch_0
    .end sparse-switch
.end method

.method public getBaselineAlignedChildIndex()I
    .locals 1

    #@0
    .prologue
    .line 476
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@2
    return v0
.end method

.method getChildrenSkipCount(Landroid/view/View;I)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "index"    # I

    #@0
    .prologue
    .line 1343
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getDividerDrawable()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 218
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public getDividerPadding()I
    .locals 1

    #@0
    .prologue
    .line 265
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@2
    return v0
.end method

.method public getDividerWidth()I
    .locals 1

    #@0
    .prologue
    .line 274
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@2
    return v0
.end method

.method getLocationOffset(Landroid/view/View;)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1385
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method getNextLocationOffset(Landroid/view/View;)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1397
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getOrientation()I
    .locals 1

    #@0
    .prologue
    .line 1663
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@2
    return v0
.end method

.method public getShowDividers()I
    .locals 1

    #@0
    .prologue
    .line 209
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@2
    return v0
.end method

.method getVirtualChildAt(I)Landroid/view/View;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 501
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method getVirtualChildCount()I
    .locals 1

    #@0
    .prologue
    .line 514
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getWeightSum()F
    .locals 1

    #@0
    .prologue
    .line 525
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@2
    return v0
.end method

.method protected hasDividerBeforeChildAt(I)Z
    .locals 5
    .param p1, "childIndex"    # I

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 561
    if-nez p1, :cond_2

    #@4
    .line 562
    iget v4, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@6
    and-int/lit8 v4, v4, 0x1

    #@8
    if-eqz v4, :cond_1

    #@a
    .line 575
    :cond_0
    :goto_0
    return v2

    #@b
    :cond_1
    move v2, v3

    #@c
    .line 562
    goto :goto_0

    #@d
    .line 563
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@10
    move-result v4

    #@11
    if-ne p1, v4, :cond_3

    #@13
    .line 564
    iget v4, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@15
    and-int/lit8 v4, v4, 0x4

    #@17
    if-nez v4, :cond_0

    #@19
    move v2, v3

    #@1a
    goto :goto_0

    #@1b
    .line 565
    :cond_3
    iget v2, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@1d
    and-int/lit8 v2, v2, 0x2

    #@1f
    if-eqz v2, :cond_6

    #@21
    .line 566
    const/4 v0, 0x0

    #@22
    .line 567
    .local v0, "hasVisibleViewBefore":Z
    add-int/lit8 v1, p1, -0x1

    #@24
    .local v1, "i":I
    :goto_1
    if-ltz v1, :cond_4

    #@26
    .line 568
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@29
    move-result-object v2

    #@2a
    invoke-virtual {v2}, Landroid/view/View;->getVisibility()I

    #@2d
    move-result v2

    #@2e
    const/16 v3, 0x8

    #@30
    if-eq v2, v3, :cond_5

    #@32
    .line 569
    const/4 v0, 0x1

    #@33
    :cond_4
    move v2, v0

    #@34
    .line 573
    goto :goto_0

    #@35
    .line 567
    :cond_5
    add-int/lit8 v1, v1, -0x1

    #@37
    goto :goto_1

    #@38
    .end local v0    # "hasVisibleViewBefore":Z
    .end local v1    # "i":I
    :cond_6
    move v2, v3

    #@39
    .line 575
    goto :goto_0
.end method

.method public isBaselineAligned()Z
    .locals 1

    #@0
    .prologue
    .line 377
    iget-boolean v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAligned:Z

    #@2
    return v0
.end method

.method public isMeasureWithLargestChildEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 400
    iget-boolean v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mUseLargestChild:Z

    #@2
    return v0
.end method

.method layoutHorizontal(IIII)V
    .locals 30
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 1517
    invoke-static/range {p0 .. p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@3
    move-result v20

    #@4
    .line 1518
    .local v20, "isLayoutRtl":Z
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@7
    move-result v27

    #@8
    .line 1524
    .local v27, "paddingTop":I
    sub-int v18, p4, p2

    #@a
    .line 1525
    .local v18, "height":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@d
    move-result v2

    #@e
    sub-int v10, v18, v2

    #@10
    .line 1528
    .local v10, "childBottom":I
    sub-int v2, v18, v27

    #@12
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@15
    move-result v4

    #@16
    sub-int v13, v2, v4

    #@18
    .line 1530
    .local v13, "childSpace":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@1b
    move-result v14

    #@1c
    .line 1532
    .local v14, "count":I
    move-object/from16 v0, p0

    #@1e
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@20
    const v4, 0x800007

    #@23
    and-int v23, v2, v4

    #@25
    .line 1533
    .local v23, "majorGravity":I
    move-object/from16 v0, p0

    #@27
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@29
    and-int/lit8 v26, v2, 0x70

    #@2b
    .line 1535
    .local v26, "minorGravity":I
    move-object/from16 v0, p0

    #@2d
    iget-boolean v8, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAligned:Z

    #@2f
    .line 1537
    .local v8, "baselineAligned":Z
    move-object/from16 v0, p0

    #@31
    iget-object v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxAscent:[I

    #@33
    move-object/from16 v24, v0

    #@35
    .line 1538
    .local v24, "maxAscent":[I
    move-object/from16 v0, p0

    #@37
    iget-object v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxDescent:[I

    #@39
    move-object/from16 v25, v0

    #@3b
    .line 1540
    .local v25, "maxDescent":[I
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3e
    move-result v21

    #@3f
    .line 1541
    .local v21, "layoutDirection":I
    move/from16 v0, v23

    #@41
    move/from16 v1, v21

    #@43
    invoke-static {v0, v1}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@46
    move-result v2

    #@47
    sparse-switch v2, :sswitch_data_0

    #@4a
    .line 1554
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@4d
    move-result v12

    #@4e
    .line 1558
    .local v12, "childLeft":I
    :goto_0
    const/16 v28, 0x0

    #@50
    .line 1559
    .local v28, "start":I
    const/16 v16, 0x1

    #@52
    .line 1561
    .local v16, "dir":I
    if-eqz v20, :cond_0

    #@54
    .line 1562
    add-int/lit8 v28, v14, -0x1

    #@56
    .line 1563
    const/16 v16, -0x1

    #@58
    .line 1566
    :cond_0
    const/16 v19, 0x0

    #@5a
    .local v19, "i":I
    :goto_1
    move/from16 v0, v19

    #@5c
    if-ge v0, v14, :cond_7

    #@5e
    .line 1567
    mul-int v2, v16, v19

    #@60
    add-int v11, v28, v2

    #@62
    .line 1568
    .local v11, "childIndex":I
    move-object/from16 v0, p0

    #@64
    invoke-virtual {v0, v11}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@67
    move-result-object v3

    #@68
    .line 1570
    .local v3, "child":Landroid/view/View;
    if-nez v3, :cond_2

    #@6a
    .line 1571
    move-object/from16 v0, p0

    #@6c
    invoke-virtual {v0, v11}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@6f
    move-result v2

    #@70
    add-int/2addr v12, v2

    #@71
    .line 1566
    :cond_1
    :goto_2
    add-int/lit8 v19, v19, 0x1

    #@73
    goto :goto_1

    #@74
    .line 1544
    .end local v3    # "child":Landroid/view/View;
    .end local v11    # "childIndex":I
    .end local v12    # "childLeft":I
    .end local v16    # "dir":I
    .end local v19    # "i":I
    .end local v28    # "start":I
    :sswitch_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@77
    move-result v2

    #@78
    add-int v2, v2, p3

    #@7a
    sub-int v2, v2, p1

    #@7c
    move-object/from16 v0, p0

    #@7e
    iget v4, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@80
    sub-int v12, v2, v4

    #@82
    .line 1545
    .restart local v12    # "childLeft":I
    goto :goto_0

    #@83
    .line 1549
    .end local v12    # "childLeft":I
    :sswitch_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@86
    move-result v2

    #@87
    sub-int v4, p3, p1

    #@89
    move-object/from16 v0, p0

    #@8b
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@8d
    move/from16 v29, v0

    #@8f
    sub-int v4, v4, v29

    #@91
    div-int/lit8 v4, v4, 0x2

    #@93
    add-int v12, v2, v4

    #@95
    .line 1550
    .restart local v12    # "childLeft":I
    goto :goto_0

    #@96
    .line 1572
    .restart local v3    # "child":Landroid/view/View;
    .restart local v11    # "childIndex":I
    .restart local v16    # "dir":I
    .restart local v19    # "i":I
    .restart local v28    # "start":I
    :cond_2
    invoke-virtual {v3}, Landroid/view/View;->getVisibility()I

    #@99
    move-result v2

    #@9a
    const/16 v4, 0x8

    #@9c
    if-eq v2, v4, :cond_1

    #@9e
    .line 1573
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredWidth()I

    #@a1
    move-result v6

    #@a2
    .line 1574
    .local v6, "childWidth":I
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredHeight()I

    #@a5
    move-result v7

    #@a6
    .line 1575
    .local v7, "childHeight":I
    const/4 v9, -0x1

    #@a7
    .line 1577
    .local v9, "childBaseline":I
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@aa
    move-result-object v22

    #@ab
    check-cast v22, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@ad
    .line 1580
    .local v22, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    if-eqz v8, :cond_3

    #@af
    move-object/from16 v0, v22

    #@b1
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@b3
    const/4 v4, -0x1

    #@b4
    if-eq v2, v4, :cond_3

    #@b6
    .line 1581
    invoke-virtual {v3}, Landroid/view/View;->getBaseline()I

    #@b9
    move-result v9

    #@ba
    .line 1584
    :cond_3
    move-object/from16 v0, v22

    #@bc
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@be
    move/from16 v17, v0

    #@c0
    .line 1585
    .local v17, "gravity":I
    if-gez v17, :cond_4

    #@c2
    .line 1586
    move/from16 v17, v26

    #@c4
    .line 1589
    :cond_4
    and-int/lit8 v2, v17, 0x70

    #@c6
    sparse-switch v2, :sswitch_data_1

    #@c9
    .line 1621
    move/from16 v5, v27

    #@cb
    .line 1625
    .local v5, "childTop":I
    :cond_5
    :goto_3
    move-object/from16 v0, p0

    #@cd
    invoke-virtual {v0, v11}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@d0
    move-result v2

    #@d1
    if-eqz v2, :cond_6

    #@d3
    .line 1626
    move-object/from16 v0, p0

    #@d5
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@d7
    add-int/2addr v12, v2

    #@d8
    .line 1629
    :cond_6
    move-object/from16 v0, v22

    #@da
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@dc
    add-int/2addr v12, v2

    #@dd
    .line 1630
    move-object/from16 v0, p0

    #@df
    invoke-virtual {v0, v3}, Landroid/support/v7/widget/LinearLayoutCompat;->getLocationOffset(Landroid/view/View;)I

    #@e2
    move-result v2

    #@e3
    add-int v4, v12, v2

    #@e5
    move-object/from16 v2, p0

    #@e7
    invoke-direct/range {v2 .. v7}, Landroid/support/v7/widget/LinearLayoutCompat;->setChildFrame(Landroid/view/View;IIII)V

    #@ea
    .line 1632
    move-object/from16 v0, v22

    #@ec
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@ee
    add-int/2addr v2, v6

    #@ef
    move-object/from16 v0, p0

    #@f1
    invoke-virtual {v0, v3}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@f4
    move-result v4

    #@f5
    add-int/2addr v2, v4

    #@f6
    add-int/2addr v12, v2

    #@f7
    .line 1635
    move-object/from16 v0, p0

    #@f9
    invoke-virtual {v0, v3, v11}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@fc
    move-result v2

    #@fd
    add-int v19, v19, v2

    #@ff
    goto/16 :goto_2

    #@101
    .line 1591
    .end local v5    # "childTop":I
    :sswitch_2
    move-object/from16 v0, v22

    #@103
    iget v2, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@105
    add-int v5, v27, v2

    #@107
    .line 1592
    .restart local v5    # "childTop":I
    const/4 v2, -0x1

    #@108
    if-eq v9, v2, :cond_5

    #@10a
    .line 1593
    const/4 v2, 0x1

    #@10b
    aget v2, v24, v2

    #@10d
    sub-int/2addr v2, v9

    #@10e
    add-int/2addr v5, v2

    #@10f
    goto :goto_3

    #@110
    .line 1609
    .end local v5    # "childTop":I
    :sswitch_3
    sub-int v2, v13, v7

    #@112
    div-int/lit8 v2, v2, 0x2

    #@114
    add-int v2, v2, v27

    #@116
    move-object/from16 v0, v22

    #@118
    iget v4, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@11a
    add-int/2addr v2, v4

    #@11b
    move-object/from16 v0, v22

    #@11d
    iget v4, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@11f
    sub-int v5, v2, v4

    #@121
    .line 1611
    .restart local v5    # "childTop":I
    goto :goto_3

    #@122
    .line 1614
    .end local v5    # "childTop":I
    :sswitch_4
    sub-int v2, v10, v7

    #@124
    move-object/from16 v0, v22

    #@126
    iget v4, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@128
    sub-int v5, v2, v4

    #@12a
    .line 1615
    .restart local v5    # "childTop":I
    const/4 v2, -0x1

    #@12b
    if-eq v9, v2, :cond_5

    #@12d
    .line 1616
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredHeight()I

    #@130
    move-result v2

    #@131
    sub-int v15, v2, v9

    #@133
    .line 1617
    .local v15, "descent":I
    const/4 v2, 0x2

    #@134
    aget v2, v25, v2

    #@136
    sub-int/2addr v2, v15

    #@137
    sub-int/2addr v5, v2

    #@138
    .line 1618
    goto :goto_3

    #@139
    .line 1638
    .end local v3    # "child":Landroid/view/View;
    .end local v5    # "childTop":I
    .end local v6    # "childWidth":I
    .end local v7    # "childHeight":I
    .end local v9    # "childBaseline":I
    .end local v11    # "childIndex":I
    .end local v15    # "descent":I
    .end local v17    # "gravity":I
    .end local v22    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    :cond_7
    return-void

    #@13a
    .line 1541
    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_1
        0x5 -> :sswitch_0
    .end sparse-switch

    #@144
    .line 1589
    :sswitch_data_1
    .sparse-switch
        0x10 -> :sswitch_3
        0x30 -> :sswitch_2
        0x50 -> :sswitch_4
    .end sparse-switch
.end method

.method layoutVertical(IIII)V
    .locals 21
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 1422
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@3
    move-result v18

    #@4
    .line 1428
    .local v18, "paddingLeft":I
    sub-int v19, p3, p1

    #@6
    .line 1429
    .local v19, "width":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@9
    move-result v1

    #@a
    sub-int v8, v19, v1

    #@c
    .line 1432
    .local v8, "childRight":I
    sub-int v1, v19, v18

    #@e
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@11
    move-result v4

    #@12
    sub-int v9, v1, v4

    #@14
    .line 1434
    .local v9, "childSpace":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@17
    move-result v11

    #@18
    .line 1436
    .local v11, "count":I
    move-object/from16 v0, p0

    #@1a
    iget v1, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@1c
    and-int/lit8 v16, v1, 0x70

    #@1e
    .line 1437
    .local v16, "majorGravity":I
    move-object/from16 v0, p0

    #@20
    iget v1, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@22
    const v4, 0x800007

    #@25
    and-int v17, v1, v4

    #@27
    .line 1439
    .local v17, "minorGravity":I
    sparse-switch v16, :sswitch_data_0

    #@2a
    .line 1452
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@2d
    move-result v10

    #@2e
    .line 1456
    .local v10, "childTop":I
    :goto_0
    const/4 v13, 0x0

    #@2f
    .local v13, "i":I
    :goto_1
    if-ge v13, v11, :cond_4

    #@31
    .line 1457
    move-object/from16 v0, p0

    #@33
    invoke-virtual {v0, v13}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@36
    move-result-object v2

    #@37
    .line 1458
    .local v2, "child":Landroid/view/View;
    if-nez v2, :cond_1

    #@39
    .line 1459
    move-object/from16 v0, p0

    #@3b
    invoke-virtual {v0, v13}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@3e
    move-result v1

    #@3f
    add-int/2addr v10, v1

    #@40
    .line 1456
    :cond_0
    :goto_2
    add-int/lit8 v13, v13, 0x1

    #@42
    goto :goto_1

    #@43
    .line 1442
    .end local v2    # "child":Landroid/view/View;
    .end local v10    # "childTop":I
    .end local v13    # "i":I
    :sswitch_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@46
    move-result v1

    #@47
    add-int v1, v1, p4

    #@49
    sub-int v1, v1, p2

    #@4b
    move-object/from16 v0, p0

    #@4d
    iget v4, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@4f
    sub-int v10, v1, v4

    #@51
    .line 1443
    .restart local v10    # "childTop":I
    goto :goto_0

    #@52
    .line 1447
    .end local v10    # "childTop":I
    :sswitch_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@55
    move-result v1

    #@56
    sub-int v4, p4, p2

    #@58
    move-object/from16 v0, p0

    #@5a
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@5c
    move/from16 v20, v0

    #@5e
    sub-int v4, v4, v20

    #@60
    div-int/lit8 v4, v4, 0x2

    #@62
    add-int v10, v1, v4

    #@64
    .line 1448
    .restart local v10    # "childTop":I
    goto :goto_0

    #@65
    .line 1460
    .restart local v2    # "child":Landroid/view/View;
    .restart local v13    # "i":I
    :cond_1
    invoke-virtual {v2}, Landroid/view/View;->getVisibility()I

    #@68
    move-result v1

    #@69
    const/16 v4, 0x8

    #@6b
    if-eq v1, v4, :cond_0

    #@6d
    .line 1461
    invoke-virtual {v2}, Landroid/view/View;->getMeasuredWidth()I

    #@70
    move-result v5

    #@71
    .line 1462
    .local v5, "childWidth":I
    invoke-virtual {v2}, Landroid/view/View;->getMeasuredHeight()I

    #@74
    move-result v6

    #@75
    .line 1464
    .local v6, "childHeight":I
    invoke-virtual {v2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@78
    move-result-object v15

    #@79
    check-cast v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@7b
    .line 1467
    .local v15, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iget v12, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@7d
    .line 1468
    .local v12, "gravity":I
    if-gez v12, :cond_2

    #@7f
    .line 1469
    move/from16 v12, v17

    #@81
    .line 1471
    :cond_2
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@84
    move-result v14

    #@85
    .line 1472
    .local v14, "layoutDirection":I
    invoke-static {v12, v14}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@88
    move-result v7

    #@89
    .line 1474
    .local v7, "absoluteGravity":I
    and-int/lit8 v1, v7, 0x7

    #@8b
    sparse-switch v1, :sswitch_data_1

    #@8e
    .line 1486
    iget v1, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@90
    add-int v3, v18, v1

    #@92
    .line 1490
    .local v3, "childLeft":I
    :goto_3
    move-object/from16 v0, p0

    #@94
    invoke-virtual {v0, v13}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@97
    move-result v1

    #@98
    if-eqz v1, :cond_3

    #@9a
    .line 1491
    move-object/from16 v0, p0

    #@9c
    iget v1, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@9e
    add-int/2addr v10, v1

    #@9f
    .line 1494
    :cond_3
    iget v1, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@a1
    add-int/2addr v10, v1

    #@a2
    .line 1495
    move-object/from16 v0, p0

    #@a4
    invoke-virtual {v0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->getLocationOffset(Landroid/view/View;)I

    #@a7
    move-result v1

    #@a8
    add-int v4, v10, v1

    #@aa
    move-object/from16 v1, p0

    #@ac
    invoke-direct/range {v1 .. v6}, Landroid/support/v7/widget/LinearLayoutCompat;->setChildFrame(Landroid/view/View;IIII)V

    #@af
    .line 1497
    iget v1, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@b1
    add-int/2addr v1, v6

    #@b2
    move-object/from16 v0, p0

    #@b4
    invoke-virtual {v0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@b7
    move-result v4

    #@b8
    add-int/2addr v1, v4

    #@b9
    add-int/2addr v10, v1

    #@ba
    .line 1499
    move-object/from16 v0, p0

    #@bc
    invoke-virtual {v0, v2, v13}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@bf
    move-result v1

    #@c0
    add-int/2addr v13, v1

    #@c1
    goto/16 :goto_2

    #@c3
    .line 1476
    .end local v3    # "childLeft":I
    :sswitch_2
    sub-int v1, v9, v5

    #@c5
    div-int/lit8 v1, v1, 0x2

    #@c7
    add-int v1, v1, v18

    #@c9
    iget v4, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@cb
    add-int/2addr v1, v4

    #@cc
    iget v4, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@ce
    sub-int v3, v1, v4

    #@d0
    .line 1478
    .restart local v3    # "childLeft":I
    goto :goto_3

    #@d1
    .line 1481
    .end local v3    # "childLeft":I
    :sswitch_3
    sub-int v1, v8, v5

    #@d3
    iget v4, v15, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@d5
    sub-int v3, v1, v4

    #@d7
    .line 1482
    .restart local v3    # "childLeft":I
    goto :goto_3

    #@d8
    .line 1502
    .end local v2    # "child":Landroid/view/View;
    .end local v3    # "childLeft":I
    .end local v5    # "childWidth":I
    .end local v6    # "childHeight":I
    .end local v7    # "absoluteGravity":I
    .end local v12    # "gravity":I
    .end local v14    # "layoutDirection":I
    .end local v15    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    :cond_4
    return-void

    #@d9
    .line 1439
    nop

    #@da
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_1
        0x50 -> :sswitch_0
    .end sparse-switch

    #@e4
    .line 1474
    :sswitch_data_1
    .sparse-switch
        0x1 -> :sswitch_2
        0x5 -> :sswitch_3
    .end sparse-switch
.end method

.method measureChildBeforeLayout(Landroid/view/View;IIIII)V
    .locals 6
    .param p1, "child"    # Landroid/view/View;
    .param p2, "childIndex"    # I
    .param p3, "widthMeasureSpec"    # I
    .param p4, "totalWidth"    # I
    .param p5, "heightMeasureSpec"    # I
    .param p6, "totalHeight"    # I

    #@0
    .prologue
    .line 1373
    move-object v0, p0

    #@1
    move-object v1, p1

    #@2
    move v2, p3

    #@3
    move v3, p4

    #@4
    move v4, p5

    #@5
    move v5, p6

    #@6
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureChildWithMargins(Landroid/view/View;IIII)V

    #@9
    .line 1375
    return-void
.end method

.method measureHorizontal(II)V
    .locals 47
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 923
    const/4 v3, 0x0

    #@1
    move-object/from16 v0, p0

    #@3
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@5
    .line 924
    const/16 v35, 0x0

    #@7
    .line 925
    .local v35, "maxHeight":I
    const/16 v18, 0x0

    #@9
    .line 926
    .local v18, "childState":I
    const/4 v11, 0x0

    #@a
    .line 927
    .local v11, "alternativeMaxHeight":I
    const/16 v43, 0x0

    #@c
    .line 928
    .local v43, "weightedMaxHeight":I
    const/4 v10, 0x1

    #@d
    .line 929
    .local v10, "allFillParent":Z
    const/16 v40, 0x0

    #@f
    .line 931
    .local v40, "totalWeight":F
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@12
    move-result v20

    #@13
    .line 933
    .local v20, "count":I
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@16
    move-result v44

    #@17
    .line 934
    .local v44, "widthMode":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@1a
    move-result v25

    #@1b
    .line 936
    .local v25, "heightMode":I
    const/16 v31, 0x0

    #@1d
    .line 937
    .local v31, "matchHeight":Z
    const/16 v38, 0x0

    #@1f
    .line 939
    .local v38, "skippedMeasure":Z
    move-object/from16 v0, p0

    #@21
    iget-object v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxAscent:[I

    #@23
    if-eqz v3, :cond_0

    #@25
    move-object/from16 v0, p0

    #@27
    iget-object v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxDescent:[I

    #@29
    if-nez v3, :cond_1

    #@2b
    .line 940
    :cond_0
    const/4 v3, 0x4

    #@2c
    new-array v3, v3, [I

    #@2e
    move-object/from16 v0, p0

    #@30
    iput-object v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxAscent:[I

    #@32
    .line 941
    const/4 v3, 0x4

    #@33
    new-array v3, v3, [I

    #@35
    move-object/from16 v0, p0

    #@37
    iput-object v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxDescent:[I

    #@39
    .line 944
    :cond_1
    move-object/from16 v0, p0

    #@3b
    iget-object v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxAscent:[I

    #@3d
    move-object/from16 v33, v0

    #@3f
    .line 945
    .local v33, "maxAscent":[I
    move-object/from16 v0, p0

    #@41
    iget-object v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mMaxDescent:[I

    #@43
    move-object/from16 v34, v0

    #@45
    .line 947
    .local v34, "maxDescent":[I
    const/4 v3, 0x0

    #@46
    const/4 v6, 0x1

    #@47
    const/4 v7, 0x2

    #@48
    const/4 v8, 0x3

    #@49
    const/4 v9, -0x1

    #@4a
    aput v9, v33, v8

    #@4c
    aput v9, v33, v7

    #@4e
    aput v9, v33, v6

    #@50
    aput v9, v33, v3

    #@52
    .line 948
    const/4 v3, 0x0

    #@53
    const/4 v6, 0x1

    #@54
    const/4 v7, 0x2

    #@55
    const/4 v8, 0x3

    #@56
    const/4 v9, -0x1

    #@57
    aput v9, v34, v8

    #@59
    aput v9, v34, v7

    #@5b
    aput v9, v34, v6

    #@5d
    aput v9, v34, v3

    #@5f
    .line 950
    move-object/from16 v0, p0

    #@61
    iget-boolean v13, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAligned:Z

    #@63
    .line 951
    .local v13, "baselineAligned":Z
    move-object/from16 v0, p0

    #@65
    iget-boolean v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mUseLargestChild:Z

    #@67
    move/from16 v41, v0

    #@69
    .line 953
    .local v41, "useLargestChild":Z
    const/high16 v3, 0x40000000    # 2.0f

    #@6b
    move/from16 v0, v44

    #@6d
    if-ne v0, v3, :cond_2

    #@6f
    const/16 v27, 0x1

    #@71
    .line 955
    .local v27, "isExactly":Z
    :goto_0
    const/high16 v28, -0x80000000

    #@73
    .line 958
    .local v28, "largestChildWidth":I
    const/4 v5, 0x0

    #@74
    .local v5, "i":I
    :goto_1
    move/from16 v0, v20

    #@76
    if-ge v5, v0, :cond_15

    #@78
    .line 959
    move-object/from16 v0, p0

    #@7a
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@7d
    move-result-object v4

    #@7e
    .line 961
    .local v4, "child":Landroid/view/View;
    if-nez v4, :cond_3

    #@80
    .line 962
    move-object/from16 v0, p0

    #@82
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@84
    move-object/from16 v0, p0

    #@86
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@89
    move-result v6

    #@8a
    add-int/2addr v3, v6

    #@8b
    move-object/from16 v0, p0

    #@8d
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@8f
    .line 958
    :goto_2
    add-int/lit8 v5, v5, 0x1

    #@91
    goto :goto_1

    #@92
    .line 953
    .end local v4    # "child":Landroid/view/View;
    .end local v5    # "i":I
    .end local v27    # "isExactly":Z
    .end local v28    # "largestChildWidth":I
    :cond_2
    const/16 v27, 0x0

    #@94
    goto :goto_0

    #@95
    .line 966
    .restart local v4    # "child":Landroid/view/View;
    .restart local v5    # "i":I
    .restart local v27    # "isExactly":Z
    .restart local v28    # "largestChildWidth":I
    :cond_3
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@98
    move-result v3

    #@99
    const/16 v6, 0x8

    #@9b
    if-ne v3, v6, :cond_4

    #@9d
    .line 967
    move-object/from16 v0, p0

    #@9f
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@a2
    move-result v3

    #@a3
    add-int/2addr v5, v3

    #@a4
    .line 968
    goto :goto_2

    #@a5
    .line 971
    :cond_4
    move-object/from16 v0, p0

    #@a7
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@aa
    move-result v3

    #@ab
    if-eqz v3, :cond_5

    #@ad
    .line 972
    move-object/from16 v0, p0

    #@af
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@b1
    move-object/from16 v0, p0

    #@b3
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@b5
    add-int/2addr v3, v6

    #@b6
    move-object/from16 v0, p0

    #@b8
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@ba
    .line 975
    :cond_5
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@bd
    move-result-object v29

    #@be
    check-cast v29, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@c0
    .line 978
    .local v29, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v29

    #@c2
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@c4
    add-float v40, v40, v3

    #@c6
    .line 980
    const/high16 v3, 0x40000000    # 2.0f

    #@c8
    move/from16 v0, v44

    #@ca
    if-ne v0, v3, :cond_b

    #@cc
    move-object/from16 v0, v29

    #@ce
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@d0
    if-nez v3, :cond_b

    #@d2
    move-object/from16 v0, v29

    #@d4
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@d6
    const/4 v6, 0x0

    #@d7
    cmpl-float v3, v3, v6

    #@d9
    if-lez v3, :cond_b

    #@db
    .line 984
    if-eqz v27, :cond_9

    #@dd
    .line 985
    move-object/from16 v0, p0

    #@df
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@e1
    move-object/from16 v0, v29

    #@e3
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@e5
    move-object/from16 v0, v29

    #@e7
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@e9
    add-int/2addr v6, v7

    #@ea
    add-int/2addr v3, v6

    #@eb
    move-object/from16 v0, p0

    #@ed
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@ef
    .line 997
    :goto_3
    if-eqz v13, :cond_a

    #@f1
    .line 998
    const/4 v3, 0x0

    #@f2
    const/4 v6, 0x0

    #@f3
    invoke-static {v3, v6}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@f6
    move-result v23

    #@f7
    .line 999
    .local v23, "freeSpec":I
    move/from16 v0, v23

    #@f9
    move/from16 v1, v23

    #@fb
    invoke-virtual {v4, v0, v1}, Landroid/view/View;->measure(II)V

    #@fe
    .line 1042
    .end local v23    # "freeSpec":I
    :cond_6
    :goto_4
    const/16 v32, 0x0

    #@100
    .line 1043
    .local v32, "matchHeightLocally":Z
    const/high16 v3, 0x40000000    # 2.0f

    #@102
    move/from16 v0, v25

    #@104
    if-eq v0, v3, :cond_7

    #@106
    move-object/from16 v0, v29

    #@108
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@10a
    const/4 v6, -0x1

    #@10b
    if-ne v3, v6, :cond_7

    #@10d
    .line 1047
    const/16 v31, 0x1

    #@10f
    .line 1048
    const/16 v32, 0x1

    #@111
    .line 1051
    :cond_7
    move-object/from16 v0, v29

    #@113
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@115
    move-object/from16 v0, v29

    #@117
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@119
    add-int v30, v3, v6

    #@11b
    .line 1052
    .local v30, "margin":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@11e
    move-result v3

    #@11f
    add-int v16, v3, v30

    #@121
    .line 1053
    .local v16, "childHeight":I
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@124
    move-result v3

    #@125
    move/from16 v0, v18

    #@127
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@12a
    move-result v18

    #@12b
    .line 1056
    if-eqz v13, :cond_8

    #@12d
    .line 1057
    invoke-virtual {v4}, Landroid/view/View;->getBaseline()I

    #@130
    move-result v14

    #@131
    .line 1058
    .local v14, "childBaseline":I
    const/4 v3, -0x1

    #@132
    if-eq v14, v3, :cond_8

    #@134
    .line 1061
    move-object/from16 v0, v29

    #@136
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@138
    if-gez v3, :cond_10

    #@13a
    move-object/from16 v0, p0

    #@13c
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@13e
    :goto_5
    and-int/lit8 v24, v3, 0x70

    #@140
    .line 1063
    .local v24, "gravity":I
    shr-int/lit8 v3, v24, 0x4

    #@142
    and-int/lit8 v3, v3, -0x2

    #@144
    shr-int/lit8 v26, v3, 0x1

    #@146
    .line 1066
    .local v26, "index":I
    aget v3, v33, v26

    #@148
    invoke-static {v3, v14}, Ljava/lang/Math;->max(II)I

    #@14b
    move-result v3

    #@14c
    aput v3, v33, v26

    #@14e
    .line 1067
    aget v3, v34, v26

    #@150
    sub-int v6, v16, v14

    #@152
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@155
    move-result v3

    #@156
    aput v3, v34, v26

    #@158
    .line 1071
    .end local v14    # "childBaseline":I
    .end local v24    # "gravity":I
    .end local v26    # "index":I
    :cond_8
    move/from16 v0, v35

    #@15a
    move/from16 v1, v16

    #@15c
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@15f
    move-result v35

    #@160
    .line 1073
    if-eqz v10, :cond_11

    #@162
    move-object/from16 v0, v29

    #@164
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@166
    const/4 v6, -0x1

    #@167
    if-ne v3, v6, :cond_11

    #@169
    const/4 v10, 0x1

    #@16a
    .line 1074
    :goto_6
    move-object/from16 v0, v29

    #@16c
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@16e
    const/4 v6, 0x0

    #@16f
    cmpl-float v3, v3, v6

    #@171
    if-lez v3, :cond_13

    #@173
    .line 1079
    if-eqz v32, :cond_12

    #@175
    .end local v30    # "margin":I
    :goto_7
    move/from16 v0, v43

    #@177
    move/from16 v1, v30

    #@179
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@17c
    move-result v43

    #@17d
    .line 1086
    :goto_8
    move-object/from16 v0, p0

    #@17f
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@182
    move-result v3

    #@183
    add-int/2addr v5, v3

    #@184
    goto/16 :goto_2

    #@186
    .line 987
    .end local v16    # "childHeight":I
    .end local v32    # "matchHeightLocally":Z
    :cond_9
    move-object/from16 v0, p0

    #@188
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@18a
    move/from16 v39, v0

    #@18c
    .line 988
    .local v39, "totalLength":I
    move-object/from16 v0, v29

    #@18e
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@190
    add-int v3, v3, v39

    #@192
    move-object/from16 v0, v29

    #@194
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@196
    add-int/2addr v3, v6

    #@197
    move/from16 v0, v39

    #@199
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@19c
    move-result v3

    #@19d
    move-object/from16 v0, p0

    #@19f
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1a1
    goto/16 :goto_3

    #@1a3
    .line 1001
    .end local v39    # "totalLength":I
    :cond_a
    const/16 v38, 0x1

    #@1a5
    goto/16 :goto_4

    #@1a7
    .line 1004
    :cond_b
    const/high16 v36, -0x80000000

    #@1a9
    .line 1006
    .local v36, "oldWidth":I
    move-object/from16 v0, v29

    #@1ab
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@1ad
    if-nez v3, :cond_c

    #@1af
    move-object/from16 v0, v29

    #@1b1
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@1b3
    const/4 v6, 0x0

    #@1b4
    cmpl-float v3, v3, v6

    #@1b6
    if-lez v3, :cond_c

    #@1b8
    .line 1011
    const/16 v36, 0x0

    #@1ba
    .line 1012
    const/4 v3, -0x2

    #@1bb
    move-object/from16 v0, v29

    #@1bd
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@1bf
    .line 1019
    :cond_c
    const/4 v3, 0x0

    #@1c0
    cmpl-float v3, v40, v3

    #@1c2
    if-nez v3, :cond_e

    #@1c4
    move-object/from16 v0, p0

    #@1c6
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1c8
    :goto_9
    const/4 v9, 0x0

    #@1c9
    move-object/from16 v3, p0

    #@1cb
    move/from16 v6, p1

    #@1cd
    move/from16 v8, p2

    #@1cf
    invoke-virtual/range {v3 .. v9}, Landroid/support/v7/widget/LinearLayoutCompat;->measureChildBeforeLayout(Landroid/view/View;IIIII)V

    #@1d2
    .line 1023
    const/high16 v3, -0x80000000

    #@1d4
    move/from16 v0, v36

    #@1d6
    if-eq v0, v3, :cond_d

    #@1d8
    .line 1024
    move/from16 v0, v36

    #@1da
    move-object/from16 v1, v29

    #@1dc
    iput v0, v1, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@1de
    .line 1027
    :cond_d
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@1e1
    move-result v19

    #@1e2
    .line 1028
    .local v19, "childWidth":I
    if-eqz v27, :cond_f

    #@1e4
    .line 1029
    move-object/from16 v0, p0

    #@1e6
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1e8
    move-object/from16 v0, v29

    #@1ea
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@1ec
    add-int v6, v6, v19

    #@1ee
    move-object/from16 v0, v29

    #@1f0
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@1f2
    add-int/2addr v6, v7

    #@1f3
    move-object/from16 v0, p0

    #@1f5
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@1f8
    move-result v7

    #@1f9
    add-int/2addr v6, v7

    #@1fa
    add-int/2addr v3, v6

    #@1fb
    move-object/from16 v0, p0

    #@1fd
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1ff
    .line 1037
    :goto_a
    if-eqz v41, :cond_6

    #@201
    .line 1038
    move/from16 v0, v19

    #@203
    move/from16 v1, v28

    #@205
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@208
    move-result v28

    #@209
    goto/16 :goto_4

    #@20b
    .line 1019
    .end local v19    # "childWidth":I
    :cond_e
    const/4 v7, 0x0

    #@20c
    goto :goto_9

    #@20d
    .line 1032
    .restart local v19    # "childWidth":I
    :cond_f
    move-object/from16 v0, p0

    #@20f
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@211
    move/from16 v39, v0

    #@213
    .line 1033
    .restart local v39    # "totalLength":I
    add-int v3, v39, v19

    #@215
    move-object/from16 v0, v29

    #@217
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@219
    add-int/2addr v3, v6

    #@21a
    move-object/from16 v0, v29

    #@21c
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@21e
    add-int/2addr v3, v6

    #@21f
    move-object/from16 v0, p0

    #@221
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@224
    move-result v6

    #@225
    add-int/2addr v3, v6

    #@226
    move/from16 v0, v39

    #@228
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@22b
    move-result v3

    #@22c
    move-object/from16 v0, p0

    #@22e
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@230
    goto :goto_a

    #@231
    .line 1061
    .end local v19    # "childWidth":I
    .end local v36    # "oldWidth":I
    .end local v39    # "totalLength":I
    .restart local v14    # "childBaseline":I
    .restart local v16    # "childHeight":I
    .restart local v30    # "margin":I
    .restart local v32    # "matchHeightLocally":Z
    :cond_10
    move-object/from16 v0, v29

    #@233
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@235
    goto/16 :goto_5

    #@237
    .line 1073
    .end local v14    # "childBaseline":I
    :cond_11
    const/4 v10, 0x0

    #@238
    goto/16 :goto_6

    #@23a
    :cond_12
    move/from16 v30, v16

    #@23c
    .line 1079
    goto/16 :goto_7

    #@23e
    .line 1082
    :cond_13
    if-eqz v32, :cond_14

    #@240
    .end local v30    # "margin":I
    :goto_b
    move/from16 v0, v30

    #@242
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@245
    move-result v11

    #@246
    goto/16 :goto_8

    #@248
    .restart local v30    # "margin":I
    :cond_14
    move/from16 v30, v16

    #@24a
    goto :goto_b

    #@24b
    .line 1089
    .end local v4    # "child":Landroid/view/View;
    .end local v16    # "childHeight":I
    .end local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v30    # "margin":I
    .end local v32    # "matchHeightLocally":Z
    :cond_15
    move-object/from16 v0, p0

    #@24d
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@24f
    if-lez v3, :cond_16

    #@251
    move-object/from16 v0, p0

    #@253
    move/from16 v1, v20

    #@255
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@258
    move-result v3

    #@259
    if-eqz v3, :cond_16

    #@25b
    .line 1090
    move-object/from16 v0, p0

    #@25d
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@25f
    move-object/from16 v0, p0

    #@261
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@263
    add-int/2addr v3, v6

    #@264
    move-object/from16 v0, p0

    #@266
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@268
    .line 1095
    :cond_16
    const/4 v3, 0x1

    #@269
    aget v3, v33, v3

    #@26b
    const/4 v6, -0x1

    #@26c
    if-ne v3, v6, :cond_17

    #@26e
    const/4 v3, 0x0

    #@26f
    aget v3, v33, v3

    #@271
    const/4 v6, -0x1

    #@272
    if-ne v3, v6, :cond_17

    #@274
    const/4 v3, 0x2

    #@275
    aget v3, v33, v3

    #@277
    const/4 v6, -0x1

    #@278
    if-ne v3, v6, :cond_17

    #@27a
    const/4 v3, 0x3

    #@27b
    aget v3, v33, v3

    #@27d
    const/4 v6, -0x1

    #@27e
    if-eq v3, v6, :cond_18

    #@280
    .line 1099
    :cond_17
    const/4 v3, 0x3

    #@281
    aget v3, v33, v3

    #@283
    const/4 v6, 0x0

    #@284
    aget v6, v33, v6

    #@286
    const/4 v7, 0x1

    #@287
    aget v7, v33, v7

    #@289
    const/4 v8, 0x2

    #@28a
    aget v8, v33, v8

    #@28c
    invoke-static {v7, v8}, Ljava/lang/Math;->max(II)I

    #@28f
    move-result v7

    #@290
    invoke-static {v6, v7}, Ljava/lang/Math;->max(II)I

    #@293
    move-result v6

    #@294
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@297
    move-result v12

    #@298
    .line 1102
    .local v12, "ascent":I
    const/4 v3, 0x3

    #@299
    aget v3, v34, v3

    #@29b
    const/4 v6, 0x0

    #@29c
    aget v6, v34, v6

    #@29e
    const/4 v7, 0x1

    #@29f
    aget v7, v34, v7

    #@2a1
    const/4 v8, 0x2

    #@2a2
    aget v8, v34, v8

    #@2a4
    invoke-static {v7, v8}, Ljava/lang/Math;->max(II)I

    #@2a7
    move-result v7

    #@2a8
    invoke-static {v6, v7}, Ljava/lang/Math;->max(II)I

    #@2ab
    move-result v6

    #@2ac
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@2af
    move-result v22

    #@2b0
    .line 1105
    .local v22, "descent":I
    add-int v3, v12, v22

    #@2b2
    move/from16 v0, v35

    #@2b4
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@2b7
    move-result v35

    #@2b8
    .line 1108
    .end local v12    # "ascent":I
    .end local v22    # "descent":I
    :cond_18
    if-eqz v41, :cond_1d

    #@2ba
    const/high16 v3, -0x80000000

    #@2bc
    move/from16 v0, v44

    #@2be
    if-eq v0, v3, :cond_19

    #@2c0
    if-nez v44, :cond_1d

    #@2c2
    .line 1110
    :cond_19
    const/4 v3, 0x0

    #@2c3
    move-object/from16 v0, p0

    #@2c5
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@2c7
    .line 1112
    const/4 v5, 0x0

    #@2c8
    :goto_c
    move/from16 v0, v20

    #@2ca
    if-ge v5, v0, :cond_1d

    #@2cc
    .line 1113
    move-object/from16 v0, p0

    #@2ce
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@2d1
    move-result-object v4

    #@2d2
    .line 1115
    .restart local v4    # "child":Landroid/view/View;
    if-nez v4, :cond_1a

    #@2d4
    .line 1116
    move-object/from16 v0, p0

    #@2d6
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@2d8
    move-object/from16 v0, p0

    #@2da
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@2dd
    move-result v6

    #@2de
    add-int/2addr v3, v6

    #@2df
    move-object/from16 v0, p0

    #@2e1
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@2e3
    .line 1112
    :goto_d
    add-int/lit8 v5, v5, 0x1

    #@2e5
    goto :goto_c

    #@2e6
    .line 1120
    :cond_1a
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@2e9
    move-result v3

    #@2ea
    const/16 v6, 0x8

    #@2ec
    if-ne v3, v6, :cond_1b

    #@2ee
    .line 1121
    move-object/from16 v0, p0

    #@2f0
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@2f3
    move-result v3

    #@2f4
    add-int/2addr v5, v3

    #@2f5
    .line 1122
    goto :goto_d

    #@2f6
    .line 1125
    :cond_1b
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2f9
    move-result-object v29

    #@2fa
    check-cast v29, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@2fc
    .line 1127
    .restart local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    if-eqz v27, :cond_1c

    #@2fe
    .line 1128
    move-object/from16 v0, p0

    #@300
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@302
    move-object/from16 v0, v29

    #@304
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@306
    add-int v6, v6, v28

    #@308
    move-object/from16 v0, v29

    #@30a
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@30c
    add-int/2addr v6, v7

    #@30d
    move-object/from16 v0, p0

    #@30f
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@312
    move-result v7

    #@313
    add-int/2addr v6, v7

    #@314
    add-int/2addr v3, v6

    #@315
    move-object/from16 v0, p0

    #@317
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@319
    goto :goto_d

    #@31a
    .line 1131
    :cond_1c
    move-object/from16 v0, p0

    #@31c
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@31e
    move/from16 v39, v0

    #@320
    .line 1132
    .restart local v39    # "totalLength":I
    add-int v3, v39, v28

    #@322
    move-object/from16 v0, v29

    #@324
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@326
    add-int/2addr v3, v6

    #@327
    move-object/from16 v0, v29

    #@329
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@32b
    add-int/2addr v3, v6

    #@32c
    move-object/from16 v0, p0

    #@32e
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@331
    move-result v6

    #@332
    add-int/2addr v3, v6

    #@333
    move/from16 v0, v39

    #@335
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@338
    move-result v3

    #@339
    move-object/from16 v0, p0

    #@33b
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@33d
    goto :goto_d

    #@33e
    .line 1139
    .end local v4    # "child":Landroid/view/View;
    .end local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v39    # "totalLength":I
    :cond_1d
    move-object/from16 v0, p0

    #@340
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@342
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@345
    move-result v6

    #@346
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@349
    move-result v7

    #@34a
    add-int/2addr v6, v7

    #@34b
    add-int/2addr v3, v6

    #@34c
    move-object/from16 v0, p0

    #@34e
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@350
    .line 1141
    move-object/from16 v0, p0

    #@352
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@354
    move/from16 v45, v0

    #@356
    .line 1144
    .local v45, "widthSize":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getSuggestedMinimumWidth()I

    #@359
    move-result v3

    #@35a
    move/from16 v0, v45

    #@35c
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@35f
    move-result v45

    #@360
    .line 1147
    const/4 v3, 0x0

    #@361
    move/from16 v0, v45

    #@363
    move/from16 v1, p1

    #@365
    invoke-static {v0, v1, v3}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@368
    move-result v46

    #@369
    .line 1148
    .local v46, "widthSizeAndState":I
    const v3, 0xffffff

    #@36c
    and-int v45, v46, v3

    #@36e
    .line 1153
    move-object/from16 v0, p0

    #@370
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@372
    sub-int v21, v45, v3

    #@374
    .line 1154
    .local v21, "delta":I
    if-nez v38, :cond_1e

    #@376
    if-eqz v21, :cond_31

    #@378
    const/4 v3, 0x0

    #@379
    cmpl-float v3, v40, v3

    #@37b
    if-lez v3, :cond_31

    #@37d
    .line 1155
    :cond_1e
    move-object/from16 v0, p0

    #@37f
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@381
    const/4 v6, 0x0

    #@382
    cmpl-float v3, v3, v6

    #@384
    if-lez v3, :cond_20

    #@386
    move-object/from16 v0, p0

    #@388
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@38a
    move/from16 v42, v0

    #@38c
    .line 1157
    .local v42, "weightSum":F
    :goto_e
    const/4 v3, 0x0

    #@38d
    const/4 v6, 0x1

    #@38e
    const/4 v7, 0x2

    #@38f
    const/4 v8, 0x3

    #@390
    const/4 v9, -0x1

    #@391
    aput v9, v33, v8

    #@393
    aput v9, v33, v7

    #@395
    aput v9, v33, v6

    #@397
    aput v9, v33, v3

    #@399
    .line 1158
    const/4 v3, 0x0

    #@39a
    const/4 v6, 0x1

    #@39b
    const/4 v7, 0x2

    #@39c
    const/4 v8, 0x3

    #@39d
    const/4 v9, -0x1

    #@39e
    aput v9, v34, v8

    #@3a0
    aput v9, v34, v7

    #@3a2
    aput v9, v34, v6

    #@3a4
    aput v9, v34, v3

    #@3a6
    .line 1159
    const/16 v35, -0x1

    #@3a8
    .line 1161
    const/4 v3, 0x0

    #@3a9
    move-object/from16 v0, p0

    #@3ab
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@3ad
    .line 1163
    const/4 v5, 0x0

    #@3ae
    :goto_f
    move/from16 v0, v20

    #@3b0
    if-ge v5, v0, :cond_2c

    #@3b2
    .line 1164
    move-object/from16 v0, p0

    #@3b4
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@3b7
    move-result-object v4

    #@3b8
    .line 1166
    .restart local v4    # "child":Landroid/view/View;
    if-eqz v4, :cond_1f

    #@3ba
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@3bd
    move-result v3

    #@3be
    const/16 v6, 0x8

    #@3c0
    if-ne v3, v6, :cond_21

    #@3c2
    .line 1163
    :cond_1f
    :goto_10
    add-int/lit8 v5, v5, 0x1

    #@3c4
    goto :goto_f

    #@3c5
    .end local v4    # "child":Landroid/view/View;
    .end local v42    # "weightSum":F
    :cond_20
    move/from16 v42, v40

    #@3c7
    .line 1155
    goto :goto_e

    #@3c8
    .line 1170
    .restart local v4    # "child":Landroid/view/View;
    .restart local v42    # "weightSum":F
    :cond_21
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3cb
    move-result-object v29

    #@3cc
    check-cast v29, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@3ce
    .line 1173
    .restart local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v29

    #@3d0
    iget v15, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@3d2
    .line 1174
    .local v15, "childExtra":F
    const/4 v3, 0x0

    #@3d3
    cmpl-float v3, v15, v3

    #@3d5
    if-lez v3, :cond_24

    #@3d7
    .line 1176
    move/from16 v0, v21

    #@3d9
    int-to-float v3, v0

    #@3da
    mul-float/2addr v3, v15

    #@3db
    div-float v3, v3, v42

    #@3dd
    float-to-int v0, v3

    #@3de
    move/from16 v37, v0

    #@3e0
    .line 1177
    .local v37, "share":I
    sub-float v42, v42, v15

    #@3e2
    .line 1178
    sub-int v21, v21, v37

    #@3e4
    .line 1180
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@3e7
    move-result v3

    #@3e8
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@3eb
    move-result v6

    #@3ec
    add-int/2addr v3, v6

    #@3ed
    move-object/from16 v0, v29

    #@3ef
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@3f1
    add-int/2addr v3, v6

    #@3f2
    move-object/from16 v0, v29

    #@3f4
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@3f6
    add-int/2addr v3, v6

    #@3f7
    move-object/from16 v0, v29

    #@3f9
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@3fb
    move/from16 v0, p2

    #@3fd
    invoke-static {v0, v3, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildMeasureSpec(III)I

    #@400
    move-result v17

    #@401
    .line 1187
    .local v17, "childHeightMeasureSpec":I
    move-object/from16 v0, v29

    #@403
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@405
    if-nez v3, :cond_22

    #@407
    const/high16 v3, 0x40000000    # 2.0f

    #@409
    move/from16 v0, v44

    #@40b
    if-eq v0, v3, :cond_25

    #@40d
    .line 1190
    :cond_22
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@410
    move-result v3

    #@411
    add-int v19, v3, v37

    #@413
    .line 1191
    .restart local v19    # "childWidth":I
    if-gez v19, :cond_23

    #@415
    .line 1192
    const/16 v19, 0x0

    #@417
    .line 1195
    :cond_23
    const/high16 v3, 0x40000000    # 2.0f

    #@419
    move/from16 v0, v19

    #@41b
    invoke-static {v0, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@41e
    move-result v3

    #@41f
    move/from16 v0, v17

    #@421
    invoke-virtual {v4, v3, v0}, Landroid/view/View;->measure(II)V

    #@424
    .line 1206
    .end local v19    # "childWidth":I
    .end local v37    # "share":I
    :goto_11
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@427
    move-result v3

    #@428
    const/high16 v6, -0x1000000

    #@42a
    and-int/2addr v3, v6

    #@42b
    move/from16 v0, v18

    #@42d
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@430
    move-result v18

    #@431
    .line 1210
    .end local v17    # "childHeightMeasureSpec":I
    :cond_24
    if-eqz v27, :cond_27

    #@433
    .line 1211
    move-object/from16 v0, p0

    #@435
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@437
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@43a
    move-result v6

    #@43b
    move-object/from16 v0, v29

    #@43d
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@43f
    add-int/2addr v6, v7

    #@440
    move-object/from16 v0, v29

    #@442
    iget v7, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@444
    add-int/2addr v6, v7

    #@445
    move-object/from16 v0, p0

    #@447
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@44a
    move-result v7

    #@44b
    add-int/2addr v6, v7

    #@44c
    add-int/2addr v3, v6

    #@44d
    move-object/from16 v0, p0

    #@44f
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@451
    .line 1219
    :goto_12
    const/high16 v3, 0x40000000    # 2.0f

    #@453
    move/from16 v0, v25

    #@455
    if-eq v0, v3, :cond_28

    #@457
    move-object/from16 v0, v29

    #@459
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@45b
    const/4 v6, -0x1

    #@45c
    if-ne v3, v6, :cond_28

    #@45e
    const/16 v32, 0x1

    #@460
    .line 1222
    .restart local v32    # "matchHeightLocally":Z
    :goto_13
    move-object/from16 v0, v29

    #@462
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@464
    move-object/from16 v0, v29

    #@466
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@468
    add-int v30, v3, v6

    #@46a
    .line 1223
    .restart local v30    # "margin":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@46d
    move-result v3

    #@46e
    add-int v16, v3, v30

    #@470
    .line 1224
    .restart local v16    # "childHeight":I
    move/from16 v0, v35

    #@472
    move/from16 v1, v16

    #@474
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@477
    move-result v35

    #@478
    .line 1225
    if-eqz v32, :cond_29

    #@47a
    .end local v30    # "margin":I
    :goto_14
    move/from16 v0, v30

    #@47c
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@47f
    move-result v11

    #@480
    .line 1228
    if-eqz v10, :cond_2a

    #@482
    move-object/from16 v0, v29

    #@484
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@486
    const/4 v6, -0x1

    #@487
    if-ne v3, v6, :cond_2a

    #@489
    const/4 v10, 0x1

    #@48a
    .line 1230
    :goto_15
    if-eqz v13, :cond_1f

    #@48c
    .line 1231
    invoke-virtual {v4}, Landroid/view/View;->getBaseline()I

    #@48f
    move-result v14

    #@490
    .line 1232
    .restart local v14    # "childBaseline":I
    const/4 v3, -0x1

    #@491
    if-eq v14, v3, :cond_1f

    #@493
    .line 1234
    move-object/from16 v0, v29

    #@495
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@497
    if-gez v3, :cond_2b

    #@499
    move-object/from16 v0, p0

    #@49b
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@49d
    :goto_16
    and-int/lit8 v24, v3, 0x70

    #@49f
    .line 1236
    .restart local v24    # "gravity":I
    shr-int/lit8 v3, v24, 0x4

    #@4a1
    and-int/lit8 v3, v3, -0x2

    #@4a3
    shr-int/lit8 v26, v3, 0x1

    #@4a5
    .line 1239
    .restart local v26    # "index":I
    aget v3, v33, v26

    #@4a7
    invoke-static {v3, v14}, Ljava/lang/Math;->max(II)I

    #@4aa
    move-result v3

    #@4ab
    aput v3, v33, v26

    #@4ad
    .line 1240
    aget v3, v34, v26

    #@4af
    sub-int v6, v16, v14

    #@4b1
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@4b4
    move-result v3

    #@4b5
    aput v3, v34, v26

    #@4b7
    goto/16 :goto_10

    #@4b9
    .line 1200
    .end local v14    # "childBaseline":I
    .end local v16    # "childHeight":I
    .end local v24    # "gravity":I
    .end local v26    # "index":I
    .end local v32    # "matchHeightLocally":Z
    .restart local v17    # "childHeightMeasureSpec":I
    .restart local v37    # "share":I
    :cond_25
    if-lez v37, :cond_26

    #@4bb
    .end local v37    # "share":I
    :goto_17
    const/high16 v3, 0x40000000    # 2.0f

    #@4bd
    move/from16 v0, v37

    #@4bf
    invoke-static {v0, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@4c2
    move-result v3

    #@4c3
    move/from16 v0, v17

    #@4c5
    invoke-virtual {v4, v3, v0}, Landroid/view/View;->measure(II)V

    #@4c8
    goto/16 :goto_11

    #@4ca
    .restart local v37    # "share":I
    :cond_26
    const/16 v37, 0x0

    #@4cc
    goto :goto_17

    #@4cd
    .line 1214
    .end local v17    # "childHeightMeasureSpec":I
    .end local v37    # "share":I
    :cond_27
    move-object/from16 v0, p0

    #@4cf
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@4d1
    move/from16 v39, v0

    #@4d3
    .line 1215
    .restart local v39    # "totalLength":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@4d6
    move-result v3

    #@4d7
    add-int v3, v3, v39

    #@4d9
    move-object/from16 v0, v29

    #@4db
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@4dd
    add-int/2addr v3, v6

    #@4de
    move-object/from16 v0, v29

    #@4e0
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@4e2
    add-int/2addr v3, v6

    #@4e3
    move-object/from16 v0, p0

    #@4e5
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@4e8
    move-result v6

    #@4e9
    add-int/2addr v3, v6

    #@4ea
    move/from16 v0, v39

    #@4ec
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@4ef
    move-result v3

    #@4f0
    move-object/from16 v0, p0

    #@4f2
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@4f4
    goto/16 :goto_12

    #@4f6
    .line 1219
    .end local v39    # "totalLength":I
    :cond_28
    const/16 v32, 0x0

    #@4f8
    goto/16 :goto_13

    #@4fa
    .restart local v16    # "childHeight":I
    .restart local v30    # "margin":I
    .restart local v32    # "matchHeightLocally":Z
    :cond_29
    move/from16 v30, v16

    #@4fc
    .line 1225
    goto/16 :goto_14

    #@4fe
    .line 1228
    .end local v30    # "margin":I
    :cond_2a
    const/4 v10, 0x0

    #@4ff
    goto :goto_15

    #@500
    .line 1234
    .restart local v14    # "childBaseline":I
    :cond_2b
    move-object/from16 v0, v29

    #@502
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@504
    goto :goto_16

    #@505
    .line 1247
    .end local v4    # "child":Landroid/view/View;
    .end local v14    # "childBaseline":I
    .end local v15    # "childExtra":F
    .end local v16    # "childHeight":I
    .end local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v32    # "matchHeightLocally":Z
    :cond_2c
    move-object/from16 v0, p0

    #@507
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@509
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@50c
    move-result v6

    #@50d
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@510
    move-result v7

    #@511
    add-int/2addr v6, v7

    #@512
    add-int/2addr v3, v6

    #@513
    move-object/from16 v0, p0

    #@515
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@517
    .line 1252
    const/4 v3, 0x1

    #@518
    aget v3, v33, v3

    #@51a
    const/4 v6, -0x1

    #@51b
    if-ne v3, v6, :cond_2d

    #@51d
    const/4 v3, 0x0

    #@51e
    aget v3, v33, v3

    #@520
    const/4 v6, -0x1

    #@521
    if-ne v3, v6, :cond_2d

    #@523
    const/4 v3, 0x2

    #@524
    aget v3, v33, v3

    #@526
    const/4 v6, -0x1

    #@527
    if-ne v3, v6, :cond_2d

    #@529
    const/4 v3, 0x3

    #@52a
    aget v3, v33, v3

    #@52c
    const/4 v6, -0x1

    #@52d
    if-eq v3, v6, :cond_2e

    #@52f
    .line 1256
    :cond_2d
    const/4 v3, 0x3

    #@530
    aget v3, v33, v3

    #@532
    const/4 v6, 0x0

    #@533
    aget v6, v33, v6

    #@535
    const/4 v7, 0x1

    #@536
    aget v7, v33, v7

    #@538
    const/4 v8, 0x2

    #@539
    aget v8, v33, v8

    #@53b
    invoke-static {v7, v8}, Ljava/lang/Math;->max(II)I

    #@53e
    move-result v7

    #@53f
    invoke-static {v6, v7}, Ljava/lang/Math;->max(II)I

    #@542
    move-result v6

    #@543
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@546
    move-result v12

    #@547
    .line 1259
    .restart local v12    # "ascent":I
    const/4 v3, 0x3

    #@548
    aget v3, v34, v3

    #@54a
    const/4 v6, 0x0

    #@54b
    aget v6, v34, v6

    #@54d
    const/4 v7, 0x1

    #@54e
    aget v7, v34, v7

    #@550
    const/4 v8, 0x2

    #@551
    aget v8, v34, v8

    #@553
    invoke-static {v7, v8}, Ljava/lang/Math;->max(II)I

    #@556
    move-result v7

    #@557
    invoke-static {v6, v7}, Ljava/lang/Math;->max(II)I

    #@55a
    move-result v6

    #@55b
    invoke-static {v3, v6}, Ljava/lang/Math;->max(II)I

    #@55e
    move-result v22

    #@55f
    .line 1262
    .restart local v22    # "descent":I
    add-int v3, v12, v22

    #@561
    move/from16 v0, v35

    #@563
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@566
    move-result v35

    #@567
    .line 1291
    .end local v12    # "ascent":I
    .end local v22    # "descent":I
    .end local v42    # "weightSum":F
    :cond_2e
    if-nez v10, :cond_2f

    #@569
    const/high16 v3, 0x40000000    # 2.0f

    #@56b
    move/from16 v0, v25

    #@56d
    if-eq v0, v3, :cond_2f

    #@56f
    .line 1292
    move/from16 v35, v11

    #@571
    .line 1295
    :cond_2f
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@574
    move-result v3

    #@575
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@578
    move-result v6

    #@579
    add-int/2addr v3, v6

    #@57a
    add-int v35, v35, v3

    #@57c
    .line 1298
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getSuggestedMinimumHeight()I

    #@57f
    move-result v3

    #@580
    move/from16 v0, v35

    #@582
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@585
    move-result v35

    #@586
    .line 1300
    const/high16 v3, -0x1000000

    #@588
    and-int v3, v3, v18

    #@58a
    or-int v3, v3, v46

    #@58c
    shl-int/lit8 v6, v18, 0x10

    #@58e
    move/from16 v0, v35

    #@590
    move/from16 v1, p2

    #@592
    invoke-static {v0, v1, v6}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@595
    move-result v6

    #@596
    move-object/from16 v0, p0

    #@598
    invoke-virtual {v0, v3, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->setMeasuredDimension(II)V

    #@59b
    .line 1304
    if-eqz v31, :cond_30

    #@59d
    .line 1305
    move-object/from16 v0, p0

    #@59f
    move/from16 v1, v20

    #@5a1
    move/from16 v2, p1

    #@5a3
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->forceUniformHeight(II)V

    #@5a6
    .line 1307
    :cond_30
    return-void

    #@5a7
    .line 1265
    :cond_31
    move/from16 v0, v43

    #@5a9
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@5ac
    move-result v11

    #@5ad
    .line 1269
    if-eqz v41, :cond_2e

    #@5af
    const/high16 v3, 0x40000000    # 2.0f

    #@5b1
    move/from16 v0, v44

    #@5b3
    if-eq v0, v3, :cond_2e

    #@5b5
    .line 1270
    const/4 v5, 0x0

    #@5b6
    :goto_18
    move/from16 v0, v20

    #@5b8
    if-ge v5, v0, :cond_2e

    #@5ba
    .line 1271
    move-object/from16 v0, p0

    #@5bc
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@5bf
    move-result-object v4

    #@5c0
    .line 1273
    .restart local v4    # "child":Landroid/view/View;
    if-eqz v4, :cond_32

    #@5c2
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@5c5
    move-result v3

    #@5c6
    const/16 v6, 0x8

    #@5c8
    if-ne v3, v6, :cond_33

    #@5ca
    .line 1270
    :cond_32
    :goto_19
    add-int/lit8 v5, v5, 0x1

    #@5cc
    goto :goto_18

    #@5cd
    .line 1277
    :cond_33
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@5d0
    move-result-object v29

    #@5d1
    check-cast v29, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@5d3
    .line 1280
    .restart local v29    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v29

    #@5d5
    iget v15, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@5d7
    .line 1281
    .restart local v15    # "childExtra":F
    const/4 v3, 0x0

    #@5d8
    cmpl-float v3, v15, v3

    #@5da
    if-lez v3, :cond_32

    #@5dc
    .line 1282
    const/high16 v3, 0x40000000    # 2.0f

    #@5de
    move/from16 v0, v28

    #@5e0
    invoke-static {v0, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@5e3
    move-result v3

    #@5e4
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@5e7
    move-result v6

    #@5e8
    const/high16 v7, 0x40000000    # 2.0f

    #@5ea
    invoke-static {v6, v7}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@5ed
    move-result v6

    #@5ee
    invoke-virtual {v4, v3, v6}, Landroid/view/View;->measure(II)V

    #@5f1
    goto :goto_19
.end method

.method measureNullChild(I)I
    .locals 1
    .param p1, "childIndex"    # I

    #@0
    .prologue
    .line 1354
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method measureVertical(II)V
    .locals 38
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 590
    const/4 v3, 0x0

    #@1
    move-object/from16 v0, p0

    #@3
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@5
    .line 591
    const/16 v27, 0x0

    #@7
    .line 592
    .local v27, "maxWidth":I
    const/4 v15, 0x0

    #@8
    .line 593
    .local v15, "childState":I
    const/4 v11, 0x0

    #@9
    .line 594
    .local v11, "alternativeMaxWidth":I
    const/16 v36, 0x0

    #@b
    .line 595
    .local v36, "weightedMaxWidth":I
    const/4 v10, 0x1

    #@c
    .line 596
    .local v10, "allFillParent":Z
    const/16 v33, 0x0

    #@e
    .line 598
    .local v33, "totalWeight":F
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildCount()I

    #@11
    move-result v17

    #@12
    .line 600
    .local v17, "count":I
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@15
    move-result v37

    #@16
    .line 601
    .local v37, "widthMode":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@19
    move-result v19

    #@1a
    .line 603
    .local v19, "heightMode":I
    const/16 v25, 0x0

    #@1c
    .line 604
    .local v25, "matchWidth":Z
    const/16 v31, 0x0

    #@1e
    .line 606
    .local v31, "skippedMeasure":Z
    move-object/from16 v0, p0

    #@20
    iget v12, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@22
    .line 607
    .local v12, "baselineChildIndex":I
    move-object/from16 v0, p0

    #@24
    iget-boolean v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mUseLargestChild:Z

    #@26
    move/from16 v34, v0

    #@28
    .line 609
    .local v34, "useLargestChild":Z
    const/high16 v22, -0x80000000

    #@2a
    .line 612
    .local v22, "largestChildHeight":I
    const/4 v5, 0x0

    #@2b
    .local v5, "i":I
    :goto_0
    move/from16 v0, v17

    #@2d
    if-ge v5, v0, :cond_f

    #@2f
    .line 613
    move-object/from16 v0, p0

    #@31
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@34
    move-result-object v4

    #@35
    .line 615
    .local v4, "child":Landroid/view/View;
    if-nez v4, :cond_0

    #@37
    .line 616
    move-object/from16 v0, p0

    #@39
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@3b
    move-object/from16 v0, p0

    #@3d
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@40
    move-result v6

    #@41
    add-int/2addr v3, v6

    #@42
    move-object/from16 v0, p0

    #@44
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@46
    .line 612
    :goto_1
    add-int/lit8 v5, v5, 0x1

    #@48
    goto :goto_0

    #@49
    .line 620
    :cond_0
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@4c
    move-result v3

    #@4d
    const/16 v6, 0x8

    #@4f
    if-ne v3, v6, :cond_1

    #@51
    .line 621
    move-object/from16 v0, p0

    #@53
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@56
    move-result v3

    #@57
    add-int/2addr v5, v3

    #@58
    .line 622
    goto :goto_1

    #@59
    .line 625
    :cond_1
    move-object/from16 v0, p0

    #@5b
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@5e
    move-result v3

    #@5f
    if-eqz v3, :cond_2

    #@61
    .line 626
    move-object/from16 v0, p0

    #@63
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@65
    move-object/from16 v0, p0

    #@67
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@69
    add-int/2addr v3, v6

    #@6a
    move-object/from16 v0, p0

    #@6c
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@6e
    .line 629
    :cond_2
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@71
    move-result-object v23

    #@72
    check-cast v23, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@74
    .line 631
    .local v23, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v23

    #@76
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@78
    add-float v33, v33, v3

    #@7a
    .line 633
    const/high16 v3, 0x40000000    # 2.0f

    #@7c
    move/from16 v0, v19

    #@7e
    if-ne v0, v3, :cond_5

    #@80
    move-object/from16 v0, v23

    #@82
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@84
    if-nez v3, :cond_5

    #@86
    move-object/from16 v0, v23

    #@88
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@8a
    const/4 v6, 0x0

    #@8b
    cmpl-float v3, v3, v6

    #@8d
    if-lez v3, :cond_5

    #@8f
    .line 637
    move-object/from16 v0, p0

    #@91
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@93
    move/from16 v32, v0

    #@95
    .line 638
    .local v32, "totalLength":I
    move-object/from16 v0, v23

    #@97
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@99
    add-int v3, v3, v32

    #@9b
    move-object/from16 v0, v23

    #@9d
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@9f
    add-int/2addr v3, v6

    #@a0
    move/from16 v0, v32

    #@a2
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@a5
    move-result v3

    #@a6
    move-object/from16 v0, p0

    #@a8
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@aa
    .line 639
    const/16 v31, 0x1

    #@ac
    .line 678
    :cond_3
    :goto_2
    if-ltz v12, :cond_4

    #@ae
    add-int/lit8 v3, v5, 0x1

    #@b0
    if-ne v12, v3, :cond_4

    #@b2
    .line 679
    move-object/from16 v0, p0

    #@b4
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@b6
    move-object/from16 v0, p0

    #@b8
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineChildTop:I

    #@ba
    .line 685
    :cond_4
    if-ge v5, v12, :cond_9

    #@bc
    move-object/from16 v0, v23

    #@be
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@c0
    const/4 v6, 0x0

    #@c1
    cmpl-float v3, v3, v6

    #@c3
    if-lez v3, :cond_9

    #@c5
    .line 686
    new-instance v3, Ljava/lang/RuntimeException;

    #@c7
    const-string v6, "A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won\'t work.  Either remove the weight, or don\'t set mBaselineAlignedChildIndex."

    #@c9
    invoke-direct {v3, v6}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@cc
    throw v3

    #@cd
    .line 641
    .end local v32    # "totalLength":I
    :cond_5
    const/high16 v29, -0x80000000

    #@cf
    .line 643
    .local v29, "oldHeight":I
    move-object/from16 v0, v23

    #@d1
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@d3
    if-nez v3, :cond_6

    #@d5
    move-object/from16 v0, v23

    #@d7
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@d9
    const/4 v6, 0x0

    #@da
    cmpl-float v3, v3, v6

    #@dc
    if-lez v3, :cond_6

    #@de
    .line 648
    const/16 v29, 0x0

    #@e0
    .line 649
    const/4 v3, -0x2

    #@e1
    move-object/from16 v0, v23

    #@e3
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@e5
    .line 656
    :cond_6
    const/4 v7, 0x0

    #@e6
    const/4 v3, 0x0

    #@e7
    cmpl-float v3, v33, v3

    #@e9
    if-nez v3, :cond_8

    #@eb
    move-object/from16 v0, p0

    #@ed
    iget v9, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@ef
    :goto_3
    move-object/from16 v3, p0

    #@f1
    move/from16 v6, p1

    #@f3
    move/from16 v8, p2

    #@f5
    invoke-virtual/range {v3 .. v9}, Landroid/support/v7/widget/LinearLayoutCompat;->measureChildBeforeLayout(Landroid/view/View;IIIII)V

    #@f8
    .line 660
    const/high16 v3, -0x80000000

    #@fa
    move/from16 v0, v29

    #@fc
    if-eq v0, v3, :cond_7

    #@fe
    .line 661
    move/from16 v0, v29

    #@100
    move-object/from16 v1, v23

    #@102
    iput v0, v1, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@104
    .line 664
    :cond_7
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@107
    move-result v14

    #@108
    .line 665
    .local v14, "childHeight":I
    move-object/from16 v0, p0

    #@10a
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@10c
    move/from16 v32, v0

    #@10e
    .line 666
    .restart local v32    # "totalLength":I
    add-int v3, v32, v14

    #@110
    move-object/from16 v0, v23

    #@112
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@114
    add-int/2addr v3, v6

    #@115
    move-object/from16 v0, v23

    #@117
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@119
    add-int/2addr v3, v6

    #@11a
    move-object/from16 v0, p0

    #@11c
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@11f
    move-result v6

    #@120
    add-int/2addr v3, v6

    #@121
    move/from16 v0, v32

    #@123
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@126
    move-result v3

    #@127
    move-object/from16 v0, p0

    #@129
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@12b
    .line 669
    if-eqz v34, :cond_3

    #@12d
    .line 670
    move/from16 v0, v22

    #@12f
    invoke-static {v14, v0}, Ljava/lang/Math;->max(II)I

    #@132
    move-result v22

    #@133
    goto/16 :goto_2

    #@135
    .line 656
    .end local v14    # "childHeight":I
    .end local v32    # "totalLength":I
    :cond_8
    const/4 v9, 0x0

    #@136
    goto :goto_3

    #@137
    .line 692
    .end local v29    # "oldHeight":I
    .restart local v32    # "totalLength":I
    :cond_9
    const/16 v26, 0x0

    #@139
    .line 693
    .local v26, "matchWidthLocally":Z
    const/high16 v3, 0x40000000    # 2.0f

    #@13b
    move/from16 v0, v37

    #@13d
    if-eq v0, v3, :cond_a

    #@13f
    move-object/from16 v0, v23

    #@141
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@143
    const/4 v6, -0x1

    #@144
    if-ne v3, v6, :cond_a

    #@146
    .line 698
    const/16 v25, 0x1

    #@148
    .line 699
    const/16 v26, 0x1

    #@14a
    .line 702
    :cond_a
    move-object/from16 v0, v23

    #@14c
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@14e
    move-object/from16 v0, v23

    #@150
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@152
    add-int v24, v3, v6

    #@154
    .line 703
    .local v24, "margin":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@157
    move-result v3

    #@158
    add-int v28, v3, v24

    #@15a
    .line 704
    .local v28, "measuredWidth":I
    invoke-static/range {v27 .. v28}, Ljava/lang/Math;->max(II)I

    #@15d
    move-result v27

    #@15e
    .line 705
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@161
    move-result v3

    #@162
    invoke-static {v15, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@165
    move-result v15

    #@166
    .line 708
    if-eqz v10, :cond_b

    #@168
    move-object/from16 v0, v23

    #@16a
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@16c
    const/4 v6, -0x1

    #@16d
    if-ne v3, v6, :cond_b

    #@16f
    const/4 v10, 0x1

    #@170
    .line 709
    :goto_4
    move-object/from16 v0, v23

    #@172
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@174
    const/4 v6, 0x0

    #@175
    cmpl-float v3, v3, v6

    #@177
    if-lez v3, :cond_d

    #@179
    .line 714
    if-eqz v26, :cond_c

    #@17b
    .end local v24    # "margin":I
    :goto_5
    move/from16 v0, v36

    #@17d
    move/from16 v1, v24

    #@17f
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@182
    move-result v36

    #@183
    .line 721
    :goto_6
    move-object/from16 v0, p0

    #@185
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@188
    move-result v3

    #@189
    add-int/2addr v5, v3

    #@18a
    goto/16 :goto_1

    #@18c
    .line 708
    .restart local v24    # "margin":I
    :cond_b
    const/4 v10, 0x0

    #@18d
    goto :goto_4

    #@18e
    :cond_c
    move/from16 v24, v28

    #@190
    .line 714
    goto :goto_5

    #@191
    .line 717
    :cond_d
    if-eqz v26, :cond_e

    #@193
    .end local v24    # "margin":I
    :goto_7
    move/from16 v0, v24

    #@195
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@198
    move-result v11

    #@199
    goto :goto_6

    #@19a
    .restart local v24    # "margin":I
    :cond_e
    move/from16 v24, v28

    #@19c
    goto :goto_7

    #@19d
    .line 724
    .end local v4    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v24    # "margin":I
    .end local v26    # "matchWidthLocally":Z
    .end local v28    # "measuredWidth":I
    .end local v32    # "totalLength":I
    :cond_f
    move-object/from16 v0, p0

    #@19f
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1a1
    if-lez v3, :cond_10

    #@1a3
    move-object/from16 v0, p0

    #@1a5
    move/from16 v1, v17

    #@1a7
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->hasDividerBeforeChildAt(I)Z

    #@1aa
    move-result v3

    #@1ab
    if-eqz v3, :cond_10

    #@1ad
    .line 725
    move-object/from16 v0, p0

    #@1af
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1b1
    move-object/from16 v0, p0

    #@1b3
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@1b5
    add-int/2addr v3, v6

    #@1b6
    move-object/from16 v0, p0

    #@1b8
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1ba
    .line 728
    :cond_10
    if-eqz v34, :cond_14

    #@1bc
    const/high16 v3, -0x80000000

    #@1be
    move/from16 v0, v19

    #@1c0
    if-eq v0, v3, :cond_11

    #@1c2
    if-nez v19, :cond_14

    #@1c4
    .line 730
    :cond_11
    const/4 v3, 0x0

    #@1c5
    move-object/from16 v0, p0

    #@1c7
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1c9
    .line 732
    const/4 v5, 0x0

    #@1ca
    :goto_8
    move/from16 v0, v17

    #@1cc
    if-ge v5, v0, :cond_14

    #@1ce
    .line 733
    move-object/from16 v0, p0

    #@1d0
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@1d3
    move-result-object v4

    #@1d4
    .line 735
    .restart local v4    # "child":Landroid/view/View;
    if-nez v4, :cond_12

    #@1d6
    .line 736
    move-object/from16 v0, p0

    #@1d8
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1da
    move-object/from16 v0, p0

    #@1dc
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->measureNullChild(I)I

    #@1df
    move-result v6

    #@1e0
    add-int/2addr v3, v6

    #@1e1
    move-object/from16 v0, p0

    #@1e3
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@1e5
    .line 732
    :goto_9
    add-int/lit8 v5, v5, 0x1

    #@1e7
    goto :goto_8

    #@1e8
    .line 740
    :cond_12
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@1eb
    move-result v3

    #@1ec
    const/16 v6, 0x8

    #@1ee
    if-ne v3, v6, :cond_13

    #@1f0
    .line 741
    move-object/from16 v0, p0

    #@1f2
    invoke-virtual {v0, v4, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildrenSkipCount(Landroid/view/View;I)I

    #@1f5
    move-result v3

    #@1f6
    add-int/2addr v5, v3

    #@1f7
    .line 742
    goto :goto_9

    #@1f8
    .line 745
    :cond_13
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1fb
    move-result-object v23

    #@1fc
    check-cast v23, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@1fe
    .line 748
    .restart local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, p0

    #@200
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@202
    move/from16 v32, v0

    #@204
    .line 749
    .restart local v32    # "totalLength":I
    add-int v3, v32, v22

    #@206
    move-object/from16 v0, v23

    #@208
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@20a
    add-int/2addr v3, v6

    #@20b
    move-object/from16 v0, v23

    #@20d
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@20f
    add-int/2addr v3, v6

    #@210
    move-object/from16 v0, p0

    #@212
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@215
    move-result v6

    #@216
    add-int/2addr v3, v6

    #@217
    move/from16 v0, v32

    #@219
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@21c
    move-result v3

    #@21d
    move-object/from16 v0, p0

    #@21f
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@221
    goto :goto_9

    #@222
    .line 755
    .end local v4    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v32    # "totalLength":I
    :cond_14
    move-object/from16 v0, p0

    #@224
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@226
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@229
    move-result v6

    #@22a
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@22d
    move-result v7

    #@22e
    add-int/2addr v6, v7

    #@22f
    add-int/2addr v3, v6

    #@230
    move-object/from16 v0, p0

    #@232
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@234
    .line 757
    move-object/from16 v0, p0

    #@236
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@238
    move/from16 v20, v0

    #@23a
    .line 760
    .local v20, "heightSize":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getSuggestedMinimumHeight()I

    #@23d
    move-result v3

    #@23e
    move/from16 v0, v20

    #@240
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@243
    move-result v20

    #@244
    .line 763
    const/4 v3, 0x0

    #@245
    move/from16 v0, v20

    #@247
    move/from16 v1, p2

    #@249
    invoke-static {v0, v1, v3}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@24c
    move-result v21

    #@24d
    .line 764
    .local v21, "heightSizeAndState":I
    const v3, 0xffffff

    #@250
    and-int v20, v21, v3

    #@252
    .line 769
    move-object/from16 v0, p0

    #@254
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@256
    sub-int v18, v20, v3

    #@258
    .line 770
    .local v18, "delta":I
    if-nez v31, :cond_15

    #@25a
    if-eqz v18, :cond_24

    #@25c
    const/4 v3, 0x0

    #@25d
    cmpl-float v3, v33, v3

    #@25f
    if-lez v3, :cond_24

    #@261
    .line 771
    :cond_15
    move-object/from16 v0, p0

    #@263
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@265
    const/4 v6, 0x0

    #@266
    cmpl-float v3, v3, v6

    #@268
    if-lez v3, :cond_16

    #@26a
    move-object/from16 v0, p0

    #@26c
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@26e
    move/from16 v35, v0

    #@270
    .line 773
    .local v35, "weightSum":F
    :goto_a
    const/4 v3, 0x0

    #@271
    move-object/from16 v0, p0

    #@273
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@275
    .line 775
    const/4 v5, 0x0

    #@276
    :goto_b
    move/from16 v0, v17

    #@278
    if-ge v5, v0, :cond_20

    #@27a
    .line 776
    move-object/from16 v0, p0

    #@27c
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@27f
    move-result-object v4

    #@280
    .line 778
    .restart local v4    # "child":Landroid/view/View;
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@283
    move-result v3

    #@284
    const/16 v6, 0x8

    #@286
    if-ne v3, v6, :cond_17

    #@288
    .line 775
    :goto_c
    add-int/lit8 v5, v5, 0x1

    #@28a
    goto :goto_b

    #@28b
    .end local v4    # "child":Landroid/view/View;
    .end local v35    # "weightSum":F
    :cond_16
    move/from16 v35, v33

    #@28d
    .line 771
    goto :goto_a

    #@28e
    .line 782
    .restart local v4    # "child":Landroid/view/View;
    .restart local v35    # "weightSum":F
    :cond_17
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@291
    move-result-object v23

    #@292
    check-cast v23, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@294
    .line 784
    .restart local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v23

    #@296
    iget v13, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@298
    .line 785
    .local v13, "childExtra":F
    const/4 v3, 0x0

    #@299
    cmpl-float v3, v13, v3

    #@29b
    if-lez v3, :cond_1a

    #@29d
    .line 787
    move/from16 v0, v18

    #@29f
    int-to-float v3, v0

    #@2a0
    mul-float/2addr v3, v13

    #@2a1
    div-float v3, v3, v35

    #@2a3
    float-to-int v0, v3

    #@2a4
    move/from16 v30, v0

    #@2a6
    .line 788
    .local v30, "share":I
    sub-float v35, v35, v13

    #@2a8
    .line 789
    sub-int v18, v18, v30

    #@2aa
    .line 791
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@2ad
    move-result v3

    #@2ae
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@2b1
    move-result v6

    #@2b2
    add-int/2addr v3, v6

    #@2b3
    move-object/from16 v0, v23

    #@2b5
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@2b7
    add-int/2addr v3, v6

    #@2b8
    move-object/from16 v0, v23

    #@2ba
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@2bc
    add-int/2addr v3, v6

    #@2bd
    move-object/from16 v0, v23

    #@2bf
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@2c1
    move/from16 v0, p1

    #@2c3
    invoke-static {v0, v3, v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildMeasureSpec(III)I

    #@2c6
    move-result v16

    #@2c7
    .line 797
    .local v16, "childWidthMeasureSpec":I
    move-object/from16 v0, v23

    #@2c9
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->height:I

    #@2cb
    if-nez v3, :cond_18

    #@2cd
    const/high16 v3, 0x40000000    # 2.0f

    #@2cf
    move/from16 v0, v19

    #@2d1
    if-eq v0, v3, :cond_1b

    #@2d3
    .line 800
    :cond_18
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@2d6
    move-result v3

    #@2d7
    add-int v14, v3, v30

    #@2d9
    .line 801
    .restart local v14    # "childHeight":I
    if-gez v14, :cond_19

    #@2db
    .line 802
    const/4 v14, 0x0

    #@2dc
    .line 805
    :cond_19
    const/high16 v3, 0x40000000    # 2.0f

    #@2de
    invoke-static {v14, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@2e1
    move-result v3

    #@2e2
    move/from16 v0, v16

    #@2e4
    invoke-virtual {v4, v0, v3}, Landroid/view/View;->measure(II)V

    #@2e7
    .line 816
    .end local v14    # "childHeight":I
    .end local v30    # "share":I
    :goto_d
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@2ea
    move-result v3

    #@2eb
    and-int/lit16 v3, v3, -0x100

    #@2ed
    invoke-static {v15, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@2f0
    move-result v15

    #@2f1
    .line 821
    .end local v16    # "childWidthMeasureSpec":I
    :cond_1a
    move-object/from16 v0, v23

    #@2f3
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->leftMargin:I

    #@2f5
    move-object/from16 v0, v23

    #@2f7
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->rightMargin:I

    #@2f9
    add-int v24, v3, v6

    #@2fb
    .line 822
    .restart local v24    # "margin":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@2fe
    move-result v3

    #@2ff
    add-int v28, v3, v24

    #@301
    .line 823
    .restart local v28    # "measuredWidth":I
    invoke-static/range {v27 .. v28}, Ljava/lang/Math;->max(II)I

    #@304
    move-result v27

    #@305
    .line 825
    const/high16 v3, 0x40000000    # 2.0f

    #@307
    move/from16 v0, v37

    #@309
    if-eq v0, v3, :cond_1d

    #@30b
    move-object/from16 v0, v23

    #@30d
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@30f
    const/4 v6, -0x1

    #@310
    if-ne v3, v6, :cond_1d

    #@312
    const/16 v26, 0x1

    #@314
    .line 828
    .restart local v26    # "matchWidthLocally":Z
    :goto_e
    if-eqz v26, :cond_1e

    #@316
    .end local v24    # "margin":I
    :goto_f
    move/from16 v0, v24

    #@318
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@31b
    move-result v11

    #@31c
    .line 831
    if-eqz v10, :cond_1f

    #@31e
    move-object/from16 v0, v23

    #@320
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->width:I

    #@322
    const/4 v6, -0x1

    #@323
    if-ne v3, v6, :cond_1f

    #@325
    const/4 v10, 0x1

    #@326
    .line 833
    :goto_10
    move-object/from16 v0, p0

    #@328
    iget v0, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@32a
    move/from16 v32, v0

    #@32c
    .line 834
    .restart local v32    # "totalLength":I
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@32f
    move-result v3

    #@330
    add-int v3, v3, v32

    #@332
    move-object/from16 v0, v23

    #@334
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->topMargin:I

    #@336
    add-int/2addr v3, v6

    #@337
    move-object/from16 v0, v23

    #@339
    iget v6, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->bottomMargin:I

    #@33b
    add-int/2addr v3, v6

    #@33c
    move-object/from16 v0, p0

    #@33e
    invoke-virtual {v0, v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getNextLocationOffset(Landroid/view/View;)I

    #@341
    move-result v6

    #@342
    add-int/2addr v3, v6

    #@343
    move/from16 v0, v32

    #@345
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@348
    move-result v3

    #@349
    move-object/from16 v0, p0

    #@34b
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@34d
    goto/16 :goto_c

    #@34f
    .line 810
    .end local v26    # "matchWidthLocally":Z
    .end local v28    # "measuredWidth":I
    .end local v32    # "totalLength":I
    .restart local v16    # "childWidthMeasureSpec":I
    .restart local v30    # "share":I
    :cond_1b
    if-lez v30, :cond_1c

    #@351
    .end local v30    # "share":I
    :goto_11
    const/high16 v3, 0x40000000    # 2.0f

    #@353
    move/from16 v0, v30

    #@355
    invoke-static {v0, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@358
    move-result v3

    #@359
    move/from16 v0, v16

    #@35b
    invoke-virtual {v4, v0, v3}, Landroid/view/View;->measure(II)V

    #@35e
    goto :goto_d

    #@35f
    .restart local v30    # "share":I
    :cond_1c
    const/16 v30, 0x0

    #@361
    goto :goto_11

    #@362
    .line 825
    .end local v16    # "childWidthMeasureSpec":I
    .end local v30    # "share":I
    .restart local v24    # "margin":I
    .restart local v28    # "measuredWidth":I
    :cond_1d
    const/16 v26, 0x0

    #@364
    goto :goto_e

    #@365
    .restart local v26    # "matchWidthLocally":Z
    :cond_1e
    move/from16 v24, v28

    #@367
    .line 828
    goto :goto_f

    #@368
    .line 831
    .end local v24    # "margin":I
    :cond_1f
    const/4 v10, 0x0

    #@369
    goto :goto_10

    #@36a
    .line 839
    .end local v4    # "child":Landroid/view/View;
    .end local v13    # "childExtra":F
    .end local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v26    # "matchWidthLocally":Z
    .end local v28    # "measuredWidth":I
    :cond_20
    move-object/from16 v0, p0

    #@36c
    iget v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@36e
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingTop()I

    #@371
    move-result v6

    #@372
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingBottom()I

    #@375
    move-result v7

    #@376
    add-int/2addr v6, v7

    #@377
    add-int/2addr v3, v6

    #@378
    move-object/from16 v0, p0

    #@37a
    iput v3, v0, Landroid/support/v7/widget/LinearLayoutCompat;->mTotalLength:I

    #@37c
    .line 871
    .end local v35    # "weightSum":F
    :cond_21
    if-nez v10, :cond_22

    #@37e
    const/high16 v3, 0x40000000    # 2.0f

    #@380
    move/from16 v0, v37

    #@382
    if-eq v0, v3, :cond_22

    #@384
    .line 872
    move/from16 v27, v11

    #@386
    .line 875
    :cond_22
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingLeft()I

    #@389
    move-result v3

    #@38a
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getPaddingRight()I

    #@38d
    move-result v6

    #@38e
    add-int/2addr v3, v6

    #@38f
    add-int v27, v27, v3

    #@391
    .line 878
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getSuggestedMinimumWidth()I

    #@394
    move-result v3

    #@395
    move/from16 v0, v27

    #@397
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@39a
    move-result v27

    #@39b
    .line 880
    move/from16 v0, v27

    #@39d
    move/from16 v1, p1

    #@39f
    invoke-static {v0, v1, v15}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@3a2
    move-result v3

    #@3a3
    move-object/from16 v0, p0

    #@3a5
    move/from16 v1, v21

    #@3a7
    invoke-virtual {v0, v3, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->setMeasuredDimension(II)V

    #@3aa
    .line 883
    if-eqz v25, :cond_23

    #@3ac
    .line 884
    move-object/from16 v0, p0

    #@3ae
    move/from16 v1, v17

    #@3b0
    move/from16 v2, p2

    #@3b2
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->forceUniformWidth(II)V

    #@3b5
    .line 886
    :cond_23
    return-void

    #@3b6
    .line 842
    :cond_24
    move/from16 v0, v36

    #@3b8
    invoke-static {v11, v0}, Ljava/lang/Math;->max(II)I

    #@3bb
    move-result v11

    #@3bc
    .line 848
    if-eqz v34, :cond_21

    #@3be
    const/high16 v3, 0x40000000    # 2.0f

    #@3c0
    move/from16 v0, v19

    #@3c2
    if-eq v0, v3, :cond_21

    #@3c4
    .line 849
    const/4 v5, 0x0

    #@3c5
    :goto_12
    move/from16 v0, v17

    #@3c7
    if-ge v5, v0, :cond_21

    #@3c9
    .line 850
    move-object/from16 v0, p0

    #@3cb
    invoke-virtual {v0, v5}, Landroid/support/v7/widget/LinearLayoutCompat;->getVirtualChildAt(I)Landroid/view/View;

    #@3ce
    move-result-object v4

    #@3cf
    .line 852
    .restart local v4    # "child":Landroid/view/View;
    if-eqz v4, :cond_25

    #@3d1
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@3d4
    move-result v3

    #@3d5
    const/16 v6, 0x8

    #@3d7
    if-ne v3, v6, :cond_26

    #@3d9
    .line 849
    :cond_25
    :goto_13
    add-int/lit8 v5, v5, 0x1

    #@3db
    goto :goto_12

    #@3dc
    .line 856
    :cond_26
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3df
    move-result-object v23

    #@3e0
    check-cast v23, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@3e2
    .line 859
    .restart local v23    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    move-object/from16 v0, v23

    #@3e4
    iget v13, v0, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->weight:F

    #@3e6
    .line 860
    .restart local v13    # "childExtra":F
    const/4 v3, 0x0

    #@3e7
    cmpl-float v3, v13, v3

    #@3e9
    if-lez v3, :cond_25

    #@3eb
    .line 861
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@3ee
    move-result v3

    #@3ef
    const/high16 v6, 0x40000000    # 2.0f

    #@3f1
    invoke-static {v3, v6}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@3f4
    move-result v3

    #@3f5
    const/high16 v6, 0x40000000    # 2.0f

    #@3f7
    move/from16 v0, v22

    #@3f9
    invoke-static {v0, v6}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@3fc
    move-result v6

    #@3fd
    invoke-virtual {v4, v3, v6}, Landroid/view/View;->measure(II)V

    #@400
    goto :goto_13
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 2
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 279
    iget-object v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 288
    :goto_0
    return-void

    #@5
    .line 283
    :cond_0
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@7
    const/4 v1, 0x1

    #@8
    if-ne v0, v1, :cond_1

    #@a
    .line 284
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->drawDividersVertical(Landroid/graphics/Canvas;)V

    #@d
    goto :goto_0

    #@e
    .line 286
    :cond_1
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->drawDividersHorizontal(Landroid/graphics/Canvas;)V

    #@11
    goto :goto_0
.end method

.method public onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V
    .locals 2
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 1741
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 1742
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    #@9
    .line 1743
    const-class v0, Landroid/support/v7/widget/LinearLayoutCompat;

    #@b
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@e
    move-result-object v0

    #@f
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setClassName(Ljava/lang/CharSequence;)V

    #@12
    .line 1745
    :cond_0
    return-void
.end method

.method public onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V
    .locals 2
    .param p1, "info"    # Landroid/view/accessibility/AccessibilityNodeInfo;

    #@0
    .prologue
    .line 1748
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 1749
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V

    #@9
    .line 1750
    const-class v0, Landroid/support/v7/widget/LinearLayoutCompat;

    #@b
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@e
    move-result-object v0

    #@f
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityNodeInfo;->setClassName(Ljava/lang/CharSequence;)V

    #@12
    .line 1752
    :cond_0
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 2
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 1402
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@2
    const/4 v1, 0x1

    #@3
    if-ne v0, v1, :cond_0

    #@5
    .line 1403
    invoke-virtual {p0, p2, p3, p4, p5}, Landroid/support/v7/widget/LinearLayoutCompat;->layoutVertical(IIII)V

    #@8
    .line 1407
    :goto_0
    return-void

    #@9
    .line 1405
    :cond_0
    invoke-virtual {p0, p2, p3, p4, p5}, Landroid/support/v7/widget/LinearLayoutCompat;->layoutHorizontal(IIII)V

    #@c
    goto :goto_0
.end method

.method protected onMeasure(II)V
    .locals 2
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 546
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@2
    const/4 v1, 0x1

    #@3
    if-ne v0, v1, :cond_0

    #@5
    .line 547
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->measureVertical(II)V

    #@8
    .line 551
    :goto_0
    return-void

    #@9
    .line 549
    :cond_0
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->measureHorizontal(II)V

    #@c
    goto :goto_0
.end method

.method public setBaselineAligned(Z)V
    .locals 0
    .param p1, "baselineAligned"    # Z

    #@0
    .prologue
    .line 388
    iput-boolean p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAligned:Z

    #@2
    .line 389
    return-void
.end method

.method public setBaselineAlignedChildIndex(I)V
    .locals 3
    .param p1, "i"    # I

    #@0
    .prologue
    .line 484
    if-ltz p1, :cond_0

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@5
    move-result v0

    #@6
    if-lt p1, v0, :cond_1

    #@8
    .line 485
    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@a
    new-instance v1, Ljava/lang/StringBuilder;

    #@c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@f
    const-string v2, "base aligned child index out of range (0, "

    #@11
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@18
    move-result v2

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    const-string v2, ")"

    #@1f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v1

    #@23
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@26
    move-result-object v1

    #@27
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@2a
    throw v0

    #@2b
    .line 488
    :cond_1
    iput p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mBaselineAlignedChildIndex:I

    #@2d
    .line 489
    return-void
.end method

.method public setDividerDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 2
    .param p1, "divider"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 229
    iget-object v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@3
    if-ne p1, v1, :cond_0

    #@5
    .line 242
    :goto_0
    return-void

    #@6
    .line 232
    :cond_0
    iput-object p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDivider:Landroid/graphics/drawable/Drawable;

    #@8
    .line 233
    if-eqz p1, :cond_2

    #@a
    .line 234
    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@d
    move-result v1

    #@e
    iput v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@10
    .line 235
    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@13
    move-result v1

    #@14
    iput v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@16
    .line 240
    :goto_1
    if-nez p1, :cond_1

    #@18
    const/4 v0, 0x1

    #@19
    :cond_1
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/LinearLayoutCompat;->setWillNotDraw(Z)V

    #@1c
    .line 241
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@1f
    goto :goto_0

    #@20
    .line 237
    :cond_2
    iput v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerWidth:I

    #@22
    .line 238
    iput v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerHeight:I

    #@24
    goto :goto_1
.end method

.method public setDividerPadding(I)V
    .locals 0
    .param p1, "padding"    # I

    #@0
    .prologue
    .line 254
    iput p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mDividerPadding:I

    #@2
    .line 255
    return-void
.end method

.method public setGravity(I)V
    .locals 1
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1675
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@2
    if-eq v0, p1, :cond_2

    #@4
    .line 1676
    const v0, 0x800007

    #@7
    and-int/2addr v0, p1

    #@8
    if-nez v0, :cond_0

    #@a
    .line 1677
    const v0, 0x800003

    #@d
    or-int/2addr p1, v0

    #@e
    .line 1680
    :cond_0
    and-int/lit8 v0, p1, 0x70

    #@10
    if-nez v0, :cond_1

    #@12
    .line 1681
    or-int/lit8 p1, p1, 0x30

    #@14
    .line 1684
    :cond_1
    iput p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@16
    .line 1685
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@19
    .line 1687
    :cond_2
    return-void
.end method

.method public setHorizontalGravity(I)V
    .locals 3
    .param p1, "horizontalGravity"    # I

    #@0
    .prologue
    const v2, 0x800007

    #@3
    .line 1690
    and-int v0, p1, v2

    #@5
    .line 1691
    .local v0, "gravity":I
    iget v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@7
    and-int/2addr v1, v2

    #@8
    if-eq v1, v0, :cond_0

    #@a
    .line 1692
    iget v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@c
    const v2, -0x800008

    #@f
    and-int/2addr v1, v2

    #@10
    or-int/2addr v1, v0

    #@11
    iput v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@13
    .line 1693
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@16
    .line 1695
    :cond_0
    return-void
.end method

.method public setMeasureWithLargestChildEnabled(Z)V
    .locals 0
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 414
    iput-boolean p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mUseLargestChild:Z

    #@2
    .line 415
    return-void
.end method

.method public setOrientation(I)V
    .locals 1
    .param p1, "orientation"    # I

    #@0
    .prologue
    .line 1650
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 1651
    iput p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mOrientation:I

    #@6
    .line 1652
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@9
    .line 1654
    :cond_0
    return-void
.end method

.method public setShowDividers(I)V
    .locals 1
    .param p1, "showDividers"    # I

    #@0
    .prologue
    .line 192
    iget v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@2
    if-eq p1, v0, :cond_0

    #@4
    .line 193
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@7
    .line 195
    :cond_0
    iput p1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mShowDividers:I

    #@9
    .line 196
    return-void
.end method

.method public setVerticalGravity(I)V
    .locals 2
    .param p1, "verticalGravity"    # I

    #@0
    .prologue
    .line 1698
    and-int/lit8 v0, p1, 0x70

    #@2
    .line 1699
    .local v0, "gravity":I
    iget v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@4
    and-int/lit8 v1, v1, 0x70

    #@6
    if-eq v1, v0, :cond_0

    #@8
    .line 1700
    iget v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@a
    and-int/lit8 v1, v1, -0x71

    #@c
    or-int/2addr v1, v0

    #@d
    iput v1, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mGravity:I

    #@f
    .line 1701
    invoke-virtual {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->requestLayout()V

    #@12
    .line 1703
    :cond_0
    return-void
.end method

.method public setWeightSum(F)V
    .locals 1
    .param p1, "weightSum"    # F

    #@0
    .prologue
    .line 541
    const/4 v0, 0x0

    #@1
    invoke-static {v0, p1}, Ljava/lang/Math;->max(FF)F

    #@4
    move-result v0

    #@5
    iput v0, p0, Landroid/support/v7/widget/LinearLayoutCompat;->mWeightSum:F

    #@7
    .line 542
    return-void
.end method

.method public shouldDelayChildPressedState()Z
    .locals 1

    #@0
    .prologue
    .line 200
    const/4 v0, 0x0

    #@1
    return v0
.end method
