.class public Landroid/support/v7/widget/ActionMenuView;
.super Landroid/support/v7/widget/LinearLayoutCompat;
.source "ActionMenuView.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;
.implements Landroid/support/v7/internal/view/menu/MenuView;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/ActionMenuView$1;,
        Landroid/support/v7/widget/ActionMenuView$LayoutParams;,
        Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;,
        Landroid/support/v7/widget/ActionMenuView$ActionMenuPresenterCallback;,
        Landroid/support/v7/widget/ActionMenuView$MenuBuilderCallback;,
        Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;
    }
.end annotation


# static fields
.field static final GENERATED_ITEM_PADDING:I = 0x4

.field static final MIN_CELL_SIZE:I = 0x38

.field private static final TAG:Ljava/lang/String; = "ActionMenuView"


# instance fields
.field private mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

.field private mContext:Landroid/content/Context;

.field private mFormatItems:Z

.field private mFormatItemsWidth:I

.field private mGeneratedItemPadding:I

.field private mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

.field private mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

.field private mMinCellSize:I

.field private mOnMenuItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

.field private mPopupContext:Landroid/content/Context;

.field private mPopupTheme:I

.field private mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

.field private mReserveOverflow:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 73
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/ActionMenuView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 74
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 77
    invoke-direct {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 78
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mContext:Landroid/content/Context;

    #@6
    .line 79
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuView;->setBaselineAligned(Z)V

    #@9
    .line 80
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@c
    move-result-object v1

    #@d
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@10
    move-result-object v1

    #@11
    iget v0, v1, Landroid/util/DisplayMetrics;->density:F

    #@13
    .line 81
    .local v0, "density":F
    const/high16 v1, 0x42600000    # 56.0f

    #@15
    mul-float/2addr v1, v0

    #@16
    float-to-int v1, v1

    #@17
    iput v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMinCellSize:I

    #@19
    .line 82
    const/high16 v1, 0x40800000    # 4.0f

    #@1b
    mul-float/2addr v1, v0

    #@1c
    float-to-int v1, v1

    #@1d
    iput v1, p0, Landroid/support/v7/widget/ActionMenuView;->mGeneratedItemPadding:I

    #@1f
    .line 83
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupContext:Landroid/content/Context;

    #@21
    .line 84
    iput v2, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupTheme:I

    #@23
    .line 85
    return-void
.end method

.method static synthetic access$200(Landroid/support/v7/widget/ActionMenuView;)Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuView;

    #@0
    .prologue
    .line 43
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mOnMenuItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/widget/ActionMenuView;)Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ActionMenuView;

    #@0
    .prologue
    .line 43
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@2
    return-object v0
.end method

