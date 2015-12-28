.class Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;
.super Ljava/lang/Object;
.source "ActionBarOverlayLayout.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V
    .locals 0

    #@0
    .prologue
    .line 121
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 123
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@3
    # invokes: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$300(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@6
    .line 124
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@8
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@a
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$500(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@d
    move-result-object v1

    #@e
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@11
    move-result-object v1

    #@12
    invoke-virtual {v1, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@15
    move-result-object v1

    #@16
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@18
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$400(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@1b
    move-result-object v2

    #@1c
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@1f
    move-result-object v1

    #@20
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$002(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@23
    .line 126
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@25
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@28
    move-result-object v0

    #@29
    if-eqz v0, :cond_0

    #@2b
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2d
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@30
    move-result-object v0

    #@31
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@34
    move-result v0

    #@35
    const/16 v1, 0x8

    #@37
    if-eq v0, v1, :cond_0

    #@39
    .line 127
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@3b
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@3d
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@40
    move-result-object v1

    #@41
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@44
    move-result-object v1

    #@45
    invoke-virtual {v1, v3}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@48
    move-result-object v1

    #@49
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$3;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@4b
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$700(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@4e
    move-result-object v2

    #@4f
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@52
    move-result-object v1

    #@53
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$202(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@56
    .line 130
    :cond_0
    return-void
.end method
