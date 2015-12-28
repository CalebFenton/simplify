.class public Landroid/support/v7/internal/widget/ActionBarContainer;
.super Landroid/widget/FrameLayout;
.source "ActionBarContainer.java"


# instance fields
.field private mActionBarView:Landroid/view/View;

.field mBackground:Landroid/graphics/drawable/Drawable;

.field private mContextView:Landroid/view/View;

.field private mHeight:I

.field mIsSplit:Z

.field mIsStacked:Z

.field private mIsTransitioning:Z

.field mSplitBackground:Landroid/graphics/drawable/Drawable;

.field mStackedBackground:Landroid/graphics/drawable/Drawable;

.field private mTabContainer:Landroid/view/View;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 51
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 52
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 6
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    const/4 v2, 0x1

    #@2
    .line 55
    invoke-direct {p0, p1, p2}, Landroid/widget/FrameLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@5
    .line 58
    invoke-static {}, Landroid/support/v7/internal/VersionUtils;->isAtLeastL()Z

    #@8
    move-result v4

    #@9
    if-eqz v4, :cond_2

    #@b
    new-instance v1, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;

    #@d
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawableV21;-><init>(Landroid/support/v7/internal/widget/ActionBarContainer;)V

    #@10
    .line 61
    .local v1, "bg":Landroid/graphics/drawable/Drawable;
    :goto_0
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@13
    .line 63
    sget-object v4, Landroid/support/v7/appcompat/R$styleable;->ActionBar:[I

    #@15
    invoke-virtual {p1, p2, v4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@18
    move-result-object v0

    #@19
    .line 65
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v4, Landroid/support/v7/appcompat/R$styleable;->ActionBar_background:I

    #@1b
    invoke-virtual {v0, v4}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@1e
    move-result-object v4

    #@1f
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@21
    .line 66
    sget v4, Landroid/support/v7/appcompat/R$styleable;->ActionBar_backgroundStacked:I

    #@23
    invoke-virtual {v0, v4}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@26
    move-result-object v4

    #@27
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@29
    .line 68
    sget v4, Landroid/support/v7/appcompat/R$styleable;->ActionBar_height:I

    #@2b
    const/4 v5, -0x1

    #@2c
    invoke-virtual {v0, v4, v5}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@2f
    move-result v4

    #@30
    iput v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mHeight:I

    #@32
    .line 70
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getId()I

    #@35
    move-result v4

    #@36
    sget v5, Landroid/support/v7/appcompat/R$id;->split_action_bar:I

    #@38
    if-ne v4, v5, :cond_0

    #@3a
    .line 71
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@3c
    .line 72
    sget v4, Landroid/support/v7/appcompat/R$styleable;->ActionBar_backgroundSplit:I

    #@3e
    invoke-virtual {v0, v4}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@41
    move-result-object v4

    #@42
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@44
    .line 74
    :cond_0
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@47
    .line 76
    iget-boolean v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@49
    if-eqz v4, :cond_4

    #@4b
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@4d
    if-nez v4, :cond_3

    #@4f
    :cond_1
    :goto_1
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->setWillNotDraw(Z)V

    #@52
    .line 78
    return-void

    #@53
    .line 58
    .end local v0    # "a":Landroid/content/res/TypedArray;
    .end local v1    # "bg":Landroid/graphics/drawable/Drawable;
    :cond_2
    new-instance v1, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;

    #@55
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/ActionBarBackgroundDrawable;-><init>(Landroid/support/v7/internal/widget/ActionBarContainer;)V

    #@58
    goto :goto_0

    #@59
    .restart local v0    # "a":Landroid/content/res/TypedArray;
    .restart local v1    # "bg":Landroid/graphics/drawable/Drawable;
    :cond_3
    move v2, v3

    #@5a
    .line 76
    goto :goto_1

    #@5b
    :cond_4
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@5d
    if-nez v4, :cond_5

    #@5f
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@61
    if-eqz v4, :cond_1

    #@63
    :cond_5
    move v2, v3

    #@64
    goto :goto_1
.end method

.method private getMeasuredHeightWithMargins(Landroid/view/View;)I
    .locals 3
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 245
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/widget/FrameLayout$LayoutParams;

    #@6
    .line 246
    .local v0, "lp":Landroid/widget/FrameLayout$LayoutParams;
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@9
    move-result v1

    #@a
    iget v2, v0, Landroid/widget/FrameLayout$LayoutParams;->topMargin:I

    #@c
    add-int/2addr v1, v2

    #@d
    iget v2, v0, Landroid/widget/FrameLayout$LayoutParams;->bottomMargin:I

    #@f
    add-int/2addr v1, v2

    #@10
    return v1
.end method

.method private isCollapsed(Landroid/view/View;)Z
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 241
    if-eqz p1, :cond_0

    #@2
    invoke-virtual {p1}, Landroid/view/View;->getVisibility()I

    #@5
    move-result v0

    #@6
    const/16 v1, 0x8

    #@8
    if-eq v0, v1, :cond_0

    #@a
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@d
    move-result v0

    #@e
    if-nez v0, :cond_1

    #@10
    :cond_0
    const/4 v0, 0x1

    #@11
    :goto_0
    return v0

    #@12
    :cond_1
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method


# virtual methods
.method protected drawableStateChanged()V
    .locals 2

    #@0
    .prologue
    .line 157
    invoke-super {p0}, Landroid/widget/FrameLayout;->drawableStateChanged()V

    #@3
    .line 158
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@5
    if-eqz v0, :cond_0

    #@7
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@9
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->isStateful()Z

    #@c
    move-result v0

    #@d
    if-eqz v0, :cond_0

    #@f
    .line 159
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@11
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getDrawableState()[I

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@18
    .line 161
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@1a
    if-eqz v0, :cond_1

    #@1c
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->isStateful()Z

    #@21
    move-result v0

    #@22
    if-eqz v0, :cond_1

    #@24
    .line 162
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@26
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getDrawableState()[I

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@2d
    .line 164
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@2f
    if-eqz v0, :cond_2

    #@31
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@33
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->isStateful()Z

    #@36
    move-result v0

    #@37
    if-eqz v0, :cond_2

    #@39
    .line 165
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@3b
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getDrawableState()[I

    #@3e
    move-result-object v1

    #@3f
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@42
    .line 167
    :cond_2
    return-void
.end method

.method public getTabContainer()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 225
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public jumpDrawablesToCurrentState()V
    .locals 2

    #@0
    .prologue
    .line 170
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xb

    #@4
    if-lt v0, v1, :cond_2

    #@6
    .line 171
    invoke-super {p0}, Landroid/widget/FrameLayout;->jumpDrawablesToCurrentState()V

    #@9
    .line 172
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@b
    if-eqz v0, :cond_0

    #@d
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@f
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->jumpToCurrentState()V

    #@12
    .line 175
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@14
    if-eqz v0, :cond_1

    #@16
    .line 176
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@18
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->jumpToCurrentState()V

    #@1b
    .line 178
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@1d
    if-eqz v0, :cond_2

    #@1f
    .line 179
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@21
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->jumpToCurrentState()V

    #@24
    .line 182
    :cond_2
    return-void
.end method

.method public onFinishInflate()V
    .locals 1

    #@0
    .prologue
    .line 82
    invoke-super {p0}, Landroid/widget/FrameLayout;->onFinishInflate()V

    #@3
    .line 83
    sget v0, Landroid/support/v7/appcompat/R$id;->action_bar:I

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->findViewById(I)Landroid/view/View;

    #@8
    move-result-object v0

    #@9
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@b
    .line 84
    sget v0, Landroid/support/v7/appcompat/R$id;->action_context_bar:I

    #@d
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->findViewById(I)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@13
    .line 85
    return-void
.end method

.method public onInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 199
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsTransitioning:Z

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-super {p0, p1}, Landroid/widget/FrameLayout;->onInterceptTouchEvent(Landroid/view/MotionEvent;)Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public onLayout(ZIIII)V
    .locals 11
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 281
    invoke-super/range {p0 .. p5}, Landroid/widget/FrameLayout;->onLayout(ZIIII)V

    #@3
    .line 283
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@5
    .line 284
    .local v4, "tabContainer":Landroid/view/View;
    if-eqz v4, :cond_3

    #@7
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@a
    move-result v6

    #@b
    const/16 v7, 0x8

    #@d
    if-eq v6, v7, :cond_3

    #@f
    const/4 v1, 0x1

    #@10
    .line 286
    .local v1, "hasTabs":Z
    :goto_0
    if-eqz v4, :cond_0

    #@12
    invoke-virtual {v4}, Landroid/view/View;->getVisibility()I

    #@15
    move-result v6

    #@16
    const/16 v7, 0x8

    #@18
    if-eq v6, v7, :cond_0

    #@1a
    .line 287
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@1d
    move-result v0

    #@1e
    .line 288
    .local v0, "containerHeight":I
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@21
    move-result-object v2

    #@22
    check-cast v2, Landroid/widget/FrameLayout$LayoutParams;

    #@24
    .line 289
    .local v2, "lp":Landroid/widget/FrameLayout$LayoutParams;
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@27
    move-result v5

    #@28
    .line 290
    .local v5, "tabHeight":I
    sub-int v6, v0, v5

    #@2a
    iget v7, v2, Landroid/widget/FrameLayout$LayoutParams;->bottomMargin:I

    #@2c
    sub-int/2addr v6, v7

    #@2d
    iget v7, v2, Landroid/widget/FrameLayout$LayoutParams;->bottomMargin:I

    #@2f
    sub-int v7, v0, v7

    #@31
    invoke-virtual {v4, p2, v6, p4, v7}, Landroid/view/View;->layout(IIII)V

    #@34
    .line 294
    .end local v0    # "containerHeight":I
    .end local v2    # "lp":Landroid/widget/FrameLayout$LayoutParams;
    .end local v5    # "tabHeight":I
    :cond_0
    const/4 v3, 0x0

    #@35
    .line 295
    .local v3, "needsInvalidate":Z
    iget-boolean v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@37
    if-eqz v6, :cond_4

    #@39
    .line 296
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@3b
    if-eqz v6, :cond_1

    #@3d
    .line 297
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@3f
    const/4 v7, 0x0

    #@40
    const/4 v8, 0x0

    #@41
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredWidth()I

    #@44
    move-result v9

    #@45
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@48
    move-result v10

    #@49
    invoke-virtual {v6, v7, v8, v9, v10}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@4c
    .line 298
    const/4 v3, 0x1

    #@4d
    .line 322
    :cond_1
    :goto_1
    if-eqz v3, :cond_2

    #@4f
    .line 323
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->invalidate()V

    #@52
    .line 325
    :cond_2
    return-void

    #@53
    .line 284
    .end local v1    # "hasTabs":Z
    .end local v3    # "needsInvalidate":Z
    :cond_3
    const/4 v1, 0x0

    #@54
    goto :goto_0

    #@55
    .line 301
    .restart local v1    # "hasTabs":Z
    .restart local v3    # "needsInvalidate":Z
    :cond_4
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@57
    if-eqz v6, :cond_5

    #@59
    .line 302
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@5b
    invoke-virtual {v6}, Landroid/view/View;->getVisibility()I

    #@5e
    move-result v6

    #@5f
    if-nez v6, :cond_6

    #@61
    .line 303
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@63
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@65
    invoke-virtual {v7}, Landroid/view/View;->getLeft()I

    #@68
    move-result v7

    #@69
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@6b
    invoke-virtual {v8}, Landroid/view/View;->getTop()I

    #@6e
    move-result v8

    #@6f
    iget-object v9, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@71
    invoke-virtual {v9}, Landroid/view/View;->getRight()I

    #@74
    move-result v9

    #@75
    iget-object v10, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@77
    invoke-virtual {v10}, Landroid/view/View;->getBottom()I

    #@7a
    move-result v10

    #@7b
    invoke-virtual {v6, v7, v8, v9, v10}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@7e
    .line 312
    :goto_2
    const/4 v3, 0x1

    #@7f
    .line 314
    :cond_5
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsStacked:Z

    #@81
    .line 315
    if-eqz v1, :cond_1

    #@83
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@85
    if-eqz v6, :cond_1

    #@87
    .line 316
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@89
    invoke-virtual {v4}, Landroid/view/View;->getLeft()I

    #@8c
    move-result v7

    #@8d
    invoke-virtual {v4}, Landroid/view/View;->getTop()I

    #@90
    move-result v8

    #@91
    invoke-virtual {v4}, Landroid/view/View;->getRight()I

    #@94
    move-result v9

    #@95
    invoke-virtual {v4}, Landroid/view/View;->getBottom()I

    #@98
    move-result v10

    #@99
    invoke-virtual {v6, v7, v8, v9, v10}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@9c
    .line 318
    const/4 v3, 0x1

    #@9d
    goto :goto_1

    #@9e
    .line 305
    :cond_6
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@a0
    if-eqz v6, :cond_7

    #@a2
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@a4
    invoke-virtual {v6}, Landroid/view/View;->getVisibility()I

    #@a7
    move-result v6

    #@a8
    if-nez v6, :cond_7

    #@aa
    .line 307
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@ac
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@ae
    invoke-virtual {v7}, Landroid/view/View;->getLeft()I

    #@b1
    move-result v7

    #@b2
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@b4
    invoke-virtual {v8}, Landroid/view/View;->getTop()I

    #@b7
    move-result v8

    #@b8
    iget-object v9, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@ba
    invoke-virtual {v9}, Landroid/view/View;->getRight()I

    #@bd
    move-result v9

    #@be
    iget-object v10, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@c0
    invoke-virtual {v10}, Landroid/view/View;->getBottom()I

    #@c3
    move-result v10

    #@c4
    invoke-virtual {v6, v7, v8, v9, v10}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@c7
    goto :goto_2

    #@c8
    .line 310
    :cond_7
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@ca
    const/4 v7, 0x0

    #@cb
    const/4 v8, 0x0

    #@cc
    const/4 v9, 0x0

    #@cd
    const/4 v10, 0x0

    #@ce
    invoke-virtual {v6, v7, v8, v9, v10}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@d1
    goto :goto_2
.end method

.method public onMeasure(II)V
    .locals 6
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/high16 v5, -0x80000000

    #@2
    .line 251
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@4
    if-nez v3, :cond_0

    #@6
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@9
    move-result v3

    #@a
    if-ne v3, v5, :cond_0

    #@c
    iget v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mHeight:I

    #@e
    if-ltz v3, :cond_0

    #@10
    .line 253
    iget v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mHeight:I

    #@12
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@15
    move-result v4

    #@16
    invoke-static {v3, v4}, Ljava/lang/Math;->min(II)I

    #@19
    move-result v3

    #@1a
    invoke-static {v3, v5}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1d
    move-result p2

    #@1e
    .line 256
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/widget/FrameLayout;->onMeasure(II)V

    #@21
    .line 258
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@23
    if-nez v3, :cond_2

    #@25
    .line 277
    :cond_1
    :goto_0
    return-void

    #@26
    .line 260
    :cond_2
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@29
    move-result v1

    #@2a
    .line 261
    .local v1, "mode":I
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@2c
    if-eqz v3, :cond_1

    #@2e
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@30
    invoke-virtual {v3}, Landroid/view/View;->getVisibility()I

    #@33
    move-result v3

    #@34
    const/16 v4, 0x8

    #@36
    if-eq v3, v4, :cond_1

    #@38
    const/high16 v3, 0x40000000    # 2.0f

    #@3a
    if-eq v1, v3, :cond_1

    #@3c
    .line 264
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@3e
    invoke-direct {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->isCollapsed(Landroid/view/View;)Z

    #@41
    move-result v3

    #@42
    if-nez v3, :cond_3

    #@44
    .line 265
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@46
    invoke-direct {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeightWithMargins(Landroid/view/View;)I

    #@49
    move-result v2

    #@4a
    .line 271
    .local v2, "topMarginForTabs":I
    :goto_1
    if-ne v1, v5, :cond_5

    #@4c
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@4f
    move-result v0

    #@50
    .line 273
    .local v0, "maxHeight":I
    :goto_2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredWidth()I

    #@53
    move-result v3

    #@54
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@56
    invoke-direct {p0, v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeightWithMargins(Landroid/view/View;)I

    #@59
    move-result v4

    #@5a
    add-int/2addr v4, v2

    #@5b
    invoke-static {v4, v0}, Ljava/lang/Math;->min(II)I

    #@5e
    move-result v4

    #@5f
    invoke-virtual {p0, v3, v4}, Landroid/support/v7/internal/widget/ActionBarContainer;->setMeasuredDimension(II)V

    #@62
    goto :goto_0

    #@63
    .line 266
    .end local v0    # "maxHeight":I
    .end local v2    # "topMarginForTabs":I
    :cond_3
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@65
    invoke-direct {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->isCollapsed(Landroid/view/View;)Z

    #@68
    move-result v3

    #@69
    if-nez v3, :cond_4

    #@6b
    .line 267
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mContextView:Landroid/view/View;

    #@6d
    invoke-direct {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeightWithMargins(Landroid/view/View;)I

    #@70
    move-result v2

    #@71
    .restart local v2    # "topMarginForTabs":I
    goto :goto_1

    #@72
    .line 269
    .end local v2    # "topMarginForTabs":I
    :cond_4
    const/4 v2, 0x0

    #@73
    .restart local v2    # "topMarginForTabs":I
    goto :goto_1

    #@74
    .line 271
    :cond_5
    const v0, 0x7fffffff

    #@77
    goto :goto_2
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 204
    invoke-super {p0, p1}, Landroid/widget/FrameLayout;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@3
    .line 207
    const/4 v0, 0x1

    #@4
    return v0
.end method

.method public setPrimaryBackground(Landroid/graphics/drawable/Drawable;)V
    .locals 7
    .param p1, "bg"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 88
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@4
    if-eqz v2, :cond_0

    #@6
    .line 89
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@8
    const/4 v3, 0x0

    #@9
    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@c
    .line 90
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@e
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->unscheduleDrawable(Landroid/graphics/drawable/Drawable;)V

    #@11
    .line 92
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@13
    .line 93
    if-eqz p1, :cond_1

    #@15
    .line 94
    invoke-virtual {p1, p0}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@18
    .line 95
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@1a
    if-eqz v2, :cond_1

    #@1c
    .line 96
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@20
    invoke-virtual {v3}, Landroid/view/View;->getLeft()I

    #@23
    move-result v3

    #@24
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@26
    invoke-virtual {v4}, Landroid/view/View;->getTop()I

    #@29
    move-result v4

    #@2a
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@2c
    invoke-virtual {v5}, Landroid/view/View;->getRight()I

    #@2f
    move-result v5

    #@30
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mActionBarView:Landroid/view/View;

    #@32
    invoke-virtual {v6}, Landroid/view/View;->getBottom()I

    #@35
    move-result v6

    #@36
    invoke-virtual {v2, v3, v4, v5, v6}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@39
    .line 100
    :cond_1
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@3b
    if-eqz v2, :cond_4

    #@3d
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@3f
    if-nez v2, :cond_3

    #@41
    :cond_2
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->setWillNotDraw(Z)V

    #@44
    .line 102
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->invalidate()V

    #@47
    .line 103
    return-void

    #@48
    :cond_3
    move v0, v1

    #@49
    .line 100
    goto :goto_0

    #@4a
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@4c
    if-nez v2, :cond_5

    #@4e
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@50
    if-eqz v2, :cond_2

    #@52
    :cond_5
    move v0, v1

    #@53
    goto :goto_0
.end method

.method public setSplitBackground(Landroid/graphics/drawable/Drawable;)V
    .locals 5
    .param p1, "bg"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 124
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@4
    if-eqz v2, :cond_0

    #@6
    .line 125
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@8
    const/4 v3, 0x0

    #@9
    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@c
    .line 126
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@e
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->unscheduleDrawable(Landroid/graphics/drawable/Drawable;)V

    #@11
    .line 128
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@13
    .line 129
    if-eqz p1, :cond_1

    #@15
    .line 130
    invoke-virtual {p1, p0}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@18
    .line 131
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@1a
    if-eqz v2, :cond_1

    #@1c
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    if-eqz v2, :cond_1

    #@20
    .line 132
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@22
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredWidth()I

    #@25
    move-result v3

    #@26
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getMeasuredHeight()I

    #@29
    move-result v4

    #@2a
    invoke-virtual {v2, v1, v1, v3, v4}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@2d
    .line 135
    :cond_1
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@2f
    if-eqz v2, :cond_4

    #@31
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@33
    if-nez v2, :cond_3

    #@35
    :cond_2
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->setWillNotDraw(Z)V

    #@38
    .line 137
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->invalidate()V

    #@3b
    .line 138
    return-void

    #@3c
    :cond_3
    move v0, v1

    #@3d
    .line 135
    goto :goto_0

    #@3e
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@40
    if-nez v2, :cond_5

    #@42
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@44
    if-eqz v2, :cond_2

    #@46
    :cond_5
    move v0, v1

    #@47
    goto :goto_0
.end method

.method public setStackedBackground(Landroid/graphics/drawable/Drawable;)V
    .locals 7
    .param p1, "bg"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 106
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@4
    if-eqz v2, :cond_0

    #@6
    .line 107
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@8
    const/4 v3, 0x0

    #@9
    invoke-virtual {v2, v3}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@c
    .line 108
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@e
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->unscheduleDrawable(Landroid/graphics/drawable/Drawable;)V

    #@11
    .line 110
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@13
    .line 111
    if-eqz p1, :cond_1

    #@15
    .line 112
    invoke-virtual {p1, p0}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    #@18
    .line 113
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsStacked:Z

    #@1a
    if-eqz v2, :cond_1

    #@1c
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@1e
    if-eqz v2, :cond_1

    #@20
    .line 114
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@22
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@24
    invoke-virtual {v3}, Landroid/view/View;->getLeft()I

    #@27
    move-result v3

    #@28
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@2a
    invoke-virtual {v4}, Landroid/view/View;->getTop()I

    #@2d
    move-result v4

    #@2e
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@30
    invoke-virtual {v5}, Landroid/view/View;->getRight()I

    #@33
    move-result v5

    #@34
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@36
    invoke-virtual {v6}, Landroid/view/View;->getBottom()I

    #@39
    move-result v6

    #@3a
    invoke-virtual {v2, v3, v4, v5, v6}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@3d
    .line 118
    :cond_1
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@3f
    if-eqz v2, :cond_4

    #@41
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@43
    if-nez v2, :cond_3

    #@45
    :cond_2
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->setWillNotDraw(Z)V

    #@48
    .line 120
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContainer;->invalidate()V

    #@4b
    .line 121
    return-void

    #@4c
    :cond_3
    move v0, v1

    #@4d
    .line 118
    goto :goto_0

    #@4e
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@50
    if-nez v2, :cond_5

    #@52
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@54
    if-eqz v2, :cond_2

    #@56
    :cond_5
    move v0, v1

    #@57
    goto :goto_0
.end method

.method public setTabContainer(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V
    .locals 2
    .param p1, "tabView"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@0
    .prologue
    .line 211
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 212
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@6
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->removeView(Landroid/view/View;)V

    #@9
    .line 214
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mTabContainer:Landroid/view/View;

    #@b
    .line 215
    if-eqz p1, :cond_1

    #@d
    .line 216
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActionBarContainer;->addView(Landroid/view/View;)V

    #@10
    .line 217
    invoke-virtual {p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@13
    move-result-object v0

    #@14
    .line 218
    .local v0, "lp":Landroid/view/ViewGroup$LayoutParams;
    const/4 v1, -0x1

    #@15
    iput v1, v0, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@17
    .line 219
    const/4 v1, -0x2

    #@18
    iput v1, v0, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@1a
    .line 220
    const/4 v1, 0x0

    #@1b
    invoke-virtual {p1, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setAllowCollapse(Z)V

    #@1e
    .line 222
    .end local v0    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_1
    return-void
.end method

.method public setTransitioning(Z)V
    .locals 1
    .param p1, "isTransitioning"    # Z

    #@0
    .prologue
    .line 192
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsTransitioning:Z

    #@2
    .line 193
    if-eqz p1, :cond_0

    #@4
    const/high16 v0, 0x60000

    #@6
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->setDescendantFocusability(I)V

    #@9
    .line 195
    return-void

    #@a
    .line 193
    :cond_0
    const/high16 v0, 0x40000

    #@c
    goto :goto_0
.end method

.method public setVisibility(I)V
    .locals 3
    .param p1, "visibility"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 142
    invoke-super {p0, p1}, Landroid/widget/FrameLayout;->setVisibility(I)V

    #@4
    .line 143
    if-nez p1, :cond_3

    #@6
    const/4 v0, 0x1

    #@7
    .line 144
    .local v0, "isVisible":Z
    :goto_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@9
    if-eqz v2, :cond_0

    #@b
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@d
    invoke-virtual {v2, v0, v1}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@10
    .line 145
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@12
    if-eqz v2, :cond_1

    #@14
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@16
    invoke-virtual {v2, v0, v1}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@19
    .line 146
    :cond_1
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@1b
    if-eqz v2, :cond_2

    #@1d
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@1f
    invoke-virtual {v2, v0, v1}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@22
    .line 147
    :cond_2
    return-void

    #@23
    .end local v0    # "isVisible":Z
    :cond_3
    move v0, v1

    #@24
    .line 143
    goto :goto_0
.end method

.method public startActionModeForChild(Landroid/view/View;Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "callback"    # Landroid/support/v7/view/ActionMode$Callback;

    #@0
    .prologue
    .line 231
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public startActionModeForChild(Landroid/view/View;Landroid/view/ActionMode$Callback;)Landroid/view/ActionMode;
    .locals 1
    .param p1, "originalView"    # Landroid/view/View;
    .param p2, "callback"    # Landroid/view/ActionMode$Callback;

    #@0
    .prologue
    .line 237
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method protected verifyDrawable(Landroid/graphics/drawable/Drawable;)Z
    .locals 1
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 151
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mBackground:Landroid/graphics/drawable/Drawable;

    #@2
    if-ne p1, v0, :cond_0

    #@4
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@6
    if-eqz v0, :cond_3

    #@8
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mStackedBackground:Landroid/graphics/drawable/Drawable;

    #@a
    if-ne p1, v0, :cond_1

    #@c
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsStacked:Z

    #@e
    if-nez v0, :cond_3

    #@10
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@12
    if-ne p1, v0, :cond_2

    #@14
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContainer;->mIsSplit:Z

    #@16
    if-nez v0, :cond_3

    #@18
    :cond_2
    invoke-super {p0, p1}, Landroid/widget/FrameLayout;->verifyDrawable(Landroid/graphics/drawable/Drawable;)Z

    #@1b
    move-result v0

    #@1c
    if-eqz v0, :cond_4

    #@1e
    :cond_3
    const/4 v0, 0x1

    #@1f
    :goto_0
    return v0

    #@20
    :cond_4
    const/4 v0, 0x0

    #@21
    goto :goto_0
.end method