.method static measureChildForCells(Landroid/view/View;IIII)I
    .locals 13
    .param p0, "child"    # Landroid/view/View;
    .param p1, "cellSize"    # I
    .param p2, "cellsRemaining"    # I
    .param p3, "parentHeightMeasureSpec"    # I
    .param p4, "parentHeightPadding"    # I

    #@0
    .prologue
    .line 401
    invoke-virtual {p0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v8

    #@4
    check-cast v8, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@6
    .line 403
    .local v8, "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    invoke-static/range {p3 .. p3}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@9
    move-result v11

    #@a
    sub-int v2, v11, p4

    #@c
    .line 405
    .local v2, "childHeightSize":I
    invoke-static/range {p3 .. p3}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@f
    move-result v1

    #@10
    .line 406
    .local v1, "childHeightMode":I
    invoke-static {v2, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@13
    move-result v3

    #@14
    .line 408
    .local v3, "childHeightSpec":I
    instance-of v11, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@16
    if-eqz v11, :cond_3

    #@18
    move-object v11, p0

    #@19
    check-cast v11, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@1b
    move-object v7, v11

    #@1c
    .line 410
    .local v7, "itemView":Landroid/support/v7/internal/view/menu/ActionMenuItemView;
    :goto_0
    if-eqz v7, :cond_4

    #@1e
    invoke-virtual {v7}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@21
    move-result v11

    #@22
    if-eqz v11, :cond_4

    #@24
    const/4 v6, 0x1

    #@25
    .line 412
    .local v6, "hasText":Z
    :goto_1
    const/4 v0, 0x0

    #@26
    .line 413
    .local v0, "cellsUsed":I
    if-lez p2, :cond_2

    #@28
    if-eqz v6, :cond_0

    #@2a
    const/4 v11, 0x2

    #@2b
    if-lt p2, v11, :cond_2

    #@2d
    .line 414
    :cond_0
    mul-int v11, p1, p2

    #@2f
    const/high16 v12, -0x80000000

    #@31
    invoke-static {v11, v12}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@34
    move-result v4

    #@35
    .line 416
    .local v4, "childWidthSpec":I
    invoke-virtual {p0, v4, v3}, Landroid/view/View;->measure(II)V

    #@38
    .line 418
    invoke-virtual {p0}, Landroid/view/View;->getMeasuredWidth()I

    #@3b
    move-result v9

    #@3c
    .line 419
    .local v9, "measuredWidth":I
    div-int v0, v9, p1

    #@3e
    .line 420
    rem-int v11, v9, p1

    #@40
    if-eqz v11, :cond_1

    #@42
    add-int/lit8 v0, v0, 0x1

    #@44
    .line 421
    :cond_1
    if-eqz v6, :cond_2

    #@46
    const/4 v11, 0x2

    #@47
    if-ge v0, v11, :cond_2

    #@49
    const/4 v0, 0x2

    #@4a
    .line 424
    .end local v4    # "childWidthSpec":I
    .end local v9    # "measuredWidth":I
    :cond_2
    iget-boolean v11, v8, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@4c
    if-nez v11, :cond_5

    #@4e
    if-eqz v6, :cond_5

    #@50
    const/4 v5, 0x1

    #@51
    .line 425
    .local v5, "expandable":Z
    :goto_2
    iput-boolean v5, v8, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expandable:Z

    #@53
    .line 427
    iput v0, v8, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@55
    .line 428
    mul-int v10, v0, p1

    #@57
    .line 429
    .local v10, "targetWidth":I
    const/high16 v11, 0x40000000    # 2.0f

    #@59
    invoke-static {v10, v11}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@5c
    move-result v11

    #@5d
    invoke-virtual {p0, v11, v3}, Landroid/view/View;->measure(II)V

    #@60
    .line 431
    return v0

    #@61
    .line 408
    .end local v0    # "cellsUsed":I
    .end local v5    # "expandable":Z
    .end local v6    # "hasText":Z
    .end local v7    # "itemView":Landroid/support/v7/internal/view/menu/ActionMenuItemView;
    .end local v10    # "targetWidth":I
    :cond_3
    const/4 v7, 0x0

    #@62
    goto :goto_0

    #@63
    .line 410
    .restart local v7    # "itemView":Landroid/support/v7/internal/view/menu/ActionMenuItemView;
    :cond_4
    const/4 v6, 0x0

    #@64
    goto :goto_1

    #@65
    .line 424
    .restart local v0    # "cellsUsed":I
    .restart local v6    # "hasText":Z
    :cond_5
    const/4 v5, 0x0

    #@66
    goto :goto_2
.end method

.method private onMeasureExactFormat(II)V
    .locals 42
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 175
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v17

    #@4
    .line 176
    .local v17, "heightMode":I
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@7
    move-result v37

    #@8
    .line 177
    .local v37, "widthSize":I
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@b
    move-result v19

    #@c
    .line 179
    .local v19, "heightSize":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingLeft()I

    #@f
    move-result v38

    #@10
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingRight()I

    #@13
    move-result v39

    #@14
    add-int v36, v38, v39

    #@16
    .line 180
    .local v36, "widthPadding":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingTop()I

    #@19
    move-result v38

    #@1a
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingBottom()I

    #@1d
    move-result v39

    #@1e
    add-int v18, v38, v39

    #@20
    .line 182
    .local v18, "heightPadding":I
    const/16 v38, -0x2

    #@22
    move/from16 v0, p2

    #@24
    move/from16 v1, v18

    #@26
    move/from16 v2, v38

    #@28
    invoke-static {v0, v1, v2}, Landroid/support/v7/widget/ActionMenuView;->getChildMeasureSpec(III)I

    #@2b
    move-result v22

    #@2c
    .line 185
    .local v22, "itemHeightSpec":I
    sub-int v37, v37, v36

    #@2e
    .line 188
    move-object/from16 v0, p0

    #@30
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mMinCellSize:I

    #@32
    move/from16 v38, v0

    #@34
    div-int v4, v37, v38

    #@36
    .line 189
    .local v4, "cellCount":I
    move-object/from16 v0, p0

    #@38
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mMinCellSize:I

    #@3a
    move/from16 v38, v0

    #@3c
    rem-int v6, v37, v38

    #@3e
    .line 191
    .local v6, "cellSizeRemaining":I
    if-nez v4, :cond_0

    #@40
    .line 193
    const/16 v38, 0x0

    #@42
    move-object/from16 v0, p0

    #@44
    move/from16 v1, v37

    #@46
    move/from16 v2, v38

    #@48
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setMeasuredDimension(II)V

    #@4b
    .line 384
    :goto_0
    return-void

    #@4c
    .line 197
    :cond_0
    move-object/from16 v0, p0

    #@4e
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mMinCellSize:I

    #@50
    move/from16 v38, v0

    #@52
    div-int v39, v6, v4

    #@54
    add-int v5, v38, v39

    #@56
    .line 199
    .local v5, "cellSize":I
    move v8, v4

    #@57
    .line 200
    .local v8, "cellsRemaining":I
    const/16 v25, 0x0

    #@59
    .line 201
    .local v25, "maxChildHeight":I
    const/16 v24, 0x0

    #@5b
    .line 202
    .local v24, "maxCellsUsed":I
    const/4 v14, 0x0

    #@5c
    .line 203
    .local v14, "expandableItemCount":I
    const/16 v34, 0x0

    #@5e
    .line 204
    .local v34, "visibleItemCount":I
    const/16 v16, 0x0

    #@60
    .line 207
    .local v16, "hasOverflow":Z
    const-wide/16 v32, 0x0

    #@62
    .line 209
    .local v32, "smallestItemsAt":J
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@65
    move-result v12

    #@66
    .line 210
    .local v12, "childCount":I
    const/16 v20, 0x0

    #@68
    .local v20, "i":I
    :goto_1
    move/from16 v0, v20

    #@6a
    if-ge v0, v12, :cond_8

    #@6c
    .line 211
    move-object/from16 v0, p0

    #@6e
    move/from16 v1, v20

    #@70
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@73
    move-result-object v11

    #@74
    .line 212
    .local v11, "child":Landroid/view/View;
    invoke-virtual {v11}, Landroid/view/View;->getVisibility()I

    #@77
    move-result v38

    #@78
    const/16 v39, 0x8

    #@7a
    move/from16 v0, v38

    #@7c
    move/from16 v1, v39

    #@7e
    if-ne v0, v1, :cond_2

    #@80
    .line 210
    :cond_1
    :goto_2
    add-int/lit8 v20, v20, 0x1

    #@82
    goto :goto_1

    #@83
    .line 214
    :cond_2
    instance-of v0, v11, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@85
    move/from16 v21, v0

    #@87
    .line 215
    .local v21, "isGeneratedItem":Z
    add-int/lit8 v34, v34, 0x1

    #@89
    .line 217
    if-eqz v21, :cond_3

    #@8b
    .line 220
    move-object/from16 v0, p0

    #@8d
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mGeneratedItemPadding:I

    #@8f
    move/from16 v38, v0

    #@91
    const/16 v39, 0x0

    #@93
    move-object/from16 v0, p0

    #@95
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mGeneratedItemPadding:I

    #@97
    move/from16 v40, v0

    #@99
    const/16 v41, 0x0

    #@9b
    move/from16 v0, v38

    #@9d
    move/from16 v1, v39

    #@9f
    move/from16 v2, v40

    #@a1
    move/from16 v3, v41

    #@a3
    invoke-virtual {v11, v0, v1, v2, v3}, Landroid/view/View;->setPadding(IIII)V

    #@a6
    .line 223
    :cond_3
    invoke-virtual {v11}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@a9
    move-result-object v23

    #@aa
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@ac
    .line 224
    .local v23, "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    const/16 v38, 0x0

    #@ae
    move/from16 v0, v38

    #@b0
    move-object/from16 v1, v23

    #@b2
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expanded:Z

    #@b4
    .line 225
    const/16 v38, 0x0

    #@b6
    move/from16 v0, v38

    #@b8
    move-object/from16 v1, v23

    #@ba
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->extraPixels:I

    #@bc
    .line 226
    const/16 v38, 0x0

    #@be
    move/from16 v0, v38

    #@c0
    move-object/from16 v1, v23

    #@c2
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@c4
    .line 227
    const/16 v38, 0x0

    #@c6
    move/from16 v0, v38

    #@c8
    move-object/from16 v1, v23

    #@ca
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expandable:Z

    #@cc
    .line 228
    const/16 v38, 0x0

    #@ce
    move/from16 v0, v38

    #@d0
    move-object/from16 v1, v23

    #@d2
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@d4
    .line 229
    const/16 v38, 0x0

    #@d6
    move/from16 v0, v38

    #@d8
    move-object/from16 v1, v23

    #@da
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@dc
    .line 230
    if-eqz v21, :cond_6

    #@de
    move-object/from16 v38, v11

    #@e0
    check-cast v38, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@e2
    invoke-virtual/range {v38 .. v38}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@e5
    move-result v38

    #@e6
    if-eqz v38, :cond_6

    #@e8
    const/16 v38, 0x1

    #@ea
    :goto_3
    move/from16 v0, v38

    #@ec
    move-object/from16 v1, v23

    #@ee
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->preventEdgeOffset:Z

    #@f0
    .line 233
    move-object/from16 v0, v23

    #@f2
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@f4
    move/from16 v38, v0

    #@f6
    if-eqz v38, :cond_7

    #@f8
    const/4 v7, 0x1

    #@f9
    .line 235
    .local v7, "cellsAvailable":I
    :goto_4
    move/from16 v0, v22

    #@fb
    move/from16 v1, v18

    #@fd
    invoke-static {v11, v5, v7, v0, v1}, Landroid/support/v7/widget/ActionMenuView;->measureChildForCells(Landroid/view/View;IIII)I

    #@100
    move-result v9

    #@101
    .line 238
    .local v9, "cellsUsed":I
    move/from16 v0, v24

    #@103
    invoke-static {v0, v9}, Ljava/lang/Math;->max(II)I

    #@106
    move-result v24

    #@107
    .line 239
    move-object/from16 v0, v23

    #@109
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expandable:Z

    #@10b
    move/from16 v38, v0

    #@10d
    if-eqz v38, :cond_4

    #@10f
    add-int/lit8 v14, v14, 0x1

    #@111
    .line 240
    :cond_4
    move-object/from16 v0, v23

    #@113
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@115
    move/from16 v38, v0

    #@117
    if-eqz v38, :cond_5

    #@119
    const/16 v16, 0x1

    #@11b
    .line 242
    :cond_5
    sub-int/2addr v8, v9

    #@11c
    .line 243
    invoke-virtual {v11}, Landroid/view/View;->getMeasuredHeight()I

    #@11f
    move-result v38

    #@120
    move/from16 v0, v25

    #@122
    move/from16 v1, v38

    #@124
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@127
    move-result v25

    #@128
    .line 244
    const/16 v38, 0x1

    #@12a
    move/from16 v0, v38

    #@12c
    if-ne v9, v0, :cond_1

    #@12e
    const/16 v38, 0x1

    #@130
    shl-int v38, v38, v20

    #@132
    move/from16 v0, v38

    #@134
    int-to-long v0, v0

    #@135
    move-wide/from16 v38, v0

    #@137
    or-long v32, v32, v38

    #@139
    goto/16 :goto_2

    #@13b
    .line 230
    .end local v7    # "cellsAvailable":I
    .end local v9    # "cellsUsed":I
    :cond_6
    const/16 v38, 0x0

    #@13d
    goto :goto_3

    #@13e
    :cond_7
    move v7, v8

    #@13f
    .line 233
    goto :goto_4

    #@140
    .line 249
    .end local v11    # "child":Landroid/view/View;
    .end local v21    # "isGeneratedItem":Z
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_8
    if-eqz v16, :cond_a

    #@142
    const/16 v38, 0x2

    #@144
    move/from16 v0, v34

    #@146
    move/from16 v1, v38

    #@148
    if-ne v0, v1, :cond_a

    #@14a
    const/4 v10, 0x1

    #@14b
    .line 254
    .local v10, "centerSingleExpandedItem":Z
    :goto_5
    const/16 v30, 0x0

    #@14d
    .line 255
    .local v30, "needsExpansion":Z
    :goto_6
    if-lez v14, :cond_e

    #@14f
    if-lez v8, :cond_e

    #@151
    .line 256
    const v26, 0x7fffffff

    #@154
    .line 257
    .local v26, "minCells":I
    const-wide/16 v28, 0x0

    #@156
    .line 258
    .local v28, "minCellsAt":J
    const/16 v27, 0x0

    #@158
    .line 259
    .local v27, "minCellsItemCount":I
    const/16 v20, 0x0

    #@15a
    :goto_7
    move/from16 v0, v20

    #@15c
    if-ge v0, v12, :cond_d

    #@15e
    .line 260
    move-object/from16 v0, p0

    #@160
    move/from16 v1, v20

    #@162
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@165
    move-result-object v11

    #@166
    .line 261
    .restart local v11    # "child":Landroid/view/View;
    invoke-virtual {v11}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@169
    move-result-object v23

    #@16a
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@16c
    .line 264
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    move-object/from16 v0, v23

    #@16e
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expandable:Z

    #@170
    move/from16 v38, v0

    #@172
    if-nez v38, :cond_b

    #@174
    .line 259
    :cond_9
    :goto_8
    add-int/lit8 v20, v20, 0x1

    #@176
    goto :goto_7

    #@177
    .line 249
    .end local v10    # "centerSingleExpandedItem":Z
    .end local v11    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .end local v26    # "minCells":I
    .end local v27    # "minCellsItemCount":I
    .end local v28    # "minCellsAt":J
    .end local v30    # "needsExpansion":Z
    :cond_a
    const/4 v10, 0x0

    #@178
    goto :goto_5

    #@179
    .line 267
    .restart local v10    # "centerSingleExpandedItem":Z
    .restart local v11    # "child":Landroid/view/View;
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .restart local v26    # "minCells":I
    .restart local v27    # "minCellsItemCount":I
    .restart local v28    # "minCellsAt":J
    .restart local v30    # "needsExpansion":Z
    :cond_b
    move-object/from16 v0, v23

    #@17b
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@17d
    move/from16 v38, v0

    #@17f
    move/from16 v0, v38

    #@181
    move/from16 v1, v26

    #@183
    if-ge v0, v1, :cond_c

    #@185
    .line 268
    move-object/from16 v0, v23

    #@187
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@189
    move/from16 v26, v0

    #@18b
    .line 269
    const/16 v38, 0x1

    #@18d
    shl-int v38, v38, v20

    #@18f
    move/from16 v0, v38

    #@191
    int-to-long v0, v0

    #@192
    move-wide/from16 v28, v0

    #@194
    .line 270
    const/16 v27, 0x1

    #@196
    goto :goto_8

    #@197
    .line 271
    :cond_c
    move-object/from16 v0, v23

    #@199
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@19b
    move/from16 v38, v0

    #@19d
    move/from16 v0, v38

    #@19f
    move/from16 v1, v26

    #@1a1
    if-ne v0, v1, :cond_9

    #@1a3
    .line 272
    const/16 v38, 0x1

    #@1a5
    shl-int v38, v38, v20

    #@1a7
    move/from16 v0, v38

    #@1a9
    int-to-long v0, v0

    #@1aa
    move-wide/from16 v38, v0

    #@1ac
    or-long v28, v28, v38

    #@1ae
    .line 273
    add-int/lit8 v27, v27, 0x1

    #@1b0
    goto :goto_8

    #@1b1
    .line 278
    .end local v11    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_d
    or-long v32, v32, v28

    #@1b3
    .line 280
    move/from16 v0, v27

    #@1b5
    if-le v0, v8, :cond_13

    #@1b7
    .line 309
    .end local v26    # "minCells":I
    .end local v27    # "minCellsItemCount":I
    .end local v28    # "minCellsAt":J
    :cond_e
    if-nez v16, :cond_18

    #@1b9
    const/16 v38, 0x1

    #@1bb
    move/from16 v0, v34

    #@1bd
    move/from16 v1, v38

    #@1bf
    if-ne v0, v1, :cond_18

    #@1c1
    const/16 v31, 0x1

    #@1c3
    .line 310
    .local v31, "singleItem":Z
    :goto_9
    if-lez v8, :cond_20

    #@1c5
    const-wide/16 v38, 0x0

    #@1c7
    cmp-long v38, v32, v38

    #@1c9
    if-eqz v38, :cond_20

    #@1cb
    add-int/lit8 v38, v34, -0x1

    #@1cd
    move/from16 v0, v38

    #@1cf
    if-lt v8, v0, :cond_f

    #@1d1
    if-nez v31, :cond_f

    #@1d3
    const/16 v38, 0x1

    #@1d5
    move/from16 v0, v24

    #@1d7
    move/from16 v1, v38

    #@1d9
    if-le v0, v1, :cond_20

    #@1db
    .line 312
    :cond_f
    invoke-static/range {v32 .. v33}, Ljava/lang/Long;->bitCount(J)I

    #@1de
    move-result v38

    #@1df
    move/from16 v0, v38

    #@1e1
    int-to-float v13, v0

    #@1e2
    .line 314
    .local v13, "expandCount":F
    if-nez v31, :cond_11

    #@1e4
    .line 316
    const-wide/16 v38, 0x1

    #@1e6
    and-long v38, v38, v32

    #@1e8
    const-wide/16 v40, 0x0

    #@1ea
    cmp-long v38, v38, v40

    #@1ec
    if-eqz v38, :cond_10

    #@1ee
    .line 317
    const/16 v38, 0x0

    #@1f0
    move-object/from16 v0, p0

    #@1f2
    move/from16 v1, v38

    #@1f4
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@1f7
    move-result-object v38

    #@1f8
    invoke-virtual/range {v38 .. v38}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1fb
    move-result-object v23

    #@1fc
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@1fe
    .line 318
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    move-object/from16 v0, v23

    #@200
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->preventEdgeOffset:Z

    #@202
    move/from16 v38, v0

    #@204
    if-nez v38, :cond_10

    #@206
    const/high16 v38, 0x3f000000    # 0.5f

    #@208
    sub-float v13, v13, v38

    #@20a
    .line 320
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_10
    const/16 v38, 0x1

    #@20c
    add-int/lit8 v39, v12, -0x1

    #@20e
    shl-int v38, v38, v39

    #@210
    move/from16 v0, v38

    #@212
    int-to-long v0, v0

    #@213
    move-wide/from16 v38, v0

    #@215
    and-long v38, v38, v32

    #@217
    const-wide/16 v40, 0x0

    #@219
    cmp-long v38, v38, v40

    #@21b
    if-eqz v38, :cond_11

    #@21d
    .line 321
    add-int/lit8 v38, v12, -0x1

    #@21f
    move-object/from16 v0, p0

    #@221
    move/from16 v1, v38

    #@223
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@226
    move-result-object v38

    #@227
    invoke-virtual/range {v38 .. v38}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@22a
    move-result-object v23

    #@22b
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@22d
    .line 322
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    move-object/from16 v0, v23

    #@22f
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->preventEdgeOffset:Z

    #@231
    move/from16 v38, v0

    #@233
    if-nez v38, :cond_11

    #@235
    const/high16 v38, 0x3f000000    # 0.5f

    #@237
    sub-float v13, v13, v38

    #@239
    .line 326
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_11
    const/16 v38, 0x0

    #@23b
    cmpl-float v38, v13, v38

    #@23d
    if-lez v38, :cond_19

    #@23f
    mul-int v38, v8, v5

    #@241
    move/from16 v0, v38

    #@243
    int-to-float v0, v0

    #@244
    move/from16 v38, v0

    #@246
    div-float v38, v38, v13

    #@248
    move/from16 v0, v38

    #@24a
    float-to-int v15, v0

    #@24b
    .line 329
    .local v15, "extraPixels":I
    :goto_a
    const/16 v20, 0x0

    #@24d
    :goto_b
    move/from16 v0, v20

    #@24f
    if-ge v0, v12, :cond_1f

    #@251
    .line 330
    const/16 v38, 0x1

    #@253
    shl-int v38, v38, v20

    #@255
    move/from16 v0, v38

    #@257
    int-to-long v0, v0

    #@258
    move-wide/from16 v38, v0

    #@25a
    and-long v38, v38, v32

    #@25c
    const-wide/16 v40, 0x0

    #@25e
    cmp-long v38, v38, v40

    #@260
    if-nez v38, :cond_1a

    #@262
    .line 329
    :cond_12
    :goto_c
    add-int/lit8 v20, v20, 0x1

    #@264
    goto :goto_b

    #@265
    .line 283
    .end local v13    # "expandCount":F
    .end local v15    # "extraPixels":I
    .end local v31    # "singleItem":Z
    .restart local v26    # "minCells":I
    .restart local v27    # "minCellsItemCount":I
    .restart local v28    # "minCellsAt":J
    :cond_13
    add-int/lit8 v26, v26, 0x1

    #@267
    .line 285
    const/16 v20, 0x0

    #@269
    :goto_d
    move/from16 v0, v20

    #@26b
    if-ge v0, v12, :cond_17

    #@26d
    .line 286
    move-object/from16 v0, p0

    #@26f
    move/from16 v1, v20

    #@271
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@274
    move-result-object v11

    #@275
    .line 287
    .restart local v11    # "child":Landroid/view/View;
    invoke-virtual {v11}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@278
    move-result-object v23

    #@279
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@27b
    .line 288
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    const/16 v38, 0x1

    #@27d
    shl-int v38, v38, v20

    #@27f
    move/from16 v0, v38

    #@281
    int-to-long v0, v0

    #@282
    move-wide/from16 v38, v0

    #@284
    and-long v38, v38, v28

    #@286
    const-wide/16 v40, 0x0

    #@288
    cmp-long v38, v38, v40

    #@28a
    if-nez v38, :cond_15

    #@28c
    .line 290
    move-object/from16 v0, v23

    #@28e
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@290
    move/from16 v38, v0

    #@292
    move/from16 v0, v38

    #@294
    move/from16 v1, v26

    #@296
    if-ne v0, v1, :cond_14

    #@298
    const/16 v38, 0x1

    #@29a
    shl-int v38, v38, v20

    #@29c
    move/from16 v0, v38

    #@29e
    int-to-long v0, v0

    #@29f
    move-wide/from16 v38, v0

    #@2a1
    or-long v32, v32, v38

    #@2a3
    .line 285
    :cond_14
    :goto_e
    add-int/lit8 v20, v20, 0x1

    #@2a5
    goto :goto_d

    #@2a6
    .line 294
    :cond_15
    if-eqz v10, :cond_16

    #@2a8
    move-object/from16 v0, v23

    #@2aa
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->preventEdgeOffset:Z

    #@2ac
    move/from16 v38, v0

    #@2ae
    if-eqz v38, :cond_16

    #@2b0
    const/16 v38, 0x1

    #@2b2
    move/from16 v0, v38

    #@2b4
    if-ne v8, v0, :cond_16

    #@2b6
    .line 296
    move-object/from16 v0, p0

    #@2b8
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mGeneratedItemPadding:I

    #@2ba
    move/from16 v38, v0

    #@2bc
    add-int v38, v38, v5

    #@2be
    const/16 v39, 0x0

    #@2c0
    move-object/from16 v0, p0

    #@2c2
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView;->mGeneratedItemPadding:I

    #@2c4
    move/from16 v40, v0

    #@2c6
    const/16 v41, 0x0

    #@2c8
    move/from16 v0, v38

    #@2ca
    move/from16 v1, v39

    #@2cc
    move/from16 v2, v40

    #@2ce
    move/from16 v3, v41

    #@2d0
    invoke-virtual {v11, v0, v1, v2, v3}, Landroid/view/View;->setPadding(IIII)V

    #@2d3
    .line 298
    :cond_16
    move-object/from16 v0, v23

    #@2d5
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@2d7
    move/from16 v38, v0

    #@2d9
    add-int/lit8 v38, v38, 0x1

    #@2db
    move/from16 v0, v38

    #@2dd
    move-object/from16 v1, v23

    #@2df
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@2e1
    .line 299
    const/16 v38, 0x1

    #@2e3
    move/from16 v0, v38

    #@2e5
    move-object/from16 v1, v23

    #@2e7
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expanded:Z

    #@2e9
    .line 300
    add-int/lit8 v8, v8, -0x1

    #@2eb
    goto :goto_e

    #@2ec
    .line 303
    .end local v11    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_17
    const/16 v30, 0x1

    #@2ee
    .line 304
    goto/16 :goto_6

    #@2f0
    .line 309
    .end local v26    # "minCells":I
    .end local v27    # "minCellsItemCount":I
    .end local v28    # "minCellsAt":J
    :cond_18
    const/16 v31, 0x0

    #@2f2
    goto/16 :goto_9

    #@2f4
    .line 326
    .restart local v13    # "expandCount":F
    .restart local v31    # "singleItem":Z
    :cond_19
    const/4 v15, 0x0

    #@2f5
    goto/16 :goto_a

    #@2f7
    .line 332
    .restart local v15    # "extraPixels":I
    :cond_1a
    move-object/from16 v0, p0

    #@2f9
    move/from16 v1, v20

    #@2fb
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@2fe
    move-result-object v11

    #@2ff
    .line 333
    .restart local v11    # "child":Landroid/view/View;
    invoke-virtual {v11}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@302
    move-result-object v23

    #@303
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@305
    .line 334
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    instance-of v0, v11, Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@307
    move/from16 v38, v0

    #@309
    if-eqz v38, :cond_1c

    #@30b
    .line 336
    move-object/from16 v0, v23

    #@30d
    iput v15, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->extraPixels:I

    #@30f
    .line 337
    const/16 v38, 0x1

    #@311
    move/from16 v0, v38

    #@313
    move-object/from16 v1, v23

    #@315
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expanded:Z

    #@317
    .line 338
    if-nez v20, :cond_1b

    #@319
    move-object/from16 v0, v23

    #@31b
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->preventEdgeOffset:Z

    #@31d
    move/from16 v38, v0

    #@31f
    if-nez v38, :cond_1b

    #@321
    .line 341
    neg-int v0, v15

    #@322
    move/from16 v38, v0

    #@324
    div-int/lit8 v38, v38, 0x2

    #@326
    move/from16 v0, v38

    #@328
    move-object/from16 v1, v23

    #@32a
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@32c
    .line 343
    :cond_1b
    const/16 v30, 0x1

    #@32e
    goto/16 :goto_c

    #@330
    .line 344
    :cond_1c
    move-object/from16 v0, v23

    #@332
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@334
    move/from16 v38, v0

    #@336
    if-eqz v38, :cond_1d

    #@338
    .line 345
    move-object/from16 v0, v23

    #@33a
    iput v15, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->extraPixels:I

    #@33c
    .line 346
    const/16 v38, 0x1

    #@33e
    move/from16 v0, v38

    #@340
    move-object/from16 v1, v23

    #@342
    iput-boolean v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expanded:Z

    #@344
    .line 347
    neg-int v0, v15

    #@345
    move/from16 v38, v0

    #@347
    div-int/lit8 v38, v38, 0x2

    #@349
    move/from16 v0, v38

    #@34b
    move-object/from16 v1, v23

    #@34d
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@34f
    .line 348
    const/16 v30, 0x1

    #@351
    goto/16 :goto_c

    #@353
    .line 353
    :cond_1d
    if-eqz v20, :cond_1e

    #@355
    .line 354
    div-int/lit8 v38, v15, 0x2

    #@357
    move/from16 v0, v38

    #@359
    move-object/from16 v1, v23

    #@35b
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@35d
    .line 356
    :cond_1e
    add-int/lit8 v38, v12, -0x1

    #@35f
    move/from16 v0, v20

    #@361
    move/from16 v1, v38

    #@363
    if-eq v0, v1, :cond_12

    #@365
    .line 357
    div-int/lit8 v38, v15, 0x2

    #@367
    move/from16 v0, v38

    #@369
    move-object/from16 v1, v23

    #@36b
    iput v0, v1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@36d
    goto/16 :goto_c

    #@36f
    .line 362
    .end local v11    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_1f
    const/4 v8, 0x0

    #@370
    .line 366
    .end local v13    # "expandCount":F
    .end local v15    # "extraPixels":I
    :cond_20
    if-eqz v30, :cond_22

    #@372
    .line 367
    const/16 v20, 0x0

    #@374
    :goto_f
    move/from16 v0, v20

    #@376
    if-ge v0, v12, :cond_22

    #@378
    .line 368
    move-object/from16 v0, p0

    #@37a
    move/from16 v1, v20

    #@37c
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@37f
    move-result-object v11

    #@380
    .line 369
    .restart local v11    # "child":Landroid/view/View;
    invoke-virtual {v11}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@383
    move-result-object v23

    #@384
    check-cast v23, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@386
    .line 371
    .restart local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    move-object/from16 v0, v23

    #@388
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->expanded:Z

    #@38a
    move/from16 v38, v0

    #@38c
    if-nez v38, :cond_21

    #@38e
    .line 367
    :goto_10
    add-int/lit8 v20, v20, 0x1

    #@390
    goto :goto_f

    #@391
    .line 373
    :cond_21
    move-object/from16 v0, v23

    #@393
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->cellsUsed:I

    #@395
    move/from16 v38, v0

    #@397
    mul-int v38, v38, v5

    #@399
    move-object/from16 v0, v23

    #@39b
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->extraPixels:I

    #@39d
    move/from16 v39, v0

    #@39f
    add-int v35, v38, v39

    #@3a1
    .line 374
    .local v35, "width":I
    const/high16 v38, 0x40000000    # 2.0f

    #@3a3
    move/from16 v0, v35

    #@3a5
    move/from16 v1, v38

    #@3a7
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@3aa
    move-result v38

    #@3ab
    move/from16 v0, v38

    #@3ad
    move/from16 v1, v22

    #@3af
    invoke-virtual {v11, v0, v1}, Landroid/view/View;->measure(II)V

    #@3b2
    goto :goto_10

    #@3b3
    .line 379
    .end local v11    # "child":Landroid/view/View;
    .end local v23    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .end local v35    # "width":I
    :cond_22
    const/high16 v38, 0x40000000    # 2.0f

    #@3b5
    move/from16 v0, v17

    #@3b7
    move/from16 v1, v38

    #@3b9
    if-eq v0, v1, :cond_23

    #@3bb
    .line 380
    move/from16 v19, v25

    #@3bd
    .line 383
    :cond_23
    move-object/from16 v0, p0

    #@3bf
    move/from16 v1, v37

    #@3c1
    move/from16 v2, v19

    #@3c3
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setMeasuredDimension(II)V

    #@3c6
    goto/16 :goto_0
.end method


# virtual methods
.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 584
    if-eqz p1, :cond_0

    #@2
    instance-of v0, p1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public dismissPopupMenus()V
    .locals 1

    #@0
    .prologue
    .line 687
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 688
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->dismissPopupMenus()Z

    #@9
    .line 690
    :cond_0
    return-void
.end method

.method public dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z
    .locals 1
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 712
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method protected generateDefaultLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .locals 2

    #@0
    .prologue
    const/4 v1, -0x2

    #@1
    .line 557
    new-instance v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    invoke-direct {v0, v1, v1}, Landroid/support/v7/widget/ActionMenuView$LayoutParams;-><init>(II)V

    #@6
    .line 559
    .local v0, "params":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    const/16 v1, 0x10

    #@8
    iput v1, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->gravity:I

    #@a
    .line 560
    return-object v0
.end method

.method protected bridge synthetic generateDefaultLayoutParams()Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 43
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->generateDefaultLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method protected bridge synthetic generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 43
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->generateDefaultLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 565
    new-instance v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v7/widget/ActionMenuView$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .locals 2
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 570
    if-eqz p1, :cond_2

    #@2
    .line 571
    instance-of v1, p1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@4
    if-eqz v1, :cond_1

    #@6
    new-instance v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@8
    check-cast p1, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@a
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v7/widget/ActionMenuView$LayoutParams;-><init>(Landroid/support/v7/widget/ActionMenuView$LayoutParams;)V

    #@d
    .line 574
    .local v0, "result":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :goto_0
    iget v1, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->gravity:I

    #@f
    if-gtz v1, :cond_0

    #@11
    .line 575
    const/16 v1, 0x10

    #@13
    iput v1, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->gravity:I

    #@15
    .line 579
    .end local v0    # "result":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_0
    :goto_1
    return-object v0

    #@16
    .line 571
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_1
    new-instance v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@18
    invoke-direct {v0, p1}, Landroid/support/v7/widget/ActionMenuView$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@1b
    goto :goto_0

    #@1c
    .line 579
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->generateDefaultLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@1f
    move-result-object v0

    #@20
    goto :goto_1
.end method

.method public bridge synthetic generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 43
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ActionMenuView;->generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method protected bridge synthetic generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 43
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ActionMenuView;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public bridge synthetic generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 43
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ActionMenuView;->generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;

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
    .line 43
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ActionMenuView;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public generateOverflowButtonLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .locals 2

    #@0
    .prologue
    .line 589
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->generateDefaultLayoutParams()Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@3
    move-result-object v0

    #@4
    .line 590
    .local v0, "result":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    const/4 v1, 0x1

    #@5
    iput-boolean v1, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@7
    .line 591
    return-object v0
.end method

.method public getMenu()Landroid/view/Menu;
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 618
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@3
    if-nez v1, :cond_0

    #@5
    .line 619
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->getContext()Landroid/content/Context;

    #@8
    move-result-object v0

    #@9
    .line 620
    .local v0, "context":Landroid/content/Context;
    new-instance v1, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@b
    invoke-direct {v1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;-><init>(Landroid/content/Context;)V

    #@e
    iput-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@10
    .line 621
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@12
    new-instance v2, Landroid/support/v7/widget/ActionMenuView$MenuBuilderCallback;

    #@14
    invoke-direct {v2, p0, v3}, Landroid/support/v7/widget/ActionMenuView$MenuBuilderCallback;-><init>(Landroid/support/v7/widget/ActionMenuView;Landroid/support/v7/widget/ActionMenuView$1;)V

    #@17
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@1a
    .line 622
    new-instance v1, Landroid/support/v7/widget/ActionMenuPresenter;

    #@1c
    invoke-direct {v1, v0}, Landroid/support/v7/widget/ActionMenuPresenter;-><init>(Landroid/content/Context;)V

    #@1f
    iput-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@21
    .line 623
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@23
    const/4 v2, 0x1

    #@24
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/ActionMenuPresenter;->setReserveOverflow(Z)V

    #@27
    .line 624
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@29
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2b
    if-eqz v1, :cond_1

    #@2d
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2f
    :goto_0
    invoke-virtual {v2, v1}, Landroid/support/v7/widget/ActionMenuPresenter;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@32
    .line 626
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@34
    iget-object v2, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@36
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupContext:Landroid/content/Context;

    #@38
    invoke-virtual {v1, v2, v3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@3b
    .line 627
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@3d
    invoke-virtual {v1, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->setMenuView(Landroid/support/v7/widget/ActionMenuView;)V

    #@40
    .line 630
    .end local v0    # "context":Landroid/content/Context;
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@42
    return-object v1

    #@43
    .line 624
    .restart local v0    # "context":Landroid/content/Context;
    :cond_1
    new-instance v1, Landroid/support/v7/widget/ActionMenuView$ActionMenuPresenterCallback;

    #@45
    invoke-direct {v1, p0, v3}, Landroid/support/v7/widget/ActionMenuView$ActionMenuPresenterCallback;-><init>(Landroid/support/v7/widget/ActionMenuView;Landroid/support/v7/widget/ActionMenuView$1;)V

    #@48
    goto :goto_0
.end method

.method public getPopupTheme()I
    .locals 1

    #@0
    .prologue
    .line 111
    iget v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupTheme:I

    #@2
    return v0
.end method

.method public getWindowAnimations()I
    .locals 1

    #@0
    .prologue
    .line 601
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method protected hasSupportDividerBeforeChildAt(I)Z
    .locals 4
    .param p1, "childIndex"    # I

    #@0
    .prologue
    .line 696
    if-nez p1, :cond_1

    #@2
    .line 697
    const/4 v2, 0x0

    #@3
    .line 708
    :cond_0
    :goto_0
    return v2

    #@4
    .line 699
    :cond_1
    add-int/lit8 v3, p1, -0x1

    #@6
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@9
    move-result-object v1

    #@a
    .line 700
    .local v1, "childBefore":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@d
    move-result-object v0

    #@e
    .line 701
    .local v0, "child":Landroid/view/View;
    const/4 v2, 0x0

    #@f
    .line 702
    .local v2, "result":Z
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@12
    move-result v3

    #@13
    if-ge p1, v3, :cond_2

    #@15
    instance-of v3, v1, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;

    #@17
    if-eqz v3, :cond_2

    #@19
    .line 703
    check-cast v1, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;

    #@1b
    .end local v1    # "childBefore":Landroid/view/View;
    invoke-interface {v1}, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;->needsDividerAfter()Z

    #@1e
    move-result v3

    #@1f
    or-int/2addr v2, v3

    #@20
    .line 705
    :cond_2
    if-lez p1, :cond_0

    #@22
    instance-of v3, v0, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;

    #@24
    if-eqz v3, :cond_0

    #@26
    .line 706
    check-cast v0, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;

    #@28
    .end local v0    # "child":Landroid/view/View;
    invoke-interface {v0}, Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;->needsDividerBefore()Z

    #@2b
    move-result v3

    #@2c
    or-int/2addr v2, v3

    #@2d
    goto :goto_0
.end method

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 665
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public initialize(Landroid/support/v7/internal/view/menu/MenuBuilder;)V
    .locals 0
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    .line 606
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    .line 607
    return-void
.end method

.method public invokeItem(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 2
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 596
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-virtual {v0, p1, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;I)Z

    #@6
    move-result v0

    #@7
    return v0
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 680
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowPending()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 675
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isOverflowReserved()Z
    .locals 1

    #@0
    .prologue
    .line 547
    iget-boolean v0, p0, Landroid/support/v7/widget/ActionMenuView;->mReserveOverflow:Z

    #@2
    return v0
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 2
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 125
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x8

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 126
    invoke-super {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@9
    .line 129
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@b
    const/4 v1, 0x0

    #@c
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuPresenter;->updateMenuView(Z)V

    #@f
    .line 131
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@11
    if-eqz v0, :cond_1

    #@13
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@15
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@18
    move-result v0

    #@19
    if-eqz v0, :cond_1

    #@1b
    .line 132
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@1d
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@20
    .line 133
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@22
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->showOverflowMenu()Z

    #@25
    .line 135
    :cond_1
    return-void
.end method

.method public onDetachedFromWindow()V
    .locals 0

    #@0
    .prologue
    .line 541
    invoke-super {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->onDetachedFromWindow()V

    #@3
    .line 542
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->dismissPopupMenus()V

    #@6
    .line 543
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 32
    .param p1, "changed"    # Z
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "right"    # I
    .param p5, "bottom"    # I

    #@0
    .prologue
    .line 436
    move-object/from16 v0, p0

    #@2
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@4
    move/from16 v30, v0

    #@6
    if-nez v30, :cond_1

    #@8
    .line 437
    invoke-super/range {p0 .. p5}, Landroid/support/v7/widget/LinearLayoutCompat;->onLayout(ZIIII)V

    #@b
    .line 537
    :cond_0
    :goto_0
    return-void

    #@c
    .line 441
    :cond_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@f
    move-result v6

    #@10
    .line 442
    .local v6, "childCount":I
    sub-int v30, p5, p3

    #@12
    div-int/lit8 v15, v30, 0x2

    #@14
    .line 443
    .local v15, "midVertical":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getDividerWidth()I

    #@17
    move-result v7

    #@18
    .line 444
    .local v7, "dividerWidth":I
    const/16 v18, 0x0

    #@1a
    .line 445
    .local v18, "overflowWidth":I
    const/16 v17, 0x0

    #@1c
    .line 446
    .local v17, "nonOverflowWidth":I
    const/16 v16, 0x0

    #@1e
    .line 447
    .local v16, "nonOverflowCount":I
    sub-int v30, p4, p2

    #@20
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingRight()I

    #@23
    move-result v31

    #@24
    sub-int v30, v30, v31

    #@26
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingLeft()I

    #@29
    move-result v31

    #@2a
    sub-int v29, v30, v31

    #@2c
    .line 448
    .local v29, "widthRemaining":I
    const/4 v8, 0x0

    #@2d
    .line 449
    .local v8, "hasOverflow":Z
    invoke-static/range {p0 .. p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@30
    move-result v11

    #@31
    .line 450
    .local v11, "isLayoutRtl":Z
    const/4 v10, 0x0

    #@32
    .local v10, "i":I
    :goto_1
    if-ge v10, v6, :cond_7

    #@34
    .line 451
    move-object/from16 v0, p0

    #@36
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@39
    move-result-object v27

    #@3a
    .line 452
    .local v27, "v":Landroid/view/View;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getVisibility()I

    #@3d
    move-result v30

    #@3e
    const/16 v31, 0x8

    #@40
    move/from16 v0, v30

    #@42
    move/from16 v1, v31

    #@44
    if-ne v0, v1, :cond_2

    #@46
    .line 450
    :goto_2
    add-int/lit8 v10, v10, 0x1

    #@48
    goto :goto_1

    #@49
    .line 456
    :cond_2
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4c
    move-result-object v19

    #@4d
    check-cast v19, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@4f
    .line 457
    .local v19, "p":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    move-object/from16 v0, v19

    #@51
    iget-boolean v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@53
    move/from16 v30, v0

    #@55
    if-eqz v30, :cond_5

    #@57
    .line 458
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredWidth()I

    #@5a
    move-result v18

    #@5b
    .line 459
    move-object/from16 v0, p0

    #@5d
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/ActionMenuView;->hasSupportDividerBeforeChildAt(I)Z

    #@60
    move-result v30

    #@61
    if-eqz v30, :cond_3

    #@63
    .line 460
    add-int v18, v18, v7

    #@65
    .line 462
    :cond_3
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredHeight()I

    #@68
    move-result v9

    #@69
    .line 465
    .local v9, "height":I
    if-eqz v11, :cond_4

    #@6b
    .line 466
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingLeft()I

    #@6e
    move-result v30

    #@6f
    move-object/from16 v0, v19

    #@71
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@73
    move/from16 v31, v0

    #@75
    add-int v12, v30, v31

    #@77
    .line 467
    .local v12, "l":I
    add-int v20, v12, v18

    #@79
    .line 472
    .local v20, "r":I
    :goto_3
    div-int/lit8 v30, v9, 0x2

    #@7b
    sub-int v26, v15, v30

    #@7d
    .line 473
    .local v26, "t":I
    add-int v5, v26, v9

    #@7f
    .line 474
    .local v5, "b":I
    move-object/from16 v0, v27

    #@81
    move/from16 v1, v26

    #@83
    move/from16 v2, v20

    #@85
    invoke-virtual {v0, v12, v1, v2, v5}, Landroid/view/View;->layout(IIII)V

    #@88
    .line 476
    sub-int v29, v29, v18

    #@8a
    .line 477
    const/4 v8, 0x1

    #@8b
    .line 478
    goto :goto_2

    #@8c
    .line 469
    .end local v5    # "b":I
    .end local v12    # "l":I
    .end local v20    # "r":I
    .end local v26    # "t":I
    :cond_4
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getWidth()I

    #@8f
    move-result v30

    #@90
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingRight()I

    #@93
    move-result v31

    #@94
    sub-int v30, v30, v31

    #@96
    move-object/from16 v0, v19

    #@98
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@9a
    move/from16 v31, v0

    #@9c
    sub-int v20, v30, v31

    #@9e
    .line 470
    .restart local v20    # "r":I
    sub-int v12, v20, v18

    #@a0
    .restart local v12    # "l":I
    goto :goto_3

    #@a1
    .line 479
    .end local v9    # "height":I
    .end local v12    # "l":I
    .end local v20    # "r":I
    :cond_5
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredWidth()I

    #@a4
    move-result v30

    #@a5
    move-object/from16 v0, v19

    #@a7
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@a9
    move/from16 v31, v0

    #@ab
    add-int v30, v30, v31

    #@ad
    move-object/from16 v0, v19

    #@af
    iget v0, v0, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@b1
    move/from16 v31, v0

    #@b3
    add-int v21, v30, v31

    #@b5
    .line 480
    .local v21, "size":I
    add-int v17, v17, v21

    #@b7
    .line 481
    sub-int v29, v29, v21

    #@b9
    .line 482
    move-object/from16 v0, p0

    #@bb
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/ActionMenuView;->hasSupportDividerBeforeChildAt(I)Z

    #@be
    move-result v30

    #@bf
    if-eqz v30, :cond_6

    #@c1
    .line 483
    add-int v17, v17, v7

    #@c3
    .line 485
    :cond_6
    add-int/lit8 v16, v16, 0x1

    #@c5
    goto :goto_2

    #@c6
    .line 489
    .end local v19    # "p":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .end local v21    # "size":I
    .end local v27    # "v":Landroid/view/View;
    :cond_7
    const/16 v30, 0x1

    #@c8
    move/from16 v0, v30

    #@ca
    if-ne v6, v0, :cond_8

    #@cc
    if-nez v8, :cond_8

    #@ce
    .line 491
    const/16 v30, 0x0

    #@d0
    move-object/from16 v0, p0

    #@d2
    move/from16 v1, v30

    #@d4
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@d7
    move-result-object v27

    #@d8
    .line 492
    .restart local v27    # "v":Landroid/view/View;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredWidth()I

    #@db
    move-result v28

    #@dc
    .line 493
    .local v28, "width":I
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredHeight()I

    #@df
    move-result v9

    #@e0
    .line 494
    .restart local v9    # "height":I
    sub-int v30, p4, p2

    #@e2
    div-int/lit8 v14, v30, 0x2

    #@e4
    .line 495
    .local v14, "midHorizontal":I
    div-int/lit8 v30, v28, 0x2

    #@e6
    sub-int v12, v14, v30

    #@e8
    .line 496
    .restart local v12    # "l":I
    div-int/lit8 v30, v9, 0x2

    #@ea
    sub-int v26, v15, v30

    #@ec
    .line 497
    .restart local v26    # "t":I
    add-int v30, v12, v28

    #@ee
    add-int v31, v26, v9

    #@f0
    move-object/from16 v0, v27

    #@f2
    move/from16 v1, v26

    #@f4
    move/from16 v2, v30

    #@f6
    move/from16 v3, v31

    #@f8
    invoke-virtual {v0, v12, v1, v2, v3}, Landroid/view/View;->layout(IIII)V

    #@fb
    goto/16 :goto_0

    #@fd
    .line 501
    .end local v9    # "height":I
    .end local v12    # "l":I
    .end local v14    # "midHorizontal":I
    .end local v26    # "t":I
    .end local v27    # "v":Landroid/view/View;
    .end local v28    # "width":I
    :cond_8
    if-eqz v8, :cond_a

    #@ff
    const/16 v30, 0x0

    #@101
    :goto_4
    sub-int v22, v16, v30

    #@103
    .line 502
    .local v22, "spacerCount":I
    const/16 v31, 0x0

    #@105
    if-lez v22, :cond_b

    #@107
    div-int v30, v29, v22

    #@109
    :goto_5
    move/from16 v0, v31

    #@10b
    move/from16 v1, v30

    #@10d
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@110
    move-result v23

    #@111
    .line 504
    .local v23, "spacerSize":I
    if-eqz v11, :cond_d

    #@113
    .line 505
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getWidth()I

    #@116
    move-result v30

    #@117
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingRight()I

    #@11a
    move-result v31

    #@11b
    sub-int v25, v30, v31

    #@11d
    .line 506
    .local v25, "startRight":I
    const/4 v10, 0x0

    #@11e
    :goto_6
    if-ge v10, v6, :cond_0

    #@120
    .line 507
    move-object/from16 v0, p0

    #@122
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@125
    move-result-object v27

    #@126
    .line 508
    .restart local v27    # "v":Landroid/view/View;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@129
    move-result-object v13

    #@12a
    check-cast v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@12c
    .line 509
    .local v13, "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getVisibility()I

    #@12f
    move-result v30

    #@130
    const/16 v31, 0x8

    #@132
    move/from16 v0, v30

    #@134
    move/from16 v1, v31

    #@136
    if-eq v0, v1, :cond_9

    #@138
    iget-boolean v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@13a
    move/from16 v30, v0

    #@13c
    if-eqz v30, :cond_c

    #@13e
    .line 506
    :cond_9
    :goto_7
    add-int/lit8 v10, v10, 0x1

    #@140
    goto :goto_6

    #@141
    .line 501
    .end local v13    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .end local v22    # "spacerCount":I
    .end local v23    # "spacerSize":I
    .end local v25    # "startRight":I
    .end local v27    # "v":Landroid/view/View;
    :cond_a
    const/16 v30, 0x1

    #@143
    goto :goto_4

    #@144
    .line 502
    .restart local v22    # "spacerCount":I
    :cond_b
    const/16 v30, 0x0

    #@146
    goto :goto_5

    #@147
    .line 513
    .restart local v13    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .restart local v23    # "spacerSize":I
    .restart local v25    # "startRight":I
    .restart local v27    # "v":Landroid/view/View;
    :cond_c
    iget v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@149
    move/from16 v30, v0

    #@14b
    sub-int v25, v25, v30

    #@14d
    .line 514
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredWidth()I

    #@150
    move-result v28

    #@151
    .line 515
    .restart local v28    # "width":I
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredHeight()I

    #@154
    move-result v9

    #@155
    .line 516
    .restart local v9    # "height":I
    div-int/lit8 v30, v9, 0x2

    #@157
    sub-int v26, v15, v30

    #@159
    .line 517
    .restart local v26    # "t":I
    sub-int v30, v25, v28

    #@15b
    add-int v31, v26, v9

    #@15d
    move-object/from16 v0, v27

    #@15f
    move/from16 v1, v30

    #@161
    move/from16 v2, v26

    #@163
    move/from16 v3, v25

    #@165
    move/from16 v4, v31

    #@167
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/view/View;->layout(IIII)V

    #@16a
    .line 518
    iget v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@16c
    move/from16 v30, v0

    #@16e
    add-int v30, v30, v28

    #@170
    add-int v30, v30, v23

    #@172
    sub-int v25, v25, v30

    #@174
    goto :goto_7

    #@175
    .line 521
    .end local v9    # "height":I
    .end local v13    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    .end local v25    # "startRight":I
    .end local v26    # "t":I
    .end local v27    # "v":Landroid/view/View;
    .end local v28    # "width":I
    :cond_d
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ActionMenuView;->getPaddingLeft()I

    #@178
    move-result v24

    #@179
    .line 522
    .local v24, "startLeft":I
    const/4 v10, 0x0

    #@17a
    :goto_8
    if-ge v10, v6, :cond_0

    #@17c
    .line 523
    move-object/from16 v0, p0

    #@17e
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@181
    move-result-object v27

    #@182
    .line 524
    .restart local v27    # "v":Landroid/view/View;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@185
    move-result-object v13

    #@186
    check-cast v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@188
    .line 525
    .restart local v13    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getVisibility()I

    #@18b
    move-result v30

    #@18c
    const/16 v31, 0x8

    #@18e
    move/from16 v0, v30

    #@190
    move/from16 v1, v31

    #@192
    if-eq v0, v1, :cond_e

    #@194
    iget-boolean v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->isOverflowButton:Z

    #@196
    move/from16 v30, v0

    #@198
    if-eqz v30, :cond_f

    #@19a
    .line 522
    :cond_e
    :goto_9
    add-int/lit8 v10, v10, 0x1

    #@19c
    goto :goto_8

    #@19d
    .line 529
    :cond_f
    iget v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@19f
    move/from16 v30, v0

    #@1a1
    add-int v24, v24, v30

    #@1a3
    .line 530
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredWidth()I

    #@1a6
    move-result v28

    #@1a7
    .line 531
    .restart local v28    # "width":I
    invoke-virtual/range {v27 .. v27}, Landroid/view/View;->getMeasuredHeight()I

    #@1aa
    move-result v9

    #@1ab
    .line 532
    .restart local v9    # "height":I
    div-int/lit8 v30, v9, 0x2

    #@1ad
    sub-int v26, v15, v30

    #@1af
    .line 533
    .restart local v26    # "t":I
    add-int v30, v24, v28

    #@1b1
    add-int v31, v26, v9

    #@1b3
    move-object/from16 v0, v27

    #@1b5
    move/from16 v1, v24

    #@1b7
    move/from16 v2, v26

    #@1b9
    move/from16 v3, v30

    #@1bb
    move/from16 v4, v31

    #@1bd
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/view/View;->layout(IIII)V

    #@1c0
    .line 534
    iget v0, v13, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@1c2
    move/from16 v30, v0

    #@1c4
    add-int v30, v30, v28

    #@1c6
    add-int v30, v30, v23

    #@1c8
    add-int v24, v24, v30

    #@1ca
    goto :goto_9
.end method

.method protected onMeasure(II)V
    .locals 10
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v8, 0x0

    #@2
    .line 144
    iget-boolean v4, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@4
    .line 145
    .local v4, "wasFormatted":Z
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@7
    move-result v6

    #@8
    const/high16 v9, 0x40000000    # 2.0f

    #@a
    if-ne v6, v9, :cond_2

    #@c
    move v6, v7

    #@d
    :goto_0
    iput-boolean v6, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@f
    .line 147
    iget-boolean v6, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@11
    if-eq v4, v6, :cond_0

    #@13
    .line 148
    iput v8, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItemsWidth:I

    #@15
    .line 153
    :cond_0
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@18
    move-result v5

    #@19
    .line 154
    .local v5, "widthSize":I
    iget-boolean v6, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@1b
    if-eqz v6, :cond_1

    #@1d
    iget-object v6, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@1f
    if-eqz v6, :cond_1

    #@21
    iget v6, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItemsWidth:I

    #@23
    if-eq v5, v6, :cond_1

    #@25
    .line 155
    iput v5, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItemsWidth:I

    #@27
    .line 156
    iget-object v6, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@29
    invoke-virtual {v6, v7}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@2c
    .line 159
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@2f
    move-result v1

    #@30
    .line 160
    .local v1, "childCount":I
    iget-boolean v6, p0, Landroid/support/v7/widget/ActionMenuView;->mFormatItems:Z

    #@32
    if-eqz v6, :cond_3

    #@34
    if-lez v1, :cond_3

    #@36
    .line 161
    invoke-direct {p0, p1, p2}, Landroid/support/v7/widget/ActionMenuView;->onMeasureExactFormat(II)V

    #@39
    .line 171
    :goto_1
    return-void

    #@3a
    .end local v1    # "childCount":I
    .end local v5    # "widthSize":I
    :cond_2
    move v6, v8

    #@3b
    .line 145
    goto :goto_0

    #@3c
    .line 164
    .restart local v1    # "childCount":I
    .restart local v5    # "widthSize":I
    :cond_3
    const/4 v2, 0x0

    #@3d
    .local v2, "i":I
    :goto_2
    if-ge v2, v1, :cond_4

    #@3f
    .line 165
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@42
    move-result-object v0

    #@43
    .line 166
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@46
    move-result-object v3

    #@47
    check-cast v3, Landroid/support/v7/widget/ActionMenuView$LayoutParams;

    #@49
    .line 167
    .local v3, "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    iput v8, v3, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->rightMargin:I

    #@4b
    iput v8, v3, Landroid/support/v7/widget/ActionMenuView$LayoutParams;->leftMargin:I

    #@4d
    .line 164
    add-int/lit8 v2, v2, 0x1

    #@4f
    goto :goto_2

    #@50
    .line 169
    .end local v0    # "child":Landroid/view/View;
    .end local v3    # "lp":Landroid/support/v7/widget/ActionMenuView$LayoutParams;
    :cond_4
    invoke-super {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->onMeasure(II)V

    #@53
    goto :goto_1
.end method

.method public peekMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 1

    #@0
    .prologue
    .line 647
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@2
    return-object v0
.end method

.method public setExpandedActionViewsExclusive(Z)V
    .locals 1
    .param p1, "exclusive"    # Z

    #@0
    .prologue
    .line 717
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/ActionMenuPresenter;->setExpandedActionViewsExclusive(Z)V

    #@5
    .line 718
    return-void
.end method

.method public setMenuCallbacks(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V
    .locals 0
    .param p1, "pcb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;
    .param p2, "mcb"    # Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@0
    .prologue
    .line 638
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    .line 639
    iput-object p2, p0, Landroid/support/v7/widget/ActionMenuView;->mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@4
    .line 640
    return-void
.end method

.method public setOnMenuItemClickListener(Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

    #@0
    .prologue
    .line 138
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mOnMenuItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

    #@2
    .line 139
    return-void
.end method

.method public setOverflowReserved(Z)V
    .locals 0
    .param p1, "reserveOverflow"    # Z

    #@0
    .prologue
    .line 552
    iput-boolean p1, p0, Landroid/support/v7/widget/ActionMenuView;->mReserveOverflow:Z

    #@2
    .line 553
    return-void
.end method

.method public setPopupTheme(I)V
    .locals 2
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 95
    iget v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupTheme:I

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 96
    iput p1, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupTheme:I

    #@6
    .line 97
    if-nez p1, :cond_1

    #@8
    .line 98
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mContext:Landroid/content/Context;

    #@a
    iput-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupContext:Landroid/content/Context;

    #@c
    .line 103
    :cond_0
    :goto_0
    return-void

    #@d
    .line 100
    :cond_1
    new-instance v0, Landroid/view/ContextThemeWrapper;

    #@f
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuView;->mContext:Landroid/content/Context;

    #@11
    invoke-direct {v0, v1, p1}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@14
    iput-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPopupContext:Landroid/content/Context;

    #@16
    goto :goto_0
.end method

.method public setPresenter(Landroid/support/v7/widget/ActionMenuPresenter;)V
    .locals 1
    .param p1, "presenter"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    .line 119
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    .line 120
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@4
    invoke-virtual {v0, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->setMenuView(Landroid/support/v7/widget/ActionMenuView;)V

    #@7
    .line 121
    return-void
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 656
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuView;->mPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->showOverflowMenu()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method
