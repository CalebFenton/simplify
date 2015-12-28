.class final Landroid/support/v4/view/ViewPropertyAnimatorCompatKK$1;
.super Ljava/lang/Object;
.source "ViewPropertyAnimatorCompatKK.java"

# interfaces
.implements Landroid/animation/ValueAnimator$AnimatorUpdateListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/view/ViewPropertyAnimatorCompatKK;->setUpdateListener(Landroid/view/View;Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field final synthetic val$listener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

.field final synthetic val$view:Landroid/view/View;


# direct methods
.method constructor <init>(Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;Landroid/view/View;)V
    .locals 0

    #@0
    .prologue
    .line 25
    iput-object p1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatKK$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@2
    iput-object p2, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatKK$1;->val$view:Landroid/view/View;

    #@4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public onAnimationUpdate(Landroid/animation/ValueAnimator;)V
    .locals 2
    .param p1, "valueAnimator"    # Landroid/animation/ValueAnimator;

    #@0
    .prologue
    .line 28
    iget-object v0, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatKK$1;->val$listener:Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;

    #@2
    iget-object v1, p0, Landroid/support/v4/view/ViewPropertyAnimatorCompatKK$1;->val$view:Landroid/view/View;

    #@4
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorUpdateListener;->onAnimationUpdate(Landroid/view/View;)V

    #@7
    .line 29
    return-void
.end method
