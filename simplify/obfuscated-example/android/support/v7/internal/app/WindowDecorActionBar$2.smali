.class Landroid/support/v7/internal/app/WindowDecorActionBar$2;
.super Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;
.source "WindowDecorActionBar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/WindowDecorActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V
    .locals 0

    #@0
    .prologue
    .line 155
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$2;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-direct {p0}, Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 158
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$2;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    const/4 v1, 0x0

    #@3
    # setter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    invoke-static {v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$502(Landroid/support/v7/internal/app/WindowDecorActionBar;Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@6
    .line 159
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$2;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@b
    move-result-object v0

    #@c
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActionBarContainer;->requestLayout()V

    #@f
    .line 160
    return-void
.end method
