.class Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;
.super Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;
.source "ViewPropertyAnimatorCompatSet.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private mProxyEndCount:I

.field private mProxyStarted:Z

.field final synthetic this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 108
    iput-object p1, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@3
    invoke-direct {p0}, Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;-><init>()V

    #@6
    .line 109
    iput-boolean v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyStarted:Z

    #@8
    .line 110
    iput v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyEndCount:I

    #@a
    return-void
.end method


# virtual methods
.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 131
    iget v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyEndCount:I

    #@2
    add-int/lit8 v0, v0, 0x1

    #@4
    iput v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyEndCount:I

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@8
    # getter for: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->mAnimators:Ljava/util/ArrayList;
    invoke-static {v1}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$200(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Ljava/util/ArrayList;

    #@b
    move-result-object v1

    #@c
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@f
    move-result v1

    #@10
    if-ne v0, v1, :cond_1

    #@12
    .line 132
    iget-object v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@14
    # getter for: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->mListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$000(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@17
    move-result-object v0

    #@18
    if-eqz v0, :cond_0

    #@1a
    .line 133
    iget-object v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@1c
    # getter for: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->mListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$000(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@1f
    move-result-object v0

    #@20
    const/4 v1, 0x0

    #@21
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationEnd(Landroid/view/View;)V

    #@24
    .line 135
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->onEnd()V

    #@27
    .line 137
    :cond_1
    return-void
.end method

.method public onAnimationStart(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 114
    iget-boolean v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyStarted:Z

    #@2
    if-eqz v0, :cond_1

    #@4
    .line 121
    :cond_0
    :goto_0
    return-void

    #@5
    .line 117
    :cond_1
    const/4 v0, 0x1

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyStarted:Z

    #@8
    .line 118
    iget-object v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@a
    # getter for: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->mListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$000(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@d
    move-result-object v0

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 119
    iget-object v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@12
    # getter for: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->mListener:Landroid/support/v4/view/ViewPropertyAnimatorListener;
    invoke-static {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$000(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v4/view/ViewPropertyAnimatorListener;

    #@15
    move-result-object v0

    #@16
    const/4 v1, 0x0

    #@17
    invoke-interface {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorListener;->onAnimationStart(Landroid/view/View;)V

    #@1a
    goto :goto_0
.end method

.method onEnd()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 124
    iput v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyEndCount:I

    #@3
    .line 125
    iput-boolean v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->mProxyStarted:Z

    #@5
    .line 126
    iget-object v0, p0, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet$1;->this$0:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@7
    # invokes: Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->onAnimationsEnded()V
    invoke-static {v0}, Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;->access$100(Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)V

    #@a
    .line 127
    return-void
.end method
