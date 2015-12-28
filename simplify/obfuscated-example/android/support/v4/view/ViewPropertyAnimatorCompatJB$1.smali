.class final Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;
.super Landroid/animation/AnimatorListenerAdapter;
.source "ViewPropertyAnimatorCompatJB.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/view/ViewPropertyAnimatorCompatJB;->setListener(Landroid/view/View;Landroid/support/v4/view/ViewPropertyAnimatorListener;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field final synthetic val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

.field final synthetic val$view:Landroid/view/View;


# direct methods
.method constructor <init>(Landroid/support/v4/view/ViewPropertyAnimatorListener;Landroid/view/View;)V
    .locals 0

    #@0
    .prologue
    .line 39
    iput-object p1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iput-object p2, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$view:Landroid/view/View;

    #@4
    invoke-direct {p0}, Landroid/animation/AnimatorListenerAdapter;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public onAnimationCancel(Landroid/animation/Animator;)V
    .locals 2
    .param p1, "animation"    # Landroid/animation/Animator;

    #@0
    .prologue
    .line 42
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationCancel(Landroid/view/View;)V

    #@7
    .line 43
    return-void
.end method

.method public onAnimationEnd(Landroid/animation/Animator;)V
    .locals 2
    .param p1, "animation"    # Landroid/animation/Animator;

    #@0
    .prologue
    .line 47
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationEnd(Landroid/view/View;)V

    #@7
    .line 48
    return-void
.end method

.method public onAnimationStart(Landroid/animation/Animator;)V
    .locals 2
    .param p1, "animation"    # Landroid/animation/Animator;

    #@0
    .prologue
    .line 52
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatJB$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationStart(Landroid/view/View;)V

    #@7
    .line 53
    return-void
.end method
