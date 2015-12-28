.class Landroid/support/v7/internal/app/WindowDecorActionBar$1;
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
    .line 135
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-direct {p0}, Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onAnimationEnd(Landroid/view/View;)V
    .locals 3
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/16 v2, 0x8

    #@2
    const/4 v1, 0x0

    #@3
    .line 138
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@5
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentAnimations:Z
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$000(Landroid/support/v7/internal/app/WindowDecorActionBar;)Z

    #@8
    move-result v0

    #@9
    if-eqz v0, :cond_0

    #@b
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@d
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$100(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    if-eqz v0, :cond_0

    #@13
    .line 139
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@15
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContentView:Landroid/view/View;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$100(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/view/View;

    #@18
    move-result-object v0

    #@19
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@1c
    .line 140
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@1e
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@21
    move-result-object v0

    #@22
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewCompat;->setTranslationY(Landroid/view/View;F)V

    #@25
    .line 142
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@27
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@2a
    move-result-object v0

    #@2b
    if-eqz v0, :cond_1

    #@2d
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2f
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContextDisplayMode:I
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$400(Landroid/support/v7/internal/app/WindowDecorActionBar;)I

    #@32
    move-result v0

    #@33
    const/4 v1, 0x1

    #@34
    if-ne v0, v1, :cond_1

    #@36
    .line 143
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@38
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mSplitView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@3b
    move-result-object v0

    #@3c
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@3f
    .line 145
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@41
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@44
    move-result-object v0

    #@45
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/ActionBarContainer;->setVisibility(I)V

    #@48
    .line 146
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@4a
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContainerView:Landroid/support/v7/internal/widget/ActionBarContainer;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarContainer;

    #@4d
    move-result-object v0

    #@4e
    const/4 v1, 0x0

    #@4f
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActionBarContainer;->setTransitioning(Z)V

    #@52
    .line 147
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@54
    const/4 v1, 0x0

    #@55
    # setter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mCurrentShowAnim:Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;
    invoke-static {v0, v1}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$502(Landroid/support/v7/internal/app/WindowDecorActionBar;Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;)Landroid/support/v7/internal/view/ViewPropertyAnimatorCompatSet;

    #@58
    .line 148
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@5a
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->completeDeferredDestroyActionMode()V

    #@5d
    .line 149
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@5f
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$600(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@62
    move-result-object v0

    #@63
    if-eqz v0, :cond_2

    #@65
    .line 150
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$1;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@67
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mOverlayLayout:Landroid/support/v7/internal/widget/ActionBarOverlayLayout;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$600(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ActionBarOverlayLayout;

    #@6a
    move-result-object v0

    #@6b
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->requestApplyInsets(Landroid/view/View;)V

    #@6e
    .line 152
    :cond_2
    return-void
.end method
