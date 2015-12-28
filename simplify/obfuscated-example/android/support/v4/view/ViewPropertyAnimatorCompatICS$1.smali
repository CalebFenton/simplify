.class final Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;
.super Landroid/animation/AnimatorListenerAdapter;
.source "ViewPropertyAnimatorCompatICS.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/view/ViewPropertyAnimatorCompatICS;->setListener(Landroid/view/View;Landroid/support/v4/view/ViewPropertyAnimatorListener;)V
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
    .line 136
    iput-object p1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iput-object p2, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$view:Landroid/view/View;

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
    .line 139
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationCancel(Landroid/view/View;)V

    #@7
    .line 140
    return-void
.end method

.method public onAnimationEnd(Landroid/animation/Animator;)V
    .locals 2
    .param p1, "animation"    # Landroid/animation/Animator;

    #@0
    .prologue
    .line 144
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationEnd(Landroid/view/View;)V

    #@7
    .line 145
    return-void
.end method

.method public onAnimationStart(Landroid/animation/Animator;)V
    .locals 2
    .param p1, "animation"    # Landroid/animation/Animator;

    #@0
    .prologue
    .line 149
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatICS$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationStart(Landroid/view/View;)V

    #@7
    .line 150
    return-void
.end method
