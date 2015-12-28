.class public Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;
.super Ljava/lang/Object;
.source "AbsActionBarView.java"

# interfaces
.implements Landroid/support/v4/view/ViewPropertyAnimatorListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/AbsActionBarView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4
    name = "VisibilityAnimListener"
.end annotation


# instance fields
.field private mCanceled:Z

.field mFinalVisibility:I

.field final synthetic this$0:Landroid/support/v7/internal/widget/AbsActionBarView;


# direct methods
.method protected constructor <init>(Landroid/support/v7/internal/widget/AbsActionBarView;)V
    .locals 1

    #@0
    .prologue
    .line 255
    iput-object p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 256
    const/4 v0, 0x0

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mCanceled:Z

    #@8
    return-void
.end method


# virtual methods
.method public onAnimationCancel(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 285
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mCanceled:Z

    #@3
    .line 286
    return-void
.end method

.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 274
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mCanceled:Z

    #@2
    if-eqz v0, :cond_1

    #@4
    .line 281
    :cond_0
    :goto_0
    return-void

    #@5
    .line 276
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@7
    const/4 v1, 0x0

    #@8
    iput-object v1, v0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@a
    .line 277
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@c
    iget v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mFinalVisibility:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setVisibility(I)V

    #@11
    .line 278
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@13
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsActionBarView;->mSplitView:Landroid/view/ViewGroup;

    #@15
    if-eqz v0, :cond_0

    #@17
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@19
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@1b
    if-eqz v0, :cond_0

    #@1d
    .line 279
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@1f
    iget-object v0, v0, Landroid/support/v7/internal/widget/AbsActionBarView;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@21
    iget v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mFinalVisibility:I

    #@23
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuView;->setVisibility(I)V

    #@26
    goto :goto_0
.end method

.method public onAnimationStart(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 268
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/AbsActionBarView;->setVisibility(I)V

    #@6
    .line 269
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mCanceled:Z

    #@8
    .line 270
    return-void
.end method

.method public withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;
    .locals 1
    .param p1, "animation"    # Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .param p2, "visibility"    # I

    #@0
    .prologue
    .line 261
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@2
    iput-object p1, v0, Landroid/support/v7/internal/widget/AbsActionBarView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4
    .line 262
    iput p2, p0, Landroid/support/v7/internal/widget/AbsActionBarView$VisibilityAnimListener;->mFinalVisibility:I

    #@6
    .line 263
    return-object p0
.end method
