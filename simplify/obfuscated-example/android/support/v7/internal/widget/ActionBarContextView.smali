.class public Landroid/support/v7/internal/widget/ActionBarContextView;
.super Landroid/support/v7/internal/widget/AbsActionBarView;
.source "ActionBarContextView.java"

# interfaces
.implements Landroid/support/v4/view/ViewPropertyAnimatorListener;


# static fields
.field private static final ANIMATE_IDLE:I = 0x0

.field private static final ANIMATE_IN:I = 0x1

.field private static final ANIMATE_OUT:I = 0x2

.field private static final TAG:Ljava/lang/String; = "ActionBarContextView"


# instance fields
.field private mAnimateInOnLayout:Z

.field private mAnimationMode:I

.field private mClose:Landroid/view/View;

.field private mCloseItemLayout:I

.field private mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

.field private mCustomView:Landroid/view/View;

.field private mSplitBackground:Landroid/graphics/drawable/Drawable;

.field private mSubtitle:Ljava/lang/CharSequence;

.field private mSubtitleStyleRes:I

.field private mSubtitleView:Landroid/widget/TextView;

.field private mTitle:Ljava/lang/CharSequence;

.field private mTitleLayout:Landroid/widget/LinearLayout;

.field private mTitleOptional:Z

.field private mTitleStyleRes:I

