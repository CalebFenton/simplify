.class Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;
.super Ljava/lang/Object;
.source "ScrollingTabContainerView.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ScrollingTabContainerView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "TabClickListener"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V
    .locals 0

    #@0
    .prologue
    .line 566
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;

    #@0
    .prologue
    .line 566
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@3
    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 5
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 568
    move-object v3, p1

    #@1
    check-cast v3, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@3
    .line 569
    .local v3, "tabView":Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getTab()Landroid/support/v7/app/ActionBar$Tab;

    #@6
    move-result-object v4

    #@7
    invoke-virtual {v4}, Landroid/support/v7/app/ActionBar$Tab;->select()V

    #@a
    .line 570
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@c
    # getter for: Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;
    invoke-static {v4}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->access$200(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)Landroid/support/v7/widget/LinearLayoutCompat;

    #@f
    move-result-object v4

    #@10
    invoke-virtual {v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@13
    move-result v2

    #@14
    .line 571
    .local v2, "tabCount":I
    const/4 v1, 0x0

    #@15
    .local v1, "i":I
    :goto_0
    if-ge v1, v2, :cond_1

    #@17
    .line 572
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@19
    # getter for: Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;
    invoke-static {v4}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->access$200(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)Landroid/support/v7/widget/LinearLayoutCompat;

    #@1c
    move-result-object v4

    #@1d
    invoke-virtual {v4, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@20
    move-result-object v0

    #@21
    .line 573
    .local v0, "child":Landroid/view/View;
    if-ne v0, p1, :cond_0

    #@23
    const/4 v4, 0x1

    #@24
    :goto_1
    invoke-virtual {v0, v4}, Landroid/view/View;->setSelected(Z)V

    #@27
    .line 571
    add-int/lit8 v1, v1, 0x1

    #@29
    goto :goto_0

    #@2a
    .line 573
    :cond_0
    const/4 v4, 0x0

    #@2b
    goto :goto_1

    #@2c
    .line 575
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    return-void
.end method
