.class Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;
.super Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;
.source "ToolbarWidgetWrapper.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->animateToVisibility(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private mCanceled:Z

.field final synthetic this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)V
    .locals 1

    #@0
    .prologue
    .line 604
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@2
    invoke-direct {p0}, Landroid/support/v4/view/ViewPropertyAnimatorListenerAdapter;-><init>()V

    #@5
    .line 605
    const/4 v0, 0x0

    #@6
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;->mCanceled:Z

    #@8
    return-void
.end method


# virtual methods
.method public onAnimationCancel(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 615
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;->mCanceled:Z

    #@3
    .line 616
    return-void
.end method

.method public onAnimationEnd(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 608
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;->mCanceled:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 609
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@6
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$000(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/widget/Toolbar;

    #@9
    move-result-object v0

    #@a
    const/16 v1, 0x8

    #@c
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setVisibility(I)V

    #@f
    .line 611
    :cond_0
    return-void
.end method
