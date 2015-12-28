.class abstract Landroid/support/v7/internal/widget/AbsActionBarView;
.super Landroid/view/ViewGroup;
.source "AbsActionBarView.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;
    }
.end annotation


# static fields
.field private static final FADE_DURATION:I = 0xc8

.field private static final sAlphaInterpolator:Landroid/view/animation/Interpolator;


# instance fields
.field protected mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

.field protected mContentHeight:I

.field protected mMenuView:Landroid/support/v7/widget/ActionMenuView;

.field protected final mPopupContext:Landroid/content/Context;

.field protected mSplitActionBar:Z

.field protected mSplitView:Landroid/view/ViewGroup;

.field protected mSplitWhenNarrow:Z

.field protected final mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

.field protected mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 38
    new-instance v0, Landroid/view/animation/DecelerateInterpolator;

    #@2
    invoke-direct {v0}, Landroid/view/animation/DecelerateInterpolator;-><init>()V

    #@5
    sput-object v0, Landroid/support/v7/internal/widget/AbsActionBarView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@7
    return-void
.end method

.method constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 57
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/AbsActionBarView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 58
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 61
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/AbsActionBarView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 62
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    .line 65
    invoke-direct {p0, p1, p2, p3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@3
    .line 42
    new-instance v1, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@5
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;-><init>(Landroid/support/v7/internal/widget/AbsActionBarView;)V

    #@8
    iput-object v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@a
    .line 67
    new-instance v0, Landroid/util/TypedValue;

    #@c
    invoke-direct {v0}, Landroid/util/TypedValue;-><init>()V

    #@f
    .line 68
    .local v0, "tv":Landroid/util/TypedValue;
    invoke-virtual {p1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@12
    move-result-object v1

    #@13
    sget v2, Landroid/support/v7/appcompat/R$attr;->actionBarPopupTheme:I

    #@15
    const/4 v3, 0x1

    #@16
    invoke-virtual {v1, v2, v0, v3}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@19
    move-result v1

    #@1a
    if-eqz v1, :cond_0

    #@1c
    iget v1, v0, Landroid/util/TypedValue;->resourceId:I

    #@1e
    if-eqz v1, :cond_0

    #@20
    .line 70
    new-instance v1, Landroid/view/ContextThemeWrapper;

    #@22
    iget v2, v0, Landroid/util/TypedValue;->resourceId:I

    #@24
    invoke-direct {v1, p1, v2}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@27
    iput-object v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mPopupContext:Landroid/content/Context;

    #@29
    .line 74
    :goto_0
    return-void

    #@2a
    .line 72
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mPopupContext:Landroid/content/Context;

    #@2c
    goto :goto_0
.end method

.method protected static next(IIZ)I
    .locals 1
    .param p0, "x"    # I
    .param p1, "val"    # I
    .param p2, "isRtl"    # Z

    #@0
    .prologue
    .line 238
    if-eqz p2, :cond_0

    #@2
    sub-int v0, p0, p1

    #@4
    :goto_0
    return v0

    #@5
    :cond_0
    add-int v0, p0, p1

    #@7
    goto :goto_0
.end method


# virtual methods
.method public animateToVisibility(I)V
    .locals 8
    .param p1, "visibility"    # I

    #@0
    .prologue
    const/high16 v5, 0x3f800000    # 1.0f

    #@2
    const-wide/16 v6, 0xc8

    #@4
    const/4 v4, 0x0

    #@5
    .line 134
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@7
    if-eqz v3, :cond_0

    #@9
    .line 135
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@b
    invoke-virtual {v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->cancel()V

    #@e
    .line 137
    :cond_0
    if-nez p1, :cond_3

    #@10
    .line 138
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getVisibility()I

    #@13
    move-result v3

    #@14
    if-eqz v3, :cond_1

    #@16
    .line 139
    invoke-static {p0, v4}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@19
    .line 140
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitView:Landroid/view/ViewGroup;

    #@1b
    if-eqz v3, :cond_1

    #@1d
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@1f
    if-eqz v3, :cond_1

    #@21
    .line 141
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@23
    invoke-static {v3, v4}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@26
    .line 144
    :cond_1
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@29
    move-result-object v3

    #@2a
    invoke-virtual {v3, v5}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2d
    move-result-object v0

    #@2e
    .line 145
    .local v0, "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v0, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@31
    .line 146
    sget-object v3, Landroid/support/v7/internal/widget/AbsActionBarView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@33
    invoke-virtual {v0, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@36
    .line 147
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitView:Landroid/view/ViewGroup;

    #@38
    if-eqz v3, :cond_2

    #@3a
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@3c
    if-eqz v3, :cond_2

    #@3e
    .line 148
    new-instance v1, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@40
    invoke-direct {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@43
    .line 149
    .local v1, "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@45
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@48
    move-result-object v3

    #@49
    invoke-virtual {v3, v5}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4c
    move-result-object v2

    #@4d
    .line 150
    .local v2, "splitAnim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v2, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@50
    .line 151
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@52
    invoke-virtual {v3, v0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@55
    move-result-object v3

    #@56
    invoke-virtual {v1, v3}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@59
    .line 152
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@5c
    move-result-object v3

    #@5d
    invoke-virtual {v3, v2}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@60
    .line 153
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@63
    .line 174
    .end local v1    # "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .end local v2    # "splitAnim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    :goto_0
    return-void

    #@64
    .line 155
    :cond_2
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@66
    invoke-virtual {v3, v0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@69
    move-result-object v3

    #@6a
    invoke-virtual {v0, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6d
    .line 156
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->start()V

    #@70
    goto :goto_0

    #@71
    .line 159
    .end local v0    # "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    :cond_3
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@74
    move-result-object v3

    #@75
    invoke-virtual {v3, v4}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@78
    move-result-object v0

    #@79
    .line 160
    .restart local v0    # "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v0, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@7c
    .line 161
    sget-object v3, Landroid/support/v7/internal/widget/AbsActionBarView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@7e
    invoke-virtual {v0, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@81
    .line 162
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitView:Landroid/view/ViewGroup;

    #@83
    if-eqz v3, :cond_4

    #@85
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@87
    if-eqz v3, :cond_4

    #@89
    .line 163
    new-instance v1, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@8b
    invoke-direct {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;-><init>()V

    #@8e
    .line 164
    .restart local v1    # "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@90
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@93
    move-result-object v3

    #@94
    invoke-virtual {v3, v4}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@97
    move-result-object v2

    #@98
    .line 165
    .restart local v2    # "splitAnim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v2, v6, v7}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@9b
    .line 166
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@9d
    invoke-virtual {v3, v0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@a0
    move-result-object v3

    #@a1
    invoke-virtual {v1, v3}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a4
    .line 167
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a7
    move-result-object v3

    #@a8
    invoke-virtual {v3, v2}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->play(Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@ab
    .line 168
    invoke-virtual {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->start()V

    #@ae
    goto :goto_0

    #@af
    .line 170
    .end local v1    # "set":Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    .end local v2    # "splitAnim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    :cond_4
    iget-object v3, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@b1
    invoke-virtual {v3, v0, p1}, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@b4
    move-result-object v3

    #@b5
    invoke-virtual {v0, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@b8
    .line 171
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->start()V

    #@bb
    goto :goto_0
.end method

.method public canShowOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 217
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->isOverflowReserved()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getVisibility()I

    #@9
    move-result v0

    #@a
    if-nez v0, :cond_0

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

.method public dismissPopupMenus()V
    .locals 1

    #@0
    .prologue
    .line 221
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 222
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->dismissPopupMenus()Z

    #@9
    .line 224
    :cond_0
    return-void
.end method

.method public getAnimatedVisibility()I
    .locals 1

    #@0
    .prologue
    .line 127
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 128
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisAnimListener:Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;

    #@6
    iget v0, v0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mFinalVisibility:I

    #@8
    .line 130
    :goto_0
    return v0

    #@9
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getVisibility()I

    #@c
    move-result v0

    #@d
    goto :goto_0
.end method

.method public getContentHeight()I
    .locals 1

    #@0
    .prologue
    .line 116
    iget v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mContentHeight:I

    #@2
    return v0
.end method

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 192
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 193
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->hideOverflowMenu()Z

    #@9
    move-result v0

    #@a
    .line 195
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 206
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 207
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowPending()Z

    #@9
    move-result v0

    #@a
    .line 209
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 199
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 200
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowMenuShowing()Z

    #@9
    move-result v0

    #@a
    .line 202
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public isOverflowReserved()Z
    .locals 1

    #@0
    .prologue
    .line 213
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->isOverflowReserved()Z

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

.method protected measureChildView(Landroid/view/View;III)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "availableWidth"    # I
    .param p3, "childSpecHeight"    # I
    .param p4, "spacing"    # I

    #@0
    .prologue
    .line 228
    const/high16 v0, -0x80000000

    #@2
    invoke-static {p2, v0}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@5
    move-result v0

    #@6
    invoke-virtual {p1, v0, p3}, Landroid/view/View;->measure(II)V

    #@9
    .line 231
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@c
    move-result v0

    #@d
    sub-int/2addr p2, v0

    #@e
    .line 232
    sub-int/2addr p2, p4

    #@f
    .line 234
    const/4 v0, 0x0

    #@10
    invoke-static {v0, p2}, Ljava/lang/Math;->max(II)I

    #@13
    move-result v0

    #@14
    return v0
.end method

.method protected onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 6
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 78
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3
    const/16 v2, 0x8

    #@5
    if-lt v1, v2, :cond_0

    #@7
    .line 79
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@a
    .line 84
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->getContext()Landroid/content/Context;

    #@d
    move-result-object v1

    #@e
    const/4 v2, 0x0

    #@f
    sget-object v3, Landroid/support/v7/appcompat/R$styleable;->ActionBar:[I

    #@11
    sget v4, Landroid/support/v7/appcompat/R$attr;->actionBarStyle:I

    #@13
    invoke-virtual {v1, v2, v3, v4, v5}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@16
    move-result-object v0

    #@17
    .line 86
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionBar_height:I

    #@19
    invoke-virtual {v0, v1, v5}, Landroid/content/res/TypedArray;->getLayoutDimension(II)I

    #@1c
    move-result v1

    #@1d
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setContentHeight(I)V

    #@20
    .line 87
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@23
    .line 89
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@25
    if-eqz v1, :cond_1

    #@27
    .line 90
    iget-object v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@29
    invoke-virtual {v1, p1}, Landroid/support/v7/widget/ActionMenuPresenter;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@2c
    .line 92
    :cond_1
    return-void
.end method

.method protected positionChild(Landroid/view/View;IIIZ)I
    .locals 5
    .param p1, "child"    # Landroid/view/View;
    .param p2, "x"    # I
    .param p3, "y"    # I
    .param p4, "contentHeight"    # I
    .param p5, "reverse"    # Z

    #@0
    .prologue
    .line 242
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@3
    move-result v2

    #@4
    .line 243
    .local v2, "childWidth":I
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@7
    move-result v0

    #@8
    .line 244
    .local v0, "childHeight":I
    sub-int v3, p4, v0

    #@a
    div-int/lit8 v3, v3, 0x2

    #@c
    add-int v1, p3, v3

    #@e
    .line 246
    .local v1, "childTop":I
    if-eqz p5, :cond_1

    #@10
    .line 247
    sub-int v3, p2, v2

    #@12
    add-int v4, v1, v0

    #@14
    invoke-virtual {p1, v3, v1, p2, v4}, Landroid/view/View;->layout(IIII)V

    #@17
    .line 252
    :goto_0
    if-eqz p5, :cond_0

    #@19
    neg-int v2, v2

    #@1a
    .end local v2    # "childWidth":I
    :cond_0
    return v2

    #@1b
    .line 249
    .restart local v2    # "childWidth":I
    :cond_1
    add-int v3, p2, v2

    #@1d
    add-int v4, v1, v0

    #@1f
    invoke-virtual {p1, p2, v1, v3, v4}, Landroid/view/View;->layout(IIII)V

    #@22
    goto :goto_0
.end method

.method public postShowOverflowMenu()V
    .locals 1

    #@0
    .prologue
    .line 184
    new-instance v0, Landroid/support/v7/internal/widget/AbsActionBarView$1;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/AbsActionBarView$1;-><init>(Landroid/support/v7/internal/widget/AbsActionBarView;)V

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AbsActionBarView;->post(Ljava/lang/Runnable;)Z

    #@8
    .line 189
    return-void
.end method

.method public setContentHeight(I)V
    .locals 0
    .param p1, "height"    # I

    #@0
    .prologue
    .line 111
    iput p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mContentHeight:I

    #@2
    .line 112
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AbsActionBarView;->requestLayout()V

    #@5
    .line 113
    return-void
.end method

.method public setSplitToolbar(Z)V
    .locals 0
    .param p1, "split"    # Z

    #@0
    .prologue
    .line 99
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitActionBar:Z

    #@2
    .line 100
    return-void
.end method

.method public setSplitView(Landroid/view/ViewGroup;)V
    .locals 0
    .param p1, "splitView"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 120
    iput-object p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitView:Landroid/view/ViewGroup;

    #@2
    .line 121
    return-void
.end method

.method public setSplitWhenNarrow(Z)V
    .locals 0
    .param p1, "splitWhenNarrow"    # Z

    #@0
    .prologue
    .line 107
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitWhenNarrow:Z

    #@2
    .line 108
    return-void
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 177
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 178
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuPresenter;->showOverflowMenu()Z

    #@9
    move-result v0

    #@a
    .line 180
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method
