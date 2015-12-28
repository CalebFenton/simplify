.class Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;
.super Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;
.source "ActionBarOverlayLayout.java"


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
    .line 92
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    invoke-direct {p0}, Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onAnimationCancel(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 101
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    const/4 v1, 0x0

    #@3
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$002(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6
    .line 102
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@8
    const/4 v1, 0x0

    #@9
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAnimatingForFling:Z
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$102(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Z)Z

    #@c
    .line 103
    return-void
.end method

.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 95
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@2
    const/4 v1, 0x0

    #@3
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mCurrentActionBarTopAnimator:Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$002(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Landroid/support/v4/view/ViewPropertyAnimatorCompat;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@6
    .line 96
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarOverlayLayout$1;->this$0:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@8
    const/4 v1, 0x0

    #@9
    # setter for: Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->mAnimatingForFling:Z
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/ActionBarOverlayLayout;->access$102(Landroid/support/v7/internal/widget/ActionBarOverlayLayout;Z)Z

    #@c
    .line 97
    return-void
.end method
