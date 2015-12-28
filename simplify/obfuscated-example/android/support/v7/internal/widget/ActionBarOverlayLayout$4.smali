.class Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;
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
    .line 133
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    #@0
    .prologue
    .line 135
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    # invokes: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->haltActionBarHideOffsetAnimations()V
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$300(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)V

    #@5
    .line 136
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@9
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$500(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@c
    move-result-object v1

    #@d
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@10
    move-result-object v1

    #@11
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@13
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarTop:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$500(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@16
    move-result-object v2

    #@17
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@1a
    move-result v2

    #@1b
    neg-int v2, v2

    #@1c
    int-to-float v2, v2

    #@1d
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@20
    move-result-object v1

    #@21
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@23
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mTopAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$400(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@26
    move-result-object v2

    #@27
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2a
    move-result-object v1

    #@2b
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$002(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2e
    .line 139
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@30
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@33
    move-result-object v0

    #@34
    if-eqz v0, :cond_0

    #@36
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@38
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3b
    move-result-object v0

    #@3c
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->getVisibility()I

    #@3f
    move-result v0

    #@40
    const/16 v1, 0x8

    #@42
    if-eq v0, v1, :cond_0

    #@44
    .line 140
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@46
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@48
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@4b
    move-result-object v1

    #@4c
    invoke-static {v1}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4f
    move-result-object v1

    #@50
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@52
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mActionBarBottom:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$600(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@55
    move-result-object v2

    #@56
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->getHeight()I

    #@59
    move-result v2

    #@5a
    int-to-float v2, v2

    #@5b
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->translationY(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@5e
    move-result-object v1

    #@5f
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$4;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@61
    # getter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mBottomAnimatorListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v2}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$700(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@64
    move-result-object v2

    #@65
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@68
    move-result-object v1

    #@69
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarBottomAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$202(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6c
    .line 144
    :cond_0
    return-void
.end method