.field private mTitleView:Landroid/widget/TextView;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 71
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 72
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 75
    sget v0, Landroid/support/v7/appcompat/R$attr;->actionModeStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 76
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 79
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/internal/widget/AbsActionBarView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 81
    sget-object v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode:[I

    #@6
    invoke-static {p1, p2, v1, p3, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@9
    move-result-object v0

    #@a
    .line 83
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_background:I

    #@c
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@f
    move-result-object v1

    #@10
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@13
    .line 85
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_titleTextStyle:I

    #@15
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@18
    move-result v1

    #@19
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleStyleRes:I

    #@1b
    .line 87
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_subtitleTextStyle:I

    #@1d
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@20
    move-result v1

    #@21
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleStyleRes:I

    #@23
    .line 90
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_height:I

    #@25
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getLayoutDimension(II)I

    #@28
    move-result v1

    #@29
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@2b
    .line 93
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_backgroundSplit:I

    #@2d
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@30
    move-result-object v1

    #@31
    iput-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@33
    .line 96
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionMode_closeItemLayout:I

    #@35
    sget v2, Landroid/support/v7/appcompat/R$layout;->abc_action_mode_close_item_material:I

    #@37
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@3a
    move-result v1

    #@3b
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCloseItemLayout:I

    #@3d
    .line 100
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@40
    .line 101
    return-void
.end method

.method private finishAnimation()V
    .locals 2

    #@0
    .prologue
    .line 274
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@2
    .line 275
    .local v0, "a":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    if-eqz v0, :cond_0

    #@4
    .line 276
    const/4 v1, 0x0

    #@5
    iput-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@7
    .line 277
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->cancel()V

    #@a
    .line 279
    :cond_0
    return-void
.end method

.method private initTitle()V
    .locals 9

    #@0
    .prologue
    const/16 v6, 0x8

    #@2
    const/4 v4, 0x1

    #@3
    const/4 v5, 0x0

    #@4
    .line 183
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@6
    if-nez v3, :cond_1

    #@8
    .line 184
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@b
    move-result-object v3

    #@c
    invoke-static {v3}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@f
    move-result-object v2

    #@10
    .line 185
    .local v2, "inflater":Landroid/view/LayoutInflater;
    sget v3, Landroid/support/v7/appcompat/R$layout;->abc_action_bar_title_item:I

    #@12
    invoke-virtual {v2, v3, p0}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@15
    .line 186
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getChildCount()I

    #@18
    move-result v3

    #@19
    add-int/lit8 v3, v3, -0x1

    #@1b
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContextView;->getChildAt(I)Landroid/view/View;

    #@1e
    move-result-object v3

    #@1f
    check-cast v3, Landroid/widget/LinearLayout;

    #@21
    iput-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@23
    .line 187
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@25
    sget v7, Landroid/support/v7/appcompat/R$id;->action_bar_title:I

    #@27
    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->findViewById(I)Landroid/view/View;

    #@2a
    move-result-object v3

    #@2b
    check-cast v3, Landroid/widget/TextView;

    #@2d
    iput-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleView:Landroid/widget/TextView;

    #@2f
    .line 188
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@31
    sget v7, Landroid/support/v7/appcompat/R$id;->action_bar_subtitle:I

    #@33
    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->findViewById(I)Landroid/view/View;

    #@36
    move-result-object v3

    #@37
    check-cast v3, Landroid/widget/TextView;

    #@39
    iput-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleView:Landroid/widget/TextView;

    #@3b
    .line 189
    iget v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleStyleRes:I

    #@3d
    if-eqz v3, :cond_0

    #@3f
    .line 190
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleView:Landroid/widget/TextView;

    #@41
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@44
    move-result-object v7

    #@45
    iget v8, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleStyleRes:I

    #@47
    invoke-virtual {v3, v7, v8}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@4a
    .line 192
    :cond_0
    iget v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleStyleRes:I

    #@4c
    if-eqz v3, :cond_1

    #@4e
    .line 193
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleView:Landroid/widget/TextView;

    #@50
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@53
    move-result-object v7

    #@54
    iget v8, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleStyleRes:I

    #@56
    invoke-virtual {v3, v7, v8}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@59
    .line 197
    .end local v2    # "inflater":Landroid/view/LayoutInflater;
    :cond_1
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleView:Landroid/widget/TextView;

    #@5b
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitle:Ljava/lang/CharSequence;

    #@5d
    invoke-virtual {v3, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@60
    .line 198
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleView:Landroid/widget/TextView;

    #@62
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitle:Ljava/lang/CharSequence;

    #@64
    invoke-virtual {v3, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@67
    .line 200
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitle:Ljava/lang/CharSequence;

    #@69
    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@6c
    move-result v3

    #@6d
    if-nez v3, :cond_5

    #@6f
    move v1, v4

    #@70
    .line 201
    .local v1, "hasTitle":Z
    :goto_0
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitle:Ljava/lang/CharSequence;

    #@72
    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@75
    move-result v3

    #@76
    if-nez v3, :cond_6

    #@78
    move v0, v4

    #@79
    .line 202
    .local v0, "hasSubtitle":Z
    :goto_1
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitleView:Landroid/widget/TextView;

    #@7b
    if-eqz v0, :cond_7

    #@7d
    move v3, v5

    #@7e
    :goto_2
    invoke-virtual {v4, v3}, Landroid/widget/TextView;->setVisibility(I)V

    #@81
    .line 203
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@83
    if-nez v1, :cond_2

    #@85
    if-eqz v0, :cond_3

    #@87
    :cond_2
    move v6, v5

    #@88
    :cond_3
    invoke-virtual {v3, v6}, Landroid/widget/LinearLayout;->setVisibility(I)V

    #@8b
    .line 204
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@8d
    invoke-virtual {v3}, Landroid/widget/LinearLayout;->getParent()Landroid/view/ViewParent;

    #@90
    move-result-object v3

    #@91
    if-nez v3, :cond_4

    #@93
    .line 205
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@95
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;)V

    #@98
    .line 207
    :cond_4
    return-void

    #@99
    .end local v0    # "hasSubtitle":Z
    .end local v1    # "hasTitle":Z
    :cond_5
    move v1, v5

    #@9a
    .line 200
    goto :goto_0

    #@9b
    .restart local v1    # "hasTitle":Z
    :cond_6
    move v0, v5

    #@9c
    .line 201
    goto :goto_1

    #@9d
    .restart local v0    # "hasSubtitle":Z
    :cond_7
    move v3, v6

    #@9e
    .line 202
    goto :goto_2
.end method

.method private makeInAnimation()Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .locals 11

    #@0
    .prologue
    const/4 v10, 0x0

    #@1
    .line 409
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@3
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@5
    invoke-virtual {v7}, Landroid/view/View;->getWidth()I

    #@8
    move-result v7

    #@9
    neg-int v9, v7

    #@a
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@c
    invoke-virtual {v7}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@f
    move-result-object v7

    #@10
    check-cast v7, Landroid/view/ViewGroup$MarginLayoutParams;

    #@12
    iget v7, v7, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@14
    sub-int v7, v9, v7

    #@16
    int-to-float v7, v7

    #@17
    invoke-static {v8, v7}, Landroid/support/v4/view/ViewCompat;->setTranslationX(Landroid/view/View;F)V

    #@1a
    .line 411
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@1c
    invoke-static {v7}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@1f
    move-result-object v7

    #@20
    invoke-virtual {v7, v10}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationX(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@23
    move-result-object v1

    #@24
    .line 412
    .local v1, "buttonAnimator":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    const-wide/16 v8, 0xc8

    #@26
    invoke-virtual {v1, v8, v9}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@29
    .line 413
    invoke-virtual {v1, p0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2c
    .line 414
    new-instance v7, Landroid/view/animation/DecelerateInterpolator;

    #@2e
    invoke-direct {v7}, Landroid/view/animation/DecelerateInterpolator;-><init>()V

    #@31
    invoke-virtual {v1, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@34
    .line 416
    new-instance v6, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@36
    invoke-direct {v6}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@39
    .line 417
    .local v6, "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    invoke-virtual {v6, v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@3c
    .line 419
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@3e
    if-eqz v7, :cond_0

    #@40
    .line 420
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@42
    invoke-virtual {v7}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@45
    move-result v3

    #@46
    .line 421
    .local v3, "count":I
    if-lez v3, :cond_0

    #@48
    .line 422
    add-int/lit8 v4, v3, -0x1

    #@4a
    .local v4, "i":I
    const/4 v5, 0x0

    #@4b
    .local v5, "j":I
    :goto_0
    if-ltz v4, :cond_0

    #@4d
    .line 423
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@4f
    invoke-virtual {v7, v4}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@52
    move-result-object v2

    #@53
    .line 424
    .local v2, "child":Landroid/view/View;
    invoke-static {v2, v10}, Landroid/support/v4/view/ViewCompat;->setScaleY(Landroid/view/View;F)V

    #@56
    .line 425
    invoke-static {v2}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@59
    move-result-object v7

    #@5a
    const/high16 v8, 0x3f800000    # 1.0f

    #@5c
    invoke-virtual {v7, v8}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->scaleY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@5f
    move-result-object v0

    #@60
    .line 426
    .local v0, "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    const-wide/16 v8, 0x12c

    #@62
    invoke-virtual {v0, v8, v9}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@65
    .line 427
    invoke-virtual {v6, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@68
    .line 422
    add-int/lit8 v4, v4, -0x1

    #@6a
    add-int/lit8 v5, v5, 0x1

    #@6c
    goto :goto_0

    #@6d
    .line 432
    .end local v0    # "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .end local v2    # "child":Landroid/view/View;
    .end local v3    # "count":I
    .end local v4    # "i":I
    .end local v5    # "j":I
    :cond_0
    return-object v6
.end method

.method private makeOutAnimation()Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .locals 9

    #@0
    .prologue
    .line 436
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@2
    invoke-static {v6}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@5
    move-result-object v7

    #@6
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@8
    invoke-virtual {v6}, Landroid/view/View;->getWidth()I

    #@b
    move-result v6

    #@c
    neg-int v8, v6

    #@d
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@f
    invoke-virtual {v6}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@12
    move-result-object v6

    #@13
    check-cast v6, Landroid/view/ViewGroup$MarginLayoutParams;

    #@15
    iget v6, v6, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@17
    sub-int v6, v8, v6

    #@19
    int-to-float v6, v6

    #@1a
    invoke-virtual {v7, v6}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationX(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@1d
    move-result-object v1

    #@1e
    .line 439
    .local v1, "buttonAnimator":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    const-wide/16 v6, 0xc8

    #@20
    invoke-virtual {v1, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@23
    .line 440
    invoke-virtual {v1, p0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@26
    .line 441
    new-instance v6, Landroid/view/animation/DecelerateInterpolator;

    #@28
    invoke-direct {v6}, Landroid/view/animation/DecelerateInterpolator;-><init>()V

    #@2b
    invoke-virtual {v1, v6}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2e
    .line 443
    new-instance v5, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@30
    invoke-direct {v5}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@33
    .line 444
    .local v5, "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    invoke-virtual {v5, v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@36
    .line 446
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@38
    if-eqz v6, :cond_0

    #@3a
    .line 447
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@3c
    invoke-virtual {v6}, Landroid/support/v7/widget/ActionMenuView;->getChildCount()I

    #@3f
    move-result v3

    #@40
    .line 448
    .local v3, "count":I
    if-lez v3, :cond_0

    #@42
    .line 449
    const/4 v4, 0x0

    #@43
    .local v4, "i":I
    :goto_0
    if-gez v4, :cond_0

    #@45
    .line 450
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@47
    invoke-virtual {v6, v4}, Landroid/support/v7/widget/ActionMenuView;->getChildAt(I)Landroid/view/View;

    #@4a
    move-result-object v2

    #@4b
    .line 451
    .local v2, "child":Landroid/view/View;
    const/high16 v6, 0x3f800000    # 1.0f

    #@4d
    invoke-static {v2, v6}, Landroid/support/v4/view/ViewCompat;->setScaleY(Landroid/view/View;F)V

    #@50
    .line 452
    invoke-static {v2}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@53
    move-result-object v6

    #@54
    const/4 v7, 0x0

    #@55
    invoke-virtual {v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->scaleY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@58
    move-result-object v0

    #@59
    .line 453
    .local v0, "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    const-wide/16 v6, 0x12c

    #@5b
    invoke-virtual {v0, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@5e
    .line 454
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@61
    .line 449
    add-int/lit8 v4, v4, 0x1

    #@63
    goto :goto_0

    #@64
    .line 459
    .end local v0    # "a":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .end local v2    # "child":Landroid/view/View;
    .end local v3    # "count":I
    .end local v4    # "i":I
    :cond_0
    return-object v5
.end method


# virtual methods
.method public bridge synthetic animateToVisibility(I)V
    .locals 0
    .param p1, "x0"    # I

    #@0
    .prologue
    .line 45
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView;->animateToVisibility(I)V

    #@3
    return-void
.end method

.method public bridge synthetic canShowOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->canShowOverflowMenu()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public closeMode()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x2

    #@1
    .line 258
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimationMode:I

    #@3
    if-ne v0, v1, :cond_0

    #@5
    .line 271
    :goto_0
    return-void

    #@6
    .line 262
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@8
    if-nez v0, :cond_1

    #@a
    .line 263
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->killMode()V

    #@d
    goto :goto_0

    #@e
    .line 267
    :cond_1
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->finishAnimation()V

    #@11
    .line 268
    iput v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimationMode:I

    #@13
    .line 269
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->makeOutAnimation()Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@16
    move-result-object v0

    #@17
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@19
    .line 270
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@1b
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@1e
    goto :goto_0
.end method

.method public bridge synthetic dismissPopupMenus()V
    .locals 0

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->dismissPopupMenus()V

    #@3
    return-void
.end method

.method protected generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 3

    #@0
    .prologue
    .line 320
    new-instance v0, Landroid/view/ViewGroup$MarginLayoutParams;

    #@2
    const/4 v1, -0x1

    #@3
    const/4 v2, -0x2

    #@4
    invoke-direct {v0, v1, v2}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(II)V

    #@7
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 325
    new-instance v0, Landroid/view/ViewGroup$MarginLayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method public bridge synthetic getAnimatedVisibility()I
    .locals 1

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getAnimatedVisibility()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public bridge synthetic getContentHeight()I
    .locals 1

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getContentHeight()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 179
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitle:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 175
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitle:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 302
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 303
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@9
    move-result v0

    #@a
    .line 305
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public initForMode(Landroid/support/v7/view/ActionMode;)V
    .locals 8
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    const/4 v7, -0x1

    #@1
    const/4 v6, 0x1

    #@2
    .line 210
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@4
    if-nez v4, :cond_2

    #@6
    .line 211
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@9
    move-result-object v4

    #@a
    invoke-static {v4}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@d
    move-result-object v1

    #@e
    .line 212
    .local v1, "inflater":Landroid/view/LayoutInflater;
    iget v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCloseItemLayout:I

    #@10
    const/4 v5, 0x0

    #@11
    invoke-virtual {v1, v4, p0, v5}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@14
    move-result-object v4

    #@15
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@17
    .line 213
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@19
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;)V

    #@1c
    .line 218
    .end local v1    # "inflater":Landroid/view/LayoutInflater;
    :cond_0
    :goto_0
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@1e
    sget v5, Landroid/support/v7/appcompat/R$id;->action_mode_close_button:I

    #@20
    invoke-virtual {v4, v5}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@23
    move-result-object v0

    #@24
    .line 219
    .local v0, "closeButton":Landroid/view/View;
    new-instance v4, Landroid/support/v7/internal/widget/ActionBarContextView$1;

    #@26
    invoke-direct {v4, p0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView$1;-><init>(Landroid/support/v7/internal/widget/ActionBarContextView;Landroid/support/v7/view/ActionMode;)V

    #@29
    invoke-virtual {v0, v4}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@2c
    .line 225
    invoke-virtual {p1}, Landroid/support/v7/view/ActionMode;->getMenu()Landroid/view/Menu;

    #@2f
    move-result-object v3

    #@30
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@32
    .line 226
    .local v3, "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@34
    if-eqz v4, :cond_1

    #@36
    .line 227
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@38
    invoke-virtual {v4}, Landroid/support/v7/widget/ActionMenuPresenter;->dismissPopupMenus()Z

    #@3b
    .line 229
    :cond_1
    new-instance v4, Landroid/support/v7/widget/ActionMenuPresenter;

    #@3d
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@40
    move-result-object v5

    #@41
    invoke-direct {v4, v5}, Landroid/support/v7/widget/ActionMenuPresenter;-><init>(Landroid/content/Context;)V

    #@44
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@46
    .line 230
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@48
    invoke-virtual {v4, v6}, Landroid/support/v7/widget/ActionMenuPresenter;->setReserveOverflow(Z)V

    #@4b
    .line 232
    new-instance v2, Landroid/view/ViewGroup$LayoutParams;

    #@4d
    const/4 v4, -0x2

    #@4e
    invoke-direct {v2, v4, v7}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@51
    .line 234
    .local v2, "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    iget-boolean v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitActionBar:Z

    #@53
    if-nez v4, :cond_3

    #@55
    .line 235
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@57
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mPopupContext:Landroid/content/Context;

    #@59
    invoke-virtual {v3, v4, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@5c
    .line 236
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@5e
    invoke-virtual {v4, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@61
    move-result-object v4

    #@62
    check-cast v4, Landroid/support/v7/widget/ActionMenuView;

    #@64
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@66
    .line 237
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@68
    const/4 v5, 0x0

    #@69
    invoke-virtual {v4, v5}, Landroid/support/v7/widget/ActionMenuView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@6c
    .line 238
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6e
    invoke-virtual {p0, v4, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@71
    .line 254
    :goto_1
    iput-boolean v6, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimateInOnLayout:Z

    #@73
    .line 255
    return-void

    #@74
    .line 214
    .end local v0    # "closeButton":Landroid/view/View;
    .end local v2    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .end local v3    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@76
    invoke-virtual {v4}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@79
    move-result-object v4

    #@7a
    if-nez v4, :cond_0

    #@7c
    .line 215
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@7e
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;)V

    #@81
    goto :goto_0

    #@82
    .line 241
    .restart local v0    # "closeButton":Landroid/view/View;
    .restart local v2    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .restart local v3    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_3
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@84
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@87
    move-result-object v5

    #@88
    invoke-virtual {v5}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@8b
    move-result-object v5

    #@8c
    invoke-virtual {v5}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@8f
    move-result-object v5

    #@90
    iget v5, v5, Landroid/util/DisplayMetrics;->widthPixels:I

    #@92
    invoke-virtual {v4, v5, v6}, Landroid/support/v7/widget/ActionMenuPresenter;->setWidthLimit(IZ)V

    #@95
    .line 244
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@97
    const v5, 0x7fffffff

    #@9a
    invoke-virtual {v4, v5}, Landroid/support/v7/widget/ActionMenuPresenter;->setItemLimit(I)V

    #@9d
    .line 246
    iput v7, v2, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@9f
    .line 247
    iget v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@a1
    iput v4, v2, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@a3
    .line 248
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@a5
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mPopupContext:Landroid/content/Context;

    #@a7
    invoke-virtual {v3, v4, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@aa
    .line 249
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@ac
    invoke-virtual {v4, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@af
    move-result-object v4

    #@b0
    check-cast v4, Landroid/support/v7/widget/ActionMenuView;

    #@b2
    iput-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@b4
    .line 250
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@b6
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@b8
    invoke-virtual {v4, v5}, Landroid/support/v7/widget/ActionMenuView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@bb
    .line 251
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitView:Landroid/view/ViewGroup;

    #@bd
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@bf
    invoke-virtual {v4, v5, v2}, Landroid/view/ViewGroup;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@c2
    goto :goto_1
.end method

.method public bridge synthetic isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->isOverflowMenuShowPending()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 310
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 311
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@9
    move-result v0

    #@a
    .line 313
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public bridge synthetic isOverflowReserved()Z
    .locals 1

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->isOverflowReserved()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isTitleOptional()Z
    .locals 1

    #@0
    .prologue
    .line 544
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleOptional:Z

    #@2
    return v0
.end method

.method public killMode()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 282
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->finishAnimation()V

    #@4
    .line 283
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->removeAllViews()V

    #@7
    .line 284
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitView:Landroid/view/ViewGroup;

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 285
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitView:Landroid/view/ViewGroup;

    #@d
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@f
    invoke-virtual {v0, v1}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@12
    .line 287
    :cond_0
    iput-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@14
    .line 288
    iput-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@16
    .line 289
    const/4 v0, 0x0

    #@17
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimateInOnLayout:Z

    #@19
    .line 290
    return-void
.end method

.method public onAnimationCancel(Landroid/view/View;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 514
    return-void
.end method

.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 506
    iget v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimationMode:I

    #@2
    const/4 v1, 0x2

    #@3
    if-ne v0, v1, :cond_0

    #@5
    .line 507
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->killMode()V

    #@8
    .line 509
    :cond_0
    const/4 v0, 0x0

    #@9
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimationMode:I

    #@b
    .line 510
    return-void
.end method

.method public onAnimationStart(Landroid/view/View;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 502
    return-void
.end method

.method public onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 105
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->onDetachedFromWindow()V

    #@3
    .line 106
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 107
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@9
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@c
    .line 108
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@e
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideSubMenus()Z

    #@11
    .line 110
    :cond_0
    return-void
.end method

.method public onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V
    .locals 2
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 523
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 524
    invoke-virtual {p1}, Landroid/view/accessibility/AccessibilityEvent;->getEventType()I

    #@9
    move-result v0

    #@a
    const/16 v1, 0x20

    #@c
    if-ne v0, v1, :cond_1

    #@e
    .line 526
    invoke-virtual {p1, p0}, Landroid/view/accessibility/AccessibilityEvent;->setSource(Landroid/view/View;)V

    #@11
    .line 527
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@14
    move-result-object v0

    #@15
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@18
    move-result-object v0

    #@19
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setClassName(Ljava/lang/CharSequence;)V

    #@1c
    .line 528
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@1f
    move-result-object v0

    #@20
    invoke-virtual {v0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@23
    move-result-object v0

    #@24
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setPackageName(Ljava/lang/CharSequence;)V

    #@27
    .line 529
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitle:Ljava/lang/CharSequence;

    #@29
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setContentDescription(Ljava/lang/CharSequence;)V

    #@2c
    .line 534
    :cond_0
    :goto_0
    return-void

    #@2d
    .line 531
    :cond_1
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView;->onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    #@30
    goto :goto_0
.end method

.method protected onLayout(ZIIII)V
    .locals 15
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 464
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@3
    move-result v5

    #@4
    .line 465
    .local v5, "isLayoutRtl":Z
    if-eqz v5, :cond_4

    #@6
    sub-int v0, p4, p2

    #@8
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingRight()I

    #@b
    move-result v1

    #@c
    sub-int v2, v0, v1

    #@e
    .line 466
    .local v2, "x":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingTop()I

    #@11
    move-result v3

    #@12
    .line 467
    .local v3, "y":I
    sub-int v0, p5, p3

    #@14
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingTop()I

    #@17
    move-result v1

    #@18
    sub-int/2addr v0, v1

    #@19
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingBottom()I

    #@1c
    move-result v1

    #@1d
    sub-int v4, v0, v1

    #@1f
    .line 469
    .local v4, "contentHeight":I
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@21
    if-eqz v0, :cond_0

    #@23
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@25
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@28
    move-result v0

    #@29
    const/16 v1, 0x8

    #@2b
    if-eq v0, v1, :cond_0

    #@2d
    .line 470
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@2f
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@32
    move-result-object v13

    #@33
    check-cast v13, Landroid/view/ViewGroup$MarginLayoutParams;

    #@35
    .line 471
    .local v13, "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    if-eqz v5, :cond_5

    #@37
    iget v14, v13, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@39
    .line 472
    .local v14, "startMargin":I
    :goto_1
    if-eqz v5, :cond_6

    #@3b
    iget v12, v13, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@3d
    .line 473
    .local v12, "endMargin":I
    :goto_2
    invoke-static {v2, v14, v5}, Landroid/support/v7/internal/widget/ActionBarContextView;->next(IIZ)I

    #@40
    move-result v2

    #@41
    .line 474
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@43
    move-object v0, p0

    #@44
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/internal/widget/ActionBarContextView;->positionChild(Landroid/view/View;IIIZ)I

    #@47
    move-result v0

    #@48
    add-int/2addr v2, v0

    #@49
    .line 475
    invoke-static {v2, v12, v5}, Landroid/support/v7/internal/widget/ActionBarContextView;->next(IIZ)I

    #@4c
    move-result v2

    #@4d
    .line 477
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimateInOnLayout:Z

    #@4f
    if-eqz v0, :cond_0

    #@51
    .line 478
    const/4 v0, 0x1

    #@52
    iput v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimationMode:I

    #@54
    .line 479
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->makeInAnimation()Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@57
    move-result-object v0

    #@58
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@5a
    .line 480
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCurrentAnimation:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@5c
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@5f
    .line 481
    const/4 v0, 0x0

    #@60
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mAnimateInOnLayout:Z

    #@62
    .line 485
    .end local v12    # "endMargin":I
    .end local v13    # "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    .end local v14    # "startMargin":I
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@64
    if-eqz v0, :cond_1

    #@66
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@68
    if-nez v0, :cond_1

    #@6a
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@6c
    invoke-virtual {v0}, Landroid/widget/LinearLayout;->getVisibility()I

    #@6f
    move-result v0

    #@70
    const/16 v1, 0x8

    #@72
    if-eq v0, v1, :cond_1

    #@74
    .line 486
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@76
    move-object v0, p0

    #@77
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/internal/widget/ActionBarContextView;->positionChild(Landroid/view/View;IIIZ)I

    #@7a
    move-result v0

    #@7b
    add-int/2addr v2, v0

    #@7c
    .line 489
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@7e
    if-eqz v0, :cond_2

    #@80
    .line 490
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@82
    move-object v0, p0

    #@83
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/internal/widget/ActionBarContextView;->positionChild(Landroid/view/View;IIIZ)I

    #@86
    move-result v0

    #@87
    add-int/2addr v2, v0

    #@88
    .line 493
    :cond_2
    if-eqz v5, :cond_7

    #@8a
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingLeft()I

    #@8d
    move-result v2

    #@8e
    .line 495
    :goto_3
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@90
    if-eqz v0, :cond_3

    #@92
    .line 496
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@94
    if-nez v5, :cond_8

    #@96
    const/4 v11, 0x1

    #@97
    :goto_4
    move-object v6, p0

    #@98
    move v8, v2

    #@99
    move v9, v3

    #@9a
    move v10, v4

    #@9b
    invoke-virtual/range {v6 .. v11}, Landroid/support/v7/internal/widget/ActionBarContextView;->positionChild(Landroid/view/View;IIIZ)I

    #@9e
    move-result v0

    #@9f
    add-int/2addr v2, v0

    #@a0
    .line 498
    :cond_3
    return-void

    #@a1
    .line 465
    .end local v2    # "x":I
    .end local v3    # "y":I
    .end local v4    # "contentHeight":I
    :cond_4
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingLeft()I

    #@a4
    move-result v2

    #@a5
    goto/16 :goto_0

    #@a7
    .line 471
    .restart local v2    # "x":I
    .restart local v3    # "y":I
    .restart local v4    # "contentHeight":I
    .restart local v13    # "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    :cond_5
    iget v14, v13, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@a9
    goto :goto_1

    #@aa
    .line 472
    .restart local v14    # "startMargin":I
    :cond_6
    iget v12, v13, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@ac
    goto :goto_2

    #@ad
    .line 493
    .end local v13    # "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    .end local v14    # "startMargin":I
    :cond_7
    sub-int v0, p4, p2

    #@af
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingRight()I

    #@b2
    move-result v1

    #@b3
    sub-int v2, v0, v1

    #@b5
    goto :goto_3

    #@b6
    .line 496
    :cond_8
    const/4 v11, 0x0

    #@b7
    goto :goto_4
.end method

.method protected onMeasure(II)V
    .locals 27
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 330
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v23

    #@4
    .line 331
    .local v23, "widthMode":I
    const/high16 v24, 0x40000000    # 2.0f

    #@6
    move/from16 v0, v23

    #@8
    move/from16 v1, v24

    #@a
    if-eq v0, v1, :cond_0

    #@c
    .line 332
    new-instance v24, Ljava/lang/IllegalStateException;

    #@e
    new-instance v25, Ljava/lang/StringBuilder;

    #@10
    invoke-direct/range {v25 .. v25}, Ljava/lang/StringBuilder;-><init>()V

    #@13
    invoke-virtual/range {p0 .. p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@16
    move-result-object v26

    #@17
    invoke-virtual/range {v26 .. v26}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@1a
    move-result-object v26

    #@1b
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1e
    move-result-object v25

    #@1f
    const-string v26, " can only be used "

    #@21
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v25

    #@25
    const-string v26, "with android:layout_width=\"match_parent\" (or fill_parent)"

    #@27
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2a
    move-result-object v25

    #@2b
    invoke-virtual/range {v25 .. v25}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2e
    move-result-object v25

    #@2f
    invoke-direct/range {v24 .. v25}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@32
    throw v24

    #@33
    .line 336
    :cond_0
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@36
    move-result v12

    #@37
    .line 337
    .local v12, "heightMode":I
    if-nez v12, :cond_1

    #@39
    .line 338
    new-instance v24, Ljava/lang/IllegalStateException;

    #@3b
    new-instance v25, Ljava/lang/StringBuilder;

    #@3d
    invoke-direct/range {v25 .. v25}, Ljava/lang/StringBuilder;-><init>()V

    #@40
    invoke-virtual/range {p0 .. p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@43
    move-result-object v26

    #@44
    invoke-virtual/range {v26 .. v26}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@47
    move-result-object v26

    #@48
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4b
    move-result-object v25

    #@4c
    const-string v26, " can only be used "

    #@4e
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@51
    move-result-object v25

    #@52
    const-string v26, "with android:layout_height=\"wrap_content\""

    #@54
    invoke-virtual/range {v25 .. v26}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@57
    move-result-object v25

    #@58
    invoke-virtual/range {v25 .. v25}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@5b
    move-result-object v25

    #@5c
    invoke-direct/range {v24 .. v25}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@5f
    throw v24

    #@60
    .line 342
    :cond_1
    invoke-static/range {p1 .. p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@63
    move-result v5

    #@64
    .line 344
    .local v5, "contentWidth":I
    move-object/from16 v0, p0

    #@66
    iget v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@68
    move/from16 v24, v0

    #@6a
    if-lez v24, :cond_8

    #@6c
    move-object/from16 v0, p0

    #@6e
    iget v15, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@70
    .line 347
    .local v15, "maxHeight":I
    :goto_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingTop()I

    #@73
    move-result v24

    #@74
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingBottom()I

    #@77
    move-result v25

    #@78
    add-int v22, v24, v25

    #@7a
    .line 348
    .local v22, "verticalPadding":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingLeft()I

    #@7d
    move-result v24

    #@7e
    sub-int v24, v5, v24

    #@80
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getPaddingRight()I

    #@83
    move-result v25

    #@84
    sub-int v3, v24, v25

    #@86
    .line 349
    .local v3, "availableWidth":I
    sub-int v11, v15, v22

    #@88
    .line 350
    .local v11, "height":I
    const/high16 v24, -0x80000000

    #@8a
    move/from16 v0, v24

    #@8c
    invoke-static {v11, v0}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@8f
    move-result v4

    #@90
    .line 352
    .local v4, "childSpecHeight":I
    move-object/from16 v0, p0

    #@92
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@94
    move-object/from16 v24, v0

    #@96
    if-eqz v24, :cond_2

    #@98
    .line 353
    move-object/from16 v0, p0

    #@9a
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@9c
    move-object/from16 v24, v0

    #@9e
    const/16 v25, 0x0

    #@a0
    move-object/from16 v0, p0

    #@a2
    move-object/from16 v1, v24

    #@a4
    move/from16 v2, v25

    #@a6
    invoke-virtual {v0, v1, v3, v4, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->measureChildView(Landroid/view/View;III)I

    #@a9
    move-result v3

    #@aa
    .line 354
    move-object/from16 v0, p0

    #@ac
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mClose:Landroid/view/View;

    #@ae
    move-object/from16 v24, v0

    #@b0
    invoke-virtual/range {v24 .. v24}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@b3
    move-result-object v14

    #@b4
    check-cast v14, Landroid/view/ViewGroup$MarginLayoutParams;

    #@b6
    .line 355
    .local v14, "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    iget v0, v14, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@b8
    move/from16 v24, v0

    #@ba
    iget v0, v14, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@bc
    move/from16 v25, v0

    #@be
    add-int v24, v24, v25

    #@c0
    sub-int v3, v3, v24

    #@c2
    .line 358
    .end local v14    # "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    :cond_2
    move-object/from16 v0, p0

    #@c4
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@c6
    move-object/from16 v24, v0

    #@c8
    if-eqz v24, :cond_3

    #@ca
    move-object/from16 v0, p0

    #@cc
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@ce
    move-object/from16 v24, v0

    #@d0
    invoke-virtual/range {v24 .. v24}, Landroid/support/v7/widget/ActionMenuView;->getParent()Landroid/view/ViewParent;

    #@d3
    move-result-object v24

    #@d4
    move-object/from16 v0, v24

    #@d6
    move-object/from16 v1, p0

    #@d8
    if-ne v0, v1, :cond_3

    #@da
    .line 359
    move-object/from16 v0, p0

    #@dc
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@de
    move-object/from16 v24, v0

    #@e0
    const/16 v25, 0x0

    #@e2
    move-object/from16 v0, p0

    #@e4
    move-object/from16 v1, v24

    #@e6
    move/from16 v2, v25

    #@e8
    invoke-virtual {v0, v1, v3, v4, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->measureChildView(Landroid/view/View;III)I

    #@eb
    move-result v3

    #@ec
    .line 363
    :cond_3
    move-object/from16 v0, p0

    #@ee
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@f0
    move-object/from16 v24, v0

    #@f2
    if-eqz v24, :cond_5

    #@f4
    move-object/from16 v0, p0

    #@f6
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@f8
    move-object/from16 v24, v0

    #@fa
    if-nez v24, :cond_5

    #@fc
    .line 364
    move-object/from16 v0, p0

    #@fe
    iget-boolean v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleOptional:Z

    #@100
    move/from16 v24, v0

    #@102
    if-eqz v24, :cond_b

    #@104
    .line 365
    const/16 v24, 0x0

    #@106
    const/16 v25, 0x0

    #@108
    invoke-static/range {v24 .. v25}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@10b
    move-result v20

    #@10c
    .line 366
    .local v20, "titleWidthSpec":I
    move-object/from16 v0, p0

    #@10e
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@110
    move-object/from16 v24, v0

    #@112
    move-object/from16 v0, v24

    #@114
    move/from16 v1, v20

    #@116
    invoke-virtual {v0, v1, v4}, Landroid/widget/LinearLayout;->measure(II)V

    #@119
    .line 367
    move-object/from16 v0, p0

    #@11b
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@11d
    move-object/from16 v24, v0

    #@11f
    invoke-virtual/range {v24 .. v24}, Landroid/widget/LinearLayout;->getMeasuredWidth()I

    #@122
    move-result v19

    #@123
    .line 368
    .local v19, "titleWidth":I
    move/from16 v0, v19

    #@125
    if-gt v0, v3, :cond_9

    #@127
    const/16 v18, 0x1

    #@129
    .line 369
    .local v18, "titleFits":Z
    :goto_1
    if-eqz v18, :cond_4

    #@12b
    .line 370
    sub-int v3, v3, v19

    #@12d
    .line 372
    :cond_4
    move-object/from16 v0, p0

    #@12f
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@131
    move-object/from16 v25, v0

    #@133
    if-eqz v18, :cond_a

    #@135
    const/16 v24, 0x0

    #@137
    :goto_2
    move-object/from16 v0, v25

    #@139
    move/from16 v1, v24

    #@13b
    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setVisibility(I)V

    #@13e
    .line 378
    .end local v18    # "titleFits":Z
    .end local v19    # "titleWidth":I
    .end local v20    # "titleWidthSpec":I
    :cond_5
    :goto_3
    move-object/from16 v0, p0

    #@140
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@142
    move-object/from16 v24, v0

    #@144
    if-eqz v24, :cond_6

    #@146
    .line 379
    move-object/from16 v0, p0

    #@148
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@14a
    move-object/from16 v24, v0

    #@14c
    invoke-virtual/range {v24 .. v24}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@14f
    move-result-object v14

    #@150
    .line 380
    .local v14, "lp":Landroid/view/ViewGroup$LayoutParams;
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@152
    move/from16 v24, v0

    #@154
    const/16 v25, -0x2

    #@156
    move/from16 v0, v24

    #@158
    move/from16 v1, v25

    #@15a
    if-eq v0, v1, :cond_c

    #@15c
    const/high16 v10, 0x40000000    # 2.0f

    #@15e
    .line 382
    .local v10, "customWidthMode":I
    :goto_4
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@160
    move/from16 v24, v0

    #@162
    if-ltz v24, :cond_d

    #@164
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@166
    move/from16 v24, v0

    #@168
    move/from16 v0, v24

    #@16a
    invoke-static {v0, v3}, Ljava/lang/Math;->min(II)I

    #@16d
    move-result v9

    #@16e
    .line 384
    .local v9, "customWidth":I
    :goto_5
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@170
    move/from16 v24, v0

    #@172
    const/16 v25, -0x2

    #@174
    move/from16 v0, v24

    #@176
    move/from16 v1, v25

    #@178
    if-eq v0, v1, :cond_e

    #@17a
    const/high16 v8, 0x40000000    # 2.0f

    #@17c
    .line 386
    .local v8, "customHeightMode":I
    :goto_6
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@17e
    move/from16 v24, v0

    #@180
    if-ltz v24, :cond_f

    #@182
    iget v0, v14, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@184
    move/from16 v24, v0

    #@186
    move/from16 v0, v24

    #@188
    invoke-static {v0, v11}, Ljava/lang/Math;->min(II)I

    #@18b
    move-result v7

    #@18c
    .line 388
    .local v7, "customHeight":I
    :goto_7
    move-object/from16 v0, p0

    #@18e
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@190
    move-object/from16 v24, v0

    #@192
    invoke-static {v9, v10}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@195
    move-result v25

    #@196
    invoke-static {v7, v8}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@199
    move-result v26

    #@19a
    invoke-virtual/range {v24 .. v26}, Landroid/view/View;->measure(II)V

    #@19d
    .line 392
    .end local v7    # "customHeight":I
    .end local v8    # "customHeightMode":I
    .end local v9    # "customWidth":I
    .end local v10    # "customWidthMode":I
    .end local v14    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_6
    move-object/from16 v0, p0

    #@19f
    iget v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@1a1
    move/from16 v24, v0

    #@1a3
    if-gtz v24, :cond_11

    #@1a5
    .line 393
    const/16 v16, 0x0

    #@1a7
    .line 394
    .local v16, "measuredHeight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getChildCount()I

    #@1aa
    move-result v6

    #@1ab
    .line 395
    .local v6, "count":I
    const/4 v13, 0x0

    #@1ac
    .local v13, "i":I
    :goto_8
    if-ge v13, v6, :cond_10

    #@1ae
    .line 396
    move-object/from16 v0, p0

    #@1b0
    invoke-virtual {v0, v13}, Landroid/support/v7/internal/widget/ActionBarContextView;->getChildAt(I)Landroid/view/View;

    #@1b3
    move-result-object v21

    #@1b4
    .line 397
    .local v21, "v":Landroid/view/View;
    invoke-virtual/range {v21 .. v21}, Landroid/view/View;->getMeasuredHeight()I

    #@1b7
    move-result v24

    #@1b8
    add-int v17, v24, v22

    #@1ba
    .line 398
    .local v17, "paddedViewHeight":I
    move/from16 v0, v17

    #@1bc
    move/from16 v1, v16

    #@1be
    if-le v0, v1, :cond_7

    #@1c0
    .line 399
    move/from16 v16, v17

    #@1c2
    .line 395
    :cond_7
    add-int/lit8 v13, v13, 0x1

    #@1c4
    goto :goto_8

    #@1c5
    .line 344
    .end local v3    # "availableWidth":I
    .end local v4    # "childSpecHeight":I
    .end local v6    # "count":I
    .end local v11    # "height":I
    .end local v13    # "i":I
    .end local v15    # "maxHeight":I
    .end local v16    # "measuredHeight":I
    .end local v17    # "paddedViewHeight":I
    .end local v21    # "v":Landroid/view/View;
    .end local v22    # "verticalPadding":I
    :cond_8
    invoke-static/range {p2 .. p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@1c8
    move-result v15

    #@1c9
    goto/16 :goto_0

    #@1cb
    .line 368
    .restart local v3    # "availableWidth":I
    .restart local v4    # "childSpecHeight":I
    .restart local v11    # "height":I
    .restart local v15    # "maxHeight":I
    .restart local v19    # "titleWidth":I
    .restart local v20    # "titleWidthSpec":I
    .restart local v22    # "verticalPadding":I
    :cond_9
    const/16 v18, 0x0

    #@1cd
    goto/16 :goto_1

    #@1cf
    .line 372
    .restart local v18    # "titleFits":Z
    :cond_a
    const/16 v24, 0x8

    #@1d1
    goto/16 :goto_2

    #@1d3
    .line 374
    .end local v18    # "titleFits":Z
    .end local v19    # "titleWidth":I
    .end local v20    # "titleWidthSpec":I
    :cond_b
    move-object/from16 v0, p0

    #@1d5
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@1d7
    move-object/from16 v24, v0

    #@1d9
    const/16 v25, 0x0

    #@1db
    move-object/from16 v0, p0

    #@1dd
    move-object/from16 v1, v24

    #@1df
    move/from16 v2, v25

    #@1e1
    invoke-virtual {v0, v1, v3, v4, v2}, Landroid/support/v7/internal/widget/ActionBarContextView;->measureChildView(Landroid/view/View;III)I

    #@1e4
    move-result v3

    #@1e5
    goto/16 :goto_3

    #@1e7
    .line 380
    .restart local v14    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_c
    const/high16 v10, -0x80000000

    #@1e9
    goto/16 :goto_4

    #@1eb
    .restart local v10    # "customWidthMode":I
    :cond_d
    move v9, v3

    #@1ec
    .line 382
    goto :goto_5

    #@1ed
    .line 384
    .restart local v9    # "customWidth":I
    :cond_e
    const/high16 v8, -0x80000000

    #@1ef
    goto :goto_6

    #@1f0
    .restart local v8    # "customHeightMode":I
    :cond_f
    move v7, v11

    #@1f1
    .line 386
    goto :goto_7

    #@1f2
    .line 402
    .end local v8    # "customHeightMode":I
    .end local v9    # "customWidth":I
    .end local v10    # "customWidthMode":I
    .end local v14    # "lp":Landroid/view/ViewGroup$LayoutParams;
    .restart local v6    # "count":I
    .restart local v13    # "i":I
    .restart local v16    # "measuredHeight":I
    :cond_10
    move-object/from16 v0, p0

    #@1f4
    move/from16 v1, v16

    #@1f6
    invoke-virtual {v0, v5, v1}, Landroid/support/v7/internal/widget/ActionBarContextView;->setMeasuredDimension(II)V

    #@1f9
    .line 406
    .end local v6    # "count":I
    .end local v13    # "i":I
    .end local v16    # "measuredHeight":I
    :goto_9
    return-void

    #@1fa
    .line 404
    :cond_11
    move-object/from16 v0, p0

    #@1fc
    invoke-virtual {v0, v5, v15}, Landroid/support/v7/internal/widget/ActionBarContextView;->setMeasuredDimension(II)V

    #@1ff
    goto :goto_9
.end method

.method public bridge synthetic postShowOverflowMenu()V
    .locals 0

    #@0
    .prologue
    .line 45
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->postShowOverflowMenu()V

    #@3
    return-void
.end method

.method public setContentHeight(I)V
    .locals 0
    .param p1, "height"    # I

    #@0
    .prologue
    .line 146
    iput p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@2
    .line 147
    return-void
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 150
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 151
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->removeView(Landroid/view/View;)V

    #@9
    .line 153
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mCustomView:Landroid/view/View;

    #@b
    .line 154
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@d
    if-eqz v0, :cond_1

    #@f
    .line 155
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@11
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->removeView(Landroid/view/View;)V

    #@14
    .line 156
    const/4 v0, 0x0

    #@15
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleLayout:Landroid/widget/LinearLayout;

    #@17
    .line 158
    :cond_1
    if-eqz p1, :cond_2

    #@19
    .line 159
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;)V

    #@1c
    .line 161
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->requestLayout()V

    #@1f
    .line 162
    return-void
.end method

.method public setSplitToolbar(Z)V
    .locals 6
    .param p1, "split"    # Z

    #@0
    .prologue
    const/4 v5, -0x1

    #@1
    .line 114
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitActionBar:Z

    #@3
    if-eq v2, p1, :cond_2

    #@5
    .line 115
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@7
    if-eqz v2, :cond_1

    #@9
    .line 117
    new-instance v0, Landroid/view/ViewGroup$LayoutParams;

    #@b
    const/4 v2, -0x2

    #@c
    invoke-direct {v0, v2, v5}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@f
    .line 119
    .local v0, "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    if-nez p1, :cond_3

    #@11
    .line 120
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@13
    invoke-virtual {v2, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@16
    move-result-object v2

    #@17
    check-cast v2, Landroid/support/v7/widget/ActionMenuView;

    #@19
    iput-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@1b
    .line 121
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@1d
    const/4 v3, 0x0

    #@1e
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/ActionMenuView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@21
    .line 122
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@23
    invoke-virtual {v2}, Landroid/support/v7/widget/ActionMenuView;->getParent()Landroid/view/ViewParent;

    #@26
    move-result-object v1

    #@27
    check-cast v1, Landroid/view/ViewGroup;

    #@29
    .line 123
    .local v1, "oldParent":Landroid/view/ViewGroup;
    if-eqz v1, :cond_0

    #@2b
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2d
    invoke-virtual {v1, v2}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@30
    .line 124
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@32
    invoke-virtual {p0, v2, v0}, Landroid/support/v7/internal/widget/ActionBarContextView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@35
    .line 141
    .end local v0    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .end local v1    # "oldParent":Landroid/view/ViewGroup;
    :cond_1
    :goto_0
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setSplitToolbar(Z)V

    #@38
    .line 143
    :cond_2
    return-void

    #@39
    .line 127
    .restart local v0    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    :cond_3
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@3b
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->getContext()Landroid/content/Context;

    #@3e
    move-result-object v3

    #@3f
    invoke-virtual {v3}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@42
    move-result-object v3

    #@43
    invoke-virtual {v3}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@46
    move-result-object v3

    #@47
    iget v3, v3, Landroid/util/DisplayMetrics;->widthPixels:I

    #@49
    const/4 v4, 0x1

    #@4a
    invoke-virtual {v2, v3, v4}, Landroid/support/v7/widget/ActionMenuPresenter;->setWidthLimit(IZ)V

    #@4d
    .line 130
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@4f
    const v3, 0x7fffffff

    #@52
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/ActionMenuPresenter;->setItemLimit(I)V

    #@55
    .line 132
    iput v5, v0, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@57
    .line 133
    iget v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mContentHeight:I

    #@59
    iput v2, v0, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@5b
    .line 134
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@5d
    invoke-virtual {v2, p0}, Landroid/support/v7/widget/ActionMenuPresenter;->getMenuView(Landroid/view/ViewGroup;)Landroid/support/v7/internal/view/menu/MenuView;

    #@60
    move-result-object v2

    #@61
    check-cast v2, Landroid/support/v7/widget/ActionMenuView;

    #@63
    iput-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@65
    .line 135
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@67
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitBackground:Landroid/graphics/drawable/Drawable;

    #@69
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/ActionMenuView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@6c
    .line 136
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6e
    invoke-virtual {v2}, Landroid/support/v7/widget/ActionMenuView;->getParent()Landroid/view/ViewParent;

    #@71
    move-result-object v1

    #@72
    check-cast v1, Landroid/view/ViewGroup;

    #@74
    .line 137
    .restart local v1    # "oldParent":Landroid/view/ViewGroup;
    if-eqz v1, :cond_4

    #@76
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@78
    invoke-virtual {v1, v2}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@7b
    .line 138
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSplitView:Landroid/view/ViewGroup;

    #@7d
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@7f
    invoke-virtual {v2, v3, v0}, Landroid/view/ViewGroup;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@82
    goto :goto_0
.end method

.method public bridge synthetic setSplitView(Landroid/view/ViewGroup;)V
    .locals 0
    .param p1, "x0"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 45
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setSplitView(Landroid/view/ViewGroup;)V

    #@3
    return-void
.end method

.method public bridge synthetic setSplitWhenNarrow(Z)V
    .locals 0
    .param p1, "x0"    # Z

    #@0
    .prologue
    .line 45
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setSplitWhenNarrow(Z)V

    #@3
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 170
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mSubtitle:Ljava/lang/CharSequence;

    #@2
    .line 171
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->initTitle()V

    #@5
    .line 172
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 165
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitle:Ljava/lang/CharSequence;

    #@2
    .line 166
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->initTitle()V

    #@5
    .line 167
    return-void
.end method

.method public setTitleOptional(Z)V
    .locals 1
    .param p1, "titleOptional"    # Z

    #@0
    .prologue
    .line 537
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleOptional:Z

    #@2
    if-eq p1, v0, :cond_0

    #@4
    .line 538
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActionBarContextView;->requestLayout()V

    #@7
    .line 540
    :cond_0
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mTitleOptional:Z

    #@9
    .line 541
    return-void
.end method

.method public shouldDelayChildPressedState()Z
    .locals 1

    #@0
    .prologue
    .line 518
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 294
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 295
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->showOverflowMenu()Z

    #@9
    move-result v0

    #@a
    .line 297
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method
