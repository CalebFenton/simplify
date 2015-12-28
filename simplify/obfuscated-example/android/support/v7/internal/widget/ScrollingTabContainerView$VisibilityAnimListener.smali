.class public Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;
.super Ljava/lang/Object;
.source "ScrollingTabContainerView.java"

# interfaces
.implements Landroid/support/v4/view/ViewPropertyAnimatorListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ScrollingTabContainerView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4
    name = "VisibilityAnimListener"
.end annotation


# instance fields
.field private mCanceled:Z

.field private mFinalVisibility:I

.field final synthetic this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;


# direct methods
.method protected constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V
    .locals 1

    #@0
    .prologue
    .line 578
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 579
    const/4 v0, 0x0

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mCanceled:Z

    #@8
    return-void
.end method


# virtual methods
.method public onAnimationCancel(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 605
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mCanceled:Z

    #@3
    .line 606
    return-void
.end method

.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 597
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mCanceled:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 601
    :goto_0
    return-void

    #@5
    .line 599
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@7
    const/4 v1, 0x0

    #@8
    iput-object v1, v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@a
    .line 600
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@c
    iget v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mFinalVisibility:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@11
    goto :goto_0
.end method

.method public onAnimationStart(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 591
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setVisibility(I)V

    #@6
    .line 592
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mCanceled:Z

    #@8
    .line 593
    return-void
.end method

.method public withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;
    .locals 1
    .param p1, "animation"    # Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    .param p2, "visibility"    # I

    #@0
    .prologue
    .line 584
    iput p2, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->mFinalVisibility:I

    #@2
    .line 585
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@4
    iput-object p1, v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6
    .line 586
    return-object p0
.end method
