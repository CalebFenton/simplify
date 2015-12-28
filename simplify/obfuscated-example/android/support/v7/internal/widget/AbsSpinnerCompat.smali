.class abstract Landroid/support/v7/internal/widget/AbsSpinnerCompat;
.super Landroid/support/v7/internal/widget/AdapterViewCompat;
.source "AbsSpinnerCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;,
        Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/support/v7/internal/widget/AdapterViewCompat",
        "<",
        "Landroid/widget/SpinnerAdapter;",
        ">;"
    }
.end annotation


# instance fields
.field mAdapter:Landroid/widget/SpinnerAdapter;

.field private mDataSetObserver:Landroid/database/DataSetObserver;

.field mHeightMeasureSpec:I

.field final mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

.field mSelectionBottomPadding:I

.field mSelectionLeftPadding:I

.field mSelectionRightPadding:I

.field mSelectionTopPadding:I

.field final mSpinnerPadding:Landroid/graphics/Rect;

.field private mTouchFrame:Landroid/graphics/Rect;

.field mWidthMeasureSpec:I


# direct methods
.method constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 54
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;-><init>(Landroid/content/Context;)V

    #@4
    .line 41
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionLeftPadding:I

    #@6
    .line 42
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionTopPadding:I

    #@8
    .line 43
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionRightPadding:I

    #@a
    .line 44
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionBottomPadding:I

    #@c
    .line 45
    new-instance v0, Landroid/graphics/Rect;

    #@e
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@11
    iput-object v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@13
    .line 47
    new-instance v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@15
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;-><init>(Landroid/support/v7/internal/widget/AbsSpinnerCompat;)V

    #@18
    iput-object v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@1a
    .line 55
    invoke-direct {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->initAbsSpinner()V

    #@1d
    .line 56
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 59
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 60
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 63
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/internal/widget/AdapterViewCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 41
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionLeftPadding:I

    #@6
    .line 42
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionTopPadding:I

    #@8
    .line 43
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionRightPadding:I

    #@a
    .line 44
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionBottomPadding:I

    #@c
    .line 45
    new-instance v0, Landroid/graphics/Rect;

    #@e
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@11
    iput-object v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@13
    .line 47
    new-instance v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@15
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;-><init>(Landroid/support/v7/internal/widget/AbsSpinnerCompat;)V

    #@18
    iput-object v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@1a
    .line 64
    invoke-direct {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->initAbsSpinner()V

    #@1d
    .line 65
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/widget/AbsSpinnerCompat;Landroid/view/View;Z)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/AbsSpinnerCompat;
    .param p1, "x1"    # Landroid/view/View;
    .param p2, "x2"    # Z

    #@0
    .prologue
    .line 35
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->removeDetachedView(Landroid/view/View;Z)V

    #@3
    return-void
.end method

.method private initAbsSpinner()V
    .locals 1

    #@0
    .prologue
    .line 71
    const/4 v0, 0x1

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setFocusable(Z)V

    #@4
    .line 72
    const/4 v0, 0x0

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setWillNotDraw(Z)V

    #@8
    .line 73
    return-void
.end method


# virtual methods
.method protected generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 3

    #@0
    .prologue
    .line 229
    new-instance v0, Landroid/view/ViewGroup$LayoutParams;

    #@2
    const/4 v1, -0x1

    #@3
    const/4 v2, -0x2

    #@4
    invoke-direct {v0, v1, v2}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@7
    return-object v0
.end method

.method public bridge synthetic getAdapter()Landroid/widget/Adapter;
    .locals 1

    #@0
    .prologue
    .line 35
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getAdapter()Landroid/widget/SpinnerAdapter;
    .locals 1

    #@0
    .prologue
    .line 308
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    return-object v0
.end method

.method getChildHeight(Landroid/view/View;)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 220
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method getChildWidth(Landroid/view/View;)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 224
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getCount()I
    .locals 1

    #@0
    .prologue
    .line 313
    iget v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@2
    return v0
.end method

.method public getSelectedView()Landroid/view/View;
    .locals 2

    #@0
    .prologue
    .line 286
    iget v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@2
    if-lez v0, :cond_0

    #@4
    iget v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectedPosition:I

    #@6
    if-ltz v0, :cond_0

    #@8
    .line 287
    iget v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectedPosition:I

    #@a
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mFirstPosition:I

    #@c
    sub-int/2addr v0, v1

    #@d
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildAt(I)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    .line 289
    :goto_0
    return-object v0

    #@12
    :cond_0
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method abstract layout(IZ)V
.end method

.method protected onMeasure(II)V
    .locals 17
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 147
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v13

    #@4
    .line 151
    .local v13, "widthMode":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getPaddingLeft()I

    #@7
    move-result v6

    #@8
    .line 152
    .local v6, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getPaddingTop()I

    #@b
    move-result v8

    #@c
    .line 153
    .local v8, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getPaddingRight()I

    #@f
    move-result v7

    #@10
    .line 154
    .local v7, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getPaddingBottom()I

    #@13
    move-result v5

    #@14
    .line 156
    .local v5, "paddingBottom":I
    move-object/from16 v0, p0

    #@16
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@18
    move-object/from16 v0, p0

    #@1a
    iget v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionLeftPadding:I

    #@1c
    move/from16 v16, v0

    #@1e
    move/from16 v0, v16

    #@20
    if-le v6, v0, :cond_5

    #@22
    .end local v6    # "paddingLeft":I
    :goto_0
    iput v6, v15, Landroid/graphics/Rect;->left:I

    #@24
    .line 158
    move-object/from16 v0, p0

    #@26
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@28
    move-object/from16 v0, p0

    #@2a
    iget v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionTopPadding:I

    #@2c
    move/from16 v16, v0

    #@2e
    move/from16 v0, v16

    #@30
    if-le v8, v0, :cond_6

    #@32
    .end local v8    # "paddingTop":I
    :goto_1
    iput v8, v15, Landroid/graphics/Rect;->top:I

    #@34
    .line 160
    move-object/from16 v0, p0

    #@36
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@38
    move-object/from16 v0, p0

    #@3a
    iget v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionRightPadding:I

    #@3c
    move/from16 v16, v0

    #@3e
    move/from16 v0, v16

    #@40
    if-le v7, v0, :cond_7

    #@42
    .end local v7    # "paddingRight":I
    :goto_2
    iput v7, v15, Landroid/graphics/Rect;->right:I

    #@44
    .line 162
    move-object/from16 v0, p0

    #@46
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@48
    move-object/from16 v0, p0

    #@4a
    iget v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionBottomPadding:I

    #@4c
    move/from16 v16, v0

    #@4e
    move/from16 v0, v16

    #@50
    if-le v5, v0, :cond_8

    #@52
    .end local v5    # "paddingBottom":I
    :goto_3
    iput v5, v15, Landroid/graphics/Rect;->bottom:I

    #@54
    .line 165
    move-object/from16 v0, p0

    #@56
    iget-boolean v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataChanged:Z

    #@58
    if-eqz v15, :cond_0

    #@5a
    .line 166
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->handleDataChanged()V

    #@5d
    .line 169
    :cond_0
    const/4 v9, 0x0

    #@5e
    .line 170
    .local v9, "preferredHeight":I
    const/4 v10, 0x0

    #@5f
    .line 171
    .local v10, "preferredWidth":I
    const/4 v4, 0x1

    #@60
    .line 173
    .local v4, "needsMeasuring":Z
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getSelectedItemPosition()I

    #@63
    move-result v11

    #@64
    .line 174
    .local v11, "selectedPosition":I
    if-ltz v11, :cond_3

    #@66
    move-object/from16 v0, p0

    #@68
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@6a
    if-eqz v15, :cond_3

    #@6c
    move-object/from16 v0, p0

    #@6e
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@70
    invoke-interface {v15}, Landroid/widget/SpinnerAdapter;->getCount()I

    #@73
    move-result v15

    #@74
    if-ge v11, v15, :cond_3

    #@76
    .line 176
    move-object/from16 v0, p0

    #@78
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@7a
    invoke-virtual {v15, v11}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->get(I)Landroid/view/View;

    #@7d
    move-result-object v12

    #@7e
    .line 177
    .local v12, "view":Landroid/view/View;
    if-nez v12, :cond_1

    #@80
    .line 179
    move-object/from16 v0, p0

    #@82
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@84
    const/16 v16, 0x0

    #@86
    move-object/from16 v0, v16

    #@88
    move-object/from16 v1, p0

    #@8a
    invoke-interface {v15, v11, v0, v1}, Landroid/widget/SpinnerAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@8d
    move-result-object v12

    #@8e
    .line 182
    :cond_1
    if-eqz v12, :cond_3

    #@90
    .line 184
    move-object/from16 v0, p0

    #@92
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@94
    invoke-virtual {v15, v11, v12}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->put(ILandroid/view/View;)V

    #@97
    .line 186
    invoke-virtual {v12}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@9a
    move-result-object v15

    #@9b
    if-nez v15, :cond_2

    #@9d
    .line 187
    const/4 v15, 0x1

    #@9e
    move-object/from16 v0, p0

    #@a0
    iput-boolean v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mBlockLayoutRequests:Z

    #@a2
    .line 188
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@a5
    move-result-object v15

    #@a6
    invoke-virtual {v12, v15}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@a9
    .line 189
    const/4 v15, 0x0

    #@aa
    move-object/from16 v0, p0

    #@ac
    iput-boolean v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mBlockLayoutRequests:Z

    #@ae
    .line 191
    :cond_2
    move-object/from16 v0, p0

    #@b0
    move/from16 v1, p1

    #@b2
    move/from16 v2, p2

    #@b4
    invoke-virtual {v0, v12, v1, v2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->measureChild(Landroid/view/View;II)V

    #@b7
    .line 193
    move-object/from16 v0, p0

    #@b9
    invoke-virtual {v0, v12}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildHeight(Landroid/view/View;)I

    #@bc
    move-result v15

    #@bd
    move-object/from16 v0, p0

    #@bf
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@c1
    move-object/from16 v16, v0

    #@c3
    move-object/from16 v0, v16

    #@c5
    iget v0, v0, Landroid/graphics/Rect;->top:I

    #@c7
    move/from16 v16, v0

    #@c9
    add-int v15, v15, v16

    #@cb
    move-object/from16 v0, p0

    #@cd
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@cf
    move-object/from16 v16, v0

    #@d1
    move-object/from16 v0, v16

    #@d3
    iget v0, v0, Landroid/graphics/Rect;->bottom:I

    #@d5
    move/from16 v16, v0

    #@d7
    add-int v9, v15, v16

    #@d9
    .line 194
    move-object/from16 v0, p0

    #@db
    invoke-virtual {v0, v12}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildWidth(Landroid/view/View;)I

    #@de
    move-result v15

    #@df
    move-object/from16 v0, p0

    #@e1
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@e3
    move-object/from16 v16, v0

    #@e5
    move-object/from16 v0, v16

    #@e7
    iget v0, v0, Landroid/graphics/Rect;->left:I

    #@e9
    move/from16 v16, v0

    #@eb
    add-int v15, v15, v16

    #@ed
    move-object/from16 v0, p0

    #@ef
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@f1
    move-object/from16 v16, v0

    #@f3
    move-object/from16 v0, v16

    #@f5
    iget v0, v0, Landroid/graphics/Rect;->right:I

    #@f7
    move/from16 v16, v0

    #@f9
    add-int v10, v15, v16

    #@fb
    .line 196
    const/4 v4, 0x0

    #@fc
    .line 200
    .end local v12    # "view":Landroid/view/View;
    :cond_3
    if-eqz v4, :cond_4

    #@fe
    .line 202
    move-object/from16 v0, p0

    #@100
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@102
    iget v15, v15, Landroid/graphics/Rect;->top:I

    #@104
    move-object/from16 v0, p0

    #@106
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@108
    move-object/from16 v16, v0

    #@10a
    move-object/from16 v0, v16

    #@10c
    iget v0, v0, Landroid/graphics/Rect;->bottom:I

    #@10e
    move/from16 v16, v0

    #@110
    add-int v9, v15, v16

    #@112
    .line 203
    if-nez v13, :cond_4

    #@114
    .line 204
    move-object/from16 v0, p0

    #@116
    iget-object v15, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@118
    iget v15, v15, Landroid/graphics/Rect;->left:I

    #@11a
    move-object/from16 v0, p0

    #@11c
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@11e
    move-object/from16 v16, v0

    #@120
    move-object/from16 v0, v16

    #@122
    iget v0, v0, Landroid/graphics/Rect;->right:I

    #@124
    move/from16 v16, v0

    #@126
    add-int v10, v15, v16

    #@128
    .line 208
    :cond_4
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getSuggestedMinimumHeight()I

    #@12b
    move-result v15

    #@12c
    invoke-static {v9, v15}, Ljava/lang/Math;->max(II)I

    #@12f
    move-result v9

    #@130
    .line 209
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getSuggestedMinimumWidth()I

    #@133
    move-result v15

    #@134
    invoke-static {v10, v15}, Ljava/lang/Math;->max(II)I

    #@137
    move-result v10

    #@138
    .line 211
    const/4 v15, 0x0

    #@139
    move/from16 v0, p2

    #@13b
    invoke-static {v9, v0, v15}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@13e
    move-result v3

    #@13f
    .line 212
    .local v3, "heightSize":I
    const/4 v15, 0x0

    #@140
    move/from16 v0, p1

    #@142
    invoke-static {v10, v0, v15}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@145
    move-result v14

    #@146
    .line 214
    .local v14, "widthSize":I
    move-object/from16 v0, p0

    #@148
    invoke-virtual {v0, v14, v3}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setMeasuredDimension(II)V

    #@14b
    .line 215
    move/from16 v0, p2

    #@14d
    move-object/from16 v1, p0

    #@14f
    iput v0, v1, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mHeightMeasureSpec:I

    #@151
    .line 216
    move/from16 v0, p1

    #@153
    move-object/from16 v1, p0

    #@155
    iput v0, v1, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mWidthMeasureSpec:I

    #@157
    .line 217
    return-void

    #@158
    .line 156
    .end local v3    # "heightSize":I
    .end local v4    # "needsMeasuring":Z
    .end local v9    # "preferredHeight":I
    .end local v10    # "preferredWidth":I
    .end local v11    # "selectedPosition":I
    .end local v14    # "widthSize":I
    .restart local v5    # "paddingBottom":I
    .restart local v6    # "paddingLeft":I
    .restart local v7    # "paddingRight":I
    .restart local v8    # "paddingTop":I
    :cond_5
    move-object/from16 v0, p0

    #@15a
    iget v6, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionLeftPadding:I

    #@15c
    goto/16 :goto_0

    #@15e
    .line 158
    .end local v6    # "paddingLeft":I
    :cond_6
    move-object/from16 v0, p0

    #@160
    iget v8, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionTopPadding:I

    #@162
    goto/16 :goto_1

    #@164
    .line 160
    .end local v8    # "paddingTop":I
    :cond_7
    move-object/from16 v0, p0

    #@166
    iget v7, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionRightPadding:I

    #@168
    goto/16 :goto_2

    #@16a
    .line 162
    .end local v7    # "paddingRight":I
    :cond_8
    move-object/from16 v0, p0

    #@16c
    iget v5, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectionBottomPadding:I

    #@16e
    goto/16 :goto_3
.end method

.method public onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 7
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    .line 406
    move-object v0, p1

    #@2
    check-cast v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;

    #@4
    .line 408
    .local v0, "ss":Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@7
    move-result-object v1

    #@8
    invoke-super {p0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@b
    .line 410
    iget-wide v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->selectedId:J

    #@d
    const-wide/16 v4, 0x0

    #@f
    cmp-long v1, v2, v4

    #@11
    if-ltz v1, :cond_0

    #@13
    .line 411
    iput-boolean v6, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataChanged:Z

    #@15
    .line 412
    iput-boolean v6, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mNeedSync:Z

    #@17
    .line 413
    iget-wide v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->selectedId:J

    #@19
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSyncRowId:J

    #@1b
    .line 414
    iget v1, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->position:I

    #@1d
    iput v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSyncPosition:I

    #@1f
    .line 415
    const/4 v1, 0x0

    #@20
    iput v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSyncMode:I

    #@22
    .line 416
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->requestLayout()V

    #@25
    .line 418
    :cond_0
    return-void
.end method

.method public onSaveInstanceState()Landroid/os/Parcelable;
    .locals 6

    #@0
    .prologue
    .line 393
    invoke-super {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->onSaveInstanceState()Landroid/os/Parcelable;

    #@3
    move-result-object v1

    #@4
    .line 394
    .local v1, "superState":Landroid/os/Parcelable;
    new-instance v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;

    #@6
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 395
    .local v0, "ss":Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getSelectedItemId()J

    #@c
    move-result-wide v2

    #@d
    iput-wide v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->selectedId:J

    #@f
    .line 396
    iget-wide v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->selectedId:J

    #@11
    const-wide/16 v4, 0x0

    #@13
    cmp-long v2, v2, v4

    #@15
    if-ltz v2, :cond_0

    #@17
    .line 397
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getSelectedItemPosition()I

    #@1a
    move-result v2

    #@1b
    iput v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->position:I

    #@1d
    .line 401
    :goto_0
    return-object v0

    #@1e
    .line 399
    :cond_0
    const/4 v2, -0x1

    #@1f
    iput v2, v0, Landroid/support/v7/internal/widget/AbsSpinnerCompat$SavedState;->position:I

    #@21
    goto :goto_0
.end method

.method public pointToPosition(II)I
    .locals 5
    .param p1, "x"    # I
    .param p2, "y"    # I

    #@0
    .prologue
    .line 325
    iget-object v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mTouchFrame:Landroid/graphics/Rect;

    #@2
    .line 326
    .local v2, "frame":Landroid/graphics/Rect;
    if-nez v2, :cond_0

    #@4
    .line 327
    new-instance v4, Landroid/graphics/Rect;

    #@6
    invoke-direct {v4}, Landroid/graphics/Rect;-><init>()V

    #@9
    iput-object v4, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mTouchFrame:Landroid/graphics/Rect;

    #@b
    .line 328
    iget-object v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mTouchFrame:Landroid/graphics/Rect;

    #@d
    .line 331
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildCount()I

    #@10
    move-result v1

    #@11
    .line 332
    .local v1, "count":I
    add-int/lit8 v3, v1, -0x1

    #@13
    .local v3, "i":I
    :goto_0
    if-ltz v3, :cond_2

    #@15
    .line 333
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildAt(I)Landroid/view/View;

    #@18
    move-result-object v0

    #@19
    .line 334
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@1c
    move-result v4

    #@1d
    if-nez v4, :cond_1

    #@1f
    .line 335
    invoke-virtual {v0, v2}, Landroid/view/View;->getHitRect(Landroid/graphics/Rect;)V

    #@22
    .line 336
    invoke-virtual {v2, p1, p2}, Landroid/graphics/Rect;->contains(II)Z

    #@25
    move-result v4

    #@26
    if-eqz v4, :cond_1

    #@28
    .line 337
    iget v4, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mFirstPosition:I

    #@2a
    add-int/2addr v4, v3

    #@2b
    .line 341
    .end local v0    # "child":Landroid/view/View;
    :goto_1
    return v4

    #@2c
    .line 332
    .restart local v0    # "child":Landroid/view/View;
    :cond_1
    add-int/lit8 v3, v3, -0x1

    #@2e
    goto :goto_0

    #@2f
    .line 341
    .end local v0    # "child":Landroid/view/View;
    :cond_2
    const/4 v4, -0x1

    #@30
    goto :goto_1
.end method

.method recycleAllViews()V
    .locals 6

    #@0
    .prologue
    .line 235
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildCount()I

    #@3
    move-result v0

    #@4
    .line 236
    .local v0, "childCount":I
    iget-object v4, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@6
    .line 237
    .local v4, "recycleBin":Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;
    iget v3, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mFirstPosition:I

    #@8
    .line 240
    .local v3, "position":I
    const/4 v1, 0x0

    #@9
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@b
    .line 241
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildAt(I)Landroid/view/View;

    #@e
    move-result-object v5

    #@f
    .line 242
    .local v5, "v":Landroid/view/View;
    add-int v2, v3, v1

    #@11
    .line 243
    .local v2, "index":I
    invoke-virtual {v4, v2, v5}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->put(ILandroid/view/View;)V

    #@14
    .line 240
    add-int/lit8 v1, v1, 0x1

    #@16
    goto :goto_0

    #@17
    .line 245
    .end local v2    # "index":I
    .end local v5    # "v":Landroid/view/View;
    :cond_0
    return-void
.end method

.method public requestLayout()V
    .locals 1

    #@0
    .prologue
    .line 301
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mBlockLayoutRequests:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 302
    invoke-super {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->requestLayout()V

    #@7
    .line 304
    :cond_0
    return-void
.end method

.method resetList()V
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    const/4 v2, -0x1

    #@2
    .line 125
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataChanged:Z

    #@4
    .line 126
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mNeedSync:Z

    #@6
    .line 128
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->removeAllViewsInLayout()V

    #@9
    .line 129
    iput v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldSelectedPosition:I

    #@b
    .line 130
    const-wide/high16 v0, -0x8000000000000000L

    #@d
    iput-wide v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldSelectedRowId:J

    #@f
    .line 132
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setSelectedPositionInt(I)V

    #@12
    .line 133
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setNextSelectedPositionInt(I)V

    #@15
    .line 134
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->invalidate()V

    #@18
    .line 135
    return-void
.end method

.method public bridge synthetic setAdapter(Landroid/widget/Adapter;)V
    .locals 0
    .param p1, "x0"    # Landroid/widget/Adapter;

    #@0
    .prologue
    .line 35
    check-cast p1, Landroid/widget/SpinnerAdapter;

    #@2
    .end local p1    # "x0":Landroid/widget/Adapter;
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    #@5
    return-void
.end method

.method public setAdapter(Landroid/widget/SpinnerAdapter;)V
    .locals 4
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;

    #@0
    .prologue
    const/4 v0, -0x1

    #@1
    .line 83
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@3
    if-eqz v1, :cond_0

    #@5
    .line 84
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@7
    iget-object v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@9
    invoke-interface {v1, v2}, Landroid/widget/SpinnerAdapter;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@c
    .line 85
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->resetList()V

    #@f
    .line 88
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@11
    .line 90
    iput v0, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldSelectedPosition:I

    #@13
    .line 91
    const-wide/high16 v2, -0x8000000000000000L

    #@15
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldSelectedRowId:J

    #@17
    .line 93
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@19
    if-eqz v1, :cond_3

    #@1b
    .line 94
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@1d
    iput v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldItemCount:I

    #@1f
    .line 95
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@21
    invoke-interface {v1}, Landroid/widget/SpinnerAdapter;->getCount()I

    #@24
    move-result v1

    #@25
    iput v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@27
    .line 96
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->checkFocus()V

    #@2a
    .line 98
    new-instance v1, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;

    #@2c
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;-><init>(Landroid/support/v7/internal/widget/AdapterViewCompat;)V

    #@2f
    iput-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@31
    .line 99
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@33
    iget-object v2, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@35
    invoke-interface {v1, v2}, Landroid/widget/SpinnerAdapter;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@38
    .line 101
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@3a
    if-lez v1, :cond_1

    #@3c
    const/4 v0, 0x0

    #@3d
    .line 103
    .local v0, "position":I
    :cond_1
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setSelectedPositionInt(I)V

    #@40
    .line 104
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setNextSelectedPositionInt(I)V

    #@43
    .line 106
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mItemCount:I

    #@45
    if-nez v1, :cond_2

    #@47
    .line 108
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->checkSelectionChanged()V

    #@4a
    .line 118
    .end local v0    # "position":I
    :cond_2
    :goto_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->requestLayout()V

    #@4d
    .line 119
    return-void

    #@4e
    .line 112
    :cond_3
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->checkFocus()V

    #@51
    .line 113
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->resetList()V

    #@54
    .line 115
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->checkSelectionChanged()V

    #@57
    goto :goto_0
.end method

.method public setSelection(I)V
    .locals 0
    .param p1, "position"    # I

    #@0
    .prologue
    .line 259
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setNextSelectedPositionInt(I)V

    #@3
    .line 260
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->requestLayout()V

    #@6
    .line 261
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->invalidate()V

    #@9
    .line 262
    return-void
.end method

.method public setSelection(IZ)V
    .locals 3
    .param p1, "position"    # I
    .param p2, "animate"    # Z

    #@0
    .prologue
    .line 252
    if-eqz p2, :cond_0

    #@2
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mFirstPosition:I

    #@4
    if-gt v1, p1, :cond_0

    #@6
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mFirstPosition:I

    #@8
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->getChildCount()I

    #@b
    move-result v2

    #@c
    add-int/2addr v1, v2

    #@d
    add-int/lit8 v1, v1, -0x1

    #@f
    if-gt p1, v1, :cond_0

    #@11
    const/4 v0, 0x1

    #@12
    .line 254
    .local v0, "shouldAnimate":Z
    :goto_0
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setSelectionInt(IZ)V

    #@15
    .line 255
    return-void

    #@16
    .line 252
    .end local v0    # "shouldAnimate":Z
    :cond_0
    const/4 v0, 0x0

    #@17
    goto :goto_0
.end method

.method setSelectionInt(IZ)V
    .locals 2
    .param p1, "position"    # I
    .param p2, "animate"    # Z

    #@0
    .prologue
    .line 273
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mOldSelectedPosition:I

    #@2
    if-eq p1, v1, :cond_0

    #@4
    .line 274
    const/4 v1, 0x1

    #@5
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mBlockLayoutRequests:Z

    #@7
    .line 275
    iget v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mSelectedPosition:I

    #@9
    sub-int v0, p1, v1

    #@b
    .line 276
    .local v0, "delta":I
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setNextSelectedPositionInt(I)V

    #@e
    .line 277
    invoke-virtual {p0, v0, p2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->layout(IZ)V

    #@11
    .line 278
    const/4 v1, 0x0

    #@12
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->mBlockLayoutRequests:Z

    #@14
    .line 280
    .end local v0    # "delta":I
    :cond_0
    return-void
.end method
