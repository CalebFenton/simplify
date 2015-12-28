.class public Landroid/support/v7/internal/widget/ListViewCompat;
.super Landroid/widget/ListView;
.source "ListViewCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;
    }
.end annotation


# static fields
.field public static final INVALID_POSITION:I = -0x1

.field public static final NO_POSITION:I = -0x1

.field private static final STATE_SET_NOTHING:[I


# instance fields
.field private mIsChildViewEnabled:Ljava/lang/reflect/Field;

.field mSelectionBottomPadding:I

.field mSelectionLeftPadding:I

.field mSelectionRightPadding:I

.field mSelectionTopPadding:I

.field private mSelector:Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

.field final mSelectorRect:Landroid/graphics/Rect;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 44
    const/4 v0, 0x1

    #@2
    new-array v0, v0, [I

    #@4
    aput v1, v0, v1

    #@6
    sput-object v0, Landroid/support/v7/internal/widget/ListViewCompat;->STATE_SET_NOTHING:[I

    #@8
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 57
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/ListViewCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 58
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 61
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/ListViewCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 62
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 65
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/ListView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 46
    new-instance v1, Landroid/graphics/Rect;

    #@6
    invoke-direct {v1}, Landroid/graphics/Rect;-><init>()V

    #@9
    iput-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectorRect:Landroid/graphics/Rect;

    #@b
    .line 47
    iput v2, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionLeftPadding:I

    #@d
    .line 48
    iput v2, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionTopPadding:I

    #@f
    .line 49
    iput v2, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionRightPadding:I

    #@11
    .line 50
    iput v2, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionBottomPadding:I

    #@13
    .line 68
    :try_start_0
    const-class v1, Landroid/widget/AbsListView;

    #@15
    const-string v2, "mIsChildViewEnabled"

    #@17
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    #@1a
    move-result-object v1

    #@1b
    iput-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mIsChildViewEnabled:Ljava/lang/reflect/Field;

    #@1d
    .line 69
    iget-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mIsChildViewEnabled:Ljava/lang/reflect/Field;

    #@1f
    const/4 v2, 0x1

    #@20
    invoke-virtual {v1, v2}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    :try_end_0
    .catch Ljava/lang/NoSuchFieldException; {:try_start_0 .. :try_end_0} :catch_0

    #@23
    .line 73
    :goto_0
    return-void

    #@24
    .line 70
    :catch_0
    move-exception v0

    #@25
    .line 71
    .local v0, "e":Ljava/lang/NoSuchFieldException;
    invoke-virtual {v0}, Ljava/lang/NoSuchFieldException;->printStackTrace()V

    #@28
    goto :goto_0
.end method


# virtual methods
.method protected dispatchDraw(Landroid/graphics/Canvas;)V
    .locals 1
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 97
    const/4 v0, 0x0

    #@1
    .line 99
    .local v0, "drawSelectorOnTop":Z
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ListViewCompat;->drawSelectorCompat(Landroid/graphics/Canvas;)V

    #@4
    .line 102
    invoke-super {p0, p1}, Landroid/widget/ListView;->dispatchDraw(Landroid/graphics/Canvas;)V

    #@7
    .line 103
    return-void
.end method

.method protected drawSelectorCompat(Landroid/graphics/Canvas;)V
    .locals 2
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 121
    iget-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectorRect:Landroid/graphics/Rect;

    #@2
    invoke-virtual {v1}, Landroid/graphics/Rect;->isEmpty()Z

    #@5
    move-result v1

    #@6
    if-nez v1, :cond_0

    #@8
    .line 122
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getSelector()Landroid/graphics/drawable/Drawable;

    #@b
    move-result-object v0

    #@c
    .line 123
    .local v0, "selector":Landroid/graphics/drawable/Drawable;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectorRect:Landroid/graphics/Rect;

    #@e
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setBounds(Landroid/graphics/Rect;)V

    #@11
    .line 124
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@14
    .line 126
    .end local v0    # "selector":Landroid/graphics/drawable/Drawable;
    :cond_0
    return-void
.end method

.method protected drawableStateChanged()V
    .locals 2

    #@0
    .prologue
    .line 90
    invoke-super {p0}, Landroid/widget/ListView;->drawableStateChanged()V

    #@3
    .line 91
    iget-object v0, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelector:Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

    #@5
    const/4 v1, 0x1

    #@6
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;->setEnabled(Z)V

    #@9
    .line 92
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->updateSelectorStateCompat()V

    #@c
    .line 93
    return-void
.end method

.method public lookForSelectablePosition(IZ)I
    .locals 4
    .param p1, "position"    # I
    .param p2, "lookDown"    # Z

    #@0
    .prologue
    const/4 v2, -0x1

    #@1
    .line 137
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getAdapter()Landroid/widget/ListAdapter;

    #@4
    move-result-object v0

    #@5
    .line 138
    .local v0, "adapter":Landroid/widget/ListAdapter;
    if-eqz v0, :cond_0

    #@7
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->isInTouchMode()Z

    #@a
    move-result v3

    #@b
    if-eqz v3, :cond_1

    #@d
    .line 164
    :cond_0
    :goto_0
    return v2

    #@e
    .line 142
    :cond_1
    invoke-interface {v0}, Landroid/widget/ListAdapter;->getCount()I

    #@11
    move-result v1

    #@12
    .line 143
    .local v1, "count":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getAdapter()Landroid/widget/ListAdapter;

    #@15
    move-result-object v3

    #@16
    invoke-interface {v3}, Landroid/widget/ListAdapter;->areAllItemsEnabled()Z

    #@19
    move-result v3

    #@1a
    if-nez v3, :cond_4

    #@1c
    .line 144
    if-eqz p2, :cond_2

    #@1e
    .line 145
    const/4 v3, 0x0

    #@1f
    invoke-static {v3, p1}, Ljava/lang/Math;->max(II)I

    #@22
    move-result p1

    #@23
    .line 146
    :goto_1
    if-ge p1, v1, :cond_3

    #@25
    invoke-interface {v0, p1}, Landroid/widget/ListAdapter;->isEnabled(I)Z

    #@28
    move-result v3

    #@29
    if-nez v3, :cond_3

    #@2b
    .line 147
    add-int/lit8 p1, p1, 0x1

    #@2d
    goto :goto_1

    #@2e
    .line 150
    :cond_2
    add-int/lit8 v3, v1, -0x1

    #@30
    invoke-static {p1, v3}, Ljava/lang/Math;->min(II)I

    #@33
    move-result p1

    #@34
    .line 151
    :goto_2
    if-ltz p1, :cond_3

    #@36
    invoke-interface {v0, p1}, Landroid/widget/ListAdapter;->isEnabled(I)Z

    #@39
    move-result v3

    #@3a
    if-nez v3, :cond_3

    #@3c
    .line 152
    add-int/lit8 p1, p1, -0x1

    #@3e
    goto :goto_2

    #@3f
    .line 156
    :cond_3
    if-ltz p1, :cond_0

    #@41
    if-ge p1, v1, :cond_0

    #@43
    move v2, p1

    #@44
    .line 159
    goto :goto_0

    #@45
    .line 161
    :cond_4
    if-ltz p1, :cond_0

    #@47
    if-ge p1, v1, :cond_0

    #@49
    move v2, p1

    #@4a
    .line 164
    goto :goto_0
.end method

.method public measureHeightOfChildrenCompat(IIIII)I
    .locals 21
    .param p1, "widthMeasureSpec"    # I
    .param p2, "startPosition"    # I
    .param p3, "endPosition"    # I
    .param p4, "maxHeight"    # I
    .param p5, "disallowPartialChildPosition"    # I

    #@0
    .prologue
    .line 252
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getListPaddingTop()I

    #@3
    move-result v14

    #@4
    .line 253
    .local v14, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getListPaddingBottom()I

    #@7
    move-result v11

    #@8
    .line 254
    .local v11, "paddingBottom":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getListPaddingLeft()I

    #@b
    move-result v12

    #@c
    .line 255
    .local v12, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getListPaddingRight()I

    #@f
    move-result v13

    #@10
    .line 256
    .local v13, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getDividerHeight()I

    #@13
    move-result v16

    #@14
    .line 257
    .local v16, "reportedDividerHeight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getDivider()Landroid/graphics/drawable/Drawable;

    #@17
    move-result-object v6

    #@18
    .line 259
    .local v6, "divider":Landroid/graphics/drawable/Drawable;
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getAdapter()Landroid/widget/ListAdapter;

    #@1b
    move-result-object v2

    #@1c
    .line 261
    .local v2, "adapter":Landroid/widget/ListAdapter;
    if-nez v2, :cond_1

    #@1e
    .line 262
    add-int v15, v14, v11

    #@20
    .line 321
    :cond_0
    :goto_0
    return v15

    #@21
    .line 266
    :cond_1
    add-int v17, v14, v11

    #@23
    .line 267
    .local v17, "returnedHeight":I
    if-lez v16, :cond_5

    #@25
    if-eqz v6, :cond_5

    #@27
    move/from16 v7, v16

    #@29
    .line 272
    .local v7, "dividerHeight":I
    :goto_1
    const/4 v15, 0x0

    #@2a
    .line 274
    .local v15, "prevHeightWithoutPartialChild":I
    const/4 v3, 0x0

    #@2b
    .line 275
    .local v3, "child":Landroid/view/View;
    const/16 v18, 0x0

    #@2d
    .line 276
    .local v18, "viewType":I
    invoke-interface {v2}, Landroid/widget/ListAdapter;->getCount()I

    #@30
    move-result v5

    #@31
    .line 277
    .local v5, "count":I
    const/4 v9, 0x0

    #@32
    .local v9, "i":I
    :goto_2
    if-ge v9, v5, :cond_9

    #@34
    .line 278
    invoke-interface {v2, v9}, Landroid/widget/ListAdapter;->getItemViewType(I)I

    #@37
    move-result v10

    #@38
    .line 279
    .local v10, "newType":I
    move/from16 v0, v18

    #@3a
    if-eq v10, v0, :cond_2

    #@3c
    .line 280
    const/4 v3, 0x0

    #@3d
    .line 281
    move/from16 v18, v10

    #@3f
    .line 283
    :cond_2
    move-object/from16 v0, p0

    #@41
    invoke-interface {v2, v9, v3, v0}, Landroid/widget/ListAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@44
    move-result-object v3

    #@45
    .line 287
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@48
    move-result-object v4

    #@49
    .line 288
    .local v4, "childLp":Landroid/view/ViewGroup$LayoutParams;
    if-eqz v4, :cond_6

    #@4b
    iget v0, v4, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@4d
    move/from16 v19, v0

    #@4f
    if-lez v19, :cond_6

    #@51
    .line 289
    iget v0, v4, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@53
    move/from16 v19, v0

    #@55
    const/high16 v20, 0x40000000    # 2.0f

    #@57
    invoke-static/range {v19 .. v20}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@5a
    move-result v8

    #@5b
    .line 294
    .local v8, "heightMeasureSpec":I
    :goto_3
    move/from16 v0, p1

    #@5d
    invoke-virtual {v3, v0, v8}, Landroid/view/View;->measure(II)V

    #@60
    .line 296
    if-lez v9, :cond_3

    #@62
    .line 298
    add-int v17, v17, v7

    #@64
    .line 301
    :cond_3
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredHeight()I

    #@67
    move-result v19

    #@68
    add-int v17, v17, v19

    #@6a
    .line 303
    move/from16 v0, v17

    #@6c
    move/from16 v1, p4

    #@6e
    if-lt v0, v1, :cond_7

    #@70
    .line 306
    if-ltz p5, :cond_4

    #@72
    move/from16 v0, p5

    #@74
    if-le v9, v0, :cond_4

    #@76
    if-lez v15, :cond_4

    #@78
    move/from16 v0, v17

    #@7a
    move/from16 v1, p4

    #@7c
    if-ne v0, v1, :cond_0

    #@7e
    :cond_4
    move/from16 v15, p4

    #@80
    goto :goto_0

    #@81
    .line 267
    .end local v3    # "child":Landroid/view/View;
    .end local v4    # "childLp":Landroid/view/ViewGroup$LayoutParams;
    .end local v5    # "count":I
    .end local v7    # "dividerHeight":I
    .end local v8    # "heightMeasureSpec":I
    .end local v9    # "i":I
    .end local v10    # "newType":I
    .end local v15    # "prevHeightWithoutPartialChild":I
    .end local v18    # "viewType":I
    :cond_5
    const/4 v7, 0x0

    #@82
    goto :goto_1

    #@83
    .line 292
    .restart local v3    # "child":Landroid/view/View;
    .restart local v4    # "childLp":Landroid/view/ViewGroup$LayoutParams;
    .restart local v5    # "count":I
    .restart local v7    # "dividerHeight":I
    .restart local v9    # "i":I
    .restart local v10    # "newType":I
    .restart local v15    # "prevHeightWithoutPartialChild":I
    .restart local v18    # "viewType":I
    :cond_6
    const/16 v19, 0x0

    #@85
    const/16 v20, 0x0

    #@87
    invoke-static/range {v19 .. v20}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@8a
    move-result v8

    #@8b
    .restart local v8    # "heightMeasureSpec":I
    goto :goto_3

    #@8c
    .line 314
    :cond_7
    if-ltz p5, :cond_8

    #@8e
    move/from16 v0, p5

    #@90
    if-lt v9, v0, :cond_8

    #@92
    .line 315
    move/from16 v15, v17

    #@94
    .line 277
    :cond_8
    add-int/lit8 v9, v9, 0x1

    #@96
    goto :goto_2

    #@97
    .end local v4    # "childLp":Landroid/view/ViewGroup$LayoutParams;
    .end local v8    # "heightMeasureSpec":I
    .end local v10    # "newType":I
    :cond_9
    move/from16 v15, v17

    #@99
    .line 321
    goto :goto_0
.end method

.method protected positionSelectorCompat(ILandroid/view/View;)V
    .locals 7
    .param p1, "position"    # I
    .param p2, "sel"    # Landroid/view/View;

    #@0
    .prologue
    .line 198
    iget-object v2, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectorRect:Landroid/graphics/Rect;

    #@2
    .line 199
    .local v2, "selectorRect":Landroid/graphics/Rect;
    invoke-virtual {p2}, Landroid/view/View;->getLeft()I

    #@5
    move-result v3

    #@6
    invoke-virtual {p2}, Landroid/view/View;->getTop()I

    #@9
    move-result v4

    #@a
    invoke-virtual {p2}, Landroid/view/View;->getRight()I

    #@d
    move-result v5

    #@e
    invoke-virtual {p2}, Landroid/view/View;->getBottom()I

    #@11
    move-result v6

    #@12
    invoke-virtual {v2, v3, v4, v5, v6}, Landroid/graphics/Rect;->set(IIII)V

    #@15
    .line 202
    iget v3, v2, Landroid/graphics/Rect;->left:I

    #@17
    iget v4, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionLeftPadding:I

    #@19
    sub-int/2addr v3, v4

    #@1a
    iput v3, v2, Landroid/graphics/Rect;->left:I

    #@1c
    .line 203
    iget v3, v2, Landroid/graphics/Rect;->top:I

    #@1e
    iget v4, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionTopPadding:I

    #@20
    sub-int/2addr v3, v4

    #@21
    iput v3, v2, Landroid/graphics/Rect;->top:I

    #@23
    .line 204
    iget v3, v2, Landroid/graphics/Rect;->right:I

    #@25
    iget v4, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionRightPadding:I

    #@27
    add-int/2addr v3, v4

    #@28
    iput v3, v2, Landroid/graphics/Rect;->right:I

    #@2a
    .line 205
    iget v3, v2, Landroid/graphics/Rect;->bottom:I

    #@2c
    iget v4, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionBottomPadding:I

    #@2e
    add-int/2addr v3, v4

    #@2f
    iput v3, v2, Landroid/graphics/Rect;->bottom:I

    #@31
    .line 210
    :try_start_0
    iget-object v3, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mIsChildViewEnabled:Ljava/lang/reflect/Field;

    #@33
    invoke-virtual {v3, p0}, Ljava/lang/reflect/Field;->getBoolean(Ljava/lang/Object;)Z

    #@36
    move-result v1

    #@37
    .line 211
    .local v1, "isChildViewEnabled":Z
    invoke-virtual {p2}, Landroid/view/View;->isEnabled()Z

    #@3a
    move-result v3

    #@3b
    if-eq v3, v1, :cond_0

    #@3d
    .line 212
    iget-object v4, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mIsChildViewEnabled:Ljava/lang/reflect/Field;

    #@3f
    if-nez v1, :cond_1

    #@41
    const/4 v3, 0x1

    #@42
    :goto_0
    invoke-static {v3}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@45
    move-result-object v3

    #@46
    invoke-virtual {v4, p0, v3}, Ljava/lang/reflect/Field;->set(Ljava/lang/Object;Ljava/lang/Object;)V

    #@49
    .line 213
    const/4 v3, -0x1

    #@4a
    if-eq p1, v3, :cond_0

    #@4c
    .line 214
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->refreshDrawableState()V
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_0

    #@4f
    .line 220
    .end local v1    # "isChildViewEnabled":Z
    :cond_0
    :goto_1
    return-void

    #@50
    .line 212
    .restart local v1    # "isChildViewEnabled":Z
    :cond_1
    const/4 v3, 0x0

    #@51
    goto :goto_0

    #@52
    .line 217
    .end local v1    # "isChildViewEnabled":Z
    :catch_0
    move-exception v0

    #@53
    .line 218
    .local v0, "e":Ljava/lang/IllegalAccessException;
    invoke-virtual {v0}, Ljava/lang/IllegalAccessException;->printStackTrace()V

    #@56
    goto :goto_1
.end method

.method protected positionSelectorLikeFocusCompat(ILandroid/view/View;)V
    .locals 8
    .param p1, "position"    # I
    .param p2, "sel"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 180
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getSelector()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v2

    #@6
    .line 181
    .local v2, "selector":Landroid/graphics/drawable/Drawable;
    if-eqz v2, :cond_2

    #@8
    const/4 v7, -0x1

    #@9
    if-eq p1, v7, :cond_2

    #@b
    move v1, v5

    #@c
    .line 182
    .local v1, "manageState":Z
    :goto_0
    if-eqz v1, :cond_0

    #@e
    .line 183
    invoke-virtual {v2, v6, v6}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@11
    .line 186
    :cond_0
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/internal/widget/ListViewCompat;->positionSelectorCompat(ILandroid/view/View;)V

    #@14
    .line 188
    if-eqz v1, :cond_1

    #@16
    .line 189
    iget-object v0, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectorRect:Landroid/graphics/Rect;

    #@18
    .line 190
    .local v0, "bounds":Landroid/graphics/Rect;
    invoke-virtual {v0}, Landroid/graphics/Rect;->exactCenterX()F

    #@1b
    move-result v3

    #@1c
    .line 191
    .local v3, "x":F
    invoke-virtual {v0}, Landroid/graphics/Rect;->exactCenterY()F

    #@1f
    move-result v4

    #@20
    .line 192
    .local v4, "y":F
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getVisibility()I

    #@23
    move-result v7

    #@24
    if-nez v7, :cond_3

    #@26
    :goto_1
    invoke-virtual {v2, v5, v6}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@29
    .line 193
    invoke-static {v2, v3, v4}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspot(Landroid/graphics/drawable/Drawable;FF)V

    #@2c
    .line 195
    .end local v0    # "bounds":Landroid/graphics/Rect;
    .end local v3    # "x":F
    .end local v4    # "y":F
    :cond_1
    return-void

    #@2d
    .end local v1    # "manageState":Z
    :cond_2
    move v1, v6

    #@2e
    .line 181
    goto :goto_0

    #@2f
    .restart local v0    # "bounds":Landroid/graphics/Rect;
    .restart local v1    # "manageState":Z
    .restart local v3    # "x":F
    .restart local v4    # "y":F
    :cond_3
    move v5, v6

    #@30
    .line 192
    goto :goto_1
.end method

.method protected positionSelectorLikeTouchCompat(ILandroid/view/View;FF)V
    .locals 2
    .param p1, "position"    # I
    .param p2, "sel"    # Landroid/view/View;
    .param p3, "x"    # F
    .param p4, "y"    # F

    #@0
    .prologue
    .line 169
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/internal/widget/ListViewCompat;->positionSelectorLikeFocusCompat(ILandroid/view/View;)V

    #@3
    .line 171
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getSelector()Landroid/graphics/drawable/Drawable;

    #@6
    move-result-object v0

    #@7
    .line 172
    .local v0, "selector":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@9
    const/4 v1, -0x1

    #@a
    if-eq p1, v1, :cond_0

    #@c
    .line 173
    invoke-static {v0, p3, p4}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspot(Landroid/graphics/drawable/Drawable;FF)V

    #@f
    .line 175
    :cond_0
    return-void
.end method

.method public setSelector(Landroid/graphics/drawable/Drawable;)V
    .locals 2
    .param p1, "sel"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 77
    new-instance v1, Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

    #@2
    invoke-direct {v1, p1}, Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;-><init>(Landroid/graphics/drawable/Drawable;)V

    #@5
    iput-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelector:Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

    #@7
    .line 78
    iget-object v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelector:Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

    #@9
    invoke-super {p0, v1}, Landroid/widget/ListView;->setSelector(Landroid/graphics/drawable/Drawable;)V

    #@c
    .line 80
    new-instance v0, Landroid/graphics/Rect;

    #@e
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@11
    .line 81
    .local v0, "padding":Landroid/graphics/Rect;
    invoke-virtual {p1, v0}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@14
    .line 82
    iget v1, v0, Landroid/graphics/Rect;->left:I

    #@16
    iput v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionLeftPadding:I

    #@18
    .line 83
    iget v1, v0, Landroid/graphics/Rect;->top:I

    #@1a
    iput v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionTopPadding:I

    #@1c
    .line 84
    iget v1, v0, Landroid/graphics/Rect;->right:I

    #@1e
    iput v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionRightPadding:I

    #@20
    .line 85
    iget v1, v0, Landroid/graphics/Rect;->bottom:I

    #@22
    iput v1, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelectionBottomPadding:I

    #@24
    .line 86
    return-void
.end method

.method protected setSelectorEnabled(Z)V
    .locals 1
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 325
    iget-object v0, p0, Landroid/support/v7/internal/widget/ListViewCompat;->mSelector:Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ListViewCompat$GateKeeperDrawable;->setEnabled(Z)V

    #@5
    .line 326
    return-void
.end method

.method protected shouldShowSelectorCompat()Z
    .locals 1

    #@0
    .prologue
    .line 113
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->touchModeDrawsInPressedStateCompat()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->isPressed()Z

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

.method protected touchModeDrawsInPressedStateCompat()Z
    .locals 1

    #@0
    .prologue
    .line 117
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method protected updateSelectorStateCompat()V
    .locals 2

    #@0
    .prologue
    .line 106
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getSelector()Landroid/graphics/drawable/Drawable;

    #@3
    move-result-object v0

    #@4
    .line 107
    .local v0, "selector":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->shouldShowSelectorCompat()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 108
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->getDrawableState()[I

    #@f
    move-result-object v1

    #@10
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@13
    .line 110
    :cond_0
    return-void
.end method
