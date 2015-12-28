.class Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;
.super Ljava/lang/Object;
.source "ScrollingTabContainerView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/ScrollingTabContainerView;->animateToTab(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

.field final synthetic val$tabView:Landroid/view/View;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/view/View;)V
    .locals 0

    #@0
    .prologue
    .line 261
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    iput-object p2, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->val$tabView:Landroid/view/View;

    #@4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    #@0
    .prologue
    .line 263
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->val$tabView:Landroid/view/View;

    #@2
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@5
    move-result v1

    #@6
    iget-object v2, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@8
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getWidth()I

    #@b
    move-result v2

    #@c
    iget-object v3, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->val$tabView:Landroid/view/View;

    #@e
    invoke-virtual {v3}, Landroid/view/View;->getWidth()I

    #@11
    move-result v3

    #@12
    sub-int/2addr v2, v3

    #@13
    div-int/lit8 v2, v2, 0x2

    #@15
    sub-int v0, v1, v2

    #@17
    .line 264
    .local v0, "scrollPos":I
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@19
    const/4 v2, 0x0

    #@1a
    invoke-virtual {v1, v0, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->smoothScrollTo(II)V

    #@1d
    .line 265
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@1f
    const/4 v2, 0x0

    #@20
    iput-object v2, v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@22
    .line 266
    return-void
.end method
